/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * @author sihai
 *
 */
public class ActionButton extends TextView {

	public ActionButton(Context context)
    {
        this(context, null);
    }

    public ActionButton(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        setup();
    }

    public ActionButton(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        setup();
    }

    private void setup()
    {
        CharSequence charsequence = getText();
        if(charsequence != null)
            setText(charsequence.toString().toUpperCase());
    }

    public void setLabel(int i)
    {
        setText(i);
        setup();
    }

    public void setLabel(String s)
    {
        setText(s);
        setup();
    }
}
