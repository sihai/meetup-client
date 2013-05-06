/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EventThemeImageRequest;
import com.galaxy.meetup.client.android.content.MediaImageRequest;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageCache.OnAvatarChangeListener;
import com.galaxy.meetup.client.android.service.ImageCache.OnMediaImageChangeListener;
import com.galaxy.meetup.client.util.EventDateUtils;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.client.util.TimeZoneHelper;
import com.galaxy.meetup.server.client.v2.domain.Event;
import com.galaxy.meetup.server.client.v2.domain.HangoutInfo;
import com.galaxy.meetup.server.client.v2.domain.Location;
import com.galaxy.meetup.server.client.v2.domain.ThemeImage;

/**
 * 
 * @author sihai
 *
 */
public class EventCardDrawer implements OnAvatarChangeListener,
		OnMediaImageChangeListener {

	private static Bitmap sAuthorBitmap;
    private static int sAvatarSize;
    private static Bitmap sBlueRsvpBannerBitmap;
    private static Paint sDividerPaint;
    private static Bitmap sEventAttendingBitmap;
    private static int sEventCardPadding;
    private static Paint sEventInfoBackgroundPaint;
    private static TextPaint sEventInfoTextPaint;
    private static Bitmap sEventMaybeBitmap;
    private static TextPaint sEventNameTextPaint;
    private static Bitmap sEventNotAttendingBitmap;
    private static int sEventTextLineSpacing;
    private static Bitmap sGreenRsvpBannerBitmap;
    private static Bitmap sGreyRsvpBannerBitmap;
    private static Bitmap sHangoutBitmap;
    private static String sHangoutTitle;
    private static ImageCache sImageCache;
    private static boolean sInitialized;
    private static Bitmap sLocationBitmap;
    private static NinePatchDrawable sOnAirNinePatch;
    private static TextPaint sOnAirPaint;
    private static String sOnAirTitle;
    private static Paint sResizePaint;
    private static float sRibbonHeightPercentOverlap;
    private static TextPaint sStatusGoingPaint;
    private static TextPaint sStatusInvitedPaint;
    private static TextPaint sStatusMaybePaint;
    private static TextPaint sStatusNotGoingPaint;
    EsAccount mAccount;
    private CharSequence mAttribution;
    private Point mAttributionLayoutCorner;
    ClickableUserImage mAvatar;
    private boolean mBound;
    CardView mContainingCardView;
    private StaticLayout mCreatorLayout;
    private StaticLayout mDateLayout;
    private Point mDateLayoutCorner;
    float mDividerLines[];
    Event mEventInfo;
    private boolean mIgnoreHeight;
    private Bitmap mLocationIcon;
    private Rect mLocationIconRect;
    private StaticLayout mLocationLayout;
    private Point mLocationLayoutCorner;
    private StaticLayout mNameLayout;
    private Point mNameLayoutCorner;
    private Bitmap mRsvpBanner;
    private Rect mRsvpBannerRect;
    private Bitmap mRsvpIcon;
    private Rect mRsvpIconRect;
    private StaticLayout mRsvpLayout;
    private Point mRsvpLayoutCorner;
    RemoteImage mThemeImage;
    private Rect mThemeImageRect;
    ThemeImage mThemeInfo;
    private Point mTimeZoneCorner;
    private StaticLayout mTimeZoneLayout;
    private ClickableButton mTypeLabel;
    
    public EventCardDrawer(View view)
    {
        TimeZoneHelper.initialize(view.getContext());
        if(!sInitialized)
        {
            Resources resources = view.getResources();
            sImageCache = ImageCache.getInstance(view.getContext());
            sResizePaint = new Paint(2);
            sAvatarSize = (int)resources.getDimension(R.dimen.card_avatar_size);
            sRibbonHeightPercentOverlap = resources.getDimension(R.dimen.event_card_ribbon_percent_height_overlap);
            sEventCardPadding = (int)resources.getDimension(R.dimen.event_card_padding);
            sEventTextLineSpacing = (int)resources.getDimension(R.dimen.event_card_text_line_spacing);
            sAuthorBitmap = EsAvatarData.getMediumDefaultAvatar(view.getContext(), true);
            Paint paint = new Paint();
            sEventInfoBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.event_info_background_color));
            sEventInfoBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            TextPaint textpaint = new TextPaint();
            sEventNameTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sEventNameTextPaint.setColor(resources.getColor(R.color.event_name_text_color));
            sEventNameTextPaint.setTextSize(resources.getDimension(R.dimen.event_card_name_text_size));
            sEventNameTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            TextPaintUtils.registerTextPaint(sEventNameTextPaint, R.dimen.event_card_name_text_size);
            TextPaint textpaint1 = new TextPaint();
            sEventInfoTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sEventInfoTextPaint.setColor(resources.getColor(R.color.card_event_info_text));
            sEventInfoTextPaint.setTextSize(resources.getDimension(R.dimen.event_card_info_text_size));
            TextPaintUtils.registerTextPaint(sEventInfoTextPaint, R.dimen.event_card_info_text_size);
            TextPaint textpaint2 = new TextPaint();
            textpaint2.setAntiAlias(true);
            textpaint2.setTextSize(resources.getDimension(R.dimen.event_card_status_text_size));
            textpaint2.setTypeface(Typeface.DEFAULT_BOLD);
            TextPaint textpaint3 = new TextPaint(textpaint2);
            sStatusInvitedPaint = textpaint3;
            textpaint3.setColor(resources.getColor(R.color.card_event_invited));
            TextPaintUtils.registerTextPaint(sStatusInvitedPaint, R.dimen.event_card_status_text_size);
            TextPaint textpaint4 = new TextPaint(textpaint2);
            sStatusGoingPaint = textpaint4;
            textpaint4.setColor(resources.getColor(R.color.card_event_going));
            TextPaintUtils.registerTextPaint(sStatusGoingPaint, R.dimen.event_card_status_text_size);
            TextPaint textpaint5 = new TextPaint(textpaint2);
            sStatusNotGoingPaint = textpaint5;
            textpaint5.setColor(resources.getColor(R.color.card_event_not_going));
            TextPaintUtils.registerTextPaint(sStatusNotGoingPaint, R.dimen.event_card_status_text_size);
            TextPaint textpaint6 = new TextPaint(textpaint2);
            sStatusMaybePaint = textpaint6;
            textpaint6.setColor(resources.getColor(R.color.card_event_maybe));
            TextPaintUtils.registerTextPaint(sStatusMaybePaint, R.dimen.event_card_status_text_size);
            TextPaint textpaint7 = new TextPaint(sEventInfoTextPaint);
            sOnAirPaint = textpaint7;
            textpaint7.setColor(resources.getColor(R.color.event_detail_on_air));
            sOnAirPaint.setTextSize(resources.getDimension(R.dimen.event_card_details_on_air_size));
            TextPaintUtils.registerTextPaint(sOnAirPaint, R.dimen.event_card_details_on_air_size);
            Paint paint1 = new Paint();
            sDividerPaint = paint1;
            paint1.setColor(resources.getColor(R.color.card_event_divider));
            sDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.event_card_divider_stroke_width));
            sEventAttendingBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_events_check);
            sEventNotAttendingBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_events_not_going);
            sEventMaybeBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_events_maybe);
            sBlueRsvpBannerBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_events_ribbon_blue);
            sGreenRsvpBannerBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_events_ribbon_green);
            sGreyRsvpBannerBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_events_ribbon_grey);
            sLocationBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_location_card);
            sHangoutBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_events_hangout_taco);
            sHangoutTitle = resources.getString(R.string.event_hangout_text);
            sOnAirTitle = resources.getString(R.string.event_detail_on_air);
            sOnAirNinePatch = (NinePatchDrawable)resources.getDrawable(R.drawable.btn_events_on_air);
            sInitialized = true;
        }
        mThemeImageRect = new Rect();
        mRsvpIconRect = new Rect();
        mRsvpLayoutCorner = new Point();
        mRsvpBannerRect = new Rect();
        mLocationIconRect = new Rect();
        mNameLayoutCorner = new Point();
        mDateLayoutCorner = new Point();
        mTimeZoneCorner = new Point();
        mLocationLayoutCorner = new Point();
        mAttributionLayoutCorner = new Point();
        mDividerLines = new float[4];
    }

    private static void drawTextLayout(StaticLayout staticlayout, Point point, Canvas canvas)
    {
        canvas.translate(point.x, point.y);
        staticlayout.draw(canvas);
        canvas.translate(-point.x, -point.y);
    }

    private static StaticLayout layoutTextLabel(int i, int j, int k, CharSequence charsequence, Point point, TextPaint textpaint, boolean flag)
    {
        return TextPaintUtils.layoutBitmapTextLabel(i, j, k, 0, null, null, 0, charsequence, point, textpaint, flag);
    }

    public final void attach()
    {
        ImageCache _tmp = sImageCache;
        ImageCache.registerMediaImageChangeListener(this);
        ImageCache _tmp1 = sImageCache;
        ImageCache.registerAvatarChangeListener(this);
    }

    public final void bind(EsAccount esaccount, CardView cardview, Event plusevent, ClickableUserImage.UserImageClickListener userimageclicklistener)
    {
        bind(esaccount, cardview, plusevent, null, null, userimageclicklistener);
    }

    public final void bind(EsAccount esaccount, CardView cardview, Event plusevent, String s, CharSequence charsequence, ClickableUserImage.UserImageClickListener userimageclicklistener)
    {
        clear();
        mEventInfo = plusevent;
        boolean flag;
        if(mEventInfo != null)
            flag = true;
        else
            flag = false;
        mBound = flag;
        if(mBound)
        {
            mAccount = esaccount;
            mContainingCardView = cardview;
            mThemeInfo = EsEventData.getThemeImage(mEventInfo.getTheme());
            mContainingCardView.removeClickableItem(mAvatar);
            CardView cardview1 = mContainingCardView;
            String s1;
            if(s != null)
                s1 = s;
            else
                s1 = mEventInfo.getPublisher();
            mAvatar = new ClickableUserImage(cardview1, s1, null, null, userimageclicklistener, 2);
            mAttribution = charsequence;
            mContainingCardView.addClickableItem(mAvatar);
        }
    }

    public final void clear()
    {
        if(mBound)
        {
            mContainingCardView.removeClickableItem(mAvatar);
            mAvatar = null;
            mThemeInfo = null;
            mEventInfo = null;
            mContainingCardView = null;
            mAccount = null;
            mRsvpIcon = null;
            mRsvpBanner = null;
            mThemeImage = null;
            mThemeImageRect.setEmpty();
            mRsvpIconRect.setEmpty();
            mRsvpLayoutCorner.set(0, 0);
            mRsvpBannerRect.setEmpty();
            mLocationIconRect.setEmpty();
            mNameLayout = null;
            mDateLayout = null;
            mTimeZoneLayout = null;
            mLocationLayout = null;
            mRsvpLayout = null;
            mCreatorLayout = null;
            mTypeLabel = null;
            mLocationIcon = null;
            mNameLayoutCorner.set(0, 0);
            mDateLayoutCorner.set(0, 0);
            mTimeZoneCorner.set(0, 0);
            mLocationLayoutCorner.set(0, 0);
            mAttributionLayoutCorner.set(0, 0);
            mAttribution = null;
            mBound = false;
        }
    }

    public final void detach()
    {
        ImageCache _tmp = sImageCache;
        ImageCache.unregisterAvatarChangeListener(this);
        ImageCache _tmp1 = sImageCache;
        ImageCache.unregisterMediaImageChangeListener(this);
    }

    public final int draw(int i, int j, Canvas canvas)
    {
        int k = i;
        int l = i + j;
        if(mBound)
        {
            if(mThemeImage != null)
            {
                mThemeImage.refreshIfInvalidated();
                Bitmap bitmap1 = mThemeImage.getBitmap();
                if(bitmap1 != null && (mThemeImageRect.bottom <= l || mIgnoreHeight))
                    canvas.drawBitmap(bitmap1, null, mThemeImageRect, sResizePaint);
            }
            if(mAvatar != null && (mAvatar.getRect().bottom <= l || mIgnoreHeight))
            {
                Bitmap bitmap = mAvatar.getBitmap();
                if(bitmap == null)
                    bitmap = sAuthorBitmap;
                canvas.drawBitmap(bitmap, null, mAvatar.getRect(), sResizePaint);
                if(mAvatar.isClicked())
                    mAvatar.drawSelectionRect(canvas);
            }
            if(mRsvpLayout != null && (Math.max(mRsvpBannerRect.bottom, Math.max(mRsvpIconRect.bottom, mRsvpLayoutCorner.y + mRsvpLayout.getHeight())) <= l || mIgnoreHeight))
            {
                canvas.drawBitmap(mRsvpBanner, null, mRsvpBannerRect, null);
                drawTextLayout(mRsvpLayout, mRsvpLayoutCorner, canvas);
                if(mRsvpIcon != null)
                    canvas.drawBitmap(mRsvpIcon, null, mRsvpIconRect, null);
                canvas.drawLines(mDividerLines, sDividerPaint);
            }
            if(mNameLayoutCorner.y + mNameLayout.getHeight() <= l || mIgnoreHeight)
                drawTextLayout(mNameLayout, mNameLayoutCorner, canvas);
            int i1 = mDateLayoutCorner.y + mDateLayout.getHeight();
            if(i1 <= l || mIgnoreHeight)
            {
                drawTextLayout(mDateLayout, mDateLayoutCorner, canvas);
                k = i1;
            }
            if(mTimeZoneLayout != null)
            {
                int i2 = mTimeZoneCorner.y + mTimeZoneLayout.getHeight();
                if(i2 <= l || mIgnoreHeight)
                {
                    drawTextLayout(mTimeZoneLayout, mTimeZoneCorner, canvas);
                    k = i2;
                }
            }
            if(mTypeLabel != null)
            {
                int l1 = mTypeLabel.getRect().bottom;
                if(l1 <= l || mIgnoreHeight)
                {
                    mTypeLabel.draw(canvas);
                    k = l1;
                }
            }
            if(mLocationLayout != null)
            {
                int k1 = Math.max(mLocationIconRect.bottom, mLocationLayout.getHeight() + mLocationLayoutCorner.y);
                if(k1 <= l || mIgnoreHeight)
                {
                    drawTextLayout(mLocationLayout, mLocationLayoutCorner, canvas);
                    canvas.drawBitmap(mLocationIcon, null, mLocationIconRect, null);
                    k = k1;
                }
            }
            if(mAttribution != null)
            {
                int j1 = mAttributionLayoutCorner.y + mCreatorLayout.getHeight();
                if(j1 <= l || mIgnoreHeight)
                {
                    drawTextLayout(mCreatorLayout, mAttributionLayoutCorner, canvas);
                    k = j1;
                }
            }
            i = k;
        }
        return i;
    }

    public final int layout(int i, int j, boolean flag, int k, int l)
    {
    	if(null == mContainingCardView || !mBound) {
    		return 0;
    	}
    	
    	int j1;
        int l1;
        int i3;
        int j3;
        int k3;
        Context context;
        Object obj;
        TextPaint textpaint;
        int i4;
        int j4;
        int k4;
        int l4;
        int i5;
        int j5;
        int l6;
        String s2;
        
        mIgnoreHeight = flag;
        j1 = sEventCardPadding;
        int k1 = j1 * 2;
        l1 = sEventTextLineSpacing;
        int i2 = Math.round((float)k / 3.36F);
        if((mThemeImage == null || mThemeImageRect.width() != k || mThemeImageRect.height() != i2 || mThemeImageRect.top != j || mThemeImageRect.left != i) && mThemeInfo != null)
        {
            mThemeImageRect.set(i, j, i + k, j + i2);
            EventThemeImageRequest eventthemeimagerequest = new EventThemeImageRequest(ImageUtils.getCenterCroppedAndResizedUrl(k, i2, mThemeInfo.getUrl()));
            mThemeImage = new RemoteImage(mContainingCardView, eventthemeimagerequest);
            mThemeImage.load();
        }
        int j2 = sAvatarSize;
        int k2 = i + j1;
        int l2 = (j + i2) - k1;
        mAvatar.setRect(k2, l2, k2 + j2, l2 + j2);
        i3 = j1 + (k2 + j2);
        j3 = j + i2;
        k3 = (i + k) - i3 - k1;
        context = mContainingCardView.getContext();
        int k5 = 0;
        if(EsEventData.canRsvp(mEventInfo)) {
        	obj = null;
            textpaint = null;
        	switch(EsEventData.getRsvpStatus(mEventInfo)) {
        	case 0:
        		obj = context.getString(R.string.card_event_invited_prompt);
                textpaint = sStatusInvitedPaint;
                mRsvpBanner = sBlueRsvpBannerBitmap;
        		break;
        	case 1:
        		obj = context.getString(R.string.card_event_going_prompt);
                textpaint = sStatusGoingPaint;
                mRsvpIcon = sEventAttendingBitmap;
                mRsvpBanner = sGreenRsvpBannerBitmap;
        		break;
        	case 2:
        		obj = context.getString(R.string.card_event_maybe_prompt);
                textpaint = sStatusMaybePaint;
                mRsvpIcon = sEventMaybeBitmap;
                mRsvpBanner = sBlueRsvpBannerBitmap;
        		break;
        	case 3:
        		obj = context.getString(R.string.card_event_declined_prompt);
                textpaint = sStatusNotGoingPaint;
                mRsvpIcon = sEventNotAttendingBitmap;
                mRsvpBanner = sGreyRsvpBannerBitmap;
        		break;
        	default:
        		break;
        	}
           
        	i4 = Math.round(sRibbonHeightPercentOverlap * (float)mRsvpBanner.getHeight());
            j4 = j3 - i4;
            mRsvpBannerRect.set((i3 + k3) - mRsvpBanner.getWidth(), j4, i3 + k3, j4 + mRsvpBanner.getHeight());
            k4 = k3 - mRsvpBannerRect.width() - j1;
            mRsvpLayout = TextPaintUtils.layoutBitmapTextLabel(i3, j3 + j1, k4, 0, mRsvpIcon, mRsvpIconRect, j1, ((CharSequence) (obj)), mRsvpLayoutCorner, textpaint, true);
            l4 = (mRsvpLayoutCorner.y + mRsvpLayout.getHeight()) - j3;
            if(mRsvpIcon != null)
                i5 = mRsvpIconRect.bottom - j3;
            else
                i5 = 0;
            j5 = j1 + (j3 + Math.max(l4, i5));
            mDividerLines[0] = i3;
            mDividerLines[1] = j5;
            mDividerLines[2] = k4 + i3;
            mDividerLines[3] = j5;
            k5 = Math.max(j5 - j3, mRsvpBannerRect.height() - i4);
        }
    	
        int l5 = j1 + (k5 + j3);
        mNameLayout = layoutTextLabel(i3, l5, k3, mEventInfo.getName(), mNameLayoutCorner, sEventNameTextPaint, true);
        int i6 = l5 + mNameLayout.getHeight();
        if(mAttribution != null)
        {
            int k7 = i6 + l1;
            mCreatorLayout = layoutTextLabel(i3, k7, k3, mAttribution, mAttributionLayoutCorner, sEventInfoTextPaint, false);
            i6 = k7 + mCreatorLayout.getHeight();
        }
        int j6 = i6 + l1;
        mDateLayout = layoutTextLabel(i3, j6, k3, EventDateUtils.getSingleDisplayLine(mContainingCardView.getContext(), mEventInfo.getStartTime(), null, false, null), mDateLayoutCorner, sEventInfoTextPaint, true);
        int k6 = j6 + mDateLayout.getHeight();
        boolean flag1 = EsEventData.isEventHangout(mEventInfo);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mEventInfo.getStartTime().getTimeMs().longValue());
        String s = TimeZoneHelper.getDisplayString(mEventInfo.getStartTime().getTimezone(), calendar, flag1);
        if(s != null)
        {
            int j7 = k6 + l1;
            mTimeZoneLayout = layoutTextLabel(i3, j7, k3, s, mTimeZoneCorner, sEventInfoTextPaint, true);
            k6 = j7 + mTimeZoneLayout.getHeight();
        }
        if(flag1)
        {
            int i7 = k6 + l1;
            mTypeLabel = new ClickableButton(mContainingCardView.getContext(), null, sOnAirTitle, sOnAirPaint, sOnAirNinePatch, sOnAirNinePatch, null, i3, i7);
            k6 = i7 + mTypeLabel.getRect().height();
        }
        Location location = mEventInfo.getLocation();
        HangoutInfo hangoutinfo = mEventInfo.getHangoutInfo();
        String s1;
        if(location != null)
        {
            s2 = location.buildAddress();
            mLocationIcon = sLocationBitmap;
            s1 = s2;
        } else
        {
            s1 = null;
            if(hangoutinfo != null)
            {
                s1 = sHangoutTitle;
                mLocationIcon = sHangoutBitmap;
            }
        }
        if(s1 != null)
        {
            l6 = k6 + l1;
            mLocationLayout = TextPaintUtils.layoutBitmapTextLabel(i3, l6, k3, 0, mLocationIcon, mLocationIconRect, j1, s1, mLocationLayoutCorner, sEventInfoTextPaint, true);
            k6 = l6 + mLocationLayout.getHeight();
        }
        return (j3 + (k6 - j3)) - j;
    }

    public final void onAvatarChanged(String s)
    {
        if(mAvatar != null)
            mAvatar.onAvatarChanged(s);
    }

    public final void onMediaImageChanged(String s)
    {
        if(mEventInfo != null && mThemeImage != null && MediaImageRequest.areCanonicallyEqual((MediaImageRequest)mThemeImage.getRequest(), s))
            mThemeImage.invalidate();
    }

}
