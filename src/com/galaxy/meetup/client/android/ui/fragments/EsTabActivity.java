/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.view.TabContainer;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public abstract class EsTabActivity extends EsFragmentActivity implements
		OnClickListener, TabContainer.OnTabChangeListener {

	private int mCurrentTab;
    private ProgressBar mProgressBar;
    private boolean mSwipeEnabled;
    private TabContainer mTabContainer;
    private int mTabContainerId;
    private final ArrayList mTabs = new ArrayList();
    
    protected EsTabActivity(int i, int j)
    {
        mSwipeEnabled = true;
        mCurrentTab = 0;
        mTabContainerId = j;
    }

    private void createTab(int i)
    {
        Tab tab = getTab(i);
        tab.fragment = onCreateTab(i);
        FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
        fragmenttransaction.setTransition(0);
        String s = (new StringBuilder("tab.")).append(Integer.toString(i)).toString();
        fragmenttransaction.add(tab.containerView.getId(), tab.fragment, s);
        fragmenttransaction.commitAllowingStateLoss();
    }

    private Tab getTab(int i)
    {
        for(; i >= mTabs.size(); mTabs.add(new Tab()));
        return (Tab)mTabs.get(i);
    }

    protected static int getTabIndexForFragment(Fragment fragment)
    {
        String s = fragment.getTag();
        if(s == null || !s.startsWith("tab.")) 
        	return -1; 
        else {
        	try {
        		return Integer.parseInt(s.substring(4));
        	} catch (NumberFormatException numberformatexception) {
        		if(EsLog.isLoggable("EsEvents", 5))
                    Log.w("EsEvents", (new StringBuilder("Unknown format for fragment tag; ")).append(s).toString());
        	}
        	return -1;
        }
    }

    private void onPrepareSelectedTab()
    {
        Fragment fragment = getTab(mCurrentTab).fragment;
        int _tmp = mCurrentTab;
        if(fragment instanceof Refreshable)
            ((Refreshable)fragment).setProgressBar(mProgressBar);
    }

    private void selectTab(int i)
    {
        if(i != mCurrentTab)
        {
            if(mCurrentTab != -1)
            {
                Fragment fragment = getTab(mCurrentTab).fragment;
                int _tmp = mCurrentTab;
                if(fragment instanceof Refreshable)
                {
                    if(mProgressBar != null)
                        mProgressBar.setVisibility(8);
                    ((Refreshable)fragment).setProgressBar(null);
                }
            }
            int _tmp1 = mCurrentTab;
            EsAccount esaccount = getAccount();
            if(esaccount != null)
            {
                OzViews ozviews = getViewForLogging();
                OzViews ozviews1 = getViewForLogging();
                if(ozviews1 != ozviews)
                    EsAnalytics.recordNavigationEvent(this, esaccount, ozviews, ozviews1, null, null, null, null);
            }
            mCurrentTab = i;
            updateViewVisibility();
            onPrepareSelectedTab();
        }
    }

    private void updateViewVisibility()
    {
        if(mTabContainer == null)
        {
            mTabContainer = (TabContainer)findViewById(mTabContainerId);
            mTabContainer.setScrollEnabled(mSwipeEnabled);
            mTabContainer.setOnTabChangeListener(this);
        }
        mTabContainer.setSelectedPanel(mCurrentTab);
        int i = 0;
        while(i < mTabs.size()) 
        {
            Tab tab = (Tab)mTabs.get(i);
            if(i == mCurrentTab)
            {
                tab.tabButton.setSelected(true);
                tab.containerView.setVisibility(0);
                if(tab.fragment == null)
                    createTab(i);
            } else
            {
                tab.tabButton.setSelected(false);
            }
            i++;
        }
    }

    protected final void addTab(int i, int j, int k)
    {
        Tab tab = getTab(i);
        tab.tabButton = findViewById(j);
        tab.tabButton.setOnClickListener(this);
        tab.containerView = findViewById(k);
    }

    protected final void onAttachFragment(int i, Fragment fragment)
    {
        getTab(i).fragment = fragment;
        int _tmp = mCurrentTab;
    }

    public void onClick(View view) {
    	int size = mTabs.size();
        for(int i = 0; i < size; i++) {
        	if(view == ((Tab)mTabs.get(i)).tabButton) {
        		selectTab(i);
        		break;
        	}
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            getMenuInflater().inflate(R.menu.progress_bar_menu, menu);
            mProgressBar = (ProgressBar)menu.findItem(R.id.action_bar_progress_spinner).getActionView().findViewById(R.id.action_bar_progress_spinner_view);
            if(mCurrentTab != -1)
            {
                Fragment fragment = getTab(mCurrentTab).fragment;
                if(fragment instanceof Refreshable)
                    ((Refreshable)fragment).setProgressBar(mProgressBar);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected abstract Fragment onCreateTab(int i);

    protected void onRestoreInstanceState(Bundle bundle)
    {
        super.onRestoreInstanceState(bundle);
        mCurrentTab = bundle.getInt("currentTab");
    }

    public void onResume()
    {
        super.onResume();
        updateViewVisibility();
        if(mCurrentTab != -1)
        {
            if(android.os.Build.VERSION.SDK_INT < 11 && mProgressBar == null)
                mProgressBar = (ProgressBar)findViewById(R.id.progress_spinner);
            onPrepareSelectedTab();
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("currentTab", mCurrentTab);
    }

    public final void onTabSelected(int i)
    {
        selectTab(i);
    }

    public final void onTabVisibilityChange(int i, boolean flag)
    {
        Tab tab = (Tab)mTabs.get(i);
        View view = tab.containerView;
        int j;
        if(flag)
            j = 0;
        else
            j = 4;
        view.setVisibility(j);
        if(flag && tab.fragment == null)
            createTab(i);
    }
    
    //==================================================================================================================
    //										Inner class
    //==================================================================================================================
    private static final class Tab
    {

        public View containerView;
        public Fragment fragment;
        public View tabButton;
    }
}
