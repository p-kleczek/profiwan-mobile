package pkleczek.profiwan.revisions;

import pkleczek.profiwan.R;
import pkleczek.profiwan.R.id;
import pkleczek.profiwan.R.layout;
import pkleczek.profiwan.R.menu;
import pkleczek.profiwan.R.string;
import pkleczek.profiwan.model.AndroidPhraseEntry;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.Language;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RevisionsEnteredActivity extends Activity {

	public static final String EDITED_PHRASE = "pkleczek.profiwan.revisions.EDITED_PHRASE";
	public static final int EDIT_ACTIVITY = 1;

	/**
	 * The new langB text.
	 */
	public static final String CORRECTED_PHRASE_EXTRA = "pkleczek.profiwan.revisions.CORRECTED_PHRASE_EXTRA";

	public static final int RESULT_NEXT = 0;
	public static final int RESULT_ACCEPTED = 1;

	private PhraseEntry phrase;
	private Boolean wasCorrect = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_revisions_entered);

		int correctWords = -1;
		int totalWords = -1;
		String enteredPhrase = "";

		try {
			phrase = getIntent().getParcelableExtra(
					RevisionsActivity.REVISED_PHRASE_EXTRA);

			correctWords = getIntent().getIntExtra(
					RevisionsActivity.CORRECT_WORDS_EXTRA, -1);
			totalWords = getIntent().getIntExtra(
					RevisionsActivity.TOTAL_WORDS_EXTRA, -1);
			enteredPhrase = getIntent().getStringExtra(
					RevisionsActivity.ENTERED_PHRASE_EXTRA);
			wasCorrect = getIntent().getBooleanExtra(
					RevisionsActivity.WAS_CORRECT_EXTRA, false);

		} catch (ClassCastException e) {
			Log.e("REA", e.toString());
		}

		Language langA = Language.getLanguageByCode(phrase.getLangA());
		Language langB = Language.getLanguageByCode(phrase.getLangB());

		String title = getResources().getString(
				R.string.title_activity_revisions);
		setTitle(String.format(title, correctWords, totalWords));

		ImageView imvA = (ImageView) findViewById(R.id.revisions_imgview_knownLanguageFlag);
		imvA.setImageResource(langA.getFlagIconId());
		ImageView imvB = (ImageView) findViewById(R.id.revisions_imgview_revisedLanguageFlag);
		imvB.setImageResource(langB.getFlagIconId());

		TextView tvKnownLanguage = (TextView) findViewById(R.id.revisions_knownLangText);
		tvKnownLanguage.setText(phrase.getLangAText());

		TextView enteredText = (TextView) findViewById(R.id.revisions_entered_text_entered);
		enteredText.setText(enteredPhrase);

		Button btnAccept = (Button) findViewById(R.id.revisions_entered_btn_accept);
		TextView tvCorrect = (TextView) findViewById(R.id.revisions_entered_text_correct);

		if (wasCorrect) {
			enteredText.setPaintFlags(enteredText.getPaintFlags()
					& ~(Paint.STRIKE_THRU_TEXT_FLAG));

			tvCorrect.setText("Correct!");
			btnAccept.setEnabled(false);
		} else {
			enteredText.setPaintFlags(enteredText.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);

			tvCorrect.setText(phrase.getLangBText());
			btnAccept.setEnabled(true);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_revisions_entered, menu);
		return true;
	}

	public void editPhrase(View view) {
		Intent intent = new Intent(this, RevisionsEditActivity.class);
		AndroidPhraseEntry pe = new AndroidPhraseEntry(phrase);
		intent.putExtra(EDITED_PHRASE, pe);
		startActivityForResult(intent, EDIT_ACTIVITY);
	}

	public void acceptPhrase(View view) {
		setResult(RESULT_ACCEPTED, prepareIntent());
		finish();
	}

	public void nextPhrase(View view) {
		setResult(RESULT_NEXT, prepareIntent());
		finish();
	}

	private Intent prepareIntent() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RevisionsEnteredActivity.CORRECTED_PHRASE_EXTRA,
				phrase.getLangBText());
		return resultIntent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case (EDIT_ACTIVITY): {
			if (resultCode == Activity.RESULT_OK) {

				phrase.setLangBText(data.getStringExtra(EDITED_PHRASE));

				TextView enteredText = (TextView) findViewById(R.id.revisions_entered_text_entered);
				if (wasCorrect) {
					enteredText.setText(phrase.getLangBText());
				} else {
					TextView correctText = (TextView) findViewById(R.id.revisions_entered_text_correct);
					correctText.setText(phrase.getLangBText());
				}

				DatabaseHelper dbHelper = DatabaseHelperImpl.getInstance(this);
				dbHelper.updatePhrase(phrase);
			}
			break;
		}
		}
	}
}
