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
import com.galaxy.meetup.client.android.content.EsPeopleData;

/**
 * 
 * @author sihai
 *
 */
public class SuggestedPeopleListItemView extends RelativeLayout {

	private AvatarView mAvatarView;
    private View mCheckIndicator;
    private TextView mNameTextView;
    private String mPersonId;
    private int mPosition;
    
	public SuggestedPeopleListItemView(Context context)
    {
        this(context, null);
    }

    public SuggestedPeopleListItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public void onFinishInflate()
    {
        mAvatarView = (AvatarView)findViewById(R.id.avatar);
        mNameTextView = (TextView)findViewById(R.id.name);
        mCheckIndicator = findViewById(R.id.check_indicator);
    }

    public void setChecked(boolean flag)
    {
        View view = mCheckIndicator;
        int i;
        if(flag)
            i = 0;
        else
            i = 4;
        view.setVisibility(i);
    }

    public void setParticipantName(String s)
    {
        if(s == null)
            mNameTextView.setText(null);
        else
            mNameTextView.setText(s);
    }

    public void setPersonId(String s)
    {
        mPersonId = s;
        mAvatarView.setGaiaId(EsPeopleData.extractGaiaId(s));
    }

    public void setPosition(int i)
    {
        mPosition = i;
    }

}
