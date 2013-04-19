/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.activity.ConversationActivity;

/**
 * 
 * @author sihai
 *
 */
public class HangoutTileEventMessageListItemView extends RelativeLayout {

	private static boolean sInitialized = false;
    private static int sMessageColor;
    private static int sTimestampColor;
    private TextView mText;
    private TextView mTimestamp;
    
    public HangoutTileEventMessageListItemView(Context context)
    {
        this(context, null);
    }

    public HangoutTileEventMessageListItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                ((ConversationActivity)getContext()).toggleTiles();
            }

        });
        if(!sInitialized)
        {
            Resources resources = context.getApplicationContext().getResources();
            sMessageColor = resources.getColor(R.color.realtimechat_system_information_foreground);
            sTimestampColor = resources.getColor(R.color.realtimechat_system_information_timestamp);
            sInitialized = true;
        }
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
        if(i == 6)
        {
            mText.setTextColor(sMessageColor);
            mTimestamp.setTextColor(sTimestampColor);
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
            stringbuilder.append(resources.getString(R.string.realtimechat_message_description_message, new Object[] {
                charsequence1
            }));
        setContentDescription(stringbuilder.toString());
    }

}
