package pkleczek.profiwan;

import pkleczek.profiwan.debug.Debug;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.DatabaseHelperImplMock;
import android.app.Application;

public class ProfIwanApplication extends Application {

	private static ProfIwanApplication instance;

	public static ProfIwanApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		
		// XXX: debug
		debug();
	}
	
	private void debug() {
		DatabaseHelper dbHelper = DatabaseHelperImpl.getInstance(this);
		((DatabaseHelperImpl) dbHelper).clearDB();
		Debug.populateDB(dbHelper);
	}
}
