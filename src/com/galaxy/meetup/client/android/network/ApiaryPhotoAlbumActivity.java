/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.galaxy.meetup.client.util.StringUtils;
import com.galaxy.meetup.server.client.domain.MediaItem;
import com.galaxy.meetup.server.client.domain.MediaLayout;

/**
 * 
 * @author sihai
 *
 */
public class ApiaryPhotoAlbumActivity extends ApiaryActivity {

	private String mDisplayName;
    private List mImageList;
    
	public ApiaryPhotoAlbumActivity()
    {
        mImageList = new LinkedList();
    }

    public final String getDisplayName()
    {
        return mDisplayName;
    }

    public final List getImages()
    {
        return Collections.unmodifiableList(mImageList);
    }

    public final ApiaryActivity.Type getType()
    {
        return ApiaryActivity.Type.PHOTOALBUM;
    }

    protected final void update(MediaLayout medialayout)
        throws IOException
    {
        super.update(medialayout);
        mDisplayName = null;
        mImageList.clear();
        List list = medialayout.media;
        if(list == null || list.isEmpty())
            throw new IOException("empty media item");
        MediaItem mediaitem;
        for(Iterator iterator = list.iterator(); iterator.hasNext(); mImageList.add(mediaitem.thumbnailUrl))
            mediaitem = (MediaItem)iterator.next();

        mDisplayName = StringUtils.unescape(medialayout.title);
    }

}
