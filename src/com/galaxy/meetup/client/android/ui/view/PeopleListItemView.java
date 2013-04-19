/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.AvatarRequest;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageCache.ImageConsumer;
import com.galaxy.meetup.client.android.service.ImageCache.OnAvatarChangeListener;
import com.galaxy.meetup.client.android.ui.fragments.CircleNameResolver;
import com.galaxy.meetup.client.util.SpannableUtils;

/**
 * 
 * @author sihai
 *
 */
public class PeopleListItemView extends CheckableListItemView implements
		OnClickListener, ImageConsumer, OnAvatarChangeListener, Recyclable {

	private static Drawable sAddButtonIcon;
    private static Bitmap sDefaultUserImage;
    private static int sMediumAvatarSize;
    private static Drawable sRemoveButtonIcon;
    private static int sSmallAvatarSize;
    private static int sTinyAvatarSize;
    private static Drawable sUnblockButtonIcon;
    private static Drawable sVerticalDivider;
    private static Drawable sWellFormedEmailIcon;
    private static Drawable sWellFormedSmsIcon;
    private TextView mActionButton;
    private final int mActionButtonResourceId;
    private boolean mActionButtonVisible;
    private final int mActionButtonWidth;
    private ImageView mAddButton;
    private boolean mAddButtonVisible;
    private Bitmap mAvatarBitmap;
    private final Rect mAvatarBounds;
    private final ImageCache mAvatarCache;
    private boolean mAvatarInvalidated;
    private final Rect mAvatarOriginalBounds;
    private final Paint mAvatarPaint;
    private AvatarRequest mAvatarRequest;
    private int mAvatarRequestSize;
    private final int mAvatarSize;
    private String mAvatarUrl;
    private boolean mAvatarVisible;
    private final Drawable mCircleIconDrawable;
    private final int mCircleIconSize;
    private boolean mCircleIconVisible;
    private int mCircleLineHeight;
    private boolean mCircleListVisible;
    private CircleNameResolver mCircleNameResolver;
    private final int mCirclesTextColor;
    private final float mCirclesTextSize;
    private final TextView mCirclesTextView;
    private String mContactLookupKey;
    private Bitmap mDefaultAvatarBitmap;
    private String mDisplayName;
    private final int mEmailIconPaddingLeft;
    private final int mEmailIconPaddingTop;
    private final SpannableStringBuilder mEmailTextBuilder;
    private boolean mFirstRow;
    private String mGaiaId;
    private final int mGapBetweenIconAndCircles;
    private final int mGapBetweenImageAndText;
    private final int mGapBetweenNameAndCircles;
    private final int mGapBetweenTextAndButton;
    private String mHighlightedText;
    private OnActionButtonClickListener mListener;
    private final SpannableStringBuilder mNameTextBuilder;
    private final TextView mNameTextView;
    private final int mPaddingBottom;
    private final int mPaddingLeft;
    private final int mPaddingRight;
    private final int mPaddingTop;
    private String mPersonId;
    private boolean mPlusPage;
    private final int mPreferredHeight;
    private ImageView mRemoveButton;
    private boolean mRemoveButtonVisible;
    protected SectionHeaderView mSectionHeader;
    protected int mSectionHeaderHeight;
    protected boolean mSectionHeaderVisible;
    private TextView mTypeTextView;
    private boolean mTypeTextViewVisible;
    private ImageView mUnblockButton;
    private boolean mUnblockButtonVisible;
    private int mVerticalDividerLeft;
    private final int mVerticalDividerPadding;
    private final int mVerticalDividerWidth;
    private String mWellFormedEmail;
    private boolean mWellFormedEmailMode;
    private String mWellFormedSms;
    private boolean mWellFormedSmsMode;
    
    public PeopleListItemView(Context context) {
        this(context, null);
    }

    public PeopleListItemView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        mAvatarVisible = true;
        mFirstRow = true;
        mNameTextBuilder = new SpannableStringBuilder();
        mEmailTextBuilder = new SpannableStringBuilder();
        mAvatarOriginalBounds = new Rect();
        mAvatarBounds = new Rect();
        mAvatarCache = ImageCache.getInstance(context);
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.ContactListItemView);
        mPreferredHeight = typedarray.getDimensionPixelSize(0, 0);
        mPaddingTop = typedarray.getDimensionPixelOffset(1, 0);
        mPaddingBottom = typedarray.getDimensionPixelOffset(2, 0);
        mPaddingLeft = typedarray.getDimensionPixelOffset(3, 0);
        mPaddingRight = typedarray.getDimensionPixelOffset(4, 0);
        float f = typedarray.getFloat(6, 0.0F);
        mCirclesTextSize = typedarray.getFloat(9, 0.0F);
        mGapBetweenImageAndText = typedarray.getDimensionPixelOffset(5, 0);
        mCircleIconDrawable = typedarray.getDrawable(7);
        mCircleIconSize = typedarray.getDimensionPixelSize(8, 0);
        mGapBetweenNameAndCircles = typedarray.getDimensionPixelOffset(11, 0);
        mGapBetweenIconAndCircles = typedarray.getDimensionPixelOffset(12, 0);
        mGapBetweenTextAndButton = typedarray.getDimensionPixelOffset(13, 0);
        mActionButtonResourceId = typedarray.getResourceId(14, 0);
        mActionButtonWidth = typedarray.getDimensionPixelSize(15, 0);
        mVerticalDividerWidth = typedarray.getDimensionPixelSize(16, 0);
        mVerticalDividerPadding = typedarray.getDimensionPixelOffset(17, 0);
        mCirclesTextColor = typedarray.getColor(10, 0);
        mEmailIconPaddingTop = typedarray.getDimensionPixelOffset(18, 0);
        mEmailIconPaddingLeft = typedarray.getDimensionPixelOffset(19, 0);
        typedarray.recycle();
        mNameTextView = new TextView(context);
        mNameTextView.setSingleLine(true);
        mNameTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        mNameTextView.setTextAppearance(context, 0x1030044);
        mNameTextView.setTextSize(f);
        mNameTextView.setGravity(16);
        mNameTextView.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -2));
        addView(mNameTextView);
        mCirclesTextView = new TextView(context);
        mCirclesTextView.setSingleLine(true);
        mCirclesTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        mCirclesTextView.setTextAppearance(context, 0x1030044);
        mCirclesTextView.setTextSize(mCirclesTextSize);
        mCirclesTextView.setTextColor(mCirclesTextColor);
        mCirclesTextView.setGravity(16);
        mCirclesTextView.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -2));
        addView(mCirclesTextView);
        mAvatarPaint = new Paint();
        if(sDefaultUserImage == null)
            sDefaultUserImage = EsAvatarData.getMediumDefaultAvatar(context);
        mDefaultAvatarBitmap = sDefaultUserImage;
        if(sVerticalDivider == null)
            sVerticalDivider = context.getApplicationContext().getResources().getDrawable(R.drawable.divider);
        if(sWellFormedEmailIcon == null)
            sWellFormedEmailIcon = context.getApplicationContext().getResources().getDrawable(R.drawable.profile_email);
        if(sWellFormedSmsIcon == null)
            sWellFormedSmsIcon = context.getApplicationContext().getResources().getDrawable(R.drawable.profile_sms);
        mAvatarSize = Math.min(sDefaultUserImage.getWidth(), mPreferredHeight);
        if(sMediumAvatarSize == 0)
        {
            sMediumAvatarSize = EsAvatarData.getMediumAvatarSize(context);
            sSmallAvatarSize = EsAvatarData.getSmallAvatarSize(context);
            sTinyAvatarSize = EsAvatarData.getTinyAvatarSize(context);
        }
        if(mAvatarSize > sMediumAvatarSize)
        {
            mAvatarRequestSize = 2;
            mAvatarPaint.setFilterBitmap(true);
        } else
        if(mAvatarSize == sMediumAvatarSize)
            mAvatarRequestSize = 2;
        else
        if(mAvatarSize > sSmallAvatarSize)
        {
            mAvatarRequestSize = 2;
            mAvatarPaint.setFilterBitmap(true);
        } else
        if(mAvatarSize == sSmallAvatarSize)
            mAvatarRequestSize = 1;
        else
        if(mAvatarSize > sTinyAvatarSize)
        {
            mAvatarRequestSize = 1;
            mAvatarPaint.setFilterBitmap(true);
        } else
        if(mAvatarSize == sTinyAvatarSize)
        {
            mAvatarRequestSize = 0;
        } else
        {
            mAvatarRequestSize = 0;
            mAvatarPaint.setFilterBitmap(true);
        }
    }
    
    public static PeopleListItemView createInstance(Context context) {
    	
    	PeopleListItemView peoplelistitemview = null;
    	try {
	        if(android.os.Build.VERSION.SDK_INT < 11) {
	        	peoplelistitemview = new PeopleListItemView(context); 
	    	} else {
	    		peoplelistitemview = (PeopleListItemView)Class.forName("views.PeopleListItemViewV11").getConstructor(new Class[] {
	    	            Context.class
	    	        }).newInstance(new Object[] {
	    	            context
	    	        });
	        }
    	} catch (Exception e) {
    		Log.e("PeopleListItemView", "Cannot instantiate", e);
    	}
    	
    	return peoplelistitemview;
    }
    
    private void updateDisplayName() {
        if(mDisplayName != null) {
            SpannableUtils.setTextWithHighlight(mNameTextView, mDisplayName, mNameTextBuilder, mHighlightedText, sBoldSpan, sColorSpan);
        } else {
            mWellFormedEmailMode = true;
            mNameTextView.setText(mWellFormedEmail);
        }
    }
    
    public void dispatchDraw(Canvas canvas) {
        if(mWellFormedEmailMode)
        {
            int j2 = sWellFormedEmailIcon.getIntrinsicWidth();
            int k2 = sWellFormedEmailIcon.getIntrinsicHeight();
            int l2 = mAvatarBounds.left + (mAvatarSize - j2) / 2;
            int i3 = mAvatarBounds.top + (mAvatarSize - k2) / 2;
            sWellFormedEmailIcon.setBounds(l2, i3, l2 + j2, i3 + k2);
            sWellFormedEmailIcon.draw(canvas);
        } else
        if(mWellFormedSmsMode)
        {
            int j1 = sWellFormedSmsIcon.getIntrinsicWidth();
            int k1 = sWellFormedSmsIcon.getIntrinsicHeight();
            int l1 = mAvatarBounds.left + (mAvatarSize - j1) / 2;
            int i2 = mAvatarBounds.top + (mAvatarSize - k1) / 2;
            sWellFormedSmsIcon.setBounds(l1, i2, l1 + j1, i2 + k1);
            sWellFormedSmsIcon.draw(canvas);
        } else
        if(mFirstRow)
        {
        	int i;
            int j;
            int k;
            int l;
            Bitmap bitmap;
            
            if(mCircleIconVisible)
                mCircleIconDrawable.draw(canvas);
            if(mAvatarVisible)
            {
                if(mAvatarInvalidated && mAvatarRequest != null)
                {
                    mAvatarInvalidated = false;
                    mAvatarCache.refreshImage(this, mAvatarRequest);
                }
                if(mAvatarBitmap != null)
                    bitmap = mAvatarBitmap;
                else
                    bitmap = mDefaultAvatarBitmap;
                mAvatarOriginalBounds.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap, mAvatarOriginalBounds, mAvatarBounds, mAvatarPaint);
            }
            if(mContactLookupKey != null)
            {
                i = sWellFormedEmailIcon.getIntrinsicWidth();
                j = sWellFormedEmailIcon.getIntrinsicHeight();
                k = mNameTextView.getLeft() - i - mEmailIconPaddingLeft;
                l = mEmailIconPaddingTop;
                sWellFormedEmailIcon.setBounds(k, l, k + i, l + j);
                sWellFormedEmailIcon.draw(canvas);
            }
        }
        if(mActionButtonVisible || mAddButtonVisible || mRemoveButtonVisible || mUnblockButtonVisible)
        {
            int i1;
            if(mSectionHeaderVisible)
                i1 = mSectionHeaderHeight + mVerticalDividerPadding;
            else
                i1 = mVerticalDividerPadding;
            sVerticalDivider.setBounds(mVerticalDividerLeft, i1, mVerticalDividerLeft + mVerticalDividerWidth, getHeight() - mVerticalDividerPadding);
            sVerticalDivider.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }
    
    protected final void drawBackground(Canvas canvas, Drawable drawable)
    {
        int i;
        if(mSectionHeaderVisible)
            i = mSectionHeaderHeight;
        else
            i = 0;
        drawable.setBounds(0, i, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    public String getContactName()
    {
        return mDisplayName;
    }

    public String getGaiaId()
    {
        return mGaiaId;
    }

    public String getPersonId()
    {
        return mPersonId;
    }

    public String getWellFormedEmail()
    {
        return mWellFormedEmail;
    }

    public String getWellFormedSms()
    {
        return mWellFormedSms;
    }

    public boolean isPlusPage()
    {
        return mPlusPage;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ImageCache _tmp = mAvatarCache;
        ImageCache.registerAvatarChangeListener(this);
    }

    public void onAvatarChanged(String s)
    {
        if(s != null && s.equals(mGaiaId))
        {
            mAvatarInvalidated = true;
            invalidate();
        }
    }

    public void onClick(View view) {
        if(mListener == null) { 
        	return; 
        }
        if(view != mAddButton) {
        	if(view == mRemoveButton)
                mListener.onActionButtonClick(this, 1);
            else
            if(view == mUnblockButton)
                mListener.onActionButtonClick(this, 2);
            else
            if(view == mActionButton)
                mListener.onActionButtonClick(this, 3); 
        } else { 
        	mListener.onActionButtonClick(this, 0);
        }
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        ImageCache _tmp = mAvatarCache;
        ImageCache.unregisterAvatarChangeListener(this);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = l - j;
        boolean flag1 = mSectionHeaderVisible;
        int j1 = 0;
        if(flag1)
        {
            mSectionHeader.layout(0, 0, k - i, mSectionHeaderHeight);
            j1 = 0 + mSectionHeaderHeight;
        }
        int k1 = mPaddingLeft;
        if(mAvatarVisible)
        {
            mAvatarBounds.left = k1;
            mAvatarBounds.top = j1 + (i1 - j1 - mAvatarSize) / 2;
            mAvatarBounds.right = mAvatarBounds.left + mAvatarSize;
            mAvatarBounds.bottom = mAvatarBounds.top + mAvatarSize;
            k1 += mAvatarSize + mGapBetweenImageAndText;
        }
        int l1 = k - i - mPaddingRight;
        if(mActionButtonVisible)
        {
            int l10 = mActionButton.getMeasuredWidth();
            int i11 = l1 - l10;
            mVerticalDividerLeft = i11 - mVerticalDividerWidth;
            TextView textview6 = mActionButton;
            int j11 = i11 + l10;
            textview6.layout(i11, j1, j11, i1);
            l1 -= l10;
        }
        if(mUnblockButtonVisible)
        {
            int k10 = mUnblockButton.getMeasuredWidth();
            mVerticalDividerLeft = l1 - k10;
            mUnblockButton.layout(mVerticalDividerLeft + mVerticalDividerWidth, j1, l1, i1);
            l1 -= k10;
        }
        if(mRemoveButtonVisible)
        {
            int j10 = mRemoveButton.getMeasuredWidth();
            mVerticalDividerLeft = l1 - j10;
            mRemoveButton.layout(mVerticalDividerLeft + mVerticalDividerWidth, j1, l1, i1);
            l1 -= j10;
        }
        int i2;
        int k9 = mAddButton.getMeasuredWidth();
        int i7;
        int j7;
        TextView textview4;
        int k7;
        TextView textview5;
        int l7;
        int i8;
        int j8;
        int k8;
        int l8;
        CheckBox checkbox;
        int i9;
        int j9;
        int l9;
        ImageView imageview;
        int i10;
        if(mAddButtonVisible)
        {
            if(!mRemoveButtonVisible)
            {
                mVerticalDividerLeft = l1 - k9;
                l9 = mVerticalDividerLeft + mVerticalDividerWidth;
            } else
            {
                l9 = mVerticalDividerLeft - k9;
            }
            imageview = mAddButton;
            i10 = l9 + k9;
            imageview.layout(l9, j1, i10, i1);
            l1 -= k9;
        }
        if(mCheckBoxVisible)
        {
            j8 = mCheckBox.getMeasuredWidth();
            k8 = mCheckBox.getMeasuredHeight();
            l8 = j1 + (i1 - j1 - k8) / 2;
            checkbox = mCheckBox;
            i9 = l1 - j8;
            j9 = l8 + k8;
            checkbox.layout(i9, l8, l1, j9);
            l1 -= j8;
        }
        if(mActionButtonVisible || mRemoveButtonVisible || mAddButtonVisible || mUnblockButtonVisible || mCheckBoxVisible)
            l1 -= mGapBetweenTextAndButton;
        if(mTypeTextViewVisible)
            i2 = mTypeTextView.getMeasuredWidth();
        else
            i2 = 0;
        if(!mFirstRow)
        {
            i7 = mCirclesTextView.getMeasuredHeight();
            j7 = j1 + (i1 - j1 - i7) / 2;
            if(mTypeTextViewVisible)
            {
                textview5 = mTypeTextView;
                l7 = l1 - i2;
                i8 = j7 + i7;
                textview5.layout(l7, j7, l1, i8);
                l1 -= i2 + mGapBetweenIconAndCircles;
            }
            textview4 = mCirclesTextView;
            k7 = j7 + i7;
            textview4.layout(k1, j7, l1, k7);
        } else
        if(mCircleListVisible)
        {
            int i3 = mNameTextView.getMeasuredHeight();
            int j3 = mCirclesTextView.getMeasuredHeight();
            int k3 = j3;
            if(mCircleIconVisible)
                k3 = Math.max(mCircleIconSize, k3);
            int l3 = k3 + i3;
            int i4 = j1 + (mPreferredHeight - l3) / 2;
            int j4 = k1;
            if(mContactLookupKey != null)
                j4 += sWellFormedEmailIcon.getIntrinsicWidth() + mEmailIconPaddingLeft;
            TextView textview1 = mNameTextView;
            int k4 = i4 + i3;
            textview1.layout(j4, i4, l1, k4);
            int l4 = i4 + (i3 + mGapBetweenNameAndCircles);
            int i5 = k1;
            if(mCircleIconVisible)
            {
                int j6 = l4 + (mCircleLineHeight - mCircleIconSize) / 2;
                Drawable drawable = mCircleIconDrawable;
                int k6 = k1 + mCircleIconSize;
                int l6 = j6 + mCircleIconSize;
                drawable.setBounds(k1, j6, k6, l6);
                i5 += mCircleIconSize + mGapBetweenIconAndCircles;
            }
            int j5 = l4 + (mCircleLineHeight - j3) / 2;
            if(mTypeTextViewVisible)
            {
                TextView textview3 = mTypeTextView;
                int l5 = l1 - i2;
                int i6 = j5 + j3;
                textview3.layout(l5, j5, l1, i6);
                l1 -= i2 + mGapBetweenIconAndCircles;
            }
            TextView textview2 = mCirclesTextView;
            int k5 = j5 + j3;
            textview2.layout(i5, j5, l1, k5);
        } else
        {
            int j2 = mNameTextView.getMeasuredHeight();
            int k2 = j1 + (i1 - j1 - j2) / 2;
            TextView textview = mNameTextView;
            int l2 = k2 + j2;
            textview.layout(k1, k2, l1, l2);
        }
        if(mAvatarVisible && !mWellFormedEmailMode && !mWellFormedSmsMode && mAvatarBitmap == null && mAvatarRequest != null)
            mAvatarCache.loadImage(this, mAvatarRequest);
    }

    protected void onMeasure(int i, int j)
    {
        int k = resolveSize(0, i);
        int l = k - mPaddingLeft - mPaddingRight;
        if(mAvatarVisible)
            l -= mAvatarSize + mGapBetweenImageAndText;
        boolean flag = mActionButtonVisible;
        int i1 = 0;
        int j1 = 0;
        if(flag)
        {
            mActionButton.measure(0, j);
            i1 = mActionButton.getMeasuredWidth();
            j1 = Math.max(0, mActionButton.getMeasuredHeight());
            l -= i1 + mVerticalDividerWidth;
        }
        if(mAddButtonVisible)
        {
            mAddButton.measure(android.view.View.MeasureSpec.makeMeasureSpec(mActionButtonWidth, 0x40000000), j);
            j1 = Math.max(j1, mAddButton.getMeasuredHeight());
            l -= mActionButtonWidth + mVerticalDividerWidth;
        }
        if(mRemoveButtonVisible)
        {
            mRemoveButton.measure(android.view.View.MeasureSpec.makeMeasureSpec(mActionButtonWidth, 0x40000000), j);
            j1 = Math.max(j1, mRemoveButton.getMeasuredHeight());
            l -= mActionButtonWidth + mVerticalDividerWidth;
        }
        if(mUnblockButtonVisible)
        {
            mUnblockButton.measure(android.view.View.MeasureSpec.makeMeasureSpec(mActionButtonWidth, 0x40000000), j);
            j1 = Math.max(j1, mUnblockButton.getMeasuredHeight());
            l -= mActionButtonWidth + mVerticalDividerWidth;
        }
        if(mCheckBoxVisible)
        {
            mCheckBox.measure(0, j);
            Math.max(j1, mCheckBox.getMeasuredHeight());
            l -= mCheckBox.getMeasuredWidth();
        }
        if(mRemoveButtonVisible || mActionButtonVisible || mAddButtonVisible || mUnblockButtonVisible || mCheckBoxVisible)
            l -= mGapBetweenTextAndButton;
        int k1 = l;
        if(mContactLookupKey != null)
            k1 -= sWellFormedEmailIcon.getIntrinsicWidth() + mGapBetweenIconAndCircles;
        mNameTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k1, 0x40000000), j);
        int l1 = mAvatarSize;
        int i2 = l;
        if(mCircleIconVisible)
            i2 -= mCircleIconSize + mGapBetweenIconAndCircles;
        if(mTypeTextViewVisible)
        {
            mTypeTextView.measure(0, 0);
            i2 -= mTypeTextView.getMeasuredWidth() + mGapBetweenIconAndCircles;
        }
        if(mCircleListVisible)
        {
            mCirclesTextView.measure(0, 0);
            int k2 = Math.min(mCirclesTextView.getMeasuredWidth(), i2);
            mCircleLineHeight = Math.max(mCircleIconSize, mCirclesTextView.getMeasuredHeight());
            mCirclesTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k2, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(mCircleLineHeight, 0x40000000));
            l1 = Math.max(l1, mNameTextView.getMeasuredHeight() + mGapBetweenNameAndCircles + mCircleLineHeight);
        }
        int j2 = Math.max(l1 + (mPaddingTop + mPaddingBottom), mPreferredHeight);
        if(mActionButtonVisible)
            mActionButton.measure(android.view.View.MeasureSpec.makeMeasureSpec(i1, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(j2, 0x40000000));
        if(mSectionHeaderVisible)
        {
            mSectionHeader.measure(i, 0);
            mSectionHeaderHeight = mSectionHeader.getMeasuredHeight();
            j2 += mSectionHeaderHeight;
        }
        setMeasuredDimension(k, j2);
    }

    public void onRecycle()
    {
        mPersonId = null;
        mGaiaId = null;
        mContactLookupKey = null;
        mAvatarVisible = true;
        mAvatarRequest = null;
        mAvatarBitmap = null;
        mDisplayName = null;
        mPlusPage = false;
        mFirstRow = true;
        mWellFormedEmailMode = false;
        mWellFormedEmail = null;
        mWellFormedSmsMode = false;
        mWellFormedSms = null;
        mHighlightedText = null;
        mCirclesTextView.setText(null);
    }

    public void setActionButtonLabel(int i)
    {
        if(mActionButton == null)
        {
            mActionButton = new TextView(getContext());
            mActionButton.setBackgroundResource(mActionButtonResourceId);
            mActionButton.setGravity(17);
            mActionButton.setPadding(mVerticalDividerPadding, 0, mVerticalDividerPadding, 0);
            mActionButton.setOnClickListener(this);
            addView(mActionButton);
        }
        String s = getResources().getString(i);
        mActionButton.setText(s.toUpperCase());
    }
    
    public void setActionButtonVisible(boolean flag) {
    	if(mActionButtonVisible == flag) {
    		return;
    	}
    	
    	mActionButtonVisible = flag;
        if(mActionButtonVisible)
        {
            if(mActionButton == null)
                setActionButtonLabel(R.string.add);
            mActionButton.setVisibility(0);
        } else
        if(mActionButton != null)
            mActionButton.setVisibility(8);
    }
    
    public void setAddButtonVisible(boolean flag) {
        if(mAddButtonVisible == flag) { 
        	return; 
        }

        mAddButtonVisible = flag;
        if(mAddButtonVisible)
        {
            if(mAddButton == null)
            {
                mAddButton = new ImageView(getContext());
                mAddButton.setBackgroundResource(mActionButtonResourceId);
                mAddButton.setOnClickListener(this);
                mAddButton.setFocusable(false);
                ImageView imageview = mAddButton;
                if(sAddButtonIcon == null)
                    sAddButtonIcon = getContext().getApplicationContext().getResources().getDrawable(R.drawable.ic_btn_add_member);
                imageview.setImageDrawable(sAddButtonIcon);
                mAddButton.setScaleType(android.widget.ImageView.ScaleType.CENTER);
                mAddButton.setContentDescription(getResources().getString(R.string.add_to_circles));
                addView(mAddButton);
            }
            mAddButton.setVisibility(0);
        } else
        if(mAddButton != null)
            mAddButton.setVisibility(8);
    }

    public void setAvatarVisible(boolean flag)
    {
        mAvatarVisible = flag;
    }

    public void setBitmap(Bitmap bitmap, boolean flag)
    {
        if(mAvatarRequest != null)
        {
            mAvatarBitmap = bitmap;
            invalidate();
        }
    }

    public void setCircleNameResolver(CircleNameResolver circlenameresolver)
    {
        mCircleNameResolver = circlenameresolver;
    }

    public void setContactIdAndAvatarUrl(String s, String s1, String s2)
    {
        if((!TextUtils.equals(mGaiaId, s) || !TextUtils.equals(mContactLookupKey, s1)) && mAvatarVisible)
        {
            mGaiaId = s;
            mContactLookupKey = s1;
            mAvatarUrl = s2;
            if(mContactLookupKey != null)
                mAvatarRequest = null;
            else
            if(mGaiaId == null)
                mAvatarRequest = null;
            else
                mAvatarRequest = new AvatarRequest(mGaiaId, mAvatarUrl, mAvatarRequestSize);
            mAvatarBitmap = null;
            requestLayout();
        }
    }

    public void setContactName(String s)
    {
        mDisplayName = s;
        updateDisplayName();
    }

    public void setCustomText(String s)
    {
        mCircleListVisible = true;
        mCircleIconVisible = false;
        mCirclesTextView.setText(s);
    }

    public void setDefaultAvatar(Bitmap bitmap)
    {
        if(bitmap == null)
            bitmap = sDefaultUserImage;
        mDefaultAvatarBitmap = bitmap;
    }

    public void setFirstRow(boolean flag)
    {
        mFirstRow = flag;
        TextView textview = mNameTextView;
        int i;
        if(mFirstRow)
            i = 0;
        else
            i = 8;
        textview.setVisibility(i);
    }

    public void setGaiaId(String s)
    {
        setGaiaIdAndAvatarUrl(s, null);
    }

    public void setGaiaIdAndAvatarUrl(String s, String s1)
    {
        if(!TextUtils.equals(mGaiaId, s) && mAvatarVisible)
        {
            mGaiaId = s;
            if(mGaiaId != null)
                mAvatarRequest = new AvatarRequest(s, s1, mAvatarRequestSize);
            else
                mAvatarRequest = null;
            mAvatarBitmap = null;
            requestLayout();
        }
    }

    public void setHighlightedText(String s)
    {
        if(s == null)
            mHighlightedText = null;
        else
            mHighlightedText = s.toUpperCase();
    }

    public void setOnActionButtonClickListener(OnActionButtonClickListener onactionbuttonclicklistener)
    {
        mListener = onactionbuttonclicklistener;
    }

    public void setPackedCircleIds(String s)
    {
        boolean flag = true;
        if(mCircleNameResolver != null)
        {
            boolean flag1;
            if(s != null)
                flag1 = flag;
            else
                flag1 = false;
            mCircleListVisible = flag1;
            if(TextUtils.isEmpty(s))
                flag = false;
            mCircleIconVisible = flag;
            mCirclesTextView.setText(mCircleNameResolver.getCircleNamesForPackedIds(s));
        }
    }

    public void setPackedCircleIdsAndEmailAddress(String s, String s1, String s2)
    {
        setPackedCircleIdsEmailAddressAndPhoneNumber(s, s1, s2, null, null);
    }

    public void setPackedCircleIdsEmailAddressAndPhoneNumber(String s, String s1, String s2, String s3, String s4)
    {
        setPackedCircleIdsEmailAddressPhoneNumberAndSnippet(s, s1, s2, s3, s4, null);
    }

    public void setPackedCircleIdsEmailAddressPhoneNumberAndSnippet(String s, String s1, String s2, String s3, String s4, String s5)
    {
        mTypeTextViewVisible = false;
        TextView textview;
        int i;
        if(!TextUtils.isEmpty(s3))
        {
            mCircleListVisible = true;
            mCircleIconVisible = false;
            mCirclesTextView.setText(PhoneNumberUtils.formatNumber(s3));
            String s8 = EsPeopleData.getStringForPhoneType(getContext(), s4);
            if(!TextUtils.isEmpty(s8))
            {
                if(mTypeTextView == null)
                {
                    Context context = getContext();
                    mTypeTextView = new TextView(context);
                    mTypeTextView.setSingleLine(true);
                    mTypeTextView.setTextAppearance(context, 0x1030044);
                    mTypeTextView.setTextSize(mCirclesTextSize);
                    mTypeTextView.setTextColor(mCirclesTextColor);
                    mTypeTextView.setGravity(16);
                    addView(mTypeTextView);
                }
                mTypeTextView.setText(s8.toUpperCase());
                mTypeTextViewVisible = true;
            }
        } else
        if(!TextUtils.isEmpty(s2))
        {
            mCircleListVisible = true;
            if(!TextUtils.isEmpty(s))
            {
                int k = 1;
                int l = 0;
                do
                {
                    int i1 = s.indexOf('|', l);
                    if(i1 == -1)
                        break;
                    k++;
                    l = i1 + 1;
                } while(true);
                String s6 = (new StringBuilder("|")).append(s2).toString();
                Resources resources = getResources();
                int j1 = R.plurals.circle_count_and_matched_email;
                Object aobj[] = new Object[2];
                aobj[0] = Integer.valueOf(k);
                aobj[1] = s6;
                resources.getQuantityString(j1, k, aobj).toUpperCase().indexOf(s6);
                Resources resources1 = getResources();
                int k1 = R.plurals.circle_count_and_matched_email;
                Object aobj1[] = new Object[2];
                aobj1[0] = Integer.valueOf(k);
                aobj1[1] = s2;
                String s7 = resources1.getQuantityString(k1, k, aobj1);
                SpannableUtils.setTextWithHighlight(mCirclesTextView, s7, mEmailTextBuilder, mHighlightedText, sBoldSpan, sColorSpan);
                mCircleIconVisible = true;
            } else
            {
                SpannableUtils.setTextWithHighlight(mCirclesTextView, s2, mEmailTextBuilder, mHighlightedText, sBoldSpan, sColorSpan);
                mCircleIconVisible = false;
            }
        } else
        if(!TextUtils.isEmpty(s))
        {
            mCircleListVisible = true;
            mCircleIconVisible = true;
            if(mCircleNameResolver != null)
                mCirclesTextView.setText(mCircleNameResolver.getCircleNamesForPackedIds(s));
        } else
        if(!TextUtils.isEmpty(s1))
        {
            mCircleListVisible = true;
            mCircleIconVisible = false;
            mCirclesTextView.setText(s1);
        } else
        if(!TextUtils.isEmpty(s5))
        {
            mCircleListVisible = true;
            mCircleIconVisible = false;
            mCirclesTextView.setText(Html.fromHtml(s5));
        } else
        {
            mCircleListVisible = false;
            mCircleIconVisible = false;
            mCirclesTextView.setText(null);
        }
        textview = mCirclesTextView;
        if(mCircleListVisible)
            i = 0;
        else
            i = 8;
        textview.setVisibility(i);
        if(mTypeTextView != null)
        {
            TextView textview1 = mTypeTextView;
            int j;
            if(mTypeTextViewVisible)
                j = 0;
            else
                j = 8;
            textview1.setVisibility(j);
        }
    }

    public void setPersonId(String s)
    {
        mPersonId = s;
    }

    public void setPlusPage(boolean flag)
    {
        mPlusPage = flag;
    }
    
    public void setRemoveButtonVisible(boolean flag) {
        if(mRemoveButtonVisible == flag) {
        	return;
        }
        mRemoveButtonVisible = flag;
        if(mRemoveButtonVisible)
        {
            if(mRemoveButton == null)
            {
                mRemoveButton = new ImageView(getContext());
                mRemoveButton.setBackgroundResource(mActionButtonResourceId);
                mRemoveButton.setOnClickListener(this);
                mRemoveButton.setFocusable(false);
                ImageView imageview = mRemoveButton;
                if(sRemoveButtonIcon == null)
                    sRemoveButtonIcon = getContext().getApplicationContext().getResources().getDrawable(R.drawable.ic_btn_dismiss_person);
                imageview.setImageDrawable(sRemoveButtonIcon);
                mRemoveButton.setScaleType(android.widget.ImageView.ScaleType.CENTER);
                mRemoveButton.setContentDescription(getResources().getString(R.string.remove_from_circles));
                addView(mRemoveButton);
            }
            mRemoveButton.setVisibility(0);
        } else
        if(mRemoveButton != null)
            mRemoveButton.setVisibility(8);
    }
    
    public void setSectionHeader(char c)
    {
        setSectionHeaderVisible(true);
        mSectionHeader.setText(String.valueOf(c));
    }

    protected void setSectionHeaderBackgroundColor()
    {
        mSectionHeader.setBackgroundColor(getContext().getResources().getColor(R.color.section_header_opaque_bg));
    }
    
    public void setSectionHeaderVisible(boolean flag) {
        mSectionHeaderVisible = flag;
        if(!mSectionHeaderVisible) {
        	if(mSectionHeader != null)
                mSectionHeader.setVisibility(8); 
        } else { 
        	if(mSectionHeader == null)
            {
                mSectionHeader = (SectionHeaderView)LayoutInflater.from(getContext()).inflate(R.layout.section_header, this, false);
                setSectionHeaderBackgroundColor();
                addView(mSectionHeader);
            } else
            {
                mSectionHeader.setVisibility(0);
            }
        }
    }
    
    public void setUnblockButtonVisible(boolean flag) {
    	
    	if(mUnblockButtonVisible == flag) {
    		return;
    	}
    	
    	mUnblockButtonVisible = flag;
        if(mUnblockButtonVisible)
        {
            if(mUnblockButton == null)
            {
                mUnblockButton = new ImageView(getContext());
                mUnblockButton.setBackgroundResource(mActionButtonResourceId);
                mUnblockButton.setOnClickListener(this);
                mUnblockButton.setFocusable(false);
                ImageView imageview = mUnblockButton;
                if(sUnblockButtonIcon == null)
                    sUnblockButtonIcon = getContext().getApplicationContext().getResources().getDrawable(R.drawable.list_unblock);
                imageview.setImageDrawable(sUnblockButtonIcon);
                mUnblockButton.setScaleType(android.widget.ImageView.ScaleType.CENTER);
                mUnblockButton.setContentDescription(getResources().getString(R.string.menu_item_unblock_person));
                addView(mUnblockButton);
            }
            mUnblockButton.setVisibility(0);
        } else
        if(mUnblockButton != null)
            mUnblockButton.setVisibility(8);
    }

    public void setWellFormedEmail(String s)
    {
        mWellFormedEmail = s;
        updateDisplayName();
    }

    public void setWellFormedSms(String s)
    {
        mWellFormedSmsMode = true;
        mWellFormedSms = s;
        mNameTextView.setText(s);
    }

    public void updateContentDescription()
    {
        Resources resources;
        CharSequence charsequence;
        resources = getResources();
        charsequence = mCirclesTextView.getText();
        if(!mCircleListVisible || charsequence == null || charsequence.length() <= 0) {
        	if(mDisplayName != null)
            {
                int j = R.string.person_entry_content_description;
                Object aobj1[] = new Object[1];
                aobj1[0] = mDisplayName;
                setContentDescription(resources.getString(j, aobj1));
            } else
            if(mWellFormedEmail != null)
            {
                int i = R.string.person_entry_email_content_description;
                Object aobj[] = new Object[1];
                aobj[0] = mWellFormedEmail;
                setContentDescription(resources.getString(i, aobj));
            } 
        } else { 
        	 int k = R.string.person_with_subtitle_entry_content_description;
             Object aobj2[] = new Object[2];
             aobj2[0] = mDisplayName;
             aobj2[1] = charsequence;
             setContentDescription(resources.getString(k, aobj2));
        }
    }

    public static interface OnActionButtonClickListener {

        void onActionButtonClick(PeopleListItemView peoplelistitemview, int i);
    }
}
