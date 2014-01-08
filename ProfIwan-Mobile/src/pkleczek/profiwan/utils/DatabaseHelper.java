package pkleczek.profiwan.utils;

import java.util.ArrayList;
import java.util.List;

import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.model.PhraseEntry.RevisionEntry;
import pkleczek.profiwan.model.RevisionsSession;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = RevisionsSession.class.getName();

	public static final String TABLE_COMMENTS = "comments";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_COMMENT = "comment";

	private static final String DATABASE_NAME = "profiwan";

	// Table Names
	private static final String TABLE_PHRASE = "Phrase";
	private static final String TABLE_REVISION = "Revision";

	// Common column names
	private static final String KEY_ID = "_id";
	private static final String KEY_CREATED_AT = "created_at";

	// PHRASE Table - column names
	private static final String KEY_PHRASE_LANG1 = "lang1";
	private static final String KEY_PHRASE_LANG2 = "lang2";
	private static final String KEY_PHRASE_LANG1_TEXT = "lang1_text";
	private static final String KEY_PHRASE_LANG2_TEXT = "lang2_text";
	private static final String KEY_PHRASE_LABEL = "label";
	private static final String KEY_PHRASE_IN_REVISION = "in_revision";

	// PHRASE Table - column names
	private static final String KEY_REVISION_MISTAKES = "mistakes";
	private static final String KEY_REVISION_PHRASE_ID = "Phrase_idPhrase";

	// Table Create Statements
	// PHRASE Table - create statement
	private static final String CREATE_TABLE_PHRASE = "CREATE TABLE "
			+ TABLE_PHRASE + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PHRASE_LANG1
			+ " TEXT NOT NULL," + KEY_PHRASE_LANG2 + " TEXT NOT NULL,"
			+ KEY_PHRASE_LANG1_TEXT + " TEXT NOT NULL," + KEY_PHRASE_LANG2_TEXT
			+ " TEXT NOT NULL," + KEY_PHRASE_LABEL + " TEXT NOT NULL,"
			+ KEY_CREATED_AT + " INTEGER NOT NULL," + KEY_PHRASE_IN_REVISION
			+ " INTEGER NOT NULL" + ");";

	private static final String CREATE_TABLE_REVISION = "CREATE TABLE "
			+ TABLE_REVISION + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CREATED_AT
			+ " INTEGER NOT NULL," + KEY_REVISION_MISTAKES
			+ " INTEGER NOT NULL," + KEY_REVISION_PHRASE_ID
			+ " INTEGER NOT NULL," + "FOREIGN KEY(" + KEY_REVISION_PHRASE_ID
			+ ") REFERENCES " + TABLE_PHRASE + "(" + KEY_ID
			+ ") ON DELETE CASCADE ON UPDATE CASCADE" + ");";

	private static int DATABASE_VERSION = 1;

	private static DatabaseHelper instance;

	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context.getApplicationContext());
		}
		return instance;
	}

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		// XXX: debug!
		// clearDB();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_PHRASE);
		db.execSQL(CREATE_TABLE_REVISION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHRASE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVISION);

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
				DBUtils.getIntFromDateTime(phrase.getCreationDate()));

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
					pe.setCreationDate(DBUtils.getDateTimeFromInt(c.getInt(c
							.getColumnIndex(KEY_CREATED_AT))));

					phrases.add(pe);
				} while (c.moveToNext());
			}
		} finally {
			closeCursor(c);
			closeDB();
		}

		return phrases;
	}

	public int updatePhrase(PhraseEntry phrase) {

		ContentValues values = new ContentValues();
		values.put(KEY_PHRASE_LANG1, phrase.getLangA());
		values.put(KEY_PHRASE_LANG2, phrase.getLangB());
		values.put(KEY_PHRASE_LANG1_TEXT, phrase.getLangAText());
		values.put(KEY_PHRASE_LANG2_TEXT, phrase.getLangBText());
		values.put(KEY_PHRASE_LABEL, phrase.getLabel());
		// int inRevisions = phrase.isInRevisions() ? 1 : 0;
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
		values.put(KEY_REVISION_MISTAKES, revision.mistakes);
		values.put(KEY_REVISION_PHRASE_ID, phrase_id);
		values.put(KEY_CREATED_AT, DBUtils.getIntFromDateTime(revision.date));

		long revision_id = db.insert(TABLE_REVISION, null, values);
		revision.id = revision_id;

		return revision_id;
	}

	public int updateRevision(RevisionEntry revision) {

		ContentValues values = new ContentValues();
		values.put(KEY_REVISION_MISTAKES, revision.mistakes);

		int ret = 0;

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ret = db.update(TABLE_REVISION, values, KEY_ID + " = ?",
					new String[] { String.valueOf(revision.id) });
		} finally {
			closeDB();
		}

		return ret;
	}

	private List<RevisionEntry> getAllRevisions(long phrase_id) {
		List<RevisionEntry> revisions = new ArrayList<RevisionEntry>();
		String selectQuery = "SELECT  * FROM " + TABLE_REVISION + " WHERE "
				+ KEY_REVISION_PHRASE_ID + " = ?";

		Cursor c = null;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			c = db.query(TABLE_REVISION, null, KEY_REVISION_PHRASE_ID + "="
					+ phrase_id, null, null, null, KEY_CREATED_AT);

			if (c.moveToFirst()) {
				do {
					RevisionEntry re = new RevisionEntry();

					re.id = c.getInt(c.getColumnIndex(KEY_ID));
					re.mistakes = c.getInt(c
							.getColumnIndex(KEY_REVISION_MISTAKES));
					re.date = DBUtils.getDateTimeFromInt(c.getInt(c
							.getColumnIndex(KEY_CREATED_AT)));

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
			db.close();
		}
	}

	public void clearDB() {
		SQLiteDatabase db = this.getReadableDatabase();

		try {
			db.delete(TABLE_PHRASE, null, null);
			db.delete(TABLE_REVISION, null, null);
		} finally {
			closeDB();
		}
	}
}
