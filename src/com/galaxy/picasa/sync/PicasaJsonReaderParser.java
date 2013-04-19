/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import android.content.ContentValues;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.android.gallery3d.common.Utils;

/**
 * 
 * @author sihai
 * 
 */
public abstract class PicasaJsonReaderParser {

	int entryCount;
	private final PicasaApi.EntryHandler mHandler;
	int totalCount;

	protected PicasaJsonReaderParser(PicasaApi.EntryHandler entryhandler) {
		mHandler = (PicasaApi.EntryHandler) Utils.checkNotNull(entryhandler);
	}
	
	protected abstract Map<String, ObjectField> getEntryFieldMap();

    protected abstract void handleComplexValue(JsonReader jsonreader, int i, ContentValues contentvalues) throws IOException;
	
	protected static ObjectField newObjectField(com.android.gallery3d.common.EntrySchema.ColumnInfo columninfo) {
		byte byte0;
		switch(columninfo.type) {
			case 0:
				byte0 = 0;
				break;
			case 1:
				byte0 = 1;
				break;
			case 2:
				Log.e("gp.PicasaAPI", (new StringBuilder("unexpected column ")).append(columninfo.name).append(" of type ").append(columninfo.type).toString());
			    byte0 = 11;
				break;
			case 3:
				byte0 = 3;
				break;
			case 4:
				byte0 = 4;
				break;
			case 5:
				byte0 = 5;
				break;
			case 6:
				byte0 = 6;
				break;
			default:
				Log.e("gp.PicasaAPI", (new StringBuilder("unexpected column ")).append(columninfo.name).append(" of type ").append(columninfo.type).toString());
			    byte0 = 11;
				break;
		}
		return new ObjectField(columninfo.name, byte0);
    }
	
	private void parseFeed(JsonReader jsonreader) throws IOException {
		String s = null;
		entryCount = 0;
		totalCount = -1;
		jsonreader.beginObject();
		while (jsonreader.hasNext()) {
			String s1 = jsonreader.nextName();
			if (s1.equals("gd$etag"))
				s = jsonreader.nextString();
			else if (s1.equals("openSearch$totalResults"))
				totalCount = Integer.parseInt(parseObject(jsonreader, "$t"));
			else if (s1.equals("entry")) {
				jsonreader.beginArray();
				while (jsonreader.hasNext()) {
					ContentValues contentvalues = new ContentValues();
					parseObject(jsonreader, getEntryFieldMap(), contentvalues);
					mHandler.handleEntry(contentvalues);
					entryCount = 1 + entryCount;
				}
				jsonreader.endArray();
			} else {
				jsonreader.skipValue();
			}
		}
		jsonreader.endObject();
		Log.v("gp.PicasaAPI", (new StringBuilder("   etag: --> ")).append(s)
				.append(",entryCount=").append(entryCount).toString());
	}

	private static void parsePrimitiveValue(JsonReader jsonreader,
			ObjectField objectfield, ContentValues contentvalues)
			throws IOException {
		String s = objectfield.columnName;
		switch (objectfield.type) {
		case 0:
			contentvalues.put(s, jsonreader.nextString());
			break;
		case 1:
			BooleanObjectField booleanobjectfield = (BooleanObjectField) objectfield;
			int j;
			if (Boolean.parseBoolean(jsonreader.nextString()))
				j = booleanobjectfield.onValue;
			else
				j = booleanobjectfield.offValue;
			contentvalues.put(s, Integer.valueOf(j));
			break;

		case 3:
			contentvalues.put(s, Integer.valueOf(jsonreader.nextInt()));
			break;
		case 4:
			contentvalues.put(s, Long.valueOf(jsonreader.nextLong()));
			break;
		case 5:
			contentvalues
					.put(s, Float.valueOf((float) jsonreader.nextDouble()));
			break;
		case 6:
			contentvalues.put(s, Double.valueOf(jsonreader.nextDouble()));
			break;
		case 10:
			String s1;
			s1 = jsonreader.nextString();
			if (!TextUtils.isEmpty(s1)) {
				try {
					Time time = new Time();
					time.parse3339(s1);
					contentvalues.put(s, Long.valueOf(time.toMillis(true)));
				} catch (Exception e) {
					Log.w("gp.PicasaAPI", "parseAtomTimestamp", e);
				}
			}
			break;
		case 2:
		case 7:
		case 8:
		case 9:
		default:
			try {
				throw new RuntimeException((new StringBuilder(
						"unexpected type: ")).append(objectfield.type).append(" for ")
						.append(s).toString());
			} catch (Exception exception1) {
				Log.e("gp.PicasaAPI", "error parsing value", exception1);
			}
			jsonreader.skipValue();
			break;
		}
	}

	public final void parse(InputStream inputstream) throws IOException {
		JsonReader jsonreader;
		jsonreader = new JsonReader(new InputStreamReader(inputstream, "UTF-8"));
		jsonreader.beginObject();
		while (jsonreader.hasNext()) {
			if (jsonreader.nextName().equals("feed")) {
				parseFeed(jsonreader);
			} else {
				jsonreader.skipValue();
			}
		}
		jsonreader.endObject();
	}

	protected final String parseObject(JsonReader jsonreader, String s)
			throws IOException {
		jsonreader.beginObject();
		String s1;
		do {
			boolean flag = jsonreader.hasNext();
			s1 = null;
			if (!flag)
				break;
			if (s.equals(jsonreader.nextName())) {
				if (jsonreader.peek() == JsonToken.BEGIN_OBJECT)
					s1 = parseObject(jsonreader, "$t");
				else
					s1 = jsonreader.nextString();
				for (; jsonreader.hasNext(); jsonreader.skipValue())
					jsonreader.nextName();

				break;
			}
			jsonreader.skipValue();
		} while (true);
		jsonreader.endObject();
		return s1;
	}

	protected final void parseObject(JsonReader jsonreader, Map map,
			ContentValues contentvalues) throws IOException {
		jsonreader.beginObject();
		do
			if (jsonreader.hasNext()) {
				ObjectField objectfield = (ObjectField) map.get(jsonreader
						.nextName());
				if (objectfield != null) {
					if (objectfield.type >= 12)
						switch (objectfield.type) {
						default:
							handleComplexValue(jsonreader, objectfield.type,
									contentvalues);
							break;

						case 12: // '\f'
							parseObject(jsonreader,
									((NestedObjectField) objectfield).map,
									contentvalues);
							break;
						}
					else if (jsonreader.peek() == JsonToken.BEGIN_OBJECT) {
						jsonreader.beginObject();
						if (jsonreader.hasNext()) {
							Utils.assertTrue(jsonreader.nextName().equals("$t"));
							parsePrimitiveValue(jsonreader, objectfield,
									contentvalues);
							boolean flag;
							if (!jsonreader.hasNext())
								flag = true;
							else
								flag = false;
							Utils.assertTrue(flag);
						}
						jsonreader.endObject();
					} else {
						parsePrimitiveValue(jsonreader, objectfield,
								contentvalues);
					}
				} else {
					jsonreader.skipValue();
				}
			} else {
				jsonreader.endObject();
				return;
			}
		while (true);
	}
	
	protected static final class BooleanObjectField extends ObjectField {

		final int offValue = 2;
		final int onValue = 1;

		BooleanObjectField(String s, int i, int j) {
			super(s, 1);
		}
	}

	protected static final class NestedObjectField extends ObjectField {

		final Map map;

		NestedObjectField(Map map1) {
			super(12);
			map = map1;
		}
	}

	protected static class ObjectField {

		final String columnName;
		final int type;

		ObjectField(int i) {
			columnName = null;
			type = i;
			boolean flag;
			if (i > 10)
				flag = true;
			else
				flag = false;
			Utils.assertTrue(flag);
		}

		ObjectField(String s, int i) {
			columnName = s;
			type = i;
			boolean flag;
			if (i <= 10)
				flag = true;
			else
				flag = false;
			Utils.assertTrue(flag);
		}
	}
}
