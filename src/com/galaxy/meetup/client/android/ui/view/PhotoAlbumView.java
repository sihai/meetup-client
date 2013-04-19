/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class PhotoAlbumView extends RelativeLayout {

	private TextView mDateDisplay;
    private int mDateVisibilityState;
    private AlphaAnimation mFadeIn;
    private AlphaAnimation mFadeOut;
    private AlphaAnimation mResetToOpaque;
    
	public PhotoAlbumView(Context context)
    {
        this(context, null);
    }

    public PhotoAlbumView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mDateVisibilityState = 8;
        mFadeIn = new AlphaAnimation(0.0F, 1.0F);
        mFadeIn.setInterpolator(new DecelerateInterpolator());
        mFadeIn.setDuration(250L);
        mFadeIn.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation)
            {
            }

            public final void onAnimationRepeat(Animation animation)
            {
            }

            public final void onAnimationStart(Animation animation)
            {
                mDateDisplay.setVisibility(0);
            }

        });
        mFadeOut = new AlphaAnimation(1.0F, 0.0F);
        mFadeOut.setInterpolator(new AccelerateInterpolator());
        mFadeOut.setStartOffset(500L);
        mFadeOut.setDuration(250L);
        mFadeOut.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation)
            {
                mDateDisplay.setVisibility(4);
            }

            public final void onAnimationRepeat(Animation animation)
            {
            }

            public final void onAnimationStart(Animation animation)
            {
            }

        });
        if(android.os.Build.VERSION.SDK_INT < 11)
            mResetToOpaque = new AlphaAnimation(1.0F, 1.0F);
    }

    public final void enableDateDisplay(boolean flag)
    {
        mDateDisplay = (TextView)findViewById(R.id.date);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        if(mDateDisplay != null)
        {
            android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)mDateDisplay.getLayoutParams();
            boolean flag1;
            int i1;
            if(getResources().getConfiguration().orientation == 2)
                flag1 = true;
            else
                flag1 = false;
            i1 = ((ColumnGridView)findViewById(R.id.grid)).getColumnSize();
            if(flag1)
            {
                layoutparams.setMargins(i1 - mDateDisplay.getWidth() / 2, 0, 0, 0);
                layoutparams.addRule(8, R.id.grid);
                mDateDisplay.setBackgroundResource(R.drawable.photos_date_h);
            } else
            {
                layoutparams.setMargins(0, i1 - mDateDisplay.getHeight() / 2, 0, 0);
                layoutparams.addRule(7, R.id.grid);
                mDateDisplay.setBackgroundResource(R.drawable.photos_date_v);
            }
        }
    }

    public void setDate(String s)
    {
        mDateDisplay.setText(s);
    }

    public void setDateVisibility(int i)
    {
        if(mDateDisplay != null && mDateVisibilityState != i)
        {
            if(i == 0)
            {
                if(mFadeOut.hasEnded())
                {
                    mDateDisplay.startAnimation(mFadeIn);
                } else
                {
                    mFadeOut.cancel();
                    if(android.os.Build.VERSION.SDK_INT >= 11)
                        mDateDisplay.setAlpha(1.0F);
                    else
                        mDateDisplay.startAnimation(mResetToOpaque);
                    mDateDisplay.setVisibility(0);
                }
            } else
            {
                mFadeOut.reset();
                mDateDisplay.startAnimation(mFadeOut);
            }
            mDateVisibilityState = i;
        }
    }

}
