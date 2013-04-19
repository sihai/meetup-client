/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAudienceData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.realtimechat.Client;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.ParticipantUtils;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceListener;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.PeopleSearchAdapter.SearchListAdapterListener;
import com.galaxy.meetup.client.android.ui.view.AudienceView;
import com.galaxy.meetup.client.android.ui.view.SuggestedPeopleListItemView;
import com.galaxy.meetup.client.android.ui.view.TypeableAudienceView;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class AudienceFragment extends EsFragment implements LoaderCallbacks,
		OnClickListener, OnItemClickListener, SearchListAdapterListener {

	private List displayedSuggestedParticipants;
    private EsAccount mAccount;
    private Runnable mAudienceChangedCallback;
    protected AudienceData mAudienceResult;
    protected AudienceView mAudienceView;
    private boolean mCacheSuggestionsResponse;
    private int mCircleUsageType;
    private boolean mFilterNullGaiaIds;
    private GridView mGridView;
    private boolean mIncludePhoneOnlyContacts;
    private boolean mIncludePlusPages;
    private TextView mListHeader;
    private View mListParent;
    private boolean mPublicProfileSearchEnabled;
    private RTCListener mRealTimeChatListener;
    private Integer mRequestId;
    protected PeopleSearchListAdapter mSearchListAdapter;
    private boolean mShowSuggestedPeople;
    private List mSuggestedPeople;
    private SuggestedPeopleAdpater mSuggestedPeopleAdapter;
    private ScrollView mSuggestedPeopleScrollView;
    private int mSuggestedPeopleSize;
    
    
    public AudienceFragment()
    {
        mIncludePhoneOnlyContacts = true;
        mRealTimeChatListener = new RTCListener();
        mRequestId = null;
        mSuggestedPeopleSize = 0;
    }

    private void cacheSuggestedResponse(Client.SuggestionsResponse suggestionsresponse)
    {
        if(mCacheSuggestionsResponse)
        {
            EsAudienceData.processSuggestionsResponse(getActivity(), mAccount, suggestionsresponse);
            mCacheSuggestionsResponse = false;
        }
    }

    private boolean isInAudience(String s) {
    	String s1;
    	PersonData apersondata[] = mAudienceView.getAudience().getUsers();
        int length = apersondata.length;
        for(int j = 0; j < length; j++) {
        	s1 = ParticipantUtils.getParticipantIdFromPerson(apersondata[j]);
            if(null != s1 && s1.equals(s)) {
            	return true;
            }
        }
        
        return false;
    }

    private void loadSuggestedPeople(Client.SuggestionsResponse suggestionsresponse)
    {
        for(Iterator iterator = suggestionsresponse.getSuggestionList().iterator(); iterator.hasNext();)
        {
            Iterator iterator1 = ((Client.Suggestion)iterator.next()).getSuggestedUserList().iterator();
            while(iterator1.hasNext()) 
            {
                Data.Participant participant = (Data.Participant)iterator1.next();
                mSuggestedPeople.add(participant);
            }
        }

        updateSuggestedPeopleDisplay();
    }

    private void updateSuggestedPeopleDisplay()
    {
        Iterator iterator = mSuggestedPeople.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Data.Participant participant1 = (Data.Participant)iterator.next();
            Iterator iterator2 = displayedSuggestedParticipants.iterator();
            boolean flag1;
            do
            {
                boolean flag = iterator2.hasNext();
                flag1 = false;
                if(!flag)
                    break;
                if(!((Data.Participant)iterator2.next()).getParticipantId().equals(participant1.getParticipantId()))
                    continue;
                flag1 = true;
                break;
            } while(true);
            if(!flag1)
            {
                displayedSuggestedParticipants.add(participant1);
                if(mListHeader != null && mListHeader.getVisibility() != 0)
                    mListHeader.setVisibility(0);
            }
        } while(true);
        int i = 0;
        MatrixCursor matrixcursor = new MatrixCursor(SuggestedPeopleQuery.columnNames);
        Iterator iterator1 = displayedSuggestedParticipants.iterator();
        while(iterator1.hasNext()) 
        {
            Data.Participant participant = (Data.Participant)iterator1.next();
            Object aobj[] = new Object[4];
            int j = i + 1;
            aobj[0] = Integer.valueOf(i);
            aobj[1] = participant.getParticipantId();
            aobj[2] = participant.getFullName();
            int k;
            if(isInAudience(participant.getParticipantId()))
                k = 1;
            else
                k = 0;
            aobj[3] = Integer.valueOf(k);
            matrixcursor.addRow(aobj);
            i = j;
        }
        mSuggestedPeopleAdapter.swapCursor(matrixcursor);
        if(mSuggestedPeopleSize != mSuggestedPeopleAdapter.getCount() && mSuggestedPeopleAdapter.getCount() == mGridView.getChildCount())
        {
            mSuggestedPeopleScrollView.scrollTo(0, 0);
            mSuggestedPeopleSize = mSuggestedPeopleAdapter.getCount();
        }
    }

    public final AudienceData getAudience()
    {
        return mAudienceView.getAudience();
    }

    protected final void getSuggestedPeople()
    {
        AudienceData audiencedata = mAudienceView.getAudience();
        boolean flag = isAudienceEmpty();
        mCacheSuggestionsResponse = flag;
        mRequestId = Integer.valueOf(RealTimeChatService.requestSuggestedParticipants(getActivity(), mAccount, audiencedata, Client.SuggestionsRequest.SuggestionsType.HANGOUT));
        if(flag)
            getLoaderManager().initLoader(1, null, this);
    }

    public final boolean isAudienceEmpty()
    {
        boolean flag = true;
        AudienceData audiencedata = mAudienceView.getAudience();
        if(null == audiencedata) {
        	return true;
        }
        
        CircleData acircledata[];
        if(audiencedata.getUserCount() > 0)
        {
            return false;
        }
        acircledata = audiencedata.getCircles();
        int length = acircledata.length;
        for(int j = 0; j < length; j++) {
        	CircleData circledata = acircledata[j];
            if(circledata.getSize() > 0 || circledata.getType() == 9 || circledata.getType() == 7) {
            	return false;
            }
        }
        
        return true;
        
    }

    public final boolean isEmpty()
    {
        return false;
    }

    public final void onActivityCreated(Bundle bundle)
    {
        super.onActivityCreated(bundle);
        if(bundle == null)
        {
            AudienceData audiencedata = (AudienceData)getActivity().getIntent().getParcelableExtra("audience");
            if(audiencedata != null)
                mAudienceView.replaceAudience(audiencedata);
        }
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
        if(i == 1 && j == -1 && intent != null)
            mAudienceResult = (AudienceData)intent.getParcelableExtra("audience");
    }

    public final void onAddPersonToCirclesAction(String s, String s1, boolean flag)
    {
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mAccount = (EsAccount)getActivity().getIntent().getParcelableExtra("account");
    }

    protected final void onAudienceChanged()
    {
        if(mAudienceChangedCallback != null)
            mAudienceChangedCallback.run();
    }

    public final void onChangeCirclesAction(String s, String s1)
    {
    }

    public final void onCircleSelected(String s, CircleData circledata)
    {
        mAudienceView.addCircle(circledata);
        if(mAudienceView instanceof TypeableAudienceView)
            ((TypeableAudienceView)mAudienceView).clearText();
    }

    public void onClick(View view)
    {
        int i = view.getId();
        if(i == R.id.edit_audience || i == R.id.audience_view)
        {
            AudienceData audiencedata = mAudienceView.getAudience();
            startActivityForResult(Intents.getEditAudienceActivityIntent(getActivity(), mAccount, getString(R.string.realtimechat_edit_audience_activity_title), audiencedata, mCircleUsageType, mIncludePhoneOnlyContacts, mIncludePlusPages, mPublicProfileSearchEnabled, mFilterNullGaiaIds), 1);
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mSuggestedPeople = new LinkedList();
        displayedSuggestedParticipants = new LinkedList();
        if(bundle != null)
        {
            if(bundle.containsKey("request_id"))
            {
                mRequestId = Integer.valueOf(bundle.getInt("request_id"));
                mCacheSuggestionsResponse = bundle.getBoolean("cache_suggestions_response");
            } else
            {
                mRequestId = null;
                mCacheSuggestionsResponse = false;
            }
            mShowSuggestedPeople = bundle.getBoolean("show_suggested_people");
            mPublicProfileSearchEnabled = bundle.getBoolean("public_profile_search");
            mIncludePhoneOnlyContacts = bundle.getBoolean("phone_only_contacts");
            mIncludePlusPages = bundle.getBoolean("plus_pages");
        }
    }

    public Loader onCreateLoader(int i, Bundle bundle)
    {
        if(EsLog.isLoggable("Audience", 3))
            Log.d("Audience", (new StringBuilder("onCreateLoader ")).append(i).toString());
        EsCursorLoader escursorloader;
        if(i == 1)
        {
            android.net.Uri uri = EsProvider.appendAccountParameter(EsProvider.HANGOUT_SUGGESTIONS_URI, mAccount);
            escursorloader = new EsCursorLoader(getActivity(), uri, HangoutSuggestionsQuery.PROJECTION, null, null, "sequence ASC", null);
        } else
        {
            escursorloader = null;
        }
        return escursorloader;
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.audience_fragment, viewgroup, false);
        mGridView = (GridView)view.findViewById(0x102000a);
        mSuggestedPeopleScrollView = (ScrollView)view.findViewById(R.id.suggested_people_scroll_view);
        mListParent = view.findViewById(R.id.list_layout_parent);
        mListHeader = (TextView)view.findViewById(R.id.list_header);
        mGridView.setOnItemClickListener(this);
        mSuggestedPeopleAdapter = new SuggestedPeopleAdpater(getActivity(), null);
        mGridView.setAdapter(mSuggestedPeopleAdapter);
        return view;
    }

    public final void onDismissSuggestionAction(String s, String s1)
    {
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        mSuggestedPeopleAdapter.onItemClick(i);
    }

    public void onLoadFinished(Loader loader, Cursor cursor)
    {
        if(EsLog.isLoggable("Audience", 3))
            Log.d("Audience", (new StringBuilder("onLoadFinished ")).append(loader.getId()).toString());
        if(loader.getId() == 1 && cursor != null && cursor.moveToFirst())
        {
            do
            {
                Data.Participant participant = Data.Participant.newBuilder().setParticipantId(cursor.getString(1)).setFullName(cursor.getString(2)).setFirstName(cursor.getString(3)).build();
                mSuggestedPeople.add(participant);
            } while(cursor.moveToNext());
            updateSuggestedPeopleDisplay();
        }
    }

    public void onLoadFinished(Loader loader, Object obj)
    {
        onLoadFinished(loader, (Cursor)obj);
    }

    public void onLoaderReset(Loader loader)
    {
    }

    public void onPause()
    {
        super.onPause();
        if(mShowSuggestedPeople)
            RealTimeChatService.unregisterListener(mRealTimeChatListener);
    }

    public final void onPersonSelected(String s, String s1, PersonData persondata)
    {
        mAudienceView.addPerson(persondata);
        if(mAudienceView instanceof TypeableAudienceView)
            ((TypeableAudienceView)mAudienceView).clearText();
    }

    public void onResume()
    {
        super.onResume();
        if(mAudienceResult != null)
        {
            AudienceData audiencedata = mAudienceResult;
            mAudienceView.replaceAudience(audiencedata);
            mAudienceResult = null;
        }
        if(mShowSuggestedPeople)
            RealTimeChatService.registerListener(mRealTimeChatListener);
        if(mRequestId != null && !RealTimeChatService.isRequestPending(mRequestId.intValue()))
        {
            RealTimeChatServiceResult realtimechatserviceresult = RealTimeChatService.removeResult(mRequestId.intValue());
            if(realtimechatserviceresult != null && realtimechatserviceresult.getErrorCode() == 1 && realtimechatserviceresult.getCommand() != null && realtimechatserviceresult.getCommand().hasSuggestionsResponse())
            {
                loadSuggestedPeople(realtimechatserviceresult.getCommand().getSuggestionsResponse());
                cacheSuggestedResponse(realtimechatserviceresult.getCommand().getSuggestionsResponse());
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mSearchListAdapter != null)
            mSearchListAdapter.onSaveInstanceState(bundle);
        if(mRequestId != null)
        {
            bundle.putInt("request_id", mRequestId.intValue());
            bundle.putBoolean("cache_suggestions_response", mCacheSuggestionsResponse);
        }
        bundle.putBoolean("show_suggested_people", mShowSuggestedPeople);
        bundle.putBoolean("public_profile_search", mPublicProfileSearchEnabled);
        bundle.putBoolean("phone_only_contacts", mIncludePhoneOnlyContacts);
        bundle.putBoolean("plus_pages", mIncludePlusPages);
    }

    public final void onSearchListAdapterStateChange(PeopleSearchAdapter peoplesearchadapter)
    {
        if(mListParent != null)
            if(peoplesearchadapter.isEmpty())
                mListParent.setVisibility(0);
            else
                mListParent.setVisibility(8);
    }

    public final void onStart()
    {
        super.onStart();
        if(mSearchListAdapter != null)
            mSearchListAdapter.onStart();
    }

    public final void onStop()
    {
        super.onStart();
        if(mSearchListAdapter != null)
            mSearchListAdapter.onStop();
    }

    public final void onUnblockPersonAction(String s, boolean flag)
    {
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        mAudienceView = (AudienceView)view.findViewById(R.id.audience_view);
        mSearchListAdapter = new PeopleSearchListAdapter(new ContextThemeWrapper(getActivity(), R.style.CircleBrowserTheme), getFragmentManager(), getLoaderManager(), mAccount);
        mSearchListAdapter.setIncludePhoneNumberContacts(mIncludePhoneOnlyContacts);
        mSearchListAdapter.setIncludePlusPages(mIncludePlusPages);
        mSearchListAdapter.setPublicProfileSearchEnabled(mPublicProfileSearchEnabled);
        mSearchListAdapter.setCircleUsageType(mCircleUsageType);
        mSearchListAdapter.setFilterNullGaiaIds(mFilterNullGaiaIds);
        mSearchListAdapter.setListener(this);
        mSearchListAdapter.onCreate(bundle);
        if(mAudienceView instanceof TypeableAudienceView)
        {
            TypeableAudienceView typeableaudienceview = (TypeableAudienceView)mAudienceView;
            typeableaudienceview.setEmptyAudienceHint(R.string.realtimechat_new_conversation_hint_text);
            typeableaudienceview.setAutoCompleteAdapter(mSearchListAdapter);
        }
        mAudienceView.setAccount(mAccount);
        mAudienceView.initLoaders(getLoaderManager());
        setupAudienceClickListener();
        mAudienceView.setAudienceChangedCallback(new Runnable() {

            public final void run()
            {
                if(mShowSuggestedPeople)
                {
                    getSuggestedPeople();
                    if(mSuggestedPeopleAdapter.isEmpty() && mListHeader != null)
                        mListHeader.setVisibility(8);
                    updateSuggestedPeopleDisplay();
                }
                onAudienceChanged();
            }
        });
        if(mShowSuggestedPeople)
        {
            if(mSuggestedPeopleAdapter.isEmpty() && mListHeader != null)
                mListHeader.setVisibility(8);
            getSuggestedPeople();
        }
    }

    public final void setAudienceChangedCallback(Runnable runnable)
    {
        mAudienceChangedCallback = runnable;
    }

    public final void setCirclesUsageType(int i)
    {
        mCircleUsageType = i;
    }

    public final void setFilterNullGaiaIds(boolean flag)
    {
        mFilterNullGaiaIds = true;
    }

    public final void setIncludePhoneOnlyContacts(boolean flag)
    {
        mIncludePhoneOnlyContacts = flag;
    }

    public final void setIncludePlusPages(boolean flag)
    {
        mIncludePlusPages = flag;
    }

    public final void setPublicProfileSearchEnabled(boolean flag)
    {
        mPublicProfileSearchEnabled = true;
    }

    public final void setShowSuggestedPeople(boolean flag)
    {
        mShowSuggestedPeople = true;
    }

    protected void setupAudienceClickListener()
    {
        getView().findViewById(R.id.edit_audience).setOnClickListener(this);
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private static interface HangoutSuggestionsQuery
    {

        public static final String PROJECTION[] = {
            "_id", "participant_id", "full_name", "first_name"
        };

    }

    private final class RTCListener extends RealTimeChatServiceListener
    {

        public final void onResponseReceived$1587694a(int i, RealTimeChatServiceResult realtimechatserviceresult)
        {
            if(mRequestId != null && i == mRequestId.intValue() && realtimechatserviceresult.getErrorCode() == 1 && realtimechatserviceresult.getCommand().hasSuggestionsResponse())
            {
                Client.SuggestionsResponse suggestionsresponse = realtimechatserviceresult.getCommand().getSuggestionsResponse();
                loadSuggestedPeople(suggestionsresponse);
                cacheSuggestedResponse(suggestionsresponse);
            }
        }

        public final void onResponseTimeout(int i)
        {
            mRequestId.intValue();
        }
    }

    private final class SuggestedPeopleAdpater extends EsCursorAdapter {

    	final LayoutInflater mLayoutInflater;

        public SuggestedPeopleAdpater(Context context, Cursor cursor)
        {
            super(context, null);
            mLayoutInflater = (LayoutInflater)context.getSystemService("layout_inflater");
        }
        
        public final void bindView(View view, Context context, Cursor cursor)
        {
            boolean flag = true;
            SuggestedPeopleListItemView suggestedpeoplelistitemview = (SuggestedPeopleListItemView)view;
            suggestedpeoplelistitemview.setPersonId(cursor.getString(1));
            suggestedpeoplelistitemview.setParticipantName(cursor.getString(2).replaceAll(" .*", ""));
            if(cursor.getInt(3) <= 0)
                flag = false;
            suggestedpeoplelistitemview.setChecked(flag);
        }

        public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
        {
            return mLayoutInflater.inflate(R.layout.suggested_people_list_item_view, null);
        }

        public final void onItemClick(int i)
        {
            String s = null;
            Cursor cursor = getCursor();
            cursor.moveToPosition(i);
            String s1 = cursor.getString(2);
            String s2 = cursor.getString(1);
            String s3;
            PersonData persondata;
            if(s2.startsWith("g:"))
                s3 = EsPeopleData.extractGaiaId(s2);
            else
            if(s2.startsWith("e:"))
            {
                s = s2.substring(2);
                s3 = null;
            } else
            if(s2.startsWith("p:"))
            {
                s = s2;
                s3 = null;
            } else
            {
                s = null;
                s3 = null;
            }
            persondata = new PersonData(s3, s1, s);
            if(isInAudience(ParticipantUtils.getParticipantIdFromPerson(persondata)))
                mAudienceView.removePerson(persondata);
            else
                mAudienceView.addPerson(persondata);
        }
    }

    private static interface SuggestedPeopleQuery {

        public static final String columnNames[] = {
            "_id", "participant_id", "full_name", "in_audience"
        };

    }
}
