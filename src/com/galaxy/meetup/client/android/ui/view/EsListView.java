/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 
 * @author sihai
 *
 */
public class EsListView extends ListView {
	
	private final DataSetObserver mObserver;
	
	public EsListView(Context context)
    {
        super(context);
        mObserver = new EsListDataSetObserver();
    }

    public EsListView(Context context, AttributeSet attributeset)
    {
        super(wrapContextIfNeeded(context, attributeset), attributeset);
        mObserver = new EsListDataSetObserver();
    }

    public EsListView(Context context, AttributeSet attributeset, int i)
    {
        super(wrapContextIfNeeded(context, attributeset), attributeset, i);
        mObserver = new EsListDataSetObserver();
    }

    private static Context wrapContextIfNeeded(Context context, AttributeSet attributeset)
    {
        if(attributeset != null) {
        	int i = attributeset.getAttributeResourceValue(null, "theme", 0);
            if(i != 0)
                context = new ContextThemeWrapper(context, i); 
        }
        return context;
    }

    protected final void adjustFastScroll()
    {
        if(isFastScrollEnabled())
        {
            setFastScrollEnabled(false);
            setFastScrollEnabled(true);
            int i = getWidth();
            int j = getHeight();
            onSizeChanged(i, j, i, j);
        }
    }

    public void setAdapter(ListAdapter listadapter)
    {
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            super.setAdapter(listadapter);
        } else
        {
            ListAdapter listadapter1 = getAdapter();
            if(listadapter1 != null)
                listadapter1.unregisterDataSetObserver(mObserver);
            if(listadapter != null)
                listadapter.registerDataSetObserver(mObserver);
            super.setAdapter(listadapter);
        }
    }
    
    private class EsListDataSetObserver extends DataSetObserver {
    	
    	public final void onChanged()
        {
            adjustFastScroll();
        }

    }
}
