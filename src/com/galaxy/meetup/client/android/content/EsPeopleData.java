/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.util.SparseArray;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.GetBlockedPeopleOperation;
import com.galaxy.meetup.client.android.api.GetContactInfoOperation;
import com.galaxy.meetup.client.android.api.GetProfileOperation;
import com.galaxy.meetup.client.android.api.GetSuggestedPeopleOperation;
import com.galaxy.meetup.client.android.api.GetVolumeControlsOperation;
import com.galaxy.meetup.client.android.api.LoadSocialNetworkOperation;
import com.galaxy.meetup.client.android.network.ApiaryBatchOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.HttpTransactionMetrics;
import com.galaxy.meetup.client.android.service.AndroidContactsSync;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.ContactTag;
import com.galaxy.meetup.server.client.domain.DataCircleId;
import com.galaxy.meetup.server.client.domain.DataCircleMemberId;
import com.galaxy.meetup.server.client.domain.DataCircleMemberPropertiesAddress;
import com.galaxy.meetup.server.client.domain.DataCirclePerson;
import com.galaxy.meetup.server.client.domain.DataEmail;
import com.galaxy.meetup.server.client.domain.DataMembership;
import com.galaxy.meetup.server.client.domain.DataPhone;
import com.galaxy.meetup.server.client.domain.DataPlusOne;
import com.galaxy.meetup.server.client.domain.DataSuggestedCelebrityCategory;
import com.galaxy.meetup.server.client.domain.DataSystemGroups;
import com.galaxy.meetup.server.client.domain.DataViewerCircles;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.Page;
import com.galaxy.meetup.server.client.domain.RenderedSharingRosters;
import com.galaxy.meetup.server.client.domain.SharingRoster;
import com.galaxy.meetup.server.client.domain.SharingTarget;
import com.galaxy.meetup.server.client.domain.SharingTargetId;
import com.galaxy.meetup.server.client.domain.SimpleProfile;
import com.galaxy.meetup.server.client.domain.VolumeControlMap;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EsPeopleData {

	private static final String CIRCLES_PROJECTION[] = {
        "circle_id", "circle_name", "type", "contact_count", "semantic_hints", "volume"
    };
    private static final String CONTACT_PROJECTION[] = {
        "profile_state", "name", "profile_type"
    };
    private static final String PROFILE_COLUMNS[] = {
        "profile_state", "name", "group_concat(link_circle_id, '|') AS packed_circle_ids", "blocked", "last_updated_time", "contact_update_time", "contact_proto", "profile_update_time", "profile_proto"
    };
    private static final String SUGGESTED_PEOPLE_COLUMNS[] = {
        "suggested_person_id", "dismissed", "sort_order"
    };
    private static final String USERS_PROJECTION[] = {
        "gaia_id", "name", "avatar", "in_my_circles"
    };
    private static final Object sCircleSyncLock = new Object();
    public static Handler sHandler;
    private static volatile CountDownLatch sInitialSyncLatch = new CountDownLatch(1);
    private static final Object sMyProfileSyncLock = new Object();
    private static final Object sPeopleSyncLock = new Object();
    private static Map sProfileFetchLocks = new HashMap();
    private static final Object sSuggestedPeopleSyncLock = new Object();

    
    public static int getMembershipChangeMessageId(List list, List list1) {
        boolean flag = true;
        boolean flag1;
        int i;
        if(list != null && !list.isEmpty())
            flag1 = flag;
        else
            flag1 = false;
        if(list1 == null || list1.isEmpty())
            flag = false;
        if(flag1 && !flag)
            i = R.string.add_to_circle_operation_pending;
        else
        if(flag && !flag1)
            i = R.string.remove_from_circle_operation_pending;
        else
            i = R.string.moving_between_circles_operation_pending;
        return i;
    }
    
	public static CircleData getCircleData(Context context, EsAccount esaccount, int type) {
		
		Cursor cursor = null;
		try {
			Uri.Builder builder = EsProvider.CIRCLES_URI.buildUpon();
			EsProvider.appendAccountParameter(builder, esaccount);
	        builder.appendQueryParameter("limit", "1");
	        cursor = context.getContentResolver().query(builder.build(), new String[]{
	            "circle_id", "circle_name", "contact_count"
	        }, (new StringBuilder("type = ")).append(type).toString(), null, null);
	        if(null == cursor) {
	        	return null;
	        }
	        if(!cursor.moveToFirst()) {
	        	return null;
	        }
	        return new CircleData(cursor.getString(0), type, cursor.getString(1), cursor.getInt(2));
		} finally {
			if(null != cursor) {
				cursor.close();
			}
		}
    }
	
	public static String getCircleId(Context context, EsAccount esaccount, String s) {
        SQLiteDatabase sqlitedatabase;
        String s1 = null;
        if(TextUtils.isEmpty(s))
            return null;
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        try {
	        return DatabaseUtils.stringForQuery(sqlitedatabase, "SELECT circle_id FROM circles WHERE circle_name=?", new String[] {
	            s
	        });
        } catch (SQLiteDoneException sqlitedoneexception) {
        	return null;
        }
    }

    public static String getCircleId(String s)
    {
        if(!s.startsWith("f."))
            s = (new StringBuilder("f.")).append(s).toString();
        return s;
    }
	
    public static String getFocusCircleId(String s)
    {
        if(s.startsWith("f."))
            s = s.substring(2);
        return s;
    }
    
    public static Cursor getCircles(Context context, EsAccount esaccount, int i, String as[], String s, int j) {
    	
        if(queryCircleSyncTimestamp(context, esaccount) == -1L)
        	try {
        		syncCircles(context, esaccount, true);
        	} catch (IOException e) {
        		Log.e("EsPeopleData", "Error syncing circles", e);
        		return null;
        	}
        
        android.net.Uri.Builder builder;
        Cursor cursor;
        ArrayList arraylist;
        StringBuilder stringbuilder;
        int k;
        int l;
        String s1 = null;
        switch(i) {
        case -1:
        	s1 = "0";
        	break;
        case 0:
        	
        	break;
        case 1:
        	s1 = "semantic_hints&1=0 AND (type!=10 OR contact_count>0)";
        	break;
        case 2:
        	s1 = "semantic_hints&2=0 AND type!=10 AND type!=100";
        	break;
        case 3:
        	s1 = "type IN (1,-1)";
        	break;
        case 4:
        	s1 = "type=1 OR circle_id='v.whatshot'";
        	break;
        case 5:
        	s1 = "semantic_hints&8=0";
        	break;
        case 6:
        	s1 = "semantic_hints&8=0 AND type=1";
        	break;
        case 7:
        	s1 = "semantic_hints&64!=0 AND type=1";
        	break;
        case 8:
        	arraylist = EsAccountsData.getStreamViewList(context, esaccount);
            stringbuilder = new StringBuilder();
            stringbuilder.append("circle_id IN (");
            k = arraylist.size();
            for(l = 0; l < k; l++)
            {
                if(l != 0)
                    stringbuilder.append(',');
                DatabaseUtils.appendEscapedSQLString(stringbuilder, (String)arraylist.get(l));
            }

            stringbuilder.append(')');
            s1 = stringbuilder.toString();
        	break;
        case 9:
        	s1 = "semantic_hints&8=0 AND type NOT IN (9,8)";
        	break;
        case 10:
        	s1 = "semantic_hints&8=0";
        	break;
        case 11:
        	s1 = "semantic_hints&8=0";
        	break;
        case 12:
        	s1 = "semantic_hints&1=0";
        	break;
        case 13:
        	s1 = "semantic_hints&8=0 AND type IN (9,5,8)";
        	break;
        default:
        	break;
        }
        
        if(s != null)
        {
            String s2 = (new StringBuilder("(circle_name LIKE ")).append(DatabaseUtils.sqlEscapeString((new StringBuilder()).append(s).append('%').toString())).append(")").toString();
            if(s1 == null)
                s1 = s2;
            else
                s1 = (new StringBuilder()).append(s1).append(" AND ").append(s2).toString();
        }
        builder = EsProvider.CIRCLES_URI.buildUpon();
        EsProvider.appendAccountParameter(builder, esaccount);
        if(j != 0)
            builder.appendQueryParameter("limit", String.valueOf(j));
        return context.getContentResolver().query(builder.build(), as, s1, null, "show_order ASC, sort_key");
        
    }
	
	public static String getStringForEmailType(Context context, ContactTag contacttag) {
        String s = null;
        if(null == contacttag) {
        	return null;
        }
        
        if("HOME".equals(contacttag.tag))
            s = context.getString(R.string.profile_item_email_home);
        else if("WORK".equals(contacttag.tag)) {
            s = context.getString(R.string.profile_item_email_work);
        } else {
            boolean flag = "OTHER".equals(contacttag.tag);
            s = null;
            if(!flag) {
                boolean flag1 = "CUSTOM".equals(contacttag.tag);
                s = null;
                if(flag1)
                    s = contacttag.customTag;
            }
        }
        return s;
    }
	
	public static String getStringForPhoneType(Context context, ContactTag contacttag) {
        String s = null;
        if(null == contacttag) {
        	return null;
        }

        if("HOME".equals(contacttag.tag))
            s = context.getString(R.string.profile_item_phone_home);
        else if("WORK".equals(contacttag.tag)) {
            s = context.getString(R.string.profile_item_phone_work);
        } else {
            boolean flag = "OTHER".equals(contacttag.tag);
            s = null;
            if(!flag)
                if("HOME_FAX".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_home_fax);
                else
                if("WORK_FAX".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_work_fax);
                else
                if("MOBILE".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_mobile);
                else
                if("PAGER".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_pager);
                else
                if("OTHER_FAX".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_other_fax);
                else
                if("COMPANY_MAIN".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_company_main);
                else
                if("ASSISTANT".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_assistant);
                else
                if("CAR".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_car);
                else
                if("RADIO".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_radio);
                else
                if("ISDN".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_isdn);
                else
                if("CALLBACK".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_callback);
                else
                if("TELEX".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_telex);
                else
                if("TTY_TDD".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_tty_tdd);
                else
                if("WORK_MOBILE".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_work_mobile);
                else
                if("WORK_PAGER".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_work_pager);
                else
                if("MAIN".equals(contacttag.tag))
                    s = context.getString(R.string.profile_item_phone_main);
                else
                if("GRAND_CENTRAL".equals(contacttag.tag))
                {
                    s = context.getString(R.string.profile_item_phone_google_voice);
                } else
                {
                    boolean flag1 = "CUSTOM".equals(contacttag.tag);
                    s = null;
                    if(flag1)
                        s = contacttag.customTag;
                }
        }
        return s;
    }

    public static String getStringForPhoneType(Context context, String s) {
        if(TextUtils.isEmpty(s)) 
        	return null; 
        
        if(s.equals("1"))
            s = context.getString(R.string.profile_item_phone_home);
        else
        if(s.equals("2"))
            s = context.getString(R.string.profile_item_phone_work);
        else
        if(s.equals("3"))
            s = null;
        else
        if(s.equals("4"))
            s = context.getString(R.string.profile_item_phone_home_fax);
        else
        if(s.equals("5"))
            s = context.getString(R.string.profile_item_phone_work_fax);
        else
        if(s.equals("6"))
            s = context.getString(R.string.profile_item_phone_mobile);
        else
        if(s.equals("7"))
            s = context.getString(R.string.profile_item_phone_pager);
        else
        if(s.equals("8"))
            s = context.getString(R.string.profile_item_phone_other_fax);
        else
        if(s.equals("9"))
            s = context.getString(R.string.profile_item_phone_company_main);
        else
        if(s.equals("10"))
            s = context.getString(R.string.profile_item_phone_assistant);
        else
        if(s.equals("11"))
            s = context.getString(R.string.profile_item_phone_car);
        else
        if(s.equals("12"))
            s = context.getString(R.string.profile_item_phone_radio);
        else
        if(s.equals("13"))
            s = context.getString(R.string.profile_item_phone_isdn);
        else
        if(s.equals("14"))
            s = context.getString(R.string.profile_item_phone_callback);
        else
        if(s.equals("15"))
            s = context.getString(R.string.profile_item_phone_telex);
        else
        if(s.equals("16"))
            s = context.getString(R.string.profile_item_phone_tty_tdd);
        else
        if(s.equals("17"))
            s = context.getString(R.string.profile_item_phone_work_mobile);
        else
        if(s.equals("18"))
            s = context.getString(R.string.profile_item_phone_work_pager);
        else
        if(s.equals("19"))
            s = context.getString(R.string.profile_item_phone_main);
        else
        if(s.equals("20"))
            s = context.getString(R.string.profile_item_phone_google_voice);
        return s;
    }

    public static String getStringForPlusPagePhoneType(Context context, ContactTag contacttag) {
        String s = null;
        if(null == contacttag)
        	return null;
        
        if("HOME".equals(contacttag.tag) || "WORK".equals(contacttag.tag) || "OTHER".equals(contacttag.tag))
            s = context.getString(R.string.profile_item_phone);
        else
        if("HOME_FAX".equals(contacttag.tag) || "WORK_FAX".equals(contacttag.tag) || "OTHER_FAX".equals(contacttag.tag))
            s = context.getString(R.string.profile_item_phone_fax);
        else
        if("MOBILE".equals(contacttag.tag))
            s = context.getString(R.string.profile_item_phone_mobile);
        else
        if("PAGER".equals(contacttag.tag))
        {
            s = context.getString(R.string.profile_item_phone_pager);
        } else
        {
            boolean flag = "CUSTOM".equals(contacttag.tag);
            s = null;
            if(flag)
                s = contacttag.customTag;
        }
        return s;
    }

    public static Cursor getSuggestedPeople(Context context, EsAccount esaccount, String as[], boolean flag, boolean flag1)
    {
        boolean flag2 = ensureSuggestedPeopleSynced(context, esaccount, 0x2bf20L, flag);
        Cursor cursor = null;
        if(flag2)
        {
            Uri uri = EsProvider.appendAccountParameter(EsProvider.SUGGESTED_PEOPLE_URI, esaccount);
            cursor = context.getContentResolver().query(uri, as, null, null, null);
        }
        return cursor;
    }
    
    public static Cursor getBlockedPeople(final Context context, EsAccount esaccount, String as[], List arraylist) {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        Uri uri = EsProvider.appendAccountParameter(EsProvider.CONTACTS_URI, esaccount);
        String as1[] = (String[])arraylist.toArray(new String[arraylist.size()]);
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("blocked=1");
        if(as1.length > 0)
        {
            stringbuilder.append(" OR person_id IN (");
            for(int i = 0; i < as1.length; i++)
                stringbuilder.append("?,");

            stringbuilder.setLength(-1 + stringbuilder.length());
            stringbuilder.append(")");
        }
        final EsAccount account = esaccount;
        long l = queryBlockedPeopleSyncTimestamp(context, esaccount);
        Cursor cursor = null;
        if(l != -1L) {
        	cursor = context.getContentResolver().query(uri, as, stringbuilder.toString(), as1, null);
            if(System.currentTimeMillis() - l > 10000L)
            {
                ContentValues contentvalues1 = new ContentValues();
                contentvalues1.put("blocked_people_sync_time", Long.valueOf(System.currentTimeMillis()));
                sqlitedatabase.update("account_status", contentvalues1, null, null);
                EsService.postOnServiceThread(new Runnable() {

                    public final void run()
                    {
                        EsService.syncBlockedPeople(context, account);
                    }

                });
            }
            return cursor;
        } else { 
            ContentValues contentvalues = new ContentValues();
            contentvalues.put("blocked_people_sync_time", Long.valueOf(System.currentTimeMillis()));
            sqlitedatabase.update("account_status", contentvalues, null, null);
            GetBlockedPeopleOperation getblockedpeopleoperation = new GetBlockedPeopleOperation(context, esaccount, null, null);
            getblockedpeopleoperation.start();
            if(getblockedpeopleoperation.hasError())
            {
                getblockedpeopleoperation.logError("EsPeopleData");
                contentvalues.put("blocked_people_sync_time", Integer.valueOf(-1));
                sqlitedatabase.update("account_status", contentvalues, null, null);
                cursor = null;
            } else
            {
                cursor = context.getContentResolver().query(uri, as, stringbuilder.toString(), as1, null);
            }
            return cursor;
        }
    }

    public static String getUserName(Context context, EsAccount esaccount, String s) {
    	
    	try {
    		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
    		return DatabaseUtils.stringForQuery(sqlitedatabase, "SELECT name  FROM contacts  WHERE gaia_id = ?", new String[] {
    				s
    		});
    	} catch (SQLiteDoneException sqlitedoneexception) {
    		return null;
    	}
    }

    public static boolean hasCircleActionData(Context context, EsAccount esaccount, String s) {
        boolean flag = true;
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        String as[] = new String[1];
        as[0] = s;
        if(DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM circle_action WHERE notification_id=?", as) == 0L)
            flag = false;
        return flag;
    }
    
    public static String getDefaultCircleId(Context context, Cursor cursor, boolean flag) {
    	if(null == cursor) {
    		return null;
    	}
    	
    	int i = cursor.getColumnIndex("semantic_hints");
        int j = cursor.getColumnIndex("circle_name");
        int k = cursor.getColumnIndex("circle_id");
        int l;
        String s;
        if(flag)
            l = R.string.friends_circle_name;
        else
            l = R.string.following_circle_name;
        s = context.getString(l);
        if(cursor.moveToFirst())
            do
            {
                boolean flag1;
                if((0x40 & cursor.getInt(i)) != 0)
                    flag1 = true;
                else
                    flag1 = false;
                if(flag == flag1 && s.equalsIgnoreCase(cursor.getString(j)))
                	return cursor.getString(k);
            } while(cursor.moveToNext());
        return null;
    }

    public static boolean hasPublicCircle(Context context, EsAccount esaccount) {
        boolean flag = true;
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        if(queryNumEntries(sqlitedatabase, "circles", "type=9") != 0) {
        	return true;
        }
        if(queryCircleSyncTimestamp(context, esaccount) != -1L) {
        	return true;
        }
        try {
	        syncCircles(context, esaccount, false);
	        if(queryNumEntries(sqlitedatabase, "circles", "type=9") == 0)
	            flag = false;
        } catch (IOException e) {
        	Log.e("EsPeopleData", "Error syncing circles", e);
            flag = false;
        }
        return flag;
        
    }
    
    public static void insertSuggestedPeople(Context context, EsAccount esaccount, List list) {
    	SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
    	try {
	        sqlitedatabase.beginTransaction();
	        replaceSuggestionsInTransaction(sqlitedatabase, "#", null, "0", list);
	        ContentValues contentvalues = new ContentValues();
	        contentvalues.clear();
	        contentvalues.put("suggested_people_sync_time", Long.valueOf(System.currentTimeMillis()));
	        sqlitedatabase.update("account_status", contentvalues, null, null);
	        sqlitedatabase.setTransactionSuccessful();
	        sqlitedatabase.endTransaction();
	        context.getContentResolver().notifyChange(EsProvider.SUGGESTED_PEOPLE_URI, null);
    	} finally {
    		sqlitedatabase.endTransaction();
    	}
    }
    
    public static String getStringForAddress(Context context, ContactTag contacttag) {
    	
    	if(null == contacttag) {
    		return null;
    	}
    	String s = null;
    	if("HOME".equals(contacttag.tag))
            s = context.getString(R.string.profile_item_address_home);
        else
        if("WORK".equals(contacttag.tag))
        {
            s = context.getString(R.string.profile_item_address_work);
        } else
        {
            boolean flag = "OTHER".equals(contacttag.tag);
            s = null;
            if(!flag)
            {
                boolean flag1 = "CUSTOM".equals(contacttag.tag);
                s = null;
                if(flag1)
                    s = contacttag.customTag;
            }
        }
    	return s;
    }
    
    public static void insertCelebritySuggestions(Context context, EsAccount esaccount, List list) {
    	SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        int i = list.size();
	        String s = null;
	        if(i > 0)
	        {
	            StringBuilder stringbuilder = new StringBuilder();
	            stringbuilder.append("category NOT IN (");
	            DatabaseUtils.appendEscapedSQLString(stringbuilder, "#");
	            for(int j = 0; j < i; j++)
	            {
	                stringbuilder.append(",");
	                DatabaseUtils.appendEscapedSQLString(stringbuilder, ((DataSuggestedCelebrityCategory)list.get(j)).category);
	            }
	
	            stringbuilder.append(")");
	            s = stringbuilder.toString();
	        }
	        sqlitedatabase.delete("suggested_people", s, null);
	        for(int k = 0; k < i; k++)
	        {
	            DataSuggestedCelebrityCategory datasuggestedcelebritycategory = (DataSuggestedCelebrityCategory)list.get(k);
	            replaceSuggestionsInTransaction(sqlitedatabase, datasuggestedcelebritycategory.category, datasuggestedcelebritycategory.categoryName, Integer.toString(k + 1), datasuggestedcelebritycategory.celebrity);
	        }
	
	        ContentValues contentvalues = new ContentValues();
	        contentvalues.clear();
	        contentvalues.put("suggested_people_sync_time", Long.valueOf(System.currentTimeMillis()));
	        sqlitedatabase.update("account_status", contentvalues, null, null);
	        sqlitedatabase.setTransactionSuccessful();
	        sqlitedatabase.endTransaction();
	        context.getContentResolver().notifyChange(EsProvider.SUGGESTED_PEOPLE_URI, null);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }
    
    public static void insertSelf(Context context, EsAccount esaccount, String s) {
        if(EsLog.isLoggable("EsPeopleData", 3))
            Log.d("EsPeopleData", (new StringBuilder(">>>> insertSelf: ")).append(esaccount.getDisplayName()).toString());
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        if(TextUtils.isEmpty(s))
            s = null;
        try {
        	sqlitedatabase.beginTransaction();
        	replaceUserInTransaction(sqlitedatabase, esaccount.getGaiaId(), esaccount.getDisplayName(), s);
        	sqlitedatabase.setTransactionSuccessful();
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }
    
    
    public static void insertBlockedPeople(Context context, EsAccount esaccount, List list)
    {
        HashSet hashset = new HashSet();
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        Cursor cursor = null;
        ContentValues contentvalues = new ContentValues();
        String as[] = new String[1];
        try {
        	sqlitedatabase.beginTransaction();
        	cursor = sqlitedatabase.query("contacts", new String[] {
                    "person_id"
                }, "blocked=1", null, null, null, null);
            while(cursor.moveToNext()) 
                hashset.add(cursor.getString(0));
        
	        int i;
	        int j;
	        if(list == null)
	            i = 0;
	        else
	            i = list.size();
	        j = 0;
	        for(; j < i; j++)
	            hashset.remove(getPersonId(((DataCirclePerson)list.get(j)).memberId));
	
	        if(!hashset.isEmpty())
	        {
	            contentvalues.put("blocked", Integer.valueOf(0));
	            contentvalues.put("last_updated_time", Integer.valueOf(0));
	            for(Iterator iterator = hashset.iterator(); iterator.hasNext(); sqlitedatabase.update("contacts", contentvalues, "person_id=?", as))
	                as[0] = (String)iterator.next();
	
	        }
	        
	        for(int k = 0; k < i; k++) {
	        	DataCirclePerson datacircleperson;
	            String s;
	            byte byte0;
	            datacircleperson = (DataCirclePerson)list.get(k);
	            s = getPersonId(((DataCirclePerson)list.get(k)).memberId);
	            if(!"PLUSPAGE".equals(datacircleperson.memberProperties.profileType)) {
	            	byte0 = 1;
	            } else {
	            	byte0 = 2;
	            }
	            String s1 = datacircleperson.memberProperties.displayName;
	            long l = getLastUpdatedTime(datacircleperson);
	            ContentValues contentvalues1 = new ContentValues();
	            contentvalues1.put("name", s1);
	            contentvalues1.put("last_updated_time", Long.valueOf(l));
	            contentvalues1.put("in_my_circles", Integer.valueOf(0));
	            contentvalues1.put("blocked", Integer.valueOf(1));
	            contentvalues1.put("profile_type", Integer.valueOf(byte0));
	            contentvalues1.put("avatar", EsAvatarData.compressAvatarUrl(null));
	            String s2 = extractGaiaId(s);
	            contentvalues1.put("profile_state", Integer.valueOf(5));
	            if(sqlitedatabase.update("contacts", contentvalues1, "person_id=?", new String[] {s}) == 0)
	            {
	                contentvalues1.put("person_id", s);
	                contentvalues1.put("gaia_id", s2);
	                sqlitedatabase.insert("contacts", null, contentvalues1);
	            }
	            contentvalues1.clear();
	            contentvalues1.put("contact_update_time", Long.valueOf(System.currentTimeMillis()));
	            contentvalues1.put("contact_proto", serializeContactInfo(null));
	            if(sqlitedatabase.update("profiles", contentvalues1, "profile_person_id=?", new String[] {s}) == 0)
	            {
	                contentvalues1.put("profile_person_id", s);
	                sqlitedatabase.insert("profiles", null, contentvalues1);
	            }
	            as[0] = s;
	            sqlitedatabase.delete("circle_contact", "link_person_id=?", as);
	            sqlitedatabase.delete("contact_search", "search_person_id=?", as);
	        }
	        
	        contentvalues.clear();
	        contentvalues.put("contact_count", Integer.valueOf(i));
	        as[0] = "15";
	        sqlitedatabase.update("circles", contentvalues, "circle_id=?", as);
	        contentvalues.clear();
	        contentvalues.put("blocked_people_sync_time", Long.valueOf(System.currentTimeMillis()));
	        sqlitedatabase.update("account_status", contentvalues, null, null);
	        sqlitedatabase.setTransactionSuccessful();
	        sqlitedatabase.endTransaction();
	        ContentResolver contentresolver = context.getContentResolver();
	        contentresolver.notifyChange(EsProvider.CONTACTS_URI, null);
	        contentresolver.notifyChange(EsProvider.CIRCLES_URI, null);
        } finally {
        	sqlitedatabase.endTransaction();
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
    }
    
    public static boolean replaceUserInTransaction(SQLiteDatabase sqlitedatabase, String s, String s1, String s2) {
    	
    	boolean flag = false;
        if(s1 != null && s != null && !"0".equals(s)) {
        	Cursor cursor = null;
        	try {
	        	cursor = sqlitedatabase.query("contacts", USERS_PROJECTION, "gaia_id = ?", new String[] {
	                    s
	                }, null, null, null);
	        	if(cursor.moveToNext()) {
	        		UserInfo userinfo1 = new UserInfo();
	                userinfo1.name = cursor.getString(1);
	                userinfo1.avatarUrl = cursor.getString(2);
	                if(cursor.getInt(3) != 0) {
	                	userinfo1.inMyCircles = true;
		                flag = replaceUserInTransaction(sqlitedatabase, s, s1, s2, userinfo1);
	                }
	        	}
        	} finally {
        		if(null != cursor) {
        			cursor.close();
        		}
        	}
        } else {
        	if(EsLog.isLoggable("EsPeopleData", 3))
                Log.d("EsPeopleData", (new StringBuilder(">>>>> Person id: ")).append(s).append(", name: ").append(s1).append("; *** Skip. No gaia id or name").toString());
            flag = false;
        }
        return flag;
        
    }
    
    public static ProfileAndContactData getProfileAndContactData(Context context, EsAccount esaccount, String s, boolean flag) {
        Object obj;
        ProfileAndContactData profileandcontactdata = new ProfileAndContactData();
        String s1;
        synchronized(sProfileFetchLocks)
        {
            obj = sProfileFetchLocks.get(s);
            if(obj == null)
            {
                obj = new Object();
                sProfileFetchLocks.put(s, obj);
            }
        }
        synchronized(obj) {
        	loadProfileAndContactDataFromDatabase(context, esaccount, s, profileandcontactdata);
        	if(!(profileandcontactdata.profileState != 0 && (!flag || profileandcontactdata.profile != null))) {
        		s1 = extractGaiaId(s);
        		if(null != s1) {
			        try
			        {
			            loadProfileFromServer(context, esaccount, s1);
			            loadProfileAndContactDataFromDatabase(context, esaccount, s, profileandcontactdata);
			        }
			        catch(IOException ioexception) { }
        		}
        	}
        }
        synchronized(sProfileFetchLocks)
        {
            sProfileFetchLocks.remove(s);
        }
        return profileandcontactdata;
    }
    
	public static void refreshProfile(Context context, EsAccount esaccount,
			String s) throws IOException {
		String s1 = extractGaiaId(s);
		if (s1 != null)
			if (esaccount.isMyGaiaId(s1))
				syncMyProfile(context, esaccount,
						new EsSyncAdapterService.SyncState(), null, true);
			else
				loadProfileFromServer(context, esaccount, s1);
	}
	
	public static void syncMyProfile(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, HttpOperation.OperationListener operationlistener, boolean flag) throws IOException {
		synchronized(sMyProfileSyncLock) {
			SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
			if(!flag) {
				long l2;
		        String as[] = new String[1];
		        as[0] = esaccount.getPersonId();
		        l2 = DatabaseUtils.longForQuery(sqlitedatabase, "SELECT contact_update_time  FROM profiles  WHERE profile_person_id=?", as);
		        long l1 = l2;
		        if(System.currentTimeMillis() - l1 < 60000L) {
		        	return;
		        }
			}
			
			String s;
	        String s1;
	        long l;
	        s = esaccount.getGaiaId();
	        syncstate.onStart("MyProfile");
	        GetContactInfoOperation getcontactinfooperation = new GetContactInfoOperation(context, esaccount, s, null, operationlistener);
	        getcontactinfooperation.start();
	        getcontactinfooperation.logAndThrowExceptionIfFailed("EsPeopleData");
	        s1 = esaccount.getPersonId();
	        l = getLastUpdatedTime(getcontactinfooperation.getPerson());
	        if(!flag && !isContactModified(sqlitedatabase, s1, l)) {
	        	syncstate.onFinish();
	        	return;
	        }
	        
	        GetProfileOperation getprofileoperation;
	        getprofileoperation = new GetProfileOperation(context, esaccount, s, false, null, null);
	        getprofileoperation.start();
	        getprofileoperation.logAndThrowExceptionIfFailed("EsPeopleData");
	        try {
		        sqlitedatabase.beginTransaction();
		        replaceProfileProtoInTransaction(sqlitedatabase, s1, getprofileoperation.getProfile());
		        ContentValues contentvalues = new ContentValues();
		        contentvalues.put("last_updated_time", Long.valueOf(l));
		        sqlitedatabase.update("contacts", contentvalues, "person_id=?", new String[] {
		            s1
		        });
		        sqlitedatabase.setTransactionSuccessful();
	        } finally {
	        	sqlitedatabase.endTransaction();
	        }
	        syncstate.onFinish();
	        context.getContentResolver().notifyChange(Uri.withAppendedPath(EsProvider.CONTACT_BY_PERSON_ID_URI, s1), null);
	        ImageCache.getInstance(context).notifyAvatarChange(s);
	        AndroidContactsSync.requestSync(context);
				
		}
	    
	}
	
	private static boolean isContactModified(SQLiteDatabase sqlitedatabase, String s, long l) {
		
		Cursor cursor = null;
		try {
			cursor = sqlitedatabase.query("contacts", new String[] {
					"last_updated_time"
			}, "person_id=?", new String[] {
					s
			}, null, null, null);
			if(cursor.moveToFirst()) {
				long l1 = cursor.getLong(0);
				return l1 != l;
			}
		} finally {
			if(null != cursor) {
				cursor.close();
			}
		}
        return false;
    }
	
	public static void syncPeople(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, HttpOperation.OperationListener operationlistener, boolean flag) throws IOException {
		// TODO
	}
	
	public static boolean changePlusOneData(Context context, EsAccount esaccount, String s, boolean flag)
    {
			Cursor cursor = null;
			byte abyte0[] = null;
			SimpleProfile simpleprofile = null;
			boolean flag1 = false;
			String s1 = (new StringBuilder("g:")).append(s).toString();
	        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
	        try {
	        	sqlitedatabase.beginTransaction();
		        cursor = sqlitedatabase.query("profiles", new String[] {
		            "profile_proto"
		        }, "profile_person_id=?", new String[] {
		            s1
		        }, null, null, null);
		        if(cursor.moveToFirst())
		        	abyte0 = cursor.getBlob(0);
		        if(null != abyte0) {
		        	simpleprofile = deserializeProfile(abyte0);
		        }
		        
		        if(null != simpleprofile) {
		        	 Page page;
		             page = simpleprofile.page;
		             flag1 = false;
		             if(null != page) {
		            	 DataPlusOne dataplusone;
		                 dataplusone = simpleprofile.page.plusone;
		                 flag1 = false;
		                 if(dataplusone != null) {
		                	 DataPlusOne dataplusone1;
		                     boolean flag2;
		                     dataplusone1 = simpleprofile.page.plusone;
		                     flag2 = dataplusone1.isPlusonedByViewer.booleanValue();
		                     flag1 = false;
		                     if(flag2 != flag) {
		                    	 int i;
		                         dataplusone1.isPlusonedByViewer = Boolean.valueOf(flag);
		                         i = dataplusone1.globalCount.intValue();
		                         int j = 0;
		                         if(!flag) 
		                        	 j = -1; 
		                         else 
		                        	 j = 1;
		                         
		                         dataplusone1.globalCount = Integer.valueOf(j + i);
		                         ContentValues contentvalues = new ContentValues();
		                         contentvalues.put("profile_proto", serializeProfile(simpleprofile));
		                         sqlitedatabase.update("profiles", contentvalues, "profile_person_id=?", new String[] {
		                             s1
		                         });
		                         flag1 = true;
		                     }
		                 }
		             }
		        }
		        
		        sqlitedatabase.setTransactionSuccessful();
		        if(flag1)
		            context.getContentResolver().notifyChange(Uri.withAppendedPath(EsProvider.CONTACT_BY_PERSON_ID_URI, s1), null);
		        return flag1;
	        } finally {
	        	sqlitedatabase.endTransaction();
	        	if(null != cursor) {
	        		cursor.close();
	        	}
	        }
    }
	
	public static void insertNewCircle(Context context, EsAccount esaccount, String s, String s1, String s2, boolean flag)
    {
		synchronized(sCircleSyncLock) {
			int i = 0;
	        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
	        ContentValues contentvalues = new ContentValues();
	        contentvalues.put("circle_id", getCircleId(s));
	        contentvalues.put("circle_name", s1);
	        contentvalues.put("sort_key", s2);
	        contentvalues.put("type", Integer.valueOf(1));
	        contentvalues.put("contact_count", Integer.valueOf(0));
	        if(!flag)
	            i = 64;
	        contentvalues.put("semantic_hints", Integer.valueOf(i));
	        contentvalues.put("show_order", Integer.valueOf(getDefaultShowOrder(1)));
	        sqlitedatabase.insertWithOnConflict("circles", null, contentvalues, 5);
	        context.getContentResolver().notifyChange(EsProvider.CIRCLES_URI, null);
	        AndroidContactsSync.requestSync(context);
		}
    }
	
	public static void removeDeletedCircles(Context context, EsAccount esaccount, ArrayList arraylist)
    {
        if(arraylist != null && arraylist.size() != 0)
        {
            SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("circle_id IN (");
            int i = arraylist.size();
            for(int j = 0; j < i; j++)
            {
                if(j > 0)
                    stringbuilder.append(',');
                stringbuilder.append('?');
            }

            stringbuilder.append(")");
            sqlitedatabase.delete("circles", stringbuilder.toString(), (String[])arraylist.toArray(new String[0]));
            context.getContentResolver().notifyChange(EsProvider.CIRCLES_URI, null);
            AndroidContactsSync.requestSync(context);
        }
    }
	
	public static void modifyCircleProperties(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("circle_name", s1);
        int i;
        String as[];
        if(flag)
            i = 0;
        else
            i = 64;
        contentvalues.put("semantic_hints", Integer.valueOf(i));
        as = new String[1];
        as[0] = String.valueOf(getCircleId(s));
        sqlitedatabase.update("circles", contentvalues, "circle_id=?", as);
        context.getContentResolver().notifyChange(EsProvider.CIRCLES_URI, null);
        AndroidContactsSync.requestSync(context);
    }
	
	public static void setCircleVolumes(Context context, EsAccount esaccount, HashMap hashmap)
    {
		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
		try {
			sqlitedatabase.beginTransaction();
			java.util.Map.Entry entry;
			for(Iterator iterator = hashmap.entrySet().iterator(); iterator.hasNext(); setCircleVolumeInTransaction(sqlitedatabase, (String)entry.getKey(), ((Integer)entry.getValue()).intValue()))
				entry = (java.util.Map.Entry)iterator.next();
			sqlitedatabase.setTransactionSuccessful();
			context.getContentResolver().notifyChange(EsProvider.CIRCLES_URI, null);
		} finally {
			sqlitedatabase.endTransaction();
		}

    }
	
	private static void setCircleVolumeInTransaction(SQLiteDatabase sqlitedatabase, String s, int i)
    {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("volume", Integer.valueOf(i));
        sqlitedatabase.update("circles", contentvalues, "circle_id=?", new String[] {
            s
        });
    }
	
	public static void setCircleVolume(Context context, EsAccount esaccount, String s, int i)
    {
		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
		try {
			sqlitedatabase.beginTransaction();
			setCircleVolumeInTransaction(sqlitedatabase, s, i);
			sqlitedatabase.setTransactionSuccessful();
			context.getContentResolver().notifyChange(EsProvider.CIRCLES_URI, null);
		} finally {
			sqlitedatabase.endTransaction();
		}
    }

    public static DataCircleId buildCircleId(String s)
    {
        DataCircleId datacircleid = new DataCircleId();
        datacircleid.focusId = getFocusCircleId(s);
        return datacircleid;
    }
    
    public static void setCircleMembership(Context context, EsAccount esaccount, String s, DataCirclePerson datacircleperson, String as[], String as1[])
    {
    	String s1 = null;
    	SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
    	try {
    		sqlitedatabase.beginTransaction();
    		if(datacircleperson == null) 
    			s1 = s; 
    		else 
    			s1 = getPersonId(datacircleperson.memberId);
    		
            StringBuilder stringbuilder;
            boolean flag1 = false;
            if(!TextUtils.equals(s1, s))
                removeContactInTransaction(sqlitedatabase, s);
            boolean flag = isInMyCircles(sqlitedatabase, s1);
            if(!flag && datacircleperson != null)
                replaceCirclePersonInTransaction(sqlitedatabase, s1, datacircleperson);
            stringbuilder = new StringBuilder();
            if(as != null)
            {
                int j = as.length;
                flag1 = false;
                if(j > 0)
                {
                    stringbuilder.append((new StringBuilder("INSERT OR IGNORE INTO circle_contact(link_person_id,link_circle_id) SELECT ")).append(DatabaseUtils.sqlEscapeString(s1)).append(",circle_id").append(" FROM circles").append(" WHERE circle_id").append(" IN(").toString());
                    for(int k = 0; k < as.length; k++)
                        stringbuilder.append("?,");

                    stringbuilder.setLength(-1 + stringbuilder.length());
                    stringbuilder.append(")");
                    sqlitedatabase.execSQL(stringbuilder.toString(), as);
                    updateMemberCountsInTransaction(sqlitedatabase, as);
                    if(!flag)
                        updateInMyCirclesFlagAndTimestampInTransaction(sqlitedatabase, s1);
                    flag1 = true;
                }
            }
            
            if(as1 != null && as1.length > 0)
            {
                stringbuilder.setLength(0);
                stringbuilder.append((new StringBuilder("DELETE FROM circle_contact WHERE link_person_id=")).append(DatabaseUtils.sqlEscapeString(s1)).append(" AND link_circle_id").append(" IN  (").toString());
                for(int i = 0; i < as1.length; i++)
                    stringbuilder.append("?,");

                stringbuilder.setLength(-1 + stringbuilder.length());
                stringbuilder.append(")");
                sqlitedatabase.execSQL(stringbuilder.toString(), as1);
                updateMemberCountsInTransaction(sqlitedatabase, as1);
                updateInMyCirclesFlagAndTimestampInTransaction(sqlitedatabase, s1);
                if(!flag1)
                    flag1 = isInMyCircles(sqlitedatabase, s1);
            }
            sqlitedatabase.setTransactionSuccessful();
            ContentResolver contentresolver = context.getContentResolver();
            contentresolver.notifyChange(EsProvider.CONTACTS_URI, null);
            contentresolver.notifyChange(EsProvider.CIRCLES_URI, null);
            if(flag != flag1)
                AndroidContactsSync.requestSync(context, true);
    		
    	} finally {
    		sqlitedatabase.endTransaction();
    	}
    }
    
    private static byte[] serializeProfile(SimpleProfile simpleprofile)
    {
        byte abyte0[];
        if(simpleprofile == null)
            abyte0 = null;
        else
            abyte0 = JsonUtil.toByteArray(simpleprofile);
        return abyte0;
    }
    
    private static byte[] serializeContactInfo(ContactInfo contactinfo)
    {
        byte abyte0[];
        if(contactinfo == null)
            abyte0 = null;
        else
            abyte0 = JsonUtil.toByteArray(contactinfo);
        return abyte0;
    }
    
    public static ContactInfo deserializeContactInfo(byte abyte0[]) {
        ContactInfo contactinfo;
        if(abyte0 == null)
            contactinfo = null;
        else
            contactinfo = (ContactInfo)JsonUtil.fromByteArray(abyte0, ContactInfo.class);
        return contactinfo;
    }
    
    public static boolean isSamePerson(PersonData persondata, PersonData persondata1) {
        boolean flag = true;
        if((TextUtils.isEmpty(persondata.getObfuscatedId()) || persondata1.getObfuscatedId() == null || !persondata.getObfuscatedId().equals(persondata1.getObfuscatedId())) && (TextUtils.isEmpty(persondata.getEmail()) || persondata1.getEmail() == null || !persondata.getEmail().equals(persondata1.getEmail())))
            flag = false;
        return flag;
    }
    
    public static Cursor getPeople(Context context, EsAccount esaccount, String s, String s1, String as[], String s2, String as1[]) {
        boolean flag = ensurePeopleSynced(context, esaccount);
        Cursor cursor = null;
        if(flag) {
            Uri uri;
            Uri uri1;
            if(s == null)
                uri = EsProvider.CONTACTS_URI;
            else
                uri = EsProvider.CONTACTS_BY_CIRCLE_ID_URI.buildUpon().appendPath(s).build();
            uri1 = EsProvider.appendAccountParameter(uri, esaccount);
            if(s1 != null) {
                if(as1 == null)
                {
                    as1 = (new String[] {
                        s1
                    });
                } else
                {
                    String as2[] = as1;
                    as1 = new String[1 + as1.length];
                    System.arraycopy(as2, 0, as1, 0, as2.length);
                    as1[-1 + as1.length] = s1;
                }
                s2 = (new StringBuilder()).append(s2).append(" AND person_id NOT IN (SELECT link_person_id FROM circle_contact WHERE link_circle_id=?)").toString();
            }
            cursor = context.getContentResolver().query(uri1, as, s2, as1, null);
        }
        return cursor;
    }
    
    public static boolean ensurePeopleSynced(Context context, EsAccount esaccount)
    {
        boolean flag;
        CountDownLatch countdownlatch;
        flag = true;
        countdownlatch = sInitialSyncLatch;
        while(true) 
        {
            if(countdownlatch == null || queryPeopleSyncTimestamp(context, esaccount) != -1L)
                return flag;
            
            postSyncPeopleRequest(context, esaccount);
            try
            {
                countdownlatch.await(0x15f90L, TimeUnit.MILLISECONDS);
            }
            catch(InterruptedException interruptedexception) { }
            if(countdownlatch.getCount() != 0L)
                flag = false;
        }
    }
    
    public static String extractGaiaId(String s)
    {
        String s1;
        if(s != null && s.startsWith("g:"))
            s1 = s.substring(2);
        else
            s1 = null;
        return s1;
    }
    
    public static PersonData buildPersonFromPersonIdAndName(String s, String s1) {
        String s2 = null;
        String s3 = null;
        if(!s.startsWith("g:")) {
        	if(s.startsWith("e:"))
            {
                s2 = s.substring(2);
                s3 = null;
            } else
            {
                boolean flag = s.startsWith("p:");
                s2 = null;
                s3 = null;
                if(flag)
                {
                    s2 = s;
                    s3 = null;
                }
            } 
        } else { 
        	s3 = s.substring(2);
        }
        return new PersonData(s3, s1, s2);
    }
    
    public static boolean isPersonInList(PersonData persondata, List list) {
    	
    	for(Iterator iterator = list.iterator(); iterator.hasNext();) {
    		if(isSamePerson((PersonData)iterator.next(), persondata)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    
    public static SharingRoster convertAudienceToSharingRoster(AudienceData audiencedata) {
        // TODO
    	return null;
    }
    
    public static AudienceData convertSharingRosterToAudience(Context context, EsAccount esaccount, RenderedSharingRosters renderedsharingrosters)
    {
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList();
        Cursor cursor = getCircles(context, esaccount, 5, new String[] {
            "circle_id", "type", "contact_count"
        }, null, 0);
        AudienceData audiencedata;
        if(cursor == null)
        {
            if(EsLog.isLoggable("EsPeopleData", 6))
                Log.d("EsPeopleData", "Error converting sharing roster to audience");
            audiencedata = null;
        } else
        {
            SparseArray sparsearray = new SparseArray();
            HashMap hashmap = new HashMap();
            cursor.moveToPosition(-1);
            String s2;
            int l;
            for(; cursor.moveToNext(); hashmap.put(s2, Integer.valueOf(l)))
            {
                s2 = cursor.getString(0);
                int k = cursor.getInt(1);
                l = cursor.getInt(2);
                if(k != 1)
                    sparsearray.put(k, s2);
            }

            cursor.close();
            if(renderedsharingrosters.targets != null)
            {
                Iterator iterator = renderedsharingrosters.targets.iterator();
                do
                {
                    if(!iterator.hasNext())
                        break;
                    SharingTarget sharingtarget = (SharingTarget)iterator.next();
                    String s = sharingtarget.displayName;
                    SharingTargetId sharingtargetid = sharingtarget.id;
                    if(sharingtargetid == null)
                    {
                        if(EsLog.isLoggable("EsPeopleData", 6))
                            Log.e("EsPeopleData", "null SharingTargetId");
                        continue;
                    }
                    if(sharingtargetid.groupType != null || sharingtargetid.circleId != null)
                    {
                        int i;
                        String s1;
                        if(sharingtargetid.groupType != null)
                        {
                            i = getCircleType(sharingtargetid.groupType);
                            s1 = (String)sparsearray.get(i);
                            if(s1 == null)
                            {
                                if(EsLog.isLoggable("EsPeopleData", 6))
                                    Log.e("EsPeopleData", (new StringBuilder("Circle ID not found for type: ")).append(i).toString());
                                continue;
                            }
                        } else
                        {
                            i = 1;
                            s1 = getCircleId(sharingtargetid.circleId);
                        }
                        Integer integer = (Integer)hashmap.get(s1);
                        if(integer == null)
                            integer = Integer.valueOf(0);
                        int j = integer.intValue();
                        arraylist1.add(new CircleData(s1, i, s, j));
                    } else
                    if(sharingtargetid.personId != null)
                    {
                        DataCircleMemberId datacirclememberid = sharingtargetid.personId;
                        if(datacirclememberid.obfuscatedGaiaId != null || datacirclememberid.email != null)
                            arraylist.add(new PersonData(datacirclememberid.obfuscatedGaiaId, s, datacirclememberid.email));
                        else
                        if(EsLog.isLoggable("EsPeopleData", 6))
                            Log.e("EsPeopleData", (new StringBuilder("Invalid user from roster: ")).append(s).toString());
                    } else
                    if(EsLog.isLoggable("EsPeopleData", 6))
                        Log.e("EsPeopleData", "Invalid SharingTargetId");
                } while(true);
            }
            audiencedata = new AudienceData(arraylist, arraylist1);
        }
        return audiencedata;
    }
    
    private static SimpleProfile deserializeProfile(byte abyte0[]) {
        SimpleProfile simpleprofile;
        if(abyte0 == null)
            simpleprofile = null;
        else
            simpleprofile = (SimpleProfile)JsonUtil.fromByteArray(abyte0, SimpleProfile.class);
        return simpleprofile;
    }
    
    private static void loadProfileAndContactDataFromDatabase(Context context, EsAccount esaccount, String s, ProfileAndContactData profileandcontactdata) {
        Cursor cursor = null;
        profileandcontactdata.profileState = 0;
        try {
	        cursor = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("contacts LEFT OUTER JOIN profiles ON (contacts.person_id=profiles.profile_person_id) LEFT OUTER JOIN circle_contact ON ( contacts.person_id = circle_contact.link_person_id)", PROFILE_COLUMNS, "profiles.profile_person_id=?", new String[] {
	            s
	        }, null, null, null);
	        if(cursor.moveToFirst()) {
	        	boolean flag = false;
	        	if(0 != cursor.getInt(3)) {
	        		flag = true;
	        	}
	            profileandcontactdata.gaiaId = extractGaiaId(s);
                profileandcontactdata.profileState = cursor.getInt(0);
                
                profileandcontactdata.blocked = flag;
                profileandcontactdata.packedCircleIds = cursor.getString(2);
                profileandcontactdata.lastUpdatedTime = cursor.getLong(4);
                profileandcontactdata.displayName = cursor.getString(1);
                profileandcontactdata.contactUpdateTime = cursor.getLong(5);
                byte abyte0[] = cursor.getBlob(6);
                if(abyte0 != null)
                    profileandcontactdata.contact = deserializeContactInfo(abyte0);
                profileandcontactdata.profileUpdateTime = cursor.getLong(7);
                byte abyte1[] = cursor.getBlob(8);
                if(abyte1 != null)
                    profileandcontactdata.profile = deserializeProfile(abyte1);
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static void loadProfileFromServer(Context context, EsAccount esaccount, String s) throws IOException {
        GetProfileOperation getprofileoperation = new GetProfileOperation(context, esaccount, s, true, null, null);
        getprofileoperation.start();
        getprofileoperation.logAndThrowExceptionIfFailed("EsPeopleData");
    }
    
    private static void updateInMyCirclesFlagAndTimestampInTransaction(SQLiteDatabase sqlitedatabase, String s)
    {
        sqlitedatabase.execSQL("UPDATE contacts SET in_my_circles=(EXISTS (SELECT 1 FROM circle_contact WHERE link_person_id=?)),last_updated_time=last_updated_time + 1 WHERE person_id=?", new String[] {
            s, s
        });
    }
    
    private static void updateMemberCountsInTransaction(SQLiteDatabase sqlitedatabase, String as[])
    {
        if(as != null && as.length != 0)
        {
            String as1[] = new String[2];
            int i = as.length;
            for(int j = 0; j < i; j++)
            {
                String s = as[j];
                as1[1] = s;
                as1[0] = s;
                sqlitedatabase.execSQL("UPDATE circles SET contact_count=(SELECT count(*) FROM circle_contact WHERE link_circle_id=?) WHERE circle_id=?", as1);
            }

        }
    }

    private static boolean replaceUserInTransaction(SQLiteDatabase sqlitedatabase, String s, String s1, String s2, UserInfo userinfo) {
        boolean flag = true;
        ContentValues contentvalues = new ContentValues(4);
        String s3 = EsAvatarData.compressAvatarUrl(s2);
        if(userinfo == null)
        {
            if(EsLog.isLoggable("EsPeopleData", 3))
                Log.d("EsPeopleData", (new StringBuilder(">>>>> Inserting person id: ")).append(s).append(", name: ").append(s1).toString());
            contentvalues.put("person_id", (new StringBuilder("g:")).append(s).toString());
            contentvalues.put("gaia_id", s);
            contentvalues.put("name", s1);
            contentvalues.put("avatar", s3);
            sqlitedatabase.insertWithOnConflict("contacts", null, contentvalues, 4);
        } else
        if(!TextUtils.equals(s1, userinfo.name) || !userinfo.inMyCircles && !TextUtils.equals(s3, userinfo.avatarUrl))
        {
            if(EsLog.isLoggable("EsPeopleData", 3))
                Log.d("EsPeopleData", (new StringBuilder(">>>>> Updating person id: ")).append(s).append(", name: ").append(s1).toString());
            contentvalues.put("name", s1);
            if(!userinfo.inMyCircles)
                contentvalues.put("avatar", s3);
            String as[] = new String[1];
            as[0] = s;
            sqlitedatabase.update("contacts", contentvalues, "gaia_id = ?", as);
        } else
        {
            flag = false;
        }
        return flag;
    }

    public static void replaceUsersInTransaction(SQLiteDatabase sqlitedatabase, List list) {
    	// TODO
    }
    
    private static void replaceSuggestionsInTransaction(SQLiteDatabase sqlitedatabase, String s, String s1, String s2, List list) {
        // TODO
    }
    
    private static void replaceCirclePersonInTransaction(SQLiteDatabase sqlitedatabase, String s, DataCirclePerson datacircleperson)
    {
        ArrayList arraylist = new ArrayList();
        int i;
        String s1;
        ContentValues contentvalues;
        String s2;
        boolean flag;
        if(getCircleCount(datacircleperson) != 0)
            i = 1;
        else
            i = 0;
        s1 = getPersonId(datacircleperson.memberId);
        contentvalues = new ContentValues();
        s2 = datacircleperson.memberProperties.displayName;
        if(i != 0)
            collectSearchKeysForName(arraylist, s2);
        contentvalues.put("name", s2);
        contentvalues.put("sort_key", datacircleperson.memberProperties.firstNameSortKey);
        contentvalues.put("avatar", EsAvatarData.compressAvatarUrl(datacircleperson.memberProperties.photoUrl));
        contentvalues.put("last_updated_time", Long.valueOf(getLastUpdatedTime(datacircleperson)));
        contentvalues.put("in_my_circles", Integer.valueOf(i));
        if("ENTITY".equals(datacircleperson.memberProperties.profileType))
            contentvalues.put("profile_type", Integer.valueOf(2));
        else
            contentvalues.put("profile_type", Integer.valueOf(1));
        contentvalues.put("profile_state", Integer.valueOf(5));
        if(sqlitedatabase.update("contacts", contentvalues, "person_id=?", new String[] {s1}) == 0)
        {
            contentvalues.put("person_id", s1);
            contentvalues.put("gaia_id", extractGaiaId(s1));
            sqlitedatabase.insert("contacts", null, contentvalues);
            flag = true;
        } else
        {
            flag = false;
        }
        contentvalues.clear();
        contentvalues.put("contact_update_time", Long.valueOf(System.currentTimeMillis()));
        contentvalues.put("contact_proto", serializeContactInfo(convertCirclePersonToContactInfo(datacircleperson)));
        if(sqlitedatabase.update("profiles", contentvalues, "profile_person_id=?", new String[] {s1}) == 0)
        {
            contentvalues.put("profile_person_id", s1);
            sqlitedatabase.insert("profiles", null, contentvalues);
        }
        replaceCircleMembershipInTransaction(sqlitedatabase, s, datacircleperson, flag);
        buildSearchKeysForEmailAddresses(datacircleperson, arraylist);
        replaceSearchKeysInTransaction(sqlitedatabase, s, arraylist, flag);
    }
    
    private static void buildSearchKeysForEmailAddresses(DataCirclePerson datacircleperson, ArrayList arraylist)
    {
        boolean flag;
        boolean flag1;
        if(!TextUtils.isEmpty(datacircleperson.memberId.email))
            flag = true;
        else
            flag = false;
        if(datacircleperson.memberProperties.email != null && datacircleperson.memberProperties.email.size() > 0)
            flag1 = true;
        else
            flag1 = false;
        if(flag || flag1)
        {
            ArrayList arraylist1 = new ArrayList();
            if(flag)
            {
                String s1 = normalizeEmailAddress(datacircleperson.memberId.email);
                if(s1 != null)
                {
                    arraylist1.add(s1);
                    arraylist.add(new SearchKey(1, s1));
                }
            }
            if(flag1)
            {
                Iterator iterator = datacircleperson.memberProperties.email.iterator();
                do
                {
                    if(!iterator.hasNext())
                        break;
                    DataEmail dataemail = (DataEmail)iterator.next();
                    if(!TextUtils.isEmpty(dataemail.value))
                    {
                        String s = normalizeEmailAddress(dataemail.value);
                        if(s != null && !arraylist1.contains(s))
                        {
                            arraylist1.add(s);
                            arraylist.add(new SearchKey(1, s));
                        }
                    }
                } while(true);
            }
        }
    }
    
    private static String normalizeEmailAddress(String s)
    {
        Rfc822Token arfc822token[] = Rfc822Tokenizer.tokenize(s);
        String s1 = null;
        if(arfc822token == null) { 
        	return null;
        }
        
        int length = arfc822token.length;
        s1 = null;
        if(length == 0) {
        	return null;
        }
        
        String s2 = arfc822token[0].getAddress();
        boolean flag = TextUtils.isEmpty(s2);
        s1 = null;
        if(!flag)
            s1 = s2.toLowerCase();
        return s1;
    }

    
    private static ContactInfo convertCirclePersonToContactInfo(DataCirclePerson datacircleperson)
    {
        ContactInfo contactinfo = new ContactInfo();
        if(null == datacircleperson.memberProperties){
        	return contactinfo;
        }

        DataCircleMemberId datacirclememberid = datacircleperson.memberId;
        if(!TextUtils.isEmpty(datacirclememberid.email))
        {
            contactinfo.emails = new ArrayList();
            DataEmail dataemail2 = new DataEmail();
            dataemail2.value = datacirclememberid.email;
            contactinfo.emails.add(dataemail2);
        }
        if(!TextUtils.isEmpty(datacirclememberid.phone))
        {
            contactinfo.phones = new ArrayList();
            DataPhone dataphone2 = new DataPhone();
            dataphone2.value = datacirclememberid.phone;
            contactinfo.phones.add(dataphone2);
        }
        if(datacircleperson.memberProperties.email != null)
        {
            if(contactinfo.emails == null)
                contactinfo.emails = new ArrayList();
            DataEmail dataemail1;
            for(Iterator iterator2 = datacircleperson.memberProperties.email.iterator(); iterator2.hasNext(); contactinfo.emails.add(dataemail1))
            {
                DataEmail dataemail = (DataEmail)iterator2.next();
                dataemail1 = new DataEmail();
                dataemail1.standardTag = dataemail.standardTag;
                dataemail1.customTag = dataemail.customTag;
                dataemail1.value = dataemail.value;
            }

        }
        if(datacircleperson.memberProperties.phone != null)
        {
            if(contactinfo.phones == null)
                contactinfo.phones = new ArrayList();
            DataPhone dataphone1;
            for(Iterator iterator1 = datacircleperson.memberProperties.phone.iterator(); iterator1.hasNext(); contactinfo.phones.add(dataphone1))
            {
                DataPhone dataphone = (DataPhone)iterator1.next();
                dataphone1 = new DataPhone();
                dataphone1.standardTag = dataphone.standardTag;
                dataphone1.customTag = dataphone.customTag;
                dataphone1.value = dataphone.value;
            }

        }
        if(datacircleperson.memberProperties.address != null)
        {
            if(contactinfo.addresses == null)
                contactinfo.addresses = new ArrayList();
            Iterator iterator = datacircleperson.memberProperties.address.iterator();
            while(iterator.hasNext()) 
            {
                DataCircleMemberPropertiesAddress datacirclememberpropertiesaddress = (DataCircleMemberPropertiesAddress)iterator.next();
                DataCircleMemberPropertiesAddress datacirclememberpropertiesaddress1 = new DataCircleMemberPropertiesAddress();
                datacirclememberpropertiesaddress1.standardTag = datacirclememberpropertiesaddress.standardTag;
                datacirclememberpropertiesaddress1.customTag = datacirclememberpropertiesaddress.customTag;
                datacirclememberpropertiesaddress1.value = datacirclememberpropertiesaddress.value;
                contactinfo.addresses.add(datacirclememberpropertiesaddress1);
            }
        }
        return contactinfo;
    }
    
    private static boolean removeContactInTransaction(SQLiteDatabase sqlitedatabase, String s)
    {
        boolean flag = true;
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("in_my_circles", Integer.valueOf(0));
        String as[] = new String[1];
        as[0] = s;
        int i = sqlitedatabase.update("contacts", contentvalues, "person_id=?", as);
        if(i != 0)
            sqlitedatabase.delete("circle_contact", "link_person_id=?", as);
        if(i <= 0)
            flag = false;
        return flag;
    }
    
    private static void collectSearchKeysForName(ArrayList arraylist, String s)
    {
        if(arraylist != null && !TextUtils.isEmpty(s)) {
        	String s1 = s.toLowerCase();
            int i = 0;
            int j = s1.length();
            for(int k = 0; k < j; k++)
            {
                if(Character.isLetterOrDigit(s1.charAt(k)))
                    continue;
                if(k > i)
                    arraylist.add(new SearchKey(0, s1.substring(i, k)));
                i = k + 1;
            }

            if(j > i)
                arraylist.add(new SearchKey(0, s1.substring(i)));
        }
    }
    
    private static boolean isInMyCircles(SQLiteDatabase sqlitedatabase, String s)
    {
        try {
	        long l = DatabaseUtils.longForQuery(sqlitedatabase, "SELECT in_my_circles FROM contacts WHERE person_id=?", new String[] {
	            s
	        });
	        return  l != 0L;
        } catch(SQLiteDoneException sqlitedoneexception) {
        	// TODO log
        	return false;
        }
        
    }
    
    public static void activateAccount() {
        sInitialSyncLatch = new CountDownLatch(1);
    }
    
    private static boolean ensureSuggestedPeopleSynced(Context context, EsAccount esaccount, long l, boolean flag) {
    	synchronized(sSuggestedPeopleSyncLock) {
    		long l1 = querySuggestedPeopleSyncTimestamp(context, esaccount);
    		if(l1 != -1L && (!flag || System.currentTimeMillis() - l1 <= l)) 
    			return true; 
            SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
            ContentValues contentvalues = new ContentValues();
            contentvalues.put("suggested_people_sync_time", Long.valueOf(System.currentTimeMillis()));
            sqlitedatabase.update("account_status", contentvalues, null, null);
            GetSuggestedPeopleOperation getsuggestedpeopleoperation = new GetSuggestedPeopleOperation(context, esaccount, null, null);
            getsuggestedpeopleoperation.start();
            if(!getsuggestedpeopleoperation.hasError()) {
            	return true;
            }
            getsuggestedpeopleoperation.logError("EsPeopleData");
            contentvalues.put("suggested_people_sync_time", Integer.valueOf(-1));
            sqlitedatabase.update("account_status", contentvalues, null, null);
            return false;
    	}
    }
    
    private static long querySuggestedPeopleSyncTimestamp(Context context, EsAccount esaccount) {
    	
    	try {
    		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
    		return DatabaseUtils.longForQuery(sqlitedatabase, "SELECT suggested_people_sync_time  FROM account_status", null);
    	} catch (SQLiteDoneException e) {
    		return -1;
    	}
    }
    
    private static int queryNumEntries(SQLiteDatabase sqlitedatabase, String s, String s1) {
        return (int)DatabaseUtils.longForQuery(sqlitedatabase, (new StringBuilder("SELECT COUNT(*) FROM ")).append(s).append(" WHERE ").append(s1).toString(), null);
    }
    
    private static long queryCircleSyncTimestamp(Context context, EsAccount esaccount) {
    	try {
    		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
    		return DatabaseUtils.longForQuery(sqlitedatabase, "SELECT circle_sync_time  FROM account_status", null);
    	} catch (SQLiteDoneException sqlitedoneexception) {
    		return -1L;
    	}
    }
    
    private static long queryBlockedPeopleSyncTimestamp(Context context, EsAccount esaccount) {
    	try {
	        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
	        return DatabaseUtils.longForQuery(sqlitedatabase, "SELECT blocked_people_sync_time  FROM account_status", null);
    	} catch (SQLiteDoneException sqlitedoneexception) {
    		return -1L;
    	}
    }
    
    private static long getLastUpdatedTime(DataCirclePerson datacircleperson)
    {
        long l;
        if(!TextUtils.isEmpty(datacircleperson.memberProperties.lastUpdateTime))
            l = Long.parseLong(datacircleperson.memberProperties.lastUpdateTime, 16);
        else
            l = System.currentTimeMillis();
        return l;
    }
    
    private static long queryPeopleSyncTimestamp(Context context, EsAccount esaccount) {
    	
    	try {
    		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        	return DatabaseUtils.longForQuery(sqlitedatabase, "SELECT people_sync_time  FROM account_status", null);
    	} catch (SQLiteDoneException sqlitedoneexception) {
    		return -1L;
    	}
        
    }
    
	private static void syncCircles(Context context, EsAccount esaccount, boolean flag) throws IOException {
		synchronized (sCircleSyncLock) {
			EsSyncAdapterService.SyncState syncstate = new EsSyncAdapterService.SyncState();
			syncstate.onSyncStart("Circle sync");
			boolean flag1 = doCirclesSync(context, esaccount, syncstate, null);
			syncstate.onSyncFinish();
			if (flag1) {
				ContentValues contentvalues = new ContentValues();
				contentvalues.put("circle_sync_time",
						Long.valueOf(System.currentTimeMillis()));
				EsDatabaseHelper.getDatabaseHelper(context, esaccount)
						.getWritableDatabase()
						.update("account_status", contentvalues, null, null);
				context.getContentResolver().notifyChange(
						EsProvider.ACCOUNT_STATUS_URI, null);
				if (flag)
					postSyncPeopleRequest(context, esaccount);
			}
		}
	}
	
	private static boolean doCirclesSync(Context context, EsAccount esaccount,
			EsSyncAdapterService.SyncState syncstate,
			HttpOperation.OperationListener operationlistener)
			throws IOException {
		boolean flag = syncstate.isCanceled();
		boolean flag1 = false;
		if (!flag) {
			syncstate.onStart("Circles");
			LoadSocialNetworkOperation loadsocialnetworkoperation = new LoadSocialNetworkOperation(context, esaccount, true, false, 0, null, null, operationlistener);
			GetVolumeControlsOperation getvolumecontrolsoperation = new GetVolumeControlsOperation(context, esaccount, null, operationlistener);
			ApiaryBatchOperation apiarybatchoperation = new ApiaryBatchOperation(context, esaccount, null, operationlistener);
			apiarybatchoperation.add(loadsocialnetworkoperation);
			apiarybatchoperation.add(getvolumecontrolsoperation);
			apiarybatchoperation.start(syncstate, new HttpTransactionMetrics());
			loadsocialnetworkoperation.logAndThrowExceptionIfFailed("EsPeopleData");
			syncstate.onFinish(insertCircles(context, esaccount,
					loadsocialnetworkoperation.getCircleList(),
					loadsocialnetworkoperation.getSystemGroups(),
					getvolumecontrolsoperation.getVolumeControlMap()));
			flag1 = true;
		}
		return flag1;
	}
	
	public static void deleteFromSuggestedPeople(Context context, EsAccount esaccount, List list)
    {
		String s;
		ContentValues contentvalues;
		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
		try {
			sqlitedatabase.beginTransaction();
	        for(Iterator iterator = list.iterator(); iterator.hasNext(); sqlitedatabase.update("suggested_people", contentvalues, "suggested_person_id=?", new String[] {s}))
	        {
	            s = (String)iterator.next();
	            contentvalues = new ContentValues();
	            contentvalues.put("dismissed", Integer.valueOf(1));
	        }

	        sqlitedatabase.setTransactionSuccessful();
	        context.getContentResolver().notifyChange(EsProvider.SUGGESTED_PEOPLE_URI, null);
		} finally {
			sqlitedatabase.endTransaction();
		}
    }
	
	private static int getCircleType(String s)
    {
        byte byte0;
        if("PUBLIC".equals(s))
            byte0 = 9;
        else
        if("DASHER_DOMAIN".equals(s))
            byte0 = 8;
        else
        if("YOUR_CIRCLES".equals(s))
            byte0 = 5;
        else
        if("EXTENDED_CIRCLES".equals(s))
            byte0 = 7;
        else
        if("BLOCKED".equals(s))
            byte0 = 10;
        else
        if("IGNORED".equals(s))
            byte0 = 100;
        else
        if("ALL_CIRCLE_MEMBERS".equals(s))
            byte0 = 11;
        else
            byte0 = 0;
        return byte0;
    }
	
	private static int getCircleCount(DataCirclePerson datacircleperson)
    {
        int i;
        if(datacircleperson.membership == null)
        {
            i = 0;
        } else
        {
            i = 0;
            int j = datacircleperson.membership.size();
            int k = 0;
            while(k < j) 
            {
                DataMembership datamembership = (DataMembership)datacircleperson.membership.get(k);
                if(datamembership.deleted == null || !datamembership.deleted.booleanValue())
                    i++;
                k++;
            }
        }
        return i;
    }
	
	private static int insertCircles(Context context, EsAccount esaccount, DataViewerCircles dataviewercircles, DataSystemGroups datasystemgroups, VolumeControlMap volumecontrolmap) {
		// TODO
		return 0;
    }
	
	public static void insertProfile(Context context, EsAccount esaccount, String s, SimpleProfile simpleprofile)
    {
		String s1;
        boolean flag;
        ContentResolver contentresolver;
        boolean flag1;
        String s2;
        ContentValues contentvalues;
        String s3;
        SQLiteDatabase sqlitedatabase;
        if(EsLog.isLoggable("EsPeopleData", 3))
        {
            StringBuilder stringbuilder = (new StringBuilder("Profile for ")).append(s).append(": ");
            if(simpleprofile != null)
                s3 = simpleprofile.toString();
            else
                s3 = null;
            Log.d("EsPeopleData", stringbuilder.append(s3).toString());
        }
        s1 = (new StringBuilder("g:")).append(s).toString();
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        flag = replaceProfileProtoInTransaction(sqlitedatabase, s1, simpleprofile);
	        if(flag)
	        {
	            flag1 = simpleprofile.config.socialGraphData.blocked.booleanValue();
	            s2 = simpleprofile.displayName;
	            contentvalues = new ContentValues();
	            contentvalues.put("blocked", Boolean.valueOf(flag1));
	            if(flag1)
	                contentvalues.put("in_my_circles", Integer.valueOf(0));
	            if(sqlitedatabase.update("contacts", contentvalues, "person_id=?", new String[] {s1}) == 0 && flag1)
	            {
	                contentvalues.put("person_id", s1);
	                contentvalues.put("gaia_id", extractGaiaId(s1));
	                contentvalues.put("name", s2);
	                sqlitedatabase.insert("contacts", null, contentvalues);
	            }
	            if(flag1)
	            {
	                sqlitedatabase.delete("circle_contact", "link_person_id=?", new String[] {
	                    s1
	                });
	                sqlitedatabase.execSQL("UPDATE circles SET contact_count=(SELECT count(*) FROM circle_contact WHERE link_circle_id=circle_id) WHERE type=1");
	            }
	        }
	        sqlitedatabase.setTransactionSuccessful();
	        if(flag)
	        {
	            contentresolver = context.getContentResolver();
	            contentresolver.notifyChange(Uri.withAppendedPath(EsProvider.CONTACT_BY_PERSON_ID_URI, s1), null);
	            contentresolver.notifyChange(EsProvider.CIRCLES_URI, null);
	            ImageCache.getInstance(context).notifyAvatarChange(extractGaiaId(s1));
	            AndroidContactsSync.requestSync(context);
	        }
        } finally {
        	sqlitedatabase.endTransaction();
        }
       
    }
	
	private static void postSyncPeopleRequest(final Context context, final EsAccount esaccount) {
        EsService.postOnServiceThread(new Runnable() {

            public final void run()
            {
                EsService.syncPeople(context, esaccount, false);
            }
        });
    }
	
	public static void updateMemberCounts(SQLiteDatabase sqlitedatabase)
    {
        sqlitedatabase.execSQL("UPDATE circles SET contact_count=(SELECT count(*) FROM circle_contact WHERE link_circle_id=circle_id) WHERE type=1");
    }
	
	public static DataCircleMemberId getCircleMemberId(String s) {
		
        DataCircleMemberId datacirclememberid = new DataCircleMemberId();
        if(!s.startsWith("g:")) {
        	if(s.startsWith("e:"))
                datacirclememberid.email = s.substring(2);
            else
            if(s.startsWith("p:"))
                datacirclememberid.phone = s.substring(2); 
        } else { 
        	datacirclememberid.obfuscatedGaiaId = s.substring(2);
        }

        return datacirclememberid;
    }
	
	private static String getPersonId(DataCircleMemberId datacirclememberid)
    {
        if(null == datacirclememberid) {
        	return null;
        }
        
        String s = null;
        if(!TextUtils.isEmpty(datacirclememberid.obfuscatedGaiaId))
            s = (new StringBuilder("g:")).append(datacirclememberid.obfuscatedGaiaId).toString();
        else
        if(!TextUtils.isEmpty(datacirclememberid.email))
        {
            s = (new StringBuilder("e:")).append(datacirclememberid.email).toString();
        } else
        {
            boolean flag = TextUtils.isEmpty(datacirclememberid.phone);
            s = null;
            if(!flag)
                s = (new StringBuilder("p:")).append(datacirclememberid.phone).toString();
        }
        return s;
    }
	
	
	private static boolean replaceProfileProtoInTransaction(SQLiteDatabase sqlitedatabase, String s, SimpleProfile simpleprofile)
    {
       // TODO
		return false;
    }
	
	private static void replaceCircleMembershipInTransaction(SQLiteDatabase sqlitedatabase, String s, DataCirclePerson datacircleperson, boolean flag) {
		
		int i = getCircleCount(datacircleperson);
		if(0 == i) {
			if(!flag)
	            sqlitedatabase.delete("circle_contact", "link_person_id=?", new String[] {
	                s
	            });
			return;
		}
		
		String as[] = new String[i + 1];
        as[0] = s;
        StringBuilder stringbuilder = new StringBuilder();
        int j = datacircleperson.membership.size();
        int k = 0;
        int l = 1;
        while(k < j) 
        {
            DataMembership datamembership = (DataMembership)datacircleperson.membership.get(k);
            int i1;
            if(datamembership.deleted == null || !datamembership.deleted.booleanValue())
            {
                stringbuilder.append("?,");
                i1 = l + 1;
                as[l] = getCircleId(datamembership.circleId.focusId);
            } else
            {
                i1 = l;
            }
            k++;
            l = i1;
        }
        stringbuilder.setLength(-1 + stringbuilder.length());
        if(!flag)
            sqlitedatabase.execSQL((new StringBuilder("DELETE FROM circle_contact WHERE link_person_id=? AND link_circle_id NOT IN (")).append(stringbuilder.toString()).append(")").toString(), as);
        sqlitedatabase.execSQL((new StringBuilder("INSERT OR IGNORE INTO circle_contact(link_person_id,link_circle_id) SELECT ?, circle_id FROM circles WHERE circle_id IN (")).append(stringbuilder.toString()).append(")").toString(), as);
    }
	
	private static void replaceSearchKeysInTransaction(SQLiteDatabase sqlitedatabase, String s, ArrayList arraylist, boolean flag)
    {
        if(!flag)
            sqlitedatabase.delete("contact_search", "search_person_id=?", new String[] {
                s
            });
        ContentValues contentvalues = new ContentValues();
        for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); sqlitedatabase.insert("contact_search", null, contentvalues))
        {
            SearchKey searchkey = (SearchKey)iterator.next();
            contentvalues.put("search_person_id", s);
            contentvalues.put("search_key_type", Integer.valueOf(searchkey.keyType));
            contentvalues.put("search_key", searchkey.key);
        }

    }
	
	private static int getDefaultShowOrder(int i) {
		
		char c = '<';
		switch(i) {
			case 5:
				c = '\024';
				break;
			case 6:
				
				break;
			case 7:
				c = '\036';
				break;
			case 8:
				c = '(';
				break;
			case 9:
				c = '2';
				break;
			case 10:
				c = '\u03E8';
				break;
			default:
				break;
		}
        return c;
    }
	
	static void cleanupData(SQLiteDatabase sqlitedatabase, EsAccount esaccount)
    {
        sqlitedatabase.delete("contacts", (new StringBuilder("in_my_circles=0  AND blocked=0 AND gaia_id!=")).append(esaccount.getGaiaId()).append(" AND gaia_id").append(" NOT IN (SELECT author_id").append(" FROM activities").append(')').append(" AND gaia_id").append(" NOT IN (SELECT author_id").append(" FROM activity_comments").append(')').append(" AND person_id").append(" NOT IN (SELECT suggested_person_id").append(" FROM suggested_people").append(')').append(" AND gaia_id").append(" NOT IN (SELECT author_id").append(" FROM photo_comment").append(')').append(" AND gaia_id").append(" NOT IN (SELECT creator_id").append(" FROM photo_shape").append(')').append(" AND gaia_id").append(" NOT IN (SELECT subject_id").append(" FROM photo_shape").append(')').append(" AND gaia_id").append(" NOT IN (SELECT gaia_id").append(" FROM circle_action").append(')').append(" AND gaia_id").append(" NOT IN (SELECT gaia_id").append(" FROM event_people").append(')').append(" AND person_id").append(" NOT IN (SELECT link_person_id").append(" FROM square_contact").append(')').append(" AND gaia_id").append(" NOT IN (SELECT inviter_gaia_id").append(" FROM squares").append(')').toString(), null);
    }
	
	public static class ContactInfo extends GenericJson
    {

        public List addresses;
        public List emails;
        public List phones;

        public ContactInfo()
        {
        }
    }
	
	public static final class ProfileAndContactData {
		
		public boolean blocked;
        public ContactInfo contact;
        public long contactUpdateTime;
        public String displayName;
        public String gaiaId;
        public long lastUpdatedTime;
        public String packedCircleIds;
        public SimpleProfile profile;
        public int profileState;
        public long profileUpdateTime;
        
        public ProfileAndContactData() {
        }
        
        public final String toString() {
            return (new StringBuilder("Contact: ")).append(contact).append("\nProfile: ").append(profile).toString();
        }

    }
	
	private static final class UserInfo {

        String avatarUrl;
        boolean inMyCircles;
        String name;

        UserInfo() {
        }
    }
	
	private static final class SearchKey
    {

        final String key;
        final int keyType;

        public SearchKey(int i, String s)
        {
            keyType = i;
            key = s;
        }
    }

}
