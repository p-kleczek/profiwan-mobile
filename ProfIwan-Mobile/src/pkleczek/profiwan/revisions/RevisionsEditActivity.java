package pkleczek.profiwan.revisions;

import pkleczek.profiwan.R;
import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.model.PhraseEntry;
import pkleczek.profiwan.utils.Language;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RevisionsEditActivity extends Activity {

	private CustomKeyboard kbd;
	private PhraseEntry editedPhrase;
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_revisions_edit);

		try {
			editedPhrase = getIntent().getParcelableExtra(
					RevisionsEnteredActivity.EDITED_PHRASE);
		} catch (ClassCastException e) {
			Log.e("REA", e.toString());
		}

		Language langA = Language.getLanguageByCode(editedPhrase.getLangA());
		Language langB = Language.getLanguageByCode(editedPhrase.getLangB());

		ImageView imvA = (ImageView) findViewById(R.id.revisions_imgview_knownLanguageFlag);
		imvA.setImageResource(langA.getFlagIconId());
		ImageView imvB = (ImageView) findViewById(R.id.revisions_imgview_revisedLanguageFlag);
		imvB.setImageResource(langB.getFlagIconId());

		kbd = CustomKeyboard.changeKeyboard(this, kbd,
				R.id.revisions_edit_edit_revisedLanguage, R.id.revisions_kbd,
				langB);

		TextView enteredText = (TextView) findViewById(R.id.revisions_edit_knownLangText);
		enteredText.setText(editedPhrase.getLangAText());

		editText = (EditText) findViewById(R.id.revisions_edit_edit_revisedLanguage);
		editText.setText(editedPhrase.getLangBText());
	}

	public void acceptChanges(View view) {
		String newText = editText.getText().toString();

		Intent resultIntent = new Intent();
		resultIntent.putExtra(RevisionsEnteredActivity.EDITED_PHRASE, newText);
		setResult(Activity.RESULT_OK, resultIntent);

		finish();
	}
}
