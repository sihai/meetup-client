/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * 
 * @author sihai
 *
 */
public class SpannableUtils {

	public static void appendWithSpan(SpannableStringBuilder spannablestringbuilder, CharSequence charsequence, Object obj)
    {
        int i = spannablestringbuilder.length();
        spannablestringbuilder.append(charsequence);
        spannablestringbuilder.setSpan(obj, i, spannablestringbuilder.length(), 33);
    }

    public static void setTextWithHighlight(TextView textview, String s, SpannableStringBuilder spannablestringbuilder, String s1, Object obj, Object obj1)
    {
        if(TextUtils.isEmpty(s) || TextUtils.isEmpty(s1))
        {
            textview.setText(s);
        } else
        {
            int i = s.toUpperCase().indexOf(s1);
            if(i == -1)
            {
                textview.setText(s);
            } else
            {
                spannablestringbuilder.clear();
                spannablestringbuilder.append(s);
                int j = i + s1.length();
                if(j > spannablestringbuilder.length())
                    j = spannablestringbuilder.length();
                spannablestringbuilder.setSpan(obj, i, j, 0);
                spannablestringbuilder.setSpan(obj1, i, j, 0);
                textview.setText(spannablestringbuilder);
            }
        }
    }
}
