/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.Random;

import android.text.Html;
import android.text.TextUtils;

/**
 * 
 * @author sihai
 *
 */
public class StringUtils {

	private static Random randGen = new Random();
    private static char sNumbersAndLetters[] = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    
	public static boolean equals(CharSequence charsequence, CharSequence charsequence1)
    {
        boolean flag;
        if(TextUtils.equals(charsequence, charsequence1) || TextUtils.isEmpty(charsequence) && TextUtils.isEmpty(charsequence1))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static char firstLetter(String s) {
    	
        char c = ' ';
        if(null == s || 0 == s.length()) 
        	return c;
        char c1 = s.charAt(0);
        if(Character.isLetter(c1))
            c = Character.toUpperCase(c1);
        return c;
    }

    public static String getDomain(String s) {
        String as[] = s.split("@");
        String s1;
        if(as.length != 2)
            s1 = null;
        else
            s1 = as[1];
        return s1;
    }

    public static String randomString(int i) {
        char ac[] = new char[32];
        for(int j = 0; j < ac.length; j++)
            ac[j] = sNumbersAndLetters[randGen.nextInt(71)];

        return new String(ac);
    }

    public static String unescape(String s) {
        if(s != null)
            s = Html.fromHtml(s).toString();
        return s;
    }
}
