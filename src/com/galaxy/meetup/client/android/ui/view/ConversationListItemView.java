/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAvatarData;

/**
 * 
 * @author sihai
 *
 */
public class ConversationListItemView extends RelativeLayout implements
		Checkable {

	private static Drawable sBackgroundChecked;
    private static int sBackgroundUnchecked;
    private static Bitmap sDefaultUserImage;
    private static boolean sInitialized;
    private AvatarView mAvatarFullView;
    private AvatarView mAvatarLeftFullView;
    private AvatarView mAvatarLowerLeftView;
    private AvatarView mAvatarLowerRightView;
    private AvatarView mAvatarUpperLeftView;
    private AvatarView mAvatarUpperRightView;
    private boolean mChecked;
    private TextView mLastMessageTextView;
    private boolean mMuted;
    private ImageView mMutedIcon;
    private TextView mNameTextView;
    private int mPosition;
    private TextView mTimeSinceTextView;
    private int mUnreadCount;
    private TextView mUnreadCountTextView;
    
    public ConversationListItemView(Context context)
    {
        this(context, null);
    }

    public ConversationListItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        if(!sInitialized)
        {
            sInitialized = true;
            Resources resources = context.getApplicationContext().getResources();
            sBackgroundChecked = resources.getDrawable(R.drawable.list_selected_holo);
            sBackgroundUnchecked = resources.getColor(R.color.clear);
        }
        mUnreadCount = 0;
        mMuted = false;
        if(sDefaultUserImage == null)
            sDefaultUserImage = EsAvatarData.getMediumDefaultAvatar(context);
    }

    private void refreshUnreadCountView()
    {
        if(mUnreadCount == 0)
            mUnreadCountTextView.setVisibility(8);
        else
            mUnreadCountTextView.setVisibility(0);
    }

    public final void clear()
    {
        mNameTextView.setText(null);
        mLastMessageTextView.setText(null);
        mTimeSinceTextView.setText(null);
        mUnreadCountTextView.setText(null);
    }

    public boolean isChecked()
    {
        return mChecked;
    }

    public void onFinishInflate()
    {
        mAvatarFullView = (AvatarView)findViewById(R.id.avatarFull);
        mAvatarLeftFullView = (AvatarView)findViewById(R.id.avatarLeftFull);
        mAvatarUpperLeftView = (AvatarView)findViewById(R.id.avatarUpperLeft);
        mAvatarLowerLeftView = (AvatarView)findViewById(R.id.avatarLowerLeft);
        mAvatarUpperRightView = (AvatarView)findViewById(R.id.avatarUpperRight);
        mAvatarLowerRightView = (AvatarView)findViewById(R.id.avatarLowerRight);
        mNameTextView = (TextView)findViewById(R.id.conversationName);
        mLastMessageTextView = (TextView)findViewById(R.id.lastMessage);
        mTimeSinceTextView = (TextView)findViewById(R.id.timeSince);
        mUnreadCountTextView = (TextView)findViewById(R.id.unreadCount);
        mMutedIcon = (ImageView)findViewById(R.id.mutedIcon);
    }

    public void setChecked(boolean flag)
    {
        if(flag != mChecked)
        {
            mChecked = flag;
            if(flag)
                setBackgroundDrawable(sBackgroundChecked);
            else
                setBackgroundColor(sBackgroundUnchecked);
            invalidate();
        }
    }

    public void setConversationName(CharSequence charsequence)
    {
        mNameTextView.setText(charsequence);
    }

    public void setLastMessage(CharSequence charsequence)
    {
        mLastMessageTextView.setText(charsequence);
    }

    public void setMuted(boolean flag)
    {
        if(mMuted != flag)
        {
            mMuted = flag;
            if(mMuted)
                mMutedIcon.setVisibility(0);
            else
                mMutedIcon.setVisibility(8);
            refreshUnreadCountView();
        }
    }

    public void setParticipantsId(List list, String s) {
        if(list != null && list.size() != 0) {
        	if(list.size() == 1)
            {
                mAvatarFullView.setVisibility(0);
                mAvatarFullView.setGaiaId((String)list.get(0));
                mAvatarLeftFullView.setVisibility(8);
                mAvatarUpperLeftView.setVisibility(8);
                mAvatarLowerLeftView.setVisibility(8);
                mAvatarUpperRightView.setVisibility(8);
                mAvatarLowerRightView.setVisibility(8);
            } else
            if(list.size() == 2 || list.size() == 3)
            {
                mAvatarFullView.setVisibility(8);
                mAvatarLeftFullView.setGaiaId((String)list.get(0));
                mAvatarLeftFullView.setVisibility(0);
                mAvatarUpperLeftView.setVisibility(8);
                mAvatarLowerLeftView.setVisibility(8);
                mAvatarUpperRightView.setGaiaId((String)list.get(1));
                mAvatarUpperRightView.setVisibility(0);
                if(list.size() == 2)
                    mAvatarLowerRightView.setGaiaId(s);
                else
                    mAvatarLowerRightView.setGaiaId((String)list.get(2));
                mAvatarLowerRightView.setVisibility(0);
            } else
            if(list.size() >= 4)
            {
                mAvatarFullView.setVisibility(8);
                mAvatarLeftFullView.setVisibility(8);
                mAvatarUpperLeftView.setVisibility(0);
                mAvatarLowerLeftView.setVisibility(0);
                mAvatarUpperRightView.setVisibility(0);
                mAvatarLowerRightView.setVisibility(0);
                mAvatarUpperLeftView.setGaiaId((String)list.get(0));
                mAvatarLowerLeftView.setGaiaId((String)list.get(1));
                mAvatarUpperRightView.setGaiaId((String)list.get(2));
                mAvatarLowerRightView.setGaiaId((String)list.get(3));
            }
        } else { 
        	mAvatarFullView.setVisibility(0);
            mAvatarFullView.setGaiaId(s);
            mAvatarLeftFullView.setVisibility(8);
            mAvatarUpperLeftView.setVisibility(8);
            mAvatarLowerLeftView.setVisibility(8);
            mAvatarUpperRightView.setVisibility(8);
            mAvatarLowerRightView.setVisibility(8);
        }
    }

    public void setPosition(int i)
    {
        mPosition = i;
    }

    public void setTimeSince(CharSequence charsequence)
    {
        mTimeSinceTextView.setText(charsequence);
    }

    public void setUnreadCount(int i)
    {
        mUnreadCount = i;
        mUnreadCountTextView.setText(String.valueOf(i));
        refreshUnreadCountView();
    }

    public void toggle()
    {
        boolean flag;
        if(!mChecked)
            flag = true;
        else
            flag = false;
        setChecked(flag);
    }

    public final void updateContentDescription()
    {
        StringBuilder stringbuilder = new StringBuilder();
        Resources resources = getResources();
        if(mMuted)
        {
            stringbuilder.append(resources.getString(R.string.realtimechat_conversation_description_muted));
            stringbuilder.append(" ");
        }
        if(mUnreadCount > 0)
        {
            int i = R.string.realtimechat_conversation_description_unread_count;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(mUnreadCount);
            stringbuilder.append(resources.getString(i, aobj));
            stringbuilder.append(" ");
        }
        CharSequence charsequence = mNameTextView.getText();
        if(charsequence != null && charsequence.length() > 0)
        {
            stringbuilder.append(resources.getString(R.string.realtimechat_conversation_description_title, new Object[] {
                charsequence
            }));
            stringbuilder.append(" ");
        }
        CharSequence charsequence1 = mTimeSinceTextView.getText();
        if(charsequence1 != null && charsequence1.length() > 0)
        {
            stringbuilder.append(resources.getString(R.string.realtimechat_conversation_description_time_since, new Object[] {
                charsequence1
            }));
            stringbuilder.append(" ");
        }
        CharSequence charsequence2 = mLastMessageTextView.getText();
        if(charsequence2 != null && charsequence2.length() > 0)
        {
            stringbuilder.append(resources.getString(R.string.realtimechat_conversation_description_message, new Object[] {
                charsequence2
            }));
            stringbuilder.append(" ");
        }
        setContentDescription(stringbuilder.toString());
    }
}
