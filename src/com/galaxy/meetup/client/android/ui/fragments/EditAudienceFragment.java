/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.EsCompositeCursorAdapter;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.ui.view.AudienceView;
import com.galaxy.meetup.client.android.ui.view.CheckableListItemView;
import com.galaxy.meetup.client.android.ui.view.CheckableListItemView.OnItemCheckedChangeListener;
import com.galaxy.meetup.client.android.ui.view.CircleListItemView;
import com.galaxy.meetup.client.android.ui.view.PeopleListItemView;
import com.galaxy.meetup.client.android.ui.view.SectionHeaderView;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.StringUtils;

/**
 * 
 * @author sihai
 *
 */
public class EditAudienceFragment extends EsFragment implements
		LoaderCallbacks, OnScrollListener, OnItemClickListener,
		OnItemCheckedChangeListener {

	private EditAudienceAdapter mAdapter;
    private Runnable mAudienceChangedCallback;
    private boolean mAudienceSet;
    private AudienceView mAudienceView;
    private ImageCache mAvatarCache;
    private final DataSetObserver mCircleContentObserver = new DataSetObserver() {

        public final void onChanged()
        {
            updateView(getView());
        }

    };
    private CircleNameResolver mCircleNameResolver;
    private boolean mCircleSelectionEnabled;
    private int mCircleUsageType;
    private boolean mFilterNullGaiaIds;
    private boolean mIncludePlusPages;
    private boolean mIncomingAudienceIsReadOnly;
    private ListView mListView;
    private OnAudienceChangeListener mListener;
    private boolean mLoaderError;
    private boolean mLoadersInitialized;
    private final Map mSelectedCircles = new HashMap();
    private final Map mSelectedPeople = new HashMap();
    
    
    public EditAudienceFragment()
    {
        mAudienceChangedCallback = new Runnable() {

            public final void run()
            {
                AudienceData audiencedata;
                AudienceData audiencedata1;
                int i;
                int j;
                int k;
                int l;
                boolean flag;
                audiencedata = getAudience();
                audiencedata1 = getAudienceFromList();
                i = audiencedata.getCircleCount();
                j = audiencedata1.getCircleCount();
                k = audiencedata.getUserCount();
                l = audiencedata1.getUserCount();
                flag = false;
                if(i != j) {
                	flag = false;
                    if(k != l) {
                    	if(!flag)
                        {
                            setAudience(getAudience());
                            mAdapter.notifyDataSetChanged();
                        }
                        return;
                    }
                    ArrayList arraylist = new ArrayList();
                    CircleData acircledata[] = audiencedata1.getCircles();
                    int i1 = acircledata.length;
                    for(int j1 = 0; j1 < i1; j1++)
                        arraylist.add(acircledata[j1].getId());

                    CircleData acircledata1[] = audiencedata.getCircles();
                    int k1 = acircledata1.length;
                    for(int l1 = 0; l1 < k1; l1++)
                    {
                        boolean flag2 = arraylist.contains(acircledata1[l1].getId());
                        flag = false;
                        if(!flag2) {
                        	if(!flag)
                            {
                                setAudience(getAudience());
                                mAdapter.notifyDataSetChanged();
                            }
                            return;
                        }
                    }

                    ArrayList arraylist1 = new ArrayList();
                    PersonData apersondata[] = audiencedata1.getUsers();
                    int i2 = apersondata.length;
                    for(int j2 = 0; j2 < i2; j2++)
                        arraylist1.add(apersondata[j2].getObfuscatedId());

                    PersonData apersondata1[] = audiencedata.getUsers();
                    int k2 = apersondata1.length;
                    for(int l2 = 0; l2 < k2; l2++)
                    {
                        boolean flag1 = arraylist1.contains(apersondata1[l2].getObfuscatedId());
                        flag = false;
                        if(!flag1) {
                        	if(!flag)
                            {
                                setAudience(getAudience());
                                mAdapter.notifyDataSetChanged();
                            }
                            return;
                        }
                    }

                    flag = true;
                }
                
                if(!flag)
                {
                    setAudience(getAudience());
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    private void addToSelectedCircles(CircleListItemView circlelistitemview)
    {
        String s = circlelistitemview.getCircleId();
        CircleData circledata = new CircleData(s, circlelistitemview.getCircleType(), circlelistitemview.getCircleName(), circlelistitemview.getMemberCount());
        mSelectedCircles.put(s, circledata);
    }

    private EsAccount getAccount()
    {
        return (EsAccount)getActivity().getIntent().getExtras().get("account");
    }

    private AudienceData getAudienceFromList()
    {
        ArrayList arraylist = new ArrayList(mSelectedCircles.size());
        for(Iterator iterator = mSelectedCircles.values().iterator(); iterator.hasNext(); arraylist.add((CircleData)iterator.next()));
        ArrayList arraylist1 = new ArrayList(mSelectedPeople.size());
        for(Iterator iterator1 = mSelectedPeople.values().iterator(); iterator1.hasNext(); arraylist1.add((PersonData)iterator1.next()));
        return new AudienceData(arraylist1, arraylist);
    }

    private boolean isLoading() {
        {
            if(mAdapter != null && (!mCircleSelectionEnabled || mAdapter.getCursor(1) != null) && mAdapter.getCursor(2) != null && mAdapter.getCursor(0) != null)
            {
                boolean flag1 = mCircleNameResolver.isLoaded();
                if(flag1)
                    return false;
            }
        }
        return true;
    }

    private void updateSelectionCount() {
        // TODO
    }

    public final void addSelectedCircle(String s, CircleData circledata) {
        mSelectedCircles.put(s, circledata);
        updateSelectionCount();
    }

    public final void addSelectedPerson(String s, PersonData persondata) {
        mSelectedPeople.put(s, persondata);
        if(mLoadersInitialized)
            getLoaderManager().restartLoader(0, null, this);
        updateSelectionCount();
    }

    public final AudienceData getAudience()
    {
        return mAudienceView.getAudience();
    }

    public final boolean hasAudience()
    {
        return mAudienceSet;
    }

    protected final boolean isEmpty() {
    	
    	if(!isLoading())
        {
            boolean flag1 = mAdapter.isPartitionEmpty(1);
            if(!flag1)
                return false;
            boolean flag2 = mAdapter.isPartitionEmpty(2);
            if(!flag2)
            	return false;
            boolean flag3 = mAdapter.isPartitionEmpty(0);
            if(!flag3)
            	return false;
        }
    	
        return true;
    }

    public final boolean isSelectionValid()
    {
        boolean flag;
        if(!mSelectedPeople.isEmpty() || !mSelectedCircles.isEmpty())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mAdapter = new EditAudienceAdapter(activity);
        mAdapter.addPartition(false, true);
        mAdapter.addPartition(false, true);
        mAdapter.addPartition(false, false);
        mAvatarCache = ImageCache.getInstance(activity);
        mCircleNameResolver = new CircleNameResolver(activity, getLoaderManager(), getAccount());
        mCircleNameResolver.registerObserver(mCircleContentObserver);
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
            setAudience((AudienceData)bundle.getParcelable("audience"));
        getLoaderManager().initLoader(0, null, this);
        if(mCircleSelectionEnabled)
            getLoaderManager().initLoader(1, null, this);
        getLoaderManager().initLoader(2, null, this);
        mCircleNameResolver.initLoader();
        mLoadersInitialized = true;
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	
    	Loader loader = null;
    	if(0 == i) {
    		loader = new PeopleNotInCirclesLoader(getActivity(), getAccount(), new String[] {
                "_id", "name", "person_id", "gaia_id"
            }, mSelectedPeople, mFilterNullGaiaIds);
    	} else if(1 == i) {
    		loader = new CircleListLoader(getActivity(), getAccount(), mCircleUsageType, new String[] {
                "_id", "circle_name", "circle_id", "type", "contact_count"
            });
    	} else if(2 == i) {
    		loader = new PeopleListLoader(getActivity(), getAccount(), new String[] {
                "_id", "name", "person_id", "gaia_id", "packed_circle_ids"
            }, null, mIncludePlusPages, mFilterNullGaiaIds);
    	}
    	
        return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.edit_audience_fragment, viewgroup, false);
        mAudienceView = new AudienceView(getActivity(), null, 0, true);
        mAudienceView.setAccount(getAccount());
        mAudienceView.setAudienceChangedCallback(mAudienceChangedCallback);
        mAudienceView.findViewById(R.id.audience_to_text).setVisibility(8);
        mAudienceView.findViewById(R.id.edit_audience).setVisibility(8);
        mListView = (ListView)view.findViewById(0x102000a);
        mListView.addHeaderView(mAudienceView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        return view;
    }

    public final void onItemCheckedChanged(final CheckableListItemView view, boolean flag)
    {
        if((view instanceof CircleListItemView)) {
        	final CircleListItemView item = (CircleListItemView)view;
            String s4 = item.getCircleId();
            if(flag)
            {
                final FragmentActivity activity = getActivity();
                final EsAccount account = getAccount();
                if(AccountsUtil.isRestrictedCircleForAccount(account, item.getCircleType()) && !EsAccountsData.hasSeenMinorPublicExtendedDialog(activity, account))
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                    builder.setTitle(item.getCircleName());
                    builder.setMessage(R.string.dialog_public_or_extended_circle_for_minor);
                    builder.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

                        public final void onClick(DialogInterface dialoginterface, int i)
                        {
                            addToSelectedCircles(item);
                            EsAccountsData.saveMinorPublicExtendedDialogSeenPreference(activity, account, true);
                        }

                    });
                    builder.setNegativeButton(R.string.cancel, new android.content.DialogInterface.OnClickListener() {

                        public final void onClick(DialogInterface dialoginterface, int i)
                        {
                            view.setChecked(false);
                        }
                    });
                    builder.show();
                } else
                {
                    addToSelectedCircles(item);
                }
            } else
            {
                mSelectedCircles.remove(s4);
            }
        } else if(view instanceof PeopleListItemView) {
        	PeopleListItemView peoplelistitemview = (PeopleListItemView)view;
        	String s = peoplelistitemview.getPersonId();
        	if(!flag) {
        		mSelectedPeople.remove(s); 
        	} else { 
        		String s2 = null;
        		String s1 = peoplelistitemview.getGaiaId();
                if(!s.startsWith("e:")) { 
                	boolean flag1 = s.startsWith("p:");
                    if(flag1)
                        s2 = s;
                } else {
                	s2 = s.substring(2);
                }
                	
                String s3 = peoplelistitemview.getContactName();
                mSelectedPeople.put(s, new PersonData(s1, s3, s2));
        	}
        }
        
        updateSelectionCount();
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        if(view instanceof Checkable)
            ((Checkable)view).toggle();
    }

    public final void onLoadFinished(Loader loader, Object obj) {
        Cursor cursor = (Cursor)obj;
        boolean flag;
        if(cursor == null)
            flag = true;
        else
            flag = false;
        mLoaderError = flag;
        int id = loader.getId();
        if(0 == id) {
        	mAdapter.changeCursor(0, cursor);
            updateView(getView());
        } else if(1 == id) {
        	mAdapter.changeCursor(1, cursor);
            updateView(getView());
        } else if(2 == id) {
        	mAdapter.changeCursor(2, cursor);
            updateView(getView());
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onResume()
    {
        super.onResume();
        updateView(getView());
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("audience", getAudienceFromList());
    }

    public void onScroll(AbsListView abslistview, int i, int j, int k)
    {
    }

    public void onScrollStateChanged(AbsListView abslistview, int i)
    {
        if(i == 2)
            mAvatarCache.pause();
        else
            mAvatarCache.resume();
    }

    public final void setAudience(AudienceData audiencedata)
    {
        mAudienceSet = true;
        mSelectedPeople.clear();
        mSelectedCircles.clear();
        if(audiencedata != null)
        {
            CircleData acircledata[] = audiencedata.getCircles();
            int i = acircledata.length;
            for(int j = 0; j < i; j++)
            {
                CircleData circledata = acircledata[j];
                mSelectedCircles.put(circledata.getId(), circledata);
            }

            PersonData apersondata[] = audiencedata.getUsers();
            int k = apersondata.length;
            int l = 0;
            while(l < k) 
            {
                PersonData persondata = apersondata[l];
                String s = persondata.getObfuscatedId();
                String s1;
                if(!TextUtils.isEmpty(s))
                {
                    s1 = (new StringBuilder("g:")).append(s).toString();
                } else
                {
                    boolean flag = TextUtils.isEmpty(persondata.getEmail());
                    s1 = null;
                    if(!flag)
                    {
                        String s2 = persondata.getEmail();
                        if(s2.startsWith("p:"))
                            s1 = s2;
                        else
                            s1 = (new StringBuilder("e:")).append(s2).toString();
                    }
                }
                if(s1 != null)
                    mSelectedPeople.put(s1, persondata);
                l++;
            }
        }
        updateSelectionCount();
    }

    public final void setCircleSelectionEnabled(boolean flag)
    {
        mCircleSelectionEnabled = true;
    }

    public final void setCircleUsageType(int i)
    {
        mCircleUsageType = i;
    }

    public final void setFilterNullGaiaIds(boolean flag)
    {
        mFilterNullGaiaIds = flag;
    }

    public final void setIncludePlusPages(boolean flag)
    {
        mIncludePlusPages = flag;
    }

    public final void setIncomingAudienceIsReadOnly(boolean flag)
    {
        mIncomingAudienceIsReadOnly = flag;
    }

    public final void setOnSelectionChangeListener(OnAudienceChangeListener onaudiencechangelistener)
    {
        mListener = onaudiencechangelistener;
    }

    protected final void updateView(View view)
    {
        View view1 = view.findViewById(0x102000a);
        View view2 = view.findViewById(R.id.server_error);
        if(mLoaderError)
        {
            view1.setVisibility(8);
            view2.setVisibility(0);
            showContent(view);
        } else
        if(isLoading())
        {
            view1.setVisibility(8);
            view2.setVisibility(8);
            showEmptyViewProgress(view);
        } else
        if(isEmpty())
        {
            view1.setVisibility(8);
            view2.setVisibility(8);
            setupEmptyView(view, R.string.no_people_in_circles);
            showEmptyView(view);
        } else
        {
            view1.setVisibility(0);
            view2.setVisibility(8);
            showContent(view);
        }
        updateSelectionCount();
    }
    
    private final class EditAudienceAdapter extends EsCompositeCursorAdapter implements SectionIndexer {
    	
    	private EsAlphabetIndexer mIndexer;

        public EditAudienceAdapter(Context context)
        {
            super(context);
        }

	    protected final void bindView(View view, int i, Cursor cursor, int j) {
	    	
	    	if(0 == i) {
	    		PeopleListItemView peoplelistitemview1 = (PeopleListItemView)view;
		        peoplelistitemview1.setCircleNameResolver(mCircleNameResolver);
		        String s3 = cursor.getString(2);
		        peoplelistitemview1.setPersonId(s3);
		        String s4 = cursor.getString(3);
		        if(!TextUtils.isEmpty(s4))
		            peoplelistitemview1.setGaiaId(s4);
		        peoplelistitemview1.setContactName(cursor.getString(1));
		        boolean flag2 = mSelectedPeople.containsKey(s3);
		        peoplelistitemview1.setChecked(flag2);
		        peoplelistitemview1.updateContentDescription();
		        boolean flag3;
		        if(!flag2 || !mIncomingAudienceIsReadOnly)
		            flag3 = true;
		        else
		            flag3 = false;
		        peoplelistitemview1.setEnabled(flag3);
	    	} else if(1 == i) {
	    		CircleListItemView circlelistitemview = (CircleListItemView)view;
		        String s = cursor.getString(2);
		        int k = cursor.getInt(3);
		        circlelistitemview.setCircle(s, k, cursor.getString(1), cursor.getInt(4), AccountsUtil.isRestrictedCircleForAccount(getAccount(), k));
		        circlelistitemview.setChecked(mSelectedCircles.containsKey(s));
		        circlelistitemview.updateContentDescription();
	    	} else if(2 == i) {
	    		PeopleListItemView peoplelistitemview = (PeopleListItemView)view;
		        peoplelistitemview.setCircleNameResolver(mCircleNameResolver);
		        String s1 = cursor.getString(2);
		        peoplelistitemview.setPersonId(s1);
		        peoplelistitemview.setGaiaId(cursor.getString(3));
		        String s2 = cursor.getString(1);
		        peoplelistitemview.setContactName(s2);
		        peoplelistitemview.setPackedCircleIds(cursor.getString(4));
		        boolean flag = mSelectedPeople.containsKey(s1);
		        peoplelistitemview.setChecked(flag);
		        boolean flag1;
		        char c;
		        if(!flag || !mIncomingAudienceIsReadOnly)
		            flag1 = true;
		        else
		            flag1 = false;
		        peoplelistitemview.setEnabled(flag1);
		        c = StringUtils.firstLetter(s2);
		        if(!cursor.moveToPrevious())
		            peoplelistitemview.setSectionHeader(c);
		        else
		        if(StringUtils.firstLetter(cursor.getString(1)) != c)
		            peoplelistitemview.setSectionHeader(c);
		        else
		            peoplelistitemview.setSectionHeaderVisible(false);
		        peoplelistitemview.updateContentDescription();
	    	}
	    	
	    }
	
	    public final void changeCursor(int i, Cursor cursor)
	    {
	        if(i == 2 && cursor != null)
	            mIndexer = new EsAlphabetIndexer(cursor, 1);
	        super.changeCursor(i, cursor);
	    }
	
	    protected final int getItemViewType(int i, int j)
	    {
	        return i;
	    }
	
	    public final int getItemViewTypeCount()
	    {
	        return 3;
	    }
	
	    public final int getPositionForSection(int i)
	    {
	        int j;
	        if(i == 0 || mIndexer == null)
	            j = 0;
	        else
	            j = getPositionForPartition(2) + mIndexer.getPositionForSection(i - 1);
	        return j;
	    }
	
	    public final int getSectionForPosition(int i) {
	    	int j = 0;
	    	EsAlphabetIndexer esalphabetindexer = mIndexer;
	    	if(null == esalphabetindexer) {
	    		return j;
	    	}
	    	
	    	int k = getPositionForPartition(2);
	        j = 0;
	        if(i >= k)
	            j = 1 + mIndexer.getSectionForPosition(i - k);
	        return j;
	    }
	
	    public final Object[] getSections()
	    {
	        Object aobj1[];
	        if(mIndexer == null)
	        {
	            aobj1 = null;
	        } else
	        {
	            Object aobj[] = mIndexer.getSections();
	            aobj1 = new Object[1 + aobj.length];
	            aobj1[0] = "?";
	            System.arraycopy(((Object) (aobj)), 0, ((Object) (aobj1)), 1, aobj.length);
	        }
	        return aobj1;
	    }
	
	    @Override
	    protected final View newHeaderView(Context context, int partion, Cursor curosr, ViewGroup viewgroup) {
	        SectionHeaderView sectionheaderview = (SectionHeaderView)LayoutInflater.from(context).inflate(R.layout.section_header, viewgroup, false);
	        if(0 == partion) {
	        	sectionheaderview.setText(R.string.edit_audience_header_added);
	        } else if(1 == partion) {
	        	sectionheaderview.setText(R.string.edit_audience_header_circles);
	        } 
	        return sectionheaderview;
	    }
	
	    protected final View newView(Context context, int partion, Cursor cursor, int position, ViewGroup parent) {
	    	
	    	View view = null;
	    	if(0 == partion || 2 == partion) {
	    		view = PeopleListItemView.createInstance(context);
		        ((PeopleListItemView) (view)).setOnItemCheckedChangeListener(EditAudienceFragment.this);
		        ((PeopleListItemView) (view)).setCheckBoxVisible(true);
		        ((PeopleListItemView) (view)).setCircleNameResolver(mCircleNameResolver);
	    	} else if(1 == partion) {
	    		view = new CircleListItemView(context);
	 	        ((CircleListItemView) (view)).setOnItemCheckedChangeListener(EditAudienceFragment.this);
	 	        ((CircleListItemView) (view)).setCheckBoxVisible(true);
	 	        ((CircleListItemView) (view)).updateContentDescription();
	    	}
	    	return view;
	    }
	}

	public static interface OnAudienceChangeListener
	{
	
	    public abstract void onAudienceChanged(String s);
	}
}
