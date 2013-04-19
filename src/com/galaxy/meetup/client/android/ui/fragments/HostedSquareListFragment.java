/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.SquareCardAdapter;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.EsSquaresData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.SquareListItemView;
import com.galaxy.meetup.client.util.HelpUrl;
import com.galaxy.meetup.client.util.Property;


/**
 * 
 * @author sihai
 *
 */
public class HostedSquareListFragment extends HostedEsFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks, AlertFragmentDialog.AlertDialogListener, SquareListItemView.OnItemClickListener {

	private SquareCardAdapter mAdapter;
    private Context mContext;
    private int mCurrentMode;
    private int mCurrentSpinnerPosition;
    private boolean mDataPresent;
    private EsMatrixCursor mDescriptionHeaderCursor;
    private String mErrorText;
    private ColumnGridView mGridView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final EsServiceListener mListener = new EsServiceListener() {

        public final void onGetSquaresComplete(int i, ServiceResult serviceresult)
        {
            if(mNewerReqId != null && i == mNewerReqId.intValue())
            {
                mNewerReqId = null;
                mRefreshNeeded = false;
                if(serviceresult.hasError())
                    setError(getString(R.string.squares_load_error));
                else
                    clearError();
                updateSpinner();
                mHandler.post(new Runnable() {

                    public final void run()
                    {
                        if(getActivity() != null && !getActivity().isFinishing())
                            getLoaderManager().restartLoader(0, null, HostedSquareListFragment.this);
                    }

                });
            }
        }
    };
    private boolean mRefreshNeeded;
    private ArrayAdapter mSpinnerAdapter;
    private boolean mSquaresLoaderActive;
    
    public HostedSquareListFragment()
    {
        mCurrentMode = 0;
        mSquaresLoaderActive = true;
    }

    private void fetchData()
    {
        if(mNewerReqId == null && mContext != null)
        {
            if(!mDataPresent)
                showEmptyViewProgress(getView(), getString(R.string.loading));
            mNewerReqId = Integer.valueOf(EsService.getSquares(mContext, mAccount));
        }
        updateSpinner();
    }

    private boolean hasError()
    {
        boolean flag;
        if(mErrorText != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final void clearError()
    {
        mErrorText = null;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.SQUARE_HOME;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mAdapter == null || mAdapter.isEmpty())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final boolean isProgressIndicatorVisible()
    {
        boolean flag;
        if(super.isProgressIndicatorVisible() || mSquaresLoaderActive)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onActionButtonClicked(int i) {
    	if(0 == i) {
    		startActivity(Intents.getSquareSearchActivityIntent(mContext, mAccount));
    	}
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mContext = activity;
    }

    public final void onClick(String s)
    {
        startActivity(Intents.getSquareStreamActivityIntent(getActivity(), mAccount, s, null, null));
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mRefreshNeeded = bundle.getBoolean("squares_refresh", false);
            mCurrentMode = bundle.getInt("squares_currentmode", 0);
            mDataPresent = bundle.getBoolean("squares_datapresent", false);
        } else
        {
            mRefreshNeeded = getArguments().getBoolean("refresh", false);
        }
        getLoaderManager().initLoader(0, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        mSquaresLoaderActive = true;
        return new SquaresLoader(mContext, mAccount, mCurrentMode);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.hosted_squares_fragment, viewgroup, false);
        mGridView = (ColumnGridView)view.findViewById(R.id.grid);
        mAdapter = new SquareCardAdapter(mContext, mAccount, this, mGridView);
        mGridView.setAdapter(mAdapter);
        setupEmptyView(view, R.string.no_squares);
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
        if("dismiss_invitation".equals(s))
        {
            String s1 = bundle.getString("square_id");
            FragmentActivity fragmentactivity = getActivity();
            EsService.declineSquareInvitation(fragmentactivity, mAccount, s1);
            Bundle bundle1 = EsAnalyticsData.createExtras("extra_square_id", s1);
            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.SQUARE_DECLINE_INVITATION, OzViews.SQUARE_HOME, bundle1);
        }
    }

    public final void onInvitationDismissed(String s)
    {
        AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(null, getString(R.string.square_dismiss_invitation_text), getString(R.string.square_dialog_decline_button), getString(R.string.cancel));
        alertfragmentdialog.setTargetFragment(this, 0);
        Bundle bundle = alertfragmentdialog.getArguments();
        if(bundle == null)
            bundle = new Bundle();
        bundle.putString("square_id", s);
        alertfragmentdialog.setArguments(bundle);
        alertfragmentdialog.show(getFragmentManager(), "dismiss_invitation");
    }

    public final void onInviterImageClick(String s)
    {
        if(!TextUtils.isEmpty(s))
        {
            String s1 = (new StringBuilder("g:")).append(s).toString();
            startActivity(Intents.getProfileActivityIntent(mContext, mAccount, s1, null, 0));
        }
    }

    public final void onLoadFinished(Loader loader, Object obj) {
        int i;
        byte byte0;
        Cursor cursor = (Cursor)obj;
        mSquaresLoaderActive = false;
        boolean flag;
        SquareCardAdapter squarecardadapter;
        EsMatrixCursor esmatrixcursor;
        SquareCardAdapter squarecardadapter1;
        if(cursor != null && cursor.getCount() > 0)
            flag = true;
        else
            flag = false;
        mDataPresent = flag;
        if(!hasError() && (loader instanceof SquaresLoader))
            mRefreshNeeded = mRefreshNeeded | ((SquaresLoader)loader).isDataStale();
        i = mCurrentMode;
        if(loader instanceof SquaresLoader)
            i = ((SquaresLoader)loader).getCurrentMode();
        squarecardadapter = mAdapter;
        if(i == 3)
        {
            if(mDescriptionHeaderCursor == null)
            {
                mDescriptionHeaderCursor = new EsMatrixCursor(new String[] {
                    "_id"
                }, 1);
                EsMatrixCursor esmatrixcursor1 = mDescriptionHeaderCursor;
                Integer ainteger[] = new Integer[1];
                ainteger[0] = Integer.valueOf(0);
                esmatrixcursor1.addRow(ainteger);
            }
            esmatrixcursor = mDescriptionHeaderCursor;
        } else
        {
            esmatrixcursor = null;
        }
        squarecardadapter.changeDescriptionHeaderCursor(esmatrixcursor);
        squarecardadapter1 = mAdapter;
        if(1 == i) {
        	byte0 = 1;
        } else if(2 == i) {
        	byte0 = 2;
        } else if(3 == i) {
        	byte0 = 3;
        } else {
        	byte0 = 2;
        }
        
        squarecardadapter1.changeSquaresCursor(cursor, byte0);
        if(!mRefreshNeeded)
            if(mDataPresent)
                mCurrentMode = i;
            else
            if(i != mCurrentMode)
                getLoaderManager().restartLoader(0, null, this);
        if(mDataPresent) { 
        	if(1 == i) {
        		mCurrentSpinnerPosition = 0;
        	} else if(2 == i) {
        		mCurrentSpinnerPosition = 1;
        	} else if(3 == i) {
        		mCurrentSpinnerPosition = 2;
        	}
        	
        }
        
        invalidateActionBar();
        if(mDataPresent)
            showContent(getView());
        else
        if(mRefreshNeeded)
        {
            showEmptyViewProgress(getView(), getString(R.string.loading));
        } else
        {
            View view = getView();
            String s;
            if(hasError())
                s = mErrorText;
            else
            if(mCurrentSpinnerPosition == 0)
                s = getString(R.string.no_square_invitations);
            else
                s = getString(R.string.no_squares);
            showEmptyView(view, s);
        }
        return; 
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(i == R.id.refresh)
            refresh();
        else
        if(i == R.id.help)
        {
            String s = getResources().getString(R.string.url_param_help_squares);
            startExternalActivity(new Intent("android.intent.action.VIEW", HelpUrl.getHelpUrl(mContext, s)));
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public final void onPause()
    {
        EsService.unregisterListener(mListener);
        super.onPause();
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        mSpinnerAdapter = new ArrayAdapter(mContext, R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(0x1090009);
        CharSequence acharsequence[] = getResources().getTextArray(R.array.square_list_spinner_items);
        int i = 0;
        for(int j = acharsequence.length; i < j; i++)
            mSpinnerAdapter.add(acharsequence[i].toString());

        if(mCurrentMode != 0 || mDataPresent)
            hostactionbar.showPrimarySpinner(mSpinnerAdapter, mCurrentSpinnerPosition);
        hostactionbar.showRefreshButtonIfRoom();
        hostactionbar.showActionButton(0, R.drawable.ic_menu_search_holo_light, R.string.menu_search);
    }

    public final void onPrepareOptionsMenu(Menu menu)
    {
        boolean flag = getActionBar().isRefreshButtonVisible();
        MenuItem menuitem = menu.findItem(R.id.refresh);
        boolean flag1;
        if(!flag)
            flag1 = true;
        else
            flag1 = false;
        menuitem.setVisible(flag1);
    }

    public final void onPrimarySpinnerSelectionChange(int i) {
        if(mCurrentSpinnerPosition == i) {
        	return;
        }
        
        if(0 == mCurrentSpinnerPosition) {
        	mCurrentMode = 1;
        } else if(1 == mCurrentSpinnerPosition) {
        	mCurrentMode = 2;
        } else {
        	mCurrentMode = 3;
        }
        
        mGridView.setSelectionToTop();
        getLoaderManager().restartLoader(0, null, this);
    }

    public final void onResume()
    {
        EsService.registerListener(mListener);
        super.onResume();
        if(!Property.ENABLE_SQUARES.getBoolean())
            getActivity().finish();
        if(mRefreshNeeded)
            fetchData();
        updateSpinner();
    }

    protected final void onResumeContentFetched(View view)
    {
        super.onResumeContentFetched(view);
        mRefreshNeeded = false;
        mErrorText = null;
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        bundle.putBoolean("squares_refresh", mRefreshNeeded);
        bundle.putInt("squares_currentmode", mCurrentMode);
        bundle.putBoolean("squares_datapresent", mDataPresent);
        super.onSaveInstanceState(bundle);
    }

    public final void refresh()
    {
        super.refresh();
        fetchData();
    }

    protected final void setError(String s)
    {
        mErrorText = s;
        if(mDataPresent && mErrorText != null)
            Toast.makeText(mContext, mErrorText, 0).show();
    }

    protected final void showContent(View view)
    {
        super.showContent(view);
        mGridView.setVisibility(0);
    }

    protected final void showEmptyView(View view, String s)
    {
        super.showEmptyView(view, s);
        mGridView.setVisibility(8);
    }
    
    
    //==================================================================================
    //							Inner class
    //==================================================================================
    public static interface Query {

        public static final String INVITATION_PROJECTION[] = {
            "_id", "square_id", "square_name", "photo_url", "post_visibility", "member_count", "membership_status", "unread_count", "inviter_gaia_id", "inviter_name", 
            "inviter_photo_url"
        };
        public static final String PROJECTION[] = {
            "_id", "square_id", "square_name", "photo_url", "post_visibility", "member_count", "membership_status", "unread_count"
        };

    }

    static final class SquaresLoader extends EsCursorLoader {

    	private final EsAccount mAccount;
        private boolean mIsDataStale;
        private int mMode;

        public SquaresLoader(Context context, EsAccount esaccount, int i)
        {
            super(context, EsProvider.SQUARES_URI);
            mMode = i;
            mAccount = esaccount;
        }
        
        public final Cursor esLoadInBackground() {
            boolean flag1 = false;
            Cursor cursor = null;
            long l = EsSquaresData.queryLastSquaresSyncTimestamp(getContext(), mAccount);
            boolean flag;
            if(System.currentTimeMillis() - l > 0xdbba0L)
                flag = true;
            else
                flag = false;
            mIsDataStale = flag;
            int i = mMode;
            
            if(0 == i || 1 == i) {
            	if(0 == i) {
            		flag1 = true;
            	}
            	cursor = EsSquaresData.getInvitedSquares(getContext(), mAccount, Query.INVITATION_PROJECTION, null);
                if(!flag1 || cursor != null && cursor.getCount() > 0)
                {
                    mMode = 1;
                }
            }else if(2 == i) {
            	cursor = EsSquaresData.getJoinedSquares(getContext(), mAccount, Query.PROJECTION, null);
                if(!flag1 || cursor != null && cursor.getCount() > 0)
                {
                    mMode = 2;
                }
            } else if(3 == i) {
            	cursor = EsSquaresData.getSuggestedSquares(getContext(), mAccount, Query.PROJECTION, null);
                if(!flag1 || cursor != null && cursor.getCount() > 0)
                    mMode = 3;
            }
            
            return cursor;
           
        }

        public final int getCurrentMode()
        {
            return mMode;
        }

        public final boolean isDataStale()
        {
            return mIsDataStale;
        }

    }
    
}
