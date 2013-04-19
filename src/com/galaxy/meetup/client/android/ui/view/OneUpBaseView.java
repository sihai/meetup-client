/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author sihai
 *
 */
public class OneUpBaseView extends View {

	OnMeasuredListener mOnMeasuredListener;
	
	public OneUpBaseView(Context context)
    {
        super(context);
    }

    public OneUpBaseView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public OneUpBaseView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public static void onStart()
    {
    }

    public static void onStop()
    {
    }

    public void setOnMeasureListener(OnMeasuredListener onmeasuredlistener)
    {
        mOnMeasuredListener = onmeasuredlistener;
    }


	public static interface OnMeasuredListener {

        public abstract void onMeasured(View view);
    }
}
