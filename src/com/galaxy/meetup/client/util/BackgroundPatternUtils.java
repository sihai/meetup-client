/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class BackgroundPatternUtils {

	private static BackgroundPatternUtils sInstance;
    private static BitmapDrawable sTiledPlusStageDrawables[];
    
    private BackgroundPatternUtils()
    {
    }

    public static BitmapDrawable getBackgroundPattern(String s)
    {
        return sTiledPlusStageDrawables[3 & s.hashCode()];
    }

    public static BackgroundPatternUtils getInstance(Context context)
    {
        if(sTiledPlusStageDrawables == null)
        {
            Resources resources = context.getResources();
            BitmapDrawable abitmapdrawable[] = new BitmapDrawable[4];
            abitmapdrawable[0] = (BitmapDrawable)resources.getDrawable(R.drawable.bg_blue_tile);
            abitmapdrawable[1] = (BitmapDrawable)resources.getDrawable(R.drawable.bg_green_tile);
            abitmapdrawable[2] = (BitmapDrawable)resources.getDrawable(R.drawable.bg_red_tile);
            abitmapdrawable[3] = (BitmapDrawable)resources.getDrawable(R.drawable.bg_yellow_tile);
            sTiledPlusStageDrawables = abitmapdrawable;
            int i = 0;
            for(int j = sTiledPlusStageDrawables.length; i < j; i++)
            {
                sTiledPlusStageDrawables[i].setTileModeX(android.graphics.Shader.TileMode.REPEAT);
                sTiledPlusStageDrawables[i].setTileModeY(android.graphics.Shader.TileMode.REPEAT);
            }

            sInstance = new BackgroundPatternUtils();
        }
        return sInstance;
    }

}
