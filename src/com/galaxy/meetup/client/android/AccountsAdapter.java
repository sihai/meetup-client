/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author sihai
 *
 */
public class AccountsAdapter extends ArrayAdapter {

	public AccountsAdapter(Context context)
    {
		// TODO 0x1090003 ?
        super(context, 0x1090003, new ArrayList());
    }

    public final View getView(int i, View view, ViewGroup viewgroup)
    {
        View view1 = super.getView(i, view, viewgroup);
        String s = (String)getItem(i);
        // TODO 0x1020014 ?
        ((TextView)view1.findViewById(0x1020014)).setText(s);
        return view1;
    }
}
