/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.galaxy.meetup.client.android.api.CallToActionData;
import com.galaxy.meetup.server.client.domain.AppInvite;
import com.galaxy.meetup.server.client.domain.DeepLink;
import com.galaxy.meetup.server.client.domain.DeepLinkData;
import com.galaxy.meetup.server.client.domain.EmbedClientItem;
import com.galaxy.meetup.server.client.domain.MediaLayout;
import com.galaxy.meetup.server.client.domain.Thing;
import com.galaxy.meetup.server.client.domain.response.LinkPreviewResponse;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class ApiaryActivity implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            String s = parcel.readString();
            Bundle bundle = parcel.readBundle();
            CallToActionData calltoactiondata = (CallToActionData)parcel.readParcelable(CallToActionData.class.getClassLoader());
            ApiaryActivity apiaryactivity;
            if(s != null)
                apiaryactivity = ApiaryActivityFactory.getApiaryActivity((LinkPreviewResponse)JsonUtil.toBean(s, LinkPreviewResponse.class));
            else
                apiaryactivity = ApiaryActivityFactory.getApiaryActivity(bundle, calltoactiondata);
            return apiaryactivity;
        }

        public final Object[] newArray(int i)
        {
            return new ApiaryActivity[i];
        }

    };
    
    private CallToActionData mCallToActionButton;
    private Bundle mContentDeepLinkMetadata;
    private LinkPreviewResponse mLinkPreview;
    
    public ApiaryActivity()
    {
    }

    private void update()
        throws IOException
    {
        if(mLinkPreview == null)
        {
            Bundle _tmp = mContentDeepLinkMetadata;
            if(mContentDeepLinkMetadata == null)
                throw new IOException("No metadata.");
        } else
        {
            update((MediaLayout)mLinkPreview.mediaLayout.get(0));
        }
    }

    public int describeContents()
    {
        return 0;
    }

    public String getContent()
    {
        return mContentDeepLinkMetadata.getString("description");
    }

    public String getDisplayName()
    {
        return mContentDeepLinkMetadata.getString("title");
    }

    public final EmbedClientItem getEmbed(String s)
    {
        EmbedClientItem embedclientitem1;
        if(mLinkPreview != null && mLinkPreview.embedItem != null)
            embedclientitem1 = (EmbedClientItem)mLinkPreview.embedItem.get(0);
        else
        if(mContentDeepLinkMetadata != null)
        {
            EmbedClientItem embedclientitem = new EmbedClientItem();
            embedclientitem.thing = new Thing();
            embedclientitem.thing.name = mContentDeepLinkMetadata.getString("title");
            embedclientitem.thing.description = mContentDeepLinkMetadata.getString("description");
            embedclientitem.thing.imageUrl = mContentDeepLinkMetadata.getString("thumbnailUrl");
            embedclientitem.type = new ArrayList();
            embedclientitem.type.add("THING");
            if(mCallToActionButton != null)
            {
                embedclientitem1 = new EmbedClientItem();
                embedclientitem1.appInvite = new AppInvite();
                embedclientitem1.appInvite.about = embedclientitem;
                embedclientitem1.appInvite.callToAction = new DeepLink();
                embedclientitem1.appInvite.callToAction.deepLinkLabel = mCallToActionButton.mLabel;
                embedclientitem1.appInvite.callToAction.label = mCallToActionButton.mLabel;
                embedclientitem1.appInvite.callToAction.deepLink = new DeepLinkData();
                embedclientitem1.appInvite.callToAction.deepLink.deepLinkId = mCallToActionButton.mDeepLinkId;
                embedclientitem1.appInvite.callToAction.deepLink.url = mCallToActionButton.mUrl;
                embedclientitem1.type = new ArrayList();
                embedclientitem1.type.add("APP_INVITE");
            } else
            {
                embedclientitem1 = embedclientitem;
            }
        } else
        {
            embedclientitem1 = null;
        }
        if(s != null && embedclientitem1 != null)
        {
            embedclientitem1.deepLinkData = new DeepLinkData();
            embedclientitem1.deepLinkData.deepLinkId = s;
        }
        return embedclientitem1;
    }

    public String getFavIconUrl()
    {
        return null;
    }

    public String getImage()
    {
        return mContentDeepLinkMetadata.getString("thumbnailUrl");
    }

    public final String getMediaJson()
    {
        List list;
        String s;
        if(mLinkPreview == null)
            list = null;
        else
            list = mLinkPreview.blackboxPreviewData;
        s = null;
        if(list != null)
        {
            boolean flag = list.isEmpty();
            s = null;
            if(!flag)
            {
                StringBuilder stringbuilder = new StringBuilder("[");
                for(Iterator iterator = list.iterator(); iterator.hasNext(); stringbuilder.append(","))
                    stringbuilder.append((String)iterator.next());

                stringbuilder.deleteCharAt(-1 + stringbuilder.length());
                stringbuilder.append("]");
                s = stringbuilder.toString();
            }
        }
        return s;
    }

    public Type getType()
    {
        return Type.NONE;
    }

    public final void setCallToActionMetadata(CallToActionData calltoactiondata)
    {
        mCallToActionButton = calltoactiondata;
    }

    public final void setContentDeepLinkMetadata(Bundle bundle)
        throws IOException
    {
        mContentDeepLinkMetadata = bundle;
        update();
    }

    public final void setLinkPreview(LinkPreviewResponse linkpreviewresponse)
        throws IOException
    {
        mLinkPreview = linkpreviewresponse;
        update();
    }

    protected void update(MediaLayout medialayout)
        throws IOException
    {
        if(mLinkPreview == null)
            throw new IOException("No metadata.");
        else
            return;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        if(mLinkPreview != null)
            parcel.writeString(mLinkPreview.toJsonString());
        else
            parcel.writeString(null);
        parcel.writeBundle(mContentDeepLinkMetadata);
        parcel.writeParcelable(mCallToActionButton, 0);
    }


    public static enum Type {
    	NONE,
        ARTICLE,
        PHOTOALBUM,
        VIDEO,
        AUDIO;
    }
}
