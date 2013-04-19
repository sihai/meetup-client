/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.IOException;
import java.util.List;

import com.galaxy.meetup.client.util.StringUtils;
import com.galaxy.meetup.server.client.domain.MediaItem;
import com.galaxy.meetup.server.client.domain.MediaLayout;

/**
 * 
 * @author sihai
 *
 */
public class ApiaryVideoActivity extends ApiaryActivity {

	private String mDisplayName;
    private String mImage;
    
	public ApiaryVideoActivity()
    {
    }

    public final String getDisplayName()
    {
        return mDisplayName;
    }

    public final String getImage()
    {
        return mImage;
    }

    public final ApiaryActivity.Type getType()
    {
        return ApiaryActivity.Type.VIDEO;
    }

    protected final void update(MediaLayout medialayout)
        throws IOException
    {
        super.update(medialayout);
        mDisplayName = null;
        mImage = null;
        List list = medialayout.media;
        if(list == null || list.isEmpty())
            throw new IOException("empty media item");
        MediaItem mediaitem = (MediaItem)list.get(0);
        if(mediaitem.thumbnailUrl == null)
        {
            throw new IOException("missing image object");
        } else
        {
            mImage = (new StringBuilder("https:")).append(mediaitem.thumbnailUrl).toString();
            mDisplayName = StringUtils.unescape(medialayout.title);
            return;
        }
    }
    
}