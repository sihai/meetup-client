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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.android.ui.fragments.CircleNameResolver;
import com.galaxy.meetup.client.android.ui.fragments.CircleSpinnerAdapter;
import com.galaxy.meetup.client.util.SpannableUtils;
import com.galaxy.meetup.client.util.ViewUtils;

/**
 * 
 * @author sihai
 *
 */
public class PersonCardView extends ViewGroup implements OnClickListener,
		ResourceConsumer {

	private static Paint sAvatarPaint;
    private static final StyleSpan sBoldSpan = new StyleSpan(1);
    private static Bitmap sCircleIconBitmap;
    private static Paint sCircleIconPaint;
    private static ForegroundColorSpan sColorSpan;
    private static Bitmap sDefaultAvatar;
    private static Drawable sEmailIcon;
    private static boolean sInitialized;
    private static ImageResourceManager sResourceManager;
    private int mAction;
    private Button mActionButton;
    private int mActionButtonHeight;
    private int mActionButtonMinWidth;
    private int mActionButtonTextColor;
    private boolean mActionButtonVisible;
    private boolean mAutoWidth;
    private final Rect mAvatarBounds;
    private final int mAvatarBoxHeight;
    private final Rect mAvatarOriginalBounds;
    private Resource mAvatarResource;
    private final int mAvatarSize;
    private String mAvatarUrl;
    private final Drawable mBackground;
    private boolean mCircleChangePending;
    private int mCircleIconPaddingTop;
    private boolean mCircleIconVisible;
    private int mCircleIconX;
    private int mCircleIconY;
    private CircleNameResolver mCircleNameResolver;
    private CirclesButton mCirclesButton;
    private final int mCirclesButtonPadding;
    private String mContactLookupKey;
    private final TextView mDescriptionTextView;
    private boolean mDescriptionVisible;
    private ImageView mDismissButton;
    private Drawable mDismissButtonBackground;
    private final int mDismissButtonSize;
    private boolean mDismissButtonVisible;
    private String mDisplayName;
    private int mEmailIconPaddingRight;
    private int mEmailIconPaddingTop;
    private final SpannableStringBuilder mEmailTextBuilder;
    private boolean mForSharing;
    private boolean mForceAvatarDefault;
    private String mGaiaId;
    private final int mGapBetweenAvatarAndText;
    private final int mGapBetweenIconAndCircles;
    private final int mGapBetweenNameAndCircles;
    private String mHighlightedText;
    private final int mImageButtonMargin;
    private OnPersonCardClickListener mListener;
    private final int mMinHeight;
    private final int mMinWidth;
    private final SpannableStringBuilder mNameTextBuilder;
    private final TextView mNameTextView;
    private final int mNextCardPeekWidth;
    private int mOneClickMode;
    private int mOptimalWidth;
    private final int mPaddingBottom;
    private final int mPaddingLeft;
    private final int mPaddingRight;
    private final int mPaddingTop;
    private String mPersonId;
    private boolean mPlusPage;
    private int mPosition;
    private final int mPreferredWidth;
    private final Drawable mSelector;
    private boolean mShowTooltip;
    private String mSuggestionId;
    private String mTooltipText;
    private String mWellFormedEmail;
    private boolean mWellFormedEmailMode;
    private int mWideLeftMargin;
    private boolean mWideMargin;

    public PersonCardView(Context context)
    {
        this(context, null);
    }

    public PersonCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mOneClickMode = 0;
        mNameTextBuilder = new SpannableStringBuilder();
        mEmailTextBuilder = new SpannableStringBuilder();
        mAvatarOriginalBounds = new Rect();
        mAvatarBounds = new Rect();
        Resources resources = context.getApplicationContext().getResources();
        if(!sInitialized)
        {
            sInitialized = true;
            sResourceManager = ImageResourceManager.getInstance(context);
            sDefaultAvatar = EsAvatarData.getMediumDefaultAvatar(context, true);
            sCircleIconBitmap = ((BitmapDrawable)resources.getDrawable(R.drawable.ic_circles)).getBitmap();
            Paint paint = new Paint();
            sAvatarPaint = paint;
            paint.setFilterBitmap(true);
            sCircleIconPaint = new Paint();
            sColorSpan = new ForegroundColorSpan(resources.getColor(R.color.search_query_highlight_color));
            sEmailIcon = context.getApplicationContext().getResources().getDrawable(R.drawable.profile_email);
        }
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Theme);
        mDismissButtonBackground = typedarray.getDrawable(5);
        typedarray.recycle();
        mBackground = resources.getDrawable(R.drawable.bg_tacos);
        mSelector = resources.getDrawable(R.drawable.stream_list_selector);
        mSelector.setCallback(this);
        mPreferredWidth = resources.getDimensionPixelSize(R.dimen.person_card_preferred_width);
        mMinWidth = resources.getDimensionPixelSize(R.dimen.person_card_min_width);
        mMinHeight = resources.getDimensionPixelSize(R.dimen.person_card_min_height);
        mPaddingTop = resources.getDimensionPixelOffset(R.dimen.person_card_padding_top);
        mPaddingLeft = resources.getDimensionPixelOffset(R.dimen.person_card_padding_left);
        int i = resources.getDimensionPixelOffset(R.dimen.person_card_padding);
        mPaddingRight = i;
        mPaddingBottom = i;
        mWideLeftMargin = resources.getDimensionPixelOffset(R.dimen.person_card_wide_left_margin);
        mAvatarSize = resources.getDimensionPixelSize(R.dimen.person_card_avatar_size);
        mAvatarBoxHeight = resources.getDimensionPixelOffset(R.dimen.person_card_avatar_box_height);
        mGapBetweenAvatarAndText = resources.getDimensionPixelOffset(R.dimen.person_card_gap_between_avatar_and_text);
        mActionButtonMinWidth = resources.getDimensionPixelSize(R.dimen.person_card_add_button_min_width);
        mActionButtonHeight = resources.getDimensionPixelSize(R.dimen.person_card_add_button_height);
        mActionButtonTextColor = resources.getColor(R.color.person_card_add_button_text_color);
        mCircleIconPaddingTop = resources.getDimensionPixelOffset(R.dimen.person_card_circle_icon_padding_top);
        mGapBetweenIconAndCircles = resources.getDimensionPixelOffset(R.dimen.person_card_gap_between_icon_and_circles);
        mGapBetweenNameAndCircles = resources.getDimensionPixelOffset(R.dimen.person_card_gap_between_name_and_circles);
        mImageButtonMargin = resources.getDimensionPixelOffset(R.dimen.person_card_dismiss_button_margin);
        mDismissButtonSize = resources.getDimensionPixelOffset(R.dimen.person_card_dismiss_button_size);
        mCirclesButtonPadding = resources.getDimensionPixelSize(R.dimen.person_card_circles_button_padding);
        mNextCardPeekWidth = resources.getDimensionPixelSize(R.dimen.person_card_next_card_peek_width);
        mEmailIconPaddingTop = resources.getDimensionPixelSize(R.dimen.person_card_email_icon_padding_top);
        mEmailIconPaddingRight = resources.getDimensionPixelSize(R.dimen.person_card_email_icon_padding_right);
        float f = resources.getDimension(R.dimen.person_card_name_text_size);
        float f1 = resources.getDimension(R.dimen.person_card_circle_text_size);
        mNameTextView = new TextView(context);
        mNameTextView.setMaxLines(2);
        mNameTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        mNameTextView.setTextAppearance(context, 0x1030044);
        mNameTextView.setTextSize(0, f);
        mNameTextView.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -2));
        mNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        addView(mNameTextView);
        mDescriptionTextView = new TextView(context);
        mDescriptionTextView.setMaxLines(3);
        mDescriptionTextView.setGravity(48);
        mDescriptionTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        mDescriptionTextView.setTextSize(0, f1);
        mDescriptionTextView.setTextColor(resources.getColor(R.color.person_card_circle_text_color));
        mDescriptionTextView.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -2));
        addView(mDescriptionTextView);
        setOnClickListener(this);
    }

    private void addCirclesButton()
    {
        mCirclesButton = new CirclesButton(getContext());
        mCirclesButton.setBackgroundResource(R.drawable.plusone_button);
        mCirclesButton.setPadding(mCirclesButtonPadding, 0, mCirclesButtonPadding, 0);
        mCirclesButton.setOnClickListener(this);
        addView(mCirclesButton);
    }

    public final void bindResources()
    {
        if(ViewUtils.isViewAttached(this) && !mForceAvatarDefault && mAvatarUrl != null)
            mAvatarResource = sResourceManager.getAvatar(mAvatarUrl, 2, true, this);
    }

    public void dispatchDraw(Canvas canvas)
    {
        mBackground.draw(canvas);
        if(mCircleIconVisible)
            canvas.drawBitmap(sCircleIconBitmap, mCircleIconX, mCircleIconY, sCircleIconPaint);
        Bitmap bitmap = sDefaultAvatar;
        if(mAvatarResource != null && mAvatarResource.getStatus() == 1)
            bitmap = (Bitmap)mAvatarResource.getResource();
        mAvatarOriginalBounds.right = bitmap.getWidth();
        mAvatarOriginalBounds.bottom = bitmap.getHeight();
        canvas.drawBitmap(bitmap, mAvatarOriginalBounds, mAvatarBounds, sAvatarPaint);
        if(mContactLookupKey != null)
        {
            int i = sEmailIcon.getIntrinsicWidth();
            int j = sEmailIcon.getIntrinsicHeight();
            int k = (mAvatarBounds.right - i) + mEmailIconPaddingRight;
            int l = mAvatarBounds.top + mEmailIconPaddingTop;
            sEmailIcon.setBounds(k, l, k + i, l + j);
            sEmailIcon.draw(canvas);
        }
        super.dispatchDraw(canvas);
        if(isPressed() || isFocused())
            mSelector.draw(canvas);
        if(mShowTooltip)
        {
            mShowTooltip = false;
            final int screenPos[] = new int[2];
            mCirclesButton.getLocationOnScreen(screenPos);
            post(new Runnable() {

                public final void run()
                {
                    Context context = PersonCardView.this.getContext();
                }
            });
        }
    }

    protected void drawableStateChanged()
    {
        mSelector.setState(getDrawableState());
        invalidate();
        super.drawableStateChanged();
    }

    public final String getContactName()
    {
        return mDisplayName;
    }

    public final String getPersonId()
    {
        return mPersonId;
    }

    public final int getPosition()
    {
        return mPosition;
    }

    public final String getSuggestionId()
    {
        return mSuggestionId;
    }

    public final String getWellFormedEmail()
    {
        return mWellFormedEmail;
    }

    public final boolean isForSharing()
    {
        return mForSharing;
    }

    public final boolean isOneClickAdd()
    {
        boolean flag = true;
        if(mOneClickMode != 1)
            flag = false;
        return flag;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        bindResources();
    }

    public void onClick(View view)
    {
        if(!mCircleChangePending && mListener != null)
            if(view == mActionButton)
                mListener.onActionButtonClick(this, mAction);
            else
            if(view == mCirclesButton)
                mListener.onChangeCircles(this);
            else
            if(view == mDismissButton)
                mListener.onDismissButtonClick(this);
            else
                mListener.onItemClick(this);
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        unbindResources();
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        int k1;
        int l1;
        int j2;
        int k2;
        int l2;
        Object obj;
        boolean flag1 = mWideMargin;
        boolean flag2;
        int i1 = 0;
        if(flag1)
            i1 = mWideLeftMargin;
        mBackground.setBounds(i1, 0, k - i, l - j);
        mSelector.setBounds(i1, 0, k - i, l - j);
        int j1 = k - i;
        k1 = l - j;
        l1 = mPaddingTop;
        int i2 = i1 + mPaddingLeft;
        j2 = j1 - mPaddingRight;
        if(mDismissButtonVisible)
        {
            int k4 = mDismissButton.getMeasuredWidth();
            int l4 = mDismissButton.getMeasuredHeight();
            mDismissButton.layout(j1 - mImageButtonMargin - k4, mImageButtonMargin, j1 - mImageButtonMargin, l4 + mImageButtonMargin);
        }
        mAvatarBounds.left = i2;
        mAvatarBounds.right = mAvatarBounds.left + mAvatarSize;
        mAvatarBounds.top = (mAvatarBoxHeight - mAvatarSize) / 2;
        mAvatarBounds.bottom = mAvatarBounds.top + mAvatarSize;
        k2 = i2 + (mAvatarSize + mGapBetweenAvatarAndText);
        l2 = mOneClickMode;
        obj = null;
        
        if(0 == l2) {
        	flag2 = mActionButtonVisible;
            obj = null;
            if(flag2)
                obj = mActionButton;
        } else if(1 == l2) {
        	obj = mCirclesButton;
        } else if(2 == l2) {
        	
        } else if(3 == l2) {
        	obj = mCirclesButton;
        }
        
        if(obj != null)
        {
            int l3 = ((View) (obj)).getMeasuredWidth();
            int i4 = ((View) (obj)).getMeasuredHeight();
            int j4 = k1 - mPaddingBottom - i4;
            ((View) (obj)).layout(k2, j4, k2 + l3, j4 + i4);
        }
        int i3 = mNameTextView.getMeasuredHeight();
        mNameTextView.layout(k2, l1, j2, l1 + i3);
        int j3 = k2;
        int k3 = i3 + mPaddingTop + mGapBetweenNameAndCircles;
        if(mCircleIconVisible)
            j3 += sCircleIconBitmap.getWidth() + mGapBetweenIconAndCircles;
        if(mDescriptionVisible)
            mDescriptionTextView.layout(j3, k3, j3 + mDescriptionTextView.getMeasuredWidth(), k3 + mDescriptionTextView.getMeasuredHeight());
        if(mCircleIconVisible)
        {
            mCircleIconX = k2;
            mCircleIconY = k3;
            if(mDescriptionTextView.getLineCount() > 1)
                mCircleIconY = mCircleIconY + mCircleIconPaddingTop;
            else
                mCircleIconY = mCircleIconY + (mDescriptionTextView.getMeasuredHeight() - sCircleIconBitmap.getHeight()) / 2;
        }
        return;
    }

    protected void onMeasure(int i, int j)
    {
        Object obj;
        int k;
        int l;
        boolean flag;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        int k2;
        int l2;
        int i3;
        if(mAutoWidth)
        {
            if(mOptimalWidth == 0)
            {
                int j3 = getContext().getResources().getDisplayMetrics().widthPixels - mNextCardPeekWidth;
                mOptimalWidth = Math.min(Math.max(j3 / Math.max((-1 + (j3 + mPreferredWidth)) / mPreferredWidth, 1), mMinWidth), mPreferredWidth);
            }
            k = resolveSize(mOptimalWidth, i);
        } else
        {
            k = resolveSize(mPreferredWidth, i);
        }
        l = resolveSize(mMinHeight, j);
        flag = mWideMargin;
        i1 = 0;
        if(flag)
        {
            k += mWideLeftMargin;
            i1 = mWideLeftMargin;
        }
        j1 = k - mPaddingLeft - mPaddingRight - i1;
        k1 = l - mPaddingTop - mPaddingBottom;
        l1 = j1 - (mAvatarSize + mGapBetweenAvatarAndText);
        i2 = mOneClickMode;
        obj = null;
        
        if(0 == i2) {
        	boolean flag1 = mActionButtonVisible;
            obj = null;
            if(flag1)
                obj = mActionButton;
        } else if(1 == i2) {
        	obj = mCirclesButton;
        } else if(2 == i2) {
        	
        } else if(3 == i2) {
        	obj = mCirclesButton;
        }
        
        if(obj != null)
        {
            ((View) (obj)).measure(android.view.View.MeasureSpec.makeMeasureSpec(l1, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(mActionButtonHeight, 0x40000000));
            k1 -= ((View) (obj)).getMeasuredHeight() + mGapBetweenNameAndCircles;
        }
        j2 = l1;
        if(mDismissButtonVisible)
            j2 -= mImageButtonMargin;
        mNameTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(j2, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(k1, 0x80000000));
        k2 = k1 - (mNameTextView.getMeasuredHeight() + mGapBetweenNameAndCircles);
        l2 = l1;
        if(mCircleIconVisible)
            l2 -= sCircleIconBitmap.getWidth() + mGapBetweenIconAndCircles;
        if(mDescriptionVisible)
        {
            i3 = mDescriptionTextView.getLineHeight();
            mDescriptionTextView.setMaxLines(k2 / i3);
            mDescriptionTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(l2, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(mMinHeight, 0x80000000));
            mDescriptionTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(mDescriptionTextView.getMeasuredWidth(), 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(mDescriptionTextView.getMeasuredHeight(), 0x40000000));
        }
        if(mDismissButtonVisible)
            mDismissButton.measure(android.view.View.MeasureSpec.makeMeasureSpec(mDismissButtonSize, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(mDismissButtonSize, 0x40000000));
        setMeasuredDimension(k, l);
        return;
        
    }

    public final void onResourceStatusChange(Resource resource)
    {
        invalidate();
    }

    public void setActionButtonVisible(boolean flag, int i, int j)
    {
        if(mActionButtonVisible != flag) {
        	mActionButtonVisible = flag;
            if(mActionButtonVisible)
            {
                if(mActionButton == null)
                {
                    mActionButton = new Button(getContext());
                    mActionButton.setOnClickListener(this);
                    mActionButton.setBackgroundResource(R.drawable.plusone_button);
                    mActionButton.setFocusable(false);
                    mActionButton.setTextColor(mActionButtonTextColor);
                    mActionButton.setMinimumWidth(mActionButtonMinWidth);
                    addView(mActionButton);
                }
                mAction = j;
                mActionButton.setText(i);
                mActionButton.setVisibility(0);
            } else
            if(mActionButton != null)
                mActionButton.setVisibility(8); 
        } else { 
        	if(mActionButtonVisible)
            {
                mAction = j;
                mActionButton.setText(i);
            }
        }
    }

    public void setAutoWidthForHorizontalScrolling()
    {
        mAutoWidth = true;
    }

    public void setCircleNameResolver(CircleNameResolver circlenameresolver)
    {
        mCircleNameResolver = circlenameresolver;
    }

    public void setContactIdAndAvatarUrl(String s, String s1, String s2)
    {
        if(!TextUtils.equals(mGaiaId, s) || !TextUtils.equals(mContactLookupKey, s1))
        {
            unbindResources();
            mGaiaId = s;
            mContactLookupKey = s1;
            mAvatarUrl = s2;
            bindResources();
        }
    }

    public void setContactName(String s)
    {
        mDisplayName = s;
        SpannableUtils.setTextWithHighlight(mNameTextView, s, mNameTextBuilder, mHighlightedText, sBoldSpan, sColorSpan);
    }

    public void setDescription(String s, boolean flag, boolean flag1)
    {
        if(!TextUtils.isEmpty(s))
        {
            mDescriptionVisible = true;
            mCircleIconVisible = false;
            if(flag)
                mDescriptionTextView.setText(Html.fromHtml(s));
            else
                mDescriptionTextView.setText(s);
        } else
        {
            mDescriptionVisible = false;
            mCircleIconVisible = false;
            mDescriptionTextView.setText(null);
        }
    }

    public void setDismissActionButtonVisible(boolean flag) {
        if(mDismissButtonVisible == flag) {
        	return;
        }
       
        mDismissButtonVisible = flag;
        if(mDismissButtonVisible)
        {
            if(mDismissButton == null)
            {
                mDismissButton = new ImageView(getContext());
                mDismissButton.setBackgroundDrawable(mDismissButtonBackground);
                mDismissButton.setOnClickListener(this);
                mDismissButton.setFocusable(false);
                mDismissButton.setImageResource(R.drawable.ic_friend_dismiss);
                mDismissButton.setScaleType(android.widget.ImageView.ScaleType.CENTER);
                mDismissButton.setContentDescription(getResources().getString(R.string.menu_dismiss_people));
                addView(mDismissButton);
            }
            mDismissButton.setVisibility(0);
        } else
        if(mDismissButton != null)
            mDismissButton.setVisibility(8);
    }

    public void setForceAvatarDefault(boolean flag)
    {
        mForceAvatarDefault = flag;
        if(flag)
        {
            if(mAvatarResource != null)
                mAvatarResource.unregister(this);
            mAvatarResource = null;
        }
    }

    public void setGaiaIdAndAvatarUrl(String s, String s1)
    {
        if(!TextUtils.equals(mGaiaId, s))
        {
            unbindResources();
            mGaiaId = s;
            mAvatarUrl = s1;
            bindResources();
        }
    }

    public void setHighlightedText(String s)
    {
        if(s == null)
            mHighlightedText = null;
        else
            mHighlightedText = s.toUpperCase();
    }

    public void setOnPersonCardClickListener(OnPersonCardClickListener onpersoncardclicklistener)
    {
        mListener = onpersoncardclicklistener;
    }

    public void setOneClickCircles(String s, CircleSpinnerAdapter circlespinneradapter, boolean flag)
    {
        if(TextUtils.isEmpty(s))
            mOneClickMode = 1;
        else
            mOneClickMode = 3;
        if(mCirclesButton == null)
            addCirclesButton();
        if(mOneClickMode == 1)
        {
            if(flag)
                mCirclesButton.setText(getContext().getString(R.string.add_to_circles));
            else
                mCirclesButton.setText(getContext().getString(R.string.follow));
            mCirclesButton.setShowIcon(false);
            mCirclesButton.setHighlighted(true);
        } else
        {
            mCirclesButton.setCircles(mCircleNameResolver.getCircleNameListForPackedIds(s));
            mCirclesButton.setShowIcon(true);
            mCirclesButton.setHighlighted(false);
        }
        mCirclesButton.setVisibility(0);
        mDescriptionVisible = true;
        mDescriptionTextView.setVisibility(0);
        mForSharing = flag;
    }

    public void setPackedCircleIdsEmailAndDescription(String s, String s1, String s2, boolean flag, boolean flag1)
    {
        if(!TextUtils.isEmpty(s))
        {
            mDescriptionVisible = true;
            mCircleIconVisible = true;
            mDescriptionTextView.setText(mCircleNameResolver.getCircleNamesForPackedIds(s));
        } else
        if(!TextUtils.isEmpty(s1))
        {
            mDescriptionVisible = true;
            mCircleIconVisible = false;
            SpannableUtils.setTextWithHighlight(mDescriptionTextView, s1, mEmailTextBuilder, mHighlightedText, sBoldSpan, sColorSpan);
        } else
        if(!TextUtils.isEmpty(s2))
        {
            mDescriptionVisible = true;
            mCircleIconVisible = false;
            if(flag)
                mDescriptionTextView.setText(Html.fromHtml(s2));
            else
                mDescriptionTextView.setText(s2);
        } else
        {
            mDescriptionVisible = false;
            mCircleIconVisible = false;
            mDescriptionTextView.setText(null);
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

    public void setPosition(int i)
    {
        mPosition = i;
    }

    public void setShowCircleChangePending(boolean flag)
    {
        if(mCircleChangePending != flag)
        {
            mCircleChangePending = flag;
            if(mCircleChangePending)
            {
                if(mCirclesButton == null)
                {
                    mOneClickMode = 1;
                    addCirclesButton();
                }
                mCirclesButton.setText("");
                mCirclesButton.setShowIcon(false);
                mCirclesButton.setHighlighted(false);
                mCirclesButton.setVisibility(0);
            }
            if(mCirclesButton != null)
                mCirclesButton.setShowProgressIndicator(flag);
        }
    }

    public void setShowTooltip(boolean flag, int i)
    {
        mShowTooltip = flag;
        if(mShowTooltip)
        {
            mTooltipText = getContext().getString(i);
            invalidate();
        }
    }

    public void setSuggestionId(String s)
    {
        mSuggestionId = s;
    }

    public void setWellFormedEmail(String s)
    {
        mWellFormedEmailMode = true;
        mWellFormedEmail = s;
        mNameTextView.setText(s);
    }

    public void setWideMargin(boolean flag)
    {
        if(mWideMargin != flag)
        {
            mWideMargin = flag;
            requestLayout();
        }
    }

    public final void unbindResources()
    {
        if(mAvatarResource != null)
        {
            mAvatarResource.unregister(this);
            mAvatarResource = null;
        }
    }

    public final void updateContentDescription()
    {
        Resources resources = getResources();
        CharSequence charsequence = mDescriptionTextView.getText();
        if(mDescriptionVisible && !TextUtils.isEmpty(charsequence))
        {
            int j = R.string.person_with_subtitle_entry_content_description;
            Object aobj1[] = new Object[2];
            aobj1[0] = mDisplayName;
            aobj1[1] = charsequence;
            setContentDescription(resources.getString(j, aobj1));
        } else
        {
            int i = R.string.person_entry_content_description;
            Object aobj[] = new Object[1];
            aobj[0] = mDisplayName;
            setContentDescription(resources.getString(i, aobj));
        }
    }

    protected boolean verifyDrawable(Drawable drawable)
    {
        boolean flag;
        if(drawable == mSelector)
            flag = true;
        else
            flag = super.verifyDrawable(drawable);
        return flag;
    }
    
    
    //============================================================================
    //								Inner class
    //============================================================================
    public static interface OnPersonCardClickListener {

        public abstract void onActionButtonClick(PersonCardView personcardview, int i);

        public abstract void onChangeCircles(PersonCardView personcardview);

        public abstract void onDismissButtonClick(PersonCardView personcardview);

        public abstract void onItemClick(PersonCardView personcardview);
    }

}
