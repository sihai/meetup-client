/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView.PressedHighlightable;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;

/**
 * 
 * @author sihai
 *
 */
public class AlbumColumnGridItemView extends ImageResourceView implements
		PressedHighlightable {

	private static TextPaint sCommentCountPaint;
    private static Bitmap sCommentImage;
    private static Rect sDisabledArea;
    private static Paint sDisabledPaint;
    private static int sInfoHeight;
    private static int sInfoInnerPadding;
    private static int sInfoLeftMargin;
    private static Paint sInfoPaint;
    private static int sInfoRightMargin;
    private static boolean sInitialized;
    private static Bitmap sNotifyImage;
    private static int sNotifyRightMargin;
    private static int sNotifyTopMargin;
    private static TextPaint sPlusOneCountPaint;
    private static Bitmap sPlusOneImage;
    private CharSequence mCommentCount;
    private boolean mNotify;
    private CharSequence mPlusOneCount;
    
    public AlbumColumnGridItemView(Context context)
    {
        this(context, null);
    }

    public AlbumColumnGridItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        setSizeCategory(2);
        setReleaseImageWhenPaused(true);
        setDefaultIconEnabled(true);
        Resources resources = context.getApplicationContext().getResources();
        if(!sInitialized)
        {
            TextPaint textpaint = new TextPaint();
            sCommentCountPaint = textpaint;
            textpaint.setAntiAlias(true);
            sCommentCountPaint.setColor(resources.getColor(R.color.album_comment_count_color));
            sCommentCountPaint.setTextSize(resources.getDimension(R.dimen.album_comment_count_text_size));
            TextPaintUtils.registerTextPaint(sCommentCountPaint, R.dimen.album_comment_count_text_size);
            TextPaint textpaint1 = new TextPaint();
            sPlusOneCountPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sPlusOneCountPaint.setColor(resources.getColor(R.color.album_plusone_count_color));
            sPlusOneCountPaint.setTextSize(resources.getDimension(R.dimen.album_plusone_count_text_size));
            TextPaintUtils.registerTextPaint(sPlusOneCountPaint, R.dimen.album_plusone_count_text_size);
            Paint paint = new Paint();
            sInfoPaint = paint;
            paint.setColor(resources.getColor(R.color.album_info_background_color));
            sInfoPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sDisabledPaint = paint1;
            paint1.setColor(resources.getColor(R.color.album_disabled_color));
            sDisabledPaint.setStyle(android.graphics.Paint.Style.FILL);
            sDisabledArea = new Rect();
            sInfoInnerPadding = resources.getDimensionPixelSize(R.dimen.album_info_inner_padding);
            sInfoRightMargin = resources.getDimensionPixelSize(R.dimen.album_info_right_margin);
            sInfoLeftMargin = resources.getDimensionPixelSize(R.dimen.album_info_left_margin);
            sInfoHeight = resources.getDimensionPixelSize(R.dimen.album_info_height);
            sNotifyRightMargin = resources.getDimensionPixelSize(R.dimen.album_notification_right_margin);
            sNotifyTopMargin = resources.getDimensionPixelSize(R.dimen.album_notification_top_margin);
            sPlusOneImage = ImageUtils.decodeResource(resources, R.drawable.photo_plusone);
            sCommentImage = ImageUtils.decodeResource(resources, R.drawable.photo_comment);
            sNotifyImage = ImageUtils.decodeResource(resources, R.drawable.tag);
            sInitialized = true;
        }
    }

    protected void onDraw(Canvas canvas) {
        int i = canvas.getSaveCount();
        canvas.save();
        super.onDraw(canvas);
        canvas.restoreToCount(i);
        if(!hasImage()) {
        	return;
        }
        
        if(!isEnabled())
        {
            sDisabledArea.set(0, 0, getWidth(), getHeight());
            canvas.drawRect(sDisabledArea, sDisabledPaint);
        } else
        {
            if(mNotify)
            {
                int i1 = getWidth() - sNotifyRightMargin - sNotifyImage.getWidth();
                canvas.drawBitmap(sNotifyImage, i1, sNotifyTopMargin, null);
            }
            if(mPlusOneCount != null || mCommentCount != null)
            {
                int j = getHeight() - sInfoHeight;
                canvas.drawRect(0.0F, j, getWidth(), getHeight(), sInfoPaint);
                int k = sInfoLeftMargin;
                if(mPlusOneCount != null)
                {
                    float f3 = j + (sInfoHeight - sPlusOneImage.getHeight()) / 2;
                    float f4 = sPlusOneCountPaint.descent() - sPlusOneCountPaint.ascent();
                    float f5 = ((float)j + ((float)sInfoHeight - f4) / 2.0F) - sPlusOneCountPaint.ascent();
                    canvas.drawBitmap(sPlusOneImage, k, f3, null);
                    k += sPlusOneImage.getWidth() + sInfoInnerPadding;
                    canvas.drawText(mPlusOneCount, 0, mPlusOneCount.length(), k, f5, sPlusOneCountPaint);
                    if(mCommentCount != null)
                        k = (int)((float)(getWidth() - sInfoRightMargin) - sCommentCountPaint.measureText(mCommentCount, 0, mCommentCount.length())) - sInfoInnerPadding - sCommentImage.getWidth();
                }
                if(mCommentCount != null)
                {
                    float f = j + (sInfoHeight - sCommentImage.getHeight()) / 2;
                    float f1 = sCommentCountPaint.descent() - sCommentCountPaint.ascent();
                    float f2 = ((float)j + ((float)sInfoHeight - f1) / 2.0F) - sCommentCountPaint.ascent();
                    canvas.drawBitmap(sCommentImage, k, f, null);
                    int l = k + (sCommentImage.getWidth() + sInfoInnerPadding);
                    canvas.drawText(mCommentCount, 0, mCommentCount.length(), l, f2, sCommentCountPaint);
                }
            }
        }
    }

    public void setCommentCount(Integer integer)
    {
        if(integer == null)
            mCommentCount = null;
        else
        if(integer.intValue() > 99)
            mCommentCount = getResources().getString(R.string.ninety_nine_plus);
        else
            mCommentCount = integer.toString();
    }

    public void setNotification(boolean flag)
    {
        mNotify = flag;
    }

    public void setPlusOneCount(Integer integer)
    {
        if(integer == null)
            mPlusOneCount = null;
        else
        if(integer.intValue() > 99)
            mPlusOneCount = getResources().getString(R.string.ninety_nine_plus);
        else
            mPlusOneCount = integer.toString();
    }

    public final boolean shouldHighlightOnPress()
    {
        return isEnabled();
    }

}
