/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.DbPlusOneData;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.android.ui.view.ClickableButton.ClickableButtonListener;
import com.galaxy.meetup.client.util.AccessibilityUtils;
import com.galaxy.meetup.client.util.Dates;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.PlusBarUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.client.util.TouchExplorationHelper;
import com.galaxy.meetup.client.util.ViewUtils;
import com.galaxy.meetup.server.client.domain.DataPlusOne;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class PhotoOneUpInfoView extends OneUpBaseView implements
		ResourceConsumer, ClickableButtonListener, Recyclable {

	
	private static Paint sActionBarBackgroundPaint;
    private static int sAvatarMarginLeft;
    private static int sAvatarMarginRight;
    private static int sAvatarMarginTop;
    private static Bitmap sAvatarOverlayBitmap;
    private static int sAvatarSize;
    private static Paint sBackgroundPaint;
    private static int sCaptionMarginTop;
    private static TextPaint sContentPaint;
    private static TextPaint sDatePaint;
    private static Bitmap sDefaultAvatarBitmap;
    private static float sFontSpacing;
    private static int sMarginBottom;
    private static int sMarginLeft;
    private static int sMarginRight;
    private static int sNameMarginTop;
    private static TextPaint sNamePaint;
    private static int sPlusOneButtonMarginLeft;
    private static int sPlusOneButtonMarginRight;
    private static Paint sResizePaint;
    private String mAlbumId;
    private ClickableAvatar mAuthorImage;
    private PositionedStaticLayout mAuthorLayout;
    private int mBackgroundOffset;
    private Spannable mCaption;
    private ClickableStaticLayout mCaptionLayout;
    private Set mClickableItems;
    private boolean mContentDescriptionDirty;
    private ClickableItem mCurrentClickableItem;
    private String mDate;
    private ClickableStaticLayout mDateLayout;
    private OneUpListener mOneUpListener;
    private String mOwnerId;
    private String mOwnerName;
    protected ClickableButton mPlusOneButton;
    private DbPlusOneData mPlusOneData;
    private OneUpActivityTouchExplorer mTouchExplorer;
    
    
    public PhotoOneUpInfoView(Context context)
    {
        super(context);
        mClickableItems = new HashSet();
        mContentDescriptionDirty = true;
        if(sNamePaint == null)
        {
            Resources resources = getContext().getResources();
            sFontSpacing = resources.getDimension(R.dimen.stream_one_up_font_spacing);
            sAvatarSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_size);
            sMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_bottom);
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_right);
            sAvatarMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_top);
            sAvatarMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_left);
            sAvatarMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_right);
            sNameMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_name_margin_top);
            sCaptionMarginTop = resources.getDimensionPixelOffset(R.dimen.photo_one_up_caption_margin_top);
            int i = resources.getDimensionPixelOffset(R.dimen.stream_one_up_plus_one_button_margin_right);
            sPlusOneButtonMarginLeft = i;
            sPlusOneButtonMarginRight = i;
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(getContext(), true);
            sAvatarOverlayBitmap = ImageUtils.decodeResource(resources, R.drawable.bg_taco_avatar);
            TextPaint textpaint = new TextPaint();
            sNamePaint = textpaint;
            textpaint.setAntiAlias(true);
            sNamePaint.setTypeface(Typeface.DEFAULT_BOLD);
            sNamePaint.setColor(resources.getColor(R.color.stream_one_up_name));
            sNamePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_name_text_size));
            TextPaintUtils.registerTextPaint(sNamePaint, R.dimen.stream_one_up_name_text_size);
            TextPaint textpaint1 = new TextPaint();
            sDatePaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sDatePaint.setColor(resources.getColor(R.color.stream_one_up_date));
            sDatePaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sDatePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_date_text_size));
            TextPaintUtils.registerTextPaint(sDatePaint, R.dimen.stream_one_up_date_text_size);
            TextPaint textpaint2 = new TextPaint();
            sContentPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sContentPaint.setColor(resources.getColor(R.color.stream_one_up_content));
            sContentPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sContentPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_content_text_size));
            TextPaintUtils.registerTextPaint(sContentPaint, R.dimen.stream_one_up_content_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sActionBarBackgroundPaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_action_bar_background));
            sActionBarBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            sResizePaint = new Paint(2);
        }
        setupAccessibility(getContext());
    }

    public PhotoOneUpInfoView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mClickableItems = new HashSet();
        mContentDescriptionDirty = true;
        if(sNamePaint == null)
        {
            Resources resources = getContext().getResources();
            sFontSpacing = resources.getDimension(R.dimen.stream_one_up_font_spacing);
            sAvatarSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_size);
            sMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_bottom);
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_right);
            sAvatarMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_top);
            sAvatarMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_left);
            sAvatarMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_right);
            sNameMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_name_margin_top);
            sCaptionMarginTop = resources.getDimensionPixelOffset(R.dimen.photo_one_up_caption_margin_top);
            int i = resources.getDimensionPixelOffset(R.dimen.stream_one_up_plus_one_button_margin_right);
            sPlusOneButtonMarginLeft = i;
            sPlusOneButtonMarginRight = i;
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(getContext(), true);
            sAvatarOverlayBitmap = ImageUtils.decodeResource(resources, R.drawable.bg_taco_avatar);
            TextPaint textpaint = new TextPaint();
            sNamePaint = textpaint;
            textpaint.setAntiAlias(true);
            sNamePaint.setTypeface(Typeface.DEFAULT_BOLD);
            sNamePaint.setColor(resources.getColor(R.color.stream_one_up_name));
            sNamePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_name_text_size));
            TextPaintUtils.registerTextPaint(sNamePaint, R.dimen.stream_one_up_name_text_size);
            TextPaint textpaint1 = new TextPaint();
            sDatePaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sDatePaint.setColor(resources.getColor(R.color.stream_one_up_date));
            sDatePaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sDatePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_date_text_size));
            TextPaintUtils.registerTextPaint(sDatePaint, R.dimen.stream_one_up_date_text_size);
            TextPaint textpaint2 = new TextPaint();
            sContentPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sContentPaint.setColor(resources.getColor(R.color.stream_one_up_content));
            sContentPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sContentPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_content_text_size));
            TextPaintUtils.registerTextPaint(sContentPaint, R.dimen.stream_one_up_content_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sActionBarBackgroundPaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_action_bar_background));
            sActionBarBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            sResizePaint = new Paint(2);
        }
        setupAccessibility(getContext());
    }

    public PhotoOneUpInfoView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mClickableItems = new HashSet();
        mContentDescriptionDirty = true;
        if(sNamePaint == null)
        {
            Resources resources = getContext().getResources();
            sFontSpacing = resources.getDimension(R.dimen.stream_one_up_font_spacing);
            sAvatarSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_size);
            sMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_bottom);
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_right);
            sAvatarMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_top);
            sAvatarMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_left);
            sAvatarMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_right);
            sNameMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_name_margin_top);
            sCaptionMarginTop = resources.getDimensionPixelOffset(R.dimen.photo_one_up_caption_margin_top);
            int j = resources.getDimensionPixelOffset(R.dimen.stream_one_up_plus_one_button_margin_right);
            sPlusOneButtonMarginLeft = j;
            sPlusOneButtonMarginRight = j;
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(getContext(), true);
            sAvatarOverlayBitmap = ImageUtils.decodeResource(resources, R.drawable.bg_taco_avatar);
            TextPaint textpaint = new TextPaint();
            sNamePaint = textpaint;
            textpaint.setAntiAlias(true);
            sNamePaint.setTypeface(Typeface.DEFAULT_BOLD);
            sNamePaint.setColor(resources.getColor(R.color.stream_one_up_name));
            sNamePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_name_text_size));
            TextPaintUtils.registerTextPaint(sNamePaint, R.dimen.stream_one_up_name_text_size);
            TextPaint textpaint1 = new TextPaint();
            sDatePaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sDatePaint.setColor(resources.getColor(R.color.stream_one_up_date));
            sDatePaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sDatePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_date_text_size));
            TextPaintUtils.registerTextPaint(sDatePaint, R.dimen.stream_one_up_date_text_size);
            TextPaint textpaint2 = new TextPaint();
            sContentPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sContentPaint.setColor(resources.getColor(R.color.stream_one_up_content));
            sContentPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sContentPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_content_text_size));
            TextPaintUtils.registerTextPaint(sContentPaint, R.dimen.stream_one_up_content_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sActionBarBackgroundPaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_action_bar_background));
            sActionBarBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            sResizePaint = new Paint(2);
        }
        setupAccessibility(getContext());
    }

    private void setupAccessibility(Context context)
    {
        if(android.os.Build.VERSION.SDK_INT >= 16 && AccessibilityUtils.isAccessibilityEnabled(context) && mTouchExplorer == null)
        {
            mTouchExplorer = new OneUpActivityTouchExplorer(context);
            mTouchExplorer.install(this);
        }
    }

    private void updateAccessibility()
    {
        if(mTouchExplorer != null)
        {
            mTouchExplorer.invalidateItemCache();
            mTouchExplorer.invalidateParent();
        }
    }

    public final void bindResources()
    {
        if(ViewUtils.isViewAttached(this) && mAuthorImage != null)
            mAuthorImage.bindResources();
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent)
    {
        boolean flag = true;
        int i = (int)motionevent.getX();
        int j = (int)motionevent.getY();
        switch(motionevent.getAction()) {
	        case 0:
	        	Iterator iterator1 = mClickableItems.iterator();
	            do
	            {
	                ClickableItem clickableitem = null;
	                do
	                {
	                    if(!iterator1.hasNext())
	                        break;
	                    clickableitem = (ClickableItem)iterator1.next();
	                } while(!clickableitem.handleEvent(i, j, 0));
	                mCurrentClickableItem = clickableitem;
	                invalidate();
	            } while(true);
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

    public void invalidate()
    {
        super.invalidate();
        if(mContentDescriptionDirty)
        {
            if(android.os.Build.VERSION.SDK_INT < 16)
            {
                StringBuilder stringbuilder = new StringBuilder();
                AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mOwnerName);
                AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mDate);
                AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mCaption);
                if(mPlusOneData == null || !mPlusOneData.isPlusOnedByMe());
                stringbuilder.append((String)null);
                setContentDescription(stringbuilder.toString());
                setFocusable(true);
            }
            mContentDescriptionDirty = false;
        }
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        bindResources();
        updateAccessibility();
    }

    public final void onClickableButtonListenerClick(ClickableButton clickablebutton)
    {
        if(mOneUpListener != null && clickablebutton == mPlusOneButton)
        {
            mOneUpListener.onPlusOne(mAlbumId, mPlusOneData);
            if(AccessibilityUtils.isAccessibilityEnabled(getContext()))
            {
                boolean flag;
                int i;
                AccessibilityEvent accessibilityevent;
                if(mPlusOneData != null && mPlusOneData.isPlusOnedByMe())
                    flag = true;
                else
                    flag = false;
                if(flag)
                    i = R.string.plus_one_removed_confirmation;
                else
                    i = R.string.plus_one_added_confirmation;
                accessibilityevent = AccessibilityEvent.obtain(16384);
                accessibilityevent.getText().add(getResources().getString(i));
                onInitializeAccessibilityEvent(accessibilityevent);
                accessibilityevent.setContentDescription(null);
                getParent().requestSendAccessibilityEvent(this, accessibilityevent);
            }
        }
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        unbindResources();
        if(mTouchExplorer != null)
        {
            mTouchExplorer.uninstall();
            mTouchExplorer = null;
        }
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawRect(0.0F, mBackgroundOffset, getWidth(), getHeight(), sBackgroundPaint);
        Bitmap bitmap;
        int i;
        int j;
        if(mAuthorImage.getBitmap() != null)
            bitmap = mAuthorImage.getBitmap();
        else
            bitmap = sDefaultAvatarBitmap;
        canvas.drawBitmap(bitmap, null, mAuthorImage.getRect(), sResizePaint);
        canvas.drawBitmap(sAvatarOverlayBitmap, null, mAuthorImage.getRect(), sResizePaint);
        if(mAuthorImage.isClicked())
            mAuthorImage.drawSelectionRect(canvas);
        mPlusOneButton.draw(canvas);
        if(mDateLayout != null)
        {
            int i1 = mDateLayout.getLeft();
            int j1 = mDateLayout.getTop();
            canvas.translate(i1, j1);
            mDateLayout.draw(canvas);
            canvas.translate(-i1, -j1);
        }
        i = mAuthorLayout.getLeft();
        j = mAuthorLayout.getTop();
        canvas.translate(i, j);
        mAuthorLayout.draw(canvas);
        canvas.translate(-i, -j);
        if(mCaptionLayout != null)
        {
            int k = mCaptionLayout.getLeft();
            int l = mCaptionLayout.getTop();
            canvas.translate(k, l);
            mCaptionLayout.draw(canvas);
            canvas.translate(-k, -l);
        }
        updateAccessibility();
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = getPaddingLeft() + sMarginLeft;
        int l = getPaddingTop() - sAvatarMarginTop;
        int i1 = getMeasuredWidth();
        int j1 = i1 - k - getPaddingRight() - sMarginRight;
        mBackgroundOffset = l;
        Context context = getContext();
        int k1 = k + sAvatarMarginLeft;
        int l1 = l + sAvatarMarginTop;
        mAuthorImage.setRect(k1, l1, k1 + sAvatarSize, l1 + sAvatarSize);
        boolean flag;
        int i2;
        Resources resources;
        int j2;
        Object aobj[];
        String s;
        int k2;
        int l2;
        TextPaint textpaint;
        android.graphics.drawable.NinePatchDrawable ninepatchdrawable;
        android.graphics.drawable.NinePatchDrawable ninepatchdrawable1;
        int i3;
        int j3;
        int k3;
        int l3;
        int i4;
        int j4;
        int k4;
        if(mPlusOneData != null && mPlusOneData.isPlusOnedByMe())
            flag = true;
        else
            flag = false;
        if(mPlusOneData == null)
            i2 = 1;
        else
            i2 = mPlusOneData.getCount();
        resources = getResources();
        j2 = R.string.stream_plus_one_count_with_plus;
        aobj = new Object[1];
        aobj[0] = Integer.valueOf(Math.max(i2, 1));
        s = resources.getString(j2, aobj);
        k2 = (k + j1) - sPlusOneButtonMarginRight;
        l2 = l + sNameMarginTop;
        mClickableItems.remove(mPlusOneButton);
        if(flag)
            textpaint = PlusBarUtils.sPlusOnedTextPaint;
        else
            textpaint = PlusBarUtils.sNotPlusOnedTextPaint;
        if(flag)
            ninepatchdrawable = PlusBarUtils.sPlusOnedDrawable;
        else
            ninepatchdrawable = PlusBarUtils.sButtonDrawable;
        if(flag)
            ninepatchdrawable1 = PlusBarUtils.sPlusOnedPressedDrawable;
        else
            ninepatchdrawable1 = PlusBarUtils.sButtonPressedDrawable;
        mPlusOneButton = new ClickableButton(context, s, textpaint, ninepatchdrawable, ninepatchdrawable1, this, 0, 0);
        i3 = k2 - mPlusOneButton.getRect().width();
        mPlusOneButton.getRect().offsetTo(i3, l2);
        mClickableItems.add(mPlusOneButton);
        j3 = k + sAvatarMarginLeft + sAvatarSize + sAvatarMarginRight;
        k3 = l + sNameMarginTop;
        l3 = j1 - j3 - mPlusOneButton.getRect().width() - sPlusOneButtonMarginLeft;
        mAuthorLayout = new PositionedStaticLayout(TextUtils.ellipsize(mOwnerName, sNamePaint, l3, android.text.TextUtils.TruncateAt.END), sNamePaint, l3, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false);
        mAuthorLayout.setPosition(j3, k3);
        i4 = k3 + mAuthorLayout.getHeight();
        mClickableItems.remove(mDateLayout);
        if(mDate != null)
        {
            java.util.Locale locale = getContext().getResources().getConfiguration().locale;
            SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder(mDate.toUpperCase(locale));
            spannablestringbuilder.append(" ");
            mDateLayout = new ClickableStaticLayout(spannablestringbuilder, sDatePaint, l3, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, null);
            mDateLayout.setPosition(j3, i4);
            mClickableItems.add(mDateLayout);
            j4 = i4 + mDateLayout.getHeight();
        } else
        {
            j4 = i4;
        }
        k4 = Math.max(sAvatarSize, j4 - l);
        if(!TextUtils.isEmpty(mCaption))
        {
            int l4 = k4 + sCaptionMarginTop;
            mClickableItems.remove(mCaptionLayout);
            mCaptionLayout = new ClickableStaticLayout(mCaption, sContentPaint, j1, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mOneUpListener);
            mCaptionLayout.setPosition(k, l4);
            mClickableItems.add(mCaptionLayout);
            k4 = mCaptionLayout.getBottom();
        }
        setMeasuredDimension(i1, k4 + sMarginBottom + getPaddingBottom());
        if(mOnMeasuredListener != null)
            mOnMeasuredListener.onMeasured(this);
        if(mTouchExplorer != null)
            mTouchExplorer.invalidateItemCache();
    }

    public void onRecycle()
    {
        unbindResources();
        mAuthorLayout = null;
        mDateLayout = null;
        mCaptionLayout = null;
        mAuthorImage = null;
        mClickableItems.clear();
        mCurrentClickableItem = null;
        mPlusOneData = null;
        mCaption = null;
        mOneUpListener = null;
    }

    public final void onResourceStatusChange(Resource resource)
    {
    }

    public void setAlbum(String s)
    {
        mAlbumId = s;
    }

    public void setCaption(String s)
    {
        mCaption = null;
        if(!TextUtils.isEmpty(s))
            mCaption = ClickableStaticLayout.buildStateSpans(s);
        mContentDescriptionDirty = true;
    }

    public void setDate(long l)
    {
        mDate = Dates.getAbbreviatedRelativeTimeSpanString(getContext(), l).toString();
        mContentDescriptionDirty = true;
    }

    public void setOneUpClickListener(OneUpListener oneuplistener)
    {
        mOneUpListener = oneuplistener;
    }

    public void setOwner(String s, String s1, String s2)
    {
        if(!TextUtils.equals(mOwnerId, s))
        {
            unbindResources();
            mOwnerId = s;
            mOwnerName = s1;
            if(mOwnerName == null)
            {
                mOwnerName = "";
                Log.w("PhotoOneUp", (new StringBuilder("===> Author name was null for gaia id: ")).append(s).toString());
            }
            if(mAuthorImage != null)
                mClickableItems.remove(mAuthorImage);
            mAuthorImage = new ClickableAvatar(this, mOwnerId, s2, mOwnerName, mOneUpListener, 2);
            mClickableItems.add(mAuthorImage);
            mContentDescriptionDirty = true;
            bindResources();
        }
    }

    public void setPlusOne(byte abyte0[])
    {
        mPlusOneData = null;
        if(abyte0 != null)
        {
            DataPlusOne dataplusone = (DataPlusOne)JsonUtil.fromByteArray(abyte0, DataPlusOne.class);
            if(dataplusone.globalCount != null && dataplusone.isPlusonedByViewer != null)
                mPlusOneData = new DbPlusOneData(null, dataplusone.globalCount.intValue(), dataplusone.isPlusonedByViewer.booleanValue());
        }
        mContentDescriptionDirty = true;
    }

    public final void unbindResources()
    {
        if(mAuthorImage != null)
            mAuthorImage.unbindResources();
    }
	
	private final class OneUpActivityTouchExplorer extends TouchExplorationHelper
    {

        private void refreshItemCache()
        {
            if(mIsItemCacheStale)
            {
                mItemCache.clear();
                mItemCache.addAll(mClickableItems);
                Collections.sort(mItemCache, ClickableItem.sComparator);
                mIsItemCacheStale = false;
            }
        }

        protected final int getIdForItem(Object obj)
        {
            ClickableItem clickableitem = (ClickableItem)obj;
            refreshItemCache();
            return mItemCache.indexOf(clickableitem);
        }

        protected final Object getItemAt(float f, float f1)
        {
            refreshItemCache();
            ClickableItem clickableitem;
            int size = mItemCache.size();
            for(int j = 0; j < size; j++) {
            	clickableitem = (ClickableItem)mItemCache.get(j);
            	if(clickableitem.getRect().contains((int)f, (int)f1)) 
            		return clickableitem; 
            }
            return null;
        }

        protected final Object getItemForId(int i)
        {
            ClickableItem clickableitem;
            if(i >= 0 && i < mItemCache.size())
            {
                refreshDrawableState();
                clickableitem = (ClickableItem)mItemCache.get(i);
            } else
            {
                clickableitem = null;
            }
            return clickableitem;
        }

        protected final void getVisibleItems(List list)
        {
            refreshItemCache();
            int i = 0;
            for(int j = mItemCache.size(); i < j; i++)
                list.add((ClickableItem)mItemCache.get(i));

        }

        public final void invalidateItemCache()
        {
            mIsItemCacheStale = true;
        }

        protected final boolean performActionForItem(Object obj, int i, Bundle bundle)
        {
            boolean flag = true;
            ClickableItem clickableitem = (ClickableItem)obj;
            if(i == 16)
            {
                clickableitem.handleEvent(clickableitem.getRect().centerX(), clickableitem.getRect().centerY(), 0);
                clickableitem.handleEvent(clickableitem.getRect().centerX(), clickableitem.getRect().centerY(), 1);
            } else
            {
                flag = false;
            }
            return flag;
        }

        protected final void populateEventForItem(Object obj, AccessibilityEvent accessibilityevent)
        {
            accessibilityevent.setContentDescription(((ClickableItem)obj).getContentDescription());
        }

        protected final void populateNodeForItem(Object obj, AccessibilityNodeInfoCompat accessibilitynodeinfocompat)
        {
            ClickableItem clickableitem = (ClickableItem)obj;
            accessibilitynodeinfocompat.setBoundsInParent(clickableitem.getRect());
            accessibilitynodeinfocompat.addAction(16);
            accessibilitynodeinfocompat.setText(clickableitem.getContentDescription());
        }

        private boolean mIsItemCacheStale;
        private ArrayList mItemCache;

        public OneUpActivityTouchExplorer(Context context)
        {
            super(context);
            mIsItemCacheStale = true;
            mItemCache = new ArrayList(mClickableItems.size());
        }
    }
}
