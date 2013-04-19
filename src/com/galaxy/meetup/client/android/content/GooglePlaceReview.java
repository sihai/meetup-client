/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.Iterator;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.galaxy.meetup.server.client.domain.GoogleReviewProto;
import com.galaxy.meetup.server.client.domain.PriceLevelsProto;
import com.galaxy.meetup.server.client.domain.PriceProto;
import com.galaxy.meetup.server.client.domain.ZagatAspectRatingProto;

/**
 * 
 * @author sihai
 *
 */
public class GooglePlaceReview implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new GooglePlaceReview(parcel, (byte)0);
        }

        public final Object[] newArray(int i)
        {
            return new GooglePlaceReview[i];
        }

    };
    private String priceCurrencyCode;
    private long priceLevelValueId;
    private String priceValue;
    private String reviewText;
    private Bundle zagatAspects;
    
    private GooglePlaceReview(Parcel parcel)
    {
        zagatAspects = parcel.readBundle();
        reviewText = parcel.readString();
        priceValue = parcel.readString();
        priceCurrencyCode = parcel.readString();
        priceLevelValueId = parcel.readLong();
    }

    GooglePlaceReview(Parcel parcel, byte byte0)
    {
        this(parcel);
    }

    public GooglePlaceReview(GoogleReviewProto googlereviewproto)
    {
        zagatAspects = new Bundle();
        String s4;
        String s5;
        for(Iterator iterator = googlereviewproto.zagatAspectRatings.aspectRating.iterator(); iterator.hasNext(); zagatAspects.putString(s4, s5))
        {
            ZagatAspectRatingProto zagataspectratingproto = (ZagatAspectRatingProto)iterator.next();
            s4 = zagataspectratingproto.labelId;
            s5 = zagataspectratingproto.valueDisplay;
        }

        String s = googlereviewproto.fullText;
        String s1 = googlereviewproto.snippet;
        if(s == null || s.isEmpty())
            s = s1;
        reviewText = s;
        PriceProto priceproto = googlereviewproto.price;
        String s2;
        String s3;
        PriceLevelsProto pricelevelsproto;
        long l;
        if(priceproto == null)
            s2 = null;
        else
            s2 = priceproto.valueDisplay;
        priceValue = s2;
        s3 = null;
        if(priceproto != null)
            s3 = priceproto.currencyCode;
        priceCurrencyCode = s3;
        pricelevelsproto = googlereviewproto.priceLevel;
        if(pricelevelsproto == null || pricelevelsproto.ratedValueId == null)
            l = 0L;
        else
            l = pricelevelsproto.ratedValueId.longValue();
        priceLevelValueId = l;
    }

    public int describeContents()
    {
        return 0;
    }

    public final String getPriceCurrencyCode()
    {
        return priceCurrencyCode;
    }

    public final Long getPriceLevelId()
    {
        return Long.valueOf(priceLevelValueId);
    }

    public final String getPriceValue()
    {
        return priceValue;
    }

    public final String getReviewText()
    {
        return reviewText;
    }

    public final Bundle getZagatAspects()
    {
        return zagatAspects;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeBundle(zagatAspects);
        parcel.writeString(reviewText);
        parcel.writeString(priceValue);
        parcel.writeString(priceCurrencyCode);
        parcel.writeLong(priceLevelValueId);
    }

}
