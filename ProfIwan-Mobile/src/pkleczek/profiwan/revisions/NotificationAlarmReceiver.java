package pkleczek.profiwan.revisions;

import pkleczek.profiwan.utils.Notifications;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationAlarmReceiver extends BroadcastReceiver {
	 @Override
	 public void onReceive(Context context, Intent intent) {
		 Log.d("XXX", "received something...");
		 
		 // TODO:sprawdź, czy nadeszła pora na powtórki i ewentualnie ustaw powiadomienie
		 Notifications.notify(context);
	 }
}
