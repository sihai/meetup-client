/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ImageTextButton extends RelativeLayout {

	private ImageView mImage;
    private TextView mText;
    
    public ImageTextButton(Context context)
    {
        this(context, null);
    }

    public ImageTextButton(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset);
    }

    public ImageTextButton(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset);
    }

    private void init(Context context, AttributeSet attributeset)
    {
        LayoutInflater.from(context).inflate(R.layout.image_text_button, this);
        mText = (TextView)findViewById(R.id.text_view);
        mImage = (ImageView)findViewById(R.id.image_view);
        if(attributeset != null)
        {
            TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.ImageTextButton);
            android.graphics.drawable.Drawable drawable = typedarray.getDrawable(1);
            mImage.setImageDrawable(drawable);
            String s = typedarray.getString(0);
            mText.setText(s);
            typedarray.recycle();
        }
    }

    public void setEnabled(boolean flag)
    {
        super.setEnabled(flag);
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            float f;
            if(flag)
                f = 1.0F;
            else
                f = 0.5F;
            setAlpha(f);
        } else
        {
            mText.setEnabled(flag);
            mImage.setEnabled(flag);
        }
    }

    public void setText(String s)
    {
        mText.setText(s);
    }
}
