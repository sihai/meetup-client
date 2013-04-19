/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class EventDetailsOptionRowLayout extends ExactLayout {

	private static float sDividerHeight;
    private static Paint sDividerPaint;
    private static boolean sInitialized;
    private static int sMinHeight;
    private static int sMinSideWidth;
    private static int sPadding;
    private boolean mFirst;
    private View mLeftView;
    private int mMeasuredHeight;
    private int mMeasuredWidth;
    private View mRightView;
    private EventDetailsOptionTitleDescription mText;
    
	public EventDetailsOptionRowLayout(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public EventDetailsOptionRowLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset, 0);
    }

    public EventDetailsOptionRowLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset, i);
    }

    public final void bind(String s, String s1, View view, View view1)
    {
        clear();
        mLeftView = view;
        mRightView = view1;
        if(mLeftView != null)
            addView(mLeftView);
        if(mRightView != null)
            addView(mRightView);
        mText.bind(s, s1);
    }

    public final void bind(String s, List list, View view, View view1)
    {
        clear();
        mLeftView = view;
        mRightView = null;
        if(mLeftView != null)
            addView(mLeftView);
        if(mRightView != null)
            addView(mRightView);
        mText.bind(s, list);
    }

    public final void clear()
    {
        if(mLeftView != null)
        {
            removeView(mLeftView);
            mLeftView = null;
        }
        if(mRightView != null)
        {
            removeView(mRightView);
            mRightView = null;
        }
        mText.clear();
    }

    protected void init(Context context, AttributeSet attributeset, int i)
    {
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sMinSideWidth = resources.getDimensionPixelSize(R.dimen.event_card_details_option_min_side_width);
            sMinHeight = resources.getDimensionPixelSize(R.dimen.event_card_details_option_min_height);
            sPadding = resources.getDimensionPixelSize(R.dimen.event_card_padding);
            Paint paint = new Paint();
            sDividerPaint = paint;
            paint.setColor(resources.getColor(R.color.card_event_divider));
            sDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.event_card_divider_stroke_width));
            sDividerHeight = sDividerPaint.getStrokeWidth();
            sInitialized = true;
        }
        mText = new EventDetailsOptionTitleDescription(context, attributeset, i);
        mText.setLayoutParams(new ExactLayout.LayoutParams(0, 0));
        addView(mText);
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Theme);
        setBackgroundDrawable(typedarray.getDrawable(5));
        typedarray.recycle();
        setWillNotDraw(false);
    }

    protected void onDraw(Canvas canvas)
    {
        if(mFirst)
            canvas.drawLine(0.0F, 0.0F, mMeasuredWidth, 0.0F, sDividerPaint);
        int i = Math.round((float)mMeasuredHeight - sDividerHeight);
        canvas.drawLine(0.0F, i, mMeasuredWidth, i, sDividerPaint);
        super.onDraw(canvas);
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        int i1 = k;
        boolean flag = mFirst;
        int j1 = 0;
        if(flag)
            j1 = (int)(0.0F + sDividerHeight);
        View view = mLeftView;
        int k1 = 0;
        if(view != null)
        {
            measure(mLeftView, k, 0x80000000, 0, 0);
            int j2 = Math.max(2 * sPadding + mLeftView.getMeasuredWidth(), sMinSideWidth);
            setCorner(mLeftView, 0, j1);
            setCenterBounds(mLeftView, j2, 0);
            k1 = j2 + 0;
            i1 = k - j2;
        }
        if(mRightView != null)
        {
            measure(mRightView, i1, 0x80000000, 0, 0);
            int i2 = Math.max(2 * sPadding + mRightView.getMeasuredWidth(), sMinSideWidth);
            setCorner(mRightView, k - i2, j1);
            setCenterBounds(mRightView, i2, 0);
            i1 -= i2;
        }
        measure(mText, i1, 0x80000000, l, 0);
        setCorner(mText, k1, j1);
        View aview[] = new View[3];
        aview[0] = mRightView;
        aview[1] = mLeftView;
        aview[2] = mText;
        int l1 = Math.max(sMinHeight, getMaxHeight(aview) + 2 * sPadding);
        verticallyCenter(l1, aview);
        mMeasuredHeight = (int)((float)(j1 + l1) + sDividerHeight);
        mMeasuredWidth = k;
        setMeasuredDimension(k, mMeasuredHeight);
    }

    public void setFirst(boolean flag)
    {
        mFirst = flag;
    }

}
