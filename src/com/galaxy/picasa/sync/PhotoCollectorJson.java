/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.JsonReader;

import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Fingerprint;

/**
 * 
 * @author sihai
 *
 */
public class PhotoCollectorJson extends AlbumCollectorJson {

	private static final Map<String, ObjectField> sMediaContentFieldMap;
    private static final Map<String, ObjectField> sPhotoEntryFieldMap;
    private static final Map<String, ObjectField> sShapeFieldMap;

    static {
        sPhotoEntryFieldMap = new HashMap<String, ObjectField>();
        EntrySchema entryschema = PhotoEntry.SCHEMA;
        sPhotoEntryFieldMap.put("gphoto$id", newObjectField(entryschema.getColumn("_id")));
        sPhotoEntryFieldMap.put("gphoto$albumid", newObjectField(entryschema.getColumn("album_id")));
        sPhotoEntryFieldMap.put("gphoto$timestamp", newObjectField(entryschema.getColumn("date_taken")));
        sPhotoEntryFieldMap.put("gphoto$width", newObjectField(entryschema.getColumn("width")));
        sPhotoEntryFieldMap.put("gphoto$height", newObjectField(entryschema.getColumn("height")));
        sPhotoEntryFieldMap.put("gphoto$size", newObjectField(entryschema.getColumn("size")));
        sPhotoEntryFieldMap.put("title", newObjectField(entryschema.getColumn("title")));
        sPhotoEntryFieldMap.put("summary", newObjectField(entryschema.getColumn("summary")));
        sPhotoEntryFieldMap.put("gphoto$commentCount", newObjectField(entryschema.getColumn("comment_count")));
        sPhotoEntryFieldMap.put("gphoto$rotation", newObjectField(entryschema.getColumn("rotation")));
        sPhotoEntryFieldMap.put("published", new PicasaJsonReaderParser.ObjectField("date_published", 10));
        sPhotoEntryFieldMap.put("updated", new PicasaJsonReaderParser.ObjectField("date_updated", 10));
        sPhotoEntryFieldMap.put("app$edited", new PicasaJsonReaderParser.ObjectField("date_edited", 10));
        sPhotoEntryFieldMap.put("link", new PicasaJsonReaderParser.ObjectField(13));
        sPhotoEntryFieldMap.put("gphoto$streamId", new PicasaJsonReaderParser.ObjectField(15));
        Map<String, ObjectField> hashmap = new HashMap<String, ObjectField>();
        sPhotoEntryFieldMap.put("media$group", new PicasaJsonReaderParser.NestedObjectField(hashmap));
        hashmap.put("media$content", new PicasaJsonReaderParser.ObjectField(17));
        hashmap.put("media$thumbnail", new PicasaJsonReaderParser.ObjectField(18));
        hashmap.put("media$keywords", newObjectField(entryschema.getColumn("keywords")));
        Map<String, ObjectField> hashmap1 = new HashMap<String, ObjectField>();
        sPhotoEntryFieldMap.put("gphoto$shapes", new PicasaJsonReaderParser.NestedObjectField(hashmap1));
        hashmap1.put("gphoto$shape", new PicasaJsonReaderParser.ObjectField(16));
        Map<String, ObjectField> hashmap2 = new HashMap<String, ObjectField>();
        sPhotoEntryFieldMap.put("georss$where", new PicasaJsonReaderParser.NestedObjectField(hashmap2));
        hashmap2.put("gml$Point", new PicasaJsonReaderParser.ObjectField(14));
        Map<String, ObjectField> hashmap3 = new HashMap<String, ObjectField>();
        sPhotoEntryFieldMap.put("exif$tags", new PicasaJsonReaderParser.NestedObjectField(hashmap3));
        hashmap3.put("exif$make", newObjectField(entryschema.getColumn("exif_make")));
        hashmap3.put("exif$model", newObjectField(entryschema.getColumn("exif_model")));
        hashmap3.put("exif$exposure", newObjectField(entryschema.getColumn("exif_exposure")));
        hashmap3.put("exif$flash", new PicasaJsonReaderParser.BooleanObjectField("exif_flash", 1, 2));
        hashmap3.put("exif$focallength", newObjectField(entryschema.getColumn("exif_focal_length")));
        hashmap3.put("exif$iso", newObjectField(entryschema.getColumn("exif_iso")));
        hashmap3.put("exif$fstop", newObjectField(entryschema.getColumn("exif_fstop")));
        
        sMediaContentFieldMap = new HashMap<String, ObjectField>();
        sMediaContentFieldMap.put("url", new PicasaJsonReaderParser.ObjectField("url", 0));
        sMediaContentFieldMap.put("type", new PicasaJsonReaderParser.ObjectField("type", 0));
        sShapeFieldMap = new HashMap<String, ObjectField>();
        sShapeFieldMap.put("personid", new PicasaJsonReaderParser.ObjectField("personid", 0));
        sShapeFieldMap.put("name", new PicasaJsonReaderParser.ObjectField("name", 0));
        sShapeFieldMap.put("upperLeft", new PicasaJsonReaderParser.ObjectField("upperLeft", 0));
        sShapeFieldMap.put("lowerRight", new PicasaJsonReaderParser.ObjectField("lowerRight", 0));
    }
    
	public PhotoCollectorJson(PicasaApi.EntryHandler entryhandler) {
		super(entryhandler);
	}

	private int getFaces(JsonReader jsonreader, StringBuilder stringbuilder,
			StringBuilder stringbuilder1, StringBuilder stringbuilder2)
			throws IOException {
		int i = 0;
		jsonreader.beginArray();
		ContentValues contentvalues = new ContentValues();
		do {
			if (!jsonreader.hasNext())
				break;
			contentvalues.clear();
			parseObject(jsonreader, sShapeFieldMap, contentvalues);
			String s = contentvalues.getAsString("name");
			String s1 = contentvalues.getAsString("personid");
			String s2 = contentvalues.getAsString("upperLeft");
			String s3 = contentvalues.getAsString("lowerRight");
			if (!TextUtils.isEmpty(s) && !TextUtils.isEmpty(s1)
					&& !TextUtils.isEmpty(s2) && !TextUtils.isEmpty(s3)) {
				if (++i > 1) {
					stringbuilder.append(',');
					stringbuilder1.append(',');
					stringbuilder2.append(',');
				}
				stringbuilder.append(s);
				stringbuilder1.append(s1);
				stringbuilder2.append(s2).append(' ').append(s3);
			}
		} while (true);
		jsonreader.endArray();
		return i;
	}

	private void parseStreamIds(JsonReader jsonreader,
			ContentValues contentvalues) throws IOException {
		List<String> arraylist = new ArrayList<String>();
		jsonreader.beginArray();
		do {
			if (!jsonreader.hasNext())
				break;
			String s = parseObject(jsonreader, "$t");
			if (s != null)
				if (s.equals("camera_sync_created"))
					contentvalues.put("camera_sync", Integer.valueOf(1));
				else
					arraylist.add(s);
		} while (true);
		jsonreader.endArray();
		Fingerprint fingerprint = Fingerprint.extractFingerprint(arraylist);
		if (fingerprint != null) {
			contentvalues.put("fingerprint", fingerprint.getBytes());
			contentvalues.put("fingerprint_hash",
					Integer.valueOf(fingerprint.hashCode()));
		}
	}

	protected final Map<String, ObjectField> getEntryFieldMap() {
		return sPhotoEntryFieldMap;
	}
	
	protected final void handleComplexValue(JsonReader jsonreader, int i,
			ContentValues contentvalues) throws IOException {
		switch (i) {
		case 13:
			addHtmlPageUrl(jsonreader, contentvalues);
			break;
		case 14:
			String s = parseObject(jsonreader, "gml$pos");
			if (s != null) {
				int j = s.indexOf(' ');
				if (j != -1) {
					contentvalues.put("latitude", s.substring(0, j));
					contentvalues.put("longitude", s.substring(j + 1));
				}
			}
			break;
		case 15:
			parseStreamIds(jsonreader, contentvalues);
			break;
		case 16:
			StringBuilder stringbuilder = new StringBuilder();
			StringBuilder stringbuilder1 = new StringBuilder();
			StringBuilder stringbuilder2 = new StringBuilder();
			if (getFaces(jsonreader, stringbuilder, stringbuilder1,
					stringbuilder2) > 0) {
				contentvalues.put("face_names", stringbuilder.toString());
				contentvalues.put("face_ids", stringbuilder1.toString());
				contentvalues.put("face_rectangles", stringbuilder2.toString());
			}
			break;
		case 17:
			jsonreader.beginArray();
			ContentValues contentvalues1 = new ContentValues();
			do {
				if (!jsonreader.hasNext())
					break;
				contentvalues1.clear();
				parseObject(jsonreader, sMediaContentFieldMap, contentvalues1);
				String s1 = contentvalues1.getAsString("type");
				if (!contentvalues.containsKey("content_url")
						|| s1.startsWith("video/")) {
					contentvalues.put("content_url",
							contentvalues1.getAsString("url"));
					contentvalues.put("content_type", s1);
				}
			} while (true);
			jsonreader.endArray();
			break;
		case 18:
			addThumbnailUrl(jsonreader, contentvalues, "screennail_url");
			break;
		default:
			jsonreader.skipValue();
			break;
		}
	    }
}
