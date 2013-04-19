/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView.Tokenizer;

/**
 * 
 * @author sihai
 *
 */
public class MentionTokenizer implements Tokenizer {

	public MentionTokenizer() {
    }

    private static int findTokenEnd(CharSequence charsequence, int i, int j) {
        // TODO
    	return 0;
    }

    public static boolean isMentionTrigger(char c)
    {
        boolean flag;
        if(c == '+' || c == '@')
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final int findTokenEnd(CharSequence charsequence, int i)
    {
        return findTokenEnd(charsequence, i, i);
    }

    public final int findTokenStart(CharSequence charsequence, int i) {
    	// TODO
    	return 0;
    }

    public final CharSequence terminateToken(CharSequence charsequence)
    {
        int i = charsequence.length();
        Object obj;
        if(i == 0 || Character.isWhitespace(charsequence.charAt(i - 1)))
            obj = charsequence;
        else
        if(charsequence instanceof Spanned)
        {
            obj = new SpannableString((new StringBuilder()).append(charsequence).append(" ").toString());
            TextUtils.copySpansFrom((Spanned)charsequence, 0, charsequence.length(), Object.class, ((Spannable) (obj)), 0);
        } else
        {
            obj = (new StringBuilder()).append(charsequence).append(" ").toString();
        }
        return ((CharSequence) (obj));
    }
}
