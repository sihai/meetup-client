/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.fragments.SuggestionGridAdapter;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView.OnScrollListener;

/**
 * 
 * @author sihai
 *
 */
public class SuggestionGridView extends LinearLayout implements
		OnScrollListener {

	private SuggestionGridAdapter mAdapter;
    private DataSetObserver mObserver = new DataSetObserver() {

        public final void onChanged()
        {
            onDataChanged();
        }
    };
    private HashMap mRows;
    
    public SuggestionGridView(Context context)
    {
        super(context);
        mRows = new HashMap();
        setOrientation(1);
    }

    public SuggestionGridView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mRows = new HashMap();
        setOrientation(1);
    }

    public SuggestionGridView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mRows = new HashMap();
        setOrientation(1);
    }

    public final ScrollPositions getScrollPositions()
    {
        ScrollPositions scrollpositions = new ScrollPositions();
        Iterator iterator = mRows.entrySet().iterator();
        while(iterator.hasNext()) 
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String s = (String)entry.getKey();
            ColumnGridView columngridview = (ColumnGridView)entry.getValue();
            View view = columngridview.getChildAt(0);
            int i;
            if(view != null)
                i = view.getLeft();
            else
                i = 0;
            scrollpositions.setScrollPosition(s, columngridview.getFirstVisiblePosition(), i);
        }
        return scrollpositions;
    }

    protected final void onDataChanged() {
        List arraylist = mAdapter.getCategories();
        int i = arraylist.size();
        if(i > getChildCount())
        {
            LayoutInflater layoutinflater = LayoutInflater.from(getContext());
            View view1;
            for(; i > getChildCount(); addView(view1))
            {
                view1 = layoutinflater.inflate(R.layout.suggestion_category, this, false);
                ColumnGridView columngridview1 = (ColumnGridView)view1.findViewById(R.id.suggestion_row);
                columngridview1.setOrientation(1);
                columngridview1.setMinColumnWidth(getResources().getDimensionPixelSize(R.dimen.person_card_min_height));
                columngridview1.setColumnCount(1);
                columngridview1.setOnScrollListener(this);
            }

        } else
        {
            for(; getChildCount() > i; removeViewAt(-1 + getChildCount()));
        }
        mRows.clear();
        int j = 0;
        while(j < i) 
        {
            SuggestionGridAdapter.SuggestionCategoryAdapter suggestioncategoryadapter = (SuggestionGridAdapter.SuggestionCategoryAdapter)arraylist.get(j);
            View view = getChildAt(j);
            TextView textview = (TextView)view.findViewById(R.id.category_label);
            ColumnGridView columngridview;
            if("#".equals(suggestioncategoryadapter.getCategory()))
                textview.setText(getContext().getString(R.string.suggestion_category_friends).toUpperCase());
            else
                textview.setText(suggestioncategoryadapter.getCategoryLabel().toUpperCase());
            columngridview = (ColumnGridView)view.findViewById(R.id.suggestion_row);
            if(columngridview.getAdapter() != suggestioncategoryadapter)
                columngridview.setAdapter(suggestioncategoryadapter);
            else
                suggestioncategoryadapter.notifyDataSetChanged();
            mRows.put(suggestioncategoryadapter.getCategory(), columngridview);
            j++;
        }
    }

    public final void onScroll(ColumnGridView columngridview, int i, int j, int k, int l, int i1)
    {
    }

    public final void onScrollStateChanged(ColumnGridView columngridview, int i)
    {
        if(i == 1)
            requestDisallowInterceptTouchEvent(true);
    }

    public void setAdapter(SuggestionGridAdapter suggestiongridadapter)
    {
        if(mAdapter != null)
            mAdapter.unregisterDataSetObserver(mObserver);
        mAdapter = suggestiongridadapter;
        mAdapter.registerDataSetObserver(mObserver);
    }

    public void setScrollPositions(ScrollPositions scrollpositions)
    {
        Iterator iterator = mRows.entrySet().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String s = (String)entry.getKey();
            Integer integer = (Integer)scrollpositions.positions.get(s);
            Integer integer1 = (Integer)scrollpositions.offsets.get(s);
            if(integer != null && integer1 != null) {
                ((ColumnGridView)entry.getValue()).setSelectionFromTop(integer.intValue(), integer1.intValue());
            }
        } while(true);
    }
    

    public static class ScrollPositions implements Parcelable {
    	
    	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public final Object createFromParcel(Parcel parcel)
            {
                return new ScrollPositions(parcel);
            }

            public final Object[] newArray(int i)
            {
                return new ScrollPositions[i];
            }

        };
        
    	private HashMap offsets;
        private HashMap positions;

        public ScrollPositions()
        {
            positions = new HashMap();
            offsets = new HashMap();
        }

        public ScrollPositions(Parcel parcel)
        {
            positions = new HashMap();
            offsets = new HashMap();
            int i = parcel.readInt();
            for(int j = 0; j < i; j++)
                setScrollPosition(parcel.readString(), parcel.readInt(), parcel.readInt());

        }

	    public int describeContents()
	    {
	        return 0;
	    }
	
	    public final void setScrollPosition(String s, int i, int j)
	    {
	        positions.put(s, Integer.valueOf(i));
	        offsets.put(s, Integer.valueOf(j));
	    }
	
	    public void writeToParcel(Parcel parcel, int i)
	    {
	        parcel.writeInt(positions.size());
	        String s;
	        for(Iterator iterator = positions.entrySet().iterator(); iterator.hasNext(); parcel.writeInt(((Integer)offsets.get(s)).intValue()))
	        {
	            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
	            s = (String)entry.getKey();
	            parcel.writeString(s);
	            parcel.writeInt(((Integer)entry.getValue()).intValue());
	        }
	
	    }
    
    }
}
