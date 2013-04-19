// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.android.content;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

public class EsCursorLoader extends CursorLoader {

	private boolean mLoaderException;
	private final Uri mNotificationUri;
	private final ForceLoadContentObserver mObserver;
	private boolean mObserverRegistered;

	public EsCursorLoader(Context context) {
		this(context, null);
	}

	public EsCursorLoader(Context context, Uri uri) {
		super(context);
		mObserver = new ForceLoadContentObserver();
		mNotificationUri = uri;
	}

	public EsCursorLoader(Context context, Uri uri, String as[], String s,
			String as1[], String s1) {
		this(context, uri, as, s, as1, s1, null);
	}

	public EsCursorLoader(Context context, Uri uri, String as[], String s,
			String as1[], String s1, Uri uri1) {
		super(context, uri, as, s, as1, s1);
		mObserver = new ForceLoadContentObserver();
		mNotificationUri = uri1;
	}

	public final void deliverResult(Cursor cursor) {
		boolean flag;
		if (cursor != null && cursor.isClosed())
			flag = true;
		else
			flag = false;

		if (mLoaderException || flag) {
			if (flag)
				Log.w("EsCursorLoader",
						"Cursor was delivered closed.  This should never happen");
			else
				super.deliverResult(cursor);
		}
	}

	@Override
	public Cursor loadInBackground() {

		Cursor cursor = null;
		try {
			cursor = super.loadInBackground();
		} catch (Exception e) {
			mLoaderException = true;
			Log.w("EsCursorLoader", "loadInBackground failed", e);
		}
		return cursor;
	}

	protected void onAbandon() {
		if (mObserverRegistered) {
			getContext().getContentResolver().unregisterContentObserver(
					mObserver);
			mObserverRegistered = false;
		}
	}

	protected void onReset() {
		cancelLoad();
		super.onReset();
		onAbandon();
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if (!mObserverRegistered && mNotificationUri != null) {
			getContext().getContentResolver().registerContentObserver(
					mNotificationUri, false, mObserver);
			mObserverRegistered = true;
		}
	}

	@Override
	protected final void onStopLoading() {
	}

}
