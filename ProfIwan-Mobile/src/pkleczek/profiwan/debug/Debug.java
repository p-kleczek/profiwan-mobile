package pkleczek.profiwan.debug;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.model.RevisionEntry;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.DatabaseHelperImplMock;
import android.database.Cursor;
import android.util.Log;

public class Debug {

	private static final String LOG_TAG = Debug.class.getName();

	public static void populateDB(DatabaseHelper dbHelper) {

		PhraseEntry e = null;
		RevisionEntry re = null;

		e = new PhraseEntry();
		e.setLangA("pl");
		e.setLangB("rus");
		e.setLangAText("а");
		e.setLangBText("а");
		e.setCreatedAt(DateTime.now());
		e.setLabel("rand");
		e.setInRevisions(true);
		dbHelper.createPhrase(e);
		
		re = new RevisionEntry();
		re.setCreatedAt(new DateTime(2013, 12, 5, 10, 15, 8));
		re.setMistakes(3);
		e.getRevisions().add(re);
		dbHelper.createRevision(re, e.getId());

		re = new RevisionEntry();
		re.setCreatedAt(new DateTime(2014, 1, 2, 10, 15, 8));
		re.setMistakes(2);
		e.getRevisions().add(re);
		dbHelper.createRevision(re, e.getId());
		

		e = new PhraseEntry();
		e.setLangA("pl");
		e.setLangB("rus");
		e.setLangAText("ала");
		e.setLangBText("ma");
		e.setCreatedAt(DateTime.now());
		e.setLabel("rand");
		e.setInRevisions(true);
		dbHelper.createPhrase(e);
//
//		e = new PhraseEntry();
//		e.setLangA("pl");
//		e.setLangB("rus");
//		e.setLangAText("x");
//		e.setLangBText("y");
//		e.setCreationDate(DateTime.now());
//		e.setLabel("rand");
//		e.setInRevisions(true);
//		dbHelper.createPhrase(e);
//
//		e.setLangAText("-x-"); //$NON-NLS-1$
//		dbHelper.updatePhrase(e);


		//
		// re = new RevisionEntry();
		// cal.set(2013, 12, 15);
		// re.date = cal.getTime();
		// re.mistakes = 2;
		// e.getRevisions().add(re);
		// re.insertDBEntry(1);
		//
		// re = new RevisionEntry();
		// cal = Calendar.getInstance();
		// re.date = cal.getTime();
		// re.mistakes = 2;
		// e.getRevisions().add(re);
		// re.insertDBEntry(1);

		Debug.printDict(dbHelper, "init"); //$NON-NLS-1$
	}

	public static void printDict(DatabaseHelper dbhHelper, String operation) {
		List<PhraseEntry> dict = dbhHelper.getDictionary();
		Log.d(LOG_TAG, String.format("--- %s (%d) ---", operation, dict.size()));
		for (PhraseEntry e : dict) {
			String[] out = e.toString().split("\\n");
			for (String s : out) {
				Log.d(LOG_TAG, s);
			}
		}

		Log.d(LOG_TAG, "------------");
		Log.d(LOG_TAG, "");
	}

	public ArrayList<String[]> getDbTableDetails(DatabaseHelper dbHelper) {
		
		DatabaseHelperImpl dbHelperImpl = (DatabaseHelperImpl) dbHelper;
		
		Cursor c = dbHelperImpl.getReadableDatabase().rawQuery(
				"SELECT name FROM sqlite_master WHERE type='table'", null);
		ArrayList<String[]> result = new ArrayList<String[]>();
		int i = 0;
		result.add(c.getColumnNames());
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			String[] temp = new String[c.getColumnCount()];
			for (i = 0; i < temp.length; i++) {
				temp[i] = c.getString(i);
			}

			System.out.println(temp);
			result.add(temp);
		}

		return result;
	}
}
