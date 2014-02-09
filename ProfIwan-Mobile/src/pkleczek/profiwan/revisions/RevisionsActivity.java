package pkleczek.profiwan.revisions;

import pkleczek.profiwan.R;
import pkleczek.profiwan.RevisionsEnteredActivity;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.model.AndroidPhraseEntry;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.model.RevisionsSession;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.Language;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RevisionsActivity extends Activity {

	private CustomKeyboard kbd;

	public static final String REVISED_PHRASE_EXTRA = "pkleczek.profiwan.revisions.REVISED_PHRASE_EXTRA";
	public static final String CORRECT_WORDS_EXTRA = "pkleczek.profiwan.revisions.CORRECT_WORDS_EXTRA";
	public static final String TOTAL_WORDS_EXTRA = "pkleczek.profiwan.revisions.TOTAL_WORDS_EXTRA";
	public static final String ENTERED_PHRASE_EXTRA = "pkleczek.profiwan.revisions.ENTERED_PHRASE_EXTRA";
	public static final String WAS_CORRECT_EXTRA = "pkleczek.profiwan.revisions.WAS_CORRECT_EXTRA";

	public static final int ENTERED_ACTIVITY_REQ = 1;

	private RevisionsSession revisionsSession;

	private EditText revisedEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_revisions);

		revisionsSession = new RevisionsSession(
				DatabaseHelperImpl.getInstance(this));
		setupViewsForNextPhrase();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_revisions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private void setupViewsForNextPhrase() {
		PhraseEntry phrase = revisionsSession.getCurrentPhrase();
		Language langA = Language.getLanguageByCode(phrase.getLangA());
		Language langB = Language.getLanguageByCode(phrase.getLangB());

		revisedEditText = (EditText) findViewById(R.id.revisions_edit_revisedLanguage);

		String title = getResources().getString(
				R.string.title_activity_revisions);
		setTitle(String.format(title, revisionsSession.getCorrectWordsNumber(),
				revisionsSession.getWordsNumber()));

		ImageView imvA = (ImageView) findViewById(R.id.revisions_imgview_knownLanguageFlag);
		imvA.setImageResource(langA.getFlagIconId());
		ImageView imvB = (ImageView) findViewById(R.id.revisions_imgview_revisedLanguageFlag);
		imvB.setImageResource(langB.getFlagIconId());

		TextView tvKnownLanguage = (TextView) findViewById(R.id.revisions_knownLangText);
		tvKnownLanguage.setText(phrase.getLangAText());

		kbd = CustomKeyboard.changeKeyboard(this, kbd,
				R.id.revisions_edit_revisedLanguage, R.id.revisions_kbd, langB);

		revisedEditText.setInputType(revisedEditText.getInputType()
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		revisedEditText.setText("");
		revisedEditText.clearFocus();
		revisedEditText.requestFocus();
		revisedEditText.performClick();
	}

	public void enterPhrase(View view) {

		Intent intent = new Intent(this, RevisionsEnteredActivity.class);
		PhraseEntry phrase = revisionsSession.getCurrentPhrase();
		AndroidPhraseEntry pe = new AndroidPhraseEntry(phrase);
		String enteredPhrase = revisedEditText.getText().toString();
		boolean enteredCorrectly = revisionsSession
				.processTypedWord(enteredPhrase);

		intent.putExtra(REVISED_PHRASE_EXTRA, pe);
		intent.putExtra(CORRECT_WORDS_EXTRA,
				revisionsSession.getCorrectWordsNumber());
		intent.putExtra(TOTAL_WORDS_EXTRA, revisionsSession.getWordsNumber());
		intent.putExtra(ENTERED_PHRASE_EXTRA, enteredPhrase);
		intent.putExtra(WAS_CORRECT_EXTRA, enteredCorrectly);

		startActivityForResult(intent, ENTERED_ACTIVITY_REQ);

		if (!revisionsSession.hasRevisions()) {
			showStats();
		}
	}

	private void tryNextPhrase() {
		if (!revisionsSession.hasRevisions()) {
			showStats();
		} else {
			revisionsSession.nextRevision();
			setupViewsForNextPhrase();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case (ENTERED_ACTIVITY_REQ): {
			if (resultCode == RevisionsEnteredActivity.RESULT_ACCEPTED) {
				revisionsSession.acceptRevision();
			} else if (resultCode == RevisionsEnteredActivity.RESULT_NEXT) {
				// TODO ...
			}

			if (resultCode == RevisionsEnteredActivity.RESULT_ACCEPTED
					|| resultCode == RevisionsEnteredActivity.RESULT_NEXT) {
				String newText = data
						.getStringExtra(RevisionsEnteredActivity.CORRECTED_PHRASE_EXTRA);
				revisionsSession.getCurrentPhrase().setLangBText(newText);

				tryNextPhrase();
			}

			break;
		}
		}
	}

	private void showStats() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Stats");

		String message = String
				.format("words                = %4d\ncorrect words    = %4d\ntotal revisions   = %4d",
						revisionsSession.getWordsNumber(),
						revisionsSession.getCorrectWordsNumber(),
						revisionsSession.getRevisionsNumber());

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						RevisionsActivity.this.finish();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

}
