/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ShakeDetector;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.EditEventFragment;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;

/**
 * 
 * @author sihai
 *
 */
public class NewEventActivity extends EsFragmentActivity implements EditEventFragment.OnEditEventListener {

	private EditEventFragment mEditEventFragment;
    private boolean mShakeDetectorWasRunning;
    
    public NewEventActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    protected final CharSequence getTitleButton3Text()
    {
        return getResources().getText(R.string.invite);
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.CREATE_EVENT;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof EditEventFragment)
        {
            mEditEventFragment = (EditEventFragment)fragment;
            mEditEventFragment.createEvent();
            mEditEventFragment.setOnEventChangedListener(this);
        }
    }

    public void onBackPressed()
    {
        if(mEditEventFragment != null)
            mEditEventFragment.onDiscard();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.new_event_activity);
        View view = findViewById(R.id.cancel_button);
        if(view != null)
            view.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view2)
                {
                    if(mEditEventFragment != null)
                        mEditEventFragment.onDiscard();
                }

            });
        View view1 = findViewById(R.id.share_button);
        if(view1 != null)
            view1.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view2)
                {
                    if(mEditEventFragment != null)
                        mEditEventFragment.save();
                }
            });
        ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
        if(shakedetector != null)
            mShakeDetectorWasRunning = shakedetector.stop();
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if(mShakeDetectorWasRunning)
        {
            ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
            if(shakedetector != null)
                shakedetector.start();
        }
    }

    public final void onEventClosed()
    {
        finish();
    }

    public final void onEventSaved()
    {
        finish();
    }

    protected void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

    protected final void onTitlebarLabelClick()
    {
        onBackPressed();
    }

}
