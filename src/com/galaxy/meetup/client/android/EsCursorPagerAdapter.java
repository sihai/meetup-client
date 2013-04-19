/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;

import com.galaxy.meetup.client.android.ui.fragments.EsFragmentPagerAdapter;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public abstract class EsCursorPagerAdapter extends EsFragmentPagerAdapter {

	Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private SparseIntArray mItemPosition;
    private HashMap mObjectRowMap;
    private int mRowIDColumn;
    
    public EsCursorPagerAdapter(Context context, FragmentManager fragmentmanager, Cursor cursor)
    {
        super(fragmentmanager);
        mObjectRowMap = new HashMap();
        boolean flag;
        int i;
        if(cursor != null)
            flag = true;
        else
            flag = false;
        mCursor = cursor;
        mDataValid = flag;
        mContext = context;
        if(flag)
            i = cursor.getColumnIndexOrThrow("_id");
        else
            i = -1;
        mRowIDColumn = i;
    }

    private boolean moveCursorTo(int i)
    {
        boolean flag;
        if(mCursor != null && !mCursor.isClosed())
            flag = mCursor.moveToPosition(i);
        else
            flag = false;
        return flag;
    }

    private void setItemPosition()
    {
        if(!mDataValid || mCursor == null || mCursor.isClosed())
        {
            mItemPosition = null;
        } else
        {
            SparseIntArray sparseintarray = new SparseIntArray(mCursor.getCount());
            mCursor.moveToPosition(-1);
            for(; mCursor.moveToNext(); sparseintarray.append(mCursor.getInt(mRowIDColumn), mCursor.getPosition()));
            mItemPosition = sparseintarray;
        }
    }

    public final void destroyItem(View view, int i, Object obj)
    {
        mObjectRowMap.remove(obj);
        super.destroyItem(view, i, obj);
    }

    public int getCount()
    {
        int i;
        if(mDataValid && mCursor != null)
            i = mCursor.getCount();
        else
            i = 0;
        return i;
    }

    public final Cursor getCursor()
    {
        return mCursor;
    }

    public Fragment getItem(int i)
    {
        Fragment fragment;
        if(mDataValid && moveCursorTo(i))
        {
            Context _tmp = mContext;
            fragment = getItem(mCursor);
        } else
        {
            fragment = null;
        }
        return fragment;
    }

    public abstract Fragment getItem(Cursor cursor);

    public final int getItemPosition(Object obj)
    {
        int i = -2;
        Integer integer = (Integer)mObjectRowMap.get(obj);
        if(integer != null && mItemPosition != null)
            i = mItemPosition.get(integer.intValue(), i);
        return i;
    }

    public final Object instantiateItem(View view, int i)
    {
        if(!mDataValid)
            throw new IllegalStateException("this should only be called when the cursor is valid");
        Integer integer;
        Object obj;
        if(moveCursorTo(i))
            integer = Integer.valueOf(mCursor.getInt(mRowIDColumn));
        else
            integer = null;
        obj = super.instantiateItem(view, i);
        if(obj != null)
            mObjectRowMap.put(obj, integer);
        return obj;
    }

    public final boolean isDataValid()
    {
        return mDataValid;
    }

    protected final String makeFragmentName(int i, int j)
    {
        String s;
        if(moveCursorTo(j))
            s = (new StringBuilder("android:espager:")).append(i).append(":").append(mCursor.getInt(mRowIDColumn)).toString();
        else
            s = super.makeFragmentName(i, j);
        return s;
    }

    public final Cursor swapCursor(Cursor cursor)
    {
        Cursor cursor1;
        if(EsLog.isLoggable("EsCursorPagerAdapter", 2))
        {
            StringBuilder stringbuilder = new StringBuilder("swapCursor old=");
            int i;
            StringBuilder stringbuilder1;
            int j;
            if(mCursor == null)
                i = -1;
            else
                i = mCursor.getCount();
            stringbuilder1 = stringbuilder.append(i).append("; new=");
            if(cursor == null)
                j = -1;
            else
                j = cursor.getCount();
            Log.v("EsCursorPagerAdapter", stringbuilder1.append(j).toString());
        }
        if(cursor == mCursor)
        {
            cursor1 = null;
        } else
        {
            cursor1 = mCursor;
            mCursor = cursor;
            if(cursor != null)
            {
                mRowIDColumn = cursor.getColumnIndexOrThrow("_id");
                mDataValid = true;
            } else
            {
                mRowIDColumn = -1;
                mDataValid = false;
            }
            setItemPosition();
            notifyDataSetChanged();
        }
        return cursor1;
    }
}
