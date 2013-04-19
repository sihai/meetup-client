/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public abstract class CheckableListItemView extends ViewGroup implements Checkable,
		OnCheckedChangeListener {

	protected static final StyleSpan sBoldSpan = new StyleSpan(1);
    private static Drawable sCheckedStateBackground;
    protected static ForegroundColorSpan sColorSpan;
    protected CheckBox mCheckBox;
    protected boolean mCheckBoxVisible;
    protected boolean mChecked;
    private OnItemCheckedChangeListener mListener;
    
	public CheckableListItemView(Context context) {
        this(context, null);
    }

    public CheckableListItemView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        if(sCheckedStateBackground == null)
        {
            Resources resources = context.getApplicationContext().getResources();
            sCheckedStateBackground = resources.getDrawable(R.drawable.list_selected_holo);
            sColorSpan = new ForegroundColorSpan(resources.getColor(R.color.search_query_highlight_color));
        }
    }

    protected void drawBackground(Canvas canvas, Drawable drawable)
    {
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    public boolean isChecked()
    {
        boolean flag;
        if(mCheckBoxVisible)
            flag = mCheckBox.isChecked();
        else
            flag = mChecked;
        return flag;
    }

    public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
        mListener.onItemCheckedChanged(this, mCheckBox.isChecked());
    }

    protected void onDraw(Canvas canvas) {
        if(!mCheckBoxVisible && mChecked)
            drawBackground(canvas, sCheckedStateBackground);
        super.onDraw(canvas);
    }

    public void setCheckBoxVisible(boolean flag) {
        mCheckBoxVisible = flag;
        if(!mCheckBoxVisible) {
        	if(mCheckBox != null)
                mCheckBox.setVisibility(8); 
        } else {
        	if(mCheckBox == null) {
                mCheckBox = new CheckBox(getContext());
                mCheckBox.setOnCheckedChangeListener(this);
                mCheckBox.setFocusable(false);
                addView(mCheckBox);
            }
            mCheckBox.setVisibility(0);
        }
    }

    public void setChecked(boolean flag) {
        if(!mCheckBoxVisible) { 
        	if(flag != mChecked) {
                mChecked = flag;
                boolean flag1;
                if(!flag)
                    flag1 = true;
                else
                    flag1 = false;
                setWillNotDraw(flag1);
                invalidate();
            } 
        } else {
        	mCheckBox.setChecked(flag);
        }
    }

    public void setEnabled(boolean flag)
    {
        super.setEnabled(flag);
        mCheckBox.setEnabled(flag);
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener onitemcheckedchangelistener)
    {
        mListener = onitemcheckedchangelistener;
    }

    public void toggle()
    {
        if(mCheckBoxVisible && mCheckBox.isEnabled())
        {
            mCheckBox.toggle();
        } else
        {
            boolean flag;
            if(!mChecked)
                flag = true;
            else
                flag = false;
            mChecked = flag;
        }
    }

    
	public static interface OnItemCheckedChangeListener {

        void onItemCheckedChanged(CheckableListItemView checkablelistitemview, boolean flag);
    }
}
