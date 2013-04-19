/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.Serializable;

/**
 * 
 * @author sihai
 *
 */
public class ApiaryApiInfo implements Serializable {

	private static final long serialVersionUID = 0x2ba6046fa3ebca2aL;
    private final String mApiKey;
    private final String mCertificate;
    private final String mClientId;
    private final String mPackageName;
    private final String mSdkVersion;
    private final ApiaryApiInfo mSourceInfo;
    
	public ApiaryApiInfo(String s, String s1, String s2, String s3, String s4)
    {
        this(s, s1, s2, s3, s4, null);
    }

    public ApiaryApiInfo(String s, String s1, String s2, String s3, String s4, ApiaryApiInfo apiaryapiinfo)
    {
        mApiKey = s;
        mClientId = s1;
        mPackageName = s2;
        mCertificate = s3;
        mSourceInfo = apiaryapiinfo;
        mSdkVersion = s4;
    }

    public final String getApiKey()
    {
        return mApiKey;
    }

    public final String getCertificate()
    {
        return mCertificate;
    }

    public final String getClientId()
    {
        return mClientId;
    }

    public final String getPackageName()
    {
        return mPackageName;
    }

    public final String getSdkVersion()
    {
        return mSdkVersion;
    }

    public final ApiaryApiInfo getSourceInfo()
    {
        return mSourceInfo;
    }
}
