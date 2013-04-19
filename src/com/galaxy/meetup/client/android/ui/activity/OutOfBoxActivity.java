/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.oob.OutOfBoxResponseParcelable;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.OutOfBoxFragment;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;

/**
 * 
 * @author sihai
 *
 */
public class OutOfBoxActivity extends EsFragmentActivity {

	public OutOfBoxActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.UNKNOWN;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.out_of_box_activity);
        showTitlebar(false);
        setTitlebarTitle(getString(R.string.app_name));
        EsAccountsData.setHasVisitedOob(this, true);
        if(bundle == null)
        {
            EsAccount esaccount = getAccount();
            OutOfBoxResponseParcelable outofboxresponseparcelable = (OutOfBoxResponseParcelable)getIntent().getParcelableExtra("network_oob");
            MobileOutOfBoxResponse mobileoutofboxresponse;
            if(outofboxresponseparcelable != null)
                mobileoutofboxresponse = outofboxresponseparcelable.getResponse();
            else
                mobileoutofboxresponse = null;
            if(esaccount != null && mobileoutofboxresponse != null)
            {
                String s = getIntent().getStringExtra("oob_origin");
                FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
                String s1 = OutOfBoxFragment.createInitialTag();
                fragmenttransaction.add(R.id.oob_container, OutOfBoxFragment.newInstance(esaccount, mobileoutofboxresponse, s), s1);
                fragmenttransaction.commit();
            } else
            {
                setResult(0);
                finish();
            }
        }
    }

}
