/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class SuggestedParticipantView extends RelativeLayout {

	 private AvatarView mAvatarView;
	 private View mHeaderView;
	 private TextView mParticipantName;
	    
	public SuggestedParticipantView(Context context)
    {
        super(context);
    }

    public SuggestedParticipantView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public final void clear()
    {
        mParticipantName.setText(null);
        mAvatarView.setGaiaId(null);
    }

    public void onFinishInflate()
    {
        mAvatarView = (AvatarView)findViewById(R.id.avatar);
        mParticipantName = (TextView)findViewById(R.id.participantName);
        mHeaderView = findViewById(R.id.sectionHeader);
    }

    public void setHeaderVisible(boolean flag)
    {
        View view = mHeaderView;
        int i;
        if(flag)
            i = 0;
        else
            i = 8;
        view.setVisibility(i);
    }

    public void setParticipantId(String s)
    {
        mAvatarView.setGaiaId(s);
    }

    public void setParticipantName(CharSequence charsequence)
    {
        mParticipantName.setText(charsequence);
    }

}
