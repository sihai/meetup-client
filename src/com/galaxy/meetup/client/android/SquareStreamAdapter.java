/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.animation.LayoutTransition;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.controller.ComposeBarController;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.ItemClickListener;
import com.galaxy.meetup.client.android.ui.view.SquareLandingView;
import com.galaxy.meetup.client.android.ui.view.StreamCardView;
import com.galaxy.meetup.client.util.PrimitiveUtils;

/**
 * 
 * @author sihai
 *
 */
public class SquareStreamAdapter extends StreamAdapter {

	private boolean mCanJoin;
    private boolean mCanRequestToJoin;
    private boolean mCanSeeMembers;
    private boolean mDisableSubscription;
    private boolean mIsMember;
    private int mJoinability;
    private int mMemberCount;
    private int mMembershipStatus;
    private boolean mNotificationsEnabled;
    private String mSquareAboutText;
    private SquareLandingView.OnClickListener mSquareDetailsViewOnClickListener;
    private String mSquareName;
    private String mSquarePhotoUrl;
    private Boolean mViewIsExpanded;
    private int mVisibility;
    
    public SquareStreamAdapter(Context context, ColumnGridView columngridview, EsAccount esaccount, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamAdapter.ViewUseListener viewuselistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener, ComposeBarController composebarcontroller)
    {
        super(context, columngridview, esaccount, onclicklistener, itemclicklistener, viewuselistener, streamplusbarclicklistener, streammediaclicklistener, composebarcontroller);
        mVisibleIndex = 0;
    }

    private ColumnGridView.LayoutParams getLayoutParams()
    {
        int k;
        int l;
        int i;
        int j;
        ColumnGridView.LayoutParams layoutparams;
        if(mLandscape)
            i = 1;
        else
            i = 2;
        if(mLandscape)
        {
            int i1 = (int)(0.7F * (float)sScreenMetrics.longDimension);
            j = Math.max(getContext().getResources().getDimensionPixelSize(R.dimen.square_card_min_width), Math.min(getContext().getResources().getDimensionPixelSize(R.dimen.square_card_max_width), i1));
        } else
        {
            j = -3;
        }
        if(1 == sScreenMetrics.screenDisplayType) {
        	k = 2;
            l = 2;
        } else {
        	k = 1;
            l = 1;
        }
        
        layoutparams = new ColumnGridView.LayoutParams(i, j, k, l);
        if(!mLandscape)
            layoutparams.height = -2;
        return layoutparams;
        
    }

    public final void bindStreamHeaderView(View view, Cursor cursor)
    {
        byte byte0 = 5;
        if(cursor.getPosition() == 0)
        {
            if(Log.isLoggable("SquareStreamAdapter", 3))
                Log.d("SquareStreamAdapter", (new StringBuilder("bindView(); ")).append(view).toString());
            SquareLandingView squarelandingview = (SquareLandingView)view;
            squarelandingview.setLayoutParams(getLayoutParams());
            squarelandingview.init(PrimitiveUtils.safeBoolean(mViewIsExpanded), mLandscape);
            if(!TextUtils.isEmpty(mSquareName))
            {
                squarelandingview.setSquareName(mSquareName);
                squarelandingview.setSquarePhoto(mSquarePhotoUrl);
                squarelandingview.setSquareMemberCount(mMemberCount);
                squarelandingview.setSquareAboutText(mSquareAboutText);
                squarelandingview.setSquareVisibility(mVisibility);
                squarelandingview.setMemberVisibility(mCanSeeMembers);
                if(mMembershipStatus != 4)
                    if(mMembershipStatus == 1)
                        byte0 = 6;
                    else
                    if(mMembershipStatus == byte0)
                        byte0 = 2;
                    else
                    if(mCanJoin)
                        byte0 = 1;
                    else
                    if(mCanRequestToJoin)
                        byte0 = 3;
                    else
                    if(mIsMember)
                        byte0 = 4;
                    else
                        byte0 = 0;
                squarelandingview.updateJoinLeaveButton(byte0);
                if(mIsMember && !mDisableSubscription)
                    squarelandingview.showNotificationSwitch(mNotificationsEnabled);
                else
                    squarelandingview.hideNotificationSwitch();
                if(!mIsMember && mVisibility == 1 && mJoinability == 1)
                    squarelandingview.showBlockingExplanation();
                else
                    squarelandingview.hideBlockingExplanation();
                squarelandingview.setOnClickListener(mSquareDetailsViewOnClickListener);
            }
        } else
        {
            view.setLayoutParams(getLayoutParams());
        }
    }

    public final void bindStreamView(View view, Cursor cursor)
    {
        ((StreamCardView)view).setSquareMode(true, isSquareAdmin());
        super.bindStreamView(view, cursor);
    }

    public final int getStreamHeaderViewType(int i) {
    	byte byte0 = 11;
    	if(0 == i) {
    		byte0 = 10;
    	}
    	return byte0;
    }

    public final int getViewTypeCount()
    {
        return 2 + super.getViewTypeCount();
    }

    public final int getVisibility()
    {
        return mVisibility;
    }

    public final boolean hasSquareData()
    {
        boolean flag;
        if(!TextUtils.isEmpty(mSquareName))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isEmpty()
    {
        boolean flag;
        if(super.isEmpty() && !hasSquareData())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isSquareAdmin()
    {
        boolean flag = true;
        if(mMembershipStatus != 2 && mMembershipStatus != 1)
            flag = false;
        return flag;
    }

    public final View newStreamHeaderView$4b8874c5(Context context, Cursor cursor)
    {
        LayoutInflater layoutinflater = (LayoutInflater)context.getSystemService("layout_inflater");
        Object obj;
        if(cursor.getPosition() == 0)
        {
            obj = (SquareLandingView)layoutinflater.inflate(R.layout.square_landing_view, null);
            if(android.os.Build.VERSION.SDK_INT >= 11)
                ((SquareLandingView) (obj)).setLayoutTransition(new LayoutTransition());
            if(Log.isLoggable("SquareStreamAdapter", 3))
                Log.d("SquareStreamAdapter", (new StringBuilder("newView() -> ")).append(obj).toString());
        } else
        {
            obj = layoutinflater.inflate(R.layout.square_cant_see_posts, null);
        }
        return ((View) (obj));
    }

    public final void resetAnimationState()
    {
        mVisibleIndex = 0;
    }

    public final void setOnClickListener(SquareLandingView.OnClickListener onclicklistener)
    {
        mSquareDetailsViewOnClickListener = onclicklistener;
    }

    public final void setSquareData(Cursor cursor)
    {
        boolean flag = true;
        mSquareName = cursor.getString(1);
        mSquarePhotoUrl = cursor.getString(3);
        mSquareAboutText = cursor.getString(4);
        mMemberCount = cursor.getInt(6);
        mMembershipStatus = cursor.getInt(7);
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        boolean flag6;
        if(cursor.getInt(8) != 0)
            flag1 = flag;
        else
            flag1 = false;
        mIsMember = flag1;
        mVisibility = cursor.getInt(10);
        mJoinability = cursor.getInt(5);
        if(cursor.getInt(13) != 0)
            flag2 = flag;
        else
            flag2 = false;
        mCanJoin = flag2;
        if(cursor.getInt(14) != 0)
            flag3 = flag;
        else
            flag3 = false;
        mCanRequestToJoin = flag3;
        if(cursor.getInt(11) != 0)
            flag4 = flag;
        else
            flag4 = false;
        mCanSeeMembers = flag4;
        if(cursor.getInt(17) != 0)
            flag5 = flag;
        else
            flag5 = false;
        mNotificationsEnabled = flag5;
        if(cursor.getInt(24) != 0)
            flag6 = flag;
        else
            flag6 = false;
        mDisableSubscription = flag6;
        if(mViewIsExpanded == null)
        {
            if(mIsMember)
                flag = false;
            mViewIsExpanded = Boolean.valueOf(flag);
        }
        notifyDataSetChanged();
    }

    public final void setViewIsExpanded(Boolean boolean1)
    {
        mViewIsExpanded = boolean1;
    }
}
