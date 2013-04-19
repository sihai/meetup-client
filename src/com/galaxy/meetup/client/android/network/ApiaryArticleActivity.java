/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

import com.galaxy.meetup.client.util.StringUtils;
import com.galaxy.meetup.server.client.domain.MediaItem;
import com.galaxy.meetup.server.client.domain.MediaLayout;

/**
 * 
 * @author sihai
 *
 */
public class ApiaryArticleActivity extends ApiaryActivity {

	private String mContent;
    private String mDisplayName;
    private String mFavIconUrl;
    private List mImageList;
    
    public ApiaryArticleActivity()
    {
        mImageList = new ArrayList();
    }

    public final String getContent()
    {
        return mContent;
    }

    public final String getDisplayName()
    {
        return mDisplayName;
    }

    public final String getFavIconUrl()
    {
        return mFavIconUrl;
    }

    public final List getImages()
    {
        return Collections.unmodifiableList(mImageList);
    }

    public final ApiaryActivity.Type getType()
    {
        return ApiaryActivity.Type.ARTICLE;
    }

    protected final void update(MediaLayout medialayout)
        throws IOException
    {
        super.update(medialayout);
        mDisplayName = null;
        mContent = null;
        mImageList.clear();
        List list = medialayout.media;
        if(list != null && !list.isEmpty())
        {
            MediaItem mediaitem = (MediaItem)list.get(0);
            mImageList.add((new StringBuilder("https:")).append(mediaitem.thumbnailUrl).toString());
        }
        mDisplayName = StringUtils.unescape(medialayout.title);
        mFavIconUrl = medialayout.faviconUrl;
        if(!TextUtils.isEmpty(mFavIconUrl))
            mFavIconUrl = (new StringBuilder("https:")).append(mFavIconUrl).toString();
        mContent = StringUtils.unescape(medialayout.description);
    }
}
