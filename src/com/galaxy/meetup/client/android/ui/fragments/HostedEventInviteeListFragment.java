/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.PeopleListItemView;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class HostedEventInviteeListFragment extends HostedEsFragment implements
		LoaderCallbacks, RecyclerListener, OnItemClickListener, EventInviteeListAdapter.OnActionListener {

	private EventInviteeListAdapter mAdapter;
    private String mAuthKey;
    private Integer mDeleteAddRequestId;
    private String mEventId;
    private Integer mGetEventRequestId;
    private Integer mGetInviteesRequestId;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Integer mInviteMoreRequestId;
    protected ListView mListView;
    private final EsServiceListener mListener = new EsServiceListener() {

        public final void onEventInviteComplete(int i, ServiceResult serviceresult)
        {
            handleInviteMoreComplete(i, serviceresult);
        }

        public final void onEventManageGuestComplete(int i, ServiceResult serviceresult)
        {
            handleAddRemoveGuestCallback(i, serviceresult);
        }

        public final void onGetEventComplete(int i, ServiceResult serviceresult)
        {
            handleGetEventCallback(i, serviceresult);
        }

        public final void onGetEventInviteesComplete(int i, ServiceResult serviceresult)
        {
            handleGetEventInviteesCallback(i, serviceresult);
        }

        public final void onRemovePeopleRequestComplete(int i, ServiceResult serviceresult)
        {
            handleAddRemoveGuestCallback(i, serviceresult);
        }

    };
    private String mOwnerId;
    private PlusEvent mPlusEvent;
    private boolean mRefreshNeeded;
    
    
    public HostedEventInviteeListFragment()
    {
    }

    private void handleGetEventCallback(int i, ServiceResult serviceresult)
    {
        if(mGetEventRequestId != null)
            mGetEventRequestId.intValue();
        if(!serviceresult.hasError())
            getLoaderManager().restartLoader(0, null, this);
        mGetEventRequestId = null;
        updateRefreshStatus();
    }

    private void handleInviteMoreComplete(int i, ServiceResult serviceresult)
    {
        if(mInviteMoreRequestId != null && i == mInviteMoreRequestId.intValue())
        {
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            mInviteMoreRequestId = null;
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
            else
                refresh(2);
        }
    }

    private void refresh(int i)
    {
        if((i == 1 || i == 0) && mGetEventRequestId == null)
            mGetEventRequestId = Integer.valueOf(EsService.getEvent(getActivity(), getAccount(), mEventId));
        if((i == 2 || i == 0) && mGetInviteesRequestId == null)
            mGetInviteesRequestId = Integer.valueOf(EsService.getEventInvitees(getActivity(), getAccount(), mEventId, mAuthKey, true));
        updateRefreshStatus();
    }

    private void showProgressDialog(int i)
    {
        ProgressFragmentDialog.newInstance(null, getString(i), false).show(getFragmentManager(), "req_pending");
    }

    private void updateRefreshStatus()
    {
        if(getActionBar() != null)
            if(mGetEventRequestId != null || mGetInviteesRequestId != null)
                getActionBar().showProgressIndicator();
            else
                getActionBar().hideProgressIndicator();
        if(mGetEventRequestId == null && mGetInviteesRequestId == null)
            mRefreshNeeded = false;
    }

    public final OzViews getViewForLogging()
    {
        return null;
    }
    
    protected final void handleAddRemoveGuestCallback(int i, ServiceResult serviceresult)
    {
        if(mDeleteAddRequestId != null && i == mDeleteAddRequestId.intValue()) {
        	DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            mDeleteAddRequestId = null;
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
        }
    }

    protected final void handleGetEventInviteesCallback(int i, ServiceResult serviceresult)
    {
        if(mGetInviteesRequestId != null && mGetInviteesRequestId.intValue() == i)
        {
            if(mGetEventRequestId == null)
                mRefreshNeeded = false;
            if(!serviceresult.hasError())
                getLoaderManager().restartLoader(1, null, this);
            mGetInviteesRequestId = null;
            updateRefreshStatus();
        }
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mPlusEvent == null)
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    public final void onActionButtonClicked(int i)
    {
        if(0 == i) {
        	startActivityForResult(Intents.getEditAudienceActivityIntent(getActivity(), getAccount(), getString(R.string.event_invite_activity_title), null, 11, false, false, true, false), 0);
        }
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
        if(j == -1) {
        	switch(i)
            {
            case 0: // '\0'
                final AudienceData audience = (AudienceData)intent.getParcelableExtra("audience");
                mHandler.post(new Runnable() {

                    public final void run()
                    {
                        HostedEventInviteeListFragment.access$400(HostedEventInviteeListFragment.this, audience);
                    }
                });
                break;
            }
        }
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mAdapter = new EventInviteeListAdapter(activity);
        mAdapter.setOnActionListener(this);
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mRefreshNeeded = true;
        if(bundle != null)
        {
            if(bundle.containsKey("id"))
                mEventId = bundle.getString("id");
            if(bundle.containsKey("ownerid"))
                mOwnerId = bundle.getString("ownerid");
            if(bundle.containsKey("invitemoreid"))
                mInviteMoreRequestId = Integer.valueOf(bundle.getInt("invitemoreid"));
            if(bundle.containsKey("inviteesreq"))
                mGetInviteesRequestId = Integer.valueOf(bundle.getInt("inviteesreq"));
            if(bundle.containsKey("eventreq"))
                mGetEventRequestId = Integer.valueOf(bundle.getInt("eventreq"));
            if(bundle.containsKey("eventaddremovereq"))
                mDeleteAddRequestId = Integer.valueOf(bundle.getInt("eventaddremovereq"));
            if(bundle.containsKey("inviteesrefreshneeded"))
                mRefreshNeeded = bundle.getBoolean("inviteesrefreshneeded");
        }
        mAdapter.setViewerGaiaId(getAccount().getGaiaId());
        mAdapter.setEventOwnerId(mOwnerId);
        if(mRefreshNeeded)
            refresh();
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        Loader loader = null;
        final android.support.v4.app.FragmentActivity final_context1 = getActivity();
        if(0 == i) {
        	loader = new EsCursorLoader(final_context1, EsProvider.EVENTS_ALL_URI) ;
        } else if(1 == i) {
        	loader = new EventInviteeListLoader(getActivity(), getAccount(), mEventId, mOwnerId);
        }
        return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.people_list_fragment, viewgroup, false);
        mListView = (ListView)view.findViewById(0x102000a);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setRecyclerListener(this);
        getLoaderManager().restartLoader(0, null, this);
        getLoaderManager().restartLoader(1, null, this);
        return view;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        if(view instanceof PeopleListItemView)
        {
            String s = ((PeopleListItemView)view).getGaiaId();
            if(s != null)
                startActivity(Intents.getProfileActivityByGaiaIdIntent(getActivity(), getAccount(), s, null));
        }
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        int id = loader.getId();
        if(0 == id) {
        	if(cursor.moveToFirst())
            {
                mPlusEvent = (PlusEvent)JsonUtil.fromByteArray(cursor.getBlob(1), PlusEvent.class);
                mAuthKey = mPlusEvent.authKey;
                invalidateActionBar();
            }
        } else if(1 == id) {
        	mAdapter.swapCursor(cursor);
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public void onMovedToScrapHeap(View view)
    {
        if(view instanceof Recyclable)
            ((Recyclable)view).onRecycle();
    }

    public final void onPause()
    {
        EsService.unregisterListener(mListener);
        onPause();
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        if(EsEventData.canInviteOthers(mPlusEvent, mAccount))
            hostactionbar.showActionButton(0, R.drawable.icn_events_rsvp_invite_more, R.string.event_button_invite_more_label);
        hostactionbar.showRefreshButton();
        if(mPlusEvent != null)
            hostactionbar.showTitle(mPlusEvent.name);
        updateRefreshStatus();
        onPrepareActionBar(hostactionbar);
    }

    public final void onReInviteInvitee(String s, String s1)
    {
        showProgressDialog(R.string.event_reinviting_invitee);
        mDeleteAddRequestId = Integer.valueOf(EsService.manageEventGuest(getActivity(), getAccount(), mEventId, mAuthKey, false, s, s1));
    }

    public final void onRemoveInvitee(String s, String s1)
    {
        showProgressDialog(R.string.event_removing_invitee);
        mDeleteAddRequestId = Integer.valueOf(EsService.manageEventGuest(getActivity(), getAccount(), mEventId, mAuthKey, true, s, s1));
    }

    public final void onResume()
    {
        onResume();
        EsService.registerListener(mListener);
        if(mInviteMoreRequestId != null && !EsService.isRequestPending(mInviteMoreRequestId.intValue()))
        {
            ServiceResult serviceresult3 = EsService.removeResult(mInviteMoreRequestId.intValue());
            handleInviteMoreComplete(mInviteMoreRequestId.intValue(), serviceresult3);
            mInviteMoreRequestId = null;
        }
        if(mGetInviteesRequestId != null && !EsService.isRequestPending(mGetInviteesRequestId.intValue()))
        {
            ServiceResult serviceresult2 = EsService.removeResult(mGetInviteesRequestId.intValue());
            handleGetEventInviteesCallback(mGetInviteesRequestId.intValue(), serviceresult2);
            mGetInviteesRequestId = null;
        }
        if(mGetEventRequestId != null && !EsService.isRequestPending(mGetEventRequestId.intValue()))
        {
            ServiceResult serviceresult1 = EsService.removeResult(mGetEventRequestId.intValue());
            handleGetEventCallback(mGetEventRequestId.intValue(), serviceresult1);
            mGetEventRequestId = null;
        }
        if(mDeleteAddRequestId != null && !EsService.isRequestPending(mDeleteAddRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mDeleteAddRequestId.intValue());
            handleAddRemoveGuestCallback(mDeleteAddRequestId.intValue(), serviceresult);
            mDeleteAddRequestId = null;
        }
        if(mRefreshNeeded)
            refresh();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        onSaveInstanceState(bundle);
        bundle.putString("id", mEventId);
        bundle.putString("ownerid", mOwnerId);
        if(mInviteMoreRequestId != null)
            bundle.putInt("invitemoreid", mInviteMoreRequestId.intValue());
        if(mGetInviteesRequestId != null)
            bundle.putInt("inviteesreq", mGetInviteesRequestId.intValue());
        if(mGetEventRequestId != null)
            bundle.putInt("eventreq", mGetEventRequestId.intValue());
        if(mDeleteAddRequestId != null)
            bundle.putInt("eventaddremovereq", mDeleteAddRequestId.intValue());
        bundle.putBoolean("inviteesrefreshneeded", mRefreshNeeded);
    }

    protected final void onSetArguments(Bundle bundle)
    {
        onSetArguments(bundle);
        mEventId = bundle.getString("event_id");
        mOwnerId = bundle.getString("owner_id");
        mAuthKey = bundle.getString("auth_key");
    }

    public final void refresh()
    {
        refresh();
        refresh(0);
    }
    
    static void access$400(HostedEventInviteeListFragment hostedeventinviteelistfragment, AudienceData audiencedata)
    {
        if(hostedeventinviteelistfragment.getActivity() != null && hostedeventinviteelistfragment.mEventId != null && hostedeventinviteelistfragment.mOwnerId != null)
        {
            hostedeventinviteelistfragment.showProgressDialog(R.string.event_inviting_more);
            hostedeventinviteelistfragment.mInviteMoreRequestId = Integer.valueOf(EsService.invitePeopleToEvent(hostedeventinviteelistfragment.getActivity(), hostedeventinviteelistfragment.getAccount(), hostedeventinviteelistfragment.mEventId, hostedeventinviteelistfragment.mAuthKey, hostedeventinviteelistfragment.mOwnerId, audiencedata));
        }
        return;
    }
}
