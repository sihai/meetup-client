/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Fingerprint;

/**
 * 
 * @author sihai
 * 
 */
@Entry.Table("media_record")
public class MediaRecordEntry extends Entry {

	public static final EntrySchema SCHEMA = new EntrySchema(
			MediaRecordEntry.class);

	@Entry.Column("album_id")
	private String mAlbumId;

	@Entry.Column("bytes_total")
	private long mBytesTotal;

	@Entry.Column("bytes_uploaded")
	private long mBytesUploaded;
	
	private Throwable mError;

	@Entry.Column("event_id")
	private String mEventId;

	@Entry.Column("fingerprint")
	private byte[] mFingerprint;

	@Entry.Column(allowNull = false, defaultValue = "0", value = "from_camera")
	private boolean mFromCamera;

	@Entry.Column(allowNull = false, defaultValue = "1", value = "is_image")
	private boolean mIsImage;

	@Entry.Column(allowNull = false, value = "media_hash")
	private long mMediaHash;

	@Entry.Column(allowNull = false, indexed = true, value = "media_id")
	private long mMediaId;

	@Entry.Column(allowNull = false, value = "media_time")
	private long mMediaTime;

	@Entry.Column(allowNull = false, value = "media_url")
	private String mMediaUrl;

	@Entry.Column("upload_account")
	private String mUploadAccount;

	@Entry.Column("upload_error")
	private String mUploadError;

	@Entry.Column("upload_id")
	private long mUploadId;

	@Entry.Column(allowNull = false, defaultValue = "0", value = "upload_reason")
	private int mUploadReason;

	@Entry.Column(allowNull = false, defaultValue = "200", value = "upload_state")
	private int mUploadState;

	@Entry.Column("upload_time")
	private long mUploadTime;

	@Entry.Column("upload_url")
	private String mUploadUrl;

	private MediaRecordEntry() {
	}

	static MediaRecordEntry createNew(ContentValues contentvalues) {
		return (MediaRecordEntry) SCHEMA.valuesToObject(contentvalues,
				new MediaRecordEntry());
	}

	public static MediaRecordEntry fromCursor(Cursor cursor) {
		return (MediaRecordEntry) SCHEMA.cursorToObject(cursor,
				new MediaRecordEntry());
	}

	public static MediaRecordEntry fromId(SQLiteDatabase sqlitedatabase, long l) {
		MediaRecordEntry mediarecordentry = new MediaRecordEntry();
		if (!SCHEMA.queryWithId(sqlitedatabase, l, mediarecordentry))
			mediarecordentry = null;
		return mediarecordentry;
	}

	public static MediaRecordEntry fromMediaId(SQLiteDatabase sqlitedatabase,
			long l) {
		Cursor cursor = null;
		String s = SCHEMA.getTableName();
		String as[] = SCHEMA.getProjection();
		String as1[] = new String[1];
		as1[0] = Long.toString(l);
		try {
			cursor = sqlitedatabase.query(s, as,
					"media_id = ? AND upload_account IS NULL", as1, null, null,
					null);
			if (cursor.moveToFirst()) {
				return (MediaRecordEntry) SCHEMA.cursorToObject(cursor,
						new MediaRecordEntry());
			}
			return null;
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
	}

	public final String getAlbumId() {
		return mAlbumId;
	}

	public final long getBytesTotal() {
		return mBytesTotal;
	}

	public final String getEventId() {
		return mEventId;
	}

	public final Fingerprint getFingerprint() {
		Fingerprint fingerprint;
		if (mFingerprint != null)
			fingerprint = new Fingerprint(mFingerprint);
		else
			fingerprint = null;
		return fingerprint;
	}

	public final byte[] getFingerprintBytes() {
		return mFingerprint;
	}

	public final long getMediaTime() {
		return mMediaTime;
	}

	public final String getMediaUrl() {
		return mMediaUrl;
	}

	public final String getUploadAccount() {
		return mUploadAccount;
	}

	public final long getUploadId() {
		return mUploadId;
	}

	public final int getUploadReason() {
		return mUploadReason;
	}

	public final boolean isImage() {
		return mIsImage;
	}

	public final MediaRecordEntry setBytesUploaded(long l) {
		mBytesUploaded = l;
		return this;
	}

	public final MediaRecordEntry setState(int i) {
		mUploadState = i;
		return this;
	}

	public final MediaRecordEntry setState(int i, int j) {
		mUploadState = i + j;
		return this;
	}

	public final MediaRecordEntry setState(int i, int j, Throwable throwable) {
		mUploadState = i + j;
		mError = throwable;
		return this;
	}

	public final MediaRecordEntry setUploadAccount(String s) {
		mUploadAccount = s;
		return this;
	}

	public final MediaRecordEntry setUploadId(long l) {
		mUploadId = l;
		return this;
	}

	public final MediaRecordEntry setUploadReason(int i) {
		mUploadReason = i;
		return this;
	}

	public final MediaRecordEntry setUploadTime(long l) {
		mUploadTime = l;
		return this;
	}

	public final MediaRecordEntry setUploadUrl(String s) {
		mUploadUrl = s;
		return this;
	}

	public String toString() {
		StringBuilder stringbuilder = (new StringBuilder()).append(
				SCHEMA.toDebugString(this, new String[] { "media_url",
						"media_id", "album_id", "event_id", "upload_reason",
						"upload_state", "upload_account", "upload_url",
						"bytes_total" })).append(" [");
		boolean i = mBytesTotal != 0L;
		int j = 0;
		if (i != false) {
			boolean k = mBytesUploaded != 0L;
			j = 0;
			if (k != false)
				j = Math.min(
						(int) Math
								.round(100D * (double) ((float) mBytesUploaded / (float) mBytesTotal)),
						100);
		}
		return stringbuilder.append(j).append("%]").toString();
	}
}
