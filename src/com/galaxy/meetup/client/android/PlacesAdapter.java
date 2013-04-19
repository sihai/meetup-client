/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.content.DbLocation;

/**
 * 
 * @author sihai
 *
 */
public class PlacesAdapter extends EsCursorAdapter {

	public PlacesAdapter(Context context)
    {
        super(context, null);
    }

    public static DbLocation getLocation(Cursor cursor)
    {
        return DbLocation.deserialize(cursor.getBlob(2));
    }

    public final void bindView(View view, Context context, Cursor cursor)
    {
        ImageView imageview = (ImageView)view.findViewById(0x1020006);
        TextView textview = (TextView)view.findViewById(0x1020016);
        TextView textview1 = (TextView)view.findViewById(0x1020005);
        DbLocation dblocation = getLocation(cursor);
        if(dblocation != null)
        {
            String s;
            if(dblocation.isPrecise())
            {
                imageview.setImageResource(R.drawable.list_current);
                textview.setText(R.string.my_location);
                s = dblocation.getLocationName();
            } else
            if(dblocation.isCoarse())
            {
                imageview.setImageResource(R.drawable.ic_location_city);
                textview.setText(R.string.my_city);
                s = dblocation.getLocationName();
            } else
            {
                imageview.setImageResource(R.drawable.ic_location_grey);
                textview.setText(dblocation.getName());
                s = dblocation.getBestAddress();
            }
            textview1.setText(s);
        }
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        return LayoutInflater.from(context).inflate(R.layout.location_row_layout, viewgroup, false);
    }
    
    public static interface LocationQuery
    {

        public static final String PROJECTION[] = {
            "_id", "name", "location"
        };

    }
}
