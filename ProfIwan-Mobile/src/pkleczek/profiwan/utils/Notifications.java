package pkleczek.profiwan.utils;

import java.util.Calendar;

import pkleczek.profiwan.MainActivity;
import pkleczek.profiwan.R;
import pkleczek.profiwan.revisions.NotificationAlarmReceiver;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notifications {

	public static void notify(Context context) {
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(context)
			    .setSmallIcon(R.drawable.speech_icon)
			    .setContentTitle(context.getString(R.string.revisions_notification_title))
			    .setContentText(context.getString(R.string.revision_notification_content));
		
		Intent notifyIntent = new Intent(context, MainActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
    		context,
		    0,
		    notifyIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		
		mBuilder.setContentIntent(resultPendingIntent);
		
		int mNotificationId = 001;
		NotificationManager mNotifyMgr = 
		        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		// Hide notification after it's clicked by a user.
		notification.flags |= Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

		
		mNotifyMgr.notify(mNotificationId, notification);		
	}
	
	public static void setAlarm(Context context) {
		AlarmManager alarmMgr;
		PendingIntent alarmIntent;
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, NotificationAlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		// Set the alarm to start (almost) now and repeat every 5 minutes.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis() + 5000);
		final long MILLIS_PER_MINUTE = 1000 * 60;
		long interval = MILLIS_PER_MINUTE * 5;
		interval = 10 * 1000;
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				interval, alarmIntent);		
	}
}
