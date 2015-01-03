package pkleczek.profiwan;

import pkleczek.profiwan.debug.Debug;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ProfIwanApplication extends Application {

	public static enum RunningMode {
		NORMAL, DEBUG, TEST;
	}

	public static final String TAG = "ProfIwan";

	private static ProfIwanApplication instance;

	public static final RunningMode runningMode = RunningMode.TEST;
	public static final boolean isUpdate = true;

	public static ProfIwanApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		// XXX: debug
		if (runningMode == RunningMode.DEBUG || runningMode == RunningMode.TEST) {
			debug();
		}

		if (isUpdate) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = prefs.edit();
			editor.clear();

			editor.putString(getString(R.string.pref_key_known_language),
					getString(R.string.known_language_default));
			editor.putString(getString(R.string.pref_key_revised_language),
					getString(R.string.revised_language_default));

			editor.commit();
		}
	}

	private void debug() {
		DatabaseHelper dbHelper = DatabaseHelperImpl.getInstance(this);
		((DatabaseHelperImpl) dbHelper).clearDB();
		Debug.populateDB(dbHelper);
	}
}
