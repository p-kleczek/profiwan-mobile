package pkleczek.profiwan;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import pkleczek.profiwan.ProfIwanApplication.RunningMode;
import pkleczek.profiwan.dictionary.DictionaryActivity;
import pkleczek.profiwan.model.RevisionsSession;
import pkleczek.profiwan.prefs.Settings;
import pkleczek.profiwan.revisions.RevisionsActivity;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.Notifications;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	DatabaseHelper dbHelper;
	
	private static TextView tvRevisionTimes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.setProperty("org.joda.time.DateTimeZone.Provider",
				"org.joda.time.tz.UTCProvider");

		setContentView(R.layout.activity_main);
		
		dbHelper = DatabaseHelperImpl.getInstance(this);

		// debug
		if (ProfIwanApplication.runningMode == RunningMode.DEBUG) {
			Intent intent = new Intent(this, DictionaryActivity.class);
			startActivity(intent);
		}
		
//		Logging.logEvent(DatabaseHelperImpl.getInstance(this), TimepointType.SESSION_STARTED);
		Notifications.setAlarm(this.getApplicationContext());
		
		tvRevisionTimes = (TextView) findViewById(R.id.main_tv_revision_times);
	}
	
	public static void setRevisionTimes(List<DateTime> times) {
		if (tvRevisionTimes == null) {
			return;
		}

		String text = "Revision times:\n";
		String indent = "   ";
		DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
		for (DateTime t : times) {
			text += indent + "- " + dtf.print(t) + "\n";
		}
		tvRevisionTimes.setText(text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_app_settings:
			Intent intent = new Intent(this, Settings.class);
			startActivity(intent);
			break;
		default:
			break;
		}

		return true;
	}

	public void startRevisions(View view) {
		Intent intent = new Intent(this, RevisionsActivity.class);
		startActivity(intent);
	}

	public void startDictionary(View view) {
		Intent intent = new Intent(this, DictionaryActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

//		Logging.logEvent(DatabaseHelperImpl.getInstance(this), TimepointType.SESSION_FINISHED);
	}

	@Override
	protected void onStart() {
		super.onStart();

		RevisionsSession revisionsSession = new RevisionsSession(dbHelper);
		Button btn = (Button) findViewById(R.id.main_btn_revisions);
		btn.setEnabled(revisionsSession.hasRevisions());
	}
}
