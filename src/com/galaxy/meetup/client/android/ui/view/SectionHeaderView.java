/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.SuperscriptSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class SectionHeaderView extends RelativeLayout {

	public SectionHeaderView(Context context)
    {
        super(context);
    }

    public SectionHeaderView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public SectionHeaderView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    private static void adjustSuperscriptSpans(SpannableStringBuilder spannablestringbuilder)
    {
        SuperscriptSpan asuperscriptspan[] = (SuperscriptSpan[])spannablestringbuilder.getSpans(0, spannablestringbuilder.length(), SuperscriptSpan.class);
        if(asuperscriptspan != null)
        {
            for(int i = 0; i < asuperscriptspan.length; i++)
            {
                SuperscriptSpan superscriptspan = asuperscriptspan[i];
                int j = spannablestringbuilder.getSpanStart(superscriptspan);
                int k = spannablestringbuilder.getSpanEnd(superscriptspan);
                int l = spannablestringbuilder.getSpanFlags(superscriptspan);
                spannablestringbuilder.removeSpan(superscriptspan);
                spannablestringbuilder.setSpan(SUPERSCRIPT_SPAN, j, k, l);
            }

        }
    }

    public final void enableEditIcon(boolean flag)
    {
        View view = findViewById(R.id.edit);
        int i;
        if(flag)
            i = 0;
        else
            i = 8;
        view.setVisibility(i);
    }

    public void setText(int i)
    {
        setText(getContext().getText(i));
    }

    public void setText(CharSequence charsequence)
    {
        TextView textview = (TextView)findViewById(0x1020014);
        String s = charsequence.toString().toUpperCase();
        if(charsequence instanceof SpannedString)
        {
            SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder(s);
            TextUtils.copySpansFrom((SpannedString)charsequence, 0, charsequence.length(), Object.class, spannablestringbuilder, 0);
            if(android.os.Build.VERSION.SDK_INT < 14)
                adjustSuperscriptSpans(spannablestringbuilder);
            textview.setText(spannablestringbuilder);
        } else
        {
            textview.setText(s);
        }
    }

    private static final SuperscriptSpan SUPERSCRIPT_SPAN = new SuperscriptSpan() {

        public final void updateDrawState(TextPaint textpaint)
        {
            textpaint.baselineShift = textpaint.baselineShift + (int)(textpaint.ascent() / 4F);
        }

        public final void updateMeasureState(TextPaint textpaint)
        {
            textpaint.baselineShift = textpaint.baselineShift + (int)(textpaint.ascent() / 4F);
        }

    };
}
