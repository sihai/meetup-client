/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class EventDetailsOptionTitleDescription extends ExactLayout {

	private static int sDescriptionColor;
    private static float sDescriptionSize;
    private static boolean sInitialized;
    private static int sTitleColor;
    private static float sTitleSize;
    private int mActiveDescriptionCount;
    private AttributeSet mAttrs;
    private Context mContext;
    private int mDefStyle;
    private ArrayList mDescriptionViews;
    private TextView mTitle;
    
    public EventDetailsOptionTitleDescription(Context context)
    {
        super(context);
        mDescriptionViews = new ArrayList();
        init(context, null, 0);
    }

    public EventDetailsOptionTitleDescription(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mDescriptionViews = new ArrayList();
        init(context, attributeset, 0);
    }

    public EventDetailsOptionTitleDescription(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mDescriptionViews = new ArrayList();
        init(context, attributeset, i);
    }

    private void init(Context context, AttributeSet attributeset, int i)
    {
        mContext = context;
        mAttrs = attributeset;
        mDefStyle = i;
        Resources resources = context.getResources();
        if(!sInitialized)
        {
            sTitleColor = resources.getColor(R.color.event_card_details_option_title_color);
            sTitleSize = resources.getDimension(R.dimen.event_card_details_option_title_size);
            sDescriptionColor = resources.getColor(R.color.event_card_details_option_description_color);
            sDescriptionSize = resources.getDimension(R.dimen.event_card_details_option_description_size);
            sInitialized = true;
        }
        mTitle = TextViewUtils.createText(context, attributeset, i, sTitleSize, sTitleColor, true, true);
        addView(mTitle);
    }

    public final void bind(String s, String s1)
    {
        ArrayList arraylist = new ArrayList();
        if(s == null)
            s = s1;
        else
            arraylist.add(s1);
        bind(s, ((List) (arraylist)));
    }

    public final void bind(String s, List list)
    {
        mTitle.setText(s);
        TextView textview = mTitle;
        byte byte0;
        if(TextUtils.isEmpty(mTitle.getText()))
            byte0 = 8;
        else
            byte0 = 0;
        textview.setVisibility(byte0);
        for(int i = -1 + mDescriptionViews.size(); i >= 0; i--)
            removeView((View)mDescriptionViews.get(i));

        mActiveDescriptionCount = 0;
        if(list != null)
        {
            int j = list.size();
            for(int k = 0; k < j; k++)
            {
                if(TextUtils.isEmpty((String)list.get(k)))
                    continue;
                if(k > -1 + mDescriptionViews.size())
                    mDescriptionViews.add(TextViewUtils.createText(mContext, mAttrs, mDefStyle, sDescriptionSize, sDescriptionColor, false, true));
                TextView textview1 = (TextView)mDescriptionViews.get(k);
                textview1.setText((CharSequence)list.get(k));
                addView(textview1);
                mActiveDescriptionCount = 1 + mActiveDescriptionCount;
            }

        }
    }

    public final void clear()
    {
        mTitle.setText(null);
        for(int i = -1 + mDescriptionViews.size(); i >= 0; i--)
            removeView((View)mDescriptionViews.get(i));

        mActiveDescriptionCount = 0;
    }

    protected void measureChildren(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        measure(mTitle, k, 0x80000000, l, 0);
        setCorner(mTitle, 0, 0);
        int i1;
        int j1;
        if(TextUtils.isEmpty(mTitle.getText()))
            i1 = 0;
        else
            i1 = mTitle.getMeasuredHeight();
        j1 = i1 + 0;
        for(int k1 = 0; k1 < mActiveDescriptionCount; k1++)
        {
            TextView textview = (TextView)mDescriptionViews.get(k1);
            measure(textview, k, 0x80000000, l, 0);
            setCorner(textview, 0, j1);
            j1 += textview.getMeasuredHeight();
        }

    }
}
