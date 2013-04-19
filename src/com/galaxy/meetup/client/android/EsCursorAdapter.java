/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author sihai
 *
 */
public class EsCursorAdapter extends CursorAdapter {

	public EsCursorAdapter(Context context, Cursor cursor)
    {
        super(context, cursor, false);
    }

    public static void onPause()
    {
    }

    public void bindView(View view, Context context, Cursor cursor)
    {
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        if(i >= getCount())
        {
            if(view == null)
                view = newView(mContext, getCursor(), viewgroup);
        } else
        {
            view = super.getView(i, view, viewgroup);
        }
        return view;
    }

    public boolean isEmpty()
    {
        boolean flag;
        if(getCursor() == null)
            flag = true;
        else
            flag = super.isEmpty();
        return flag;
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        return null;
    }

    public void onResume()
    {
    }

    public void onStop()
    {
    }

}
