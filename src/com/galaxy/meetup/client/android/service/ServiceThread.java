// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.android.service;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public final class ServiceThread extends Thread {

	public static interface IntentProcessor {

		public abstract void onServiceThreadEnd();

		public abstract void processIntent(Intent intent);
	}

	public ServiceThread(Handler handler, String s,
			IntentProcessor intentprocessor) {
		mMainHandler = handler;
		setName((new StringBuilder()).append(s).append(this).toString());
		mIntentProcessor = intentprocessor;
	}

	public final void put(Intent intent) {
		mQueue.add(intent);
		if (mThreadHandler != null)
			mThreadHandler.post(mProcessQueueRunnable);
	}

	public final void quit() {
		if (mThreadHandler != null)
			mThreadHandler.getLooper().quit();
		if (mQueue.size() > 0)
			mQueue.clear();
	}

	public final void run() {
		Looper.prepare();
		mThreadHandler = new Handler();
		mMainHandler.post(new Runnable() {

			public final void run() {
				mThreadHandler.post(mProcessQueueRunnable);
			}

		});
		Looper.loop();
		if (mIntentProcessor != null)
			mIntentProcessor.onServiceThreadEnd();
	}

	private IntentProcessor mIntentProcessor;
	private final Handler mMainHandler;
	private final Runnable mProcessQueueRunnable = new Runnable() {

		public final void run() {
			do {
				Intent intent = (Intent) mQueue.poll();
				if (intent == null)
					break;
				try {
					if (mIntentProcessor != null)
						mIntentProcessor.processIntent(intent);
				} catch (Throwable throwable) {
					Thread.getDefaultUncaughtExceptionHandler()
							.uncaughtException(Thread.currentThread(),
									throwable);
				}
			} while (true);
		}
	};
	private final Queue mQueue = new LinkedBlockingQueue();
	private Handler mThreadHandler;

}
