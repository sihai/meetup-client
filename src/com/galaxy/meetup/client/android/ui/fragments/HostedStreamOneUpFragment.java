/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import WriteReviewOperation.MediaRef;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.MeetupFeedback;
import com.galaxy.meetup.client.android.PhotoOneUpAnimationController;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.ApiUtils;
import com.galaxy.meetup.client.android.api.OzServerException;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbEmbedDeepLink;
import com.galaxy.meetup.client.android.content.DbEmbedHangout;
import com.galaxy.meetup.client.android.content.DbEmbedSquare;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.DbPlusOneData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.activity.StreamOneUpActivity;
import com.galaxy.meetup.client.android.ui.view.ClickableButton;
import com.galaxy.meetup.client.android.ui.view.EsImageView;
import com.galaxy.meetup.client.android.ui.view.ExpandingScrollView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.LinearLayoutWithLayoutNotifications;
import com.galaxy.meetup.client.android.ui.view.MentionMultiAutoCompleteTextView;
import com.galaxy.meetup.client.android.ui.view.OneUpBaseView;
import com.galaxy.meetup.client.android.ui.view.OneUpLinkView;
import com.galaxy.meetup.client.android.ui.view.OneUpListener;
import com.galaxy.meetup.client.android.ui.view.OneUpTouchHandler;
import com.galaxy.meetup.client.android.ui.view.PhotoHeaderView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpCommentView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpHangoutView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpListView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpSkyjamView;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.MapUtils;
import com.galaxy.meetup.client.util.SoftInput;

/**
 * 
 * @author sihai
 * 
 */
public class HostedStreamOneUpFragment extends HostedFragment implements
		android.support.v4.app.LoaderManager.LoaderCallbacks,
		View.OnClickListener, AdapterView.OnItemClickListener,
		AlertFragmentDialog.AlertDialogListener,
		StreamOneUpActivity.OnScreenListener,
		ClickableButton.ClickableButtonListener,
		LinearLayoutWithLayoutNotifications.LayoutListener,
		OneUpBaseView.OnMeasuredListener,
		OneUpLinkView.BackgroundViewLoadedListener, OneUpListener,
		PhotoHeaderView.OnImageListener {

	private static int sAvatarMarginTop;
	private static int sMaxWidth;
	private static int sMinExposureLand;
	private static int sMinExposurePort;
	private static boolean sResourcesLoaded;
	private EsAccount mAccount;
	private PhotoOneUpAnimationController mActionBarAnimator;
	private boolean mActivityDataNotFound;
	private String mActivityId;
	private Integer mActivityRequestId;
	private StreamOneUpAdapter mAdapter;
	private String mAlbumId;
	private DbEmbedDeepLink mAppInviteData;
	private AudienceData mAudienceData;
	private String mAuthorId;
	private boolean mAutoPlay;
	private String mBackgroundLinkUrl;
	private MediaRef mBackgroundRef;
	private StreamOneUpCallbacks mCallback;
	private View mCommentButton;
	private MentionMultiAutoCompleteTextView mCommentText;
	private String mCreationSource;
	private String mCreationSourceId;
	private DbEmbedDeepLink mDeepLinkData;
	private String mDeepLinkLabel;
	private String mEditableText;
	private HashSet mFlaggedComments;
	private LinearLayoutWithLayoutNotifications mFooter;
	private PhotoOneUpAnimationController mFooterAnimator;
	private boolean mFullScreen;
	private boolean mGetActivityComplete;
	private PhotoHeaderView mImageView;
	private boolean mIsActivityMuted;
	private boolean mIsActivityPublic;
	private boolean mIsActivityResharable;
	private boolean mIsAlbum;
	private boolean mIsGraySpam;
	private Boolean mIsMyActivity;
	private boolean mIsSquarePost;
	private String mLinkTitle;
	private String mLinkUrl;
	private OneUpLinkView mLinkView;
	private PhotoOneUpAnimationController mListAnimator;
	private View mListParent;
	private StreamOneUpListView mListView;
	private DbLocation mLocationData;
	private boolean mMuteProcessed;
	private int mOperationType;
	private Integer mPendingRequestId;
	private Pair mPlusOnedByData;
	private boolean mReadProcessed;
	private boolean mReshare;
	private final ServiceListener mServiceListener = new ServiceListener();
	private String mSourceAuthorId;
	private String mSourcePackageName;
	private String mSquareId;
	private boolean mStageMediaLoaded;
	private TextWatcher mTextWatcher;
	private OneUpTouchHandler mTouchHandler;
	private boolean mUpdateActionBar;
	private boolean mViewerIsSquareAdmin;
	
    public HostedStreamOneUpFragment()
    {
        mOperationType = 0;
    }

    private static void adjustActionBarMargins(HostActionBar hostactionbar, boolean flag)
    {
        android.widget.FrameLayout.LayoutParams layoutparams = (android.widget.FrameLayout.LayoutParams)hostactionbar.getLayoutParams();
        int i = layoutparams.leftMargin;
        int j = layoutparams.rightMargin;
        int k;
        if(flag)
            k = -hostactionbar.getHeight();
        else
            k = 0;
        layoutparams.setMargins(i, k, j, layoutparams.bottomMargin);
        hostactionbar.setLayoutParams(layoutparams);
    }

    private boolean bindStageLink(View view)
    {
        MediaRef mediaref;
        View view1;
        boolean flag;
        if(mBackgroundRef == null || TextUtils.isEmpty(mBackgroundRef.getUrl()))
            mediaref = null;
        else
            mediaref = mBackgroundRef;
        view1 = view.findViewById(R.id.stage);
        if(mediaref == null && mDeepLinkLabel == null)
        {
            flag = false;
            if(view1 != null)
                view1.setVisibility(8);
        } else
        {
            if(view1 == null)
            {
                View view2 = ((ViewStub)view.findViewById(R.id.stage_link)).inflate();
                View view3 = view2.findViewById(R.id.loading);
                int i;
                byte byte0;
                if(!mGetActivityComplete)
                    i = 0;
                else
                    i = 8;
                view3.setVisibility(i);
                mLinkView = (OneUpLinkView)view2.findViewById(R.id.background);
                if(mediaref == null || mediaref.getType() == MediaRef.MediaType.IMAGE)
                    byte0 = 3;
                else
                    byte0 = 2;
                mLinkView.init(mediaref, byte0, this, mLinkTitle, mDeepLinkLabel, this, mLinkUrl);
                mLinkView.setOnClickListener(this);
                if(mediaref == null)
                    onBackgroundViewLoaded(mLinkView);
                mTouchHandler.setBackground(mLinkView);
                ((ExpandingScrollView)mListParent.findViewById(R.id.list_expander)).setAlwaysExpanded(false);
                view2.invalidate();
            }
            flag = true;
        }
        return flag;
    }

    private boolean bindStageMedia(View view)
    {
        String s;
        View view1;
        boolean flag;
        if(mBackgroundRef == null)
            s = null;
        else
            s = mBackgroundRef.getUrl();
        view1 = view.findViewById(R.id.stage);
        if(s == null)
        {
            flag = false;
            if(view1 != null)
                view1.setVisibility(8);
        } else
        {
            if(view1 == null)
            {
                View view2 = ((ViewStub)view.findViewById(R.id.stage_media)).inflate();
                View view3 = view2.findViewById(R.id.loading);
                int i;
                ExpandingScrollView expandingscrollview;
                if(!mGetActivityComplete)
                    i = 0;
                else
                    i = 8;
                view3.setVisibility(i);
                mImageView = (PhotoHeaderView)view2.findViewById(R.id.background);
                mImageView.setOnImageListener(this);
                mImageView.init(new MediaRef(mBackgroundRef.getOwnerGaiaId(), mBackgroundRef.getPhotoId(), mBackgroundRef.getUrl(), null, mBackgroundRef.getType()), getResources().getColor(R.color.stream_one_up_background));
                mImageView.doAnimate(true);
                mImageView.setOnClickListener(this);
                mImageView.enableImageTransforms(true);
                mTouchHandler.setBackground(mImageView);
                expandingscrollview = (ExpandingScrollView)mListParent.findViewById(R.id.list_expander);
                expandingscrollview.setAlwaysExpanded(false);
                expandingscrollview.setMinimumExposure(sMinExposureLand, sMinExposurePort);
                expandingscrollview.setBigBounce(true);
                view2.invalidate();
            }
            flag = true;
        }
        return flag;
    }

    private void doReportComment(boolean flag, String s, boolean flag1)
    {
        Bundle bundle = EsAnalyticsData.createExtras("extra_comment_id", s);
        recordUserAction(OzActions.ONE_UP_REPORT_ABUSE_COMMENT, bundle);
        String s1;
        String s2;
        AlertFragmentDialog alertfragmentdialog;
        if(flag1)
            s1 = getString(R.string.stream_one_up_comment_undo_report_dialog_title);
        else
            s1 = getString(R.string.stream_one_up_comment_report_dialog_title);
        if(flag1)
            s2 = getString(R.string.stream_one_up_comment_undo_report_dialog_question);
        else
            s2 = getString(R.string.stream_one_up_comment_report_dialog_question);
        alertfragmentdialog = AlertFragmentDialog.newInstance(s1, s2, getString(R.string.ok), getString(R.string.cancel));
        alertfragmentdialog.setTargetFragment(this, 0);
        alertfragmentdialog.getArguments().putString("comment_id", s);
        alertfragmentdialog.getArguments().putBoolean("delete", flag);
        alertfragmentdialog.getArguments().putBoolean("is_undo", flag1);
        alertfragmentdialog.show(getFragmentManager(), "hsouf_report_comment");
    }
    
    private void launchDeepLink(List list, String s, String s1, String s2, boolean flag) {
        // TODO
    }

    private void onUrlClick(String s) {
        Context context = getSafeContext();
        if(!s.startsWith("acl:")) {
        	if(s.startsWith("+1:"))
            {
                String as[] = s.split(":");
                if(as != null && as.length == 3)
                    showPlusOnePeople(as[1], Integer.valueOf(as[2]).intValue());
            } else
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
                Intents.viewContent(getActivity(), mAccount, s, mCreationSourceId);
            }
        	return;
        }
        
        if(mAudienceData == null)
        {
            if(mPendingRequestId == null)
            {
                String s2 = mAdapter.getAclText();
                String s3;
                if(TextUtils.equals(s2, context.getString(R.string.acl_public)))
                    s3 = getString(R.string.acl_description_public);
                else
                if(TextUtils.equals(s2, context.getString(R.string.acl_private_contacts)))
                    s3 = getString(R.string.acl_description_private_contacts);
                else
                if(TextUtils.equals(s2, context.getString(R.string.acl_extended_network)))
                    s3 = getString(R.string.acl_description_extended_network);
                else
                if(!TextUtils.equals(s2, context.getString(R.string.acl_limited)))
                {
                    s3 = getString(R.string.acl_description_domain, new Object[] {
                        s2
                    });
                } else
                {
                    mPendingRequestId = Integer.valueOf(EsService.getActivityAudience(context, mAccount, mActivityId));
                    showProgressDialog(48);
                    s3 = null;
                }
                if(s3 != null)
                    AlertFragmentDialog.newInstance(s2, s3, getString(R.string.ok), null).show(getFragmentManager(), "hsouf_audience");
            }
        } else
        {
            showAudience(mAudienceData);
        }

    }

    private void showAudience(AudienceData audiencedata)
    {
        if(EsLog.isLoggable("StreamOneUp", 3))
        {
            Log.d("StreamOneUp", (new StringBuilder("Hidden count: ")).append(audiencedata.getHiddenUserCount()).toString());
            Log.d("StreamOneUp", (new StringBuilder("Audience users: ")).append(audiencedata.getUserCount()).toString());
            PersonData apersondata[] = audiencedata.getUsers();
            int i = apersondata.length;
            for(int j = 0; j < i; j++)
            {
                PersonData persondata = apersondata[j];
                Log.d("StreamOneUp", (new StringBuilder("Users: ")).append(persondata.getName()).toString());
            }

        }
        String s = mAdapter.getAclText();
        PeopleListDialogFragment peoplelistdialogfragment = new PeopleListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", mAccount);
        bundle.putParcelable("audience", audiencedata);
        bundle.putString("people_list_title", s);
        peoplelistdialogfragment.setArguments(bundle);
        peoplelistdialogfragment.show(getFragmentManager(), "hsouf_audience");
    }

    private void showPlusOnePeople(String s, int i)
    {
        PlusOnePeopleFragment plusonepeoplefragment = new PlusOnePeopleFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", mAccount);
        bundle.putString("plus_one_id", s);
        bundle.putInt("total_plus_ones", i);
        plusonepeoplefragment.setArguments(bundle);
        plusonepeoplefragment.show(getFragmentManager(), "hsouf_plus_ones");
    }

    private void showProgressDialog(int i)
    {
        mOperationType = i;
        int j;
        if(i == 48)
            j = R.string.loading;
        else
            j = R.string.post_operation_pending;
        ProgressFragmentDialog.newInstance(null, getString(j), false).show(getFragmentManager(), "hsouf_pending");
    }

    private void updateLoadingSpinner(View view)
    {
        byte byte0 = 8;
        if(view != null)
        {
            View view1 = view.findViewById(R.id.loading);
            if(view1 != null)
                if(view.findViewById(R.id.stage) != null)
                {
                    if(!mStageMediaLoaded)
                        byte0 = 0;
                    view1.setVisibility(byte0);
                } else
                {
                    if(!mGetActivityComplete)
                        byte0 = 0;
                    view1.setVisibility(byte0);
                }
        }
    }

    public final void enableImageTransforms(boolean flag)
    {
        if(mImageView != null)
            mImageView.enableImageTransforms(flag);
    }

    public final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.ACTIVITY;
    }

    public final void onActionButtonClicked(int i)
    {
        if(i == 0)
        {
            Intent intent = Intents.getReshareActivityIntent(getActivity(), mAccount, mActivityId, mIsActivityPublic);
            if(mIsActivityPublic)
            {
                Bundle bundle = EsAnalyticsData.createExtras("extra_activity_id", mActivityId);
                recordUserAction(OzActions.OPEN_RESHARE_SHAREBOX, bundle);
                ConfirmIntentDialog.newInstance(getString(R.string.reshare_dialog_title), getString(R.string.reshare_dialog_message), getString(R.string.reshare_dialog_positive_button), intent).show(getFragmentManager(), "reshare_activity");
            } else
            {
                startActivity(intent);
            }
        } else
        {
            super.onActionButtonClicked(i);
        }
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
        int k = 1;
        Bundle bundle = EsAnalyticsData.createExtras("extra_creation_source_id", mCreationSourceId);
        if(i == k || i == 2)
        {
            if(i != 2)
                k = 0;
            if(j == -1)
            {
                if(getActivity() != null)
                {
                    Context context1 = getSafeContext();
                    EsAccount esaccount1 = mAccount;
                    OzActions ozactions1;
                    if(k != 0)
                        ozactions1 = OzActions.CALL_TO_ACTION_INSTALL_STARTED_ON_PLAY_STORE;
                    else
                        ozactions1 = OzActions.DEEP_LINK_INSTALL_STARTED_ON_PLAY_STORE;
                    EsAnalytics.recordActionEvent(context1, esaccount1, ozactions1, OzViews.ACTIVITY, bundle);
                }
            } else
            {
                Context context = getSafeContext();
                EsAccount esaccount = mAccount;
                OzActions ozactions;
                if(k != 0)
                    ozactions = OzActions.CALL_TO_ACTION_INSTALL_NOT_STARTED_ON_PLAY_STORE;
                else
                    ozactions = OzActions.DEEP_LINK_INSTALL_NOT_STARTED_ON_PLAY_STORE;
                EsAnalytics.recordActionEvent(context, esaccount, ozactions, OzViews.ACTIVITY, bundle);
            }
        }
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if(activity instanceof StreamOneUpCallbacks)
        {
            mCallback = (StreamOneUpCallbacks)activity;
            return;
        } else
        {
            throw new IllegalArgumentException("Activity must implement PhotoOneUpCallbacks");
        }
    }

    public final void onBackgroundViewLoaded(OneUpLinkView oneuplinkview)
    {
        if(oneuplinkview.getId() == R.id.background)
        {
            mStageMediaLoaded = true;
            updateLoadingSpinner(getView());
        }
    }

    public final void onCancelRequested()
    {
        if(mCommentText == null)
            getActivity().finish();
        else
        if(mCommentText.getText().toString().length() == 0)
        {
            getActivity().finish();
        } else
        {
            AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.comment_title), getString(R.string.comment_quit_question), getString(R.string.yes), getString(R.string.no));
            alertfragmentdialog.setTargetFragment(this, 0);
            alertfragmentdialog.show(getFragmentManager(), "hsouf_cancel_edits");
        }
    }

    public void onClick(View view) {
        int i = view.getId();
        if(i != R.id.footer_post_button) {
        	if(i == R.id.list_expander || i == R.id.background)
                if(mBackgroundRef == null)
                {
                    if(view instanceof ExpandingScrollView)
                    {
                        MotionEvent motionevent = ((ExpandingScrollView)view).getLastTouchEvent();
                        if(motionevent != null)
                        {
                            float f = motionevent.getRawX();
                            float f1 = motionevent.getRawY();
                            View view1 = getView().findViewById(R.id.stage);
                            if(view1 != null)
                            {
                                StreamOneUpHangoutView streamoneuphangoutview = (StreamOneUpHangoutView)view1.findViewById(R.id.hangout);
                                if(streamoneuphangoutview != null)
                                    streamoneuphangoutview.processClick(f, f1);
                                StreamOneUpSkyjamView streamoneupskyjamview = (StreamOneUpSkyjamView)view1.findViewById(R.id.skyjam);
                                if(streamoneupskyjamview != null)
                                    streamoneupskyjamview.processClick(f, f1);
                            }
                        }
                    }
                } else
                {
                    MediaRef.MediaType mediatype = mBackgroundRef.getType();
                    if(mediatype == MediaRef.MediaType.IMAGE || mediatype == MediaRef.MediaType.PANORAMA)
                    {
                        if(mBackgroundRef.getPhotoId() != 0L)
                        {
                            if(!mIsAlbum)
                            {
                                if(mImageView.isPanorama())
                                    startActivity(Intents.getViewPanoramaActivityIntent(getActivity(), mAccount, mBackgroundRef));
                                else
                                    mCallback.toggleFullScreen();
                            } else
                            {
                                Context context = getSafeContext();
                                if(context != null)
                                {
                                    Intents.PhotoViewIntentBuilder photoviewintentbuilder = Intents.newPhotoViewActivityIntentBuilder(context);
                                    String s = mBackgroundRef.getOwnerGaiaId();
                                    photoviewintentbuilder.setAccount(mAccount).setPhotoRef(mBackgroundRef).setGaiaId(s).setAlbumId(mAlbumId).setRefreshAlbumId(mAlbumId).setDisableComments(Boolean.valueOf(mIsSquarePost));
                                    Bundle bundle = EsAnalyticsData.createExtras("extra_gaia_id", s);
                                    recordUserAction(OzActions.ONE_UP_SELECT_PHOTO, bundle);
                                    startActivity(photoviewintentbuilder.build());
                                }
                            }
                        } else
                        if(mDeepLinkData != null && !mDeepLinkData.getClientPackageNames().isEmpty())
                        {
                            //mCreationSource;
                            launchDeepLink(mDeepLinkData.getClientPackageNames(), mDeepLinkData.getDeepLinkId(), mBackgroundLinkUrl, mAuthorId, false);
                        } else
                        if(!TextUtils.isEmpty(mBackgroundLinkUrl))
                            Intents.viewContent(getActivity(), mAccount, mBackgroundLinkUrl, mCreationSourceId);
                    } else
                    {
                        String s1 = mBackgroundRef.getLocalUri().toString();
                        Intents.viewContent(getActivity(), mAccount, s1, mCreationSourceId);
                    }
                } 
        } else { 
        	if(mCommentText.getText().toString().trim().length() > 0)
            {
                if(mPendingRequestId == null)
                {
                    Bundle bundle1 = EsAnalyticsData.createExtras("extra_activity_id", mActivityId);
                    recordUserAction(OzActions.ONE_UP_POST_COMMENT, bundle1);
                    Editable editable = mCommentText.getText();
                    Context context1 = getSafeContext();
                    String s2 = ApiUtils.buildPostableString(editable);
                    mPendingRequestId = Integer.valueOf(EsService.createComment(context1, mAccount, mActivityId, s2));
                    showProgressDialog(32);
                }
            } else
            {
                mCommentButton.setEnabled(false);
            }
        }
    }

    public final void onClickableButtonListenerClick(ClickableButton clickablebutton) {
        if(mAppInviteData == null) {
        	return;
        }
        
        if(mAppInviteData.getClientPackageNames().isEmpty() || TextUtils.isEmpty(mAppInviteData.getDeepLinkId())) {
        	if(!TextUtils.isEmpty(mAppInviteData.getUrl()))
                Intents.viewContent(getActivity(), mAccount, mAppInviteData.getUrl(), mCreationSourceId);
        } else { 
        	//mCreationSource;
            launchDeepLink(mAppInviteData.getClientPackageNames(), mAppInviteData.getDeepLinkId(), mAppInviteData.getUrl(), mAuthorId, true);
        }
    }

    public final void onCreate(Bundle bundle) {
        Intent intent;
        super.onCreate(bundle);
        mFlaggedComments = new HashSet();
        intent = getActivity().getIntent();
        mAccount = (EsAccount)intent.getParcelableExtra("account");
        mActivityId = intent.getStringExtra("activity_id");
        mAlbumId = intent.getStringExtra("album_id");
        mBackgroundRef = (MediaRef)intent.getParcelableExtra("photo_ref");
        mBackgroundLinkUrl = intent.getStringExtra("photo_link_url");
        mLinkTitle = intent.getStringExtra("link_title");
        mDeepLinkLabel = intent.getStringExtra("deep_link_label");
        mLinkUrl = intent.getStringExtra("link_url");
        mIsAlbum = intent.getBooleanExtra("is_album", false);
        mViewerIsSquareAdmin = intent.getBooleanExtra("square_admin", false);
        mSquareId = intent.getStringExtra("square_id");
        mFullScreen = false;
        if(bundle == null) {
        	if(intent.getBooleanExtra("refresh", false))
                refresh();
        } else { 
        	if(bundle.containsKey("pending_request_id"))
                mPendingRequestId = Integer.valueOf(bundle.getInt("pending_request_id"));
            if(bundle.containsKey("activity_request_id"))
                mActivityRequestId = Integer.valueOf(bundle.getInt("activity_request_id"));
            mAudienceData = (AudienceData)bundle.getParcelable("audience_data");
            String as[] = bundle.getStringArray("flagged_comments");
            if(as != null)
                mFlaggedComments.addAll(Arrays.asList(as));
            mOperationType = bundle.getInt("operation_type", 0);
            mMuteProcessed = bundle.getBoolean("mute_processed", false);
            mReadProcessed = bundle.getBoolean("read_processed", false);
            mSourcePackageName = bundle.getString("source_package_name");
            mSourceAuthorId = bundle.getString("source_author_id");
            mGetActivityComplete = bundle.getBoolean("get_activity_complete");
            mStageMediaLoaded = bundle.getBoolean("stage_media_loaded");
            mFullScreen = bundle.getBoolean("full_screen");
            mAutoPlay = false;
        }
        
        mUpdateActionBar = mFullScreen;
        return;
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	Loader loader = null;
    	if(519746381 == i) {
    		loader = new StreamOneUpLoader(getSafeContext(), mAccount, mActivityId);
    	}
    	return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        byte byte0 = 8;
        Context context = getSafeContext();
        if(!sResourcesLoaded)
        {
            Resources resources = context.getResources();
            sMaxWidth = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_max_width);
            sMinExposureLand = resources.getDimensionPixelOffset(R.dimen.one_up_photo_min_exposure_land);
            sMinExposurePort = resources.getDimensionPixelOffset(R.dimen.one_up_photo_min_exposure_port);
            sAvatarMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_top);
            sResourcesLoaded = true;
        }
        View view = layoutinflater.inflate(R.layout.stream_one_up_fragment, viewgroup, false);
        mListParent = view.findViewById(R.id.list_parent);
        mListParent.findViewById(R.id.list_expander).setOnClickListener(this);
        View view1 = mListParent;
        byte byte1;
        LinearLayoutWithLayoutNotifications linearlayoutwithlayoutnotifications;
        View view2;
        boolean flag;
        if(mFullScreen)
            byte1 = byte0;
        else
            byte1 = 0;
        view1.setVisibility(byte1);
        mListAnimator = new PhotoOneUpAnimationController(mListParent, false, false);
        mListView = (StreamOneUpListView)view.findViewById(0x102000a);
        mAdapter = new StreamOneUpAdapter(context, null, this, mListView);
        mAdapter.setLoading(true);
        mListView.setMaxWidth(sMaxWidth);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnMeasureListener(this);
        mTouchHandler = (OneUpTouchHandler)view.findViewById(R.id.touch_handler);
        mTouchHandler.setScrollView(mListParent);
        mTouchHandler.setActionBar(getActionBar());
        if(mBackgroundRef == null || mBackgroundRef.hasPhotoId() || mBackgroundRef.getType() == MediaRef.MediaType.VIDEO)
            bindStageMedia(view);
        else
            bindStageLink(view);
        mFooter = (LinearLayoutWithLayoutNotifications)view.findViewById(R.id.footer);
        mCommentText = (MentionMultiAutoCompleteTextView)view.findViewById(R.id.footer_text);
        mCommentText.setEnabled(false);
        mCommentText.setHint(null);
        mFooter.setLayoutListener(this);
        mFooter.setMaxWidth(sMaxWidth);
        mFooterAnimator = new PhotoOneUpAnimationController(mFooter, false, true);
        linearlayoutwithlayoutnotifications = mFooter;
        if(!mFullScreen)
            byte0 = 0;
        linearlayoutwithlayoutnotifications.setVisibility(byte0);
        (new CircleNameResolver(context, getLoaderManager(), mAccount)).initLoader();
        mCommentText.init(this, mAccount, mActivityId, null);
        mCommentButton = view.findViewById(R.id.footer_post_button);
        mCommentButton.setOnClickListener(this);
        view2 = mCommentButton;
        if(mCommentText.getText().length() > 0)
            flag = true;
        else
            flag = false;
        view2.setEnabled(flag);
        mTextWatcher = new MyTextWatcher(mCommentButton);
        mCommentText.addTextChangedListener(mTextWatcher);
        mCommentText.setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {

            public final boolean onEditorAction(TextView textview, int i, KeyEvent keyevent)
            {
                boolean flag1;
                if(i == 6)
                {
                    mCommentButton.performClick();
                    flag1 = true;
                } else
                {
                    flag1 = false;
                }
                return flag1;
            }

        });
        getLoaderManager().initLoader(0x1efab34d, null, this);
        if(getActivity().getIntent().getBooleanExtra("show_keyboard", false))
            mCommentText.postDelayed(new Runnable() {

                public final void run()
                {
                    if(mCommentButton != null && mCommentText != null && mCommentText.isEnabled())
                    {
                        mCommentText.requestFocus();
                        SoftInput.show(mCommentText);
                    }
                }

            }, 250L);
        return view;
    }

    public final void onDestroyView()
    {
        mCommentText.removeTextChangedListener(mTextWatcher);
        mCommentText.setOnEditorActionListener(null);
        mCommentText.destroy();
        mCommentText = null;
        mCommentButton.setOnClickListener(null);
        mCommentButton = null;
        super.onDestroyView();
    }

    public final void onDialogCanceled(String s)
    {
    }

    public final void onDialogListClick(int i, Bundle bundle) {
        ArrayList arraylist = bundle.getIntegerArrayList("comment_action");
        if(null == arraylist) {
        	Log.w("StreamOneUp", "No actions for comment option dialog");
        	return;
        }
        if(i >= arraylist.size()) {
        	Log.w("StreamOneUp", "Option selected outside the action list");
        	return;
        }
        String s = bundle.getString("comment_id");
        String s1 = bundle.getString("comment_content");
        String s2 = bundle.getString("plus_one_id");
        boolean flag = bundle.getBoolean("plus_one_by_me");
        int j = bundle.getInt("plus_one_count");
        switch(((Integer)arraylist.get(i)).intValue())
        {
        case 33: // '!'
            Bundle bundle2 = EsAnalyticsData.createExtras("extra_comment_id", s);
            recordUserAction(OzActions.ONE_UP_DELETE_COMMENT, bundle2);
            AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.menu_delete_comment), getString(R.string.comment_delete_question), getString(R.string.ok), getString(R.string.cancel));
            alertfragmentdialog.setTargetFragment(this, 0);
            alertfragmentdialog.getArguments().putString("comment_id", s);
            alertfragmentdialog.show(getFragmentManager(), "hsouf_delete_comment");
            break;

        case 37: // '%'
            if(s2 == null || !flag)
                EsService.plusOneComment(getSafeContext(), mAccount, mActivityId, 0L, s, null, true);
            else
                EsService.plusOneComment(getSafeContext(), mAccount, mActivityId, 0L, s, s2, false);
            break;

        case 38: // '&'
            Bundle bundle1 = EsAnalyticsData.createExtras("extra_comment_id", s);
            recordUserAction(OzActions.ONE_UP_EDIT_COMMENT, bundle1);
            startActivity(Intents.getEditCommentActivityIntent(getSafeContext(), mAccount, mActivityId, s, s1, null, null));
            break;

        case 34: // '"'
            doReportComment(false, s, false);
            break;

        case 35: // '#'
            doReportComment(false, s, true);
            break;

        case 36: // '$'
            doReportComment(true, s, false);
            break;

        case 64: // '@'
            showPlusOnePeople(s2, j);
            break;
        }
    }

    public final void onDialogNegativeClick(String s)
    {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s) {
        boolean flag = true;
        if("hsouf_delete_activity".equals(s)) {
        	mPendingRequestId = Integer.valueOf(EsService.deleteActivity(getSafeContext(), mAccount, mActivityId));
            showProgressDialog(16);
            return;
        }
        if("hsouf_delete_comment".equals(s))
        {
            mPendingRequestId = Integer.valueOf(EsService.deleteComment(getSafeContext(), mAccount, mActivityId, bundle.getString("comment_id")));
            showProgressDialog(33);
        } else
        if("hsouf_report_comment".equals(s))
        {
            mPendingRequestId = Integer.valueOf(EsService.moderateComment(getSafeContext(), mAccount, mActivityId, bundle.getString("comment_id"), bundle.getBoolean("delete", false), flag, bundle.getBoolean("is_undo", false)));
            showProgressDialog(34);
        } else
        if("hsouf_mute_activity".equals(s))
        {
            Context context = getSafeContext();
            EsAccount esaccount = mAccount;
            String s2 = mActivityId;
            if(mIsActivityMuted)
                flag = false;
            mPendingRequestId = Integer.valueOf(EsService.muteActivity(context, esaccount, s2, flag));
            showProgressDialog(17);
        } else
        if("hsouf_report_activity".equals(s))
        {
            String s1;
            if(mViewerIsSquareAdmin)
                s1 = mCreationSourceId;
            else
                s1 = null;
            mPendingRequestId = Integer.valueOf(EsService.reportActivity(getSafeContext(), mAccount, mActivityId, s1));
            showProgressDialog(18);
        } else
        if("hsouf_cancel_edits".equals(s))
            getActivity().finish();
    }

    public final void onFullScreenChanged(boolean flag)
    {
        mFullScreen = flag;
        if(!mFullScreen)
        {
            if(mUpdateActionBar)
                adjustActionBarMargins(getActionBar(), false);
            mUpdateActionBar = false;
        }
        if(mFooterAnimator != null)
            mFooterAnimator.animate(mFullScreen);
        if(mListAnimator != null)
            mListAnimator.animate(mFullScreen);
        if(mActionBarAnimator != null)
            mActionBarAnimator.animate(mFullScreen);
        if(!mFullScreen && mImageView != null)
            mImageView.resetTransformations();
    }

    public final void onImageLoadFinished(PhotoHeaderView photoheaderview)
    {
        mStageMediaLoaded = true;
        View view = getView();
        updateLoadingSpinner(view);
        if(photoheaderview.isPhotoBound())
        {
            boolean flag;
            View view1;
            int i;
            boolean flag1;
            EsImageView esimageview;
            int j;
            if(mBackgroundRef != null && mBackgroundRef.getType() == MediaRef.MediaType.VIDEO)
                flag = true;
            else
                flag = false;
            view1 = view.findViewById(R.id.video_overlay);
            if(flag)
                i = 0;
            else
                i = 8;
            view1.setVisibility(i);
            if(!flag && mIsAlbum)
                flag1 = true;
            else
                flag1 = false;
            esimageview = (EsImageView)view.findViewById(R.id.album_overlay);
            j = 0;
            if(!flag1)
                j = 8;
            esimageview.setVisibility(j);
            if(flag1)
                esimageview.startFadeOut(2000);
        }
    }

    public final void onImageScaled(float f)
    {
        boolean flag;
        if(f > 1.0F)
            flag = true;
        else
            flag = false;
        if(mFullScreen != flag)
            mCallback.toggleFullScreen();
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l) {
    	
        if(!(view instanceof StreamOneUpCommentView)) {
        	if(Log.isLoggable("StreamOneUp", 3))
                Log.e("StreamOneUp", (new StringBuilder("HostedStreamOneUpFragment.onItemClick: Some other view: ")).append(view).toString());
        	return;
        }
        StreamOneUpCommentView streamoneupcommentview = (StreamOneUpCommentView)view;
        Resources resources = getSafeContext().getResources();
        boolean flag = mAccount.isMyGaiaId(streamoneupcommentview.getAuthorId());
        boolean flag1 = mAccount.isMyGaiaId(mAdapter.getActivityAuthorId());
        ArrayList arraylist = new ArrayList(5);
        ArrayList arraylist1 = new ArrayList(5);
        boolean flag2 = streamoneupcommentview.getPlusOneByMe();
        String s = streamoneupcommentview.getPlusOneId();
        int j = streamoneupcommentview.getPlusOneCount();
        boolean flag3 = streamoneupcommentview.isFlagged();
        if(!flag3)
        {
            String as[];
            AlertFragmentDialog alertfragmentdialog;
            int k;
            if(flag2)
                k = R.string.stream_one_up_comment_option_plusminus;
            else
                k = R.string.stream_one_up_comment_option_plusone;
            arraylist.add(resources.getString(k));
            arraylist1.add(Integer.valueOf(37));
        }
        if(flag)
        {
            arraylist.add(resources.getString(R.string.stream_one_up_comment_option_edit));
            arraylist1.add(Integer.valueOf(38));
        } else
        if(flag3)
        {
            arraylist.add(resources.getString(R.string.stream_one_up_comment_option_undo_report));
            arraylist1.add(Integer.valueOf(35));
        } else
        {
            arraylist.add(resources.getString(R.string.stream_one_up_comment_option_report));
            if(flag1)
                arraylist1.add(Integer.valueOf(36));
            else
                arraylist1.add(Integer.valueOf(34));
        }
        if(flag1 || flag)
        {
            arraylist.add(resources.getString(R.string.stream_one_up_comment_option_delete));
            arraylist1.add(Integer.valueOf(33));
        }
        if(s != null && j > 0)
        {
            arraylist.add(resources.getString(R.string.stream_one_up_comment_option_plus_ones));
            arraylist1.add(Integer.valueOf(64));
        }
        String[] as = new String[arraylist.size()];
        arraylist.toArray(as);
        AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.stream_one_up_comment_options_title), as);
        alertfragmentdialog.setTargetFragment(this, 0);
        alertfragmentdialog.getArguments().putIntegerArrayList("comment_action", arraylist1);
        alertfragmentdialog.getArguments().putString("comment_id", streamoneupcommentview.getCommentId());
        alertfragmentdialog.getArguments().putString("comment_content", streamoneupcommentview.getCommentContent());
        alertfragmentdialog.getArguments().putString("plus_one_id", s);
        alertfragmentdialog.getArguments().putBoolean("plus_one_by_me", flag2);
        alertfragmentdialog.getArguments().putInt("plus_one_count", streamoneupcommentview.getPlusOneCount());
        alertfragmentdialog.show(getFragmentManager(), "hsouf_delete_comment");
        streamoneupcommentview.cancelPressedState();
    }

    public final void onLoadFinished(Loader loader, Object obj) {
        Cursor cursor = (Cursor)obj;
        if(519746381 == loader.getId()) {
        	View view2;
            View view1;
            if(cursor == null || !cursor.moveToFirst())
            {
                mActivityDataNotFound = true;
                refresh();
            } else
            {
                mActivityDataNotFound = false;
                mCreationSourceId = cursor.getString(13);
                if(!mReadProcessed)
                {
                    Bundle bundle = EsAnalyticsData.createExtras("extra_activity_id", mActivityId);
                    if(!TextUtils.isEmpty(mCreationSourceId))
                    {
                        if(bundle == null)
                            bundle = new Bundle();
                        bundle.putString("extra_creation_source_id", mCreationSourceId);
                    }
                    recordUserAction(OzActions.ONE_UP_MARK_ACTIVITY_AS_READ, bundle);
                    mReadProcessed = true;
                }
                long l1 = cursor.getLong(21);
                mLocationData = DbLocation.deserialize(cursor.getBlob(9));
                mAuthorId = cursor.getString(4);
                mIsMyActivity = Boolean.valueOf(mAccount.isMyGaiaId(mAuthorId));
                boolean flag2;
                boolean flag3;
                boolean flag4;
                boolean flag5;
                boolean flag6;
                byte abyte4[];
                boolean flag7;
                boolean flag8;
                byte abyte5[];
                if(cursor.getInt(18) != 0)
                    flag2 = true;
                else
                    flag2 = false;
                mIsActivityResharable = flag2;
                if(cursor.getInt(15) == 0)
                    flag3 = true;
                else
                    flag3 = false;
                mIsActivityPublic = flag3;
                if(cursor.getInt(19) != 0)
                    flag4 = true;
                else
                    flag4 = false;
                mIsActivityMuted = flag4;
                if(!TextUtils.isEmpty(cursor.getString(24)))
                    flag5 = true;
                else
                    flag5 = false;
                mReshare = flag5;
                if(mViewerIsSquareAdmin && cursor.getInt(16) == 1 && (0x80000L & l1) != 0L)
                    flag6 = true;
                else
                    flag6 = false;
                mIsGraySpam = flag6;
                abyte4 = cursor.getBlob(8);
                if(abyte4 != null)
                {
                    DbPlusOneData dbplusonedata = DbPlusOneData.deserialize(abyte4);
                    if(dbplusonedata.getId() != null)
                        mPlusOnedByData = new Pair(dbplusonedata.getId(), Integer.valueOf(dbplusonedata.getCount()));
                }
                invalidateActionBar();
                if(cursor.getInt(17) != 0)
                    flag7 = true;
                else
                    flag7 = false;
                mCommentText.setEnabled(flag7);
                if(flag7)
                    mCommentText.setHint(R.string.compose_comment_hint);
                else
                    mCommentText.setHint(R.string.compose_comment_not_allowed_hint);
                if(mListParent != null)
                    ((ExpandingScrollView)mListParent.findViewById(R.id.list_expander)).setCanAnimate(true);
                if(TextUtils.isEmpty(cursor.getString(24)))
                    mEditableText = cursor.getString(23);
                else
                    mEditableText = cursor.getString(22);
                mCreationSource = cursor.getString(14);
                if((32768L & l1) != 0L)
                    mDeepLinkData = DbEmbedDeepLink.deserialize(cursor.getBlob(26));
                if((0x20000L & l1) != 0L)
                {
                    mAppInviteData = DbEmbedDeepLink.deserialize(cursor.getBlob(27));
                    mDeepLinkLabel = mAppInviteData.getLabelOrDefault(getSafeContext());
                }
                if(cursor.getInt(20) == 1 || ((StreamOneUpLoader)loader).needToRefreshComments())
                    refresh();
                if((l1 & 0x80000L) != 0L)
                    flag8 = true;
                else
                    flag8 = false;
                mIsSquarePost = flag8;
                abyte5 = cursor.getBlob(32);
                if(abyte5 != null)
                    mSquareId = DbEmbedSquare.deserialize(abyte5).getSquareId();
            }
            view1 = getView();
            Intent intent;
            final View view;
            if(view1 != null)
            {
                View view6 = view1.findViewById(R.id.gray_spam_bar);
                TextView textview = (TextView)view1.findViewById(R.id.gray_spam_bar_text);
                int i;
                int j;
                if(mIsGraySpam)
                    i = 0;
                else
                    i = 8;
                view6.setVisibility(i);
                if(mViewerIsSquareAdmin)
                    j = R.string.card_square_gray_spam_for_moderator;
                else
                    j = R.string.card_square_gray_spam;
                textview.setText(j);
            }
            view2 = getView();
            if(view2 != null && cursor != null && cursor.moveToFirst()) {
            	byte abyte0[] = cursor.getBlob(31);
                if(abyte0 == null) {
                	;
                	// TODO
                } else { 
                	DbEmbedHangout dbembedhangout = DbEmbedHangout.deserialize(abyte0);
                    String s8 = cursor.getString(5);
                    String s9 = cursor.getString(4);
                    View view5 = view2.findViewById(R.id.stage);
                    if(view5 == null)
                        view5 = ((ViewStub)view2.findViewById(R.id.stage_hangout)).inflate();
                    StreamOneUpHangoutView streamoneuphangoutview = (StreamOneUpHangoutView)view5.findViewById(R.id.hangout);
                    if(streamoneuphangoutview != null)
                    {
                        streamoneuphangoutview.bind(dbembedhangout, s8, s9, this);
                        ((ExpandingScrollView)mListParent.findViewById(R.id.list_expander)).setAlwaysExpanded(false);
                    }
                    
                    // L6
                }
            } else {
            	mAdapter.changeCursor(cursor);
                mAdapter.setFlaggedComments(mFlaggedComments);
                intent = getActivity().getIntent();
                if(!mMuteProcessed && intent.getBooleanExtra("mute", false))
                    getView().post(new Runnable() {

                        public final void run()
                        {
                            if(!isPaused())
                            {
                                mMuteProcessed = true;
                                mPendingRequestId = Integer.valueOf(EsService.muteActivity(getSafeContext(), mAccount, mActivityId, true));
                                showProgressDialog(17);
                            }
                        }
                    });
                if(intent.getBooleanExtra("enable_comment_action", false)) {
                    view = getView();
                    if(view != null && view.findViewById(R.id.stage) == null)
                        view.postDelayed(new Runnable() {

                            public final void run()
                            {
                                if(mCommentButton != null && mCommentText != null && mCommentText.isEnabled())
                                {
                                    mCommentText.requestFocus();
                                    SoftInput.show(mCommentText);
                                }
                                view.postDelayed(new Runnable() {

                                    public final void run()
                                    {
                                        mListView.setSelection(-1 + mAdapter.getCount());
                                    }

                                }, 250L);
                            }
                        }, 250L);
                }
            }
            
        }
        
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onLocationClick(DbLocation dblocation)
    {
        MapUtils.showActivityOnMap(getActivity(), dblocation);
    }

    public final void onMeasured(View view) {
    	if(view == mListView) {
    		mAdapter.setContainerHeight(mListView.getMeasuredHeight());
    		return;
    	}
        if(view == mFooter)
        {
            final int footerHeight = mFooter.getMeasuredHeight();
            (new Handler(Looper.getMainLooper())).post(new Runnable() {

                public final void run()
                {
                    View view1 = getView();
                    if(null == view1) {
                    	return;
                    }
                    
                    android.view.ViewGroup.MarginLayoutParams marginlayoutparams = (android.view.ViewGroup.MarginLayoutParams)mListParent.getLayoutParams();
                    marginlayoutparams.bottomMargin = footerHeight;
                    mListParent.setLayoutParams(marginlayoutparams);
                    if(view1.getMeasuredWidth() <= HostedStreamOneUpFragment.sMaxWidth)
                    {
                        View view2 = view1.findViewById(R.id.stage);
                        if(view2 != null && mLinkView != null)
                        {
                            boolean flag;
                            android.view.ViewGroup.MarginLayoutParams marginlayoutparams1;
                            int i;
                            int j;
                            if(getResources().getConfiguration().orientation == 2)
                                flag = true;
                            else
                                flag = false;
                            marginlayoutparams1 = (android.view.ViewGroup.MarginLayoutParams)view2.getLayoutParams();
                            i = footerHeight + HostedStreamOneUpFragment.sAvatarMarginTop;
                            if(flag)
                                j = HostedStreamOneUpFragment.sMinExposureLand;
                            else
                                j = HostedStreamOneUpFragment.sMinExposurePort;
                            marginlayoutparams1.bottomMargin = j + i;
                            view2.setLayoutParams(marginlayoutparams1);
                        }
                    }
                    mAdapter.setContainerHeight(mListView.getMeasuredHeight());
                }

            });
        }
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        Context context = getSafeContext();
        int i = menuitem.getItemId();
        boolean flag;
        if(i == R.id.feedback)
        {
            recordUserAction(OzActions.SETTINGS_FEEDBACK);
            MeetupFeedback.launch(getActivity());
            flag = true;
        } else
        if(i == R.id.show_location)
        {
            MapUtils.showActivityOnMap(getActivity(), mLocationData);
            flag = true;
        } else
        if(i == R.id.edit)
        {
            startActivity(Intents.getEditPostActivityIntent(context, mAccount, mActivityId, mEditableText, mReshare));
            flag = true;
        } else
        if(i == R.id.delete_post)
        {
            Bundle bundle3 = EsAnalyticsData.createExtras("extra_activity_id", mActivityId);
            recordUserAction(OzActions.ONE_UP_REMOVE_ACTIVITY, bundle3);
            AlertFragmentDialog alertfragmentdialog2 = AlertFragmentDialog.newInstance(getString(R.string.menu_remove_post), getString(R.string.post_delete_question), getString(R.string.ok), getString(R.string.cancel));
            alertfragmentdialog2.setTargetFragment(this, 0);
            alertfragmentdialog2.show(getFragmentManager(), "hsouf_delete_activity");
            flag = true;
        } else
        if(i == R.id.plus_oned_by)
        {
            if(mPlusOnedByData != null)
                showPlusOnePeople((String)mPlusOnedByData.first, ((Integer)mPlusOnedByData.second).intValue());
            flag = true;
        } else
        if(i == R.id.report_abuse)
        {
            Bundle bundle2 = EsAnalyticsData.createExtras("extra_activity_id", mActivityId);
            recordUserAction(OzActions.ONE_UP_REPORT_ABUSE_ACTIVITY, bundle2);
            AlertFragmentDialog alertfragmentdialog1 = AlertFragmentDialog.newInstance(getString(R.string.menu_report_abuse), getString(R.string.post_report_question), getString(R.string.ok), getString(R.string.cancel));
            alertfragmentdialog1.setTargetFragment(this, 0);
            alertfragmentdialog1.getArguments().putString("activity_id", mActivityId);
            alertfragmentdialog1.show(getFragmentManager(), "hsouf_report_activity");
            flag = true;
        } else
        if(i == R.id.mute_post)
        {
            Bundle bundle1 = EsAnalyticsData.createExtras("extra_activity_id", mActivityId);
            recordUserAction(OzActions.ONE_UP_MUTE_ACTIVITY, bundle1);
            int j;
            String s1;
            int k;
            AlertFragmentDialog alertfragmentdialog;
            if(mIsActivityMuted)
                j = R.string.menu_unmute_post;
            else
                j = R.string.menu_mute_post;
            s1 = getString(j);
            if(mIsActivityMuted)
                k = R.string.post_unmute_question;
            else
                k = R.string.post_mute_question;
            alertfragmentdialog = AlertFragmentDialog.newInstance(s1, getString(k), getString(R.string.ok), getString(R.string.cancel));
            alertfragmentdialog.setTargetFragment(this, 0);
            alertfragmentdialog.getArguments().putString("activity_id", mActivityId);
            alertfragmentdialog.show(getFragmentManager(), "hsouf_mute_activity");
            flag = true;
        } else
        if(i == R.id.photo_details)
        {
            if(mBackgroundRef != null && mBackgroundRef.getPhotoId() != 0L)
            {
                Intents.PhotoViewIntentBuilder photoviewintentbuilder = Intents.newPhotoViewActivityIntentBuilder(getSafeContext());
                String s = mBackgroundRef.getOwnerGaiaId();
                photoviewintentbuilder.setAccount(mAccount).setPhotoRef(mBackgroundRef).setGaiaId(s).setAlbumId(mAlbumId).setRefreshAlbumId(mAlbumId).setDisableComments(Boolean.valueOf(mIsSquarePost));
                Bundle bundle = EsAnalyticsData.createExtras("extra_gaia_id", s);
                recordUserAction(OzActions.ONE_UP_SELECT_PHOTO, bundle);
                startActivity(photoviewintentbuilder.build());
            }
            flag = true;
        } else
        {
            flag = false;
        }
        return flag;
    }

    public final void onPause()
    {
        super.onPause();
        if(mImageView != null)
        {
            PhotoHeaderView _tmp = mImageView;
            PhotoHeaderView.onStop();
        }
        if(mLinkView != null)
        {
            OneUpLinkView _tmp1 = mLinkView;
            OneUpLinkView.onStop();
        }
        if(mListView != null)
        {
            for(int i = -1 + mListView.getChildCount(); i >= 0; i--)
                if(mListView.getChildAt(i) instanceof OneUpBaseView)
                    OneUpBaseView.onStop();

        }
        EsService.unregisterListener(mServiceListener);
    }

    public final void onPlaceClick(String s)
    {
        if(!TextUtils.isEmpty(s))
            startActivity(Intents.getProfileActivityByGaiaIdIntent(getActivity(), mAccount, s, null));
    }

    public final void onPlusOne(String s, DbPlusOneData dbplusonedata)
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

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        hostactionbar.showRefreshButton();
        hostactionbar.showProgressIndicator();
        if(mIsActivityResharable)
            hostactionbar.showActionButton(0, R.drawable.ic_actionbar_reshare, R.string.menu_reshare_post);
        if(mActionBarAnimator == null)
            mActionBarAnimator = new PhotoOneUpAnimationController(hostactionbar, true, true);
        if(mUpdateActionBar)
        {
            adjustActionBarMargins(hostactionbar, true);
            hostactionbar.setVisibility(8);
        } else
        {
            hostactionbar.setVisibility(0);
        }
        updateProgressIndicator();
    }

    public final void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if(mLocationData != null)
            menu.findItem(R.id.show_location).setVisible(true);
        if(mIsMyActivity != null)
            if(mIsMyActivity.booleanValue())
            {
                menu.findItem(R.id.edit).setVisible(true);
                menu.findItem(R.id.delete_post).setVisible(true);
            } else
            {
                menu.findItem(R.id.report_abuse).setVisible(true);
                MenuItem menuitem = menu.findItem(R.id.mute_post);
                menuitem.setVisible(true);
                if(mIsActivityMuted)
                    menuitem.setTitle(R.string.menu_unmute_post);
                else
                    menuitem.setTitle(R.string.menu_mute_post);
            }
        if(mPlusOnedByData != null)
            menu.findItem(R.id.plus_oned_by).setVisible(true);
        menu.findItem(R.id.feedback).setVisible(true);
        if(mBackgroundRef != null && mBackgroundRef.getPhotoId() != 0L)
            menu.findItem(R.id.photo_details).setVisible(true);
    }

    public final void onResume()
    {
        super.onResume();
        if(mImageView != null)
        {
            PhotoHeaderView _tmp = mImageView;
            PhotoHeaderView.onStart();
        }
        if(mLinkView != null)
        {
            OneUpLinkView _tmp1 = mLinkView;
            OneUpLinkView.onStart();
        }
        if(mListView != null)
        {
            for(int i = -1 + mListView.getChildCount(); i >= 0; i--)
                if(mListView.getChildAt(i) instanceof OneUpBaseView)
                    OneUpBaseView.onStart();

        }
        EsService.registerListener(mServiceListener);
        mCallback.addScreenListener(this);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingRequestId.intValue());
            mServiceListener.handleServiceCallback(mPendingRequestId.intValue(), serviceresult);
        }
        if(mActivityRequestId != null && !EsService.isRequestPending(mActivityRequestId.intValue()))
        {
            EsService.removeResult(mActivityRequestId.intValue());
            mActivityRequestId = null;
        }
        updateProgressIndicator();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mPendingRequestId != null)
            bundle.putInt("pending_request_id", mPendingRequestId.intValue());
        if(mActivityRequestId != null)
            bundle.putInt("activity_request_id", mActivityRequestId.intValue());
        if(mAudienceData != null)
            bundle.putParcelable("audience_data", mAudienceData);
        if(!mFlaggedComments.isEmpty())
        {
            String as[] = new String[mFlaggedComments.size()];
            mFlaggedComments.toArray(as);
            bundle.putStringArray("flagged_comments", as);
        }
        bundle.putInt("operation_type", mOperationType);
        bundle.putBoolean("mute_processed", mMuteProcessed);
        bundle.putBoolean("read_processed", mReadProcessed);
        bundle.putString("source_package_name", mSourcePackageName);
        bundle.putString("source_author_id", mSourceAuthorId);
        bundle.putBoolean("get_activity_complete", mGetActivityComplete);
        bundle.putBoolean("stage_media_loaded", mStageMediaLoaded);
        bundle.putBoolean("full_screen", mFullScreen);
    }

    protected final void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mAutoPlay = bundle.getBoolean("auto_play_music", false);
    }

    public final void onSkyjamBuyClick(String s)
    {
        FragmentActivity fragmentactivity = getActivity();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(0x80000);
        intent.setData(Uri.parse(s));
        intent.setPackage("com.android.vending");
        if(fragmentactivity.getPackageManager().resolveActivity(intent, 0) == null)
            intent.setPackage(null);
        fragmentactivity.startActivity(intent);
    }

    public final void onSkyjamListenClick(String s)
    {
        FragmentActivity fragmentactivity = getActivity();
        Intent intent = new Intent("com.google.android.music.SHARED_PLAY");
        intent.putExtra("url", s);
        intent.putExtra("authAccount", EsService.getActiveAccount(fragmentactivity).getName());
        intent.putExtra("accountType", AccountsUtil.ACCOUNT_TYPE);
        intent.setPackage("com.google.android.music");
        if(fragmentactivity.getPackageManager().resolveActivity(intent, 0) == null)
        {
            intent = new Intent("android.intent.action.VIEW");
            intent.addFlags(0x80000);
            intent.setData(Uri.parse("market://details?id=com.google.android.music"));
        }
        fragmentactivity.startActivity(intent);
    }

    public final void onSourceAppContentClick(String s, List list, String s1, String s2, String s3)
    {
        launchDeepLink(list, s1, s2, s3, false);
    }

    public final void onSpanClick(URLSpan urlspan)
    {
        onUrlClick(urlspan.getURL());
    }

    public final void onSquareClick(String s, String s1)
    {
        startActivity(Intents.getSquareStreamActivityIntent(getActivity(), mAccount, s, s1, null));
    }

    public final void onUserImageClick(String s, String s1)
    {
        Context context = getSafeContext();
        Bundle bundle = EsAnalyticsData.createExtras("extra_gaia_id", s);
        recordUserAction(OzActions.ONE_UP_SELECT_AUTHOR, bundle);
        startActivity(Intents.getProfileActivityByGaiaIdIntent(context, mAccount, s, null));
    }

    public final void refresh()
    {
        super.refresh();
        mGetActivityComplete = false;
        if(mActivityRequestId == null)
            mActivityRequestId = Integer.valueOf(EsService.getActivity(getSafeContext(), mAccount, mActivityId, mSquareId));
        updateProgressIndicator();
    }

    protected final void updateProgressIndicator()
    {
        HostActionBar hostactionbar = getActionBar();
        if(hostactionbar != null)
            if(mActivityRequestId != null || mAdapter != null && mAdapter.getCursor() == null)
                hostactionbar.showProgressIndicator();
            else
                hostactionbar.hideProgressIndicator();
        if(mAdapter != null)
        {
            StreamOneUpAdapter streamoneupadapter = mAdapter;
            boolean flag;
            if(mActivityRequestId != null)
                flag = true;
            else
                flag = false;
            streamoneupadapter.setLoading(flag);
        }
    }

	private static final class MyTextWatcher implements TextWatcher {

		private final View mView;

		MyTextWatcher(View view) {
			mView = view;
		}

		public final void afterTextChanged(Editable editable) {
			View view = mView;
			boolean flag;
			if (TextUtils.getTrimmedLength(editable) > 0)
				flag = true;
			else
				flag = false;
			view.setEnabled(flag);
		}

		public final void beforeTextChanged(CharSequence charsequence, int i,
				int j, int k) {
		}

		public final void onTextChanged(CharSequence charsequence, int i,
				int j, int k) {
		}
	}

	private final class ServiceListener extends EsServiceListener {

		private boolean handleServiceCallback(int i, ServiceResult serviceresult) {
			Integer integer = mPendingRequestId;
			boolean flag = false;
			if (integer == null) {
				return false;
			}

			int j;
			j = mPendingRequestId.intValue();
			flag = false;
			if (j != i) {
				return false;
			}

			mPendingRequestId = null;
			DialogFragment dialogfragment = (DialogFragment) HostedStreamOneUpFragment.this.getFragmentManager().findFragmentByTag("hsouf_pending");
			if (serviceresult == null || !serviceresult.hasError()) {
				switch (mOperationType) {
				case 16:
					getActivity().finish();
					break;
				case 17:
					if (mIsActivityMuted) {
						flag = true;
					} else {
						getActivity().finish();
					}
					break;
				case 18:
					getActivity().finish();
					break;
				default:
					flag = true;
					break;
				}
			} else {
				int k = 0;

				switch (mOperationType) {
				case 16:
					k = R.string.remove_post_error;
					break;
				case 17:
					k = R.string.mute_activity_error;
					break;
				case 18:
					k = R.string.report_activity_error;
					break;
				case 19:
					k = R.string.reshare_post_error;
					break;
				case 32:
					k = R.string.comment_post_error;
					break;
				case 33:
					k = R.string.comment_delete_error;
					break;
				case 34:
					k = R.string.comment_moderate_error;
					break;
				case 48:
					k = R.string.get_acl_error;
					break;
				default:
					k = R.string.operation_failed;
					break;
				}
				Toast.makeText(getSafeContext(), k, 0).show();
				flag = false;
			}
			return flag;
		}

		public final void onCreateComment(int i, ServiceResult serviceresult) {
			if (serviceresult.hasError()) {
				Exception exception = serviceresult.getException();
				if (!(exception instanceof OzServerException)
						|| ((OzServerException) exception).getErrorCode() != 14) {
					handleServiceCallback(i, serviceresult);
					return;
				}
				DialogFragment dialogfragment = (DialogFragment) HostedStreamOneUpFragment.this.getFragmentManager().findFragmentByTag("hsouf_pending");
				AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog
						.newInstance(
								getString(R.string.post_not_sent_title),
								getString(R.string.post_restricted_mention_error),
								getString(R.string.ok),
								null);
				alertfragmentdialog.setTargetFragment(getTargetFragment(), 0);
				alertfragmentdialog.show(getFragmentManager(),
						"StreamPostRestrictionsNotSupported");
			} else {
				if (mCommentText != null)
					mCommentText.setText(null);
				handleServiceCallback(i, serviceresult);
			}
		}

		public final void onCreatePostPlusOne(ServiceResult serviceresult) {
			if (serviceresult != null && serviceresult.hasError())
				Toast.makeText(getSafeContext(),
						R.string.plusone_error, 0)
						.show();
		}

		public final void onDeleteActivity(int i, ServiceResult serviceresult) {
			handleServiceCallback(i, serviceresult);
		}

		public final void onDeleteComment(int i, ServiceResult serviceresult) {
			handleServiceCallback(i, serviceresult);
		}

		public final void onDeletePostPlusOne(ServiceResult serviceresult) {
			if (serviceresult != null && serviceresult.hasError())
				Toast.makeText(
						getSafeContext(),
						R.string.delete_plusone_error,
						0).show();
		}

		public final void onEditActivity(int i, ServiceResult serviceresult) {
			handleServiceCallback(i, serviceresult);
		}

		public final void onEditComment(int i, ServiceResult serviceresult) {
			handleServiceCallback(i, serviceresult);
		}

		public final void onGetActivity(int i, String s,
				ServiceResult serviceresult) {

			if (null == s || !s.equals(mActivityId)) {
				return;
			}
			if (mActivityRequestId != null
					&& i == mActivityRequestId.intValue()) {
				mActivityRequestId = null;
				updateProgressIndicator();
			}
			mGetActivityComplete = true;
			if (serviceresult.hasError() && mActivityDataNotFound)
				Toast.makeText(
						getSafeContext(),
						getText(R.string.comments_activity_not_found),
						0).show();
			else if (mAdapter != null && !mAdapter.isEmpty()) {
				View view = getView();
				updateLoadingSpinner(view);
			}
		}

		public final void onGetActivityAudience(int i,
				AudienceData audiencedata, ServiceResult serviceresult) {
			if (!serviceresult.hasError() && audiencedata != null) {
				mAudienceData = audiencedata;
				showAudience(audiencedata);
			}
			handleServiceCallback(i, serviceresult);
		}

		public final void onModerateComment(int i, String s, boolean flag,
				ServiceResult serviceresult) {
			if (handleServiceCallback(i, serviceresult))
				if (flag)
					mAdapter.removeFlaggedComment(s);
				else
					mAdapter.addFlaggedComment(s);
		}

		public final void onMuteActivity(int i, ServiceResult serviceresult) {
			handleServiceCallback(i, serviceresult);
		}

		public final void onPlusOneComment(boolean flag,
				ServiceResult serviceresult) {
			if (serviceresult != null && serviceresult.hasError()) {
				Context context = getSafeContext();
				int i;
				if (flag)
					i = R.string.plusone_error;
				else
					i = R.string.delete_plusone_error;
				Toast.makeText(context, i, 0).show();
			}
		}

		public final void onReportActivity(int i, ServiceResult serviceresult) {
			handleServiceCallback(i, serviceresult);
		}

		public final void onReshareActivity(int i, ServiceResult serviceresult) {
			handleServiceCallback(i, serviceresult);
		}
	}

}
