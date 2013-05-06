/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.util.JsonUtil;
import com.galaxy.meetup.server.client.v2.domain.Event;


/**
 * 
 * @author sihai
 *
 */
public class EventStreamCardView extends StreamCardView {

	private EventCardDrawer mDrawer;
    private boolean mIgnoreHeight;
    
    public EventStreamCardView(Context context)
    {
        this(context, null);
    }

    public EventStreamCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mDrawer = new EventCardDrawer(this);
        setPaddingEnabled(false);
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        int i1 = mDrawer.draw(j, (j + l) - mPlusOneButton.getRect().height(), canvas);
        drawPlusOneBar(canvas);
        drawCornerIcon(canvas);
        return i1;
    }

    public final void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamCardView.ViewedListener viewedlistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener)
    {
        super.init(cursor, i, j, onclicklistener, itemclicklistener, viewedlistener, streamplusbarclicklistener, streammediaclicklistener);
        byte abyte0[] = cursor.getBlob(13);
        Event event;
        if(abyte0 != null)
            event = (Event)JsonUtil.fromByteArray(abyte0, Event.class);
        else
            event = null;
        mDrawer.bind(EsService.getActiveAccount(getContext()), this, event, mAuthorGaiaId, mAttribution, mItemClickListener);
    }

    protected final int layoutElements(int i, int j, int k, int l)
    {
        createPlusOneBar(i + sXPadding, l - sBottomBorderPadding, k - sXDoublePadding);
        Rect rect = mPlusOneButton.getRect();
        int i1 = mDrawer.layout(i, j, mIgnoreHeight, k, l - rect.height()) + sBottomBorderPadding;
        if(mIgnoreHeight)
        {
            int j1 = j + i1;
            rect.offsetTo(rect.left, j1);
            if(mReshareButton != null)
            {
                Rect rect2 = mReshareButton.getRect();
                rect2.offsetTo(rect2.left, j1);
            }
            if(mCommentsButton != null)
            {
                Rect rect1 = mCommentsButton.getRect();
                rect1.offsetTo(rect1.left, j1);
            }
            i1 = rect.bottom + sBottomBorderPadding;
        }
        return i1;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        mDrawer.attach();
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mDrawer.detach();
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        mIgnoreHeight = shouldWrapContent(j);
        int i1;
        boolean flag;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        if(mIgnoreHeight)
            i1 = k;
        else
            i1 = l;
        flag = mPaddingEnabled;
        j1 = 0;
        k1 = 0;
        l1 = 0;
        i2 = 0;
        if(flag)
        {
            k1 = sXPadding;
            i2 = sYPadding;
            j1 = sXDoublePadding;
            l1 = sYDoublePadding;
        }
        j2 = layoutElements(k1 + sLeftBorderPadding, i2 + sTopBorderPadding, k - (j1 + sLeftBorderPadding + sRightBorderPadding), i1 - (l1 + sTopBorderPadding + sBottomBorderPadding));
        if(mIgnoreHeight)
            setMeasuredDimension(k, l1 + (j2 + sTopBorderPadding) + sBottomBorderPadding);
        else
            setMeasuredDimension(k, i1);
        createGraySpamBar(getMeasuredWidth() - sLeftBorderPadding - sRightBorderPadding);
        mBackgroundRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void onRecycle()
    {
        super.onRecycle();
        mDrawer.clear();
        mIgnoreHeight = false;
    }
}
