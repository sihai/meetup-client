/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.IOException;

import android.os.Bundle;
import android.util.Log;

import com.galaxy.meetup.client.android.api.CallToActionData;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.MediaLayout;
import com.galaxy.meetup.server.client.domain.response.LinkPreviewResponse;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class ApiaryActivityFactory {

	public static ApiaryActivity getApiaryActivity(Bundle bundle, CallToActionData calltoactiondata) {
        ApiaryActivity apiaryactivity;
        if(bundle == null)
            throw new NullPointerException("Content deep-link metadata must not be null.");
        try {
	        if(EsLog.isLoggable("ApiaryActivityFactory", 3))
	            Log.d("ApiaryActivityFactory", bundle.toString());
	        apiaryactivity = new ApiaryActivity();
	        apiaryactivity.setContentDeepLinkMetadata(bundle);
	        if(calltoactiondata != null)
	            apiaryactivity.setCallToActionMetadata(calltoactiondata);
        } catch (IOException e) {
        	Log.e("Create ApiaryActivity failed", e.getMessage());
        	apiaryactivity = null;
        }
        return apiaryactivity;
    }

    public static ApiaryActivity getApiaryActivity(LinkPreviewResponse linkpreviewresponse) {
        if(linkpreviewresponse == null)
            throw new NullPointerException("");
        if(EsLog.isLoggable("ApiaryActivityFactory", 3))
            Log.d("ApiaryActivityFactory", JsonUtil.toJsonString(linkpreviewresponse));
        if(linkpreviewresponse.mediaLayout == null || linkpreviewresponse.mediaLayout.isEmpty())
            throw new IllegalArgumentException("Media layout must be specified");
        MediaLayout medialayout = (MediaLayout)linkpreviewresponse.mediaLayout.get(0);
        Object obj;
        if("WEBPAGE".equals(medialayout.layoutType))
            obj = new ApiaryArticleActivity();
        else
        if("VIDEO".equals(medialayout.layoutType))
            obj = new ApiaryVideoActivity();
        else
        if("SKYJAM_PREVIEW".equals(medialayout.layoutType))
            obj = new ApiarySkyjamActivity();
        else
        if("SKYJAM_PREVIEW_ALBUM".equals(medialayout.layoutType))
        {
            obj = new ApiarySkyjamActivity();
        } else if("IMAGE".equals(medialayout.layoutType))
        {
        	 obj = new ApiaryPhotoAlbumActivity();
        } else {
        	throw new RuntimeException(String.format("Unknown type:%s", medialayout.layoutType));
        }
        
        try
        {
            ((ApiaryActivity) (obj)).setLinkPreview(linkpreviewresponse);
        }
        catch(IOException ioexception)
        {
            obj = null;
        }
        return ((ApiaryActivity) (obj));
    }
}
