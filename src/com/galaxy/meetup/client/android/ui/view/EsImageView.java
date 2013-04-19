/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.MediaImageRequest;
import com.galaxy.meetup.client.android.content.cache.ImageRequest;
import com.galaxy.meetup.client.android.service.ImageCache;

/**
 * 
 * @author sihai
 *
 */
public class EsImageView extends ImageView implements ImageCache.DrawableConsumer, ImageCache.ImageConsumer, ImageCache.OnMediaImageChangeListener, Recyclable {

	private static Interpolator sAccelerateInterpolator;
    private static Interpolator sDecelerateInterpolator;
    private static ImageCache sImageCache;
    private int mDefaultResourceId;
    private Uri mDefaultResourceUri;
    private boolean mFadeIn;
    private boolean mInvalidated;
    private boolean mLayoutBlocked;
    private OnImageLoadedListener mListener;
    private boolean mLoaded;
    private ImageRequest mRequest;
    private long mRequestTime;
    private boolean mResizeable;
    
    public EsImageView(Context context)
    {
        super(context);
        if(sImageCache == null)
            sImageCache = ImageCache.getInstance(getContext());
    }

    public EsImageView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        if(sImageCache == null)
            sImageCache = ImageCache.getInstance(getContext());
        updateDefaultResourceId(attributeset);
    }

    public EsImageView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        if(sImageCache == null)
            sImageCache = ImageCache.getInstance(getContext());
        updateDefaultResourceId(attributeset);
    }

    private void onImageLoaded()
    {
        mLoaded = true;
        if(System.currentTimeMillis() - mRequestTime > 100L && mFadeIn && android.os.Build.VERSION.SDK_INT >= 12)
        {
            if(sDecelerateInterpolator == null)
                sDecelerateInterpolator = new DecelerateInterpolator();
            setAlpha(0.01F);
            animate().alpha(1.0F).setDuration(500L).setInterpolator(sDecelerateInterpolator);
        }
        if(mListener != null)
            mListener.onImageLoaded();
    }

    private void updateDefaultResourceId(AttributeSet attributeset)
    {
        if(attributeset != null)
        {
            mDefaultResourceId = attributeset.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", 0);
            mDefaultResourceUri = null;
        }
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ImageCache _tmp = sImageCache;
        ImageCache.registerMediaImageChangeListener(this);
        mInvalidated = true;
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        ImageCache _tmp = sImageCache;
        ImageCache.unregisterMediaImageChangeListener(this);
        mInvalidated = false;
    }

    protected void onDraw(Canvas canvas)
    {
        if(mInvalidated)
        {
            mInvalidated = false;
            if(mRequest != null)
                sImageCache.refreshImage(this, mRequest);
        }
        super.onDraw(canvas);
    }

    public final void onMediaImageChanged(String s)
    {
        if(mRequest != null && (mRequest instanceof MediaImageRequest) && MediaImageRequest.areCanonicallyEqual((MediaImageRequest)mRequest, s))
        {
            mInvalidated = true;
            invalidate();
        }
    }

    public void onRecycle()
    {
        setRequest(null);
        mListener = null;
        mLoaded = false;
    }

    public void requestLayout()
    {
        if(mLayoutBlocked && !mResizeable)
            forceLayout();
        else
            super.requestLayout();
    }

    public void setBitmap(Bitmap bitmap, boolean flag) {
        if(flag)
            return;
        try {
        	mLayoutBlocked = true;
        	setImageBitmap(bitmap);
        	mLayoutBlocked = false;
        	onImageLoaded();
        } catch (Exception e) {
        	 mLayoutBlocked = false;
        }
    }

    public void setDefaultImageUri(Uri uri) {
        mDefaultResourceUri = uri;
        mDefaultResourceId = 0;
        if(!mLoaded && mDefaultResourceUri != null)
            setImageURI(mDefaultResourceUri);
    }

    public void setDrawable(Drawable drawable, boolean flag) {
        if(flag)
            return;
        try {
	        mLayoutBlocked = true;
	        setImageDrawable(drawable);
	        mLayoutBlocked = false;
	        onImageLoaded();
        } catch (Exception e) {
        	mLayoutBlocked = false;
        }
    }

    public void setFadeIn(boolean flag)
    {
        mFadeIn = flag;
    }

    public void setOnImageLoadedListener(OnImageLoadedListener onimageloadedlistener)
    {
        mListener = onimageloadedlistener;
        if(mLoaded)
            onImageLoaded();
    }

    public void setRequest(ImageRequest imagerequest)
    {
        mRequestTime = System.currentTimeMillis();
        if(mRequest == null || !mRequest.equals(imagerequest))
        {
            mRequest = imagerequest;
            mInvalidated = false;
            if(mRequest != null)
            {
                sImageCache.loadImage(this, mRequest);
            } else
            {
                sImageCache.cancel(this);
                if(mDefaultResourceId != 0)
                    setImageResource(mDefaultResourceId);
                else
                    setImageDrawable(null);
            }
        }
    }

    protected final void setResizeable(boolean flag)
    {
        mResizeable = true;
    }

    public void setUrl(String s)
    {
        if(s != null)
            setRequest(new MediaImageRequest(s, 3, getLayoutParams().height));
        else
            setRequest(null);
    }

    public final void startFadeOut(int i)
    {
        if(android.os.Build.VERSION.SDK_INT >= 12)
        {
            if(sAccelerateInterpolator == null)
                sAccelerateInterpolator = new AccelerateInterpolator();
            float af[] = new float[2];
            af[0] = getAlpha();
            af[1] = 0.01F;
            ObjectAnimator objectanimator = ObjectAnimator.ofFloat(this, "alpha", af).setDuration(i);
            objectanimator.setInterpolator(sAccelerateInterpolator);
            objectanimator.start();
        }
    }
    
    public static interface OnImageLoadedListener {

        void onImageLoaded();
    }
}
