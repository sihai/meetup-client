/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.URLSpan;

import com.galaxy.meetup.client.util.TextPaintUtils;

/**
 * 
 * @author sihai
 *
 */
public class ClickableStaticLayout extends PositionedStaticLayout implements ClickableItem {

	private final SpanClickListener mClickListener;
    private StateURLSpan mClickedSpan;
    private CharSequence mContentDescription;
    private final Spanned mSpannedText;
    
    public ClickableStaticLayout(CharSequence charsequence, TextPaint textpaint, int i, Alignment alignment, float f, float f1, boolean flag,  SpanClickListener spanclicklistener)
    {
        super(charsequence, textpaint, i, alignment, f, 0.0F, false);
        mClickListener = spanclicklistener;
        mContentDescription = charsequence;
        if(charsequence instanceof Spanned)
            mSpannedText = (Spanned)charsequence;
        else
            mSpannedText = null;
    }
    
	@Override
	public int compare(ClickableItem obj, ClickableItem obj1) {
		ClickableItem clickableitem = (ClickableItem)obj;
        ClickableItem clickableitem1 = (ClickableItem)obj1;
        return sComparator.compare(clickableitem, clickableitem1);
	}

	@Override
	public CharSequence getContentDescription() {
		return mContentDescription;
	}

	@Override
	public Rect getRect() {
		return mContentArea;
	}

	@Override
	public boolean handleEvent(int i, int j, int k) {

		if (3 == k) {
			if (mClickedSpan != null) {
				mClickedSpan.setClicked(false);
				mClickedSpan = null;
			}
			return true;
		}

		boolean flag = true;
		if (mSpannedText == null)
			flag = false;
		else if (!mContentArea.contains(i, j)) {
			if (k == 1 && mClickedSpan != null) {
				mClickedSpan.setClicked(false);
				mClickedSpan = null;
			}
			flag = false;
		} else {
			float f = i - mContentArea.left;
			float f1 = Math.max(0.0F, j - mContentArea.top);
			int l = getLineForVertical((int) Math.min(-1 + getHeight(), f1));
			float f2 = Math.max(0.0F, f);
			int i1 = getOffsetForHorizontal(l, Math.min(-1 + getWidth(), f2));
			if (i1 < 0) {
				flag = false;
			} else {
				StateURLSpan astateurlspan[] = (StateURLSpan[]) mSpannedText
						.getSpans(i1, i1,
								ClickableStaticLayout.StateURLSpan.class);
				if (astateurlspan.length == 0)
					flag = false;
				else
					switch (k) {
					default:
						break;

					case 0: // '\0'
						mClickedSpan = astateurlspan[0];
						mClickedSpan.setClicked(flag);
						break;

					case 1: // '\001'
						if (mClickedSpan == astateurlspan[0]
								&& mClickListener != null)
							mClickListener.onSpanClick(astateurlspan[0]);
						if (mClickedSpan != null) {
							mClickedSpan.setClicked(false);
							mClickedSpan = null;
						}
						break;
					}
			}
		}
		return flag;
	}
	
	public static ClickableStaticLayout createConstrainedLayout(TextPaint textpaint, CharSequence charsequence, int i, int j, SpanClickListener spanclicklistener)
    {
		int k = Math.max(i, 0);
		CharSequence txt = null;
		if(0 == j) {
			txt = "";
			
		} else if(1 == j) {
			txt = TextPaintUtils.smartEllipsize(charsequence, textpaint, k, android.text.TextUtils.TruncateAt.END);
		} else {
			ClickableStaticLayout clickablestaticlayout = new ClickableStaticLayout(charsequence, textpaint, k, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false, spanclicklistener);
	        if(clickablestaticlayout.getLineCount() <= j) {
	        	return clickablestaticlayout;
	        }
	        int l = clickablestaticlayout.getLineEnd(j - 2);
	        SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder(charsequence.subSequence(0, l));
	        spannablestringbuilder.append(TextPaintUtils.smartEllipsize(charsequence.subSequence(l, charsequence.length()), textpaint, k, android.text.TextUtils.TruncateAt.END));
	        txt = spannablestringbuilder;
		}
		
		return new ClickableStaticLayout(txt, textpaint, k, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false, spanclicklistener);
    }
	
	public static SpannableStringBuilder buildStateSpans(String s)
    {
        return buildStateSpans(null, s, null, -1, false);
    }

    public static SpannableStringBuilder buildStateSpans(String s, android.text.Html.TagHandler taghandler)
    {
        return buildStateSpans(null, s, taghandler, -1, false);
    }

	private static SpannableStringBuilder buildStateSpans(Context context, String s, android.text.Html.TagHandler taghandler, int i, boolean flag)
    {
        SpannableStringBuilder spannablestringbuilder;
        if(s == null)
        {
            spannablestringbuilder = new SpannableStringBuilder();
        } else
        {
            Spanned spanned = Html.fromHtml(s, null, taghandler);
            spannablestringbuilder = new SpannableStringBuilder(spanned);
            URLSpan aurlspan[] = (URLSpan[])spannablestringbuilder.getSpans(0, spanned.length(), URLSpan.class);
            int j = 0;
            while(j < aurlspan.length) 
            {
                URLSpan urlspan = aurlspan[j];
                spannablestringbuilder.setSpan(new StateURLSpan(urlspan.getURL()), spannablestringbuilder.getSpanStart(urlspan), spannablestringbuilder.getSpanEnd(urlspan), spannablestringbuilder.getSpanFlags(urlspan));
                spannablestringbuilder.removeSpan(urlspan);
                j++;
            }
        }
        return spannablestringbuilder;
    }
	
	public static abstract interface SpanClickListener {
		void onSpanClick(URLSpan paramURLSpan);
	}
	
	public static final class StateURLSpan extends URLSpan {

		private int mBgColor;
		private boolean mClicked;
		private boolean mFirstTime;

		public StateURLSpan(String s) {
			super(s);
			mFirstTime = true;
		}

		public final void setClicked(boolean flag) {
			mClicked = flag;
		}

		public final void updateDrawState(TextPaint textpaint) {
			if (mFirstTime) {
				mFirstTime = false;
				mBgColor = textpaint.bgColor;
			}
			if (mClicked) {
				if (android.os.Build.VERSION.SDK_INT >= 13)
					textpaint.bgColor = 0xff33b5e5;
				else
					textpaint.bgColor = -32768;
			} else {
				textpaint.bgColor = mBgColor;
			}
			textpaint.setColor(textpaint.linkColor);
			textpaint.setUnderlineText(false);
		}

	}
}
