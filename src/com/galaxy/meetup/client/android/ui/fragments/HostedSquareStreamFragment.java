/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.SquareStreamAdapter;
import com.galaxy.meetup.client.android.StreamAdapter;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbSquareStream;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.EsSquaresData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.content.SquareTargetData;
import com.galaxy.meetup.client.android.controller.ComposeBarController;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.ItemClickListener;
import com.galaxy.meetup.client.android.ui.view.SquareLandingView;
import com.galaxy.meetup.client.android.ui.view.StreamCardView;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class HostedSquareStreamFragment extends HostedStreamFragment implements
		AlertDialogListener, SquareLandingView.OnClickListener {

	private boolean mAutoSubscribe;
    private Boolean mCanSeePosts;
    private int mCurrentSpinnerPosition;
    private boolean mFirstStreamListLoad;
    private boolean mFragmentCreated;
    private Integer mGetSquareRequestId;
    private Boolean mIsMember;
    private int mOperationType;
    private Integer mPendingRequestId;
    private ArrayAdapter mPrimarySpinnerAdapter;
    protected String mSquareId;
    private Boolean mSquareIsExpanded;
    private boolean mSquareLoaderActive;
    private final android.support.v4.app.LoaderManager.LoaderCallbacks mSquareLoaderCallbacks = new android.support.v4.app.LoaderManager.LoaderCallbacks() {

        public final Loader onCreateLoader(int i, Bundle bundle)
        {
            Uri uri = EsProvider.appendAccountParameter(EsProvider.SQUARES_URI.buildUpon().appendPath(mSquareId), mAccount).build();
            return new EsCursorLoader(getActivity(), uri, EsSquaresData.SQUARES_PROJECTION, null, null, null);
        }

        public final void onLoadFinished(Loader loader, Object obj) {
            boolean flag = true;
            Cursor cursor = (Cursor)obj;
            if(EsLog.isLoggable("HostedSquareStreamFrag", 4))
                Log.i("HostedSquareStreamFrag", "onLoadFinished - SquareLoader");
            mSquareLoaderActive = false;
            if(!cursor.moveToFirst()) {
            	if(flag)
                    refreshSquare();
                updateSpinner();
                return;
            }
            
            long l = cursor.getLong(21);
            boolean flag1;
            HostedSquareStreamFragment hostedsquarestreamfragment;
            boolean flag2;
            HostedSquareStreamFragment hostedsquarestreamfragment1;
            boolean flag3;
            HostedSquareStreamFragment hostedsquarestreamfragment2;
            if(System.currentTimeMillis() - l > 0xdbba0L)
                flag1 = flag;
            else
                flag1 = false;
            if(l <= 0L) {
            	showEmptyViewProgress(getView(), getString(R.string.loading));
            	flag = flag1;
            	if(flag)
            		refreshSquare();
            	updateSpinner();
            } else { 
            	hostedsquarestreamfragment = HostedSquareStreamFragment.this;
                if(cursor.getInt(12) != 0)
                    flag2 = flag;
                else
                    flag2 = false;
                hostedsquarestreamfragment.mCanSeePosts = Boolean.valueOf(flag2);
                hostedsquarestreamfragment1 = HostedSquareStreamFragment.this;
                if(cursor.getInt(8) != 0)
                    flag3 = flag;
                else
                    flag3 = false;
                hostedsquarestreamfragment1.mIsMember = Boolean.valueOf(flag3);
                mSquareName = cursor.getString(1);
                hostedsquarestreamfragment2 = HostedSquareStreamFragment.this;
                if(cursor.getInt(23) == 0)
                    flag = false;
                hostedsquarestreamfragment2.mAutoSubscribe = flag;
                mSquareStreamAdapter.changeStreamHeaderCursor(getStreamHeaderCursor());
                HostedSquareStreamFragment.access$900(HostedSquareStreamFragment.this, cursor);
                updateComposeBar();
                mSquareStreamAdapter.setSquareData(cursor);
                showContent(getView());
                if(!EsLog.isLoggable("HostedSquareStreamFrag", 4)) {
                	flag = flag1; 
                } else { 
                	Log.i("HostedSquareStreamFrag", (new StringBuilder("- setSquareData name=")).append(mSquareName).toString());
                    flag = flag1;
                }
                if(flag)
            		refreshSquare();
            	updateSpinner();
            }
        }

        public final void onLoaderReset(Loader loader)
        {
        }
    };
    private AudienceData mSquareMembers;
    protected String mSquareName;
    private final EsServiceListener mSquareServiceListener = new EsServiceListener() {

        public final void onEditSquareMembershipComplete$4cb07f77(int i, boolean flag, ServiceResult serviceresult)
        {
            if(EsLog.isLoggable("HostedSquareStreamFrag", 3))
                Log.d("HostedSquareStreamFrag", (new StringBuilder("onEditSquareMembershipComplete() requestId=")).append(i).toString());
            if(flag && (mOperationType == 2 || mOperationType == 3))
                AlertFragmentDialog.newInstance(null, getString(R.string.square_blocking_moderator_text), getString(R.string.ok), null).show(getFragmentManager(), null);
            handleServiceCallback(i, serviceresult);
        }

        public final void onGetSquareComplete(int i, ServiceResult serviceresult)
        {
            if(EsLog.isLoggable("HostedSquareStreamFrag", 3))
                Log.d("HostedSquareStreamFrag", (new StringBuilder("onGetSquareComplete() requestId=")).append(i).toString());
            handleGetSquareServiceCallback(i, serviceresult);
        }

        public final void onReadSquareMembersComplete$71621a40(int i, AudienceData audiencedata, ServiceResult serviceresult)
        {
            if(EsLog.isLoggable("HostedSquareStreamFrag", 3))
                Log.d("HostedSquareStreamFrag", (new StringBuilder("onGetSquareComplete() requestId=")).append(i).toString());
            if(!serviceresult.hasError() && audiencedata != null)
            {
                mSquareMembers = audiencedata;
                showSquareMembers(audiencedata);
            }
            handleServiceCallback(i, serviceresult);
        }

    };
    private SquareStreamAdapter mSquareStreamAdapter;
    protected String mStreamId;
    protected String mStreamName;
    
    public HostedSquareStreamFragment()
    {
        mOperationType = 0;
        mSquareLoaderActive = true;
        mFirstStreamListLoad = true;
    }

    private void handleServiceCallback(int i, ServiceResult serviceresult) {
    	
    	if(null == mPendingRequestId || i != mPendingRequestId.intValue())  {
    		return;
    	}
    	
    	mPendingRequestId = null;
        DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
        if(dialogfragment != null)
            dialogfragment.dismiss();
        if(serviceresult == null || !serviceresult.hasError()) {
        	switch(mOperationType)
            {
            case 2: // '\002'
                mFirstStreamListLoad = true;
                refresh();
                break;
            }
        	mOperationType = 0;
        } else { 
        	int j = R.string.operation_failed;
        	switch(mOperationType) {
        	case 1:
        		j = R.string.square_get_members_error;
        		break;
        	case 2:
        		j = R.string.square_join_error;
        		break;
        	case 3:
        		j = R.string.square_request_to_join_error;
        		break;
        	case 4:
        		j = R.string.square_cancel_join_request_error;
        		break;
        	case 5:
        		j = R.string.square_leave_error;
        		break;
        	case 6:
        		mSquareStreamAdapter.notifyDataSetChanged();
                j = R.string.square_set_notifications_error;
        		break;
        	default:
        		j = R.string.operation_failed;
        		break;
        	}
        	Toast.makeText(getSafeContext(), j, 0).show();
        }
        
    }

    private void showProgressDialog() {
    	
    	int i = R.string.loading;
    	switch(mOperationType) {
    	case 2:
    		i = R.string.square_joining;
    		break;
    	case 3:
    		i = R.string.square_sending_join_request;
    		break;
    	case 4:
    		i = R.string.square_canceling_join_request;
    		break;
    	case 5:
    		i = R.string.square_leaving;
    		break;
    	default:
    		i = R.string.loading;
    		break;
    	}
    	ProgressFragmentDialog.newInstance(null, getString(i), false).show(getFragmentManager(), "req_pending");
    }

    private void showSquareMembers(AudienceData audiencedata)
    {
        if(EsLog.isLoggable("HostedSquareStreamFrag", 3))
        {
            Log.d("HostedSquareStreamFrag", (new StringBuilder("Hidden count: ")).append(audiencedata.getHiddenUserCount()).toString());
            Log.d("HostedSquareStreamFrag", (new StringBuilder("Audience users: ")).append(audiencedata.getUserCount()).toString());
            PersonData apersondata[] = audiencedata.getUsers();
            int i = apersondata.length;
            for(int j = 0; j < i; j++)
            {
                PersonData persondata = apersondata[j];
                Log.d("HostedSquareStreamFrag", (new StringBuilder("Users: ")).append(persondata.getName()).toString());
            }

        }
        PeopleListDialogFragment peoplelistdialogfragment = new PeopleListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", mAccount);
        bundle.putParcelable("audience", audiencedata);
        bundle.putString("people_list_title", getString(R.string.square_members));
        peoplelistdialogfragment.setArguments(bundle);
        peoplelistdialogfragment.show(getFragmentManager(), "members");
    }

    private void updateComposeBar()
    {
        if(mComposeBarController != null)
            if(PrimitiveUtils.safeBoolean(mIsMember))
                mComposeBarController.forceShow();
            else
                mComposeBarController.forceHide();
    }

    private void updateSelectedStream(String s, String s1)
    {
        if(EsLog.isLoggable("HostedSquareStreamFrag", 4))
            Log.i("HostedSquareStreamFrag", "updateSelectedStream");
        mStreamId = s;
        mStreamName = s1;
        mFirstLoad = true;
        mContinuationToken = null;
        prepareLoaderUri();
        getArguments().putString("stream_id", mStreamId);
        getLoaderManager().restartLoader(2, null, this);
        mResetAnimationState = true;
        updateComposeBar();
        super.refresh();
    }

    protected final StreamAdapter createStreamAdapter(Context context, ColumnGridView columngridview, EsAccount esaccount, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamAdapter.ViewUseListener viewuselistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener, ComposeBarController composebarcontroller)
    {
        return new SquareStreamAdapter(context, columngridview, esaccount, onclicklistener, itemclicklistener, viewuselistener, streamplusbarclicklistener, streammediaclicklistener, composebarcontroller);
    }

    protected final void fetchContent(boolean flag)
    {
        if(EsLog.isLoggable("HostedSquareStreamFrag", 4))
            Log.i("HostedSquareStreamFrag", (new StringBuilder("fetchContent - newer=")).append(flag).toString());
        if(showEmptyStream() || !flag && mEndOfStream) {
        	return;
        }
        if(!flag) {
        	if(mContinuationToken == null) {
        		return;
        	}
        } else {
        	mContinuationToken = null;
        }
        
        showEmptyViewProgress(getView(), getString(R.string.loading));
        Integer integer = Integer.valueOf(EsService.getActivityStream(getActivity(), mAccount, 4, null, mSquareId, mStreamId, mContinuationToken, false));
        if(flag)
            mNewerReqId = integer;
        else
            mOlderReqId = integer;
        updateSpinner();
    }

    public final Bundle getExtrasForLogging()
    {
        return EsAnalyticsData.createExtras("extra_square_id", mSquareId);
    }

    protected final EsMatrixCursor getStreamHeaderCursor()
    {
        EsMatrixCursor esmatrixcursor = new EsMatrixCursor(new String[] {
            "_id"
        }, 2);
        Integer ainteger[] = new Integer[1];
        ainteger[0] = Integer.valueOf(0);
        esmatrixcursor.addRow(ainteger);
        if(showEmptyStream())
        {
            Integer ainteger1[] = new Integer[1];
            ainteger1[0] = Integer.valueOf(1);
            esmatrixcursor.addRow(ainteger1);
        }
        return esmatrixcursor;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.SQUARE_LANDING;
    }

    protected final void handleGetSquareServiceCallback(int i, ServiceResult serviceresult)
    {
        if(mGetSquareRequestId != null && mGetSquareRequestId.intValue() == i)
        {
            if(serviceresult.hasError())
            {
                mError = true;
                updateServerErrorView();
                if(!mSquareStreamAdapter.hasSquareData())
                    showEmptyView(getView(), getString(R.string.people_list_error));
            }
            mGetSquareRequestId = null;
            updateSpinner();
        }
    }

    protected final void initCirclesLoader()
    {
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mSquareStreamAdapter.isEmpty() && super.isEmpty())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final boolean isProgressIndicatorVisible()
    {
        boolean flag;
        if(super.isProgressIndicatorVisible() || mSquareLoaderActive || mGetSquareRequestId != null || mPendingRequestId != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isSquareStream()
    {
        return true;
    }

    public final void onBlockingHelpLinkClicked(Uri uri)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(uri);
        startExternalActivity(intent);
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            if(bundle.containsKey("square_request_id"))
                mGetSquareRequestId = Integer.valueOf(bundle.getInt("square_request_id"));
            if(bundle.containsKey("pending_request_id"))
                mPendingRequestId = Integer.valueOf(bundle.getInt("pending_request_id"));
            if(bundle.containsKey("square_expanded"))
                mSquareIsExpanded = Boolean.valueOf(bundle.getBoolean("square_expanded"));
            if(bundle.containsKey("square_members"))
                mSquareMembers = (AudienceData)bundle.getParcelable("square_members");
            if(bundle.containsKey("square_name"))
                mSquareName = bundle.getString("square_name");
            if(bundle.containsKey("square_stream_name"))
                mStreamName = bundle.getString("square_stream_name");
            mOperationType = bundle.getInt("operation_type", 0);
            mFragmentCreated = false;
        } else
        {
            mFragmentCreated = true;
        }
        getLoaderManager().initLoader(1, null, mSquareLoaderCallbacks);
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	Loader loader = null;
    	if(4 == i) {
    		loader = new StreamChangeLoader(getActivity(), mAccount, 4, null, mSquareId, mStreamId, false);
    	} else {
    		loader = super.onCreateLoader(i, bundle);
    	}
    	return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle);
        mSquareStreamAdapter = (SquareStreamAdapter)mInnerAdapter;
        mSquareStreamAdapter.setOnClickListener(this);
        mSquareStreamAdapter.setViewIsExpanded(mSquareIsExpanded);
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
        FragmentActivity fragmentactivity = getActivity();
        if("leave_square".equals(s))
        {
            mOperationType = 5;
            mPendingRequestId = Integer.valueOf(EsService.editSquareMembership(fragmentactivity, mAccount, mSquareId, "LEAVE"));
            showProgressDialog();
            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.SQUARE_LEAVE, OzViews.SQUARE_LANDING, getExtrasForLogging());
        }
    }

    public final void onExpandClicked(boolean flag)
    {
        mSquareIsExpanded = Boolean.valueOf(flag);
        mSquareStreamAdapter.setViewIsExpanded(Boolean.valueOf(flag));
    }

    public final void onJoinLeaveClicked(int i)
    {
        FragmentActivity fragmentactivity;
        OzViews ozviews;
        Bundle bundle;
        fragmentactivity = getActivity();
        ozviews = OzViews.SQUARE_LANDING;
        bundle = getExtrasForLogging();
        switch(i) {
	        case 1:
	        	mOperationType = 2;
	            EsAccount esaccount4 = mAccount;
	            String s5 = mSquareId;
	            String s6;
	            EsAccount esaccount5;
	            OzActions ozactions2;
	            if(mAutoSubscribe)
	                s6 = "JOIN_WITH_SUBSCRIPTION";
	            else
	                s6 = "JOIN";
	            mPendingRequestId = Integer.valueOf(EsService.editSquareMembership(fragmentactivity, esaccount4, s5, s6));
	            showProgressDialog();
	            esaccount5 = mAccount;
	            if(mAutoSubscribe)
	                ozactions2 = OzActions.SQUARE_JOIN_WITH_SUBSCRIPTION;
	            else
	                ozactions2 = OzActions.SQUARE_JOIN;
	            EsAnalytics.recordActionEvent(fragmentactivity, esaccount5, ozactions2, ozviews, bundle);
	        	break;
	        case 2:
	        	mOperationType = 2;
	            EsAccount esaccount2 = mAccount;
	            String s3 = mSquareId;
	            String s4;
	            EsAccount esaccount3;
	            OzActions ozactions1;
	            if(mAutoSubscribe)
	                s4 = "ACCEPT_INVITATION_WITH_SUBSCRIPTION";
	            else
	                s4 = "ACCEPT_INVITATION";
	            mPendingRequestId = Integer.valueOf(EsService.editSquareMembership(fragmentactivity, esaccount2, s3, s4));
	            showProgressDialog();
	            esaccount3 = mAccount;
	            if(mAutoSubscribe)
	                ozactions1 = OzActions.SQUARE_ACCEPT_INVITATION_WITH_SUBSCRIPTION;
	            else
	                ozactions1 = OzActions.SQUARE_ACCEPT_INVITATION;
	            EsAnalytics.recordActionEvent(fragmentactivity, esaccount3, ozactions1, ozviews, bundle);
	        	break;
	        case 3:
	        	mOperationType = 3;
	            EsAccount esaccount = mAccount;
	            String s1 = mSquareId;
	            String s2;
	            EsAccount esaccount1;
	            OzActions ozactions;
	            if(mAutoSubscribe)
	                s2 = "APPLY_TO_JOIN_WITH_SUBSCRIPTION";
	            else
	                s2 = "APPLY_TO_JOIN";
	            mPendingRequestId = Integer.valueOf(EsService.editSquareMembership(fragmentactivity, esaccount, s1, s2));
	            showProgressDialog();
	            esaccount1 = mAccount;
	            if(mAutoSubscribe)
	                ozactions = OzActions.SQUARE_APPLY_TO_JOIN_WITH_SUBSCRIPTION;
	            else
	                ozactions = OzActions.SQUARE_APPLY_TO_JOIN;
	            EsAnalytics.recordActionEvent(fragmentactivity, esaccount1, ozactions, ozviews, bundle);
	        	break;
	        case 4:
	        	String s = getString(R.string.square_confirm_leave_title);
	            int j;
	            AlertFragmentDialog alertfragmentdialog;
	            if(mSquareStreamAdapter.getVisibility() == 0)
	                j = R.string.square_confirm_leave_public;
	            else
	                j = R.string.square_confirm_leave_private;
	            alertfragmentdialog = AlertFragmentDialog.newInstance(s, getString(j), getString(R.string.square_dialog_leave_button), getString(R.string.cancel));
	            alertfragmentdialog.setTargetFragment(this, 0);
	            alertfragmentdialog.show(getFragmentManager(), "leave_square");
	        	break;
	        case 5:
	        	mOperationType = 4;
	            mPendingRequestId = Integer.valueOf(EsService.editSquareMembership(fragmentactivity, mAccount, mSquareId, "CANCEL_JOIN_REQUEST"));
	            showProgressDialog();
	            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.SQUARE_CANCEL_JOIN_REQUEST, ozviews, bundle);
	        	break;
	        default:
	        	break;
        }
    }

    public final void onLoadFinished(Loader loader, Cursor cursor)
    {
        super.onLoadFinished(loader, cursor);
        if(3 == loader.getId()) {
        	mSquareStreamAdapter.notifyDataSetChanged();
            if(mSquareStreamAdapter.hasSquareData())
                showContent(getView());
        }
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        onLoadFinished(loader, (Cursor)obj);
    }

    public final void onMembersClicked()
    {
        if(mSquareMembers == null) {
        	if(mPendingRequestId == null)
            {
                mOperationType = 1;
                mPendingRequestId = Integer.valueOf(EsService.readSquareMembers(getActivity(), mAccount, mSquareId, null));
                showProgressDialog();
            } 
        } else {
        	showSquareMembers(mSquareMembers);
        }
    }

    public final void onNotificationSwitchChanged(boolean flag)
    {
        mOperationType = 6;
        FragmentActivity fragmentactivity = getActivity();
        EsAccount esaccount = mAccount;
        String s = mSquareId;
        String s1;
        FragmentActivity fragmentactivity1;
        EsAccount esaccount1;
        OzActions ozactions;
        if(flag)
            s1 = "SUBSCRIBE";
        else
            s1 = "UNSUBSCRIBE";
        mPendingRequestId = Integer.valueOf(EsService.editSquareMembership(fragmentactivity, esaccount, s, s1));
        fragmentactivity1 = getActivity();
        esaccount1 = mAccount;
        if(flag)
            ozactions = OzActions.SQUARE_SUBSCRIBE;
        else
            ozactions = OzActions.SQUARE_UNSUBSCRIBE;
        EsAnalytics.recordActionEvent(fragmentactivity1, esaccount1, ozactions, OzViews.SQUARE_LANDING, getExtrasForLogging());
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mSquareServiceListener);
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        mPrimarySpinnerAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item);
        mPrimarySpinnerAdapter.setDropDownViewResource(0x1090009);
        mPrimarySpinnerAdapter.clear();
        hostactionbar.showPrimarySpinner(mPrimarySpinnerAdapter, 0);
        hostactionbar.showRefreshButton();
        hostactionbar.showProgressIndicator();
    }

    public final void onPrimarySpinnerSelectionChange(int i)
    {
        if(mCurrentSpinnerPosition != i)
        {
            mCurrentSpinnerPosition = i;
            SquareStreamSpinnerInfo squarestreamspinnerinfo = (SquareStreamSpinnerInfo)mPrimarySpinnerAdapter.getItem(i);
            updateSelectedStream(squarestreamspinnerinfo.getStreamId(), squarestreamspinnerinfo.getStreamName());
        }
    }

    public final void onResume()
    {
        super.onResume();
        if(!Property.ENABLE_SQUARES.getBoolean())
            getActivity().finish();
        EsService.registerListener(mSquareServiceListener);
        if(mGetSquareRequestId != null && !EsService.isRequestPending(mGetSquareRequestId.intValue()))
        {
            ServiceResult serviceresult1 = EsService.removeResult(mGetSquareRequestId.intValue());
            handleGetSquareServiceCallback(mGetSquareRequestId.intValue(), serviceresult1);
        }
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingRequestId.intValue());
            handleServiceCallback(mPendingRequestId.intValue(), serviceresult);
        }
        if(mFragmentCreated)
        {
            mFragmentCreated = false;
            refreshSquare();
        }
        updateSpinner();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mGetSquareRequestId != null)
            bundle.putInt("square_request_id", mGetSquareRequestId.intValue());
        if(mPendingRequestId != null)
            bundle.putInt("pending_request_id", mPendingRequestId.intValue());
        if(mSquareMembers != null)
            bundle.putParcelable("square_members", mSquareMembers);
        if(mSquareName != null)
            bundle.putString("square_name", mSquareName);
        if(mStreamName != null)
            bundle.putString("square_stream_name", mStreamName);
        if(mSquareIsExpanded != null)
            bundle.putBoolean("square_expanded", mSquareIsExpanded.booleanValue());
        bundle.putInt("operation_type", mOperationType);
    }

    protected final void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mSquareId = bundle.getString("square_id");
        mStreamId = bundle.getString("stream_id");
    }

    protected final void prepareLoaderUri()
    {
        mPostsUri = EsProvider.buildStreamUri(mAccount, EsPostsData.buildSquareStreamKey(mSquareId, mStreamId, false));
    }

    public final void refresh()
    {
        super.refresh();
        refreshSquare();
    }

    public final void refreshSquare()
    {
        if(EsLog.isLoggable("HostedSquareStreamFrag", 4))
            Log.i("HostedSquareStreamFrag", "refreshSquare");
        mSquareMembers = null;
        mGetSquareRequestId = Integer.valueOf(EsService.getSquare(getActivity(), mAccount, mSquareId));
        updateSpinner();
    }

    protected final boolean showEmptyStream()
    {
        boolean flag;
        if(mCanSeePosts != null && !mCanSeePosts.booleanValue())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final void startActivityForCompose(Intent intent)
    {
        boolean flag = TextUtils.isEmpty(mSquareName);
        AudienceData audiencedata = null;
        if(!flag)
        {
            String s = mSquareId;
            String s1 = mSquareName;
            String s2 = mStreamId;
            String s3;
            if(mStreamId == null)
                s3 = "";
            else
                s3 = mStreamName;
            audiencedata = new AudienceData(new SquareTargetData(s, s1, s2, s3));
        }
        intent.putExtra("audience", audiencedata);
        super.startActivityForCompose(intent);
    }

    protected final void startStreamOneUp(Intent intent)
    {
        if(mSquareStreamAdapter.isSquareAdmin())
            intent.putExtra("square_admin", true);
        intent.putExtra("refresh", true);
        super.startStreamOneUp(intent);
    }
    
    static void access$900(HostedSquareStreamFragment hostedsquarestreamfragment, Cursor cursor) {
        String s;
        String s1;
        int i1;
        int j1;
        int i = 0;
        DbSquareStream adbsquarestream[] = DbSquareStream.deserialize(cursor.getBlob(18));
        int j;
        int k;
        int l;
        boolean flag;
        String s2;
        if(adbsquarestream != null)
            j = adbsquarestream.length;
        else
            j = 0;
        if(j == 1)
        {
            String s3 = adbsquarestream[0].getStreamId();
            String s4 = adbsquarestream[0].getName();
            s1 = s3;
            k = 0;
            s = s4;
        } else
        {
            k = j;
            s = null;
            s1 = null;
        }
        l = Math.max(0, -1 + hostedsquarestreamfragment.mPrimarySpinnerAdapter.getCount());
        if(hostedsquarestreamfragment.mFirstStreamListLoad || k != l || !TextUtils.equals(s1, hostedsquarestreamfragment.mStreamId))
            flag = true;
        else
            flag = false;
        if(EsLog.isLoggable("HostedSquareStreamFrag", 3))
            Log.d("HostedSquareStreamFrag", (new StringBuilder("populatePrimarySpinner firstLoad=")).append(hostedsquarestreamfragment.mFirstStreamListLoad).append(" numStreams=").append(k).append(" old=").append(l).append(" streamId=").append(s1).append(" old=").append(hostedsquarestreamfragment.mStreamId).toString());
        hostedsquarestreamfragment.mFirstStreamListLoad = false;
        
        // TODO
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private class SquareStreamSpinnerInfo {

        public final String getStreamId()
        {
            return mStreamId;
        }

        public final String getStreamName()
        {
            return mStreamName;
        }

        public final String toString()
        {
            return mStreamName;
        }

        private final String mStreamId;
        private final String mStreamName;

        public SquareStreamSpinnerInfo(String s, String s1)
        {
            mStreamId = s1;
            mStreamName = s;
        }
    }
	
	
}
