/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import WriteReviewOperation.MediaRef;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.MeetupFeedback;
import com.galaxy.meetup.client.android.PhotoOneUpAnimationController;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.ApiUtils;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.DbPlusOneData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.activity.PhotoOneUpActivity.OnMenuItemListener;
import com.galaxy.meetup.client.android.ui.activity.PhotoOneUpActivity.OnScreenListener;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.view.ExpandingScrollView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.LinearLayoutWithLayoutNotifications;
import com.galaxy.meetup.client.android.ui.view.LinearLayoutWithLayoutNotifications.LayoutListener;
import com.galaxy.meetup.client.android.ui.view.MentionMultiAutoCompleteTextView;
import com.galaxy.meetup.client.android.ui.view.OneUpBaseView;
import com.galaxy.meetup.client.android.ui.view.OneUpBaseView.OnMeasuredListener;
import com.galaxy.meetup.client.android.ui.view.OneUpListener;
import com.galaxy.meetup.client.android.ui.view.OneUpTouchHandler;
import com.galaxy.meetup.client.android.ui.view.PhotoHeaderView;
import com.galaxy.meetup.client.android.ui.view.PhotoHeaderView.OnImageListener;
import com.galaxy.meetup.client.android.ui.view.PhotoTagScroller;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpCommentView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpListView;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.FIFEUtil;
import com.galaxy.meetup.client.util.ImageProxyUtil;
import com.galaxy.meetup.client.util.MediaStoreUtils;

/**
 * 
 * @author sihai
 *
 */
public class PhotoOneUpFragment extends HostedFragment implements
		LoaderCallbacks, OnClickListener, OnItemClickListener,
		AlertDialogListener, OnMenuItemListener, OnScreenListener,
		LayoutListener, OnMeasuredListener, OneUpListener, OnImageListener {

	private static int sActionBarHeight;
    private static int sMaxWidth;
    private static boolean sResourcesLoaded;
    private EsAccount mAccount;
    private PhotoOneUpAnimationController mActionBarAnimator;
    private PhotoOneUpAdapter mAdapter;
    private String mAlbumName;
    private boolean mAllowPlusOne;
    private AudienceData mAudienceData;
    private String mAuthkey;
    private boolean mAutoPlay;
    private boolean mAutoRefreshDone;
    private int mBackgroundDesiredHeight;
    private int mBackgroundDesiredWidth;
    private MediaRef mBackgroundRef;
    private PhotoHeaderView mBackgroundView;
    private PhotoOneUpCallbacks mCallback;
    private View mCommentButton;
    private MentionMultiAutoCompleteTextView mCommentText;
    private boolean mDisableComments;
    private Boolean mDownloadable;
    private HashSet mFlaggedComments;
    private LinearLayoutWithLayoutNotifications mFooter;
    private PhotoOneUpAnimationController mFooterAnimator;
    private boolean mFullScreen;
    private boolean mIsPlaceholder;
    private PhotoOneUpAnimationController mListAnimator;
    private View mListParent;
    private StreamOneUpListView mListView;
    private int mOperationType;
    private byte mPendingBytes[];
    private Integer mPendingRequestId;
    private boolean mReadProcessed;
    private Integer mRefreshRequestId;
    private final ServiceListener mServiceListener = new ServiceListener();
    private PhotoOneUpAnimationController mTagBarAnimator;
    private View mTagLayout;
    private PhotoTagScroller mTagScroll;
    private TextWatcher mTextWatcher;
    private String mTitle;
    private OneUpTouchHandler mTouchHandler;
    private boolean mUpdateActionBar;
    
    
    public PhotoOneUpFragment()
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

    private void doReportComment(String s, boolean flag, boolean flag1)
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
        alertfragmentdialog.show(getFragmentManager(), "pouf_report_comment");
    }

    private void showProgressDialog(int i)
    {
        showProgressDialog(i, getString(R.string.post_operation_pending));
    }

    private void showProgressDialog(int i, String s)
    {
        mOperationType = i;
        ProgressFragmentDialog.newInstance(null, s, false).show(getFragmentManager(), "pouf_pending");
    }

    private void updateProgressIndicator(HostActionBar hostactionbar)
    {
        if(hostactionbar != null) {
        	if(mRefreshRequestId != null || mAdapter != null && mAdapter.getCursor() == null)
                hostactionbar.showProgressIndicator();
            else
                hostactionbar.hideProgressIndicator();
            if(mAdapter != null)
            {
                PhotoOneUpAdapter photooneupadapter = mAdapter;
                boolean flag;
                if(mRefreshRequestId != null)
                    flag = true;
                else
                    flag = false;
                photooneupadapter.setLoading(flag);
            }
        }
    }

    public final void doDownload(Context context, boolean flag)
    {
        char c = '\u0800';
        if(null == mAdapter) {
        	return;
        }
        String s1;
        String s = mBackgroundRef.getUrl();
        if(!FIFEUtil.isFifeHostedUrl(s)) {
        	if(flag)
                c = '\uFFFF';
            s1 = ImageProxyUtil.setImageUrlSize(c, s);
        } else { 
            if(flag)
                s1 = FIFEUtil.setImageUrlOptions("d", s).toString();
            else
                s1 = FIFEUtil.setImageUrlSize(c, s, false);
        }
        
        if(s1 != null)
        {
            if(EsLog.isLoggable("StreamOneUp", 3))
                Log.d("StreamOneUp", (new StringBuilder("Downloading image from: ")).append(s1).toString());
            mPendingRequestId = Integer.valueOf(EsService.savePhoto(context, mAccount, s1, flag, mAlbumName));
            showProgressDialog(19, getString(R.string.download_photo_pending));
        } else
        {
            Toast.makeText(context, getResources().getString(R.string.download_photo_error), 1).show();
        }
    }

    public final void enableImageTransforms(boolean flag)
    {
        mBackgroundView.enableImageTransforms(flag);
    }

    public final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTO;
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
    	if(1 == i) {
    		if(j == -1)
                mPendingBytes = intent.getByteArrayExtra("data");
    	}
        
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if(activity instanceof PhotoOneUpCallbacks)
        {
            mCallback = (PhotoOneUpCallbacks)activity;
            return;
        } else
        {
            throw new IllegalArgumentException("Activity must implement PhotoOneUpCallbacks");
        }
    }

    public void onClick(View view)
    {
        int i = view.getId();
        if(R.id.footer_post_button == i) {
        	if(mCommentText.getText().toString().trim().length() > 0)
            {
                if(mPendingRequestId == null)
                {
                    recordUserAction(OzActions.ONE_UP_POST_COMMENT);
                    Editable editable = mCommentText.getText();
                    Context context = getSafeContext();
                    //mAccount;
                    String s3 = ApiUtils.buildPostableString(editable);
                    mPendingRequestId = Integer.valueOf(EsService.createPhotoComment(context, mAccount, mBackgroundRef.getOwnerGaiaId(), mBackgroundRef.getPhotoId(), s3, mAuthkey));
                    showProgressDialog(32);
                }
            } else
            {
                mCommentButton.setEnabled(false);
            }
        } else if(i == R.id.background)
        {
            if(mBackgroundView.isVideo())
            {
                if(mBackgroundView.isVideoReady())
                {
                    startActivity(Intents.getVideoViewActivityIntent(getActivity(), mAccount, mBackgroundRef.getOwnerGaiaId(), mBackgroundRef.getPhotoId(), mBackgroundView.getVideoData()));
                } else
                {
                    String s2 = getString(R.string.photo_view_video_not_ready);
                    Toast.makeText(getActivity(), s2, 1).show();
                }
            } else
            if(mBackgroundView.isPanorama())
                startActivity(Intents.getViewPanoramaActivityIntent(getActivity(), mAccount, mBackgroundRef));
            else
                mCallback.toggleFullScreen();
        } else
        if(i == R.id.tag_approve)
        {
            Long long2 = (Long)view.getTag(R.id.tag_shape_id);
            if(((Boolean)view.getTag(R.id.tag_is_suggestion)).booleanValue())
            {
                String s1 = (String)view.getTag(R.id.tag_gaiaid);
                mPendingRequestId = Integer.valueOf(EsService.suggestedTagApproval(getActivity(), mAccount, s1, mBackgroundRef.getOwnerGaiaId(), Long.toString(mBackgroundRef.getPhotoId()), Long.toString(long2.longValue()), true));
            } else
            {
                mPendingRequestId = Integer.valueOf(EsService.nameTagApproval(getActivity(), mAccount, mBackgroundRef.getOwnerGaiaId(), Long.valueOf(mBackgroundRef.getPhotoId()), long2, true));
            }
            showProgressDialog(49);
        } else
        if(i == R.id.tag_deny)
        {
            Long long1 = (Long)view.getTag(R.id.tag_shape_id);
            if(((Boolean)view.getTag(R.id.tag_is_suggestion)).booleanValue())
            {
                String s = (String)view.getTag(R.id.tag_gaiaid);
                mPendingRequestId = Integer.valueOf(EsService.suggestedTagApproval(getActivity(), mAccount, s, mBackgroundRef.getOwnerGaiaId(), Long.toString(mBackgroundRef.getPhotoId()), Long.toString(long1.longValue()), false));
            } else
            {
                mPendingRequestId = Integer.valueOf(EsService.nameTagApproval(getActivity(), mAccount, mBackgroundRef.getOwnerGaiaId(), Long.valueOf(mBackgroundRef.getPhotoId()), long1, false));
            }
            showProgressDialog(50);
        }
        
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mFlaggedComments = new HashSet();
        Bundle bundle1 = getArguments();
        mAccount = (EsAccount)bundle1.getParcelable("account");
        mBackgroundDesiredWidth = bundle1.getInt("photo_width", -1);
        mBackgroundDesiredHeight = bundle1.getInt("photo_height", -1);
        mAllowPlusOne = bundle1.getBoolean("allow_plusone", true);
        mDisableComments = bundle1.getBoolean("disable_photo_comments");
        if(bundle != null)
        {
            if(bundle.containsKey("pending_request_id"))
                mPendingRequestId = Integer.valueOf(bundle.getInt("pending_request_id"));
            if(bundle.containsKey("refresh_request_id"))
                mRefreshRequestId = Integer.valueOf(bundle.getInt("refresh_request_id"));
            mAudienceData = (AudienceData)bundle.getParcelable("audience_data");
            String as[] = bundle.getStringArray("flagged_comments");
            if(as != null)
                mFlaggedComments.addAll(Arrays.asList(as));
            mOperationType = bundle.getInt("operation_type", 0);
            mReadProcessed = bundle.getBoolean("read_processed", false);
            mFullScreen = bundle.getBoolean("full_screen");
            mAutoPlay = false;
            mBackgroundRef = (MediaRef)bundle.getParcelable("photo_ref");
            mIsPlaceholder = bundle.getBoolean("is_placeholder");
        } else
        {
            mBackgroundRef = (MediaRef)bundle1.getParcelable("photo_ref");
            mIsPlaceholder = bundle1.getBoolean("is_placeholder");
            if(bundle1.getBoolean("refresh", false) || bundle1.getLong("force_load_id", 0L) == mBackgroundRef.getPhotoId())
                refresh();
            mAuthkey = bundle1.getString("auth_key");
        }
        mUpdateActionBar = mFullScreen;
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
    	Loader loader = null;
    	if(519746381 == i) {
    		loader = new PhotoOneUpLoader(getSafeContext(), mAccount, mBackgroundRef.getPhotoId(), mBackgroundRef.getOwnerGaiaId(), mBackgroundRef.getUrl(), mDisableComments);
    	} else if(533919674 == i) {
    		Uri uri = EsProvider.appendAccountParameter(ContentUris.withAppendedId(EsProvider.PHOTO_SHAPES_BY_PHOTO_ID_URI, mBackgroundRef.getPhotoId()), mAccount);
    		loader = new EsCursorLoader(getActivity(), uri, PhotoTagScroller.PhotoShapeQuery.PROJECTION, null, null, "shape_id");
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
            sActionBarHeight = resources.getDimensionPixelOffset(R.dimen.host_action_bar_height);
            sResourcesLoaded = true;
        }
        View view = layoutinflater.inflate(R.layout.photo_one_up_fragment, viewgroup, false);
        mListParent = view.findViewById(R.id.list_parent);
        mListParent.findViewById(R.id.list_expander).setOnClickListener(this);
        mListAnimator = new PhotoOneUpAnimationController(mListParent, false, false);
        View view1 = mListParent;
        byte byte1;
        String s;
        View view2;
        LinearLayoutWithLayoutNotifications linearlayoutwithlayoutnotifications;
        View view4;
        boolean flag;
        if(mFullScreen)
            byte1 = byte0;
        else
            byte1 = 0;
        view1.setVisibility(byte1);
        mListView = (StreamOneUpListView)view.findViewById(0x102000a);
        mAdapter = new PhotoOneUpAdapter(context, null, this, mListView);
        mListView.setMaxWidth(sMaxWidth);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnMeasureListener(this);
        if(mBackgroundRef == null)
            s = null;
        else
            s = mBackgroundRef.getUrl();
        view2 = view.findViewById(R.id.stage);
        if(s == null)
        {
            if(view2 != null)
                view2.setVisibility(byte0);
        } else
        if(view2 == null)
        {
            View view3 = ((ViewStub)view.findViewById(R.id.stage_media)).inflate();
            view3.findViewById(R.id.loading).setVisibility(0);
            mBackgroundView = (PhotoHeaderView)view3.findViewById(R.id.background);
            mBackgroundView.init(mBackgroundRef, mIsPlaceholder);
            mBackgroundView.setOnClickListener(this);
            mBackgroundView.setOnImageListener(this);
            mBackgroundView.enableImageTransforms(true);
            ((ExpandingScrollView)mListParent.findViewById(R.id.list_expander)).setAlwaysExpanded(false);
            view3.invalidate();
        }
        mTagLayout = view.findViewById(R.id.one_up_tag_layout);
        mTagLayout.setOnClickListener(this);
        mTagScroll = (PhotoTagScroller)view.findViewById(R.id.one_up_tag_list);
        mTagScroll.setHeaderView(mBackgroundView);
        mTagScroll.setExternalOnClickListener(this);
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
        mCommentText.init(this, mAccount, null, null);
        mCommentButton = view.findViewById(R.id.footer_post_button);
        mCommentButton.setOnClickListener(this);
        view4 = mCommentButton;
        if(mCommentText.getText().length() > 0)
            flag = true;
        else
            flag = false;
        view4.setEnabled(flag);
        mTextWatcher = new MyTextWatcher(mCommentButton, (byte)0);
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
        mTouchHandler = (OneUpTouchHandler)view.findViewById(R.id.touch_handler);
        mTouchHandler.setBackground(mBackgroundView);
        mTouchHandler.setScrollView(mListParent);
        mTouchHandler.setTagLayout(mTagLayout);
        mTouchHandler.setActionBar(getActionBar());
        getLoaderManager().initLoader(0x1efab34d, null, this);
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
        mBackgroundView.destroy();
        mBackgroundView = null;
        super.onDestroyView();
    }

    public final void onDetach()
    {
        mCallback = null;
        super.onDetach();
    }

    public final void onDialogCanceled(String s)
    {
    }

    public final void onDialogListClick(int i, Bundle bundle)
    {
        ArrayList arraylist = bundle.getIntegerArrayList("comment_action");
        if(null == arraylist) {
        	if(EsLog.isLoggable("StreamOneUp", 5))
                Log.w("StreamOneUp", "No actions for comment option dialog");
        	return;
        }
        
        if(i >= arraylist.size()) {
        	if(EsLog.isLoggable("StreamOneUp", 5))
                Log.w("StreamOneUp", "Option selected outside the action list");
        	return;
        }
        
        String s = bundle.getString("comment_id");
        String s1 = bundle.getString("comment_content");
        boolean flag = bundle.getBoolean("plus_one_by_me");
        long l = bundle.getLong("photo_id");
        switch(((Integer)arraylist.get(i)).intValue())
        {
        case 33: // '!'
            Bundle bundle2 = EsAnalyticsData.createExtras("extra_comment_id", s);
            recordUserAction(OzActions.ONE_UP_DELETE_COMMENT, bundle2);
            AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.menu_delete_comment), getString(R.string.comment_delete_question), getString(R.string.ok), getString(R.string.cancel));
            alertfragmentdialog.setTargetFragment(this, 0);
            alertfragmentdialog.getArguments().putString("comment_id", s);
            alertfragmentdialog.show(getFragmentManager(), "pouf_delete_comment");
            break;

        case 38: // '&'
            Context context = getSafeContext();
            EsAccount esaccount = mAccount;
            boolean flag1;
            if(!flag)
                flag1 = true;
            else
                flag1 = false;
            EsService.plusOneComment(context, esaccount, null, l, s, null, flag1);
            break;

        case 37: // '%'
            Bundle bundle1 = EsAnalyticsData.createExtras("extra_comment_id", s);
            recordUserAction(OzActions.ONE_UP_EDIT_COMMENT, bundle1);
            startActivity(Intents.getEditCommentActivityIntent(getSafeContext(), mAccount, null, s, s1, Long.valueOf(mBackgroundRef.getPhotoId()), mBackgroundRef.getOwnerGaiaId()));
            break;

        case 34: // '"'
            doReportComment(s, false, false);
            break;

        case 35: // '#'
            doReportComment(s, false, true);
            break;

        case 36: // '$'
            doReportComment(s, true, false);
            break;
        }
    }

    public final void onDialogNegativeClick(String s)
    {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s)
    {
        long l;
        String s1;
        l = mBackgroundRef.getPhotoId();
        s1 = mBackgroundRef.getOwnerGaiaId();
        
        if("pouf_delete_photo".equals(s)) {
        	ArrayList arraylist = new ArrayList(1);
            arraylist.add(Long.valueOf(l));
            FragmentActivity fragmentactivity = getActivity();
            mPendingRequestId = Integer.valueOf(EsService.deletePhotos(fragmentactivity, mAccount, s1, arraylist));
            showProgressDialog(16, fragmentactivity.getResources().getQuantityString(R.plurals.delete_photo_pending, 1));
        } else if("pouf_report_photo".equals(s)) {
        	 mPendingRequestId = Integer.valueOf(EsService.reportPhotoAbuse(getActivity(), mAccount, l, s1));
             showProgressDialog(17);
        } else if("pouf_delete_comment".equals(s)) {
                mPendingRequestId = Integer.valueOf(EsService.deletePhotoComment(getActivity(), mAccount, Long.valueOf(l), bundle.getString("comment_id")));
                showProgressDialog(33);
        } else if("pouf_report_comment".equals(s)) {
                mPendingRequestId = Integer.valueOf(EsService.reportPhotoComment(getActivity(), mAccount, Long.valueOf(l), bundle.getString("comment_id"), bundle.getBoolean("delete", false), bundle.getBoolean("is_undo", false)));
                showProgressDialog(34);
        } else if("pouf_delete_tag".equals(s)) {
            Long long1 = mTagScroll.getMyApprovedShapeId();
            if(long1 != null)
            {
                mPendingRequestId = Integer.valueOf(EsService.nameTagApproval(getActivity(), mAccount, s1, Long.valueOf(l), long1, false));
                showProgressDialog(48);
            }
        }
        
    }

    public final void onFullScreenChanged(boolean flag)
    {
        if(mCallback.isFragmentActive(this)) {
        	mFullScreen = flag;
            if(!mFullScreen)
            {
                if(mUpdateActionBar)
                    adjustActionBarMargins(getActionBar(), false);
                mUpdateActionBar = false;
            }
            if(mActionBarAnimator != null)
                mActionBarAnimator.animate(mFullScreen);
            if(mListAnimator != null)
                mListAnimator.animate(mFullScreen);
            if(mFooterAnimator != null)
                mFooterAnimator.animate(mFullScreen);
            if(mTagBarAnimator != null)
                mTagBarAnimator.animate(mFullScreen);
            if(!mFullScreen)
                mBackgroundView.resetTransformations();
        }
    }

    public final void onImageLoadFinished(PhotoHeaderView photoheaderview)
    {
        getView().findViewById(R.id.loading).setVisibility(8);
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

    public final boolean onInterceptMoveLeft()
    {
        if(!mCallback.isFragmentActive(this)){
        	return false;
        }
        
        if(!mFullScreen)
        {
            View view = mTouchHandler.getTargetView();
            View view1 = mTagLayout;
            if(view != view1)
                return false;
        }
        
        return true;
    }

    public final boolean onInterceptMoveRight()
    {
        if(!mCallback.isFragmentActive(this)) {
        	return false;
        }
        
        if(!mFullScreen)
        {
            View view = mTouchHandler.getTargetView();
            View view1 = mTagLayout;
            if(view != view1)
                return false;
        }
        return true;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        if(!(view instanceof StreamOneUpCommentView)) {
        	if(Log.isLoggable("StreamOneUp", 3))
                Log.d("StreamOneUp", (new StringBuilder("PhotoOneUpFragment.onItemClick: Some other view: ")).append(view).toString());
        	return;
        } 
        
        StreamOneUpCommentView streamoneupcommentview = (StreamOneUpCommentView)view;
        Resources resources = getSafeContext().getResources();
        boolean flag = mAccount.isMyGaiaId(streamoneupcommentview.getAuthorId());
        boolean flag1 = mAccount.isMyGaiaId(mBackgroundRef.getOwnerGaiaId());
        ArrayList arraylist = new ArrayList(5);
        ArrayList arraylist1 = new ArrayList(5);
        boolean flag2 = streamoneupcommentview.getPlusOneByMe();
        boolean flag3 = streamoneupcommentview.isFlagged();
        if(!flag3)
        {
            int j;
            if(flag2)
                j = R.string.stream_one_up_comment_option_plusminus;
            else
                j = R.string.stream_one_up_comment_option_plusone;
            arraylist.add(resources.getString(j));
            arraylist1.add(Integer.valueOf(38));
        }
        if(flag)
        {
            arraylist.add(resources.getString(R.string.stream_one_up_comment_option_edit));
            arraylist1.add(Integer.valueOf(37));
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
        String as[];
        AlertFragmentDialog alertfragmentdialog;
        as = new String[arraylist.size()];
        arraylist.toArray(as);
        alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.stream_one_up_comment_options_title), as);
        alertfragmentdialog.setTargetFragment(this, 0);
        alertfragmentdialog.getArguments().putIntegerArrayList("comment_action", arraylist1);
        alertfragmentdialog.getArguments().putString("comment_id", streamoneupcommentview.getCommentId());
        alertfragmentdialog.getArguments().putString("comment_content", streamoneupcommentview.getCommentContent());
        alertfragmentdialog.getArguments().putBoolean("plus_one_by_me", flag2);
        alertfragmentdialog.getArguments().putLong("photo_id", mBackgroundRef.getPhotoId());
        alertfragmentdialog.show(getFragmentManager(), "pouf_delete_comment");
        streamoneupcommentview.cancelPressedState();
    }

    public final void onLoadFinished(Loader loader, Object obj) {
        // TODO
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onLocationClick(DbLocation dblocation)
    {
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
                     if(getView() != null)
                     {
                         android.view.ViewGroup.MarginLayoutParams marginlayoutparams = (android.view.ViewGroup.MarginLayoutParams)mListParent.getLayoutParams();
                         marginlayoutparams.bottomMargin = footerHeight;
                         mListParent.setLayoutParams(marginlayoutparams);
                     }
                 }

             });
         }
    	 
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag1;
        if(!mCallback.isFragmentActive(this))
        {
            flag1 = false;
        } else
        {
            int i = menuitem.getItemId();
            if(i == R.id.feedback)
            {
                recordUserAction(OzActions.SETTINGS_FEEDBACK);
                MeetupFeedback.launch(getActivity());
                flag1 = true;
            } else
            if(i == R.id.report_abuse)
            {
                AlertFragmentDialog alertfragmentdialog2 = AlertFragmentDialog.newInstance(getString(R.string.menu_report_photo), getString(R.string.report_photo_question), getString(R.string.ok), getString(R.string.cancel));
                alertfragmentdialog2.setTargetFragment(this, 0);
                alertfragmentdialog2.show(getFragmentManager(), "pouf_report_photo");
                flag1 = true;
            } else
            if(i == R.id.share)
            {
                ArrayList arraylist = new ArrayList(1);
                arraylist.add(mBackgroundRef);
                startActivity(Intents.getPostActivityIntent(getActivity(), mAccount, arraylist));
                FragmentActivity fragmentactivity = getActivity();
                boolean flag = false;
                if(fragmentactivity != null)
                {
                    Intent intent = fragmentactivity.getIntent();
                    flag = false;
                    if(intent != null)
                    {
                        boolean flag2 = TextUtils.isEmpty(intent.getStringExtra("notif_id"));
                        flag = false;
                        if(!flag2)
                            flag = true;
                    }
                }
                if(flag)
                    EsAnalytics.recordActionEvent(getSafeContext(), mAccount, OzActions.SHARE_INSTANT_UPLOAD_FROM_NOTIFICATION, OzViews.getViewForLogging(getSafeContext()));
                flag1 = true;
            } else
            if(i == R.id.set_profile_photo)
            {
                startActivityForResult(Intents.getPhotoPickerIntent(getActivity(), mAccount, null, mBackgroundRef, 1), 1);
                flag1 = true;
            } else
            if(i == R.id.set_wallpaper)
            {
                PhotoHeaderView photoheaderview = mBackgroundView;
                Bitmap bitmap = null;
                if(photoheaderview != null)
                    bitmap = mBackgroundView.getBitmap();
                if(bitmap != null)
                {
                    Resources resources1 = getResources();
                    final String toastSuccess = resources1.getString(R.string.set_wallpaper_photo_success);
                    final String toastError = resources1.getString(R.string.set_wallpaper_photo_error);
                    showProgressDialog(20, getString(R.string.set_wallpaper_photo_pending));
                    (new AsyncTask() {

                        public Boolean doInBackground(Object abitmap[])
                        {
                            Boolean boolean1;
                            try
                            {
                                WallpaperManager.getInstance(getActivity()).setBitmap((Bitmap)abitmap[0]);
                                boolean1 = Boolean.TRUE;
                            }
                            catch(IOException ioexception)
                            {
                                if(EsLog.isLoggable("StreamOneUp", 6))
                                    Log.e("StreamOneUp", "Exception setting wallpaper", ioexception);
                                boolean1 = Boolean.FALSE;
                            }
                            return boolean1;
                        }

                        protected final void onPostExecute(Object obj)
                        {
                            String s;
                            if(((Boolean)obj).booleanValue())
                                s = toastSuccess;
                            else
                                s = toastError;
                            Toast.makeText(getSafeContext(), s, 0).show();
                            DialogFragment dialogfragment = (DialogFragment)PhotoOneUpFragment.this.getFragmentManager().findFragmentByTag("pouf_pending");
                        }

                    }).execute(new Bitmap[] {
                        bitmap
                    });
                }
                flag1 = true;
            } else
            if(i == R.id.delete)
            {
                Resources resources = getResources();
                Uri uri;
                int j;
                AlertFragmentDialog alertfragmentdialog1;
                if(mBackgroundRef.getUrl() != null)
                    uri = Uri.parse(mBackgroundRef.getUrl());
                else
                    uri = null;
                if(!MediaStoreUtils.isMediaStoreUri(uri))
                    uri = null;
                if(uri == null)
                    j = R.plurals.delete_remote_photo_dialog_message;
                else
                    j = R.plurals.delete_local_photo_dialog_message;
                alertfragmentdialog1 = AlertFragmentDialog.newInstance(resources.getQuantityString(R.plurals.delete_photo_dialog_title, 1), resources.getQuantityString(j, 1), resources.getQuantityString(R.plurals.delete_photo, 1), getString(R.string.cancel));
                alertfragmentdialog1.setTargetFragment(this, 0);
                alertfragmentdialog1.show(getFragmentManager(), "pouf_delete_photo");
                flag1 = true;
            } else
            if(i == R.id.download)
            {
                doDownload(getSafeContext(), true);
                flag1 = true;
            } else
            if(i == R.id.refresh)
            {
                refresh();
                flag1 = true;
            } else
            if(i == R.id.remove_tag)
            {
                AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.menu_remove_tag), getString(R.string.remove_tag_question), getString(R.string.ok), getString(R.string.cancel));
                alertfragmentdialog.setTargetFragment(this, 0);
                alertfragmentdialog.show(getFragmentManager(), "pouf_delete_tag");
                flag1 = true;
            } else
            {
                flag1 = false;
            }
        }
        return flag1;
    }

    public final void onPause()
    {
        super.onPause();
        if(mBackgroundView != null)
        {
            PhotoHeaderView _tmp = mBackgroundView;
            PhotoHeaderView.onStop();
        }
        if(mListView != null)
        {
            for(int i = -1 + mListView.getChildCount(); i >= 0; i--)
                if(mListView.getChildAt(i) instanceof OneUpBaseView)
                    OneUpBaseView.onStop();

        }
        mCallback.removeScreenListener(this);
        mCallback.removeMenuItemListener(this);
        EsService.unregisterListener(mServiceListener);
    }

    public final void onPlaceClick(String s)
    {
        if(!TextUtils.isEmpty(s))
            startActivity(Intents.getProfileActivityByGaiaIdIntent(getActivity(), mAccount, s, null));
    }

    public final void onPlusOne(String s, DbPlusOneData dbplusonedata)
    {
        String s1 = mBackgroundRef.getOwnerGaiaId();
        long l = mBackgroundRef.getPhotoId();
        if(!EsService.isPhotoPlusOnePending(s1, s, l))
        {
            boolean flag;
            if(dbplusonedata == null || !dbplusonedata.isPlusOnedByMe())
                flag = true;
            else
                flag = false;
            EsService.photoPlusOne(getSafeContext(), mAccount, s1, s, l, flag);
        }
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        hostactionbar.showRefreshButton();
        hostactionbar.showTitle(mTitle);
        updateProgressIndicator(hostactionbar);
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
    }

    public final void onPrepareOptionsMenu(Menu menu) {
        if(null != mCallback && !mCallback.isFragmentActive(this)) {
        	return;
        }
        
        boolean flag6;
        boolean flag10;
        super.onPrepareOptionsMenu(menu);
        long l = mBackgroundRef.getPhotoId();
        String s = mBackgroundRef.getOwnerGaiaId();
        Long long1;
        boolean flag;
        Uri uri;
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        MenuItem menuitem;
        MenuItem menuitem1;
        if(mTagScroll == null)
            long1 = null;
        else
            long1 = mTagScroll.getMyApprovedShapeId();
        if(long1 != null)
            flag = true;
        else
            flag = false;
        if(mBackgroundRef.getUrl() != null)
            uri = Uri.parse(mBackgroundRef.getUrl());
        else
            uri = null;
        if(l == 0L && MediaStoreUtils.isMediaStoreUri(uri))
            flag1 = true;
        else
            flag1 = false;
        if(l != 0L && !MediaStoreUtils.isMediaStoreUri(uri))
            flag2 = true;
        else
            flag2 = false;
        if(l == 0L && uri != null)
            flag3 = true;
        else
            flag3 = false;
        if(mAccount.isMyGaiaId(s) || s == null && MediaStoreUtils.isMediaStoreUri(uri))
            flag4 = true;
        else
            flag4 = false;
        flag5 = "camerasync".equals(getArguments().getString("stream_id"));
        if(flag3) {
        	flag6 = true;
        } else {
        	if(!flag2) {
        		flag6 = false;
        	} else { 
        		if(flag4) {
        			flag6 = true;
        		} else { 
        			if(mDownloadable != null && mDownloadable.booleanValue())
        	            flag10 = true;
        	        else
        	            flag10 = false;
        	        if(!flag10) {
        	        	flag6 = false;
        	        } else { 
        	        	flag6 = true;
        	        }
        		}
        	}
        }
        
        boolean flag7;
        boolean flag8;
        boolean flag9;
        if(flag4 && (flag2 || flag1))
            flag7 = true;
        else
            flag7 = false;
        menu.findItem(R.id.share).setVisible(flag5);
        menuitem = menu.findItem(R.id.set_profile_photo);
        if(flag4 || flag)
            flag8 = true;
        else
            flag8 = false;
        menuitem.setVisible(flag8);
        menu.findItem(R.id.set_wallpaper).setVisible(flag4);
        menu.findItem(R.id.delete).setVisible(flag7);
        menu.findItem(R.id.download).setVisible(flag6);
        menuitem1 = menu.findItem(R.id.report_abuse);
        if(!flag4 && flag2)
            flag9 = true;
        else
            flag9 = false;
        menuitem1.setVisible(flag9);
        menu.findItem(R.id.remove_tag).setVisible(flag);
        menu.findItem(R.id.feedback).setVisible(true);
        
    }

    public final void onResume()
    {
        super.onResume();
        if(mBackgroundView != null)
        {
            PhotoHeaderView _tmp = mBackgroundView;
            PhotoHeaderView.onStart();
        }
        if(mListView != null)
        {
            for(int i = -1 + mListView.getChildCount(); i >= 0; i--)
                if(mListView.getChildAt(i) instanceof OneUpBaseView)
                    OneUpBaseView.onStart();

        }
        EsService.registerListener(mServiceListener);
        mCallback.addScreenListener(this);
        mCallback.addMenuItemListener(this);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingRequestId.intValue());
            mServiceListener.handleServiceCallback(mPendingRequestId.intValue(), serviceresult);
        }
        if(mRefreshRequestId != null && !EsService.isRequestPending(mRefreshRequestId.intValue()))
        {
            EsService.removeResult(mRefreshRequestId.intValue());
            mRefreshRequestId = null;
        }
        updateProgressIndicator(getActionBar());
        invalidateActionBar();
        if(mPendingBytes != null)
        {
            if(mPendingRequestId != null)
            {
                if(Log.isLoggable("StreamOneUp", 5))
                    Log.w("StreamOneUp", "Both a pending profile image and an existing request");
            } else
            {
                byte abyte0[] = mPendingBytes;
                mPendingRequestId = Integer.valueOf(EsService.uploadProfilePhoto(getActivity(), mAccount, abyte0));
                showProgressDialog(21, getString(R.string.set_profile_photo_pending));
            }
            mPendingBytes = null;
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mPendingRequestId != null)
            bundle.putInt("pending_request_id", mPendingRequestId.intValue());
        if(mRefreshRequestId != null)
            bundle.putInt("refresh_request_id", mRefreshRequestId.intValue());
        if(mAudienceData != null)
            bundle.putParcelable("audience_data", mAudienceData);
        if(!mFlaggedComments.isEmpty())
        {
            String as[] = new String[mFlaggedComments.size()];
            mFlaggedComments.toArray(as);
            bundle.putStringArray("flagged_comments", as);
        }
        bundle.putParcelable("photo_ref", mBackgroundRef);
        bundle.putInt("operation_type", mOperationType);
        bundle.putBoolean("read_processed", mReadProcessed);
        bundle.putBoolean("full_screen", mFullScreen);
        bundle.putBoolean("is_placeholder", mIsPlaceholder);
    }

    protected final void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mAutoPlay = bundle.getBoolean("auto_play_music", false);
    }

    public final void onSkyjamBuyClick(String s)
    {
    }

    public final void onSkyjamListenClick(String s)
    {
    }

    public final void onSourceAppContentClick(String s, List list, String s1, String s2, String s3)
    {
    }

    public final void onSpanClick(URLSpan urlspan)
    {
        String s = urlspan.getURL();
        Context context = getSafeContext();
        if(s.startsWith("https://plus.google.com/s/%23"))
        {
            String s1 = (new StringBuilder("#")).append(s.substring(29)).toString();
            startActivity(Intents.getPostSearchActivityIntent(context, mAccount, s1));
        } else
        {
            if(Intents.isProfileUrl(urlspan.getURL()))
            {
                Bundle bundle = EsAnalyticsData.createExtras("extra_gaia_id", Intents.getParameter(s, "pid="));
                recordUserAction(OzActions.ONE_UP_SELECT_PERSON, bundle);
            }
            Intents.viewContent(getActivity(), mAccount, s);
        }
    }

    public final void onSquareClick(String s, String s1)
    {
    }

    public final void onUserImageClick(String s, String s1)
    {
        Context context = getSafeContext();
        Bundle bundle = EsAnalyticsData.createExtras("extra_gaia_id", s);
        recordUserAction(OzActions.ONE_UP_SELECT_AUTHOR, bundle);
        startActivity(Intents.getProfileActivityByGaiaIdIntent(context, mAccount, s, null));
    }

    public final void onViewActivated()
    {
        boolean flag = mCallback.isFragmentActive(this);
        if(flag)
            mCallback.onFragmentVisible(this);
        if(mBackgroundView != null)
            mBackgroundView.doAnimate(flag);
    }

    public final void recordNavigationAction()
    {
    }

    public final void refresh()
    {
        super.refresh();
        if(mRefreshRequestId == null)
            if(mBackgroundRef.getPhotoId() == 0L)
                mRefreshRequestId = Integer.valueOf(EsService.getPhotoSettings(getSafeContext(), mAccount, mBackgroundRef.getOwnerGaiaId()));
            else
                mRefreshRequestId = Integer.valueOf(EsService.getPhoto(getSafeContext(), mAccount, mBackgroundRef.getOwnerGaiaId(), mBackgroundRef.getPhotoId(), mAuthkey));
        updateProgressIndicator(getActionBar());
    }

    public final void setTitle(String s)
    {
        mTitle = s;
    }
    
    static void access$2200(PhotoOneUpFragment photooneupfragment, Context context, File file, String s, String s1)
    {
        if(android.os.Build.VERSION.SDK_INT < 12) {
        	Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            intent.setData(Uri.parse(file.toURI().toString()));
            context.sendBroadcast(intent);
        } else {
        	try {
        		((DownloadManager)context.getSystemService("download")).addCompletedDownload(file.getName(), s, true, s1, file.getAbsolutePath(), file.length(), false);
        	} catch (IllegalArgumentException illegalargumentexception) {
        		if(EsLog.isLoggable("StreamOneUp", 5))
                    Log.w("StreamOneUp", "Could not add photo to the Downloads application", illegalargumentexception);
        		
        		//
        		Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                intent.setData(Uri.parse(file.toURI().toString()));
                context.sendBroadcast(intent);
        	}
        }
    }
	
	
	private static final class MyTextWatcher implements TextWatcher {

	    public final void afterTextChanged(Editable editable)
	    {
	        View view = mView;
	        boolean flag;
	        if(TextUtils.getTrimmedLength(editable) > 0)
	            flag = true;
	        else
	            flag = false;
	        view.setEnabled(flag);
	    }
	
	    public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
	    {
	    }
	
	    public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
	    {
	    }
	
	    private final View mView;
	
	    private MyTextWatcher(View view)
	    {
	        mView = view;
	    }
	
	    MyTextWatcher(View view, byte byte0)
	    {
	        this(view);
	    }
	}

	private final class ServiceListener extends EsServiceListener {

	    private boolean handleServiceCallback(int i, ServiceResult serviceresult)
	    {
	        Integer integer;
	        boolean flag;
	        integer = mPendingRequestId;
	        flag = false;
	        if(integer == null) {
	        	return flag;
	        }
	        int j;
	        j = mPendingRequestId.intValue();
	        flag = false;
	        if(j != i){
	        	return flag;
	        }
	        
	        mPendingRequestId = null;
	        DialogFragment dialogfragment = (DialogFragment)PhotoOneUpFragment.this.getFragmentManager().findFragmentByTag("pouf_pending");
	        if(serviceresult == null || !serviceresult.hasError()) {
	        	if(16 == mOperationType) {
	        		getActivity().finish();
	        	} else if(17 == mOperationType) {
	        		getActivity().finish();
	        	}
	        	flag = true;
        		return flag;
	        } else {
	        	int k = R.string.operation_failed;
	        	switch(mOperationType) {
	        	case 16:
	        		k = R.string.remove_photo_error;
	        		break;
	        	case 17:
	        		k = R.string.report_photo_error;
	        		break;
	        	case 18:
	        		return flag;
	        	case 21:
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
	        	case 37:
	        		k = R.string.comment_edit_error;
	        		break;
	        	case 48:
	        		k = R.string.photo_tag_deny_error;
	        		break;
	        	case 49:
	        		k = R.string.photo_tag_approve_error;
	        		break;
	        	case 50:
	        		k = R.string.photo_tag_deny_error;
	        		break;
	        	default:
	        		k = R.string.operation_failed;
	        		break;
	        	}
	        	Toast.makeText(getSafeContext(), k, 0).show();
	 	        flag = false;
	 	        return flag;
	        }
	    }
	
	    public final void onCreatePhotoCommentComplete(int i, ServiceResult serviceresult)
	    {
	        if(handleServiceCallback(i, serviceresult) && mCommentText != null)
	            mCommentText.setText(null);
	    }
	
	    public final void onDeletePhotoCommentsComplete(int i, ServiceResult serviceresult)
	    {
	        handleServiceCallback(i, serviceresult);
	    }
	
	    public final void onDeletePhotosComplete(int i, ServiceResult serviceresult)
	    {
	        if(mPendingRequestId != null && mPendingRequestId.intValue() == i)
	        {
	            mPendingRequestId = null;
	            if(serviceresult != null && serviceresult.hasError())
	            {
	                DialogFragment dialogfragment = (DialogFragment)PhotoOneUpFragment.this.getFragmentManager().findFragmentByTag("pouf_pending");
	                Toast.makeText(getSafeContext(), R.string.remove_photo_error, 1).show();
	            } else
	            {
	                ArrayList arraylist = new ArrayList(1);
	                arraylist.add(mBackgroundRef);
	                mPendingRequestId = Integer.valueOf(EsService.deleteLocalPhotos(getSafeContext(), arraylist));
	            }
	        }
	    }
	
	    public final void onEditPhotoCommentComplete(int i, ServiceResult serviceresult)
	    {
	        handleServiceCallback(i, serviceresult);
	    }
	
	    public final void onGetPhoto(int i, long l)
	    {
	        if(mRefreshRequestId != null && mRefreshRequestId.intValue() == i)
	        {
	            mRefreshRequestId = null;
	            updateProgressIndicator(getActionBar());
	            invalidateActionBar();
	        }
	    }
	
	    public final void onGetPhotoSettings(int i, boolean flag)
	    {
	        if(mRefreshRequestId != null && mRefreshRequestId.intValue() == i)
	        {
	            mRefreshRequestId = null;
	            mDownloadable = Boolean.valueOf(false);
	            updateProgressIndicator(getActionBar());
	            invalidateActionBar();
	        }
	    }
	
	    public final void onLocalPhotoDelete(int i, ArrayList arraylist, ServiceResult serviceresult)
	    {
	        if(!handleServiceCallback(i, serviceresult))
	        {
	            PhotoOneUpCallbacks photooneupcallbacks;
	            for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); photooneupcallbacks.onPhotoRemoved())
	            {
	                MediaRef mediaref = (MediaRef)iterator.next();
	                photooneupcallbacks = mCallback;
	                mediaref.getPhotoId();
	            }
	
	        }
	    }
	
	    public final void onNameTagApprovalComplete(int i, long l, ServiceResult serviceresult)
	    {
	        if(!handleServiceCallback(i, serviceresult))
	            mCallback.onPhotoRemoved();
	    }
	
	    public final void onPhotoPlusOneComplete(int i, boolean flag, ServiceResult serviceresult)
	    {
	        if(mPendingRequestId != null && mPendingRequestId.intValue() == i){
	        	mPendingRequestId = null;
		        if(serviceresult != null && serviceresult.hasError())
		        {
		            Context context = getSafeContext();
		            int j;
		            if(flag)
		                j = R.string.plusone_error;
		            else
		                j = R.string.delete_plusone_error;
		            Toast.makeText(context, j, 1).show();
		        }
	        }
	
	    }
	
	    public final void onPlusOneComment(boolean flag, ServiceResult serviceresult)
	    {
	        if(serviceresult != null && serviceresult.hasError())
	        {
	            Context context = getSafeContext();
	            int i;
	            if(flag)
	                i = R.string.plusone_error;
	            else
	                i = R.string.delete_plusone_error;
	            Toast.makeText(context, i, 0).show();
	        }
	    }
	
	    public final void onReportPhotoCommentsComplete(int i, String s, boolean flag, ServiceResult serviceresult)
	    {
	        if(handleServiceCallback(i, serviceresult))
	            if(flag)
	                mAdapter.removeFlaggedComment(s);
	            else
	                mAdapter.addFlaggedComment(s);
	    }
	
	    public final void onReportPhotoComplete(int i, ServiceResult serviceresult)
	    {
	        handleServiceCallback(i, serviceresult);
	    }
	
	    public final void onSavePhoto(int i, File file, boolean flag, String s, String s1, ServiceResult serviceresult)
	    {
	        if(mPendingRequestId != null && mPendingRequestId.intValue() == i)
	        {
	            mPendingRequestId = null;
	            DialogFragment dialogfragment = (DialogFragment)PhotoOneUpFragment.this.getFragmentManager().findFragmentByTag("pouf_pending");
	            if(serviceresult != null && serviceresult.hasError())
	            {
	                int j;
	                Bundle bundle;
	                if(EsLog.isLoggable("StreamOneUp", 6))
	                    if(serviceresult.getException() != null)
	                        Log.e("StreamOneUp", "Could not download image", serviceresult.getException());
	                    else
	                        Log.e("StreamOneUp", (new StringBuilder("Could not download image: ")).append(serviceresult.getErrorCode()).toString());
	                if(flag)
	                    j = R.id.photo_view_download_full_failed_dialog;
	                else
	                    j = R.id.photo_view_download_nonfull_failed_dialog;
	                bundle = new Bundle();
	                bundle.putString("tag", getTag());
	                getActivity().showDialog(j, bundle);
	            } else
	            {
	                FragmentActivity fragmentactivity = getActivity();
	                if(file != null && file.exists())
	                    PhotoOneUpFragment.access$2200(PhotoOneUpFragment.this, fragmentactivity, file, s, s1);
	                Toast.makeText(fragmentactivity, R.string.download_photo_success, 1).show();
	            }
	        }
	    }
	
	    public final void onTagSuggestionApprovalComplete(int i, String s, ServiceResult serviceresult)
	    {
	        if(!handleServiceCallback(i, serviceresult))
	        {
	            PhotoOneUpCallbacks photooneupcallbacks = mCallback;
	            Long.valueOf(s).longValue();
	            photooneupcallbacks.onPhotoRemoved();
	        }
	    }
	
	    public final void onUploadProfilePhotoComplete(int i, ServiceResult serviceresult)
	    {
	        handleServiceCallback(i, serviceresult);
	    }
	
	}
}
