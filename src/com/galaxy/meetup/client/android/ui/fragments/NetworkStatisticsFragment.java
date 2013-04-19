/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsCursorLoader;
import com.galaxy.meetup.client.android.content.EsNetworkData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.ui.view.BarGraphView;

/**
 * 
 * @author sihai
 *
 */
public class NetworkStatisticsFragment extends EsFragment implements android.content.DialogInterface.OnClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks {

	protected static final String sSortColumns[];
    protected static final int sValueColumns[][] = {
        {
            2, 3
        }, {
            2
        }, {
            3
        }, {
            4, 5
        }, {
            4
        }, {
            5
        }, {
            6
        }
    };
    protected EsAccount mAccount;
    protected BarGraphView mBarGraphView;
    protected int mPendingViewType;
    protected int mViewType;

    static 
    {
        String as[] = new String[7];
        as[0] = (new StringBuilder("(")).append(NetworkStatisticsQuery.PROJECTION[2]).append("+").append(NetworkStatisticsQuery.PROJECTION[3]).append(")").toString();
        as[1] = NetworkStatisticsQuery.PROJECTION[2];
        as[2] = NetworkStatisticsQuery.PROJECTION[3];
        as[3] = (new StringBuilder("(")).append(NetworkStatisticsQuery.PROJECTION[4]).append("+").append(NetworkStatisticsQuery.PROJECTION[5]).append(")").toString();
        as[4] = NetworkStatisticsQuery.PROJECTION[4];
        as[5] = NetworkStatisticsQuery.PROJECTION[5];
        as[6] = NetworkStatisticsQuery.PROJECTION[6];
        sSortColumns = as;
    }
    
    public NetworkStatisticsFragment()
    {
        mViewType = 0;
    }

    private void updateTitle(EsFragmentActivity esfragmentactivity)
    {
        CharSequence acharsequence[] = getResources().getTextArray(R.array.network_statistics_types);
        if(android.os.Build.VERSION.SDK_INT >= 11)
            esfragmentactivity.setTitle(acharsequence[mViewType]);
        else
            esfragmentactivity.setTitlebarTitle((String)acharsequence[mViewType]);
    }

    protected final boolean isEmpty()
    {
        return false;
    }

    public void onClick(DialogInterface dialoginterface, int i)
    {
    	if(-2 == i) {
    		dialoginterface.dismiss();
    	} else if(-1 == i) {
    		if(mPendingViewType != mViewType)
            {
                mViewType = mPendingViewType;
                getLoaderManager().restartLoader(0, null, this);
                updateTitle((EsFragmentActivity)getActivity());
            }
            dialoginterface.dismiss();
    	} else {
    		 mPendingViewType = i;
    	}
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null && bundle.containsKey("view_type"))
        {
            mViewType = bundle.getInt("view_type");
            mPendingViewType = mViewType;
        }
        mAccount = (EsAccount)getActivity().getIntent().getParcelableExtra("account");
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(0, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
    	Loader loader = null;
    	if(0 == i) {
    		android.net.Uri uri = EsProvider.appendAccountParameter(EsProvider.NETWORK_DATA_STATS_URI, mAccount);
    		loader = new EsCursorLoader(getActivity(), uri, NetworkStatisticsQuery.PROJECTION, null, null, (new StringBuilder()).append(sSortColumns[mViewType]).append(" DESC").toString());
    	}
    	return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.network_statistics_fragment, viewgroup, false);
        mBarGraphView = (BarGraphView)view.findViewById(R.id.bar_graph);
        return view;
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        int i = cursor.getCount();
        BarGraphView.RowInfo arowinfo[] = new BarGraphView.RowInfo[i];
        cursor.moveToFirst();
        for(int j = 0; j < i; j++)
        {
            arowinfo[j] = new BarGraphView.RowInfo();
            arowinfo[j].mLabel = cursor.getString(1);
            arowinfo[j].mValue = 0L;
            int k = sValueColumns[mViewType].length;
            for(int l = 0; l < k; l++)
            {
                BarGraphView.RowInfo rowinfo = arowinfo[j];
                rowinfo.mValue = rowinfo.mValue + cursor.getLong(sValueColumns[mViewType][l]);
            }

            cursor.moveToNext();
        }

        mBarGraphView.update(arowinfo, getResources().getStringArray(R.array.network_statistics_types_units)[mViewType]);
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onMenuItemSelected(MenuItem menuitem)
    {
        int i = menuitem.getItemId();
        if(i == R.id.customize) {
	        mPendingViewType = mViewType;
	        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
	        builder.setTitle(R.string.menu_network_customize);
	        builder.setSingleChoiceItems(R.array.network_statistics_types, mViewType, this);
	        builder.setPositiveButton(R.string.ok, this);
	        builder.setNegativeButton(R.string.cancel, this);
	        builder.setCancelable(true);
	        builder.show();
        } else if(i == R.id.clear) {
        	EsNetworkData.resetStatsData(getActivity(), mAccount);
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    public final void onResume()
    {
        super.onResume();
        updateTitle((EsFragmentActivity)getActivity());
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("view_type", mViewType);
    }
    
    public static interface NetworkStatisticsQuery
    {

        public static final String PROJECTION[] = {
            "_id", "name", "sent", "recv", "network_duration", "process_duration", "req_count"
        };

    }
}
