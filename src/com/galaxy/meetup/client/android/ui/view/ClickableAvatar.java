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
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;

/**
 * 
 * @author sihai
 *
 */
public class ClickableAvatar implements ResourceConsumer, ClickableItem {

	private static Paint sImageSelectedPaint;
    private Resource mAvatarResource;
    private int mAvatarSizeCategory;
    private String mAvatarUrl;
    private final ClickableUserImage.UserImageClickListener mClickListener;
    private boolean mClicked;
    private CharSequence mContentDescription;
    private final Rect mContentRect = new Rect();
    private final String mGaiaId;
    private final String mUserName;
    private final View mView;
    
    public ClickableAvatar(View view, String s, String s1, String s2, ClickableUserImage.UserImageClickListener userimageclicklistener, int i)
    {
        mView = view;
        Context context = view.getContext();
        mClickListener = userimageclicklistener;
        mGaiaId = s;
        mUserName = s2;
        mContentDescription = s2;
        mAvatarUrl = s1;
        mAvatarSizeCategory = 2;
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
    
	@Override
	public final void bindResources()
    {
        if(mAvatarUrl != null)
            mAvatarResource = ImageResourceManager.getInstance(mView.getContext()).getAvatar(mAvatarUrl, mAvatarSizeCategory, true, this);
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
        Bitmap bitmap;
        if(mAvatarResource != null && mAvatarResource.getStatus() == 1)
            bitmap = (Bitmap)mAvatarResource.getResource();
        else
            bitmap = null;
        return bitmap;
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
    	boolean flag = true;
    	if(3 == k) {
    		mClicked = false;
    		return false;
    	}
    	if(!mContentRect.contains(i, j)) {
    		if(k == 1)
                mClicked = false;
            return false;
    	}
    	switch(k) {
	        case 0: // '\0'
	            mClicked = flag;
	            break;
	
	        case 1: // '\001'
	            if(mClicked && mClickListener != null)
	                mClickListener.onUserImageClick(mGaiaId, mUserName);
	            mClicked = false;
	            break;
        }
    	return flag;
    }

    public final boolean isClicked()
    {
        return mClicked;
    }

    public final void onResourceStatusChange(Resource resource)
    {
        mView.invalidate();
    }

    public final void setRect(int i, int j, int k, int l)
    {
        mContentRect.set(i, j, k, l);
    }

    public final void unbindResources()
    {
        if(mAvatarResource != null)
        {
            mAvatarResource.unregister(this);
            mAvatarResource = null;
        }
    }
}
