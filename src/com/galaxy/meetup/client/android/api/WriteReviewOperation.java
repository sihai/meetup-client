/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.GooglePlaceReview;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.AbuseSignals;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.PriceLevelsProto;
import com.galaxy.meetup.server.client.domain.PriceProto;
import com.galaxy.meetup.server.client.domain.ZagatAspectRatingProto;
import com.galaxy.meetup.server.client.domain.request.WritePlaceReviewRequest;
import com.galaxy.meetup.server.client.domain.response.WritePlaceReviewResponse;

/**
 * 
 * @author sihai
 *
 */
public class WriteReviewOperation extends PlusiOperation {

	private String cid;
    private GooglePlaceReview review;
    
	public WriteReviewOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, GooglePlaceReview googleplacereview, String s)
    {
        super(context, esaccount, "writeplacereview", intent, operationlistener, WritePlaceReviewResponse.class);
        review = googleplacereview;
        cid = s;
    }

    protected final void handleResponse(GenericJson genericjson)
        throws IOException
    {
    }

    protected final GenericJson populateRequest()
    {
        WritePlaceReviewRequest writeplacereviewrequest = new WritePlaceReviewRequest();
        writeplacereviewrequest.cid = cid;
        ArrayList arraylist = new ArrayList();
        Bundle bundle = review.getZagatAspects();
        ZagatAspectRatingProto zagataspectratingproto;
        for(Iterator iterator = bundle.keySet().iterator(); iterator.hasNext(); arraylist.add(zagataspectratingproto))
        {
            String s = (String)iterator.next();
            zagataspectratingproto = new ZagatAspectRatingProto();
            zagataspectratingproto.labelId = s;
            zagataspectratingproto.valueDisplay = bundle.getString(s);
        }

        writeplacereviewrequest.zagatAspectRatings = arraylist;
        if(review.getPriceValue() != null)
        {
            PriceProto priceproto = new PriceProto();
            priceproto.valueDisplay = review.getPriceValue();
            priceproto.currencyCode = review.getPriceCurrencyCode();
            writeplacereviewrequest.price = priceproto;
        }
        long l = review.getPriceLevelId().longValue();
        if(l != 0L)
        {
            PriceLevelsProto pricelevelsproto = new PriceLevelsProto();
            pricelevelsproto.ratedValueId = Long.valueOf(l);
            writeplacereviewrequest.priceLevel = pricelevelsproto;
        }
        writeplacereviewrequest.reviewText = review.getReviewText();
        writeplacereviewrequest.source = "hotpot-android-gplus";
        writeplacereviewrequest.abuseSignals = new AbuseSignals();
        return writeplacereviewrequest;
    }

}
