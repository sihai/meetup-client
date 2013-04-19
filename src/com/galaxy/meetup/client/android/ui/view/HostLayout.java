/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.HostedFragment;
import com.galaxy.meetup.client.util.SoftInput;

public class HostLayout extends FrameLayout implements HostActionBar.HostActionBarListener, SlidingPanelLayout.OnSlidingPanelStateChange {

	private HostActionBar mActionBar;
    private FragmentManager mFragmentManager;
    private HostLayoutListener mListener;
    private ListView mNavigationBar;
    private int mNavigationItemHeight;
    private SlidingPanelLayout mPanel;
    private View mSlidingBackground;
    
	//===========================================================================
    //						Constructor
    //===========================================================================
	public HostLayout(Context context) {
        super(context);
        FragmentActivity fragmentactivity = (FragmentActivity)getContext();
        mFragmentManager = fragmentactivity.getSupportFragmentManager();
        int ai[] = new int[1];
        ai[0] = R.attr.navigationItemHeight;
        TypedArray typedarray = fragmentactivity.obtainStyledAttributes(ai);
        mNavigationItemHeight = typedarray.getDimensionPixelSize(0, 1);
        typedarray.recycle();
    }

    public HostLayout(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        FragmentActivity fragmentactivity = (FragmentActivity)getContext();
        mFragmentManager = fragmentactivity.getSupportFragmentManager();
        int ai[] = new int[1];
        ai[0] = R.attr.navigationItemHeight;
        TypedArray typedarray = fragmentactivity.obtainStyledAttributes(ai);
        mNavigationItemHeight = typedarray.getDimensionPixelSize(0, 1);
        typedarray.recycle();
    }

    public HostLayout(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        FragmentActivity fragmentactivity = (FragmentActivity)getContext();
        mFragmentManager = fragmentactivity.getSupportFragmentManager();
        int ai[] = new int[1];
        ai[0] = R.attr.navigationItemHeight;
        TypedArray typedarray = fragmentactivity.obtainStyledAttributes(ai);
        mNavigationItemHeight = typedarray.getDimensionPixelSize(0, 1);
        typedarray.recycle();
    }
    
    public final void attachActionBar() {
        HostedFragment hostedfragment = getCurrentHostedFragment();
        if(hostedfragment != null) {
            mActionBar.reset();
            hostedfragment.attachActionBar(mActionBar);
            mActionBar.commit();
        }
    }

    public final HostActionBar getActionBar() {
        return mActionBar;
    }

    public final int getCollapsedMenuItemCount() {
        WindowManager windowmanager = (WindowManager)getContext().getSystemService("window");
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowmanager.getDefaultDisplay().getMetrics(displaymetrics);
        return (int)((0.6F * (float)displaymetrics.heightPixels) / (float)mNavigationItemHeight);
    }

    public final HostedFragment getCurrentHostedFragment() {
        return (HostedFragment)mFragmentManager.findFragmentByTag("hosted");
    }

    public final View getNavigationBar() {
        return mNavigationBar;
    }

    public final void hideNavigationBar() {
        if(mPanel.isOpen()) {
            if(mListener != null)
                mListener.onNavigationBarVisibilityChange(false);
            mPanel.close();
        }
    }

    public final boolean isNavigationBarVisible() {
        return mPanel.isOpen();
    }

    public final void onActionBarInvalidated() {
        attachActionBar();
    }

    public final void onActionButtonClicked(int i) {
        HostedFragment hostedfragment = getCurrentHostedFragment();
        if(hostedfragment != null)
            hostedfragment.onActionButtonClicked(i);
    }

    public final void onAttachFragment(HostedFragment hostedfragment) {
        hostedfragment.attachActionBar(mActionBar);
        mActionBar.commit();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        mActionBar = (HostActionBar)findViewById(R.id.title_bar);
        mActionBar.setHostActionBarListener(this);
        mNavigationBar = (ListView)findViewById(R.id.navigation_bar);
        mSlidingBackground = findViewById(R.id.fragment_sliding_background);
        mPanel = (SlidingPanelLayout)findViewById(R.id.panel);
        mPanel.setOnSlidingPanelStateChange(this);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        super.onLayout(flag, i, j, k, l);
        final int visibility;
        if(mPanel.isOpen())
            visibility = View.VISIBLE;
        else
            visibility = View.GONE;
        mNavigationBar.setVisibility(visibility);
        
        mSlidingBackground.post(new Runnable() {

            public final void run() {
                mSlidingBackground.setVisibility(visibility);
            }
        });
        if(visibility == View.VISIBLE)
            mNavigationBar.layout(0, 0, mNavigationBar.getMeasuredWidth(), mNavigationBar.getMeasuredHeight());
    }

    protected void onMeasure(int i, int j) {
        super.onMeasure(i, j);
        int k = getMeasuredHeight();
        if(mNavigationBar.getVisibility() == View.VISIBLE) {
            int l = mPanel.getNavigationBarWidth();
            mNavigationBar.measure(android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(k, 0x40000000));
        }
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem) {
        HostedFragment hostedfragment = getCurrentHostedFragment();
        boolean flag;
        if(hostedfragment != null)
            flag = hostedfragment.onOptionsItemSelected(menuitem);
        else
            flag = false;
        return flag;
    }

    public final void onPanelClosed() {
        mNavigationBar.setVisibility(View.GONE);
        mSlidingBackground.post(new Runnable() {

            public final void run() {
                mSlidingBackground.setVisibility(View.GONE);
            }
        });
        if(mListener != null)
            mListener.onNavigationBarVisibilityChange(false);
    }

    public final void onPrepareOptionsMenu(Menu menu) {
        HostedFragment hostedfragment = getCurrentHostedFragment();
        if(hostedfragment != null)
            hostedfragment.onPrepareOptionsMenu(menu);
    }

    public final void onPrimarySpinnerSelectionChange(int i) {
        HostedFragment hostedfragment = getCurrentHostedFragment();
        if(hostedfragment != null)
            hostedfragment.onPrimarySpinnerSelectionChange(i);
    }

    public final void onRefreshButtonClicked() {
        HostedFragment hostedfragment = getCurrentHostedFragment();
        if(hostedfragment != null)
            hostedfragment.refresh();
    }

    public final Fragment.SavedState saveHostedFragmentState() {
        HostedFragment hostedfragment = getCurrentHostedFragment();
        Fragment.SavedState savedstate;
        if(hostedfragment != null)
            savedstate = mFragmentManager.saveFragmentInstanceState(hostedfragment);
        else
            savedstate = null;
        return savedstate;
    }

    public void setListener(HostLayoutListener hostlayoutlistener) {
        mListener = hostlayoutlistener;
    }

    public final void showFragment(HostedFragment hostedfragment, boolean flag, Fragment.SavedState savedstate) {
        HostedFragment hostedfragment1 = getCurrentHostedFragment();
        OzViews ozviews;
        android.os.Bundle bundle;
        long l;
        FragmentTransaction fragmenttransaction;
        if(hostedfragment1 != null) {
            ozviews = hostedfragment1.getViewForLogging();
            bundle = hostedfragment1.getExtrasForLogging();
            hostedfragment1.detachActionBar();
        } else {
            ozviews = null;
            bundle = null;
        }
        l = System.currentTimeMillis();
        mActionBar.reset();
        if(savedstate != null)
            hostedfragment.setInitialSavedState(savedstate);
        fragmenttransaction = mFragmentManager.beginTransaction();
        fragmenttransaction.replace(R.id.fragment_container, hostedfragment, "hosted");
        if(flag)
            fragmenttransaction.setTransition(4099);
        else
            fragmenttransaction.setTransition(0);
        fragmenttransaction.commitAllowingStateLoss();
        hideNavigationBar();
        mFragmentManager.executePendingTransactions();
        if(ozviews == null)
            hostedfragment.recordNavigationAction();
        else
            hostedfragment.recordNavigationAction(ozviews, l, bundle);
    }

    public final void showNavigationBar() {
        if(!mPanel.isOpen()) {
            if(mListener != null)
                mListener.onNavigationBarVisibilityChange(true);
            mActionBar.dismissPopupMenus();
            View view = mActionBar.getRootView();
            if(view != null) {
                View view1 = view.findFocus();
                if(view1 != null)
                    SoftInput.hide(view1);
            }
            mNavigationBar.setVisibility(View.VISIBLE);
            mSlidingBackground.post(new Runnable() {

                public final void run() {
                    mSlidingBackground.setVisibility(View.VISIBLE);
                }
            });
            mPanel.open();
        }
    }

    public final void showNavigationBarDelayed() {
        postDelayed(new Runnable() {

            public final void run() {
                showNavigationBar();
            }
        }, 500L);
    }

    public final void toggleNavigationBarVisibility() {
        if(mPanel.isOpen())
            hideNavigationBar();
        else
            showNavigationBar();
    }

	
	//===========================================================================
    //						Inner class
    //===========================================================================
	public static interface HostLayoutListener {

		void onNavigationBarVisibilityChange(boolean flag);
	}
}
