package pkleczek.profiwan.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pkleczek.profiwan.ProfIwanApplication;
import pkleczek.profiwan.ProfIwanApplication.RunningMode;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.model.RevisionEntry;
import pkleczek.profiwan.model.Timepoint;
import pkleczek.profiwan.model.Timepoint.TimepointType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelperImpl extends SQLiteOpenHelper implements
		DatabaseHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String TEST_DATABASE_NAME = "profiwan_test";

	private static DatabaseHelper instance;

	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelperImpl(context.getApplicationContext());
		}
		return instance;
	}

	private DatabaseHelperImpl(Context context) {
		super(context,
				(ProfIwanApplication.runningMode == RunningMode.TEST) ? null
						: DATABASE_NAME, null, DATABASE_VERSION);

		// XXX: debug!
		// clearDB();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_PHRASE);
		db.execSQL(CREATE_TABLE_REVISION);
		db.execSQL(CREATE_TABLE_TIMEPOINT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHRASE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVISION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMEPOINT);

		onCreate(db);
	}

	public long createPhrase(PhraseEntry phrase) {

		ContentValues values = new ContentValues();
		values.put(KEY_PHRASE_LANG1, phrase.getLangA());
		values.put(KEY_PHRASE_LANG2, phrase.getLangB());
		values.put(KEY_PHRASE_LANG1_TEXT, phrase.getLangAText());
		values.put(KEY_PHRASE_LANG2_TEXT, phrase.getLangBText());
		values.put(KEY_PHRASE_LABEL, phrase.getLabel());
		values.put(KEY_PHRASE_IN_REVISION, phrase.isInRevisions());
		values.put(KEY_CREATED_AT,
				DBUtils.getIntFromDateTime(phrase.getCreatedAt()));

		// insert row
		long phrase_id = 0;

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			phrase_id = db.insert(TABLE_PHRASE, null, values);
		} finally {
			closeDB();
		}

		if (phrase_id == 0) {
			Log.e(TAG, "insert PHRASE: _id = 0");
		}

		phrase.setId(phrase_id);

		return phrase_id;
	}

	private List<PhraseEntry> getAllPhrases() {
		List<PhraseEntry> phrases = new ArrayList<PhraseEntry>();
		String selectQuery = "SELECT  * FROM " + TABLE_PHRASE;

		Cursor c = null;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			c = db.rawQuery(selectQuery, null);

			if (c.moveToFirst()) {
				do {
					PhraseEntry pe = new PhraseEntry();

					pe.setId(c.getInt(c.getColumnIndex(KEY_ID)));
					pe.setLangA(c.getString(c.getColumnIndex(KEY_PHRASE_LANG1)));
					pe.setLangB(c.getString(c.getColumnIndex(KEY_PHRASE_LANG2)));
					pe.setLangAText(c.getString(c
							.getColumnIndex(KEY_PHRASE_LANG1_TEXT)));
					pe.setLangBText(c.getString(c
							.getColumnIndex(KEY_PHRASE_LANG2_TEXT)));
					pe.setLabel(c.getString(c.getColumnIndex(KEY_PHRASE_LABEL)));
					pe.setInRevisions(c.getInt(c
							.getColumnIndex(KEY_PHRASE_IN_REVISION)) != 0);
					pe.setCreatedAt(DBUtils.getDateTimeFromInt(c.getInt(c
							.getColumnIndex(KEY_CREATED_AT))));

					phrases.add(pe);
				} while (c.moveToNext());
			}
		} finally {
			closeCursor(c);
			closeDB();
		}

		Collections.sort(phrases);

		return phrases;
	}

	public int updatePhrase(PhraseEntry phrase) {

		ContentValues values = new ContentValues();
		values.put(KEY_PHRASE_LANG1, phrase.getLangA());
		values.put(KEY_PHRASE_LANG2, phrase.getLangB());
		values.put(KEY_PHRASE_LANG1_TEXT, phrase.getLangAText());
		values.put(KEY_PHRASE_LANG2_TEXT, phrase.getLangBText());
		values.put(KEY_PHRASE_LABEL, phrase.getLabel());
		values.put(KEY_PHRASE_IN_REVISION, phrase.isInRevisions());

		int ret = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ret = db.update(TABLE_PHRASE, values, KEY_ID + " = ?",
					new String[] { String.valueOf(phrase.getId()) });
		} finally {
			closeDB();
		}

		return ret;
	}

	public void deletePhrase(long phrase_id) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_PHRASE, KEY_ID + " = ?",
					new String[] { String.valueOf(phrase_id) });
		} finally {
			closeDB();
		}
	}

	public long createRevision(RevisionEntry revision, long phrase_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_REVISION_MISTAKES, revision.getMistakes());
		values.put(KEY_REVISION_PHRASE_ID, phrase_id);
		values.put(KEY_CREATED_AT,
				DBUtils.getIntFromDateTime(revision.getCreatedAt()));

		long revision_id = db.insert(TABLE_REVISION, null, values);
		revision.setId(revision_id);

		return revision_id;
	}

	public int updateRevision(RevisionEntry revision) {

		ContentValues values = new ContentValues();
		values.put(KEY_REVISION_MISTAKES, revision.getMistakes());

		int ret = 0;

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ret = db.update(TABLE_REVISION, values, KEY_ID + " = ?",
					new String[] { String.valueOf(revision.getId()) });
		} finally {
			closeDB();
		}

		return ret;
	}

	private List<RevisionEntry> getAllRevisions(long phrase_id) {
		List<RevisionEntry> revisions = new ArrayList<RevisionEntry>();

		Cursor c = null;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			c = db.query(TABLE_REVISION, null, KEY_REVISION_PHRASE_ID + "="
					+ phrase_id, null, null, null, KEY_CREATED_AT);

			if (c.moveToFirst()) {
				do {
					RevisionEntry re = new RevisionEntry();

					re.setId(c.getInt(c.getColumnIndex(KEY_ID)));
					re.setMistakes(c.getInt(c
							.getColumnIndex(KEY_REVISION_MISTAKES)));
					re.setCreatedAt(DBUtils.getDateTimeFromInt(c.getInt(c
							.getColumnIndex(KEY_CREATED_AT))));

					revisions.add(re);
				} while (c.moveToNext());
			}
		} finally {
			closeCursor(c);
			closeDB();
		}

		return revisions;
	}

	public List<PhraseEntry> getDictionary() {
		List<PhraseEntry> phrases = getAllPhrases();

		for (PhraseEntry pe : phrases) {
			List<RevisionEntry> revisions = getAllRevisions(pe.getId());
			pe.getRevisions().addAll(revisions);
		}

		return phrases;
	}

	private void closeCursor(Cursor c) {
		if (c != null && !c.isClosed()) {
			c.close();
		}
	}

	private void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen()) {
			if (ProfIwanApplication.runningMode != RunningMode.TEST) {
				db.close();
			}
		}
	}

	public void clearDB() {
		SQLiteDatabase db = this.getReadableDatabase();

		try {
			db.delete(TABLE_PHRASE, null, null);
			db.delete(TABLE_REVISION, null, null);
			db.delete(TABLE_TIMEPOINT, null, null);
		} finally {
			closeDB();
		}
	}

	@Override
	public long createTimepoint(Timepoint timepoint) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMEPOINT_TYPE, timepoint.getType().name());
		values.put(KEY_CREATED_AT,
				DBUtils.getIntFromDateTime(timepoint.getCreatedAt()));

		long timepoint_id = db.insert(TABLE_TIMEPOINT, null, values);
		timepoint.setId(timepoint_id);

		return timepoint_id;
	}

	@Override
	public List<Timepoint> getAllTimepoints() {
		List<Timepoint> timepoints = new ArrayList<Timepoint>();
		String selectQuery = "SELECT  * FROM " + TABLE_TIMEPOINT;
		
		Cursor c = null;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			c = db.rawQuery(selectQuery, null);

			if (c.moveToFirst()) {
				do {
					Timepoint t = new Timepoint();

					t.setId(c.getInt(c.getColumnIndex(KEY_ID)));
					t.setCreatedAt(DBUtils.getDateTimeFromInt(c.getInt(c
							.getColumnIndex(KEY_CREATED_AT))));
					t.setType(TimepointType.valueOf(c.getString(c.getColumnIndex(KEY_TIMEPOINT_TYPE))));
					timepoints.add(t);
				} while (c.moveToNext());
			}
		} finally {
			closeCursor(c);
			closeDB();
		}

		Collections.sort(timepoints);

		return timepoints;
	}
}
