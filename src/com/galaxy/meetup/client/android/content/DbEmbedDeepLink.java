/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.server.client.domain.DeepLinkData;
import com.galaxy.meetup.server.client.domain.PackagingServiceClient;

/**
 * 
 * @author sihai
 *
 */
public class DbEmbedDeepLink extends DbSerializer {

	protected List mClientPackageNames;
    protected String mDeepLinkId;
    protected String mLabel;
    protected String mUrl;
    
	protected DbEmbedDeepLink()
    {
    }

    public DbEmbedDeepLink(DeepLinkData deeplinkdata, String s)
    {
        mClientPackageNames = new ArrayList();
        if(deeplinkdata.client != null)
        {
            Iterator iterator = deeplinkdata.client.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                PackagingServiceClient packagingserviceclient = (PackagingServiceClient)iterator.next();
                if(!TextUtils.isEmpty(packagingserviceclient.androidPackageName) && TextUtils.equals("ANDROID", packagingserviceclient.type))
                    mClientPackageNames.add(packagingserviceclient.androidPackageName);
            } while(true);
        }
        mDeepLinkId = deeplinkdata.deepLinkId;
        mUrl = deeplinkdata.url;
        mLabel = s;
    }

    public static DbEmbedDeepLink deserialize(byte abyte0[])
    {
        DbEmbedDeepLink dbembeddeeplink;
        if(abyte0 == null)
        {
            dbembeddeeplink = null;
        } else
        {
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
            dbembeddeeplink = new DbEmbedDeepLink();
            dbembeddeeplink.mClientPackageNames = (ArrayList)getShortStringList(bytebuffer);
            dbembeddeeplink.mDeepLinkId = getShortString(bytebuffer);
            dbembeddeeplink.mLabel = getShortString(bytebuffer);
            dbembeddeeplink.mUrl = getShortString(bytebuffer);
        }
        return dbembeddeeplink;
    }

    public static byte[] serialize(DeepLinkData deeplinkdata, String s)
        throws IOException
    {
        DbEmbedDeepLink dbembeddeeplink = new DbEmbedDeepLink(deeplinkdata, s);
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(128);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        putShortStringList(dataoutputstream, dbembeddeeplink.mClientPackageNames);
        putShortString(dataoutputstream, dbembeddeeplink.mDeepLinkId);
        putShortString(dataoutputstream, dbembeddeeplink.mLabel);
        putShortString(dataoutputstream, dbembeddeeplink.mUrl);
        byte abyte0[] = bytearrayoutputstream.toByteArray();
        dataoutputstream.close();
        return abyte0;
    }

    public final List getClientPackageNames()
    {
        return mClientPackageNames;
    }

    public final String getDeepLinkId()
    {
        return mDeepLinkId;
    }

    public final String getLabelOrDefault(Context context)
    {
        String s;
        if(TextUtils.isEmpty(mLabel))
            s = context.getString(R.string.app_invite_default_action);
        else
            s = mLabel;
        return s;
    }

    public final String getUrl()
    {
        return mUrl;
    }
}
