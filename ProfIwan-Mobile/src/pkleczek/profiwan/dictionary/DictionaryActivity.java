package pkleczek.profiwan.dictionary;

import java.util.List;

import org.joda.time.DateTime;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.model.AndroidPhraseEntry;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.Language;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

public class DictionaryActivity extends ListActivity {

	public static final String EDITED_PHRASE = "pkleczek.profiwan.dictionary.EDITED_PHRASE";

	private DatabaseHelper dbHelper;

	private EditText edittext;

	private Language sessionLanguage = Language.RU;
	private CustomKeyboard kbd;

	private PhraseEntryArrayAdapter adapter;
	private List<PhraseEntry> dictionary;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictionary);

		kbd = CustomKeyboard.changeKeyboard(this, kbd,
				R.id.dictionary_autoPhrase, R.id.dictionary_kbd,
				sessionLanguage);

		dbHelper = DatabaseHelperImpl.getInstance(this);
		dictionary = dbHelper.getDictionary();

		// debug
		edittext = (EditText) findViewById(R.id.dictionary_autoPhrase);
		edittext.requestFocus();

		edittext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// XXX : ok?
		// mKeyboardView.setVisibility(View.INVISIBLE);

		adapter = new PhraseEntryArrayAdapter(this, dictionary);
		setListAdapter(adapter);

		getListView().setFastScrollEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// TODO: make it smarter - don't reload whole list

		dictionary.clear();
		dictionary.addAll(dbHelper.getDictionary());
		adapter.notifyPhraseListChanged();

		SideBar indexBar = (SideBar) findViewById(R.id.sideBar);
		indexBar.setListView((PhraseListView) getListView());
	}

	@Override
	public void onBackPressed() {
		if (kbd.isCustomKeyboardVisible()) {
			kbd.hideCustomKeyboard();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		PhraseEntry item = (PhraseEntry) l.getItemAtPosition(position);
		Intent intent = new Intent(this, DictionaryEditActivity.class);
		AndroidPhraseEntry pe = new AndroidPhraseEntry(item);
		intent.putExtra(EDITED_PHRASE, pe);
		startActivity(intent);
	}

	public void editPhrase(View v) {
		Intent intent = new Intent(this, DictionaryEditActivity.class);
		PhraseEntry item = createDefaultPhrase();
		AndroidPhraseEntry pe = new AndroidPhraseEntry(item);
		intent.putExtra(EDITED_PHRASE, pe);
		startActivity(intent);
	}

	private PhraseEntry createDefaultPhrase() {
		PhraseEntry item = new PhraseEntry();
		item.setCreatedAt(DateTime.now());

		// TODO: do it with (default) settings
		item.setLangA(Language.PL.getLanguageISOCode());
		item.setLangB(Language.PL.getLanguageISOCode());

		return item;
	}

	public void inRevisionsClick(View v) {
		PhraseEntry pe = (PhraseEntry) v.getTag();
		CheckBox cbx = (CheckBox) v;

		pe.setInRevisions(cbx.isChecked());
		dbHelper.updatePhrase(pe);
	}
}
