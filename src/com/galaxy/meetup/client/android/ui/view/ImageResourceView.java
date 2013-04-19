/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.util.GifDrawable;
import com.galaxy.meetup.client.util.GifImage;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.ViewUtils;

/**
 * 
 * @author sihai
 *
 */
public class ImageResourceView extends View implements ResourceConsumer,
		Recyclable {

	private static Interpolator sDecelerateInterpolator;
    private static Bitmap sDefaultIcon;
    private static ImageResourceManager sImageManager;
    private static Bitmap sPanoramaIcon;
    private static final Paint sScalePaint;
    private static Bitmap sVideoIcon;
    private boolean mAnimationEnabled;
    private Bitmap mBitmap;
    private Matrix mBitmapMatrix;
    private boolean mClearCurrentContent;
    private int mCustomImageHeight;
    private int mCustomImageWidth;
    private boolean mDefaultIconEnabled;
    private int mDefaultIconX;
    private int mDefaultIconY;
    private Rect mDestinationRect;
    private RectF mDestinationRectF;
    private Drawable mDrawable;
    private boolean mFadeIn;
    private int mImageResourceFlags;
    private int mLastRequestedHeight;
    private int mLastRequestedWidth;
    private Matrix mMatrix;
    private MediaRef mMediaRef;
    private Drawable mOverlayDrawable;
    private int mPanoramaIconX;
    private int mPanoramaIconY;
    private boolean mPaused;
    private boolean mReleaseImageWhenPaused;
    private long mRequestTime;
    private Resource mResource;
    private Drawable mResourceBrokenDrawable;
    private Drawable mResourceLoadingDrawable;
    private boolean mResourceMissing;
    private Drawable mResourceMissingDrawable;
    private int mScaleMode;
    private Drawable mSelectorDrawable;
    private int mSizeCategory;
    private Rect mSourceRect;
    private RectF mSourceRectF;
    private int mVideoIconX;
    private int mVideoIconY;

	static {
		Paint paint = new Paint();
		sScalePaint = paint;
		paint.setFilterBitmap(true);
	}

	public ImageResourceView(Context context) {
		super(context);
		mSizeCategory = 1;
		mScaleMode = 1;
		mSourceRect = new Rect();
		mDestinationRect = new Rect();
		mDestinationRectF = new RectF();
		mSourceRectF = new RectF();
		mMatrix = new Matrix();
		mAnimationEnabled = true;
		init(context, null);
	}
	
	public ImageResourceView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mSizeCategory = 1;
        mScaleMode = 1;
        mSourceRect = new Rect();
        mDestinationRect = new Rect();
        mDestinationRectF = new RectF();
        mSourceRectF = new RectF();
        mMatrix = new Matrix();
        mAnimationEnabled = true;
        init(context, attributeset);
    }

    public ImageResourceView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mSizeCategory = 1;
        mScaleMode = 1;
        mSourceRect = new Rect();
        mDestinationRect = new Rect();
        mDestinationRectF = new RectF();
        mSourceRectF = new RectF();
        mMatrix = new Matrix();
        mAnimationEnabled = true;
        init(context, attributeset);
    }
    
    private void init(Context context, AttributeSet attributeset) {
        if(sImageManager == null) {
            sImageManager = ImageResourceManager.getInstance(context);
            sVideoIcon = ImageUtils.decodeResource(context.getResources(), R.drawable.ic_video_play);
            sPanoramaIcon = ImageUtils.decodeResource(context.getResources(), R.drawable.overlay_lightcycle);
            sDefaultIcon = ImageUtils.decodeResource(context.getResources(), R.drawable.ic_missing_photo);
        }
        mSelectorDrawable = context.getResources().getDrawable(R.drawable.stream_list_selector);
        mSelectorDrawable.setCallback(this);
        if(attributeset == null) {
        	return;
        }
        
        String s = attributeset.getAttributeValue(null, "size");
        if(s != null)
            if("custom".equals(s))
                mSizeCategory = 0;
            else
            if("thumbnail".equals(s))
                mSizeCategory = 2;
            else
            if("large".equals(s))
                mSizeCategory = 3;
            else
            if("portrait".equals(s))
                mSizeCategory = 4;
            else
            if("landscape".equals(s))
                mSizeCategory = 5;
            else
            if("full".equals(s))
                mSizeCategory = 1;
            else
                throw new IllegalArgumentException((new StringBuilder("Invalid size category: ")).append(s).toString());
        String s1 = attributeset.getAttributeValue(null, "scale");
        if(s1 == null) {
        	return;
        }
        
        if("zoom".equals(s1)) {
        	mScaleMode = 1;
        	return;
        }
        if("fit".equals(s1)) {
        	mScaleMode = 0;
        	return;
        }
        throw new IllegalArgumentException((new StringBuilder("Invalid scale mode: ")).append(s1).toString());
    }
    
    private void clearDrawable()
    {
        if(mDrawable != null)
            mDrawable.setCallback(null);
        if(mDrawable instanceof Recyclable)
            ((Recyclable)mDrawable).onRecycle();
        mDrawable = null;
    }
    
    private void computeRects(int i, int j) {
        int k = getPaddingTop();
        int l = getPaddingRight();
        int i1 = getPaddingBottom();
        int j1 = getPaddingLeft();
        int k1 = getWidth() - j1 - l;
        int l1 = getHeight() - k - i1;
        float f = (float)i / (float)j;
        float f1 = (float)k1 / (float)l1;
        if(0 == mScaleMode) {
        	mSourceRect.set(0, 0, i, j);
            if(f > f1)
            {
                int i3 = (l1 - (int)((float)k1 / f)) / 2;
                mDestinationRect.set(j1, k + i3, j1 + k1, (k + l1) - i3);
            } else
            {
                int l2 = (k1 - (int)(f * (float)l1)) / 2;
                mDestinationRect.set(j1 + l2, k, (j1 + k1) - l2, k + l1);
            }
        } else if(1 == mScaleMode) {
        	if(f > f1)
            {
                int k2 = (i - (int)(f1 * (float)j)) / 2;
                mSourceRect.set(k2, 0, i - k2, j);
            } else
            {
                int i2 = (int)((float)i / f1);
                int j2 = Math.max((int)(0.4F * (float)j) - i2 / 2, 0);
                mSourceRect.set(0, j2, i, j2 + i2);
            }
            mDestinationRect.set(j1, k, j1 + k1, k + l1);
        } else if(2 == mScaleMode) {
        	 mSourceRect.set(0, 0, i, j);
             mDestinationRect.set(0, 0, k1, l1);
        } else {
        	mSourceRectF.set(mSourceRect);
            mDestinationRectF.set(mDestinationRect);
            mMatrix.setRectToRect(mSourceRectF, mDestinationRectF, android.graphics.Matrix.ScaleToFit.FILL);
        }
    }
    
    private void drawResourceStatus(Canvas canvas, Drawable drawable)
    {
        if(drawable != null)
        {
            drawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            drawable.draw(canvas);
        }
    }

    private Drawable getDrawable(int i)
    {
        Drawable drawable;
        if(i == 0)
            drawable = null;
        else
            drawable = getResources().getDrawable(i);
        return drawable;
    }

    private boolean hasDrawable()
    {
        boolean flag;
        if(hasImage() && (mResource.getResource() instanceof GifImage))
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    public final void bindResources()
    {
        if(ViewUtils.isViewAttached(this) && !mPaused)
        {
            mRequestTime = System.currentTimeMillis();
            if(mMediaRef != null)
            {
                if(mSizeCategory == 0)
                {
                    int i;
                    int j;
                    if(mCustomImageWidth != 0 || mCustomImageHeight != 0)
                    {
                        i = mCustomImageWidth;
                        j = mCustomImageHeight;
                    } else
                    {
                        i = getWidth();
                        j = getHeight();
                    }
                    if((i != 0 || j != 0) && (mLastRequestedHeight != j || mLastRequestedWidth != i))
                    {
                        mResource = sImageManager.getMedia(mMediaRef, i, j, mImageResourceFlags, this);
                        mLastRequestedWidth = i;
                        mLastRequestedHeight = j;
                    }
                } else
                {
                    mResource = sImageManager.getMedia(mMediaRef, mSizeCategory, mImageResourceFlags, this);
                }
            } else
            {
                mBitmap = null;
                clearDrawable();
            }
        }
    }
    
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        if(!isPressed() && !isFocused()) {
        	if(isSelected())
            {
                mSelectorDrawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
                mSelectorDrawable.draw(canvas);
            }
        } else {
        	mSelectorDrawable.setBounds(0, 0, getWidth(), getHeight());
            mSelectorDrawable.draw(canvas);
        }
    }

    protected void drawableStateChanged()
    {
        mSelectorDrawable.setState(getDrawableState());
        invalidate();
        super.drawableStateChanged();
    }

    protected final Bitmap getBitmap()
    {
        Bitmap bitmap;
        if(hasBitmap())
            bitmap = (Bitmap)mResource.getResource();
        else
            bitmap = mBitmap;
        return bitmap;
    }

    public final MediaRef getMediaRef()
    {
        return mMediaRef;
    }

    protected final boolean hasBitmap()
    {
        boolean flag;
        if(hasImage() && (mResource.getResource() instanceof Bitmap))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean hasImage()
    {
        boolean flag = true;
        if(mResource == null || mResource.getStatus() != 1)
            flag = false;
        return flag;
    }

    public void invalidateDrawable(Drawable drawable)
    {
        if(drawable == mDrawable)
            invalidate();
        else
            super.invalidateDrawable(drawable);
    }

    public void jumpDrawablesToCurrentState()
    {
        super.jumpDrawablesToCurrentState();
        if(mDrawable != null)
            mDrawable.jumpToCurrentState();
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        mPaused = false;
        bindResources();
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        onUnbindResources();
    }
    
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		boolean flag = hasBitmap();
		if (!flag && !hasDrawable()) {
			if (mDefaultIconEnabled) {
				canvas.drawBitmap(sDefaultIcon, mDefaultIconX, mDefaultIconY,
						null);
			} else {
				if (mResource != null)
					switch (mResource.getStatus()) {
					case 0: // '\0'
					case 2: // '\002'
					case 3: // '\003'
						drawResourceStatus(canvas, mResourceLoadingDrawable);
						break;

					case 4: // '\004'
						drawResourceStatus(canvas, mResourceMissingDrawable);
						break;

					case 5: // '\005'
					case 6: // '\006'
					case 7: // '\007'
						drawResourceStatus(canvas, mResourceBrokenDrawable);
						break;
					}
				else if (mResourceMissing)
					drawResourceStatus(canvas, mResourceMissingDrawable);
				else
					drawResourceStatus(canvas, mResourceLoadingDrawable);
			}
		} else {
			boolean flag1;
			if (flag) {
				Bitmap bitmap = getBitmap();
				if (bitmap != null && !bitmap.isRecycled()) {
					if (mSourceRect.isEmpty())
						computeRects(bitmap.getWidth(), bitmap.getHeight());
					if (mScaleMode == 2 && mBitmapMatrix != null)
						canvas.drawBitmap(bitmap, mBitmapMatrix, sScalePaint);
					else
						canvas.drawBitmap(bitmap, mSourceRect,
								mDestinationRect, sScalePaint);
				}
			} else {
				if (mDrawable == null && hasDrawable()) {
					mSourceRect.setEmpty();
					mDrawable = new GifDrawable(
							(GifImage) mResource.getResource());
					((GifDrawable) mDrawable)
							.setAnimationEnabled(mAnimationEnabled);
					mDrawable.setCallback(this);
				}
				Drawable drawable = mDrawable;
				if (drawable != null)
					if ((drawable instanceof GifDrawable)
							&& !((GifDrawable) drawable).isValid()) {
						if (mDefaultIconEnabled)
							canvas.drawBitmap(sDefaultIcon, mDefaultIconX,
									mDefaultIconY, null);
						else
							drawResourceStatus(canvas, mResourceBrokenDrawable);
					} else {
						int i = drawable.getIntrinsicWidth();
						int j = drawable.getIntrinsicHeight();
						if (mSourceRect.isEmpty())
							computeRects(i, j);
						canvas.save();
						drawable.setBounds(0, 0, i, j);
						canvas.concat(mMatrix);
						drawable.draw(canvas);
						canvas.restore();
					}
			}
			if (mMediaRef != null
					&& MediaRef.MediaType.VIDEO == mMediaRef.getType())
				flag1 = true;
			else
				flag1 = false;
			if (flag1) {
				canvas.drawBitmap(sVideoIcon, mVideoIconX, mVideoIconY, null);
			} else {
				boolean flag2;
				if (mMediaRef != null
						&& MediaRef.MediaType.PANORAMA == mMediaRef.getType())
					flag2 = true;
				else if (mResource != null && mResource.getResourceType() == 2)
					flag2 = true;
				else
					flag2 = false;
				if (flag2)
					canvas.drawBitmap(sPanoramaIcon, mPanoramaIconX,
							mPanoramaIconY, null);
			}
		}

		if (mOverlayDrawable != null) {
			mOverlayDrawable.setBounds(0, 0, getWidth(), getHeight());
			mOverlayDrawable.draw(canvas);
		}
	}
    
    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        int i1 = k - i;
        int j1 = l - j;
        mVideoIconX = (i1 - sVideoIcon.getWidth()) / 2;
        mVideoIconY = (j1 - sVideoIcon.getHeight()) / 2;
        mPanoramaIconX = (i1 - sPanoramaIcon.getWidth()) / 2;
        mPanoramaIconY = (j1 - sPanoramaIcon.getHeight()) / 2;
        mDefaultIconX = (i1 - sDefaultIcon.getWidth()) / 2;
        mDefaultIconY = (j1 - sDefaultIcon.getHeight()) / 2;
        if(flag && mSizeCategory == 0)
        {
            onUnbindResources();
            bindResources();
        }
    }

    public void onRecycle()
    {
        onUnbindResources();
        setMediaRef(null);
    }
    
    public final void onResourceStatusChange(Resource resource)
    {
        if(1 == resource.getStatus()) {
        	 if(System.currentTimeMillis() - mRequestTime > 100L && mFadeIn && android.os.Build.VERSION.SDK_INT >= 12)
             {
                 if(sDecelerateInterpolator == null)
                     sDecelerateInterpolator = new DecelerateInterpolator();
                 setAlpha(0.01F);
                 animate().alpha(1.0F).setDuration(500L).setInterpolator(sDecelerateInterpolator);
             }
        }
        invalidate();
    }

    public final void onResume()
    {
        if(mReleaseImageWhenPaused)
        {
            mPaused = false;
            bindResources();
        }
    }

    public final void onStop()
    {
        if(mReleaseImageWhenPaused)
        {
            mPaused = true;
            onUnbindResources();
        }
    }

    protected void onUnbindResources()
    {
        if(mResource != null)
        {
            mResource.unregister(this);
            mResource = null;
        }
        if(!mClearCurrentContent)
            mBitmap = getBitmap();
        clearDrawable();
        mOverlayDrawable = null;
        mSourceRect.setEmpty();
        mLastRequestedWidth = 0;
        mLastRequestedHeight = 0;
    }

    public void setCustomImageSize(int i, int j)
    {
        mCustomImageWidth = i;
        mCustomImageHeight = j;
    }

    public void setDefaultIconEnabled(boolean flag)
    {
        if(flag != mDefaultIconEnabled)
        {
            mDefaultIconEnabled = flag;
            invalidate();
        }
    }

    public void setFadeIn(boolean flag)
    {
        mFadeIn = flag;
    }

    public void setImageMatrix(Matrix matrix)
    {
        mBitmapMatrix = matrix;
    }

    public void setImageResourceFlags(int i)
    {
        mImageResourceFlags = i;
    }

    public void setMediaRef(MediaRef mediaref)
    {
        setMediaRef(mediaref, true);
    }

    public void setMediaRef(MediaRef mediaref, boolean flag)
    {
        if(mMediaRef == null || !mMediaRef.equals(mediaref))
        {
            mClearCurrentContent = flag;
            onUnbindResources();
            mMediaRef = mediaref;
            if(mMediaRef != null)
                mResourceMissing = false;
            bindResources();
            invalidate();
        }
    }

    public void setOverlay(Drawable drawable)
    {
        if(mOverlayDrawable != drawable)
        {
            mOverlayDrawable = drawable;
            invalidate();
        }
    }

    public void setReleaseImageWhenPaused(boolean flag)
    {
        mReleaseImageWhenPaused = flag;
    }

    public void setResourceBrokenDrawable(int i)
    {
        mResourceBrokenDrawable = getDrawable(i);
    }

    public void setResourceBrokenDrawable(Drawable drawable)
    {
        mResourceBrokenDrawable = drawable;
    }

    public void setResourceLoadingDrawable(int i)
    {
        mResourceLoadingDrawable = getDrawable(i);
    }

    public void setResourceLoadingDrawable(Drawable drawable)
    {
        mResourceLoadingDrawable = drawable;
    }

    public void setResourceMissing(boolean flag)
    {
        mResourceMissing = flag;
    }

    public void setResourceMissingDrawable(int i)
    {
        mResourceMissingDrawable = getDrawable(i);
    }

    public void setResourceMissingDrawable(Drawable drawable)
    {
        mResourceMissingDrawable = drawable;
    }

    public void setScaleMode(int i)
    {
        if(i != mScaleMode)
        {
            mScaleMode = i;
            mSourceRect.setEmpty();
            invalidate();
        }
    }

    public void setSelected(boolean flag)
    {
        if(flag != isSelected())
        {
            super.setSelected(flag);
            if(mSelectorDrawable != null)
                invalidate();
        }
    }

    public void setSelector(Drawable drawable)
    {
        mSelectorDrawable = drawable;
        if(mSelectorDrawable != null)
            mSelectorDrawable.setCallback(this);
    }

    public void setSizeCategory(int i)
    {
        mSizeCategory = i;
    }

    public final void unbindResources()
    {
        onUnbindResources();
    }

    protected boolean verifyDrawable(Drawable drawable)
    {
        boolean flag;
        if(drawable == mSelectorDrawable || drawable == mDrawable)
            flag = true;
        else
            flag = super.verifyDrawable(drawable);
        return flag;
    }
}
