/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Patterns;
import android.widget.Filter;
import android.widget.Filterable;

import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.EsCompositeCursorAdapter;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.MentionTokenizer;

/**
 * 
 * @author sihai
 *
 */
public abstract class PeopleSearchAdapter extends EsCompositeCursorAdapter implements
		LoaderCallbacks, Filterable, AlertDialogListener {

	private static final String CIRCLES_PROJECTION[] = {
        "_id", "circle_id", "type", "circle_name", "contact_count"
    };
    private static final String CONTACT_PROJECTION[] = {
        "person_id", "lookup_key", "name", "email"
    };
    private static final String CONTACT_PROJECTION_WITH_PHONE[] = {
        "person_id", "lookup_key", "name", "email", "phone", "phone_type"
    };
    private static final String GAIA_ID_CIRCLE_PROJECTION[] = {
        "gaia_id", "packed_circle_ids"
    };
    private static final String LOCAL_PROFILE_PROJECTION[] = {
        "_id", "person_id", "gaia_id", "name", "avatar", "packed_circle_ids", "email", "profile_type"
    };
    public static final String PEOPLE_PROJECTION[] = {
        "_id", "person_id", "gaia_id", "name", "avatar", "packed_circle_ids", "blocked", "profile_type"
    };
    private static final String PUBLIC_PROFILE_PROJECTION[] = {
        "_id", "gaia_id", "person_id", "name", "avatar", "profile_type", "snippet"
    };
    protected final EsAccount mAccount;
    private int mActiveLoaderCount;
    private String mActivityId;
    protected boolean mAddToCirclesActionEnabled;
    private final DataSetObserver mCircleContentObserver;
    protected final CircleNameResolver mCircleNameResolver;
    private int mCircleUsageType;
    private boolean mCirclesError;
    private boolean mCirclesLoaded;
    private final int mCirclesLoaderId;
    private Cursor mContactsCursor;
    private boolean mContactsError;
    private boolean mContactsLoaded;
    private final int mContactsLoaderId;
    private Filter mFilter;
    private volatile CountDownLatch mFilterLatch;
    private boolean mFilterNullGaiaIds;
    private final FragmentManager mFragmentManager;
    private Cursor mGaiaIdCircleCursor;
    private final int mGaiaIdLoaderId;
    private final Handler mHandler;
    private boolean mIncludePeopleInCircles;
    protected boolean mIncludePhoneNumberContacts;
    private boolean mIncludePlusPages;
    private boolean mIsMentionsAdapter;
    protected SearchListAdapterListener mListener;
    private final LoaderManager mLoaderManager;
    private boolean mLocalProfileError;
    private Cursor mLocalProfilesCursor;
    private boolean mLocalProfilesLoaded;
    private final int mPeopleLoaderId;
    private final int mProfilesLoaderId;
    private boolean mPublicProfileSearchEnabled;
    private Cursor mPublicProfilesCursor;
    private boolean mPublicProfilesError;
    private boolean mPublicProfilesLoading;
    private boolean mPublicProfilesNotFound;
    protected String mQuery;
    private PeopleSearchResults mResults;
    private boolean mResultsPreserved;
    private boolean mShowPersonNameDialog;
    private boolean mShowProgressWhenEmpty;
    
    public PeopleSearchAdapter(Context context, FragmentManager fragmentmanager, LoaderManager loadermanager, EsAccount esaccount)
    {
        this(context, fragmentmanager, loadermanager, esaccount, 0);
    }

    public PeopleSearchAdapter(Context context, FragmentManager fragmentmanager, LoaderManager loadermanager, EsAccount esaccount, int i) {
    	super(context);
        SearchResultsFragment searchresultsfragment;
        mCircleUsageType = -1;
        mShowProgressWhenEmpty = true;
        mShowPersonNameDialog = true;
        mIncludePeopleInCircles = true;
        mResults = new PeopleSearchResults();
        mHandler = new Handler() {

            public final void handleMessage(Message message) {
            	if(0 == message.what) {
            		showEmptyPeopleSearchResults();
            	} else if(1 == message.what) {
            		// TODO
            	}
            }
        };
        
        mCircleContentObserver = new DataSetObserver() {

            public final void onChanged()
            {
                notifyDataSetChanged();
            }
        };
        
        for(int j = 0; j < 6; j++)
            addPartition(false, false);

        int k = 1024 + i * 10;
        int l = k + 1;
        mCirclesLoaderId = k;
        int i1 = l + 1;
        mGaiaIdLoaderId = l;
        int j1 = i1 + 1;
        mPeopleLoaderId = i1;
        int k1 = j1 + 1;
        mContactsLoaderId = j1;
        mProfilesLoaderId = k1;
        searchresultsfragment = (SearchResultsFragment)fragmentmanager.findFragmentByTag("people_search_results");
        if(searchresultsfragment != null) {
        	PeopleSearchResults peoplesearchresults = searchresultsfragment.getPeopleSearchResults();
            if(peoplesearchresults != null)
            {
                mResults = peoplesearchresults;
                mQuery = mResults.getQuery();
                mResultsPreserved = true;
            } 
        } else { 
        	searchresultsfragment = new SearchResultsFragment();
            fragmentmanager.beginTransaction().add(searchresultsfragment, "people_search_results").commitAllowingStateLoss();
        }
        
        searchresultsfragment.setPeopleSearchResults(mResults);
        mFragmentManager = fragmentmanager;
        mLoaderManager = loadermanager;
        mAccount = esaccount;
        mResults.setMyProfile(mAccount.getPersonId());
        mResults.setIncludePeopleInCircles(mIncludePeopleInCircles);
        mCircleNameResolver = new CircleNameResolver(context, loadermanager, mAccount, i);
        mCircleNameResolver.registerObserver(mCircleContentObserver);
        return;
    }
    
    private void changeCursorForPeoplePartition() {
        mHandler.removeMessages(0);
        Cursor cursor = mResults.getCursor();
        if(cursor.getCount() == 0)
            mHandler.sendEmptyMessageDelayed(0, 500L);
        else
            changeCursor(4, cursor);
    }
    
    private String getWellFormedEmailAddress() {
        if(TextUtils.isEmpty(mQuery)) {
        	return null;
        }
        
        Rfc822Token arfc822token[] = Rfc822Tokenizer.tokenize(mQuery);
        if(arfc822token != null && arfc822token.length > 0) {
            String s = arfc822token[0].getAddress();
            if(!TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches())
                return s;
        }
        return null;
    }
    
    private String getWellFormedSmsAddress() {
        boolean flag;
        flag = TextUtils.isEmpty(mQuery);
        if(flag) {
        	return null;
        }
        
        String s;
        boolean flag1 = PhoneNumberUtils.isWellFormedSmsAddress(mQuery);
        s = null;
        if(!flag1)
            return null;
        int i = mQuery.length();
        boolean flag2 = true;
        for(int j = 0; j < i;)
        {
            char c = mQuery.charAt(j);
            boolean flag3 = PhoneNumberUtils.isDialable(c);
            s = null;
            if(!flag3)
                continue; /* Loop/switch isn't completed */
            if(c == '+')
            {
                s = null;
                if(!flag2)
                    continue; /* Loop/switch isn't completed */
            }
            j++;
            flag2 = false;
        }

        s = mQuery;

        return s;
    }

    private void releaseLatch()
    {
        CountDownLatch countdownlatch = mFilterLatch;
        if(countdownlatch != null)
            countdownlatch.countDown();
    }

    private void updatePublicProfileSearchStatus() {
        if(!mPublicProfileSearchEnabled) {
        	return;
        }
        EsMatrixCursor esmatrixcursor;
        esmatrixcursor = new EsMatrixCursor(new String[] {
            "_id"
        });
        if(!TextUtils.isEmpty(mQuery) && mQuery.trim().length() >= 2 && mLocalProfilesLoaded && mContactsLoaded)
        {
            if(mPublicProfilesError) {
	            Object aobj2[] = new Object[1];
	            aobj2[0] = Integer.valueOf(3);
	            esmatrixcursor.addRow(aobj2);
            } else {
            	if(mPublicProfilesNotFound && !mIsMentionsAdapter)
                {
                    Object aobj1[] = new Object[1];
                    aobj1[0] = Integer.valueOf(2);
                    esmatrixcursor.addRow(aobj1);
                } else
                if(mPublicProfilesLoading && !mIsMentionsAdapter && (mShowProgressWhenEmpty || mResults.getCount() > 0))
                {
                    Object aobj[] = new Object[1];
                    aobj[0] = Integer.valueOf(1);
                    esmatrixcursor.addRow(aobj);
                }
            }
        }
        
        if(esmatrixcursor.getCount() != 0)
            showEmptyPeopleSearchResults();
        changeCursor(5, esmatrixcursor);
    }

    protected final void continueLoadingPublicProfiles() {
        if(mResults.hasMoreResults())
            mHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
				}
            	
            } );
    }

    public Filter getFilter()
    {
        if(mFilter == null)
            mFilter = new Filter() {

                public final CharSequence convertResultToString(Object obj)
                {
                    Cursor cursor = (Cursor)obj;
                    String s;
                    if(cursor == null || cursor.isClosed())
                    {
                        s = "";
                    } else
                    {
                        int i = cursor.getColumnIndex("circle_name");
                        if(i != -1)
                        {
                            s = cursor.getString(i);
                        } else
                        {
                            int j = cursor.getColumnIndex("name");
                            if(j != -1)
                            {
                                s = cursor.getString(j);
                            } else
                            {
                                int k = cursor.getColumnIndex("address");
                                if(k != -1)
                                    s = cursor.getString(k);
                                else
                                    s = "";
                            }
                        }
                    }
                    return s;
                }

                protected final android.widget.Filter.FilterResults performFiltering(CharSequence charsequence) {
                	final CharSequence queryString = charsequence;
                    releaseLatch();
                    CountDownLatch countdownlatch = new CountDownLatch(1);
                    mFilterLatch = countdownlatch;
                    mHandler.post(new Runnable() {

                        public final void run()
                        {
                            String s;
                            if(queryString == null)
                                s = null;
                            else
                            if(mIsMentionsAdapter)
                            {
                                int i = queryString.length();
                                if(i > 0 && MentionTokenizer.isMentionTrigger(queryString.charAt(0)))
                                    s = queryString.subSequence(1, i).toString();
                                else
                                    s = null;
                            } else
                            {
                                s = queryString.toString();
                            }
                            setQueryString(s);
                        }

                    });
                    try
                    {
                        countdownlatch.await();
                    }
                    catch(InterruptedException interruptedexception) { }
                    mFilterLatch = null;
                    return new android.widget.Filter.FilterResults();
                }

                protected final void publishResults(CharSequence charsequence, android.widget.Filter.FilterResults filterresults)
                {
                    filterresults.count = getCount();
                }
            };
        return mFilter;
    }

    protected final int getItemViewType(int i, int j)
    {
        return i;
    }

    public final int getItemViewTypeCount()
    {
        return 6;
    }

    public boolean isEmpty()
    {
        boolean flag;
        if(TextUtils.isEmpty(mQuery) || !mCircleNameResolver.isLoaded())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isError()
    {
        boolean flag;
        if(mCirclesError || mLocalProfileError || mContactsError)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isLoaded()
    {
        boolean flag;
        if(mLocalProfilesLoaded && mContactsLoaded && (mCircleUsageType == -1 || mCirclesLoaded) && mCircleNameResolver.isLoaded())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isSearchingForFirstResult()
    {
        boolean flag;
        if(!TextUtils.isEmpty(mQuery) && mResults.getCount() == 0 && (!isLoaded() || mPublicProfilesLoading))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onCreate(Bundle bundle)
    {
        if(bundle != null)
        {
            bundle.setClassLoader(getClass().getClassLoader());
            mQuery = bundle.getString("search_list_adapter.query");
            if(bundle.containsKey("search_list_adapter.results") && !mResultsPreserved)
                mResults = (PeopleSearchResults)bundle.getParcelable("search_list_adapter.results");
        }
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        Object obj;
        if(i == mCirclesLoaderId)
            obj = new CircleListLoader(getContext(), mAccount, mCircleUsageType, CIRCLES_PROJECTION, mQuery, 10);
        else
        if(i == mGaiaIdLoaderId)
            obj = new PeopleListLoader(getContext(), mAccount, GAIA_ID_CIRCLE_PROJECTION, null, mIncludePlusPages, mFilterNullGaiaIds);
        else
        if(i == mContactsLoaderId)
        {
            Context context = getContext();
            EsAccount _tmp = mAccount;
            String as[];
            if(mIncludePhoneNumberContacts)
                as = CONTACT_PROJECTION_WITH_PHONE;
            else
                as = CONTACT_PROJECTION;
            obj = new AndroidContactSearchLoader(context, as, mQuery, 2, mIncludePhoneNumberContacts);
        } else
        if(i == mPeopleLoaderId)
            obj = new PeopleSearchListLoader(getContext(), mAccount, LOCAL_PROFILE_PROJECTION, mQuery, mIncludePlusPages, mIncludePeopleInCircles, mFilterNullGaiaIds, mActivityId, 10);
        else
        if(i == mProfilesLoaderId)
            obj = new PublicProfileSearchLoader(getContext(), mAccount, PUBLIC_PROFILE_PROJECTION, mQuery, 2, mIncludePlusPages, mFilterNullGaiaIds, mResults.getToken());
        else
            obj = null;
        return ((Loader) (obj));
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
    
    public final void onDialogPositiveClick(Bundle bundle, String s) {
        if(!"add_email_dialog".equals(s)) {
        	if("add_sms_dialog".equals(s))
            {
                String s1 = bundle.getString("message");
                String s2 = getWellFormedSmsAddress();
                if(!TextUtils.isEmpty(s1) && !TextUtils.isEmpty(s2))
                {
                    String s3 = (new StringBuilder("p:")).append(s2).toString();
                    if(mAddToCirclesActionEnabled)
                    {
                        mListener.onChangeCirclesAction(s3, s1);
                    } else
                    {
                        PersonData persondata = new PersonData(null, s1, s3);
                        mListener.onPersonSelected(s3, null, persondata);
                    }
                }
            } 
        } else {
        	String s4 = bundle.getString("message");
            String s5 = getWellFormedEmailAddress();
            if(!TextUtils.isEmpty(s4) && !TextUtils.isEmpty(s5))
            {
                String s6 = (new StringBuilder("e:")).append(s5).toString();
                if(mAddToCirclesActionEnabled)
                {
                    mListener.onChangeCirclesAction(s6, s4);
                } else
                {
                    PersonData persondata1 = new PersonData(null, s4, s5);
                    mListener.onPersonSelected(s6, null, persondata1);
                }
            }
        }

    }

    public final void onItemClick(int i) {
        Cursor cursor = (Cursor)getItem(i);
        if(null == cursor) {
        	return;
        }
        
        switch(getPartitionForPosition(i))
        {
        case 0: // '\0'
            String s11 = cursor.getString(1);
            PersonData persondata3 = new PersonData(cursor.getString(2), cursor.getString(3), null);
            mListener.onPersonSelected(s11, null, persondata3);
            break;

        case 1: // '\001'
            final Context context = getContext();
            final String circleId = cursor.getString(1);
            String s10 = cursor.getString(3);
            int j = cursor.getInt(2);
            final CircleData circle = new CircleData(circleId, j, s10, cursor.getInt(4));
            if(AccountsUtil.isRestrictedCircleForAccount(mAccount, j) && !EsAccountsData.hasSeenMinorPublicExtendedDialog(context, mAccount))
            {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle(cursor.getString(3));
                builder.setMessage(R.string.dialog_public_or_extended_circle_for_minor);
                int k = R.string.ok;
                android.content.DialogInterface.OnClickListener onclicklistener = new android.content.DialogInterface.OnClickListener() {

                    public final void onClick(DialogInterface dialoginterface, int i1)
                    {
                        mListener.onCircleSelected(circleId, circle);
                        EsAccountsData.saveMinorPublicExtendedDialogSeenPreference(context, mAccount, true);
                    }
                };
                
                builder.setPositiveButton(k, onclicklistener);
                int l = R.string.cancel;
                android.content.DialogInterface.OnClickListener onclicklistener1 = new android.content.DialogInterface.OnClickListener() {

                    public final void onClick(DialogInterface dialoginterface, int i1)
                    {
                    }

                };
                builder.setNegativeButton(l, onclicklistener1);
                builder.show();
            } else
            {
                mListener.onCircleSelected(circleId, circle);
            }
            break;

        case 4: // '\004'
            String s4 = cursor.getString(1);
            String s5 = cursor.getString(2);
            SearchListAdapterListener searchlistadapterlistener = mListener;
            String s6 = cursor.getString(3);
            String s7 = cursor.getString(4);
            boolean flag = mIncludePhoneNumberContacts;
            String s8 = null;
            if(flag)
            {
                String s9 = cursor.getString(10);
                boolean flag1 = TextUtils.isEmpty(s9);
                s8 = null;
                if(!flag1)
                {
                    StringBuilder stringbuilder = new StringBuilder("p:");
                    s8 = stringbuilder.append(s9.trim()).toString();
                }
            }
            if(s8 == null)
                s8 = cursor.getString(8);
            if(TextUtils.isEmpty(s8))
            {
                s8 = cursor.getString(9);
                if(TextUtils.isEmpty(s8))
                    s8 = null;
            }
            PersonData persondata2 = new PersonData(s6, s7, s8);
            searchlistadapterlistener.onPersonSelected(s4, s5, persondata2);
            break;

        case 2: // '\002'
            if(!mAddToCirclesActionEnabled)
                if(mShowPersonNameDialog)
                {
                    showPersonNameDialog("add_email_dialog");
                } else
                {
                    String s2 = getWellFormedEmailAddress();
                    if(!TextUtils.isEmpty(s2))
                    {
                        String s3 = (new StringBuilder("e:")).append(s2).toString();
                        PersonData persondata1 = new PersonData(null, null, s2);
                        mListener.onPersonSelected(s3, null, persondata1);
                    }
                }
            break;

        case 3: // '\003'
            if(!mAddToCirclesActionEnabled)
                if(mShowPersonNameDialog)
                {
                    showPersonNameDialog("add_sms_dialog");
                } else
                {
                    String s = getWellFormedSmsAddress();
                    if(!TextUtils.isEmpty(s))
                    {
                        String s1 = (new StringBuilder("p:")).append(s).toString();
                        PersonData persondata = new PersonData(null, null, s1);
                        mListener.onPersonSelected(s1, null, persondata);
                    }
                }
            break;
        }
    }
    
    public final void onLoadFinished(Loader loader, Object obj) {
        boolean flag;
        Cursor cursor;
        int i;
        flag = true;
        cursor = (Cursor)obj;
        i = loader.getId();
        if(i != mCirclesLoaderId) {
        	if(i == mGaiaIdLoaderId)
            {
                if(mGaiaIdCircleCursor != null && mGaiaIdCircleCursor != cursor)
                    mGaiaIdCircleCursor.close();
                mGaiaIdCircleCursor = cursor;
                mResults.onStartGaiaIdsAndCircles();
                if(cursor != null && cursor.moveToFirst())
                    do
                        mResults.addGaiaIdAndCircles(cursor.getString(0), cursor.getString(1));
                    while(cursor.moveToNext());
                mResults.onFinishGaiaIdsAndCircles();
                changeCursorForPeoplePartition();
            } else
            if(i == mContactsLoaderId)
            {
                boolean flag4;
                if(cursor == null)
                    flag4 = flag;
                else
                    flag4 = false;
                mContactsError = flag4;
                mContactsLoaded = flag;
                if(mContactsCursor != null && mContactsCursor != cursor)
                    mContactsCursor.close();
                mContactsCursor = cursor;
                mResults.onStartContacts();
                String as[];
                EsMatrixCursor esmatrixcursor;
                String as1[];
                EsMatrixCursor esmatrixcursor1;
                String s1;
                Object aobj[];
                String s2;
                Object aobj1[];
                String s3;
                String s4;
                if(cursor != null && cursor.moveToFirst())
                    do
                    {
                        if(mIncludePhoneNumberContacts)
                            s3 = cursor.getString(4);
                        else
                            s3 = null;
                        if(mIncludePhoneNumberContacts)
                            s4 = cursor.getString(5);
                        else
                            s4 = null;
                        mResults.addContact(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), s3, s4);
                    } while(cursor.moveToNext());
                //mResults;
                PeopleSearchResults.onFinishContacts();
                changeCursorForPeoplePartition();
                as = new String[2];
                as[0] = "_id";
                as[1] = "address";
                esmatrixcursor = new EsMatrixCursor(as);
                if(cursor != null && cursor.getCount() == 0)
                {
                    s2 = getWellFormedEmailAddress();
                    if(!TextUtils.isEmpty(s2))
                    {
                        aobj1 = new Object[2];
                        aobj1[0] = Integer.valueOf(0);
                        aobj1[1] = s2;
                        esmatrixcursor.addRow(aobj1);
                    }
                }
                changeCursor(2, esmatrixcursor);
                if(mIncludePhoneNumberContacts)
                {
                    as1 = new String[2];
                    as1[0] = "_id";
                    as1[1] = "address";
                    esmatrixcursor1 = new EsMatrixCursor(as1);
                    if(cursor != null && cursor.getCount() == 0)
                    {
                        s1 = getWellFormedSmsAddress();
                        if(!TextUtils.isEmpty(s1))
                        {
                            aobj = new Object[2];
                            aobj[0] = Integer.valueOf(0);
                            aobj[1] = s1;
                            esmatrixcursor1.addRow(aobj);
                        }
                    }
                    changeCursor(3, esmatrixcursor1);
                }
            } else
            if(i == mPeopleLoaderId)
            {
                boolean flag1 = false;
                if(cursor == null)
                    flag1 = flag;
                mLocalProfileError = flag1;
                mLocalProfilesLoaded = flag;
                if(mLocalProfilesCursor != null && mLocalProfilesCursor != cursor)
                    mLocalProfilesCursor.close();
                mLocalProfilesCursor = cursor;
                mResults.onStartLocalProfiles();
                if(cursor != null && cursor.moveToFirst())
                    do
                        mResults.addLocalProfile(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(7), cursor.getString(4), cursor.getString(5), cursor.getString(6), null, null);
                    while(cursor.moveToNext());
                mResults.onFinishLocalProfiles();
                changeCursorForPeoplePartition();
            } else
            if(i == mProfilesLoaderId && cursor != PublicProfileSearchLoader.ABORTED)
            {
                mHandler.removeMessages(1);
                if(mPublicProfilesCursor != null && mPublicProfilesCursor != cursor)
                    mPublicProfilesCursor.close();
                mPublicProfilesCursor = cursor;
                boolean flag2;
                if(cursor == null || !cursor.moveToFirst())
                    flag2 = flag;
                else
                    flag2 = false;
                mPublicProfilesError = flag2;
                if(mPublicProfilesError)
                    mPublicProfilesLoading = false;
                else
                if(TextUtils.equals(cursor.getString(0), mResults.getToken()))
                {
                    mPublicProfilesLoading = false;
                    String s = cursor.getString(1);
                    mResults.setToken(s);
                    PeopleSearchResults peoplesearchresults = mResults;
                    boolean flag3;
                    if(!TextUtils.isEmpty(s))
                        flag3 = flag;
                    else
                        flag3 = false;
                    peoplesearchresults.setHasMoreResults(flag3);
                    for(; cursor.moveToNext(); mResults.addPublicProfile(cursor.getString(2), cursor.getString(1), cursor.getString(3), cursor.getInt(5), cursor.getString(4), cursor.getString(6)));
                    if(mResults.getPublicProfileCount() != 0)
                        flag = false;
                    mPublicProfilesNotFound = flag;
                    changeCursorForPeoplePartition();
                }
            }
 
        } else { 
        	boolean flag5;
            if(cursor == null)
                flag5 = flag;
            else
                flag5 = false;
            mCirclesError = flag5;
            mCirclesLoaded = flag;
            changeCursor(1, cursor);
            updatePublicProfileSearchStatus();
            if(mListener != null)
                mListener.onSearchListAdapterStateChange(this);
            mActiveLoaderCount = -1 + mActiveLoaderCount;
            if(mActiveLoaderCount <= 0)
                releaseLatch();
            return;
        }
    }
    
    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        bundle.putString("search_list_adapter.query", mQuery);
        if(mResults.isParcelable())
            bundle.putParcelable("search_list_adapter.results", mResults);
    }

    public final void onStart()
    {
        mCircleNameResolver.initLoader();
        mLoaderManager.initLoader(mGaiaIdLoaderId, null, this);
        Bundle bundle = new Bundle();
        bundle.putString("query", mQuery);
        if(mCircleUsageType != -1)
            mLoaderManager.initLoader(mCirclesLoaderId, bundle, this);
        mLoaderManager.initLoader(mPeopleLoaderId, bundle, this);
        if(!mFilterNullGaiaIds)
            mLoaderManager.initLoader(mContactsLoaderId, bundle, this);
        if(mPublicProfileSearchEnabled)
            mLoaderManager.initLoader(mProfilesLoaderId, bundle, this);
        updatePublicProfileSearchStatus();
        AddEmailDialogListener addemaildialoglistener = (AddEmailDialogListener)mFragmentManager.findFragmentByTag("add_person_dialog_listener");
        if(addemaildialoglistener != null)
            addemaildialoglistener.setAdapter(this);
    }

    public final void onStop()
    {
        mHandler.removeMessages(0);
    }

    public final void setAddToCirclesActionEnabled(boolean flag)
    {
        mAddToCirclesActionEnabled = flag;
    }

    public final void setCircleUsageType(int i)
    {
        mCircleUsageType = i;
    }

    public final void setFilterNullGaiaIds(boolean flag)
    {
        mFilterNullGaiaIds = flag;
    }

    public final void setIncludePeopleInCircles(boolean flag)
    {
        mIncludePeopleInCircles = flag;
        mResults.setIncludePeopleInCircles(mIncludePeopleInCircles);
    }

    public final void setIncludePhoneNumberContacts(boolean flag)
    {
        mIncludePhoneNumberContacts = flag;
    }

    public final void setIncludePlusPages(boolean flag)
    {
        mIncludePlusPages = flag;
    }

    public final void setListener(SearchListAdapterListener searchlistadapterlistener)
    {
        mListener = searchlistadapterlistener;
    }

    public final void setMention(String s)
    {
        mActivityId = s;
        mIsMentionsAdapter = true;
    }

    public final void setPublicProfileSearchEnabled(boolean flag)
    {
        mPublicProfileSearchEnabled = flag;
    }

    public final void setQueryString(String s) {
        if(TextUtils.equals(mQuery, s)) {
        	releaseLatch();
        	return;
        }
        
        mResults.setQueryString(s);
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mQuery = s;
        mActiveLoaderCount = 0;
        if(TextUtils.isEmpty(s))
        {
            mLoaderManager.destroyLoader(mCirclesLoaderId);
            mLoaderManager.destroyLoader(mPeopleLoaderId);
            mLoaderManager.destroyLoader(mContactsLoaderId);
            mLoaderManager.destroyLoader(mProfilesLoaderId);
            clearPartitions();
            releaseLatch();
            if(mListener != null)
                mListener.onSearchListAdapterStateChange(this);
        } else
        {
            Bundle bundle = new Bundle();
            bundle.putString("query", mQuery);
            if(mCircleUsageType != -1)
            {
                mActiveLoaderCount = 1 + mActiveLoaderCount;
                mLoaderManager.restartLoader(mCirclesLoaderId, bundle, this);
            }
            mActiveLoaderCount = 1 + mActiveLoaderCount;
            mLoaderManager.restartLoader(mPeopleLoaderId, bundle, this);
            if(!mFilterNullGaiaIds)
            {
                mActiveLoaderCount = 1 + mActiveLoaderCount;
                mLoaderManager.restartLoader(mContactsLoaderId, bundle, this);
            }
            if(mPublicProfileSearchEnabled)
            {
                mPublicProfilesError = false;
                mPublicProfilesNotFound = false;
                mPublicProfilesLoading = false;
                mHandler.sendEmptyMessageDelayed(1, 300L);
                mLoaderManager.destroyLoader(mProfilesLoaderId);
                mLoaderManager.initLoader(mProfilesLoaderId, bundle, this);
                updatePublicProfileSearchStatus();
            }
        }
    }

    public final void setShowPersonNameDialog(boolean flag)
    {
        mShowPersonNameDialog = false;
    }

    public final void setShowProgressWhenEmpty(boolean flag)
    {
        mShowProgressWhenEmpty = false;
    }

    protected final void showEmptyPeopleSearchResults()
    {
        mHandler.removeMessages(0);
        Cursor cursor = mResults.getCursor();
        if(cursor.getCount() == 0)
            changeCursor(4, cursor);
    }

    protected final void showPersonNameDialog(String s) {
        AddEmailDialogListener addemaildialoglistener = (AddEmailDialogListener)mFragmentManager.findFragmentByTag("add_person_dialog_listener");
        if(addemaildialoglistener == null)
        {
            addemaildialoglistener = new AddEmailDialogListener();
            mFragmentManager.beginTransaction().add(addemaildialoglistener, "add_person_dialog_listener").commit();
        }
        addemaildialoglistener.setAdapter(this);
        Context context = getContext();
        EditFragmentDialog editfragmentdialog = EditFragmentDialog.newInstance(context.getString(R.string.add_email_dialog_title), null, context.getString(R.string.add_email_dialog_hint), context.getString(0x104000a), context.getString(0x1040000), false);
        editfragmentdialog.setTargetFragment(addemaildialoglistener, 0);
        editfragmentdialog.show(mFragmentManager, s);
    }

    public static interface SearchListAdapterListener {

        void onAddPersonToCirclesAction(String s, String s1, boolean flag);

        void onChangeCirclesAction(String s, String s1);

        void onCircleSelected(String s, CircleData circledata);

        void onDismissSuggestionAction(String s, String s1);

        void onPersonSelected(String s, String s1, PersonData persondata);

        void onSearchListAdapterStateChange(PeopleSearchAdapter peoplesearchadapter);

        void onUnblockPersonAction(String s, boolean flag);
    }
    
	public static class AddEmailDialogListener extends Fragment implements
			AlertFragmentDialog.AlertDialogListener {

		private PeopleSearchAdapter mAdapter;

		public AddEmailDialogListener() {
		}

		public final void onDialogCanceled(String s) {
		}

		public final void onDialogListClick(int i, Bundle bundle) {
		}

		public final void onDialogNegativeClick(String s) {
		}

		public final void onDialogPositiveClick(Bundle bundle, String s) {
			if (mAdapter != null)
				mAdapter.onDialogPositiveClick(bundle, s);
		}

		public final void setAdapter(PeopleSearchAdapter peoplesearchadapter) {
			mAdapter = peoplesearchadapter;
		}

	}

	public static class SearchResultsFragment extends Fragment {

		private PeopleSearchResults mResults;

		public SearchResultsFragment() {
			setRetainInstance(true);
		}

		public final PeopleSearchResults getPeopleSearchResults() {
			return mResults;
		}

		public final void setPeopleSearchResults(
				PeopleSearchResults peoplesearchresults) {
			mResults = peoplesearchresults;
		}

	}
}
