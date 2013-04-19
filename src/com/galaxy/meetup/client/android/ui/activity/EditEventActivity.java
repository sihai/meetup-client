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
import com.galaxy.meetup.client.android.ui.fragments.EditEventFragment.OnEditEventListener;
import com.galaxy.meetup.client.android.ui.view.ImageTextButton;

/**
 * 
 * @author sihai
 *
 */
public class EditEventActivity extends EsFragmentActivity implements
		OnEditEventListener {

	private String mAuthKey;
    private EditEventFragment mEditEventFragment;
    private String mEventId;
    private String mOwnerId;
    private boolean mShakeDetectorWasRunning;
    
    public EditEventActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    protected final CharSequence getTitleButton3Text()
    {
        return getResources().getText(R.string.save);
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
            mEditEventFragment.editEvent(mEventId, mOwnerId, mAuthKey);
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
        mEventId = getIntent().getStringExtra("event_id");
        mOwnerId = getIntent().getStringExtra("owner_id");
        mAuthKey = getIntent().getStringExtra("auth_key");
        setContentView(R.layout.new_event_activity);
        View view = findViewById(R.id.cancel_button);
        if(view != null)
            view.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    if(mEditEventFragment != null)
                        mEditEventFragment.onDiscard();
                }
            });
        ImageTextButton imagetextbutton = (ImageTextButton)findViewById(R.id.share_button);
        if(imagetextbutton != null)
        {
            imagetextbutton.setText(getResources().getString(R.string.save));
            imagetextbutton.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    if(mEditEventFragment != null)
                        mEditEventFragment.save();
                }
            });
        }
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
