/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EventThemeImageRequest;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.server.client.domain.Theme;

/**
 * 
 * @author sihai
 *
 */
public class EventThemeView extends EsImageView {

	private boolean mImageRequested;
    private String mThemeImageUrl;
    
    public EventThemeView(Context context)
    {
        super(context);
        setResizeable(true);
    }

    public EventThemeView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        setResizeable(true);
    }

    public EventThemeView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        setResizeable(true);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        if(l - j > 0 && !mImageRequested)
        {
            mImageRequested = true;
            if(mThemeImageUrl == null)
                setRequest(null);
            else
            if(mThemeImageUrl.toLowerCase().endsWith(".gif"))
                setRequest(new EventThemeImageRequest(mThemeImageUrl));
            else
                setRequest(new EventThemeImageRequest(ImageUtils.getCenterCroppedAndResizedUrl(getMeasuredWidth(), getMeasuredHeight(), mThemeImageUrl)));
        }
    }

    protected void onMeasure(int i, int j) {
    	int l = 0;
        int k = android.view.View.MeasureSpec.getMode(i);
        if(k != 0x40000000) {
        	l = 0;
            if(k == 0x80000000)
                l = Math.min(0, android.view.View.MeasureSpec.getSize(i)); 
        } else { 
        	l = android.view.View.MeasureSpec.getSize(i);
        }
        setMeasuredDimension(l, (int)((float)l / 3.36F));
    }

    public void onRecycle()
    {
        super.onRecycle();
        mThemeImageUrl = null;
    }

    public void setEventTheme(Theme theme)
    {
        setImageUrl(EsEventData.getImageUrl(theme));
    }

    public void setImageUrl(String s)
    {
        if(!TextUtils.equals(s, mThemeImageUrl))
        {
            mThemeImageUrl = s;
            mImageRequested = false;
            requestLayout();
        }
    }
}
