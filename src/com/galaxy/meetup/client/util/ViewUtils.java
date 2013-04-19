/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.view.View;

/**
 * 
 * @author sihai
 *
 */
public class ViewUtils {

	public static boolean isViewAttached(View view)
    {
        boolean flag;
        if(view.getWindowToken() != null)
            flag = true;
        else
            flag = false;
        return flag;
    }
}
