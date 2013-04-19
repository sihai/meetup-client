/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.galaxy.meetup.client.android.AccountsAdapter;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.activity.BaseAccountSelectionActivity;
import com.galaxy.meetup.client.util.AccountsUtil;

/**
 * 
 * @author sihai
 *
 */
public class AccountsListFragment extends EsFragment implements LoaderCallbacks, OnItemClickListener {

	private BaseAccountSelectionActivity.AccountsAdder mAccountsAdder;
    private AccountsAdapter mAdapter;
    private boolean mAddAccountShown;
    private ListView mListView;
    
	public AccountsListFragment() {
	}

	protected final boolean isEmpty() {
		boolean flag;
		if (mAdapter == null || mAdapter.isEmpty())
			flag = true;
		else
			flag = false;
		return flag;
	}

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
            mAddAccountShown = bundle.getBoolean("add_account_shown");
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        return new AccountsLoader(getActivity());
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle, R.layout.list_layout);
        mListView = (ListView)view.findViewById(0x102000a);
        mListView.setOnItemClickListener(this);
        mAdapter = new AccountsAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        return view;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l) {
    	
    	mAccountsAdder.addAccount("sihai");
    	/*if(i != -1 + adapterview.getCount()) {
    		String s = (String)mListView.getItemAtPosition(i);
            if(mAccountsAdder != null)
                mAccountsAdder.addAccount(s);
    	} else {
    		AccountsUtil.addAccount(getActivity());
    	}*/
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        List list = (List)obj;
        mAdapter.clear();
        Account account;
        for(Iterator iterator = list.iterator(); iterator.hasNext(); mAdapter.add(account.name))
            account = (Account)iterator.next();

        mAdapter.add(getString(R.string.signup_create_new_account));
        mAdapter.notifyDataSetChanged();
        if(list.size() == 0 && !mAddAccountShown)
        {
            AccountsUtil.addAccount(getActivity());
            mAddAccountShown = true;
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onResume()
    {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("add_account_shown", mAddAccountShown);
    }

    public final void setAccountsAdder(BaseAccountSelectionActivity.AccountsAdder accountsadder)
    {
        mAccountsAdder = accountsadder;
    }

    public final void showList()
    {
        View view = getView();
        if(mListView.getAdapter().getCount() > 0)
            view.findViewById(0x1020004).setVisibility(8);
        else
            view.findViewById(0x1020004).setVisibility(0);
        mListView.setVisibility(0);
    }

	private static final class AccountsLoader extends AsyncTaskLoader {

		public final List<Account> loadInBackground() {
			try {
				return AccountsUtil.getAccounts(getContext());
			} catch (Exception e) {
				e.printStackTrace();
				return Collections.emptyList();
			}
		}

		protected final void onStartLoading() {
			forceLoad();
		}

		public AccountsLoader(Context context) {
			super(context);
		}
	}
}
