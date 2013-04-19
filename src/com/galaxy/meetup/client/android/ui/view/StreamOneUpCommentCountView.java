/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.util.TextPaintUtils;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpCommentCountView extends OneUpBaseView implements
		Recyclable {

	private static Paint sBackgroundPaint;
    private static int sCountMarginLeft;
    private static TextPaint sCountPaint;
    private static Paint sDividerPaint;
    private static int sDividerWidth;
    private static int sMarginLeft;
    private static int sMarginRight;
    private boolean mContentDescriptionDirty;
    private String mCount;
    private PositionedStaticLayout mCountLayout;
    private RectF mDivider;
    
    public StreamOneUpCommentCountView(Context context)
    {
        super(context);
        mDivider = new RectF();
        mContentDescriptionDirty = true;
        if(sBackgroundPaint == null)
        {
            Resources resources = getContext().getResources();
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_right);
            sCountMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_count_margin_left);
            sDividerWidth = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_count_divider_width);
            TextPaint textpaint = new TextPaint();
            sCountPaint = textpaint;
            textpaint.setAntiAlias(true);
            sCountPaint.setColor(resources.getColor(R.color.stream_one_up_comment_count));
            sCountPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_comment_count_text_size));
            TextPaintUtils.registerTextPaint(sCountPaint, R.dimen.stream_one_up_comment_count_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sDividerPaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_comment_count_divider));
            sDividerPaint.setStyle(android.graphics.Paint.Style.STROKE);
            sDividerPaint.setStrokeWidth(sDividerWidth);
        }
    }

    public StreamOneUpCommentCountView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mDivider = new RectF();
        mContentDescriptionDirty = true;
        if(sBackgroundPaint == null)
        {
            Resources resources = getContext().getResources();
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_right);
            sCountMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_count_margin_left);
            sDividerWidth = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_count_divider_width);
            TextPaint textpaint = new TextPaint();
            sCountPaint = textpaint;
            textpaint.setAntiAlias(true);
            sCountPaint.setColor(resources.getColor(R.color.stream_one_up_comment_count));
            sCountPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_comment_count_text_size));
            TextPaintUtils.registerTextPaint(sCountPaint, R.dimen.stream_one_up_comment_count_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sDividerPaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_comment_count_divider));
            sDividerPaint.setStyle(android.graphics.Paint.Style.STROKE);
            sDividerPaint.setStrokeWidth(sDividerWidth);
        }
    }

    public StreamOneUpCommentCountView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mDivider = new RectF();
        mContentDescriptionDirty = true;
        if(sBackgroundPaint == null)
        {
            Resources resources = getContext().getResources();
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_right);
            sCountMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_count_margin_left);
            sDividerWidth = resources.getDimensionPixelOffset(R.dimen.stream_one_up_comment_count_divider_width);
            TextPaint textpaint = new TextPaint();
            sCountPaint = textpaint;
            textpaint.setAntiAlias(true);
            sCountPaint.setColor(resources.getColor(R.color.stream_one_up_comment_count));
            sCountPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_comment_count_text_size));
            TextPaintUtils.registerTextPaint(sCountPaint, R.dimen.stream_one_up_comment_count_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sDividerPaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_comment_count_divider));
            sDividerPaint.setStyle(android.graphics.Paint.Style.STROKE);
            sDividerPaint.setStrokeWidth(sDividerWidth);
        }
    }

    public final void bind(Cursor cursor)
    {
        setCount(cursor.getInt(2));
        invalidate();
        requestLayout();
    }

    public void invalidate()
    {
        super.invalidate();
        if(mContentDescriptionDirty)
        {
            setContentDescription(mCount);
            mContentDescriptionDirty = false;
        }
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int i = getWidth();
        int j = getHeight();
        canvas.drawRect(0.0F, 0.0F, i, j, sBackgroundPaint);
        if(mCountLayout != null)
        {
            int k = mCountLayout.getLeft();
            int l = mCountLayout.getTop();
            canvas.translate(k, l);
            mCountLayout.draw(canvas);
            canvas.translate(-k, -l);
            canvas.drawLine(mDivider.left, mDivider.top, mDivider.right, mDivider.bottom, sDividerPaint);
        }
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = getPaddingLeft() + sMarginLeft;
        int l = getPaddingTop();
        int i1 = getMeasuredWidth();
        int j1 = i1 - k - sMarginRight;
        int k1 = (int)sCountPaint.measureText(mCount);
        mCountLayout = new PositionedStaticLayout(mCount, sCountPaint, k1, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        mCountLayout.setPosition(k, l);
        android.graphics.Paint.FontMetricsInt fontmetricsint = sCountPaint.getFontMetricsInt();
        int l1 = k + (k1 + sCountMarginLeft);
        int i2 = l + (fontmetricsint.descent - fontmetricsint.ascent - sDividerWidth) / 2;
        mDivider.set(l1, i2, (k + j1) - sCountMarginLeft, i2);
        setMeasuredDimension(i1, l + mCountLayout.getHeight() + getPaddingBottom());
        if(mOnMeasuredListener != null)
            mOnMeasuredListener.onMeasured(this);
    }

    public void onRecycle()
    {
        mCountLayout = null;
        mCount = null;
    }

    public void setCount(int i)
    {
        Resources resources = getContext().getResources();
        int j = R.plurals.stream_one_up_comment_count;
        Object aobj[] = new Object[1];
        aobj[0] = Integer.valueOf(i);
        mCount = resources.getQuantityString(j, i, aobj).toUpperCase(resources.getConfiguration().locale);
        mCountLayout = null;
        mContentDescriptionDirty = true;
    }
}
