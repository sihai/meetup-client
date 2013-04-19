/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.analytics.AnalyticsInfo;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.AndroidNotification;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.EntitySquaresData;

/**
 * 
 * @author sihai
 *
 */
public class EsNotificationData {

	private static final Map MAP_CATEGORY;
    private static final Map MAP_ENTITY_TYPE;
    private static final Map MAP_NOTIFICATION_TYPE;
    private static final Object mSyncLock = new Object();

    static {
        MAP_CATEGORY = new HashMap();
        MAP_ENTITY_TYPE = new HashMap();
        MAP_NOTIFICATION_TYPE = new HashMap();
        MAP_CATEGORY.put("CIRCLE", Integer.valueOf(2));
        MAP_CATEGORY.put("ENTITYPROFILE", Integer.valueOf(9));
        MAP_CATEGORY.put("EVENTS", Integer.valueOf(10));
        MAP_CATEGORY.put("GAMES", Integer.valueOf(4));
        MAP_CATEGORY.put("GENERIC_CATEGORY", Integer.valueOf(65535));
        MAP_CATEGORY.put("HANGOUT", Integer.valueOf(8));
        MAP_CATEGORY.put("MOBILE", Integer.valueOf(7));
        MAP_CATEGORY.put("PHOTOS", Integer.valueOf(3));
        MAP_CATEGORY.put("QUESTIONS", Integer.valueOf(6));
        MAP_CATEGORY.put("SQUARE", Integer.valueOf(11));
        MAP_CATEGORY.put("STREAM", Integer.valueOf(1));
        MAP_CATEGORY.put("SYSTEM", Integer.valueOf(5));
        MAP_CATEGORY.put("TARGET", Integer.valueOf(12));
        MAP_ENTITY_TYPE.put("ACTIVITY", Integer.valueOf(1));
        MAP_ENTITY_TYPE.put("ALBUM", Integer.valueOf(7));
        MAP_ENTITY_TYPE.put("CIRCLE_SHARE", Integer.valueOf(8));
        MAP_ENTITY_TYPE.put("DEPRECATED_SYSTEM_TACO", Integer.valueOf(4));
        MAP_ENTITY_TYPE.put("EVENT", Integer.valueOf(9));
        MAP_ENTITY_TYPE.put("MATERIALIZED_TORTILLA", Integer.valueOf(5));
        MAP_ENTITY_TYPE.put("PHOTO", Integer.valueOf(2));
        MAP_ENTITY_TYPE.put("QUESTION", Integer.valueOf(3));
        MAP_ENTITY_TYPE.put("RESHARED", Integer.valueOf(6));
        MAP_ENTITY_TYPE.put("UNKNOWN_ENTITY_TYPE", Integer.valueOf(65535));
        MAP_NOTIFICATION_TYPE.put("ASPEN_INVITE", Integer.valueOf(74));
        MAP_NOTIFICATION_TYPE.put("BIRTHDAY_WISH", Integer.valueOf(63));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_CONTACT_JOINED", Integer.valueOf(69));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_DIGESTED_ADD", Integer.valueOf(40));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_EXPLICIT_INVITE", Integer.valueOf(32));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_INVITE_REQUEST", Integer.valueOf(8));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_INVITEE_JOINED_ES", Integer.valueOf(38));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_MEMBER_JOINED_ES", Integer.valueOf(9));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_PERSONAL_ADD", Integer.valueOf(6));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_RECIPROCATING_ADD", Integer.valueOf(39));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_RECOMMEND_PEOPLE", Integer.valueOf(66));
        MAP_NOTIFICATION_TYPE.put("CIRCLE_STATUS_CHANGE", Integer.valueOf(7));
        MAP_NOTIFICATION_TYPE.put("DIGEST_SWEEP", Integer.valueOf(70));
        MAP_NOTIFICATION_TYPE.put("ENTITYPROFILE_ADD_ADMIN", Integer.valueOf(34));
        MAP_NOTIFICATION_TYPE.put("ENTITYPROFILE_REMOVE_ADMIN", Integer.valueOf(35));
        MAP_NOTIFICATION_TYPE.put("ENTITYPROFILE_TRANSFER_OWNERSHIP", Integer.valueOf(36));
        MAP_NOTIFICATION_TYPE.put("EVENTS_BEFORE_REMINDER", Integer.valueOf(59));
        MAP_NOTIFICATION_TYPE.put("EVENTS_CHANGE", Integer.valueOf(53));
        MAP_NOTIFICATION_TYPE.put("EVENTS_CHECKIN", Integer.valueOf(58));
        MAP_NOTIFICATION_TYPE.put("EVENTS_INVITE", Integer.valueOf(47));
        MAP_NOTIFICATION_TYPE.put("EVENTS_INVITEE_CHANGE", Integer.valueOf(57));
        MAP_NOTIFICATION_TYPE.put("EVENTS_PHOTOS_ADDED", Integer.valueOf(62));
        MAP_NOTIFICATION_TYPE.put("EVENTS_PHOTOS_COLLECTION", Integer.valueOf(56));
        MAP_NOTIFICATION_TYPE.put("EVENTS_PHOTOS_REMINDER", Integer.valueOf(55));
        MAP_NOTIFICATION_TYPE.put("EVENTS_RSVP_CONFIRMATION", Integer.valueOf(67));
        MAP_NOTIFICATION_TYPE.put("EVENTS_STARTING", Integer.valueOf(54));
        MAP_NOTIFICATION_TYPE.put("GAMES_APPLICATION_MESSAGE", Integer.valueOf(12));
        MAP_NOTIFICATION_TYPE.put("GAMES_INVITE_REQUEST", Integer.valueOf(11));
        MAP_NOTIFICATION_TYPE.put("GAMES_ONEUP_NOTIFICATION", Integer.valueOf(73));
        MAP_NOTIFICATION_TYPE.put("GAMES_PERSONAL_MESSAGE", Integer.valueOf(17));
        MAP_NOTIFICATION_TYPE.put("HANGOUT_INVITE", Integer.valueOf(33));
        MAP_NOTIFICATION_TYPE.put("MOBILE_NEW_CONVERSATION", Integer.valueOf(29));
        MAP_NOTIFICATION_TYPE.put("PHOTOS_CAMERASYNC_UPLOADED", Integer.valueOf(18));
        MAP_NOTIFICATION_TYPE.put("PHOTOS_FACE_SUGGESTED", Integer.valueOf(41));
        MAP_NOTIFICATION_TYPE.put("PHOTOS_PROFILE_PHOTO_SUGGESTED", Integer.valueOf(68));
        MAP_NOTIFICATION_TYPE.put("PHOTOS_PROFILE_PHOTO_SUGGESTION_ACCEPTED", Integer.valueOf(71));
        MAP_NOTIFICATION_TYPE.put("PHOTOS_TAG_ADDED_ON_PHOTO", Integer.valueOf(13));
        MAP_NOTIFICATION_TYPE.put("PHOTOS_TAGGED_IN_PHOTO", Integer.valueOf(10));
        MAP_NOTIFICATION_TYPE.put("QUESTIONS_ANSWERER_FOLLOWUP", Integer.valueOf(30));
        MAP_NOTIFICATION_TYPE.put("QUESTIONS_ASKER_FOLLOWUP", Integer.valueOf(31));
        MAP_NOTIFICATION_TYPE.put("QUESTIONS_DASHER_WELCOME", Integer.valueOf(27));
        MAP_NOTIFICATION_TYPE.put("QUESTIONS_REFERRAL", Integer.valueOf(19));
        MAP_NOTIFICATION_TYPE.put("QUESTIONS_REPLY", Integer.valueOf(22));
        MAP_NOTIFICATION_TYPE.put("QUESTIONS_UNANSWERED_QUESTION", Integer.valueOf(28));
        MAP_NOTIFICATION_TYPE.put("SQUARE_ABUSE", Integer.valueOf(79));
        MAP_NOTIFICATION_TYPE.put("SQUARE_INVITE", Integer.valueOf(48));
        MAP_NOTIFICATION_TYPE.put("SQUARE_MEMBERSHIP_APPROVED", Integer.valueOf(51));
        MAP_NOTIFICATION_TYPE.put("SQUARE_MEMBERSHIP_REQUEST", Integer.valueOf(52));
        MAP_NOTIFICATION_TYPE.put("SQUARE_NAME_CHANGE", Integer.valueOf(72));
        MAP_NOTIFICATION_TYPE.put("SQUARE_NEW_MODERATOR", Integer.valueOf(65));
        MAP_NOTIFICATION_TYPE.put("SQUARE_SUBSCRIPTION", Integer.valueOf(49));
        MAP_NOTIFICATION_TYPE.put("STREAM_COMMENT_AT_REPLY", Integer.valueOf(15));
        MAP_NOTIFICATION_TYPE.put("STREAM_COMMENT_FOLLOWUP", Integer.valueOf(3));
        MAP_NOTIFICATION_TYPE.put("STREAM_COMMENT_FOR_PHOTO_TAGGED", Integer.valueOf(25));
        MAP_NOTIFICATION_TYPE.put("STREAM_COMMENT_FOR_PHOTO_TAGGER", Integer.valueOf(26));
        MAP_NOTIFICATION_TYPE.put("STREAM_COMMENT_NEW", Integer.valueOf(2));
        MAP_NOTIFICATION_TYPE.put("STREAM_COMMENT_ON_MENTION", Integer.valueOf(14));
        MAP_NOTIFICATION_TYPE.put("STREAM_LIKE", Integer.valueOf(4));
        MAP_NOTIFICATION_TYPE.put("STREAM_PLUSONE_COMMENT", Integer.valueOf(21));
        MAP_NOTIFICATION_TYPE.put("STREAM_PLUSONE_POST", Integer.valueOf(20));
        MAP_NOTIFICATION_TYPE.put("STREAM_POST_AT_REPLY", Integer.valueOf(16));
        MAP_NOTIFICATION_TYPE.put("STREAM_POST_FROM_UNCIRCLED", Integer.valueOf(61));
        MAP_NOTIFICATION_TYPE.put("STREAM_POST_SHARED", Integer.valueOf(24));
        MAP_NOTIFICATION_TYPE.put("STREAM_POST", Integer.valueOf(1));
        MAP_NOTIFICATION_TYPE.put("STREAM_POST_SUBSCRIBED", Integer.valueOf(64));
        MAP_NOTIFICATION_TYPE.put("STREAM_RESHARE", Integer.valueOf(5));
        MAP_NOTIFICATION_TYPE.put("SYSTEM_CELEBRITY_SUGGESTIONS", Integer.valueOf(45));
        MAP_NOTIFICATION_TYPE.put("SYSTEM_CONNECTED_SITES", Integer.valueOf(46));
        MAP_NOTIFICATION_TYPE.put("SYSTEM_DO_NOT_USE", Integer.valueOf(50));
        MAP_NOTIFICATION_TYPE.put("SYSTEM_FRIEND_SUGGESTIONS", Integer.valueOf(44));
        MAP_NOTIFICATION_TYPE.put("SYSTEM_INVITE", Integer.valueOf(37));
        MAP_NOTIFICATION_TYPE.put("SYSTEM_TOOLTIP", Integer.valueOf(43));
        MAP_NOTIFICATION_TYPE.put("SYSTEM_WELCOME", Integer.valueOf(42));
        MAP_NOTIFICATION_TYPE.put("TARGET_SHARED", Integer.valueOf(60));
        MAP_NOTIFICATION_TYPE.put("UNKNOWN_NOTIFICATION_TYPE", Integer.valueOf(0));
    }
    
    static void cleanupData(SQLiteDatabase sqlitedatabase) {
        StringBuffer stringbuffer;
        long l = EsDatabaseHelper.getRowsCount(sqlitedatabase, "notifications", null, null);
        if(EsLog.isLoggable("EsNotificationData", 4))
            Log.i("EsNotificationData", (new StringBuilder("deleteOldNotifications, keep count: ")).append(200L).append(", have: ").append(l).toString());
        if(l - 200L > 0L) {
        	Cursor cursor = null;
        	try {
	        	cursor = sqlitedatabase.query("notifications", NotificationIdsQuery.PROJECTION, null, null, null, null, "timestamp ASC", Long.toString(l - 200L));
	        	if(null != cursor) {
	        		if(cursor.moveToNext()) {
		        		StringBuilder sb = new StringBuilder(256);
		        		sb.append("notif_id IN(");
		        		sb.append('\'');
	        			sb.append(cursor.getString(0));
	        			sb.append('\'');
		        		while(cursor.moveToNext()) {
		        			sb.append(',');
		        			sb.append('\'');
		        			sb.append(cursor.getString(0));
		        			sb.append('\'');
		        		}
		        		sb.append(')');
		        		sqlitedatabase.delete("notifications", sb.toString(), null);
	        		}
	        	}
        	} finally {
        		if(null != cursor) {
        			cursor.close();
        		}
        	}
        }
    }
	
    public static void deactivateAccount(Context context, EsAccount esaccount) {
        AndroidNotification.cancelAll(context, esaccount);
        AndroidNotification.cancelQuotaNotification(context, esaccount);
        AndroidNotification.cancelFirstTimeFullSizeNotification(context, esaccount);
        //RealTimeChatNotifications.cancel(context, esaccount);
    }
    
    public static double getLatestNotificationTimestamp(Context context, EsAccount esaccount) {
    	double timestamp = -1D;
    	Cursor cursor = null;
    	try {
    		cursor = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("notifications", NotificationTimestampsQuery.PROJECTION, null, null, null, null, "timestamp DESC", "1");
    		if(cursor != null) {
    			if(cursor.moveToNext()) {
    				timestamp = cursor.getDouble(0);
    			}
    		}
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
    	return timestamp;
    }
    
    public static int getNotificationType(String s) {
        int i;
        if(!TextUtils.isEmpty(s) && MAP_NOTIFICATION_TYPE.containsKey(s))
            i = ((Integer)MAP_NOTIFICATION_TYPE.get(s)).intValue();
        else
            i = 0;
        return i;
    }

    public static Cursor getNotificationsToDisplay(Context context, EsAccount esaccount) {
        Cursor cursor = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("notifications", NotificationQuery.PROJECTION, "read=0 AND seen=0 AND enabled=1", null, null, null, "timestamp DESC");
        if(cursor != null && EsLog.isLoggable("EsNotificationData", 4)) {
            for(; cursor.moveToNext(); Log.i("EsNotificationData", (new StringBuilder("getNotificationsToDisplay: unread notification id: ")).append(cursor.getString(1)).append(", coalescingCode: ").append(cursor.getString(2)).append(", message: ").append(cursor.getString(4)).append(", timestamp: ").append(cursor.getLong(5)).toString()));
            cursor.moveToPosition(-1);
        }
        return cursor;
    }

    public static int getNumSquarePosts(EntitySquaresData entitysquaresdata) {
        int i;
        if(entitysquaresdata.subscription != null)
            i = entitysquaresdata.subscription.size();
        else
            i = 0;
        return i;
    }
    
    private static double getOldestUnreadNotificationTimestamp(Context context, EsAccount esaccount) {
    	double timestamp = -1D;
    	Cursor cursor = null;
    	try {
    		cursor = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("notifications", NotificationTimestampsQuery.PROJECTION, "read=0", null, null, null, "timestamp ASC", "1");
    		if(cursor != null) {
    			if(cursor.moveToNext()) {
    				timestamp = cursor.getDouble(0);
    			}
    		}
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
    	return timestamp;
    }

    public static String getSquarePostActivityId(EntitySquaresData entitysquaresdata, boolean flag) {
    	String id = null;
    	if(entitysquaresdata.subscription == null) {
    		return id;
    	}
    	
    	EntitySquaresDataSquareSubscription entitysquaresdatasquaresubscription;
    	for(int i = 0; i < entitysquaresdata.subscription.size(); i++) {
    		entitysquaresdatasquaresubscription = (EntitySquaresDataSquareSubscription)entitysquaresdata.subscription.get(i);
    		if((entitysquaresdatasquaresubscription == null || flag) && PrimitiveUtils.safeBoolean(entitysquaresdatasquaresubscription.isRead)) {
    			continue;
    		} else {
    			id = entitysquaresdatasquaresubscription.activityId;
    			break;
    		}
    	}
    	return id;
    }

    private static long getUnreadCount(SQLiteDatabase sqlitedatabase) {
        return sqlitedatabase.compileStatement(String.format("SELECT COUNT(*) FROM %s WHERE %s", new Object[] {
            "notifications", "read=0 AND seen=0 AND enabled=1"
        })).simpleQueryForLong();
    }

    public static int getUnreadSquarePosts(EntitySquaresData entitysquaresdata) {
        int i;
        if(entitysquaresdata.renderSquaresData != null && entitysquaresdata.renderSquaresData.renderSubscriptionData != null)
            i = PrimitiveUtils.safeInt(entitysquaresdata.renderSquaresData.renderSubscriptionData.numUnread);
        else
            i = 0;
        return i;
    }

    private static void insertNotifications(Context context, EsAccount esaccount, List list, double d, double d1, boolean flag,  Map map) throws IOException {
        
    	OzActions ozactions;
        OzViews ozviews;
        long l;
        Bundle bundle1;
        if(flag)
            ozactions = OzActions.NOTIFICATION_FETCHED_FROM_TICKLE;
        else
            ozactions = OzActions.NOTIFICATION_FETCHED_FROM_USER_REFRESH;
        if(flag)
            ozviews = OzViews.NOTIFICATIONS_SYSTEM;
        else
            ozviews = OzViews.NOTIFICATIONS_WIDGET;
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        sqlitedatabase.beginTransaction();
        l = getUnreadCount(sqlitedatabase);
        if(list.isEmpty()) {
        	if(d <= 0.0D)
                context.getContentResolver().notifyChange(EsProvider.appendAccountParameter(EsProvider.NOTIFICATIONS_URI, esaccount), null);
            sqlitedatabase.setTransactionSuccessful();
            bundle1 = new Bundle();
            bundle1.putInt("extra_num_unread_notifi", (int)l);
            bundle1.putInt("extra_prev_num_unread_noti", (int)l);
            EsAnalytics.postRecordEvent(context, esaccount, new AnalyticsInfo(ozviews), ozactions, bundle1);
            sqlitedatabase.endTransaction();
            return;
        }
    	
    	
    	
    	
    	/*
    	OzActions ozactions;
        OzViews ozviews;
        SQLiteDatabase sqlitedatabase;
        long l;
        Bundle bundle1;
        if(flag)
            ozactions = OzActions.NOTIFICATION_FETCHED_FROM_TICKLE;
        else
            ozactions = OzActions.NOTIFICATION_FETCHED_FROM_USER_REFRESH;
        if(flag)
            ozviews = OzViews.NOTIFICATIONS_SYSTEM;
        else
            ozviews = OzViews.NOTIFICATIONS_WIDGET;
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        sqlitedatabase.beginTransaction();
        l = getUnreadCount(sqlitedatabase);
        if(!list.isEmpty()) goto _L2; else goto _L1
_L1:
        if(d <= 0.0D)
            context.getContentResolver().notifyChange(EsProvider.appendAccountParameter(EsProvider.NOTIFICATIONS_URI, esaccount), null);
        sqlitedatabase.setTransactionSuccessful();
        bundle1 = new Bundle();
        bundle1.putInt("extra_num_unread_notifi", (int)l);
        bundle1.putInt("extra_prev_num_unread_noti", (int)l);
        EsAnalytics.postRecordEvent(context, esaccount, new AnalyticsInfo(ozviews), ozactions, bundle1);
        sqlitedatabase.endTransaction();
_L65:
        return;
_L2:
        HashMap hashmap;
        Cursor cursor;
        hashmap = new HashMap();
        cursor = sqlitedatabase.query("notifications", IdAndTimestampQuery.PROJECTION, null, null, null, null, null);
        while(cursor.moveToNext()) 
            hashmap.put(cursor.getString(0), Double.valueOf(cursor.getDouble(1)));
        break MISSING_BLOCK_LABEL_234;
        Exception exception1;
        exception1;
        cursor.close();
        throw exception1;
        Exception exception;
        exception;
        sqlitedatabase.endTransaction();
        throw exception;
        long l1;
        ArrayList arraylist;
        ArrayList arraylist1;
        ContentValues contentvalues;
        Iterator iterator;
        cursor.close();
        l1 = sqlitedatabase.compileStatement(String.format("SELECT MAX(%s) FROM %s", new Object[] {
            "timestamp", "notifications"
        })).simpleQueryForLong();
        arraylist = new ArrayList();
        arraylist1 = new ArrayList();
        contentvalues = new ContentValues();
        iterator = list.iterator();
_L6:
        if(!iterator.hasNext()) goto _L4; else goto _L3
_L3:
        DataCoalescedItem datacoalesceditem = (DataCoalescedItem)iterator.next();
        if(TextUtils.isEmpty(datacoalesceditem.id) || TextUtils.isEmpty(datacoalesceditem.coalescingCode)) goto _L6; else goto _L5
_L5:
        String s;
        String s1;
        double d2;
        boolean flag1;
        String s2;
        s = datacoalesceditem.id;
        s1 = datacoalesceditem.coalescingCode;
        d2 = PrimitiveUtils.safeDouble(datacoalesceditem.timestamp);
        flag1 = PrimitiveUtils.safeBoolean(datacoalesceditem.isEntityDeleted);
        s2 = datacoalesceditem.category;
        if(TextUtils.isEmpty(s2) || !MAP_CATEGORY.containsKey(s2)) goto _L8; else goto _L7
_L7:
        int i = ((Integer)MAP_CATEGORY.get(s2)).intValue();
_L66:
        String s3 = datacoalesceditem.entityReferenceType;
        if(TextUtils.isEmpty(s3) || !MAP_ENTITY_TYPE.containsKey(s3)) goto _L10; else goto _L9
_L9:
        int j = ((Integer)MAP_ENTITY_TYPE.get(s3)).intValue();
_L67:
        boolean flag2;
        Double double1;
        int k;
        boolean flag3;
        EntityEntityData entityentitydata;
        String s4;
        int i1;
        EntityUpdateData entityupdatedata;
        String s5;
        String s6;
        Long long1;
        EntityPhotosData entityphotosdata;
        EntitySquaresData entitysquaresdata;
        String s7;
        EntitySquaresDataSquareInvite entitysquaresdatasquareinvite;
        DataPhoto dataphoto;
        Long long2;
        EntityUpdateData entityupdatedata1;
        String s8;
        Update update;
        EmbedClientItem embedclientitem;
        PlusPhoto plusphoto;
        PlusPhoto plusphoto1;
        boolean flag5;
        Long long3;
        if(PrimitiveUtils.safeBoolean(datacoalesceditem.isRead) || d2 <= d1)
            flag2 = true;
        else
            flag2 = false;
        if(EsLog.isLoggable("EsNotificationData", 4))
        {
            String s9 = (new StringBuilder("Notification id: ")).append(s).append(", coalescingCode: ").append(s1).append(", category: ").append(datacoalesceditem.category).append(", filterType: ").append(datacoalesceditem.filterType).append(", timestamp: ").append(d2).append(", read: ").append(flag2).append(", entityDeleted: ").append(flag1).append(", pushEnabled: ").append(datacoalesceditem.pushEnabled).toString();
            if(datacoalesceditem.entityData != null && datacoalesceditem.entityData.summarySnippet != null)
                s9 = (new StringBuilder()).append(s9).append(", snippet: ").append(datacoalesceditem.entityData.summarySnippet.summaryPlaintext).toString();
            Log.i("EsNotificationData", s9);
        }
        double1 = (Double)hashmap.get(s);
        if(double1 == null || double1.doubleValue() != d2 || flag2) goto _L12; else goto _L11
_L11:
        if(EsLog.isLoggable("EsNotificationData", 4))
            Log.i("EsNotificationData", (new StringBuilder("Ignore notification with same timestamp and not read. Id: ")).append(s).toString());
          goto _L6
_L12:
        contentvalues.clear();
        contentvalues.put("notif_id", s);
        contentvalues.put("coalescing_code", s1);
        contentvalues.put("timestamp", Double.valueOf(d2));
        contentvalues.put("entity_type", Integer.valueOf(j));
        if(i == 1 && datacoalesceditem.entityReference != null)
            contentvalues.put("activity_id", datacoalesceditem.entityReference);
        List list1 = datacoalesceditem.action;
        k = 0;
        if(list1 != null)
        {
            int k1 = datacoalesceditem.action.size();
            k = 0;
            if(k1 > 0)
            {
                Iterator iterator2 = datacoalesceditem.action.iterator();
                do
                {
                    if(!iterator2.hasNext())
                        break;
                    DataAction dataaction = (DataAction)iterator2.next();
                    if(dataaction != null && dataaction.item != null && !dataaction.item.isEmpty())
                    {
                        ArrayList arraylist3 = new ArrayList();
                        Iterator iterator3 = dataaction.item.iterator();
                        do
                        {
                            if(!iterator3.hasNext())
                                break;
                            DataActor dataactor1 = ((DataItem)iterator3.next()).actor;
                            if(dataactor1 != null)
                            {
                                if(dataactor1.photoUrl != null)
                                    dataactor1.photoUrl = EsAvatarData.compressAvatarUrl(dataactor1.photoUrl);
                                arraylist3.add(dataactor1);
                                if(EsLog.isLoggable("EsNotificationData", 4))
                                    Log.i("EsNotificationData", (new StringBuilder("- Actor name: ")).append(dataactor1.name).append(" gaiaId: ").append(dataactor1.obfuscatedGaiaId).append(" photoUrl: ").append(dataactor1.photoUrl).toString());
                            }
                        } while(true);
                        k = getNotificationType(((DataItem)dataaction.item.get(0)).notificationType);
                    }
                } while(true);
                contentvalues.put("circle_data", DbDataAction.serializeDataActionList(datacoalesceditem.action));
            }
        }
        contentvalues.put("notification_type", Integer.valueOf(k));
        contentvalues.put("read", Boolean.valueOf(flag2));
        contentvalues.put("seen", Integer.valueOf(0));
        Iterator iterator1;
        List list2;
        if(datacoalesceditem.pushEnabled != null)
            contentvalues.put("enabled", datacoalesceditem.pushEnabled);
        else
            contentvalues.put("enabled", Boolean.valueOf(flag2));
        if(isEventNotificationType(k))
            i = 10;
          goto _L13
_L68:
        if(k == 18 && datacoalesceditem.opaqueClientFields != null)
        {
            iterator1 = datacoalesceditem.opaqueClientFields.iterator();
            do
            {
                if(!iterator1.hasNext())
                    break;
                DataKvPair datakvpair = (DataKvPair)iterator1.next();
                if(TextUtils.equals("TAGGEE_OGIDS", datakvpair.key) && !TextUtils.isEmpty(datakvpair.value))
                {
                    list2 = PhotoTaggeeData.createDataActorList(map, datakvpair.value);
                    if(!list2.isEmpty())
                        contentvalues.put("taggee_data_actors", DbDataAction.serializeDataActorList(list2));
                } else
                if(TextUtils.equals("TAGGEE_PHOTO_IDS", datakvpair.key) && !TextUtils.isEmpty(datakvpair.value))
                    contentvalues.put("taggee_photo_ids", datakvpair.value);
            } while(true);
        }
        entityentitydata = datacoalesceditem.entityData;
        s4 = null;
        if(entityentitydata == null) goto _L15; else goto _L14
_L14:
        EntitySummaryData entitysummarydata = datacoalesceditem.entityData.summarySnippet;
        s4 = null;
        if(entitysummarydata != null)
            s4 = datacoalesceditem.entityData.summarySnippet.summaryPlaintext;
        entityupdatedata = datacoalesceditem.entityData.update;
        s5 = null;
        s6 = null;
        long1 = null;
        if(entityupdatedata == null) goto _L17; else goto _L16
_L16:
        entityupdatedata1 = datacoalesceditem.entityData.update;
        if(entityupdatedata1.activity != null)
        {
            ArrayList arraylist2 = new ArrayList(1);
            arraylist2.add(entityupdatedata1.activity);
            EsPostsData.insertActivitiesAndOverwrite(context, esaccount, null, arraylist2, "DEFAULT");
        }
        if(TextUtils.isEmpty(entityupdatedata1.safeAnnotationHtml)) goto _L19; else goto _L18
_L18:
        s8 = entityupdatedata1.safeAnnotationHtml;
_L38:
        if(!TextUtils.isEmpty(s8))
            contentvalues.put("entity_snippet", s8);
        update = datacoalesceditem.entityData.update.activity;
        s5 = null;
        s6 = null;
        long1 = null;
        if(update == null) goto _L17; else goto _L20
_L20:
        embedclientitem = datacoalesceditem.entityData.update.activity.embed;
        s5 = null;
        s6 = null;
        long1 = null;
        if(embedclientitem == null) goto _L17; else goto _L21
_L21:
        plusphoto = datacoalesceditem.entityData.update.activity.embed.plusPhoto;
        s5 = null;
        s6 = null;
        long1 = null;
        if(plusphoto == null) goto _L17; else goto _L22
_L22:
        plusphoto1 = datacoalesceditem.entityData.update.activity.embed.plusPhoto;
        s5 = plusphoto1.ownerObfuscatedId;
        s6 = plusphoto1.albumId;
        flag5 = TextUtils.isEmpty(plusphoto1.photoId);
        long1 = null;
        if(flag5) goto _L17; else goto _L23
_L23:
        long3 = Long.valueOf(Long.parseLong(plusphoto1.photoId));
        long1 = long3;
_L17:
        entityphotosdata = datacoalesceditem.entityData.photos;
        if(i != 3 || entityphotosdata == null) goto _L25; else goto _L24
_L24:
        contentvalues.put("entity_photos_data", EntityPhotosDataJson.getInstance().toByteArray(entityphotosdata));
        if(entityphotosdata.photo == null || entityphotosdata.photo.isEmpty()) goto _L25; else goto _L26
_L26:
        dataphoto = (DataPhoto)entityphotosdata.photo.get(0);
        if(dataphoto == null) goto _L28; else goto _L27
_L27:
        if(TextUtils.isEmpty(dataphoto.id))
            break MISSING_BLOCK_LABEL_1776;
        long2 = Long.valueOf(Long.parseLong(dataphoto.id));
        long1 = long2;
_L47:
        if(dataphoto.album != null)
            s6 = dataphoto.album.id;
        if(dataphoto.owner != null)
            s5 = dataphoto.owner.id;
_L28:
        if(k == 18 && !flag2)
        {
            if(EsLog.isLoggable("EsNotificationData", 3))
            {
                int j1 = entityphotosdata.numPhotos.intValue() + entityphotosdata.numVideos.intValue();
                Log.d("EsNotificationData", (new StringBuilder("Insert ")).append(j1).append(" IU photos into the photo table!").toString());
            }
            EsPhotosDataApiary.insertStreamPhotos(context, esaccount, null, "camerasync", s5, entityphotosdata.photo, true);
        }
_L25:
        if(EsLog.isLoggable("EsNotificationData", 4) && (!TextUtils.isEmpty(s5) || !TextUtils.isEmpty(s6) || long1 != null))
            Log.i("EsNotificationData", (new StringBuilder("- Photo ownerId: ")).append(s5).append(" albumId: ").append(s6).append(" photoId: ").append(long1).toString());
        if(!TextUtils.isEmpty(s5))
            contentvalues.put("pd_gaia_id", s5);
        if(!TextUtils.isEmpty(s6))
            contentvalues.put("pd_album_id", s6);
        if(long1 != null)
            contentvalues.put("pd_photo_id", long1);
        if(datacoalesceditem.entityData.squares == null) goto _L30; else goto _L29
_L29:
        entitysquaresdata = datacoalesceditem.entityData.squares;
        contentvalues.put("entity_squares_data", EntitySquaresDataJson.getInstance().toByteArray(entitysquaresdata));
        if(entitysquaresdata.invite == null || entitysquaresdata.invite.size() <= 0) goto _L32; else goto _L31
_L31:
        entitysquaresdatasquareinvite = (EntitySquaresDataSquareInvite)entitysquaresdata.invite.get(0);
        if(entitysquaresdatasquareinvite == null || entitysquaresdatasquareinvite.square == null) goto _L32; else goto _L33
_L33:
        s7 = entitysquaresdatasquareinvite.square.oid;
_L51:
        if(EsLog.isLoggable("EsNotificationData", 3))
            Log.d("EsNotificationData", (new StringBuilder("- squareId: ")).append(s7).toString());
        if(!TextUtils.isEmpty(s7))
        {
            contentvalues.put("square_id", s7);
            DataActor dataactor = (DataActor)map.get(s7);
            if(dataactor != null)
            {
                contentvalues.put("square_name", dataactor.name);
                contentvalues.put("square_photo_url", EsAvatarData.compressAvatarUrl(dataactor.photoUrl));
            }
        }
_L30:
        if(datacoalesceditem.entityData.update == null || datacoalesceditem.entityData.update.activity == null || datacoalesceditem.entityData.update.activity.embed == null || datacoalesceditem.entityData.update.activity.embed.plusEvent == null) goto _L15; else goto _L34
_L34:
        PlusEvent plusevent = datacoalesceditem.entityData.update.activity.embed.plusEvent;
        contentvalues.put("ed_event_id", plusevent.id);
        contentvalues.put("ed_creator_id", plusevent.creatorObfuscatedId);
        if(58 == k && TextUtils.equals(plusevent.id, InstantUpload.getInstantShareEventId(context)))
            contentvalues.put("read", Boolean.valueOf(true));
          goto _L35
_L15:
        EntitySquaresDataSquareSubscription entitysquaresdatasquaresubscription;
        EntitySquaresDataSquareNameChange entitysquaresdatasquarenamechange;
        EntitySquaresDataNewModerator entitysquaresdatanewmoderator;
        EntitySquaresDataSquareMembershipRequest entitysquaresdatasquaremembershiprequest;
        EntitySquaresDataSquareMembershipApproved entitysquaresdatasquaremembershipapproved;
        NumberFormatException numberformatexception;
        NumberFormatException numberformatexception1;
        boolean flag4;
        EntityUpdateDataSummarySnippet entityupdatedatasummarysnippet;
        if(TextUtils.isEmpty(s4) || flag1)
            if(flag3)
                s4 = context.getString(com.google.android.apps.plus.R.string.notification_event_deleted);
            else
            if(i == 3)
                s4 = context.getString(com.google.android.apps.plus.R.string.notification_photo_deleted);
            else
                s4 = context.getString(com.google.android.apps.plus.R.string.notification_post_deleted);
        contentvalues.put("message", s4);
        if(!flag3)
            break MISSING_BLOCK_LABEL_3229;
        i1 = 1;
_L69:
        contentvalues.put("ed_event", Integer.valueOf(i1));
        contentvalues.put("category", Integer.valueOf(i));
        sqlitedatabase.insertWithOnConflict("notifications", "coalescing_code", contentvalues, 5);
        if(d2 > (double)l1)
        {
            arraylist.add(Integer.valueOf(k));
            arraylist1.add(s1);
        }
          goto _L6
_L19:
        if(TextUtils.isEmpty(entityupdatedata1.safeTitleHtml)) goto _L37; else goto _L36
_L36:
        s8 = entityupdatedata1.safeTitleHtml;
          goto _L38
_L37:
        if(entityupdatedata1.summary == null) goto _L40; else goto _L39
_L39:
        entityupdatedatasummarysnippet = entityupdatedata1.summary;
        if(TextUtils.isEmpty(entityupdatedatasummarysnippet.bodySanitizedHtml)) goto _L42; else goto _L41
_L41:
        s8 = entityupdatedatasummarysnippet.bodySanitizedHtml;
          goto _L38
_L42:
        if(TextUtils.isEmpty(entityupdatedatasummarysnippet.activityContentSanitizedHtml)) goto _L44; else goto _L43
_L43:
        s8 = entityupdatedatasummarysnippet.activityContentSanitizedHtml;
          goto _L38
_L44:
        if(TextUtils.isEmpty(entityupdatedatasummarysnippet.headerSanitizedHtml)) goto _L46; else goto _L45
_L45:
        s8 = entityupdatedatasummarysnippet.activityContentSanitizedHtml;
          goto _L38
        numberformatexception1;
        flag4 = EsLog.isLoggable("EsNotificationData", 6);
        long1 = null;
        if(flag4)
        {
            Log.e("EsNotificationData", (new StringBuilder("Invalid photoId ")).append(numberformatexception1).toString());
            long1 = null;
        }
          goto _L17
        numberformatexception;
        if(EsLog.isLoggable("EsNotificationData", 6))
            Log.e("EsNotificationData", (new StringBuilder("Invalid photoId ")).append(numberformatexception).toString());
          goto _L47
_L32:
        if(entitysquaresdata.membershipApproved == null || entitysquaresdata.membershipApproved.size() <= 0) goto _L49; else goto _L48
_L48:
        entitysquaresdatasquaremembershipapproved = (EntitySquaresDataSquareMembershipApproved)entitysquaresdata.membershipApproved.get(0);
        if(entitysquaresdatasquaremembershipapproved == null || entitysquaresdatasquaremembershipapproved.square == null) goto _L49; else goto _L50
_L50:
        s7 = entitysquaresdatasquaremembershipapproved.square.oid;
          goto _L51
_L49:
        if(entitysquaresdata.membershipRequest == null || entitysquaresdata.membershipRequest.size() <= 0) goto _L53; else goto _L52
_L52:
        entitysquaresdatasquaremembershiprequest = (EntitySquaresDataSquareMembershipRequest)entitysquaresdata.membershipRequest.get(0);
        if(entitysquaresdatasquaremembershiprequest == null || entitysquaresdatasquaremembershiprequest.square == null) goto _L53; else goto _L54
_L54:
        s7 = entitysquaresdatasquaremembershiprequest.square.oid;
          goto _L51
_L53:
        if(entitysquaresdata.newModerator == null || entitysquaresdata.newModerator.size() <= 0) goto _L56; else goto _L55
_L55:
        entitysquaresdatanewmoderator = (EntitySquaresDataNewModerator)entitysquaresdata.newModerator.get(0);
        if(entitysquaresdatanewmoderator == null) goto _L56; else goto _L57
_L57:
        s7 = entitysquaresdatanewmoderator.squareOid;
          goto _L51
_L56:
        if(entitysquaresdata.squareNameChange == null || entitysquaresdata.squareNameChange.size() <= 0) goto _L59; else goto _L58
_L58:
        entitysquaresdatasquarenamechange = (EntitySquaresDataSquareNameChange)entitysquaresdata.squareNameChange.get(0);
        if(entitysquaresdatasquarenamechange == null) goto _L59; else goto _L60
_L60:
        s7 = entitysquaresdatasquarenamechange.squareOid;
          goto _L51
_L59:
        if(entitysquaresdata.subscription == null || entitysquaresdata.subscription.size() <= 0) goto _L62; else goto _L61
_L61:
        entitysquaresdatasquaresubscription = (EntitySquaresDataSquareSubscription)entitysquaresdata.subscription.get(0);
        if(entitysquaresdatasquaresubscription == null || entitysquaresdatasquaresubscription.square == null) goto _L62; else goto _L63
_L63:
        s7 = entitysquaresdatasquaresubscription.square.oid;
          goto _L51
_L62:
        if(EsLog.isLoggable("EsNotificationData", 6))
            Log.e("EsNotificationData", (new StringBuilder("No Square ID in notification:\n")).append(EntitySquaresDataJson.getInstance().toPrettyString(entitysquaresdata)).toString());
          goto _L64
_L4:
        Bundle bundle = new Bundle();
        if(!arraylist.isEmpty() && arraylist.size() == arraylist1.size())
        {
            bundle.putIntegerArrayList("extra_notification_types", arraylist);
            bundle.putStringArrayList("extra_coalescing_codes", arraylist1);
        }
        bundle.putInt("extra_num_unread_notifi", (int)getUnreadCount(sqlitedatabase));
        bundle.putInt("extra_prev_num_unread_noti", (int)l);
        EsAnalytics.postRecordEvent(context, esaccount, new AnalyticsInfo(ozviews), ozactions, bundle);
        sqlitedatabase.setTransactionSuccessful();
        sqlitedatabase.endTransaction();
        context.getContentResolver().notifyChange(EsProvider.appendAccountParameter(EsProvider.NOTIFICATIONS_URI, esaccount), null);
          goto _L65
_L8:
        i = 65535;
          goto _L66
_L10:
        j = 65535;
          goto _L67
_L13:
        if(i == 10)
            flag3 = true;
        else
            flag3 = false;
          goto _L68
_L35:
        i = 10;
        flag3 = true;
          goto _L15
_L46:
        s8 = null;
          goto _L38
_L40:
        s8 = null;
          goto _L38
_L64:
        s7 = null;
          goto _L51
        i1 = 0;
          goto _L69
          */
    }
    
    public static boolean isCommentNotificationType(int i) {
    	
    	boolean flag = false;
    	switch(i) {
	    	case 2:
	    	case 3:
	    	case 14:
	    	case 15:
	    	case 25:
	    	case 26:
	    		 flag = true;
	    		 break;
	    	default:
	    		flag = false;
	    		break;
    	}
    	return flag;
    }

    public static boolean isEventNotificationType(int i) {
    	boolean flag = false;
    	switch(i) {
    	case 47:
    		flag = true;
    		break;
    	case 48:
    		flag = false;
    		break;
    	case 49:
    		flag = false;
    		break;
    	case 50:
    		flag = false;
    		break;
    	case 51:
    		flag = false;
    		break;
    	case 52:
    		flag = false;
    		break;
    	case 53:
    		flag = true;
    		break;
    	case 54:
    		flag = true;
    		break;
    	case 55:
    		flag = true;
    		break;
    	case 56:
    		flag = true;
    		break;
    	case 57:
    		flag = true;
    		break;
    	case 58:
    		flag = true;
    		break;
    	case 59:
    		flag = true;
    		break;
    	case 60:
    		flag = false;
    		break;
    	case 61:
    		flag = false;
    		break;
    	case 62:
    		flag = true;
    		break;
    	case 63:
    		flag = false;
    		break;
    	case 64:
    		flag = false;
    		break;
    	case 65:
    		flag = false;
    		break;
    	case 66:
    		flag = false;
    		break;
    	case 67:
    		flag = true;
    		break;
    	default:
    		flag = false;
    		break;
    	}
    	return flag;
    }

    public static void markAllNotificationsAsRead(Context context, EsAccount esaccount) {
        if(EsLog.isLoggable("EsNotificationData", 4))
            Log.i("EsNotificationData", "markAllNotificationsAsRead");
        if(esaccount == null) {
            Log.e("EsNotificationData", "markAllNotificationsAsRead: The account cannot be null");
        } else {
            SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
            ContentValues contentvalues = new ContentValues(1);
            contentvalues.put("read", Integer.valueOf(1));
            sqlitedatabase.update("notifications", contentvalues, null, null);
            context.getContentResolver().notifyChange(EsProvider.appendAccountParameter(EsProvider.NOTIFICATIONS_URI, esaccount), null);
            AndroidNotification.cancel(context, esaccount, 1);
        }
    }

    public static void markAllNotificationsAsSeen(Context context, EsAccount esaccount) {
        if(EsLog.isLoggable("EsNotificationData", 4))
            Log.i("EsNotificationData", "markAllNotificationsAsSeen");
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues(1);
        contentvalues.put("seen", Integer.valueOf(1));
        sqlitedatabase.update("notifications", contentvalues, null, null);
    }

    public static void markNotificationAsRead(Context context, EsAccount esaccount, String s) {
        if(EsLog.isLoggable("EsNotificationData", 4))
            Log.i("EsNotificationData", (new StringBuilder("markNotificationAsRead: ")).append(s).toString());
        if(esaccount == null) {
            Log.e("EsNotificationData", "markNotificationAsRead: The account cannot be null");
        } else {
            SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
            ContentValues contentvalues = new ContentValues(2);
            contentvalues.put("read", Integer.valueOf(1));
            sqlitedatabase.update("notifications", contentvalues, "notif_id=?", new String[] {
                s
            });
            context.getContentResolver().notifyChange(EsProvider.appendAccountParameter(EsProvider.NOTIFICATIONS_URI, esaccount), null);
        }
    }

    public static void syncNotifications(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, HttpOperation.OperationListener operationlistener, boolean flag) throws IOException {
        
    	synchronized(mSyncLock) {
    		if(syncstate.isCanceled()) {
    			return;
    		}
    		// TODO
    	}
    }
    
	private static interface IdAndTimestampQuery {

		public static final String PROJECTION[] = { "notif_id", "timestamp" };

	}

	private static interface NotificationIdsQuery {

		public static final String PROJECTION[] = { "notif_id" };

	}

	public static interface NotificationQuery {

		public static final String PROJECTION[] = { "_id", "notif_id",
				"coalescing_code", "category", "message", "timestamp",
				"circle_data", "pd_gaia_id", "pd_album_id", "pd_photo_id",
				"activity_id", "read", "ed_event", "ed_event_id",
				"ed_creator_id", "notification_type", "entity_type",
				"entity_snippet", "entity_photos_data", "entity_squares_data",
				"square_id", "square_name", "square_photo_url",
				"taggee_photo_ids", "taggee_data_actors" };

	}

	private static interface NotificationTimestampsQuery {

		public static final String PROJECTION[] = { "timestamp" };

	}
}
