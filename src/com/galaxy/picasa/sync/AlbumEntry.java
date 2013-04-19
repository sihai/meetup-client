/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;

/**
 * 
 * @author sihai
 *
 */
@Entry.Table("albums")
public class AlbumEntry extends Entry {

	public static final EntrySchema SCHEMA = new EntrySchema(AlbumEntry.class);
	
	@Entry.Column("album_type")
    public String albumType;
	
	@Entry.Column("bytes_used")
    public long bytesUsed;
	
	@Entry.Column(defaultValue="1", value="cache_flag")
    public int cacheFlag;
	
	@Entry.Column(defaultValue="0", value="cache_status")
    public int cacheStatus;
	
	@Entry.Column("date_edited")
    public long dateEdited;
	
	@Entry.Column("date_published")
    public long datePublished;
	
	@Entry.Column("date_updated")
    public long dateUpdated;
	
	@Entry.Column("html_page_url")
    public String htmlPageUrl;
	
	@Entry.Column("location_string")
    public String locationString;
	
	@Entry.Column("num_photos")
    public int numPhotos;
	
	@Entry.Column("photos_dirty")
    public boolean photosDirty;
	
	@Entry.Column("photos_etag")
    public String photosEtag;
	
	@Entry.Column("summary")
    public String summary;
	
	@Entry.Column("thumbnail_url")
    public String thumbnailUrl;
	
	@Entry.Column("title")
    public String title;
	
	@Entry.Column("user")
    public String user;
	
	@Entry.Column(indexed=true, value="user_id")
    public long userId;
    
    public AlbumEntry()
    {
        photosEtag = null;
    }

    public boolean equals(Object obj) {
    	
    	if(this == obj) {
    		return true;
    	}
    	if(!(obj instanceof AlbumEntry)) {
    		return false;
    	}
    	
        boolean flag1;
        AlbumEntry albumentry = (AlbumEntry)obj;
        long i = userId - albumentry.userId;
        flag1 = false;
        if(i == 0)
        {
            int j = cacheFlag;
            int k = albumentry.cacheFlag;
            flag1 = false;
            if(j == k)
            {
                int l = cacheStatus;
                int i1 = albumentry.cacheStatus;
                flag1 = false;
                if(l == i1)
                {
                    boolean flag2 = photosDirty;
                    boolean flag3 = albumentry.photosDirty;
                    flag1 = false;
                    if(flag2 == flag3)
                    {
                        boolean flag4 = Utils.equals(albumType, albumentry.albumType);
                        flag1 = false;
                        if(flag4)
                        {
                            boolean flag5 = Utils.equals(user, albumentry.user);
                            flag1 = false;
                            if(flag5)
                            {
                                boolean flag6 = Utils.equals(title, albumentry.title);
                                flag1 = false;
                                if(flag6)
                                {
                                    boolean flag7 = Utils.equals(summary, albumentry.summary);
                                    flag1 = false;
                                    if(flag7)
                                    {
                                        long j1 = datePublished - albumentry.datePublished;
                                        flag1 = false;
                                        if(j1 == 0)
                                        {
                                        	long k1 = dateUpdated - albumentry.dateUpdated;
                                            flag1 = false;
                                            if(k1 == 0)
                                            {
                                            	long l1 = dateEdited - albumentry.dateEdited;
                                                flag1 = false;
                                                if(l1 == 0)
                                                {
                                                    int i2 = numPhotos;
                                                    int j2 = albumentry.numPhotos;
                                                    flag1 = false;
                                                    if(i2 == j2)
                                                    {
                                                    	long k2 = bytesUsed - albumentry.bytesUsed;
                                                        flag1 = false;
                                                        if(k2 == 0)
                                                        {
                                                            boolean flag8 = Utils.equals(locationString, albumentry.locationString);
                                                            flag1 = false;
                                                            if(flag8)
                                                            {
                                                                boolean flag9 = Utils.equals(thumbnailUrl, albumentry.thumbnailUrl);
                                                                flag1 = false;
                                                                if(flag9)
                                                                {
                                                                    boolean flag10 = Utils.equals(htmlPageUrl, albumentry.htmlPageUrl);
                                                                    flag1 = false;
                                                                    if(flag10)
                                                                        flag1 = true;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return flag1;
    }

    public int hashCode()
    {
        return super.hashCode();
    }
}
