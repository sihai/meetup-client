/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * @author sihai
 *
 */
public class TextViewUtils {

	public static TextView createText(Context context, AttributeSet attributeset, int i, float f, int j, boolean flag, boolean flag1)
    {
        TextView textview = new TextView(context, attributeset, i);
        textview.setTextSize(0, f);
        textview.setSingleLine(true);
        textview.setTextColor(j);
        textview.setEllipsize(android.text.TextUtils.TruncateAt.END);
        if(flag)
            textview.setTypeface(Typeface.DEFAULT_BOLD);
        return textview;
    }
}
