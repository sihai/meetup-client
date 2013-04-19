/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import WriteReviewOperation.MediaRef;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.LocalImageRequest;
import com.galaxy.meetup.client.android.content.MediaImageRequest;
import com.galaxy.meetup.client.android.content.cache.ImageRequest;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceListener;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceResult;
import com.galaxy.meetup.client.android.ui.activity.BaseActivity;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.view.HangoutTileEventMessageListItemView;
import com.galaxy.meetup.client.android.ui.view.MessageClickListener;
import com.galaxy.meetup.client.android.ui.view.MessageListItemView;
import com.galaxy.meetup.client.android.ui.view.MessageListItemViewImage;
import com.galaxy.meetup.client.android.ui.view.SystemMessageListItemView;
import com.galaxy.meetup.client.util.Dates;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class MessageListFragment extends EsListFragment implements
		LoaderCallbacks, AlertDialogListener, MessageClickListener {

	private static long COLLAPSE_POSTS_THRESHOLD = 60000L;
    private android.widget.RelativeLayout.LayoutParams defaultListViewLayoutParams;
    private EsAccount mAccount;
    private Runnable mAnimateTypingVisibilityRunnable;
    private Runnable mCheckExpiredTypingRunnable;
    private String mConversationId;
    private Long mConversationRowId;
    private long mEarliestEventTimestamp;
    private long mFirstEventTimestamp;
    private Handler mHandler;
    private View mHeaderView;
    private boolean mInitialLoadFinished;
    private boolean mIsGroup;
    private boolean mIsTypingVisible;
    private LeaveConversationListener mLeaveConversationListener;
    private boolean mLoadingOlderEvents;
    private Uri mMessagesUri;
    private HashMap mParticipantList;
    private final RealTimeChatServiceListener mRTCServiceListener = new RTCServiceListener();
    private Integer mRequestId;
    private Data.Participant mSingleParticipant;
    private Animation mSlideInUpAnimation;
    private Animation mSlideOutDownAnimation;
    private int mTotalItemBeforeLoadingOlder;
    private TranslateAnimation mTranslateListAnimation;
    private HashMap mTypingParticipants;
    private TextView mTypingTextView;
    private View mTypingView;
    private boolean mTypingVisibilityChanged;
    
	public MessageListFragment()
    {
        mRequestId = null;
        mTypingParticipants = new HashMap();
        mIsTypingVisible = false;
        mTypingVisibilityChanged = false;
        mHandler = new Handler();
        mAnimateTypingVisibilityRunnable = new Runnable() {

            public final void run()
            {
                animateTypingVisibility();
            }
        };
        mCheckExpiredTypingRunnable = new Runnable() {

            public final void run()
            {
                long l = System.currentTimeMillis();
                updateTypingVisibility();
            }
        };
    }

    private synchronized void animateTypingVisibility()
    {
    	if(null == mTypingView || null == mListView) {
    		if(EsLog.isLoggable("MessageListFragment", 3))
                Log.d("MessageListFragment", "Ignoring animation due to null views");
    		return;
    	}
    	
    	boolean flag = true;
    	mTypingVisibilityChanged = false;
        if(mTypingParticipants.size() <= 0) {
        	flag = false;
        } else { 
        	flag = true;
        }
        
        if(mIsTypingVisible == flag) {
        	mIsTypingVisible = flag;
        	return;
        }
        
        View view;
        int i;
        view = mTypingView;
        i = 0;
        Exception exception;
        int j;
        View view1;
        Animation animation;
        if(!flag)
            i = 8;
        view.setVisibility(i);
        ((ListView)mListView).setLayoutParams(defaultListViewLayoutParams);
        j = mTypingView.getHeight();
        if(!flag) {
        	mTranslateListAnimation = new TranslateAnimation(0, 0.0F, 0, 0.0F, 0, -j, 0, 0.0F);
        } else { 
        	android.widget.RelativeLayout.LayoutParams layoutparams = new android.widget.RelativeLayout.LayoutParams(defaultListViewLayoutParams);
            layoutparams.addRule(2, mTypingView.getId());
            ((ListView)mListView).setLayoutParams(layoutparams);
            mTranslateListAnimation = new TranslateAnimation(0, 0.0F, 0, 0.0F, 0, j, 0, 0.0F);
        }
        
        int k = ((View)((ListView)mListView).getParent()).getHeight();
        int l = ((View)((ListView)mListView).getParent()).getWidth();
        mTranslateListAnimation.initialize(((ListView)mListView).getWidth(), ((ListView)mListView).getHeight(), l, k);
        mTranslateListAnimation.setDuration(mSlideInUpAnimation.getDuration());
        view1 = mTypingView;
        if(flag) {
        	animation = mSlideInUpAnimation;
        } else{
        	animation = mSlideOutDownAnimation;
        }
        
        view1.startAnimation(animation);
        ((ListView)mListView).startAnimation(mTranslateListAnimation);
        mIsTypingVisible = flag;

    }

    private synchronized boolean isTypingAnimationPlaying() {
        if(mTranslateListAnimation == null || !mTranslateListAnimation.hasStarted()) {
        	return false; 
        } else { 
        	boolean flag1 = mTranslateListAnimation.hasEnded();
        	if(flag1) {
        		return false;
        	} else {
        		return true;
        	}
        }
    }

    private void recordUserAction(OzActions ozactions)
    {
        if(mAccount != null)
        {
            FragmentActivity fragmentactivity = getActivity();
            EsAnalytics.recordActionEvent(fragmentactivity, mAccount, ozactions, OzViews.getViewForLogging(fragmentactivity));
        }
    }

    private void updateHeaderVisibility()
    {
        if(EsLog.isLoggable("MessageListFragment", 3))
            Log.d("MessageListFragment", (new StringBuilder("updateHeaderVisibility ")).append(mLoadingOlderEvents).append(" ").append(mInitialLoadFinished).toString());
        if(mLoadingOlderEvents && mInitialLoadFinished)
            mHeaderView.setVisibility(0);
        else
            mHeaderView.setVisibility(8);
    }

    private synchronized void updateTypingVisibility() {
    	
        TextView textview = mTypingTextView;
        if(null == textview) {
        	return;
        }
        
        String as[];
        int i;
        as = new String[Math.min(3, mTypingParticipants.size())];
        Iterator iterator = mTypingParticipants.values().iterator();
        i = 0;
        do
        {
            if(!iterator.hasNext())
                break;
            UserTypingInfo usertypinginfo = (UserTypingInfo)iterator.next();
            int j1 = i + 1;
            as[i] = usertypinginfo.userName;
            if(j1 == 3)
                break;
            i = j1;
        } while(true);
        
        String s;
        switch(mTypingParticipants.size()) {
        case 0:
        	s = mTypingTextView.getText().toString();
        	break;
        case 1:
        	int l = R.string.realtimechat_one_person_typing_text;
            Object aobj2[] = new Object[1];
            aobj2[0] = as[0];
            s = getString(l, aobj2);
        	break;
        case 2:
            int k = R.string.realtimechat_two_people_typing_text;
            Object aobj1[] = new Object[2];
            aobj1[0] = as[0];
            aobj1[1] = as[1];
            s = getString(k, aobj1);
        	break;
        case 3:
        	int j = R.string.realtimechat_three_people_typing_text;
            Object aobj[] = new Object[3];
            aobj[0] = as[0];
            aobj[1] = as[1];
            aobj[2] = as[2];
            s = getString(j, aobj);
        	break;
        default:
        	int i1 = R.string.realtimechat_more_than_three_people_typing_text;
            Object aobj3[] = new Object[3];
            aobj3[0] = as[0];
            aobj3[1] = as[1];
            aobj3[2] = Integer.valueOf(-2 + mTypingParticipants.size());
            s = getString(i1, aobj3);
        	break;
        }
        
        mTypingTextView.setText(s);
        if(!isTypingAnimationPlaying()) {
        	animateTypingVisibility();
        	return;
        } else {
        	 if(EsLog.isLoggable("MessageListFragment", 3))
                 Log.d("MessageListFragment", "Animation already playing. Setting typing visibility changed");
             mTypingVisibilityChanged = true;
             return;
        }
    }

    public final void displayLeaveConversationDialog()
    {
        AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.realtimechat_leave_conversation_title), getString(R.string.realtimechat_leave_conversation_text), getString(R.string.realtimechat_conversation_leave_menu_item_text), getString(R.string.cancel));
        alertfragmentdialog.setTargetFragment(this, 0);
        alertfragmentdialog.show(getFragmentManager(), "leave_conversation");
    }

    public final void handleFatalError(int i) {
    	
    	int j = R.string.realtimechat_conversation_error_dialog_general;
    	
    	if(3 == i) {
    		j = R.string.realtimechat_conversation_error_dialog_huddle_too_big;
    	} else if(4 == i) {
    		j = R.string.realtimechat_conversation_error_dialog_some_invalid_participants;
    	} else {
    		j = R.string.realtimechat_conversation_error_dialog_general;
    	}
    	
    	Resources resources = getResources();
        AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(resources.getString(R.string.realtimechat_conversation_error_dialog_title), resources.getString(j), getString(R.string.realtimechat_conversation_error_dialog_leave_button), null);
        alertfragmentdialog.setTargetFragment(this, 0);
        alertfragmentdialog.show(getFragmentManager(), "conversation_error");
        return;
    }

    public final void messageLoadFailed()
    {
        if(EsLog.isLoggable("MessageListFragment", 3))
            Log.d("MessageListFragment", "messageLoadFailed");
        mLoadingOlderEvents = false;
        updateHeaderVisibility();
        Toast.makeText(getActivity(), R.string.realtimechat_failure_loading_messages, 0).show();
    }

    public final void messageLoadSucceeded() {
        if(EsLog.isLoggable("MessageListFragment", 3))
            Log.d("MessageListFragment", "messageLoadSucceeded");
        (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {

            public final void run()
            {
                mLoadingOlderEvents = false;
                updateHeaderVisibility();
            }
        }, 500L);
    }

    public final void onCancelButtonClicked(long l)
    {
        RealTimeChatService.removeMessage(getActivity(), mAccount, l);
    }

    public final void onCreate(Bundle bundle) {
    	
        super.onCreate(bundle);
        long l;
        FragmentActivity fragmentactivity = getActivity();
        if(bundle != null)
        {
            if(bundle.containsKey("request_id"))
                mRequestId = Integer.valueOf(bundle.getInt("request_id"));
            else
                mRequestId = null;
            mLoadingOlderEvents = bundle.getBoolean("loading_older_events", false);
            mInitialLoadFinished = bundle.getBoolean("initial_load_finished", false);
        } else
        {
            mRequestId = null;
            mInitialLoadFinished = false;
            mLoadingOlderEvents = false;
        }
        mSingleParticipant = (Data.Participant)fragmentactivity.getIntent().getSerializableExtra("participant");
        mAccount = (EsAccount)getActivity().getIntent().getExtras().get("account");
        mIsGroup = getActivity().getIntent().getBooleanExtra("is_group", false);
        l = fragmentactivity.getIntent().getLongExtra("conversation_row_id", -1L);
        if(l != -1L)
        {
            mConversationRowId = Long.valueOf(l);
            mMessagesUri = EsProvider.buildMessagesUri(mAccount, mConversationRowId.longValue());
            getLoaderManager().initLoader(1, null, this);
        } else
        {
            mConversationRowId = null;
        }
        mSlideOutDownAnimation = AnimationUtils.loadAnimation(fragmentactivity, R.anim.slide_out_down_self);
        mSlideOutDownAnimation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation)
            {
                if(mTypingVisibilityChanged)
                    mHandler.post(mAnimateTypingVisibilityRunnable);
            }

            public final void onAnimationRepeat(Animation animation)
            {
            }

            public final void onAnimationStart(Animation animation)
            {
            }
        });
        mSlideOutDownAnimation.setDuration(350L);
        mSlideInUpAnimation = AnimationUtils.loadAnimation(fragmentactivity, R.anim.slide_in_up_self);
        mSlideInUpAnimation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation)
            {
                if(mTypingVisibilityChanged)
                    mHandler.post(mAnimateTypingVisibilityRunnable);
            }

            public final void onAnimationRepeat(Animation animation)
            {
            }

            public final void onAnimationStart(Animation animation)
            {
            }

        });
        mSlideInUpAnimation.setDuration(350L);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        EsCursorLoader escursorloader;
        if(i == 1)
            escursorloader = new EsCursorLoader(getActivity(), mMessagesUri, MessagesQuery.PROJECTION, null, null, "timestamp");
        else
            escursorloader = null;
        return escursorloader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.message_list_fragment, viewgroup, false);
        mListView = (ListView)view.findViewById(0x102000a);
        defaultListViewLayoutParams = (android.widget.RelativeLayout.LayoutParams)((ListView)mListView).getLayoutParams();
        mTypingView = view.findViewById(R.id.typing_text);
        mTypingTextView = (TextView)mTypingView.findViewById(R.id.typing_text_view);
        View view1 = layoutinflater.inflate(R.layout.message_list_item_loading_older, viewgroup);
        ((ListView)mListView).addHeaderView(view1);
        mHeaderView = view1.findViewById(R.id.message_list_item_loading_content);
        mAdapter = new MessageCursorAdapter(this, mListView, null);
        ((ListView)mListView).setAdapter(mAdapter);
        if(android.os.Build.VERSION.SDK_INT >= 11)
            ((ListView)mListView).setChoiceMode(0);
        ((ListView)mListView).setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {

            public final boolean onItemLongClick(AdapterView adapterview, View view3, int j, long l) {
            	
            	CharSequence charsequence = null;
                if(!(view3 instanceof MessageListItemView)) {
                	boolean flag = view3 instanceof SystemMessageListItemView;
                    charsequence = null;
                    if(flag)
                        charsequence = ((SystemMessageListItemView)view3).getText();
                } else { 
                	charsequence = ((MessageListItemView)view3).getMessage();
                }
                
                if(charsequence != null && android.os.Build.VERSION.SDK_INT >= 11)
                {
                    ClipboardManager clipboardmanager = (ClipboardManager)getActivity().getSystemService("clipboard");
                    if(clipboardmanager != null)
                    {
                        clipboardmanager.setText(charsequence);
                        Toast.makeText(getActivity(), R.string.copied_to_clipboard, 0).show();
                    }
                }
                return true;

            }

        });
        ((ListView)mListView).setOnScrollListener(new android.widget.AbsListView.OnScrollListener() {

            public final void onScroll(AbsListView abslistview, int j, int k, int l)
            {
                if(mInitialLoadFinished && !mLoadingOlderEvents && j == 0 && mConversationRowId != null && (mEarliestEventTimestamp > mFirstEventTimestamp || mFirstEventTimestamp == 0L || mEarliestEventTimestamp == 0L) && mConversationId != null && !mConversationId.startsWith("c:"))
                {
                    mLoadingOlderEvents = true;
                    mHeaderView.setVisibility(0);
                    mTotalItemBeforeLoadingOlder = ((MessageCursorAdapter)mAdapter).getCount();
                    mRequestId = Integer.valueOf(RealTimeChatService.requestMoreEvents(getActivity(), mAccount, mConversationRowId.longValue()));
                    updateHeaderVisibility();
                }
            }

            public final void onScrollStateChanged(AbsListView abslistview, int j)
            {
            }
        });
        if(mSingleParticipant != null)
        {
            View view2 = view.findViewById(R.id.empty_conversation_view);
            TextView textview = (TextView)view.findViewById(R.id.empty_conversation_text);
            FragmentActivity fragmentactivity = getActivity();
            int i = R.string.new_conversation_description;
            Object aobj[] = new Object[1];
            aobj[0] = mSingleParticipant.getFullName();
            textview.setText(fragmentactivity.getString(i, aobj));
            view2.setVisibility(0);
        }
        return view;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle, int i)
    {
        return super.onCreateView(layoutinflater, viewgroup, bundle, i);
    }

    public final void onDestroyView()
    {
        super.onDestroyView();
    }

    public final void onDialogCanceled(String s)
    {
        if(s.equals("conversation_error"))
        {
            RealTimeChatService.leaveConversation(getActivity(), mAccount, mConversationRowId.longValue());
            mLeaveConversationListener.leaveConversation();
            getActivity().finish();
        }
    }

    public final void onDialogListClick(int i, Bundle bundle)
    {
    }

    public final void onDialogNegativeClick(String s)
    {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s)
    {
        if(!s.equals("leave_conversation")) {
        	if(s.equals("conversation_error"))
            {
                RealTimeChatService.leaveConversation(getActivity(), mAccount, mConversationRowId.longValue());
                mLeaveConversationListener.leaveConversation();
                getActivity().finish();
            } else
            if(EsLog.isLoggable("MessageListFragment", 6))
                Log.e("MessageListFragment", (new StringBuilder("invalidate dialog ")).append(s).toString()); 
        } else { 
        	recordUserAction(OzActions.GROUP_CONVERSATION_LEAVE);
            RealTimeChatService.leaveConversation(getActivity(), mAccount, mConversationRowId.longValue());
            mLeaveConversationListener.leaveConversation();
            getActivity().finish();
        }
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(EsLog.isLoggable("MessageListFragment", 3))
            Log.d("MessageListFragment", (new StringBuilder("onLoadFinished ")).append(mLoadingOlderEvents).toString());
        if(loader.getId() == 1)
        {
            ((MessageCursorAdapter)mAdapter).swapCursor(cursor);
            restoreScrollPosition();
            if(cursor.getCount() > 0)
                showContent(getView());
            else
                showEmptyView(getView());
            if(mLoadingOlderEvents)
            {
                int i = cursor.getCount() - mTotalItemBeforeLoadingOlder;
                int j;
                if(i < 0)
                    j = 0;
                else
                if(i >= cursor.getCount())
                    j = -1 + cursor.getCount();
                else
                    j = i;
                ((ListView)mListView).setSelection(j);
            }
            mInitialLoadFinished = true;
            updateHeaderVisibility();
            FragmentActivity fragmentactivity = getActivity();
            if(fragmentactivity instanceof BaseActivity)
                ((BaseActivity)fragmentactivity).onAsyncData();
        }
    }

    public final void onLoaderReset(Loader loader)
    {
        ((MessageCursorAdapter)mAdapter).swapCursor(null);
    }

    public final void onMediaImageClick(String s, String s1)
    {
        Intents.PhotoViewIntentBuilder photoviewintentbuilder = Intents.newPhotoViewActivityIntentBuilder(getActivity());
        photoviewintentbuilder.setAccount(mAccount).setPhotoOnly(Boolean.valueOf(true)).setPhotoUrl(s).setGaiaId(s1).setAlbumName(getString(R.string.photo_view_messenger_title));
        startActivity(photoviewintentbuilder.build());
    }

    public final void onPause()
    {
        super.onPause();
        RealTimeChatService.unregisterListener(mRTCServiceListener);
        mHandler.removeCallbacks(mAnimateTypingVisibilityRunnable);
        mHandler.removeCallbacks(mCheckExpiredTypingRunnable);
    }

    public final void onResume()
    {
        super.onResume();
        if(!getActivity().isFinishing())
        {
            mIsTypingVisible = false;
            mTypingVisibilityChanged = false;
            mTypingParticipants.clear();
            RealTimeChatService.registerListener(mRTCServiceListener);
            if(mConversationRowId != null && ((MessageCursorAdapter)mAdapter).getCursor() == null)
                showEmptyViewProgress(getView());
            if(mRequestId != null && !RealTimeChatService.isRequestPending(mRequestId.intValue()))
            {
                RealTimeChatServiceResult realtimechatserviceresult = RealTimeChatService.removeResult(mRequestId.intValue());
                if(realtimechatserviceresult != null)
                    if(realtimechatserviceresult.getErrorCode() == 1)
                        messageLoadSucceeded();
                    else
                        messageLoadFailed();
                mRequestId = null;
            }
            ((MessageCursorAdapter)mAdapter).onResume();
        }
    }

    public final void onRetryButtonClicked(long l)
    {
        recordUserAction(OzActions.CONVERSATION_RETRY_SEND);
        RealTimeChatService.retrySendMessage(getActivity(), mAccount, l);
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mRequestId != null)
            bundle.putInt("request_id", mRequestId.intValue());
        bundle.putBoolean("loading_older_events", mLoadingOlderEvents);
        bundle.putBoolean("initial_load_finished", mInitialLoadFinished);
    }

    public void onScroll(AbsListView abslistview, int i, int j, int k)
    {
        super.onScroll(abslistview, i, j, k);
    }

    public void onScrollStateChanged(AbsListView abslistview, int i)
    {
        super.onScrollStateChanged(abslistview, i);
    }

    public final void onUserImageClicked(String s)
    {
        if(s != null)
        {
            Intent intent = Intents.getProfileActivityByGaiaIdIntent(getActivity(), mAccount, String.valueOf(s), null);
            getActivity().startActivity(intent);
        }
    }

    public final void reinitialize()
    {
        mInitialLoadFinished = false;
        mAccount = (EsAccount)getActivity().getIntent().getExtras().get("account");
        mIsGroup = getActivity().getIntent().getBooleanExtra("is_group", false);
        long l = getActivity().getIntent().getLongExtra("conversation_row_id", -1L);
        if(l != -1L)
        {
            mConversationRowId = Long.valueOf(l);
            mMessagesUri = EsProvider.buildMessagesUri(mAccount, mConversationRowId.longValue());
            getLoaderManager().restartLoader(1, null, this);
        } else
        {
            mConversationRowId = null;
        }
        mSingleParticipant = (Data.Participant)getActivity().getIntent().getSerializableExtra("participant");
        if(getView() != null)
        {
            View view = getView().findViewById(R.id.empty_conversation_view);
            if(mSingleParticipant != null)
            {
                TextView textview = (TextView)getView().findViewById(R.id.empty_conversation_text);
                FragmentActivity fragmentactivity = getActivity();
                int i = R.string.new_conversation_description;
                Object aobj[] = new Object[1];
                aobj[0] = mSingleParticipant.getFullName();
                textview.setText(fragmentactivity.getString(i, aobj));
                view.setVisibility(0);
            } else
            {
                view.setVisibility(8);
            }
        }
        mTypingParticipants.clear();
        animateTypingVisibility();
    }

    public final void setConversationInfo(String s, long l, long l1)
    {
        if(EsLog.isLoggable("MessageListFragment", 3))
            Log.d("MessageListFragment", (new StringBuilder("setConversationInfo first ")).append(l).append(" earliest local ").append(l1).toString());
        mConversationId = s;
        mFirstEventTimestamp = l;
        mEarliestEventTimestamp = l1;
    }

    public final void setLeaveConversationListener(LeaveConversationListener leaveconversationlistener)
    {
        mLeaveConversationListener = leaveconversationlistener;
    }

    public final void setParticipantList(HashMap hashmap)
    {
        mParticipantList = hashmap;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private class UserTypingInfo {

        public long typingStartTimeMs;
        public String userName;

        public UserTypingInfo(String s)
        {
            super();
            userName = s;
            typingStartTimeMs = System.currentTimeMillis();
        }
    }

    public static interface LeaveConversationListener
    {

        public abstract void leaveConversation();
    }

    static final class MessageCursorAdapter extends EsCursorAdapter implements MessageListItemViewImage.OnMeasuredListener {

    	final MessageListFragment mFragment;
        final List mViews = new ArrayList();

        public MessageCursorAdapter(MessageListFragment messagelistfragment, AbsListView abslistview, Cursor cursor)
        {
            super(messagelistfragment.getActivity(), null);
            mFragment = messagelistfragment;
            abslistview.setRecyclerListener(new AbsListView.RecyclerListener() {
            	
            	public final void onMovedToScrapHeap(View view) {
                    if(!(view instanceof MessageListItemView)) { 
                    	 if(view instanceof MessageListItemViewImage)
                             ((MessageListItemViewImage)view).clear(); 
                    } else { 
                    	((MessageListItemView)view).clear();
                    }
                    mViews.remove(view);
                }
            });
        }
        
        public final View getView(int i, View view, ViewGroup viewgroup)
        {
            if(!mDataValid)
                throw new IllegalStateException("this should only be called when the cursor is valid");
            if(!mCursor.moveToPosition(i))
                throw new IllegalStateException((new StringBuilder("couldn't move cursor to position ")).append(i).toString());
            LayoutInflater layoutinflater = (LayoutInflater)mContext.getSystemService("layout_inflater");
            int j = mCursor.getInt(7);
            
            boolean flag6;
            boolean flag7;
            boolean flag9;
            long l3;
            Context context;
            Context context1;
            Context context2;
            String s;
            long l;
            CharSequence charsequence;
            boolean flag;
            boolean flag1;
            boolean flag2;
            boolean flag3;
            boolean flag4;
            long l1;
            boolean flag5;
            int k;
            long i1;
            boolean flag8;
            boolean flag10;
            String s3;
            Object obj1;
            Object obj;
            Context context3;
            String s1;
            long l2;
            CharSequence charsequence1;
            String s2;
            
            if(j == 1)
            {
                if(mCursor.getString(11) != null)
                {
                    if(view != null && (view instanceof MessageListItemViewImage))
                    {
                        obj = (MessageListItemViewImage)view;
                    } else
                    {
                        obj = (MessageListItemViewImage)layoutinflater.inflate(R.layout.message_list_item_view_image, null);
                        ((MessageListItemViewImage) (obj)).setMessageClickListener(mFragment);
                    }
                    context3 = mContext;
                    if(!mViews.contains(obj))
                        mViews.add(obj);
                    ((MessageListItemViewImage) (obj)).clear();
                    s1 = mCursor.getString(3);
                    l2 = mCursor.getLong(5) / 1000L;
                    charsequence1 = Dates.getShortRelativeTimeSpanString(context3, l2);
                    ((MessageListItemViewImage) (obj)).setMessageRowId(mCursor.getLong(0));
                    ((MessageListItemViewImage) (obj)).setAuthorName(mCursor.getString(9));
                    s2 = mCursor.getString(11);
                    if(s2.length() > 0)
                    {
                        if(s2.startsWith("//"))
                            s3 = (new StringBuilder("http:")).append(s2).toString();
                        else
                            s3 = s2;
                        if(((MessageListItemViewImage) (obj)).getImageWidth() != null)
                        {
                            int j1 = ((MessageListItemViewImage) (obj)).getImageWidth().intValue();
                            int k1 = ((MessageListItemViewImage) (obj)).getImageHeight().intValue();
                            if(EsLog.isLoggable("MessageListFragment", 4))
                                Log.i("MessageListFragment", (new StringBuilder("BindUserImageMessageView image width ")).append(j1).append(" height ").append(k1).toString());
                            if(s3.startsWith("content://"))
                                obj1 = new LocalImageRequest(new MediaRef(null, 0L, null, Uri.parse(s3), MediaRef.MediaType.IMAGE), j1, k1);
                            else
                                obj1 = new MediaImageRequest(ImageUtils.getCenterCroppedAndResizedUrl(j1, k1, s2), 3, j1, k1, false);
                        } else
                        {
                            ((MessageListItemViewImage) (obj)).setOnMeasureListener(this);
                            obj1 = null;
                        }
                        ((MessageListItemViewImage) (obj)).setImage(s2, ((ImageRequest) (obj1)));
                    }
                    flag6 = mCursor.isFirst();
                    flag7 = false;
                    if(!flag6)
                    {
                        flag9 = mCursor.moveToPrevious();
                        flag7 = false;
                        if(flag9)
                        {
                            l3 = mCursor.getLong(5) / 1000L;
                            if(s1.equals(mCursor.getString(3)) && mCursor.getInt(7) == 1 && l2 - l3 <= MessageListFragment.COLLAPSE_POSTS_THRESHOLD)
                                flag10 = true;
                            else
                                flag10 = false;
                            mCursor.moveToNext();
                            flag7 = flag10;
                        }
                    }
                    if(s1 != null && s1.equals(mFragment.mAccount.getRealTimeChatParticipantId()) && !mFragment.mIsGroup)
                        flag8 = true;
                    else
                        flag8 = false;
                    ((MessageListItemViewImage) (obj)).setMessageStatus(mCursor.getInt(6), flag8);
                    ((MessageListItemViewImage) (obj)).setTimeSince(charsequence1);
                    ((MessageListItemViewImage) (obj)).setGaiaId(EsPeopleData.extractGaiaId(s1));
                    if(flag7)
                        ((MessageListItemViewImage) (obj)).hideAuthor();
                    else
                        ((MessageListItemViewImage) (obj)).showAuthor();
                    ((MessageListItemViewImage) (obj)).updateContentDescription();
                } else
                {
                    if(view != null && (view instanceof MessageListItemView))
                    {
                        obj = (MessageListItemView)view;
                    } else
                    {
                        obj = (MessageListItemView)layoutinflater.inflate(R.layout.message_list_item_view, null);
                        ((MessageListItemView) (obj)).setMessageClickListener(mFragment);
                    }
                    context2 = mContext;
                    if(!mViews.contains(obj))
                        mViews.add(obj);
                    ((MessageListItemView) (obj)).clear();
                    s = mCursor.getString(3);
                    l = mCursor.getLong(5) / 1000L;
                    charsequence = Dates.getShortRelativeTimeSpanString(context2, l);
                    ((MessageListItemView) (obj)).setMessageRowId(mCursor.getLong(0));
                    ((MessageListItemView) (obj)).setAuthorName(mCursor.getString(9));
                    ((MessageListItemView) (obj)).setMessage(mCursor.getString(4));
                    flag = mCursor.isFirst();
                    flag1 = false;
                    if(!flag)
                    {
                        flag4 = mCursor.moveToPrevious();
                        flag1 = false;
                        if(flag4)
                        {
                            l1 = mCursor.getLong(5) / 1000L;
                            flag5 = s.equals(mCursor.getString(3));
                            flag1 = false;
                            if(flag5)
                            {
                                k = mCursor.getInt(7);
                                flag1 = false;
                                if(k == 1)
                                {
                                    i1 = l - l1 - MessageListFragment.COLLAPSE_POSTS_THRESHOLD;
                                    flag1 = false;
                                    if(i1 <= 0)
                                        flag1 = true;
                                }
                            }
                            mCursor.moveToNext();
                        }
                    }
                    flag2 = flag1;
                    if(s != null && s.equals(mFragment.mAccount.getRealTimeChatParticipantId()) && !mFragment.mIsGroup)
                        flag3 = true;
                    else
                        flag3 = false;
                    ((MessageListItemView) (obj)).setMessageStatus(mCursor.getInt(6), flag3);
                    ((MessageListItemView) (obj)).setTimeSince(charsequence);
                    ((MessageListItemView) (obj)).setGaiaId(EsPeopleData.extractGaiaId(s));
                    if(flag2)
                        ((MessageListItemView) (obj)).hideAuthor();
                    else
                        ((MessageListItemView) (obj)).showAuthor();
                    ((MessageListItemView) (obj)).updateContentDescription();
                }
            } else
            if(j == 6)
            {
                if(view != null && (view instanceof HangoutTileEventMessageListItemView))
                    obj = (HangoutTileEventMessageListItemView)view;
                else
                    obj = (HangoutTileEventMessageListItemView)layoutinflater.inflate(R.layout.hangout_tile_event_message_list_item_view, null);
                context1 = mContext;
                ((HangoutTileEventMessageListItemView) (obj)).setType(mCursor.getInt(7));
                ((HangoutTileEventMessageListItemView) (obj)).setText(mCursor.getString(4));
                ((HangoutTileEventMessageListItemView) (obj)).setTimeSince(Dates.getShortRelativeTimeSpanString(context1, mCursor.getLong(5) / 1000L));
                ((HangoutTileEventMessageListItemView) (obj)).updateContentDescription();
            } else
            {
                if(view != null && (view instanceof SystemMessageListItemView))
                    obj = (SystemMessageListItemView)view;
                else
                    obj = (SystemMessageListItemView)layoutinflater.inflate(R.layout.system_message_list_item_view, null);
                context = mContext;
                ((SystemMessageListItemView) (obj)).setType(mCursor.getInt(7));
                ((SystemMessageListItemView) (obj)).setText(mCursor.getString(4));
                ((SystemMessageListItemView) (obj)).setTimeSince(Dates.getShortRelativeTimeSpanString(context, mCursor.getLong(5) / 1000L));
                ((SystemMessageListItemView) (obj)).updateContentDescription();
            }
            return ((View) (obj));
        }

        public final void onMeasured(View view)
        {
            if(view instanceof MessageListItemViewImage)
            {
                MessageListItemViewImage messagelistitemviewimage = (MessageListItemViewImage)view;
                messagelistitemviewimage.setOnMeasureListener(null);
                int i = messagelistitemviewimage.getImageWidth().intValue();
                int j = messagelistitemviewimage.getImageHeight().intValue();
                if(EsLog.isLoggable("MessageListFragment", 4))
                    Log.i("MessageListFragment", (new StringBuilder("onMeasured image width ")).append(i).append(" height ").append(j).toString());
                String s = messagelistitemviewimage.getFullResUrl();
                Object obj;
                if(s.startsWith("content://"))
                    obj = new LocalImageRequest(new MediaRef(null, 0L, null, Uri.parse(s), MediaRef.MediaType.IMAGE), i, j);
                else
                    obj = new MediaImageRequest(ImageUtils.getCenterCroppedAndResizedUrl(i, j, s), 3, i, j, false);
                messagelistitemviewimage.setImage(s, ((ImageRequest) (obj)));
            }
        }

        public final void onResume()
        {
            mViews.clear();
        }

    }

    public static interface MessagesQuery
    {

        public static final String PROJECTION[] = {
            "_id", "message_id", "conversation_id", "author_id", "text", "timestamp", "status", "type", "author_full_name", "author_first_name", 
            "author_type", "image_url"
        };

    }

    private final class RTCServiceListener extends RealTimeChatServiceListener
    {

        public final void onResponseReceived(int i, RealTimeChatServiceResult realtimechatserviceresult)
        {
            if(mRequestId == null || mRequestId.intValue() != i) {
            	return;
            }
            if(realtimechatserviceresult.getErrorCode() != 1) {
            	 messageLoadFailed();
                 if(EsLog.isLoggable("MessageListFragment", 4))
                     Log.i("MessageListFragment", (new StringBuilder("message load failed ")).append(realtimechatserviceresult.getErrorCode()).toString());
            } else {
            	messageLoadSucceeded();
            }
        }

        public final void onResponseTimeout(int i)
        {
            if(mRequestId != null && mRequestId.intValue() == i)
            {
                messageLoadFailed();
                if(EsLog.isLoggable("MessageListFragment", 4))
                    Log.i("MessageListFragment", "message load timeout");
            }
        }

        public final void onUserTypingStatusChanged(long l, String s, String s1, boolean flag)
        {
            if(mConversationRowId == null || mConversationRowId.longValue() != l) {
            	return;
            }
            
            Data.Participant participant;
            HashMap hashmap = mParticipantList;
            participant = null;
            if(hashmap != null)
                participant = (Data.Participant)mParticipantList.get(s1);
            if(participant == null) {
            	if(EsLog.isLoggable("MessageListFragment", 6))
                    Log.e("MessageListFragment", (new StringBuilder("Typing status for non existing participant ")).append(s1).append(" conversation ").append(s).toString());
            } else {
            	if(flag)
                    mTypingParticipants.put(s1, new UserTypingInfo(participant.getFullName()));
                else
                    mTypingParticipants.remove(s1);
                if(EsLog.isLoggable("MessageListFragment", 3))
                    Log.d("MessageListFragment", (new StringBuilder("Typing status for ")).append(participant.getFullName()).append(" changed to ").append(flag).toString());
                mHandler.removeCallbacks(mCheckExpiredTypingRunnable);
                mHandler.post(mCheckExpiredTypingRunnable);
                mHandler.postDelayed(mCheckExpiredTypingRunnable, 31000L);
            }
            
        }

    }
}
