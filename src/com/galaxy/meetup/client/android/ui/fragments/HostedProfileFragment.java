/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import WriteReviewOperation.MediaRef;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.ProfileStreamAdapter;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.StreamAdapter;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.controller.ComposeBarController;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.activity.ProfileActivity;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.ItemClickListener;
import com.galaxy.meetup.client.android.ui.view.ProfileAboutView;
import com.galaxy.meetup.client.android.ui.view.StreamCardView;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.HelpUrl;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.MapUtils;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 *
 */
public class HostedProfileFragment extends HostedStreamFragment implements
		LoaderCallbacks, BlockFragment.Listener, BlockPersonDialog.PersonBlocker, ChoosePhotoDialog.PhotoHandler, ProfileAboutView.OnClickListener {

	private ProfileMergeCursor mActiveProfileCursor;
    private boolean mBlockInProgress;
    private int mChoosePhotoTarget;
    private final DataSetObserver mCircleContentObserver = new DataSetObserver() {

        public final void onChanged()
        {
            mProfileAdapter.updateCircleList();
        }
    };
    
    private CircleNameResolver mCircleNameResolver;
    private Context mContext;
    private boolean mControlPrimarySpinner;
    private int mCurrentSpinnerPosition;
    private final Handler mHandler = new Handler();
    private boolean mHasGaiaId;
    private Integer mInsertCameraPhotoRequestId;
    private boolean mIsBlocked;
    private boolean mIsMute;
    private boolean mIsMyProfile;
    private boolean mIsPlusPage;
    private boolean mLandscape;
    private Integer mMuteRequestId;
    private boolean mMuteRequestIsMuted;
    private String mPersonId;
    private Integer mPlusOneRequestId;
    private ProfileStreamAdapter mProfileAdapter;
    private final android.support.v4.app.LoaderManager.LoaderCallbacks mProfileAndContactDataLoader = new android.support.v4.app.LoaderManager.LoaderCallbacks() {

        public final Loader onCreateLoader(int i, Bundle bundle)
        {
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", "Loader<ProfileAndContactData> onCreateLoader()");
            return new ProfileLoader(getActivity(), mAccount, bundle.getString("person_id"), true);
        }

        public final void onLoadFinished(Loader loader, Object obj)
        {
        	EsPeopleData.ProfileAndContactData profileandcontactdata = (EsPeopleData.ProfileAndContactData)obj;
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", "Loader<ProfileAndContactData> onLoadFinished()");
            mProfileAndContactLoaderActive = false;
            if((profileandcontactdata.profileState == 2 || profileandcontactdata.profileState == 5 || profileandcontactdata.profileState == 1) && (!mHasGaiaId || profileandcontactdata.profile != null)) {
            	mError = false;
                mProfileAdapter.setProfileData(profileandcontactdata);
                mIsPlusPage = mProfileAdapter.isPlusPage();
                mIsBlocked = mProfileAdapter.isBlocked();
                mIsMute = mProfileAdapter.isMuted();
                updateSpinner();
                invalidateActionBar();
                onAsyncData();
                if(HostedProfileFragment.access$1200(HostedProfileFragment.this, profileandcontactdata))
                    refreshProfile();
                if(mActiveProfileCursor != null && mActiveProfileCursor.mProfileCursor != null)
                    mActiveProfileCursor.mProfileCursor.requery();
            } else { 
            	 mError = true;
                 ProfileStreamAdapter profilestreamadapter = mProfileAdapter;
                 HostedProfileFragment hostedprofilefragment = HostedProfileFragment.this;
                 int i;
                 if(profileandcontactdata.profileState == 0)
                     i = R.string.profile_load_error;
                 else
                     i = R.string.profile_does_not_exist;
                 profilestreamadapter.showError(hostedprofilefragment.getString(i));
                 updateSpinner();
                 invalidateActionBar();
            }
        }

        public final void onLoaderReset(Loader loader)
        {
        }
    };
    
    private boolean mProfileAndContactLoaderActive;
    private boolean mProfileIsExpanded;
    private Integer mProfilePendingRequestId;
    private final EsServiceListener mProfileServiceListener = new EsServiceListener() {

        public final void onCreateProfilePlusOneRequestComplete(int i, ServiceResult serviceresult)
        {
            handlePlusOneCallback(i, serviceresult);
        }

        public final void onDeleteProfilePlusOneRequestComplete(int i, ServiceResult serviceresult)
        {
            handlePlusOneCallback(i, serviceresult);
        }

        public final void onGetProfileAndContactComplete(int i, ServiceResult serviceresult)
        {
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", (new StringBuilder("onGetProfileAndContactComplete(); requestId=")).append(i).toString());
            handleProfileServiceCallback(i, serviceresult);
        }

        public final void onInsertCameraPhotoComplete(int i, ServiceResult serviceresult)
        {
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", (new StringBuilder("onInsertCameraPhotoComplete(); requestId=")).append(i).toString());
            handlerInsertCameraPhoto(i);
        }

        public final void onReportAbuseRequestComplete(int i, ServiceResult serviceresult)
        {
            handleReportAbuseCallback(i, serviceresult);
        }

        public final void onSetCircleMembershipComplete(int i, ServiceResult serviceresult)
        {
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", (new StringBuilder("onSetCircleMembershipComplete(); requestId=")).append(i).toString());
            handleProfileServiceCallback(i, serviceresult);
        }

        public final void onSetCoverPhotoComplete$6a63df5(int i, ServiceResult serviceresult)
        {
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", (new StringBuilder("onSetCoverPhotoComplete(); requestId=")).append(i).toString());
            handleCoverPhotoCallback(i, serviceresult);
        }

        public final void onSetMutedRequestComplete(int i, boolean flag, ServiceResult serviceresult)
        {
            handleSetMutedCallback(i, flag, serviceresult);
        }

        public final void onSetScrapbookInfoComplete(int i, ServiceResult serviceresult)
        {
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", (new StringBuilder("onSetCoverPhotoComplete(); requestId=")).append(i).toString());
            handleCoverPhotoCallback(i, serviceresult);
        }

        public final void onUploadCoverPhotoComplete(int i, ServiceResult serviceresult)
        {
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", (new StringBuilder("onUploadCoverPhotoComplete(); requestId=")).append(i).toString());
            handleCoverPhotoCallback(i, serviceresult);
        }

        public final void onUploadProfilePhotoComplete(int i, ServiceResult serviceresult)
        {
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", (new StringBuilder("onUploadProfilePhotoComplete(); requestId=")).append(i).toString());
            handleProfileServiceCallback(i, serviceresult);
            if(serviceresult != null && !serviceresult.hasError() && serviceresult.getException() == null)
            {
                mProfilePendingRequestId = EsService.getProfileAndContact(getActivity(), mAccount, mPersonId, true);
                updateSpinner();
            }
        }
    };
    
    private Integer mReportAbuseRequestId;
    private Integer mSetCoverPhotoRequestId;
    
	public HostedProfileFragment()
    {
        mControlPrimarySpinner = true;
        mCurrentSpinnerPosition = -1;
        mProfileAndContactLoaderActive = true;
    }

    private boolean canShowConversationActions()
    {
        boolean flag;
        if(!mProfileAndContactLoaderActive && !mIsMyProfile && !mIsPlusPage && !mAccount.isPlusPage() && !mIsBlocked && !mBlockInProgress && !mError)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private boolean canShowRefreshInActionBar()
    {
        boolean flag;
        if(ScreenMetrics.getInstance(mContext).screenDisplayType != 0 || mLandscape)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void handleCoverPhotoCallback(int i, ServiceResult serviceresult)
    {
        if(mSetCoverPhotoRequestId != null && mSetCoverPhotoRequestId.intValue() == i)
        {
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            if(serviceresult == null || serviceresult.hasError() || serviceresult.getException() != null)
            {
                Toast.makeText(mContext, R.string.transient_server_error, 0).show();
            } else
            {
                mProfilePendingRequestId = EsService.getProfileAndContact(getActivity(), mAccount, mPersonId, true);
                updateSpinner();
            }
            mSetCoverPhotoRequestId = null;
            updateSpinner();
        }
    }
    
    private void handlerInsertCameraPhoto(int i)
    {
        byte byte0 = 2;
        if(null == mInsertCameraPhotoRequestId || mInsertCameraPhotoRequestId.intValue() != i) {
        	return;
        }
        
        FragmentActivity fragmentactivity;
        String s = EsService.getLastCameraMediaLocation();
        fragmentactivity = getActivity();
        if(s == null)
        	Toast.makeText(getActivity(), getString(R.string.camera_photo_error), 1).show();
        else {
        	MediaRef mediaref;
            byte byte1;
            mediaref = new MediaRef(null, 0L, null, Uri.parse(s), MediaRef.MediaType.IMAGE);
            switch(mChoosePhotoTarget)
            {
            default:
                byte1 = byte0;
                byte0 = 1;
                break;

            case 2: // '\002'
            	byte1 = 5;
            	break;
            }
            startActivityForResult(Intents.getPhotoPickerIntent(fragmentactivity, mAccount, fragmentactivity.getString(R.string.change_photo_crop_title), mediaref, byte0), byte1);
        }
        
        if(fragmentactivity instanceof ImageUtils.InsertCameraPhotoDialogDisplayer)
            ((ImageUtils.InsertCameraPhotoDialogDisplayer)fragmentactivity).hideInsertCameraPhotoDialog();
        mInsertCameraPhotoRequestId = null;
        updateSpinner();
    }

    private boolean isDialogVisible(String s)
    {
        boolean flag;
        if((DialogFragment)getFragmentManager().findFragmentByTag(s) != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void launchEditActivity(int i, String s, String s1)
    {
        startActivityForResult(Intents.getProfileEditActivityIntent(getActivity(), getAccount(), i, s, s1), 7);
        EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.PROFILE_EDIT_START, getViewForLogging());
    }

    private void safeStartActivity(Intent intent)
    {
    	try {
    		startActivity(intent);
    	} catch (ActivityNotFoundException activitynotfoundexception) {
    		if(EsLog.isLoggable("HostedProfileFragment", 6))
                Log.e("HostedProfileFragment", (new StringBuilder("Cannot launch activity: ")).append(intent).toString(), activitynotfoundexception);
    	}
    }

    private void setPersonBlocked(boolean flag)
    {
        BlockFragment blockfragment = BlockFragment.getInstance(mContext, mAccount, mPersonId, mProfileAdapter.getFullName(), mProfileAdapter.isPlusPage(), flag);
        blockfragment.setTargetFragment(this, 0);
        blockfragment.show(getActivity());
        mBlockInProgress = true;
        mProfileAdapter.beginBlockInProgress();
    }

    private void showChooseCoverPhotoDialog()
    {
        if(!isDialogVisible("change_photo"))
        {
            ChoosePhotoDialog choosephotodialog = new ChoosePhotoDialog(R.string.change_cover_photo_dialog_title);
            choosephotodialog.setIsCameraSupported(Intents.isCameraIntentRegistered(mContext));
            boolean flag;
            Long long1;
            if(mProfileAdapter.getScrapbookAlbumId() != null)
                flag = true;
            else
                flag = false;
            if(mProfileAdapter.hasCoverPhotoUpgrade())
                long1 = mProfileAdapter.getScrapbookCoverPhotoId();
            else
                long1 = null;
            choosephotodialog.setIsForCoverPhoto(true, flag, long1);
            choosephotodialog.setTargetFragment(this, 0);
            choosephotodialog.show(getFragmentManager(), "change_photo");
        }
    }

    private void updateCoverPhoto(String s, int i)
    {
        String s1 = mProfileAdapter.getScrapbookLayout();
        mSetCoverPhotoRequestId = Integer.valueOf(EsService.setScrapbookInfo(getActivity(), mAccount, s, i, s1));
        showProgressDialog(R.string.setting_cover_photo);
    }

    public final void blockPerson(Serializable serializable)
    {
        setPersonBlocked(true);
    }

    protected final StreamAdapter createStreamAdapter(Context context, ColumnGridView columngridview, EsAccount esaccount, android.view.View.OnClickListener onclicklistener, final ItemClickListener originalListener, StreamAdapter.ViewUseListener viewuselistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener, ComposeBarController composebarcontroller)
    {
        return new ProfileStreamAdapter(context, columngridview, esaccount, onclicklistener, new ItemClickListener() {

            public final void onSpanClick(URLSpan urlspan)
            {
                originalListener.onSpanClick(urlspan);
            }

            public final void onUserImageClick(String s, String s1)
            {
            }
        }, viewuselistener, streamplusbarclicklistener, streammediaclicklistener, null);
    }

    public final void doPickPhotoFromAlbums(int i)
    {
        if(1 == mChoosePhotoTarget) {
        	Intents.PhotosIntentBuilder photosintentbuilder3 = Intents.newAlbumsActivityIntentBuilder(getActivity());
            photosintentbuilder3.setAccount(mAccount).setPersonId(mAccount.getPersonId()).setPhotosHome(Boolean.valueOf(true)).setShowCameraAlbum(Boolean.valueOf(true)).setPhotoPickerMode(Integer.valueOf(1)).setPhotoPickerTitleResourceId(Integer.valueOf(R.string.photo_picker_album_label_profile)).setCropMode(Integer.valueOf(1));
            startActivityForResult(photosintentbuilder3.build(), 3);
            return;
        }
        
        if(mChoosePhotoTarget != 2) {
        	return;
        }
        
        switch(i)
        {
        case 0: // '\0'
            Intents.PhotosIntentBuilder photosintentbuilder2 = Intents.newAlbumsActivityIntentBuilder(getActivity());
            photosintentbuilder2.setAccount(mAccount).setPersonId(mAccount.getPersonId()).setPhotosHome(Boolean.valueOf(true)).setShowCameraAlbum(Boolean.valueOf(true)).setPhotoPickerMode(Integer.valueOf(1)).setPhotoPickerTitleResourceId(Integer.valueOf(R.string.photo_picker_album_label_cover_photo)).setCropMode(Integer.valueOf(2));
            startActivityForResult(photosintentbuilder2.build(), 5);
            break;

        case 1: // '\001'
            Intents.PhotosIntentBuilder photosintentbuilder1 = Intents.newPhotosActivityIntentBuilder(getActivity());
            photosintentbuilder1.setAccount(mAccount).setGaiaId("115239603441691718952").setAlbumId("5745127577944303633").setPhotoPickerMode(Integer.valueOf(1)).setPhotoPickerTitleResourceId(Integer.valueOf(R.string.photo_picker_album_label_cover_photo)).setCropMode(Integer.valueOf(2));
            startActivityForResult(photosintentbuilder1.build(), 5);
            break;

        case 2: // '\002'
            Intents.PhotosIntentBuilder photosintentbuilder = Intents.newPhotosActivityIntentBuilder(getActivity());
            photosintentbuilder.setAccount(mAccount).setGaiaId(mAccount.getGaiaId()).setPersonId(mAccount.getPersonId()).setAlbumId(mProfileAdapter.getScrapbookAlbumId()).setPhotoPickerMode(Integer.valueOf(1)).setPhotoPickerTitleResourceId(Integer.valueOf(R.string.photo_picker_album_label_cover_photo)).setCropMode(Integer.valueOf(2));
            startActivityForResult(photosintentbuilder.build(), 5);
            break;
        }
    }

    public final void doRepositionCoverPhoto()
    {
        MediaRef mediaref = new MediaRef(mProfileAdapter.getScrapbookCoverPhotoOwnerId(), mProfileAdapter.getScrapbookCoverPhotoId().longValue(), mProfileAdapter.getScrapbookCoverPhotoUrl(), null, MediaRef.MediaType.IMAGE);
        Intent intent = Intents.getPhotoPickerIntent(getActivity(), mAccount, null, mediaref, 2);
        intent.putExtra("top_offset", mProfileAdapter.getScrapbookCoverPhotoOffset());
        startActivityForResult(intent, 6);
    }

    protected final void doShowEmptyView(View view, String s)
    {
    }

    protected final void doShowEmptyViewProgress(View view)
    {
    }

    public final void doTakePhoto()
    {
    	int i = 1;
    	
        if(mChoosePhotoTarget != 1) 
        	i = 4; 
        else 
        	i = 1;
        
        try {
        	getActivity();
        	startActivityForResult(Intents.getCameraIntentPhoto("camera-profile.jpg"), i);
        } catch (ActivityNotFoundException activitynotfoundexception) {
        	Toast.makeText(getActivity(), R.string.change_photo_no_camera, 1).show();
        }
    }

    public final Bundle getExtrasForLogging()
    {
        Bundle bundle;
        if(!TextUtils.isEmpty(mGaiaId))
            bundle = EsAnalyticsData.createExtras("extra_gaia_id", mGaiaId);
        else
            bundle = null;
        return bundle;
    }

    public final OzViews getViewForLogging()
    {
        OzViews ozviews;
        if(mProfileAdapter != null && mProfileAdapter.getViewIsExpanded())
            ozviews = OzViews.PROFILE;
        else
            ozviews = OzViews.LOOP_USER;
        return ozviews;
    }

    protected final void handlePlusOneCallback(int i, ServiceResult serviceresult)
    {
        if(mPlusOneRequestId != null && mPlusOneRequestId.intValue() == i) {
        	 mPlusOneRequestId = null;
             updateSpinner();
             if(serviceresult != null && serviceresult.hasError())
                 Toast.makeText(mContext, R.string.transient_server_error, 0).show();
        }
    }

    protected final void handleProfileServiceCallback(int i, ServiceResult serviceresult)
    {
        if(mProfilePendingRequestId != null && mProfilePendingRequestId.intValue() == i)
        {
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            if(serviceresult == null || serviceresult.hasError() || serviceresult.getException() != null)
                Toast.makeText(mContext, R.string.transient_server_error, 0).show();
            mProfilePendingRequestId = null;
            updateSpinner();
        }
    }

    protected final void handleReportAbuseCallback(int i, ServiceResult serviceresult)
    {
        if(mReportAbuseRequestId != null && mReportAbuseRequestId.intValue() == i)
        {
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            mReportAbuseRequestId = null;
            updateSpinner();
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(mContext, R.string.transient_server_error, 0).show();
            else
                Toast.makeText(mContext, R.string.report_abuse_completed_toast, 0).show();
        }
    }

    protected final void handleSetMutedCallback(int i, boolean flag, ServiceResult serviceresult)
    {
        if(mMuteRequestId != null && mMuteRequestId.intValue() == i)
        {
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            mMuteRequestId = null;
            updateSpinner();
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(mContext, R.string.transient_server_error, 0).show();
            else
            if(flag)
                Toast.makeText(mContext, R.string.report_mute_completed_toast, 0).show();
            else
                Toast.makeText(mContext, R.string.report_unmute_completed_toast, 0).show();
        }
    }

    protected final void initCirclesLoader()
    {
    }

    protected final boolean isAdapterEmpty()
    {
        boolean flag = true;
        if(mAdapter.getCount() != 1)
            flag = false;
        return flag;
    }

    protected final boolean isLocalDataAvailable(Cursor cursor)
    {
        boolean flag = true;
        if(cursor == null || cursor.getCount() <= 1)
            flag = false;
        return flag;
    }

    protected final boolean isProgressIndicatorVisible()
    {
        boolean flag;
        if(super.isProgressIndicatorVisible() || mProfileAndContactLoaderActive || mProfilePendingRequestId != null || mPlusOneRequestId != null || mReportAbuseRequestId != null || mInsertCameraPhotoRequestId != null || mMuteRequestId != null || mSetCoverPhotoRequestId != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final boolean needsAsyncData()
    {
        return true;
    }

    public final void onActionButtonClicked(int i)
    {
        Intent intent = null;
        
        if(0 == i) {
        	String s3 = mProfileAdapter.getFullName();
            String s4 = null;
            String s5;
            AudienceData audiencedata1;
            if(mPersonId.startsWith("e:"))
            {
                s5 = mPersonId.substring(2);
            } else
            {
                boolean flag1 = mHasGaiaId;
                intent = null;
                if(!flag1) {
                    return;
                }
                s4 = mGaiaId;
                s5 = null;
            }
            audiencedata1 = new AudienceData(new PersonData(s4, s3, s5));
            intent = Intents.getNewConversationActivityIntent(mContext, getAccount(), audiencedata1);
        } else if(1 == i) {
        	 String s;
             String s1;
             String s2;
             s = mProfileAdapter.getFullName();
             s1 = null;
             if(mPersonId.startsWith("e:")) {
            	 s2 = mPersonId.substring(2);
             } else {
            	 boolean flag;
                 flag = mHasGaiaId;
                 intent = null;
                 if(flag) {
                	 s1 = mGaiaId;
                     s2 = null;
                 } else {
                     return;
                 }
             }
             AudienceData audiencedata = new AudienceData(new PersonData(s1, s, s2));
             intent = Intents.getNewHangoutActivityIntent(mContext, getAccount(), true, audiencedata);
        }
        
        if(intent != null)
            safeStartActivity(intent);
        return;
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
    	if(-1 != j) {
    		if(i == 7)
                EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.PROFILE_EDIT_CANCEL, getViewForLogging());
    		return;
    	}
    	
        switch(i)
        {
        case 0: // '\0'
            final ArrayList originalCircleIds = intent.getExtras().getStringArrayList("original_circle_ids");
            final ArrayList selectedCircleIds = intent.getExtras().getStringArrayList("selected_circle_ids");
            mHandler.post(new Runnable() {

                public final void run()
                {
                    setCircleMembership(originalCircleIds, selectedCircleIds);
                }
            });
            break;

        case 1: // '\001'
        case 4: // '\004'
            FragmentActivity fragmentactivity = getActivity();
            if(fragmentactivity instanceof ImageUtils.InsertCameraPhotoDialogDisplayer)
                ((ImageUtils.InsertCameraPhotoDialogDisplayer)fragmentactivity).showInsertCameraPhotoDialog();
            mInsertCameraPhotoRequestId = EsService.insertCameraPhoto(fragmentactivity, mAccount, "camera-profile.jpg");
            break;

        case 2: // '\002'
        case 3: // '\003'
            if(intent != null)
            {
                final byte imageBytes[] = intent.getByteArrayExtra("data");
                if(imageBytes != null)
                    mHandler.post(new Runnable() {

                        public final void run()
                        {
                            setProfilePhoto(imageBytes);
                        }
                    });
            }
            break;

        case 5: // '\005'
            if(intent != null)
            {
                final int topOffset = intent.getIntExtra("top_offset", 0);
                final long photoId = intent.getLongExtra("photo_id", 0L);
                if(photoId != 0L)
                {
                    final boolean isGalleryPhoto = intent.getBooleanExtra("is_gallery_photo", false);
                    mHandler.post(new Runnable() {

                        public final void run()
                        {
                            setCoverPhoto(Long.toString(photoId), topOffset, isGalleryPhoto);
                        }
                    });
                    if(isGalleryPhoto)
                        EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.COVER_PHOTO_CHOOSE_GALLERY, getViewForLogging());
                    else
                        EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.COVER_PHOTO_CHOOSE_OWN_PHOTO, getViewForLogging());
                } else
                {
                    final byte imageBytes[] = intent.getByteArrayExtra("data");
                    if(imageBytes != null)
                        mHandler.post(new Runnable() {

                            public final void run()
                            {
                                setCoverPhoto(imageBytes, topOffset);
                            }
                        });
                    EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.COVER_PHOTO_CHOOSE_OWN_PHOTO, getViewForLogging());
                }
            }
            break;

        case 6: // '\006'
            int k = intent.getIntExtra("top_offset", 0);
            updateCoverPhoto(Long.toString(intent.getLongExtra("photo_id", 0L)), k);
            EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.COVER_PHOTO_REPOSITION, getViewForLogging());
            break;

        case 7: // '\007'
            mProfilePendingRequestId = EsService.getProfileAndContact(getActivity(), mAccount, mPersonId, true);
            updateSpinner();
            EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.PROFILE_EDIT_SAVE, getViewForLogging());
            break;
        }
    }

    public final void onAddressClicked(String s)
    {
        if(!TextUtils.isEmpty(s))
        {
            Uri uri = Uri.parse((new StringBuilder("geo:0,0?q=")).append(Uri.encode(s)).toString());
            MapUtils.launchMapsActivity(getActivity(), uri);
        }
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mContext = activity;
        boolean flag;
        if(activity.getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        mLandscape = flag;
    }

    public final void onAvatarClicked()
    {
        if(mIsMyProfile)
        {
            mChoosePhotoTarget = 1;
            if(!isDialogVisible("change_photo"))
            {
                ChoosePhotoDialog choosephotodialog = new ChoosePhotoDialog(R.string.change_photo_dialog_title);
                choosephotodialog.setIsCameraSupported(Intents.isCameraIntentRegistered(mContext));
                choosephotodialog.setTargetFragment(this, 0);
                choosephotodialog.show(getFragmentManager(), "change_photo");
            }
        } else
        {
            startActivity(Intents.newPhotosActivityIntentBuilder(mContext).setAccount(mAccount).setGaiaId(EsPeopleData.extractGaiaId(mPersonId)).setAlbumName(getString(R.string.profile_photos_stream_title)).setStreamId("profile").build());
        }
    }

    public final void onBlockCompleted(boolean flag)
    {
        mBlockInProgress = false;
        mProfileAdapter.endBlockInProgress(flag);
        invalidateActionBar();
    }

    public final void onCirclesButtonClicked()
    {
        startActivityForResult(Intents.getCircleMembershipActivityIntent(getActivity(), mAccount, mPersonId, null, true), 0);
    }

    public final void onCoverPhotoClicked(int i)
    {
        if(mIsMyProfile)
        {
            mChoosePhotoTarget = 2;
            if(mProfileAdapter.hasCoverPhotoUpgrade())
            {
                EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.COVER_PHOTO_CHANGE, getViewForLogging());
                showChooseCoverPhotoDialog();
            } else
            {
                EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.COVER_PHOTO_UPGRADE_START, getViewForLogging());
                AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.upgrade_to_cover_photo_dialog_title), getString(R.string.upgrade_to_cover_photo_dialog_content), getString(R.string.upgrade_to_cover_photo_dialog_confirm), getString(R.string.cancel));
                alertfragmentdialog.setListener(new AlertFragmentDialog.AlertDialogListener() {

                    public final void onDialogCanceled(String s)
                    {
                    }

                    public final void onDialogListClick(int j, Bundle bundle)
                    {
                    }

                    public final void onDialogNegativeClick(String s)
                    {
                    }

                    public final void onDialogPositiveClick(Bundle bundle, String s)
                    {
                        showChooseCoverPhotoDialog();
                    }
                });
                alertfragmentdialog.show(getFragmentManager(), "cover_photo_upgrade");
            }
        } else
        {
            startActivity(Intents.newPhotoViewActivityIntentBuilder(mContext).setAccount(mAccount).setGaiaId(mGaiaId).setAlbumName(getString(R.string.profile_cover_photos_stream_title)).setAlbumId(mProfileAdapter.getScrapbookAlbumId()).setPhotoId(mProfileAdapter.getScrapbookPhotoId(i)).build());
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            if(bundle.containsKey("profile_request_id"))
                mProfilePendingRequestId = Integer.valueOf(bundle.getInt("profile_request_id"));
            if(bundle.containsKey("plusone_request_id"))
                mPlusOneRequestId = Integer.valueOf(bundle.getInt("plusone_request_id"));
            if(bundle.containsKey("abuse_request_id"))
                mReportAbuseRequestId = Integer.valueOf(bundle.getInt("abuse_request_id"));
            if(bundle.containsKey("mute_request_id"))
            {
                mMuteRequestId = Integer.valueOf(bundle.getInt("mute_request_id"));
                mMuteRequestIsMuted = bundle.getBoolean("mute_state");
            }
            if(bundle.containsKey("camera_request_id"))
                mInsertCameraPhotoRequestId = Integer.valueOf(bundle.getInt("camera_request_id"));
            if(bundle.containsKey("cover_photo_request_id"))
                mSetCoverPhotoRequestId = Integer.valueOf(bundle.getInt("cover_photo_request_id"));
            if(bundle.containsKey("block_in_progress"))
                mBlockInProgress = bundle.getBoolean("block_in_progress");
            if(bundle.containsKey("profile_is_expanded"))
                mProfileIsExpanded = bundle.getBoolean("profile_is_expanded");
            if(bundle.containsKey("choose_photo_target"))
                mChoosePhotoTarget = bundle.getInt("choose_photo_target");
        }
        Bundle bundle1 = new Bundle();
        bundle1.putString("person_id", mPersonId);
        getLoaderManager().initLoader(100, bundle1, mProfileAndContactDataLoader);
        mCircleNameResolver = new CircleNameResolver(mContext, getLoaderManager(), mAccount);
        mCircleNameResolver.registerObserver(mCircleContentObserver);
        mCircleNameResolver.initLoader();
        getLoaderManager().initLoader(2, null, this);
        fetchStreamContent(true);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        if(Log.isLoggable("HostedProfileFragment", 3))
        {
            StringBuilder stringbuilder = new StringBuilder("Loader<Cursor> onCreateLoader() -- ");
            Object obj;
            if(i == 3)
                obj = "POSTS_LOADER_ID";
            else
                obj = Integer.valueOf(i);
            Log.d("HostedProfileFragment", stringbuilder.append(obj).toString());
        }
        return super.onCreateLoader(i, bundle);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle);
        mProfileAdapter = (ProfileStreamAdapter)mInnerAdapter;
        ProfileStreamAdapter profilestreamadapter = mProfileAdapter;
        String s = mPersonId;
        boolean flag = mIsMyProfile;
        boolean flag1 = mHasGaiaId;
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.fromParts("sms", "", null));
        boolean flag2;
        if(getActivity().getPackageManager().queryIntentActivities(intent, 0x10000).size() > 0)
            flag2 = true;
        else
            flag2 = false;
        profilestreamadapter.init(s, flag, flag1, flag2, mCircleNameResolver);
        mProfileAdapter.setOnClickListener(this);
        if(mBlockInProgress)
            mProfileAdapter.beginBlockInProgress();
        mProfileAdapter.setViewIsExpanded(mProfileIsExpanded);
        return view;
    }

    public final void onEditEducationClicked()
    {
        launchEditActivity(2, mProfileAdapter.getEducationList(), mProfileAdapter.getSharingRosterData());
    }

    public final void onEditEmploymentClicked()
    {
        launchEditActivity(1, mProfileAdapter.getEmploymentList(), mProfileAdapter.getSharingRosterData());
    }

    public final void onEditPlacesLivedClicked()
    {
        launchEditActivity(3, mProfileAdapter.getPlacesLivedList(), mProfileAdapter.getSharingRosterData());
    }

    public final void onEmailClicked(String s)
    {
        Rfc822Token arfc822token[] = null;
        if(s != null)
            arfc822token = Rfc822Tokenizer.tokenize(s);
        if(arfc822token != null && arfc822token.length != 0)
        {
            Rfc822Token rfc822token = arfc822token[0];
            if(TextUtils.isEmpty(rfc822token.getName()) && !TextUtils.isEmpty(mProfileAdapter.getFullName()))
                rfc822token.setName(mProfileAdapter.getFullName());
            safeStartActivity(new Intent("android.intent.action.SENDTO", Uri.parse((new StringBuilder("mailto:")).append(Uri.encode(rfc822token.toString())).toString())));
        }
    }

    public final void onExpandClicked(boolean flag)
    {
        mProfileIsExpanded = flag;
        mProfileAdapter.setViewIsExpanded(flag);
        OzViews ozviews;
        OzViews ozviews1;
        Bundle bundle;
        if(flag)
        {
            ozviews = OzViews.LOOP_USER;
            ozviews1 = OzViews.PROFILE;
        } else
        {
            ozviews = OzViews.PROFILE;
            ozviews1 = OzViews.LOOP_USER;
        }
        bundle = getExtrasForLogging();
        EsAnalytics.recordNavigationEvent(getActivity(), getAccount(), ozviews, ozviews1, null, null, bundle, bundle);
    }

    public final void onLinkClicked(String s)
    {
        if(!TextUtils.isEmpty(s))
        {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(s));
            intent.addFlags(0x80000);
            safeStartActivity(intent);
        }
    }

    public final void onLoadFinished(Loader loader, Cursor cursor)
    {
        if(3 == loader.getId()) {
        	if(mActiveProfileCursor == null || !mActiveProfileCursor.wrapsStreamCursor(cursor))
            {
                EsMatrixCursor esmatrixcursor = new EsMatrixCursor(cursor.getColumnNames(), 1);
                Object aobj[] = new Object[StreamAdapter.StreamQuery.PROJECTION_STREAM.length];
                aobj[15] = Long.valueOf(512L);
                esmatrixcursor.addRow(aobj);
                mActiveProfileCursor = new ProfileMergeCursor(new Cursor[] {
                    esmatrixcursor, cursor
                });
            }
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", (new StringBuilder("Loader<Cursor> onLoadFinished() -- POSTS_LOADER_ID, ")).append(mActiveProfileCursor.getCount()).append(" rows").toString());
            super.onLoadFinished(loader, mActiveProfileCursor);
            if(mActiveProfileCursor.getCount() > 0)
            {
                mProfileAdapter.notifyDataSetChanged();
                showContent(getView());
            }
        } else {
        	if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", (new StringBuilder("Loader<Cursor> onLoadFinished() -- ")).append(loader.getId()).toString());
            super.onLoadFinished(loader, cursor);
        }
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        onLoadFinished(loader, (Cursor)obj);
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onLocalCallClicked(String s)
    {
        startExternalActivity(new Intent("android.intent.action.DIAL", Uri.parse((new StringBuilder("tel:")).append(Uri.encode(s)).toString())));
    }

    public final void onLocalDirectionsClicked(String s)
    {
        if(!TextUtils.isEmpty(s))
            MapUtils.launchMapsActivity(getActivity(), Uri.parse(s));
    }

    public final void onLocalMapClicked(String s)
    {
        if(!TextUtils.isEmpty(s))
            MapUtils.launchMapsActivity(getActivity(), Uri.parse(s));
    }

    public final void onLocalReviewClicked(int i, int j)
    {
        safeStartActivity(Intents.getLocalReviewActivityIntent(getActivity(), mAccount, mPersonId, i, j));
    }

    public final void onLocationClicked(String s)
    {
        if(!TextUtils.isEmpty(s))
        {
            Uri uri = Uri.parse((new StringBuilder("geo:0,0?q=")).append(Uri.encode(s)).toString());
            MapUtils.launchMapsActivity(getActivity(), uri);
        }
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(i == R.id.refresh)
            refresh();
        else
        if(i == R.id.mute)
        {
            String s2;
            MuteProfileDialog muteprofiledialog1;
            Bundle bundle1;
            if(mIsPlusPage)
                s2 = mProfileAdapter.getFullName();
            else
                s2 = mProfileAdapter.getGivenName();
            muteprofiledialog1 = new MuteProfileDialog();
            bundle1 = new Bundle();
            bundle1.putString("name", s2);
            bundle1.putString("gender", mProfileAdapter.getGender());
            bundle1.putBoolean("target_mute", flag);
            muteprofiledialog1.setArguments(bundle1);
            muteprofiledialog1.setTargetFragment(this, 0);
            muteprofiledialog1.show(getFragmentManager(), "mute_profile");
        } else
        if(i == R.id.unmute)
        {
            String s1;
            MuteProfileDialog muteprofiledialog;
            Bundle bundle;
            if(mIsPlusPage)
                s1 = mProfileAdapter.getFullName();
            else
                s1 = mProfileAdapter.getGivenName();
            muteprofiledialog = new MuteProfileDialog();
            bundle = new Bundle();
            bundle.putString("name", s1);
            bundle.putString("gender", mProfileAdapter.getGender());
            bundle.putBoolean("target_mute", false);
            muteprofiledialog.setArguments(bundle);
            muteprofiledialog.setTargetFragment(this, 0);
            muteprofiledialog.show(getFragmentManager(), "unmute_profile");
        } else
        if(i == R.id.block)
        {
            BlockPersonDialog blockpersondialog = new BlockPersonDialog(mProfileAdapter.isPlusPage());
            blockpersondialog.setTargetFragment(this, 0);
            blockpersondialog.show(getFragmentManager(), "block_person");
        } else
        if(i == R.id.unblock)
        {
            UnblockPersonDialog unblockpersondialog = new UnblockPersonDialog(mPersonId, mProfileAdapter.isPlusPage());
            unblockpersondialog.setTargetFragment(this, 0);
            unblockpersondialog.show(getFragmentManager(), "unblock_person");
        } else
        if(i == R.id.report_abuse)
        {
            ReportAbuseDialog reportabusedialog = new ReportAbuseDialog();
            reportabusedialog.setTargetFragment(this, 0);
            reportabusedialog.show(getFragmentManager(), "report_abuse");
        } else
        if(i == R.id.help)
        {
            String s = getResources().getString(R.string.url_param_help_profile);
            startExternalActivity(new Intent("android.intent.action.VIEW", HelpUrl.getHelpUrl(getActivity(), s)));
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public final void onPause()
    {
        super.onPause();
        mInnerAdapter.onPause();
        mGridView.onPause();
        EsService.unregisterListener(mProfileServiceListener);
    }

    public final void onPhoneNumberClicked(String s)
    {
        if(!TextUtils.isEmpty(s))
            safeStartActivity(new Intent("android.intent.action.DIAL", Uri.parse((new StringBuilder("tel:")).append(Uri.encode(s)).toString())));
    }

    public final void onPlusOneClicked()
    {
        if(!mProfileAdapter.isPlusOnedByMe()) {
        	String s = EsPeopleData.extractGaiaId(mPersonId);
            if(!EsService.isProfilePlusOnePending(s))
                mPlusOneRequestId = Integer.valueOf(EsService.createProfilePlusOne(mContext, mAccount, s));
        } else { 
        	String s1 = EsPeopleData.extractGaiaId(mPersonId);
            if(!EsService.isProfilePlusOnePending(s1))
                mPlusOneRequestId = Integer.valueOf(EsService.deleteProfilePlusOne(mContext, mAccount, s1));
        }
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        boolean flag1;
        boolean flag2;
        boolean flag;
        if(mControlPrimarySpinner)
        {
            android.widget.ArrayAdapter arrayadapter = ProfileActivity.createSpinnerAdapter(mContext);
            int i;
            if(mCurrentSpinnerPosition < 0)
                i = 0;
            else
                i = mCurrentSpinnerPosition;
            hostactionbar.showPrimarySpinner(arrayadapter, i);
        }
        flag = canShowRefreshInActionBar();
        if(!flag)
            super.updateSpinner();
        if(flag || !canShowConversationActions())
            hostactionbar.showRefreshButton();
        flag1 = isProgressIndicatorVisible();
        if(!canShowConversationActions())
            flag2 = false;
        else
        if(ScreenMetrics.getInstance(mContext).screenDisplayType == 0 && !mLandscape && flag1)
            flag2 = false;
        else
            flag2 = true;
        if(flag2)
        {
            hostactionbar.showActionButton(0, R.drawable.icn_startmessenger, R.string.start_conversation_action_label);
            if(Hangout.isHangoutCreationSupported(mContext, mAccount))
                hostactionbar.showActionButton(1, R.drawable.icn_starthangout, R.string.start_hangout_action_label);
        }
    }

    public final void onPrepareOptionsMenu(Menu menu)
    {
        boolean flag;
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        if(mHasGaiaId && !mIsMyProfile && mProfileAdapter != null && !TextUtils.isEmpty(mProfileAdapter.getFullName()) && mMuteRequestId == null && !mBlockInProgress && mReportAbuseRequestId == null && !mIsMute)
            flag = true;
        else
            flag = false;
        if(mHasGaiaId && !mIsMyProfile && mMuteRequestId == null && !mBlockInProgress && mReportAbuseRequestId == null && mIsMute)
            flag1 = true;
        else
            flag1 = false;
        if(mHasGaiaId && !mIsMyProfile && mProfileAdapter != null && !TextUtils.isEmpty(mProfileAdapter.getFullName()) && !mBlockInProgress && mReportAbuseRequestId == null && !mIsBlocked)
            flag2 = true;
        else
            flag2 = false;
        if(mHasGaiaId && !mIsMyProfile && !mBlockInProgress && mReportAbuseRequestId == null && mIsBlocked)
            flag3 = true;
        else
            flag3 = false;
        if(mHasGaiaId && !mIsMyProfile && mReportAbuseRequestId == null)
            flag4 = true;
        else
            flag4 = false;
        if(!canShowRefreshInActionBar() && canShowConversationActions())
            flag5 = true;
        else
            flag5 = false;
        menu.findItem(R.id.refresh).setVisible(flag5);
        menu.findItem(R.id.mute).setVisible(flag);
        menu.findItem(R.id.unmute).setVisible(flag1);
        menu.findItem(R.id.block).setVisible(flag2);
        menu.findItem(R.id.unblock).setVisible(flag3);
        menu.findItem(R.id.report_abuse).setVisible(flag4);
        if(mIsPlusPage)
        {
            menu.findItem(R.id.block).setTitle(R.string.menu_item_block_profile);
            menu.findItem(R.id.unblock).setTitle(R.string.menu_item_unblock_profile);
        } else
        {
            menu.findItem(R.id.block).setTitle(R.string.menu_item_block_person);
            menu.findItem(R.id.unblock).setTitle(R.string.menu_item_unblock_person);
        }
    }

    public final void onPrimarySpinnerSelectionChange(int i)
    {
        if(!mControlPrimarySpinner || mCurrentSpinnerPosition == i) {
        	return;
        }
        
        if(0 == i) {
        	super.refresh();
        } else if(1 == i) {
        	startActivity(Intents.getHostedProfileAlbumsIntent(mContext, mAccount, mPersonId, null));
        }
        
        mCurrentSpinnerPosition = i;
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mProfileServiceListener);
        if(mProfilePendingRequestId != null && !EsService.isRequestPending(mProfilePendingRequestId.intValue()))
        {
            ServiceResult serviceresult4 = EsService.removeResult(mProfilePendingRequestId.intValue());
            handleProfileServiceCallback(mProfilePendingRequestId.intValue(), serviceresult4);
            mProfilePendingRequestId = null;
        }
        if(mReportAbuseRequestId != null && !EsService.isRequestPending(mReportAbuseRequestId.intValue()))
        {
            ServiceResult serviceresult3 = EsService.removeResult(mReportAbuseRequestId.intValue());
            handleReportAbuseCallback(mReportAbuseRequestId.intValue(), serviceresult3);
            mReportAbuseRequestId = null;
        }
        if(mMuteRequestId != null && !EsService.isRequestPending(mMuteRequestId.intValue()))
        {
            ServiceResult serviceresult2 = EsService.removeResult(mMuteRequestId.intValue());
            handleSetMutedCallback(mMuteRequestId.intValue(), mMuteRequestIsMuted, serviceresult2);
            mMuteRequestId = null;
        }
        if(mPlusOneRequestId != null && !EsService.isRequestPending(mPlusOneRequestId.intValue()))
        {
            ServiceResult serviceresult1 = EsService.removeResult(mPlusOneRequestId.intValue());
            handlePlusOneCallback(mPlusOneRequestId.intValue(), serviceresult1);
            mPlusOneRequestId = null;
        }
        if(mInsertCameraPhotoRequestId != null && !EsService.isRequestPending(mInsertCameraPhotoRequestId.intValue()))
        {
            EsService.removeResult(mInsertCameraPhotoRequestId.intValue());
            handlerInsertCameraPhoto(mInsertCameraPhotoRequestId.intValue());
            mInsertCameraPhotoRequestId = null;
        }
        if(mSetCoverPhotoRequestId != null && !EsService.isRequestPending(mSetCoverPhotoRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mSetCoverPhotoRequestId.intValue());
            handleCoverPhotoCallback(mSetCoverPhotoRequestId.intValue(), serviceresult);
        }
        updateSpinner();
    }

    public final void onReviewAuthorAvatarClicked(String s)
    {
        if(!TextUtils.isEmpty(s))
            safeStartActivity(Intents.getProfileActivityByGaiaIdIntent(getActivity(), mAccount, s, null));
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mProfilePendingRequestId != null)
            bundle.putInt("profile_request_id", mProfilePendingRequestId.intValue());
        if(mPlusOneRequestId != null)
            bundle.putInt("plusone_request_id", mPlusOneRequestId.intValue());
        if(mReportAbuseRequestId != null)
            bundle.putInt("abuse_request_id", mReportAbuseRequestId.intValue());
        if(mMuteRequestId != null)
        {
            bundle.putInt("mute_request_id", mMuteRequestId.intValue());
            bundle.putBoolean("mute_state", mMuteRequestIsMuted);
        }
        if(mInsertCameraPhotoRequestId != null)
            bundle.putInt("camera_request_id", mInsertCameraPhotoRequestId.intValue());
        if(mSetCoverPhotoRequestId != null)
            bundle.putInt("cover_photo_request_id", mSetCoverPhotoRequestId.intValue());
        bundle.putBoolean("block_in_progress", mBlockInProgress);
        bundle.putBoolean("profile_is_expanded", mProfileIsExpanded);
        bundle.putInt("choose_photo_target", mChoosePhotoTarget);
    }

    public final void onSendTextClicked(String s)
    {
        if(!TextUtils.isEmpty(s))
            startExternalActivity(new Intent("android.intent.action.VIEW", Uri.parse((new StringBuilder("sms:")).append(Uri.encode(s)).toString())));
    }

    protected final void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mPersonId = bundle.getString("person_id");
        mIsMyProfile = mPersonId.equals(mAccount.getPersonId());
        boolean flag;
        if(EsPeopleData.extractGaiaId(mPersonId) != null)
            flag = true;
        else
            flag = false;
        mHasGaiaId = flag;
        mGaiaId = EsPeopleData.extractGaiaId(mPersonId);
        if(mGaiaId == null)
            bundle.putBoolean("show_empty_stream", true);
    }

    public final void onZagatExplanationClicked()
    {
        (new ProfileZagatExplanationDialog()).show(getFragmentManager(), "zagat_explanation");
    }

    public final void refresh()
    {
        super.refresh();
        refreshProfile();
    }

    public final void refreshProfile()
    {
        mProfilePendingRequestId = EsService.getProfileAndContact(getActivity(), mAccount, mPersonId, true);
        updateSpinner();
    }

    public final void relinquishPrimarySpinner()
    {
        mControlPrimarySpinner = false;
    }

    public final void reportAbuse(String s)
    {
        if("IMPERSONATION".equals(s))
        {
            AlertFragmentDialog.newInstance(getString(R.string.report_user_dialog_title), getString(R.string.report_impersonation_dialog_message), getString(0x104000a), null).show(getFragmentManager(), "dialog_warning");
        } else
        {
            mReportAbuseRequestId = EsService.reportProfileAbuse(mContext, mAccount, mGaiaId, s);
            showProgressDialog(R.string.report_abuse_operation_pending);
        }
    }

    protected final void setCircleMembership(ArrayList arraylist, ArrayList arraylist1)
    {
        ArrayList arraylist2 = new ArrayList();
        Iterator iterator = arraylist1.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s1 = (String)iterator.next();
            if(!arraylist.contains(s1))
                arraylist2.add(s1);
        } while(true);
        ArrayList arraylist3 = new ArrayList();
        Iterator iterator1 = arraylist.iterator();
        do
        {
            if(!iterator1.hasNext())
                break;
            String s = (String)iterator1.next();
            if(!arraylist1.contains(s))
                arraylist3.add(s);
        } while(true);
        mProfilePendingRequestId = EsService.setCircleMembership(getActivity(), mAccount, mPersonId, mProfileAdapter.getFullName(), (String[])arraylist2.toArray(new String[arraylist2.size()]), (String[])arraylist3.toArray(new String[arraylist3.size()]));
        if(!arraylist2.isEmpty() && arraylist3.isEmpty())
            showProgressDialog(R.string.add_to_circle_operation_pending);
        else
        if(arraylist2.isEmpty() && !arraylist3.isEmpty())
            showProgressDialog(R.string.remove_from_circle_operation_pending);
        else
            showProgressDialog(R.string.moving_between_circles_operation_pending);
    }

    protected final void setCoverPhoto(String s, int i, boolean flag)
    {
        Long long1 = mProfileAdapter.getScrapbookCoverPhotoId();
        if(long1 != null && Long.toString(long1.longValue()).equals(s))
        {
            updateCoverPhoto(s, i);
        } else
        {
            mSetCoverPhotoRequestId = Integer.valueOf(EsService.setCoverPhoto(getActivity(), mAccount, s, i, flag));
            showProgressDialog(R.string.setting_cover_photo);
        }
    }

    protected final void setCoverPhoto(byte abyte0[], int i)
    {
        mSetCoverPhotoRequestId = Integer.valueOf(EsService.uploadCoverPhoto(getActivity(), mAccount, abyte0, i));
        showProgressDialog(R.string.setting_cover_photo);
    }

    public final void setPersonMuted(boolean flag)
    {
        mMuteRequestId = EsService.setPersonMuted(mContext, mAccount, mGaiaId, flag);
        mMuteRequestIsMuted = flag;
        showProgressDialog(R.string.mute_operation_pending);
    }

    protected final void setProfilePhoto(byte abyte0[])
    {
        mProfilePendingRequestId = Integer.valueOf(EsService.uploadProfilePhoto(getActivity(), mAccount, abyte0));
        showProgressDialog(R.string.setting_profile_photo);
    }

    protected final void showProgressDialog(int i)
    {
        ProgressFragmentDialog.newInstance(null, getString(i), false).show(getFragmentManager(), "req_pending");
    }

    public final void unblockPerson(String s)
    {
        setPersonBlocked(false);
    }

    protected final void updateSpinner()
    {
        if(canShowRefreshInActionBar())
            super.updateSpinner();
        else
            invalidateActionBar();
    }
    
    
    static Integer access$002(HostedProfileFragment hostedprofilefragment, Integer integer)
    {
        hostedprofilefragment.mProfilePendingRequestId = integer;
        return integer;
    }
    
    static boolean access$1002(HostedProfileFragment hostedprofilefragment, boolean flag)
    {
        hostedprofilefragment.mIsMute = flag;
        return flag;
    }
    
    static boolean access$1200(HostedProfileFragment hostedprofilefragment, EsPeopleData.ProfileAndContactData profileandcontactdata)
    {
        boolean flag;
        if(System.currentTimeMillis() - profileandcontactdata.profileUpdateTime > 0xdbba0L)
        {
            if(Log.isLoggable("HostedProfileFragment", 3))
                Log.d("HostedProfileFragment", "Refreshing because profile info is stale.");
            flag = true;
        } else
        {
            flag = false;
        }
        return flag;
    }
    
    static boolean access$402(HostedProfileFragment hostedprofilefragment, boolean flag)
    {
        hostedprofilefragment.mProfileAndContactLoaderActive = false;
        return false;
    }
    
    static boolean access$802(HostedProfileFragment hostedprofilefragment, boolean flag)
    {
        hostedprofilefragment.mIsPlusPage = flag;
        return flag;
    }
    
    static boolean access$902(HostedProfileFragment hostedprofilefragment, boolean flag)
    {
        hostedprofilefragment.mIsBlocked = flag;
        return flag;
    }
    
	
	private static final class ProfileMergeCursor extends MergeCursor {

		private EsMatrixCursor mProfileCursor;
		private Cursor mStreamCursor;

		public ProfileMergeCursor(Cursor acursor[]) {
			super(acursor);
			mProfileCursor = (EsMatrixCursor) acursor[0];
			mStreamCursor = acursor[1];
		}

		public final boolean wrapsStreamCursor(Cursor cursor) {
			boolean flag;
			if (mStreamCursor == cursor)
				flag = true;
			else
				flag = false;
			return flag;
		}

	}
}
