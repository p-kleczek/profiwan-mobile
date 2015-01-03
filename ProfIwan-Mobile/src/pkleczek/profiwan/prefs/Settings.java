package pkleczek.profiwan.prefs;

import pkleczek.profiwan.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);

		super.onDestroy();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO here
		
//		if (preference == mCapacitiveBacklight) {
//            int valCapacitiveBacklight = Integer.valueOf((String) newValue);
// 
//            Settings.System.putInt(getContentResolver(), "tweaks_capacitive_backlight",
//                    valCapacitiveBacklight);
//             
//            if (ShellInterface.isSuAvailable()) {
//                ShellInterface.runCommand("echo '"+valCapacitiveBacklight+"' > /sys/devices/platform/leds-pm8058/leds/button-backlight/currents");
//            }     
//             
//             
//            return true;
//        } 
// 
//        return false;
	}
}
