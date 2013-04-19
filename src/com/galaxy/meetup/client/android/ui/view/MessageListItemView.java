/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class MessageListItemView extends RelativeLayout implements
		OnClickListener {

	private static int sFailedAuthorColor;
    private static int sFailedMessageColor;
    private static boolean sInitialized;
    private static int sNormalAuthorColor;
    private static int sNormalMessageColor;
    private ImageView mAuthorArrow;
    private TextView mAuthorNameTextView;
    private AvatarView mAvatarView;
    private TextView mCancelButton;
    private String mGaiaId;
    private MessageClickListener mMessageClickListener;
    private long mMessageRowId;
    private int mMessageStatus;
    private TextView mMessageTextView;
    private int mPosition;
    private TextView mRetryButton;
    private boolean mShowAuthor;
    private boolean mShowStatus;
    private ImageView mStatusImage;
    private TextView mTimeSinceTextView;
    
    public MessageListItemView(Context context)
    {
        super(context);
        if(!sInitialized)
        {
            Resources resources = getContext().getApplicationContext().getResources();
            sNormalAuthorColor = resources.getColor(R.color.realtimechat_message_author);
            sNormalMessageColor = resources.getColor(R.color.realtimechat_message_text);
            sFailedAuthorColor = resources.getColor(R.color.realtimechat_message_author_failed);
            sFailedMessageColor = resources.getColor(R.color.realtimechat_message_text_failed);
            sInitialized = true;
        }
    }

    public MessageListItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        if(!sInitialized)
        {
            Resources resources = getContext().getApplicationContext().getResources();
            sNormalAuthorColor = resources.getColor(R.color.realtimechat_message_author);
            sNormalMessageColor = resources.getColor(R.color.realtimechat_message_text);
            sFailedAuthorColor = resources.getColor(R.color.realtimechat_message_author_failed);
            sFailedMessageColor = resources.getColor(R.color.realtimechat_message_text_failed);
            sInitialized = true;
        }
    }

    private void updateStatusImages() {
    	
    	if(mShowStatus) {
    		switch(mMessageStatus) {
    		case 0:
    		case 1:
    		case 7:
    			mStatusImage.setImageResource(R.drawable.ic_huddle_sending);
    	        mStatusImage.setVisibility(0);
    			break;
    		case 2:
    		case 8:
    			mStatusImage.setVisibility(8);
    			break;
    		case 3:
    		case 4:
    			mStatusImage.setImageResource(R.drawable.ic_huddle_sent);
    	        mStatusImage.setVisibility(0);
    			break;
    		case 5:
    			mStatusImage.setImageResource(R.drawable.ic_huddle_read);
    	        mStatusImage.setVisibility(0);
    			break;
    		case 6:
    		default:
    			mStatusImage.setVisibility(8);
    	        mRetryButton.setVisibility(8);
    	        mCancelButton.setVisibility(8);
    	        mTimeSinceTextView.setVisibility(8);
    			break;
    		}
    	} else {
    		mStatusImage.setVisibility(8);
    	}
    	
    }

    public final void clear()
    {
        mAuthorNameTextView.setText(null);
        mMessageTextView.setText(null);
        mTimeSinceTextView.setText(null);
        mGaiaId = null;
        mStatusImage.setVisibility(8);
        mRetryButton.setVisibility(8);
    }

    public final CharSequence getMessage()
    {
        return mMessageTextView.getText();
    }

    public final void hideAuthor()
    {
        mShowAuthor = false;
        mAvatarView.setVisibility(8);
        mAuthorNameTextView.setVisibility(8);
        mAuthorArrow.setVisibility(8);
        updateStatusImages();
    }

    public void onClick(View view)
    {
        if(view != mAvatarView || mMessageClickListener == null) { 
        	if(view == mRetryButton && mMessageClickListener != null)
                mMessageClickListener.onRetryButtonClicked(mMessageRowId);
            else
            if(view == mCancelButton && mMessageClickListener != null)
                mMessageClickListener.onCancelButtonClicked(mMessageRowId); 
        } else { 
        	mMessageClickListener.onUserImageClicked(mGaiaId);
        }
    }

    public void onFinishInflate()
    {
        mAvatarView = (AvatarView)findViewById(R.id.avatar_image);
        mAuthorArrow = (ImageView)findViewById(R.id.authorArrow);
        mAvatarView.setOnClickListener(this);
        mAuthorNameTextView = (TextView)findViewById(R.id.authorName);
        mMessageTextView = (TextView)findViewById(R.id.messageText);
        mTimeSinceTextView = (TextView)findViewById(R.id.timeSince);
        mStatusImage = (ImageView)findViewById(R.id.message_status);
        mRetryButton = (TextView)findViewById(R.id.retry_send);
        mRetryButton.setOnClickListener(this);
        mCancelButton = (TextView)findViewById(R.id.cancel_send);
        mCancelButton.setOnClickListener(this);
        mShowStatus = true;
        mShowAuthor = true;
    }

    public void setAuthorName(CharSequence charsequence)
    {
        mAuthorNameTextView.setText(charsequence);
    }

    public void setGaiaId(String s)
    {
        mGaiaId = s;
        mAvatarView.setGaiaId(s);
    }

    public void setMessage(CharSequence charsequence)
    {
        mMessageTextView.setText(charsequence);
        mMessageTextView.setVisibility(0);
        setBackgroundResource(R.color.clear);
    }

    public void setMessageClickListener(MessageClickListener messageclicklistener)
    {
        mMessageClickListener = messageclicklistener;
    }

    public void setMessageRowId(long l)
    {
        mMessageRowId = l;
    }

    public void setMessageStatus(int i, boolean flag)
    {
        mMessageStatus = i;
        mShowStatus = flag;
        updateStatusImages();
    }

    public void setPosition(int i)
    {
        mPosition = i;
    }

    public void setTimeSince(CharSequence charsequence)
    {
        mTimeSinceTextView.setText(charsequence);
    }

    public final void showAuthor()
    {
        mShowAuthor = true;
        mAvatarView.setVisibility(0);
        mAuthorNameTextView.setVisibility(0);
        mAuthorArrow.setVisibility(0);
        updateStatusImages();
    }

    public final void updateContentDescription()
    {
        StringBuilder stringbuilder = new StringBuilder();
        Resources resources = getResources();
        if(mShowAuthor)
        {
            CharSequence charsequence2 = mAuthorNameTextView.getText();
            if(charsequence2 != null && charsequence2.length() > 0)
            {
                stringbuilder.append(resources.getString(R.string.realtimechat_message_description_author, new Object[] {
                    charsequence2
                }));
                stringbuilder.append(" ");
            }
        }
        CharSequence charsequence = mTimeSinceTextView.getText();
        if(charsequence != null && charsequence.length() > 0)
        {
            stringbuilder.append(resources.getString(R.string.realtimechat_message_description_time_since, new Object[] {
                charsequence
            }));
            stringbuilder.append(" ");
        }
        CharSequence charsequence1 = mMessageTextView.getText();
        if(charsequence1 != null && charsequence1.length() > 0)
            stringbuilder.append(resources.getString(R.string.realtimechat_message_description_message, new Object[] {
                charsequence1
            }));
        setContentDescription(stringbuilder.toString());
    }
}
