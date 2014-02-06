package pkleczek.profiwan.dictionary;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.keyboards.RussianKeyboard;
import pkleczek.profiwan.utils.Language;
import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class DictionaryEditActivity extends Activity {

	// private static Integer[] imageIconDatabase = {
	// R.drawable.flag_pl,
	// R.drawable.flag_rus };
	//
	// private String[] imageNameDatabase = { "pl", "rus" };

	private Language knowLang = Language.PL;
	private Language revisedLang = Language.RU;

	private Keyboard mRevisedKeyboard;
	private CustomKeyboard mRevisedCustomKeyboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dictionary_edit);

		Spinner spnKnownLang = (Spinner) findViewById(R.id.dictionary_spin_knownLanguage);
		spnKnownLang.setAdapter(new FlagSpinnerAdapter(this));
		spnKnownLang.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				knowLang = (Language) parent.getItemAtPosition(pos);
				changeKeyboard(R.id.dictionary_edit_knownLanguage, knowLang);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spnKnownLang.setSelection(knowLang.ordinal());

		Spinner spnRevisedLang = (Spinner) findViewById(R.id.dictionary_spin_revisedLanguage);
		spnRevisedLang.setAdapter(new FlagSpinnerAdapter(this));
		spnRevisedLang.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				revisedLang = (Language) parent.getItemAtPosition(pos);
				changeKeyboard(R.id.dictionary_edit_revisedLanguage,
						revisedLang);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spnRevisedLang.setSelection(revisedLang.ordinal());
	}

	@Override
	public void onBackPressed() {
		if (mRevisedCustomKeyboard.isCustomKeyboardVisible()) {
			mRevisedCustomKeyboard.hideCustomKeyboard();
		} else {
			super.onBackPressed();
		}
	}

	private void changeKeyboard(int editTextId, Language lang) {

		if (mRevisedKeyboard != null) {
			mRevisedCustomKeyboard.unregisterEditText(editTextId);
		}

		if (lang != Language.PL) {
			mRevisedKeyboard = new Keyboard(this, lang.getKeyboard());
			mRevisedCustomKeyboard = new RussianKeyboard(this,
					R.id.dictionary_edit_kbd, lang.getKeyboard());

			mRevisedCustomKeyboard.registerEditText(editTextId);

			KeyboardView mKeyboardView = (KeyboardView) findViewById(R.id.dictionary_edit_kbd);
			mKeyboardView.setKeyboard(mRevisedKeyboard);
			mKeyboardView.setPreviewEnabled(false);
		}
	}
}
