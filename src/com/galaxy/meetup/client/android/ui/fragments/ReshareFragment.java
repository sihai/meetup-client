/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.StreamAdapter;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.ApiUtils;
import com.galaxy.meetup.client.android.api.OzServerException;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.AvatarRequest;
import com.galaxy.meetup.client.android.content.DbAudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageCache.ImageConsumer;
import com.galaxy.meetup.client.android.service.ImageCache.OnAvatarChangeListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.view.AudienceView;
import com.galaxy.meetup.client.android.ui.view.MentionMultiAutoCompleteTextView;
import com.galaxy.meetup.client.util.MentionTokenizer;
import com.galaxy.meetup.client.util.PeopleUtils;
import com.galaxy.meetup.client.util.SoftInput;

/**
 * 
 * @author sihai
 *
 */
public class ReshareFragment extends AudienceFragment implements LoaderCallbacks,
		OnEditorActionListener, AlertDialogListener, ImageConsumer,
		OnAvatarChangeListener {

	private static Bitmap sAuthorBitmap;
    private EsAccount mAccount;
    private String mActivityId;
    private String mAuthorId;
    private ImageCache mAvatarCache;
    private AvatarRequest mAvatarRequest;
    private ImageView mAvatarView;
    private MentionMultiAutoCompleteTextView mEditor;
    private boolean mLimited;
    private Integer mPendingRequestId;
    private TextView mReshareInfo;
    private ScrollView mScrollView;
    private final EsServiceListener mServiceListener = new ServiceListener();
    private final TextWatcher mTextWatcher = new TextWatcher() {
    	
    	final MentionTokenizer mentionTokenizer = new MentionTokenizer();
    	
        public final void afterTextChanged(Editable editable)
        {
        }

        public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
        {
        }

        public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
        {
            if(mEditor != null) {
            	int l = mEditor.getSelectionEnd();
                if(mentionTokenizer.findTokenStart(charsequence, l) + mEditor.getThreshold() <= l)
                {
                    int i1 = (int)getActivity().getResources().getDimension(R.dimen.plus_mention_suggestion_min_space);
                    int ai[] = new int[2];
                    mEditor.getLocationOnScreen(ai);
                    Rect rect = new Rect();
                    getView().getWindowVisibleDisplayFrame(rect);
                    int j1 = ai[1] + mEditor.getCursorYPosition();
                    if(rect.height() - j1 < i1)
                        mScrollView.smoothScrollTo(0, mEditor.getCursorYTop());
                }
            }
        }
    };
    
    
    
    public ReshareFragment()
    {
    }

    private void handleServiceCallback(int i, ServiceResult serviceresult)
    {
        if(mPendingRequestId != null && mPendingRequestId.intValue() == i)
        {
            mPendingRequestId = null;
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            if(serviceresult != null && serviceresult.hasError())
            {
                Exception exception = serviceresult.getException();
                if((exception instanceof OzServerException) && ((OzServerException)exception).getErrorCode() == 14)
                {
                    AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.post_not_sent_title), getString(R.string.post_restricted_mention_error), getString(R.string.ok), null);
                    alertfragmentdialog.setTargetFragment(getTargetFragment(), 0);
                    alertfragmentdialog.show(getFragmentManager(), "StreamPostRestrictionsNotSupported");
                } else
                {
                    Toast.makeText(getActivity(), R.string.reshare_post_error, 0).show();
                }
            } else
            {
                getActivity().finish();
            }
        }
    }

    public void onAvatarChanged(String s)
    {
        if(s != null && s.equals(String.valueOf(mAuthorId)))
            mAvatarCache.refreshImage(this, mAvatarRequest);
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Intent intent = getActivity().getIntent();
        mAccount = (EsAccount)intent.getParcelableExtra("account");
        mActivityId = intent.getStringExtra("activity_id");
        mLimited = intent.getBooleanExtra("limited", false);
        mAvatarCache = ImageCache.getInstance(getActivity());
        if(sAuthorBitmap == null)
            sAuthorBitmap = EsAvatarData.getTinyDefaultAvatar(getActivity(), true);
        byte byte0;
        if(mLimited)
            byte0 = 9;
        else
            byte0 = 5;
        setCirclesUsageType(byte0);
        setIncludePhoneOnlyContacts(false);
        setIncludePlusPages(true);
        if(bundle != null && bundle.containsKey("reshare_request_id"))
            mPendingRequestId = Integer.valueOf(bundle.getInt("reshare_request_id"));
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	
    	Loader loader = null;
    	if(2 == i) {
    		loader = new EsCursorLoader(getActivity(), EsProvider.appendAccountParameter(EsProvider.ACCOUNT_STATUS_URI, mAccount), AccountStatusQuery.PROJECTION, null, null, null);
    	} else if(3 == i) {
    		android.net.Uri.Builder builder = EsProvider.ACTIVITY_VIEW_BY_ACTIVITY_ID_URI.buildUpon();
            builder.appendPath(mActivityId);
            EsProvider.appendAccountParameter(builder, mAccount);
            loader = new EsCursorLoader(getActivity(), builder.build(), StreamAdapter.StreamQuery.PROJECTION_ACTIVITY, null, null, null);
    	}
    	return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.reshare_fragment, viewgroup, false);
        mAvatarView = (ImageView)view.findViewById(R.id.reshare_avatar);
        mReshareInfo = (TextView)view.findViewById(R.id.reshare_info);
        return view;
    }

    public final void onDestroyView()
    {
        mEditor.removeTextChangedListener(mTextWatcher);
        mEditor.destroy();
        mEditor = null;
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
            getActivity().finish();
    }

    public final void onDiscard()
    {
        SoftInput.hide(mEditor);
        boolean flag;
        if(!TextUtils.isEmpty(mEditor.getText()))
            flag = true;
        else
            flag = false;
        if(flag)
        {
            AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.reshare_title), getString(R.string.post_quit_question), getString(R.string.yes), getString(R.string.no));
            alertfragmentdialog.setTargetFragment(this, 0);
            alertfragmentdialog.show(getFragmentManager(), "quit");
        } else
        {
            getActivity().finish();
        }
    }

    public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent) {
        if(textview != mEditor) {
        	return false;
        }
        if(6 == i) {
        	SoftInput.hide(textview);
        	return true;
        }
        return false;
    }

    public final void onLoadFinished(Loader loader, Cursor cursor) {
        int id = loader.getId();
        if(2 == id) {
        	if(!mAudienceView.isEdited() && cursor != null && cursor.moveToFirst())
            {
                byte abyte0[] = cursor.getBlob(0);
                if(abyte0 != null)
                {
                    AudienceData audiencedata = DbAudienceData.deserialize(abyte0);
                    mAudienceView.setDefaultAudience(audiencedata);
                }
            }
        } else if(3 == id) {
        	FragmentActivity fragmentactivity = getActivity();
            if(cursor != null && cursor.moveToFirst())
            {
                String s;
                if(!TextUtils.isEmpty(cursor.getString(18)))
                {
                    mAuthorId = cursor.getString(18);
                    s = cursor.getString(19);
                } else
                {
                    mAuthorId = cursor.getString(2);
                    s = cursor.getString(3);
                }
                mReshareInfo.setText(getString(R.string.originally_shared, new Object[] {
                    s
                }).toUpperCase());
                mAvatarRequest = new AvatarRequest(mAuthorId, 0, true);
                mAvatarCache.loadImage(this, mAvatarRequest);
            } else
            {
                EsService.getActivity(fragmentactivity, mAccount, mActivityId, null);
            }
        }
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        onLoadFinished(loader, (Cursor)obj);
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onPause()
    {
        EsService.unregisterListener(mServiceListener);
        ImageCache _tmp = mAvatarCache;
        ImageCache.unregisterAvatarChangeListener(this);
        super.onPause();
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        ImageCache _tmp = mAvatarCache;
        ImageCache.registerAvatarChangeListener(this);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingRequestId.intValue());
            handleServiceCallback(mPendingRequestId.intValue(), serviceresult);
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mPendingRequestId != null)
            bundle.putInt("reshare_request_id", mPendingRequestId.intValue());
    }

    public final void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        mScrollView = (ScrollView)view.findViewById(R.id.mention_scroll_view);
        mEditor = (MentionMultiAutoCompleteTextView)view.findViewById(R.id.reshare_text);
        mEditor.init(this, mAccount, null, mAudienceView);
        mEditor.setOnEditorActionListener(this);
        mEditor.addTextChangedListener(mTextWatcher);
        mSearchListAdapter.setShowPersonNameDialog(false);
        getLoaderManager().initLoader(3, null, this);
        if(!mLimited)
            getLoaderManager().initLoader(2, null, this);
    }

    public final boolean reshare()
    {
        boolean flag = false;
        SoftInput.hide(mEditor);
        AudienceData audiencedata = getAudience();
        if(PeopleUtils.isEmpty(audiencedata))
        {
            mAudienceView.performClick();
        } else
        {
            FragmentActivity fragmentactivity = getActivity();
            EsAccount _tmp = mAccount;
            String s = ApiUtils.buildPostableString(mEditor.getText());
            mPendingRequestId = Integer.valueOf(EsService.reshareActivity(fragmentactivity, mAccount, mActivityId, s, audiencedata));
            ProgressFragmentDialog.newInstance(null, getString(R.string.post_operation_pending), false).show(getFragmentManager(), "req_pending");
            FragmentActivity fragmentactivity1 = getActivity();
            FragmentActivity fragmentactivity2 = getActivity();
            Bundle bundle = null;
            if(fragmentactivity2 != null)
            {
                AudienceView audienceview = mAudienceView;
                bundle = null;
                if(audienceview != null)
                {
                    AudienceData audiencedata1 = mAudienceView.getAudience();
                    bundle = null;
                    if(audiencedata1 != null)
                    {
                        int i = audiencedata1.getSquareTargetCount();
                        bundle = null;
                        if(i > 0)
                        {
                            bundle = new Bundle();
                            bundle.putString("extra_square_id", mAudienceView.getAudience().getSquareTarget(0).getSquareId());
                        }
                    }
                }
            }
            OzViews ozviews = OzViews.getViewForLogging(fragmentactivity1);
            EsAnalytics.recordActionEvent(fragmentactivity1, mAccount, OzActions.RESHARE, ozviews, bundle);
            flag = true;
        }
        return flag;
    }

    public void setBitmap(Bitmap bitmap, boolean flag)
    {
        if(bitmap == null)
            mAvatarView.setImageBitmap(sAuthorBitmap);
        else
            mAvatarView.setImageBitmap(bitmap);
    }

    protected final void setupAudienceClickListener()
    {
        mAudienceView.setOnClickListener(this);
    }
    
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private static interface AccountStatusQuery
    {

        public static final String PROJECTION[] = {
            "audience_data"
        };

    }

    private final class ServiceListener extends EsServiceListener
    {

        public final void onReshareActivity(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }
    }
}
