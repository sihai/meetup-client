/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;

/**
 * 
 * @author sihai
 *
 */
public abstract class HostedFragment extends Fragment {

	private HostActionBar mActionBar;
	private boolean mCalled;
	private OzViews mEndView;
	private Bundle mEndViewExtras;
	private boolean mPaused;
	private boolean mRecorded;
	private long mStartTime;
	private OzViews mStartView;
	private Bundle mStartViewExtras;
	
	public HostedFragment() {
    }

    public final void attachActionBar(HostActionBar hostactionbar) {
        mActionBar = hostactionbar;
        onPrepareActionBar(hostactionbar);
    }

    public final void clearNavigationAction() {
        mRecorded = false;
        mStartView = null;
        mStartViewExtras = null;
        mStartTime = 0L;
        mEndView = null;
        mEndViewExtras = null;
    }

    public final void detachActionBar() {
        mActionBar = null;
    }

    public abstract EsAccount getAccount();

    public final HostActionBar getActionBar() {
        return mActionBar;
    }

    public Bundle getExtrasForLogging() {
        return null;
    }

    protected final Context getSafeContext() {
        FragmentActivity fragmentactivity = getActivity();
        Context context;
        if(fragmentactivity != null)
            context = fragmentactivity.getApplicationContext();
        else
            context = null;
        return context;
    }

    public abstract OzViews getViewForLogging();

    protected final void invalidateActionBar() {
        if(mActionBar != null)
            mActionBar.invalidateActionBar();
    }

    protected final boolean isPaused() {
        return mPaused;
    }

    protected boolean needsAsyncData() {
        return false;
    }

    public void onActionButtonClicked(int i) {
    }

    protected void onAsyncData() {
        EsAccount esaccount = getAccount();
        if(esaccount != null && !mRecorded && mEndView != null) {
            EsAnalytics.recordNavigationEvent(getActivity(), esaccount, mStartView, mEndView, Long.valueOf(mStartTime), null, mStartViewExtras, mEndViewExtras);
            mRecorded = true;
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mCalled = false;
        onSetArguments(getArguments());
        if(!mCalled)
            throw new IllegalStateException("Did you forget to call super.onSetArguments()?");
        else
            return;
    }

    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    protected void onPrepareActionBar(HostActionBar hostactionbar) {
    }

    public void onPrimarySpinnerSelectionChange(int i) {
    }

    public void onResume() {
        super.onResume();
        mPaused = false;
    }

    protected void onSetArguments(Bundle bundle) {
        mCalled = true;
    }

    public boolean onUpButtonClicked() {
        return false;
    }

    public void recordNavigationAction() {
        recordNavigationAction(null, getViewForLogging(), null, null, getExtrasForLogging());
    }

    public final void recordNavigationAction(OzViews ozviews, long l, Bundle bundle) {
        recordNavigationAction(ozviews, getViewForLogging(), Long.valueOf(l), bundle, getExtrasForLogging());
    }

    public final void recordNavigationAction(OzViews ozviews, OzViews ozviews1, Long long1, Bundle bundle, Bundle bundle1) {
        if(!needsAsyncData()) {
            EsAccount esaccount = getAccount();
            if(esaccount != null && !mRecorded && ozviews1 != null) {
                EsAnalytics.recordNavigationEvent(getActivity(), esaccount, ozviews, ozviews1, long1, null, bundle, bundle1);
                mRecorded = true;
            }
        } else {
            mStartView = ozviews;
            mStartViewExtras = bundle;
            long l;
            if(long1 == null)
                l = System.currentTimeMillis();
            else
                l = long1.longValue();
            mStartTime = l;
            mEndView = ozviews1;
            mEndViewExtras = bundle1;
        }
    }

    protected final void recordUserAction(OzActions ozactions) {
        EsAccount esaccount = getAccount();
        if(esaccount != null)
            EsAnalytics.recordActionEvent(getActivity(), esaccount, ozactions, getViewForLogging());
    }

	protected final void recordUserAction(OzActions ozactions, Bundle bundle) {
		EsAccount esaccount = getAccount();
		if (esaccount != null)
			EsAnalytics.recordActionEvent(getActivity(), esaccount, ozactions, getViewForLogging(), bundle);
	}

	public void refresh() {
		ImageCache.getInstance(getActivity()).clearFailedRequests();
	}
}
