package pkleczek.profiwan.revisions;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.keyboards.RussianKeyboard;
import pkleczek.profiwan.model.AndroidPhraseEntry;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.model.RevisionsSession;
import pkleczek.profiwan.utils.DatabaseHelper;
import pkleczek.profiwan.utils.DatabaseHelperImpl;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class RevisionsActivity extends Activity {

	CustomKeyboard mCustomKeyboard;
	PopupWindow popupWindow = null;

	public static final String EDITED_PHRASE = "pkleczek.profiwan.revisions.EDITED_PHRASE";
	public static final int EDIT_ACTIVITY = 1;

	private RevisionsSession revisionsSession;
	private Keyboard mKeyboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_revisions);
		// Show the Up button in the action bar.
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		mKeyboard = new Keyboard(this, R.xml.kbd_rus);
		
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
		// switch (item.getItemId()) {
		// case android.R.id.home:
		// // This ID represents the Home or Up button. In the case of this
		// // activity, the Up button is shown. Use NavUtils to allow users
		// // to navigate up one level in the application structure. For
		// // more details, see the Navigation pattern on Android Design:
		// //
		// //
		// http://developer.android.com/design/patterns/navigation.html#up-vs-back
		// //
		// NavUtils.navigateUpFromSameTask(this);
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	private void setupViewsForNextPhrase() {
		PhraseEntry phrase = revisionsSession.getCurrentPhrase();

		// TODO: set flags

		TextView tvKnownLanguage = (TextView) findViewById(R.id.dictionary_spin_knownLanguage);
		tvKnownLanguage.setText(phrase.getLangAText());

		mCustomKeyboard = new RussianKeyboard(this, R.id.revisions_kbd,
				R.xml.kbd_rus);

		mCustomKeyboard.registerEditText(R.id.revisions_edit_revisedLanguage);
		KeyboardView mKeyboardView = (KeyboardView) findViewById(R.id.revisions_kbd);
		mKeyboardView.setKeyboard(mKeyboard);
		mKeyboardView.setPreviewEnabled(false);

		EditText etRevisedLanguage = (EditText) findViewById(R.id.revisions_edit_revisedLanguage);
		etRevisedLanguage.setText("");
		etRevisedLanguage.requestFocus();
		etRevisedLanguage.performClick();
	}

	public void enterPhrase(View view) {
		EditText editText = (EditText) findViewById(R.id.revisions_edit_revisedLanguage);
		String enteredPhrase = editText.getText().toString();

		setContentView(R.layout.activity_revisions_entered);

		PhraseEntry phrase = revisionsSession.getCurrentPhrase();

		TextView tvKnownLanguage = (TextView) findViewById(R.id.dictionary_spin_knownLanguage);
		tvKnownLanguage.setText(phrase.getLangAText());

		TextView enteredText = (TextView) findViewById(R.id.revisions_entered_text_entered);
		boolean enteredCorrectly = revisionsSession
				.processTypedWord(enteredPhrase);

		enteredText.setText(enteredPhrase);

		if (!enteredCorrectly) {
			enteredText.setPaintFlags(enteredText.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			enteredText.setPaintFlags(enteredText.getPaintFlags()
					^ Paint.STRIKE_THRU_TEXT_FLAG);
		}

		Button btnAccept = (Button) findViewById(R.id.revisions_entered_btn_accept);

		TextView tvCorrect = (TextView) findViewById(R.id.revisions_entered_text_correct);
		if (enteredCorrectly) {
			tvCorrect.setText("Correct!");
			btnAccept.setEnabled(false);
		} else {
			tvCorrect.setText(phrase.getLangBText());
			btnAccept.setEnabled(true);
		}
		
		if (!revisionsSession.hasRevisions()) {
			showStats();
		}
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
