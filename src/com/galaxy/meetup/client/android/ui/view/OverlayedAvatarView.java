/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class OverlayedAvatarView extends RelativeLayout {

	private AvatarView mAvatarView;
    private ImageView mOverlay;
    private ImageView mTypeOverlay;
    
    public OverlayedAvatarView(Context context)
    {
        this(context, null);
    }

    public OverlayedAvatarView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public OverlayedAvatarView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public static OverlayedAvatarView create(LayoutInflater layoutinflater, ViewGroup viewgroup)
    {
        return (OverlayedAvatarView)layoutinflater.inflate(R.layout.participant_tray_avatar_view, viewgroup, false);
    }

    public void onFinishInflate()
    {
        mAvatarView = (AvatarView)findViewById(R.id.avatar_image);
        mTypeOverlay = (ImageView)findViewById(R.id.type_overlay);
        mOverlay = (ImageView)findViewById(R.id.overlay);
    }

    public void setBorderResource(int i)
    {
        setBackgroundResource(i);
        invalidate();
    }

    public void setOverlayResource(int i)
    {
        if(i == 0)
        {
            mOverlay.setVisibility(8);
        } else
        {
            mOverlay.setVisibility(0);
            mOverlay.setBackgroundResource(i);
        }
        invalidate();
    }

    public void setParticipantGaiaId(String s)
    {
        mAvatarView.setGaiaId(s);
    }

    public void setParticipantType(int i) {
    	
    	byte byte0 = 4;
    	
    	switch(i) {
    	case 1:
    		mTypeOverlay.setImageResource(R.drawable.ic_profile_invited);
            byte0 = 0;
    		break;
    	case 2:
    		mTypeOverlay.setImageResource(R.drawable.ic_profile_sms);
            byte0 = 0;
    		break;
    	case 3:
    	case 4:
    	default:
    		byte0 = 4;
    		break;
    	}
    	mTypeOverlay.setVisibility(byte0);
    }
}
