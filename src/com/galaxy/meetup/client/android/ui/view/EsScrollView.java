/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ScrollView;

/**
 * 
 * @author sihai
 *
 */
public class EsScrollView extends ScrollView {

	public EsScrollView(Context context)
    {
        super(context);
    }

    public EsScrollView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public EsScrollView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        boolean flag = true;
        if(super.onInterceptTouchEvent(motionevent))
        {
            ViewParent viewparent = getParent();
            if(viewparent != null)
                viewparent.requestDisallowInterceptTouchEvent(flag);
        } else
        {
            flag = false;
        }
        return flag;
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        int i = getScrollY();
        boolean flag = super.onTouchEvent(motionevent);
        if(getScrollY() != i)
        {
            ViewParent viewparent = getParent();
            if(viewparent != null)
                viewparent.requestDisallowInterceptTouchEvent(true);
        }
        return flag;
    }
}
