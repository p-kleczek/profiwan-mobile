package pkleczek.profiwan.keyboards;

import android.app.Activity;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public abstract class CustomKeyboard {

	/** A link to the KeyboardView that is used to render this CustomKeyboard. */
	protected KeyboardView mKeyboardView;
	/** A link to the activity that hosts the {@link #mKeyboardView}. */
	protected Activity mHostActivity;

	/** Returns whether the CustomKeyboard is visible. */
	public boolean isCustomKeyboardVisible() {
		return mKeyboardView.getVisibility() == View.VISIBLE;
	}

	/**
	 * Make the CustomKeyboard visible, and hide the system keyboard for view v.
	 */
	public void showCustomKeyboard(View v) {
		mKeyboardView.setVisibility(View.VISIBLE);
		mKeyboardView.setEnabled(true);
		if (v != null) {
			((InputMethodManager) mHostActivity
					.getSystemService(Activity.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	/** Make the CustomKeyboard invisible. */
	public void hideCustomKeyboard() {
		mKeyboardView.setVisibility(View.GONE);
		mKeyboardView.setEnabled(false);
	}

	/**
	 * Register <var>EditText<var> with resource id <var>resid</var> (on the
	 * hosting activity) for using this custom keyboard.
	 * 
	 * @param resid
	 *            The resource id of the EditText that registers to the custom
	 *            keyboard.
	 */
	public void registerEditText(int resid) {
		// Find the EditText 'resid'
		EditText edittext = (EditText) mHostActivity.findViewById(resid);
		// Make the custom keyboard appear
		edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
			// NOTE By setting the on focus listener, we can show the custom
			// keyboard when the edit box gets focus, but also hide it when the
			// edit box loses focus
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showCustomKeyboard(v);
				} else {
					hideCustomKeyboard();
				}
			}
		});
		edittext.setOnClickListener(new OnClickListener() {
			// NOTE By setting the on click listener, we can show the custom
			// keyboard again, by tapping on an edit box that already had focus
			// (but that had the keyboard hidden).
			@Override
			public void onClick(View v) {
				showCustomKeyboard(v);
			}
		});
		// Disable standard keyboard hard way
		// NOTE There is also an easy way:
		// 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a
		// cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
		edittext.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				EditText edittext = (EditText) v;
				int inType = edittext.getInputType(); // Backup the input type
				// edittext.setInputType(InputType.TYPE_NULL); // Disable
				// standard
				// keyboard

				if (v instanceof AutoCompleteTextView) {
					edittext.setInputType(InputType.TYPE_NULL); // Disable
					// standard keyboard
				}

				edittext.onTouchEvent(event); // Call native handler
				edittext.setInputType(inType); // Restore input type
				return true; // Consume touch event
			}
		});
		// Disable spell check (hex strings look like words to Android)
		edittext.setInputType(edittext.getInputType()
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
	}
}
