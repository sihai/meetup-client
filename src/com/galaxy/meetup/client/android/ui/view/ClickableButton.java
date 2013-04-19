/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ClickableButton implements ClickableItem {

	private static int sBitmapTextXSpacing;
    private static boolean sClickableButtonInitialized;
    private static int sExtraTextXPadding;
    private static int sTextXPadding;
    private Bitmap mBitmap;
    private boolean mClicked;
    private NinePatchDrawable mClickedBackground;
    private CharSequence mContentDescription;
    private Context mContext;
    private NinePatchDrawable mDefaultBackground;
    private ClickableButtonListener mListener;
    private Rect mRect;
    private StaticLayout mTextLayout;
    
    public ClickableButton(Context context, Bitmap bitmap, NinePatchDrawable ninepatchdrawable, NinePatchDrawable ninepatchdrawable1, ClickableButtonListener clickablebuttonlistener, int i, int j, 
            CharSequence charsequence)
    {
        this(context, bitmap, null, null, ninepatchdrawable, ninepatchdrawable1, clickablebuttonlistener, i, j, charsequence, false);
    }

    public ClickableButton(Context context, Bitmap bitmap, CharSequence charsequence, TextPaint textpaint, NinePatchDrawable ninepatchdrawable, NinePatchDrawable ninepatchdrawable1, ClickableButtonListener clickablebuttonlistener, 
            int i, int j)
    {
        this(context, null, charsequence, textpaint, ninepatchdrawable, ninepatchdrawable1, null, i, j, charsequence, false);
    }

    public ClickableButton(Context context, Bitmap bitmap, CharSequence charsequence, TextPaint textpaint, NinePatchDrawable ninepatchdrawable, NinePatchDrawable ninepatchdrawable1, ClickableButtonListener clickablebuttonlistener, 
            int i, int j, CharSequence charsequence1)
    {
        this(context, bitmap, charsequence, textpaint, ninepatchdrawable, ninepatchdrawable1, clickablebuttonlistener, i, j, charsequence1, false);
    }

    public ClickableButton(Context context, Bitmap bitmap, CharSequence charsequence, TextPaint textpaint, NinePatchDrawable ninepatchdrawable, NinePatchDrawable ninepatchdrawable1, ClickableButtonListener clickablebuttonlistener, 
            int i, int j, CharSequence charsequence1, boolean flag)
    {
        initialize(context);
        mContext = context;
        mBitmap = bitmap;
        mDefaultBackground = ninepatchdrawable;
        mClickedBackground = ninepatchdrawable1;
        mListener = clickablebuttonlistener;
        mContentDescription = charsequence1;
        int k = ninepatchdrawable.getMinimumWidth();
        int l = ninepatchdrawable.getMinimumHeight();
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        if(mBitmap != null && charsequence != null)
            i1 = sBitmapTextXSpacing;
        else
            i1 = 0;
        if(charsequence == null)
        {
            k1 = 0;
            j1 = 0;
        } else
        {
            j1 = (int)textpaint.measureText(charsequence.toString());
            mTextLayout = new StaticLayout(charsequence, textpaint, j1, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            k1 = mTextLayout.getHeight();
        }
        if(bitmap == null)
            l1 = 0;
        else
            l1 = bitmap.getWidth();
        if(bitmap == null)
            i2 = 0;
        else
            i2 = bitmap.getHeight();
        mRect = new Rect(i, j, i + Math.max(k, i1 + (j1 + l1)) + 2 * getTextXPadding(flag), j + Math.max(l, Math.max(k1, i2)));
    }

    public ClickableButton(Context context, CharSequence charsequence, TextPaint textpaint, NinePatchDrawable ninepatchdrawable, NinePatchDrawable ninepatchdrawable1, ClickableButtonListener clickablebuttonlistener, int i, 
            int j)
    {
        this(context, null, charsequence, textpaint, ninepatchdrawable, ninepatchdrawable1, clickablebuttonlistener, i, j, charsequence, false);
    }
    
    private static int getTextXPadding(boolean flag)
    {
        int i;
        if(flag)
            i = sExtraTextXPadding;
        else
            i = sTextXPadding;
        return i;
    }

    public static int getTotalPadding(Context context, boolean flag, boolean flag1)
    {
        initialize(context);
        return 2 * getTextXPadding(true) + sBitmapTextXSpacing;
    }

    private static void initialize(Context context)
    {
        if(!sClickableButtonInitialized)
        {
            sClickableButtonInitialized = true;
            Resources resources = context.getResources();
            sTextXPadding = (int)resources.getDimension(R.dimen.clickable_button_horizontal_spacing);
            sExtraTextXPadding = (int)resources.getDimension(R.dimen.clickable_button_extra_horizontal_spacing);
            sBitmapTextXSpacing = (int)resources.getDimension(R.dimen.clickable_button_bitmap_text_x_spacing);
        }
    }

    public final int compare(ClickableItem obj, ClickableItem obj1)
    {
        ClickableItem clickableitem = (ClickableItem)obj;
        ClickableItem clickableitem1 = (ClickableItem)obj1;
        return sComparator.compare(clickableitem, clickableitem1);
    }

    public final ClickableButton createAbsoluteCoordinatesCopy(int i, int j)
    {
        Object obj;
        TextPaint textpaint;
        if(mTextLayout == null)
        {
            obj = null;
            textpaint = null;
        } else
        {
            obj = mTextLayout.getText().toString();
            textpaint = mTextLayout.getPaint();
        }
        return new ClickableButton(mContext, mBitmap, ((CharSequence) (obj)), textpaint, mDefaultBackground, mClickedBackground, null, i + mRect.left, j + mRect.top, mContentDescription);
    }

    public final void draw(Canvas canvas) {
        NinePatchDrawable ninepatchdrawable;
        int i;
        int j;
        int k;
        if(mClicked)
            ninepatchdrawable = mClickedBackground;
        else
            ninepatchdrawable = mDefaultBackground;
        if(ninepatchdrawable != null)
        {
            ninepatchdrawable.setBounds(mRect);
            ninepatchdrawable.draw(canvas);
        }
        if(mBitmap == null)
            i = 0;
        else
            i = mBitmap.getWidth();
        if(mTextLayout == null)
            j = 0;
        else
            j = mTextLayout.getWidth();
        k = mRect.left + (mRect.width() - i - j) / 2;
        if(mBitmap != null)
        {
            int i1 = mRect.top + (mRect.height() - mBitmap.getHeight()) / 2;
            canvas.drawBitmap(mBitmap, k, i1, null);
            StaticLayout staticlayout = mTextLayout;
            int j1 = 0;
            if(staticlayout != null)
                j1 = sBitmapTextXSpacing;
            k += j1 + i;
        }
        if(mTextLayout != null)
        {
            int l = mRect.top + (mRect.height() - mTextLayout.getHeight()) / 2;
            canvas.translate(k, l);
            mTextLayout.draw(canvas);
            canvas.translate(-k, -l);
        }
    }

    public final CharSequence getContentDescription()
    {
        return mContentDescription;
    }

    public final Rect getRect()
    {
        return mRect;
    }
	
	public static interface ClickableButtonListener {
		void onClickableButtonListenerClick(ClickableButton clickablebutton);
    }
	
	public final boolean handleEvent(int i, int j, int k) {
        ClickableButtonListener clickablebuttonlistener = mListener;
        if(null == clickablebuttonlistener) {
        	return false;
        }
        
        boolean flag = false;
		if (k == 3) {
			mClicked = false;
			return true;
		}
		if (!mRect.contains(i, j)) {
			flag = false;
			if (k == 1) {
				mClicked = false;
				flag = false;
			}
			return false;
		}
        mClicked = true;
        if(mClicked)
            mListener.onClickableButtonListenerClick(this);
        mClicked = false;
        return true;
    }

	public final void setListener(
			ClickableButtonListener clickablebuttonlistener) {
		mListener = clickablebuttonlistener;
	}
}
