/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsEventData;

/**
 * 
 * @author sihai
 *
 */
public class AvatarLineupLayout extends ExactLayout implements android.view.View.OnClickListener {

	private static int sAvatarLineupItemPadding;
    private static int sAvatarLineupItemSize;
    private static int sDescriptionFontColor;
    private static float sDescriptionFontSize;
    private static boolean sInitialized;
    private ArrayList mAvatars;
    private EventActionListener mListener;
    private TextView mOverflowText;
    private ArrayList mPeople;
    private int mTotalPeopleCount;
    
    public AvatarLineupLayout(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public AvatarLineupLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset, 0);
    }

    public AvatarLineupLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset, i);
    }

    private void init(Context context, AttributeSet attributeset, int i)
    {
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sAvatarLineupItemPadding = resources.getDimensionPixelSize(R.dimen.event_card_avatar_lineup_item_padding);
            sAvatarLineupItemSize = resources.getDimensionPixelSize(R.dimen.event_card_avatar_lineup_item_size);
            sDescriptionFontSize = resources.getDimension(R.dimen.event_card_avatar_lineup_overflow_text_size);
            sDescriptionFontColor = resources.getColor(R.color.avatar_lineup_overflow_text_color);
            sInitialized = true;
        }
        mOverflowText = TextViewUtils.createText(context, attributeset, i, sDescriptionFontSize, sDescriptionFontColor, false, true);
        addView(mOverflowText);
        mAvatars = new ArrayList();
    }

    public final void bind(ArrayList arraylist, EventActionListener eventactionlistener, int i)
    {
        ArrayList arraylist1 = new ArrayList();
        for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); arraylist1.add(((EsEventData.EventPerson)iterator.next()).gaiaId));
        mPeople = arraylist1;
        mListener = eventactionlistener;
        mTotalPeopleCount = i;
        requestLayout();
    }

    public final void bindIds(ArrayList arraylist, EventActionListener eventactionlistener, int i)
    {
        mPeople = arraylist;
        mListener = eventactionlistener;
        mTotalPeopleCount = i;
        requestLayout();
    }

    public final void clear()
    {
        mPeople.clear();
        int i = mAvatars.size();
        for(int j = 0; j < i; j++)
        {
            AvatarView avatarview = (AvatarView)mAvatars.get(j);
            avatarview.setVisibility(8);
            avatarview.setGaiaId(null);
            removeView(avatarview);
        }

        mAvatars.clear();
        mListener = null;
    }

    protected void measureChildren(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = sAvatarLineupItemSize + sAvatarLineupItemPadding;
        int i1 = k / l;
        int j1 = mPeople.size();
        int k1 = 0;
        AvatarView avatarview;
        boolean flag;
        int k2;
        int j2 = 0;
        if(i1 < j1 || mTotalPeopleCount > j1)
        {
            do
            {
                int l1 = mTotalPeopleCount - j1;
                Resources resources = getContext().getResources();
                int i2 = R.plurals.event_invitee_other_count;
                Object aobj[] = new Object[1];
                aobj[0] = Integer.valueOf(l1);
                String s = resources.getQuantityString(i2, l1, aobj);
                mOverflowText.setText(s);
                mOverflowText.measure(android.view.View.MeasureSpec.makeMeasureSpec(0, 0), android.view.View.MeasureSpec.makeMeasureSpec(sAvatarLineupItemSize, 0x80000000));
                j2 = k - j1 * l;
                if(mPeople.size() > 1 && j2 < mOverflowText.getMeasuredWidth())
                    flag = true;
                else
                    flag = false;
                if(flag)
                    j1--;
            } while(flag);
            mOverflowText.measure(android.view.View.MeasureSpec.makeMeasureSpec(j2, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(sAvatarLineupItemSize, 0x80000000));
            mOverflowText.setVisibility(0);
            setCorner(mOverflowText, l * j1, Math.max(0, (sAvatarLineupItemSize - mOverflowText.getMeasuredHeight()) / 2));
        } else
        {
            mOverflowText.setVisibility(8);
        }
        for(k2 = Math.max(0, j1 - mAvatars.size()); k2 > 0; k2--)
        {
            avatarview = new AvatarView(getContext());
            avatarview.setOnClickListener(this);
            avatarview.setAvatarSize(0);
            addView(avatarview);
            mAvatars.add(avatarview);
        }

        int l2 = mAvatars.size();
        int i3 = 0;
        while(i3 < l2) 
        {
            AvatarView avatarview1 = (AvatarView)mAvatars.get(i3);
            if(i3 < j1)
            {
                avatarview1.setGaiaId((String)mPeople.get(i3));
                avatarview1.setVisibility(0);
                avatarview1.measure(android.view.View.MeasureSpec.makeMeasureSpec(sAvatarLineupItemSize, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(sAvatarLineupItemSize, 0x40000000));
                int j3;
                int k3;
                if(i3 > 0)
                    j3 = sAvatarLineupItemPadding;
                else
                    j3 = 0;
                k3 = k1 + j3;
                setCorner(avatarview1, k3, 0);
                k1 = k3 + sAvatarLineupItemSize;
            } else
            {
                avatarview1.setGaiaId(null);
                avatarview1.setVisibility(8);
            }
            i3++;
        }
    }

    public void onClick(View view)
    {
        if((view instanceof AvatarView) && mListener != null)
            mListener.onAvatarClicked(((AvatarView)view).getGaiaId());
    }
}
