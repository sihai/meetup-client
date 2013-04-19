/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class EmotiShareView {

	private final Context mContext;
    private final ImageResourceView mImageView;
    private final View mMainView;
    private final ImageView mMissingImageView;
    private final View mSelector;
    
    public EmotiShareView(Context context)
    {
        mContext = context;
        mMainView = LayoutInflater.from(mContext).inflate(R.layout.emotishare_view, null, false);
        mImageView = (ImageResourceView)mMainView.findViewById(R.id.image_view);
        mMissingImageView = (ImageView)mMainView.findViewById(R.id.missing_image_view);
        mImageView.setScaleMode(0);
        mSelector = mMainView.findViewById(R.id.selector_view);
    }

    public final ImageResourceView getImageView()
    {
        return mImageView;
    }

    public final ImageView getMissingImageView()
    {
        return mMissingImageView;
    }

    public final View getView()
    {
        return mMainView;
    }

    public final void setMediaRef(MediaRef mediaref)
    {
        mImageView.setImageResourceFlags(4);
        mImageView.setMediaRef(mediaref);
    }

    public final void setOnClickListener(android.view.View.OnClickListener onclicklistener)
    {
        mSelector.setOnClickListener(onclicklistener);
    }
}
