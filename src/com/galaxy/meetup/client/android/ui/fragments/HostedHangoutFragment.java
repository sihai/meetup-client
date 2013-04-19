/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ShakeDetector;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAudienceData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.hangout.GCommApp;
import com.galaxy.meetup.client.android.realtimechat.Client;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.ParticipantUtils;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceListener;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceResult;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.SuggestedPeopleListItemView;
import com.galaxy.meetup.client.android.ui.view.TypeableAudienceView;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.HelpUrl;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class HostedHangoutFragment extends HostedEsFragment implements
		LoaderCallbacks, OnClickListener, OnItemClickListener,
		PeopleSearchAdapter.SearchListAdapterListener {

	private static final ActiveHangoutMode ACTIVE_HANGOUT_MODE_DEFAULT;
    private List displayedSuggestedParticipants;
    private ActiveHangoutMode mActiveViewMode;
    private View mAudienceOverlay;
    protected AudienceData mAudienceResult;
    protected TypeableAudienceView mAudienceView;
    private boolean mCacheSuggestionsResponse;
    private int mCircleUsageType;
    private boolean mFilterNullGaiaIds;
    private GridView mGridView;
    private boolean mIncludePhoneOnlyContacts;
    private boolean mIncludePlusPages;
    private TextView mListHeader;
    private View mListParent;
    private boolean mPreviouslyAudienceEmpty;
    private boolean mPreviouslyOvercapacity;
    private boolean mPublicProfileSearchEnabled;
    private RTCListener mRealTimeChatListener;
    private Integer mRequestId;
    private Button mResumeHangoutButton;
    private boolean mRingBeforeDisable;
    protected PeopleSearchListAdapter mSearchListAdapter;
    private boolean mShakeDetectorWasRunning;
    private boolean mShowSuggestedPeople;
    private Button mStartHangoutButton;
    private List mSuggestedPeople;
    private SuggestedPeopleAdpater mSuggestedPeopleAdapter;
    private ScrollView mSuggestedPeopleScrollView;
    private int mSuggestedPeopleSize;
    private ImageButton mToggleHangoutRingButton;

    static 
    {
        ACTIVE_HANGOUT_MODE_DEFAULT = ActiveHangoutMode.MODE_DISABLE;
    }
	
    
    public HostedHangoutFragment()
    {
        mIncludePhoneOnlyContacts = true;
        mRealTimeChatListener = new RTCListener();
        mRequestId = null;
        mActiveViewMode = ACTIVE_HANGOUT_MODE_DEFAULT;
        mSuggestedPeopleSize = 0;
        mPreviouslyAudienceEmpty = true;
        mPreviouslyOvercapacity = false;
    }

    private void cacheSuggestedResponse(Client.SuggestionsResponse suggestionsresponse)
    {
        if(mCacheSuggestionsResponse)
        {
            EsAudienceData.processSuggestionsResponse(getActivity(), mAccount, suggestionsresponse);
            mCacheSuggestionsResponse = false;
        }
    }

    private void disableHangoutRing(boolean flag, boolean flag1)
    {
        mRingBeforeDisable = false;
        mToggleHangoutRingButton.setImageResource(R.drawable.icn_ring_off);
        mToggleHangoutRingButton.setContentDescription(getString(R.string.hangout_ring_off_content_description));
        if(flag1)
        {
            int i;
            if(flag)
                i = R.string.ring_off_overcapacity_hangout_toast;
            else
                i = R.string.ring_off_hangout_toast;
            toast(i);
        }
    }

    private void enableHangoutRing(boolean flag)
    {
        mRingBeforeDisable = true;
        mToggleHangoutRingButton.setImageResource(R.drawable.icn_ring_on);
        mToggleHangoutRingButton.setContentDescription(getString(R.string.hangout_ring_on_content_description));
        if(flag)
            toast(R.string.ring_on_hangout_toast);
    }

    private boolean isInAudience(String s) {
        PersonData apersondata[];
        apersondata = mAudienceView.getAudience().getUsers();
        int length = apersondata.length;
        String s1;
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

    private void toast(int i)
    {
        String s = getString(i);
        Toast.makeText(getActivity(), s, 0).show();
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
    
    public final boolean audienceSizeIsGreaterThan(int i) {
        boolean flag = false;
        AudienceData audiencedata = mAudienceView.getAudience();
        if(null == audiencedata) {
        	return false;
        }
        int j = audiencedata.getUserCount();
        int k = 0;
        if(j > 0)
            k = 0 + audiencedata.getUserCount();
        CircleData acircledata[] = audiencedata.getCircles();
        int l = acircledata.length;
        for(int i1 = 0; i1 < l; i1++)
        {
            CircleData circledata = acircledata[i1];
            if(circledata.getType() == 9 || circledata.getType() == 7 || circledata.getType() == 8)
            {
                return true;
            }
            if(circledata.getSize() > 0)
                k += circledata.getSize();
        }

        flag = false;
        if(k > 10)
            flag = true;
        
        return flag;
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

    public final OzViews getViewForLogging()
    {
        return OzViews.HANGOUT;
    }

    public final boolean isAudienceEmpty() {
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

    public final void onChangeCirclesAction(String s, String s1)
    {
    }

    public final void onCircleSelected(String s, CircleData circledata)
    {
        mAudienceView.addCircle(circledata);
        mAudienceView.clearText();
    }

    public void onClick(View view)
    {
        if(view.getId() == R.id.edit_audience)
        {
            AudienceData audiencedata = mAudienceView.getAudience();
            startActivityForResult(Intents.getEditAudienceActivityIntent(getActivity(), mAccount, getString(R.string.realtimechat_edit_audience_activity_title), audiencedata, mCircleUsageType, mIncludePhoneOnlyContacts, mIncludePlusPages, mPublicProfileSearchEnabled, mFilterNullGaiaIds), 1);
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mSuggestedPeople = new LinkedList();
        displayedSuggestedParticipants = new LinkedList();
        String s;
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
        mCircleUsageType = 10;
        mIncludePhoneOnlyContacts = false;
        mIncludePlusPages = false;
        mPublicProfileSearchEnabled = true;
        mShowSuggestedPeople = true;
        mFilterNullGaiaIds = true;
        s = Property.ACTIVE_HANGOUT_MODE.get();
        if("disable".equalsIgnoreCase(s))
            mActiveViewMode = ActiveHangoutMode.MODE_DISABLE;
        else
        if("hide".equalsIgnoreCase(s))
            mActiveViewMode = ActiveHangoutMode.MODE_HIDE;
        else
        if("none".equalsIgnoreCase(s))
            mActiveViewMode = ActiveHangoutMode.MODE_NONE;
        else
            mActiveViewMode = ACTIVE_HANGOUT_MODE_DEFAULT;
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        if(EsLog.isLoggable("HangoutFrag", 3))
            Log.d("HangoutFrag", (new StringBuilder("onCreateLoader ")).append(i).toString());
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

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.hosted_hangout_fragment, viewgroup, false);
        mGridView = (GridView)view.findViewById(0x102000a);
        mSuggestedPeopleScrollView = (ScrollView)view.findViewById(R.id.suggested_people_scroll_view);
        mListParent = view.findViewById(R.id.list_layout_parent);
        mListHeader = (TextView)view.findViewById(R.id.list_header);
        mGridView.setOnItemClickListener(this);
        mSuggestedPeopleAdapter = new SuggestedPeopleAdpater(getActivity(), null);
        mGridView.setAdapter(mSuggestedPeopleAdapter);
        mToggleHangoutRingButton = (ImageButton)view.findViewById(R.id.toggle_hangout_ring_button);
        disableHangoutRing(false, false);
        mToggleHangoutRingButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                HostedHangoutFragment.access$500(HostedHangoutFragment.this);
            }

        });
        mStartHangoutButton = (Button)view.findViewById(R.id.start_hangout_button);
        mStartHangoutButton.setEnabled(false);
        mStartHangoutButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                startActivity(Intents.getNewHangoutActivityIntent(getActivity(), mAccount, mRingBeforeDisable, getAudience()));
            }

        });
        
        mResumeHangoutButton = (Button)view.findViewById(R.id.resume_hangout_button);
        mResumeHangoutButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                GCommApp gcommapp = GCommApp.getInstance(getActivity());
                if(gcommapp.isInAHangout())
                {
                    Intent intent = gcommapp.getGCommService().getNotificationIntent();
                    if(intent != null)
                        startActivity(intent);
                }
            }
        });
        
        mAudienceOverlay = view.findViewById(R.id.audience_overlay);
        mAudienceOverlay.setOnTouchListener(null);
        mAudienceOverlay.setOnClickListener(null);
        if(android.os.Build.VERSION.SDK_INT >= 12)
            mAudienceOverlay.setOnGenericMotionListener(null);
        mAudienceOverlay.setOnKeyListener(null);
        mAudienceOverlay.setOnLongClickListener(null);
        return view;
    }

    public final void onDismissSuggestionAction(String s, String s1)
    {
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        mSuggestedPeopleAdapter.onItemClick(i);
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(EsLog.isLoggable("HangoutFrag", 3))
            Log.d("HangoutFrag", (new StringBuilder("onLoadFinished ")).append(loader.getId()).toString());
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

    public final void onLoaderReset(Loader loader)
    {
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == R.id.help)
        {
            String s = getResources().getString(R.string.url_param_help_hangouts);
            startExternalActivity(new Intent("android.intent.action.VIEW", HelpUrl.getHelpUrl(getActivity(), s)));
            flag = true;
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public final void onPause()
    {
        super.onPause();
        if(mShowSuggestedPeople)
            RealTimeChatService.unregisterListener(mRealTimeChatListener);
    }

    public final void onPersonSelected(String s, String s1, PersonData persondata)
    {
        mAudienceView.addPerson(persondata);
        mAudienceView.clearText();
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        hostactionbar.showTitle(R.string.home_screen_hangout_label);
    }

    public final void onResume() {
        super.onResume();
        mStartHangoutButton.setVisibility(0);
        mToggleHangoutRingButton.setVisibility(0);
        mResumeHangoutButton.setVisibility(8);
        mAudienceOverlay.setVisibility(8);
        mAudienceView.setVisibility(0);
        mListParent.setVisibility(0);
        mListHeader.setText(R.string.realtimechat_users_you_may_know);
        if(!GCommApp.getInstance(getActivity()).isInAHangout()) {
        	return; 
        } 
        
        mStartHangoutButton.setVisibility(8);
        mToggleHangoutRingButton.setVisibility(8);
        mResumeHangoutButton.setVisibility(0);
        if(ActiveHangoutMode.MODE_DISABLE == mActiveViewMode) {
        	mAudienceView.setVisibility(4);
            mListParent.setVisibility(4);
        } else if(ActiveHangoutMode.MODE_HIDE == mActiveViewMode) {
        	mAudienceOverlay.setVisibility(0);
        } else {
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
        
    }

    public final void onSaveInstanceState(Bundle bundle)
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
        ShakeDetector shakedetector = ShakeDetector.getInstance(getActivity());
        if(shakedetector != null)
            mShakeDetectorWasRunning = shakedetector.stop();
    }

    public final void onStop()
    {
        super.onStart();
        if(mSearchListAdapter != null)
            mSearchListAdapter.onStop();
        if(mShakeDetectorWasRunning)
        {
            ShakeDetector shakedetector = ShakeDetector.getInstance(getActivity());
            if(shakedetector != null)
                shakedetector.start();
        }
    }

    public final void onUnblockPersonAction(String s, boolean flag)
    {
    }

    public final void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        mAudienceView = (TypeableAudienceView)view.findViewById(R.id.audience_view);
        mAudienceView.setEmptyAudienceHint(R.string.realtimechat_new_conversation_hint_text);
        mSearchListAdapter = new PeopleSearchListAdapter(new ContextThemeWrapper(getActivity(), R.style.CircleBrowserTheme), getFragmentManager(), getLoaderManager(), mAccount);
        mSearchListAdapter.setIncludePhoneNumberContacts(mIncludePhoneOnlyContacts);
        mSearchListAdapter.setIncludePlusPages(mIncludePlusPages);
        mSearchListAdapter.setPublicProfileSearchEnabled(mPublicProfileSearchEnabled);
        mSearchListAdapter.setCircleUsageType(mCircleUsageType);
        mSearchListAdapter.setFilterNullGaiaIds(mFilterNullGaiaIds);
        mSearchListAdapter.setListener(this);
        mSearchListAdapter.onCreate(bundle);
        mAudienceView.setAutoCompleteAdapter(mSearchListAdapter);
        mAudienceView.setAccount(mAccount);
        mAudienceView.initLoaders(getLoaderManager());
        view.findViewById(R.id.edit_audience).setOnClickListener(this);
        mAudienceView.setAudienceChangedCallback(new Runnable() {

            public final void run()
            {
                boolean flag = true;
                if(mShowSuggestedPeople)
                {
                    getSuggestedPeople();
                    if(mSuggestedPeopleAdapter.isEmpty() && mListHeader != null)
                        mListHeader.setVisibility(8);
                    updateSuggestedPeopleDisplay();
                }
                boolean flag1 = audienceSizeIsGreaterThan(10);
                boolean flag2 = isAudienceEmpty();
                ImageButton imagebutton = mToggleHangoutRingButton;
                boolean flag3;
                if(!flag2)
                    flag3 = flag;
                else
                    flag3 = false;
                imagebutton.setEnabled(flag3);
                if(flag2 || flag1)
                    disableHangoutRing(false, false);
                else
                if((mPreviouslyAudienceEmpty || mPreviouslyOvercapacity) && !mRingBeforeDisable)
                    enableHangoutRing(false);
                mPreviouslyAudienceEmpty = flag2;
                mPreviouslyOvercapacity = flag1;
                if(mStartHangoutButton != null)
                {
                    Button button = mStartHangoutButton;
                    if(isAudienceEmpty())
                        flag = false;
                    button.setEnabled(flag);
                }
            }

        });
        
        if(mShowSuggestedPeople)
        {
            if(mSuggestedPeopleAdapter.isEmpty() && mListHeader != null)
                mListHeader.setVisibility(8);
            getSuggestedPeople();
        }
    }
    
    static void access$500(HostedHangoutFragment hostedhangoutfragment) {
        if(hostedhangoutfragment.mRingBeforeDisable) {
        	hostedhangoutfragment.disableHangoutRing(false, true);
        	return;
        }

        if(hostedhangoutfragment.audienceSizeIsGreaterThan(10))
            hostedhangoutfragment.disableHangoutRing(true, true);
        else
        if(!hostedhangoutfragment.isAudienceEmpty())
            hostedhangoutfragment.enableHangoutRing(true);
    }
    
    
	//==================================================================================
	//							Inner class
	//==================================================================================
	private static enum ActiveHangoutMode {
		MODE_NONE,
		MODE_DISABLE,
		MODE_HIDE;
	}
	
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

    private final class SuggestedPeopleAdpater extends EsCursorAdapter
    {

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

    private static interface SuggestedPeopleQuery
    {

        public static final String columnNames[] = {
            "_id", "participant_id", "full_name", "in_audience"
        };

    }
}
