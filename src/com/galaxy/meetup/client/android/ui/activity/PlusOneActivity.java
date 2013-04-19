/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.PlusOneFragment;
import com.galaxy.meetup.client.util.PlatformContractUtils;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class PlusOneActivity extends EsFragmentActivity implements
		OnClickListener {

	private EsAccount mAccount;
    private ApiaryApiInfo mApiInfo;
    private PlusOneFragment mFragment;
    private boolean mInsert;
    
    public PlusOneActivity()
    {
    }

    private void recordErrorAndFinish()
    {
        EsAccount esaccount = mAccount;
        PlatformContractUtils.getCallingPackageAnalytics(mApiInfo);
        EsAnalytics.recordEvent(this, esaccount, getAnalyticsInfo(), OzActions.PLATFORM_ERROR_PLUSONE);
        finish();
    }

    private void recordExitedAction()
    {
        if(mInsert)
            recordUserAction(OzActions.PLATFORM_PLUSONE_CONFIRMED);
        else
            recordUserAction(OzActions.PLATFORM_UNDO_PLUSONE_CANCELED);
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PLUSONE;
    }

    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ProgressBar progressbar = (ProgressBar)findViewById(R.id.progress_spinner);
        mFragment.setProgressBar(progressbar);
    }

    public void onBackPressed()
    {
        recordExitedAction();
        super.onBackPressed();
    }

    public void onClick(DialogInterface dialoginterface, int i) {
    	
    	if(-3 == i) {
    		setResult(0);
            finish();
    	}
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.plus_one_activity);
        showTitlebar(false);
        setTitlebarTitle(getString(R.string.app_name));
        Intent intent = getIntent();
        Bundle bundle1 = new Bundle();
        mAccount = EsAccountsData.getActiveAccount(this);
        if(!SignOnActivity.finishIfNoAccount(this, mAccount))
        {
            bundle1.putParcelable("PlusOneFragment#mAccount", mAccount);
            String s = getCallingPackage();
            boolean flag = intent.getBooleanExtra("from_signup", false);
            String s1 = intent.getStringExtra("calling_package");
            String s2 = Property.PLUS_CLIENTID.get();
            String s3 = getPackageName();
            android.content.pm.PackageManager packagemanager = getPackageManager();
            String s4 = PlatformContractUtils.getCertificate(s1, packagemanager);
            String s5 = PlatformContractUtils.getCertificate(s3, packagemanager);
            String s6 = intent.getStringExtra("com.google.circles.platform.intent.extra.APIKEY");
            String s7 = intent.getStringExtra("com.google.circles.platform.intent.extra.CLIENTID");
            String s8 = intent.getStringExtra("com.google.circles.platform.intent.extra.APIVERSION");
            mApiInfo = new ApiaryApiInfo(null, s2, s3, s5, s8, new ApiaryApiInfo(s6, s7, s1, s4, s8));
            if(bundle == null)
            {
                PlatformContractUtils.getCallingPackageAnalytics(mApiInfo);
                recordUserAction(getAnalyticsInfo(), OzActions.PLATFORM_CLICKED_PLUSONE);
            }
            if(!flag || !getPackageName().equals(s) || TextUtils.isEmpty(s1))
            {
                recordErrorAndFinish();
            } else
            {
                ApiaryApiInfo apiaryapiinfo = mApiInfo.getSourceInfo();
                if(apiaryapiinfo == null || TextUtils.isEmpty(apiaryapiinfo.getApiKey()) || TextUtils.isEmpty(apiaryapiinfo.getCertificate()) || TextUtils.isEmpty(apiaryapiinfo.getClientId()) || TextUtils.isEmpty(apiaryapiinfo.getSdkVersion()) || TextUtils.isEmpty(apiaryapiinfo.getPackageName()))
                {
                    recordErrorAndFinish();
                } else
                {
                    bundle1.putSerializable("PlusOneFragment#mApiaryApiInfo", mApiInfo);
                    String s9 = intent.getStringExtra("com.google.circles.platform.intent.extra.TOKEN");
                    String s10 = intent.getStringExtra("com.google.circles.platform.intent.extra.ENTITY");
                    String s11 = intent.getStringExtra("com.google.circles.platform.intent.extra.ACTION");
                    if(TextUtils.isEmpty(s9) || TextUtils.isEmpty(s10) || TextUtils.isEmpty(s11))
                    {
                        recordErrorAndFinish();
                    } else
                    {
                        boolean flag1;
                        if(!"delete".equals(s11))
                            flag1 = true;
                        else
                            flag1 = false;
                        mInsert = flag1;
                        bundle1.putString("PlusOneFragment#mToken", s9);
                        bundle1.putString("PlusOneFragment#mUrl", s10);
                        bundle1.putBoolean("PlusOneFragment#mInsert", mInsert);
                        mFragment = (PlusOneFragment)getSupportFragmentManager().findFragmentByTag("PlusOneActivity#Fragment");
                        if(mFragment == null)
                        {
                            mFragment = new PlusOneFragment();
                            mFragment.setArguments(bundle1);
                            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
                            fragmenttransaction.add(R.id.plusone_container, mFragment, "PlusOneActivity#Fragment");
                            fragmenttransaction.commit();
                        }
                        findViewById(R.id.frame_container).setOnClickListener(new android.view.View.OnClickListener() {

                            public final void onClick(View view)
                            {
                                recordExitedAction();
                                PlusOneActivity plusoneactivity = PlusOneActivity.this;
                                byte byte0;
                                if(mInsert)
                                    byte0 = -1;
                                else
                                    byte0 = 0;
                                plusoneactivity.setResult(byte0);
                                finish();
                            }
                        });
                        
                        findViewById(R.id.plusone_container).setOnClickListener(new android.view.View.OnClickListener() {

                            public final void onClick(View view)
                            {
                            }

                        });
                    }
                }
            }
        }
    }

    public Dialog onCreateDialog(int i, Bundle bundle) {
    	if(1 == i) {
    		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.plusone_post_error).setNeutralButton(0x104000a, this).setCancelable(false);
            return builder.create();
    	}
    	
    	return null;
    }

    protected void onResume()
    {
        super.onResume();
        if(!SignOnActivity.finishIfNoAccount(this, mAccount));
    }

    protected final void showTitlebar(boolean flag, boolean flag1)
    {
        super.showTitlebar(flag, flag1);
        findViewById(R.id.title_layout).setPadding(getResources().getDimensionPixelOffset(R.dimen.plus_one_title_padding_left), 0, 0, 0);
    }

}
