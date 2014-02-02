package pkleczek.profiwan.dictionary;

import pkleczek.profiwan.R;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class DictionaryActivity extends ListActivity {
	
	DatabaseHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictionary);

		dbHelper = DatabaseHelperImpl.getInstance(this);
		
		PhraseEntryArrayAdapter adapter = new PhraseEntryArrayAdapter(this, dbHelper.getDictionary());
		setListAdapter(adapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		final PhraseEntry item = (PhraseEntry) l.getItemAtPosition(position);
		
		Log.i("profiwan", item.toString());
	}

}
