package pkleczek.profiwan.dictionary;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.RussianKeyboard;
import pkleczek.profiwan.model.AndroidPhraseEntry;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import android.app.ListActivity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class DictionaryActivity extends ListActivity {

	public static final String EDITED_PHRASE = "pkleczek.profiwan.dictionary.EDITED_PHRASE";
	public static final int EDIT_ACTIVITY = 1;

	private DatabaseHelper dbHelper;

	private EditText edittext;
	private ListActivity listactivity;

	private RussianKeyboard mCustomKeyboard;

	private Keyboard mKeyboard;
	private PhraseEntryArrayAdapter adapter;
	private List<PhraseEntry> dictionary;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictionary);

		listactivity = this;

		mKeyboard = new Keyboard(this, R.xml.kbd_rus);

		mCustomKeyboard = new RussianKeyboard(this, R.id.dictionary_kbd,
				R.xml.kbd_rus);

		mCustomKeyboard.registerEditText(R.id.dictionary_autoPhrase);
		KeyboardView mKeyboardView = (KeyboardView) findViewById(R.id.dictionary_kbd);
		mKeyboardView.setKeyboard(mKeyboard);
		mKeyboardView.setPreviewEnabled(false);

		dbHelper = DatabaseHelperImpl.getInstance(this);
		dictionary = dbHelper.getDictionary();

		// FIXME: refresh lists after an item added/deleted
		// PhraseEntryArrayAdapter adapter = new PhraseEntryArrayAdapter(this,
		// dictionary);
		// setListAdapter(adapter);

		List<String> lookupList = new ArrayList<String>();
		for (PhraseEntry pe : dictionary) {
			lookupList.add(pe.getLangAText());
		}

		// autocompletetextview = (AutoCompleteTextView)
		// findViewById(R.id.dictionary_autoPhrase);
		// ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.select_dialog_item, lookupList);
		// autocompletetextview.setThreshold(1);
		// autocompletetextview.setAdapter(autoAdapter);
		// autocompletetextview.setInputType(autocompletetextview.getInputType()
		// | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

		// debug
		edittext = (EditText) findViewById(R.id.dictionary_autoPhrase);
		edittext.requestFocus();

		edittext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Log.i("profiwan", "text changed: " + s);
				// listactivity.setSelection(1);

				adapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		mKeyboardView.setVisibility(View.INVISIBLE);
		
		adapter = new PhraseEntryArrayAdapter(this, dictionary);
		setListAdapter(adapter);
		
		getListView().setFastScrollEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();

		dictionary = dbHelper.getDictionary();
		adapter.notifyDataSetChanged();
		
		SideBar indexBar = (SideBar) findViewById(R.id.sideBar);  
        indexBar.setListView((PhraseListView) getListView());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		PhraseEntry item = (PhraseEntry) l.getItemAtPosition(position);
		Intent intent = new Intent(this, DictionaryEditActivity.class);
		AndroidPhraseEntry pe = new AndroidPhraseEntry(item);
		intent.putExtra(EDITED_PHRASE, pe);
		startActivityForResult(intent, EDIT_ACTIVITY);
	}

	public void editPhrase(View v) {
		Intent intent = new Intent(this, DictionaryEditActivity.class);
		PhraseEntry item = new PhraseEntry();
		item.setCreatedAt(DateTime.now());
		AndroidPhraseEntry pe = new AndroidPhraseEntry(item);
		intent.putExtra(EDITED_PHRASE, pe);
		startActivityForResult(intent, EDIT_ACTIVITY);
	}
}
