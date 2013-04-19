/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ScreenMetrics {
	
	private static ScreenMetrics sInstance;
    public final int itemMargin;
    public final int longDimension;
    public final int screenDisplayType;
    public final int shortDimension;

	private ScreenMetrics(int shortDimension, int longDimension,
			int screenDisplayType, int itemMargin) {
		this.shortDimension = shortDimension;
		this.longDimension = longDimension;
		this.screenDisplayType = screenDisplayType;
		this.itemMargin = itemMargin;
	}

	public static ScreenMetrics getInstance(Context context)
    {
		if(null != sInstance) {
			return sInstance;
		} else {
			WindowManager windowmanager = (WindowManager)context.getSystemService("window");
			DisplayMetrics displaymetrics = new DisplayMetrics();
	        windowmanager.getDefaultDisplay().getMetrics(displaymetrics);
	        boolean flag;
	        int l;
	        int k;
	        if(context.getResources().getConfiguration().orientation == 2)
	            flag = true;
	        else
	            flag = false;
	        int i = displaymetrics.widthPixels;
	        int j = displaymetrics.heightPixels;
	        if(i != 0 && j != 0 && displaymetrics.density != 0.0F) {
	        	float f = (float)i / displaymetrics.density;
	            float f1 = (float)j / displaymetrics.density;
	            if(f >= 550F && f1 >= 550F)
	                k = 1;
	            else
	                k = 0;
	        } else {
	        	k = 0;
	        }
	        
	        l = (int)(context.getResources().getDimension(R.dimen.card_margin_percentage) * (float)Math.min(i, j));
            int i1;
            if(flag)
                i1 = j;
            else
                i1 = i;
            if(!flag)
                i = j;
            sInstance = new ScreenMetrics(i1, i, k, l);
            return sInstance;
		}
    }
}