/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.DbEmbedHangout;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageCache.OnAvatarChangeListener;
import com.galaxy.meetup.client.android.ui.view.ClickableButton.ClickableButtonListener;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.client.util.Utils;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpHangoutView extends View implements
		OnAvatarChangeListener, ClickableButtonListener {

	protected static TextPaint sActiveTextPaint;
    protected static int sAvatarSize;
    protected static int sAvatarSpacing;
    protected static int sButtonMarginBottom;
    protected static Bitmap sDefaultAvatarBitmap;
    protected static TextPaint sDefaultTextPaint;
    protected static Bitmap sHangoutActiveBitmap;
    private static boolean sHangoutCardViewInitialized;
    protected static NinePatchDrawable sHangoutJoinDrawable;
    protected static NinePatchDrawable sHangoutJoinPressedDrawable;
    protected static Bitmap sHangoutOverBitmap;
    private static ImageCache sImageCache;
    private static TextPaint sJoinButtonPaint;
    protected static int sMaxHangoutAvatarsToDisplay;
    protected static int sMaxWidth;
    protected static int sNameMargin;
    private static Paint sResizePaint;
    private static TextPaint sUnsupportedTextPaint;
    private String mAuthorId;
    private String mAuthorName;
    private int mAvatarsToDisplay;
    private final Set mClickableItems;
    private ClickableItem mCurrentClickableItem;
    private DbEmbedHangout mDbEmbedHangout;
    private PositionedStaticLayout mExtraParticpantsLayout;
    private final ArrayList mHangoutAvatars;
    private Bitmap mHangoutIcon;
    private Rect mHangoutIconRect;
    private PositionedStaticLayout mHangoutLayout;
    private ClickableButton mJoinButton;
    private String mParticipantNames;
    
    public StreamOneUpHangoutView(Context context)
    {
        super(context);
        mHangoutAvatars = new ArrayList();
        mClickableItems = new HashSet();
        Context context1 = getContext();
        if(!sHangoutCardViewInitialized)
        {
            sHangoutCardViewInitialized = true;
            sImageCache = ImageCache.getInstance(context1);
            Resources resources = getResources();
            sHangoutJoinDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.blue_button_default);
            sHangoutJoinPressedDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.blue_button_pressed);
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(context1, true);
            sHangoutActiveBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_nav_hangouts);
            sHangoutOverBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_hangouts_over);
            sAvatarSize = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_avatar_size);
            sAvatarSpacing = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_avatar_spacing);
            sMaxWidth = (int)resources.getDimension(R.dimen.stream_one_up_list_max_width);
            sButtonMarginBottom = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_button_margin_bottom);
            sNameMargin = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_name_margin);
            sMaxHangoutAvatarsToDisplay = resources.getInteger(R.integer.card_max_hangout_avatars);
            sResizePaint = new Paint(2);
            TextPaint textpaint = new TextPaint();
            sDefaultTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDefaultTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_default));
            sDefaultTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sDefaultTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            TextPaint textpaint1 = new TextPaint();
            sActiveTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sActiveTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_hangout_active_name));
            sActiveTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sActiveTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            TextPaint textpaint2 = new TextPaint();
            sJoinButtonPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sJoinButtonPaint.setColor(resources.getColor(R.color.stream_one_up_stage_default));
            sJoinButtonPaint.setTextSize(resources.getDimension(R.dimen.card_hangout_join_button_text_size));
            sJoinButtonPaint.setTypeface(Typeface.DEFAULT);
            TextPaintUtils.registerTextPaint(sJoinButtonPaint, R.dimen.card_hangout_join_button_text_size);
            TextPaint textpaint3 = new TextPaint();
            sUnsupportedTextPaint = textpaint3;
            textpaint3.setAntiAlias(true);
            sUnsupportedTextPaint.setColor(resources.getColor(R.color.card_hangout_unsupported));
            sUnsupportedTextPaint.setTextSize(resources.getDimension(R.dimen.card_hangout_unsupported_text_size));
            sUnsupportedTextPaint.setTypeface(Typeface.DEFAULT);
            TextPaintUtils.registerTextPaint(sUnsupportedTextPaint, R.dimen.card_hangout_unsupported_text_size);
        }
    }

    public StreamOneUpHangoutView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mHangoutAvatars = new ArrayList();
        mClickableItems = new HashSet();
        Context context1 = getContext();
        if(!sHangoutCardViewInitialized)
        {
            sHangoutCardViewInitialized = true;
            sImageCache = ImageCache.getInstance(context1);
            Resources resources = getResources();
            sHangoutJoinDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.blue_button_default);
            sHangoutJoinPressedDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.blue_button_pressed);
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(context1, true);
            sHangoutActiveBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_nav_hangouts);
            sHangoutOverBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_hangouts_over);
            sAvatarSize = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_avatar_size);
            sAvatarSpacing = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_avatar_spacing);
            sMaxWidth = (int)resources.getDimension(R.dimen.stream_one_up_list_max_width);
            sButtonMarginBottom = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_button_margin_bottom);
            sNameMargin = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_name_margin);
            sMaxHangoutAvatarsToDisplay = resources.getInteger(R.integer.card_max_hangout_avatars);
            sResizePaint = new Paint(2);
            TextPaint textpaint = new TextPaint();
            sDefaultTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDefaultTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_default));
            sDefaultTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sDefaultTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            TextPaint textpaint1 = new TextPaint();
            sActiveTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sActiveTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_hangout_active_name));
            sActiveTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sActiveTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            TextPaint textpaint2 = new TextPaint();
            sJoinButtonPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sJoinButtonPaint.setColor(resources.getColor(R.color.stream_one_up_stage_default));
            sJoinButtonPaint.setTextSize(resources.getDimension(R.dimen.card_hangout_join_button_text_size));
            sJoinButtonPaint.setTypeface(Typeface.DEFAULT);
            TextPaintUtils.registerTextPaint(sJoinButtonPaint, R.dimen.card_hangout_join_button_text_size);
            TextPaint textpaint3 = new TextPaint();
            sUnsupportedTextPaint = textpaint3;
            textpaint3.setAntiAlias(true);
            sUnsupportedTextPaint.setColor(resources.getColor(R.color.card_hangout_unsupported));
            sUnsupportedTextPaint.setTextSize(resources.getDimension(R.dimen.card_hangout_unsupported_text_size));
            sUnsupportedTextPaint.setTypeface(Typeface.DEFAULT);
            TextPaintUtils.registerTextPaint(sUnsupportedTextPaint, R.dimen.card_hangout_unsupported_text_size);
        }
    }

    public StreamOneUpHangoutView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mHangoutAvatars = new ArrayList();
        mClickableItems = new HashSet();
        Context context1 = getContext();
        if(!sHangoutCardViewInitialized)
        {
            sHangoutCardViewInitialized = true;
            sImageCache = ImageCache.getInstance(context1);
            Resources resources = getResources();
            sHangoutJoinDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.blue_button_default);
            sHangoutJoinPressedDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.blue_button_pressed);
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(context1, true);
            sHangoutActiveBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_nav_hangouts);
            sHangoutOverBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_hangouts_over);
            sAvatarSize = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_avatar_size);
            sAvatarSpacing = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_avatar_spacing);
            sMaxWidth = (int)resources.getDimension(R.dimen.stream_one_up_list_max_width);
            sButtonMarginBottom = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_button_margin_bottom);
            sNameMargin = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_name_margin);
            sMaxHangoutAvatarsToDisplay = resources.getInteger(R.integer.card_max_hangout_avatars);
            sResizePaint = new Paint(2);
            TextPaint textpaint = new TextPaint();
            sDefaultTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDefaultTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_default));
            sDefaultTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sDefaultTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            TextPaint textpaint1 = new TextPaint();
            sActiveTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sActiveTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_hangout_active_name));
            sActiveTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sActiveTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            TextPaint textpaint2 = new TextPaint();
            sJoinButtonPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sJoinButtonPaint.setColor(resources.getColor(R.color.stream_one_up_stage_default));
            sJoinButtonPaint.setTextSize(resources.getDimension(R.dimen.card_hangout_join_button_text_size));
            sJoinButtonPaint.setTypeface(Typeface.DEFAULT);
            TextPaintUtils.registerTextPaint(sJoinButtonPaint, R.dimen.card_hangout_join_button_text_size);
            TextPaint textpaint3 = new TextPaint();
            sUnsupportedTextPaint = textpaint3;
            textpaint3.setAntiAlias(true);
            sUnsupportedTextPaint.setColor(resources.getColor(R.color.card_hangout_unsupported));
            sUnsupportedTextPaint.setTextSize(resources.getDimension(R.dimen.card_hangout_unsupported_text_size));
            sUnsupportedTextPaint.setTypeface(Typeface.DEFAULT);
            TextPaintUtils.registerTextPaint(sUnsupportedTextPaint, R.dimen.card_hangout_unsupported_text_size);
        }
    }

    public final void bind(DbEmbedHangout dbembedhangout, String s, String s1, OneUpListener oneuplistener)
    {
        mHangoutAvatars.clear();
        mClickableItems.clear();
        mCurrentClickableItem = null;
        mDbEmbedHangout = null;
        mHangoutLayout = null;
        mExtraParticpantsLayout = null;
        mJoinButton = null;
        mHangoutIcon = null;
        mHangoutIconRect = null;
        mDbEmbedHangout = dbembedhangout;
        mAuthorName = s;
        mAuthorId = s1;
        StringBuilder stringbuilder = new StringBuilder();
        List arraylist = mDbEmbedHangout.getAttendeeGaiaIds();
        List arraylist1 = mDbEmbedHangout.getAttendeeNames();
        List arraylist2 = mDbEmbedHangout.getAttendeeAvatarUrls();
        int i = Math.min(sMaxHangoutAvatarsToDisplay, mDbEmbedHangout.getNumAttendees());
        for(int j = 0; j < i; j++)
        {
            String s2 = (String)arraylist1.get(j);
            ClickableUserImage clickableuserimage = new ClickableUserImage(this, (String)arraylist.get(j), (String)arraylist2.get(j), s2, oneuplistener, 2);
            mClickableItems.add(clickableuserimage);
            mHangoutAvatars.add(clickableuserimage);
            stringbuilder.append('\n').append(s2);
        }

        mParticipantNames = stringbuilder.toString();
        invalidate();
        requestLayout();
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent)
    {
        boolean flag;
        int i;
        int j;
        flag = true;
        i = (int)motionevent.getX();
        j = (int)motionevent.getY();
        int action = motionevent.getAction();
        switch(action) {
	        case 0:
	        	for(Iterator iterator1 = mClickableItems.iterator(); iterator1.hasNext();)
	            {
	                ClickableItem clickableitem = (ClickableItem)iterator1.next();
	                if(clickableitem.handleEvent(i, j, 0))
	                {
	                    mCurrentClickableItem = clickableitem;
	                    invalidate();
	                    continue; /* Loop/switch isn't completed */
	                }
	            }

	            flag = false;
	        	break;
	        case 1:
	        	mCurrentClickableItem = null;
	            for(Iterator iterator = mClickableItems.iterator(); iterator.hasNext(); ((ClickableItem)iterator.next()).handleEvent(i, j, 1));
	            invalidate();
	            flag = false;
	        	break;
	        case 2:
	        	flag = false;
	        	break;
	        case 3:
	        	if(mCurrentClickableItem != null)
	            {
	                mCurrentClickableItem.handleEvent(i, j, 3);
	                mCurrentClickableItem = null;
	                invalidate();
	            } else
	            {
	                flag = false;
	            }
	        	break;
        	default:
        		flag = false;
        		break;
        }
        return flag;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ImageCache _tmp = sImageCache;
        ImageCache.registerAvatarChangeListener(this);
    }

    public void onAvatarChanged(String s)
    {
        for(Iterator iterator = mHangoutAvatars.iterator(); iterator.hasNext(); ((ClickableUserImage)iterator.next()).onAvatarChanged(s));
    }

    public final void onClickableButtonListenerClick(ClickableButton clickablebutton)
    {
        if(clickablebutton == mJoinButton)
        {
            Context context = getContext();
            if(!mDbEmbedHangout.isJoinable())
            {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse((new StringBuilder("https://www.youtube.com/watch?v=")).append(mDbEmbedHangout.getYoutubeLiveId()).toString()));
                intent.addFlags(0x80000);
                if(Utils.isAppInstalled("com.google.android.youtube", context))
                    intent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
                context.startActivity(intent);
            } else
            {
                Hangout.enterGreenRoom(EsService.getActiveAccount(context), context, mAuthorId, mAuthorName, mDbEmbedHangout);
            }
        }
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        ImageCache _tmp = sImageCache;
        ImageCache.unregisterAvatarChangeListener(this);
    }

    protected void onDraw(Canvas canvas)
    {
        if(mHangoutIcon != null)
            canvas.drawBitmap(mHangoutIcon, null, mHangoutIconRect, null);
        if(mHangoutLayout == null)
        {
            int l = mHangoutLayout.getLeft();
            int i1 = mHangoutLayout.getTop();
            canvas.translate(l, i1);
            mHangoutLayout.draw(canvas);
            canvas.translate(-l, -i1);
        }
        if(mJoinButton != null)
            mJoinButton.draw(canvas);
        for(int i = -1 + Math.min(mAvatarsToDisplay, mHangoutAvatars.size()); i >= 0; i--)
        {
            ClickableUserImage clickableuserimage = (ClickableUserImage)mHangoutAvatars.get(i);
            Bitmap bitmap = clickableuserimage.getBitmap();
            if(bitmap == null)
                bitmap = sDefaultAvatarBitmap;
            canvas.drawBitmap(bitmap, null, clickableuserimage.getRect(), sResizePaint);
        }

        if(mExtraParticpantsLayout != null)
        {
            int j = mExtraParticpantsLayout.getLeft();
            int k = mExtraParticpantsLayout.getTop();
            canvas.translate(j, k);
            mExtraParticpantsLayout.draw(canvas);
            canvas.translate(-j, -k);
        }
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int mode = android.view.View.MeasureSpec.getMode(i);
        int l = sMaxWidth;
        if(-2147483648 == mode) {
        	l = Math.min(k, sMaxWidth);
          return;
        } else if(1073741824 == mode) {
        	l = k;
        } else {
        	l = sMaxWidth;
        }
        
        super.onMeasure(android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000), j);
        int i1 = getPaddingLeft();
        int j1 = getPaddingTop();
        int k1 = getMeasuredWidth();
        int l1 = k1 - i1 - getPaddingRight();
        Context context = getContext();
        int i2 = mDbEmbedHangout.getNumAttendees();
        TextPaint textpaint;
        String s;
        Bitmap bitmap1;
        Hangout.SupportStatus supportstatus;
        int l3;
        int i4;
        int l4;
        int k5;
        
        int k2;
        int l2;
        int i3;
        int j3;
        int k3;
        Resources resources1;
        int j4;
        Object aobj1[];
        String s1;
        int k4;
        int i5;
        int j5;
        int l5;
        int j6;
        int k6;
        int j7;
        Object aobj2[];
        String s2;
        int k7;
        int l7;
        int i8;
        int j8;
        int k8;
        Object aobj3[];
        Rect rect;
        
        if(mDbEmbedHangout.isInProgress())
        {
            Hangout.SupportStatus supportstatus1 = Hangout.getSupportedStatus(context, EsService.getActiveAccount(context));
            Bitmap bitmap2 = sHangoutActiveBitmap;
            if(supportstatus1 != Hangout.SupportStatus.SUPPORTED)
            {
                textpaint = sUnsupportedTextPaint;
                s = supportstatus1.getErrorMessage(context);
                supportstatus = supportstatus1;
                bitmap1 = bitmap2;
            } else
            {
                textpaint = sActiveTextPaint;
                int l8 = R.string.stream_one_up_stage_hangout_active;
                Object aobj4[] = new Object[1];
                aobj4[0] = mAuthorName;
                s = context.getString(l8, aobj4);
                supportstatus = supportstatus1;
                bitmap1 = bitmap2;
            }
        } else
        {
            Bitmap bitmap = sHangoutOverBitmap;
            textpaint = sDefaultTextPaint;
            Resources resources = context.getResources();
            int j2 = R.plurals.stream_one_up_stage_hangout_over;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(i2);
            s = resources.getQuantityString(j2, i2, aobj);
            bitmap1 = bitmap;
            supportstatus = null;
        }
        k2 = bitmap1.getWidth();
        l2 = bitmap1.getHeight();
        i3 = i1 + (l1 - k2) / 2;
        mHangoutIcon = bitmap1;
        mHangoutIconRect = new Rect(i3, j1, k2 + i3, j1 + l2);
        j3 = j1 + (l2 + sNameMargin);
        mHangoutLayout = new PositionedStaticLayout(s, textpaint, (int)textpaint.measureText(s), android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        k3 = i1 + (l1 - mHangoutLayout.getWidth()) / 2;
        mHangoutLayout.setPosition(k3, j3);
        l3 = j3 + (mHangoutLayout.getHeight() + sNameMargin);
        if(supportstatus == Hangout.SupportStatus.SUPPORTED)
        {
            String s3;
            if(mDbEmbedHangout.isJoinable())
                s3 = context.getString(R.string.stream_one_up_stage_hangout_join);
            else
                s3 = context.getString(R.string.hangout_broadcast_view);
            mClickableItems.remove(mJoinButton);
            mJoinButton = new ClickableButton(context, s3, sJoinButtonPaint, sHangoutJoinDrawable, sHangoutJoinPressedDrawable, this, 0, 0);
            rect = mJoinButton.getRect();
            rect.offset(i1 + (l1 - rect.width()) / 2, l3);
            mClickableItems.add(mJoinButton);
            i4 = l3 + (mJoinButton.getRect().height() + sButtonMarginBottom);
        } else
        {
            i4 = l3;
        }
        resources1 = context.getResources();
        j4 = R.plurals.hangout_plus_others;
        aobj1 = new Object[1];
        aobj1[0] = Integer.valueOf(i2);
        s1 = resources1.getQuantityString(j4, i2, aobj1);
        k4 = (int)sDefaultTextPaint.measureText(s1);
        l4 = Math.min(mHangoutAvatars.size(), (l1 - k4) / (sAvatarSize + sAvatarSpacing));
        i5 = i2 - l4;
        j5 = sAvatarSpacing + l4 * (sAvatarSize + sAvatarSpacing);
        
        int i6;
        int l6;
        ClickableUserImage clickableuserimage;
        int i7;
        
        if(i5 > 0)
        {
            j7 = R.plurals.hangout_plus_others;
            aobj2 = new Object[1];
            aobj2[0] = Integer.valueOf(i5);
            s2 = resources1.getQuantityString(j7, i5, aobj2);
            k7 = (int)sDefaultTextPaint.measureText(s2);
            
            if(j5 + k7 > l1)
            {
                l7 = i5 - 1;
                i8 = l4 - 1;
                j5 = sAvatarSpacing + i8 * (sAvatarSize + sAvatarSpacing);
                j8 = R.plurals.hangout_plus_others;
                k8 = l7 - 1;
                aobj3 = new Object[1];
                aobj3[0] = Integer.valueOf(l7 - 1);
                s2 = resources1.getQuantityString(j8, k8, aobj3);
                k7 = (int)sDefaultTextPaint.measureText(s2);
                k5 = i8;
            } else
            {
                k5 = l4;
            }
            mExtraParticpantsLayout = new PositionedStaticLayout(s2, sDefaultTextPaint, k7, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            l1 -= mExtraParticpantsLayout.getWidth();
        } else
        {
            k5 = l4;
        }
        l5 = l1 - j5;
        i6 = i1 + sAvatarSpacing + l5 / 2;
        mAvatarsToDisplay = k5;
        j6 = mHangoutAvatars.size();
        k6 = 0;
        while(k6 < j6) 
        {
            clickableuserimage = (ClickableUserImage)mHangoutAvatars.get(k6);
            if(k6 < k5)
            {
                clickableuserimage.setRect(i6, i4, i6 + sAvatarSize, i4 + sAvatarSize);
                i7 = i6 + (sAvatarSize + sAvatarSpacing);
            } else
            {
                clickableuserimage.setRect(0, 0, 0, 0);
                i7 = i6;
            }
            k6++;
            i6 = i7;
        }
        if(mExtraParticpantsLayout != null)
            mExtraParticpantsLayout.setPosition(i6, i4 + (sAvatarSize - mExtraParticpantsLayout.getHeight()) / 2);
        l6 = i4 + sAvatarSize;
        setContentDescription((new StringBuilder()).append(s).append(mParticipantNames).toString());
        setMeasuredDimension(k1, l6 + getPaddingBottom());
        
    }

    public final void processClick(float f, float f1)
    {
        if(mJoinButton != null)
        {
            Rect rect = mJoinButton.getRect();
            int ai[] = new int[2];
            getLocationOnScreen(ai);
            if(rect.contains((int)(f - (float)ai[0]), (int)(f1 - (float)ai[1])))
                onClickableButtonListenerClick(mJoinButton);
        }
    }
}
