/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.cache.ImageRequest;

/**
 * 
 * @author sihai
 *
 */
public class MessageListItemViewImage extends RelativeLayout implements
		OnClickListener {

	private static Bitmap sDefaultUserImage;
    private static int sFailedAuthorColor;
    private static int sNormalAuthorColor;
    private ImageView mAuthorArrow;
    private TextView mAuthorNameTextView;
    private AvatarView mAvatarView;
    private TextView mCancelButton;
    private String mFullResUrl;
    private String mGaiaId;
    private View mImageFrame;
    private Integer mImageHeight;
    private Integer mImageWidth;
    private OnMeasuredListener mMeasuredListener;
    private MessageClickListener mMessageClickListener;
    private EsImageView mMessageImageView;
    private long mMessageRowId;
    private int mMessageStatus;
    private int mPosition;
    private TextView mRetryButton;
    private boolean mShowAuthor;
    private boolean mShowStatus;
    private ImageView mStatusImage;
    private TextView mTimeSinceTextView;
    
    public MessageListItemViewImage(Context context)
    {
        this(context, null);
    }

    public MessageListItemViewImage(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mImageWidth = null;
        mImageHeight = null;
        if(sDefaultUserImage == null)
        {
            Resources resources = getContext().getApplicationContext().getResources();
            sDefaultUserImage = EsAvatarData.getMediumDefaultAvatar(context);
            sNormalAuthorColor = resources.getColor(R.color.realtimechat_message_author);
            sFailedAuthorColor = resources.getColor(R.color.realtimechat_message_author_failed);
        }
    }

    private void updateStatusImages() {
    	
    	if(!mShowStatus) {
    		mStatusImage.setVisibility(8);
    		return;
    	}
    	
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
    	
    	if(mMessageStatus == 2 || mMessageStatus == 8)
        {
            mTimeSinceTextView.setVisibility(8);
            mRetryButton.setVisibility(0);
            mCancelButton.setVisibility(0);
            mAuthorNameTextView.setTextColor(sFailedAuthorColor);
            mAuthorNameTextView.setVisibility(0);
        } else
        {
            mAuthorNameTextView.setTextColor(sNormalAuthorColor);
            if(mShowAuthor)
                mTimeSinceTextView.setVisibility(0);
            else
                mTimeSinceTextView.setVisibility(8);
            mRetryButton.setVisibility(8);
            mCancelButton.setVisibility(8);
        }
    }

    public final void clear()
    {
        mAuthorNameTextView.setText(null);
        mTimeSinceTextView.setText(null);
        mMessageImageView.onRecycle();
        mGaiaId = null;
        mStatusImage.setVisibility(8);
        mRetryButton.setVisibility(8);
    }

    public final String getFullResUrl()
    {
        return mFullResUrl;
    }

    public final Integer getImageHeight()
    {
        return mImageHeight;
    }

    public final Integer getImageWidth()
    {
        return mImageWidth;
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
            else
            if(view == mMessageImageView && mMessageClickListener != null)
                mMessageClickListener.onMediaImageClick(mFullResUrl, mAvatarView.getGaiaId());
        } else { 
        	mMessageClickListener.onUserImageClicked(null);
        }
    }

    public void onFinishInflate()
    {
        mAvatarView = (AvatarView)findViewById(R.id.avatar_image);
        mAuthorArrow = (ImageView)findViewById(R.id.authorArrow);
        mAvatarView.setOnClickListener(this);
        mAuthorNameTextView = (TextView)findViewById(R.id.authorName);
        mMessageImageView = (EsImageView)findViewById(R.id.messageImage);
        mMessageImageView.setOnClickListener(this);
        mImageFrame = findViewById(R.id.image_frame);
        mTimeSinceTextView = (TextView)findViewById(R.id.timeSince);
        mStatusImage = (ImageView)findViewById(R.id.message_status);
        mRetryButton = (TextView)findViewById(R.id.retry_send);
        mRetryButton.setOnClickListener(this);
        mCancelButton = (TextView)findViewById(R.id.cancel_send);
        mCancelButton.setOnClickListener(this);
        mShowStatus = true;
        mShowAuthor = true;
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        mImageWidth = Integer.valueOf(mMessageImageView.getMeasuredWidth());
        mImageHeight = Integer.valueOf(mMessageImageView.getMeasuredHeight());
        if(mMeasuredListener != null)
            mMeasuredListener.onMeasured(this);
    }

    public void setAuthorName(CharSequence charsequence)
    {
        mAuthorNameTextView.setText(charsequence);
    }

    public void setGaiaId(String s)
    {
        mAvatarView.setGaiaId(s);
    }

    public void setImage(String s, ImageRequest imagerequest)
    {
        mFullResUrl = s;
        mMessageImageView.setRequest(imagerequest);
        mMessageImageView.setVisibility(0);
        mImageFrame.setVisibility(0);
    }

    public void setMessage(CharSequence charsequence)
    {
        mMessageImageView.setVisibility(8);
        mImageFrame.setVisibility(8);
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

    public void setOnMeasureListener(OnMeasuredListener onmeasuredlistener)
    {
        mMeasuredListener = onmeasuredlistener;
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
            CharSequence charsequence1 = mAuthorNameTextView.getText();
            if(charsequence1 != null && charsequence1.length() > 0)
            {
                stringbuilder.append(resources.getString(R.string.realtimechat_message_description_author, new Object[] {
                    charsequence1
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
        stringbuilder.append(resources.getString(R.string.realtimechat_message_description_image));
        setContentDescription(stringbuilder.toString());
    }
    
    
    public static interface OnMeasuredListener
    {

        public abstract void onMeasured(View view);
    }
}
