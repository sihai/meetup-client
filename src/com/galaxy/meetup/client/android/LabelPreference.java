/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


/**
 * 
 * @author sihai
 *
 */
public class LabelPreference extends Preference {

	private CharSequence mLabel;
    private int mLabelColor;
    
	public LabelPreference(Context context)
    {
        super(context);
        mLabelColor = -1;
        setLayoutResource(R.layout.label_preference);
    }

    public LabelPreference(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mLabelColor = -1;
        setLayoutResource(R.layout.label_preference);
    }

    public LabelPreference(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mLabelColor = -1;
        setLayoutResource(R.layout.label_preference);
    }

    protected void onBindView(View view)
    {
        super.onBindView(view);
        TextView textview = (TextView)view.findViewById(R.id.label);
        if(textview != null)
            if(TextUtils.isEmpty(mLabel))
            {
                textview.setVisibility(8);
            } else
            {
                textview.setVisibility(0);
                textview.setTextColor(mLabelColor);
                textview.setText(mLabel);
            }
    }

    public final void setLabel(CharSequence charsequence)
    {
        if(charsequence == null && mLabel != null || charsequence != null && !charsequence.equals(mLabel))
        {
            mLabel = charsequence;
            notifyChanged();
        }
    }

    public final void setLabelColor(int i)
    {
        if(i != mLabelColor)
        {
            mLabelColor = i;
            notifyChanged();
        }
    }

}
