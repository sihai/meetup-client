/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.galaxy.meetup.client.android.analytics.AnalyticsInfo;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public abstract class BaseActivity extends FragmentActivity {
	
	private boolean mExited;
    private boolean mRecorded;
    private boolean mStartingActivity;
    
    public abstract OzViews getViewForLogging();
    
    //===========================================================================
    //						Constructor
    //===========================================================================
    public BaseActivity() {
    }
    
    //===========================================================================
    //						Abstract functions
    //===========================================================================
    protected abstract EsAccount getAccount();
    
    //===========================================================================
    //						Public functions
    //===========================================================================
    public static boolean isFromThirdPartyApp(Intent intent) {
        boolean flag = false;
        if(intent != null)
            flag = intent.getBooleanExtra("com.google.plus.analytics.intent.extra.FROM_THIRD_PARTY_APP", false);
        return flag;
    }
    
    public final AnalyticsInfo getAnalyticsInfo()
    {
        return new AnalyticsInfo(getStartView(getIntent()), getViewForLogging(), getStartTime(getIntent()));
    }
    
    public final void startExternalActivity(Intent intent)
    {
        intent.addFlags(0x80000);
        startActivity(intent);
    }
    
    public static void recordViewNavigation(Activity activity, EsAccount esaccount, OzViews ozviews)
    {
        Intent intent = activity.getIntent();
        OzViews ozviews1 = getStartView(intent);
        Bundle bundle = intent.getBundleExtra("com.google.plus.analytics.intent.extra.EXTRA_START_VIEW_EXTRAS");
        long l = getStartTime(intent);
        Bundle bundle1;
        if(intent != null && intent.getBooleanExtra("com.google.plus.analytics.intent.extra.FROM_THIRD_PARTY_APP", false))
        {
            bundle1 = new Bundle();
            bundle1.putBoolean("extra_platform_event", true);
        } else
        {
            bundle1 = null;
        }
        EsAnalytics.recordNavigationEvent(activity, esaccount, ozviews1, ozviews, Long.valueOf(l), null, bundle, null, bundle1);
    }
    
    public static void recordReverseViewNavigation(Activity activity, EsAccount esaccount, OzViews ozviews, Bundle bundle)
    {
        Intent intent = activity.getIntent();
        OzViews ozviews1 = getStartView(intent);
        if(ozviews1 != null)
        {
            Bundle bundle1 = intent.getBundleExtra("com.google.plus.analytics.intent.extra.EXTRA_START_VIEW_EXTRAS");
            Bundle bundle2;
            if(intent != null && intent.getBooleanExtra("com.google.plus.analytics.intent.extra.FROM_THIRD_PARTY_APP", false))
            {
                bundle2 = new Bundle();
                bundle2.putBoolean("extra_platform_event", true);
            } else
            {
                bundle2 = null;
            }
            EsAnalytics.recordNavigationEvent(activity, esaccount, ozviews, ozviews1, null, null, bundle, bundle1, bundle2);
        }
    }
    
    public final void onAsyncData()
    {
        recordViewNavigation();
    }
    
    //===========================================================================
    //						Protected functions
    //===========================================================================
    protected void replaceFragment(Fragment fragment)
    {
        int i = getDefaultFragmentContainerViewId();
        fragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentmanager = getSupportFragmentManager();
        FragmentTransaction fragmenttransaction = fragmentmanager.beginTransaction();
        fragmenttransaction.replace(i, fragment, "default");
        fragmenttransaction.setTransition(0);
        fragmenttransaction.commitAllowingStateLoss();
        fragmentmanager.executePendingTransactions();
    }
    
    protected final void recordUserAction(AnalyticsInfo analyticsinfo, OzActions ozactions)
    {
        recordUserAction(analyticsinfo, ozactions, null);
    }

    protected final void recordUserAction(AnalyticsInfo analyticsinfo, OzActions ozactions, Bundle bundle)
    {
        EsAccount esaccount = getAccount();
        if(esaccount != null)
            EsAnalytics.recordEvent(this, esaccount, analyticsinfo, ozactions, bundle);
    }

    protected final void recordUserAction(OzActions ozactions)
    {
        EsAccount esaccount = getAccount();
        if(esaccount != null)
            EsAnalytics.recordActionEvent(this, esaccount, ozactions, getViewForLogging());
    }
    
    protected int getDefaultFragmentContainerViewId()
    {
        return 0x1020002;
    }
    
    //===========================================================================
    //						Private functions
    //===========================================================================
    private static long getStartTime(Intent intent) {
        return intent.getLongExtra("com.galaxy.meetup.client.analytics.intent.extra.START_TIME", System.currentTimeMillis());
    }

    private static OzViews getStartView(Intent intent) {
        return OzViews.valueOf(intent.getIntExtra("com.galaxy.meetup.client.analytics.intent.extra.START_VIEW", -1));
    }

    private Intent instrument(Intent intent) {
        OzViews ozviews = getViewForLogging();
        if(ozviews == null) {
        	return intent;
        }
        Intent intent2 = new Intent(intent);
        intent2.putExtra("com.galaxy.meetup.client.analytics.intent.extra.START_VIEW", ozviews.ordinal());
        intent2.putExtra("com.galaxy.meetup.client.analytics.intent.extra.START_TIME", System.currentTimeMillis());
        if(ozviews.equals(OzViews.PLATFORM_THIRD_PARTY_APP) || getIntent().getBooleanExtra("com.galaxy.meetup.client.analytics.intent.extra.FROM_THIRD_PARTY_APP", false))
            intent2.putExtra("com.galaxy.meetup.client.analytics.intent.extra.FROM_THIRD_PARTY_APP", true);
        Bundle bundle = getExtrasForLogging();
        if(bundle != null && !bundle.isEmpty())
            intent2.putExtras(bundle);
        intent = intent2;
        return intent;
    }
    
    private void recordViewNavigation() {
        if(!mRecorded) {
        	OzViews ozviews = getViewForLogging();
            EsAccount esaccount = getAccount();
            if(esaccount != null && ozviews != null)
            {
                recordViewNavigation(((Activity) (this)), esaccount, ozviews);
                mRecorded = true;
            }
        }
    }
    
    protected Bundle getExtrasForLogging() {
        return null;
    }
}
