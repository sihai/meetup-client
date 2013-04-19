/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.service.CircleMembershipManager;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.fragments.PeopleSearchAdapter.SearchListAdapterListener;
import com.galaxy.meetup.client.android.ui.fragments.UnblockPersonDialog.PersonUnblocker;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.SearchViewAdapter.OnQueryChangeListener;
import com.galaxy.meetup.client.android.ui.view.SuggestionGridView;
import com.galaxy.meetup.client.util.HelpUrl;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 * 
 */
public class HostedPeopleFragment extends HostedEsFragment implements
		LoaderCallbacks, AlertDialogListener,
		CirclePropertiesFragmentDialog.CirclePropertiesListener,
		SearchListAdapterListener,
		SuggestionGridAdapter.SuggestionGridAdapterListener, PersonUnblocker,
		OnQueryChangeListener {

	private static final String CIRCLES_PROJECTION[] = {
        "circle_id", "circle_name", "contact_count", "type", "semantic_hints"
    };
    protected static ScreenMetrics sScreenMetrics;
    private PeopleSearchGridAdapter mAdapter;
    private Cursor mCircleMembers;
    protected String mCircleName;
    private CircleSpinnerAdapter mCircleSpinnerAdapter;
    private Cursor mCirclesCursor;
    private int mCurrentSpinnerPosition;
    private boolean mDataLoaded;
    protected Integer mDeleteCircleRequestId;
    private ColumnGridView mGridView;
    private final Handler mHandler;
    private boolean mIsNew;
    protected Integer mNewCircleRequestId;
    protected Integer mPendingRequestId;
    private CircleSpinnerAdapter mPrimarySpinnerAdapter;
    private boolean mRefreshSuggestedPeople;
    private int mScrollPosition;
    private boolean mSearchMode;
    private String mSelectedCircleId;
    private int mSelectedViewType;
    private final EsServiceListener mServiceListener;
    private List mShownPersonIds;
    private SuggestionGridAdapter mSuggestionAdapter;
    private SuggestionGridView mSuggestionGridView;
    private SuggestionGridView.ScrollPositions mSuggestionScrollPositions;
    private ScrollView mSuggestionScrollView;
    private boolean mViewingAsPlusPage;
    
    public HostedPeopleFragment()
    {
        this(false);
    }

    public HostedPeopleFragment(boolean flag)
    {
        mSelectedViewType = 0;
        mCurrentSpinnerPosition = -1;
        mHandler = new Handler();
        mScrollPosition = -1;
        mShownPersonIds = new ArrayList();
        mServiceListener = new EsServiceListener() {

            public final void onCreateCircleRequestComplete$6a63df5(int i, ServiceResult serviceresult)
            {
                handleNewCircleCallback(i, serviceresult);
            }

            public final void onDeleteCirclesRequestComplete$6a63df5(int i, ServiceResult serviceresult)
            {
                handleDeleteCircleCallback(i, serviceresult);
            }

            public final void onModifyCirclePropertiesRequestComplete$6a63df5(int i, ServiceResult serviceresult)
            {
                handleServiceCallback(i, serviceresult);
            }

            public final void onSetBlockedRequestComplete$6a63df5(int i, ServiceResult serviceresult)
            {
                handleServiceCallback(i, serviceresult);
            }

            public final void onSetCircleMembershipComplete$6a63df5(int i, ServiceResult serviceresult)
            {
                handleServiceCallback(i, serviceresult);
            }

        };
        mIsNew = flag;
    }

    private void changeCircleMembers(Cursor cursor)
    {
        boolean flag = true;
        if(mSelectedViewType == 0)
        {
            mSuggestionAdapter.swapCursor(cursor);
            if(cursor != null && mSuggestionGridView != null && mSuggestionScrollPositions != null)
            {
                mSuggestionGridView.setScrollPositions(mSuggestionScrollPositions);
                mSuggestionScrollPositions = null;
            }
        } else
        {
            PeopleSearchGridAdapter peoplesearchgridadapter = mAdapter;
            if(mSelectedViewType != 1 && mSelectedViewType != 2)
                flag = false;
            int _tmp = mSelectedViewType;
            peoplesearchgridadapter.changeCircleMembers$2c8bde3e(cursor, flag);
        }
    }

    private void dismissProgressDialog()
    {
        DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
        if(dialogfragment != null)
            dialogfragment.dismiss();
    }

    private static OzViews getLoggingViewFromType(int i) {
    	
    	OzViews ozviews = OzViews.UNKNOWN;
    	if(0 == i) {
    		ozviews = OzViews.PEOPLE_SEARCH;
    	} else if(1 == i) {
    		ozviews = OzViews.PEOPLE_IN_CIRCLES;
    	} else if(2 == i) {
    		ozviews = OzViews.PEOPLE_BLOCKED;
    	}
        return ozviews;
    }

    private void handleDeleteCircleCallback(int i, ServiceResult serviceresult)
    {
        if(mDeleteCircleRequestId != null && i == mDeleteCircleRequestId.intValue())
        {
            dismissProgressDialog();
            mDeleteCircleRequestId = null;
            if(serviceresult == null || !serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.toast_circle_deleted, 0).show();
            else
                Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
        }
    }

    private void handleNewCircleCallback(int i, ServiceResult serviceresult)
    {
        if(mNewCircleRequestId != null && i == mNewCircleRequestId.intValue())
        {
            dismissProgressDialog();
            mNewCircleRequestId = null;
            if(serviceresult == null || !serviceresult.hasError())
            {
                int j = R.string.toast_new_circle_created;
                Object aobj[] = new Object[1];
                aobj[0] = mCircleName;
                String s = getString(j, aobj);
                Toast.makeText(getActivity(), s, 0).show();
            } else
            {
                Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
            }
        }
    }

    private void setSearchMode(boolean flag)
    {
        if(mSearchMode != flag)
        {
            mSearchMode = flag;
            mAdapter.setQueryString(null);
            if(flag)
            {
                mAdapter.changeCircleMembers$2c8bde3e(null, true);
                getActionBar().getSearchViewAdapter().setQueryText(null);
            } else
            {
                changeCircleMembers(mCircleMembers);
            }
            invalidateActionBar();
            updateView(getView());
        }
    }

    private void showCircleMembershipDialog(String s, String s1)
    {
        startActivityForResult(Intents.getCircleMembershipActivityIntent(getActivity(), mAccount, s, s1, true), 0);
    }

    private void showProgressDialog(int i)
    {
        ProgressFragmentDialog.newInstance(null, getString(i), false).show(getFragmentManager(), "req_pending");
    }
    
    private void updateView(View view)
    {
        if(mSearchMode) {
        	mGridView.setVisibility(0);
            mSuggestionScrollView.setVisibility(8);
            showContent(view);
            return;
        }
        
        switch(mSelectedViewType)
        {
        default:
            mSuggestionScrollView.setVisibility(8);
            if(!mDataLoaded)
            {
                mGridView.setVisibility(8);
                showEmptyViewProgress(view, getString(R.string.loading));
            } else
            if(mCircleMembers == null)
                mGridView.setVisibility(8);
            else
            if(mCircleMembers.getCount() == 0)
            {
                mGridView.setVisibility(8);
                setupEmptyView(view, R.string.empty_circle);
                showEmptyView(view, getString(R.string.empty_circle));
            } else
            {
                mGridView.setVisibility(0);
                showContent(view);
            }
            break;

        case 0: // '\0'
            if(!mDataLoaded)
            {
                mGridView.setVisibility(8);
                mSuggestionScrollView.setVisibility(8);
                showEmptyViewProgress(view, getString(R.string.loading_friend_suggestions));
            } else
            {
                mGridView.setVisibility(8);
                mSuggestionScrollView.setVisibility(0);
                showContent(view);
            }
            break;
        }

    }

    public final void doDeleteCircle()
    {
        showProgressDialog(R.string.delete_circle_operation_pending);
        List arraylist = new ArrayList();
        arraylist.add(mSelectedCircleId);
        mDeleteCircleRequestId = EsService.deleteCircles(getActivity(), mAccount, (ArrayList)arraylist);
    }

    public final OzViews getViewForLogging()
    {
        return getLoggingViewFromType(mSelectedViewType);
    }

    protected final void handleServiceCallback(int i, ServiceResult serviceresult) {
        if(mPendingRequestId != null && i == mPendingRequestId.intValue()) {
        	dismissProgressDialog();
            mPendingRequestId = null;
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
        }
    }

    protected final boolean isEmpty() {
        if(!mDataLoaded || null == mCirclesCursor) {
        	return true;
        }
        boolean flag1 = mSearchMode;
        boolean flag = false;
        if(!flag1)
            switch(mSelectedViewType)
            {
            default:
                int i = mAdapter.getCount();
                flag = false;
                if(i == 0)
                    flag = true;
                break;

            case 0: // '\0'
                flag = mSuggestionAdapter.isEmpty();
                break;
            }
        return flag;
    }

    public final void onActionButtonClicked(int i) {
    	
    	if(0 == i) {
    		setSearchMode(true);
    	}
        
    }

    public final void onActivityResult(int i, int j, Intent intent) {
        if(j == -1 && i == 0)
        {
            final String personId = intent.getStringExtra("person_id");
            final String personName = intent.getStringExtra("display_name");
            final ArrayList originalCircleIds = intent.getExtras().getStringArrayList("original_circle_ids");
            final ArrayList selectedCircleIds = intent.getExtras().getStringArrayList("selected_circle_ids");
            mHandler.post(new Runnable() {

                public final void run()
                {
                    setCircleMembership(personId, personName, originalCircleIds, selectedCircleIds);
                }
                
            });
        }
        super.onActivityResult(i, j, intent);
    }

    public final void onAddPersonToCirclesAction(String s, String s1, boolean flag) {
        if(mSelectedViewType == 2) {
        	showCircleMembershipDialog(s, s1);
        } else { 
        	String s2 = EsPeopleData.getDefaultCircleId(getActivity(), mCirclesCursor, flag);
            if(s2 == null) {
            	showCircleMembershipDialog(s, s1); 
            } else { 
            	EsService.addPersonToCircle(getActivity(), mAccount, s, s1, s2);
                if(!s.startsWith("g:"))
                    setSearchMode(false);
            }
        }
    }

    public final boolean onBackPressed()
    {
        boolean flag = mSearchMode;
        boolean flag1 = false;
        if(flag)
        {
            setSearchMode(false);
            flag1 = true;
        }
        return flag1;
    }

    public final void onChangeCirclesAction(String s, String s1)
    {
        showCircleMembershipDialog(s, s1);
    }

    public final void onCirclePropertiesChange(String s, String s1, boolean flag) {
    	if(TextUtils.isEmpty(s1)) {
    		return;
    	}
    	String s2 = s1.trim();
    	if(mAdapter != null) {
    		boolean flag1 = false;
            int count = mPrimarySpinnerAdapter.getCount();
            for(int j = 0; j < count; j++) {
            	CircleSpinnerAdapter.CircleSpinnerInfo circlespinnerinfo = (CircleSpinnerAdapter.CircleSpinnerInfo)mPrimarySpinnerAdapter.getItem(j);
                if(circlespinnerinfo.id == null || circlespinnerinfo.circleType == 10 || !s2.equalsIgnoreCase(circlespinnerinfo.title) || TextUtils.equals(s, circlespinnerinfo.id)) {
                	flag1 = true;
                	break;
                }
            }
            if(flag1) {
            	Toast.makeText(getActivity(), R.string.toast_circle_already_exists, 0).show();
            }
        }
    	
    	mCircleName = s2;
        if(s == null)
        {
            showProgressDialog(R.string.new_circle_operation_pending);
            mNewCircleRequestId = EsService.createCircle(getActivity(), mAccount, s2, flag);
        } else
        {
            showProgressDialog(R.string.circle_properties_operation_pending);
            mPendingRequestId = EsService.modifyCircleProperties(getActivity(), mAccount, s, s2, flag);
        }
    	
    }

    public final void onCircleSelected(String s, CircleData circledata)
    {
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mRefreshSuggestedPeople = mIsNew;
        mViewingAsPlusPage = mAccount.isPlusPage();
        if(mViewingAsPlusPage && mSelectedViewType == 0)
            mSelectedViewType = 1;
        if(bundle != null)
        {
            mSelectedCircleId = bundle.getString("selected_circle_id");
            mSelectedViewType = bundle.getInt("selected_view_type");
            if(!mIsNew)
                mSearchMode = bundle.getBoolean("search_mode");
            mShownPersonIds = bundle.getStringArrayList("shown_persons");
            if(bundle.containsKey("request_id"))
                mPendingRequestId = Integer.valueOf(bundle.getInt("request_id"));
            if(bundle.containsKey("new_circle_request_id"))
                mNewCircleRequestId = Integer.valueOf(bundle.getInt("new_circle_request_id"));
            if(bundle.containsKey("delete_circle_request_id"))
                mDeleteCircleRequestId = Integer.valueOf(bundle.getInt("delete_circle_request_id"));
            mCircleName = bundle.getString("new_circle_name");
            mScrollPosition = bundle.getInt("scrollPos");
            mSuggestionScrollPositions = (SuggestionGridView.ScrollPositions)bundle.getParcelable("scrollPositions");
        }
        mCircleSpinnerAdapter = new CircleSpinnerAdapter(getActivity());
        mCircleSpinnerAdapter.setNotifyOnChange(false);
        LoaderManager loadermanager = getLoaderManager();
        mAdapter = new PeopleSearchGridAdapter(getActivity(), getFragmentManager(), loadermanager, mAccount);
        mAdapter.setCircleUsageType(-1);
        mAdapter.setPublicProfileSearchEnabled(true);
        mAdapter.setIncludePeopleInCircles(true);
        mAdapter.setIncludePlusPages(true);
        PeopleSearchGridAdapter peoplesearchgridadapter = mAdapter;
        boolean flag;
        if(!mViewingAsPlusPage)
            flag = true;
        else
            flag = false;
        peoplesearchgridadapter.setAddToCirclesActionEnabled(flag);
        mAdapter.setCircleSpinnerAdapter(mCircleSpinnerAdapter);
        mAdapter.setListener(this);
        mSuggestionAdapter = new SuggestionGridAdapter(getActivity(), loadermanager, mAccount, 777);
        mSuggestionAdapter.setCircleSpinnerAdapter(mCircleSpinnerAdapter);
        mSuggestionAdapter.setListener(this);
        getLoaderManager().initLoader(2, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	Loader loader = null;
    	if(1 == i) {
    		byte byte0;
            if(mSelectedViewType == 2)
                byte0 = 12;
            else
                byte0 = 1;
            loader = new CircleListLoader(getActivity(), mAccount, byte0, CIRCLES_PROJECTION);
    	} else if(2 == i) {
    		if(0 == mSelectedViewType) {
    			loader = new SuggestedPeopleListLoader(getActivity(), mAccount, SuggestionGridAdapter.PROJECTION, mRefreshSuggestedPeople);
    		} else if(1 == mSelectedViewType) {
    			loader = new PeopleListLoader(getActivity(), mAccount, PeopleSearchGridAdapter.PEOPLE_PROJECTION, mSelectedCircleId);
    		} else if(2 == mSelectedViewType) {
    			loader = new BlockedPeopleListLoader(getActivity(), mAccount, PeopleSearchGridAdapter.PEOPLE_PROJECTION, mShownPersonIds);
    		}
    	}
    	return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.hosted_people_fragment, viewgroup, false);
        mGridView = (ColumnGridView)view.findViewById(R.id.grid);
        mGridView.setAdapter(mAdapter);
        mSuggestionScrollView = (ScrollView)view.findViewById(R.id.suggestion_scroll_view);
        mSuggestionGridView = (SuggestionGridView)view.findViewById(R.id.suggestion_grid);
        mSuggestionGridView.setAdapter(mSuggestionAdapter);
        if(sScreenMetrics == null)
            sScreenMetrics = ScreenMetrics.getInstance(getActivity());
        Resources resources = getActivity().getResources();
        int i = resources.getConfiguration().orientation;
        boolean flag = false;
        if(i == 2)
            flag = true;
        if(flag)
        {
            mGridView.setOrientation(1);
            mGridView.setColumnCount(-1);
            mGridView.setMinColumnWidth(resources.getDimensionPixelSize(R.dimen.person_card_min_height));
        } else
        {
            mGridView.setOrientation(2);
            ColumnGridView columngridview = mGridView;
            int j;
            if(sScreenMetrics.screenDisplayType == 0)
                j = 1;
            else
                j = 2;
            columngridview.setColumnCount(j);
        }
        mGridView.setItemMargin(sScreenMetrics.itemMargin);
        mGridView.setPadding(sScreenMetrics.itemMargin, sScreenMetrics.itemMargin, sScreenMetrics.itemMargin, sScreenMetrics.itemMargin);
        updateView(view);
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

    public final void onDismissSuggestionAction(String s, String s1)
    {
        List arraylist = new ArrayList();
        arraylist.add(s);
        List arraylist1 = new ArrayList();
        arraylist1.add(s1);
        EsService.dismissSuggestedPeople(getActivity(), mAccount, "ANDROID_PEOPLE_SUGGESTIONS_PAGE", arraylist, arraylist1);
    }

    public final void onLoadFinished(Loader loader, Object obj) {
        Cursor cursor = (Cursor)obj;
        int id = loader.getId();
        if(1 == id) {
        	mCirclesCursor = cursor;
            mCircleSpinnerAdapter.clear();
            if(cursor != null && cursor.moveToFirst())
                do
                    mCircleSpinnerAdapter.add(new CircleSpinnerAdapter.CircleSpinnerInfo(cursor.getString(0), cursor.getString(1), cursor.getInt(3), cursor.getInt(2), 0));
                while(cursor.moveToNext());
            mCircleSpinnerAdapter.notifyDataSetChanged();
            invalidateActionBar();
        } else if(2 == id) {
        	mDataLoaded = true;
            mCircleMembers = cursor;
            mShownPersonIds.clear();
            if(mSelectedViewType == 2 && cursor != null && cursor.moveToFirst())
                do
                    mShownPersonIds.add(cursor.getString(1));
                while(cursor.moveToNext());
            if(!mSearchMode)
                changeCircleMembers(cursor);
            if(mScrollPosition != -1 && (mSearchMode || mSelectedViewType != 0))
            {
                if(mScrollPosition == 0)
                    mGridView.setSelectionToTop();
                else
                    mGridView.setSelection(mScrollPosition);
                mScrollPosition = -1;
            }
        } else {
        	updateView(getView());
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem) {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(i != R.id.delete_circle) {
        	if(i == R.id.circle_settings) {
        		int j = -1;
        		if(mCirclesCursor == null || mCirclesCursor.isClosed() || mSelectedCircleId == null || !mCirclesCursor.moveToFirst()) {
        			j = -1;
        		} else {
        	        if(!TextUtils.equals(mCirclesCursor.getString(0), mSelectedCircleId)) {
        	            // TODO
        	        	
        	        }
        	        mCircleName = mCirclesCursor.getString(1);
        	        j = mCirclesCursor.getInt(4);
        	        if(j != -1)
        	        {
        	            getActivity();
        	            String s1 = mSelectedCircleId;
        	            String s2 = mCircleName;
        	            boolean flag1;
        	            CirclePropertiesFragmentDialog circlepropertiesfragmentdialog;
        	            if((j & 0x40) == 0)
        	                flag1 = flag;
        	            else
        	                flag1 = false;
        	            circlepropertiesfragmentdialog = CirclePropertiesFragmentDialog.newInstance$50fd8769(s1, s2, flag1);
        	            circlepropertiesfragmentdialog.setTargetFragment(this, 0);
        	            circlepropertiesfragmentdialog.show(getFragmentManager(), "circle_settings");
        	        }
        		}
        	} else if(i == R.id.help)
            {
                String s = getResources().getString(R.string.url_param_help_circles);
                startExternalActivity(new Intent("android.intent.action.VIEW", HelpUrl.getHelpUrl(getActivity(), s)));
            } else
            {
                flag = false;
            }
        } else { 
        	if(mSelectedCircleId != null)
            {
                DeleteCircleConfirmationDialog deletecircleconfirmationdialog = new DeleteCircleConfirmationDialog();
                deletecircleconfirmationdialog.setTargetFragment(this, 1);
                deletecircleconfirmationdialog.show(getFragmentManager(), "delete_circle_conf");
            }
        }
        
        return flag;

    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
        CircleMembershipManager.onPeopleListVisibilityChange(false);
    }

    public final void onPersonSelected(String s)
    {
        startActivity(Intents.getProfileActivityIntent(getActivity(), mAccount, s, null));
    }

    public final void onPersonSelected(String s, String s1, PersonData persondata)
    {
        if(s1 != null)
            startExternalActivity(new Intent("android.intent.action.VIEW", Uri.withAppendedPath(android.provider.ContactsContract.Contacts.CONTENT_LOOKUP_URI, s1)));
        else
            startActivity(Intents.getProfileActivityIntent(getActivity(), mAccount, s, null));
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar) {
        if(mSearchMode) { 
        	hostactionbar.showSearchView();
            hostactionbar.getSearchViewAdapter().addOnChangeListener(this);
            return;
        }
        
        if(mPrimarySpinnerAdapter == null)
            mPrimarySpinnerAdapter = new CircleSpinnerAdapter(getActivity());
        mPrimarySpinnerAdapter.clear();
        if(mCirclesCursor == null) {
        	getActionBar().showPrimarySpinner(mPrimarySpinnerAdapter, mCurrentSpinnerPosition);
            hostactionbar.showActionButton(0, R.drawable.ic_menu_search_holo_light, R.string.menu_search);
            return;
        } else { 
        	int i;
            int j;
            int k;
            String s;
            int l;
            if(!mAccount.isPlusPage())
            {
                mPrimarySpinnerAdapter.add(new CircleSpinnerAdapter.CircleSpinnerInfo(null, getString(R.string.suggested_people_spinner_item), 0, 0, 0));
                i = 1;
            } else
            {
                i = 0;
            }
            if(!mCirclesCursor.moveToFirst()) {
            	j = 0;
            } else { 
            	k = 0;
            	do {
	            	s = mCirclesCursor.getString(0);
	                String s1 = mCirclesCursor.getString(1);
	                l = mCirclesCursor.getInt(3);
	                int i1 = mCirclesCursor.getInt(2);
	                mPrimarySpinnerAdapter.add(new CircleSpinnerAdapter.CircleSpinnerInfo(s, s1, l, i1, 0));
	                if(0 == mSelectedViewType) {
	                	
	                } else if (1 == mSelectedViewType) {
	                	if(TextUtils.equals(mSelectedCircleId, s))
	                        k = i + mCirclesCursor.getPosition();
	                } else if (2 == mSelectedViewType) {
	                	if(l == 10)
	                        k = i + mCirclesCursor.getPosition();
	                }
            	} while(mCirclesCursor.moveToNext());
            	
            	j = k;
            }
            mPrimarySpinnerAdapter.add(new CircleSpinnerAdapter.CircleSpinnerInfo(null, getString(R.string.create_new_circle), 0, 0, R.drawable.ic_add_circles));
            if(mCurrentSpinnerPosition != j)
            {
                mCurrentSpinnerPosition = -1;
                onPrimarySpinnerSelectionChange(j);
            }
            getActionBar().showPrimarySpinner(mPrimarySpinnerAdapter, mCurrentSpinnerPosition);
            hostactionbar.showActionButton(0, R.drawable.ic_menu_search_holo_light, R.string.menu_search);
        }
    }

    public final void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if(!mSearchMode && mSelectedViewType == 1 && mSelectedCircleId != null)
        {
            menu.findItem(R.id.delete_circle).setVisible(true);
            menu.findItem(R.id.circle_settings).setVisible(true);
        }
    }

    public final void onPrimarySpinnerSelectionChange(int i) {
        if(i != -1 + mPrimarySpinnerAdapter.getCount()) {
        	if(mCurrentSpinnerPosition == i) {
        		return; 
        	} else { 
        		CircleSpinnerAdapter.CircleSpinnerInfo circlespinnerinfo;
                int j;
                mCurrentSpinnerPosition = i;
                boolean flag;
                String s;
                if(mSelectedViewType == 2)
                    flag = true;
                else
                    flag = false;
                circlespinnerinfo = (CircleSpinnerAdapter.CircleSpinnerInfo)mPrimarySpinnerAdapter.getItem(i);
                if(circlespinnerinfo.id != null) {
                	if(circlespinnerinfo.circleType == 10)
                        j = 2;
                    else
                        j = 1;
                } else { 
                	j = 0;
                }
                
                if(mSelectedViewType != j)
                {
                    clearNavigationAction();
                    recordNavigationAction(getLoggingViewFromType(mSelectedViewType), getLoggingViewFromType(j), null, null, null);
                    mSelectedViewType = j;
                    boolean flag1;
                    if(mSelectedViewType == 2)
                        flag1 = true;
                    else
                        flag1 = false;
                    flag |= flag1;
                    mScrollPosition = 0;
                }
                s = null;
                if(j != 0)
                    s = circlespinnerinfo.id;
                if(!TextUtils.equals(s, mSelectedCircleId))
                {
                    mSelectedCircleId = s;
                    mScrollPosition = 0;
                }
                mDataLoaded = false;
                getLoaderManager().restartLoader(2, null, this);
                if(flag)
                    getLoaderManager().restartLoader(1, null, this);
                invalidateActionBar();
                updateView(getView());
        	}
        } else {
        	 getActionBar().setPrimarySpinnerSelection(mCurrentSpinnerPosition);
             getActivity();
             CirclePropertiesFragmentDialog circlepropertiesfragmentdialog = CirclePropertiesFragmentDialog.newInstance$47e87423();
             circlepropertiesfragmentdialog.setTargetFragment(this, 0);
             circlepropertiesfragmentdialog.show(getFragmentManager(), "new_circle_input");
        }
    }

    public final void onQueryClose()
    {
        setSearchMode(false);
    }

    public final void onQueryTextChanged(CharSequence charsequence)
    {
        if(mAdapter != null && mSearchMode)
        {
            PeopleSearchGridAdapter peoplesearchgridadapter = mAdapter;
            String s;
            if(charsequence == null)
                s = null;
            else
                s = charsequence.toString().trim();
            peoplesearchgridadapter.setQueryString(s);
        }
    }

    public final void onQueryTextSubmitted(CharSequence charsequence)
    {
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        CircleMembershipManager.onPeopleListVisibilityChange(true);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult2 = EsService.removeResult(mPendingRequestId.intValue());
            handleServiceCallback(mPendingRequestId.intValue(), serviceresult2);
            mPendingRequestId = null;
        }
        if(mNewCircleRequestId != null && !EsService.isRequestPending(mNewCircleRequestId.intValue()))
        {
            ServiceResult serviceresult1 = EsService.removeResult(mNewCircleRequestId.intValue());
            handleNewCircleCallback(mNewCircleRequestId.intValue(), serviceresult1);
            mNewCircleRequestId = null;
        }
        if(mDeleteCircleRequestId != null && !EsService.isRequestPending(mDeleteCircleRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mDeleteCircleRequestId.intValue());
            handleDeleteCircleCallback(mDeleteCircleRequestId.intValue(), serviceresult);
            mDeleteCircleRequestId = null;
        }
        updateView(getView());
        EsService.syncPeople(getActivity(), getAccount(), false);
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mAdapter != null)
            mAdapter.onSaveInstanceState(bundle);
        bundle.putString("selected_circle_id", mSelectedCircleId);
        bundle.putInt("selected_view_type", mSelectedViewType);
        bundle.putBoolean("search_mode", mSearchMode);
        bundle.putStringArrayList("shown_persons", (ArrayList)mShownPersonIds);
        if(mPendingRequestId != null)
            bundle.putInt("request_id", mPendingRequestId.intValue());
        if(mNewCircleRequestId != null)
            bundle.putInt("new_circle_request_id", mNewCircleRequestId.intValue());
        if(mDeleteCircleRequestId != null)
            bundle.putInt("delete_circle_request_id", mDeleteCircleRequestId.intValue());
        bundle.putString("new_circle_name", mCircleName);
        int i;
        if(mGridView != null)
            i = mGridView.getFirstVisiblePosition();
        else
            i = -1;
        bundle.putInt("scrollPos", i);
        if(mSuggestionGridView != null)
            bundle.putParcelable("scrollPositions", mSuggestionGridView.getScrollPositions());
    }

    public final void onSearchListAdapterStateChange(PeopleSearchAdapter peoplesearchadapter)
    {
        View view = getView();
        if(view != null)
            updateView(view);
    }

    protected final void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mSelectedViewType = bundle.getInt("people_view_type", 0);
        mSelectedCircleId = bundle.getString("circle_id");
    }

    public final void onStart()
    {
        super.onStart();
        mAdapter.onStart();
        mSuggestionAdapter.onStart();
    }

    public final void onStop()
    {
        super.onStart();
        mAdapter.onStop();
    }

    public final void onUnblockPersonAction(String s, boolean flag)
    {
        UnblockPersonDialog unblockpersondialog = new UnblockPersonDialog(s, false);
        unblockpersondialog.setTargetFragment(this, 0);
        unblockpersondialog.show(getFragmentManager(), "unblock_person");
    }

    public final boolean onUpButtonClicked()
    {
        boolean flag = mSearchMode;
        boolean flag1 = false;
        if(flag)
        {
            setSearchMode(false);
            flag1 = true;
        }
        return flag1;
    }

    protected final void setCircleMembership(String s, String s1, ArrayList arraylist, ArrayList arraylist1)
    {
        ArrayList arraylist2 = new ArrayList();
        Iterator iterator = arraylist1.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s3 = (String)iterator.next();
            if(!arraylist.contains(s3))
                arraylist2.add(s3);
        } while(true);
        ArrayList arraylist3 = new ArrayList();
        Iterator iterator1 = arraylist.iterator();
        do
        {
            if(!iterator1.hasNext())
                break;
            String s2 = (String)iterator1.next();
            if(!arraylist1.contains(s2))
                arraylist3.add(s2);
        } while(true);
        showProgressDialog(EsPeopleData.getMembershipChangeMessageId(arraylist2, arraylist3));
        mPendingRequestId = EsService.setCircleMembership(getActivity(), mAccount, s, s1, (String[])arraylist2.toArray(new String[arraylist2.size()]), (String[])arraylist3.toArray(new String[arraylist3.size()]));
        if(!s.startsWith("g:"))
            setSearchMode(false);
    }

    public final void unblockPerson(String s)
    {
        mPendingRequestId = EsService.setPersonBlocked(getActivity(), mAccount, s, null, false);
        showProgressDialog(R.string.unblock_person_operation_pending);
    }
    
    
    //==============================================================================================
    //									Inner class
    //==============================================================================================
    public static class DeleteCircleConfirmationDialog extends DialogFragment implements android.content.DialogInterface.OnClickListener {

    	public DeleteCircleConfirmationDialog()
	    {
	    }
    	
	    public void onClick(DialogInterface dialoginterface, int i) {
	    	if(-2 == i) {
	    		dialoginterface.dismiss();
	    	} else if(-1 == i) {
	    		((HostedPeopleFragment)getTargetFragment()).doDeleteCircle();
	    	}
	    }

	    public final Dialog onCreateDialog(Bundle bundle)
	    {
	        Resources resources = getResources();
	        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
	        int i = R.plurals.delete_circles_dialog_title;
	        Object aobj[] = new Object[1];
	        aobj[0] = Integer.valueOf(1);
	        builder.setTitle(resources.getQuantityString(i, 1, aobj));
	        int j = R.plurals.delete_circles_dialog_message;
	        Object aobj1[] = new Object[1];
	        aobj1[0] = Integer.valueOf(1);
	        builder.setMessage(resources.getQuantityString(j, 1, aobj1));
	        builder.setPositiveButton(0x104000a, this);
	        builder.setNegativeButton(0x1040000, this);
	        builder.setCancelable(true);
	        return builder.create();
	    }
	
	}
}
