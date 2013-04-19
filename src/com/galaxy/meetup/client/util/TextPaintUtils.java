
/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;

/**
 * 
 * @author sihai
 *
 */
public class TextPaintUtils {

	private static ContentObserver sFontSizeObserver;
    private static final List sTextPaintsAndSizeResIds = new ArrayList();
    
    public static StaticLayout createConstrainedStaticLayout(TextPaint textpaint, CharSequence charsequence, int i, int j)
    {
        return createConstrainedStaticLayout(textpaint, charsequence, i, j, android.text.Layout.Alignment.ALIGN_NORMAL);
    }

    public static StaticLayout createConstrainedStaticLayout(TextPaint textpaint, CharSequence charsequence, int i, int j, android.text.Layout.Alignment alignment)
    {
        int k = Math.max(i, 0);
        CharSequence txt = null;
        if(0 == j) {
        	txt = "";
        } else if(1 == j) {
        	txt = smartEllipsize(charsequence, textpaint, k, android.text.TextUtils.TruncateAt.END);
        } else {
        	StaticLayout staticlayout = new StaticLayout(charsequence, textpaint, k, alignment, 1.0F, 0.0F, false);
        	if(staticlayout.getLineCount() <= j) {
        		return staticlayout;
        	}
        	int l = staticlayout.getLineEnd(j - 2);
            SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder(charsequence.subSequence(0, l));
            spannablestringbuilder.append(smartEllipsize(charsequence.subSequence(l, charsequence.length()), textpaint, k, android.text.TextUtils.TruncateAt.END));
            txt = spannablestringbuilder;
        }
        return new StaticLayout(((CharSequence) (txt)), textpaint, k, alignment, 1.0F, 0.0F, false);
    }

    public static void init(Context context)
    {
        if(sFontSizeObserver == null)
        {
        	final Resources resources = context.getResources();
            sFontSizeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {

                public final void onChange(boolean flag)
                {
                    int i = 0;
                    for(int j = TextPaintUtils.sTextPaintsAndSizeResIds.size(); i < j; i++)
                    {
                        Pair pair = (Pair)TextPaintUtils.sTextPaintsAndSizeResIds.get(i);
                        ((TextPaint)pair.first).setTextSize(resources.getDimension(((Integer)pair.second).intValue()));
                    }

                }
            };
            context.getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("font_scale"), false, sFontSizeObserver);
        }
    }

    public static StaticLayout layoutBitmapTextLabel(int i, int j, int k, int l, Bitmap bitmap, Rect rect, int i1, CharSequence charsequence, 
            Point point, TextPaint textpaint, boolean flag)
    {
        point.set(i, j);
        if(bitmap != null)
        {
            int j2 = i1 + bitmap.getWidth();
            k -= j2;
            rect.set(i, j, i + bitmap.getWidth(), j + bitmap.getHeight());
            point.set(j2 + point.x, point.y);
        }
        CharSequence charsequence1;
        Object obj;
        StaticLayout staticlayout;
        int j1;
        int k1;
        int l1;
        int i2;
        if(flag)
            charsequence1 = TextUtils.ellipsize(charsequence, textpaint, k, android.text.TextUtils.TruncateAt.END);
        else
            charsequence1 = charsequence;
        if(k <= 0)
            obj = "";
        else
            obj = charsequence1;
        staticlayout = new StaticLayout(((CharSequence) (obj)), textpaint, Math.max(k, 0), android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        j1 = Math.max(0, staticlayout.getHeight());
        if(bitmap != null)
            k1 = bitmap.getHeight();
        else
            k1 = 0;
        l1 = Math.max(j1, k1);
        if(bitmap != null)
            rect.offset(0, Math.abs(l1 - bitmap.getHeight()) / 2);
        i2 = Math.abs(l1 - staticlayout.getHeight()) / 2;
        point.set(point.x, i2 + point.y);
        return staticlayout;
    }

    public static void registerTextPaint(TextPaint textpaint, int i)
    {
        sTextPaintsAndSizeResIds.add(new Pair(textpaint, Integer.valueOf(i)));
    }

    public static CharSequence smartEllipsize(CharSequence charsequence, TextPaint textpaint, int i, android.text.TextUtils.TruncateAt truncateat)
    {
        String s = charsequence.toString();
        int j = s.indexOf('\r');
        int k = s.indexOf('\n');
        CharSequence charsequence1;
        if(j == -1 && k == -1)
            charsequence1 = charsequence;
        else
        if(j == -1)
            charsequence1 = charsequence.subSequence(0, k);
        else
        if(k == -1)
            charsequence1 = charsequence.subSequence(0, j);
        else
            charsequence1 = charsequence.subSequence(0, Math.min(j, k));
        return TextUtils.ellipsize(charsequence1, textpaint, i, truncateat);
    }
}
