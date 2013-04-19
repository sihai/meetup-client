/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class UpperCaseLabel extends TextView {

	public UpperCaseLabel(Context context)
    {
        this(context, null);
    }

    public UpperCaseLabel(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, R.style.Tab);
    }

    public UpperCaseLabel(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        CharSequence charsequence = getText();
        if(charsequence != null)
            setText(charsequence.toString().toUpperCase());
    }
}
