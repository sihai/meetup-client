/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class CircleSpinnerAdapter extends ArrayAdapter {

    public CircleSpinnerAdapter(Context context)
    {
        super(context, R.layout.simple_spinner_item, 0x1020014);
        setDropDownViewResource(R.layout.circle_spinner_dropdown_item);
    }

    private void bindView(View view, int i)
    {
        CircleSpinnerInfo circlespinnerinfo = (CircleSpinnerInfo)getItem(i);
        TextView textview = (TextView)view.findViewById(0x1020015);
        ImageView imageview;
        if(textview != null)
            if(circlespinnerinfo.id != null && circlespinnerinfo.circleType != 10)
                textview.setText((new StringBuilder(" (")).append(circlespinnerinfo.count).append(")").toString());
            else
                textview.setText(null);
        imageview = (ImageView)view.findViewById(0x1020006);
        if(imageview != null)
            if(circlespinnerinfo.iconResId == 0)
            {
                imageview.setVisibility(8);
            } else
            {
                imageview.setVisibility(0);
                imageview.setImageResource(circlespinnerinfo.iconResId);
            }
    }

    public final View getDropDownView(int i, View view, ViewGroup viewgroup)
    {
        View view1 = super.getDropDownView(i, view, viewgroup);
        bindView(view1, i);
        return view1;
    }

    public final View getView(int i, View view, ViewGroup viewgroup)
    {
        View view1 = super.getView(i, view, viewgroup);
        bindView(view1, i);
        return view1;
    }
    
    public static final class CircleSpinnerInfo
    {

        public final String toString()
        {
            return title;
        }

        public final int circleType;
        public final int count;
        public final int iconResId;
        public final String id;
        public final String title;

        public CircleSpinnerInfo(String s, String s1, int i, int j, int k)
        {
            id = s;
            title = s1;
            count = j;
            circleType = i;
            iconResId = k;
        }
    }

}
