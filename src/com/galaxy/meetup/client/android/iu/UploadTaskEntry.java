/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Fingerprint;

/**
 * 
 * @author sihai
 *
 */
@Entry.Table("upload_tasks")
public class UploadTaskEntry extends Entry {

	private static final String REQUIRED_COLUMNS[] = {
        "account", "content_uri", "media_record_id"
    };
	
	public static final EntrySchema SCHEMA = new EntrySchema(UploadTaskEntry.class);
	
	@Entry.Column("account")
    private String mAccount;
	@Entry.Column("album_id")
    private String mAlbumId;
	@Entry.Column("album_title")
    private String mAlbumTitle;
	@Entry.Column("auth_token_type")
    private String mAuthTokenType;
	@Entry.Column("bytes_total")
    private long mBytesTotal;
	@Entry.Column("bytes_uploaded")
    private long mBytesUploaded;
    private ComponentName mComponentName;
    @Entry.Column("content_uri")
    private String mContentUri;
    @Entry.Column("display_name")
    private String mDisplayName;
    private Throwable mError;
    @Entry.Column("event_id")
    private String mEventId;
    @Entry.Column("fingerprint")
    private byte mFingerprint[];
    @Entry.Column("media_record_id")
    private long mMediaRecordId;
    @Entry.Column("mime_type")
    private String mMimeType;
    @Entry.Column("priority")
    private int mPriority;
    @Entry.Column("component_name")
    private String mRawComponentName;
    @Entry.Column("request_template")
    private String mRequestTemplate;
    @Entry.Column("state")
    private int mState;
    @Entry.Column("upload_url")
    private String mUploadUrl;
    @Entry.Column("uploaded_time")
    private long mUploadedTime;
    @Entry.Column("url")
    private String mUrl;
    
    private UploadTaskEntry()
    {
        mState = 3;
    }

    static UploadTaskEntry createNew(ContentValues contentvalues)
    {
        List arraylist = new ArrayList();
        String as[] = REQUIRED_COLUMNS;
        int i = as.length;
        for(int j = 0; j < i; j++)
        {
            String s = as[j];
            if(contentvalues.get(s) == null)
                arraylist.add(s);
        }

        if(!arraylist.isEmpty())
            throw new RuntimeException((new StringBuilder("missing fields in upload request: ")).append(arraylist).toString());
        else
            return (UploadTaskEntry)SCHEMA.valuesToObject(contentvalues, new UploadTaskEntry());
    }

    public static UploadTaskEntry fromDb(SQLiteDatabase sqlitedatabase, long l)
    {
        UploadTaskEntry uploadtaskentry = new UploadTaskEntry();
        if(!SCHEMA.queryWithId(sqlitedatabase, l, uploadtaskentry))
            uploadtaskentry = null;
        return uploadtaskentry;
    }

    public final String getAccount()
    {
        return mAccount;
    }

    public final String getAlbumId()
    {
        return mAlbumId;
    }

    public final long getBytesTotal()
    {
        return mBytesTotal;
    }

    public final long getBytesUploaded()
    {
        return mBytesUploaded;
    }

    final ComponentName getComponentName()
    {
        if(mComponentName == null && mRawComponentName != null)
            mComponentName = ComponentName.unflattenFromString(mRawComponentName);
        return mComponentName;
    }

    public final Uri getContentUri()
    {
        return Uri.parse(mContentUri);
    }

    public final String getEventId()
    {
        return mEventId;
    }

    public final Fingerprint getFingerprint()
    {
        Fingerprint fingerprint;
        if(mFingerprint == null)
            fingerprint = null;
        else
            fingerprint = new Fingerprint(mFingerprint);
        return fingerprint;
    }

    public final long getMediaRecordId()
    {
        return mMediaRecordId;
    }

    final String getMimeType()
    {
        return mMimeType;
    }

    public final int getPercentageUploaded() {
    	int i = 0;
        if(mBytesTotal != 0L && mBytesUploaded != 0L) {
        	i = (int)Math.round(100D * (double)((float)mBytesUploaded / (float)mBytesTotal));
            if(i > 100)
                i = 100;
        }
        return i;
    }

    final String getRequestTemplate()
    {
        return mRequestTemplate;
    }

    public final int getState()
    {
        return mState;
    }

    final String getUploadUrl()
    {
        return mUploadUrl;
    }

    public final Uri getUrl()
    {
        Uri uri;
        if(mUrl == null)
            uri = null;
        else
            uri = Uri.parse(mUrl);
        return uri;
    }

    public final boolean hasFingerprint()
    {
        boolean flag;
        if(mFingerprint != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean hasPriority()
    {
        boolean flag = true;
        if(mPriority != 2 && mPriority != 1)
            flag = false;
        return flag;
    }

    public final boolean isCancellable()
    {
        boolean flag = true;
        if(mState != 1 && mState != 2 && mState != 3)
            flag = false;
        return flag;
    }

    public final boolean isReadyForUpload()
    {
        boolean flag = true;
        if(mState != 3 && mState != 1)
            flag = false;
        return flag;
    }

    public final boolean isStartedYet()
    {
        boolean flag;
        if(mBytesUploaded > 0L)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isUploading()
    {
        boolean flag = true;
        if(mState != 1)
            flag = false;
        return flag;
    }

    public final void setAlbumId(String s)
    {
        mAlbumId = s;
    }

    final void setAuthTokenType(String s)
    {
        mAuthTokenType = s;
    }

    public final void setBytesTotal(long l)
    {
        mBytesTotal = l;
    }

    public final void setBytesUploaded(long l)
    {
        mBytesUploaded = l;
    }

    public final void setFingerprint(Fingerprint fingerprint)
    {
        mFingerprint = fingerprint.getBytes();
    }

    final void setMimeType(String s)
    {
        mMimeType = s;
    }

    final void setPriority(int i)
    {
        mPriority = 1;
    }

    final void setRequestTemplate(String s)
    {
        mRequestTemplate = s;
    }

    public final void setState(int i)
    {
        mState = i;
    }

    public final void setState(int i, Throwable throwable)
    {
        mState = i;
        mError = throwable;
    }

    final void setUploadUrl(String s)
    {
        mUploadUrl = s;
    }

    final void setUploadedTime()
    {
        mUploadedTime = System.currentTimeMillis();
    }

    final void setUrl(String s)
    {
        mUrl = s;
    }

    public final boolean shouldRetry()
    {
        boolean flag;
        if(mState == 2)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public String toString()
    {
        return (new StringBuilder()).append(SCHEMA.toDebugString(this, new String[] {
            "content_uri", "media_record_id", "album_id", "event_id", "mime_type", "state", "bytes_total"
        })).append(" [").append(getPercentageUploaded()).append("%]").toString();
    }
}
