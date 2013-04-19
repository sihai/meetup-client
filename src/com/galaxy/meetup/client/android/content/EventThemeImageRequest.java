/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

/**
 * 
 * @author sihai
 *
 */
public class EventThemeImageRequest extends MediaImageRequest {

	public EventThemeImageRequest(String s)
    {
        super(s, 3, 0);
    }

    public final String getDownloadUrl()
    {
        return getUrl();
    }
}
