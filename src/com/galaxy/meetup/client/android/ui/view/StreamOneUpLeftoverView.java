/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpLeftoverView extends View implements Recyclable {

	private static Paint sBackgroundPaint;
    private int mFixedHeight;
    
    public StreamOneUpLeftoverView(Context context)
    {
        this(context, null);
    }

    public StreamOneUpLeftoverView(Context context, AttributeSet attributeset)
    {
        this(context, null, 0);
    }

    public StreamOneUpLeftoverView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        if(sBackgroundPaint == null)
        {
            Resources resources = getContext().getResources();
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
        }
    }

    public final void bind(int i)
    {
        mFixedHeight = 0;
        if(i < 0)
            i = 0;
        mFixedHeight = i;
        invalidate();
        requestLayout();
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int i = getWidth();
        int j = getHeight();
        canvas.drawRect(0.0F, 0.0F, i, j, sBackgroundPaint);
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        setMeasuredDimension(getMeasuredWidth(), mFixedHeight);
    }

    public void onRecycle()
    {
        mFixedHeight = 0;
    }
}
