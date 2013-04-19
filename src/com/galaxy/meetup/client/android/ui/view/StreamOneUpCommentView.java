/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.DbPlusOneData;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.util.Dates;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.client.util.ViewUtils;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpCommentView extends OneUpBaseView implements
		ResourceConsumer, Recyclable {

	private static int sAvatarMarginRight;
    private static int sAvatarMarginTop;
    private static Bitmap sAvatarOverlayBitmap;
    private static int sAvatarSize;
    private static Paint sBackgroundFadePaint;
    private static Paint sBackgroundPaint;
    private static TextPaint sContentPaint;
    private static TextPaint sDatePaint;
    private static Bitmap sDefaultAvatarBitmap;
    private static Paint sDividerPaint;
    private static int sDividerThickness;
    private static Rect sFlaggedCommentFadeArea;
    private static float sFontSpacing;
    private static int sMarginBottom;
    private static int sMarginLeft;
    private static int sMarginRight;
    private static int sMarginTop;
    private static int sNameMarginRight;
    private static TextPaint sNamePaint;
    private static int sPlusOneColor;
    private static int sPlusOneInverseColor;
    private static TextPaint sPlusOnePaint;
    protected static Drawable sPressedStateBackground;
    private static Paint sResizePaint;
    private String mAuthorAvatarUrl;
    private String mAuthorId;
    private ClickableAvatar mAuthorImage;
    private String mAuthorName;
    private Set mClickableItems;
    private String mCommentContent;
    private String mCommentId;
    private boolean mContentDescriptionDirty;
    private ClickableStaticLayout mContentLayout;
    private ClickableItem mCurrentClickableItem;
    private String mDate;
    private PositionedStaticLayout mDateLayout;
    private boolean mIsFlagged;
    private PositionedStaticLayout mNameLayout;
    private OneUpListener mOneUpListener;
    private boolean mPlusOneByMe;
    private int mPlusOneCount;
    private String mPlusOneId;
    private boolean mPressed;
    private SetPressedRunnable mSetPressedRunnable;
    
    public StreamOneUpCommentView(Context context)
    {
        this(context, null);
    }

    public StreamOneUpCommentView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public StreamOneUpCommentView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mClickableItems = new HashSet();
        mContentDescriptionDirty = true;
        if(sNamePaint == null)
        {
            Resources resources = context.getResources();
            sPressedStateBackground = resources.getDrawable(R.drawable.list_selected_holo);
            sFontSpacing = resources.getDimension(R.dimen.stream_one_up_font_spacing);
            sAvatarSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_size);
            sMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_margin_top);
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_margin_right);
            sMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_margin_bottom);
            sAvatarMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_avatar_margin_top);
            sAvatarMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_avatar_margin_right);
            sNameMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_name_margin_right);
            sDividerThickness = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_divider_thickness);
            sPlusOneColor = resources.getColor(R.color.stream_one_up_comment_plus_one);
            sPlusOneInverseColor = resources.getColor(R.color.stream_one_up_comment_plus_one_inverse);
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(getContext(), true);
            sAvatarOverlayBitmap = ImageUtils.decodeResource(resources, R.drawable.bg_taco_avatar);
            TextPaint textpaint = new TextPaint();
            sNamePaint = textpaint;
            textpaint.setAntiAlias(true);
            sNamePaint.setTypeface(Typeface.DEFAULT_BOLD);
            sNamePaint.setColor(resources.getColor(R.color.stream_one_up_comment_name));
            sNamePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_comment_name_text_size));
            TextPaintUtils.registerTextPaint(sNamePaint, R.dimen.stream_one_up_comment_name_text_size);
            TextPaint textpaint1 = new TextPaint();
            sDatePaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sDatePaint.setColor(resources.getColor(R.color.stream_one_up_comment_date));
            sDatePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_comment_date_text_size));
            TextPaintUtils.registerTextPaint(sDatePaint, R.dimen.stream_one_up_comment_date_text_size);
            TextPaint textpaint2 = new TextPaint();
            sContentPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sContentPaint.setColor(resources.getColor(R.color.stream_one_up_comment_body));
            sContentPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sContentPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_comment_content_text_size));
            TextPaintUtils.registerTextPaint(sContentPaint, R.dimen.stream_one_up_comment_content_text_size);
            TextPaint textpaint3 = new TextPaint();
            sPlusOnePaint = textpaint3;
            textpaint3.setTypeface(Typeface.DEFAULT_BOLD);
            sPlusOnePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_comment_plus_one_text_size));
            TextPaintUtils.registerTextPaint(sPlusOnePaint, R.dimen.stream_one_up_comment_plus_one_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sBackgroundFadePaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_list_background_fade));
            sBackgroundFadePaint.setStyle(android.graphics.Paint.Style.FILL);
            sFlaggedCommentFadeArea = new Rect();
            Paint paint2 = new Paint();
            sDividerPaint = paint2;
            paint2.setColor(resources.getColor(R.color.stream_one_up_comment_divider));
            sDividerPaint.setStyle(android.graphics.Paint.Style.STROKE);
            sDividerPaint.setStrokeWidth(sDividerThickness);
            sResizePaint = new Paint(2);
        }
    }

    private void removeSetPressedRunnable()
    {
        if(mSetPressedRunnable != null)
            removeCallbacks(mSetPressedRunnable);
    }

    public final void bind(Cursor cursor, boolean flag)
    {
        setAuthor(cursor.getString(2), cursor.getString(3), EsAvatarData.uncompressAvatarUrl(cursor.getString(4)));
        setComment(cursor.getString(5), cursor.getString(6), flag);
        setDate(cursor.getLong(7));
        setPlusOne(cursor.getBlob(8));
        invalidate();
        requestLayout();
    }

    public final void bindResources()
    {
        if(ViewUtils.isViewAttached(this) && mAuthorImage != null)
            mAuthorImage.bindResources();
    }

    public final void cancelPressedState()
    {
        if(mPressed)
        {
            mPressed = false;
            invalidate();
        }
        removeSetPressedRunnable();
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent) {
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

	            if(mSetPressedRunnable == null)
	                mSetPressedRunnable = new SetPressedRunnable();
	            postDelayed(mSetPressedRunnable, ViewConfiguration.getTapTimeout());
	            flag = false;
	        	break;
	        case 1:
	        	 mCurrentClickableItem = null;
	             mPressed = false;
	             removeSetPressedRunnable();
	             for(Iterator iterator = mClickableItems.iterator(); iterator.hasNext(); ((ClickableItem)iterator.next()).handleEvent(i, j, 1));
	             invalidate();
	             flag = false;
	        	break;
	        case 2:
	        	if(i < 0 || i >= getWidth() || j < 0 || j >= getHeight())
	                removeSetPressedRunnable();
	            flag = false;
	        	break;
	        case 3:
	        	boolean flag1 = mPressed;
	            mPressed = false;
	            removeSetPressedRunnable();
	            if(mCurrentClickableItem != null)
	            {
	                mCurrentClickableItem.handleEvent(i, j, 3);
	                mCurrentClickableItem = null;
	                invalidate();
	            } else
	            {
	                if(flag1)
	                    invalidate();
	                flag = false;
	            }
	        	break;
        	default:
        		flag = false;
        		break;
        }
        return flag;
    }

    public final String getAuthorId()
    {
        return mAuthorId;
    }

    public final String getCommentContent()
    {
        return mCommentContent;
    }

    public final String getCommentId()
    {
        return mCommentId;
    }

    public final boolean getPlusOneByMe()
    {
        return mPlusOneByMe;
    }

    public final int getPlusOneCount()
    {
        return mPlusOneCount;
    }

    public final String getPlusOneId()
    {
        return mPlusOneId;
    }

    public void invalidate()
    {
        super.invalidate();
        if(mContentDescriptionDirty)
        {
            StringBuffer stringbuffer = new StringBuffer(256);
            if(mAuthorName != null)
                stringbuffer.append(mAuthorName).append('\n');
            if(mCommentContent != null)
                stringbuffer.append(mCommentContent).append('\n');
            if(mDate != null)
                stringbuffer.append(mDate).append('\n');
            if(mPlusOneCount > 0)
                stringbuffer.append('+').append(mPlusOneCount);
            setContentDescription(stringbuffer.toString());
            mContentDescriptionDirty = false;
        }
    }

    public final boolean isFlagged()
    {
        return mIsFlagged;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        bindResources();
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        unbindResources();
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int i = getWidth();
        int j = getHeight();
        canvas.drawRect(0.0F, 0.0F, i, j, sBackgroundPaint);
        if(mNameLayout != null)
        {
            Bitmap bitmap;
            int k;
            int l;
            int i1;
            int j1;
            int k1;
            int l1;
            if(mAuthorImage.getBitmap() != null)
                bitmap = mAuthorImage.getBitmap();
            else
                bitmap = sDefaultAvatarBitmap;
            canvas.drawBitmap(bitmap, null, mAuthorImage.getRect(), sResizePaint);
            canvas.drawBitmap(sAvatarOverlayBitmap, null, mAuthorImage.getRect(), sResizePaint);
            if(mAuthorImage.isClicked())
                mAuthorImage.drawSelectionRect(canvas);
            k = mNameLayout.getLeft();
            l = mNameLayout.getTop();
            canvas.translate(k, l);
            mNameLayout.draw(canvas);
            canvas.translate(-k, -l);
            i1 = mDateLayout.getLeft();
            j1 = mDateLayout.getTop();
            canvas.translate(i1, j1);
            mDateLayout.draw(canvas);
            canvas.translate(-i1, -j1);
            k1 = mContentLayout.getLeft();
            l1 = mContentLayout.getTop();
            canvas.translate(k1, l1);
            mContentLayout.draw(canvas);
            canvas.translate(-k1, -l1);
            if(mIsFlagged)
            {
                Rect rect = mAuthorImage.getRect();
                int i2 = Math.max(rect.bottom, mContentLayout.getBottom());
                sFlaggedCommentFadeArea.set(rect.left, rect.top, mContentLayout.getRight(), i2);
                canvas.drawRect(sFlaggedCommentFadeArea, sBackgroundFadePaint);
            }
        }
        if(mPressed)
        {
            sPressedStateBackground.setBounds(0, 0, i, j - sDividerThickness);
            sPressedStateBackground.draw(canvas);
        }
        canvas.drawLine(sMarginLeft, j - sDividerThickness, i - sMarginRight, j - sDividerThickness, sDividerPaint);
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = getPaddingLeft() + sMarginLeft;
        int l = getPaddingTop() + sMarginTop;
        int i1 = getMeasuredWidth();
        int j1 = i1 - k - getPaddingRight() - sMarginRight;
        mClickableItems.clear();
        mCurrentClickableItem = null;
        unbindResources();
        int k1 = l + sAvatarMarginTop;
        mAuthorImage = new ClickableAvatar(this, mAuthorId, mAuthorAvatarUrl, mAuthorName, mOneUpListener, 2);
        mClickableItems.add(mAuthorImage);
        mAuthorImage.setRect(k, k1, k + sAvatarSize, k1 + sAvatarSize);
        int l1 = k + (sAvatarSize + sAvatarMarginRight);
        int i2 = k1 - sAvatarMarginTop;
        int j2 = (int)sDatePaint.measureText(mDate);
        mDateLayout = new PositionedStaticLayout(mDate, sDatePaint, j2, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false);
        int k2 = j1 - sAvatarSize - sAvatarMarginRight - j2;
        CharSequence charsequence = TextUtils.ellipsize(mAuthorName, sNamePaint, k2, android.text.TextUtils.TruncateAt.END);
        int l2 = Math.min(j1 - sAvatarSize - sAvatarMarginRight - j2, (int)sNamePaint.measureText(charsequence, 0, charsequence.length()));
        mNameLayout = new PositionedStaticLayout(charsequence, sNamePaint, l2, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false);
        mNameLayout.setPosition(l1, i2);
        int i3 = l1 + (mNameLayout.getWidth() + sNameMarginRight);
        int j3 = i2 + (sDatePaint.getFontMetricsInt().ascent - sNamePaint.getFontMetricsInt().ascent);
        mDateLayout.setPosition(i3, j3);
        int k3 = j1 - sAvatarSize - sAvatarMarginRight;
        int l3 = k + sAvatarSize + sAvatarMarginRight;
        int i4 = l + mNameLayout.getHeight();
        android.text.SpannableStringBuilder spannablestringbuilder;
        int j4;
        int l4;
        ForegroundColorSpan foregroundcolorspan;
        int i5;
        int j5;
        if(mPlusOneCount > 0)
        {
            Resources resources = getResources();
            int k4 = R.string.stream_plus_one_count_with_plus;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(Math.max(mPlusOneCount, 1));
            String s = resources.getString(k4, aobj);
            android.text.SpannableStringBuilder spannablestringbuilder1 = ClickableStaticLayout.buildStateSpans((new StringBuilder()).append(mCommentContent).append(" &nbsp; ").append(s).toString());
            TextAppearanceSpan textappearancespan = new TextAppearanceSpan(null, 1, (int)sPlusOnePaint.getTextSize(), null, null);
            if(mPlusOneByMe)
                l4 = sPlusOneInverseColor;
            else
                l4 = sPlusOneColor;
            foregroundcolorspan = new ForegroundColorSpan(l4);
            i5 = spannablestringbuilder1.length() - s.length();
            j5 = spannablestringbuilder1.length();
            spannablestringbuilder1.setSpan(textappearancespan, i5, j5, 33);
            spannablestringbuilder1.setSpan(foregroundcolorspan, i5, j5, 33);
            spannablestringbuilder = spannablestringbuilder1;
        } else
        {
            spannablestringbuilder = ClickableStaticLayout.buildStateSpans(mCommentContent);
        }
        mClickableItems.remove(mContentLayout);
        mContentLayout = new ClickableStaticLayout(spannablestringbuilder, sContentPaint, k3, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mOneUpListener);
        mContentLayout.setPosition(l3, i4);
        mClickableItems.add(mContentLayout);
        j4 = i4 + mContentLayout.getHeight();
        bindResources();
        setMeasuredDimension(i1, Math.max(mAuthorImage.getRect().height(), j4) + sMarginBottom + sDividerThickness + getPaddingBottom());
        if(mOnMeasuredListener != null)
            mOnMeasuredListener.onMeasured(this);
    }

    public void onRecycle()
    {
        unbindResources();
        mNameLayout = null;
        mDateLayout = null;
        mContentLayout = null;
        mAuthorImage = null;
        mClickableItems.clear();
        mCurrentClickableItem = null;
        mCommentContent = null;
        mPlusOneId = null;
        mPlusOneByMe = false;
        mPlusOneCount = 0;
        mOneUpListener = null;
        mPressed = false;
    }

    public final void onResourceStatusChange(Resource resource)
    {
    }

    public void setAuthor(String s, String s1, String s2)
    {
        if(!TextUtils.equals(s, mAuthorId))
        {
            mAuthorId = s;
            mAuthorName = s1;
            mAuthorAvatarUrl = s2;
            if(mAuthorName == null)
            {
                mAuthorName = "";
                Log.w("StreamOneUp", (new StringBuilder("===> Author name was null for gaia id: ")).append(mAuthorId).toString());
            }
            if(mAuthorImage != null)
            {
                mAuthorImage.unbindResources();
                mAuthorImage = null;
            }
            mNameLayout = null;
            mAuthorImage = null;
            mContentDescriptionDirty = true;
        }
    }

    public void setComment(String s, String s1, boolean flag)
    {
        mCommentId = s;
        mCommentContent = s1;
        mIsFlagged = flag;
        mContentLayout = null;
        mContentDescriptionDirty = true;
    }

    public void setDate(long l)
    {
        mDate = Dates.getAbbreviatedRelativeTimeSpanString(getContext(), l).toString();
        mDateLayout = null;
        mContentDescriptionDirty = true;
    }

    public void setOneUpClickListener(OneUpListener oneuplistener)
    {
        mOneUpListener = oneuplistener;
    }

    public void setPlusOne(byte abyte0[])
    {
        if(abyte0 != null)
        {
            DbPlusOneData dbplusonedata = DbPlusOneData.deserialize(abyte0);
            mPlusOneId = dbplusonedata.getId();
            mPlusOneByMe = dbplusonedata.isPlusOnedByMe();
            mPlusOneCount = dbplusonedata.getCount();
        } else
        {
            mPlusOneId = null;
            mPlusOneByMe = false;
            mPlusOneCount = 0;
        }
        mContentDescriptionDirty = true;
    }

    public final void unbindResources()
    {
        if(mAuthorImage != null)
            mAuthorImage.unbindResources();
    }
    
    private final class SetPressedRunnable implements Runnable {

	    public final void run()
	    {
	        mPressed = true;
	        invalidate();
	    }
    }
}
