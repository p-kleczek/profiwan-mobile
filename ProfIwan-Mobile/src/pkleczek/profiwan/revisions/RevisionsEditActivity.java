package pkleczek.profiwan.revisions;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.keyboards.RussianKeyboard;
import pkleczek.profiwan.model.AndroidPhraseEntry;
import pkleczek.profiwan.model.PhraseEntry;
import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RevisionsEditActivity extends Activity {

	CustomKeyboard mCustomKeyboard;
	private PhraseEntry editedPhrase;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_revisions_edit);
		// Show the Up button in the action bar.
//		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mCustomKeyboard = new RussianKeyboard(this, R.id.revisions_kbd,
				R.xml.kbd_rus);

		mCustomKeyboard.registerEditText(R.id.revisions_edit_revisedLanguage);

		Keyboard mKeyboard = new Keyboard(this, R.xml.kbd_rus);
		KeyboardView mKeyboardView = (KeyboardView) findViewById(R.id.revisions_kbd);
		mKeyboardView.setKeyboard(mKeyboard);
		mKeyboardView.setPreviewEnabled(false);
		
		try {
			editedPhrase = getIntent().getParcelableExtra(RevisionsActivity.EDITED_PHRASE);
		} catch (ClassCastException e) {
			Log.e("REA", e.toString());
		}
		
		TextView enteredText = (TextView) findViewById(R.id.revisions_edit_knownLangText);
		enteredText.setText(editedPhrase.getLangAText());

		EditText etRevisedText = (EditText) findViewById(R.id.revisions_edit_revisedLanguage);
		etRevisedText.setText(editedPhrase.getLangBText());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_revisions_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			// This ID represents the Home or Up button. In the case of this
//			// activity, the Up button is shown. Use NavUtils to allow users
//			// to navigate up one level in the application structure. For
//			// more details, see the Navigation pattern on Android Design:
//			//
//			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
//			//
//			NavUtils.navigateUpFromSameTask(this);
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}

	public void acceptChanges(View view) {
		EditText etRevisedText = (EditText) findViewById(R.id.revisions_edit_revisedLanguage);
		String newText = etRevisedText.getText().toString();
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RevisionsActivity.EDITED_PHRASE, newText);
		setResult(Activity.RESULT_OK, resultIntent);
		
		this.finish();
	}
}
