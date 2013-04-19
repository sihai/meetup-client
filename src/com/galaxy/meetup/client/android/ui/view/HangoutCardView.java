/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.DbEmbedHangout;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.view.ClickableButton.ClickableButtonListener;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.client.util.Utils;

/**
 * 
 * @author sihai
 *
 */
public class HangoutCardView extends StreamCardView implements
		ClickableButtonListener {

	private static boolean sHangoutCardViewInitialized;
    private static TextPaint sHangoutJoinButtonPaint;
    protected static NinePatchDrawable sHangoutJoinDrawable;
    protected static NinePatchDrawable sHangoutJoinPressedDrawable;
    private static TextPaint sHangoutUnsupportedTextPaint;
    protected static int sMaxHangoutAvatarsToDisplay;
    protected int mAvatarsToDisplay;
    protected DbEmbedHangout mDbEmbedHangout;
    protected final ArrayList mHangoutAvatars;
    protected ClickableButton mJoinButton;
    protected StaticLayout mUnsupportedLayout;
    
    public HangoutCardView(Context context)
    {
        this(context, null);
    }

    public HangoutCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mHangoutAvatars = new ArrayList();
        if(!sHangoutCardViewInitialized)
        {
            sHangoutCardViewInitialized = true;
            Resources resources = getResources();
            TextPaint textpaint = new TextPaint();
            sHangoutJoinButtonPaint = textpaint;
            textpaint.setAntiAlias(true);
            sHangoutJoinButtonPaint.setColor(resources.getColor(R.color.card_hangout_join));
            sHangoutJoinButtonPaint.setTextSize(resources.getDimension(R.dimen.card_hangout_join_button_text_size));
            sHangoutJoinButtonPaint.setTypeface(Typeface.DEFAULT);
            TextPaintUtils.registerTextPaint(sHangoutJoinButtonPaint, R.dimen.card_hangout_join_button_text_size);
            TextPaint textpaint1 = new TextPaint();
            sHangoutUnsupportedTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sHangoutUnsupportedTextPaint.setColor(resources.getColor(R.color.card_hangout_unsupported));
            sHangoutUnsupportedTextPaint.setTextSize(resources.getDimension(R.dimen.card_hangout_unsupported_text_size));
            sHangoutUnsupportedTextPaint.setTypeface(Typeface.DEFAULT);
            TextPaintUtils.registerTextPaint(sHangoutUnsupportedTextPaint, R.dimen.card_hangout_unsupported_text_size);
            sHangoutJoinDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.blue_button_default);
            sHangoutJoinPressedDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.blue_button_pressed);
            sMaxHangoutAvatarsToDisplay = resources.getInteger(R.integer.card_max_hangout_avatars);
        }
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        int i1 = (sTopBorderPadding + (int)((float)(l + 2 * sYPadding) * getMediaHeightPercentage())) - sYPadding;
        drawMediaTopAreaStageWithTiledBackground(canvas, k, i1);
        if(mUnsupportedLayout != null)
        {
            int k1 = i + (k - mUnsupportedLayout.getWidth()) / 2;
            int l1 = j + (i1 - mUnsupportedLayout.getHeight()) / 2;
            canvas.translate(k1, l1);
            mUnsupportedLayout.draw(canvas);
            canvas.translate(-k1, -l1);
        }
        if(mJoinButton != null)
            mJoinButton.draw(canvas);
        if(mUnsupportedLayout == null)
        {
            for(int j1 = 0; j1 < mAvatarsToDisplay; j1++)
            {
                ClickableAvatar clickableavatar = (ClickableAvatar)mHangoutAvatars.get(j1);
                Rect rect = clickableavatar.getRect();
                android.graphics.Bitmap bitmap = clickableavatar.getBitmap();
                if(bitmap == null)
                    bitmap = sAuthorBitmap;
                canvas.drawBitmap(bitmap, null, rect, sResizePaint);
            }

        }
        drawMediaTopAreaShadow(canvas, k, l);
        drawTagBarIconAndBackground(canvas, i, j);
        drawPlusOneBar(canvas);
        drawMediaBottomArea(canvas, i, k, l);
        drawCornerIcon(canvas);
        return l;
    }

    public final void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamCardView.ViewedListener viewedlistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener)
    {
        byte abyte0[];
        super.init(cursor, i, j, onclicklistener, itemclicklistener, viewedlistener, streamplusbarclicklistener, streammediaclicklistener);
        abyte0 = cursor.getBlob(25);
        if(abyte0 == null) {
        	if(EsLog.isLoggable("HangoutCardView", 5))
                Log.w("HangoutCardView", "No hangout data!");
        	return;
        }
        
        mDbEmbedHangout = DbEmbedHangout.deserialize(abyte0);
        int k = mDbEmbedHangout.getNumAttendees();
        int l = Math.min(sMaxHangoutAvatarsToDisplay, k);
        List arraylist = mDbEmbedHangout.getAttendeeGaiaIds();
        List arraylist1 = mDbEmbedHangout.getAttendeeNames();
        List arraylist2 = mDbEmbedHangout.getAttendeeAvatarUrls();
        for(int i1 = 0; i1 < l; i1++)
        {
            ClickableAvatar clickableavatar = new ClickableAvatar(this, (String)arraylist.get(i1), (String)arraylist2.get(i1), (String)arraylist1.get(i1), null, 2);
            mHangoutAvatars.add(clickableavatar);
        }

        Resources resources = getResources();
        if(mDbEmbedHangout.isInProgress())
        {
            int k1 = R.string.card_hangout_state_active;
            Object aobj1[] = new Object[1];
            aobj1[0] = mAuthorName;
            mContent = resources.getString(k1, aobj1);
            if(k == 0)
                k = 1;
            int l1 = R.plurals.card_hangout_with_people;
            Object aobj2[] = new Object[1];
            aobj2[0] = Integer.valueOf(k);
            mTag = resources.getQuantityString(l1, k, aobj2);
            mTagIcon = sTagHangoutBitmaps[0];
        } else
        {
            int j1 = R.string.card_hangout_state_inactive;
            Object aobj[] = new Object[1];
            aobj[0] = mAuthorName;
            mContent = resources.getString(j1, aobj);
        }
    }

    protected final int layoutElements(int i, int j, int k, int l)
    {
        int i1 = (sTopBorderPadding + (int)((float)(l + 2 * sYPadding) * getMediaHeightPercentage())) - sYPadding;
        mBackgroundRect.set(0, i1, getMeasuredWidth(), getMeasuredHeight());
        createTagBar(i, j, k);
        boolean flag = mDbEmbedHangout.isInProgress();
        int j1 = 0;
        int k1;
        int l1;
        int i2;
        int k2;
        int l2;
        int j3;
        int k3;
        int l3;
        int i4;
        if(flag)
        {
            Context context = getContext();
            Hangout.SupportStatus supportstatus = Hangout.getSupportedStatus(getContext(), EsService.getActiveAccount(context));
            if(supportstatus != Hangout.SupportStatus.SUPPORTED)
            {
                mUnsupportedLayout = new StaticLayout(supportstatus.getErrorMessage(context), sHangoutUnsupportedTextPaint, k, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            } else
            {
                String s;
                Rect rect1;
                if(mDbEmbedHangout.isJoinable())
                    s = context.getString(R.string.hangout_enter_greenroom);
                else
                    s = context.getString(R.string.hangout_broadcast_view);
                removeClickableItem(mJoinButton);
                mJoinButton = new ClickableButton(context, s, sHangoutJoinButtonPaint, sHangoutJoinDrawable, sHangoutJoinPressedDrawable, this, i, i1 / 2);
                rect1 = mJoinButton.getRect();
                j1 = 0 + rect1.width();
                rect1.offset(0, -rect1.height() / 2);
                addClickableItem(mJoinButton);
            }
        }
        if(mUnsupportedLayout == null)
        {
            k1 = mDbEmbedHangout.getNumAttendees();
            l1 = k + sXDoublePadding;
            mAvatarsToDisplay = Math.min(Math.min(sMaxHangoutAvatarsToDisplay, k1), (l1 - j1) / sAvatarSize);
            i2 = 1 + mAvatarsToDisplay;
            int j2;
            int i3;
            if(mJoinButton != null)
                j2 = 1;
            else
                j2 = 0;
            k2 = i2 + j2;
            l2 = (l1 - mAvatarsToDisplay * sAvatarSize - j1) / k2;
            i3 = l2 + sLeftBorderPadding;
            j3 = l2 + (sAvatarSize + sLeftBorderPadding);
            k3 = (i1 - sAvatarSize) / 2;
            l3 = k3 + sAvatarSize;
            for(i4 = 0; i4 < mAvatarsToDisplay; i4++)
            {
                ((ClickableAvatar)mHangoutAvatars.get(i4)).setRect(i3, k3, j3, l3);
                i3 += l2 + sAvatarSize;
                j3 += l2 + sAvatarSize;
            }

            if(mJoinButton != null)
            {
                Rect rect = mJoinButton.getRect();
                int j4 = rect.top;
                rect.offsetTo(i3, j4);
            }
        }
        createPlusOneBar(i, i1, k);
        createMediaBottomArea(i, j, k, l);
        return l;
    }

    protected final void onBindResources()
    {
        super.onBindResources();
        int i = mHangoutAvatars.size();
        for(int j = 0; j < i; j++)
            ((ClickableAvatar)mHangoutAvatars.get(j)).bindResources();

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
                Hangout.enterGreenRoom(EsService.getActiveAccount(context), context, mAuthorGaiaId, mAuthorName, mDbEmbedHangout);
            }
        } else
        {
            super.onClickableButtonListenerClick(clickablebutton);
        }
    }

    public void onRecycle()
    {
        super.onRecycle();
        mHangoutAvatars.clear();
        mDbEmbedHangout = null;
        mUnsupportedLayout = null;
        mJoinButton = null;
        mAvatarsToDisplay = 0;
    }

    protected final void onUnbindResources()
    {
        super.onUnbindResources();
        int i = mHangoutAvatars.size();
        for(int j = 0; j < i; j++)
            ((ClickableAvatar)mHangoutAvatars.get(j)).unbindResources();

    }

}
