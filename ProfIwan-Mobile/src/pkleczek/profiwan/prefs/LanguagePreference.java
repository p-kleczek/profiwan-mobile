package pkleczek.profiwan.prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pkleczek.profiwan.utils.lang.Language;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class LanguagePreference extends ListPreference {

	public LanguagePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setEntries(computeEntries());
		setEntryValues(computeEntryValues());
	}
	
	private CharSequence[] computeEntryValues() {
		List<CharSequence> values = new ArrayList<CharSequence>();
		
		for (Language lang : Language.values()) {
			values.add(lang.getLanguageISOCode());
		}
		
		return values.toArray(new String[0]);
	}

	private CharSequence[] computeEntries() {
		List<CharSequence> values = new ArrayList<CharSequence>();
		
		for (Language lang : Language.values()) {
			Locale locale = new Locale(lang.getLanguageISOCode());
			values.add(locale.getDisplayLanguage());
		}
		
		return values.toArray(new String[0]);
	}
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		// TODO Auto-generated method stub
//		int index = findIndexOfValue(getSharedPreferences().getString(getKey(),
//				"1"));

//		ListAdapter listAdapter = (ListAdapter) new FlagSpinnerAdapter(
//				getContext());
//
//		builder.setAdapter(listAdapter, this);
		super.onPrepareDialogBuilder(builder);
	}

}
