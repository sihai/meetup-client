/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.IOException;
import java.util.Iterator;

import com.galaxy.meetup.client.util.StringUtils;
import com.galaxy.meetup.server.client.domain.MediaItem;
import com.galaxy.meetup.server.client.domain.MediaLayout;

/**
 * 
 * @author sihai
 *
 */
public class ApiarySkyjamActivity extends ApiaryActivity {

	private String mAlbumName;
    private String mArtistName;
    private String mImage;
    private String mTrackName;
    
	public ApiarySkyjamActivity()
    {
    }

    public final String getAlbumName()
    {
        return mAlbumName;
    }

    public final String getArtistName()
    {
        return mArtistName;
    }

    public final String getImage()
    {
        return mImage;
    }

    public final String getTrackName()
    {
        return mTrackName;
    }

    public final ApiaryActivity.Type getType()
    {
        return ApiaryActivity.Type.AUDIO;
    }

    protected final void update(MediaLayout medialayout)
        throws IOException
    {
        super.update(medialayout);
        Iterator iterator = medialayout.media.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            MediaItem mediaitem = (MediaItem)iterator.next();
            if(mediaitem.albumArtistHtml != null)
            {
                mImage = mediaitem.thumbnailUrl;
                mTrackName = StringUtils.unescape(mediaitem.caption);
                mAlbumName = StringUtils.unescape(mediaitem.albumHtml);
                mArtistName = StringUtils.unescape(mediaitem.albumArtistHtml);
            }
        } while(true);
    }

}
