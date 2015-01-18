package pkleczek.profiwan.revisions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

import pkleczek.profiwan.MainActivity;
import pkleczek.profiwan.ProfIwanApplication;
import pkleczek.profiwan.ProfIwanApplication.RunningMode;
import pkleczek.profiwan.model.RevisionsSession;
import pkleczek.profiwan.model.Timepoint;
import pkleczek.profiwan.model.Timepoint.TimepointType;
import pkleczek.profiwan.utils.Clustering;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.Foreground;
import pkleczek.profiwan.utils.Notifications;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationAlarmReceiver extends BroadcastReceiver {

	
	// Time of cluster for which the notification should be issued. 
	static DateTime lastNotificationTime = null;
	final int minutesBetweenNotifications = ProfIwanApplication.runningMode != RunningMode.NORMAL ? 5 : 15;
	final DateTime timespanBetweenNotifications = (new DateTime(0)).withMinuteOfHour(minutesBetweenNotifications);
	final int maxNotificationsPerSession = 4;
	static int notificationCounter = 0;
	
	 @Override
	 public void onReceive(Context context, Intent intent) {
		 Log.d("XXX", "received something...");
		 
		 DatabaseHelper dbHelper = DatabaseHelperImpl.getInstance(context);
		 
		 RevisionsSession revisionsSession = new RevisionsSession(dbHelper);
		 
		 if (ProfIwanApplication.runningMode != RunningMode.NORMAL) {
			 // Just to set revision times...
			 List<Timepoint> list = dbHelper.getAllTimepoints();
			 isTimeForRevisions(list);
		 }
		 
		 boolean hasRevisions = revisionsSession.hasRevisions();
		 boolean isInForeground = Foreground.get().isForeground();
		 if (!hasRevisions || isInForeground) {
			 return;
		 }
		 
		 List<Timepoint> list = dbHelper.getAllTimepoints();
		 DateTime t = isTimeForRevisions(list);
		 
		 if (t == null) {
			 return;
		 }
		 
		 if (!t.isEqual(lastNotificationTime)) {
			 lastNotificationTime = t;
			 notificationCounter = 0;
		 }

		 boolean allNotificationsShown = notificationCounter >= maxNotificationsPerSession;
		 if (allNotificationsShown) {
			 return;
		 }
		 
		 DateTime nextNotificationTime = lastNotificationTime.plus(notificationCounter * timespanBetweenNotifications.getMillis());
		 if (nextNotificationTime.isBeforeNow()) {
			 notificationCounter++;
			 Notifications.notify(context);
			 Log.d("XXX", "notification #" + notificationCounter);
		 }
	 }
	 
		/**
		 * 
		 * @param timepoints
		 * @return DateTime of the current revision session (centroid); null = no session
		 */
		private DateTime isTimeForRevisions(List<Timepoint> timepoints) {
			// Get list of all Timepoints that refer to today's day of the week.
			int today = DateTime.now().getDayOfWeek();
			 List<DateTime> startsForCurrentDayOfWeek = new ArrayList<DateTime>();
			 for (Timepoint t : timepoints) {
				 if (t.getType() == TimepointType.REVISION_STARTED
						 && t.getCreatedAt().getDayOfWeek() == today) {
					 startsForCurrentDayOfWeek.add(t.getCreatedAt());
				 }
			 }
			 
			 // A minimum break between two different revision sessions reminders
			 // is approximately 2 hours.
			 final int kMax = 6;

			 List<List<DateTime>> clusterings = new ArrayList<List<DateTime>>();
			 for (int k = 1; k <= kMax; k++) {
				 clusterings.add(Clustering.clusterize(k, startsForCurrentDayOfWeek));
			 }
			 
			 long timespanBetweenRevisions =
						 ProfIwanApplication.runningMode == RunningMode.NORMAL ?
								 (new DateTime(0)).withHourOfDay(2).getMillis() : 
						 (new DateTime(0)).withMinuteOfHour(2).getMillis();
			 List<DateTime> clustering = Clustering.chooseClustering(clusterings, timespanBetweenRevisions);
			 
			 MainActivity.setRevisionTimes(clustering);
			 
			 Collections.sort(clustering);
			 Collections.reverse(clustering);
			 for (DateTime t : clustering) {
				 if (t.isBeforeNow()) {
					 return t;
				 }
			 }
			 
			 return null;
		}
}
