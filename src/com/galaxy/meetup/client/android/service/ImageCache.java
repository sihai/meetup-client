/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.galaxy.meetup.client.android.EsApplication;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AvatarImageRequest;
import com.galaxy.meetup.client.android.content.AvatarRequest;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.MediaImageRequest;
import com.galaxy.meetup.client.android.content.cache.CachedImageRequest;
import com.galaxy.meetup.client.android.content.cache.EsMediaCache;
import com.galaxy.meetup.client.android.content.cache.ImageRequest;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.GifDrawable;
import com.galaxy.meetup.client.util.ImageLoadingMetrics;
import com.galaxy.meetup.client.util.ImageUtils;

public class ImageCache implements Callback {

	private static final byte EMPTY_ARRAY[] = new byte[0];
	private static final Object FAILED_IMAGE = new Object();
	private static Set<OnAvatarChangeListener> mAvatarListeners = new HashSet<OnAvatarChangeListener>();
	private static Set<OnMediaImageChangeListener> mMediaImageListeners = new HashSet<OnMediaImageChangeListener>();
	private static Set mRequestCompleteListeners = new HashSet();
	private static ImageCache sInstance;
	private static int sMediumAvatarEstimatedSize;
	private static int sSmallAvatarEstimatedSize;
	private static int sTinyAvatarEstimatedSize;
	private int mBackgroundThreadBitmapCount;
	private final Context mContext;
	private final LruCache mImageCache;
	private final LruCache<ImageRequest, ImageHolder> mImageHolderCache;
	private final int mImageHolderCacheRedZoneBytes;
	private final ArrayList mImageHolderQueue = new ArrayList();
	private LoaderThread mLoaderThread;
	private boolean mLoadingRequested;
	private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper(), this);
	private boolean mPaused;
	private final ConcurrentHashMap<ImageConsumer, ImageRequest> mPendingRequests = new ConcurrentHashMap<ImageConsumer, ImageRequest>();

	// ===========================================================================
	// Constructor
	// ===========================================================================
	private ImageCache(Context context) {
		mContext = context;
		Resources resources = context.getApplicationContext().getResources();
		int i;
		int j;
		int k;
		if (EsApplication.sMemoryClass >= 48)
			i = Math.max(resources.getInteger(R.integer.config_image_cache_max_bytes_decoded_large),
					1024 * (1024 * (EsApplication.sMemoryClass / 4)));
		else
			i = resources.getInteger(R.integer.config_image_cache_max_bytes_decoded_small);
		mImageCache = new LruCache(i) {

			protected final int sizeOf(Object obj, Object obj1) {
				int l;
				if (obj1 instanceof Bitmap) {
					Bitmap bitmap = (Bitmap) obj1;
					l = bitmap.getRowBytes() * bitmap.getHeight();
				} else {
					l = 0;
				}
				return l;
			}
		};

		if (EsApplication.sMemoryClass >= 48)
			j = 1024 * (1024 * (EsApplication.sMemoryClass / 4));
		else
			j = 0;
		k = Math.max(j, resources.getInteger(R.integer.config_image_cache_max_bytes_encoded));
		mImageHolderCache = new LruCache<ImageRequest, ImageHolder>(k) {

			protected final int sizeOf(ImageRequest key, ImageHolder value) {
				ImageHolder imageholder = (ImageHolder) value;
				int l;
				if (imageholder.bytes != null)
					l = imageholder.bytes.length;
				else
					l = 0;
				return l;
			}
		};

		mImageHolderCacheRedZoneBytes = (int) (0.90000000000000002D * (double) k);
		if (sTinyAvatarEstimatedSize == 0) {
			sTinyAvatarEstimatedSize = (int) (0.3F * (float) EsAvatarData.getTinyAvatarSize(context) * (float) EsAvatarData
					.getTinyAvatarSize(context));
			sSmallAvatarEstimatedSize = (int) (0.3F * (float) EsAvatarData.getSmallAvatarSize(context) * (float) EsAvatarData
					.getSmallAvatarSize(context));
			sMediumAvatarEstimatedSize = (int) (0.3F * (float) EsAvatarData.getMediumAvatarSize(context) * (float) EsAvatarData
					.getMediumAvatarSize(context));
		}
	}

	@Override
	public boolean handleMessage(Message message) {
		boolean result = false;
		switch (message.what) {
			case 1:
				mLoadingRequested = false;
				if (!mPaused) {
					ensureLoaderThread();
					mLoaderThread.requestLoading();
				}
				result = true;
				break;
			case 2:
				if (!mPaused) {
					Iterator iterator3 = mPendingRequests.keySet().iterator();
					do {
						if (!iterator3.hasNext())
							break;
						ImageConsumer imageconsumer = (ImageConsumer) iterator3.next();
						ImageRequest imagerequest1 = (ImageRequest) mPendingRequests.get(imageconsumer);
						if (imagerequest1 != null && loadCachedImage(imageconsumer, imagerequest1, false))
							iterator3.remove();
					} while (true);
					clearTemporaryImageReferences();
					mBackgroundThreadBitmapCount = 0;
					if (!mPendingRequests.isEmpty())
						requestLoading();
				}
				result = true;
				break;
			case 3:
				String s = (String) message.obj;
				evictImage(new AvatarRequest(s, 0));
				evictImage(new AvatarRequest(s, 0, true));
				evictImage(new AvatarRequest(s, 1));
				evictImage(new AvatarRequest(s, 1, true));
				evictImage(new AvatarRequest(s, 2));
				evictImage(new AvatarRequest(s, 2, true));
				for (Iterator iterator2 = mAvatarListeners.iterator(); iterator2.hasNext(); ((OnAvatarChangeListener) iterator2
						.next()).onAvatarChanged(s))
					;
				result = true;
				break;
			case 4:
				MediaImageChangeNotification mediaimagechangenotification = (MediaImageChangeNotification) message.obj;
				Iterator iterator = mImageHolderCache.snapshot().keySet().iterator();
				do {
					if (!iterator.hasNext())
						break;
					ImageRequest imagerequest = (ImageRequest) iterator.next();
					if (!imagerequest.equals(mediaimagechangenotification.request)
							&& (imagerequest instanceof MediaImageRequest)
							&& MediaImageRequest.areCanonicallyEqual((MediaImageRequest) imagerequest,
									mediaimagechangenotification.request))
						evictImage(imagerequest);
				} while (true);
				for (Iterator iterator1 = mMediaImageListeners.iterator(); iterator1.hasNext(); ((OnMediaImageChangeListener) iterator1
						.next()).onMediaImageChanged(mediaimagechangenotification.request.getUrl()))
					;
				result = true;
				break;
			default:
				break;
		}
		return result;
	}

	public static synchronized ImageCache getInstance(Context context) {
		if (sInstance == null)
			sInstance = new ImageCache(context.getApplicationContext());
		return sInstance;
	}

	public final void loadImage(ImageConsumer imageconsumer, ImageRequest imagerequest) {
		loadImage(imageconsumer, imagerequest, true);
	}

	private void loadImage(ImageConsumer imageconsumer, ImageRequest imagerequest, boolean flag) {
		if (ImageLoadingMetrics.areImageLoadingMetricsEnabled())
			ImageLoadingMetrics.recordLoadImageRequest(imagerequest.getUriForLogging());
		if (imagerequest.isEmpty()) {
			imageconsumer.setBitmap(null, false);
			notifyRequestComplete(imagerequest);
			return;
		}
		mPendingRequests.remove(imageconsumer);
		if (loadCachedImage(imageconsumer, imagerequest, flag)) {
			mPendingRequests.remove(imageconsumer);
		} else {
			mPendingRequests.put(imageconsumer, imagerequest);
			if (!mPaused)
				requestLoading();
		}
	}

	private boolean loadCachedImage(ImageConsumer imageconsumer, ImageRequest imagerequest, boolean flag) {
		boolean flag1 = false;
		ImageHolder imageholder = (ImageHolder) mImageHolderCache.get(imagerequest);
		if (imageholder == null || !imageholder.fresh || !imageholder.complete || imageholder.image != null
				|| imageholder.bytes == null || imageholder.bytes.length <= 4000
				|| mImageCache.get(imagerequest) != null) {
			if (imageholder == null) {
				flag1 = false;
				if (flag) {
					imageconsumer.setBitmap(null, true);
					flag1 = false;
				}
				return flag1;
			}
			mImageHolderCache.put(imagerequest, imageholder);
			if (mImageHolderCache.get(imagerequest) == null) {
				imageholder = new ImageHolder(null, true);
				mImageHolderCache.put(imagerequest, imageholder);
			}
			if (imageholder.bytes == null) {
				if (imageholder.complete) {
					imageconsumer.setBitmap(null, false);
					if (ImageLoadingMetrics.areImageLoadingMetricsEnabled())
						ImageLoadingMetrics.recordImageDelivered(imagerequest.getUriForLogging(), 0, 0);
					notifyRequestComplete(imagerequest);
				} else {
					imageconsumer.setBitmap(null, true);
				}
				flag1 = imageholder.fresh;
				return flag1;
			}
			decodeImage(imagerequest, imageholder);
			Object obj = imageholder.image;
			if (obj instanceof Bitmap) {
				imageconsumer.setBitmap((Bitmap) obj, false);
				if (ImageLoadingMetrics.areImageLoadingMetricsEnabled()) {
					Bitmap bitmap = (Bitmap) obj;
					String s = imagerequest.getUriForLogging();
					int i;
					if (imageholder.bytes == null)
						i = 0;
					else
						i = imageholder.bytes.length;
					ImageLoadingMetrics.recordImageDelivered(s, i, bitmap.getByteCount());
				}
			} else if ((obj instanceof Drawable) && (imageconsumer instanceof DrawableConsumer))
				((DrawableConsumer) imageconsumer).setDrawable((Drawable) obj, false);
			else if (obj instanceof GifDrawable) {
				imageconsumer.setBitmap(null, false);
			} else {
				if (obj != FAILED_IMAGE)
					throw new UnsupportedOperationException((new StringBuilder("Cannot handle drawables of type "))
							.append(obj.getClass()).toString());
				imageconsumer.setBitmap(null, false);
			}
			notifyRequestComplete(imagerequest);
			imageholder.image = null;
			flag1 = imageholder.fresh;
			return flag1;
		} else {
			imageholder.decodeInBackground = true;
			return flag1;
		}
	}

	public final void notifyAvatarChange(String s) {
		if (s != null) {
			ensureLoaderThread();
			mLoaderThread.notifyAvatarChange(s);
		}
	}

	public static void registerAvatarChangeListener(OnAvatarChangeListener onavatarchangelistener) {
		mAvatarListeners.add(onavatarchangelistener);
	}

	public static void registerMediaImageChangeListener(OnMediaImageChangeListener onmediaimagechangelistener) {
		mMediaImageListeners.add(onmediaimagechangelistener);
	}

	private void requestLoading() {
		if (!mLoadingRequested) {
			mLoadingRequested = true;
			mMainThreadHandler.sendEmptyMessage(1);
		}
	}

	public static void unregisterAvatarChangeListener(OnAvatarChangeListener onavatarchangelistener) {
		mAvatarListeners.remove(onavatarchangelistener);
	}

	public static void unregisterMediaImageChangeListener(OnMediaImageChangeListener onmediaimagechangelistener) {
		mMediaImageListeners.remove(onmediaimagechangelistener);
	}

	public final void cancel(ImageConsumer imageconsumer) {
		mPendingRequests.remove(imageconsumer);
	}

	public final void clear() {
		mImageHolderCache.evictAll();
		mImageCache.evictAll();
		mPendingRequests.clear();
	}

	public final void clearFailedRequests() {
		Iterator<ImageRequest> iterator = mImageHolderCache.snapshot().keySet().iterator();
		do {
			if (!iterator.hasNext())
				break;
			ImageRequest imagerequest = iterator.next();
			ImageHolder imageholder = mImageHolderCache.get(imagerequest);
			if (imageholder != null && imageholder.fresh && imageholder.bytes == null)
				imageholder.fresh = false;
		} while (true);
	}

	public final void notifyMediaImageChange(CachedImageRequest cachedimagerequest, byte abyte0[]) {
		ensureLoaderThread();
		if (cachedimagerequest instanceof MediaImageRequest) {
			MediaImageChangeNotification mediaimagechangenotification = new MediaImageChangeNotification((byte) 0);
			mediaimagechangenotification.request = (MediaImageRequest) cachedimagerequest;
			mediaimagechangenotification.imageBytes = abyte0;
			mLoaderThread.notifyMediaImageChange(mediaimagechangenotification);
		} else if (cachedimagerequest instanceof AvatarImageRequest) {
			ensureLoaderThread();
			mLoaderThread.notifyAvatarChange(((AvatarImageRequest) cachedimagerequest).getGaiaId());
		}
	}

	public final void pause() {
		mPaused = true;
	}

	public final void refreshImage(ImageConsumer imageconsumer, ImageRequest imagerequest) {
		loadImage(imageconsumer, imagerequest, false);
	}

	public final void resume() {
		mPaused = false;
		if (!mPendingRequests.isEmpty())
			requestLoading();
	}

	private static void notifyRequestComplete(ImageRequest imagerequest) {
		for (Iterator iterator = mRequestCompleteListeners.iterator(); iterator.hasNext(); iterator.next())
			;
	}

	private void evictImage(ImageRequest imagerequest) {
		mImageCache.remove(imagerequest);
		ImageHolder imageholder = (ImageHolder) mImageHolderCache.get(imagerequest);
		if (imageholder != null)
			imageholder.fresh = false;
	}

	private void ensureLoaderThread() {
		if (mLoaderThread == null) {
			mContext.getContentResolver();
			mLoaderThread = new LoaderThread();
			mLoaderThread.start();
		}
	}

	private void decodeImage(ImageRequest imagerequest, ImageHolder imageholder) {

		if (null == imageholder.image) {
			byte abyte0[] = imageholder.bytes;
			if (abyte0 != null && abyte0.length != 0) {
				imageholder.image = mImageCache.get(imagerequest);
				if (imageholder.image == null) {
					imageholder.image = ImageUtils.decodeMedia(abyte0);
					if (imageholder.image == null)
						imageholder.image = FAILED_IMAGE;
					mImageCache.put(imagerequest, imageholder.image);
					if (mImageCache.get(imagerequest) == null) {
						imageholder.image = FAILED_IMAGE;
						mImageCache.put(imagerequest, FAILED_IMAGE);
					}
				}
			}
		}
	}

	private void clearTemporaryImageReferences() {
		synchronized (mImageHolderQueue) {
			for (Iterator iterator = mImageHolderQueue.iterator(); iterator.hasNext();)
				((ImageHolder) iterator.next()).image = null;
			mImageHolderQueue.clear();
		}
	}

	static void access$400(ImageCache imagecache, ImageRequest imagerequest, byte abyte0[], boolean flag, boolean flag1) {
		ImageHolder imageholder = new ImageHolder(abyte0, flag);
		imageholder.fresh = true;
		if (flag && !flag1 && imagecache.mPendingRequests.containsValue(imagerequest)
				&& imagecache.mBackgroundThreadBitmapCount < 2) {
			imagecache.mBackgroundThreadBitmapCount = 1 + imagecache.mBackgroundThreadBitmapCount;
			imagecache.decodeImage(imagerequest, imageholder);
		}
		imagecache.mImageHolderCache.put(imagerequest, imageholder);
		if (imagecache.mImageHolderCache.get(imagerequest) == null) {
			imageholder = new ImageHolder(null, true);
			imagecache.mImageHolderCache.put(imagerequest, imageholder);
		}
		synchronized (imagecache.mImageHolderQueue) {
			imagecache.mImageHolderQueue.add(imageholder);
		}
		return;
	}

	// ===========================================================================
	// Inner class
	// ===========================================================================
	private static class ImageHolder {

		final byte bytes[];
		final boolean complete;
		boolean decodeInBackground;
		volatile boolean fresh;
		Object image;

		public ImageHolder(byte[] bytes, boolean complete) {
			this.bytes = bytes;
			this.fresh = true;
			this.complete = complete;
		}
	}

	public static interface ImageConsumer {

		public abstract void setBitmap(Bitmap bitmap, boolean flag);
	}

	public static interface DrawableConsumer extends ImageConsumer {

		public abstract void setDrawable(Drawable drawable, boolean flag);
	}

	private static final class MediaImageChangeNotification {

		byte imageBytes[];
		MediaImageRequest request;

		private MediaImageChangeNotification() {
		}

		MediaImageChangeNotification(byte byte0) {
			this();
		}
	}

	public static interface OnAvatarChangeListener {

		public abstract void onAvatarChanged(String s);
	}

	public static interface OnMediaImageChangeListener {

		public abstract void onMediaImageChanged(String s);
	}

	private final class LoaderThread extends HandlerThread implements Handler.Callback {

		private Handler mLoaderThreadHandler;
		private List mPreloadRequests;
		private int mPreloadStatus;
		private final Set mRequests = new HashSet();

		public LoaderThread() {
			super("ImageCache", 1);
			mPreloadRequests = new ArrayList();
			mPreloadStatus = 0;
		}

		private void continuePreloading() {
			if (mPreloadStatus != 2) {
				ensureHandler();
				if (!mLoaderThreadHandler.hasMessages(2))
					mLoaderThreadHandler.sendEmptyMessageDelayed(1, 50L);
			}
		}

		private void ensureHandler() {
			if (mLoaderThreadHandler == null)
				mLoaderThreadHandler = new Handler(getLooper(), this);
		}

		private void loadImagesFromDatabase(boolean flag) {
			if (mRequests.size() != 0) {
				if (!flag && mPreloadStatus == 1) {
					mPreloadRequests.removeAll(mRequests);
					if (mPreloadRequests.isEmpty())
						mPreloadStatus = 2;
				}
				List arraylist = null;
				List arraylist1 = null;
				List arraylist2 = null;
				for (Iterator iterator = mRequests.iterator(); iterator.hasNext();) {
					ImageRequest imagerequest = (ImageRequest) iterator.next();
					if (imagerequest instanceof AvatarRequest) {
						if (arraylist == null)
							arraylist = new ArrayList();
						arraylist.add((AvatarRequest) imagerequest);
					} else if (imagerequest instanceof MediaImageRequest) {
						if (arraylist1 == null)
							arraylist1 = new ArrayList();
						arraylist1.add((MediaImageRequest) imagerequest);
					} else {
						if (arraylist2 == null)
							arraylist2 = new ArrayList();
						arraylist2.add(imagerequest);
					}
				}

				if (arraylist1 != null) {
					CachedImageRequest cachedimagerequest;
					for (Iterator iterator3 = EsMediaCache.loadMedia(mContext, arraylist1).entrySet().iterator(); iterator3
							.hasNext(); mRequests.remove(cachedimagerequest)) {
						java.util.Map.Entry entry1 = (java.util.Map.Entry) iterator3.next();
						cachedimagerequest = (CachedImageRequest) entry1.getKey();
						access$400(ImageCache.this, cachedimagerequest, (byte[]) entry1.getValue(), true, flag);
						mRequests.remove(cachedimagerequest);
					}

				}
				if (arraylist != null) {
					AvatarRequest avatarrequest;
					for (Iterator iterator2 = EsAvatarData.loadAvatars(mContext, arraylist).entrySet().iterator(); iterator2
							.hasNext(); mRequests.remove(avatarrequest)) {
						java.util.Map.Entry entry = (java.util.Map.Entry) iterator2.next();
						avatarrequest = (AvatarRequest) entry.getKey();
						access$400(ImageCache.this, avatarrequest, (byte[]) entry.getValue(), true, flag);
						mRequests.remove(avatarrequest);
					}

				}
				ImageRequest imagerequest;
				for (Iterator iterator1 = mRequests.iterator(); iterator1.hasNext();) {
					imagerequest = (ImageRequest) iterator1.next();
					access$400(ImageCache.this, imagerequest, null, false, flag);
				}

				mMainThreadHandler.sendEmptyMessage(2);
			}
		}

		private void preloadAvatarsInBackground() {
			if (mPreloadStatus != 2)
				if (mPreloadStatus == 0) {
					if (mPreloadRequests.isEmpty())
						mPreloadStatus = 2;
					else
						mPreloadStatus = 1;
					continuePreloading();
				} else if (mImageHolderCache.size() > mImageHolderCacheRedZoneBytes) {
					mPreloadStatus = 2;
				} else {
					mRequests.clear();
					int i = 0;
					int j = mPreloadRequests.size();
					do {
						if (j <= 0 || mRequests.size() >= 25)
							break;
						j--;
						AvatarRequest avatarrequest = (AvatarRequest) mPreloadRequests.get(j);
						mPreloadRequests.remove(j);
						if (mImageHolderCache.get(avatarrequest) == null) {
							mRequests.add(avatarrequest);
							i++;
						}
					} while (true);
					loadImagesFromDatabase(true);
					if (j == 0)
						mPreloadStatus = 2;
					if (EsLog.isLoggable("ImageCache", 4))
						Log.v("ImageCache",
								(new StringBuilder("Preloaded ")).append(i).append(" avatars. Cache size (bytes): ")
										.append(mImageHolderCache.size()).toString());
					continuePreloading();
				}
		}

		public final boolean handleMessage(Message message) {
			// TODO
			return false;
		}

		public final void notifyAvatarChange(String s) {
			ensureHandler();
			Message message = mLoaderThreadHandler.obtainMessage(3, s);
			mLoaderThreadHandler.sendMessage(message);
		}

		public final void notifyMediaImageChange(MediaImageChangeNotification mediaimagechangenotification) {
			ensureHandler();
			Message message = mLoaderThreadHandler.obtainMessage(4, mediaimagechangenotification);
			mLoaderThreadHandler.sendMessage(message);
		}

		public final void requestLoading() {
			ensureHandler();
			mLoaderThreadHandler.removeMessages(1);
			mLoaderThreadHandler.sendEmptyMessage(2);
		}
	}
}
