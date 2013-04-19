/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.controller;

import java.util.ArrayList;
import java.util.List;

import WriteReviewOperation.MediaRef;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView.OnScrollListener;
import com.galaxy.meetup.client.android.ui.view.ComposeBarView;
import com.galaxy.meetup.client.android.ui.view.ComposeBarView.OnComposeBarMeasuredListener;
import com.galaxy.meetup.client.android.ui.view.ImageResourceView;

/**
 * 
 * @author sihai
 *
 */
public class ComposeBarController implements OnClickListener, OnScrollListener,
		OnComposeBarMeasuredListener {

	private static int sActionBarHeight;
    private static Drawable sOverlayDrawable;
    private static int sRecentImagesDefaultPadding;
    private static int sRecentImagesDimension;
    private static Drawable sSelectorDrawable;
    private boolean mAlwaysHide;
    private ComposeBarListener mComposeBarListener;
    private int mCumulativeTouchSlop;
    private float mCurrentOffset;
    private int mCurrentTouchDelta;
    private View mFloatingBarView;
    private boolean mLandscape;
    private List mRecentImageRefs;
    private List mRecentImageViews;
    private int mRecentImagesMargin;
    private int mRecentImagesThatFitOnScreen;
    private int mState;
    
    private final android.view.animation.Animation.AnimationListener mSlideInListener = new android.view.animation.Animation.AnimationListener() {

        public final void onAnimationEnd(Animation animation) {
        	
        	if(1 == mState) {
        		 mState = 2;
                 mCurrentOffset = 0.0F;
        	} else if(2 == mState) {
        		
        	} else if(3 == mState) {
        		mState = 0;
                ComposeBarController composebarcontroller = ComposeBarController.this;
                float f;
                if(mLandscape)
                    f = mFloatingBarView.getWidth();
                else
                    f = mFloatingBarView.getHeight();
                composebarcontroller.mCurrentOffset = f;
        	} else {
        	}
        	
        	updateVisibility();
        }

        public final void onAnimationRepeat(Animation animation)
        {
        }

        public final void onAnimationStart(Animation animation) {
        	
        	if(0 == mState) {
        		mState = 1;
        	} else if(1 == mState) {
        		
        	} else if(2 == mState) {
        		 mState = 3;
        	} else {
        		
        	}
        	updateVisibility();
        }
    };
    
	public ComposeBarController(View view, boolean flag, ComposeBarListener composebarlistener) {
        mRecentImagesThatFitOnScreen = 10;
        mRecentImageViews = new ArrayList(10);
        mState = 0;
        mFloatingBarView = view;
        mLandscape = flag;
        mComposeBarListener = composebarlistener;
        ((ComposeBarView)mFloatingBarView).setOnComposeBarMeasuredListener(this);
        if(sRecentImagesDimension == 0) {
            Resources resources = view.getResources();
            sOverlayDrawable = resources.getDrawable(R.drawable.recent_images_border);
            sSelectorDrawable = resources.getDrawable(R.drawable.list_selected_holo);
            sRecentImagesDimension = (int)resources.getDimension(R.dimen.compose_bar_recent_images_dimension);
            sRecentImagesDefaultPadding = (int)resources.getDimension(R.dimen.compose_bar_recent_images_default_padding);
            sActionBarHeight = (int)resources.getDimension(R.dimen.host_action_bar_height);
        }
        android.content.Context context = view.getContext();
        mCumulativeTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        for(int i = 9; i >= 0; i--) {
            ImageResourceView imageresourceview = new ImageResourceView(context);
            imageresourceview.setLayoutParams(new android.widget.LinearLayout.LayoutParams(sRecentImagesDimension, sRecentImagesDimension));
            imageresourceview.setPadding(sRecentImagesDefaultPadding, sRecentImagesDefaultPadding, sRecentImagesDefaultPadding, sRecentImagesDefaultPadding);
            imageresourceview.setSizeCategory(2);
            imageresourceview.setScaleMode(1);
            imageresourceview.setFadeIn(true);
            imageresourceview.setClickable(true);
            imageresourceview.setSelector(sSelectorDrawable);
            mRecentImageViews.add(imageresourceview);
        }

    }
	
	public final void forceHide()
    {
        mAlwaysHide = true;
        mFloatingBarView.clearAnimation();
        mState = 0;
        float f;
        if(mLandscape)
            f = mFloatingBarView.getWidth();
        else
            f = mFloatingBarView.getHeight();
        startAnimation(f, 0);
        updateVisibility();
    }

    public final void forceShow()
    {
        mAlwaysHide = false;
        mFloatingBarView.clearAnimation();
        mState = 2;
        startAnimation(0.0F, 0);
        updateVisibility();
    }

    public final void onClick(View view)
    {
        boolean flag = true;
        int i = view.getId();
        if(i == R.id.compose_image_bar_close)
            dismissRecentImages(flag);
        else
        if(i == R.id.compose_image_bar_share)
        {
            ArrayList arraylist = new ArrayList();
            int j = 0;
            for(int k = mRecentImageRefs.size(); j < k; j++)
                if(((ImageResourceView)mRecentImageViews.get(j)).isSelected())
                    arraylist.add(mRecentImageRefs.get(j));

            if(mComposeBarListener != null)
                mComposeBarListener.onShareRecentImages(arraylist);
            dismissRecentImages(false);
        } else
        {
            if(view.isSelected())
                flag = false;
            view.setSelected(flag);
            updateShareButton();
        }
    }

    public final void onComposeBarMeasured(int i, int j)
    {
        int k;
        int l;
        if(mLandscape)
            k = j - sActionBarHeight;
        else
            k = i;
        l = sRecentImagesDefaultPadding + sRecentImagesDimension;
        mRecentImagesThatFitOnScreen = Math.max(0, Math.min(10, k / l));
        if(mRecentImagesThatFitOnScreen > 0)
            mRecentImagesMargin = (k - l * mRecentImagesThatFitOnScreen) / (2 * mRecentImagesThatFitOnScreen);
        else
            mRecentImagesMargin = 0;
        updateBarView();
    }

    public final void onScroll(ColumnGridView columngridview, int i, int j, int k, int l, int i1) {
    	
    	if(mAlwaysHide || j >= k) {
    		return;
    	}
    	
    	 boolean flag;
         if(i1 < 0 && mCurrentTouchDelta > 0 || i1 > 0 && mCurrentTouchDelta < 0)
             mCurrentTouchDelta = 0;
         mCurrentTouchDelta = i1 + mCurrentTouchDelta;
         if(mCurrentTouchDelta > 0)
             flag = true;
         else
             flag = false;
         if(mState == 0 && shouldShowRecentImages())
             dismissRecentImages(false);
         if(mCurrentTouchDelta <= -mCumulativeTouchSlop || mCurrentTouchDelta >= mCumulativeTouchSlop)
         {
             if(!(mState != 0 && mState != 3 || !flag))
            	 startAnimation(0.0F, 200);
             else if(!(mState != 2 && mState != 1 || flag)) {
            	 if(mLandscape)
                 {
                     startAnimation(mFloatingBarView.getWidth(), 200);
                     mCurrentOffset = mFloatingBarView.getWidth();
                 } else
                 {
                     startAnimation(mFloatingBarView.getHeight(), 200);
                     mCurrentOffset = mFloatingBarView.getHeight();
                 }
             }
         }
         
         updateVisibility();
    }

    public final void onScrollStateChanged(ColumnGridView columngridview, int i)
    {
        if(!mAlwaysHide && i != 1)
            mCurrentTouchDelta = 0;
    }

    public final void setRecentImageRefs(ArrayList arraylist)
    {
        mRecentImageRefs = arraylist;
        updateBarView();
        updateShareButton();
    }
	
	private void dismissRecentImages(boolean flag)
    {
        setRecentImageRefs(null);
        if(mComposeBarListener != null)
            mComposeBarListener.onDismissRecentImages(flag);
    }

    private android.widget.LinearLayout.LayoutParams getRecentImagesLayoutParams()
    {
        android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(sRecentImagesDimension, sRecentImagesDimension, 1.0F);
        if(mLandscape)
            layoutparams.setMargins(0, mRecentImagesMargin, 0, mRecentImagesMargin);
        else
            layoutparams.setMargins(mRecentImagesMargin, 0, mRecentImagesMargin, 0);
        return layoutparams;
    }

    private boolean shouldShowRecentImages()
    {
        boolean flag;
        if(mRecentImageRefs != null && mRecentImageRefs.size() > 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void startAnimation(float f, int i)
    {
        TranslateAnimation translateanimation;
        if(mLandscape)
            translateanimation = new TranslateAnimation(mCurrentOffset, f, 0.0F, 0.0F);
        else
            translateanimation = new TranslateAnimation(0.0F, 0.0F, mCurrentOffset, f);
        translateanimation.setDuration(i);
        translateanimation.setFillAfter(true);
        if(i > 0)
            translateanimation.setAnimationListener(mSlideInListener);
        mFloatingBarView.startAnimation(translateanimation);
    }

    private void updateBarView()
    {
        byte byte0 = 8;
        View view = mFloatingBarView.findViewById(R.id.compose_image_bar);
        if(mRecentImageRefs == null || mRecentImageViews == null)
        {
            view.setVisibility(byte0);
        } else
        {
            boolean flag = shouldShowRecentImages();
            if(flag)
                byte0 = 0;
            view.setVisibility(byte0);
            LinearLayout linearlayout = (LinearLayout)view.findViewById(R.id.compose_image_container);
            linearlayout.removeAllViews();
            View view1 = mFloatingBarView.findViewById(R.id.compose_image_bar_close);
            ComposeBarController composebarcontroller;
            if(flag)
                composebarcontroller = this;
            else
                composebarcontroller = null;
            view1.setOnClickListener(composebarcontroller);
            if(flag)
            {
                int i = Math.min(mRecentImageRefs.size(), mRecentImagesThatFitOnScreen);
                for(int j = 0; j < i; j++)
                {
                    ImageResourceView imageresourceview2 = (ImageResourceView)mRecentImageViews.get(j);
                    imageresourceview2.onRecycle();
                    imageresourceview2.setMediaRef((MediaRef)mRecentImageRefs.get(j));
                    imageresourceview2.setOnClickListener(this);
                    imageresourceview2.setOverlay(sOverlayDrawable);
                    linearlayout.addView(imageresourceview2, getRecentImagesLayoutParams());
                }

                for(int k = i; k < mRecentImagesThatFitOnScreen; k++)
                {
                    ImageResourceView imageresourceview1 = (ImageResourceView)mRecentImageViews.get(k);
                    imageresourceview1.onRecycle();
                    imageresourceview1.setBackgroundResource(R.drawable.empty_recent_image);
                    imageresourceview1.setOnClickListener(null);
                    imageresourceview1.setSelected(false);
                    linearlayout.addView(imageresourceview1, getRecentImagesLayoutParams());
                }

                for(int l = Math.max(mRecentImageRefs.size(), mRecentImagesThatFitOnScreen); l < 10; l++)
                {
                    ImageResourceView imageresourceview = (ImageResourceView)mRecentImageViews.get(l);
                    imageresourceview.onRecycle();
                    imageresourceview.setBackgroundResource(0);
                    imageresourceview.setOnClickListener(null);
                    imageresourceview.setSelected(false);
                }

            }
            view.invalidate();
        }
    }

    private void updateShareButton() {
    	
    	if(null == mRecentImageRefs || null == mRecentImageViews) {
    		return;
    	}
    	
        int j;
        j = mRecentImageRefs.size();
        
        boolean flag = false;
        for(int i = 0; i < j; i++) {
        	if(((ImageResourceView)mRecentImageViews.get(i)).isSelected()) {
        		flag = true;
        		break;
        	}
        }
        
        Button button = (Button)mFloatingBarView.findViewById(R.id.compose_image_bar_share);
        int k;
        if(flag)
            k = R.color.compose_bar_share_button_enabled;
        else
            k = R.color.compose_bar_share_button_disabled;
        button.setEnabled(flag);
        button.setTextColor(button.getResources().getColor(k));
        button.setOnClickListener(this);
    }
    
	private void updateVisibility() {
        boolean flag;
        View view;
        int i;
        if(mState == 0)
            flag = false;
        else
        if(mLandscape && mFloatingBarView.getWidth() > 0 && mCurrentOffset > (float)mFloatingBarView.getWidth() || !mLandscape && mFloatingBarView.getHeight() > 0 && mCurrentOffset > (float)mFloatingBarView.getHeight())
            flag = false;
        else
            flag = true;
        view = mFloatingBarView;
        if(flag)
            i = 0;
        else
            i = 8;
        view.setVisibility(i);
        mFloatingBarView.setClickable(flag);
        mFloatingBarView.findViewById(R.id.compose_post).setClickable(flag);
        mFloatingBarView.findViewById(R.id.compose_photos).setClickable(flag);
        mFloatingBarView.findViewById(R.id.compose_location).setClickable(flag);
        mFloatingBarView.findViewById(R.id.compose_custom).setClickable(flag);
        mFloatingBarView.findViewById(R.id.compose_image_bar_share).setClickable(flag);
        mFloatingBarView.findViewById(R.id.compose_image_bar_close).setClickable(flag);
        for(int j = -1 + mRecentImageViews.size(); j >= 0; j--)
            ((ImageResourceView)mRecentImageViews.get(j)).setClickable(flag);

        if(flag)
            mFloatingBarView.requestLayout();
    }
	
	public static interface ComposeBarListener {

		void onDismissRecentImages(boolean flag);

		void onShareRecentImages(ArrayList arraylist);
	}
}
