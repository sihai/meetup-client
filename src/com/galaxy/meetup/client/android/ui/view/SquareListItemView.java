/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;

/**
 * 
 * @author sihai
 *
 */
public class SquareListItemView extends FrameLayout implements OnClickListener,
		Recyclable {

	private TextView mMemberCountView;
    private ConstrainedTextView mNameView;
    protected OnItemClickListener mOnItemClickListener;
    private EsImageView mPhotoView;
    protected String mSquareId;
    private TextView mUnreadCountView;
    private TextView mVisibilityView;
    
    public SquareListItemView(Context context)
    {
        super(context);
    }

    public SquareListItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public SquareListItemView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public void init(Cursor cursor, OnItemClickListener onitemclicklistener, boolean flag, boolean flag1)
    {
        initChildViews();
        mOnItemClickListener = onitemclicklistener;
        mSquareId = cursor.getString(1);
        mNameView.setText(cursor.getString(2));
        String s = cursor.getString(3);
        if(TextUtils.isEmpty(s))
            s = null;
        mPhotoView.setUrl(s);
        int i;
        int j;
        int k;
        if(flag)
            i = cursor.getInt(4);
        else
            i = 0;
        setSquareVisibility(i);
        if(flag1)
            j = cursor.getInt(7);
        else
            j = 0;
        if(j == 0)
        {
            mUnreadCountView.setVisibility(8);
        } else
        {
            mUnreadCountView.setVisibility(0);
            String s1;
            if(j > 99)
                s1 = getResources().getString(R.string.ninety_nine_plus);
            else
                s1 = Integer.toString(j);
            mUnreadCountView.setText(s1);
        }
        k = cursor.getInt(5);
        if(k == 0)
        {
            mMemberCountView.setVisibility(8);
        } else
        {
            mMemberCountView.setVisibility(0);
            Resources resources = getResources();
            int l = R.plurals.square_members_count;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(k);
            String s2 = resources.getQuantityString(l, k, aobj);
            mMemberCountView.setText(s2);
        }
        setOnClickListener(this);
    }

    public void initChildViews()
    {
        if(mNameView == null)
        {
            mNameView = (ConstrainedTextView)findViewById(R.id.square_name);
            mPhotoView = (EsImageView)findViewById(R.id.square_photo);
            mVisibilityView = (TextView)findViewById(R.id.square_visibility);
            mMemberCountView = (TextView)findViewById(R.id.member_count);
            mUnreadCountView = (TextView)findViewById(R.id.unread_count);
        }
    }

    public void onClick(View view)
    {
        if(mOnItemClickListener != null)
            mOnItemClickListener.onClick(mSquareId);
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        initChildViews();
    }

    public void onRecycle()
    {
        if(mPhotoView != null)
            mPhotoView.onRecycle();
        setOnClickListener(null);
    }

    public void setSquareVisibility(int i) {
    	
    	boolean flag;
        int j;
        int k;
        flag = false;
        j = 0;
        k = 0;
        
    	if(0 == i) {
    		flag = true;
            j = R.string.square_public;
            k = R.drawable.ic_public_small;
    	} else if(1 == i) {
    		flag = true;
            j = R.string.square_private;
            k = R.drawable.ic_private_small;
    	}
    	
    	if(flag)
        {
            mVisibilityView.setVisibility(0);
            mVisibilityView.setText(j);
            mVisibilityView.setCompoundDrawablesWithIntrinsicBounds(k, 0, 0, 0);
        } else
        {
            mVisibilityView.setVisibility(8);
        }
    }
    
    //=====================================================================================
    //							Inner class
    //=====================================================================================
    public static interface OnItemClickListener {

        public abstract void onClick(String s);

        public abstract void onInvitationDismissed(String s);

        public abstract void onInviterImageClick(String s);
    }

}
