/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ConstrainedTextView extends View {

	private ItemClickListener mClickListener;
    private ClickableStaticLayout mContentLayout;
    private ClickableItem mCurrentClickableItem;
    private boolean mEllipsize;
    private int mMaxHeight;
    private int mMaxLines;
    private CharSequence mText;
    private TextPaint mTextPaint;
    
    public ConstrainedTextView(Context context)
    {
        this(context, null);
    }

    public ConstrainedTextView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public ConstrainedTextView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mEllipsize = true;
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.ConstrainedTextView, 0, i);
        int j = typedarray.getColor(2, 0);
        int k = typedarray.getInt(1, 0);
        float f = typedarray.getDimension(0, 0.0F);
        mMaxLines = typedarray.getInt(5, -1);
        mMaxHeight = typedarray.getDimensionPixelSize(3, -1);
        mText = typedarray.getString(4);
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(j);
        mTextPaint.setTextSize(f);
        mTextPaint.setTypeface(Typeface.defaultFromStyle(k));
        if(mText == null)
            mText = "";
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent)
    {
        boolean flag = true;
        int i;
        int j;
        i = (int)motionevent.getX();
        j = (int)motionevent.getY();
        
        switch(motionevent.getAction()) {
        case 0:
        	if(mContentLayout != null && mContentLayout.handleEvent(i, j, 0))
            {
                mCurrentClickableItem = mContentLayout;
                invalidate();
            } else
            {
                flag = false;
            }
        	break;
        case 1:
        	mCurrentClickableItem = null;
            if(mContentLayout != null)
                mContentLayout.handleEvent(i, j, 1);
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

    public final int getLength()
    {
        int i;
        if(mText != null)
            i = mText.length();
        else
            i = 0;
        return i;
    }

    protected void onDraw(Canvas canvas)
    {
        if(mContentLayout != null)
            mContentLayout.draw(canvas);
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        int i1 = android.view.View.MeasureSpec.getMode(j);
        boolean flag;
        TextPaint textpaint;
        int j1;
        int k1;
        if(i1 == 0x80000000 || i1 == 0x40000000 || mMaxLines >= 0 || mMaxHeight >= 0)
            flag = true;
        else
            flag = false;
        if(mMaxHeight >= 0)
            if(i1 == 0)
                l = mMaxHeight;
            else
                l = Math.min(l, mMaxHeight);
        textpaint = mTextPaint;
        j1 = 0;
        if(textpaint != null)
            if(i1 != 0 || mMaxHeight >= 0)
            {
                j1 = l / (int)(mTextPaint.descent() - mTextPaint.ascent());
                if(mMaxLines >= 0)
                    j1 = Math.min(j1, mMaxLines);
            } else
            {
                j1 = mMaxLines;
            }
        if(mTextPaint != null && (j1 > 0 || i1 == 0))
        {
            if(mEllipsize && flag)
                mContentLayout = ClickableStaticLayout.createConstrainedLayout(mTextPaint, mText, k, j1, mClickListener);
            else
                mContentLayout = new ClickableStaticLayout(mText, mTextPaint, k, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false, mClickListener);
            mContentLayout.setPosition(0, 0);
            k1 = mContentLayout.getHeight();
        } else
        {
            mContentLayout = null;
            k1 = 0;
        }
        setMeasuredDimension(k, View.resolveSize(k1, j));
    }

    public void setClickListener(ItemClickListener itemclicklistener)
    {
        mClickListener = itemclicklistener;
    }

    public void setHtmlText(String s, boolean flag)
    {
        mText = ClickableStaticLayout.buildStateSpans(s);
        mEllipsize = flag;
        requestLayout();
    }

    public void setMaxHeight(int i)
    {
        mMaxHeight = i;
    }

    public void setMaxLines(int i)
    {
        mMaxLines = i;
        requestLayout();
    }

    public void setText(CharSequence charsequence)
    {
        if(charsequence == null)
            charsequence = "";
        setText(charsequence, true);
    }

    public void setText(CharSequence charsequence, boolean flag)
    {
        mText = charsequence;
        mEllipsize = flag;
        requestLayout();
    }

    public void setTextColor(int i)
    {
        mTextPaint.setColor(i);
        requestLayout();
    }

    public void setTextPaint(TextPaint textpaint)
    {
        mTextPaint = textpaint;
    }

    public void setTextSize(float f)
    {
        mTextPaint.setTextSize(f);
        requestLayout();
    }

    public void setTypeface(Typeface typeface)
    {
        mTextPaint.setTypeface(typeface);
        requestLayout();
    }
}
