/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.Dates;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.DataPhoto;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EventActivityPhotoCardLayout extends CardViewLayout implements
		OnClickListener {

	private static int sAvatarMarginBottom;
    private static int sAvatarMarginLeft;
    private static int sAvatarSize;
    private static int sImageMarginBottom;
    private static boolean sInitialized;
    private static int sTextMarginLeft;
    private static int sTextMarginRight;
    private AvatarView mAvatarView;
    private DataPhoto mDataPhoto;
    private String mGaiaId;
    private ImageResourceView mImageResourceView;
    private EventActionListener mListener;
    private MediaRef mMediaRef;
    private boolean mPending;
    private TextView mPendingTextView;
    private byte mPhotoData[];
    private CardTitleDescriptionView mTextDescriptionView;
    
    public EventActivityPhotoCardLayout(Context context)
    {
        super(context);
        mPending = false;
    }

    public EventActivityPhotoCardLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mPending = false;
    }

    public EventActivityPhotoCardLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mPending = false;
    }

    public final void bind(String s, String s1, long l, String s2, byte abyte0[], EventActionListener eventactionlistener, 
            String s3)
    {
        mPhotoData = abyte0;
        mDataPhoto = (DataPhoto)JsonUtil.fromByteArray(mPhotoData, DataPhoto.class);
        mPending = false;
        MediaRef.MediaType mediatype;
        ImageResourceView imageresourceview;
        byte byte0;
        TextView textview;
        boolean flag;
        int i;
        if(PrimitiveUtils.safeBoolean(mDataPhoto.isPanorama))
            mediatype = MediaRef.MediaType.PANORAMA;
        else
        if(mDataPhoto.video != null)
        {
            mediatype = MediaRef.MediaType.VIDEO;
            mPending = TextUtils.equals(mDataPhoto.video.status, "PENDING");
        } else
        {
            mediatype = MediaRef.MediaType.IMAGE;
        }
        if(!mPending)
        {
            mMediaRef = new MediaRef(s2, mediatype);
            mImageResourceView.setMediaRef(mMediaRef, true);
        }
        imageresourceview = mImageResourceView;
        if(mPending)
            byte0 = 8;
        else
            byte0 = 0;
        imageresourceview.setVisibility(byte0);
        textview = mPendingTextView;
        flag = mPending;
        i = 0;
        if(!flag)
            i = 8;
        textview.setVisibility(i);
        mTextDescriptionView.setText(s, Dates.getRelativeTimeSpanString(getContext(), l), null, true);
        if(!TextUtils.isEmpty(s1))
            mAvatarView.setGaiaId(s1);
        mGaiaId = s1;
        mListener = eventactionlistener;
        if(mPending && mListener != null)
            mListener.onPhotoUpdateNeeded(mDataPhoto.owner.id, mDataPhoto.id, s3);
    }

    public final void init(Context context, AttributeSet attributeset, int i)
    {
        super.init(context, attributeset, i);
        if(!sInitialized)
        {
            Resources resources1 = context.getResources();
            sTextMarginLeft = resources1.getDimensionPixelSize(R.dimen.event_card_activity_text_margin_left);
            sTextMarginRight = resources1.getDimensionPixelSize(R.dimen.event_card_activity_text_margin_right);
            sImageMarginBottom = resources1.getDimensionPixelSize(R.dimen.event_card_activity_photo_margin_bottom);
            sAvatarSize = resources1.getDimensionPixelSize(R.dimen.event_card_activity_avatar_size);
            sAvatarMarginLeft = resources1.getDimensionPixelSize(R.dimen.event_card_activity_avatar_margin_left);
            sAvatarMarginBottom = resources1.getDimensionPixelSize(R.dimen.event_card_activity_photo_avatar_margin_bottom);
            sInitialized = true;
        }
        mImageResourceView = new ImageResourceView(context, attributeset, i);
        mImageResourceView.setScaleMode(1);
        mImageResourceView.setSizeCategory(3);
        mImageResourceView.setOnClickListener(this);
        addView(mImageResourceView);
        mTextDescriptionView = new CardTitleDescriptionView(context, attributeset, i);
        addView(mTextDescriptionView);
        Resources resources = context.getResources();
        mPendingTextView = new TextView(context, attributeset, i);
        mPendingTextView.setBackgroundColor(resources.getColor(R.color.event_card_photo_pending_background_color));
        mPendingTextView.setTextColor(resources.getColor(R.color.event_card_photo_pending_text_color));
        mPendingTextView.setGravity(17);
        mPendingTextView.setText(resources.getString(R.string.card_event_photo_missing_video));
        addView(mPendingTextView);
        mAvatarView = new AvatarView(context, attributeset, i);
        mAvatarView.setOnClickListener(this);
        mAvatarView.setRounded(true);
        mAvatarView.setAvatarSize(2);
        addView(mAvatarView);
    }

    protected void measureChildren(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        int i1 = l - sImageMarginBottom;
        int j1 = i1 + 0;
        int k1;
        int l1;
        int i2;
        int j2;
        int k2;
        int l2;
        int i3;
        if(mPending)
        {
            setCorner(mPendingTextView, 0, 0);
            mPendingTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(i1, 0x40000000));
        } else
        {
            setCorner(mImageResourceView, 0, 0);
            mImageResourceView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(i1, 0x40000000));
        }
        k1 = 0 + sAvatarMarginLeft;
        l1 = k1 + sAvatarSize;
        i2 = (l + 0) - sAvatarMarginBottom - sAvatarSize;
        setCorner(mAvatarView, k1, i2);
        mAvatarView.measure(android.view.View.MeasureSpec.makeMeasureSpec(sAvatarSize, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(sAvatarSize, 0x40000000));
        j2 = l1 + sTextMarginLeft;
        k2 = k - j2 - sTextMarginRight;
        l2 = l - i1;
        mTextDescriptionView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k2, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l2, 0x80000000));
        i3 = j1 + Math.max(0, (l2 - mTextDescriptionView.getMeasuredHeight()) / 2);
        setCorner(mTextDescriptionView, j2, i3);
    }

    public void onClick(View view)
    {
        if(view != mAvatarView || mListener == null) {
        	if(view == mImageResourceView && mListener != null && mPhotoData != null)
                mListener.onPhotoClicked(mDataPhoto.id, mDataPhoto.original.url, mGaiaId); 
        } else { 
        	mListener.onAvatarClicked(((AvatarView)view).getGaiaId());
        }
    }

    public void onRecycle()
    {
        super.onRecycle();
        mImageResourceView.onRecycle();
        mListener = null;
        mGaiaId = null;
        mPhotoData = null;
        mTextDescriptionView.clear();
    }
}
