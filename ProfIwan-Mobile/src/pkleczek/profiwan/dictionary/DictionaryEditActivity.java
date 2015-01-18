package pkleczek.profiwan.dictionary;

import org.joda.time.DateTime;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.model.AndroidPhraseEntry;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.revisions.RevisionsEnteredActivity;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.lang.FlagSpinnerAdapter;
import pkleczek.profiwan.utils.lang.Language;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DictionaryEditActivity extends Activity {

	private Language langA;
	private Language langB;

	private CustomKeyboard kbdLangA;
	private CustomKeyboard kbdLangB;

	private PhraseEntry editedPhrase;

	private EditText langAEditText;
	private EditText langBEditText;
	private EditText labelEditText;

	private final DictionaryEditActivity instance = this;

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

		initializeFieldsValues();

		Spinner spnLangA = (Spinner) findViewById(R.id.dictionary_spin_langA);
		spnLangA.setAdapter(new FlagSpinnerAdapter(this));
		spnLangA.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				langA = (Language) parent.getItemAtPosition(pos);

				kbdLangA = CustomKeyboard.changeKeyboard(instance, kbdLangA,
						R.id.dictionary_edit_langA, R.id.dictionary_edit_kbd,
						langA);
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

				kbdLangB = CustomKeyboard.changeKeyboard(instance, kbdLangB,
						R.id.dictionary_edit_langB, R.id.dictionary_edit_kbd,
						langB);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spnLangB.setSelection(langB.ordinal());
	}

	private void initializeFieldsValues() {
		langA = Language.getLanguageByCode(editedPhrase.getLangA());
		langB = Language.getLanguageByCode(editedPhrase.getLangB());

		langAEditText.setText(editedPhrase.getLangAText());
		langBEditText.setText(editedPhrase.getLangBText());
		labelEditText.setText(editedPhrase.getLabel());
	}

	@Override
	public void onBackPressed() {
		if (kbdLangA != null && kbdLangA.isCustomKeyboardVisible()) {
			kbdLangA.hideCustomKeyboard();
		} else if (kbdLangB != null && kbdLangB.isCustomKeyboardVisible()) {
			kbdLangB.hideCustomKeyboard();
		} else {
			super.onBackPressed();
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

//		DatabaseHelper dbHelper = DatabaseHelperImpl.getInstance(this);

		if (editedPhrase.getId() == 0) {
			editedPhrase.setInRevisions(true);
			editedPhrase.setCreatedAt(DateTime.now());
//			This operations are performed in DictionaryActivity
//			dbHelper.createPhrase(editedPhrase);
		} else {
//			dbHelper.updatePhrase(editedPhrase);
		}

		Intent resultIntent = new Intent();
		AndroidPhraseEntry ape = new AndroidPhraseEntry(editedPhrase);
		resultIntent.putExtra(DictionaryActivity.EDITED_PHRASE, ape);
		setResult(Activity.RESULT_OK, resultIntent);
		
		finish();
	}
}
