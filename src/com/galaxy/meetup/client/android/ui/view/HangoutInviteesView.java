/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.ui.fragments.CircleNameResolver;

/**
 * 
 * @author sihai
 *
 */
public class HangoutInviteesView extends FrameLayout {

	private static final String INVITEE_PROJECTION[] = {
        "packed_circle_ids"
    };
    private EsAccount mAccount;
    private AvatarView mAvatarView;
    private View mCircleLogoView;
    private CircleNameResolver mCircleNameResolver;
    private TextView mCirclesView;
    private String mInviteeId;
    private ArrayList mInvitees;
    private LinearLayout mMultipleInviteesContainer;
    private HorizontalScrollView mMultipleInviteesView;
    private TextView mNameView;
    private String mPackedCircleIds;
    private final PersonLoaderCallbacks mPersonLoaderCallbacks;
    private View mSingleInviteeView;
    
	public HangoutInviteesView(Context context)
    {
        super(context);
        mInvitees = new ArrayList();
        mPersonLoaderCallbacks = new PersonLoaderCallbacks();
        addView(inflate(R.layout.hangout_invitees_view));
        addView(createMultipleInviteesView());
        mAvatarView = (AvatarView)findViewById(R.id.avatar);
        mNameView = (TextView)findViewById(R.id.name);
        mCirclesView = (TextView)findViewById(R.id.circles);
        mSingleInviteeView = findViewById(R.id.single_invitee_view);
        mCircleLogoView = findViewById(R.id.circle_logo);
    }

    public HangoutInviteesView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mInvitees = new ArrayList();
        mPersonLoaderCallbacks = new PersonLoaderCallbacks();
        addView(inflate(R.layout.hangout_invitees_view));
        addView(createMultipleInviteesView());
        mAvatarView = (AvatarView)findViewById(R.id.avatar);
        mNameView = (TextView)findViewById(R.id.name);
        mCirclesView = (TextView)findViewById(R.id.circles);
        mSingleInviteeView = findViewById(R.id.single_invitee_view);
        mCircleLogoView = findViewById(R.id.circle_logo);
    }

    public HangoutInviteesView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mInvitees = new ArrayList();
        mPersonLoaderCallbacks = new PersonLoaderCallbacks();
        addView(inflate(R.layout.hangout_invitees_view));
        addView(createMultipleInviteesView());
        mAvatarView = (AvatarView)findViewById(R.id.avatar);
        mNameView = (TextView)findViewById(R.id.name);
        mCirclesView = (TextView)findViewById(R.id.circles);
        mSingleInviteeView = findViewById(R.id.single_invitee_view);
        mCircleLogoView = findViewById(R.id.circle_logo);
    }

    private HorizontalScrollView createMultipleInviteesView()
    {
        Context context = getContext();
        mMultipleInviteesView = new HorizontalScrollView(context);
        android.widget.FrameLayout.LayoutParams layoutparams = new android.widget.FrameLayout.LayoutParams(-1, -1);
        mMultipleInviteesView.setLayoutParams(layoutparams);
        mMultipleInviteesView.setBackgroundResource(R.color.hangout_common_menu_background);
        mMultipleInviteesView.setVisibility(8);
        mMultipleInviteesContainer = new LinearLayout(context);
        android.widget.FrameLayout.LayoutParams layoutparams1 = new android.widget.FrameLayout.LayoutParams(-2, -1);
        mMultipleInviteesView.addView(mMultipleInviteesContainer, layoutparams1);
        return mMultipleInviteesView;
    }

    private static String getGaiaId(PersonData persondata)
    {
        String s;
        if(persondata.getObfuscatedId() != null)
            s = (new StringBuilder()).append(persondata.getObfuscatedId()).toString();
        else
            s = "";
        return s;
    }

    private View inflate(int i)
    {
        return LayoutInflater.from(getContext()).inflate(i, this, false);
    }

    public final int getAvatarCount()
    {
        return mInvitees.size();
    }

    public void setInvitees(AudienceData audiencedata, EsAccount esaccount)
    {
        Context context;
        context = getContext();
        mAccount = esaccount;
        mInvitees.clear();
        PersonData apersondata[] = audiencedata.getUsers();
        int i = apersondata.length;
        for(int j = 0; j < i; j++)
        {
            PersonData persondata = apersondata[j];
            if(!TextUtils.isEmpty(persondata.getName()) && !TextUtils.isEmpty(persondata.getObfuscatedId()))
                mInvitees.add(persondata);
        }

        if(mInvitees.size() != 1) {
        	if(mInvitees.size() > 1)
            {
                mSingleInviteeView.setVisibility(8);
                mMultipleInviteesView.setVisibility(0);
                mMultipleInviteesContainer.removeAllViews();
                int k = 0;
                int l = mInvitees.size();
                while(k < l) 
                {
                    String s = getGaiaId((PersonData)mInvitees.get(k));
                    LinearLayout linearlayout = mMultipleInviteesContainer;
                    AvatarView avatarview = new AvatarView(getContext());
                    avatarview.setGaiaId(s);
                    int i1 = getResources().getDimensionPixelSize(R.dimen.hangout_invitees_view_height);
                    android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(i1, i1);
                    layoutparams.rightMargin = getResources().getDimensionPixelSize(R.dimen.hangout_avatar_margin);
                    avatarview.setLayoutParams(layoutparams);
                    linearlayout.addView(avatarview);
                    k++;
                }
            } 
        } else { 
        	mInviteeId = getGaiaId((PersonData)mInvitees.get(0));
            mSingleInviteeView.setVisibility(0);
            mMultipleInviteesView.setVisibility(8);
            String s1 = (new StringBuilder()).append(((PersonData)mInvitees.get(0)).getName()).toString();
            mNameView.setText(s1);
            mAvatarView.setGaiaId(mInviteeId);
            LoaderManager loadermanager = ((FragmentActivity)getContext()).getSupportLoaderManager();
            loadermanager.initLoader(0, null, mPersonLoaderCallbacks);
            mCircleNameResolver = new CircleNameResolver(context, loadermanager, mAccount);
            mCircleNameResolver.initLoader();
            mCircleNameResolver.registerObserver(new DataSetObserver() {

                public final void onChanged()
                {
                    HangoutInviteesView.access$100(HangoutInviteesView.this);
                }

            });
        }
    }

    public void setName(String s)
    {
        mNameView.setVisibility(0);
        mNameView.setText(s);
    }

    public void setVisibility(int i)
    {
        super.setVisibility(i);
        if(i == 8)
        {
            mSingleInviteeView.setVisibility(8);
            mMultipleInviteesView.setVisibility(8);
        }
    }
    
    static void access$100(HangoutInviteesView hangoutinviteesview)
    {
        if(hangoutinviteesview.mPackedCircleIds != null && hangoutinviteesview.mCircleNameResolver != null && hangoutinviteesview.mCircleNameResolver.isLoaded() && hangoutinviteesview.mCirclesView != null)
        {
            hangoutinviteesview.mCircleLogoView.setVisibility(0);
            hangoutinviteesview.mCirclesView.setText(hangoutinviteesview.mCircleNameResolver.getCircleNamesForPackedIds(hangoutinviteesview.mPackedCircleIds));
        }
        return;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private final class PersonLoaderCallbacks implements android.support.v4.app.LoaderManager.LoaderCallbacks {

	    public final Loader onCreateLoader(int i, Bundle bundle)
	    {
	        Object obj;
	        if(mAccount == null || mInviteeId == null)
	        {
	            obj = null;
	        } else
	        {
	            final Context context = getContext();
	            Uri uri = EsProvider.appendAccountParameter(EsProvider.CONTACTS_URI, mAccount);
	            String as[] = HangoutInviteesView.INVITEE_PROJECTION;
	            String as1[] = new String[1];
	            as1[0] = mInviteeId;
	            obj = new EsCursorLoader(context, uri, as, "gaia_id=?", as1, null) {
	
	                public final Cursor esLoadInBackground()
	                {
	                    EsPeopleData.ensurePeopleSynced(context, mAccount);
	                    return super.esLoadInBackground();
	                }
	
	            };
	        }
	        return ((Loader) (obj));
	    }
	
	    public final void onLoadFinished(Loader loader, Object obj)
	    {
	        Cursor cursor = (Cursor)obj;
	        if(cursor != null && cursor.moveToFirst())
	        {
	            mPackedCircleIds = cursor.getString(0);
	            HangoutInviteesView.access$100(HangoutInviteesView.this);
	        }
	    }
	
	    public final void onLoaderReset(Loader loader)
	    {
	    }
	}
}
