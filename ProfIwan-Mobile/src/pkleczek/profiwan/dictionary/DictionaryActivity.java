package pkleczek.profiwan.dictionary;

import java.util.List;

import org.joda.time.DateTime;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.model.AndroidPhraseEntry;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.lang.FlagSpinnerAdapter;
import pkleczek.profiwan.utils.lang.Language;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

public class DictionaryActivity extends ListActivity {

	public static final String EDITED_PHRASE = "pkleczek.profiwan.dictionary.EDITED_PHRASE";
	public static final int EDIT_PHRASE_REQUEST_CODE = 1;

	private DatabaseHelper dbHelper;

	private EditText edittext;

	/**
	 * Language used to filer displayed phrases.
	 */
	private Language sessionLanguage = null;

	private CustomKeyboard kbd;

	private PhraseEntryArrayAdapter adapter;
	private List<PhraseEntry> dictionary;

	private PhraseEntry editedPhrase = null;
	private SideBar indexBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictionary);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String langPref = prefs.getString(
				getString(R.string.pref_key_revised_language), null);
		sessionLanguage = Language.getLanguageByCode(langPref);

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

		indexBar = (SideBar) findViewById(R.id.sideBar);
		adapter = new PhraseEntryArrayAdapter(this, dictionary, indexBar);
		adapter.setSessionLanguage(sessionLanguage);
		setListAdapter(adapter);

		getListView().setFastScrollEnabled(true);

		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v instanceof PhraseListView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(adapter.getItem(info.position).getLangBText());
			String[] menuItems = getResources().getStringArray(
					R.array.dictionary_context_menu_items);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(
				R.array.dictionary_context_menu_items);
		String menuItemName = menuItems[menuItemIndex];

		PhraseEntry pe = adapter.getItem(info.position);

		if (menuItemName.equals(getString(R.string.dict_menu_delete))) {
			dictionary.remove(pe);
			dbHelper.deletePhrase(pe.getId());
			adapter.notifyPhraseListChanged();
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// TODO: make it smarter - don't reload whole list

		// dictionary.clear();
		// dictionary.addAll(dbHelper.getDictionary());
		adapter.notifyPhraseListChanged();

		indexBar.setListView((PhraseListView) getListView());
	}

	@Override
	public void onBackPressed() {
		if (kbd != null && kbd.isCustomKeyboardVisible()) {
			kbd.hideCustomKeyboard();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		editedPhrase = (PhraseEntry) l.getItemAtPosition(position);
		Intent intent = new Intent(this, DictionaryEditActivity.class);
		AndroidPhraseEntry pe = new AndroidPhraseEntry(editedPhrase);
		intent.putExtra(EDITED_PHRASE, pe);

		startActivityForResult(intent, EDIT_PHRASE_REQUEST_CODE);
	}

	public void addPhrase(View v) {
		Intent intent = new Intent(this, DictionaryEditActivity.class);
		editedPhrase = createDefaultPhrase();
		AndroidPhraseEntry pe = new AndroidPhraseEntry(editedPhrase);
		intent.putExtra(EDITED_PHRASE, pe);
		// startActivity(intent);
		startActivityForResult(intent, EDIT_PHRASE_REQUEST_CODE);
	}

	private PhraseEntry createDefaultPhrase() {
		PhraseEntry item = new PhraseEntry();
		item.setCreatedAt(DateTime.now());

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String langAPref = prefs.getString(
				getString(R.string.pref_key_known_language), null);
		String langBPref = prefs.getString(
				getString(R.string.pref_key_revised_language), null);

		item.setLangA(langAPref);
		item.setLangB(langBPref);

		return item;
	}

	public void inRevisionsClick(View v) {
		PhraseEntry pe = (PhraseEntry) v.getTag();
		CheckBox cbx = (CheckBox) v;

		pe.setInRevisions(cbx.isChecked());
		dbHelper.updatePhrase(pe);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_dictionary, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_choose_language:
			showLanguageChooser();
			break;
		default:
			break;
		}

		return true;
	}

	private void showLanguageChooser() {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("Choose language");
		FlagSpinnerAdapter fsa = new FlagSpinnerAdapter(this);
		b.setSingleChoiceItems(fsa, sessionLanguage.ordinal(),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						Language chosenLanguage = Language.values()[which];
						sessionLanguage = chosenLanguage;
						adapter.setSessionLanguage(sessionLanguage);
					}

				});

		b.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT_PHRASE_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {
			PhraseEntry pe = data.getParcelableExtra(EDITED_PHRASE);

			if (pe.getId() == 0) {
				dictionary.add(pe);
				dbHelper.createPhrase(pe);
			} else {
				editedPhrase.copyData(pe);
				dbHelper.updatePhrase(editedPhrase);
			}

			adapter.notifyPhraseListChanged();
		}
	}

}
