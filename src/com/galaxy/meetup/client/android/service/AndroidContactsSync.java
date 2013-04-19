/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.DownloadImageOperation;
import com.galaxy.meetup.client.android.content.AvatarImageRequest;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.cache.EsMediaCache;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.server.client.domain.ContactTag;
import com.galaxy.meetup.server.client.domain.Contacts;
import com.galaxy.meetup.server.client.domain.DataCircleMemberPropertiesAddress;
import com.galaxy.meetup.server.client.domain.DataEmail;
import com.galaxy.meetup.server.client.domain.DataPhone;
import com.galaxy.meetup.server.client.domain.TaggedAddress;
import com.galaxy.meetup.server.client.domain.TaggedEmail;
import com.galaxy.meetup.server.client.domain.TaggedPhone;

/**
 * 
 * @author sihai
 *
 */
public class AndroidContactsSync {
	
	private static final String ACTIVITY_PROJECTION[] = {
        "activity_id", "embed_media", "total_comment_count", "plus_one_data", "loc", "original_author_name", "annotation", "title"
    };
    private static final Comparator ACTIVITY_STATE_COMPARATOR = new Comparator() {

        public final int compare(Object obj, Object obj1)
        {
            int i;
            ActivityState activitystate;
            ActivityState activitystate1;
            i = -1;
            activitystate = (ActivityState)obj;
            activitystate1 = (ActivityState)obj1;
            if(!activitystate.exists || activitystate1.exists) {
            	if(activitystate.exists || !activitystate1.exists)
                {
                    if(activitystate.created == activitystate1.created)
                    {
                        return activitystate.activityId.compareTo(activitystate1.activityId);
                    }
                    if(activitystate.created > activitystate1.created)
                        return -1;
                }
                i = 1;
            }
            return i;
        }

    };
    
    private static final String ACTIVITY_SUMMARY_PROJECTION[] = {
        "activity_id", "author_id", "created", "modified"
    };
    private static final String AVATAR_URL_PROJECTION[] = {
        "gaia_id", "avatar"
    };
    private static final String CONTACTS_PROJECTION[] = {
        "person_id", "last_updated_time", "for_sharing"
    };
    private static final Uri DISPLAY_PHOTO_CONTENT_MAX_DIMENSIONS_URI;
    private static final String EMAIL_TYPE_CUSTOM = String.valueOf(0);
    private static final String EMAIL_TYPE_HOME = String.valueOf(1);
    private static final String EMAIL_TYPE_OTHER = String.valueOf(3);
    private static final String EMAIL_TYPE_WORK = String.valueOf(2);
    private static final String ENTITIES_PROJECTION[] = {
        "sourceid", "mimetype", "data_id", "data1", "data2", "data3"
    };
    private static final String GROUPS_PROJECTION[] = {
        "_id", "sourceid", "title"
    };
    private static final String LARGE_AVATAR_RAW_CONTACTS_PROJECTION[] = {
        "_id", "sourceid", "sync2"
    };
    private static final String MY_PROFILE_PROJECTION[] = {
        "name", "last_updated_time", "profile_proto"
    };
    private static final String PHONE_TYPE_ASSISTANT = String.valueOf(19);
    private static final String PHONE_TYPE_CALLBACK = String.valueOf(8);
    private static final String PHONE_TYPE_CAR = String.valueOf(9);
    private static final String PHONE_TYPE_COMPANY_MAIN = String.valueOf(10);
    private static final String PHONE_TYPE_CUSTOM = String.valueOf(0);
    private static final String PHONE_TYPE_HOME = String.valueOf(1);
    private static final String PHONE_TYPE_HOME_FAX = String.valueOf(5);
    private static final String PHONE_TYPE_ISDN = String.valueOf(11);
    private static final String PHONE_TYPE_MAIN = String.valueOf(12);
    private static final String PHONE_TYPE_MOBILE = String.valueOf(2);
    private static final String PHONE_TYPE_OTHER = String.valueOf(7);
    private static final String PHONE_TYPE_OTHER_FAX = String.valueOf(13);
    private static final String PHONE_TYPE_PAGER = String.valueOf(6);
    private static final String PHONE_TYPE_RADIO = String.valueOf(14);
    private static final String PHONE_TYPE_TELEX = String.valueOf(15);
    private static final String PHONE_TYPE_TTY_TDD = String.valueOf(16);
    private static final String PHONE_TYPE_WORK = String.valueOf(3);
    private static final String PHONE_TYPE_WORK_FAX = String.valueOf(4);
    private static final String PHONE_TYPE_WORK_MOBILE = String.valueOf(17);
    private static final String PHONE_TYPE_WORK_PAGER = String.valueOf(18);
    private static final String POSTAL_TYPE_CUSTOM = String.valueOf(0);
    private static final String POSTAL_TYPE_HOME = String.valueOf(1);
    private static final String POSTAL_TYPE_OTHER = String.valueOf(3);
    private static final String POSTAL_TYPE_WORK = String.valueOf(2);
    private static final Uri PROFILE_CONTENT_RAW_CONTACTS_URI;
    private static final String PROFILE_ENTITIES_PROJECTION[] = {
        "mimetype", "_id", "data1", "data2", "data3"
    };
    private static final String PROFILE_PROJECTION[] = {
        "person_id", "name", "contact_proto"
    };
    public static final String PROFILE_RAW_CONTACT_PROJECTION[] = {
        "_id", "sync1"
    };
    private static final String RAW_CONTACT_PROJECTION[] = {
        "_id", "sourceid", "sync1"
    };
    private static final String RAW_CONTACT_REFRESH_PROJECTION[] = {
        "_id", "account_type", "account_name", "data_set", "sourceid", "sync2", "sync4"
    };
    private static final String RAW_CONTACT_SOURCE_ID_PROJECTION[] = {
        "_id", "sourceid"
    };
    public static final Uri STREAM_ITEMS_CONTENT_LIMIT_URI;
    public static final Uri STREAM_ITEMS_PHOTO_URI;
    private static final String STREAM_ITEMS_PROJECTION[] = {
        "_id", "raw_contact_source_id", "stream_item_sync1", "timestamp", "stream_item_sync2"
    };
    public static final Uri STREAM_ITEMS_URI;
    private static final String THUMBNAILS_RAW_CONTACT_PROJECTION[] = {
        "_id", "sourceid", "data_id", "mimetype", "sync3"
    };
    private static AndroidContactsSyncThread sAndroidSyncThread;
    private static boolean sContactsProviderExists;
    private static boolean sContactsProviderStatusKnown;
    private static int sMaxStreamItemsPerRawContact;
    private static SparseArray sPhoneTypeMap;
    private static int sThumbnailSize;

    static 
    {
        PROFILE_CONTENT_RAW_CONTACTS_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "profile/raw_contacts");
        STREAM_ITEMS_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "stream_items");
        STREAM_ITEMS_CONTENT_LIMIT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "stream_items_limit");
        STREAM_ITEMS_PHOTO_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "stream_items/photo");
        DISPLAY_PHOTO_CONTENT_MAX_DIMENSIONS_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "photo_dimensions");
        SparseArray sparsearray = new SparseArray();
        sPhoneTypeMap = sparsearray;
        sparsearray.put(1, PHONE_TYPE_HOME);
        sPhoneTypeMap.put(2, PHONE_TYPE_WORK);
        sPhoneTypeMap.put(3, PHONE_TYPE_OTHER);
        sPhoneTypeMap.put(4, PHONE_TYPE_HOME_FAX);
        sPhoneTypeMap.put(5, PHONE_TYPE_WORK_FAX);
        sPhoneTypeMap.put(6, PHONE_TYPE_MOBILE);
        sPhoneTypeMap.put(7, PHONE_TYPE_PAGER);
        sPhoneTypeMap.put(8, PHONE_TYPE_OTHER_FAX);
        sPhoneTypeMap.put(9, PHONE_TYPE_COMPANY_MAIN);
        sPhoneTypeMap.put(10, PHONE_TYPE_ASSISTANT);
        sPhoneTypeMap.put(11, PHONE_TYPE_CAR);
        sPhoneTypeMap.put(12, PHONE_TYPE_RADIO);
        sPhoneTypeMap.put(13, PHONE_TYPE_ISDN);
        sPhoneTypeMap.put(14, PHONE_TYPE_CALLBACK);
        sPhoneTypeMap.put(15, PHONE_TYPE_TELEX);
        sPhoneTypeMap.put(16, PHONE_TYPE_TTY_TDD);
        sPhoneTypeMap.put(17, PHONE_TYPE_WORK_MOBILE);
        sPhoneTypeMap.put(18, PHONE_TYPE_WORK_PAGER);
        sPhoneTypeMap.put(19, PHONE_TYPE_MAIN);
    }
    
	private static DataState addData(RawContactState rawcontactstate, String s, String s1)
    {
        DataState datastate = new DataState();
        datastate.mimetype = s;
        datastate.data1 = s1;
        rawcontactstate.data.add(datastate);
        return datastate;
    }

    private static void appendImgTag(Context context, StringBuilder stringbuilder, int i)
    {
        stringbuilder.append("<img src='res://").append(context.getPackageName()).append('/').append(context.getResources().getResourceEntryName(i)).append("'/>");
    }

    private static void applyActivityChanges(Context context, EsAccount esaccount, HashMap hashmap)
    {
        ContentResolver contentresolver = context.getContentResolver();
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList(hashmap.values());
        int ai[] = {
            0
        };
        int i = arraylist1.size();
        int k;
        for(int j = 0; j < i; j = k)
        {
            k = j + 32;
            if(k > i)
                k = i;
            if(EsLog.isLoggable("GooglePlusContactsSync", 3))
            {
                for(int l = j; l < k; l++)
                    dumpPersonActivityState((PersonActivityState)arraylist1.get(l));

            }
            deleteObsoleteStreamItems(esaccount, arraylist1, j, k, arraylist);
            updateStreamItems(context, esaccount, arraylist1, j, k, arraylist, ai);
        }

        flushBatch(contentresolver, arraylist, true);
    }

    private static void buildContentProviderOperations(Context context, Uri uri, ArrayList arraylist, RawContactState rawcontactstate, boolean flag)
    {
        int i;
        DataState datastate;
        ContentProviderOperation contentprovideroperation;
        if(EsLog.isLoggable("GooglePlusContactsSync", 3))
            dumpRawContactState(rawcontactstate);
        android.content.ContentProviderOperation.Builder builder2;
        String as2[];
        if(rawcontactstate.rawContactId == 0L)
        {
            i = arraylist.size();
            arraylist.add(ContentProviderOperation.newInsert(uri).withYieldAllowed(true).withValue("sourceid", rawcontactstate.personId).withValue("sync1", Long.valueOf(rawcontactstate.lastUpdateTime)).withValue("raw_contact_is_read_only", Integer.valueOf(1)).build());
            arraylist.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", i).withValue("mimetype", "vnd.android.cursor.item/name").withValue("data1", rawcontactstate.fullName).build());
            if(!flag)
            {
                arraylist.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", i).withValue("mimetype", "vnd.android.cursor.item/vnd.googleplus.profile").withValue("data4", Integer.valueOf(10)).withValue("data5", "conversation").withValue("data3", context.getString(R.string.start_conversation_action_label)).build());
                arraylist.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", i).withValue("mimetype", "vnd.android.cursor.item/vnd.googleplus.profile").withValue("data4", Integer.valueOf(14)).withValue("data5", "hangout").withValue("data3", context.getString(R.string.start_hangout_action_label)).build());
                arraylist.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", i).withValue("mimetype", "vnd.android.cursor.item/vnd.googleplus.profile").withValue("data4", Integer.valueOf(20)).withValue("data5", "addtocircle").withValue("data3", context.getString(R.string.add_to_circle_action_label)).build());
            }
            arraylist.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", i).withValue("mimetype", "vnd.android.cursor.item/vnd.googleplus.profile").withValue("data4", Integer.valueOf(30)).withValue("data5", "view").withValue("data3", context.getString(R.string.view_profile_action_label)).build());
            String s = EsPeopleData.extractGaiaId(rawcontactstate.personId);
            if(s != null)
                arraylist.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", i).withValue("mimetype", "vnd.android.cursor.item/identity").withValue("data2", AccountsUtil.ACCOUNT_TYPE).withValue("data1", (new StringBuilder("gprofile:")).append(s).toString()).build());
        } else
        {
            android.content.ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(uri).withYieldAllowed(true);
            String as[] = new String[1];
            as[0] = String.valueOf(rawcontactstate.rawContactId);
            arraylist.add(builder.withSelection("_id=?", as).withValue("sync1", Long.valueOf(rawcontactstate.lastUpdateTime)).build());
            i = 0;
        }
        for(Iterator iterator = rawcontactstate.data.iterator(); iterator.hasNext();) {
            datastate = (DataState)iterator.next();
            if(!datastate.exists) {
            	builder2 = ContentProviderOperation.newDelete(android.provider.ContactsContract.Data.CONTENT_URI);
                as2 = new String[1];
                as2[0] = String.valueOf(datastate.dataId);
                contentprovideroperation = builder2.withSelection("_id=?", as2).build();
            } else {
            	android.content.ContentProviderOperation.Builder builder1;
            	if(0L == datastate.dataId) {
            		builder1 = ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI);
            		builder1.withValue("mimetype", datastate.mimetype);
	                if(rawcontactstate.rawContactId == 0L)
	                    builder1.withValueBackReference("raw_contact_id", i);
	                else
	                    builder1.withValue("raw_contact_id", Long.valueOf(rawcontactstate.rawContactId));
            	} else if(datastate.changed) {
            		builder1 = ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI);
                    String as1[] = new String[1];
                    as1[0] = String.valueOf(datastate.dataId);
                    builder1.withSelection("_id=?", as1);
            	} else {
            		continue;
            	}
            	
            	builder1.withValue("data1", datastate.data1);
                builder1.withValue("data2", datastate.data2);
                builder1.withValue("data3", datastate.data3);
                contentprovideroperation = builder1.build();
            }
            
            arraylist.add(contentprovideroperation);
        }
    }

    private static void buildContentProviderOperations(Context context, Uri uri, ArrayList arraylist, String as[], HashMap hashmap)
    {
        for(int i = 0; i < as.length; i++)
            buildContentProviderOperations(context, uri, arraylist, (RawContactState)hashmap.get(as[i]), false);

    }

    private static synchronized void cancelSync() {
        if(sAndroidSyncThread != null)
            sAndroidSyncThread.cancel();
    }

    private static void cleanUpActivityStateMap(HashMap hashmap)
    {
        Iterator iterator = (new ArrayList(hashmap.values())).iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            PersonActivityState personactivitystate = (PersonActivityState)iterator.next();
            Iterator iterator1 = personactivitystate.activities.iterator();
            boolean flag1;
            do
            {
                boolean flag = iterator1.hasNext();
                flag1 = false;
                if(!flag)
                    break;
                ActivityState activitystate = (ActivityState)iterator1.next();
                if((!activitystate.exists || activitystate.streamItemId != 0L) && (activitystate.exists || activitystate.streamItemId == 0L) && !activitystate.changed)
                    continue;
                flag1 = true;
                break;
            } while(true);
            if(!flag1)
                hashmap.remove(personactivitystate.gaiaId);
        } while(true);
    }

    private static void clearAndroidCircles(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        syncstate.onStart("Android:DeleteCircles");
        syncstate.incrementCount(context.getContentResolver().delete(getGroupsUri(esaccount), null, null));
        syncstate.onFinish();
    }

    private static void clearAndroidContacts(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        syncstate.onStart("Android:DeleteContacts");
        deleteAndroidContacts(context, getRawContactsUri(esaccount), null, null, syncstate);
        syncstate.onFinish();
    }

    private static void clearAndroidContactsForOtherAccounts(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        Cursor cursor = null;
        String as[] = new String[1];
        String s;
        long l;
        if(esaccount != null)
            s = esaccount.getName();
        else
            s = "";
        as[0] = s;
        syncstate.onStart("Android:DeleteProfilesOtherAccounts");
        deleteAndroidContacts(context, PROFILE_CONTENT_RAW_CONTACTS_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build(), "data_set='plus' AND account_name!=? AND account_type='com.google'", as, syncstate);
        syncstate.onFinish();
        syncstate.onStart("Android:DeleteContactsOtherAccounts");
        deleteAndroidContacts(context, android.provider.ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build(), "data_set='plus' AND account_name!=? AND account_type='com.google'", as, syncstate);
        syncstate.onFinish();
        syncstate.onStart("Android:DeleteCirclesOtherAccounts");
        Uri uri = android.provider.ContactsContract.Groups.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build();
        try {
	        cursor = context.getContentResolver().query(uri, new String[] {
	            "_id"
	        }, "data_set='plus' AND account_name!=? AND account_type='com.google'", as, null);
	        if(null != cursor) {
	        	 for(; cursor.moveToNext(); syncstate.incrementCount(context.getContentResolver().delete(ContentUris.withAppendedId(uri, l), null, null)))
	                 l = cursor.getLong(0);
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        	 syncstate.onFinish();
        }
    }

    private static void clearAndroidProfile(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        long l = 0L;
        Cursor cursor = null;
        syncstate.onStart("Android:DeleteProfile");
        ContentResolver contentresolver = context.getContentResolver();
        try {
        	cursor = contentresolver.query(getProfileRawContactUri(esaccount), PROFILE_RAW_CONTACT_PROJECTION, null, null, null);
        	if(null != cursor && cursor.moveToFirst()) {
        		l = cursor.getLong(0);
        	}
        	if(l != 0L)
                syncstate.incrementCount(contentresolver.delete(ContentUris.withAppendedId(getRawContactsUri(esaccount), l), null, null));
            syncstate.onFinish();
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static void collectRawContactIds(Context context, HashSet hashset, Uri uri, String as[], String s, String s1)
    {
        Cursor cursor = null;
        try {
        	cursor = context.getContentResolver().query(uri, as, s, null, s1);
        	if(null != cursor) {
        		for(; cursor.moveToNext(); hashset.add(cursor.getString(0)));
        	}
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public static void deactivateAccount(Context context, EsAccount esaccount)
    {
        if(isAndroidSyncSupported(context))
        {
            cancelSync();
            EsAccountsData.saveContactsSyncCleanupStatus(context, false);
            EsAccountsData.saveContactsStatsSyncCleanupStatus(context, false);
            clearAndroidProfile(context, esaccount, new EsSyncAdapterService.SyncState());
            requestSync(context);
        }
    }

    private static void deleteAndroidContacts(Context context, Uri uri, String s, String as[], EsSyncAdapterService.SyncState syncstate)
    {
        Cursor cursor = null;
        ContentResolver contentresolver = context.getContentResolver();
        ArrayList arraylist = new ArrayList();
        try {
	        cursor = contentresolver.query(uri, new String[] {
	            "_id"
	        }, s, as, null);
	        if(null != cursor) {
	        	for(; cursor.moveToNext(); arraylist.add(cursor.getString(0)));
	        }
	        if(!arraylist.isEmpty())
	        {
	            ArrayList arraylist1 = new ArrayList();
	            for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); syncstate.incrementCount())
	            {
	                String s1 = (String)iterator.next();
	                arraylist1.add(ContentProviderOperation.newDelete(uri).withSelection("_id=?", new String[] {
	                    s1
	                }).withYieldAllowed(true).build());
	                flushBatch(contentresolver, arraylist1, false);
	            }

	            flushBatch(contentresolver, arraylist1, true);
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static void deleteContacts(ContentResolver contentresolver, EsAccount esaccount, ArrayList arraylist, ArrayList arraylist1)
    {
        Uri uri = getRawContactsUri(esaccount);
        android.content.ContentProviderOperation.Builder builder;
        String as[];
        for(Iterator iterator = arraylist1.iterator(); iterator.hasNext(); arraylist.add(builder.withSelection("_id=?", as).build()))
        {
            RawContactState rawcontactstate = (RawContactState)iterator.next();
            if(EsLog.isLoggable("GooglePlusContactsSync", 3))
                dumpRawContactState(rawcontactstate);
            builder = ContentProviderOperation.newDelete(uri).withYieldAllowed(true);
            as = new String[1];
            as[0] = String.valueOf(rawcontactstate.rawContactId);
        }

        flushBatch(contentresolver, arraylist, false);
    }
    
    private static void deleteObsoleteStreamItems(EsAccount esaccount, ArrayList arraylist, int i, int j, ArrayList arraylist1)
    {
    	ActivityState activitystate;
        Uri uri = getStreamItemsUri(esaccount);
        for(int k = i; k < j; k++)
        {
            PersonActivityState personactivitystate1 = (PersonActivityState)arraylist.get(k);
            if(!TextUtils.equals(personactivitystate1.gaiaId, esaccount.getGaiaId()))
                continue;
            for(Iterator iterator1 = personactivitystate1.activities.iterator(); iterator1.hasNext();) {
            	activitystate = (ActivityState)iterator1.next();
            	if(activitystate.exists || activitystate.streamItemId == 0L) {
            		continue;
            	} else {
            		arraylist1.add(ContentProviderOperation.newDelete(ContentUris.withAppendedId(uri, activitystate.streamItemId)).build());
            		break;
            	}
            }
        }

        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("_id IN (");
        ArrayList arraylist2 = new ArrayList();
        
        for(int l = i; l < j; l++)
        {
            PersonActivityState personactivitystate = (PersonActivityState)arraylist.get(l);
            if(TextUtils.equals(personactivitystate.gaiaId, esaccount.getGaiaId()))
                continue;
            for(Iterator iterator = personactivitystate.activities.iterator(); iterator.hasNext();) {
            	activitystate = (ActivityState)iterator.next();
            	if(activitystate.exists || activitystate.streamItemId == 0L) {
            		continue;
            	} else {
            		if(!arraylist2.isEmpty())
                        stringbuilder.append(',');
                    stringbuilder.append('?');
                    arraylist2.add(String.valueOf(activitystate.streamItemId));
                    break;
            	}
            }
        }

        if(!arraylist2.isEmpty())
        {
            stringbuilder.append(')');
            arraylist1.add(ContentProviderOperation.newDelete(uri).withSelection(stringbuilder.toString(), (String[])arraylist2.toArray(new String[0])).build());
        }
    }

    private static void deleteRemovedContacts(ContentResolver contentresolver, EsAccount esaccount, HashMap hashmap, ArrayList arraylist)
    {
        ArrayList arraylist1 = new ArrayList();
        Iterator iterator = hashmap.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            RawContactState rawcontactstate = (RawContactState)iterator.next();
            if(!rawcontactstate.exists)
                arraylist1.add(rawcontactstate);
        } while(true);
        if(!arraylist1.isEmpty())
            deleteContacts(contentresolver, esaccount, arraylist, arraylist1);
    }

    private static void downloadLargeAvatars(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, HashMap hashmap, int i)
    {
        ContentResolver contentresolver = context.getContentResolver();
        ArrayList arraylist = new ArrayList(hashmap.values());
        int j = arraylist.size();
        int k = 0;
        do
        {
            if(k >= j || syncstate.isCanceled())
                break;
            int l = k + 8;
            if(l > j)
                l = j;
            int i1 = k;
            while(i1 < l) 
            {
                AvatarState avatarstate = (AvatarState)arraylist.get(i1);
                if(avatarstate.signature == 1)
                    saveAvatarSignature(contentresolver, esaccount, avatarstate);
                else
                if(avatarstate.avatarUrl != null)
                {
                    DownloadImageOperation downloadimageoperation = new DownloadImageOperation(context, esaccount, new AvatarImageRequest(avatarstate.gaiaId, EsAvatarData.uncompressAvatarUrl(avatarstate.avatarUrl), 2, i), false, null, null);
                    downloadimageoperation.start();
                    if(downloadimageoperation.hasError())
                        downloadimageoperation.logError("GooglePlusContactsSync");
                    byte abyte0[] = downloadimageoperation.getImageBytes();
                    if(abyte0 != null)
                    {
                        Uri uri = Uri.withAppendedPath(ContentUris.withAppendedId(getRawContactsUri(esaccount), avatarstate.rawContactId), "display_photo");
                        try
                        {
                            OutputStream outputstream = contentresolver.openOutputStream(uri);
                            outputstream.write(abyte0);
                            outputstream.close();
                            saveAvatarSignature(contentresolver, esaccount, avatarstate);
                        }
                        catch(IOException ioexception)
                        {
                            if(EsLog.isLoggable("GooglePlusContactsSync", 6))
                                Log.e("GooglePlusContactsSync", (new StringBuilder("Could not store large avatar: ")).append(avatarstate.gaiaId).toString(), ioexception);
                        }
                    }
                }
                i1++;
            }
            k = l;
        } while(true);
    }

    private static void dumpPersonActivityState(PersonActivityState personactivitystate)
    {
    	String s;
    	ActivityState activitystate;
        Log.d("GooglePlusContactsSync", (new StringBuilder("[STREAM] Gaia ID: ")).append(personactivitystate.gaiaId).append("  (raw_contact_id=").append(personactivitystate.rawContactId).append(")").toString());
        for(Iterator iterator = personactivitystate.activities.iterator(); iterator.hasNext();) {
        	s = null;
        	activitystate = (ActivityState)iterator.next();
            if(!activitystate.exists && activitystate.streamItemId != 0L)
            {
                s = "[DELETE]";
            } else if(activitystate.exists && activitystate.streamItemId == 0L)
            {
                s = "[INSERT]";
            } else if(activitystate.changed) {
            	s = "[UPDATE]";
            }
            if(null != s) {
            	Log.d("GooglePlusContactsSync", (new StringBuilder("    ")).append(s).append(" ").append(activitystate.activityId).append(" (stream_item_id=").append(activitystate.streamItemId).append(") created=").append(activitystate.created).append("', modified=").append(activitystate.lastModified).toString());
        
            }
        }
    }

    private static void dumpRawContactState(RawContactState rawcontactstate)
    {
        DataState datastate;
        String s1;
        String s;
        if(!rawcontactstate.exists)
            s = "[DELETE]";
        else
        if(rawcontactstate.rawContactId == 0L)
            s = "[INSERT]";
        else
            s = "[UPDATE]";
        Log.d("GooglePlusContactsSync", (new StringBuilder()).append(s).append(" ").append(rawcontactstate.personId).append(" ").append(rawcontactstate.fullName).append(" (raw_contact_id=").append(rawcontactstate.rawContactId).append(") last_updated=").append(rawcontactstate.lastUpdateTime).toString());
        
        for(Iterator iterator = rawcontactstate.data.iterator(); iterator.hasNext();) {
        	s1 = null;
        	datastate = (DataState)iterator.next();
            if(!datastate.exists)
            {
                s1 = "[DELETE]";
            } else if(datastate.dataId == 0)
            {
                s1 = "[INSERT]";
            } else if(datastate.changed) {
            	s1 = "[UPDATE]";
            }
            if(null != s1) {
            	Log.d("GooglePlusContactsSync", (new StringBuilder("    ")).append(s1).append(" ").append(datastate.mimetype).append(" (data_id=").append(datastate.dataId).append(") '").append(datastate.data1).append("', type=").append(datastate.data2).append(" ").append(datastate.data3).toString());
            }
        }
    }

    private static HashMap findChangesInCircles(Context context, EsAccount esaccount)
    {
        HashMap hashmap = new HashMap();
        Cursor cursor = null;
        try {
        	cursor = context.getContentResolver().query(getGroupsUri(esaccount), GROUPS_PROJECTION, null, null, null);
        	if(null != cursor) {
        		CircleState circlestate;
                for(; cursor.moveToNext(); hashmap.put(circlestate.circleId, circlestate))
                {
                    circlestate = new CircleState();
                    circlestate.groupId = cursor.getLong(0);
                    circlestate.circleId = cursor.getString(1);
                    circlestate.circleName = cursor.getString(2);
                }
        	}
        	
        	if((CircleState)hashmap.get("plus") == null)
            {
                CircleState circlestate1 = new CircleState();
                circlestate1.circleId = "plus";
                circlestate1.circleName = context.getString(R.string.android_contact_group);
                circlestate1.exists = true;
                hashmap.put("plus", circlestate1);
            } else
            {
                hashmap.remove("plus");
            }
        	return hashmap;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static HashMap findChangesInContacts(Context context, EsAccount esaccount)
    {
    	HashMap hashmap = new HashMap();
        Cursor cursor = null;
    	Cursor cursor1 = null;
        ContentResolver contentresolver = context.getContentResolver();
        try {
        	cursor = contentresolver.query(getRawContactsUri(esaccount), RAW_CONTACT_PROJECTION, null, null, null);
        	if(null != cursor) {
        		 RawContactState rawcontactstate;
    	        for(; cursor.moveToNext(); hashmap.put(rawcontactstate.personId, rawcontactstate))
    	        {
    	            rawcontactstate = new RawContactState();
    	            rawcontactstate.rawContactId = cursor.getLong(0);
    	            rawcontactstate.personId = cursor.getString(1);
    	            rawcontactstate.lastUpdateTime = cursor.getLong(2);
    	        }
        	}
        	
        	cursor1 = contentresolver.query(EsProvider.appendAccountParameter(EsProvider.CONTACTS_URI, esaccount), CONTACTS_PROJECTION, "in_my_circles=1 AND profile_type!=2 AND for_sharing!=0", null, null);
        	if(null != cursor1) {
        		String s;
                long l;
                RawContactState rawcontactstate1;
                while(cursor1.moveToNext()) {
                	s = cursor1.getString(0);
                    l = cursor1.getLong(1);
                    rawcontactstate1 = (RawContactState)hashmap.get(s);
                    if(null == rawcontactstate1) {
                    	rawcontactstate1 = new RawContactState();
                        rawcontactstate1.personId = s;
                        rawcontactstate1.lastUpdateTime = l;
                        hashmap.put(rawcontactstate1.personId, rawcontactstate1);
                    } else {
                    	if(rawcontactstate1.lastUpdateTime == l)
                            hashmap.remove(s);
                        else
                            rawcontactstate1.lastUpdateTime = l;
                    }
                    rawcontactstate1.exists = true;
                }
        	} else {
        		hashmap.remove(esaccount.getPersonId());
        	}
        	return hashmap;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        	if(null != cursor1) {
        		cursor1.close();
        	}
        }
    }

    private static ContentProviderResult[] flushBatch(ContentResolver contentresolver, ArrayList arraylist, int i, boolean flag)
    {
        int size = arraylist.size();
        if(0 == size) {
        	return null;
        }
        
        if(!flag)
        {
            if(size < i)
                return null;
        }
        
        try {
        	return contentresolver.applyBatch("com.android.contacts", arraylist);
        } catch (Exception exception) {
        	if(EsLog.isLoggable("GooglePlusContactsSync", 6)) {
        		Log.e("GooglePlusContactsSync", "Cannot apply a batch of content provider operations", exception);
        	}
        	return null;
        } finally {
        	arraylist.clear();
        }
    }

    private static ContentProviderResult[] flushBatch(ContentResolver contentresolver, ArrayList arraylist, boolean flag)
    {
        return flushBatch(contentresolver, arraylist, 128, flag);
    }

    private static HashMap getCircleIdMap(ContentResolver contentresolver, EsAccount esaccount)
    {
        HashMap hashmap = new HashMap();
        Cursor cursor = null;
        try {
        	cursor = contentresolver.query(getGroupsUri(esaccount), GROUPS_PROJECTION, null, null, null);
        	for(; cursor.moveToNext(); hashmap.put(cursor.getString(1), Long.valueOf(cursor.getLong(0))));
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        return hashmap;
    }

    private static Uri getEntitiesUri(EsAccount esaccount)
    {
        return android.provider.ContactsContract.RawContactsEntity.CONTENT_URI.buildUpon().appendQueryParameter("account_type", AccountsUtil.ACCOUNT_TYPE).appendQueryParameter("data_set", "plus").appendQueryParameter("account_name", esaccount.getName()).appendQueryParameter("caller_is_syncadapter", "true").build();
    }

    private static Uri getGroupsUri(EsAccount esaccount)
    {
        return android.provider.ContactsContract.Groups.CONTENT_URI.buildUpon().appendQueryParameter("account_type", AccountsUtil.ACCOUNT_TYPE).appendQueryParameter("data_set", "plus").appendQueryParameter("account_name", esaccount.getName()).appendQueryParameter("caller_is_syncadapter", "true").build();
    }

    private static String[] getLimitedRawContactSet(Context context, EsAccount esaccount)
    {
        HashSet hashset = new HashSet();
        Uri uri = android.provider.ContactsContract.Data.CONTENT_URI.buildUpon().appendQueryParameter("account_type", AccountsUtil.ACCOUNT_TYPE).appendQueryParameter("data_set", "plus").appendQueryParameter("account_name", esaccount.getName()).appendQueryParameter("caller_is_syncadapter", "true").build();
        String as[] = {
            "raw_contact_id"
        };
        collectRawContactIds(context, hashset, uri, as, "starred!=0 AND mimetype='vnd.android.cursor.item/vnd.googleplus.profile'", null);
        collectRawContactIds(context, hashset, uri, as, "starred=0 AND times_contacted>0 AND mimetype='vnd.android.cursor.item/vnd.googleplus.profile'", "times_contacted DESC LIMIT 8");
        collectRawContactIds(context, hashset, uri, as, "starred=0 AND last_time_contacted>0 AND mimetype='vnd.android.cursor.item/vnd.googleplus.profile'", "last_time_contacted DESC LIMIT 8");
        return (String[])hashset.toArray(new String[0]);
    }

    private static int getMaxStreamItemsPerRawContact(Context context)
    {
        if(sMaxStreamItemsPerRawContact != 0) {
        	return sMaxStreamItemsPerRawContact;
        }
        
        Cursor cursor = null;
        try {
        	cursor = context.getContentResolver().query(STREAM_ITEMS_CONTENT_LIMIT_URI, new String[] {
                "max_items"
            }, null, null, null);
        	if(cursor.moveToFirst())
            {
                sMaxStreamItemsPerRawContact = cursor.getInt(0);
                return sMaxStreamItemsPerRawContact;
            } else {
            	return 2;
            }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static int getPreferredAvatarSize(ContentResolver contentresolver, String s)
    {
        Cursor cursor = null;
        try {
        	cursor = contentresolver.query(DISPLAY_PHOTO_CONTENT_MAX_DIMENSIONS_URI, null, null, null, null);
        	if(null != cursor && cursor.moveToFirst()) {
        		return cursor.getInt(cursor.getColumnIndex(s));
        	} else {
        		return 96;
        	}
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static Uri getProfileRawContactUri(EsAccount esaccount)
    {
        return PROFILE_CONTENT_RAW_CONTACTS_URI.buildUpon().appendQueryParameter("account_type", AccountsUtil.ACCOUNT_TYPE).appendQueryParameter("data_set", "plus").appendQueryParameter("account_name", esaccount.getName()).appendQueryParameter("caller_is_syncadapter", "true").build();
    }

    private static Uri getRawContactsUri(EsAccount esaccount)
    {
        return android.provider.ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_type", AccountsUtil.ACCOUNT_TYPE).appendQueryParameter("data_set", "plus").appendQueryParameter("account_name", esaccount.getName()).appendQueryParameter("caller_is_syncadapter", "true").build();
    }

    private static Uri getStreamItemsUri(EsAccount esaccount)
    {
        return STREAM_ITEMS_URI.buildUpon().appendQueryParameter("account_type", AccountsUtil.ACCOUNT_TYPE).appendQueryParameter("data_set", "plus").appendQueryParameter("account_name", esaccount.getName()).appendQueryParameter("caller_is_syncadapter", "true").build();
    }

    private static void insertNewContacts(Context context, EsAccount esaccount, HashMap hashmap, ArrayList arraylist, HashMap hashmap1)
    {
        ArrayList arraylist1 = new ArrayList();
        Iterator iterator = hashmap.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            RawContactState rawcontactstate = (RawContactState)iterator.next();
            if(rawcontactstate.exists && rawcontactstate.rawContactId == 0L)
                arraylist1.add(rawcontactstate);
        } while(true);
        if(!arraylist1.isEmpty())
            updateContacts(context, esaccount, arraylist, arraylist1, hashmap, true, hashmap1);
    }

    public static boolean isAndroidSyncSupported(Context context)
    {
        boolean flag;
        if(android.os.Build.VERSION.SDK_INT < 14)
            flag = false;
        else
            flag = isContactsProviderAvailable(context);
        return flag;
    }

    public static boolean isContactsProviderAvailable(Context context)
    {
        boolean flag = true;
        if(sContactsProviderStatusKnown) { 
        	return sContactsProviderExists; 
        } else {
        	ContentProviderClient contentproviderclient = null;
        	try {
        		contentproviderclient = context.getContentResolver().acquireContentProviderClient("com.android.contacts");
                if(contentproviderclient == null)
                    flag = false;
                sContactsProviderExists = flag;
                sContactsProviderStatusKnown = true;
        	} catch (Throwable throwable) {
        		Log.e("GooglePlusContactsSync", "Cannot determine availability of the contacts provider");
        	} finally {
        		if(contentproviderclient != null)
                    contentproviderclient.release();
        	}
        	return sContactsProviderExists;
        }
    }

    private static void limitStreamItemsPerRawContact(Context context, HashMap hashmap)
    {
        int i = getMaxStreamItemsPerRawContact(context);
        Iterator iterator = hashmap.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            ArrayList arraylist = ((PersonActivityState)iterator.next()).activities;
            if(arraylist.size() > i)
            {
                Collections.sort(arraylist, ACTIVITY_STATE_COMPARATOR);
                int j = i;
                while(j < arraylist.size()) 
                {
                    ((ActivityState)arraylist.get(j)).exists = false;
                    j++;
                }
            }
        } while(true);
    }

    private static void loadContactCircleMembership(String as[], HashMap hashmap, HashMap hashmap1)
    {
        for(int i = 0; i < as.length; i++)
        {
            RawContactState rawcontactstate = (RawContactState)hashmap.get(as[i]);
            Long long1 = (Long)hashmap1.get("plus");
            if(long1 != null)
                addData(rawcontactstate, "vnd.android.cursor.item/group_membership", long1.toString()).exists = true;
        }

    }

    private static void populateRawContactState(Context context, RawContactState rawcontactstate, EsPeopleData.ContactInfo contactinfo)
    {
        if(null != contactinfo.emails) {
        	for(Iterator iterator2 = contactinfo.emails.iterator(); iterator2.hasNext();) {
        		 DataEmail dataemail = (DataEmail)iterator2.next();
                 DataState datastate2 = addData(rawcontactstate, "vnd.android.cursor.item/email_v2", dataemail.value);
                 datastate2.exists = true;
                 Integer integer2 = dataemail.standardTag;
                 String s3 = dataemail.customTag;
                 int j;
                 if(integer2 == null)
                     j = 0;
                 else
                     j = integer2.intValue();
                 switch(j)
                 {
                 default:
                     datastate2.data2 = EMAIL_TYPE_CUSTOM;
                     datastate2.data3 = s3;
                     break;

                 case 1: // '\001'
                     datastate2.data2 = EMAIL_TYPE_HOME;
                     break;

                 case 2: // '\002'
                     datastate2.data2 = EMAIL_TYPE_WORK;
                     break;

                 case 3: // '\003'
                     datastate2.data2 = EMAIL_TYPE_OTHER;
                     break;
                 }
        	}
        }
        
        if(contactinfo.phones != null)
        {
            for(Iterator iterator1 = contactinfo.phones.iterator(); iterator1.hasNext();)
            {
                DataPhone dataphone = (DataPhone)iterator1.next();
                DataState datastate1 = addData(rawcontactstate, "vnd.android.cursor.item/phone_v2", dataphone.value);
                datastate1.exists = true;
                Integer integer1 = dataphone.standardTag;
                String s1 = dataphone.customTag;
                String s2;
                if(integer1 == null)
                    s2 = null;
                else
                    s2 = (String)sPhoneTypeMap.get(integer1.intValue());
                if(s2 != null)
                    datastate1.data2 = s2;
                else
                if(integer1 != null && integer1.intValue() == 20)
                {
                    datastate1.data2 = PHONE_TYPE_CUSTOM;
                    datastate1.data3 = context.getString(R.string.profile_item_phone_google_voice);
                } else
                {
                    datastate1.data2 = PHONE_TYPE_CUSTOM;
                    datastate1.data3 = s1;
                }
            }

        }
        
        if(null != contactinfo.addresses) {
        	for(Iterator iterator = contactinfo.addresses.iterator(); iterator.hasNext();) {
        		DataCircleMemberPropertiesAddress datacirclememberpropertiesaddress = (DataCircleMemberPropertiesAddress)iterator.next();
                DataState datastate = addData(rawcontactstate, "vnd.android.cursor.item/postal-address_v2", datacirclememberpropertiesaddress.value);
                datastate.exists = true;
                Integer integer = datacirclememberpropertiesaddress.standardTag;
                String s = datacirclememberpropertiesaddress.customTag;
                int i;
                if(integer == null)
                    i = 0;
                else
                    i = integer.intValue();
                switch(i)
                {
                default:
                    datastate.data2 = POSTAL_TYPE_CUSTOM;
                    datastate.data3 = s;
                    break;

                case 1: // '\001'
                    datastate.data2 = POSTAL_TYPE_HOME;
                    break;

                case 2: // '\002'
                    datastate.data2 = POSTAL_TYPE_WORK;
                    break;

                case 3: // '\003'
                    datastate.data2 = POSTAL_TYPE_OTHER;
                    break;
                }
        	}
        }
    }

    private static void populateRawContactState(RawContactState rawcontactstate, Contacts contacts)
    {
        if(contacts.email != null)
        {
            Iterator iterator2 = contacts.email.iterator();
            do
            {
                if(!iterator2.hasNext())
                    break;
                TaggedEmail taggedemail = (TaggedEmail)iterator2.next();
                DataState datastate2 = addData(rawcontactstate, "vnd.android.cursor.item/email_v2", taggedemail.value);
                datastate2.exists = true;
                if(taggedemail.tag != null)
                {
                    ContactTag contacttag2 = taggedemail.tag;
                    if("HOME".equals(contacttag2.tag))
                        datastate2.data2 = EMAIL_TYPE_HOME;
                    else
                    if("WORK".equals(contacttag2.tag))
                        datastate2.data2 = EMAIL_TYPE_WORK;
                    else
                    if("OTHER".equals(contacttag2.tag))
                        datastate2.data2 = EMAIL_TYPE_OTHER;
                    else
                    if("CUSTOM".equals(contacttag2.tag))
                    {
                        datastate2.data2 = EMAIL_TYPE_CUSTOM;
                        datastate2.data3 = contacttag2.customTag;
                    }
                }
            } while(true);
        }
        if(contacts.phone != null)
        {
            Iterator iterator1 = contacts.phone.iterator();
            do
            {
                if(!iterator1.hasNext())
                    break;
                TaggedPhone taggedphone = (TaggedPhone)iterator1.next();
                DataState datastate1 = addData(rawcontactstate, "vnd.android.cursor.item/phone_v2", taggedphone.value);
                datastate1.exists = true;
                if(taggedphone.tag != null)
                {
                    ContactTag contacttag1 = taggedphone.tag;
                    if("HOME".equals(contacttag1.tag))
                        datastate1.data2 = PHONE_TYPE_HOME;
                    else
                    if("WORK".equals(contacttag1.tag))
                        datastate1.data2 = PHONE_TYPE_WORK;
                    else
                    if("OTHER".equals(contacttag1.tag))
                        datastate1.data2 = PHONE_TYPE_OTHER;
                    else
                    if("CUSTOM".equals(contacttag1.tag))
                    {
                        datastate1.data2 = PHONE_TYPE_CUSTOM;
                        datastate1.data3 = contacttag1.customTag;
                    }
                }
            } while(true);
        }
        if(contacts.address != null)
        {
            Iterator iterator = contacts.address.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                TaggedAddress taggedaddress = (TaggedAddress)iterator.next();
                DataState datastate = addData(rawcontactstate, "vnd.android.cursor.item/postal-address_v2", taggedaddress.value);
                datastate.exists = true;
                if(taggedaddress.tag != null)
                {
                    ContactTag contacttag = taggedaddress.tag;
                    if("HOME".equals(contacttag.tag))
                        datastate.data2 = POSTAL_TYPE_HOME;
                    else
                    if("WORK".equals(contacttag.tag))
                        datastate.data2 = POSTAL_TYPE_WORK;
                    else
                    if("OTHER".equals(contacttag.tag))
                        datastate.data2 = POSTAL_TYPE_OTHER;
                    else
                    if("CUSTOM".equals(contacttag.tag))
                    {
                        datastate.data2 = POSTAL_TYPE_CUSTOM;
                        datastate.data3 = contacttag.customTag;
                    }
                }
            } while(true);
        }
    }

    private static HashMap queryRawContactsRequiringActivities(Context context, EsAccount esaccount)
    {
        String as[] = getLimitedRawContactSet(context, esaccount);
        int i = as.length;
        HashMap hashmap = new HashMap();
        if(0 == i) {
        	return hashmap;
        }
        
        Cursor cursor = null;
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("_id IN (");
        for(int j = 0; j < as.length; j++)
        {
            if(j != 0)
                stringbuilder.append(',');
            stringbuilder.append('?');
        }

        stringbuilder.append(')');
        
        try {
        	cursor = context.getContentResolver().query(getRawContactsUri(esaccount), RAW_CONTACT_SOURCE_ID_PROJECTION, stringbuilder.toString(), as, null);
        	do
            {
                if(!cursor.moveToNext())
                    break;
                String s = EsPeopleData.extractGaiaId(cursor.getString(1));
                if(s != null)
                {
                    PersonActivityState personactivitystate = new PersonActivityState();
                    personactivitystate.gaiaId = s;
                    personactivitystate.rawContactId = cursor.getLong(0);
                    hashmap.put(s, personactivitystate);
                }
            } while(true);
        	return hashmap;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static HashMap queryRawContactsRequiringLargeAvatars(Context context, EsAccount esaccount)
    {
        HashMap hashmap = new HashMap();
        String as[] = getLimitedRawContactSet(context, esaccount);
        int i = as.length;
        hashmap = null;
        if(0 == i) {
        	return hashmap;
        }
        
        Cursor cursor = null;
        ContentResolver contentresolver = context.getContentResolver();
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("_id IN (");
        for(int j = 0; j < as.length; j++)
        {
            if(j != 0)
                stringbuilder.append(',');
            stringbuilder.append('?');
        }

        stringbuilder.append(')');
        stringbuilder.append(" OR sync2").append(" != 0");
        
        try {
        	cursor = contentresolver.query(getRawContactsUri(esaccount), LARGE_AVATAR_RAW_CONTACTS_PROJECTION, stringbuilder.toString(), as, null);
        	if(null != cursor) {
        		do
                {
                    if(!cursor.moveToNext())
                        break;
                    String s = EsPeopleData.extractGaiaId(cursor.getString(1));
                    if(s != null)
                    {
                        AvatarState avatarstate = new AvatarState();
                        avatarstate.gaiaId = s;
                        avatarstate.rawContactId = cursor.getLong(0);
                        avatarstate.signature = cursor.getInt(2);
                        hashmap.put(s, avatarstate);
                    }
                } while(true);
        	}
        	
        	if(!hashmap.isEmpty())
                retrieveAvatarSignatures(context, esaccount, hashmap, new ArrayList(hashmap.keySet()));
        	return hashmap;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static HashMap queryRawContactsRequiringThumbnails(ContentResolver contentresolver, EsAccount esaccount)
    {
        Cursor cursor = null;
        Cursor cursor1 = null;
        HashMap hashmap = new HashMap();
        try {
        	cursor = contentresolver.query(getEntitiesUri(esaccount), THUMBNAILS_RAW_CONTACT_PROJECTION, "(mimetype='vnd.android.cursor.item/photo' OR mimetype='vnd.android.cursor.item/vnd.googleplus.profile') AND (sync2=0 OR sync2 IS NULL)", null, null);
        	if(null != cursor) {
        		do
                {
                    if(!cursor.moveToNext())
                        break;
                    String s2 = EsPeopleData.extractGaiaId(cursor.getString(1));
                    if(s2 != null)
                    {
                        AvatarState avatarstate1 = (AvatarState)hashmap.get(s2);
                        if(avatarstate1 == null)
                        {
                            avatarstate1 = new AvatarState();
                            avatarstate1.gaiaId = s2;
                            avatarstate1.rawContactId = cursor.getLong(0);
                            avatarstate1.signature = cursor.getInt(4);
                            hashmap.put(s2, avatarstate1);
                        }
                        if("vnd.android.cursor.item/photo".equals(cursor.getString(3)))
                            avatarstate1.dataId = cursor.getLong(2);
                    }
                } while(true);
        	}
        	
        	cursor1 = contentresolver.query(EsProvider.appendAccountParameter(EsProvider.CONTACTS_URI, esaccount), AVATAR_URL_PROJECTION, "in_my_circles=1", null, null);
        	if(null != cursor1) {
        		String s;
        		String s1;
                int i;
                AvatarState avatarstate;
        		while(cursor1.moveToNext()) {
        			s = cursor1.getString(0);
        	        s1 = cursor1.getString(1);
        	        i = EsAvatarData.getAvatarUrlSignature(s1);
        	        avatarstate = (AvatarState)hashmap.get(s);
        	        if(avatarstate == null) {
        	        	continue;
        	        } else if(avatarstate.signature == i) {
        	            hashmap.remove(s);
        	        } else {
        	        	avatarstate.signature = i;
        	            avatarstate.avatarUrl = s1;
        	        }
        		}
        	}
        	return hashmap;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        	if(null != cursor1) {
        		cursor1.close();
        	}
        }
    }

    private static boolean queryStreamItemState(Context context, HashMap hashmap, Uri uri, String s, String as[]) {
    	
    	Cursor cursor = null;
    	try {
	    	cursor = context.getContentResolver().query(uri, STREAM_ITEMS_PROJECTION, s, as, null);
	    	if(null == cursor) {
	    		return false;
	    	}
	    	do
	        {
	            if(!cursor.moveToNext())
	                break;
	            String s1 = EsPeopleData.extractGaiaId(cursor.getString(1));
	            if(s1 != null)
	            {
	                PersonActivityState personactivitystate = (PersonActivityState)hashmap.get(s1);
	                if(personactivitystate != null)
	                {
	                    ActivityState activitystate = new ActivityState();
	                    activitystate.rawContactId = personactivitystate.rawContactId;
	                    activitystate.streamItemId = cursor.getLong(0);
	                    activitystate.activityId = cursor.getString(2);
	                    activitystate.created = cursor.getLong(3);
	                    activitystate.lastModified = cursor.getLong(4);
	                    personactivitystate.activities.add(activitystate);
	                }
	            }
	        } while(true);
	    	return true;
    	} finally {
    		if(null != cursor) {
        		cursor.close();
        	}
    	}
    }

    private static void reconcileActivitiesAndStreamItems(Context context, EsAccount esaccount, HashMap hashmap, String s, String as[])
    {
        Cursor cursor = null;
        String s3 = null;
        long l = 0L;
        long l1 = 0L;
        PersonActivityState personactivitystate = null;
        ActivityState activitystate;
        ContentResolver contentresolver = context.getContentResolver();
        String s1;
        String s2;
        Iterator iterator;
        boolean flag;
        ActivityState activitystate1;
        if(s != null)
            s1 = (new StringBuilder("(")).append(s).append(")").toString();
        else
            s1 = null;
        
        try {
        	cursor = contentresolver.query(EsProvider.appendAccountParameter(EsProvider.ACTIVITY_SUMMARY_URI, esaccount), ACTIVITY_SUMMARY_PROJECTION, s1, as, null);
	        do
	        {
	            if(!cursor.moveToNext())
	                break;
	            s2 = cursor.getString(1);
	            s3 = cursor.getString(0);
	            l = cursor.getLong(2);
	            l1 = cursor.getLong(3);
	            personactivitystate = (PersonActivityState)hashmap.get(s2);
	        } while(personactivitystate == null);
	        if(null != personactivitystate) {
	        	iterator = personactivitystate.activities.iterator();
	        	do
	            {
	                flag = iterator.hasNext();
	                activitystate = null;
	                if(!flag)
	                    break;
	                activitystate1 = (ActivityState)iterator.next();
	                if(!s3.equals(activitystate1.activityId))
	                    continue;
	                activitystate = activitystate1;
	                break;
	            } while(true);
	        	
	        	 if(activitystate == null) {
	        		 activitystate = new ActivityState();
	        	     activitystate.rawContactId = personactivitystate.rawContactId;
	        	     activitystate.activityId = s3;
	        	     activitystate.created = l;
	        	     activitystate.lastModified = l1;
	        	     personactivitystate.activities.add(activitystate);
	        	 } else {
	        		 if(activitystate.lastModified != l1)
		             {
		                 activitystate.changed = true;
		                 activitystate.lastModified = l1;
		             }
	        	 }
	        	 activitystate.exists = true;
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static boolean reconcileContacts(ContentResolver contentresolver, Uri uri, String as[], HashMap hashmap)
    {
        Cursor cursor = null;
        String as1[] = new String[as.length];
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("_id IN (");
        for(int i = 0; i < as.length; i++)
        {
            as1[i] = String.valueOf(((RawContactState)hashmap.get(as[i])).rawContactId);
            if(i != 0)
                stringbuilder.append(',');
            stringbuilder.append('?');
        }

        stringbuilder.append(") AND mimetype IN ('vnd.android.cursor.item/email_v2','vnd.android.cursor.item/phone_v2','vnd.android.cursor.item/postal-address_v2','vnd.android.cursor.item/group_membership')");
        try {
        	cursor = contentresolver.query(uri, ENTITIES_PROJECTION, stringbuilder.toString(), as1, null);
        	if(null == cursor) {
        		return false;
        	}
        	for(; cursor.moveToNext(); reconcileData((RawContactState)hashmap.get(cursor.getString(0)), cursor.getLong(2), cursor.getString(1), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        	return true;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static void reconcileData(RawContactState rawcontactstate, long l, String s, String s1, String s2, String s3)
    {
        if(TextUtils.isEmpty(s1))
            s1 = null;
        if(TextUtils.isEmpty(s2))
            s2 = null;
        if(TextUtils.isEmpty(s3))
            s3 = null;
        
        DataState datastate = null;
        for(Iterator iterator = rawcontactstate.data.iterator(); iterator.hasNext();) {
        	datastate = (DataState)iterator.next();
            if(!TextUtils.equals(datastate.mimetype, s) || !TextUtils.equals(datastate.data1, s1) || datastate.dataId != 0L) 
            	continue; 
            else {
            	datastate.dataId = l;
                if(!TextUtils.equals(datastate.data2, s2) || !TextUtils.equals(datastate.data3, s3))
                {
                    datastate.data2 = s2;
                    datastate.data3 = s3;
                    datastate.changed = true;
                }
                return;
            }
        }
        
        DataState datastate1 = new DataState();
        datastate1.dataId = l;
        rawcontactstate.data.add(datastate1);
    }

    public static synchronized void requestSync(Context context)
    {
        requestSync(context, false);
    }

    public static synchronized void requestSync(Context context, boolean flag)
    {
        boolean flag1 = isAndroidSyncSupported(context);
        if(!flag1) 
        	return;
        if(sAndroidSyncThread == null) {
        	AndroidContactsSyncThread androidcontactssyncthread = new AndroidContactsSyncThread(context.getApplicationContext());
            sAndroidSyncThread = androidcontactssyncthread;
            androidcontactssyncthread.start();
        } else {
        	sAndroidSyncThread.requestSync(flag);
        }
    }

    private static byte[] resizeThumbnail(byte abyte0[], int i)
    {
        if(abyte0 == null)
        {
            abyte0 = null;
        } else
        {
            Bitmap bitmap = ImageUtils.decodeByteArray(abyte0, 0, abyte0.length);
            if(bitmap == null)
                abyte0 = null;
            else
            if(bitmap.getWidth() <= i && bitmap.getHeight() <= i)
            {
                bitmap.recycle();
            } else
            {
                Bitmap bitmap1 = ImageUtils.resizeToSquareBitmap(bitmap, i);
                bitmap.recycle();
                if(bitmap1 == null)
                {
                    abyte0 = null;
                } else
                {
                    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(4000);
                    bitmap1.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
                    bitmap1.recycle();
                    abyte0 = bytearrayoutputstream.toByteArray();
                }
            }
        }
        return abyte0;
    }

    private static void retrieveAvatarSignatures(Context context, EsAccount esaccount, HashMap hashmap, ArrayList arraylist)
    {
        Cursor cursor = null;
        String as[] = new String[arraylist.size()];
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("gaia_id IN (");
        for(int i = 0; i < as.length; i++)
        {
            if(i != 0)
                stringbuilder.append(',');
            stringbuilder.append('?');
            as[i] = ((String)arraylist.get(i)).toString();
        }

        stringbuilder.append(")");
        
        try {
        	cursor = context.getContentResolver().query(EsProvider.appendAccountParameter(EsProvider.CONTACTS_URI, esaccount), AVATAR_URL_PROJECTION, stringbuilder.toString(), as, null);
        	if(null != cursor) {
        		String s;
        		String s1;
                int j;
                AvatarState avatarstate;
                while(cursor.moveToNext()) {
                	s = cursor.getString(0);
                    s1 = cursor.getString(1);
                    j = EsAvatarData.getAvatarUrlSignature(s1);
                    avatarstate = (AvatarState)hashmap.get(s);
                    if(null != avatarstate) {
	                    if(avatarstate.signature == j)
	                    {
	                        hashmap.remove(s);
	                        continue;
	                    } else {
	                    	avatarstate.signature = j;
	                        avatarstate.avatarUrl = s1;
	                    }
                    }
                }
        	}
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static void saveAvatarSignature(ContentResolver contentresolver, EsAccount esaccount, AvatarState avatarstate)
    {
        Uri uri = ContentUris.withAppendedId(getRawContactsUri(esaccount), avatarstate.rawContactId);
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("sync2", Integer.valueOf(avatarstate.signature));
        contentvalues.put("sync3", Integer.valueOf(avatarstate.signature));
        contentresolver.update(uri, contentvalues, null, null);
    }

    private static boolean shouldSync(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        boolean flag;
        if(isAndroidSyncSupported(context) && EsAccountsData.isContactsSyncEnabled(context, esaccount) && (syncstate == null || !syncstate.isCanceled()))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private static void syncActivitiesForRawContact(Context context, EsAccount esaccount, long l, String s)
    {
        HashMap hashmap = new HashMap();
        PersonActivityState personactivitystate = new PersonActivityState();
        personactivitystate.gaiaId = s;
        personactivitystate.rawContactId = l;
        hashmap.put(s, personactivitystate);
        if(queryStreamItemState(context, hashmap, Uri.withAppendedPath(ContentUris.withAppendedId(getRawContactsUri(esaccount), l), "stream_items"), null, null))
        {
            reconcileActivitiesAndStreamItems(context, esaccount, hashmap, "author_id=?", new String[] {
                s
            });
            limitStreamItemsPerRawContact(context, hashmap);
            cleanUpActivityStateMap(hashmap);
            if(!hashmap.isEmpty())
                applyActivityChanges(context, esaccount, hashmap);
        }
    }

    protected static void syncContactsForCurrentAccount(Context context, EsSyncAdapterService.SyncState syncstate)
    {
        // TODO
    }

    public static void syncRawContact(Context context, Uri uri)
    {
    	EsAccount esaccount = EsAccountsData.getActiveAccount(context);
        if(esaccount != null && isAndroidSyncSupported(context))
            syncRawContact(context, esaccount, uri);
    }

    private static void syncRawContact(Context context, EsAccount esaccount, Uri uri)
    {
        // TODO
    }

    private static void syncSmallAvatars(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        if(shouldSync(context, esaccount, syncstate))
        {
            syncstate.onStart("Android:Avatars");
            ContentResolver contentresolver = context.getContentResolver();
            HashMap hashmap = queryRawContactsRequiringThumbnails(contentresolver, esaccount);
            if(hashmap == null || hashmap.isEmpty())
            {
                syncstate.onFinish();
            } else
            {
                int i = EsAvatarData.getMediumAvatarSize(context);
                ArrayList arraylist = new ArrayList();
                ArrayList arraylist1 = new ArrayList(hashmap.values());
                int j = arraylist1.size();
                int l;
                for(int k = 0; k < j && !syncstate.isCanceled(); k = l)
                {
                    l = k + 8;
                    if(l > j)
                        l = j;
                    int i1 = k;
                    while(i1 < l) 
                    {
                        AvatarState avatarstate = (AvatarState)arraylist1.get(i1);
                        if(avatarstate.avatarUrl == null)
                            continue;
                        AvatarImageRequest avatarimagerequest = new AvatarImageRequest(avatarstate.gaiaId, EsAvatarData.uncompressAvatarUrl(avatarstate.avatarUrl), 2, i);
                        byte abyte0[] = EsMediaCache.getMedia(context, avatarimagerequest);
                        if(abyte0 == null)
                        {
                            DownloadImageOperation downloadimageoperation = new DownloadImageOperation(context, esaccount, avatarimagerequest, null, null);
                            downloadimageoperation.start();
                            abyte0 = downloadimageoperation.getImageBytes();
                        }
                        if(abyte0 != null)
                        {
                            if(sThumbnailSize == 0)
                                sThumbnailSize = getPreferredAvatarSize(context.getContentResolver(), "thumbnail_max_dim");
                            android.content.ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(getRawContactsUri(esaccount)).withYieldAllowed(true);
                            String as[] = new String[1];
                            as[0] = String.valueOf(avatarstate.rawContactId);
                            arraylist.add(builder.withSelection("_id=?", as).withValue("sync3", Integer.valueOf(avatarstate.signature)).build());
                            if(avatarstate.dataId != 0L)
                            {
                                android.content.ContentProviderOperation.Builder builder1 = ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI);
                                String as1[] = new String[1];
                                as1[0] = String.valueOf(avatarstate.dataId);
                                arraylist.add(builder1.withSelection("_id=?", as1).withValue("data15", resizeThumbnail(abyte0, sThumbnailSize)).build());
                            } else
                            {
                                arraylist.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI).withValue("raw_contact_id", Long.valueOf(avatarstate.rawContactId)).withValue("mimetype", "vnd.android.cursor.item/photo").withValue("data15", resizeThumbnail(abyte0, sThumbnailSize)).build());
                            }
                        }
                        i1++;
                    }
                    flushBatch(contentresolver, arraylist, 8, false);
                }

                flushBatch(contentresolver, arraylist, true);
                syncstate.onFinish(j);
            }
        }
    }

    private static void updateChangedContacts(Context context, EsAccount esaccount, HashMap hashmap, ArrayList arraylist, HashMap hashmap1)
    {
        ArrayList arraylist1 = new ArrayList();
        arraylist1.clear();
        Iterator iterator = hashmap.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            RawContactState rawcontactstate = (RawContactState)iterator.next();
            if(rawcontactstate.exists && rawcontactstate.rawContactId != 0L)
                arraylist1.add(rawcontactstate);
        } while(true);
        if(!arraylist1.isEmpty())
            updateContacts(context, esaccount, arraylist, arraylist1, hashmap, false, hashmap1);
    }

    private static void updateContacts(Context context, EsAccount esaccount, ArrayList arraylist, ArrayList arraylist1, HashMap hashmap, boolean flag, HashMap hashmap1)
    {
        // TODO
    }

    private static void updateMyProfile(Context context, EsAccount esaccount, String s, long l, byte abyte0[])
    {
        if(!isAndroidSyncSupported(context)) 
        	return;
        // TODO
    }

    private static void updateStreamItems(Context context, EsAccount esaccount, ArrayList arraylist, int i, int j, ArrayList arraylist1, int ai[])
    {
        // TODO
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private static final class ActivityState
    {

        String activityId;
        boolean changed;
        long created;
        boolean exists;
        long lastModified;
        long rawContactId;
        long streamItemId;
    }

    private static final class AndroidContactsSyncThread extends Thread {
    	
    	private final Context mContext;
        private volatile EsSyncAdapterService.SyncState mSyncState;
        private volatile Handler mThreadHandler;

        public AndroidContactsSyncThread(Context context)
        {
            mSyncState = new EsSyncAdapterService.SyncState();
            mContext = context;
            setName("AndroidContactsSync");
        }

        private void syncContactsForCurrentAccount()
        {
        	try {
	            Context context = mContext;
	            mSyncState.cancel();
	            mSyncState = new EsSyncAdapterService.SyncState();
	            AndroidContactsSync.syncContactsForCurrentAccount(context, mSyncState);
        	} catch (Throwable throwable) {
        		Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), throwable);
        	}
        }

        public final void cancel()
        {
            mSyncState.cancel();
            if(mThreadHandler != null)
                mThreadHandler.removeMessages(0);
        }

        public final void requestSync(boolean flag)
        {
            mSyncState.cancel();
            if(mThreadHandler != null)
            {
                mThreadHandler.removeMessages(0);
                Handler handler = mThreadHandler;
                long l;
                if(flag)
                    l = 500L;
                else
                    l = 5000L;
                handler.sendEmptyMessageDelayed(0, l);
            }
        }

        public final void run()
        {
            android.os.Process.setThreadPriority(19);
            Looper.prepare();
            mThreadHandler = new Handler() {

                public final void handleMessage(Message message)
                {
                    syncContactsForCurrentAccount();
                }
            };
            syncContactsForCurrentAccount();
            Looper.loop();
        }

    }

    private static final class AvatarState {

        String avatarUrl;
        long dataId;
        String gaiaId;
        long rawContactId;
        int signature;

        AvatarState()
        {
        }
    }

    private static final class CircleState
    {

        String circleId;
        String circleName;
        boolean exists;
        long groupId;

        CircleState()
        {
        }
    }

    private static final class DataState
    {

        boolean changed;
        String data1;
        String data2;
        String data3;
        long dataId;
        boolean exists;
        String mimetype;
    }

    private static final class ImageContainer
    {

        byte imageBytes[];
        int mediaIndex;
    }

    private static final class PersonActivityState
    {

        final ArrayList activities;
        String gaiaId;
        long rawContactId;

        PersonActivityState()
        {
            activities = new ArrayList();
        }
    }

    private static final class RawContactState
    {

        final ArrayList data;
        boolean exists;
        String fullName;
        long lastUpdateTime;
        String personId;
        long rawContactId;

        RawContactState()
        {
            data = new ArrayList();
        }
    }
}
