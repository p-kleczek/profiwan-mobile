package pkleczek.profiwan.dictionary;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.keyboards.RussianKeyboard;
import pkleczek.profiwan.utils.Language;
import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class DictionaryEditActivity extends Activity {

	// private static Integer[] imageIconDatabase = {
	// R.drawable.flag_pl,
	// R.drawable.flag_rus };
	//
	// private String[] imageNameDatabase = { "pl", "rus" };

	private Language knowLang = Language.PL;
	private Language revisedLang = Language.RUS;

	private Keyboard mKeyboard;
	private CustomKeyboard mCustomKeyboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dictionary_edit);

		mKeyboard = new Keyboard(this, R.xml.kbd_rus);
		mCustomKeyboard = new RussianKeyboard(this, R.id.dictionary_edit_kbd,
				R.xml.kbd_rus);

		mCustomKeyboard.registerEditText(R.id.dictionary_edit_revisedLanguage);
		KeyboardView mKeyboardView = (KeyboardView) findViewById(R.id.dictionary_edit_kbd);
		mKeyboardView.setKeyboard(mKeyboard);
		mKeyboardView.setPreviewEnabled(false);

		Spinner spnKnownLang = (Spinner) findViewById(R.id.dictionary_spin_knownLanguage);
		spnKnownLang.setAdapter(new FlagSpinnerAdapter(this));
		spnKnownLang.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				knowLang = (Language) parent.getItemAtPosition(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		Spinner spnRevisedLang = (Spinner) findViewById(R.id.dictionary_spin_revisedLanguage);
		spnRevisedLang.setAdapter(new FlagSpinnerAdapter(this));
		spnRevisedLang.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				revisedLang = (Language) parent.getItemAtPosition(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		EditText etRevisedLanguage = (EditText) findViewById(R.id.dictionary_edit_revisedLanguage);
		// etRevisedLanguage.requestFocus();

	}

	@Override
	public void onBackPressed() {
		if (mCustomKeyboard.isCustomKeyboardVisible()) {
			mCustomKeyboard.hideCustomKeyboard();
		} else {
			super.onBackPressed();
		}
	}

	// XXX: add similar code to other activities where custom keyboard is being
	// used
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//
//		}
//
//		return super.onKeyDown(keyCode, event);
//	}

}
