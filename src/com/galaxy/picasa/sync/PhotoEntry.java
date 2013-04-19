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
@Entry.Table("photos")
public class PhotoEntry extends Entry {

	public static final EntrySchema SCHEMA = new EntrySchema(PhotoEntry.class);
	
	@Entry.Column(indexed=true, value="album_id")
    public long albumId;
	
	@Entry.Column(defaultValue="0", value="cache_status")
    int cacheStatus;
	
	@Entry.Column("camera_sync")
    int cameraSync;
	
	@Entry.Column("comment_count")
    public int commentCount;
	
	@Entry.Column("content_type")
    public String contentType;
	
	@Entry.Column("content_url")
    public String contentUrl;
	
	@Entry.Column("date_edited")
    public long dateEdited;
	
	@Entry.Column("date_published")
    public long datePublished;
	
	@Entry.Column("date_taken")
    public long dateTaken;
	
	@Entry.Column("date_updated")
    public long dateUpdated;
	
	@Entry.Column(indexed=true, value="display_index")
    public int displayIndex;
	
	@Entry.Column("exif_exposure")
    public float exifExposure;
	
	@Entry.Column("exif_flash")
    public int exifFlash;
	
	@Entry.Column("exif_focal_length")
    public float exifFocalLength;
	
	@Entry.Column("exif_fstop")
    public float exifFstop;
	
	@Entry.Column("exif_iso")
    public int exifIso;
	
	@Entry.Column("exif_make")
    public String exifMake;
	
	@Entry.Column("exif_model")
    public String exifModel;
	
	@Entry.Column("face_ids")
    public String faceIds;
	
	@Entry.Column("face_names")
    public String faceNames;
	
	@Entry.Column("face_rectangles")
    public String faceRects;
	
	@Entry.Column("fingerprint")
    public byte fingerprint[];
	
	@Entry.Column("fingerprint_hash")
    int fingerprintHash;
	
	@Entry.Column("height")
    public int height;
	
	@Entry.Column("html_page_url")
    public String htmlPageUrl;
	
	@Entry.Column("keywords")
    public String keywords;
	
	@Entry.Column("latitude")
    public double latitude;
	
	@Entry.Column("longitude")
    public double longitude;
	
	@Entry.Column("rotation")
    public int rotation;
	
	@Entry.Column("screennail_url")
    public String screennailUrl;
	
	@Entry.Column("size")
    public int size;
	
	@Entry.Column("summary")
    public String summary;
	
	@Entry.Column("title")
    public String title;
	
	@Entry.Column("user_id")
    public long userId;
	
	@Entry.Column("width")
    public int width;
    
    public PhotoEntry()
    {
    }

    public final boolean equals(Object obj)
    {
    	if(this == obj) {
    		return true;
    	}
    	if(!(obj instanceof PhotoEntry)) {
    		return false;
    	}
    	
        PhotoEntry photoentry = (PhotoEntry)obj;
        long i = albumId - photoentry.albumId;
        boolean flag1 = false;
        if(i == 0)
        {
            int j = displayIndex;
            int k = photoentry.displayIndex;
            flag1 = false;
            if(j == k)
            {
                long l = userId - photoentry.userId;
                flag1 = false;
                if(l == 0)
                {
                    boolean flag2 = Utils.equals(title, photoentry.title);
                    flag1 = false;
                    if(flag2)
                    {
                        boolean flag3 = Utils.equals(summary, photoentry.summary);
                        flag1 = false;
                        if(flag3)
                        {
                        	long i1 = datePublished - photoentry.datePublished;
                            flag1 = false;
                            if(i1 == 0)
                            {
                            	long j1 = dateUpdated - photoentry.dateUpdated;
                                flag1 = false;
                                if(j1 == 0)
                                {
                                	long k1 = dateEdited - photoentry.dateEdited;
                                    flag1 = false;
                                    if(k1 == 0)
                                    {
                                    	long l1 = dateTaken - photoentry.dateTaken;
                                        flag1 = false;
                                        if(l1 == 0)
                                        {
                                            int i2 = commentCount;
                                            int j2 = photoentry.commentCount;
                                            flag1 = false;
                                            if(i2 == j2)
                                            {
                                                int k2 = width;
                                                int l2 = photoentry.width;
                                                flag1 = false;
                                                if(k2 == l2)
                                                {
                                                    int i3 = height;
                                                    int j3 = photoentry.height;
                                                    flag1 = false;
                                                    if(i3 == j3)
                                                    {
                                                        int k3 = rotation;
                                                        int l3 = photoentry.rotation;
                                                        flag1 = false;
                                                        if(k3 == l3)
                                                        {
                                                            int i4 = size;
                                                            int j4 = photoentry.size;
                                                            flag1 = false;
                                                            if(i4 == j4)
                                                            {
                                                            	double k4 = latitude - photoentry.latitude;
                                                                flag1 = false;
                                                                if(k4 == 0)
                                                                {
                                                                	double l4 = longitude - photoentry.longitude;
                                                                    flag1 = false;
                                                                    if(l4 == 0)
                                                                    {
                                                                        boolean flag4 = Utils.equals(contentUrl, photoentry.contentUrl);
                                                                        flag1 = false;
                                                                        if(flag4)
                                                                        {
                                                                            boolean flag5 = Utils.equals(htmlPageUrl, photoentry.htmlPageUrl);
                                                                            flag1 = false;
                                                                            if(flag5)
                                                                            {
                                                                                boolean flag6 = Utils.equals(keywords, photoentry.keywords);
                                                                                flag1 = false;
                                                                                if(flag6)
                                                                                {
                                                                                    boolean flag7 = Utils.equals(faceNames, photoentry.faceNames);
                                                                                    flag1 = false;
                                                                                    if(flag7)
                                                                                    {
                                                                                        boolean flag8 = Utils.equals(faceIds, photoentry.faceIds);
                                                                                        flag1 = false;
                                                                                        if(flag8)
                                                                                        {
                                                                                            boolean flag9 = Utils.equals(faceRects, photoentry.faceRects);
                                                                                            flag1 = false;
                                                                                            if(flag9)
                                                                                            {
                                                                                                boolean flag10 = Utils.equals(exifMake, photoentry.exifMake);
                                                                                                flag1 = false;
                                                                                                if(flag10)
                                                                                                {
                                                                                                    boolean flag11 = Utils.equals(exifModel, photoentry.exifModel);
                                                                                                    flag1 = false;
                                                                                                    if(flag11)
                                                                                                    {
                                                                                                    	double i5 = exifExposure - photoentry.exifExposure;
                                                                                                        flag1 = false;
                                                                                                        if(i5 == 0)
                                                                                                        {
                                                                                                            int j5 = exifFlash;
                                                                                                            int k5 = photoentry.exifFlash;
                                                                                                            flag1 = false;
                                                                                                            if(j5 == k5)
                                                                                                            {
                                                                                                            	float l5 = exifFocalLength - photoentry.exifFocalLength;
                                                                                                                flag1 = false;
                                                                                                                if(l5 == 0)
                                                                                                                {
                                                                                                                	float i6 = exifFstop - photoentry.exifFstop;
                                                                                                                    flag1 = false;
                                                                                                                    if(i6 == 0)
                                                                                                                    {
                                                                                                                        int j6 = exifIso;
                                                                                                                        int k6 = photoentry.exifIso;
                                                                                                                        flag1 = false;
                                                                                                                        if(j6 == k6)
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

    public final int hashCode()
    {
        return super.hashCode();
    }
}
