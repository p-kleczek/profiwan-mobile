package pkleczek.profiwan.keyboards;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public final class RussianKeyboard extends CustomKeyboard {

	/** The key (code) handler. */
	private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

		public final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			// NOTE We can say '<Key android:codes="49,50" ... >' in the xml
			// file; all codes come in keyCodes, the first in this list in
			// primaryCode
			// Get the EditText and its Editable
			View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
			if (focusCurrent == null
					|| focusCurrent.getClass() != EditText.class)
				return;
			EditText edittext = (EditText) focusCurrent;
			Editable editable = edittext.getText();
			int start = edittext.getSelectionStart();
			// Apply the key to the edittext
			if (primaryCode == CodeCancel) {
				hideCustomKeyboard();
			} else if (primaryCode == Keyboard.KEYCODE_DELETE) {
				if (editable != null && start > 0)
					editable.delete(start - 1, start);
			} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
				mKeyboardView.setShifted(!(mKeyboardView.isShifted()));
			} else { // insert character
				char c = (char) primaryCode;
				if (Character.isLetter(c)) {
					c = mKeyboardView.isShifted() ? Character.toUpperCase(c)
							: Character.toLowerCase(c);
				}

				editable.insert(start, Character.toString(c));
			}
		}

		@Override
		public void onPress(int arg0) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeUp() {
		}
	};

	/**
	 * Create a custom keyboard, that uses the KeyboardView (with resource id
	 * <var>viewid</var>) of the <var>host</var> activity, and load the keyboard
	 * layout from xml file <var>layoutid</var> (see {@link Keyboard} for
	 * description). Note that the <var>host</var> activity must have a
	 * <var>KeyboardView</var> in its layout (typically aligned with the bottom
	 * of the activity). Note that the keyboard layout xml file may include key
	 * codes for navigation; see the constants in this class for their values.
	 * Note that to enable EditText's to use this custom keyboard, call the
	 * {@link #registerEditText(int)}.
	 * 
	 * @param host
	 *            The hosting activity.
	 * @param viewid
	 *            The id of the KeyboardView.
	 * @param layoutid
	 *            The id of the xml file containing the keyboard layout.
	 */
	public RussianKeyboard(Activity host, int viewid, int layoutid) {
		mHostActivity = host;
		mKeyboardView = (KeyboardView) mHostActivity.findViewById(viewid);
		mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutid));
		mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview
												// balloons
		mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
		// Hide the standard keyboard initially
		mHostActivity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

}

// NOTE How can we change the background color of some keys (like the
// shift/ctrl/alt)?
// NOTE What does android:keyEdgeFlags do/mean
