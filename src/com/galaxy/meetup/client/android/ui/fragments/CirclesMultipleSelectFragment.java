/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.fragments.CirclePropertiesFragmentDialog.CirclePropertiesListener;
import com.galaxy.meetup.client.android.ui.view.CheckableListItemView;
import com.galaxy.meetup.client.android.ui.view.CheckableListItemView.OnItemCheckedChangeListener;
import com.galaxy.meetup.client.android.ui.view.CircleListItemView;
import com.galaxy.meetup.client.util.AccountsUtil;

/**
 * 
 * @author sihai
 *
 */
public class CirclesMultipleSelectFragment extends Fragment implements
		LoaderCallbacks, OnItemClickListener, AlertDialogListener,
		CirclePropertiesListener, OnItemCheckedChangeListener {
	
	private CirclesCursorAdapter mAdapter;
    private ArrayList mCircleIdSnapshot;
    private int mCircleUsageType;
    private boolean mContactLoaded;
    private ListView mListView;
    private OnCircleSelectionListener mListener;
    private boolean mNewCircleEnabled;
    private ArrayList mNewCircleIds;
    private ArrayList mOldCircleIds;
    private Integer mPendingRequestId;
    private String mPersonId;
    private final EsServiceListener mServiceListener = new EsServiceListener() {

        public final void onCreateCircleRequestComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

    };
    private ContextThemeWrapper mThemeContext;

    public CirclesMultipleSelectFragment()
    {
    }

    private EsAccount getAccount()
    {
        return (EsAccount)getActivity().getIntent().getExtras().get("account");
    }

    private void parsePackedCircleIds(String s)
    {
        mOldCircleIds = new ArrayList();
        if(!TextUtils.isEmpty(s))
        {
            int j;
            for(int i = 0; i < s.length(); i = j + 1)
            {
                j = s.indexOf('|', i);
                if(j == -1)
                    j = s.length();
                mOldCircleIds.add(s.substring(i, j));
            }

        }
        if(mNewCircleIds == null)
            mNewCircleIds = new ArrayList(mOldCircleIds);
        if(mListener != null)
            mListener.onCircleSelectionChange();
    }

    public final ArrayList getOriginalCircleIds()
    {
        return mOldCircleIds;
    }

    public final ArrayList getSelectedCircleIds()
    {
        return mNewCircleIds;
    }

    protected final void handleServiceCallback(int i, ServiceResult serviceresult)
    {
        if(mPendingRequestId != null && mPendingRequestId.intValue() == i) {
        	mPendingRequestId = null;
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            if(serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
        }
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mThemeContext = new ContextThemeWrapper(activity, R.style.CircleSelectorList);
        mAdapter = new CirclesCursorAdapter(mThemeContext);
    }

    public final void onCirclePropertiesChange(String s, String s1, boolean flag)
    {
    	if(TextUtils.isEmpty(s1)) {
    		return;
    	}
    	String s2;
        Cursor cursor1;
        boolean flag1;
        s2 = s1.trim();
       // TODO
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mNewCircleIds = bundle.getStringArrayList("new_circles");
            mCircleIdSnapshot = bundle.getStringArrayList("existing_circle_ids");
            if(bundle.containsKey("request_id"))
                mPendingRequestId = Integer.valueOf(bundle.getInt("request_id"));
        }
        if(mCircleUsageType == 2 && mPersonId != null)
            getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	
    	Loader loader = null;
    	if(0 == i) {
    		FragmentActivity fragmentactivity = getActivity();
            android.net.Uri uri = EsProvider.appendAccountParameter(EsProvider.CONTACTS_URI, getAccount());
            String as[] = {
                "name", "packed_circle_ids"
            };
            String as1[] = new String[1];
            as1[0] = mPersonId;
            loader = new EsCursorLoader(fragmentactivity, uri, as, "person_id=?", as1, null);
    	} else if(1 == i) {
    		loader = new CircleListLoader(getActivity(), getAccount(), mCircleUsageType, new String[] {
                "_id", "circle_id", "circle_name", "contact_count", "type"
            });
    	}
    	
    	return loader;
    	
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        LayoutInflater layoutinflater1 = LayoutInflater.from(mThemeContext);
        View view = layoutinflater1.inflate(R.layout.circles_multiple_select_fragment, null, false);
        mListView = (ListView)view.findViewById(0x102000a);
        if(mNewCircleEnabled)
        {
            View view1 = layoutinflater1.inflate(R.layout.circles_item_new_circle, viewgroup, false);
            view1.setContentDescription(getString(R.string.create_new_circle));
            mListView.addHeaderView(view1);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    public final void onDialogCanceled(String s)
    {
    }

    public final void onDialogListClick(int i, Bundle bundle)
    {
    }

    public final void onDialogNegativeClick(String s)
    {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s)
    {
    }

    public final void onItemCheckedChanged(CheckableListItemView checkablelistitemview, boolean flag)
    {
        String s = ((CircleListItemView)checkablelistitemview).getCircleId();
        if(flag)
        {
            if(!mNewCircleIds.contains(s))
                mNewCircleIds.add(s);
        } else
        {
            mNewCircleIds.remove(s);
        }
        if(mListener != null)
            mListener.onCircleSelectionChange();
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        if(mNewCircleEnabled && i == 0)
        {
            getActivity();
            CirclePropertiesFragmentDialog circlepropertiesfragmentdialog = CirclePropertiesFragmentDialog.newInstance$47e87423();
            circlepropertiesfragmentdialog.setTargetFragment(this, 0);
            circlepropertiesfragmentdialog.show(getFragmentManager(), "new_circle_input");
        } else
        {
            ((CircleListItemView)view).toggle();
        }
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        boolean flag = true;
        Cursor cursor = (Cursor)obj;
        
        if(null == cursor) {
        	Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
        	return;
        }
        
        // TODO
        int id = loader.getId();
        if(0 == id) {
        	
        } else if(1 == id) {
        	
        } else {
        	
        }
        
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingRequestId.intValue());
            handleServiceCallback(mPendingRequestId.intValue(), serviceresult);
            mPendingRequestId = null;
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putStringArrayList("new_circles", mNewCircleIds);
        bundle.putStringArrayList("existing_circle_ids", mCircleIdSnapshot);
        if(mPendingRequestId != null)
            bundle.putInt("request_id", mPendingRequestId.intValue());
    }

    public final void setCircleUsageType(int i)
    {
        mCircleUsageType = 2;
    }

    public final void setNewCircleEnabled(boolean flag)
    {
        mNewCircleEnabled = true;
    }

    public final void setOnCircleSelectionListener(OnCircleSelectionListener oncircleselectionlistener)
    {
        mListener = oncircleselectionlistener;
    }

    public final void setPersonId(String s)
    {
        mPersonId = s;
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private final class CirclesCursorAdapter extends CursorAdapter
    {

        public final void bindView(View view, Context context, Cursor cursor)
        {
            CircleListItemView circlelistitemview = (CircleListItemView)view;
            String s = cursor.getString(1);
            int i = cursor.getInt(4);
            circlelistitemview.setCircle(s, i, cursor.getString(2), cursor.getInt(3), AccountsUtil.isRestrictedCircleForAccount(getAccount(), i));
            circlelistitemview.setChecked(mNewCircleIds.contains(s));
            circlelistitemview.updateContentDescription();
        }

        public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
        {
            CircleListItemView circlelistitemview = new CircleListItemView(context);
            circlelistitemview.setAvatarStripVisible(false);
            circlelistitemview.setCheckBoxVisible(true);
            circlelistitemview.setOnItemCheckedChangeListener(CirclesMultipleSelectFragment.this);
            return circlelistitemview;
        }

        public CirclesCursorAdapter(Context context)
        {
            super(context, null, 0);
        }
    }

    public static interface OnCircleSelectionListener
    {

        public abstract void onCircleSelectionChange();
    }
}
