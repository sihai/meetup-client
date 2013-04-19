/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class BottomActionBar extends LinearLayout {

	public BottomActionBar(Context context)
    {
        this(context, null);
    }

    public BottomActionBar(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        setOrientation(0);
    }

    public BottomActionBar(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        setOrientation(0);
    }

    public final ActionButton addButton(int i, int j, android.view.View.OnClickListener onclicklistener)
    {
        return addButton(i, getContext().getString(j), onclicklistener);
    }

    public final ActionButton addButton(int i, String s, android.view.View.OnClickListener onclicklistener)
    {
        ActionButton actionbutton = (ActionButton)LayoutInflater.from(getContext()).inflate(R.layout.bottom_action_button, this, false);
        actionbutton.setId(i);
        actionbutton.setLabel(s);
        actionbutton.setOnClickListener(onclicklistener);
        addView(actionbutton);
        return actionbutton;
    }

    public void addView(View view, int i, android.view.ViewGroup.LayoutParams layoutparams)
    {
        if((view instanceof ActionButton) && getChildCount() > 0)
            super.addView(LayoutInflater.from(getContext()).inflate(R.layout.tab_separator, this, false), -1, layoutparams);
        super.addView(view, -1, new android.widget.LinearLayout.LayoutParams(0, -1, 1.0F));
    }

    public android.widget.LinearLayout.LayoutParams generateLayoutParams(AttributeSet attributeset)
    {
        return new android.widget.LinearLayout.LayoutParams(-2, -1);
    }

    public final List getButtons()
    {
        List arraylist = new ArrayList();
        for(int i = 0; i < getChildCount(); i++)
        {
            View view = getChildAt(i);
            if(view instanceof ActionButton)
                arraylist.add((ActionButton)view);
        }

        return arraylist;
    }
}
