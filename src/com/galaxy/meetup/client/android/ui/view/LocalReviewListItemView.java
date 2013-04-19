/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.List;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.SpannableUtils;
import com.galaxy.meetup.server.client.domain.AuthorProto;
import com.galaxy.meetup.server.client.domain.GoogleReviewProto;
import com.galaxy.meetup.server.client.domain.ZagatAspectRatingProto;

/**
 * 
 * @author sihai
 *
 */
public class LocalReviewListItemView extends RelativeLayout {

	private AvatarView mAuthorAvatar;
    private TextView mAuthorName;
    private boolean mIsFullText;
    private TextView mPublishDate;
    private TextView mRatingAspects;
    private TextView mReviewText;
    private View mTopBorder;
    
    public LocalReviewListItemView(Context context)
    {
        this(context, null);
    }

    public LocalReviewListItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public void onFinishInflate()
    {
        mTopBorder = findViewById(R.id.top_border);
        mAuthorAvatar = (AvatarView)findViewById(R.id.author_avatar);
        mAuthorName = (TextView)findViewById(R.id.author_name);
        mPublishDate = (TextView)findViewById(R.id.publish_date);
        mRatingAspects = (TextView)findViewById(R.id.rating_aspects);
        mReviewText = (TextView)findViewById(R.id.review_text);
    }

    public void setAuthorAvatarOnClickListener(android.view.View.OnClickListener onclicklistener)
    {
        mAuthorAvatar.setOnClickListener(onclicklistener);
    }

    public void setIsFullText(boolean flag)
    {
        mIsFullText = flag;
    }

    public void setReview(GoogleReviewProto googlereviewproto) {
    	String s;
        int i;
        List list;
        ZagatAspectRatingProto zagataspectratingproto;
        AuthorProto authorproto1;
        if(googlereviewproto.author != null)
        {
            AuthorProto authorproto = googlereviewproto.author;
            if(!TextUtils.isEmpty(authorproto.profileId))
            {
                mAuthorAvatar.setGaiaId(authorproto.profileId);
            } else
            {
                mAuthorAvatar.setGaiaId(null);
                mAuthorAvatar.setOnClickListener(null);
            }
            authorproto1 = googlereviewproto.author;
            if(authorproto1.profileLink != null && !TextUtils.isEmpty(authorproto1.profileLink.text))
            {
                mAuthorName.setVisibility(0);
                mAuthorName.setText(authorproto1.profileLink.text);
            } else
            {
                mAuthorName.setVisibility(8);
            }
        }
        s = googlereviewproto.publishDate;
        if(!TextUtils.isEmpty(s))
        {
            mPublishDate.setVisibility(0);
            mPublishDate.setText(s);
        } else
        {
            mPublishDate.setVisibility(8);
        }
        if(googlereviewproto.zagatAspectRatings != null && googlereviewproto.zagatAspectRatings.aspectRating != null && googlereviewproto.zagatAspectRatings.aspectRating.size() > 0)
            list = googlereviewproto.zagatAspectRatings.aspectRating;
        else
            list = null;
        String s1;
        String s2;
        if(list != null)
        {
            SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder();
            for(i = 0; i < list.size(); i++)
            {
                zagataspectratingproto = (ZagatAspectRatingProto)list.get(i);
                if(TextUtils.isEmpty(zagataspectratingproto.labelDisplay) || TextUtils.isEmpty(zagataspectratingproto.valueDisplay))
                    continue;
                SpannableUtils.appendWithSpan(spannablestringbuilder, zagataspectratingproto.labelDisplay, new TextAppearanceSpan(getContext(), R.style.ProfileLocalUserRating_AspectLabel));
                spannablestringbuilder.append("\240");
                SpannableUtils.appendWithSpan(spannablestringbuilder, zagataspectratingproto.valueDisplay, new TextAppearanceSpan(getContext(), R.style.ProfileLocalUserRating_AspectValue));
                SpannableUtils.appendWithSpan(spannablestringbuilder, "\240/\2403", new TextAppearanceSpan(getContext(), R.style.ProfileLocalUserRating_AspectExplanation));
                if(i != -1 + list.size())
                    spannablestringbuilder.append("  ");
            }

            if(spannablestringbuilder.length() > 0)
            {
                mRatingAspects.setVisibility(0);
                mRatingAspects.setText(spannablestringbuilder);
            } else
            {
                mRatingAspects.setVisibility(8);
            }
        } else
        {
            mRatingAspects.setVisibility(8);
        }
        s1 = googlereviewproto.snippet;
        if(mIsFullText && !TextUtils.isEmpty(googlereviewproto.fullText))
            s1 = googlereviewproto.fullText;
        if(!TextUtils.isEmpty(s1))
        {
            mReviewText.setVisibility(0);
            s2 = s1.replaceAll("\\<.*?>", "");
            mReviewText.setText(s2);
        } else
        {
            mReviewText.setVisibility(8);
        }
    }

    public void setTopBorderVisible(boolean flag)
    {
        if(flag)
            mTopBorder.setVisibility(0);
        else
            mTopBorder.setVisibility(8);
    }
}
