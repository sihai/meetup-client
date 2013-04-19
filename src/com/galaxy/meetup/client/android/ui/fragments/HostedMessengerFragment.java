/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.LinkedList;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.common.EsCompositeCursorAdapter;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceListener;
import com.galaxy.meetup.client.android.ui.view.ConversationListItemView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.SuggestedParticipantView;
import com.galaxy.meetup.client.util.Dates;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.HelpUrl;

/**
 * 
 * @author sihai
 *
 */
public class HostedMessengerFragment extends HostedEsFragment implements
		LoaderCallbacks, OnItemClickListener {

	private ConversationCursorAdapter mAdapter;
    private Boolean mConnected;
    private Cursor mConversationCursor;
    private Uri mConversationsUri;
    private Bundle mInvitationConversationBundle;
    private ListView mListView;
    private final RTCServiceListener mRTCServiceListener = new RTCServiceListener();
    private boolean mRecordedConversationsEmpty;
    private AudienceData mResultAudience;
    private int mScrollOffset;
    private int mScrollPos;
    private Cursor mSuggestionCursor;
    private Uri mSuggestionsUri;
    
    public HostedMessengerFragment()
    {
    }

    private boolean isLoading()
    {
        boolean flag;
        if(mConversationCursor == null || mSuggestionCursor == null || !RealTimeChatService.getConversationsLoaded() && mAdapter.isEmpty())
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void updateView(View view)
    {
        if(isEmpty() && mConnected != null && !mConnected.booleanValue())
        {
            view.findViewById(0x1020004).setVisibility(8);
            view.findViewById(R.id.list_empty_text).setVisibility(8);
            view.findViewById(R.id.list_empty_progress).setVisibility(8);
            view.findViewById(R.id.server_error).setVisibility(0);
        } else
        if(isLoading())
            showEmptyViewProgress(view);
        else
        if(isEmpty())
            showEmptyView(view, null);
        else
            showContent(view);
    }

    protected final void doShowEmptyView(View view, String s)
    {
        if(isEmpty())
        {
            view.findViewById(0x1020004).setVisibility(0);
            view.findViewById(R.id.list_empty_text).setVisibility(0);
            view.findViewById(R.id.list_empty_progress).setVisibility(8);
        }
        view.findViewById(R.id.server_error).setVisibility(8);
    }

    protected final void doShowEmptyViewProgress(View view)
    {
        if(isEmpty())
        {
            view.findViewById(0x1020004).setVisibility(8);
            view.findViewById(R.id.list_empty_text).setVisibility(8);
            view.findViewById(R.id.list_empty_progress).setVisibility(0);
        }
        view.findViewById(R.id.server_error).setVisibility(8);
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.CONVERSATIONS;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mConversationCursor != null && RealTimeChatService.getConversationsLoaded() && mAdapter.isEmpty())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final boolean needsAsyncData()
    {
        return true;
    }

    public final void onActionButtonClicked(int i)
    {
        if(i == 100)
        {
            recordUserAction(OzActions.CONVERSATIONS_START_NEW);
            startActivity(Intents.getNewConversationActivityIntent(getActivity(), mAccount, null));
        }
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
        if(i == 1 && j == -1 && intent != null)
            mResultAudience = (AudienceData)intent.getParcelableExtra("audience");
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mConnected = null;
        if(bundle != null)
        {
            mScrollPos = bundle.getInt("scroll_pos");
            mScrollOffset = bundle.getInt("scroll_off");
        } else
        {
            mScrollPos = 0;
            mScrollOffset = 0;
        }
        if(bundle != null)
        {
            mInvitationConversationBundle = bundle.getBundle("InvitationConversationBundle");
            mRecordedConversationsEmpty = true;
        }
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        if(EsLog.isLoggable("ConversationList", 3))
            Log.d("ConversationList", (new StringBuilder("onCreateLoader ")).append(i).toString());
        EsCursorLoader escursorloader;
        if(i == 1)
            escursorloader = new EsCursorLoader(getActivity(), mConversationsUri, ConversationQuery.PROJECTION, "is_visible=1 AND is_pending_leave=0", null, "latest_message_timestamp DESC", null);
        else
        if(i == 3)
            escursorloader = new EsCursorLoader(getActivity(), mSuggestionsUri, SuggestionsQuery.PROJECTION, null, null, "sequence ASC", null);
        else
        if(i == 2)
        {
            Uri uri = EsProvider.buildParticipantsUri(mAccount, bundle.getLong("conversation_row_id"));
            FragmentActivity fragmentactivity = getActivity();
            String as[] = ParticipantsQuery.PROJECTION;
            String as1[] = new String[1];
            as1[0] = mAccount.getRealTimeChatParticipantId();
            escursorloader = new EsCursorLoader(fragmentactivity, uri, as, "participant_id!=?", as1, "first_name ASC", uri);
        } else
        {
            escursorloader = null;
        }
        return escursorloader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.conversation_list_fragment, viewgroup, false);
        mListView = (ListView)view.findViewById(0x102000a);
        mListView.setOnItemClickListener(this);
        mAdapter = new ConversationCursorAdapter(getActivity(), mListView);
        mListView.setAdapter(mAdapter);
        if(android.os.Build.VERSION.SDK_INT >= 14 && !ViewConfiguration.get(getActivity()).hasPermanentMenuKey())
            view.findViewById(R.id.help_spacer).setVisibility(0);
        return view;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l) {
        if(mAdapter.getPartitionForPosition(i) != 0) {
        	 Cursor cursor = (Cursor)mAdapter.getItem(i);
             if(cursor != null)
             {
                 String s = cursor.getString(1);
                 String s1 = cursor.getString(2);
                 String s2 = cursor.getString(3);
                 Data.Participant participant = Data.Participant.newBuilder().setParticipantId(s).setFullName(s1).setFirstName(s2).build();
                 startActivity(Intents.getFakeConversationActivityIntent(getActivity(), mAccount, participant, false));
             }
        } else { 
        	Cursor cursor1 = (Cursor)mAdapter.getItem(i);
            if(cursor1 != null)
                if(cursor1.getInt(13) != 0 && cursor1.getString(14) != null)
                {
                    FragmentActivity fragmentactivity1 = getActivity();
                    EsAccount esaccount1 = mAccount;
                    String s3 = cursor1.getString(14);
                    boolean flag1;
                    if(cursor1.getInt(3) != 0)
                        flag1 = true;
                    else
                        flag1 = false;
                    startActivity(Intents.getConversationInvititationActivityIntent(fragmentactivity1, esaccount1, l, s3, flag1));
                } else
                {
                    FragmentActivity fragmentactivity = getActivity();
                    EsAccount esaccount = mAccount;
                    boolean flag;
                    if(cursor1.getInt(3) != 0)
                        flag = true;
                    else
                        flag = false;
                    startActivity(Intents.getConversationActivityIntent(fragmentactivity, esaccount, l, flag));
                }
        }
    }
    
    
    public final void onLoadFinished(Loader loader, Object obj) {
    	// TODO
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == R.id.help)
        {
            String s = getResources().getString(R.string.url_param_help_messenger);
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
        RealTimeChatService.unregisterListener(mRTCServiceListener);
        ((TextView)getView().findViewById(R.id.huddle_help_text)).setText(null);
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        hostactionbar.showTitle(R.string.home_screen_huddle_label);
        hostactionbar.showActionButton(100, R.drawable.ic_menu_start_new_huddle, R.string.menu_new_conversation);
    }
    
    public final void onPrepareOptionsMenu(Menu menu) {
    	MenuItem menuitem;
        int size = menu.size();
        for(int j = 0; j < size; j++) {
        	menuitem = menu.getItem(j);
        	// TODO why 100 ?
        	if(100 == menuitem.getItemId()) {
        		menuitem.setVisible(true);
        	}
        }
    }

    public final void onResume()
    {
        super.onResume();
        RealTimeChatService.registerListener(mRTCServiceListener);
        TextView textview = (TextView)getView().findViewById(R.id.huddle_help_text);
        Uri uri = HelpUrl.getHelpUrl(getActivity(), "plusone_messenger_promo");
        Resources resources = getResources();
        int i = R.string.huddle_help_text;
        Object aobj[] = new Object[1];
        aobj[0] = uri.toString();
        Spanned spanned = Html.fromHtml(resources.getString(i, aobj));
        URLSpan aurlspan[] = (URLSpan[])spanned.getSpans(0, spanned.length(), URLSpan.class);
        if(aurlspan.length > 0)
        {
            SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder(spanned);
            final URLSpan urlSpan = aurlspan[0];
            int j = spanned.getSpanStart(urlSpan);
            int k = spanned.getSpanEnd(urlSpan);
            spannablestringbuilder.setSpan(new ClickableSpan() {

                public final void onClick(View view)
                {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse(urlSpan.getURL()));
                    startExternalActivity(intent);
                }
            }, j, k, 33);
            textview.setText(spannablestringbuilder);
            textview.setMovementMethod(LinkMovementMethod.getInstance());
        }
        updateView(getView());
        if(mResultAudience != null)
        {
            long l = mInvitationConversationBundle.getLong("conversation_row_id", -1L);
            RealTimeChatService.inviteParticipants(getActivity(), mAccount, l, mResultAudience);
            mInvitationConversationBundle = null;
            mResultAudience = null;
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putBundle("InvitationConversationBundle", mInvitationConversationBundle);
        if(!getActivity().isFinishing() && mListView != null)
        {
            if(mListView != null)
            {
                mScrollPos = mListView.getFirstVisiblePosition();
                mScrollOffset = 0;
            }
            bundle.putInt("scroll_pos", mScrollPos);
            bundle.putInt("scroll_off", mScrollOffset);
        }
    }

    protected final void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mAccount = (EsAccount)bundle.getParcelable("account");
        if(mAccount != null)
        {
            mConversationsUri = EsProvider.appendAccountParameter(EsProvider.CONVERSATIONS_URI, mAccount);
            mSuggestionsUri = EsProvider.appendAccountParameter(EsProvider.MESSENGER_SUGGESTIONS_URI, mAccount);
            getLoaderManager().initLoader(1, null, this);
            getLoaderManager().initLoader(3, null, this);
            if(EsLog.isLoggable("ConversationList", 3))
                Log.d("ConversationList", "setAccount");
        }
        if(bundle.getBoolean("reset_notifications", false))
            RealTimeChatService.resetNotifications(getActivity(), mAccount);
    }

    protected final void showContent(View view)
    {
        super.showContent(view);
        view.findViewById(0x1020004).setVisibility(8);
        view.findViewById(R.id.list_empty_text).setVisibility(8);
        view.findViewById(R.id.list_empty_progress).setVisibility(8);
        view.findViewById(R.id.server_error).setVisibility(8);
    }

    //======================================================================================
  	//							Inner class
  	//======================================================================================
    
    final class ConversationCursorAdapter extends EsCompositeCursorAdapter {
    	private Cursor mConversationsCursor;
        private Cursor mSuggestionsCursor;

        public ConversationCursorAdapter(Context context, AbsListView abslistview) {
            super(context);
            addPartition(false, false);
            addPartition(false, true);
            abslistview.setRecyclerListener(new android.widget.AbsListView.RecyclerListener() {
            	
            	public final void onMovedToScrapHeap(View view) {
                    if(!(view instanceof ConversationListItemView)) {
                    	if(view instanceof SuggestedParticipantView)
                            ((SuggestedParticipantView)view).clear();
                    } else { 
                    	((ConversationListItemView)view).clear();
                    }
                }
            });
        }
        
        protected final void bindView(View view, int i, Cursor cursor, int j) {
        	
        	if(null == cursor || cursor.isClosed()) {
        		return;
        	}
        	
        	switch(i)
            {
            default:
                break;

            case 0: // '\0'
                ConversationListItemView conversationlistitemview = (ConversationListItemView)view;
                conversationlistitemview.clear();
                String s = cursor.getString(11);
                boolean flag;
                String s5;
                if(cursor.getInt(13) == 1)
                {
                    conversationlistitemview.setConversationName(cursor.getString(7));
                    String s7 = cursor.getString(15);
                    if(s7 == null)
                        s7 = "";
                    conversationlistitemview.setLastMessage(getContext().getString(R.string.realtimechat_invitation_preview_text, new Object[] {
                        s7
                    }));
                } else
                {
                    String s1 = cursor.getString(6);
                    if(s1 == null)
                        s1 = cursor.getString(7);
                    conversationlistitemview.setConversationName(s1);
                    int k = cursor.getInt(12);
                    String s2 = cursor.getString(8);
                    String s3 = cursor.getString(9);
                    String s4;
                    if(s == null)
                        s4 = getContext().getResources().getString(R.string.realtimechat_participant_without_name_text);
                    else
                        s4 = s;
                    if(s3 != null)
                        conversationlistitemview.setLastMessage(getContext().getString(R.string.realtimechat_name_and_message_image, new Object[] {
                            s4
                        }));
                    else
                    if(s2 != null)
                        if(k == 1)
                            conversationlistitemview.setLastMessage(getContext().getString(R.string.realtimechat_name_and_message_text, new Object[] {
                                s4, s2
                            }));
                        else
                            conversationlistitemview.setLastMessage(s2.replaceAll("\\<.*?\\>", ""));
                }
                conversationlistitemview.setTimeSince(Dates.getShortRelativeTimeSpanString(getContext(), cursor.getLong(4) / 1000L));
                conversationlistitemview.setUnreadCount(cursor.getInt(5));
                if(cursor.getInt(2) == 1)
                    flag = true;
                else
                    flag = false;
                conversationlistitemview.setMuted(flag);
                s5 = cursor.getString(16);
                if(s5 != null)
                {
                    StringTokenizer stringtokenizer = new StringTokenizer(s5, "|");
                    LinkedList linkedlist = new LinkedList();
                    do
                    {
                        if(!stringtokenizer.hasMoreElements())
                            break;
                        String s6 = EsPeopleData.extractGaiaId(stringtokenizer.nextToken());
                        if(s6 != null && !s6.equals(mAccount.getGaiaId()))
                            linkedlist.add(s6);
                    } while(true);
                    conversationlistitemview.setParticipantsId(linkedlist, mAccount.getGaiaId());
                } else
                {
                    conversationlistitemview.setParticipantsId(null, mAccount.getGaiaId());
                }
                conversationlistitemview.updateContentDescription();
                break;
            case 1: // '\001'
            	SuggestedParticipantView suggestedparticipantview;
                suggestedparticipantview = (SuggestedParticipantView)view;
                suggestedparticipantview.setParticipantId(EsPeopleData.extractGaiaId(cursor.getString(1)));
                suggestedparticipantview.setParticipantName(cursor.getString(2));
                break;
            }
        }

        protected final View getView(int i, Cursor cursor, int j, View view, ViewGroup viewgroup) {
            View view1 = null;
            if(view == null) {
            	if(view1 == null)
                    view1 = newView(getContext(), i, cursor, j, viewgroup);
                bindView(view1, i, cursor, j);
                return view1;
            } else {
            	if(0 == i) {
            		boolean flag1 = view instanceof ConversationListItemView;
                    view1 = null;
                    if(flag1)
                        view1 = view;
            	} else if(1 == i) {
            		boolean flag = view instanceof SuggestedParticipantView;
                    view1 = null;
                    if(flag)
                        view1 = view;
            	} else {
            		if(view1 == null)
                        view1 = newView(getContext(), i, cursor, j, viewgroup);
                    bindView(view1, i, cursor, j);
            	}
            }
            
            return view1;
           
        }

        public final boolean hasStableIds()
        {
            return true;
        }

        @Override
        protected final View newHeaderView(Context context, int partion, Cursor curosr, ViewGroup viewgroup)
        {
            return LayoutInflater.from(context).inflate(R.layout.section_header_view, null);
        }

        protected final View newView(Context context, int partion, Cursor cursor, int position, ViewGroup parent) {
            View view = null;
            LayoutInflater layoutinflater = LayoutInflater.from(context);
            if(0 == partion) {
            	view = layoutinflater.inflate(R.layout.conversation_list_item_view, null);
            } else if(1 == partion) {
            	view = layoutinflater.inflate(R.layout.suggested_participant_view, null);
            }
            return view;
        }

        public final void onLoadFinished(Loader loader, Cursor cursor) {
            if(loader.getId() != 1) {
            	if(loader.getId() == 3)
                {
                    mSuggestionsCursor = cursor;
                    if(mConversationCursor != null)
                        changeCursor(1, cursor);
                } 
            } else { 
            	mConversationsCursor = cursor;
                changeCursor(0, cursor);
                if(mSuggestionsCursor != null)
                    changeCursor(1, mSuggestionsCursor);
            }
            
            if(EsLog.isLoggable("ConversationList", 3))
                Log.d("ConversationList", (new StringBuilder("onLoadFinished suggestions ")).append(mSuggestionsCursor).append(" conversations ").append(mConversationsCursor).toString());
            return;
        }

    }
    
    public static interface ConversationQuery
    {

        public static final String PROJECTION[] = {
            "_id", "conversation_id", "is_muted", "is_group", "latest_message_timestamp", "unread_count", "name", "generated_name", "latest_message_text", "latest_message_image_url", 
            "latest_message_author_full_name", "latest_message_author_first_name", "latest_message_type", "is_pending_accept", "inviter_id", "inviter_first_name", "packed_participants"
        };

    }

    public static interface ParticipantsQuery
    {

        public static final String PROJECTION[] = {
            "participant_id", "full_name", "first_name"
        };

    }

    private final class RTCServiceListener extends RealTimeChatServiceListener
    {

        public final void onConnected()
        {
            mConnected = Boolean.valueOf(true);
            updateView(getView());
        }

        public final void onConversationsLoaded()
        {
            if(isEmpty())
            {
                getLoaderManager().restartLoader(1, null, HostedMessengerFragment.this);
                getLoaderManager().restartLoader(3, null, HostedMessengerFragment.this);
            }
        }

        public final void onDisconnected()
        {
            mConnected = Boolean.valueOf(false);
            updateView(getView());
        }

    }

    public static interface SuggestionsQuery
    {

        public static final String PROJECTION[] = {
            "_id", "participant_id", "full_name", "first_name"
        };

    }
}
