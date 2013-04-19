/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsConversationsData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.util.QuickActions;

/**
 * 
 * @author sihai
 *
 */
public class ParticipantsGalleryView extends FrameLayout {

	static final boolean $assertionsDisabled;
    protected Dialog avatarContextMenuDialog;
    private EsAccount mAccount;
    private CommandListener mCommandListener;
    private TextView mEmptyMessageView;
    private View mParticipantListButton;
    private ViewGroup mParticipantTrayAvatars;

    static 
    {
        boolean flag;
        if(!ParticipantsGalleryView.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
    public ParticipantsGalleryView(Context context)
    {
        this(context, null);
    }

    public ParticipantsGalleryView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        LayoutInflater.from(context).inflate(R.layout.participants_gallery_view, this, true);
        mEmptyMessageView = (TextView)findViewById(R.id.empty_message);
        mParticipantTrayAvatars = (ViewGroup)findViewById(R.id.participant_tray_avatars);
        mParticipantListButton = findViewById(R.id.show_participant_list_button);
        if(attributeset != null)
        {
            TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.ParticipantsGalleryFragment);
            setBackgroundColor(typedarray.getColor(0, 0));
            String s = typedarray.getString(1);
            mEmptyMessageView.setText(s);
        }
        mParticipantListButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                if(mCommandListener != null)
                    mCommandListener.onShowParticipantList();
            }

        });
    }

    public final OverlayedAvatarView addParticipant(LayoutInflater layoutinflater, Data.Participant participant)
    {
        if(mAccount == null)
            throw new IllegalStateException("#setAccount needs to be called first");
        mEmptyMessageView.setVisibility(8);
        mParticipantTrayAvatars.setVisibility(0);
        OverlayedAvatarView overlayedavatarview = OverlayedAvatarView.create(layoutinflater, mParticipantTrayAvatars);
        overlayedavatarview.setTag(participant);
        overlayedavatarview.setParticipantType(EsConversationsData.convertParticipantType(participant));
        String s;
        if(participant.hasParticipantId())
            s = EsPeopleData.extractGaiaId(participant.getParticipantId());
        else
            s = null;
        overlayedavatarview.setParticipantGaiaId(s);
        mParticipantTrayAvatars.addView(overlayedavatarview);
        overlayedavatarview.setOnTouchListener(new TouchListener(overlayedavatarview));
        overlayedavatarview.setContentDescription(participant.getFullName());
        return overlayedavatarview;
    }

    public final void addParticipants(List arraylist)
    {
        LayoutInflater layoutinflater = LayoutInflater.from(getContext());
        for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); addParticipant(layoutinflater, (Data.Participant)iterator.next()));
    }

    public final void dismissAvatarMenuDialog()
    {
        if(avatarContextMenuDialog != null)
        {
            avatarContextMenuDialog.dismiss();
            avatarContextMenuDialog = null;
        }
    }

    public final void removeAllParticipants()
    {
        mEmptyMessageView.setVisibility(0);
        mParticipantTrayAvatars.removeAllViews();
        mParticipantTrayAvatars.setVisibility(8);
    }

    public void setAccount(EsAccount esaccount)
    {
        if(!$assertionsDisabled && mAccount != null && !mAccount.equals(esaccount))
        {
            throw new AssertionError();
        } else
        {
            mAccount = esaccount;
            return;
        }
    }

    public void setBackgroundColor(int i)
    {
        findViewById(R.id.root_view).setBackgroundColor(i);
    }

    public void setCommandListener(CommandListener commandlistener)
    {
        mCommandListener = commandlistener;
    }

    public void setEmptyMessage(String s)
    {
        mEmptyMessageView.setText(s);
    }

    public void setParticipantActive(OverlayedAvatarView overlayedavatarview, boolean flag)
    {
        int i;
        if(flag)
            i = R.color.participants_gallery_active_border;
        else
            i = 0;
        overlayedavatarview.setBorderResource(i);
    }

    public void setParticipantActive(String s, boolean flag) {
    	
        int count = mParticipantTrayAvatars.getChildCount();
        for(int i = 0; i < count; i++) {
        	OverlayedAvatarView overlayedavatarview = (OverlayedAvatarView)mParticipantTrayAvatars.getChildAt(i);
            if(null != overlayedavatarview) {
            	Data.Participant participant = (Data.Participant)overlayedavatarview.getTag();
                if(null != participant && participant.getParticipantId().equals(s)) {
                	setParticipantActive(overlayedavatarview, flag);
                }
            }
        }
    }

    public void setParticipantListButtonVisibility(boolean flag)
    {
        View view = mParticipantListButton;
        int i;
        if(flag)
            i = 0;
        else
            i = 8;
        view.setVisibility(i);
    }

    public void setParticipantLoudestSpeaker(OverlayedAvatarView overlayedavatarview, boolean flag)
    {
        int i;
        if(flag)
            i = R.color.participants_gallery_loudest_speaker_border;
        else
            i = 0;
        overlayedavatarview.setBorderResource(i);
    }

    public void setParticipants(HashMap hashmap, HashSet hashset, HashSet hashset1)
    {
        removeAllParticipants();
        HashSet hashset2 = new HashSet(hashmap.keySet());
        LayoutInflater layoutinflater = LayoutInflater.from(getContext());
        dismissAvatarMenuDialog();
        Iterator iterator = hashset.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s1 = (String)iterator.next();
            if(hashset2.remove(s1))
                setParticipantActive(addParticipant(layoutinflater, (Data.Participant)hashmap.get(s1)), true);
        } while(true);
        Iterator iterator1 = hashset1.iterator();
        do
        {
            if(!iterator1.hasNext())
                break;
            String s = (String)iterator1.next();
            if(hashset2.remove(s))
                setParticipantActive(addParticipant(layoutinflater, (Data.Participant)hashmap.get(s)), false);
        } while(true);
        for(Iterator iterator2 = hashset2.iterator(); iterator2.hasNext(); setParticipantActive(addParticipant(layoutinflater, (Data.Participant)hashmap.get((String)iterator2.next())), false));
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private static final class AvatarContextMenuHelper implements android.view.ContextMenu.ContextMenuInfo, android.view.MenuItem.OnMenuItemClickListener, android.view.View.OnCreateContextMenuListener {

    	private final EsAccount mAccount;
        private final Context mContext;
        private final Data.Participant mParticipant;

        AvatarContextMenuHelper(Context context, EsAccount esaccount, Data.Participant participant)
        {
            mContext = context;
            mAccount = esaccount;
            mParticipant = participant;
        }
        
	    public final void onCreateContextMenu(ContextMenu contextmenu, View view, android.view.ContextMenu.ContextMenuInfo contextmenuinfo)
	    {
	        (new MenuInflater(mContext)).inflate(R.menu.conversation_avatar_menu, contextmenu);
	        MenuItem menuitem = contextmenu.findItem(R.id.menu_avatar_profile);
	        menuitem.setTitle(mParticipant.getFullName());
	        menuitem.setOnMenuItemClickListener(this);
	    }
	
	    public final boolean onMenuItemClick(MenuItem menuitem)
	    {
	        android.content.Intent intent = Intents.getProfileActivityIntent(mContext, mAccount, mParticipant.getParticipantId(), null);
	        mContext.startActivity(intent);
	        return true;
	    }

    }

	public static interface CommandListener
	{
	
	    public abstract void onAvatarClicked(OverlayedAvatarView overlayedavatarview, Data.Participant participant);
	
	    public abstract void onAvatarDoubleClicked(OverlayedAvatarView overlayedavatarview, Data.Participant participant);
	
	    public abstract void onShowParticipantList();
	}

	public static class SimpleCommandListener implements CommandListener {
		
		private final EsAccount mAccount;
	    private final ParticipantsGalleryView mView;
	
	    public SimpleCommandListener(ParticipantsGalleryView participantsgalleryview, EsAccount esaccount)
	    {
	        if(participantsgalleryview == null)
	            throw new IllegalArgumentException("view is null");
	        if(esaccount == null)
	        {
	            throw new IllegalArgumentException("account is null");
	        } else
	        {
	            mView = participantsgalleryview;
	            mAccount = esaccount;
	            return;
	        }
	    }
	    
	    public final void onAvatarClicked(OverlayedAvatarView overlayedavatarview, Data.Participant participant)
	    {
	        AvatarContextMenuHelper avatarcontextmenuhelper = new AvatarContextMenuHelper(mView.getContext(), mAccount, participant);
	        mView.avatarContextMenuDialog = QuickActions.show(overlayedavatarview, overlayedavatarview, avatarcontextmenuhelper, avatarcontextmenuhelper, avatarcontextmenuhelper, true, false);
	    }
	
	    public final void onAvatarDoubleClicked(OverlayedAvatarView overlayedavatarview, Data.Participant participant)
	    {
	        onAvatarClicked(overlayedavatarview, participant);
	    }
	
	    public void onShowParticipantList()
	    {
	        throw new IllegalStateException("onShowParticipantList is not supported");
	    }
	}

	private final class TouchListener extends android.view.GestureDetector.SimpleOnGestureListener implements android.view.View.OnTouchListener {
		
		private final OverlayedAvatarView avatarView;
	    private final GestureDetector gestureDetector;

	    TouchListener(OverlayedAvatarView overlayedavatarview)
	    {
	        super();
	        gestureDetector = new GestureDetector(getContext(), this);
	        avatarView = overlayedavatarview;
	        gestureDetector.setOnDoubleTapListener(this);
	    }

	    public final boolean onDoubleTap(MotionEvent motionevent)
	    {
	        if(mCommandListener != null)
	        {
	            Data.Participant participant = (Data.Participant)avatarView.getTag();
	            mCommandListener.onAvatarDoubleClicked(avatarView, participant);
	        }
	        return true;
	    }
	
	    public final boolean onDown(MotionEvent motionevent)
	    {
	        return true;
	    }
	
	    public final boolean onSingleTapConfirmed(MotionEvent motionevent)
	    {
	        if(getParent() != null && getVisibility() == 0 && avatarView.getParent() != null && mCommandListener != null)
	        {
	            Data.Participant participant = (Data.Participant)avatarView.getTag();
	            mCommandListener.onAvatarClicked(avatarView, participant);
	        }
	        return true;
	    }
	
	    public final boolean onTouch(View view, MotionEvent motionevent)
	    {
	        return gestureDetector.onTouchEvent(motionevent);
	    }
	}
    
}
