/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.galaxy.meetup.client.android.EsApplication;
import com.galaxy.meetup.client.android.common.Recyclable;

/**
 * 
 * @author sihai
 *
 */
public class GifDrawable extends Drawable implements Animatable, Callback,
		Recyclable, Runnable {

	private static final byte NETSCAPE2_0[] = "NETSCAPE2.0".getBytes();
	private static Handler sDecoderHandler;
	private static DecoderThread sDecoderThread;
	private static Paint sPaint;
	private static Paint sScalePaint;
	private int mActiveColorTable[];
	private boolean mAnimationEnabled;
	private int mBackgroundColor;
	private int mBackup[];
	private boolean mBackupSaved;
	private Bitmap mBitmap;
	private byte mBlock[];
	private int mColors[];
	private final byte mData[];
	private int mDisposalMethod;
	private boolean mDone;
	private volatile boolean mError;
	private boolean mFirstFrameReady;
	private int mFrameCount;
	private int mFrameDelay;
	private int mFrameHeight;
	private int mFrameWidth;
	private int mFrameX;
	private int mFrameY;
	private final GifImage mGifImage;
	private final Handler mHandler = new Handler(Looper.getMainLooper(), this);
	private int mHeight;
	private boolean mInterlace;
	private volatile int mIntrinsicHeight;
	private volatile int mIntrinsicWidth;
	private int mLocalColorTable[];
	private int mLocalColorTableSize;
	private boolean mLocalColorTableUsed;
	private byte mPixelStack[];
	private byte mPixels[];
	private volatile int mPosition;
	private short mPrefix[];
	private boolean mRecycled;
	private boolean mRunning;
	private boolean mScale;
	private float mScaleFactor;
	private boolean mScheduled;
	private byte mSuffix[];
	private boolean mTransparency;
	private int mTransparentColorIndex;
	private int mWidth;
	
	public GifDrawable(GifImage gifimage) {
		mBlock = new byte[256];
		mDisposalMethod = 2;
		mPrefix = new short[4096];
		mSuffix = new byte[4096];
		mPixelStack = new byte[4097];
		mAnimationEnabled = true;
		if (sDecoderThread == null) {
			DecoderThread decoderthread = new DecoderThread();
			sDecoderThread = decoderthread;
			decoderthread.start();
			sDecoderHandler = new Handler(sDecoderThread.getLooper(),
					sDecoderThread);
		}
		if (sPaint == null) {
			sPaint = new Paint(2);
			Paint paint = new Paint(2);
			sScalePaint = paint;
			paint.setFilterBitmap(true);
		}
		mGifImage = gifimage;
		mData = gifimage.getData();
		mPosition = mGifImage.mHeaderSize;
		int i = gifimage.getWidth();
		mIntrinsicWidth = i;
		mFrameWidth = i;
		int j = gifimage.getHeight();
		mIntrinsicHeight = j;
		mFrameHeight = j;
		mBackgroundColor = mGifImage.mBackgroundColor;
		mError = mGifImage.mError;
		if (!mError)
			try {
				int k = mIntrinsicWidth;
				int l = mIntrinsicHeight;
				int i1 = EsApplication.sMemoryClass;
				boolean flag = false;
				if (i1 < 64)
					flag = true;
				android.graphics.Bitmap.Config config;
				int j1;
				if (flag)
					config = android.graphics.Bitmap.Config.ARGB_4444;
				else
					config = android.graphics.Bitmap.Config.ARGB_8888;
				mBitmap = Bitmap.createBitmap(k, l, config);
				j1 = mIntrinsicWidth * mIntrinsicHeight;
				mColors = new int[j1];
				mPixels = new byte[j1];
				mWidth = mIntrinsicHeight;
				mHeight = mIntrinsicHeight;
				sDecoderHandler.sendMessage(sDecoderHandler.obtainMessage(0,
						this));
			} catch (OutOfMemoryError outofmemoryerror) {
				mError = true;
			}
	}
	
	private void backupFrame() {
		if (!mBackupSaved) {
			if (mBackup == null) {
				try {
					mBackup = new int[mColors.length];
				} catch (OutOfMemoryError outofmemoryerror) {
					Log.e("GifDrawable",
							"GifDrawable.backupFrame threw an OOME",
							outofmemoryerror);
				}
			}
			if (mBackup != null) {
				System.arraycopy(mColors, 0, mBackup, 0, mColors.length);
				mBackupSaved = true;
			}
		}
	}
	
	private int readBlock() {
		byte abyte0[] = mData;
		int i = mPosition;
		mPosition = i + 1;
		int j = 0xff & abyte0[i];
		if (j > 0) {
			System.arraycopy(mData, mPosition, mBlock, 0, j);
			mPosition = j + mPosition;
		}
		return j;
	}

	private int readShort() {
		byte abyte0[] = mData;
		int i = mPosition;
		mPosition = i + 1;
		int j = 0xff & abyte0[i];
		byte abyte1[] = mData;
		int k = mPosition;
		mPosition = k + 1;
		return j | (0xff & abyte1[k]) << 8;
	}

	private void reset() {
		sDecoderHandler.sendMessage(sDecoderHandler.obtainMessage(7, this));
		mFrameCount = 0;
		mScheduled = false;
	}

	private void skip() {
		int j;
		do {
			int i = mPosition;
			mPosition = i + 1;
			j = 0xff & mData[i];
			mPosition = j + mPosition;
		} while (j > 0);
	}
	
	@Override
	public void run() {
		if (!this.mRecycled) {
			if (this.mDone) {
				if (this.mFrameCount > 1) {
					this.mDone = false;
					reset();
				}
			} else {
				sDecoderHandler.sendMessage(sDecoderHandler.obtainMessage(2,
						this));
			}
			stop();
		}
	}

	@Override
	public boolean handleMessage(Message message) {
		boolean flag = false;
		switch (message.what) {
		case 1:
			mError = true;
			flag = true;
			break;
		case 2:
			break;
		case 3:
			mDone = true;
			flag = true;
			break;
		case 4:
			mScheduled = false;
			invalidateSelf();
			flag = true;
			break;
		case 5:
			if (mBitmap != null) {
				mBitmap.setPixels(mColors, 0, mIntrinsicWidth, 0, 0,
						mIntrinsicWidth, mIntrinsicHeight);
				mFirstFrameReady = true;
				invalidateSelf();
			}
			flag = true;
			break;
		case 6:
			mFrameCount = 1 + mFrameCount;
			flag = true;
			break;
		default:
			break;
		}
		return flag;
	}

	@Override
	public boolean isRunning() {
		return mRunning;
	}

	public final void start() {
		if (!isRunning()) {
			mRunning = true;
			run();
		}
	}

	public final void stop() {
		if (isRunning())
			unscheduleSelf(this);
	}

	@Override
	public void draw(Canvas canvas) {
		if (!mError && mWidth != 0 && mHeight != 0 && !mRecycled
				&& mFirstFrameReady) {
			if (mScale) {
				canvas.save();
				canvas.scale(mScaleFactor, mScaleFactor, 0.0F, 0.0F);
				canvas.drawBitmap(mBitmap, 0.0F, 0.0F, sScalePaint);
				canvas.restore();
			} else {
				canvas.drawBitmap(mBitmap, 0.0F, 0.0F, sPaint);
			}
			if (mRunning) {
				if (!mScheduled)
					scheduleSelf(this, SystemClock.uptimeMillis()
							+ (long) mFrameDelay);
			} else if (!mDone)
				start();
			else
				unscheduleSelf(this);
		}
	}
	
	public final int getIntrinsicHeight() {
		return mIntrinsicHeight;
	}

	public final int getIntrinsicWidth() {
		return mIntrinsicWidth;
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
		// TODO Auto-generated method stub

	}
	
	public final boolean isValid() {
		boolean flag;
		if (!mError && mFirstFrameReady)
			flag = true;
		else
			flag = false;
		return flag;
	}

    protected final void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        mWidth = rect.width();
        mHeight = rect.height();
        boolean flag;
        if(mWidth != mIntrinsicWidth && mHeight != mIntrinsicHeight)
            flag = true;
        else
            flag = false;
        mScale = flag;
        if(mScale)
            mScaleFactor = Math.max((float)mWidth / (float)mIntrinsicWidth, (float)mHeight / (float)mIntrinsicHeight);
        reset();
    }

    public final void onRecycle() {
        if(mBitmap != null)
            mBitmap.recycle();
        mBitmap = null;
        mRecycled = true;
    }
    
    public final void scheduleSelf(Runnable runnable, long l) {
        if(mAnimationEnabled) {
            super.scheduleSelf(runnable, l);
            mScheduled = true;
        }
    }

    public final void setAnimationEnabled(boolean flag)
    {
        if(mAnimationEnabled != flag)
        {
            mAnimationEnabled = flag;
            if(mAnimationEnabled)
                start();
            else
                stop();
        }
    }

    public final boolean setVisible(boolean flag, boolean flag1)
    {
        boolean flag2 = super.setVisible(flag, flag1);
        if(flag)
        {
            if(flag2 || flag1)
                start();
        } else
        {
            stop();
        }
        return flag2;
    }

    public final void unscheduleSelf(Runnable runnable)
    {
        super.unscheduleSelf(runnable);
        mRunning = false;
    }
	
	private static final class DecoderThread extends HandlerThread implements Handler.Callback {

		public DecoderThread() {
	        super("GifDecoder");
	    }
		
		public final boolean handleMessage(Message message) {
			boolean flag = true;
			GifDrawable gifdrawable = (GifDrawable) message.obj;

			switch (message.what) {
			case 0:
				try {
					// FIXME
					//GifDrawable.access$000(gifdrawable);
				} catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception1) {
					gifdrawable.mHandler.sendEmptyMessage(0);
				}
				break;
			case 2:
				try {
					// FIXME
					//GifDrawable.access$000(gifdrawable);
				} catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
					gifdrawable.mHandler.sendEmptyMessage(3);
				}
				gifdrawable.mHandler.sendEmptyMessage(4);
				break;
			case 7:
				gifdrawable.mPosition = gifdrawable.mGifImage.mHeaderSize;
				gifdrawable.mBackupSaved = false;
				gifdrawable.mDisposalMethod = 0;
				break;
			default:
				flag = false;
				break;
			}
			return flag;
		}
	}
}
