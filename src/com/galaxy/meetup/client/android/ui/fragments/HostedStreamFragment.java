/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.LocationController;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.RecentImagesLoader;
import com.galaxy.meetup.client.android.StreamAdapter;
import com.galaxy.meetup.client.android.StreamTranslationAdapter;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.DbPlusOneData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.controller.ComposeBarController;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.activity.ProfileActivity;
import com.galaxy.meetup.client.android.ui.view.CardView;
import com.galaxy.meetup.client.android.ui.view.ClickableButton;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.ItemClickListener;
import com.galaxy.meetup.client.android.ui.view.PlusOneAnimatorView;
import com.galaxy.meetup.client.android.ui.view.StreamCardView;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.client.util.ResourceRedirector;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 * 
 */
public class HostedStreamFragment extends HostedEsFragment implements
		android.support.v4.app.LoaderManager.LoaderCallbacks, View.OnClickListener,
		ComposeBarController.ComposeBarListener, StreamAdapter.ViewUseListener,
		PlusOneAnimatorView.PlusOneAnimListener,
		StreamCardView.StreamMediaClickListener,
		StreamCardView.StreamPlusBarClickListener {

	private static final String CIRCLES_PROJECTION[] = {
        "circle_id", "circle_name", "contact_count", "volume"
    };
    private static int sNextPagePreloadTriggerRows;
    private static boolean sUsePhotoOneUp = false;
    protected StreamTranslationAdapter mAdapter;
    protected PlusOneInfo mAnimatingPlusOneInfo;
    protected String mCircleId;
    protected ComposeBarController mComposeBarController;
    protected String mContinuationToken;
    private int mCurrentSpinnerPosition;
    protected boolean mEndOfStream;
    protected boolean mError;
    protected boolean mFirstLoad;
    private boolean mFragmentCreated;
    private long mFragmentStartTime;
    protected String mGaiaId;
    protected ColumnGridView mGridView;
    protected StreamAdapter mInnerAdapter;
    private long mLastDeactivationTime;
    protected Integer mLoaderHash;
    protected Location mLocation;
    protected LocationController mLocationController;
    protected View mLocationDisabledView;
    protected Button mLocationSettingsButton;
    protected boolean mNearby;
    private boolean mOptionsMenuIsSubscribeVisible;
    private int mOptionsMenuSubscribeIcon;
    private int mOptionsMenuSubscribeText;
    protected Uri mPostsUri;
    protected boolean mPreloadRequested;
    private ArrayAdapter mPrimarySpinnerAdapter;
    private long mRecentImagesSyncTimestamp;
    private boolean mRefreshDisabled;
    protected boolean mResetAnimationState;
    protected boolean mResetPosition;
    private int mScrollOffset;
    private int mScrollPos;
    private View mServerErrorRetryButton;
    private View mServerErrorView;
    protected final EsServiceListener mServiceListener = new ServiceListener();
    private String mSetVolumeRequestCircleName;
    private Integer mSetVolumeRequestId;
    private int mSetVolumeRequestVolume;
    private long mStreamChangeLastCheckTimeMs;
    private boolean mStreamHasChanged;
    private int mStreamLength;
    protected String mStreamOwnerUserId;
    protected int mView;
    
    public HostedStreamFragment() {
        mFirstLoad = true;
        mStreamLength = -1;
    }
    
    private void addLocationListener(Location location) {
        View view;
        if(mLocationController == null)
            mLocationController = new LocationController(getActivity(), mAccount, true, 3000L, location, new StreamLocationListener());
        view = getView();
        view.findViewById(R.id.stream_location_layout).setVisibility(0);
        updateLocationHeader(view);
        if(mLocationController.isProviderEnabled()) { 
        	 mLocationController.init();
             if(mLocation == null)
                 showEmptyViewProgress(view, getString(R.string.finding_your_location)); 
        } else { 
        	removeProgressViewMessages();
            view.findViewById(0x1020004).setVisibility(8);
            view.findViewById(R.id.list_empty_text).setVisibility(8);
            view.findViewById(R.id.list_empty_progress).setVisibility(8);
            view.findViewById(R.id.stream_location_layout).setVisibility(8);
            mGridView.setVisibility(8);
            mLocationDisabledView.setVisibility(0);
            removeLocationListener();
        }
    }
    
    private static OzViews getViewForLogging(String s) {
        OzViews ozviews;
        if("v.all.circles".equals(s))
            ozviews = OzViews.LOOP_EVERYONE;
        else
        if("v.whatshot".equals(s))
            ozviews = OzViews.LOOP_WHATS_HOT;
        else
        if("v.nearby".equals(s))
            ozviews = OzViews.LOOP_NEARBY;
        else
        if(s.startsWith("f."))
            ozviews = OzViews.LOOP_CIRCLES;
        else
        if(s.startsWith("g."))
            ozviews = OzViews.LOOP_USER;
        else
            ozviews = OzViews.HOME;
        return ozviews;
    }
    
    private void handleOnSetVolumeControlCallback(ServiceResult serviceresult)
    {
        DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
        if(dialogfragment != null)
            dialogfragment.dismiss();
        mSetVolumeRequestId = null;
        if(serviceresult != null)
        {
            String s;
            if(serviceresult.hasError())
                s = getString(R.string.transient_server_error);
            else
            if(mSetVolumeRequestVolume == 2)
            {
                int j = R.string.toast_circle_unsubscribed;
                Object aobj1[] = new Object[1];
                aobj1[0] = mSetVolumeRequestCircleName;
                s = getString(j, aobj1);
            } else
            if(mSetVolumeRequestVolume == 4)
            {
                int i = R.string.toast_circle_subscribed;
                Object aobj[] = new Object[1];
                aobj[0] = mSetVolumeRequestCircleName;
                s = getString(i, aobj);
            } else
            {
                s = getString(R.string.report_set_volume_completed_toast);
            }
            Toast.makeText(getActivity(), s, 0).show();
        }
    }
    
    private void initRecentImagesLoader() {
        getLoaderManager().restartLoader(6, null, this);
    }
    
    private void onShakeAnimFinished() {
        if(!isPaused() && getActivity() != null) {
        	View view = getView();
            if(view != null)
            {
                View view1 = view.findViewById(R.id.plus_one_glass);
                if(view1 != null)
                    view1.setVisibility(8);
            }
            if(mAnimatingPlusOneInfo != null)
            {
                togglePlusOne(mAnimatingPlusOneInfo.activityId, mAnimatingPlusOneInfo.plusOneData);
                mAnimatingPlusOneInfo = null;
            }
        }
    }
    
    private void prefetchContent() {
        mPreloadRequested = true;
        if(EsLog.isLoggable("HostedStreamFrag", 4))
            Log.i("HostedStreamFrag", "prefetchContent - mPreloadRequested=true");
        if(!mGridView.post(new Runnable() {

        	public final void run() {
        		if(isPaused()) {
        			if(EsLog.isLoggable("HostedStreamFrag", 4))
        				Log.i("HostedStreamFrag", "prefetchContent - paused!"); 
        		} else { 
        			fetchContent(false);
        		}
        	}
        }) && EsLog.isLoggable("HostedStreamFrag", 4))
            Log.i("HostedStreamFrag", "prefetchContent - posting the runnable returned false!");
    }
    
    private void removeLocationListener()
    {
        if(mLocationController != null)
        {
            mLocationController.release();
            mLocationController = null;
        }
    }

    private void startStreamOneUp(StreamCardView streamcardview, boolean flag)
    {
        String s = streamcardview.getAlbumId();
        MediaRef mediaref = streamcardview.getMediaRef();
        String s1 = streamcardview.getMediaLinkUrl();
        int i = streamcardview.getDesiredWidth();
        int j = streamcardview.getDesiredHeight();
        boolean flag1 = streamcardview.isAlbum();
        String s2 = streamcardview.getLinkTitle();
        String s3 = streamcardview.getDeepLinkLabel();
        String s4 = streamcardview.getLinkUrl();
        String s5 = streamcardview.getSquareIdForOneUp();
        if(mediaref != null)
        {
            Bundle bundle = EsAnalyticsData.createExtras("extra_gaia_id", mediaref.getOwnerGaiaId());
            recordUserAction(OzActions.STREAM_SELECT_ACTIVITY, bundle);
        }
        String s6 = streamcardview.getActivityId();
        if(s6 != null)
        {
            Intent intent = Intents.getStreamOneUpActivityIntent(getActivity().getApplicationContext(), mAccount, s6);
            if(i > 0 && j > 0)
            {
                intent.putExtra("photo_width", i);
                intent.putExtra("photo_height", j);
            }
            if(mediaref != null)
            {
                intent.putExtra("photo_ref", mediaref);
                intent.putExtra("is_album", flag1);
            }
            if(!TextUtils.isEmpty(s1))
                intent.putExtra("photo_link_url", s1);
            if(!TextUtils.isEmpty(s))
                intent.putExtra("album_id", s);
            if(!TextUtils.isEmpty(s2))
                intent.putExtra("link_title", s2);
            if(!TextUtils.isEmpty(s3))
                intent.putExtra("deep_link_label", s3);
            if(!TextUtils.isEmpty(s4))
                intent.putExtra("link_url", s4);
            if(!TextUtils.isEmpty(s5))
                intent.putExtra("square_id", s5);
            intent.putExtra("show_keyboard", flag);
            startStreamOneUp(intent);
        }
    }
    
    private void togglePlusOne(String s, DbPlusOneData dbplusonedata)
    {
        if(!EsService.isPostPlusOnePending(s))
        {
            FragmentActivity fragmentactivity = getActivity();
            if(dbplusonedata != null && dbplusonedata.isPlusOnedByMe())
                EsService.deletePostPlusOne(fragmentactivity, mAccount, s);
            else
                EsService.createPostPlusOne(fragmentactivity, mAccount, s);
        }
    }

    private void updateEmptyViewProgressText()
    {
        View view = getView();
        if(view != null)
        {
            int i;
            String s;
            if(mNearby)
                i = R.string.finding_your_location;
            else
                i = R.string.loading;
            s = getString(i);
            ((TextView)view.findViewById(R.id.list_empty_progress_text)).setText(s);
        }
    }

    private void updateLocationHeader(View view)
    {
        TextView textview = (TextView)view.findViewById(R.id.stream_location_text);
        if(mLocation == null)
        {
            textview.setText(R.string.finding_your_location);
        } else
        {
            String s = LocationController.getFormattedAddress(mLocation);
            if(s != null)
                textview.setText(s);
            else
                textview.setText(R.string.unknown_address);
        }
    }

    private void updateOptionsMenuInfo(CircleSpinnerInfo circlespinnerinfo)
    {
        String s = circlespinnerinfo.getRealCircleId();
        if("v.all.circles".equals(s))
        {
            mOptionsMenuIsSubscribeVisible = true;
            mOptionsMenuSubscribeText = R.string.menu_subscribe_to_circles;
            mOptionsMenuSubscribeIcon = R.drawable.ic_menu_unmute_conversation;
        } else
        if("v.whatshot".equals(s) || "v.nearby".equals(s))
        {
            mOptionsMenuIsSubscribeVisible = false;
            mOptionsMenuSubscribeText = 0;
            mOptionsMenuSubscribeIcon = 0;
        } else
        {
            mOptionsMenuIsSubscribeVisible = true;
            if(circlespinnerinfo.getVolume() == 4)
            {
                mOptionsMenuSubscribeText = R.string.menu_unsubscribe;
                mOptionsMenuSubscribeIcon = R.drawable.ic_menu_mute_conversation;
            } else
            {
                mOptionsMenuSubscribeText = R.string.menu_subscribe;
                mOptionsMenuSubscribeIcon = R.drawable.ic_menu_unmute_conversation;
            }
        }
    }

    private void updateRefreshButton(boolean flag)
    {
        if(EsLog.isLoggable("HostedStreamFrag", 3))
            Log.d("HostedStreamFrag", (new StringBuilder("Stream has changed: ")).append(flag).toString());
        mStreamHasChanged = flag;
        HostActionBar hostactionbar = getActionBar();
        if(hostactionbar != null)
            hostactionbar.updateRefreshButtonIcon(mStreamHasChanged);
    }

    protected final void checkResetAnimationState()
    {
        if(mResetAnimationState)
        {
            if(mResetPosition)
            {
                mScrollPos = 0;
                mScrollOffset = 0;
                mGridView.setSelectionToTop();
            }
            mResetPosition = true;
            mInnerAdapter.resetAnimationState();
            mResetAnimationState = false;
        }
    }

    protected StreamAdapter createStreamAdapter(Context context, ColumnGridView columngridview, EsAccount esaccount, View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamAdapter.ViewUseListener viewuselistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener, ComposeBarController composebarcontroller)
    {
        return new StreamAdapter(context, columngridview, esaccount, onclicklistener, itemclicklistener, viewuselistener, streamplusbarclicklistener, streammediaclicklistener, composebarcontroller);
    }

    public final void doCircleSubscribe(String s, String s1)
    {
        mSetVolumeRequestId = EsService.setVolumeControl(getActivity(), mAccount, s, 4);
        mSetVolumeRequestVolume = 4;
        mSetVolumeRequestCircleName = s1;
        showProgressDialog(R.string.dialog_pending_circle_subscribe);
    }

    public final void doCircleSubscriptions(Map hashmap)
    {
        mSetVolumeRequestId = EsService.setVolumeControl(getActivity(), mAccount, hashmap);
        mSetVolumeRequestVolume = -1;
        mSetVolumeRequestCircleName = null;
        showProgressDialog(R.string.set_volume_multiple_pending);
    }

    public final void doCircleUnsubscribe(String s, String s1)
    {
        mSetVolumeRequestId = EsService.setVolumeControl(getActivity(), mAccount, s, 2);
        mSetVolumeRequestVolume = 2;
        mSetVolumeRequestCircleName = s1;
        showProgressDialog(R.string.dialog_pending_circle_unsubscribe);
    }

    protected void doShowEmptyViewProgress(View view)
    {
        super.doShowEmptyViewProgress(view);
        mLocationDisabledView.setVisibility(8);
    }
    
    protected void fetchContent(boolean flag) {
        if(mPrimarySpinnerAdapter == null || mPrimarySpinnerAdapter.getCount() != 0) {
        	if(!showEmptyStream() && (flag || !mEndOfStream) && !fetchStreamContent(flag))
                if(mNearby)
                    showEmptyViewProgress(getView(), getString(R.string.loading));
                else
                    showEmptyViewProgress(getView());
        } else { 
        	if(EsLog.isLoggable("HostedStreamFrag", 4))
                Log.i("HostedStreamFrag", (new StringBuilder("fetchContent: No circles... reloading: ")).append(isEmpty()).toString());
            showEmptyViewProgress(getView(), getString(R.string.loading));
            updateSpinner();
            getLoaderManager().restartLoader(1, null, this);
        }
    }
    
    protected final boolean fetchStreamContent(boolean flag) {
    	boolean flag1 = false;
        if(!mNearby) {
        	if(!flag) {
        		if(mContinuationToken != null) {
        			Integer integer = Integer.valueOf(EsService.getActivityStream(getActivity(), mAccount, mView, mCircleId, mGaiaId, null, mContinuationToken, false));
        			if(flag)
        	            mNewerReqId = integer;
        	        else
        	            mOlderReqId = integer;
        	        updateSpinner();
        	        flag1 = true;
        		} else { 
        			flag1 = false;
        		}
        	} else { 
        		mContinuationToken = null;
        		Integer integer = Integer.valueOf(EsService.getActivityStream(getActivity(), mAccount, mView, mCircleId, mGaiaId, null, mContinuationToken, false));
        		if(flag)
                    mNewerReqId = integer;
                else
                    mOlderReqId = integer;
                updateSpinner();
                flag1 = true;
        	}
        } else { 
        	Location location;
            location = mLocation;
            flag1 = false;
            if(location != null) {
            	if(!flag) {
            		if(mContinuationToken != null) {
            			Integer integer = Integer.valueOf(EsService.getNearbyActivities(getActivity(), mAccount, mView, new DbLocation(0, mLocation), mContinuationToken));
            			if(flag)
            	            mNewerReqId = integer;
            	        else
            	            mOlderReqId = integer;
            	        updateSpinner();
            	        flag1 = true;
            		} else { 
            			flag1 = false;
            		}
            	} else { 
            		mContinuationToken = null;
            		Integer integer = Integer.valueOf(EsService.getNearbyActivities(getActivity(), mAccount, mView, new DbLocation(0, mLocation), mContinuationToken));
            		if(flag)
                        mNewerReqId = integer;
                    else
                        mOlderReqId = integer;
                    updateSpinner();
                    flag1 = true;
            	}
            }
        }
        return flag1;
    }
    
    public OzViews getViewForLogging()
    {
        return OzViews.HOME;
    }

    protected void initCirclesLoader()
    {
        getLoaderManager().initLoader(1, null, this);
    }

    protected boolean isAdapterEmpty()
    {
        return mAdapter.isEmpty();
    }

    protected boolean isEmpty()
    {
        boolean flag;
        if(isAdapterEmpty() || mFirstLoad)
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected boolean isLocalDataAvailable(Cursor cursor)
    {
        boolean flag;
        if(cursor != null && cursor.getCount() > 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public boolean isSquareStream()
    {
        return false;
    }

    protected boolean needsAsyncData()
    {
        return true;
    }
    
    public void onActivityResult(int i, int j, Intent intent) {
    	
    	if(1 == i) {
    		if(j == -1 && intent != null)
            {
    			List arraylist = intent.getParcelableArrayListExtra("mediarefs");
                Intent intent1 = Intents.getPostActivityIntent(getActivity(), null, (ArrayList)arraylist);
                if(intent.hasExtra("insert_photo_request_id"))
                    intent1.putExtra("insert_photo_request_id", intent.getIntExtra("insert_photo_request_id", 0));
                intent1.removeExtra("account");
                startActivityForCompose(intent1);
            }
    	}
    }

    protected final void onAsyncData()
    {
        super.onAsyncData();
        FragmentActivity fragmentactivity = getActivity();
        if(mFragmentStartTime > 0L && (fragmentactivity instanceof ProfileActivity))
            mFragmentStartTime = 0L;
    }

    public void onClick(View view) {
    	if(view == mLocationSettingsButton) {
    		startActivity(Intents.getLocationSettingActivityIntent());
    		return;
    	}
    	if(view == mServerErrorRetryButton)
        {
            mError = false;
            if(EsLog.isLoggable("HostedStreamFrag", 4))
                Log.i("HostedStreamFrag", "onClick - mError=false");
            if(isAdapterEmpty())
            {
                refresh();
            } else
            {
                prefetchContent();
                updateServerErrorView();
            }
        } else
        if(view instanceof StreamCardView)
        {
            StreamCardView streamcardview = (StreamCardView)view;
            String s = streamcardview.getEventId();
            String s1 = streamcardview.getEventOwnerId();
            if(!TextUtils.isEmpty(s) && !TextUtils.isEmpty(s1))
            {
                startActivity(Intents.getHostedEventIntent(getActivity(), mAccount, s, s1, null));
            } else
            {
                String s2 = streamcardview.getSquareId();
                if(!TextUtils.isEmpty(s2) && !isSquareStream())
                    startActivity(Intents.getSquareStreamActivityIntent(getActivity(), mAccount, s2, null, null));
                else
                    startStreamOneUp(streamcardview, false);
            }
        } else
        {
            int i = view.getId();
            FragmentActivity fragmentactivity = getActivity();
            if(i == R.id.compose_post)
                startActivityForCompose(Intents.getPostTextActivityIntent(fragmentactivity, mAccount));
            else
            if(i == R.id.compose_photos)
            {
                recordUserAction(OzActions.COMPOSE_CHOOSE_PHOTO);
                startActivityForResult(Intents.newPhotosActivityIntentBuilder(getActivity()).setAccount(mAccount).setAlbumType("camera_photos").setPhotoPickerMode(Integer.valueOf(2)).setPhotoPickerTitleResourceId(Integer.valueOf(R.string.photo_picker_album_label_share)).setTakePhoto(true).setTakeVideo(true).build(), 1);
            } else
            if(i == R.id.compose_location)
            {
                recordUserAction(OzActions.LOOP_CHECKIN);
                startActivityForCompose(Intents.getCheckinActivityIntent(fragmentactivity, mAccount));
            } else
            if(i == R.id.compose_custom)
            {
                ResourceRedirector.getInstance();
                if(Property.ENABLE_EMOTISHARE.getBoolean())
                {
                    recordUserAction(OzActions.EMOTISHARE_INSERT_CLICKED);
                    startActivityForCompose(Intents.getEmotiShareActivityIntent(fragmentactivity, mAccount, null));
                }
            }
        }
    }

    public final void onCommentsClicked(StreamCardView streamcardview)
    {
        startStreamOneUp(streamcardview, true);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mScrollPos = bundle.getInt("scroll_pos");
            mScrollOffset = bundle.getInt("scroll_off");
            mLocation = (Location)bundle.getParcelable("location");
            if(bundle.containsKey("loader_hash"))
                mLoaderHash = Integer.valueOf(bundle.getInt("loader_hash"));
            mStreamLength = bundle.getInt("stream_length");
            mLastDeactivationTime = bundle.getLong("last_deactivation");
            mError = bundle.getBoolean("error");
            mResetAnimationState = bundle.getBoolean("reset_animation", false);
            mResetPosition = mResetAnimationState;
            mStreamChangeLastCheckTimeMs = bundle.getLong("stream_change");
            mStreamHasChanged = bundle.getBoolean("stream_change_flag");
            mRefreshDisabled = true;
            mFragmentCreated = false;
            if(bundle.containsKey("set_volume_id"))
            {
                mSetVolumeRequestId = Integer.valueOf(bundle.getInt("set_volume_id"));
                mSetVolumeRequestVolume = bundle.getInt("set_volume_value");
                mSetVolumeRequestCircleName = bundle.getString("set_volume_circle");
            }
            mOptionsMenuIsSubscribeVisible = bundle.getBoolean("subscribe_visible", false);
            mOptionsMenuSubscribeText = bundle.getInt("subscribe_text");
            mOptionsMenuSubscribeIcon = bundle.getInt("subscribe_icon");
        } else
        {
            mScrollPos = 0;
            mScrollOffset = 0;
            mFragmentStartTime = System.currentTimeMillis();
            mFragmentCreated = true;
        }
        prepareLoaderUri();
        initCirclesLoader();
        initRecentImagesLoader();
    }
    
    public Loader onCreateLoader(int i, Bundle bundle) {
    	Loader loader = null;
    	switch(i) {
	    	case 1:
	    		loader = new CircleListLoader(getActivity(), mAccount, 3, CIRCLES_PROJECTION);
	    		break;
	    	case 2:
	    	case 3:
	    		if(showEmptyStream())
	                mStreamLength = 0;
	            int j;
	            String s;
	            boolean flag;
	            String as[];
	            FragmentActivity fragmentactivity;
	            Uri uri;
	            String s1;
	            Uri uri1;
	            if(i == 2)
	                j = 1;
	            else
	                j = mStreamLength;
	            s = "sort_index ASC";
	            if(j != -1)
	                s = (new StringBuilder()).append(s).append(" LIMIT ").append(j).toString();
	            if(i != 2 && mGaiaId == null)
	                flag = true;
	            else
	                flag = false;
	            if(i == 2)
	                as = ContinuationTokenQuery.PROJECTION;
	            else
	                as = StreamAdapter.StreamQuery.PROJECTION_STREAM;
	            fragmentactivity = getActivity();
	            uri = mPostsUri;
	            if(flag)
	                s1 = "has_muted=0";
	            else
	                s1 = null;
	            if(i == 3)
	                uri1 = EsProvider.EVENTS_ALL_URI;
	            else
	                uri1 = null;
	            loader = new EsCursorLoader(fragmentactivity, uri, as, s1, null, s, uri1);
	    		break;
	    	case 4:
	    		loader = new StreamChangeLoader(getActivity(), mAccount, mView, mCircleId, mGaiaId, null, false);
	    		break;
	    	case 5:
	    		loader = new NearbyStreamChangeLoader(getActivity(), mAccount, new DbLocation(0, mLocation));
	    		break;
	    	case 6:
	    		loader = new RecentImagesLoader(getActivity(), mAccount);
	    		break;
	    	default:
	    		loader = null;
	    		break;
    	}
    	return loader;
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.stream, viewgroup, false);
        View view1;
        View view2;
        int i;
        boolean flag;
        if(sNextPagePreloadTriggerRows == 0)
            if(ScreenMetrics.getInstance(view.getContext()).screenDisplayType == 0)
                sNextPagePreloadTriggerRows = 6;
            else
                sNextPagePreloadTriggerRows = 8;
        mGridView = (ColumnGridView)view.findViewById(R.id.grid);
        view1 = view.findViewById(R.id.floating_compose_bar);
        view1.findViewById(R.id.compose_post).setOnClickListener(this);
        view1.findViewById(R.id.compose_photos).setOnClickListener(this);
        view1.findViewById(R.id.compose_location).setOnClickListener(this);
        view2 = view1.findViewById(R.id.compose_custom);
        view2.setOnClickListener(this);
        ResourceRedirector.getInstance();
        if(Property.ENABLE_EMOTISHARE.getBoolean())
            i = 0;
        else
            i = 8;
        view2.setVisibility(i);
        if(getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        mComposeBarController = new ComposeBarController(view1, flag, this);
        mInnerAdapter = createStreamAdapter(getActivity(), mGridView, mAccount, this, new PostClickListener(), this, this, this, mComposeBarController);
        mAdapter = new StreamTranslationAdapter(mInnerAdapter);
        mGridView.setAdapter(mAdapter);
        mGridView.setSelector(R.drawable.list_selected_holo);
        setupEmptyView(view, R.string.no_posts);
        mLocationDisabledView = view.findViewById(R.id.location_off);
        mLocationSettingsButton = (Button)view.findViewById(R.id.location_off_settings);
        mLocationSettingsButton.setOnClickListener(this);
        mServerErrorView = view.findViewById(R.id.transient_server_error);
        mServerErrorRetryButton = view.findViewById(R.id.error_retry_button);
        mServerErrorRetryButton.setOnClickListener(this);
        if(showEmptyStream())
            showEmptyView(view, getString(R.string.no_posts));
        if(bundle == null)
            mRecentImagesSyncTimestamp = 0L;
        else
            mRecentImagesSyncTimestamp = bundle.getLong("recent_images_sync_timestamp");
        updateServerErrorView();
        return view;
    }

    public final void onDestroyView()
    {
        super.onDestroyView();
        if(mGridView != null)
        {
            mGridView.setOnScrollListener(null);
            mGridView = null;
        }
    }

    public final void onDismissRecentImages(boolean flag)
    {
        FragmentActivity fragmentactivity = getActivity();
        if(fragmentactivity != null)
        {
            EsAccountsData.saveRecentImagesTimestamp(fragmentactivity, mRecentImagesSyncTimestamp);
            initRecentImagesLoader();
            if(flag)
                EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.STREAM_DISMISS_INSTANT_UPLOAD_PHOTOS, getViewForLogging());
        }
    }
    
    public void onLoadFinished(Loader loader, Cursor cursor) {
        // TODO
    }
    
    public void onLoadFinished(Loader loader, Object obj)
    {
        onLoadFinished(loader, (Cursor)obj);
    }

    public void onLoaderReset(Loader loader)
    {
    }

    public final void onMediaClicked(String s, String s1, MediaRef mediaref, boolean flag, StreamCardView streamcardview)
    {
        FragmentActivity fragmentactivity = getActivity();
        if(flag)
        {
            String s2 = mediaref.getLocalUri().toString();
            Intents.viewContent(fragmentactivity, mAccount, s2);
        } else
        {
            startStreamOneUp(streamcardview, false);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == R.id.subscribe)
        {
            CircleSpinnerInfo circlespinnerinfo = (CircleSpinnerInfo)mPrimarySpinnerAdapter.getItem(mCurrentSpinnerPosition);
            if(circlespinnerinfo.getRealCircleId().equals("v.all.circles"))
            {
                ArrayList arraylist = new ArrayList();
                int i = mPrimarySpinnerAdapter.getCount();
                for(int j = 0; j < i; j++)
                {
                    CircleSpinnerInfo circlespinnerinfo1 = (CircleSpinnerInfo)mPrimarySpinnerAdapter.getItem(j);
                    String s = circlespinnerinfo1.getRealCircleId();
                    if(!"v.all.circles".equals(s) && !"15".equals(s) && !"1c".equals(s) && !"v.nearby".equals(s) && !"v.whatshot".equals(s))
                        arraylist.add(new CircleSubscriptionsDialog.CircleInfo(circlespinnerinfo1.getCircleId(), circlespinnerinfo1.getCircleName(), circlespinnerinfo1.getMemberCount(), circlespinnerinfo1.getVolume()));
                }

                getActivity();
                CircleSubscriptionsDialog circlesubscriptionsdialog = CircleSubscriptionsDialog.newInstance$51fb5134(arraylist);
                circlesubscriptionsdialog.setTargetFragment(this, 0);
                circlesubscriptionsdialog.show(getFragmentManager(), "circle_subscriptions");
            } else
            {
                int k;
                CircleSubscribeDialog circlesubscribedialog;
                Bundle bundle;
                if(circlespinnerinfo.getVolume() == 4)
                    k = 1;
                else
                    k = 2;
                circlesubscribedialog = new CircleSubscribeDialog();
                bundle = new Bundle();
                bundle.putInt("do_subscribe", k);
                bundle.putString("circle_id", circlespinnerinfo.getCircleId());
                bundle.putString("circle_name", circlespinnerinfo.getCircleName());
                circlesubscribedialog.setArguments(bundle);
                circlesubscribedialog.setTargetFragment(this, 0);
                circlesubscribedialog.show(getFragmentManager(), "circle_subscribe");
            }
            flag = true;
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public void onPause()
    {
        super.onPause();
        mInnerAdapter.onPause();
        mGridView.onPause();
        EsService.unregisterListener(mServiceListener);
        removeLocationListener();
    }

    public final void onPlusOneAnimFinished() {
        if(isPaused() || getActivity() == null || mAnimatingPlusOneInfo == null || mGridView == null) {
        	return;
        }
        
        boolean flag = false;
        int j = mGridView.getChildCount();
        for(int i = 0; i < j; i++) {
        	View view = mGridView.getChildAt(i);
            if((view instanceof StreamCardView)) {
            	StreamCardView streamcardview = (StreamCardView)view;
                if(TextUtils.equals(streamcardview.getActivityId(), mAnimatingPlusOneInfo.activityId)) {
                	streamcardview.overridePlusOnedButtonDisplay(true, mAnimatingPlusOneInfo.overrideCount);
                    flag = true;
                }
            }
        }
        if(!flag)
            onShakeAnimFinished();
    }

    public final void onPlusOneClicked(String s, DbPlusOneData dbplusonedata, StreamCardView streamcardview)
    {
        if(mAnimatingPlusOneInfo == null)
            if(android.os.Build.VERSION.SDK_INT < 12 || dbplusonedata != null && dbplusonedata.isPlusOnedByMe())
            {
                togglePlusOne(s, dbplusonedata);
                streamcardview.overridePlusOnedButtonDisplay(false, 0);
            } else
            {
                View view = getView();
                int i;
                PlusOneAnimatorView plusoneanimatorview;
                Pair pair;
                if(dbplusonedata == null)
                    i = 1;
                else
                    i = 1 + dbplusonedata.getCount();
                mAnimatingPlusOneInfo = new PlusOneInfo(s, dbplusonedata, i);
                plusoneanimatorview = (PlusOneAnimatorView)view.findViewById(R.id.plus_one_animator);
                pair = streamcardview.getPlusOneButtonAnimationCopies();
                plusoneanimatorview.startPlusOneAnim(this, (ClickableButton)pair.first, (ClickableButton)pair.second);
                streamcardview.startDelayedShakeAnimation();
                view.findViewById(R.id.plus_one_glass).setVisibility(0);
                getView().postDelayed(new Runnable() {

                    public final void run()
                    {
                        onShakeAnimFinished();
                    }
                }, 915L);
            }
    }

    protected void onPrepareActionBar(HostActionBar hostactionbar)
    {
        if(mPrimarySpinnerAdapter == null)
        {
            mPrimarySpinnerAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item);
            mPrimarySpinnerAdapter.setDropDownViewResource(0x1090009);
        }
        hostactionbar.showPrimarySpinner(mPrimarySpinnerAdapter, mCurrentSpinnerPosition);
        hostactionbar.showRefreshButton();
        hostactionbar.updateRefreshButtonIcon(mStreamHasChanged);
        updateSpinner();
    }

    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem menuitem = menu.findItem(R.id.subscribe);
        menuitem.setVisible(mOptionsMenuIsSubscribeVisible);
        if(mOptionsMenuIsSubscribeVisible)
        {
            menuitem.setTitle(mOptionsMenuSubscribeText);
            menuitem.setIcon(mOptionsMenuSubscribeIcon);
        }
    }

    public void onPrimarySpinnerSelectionChange(int i)
    {
        if(mCurrentSpinnerPosition != i)
        {
            CircleSpinnerInfo circlespinnerinfo = (CircleSpinnerInfo)mPrimarySpinnerAdapter.getItem(i);
            boolean flag;
            View view;
            android.content.SharedPreferences.Editor editor;
            if(mAccount != null)
            {
                OzViews ozviews;
                OzViews ozviews1;
                if(mCurrentSpinnerPosition >= 0)
                    ozviews = getViewForLogging(((CircleSpinnerInfo)mPrimarySpinnerAdapter.getItem(mCurrentSpinnerPosition)).getRealCircleId());
                else
                    ozviews = OzViews.HOME;
                ozviews1 = getViewForLogging(circlespinnerinfo.getRealCircleId());
                clearNavigationAction();
                recordNavigationAction(ozviews, ozviews1, null, null, null);
            }
            mCurrentSpinnerPosition = i;
            mCircleId = circlespinnerinfo.getCircleId();
            mView = circlespinnerinfo.getView();
            if(mView == 2)
                flag = true;
            else
                flag = false;
            mNearby = flag;
            updateEmptyViewProgressText();
            mFirstLoad = true;
            mContinuationToken = null;
            view = getView();
            if(mNearby)
            {
                addLocationListener(null);
                view.findViewById(R.id.stream_location_layout).setVisibility(0);
                updateLocationHeader(view);
                if(mLocation == null)
                    showEmptyViewProgress(view, getString(R.string.finding_your_location));
            } else
            {
                removeLocationListener();
                mLocation = null;
                view.findViewById(R.id.stream_location_layout).setVisibility(8);
            }
            prepareLoaderUri();
            getArguments().putString("circle_id", mCircleId);
            getArguments().putInt("view", mView);
            getLoaderManager().restartLoader(2, null, this);
            editor = getActivity().getSharedPreferences("streams", 0).edit();
            editor.putString("circle", ((CircleSpinnerInfo)mPrimarySpinnerAdapter.getItem(mCurrentSpinnerPosition)).getRealCircleId());
            if(android.os.Build.VERSION.SDK_INT >= 9)
                editor.apply();
            else
                editor.commit();
            mResetAnimationState = true;
            updateOptionsMenuInfo(circlespinnerinfo);
            if(mComposeBarController != null)
                mComposeBarController.forceShow();
            refresh();
        }
    }

    public final void onReshareClicked(String s, boolean flag)
    {
        Intent intent = Intents.getReshareActivityIntent(getActivity(), mAccount, s, flag);
        if(flag)
            ConfirmIntentDialog.newInstance(getString(R.string.reshare_dialog_title), getString(R.string.reshare_dialog_message), getString(R.string.reshare_dialog_positive_button), intent).show(getFragmentManager(), "confirm_reshare");
        else
            startActivity(intent);
    }

    public void onResume()
    {
        boolean flag;
        if(mNewerReqId != null)
            flag = true;
        else
            flag = false;
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mGridView != null)
        {
            int i = 0;
            for(int j = mGridView.getChildCount(); i < j; i++)
                if(mGridView.getChildAt(i) instanceof CardView)
                    CardView.onStart();

            mGridView.onResume();
        }
        if(mNearby)
            addLocationListener(null);
        if(flag && mNewerReqId == null)
            updateRefreshButton(false);
        updateSpinner();
        if(EsLog.isLoggable("HostedStreamFrag", 3))
            Log.d("HostedStreamFrag", (new StringBuilder("onResume mFragmentCreated: ")).append(mFragmentCreated).append(", mNewerReqId: ").append(mNewerReqId).append(", gaia id: ").append(mGaiaId).append(", time diff (ms): ").append(System.currentTimeMillis() - mStreamChangeLastCheckTimeMs).toString());
        if(!mFragmentCreated && mNewerReqId == null && mGaiaId == null && System.currentTimeMillis() - mStreamChangeLastCheckTimeMs > 30000L)
            if(mNearby)
            {
                if(mLocation != null)
                    getLoaderManager().restartLoader(5, null, this);
            } else
            {
                getLoaderManager().restartLoader(4, null, this);
            }
        if(mSetVolumeRequestId != null && !EsService.isRequestPending(mSetVolumeRequestId.intValue()))
        {
            handleOnSetVolumeControlCallback(EsService.removeResult(mSetVolumeRequestId.intValue()));
            mSetVolumeRequestId = null;
        }
        mFragmentCreated = false;
    }

    protected final void onResumeContentFetched(View view)
    {
        updateSpinner();
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(!getActivity().isFinishing() && mGridView != null)
        {
            saveScrollPosition();
            bundle.putInt("scroll_pos", mScrollPos);
            bundle.putInt("scroll_off", mScrollOffset);
        }
        if(mLocation != null)
            bundle.putParcelable("location", mLocation);
        if(mLoaderHash != null)
            bundle.putInt("loader_hash", mLoaderHash.intValue());
        bundle.putInt("stream_length", mStreamLength);
        bundle.putLong("last_deactivation", mLastDeactivationTime);
        bundle.putBoolean("error", mError);
        bundle.putBoolean("reset_animation", mResetAnimationState);
        bundle.putLong("stream_change", mStreamChangeLastCheckTimeMs);
        bundle.putBoolean("stream_change_flag", mStreamHasChanged);
        bundle.putLong("recent_images_sync_timestamp", mRecentImagesSyncTimestamp);
        bundle.putBoolean("subscribe_visible", mOptionsMenuIsSubscribeVisible);
        bundle.putInt("subscribe_text", mOptionsMenuSubscribeText);
        bundle.putInt("subscribe_icon", mOptionsMenuSubscribeIcon);
        if(mSetVolumeRequestId != null)
        {
            bundle.putInt("set_volume_id", mSetVolumeRequestId.intValue());
            bundle.putInt("set_volume_value", mSetVolumeRequestVolume);
            bundle.putString("set_volume_circle", mSetVolumeRequestCircleName);
        }
    }

    protected void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mGaiaId = bundle.getString("gaia_id");
        int i;
        boolean flag;
        if(mGaiaId == null)
            mStreamOwnerUserId = mAccount.getGaiaId();
        else
            mStreamOwnerUserId = mGaiaId;
        mCircleId = bundle.getString("circle_id");
        if(bundle.containsKey("view"))
            mView = bundle.getInt("view");
        else
            mView = 0;
        i = mView;
        flag = false;
        if(i == 2)
            flag = true;
        mNearby = flag;
        updateEmptyViewProgressText();
    }

    public final void onShareRecentImages(ArrayList arraylist)
    {
        FragmentActivity fragmentactivity = getActivity();
        if(fragmentactivity != null)
        {
            startActivityForCompose(Intents.getPostActivityIntent(fragmentactivity, EsAccountsData.getActiveAccount(fragmentactivity), arraylist));
            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.STREAM_SHARE_INSTANT_UPLOAD_PHOTOS, getViewForLogging());
        }
    }

    public final void onStop()
    {
        super.onStop();
        int i = 0;
        for(int j = mGridView.getChildCount(); i < j; i++)
            if(mGridView.getChildAt(i) instanceof CardView)
                CardView.onStop();

        mGridView.invalidateViews();
    }

    public final void onViewUsed(int i)
    {
        if(!mPreloadRequested && !mEndOfStream && !mError && mGridView != null && i >= mInnerAdapter.getCount() - sNextPagePreloadTriggerRows)
            prefetchContent();
    }

    protected void prepareLoaderUri()
    {
        if(mNearby && mLocation == null)
        {
            mPostsUri = EsProvider.buildStreamUri(mAccount, "no_location_stream_key");
        } else
        {
            Location location = mLocation;
            DbLocation dblocation = null;
            if(location != null)
                dblocation = new DbLocation(0, mLocation);
            mPostsUri = EsProvider.buildStreamUri(mAccount, EsPostsData.buildActivitiesStreamKey(mGaiaId, mCircleId, dblocation, false, mView));
        }
    }

    public void refresh()
    {
        if(mRefreshDisabled)
        {
            mRefreshDisabled = false;
        } else
        {
            super.refresh();
            if(mNearby)
            {
                Location location = mLocation;
                mLocation = null;
                addLocationListener(location);
                updateSpinner();
            } else
            {
                fetchContent(true);
            }
        }
    }

    protected final void restoreScrollPosition()
    {
        if(mGridView != null && (mScrollOffset != 0 || mScrollPos != 0))
        {
            mGridView.setSelectionFromTop(mScrollPos, mScrollOffset);
            mScrollPos = 0;
            mScrollOffset = 0;
        }
    }

    protected final void saveScrollPosition()
    {
        if(mGridView != null)
        {
            mScrollPos = mGridView.getFirstVisiblePosition();
            if(mAdapter != null)
            {
                View view = mGridView.getChildAt(0);
                if(view != null)
                    mScrollOffset = view.getTop();
                else
                    mScrollOffset = 0;
            } else
            {
                mScrollOffset = 0;
            }
        }
    }

    protected final void showContent(View view)
    {
        super.showContent(view);
        if(mNearby)
        {
            view.findViewById(R.id.stream_location_layout).setVisibility(0);
            updateLocationHeader(view);
        }
        mGridView.setVisibility(0);
        mLocationDisabledView.setVisibility(8);
    }

    protected boolean showEmptyStream()
    {
        return getArguments().getBoolean("show_empty_stream", false);
    }

    protected final void showEmptyView(View view, String s)
    {
        super.showEmptyView(view, s);
        if(mNearby)
        {
            view.findViewById(R.id.stream_location_layout).setVisibility(0);
            updateLocationHeader(view);
        }
        mLocationDisabledView.setVisibility(8);
    }

    protected final void showEmptyViewProgress(View view)
    {
        super.showEmptyViewProgress(view);
        mLocationDisabledView.setVisibility(8);
    }

    protected final void showEmptyViewProgress(View view, String s)
    {
        super.showEmptyViewProgress(view, s);
        mLocationDisabledView.setVisibility(8);
    }

    protected void showProgressDialog(int i)
    {
        ProgressFragmentDialog.newInstance(null, getString(i), false).show(getFragmentManager(), "req_pending");
    }

    protected void startActivityForCompose(Intent intent)
    {
        startActivity(intent);
    }

    protected void startStreamOneUp(Intent intent)
    {
        startActivity(intent);
    }

    protected final void updateServerErrorView()
    {
        View view = mServerErrorView;
        int i;
        if(mError)
            i = 0;
        else
            i = 8;
        view.setVisibility(i);
    }
    
	private static final class CircleSpinnerInfo {

		private final String mCircleId;
		private final String mCircleName;
		private int mMemberCount;
		private final String mRealCircleId;
		private final int mView;
		private int mVolume;

		public CircleSpinnerInfo(Context context, String s, String s1, int i,
				int j) {
			mRealCircleId = s1;
			mMemberCount = i;
			mVolume = j;
			if (s1.equals("v.all.circles")) {
				mView = 0;
				mCircleName = context.getString(R.string.stream_circles);
				mCircleId = null;
			} else if (s1.equals("v.whatshot")) {
				mView = 1;
				mCircleName = context.getString(R.string.stream_whats_hot);
				mCircleId = null;
			} else if (s1.equals("v.nearby")) {
				mView = 2;
				mCircleName = context.getString(R.string.stream_nearby);
				mCircleId = null;
			} else {
				mView = 3;
				mCircleName = s;
				mCircleId = s1;
			}
		}

		public final String getCircleId() {
			return mCircleId;
		}

		public final String getCircleName() {
			return mCircleName;
		}

		public final int getMemberCount() {
			return mMemberCount;
		}

		public final String getRealCircleId() {
			return mRealCircleId;
		}

		public final int getView() {
			return mView;
		}

		public final int getVolume() {
			return mVolume;
		}

		public final int setVolume(int i) {
			mVolume = i;
			return i;
		}

		public final String toString() {
			return mCircleName;
		}

	}

	private static interface ContinuationTokenQuery {

		public static final String PROJECTION[] = { "token" };

	}

	private static final class PlusOneInfo {

		public String activityId;
		public int overrideCount;
		public DbPlusOneData plusOneData;

		public PlusOneInfo(String s, DbPlusOneData dbplusonedata, int i) {
			activityId = s;
			plusOneData = dbplusonedata;
			overrideCount = i;
		}
	}
	
	protected final class PostClickListener implements ItemClickListener {

	    public final void onSpanClick(URLSpan urlspan) {
	    }

	    public final void onUserImageClick(String s, String s1) {
	    	if(getArguments().getBoolean("view_as_plus_page", false)) {
	    		return;
	    	}
	    	String s2 = mGaiaId;
	        boolean flag = false;
	        if(s2 == null)
	            flag = true;
	        if(!TextUtils.equals(mStreamOwnerUserId, s) || flag) {
	            Bundle bundle = EsAnalyticsData.createExtras("extra_gaia_id", mStreamOwnerUserId);
	            OzActions ozactions = OzActions.STREAM_SELECT_AUTHOR;
	            EsAnalytics.recordActionEvent(getActivity(), mAccount, ozactions, getViewForLogging(), bundle);
	            startActivity(Intents.getProfileActivityByGaiaIdIntent(getActivity(), mAccount, s, null));
	        }
	    }
	}
	
	protected final class ServiceListener extends EsServiceListener
    {

        public final void onCreatePostPlusOne(ServiceResult serviceresult)
        {
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.plusone_error, 0).show();
        }

        public final void onDeletePostPlusOne(ServiceResult serviceresult)
        {
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.delete_plusone_error, 0).show();
        }

        public final void onGetActivities(int i, boolean flag, int j, ServiceResult serviceresult)
        {
            HostedStreamFragment hostedstreamfragment = HostedStreamFragment.this;
            boolean flag1;
            if(serviceresult != null && serviceresult.hasError())
                flag1 = true;
            else
                flag1 = false;
            hostedstreamfragment.mError = flag1;
            if(EsLog.isLoggable("HostedStreamFrag", 4))
                Log.i("HostedStreamFrag", (new StringBuilder("onGetActivities - mError=")).append(mError).toString());
            if(!flag) {
            	if(mOlderReqId == null || !mOlderReqId.equals(Integer.valueOf(i))) {
            		return;
            	}
            	mOlderReqId = null;
                if(mError)
                {
                    mPreloadRequested = false;
                    if(EsLog.isLoggable("HostedStreamFrag", 4))
                        Log.i("HostedStreamFrag", "onGetActivities - mPreloadRequested=false");
                }
            } else { 
            	if(!(mNewerReqId != null && mNewerReqId.equals(Integer.valueOf(i)))) {
            		return;
            	}
            	mNewerReqId = null;
                if(!mError)
                {
                    mStreamChangeLastCheckTimeMs = System.currentTimeMillis();
                    updateRefreshButton(false);
                }
            }
            
            if(mStreamLength != -1 || mError)
            {
                if(!mError)
                    mStreamLength = j;
                getLoaderManager().restartLoader(2, null, HostedStreamFragment.this);
            }
            updateSpinner();
            updateServerErrorView();
        }

        public final void onSetVolumeControlsRequestComplete(int i, ServiceResult serviceresult)
        {
            if(mSetVolumeRequestId != null && mSetVolumeRequestId.intValue() == i)
                handleOnSetVolumeControlCallback(serviceresult);
        }
    }

    private final class StreamLocationListener implements LocationListener {

        public final void onLocationChanged(Location location)
        {
            if(mLocation == null)
            {
                mLocation = location;
                prepareLoaderUri();
                mFirstLoad = true;
                getLoaderManager().restartLoader(2, null, HostedStreamFragment.this);
                updateLocationHeader(getView());
                fetchContent(true);
            }
        }

        public final void onProviderDisabled(String s)
        {
        }

        public final void onProviderEnabled(String s)
        {
        }

        public final void onStatusChanged(String s, int i, Bundle bundle)
        {
        }
    }
}
