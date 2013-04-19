/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.CheckBox;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AvatarRequest;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageCache.OnAvatarChangeListener;
import com.galaxy.meetup.client.util.SpannableUtils;

/**
 * 
 * @author sihai
 *
 */
public class CircleListItemView extends CheckableListItemView implements
		OnAvatarChangeListener {

	private static final SparseArray sCircleTypeIcons = new SparseArray();
    private static Bitmap sDefaultAvatarBitmap;
    private static Drawable sDefaultCircleDrawable;
    private final ImageCache mAvatarCache;
    private int mAvatarCount;
    private final List mAvatarHolders;
    private final int mAvatarSize;
    private final int mAvatarSpacing;
    private int mAvatarStripLeft;
    private int mAvatarStripTop;
    private boolean mAvatarStripVisible;
    private final Rect mCircleIconBounds;
    private Drawable mCircleIconDrawable;
    private final int mCircleIconSizeLarge;
    private final int mCircleIconSizeSmall;
    private String mCircleId;
    private String mCircleName;
    private int mCircleType;
    private final TextView mCountTextView;
    private final String mGaiaIds[];
    private final int mGapBetweenCountAndCheckBox;
    private final int mGapBetweenIconAndText;
    private final int mGapBetweenNameAndCount;
    private String mHighlightedText;
    private int mMemberCount;
    private boolean mMemberCountShown;
    private boolean mMemberCountVisible;
    private final SpannableStringBuilder mNameTextBuilder;
    private final TextView mNameTextView;
    private final int mPaddingBottom;
    private final int mPaddingLeft;
    private final int mPaddingRight;
    private final int mPaddingTop;
    private final Paint mPhotoPaint;
    private final int mPreferredHeight;
    private final Rect mSourceRect;
    private final Rect mTargetRect;
    private int mVisibleAvatarCount;
    
    public CircleListItemView(Context context)
    {
        this(context, null);
    }

    public CircleListItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mAvatarHolders = new ArrayList();
        mGaiaIds = new String[16];
        mSourceRect = new Rect();
        mTargetRect = new Rect();
        mNameTextBuilder = new SpannableStringBuilder();
        mMemberCountVisible = true;
        mAvatarCache = ImageCache.getInstance(context);
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.CircleListItemView);
        mPreferredHeight = typedarray.getDimensionPixelSize(0, 0);
        mPaddingTop = typedarray.getDimensionPixelOffset(1, 0);
        mPaddingBottom = typedarray.getDimensionPixelOffset(2, 0);
        mPaddingLeft = typedarray.getDimensionPixelOffset(3, 0);
        mPaddingRight = typedarray.getDimensionPixelOffset(4, 0);
        mAvatarSize = typedarray.getDimensionPixelSize(8, 0);
        mAvatarSpacing = typedarray.getDimensionPixelSize(9, 0);
        float f = typedarray.getFloat(6, 0.0F);
        boolean flag = typedarray.getBoolean(7, false);
        mGapBetweenNameAndCount = typedarray.getDimensionPixelOffset(12, 0);
        mGapBetweenIconAndText = typedarray.getDimensionPixelOffset(5, 0);
        mCircleIconSizeSmall = typedarray.getDimensionPixelSize(10, 0);
        mCircleIconSizeLarge = typedarray.getDimensionPixelSize(11, 0);
        int i = typedarray.getColor(14, 0);
        mGapBetweenCountAndCheckBox = typedarray.getDimensionPixelOffset(13, 0);
        typedarray.recycle();
        mNameTextView = new TextView(context);
        mNameTextView.setSingleLine(true);
        mNameTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        mNameTextView.setTextAppearance(context, 0x1030044);
        mNameTextView.setTextSize(f);
        if(flag)
            mNameTextView.setTypeface(mNameTextView.getTypeface(), 1);
        mNameTextView.setGravity(16);
        mNameTextView.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -2));
        addView(mNameTextView);
        mCountTextView = new TextView(context);
        mCountTextView.setSingleLine(true);
        mCountTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        mCountTextView.setTextAppearance(context, 0x1030044);
        mCountTextView.setTextSize(f);
        mCountTextView.setTextColor(i);
        mCountTextView.setGravity(16);
        mCountTextView.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -2));
        addView(mCountTextView);
        mPhotoPaint = new Paint(2);
        mCircleIconBounds = new Rect();
        if(sDefaultAvatarBitmap == null)
        {
            Resources resources = context.getApplicationContext().getResources();
            sDefaultAvatarBitmap = EsAvatarData.getTinyDefaultAvatar(getContext());
            sDefaultCircleDrawable = resources.getDrawable(R.drawable.list_public);
        }
        mCircleIconDrawable = sDefaultCircleDrawable;
    }
    
    public void dispatchDraw(Canvas canvas)
    {
        if(mCircleType != -3)
        {
            mCircleIconDrawable.setBounds(mCircleIconBounds);
            mCircleIconDrawable.draw(canvas);
        }
        boolean flag;
        if(mAvatarStripVisible && mMemberCountShown && mMemberCount != 0)
            flag = true;
        else
            flag = false;
        if(flag)
        {
            int i = mAvatarStripLeft;
            for(int j = 0; j < mVisibleAvatarCount; j++)
            {
                mTargetRect.left = i;
                mTargetRect.top = mAvatarStripTop;
                mTargetRect.right = i + mAvatarSize;
                mTargetRect.bottom = mAvatarStripTop + mAvatarSize;
                AvatarHolder avatarholder = (AvatarHolder)mAvatarHolders.get(j);
                avatarholder.refreshIfNecessary();
                Bitmap bitmap = avatarholder.getBitmap();
                if(bitmap == null)
                    bitmap = sDefaultAvatarBitmap;
                if(avatarholder.isAvatarVisible() && bitmap != null)
                {
                    mSourceRect.right = bitmap.getWidth();
                    mSourceRect.bottom = bitmap.getHeight();
                    canvas.drawBitmap(bitmap, mSourceRect, mTargetRect, mPhotoPaint);
                }
                i += mAvatarSize + mAvatarSpacing;
            }

        }
        super.dispatchDraw(canvas);
    }

    public final String getCircleId()
    {
        return mCircleId;
    }

    public final String getCircleName()
    {
        return mCircleName;
    }

    public final int getCircleType()
    {
        return mCircleType;
    }

    public final int getMemberCount()
    {
        return mMemberCount;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ImageCache _tmp = mAvatarCache;
        ImageCache.registerAvatarChangeListener(this);
    }
    
    public void onAvatarChanged(String s)
    {
        int i = 0;
        do
        {
label0:
            {
                if(i < mAvatarHolders.size())
                {
                    AvatarHolder avatarholder = (AvatarHolder)mAvatarHolders.get(i);
                    if(!String.valueOf(avatarholder.mGaiaId).equals(s))
                        break label0;
                    avatarholder.reloadAvatar();
                    invalidate();
                }
                return;
            }
            i++;
        } while(true);
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        ImageCache _tmp = mAvatarCache;
        ImageCache.unregisterAvatarChangeListener(this);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = mPaddingLeft;
        int j1 = mPaddingTop;
        int k1 = l - j;
        int l1 = mNameTextView.getMeasuredHeight();
        int i2 = l1;
        if(mMemberCountShown)
            i2 = Math.max(l1, mCountTextView.getMeasuredHeight());
        int j2;
        boolean flag1;
        int k2;
        int l2;
        int i3;
        int j3;
        int k3;
        int l3;
        int i4;
        int j4;
        int k4;
        int l4;
        TextView textview;
        int i5;
        int j5;
        if(mAvatarStripVisible)
            j2 = mCircleIconSizeSmall;
        else
            j2 = mCircleIconSizeLarge;
        if(mAvatarStripVisible && mMemberCountShown && mMemberCount != 0)
            flag1 = true;
        else
            flag1 = false;
        if(flag1)
        {
            int k7 = Math.max(i2, j2);
            k2 = j1 + (k7 - j2) / 2;
            i2 = Math.max(i2, k7);
            l2 = j1 + (k7 - i2) / 2;
        } else
        {
            k2 = (k1 - j2) / 2;
            l2 = (k1 - i2) / 2;
        }
        i3 = mCircleIconDrawable.getIntrinsicWidth();
        j3 = mCircleIconDrawable.getIntrinsicHeight();
        k3 = k2 + (j2 - j3) / 2;
        l3 = i1 + (j2 - i3) / 2;
        mCircleIconBounds.set(l3, k3, l3 + i3, k3 + j3);
        i4 = i1 + (j2 + mGapBetweenIconAndText);
        j4 = k - mPaddingRight;
        if(mCheckBoxVisible)
        {
            int j6 = mCheckBox.getMeasuredWidth();
            int k6 = mCheckBox.getMeasuredHeight();
            int l6 = (k1 - k6) / 2;
            CheckBox checkbox = mCheckBox;
            int i7 = j4 - j6;
            int j7 = l6 + k6;
            checkbox.layout(i7, l6, j4, j7);
            j4 -= j6 + mGapBetweenCountAndCheckBox;
        }
        if(mMemberCountShown)
            k4 = mCountTextView.getMeasuredWidth();
        else
            k4 = 0;
        l4 = Math.min(mNameTextView.getMeasuredWidth(), j4 - i4 - k4 - mGapBetweenNameAndCount);
        textview = mNameTextView;
        i5 = i4 + l4;
        j5 = l2 + i2;
        textview.layout(i4, l2, i5, j5);
        if(mMemberCountShown)
        {
            int l5 = i4 + l4 + mGapBetweenNameAndCount;
            TextView textview1 = mCountTextView;
            int i6 = l2 + i2;
            textview1.layout(l5, l2, j4, i6);
        }
        if(flag1)
        {
            mAvatarStripTop = k1 - mPaddingBottom - mAvatarSize;
            mAvatarStripLeft = i4;
            mVisibleAvatarCount = ((k - i - i4 - mPaddingRight) + mAvatarSpacing) / (mAvatarSize + mAvatarSpacing);
            mVisibleAvatarCount = Math.min(mVisibleAvatarCount, mAvatarCount);
            for(int k5 = 0; k5 < mVisibleAvatarCount; k5++)
                ((AvatarHolder)mAvatarHolders.get(k5)).loadAvatar();

        }
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l;
        int i1;
        boolean flag;
        int j1;
        int k1;
        int l1;
        boolean flag1;
        boolean flag2;
        int i2;
        if(mAvatarStripVisible)
            l = mCircleIconSizeSmall;
        else
            l = mCircleIconSizeLarge;
        i1 = k - mPaddingLeft - mPaddingRight - l - mGapBetweenIconAndText;
        flag = mCheckBoxVisible;
        j1 = 0;
        if(flag)
        {
            mCheckBox.measure(0, j);
            j1 = Math.max(0, mCheckBox.getMeasuredHeight());
            i1 -= mCheckBox.getMeasuredWidth() + mGapBetweenCountAndCheckBox;
        }
        mNameTextView.measure(0, 0);
        k1 = mNameTextView.getMeasuredHeight();
        if(mMemberCountShown)
        {
            mCountTextView.measure(0, 0);
            k1 = Math.max(k1, mCountTextView.getMeasuredHeight());
            i1 -= mCountTextView.getMeasuredWidth() + mGapBetweenNameAndCount;
        }
        l1 = Math.max(Math.max(j1, k1), l);
        flag1 = mAvatarStripVisible;
        flag2 = false;
        if(flag1)
        {
            boolean flag3 = mMemberCountShown;
            flag2 = false;
            if(flag3)
            {
                int j2 = mMemberCount;
                flag2 = false;
                if(j2 != 0)
                    flag2 = true;
            }
        }
        if(flag2)
            l1 = Math.max(l1, k1 + mAvatarSize);
        i2 = Math.min(mNameTextView.getMeasuredWidth(), i1);
        mNameTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(i2, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(mNameTextView.getMeasuredHeight(), 0x40000000));
        setMeasuredDimension(k, Math.max(l1 + (mPaddingTop + mPaddingBottom), mPreferredHeight));
    }

    public void setAvatarStripVisible(boolean flag)
    {
        mAvatarStripVisible = flag;
    }

    public void setCircle(String s, int i, String s1, int j, boolean flag) {
        int l = 0;
        boolean flag1 = true;
        mCircleId = s;
        mCircleType = i;
        mMemberCount = j;
        int k;
        if(!mMemberCountVisible || i != 1 && i != 5 && i != 10)
            flag1 = false;
        mMemberCountShown = flag1;
        k = i;
        if(i == -1 && "v.whatshot".equals(s))
            k = -2;
        mCircleIconDrawable = (Drawable)sCircleTypeIcons.get(k);
        switch(k) {
	        case -2:
	        	mCircleName = getResources().getString(R.string.stream_whats_hot);
	        	break;
	        case 5:
	        	mCircleName = getResources().getString(R.string.acl_your_circles);
	        	break;
	        case 7:
	        	mCircleName = getResources().getString(R.string.acl_extended_network);
	        	break;
	        case 9:
	        	mCircleName = getResources().getString(R.string.acl_public);
	        	break;
        	default:
        		mCircleName = s1;
        		break;
        }
        
        SpannableUtils.setTextWithHighlight(mNameTextView, mCircleName, mNameTextBuilder, mHighlightedText, sBoldSpan, sColorSpan);
        if(mCircleIconDrawable == null) {
        	switch(k) {
	        	case -2:
	        		break;
	        	case 5:
	        		l = R.drawable.ic_circles_active;
	        		break;
	        	case 7:
	        		if(flag)
	                    l = R.drawable.list_extended_red;
	                else
	                    l = R.drawable.list_extended;
	        		break;
	        	case 8:
	        		l = R.drawable.list_domain;
	        		break;
	        	case 9:
	        		if(flag)
	                    l = R.drawable.list_public_red;
	                else
	                    l = R.drawable.list_public;
	        		break;
	        	case 10:
	        		l = R.drawable.list_circle_blocked;
	        		break;
	        	case 101:
	        		l = R.drawable.ic_private;
	        		break;
	        	default:
        			l = R.drawable.list_circle;
        			break;
        	}
        	mCircleIconDrawable = getContext().getApplicationContext().getResources().getDrawable(l);
            sCircleTypeIcons.put(k, mCircleIconDrawable);
        }

        if(mMemberCountShown)
        {
            mCountTextView.setText((new StringBuilder("(")).append(j).append(")").toString());
            mCountTextView.setVisibility(0);
        } else
        {
            mCountTextView.setVisibility(8);
        }
    }

    public void setHighlightedText(String s)
    {
        if(s == null)
            mHighlightedText = null;
        else
            mHighlightedText = s.toUpperCase();
    }

    public void setMemberCountVisible(boolean flag)
    {
        mMemberCountVisible = flag;
    }

    public void setPackedMemberIds(String s) {
        int i;
        int k;
        mAvatarCount = Math.min(16, mMemberCount);
        i = 0;
        if(s != null)
        {
            int l1;
            for(int k1 = 0; i < mAvatarCount && k1 < s.length(); k1 = l1 + 1)
            {
                l1 = s.indexOf('|', k1);
                if(l1 == -1)
                    l1 = s.length();
                String s3 = EsPeopleData.extractGaiaId(s.substring(k1, l1));
                if(s3 != null)
                {
                    String as[] = mGaiaIds;
                    int i2 = i + 1;
                    as[i] = s3;
                    i = i2;
                }
            }

        }
        for(; mAvatarHolders.size() > mAvatarCount; mAvatarHolders.remove(-1 + mAvatarHolders.size()));
        for(int j = 0; j < mAvatarCount; j++)
        {
            if(mAvatarHolders.size() <= j)
                mAvatarHolders.add(new AvatarHolder());
            ((AvatarHolder)mAvatarHolders.get(j)).setAvatarVisible(false);
        }

        // TODO
    }

    public final void updateContentDescription() {
        Resources resources = getResources();
        int i = R.plurals.circle_entry_content_description;
        int j = mMemberCount;
        Object aobj[] = new Object[2];
        aobj[0] = mCircleName;
        aobj[1] = Integer.valueOf(mMemberCount);
        setContentDescription(resources.getQuantityString(i, j, aobj));
    }
    
	private final class AvatarHolder implements ImageCache.ImageConsumer {

		private boolean mAvatarInvalidated;
		private Bitmap mBitmap;
		private String mGaiaId;
		private AvatarRequest mRequest;
		private boolean mVisible;

		AvatarHolder() {
			mRequest = new AvatarRequest();
		}

		public final Bitmap getBitmap() {
			return mBitmap;
		}

		public final String getGaiaId() {
			return mGaiaId;
		}

		public final boolean isAvatarVisible() {
			return mVisible;
		}

		public final void loadAvatar() {
			if (mBitmap == null)
				if (mGaiaId == null || !mVisible) {
					mBitmap = null;
					invalidate();
				} else {
					mAvatarCache.loadImage(this, mRequest);
				}
		}

		public final void refreshIfNecessary() {
			if (mAvatarInvalidated && mVisible && mRequest != null) {
				mAvatarInvalidated = false;
				mAvatarCache.refreshImage(this, mRequest);
			}
		}

		public final void reloadAvatar() {
			if (mGaiaId != null && mVisible)
				mAvatarInvalidated = true;
		}

		public final void setAvatarVisible(boolean flag) {
			mVisible = flag;
		}

		public final void setBitmap(Bitmap bitmap, boolean flag) {
			if (mVisible) {
				mBitmap = bitmap;
				invalidate();
			}
		}

		public final void setGaiaId(String s) {
			if (!TextUtils.equals(mGaiaId, s)) {
				mGaiaId = s;
				mRequest = new AvatarRequest(s, 0);
				mBitmap = null;
			}
		}

	}

}
