/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.MeetupFeedback;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.AudienceFragment;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.util.HelpUrl;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class NewHangoutActivity extends EsFragmentActivity {

	private EsAccount mAccount;
    private AudienceFragment mAudienceFragment;
    private Button mHangoutButton;
    
    public NewHangoutActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.HANGOUT_START_NEW;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof AudienceFragment)
        {
            mAudienceFragment = (AudienceFragment)fragment;
            mAudienceFragment.setCirclesUsageType(10);
            mAudienceFragment.setIncludePhoneOnlyContacts(false);
            mAudienceFragment.setIncludePlusPages(false);
            mAudienceFragment.setPublicProfileSearchEnabled(true);
            mAudienceFragment.setShowSuggestedPeople(true);
            mAudienceFragment.setFilterNullGaiaIds(true);
            mAudienceFragment.setAudienceChangedCallback(new Runnable() {

                public final void run()
                {
                    if(mHangoutButton != null)
                    {
                        Button button = mHangoutButton;
                        boolean flag;
                        if(!mAudienceFragment.isAudienceEmpty())
                            flag = true;
                        else
                            flag = false;
                        button.setEnabled(flag);
                    }
                    // TODO
                }

            });
        }
    }

    public void onBackPressed()
    {
        recordUserAction(OzActions.CONVERSATION_ABORT_NEW);
        super.onBackPressed();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.new_hangout_activity);
        mAccount = EsService.getActiveAccount(this);
        mHangoutButton = (Button)findViewById(R.id.start_hangout_button);
        mHangoutButton.setEnabled(false);
        mHangoutButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                // TODO
            }

        });
        if(android.os.Build.VERSION.SDK_INT < 11)
        {
            showTitlebar(true);
            setTitlebarTitle(getString(R.string.new_hangout_label));
        }
    }

    public Dialog onCreateDialog(int i, Bundle bundle)
    {
        Dialog dialog;
        if(i == 0x7f0a003e)
            dialog = ImageUtils.createInsertCameraPhotoDialog(this);
        else
            dialog = super.onCreateDialog(i, bundle);
        return dialog;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.hangout_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(i == 0x102002c)
            goHome(mAccount);
        else
        if(i == R.id.feedback)
        {
            recordUserAction(OzActions.SETTINGS_FEEDBACK);
            MeetupFeedback.launch(this);
        } else
        if(i == R.id.help)
            startExternalActivity(new Intent("android.intent.action.VIEW", HelpUrl.getHelpUrl(this, getResources().getString(R.string.url_param_help_hangouts))));
        else
            flag = super.onOptionsItemSelected(menuitem);
        return flag;
    }

    protected void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

    protected void onStart()
    {
        Log.debug("NewHangoutActivity.onStart: this=%s", new Object[] {
            this
        });
        super.onStart();
        if(android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        GCommApp.getInstance(this).signinUser(mAccount);
        GCommApp.getInstance(this).startingHangoutActivity(this);
    }

    protected void onStop()
    {
        Log.debug("NewHangoutActivity.onStop: this=%s", new Object[] {
            this
        });
        super.onStop();
        GCommApp.getInstance(this).stoppingHangoutActivity();
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(mAccount);
    }
}
