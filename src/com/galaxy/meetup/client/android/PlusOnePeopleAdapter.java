/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxy.meetup.client.android.ui.view.AvatarView;

/**
 * 
 * @author sihai
 *
 */
public class PlusOnePeopleAdapter extends EsCursorAdapter {

	private View mExtraPeopleView;
	
	public PlusOnePeopleAdapter(Context context, Cursor cursor)
    {
        super(context, null);
    }

    private boolean isLastViewExtraPeopleCount()
    {
        boolean flag;
        if(mExtraPeopleView != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void bindView(View view, Context context, Cursor cursor)
    {
        if(view != mExtraPeopleView && cursor.getPosition() < super.getCount())
        {
            AvatarView avatarview = (AvatarView)view.findViewById(R.id.avatar);
            avatarview.setVisibility(0);
            avatarview.setGaiaIdAndAvatarUrl(cursor.getString(2), cursor.getString(4));
            ((TextView)view.findViewById(R.id.name)).setText(cursor.getString(3));
        }
    }

    public final int getCount()
    {
        int i = super.getCount();
        int j;
        if(isLastViewExtraPeopleCount())
            j = 1;
        else
            j = 0;
        return j + i;
    }

    public final int getItemViewType(int i)
    {
        int j;
        if(isExtraPeopleViewIndex(i))
            j = 1;
        else
            j = 0;
        return j;
    }

    public final View getView(int i, View view, ViewGroup viewgroup)
    {
        if(i >= getCount())
        {
            if(view == null)
                view = newView(mContext, getCursor(), viewgroup);
        } else
        if(isExtraPeopleViewIndex(i))
            view = mExtraPeopleView;
        else
            view = super.getView(i, view, viewgroup);
        return view;
    }

    public final int getViewTypeCount()
    {
        return 2;
    }

    public final boolean isExtraPeopleViewIndex(int i)
    {
        boolean flag;
        if(isLastViewExtraPeopleCount() && i == super.getCount())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        View view;
        if(isExtraPeopleViewIndex(cursor.getPosition()))
            view = mExtraPeopleView;
        else
            view = ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(R.layout.acl_row_view, null);
        return view;
    }

    public final void setExtraPeopleCount(int i)
    {
        if(i <= 0)
        {
            mExtraPeopleView = null;
        } else
        {
            mExtraPeopleView = ((LayoutInflater)mContext.getSystemService("layout_inflater")).inflate(R.layout.acl_row_view, null, false);
            mExtraPeopleView.findViewById(R.id.avatar).setVisibility(4);
            TextView textview = (TextView)mExtraPeopleView.findViewById(R.id.name);
            Resources resources = mContext.getResources();
            int j = R.plurals.plus_one_people_more_plus_ones;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(i);
            textview.setText(resources.getQuantityString(j, i, aobj));
        }
    }
}
