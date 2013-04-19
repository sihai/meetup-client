/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author sihai
 *
 */
public class NetworkTransactionsAdapter extends EsCursorAdapter {

	public static interface NetworkTransactionsQuery
    {

        public static final String PROJECTION[] = {
            "_id", "name", "time", "sent", "recv", "network_duration", "process_duration", "req_count", "exception"
        };

    }

    public NetworkTransactionsAdapter(Context context, Cursor cursor)
    {
        super(context, null);
    }

    public final void bindView(View view, Context context, Cursor cursor)
    {
    	Resources resources;
        int i;
        Object aobj[];
        String s;
        String s1;
        TextView textview = (TextView)view.findViewById(R.id.transaction_time);
        long l = cursor.getLong(2);
        textview.setText((new StringBuilder()).append(DateFormat.format("MM-dd hh:mm:ss", new Date(l))).append(".").append(l % 1000L).toString());
        ((TextView)view.findViewById(R.id.transaction_name)).setText(cursor.getString(1));
        ImageView imageview = (ImageView)view.findViewById(0x1020006);
        TextView textview1 = (TextView)view.findViewById(R.id.transaction_bytes);
        if(cursor.isNull(8))
        {
            imageview.setImageResource(R.drawable.indicator_green);
            int j = cursor.getInt(7);
            if(j <= 1)
            {
                Resources resources2 = context.getResources();
                int i1 = R.string.network_transaction_one_bytes;
                Object aobj2[] = new Object[2];
                aobj2[0] = Long.valueOf(cursor.getLong(3));
                aobj2[1] = Long.valueOf(cursor.getLong(4));
                s1 = resources2.getString(i1, aobj2);
            } else
            {
                Resources resources1 = context.getResources();
                int k = R.string.network_transaction_many_bytes;
                Object aobj1[] = new Object[3];
                aobj1[0] = Long.valueOf(cursor.getLong(3));
                aobj1[1] = Long.valueOf(cursor.getLong(4));
                aobj1[2] = Integer.valueOf(j);
                s1 = resources1.getString(k, aobj1);
            }
            textview1.setText(s1.toString());
        } else
        {
            imageview.setImageResource(R.drawable.indicator_red);
            textview1.setText(cursor.getString(8));
        }
        resources = context.getResources();
        i = R.string.network_transaction_duration;
        aobj = new Object[1];
        aobj[0] = Long.valueOf(cursor.getLong(5));
        s = resources.getString(i, aobj);
        ((TextView)view.findViewById(R.id.transaction_duration)).setText(s.toString());
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        return ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(R.layout.network_transaction_row_view, null);
    }
}
