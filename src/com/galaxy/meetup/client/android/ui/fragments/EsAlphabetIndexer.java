/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.database.Cursor;
import android.widget.AlphabetIndexer;

import com.galaxy.meetup.client.util.StringUtils;

/**
 * 
 * @author sihai
 *
 */
public class EsAlphabetIndexer extends AlphabetIndexer {

	public EsAlphabetIndexer(Cursor cursor, int i)
    {
        super(cursor, i, computeAlphabet(cursor, i));
    }

    private static CharSequence computeAlphabet(Cursor cursor, int i)
    {
        StringBuilder stringbuilder = new StringBuilder();
        boolean flag = cursor.moveToFirst();
        int j = 0;
        if(flag)
            do
            {
                char c = StringUtils.firstLetter(cursor.getString(i));
                if(c != j)
                {
                    stringbuilder.append(c);
                    j = c;
                }
            } while(cursor.moveToNext());
        return stringbuilder.toString();
    }
}
