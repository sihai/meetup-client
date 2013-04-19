/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.text.TextUtils;

import com.galaxy.meetup.server.client.domain.EmbedsPostalAddress;
import com.galaxy.meetup.server.client.domain.GeoCoordinates;
import com.galaxy.meetup.server.client.domain.Location;
import com.galaxy.meetup.server.client.domain.Place;

/**
 * 
 * @author sihai
 *
 */
public class LocationUtils {

	public static Place convertLocationToPlace(Location location)
    {
        if(null == location) 
        	return null;
        
        Place place = new Place();
        if(location.latitudeE7 != null && location.longitudeE7 != null)
        {
            place.setGeo(new GeoCoordinates());
            place.getGeo().setLatitude(Double.valueOf((double)location.latitudeE7.intValue() / 10000000D));
            place.getGeo().setLongitude(Double.valueOf((double)location.longitudeE7.intValue() / 10000000D));
        }
        String s;
        if(!TextUtils.isEmpty(location.locationTag))
            s = location.locationTag;
        else
            s = location.bestAddress;
        if(s != null)
        {
            place.setName(s);
            place.setDescription(s);
            place.setAddress(new EmbedsPostalAddress());
            place.getAddress().setName(location.bestAddress);
        }
        return place;
    }
}
