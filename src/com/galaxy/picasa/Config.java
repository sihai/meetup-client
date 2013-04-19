/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 
 * @author sihai
 *
 */
public class Config {

	public static int sScreenNailSize = 640;
    public static int sThumbNailSize = 200;
    
	public static void init(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager)context.getSystemService("window")).getDefaultDisplay().getMetrics(displaymetrics);
        int i = Math.max(displaymetrics.heightPixels, displaymetrics.widthPixels);
        sScreenNailSize = i / 2;
        sThumbNailSize = i / 5;
    }

}
