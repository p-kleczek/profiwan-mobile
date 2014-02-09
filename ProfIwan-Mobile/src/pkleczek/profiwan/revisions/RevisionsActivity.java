package pkleczek.profiwan.revisions;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.model.AndroidPhraseEntry;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.model.RevisionsSession;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import pkleczek.profiwan.utils.Language;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class RevisionsActivity extends Activity {

	private CustomKeyboard kbd;
	PopupWindow popupWindow = null;

	public static final String EDITED_PHRASE = "pkleczek.profiwan.revisions.EDITED_PHRASE";
	public static final int EDIT_ACTIVITY = 1;

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

		setupUniversalElements();
		enableKeyboard();

		kbd = CustomKeyboard.changeKeyboard(this, kbd,
				R.id.revisions_edit_revisedLanguage, R.id.revisions_kbd, langB);

		revisedEditText.setInputType(revisedEditText.getInputType()
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		revisedEditText.setText("");
		revisedEditText.requestFocus();
		revisedEditText.performClick();
	}

	private void enableKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(revisedEditText, InputMethodManager.SHOW_IMPLICIT);
	}

	private void setupUniversalElements() {
		PhraseEntry phrase = revisionsSession.getCurrentPhrase();
		Language langA = Language.getLanguageByCode(phrase.getLangA());
		Language langB = Language.getLanguageByCode(phrase.getLangB());

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
	}

	public void enterPhrase(View view) {
		String enteredPhrase = revisedEditText.getText().toString();

		setContentView(R.layout.activity_revisions_entered);

		setupUniversalElements();
		hideKeyboard();

		PhraseEntry phrase = revisionsSession.getCurrentPhrase();

		TextView enteredText = (TextView) findViewById(R.id.revisions_entered_text_entered);
		boolean enteredCorrectly = revisionsSession
				.processTypedWord(enteredPhrase);

		enteredText.setText(enteredPhrase);

		Button btnAccept = (Button) findViewById(R.id.revisions_entered_btn_accept);
		TextView tvCorrect = (TextView) findViewById(R.id.revisions_entered_text_correct);

		if (enteredCorrectly) {
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

		if (!revisionsSession.hasRevisions()) {
			showStats();
		}
	}

	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(revisedEditText.getWindowToken(),
				0);
	}

	public void editPhrase(View view) {
		Intent intent = new Intent(this, RevisionsEditActivity.class);
		AndroidPhraseEntry pe = new AndroidPhraseEntry(
				revisionsSession.getCurrentPhrase());
		intent.putExtra(EDITED_PHRASE, pe);
		startActivityForResult(intent, EDIT_ACTIVITY);
	}

	public void nextPhrase(View view) {
		tryNextPhrase();
	}

	public void acceptPhrase(View view) {
		revisionsSession.acceptRevision();
		tryNextPhrase();
	}

	private void tryNextPhrase() {
		if (!revisionsSession.hasRevisions()) {
			showStats();
		} else {
			revisionsSession.nextRevision();
			setContentView(R.layout.activity_revisions);
			setupViewsForNextPhrase();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case (EDIT_ACTIVITY): {
			if (resultCode == Activity.RESULT_OK) {
				String newText = data.getStringExtra(EDITED_PHRASE);

				revisionsSession.getCurrentPhrase().setLangBText(newText);

				TextView enteredText = (TextView) findViewById(R.id.revisions_entered_text_entered);
				if (revisionsSession.isEnteredCorrectly(enteredText.getText())) {
					enteredText.setText(newText);
				} else {
					TextView correctText = (TextView) findViewById(R.id.revisions_entered_text_correct);
					correctText.setText(newText);
				}

				DatabaseHelper dbHelper = DatabaseHelperImpl.getInstance(this);
				dbHelper.updatePhrase(revisionsSession.getCurrentPhrase());
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
