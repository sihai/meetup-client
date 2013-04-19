/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class SystemMessageListItemView extends RelativeLayout {

	private static int sErrorBackgroundColor;
    private static int sErrorMessageColor;
    private static int sErrorTimestampColor;
    private static int sInfoBackgroundColor;
    private static int sInfoMessageColor;
    private static int sInfoTimestampColor;
    private static boolean sInitialized = false;
    private TextView mText;
    private TextView mTimestamp;
    
    public SystemMessageListItemView(Context context)
    {
        this(context, null);
    }

    public SystemMessageListItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        if(!sInitialized)
        {
            Resources resources = context.getApplicationContext().getResources();
            sInfoMessageColor = resources.getColor(R.color.realtimechat_system_information_foreground);
            sInfoTimestampColor = resources.getColor(R.color.realtimechat_system_information_timestamp);
            sInfoBackgroundColor = resources.getColor(R.color.realtimechat_system_information_background);
            sErrorMessageColor = resources.getColor(R.color.realtimechat_system_error_foreground);
            sErrorTimestampColor = resources.getColor(R.color.realtimechat_system_error_timestamp);
            sErrorBackgroundColor = resources.getColor(R.color.realtimechat_system_error_background);
            sInitialized = true;
        }
    }

    public final CharSequence getText()
    {
        return mText.getText();
    }

    public void onFinishInflate()
    {
        mTimestamp = (TextView)findViewById(R.id.timestamp);
        mText = (TextView)findViewById(R.id.text);
    }

    public void setText(CharSequence charsequence)
    {
        mText.setText(Html.fromHtml((String)charsequence));
    }

    public void setTimeSince(CharSequence charsequence)
    {
        mTimestamp.setText(charsequence);
    }

    public void setType(int i)
    {
        if(i == 4)
        {
            setBackgroundColor(sErrorBackgroundColor);
            mText.setTextColor(sErrorMessageColor);
            mTimestamp.setTextColor(sErrorTimestampColor);
        } else
        {
            setBackgroundColor(sInfoBackgroundColor);
            mText.setTextColor(sInfoMessageColor);
            mTimestamp.setTextColor(sInfoTimestampColor);
        }
    }

    public final void updateContentDescription()
    {
        StringBuilder stringbuilder = new StringBuilder();
        Resources resources = getResources();
        CharSequence charsequence = mTimestamp.getText();
        if(charsequence != null && charsequence.length() > 0)
        {
            stringbuilder.append(resources.getString(R.string.realtimechat_message_description_time_since, new Object[] {
                charsequence
            }));
            stringbuilder.append(" ");
        }
        CharSequence charsequence1 = mText.getText();
        if(charsequence1 != null && charsequence1.length() > 0)
            stringbuilder.append(resources.getString(R.string.realtimechat_message_description_system_message, new Object[] {
                charsequence1
            }));
        setContentDescription(stringbuilder.toString());
    }
}
