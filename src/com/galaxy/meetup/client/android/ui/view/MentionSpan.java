/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * 
 * @author sihai
 *
 */
public class MentionSpan extends URLSpan {

	MentionSpan(URLSpan urlspan)
    {
        super(urlspan.getURL());
        if(!isMention(urlspan))
            throw new IllegalArgumentException(urlspan.getURL());
        else
            return;
    }

    public MentionSpan(String s)
    {
        super((new StringBuilder("+")).append(s).toString());
    }

    public static boolean isMention(URLSpan urlspan)
    {
        String s = urlspan.getURL();
        boolean flag;
        if(s != null && s.startsWith("+"))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final String getAggregateId()
    {
        return getURL().substring(1);
    }

    public void updateDrawState(TextPaint textpaint)
    {
        textpaint.setColor(0xff3366cc);
        textpaint.bgColor = 0;
        textpaint.setUnderlineText(false);
    }
}
