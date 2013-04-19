/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class TabButton extends Button {

	public TabButton(Context context)
    {
        this(context, null);
    }

    public TabButton(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, R.style.Tab);
    }

    public TabButton(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        CharSequence charsequence = getText();
        if(charsequence != null)
            setText(charsequence.toString().toUpperCase());
    }
}
