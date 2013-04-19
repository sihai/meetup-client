/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AvatarRequest;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageCache.ImageConsumer;

/**
 * 
 * @author sihai
 *
 */
public class ClickableUserImage implements ImageConsumer, ClickableItem {

	private static Paint sImageSelectedPaint;
    private final ImageCache mAvatarCache;
    private boolean mAvatarInvalidated;
    private boolean mAvatarLoaded;
    private AvatarRequest mAvatarRequest;
    private Bitmap mBitmap;
    private final UserImageClickListener mClickListener;
    private boolean mClicked;
    private CharSequence mContentDescription;
    private final Rect mContentRect;
    private final String mUserId;
    private final String mUserName;
    private final View mView;
    
    public ClickableUserImage(View view, String s, String s1, String s2, UserImageClickListener userimageclicklistener)
    {
        this(view, s, null, s2, userimageclicklistener, 1);
    }

    public ClickableUserImage(View view, String s, String s1, String s2, UserImageClickListener userimageclicklistener, int i)
    {
        mView = view;
        Context context = view.getContext();
        mContentRect = new Rect();
        mClickListener = userimageclicklistener;
        mUserId = s;
        mUserName = s2;
        mContentDescription = s2;
        mAvatarCache = ImageCache.getInstance(context);
        mAvatarRequest = new AvatarRequest(mUserId, s1, i, true);
        mAvatarInvalidated = true;
        if(sImageSelectedPaint == null)
        {
            Paint paint = new Paint();
            sImageSelectedPaint = paint;
            paint.setAntiAlias(true);
            sImageSelectedPaint.setStrokeWidth(4F);
            sImageSelectedPaint.setColor(context.getApplicationContext().getResources().getColor(R.color.image_selected_stroke));
            sImageSelectedPaint.setStyle(android.graphics.Paint.Style.STROKE);
        }
    }

    public final int compare(ClickableItem obj, ClickableItem obj1)
    {
        ClickableItem clickableitem = (ClickableItem)obj;
        ClickableItem clickableitem1 = (ClickableItem)obj1;
        return sComparator.compare(clickableitem, clickableitem1);
    }

    public final void drawSelectionRect(Canvas canvas)
    {
        canvas.drawCircle(mContentRect.centerX(), mContentRect.centerY(), mContentRect.width() / 2, sImageSelectedPaint);
    }

    public final Bitmap getBitmap()
    {
        if(mAvatarInvalidated)
        {
            mAvatarInvalidated = false;
            mAvatarCache.refreshImage(this, mAvatarRequest);
        }
        return mBitmap;
    }

    public final CharSequence getContentDescription()
    {
        return mContentDescription;
    }

    public final Rect getRect()
    {
        return mContentRect;
    }

    public final boolean handleEvent(int i, int j, int k) {
    	if(3 == k) {
    		mClicked = false;
    		return true;
    	}
    	boolean flag = true;
    	if(!mContentRect.contains(i, j)) {
	    	if(k == 1)
	    		mClicked = false;
	    	flag = false;
    	} else {
    		switch(k)
            {
            case 0: // '\0'
                mClicked = flag;
                break;

            case 1: // '\001'
                if(mClicked && mClickListener != null)
                    mClickListener.onUserImageClick(mUserId, mUserName);
                mClicked = false;
                break;
            }
    	}
    	return flag;
    }

    public final boolean isClicked()
    {
        return mClicked;
    }

    public final void onAvatarChanged(String s)
    {
        if(TextUtils.equals(s, mUserId))
        {
            mAvatarInvalidated = true;
            mAvatarLoaded = false;
            mView.invalidate();
        }
    }

    public final void setBitmap(Bitmap bitmap, boolean flag)
    {
        boolean flag1;
        if(!flag)
            flag1 = true;
        else
            flag1 = false;
        mAvatarLoaded = flag1;
        mBitmap = bitmap;
        mView.invalidate();
    }

    public final void setRect(int i, int j, int k, int l)
    {
        mContentRect.set(i, j, k, l);
    }
	
	
	public static interface UserImageClickListener {

        void onUserImageClick(String s, String s1);
    }

}
