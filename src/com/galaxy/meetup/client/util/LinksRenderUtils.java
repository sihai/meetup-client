/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.ClickableButton;

/**
 * 
 * @author sihai
 *
 */
public class LinksRenderUtils {

	protected static Paint sAppInviteTopAreaBackgroundPaint;
    protected static Bitmap sDeepLinkIcon;
    protected static TextPaint sDeepLinkTextPaint;
    protected static int sHorizontalSpacing;
    protected static int sIconHorizontalSpacing;
    protected static Paint sImageBorderPaint;
    protected static int sImageHorizontalSpacing;
    protected static float sImageMaxWidthPercentage;
    protected static TextPaint sLinkTitleTextPaint;
    protected static TextPaint sLinkUrlTextPaint;
    private static boolean sLinksCardViewInitialized;
    protected static Paint sLinksTopAreaBackgroundPaint;
    protected static int sMaxImageDimension;
    protected static final Paint sResizePaint = new Paint(2);
    protected static Paint sTransparentOverlayPaint;
    
    public static void createBackgroundDestRect(int i, int j, int k, int l, Rect rect)
    {
        rect.set(i, j, k, l);
    }

    public static void createBackgroundSourceRect(Bitmap bitmap, Rect rect, Rect rect1)
    {
        int i = bitmap.getWidth();
        int j = bitmap.getHeight();
        float f = (float)i / (float)j;
        float f1 = (float)rect.width() / (float)rect.height();
        if(f > f1)
        {
            int l = (i - (int)(f1 * (float)j)) / 2;
            rect1.set(l, 0, i - l, j);
        } else
        {
            int k = (j - (int)((float)i / f1)) / 2;
            rect1.set(0, k, i, j - k);
        }
    }

    public static ClickableButton createDeepLinkButton(Context context, String s, int i, int j, int k, ClickableButton.ClickableButtonListener clickablebuttonlistener)
    {
        ClickableButton clickablebutton;
        if(!TextUtils.isEmpty(s))
        {
            int l = Math.max(0, k - sDeepLinkIcon.getWidth() - ClickableButton.getTotalPadding(context, true, true) - sHorizontalSpacing - sImageHorizontalSpacing);
            CharSequence charsequence = TextUtils.ellipsize(s, PlusBarUtils.sNotPlusOnedTextPaint, l, android.text.TextUtils.TruncateAt.END);
            clickablebutton = new ClickableButton(context, sDeepLinkIcon, charsequence, PlusBarUtils.sInteractivePostButtonTextPaint, PlusBarUtils.sButtonDrawable, PlusBarUtils.sButtonPressedDrawable, clickablebuttonlistener, i, j, charsequence, true);
        } else
        {
            clickablebutton = null;
        }
        return clickablebutton;
    }

    public static void createImageRects(int i, int j, int k, int l, Rect rect, Rect rect1)
    {
        int i1 = k + sHorizontalSpacing;
        int j1 = l + (i - j) / 2;
        rect.set(i1, j1, i1 + j, j1 + j);
        int k1 = (int)sImageBorderPaint.getStrokeWidth();
        rect1.set(i1 + k1, j1 + k1, (i1 + j) - k1, (j1 + j) - k1);
    }

    public static void createImageSourceRect(Bitmap bitmap, Rect rect)
    {
        int i = bitmap.getWidth();
        int j = bitmap.getHeight();
        int k = Math.min(i, j);
        if(i > k)
            rect.set((i - k) / 2, 0, (i + k) / 2, j);
        else
            rect.set(0, (j - k) / 2, i, (j + k) / 2);
    }

    public static StaticLayout createTitle(String s, int i, int j) {
        if(TextUtils.isEmpty(s)) 
        	return null; 
        int k = Math.min(3, i / (int)(sLinkTitleTextPaint.descent() - sLinkTitleTextPaint.ascent()));
        if(k <= 0)
        	return null;
        return TextPaintUtils.createConstrainedStaticLayout(sLinkTitleTextPaint, s, j - 2 * sHorizontalSpacing - sImageHorizontalSpacing, k);
    }

    public static StaticLayout createUrl(String s, int i, int j, int k) {
        if(TextUtils.isEmpty(s)) 
        	return null;
        int l = Math.min(1, (i - k) / (int)(sLinkTitleTextPaint.descent() - sLinkTitleTextPaint.ascent()));
        if(l <= 0)
        	return null;
        return TextPaintUtils.createConstrainedStaticLayout(sLinkUrlTextPaint, s, j - 2 * sHorizontalSpacing - sImageHorizontalSpacing - sIconHorizontalSpacing, l);
    }

    public static void drawBitmap(Canvas canvas, Bitmap bitmap, Rect rect, Rect rect1, Rect rect2, Rect rect3, Rect rect4)
    {
        canvas.drawBitmap(bitmap, rect1, rect2, sResizePaint);
        canvas.drawRect(rect2, sTransparentOverlayPaint);
        canvas.drawBitmap(bitmap, rect, rect3, sResizePaint);
        canvas.drawRect(rect4, sImageBorderPaint);
    }

    public static void drawTitleDeepLinkAndUrl(Canvas canvas, int i, int j, StaticLayout staticlayout, ClickableButton clickablebutton, StaticLayout staticlayout1, Bitmap bitmap)
    {
        int k = i + (sHorizontalSpacing + sImageHorizontalSpacing);
        if(staticlayout != null)
        {
            canvas.translate(k, j);
            staticlayout.draw(canvas);
            canvas.translate(-k, -j);
            j += staticlayout.getHeight();
        }
        if(clickablebutton != null)
        {
            Rect rect = clickablebutton.getRect();
            rect.offset(k - rect.left, j - rect.top);
            clickablebutton.draw(canvas);
            j += rect.height();
        }
        if(staticlayout1 != null)
        {
            canvas.drawBitmap(bitmap, k, j + (staticlayout1.getHeight() - bitmap.getHeight()) / 2, null);
            int l = k + (bitmap.getWidth() + sIconHorizontalSpacing);
            canvas.translate(l, j);
            staticlayout1.draw(canvas);
            canvas.translate(-l, -j);
            staticlayout1.getHeight();
        }
    }

    public static Paint getAppInviteTopAreaBackgroundPaint()
    {
        return sAppInviteTopAreaBackgroundPaint;
    }

    public static float getImageMaxWidthPercentage()
    {
        return sImageMaxWidthPercentage;
    }

    public static String getLinkTitle(Resources resources, String s, String s1, String s2, boolean flag, boolean flag1) {
        if(!flag1 || TextUtils.isEmpty(s) || flag) 
        	return s1; 
        if(TextUtils.isEmpty(s1)) {
        	if(!TextUtils.isEmpty(s2))
                s = resources.getString(R.string.stream_app_invite_title, new Object[] {
                    s, s2
                });
        	return s;
        }
        s = resources.getString(R.string.stream_app_invite_title, new Object[] {
                s, s1
            });
        return s;
    }

    public static Paint getLinksTopAreaBackgroundPaint()
    {
        return sLinksTopAreaBackgroundPaint;
    }

    public static int getMaxImageDimension()
    {
        return sMaxImageDimension;
    }

    public static Paint getTransparentOverlayPaint()
    {
        return sTransparentOverlayPaint;
    }

    public static void init(Context context)
    {
        if(!sLinksCardViewInitialized)
        {
            sLinksCardViewInitialized = true;
            Resources resources = context.getResources();
            sDeepLinkIcon = ImageUtils.decodeResource(resources, R.drawable.ic_app_invite);
            Paint paint = new Paint();
            sTransparentOverlayPaint = paint;
            paint.setColor(resources.getColor(R.color.card_links_background_tint));
            Paint paint1 = new Paint();
            sLinksTopAreaBackgroundPaint = paint1;
            paint1.setColor(resources.getColor(R.color.solid_black));
            Paint paint2 = new Paint();
            sAppInviteTopAreaBackgroundPaint = paint2;
            paint2.setColor(resources.getColor(R.color.card_app_invite_background));
            Paint paint3 = new Paint();
            sImageBorderPaint = paint3;
            paint3.setColor(resources.getColor(R.color.card_links_image_border));
            sImageBorderPaint.setStyle(android.graphics.Paint.Style.STROKE);
            sImageBorderPaint.setStrokeWidth(resources.getDimension(R.dimen.card_links_image_stroke_dimension));
            TextPaint textpaint = new TextPaint();
            sDeepLinkTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDeepLinkTextPaint.setColor(resources.getColor(R.color.card_not_plus_oned_text));
            sDeepLinkTextPaint.setTextSize(resources.getDimension(R.dimen.card_plus_oned_text_size));
            sDeepLinkTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sDeepLinkTextPaint.linkColor = resources.getColor(R.color.card_link);
            sDeepLinkTextPaint.setShadowLayer(resources.getDimension(R.dimen.card_plus_oned_text_shadow_radius), resources.getDimension(R.dimen.card_plus_oned_text_shadow_x), resources.getDimension(R.dimen.card_plus_oned_text_shadow_y), resources.getColor(R.color.card_not_plus_oned_shadow_text));
            TextPaintUtils.registerTextPaint(sDeepLinkTextPaint, R.dimen.card_plus_oned_text_size);
            TextPaint textpaint1 = new TextPaint();
            sLinkTitleTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sLinkTitleTextPaint.setColor(resources.getColor(R.color.card_links_title_text));
            sLinkTitleTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sLinkTitleTextPaint.setTextSize(resources.getDimension(R.dimen.card_links_title_text_size));
            sLinkTitleTextPaint.setShadowLayer(resources.getDimension(R.dimen.card_links_title_text_shadow_radius), resources.getDimension(R.dimen.card_links_title_text_shadow_x), resources.getDimension(R.dimen.card_links_title_text_shadow_y), resources.getColor(R.color.card_links_title_text_shadow));
            TextPaintUtils.registerTextPaint(sLinkTitleTextPaint, R.dimen.card_links_title_text_size);
            TextPaint textpaint2 = new TextPaint();
            sLinkUrlTextPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sLinkUrlTextPaint.setColor(resources.getColor(R.color.card_links_url_text));
            sLinkUrlTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sLinkUrlTextPaint.setTextSize(resources.getDimension(R.dimen.card_links_url_text_size));
            sLinkUrlTextPaint.setShadowLayer(resources.getDimension(R.dimen.card_links_url_text_shadow_radius), resources.getDimension(R.dimen.card_links_url_text_shadow_x), resources.getDimension(R.dimen.card_links_url_text_shadow_y), resources.getColor(R.color.card_links_url_text_shadow));
            TextPaintUtils.registerTextPaint(sLinkUrlTextPaint, R.dimen.card_links_url_text_size);
            sImageMaxWidthPercentage = resources.getDimension(R.dimen.card_links_image_max_width_percent);
            sMaxImageDimension = (int)resources.getDimension(R.dimen.card_links_image_dimension);
            sHorizontalSpacing = (int)resources.getDimension(R.dimen.card_links_x_padding);
            sImageHorizontalSpacing = (int)resources.getDimension(R.dimen.card_links_image_x_padding);
            sIconHorizontalSpacing = (int)resources.getDimension(R.dimen.card_links_icon_x_padding);
        }
    }
}
