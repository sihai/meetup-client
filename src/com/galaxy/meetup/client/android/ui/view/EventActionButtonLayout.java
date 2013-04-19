/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class EventActionButtonLayout extends ExactLayout {

	private static int sSpacing;
    private ImageView mImage;
    private TextView mText;
    private boolean sInitialized;
    
    public EventActionButtonLayout(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public EventActionButtonLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset, 0);
    }

    public EventActionButtonLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset, i);
    }

    private void init(Context context, AttributeSet attributeset, int i)
    {
        if(!sInitialized)
        {
            sSpacing = context.getResources().getDimensionPixelSize(R.dimen.event_card_Details_rsvp_action_button_internal_spacing);
            sInitialized = true;
        }
        setClickable(true);
        setFocusable(true);
        setWillNotDraw(false);
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Theme);
        setBackgroundDrawable(typedarray.getDrawable(5));
        typedarray.recycle();
        mText = new TextView(context, attributeset, R.style.EventsRsvpActionButton);
        addView(mText);
        mImage = new ImageView(context, attributeset, i);
        addView(mImage);
    }

    public final void bind(String s, Drawable drawable)
    {
        mImage.setBackgroundDrawable(drawable);
        mText.setText(s);
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        measure(mImage, Math.max(0, k + 0), 0x80000000, l, 0);
        setCorner(mImage, 0, 0);
        int i1 = 0 + (mImage.getMeasuredWidth() + sSpacing);
        measure(mText, Math.max(0, k - i1), 0x80000000, l, 0);
        setCorner(mText, i1, 0);
        int j1 = i1 + mText.getMeasuredWidth();
        View aview[] = new View[2];
        aview[0] = mImage;
        aview[1] = mText;
        verticallyCenter(l, aview);
        int k1 = (k - j1) / 2;
        View aview1[] = new View[2];
        aview1[0] = mText;
        aview1[1] = mImage;
        for(int l1 = Math.max(-1 + aview1.length, 0); l1 >= 0; l1--)
        {
            View view = aview1[l1];
            ExactLayout.LayoutParams layoutparams = (ExactLayout.LayoutParams)view.getLayoutParams();
            if(layoutparams != null)
            {
                layoutparams.x = k1 + layoutparams.x;
                layoutparams.y = 0 + layoutparams.y;
                view.setLayoutParams(layoutparams);
            }
        }

        setMeasuredDimension(resolveSize(k, i), resolveSize(l, j));
    }

}
