/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.util.JsonReader;

import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;

/**
 * 
 * @author sihai
 *
 */
public class AlbumCollectorJson extends PicasaJsonReaderParser {

	private static final Map<String, ObjectField> sAlbumEntryFieldMap;
    private static final Map<String, ObjectField> sLinkFieldMap;

    static {
        sAlbumEntryFieldMap = new HashMap<String, ObjectField>();
        EntrySchema entryschema = AlbumEntry.SCHEMA;
        sAlbumEntryFieldMap.put("gphoto$id", newObjectField(entryschema.getColumn("_id")));
        sAlbumEntryFieldMap.put("gphoto$albumType", newObjectField(entryschema.getColumn("album_type")));
        sAlbumEntryFieldMap.put("gphoto$user", newObjectField(entryschema.getColumn("user")));
        sAlbumEntryFieldMap.put("gphoto$bytesUsed", newObjectField(entryschema.getColumn("bytes_used")));
        sAlbumEntryFieldMap.put("title", newObjectField(entryschema.getColumn("title")));
        sAlbumEntryFieldMap.put("summary", newObjectField(entryschema.getColumn("summary")));
        sAlbumEntryFieldMap.put("gphoto$numphotos", newObjectField(entryschema.getColumn("num_photos")));
        sAlbumEntryFieldMap.put("published", new PicasaJsonReaderParser.ObjectField("date_published", 10));
        sAlbumEntryFieldMap.put("updated", new PicasaJsonReaderParser.ObjectField("date_updated", 10));
        sAlbumEntryFieldMap.put("app$edited", new PicasaJsonReaderParser.ObjectField("date_edited", 10));
        sAlbumEntryFieldMap.put("link", new PicasaJsonReaderParser.ObjectField(13));
        HashMap<String, ObjectField> hashmap = new HashMap<String, ObjectField>();
        sAlbumEntryFieldMap.put("media$group", new PicasaJsonReaderParser.NestedObjectField(hashmap));
        hashmap.put("media$thumbnail", new PicasaJsonReaderParser.ObjectField(14));
        sLinkFieldMap = new HashMap<String, ObjectField>();
        sLinkFieldMap.put("rel", new PicasaJsonReaderParser.ObjectField("rel", 0));
        sLinkFieldMap.put("type", new PicasaJsonReaderParser.ObjectField("type", 0));
        sLinkFieldMap.put("href", new PicasaJsonReaderParser.ObjectField("href", 0));
    }
    
	public AlbumCollectorJson(PicasaApi.EntryHandler entryhandler) {
		super(entryhandler);
	}
    
	protected final void addHtmlPageUrl(JsonReader jsonreader,
			ContentValues contentvalues) throws IOException {
		jsonreader.beginArray();
		ContentValues contentvalues1 = new ContentValues();
		do {
			if (!jsonreader.hasNext())
				break;
			contentvalues1.clear();
			parseObject(jsonreader, sLinkFieldMap, contentvalues1);
			String s = contentvalues1.getAsString("rel");
			String s1 = contentvalues1.getAsString("type");
			if (!Utils.equals(s, "alternate") || !Utils.equals(s1, "text/html"))
				continue;
			contentvalues.put("html_page_url",
					contentvalues1.getAsString("href"));
			for (; jsonreader.hasNext(); jsonreader.skipValue())
				;
			break;
		} while (true);
		jsonreader.endArray();
	}

	protected final void addThumbnailUrl(JsonReader jsonreader,
			ContentValues contentvalues, String s) throws IOException {
		jsonreader.beginArray();
		do {
			if (!jsonreader.hasNext())
				break;
			String s1 = parseObject(jsonreader, "url");
			if (s1 == null)
				continue;
			contentvalues.put(s, s1);
			for (; jsonreader.hasNext(); jsonreader.skipValue())
				;
			break;
		} while (true);
		jsonreader.endArray();
	}

	protected Map<String, ObjectField> getEntryFieldMap() {
		return sAlbumEntryFieldMap;
	}

	protected void handleComplexValue(JsonReader jsonreader, int i,
			ContentValues contentvalues) throws IOException {
		if (13 == i) {
			addHtmlPageUrl(jsonreader, contentvalues);
		} else if (14 == i) {
			addThumbnailUrl(jsonreader, contentvalues, "thumbnail_url");
		} else {
			jsonreader.skipValue();
		}

	}
}
