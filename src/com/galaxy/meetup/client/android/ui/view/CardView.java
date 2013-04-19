/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.client.util.ViewUtils;

/**
 * 
 * @author sihai
 *
 */
public abstract class CardView extends View implements ResourceConsumer, Recyclable {

	protected static NinePatchDrawable sBackground;
    protected static int sBottomBorderPadding;
    private static boolean sCardViewInitialized;
    protected static TextPaint sDefaultTextPaint;
    protected static Rect sDrawRect;
    protected static int sLeftBorderPadding;
    protected static Drawable sPressedStateBackground;
    protected static final Paint sResizePaint = new Paint(2);
    protected static int sRightBorderPadding;
    protected static int sTopBorderPadding;
    protected static int sXDoublePadding;
    protected static int sXPadding;
    protected static int sYDoublePadding;
    protected static int sYPadding;
    protected Rect mBackgroundRect;
    private final List mClickableItems;
    private ClickableItem mCurrentClickableItem;
    protected int mDisplaySizeType;
    protected ItemClickListener mItemClickListener;
    private android.view.View.OnClickListener mOnClickListener;
    protected boolean mPaddingEnabled;
    
    public CardView(Context context)
    {
        this(context, null);
    }

    public CardView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public CardView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mDisplaySizeType = -2;
        mClickableItems = new ArrayList();
        mPaddingEnabled = true;
        if(!sCardViewInitialized)
        {
            sCardViewInitialized = true;
            Resources resources = context.getResources();
            TextPaint textpaint = new TextPaint();
            sDefaultTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDefaultTextPaint.setColor(resources.getColor(R.color.card_default_text));
            sDefaultTextPaint.setTextSize(resources.getDimension(R.dimen.card_default_text_size));
            sDefaultTextPaint.linkColor = resources.getColor(R.color.card_link);
            TextPaintUtils.registerTextPaint(sDefaultTextPaint, R.dimen.card_default_text_size);
            sBackground = (NinePatchDrawable)resources.getDrawable(R.drawable.bg_tacos);
            sPressedStateBackground = resources.getDrawable(R.drawable.list_selected_holo);
            sLeftBorderPadding = (int)resources.getDimension(R.dimen.card_border_left_padding);
            sRightBorderPadding = (int)resources.getDimension(R.dimen.card_border_right_padding);
            sTopBorderPadding = (int)resources.getDimension(R.dimen.card_border_top_padding);
            sBottomBorderPadding = (int)resources.getDimension(R.dimen.card_border_bottom_padding);
            int j = (int)resources.getDimension(R.dimen.card_x_padding);
            sXPadding = j;
            sXDoublePadding = j * 2;
            int k = (int)resources.getDimension(R.dimen.card_y_padding);
            sYPadding = k;
            sYDoublePadding = k * 2;
            sDrawRect = new Rect();
        }
        mBackgroundRect = new Rect();
    }
    
    protected static boolean shouldWrapContent(int i)
    {
        boolean flag;
        if(android.view.View.MeasureSpec.getMode(i) == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void addClickableItem(ClickableItem clickableitem)
    {
        if(clickableitem != null)
        {
            mClickableItems.remove(clickableitem);
            mClickableItems.add(clickableitem);
        }
    }

    public final void bindResources()
    {
        if(ViewUtils.isViewAttached(this))
            onBindResources();
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent)
    {
        boolean flag;
        int i;
        int j;
        flag = true;
        i = (int)motionevent.getX();
        j = (int)motionevent.getY();
        
        switch(motionevent.getAction()) {
	        case 0:
	        	int l = -1 + mClickableItems.size();
	            do
	            {
	                if(l < 0)
	                    break;
	                ClickableItem clickableitem = (ClickableItem)mClickableItems.get(l);
	                if(clickableitem.handleEvent(i, j, 0))
	                {
	                    mCurrentClickableItem = clickableitem;
	                    invalidate();
	                    break;
	                }
	                l--;
	            } while(true);
	        	break;
	        case 1:
	        	 mCurrentClickableItem = null;
	             boolean flag1 = false;
	             for(int k = -1 + mClickableItems.size(); k >= 0; k--)
	                 flag1 |= ((ClickableItem)mClickableItems.get(k)).handleEvent(i, j, 1);

	             invalidate();
	             if(!flag1 && mOnClickListener != null)
	                 mOnClickListener.onClick(this);
	             flag = false;
	        	break;
	        case 2:
	        	flag = false;
	        	break;
	        case 3:
	        	if(mCurrentClickableItem != null)
	            {
	                mCurrentClickableItem.handleEvent(i, j, 3);
	                mCurrentClickableItem = null;
	                invalidate();
	            } else
	            {
	                flag = false;
	            }
	        	break;
        	default:
        		flag = false;
        		break;
        		
        }
        return flag;
    }
    
    protected abstract int draw(Canvas canvas, int i, int j, int k, int l);
	
    public void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamCardView.ViewedListener viewedlistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener)
    {
        mDisplaySizeType = i;
        mOnClickListener = onclicklistener;
        mItemClickListener = itemclicklistener;
    }

    protected abstract int layoutElements(int i, int j, int k, int l);

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        bindResources();
    }

    protected void onBindResources()
    {
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        onUnbindResources();
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int i = getWidth();
        int j = getHeight();
        int k;
        int l;
        int i1;
        int j1;
        if(mPaddingEnabled)
        {
            l = sXPadding;
            j1 = sYPadding;
            k = sXDoublePadding;
            i1 = sYDoublePadding;
        } else
        {
            k = 0;
            l = 0;
            i1 = 0;
            j1 = 0;
        }
        sBackground.setBounds(mBackgroundRect);
        sBackground.draw(canvas);
        draw(canvas, l + sLeftBorderPadding, j1 + sTopBorderPadding, i - (k + sLeftBorderPadding + sRightBorderPadding), j - (i1 + sTopBorderPadding + sBottomBorderPadding));
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        if(l <= 0)
            i1 = k;
        else
            i1 = l;
        if(mPaddingEnabled)
        {
            k1 = sXPadding;
            i2 = sYPadding;
            j1 = sXDoublePadding;
            l1 = sYDoublePadding;
        } else
        {
            j1 = 0;
            k1 = 0;
            l1 = 0;
            i2 = 0;
        }
        setMeasuredDimension(k, i1);
        mBackgroundRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        layoutElements(k1 + sLeftBorderPadding, i2 + sTopBorderPadding, k - (j1 + sLeftBorderPadding + sRightBorderPadding), i1 - (l1 + sTopBorderPadding + sBottomBorderPadding));
    }

    public void onRecycle()
    {
        onUnbindResources();
        mClickableItems.clear();
        mCurrentClickableItem = null;
        mOnClickListener = null;
        mItemClickListener = null;
        mBackgroundRect.setEmpty();
        clearAnimation();
    }

    public final void onResourceStatusChange(Resource resource)
    {
        invalidate();
    }

    protected void onUnbindResources()
    {
    }

    public final void removeClickableItem(ClickableItem clickableitem)
    {
        mClickableItems.remove(clickableitem);
    }

    public void setPaddingEnabled(boolean flag)
    {
        mPaddingEnabled = flag;
    }

    public final void unbindResources()
    {
        onUnbindResources();
    }
    
	public static void onStart()
    {
    }

    public static void onStop()
    {
    }

}
