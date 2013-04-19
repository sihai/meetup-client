/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.TextPaintUtils;

/**
 * 
 * @author sihai
 *
 */
public class CardTitleDescriptionView extends ViewGroup implements
		ItemClickListener {

	private static TextPaint sDescriptionTextPaint;
    private static boolean sInitialized;
    private Point mDateCorner;
    private TextView mDateTextView;
    private Point mDescriptionCorner;
    private ConstrainedTextView mDescriptionTextView;
    private EventActionListener mListener;
    private Point mTitleCorner;
    private TextView mTitleTextView;
    
    public CardTitleDescriptionView(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public CardTitleDescriptionView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset, 0);
    }

    public CardTitleDescriptionView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset, i);
    }

    private void init(Context context, AttributeSet attributeset, int i)
    {
        Resources resources = context.getResources();
        if(!sInitialized)
        {
            TextPaint textpaint = new TextPaint();
            sDescriptionTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDescriptionTextPaint.setColor(resources.getColor(R.color.event_card_activity_description_color));
            sDescriptionTextPaint.setTextSize(resources.getDimension(R.dimen.event_card_activity_description_size));
            sDescriptionTextPaint.linkColor = resources.getColor(R.color.comment_link);
            TextPaintUtils.registerTextPaint(sDescriptionTextPaint, R.dimen.event_card_activity_description_size);
            sInitialized = true;
        }
        mTitleCorner = new Point();
        mTitleTextView = new TextView(context, attributeset, i);
        mTitleTextView.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -2));
        mTitleTextView.setTextSize(0, resources.getDimensionPixelSize(R.dimen.event_card_activity_title_size));
        mTitleTextView.setTypeface(null, 1);
        mTitleTextView.setTextColor(resources.getColor(R.color.event_card_activity_title_color));
        mTitleTextView.setSingleLine();
        mTitleTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        addView(mTitleTextView);
        mDateCorner = new Point();
        mDateTextView = new TextView(context, attributeset, i);
        mDateTextView.setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -2));
        mDateTextView.setTextSize(0, resources.getDimensionPixelSize(R.dimen.event_card_activity_time_size));
        mDateTextView.setTextColor(resources.getColor(R.color.event_card_activity_time_color));
        mDateTextView.setSingleLine();
        mDateTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        addView(mDateTextView);
        mDescriptionCorner = new Point();
        mDescriptionTextView = new ConstrainedTextView(context, attributeset, i);
        mDescriptionTextView.setTextPaint(sDescriptionTextPaint);
        mDescriptionTextView.setClickListener(this);
        addView(mDescriptionTextView);
    }

    public final void clear()
    {
        mTitleTextView.setText(null);
        mDateTextView.setText(null);
        mDescriptionTextView.setText(null);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        mTitleTextView.layout(mTitleCorner.x, mTitleCorner.y, mTitleCorner.x + mTitleTextView.getMeasuredWidth(), mTitleCorner.y + mTitleTextView.getMeasuredHeight());
        mDateTextView.layout(mDateCorner.x, mDateCorner.y, mDateCorner.x + mDateTextView.getMeasuredWidth(), mDateCorner.y + mDateTextView.getMeasuredHeight());
        mDescriptionTextView.layout(mDescriptionCorner.x, mDescriptionCorner.y, mDescriptionCorner.x + mDescriptionTextView.getMeasuredWidth(), mDescriptionCorner.y + mDescriptionTextView.getMeasuredHeight());
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        int i1 = android.view.View.MeasureSpec.getMode(j);
        mDateTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(l, i1));
        int j1 = mDateTextView.getMeasuredWidth();
        int k1 = mDateTextView.getMeasuredHeight();
        mDateCorner.x = k - j1;
        mDateCorner.y = 0;
        mTitleTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(mDateCorner.x, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(l, i1));
        int l1 = mTitleTextView.getMeasuredHeight();
        mTitleCorner.x = 0;
        mTitleCorner.y = 0;
        Point point = mTitleCorner;
        point.y = point.y + Math.max(0, k1 - l1);
        Point point1 = mDateCorner;
        point1.y = point1.y + Math.max(0, l1 - k1);
        int i2 = 0 + (l1 + mTitleCorner.y);
        if(mDescriptionTextView.getLength() > 0)
        {
            mDescriptionTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(k, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(l - i2, i1));
            int j2 = mDescriptionTextView.getMeasuredHeight();
            mDescriptionCorner.x = 0;
            mDescriptionCorner.y = i2;
            i2 += j2;
        }
        setMeasuredDimension(resolveSize(k, i), resolveSize(i2, j));
    }

    public final void onSpanClick(URLSpan urlspan)
    {
        if(mListener != null)
            mListener.onLinkClicked(urlspan.getURL());
    }

    public final void onUserImageClick(String s, String s1)
    {
    }

    public void setListener(EventActionListener eventactionlistener)
    {
        mListener = eventactionlistener;
    }

    public void setText(CharSequence charsequence, CharSequence charsequence1, CharSequence charsequence2, boolean flag)
    {
        mTitleTextView.setText(charsequence);
        mDateTextView.setText(charsequence1);
        ConstrainedTextView constrainedtextview = mDescriptionTextView;
        String s;
        if(charsequence2 != null)
            s = charsequence2.toString();
        else
            s = null;
        constrainedtextview.setHtmlText(s, flag);
    }
}
