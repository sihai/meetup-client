/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.server.client.domain.Place;

/**
 * 
 * @author sihai
 *
 */
public class MapUtils {

	public static Intent getPlacesActivityIntent() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.PlacesActivity");
        intent.addFlags(0x80000);
        return intent;
    }
	
	public static void launchMapsActivity(Context context, Uri uri) {
		Intent intent = new Intent("android.intent.action.VIEW", uri);
		intent.addFlags(0x80000);
		intent.setPackage("com.google.android.apps.maps");
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			context.startActivity(Intent.createChooser(intent, null));
		}
    }
	
    public static void showActivityOnMap(Context context, DbLocation dblocation) {
        android.net.Uri.Builder builder;
        String s1;
        builder = Uri.parse("http://maps.google.com/maps").buildUpon();
        builder.appendQueryParameter("lci", "com.google.latitudepublicupdates");
        if(dblocation.hasCoordinates())
        {
            double d2 = (double)dblocation.getLatitudeE7() / 10000000D;
            double d3 = (double)dblocation.getLongitudeE7() / 10000000D;
            builder.appendQueryParameter("ll", (new StringBuilder()).append(d2).append(",").append(d3).toString());
        }
        String s = dblocation.getClusterId();
        boolean flag;
        double d;
        double d1;
        StringBuilder stringbuilder;
        if(!TextUtils.isEmpty(s))
            flag = true;
        else
            flag = false;
        if(flag)
            builder.appendQueryParameter("cid", s);
        s1 = dblocation.getLocationName();
        if(flag || !dblocation.hasCoordinates()) {
        	 if(!TextUtils.isEmpty(s1))
                 builder.appendQueryParameter("q", s1);
        } else {
        	d = (double)dblocation.getLatitudeE7() / 10000000D;
            d1 = (double)dblocation.getLongitudeE7() / 10000000D;
            stringbuilder = new StringBuilder();
            stringbuilder.append(d).append(',').append(d1);
            if(!TextUtils.isEmpty(s1))
                stringbuilder.append('(').append(sanitizedLocationName(s1)).append(')');
            builder.appendQueryParameter("q", stringbuilder.toString());
        }
        launchMapsActivity(context, builder.build());
    }
    
    public static void showDrivingDirections(Context context, Place place) {
    	
    	if(place.getGeo() != null || place.getName() != null || place.getClusterId() == null) {
    		android.net.Uri.Builder builder;
            builder = Uri.parse("http://maps.google.com/maps").buildUpon();
            StringBuilder stringbuilder = new StringBuilder();
            if(null != place.getGeo()) {
            	stringbuilder.append(place.getGeo().getLatitude()).append(',').append(place.getGeo().getLongitude());
            	 if(!TextUtils.isEmpty(place.getName()))
                     stringbuilder.append('(').append(sanitizedLocationName(place.getName())).append(')');
                 builder.appendQueryParameter("daddr", stringbuilder.toString());
            } else {
            	if(!TextUtils.isEmpty(place.getName())) {
            		builder.appendQueryParameter("daddr", place.getName());
            	}
            }
            launchMapsActivity(context, builder.build());
    	} else {
    		android.net.Uri.Builder builder1 = Uri.parse("http://maps.google.com/maps").buildUpon();
            if(place.getGeo() != null)
                builder1.appendQueryParameter("ll", (new StringBuilder()).append(place.getGeo().getLatitude()).append(",").append(place.getGeo().getLongitude()).toString());
            if(place.getClusterId() != null)
                builder1.appendQueryParameter("cid", place.getClusterId());
            if(place.getClusterId() == null && place.getGeo() != null)
            {
                StringBuilder stringbuilder1 = new StringBuilder();
                stringbuilder1.append(place.getGeo().getLatitude()).append(',').append(place.getGeo().getLongitude());
                if(!TextUtils.isEmpty(place.getName()))
                    stringbuilder1.append('(').append(sanitizedLocationName(place.getName())).append(')');
                builder1.appendQueryParameter("q", stringbuilder1.toString());
            } else
            if(!TextUtils.isEmpty(place.getName()))
                builder1.appendQueryParameter("q", place.getName());
            launchMapsActivity(context, builder1.build());
    	}
    }
	
	private static String sanitizedLocationName(String s) {
		String s1;
		if (s == null)
			s1 = "";
		else
			s1 = s.replace('<', '[').replace('>', ']').replace('(', '[').replace(')', ']');
		return s1;
	}
}
