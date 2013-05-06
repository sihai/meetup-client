/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringTokenizer;

import WriteReviewOperation.MediaRef;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsAsyncTaskLoader;
import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EventDetailsActivityAdapter;
import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.ApiUtils;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.iu.InstantUploadFacade;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.EventActionListener;
import com.galaxy.meetup.client.android.ui.view.EventDetailOptionRowInstantShare;
import com.galaxy.meetup.client.android.ui.view.EventDetailsHeaderView;
import com.galaxy.meetup.client.android.ui.view.EventRsvpLayout;
import com.galaxy.meetup.client.android.ui.view.EventUpdate;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.MediaStoreUtils;
import com.galaxy.meetup.client.util.ScreenMetrics;
import com.galaxy.meetup.server.client.util.JsonUtil;
import com.galaxy.meetup.server.client.v2.domain.Event;
import com.galaxy.meetup.server.client.v2.domain.EventOptions;

/**
 * 
 * @author sihai
 *
 */
public class HostedEventFragment extends HostedEsFragment
		implements
		android.support.v4.app.LoaderManager.LoaderCallbacks,
		AlertFragmentDialog.AlertDialogListener,
		CommentEditFragmentDialog.CommentEditDialogListener,
		EventDetailsActivityAdapter.ViewUseListener,
		EventActionListener {

	private static int mNextPagePreloadTriggerRows;
    private String mActivityId;
    private EventDetailsActivityAdapter mAdapter;
    private String mAuthKey;
    private boolean mCanComment;
    private Integer mCommentReqId;
    private Integer mDeleteReqId;
    private boolean mError;
    private Event mEvent;
    private String mEventId;
    private boolean mEventLoaded;
    private EventActiveState mEventState;
    private Integer mFetchReqId;
    private long mFirstActivityTimestamp;
    private boolean mGhostEvent;
    private ColumnGridView mGridView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mHasUserInteracted;
    private String mIncomingRsvpType;
    private String mInvitationToken;
    private Integer mInviteReqId;
    private final EsServiceListener mListener = new EsServiceListener() {

        public final void onCreateEventComment(int i, ServiceResult serviceresult)
        {
            handleCreateCommentComplete(i, serviceresult);
        }

        public final void onDeleteEventComplete(int i, ServiceResult serviceresult)
        {
            HostedEventFragment.access$1000(HostedEventFragment.this, i, serviceresult);
        }

        public final void onEventInviteComplete(int i, ServiceResult serviceresult)
        {
            handleInviteMoreComplete(i, serviceresult);
        }

        public final void onInsertCameraPhotoComplete(int i, ServiceResult serviceresult)
        {
            String s = EsService.getLastCameraMediaLocation();
            handleNewPhotoComplete(i, serviceresult, s);
        }

        public final void onReadEventComplete(int i, ServiceResult serviceresult)
        {
            mNeedsRefresh = false;
            if(serviceresult != null && !serviceresult.hasError())
                mInvitationToken = null;
            handleGetEventUpdatesComplete(i, serviceresult);
        }

        public final void onReportActivity(int i, ServiceResult serviceresult)
        {
            handleReportEventCallback(i, serviceresult);
        }

        public final void onSendEventRsvpComplete(int i, ServiceResult serviceresult)
        {
            handleSendEventRsvpComplete(i, serviceresult);
        }

        public final void onSharePhotosToEventComplete(int i, ServiceResult serviceresult)
        {
            handleSharePhotosToEventCallBack(serviceresult);
        }

    };
    private boolean mNeedsRefresh;
    private Integer mNewPhotoReqId;
    private String mPollingToken;
    private boolean mPreloadRequested;
    private Runnable mRefreshRunnable;
    private Integer mReportAbuseRequestId;
    private String mResumeToken;
    private int mSavedScrollPos;
    private Integer mSendRsvpReqId;
    private final SettingsLoaderCallbacks mSettingsCallbacks = new SettingsLoaderCallbacks();
    private ContentObserver mSettingsObserver;
    private int mSource;
    private String mTemporalRsvpState;
    private int mTypeId;
    
    public HostedEventFragment()
    {
        mEventState = new EventActiveState();
        mSavedScrollPos = -1;
        mSettingsObserver = new ContentObserver(mHandler) {

            public final void onChange(boolean flag)
            {
                if(!isPaused())
                    getLoaderManager().restartLoader(0, null, HostedEventFragment.this);
            }

        };
    }

    private void fetchData()
    {
        mError = false;
        mFetchReqId = Integer.valueOf(EsService.readEvent(getActivity(), mAccount, mEventId, mPollingToken, mResumeToken, mInvitationToken, mAuthKey, true));
        updateProgressIndicator();
    }

    private void handleCreateCommentComplete(int i, ServiceResult serviceresult)
    {
        if(mCommentReqId != null && mCommentReqId.intValue() == i)
        {
            mCommentReqId = null;
            hideProgressDialog();
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
            else
                fetchData();
        }
    }

    private void handleGetEventUpdatesComplete(int i, ServiceResult serviceresult) {
    	
    	if(null == mFetchReqId || i != mFetchReqId.intValue()) {
    		return;
    	}
    	
    	mFetchReqId = null;
        updateProgressIndicator();
        hideProgressDialog();
        if(serviceresult == null || !serviceresult.hasError())
            return;
        int j = serviceresult.getErrorCode();
        if(j < 400 || j >= 500) {
        	mError = true;
            if(mEvent != null)
                Toast.makeText(getActivity(), R.string.no_connection, 0).show();
        } else {
        	mGhostEvent = true;
        }
        
        getLoaderManager().restartLoader(0, null, this);
        getLoaderManager().restartLoader(4, null, this);
        mAdapter.checkPartitions("HEF", "HGEUC");
    }

    private void handleInviteMoreComplete(int i, ServiceResult serviceresult)
    {
        if(mInviteReqId != null && i == mInviteReqId.intValue())
        {
            hideProgressDialog();
            mInviteReqId = null;
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
            else
                fetchData();
        }
    }

    private void handleNewPhotoComplete(int i, ServiceResult serviceresult, String s)
    {
        if(mNewPhotoReqId != null && mNewPhotoReqId.intValue() == i)
        {
            mNewPhotoReqId = null;
            hideProgressDialog();
            final FragmentActivity context = getActivity();
            if(serviceresult != null && serviceresult.hasError())
            {
                Toast.makeText(context, R.string.transient_server_error, 0).show();
            } else
            {
                Toast.makeText(context, R.string.event_post_photo, 1).show();
                AsyncTask asynctask = new AsyncTask() {

                    protected final Object doInBackground(Object aobj[])
                    {
                        String as1[] = (String[])aobj;
                        String s1 = as1[0];
                        if(!TextUtils.equals(s1, InstantUpload.getInstantShareEventId(context)))
                        {
                            HostedEventFragment hostedeventfragment = HostedEventFragment.this;
                            Context context1 = context;
                            String s2 = as1[1];
                            String as2[] = new String[1];
                            as2[0] = as1[2];
                            HostedEventFragment.access$1500(hostedeventfragment, context1, s2, s1, as2);
                        }
                        return null;
                    }
                };
                String as[] = new String[3];
                as[0] = mEventId;
                as[1] = getAccount().getName();
                as[2] = s;
                asynctask.execute(as);
            }
        }
    }
    
    private void handleSendEventRsvpComplete(int i, ServiceResult serviceresult)
    {
    	if(null == mSendRsvpReqId || i != mSendRsvpReqId.intValue()) {
    		return;
    	}
    	
    	DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("send_rsvp");
        if(dialogfragment != null)
            dialogfragment.dismiss();
        if(serviceresult != null && serviceresult.hasError())
        {
            Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
        } else
        {
            mTemporalRsvpState = null;
            mSendRsvpReqId = null;
            if(mEvent != null)
            {
                updateActiveEventState();
                updateRsvpSection();
            }
        }
    }

    private void hideProgressDialog()
    {
        DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
        if(dialogfragment != null)
            dialogfragment.dismiss();
    }

    private void inviteMore()
    {
        startActivityForResult(Intents.getEditAudienceActivityIntent(getActivity(), getAccount(), getString(R.string.event_invite_activity_title), null, 11, false, false, true, false), 2);
    }

    private void processPendingPhotoRequest()
    {
        if(mNewPhotoReqId != null && !EsService.isRequestPending(mNewPhotoReqId.intValue()))
        {
            EsService.removeResult(mNewPhotoReqId.intValue());
            ServiceResult serviceresult = EsService.removeResult(mNewPhotoReqId.intValue());
            String s = EsService.getLastCameraMediaLocation();
            handleNewPhotoComplete(mNewPhotoReqId.intValue(), serviceresult, s);
            mNewPhotoReqId = null;
        }
    }

    private void showPhotoDialog()
    {
        recordUserAction(OzActions.COMPOSE_CHOOSE_PHOTO);
        startActivityForResult(Intents.newPhotosActivityIntentBuilder(getActivity()).setAccount(mAccount).setAlbumType("camera_photos").setPhotoPickerMode(Integer.valueOf(2)).setPhotoPickerTitleResourceId(Integer.valueOf(R.string.photo_picker_album_label_share)).setTakePhoto(true).setTakeVideo(true).build(), 3);
    }

    private void showProgressDialog(int i)
    {
        ProgressFragmentDialog.newInstance(null, getString(i), false).show(getFragmentManager(), "req_pending");
    }
    
    private void toggleInstantShare(boolean flag)
    {
    	if(flag == mEventState.isInstantShareEnabled) {
    		return;
    	}
    	
    	FragmentActivity fragmentactivity = getActivity();
        if(fragmentactivity != null)
        {
            InstantShareToggle instantsharetoggle = new InstantShareToggle();
            Object aobj[] = new Object[2];
            aobj[0] = Boolean.valueOf(flag);
            aobj[1] = fragmentactivity;
            instantsharetoggle.execute(aobj);
        }
    }

    private void turnOnInstantShare(boolean flag, boolean flag1)
    {
        if(flag1)
        {
            recordUserAction(OzActions.COMPOSE_TAKE_PHOTO);
            try
            {
                getActivity();
                startActivityForResult(Intents.getCameraIntentPhoto("camera-event.jpg"), 0);
            }
            catch(ActivityNotFoundException activitynotfoundexception)
            {
                Toast.makeText(getActivity(), R.string.change_photo_no_camera, 1).show();
            }
        }
        if(!EsEventData.isViewerCheckedIn(mEvent) && flag)
            EsService.sendEventRsvp(getActivity(), getAccount(), mEventId, mAuthKey, "CHECKIN");
        toggleInstantShare(true);
    }

    private void updateActiveEventState()
    {
        boolean flag = true;
        long now = System.currentTimeMillis();
        mEventState.hasUserInteracted = mHasUserInteracted;
        String s = getAccount().getGaiaId();
        mEventState.isOwner = TextUtils.equals(s, mEvent.getPublisher());
        mEventState.isInstantShareAvailable = false;
        mEventState.isInstantShareExpired = false;
        EventActiveState eventactivestate;
        if(EsEventData.isInstantShareAllowed(mEvent, s, now))
            mEventState.isInstantShareAvailable = flag;
        else
        if(EsEventData.isEventOver(mEvent, now))
        {
            mEventState.isInstantShareExpired = flag;
        } else
        {
            if(mRefreshRunnable == null)
                mRefreshRunnable = new EventRefreshRunnable();
            mHandler.removeCallbacks(mRefreshRunnable);
            long l1 = EsEventData.timeUntilInstantShareAllowed(mEvent, s, now);
            if(l1 > 0L)
                mHandler.postDelayed(mRefreshRunnable, l1);
        }
        mEventState.canInviteOthers = EsEventData.canInviteOthers(mEvent, mAccount);
        eventactivestate = mEventState;
        if(mSendRsvpReqId != null)
            flag = false;
        eventactivestate.isRsvpEnabled = flag;
        mEventState.temporalRsvpValue = mTemporalRsvpState;
        mEventState.eventSource = mSource;
        if(getAccount().isPlusPage())
        {
            mEventState.isInstantShareAvailable = false;
            mEventState.isInstantShareExpired = false;
        }
        if(mTypeId == 58 && mEventState.isInstantShareAvailable && !mEventState.isInstantShareEnabled)
            mHandler.post(new Runnable() {

                public final void run()
                {
                    if(!isPaused())
                        onInstantShareToggle(true);
                }
            });
        mTypeId = 0;
    }

    private void updateProgressIndicator()
    {
        HostActionBar hostactionbar = getActionBar();
        if(!mGhostEvent && (mFetchReqId != null || !mEventLoaded))
            hostactionbar.showProgressIndicator();
        else
            hostactionbar.hideProgressIndicator();
    }
    
    private void updateRsvpSection()
    {
        View view = getView();
        if(null == view) {
        	return;
        }
        
        EventRsvpLayout eventrsvplayout = (EventRsvpLayout)view.findViewById(R.id.event_rsvp_section);
        if(eventrsvplayout != null)
        {
            eventrsvplayout.bind(mEvent, mEventState, this);
            eventrsvplayout.invalidate();
        }
    }

    private void updateView(View view)
    {
    	if(null == view) {
    		return;
    	}
    	
    	TextView textview = (TextView)view.findViewById(R.id.server_error);
        View view1 = view.findViewById(R.id.grid);
        if(mGhostEvent) {
        	textview.setVisibility(0);
            textview.setText(R.string.event_does_not_exist);
            view1.setVisibility(8);
            showContent(view);
        } else {
        	if(mEvent != null)
            {
                textview.setVisibility(8);
                view1.setVisibility(0);
                showContent(view);
            } else
            if(!mEventLoaded || mFetchReqId != null)
            {
                view1.setVisibility(8);
                textview.setVisibility(8);
                showEmptyViewProgress(view);
            } else
            if(mError)
            {
                textview.setVisibility(0);
                textview.setText(R.string.event_details_error);
                view1.setVisibility(8);
                showContent(view);
            }
        }
        updateProgressIndicator();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.EVENT;
    }

    protected final void handleReportEventCallback(int i, ServiceResult serviceresult)
    {
        if(mReportAbuseRequestId != null && mReportAbuseRequestId.intValue() == i)
        {
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            mReportAbuseRequestId = null;
            updateSpinner();
            FragmentActivity fragmentactivity = getActivity();
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(fragmentactivity, R.string.transient_server_error, 0).show();
            else
                Toast.makeText(fragmentactivity, R.string.report_abuse_event_completed_toast, 0).show();
        }
    }

    protected final void handleSharePhotosToEventCallBack(ServiceResult serviceresult)
    {
        if(serviceresult.hasError())
            Toast.makeText(getSafeContext(), R.string.event_photo_share_failed_toast, 0).show();
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mEvent == null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final boolean isProgressIndicatorVisible()
    {
        boolean flag;
        if(super.isProgressIndicatorVisible() || mReportAbuseRequestId != null)
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    public final void onActionButtonClicked(int i)
    {
    	if(0 == i) {
    		showPhotoDialog();
    	} else if(1 == i) {
    		CommentEditFragmentDialog commenteditfragmentdialog = CommentEditFragmentDialog.newInstance(R.string.event_comment_dialog_title);
            commenteditfragmentdialog.setTargetFragment(this, 1);
            commenteditfragmentdialog.show(getFragmentManager(), "comment");
    	}
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
    	if(-1 != j) {
    		return;
    	}
    	
        switch(i)
        {
        case 0: // '\0'
            FragmentActivity fragmentactivity1 = getActivity();
            if(fragmentactivity1 instanceof ImageUtils.InsertCameraPhotoDialogDisplayer)
                ((ImageUtils.InsertCameraPhotoDialogDisplayer)fragmentactivity1).showInsertCameraPhotoDialog();
            mNewPhotoReqId = EsService.insertCameraPhoto(fragmentactivity1, mAccount, "camera-event.jpg");
            break;

        case 3: // '\003'
            if(j == -1 && intent != null && (!intent.hasExtra("media_taken") || !mEventState.isInstantShareEnabled))
                if(intent.hasExtra("insert_photo_request_id"))
                {
                    FragmentActivity fragmentactivity = getActivity();
                    if(fragmentactivity instanceof ImageUtils.InsertCameraPhotoDialogDisplayer)
                        ((ImageUtils.InsertCameraPhotoDialogDisplayer)fragmentactivity).showInsertCameraPhotoDialog();
                    mNewPhotoReqId = Integer.valueOf(intent.getIntExtra("insert_photo_request_id", 0));
                    processPendingPhotoRequest();
                } else
                {
                    ArrayList arraylist = intent.getParcelableArrayListExtra("mediarefs");
                    if(arraylist != null)
                    {
                        StringBuilder stringbuilder = new StringBuilder();
                        Iterator iterator = arraylist.iterator();
                        do
                        {
                            if(!iterator.hasNext())
                                break;
                            Uri uri = ((MediaRef)iterator.next()).getLocalUri();
                            if(uri != null)
                                stringbuilder.append((new StringBuilder()).append(uri.toString()).append(" ").toString());
                        } while(true);
                        final FragmentActivity context = getActivity();
                        Toast.makeText(context, R.string.event_post_photo, 1).show();
                        AsyncTask asynctask = new AsyncTask() {
                        	
                        	List skippedPhotoIds = new ArrayList();
                        	String currentEventId;
                        	EsAccount currentAccount = getAccount();
                            private Void doInBackground(String as1[])
                            {
                                String s;
                                Uri uri1;
                                LinkedHashSet linkedhashset;
                                Cursor cursor = null;
                                s = currentAccount.getName();
                                String s1 = as1[0];
                                ContentResolver contentresolver = context.getContentResolver();
                                uri1 = InstantUploadFacade.PHOTOS_URI.buildUpon().appendQueryParameter("account", mAccount.getName()).build();
                                StringTokenizer stringtokenizer = new StringTokenizer(s1);
                                linkedhashset = new LinkedHashSet();
                                for(; stringtokenizer.hasMoreTokens(); linkedhashset.add(stringtokenizer.nextToken()));
                                try {
	                                for(cursor = contentresolver.query(uri1, (String[])linkedhashset.toArray(new String[linkedhashset.size()]), null, null, null); cursor.moveToNext(); skippedPhotoIds.add(Long.valueOf(cursor.getLong(1))))
	                                    linkedhashset.remove(cursor.getString(0));
                                } finally {
                                	if(null != cursor) {
                                		cursor.close();
                                	}
                                }

                                HostedEventFragment.access$1500(HostedEventFragment.this, context, s, currentEventId, (String[])linkedhashset.toArray(new String[linkedhashset.size()]));
                                return null;
                            }

                            protected final Object doInBackground(Object aobj[])
                            {
                                return doInBackground((String[])aobj);
                            }

                            protected final void onPostExecute(Object obj)
                            {
                                if(skippedPhotoIds.size() > 0)
                                    EsService.sharePhotosToEvents(context, currentAccount, currentEventId, skippedPhotoIds);
                            }
                        };
                        String as[] = new String[1];
                        as[0] = stringbuilder.toString();
                        asynctask.execute(as);
                    }
                }
            break;

        case 2: // '\002'
            final AudienceData audience = (AudienceData)intent.getParcelableExtra("audience");
            mHandler.post(new Runnable() {

                public final void run()
                {
                    HostedEventFragment.access$1400(HostedEventFragment.this, audience);
                }
            });
            break;
        }
    }

    public final void onAddPhotosClicked()
    {
        showPhotoDialog();
    }

    public final void onAvatarClicked(String s)
    {
        startActivity(Intents.getProfileActivityByGaiaIdIntent(getActivity(), getAccount(), s, null));
    }

    public final void onCommentEditComplete(Spannable spannable)
    {
        if(mActivityId != null && !TextUtils.isEmpty(spannable))
        {
            getActivity();
            getAccount();
            String s = ApiUtils.buildPostableString(spannable);
            showProgressDialog(R.string.event_comment_sending);
            mCommentReqId = Integer.valueOf(EsService.createEventComment(getActivity(), getAccount(), mActivityId, mEventId, mAuthKey, s));
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mEventState.expanded = true;
        if(bundle != null)
        {
            mEventId = bundle.getString("id");
            mTypeId = bundle.getInt("typeid");
            mInvitationToken = bundle.getString("invitation_token");
            mIncomingRsvpType = bundle.getString("incoming_rsvp_type");
            mNeedsRefresh = bundle.getBoolean("refresh", false);
            mSavedScrollPos = bundle.getInt("scroll_pos", -1);
            mFirstActivityTimestamp = bundle.getLong("first_timestamp");
            if(bundle.containsKey("fetch_req_id"))
                mFetchReqId = Integer.valueOf(bundle.getInt("fetch_req_id"));
            if(bundle.containsKey("comment_req_id"))
                mCommentReqId = Integer.valueOf(bundle.getInt("comment_req_id"));
            if(bundle.containsKey("new_photo_req_id"))
                mNewPhotoReqId = Integer.valueOf(bundle.getInt("new_photo_req_id"));
            if(bundle.containsKey("invite_more_req_id"))
                mInviteReqId = Integer.valueOf(bundle.getInt("invite_more_req_id"));
            if(bundle.containsKey("rsvp_req_id"))
                mSendRsvpReqId = Integer.valueOf(bundle.getInt("rsvp_req_id"));
            if(bundle.containsKey("temp_rsvp_state"))
                mTemporalRsvpState = bundle.getString("temp_rsvp_state");
            if(bundle.containsKey("delete_req_id"))
                mDeleteReqId = Integer.valueOf(bundle.getInt("delete_req_id"));
            if(bundle.containsKey("abuse_request_id"))
                mReportAbuseRequestId = Integer.valueOf(bundle.getInt("abuse_request_id"));
            mEventState.expanded = bundle.getBoolean("expanded", true);
            invalidateActionBar();
        } else
        if(mEventId != null)
        {
            mNeedsRefresh = true;
        } else
        {
            mGhostEvent = true;
            updateView(getView());
        }
        if(!TextUtils.isEmpty(mIncomingRsvpType))
        {
            onRsvpChanged(mIncomingRsvpType);
            mIncomingRsvpType = null;
        }
    }
    
    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        final FragmentActivity final_context1 = getActivity();
        Loader loader = null;
        switch(i) {
	        case 0:
	        	loader = new EsCursorLoader(final_context1, EsProvider.EVENTS_ALL_URI) {

	                public final Cursor esLoadInBackground()
	                {
	                    return EsEventData.getEvent(final_context1, mAccount, mEventId, DetailsQuery.PROJECTION);
	                }
	            };
	        	break;
	        case 1:
	        	loader = new EsCursorLoader(final_context1, EsProvider.EVENTS_ALL_URI) {

	                public final Cursor esLoadInBackground()
	                {
	                    return EsEventData.getEventActivities(final_context1, mAccount, mEventId, ActivityQuery.PROJECTION);
	                }
	            };
	        	break;
	        case 2:
	        case 3:
	        	break;
	        case 4:
	        	loader = new EsCursorLoader(final_context1, EsProvider.EVENTS_ALL_URI) {

	                public final Cursor esLoadInBackground()
	                {
	                    return EsEventData.getEventResolvedPeople(final_context1, mAccount, mEventId, EventPeopleQuery.PROJECTION);
	                }
	            };
	        	break;
        	default:
        		break;
        }
        return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.hosted_event_fragment, viewgroup, false);
        mGridView = (ColumnGridView)view.findViewById(R.id.grid);
        FragmentActivity fragmentactivity = getActivity();
        EsAccount _tmp = mAccount;
        mAdapter = new EventDetailsActivityAdapter(fragmentactivity, mGridView, this, this);
        mGridView.setAdapter(mAdapter);
        getLoaderManager().initLoader(2, null, mSettingsCallbacks);
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
        getLoaderManager().initLoader(4, null, this);
        if(mNextPagePreloadTriggerRows == 0)
            if(ScreenMetrics.getInstance(view.getContext()).screenDisplayType == 0)
                mNextPagePreloadTriggerRows = 8;
            else
                mNextPagePreloadTriggerRows = 16;
        updateView(view);
        return view;
    }

    public final void onDialogCanceled(String s)
    {
    }

    public final void onDialogListClick(int i, Bundle bundle)
    {
    }

    public final void onDialogNegativeClick(String s)
    {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s)
    {
        if(!"dialog_photo_sync".equals(s)) {
        	if("report_event".equals(s))
            {
                mReportAbuseRequestId = Integer.valueOf(EsService.reportActivity(getActivity(), mAccount, mActivityId, null));
                showProgressDialog(R.string.report_abuse_operation_pending);
            } 
        } else { 
        	turnOnInstantShare(false, false);
        }
    }

    public final void onExpansionToggled(boolean flag)
    {
        mEventState.expanded = flag;
    }

    public final void onHangoutClicked()
    {
        if(mEvent.getHangoutInfo() != null)
            startActivity(Intents.getEventHangoutActivityIntent(getActivity(), mAccount, mEventId));
    }

    public final void onInstantShareToggle(boolean flag)
    {
        if(!flag) {
        	toggleInstantShare(false);
        } else {
            boolean flag1 = InstantUpload.isSyncEnabled((EsAccount)getActivity().getIntent().getParcelableExtra("account"));
            boolean flag2 = ContentResolver.getMasterSyncAutomatically();
            if(!flag2 || !flag1) {
            	if(!flag2)
                {
                    FragmentManager fragmentmanager1 = getFragmentManager();
                    if(fragmentmanager1.findFragmentByTag("dialog_master_sync") == null)
                    {
                        AlertFragmentDialog alertfragmentdialog1 = AlertFragmentDialog.newInstance(getString(R.string.event_instant_share_dialog_title), getString(R.string.event_master_sync_dialog_message), getString(R.string.ok), null);
                        alertfragmentdialog1.setTargetFragment(this, 0);
                        alertfragmentdialog1.show(fragmentmanager1, "dialog_master_sync");
                    }
                } else
                {
                    FragmentManager fragmentmanager = getFragmentManager();
                    if(fragmentmanager.findFragmentByTag("dialog_photo_sync") == null)
                    {
                        String s = getString(R.string.es_google_iu_provider);
                        String s1 = getString(R.string.event_enable_sync_dialog_message, new Object[] {
                            s
                        });
                        AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.event_instant_share_dialog_title), s1, getString(R.string.yes), getString(R.string.no));
                        alertfragmentdialog.setTargetFragment(this, 0);
                        alertfragmentdialog.show(fragmentmanager, "dialog_photo_sync");
                    }
                } 
            } else { 
            	FragmentManager fragmentmanager2 = getFragmentManager();
                if(fragmentmanager2.findFragmentByTag("dialog_check_in") == null)
                {
                    boolean flag3;
                    DialogType dialogtype;
                    InstantShareConfirmationDialog instantshareconfirmationdialog;
                    if(!EsEventData.isViewerCheckedIn(mEvent))
                        flag3 = true;
                    else
                        flag3 = false;
                    
                    EventOptions options = mEvent.getOptions();
                    if(null != options && options.isBroadcast())
                        dialogtype = DialogType.ON_AIR;
                    else
                    if(Boolean.TRUE.equals(mEvent.isPublic()))
                        dialogtype = DialogType.PUBLIC;
                    else
                        dialogtype = DialogType.PRIVATE;
                    instantshareconfirmationdialog = new InstantShareConfirmationDialog(flag3, dialogtype);
                    instantshareconfirmationdialog.show(fragmentmanager2, "dialog_check_in");
                    instantshareconfirmationdialog.setTargetFragment(this, 0);
                }
            }
        }
        
        mHasUserInteracted = true;
    }

    public final void onInviteMoreClicked()
    {
        inviteMore();
    }

    public final void onLinkClicked(String s)
    {
        Context context = getSafeContext();
        if(s.startsWith("https://plus.google.com/s/%23"))
        {
            String s1 = (new StringBuilder("#")).append(s.substring(29)).toString();
            startActivity(Intents.getPostSearchActivityIntent(context, mAccount, s1));
        } else
        {
            if(Intents.isProfileUrl(s))
            {
                Bundle bundle = EsAnalyticsData.createExtras("extra_gaia_id", Intents.getPersonIdFromProfileUrl(s));
                recordUserAction(OzActions.ONE_UP_SELECT_PERSON, bundle);
            }
            Intents.viewContent(getActivity(), mAccount, s);
        }
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        mAdapter.checkPartitions("HEF", "OLF");
        switch(loader.getId()) {
	        case 0:
	        	mPreloadRequested = false;
	            mEventLoaded = true;
	            if(cursor.moveToFirst())
	            {
	                mSource = cursor.getInt(6);
	                mEvent = (Event)JsonUtil.fromByteArray(cursor.getBlob(1), Event.class);
	                mAuthKey = mEvent.getAuthKey();
	                boolean flag;
	                if(cursor.getInt(5) != 0)
	                    flag = true;
	                else
	                    flag = false;
	                mCanComment = flag;
	                if(mEventState.isInstantShareEnabled && mEventState.isInstantShareExpired)
	                    onInstantShareToggle(false);
	                mAdapter.changeInfoCursor(cursor, mEventState);
	                if(mSavedScrollPos != -1 && mGridView != null && mAdapter.isWrapContentEnabled())
	                {
	                    mGridView.setSelection(mSavedScrollPos);
	                    mSavedScrollPos = -1;
	                }
	                mPollingToken = cursor.getString(2);
	                mResumeToken = cursor.getString(3);
	                mActivityId = cursor.getString(4);
	                invalidateActionBar();
	                if(mNeedsRefresh)
	                    fetchData();
	                updateActiveEventState();
	            } else
	            {
	                mAdapter.changeInfoCursor(null, mEventState);
	                if(mGhostEvent)
	                {
	                    mEvent = null;
	                    mEventLoaded = false;
	                    mFetchReqId = null;
	                    mSendRsvpReqId = null;
	                    mError = true;
	                } else
	                {
	                    fetchData();
	                }
	            }
	            updateView(getView());
	        	break;
	        case 1:
	        	mAdapter.changeActivityCursor(cursor);
	            if(cursor == null)
	                mFirstActivityTimestamp = 0L;
	            else
	            if(cursor.moveToFirst())
	            {
	                long l = cursor.getLong(4);
	                if(l != mFirstActivityTimestamp)
	                {
	                    mFirstActivityTimestamp = l;
	                    mGridView.setSelectionToTop();
	                }
	            }
	        	break;
	        case 2:
	        case 3:
	        	break;
	        case 4:
	        	HashMap hashmap = new HashMap();
	            if(cursor != null && cursor.moveToFirst())
	            {
	                String s;
	                for(; cursor.moveToNext(); hashmap.put(s, new EsEventData.ResolvedPerson(cursor.getString(3), s, cursor.getString(4))))
	                    s = cursor.getString(1);

	            }
	            mAdapter.setResolvedPeople(hashmap);
	            getLoaderManager().restartLoader(1, null, this);
	        	break;
        	default:
        		break;
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onLocationClicked()
    {
        if(mEvent.getLocation() != null)
        	;
            //MapUtils.showDrivingDirections(getActivity(), mEvent.getLocation());
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(i == R.id.edit_event)
            startActivity(Intents.getEditEventActivityIntent(getActivity().getApplicationContext(), mAccount, mEventId, mAuthKey));
        else
        if(i == R.id.delete_event)
        {
            DeleteEventConfirmationDialog deleteeventconfirmationdialog = new DeleteEventConfirmationDialog();
            deleteeventconfirmationdialog.show(getFragmentManager(), "delete_event_conf");
            deleteeventconfirmationdialog.setTargetFragment(this, 0);
        } else
        if(i == R.id.invite_more)
            inviteMore();
        else
        if(i == R.id.report_abuse)
        {
            recordUserAction(OzActions.ONE_UP_REPORT_ABUSE_ACTIVITY);
            AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.menu_report_abuse), getString(R.string.event_report_question), getString(R.string.ok), getString(R.string.cancel));
            alertfragmentdialog.setTargetFragment(this, 0);
            alertfragmentdialog.getArguments().putString("activity_id", mActivityId);
            alertfragmentdialog.show(getFragmentManager(), "report_event");
        } else
        {
            flag = false;
        }
        return flag;
    }

    public final void onPause()
    {
        getActivity().getContentResolver().unregisterContentObserver(mSettingsObserver);
        EsService.unregisterListener(mListener);
        EventDetailsHeaderView eventdetailsheaderview = (EventDetailsHeaderView)getView().findViewById(R.id.event_header_view);
        if(eventdetailsheaderview != null)
            eventdetailsheaderview.pausePlayback();
        super.onPause();
    }

    public final void onPhotoClicked(String s, String s1, String s2)
    {
        Intents.PhotoViewIntentBuilder photoviewintentbuilder = Intents.newPhotoViewActivityIntentBuilder(getActivity());
        photoviewintentbuilder.setAccount(getAccount());
        if(s != null)
        {
            String name;
            if(mEvent.getName() != null)
            	name = mEvent.getName();
            else
            	name = getString(R.string.event_activity_title);
            photoviewintentbuilder.setAlbumName(name);
            photoviewintentbuilder.setPhotoId(Long.valueOf(Long.parseLong(s)));
            photoviewintentbuilder.setGaiaId(s2);
            photoviewintentbuilder.setEventId(mEventId);
        } else {
            String s3;
            if(mEvent.getName() != null)
                s3 = mEvent.getName();
            else
                s3 = getString(R.string.event_activity_title);
            photoviewintentbuilder.setAlbumName(s3);
            photoviewintentbuilder.setPhotoUrl(s1);
        }
        startActivity(photoviewintentbuilder.build());
    }

    public final void onPhotoUpdateNeeded(String s, String s1, String s2)
    {
        EsService.updateEventPhoto(getSafeContext(), mAccount, mEventId, s, s1, s2);
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        super.onPrepareActionBar(hostactionbar);
        hostactionbar.showRefreshButton();
        boolean flag;
        if(mAccount != null && mAccount.isPlusPage())
            flag = true;
        else
            flag = false;
        if(mAccount != null && EsEventData.canAddPhotos(mEvent, mAccount.getGaiaId()) && mActivityId != null && !flag)
            hostactionbar.showActionButton(0, R.drawable.icn_events_add_photo, R.string.event_button_add_photo_label);
        if(mCanComment)
            hostactionbar.showActionButton(1, R.drawable.icn_events_add_comment, R.string.event_button_add_comment_label);
        updateProgressIndicator();
    }

    public final void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        boolean flag;
        boolean flag1;
        if(mEvent != null && mAccount != null && TextUtils.equals(mEvent.getPublisher(), mAccount.getGaiaId()))
            flag = true;
        else
            flag = false;
        if(flag)
        {
            menu.findItem(R.id.edit_event).setVisible(true);
            menu.findItem(R.id.delete_event).setVisible(true);
        }
        flag1 = false;
        if(!flag)
        {
            String s = mActivityId;
            flag1 = false;
            if(s != null)
            {
                Integer integer = mReportAbuseRequestId;
                flag1 = false;
                if(integer == null)
                    flag1 = true;
            }
        }
        if(mEventState != null && mEventState.canInviteOthers)
            menu.findItem(R.id.invite_more).setVisible(true);
        if(flag1)
            menu.findItem(R.id.report_abuse).setVisible(true);
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mListener);
        mAdapter.checkPartitions("HEF", "OR");
        if(mFetchReqId != null && !EsService.isRequestPending(mFetchReqId.intValue()))
        {
            ServiceResult serviceresult4 = EsService.removeResult(mFetchReqId.intValue());
            handleGetEventUpdatesComplete(mFetchReqId.intValue(), serviceresult4);
            mFetchReqId = null;
        }
        if(mSendRsvpReqId != null && !EsService.isRequestPending(mSendRsvpReqId.intValue()))
        {
            ServiceResult serviceresult3 = EsService.removeResult(mSendRsvpReqId.intValue());
            handleSendEventRsvpComplete(mSendRsvpReqId.intValue(), serviceresult3);
            mSendRsvpReqId = null;
        }
        if(mCommentReqId != null && !EsService.isRequestPending(mCommentReqId.intValue()))
        {
            ServiceResult serviceresult2 = EsService.removeResult(mCommentReqId.intValue());
            handleCreateCommentComplete(mCommentReqId.intValue(), serviceresult2);
            mCommentReqId = null;
        }
        processPendingPhotoRequest();
        if(mInviteReqId != null && !EsService.isRequestPending(mInviteReqId.intValue()))
        {
            ServiceResult serviceresult1 = EsService.removeResult(mInviteReqId.intValue());
            handleInviteMoreComplete(mInviteReqId.intValue(), serviceresult1);
            mInviteReqId = null;
        }
        if(mReportAbuseRequestId != null && !EsService.isRequestPending(mReportAbuseRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mReportAbuseRequestId.intValue());
            handleReportEventCallback(mReportAbuseRequestId.intValue(), serviceresult);
            mReportAbuseRequestId = null;
        }
        getActivity().getContentResolver().registerContentObserver(InstantUploadFacade.SETTINGS_URI, false, mSettingsObserver);
    }

    public final void onRsvpChanged(String s)
    {
        if(mEvent == null || !TextUtils.equals(s, EsEventData.getRsvpType(mEvent)))
        {
            mSendRsvpReqId = Integer.valueOf(EsService.sendEventRsvp(getActivity(), mAccount, mEventId, mAuthKey, s));
            mTemporalRsvpState = s;
            if(mEvent != null)
            {
                updateActiveEventState();
                updateRsvpSection();
            }
            if(!TextUtils.isEmpty(mIncomingRsvpType))
            {
                ProgressFragmentDialog.newInstance(null, getString(R.string.event_send_rsvp), false).show(getFragmentManager(), "send_rsvp");
                mIncomingRsvpType = null;
            }
        }
        mHasUserInteracted = true;
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putString("id", mEventId);
        bundle.putInt("typeid", mTypeId);
        bundle.putString("invitation_token", mInvitationToken);
        bundle.putString("incoming_rsvp_type", mIncomingRsvpType);
        bundle.putBoolean("refresh", mNeedsRefresh);
        bundle.putBoolean("expanded", mEventState.expanded);
        bundle.putLong("first_timestamp", mFirstActivityTimestamp);
        if(mGridView != null)
            bundle.putInt("scroll_pos", mGridView.getFirstVisiblePosition());
        if(mFetchReqId != null)
            bundle.putInt("fetch_req_id", mFetchReqId.intValue());
        if(mSendRsvpReqId != null)
            bundle.putInt("rsvp_req_id", mSendRsvpReqId.intValue());
        if(mTemporalRsvpState != null)
            bundle.putString("temp_rsvp_state", mTemporalRsvpState);
        if(mCommentReqId != null)
            bundle.putInt("comment_req_id", mCommentReqId.intValue());
        if(mNewPhotoReqId != null)
            bundle.putInt("new_photo_req_id", mNewPhotoReqId.intValue());
        if(mInviteReqId != null)
            bundle.putInt("invite_more_req_id", mInviteReqId.intValue());
        if(mDeleteReqId != null)
            bundle.putInt("delete_req_id", mDeleteReqId.intValue());
        if(mReportAbuseRequestId != null)
            bundle.putInt("abuse_request_id", mReportAbuseRequestId.intValue());
        mAdapter.checkPartitions("HEF", "ON");
    }

    protected final void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mEventId = bundle.getString("event_id");
        mInvitationToken = bundle.getString("invitation_token");
        mAuthKey = bundle.getString("auth_key");
        mIncomingRsvpType = bundle.getString("rsvp");
        mTypeId = bundle.getInt("notif_type");
    }

    public final void onUpdateCardClicked(EventUpdate eventupdate)
    {
        FragmentManager fragmentmanager = getFragmentManager();
        if(fragmentmanager.findFragmentByTag("update_card") == null)
        {
            EventUpdateDialog eventupdatedialog = EventUpdateDialog.newInstance();
            eventupdatedialog.setUpdate(eventupdate);
            eventupdatedialog.setTargetFragment(this, 0);
            eventupdatedialog.show(fragmentmanager, "update_card");
        }
    }

    public final void onViewAllInviteesClicked()
    {
        FragmentActivity fragmentactivity = getActivity();
        EsAccount esaccount = getAccount();
        String s = mEventId;
        String s1 = mAuthKey;
        String s2;
        if(mEvent != null)
            s2 = mEvent.getPublisher();
        else
            s2 = null;
        startActivity(Intents.getEventInviteeListActivityIntent(fragmentactivity, esaccount, s, s1, s2));
    }

    public final void onViewUsed(int i)
    {
        if(!mPreloadRequested && mResumeToken != null && !mError && mGridView != null && i >= mAdapter.getCount() - mNextPagePreloadTriggerRows)
        {
            mPreloadRequested = true;
            mGridView.post(new Runnable() {

                public final void run()
                {
                    if(!isPaused())
                    	HostedEventFragment.access$1300(HostedEventFragment.this);
                }

            });
        }
    }

    public final void refresh()
    {
        super.refresh();
        fetchData();
    }
    
    
    static void access$1000(HostedEventFragment hostedeventfragment, int i, ServiceResult serviceresult)
    {
        if(hostedeventfragment.mDeleteReqId != null && i == hostedeventfragment.mDeleteReqId.intValue())
        {
            hostedeventfragment.hideProgressDialog();
            hostedeventfragment.mDeleteReqId = null;
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(hostedeventfragment.getActivity(), R.string.transient_server_error, 0).show();
            else
                hostedeventfragment.getActivity().finish();
        }
        return;
    }
    
    static void access$1300(HostedEventFragment hostedeventfragment)
    {
        hostedeventfragment.mFetchReqId = Integer.valueOf(EsService.readEvent(hostedeventfragment.getActivity(), hostedeventfragment.mAccount, hostedeventfragment.mEventId, hostedeventfragment.mPollingToken, hostedeventfragment.mResumeToken, null, hostedeventfragment.mAuthKey, false));
        hostedeventfragment.updateProgressIndicator();
        return;
    }
    
    static void access$1400(HostedEventFragment hostedeventfragment, AudienceData audiencedata)
    {
        if(hostedeventfragment.getActivity() != null && hostedeventfragment.mEventId != null)
        {
            hostedeventfragment.showProgressDialog(R.string.event_inviting_more);
            FragmentActivity fragmentactivity = hostedeventfragment.getActivity();
            EsAccount esaccount = hostedeventfragment.getAccount();
            String s = hostedeventfragment.mEventId;
            String s1 = hostedeventfragment.mAuthKey;
            String s2;
            if(hostedeventfragment.mEvent != null)
                s2 = hostedeventfragment.mEvent.getPublisher();
            else
                s2 = null;
            hostedeventfragment.mInviteReqId = Integer.valueOf(EsService.invitePeopleToEvent(fragmentactivity, esaccount, s, s1, s2, audiencedata));
        }
        return;
    }
    
    static void access$1500(HostedEventFragment hostedeventfragment, Context context, String s, String s1, String as[])
    {
        Cursor cursor = null;
        ContentValues contentvalues = new ContentValues();
        String s2;
        ContentResolver contentresolver;
        Cursor cursor1;
        if(TextUtils.equals(s1, InstantUpload.getInstantShareEventId(context)))
            s2 = "camera-sync";
        else
            s2 = "events";
        for(String s3 : as) {
	        contentvalues.clear();
	        contentvalues.put("album_id", s2);
	        contentvalues.put("upload_account", s);
	        contentvalues.put("media_url", s3);
	        contentvalues.put("event_id", s1);
	        contentresolver = context.getContentResolver();
	        try {
	        	cursor = contentresolver.query(Uri.parse(s3), MediaStoreUtils.MEDIA_ID_PROJECTION, null, null, null);
		        if(null != cursor && cursor.moveToNext()) {
		        	contentvalues.put("media_id", Long.valueOf(cursor.getLong(0)));
		        }
		        contentresolver.insert(InstantUploadFacade.UPLOADS_URI, contentvalues);
	        } finally {
	        	if(null != cursor) {
	        		cursor.close();
	        	}
	        }
        }
    }
    
    
    
    static void access$1700(HostedEventFragment hostedeventfragment)
    {
        hostedeventfragment.showProgressDialog(R.string.event_deleting);
        hostedeventfragment.mDeleteReqId = Integer.valueOf(EsService.deleteEvent(hostedeventfragment.getActivity(), hostedeventfragment.getAccount(), hostedeventfragment.mEventId, hostedeventfragment.mAuthKey));
        return;
    }
    
    public static interface ActivityQuery
    {

        public static final String PROJECTION[] = {
            "_id", "type", "owner_gaia_id", "owner_name", "timestamp", "data", "url", "comment", "fingerprint"
        };

    }

    public static class DeleteEventConfirmationDialog extends DialogFragment implements android.content.DialogInterface.OnClickListener
    {

        public void onClick(DialogInterface dialoginterface, int i) {
        	if(-2 == i) {
        		dialoginterface.dismiss();
        	} else if(-1 == i) {
        		if(getTargetFragment() instanceof HostedEventFragment)
                    ((HostedEventFragment)getTargetFragment()).showProgressDialog(i);
        	}
        }

        public final Dialog onCreateDialog(Bundle bundle)
        {
            FragmentActivity fragmentactivity = getActivity();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragmentactivity);
            builder.setMessage(fragmentactivity.getString(R.string.delete_event_dialog_message));
            builder.setPositiveButton(0x104000a, this);
            builder.setNegativeButton(0x1040000, this);
            builder.setCancelable(true);
            return builder.create();
        }

        public DeleteEventConfirmationDialog()
        {
        }
    }

    public static interface DetailsQuery
    {

        public static final String PROJECTION[] = {
            "_id", "event_data", "polling_token", "resume_token", "activity_id", "can_comment", "source"
        };

    }

    public static enum DialogType {
    	PRIVATE,
    	ON_AIR,
    	PUBLIC;
    }

	public static interface EventPeopleQuery
	{
	
	    public static final String PROJECTION[] = {
	        "_id", "gaia_id", "person_id", "name", "avatar"
	    };
	
	}

	private final class EventRefreshRunnable implements Runnable
	{
	
	    public final void run()
	    {
	        if(getActivity() != null && !isPaused())
	            getLoaderManager().restartLoader(0, null, HostedEventFragment.this);
	    }
	
	}

	public static class InstantShareConfirmationDialog extends AlertFragmentDialog {
		
		private CheckBox mCheckinButton;
	    private DialogType mDialogType;
	    private boolean mFirstTime;
	    private boolean mHasCheckedIn;
	
	    public InstantShareConfirmationDialog()
	    {
	        mDialogType = DialogType.PRIVATE;
	    }
	
	    public InstantShareConfirmationDialog(boolean flag, DialogType dialogtype)
	    {
	        mDialogType = DialogType.PRIVATE;
	        mHasCheckedIn = flag;
	        mDialogType = dialogtype;
	    }
	    
	    public void onClick(DialogInterface dialoginterface, int i) {
	        if(-2 == i) {
	        	dismiss();
	        } else if(-1 == i) {
	        	 android.support.v4.app.Fragment fragment = getTargetFragment();
	 	        if(fragment instanceof HostedEventFragment)
	 	            ((HostedEventFragment)fragment).turnOnInstantShare(mCheckinButton.isChecked(), mFirstTime);
	        }
	    }
	
	    public final Dialog onCreateDialog(Bundle bundle)
	    {
	        int j = R.string.event_instant_share_dialog_content;
	        if(bundle != null)
	        {
	            mHasCheckedIn = bundle.getBoolean("has_checked_in_id", false);
	            mFirstTime = bundle.getBoolean("first_time_id", false);
	            mDialogType = DialogType.valueOf(bundle.getString("dialog_type"));
	        }
	        Context context = getDialogContext();
	        Resources resources = context.getResources();
	        View view = LayoutInflater.from(context).inflate(R.layout.event_instant_share_dialog_view, null);
	        mCheckinButton = (CheckBox)view.findViewById(R.id.checkin);
	        CheckBox checkbox = mCheckinButton;
	
	        int i;
	        TextView textview;
	        TextView textview1;
	        int k;
	        SharedPreferences sharedpreferences;
	        boolean flag;
	        boolean flag1;
	        android.app.AlertDialog.Builder builder;
	        if(mHasCheckedIn)
	            i = 0;
	        else
	            i = 8;
	        checkbox.setVisibility(i);
	        textview = (TextView)view.findViewById(R.id.dialog_content);
	        switch(mDialogType) {
		        case ON_AIR:
		        	j = R.string.event_instant_share_on_air_dialog_content;
		        	break;
		        case PUBLIC:
		        	j = R.string.event_instant_share_public_dialog_content;
		        	break;
		        default:
	        		break;
	        }
	        
	        textview.setText(j);
	        textview1 = (TextView)view.findViewById(R.id.link);
	        textview1.setText(Html.fromHtml(resources.getString(R.string.event_instant_share_dialog_learn_more)));
	        textview1.setMovementMethod(LinkMovementMethod.getInstance());
	        k = R.string.event_instant_share_dialog_positive;
	        sharedpreferences = context.getSharedPreferences("event", 0);
	        flag = sharedpreferences.contains("hasUsedInstantShare");
	        flag1 = false;
	        if(!flag)
	            flag1 = true;
	        mFirstTime = flag1;
	        if(mFirstTime)
	        {
	            sharedpreferences.edit().putBoolean("hasUsedInstantShare", true).commit();
	            k = R.string.event_instant_share_dialog_positive_first;
	        }
	        builder = new android.app.AlertDialog.Builder(context);
	        builder.setTitle(R.string.event_instant_share_dialog_title).setView(view).setPositiveButton(k, this).setNegativeButton(R.string.cancel, this);
	        return builder.create();
	    }
	
	    public final void onSaveInstanceState(Bundle bundle)
	    {
	        super.onSaveInstanceState(bundle);
	        bundle.putBoolean("has_checked_in_id", mHasCheckedIn);
	        bundle.putBoolean("first_time_id", mFirstTime);
	        bundle.putString("dialog_type", mDialogType.name());
	    }
	}

	final class InstantShareToggle extends AsyncTask
	{
		private Activity mActivity;
	    private boolean mEnabled;
	    
	    protected final Object doInBackground(Object aobj[])
	    {
	        mEnabled = ((Boolean)aobj[0]).booleanValue();
	        mActivity = (Activity)aobj[1];
	        EsEventData.enableInstantShare(mActivity, mEnabled, mEvent);
	        return null;
	    }
	
	    protected final void onPostExecute(Object obj)
	    {
	        if(getActivity() != null)
	            getLoaderManager().restartLoader(2, null, mSettingsCallbacks);
	    }
	}

	private final class SettingsLoaderCallbacks implements android.support.v4.app.LoaderManager.LoaderCallbacks {

	    public final Loader onCreateLoader(int i, Bundle bundle)
	    {
	        return new EsAsyncTaskLoader(getActivity()) {
	
	            public final Object esLoadInBackground()
	            {
	                return InstantUpload.getInstantShareEventId(getContext());
	            }
	
	            protected final void onStartLoading()
	            {
	                forceLoad();
	            }
	        };
	    }

	    public final void onLoadFinished(Loader loader, Object obj)
	    {
	        String s = (String)obj;
	        View view = getView();
	        if(view != null)
	        {
	            boolean flag = mEventState.isInstantShareEnabled;
	            mEventState.isInstantShareEnabled = TextUtils.equals(mEventId, s);
	            if(flag != mEventState.isInstantShareEnabled)
	            {
	                EventDetailOptionRowInstantShare eventdetailoptionrowinstantshare = (EventDetailOptionRowInstantShare)view.findViewById(R.id.event_instant_share_selection);
	                if(eventdetailoptionrowinstantshare != null)
	                {
	                    eventdetailoptionrowinstantshare.bind(mEventState);
	                    eventdetailoptionrowinstantshare.invalidate();
	                }
	            }
	        }
	    }

	    public final void onLoaderReset(Loader loader)
	    {
	    }
	}
}
