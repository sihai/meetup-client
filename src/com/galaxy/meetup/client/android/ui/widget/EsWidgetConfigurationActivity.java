/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.ui.fragments.CircleListLoader;

/**
 * 
 * @author sihai
 *
 */
public class EsWidgetConfigurationActivity extends FragmentActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks, android.widget.AdapterView.OnItemClickListener {

	private CirclesCursorAdapter mAdapter;
    private final Object mAdapterLock = new Object();
    private int mAppWidgetId;
    private boolean mDisplayingEmptyView;
    private ListView mListView;
    
    public EsWidgetConfigurationActivity()
    {
    }

    private void updateDisplay()
    {
        if(mDisplayingEmptyView)
        {
            mListView.setVisibility(4);
            findViewById(0x1020004).setVisibility(0);
            findViewById(R.id.list_empty_text).setVisibility(8);
            findViewById(R.id.list_empty_progress).setVisibility(0);
        } else
        {
            mListView.setVisibility(0);
            findViewById(0x1020004).setVisibility(8);
            findViewById(R.id.list_empty_text).setVisibility(8);
            findViewById(R.id.list_empty_progress).setVisibility(8);
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Bundle bundle1 = getIntent().getExtras();
        if(bundle1 != null)
            mAppWidgetId = bundle1.getInt("appWidgetId", 0);
        else
            mAppWidgetId = 0;
        if(mAppWidgetId == 0)
        {
            finish();
        } else
        {
            if(EsAccountsData.getActiveAccount(this) == null)
            {
                EsWidgetUtils.saveCircleInfo(this, mAppWidgetId, null, null);
                EsWidgetProvider.configureWidget(this, EsAccountsData.getActiveAccount(this), mAppWidgetId);
                Intent intent = new Intent();
                intent.putExtra("appWidgetId", mAppWidgetId);
                setResult(-1, intent);
                finish();
            }
            setContentView(R.layout.widget_configuration_activity);
            mDisplayingEmptyView = true;
            mListView = (ListView)findViewById(0x102000a);
            mListView.setOnItemClickListener(this);
            View view = LayoutInflater.from(this).inflate(R.layout.widget_configuration_entry, null);
            view.findViewById(R.id.circle_icon).setVisibility(8);
            ((TextView)view.findViewById(R.id.circle_name)).setText(R.string.widget_all_circles);
            view.findViewById(R.id.circle_member_count).setVisibility(8);
            mListView.addHeaderView(view, null, true);
            mAdapter = new CirclesCursorAdapter(this);
            mListView.setAdapter(mAdapter);
            updateDisplay();
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
    	Loader loader = null;
    	if(0 == i) {
    		EsAccount esaccount = EsAccountsData.getActiveAccount(this);
            if(esaccount != null)
                loader = new CircleListLoader(this, EsAccountsData.getActiveAccount(this), 4, WidgetCircleQuery.PROJECTION);
    	}
        return loader;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
    	String s = null;
        String s2 = null;
        int j = i - mListView.getHeaderViewsCount();
        if(j >= 0) {
        	synchronized(mAdapterLock) {
        		if(mAdapter == null || mAdapter.getCursor() == null) {
        			return;
        		} else { 
        			Cursor cursor;
        	        cursor = mAdapter.getCursor();
        	        if(!cursor.isClosed() && cursor.getCount() > j) {
        	        	String s1;
        	            cursor.moveToPosition(j);
        	            s = cursor.getString(1);
        	            s1 = cursor.getString(2);
        	            s2 = s1;
        	        }
        		}
        	}
        } else { 
        	s = "v.all.circles";
            s2 = getString(R.string.stream_circles);
        }
        
        EsWidgetUtils.saveCircleInfo(this, mAppWidgetId, s, s2);
        EsWidgetProvider.configureWidget(this, EsAccountsData.getActiveAccount(this), mAppWidgetId);
        Intent intent = new Intent();
        intent.putExtra("appWidgetId", mAppWidgetId);
        setResult(-1, intent);
        finish();
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(0 == loader.getId()) {
        	synchronized(mAdapterLock)
            {
                mAdapter.swapCursor(cursor);
                mDisplayingEmptyView = false;
                updateDisplay();
            }
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }
    
    private final class CirclesCursorAdapter extends CursorAdapter
    {

        public final void bindView(View view, Context context, Cursor cursor)
        {
            ((TextView)view.findViewById(R.id.circle_name)).setText(cursor.getString(2));
            TextView textview = (TextView)view.findViewById(R.id.circle_member_count);
            ImageView imageview;
            if((1 & cursor.getInt(4)) != 0)
                textview.setText(null);
            else
                textview.setText((new StringBuilder("(")).append(cursor.getInt(3)).append(")").toString());
            imageview = (ImageView)view.findViewById(R.id.circle_icon);
            if("v.whatshot".equals(cursor.getString(1)))
                imageview.setImageResource(R.drawable.list_whats_hot);
            else
                imageview.setImageResource(R.drawable.list_circle);
        }

        public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
        {
            return LayoutInflater.from(context).inflate(R.layout.widget_configuration_entry, null);
        }

        public CirclesCursorAdapter(Context context)
        {
            super(context, null, 0);
        }
    }

    public static interface WidgetCircleQuery
    {

        public static final String PROJECTION[] = {
            "_id", "circle_id", "circle_name", "contact_count", "semantic_hints"
        };

    }
}
