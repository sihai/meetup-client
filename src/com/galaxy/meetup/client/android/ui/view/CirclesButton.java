/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class CirclesButton extends ViewGroup {

	private final TextView mCircleCountText;
    private final Drawable mCircleIcon;
    private final int mCircleIconSpacing;
    private List mCircleNames;
    private final TextView mCirclesText;
    private int mDefaultTextColor;
    private String mFixedText;
    private final int mLabelSpacing;
    private Rect mPadding;
    private ProgressBar mProgressBar;
    private final StringBuilder mSb;
    private boolean mShowIcon;
    private boolean mShowProgressIndicator;
    
    public CirclesButton(Context context)
    {
        this(context, null);
    }

    public CirclesButton(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, R.style.CirclesButton);
    }

    public CirclesButton(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mSb = new StringBuilder();
        mShowIcon = true;
        mDefaultTextColor = 0;
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.CircleButton, 0, i);
        mCircleIconSpacing = typedarray.getDimensionPixelSize(5, 0);
        mLabelSpacing = typedarray.getDimensionPixelSize(6, 0);
        mDefaultTextColor = typedarray.getColor(0, 0);
        mFixedText = typedarray.getString(3);
        mCircleIcon = typedarray.getDrawable(4);
        typedarray.recycle();
        mCircleIcon.setFilterBitmap(true);
        mCirclesText = new TextView(context);
        mCirclesText.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -1));
        mCirclesText.setTextAppearance(context, 0x1030046);
        mCirclesText.setTypeface(mCirclesText.getTypeface(), 1);
        mCirclesText.setSingleLine();
        mCirclesText.setEllipsize(android.text.TextUtils.TruncateAt.END);
        mCirclesText.setGravity(16);
        if(mDefaultTextColor != 0)
            mCirclesText.setTextColor(mDefaultTextColor);
        addView(mCirclesText);
        mCircleCountText = new TextView(context);
        mCircleCountText.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -1));
        mCircleCountText.setTextAppearance(context, 0x1030046);
        mCircleCountText.setGravity(16);
        mCircleCountText.setSingleLine();
        mCircleCountText.setEllipsize(android.text.TextUtils.TruncateAt.END);
        addView(mCircleCountText);
    }

    private void appendCirclesText(StringBuilder stringbuilder, int i)
    {
        List arraylist;
        int j1;
        if(i == mCircleNames.size())
        {
            arraylist = mCircleNames;
        } else
        {
            arraylist = new ArrayList(mCircleNames);
            while(arraylist.size() > i) 
            {
                int j = 0;
                int k = -1;
                for(int l = 0; l < arraylist.size(); l++)
                {
                    int i1 = ((String)arraylist.get(l)).length();
                    if(i1 >= j)
                    {
                        j = i1;
                        k = l;
                    }
                }

                arraylist.remove(k);
            }
        }
        for(j1 = 0; j1 < i; j1++)
        {
            if(j1 > 0)
                stringbuilder.append(", ");
            stringbuilder.append((String)arraylist.get(j1));
        }

        if(i < mCircleNames.size())
            stringbuilder.append(",бн");
    }

    protected void dispatchDraw(Canvas canvas)
    {
        if(mShowIcon)
            mCircleIcon.draw(canvas);
        super.dispatchDraw(canvas);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        boolean flag1 = mShowIcon;
        int i1 = 0;
        if(flag1)
            i1 = 0 + (mCircleIcon.getIntrinsicWidth() + mCircleIconSpacing);
        if(mCircleCountText.getVisibility() == 0)
            i1 += mCircleCountText.getMeasuredWidth() + mLabelSpacing;
        int j1 = i1 + mCirclesText.getMeasuredWidth();
        int k1 = (k - i - j1) / 2;
        if(k1 < mPadding.left)
            k1 = mPadding.left;
        int l1 = k1 + j1;
        if(l1 > k - i - mPadding.right)
            l1 = k - i - mPadding.right;
        if(mShowIcon)
        {
            int i3 = mCircleIcon.getIntrinsicHeight();
            int j3 = mCircleIcon.getIntrinsicWidth();
            int k3 = (l - j - i3) / 2;
            mCircleIcon.setBounds(k1, k3, k1 + j3, k3 + i3);
            k1 += j3 + mCircleIconSpacing;
        }
        if(mCircleCountText.getVisibility() == 0)
            if(mCirclesText.getVisibility() == 0)
            {
                int k2 = mCircleCountText.getMeasuredWidth();
                int l2 = l1 - k2;
                mCircleCountText.layout(l2, mPadding.top, l2 + k2, l - j - mPadding.bottom);
                l1 = l2 - mLabelSpacing;
            } else
            {
                mCircleCountText.layout(k1, mPadding.top, l1, l - j - mPadding.bottom);
            }
        if(mCirclesText.getVisibility() == 0)
            mCirclesText.layout(k1, mPadding.top, l1, l - j - mPadding.bottom);
        if(mShowProgressIndicator)
        {
            int i2 = mProgressBar.getMeasuredWidth();
            int j2 = (k - i - i2) / 2;
            mProgressBar.layout(j2, mPadding.top, j2 + i2, i2 + mPadding.top);
        }
    }

    protected void onMeasure(int i, int j)
    {
        if(mPadding == null)
        {
            mPadding = new Rect();
            Drawable drawable = getBackground();
            if(drawable != null)
                drawable.getPadding(mPadding);
            int l6 = getPaddingLeft();
            if(l6 != 0)
                mPadding.left = l6;
            int i7 = getPaddingRight();
            if(i7 != 0)
                mPadding.right = i7;
        }
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.makeMeasureSpec(android.view.View.MeasureSpec.getSize(j), 0x80000000);
        int i1 = mPadding.left + mPadding.right;
        boolean flag = mShowIcon;
        int j1 = 0;
        if(flag)
        {
            int k6 = mCircleIcon.getIntrinsicWidth();
            j1 = mCircleIcon.getIntrinsicHeight();
            i1 += k6 + mCircleIconSpacing;
        }
        int k1;
        int l2;
        int i3;
        int j3;
        int k3;
        int l3;
        int i4;
        int j4;
        int k4;
        int l4;
        if(k == 0)
            k1 = 0x7fffffff;
        else
            k1 = k - i1;
        mCirclesText.setVisibility(0);
        if(mFixedText != null)
        {
            mCirclesText.setText(mFixedText);
            mCirclesText.measure(android.view.View.MeasureSpec.makeMeasureSpec(k1, 0x80000000), l);
            l2 = mCirclesText.getMeasuredWidth();
            mCircleCountText.setVisibility(8);
        } else
        {
            mSb.setLength(0);
            int l1 = mCircleNames.size();
            appendCirclesText(mSb, l1);
            mCirclesText.setText(mSb);
            mCirclesText.measure(0, l);
            int i2 = mCirclesText.getMeasuredWidth();
            if(i2 <= k1)
            {
                l2 = i2;
                mCircleCountText.setVisibility(8);
            } else
            if(l1 == 1)
            {
                int j6 = k1;
                mCirclesText.measure(android.view.View.MeasureSpec.makeMeasureSpec(j6, 0x80000000), l);
                l2 = mCirclesText.getMeasuredWidth();
                mCircleCountText.setVisibility(8);
            } else
            {
                mCircleCountText.setVisibility(0);
                Resources resources = getContext().getResources();
                int j2 = 0;
                int k2 = l1 - 1;
                do
                {
                    if(k2 <= 0)
                        break;
                    mSb.setLength(0);
                    appendCirclesText(mSb, k2);
                    mCirclesText.setText(mSb);
                    mCirclesText.measure(0, l);
                    i2 = mCirclesText.getMeasuredWidth();
                    int l5 = l1 - k2;
                    TextView textview1 = mCircleCountText;
                    int i6 = R.plurals.circle_button_more_circles;
                    Object aobj1[] = new Object[1];
                    aobj1[0] = Integer.valueOf(l5);
                    textview1.setText(resources.getQuantityString(i6, l5, aobj1));
                    mCircleCountText.measure(0, l);
                    j2 = mCircleCountText.getMeasuredWidth();
                    if(j2 + (i2 + mLabelSpacing) <= k1)
                        break;
                    k2--;
                } while(true);
                if(j2 + (i2 + mLabelSpacing) > k1)
                {
                    mCirclesText.setVisibility(8);
                    i2 = 0;
                    TextView textview = mCircleCountText;
                    Context context = getContext();
                    int k5 = R.string.circle_button_circles;
                    Object aobj[] = new Object[1];
                    aobj[0] = Integer.valueOf(l1);
                    textview.setText(context.getString(k5, aobj));
                    mCircleCountText.measure(android.view.View.MeasureSpec.makeMeasureSpec(k1, 0x80000000), l);
                    j2 = mCircleCountText.getMeasuredWidth();
                }
                l2 = j2 + (i2 + mLabelSpacing);
            }
        }
        i3 = l2 + i1;
        j3 = Math.max(j1, mCirclesText.getMeasuredHeight()) + mPadding.top + mPadding.bottom;
        k3 = resolveSize(i3, i);
        l3 = resolveSize(j3, j);
        i4 = mPadding.left;
        if(mShowIcon)
            i4 += mCircleIcon.getIntrinsicWidth() + mCircleIconSpacing;
        j4 = k3 - mPadding.right;
        if(mCircleCountText.getVisibility() == 0)
        {
            int j5 = mCircleCountText.getMeasuredWidth();
            mCircleCountText.measure(android.view.View.MeasureSpec.makeMeasureSpec(j5, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l3 - mPadding.top - mPadding.bottom, 0x40000000));
            j4 -= j5 + mLabelSpacing;
        }
        k4 = mCirclesText.getMeasuredWidth();
        l4 = j4 - i4;
        if(k4 > l4)
            k4 = j4 - i4;
        mCirclesText.measure(android.view.View.MeasureSpec.makeMeasureSpec(k4, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l3 - mPadding.top - mPadding.bottom, 0x40000000));
        if(mShowProgressIndicator)
        {
            int i5 = android.view.View.MeasureSpec.makeMeasureSpec(l3 - mPadding.top - mPadding.bottom, 0x40000000);
            mProgressBar.measure(i5, i5);
        }
        setMeasuredDimension(k3, l3);
    }

    public void setCircles(List arraylist)
    {
        mFixedText = null;
        mCircleNames = arraylist;
        Collections.sort(mCircleNames, String.CASE_INSENSITIVE_ORDER);
        requestLayout();
    }

	public void setHighlighted(boolean flag) {
		if (flag) {
			setBackgroundResource(R.drawable.plusone_by_me_button);
			mCirclesText.setTextColor(-1);
		} else {
			setBackgroundResource(R.drawable.plusone_button);
			mCirclesText.setTextColor(0xff000000);
		}
	}

    public void setShowIcon(boolean flag) {
        if(mShowIcon != flag)
        {
            mShowIcon = flag;
            requestLayout();
        }
    }

    public void setShowProgressIndicator(boolean flag) {
    	
    	if(mShowProgressIndicator == flag) {
    		return;
    	}
    	mShowProgressIndicator = flag;
    	if(!flag) {
    		if(mProgressBar != null)
            {
                mProgressBar.setVisibility(8);
                mCirclesText.setVisibility(0);
            }
    	} else {
    		if(mProgressBar == null)
            {
                mProgressBar = new ProgressBar(getContext());
                mProgressBar.setIndeterminate(true);
                addView(mProgressBar);
            }
            mProgressBar.setVisibility(0);
            mCirclesText.setVisibility(8);
            mShowIcon = false;
            setHighlighted(false);
    	}
    	requestLayout();
    }

    public void setText(String s)
    {
        if(!TextUtils.equals(mFixedText, s))
        {
            mFixedText = s;
            requestLayout();
        }
    }
}
