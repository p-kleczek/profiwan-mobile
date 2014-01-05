package pkleczek.profiwan;

import pkleczek.profiwan.keyboards.CustomKeyboard;
import pkleczek.profiwan.keyboards.RussianKeyboard;
import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class RevisionsActivity extends Activity {

	CustomKeyboard mCustomKeyboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_revisions);
		// Show the Up button in the action bar.
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		mCustomKeyboard = new RussianKeyboard(this, R.id.keyboardview,
				R.xml.kbd_rus);

		mCustomKeyboard.registerEditText(R.id.editText1);

		Keyboard mKeyboard = new Keyboard(this, R.xml.kbd_rus);

		// Lookup the KeyboardView
		KeyboardView mKeyboardView = (KeyboardView) findViewById(R.id.keyboardview);
		// Attach the keyboard to the view
		mKeyboardView.setKeyboard(mKeyboard);
		// Do not show the preview balloons
		mKeyboardView.setPreviewEnabled(false);

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

}
