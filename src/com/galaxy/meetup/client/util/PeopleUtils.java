/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.content.SquareTargetData;

/**
 * 
 * @author sihai
 *
 */
public class PeopleUtils {

	public static boolean in(AudienceData audiencedata, AudienceData audiencedata1) {
        PersonData apersondata[];
        int i;
        int j;
        apersondata = audiencedata1.getUsers();
        i = apersondata.length;
        
        for(j = 0; j < i; j++) {
        	// TODO
        }
        
        return false;
    }

    public static boolean in(CircleData acircledata[], CircleData circledata) {
        int i;
        int j;
        i = acircledata.length;
        for(j = 0; j < i; j++) {
        	if(!acircledata[j].getId().equals(circledata.getId())) 
        		continue; 
        	else 
        		return true;
        }
        
        return false;
    }

    public static boolean in(PersonData apersondata[], PersonData persondata) {
        int i;
        int j;
        i = apersondata.length;
        for(j = 0; j < i; j++) {
        	if(!EsPeopleData.isSamePerson(apersondata[j], persondata)) 
        		continue; 
        	else 
        		return true;
        }
        return false;
    }

    public static boolean in(SquareTargetData asquaretargetdata[], SquareTargetData squaretargetdata) {
        int i;
        int j;
        i = asquaretargetdata.length;
        
        SquareTargetData squaretargetdata1;
        for(j = 0; j < i; j++) {
        	squaretargetdata1 = asquaretargetdata[j];
        	if(!TextUtils.equals(squaretargetdata1.getSquareId(), squaretargetdata.getSquareId()) || !TextUtils.equals(squaretargetdata1.getSquareStreamId(), squaretargetdata.getSquareStreamId())) {
        		continue; 
        	} else { 
        		return true;
        	}
        }
        return false;
    }

    public static boolean isEmpty(AudienceData audiencedata) {
        boolean flag;
        if(audiencedata.getUserCount() == 0 && audiencedata.getCircleCount() == 0 && audiencedata.getSquareTargetCount() == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }
}
