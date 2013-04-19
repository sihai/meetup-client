/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.HostedStreamOneUpFragment;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpActivity extends HostActivity {

	private HostedStreamOneUpFragment mFragment;
    private boolean mFullScreen;
    private boolean mKeyboardIsVisible;
    private Set mScreenListeners;
    
    public static interface OnScreenListener
    {

        public abstract void enableImageTransforms(boolean flag);

        public abstract void onFullScreenChanged(boolean flag);
    }


    public StreamOneUpActivity()
    {
        mScreenListeners = new HashSet();
    }

    public final void addScreenListener(OnScreenListener onscreenlistener)
    {
        mScreenListeners.add(onscreenlistener);
    }

    protected final Fragment createDefaultFragment()
    {
        return new HostedStreamOneUpFragment();
    }

    protected final int getContentView()
    {
        return R.layout.host_frame_layout_activity;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.ACTIVITY;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
        if(fragment instanceof HostedStreamOneUpFragment)
            mFragment = (HostedStreamOneUpFragment)fragment;
    }

    public void onBackPressed()
    {
        if(mFullScreen)
            toggleFullScreen();
        else
            mFragment.onCancelRequested();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        final View rootView;
        if(bundle == null)
        {
            String s = getIntent().getStringExtra("notif_id");
            if(s != null)
                EsService.markNotificationAsRead(this, getAccount(), s);
        } else
        {
            mFullScreen = bundle.getBoolean("com.google.android.apps.plus.HostedStreamOneUpFragment.FULLSCREEN", false);
        }
        rootView = findViewById(R.id.host);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

            public final void onGlobalLayout()
            {
                if(rootView.getRootView().getHeight() - rootView.getHeight() > 100)
                {
                    mKeyboardIsVisible = true;
                    for(Iterator iterator1 = mScreenListeners.iterator(); iterator1.hasNext(); ((OnScreenListener)iterator1.next()).enableImageTransforms(false));
                } else
                {
                    mKeyboardIsVisible = false;
                    for(Iterator iterator = mScreenListeners.iterator(); iterator.hasNext(); ((OnScreenListener)iterator.next()).enableImageTransforms(true));
                }
            }

        });
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("com.google.android.apps.plus.HostedStreamOneUpFragment.FULLSCREEN", mFullScreen);
    }

    public final void toggleFullScreen()
    {
        if(!mKeyboardIsVisible)
        {
            boolean flag;
            Iterator iterator;
            if(!mFullScreen)
                flag = true;
            else
                flag = false;
            mFullScreen = flag;
            iterator = mScreenListeners.iterator();
            while(iterator.hasNext()) 
                ((OnScreenListener)iterator.next()).onFullScreenChanged(mFullScreen);
        }
    }

}
