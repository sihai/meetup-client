/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.Iterator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.ui.view.EsImageView.OnImageLoadedListener;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.domain.Theme;
import com.galaxy.meetup.server.client.domain.ThemeImage;

/**
 * 
 * @author sihai
 *
 */
public class EventDetailsHeaderView extends ViewGroup implements
		android.media.MediaPlayer.OnErrorListener,
		android.media.MediaPlayer.OnPreparedListener, OnClickListener,
		OnImageLoadedListener, Recyclable {

	private static int sAvatarOverlap;
    private static int sAvatarSize;
    private static String sCollapseText;
    private static String sExpandText;
    private static boolean sInitialized = false;
    private static int sOnAirColor;
    private static Drawable sOnAirDrawable;
    private static String sOnAirText;
    private static int sPadding;
    private static int sPrivatePublicColor;
    private static String sPrivateText;
    private static String sPublicText;
    private static int sSecondaryPadding;
    private static float sTypeSize;
    private EventActionListener mActionListener;
    private AvatarView mAvatar;
    private int mChevronResId;
    private ImageView mExpandCollapseChevronView;
    private TextView mExpandCollapseTextView;
    private View mExpandCollapseView;
    private boolean mOnAirWrap;
    private android.view.View.OnClickListener mOnClickListener;
    private EventThemeView mThemeImageView;
    private TextView mTitleView;
    private TextView mTypeView;
    private String mVideoThemeUrl;
    private VideoView mVideoView;
    
    public EventDetailsHeaderView(Context context)
    {
        this(context, null);
    }

    public EventDetailsHeaderView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public EventDetailsHeaderView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        if(!sInitialized)
        {
            sInitialized = true;
            Resources resources1 = getResources();
            int j = resources1.getDimensionPixelSize(R.dimen.event_card_details_avatar_size);
            sAvatarSize = j;
            sAvatarOverlap = (int)((float)j * resources1.getDimension(R.dimen.event_card_details_avatar_percent_overlap));
            sPadding = resources1.getDimensionPixelSize(R.dimen.event_card_padding);
            sSecondaryPadding = resources1.getDimensionPixelSize(R.dimen.event_card_details_secondary_padding);
            sOnAirText = resources1.getString(R.string.event_detail_on_air);
            sOnAirColor = resources1.getColor(R.color.event_detail_on_air);
            sPrivateText = resources1.getString(R.string.event_detail_private);
            sPublicText = resources1.getString(R.string.event_detail_public);
            sPrivatePublicColor = resources1.getColor(R.color.event_detail_private);
            sTypeSize = resources1.getDimension(R.dimen.event_card_details_on_air_size);
            sOnAirDrawable = resources1.getDrawable(R.drawable.btn_events_on_air);
            sExpandText = resources1.getString(R.string.profile_show_more);
            sCollapseText = resources1.getString(R.string.profile_show_less);
        }
        Resources resources = getResources();
        if(android.os.Build.VERSION.SDK_INT >= 14)
        {
            mVideoView = new VideoView(context);
            mVideoView.setOnErrorListener(this);
            addView(mVideoView);
        }
        mThemeImageView = new EventThemeView(context);
        mThemeImageView.setFadeIn(true);
        addView(mThemeImageView);
        mAvatar = new AvatarView(context);
        mAvatar.setRounded(true);
        addView(mAvatar);
        mTitleView = new TextView(context);
        mTitleView.setTextColor(resources.getColor(R.color.event_card_details_title_color));
        mTitleView.setTextSize(0, resources.getDimension(R.dimen.event_card_details_title_size));
        mTitleView.setTypeface(Typeface.DEFAULT_BOLD);
        addView(mTitleView);
        mExpandCollapseChevronView = new ImageView(context);
        mExpandCollapseChevronView.setImageResource(R.drawable.icn_events_arrow_down);
        mChevronResId = R.drawable.icn_events_arrow_down;
        addView(mExpandCollapseChevronView);
        mExpandCollapseView = new View(context);
        addView(mExpandCollapseView);
        mTypeView = new TextView(context);
        mTypeView.setTextSize(0, resources.getDimension(R.dimen.event_card_details_subtitle_size));
        mTypeView.setSingleLine();
        mTypeView.setGravity(17);
        addView(mTypeView);
        mExpandCollapseTextView = new TextView(context);
        mExpandCollapseTextView.setTextSize(0, resources.getDimension(R.dimen.event_card_details_title_size));
        mExpandCollapseTextView.setTextColor(resources.getColor(R.color.event_card_details_collapse_expand_color));
        mExpandCollapseTextView.setText(sExpandText);
        mExpandCollapseTextView.setSingleLine();
        mExpandCollapseTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        addView(mExpandCollapseTextView);
    }

    public final void bind(PlusEvent plusevent, android.view.View.OnClickListener onclicklistener, boolean flag, EventActionListener eventactionlistener)
    {
        setEventTheme(plusevent.theme);
        mAvatar.setGaiaId(plusevent.creatorObfuscatedId);
        mTitleView.setText(plusevent.name);
        removeView(mExpandCollapseTextView);
        removeView(mExpandCollapseChevronView);
        removeView(mExpandCollapseView);
        if(onclicklistener != null)
        {
            addView(mExpandCollapseChevronView);
            addView(mExpandCollapseView);
            if(flag)
                addView(mExpandCollapseTextView);
        }
        mOnClickListener = onclicklistener;
        mActionListener = eventactionlistener;
        if(plusevent.eventOptions != null && plusevent.eventOptions.broadcast != null && plusevent.eventOptions.broadcast.booleanValue())
        {
            mTypeView.setText(sOnAirText);
            mTypeView.setTextColor(sOnAirColor);
            mTypeView.setBackgroundDrawable(sOnAirDrawable);
            mTypeView.setVisibility(0);
        } else
        if(plusevent.isPublic != null)
        {
            TextView textview = mTypeView;
            String s;
            if(plusevent.isPublic.booleanValue())
                s = sPublicText;
            else
                s = sPrivateText;
            textview.setText(s);
            mTypeView.setTextColor(sPrivatePublicColor);
            mTypeView.setBackgroundDrawable(null);
            mTypeView.setVisibility(0);
        } else
        {
            mTypeView.setVisibility(8);
        }
        mTypeView.setTextSize(0, sTypeSize);
        if(mVideoView != null)
            mVideoView.setVisibility(4);
        mThemeImageView.setOnImageLoadedListener(this);
        mAvatar.setOnClickListener(this);
        mExpandCollapseView.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        if(!(view instanceof AvatarView) || mActionListener == null) {
        	if(mOnClickListener != null)
                mOnClickListener.onClick(view); 
        } else { 
        	mActionListener.onAvatarClicked(((AvatarView)view).getGaiaId());
        }
    }

    public boolean onError(MediaPlayer mediaplayer, int i, int j)
    {
        return true;
    }

    public final void onImageLoaded()
    {
        if(mVideoView != null && !TextUtils.isEmpty(mVideoThemeUrl))
        {
            android.net.Uri.Builder builder = new android.net.Uri.Builder();
            builder.path(mVideoThemeUrl);
            mVideoView.setVisibility(0);
            mVideoView.setVideoURI(builder.build());
            mVideoView.setOnPreparedListener(this);
        }
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = getMeasuredWidth();
        int j1 = (int)((float)i1 / 3.36F);
        mThemeImageView.layout(0, 0, i1, j1);
        if(mVideoView != null)
            mVideoView.layout(0, 0, i1, j1);
        int k1 = sPadding;
        int l1 = j1 - sAvatarOverlap;
        mAvatar.layout(k1, l1, k1 + sAvatarSize, l1 + sAvatarSize);
        int i2 = sPadding + sAvatarSize + sPadding;
        int j2 = j1 + sPadding;
        mTitleView.layout(i2, j2, i2 + mTitleView.getMeasuredWidth(), j2 + mTitleView.getMeasuredHeight());
        if(mTypeView.getVisibility() == 0)
        {
            int k3 = j2 + mTitleView.getMeasuredHeight();
            mTypeView.layout(i2, k3, i2 + mTypeView.getMeasuredWidth(), k3 + mTypeView.getMeasuredHeight());
        }
        if(mOnClickListener != null)
        {
            int k2 = mExpandCollapseChevronView.getMeasuredHeight();
            int l2 = (j2 + mTitleView.getBaseline()) - k2;
            int i3 = i1 - mExpandCollapseChevronView.getMeasuredWidth() - sSecondaryPadding;
            mExpandCollapseChevronView.layout(i3, l2, i3 + mExpandCollapseChevronView.getMeasuredWidth(), l2 + k2);
            int j3 = i3 - mExpandCollapseTextView.getMeasuredWidth() - sPadding;
            mExpandCollapseTextView.layout(j3, j2, j3 + mExpandCollapseTextView.getMeasuredWidth(), j2 + mExpandCollapseTextView.getMeasuredHeight());
            mExpandCollapseView.layout(sAvatarSize, j1, sAvatarSize + mExpandCollapseView.getMeasuredWidth(), j1 + mExpandCollapseView.getMeasuredHeight());
        }
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = android.view.View.MeasureSpec.getSize(i);
        if(k == 0)
            k = android.view.View.MeasureSpec.getSize(j);
        int l = (int)((float)k / 3.36F);
        mThemeImageView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000));
        if(mVideoView != null)
            mVideoView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000));
        mAvatar.measure(android.view.View.MeasureSpec.makeMeasureSpec(sAvatarSize, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(sAvatarSize, 0x40000000));
        int i1 = Math.max(0, k - mAvatar.getMeasuredWidth() - 2 * sPadding - sSecondaryPadding);
        if(mOnClickListener != null)
        {
            mExpandCollapseChevronView.measure(android.view.View.MeasureSpec.makeMeasureSpec(0, 0), android.view.View.MeasureSpec.makeMeasureSpec(0, 0));
            i1 = Math.max(0, i1 - (mExpandCollapseChevronView.getMeasuredWidth() + sPadding));
        }
        mTitleView.measure(android.view.View.MeasureSpec.makeMeasureSpec(i1, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(0, 0));
        int j1 = Math.max(0, i1 - sPadding);
        mOnAirWrap = false;
        int k1 = mTypeView.getVisibility();
        int l1 = 0;
        int i2 = 0;
        int j2;
        int k2;
        int l2;
        int i3;
        boolean flag;
        if(k1 != 8)
        {
            mTypeView.measure(android.view.View.MeasureSpec.makeMeasureSpec(j1, 0), android.view.View.MeasureSpec.makeMeasureSpec(0, 0));
            if(mTypeView.getMeasuredWidth() > j1)
                flag = true;
            else
                flag = false;
            mOnAirWrap = flag;
            if(mOnAirWrap)
                mTypeView.measure(android.view.View.MeasureSpec.makeMeasureSpec(i1, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(0, 0));
            i2 = mTypeView.getMeasuredWidth() + sPadding;
            l1 = mTypeView.getMeasuredHeight();
        }
        j2 = mTitleView.getMeasuredWidth();
        if(mOnAirWrap)
            i2 = 0;
        k2 = Math.max(0, i1 - Math.max(j2, i2));
        mExpandCollapseTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k2, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(0, 0));
        l2 = l1 + mTitleView.getMeasuredHeight() + sPadding;
        i3 = l + Math.max(sAvatarSize - sAvatarOverlap, l2);
        mExpandCollapseView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k - sAvatarSize, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(i3 - l, 0x40000000));
        setMeasuredDimension(k, i3);
    }

    public void onPrepared(MediaPlayer mediaplayer)
    {
        mThemeImageView.startFadeOut(750);
        mediaplayer.setLooping(true);
        mediaplayer.start();
    }

    public void onRecycle()
    {
        mTitleView.setText(null);
        mVideoThemeUrl = null;
        mThemeImageView.onRecycle();
        if(mVideoView != null)
            mThemeImageView.setAlpha(1.0F);
        mExpandCollapseView.setOnClickListener(null);
        mAvatar.setOnClickListener(null);
        mThemeImageView.setOnImageLoadedListener(null);
        if(mVideoView != null)
        {
            mVideoView.setOnPreparedListener(null);
            if(mVideoView.isPlaying())
                mVideoView.stopPlayback();
        }
        mOnClickListener = null;
    }

    public final void pausePlayback()
    {
        if(mVideoView != null && mVideoView.isPlaying())
            if(mVideoView.canPause())
                mVideoView.pause();
            else
                mVideoView.stopPlayback();
        if(mThemeImageView != null && mVideoView != null)
            mThemeImageView.setAlpha(1.0F);
    }
	
    public void setEventTheme(Theme theme)
    {
    	if(mVideoView == null || theme == null || theme.image == null) {
    		;
    	} else {
    		ThemeImage themeimage;
    		for(Iterator iterator = theme.image.iterator(); iterator.hasNext();) {
    			themeimage = (ThemeImage)iterator.next();
    			if(!"MOV".equals(themeimage.format) || !"LARGE".equals(themeimage.aspectRatio) || !themeimage.url.endsWith("mp4")) {
    				continue;
    			} else {
    				mVideoThemeUrl = themeimage.url;
    			}
    		}
    	}
    	
    	mThemeImageView.setEventTheme(theme);
    }

    public void setExpandState(boolean flag)
    {
        int i;
        TextView textview;
        String s;
        if(flag)
            i = R.drawable.icn_events_arrow_up;
        else
            i = R.drawable.icn_events_arrow_down;
        mChevronResId = i;
        textview = mExpandCollapseTextView;
        if(flag)
            s = sCollapseText;
        else
            s = sExpandText;
        textview.setText(s);
        mExpandCollapseChevronView.setImageResource(mChevronResId);
        if(mActionListener != null)
            mActionListener.onExpansionToggled(flag);
    }

    public void setLayoutType(boolean flag)
    {
        TextView textview = mExpandCollapseTextView;
        int i;
        if(flag)
            i = 0;
        else
            i = 8;
        textview.setVisibility(i);
    }
}
