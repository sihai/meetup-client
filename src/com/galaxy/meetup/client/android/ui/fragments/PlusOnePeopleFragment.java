/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.PlusOnePeopleAdapter;
import com.galaxy.meetup.client.android.PlusOnePeopleLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class PlusOnePeopleFragment extends DialogFragment implements
		LoaderCallbacks, OnClickListener, OnItemClickListener {

	private EsAccount mAccount;
    private PlusOnePeopleAdapter mAdapter;
    
    public PlusOnePeopleFragment()
    {
    }

    public void onClick(View view)
    {
        dismiss();
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        Loader loader = null;
        if(0 == i) {
        	if(mAccount != null)
            {
                String s = getArguments().getString("plus_one_id");
                loader = new PlusOnePeopleLoader(getActivity(), mAccount, s);
            }
        }
        return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.list_layout_acl, viewgroup);
        mAdapter = new PlusOnePeopleAdapter(getActivity(), null);
        ListView listview = (ListView)view.findViewById(0x102000a);
        listview.setOnItemClickListener(this);
        listview.setAdapter(mAdapter);
        view.findViewById(R.id.ok).setOnClickListener(this);
        view.findViewById(R.id.cancel).setVisibility(8);
        getDialog().setTitle(getString(R.string.plus_one_people_title));
        mAccount = (EsAccount)getArguments().getParcelable("account");
        getLoaderManager().initLoader(0, null, this);
        view.findViewById(R.id.list_empty_progress).setVisibility(0);
        return view;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        if(mAdapter.isExtraPeopleViewIndex(i)) 
        	return;
        
        Cursor cursor = (Cursor)mAdapter.getItem(i);
        if(cursor != null)
        {
            String s = cursor.getString(1);
            startActivity(Intents.getProfileActivityIntent(getActivity(), mAccount, s, null));
        }
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(0 == loader.getId()) {
        	getView().findViewById(R.id.list_empty_progress).setVisibility(8);
            int i = getArguments().getInt("total_plus_ones");
            int j;
            if(cursor == null)
                j = 0;
            else
                j = cursor.getCount();
            mAdapter.setExtraPeopleCount(i - j);
            mAdapter.swapCursor(cursor);
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }
    
    public static interface PeopleSetQuery
    {

        public static final String PROJECTION[] = {
            "_id", "person_id", "gaia_id", "name", "avatar"
        };

    }
}
