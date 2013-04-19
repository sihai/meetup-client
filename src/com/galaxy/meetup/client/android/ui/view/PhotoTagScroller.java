/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.server.client.domain.DataRectRelative;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class PhotoTagScroller extends HorizontalScrollView implements
		OnClickListener, OnCheckedChangeListener {

	private PhotoTagAvatarView mCheckedAvatar;
    private android.view.View.OnClickListener mExternalClickListener;
    private boolean mHideTags;
    private Long mMyApprovedShapeId;
    private PhotoHeaderView mPhotoHeader;
    private final Rect mScrollerRect;
    private boolean mShapeNeedsApproval;
    private ArrayList mTags;
    
    public PhotoTagScroller(Context context)
    {
        super(context);
        mTags = new ArrayList();
        mHideTags = true;
        mScrollerRect = new Rect();
    }

    public PhotoTagScroller(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mTags = new ArrayList();
        mHideTags = true;
        mScrollerRect = new Rect();
    }

    public PhotoTagScroller(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mTags = new ArrayList();
        mHideTags = true;
        mScrollerRect = new Rect();
    }

    public final void bind(Context context, EsAccount esaccount, Cursor cursor, ViewGroup viewgroup)
    {
        LayoutInflater layoutinflater = LayoutInflater.from(context);
        long l;
        PhotoTagAvatarView phototagavatarview;
        String s3;
        String s4;
        byte abyte0[];
        boolean flag;
        boolean flag2;
        DataRectRelative datarectrelative;
        PhotoTagAvatarView phototagavatarview2;
        RectF rectf1;
        if(mCheckedAvatar != null)
        {
            Long long1 = (Long)mCheckedAvatar.getTag(R.id.tag_shape_id);
            if(long1 != null)
                l = long1.longValue();
            else
                l = 0L;
        } else
        {
            l = 0L;
        }
        phototagavatarview = null;
        mTags.clear();
        mMyApprovedShapeId = null;
        mShapeNeedsApproval = false;
        viewgroup.removeAllViews();
        cursor.moveToPosition(-1);
        do
        {
            if(!cursor.moveToNext())
                break;
            long l1 = cursor.getLong(4);
            String s1 = cursor.getString(7);
            String s2 = cursor.getString(6);
            s3 = cursor.getString(2);
            s4 = cursor.getString(5);
            abyte0 = cursor.getBlob(0);
            flag = TextUtils.equals(s4, "PENDING");
            boolean flag1 = TextUtils.equals(s4, "SUGGESTED");
            flag2 = esaccount.isMyGaiaId(s1);
            if(!TextUtils.equals(s4, "DETECTED") && !TextUtils.equals(s4, "REJECTED") && !TextUtils.isEmpty(s2))
            {
                datarectrelative = (DataRectRelative)JsonUtil.fromByteArray(abyte0, DataRectRelative.class);
                View view;
                int i;
                if(flag2)
                {
                    if(!flag && !flag1)
                    {
                        view = layoutinflater.inflate(R.layout.photo_tag_view, null);
                        i = viewgroup.getChildCount();
                        mMyApprovedShapeId = Long.valueOf(l1);
                    } else
                    {
                        view = layoutinflater.inflate(R.layout.photo_tag_approval_view, null);
                        View view1 = view.findViewById(R.id.tag_approve);
                        view1.setTag(R.id.tag_shape_id, Long.valueOf(l1));
                        view1.setTag(R.id.tag_is_suggestion, Boolean.valueOf(flag1));
                        view1.setTag(R.id.tag_gaiaid, s1);
                        view1.setOnClickListener(this);
                        View view2 = view.findViewById(R.id.tag_deny);
                        view2.setTag(R.id.tag_shape_id, Long.valueOf(l1));
                        view2.setTag(R.id.tag_is_suggestion, Boolean.valueOf(flag1));
                        view2.setTag(R.id.tag_gaiaid, s1);
                        view2.setOnClickListener(this);
                        mShapeNeedsApproval = true;
                        i = 0;
                    }
                } else
                {
                    view = layoutinflater.inflate(R.layout.photo_tag_view, null);
                    i = viewgroup.getChildCount();
                }
                viewgroup.addView(view, i);
                phototagavatarview2 = (PhotoTagAvatarView)view.findViewById(R.id.avatar);
                phototagavatarview2.setSubjectGaiaId(s1);
                mTags.add(phototagavatarview2);
                phototagavatarview2.setOnCheckedChangeListener(this);
                if(l == l1)
                    phototagavatarview = phototagavatarview2;
                rectf1 = new RectF(datarectrelative.left.floatValue(), datarectrelative.top.floatValue(), datarectrelative.right.floatValue(), datarectrelative.bottom.floatValue());
                phototagavatarview2.setTag(R.id.tag_shape_rect, rectf1);
                phototagavatarview2.setTag(R.id.tag_shape_name, s2);
                phototagavatarview2.setTag(R.id.tag_shape_id, Long.valueOf(l1));
                if(flag2)
                    if(flag)
                        ((TextView)findViewById(R.id.name)).setText(s3);
                    else
                    if(flag1)
                    {
                        ((TextView)findViewById(R.id.name)).setText(s2);
                        ((TextView)findViewById(R.id.second)).setText(R.string.photo_view_tag_suggestion_of_you);
                    }
            }
        } while(true);
        if(viewgroup.getChildCount() > 0)
        {
            PhotoTagAvatarView phototagavatarview1;
            RectF rectf;
            String s;
            if(phototagavatarview != null)
                phototagavatarview1 = phototagavatarview;
            else
                phototagavatarview1 = (PhotoTagAvatarView)viewgroup.getChildAt(0).findViewById(R.id.avatar);
            rectf = (RectF)phototagavatarview1.getTag(R.id.tag_shape_rect);
            s = (String)phototagavatarview1.getTag(R.id.tag_shape_name);
            mPhotoHeader.bindTagData(rectf, s);
            phototagavatarview1.setChecked(true);
        } else
        {
            mPhotoHeader.bindTagData(null, null);
        }
        invalidate();
        requestLayout();
    }

    public final Long getMyApprovedShapeId()
    {
        return mMyApprovedShapeId;
    }

    public final boolean hasTags()
    {
        boolean flag;
        if(mTags.size() > 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void hideTags(boolean flag, boolean flag1)
    {
        if(mHideTags)
        {
            mHideTags = false;
            setVisibility(0);
            mPhotoHeader.showTagShape();
        }
    }

    public final boolean isWaitingMyApproval()
    {
        return mShapeNeedsApproval;
    }

    public void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
    {
        if(!(compoundbutton instanceof PhotoTagAvatarView)) {
        	return;
        }
        
        PhotoTagAvatarView phototagavatarview = (PhotoTagAvatarView)compoundbutton;
        if(!flag) {
        	if(compoundbutton == mCheckedAvatar)
                mCheckedAvatar = null;
        } else {
        	if(compoundbutton == mCheckedAvatar) {
        		return;
        	}
        	PhotoTagAvatarView phototagavatarview1 = mCheckedAvatar;
            mCheckedAvatar = phototagavatarview;
            if(phototagavatarview1 != null)
                phototagavatarview1.setChecked(false);
        }
        
        RectF rectf;
        CharSequence charsequence;
        if(mCheckedAvatar == null)
            rectf = null;
        else
            rectf = (RectF)mCheckedAvatar.getTag(R.id.tag_shape_rect);
        if(mCheckedAvatar == null)
            charsequence = null;
        else
            charsequence = (CharSequence)mCheckedAvatar.getTag(R.id.tag_shape_name);
        mPhotoHeader.bindTagData(rectf, charsequence);
        mPhotoHeader.invalidate();
        
    }

    public void onClick(View view)
    {
        if(mExternalClickListener != null)
            mExternalClickListener.onClick(view);
    }

    public void setExternalOnClickListener(android.view.View.OnClickListener onclicklistener)
    {
        mExternalClickListener = onclicklistener;
    }

    public void setHeaderView(PhotoHeaderView photoheaderview)
    {
        mPhotoHeader = photoheaderview;
    }
    
	
	public static interface PhotoShapeQuery {

        public static final String PROJECTION[] = {
            "bounds", "creator_id", "creator_name", "photo_id", "shape_id", "status", "subject_name", "subject_id"
        };

    }
}
