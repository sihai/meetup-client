/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AvatarRequest;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageCache.ImageConsumer;
import com.galaxy.meetup.client.android.service.ImageCache.OnAvatarChangeListener;

/**
 * 
 * @author sihai
 *
 */
public class AvatarView extends View implements ImageConsumer,
		OnAvatarChangeListener {

	private static RectF sBoundsRect = new RectF();
    private static Paint sImageSelectedPaint;
    private boolean mAllowNonSquare;
    private Bitmap mAvatarBitmap;
    private final ImageCache mAvatarCache;
    private boolean mAvatarInvalidated;
    private AvatarRequest mAvatarRequest;
    private int mAvatarSize;
    private boolean mDimmed;
    private String mGaiaId;
    private Paint mResizePaint;
    private Rect mResizeRectDest;
    private Rect mResizeRectSrc;
    private boolean mResizeRequired;
    private boolean mRound;
    private boolean mScale;
    private Drawable mSelector;
    private int mSizeInPixels;
    
	public AvatarView(Context context) {
		this(context, null);
	}

    public AvatarView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public AvatarView(Context context, AttributeSet attributeset, int defStyle) {
    	super(context, attributeset, defStyle);
        boolean flag = true;
        Resources resources = context.getResources();
        if(sImageSelectedPaint == null) {
            Paint paint = new Paint();
            sImageSelectedPaint = paint;
            paint.setAntiAlias(flag);
            sImageSelectedPaint.setStrokeWidth(4F);
            sImageSelectedPaint.setColor(resources.getColor(R.color.image_selected_stroke));
            sImageSelectedPaint.setStyle(android.graphics.Paint.Style.STROKE);
        }
        mSelector = resources.getDrawable(R.drawable.stream_list_selector);
        mSelector.setCallback(this);
        mAvatarCache = ImageCache.getInstance(context);
        if(attributeset != null) {
            String s = attributeset.getAttributeValue(null, "size");
            if(s == null)
                throw new RuntimeException("Missing 'size' attribute");
            String s1 = attributeset.getAttributeValue(null, "round");
            if(s1 != null)
                mRound = Boolean.parseBoolean(s1);
            String s2 = attributeset.getAttributeValue(null, "scale");
            if(s2 != null)
                mScale = Boolean.parseBoolean(s2);
            if("tiny".equals(s)) {
            	mAvatarSize = 0;
            } else if("small".equals(s)) {
            	mAvatarSize = 1;
            } else if("medium".equals(s)) {
            	mAvatarSize = 2;
        	} else {
                    throw new IllegalArgumentException((new StringBuilder("Invalid avatar size: ")).append(s).toString());
        	}
            mAllowNonSquare = attributeset.getAttributeBooleanValue(null, "allowNonSquare", false);
        } else {
            mAvatarSize = 2;
        }
        setAvatarSize(mAvatarSize);
    }
    
    protected void drawableStateChanged() {
        mSelector.setState(getDrawableState());
        invalidate();
        super.drawableStateChanged();
    }

    public final String getGaiaId() {
        return mGaiaId;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ImageCache _tmp = mAvatarCache;
        ImageCache.registerAvatarChangeListener(this);
    }

    public void onAvatarChanged(String s) {
        if(s != null && s.equals(String.valueOf(mGaiaId)) && mAvatarRequest != null) {
            mAvatarInvalidated = true;
            invalidate();
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ImageCache _tmp = mAvatarCache;
        ImageCache.unregisterAvatarChangeListener(this);
    }

    protected void onDraw(Canvas canvas) {
    	int i;
        if(mAvatarInvalidated && mAvatarRequest != null) {
            mAvatarInvalidated = false;
            mAvatarCache.refreshImage(this, mAvatarRequest);
        }
        if(mAvatarBitmap != null) {
            if(mDimmed) {
                sBoundsRect.set(0.0F, 0.0F, getWidth(), getHeight());
                canvas.saveLayerAlpha(sBoundsRect, 105, 31);
            }
            if(mResizeRequired)
                canvas.drawBitmap(mAvatarBitmap, mResizeRectSrc, mResizeRectDest, mResizePaint);
            else
                canvas.drawBitmap(mAvatarBitmap, 0.0F, 0.0F, null);
            if(mDimmed)
                canvas.restore();
        }
        if((isPressed() || isFocused()) && !mDimmed)
            if(mRound) {
                i = getWidth() / 2;
                canvas.drawCircle(i, i, i - 2, sImageSelectedPaint);
            } else {
                mSelector.draw(canvas);
            }
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        super.onLayout(flag, i, j, k, l);
        if(!mRound)
            mSelector.setBounds(0, 0, k - i, l - j);
        if(mAvatarBitmap == null)
            if(mAvatarRequest != null)
                mAvatarCache.loadImage(this, mAvatarRequest);
            else
                setBitmap(null, true);
    }

    protected void onMeasure(int i, int j) {
        int k = mSizeInPixels;
        int l = android.view.View.MeasureSpec.getMode(i);
        int i1;
        int j1;
        boolean flag;
        if(l == 0x40000000)
            k = android.view.View.MeasureSpec.getSize(i);
        else
        if(l == 0x80000000)
            k = Math.min(k, android.view.View.MeasureSpec.getSize(i));
        i1 = android.view.View.MeasureSpec.getMode(j);
        if(mAllowNonSquare) {
            j1 = mSizeInPixels;
            if(i1 == 0x40000000 || i1 == 0x80000000)
                j1 = Math.min(j1, android.view.View.MeasureSpec.getSize(j));
        } else
        if(i1 == 0x40000000)
            j1 = Math.min(k, android.view.View.MeasureSpec.getSize(j));
        else
            j1 = Math.min(k, mSizeInPixels);
        if(k != mSizeInPixels)
            flag = true;
        else
            flag = false;
        mResizeRequired = flag;
        if(mResizeRequired) {
            if(mResizePaint == null) {
                mResizePaint = new Paint(2);
                mResizeRectDest = new Rect();
            }
            mResizeRectDest.set(0, 0, k, j1);
            if(mSizeInPixels > k) {
                mResizeRectSrc = new Rect();
                if(mScale) {
                    mResizeRectSrc.set(0, 0, mSizeInPixels, mSizeInPixels);
                } else {
                    int k1 = (mSizeInPixels - k) / 2;
                    int l1 = (k + mSizeInPixels) / 2;
                    int i2 = (mSizeInPixels - j1) / 2;
                    int j2 = (j1 + mSizeInPixels) / 2;
                    mResizeRectSrc.set(k1, i2, l1, j2);
                }
            } else {
                mResizeRectSrc = null;
            }
        }
        setMeasuredDimension(k, j1);
    }
    
    public void setAvatarSize(int i) {
        mAvatarSize = i;
        switch(mAvatarSize) {
        case 0:
        	mSizeInPixels = EsAvatarData.getTinyAvatarSize(getContext());
        	break;
        case 1:
        	mSizeInPixels = EsAvatarData.getSmallAvatarSize(getContext());
        	break;
        case 2:
        	mSizeInPixels = EsAvatarData.getMediumAvatarSize(getContext());
        	break;
        default:
        	 mSizeInPixels = EsAvatarData.getMediumAvatarSize(getContext());
        	 break;
        }
    }

    public void setBitmap(Bitmap bitmap, boolean flag) {
    	if(null != bitmap) {
    		 mAvatarBitmap = bitmap;
    		 return;
    	} 
    	
    	switch(mAvatarSize) {
        case 0:
        	mAvatarBitmap = EsAvatarData.getTinyDefaultAvatar(getContext(), mRound);
        	break;
        case 1:
        	mAvatarBitmap = EsAvatarData.getSmallDefaultAvatar(getContext(), mRound);
        	break;
        case 2:
        	mAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(getContext(), mRound);
        	break;
        default:
        	mAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(getContext(), mRound);
        	break;
        }
    }

    public void setDimmed(boolean dimmed) {
        mDimmed = dimmed;
        invalidate();
    }

	public void setGaiaId(String s) {
		if (!TextUtils.equals(mGaiaId, s)) {
			mGaiaId = s;
			if (s != null)
				mAvatarRequest = new AvatarRequest(s, mAvatarSize, mRound);
			else
				mAvatarRequest = null;
			mAvatarBitmap = null;
			requestLayout();
		}
	}

	public void setGaiaIdAndAvatarUrl(String s, String s1) {
		if (!TextUtils.equals(mGaiaId, s)) {
			mGaiaId = s;
			if (s != null)
				mAvatarRequest = new AvatarRequest(s, s1, mAvatarSize, mRound);
			else
				mAvatarRequest = null;
			mAvatarBitmap = null;
			requestLayout();
		}
	}

	public void setRounded(boolean flag) {
		mRound = flag;
	}

	protected boolean verifyDrawable(Drawable drawable) {
		boolean flag;
		if (drawable == mSelector)
			flag = true;
		else
			flag = super.verifyDrawable(drawable);
		return flag;
	}
}
