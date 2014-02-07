package pkleczek.profiwan.dictionary;

import org.joda.time.DateTime;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.keyboards.RussianKeyboard;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.Language;
import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DictionaryEditActivity extends Activity {

	private Language langA = Language.PL;
	private Language langB = Language.RU;

	// XXX: rename
	private Keyboard mRevisedKeyboard;
	private CustomKeyboard mRevisedCustomKeyboard;

	private PhraseEntry editedPhrase;

	private EditText langAEditText;
	private EditText langBEditText;
	private EditText labelEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dictionary_edit);

		langAEditText = (EditText) findViewById(R.id.dictionary_edit_langA);
		langBEditText = (EditText) findViewById(R.id.dictionary_edit_langB);
		labelEditText = (EditText) findViewById(R.id.dictionary_edit_label);

		try {
			editedPhrase = getIntent().getParcelableExtra(
					DictionaryActivity.EDITED_PHRASE);
		} catch (ClassCastException e) {
			Log.e("REA", e.toString());
			finish();
		}

		Spinner spnLangA = (Spinner) findViewById(R.id.dictionary_spin_langA);
		spnLangA.setAdapter(new FlagSpinnerAdapter(this));
		spnLangA.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				langA = (Language) parent.getItemAtPosition(pos);
				changeKeyboard(R.id.dictionary_edit_langA, langA);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spnLangA.setSelection(langA.ordinal());

		Spinner spnLangB = (Spinner) findViewById(R.id.dictionary_spin_langB);
		spnLangB.setAdapter(new FlagSpinnerAdapter(this));
		spnLangB.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				langB = (Language) parent.getItemAtPosition(pos);
				changeKeyboard(R.id.dictionary_edit_langB,
						langB);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spnLangB.setSelection(langB.ordinal());

		initializeFieldsValues();
	}

	private void initializeFieldsValues() {
		// TODO: restore flags' values

		langAEditText.setText(editedPhrase.getLangAText());
		langBEditText.setText(editedPhrase.getLangBText());
		labelEditText.setText(editedPhrase.getLabel());
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

	public void savePhrase(View view) {
		String knownText = langAEditText.getText().toString();
		String revisedText = langBEditText.getText().toString();
		String labelText = labelEditText.getText().toString();

		if (knownText.isEmpty() || revisedText.isEmpty()) {
			Toast.makeText(this, "Not all values provided!", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		editedPhrase.setLangA(langA.getLanguageISOCode());
		editedPhrase.setLangAText(knownText);
		editedPhrase.setLangB(langB.getLanguageISOCode());
		editedPhrase.setLangBText(revisedText);
		editedPhrase.setLabel(labelText);

		DatabaseHelper dbHelper = DatabaseHelperImpl.getInstance(this);

		if (editedPhrase.getId() == 0) {
			editedPhrase.setInRevisions(true);
			editedPhrase.setCreatedAt(DateTime.now());

			dbHelper.createPhrase(editedPhrase);
		} else {
			dbHelper.updatePhrase(editedPhrase);
		}

		finish();
	}
}
