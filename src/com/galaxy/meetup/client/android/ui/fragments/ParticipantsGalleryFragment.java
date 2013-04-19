/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.ui.view.ParticipantsGalleryView;

/**
 * 
 * @author sihai
 *
 */
public class ParticipantsGalleryFragment extends Fragment {

	private EsAccount mAccount;
    private Integer mBackgroundColor;
    private ParticipantsGalleryView.CommandListener mCommandListener;
    private String mEmptyMessage;
    private boolean mParticipantListButtonVisibility;
    private ParticipantsGalleryView mView;
    
    public ParticipantsGalleryFragment()
    {
        mParticipantListButtonVisibility = true;
    }

    public final void addParticipants(Collection collection)
    {
        LayoutInflater layoutinflater = LayoutInflater.from(getActivity());
        Data.Participant participant;
        for(Iterator iterator = collection.iterator(); iterator.hasNext(); mView.addParticipant(layoutinflater, participant))
            participant = (Data.Participant)iterator.next();

    }

    public final ParticipantsGalleryView getParticipantsGalleryView()
    {
        return mView;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        mView = new ParticipantsGalleryView(getActivity());
        if(mBackgroundColor != null)
            mView.setBackgroundColor(mBackgroundColor.intValue());
        if(mEmptyMessage != null)
            mView.setEmptyMessage(mEmptyMessage);
        if(mAccount != null)
            mView.setAccount(mAccount);
        if(mCommandListener != null)
            mView.setCommandListener(mCommandListener);
        mView.setParticipantListButtonVisibility(mParticipantListButtonVisibility);
        return mView;
    }

    public final void onInflate(Activity activity, AttributeSet attributeset, Bundle bundle)
    {
        TypedArray typedarray = activity.obtainStyledAttributes(attributeset, R.styleable.ParticipantsGalleryFragment);
        if(typedarray.hasValue(0))
            mBackgroundColor = Integer.valueOf(typedarray.getColor(0, 0));
        if(typedarray.hasValue(1))
            mEmptyMessage = typedarray.getString(1);
        typedarray.recycle();
    }

    public final void onPause()
    {
        super.onPause();
        mView.dismissAvatarMenuDialog();
    }

    public final void removeAllParticipants()
    {
        mView.removeAllParticipants();
    }

    public final void setAccount(EsAccount esaccount)
    {
        mAccount = esaccount;
        if(mView != null)
            mView.setAccount(esaccount);
    }

    public final void setCommandListener(ParticipantsGalleryView.CommandListener commandlistener)
    {
        mCommandListener = commandlistener;
        if(mView != null)
            mView.setCommandListener(commandlistener);
    }

    public final void setParticipantListButtonVisibility(boolean flag)
    {
        mParticipantListButtonVisibility = flag;
        if(mView != null)
            mView.setParticipantListButtonVisibility(flag);
    }

    public final void setParticipants(HashMap hashmap, HashSet hashset, HashSet hashset1)
    {
        if(hashmap != null)
            mView.setParticipants(hashmap, hashset, hashset1);
    }
}
