/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsLocalPageData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.ui.view.AvatarView;
import com.galaxy.meetup.client.android.ui.view.LocalReviewListItemView;
import com.galaxy.meetup.client.util.MapUtils;
import com.galaxy.meetup.server.client.domain.GoogleReviewProto;
import com.galaxy.meetup.server.client.domain.SimpleProfile;

/**
 * 
 * @author sihai
 *
 */
public class LocalReviewFragment extends HostedFragment implements
		LoaderCallbacks, OnClickListener {

	private EsAccount mAccount;
    private ViewGroup mContainer;
    private Activity mContext;
    private String mPersonId;
    private SimpleProfile mProfile;
    private GoogleReviewProto mReview;
    private int mReviewIndex;
    private int mReviewType;
    
    public LocalReviewFragment()
    {
    }

    public final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.UNKNOWN;
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mContext = activity;
    }

    public void onClick(View view)
    {
        int i = view.getId();
        if(i == R.id.author_avatar) {
        	String s2 = ((AvatarView)view).getGaiaId();
            if(!TextUtils.isEmpty(s2))
                startActivity(Intents.getProfileActivityByGaiaIdIntent(getActivity(), mAccount, s2, null));
        } else if(i == R.id.more_reviews_text) {
        	String s = mReview.author.profileId;
            String s1 = (new StringBuilder("http://maps.google.com/maps?q=*+by:")).append(Uri.encode(s)).toString();
            MapUtils.launchMapsActivity(mContext, Uri.parse(s1));
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Bundle bundle1 = new Bundle();
        bundle1.putString("person_id", mPersonId);
        getLoaderManager().initLoader(100, bundle1, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        if(Log.isLoggable("LocalReviewFragment", 3))
            Log.d("LocalReviewFragment", "Loader<ProfileAndContactData> onCreateLoader()");
        return new ProfileLoader(getActivity(), mAccount, bundle.getString("person_id"), true);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        mContainer = (ViewGroup)layoutinflater.inflate(R.layout.local_review_fragment, viewgroup, false);
        AvatarView avatarview = (AvatarView)mContainer.findViewById(R.id.author_avatar);
        avatarview.setRounded(true);
        avatarview.setAvatarSize(2);
        return mContainer;
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        mProfile = ((EsPeopleData.ProfileAndContactData)obj).profile;
        SimpleProfile simpleprofile;
        String s;
        ViewGroup viewgroup;
        GoogleReviewProto googlereviewproto;
        LocalReviewListItemView localreviewlistitemview;
        TextView textview;
        String s1;
        View view;
        View view1;
        View view2;
        View view3;
        View view4;
        if(mReviewType == 0)
            mReview = (GoogleReviewProto)EsLocalPageData.getReviews(mProfile).get(mReviewIndex);
        else
        if(mReviewType == 1)
            mReview = EsLocalPageData.getYourReview(mProfile);
        else
        if(mReviewType == 2)
            mReview = (GoogleReviewProto)EsLocalPageData.getCircleReviews(mProfile).get(mReviewIndex);
        simpleprofile = mProfile;
        if(simpleprofile.page.localInfo.paper.title != null)
            s = simpleprofile.page.localInfo.paper.title.linkedTitle.text;
        else
            s = null;
        getActionBar().showTitle(getString(R.string.local_review_title, new Object[] {
            s
        }));
        viewgroup = mContainer;
        googlereviewproto = mReview;
        localreviewlistitemview = (LocalReviewListItemView)viewgroup.findViewById(R.id.local_review_item);
        localreviewlistitemview.setTopBorderVisible(false);
        localreviewlistitemview.setIsFullText(true);
        localreviewlistitemview.setReview(googlereviewproto);
        localreviewlistitemview.setAuthorAvatarOnClickListener(this);
        textview = (TextView)mContainer.findViewById(R.id.more_reviews_text);
        s1 = mReview.author.profileLink.text;
        textview.setText(getString(R.string.local_review_more_reviews, new Object[] {
            s1
        }));
        view = mContainer.findViewById(R.id.more_reviews_top_border);
        view1 = mContainer.findViewById(R.id.more_reviews_icon);
        view2 = mContainer.findViewById(R.id.more_reviews_text);
        view3 = mContainer.findViewById(R.id.number_of_reviews);
        view4 = mContainer.findViewById(R.id.more_reviews_bottom_border);
        view.setVisibility(8);
        view1.setVisibility(8);
        view2.setVisibility(8);
        view3.setVisibility(8);
        view4.setVisibility(8);
        view2.setOnClickListener(this);
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    protected final void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mAccount = (EsAccount)bundle.getParcelable("account");
        mPersonId = bundle.getString("person_id");
        mReviewType = bundle.getInt("local_review_type");
        mReviewIndex = bundle.getInt("local_review_index");
    }

    public final void recordNavigationAction()
    {
    }
}
