/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.Dates;

/**
 * 
 * @author sihai
 *
 */
public class EventActivityUpdateCardLayout extends CardViewLayout implements android.view.View.OnClickListener {

	private static int sAvatarMarginLeft;
    private static int sAvatarMarginTop;
    private static int sAvatarSize;
    private static int sDescriptionMarginBottom;
    private static int sDescriptionMarginLeft;
    private static int sDescriptionMarginRight;
    private static float sDescriptionTopAvatarHeightPercentage;
    private static boolean sInitialized;
    private AvatarView mAvatarView;
    private EventActionListener mListener;
    private CardTitleDescriptionView mTextDescriptionView;
    private EventUpdate mUpdate;
    
    public EventActivityUpdateCardLayout(Context context)
    {
        super(context);
    }

    public EventActivityUpdateCardLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public EventActivityUpdateCardLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public final void bind(EventUpdate eventupdate, EventActionListener eventactionlistener, boolean flag)
    {
        if(eventupdate != null)
        {
            mUpdate = eventupdate;
            mTextDescriptionView.setText(mUpdate.ownerName, Dates.getRelativeTimeSpanString(getContext(), mUpdate.timestamp), mUpdate.comment, flag);
            if(!TextUtils.isEmpty(mUpdate.gaiaId))
                mAvatarView.setGaiaId(mUpdate.gaiaId);
            mListener = eventactionlistener;
            mTextDescriptionView.setListener(mListener);
        }
    }

    protected final void init(Context context, AttributeSet attributeset, int i)
    {
        super.init(context, attributeset, i);
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sDescriptionMarginLeft = resources.getDimensionPixelSize(R.dimen.event_card_activity_text_margin_left);
            sDescriptionMarginRight = resources.getDimensionPixelSize(R.dimen.event_card_activity_text_margin_right);
            sDescriptionMarginBottom = resources.getDimensionPixelSize(R.dimen.event_card_activity_padding_bottom);
            sAvatarMarginLeft = resources.getDimensionPixelSize(R.dimen.event_card_activity_avatar_margin_left);
            sAvatarMarginTop = resources.getDimensionPixelSize(R.dimen.event_card_activity_avatar_magin_top);
            sDescriptionTopAvatarHeightPercentage = resources.getDimension(R.dimen.event_card_activity_text_top_avatar_percentage);
            sAvatarSize = resources.getDimensionPixelSize(R.dimen.event_card_activity_avatar_size);
            sInitialized = true;
        }
        addPadding(sAvatarMarginLeft, sAvatarMarginTop, sDescriptionMarginRight, sDescriptionMarginBottom);
        mAvatarView = new AvatarView(context, attributeset, i);
        mAvatarView.setRounded(true);
        mAvatarView.setOnClickListener(this);
        addView(mAvatarView);
        mTextDescriptionView = new CardTitleDescriptionView(context, attributeset, i);
        addView(mTextDescriptionView);
        setOnClickListener(this);
    }

    protected void measureChildren(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        int i1 = k + 0;
        int j1 = l + 0;
        mAvatarView.measure(android.view.View.MeasureSpec.makeMeasureSpec(sAvatarSize, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(sAvatarSize, 0x40000000));
        setCorner(mAvatarView, 0, 0);
        int k1 = 0 + Math.round(sDescriptionTopAvatarHeightPercentage * (float)mAvatarView.getMeasuredHeight());
        int l1 = 0 + mAvatarView.getMeasuredWidth() + sDescriptionMarginLeft;
        int i2 = i1 - l1;
        int j2 = j1 - k1;
        boolean flag;
        CardTitleDescriptionView cardtitledescriptionview;
        int k2;
        int l2;
        boolean flag1;
        if(android.view.View.MeasureSpec.getMode(j) == 0)
            flag = true;
        else
            flag = false;
        cardtitledescriptionview = mTextDescriptionView;
        k2 = android.view.View.MeasureSpec.makeMeasureSpec(i2, 0x40000000);
        if(flag)
            l2 = 0;
        else
            l2 = 0x80000000;
        cardtitledescriptionview.measure(k2, android.view.View.MeasureSpec.makeMeasureSpec(j2, l2));
        setCorner(mTextDescriptionView, l1, k1);
        if(!flag)
            flag1 = true;
        else
            flag1 = false;
        setClickable(flag1);
    }

    public void onClick(View view)
    {
        if(mListener != null)
            if(view instanceof AvatarView)
                mListener.onAvatarClicked(((AvatarView)view).getGaiaId());
            else
                mListener.onUpdateCardClicked(mUpdate);
    }

    public void onRecycle()
    {
        super.onRecycle();
        mListener = null;
        mTextDescriptionView.setText(null, null, null, false);
        mTextDescriptionView.setListener(null);
        mAvatarView.setGaiaId(null);
    }
}
