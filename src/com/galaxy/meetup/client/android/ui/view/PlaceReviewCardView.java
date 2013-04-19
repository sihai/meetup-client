/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.server.client.domain.PlaceReview;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class PlaceReviewCardView extends StreamCardView {

	private static Paint sDividerPaint;
    private static int sDividerYPadding;
    private static Bitmap sLocationBitmap;
    private static int sLocationIconPadding;
    private static boolean sPlaceReviewCardInitialized;
    private static int sPostLocationYPadding;
    private float mDividerY;
    private Rect mLocationIconRect;
    private StaticLayout mLocationLayout;
    private Point mLocationLayoutCorner;
    private PlaceReview mReview;
    private StaticLayout mReviewBodyLayout;
    private boolean mWrapContent;
    
    public PlaceReviewCardView(Context context)
    {
        this(context, null);
    }

    public PlaceReviewCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mDividerY = -1F;
        if(!sPlaceReviewCardInitialized)
        {
            Resources resources = context.getResources();
            sLocationBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_location_card);
            sPlaceReviewCardInitialized = true;
            Paint paint = new Paint();
            sDividerPaint = paint;
            paint.setColor(resources.getColor(R.color.card_place_review_divider));
            sDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.card_place_review_divider_stroke_width));
            sPostLocationYPadding = resources.getDimensionPixelOffset(R.dimen.card_place_review_post_location_y_padding);
            sLocationIconPadding = resources.getDimensionPixelOffset(R.dimen.card_place_review_location_icon_padding);
            sDividerYPadding = resources.getDimensionPixelOffset(R.dimen.card_place_review_divider_y_padding);
        }
        mLocationIconRect = new Rect();
        mLocationLayoutCorner = new Point();
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        drawAuthorImage(canvas);
        int i1 = i + (sAvatarSize + sContentXPadding);
        int j1 = k - (sAvatarSize + sContentXPadding);
        int k1 = drawAuthorName(canvas, i1, j);
        if(mRelativeTimeLayout != null)
            drawRelativeTimeLayout(canvas, (i1 + j1) - mRelativeTimeLayout.getWidth(), k1 - mRelativeTimeLayout.getHeight() - sRelativeTimeYOffset);
        int l1 = k1 + sContentYPadding;
        if(mContentLayout != null)
        {
            canvas.translate(i1, l1);
            mContentLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            l1 += mContentLayout.getHeight() + sContentYPadding;
        }
        if(mAutoTextLayout != null)
        {
            canvas.translate(i1, l1);
            mAutoTextLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            l1 += mAutoTextLayout.getHeight() + sContentYPadding;
        }
        if(mDividerY != -1F)
            canvas.drawLine(i1, mDividerY, i1 + j1, mDividerY, sDividerPaint);
        if(mLocationLayout != null)
        {
            int i2 = Math.max(mLocationIconRect.bottom, mLocationLayout.getHeight() + mLocationLayoutCorner.y);
            if(i2 <= l || mWrapContent)
            {
                canvas.translate(mLocationLayoutCorner.x, mLocationLayoutCorner.y);
                mLocationLayout.draw(canvas);
                canvas.translate(-mLocationLayoutCorner.x, -mLocationLayoutCorner.y);
                canvas.drawBitmap(sLocationBitmap, null, mLocationIconRect, null);
                l1 = i2 + sPostLocationYPadding;
            }
        }
        if(mReviewBodyLayout != null)
        {
            canvas.translate(i1, l1);
            mReviewBodyLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            mReviewBodyLayout.getHeight();
            int _tmp = sContentYPadding;
        }
        drawPlusOneBar(canvas);
        drawCornerIcon(canvas);
        return l;
    }

    public final void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamCardView.ViewedListener viewedlistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener)
    {
        super.init(cursor, i, j, onclicklistener, itemclicklistener, viewedlistener, streamplusbarclicklistener, streammediaclicklistener);
        byte abyte0[] = cursor.getBlob(24);
        if(abyte0 != null)
            mReview = (PlaceReview)JsonUtil.fromByteArray(abyte0, PlaceReview.class);
    }

    protected final int layoutElements(int i, int j, int k, int l)
    {
        createPlusOneBar(i, j + l, k);
        int i1 = l - mPlusOneButton.getRect().height();
        setAuthorImagePosition(i, j);
        int j1 = i + (sAvatarSize + sContentXPadding);
        int k1 = k - (sAvatarSize + sContentXPadding);
        int l1 = createAuthorNameAndRelativeTimeLayoutOnSameLine(j, k1) + sContentYPadding;
        int j2;
        int k2;
        String s;
        Rect rect;
        if(!TextUtils.isEmpty(mContent))
        {
            int j3 = (i1 - l1) / (int)(sDefaultTextPaint.descent() - sDefaultTextPaint.ascent());
            if(j3 > 0)
            {
                mContentLayout = TextPaintUtils.createConstrainedStaticLayout(sDefaultTextPaint, mContent, k1, j3);
                l1 += mContentLayout.getHeight();
            }
        } else
        if(mAutoText != 0)
        {
            int i2 = (i1 - l1) / (int)(sAutoTextPaint.descent() - sAutoTextPaint.ascent());
            if(i2 > 0)
            {
                mAutoTextLayout = TextPaintUtils.createConstrainedStaticLayout(sAutoTextPaint, getResources().getString(mAutoText), k1, i2);
                l1 += mAutoTextLayout.getHeight();
            }
        }
        j2 = l1 + sDividerYPadding;
        mDividerY = j2;
        k2 = j2 + sDividerYPadding;
        if(!TextUtils.isEmpty(mReview.name))
            s = mReview.name;
        else
            s = null;
        if(s != null)
        {
            Bitmap bitmap = sLocationBitmap;
            Rect rect1 = mLocationIconRect;
            int i3 = sLocationIconPadding;
            Point point = mLocationLayoutCorner;
            TextPaint textpaint = sDefaultTextPaint;
            mLocationLayout = TextPaintUtils.layoutBitmapTextLabel(j1, k2, k1, 0, bitmap, rect1, i3, s, point, textpaint, true);
            k2 += mLocationLayout.getHeight() + sPostLocationYPadding;
        }
        if(!TextUtils.isEmpty(mReview.reviewBody))
        {
            int l2 = (i1 - k2) / (int)(sDefaultTextPaint.descent() - sDefaultTextPaint.ascent());
            if(l2 > 0)
            {
                mReviewBodyLayout = TextPaintUtils.createConstrainedStaticLayout(sDefaultTextPaint, mReview.reviewBody, k1, l2);
                k2 += mReviewBodyLayout.getHeight() + sContentYPadding;
            }
        }
        rect = mPlusOneButton.getRect();
        if(mWrapContent)
        {
            rect.offsetTo(rect.left, k2);
            if(mReshareButton != null)
            {
                rect = mReshareButton.getRect();
                rect.offsetTo(rect.left, k2);
            }
            if(mCommentsButton != null)
            {
                rect = mCommentsButton.getRect();
                rect.offsetTo(rect.left, k2);
            }
        }
        return rect.bottom;
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
        mBackgroundRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void onRecycle()
    {
        super.onRecycle();
        mWrapContent = false;
        mLocationLayout = null;
        mLocationIconRect.setEmpty();
        mLocationLayoutCorner.set(0, 0);
        mReviewBodyLayout = null;
    }

}
