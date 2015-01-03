package pkleczek.profiwan.keyboards;

import pkleczek.profiwan.utils.lang.Language;
import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public abstract class CustomKeyboard {

	private static final String TAG = "CustomKeyboard";

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

		InputMethodManager inputManager = (InputMethodManager) mHostActivity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (inputManager != null) {
			if (mHostActivity == null)
				return;
			if (mHostActivity.getCurrentFocus() == null)
				return;
			if (mHostActivity.getCurrentFocus().getWindowToken() == null)
				return;
			inputManager.hideSoftInputFromWindow(mHostActivity
					.getCurrentFocus().getWindowToken(), 0);
		}

		if (v != null) {
			inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	/**
	 * Make the CustomKeyboard visible, and hide the system keyboard for view v.
	 */
	private void showSoftKeyboard(View v) {
		InputMethodManager inputManager = (InputMethodManager) mHostActivity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(v, 0);
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
		final EditText edittext = (EditText) mHostActivity.findViewById(resid);

		edittext.setTag(Boolean.TRUE);
		edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Boolean modeTag = (Boolean) v.getTag();
				if (modeTag != null && modeTag == Boolean.TRUE) {
					if (hasFocus) {
						showCustomKeyboard(v);
					} else {
						hideCustomKeyboard();
					}
				}
			}
		});

		edittext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean modeTag = (Boolean) v.getTag();
				if (modeTag != null && modeTag == Boolean.TRUE) {
					InputMethodManager inputManager = (InputMethodManager) mHostActivity
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					if (inputManager != null) {
						if (mHostActivity == null)
							return;
						if (mHostActivity.getCurrentFocus() == null)
							return;
						if (mHostActivity.getCurrentFocus().getWindowToken() == null)
							return;
						inputManager.hideSoftInputFromWindow(mHostActivity
								.getCurrentFocus().getWindowToken(), 0);
					} else {
						Log.i(TAG, "onClick() [null]");
					}

					showCustomKeyboard(v);
				}
			}
		});

		edittext.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				Boolean modeTag = (Boolean) view.getTag();
				if (modeTag != null && modeTag == Boolean.TRUE) {
					InputMethodManager inputManager = (InputMethodManager) mHostActivity
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					if (inputManager != null) {
						if (mHostActivity == null)
							return false;
						if (mHostActivity.getCurrentFocus() == null)
							return false;
						if (mHostActivity.getCurrentFocus().getWindowToken() == null)
							return false;
						inputManager.hideSoftInputFromWindow(mHostActivity
								.getCurrentFocus().getWindowToken(), 0);
					} else {
						Log.i(TAG, "onTouch() [null]");
					}

					setEditTextFocus(edittext, true);
				}

				return false;
			}
		});

		edittext.setInputType(edittext.getInputType()
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
	}

	public void setEditTextFocus(EditText editText, boolean isFocused) {
		editText.setCursorVisible(isFocused);
		editText.setFocusable(isFocused);
		editText.setFocusableInTouchMode(isFocused);

		if (isFocused) {
			editText.requestFocus();
		}
	}

	public void unregisterEditText(int resid) {
		final EditText edittext = (EditText) mHostActivity.findViewById(resid);

		edittext.setTag(Boolean.FALSE);
		// FIXME: normal listeners
		// edittext.setOnFocusChangeListener((OnFocusChangeListener)
		// edittext.getTag());
		// edittext.setOnClickListener(null);
		// edittext.setOnTouchListener(null);
	}

	public static CustomKeyboard changeKeyboard(Activity activity,
			CustomKeyboard customKeyboard, int editTextId, int kbdId,
			Language lang) {

		if (customKeyboard != null) {
			customKeyboard.unregisterEditText(editTextId);

			View editTextView = activity.findViewById(editTextId);

			if (customKeyboard.isCustomKeyboardVisible()) {
				customKeyboard.hideCustomKeyboard();
				customKeyboard.showSoftKeyboard(editTextView);
			}

			customKeyboard.showSoftKeyboard(editTextView);
			customKeyboard = null;
		}

		if (lang == Language.RU) {
			Keyboard keyboard = new Keyboard(activity, lang.getKeyboard());
			customKeyboard = new RussianKeyboard(activity, kbdId,
					lang.getKeyboard());

			customKeyboard.registerEditText(editTextId);

			KeyboardView mKeyboardView = (KeyboardView) activity
					.findViewById(kbdId);
			mKeyboardView.setKeyboard(keyboard);
			mKeyboardView.setPreviewEnabled(false);
		}
		
		// TODO: what with other languages?

		return customKeyboard;
	}
}
