/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.CompoundButton;

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
public class PhotoTagAvatarView extends CompoundButton implements
		ImageConsumer, OnAvatarChangeListener {

	private static Integer sAvatarHeight;
    private static Integer sAvatarWidth;
    private Drawable mAvatar;
    private final ImageCache mAvatarCache;
    private boolean mAvatarInvalidated;
    private AvatarRequest mAvatarRequest;
    private Rect mDrawRect;
    private String mSubjectGaiaId;
    private int mTagHeight;
    private int mTagLeft;
    private int mTagTop;
    private int mTagWidth;
    
    public PhotoTagAvatarView(Context context)
    {
        this(context, null);
    }

    public PhotoTagAvatarView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mDrawRect = new Rect();
        mAvatarCache = ImageCache.getInstance(context);
        if(sAvatarWidth == null)
        {
            Resources resources = context.getApplicationContext().getResources();
            sAvatarWidth = Integer.valueOf(resources.getDimensionPixelSize(R.dimen.photo_tag_scroller_avatar_width));
            sAvatarHeight = Integer.valueOf(resources.getDimensionPixelSize(R.dimen.photo_tag_scroller_avatar_height));
        }
    }

    protected void drawableStateChanged()
    {
        super.drawableStateChanged();
        if(mAvatar != null)
        {
            int ai[] = getDrawableState();
            mAvatar.setState(ai);
            invalidate();
        }
    }

    public void jumpDrawablesToCurrentState()
    {
        super.jumpDrawablesToCurrentState();
        if(mAvatar != null)
            mAvatar.jumpToCurrentState();
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ImageCache _tmp = mAvatarCache;
        ImageCache.registerAvatarChangeListener(this);
    }

    public void onAvatarChanged(String s)
    {
        if(s != null && s.equals(String.valueOf(mSubjectGaiaId)) && mAvatarRequest != null)
        {
            mAvatarInvalidated = true;
            invalidate();
        }
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        ImageCache _tmp = mAvatarCache;
        ImageCache.unregisterAvatarChangeListener(this);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(mAvatar instanceof BitmapDrawable) {
        	if(mAvatarInvalidated && mAvatarRequest != null)
            {
                mAvatarInvalidated = false;
                mAvatarCache.refreshImage(this, mAvatarRequest);
            }
            canvas.drawBitmap(((BitmapDrawable)mAvatar).getBitmap(), null, mDrawRect, null);
        } else if(null != mAvatar) {
        	int i = getPaddingTop();
            int j = getPaddingLeft() + mTagLeft;
            int k = i + mTagTop;
            int l = j + sAvatarWidth.intValue();
            int i1 = k + sAvatarHeight.intValue();
            mAvatar.setBounds(j, k, l, i1);
            mAvatar.draw(canvas);
        }
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        getWidth();
        getPaddingRight();
        //mTagWidth;
        
        int i1;
        switch(0x70 & getGravity()) {
	        case 16:
	        	i1 = ((getPaddingTop() + getHeight()) - getPaddingBottom()) / 2 - mTagHeight / 2;
	            //mTagHeight;
	        	break;
	        case 80:
	        	i1 = getHeight() - getPaddingBottom() - mTagHeight;
	        	break;
        	default:
        		i1 = getPaddingTop();
                //mTagHeight;
        		break;
        }
        
        int j1;
        switch(7 & getGravity()) {
	        case 1:
	        	j1 = ((getPaddingLeft() + getWidth()) - getPaddingRight()) / 2 - mTagWidth / 2;
	            //mTagWidth;
	        	break;
	        case 5:
	        	j1 = getWidth() - getPaddingRight() - mTagWidth;
	        	break;
	        default:
	        	j1 = getPaddingLeft();
	            //mTagWidth;
	        	break;
        }
        
        mTagLeft = j1;
        mTagTop = i1;
        int k1 = mTagLeft + getPaddingLeft();
        int l1 = mTagTop + getPaddingTop();
        mDrawRect.set(k1, l1, k1 + sAvatarWidth.intValue(), l1 + sAvatarHeight.intValue());
        if(mAvatar == null)
            if(mAvatarRequest != null)
                mAvatarCache.loadImage(this, mAvatarRequest);
            else
                setBitmap(null, true);
        return;
        
    }

    public void onMeasure(int i, int j)
    {
        int k;
        int l;
        if(mSubjectGaiaId != null)
        {
            int i1 = sAvatarWidth.intValue();
            int j1 = sAvatarHeight.intValue();
            int k1 = getPaddingTop() + getPaddingBottom();
            l = i1 + (getPaddingLeft() + getPaddingRight());
            k = k1 + j1;
        } else
        {
            k = 0;
            l = 0;
        }
        mTagWidth = l;
        mTagHeight = k;
        super.onMeasure(i, j);
        setMeasuredDimension(Math.max(getMeasuredWidth(), l), Math.max(getMeasuredHeight(), k));
    }

    public void setBitmap(Bitmap bitmap, boolean flag)
    {
        if(bitmap == null)
            mAvatar = new BitmapDrawable(EsAvatarData.getSmallDefaultAvatar(getContext()));
        else
            mAvatar = new BitmapDrawable(bitmap);
        invalidate();
    }

    public void setSubjectGaiaId(String s)
    {
        if(!TextUtils.equals(mSubjectGaiaId, s))
        {
            mSubjectGaiaId = s;
            if(s != null)
                mAvatarRequest = new AvatarRequest(s, 1);
            else
                mAvatarRequest = null;
            mAvatar = null;
            requestLayout();
        }
    }

    protected boolean verifyDrawable(Drawable drawable)
    {
        boolean flag;
        if(super.verifyDrawable(drawable) || drawable == mAvatar)
            flag = true;
        else
            flag = false;
        return flag;
    }
}
