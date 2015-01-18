package pkleczek.profiwan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pkleczek.profiwan.debug.Debug;
import pkleczek.profiwan.model.Timepoint;
import pkleczek.profiwan.model.Timepoint.TimepointType;
import pkleczek.profiwan.revisions.RevisionsActivity;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.Foreground;
import pkleczek.profiwan.utils.Logging;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

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
	
	public static boolean isDuringRevisionSession = false;
	private List<Timepoint> activityLifecycleChanges = new ArrayList<Timepoint>();

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		
		Foreground.init(this);

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
		
		this.registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
	}
	
	private void debug() {
		DatabaseHelper dbHelper = DatabaseHelperImpl.getInstance(this);
		((DatabaseHelperImpl) dbHelper).clearDB();
		Debug.populateDB(dbHelper);
		
		List<Timepoint> ts = ((DatabaseHelperImpl) dbHelper).getAllTimepoints();
		for (Timepoint x : ts) {
			Log.d("XXX", x.toString());
		}
	}
	
	class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
		@Override
		public void onActivityCreated(Activity arg0, Bundle arg1) {
		}

		@Override
		public void onActivityDestroyed(Activity activity) {
			
			if (activity instanceof RevisionsActivity) {
				boolean isFinished = true;
				for (Timepoint t : activityLifecycleChanges) {
					if (t.getType() == TimepointType.REVISION_STARTED && isFinished) {
						DatabaseHelperImpl.getInstance(activity).createTimepoint(t);
						isFinished = false;
					}
					if (t.getType() == TimepointType.REVISION_FINISHED) {
						DatabaseHelperImpl.getInstance(activity).createTimepoint(t);
						isFinished = true;
					}
				}
				
				Timepoint previous = null;
				for (Iterator<Timepoint> iter = activityLifecycleChanges.iterator(); iter.hasNext(); ) {
					Timepoint t = iter.next();

					if (t.getType() == TimepointType.REVISION_FINISHED) {
						if (previous.getType() == TimepointType.REVISION_INTERRUPTED) {
							iter.remove();
						}
					}
					
					previous = t;
				}
				
				activityLifecycleChanges.clear();
				for (Timepoint t : DatabaseHelperImpl.getInstance(activity).getAllTimepoints()) {
					Log.d("XXX", t.toString());
				}
				
			} else {
			}
		}

		@Override
		public void onActivityPaused(Activity arg0) {
		}

		@Override
		public void onActivityResumed(Activity arg0) {
		}

		@Override
		public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {
		}

		@Override
		public void onActivityStarted(Activity activity) {
			if (activity instanceof RevisionsActivity) {
				activityLifecycleChanges.add(Timepoint.create(TimepointType.REVISION_STARTED));
			} else {
				if (!activityLifecycleChanges.isEmpty() && !(activity instanceof MainActivity)) {
					activityLifecycleChanges.add(Timepoint.create(TimepointType.REVISION_INTERRUPTED));
				}
			}

			Log.d("XXX", activity.getClass().getName() + " started");
		}

		@Override
		public void onActivityStopped(Activity activity) {
			if (activity instanceof RevisionsActivity) {
				Timepoint last = activityLifecycleChanges.get(activityLifecycleChanges.size() - 1);
				
				if (last.getType() != TimepointType.REVISION_INTERRUPTED) {
					activityLifecycleChanges.add(Timepoint.create(TimepointType.REVISION_FINISHED));
				}
			}
		}
	}
	
}
