/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.LocationController;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.AnalyticsInfo;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.ApiUtils;
import com.galaxy.meetup.client.android.api.CallToActionData;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.DbAudienceData;
import com.galaxy.meetup.client.android.content.DbEmbedDeepLink;
import com.galaxy.meetup.client.android.content.DbEmbedEmotishare;
import com.galaxy.meetup.client.android.content.DbEmbedMedia;
import com.galaxy.meetup.client.android.content.DbEmbedSkyjam;
import com.galaxy.meetup.client.android.content.DbEmotishareMetadata;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsApiProvider;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.PreviewRequestData;
import com.galaxy.meetup.client.android.content.SquareTargetData;
import com.galaxy.meetup.client.android.network.ApiaryActivity;
import com.galaxy.meetup.client.android.network.ApiaryActivityFactory;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.activity.BaseActivity;
import com.galaxy.meetup.client.android.ui.activity.ShareActivity;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.view.AlbumColumnGridItemView;
import com.galaxy.meetup.client.android.ui.view.AudienceView;
import com.galaxy.meetup.client.android.ui.view.EmotiShareView;
import com.galaxy.meetup.client.android.ui.view.ImageResourceView;
import com.galaxy.meetup.client.android.ui.view.LinksCardView;
import com.galaxy.meetup.client.android.ui.view.MentionMultiAutoCompleteTextView;
import com.galaxy.meetup.client.android.ui.view.OneUpLinkView;
import com.galaxy.meetup.client.android.ui.view.PostAclButtonView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpSkyjamView;
import com.galaxy.meetup.client.android.ui.view.TextOnlyAudienceView;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.GalleryUtils;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.MentionTokenizer;
import com.galaxy.meetup.client.util.PeopleUtils;
import com.galaxy.meetup.client.util.PlatformContractUtils;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.client.util.ResourceRedirector;
import com.galaxy.meetup.client.util.SoftInput;
import com.galaxy.meetup.client.util.StringUtils;
import com.galaxy.meetup.server.client.domain.DeepLinkData;
import com.galaxy.meetup.server.client.domain.EmbedClientItem;
import com.galaxy.meetup.server.client.domain.PlayMusicAlbum;
import com.galaxy.meetup.server.client.domain.PlayMusicTrack;
import com.galaxy.meetup.server.client.domain.SharingRoster;
import com.galaxy.meetup.server.client.domain.SharingTargetId;
import com.galaxy.meetup.server.client.domain.Thing;
import com.galaxy.meetup.server.client.domain.VideoObject;
import com.galaxy.meetup.server.client.domain.WebPage;

/**
 * 
 * @author sihai
 *
 */
public class PostFragment extends Fragment implements AlertDialogListener {

	private EsAccount mAccount;
    private View mAclDropDown;
    private String mActivityId;
    private ApiaryApiInfo mApiaryApiInfo;
    private List mAttachmentRefs;
    private List mAttachments;
    private AudienceView mAudienceView;
    private CallToActionData mCallToAction;
    private MentionMultiAutoCompleteTextView mCommentsView;
    private String mContentDeepLinkId;
    private Bundle mContentDeepLinkMetadata;
    private PostAclButtonView mCreateAclButton;
    private PostAclButtonView mDefaultAclButton;
    private AudienceData mDefaultAudience;
    private PostAclButtonView mDomainAclButton;
    private CircleData mDomainCircle;
    private DbEmotishareMetadata mEmotiShare;
    private DbEmotishareMetadata mEmotiShareResult;
    private View mEmptyMediaView;
    private View mFocusOverrideView;
    private String mFooterMessage;
    private PostAclButtonView mHistoryAclButtonArray[];
    private ArrayList mHistoryAudienceArray;
    private Integer mInsertCameraPhotoRequestId;
    private boolean mIsFromPlusOne;
    private boolean mLoadingMediaAttachments;
    private boolean mLoadingUrlPreview;
    private View mLoadingView;
    private DbLocation mLocation;
    private boolean mLocationChecked;
    private LocationController mLocationController;
    private View mMediaContainer;
    private TextView mMediaCount;
    private MediaGallery mMediaGallery;
    private ViewGroup mMediaGalleryView;
    private final android.support.v4.app.LoaderManager.LoaderCallbacks mMediaRefLoaderCallbacks = new android.support.v4.app.LoaderManager.LoaderCallbacks() {

        public final Loader onCreateLoader(int i, Bundle bundle) {
            return new MediaRefLoader(getActivity(), mAccount, mAttachments);
        }

        public final void onLoadFinished(Loader loader, Object obj) {
            List arraylist = (List)obj;
            int i;
            int j;
            if(mAttachments == null)
                i = 0;
            else
                i = mAttachments.size();
            if(arraylist == null)
                j = 0;
            else
                j = arraylist.size();
            mAttachments = null;
            MediaRef mediaref;
            for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); addToMediaGallery(mediaref))
                mediaref = (MediaRef)iterator.next();

            if(j < i)
                Toast.makeText(getActivity(), R.string.post_invalid_photos_unsupported, 1).show();
        }
        
		public final void onLoaderReset(Loader loader) {
		}

    };
    
    private final MentionTokenizer mMentionTokenizer = new MentionTokenizer();
    private String mOriginalText;
    private Integer mPendingPostId;
    private ViewGroup mPreviewContainerView;
    private ApiaryActivity mPreviewResult;
    private ViewGroup mPreviewWrapperView;
    private Location mProviderLocation;
    private PostAclButtonView mPublicAclButton;
    private CircleData mPublicCircle;
    private boolean mRemoveEmotiShare;
    private boolean mRemoveLocation;
    private View mRemoveLocationView;
    private View mRemovePreviewButton;
    private AudienceData mResultAudience;
    private DbLocation mResultLocation;
    private ArrayList mResultMediaItems;
    private AudienceData mSavedDefaultAudience;
    private ScrollView mScrollView;
    private final EsServiceListener mServiceListener = new ServiceListener();
    private Animation mSlideInDown;
    private Animation mSlideOutUp;
    private PostAclButtonView mSquaresAclButton;
    private final TextWatcher mTextWatcher = new TextWatcher() {

        public final void afterTextChanged(Editable editable)
        {
            updatePostUI();
            PostFragment.updateText(getView());
        }

        public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
        {
        }

        public final void onTextChanged(CharSequence charsequence, int i, int j, int k) {
        	if(null == mCommentsView) {
        		return;
        	}
        	
        	int l = mCommentsView.getSelectionEnd();
            if(mMentionTokenizer.findTokenStart(charsequence, l) + mCommentsView.getThreshold() <= l) {
                int i1 = (int)getActivity().getResources().getDimension(R.dimen.plus_mention_suggestion_min_space);
                int ai[] = new int[2];
                mCommentsView.getLocationOnScreen(ai);
                Rect rect = new Rect();
                getView().getWindowVisibleDisplayFrame(rect);
                int j1 = ai[1] + mCommentsView.getCursorYPosition();
                if(rect.height() - j1 < i1)
                    mScrollView.smoothScrollTo(0, mCommentsView.getCursorYTop());
            }
        }
    };
    
    private String mUrl;
    private CircleData mYourCircles;
    private PostAclButtonView mYourCirclesAclButton;
    private android.view.View.OnClickListener onClickListener;
    private android.widget.TextView.OnEditorActionListener onEditorActionListener;
    
    
	public PostFragment() {
        mPreviewResult = null;
        onClickListener = new android.view.View.OnClickListener() {

            private void toggleAclOverlay() {
                if(mAclDropDown.getVisibility() == 0)
                    hideAclOverlay();
                else
                    PostFragment.access$1800(PostFragment.this);
            }

            public final void onClick(View view) {
                FragmentActivity fragmentactivity;
                Bundle bundle;
                OzViews ozviews;
                int i;
                fragmentactivity = getActivity();
                bundle = getExtrasForLogging();
                ozviews = OzViews.getViewForLogging(fragmentactivity);
                i = view.getId();
                if(i != R.id.audience_button) {
                	if(i == R.id.chevron_icon)
                        toggleAclOverlay();
                    else
                    if(i == R.id.empty_media || i == R.id.choose_media)
                    {
                        hideAclOverlay();
                    } else
                    if(i == R.id.empty_emotishare)
                    {
                        ResourceRedirector.getInstance();
                        if(Property.ENABLE_EMOTISHARE.getBoolean())
                        {
                            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.EMOTISHARE_INSERT_CLICKED, ozviews, bundle);
                            Intent intent1 = Intents.getChooseEmotiShareObjectIntent(fragmentactivity, mAccount, mEmotiShare);
                            launchActivity(intent1, 5);
                        }
                    } else
                    if(i == R.id.location_view)
                    {
                        EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.PLATFORM_SHARE_CLICKED_LOCATION, ozviews, bundle);
                        boolean flag = PostFragment.access$1300(PostFragment.this);
                        DbLocation dblocation;
                        Intent intent;
                        if(mLocation != null && mLocation.hasCoordinates())
                            dblocation = mLocation;
                        else
                            dblocation = null;
                        intent = Intents.getChooseLocationIntent(fragmentactivity, mAccount, flag, dblocation);
                        launchActivity(intent, 3);
                    } else
                    if(i == R.id.remove_location)
                    {
                        hideAclOverlay();
                        EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.PLATFORM_SHARE_CLICKED_LOCATION, ozviews, bundle);
                        mRemoveLocation = true;
                        setLocationChecked(false);
                    } else
                    if(i == R.id.mention_scroll_view || i == R.id.compose_text)
                    {
                        hideAclOverlay();
                    } else
                    {
                        Intents.PhotoViewIntentBuilder photoviewintentbuilder = Intents.newPhotoComposeActivityIntentBuilder(fragmentactivity);
                        MediaRef amediaref[] = new MediaRef[mAttachmentRefs.size()];
                        MediaRef amediaref1[] = (MediaRef[])mAttachmentRefs.toArray(amediaref);
                        MediaRef mediaref = (MediaRef)view.getTag();
                        if(mediaref != null)
                        {
                            int j = 0;
                            for(int k = 0; k < amediaref1.length; k++)
                                if(mediaref.equals(amediaref1[k]))
                                    j = k;

                            photoviewintentbuilder.setAccount(mAccount).setPhotoIndex(Integer.valueOf(j)).setMediaRefs(amediaref1);
                            launchActivity(photoviewintentbuilder.build(), 4);
                        }
                    }
                } else { 
                	if(mAudienceView.getAudience().isEmpty())
                        toggleAclOverlay();
                    else
                    if(mAudienceView.getAudience().getSquareTargetCount() > 0)
                    	// TODO
                    	;
                    else
                        launchAclPicker();
                }
            }
        };
        
        onEditorActionListener = new android.widget.TextView.OnEditorActionListener() {

            public final boolean onEditorAction(TextView textview, int i, KeyEvent keyevent) {
                if(textview != mCommentsView) {
                	return false; 
                }
                if(6 == i) {
                	SoftInput.hide(textview);
                	return true;
                }
                return false;
            }
        };
    }
	
	private void addLocationListener() {
        if(mLocationController == null && LocationController.isProviderEnabled(getActivity()))
            mLocationController = new LocationController(getActivity(), mAccount, true, 3000L, mProviderLocation, new PostLocationListener());
    }

    private void addToMediaGallery(MediaRef mediaref) {
        mAttachmentRefs.add(mediaref);
        mMediaGallery.add(mediaref);
        getView();
        updatePreviewContainer();
        updatePostUI();
    }
    
    private static boolean compareAudiences(AudienceData audiencedata, AudienceData audiencedata1) {
        boolean flag = false;
        if(audiencedata == null || audiencedata1 == null) {
        	return false; 
        }
        
        
        SharingRoster sharingroster = EsPeopleData.convertAudienceToSharingRoster(audiencedata);
        HashSet hashset = new HashSet(sharingroster.sharingTargetId.size());
        SharingTargetId sharingtargetid1;
        for(Iterator iterator = sharingroster.sharingTargetId.iterator(); iterator.hasNext(); hashset.add(sharingtargetid1.toJsonString()))
            sharingtargetid1 = (SharingTargetId)iterator.next();

        for(Iterator iterator1 = EsPeopleData.convertAudienceToSharingRoster(audiencedata1).sharingTargetId.iterator(); iterator1.hasNext();)
        {
            SharingTargetId sharingtargetid = (SharingTargetId)iterator1.next();
            if(!hashset.remove(sharingtargetid.toJsonString()))
            {
                return false;
            }
        }

        flag = hashset.isEmpty();
        return flag;
    }

    private void createDefaultAclButton(View view, AudienceData audiencedata)
    {
        mSavedDefaultAudience = audiencedata;
        PostAclButtonView postaclbuttonview = (PostAclButtonView)view.findViewById(R.id.default_acl_button);
        if(!isValidCustomAudience(audiencedata))
        {
            mDefaultAclButton = null;
            mDefaultAudience = null;
            postaclbuttonview.setVisibility(8);
        } else
        {
            boolean flag;
            PostAclButtonView postaclbuttonview1;
            String s;
            int i;
            int j;
            if(audiencedata.getSquareTargetCount() > 0)
                flag = true;
            else
                flag = false;
            mDefaultAclButton = postaclbuttonview;
            mDefaultAudience = audiencedata;
            postaclbuttonview1 = mDefaultAclButton;
            s = audiencedata.toNameList(getActivity());
            if(flag)
                i = R.drawable.ic_nav_communities;
            else
                i = R.drawable.ic_person_active;
            if(flag)
                j = R.drawable.ic_communities_grey;
            else
                j = R.drawable.ic_acl_custom_inactive;
            postaclbuttonview1.initialize(s, i, j, R.drawable.ic_done_save_ok_blue);
            mDefaultAclButton.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    updateAudienceUI(null);
                    updateAudienceUI(mSavedDefaultAudience);
                    updatePostUI();
                    hideAclOverlay();
                }
            });
            
            mDefaultAclButton.setVisibility(0);
        }
    }

    private void createDomainAclButton(View view, CircleData circledata)
    {
        mDomainCircle = circledata;
        PostAclButtonView postaclbuttonview = (PostAclButtonView)view.findViewById(R.id.domain_acl_button);
        if(circledata == null)
        {
            mDomainAclButton = null;
            postaclbuttonview.setVisibility(8);
        } else
        {
            mDomainAclButton = postaclbuttonview;
            mDomainAclButton.initialize(circledata.getName(), R.drawable.ic_acl_domain_active, R.drawable.ic_acl_domain_inactive, R.drawable.ic_done_save_ok_green);
            mDomainAclButton.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    updateAudienceUI(new AudienceData(mDomainCircle));
                    updatePostUI();
                    hideAclOverlay();
                }
            });
            mDomainAclButton.setVisibility(0);
        }
    }

    private void createPublicAclButton(View view, CircleData circledata)
    {
        mPublicCircle = circledata;
        PostAclButtonView postaclbuttonview = (PostAclButtonView)view.findViewById(R.id.public_acl_button);
        if(circledata == null)
        {
            mPublicAclButton = null;
            postaclbuttonview.setVisibility(8);
        } else
        {
            mPublicAclButton = postaclbuttonview;
            mPublicAclButton.initialize(circledata.getName(), R.drawable.ic_public_active, R.drawable.ic_public, R.drawable.ic_done_save_ok_green);
            mPublicAclButton.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    updateAudienceUI(new AudienceData(mPublicCircle));
                    updatePostUI();
                    hideAclOverlay();
                }

            });
            
            mPublicAclButton.setVisibility(0);
        }
    }

    private void createYourCirclesAclButton(View view, CircleData circledata)
    {
        mYourCircles = circledata;
        PostAclButtonView postaclbuttonview = (PostAclButtonView)view.findViewById(R.id.your_circles_acl_button);
        if(circledata == null)
        {
            mYourCirclesAclButton = null;
            postaclbuttonview.setVisibility(8);
        } else
        {
            mYourCirclesAclButton = postaclbuttonview;
            mYourCirclesAclButton.initialize(circledata.getName(), R.drawable.ic_circles_active, R.drawable.ic_circles, R.drawable.ic_done_save_ok_blue);
            mYourCirclesAclButton.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    updateAudienceUI(new AudienceData(mYourCircles));
                    updatePostUI();
                    hideAclOverlay();
                }
            });
            
            mYourCirclesAclButton.setVisibility(0);
        }
    }

    private boolean getCityLevelLocationPreference()
    {
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("streams", 0);
        boolean flag;
        if(sharedpreferences.contains("city_level_sharebox_location"))
            flag = sharedpreferences.getBoolean("city_level_sharebox_location", false);
        else
            flag = sharedpreferences.getBoolean("city_level_location", false);
        return flag;
    }

    private Bundle getExtrasForLogging() {
        BaseActivity instrumentedactivity = (BaseActivity)getActivity();
        if(null == instrumentedactivity) {
        	return null;
        }
        Bundle bundle = new Bundle();
        if(BaseActivity.isFromThirdPartyApp(instrumentedactivity.getIntent()))
            bundle.putBoolean("extra_platform_event", true);
        if(mAudienceView != null && mAudienceView.getAudience().getSquareTargetCount() > 0)
            bundle.putString("extra_square_id", mAudienceView.getAudience().getSquareTarget(0).getSquareId());
        Bundle bundle1 = EsAnalytics.addExtrasForLogging(bundle, mEmotiShare);
        if(bundle1.isEmpty())
            bundle1 = null;
        return bundle1;
    }

    private static DbLocation getLocationFromExtras(Bundle bundle) {
        DbLocation dblocation = (DbLocation)bundle.getParcelable("location");
        if(null != dblocation) {
        	return dblocation;
        }
        
        if(!bundle.containsKey("location_name") && !bundle.containsKey("cid")) {
        	return null;
        }
        
       // TODO
       return null;
    }

    private void handlePostResult(int i, ServiceResult serviceresult)
    {
        if(mPendingPostId != null && mPendingPostId.intValue() == i)
        {
            mPendingPostId = null;
            FragmentActivity fragmentactivity = getActivity();
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            if(serviceresult != null && serviceresult.hasError())
            {
                Toast.makeText(getActivity(), R.string.post_create_activity_error, 0).show();
            } else
            {
                Toast.makeText(fragmentactivity, R.string.share_post_success, 0).show();
                android.content.SharedPreferences.Editor editor = getActivity().getSharedPreferences("streams", 0).edit();
                editor.putBoolean("want_sharebox_locations", mLocationChecked);
                editor.putBoolean("city_level_sharebox_location", getCityLevelLocationPreference());
                editor.commit();
                fragmentactivity.setResult(-1);
                fragmentactivity.finish();
            }
        }
    }

    private void handlePreviewResult(ServiceResult serviceresult, ApiaryActivity apiaryactivity)
    {
        if(serviceresult.hasError())
        {
            if(EsLog.isLoggable("PostFragment", 3))
                Log.d("PostFragment", (new StringBuilder("Could not retrieve preview: errorCode: ")).append(serviceresult.getErrorCode()).toString());
            getActivity().showDialog(28199);
        }
        mPreviewResult = apiaryactivity;
        (new Handler(Looper.getMainLooper())).post(new Runnable() {

            public final void run()
            {
                updateViews(getView());
            }

        });
    }

    private boolean hasContentDeepLinkMetadata()
    {
        boolean flag;
        if(mContentDeepLinkMetadata != null && !TextUtils.isEmpty(mContentDeepLinkMetadata.getString("title")) && !TextUtils.isEmpty(mContentDeepLinkMetadata.getString("description")))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void hideAclOverlay()
    {
        if(mAclDropDown != null && mAclDropDown.getVisibility() == 0)
            mAclDropDown.startAnimation(mSlideOutUp);
    }

    private void insertCameraPhoto(String s)
    {
        FragmentActivity fragmentactivity = getActivity();
        if(s != null)
        {
            Uri uri = Uri.parse(s);
            MediaRef mediaref = new MediaRef(mAccount.getGaiaId(), 0L, null, uri, MediaRef.MediaType.IMAGE);
            mResultMediaItems = new ArrayList();
            mResultMediaItems.add(mediaref);
            updateResultMediaItems();
        } else
        {
            Toast.makeText(fragmentactivity, getString(R.string.camera_photo_error), 1).show();
        }
        if(fragmentactivity instanceof ImageUtils.InsertCameraPhotoDialogDisplayer)
            ((ImageUtils.InsertCameraPhotoDialogDisplayer)fragmentactivity).hideInsertCameraPhotoDialog();
    }

    private static boolean isAudienceCircle(AudienceData audiencedata, int i)
    {
        boolean flag = true;
        if(audiencedata.getUserCount() != 0 || audiencedata.getCircleCount() != 1 || audiencedata.getCircle(0).getType() != i)
            flag = false;
        return flag;
    }

    private static boolean isValidCustomAudience(AudienceData audiencedata) {
        boolean flag = false;
        if(audiencedata == null) 
        	return false;
        
        boolean flag1;
        flag1 = audiencedata.isEmpty();
        if(flag1) 
        	return false; 
        int i = audiencedata.getCircleCount();
        int j = audiencedata.getUserCount();
        int k = audiencedata.getSquareTargetCount();
        if(j == 0 && k == 0 && i == 1)
        {
            int l = audiencedata.getCircle(0).getType();
            if(l == 5)
                return false;
            if(l == 8)
            	return false;
            if(l == 9)
            	return false;
        }
        if(k > 0)
        {
            boolean flag2 = Property.ENABLE_SQUARES.getBoolean();
            if(!flag2)
            	return false;
        }
        return true;
    }

    private void launchAclPicker()
    {
        FragmentActivity fragmentactivity = getActivity();
        Bundle bundle = getExtrasForLogging();
        OzViews ozviews = OzViews.getViewForLogging(fragmentactivity);
        EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.PLATFORM_SHARE_CLICKED_ACL, ozviews, bundle);
        launchActivity(Intents.getEditAudienceActivityIntent(fragmentactivity, mAccount, getString(R.string.post_edit_audience_activity_title), mAudienceView.getAudience(), 5, false, true, true, false), 2);
    }

    private void launchActivity(Intent intent, int i)
    {
        if(mFocusOverrideView != null)
            mFocusOverrideView.requestFocus();
        hideAclOverlay();
        SoftInput.hide(getView());
        if(i == 0)
            startActivity(intent);
        else
            startActivityForResult(intent, i);
    }

    private String makeLinkTitle(String s, boolean flag) {
        
    	try {
	    	String s1;
	        PackageManager packagemanager;
	        if(!flag || TextUtils.isEmpty(s))
	            return s;
	        s1 = mApiaryApiInfo.getSourceInfo().getPackageName();
	        if(TextUtils.isEmpty(s1))
	        	return s;
	        packagemanager = getActivity().getPackageManager();
	        String s2;
	        CharSequence charsequence = packagemanager.getApplicationLabel(packagemanager.getApplicationInfo(s1, 0));
	        if(TextUtils.isEmpty(charsequence))
	        	return s;
	        s2 = getResources().getString(R.string.stream_app_invite_title, new Object[] {
	            charsequence, s
	        });
	        s = s2;
	        
    	} catch (android.content.pm.PackageManager.NameNotFoundException namenotfoundexception) {
    		// FIXME
    	}
    	return s;
    }

    private void maybeExtractUrlFromString(String s) {
        if(!mLoadingMediaAttachments && mUrl == null && s != null) {
        	SpannableString spannablestring = new SpannableString(s);
            Linkify.addLinks(spannablestring, 1);
            URLSpan aurlspan[] = (URLSpan[])spannablestring.getSpans(0, spannablestring.length(), URLSpan.class);
            if(aurlspan.length > 0)
                mUrl = aurlspan[0].getURL();
        }
    }

    private void removeFromMediaGallery(MediaRef mediaref) {
        mAttachmentRefs.remove(mediaref);
        mMediaGallery.remove(mediaref);
        getView();
        updatePreviewContainer();
        updatePostUI();
    }

    private void removeFromMediaGallery(List list)
    {
        for(Iterator iterator = list.iterator(); iterator.hasNext(); removeFromMediaGallery((MediaRef)iterator.next()));
    }

    private void removeLocationListener()
    {
        if(mLocationController != null)
        {
            mLocationController.release();
            mLocationController = null;
        }
    }

    private static void setLocationText(View view, String s, String s1)
    {
        TextView textview = (TextView)view.findViewById(0x1020016);
        TextView textview1;
        if(s == null)
        {
            textview.setVisibility(4);
        } else
        {
            textview.setVisibility(0);
            textview.setText(s);
        }
        textview1 = (TextView)view.findViewById(R.id.centered_text);
        if(s1 == null)
        {
            textview1.setVisibility(8);
        } else
        {
            textview1.setVisibility(0);
            textview1.setText(s1);
        }
    }

    private void updateAudienceUI(AudienceData audiencedata)
    {
        mAudienceView.replaceAudience(audiencedata);
    }

    private void updateLocation(View view)
    {
        View view1 = view.findViewById(R.id.location_progress);
        ImageView imageview = (ImageView)view.findViewById(R.id.location_marker);
        byte byte0 = 4;
        byte byte1 = 4;
        View view2;
        if(mLocationChecked)
        {
            if(mLocation != null)
            {
                imageview.setVisibility(0);
                imageview.setImageResource(R.drawable.ic_location_active);
                byte0 = 0;
                String s = mLocation.getName();
                String s1 = mLocation.getBestAddress();
                if(!TextUtils.isEmpty(s) && !TextUtils.isEmpty(s1))
                {
                    setLocationText(view, s, null);
                } else
                {
                    setLocationText(view, mLocation.getLocationName(), null);
                    byte0 = 0;
                }
            } else
            {
                imageview.setVisibility(4);
                setLocationText(view, null, getString(R.string.finding_your_location));
                byte1 = 0;
            }
        } else
        {
            imageview.setVisibility(0);
            imageview.setImageResource(R.drawable.ic_location_grey);
            setLocationText(view, null, getString(R.string.no_location_attached));
        }
        view1.setVisibility(byte1);
        if(mRemoveLocationView != null)
            mRemoveLocationView.setVisibility(byte0);
        view2 = view.findViewById(R.id.location_marker_progress_container);
        if(view2 != null)
        {
            int i;
            if(byte1 == 0 || byte0 == 0)
                i = 0;
            else
                i = 8;
            view2.setVisibility(i);
        }
    }

    private void updatePostUI() {
    	// TODO
       /* FragmentActivity fragmentactivity = getActivity();
        if(!(fragmentactivity instanceof PostActivity)) {
        	if(fragmentactivity instanceof ShareActivity)
                ((ShareActivity)fragmentactivity).invalidateMenu(); 
        else {
        	((PostActivity)fragmentactivity).invalidateMenu();
        }*/
    }

    private void updatePreviewContainer() {
        mPreviewContainerView.removeAllViews();
        final FragmentActivity activity = getActivity();
        boolean flag = false;
        Object obj;
        int i;
        byte byte1;
        int j;
        byte byte0;
        int k;
        TextView textview;
        Resources resources;
        int l;
        Object aobj[];
        EmbedClientItem embedclientitem1;
        DbEmbedDeepLink dbembeddeeplink;
        DbEmbedMedia dbembedmedia;
        DbEmbedSkyjam dbembedskyjam;
        if(mPreviewResult != null)
        {
            EmbedClientItem embedclientitem = mPreviewResult.getEmbed(null);
            mPreviewContainerView.setBackgroundResource(R.drawable.compose_item_background);
           
            if(embedclientitem != null && embedclientitem.appInvite != null && embedclientitem.appInvite.callToAction != null && embedclientitem.appInvite.callToAction.deepLink != null)
            {
                DeepLinkData deeplinkdata = embedclientitem.appInvite.callToAction.deepLink;
                String s9 = embedclientitem.appInvite.callToAction.renderedLabel;
                dbembeddeeplink = new DbEmbedDeepLink(deeplinkdata, s9);
                embedclientitem1 = embedclientitem.appInvite.about;
            } else
            {
                embedclientitem1 = embedclientitem;
                dbembeddeeplink = null;
            }
            dbembedmedia = null;
            dbembedskyjam = null;
            if(embedclientitem1 != null)
                if(embedclientitem1.webPage != null)
                {
                    WebPage webpage = embedclientitem1.webPage;
                    dbembedmedia = new DbEmbedMedia(webpage);
                } else
                if(embedclientitem1.videoObject != null)
                {
                    VideoObject videoobject = embedclientitem1.videoObject;
                    dbembedmedia = new DbEmbedMedia(videoobject);
                    dbembedskyjam = null;
                } else
                if(embedclientitem1.playMusicAlbum != null)
                {
                    PlayMusicAlbum playmusicalbum = embedclientitem1.playMusicAlbum;
                    dbembedskyjam = new DbEmbedSkyjam(playmusicalbum);
                    dbembedmedia = null;
                } else
                if(embedclientitem1.playMusicTrack != null)
                {
                    PlayMusicTrack playmusictrack = embedclientitem1.playMusicTrack;
                    dbembedskyjam = new DbEmbedSkyjam(playmusictrack);
                    dbembedmedia = null;
                } else
                if(embedclientitem1.thing != null)
                {
                    Thing thing = embedclientitem1.thing;
                    dbembedmedia = new DbEmbedMedia(thing);
                    dbembedskyjam = null;
                } else
                {
                    EsLog.writeToLog(6, "PostFragment", "Found an embed we don't understand without a THING!");
                    dbembedmedia = null;
                    dbembedskyjam = null;
                }
            if(dbembedskyjam != null)
            {
                StreamOneUpSkyjamView streamoneupskyjamview = new StreamOneUpSkyjamView(activity);
                streamoneupskyjamview.bind(dbembedskyjam.getAlbum(), dbembedskyjam.getSong(), dbembedskyjam.getImageUrl(), dbembedskyjam.getPreviewUrl(), dbembedskyjam.getMarketUrl(), mActivityId);
                obj = streamoneupskyjamview;
                mPreviewContainerView.setBackgroundResource(R.drawable.bg_taco_mediapattern);
            } else
            if(dbembedmedia != null)
            {
                String s = dbembedmedia.getTitle();
                boolean flag1;
                String s1;
                String s2;
                String s3;
                String s4;
                String s5;
                MediaRef mediaref;
                byte byte2;
                String s7;
                LinkPreviewView linkpreviewview;
                OneUpLinkView.BackgroundViewLoadedListener backgroundviewloadedlistener;
                if(dbembeddeeplink != null)
                    flag1 = true;
                else
                    flag1 = false;
                s1 = makeLinkTitle(s, flag1);
                if(dbembedmedia.isVideo())
                    s2 = dbembedmedia.getVideoUrl();
                else
                    s2 = dbembedmedia.getContentUrl();
                s3 = LinksCardView.makeLinkUrl(s2);
                s4 = dbembedmedia.getImageUrl();
                s5 = dbembedmedia.getVideoUrl();
                if(dbembedmedia.isVideo())
                {
                    String s8 = ImageUtils.rewriteYoutubeMediaUrl(s5);
                    if(!TextUtils.equals(s5, s8))
                        s4 = s8;
                }
                if(TextUtils.isEmpty(s4))
                {
                    mediaref = null;
                    byte2 = 3;
                } else
                {
                    String s6 = dbembedmedia.getOwnerId();
                    long l1 = PrimitiveUtils.safeLong(Long.valueOf(dbembedmedia.getPhotoId()));
                    Uri uri;
                    if(dbembedmedia.isVideo())
                        uri = Uri.parse(s5);
                    else
                        uri = null;
                    mediaref = new MediaRef(s6, l1, s4, uri, dbembedmedia.getMediaType());
                    if(mediaref.getType() == MediaRef.MediaType.IMAGE)
                        byte2 = 3;
                    else
                        byte2 = 2;
                }
                if(dbembeddeeplink != null)
                    s7 = dbembeddeeplink.getLabelOrDefault(activity);
                else
                    s7 = null;
                linkpreviewview = new LinkPreviewView(activity);
                backgroundviewloadedlistener = null;
                flag = false;
                if(mediaref != null)
                {
                    flag = true;
                    backgroundviewloadedlistener = new OneUpLinkView.BackgroundViewLoadedListener() {

                        public final void onBackgroundViewLoaded(OneUpLinkView oneuplinkview)
                        {
                            if(!mLoadingUrlPreview)
                                mLoadingView.setVisibility(8);
                        }
                    };
                }
                linkpreviewview.init(mediaref, byte2, backgroundviewloadedlistener, s1, s7, null, s3);
                obj = linkpreviewview;
            } else
            {
                activity.showDialog(28199);
                flag = false;
                obj = null;
            }
        } else
        if(mEmotiShare != null) {
            EmotiShareView emotishareview = new EmotiShareView(activity);
            emotishareview.setMediaRef(mEmotiShare.getImageRef());
            emotishareview.setOnClickListener(new View.OnClickListener() {

            	public final void onClick(View view) {
                    Intent intent = Intents.getChooseEmotiShareObjectIntent(activity, mAccount, mEmotiShare);
                    EsAnalytics.recordActionEvent(activity, mAccount, OzActions.EMOTISHARE_INSERT_CLICKED, OzViews.getViewForLogging(activity), getExtrasForLogging());
                    launchActivity(intent, 5);
                }
            });
            
            emotishareview.getMissingImageView().setImageResource(R.drawable.ic_error_gold_40);
            ImageResourceView imageresourceview = emotishareview.getImageView();
            emotishareview.getView().setBackgroundResource(R.drawable.bg_taco_mediapattern);
            android.view.ViewGroup.LayoutParams layoutparams = mPreviewWrapperView.getLayoutParams();
            layoutparams.width = -1;
            layoutparams.height = getResources().getDimensionPixelOffset(R.dimen.emotishare_preview_height);
            mPreviewWrapperView.setLayoutParams(layoutparams);
            if(getResources().getConfiguration().orientation != 2)
                imageresourceview.setScaleMode(1);
            if(mRemovePreviewButton != null)
            {
                mRemovePreviewButton.setVisibility(0);
                mRemovePreviewButton.setOnClickListener(new android.view.View.OnClickListener() {

                    public final void onClick(View view)
                    {
                        view.setVisibility(8);
                        view.setOnClickListener(null);
                        mEmotiShare = null;
                        if(mCommentsView != null)
                            mCommentsView.setText(null);
                        EsAnalytics.recordActionEvent(activity, mAccount, OzActions.EMOTISHARE_REMOVED, OzViews.getViewForLogging(activity), getExtrasForLogging());
                        updatePreviewContainer();
                        updatePostUI();
                    }
                });
            }
            obj = emotishareview.getView();
            flag = false;
        } else
        {
            flag = false;
            obj = null;
        }
        if(mLoadingUrlPreview || flag)
            i = 0;
        else
            i = 8;
        j = i;
        if(obj != null)
        {
            mPreviewContainerView.addView(((View) (obj)));
            j = 0;
        }
        mPreviewContainerView.setVisibility(j);
        mLoadingView.setVisibility(i);
        mPreviewWrapperView.setVisibility(j);
        byte0 = 8;
        byte1 = 8;
        if(j != 0)
        {
            k = mMediaGalleryView.getChildCount();
            textview = mMediaCount;
            resources = getResources();
            l = R.plurals.share_photo_count;
            aobj = new Object[1];
            aobj[0] = Integer.valueOf(k);
            textview.setText(resources.getQuantityString(l, k, aobj));
            if(k > 0)
                byte0 = 0;
            else
                byte1 = 0;
        }
        mMediaContainer.setVisibility(byte0);
        mEmptyMediaView.setVisibility(byte1);
    }

    private void updateResultMediaItems()
    {
        if(mResultMediaItems != null && mResultMediaItems.size() > 0)
        {
            if(mResultMediaItems.size() + mAttachmentRefs.size() > 250)
            {
                FragmentActivity fragmentactivity = getActivity();
                int i = R.string.post_max_photos;
                Object aobj[] = new Object[1];
                aobj[0] = Integer.valueOf(250);
                Toast.makeText(fragmentactivity, getString(i, aobj), 1).show();
            } else
            {
                Iterator iterator = mResultMediaItems.iterator();
                while(iterator.hasNext()) 
                    addToMediaGallery((MediaRef)iterator.next());
            }
            mResultMediaItems.clear();
            mResultMediaItems = null;
            updatePostUI();
        }
    }

    private static void updateText(View view) {
        if(null == view) {
        	return;
        }
        
        ImageView imageview = (ImageView)view.findViewById(R.id.text_marker);
        MentionMultiAutoCompleteTextView mentionmultiautocompletetextview = (MentionMultiAutoCompleteTextView)view.findViewById(R.id.compose_text);
        if(imageview != null && mentionmultiautocompletetextview != null) {
            int i;
            if(TextUtils.isEmpty(mentionmultiautocompletetextview.getText().toString()))
                i = R.drawable.ic_text_grey;
            else
                i = R.drawable.ic_text_active;
            imageview.setImageResource(i);
        }
    }

    private void updateViews(View view)
    {
        if(view != null)
        {
            View view1 = view.findViewById(R.id.footer_separator);
            TextView textview = (TextView)view.findViewById(R.id.footer_message);
            byte byte0 = 8;
            if(mFooterMessage != null)
            {
                byte0 = 0;
                textview.setText(mFooterMessage);
            }
            view1.setVisibility(byte0);
            textview.setVisibility(byte0);
            updatePreviewContainer();
            updateText(view);
        }
    }

    public final boolean canPost() {
        Integer integer;
        boolean flag = false;
        if(null != mPendingPostId) {
        	return false;
        }
        boolean flag1;
        flag1 = mLoadingUrlPreview;
        flag = false;
        if(flag1) {
        	return false;
        }
        
        boolean flag2 = PeopleUtils.isEmpty(mAudienceView.getAudience());
        if(flag2)
        	return false;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        boolean flag6;
        boolean flag7;
        boolean flag8;
        if(mEmotiShare != null)
            flag3 = true;
        else
            flag3 = false;
        if(!TextUtils.isEmpty(mUrl))
            flag4 = true;
        else
            flag4 = false;
        if(!TextUtils.isEmpty(mContentDeepLinkId))
            flag5 = true;
        else
            flag5 = false;
        if(mCommentsView.getText().length() > 0)
            flag6 = true;
        else
            flag6 = false;
        if(mLocation != null)
            flag7 = true;
        else
            flag7 = false;
        if(!mAttachmentRefs.isEmpty())
            flag8 = true;
        else
            flag8 = false;
        if(!flag3 && !flag4 && !flag5 && !flag6 && !flag7)
        {
            if(!flag8) {
            	return false;
            }
        }

        return true;
    }

    public final void onActivityCreated(Bundle bundle) {
        boolean flag = true;
        super.onActivityCreated(bundle);
        if(bundle != null) {
        	return; 
        }
        boolean flag1;
        boolean flag2;
        if(mLocation != null)
            flag1 = flag;
        else
            flag1 = false;
        if(flag1) {
        	if(!LocationController.isProviderEnabled(getActivity())) {
        		flag = false;
        	}
        	setLocationChecked(flag);
        } else { 
        	if(mAccount.isChild())
            {
                flag2 = false;
            } else
            {
                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("streams", 0);
                if(sharedpreferences.contains("want_sharebox_locations"))
                    flag2 = sharedpreferences.getBoolean("want_sharebox_locations", false);
                else
                    flag2 = sharedpreferences.getBoolean("want_locations", false);
            }
            if(!flag2) {
            	flag = false;
            	setLocationChecked(flag);
            } else { 
            	if(!LocationController.isProviderEnabled(getActivity())) {
            		flag = false;
            	} 
            	setLocationChecked(flag);
            }
        }
    }

    public final void onActivityResult(int i, int j, Intent intent) {
    	
    	if(-1 != j) {
    		return;
    	}
    	
    	switch(i)
        {
        case 1: // '\001'
            if(j == -1 && intent != null)
                if(intent.hasExtra("insert_photo_request_id"))
                {
                    FragmentActivity fragmentactivity = getActivity();
                    mInsertCameraPhotoRequestId = Integer.valueOf(intent.getIntExtra("insert_photo_request_id", 0));
                    if(fragmentactivity instanceof ImageUtils.InsertCameraPhotoDialogDisplayer)
                        ((ImageUtils.InsertCameraPhotoDialogDisplayer)fragmentactivity).showInsertCameraPhotoDialog();
                } else
                {
                    mResultMediaItems = new ArrayList();
                    ArrayList arraylist = intent.getParcelableArrayListExtra("mediarefs");
                    int j1 = arraylist.size();
                    int k1 = 0;
                    while(k1 < j1) 
                    {
                        mResultMediaItems.add(arraylist.get(k1));
                        k1++;
                    }
                }
            break;

        case 2: // '\002'
            if(intent != null)
            {
                mResultAudience = (AudienceData)intent.getParcelableExtra("audience");
                if(mResultAudience != null && EsLog.isLoggable("PostFragment", 3))
                {
                    CircleData acircledata[] = mResultAudience.getCircles();
                    int l = acircledata.length;
                    int i1 = 0;
                    while(i1 < l) 
                    {
                        CircleData circledata = acircledata[i1];
                        Log.d("PostFragment", (new StringBuilder("Out circle id: ")).append(circledata.getId()).toString());
                        i1++;
                    }
                }
            }
            break;

        case 3: // '\003'
            if(intent != null)
            {
                mResultLocation = (DbLocation)intent.getParcelableExtra("location");
                boolean flag1;
                if(mResultLocation == null)
                    flag1 = true;
                else
                    flag1 = false;
                mRemoveLocation = flag1;
            }
            break;

        case 5: // '\005'
            mEmotiShareResult = (DbEmotishareMetadata)intent.getParcelableExtra("typed_image_embed");
            boolean flag;
            if(mEmotiShareResult == null)
                flag = true;
            else
                flag = false;
            mRemoveEmotiShare = flag;
            break;

        case 4: // '\004'
            if(j == -1 && intent.hasExtra("photo_remove_from_compose"))
            {
                android.os.Parcelable aparcelable[] = intent.getParcelableArrayExtra("photo_remove_from_compose");
                MediaRef amediaref[] = new MediaRef[aparcelable.length];
                for(int k = 0; k < aparcelable.length; k++)
                    amediaref[k] = (MediaRef)aparcelable[k];

                removeFromMediaGallery(Arrays.asList(amediaref));
            }
            break;
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle bundle1 = getArguments();
        mAccount = (EsAccount)bundle1.getParcelable("account");
        if(bundle != null) { 
        	mActivityId = bundle.getString("activity_id");
            if(bundle.containsKey("location"))
            {
                mLocation = (DbLocation)bundle.getParcelable("location");
                mLocationChecked = true;
            }
            if(bundle.containsKey("prov_location"))
                mProviderLocation = (Location)bundle.getParcelable("prov_location");
            if(bundle.containsKey("pending_request_id"))
                mPendingPostId = Integer.valueOf(bundle.getInt("pending_request_id"));
            if(bundle.containsKey("insert_camera_photo_req_id"))
                mInsertCameraPhotoRequestId = Integer.valueOf(bundle.getInt("insert_camera_photo_req_id"));
            if(bundle.containsKey("preview_result"))
                mPreviewResult = (ApiaryActivity)bundle.getParcelable("preview_result");
            if(bundle.containsKey("emotishare_result"))
                mEmotiShareResult = (DbEmotishareMetadata)bundle.getParcelable("emotishare_result");
            if(bundle.containsKey("emotishare"))
                mEmotiShare = (DbEmotishareMetadata)bundle.getParcelable("emotishare");
            if(bundle.containsKey("api_info"))
                mApiaryApiInfo = (ApiaryApiInfo)bundle.getSerializable("api_info");
            if(bundle.containsKey("footer"))
                mFooterMessage = bundle.getString("footer");
            mAttachmentRefs = bundle.getParcelableArrayList("l_attachments");
            mLoadingMediaAttachments = bundle.getBoolean("loading_attachments", false);
            if(bundle.containsKey("url"))
                mUrl = bundle.getString("url");
            if(bundle.containsKey("content_deep_link_id"))
                mContentDeepLinkId = bundle.getString("content_deep_link_id");
            if(bundle.containsKey("content_deep_link_metadata"))
                mContentDeepLinkMetadata = bundle.getBundle("content_deep_link_metadata");
            if(bundle.containsKey("call_to_action"))
                mCallToAction = (CallToActionData)bundle.getParcelable("call_to_action");
            if(bundle.containsKey("text"))
                mOriginalText = bundle.getString("text");
            mIsFromPlusOne = bundle.getBoolean("is_from_plusone", false);
            if(bundle.containsKey("public_circle"))
                mPublicCircle = (CircleData)bundle.getParcelable("public_circle");
            if(bundle.containsKey("domain_circle"))
                mDomainCircle = (CircleData)bundle.getParcelable("domain_circle");
            if(bundle.containsKey("your_circles"))
                mYourCircles = (CircleData)bundle.getParcelable("your_circles");
            if(bundle.containsKey("saved_default_audience"))
                mSavedDefaultAudience = (AudienceData)bundle.getParcelable("saved_default_audience");
            if(bundle.containsKey("default_audience"))
                mDefaultAudience = (AudienceData)bundle.getParcelable("default_audience");
            if(bundle.containsKey("audience_history"))
                mHistoryAudienceArray = bundle.getParcelableArrayList("audience_history");
 
        } else { 
        	if(bundle1.containsKey("external_id"))
                mActivityId = bundle1.getString("external_id");
            if(mActivityId == null)
                mActivityId = (new StringBuilder()).append(System.currentTimeMillis()).append(".").append(StringUtils.randomString(32)).toString();
            mLocation = getLocationFromExtras(bundle1);
            mAttachmentRefs = new ArrayList();
            if(bundle1.containsKey("android.intent.extra.STREAM"))
            {
                mAttachments = bundle1.getParcelableArrayList("android.intent.extra.STREAM");
                getLoaderManager().initLoader(R.id.post_fragment_media_ref_loader_id, null, mMediaRefLoaderCallbacks);
                mLoadingMediaAttachments = true;
            }
            if(bundle1.containsKey("url"))
                mUrl = bundle1.getString("url");
            if(bundle1.containsKey("content_deep_link_id"))
            {
                mContentDeepLinkId = bundle1.getString("content_deep_link_id");
                maybeExtractUrlFromString(mContentDeepLinkId);
            }
            if(bundle1.containsKey("content_deep_link_metadata"))
                mContentDeepLinkMetadata = bundle1.getBundle("content_deep_link_metadata");
            if(bundle1.containsKey("call_to_action"))
                mCallToAction = (CallToActionData)bundle1.getParcelable("call_to_action");
            if(bundle1.containsKey("footer"))
                mFooterMessage = bundle1.getString("footer");
            if(bundle1.containsKey("api_info"))
            {
                mApiaryApiInfo = (ApiaryApiInfo)bundle1.getSerializable("api_info");
            } else
            {
                PackageManager packagemanager = getActivity().getPackageManager();
                String s = Property.PLUS_CLIENTID.get();
                ApiaryApiInfo apiaryapiinfo = new ApiaryApiInfo(null, s, "com.google.android.apps.social", PlatformContractUtils.getCertificate("com.google.android.apps.social", packagemanager), null);
                String s1 = getActivity().getPackageName();
                mApiaryApiInfo = new ApiaryApiInfo(null, s, s1, PlatformContractUtils.getCertificate(s1, packagemanager), "", apiaryapiinfo);
            }
            
            if(bundle1.containsKey("typed_image_embed"))
                mEmotiShareResult = (DbEmotishareMetadata)bundle1.getParcelable("typed_image_embed");
            mResultAudience = (AudienceData)bundle1.getParcelable("audience");
            if(bundle1.containsKey("android.intent.extra.TEXT"))
            {
                mOriginalText = bundle1.getString("android.intent.extra.TEXT");
                maybeExtractUrlFromString(mOriginalText);
                if(mUrl != null && mOriginalText != null && mOriginalText.trim().equals(mUrl))
                    mOriginalText = null;
            }
            if(bundle1.containsKey("insert_photo_request_id"))
            {
                mInsertCameraPhotoRequestId = Integer.valueOf(bundle1.getInt("insert_photo_request_id"));
                FragmentActivity fragmentactivity = getActivity();
                if(fragmentactivity instanceof ImageUtils.InsertCameraPhotoDialogDisplayer)
                    ((ImageUtils.InsertCameraPhotoDialogDisplayer)fragmentactivity).showInsertCameraPhotoDialog();
            }
            if(mContentDeepLinkId == null || mUrl != null || hasContentDeepLinkMetadata()) {
            	mIsFromPlusOne = bundle1.getBoolean("is_from_plusone", false);
            	
            	if(mSavedDefaultAudience == null)
                    getLoaderManager().restartLoader(2, null, new CursorLoaderCallbacks());
                if(mPublicCircle == null && mDomainCircle == null && mYourCircles == null)
                    getLoaderManager().initLoader(1, null, new CursorLoaderCallbacks());
                
            } else { 
            	if(EsLog.isLoggable("PostFragment", 5))
                    Log.w("PostFragment", "Mobile deep-link IDs must specify metadata.");
                getActivity().setResult(0);
                getActivity().finish();

            }
        }
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.post_fragment, viewgroup, false);
        FragmentActivity fragmentactivity = getActivity();
        mLoadingView = view.findViewById(R.id.list_empty_progress);
        mMediaGalleryView = (ViewGroup)view.findViewById(R.id.photos_gallery);
        mMediaCount = (TextView)view.findViewById(R.id.media_count);
        mAudienceView = (AudienceView)view.findViewById(R.id.audience_view);
        mScrollView = (ScrollView)view.findViewById(R.id.mention_scroll_view);
        mCommentsView = (MentionMultiAutoCompleteTextView)view.findViewById(R.id.compose_text);
        mPreviewContainerView = (ViewGroup)view.findViewById(R.id.share_preview_container);
        mEmptyMediaView = view.findViewById(R.id.empty_media_container);
        mMediaContainer = view.findViewById(R.id.photos_container);
        mRemoveLocationView = view.findViewById(R.id.remove_location);
        mFocusOverrideView = view.findViewById(R.id.focus_override);
        mPreviewWrapperView = (ViewGroup)view.findViewById(R.id.share_preview_wrapper);
        mAclDropDown = view.findViewById(R.id.acl_overlay);
        mRemovePreviewButton = view.findViewById(R.id.remove_preview_button);
        createPublicAclButton(view, mPublicCircle);
        createYourCirclesAclButton(view, mYourCircles);
        createDomainAclButton(view, mDomainCircle);
        createDefaultAclButton(view, mSavedDefaultAudience);
        mCreateAclButton = (PostAclButtonView)view.findViewById(R.id.create_acl_button);
        mCreateAclButton.initialize(getString(R.string.post_create_custom_acl), R.drawable.ic_right);
        mCreateAclButton.setActive();
        mCreateAclButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view2)
            {
                hideAclOverlay();
                launchAclPicker();
            }
        });
        
        mCreateAclButton.setVisibility(0);
        if(Property.ENABLE_SQUARES.getBoolean())
        {
            mSquaresAclButton = (PostAclButtonView)view.findViewById(R.id.squares_acl_button);
            mSquaresAclButton.initialize(getString(R.string.square_member_item_text), R.drawable.ic_communities_grey, R.drawable.ic_communities_grey, R.drawable.ic_right);
            mSquaresAclButton.setActive();
            mSquaresAclButton.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view2)
                {
                    hideAclOverlay();
                    FragmentActivity fragmentactivity = getActivity();
                }
            });
            
            mSquaresAclButton.setVisibility(0);
        }
        mAclDropDown.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view2)
            {
                hideAclOverlay();
            }

        });
        
        mSlideInDown = AnimationUtils.loadAnimation(fragmentactivity, R.anim.slide_in_down_self);
        mSlideInDown.setInterpolator(fragmentactivity, R.anim.decelerate_interpolator);
        mSlideInDown.setDuration(250L);
        mSlideOutUp = AnimationUtils.loadAnimation(fragmentactivity, R.anim.slide_out_up_self);
        mSlideOutUp.setInterpolator(fragmentactivity, R.anim.accelerate_interpolator);
        mSlideOutUp.setDuration(250L);
        mSlideInDown.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation) {
            }

            public final void onAnimationRepeat(Animation animation) {
            }

            public final void onAnimationStart(Animation animation) {
                if(mAudienceView instanceof TextOnlyAudienceView)
                    ((TextOnlyAudienceView)mAudienceView).setChevronDirection(TextOnlyAudienceView.ChevronDirection.POINT_UP);
                if(mAclDropDown != null)
                    mAclDropDown.setVisibility(0);
            }
        });
        
        mSlideOutUp.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation) {
                if(mAclDropDown != null)
                    mAclDropDown.setVisibility(8);
            }

            public final void onAnimationRepeat(Animation animation) {
            }

            public final void onAnimationStart(Animation animation) {
                if(mAudienceView instanceof TextOnlyAudienceView)
                    ((TextOnlyAudienceView)mAudienceView).setChevronDirection(TextOnlyAudienceView.ChevronDirection.POINT_DOWN);
            }
        });
        
        mCommentsView.setOnClickListener(onClickListener);
        mScrollView.setOnClickListener(onClickListener);
        if(android.os.Build.VERSION.SDK_INT < 11)
            mMediaGalleryView.setOnCreateContextMenuListener(this);
        mMediaGallery = new MediaGallery(fragmentactivity, mAttachmentRefs, mMediaGalleryView);
        if(!(getActivity() instanceof ShareActivity))
            mCommentsView.setMinLines(fragmentactivity.getResources().getInteger(R.integer.compose_text_min_lines_big));
        mCommentsView.init(this, mAccount, null, mAudienceView);
        mCommentsView.addTextChangedListener(mTextWatcher);
        mCommentsView.setOnEditorActionListener(onEditorActionListener);
        if(bundle == null) {
            try {
                mCommentsView.setText(mOriginalText);
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
        mCommentsView.addTextChangedListener(new TextWatcher() {

            public final void afterTextChanged(Editable editable)
            {
                updatePostUI();
                PostFragment.updateText(getView());
            }

            public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }
        });
        
        mAudienceView.setAudienceChangedCallback(new Runnable() {

            public final void run()
            {
                updatePostUI();
            }
        });
        
        mAudienceView.setAccount(mAccount);
        mAudienceView.findViewById(R.id.audience_button).setOnClickListener(onClickListener);
        mAudienceView.findViewById(R.id.chevron_icon).setOnClickListener(onClickListener);
        if(mAudienceView instanceof TextOnlyAudienceView)
        {
            TextOnlyAudienceView textonlyaudienceview = (TextOnlyAudienceView)mAudienceView;
            textonlyaudienceview.setChevronDirection(TextOnlyAudienceView.ChevronDirection.POINT_DOWN);
            textonlyaudienceview.setChevronVisibility(0);
        }
        view.findViewById(R.id.location_view).setOnClickListener(onClickListener);
        view.findViewById(R.id.choose_media).setOnClickListener(onClickListener);
        view.findViewById(R.id.empty_media).setOnClickListener(onClickListener);
        ResourceRedirector.getInstance();
        if(Property.ENABLE_EMOTISHARE.getBoolean())
        {
            View view1 = view.findViewById(R.id.empty_emotishare);
            view1.setOnClickListener(onClickListener);
            view1.setVisibility(0);
            view.findViewById(R.id.vertical_separator).setVisibility(0);
        }
        mMediaContainer.setOnClickListener(onClickListener);
        mRemoveLocationView.setOnClickListener(onClickListener);
        mPreviewWrapperView.setVisibility(8);
        if(mAttachmentRefs.isEmpty() && !mLoadingMediaAttachments && mUrl != null)
        {
            getLoaderManager().initLoader(3, Bundle.EMPTY, new CursorLoaderCallbacks());
            mLoadingUrlPreview = true;
        }
        if(mContentDeepLinkId != null && mUrl == null && hasContentDeepLinkMetadata())
            handlePreviewResult(new ServiceResult(), ApiaryActivityFactory.getApiaryActivity(mContentDeepLinkMetadata, mCallToAction));
        if(mUrl != null || mContentDeepLinkId != null)
            mEmptyMediaView.setVisibility(8);
        updateLocation(view);
        updatePostUI();
        updateViews(view);
        if(bundle == null)
            if(getActivity().getIntent().getBooleanExtra("start_editing", false))
                mCommentsView.requestFocus();
            else
                mFocusOverrideView.requestFocus();
        return view;
    }

    public final void onDestroyView()
    {
        mCommentsView.destroy();
        mCommentsView = null;
        super.onDestroyView();
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
        if("quit".equals(s))
        {
            OzActions ozactions;
            FragmentActivity fragmentactivity;
            if(mIsFromPlusOne)
                ozactions = OzActions.PLATFORM_CANCEL_SHARE_FROM_PLUSONE;
            else
                ozactions = OzActions.PLATFORM_CANCEL_SHARE;
            fragmentactivity = getActivity();
            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, ozactions, OzViews.getViewForLogging(fragmentactivity), getExtrasForLogging());
            getActivity().finish();
        }
    }

    public final void onDiscard(boolean flag)
    {
        SoftInput.hide(mCommentsView);
        if(!flag && mAclDropDown != null && mAclDropDown.getVisibility() == 0)
        {
            mAclDropDown.startAnimation(mSlideOutUp);
        } else
        {
            String s = mCommentsView.getText().toString();
            String s1;
            boolean flag1;
            if(mOriginalText != null)
                s1 = mOriginalText;
            else
                s1 = "";
            if(!s.equals(s1))
                flag1 = true;
            else
            if(mAttachmentRefs.size() > 0)
                flag1 = true;
            else
                flag1 = false;
            if(flag1)
            {
                AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.app_name), getString(R.string.post_quit_question), getString(R.string.yes), getString(R.string.no));
                alertfragmentdialog.setTargetFragment(this, 0);
                alertfragmentdialog.show(getFragmentManager(), "quit");
            } else
            {
                OzActions ozactions;
                FragmentActivity fragmentactivity;
                if(mIsFromPlusOne)
                    ozactions = OzActions.PLATFORM_CANCEL_SHARE_FROM_PLUSONE;
                else
                    ozactions = OzActions.PLATFORM_CANCEL_SHARE;
                fragmentactivity = getActivity();
                EsAnalytics.recordActionEvent(fragmentactivity, mAccount, ozactions, OzViews.getViewForLogging(fragmentactivity), getExtrasForLogging());
                getActivity().finish();
            }
        }
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
        removeLocationListener();
    }

    public final void onResume() {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mResultAudience != null) {
            getView();
            updateAudienceUI(mResultAudience);
            updatePostUI();
            mResultAudience = null;
        }
        if(mPendingPostId != null && !EsService.isRequestPending(mPendingPostId.intValue())) {
            ServiceResult serviceresult = EsService.removeResult(mPendingPostId.intValue());
            handlePostResult(mPendingPostId.intValue(), serviceresult);
        }
        if(mInsertCameraPhotoRequestId != null && !EsService.isRequestPending(mInsertCameraPhotoRequestId.intValue())) {
            EsService.removeResult(mInsertCameraPhotoRequestId.intValue());
            insertCameraPhoto(EsService.getLastCameraMediaLocation());
            mInsertCameraPhotoRequestId = null;
        }
        if(LocationController.isProviderEnabled(getActivity())) {
            if(mLocationChecked && !EsAccountsData.hasSeenLocationDialog(getActivity(), mAccount))
                getActivity().showDialog(0x1d71d84);
            if(mLocationChecked && mLocation == null)
                addLocationListener();
            if(mResultLocation != null || mRemoveLocation) {
                mLocation = mResultLocation;
                boolean flag;
                if(!mRemoveLocation)
                    flag = true;
                else
                    flag = false;
                setLocationChecked(flag);
                mResultLocation = null;
                mRemoveLocation = false;
            }
        } else {
            mResultLocation = null;
            mRemoveLocation = false;
            setLocationChecked(false);
        }
        if(mEmotiShareResult != null || mRemoveEmotiShare) {
            mEmotiShare = mEmotiShareResult;
            mRemoveEmotiShare = false;
            mEmotiShareResult = null;
            if(mRemoveEmotiShare) {
                EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.EMOTISHARE_REMOVED, OzViews.getViewForLogging(getActivity()), getExtrasForLogging());
                mCommentsView.setText(null);
            } else {
                mCommentsView.setText(mEmotiShare.getShareText());
            }
        }
        updateLocation(getView());
        updatePreviewContainer();
        updatePostUI();
        updateResultMediaItems();
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("activity_id", mActivityId);
        if(mLocation != null)
            bundle.putParcelable("location", mLocation);
        if(mProviderLocation != null)
            bundle.putParcelable("prov_location", mProviderLocation);
        if(mPendingPostId != null)
            bundle.putInt("pending_request_id", mPendingPostId.intValue());
        if(mPreviewResult != null)
            bundle.putParcelable("preview_result", mPreviewResult);
        if(mEmotiShareResult != null)
            bundle.putParcelable("emotishare_result", mEmotiShareResult);
        if(mEmotiShare != null)
            bundle.putParcelable("emotishare", mEmotiShare);
        if(mApiaryApiInfo != null)
            bundle.putSerializable("api_info", mApiaryApiInfo);
        if(mFooterMessage != null)
            bundle.putSerializable("footer", mFooterMessage);
        if(mAttachmentRefs != null)
            bundle.putParcelableArrayList("l_attachments", (ArrayList)mAttachmentRefs);
        bundle.putBoolean("loading_attachments", mLoadingMediaAttachments);
        if(mUrl != null)
            bundle.putString("url", mUrl);
        if(mContentDeepLinkId != null)
            bundle.putString("content_deep_link_id", mContentDeepLinkId);
        if(mContentDeepLinkMetadata != null)
            bundle.putBundle("content_deep_link_metadata", mContentDeepLinkMetadata);
        if(mCallToAction != null)
            bundle.putParcelable("call_to_action", mCallToAction);
        if(mOriginalText != null)
            bundle.putString("text", mOriginalText);
        if(mInsertCameraPhotoRequestId != null)
            bundle.putInt("insert_camera_photo_req_id", mInsertCameraPhotoRequestId.intValue());
        if(mIsFromPlusOne)
            bundle.putBoolean("is_from_plusone", true);
        if(mPublicCircle != null)
            bundle.putParcelable("public_circle", mPublicCircle);
        if(mDomainCircle != null)
            bundle.putParcelable("domain_circle", mDomainCircle);
        if(mYourCircles != null)
            bundle.putParcelable("your_circles", mYourCircles);
        if(mSavedDefaultAudience != null)
            bundle.putParcelable("saved_default_audience", mSavedDefaultAudience);
        if(mDefaultAudience != null)
            bundle.putParcelable("default_audience", mDefaultAudience);
        if(mHistoryAudienceArray != null)
            bundle.putParcelableArrayList("audience_history", mHistoryAudienceArray);
    }

    public final boolean post() {
        boolean flag;
        if(mPendingPostId != null || mLoadingUrlPreview) {
            flag = false;
        } else {
            FragmentActivity fragmentactivity = getActivity();
            Bundle bundle = getExtrasForLogging();
            OzActions ozactions;
            OzViews ozviews;
            AudienceData audiencedata;
            Editable editable;
            if(mIsFromPlusOne)
                ozactions = OzActions.PLATFORM_CONFIRM_SHARE_FROM_PLUSONE;
            else
                ozactions = OzActions.PLATFORM_CONFIRM_SHARE;
            ozviews = OzViews.getViewForLogging(fragmentactivity);
            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, ozactions, ozviews, bundle);
            SoftInput.hide(mCommentsView);
            audiencedata = mAudienceView.getAudience();
            editable = mCommentsView.getText();
            if(PeopleUtils.isEmpty(audiencedata)) {
                launchAclPicker();
                flag = false;
            } else {
                boolean flag1;
                if(audiencedata.getSquareTargetCount() != 0 && audiencedata.getSquareTarget(0).getSquareStreamId() == null)
                    flag1 = true;
                else
                    flag1 = false;
                if(flag1) {
                    SquareTargetData squaretargetdata = audiencedata.getSquareTarget(0);
                    launchActivity(Intents.getSelectSquareCategoryActivityIntent(getActivity(), mAccount, squaretargetdata.getSquareName(), squaretargetdata.getSquareId(), squaretargetdata.getSquareName()), 2);
                    flag = false;
                } else {
                    boolean flag2;
                    boolean flag3;
                    boolean flag4;
                    boolean flag5;
                    boolean flag6;
                    boolean flag7;
                    if(mEmotiShare != null)
                        flag2 = true;
                    else
                        flag2 = false;
                    if(mUrl != null)
                        flag3 = true;
                    else
                        flag3 = false;
                    if(mContentDeepLinkId != null)
                        flag4 = true;
                    else
                        flag4 = false;
                    if(editable.length() > 0)
                        flag5 = true;
                    else
                        flag5 = false;
                    if(mLocation != null)
                        flag6 = true;
                    else
                        flag6 = false;
                    if(!mAttachmentRefs.isEmpty())
                        flag7 = true;
                    else
                        flag7 = false;
                    if(!flag2 && !flag3 && !flag4 && !flag5 && !flag6 && !flag7)
                    {
                        Toast.makeText(fragmentactivity, getResources().getString(R.string.share_body_empty), 0).show();
                        flag = false;
                    } else
                    {
                        ProgressFragmentDialog.newInstance(null, getString(R.string.post_operation_pending), false).show(getFragmentManager(), "req_pending");
                        if(flag7)
                            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.PLATFORM_SHARE_POST_WITH_ATTACHMENT, ozviews, bundle);
                        if(flag5)
                            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.PLATFORM_SHARE_POST_WITH_COMMENT, ozviews, bundle);
                        if(flag6)
                            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.PLATFORM_SHARE_POST_WITH_LOCATION, ozviews, bundle);
                        if(flag3)
                            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.PLATFORM_SHARE_POST_WITH_URL, ozviews, bundle);
                        if(flag4)
                            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.PLATFORM_SHARE_POST_WITH_DEEP_LINK, ozviews, bundle);
                        EsAccount _tmp = mAccount;
                        String s = ApiUtils.buildPostableString(editable);
                        com.galaxy.meetup.client.android.api.BirthdayData birthdaydata;
                        boolean flag8;
                        DbEmbedEmotishare dbembedemotishare;
                        AnalyticsInfo analyticsinfo;
                        Bundle bundle1;
                        FragmentActivity fragmentactivity1;
                        FragmentActivity fragmentactivity2;
                        EsAccount esaccount;
                        ApiaryApiInfo apiaryapiinfo;
                        ApiaryActivity apiaryactivity;
                        String s1;
                        List arraylist;
                        DbLocation dblocation;
                        String s2;
                        Bundle bundle2;
                        OzActions ozactions1;
                        if(flag2)
                        {
                            if(flag5 && TextUtils.equals(s, mEmotiShare.getShareText()))
                                ozactions1 = OzActions.EMOTISHARE_TEXT_UNMODIFIED;
                            else
                                ozactions1 = OzActions.EMOTISHARE_TEXT_MODIFIED;
                            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, ozactions1, ozviews, bundle);
                            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.PLATFORM_CONFIRM_SHARE, ozviews, bundle);
                        }
                        analyticsinfo = new AnalyticsInfo(OzViews.SHARE, OzViews.PLATFORM_THIRD_PARTY_APP, System.currentTimeMillis(), PlatformContractUtils.getCallingPackageAnalytics(mApiaryApiInfo));
                        bundle1 = getExtrasForLogging();
                        fragmentactivity1 = getActivity();
                        if(!TextUtils.isEmpty(s))
                            EsAnalytics.recordActionEvent(fragmentactivity1, mAccount, OzActions.PLATFORM_SHARE_COMMENT_ADDED, OzViews.getViewForLogging(fragmentactivity1), bundle1);
                        if(audiencedata.getCircleCount() > 0)
                            EsAnalytics.recordActionEvent(fragmentactivity1, mAccount, OzActions.PLATFORM_CIRCLES_SHARE_ACL_ADDED, OzViews.getViewForLogging(fragmentactivity1), bundle1);
                        if(audiencedata.getUserCount() > 0)
                            EsAnalytics.recordActionEvent(fragmentactivity1, mAccount, OzActions.PLATFORM_PEOPLE_SHARE_ACL_ADDED, OzViews.getViewForLogging(fragmentactivity1), bundle1);
                        if("com.google.android.apps.plus.GOOGLE_BIRTHDAY_POST".equals(fragmentactivity.getIntent().getAction()))
                        {
                            bundle2 = getArguments();
                            if(bundle2 != null)
                                birthdaydata = (com.galaxy.meetup.client.android.api.BirthdayData)bundle2.getParcelable("birthday_data");
                            else
                                birthdaydata = null;
                        } else
                        {
                            birthdaydata = null;
                        }
                        fragmentactivity2 = getActivity();
                        esaccount = mAccount;
                        apiaryapiinfo = mApiaryApiInfo;
                        apiaryactivity = mPreviewResult;
                        s1 = mActivityId;
                        arraylist = mAttachmentRefs;
                        dblocation = mLocation;
                        s2 = mContentDeepLinkId;
                        if("com.google.android.apps.plus.GOOGLE_BIRTHDAY_POST".equals(getActivity().getIntent().getAction()))
                            flag8 = false;
                        else
                            flag8 = true;
                        if(mEmotiShare == null)
                            dbembedemotishare = null;
                        else
                            dbembedemotishare = mEmotiShare.getEmbed();
                        mPendingPostId = Integer.valueOf(EsService.postActivity(fragmentactivity2, esaccount, analyticsinfo, apiaryapiinfo, apiaryactivity, audiencedata, s1, s, arraylist, dblocation, s2, flag8, birthdaydata, dbembedemotishare));
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    public final void setLocationChecked(boolean flag) {
        boolean flag1;
        FragmentActivity fragmentactivity;
        mLocationChecked = flag;
        flag1 = mLocationChecked;
        fragmentactivity = getActivity();
        if(!flag1) {
        	removeLocationListener();
            mLocation = null;
            mProviderLocation = null; 
        } else { 
        	if(LocationController.isProviderEnabled(fragmentactivity)) {
        		if(isResumed())
                {
                    addLocationListener();
                    if(!EsAccountsData.hasSeenLocationDialog(fragmentactivity, mAccount))
                        fragmentactivity.showDialog(0x1d71d84);
                }
        	} else {
        		fragmentactivity.showDialog(0x1bfb7a8);
        	}
        }
        updateLocation(getView());
        updatePostUI();
        return;
    }
    
    
    
    
    
    
    
    
    static boolean access$1300(PostFragment postfragment) {
        boolean flag;
        if(postfragment.getActivity().getIntent().getParcelableExtra("location") != null)
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    static void access$1800(PostFragment postfragment) {
        AudienceData audiencedata;
        if(postfragment.mPublicAclButton != null)
            if(isAudienceCircle(postfragment.mAudienceView.getAudience(), 9))
                postfragment.mPublicAclButton.setActive();
            else
                postfragment.mPublicAclButton.setInactive();
        if(postfragment.mDomainAclButton != null)
            if(isAudienceCircle(postfragment.mAudienceView.getAudience(), 8))
                postfragment.mDomainAclButton.setActive();
            else
                postfragment.mDomainAclButton.setInactive();
        if(postfragment.mYourCirclesAclButton != null)
            if(isAudienceCircle(postfragment.mAudienceView.getAudience(), 5))
                postfragment.mYourCirclesAclButton.setActive();
            else
                postfragment.mYourCirclesAclButton.setInactive();
        audiencedata = ((TextOnlyAudienceView)postfragment.mAudienceView).getAudience();
        if(postfragment.mDefaultAclButton != null)
            if(postfragment.mDefaultAudience != null && compareAudiences(postfragment.mDefaultAudience, audiencedata))
                postfragment.mDefaultAclButton.setActive();
            else
                postfragment.mDefaultAclButton.setInactive();
        if(postfragment.mHistoryAclButtonArray != null && postfragment.mHistoryAudienceArray != null)
        {
            int i = 0;
            while(i < postfragment.mHistoryAclButtonArray.length) 
            {
                PostAclButtonView postaclbuttonview = postfragment.mHistoryAclButtonArray[i];
                AudienceData audiencedata1 = (AudienceData)postfragment.mHistoryAudienceArray.get(i);
                if(postaclbuttonview != null)
                    if(audiencedata1 != null && compareAudiences(audiencedata1, audiencedata))
                        postaclbuttonview.setActive();
                    else
                        postaclbuttonview.setInactive();
                i++;
            }
        }
        if(postfragment.mAclDropDown != null && postfragment.mAclDropDown.getVisibility() != 0)
        {
            SoftInput.hide(postfragment.getView());
            postfragment.mAclDropDown.startAnimation(postfragment.mSlideInDown);
        }
        return;
    }
    
    static boolean access$3600(PostFragment postfragment, AudienceData audiencedata) {
        int i;
        int j;
        if(postfragment.mHistoryAudienceArray == null) {
        	return false;
        }
            
        i = postfragment.mHistoryAudienceArray.size();
        j = 0;
        
        while(j < i) {
        	if(compareAudiences(audiencedata, (AudienceData)postfragment.mHistoryAudienceArray.get(j))) 
        		return true; 
        	
        	j++;
        }
        
        return false;
    }
    
    static void access$3900(PostFragment postfragment, List arraylist, View view, int ai[]) {
    	
    	if(null == arraylist || null == ai || 0 == ai.length) {
    		return;
    	}
    	
    	int j;
        int k;
        PostAclButtonView postaclbuttonview;
        int l;
        final AudienceData clickAudience;
        int i;
        AudienceData audiencedata;
        if(arraylist == null)
            i = 0;
        else
            i = arraylist.size();
        postfragment.mHistoryAclButtonArray = new PostAclButtonView[ai.length];
        postfragment.mHistoryAudienceArray = new ArrayList(ai.length);
        j = 0;
        k = 0;
    	
    		
       // TODO
    }
    
    
    
    
    
    
    
    
    
    
    
    
	//==========================================================================================
    //
    ///=========================================================================================

	private static interface AccountStatusQuery {

		public static final String PROJECTION[] = { "audience_data",
				"audience_history" };

	}

	private static interface CirclesQuery {

		public static final String PROJECTION[] = { "_id", "circle_name",
				"circle_id", "type", "contact_count" };

	}

	private final class CursorLoaderCallbacks implements
			android.support.v4.app.LoaderManager.LoaderCallbacks {

		public final Loader onCreateLoader(int i, Bundle bundle) {

			Loader loader = null;
			if (1 == i) {
				loader = new CircleListLoader(getActivity(), mAccount, 13,
						CirclesQuery.PROJECTION);
			} else if (2 == i) {
				loader = new EsCursorLoader(getActivity(),
						EsProvider.appendAccountParameter(
								EsProvider.ACCOUNT_STATUS_URI, mAccount),
						AccountStatusQuery.PROJECTION, null, null, null);
			} else if (3 == i) {
				PreviewCursorLoader previewcursorloader = new PreviewCursorLoader(
						getActivity());
				previewcursorloader.setUri(EsApiProvider.makePreviewUri(mApiaryApiInfo));
				PreviewRequestData previewrequestdata = new PreviewRequestData(mUrl, mCallToAction);
				String as[] = new String[1];
				JSONArray jsonarray = new JSONArray();
				jsonarray.put(previewrequestdata.uri.toString());
				if (previewrequestdata.callToAction != null) {
					Object obj1;
					Object obj2;
					Object obj3;
					if (previewrequestdata.callToAction.mLabel != null)
						obj1 = previewrequestdata.callToAction.mLabel;
					else
						obj1 = JSONObject.NULL;
					jsonarray.put(obj1);
					if (previewrequestdata.callToAction.mUrl != null)
						obj2 = previewrequestdata.callToAction.mUrl;
					else
						obj2 = JSONObject.NULL;
					jsonarray.put(obj2);
					if (previewrequestdata.callToAction.mDeepLinkId != null)
						obj3 = previewrequestdata.callToAction.mDeepLinkId;
					else
						obj3 = JSONObject.NULL;
					jsonarray.put(obj3);
				}
				as[0] = jsonarray.toString();
				previewcursorloader.setSelectionArgs(as);
				loader = previewcursorloader;
			}
			return loader;
		}

	    public final void onLoadFinished(Loader loader, Object obj) {
	        Cursor cursor = (Cursor)obj;
	        switch(loader.getId()) {
		        case 1:
		        	if(cursor == null)
		                break;
		            cursor.moveToPosition(-1);
		            do
		            {
		                if(!cursor.moveToNext())
		                    break;
		                String s2 = cursor.getString(1);
		                String s3 = cursor.getString(2);
		                int j = cursor.getInt(3);
		                int k = cursor.getInt(4);
		                if(j == 9)
		                {
		                    String s5 = getResources().getString(R.string.acl_public);
		                    createPublicAclButton(getView(), new CircleData(s3, j, s5, k));
		                } else
		                if(j == 8)
		                {
		                    if(!TextUtils.isEmpty(s2))
		                        createDomainAclButton(getView(), new CircleData(s3, j, s2, k));
		                } else
		                if(j == 5)
		                {
		                    String s4 = getResources().getString(R.string.acl_your_circles);
		                    createYourCirclesAclButton(getView(), new CircleData(s3, j, s4, k));
		                }
		            } while(true);
		        	break;
		        case 2:
		        	byte abyte1[];
                    List arraylist;
		        	if(cursor != null && cursor.moveToFirst())
		            {
		                byte abyte0[] = cursor.getBlob(0);
		                PostFragment postfragment;
	                    View view;
	                    int ai[];
	                    AudienceData audiencedata1;
		                if(abyte0 != null)
		                {
		                    AudienceData audiencedata = DbAudienceData.deserialize(abyte0);
		                    PostFragment postfragment1 = PostFragment.this;
		                    View view1 = getView();
		                    if(PostFragment.isValidCustomAudience(audiencedata) && !PostFragment.access$3600(PostFragment.this, audiencedata))
		                        audiencedata1 = audiencedata;
		                    else
		                        audiencedata1 = null;
		                    postfragment1.createDefaultAclButton(view1, audiencedata1);
		                    if(audiencedata != null && !audiencedata.isEmpty() && mAudienceView != null)
		                    {
		                        AudienceData audiencedata2 = mAudienceView.getAudience();
		                        if(!mAudienceView.isEdited() && audiencedata2.isEmpty())
		                            updateAudienceUI(audiencedata);
		                    }
		                }
		                abyte1 = cursor.getBlob(1);
		                if(abyte1 != null)
		                {
		                    arraylist = DbAudienceData.deserializeList(abyte1);
		                    if(arraylist != null)
		                    {
		                        postfragment = PostFragment.this;
		                        view = getView();
		                        ai = new int[2];
		                        ai[0] = R.id.local_acl_button1;
		                        ai[1] = R.id.local_acl_button2;
		                        PostFragment.access$3900(postfragment, arraylist, view, ai);
		                    }
		                }
		            }
		        
		        	break;
		        case 3:
		        	PreviewCursorLoader previewcursorloader = (PreviewCursorLoader)loader;
		            mLoadingUrlPreview = false;
		            if(!previewcursorloader.isCachedData())
		            {
		                previewcursorloader.setCachedData(true);
		                if(cursor != null && cursor.getExtras() != null)
		                {
		                    Bundle bundle = cursor.getExtras();
		                    int i = bundle.getInt("com.google.circles.platform.result.extra.ERROR_CODE", 200);
		                    String s = bundle.getString("com.google.circles.platform.result.extra.ERROR_MESSAGE");
		                    String s1;
		                    android.os.Parcelable aparcelable[];
		                    ApiaryActivity apiaryactivity;
		                    ServiceResult serviceresult;
		                    OzActions ozactions;
		                    FragmentActivity fragmentactivity;
		                    if(s == null)
		                        s1 = "Ok";
		                    else
		                        s1 = s;
		                    aparcelable = bundle.getParcelableArray("com.google.android.apps.content.EXTRA_ACTIVITY");
		                    if(aparcelable != null && aparcelable.length > 0)
		                        apiaryactivity = (ApiaryActivity)aparcelable[0];
		                    else
		                        apiaryactivity = null;
		                    serviceresult = new ServiceResult(i, s1, null);
		                    if(!serviceresult.hasError())
		                        ozactions = OzActions.PLATFORM_SHARE_PREVIEW_SHOWN;
		                    else
		                        ozactions = OzActions.PLATFORM_SHARE_PREVIEW_ERROR;
		                    fragmentactivity = getActivity();
		                    EsAnalytics.recordActionEvent(fragmentactivity, mAccount, ozactions, OzViews.getViewForLogging(fragmentactivity), getExtrasForLogging());
		                    handlePreviewResult(serviceresult, apiaryactivity);
		                }
		            } else
		            {
		                mLoadingView.setVisibility(8);
		            }
		        	break;
	        	default:
	        		break;
	        }
	    }

	    public final void onLoaderReset(Loader loader) {
	    	if(3 == loader.getId()) {
	    		mLoadingView.setVisibility(8);
	    	}
	    }
	}

	private static class LinkPreviewView extends OneUpLinkView {

		protected final int getMinExposureLand() {
			return sMinExposureLand;
		}

		protected final int getMinExposurePort() {
			return sMinExposurePort;
		}

		private static boolean sLinkPreviewViewInitialized;
		private static int sMinExposureLand;
		private static int sMinExposurePort;

		public LinkPreviewView(Context context) {
			super(context);
			if (!sLinkPreviewViewInitialized) {
				sLinkPreviewViewInitialized = true;
				Resources resources = context.getResources();
				sMinExposureLand = resources
						.getDimensionPixelOffset(R.dimen.share_preview_margin_top_landscape);
				sMinExposurePort = resources
						.getDimensionPixelOffset(R.dimen.share_preview_margin_top_portrait);
			}
		}
	}

	private final class MediaGallery {

		private ViewGroup mGalleryView;
		private List mImages;
		private final LayoutInflater mLayoutInflater;

		public MediaGallery(Context context, List arraylist, ViewGroup viewgroup) {
			super();
			mImages = new ArrayList();
			mGalleryView = viewgroup;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService("layout_inflater");
			if (arraylist != null) {
				int i = arraylist.size();
				for (int j = 0; j < i; j++)
					add((MediaRef) arraylist.get(j));

			}
		}

		public final void add(final MediaRef mediaref) {
			mImages.add(mediaref);
			View view = mLayoutInflater.inflate(
					R.layout.compose_gallery_image_container, null);
			AlbumColumnGridItemView albumcolumngriditemview = (AlbumColumnGridItemView) view.findViewById(R.id.image);
			albumcolumngriditemview.setTag(mediaref);
			albumcolumngriditemview.setMediaRef(mediaref);
			albumcolumngriditemview.setOnClickListener(onClickListener);
			((ImageButton) view.findViewById(R.id.remove_image_button))
					.setOnClickListener(new View.OnClickListener() {

						public final void onClick(View view) {
							hideAclOverlay();
							view.setOnClickListener(null);
							removeFromMediaGallery(mediaref);
							updatePostUI();
						}
					});
			mGalleryView.addView(view);
		}
		
		public final void remove(MediaRef mediaref) {
            mImages.remove(mediaref);
            int i = mGalleryView.getChildCount();
            for(int j = 0; j < i; j++) {
                View view = mGalleryView.getChildAt(j);
                if(((AlbumColumnGridItemView)view.findViewById(R.id.image)).getMediaRef().equals(mediaref)) {
                	mGalleryView.removeView(view);
                	view.setOnClickListener(null);
                }
            }
                  
        }
	}

	private static final class MediaRefLoader extends AsyncTaskLoader {

		private final EsAccount mAccount;
		private final List mLoadedList = new ArrayList();
		private final List mMediaRefList;

		public MediaRefLoader(Context context, EsAccount esaccount, List arraylist) {
			super(context);
			mMediaRefList = arraylist;
			mAccount = esaccount;
		}

		public final Object loadInBackground() {
			String s = mAccount.getName();
			String s1 = mAccount.getGaiaId();
			Context context = getContext();
			android.content.ContentResolver contentresolver = context
					.getContentResolver();
			Iterator iterator = mMediaRefList.iterator();
			do {
				if (!iterator.hasNext())
					break;
				MediaRef mediaref = (MediaRef) iterator.next();
				String s2 = mediaref.getUrl();
				Uri uri;
				if (s2 == null)
					uri = null;
				else
					uri = Uri.parse(s2);
				if (GalleryUtils.isGalleryContentUri(uri)) {
					Long long1 = Long.valueOf(GalleryUtils.getPhotoId(context,
							uri));
					if (long1.longValue() != 0L) {
						String s4 = GalleryUtils.getAccountName(context, uri);
						if (!TextUtils.isEmpty(s4) && s4.equalsIgnoreCase(s)) {
							String s5 = ImageUtils.getMimeType(contentresolver,
									uri);
							boolean flag1;
							MediaRef.MediaType mediatype1;
							MediaRef mediaref2;
							if (!TextUtils.isEmpty(s5)
									&& s5.startsWith("video/"))
								flag1 = true;
							else
								flag1 = false;
							if (flag1)
								mediatype1 = MediaRef.MediaType.VIDEO;
							else
								mediatype1 = MediaRef.MediaType.IMAGE;
							mediaref2 = new MediaRef(s1, long1.longValue(), s2,
									mediaref.getLocalUri(), mediatype1);
							mLoadedList.add(mediaref2);
						}
					}
				} else if (mediaref.hasLocalUri()) {
					String s3 = ImageUtils.getMimeType(contentresolver,
							mediaref.getLocalUri());
					boolean flag;
					MediaRef.MediaType mediatype;
					MediaRef mediaref1;
					if (!TextUtils.isEmpty(s3) && s3.startsWith("video/"))
						flag = true;
					else
						flag = false;
					if (flag)
						mediatype = MediaRef.MediaType.VIDEO;
					else
						mediatype = MediaRef.MediaType.IMAGE;
					mediaref1 = new MediaRef(mediaref.getOwnerGaiaId(),
							mediaref.getPhotoId(), mediaref.getUrl(),
							mediaref.getLocalUri(), mediatype);
					mLoadedList.add(mediaref1);
				} else if (mediaref.hasUrl() || mediaref.hasPhotoId())
					mLoadedList.add(mediaref);
			} while (true);
			return mLoadedList;
		}

		protected final void onStartLoading() {
			if (mLoadedList.size() == 0)
				forceLoad();
		}
	}

	private final class PostLocationListener implements LocationListener {

		public final void onLocationChanged(Location location) {
			removeLocationListener();
			if (mLocation == null) {
				mProviderLocation = location;
				PostFragment postfragment = PostFragment.this;
				DbLocation dblocation;
				if (getCityLevelLocationPreference())
					dblocation = LocationController
							.getCityLevelLocation(location);
				else
					dblocation = LocationController
							.getStreetLevelLocation(location);
				postfragment.mLocation = dblocation;
				updatePostUI();
				updateLocation(getView());
			}
		}

		public final void onProviderDisabled(String s) {
		}

		public final void onProviderEnabled(String s) {
		}

		public final void onStatusChanged(String s, int i, Bundle bundle) {
		}
	}

	private static final class PreviewCursorLoader extends EsCursorLoader {

		private boolean mCachedData;

		public PreviewCursorLoader(Context context) {
			super(context);
		}

		public final Cursor esLoadInBackground() {
			mCachedData = false;
			return super.esLoadInBackground();
		}

		public final boolean isCachedData() {
			return mCachedData;
		}

		public final void setCachedData(boolean flag) {
			mCachedData = true;
		}

	}

	private final class ServiceListener extends EsServiceListener {

	    public final void onInsertCameraPhotoComplete(int i, ServiceResult serviceresult) {
	        if(mInsertCameraPhotoRequestId != null && mInsertCameraPhotoRequestId.intValue() == i) {
	            insertCameraPhoto(EsService.getLastCameraMediaLocation());
	            mInsertCameraPhotoRequestId = null;
	        }
	    }
	
	    public final void onPostActivityResult(int i, ServiceResult serviceresult) {
	        handlePostResult(i, serviceresult);
	    }
	}

}
