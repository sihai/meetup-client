/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 
 * @author sihai
 *
 */
public class SoftInput {

	public static InputMethodManager getInputMethodManager(Context context) {
		return (InputMethodManager) context.getSystemService("input_method");
	}

	public static void hide(View view) {
		InputMethodManager inputmethodmanager = getInputMethodManager(view.getContext());
		if (inputmethodmanager != null)
			inputmethodmanager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void show(View view) {
		InputMethodManager inputmethodmanager = getInputMethodManager(view.getContext());
		if (inputmethodmanager != null)
			inputmethodmanager.showSoftInput(view, 0);
	}
}
