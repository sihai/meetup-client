/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.DbEmbedMedia;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;

/**
 * 
 * @author sihai
 *
 */
public class TextCardView extends StreamCardView {

	protected static Bitmap sCheckinIcon;
    private static boolean sTextCardViewInitialized;
    private boolean mIsCheckin;
    private boolean mWrapContent;
    
    public TextCardView(Context context)
    {
        this(context, null);
    }

    public TextCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        if(!sTextCardViewInitialized)
        {
            sTextCardViewInitialized = true;
            sCheckinIcon = ImageUtils.decodeResource(context.getResources(), R.drawable.ic_checkin_small);
        }
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l) {
        drawAuthorImage(canvas);
        int i1 = i + (sAvatarSize + sContentXPadding);
        int j1 = k - (sAvatarSize + sContentXPadding);
        int k1 = drawAuthorName(canvas, i1, j);
        if(mRelativeTimeLayout != null)
            drawRelativeTimeLayout(canvas, (i1 + j1) - mRelativeTimeLayout.getWidth(), k1 - mRelativeTimeLayout.getHeight() - sRelativeTimeYOffset);
        int l1 = k1 + sContentYPadding;
        if(mAttributionLayout != null)
        {
            canvas.translate(i1, l1);
            mAttributionLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            l1 += mAttributionLayout.getHeight() + sContentYPadding;
        }
        if(mContentLayout != null)
        {
            canvas.translate(i1, l1);
            mContentLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            l1 += mContentLayout.getHeight() + sContentYPadding;
        }
        if(mFillerContentLayout != null)
        {
            canvas.translate(i1, l1);
            mFillerContentLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            l1 += mFillerContentLayout.getHeight() + sContentYPadding;
        }
        if(mTagLayout != null)
        {
            if(mTagIcon != null)
            {
                int i2;
                int j2;
                if(mIsCheckin)
                    i2 = sTagIconYPaddingCheckin;
                else
                    i2 = sTagIconYPaddingLocation;
                j2 = l1 + i2;
                canvas.drawBitmap(mTagIcon, i1, j2, null);
                i1 += mTagIcon.getWidth() + sTagIconXPadding;
            }
            canvas.translate(i1, l1);
            mTagLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            mTagLayout.getHeight();
            int _tmp = sContentYPadding;
        }
        drawPlusOneBar(canvas);
        drawCornerIcon(canvas);
        return l;
    }

    protected final String formatLocationName(String s)
    {
        return s;
    }

    public final void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamCardView.ViewedListener viewedlistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener)
    {
        super.init(cursor, i, j, onclicklistener, itemclicklistener, viewedlistener, streamplusbarclicklistener, streammediaclicklistener);
        boolean flag;
        byte abyte0[];
        if((16L & cursor.getLong(15)) != 0L)
            flag = true;
        else
            flag = false;
        mIsCheckin = flag;
        abyte0 = cursor.getBlob(22);
        if(abyte0 != null)
        {
            DbEmbedMedia dbembedmedia = DbEmbedMedia.deserialize(abyte0);
            if(dbembedmedia != null && !TextUtils.isEmpty(dbembedmedia.getContentUrl()) && TextUtils.isEmpty(mFillerContent) && !TextUtils.isEmpty(dbembedmedia.getTitle()))
                mFillerContent = dbembedmedia.getTitle();
        }
    }

    protected final int layoutElements(int i, int j, int k, int l)
    {
        createPlusOneBar(i, j + l, k);
        int i1 = l - mPlusOneButton.getRect().height();
        setAuthorImagePosition(i, j);
        int _tmp = sAvatarSize;
        int _tmp1 = sContentXPadding;
        int j1 = k - (sAvatarSize + sContentXPadding);
        int k1 = createAuthorNameAndRelativeTimeLayoutOnSameLine(j, j1) + sContentYPadding;
        if(!TextUtils.isEmpty(mAttribution))
        {
            int k2 = (i1 - k1) / (int)(sAttributionTextPaint.descent() - sAttributionTextPaint.ascent());
            if(k2 > 0)
            {
                mAttributionLayout = TextPaintUtils.createConstrainedStaticLayout(sAttributionTextPaint, mAttribution, j1, k2);
                k1 += mAttributionLayout.getHeight() + sContentYPadding;
            }
        }
        if(!TextUtils.isEmpty(mContent))
        {
            int j2 = (i1 - k1) / (int)(sDefaultTextPaint.descent() - sDefaultTextPaint.ascent());
            if(j2 > 0)
            {
                mContentLayout = TextPaintUtils.createConstrainedStaticLayout(sDefaultTextPaint, mContent, j1, j2);
                k1 += mContentLayout.getHeight() + sContentYPadding;
            }
        }
        if(!TextUtils.isEmpty(mFillerContent))
        {
            int i2 = (i1 - k1) / (int)(sDefaultTextPaint.descent() - sDefaultTextPaint.ascent());
            if(i2 > 0)
            {
                mFillerContentLayout = TextPaintUtils.createConstrainedStaticLayout(sDefaultTextPaint, mFillerContent, j1, i2);
                k1 += mFillerContentLayout.getHeight() + sContentYPadding;
            }
        }
        Rect rect;
        Rect rect1;
        Rect rect2;
        if(!TextUtils.isEmpty(mTag))
        {
            int l1 = (i1 - k1) / (int)(sDefaultTextPaint.descent() - sDefaultTextPaint.ascent());
            if(l1 > 0)
            {
                
                Bitmap bitmap;
                if(mIsCheckin)
                    bitmap = sCheckinIcon;
                else
                    bitmap = sTagLocationBitmaps[1];
                mTagIcon = bitmap;
                mTagLayout = TextPaintUtils.createConstrainedStaticLayout(sDefaultTextPaint, mTag, j1 - mTagIcon.getWidth(), l1);
                k1 += mTagLayout.getHeight() + sContentYPadding;
            }
        }
        if(mWrapContent)
        {
            rect = mPlusOneButton.getRect();
            rect.offsetTo(rect.left, k1);
            if(mReshareButton != null)
            {
                rect2 = mReshareButton.getRect();
                rect2.offsetTo(rect2.left, k1);
            }
            if(mCommentsButton != null)
            {
                rect1 = mCommentsButton.getRect();
                rect1.offsetTo(rect1.left, k1);
            }
        }
        return mPlusOneButton.getRect().bottom;
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        mWrapContent = shouldWrapContent(j);
        int i1;
        boolean flag;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        if(mWrapContent)
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
        if(mWrapContent)
            setMeasuredDimension(k, l1 + (j2 + sTopBorderPadding) + sBottomBorderPadding);
        else
            setMeasuredDimension(k, i1);
        createGraySpamBar(getMeasuredWidth() - sLeftBorderPadding - sRightBorderPadding);
        mBackgroundRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void onRecycle()
    {
        super.onRecycle();
        mWrapContent = false;
    }
}
