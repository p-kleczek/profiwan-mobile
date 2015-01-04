package pkleczek.profiwan.utils;

import pkleczek.profiwan.MainActivity;
import pkleczek.profiwan.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notifications {

	public static void notify(Activity activity) {
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(activity)
			    .setSmallIcon(R.drawable.speech_icon)
			    .setContentTitle(activity.getString(R.string.revisions_notification_title))
			    .setContentText(activity.getString(R.string.revision_notification_content));
		
		Intent notifyIntent = new Intent(activity, MainActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
    		activity,
		    0,
		    notifyIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		
		mBuilder.setContentIntent(resultPendingIntent);
		
		int mNotificationId = 001;
		NotificationManager mNotifyMgr = 
		        (NotificationManager) activity.getSystemService(Activity.NOTIFICATION_SERVICE);
		
		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		// Hide notification after it's clicked by a user.
		notification.flags |= Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

		
		mNotifyMgr.notify(mNotificationId, notification);		
	}
}
