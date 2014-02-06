package pkleczek.profiwan.keyboards;

import android.app.Activity;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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

		// edittext.setOnKeyListener(null);

		edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.i(TAG, "onFocusChange() -> " + hasFocus);

				if (hasFocus) {
					showCustomKeyboard(v);
				} else {
					hideCustomKeyboard();
				}
			}
		});

		edittext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "onClick()");

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

					Log.i(TAG, "onClick() [hide]");
				} else {
					Log.i(TAG, "onClick() [null]");
				}

				showCustomKeyboard(v);
			}
		});

		edittext.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

					Log.i(TAG, "onTouch() [hide]");
				} else {
					Log.i(TAG, "onTouch() [null]");
				}

				setEditTextFocus(edittext, true);

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

		edittext.setOnFocusChangeListener(null);
		edittext.setOnClickListener(null);
		edittext.setOnTouchListener(null);
		edittext.setInputType(edittext.getInputType()
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		
//		InputMethodManager inputManager = (InputMethodManager) mHostActivity
//				.getSystemService(Activity.INPUT_METHOD_SERVICE);
//		if (inputManager != null) {
//			if (mHostActivity == null)
//				return;
//			if (mHostActivity.getCurrentFocus() == null)
//				return;
//			if (mHostActivity.getCurrentFocus().getWindowToken() == null)
//				return;
//			inputManager.hideSoftInputFromWindow(mHostActivity
//					.getCurrentFocus().getWindowToken(), 0);
//		}
//
//		if (v != null) {
//			inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
//		}
//		
//		mHostActivity.getWindow().setSoftInputMode(
//				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
}
