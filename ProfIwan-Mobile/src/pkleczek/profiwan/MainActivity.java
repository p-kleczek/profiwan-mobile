package pkleczek.profiwan;

import pkleczek.profiwan.debug.Debug;
import pkleczek.profiwan.revisions.RevisionsActivity;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	DatabaseHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.setProperty("org.joda.time.DateTimeZone.Provider", "org.joda.time.tz.UTCProvider");
		
		setContentView(R.layout.activity_main);
		
		dbHelper = DatabaseHelperImpl.getInstance(this);
		
		// debug
		((DatabaseHelperImpl) dbHelper).clearDB();
		Debug.populateDB(dbHelper);
		startRevisions(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void startRevisions(View view) {
		Intent intent = new Intent(this, RevisionsActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}
