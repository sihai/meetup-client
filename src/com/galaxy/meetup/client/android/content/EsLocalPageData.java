/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;

import com.galaxy.meetup.server.client.domain.AttributeProto;
import com.galaxy.meetup.server.client.domain.FeaturedActivityProto;
import com.galaxy.meetup.server.client.domain.GoogleReviewProto;
import com.galaxy.meetup.server.client.domain.GoogleReviewsProto;
import com.galaxy.meetup.server.client.domain.OpeningHoursProto;
import com.galaxy.meetup.server.client.domain.OpeningHoursProtoDay;
import com.galaxy.meetup.server.client.domain.OpeningHoursProtoDayInterval;
import com.galaxy.meetup.server.client.domain.PlaceActivityStreamEntryProto;
import com.galaxy.meetup.server.client.domain.PlacePageAddressProto;
import com.galaxy.meetup.server.client.domain.SimpleProfile;

/**
 * 
 * @author sihai
 *
 */
public abstract class EsLocalPageData {

	private static String buildOpeningHoursStringForADay(OpeningHoursProtoDay openinghoursprotoday)
    {
        List list = openinghoursprotoday.interval;
        if(list == null) {
        	return null;
        }
        
        if(openinghoursprotoday.interval.isEmpty()) {
        	return null;
        }
        
        String s = null;
        StringBuilder stringbuilder = new StringBuilder();
        boolean flag1 = true;
        Iterator iterator = openinghoursprotoday.interval.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            OpeningHoursProtoDayInterval openinghoursprotodayinterval = (OpeningHoursProtoDayInterval)iterator.next();
            if(!TextUtils.isEmpty(openinghoursprotodayinterval.value))
            {
                if(!flag1)
                    stringbuilder.append(" ");
                stringbuilder.append(openinghoursprotodayinterval.value);
                flag1 = false;
            }
        } while(true);
        int i = stringbuilder.length();
        if(i != 0)
        {
            if(!TextUtils.isEmpty(openinghoursprotoday.dayName))
                stringbuilder.insert(0, (new StringBuilder()).append(openinghoursprotoday.dayName).append(" ").toString());
            s = stringbuilder.toString();
        }
        
        return s;
    }

    public static String getCid(SimpleProfile simpleprofile)
    {
        String s;
        if(simpleprofile.page.localInfo.paper.placeInfo == null)
            s = null;
        else
            s = simpleprofile.page.localInfo.paper.placeInfo.clusterId;
        return s;
    }

    private static FeaturedActivityProto getCircleActivityStory(SimpleProfile simpleprofile)
    {
        return simpleprofile.page.localInfo.paper.circleActivity;
    }

    public static List getCircleReviews(SimpleProfile simpleprofile)
    {
        ArrayList arraylist = new ArrayList();
        FeaturedActivityProto featuredactivityproto = getCircleActivityStory(simpleprofile);
        if(hasCircleActivity(simpleprofile))
        {
            Iterator iterator = featuredactivityproto.activity.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                PlaceActivityStreamEntryProto placeactivitystreamentryproto = (PlaceActivityStreamEntryProto)iterator.next();
                if(placeactivitystreamentryproto.review != null)
                    arraylist.add(placeactivitystreamentryproto.review);
            } while(true);
        }
        return arraylist;
    }

    public static String getFullAddress(SimpleProfile simpleprofile)
    {
    	PlacePageAddressProto placepageaddressproto = simpleprofile.page.localInfo.paper.address;
        if(null == placepageaddressproto) {
        	return null;
        }
        
        String s = null;
        List list = simpleprofile.page.localInfo.paper.address.addressLine;
        if(list != null)
        {
            int i = list.size();
            s = null;
            if(i != 0)
            {
                StringBuffer stringbuffer = new StringBuffer();
                stringbuffer.append((String)list.get(0));
                if(list.size() > 1)
                    stringbuffer.append("\n").append((String)list.get(1));
                s = stringbuffer.toString();
            }
        }
        
        return s;
    }

    public static String getOpeningHoursFull(SimpleProfile simpleprofile)
    {
        OpeningHoursProto openinghoursproto = simpleprofile.page.localInfo.paper.openingHours;
        String s = null;
        if(openinghoursproto == null) {
        	return null; 
        }
        
        List list = openinghoursproto.day;
        if(list == null) {
        	return null;
        }
        
        if(openinghoursproto.day.isEmpty()) {
        	return null;
        }
        
        StringBuilder stringbuilder = new StringBuilder();
        boolean flag1 = true;
        Iterator iterator = openinghoursproto.day.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s1 = buildOpeningHoursStringForADay((OpeningHoursProtoDay)iterator.next());
            if(s1 != null)
            {
                if(!flag1)
                    stringbuilder.append("\n");
                stringbuilder.append(s1);
                flag1 = false;
            }
        } while(true);
        int i = stringbuilder.length();
        s = null;
        if(i != 0)
            s = stringbuilder.toString();
        return s;
    }

    public static String getOpeningHoursSummary(SimpleProfile simpleprofile)
    {
        OpeningHoursProto openinghoursproto = simpleprofile.page.localInfo.paper.openingHours;
        String s;
        if(openinghoursproto == null || openinghoursproto.today == null)
            s = null;
        else
            s = buildOpeningHoursStringForADay(openinghoursproto.today);
        return s;
    }

    public static String getPriceLabel(SimpleProfile simpleprofile)
    {
        AttributeProto attributeproto = getPriceStory(simpleprofile);
        String s;
        if(attributeproto != null)
            s = attributeproto.labelDisplay;
        else
            s = null;
        return s;
    }

    private static AttributeProto getPriceStory(SimpleProfile simpleprofile)
    {
        AttributeProto attributeproto;
        if(simpleprofile.page.localInfo.paper.priceContinuous != null)
            attributeproto = simpleprofile.page.localInfo.paper.priceContinuous;
        else
            attributeproto = simpleprofile.page.localInfo.paper.price;
        return attributeproto;
    }

    public static String getPriceValue(SimpleProfile simpleprofile)
    {
        AttributeProto attributeproto = getPriceStory(simpleprofile);
        String s;
        if(attributeproto != null)
            s = attributeproto.value.priceLevel;
        else
            s = null;
        return s;
    }

    public static List getReviews(SimpleProfile simpleprofile)
    {
        ArrayList arraylist = new ArrayList();
        GoogleReviewsProto googlereviewsproto = simpleprofile.page.localInfo.paper.googleReviews;
        boolean flag;
        if(googlereviewsproto != null && googlereviewsproto.review != null && googlereviewsproto.review.size() > 0)
            flag = true;
        else
            flag = false;
        if(flag)
        {
            for(Iterator iterator = googlereviewsproto.review.iterator(); iterator.hasNext(); arraylist.add((GoogleReviewProto)iterator.next()));
        }
        return arraylist;
    }

    public static FeaturedActivityProto getUserActivityStory(SimpleProfile simpleprofile)
    {
        return simpleprofile.page.localInfo.paper.userActivity;
    }

    public static GoogleReviewProto getYourReview(SimpleProfile simpleprofile) {
        FeaturedActivityProto featuredactivityproto = getUserActivityStory(simpleprofile);
        if(!hasYourActivity(simpleprofile)) { 
        	return null; 
        }
        Iterator iterator = featuredactivityproto.activity.iterator();
        while(iterator.hasNext()) {
        	PlaceActivityStreamEntryProto placeactivitystreamentryproto = (PlaceActivityStreamEntryProto)iterator.next();
        	if(null != placeactivitystreamentryproto.review) {
        		return placeactivitystreamentryproto.review;
        	}
        }
        
        return null;
    }

    public static List getZagatAspects(GoogleReviewProto googlereviewproto)
    {
        List list;
        if(googlereviewproto == null || googlereviewproto.zagatAspectRatings == null)
            list = null;
        else
            list = googlereviewproto.zagatAspectRatings.aspectRating;
        return list;
    }

    private static boolean hasActivity(FeaturedActivityProto featuredactivityproto)
    {
        boolean flag;
        if(featuredactivityproto != null && featuredactivityproto.totalReviews != null && featuredactivityproto.totalReviews.intValue() > 0 && featuredactivityproto.activity != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static boolean hasCircleActivity(SimpleProfile simpleprofile)
    {
        return hasActivity(getCircleActivityStory(simpleprofile));
    }

    public static boolean hasYourActivity(SimpleProfile simpleprofile)
    {
        return hasActivity(getUserActivityStory(simpleprofile));
    }
}
