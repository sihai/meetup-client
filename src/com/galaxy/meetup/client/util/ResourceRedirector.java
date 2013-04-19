/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

/**
 * 
 * @author sihai
 *
 */
public class ResourceRedirector extends BaseResourceRedirector {

	private static ResourceRedirector sInstance;
	
	private ResourceRedirector()
    {
    }

    public static ResourceRedirector getInstance()
    {
        if(sInstance == null)
            sInstance = new ResourceRedirector();
        return sInstance;
    }

}
