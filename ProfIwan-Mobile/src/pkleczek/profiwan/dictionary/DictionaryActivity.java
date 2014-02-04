package pkleczek.profiwan.dictionary;

import java.util.ArrayList;
import java.util.List;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.RussianKeyboard;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import android.app.ListActivity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

public class DictionaryActivity extends ListActivity {

	DatabaseHelper dbHelper;

	AutoCompleteTextView autocompletetextview;

	private RussianKeyboard mCustomKeyboard;

	private Keyboard mKeyboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictionary);

		mKeyboard = new Keyboard(this, R.xml.kbd_rus);
		
		mCustomKeyboard = new RussianKeyboard(this, R.id.dictionary_kbd,
				R.xml.kbd_rus);

		mCustomKeyboard.registerEditText(R.id.dictionary_autoPhrase);
		KeyboardView mKeyboardView = (KeyboardView) findViewById(R.id.dictionary_kbd);
		mKeyboardView.setKeyboard(mKeyboard);
		mKeyboardView.setPreviewEnabled(false);

		dbHelper = DatabaseHelperImpl.getInstance(this);
		List<PhraseEntry> dictionary = dbHelper.getDictionary();

		// FIXME: refresh lists after an item added/deleted
		PhraseEntryArrayAdapter adapter = new PhraseEntryArrayAdapter(this,
				dictionary);
		setListAdapter(adapter);

		List<String> lookupList = new ArrayList<String>();
		for (PhraseEntry pe : dictionary) {
			lookupList.add(pe.getLangAText());
		}

		autocompletetextview = (AutoCompleteTextView) findViewById(R.id.dictionary_autoPhrase);
		ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, lookupList);
		autocompletetextview.setThreshold(1);
		autocompletetextview.setAdapter(autoAdapter);
		autocompletetextview.setInputType(autocompletetextview.getInputType()
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

		// debug
		// etRevisedLanguage.requestFocus();
		autocompletetextview.performClick();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		final PhraseEntry item = (PhraseEntry) l.getItemAtPosition(position);

		Log.i("profiwan", item.toString());
	}

}
