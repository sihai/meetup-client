/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.TextPaintUtils;

/**
 * 
 * @author sihai
 *
 */
public class BarGraphView extends View {

	protected static int BAR_GRAPH_HEIGHT;
    protected static int BAR_SPACING;
    protected static int LABEL_BAR_SPACING;
    protected static int TOTAL_GRAPH_SPACING;
    protected static Paint sBarGraphPaint;
    protected static TextPaint sLabelTextPaint;
    protected static TextPaint sTotalTextPaint;
    protected static TextPaint sValueTextPaint;
    protected InternalRowInfo mInternalRowInfos[];
    protected long mMaxValue;
    protected StaticLayout mTotalLayout;
    protected long mTotalValue;
    protected String mUnits;
    
    public BarGraphView(Context context)
    {
        this(context, null);
    }

    public BarGraphView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public BarGraphView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        if(sTotalTextPaint == null)
        {
            Resources resources = context.getResources();
            TextPaint textpaint = new TextPaint();
            sTotalTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sTotalTextPaint.setColor(resources.getColor(R.color.bar_graph_total));
            sTotalTextPaint.setTextSize(resources.getDimension(R.dimen.bar_graph_total_text_size));
            sTotalTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            TextPaintUtils.registerTextPaint(sTotalTextPaint, R.dimen.bar_graph_total_text_size);
            TextPaint textpaint1 = new TextPaint();
            sLabelTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sLabelTextPaint.setColor(resources.getColor(R.color.bar_graph_label));
            sLabelTextPaint.setTextSize(resources.getDimension(R.dimen.bar_graph_label_text_size));
            sLabelTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            TextPaintUtils.registerTextPaint(sLabelTextPaint, R.dimen.bar_graph_label_text_size);
            TextPaint textpaint2 = new TextPaint();
            sValueTextPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sValueTextPaint.setColor(resources.getColor(R.color.bar_graph_value));
            sValueTextPaint.setTextSize(resources.getDimension(R.dimen.bar_graph_value_text_size));
            sValueTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            TextPaintUtils.registerTextPaint(sValueTextPaint, R.dimen.bar_graph_value_text_size);
            Paint paint = new Paint();
            sBarGraphPaint = paint;
            paint.setColor(resources.getColor(R.color.bar_graph_bar));
            sBarGraphPaint.setStyle(android.graphics.Paint.Style.FILL);
            TOTAL_GRAPH_SPACING = (int)resources.getDimension(R.dimen.bar_graph_total_graph_spacing);
            BAR_GRAPH_HEIGHT = (int)resources.getDimension(R.dimen.bar_graph_bar_height);
            LABEL_BAR_SPACING = (int)resources.getDimension(R.dimen.bar_graph_label_text_bar_spacing);
            BAR_SPACING = (int)resources.getDimension(R.dimen.bar_graph_bar_spacing);
        }
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(mInternalRowInfos != null)
        {
            int i = getPaddingLeft();
            int j = getPaddingRight();
            int k = getWidth() - i - j;
            int l = mInternalRowInfos.length;
            int i1 = getPaddingTop();
            if(l > 0)
            {
                canvas.translate(i, i1);
                mTotalLayout.draw(canvas);
                canvas.translate(-i, -i1);
                i1 += mTotalLayout.getHeight() + TOTAL_GRAPH_SPACING;
            }
            int j1 = 0;
            while(j1 < l) 
            {
                InternalRowInfo internalrowinfo = mInternalRowInfos[j1];
                if(internalrowinfo.mLabelLayout != null && internalrowinfo.mValueLayout != null)
                {
                    float f = (float)internalrowinfo.mValue / (float)mMaxValue;
                    canvas.translate(i, i1);
                    internalrowinfo.mLabelLayout.draw(canvas);
                    canvas.translate(-i, -i1);
                    int k1 = i1 + (internalrowinfo.mLabelLayout.getHeight() + LABEL_BAR_SPACING);
                    canvas.translate(i, k1);
                    internalrowinfo.mValueLayout.draw(canvas);
                    canvas.translate(-i, -k1);
                    int l1 = k1 + (internalrowinfo.mValueLayout.getHeight() + LABEL_BAR_SPACING);
                    canvas.drawRect(i, l1, (float)i + f * (float)k, l1 + BAR_GRAPH_HEIGHT, sBarGraphPaint);
                    i1 = l1 + (BAR_GRAPH_HEIGHT + BAR_SPACING);
                }
                j1++;
            }
        }
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getMode(i);
        int l = android.view.View.MeasureSpec.getSize(i);
        int i1;
        int j1;
        int k1;
        Resources resources;
        int l1;
        int i2;
        int j2;
        int k2;
        InternalRowInfo internalrowinfo;
        int l2;
        int i3;
        Object aobj[];
        int j3;
        int k3;
        Object aobj1[];
        if(k != 0x40000000)
            if(k == 0x80000000)
                l = Math.min(480, l);
            else
                l = 480;
        i1 = android.view.View.MeasureSpec.getMode(j);
        j1 = android.view.View.MeasureSpec.getSize(j);
        k1 = l - (getPaddingLeft() + getPaddingRight());
        if(i1 != 0x40000000) {
	        j1 = getPaddingTop() + getPaddingBottom();
	        resources = getResources();
	        if(mInternalRowInfos != null)
	        {
	            l1 = mInternalRowInfos.length;
	            if(l1 > 0)
	            {
	                k3 = R.string.network_statistics_total;
	                aobj1 = new Object[2];
	                aobj1[0] = Long.valueOf(mTotalValue);
	                aobj1[1] = mUnits;
	                mTotalLayout = new StaticLayout(resources.getString(k3, aobj1), sTotalTextPaint, k1, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
	                i2 = j1 + (mTotalLayout.getHeight() + TOTAL_GRAPH_SPACING);
	            } else
	            {
	                i2 = j1;
	            }
	            j2 = 0;
	            for(k2 = i2; j2 < l1; k2 = j3)
	            {
	                internalrowinfo = mInternalRowInfos[j2];
	                internalrowinfo.mLabelLayout = new StaticLayout(internalrowinfo.mLabel, sLabelTextPaint, k1, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
	                l2 = k2 + (internalrowinfo.mLabelLayout.getHeight() + LABEL_BAR_SPACING);
	                i3 = R.string.network_statistics_value;
	                aobj = new Object[3];
	                aobj[0] = Long.valueOf(internalrowinfo.mValue);
	                aobj[1] = mUnits;
	                aobj[2] = Long.valueOf((100L * internalrowinfo.mValue) / mTotalValue);
	                internalrowinfo.mValueLayout = new StaticLayout(resources.getString(i3, aobj), sValueTextPaint, k1, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
	                j3 = l2 + (internalrowinfo.mValueLayout.getHeight() + LABEL_BAR_SPACING);
	                j2++;
	            }
	
	            j1 = k2 + l1 * (BAR_GRAPH_HEIGHT + BAR_SPACING);
	        }
        }
        setMeasuredDimension(l, j1);
        return;
    }

    public final void update(RowInfo arowinfo[], String s)
    {
        mUnits = s;
        int i = arowinfo.length;
        mInternalRowInfos = new InternalRowInfo[i];
        mTotalValue = 0L;
        mMaxValue = 0xffffffff80000000L;
        for(int j = 0; j < i; j++)
        {
            mInternalRowInfos[j] = new InternalRowInfo();
            mInternalRowInfos[j].mValue = arowinfo[j].mValue;
            mInternalRowInfos[j].mLabel = arowinfo[j].mLabel;
            mTotalValue = mTotalValue + mInternalRowInfos[j].mValue;
            mMaxValue = Math.max(mMaxValue, mInternalRowInfos[j].mValue);
        }

        invalidate();
        requestLayout();
    }
    
	protected static final class InternalRowInfo
    {

        public String mLabel;
        public StaticLayout mLabelLayout;
        public long mValue;
        public StaticLayout mValueLayout;

        protected InternalRowInfo()
        {
        }
    }

    public static final class RowInfo
    {

        public String mLabel;
        public long mValue;

        public RowInfo()
        {
        }
    }
}
