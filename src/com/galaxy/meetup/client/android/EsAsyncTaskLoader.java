/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * 
 * @author sihai
 * 
 */
public abstract class EsAsyncTaskLoader extends AsyncTaskLoader {

	private boolean mLoaderException;

	public EsAsyncTaskLoader(Context context) {
		super(context);
	}

	public void deliverResult(Object obj) {
		if (!mLoaderException)
			super.deliverResult(obj);
	}

	public abstract Object esLoadInBackground();

	public final Object loadInBackground() {
		if (mLoaderException) {
			return null;
		}

		try {
			return esLoadInBackground();
		} catch (Exception e) {
			Log.w("EsAsyncTaskLoader", "loadInBackground failed", e);
			mLoaderException = true;
		}
		return null;
	}

}
