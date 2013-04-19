/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class PostAclButtonView extends FrameLayout {

	private View mButton;
    private ImageView mCheck;
    private Integer mCheckId;
    private ImageView mIcon;
    private Integer mIconActiveId;
    private Integer mIconInactiveId;
    private String mLabel;
    private ConstrainedTextView mText;
    
    public PostAclButtonView(Context context)
    {
        this(context, null);
    }

    public PostAclButtonView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public PostAclButtonView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        addView(LayoutInflater.from(getContext()).inflate(R.layout.post_acl_button, this, false));
        mButton = findViewById(R.id.button);
        mText = (ConstrainedTextView)findViewById(R.id.acl_text);
        mIcon = (ImageView)findViewById(R.id.acl_icon);
        mCheck = (ImageView)findViewById(R.id.acl_check);
    }

    private void initialize(String s, Integer integer, Integer integer1, Integer integer2)
    {
        mIconActiveId = integer;
        mIconInactiveId = integer1;
        mCheckId = integer2;
        setLabelText(s);
        setInactive();
    }

    private static void setImageDrawable(ImageView imageview, Integer integer)
    {
        if(integer == null)
            imageview.setImageDrawable(null);
        else
            imageview.setImageResource(integer.intValue());
    }

    public final void initialize(String s, int i)
    {
        initialize(s, ((Integer) (null)), ((Integer) (null)), Integer.valueOf(i));
    }

    public final void initialize(String s, int i, int j, int k)
    {
        initialize(s, new Integer(i), new Integer(j), new Integer(k));
    }

    public void setActive()
    {
        setImageDrawable(mIcon, mIconActiveId);
        setImageDrawable(mCheck, mCheckId);
    }

    public void setInactive()
    {
        setImageDrawable(mIcon, mIconInactiveId);
        setImageDrawable(mCheck, null);
    }

    public void setLabelText(String s)
    {
        if(s == null)
            s = "";
        mLabel = s;
        mText.setText(mLabel);
    }

    public void setOnClickListener(android.view.View.OnClickListener onclicklistener)
    {
        mButton.setOnClickListener(onclicklistener);
    }
}
