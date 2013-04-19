/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;

/**
 * 
 * @author sihai
 *
 */
public class CardViewLayout extends ExactLayout implements Recyclable {

	private static Drawable sBackground;
    private static boolean sInitialized;
    private static int sPaddingBottom;
    private static int sPaddingLeft;
    private static int sPaddingRight;
    private static int sPaddingTop;
    
    public CardViewLayout(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public CardViewLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset, 0);
    }

    public CardViewLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset, i);
    }

    private static int adjustPadding(int i, int j, boolean flag)
    {
        int k;
        if(flag)
            k = 1;
        else
            k = -1;
        return Math.max(i + k * j, 0);
    }

    protected void init(Context context, AttributeSet attributeset, int i)
    {
        int flag = 1;
        Resources resources = context.getResources();
        if(!sInitialized)
        {
            sPaddingLeft = (int)resources.getDimension(R.dimen.card_border_left_padding);
            sPaddingTop = (int)resources.getDimension(R.dimen.card_border_top_padding);
            sPaddingRight = (int)resources.getDimension(R.dimen.card_border_right_padding);
            sPaddingBottom = (int)resources.getDimension(R.dimen.card_border_bottom_padding);
            sBackground = resources.getDrawable(R.drawable.bg_tacos);
            sInitialized = true;
        }
        toggleCardBorderStyle(true);
        boolean flag1;
        if(context.getResources().getConfiguration().orientation == 2)
            flag1 = true;
        else
            flag1 = false;
        if(flag1)
            flag = 2;
        setLayoutParams(new ColumnGridView.LayoutParams(flag, -3));
    }

    public final void toggleCardBorderStyle(boolean flag)
    {
        Drawable drawable;
        if(flag)
            drawable = sBackground;
        else
            drawable = null;
        setBackground(drawable);
        setPadding(adjustPadding(getPaddingLeft(), sPaddingLeft, flag), adjustPadding(getPaddingTop(), sPaddingTop, flag), adjustPadding(getPaddingRight(), sPaddingRight, flag), adjustPadding(getPaddingBottom(), sPaddingBottom, flag));
    }
}
