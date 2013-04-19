/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextPaint;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class PlusBarUtils {

	public static NinePatchDrawable sButtonDrawable;
    public static NinePatchDrawable sButtonPressedDrawable;
    private static boolean sInitialized;
    public static TextPaint sInteractivePostButtonTextPaint;
    public static TextPaint sNotPlusOnedTextPaint;
    public static int sPlusBarXPadding;
    public static NinePatchDrawable sPlusOnedDrawable;
    public static NinePatchDrawable sPlusOnedPressedDrawable;
    public static TextPaint sPlusOnedTextPaint;
    
	public static void init(Context context)
    {
        if(!sInitialized)
        {
            sInitialized = true;
            Resources resources = context.getResources();
            sButtonDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.btn_default_gray);
            sButtonPressedDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.btn_default_gray_pressed);
            sPlusOnedDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.btn_plusone_red);
            sPlusOnedPressedDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.btn_plusone_red_pressed);
            TextPaint textpaint = new TextPaint();
            sNotPlusOnedTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sNotPlusOnedTextPaint.setColor(resources.getColor(R.color.card_not_plus_oned_text));
            sNotPlusOnedTextPaint.setTextSize(resources.getDimension(R.dimen.card_plus_oned_text_size));
            sNotPlusOnedTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sNotPlusOnedTextPaint.linkColor = resources.getColor(R.color.card_link);
            sNotPlusOnedTextPaint.setShadowLayer(resources.getDimension(R.dimen.card_plus_oned_text_shadow_radius), resources.getDimension(R.dimen.card_plus_oned_text_shadow_x), resources.getDimension(R.dimen.card_plus_oned_text_shadow_y), resources.getColor(R.color.card_not_plus_oned_shadow_text));
            TextPaintUtils.registerTextPaint(sNotPlusOnedTextPaint, R.dimen.card_plus_oned_text_size);
            TextPaint textpaint1 = new TextPaint();
            sPlusOnedTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sPlusOnedTextPaint.setColor(resources.getColor(R.color.card_plus_oned_text));
            sPlusOnedTextPaint.setTextSize(resources.getDimension(R.dimen.card_plus_oned_text_size));
            sPlusOnedTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sPlusOnedTextPaint.linkColor = resources.getColor(R.color.card_link);
            TextPaintUtils.registerTextPaint(sPlusOnedTextPaint, R.dimen.card_plus_oned_text_size);
            TextPaint textpaint2 = new TextPaint();
            sInteractivePostButtonTextPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sInteractivePostButtonTextPaint.setColor(resources.getColor(R.color.card_interactive_post_button_text));
            sInteractivePostButtonTextPaint.setTextSize(resources.getDimension(R.dimen.card_interactive_post_button_text_size));
            sInteractivePostButtonTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sInteractivePostButtonTextPaint.linkColor = resources.getColor(R.color.card_link);
            TextPaintUtils.registerTextPaint(sInteractivePostButtonTextPaint, R.dimen.card_interactive_post_button_text_size);
            sPlusBarXPadding = (int)resources.getDimension(R.dimen.card_plus_bar_x_padding);
        }
    }
}
