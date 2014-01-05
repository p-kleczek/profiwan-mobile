package pkleczek.profiwan.keyboards;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public final class RussianKeyboard extends CustomKeyboard {

	/** The key (code) handler. */
	private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

		public final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
		public final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL
		public final static int CodePrev = 55000;
		public final static int CodeAllLeft = 55001;
		public final static int CodeLeft = 55002;
		public final static int CodeRight = 55003;
		public final static int CodeAllRight = 55004;
		public final static int CodeNext = 55005;
		public final static int CodeClear = 55006;

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
			} else if (primaryCode == CodeDelete) {
				if (editable != null && start > 0)
					editable.delete(start - 1, start);
			} else if (primaryCode == CodeClear) {
				if (editable != null)
					editable.clear();
			} else if (primaryCode == CodeLeft) {
				if (start > 0)
					edittext.setSelection(start - 1);
			} else if (primaryCode == CodeRight) {
				if (start < edittext.length())
					edittext.setSelection(start + 1);
			} else if (primaryCode == CodeAllLeft) {
				edittext.setSelection(0);
			} else if (primaryCode == CodeAllRight) {
				edittext.setSelection(edittext.length());
			} else if (primaryCode == CodePrev) {
				View focusNew = edittext.focusSearch(View.FOCUS_BACKWARD);
				if (focusNew != null)
					focusNew.requestFocus();
			} else if (primaryCode == CodeNext) {
				View focusNew = edittext.focusSearch(View.FOCUS_FORWARD);
				if (focusNew != null)
					focusNew.requestFocus();
			} else { // insert character
				editable.insert(start, Character.toString((char) primaryCode));
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
