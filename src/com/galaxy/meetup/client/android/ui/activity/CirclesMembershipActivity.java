/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.CirclesMultipleSelectFragment;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;

/**
 * 
 * @author sihai
 *
 */
public class CirclesMembershipActivity extends EsFragmentActivity implements android.view.View.OnClickListener, CirclesMultipleSelectFragment.OnCircleSelectionListener {

	private CirclesMultipleSelectFragment mFragment;
    private View mPositiveButton;
    
    public CirclesMembershipActivity()
    {
    }

    private String getPersonId()
    {
        return getIntent().getExtras().getString("person_id");
    }

    private boolean isEmptySelectionAllowed()
    {
        return getIntent().getExtras().getBoolean("empty_selection_allowed", false);
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.CONTACTS_CIRCLELIST;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof CirclesMultipleSelectFragment)
        {
            mFragment = (CirclesMultipleSelectFragment)fragment;
            mFragment.setCircleUsageType(2);
            mFragment.setNewCircleEnabled(true);
            mFragment.setPersonId(getPersonId());
            mFragment.setOnCircleSelectionListener(this);
        }
    }

    public final void onCircleSelectionChange()
    {
        if(!isEmptySelectionAllowed() && mFragment != null && mPositiveButton != null)
        {
            View view = mPositiveButton;
            boolean flag;
            if(mFragment.getSelectedCircleIds().size() > 0)
                flag = true;
            else
                flag = false;
            view.setEnabled(flag);
        }
    }

    public void onClick(View view)
    {
        int i = view.getId();
        if(R.id.ok == i) {
        	ArrayList arraylist = mFragment.getOriginalCircleIds();
            if(arraylist != null)
            {
                ArrayList arraylist1 = mFragment.getSelectedCircleIds();
                Collections.sort(arraylist);
                Collections.sort(arraylist1);
                if(arraylist.equals(arraylist1))
                {
                    setResult(0);
                    finish();
                }
                Intent intent = new Intent();
                intent.putExtra("person_id", getPersonId());
                intent.putExtra("display_name", getIntent().getExtras().getString("display_name"));
                intent.putExtra("original_circle_ids", arraylist);
                intent.putExtra("selected_circle_ids", arraylist1);
                setResult(-1, intent);
                finish();
            }
        } else if(R.id.cancel == i) {
        	finish();
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.circle_selection_activity);
        setTitle(R.string.add_to_circles_dialog_title);
        mPositiveButton = findViewById(R.id.ok);
        mPositiveButton.setEnabled(isEmptySelectionAllowed());
        mPositiveButton.setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    public void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

}
