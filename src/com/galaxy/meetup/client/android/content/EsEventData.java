/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.EventReadOperation;
import com.galaxy.meetup.client.android.api.GetEventOperation;
import com.galaxy.meetup.client.android.api.GetEventThemesOperation;
import com.galaxy.meetup.client.android.api.GetPhotoOperation;
import com.galaxy.meetup.client.android.api.SendEventRsvpOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.HttpTransactionMetrics;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.NotificationUtils;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.DataPhoto;
import com.galaxy.meetup.server.client.domain.EmbedsPerson;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.Invitee;
import com.galaxy.meetup.server.client.domain.InviteeSummary;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.domain.Theme;
import com.galaxy.meetup.server.client.domain.ThemeImage;
import com.galaxy.meetup.server.client.domain.Update;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EsEventData {

    private static final String EVENT_QUERY_PROJECTION[] = {
        "event_data"
    };
    private static final String SYNC_QUERY_PROJECTION[] = {
        "event_id", "polling_token", "resume_token", "event_data"
    };
    private static final Object mSyncLock = new Object();
    private static final Object sEventOperationSyncObject = new Object();
    private static File sEventThemePlaceholderDir;
    private static Object sEventThemesLock = new Object();

    public static boolean canAddPhotos(PlusEvent plusevent, String s) {
        boolean flag = false;
        if(plusevent != null) {
        	if(!TextUtils.equals(s, plusevent.getCreatorObfuscatedId()) && plusevent.getEventOptions() != null && plusevent.getEventOptions().getOpenPhotoAcl() != null)
            {
                boolean flag1 = plusevent.getEventOptions().getOpenPhotoAcl().booleanValue();
                flag = false;
                if(flag1)
                	flag = true;
            } else {
            	flag = true;
            }
        }
        return flag;
        
    }
    
    public static boolean canInviteOthers(PlusEvent plusevent, EsAccount esaccount)
    {
        boolean flag = true;
        if(plusevent != null && esaccount != null) {
        	if(!TextUtils.equals(plusevent.getCreatorObfuscatedId(), esaccount.getGaiaId())) {
                boolean flag1;
                boolean flag2;
                if(plusevent.getViewerInfo() != null && plusevent.getViewerInfo().getInviter() != null)
                    flag1 = flag;
                else
                    flag1 = false;
                if(plusevent.getEventOptions() == null || plusevent.getEventOptions() != null && plusevent.getEventOptions().getOpenPhotoAcl() != null && plusevent.getEventOptions().getOpenPhotoAcl().booleanValue())
                    flag2 = flag;
                else
                    flag2 = false;
                if(!flag1 || !flag2)
                    flag = false;
            }
        } else {
        	flag = false;
        }
        
        return flag;
    }
    
    public static boolean canRsvp(PlusEvent plusevent)
    {
        boolean flag;
        if(plusevent.getIsBroadcastView() == null || !plusevent.getIsBroadcastView().booleanValue())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static boolean canViewerAddPhotos(PlusEvent plusevent)
    {
        boolean flag;
        if(plusevent != null && plusevent.getViewerInfo() != null && plusevent.getViewerInfo().getCanUploadPhotos() != null && plusevent.getViewerInfo().getCanUploadPhotos().booleanValue())
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    public static void copyRsvpFromSummary(PlusEvent plusevent, EsAccount esaccount)
    {

        if(plusevent.getInviteeSummary() == null || plusevent.getInviteeSummary().size() <= 0)
            return;
        Iterator iterator = plusevent.getInviteeSummary().iterator();
        InviteeSummary inviteesummary = null;
        do
        {
            if(!iterator.hasNext())
                break;
            inviteesummary = (InviteeSummary)iterator.next();
        } while(inviteesummary.getSetByViewer() == null || !inviteesummary.getSetByViewer().booleanValue() || inviteesummary.getRsvpType() == null || TextUtils.equals(inviteesummary.getRsvpType(), "INVITED"));
        setViewerInfoRsvp(plusevent, esaccount, inviteesummary.getRsvpType());
    }
    
    public static void deleteEvent(Context context, EsAccount esaccount, String s)
    {
        int i = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase().delete("events", "event_id=?", new String[] {
            s
        });
        boolean flag = TextUtils.equals(s, InstantUpload.getInstantShareEventId(context));
        boolean flag1 = false;
        if(flag)
        {
            flag1 = true;
            disableInstantShare(context);
        }
        if(i > 0)
            context.getContentResolver().notifyChange(EsProvider.EVENTS_ALL_URI, null);
        if(EsLog.isLoggable("EsEventData", 3))
        {
            StringBuilder stringbuilder = (new StringBuilder("[DELETE_EVENT], id: ")).append(s);
            String s1;
            if(flag1)
                s1 = "; disable IS";
            else
                s1 = "";
            Log.d("EsEventData", stringbuilder.append(s1).toString());
        }
    }

    public static void disableInstantShare(Context context)
    {
        if(EsLog.isLoggable("EsEventData", 4))
            Log.i("EsEventData", (new StringBuilder("#disableInstantShare; now: ")).append(System.currentTimeMillis()).toString());
        enableInstantShareInternal(context, null, null, null, null, 0L, 0L);
    }

    public static void enableInstantShare(Context context, boolean flag, PlusEvent plusevent)
    {
        if(EsLog.isLoggable("EsEventData", 4))
            Log.i("EsEventData", (new StringBuilder("#enableInstantShare; event: ")).append(plusevent.getId()).toString());
        AlarmManager alarmmanager = (AlarmManager)context.getSystemService("alarm");
        android.app.PendingIntent pendingintent = Intents.getEventFinishedIntent(context, plusevent.getId());
        long l = getEventEndTime(plusevent);
        long l1 = System.currentTimeMillis();
        alarmmanager.cancel(pendingintent);
        if(flag && 5000L + l1 < l)
        {
            if(EsLog.isLoggable("EsEventData", 4))
                Log.i("EsEventData", (new StringBuilder("#enableInstantShare; start IS; now: ")).append(l1).append(", end: ").append(l).append(", wake in: ").append(l - l1).toString());
            enableInstantShareInternal(context, EsAccountsData.getActiveAccount(context), plusevent.getId(), plusevent.getCreatorObfuscatedId(), plusevent.getName(), l1, l);
            alarmmanager.set(0, l, pendingintent);
        } else
        {
            if(EsLog.isLoggable("EsEventData", 4))
                Log.i("EsEventData", (new StringBuilder("#enableInstantShare; event over; now: ")).append(l1).append(", end: ").append(l).toString());
            disableInstantShare(context);
        }
    }
    
    public static Cursor getMyCurrentEvents(Context context, EsAccount esaccount, long l, String as[])
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        String as1[] = new String[1];
        as1[0] = Long.toString(l);
        return sqlitedatabase.query("events", as, "mine = 1 AND ? < end_time AND source = 1", as1, null, null, "end_time ASC");
    }
    
    public static InviteeSummary getInviteeSummary(PlusEvent plusevent, String s)
    {
        if(plusevent.getInviteeSummary() == null) 
        	return null;
        
        InviteeSummary inviteesummary1;
        for(Iterator iterator = plusevent.getInviteeSummary().iterator(); iterator.hasNext();) {
        	inviteesummary1 = (InviteeSummary)iterator.next();
        	if(inviteesummary1.getRsvpType() == null || inviteesummary1.getCount().intValue() == 1 && inviteesummary1.getSetByViewer() != null && inviteesummary1.getSetByViewer().booleanValue() || !TextUtils.equals(s, inviteesummary1.getRsvpType())) {
        		continue;
        	} else {
        		return inviteesummary1;
        	}
        }
        
        return null;
    }

    private static Set getMyEventIds(SQLiteDatabase sqlitedatabase)
    {
        Cursor cursor = null;
        HashSet hashset = new HashSet();
        try {
	        cursor = sqlitedatabase.query("events", new String[] {
	            "event_id"
	        }, "mine = 1", null, null, null, null);
	        for(; cursor.moveToNext(); hashset.add(cursor.getString(0)));
	        return hashset;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public static Cursor getMyPastEvents(Context context, EsAccount esaccount, long l, String as[]) {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        String as1[] = new String[1];
        as1[0] = Long.toString(l);
        return sqlitedatabase.query("events", as, "mine = 1 AND ? > end_time AND source = 1", as1, null, null, "end_time DESC");
    }

    public static PlusEvent getPlusEvent(Context context, EsAccount esaccount, String s) {
        
        if(esaccount == null || TextUtils.isEmpty(s)) {
        	return null; 
        }
        
        Cursor cursor = null;
        try {
        	cursor = getEvent(context, esaccount, s, EVENT_QUERY_PROJECTION);
        	if(null == cursor || !cursor.moveToNext()) {
        		return null;
        	}
        	return (PlusEvent)JsonUtil.fromByteArray(cursor.getBlob(0), PlusEvent.class);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
    
    public static Cursor getEvent(Context context, EsAccount esaccount, String s, String as[])
    {
        return EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("events", as, "event_id=?", new String[] {
            s
        }, null, null, null);
    }
    
    public static int getRsvpStatus(PlusEvent plusevent)
    {
        String s = getRsvpType(plusevent);
        int i;
        if("CHECKIN".equals(s) || "ATTENDING".equals(s))
            i = 1;
        else
        if("MAYBE".equals(s))
            i = 2;
        else
        if("NOT_ATTENDING".equals(s) || "NOT_ATTENDING_AND_REMOVE".equals(s))
            i = 3;
        else
            i = 0;
        return i;
    }

    public static String getRsvpType(PlusEvent plusevent)
    {
        String s;
        if(plusevent != null && plusevent.getViewerInfo() != null)
        {
            s = plusevent.getViewerInfo().getRsvpType();
            if(!"CHECKIN".equals(s) && !"ATTENDING".equals(s) && !"MAYBE".equals(s) && !"NOT_ATTENDING".equals(s) && !"NOT_ATTENDING_AND_REMOVE".equals(s))
            {
                if(EsLog.isLoggable("EsEventData", 3))
                    Log.d("EsEventData", (new StringBuilder("[FILTER_RSVP_TYPE]; ")).append(s).append(" not recognized").toString());
                s = "NOT_RESPONDED";
            }
        } else
        {
            s = "NOT_RESPONDED";
        }
        return s;
    }
    
    public static String getImageUrl(Theme theme)
    {
        ThemeImage themeimage = getThemeImage(theme);
        String s;
        if(themeimage != null)
            s = themeimage.getUrl();
        else
            s = null;
        return s;
    }
    
    public static Cursor getEventTheme(Context context, EsAccount esaccount, int i, String as[])
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        String s;
        String as1[];
        Cursor cursor;
        if(i == -1)
        {
            s = "is_default!=0";
            as1 = null;
        } else
        {
            s = "theme_id=?";
            as1 = new String[1];
            as1[0] = Integer.toString(i);
        }
        cursor = sqlitedatabase.query("event_themes", as, s, as1, null, null, "theme_id");
        if(cursor.getCount() == 0)
        {
            cursor.close();
            ensureFreshEventThemes(context, esaccount, null);
            cursor = sqlitedatabase.query("event_themes", as, s, as1, null, null, "theme_id");
        }
        return cursor;
    }
    
    public static Cursor getEventThemes(Context context, EsAccount esaccount, int i, String as[])
    {
        ensureFreshEventThemes(context, esaccount, null);
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        StringBuilder stringbuilder = new StringBuilder("is_featured=");
        int j;
        if(i == 0)
            j = 1;
        else
            j = 0;
        return sqlitedatabase.query("event_themes", as, stringbuilder.append(j).toString(), null, null, null, "sort_order ASC");
    }
    
    public static Cursor retrieveEvent(Context context, EsAccount esaccount, String s, String s1, String as[])
    {
        Cursor cursor = getEvent(context, esaccount, s, as);
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            if(getEventFromServer(context, esaccount, s, s1))
                cursor = getEvent(context, esaccount, s, as);
            else
                cursor = null;
        }
        return cursor;
    }
    
    public static boolean isEventOver(PlusEvent plusevent, long l)
    {
        boolean flag;
        if(l > getEventEndTime(plusevent))
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    public static boolean isViewerCheckedIn(PlusEvent plusevent)
    {
        return "CHECKIN".equals(getRsvpType(plusevent));
    }
    
    public static boolean isEventHangout(PlusEvent plusevent)
    {
        boolean flag;
        if(plusevent.getEventOptions() != null && plusevent.getEventOptions().getBroadcast() != null && plusevent.getEventOptions().getBroadcast().booleanValue())
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    public static long getEventEndTime(PlusEvent plusevent)
    {
        long l;
        if(plusevent.getEndTime() == null || plusevent.getEndTime().getTimeMs() == null)
            l = 0x6ddd00L + plusevent.getStartTime().getTimeMs().longValue();
        else
            l = plusevent.getEndTime().getTimeMs().longValue();
        return l;
    }
    
    public static ThemeImage getThemeImage(Theme theme) {
        ThemeImage themeimage = null;
        if(theme != null) {
            List list = theme.getImage();
            themeimage = null;
            if(list != null) {
                Iterator iterator = theme.getImage().iterator();
                do {
                    if(!iterator.hasNext())
                        break;
                    ThemeImage themeimage1 = (ThemeImage)iterator.next();
                    if(themeimage1 != null)
                        if("LARGE".equals(themeimage1.getAspectRatio())) {
                            if("JPG".equals(themeimage1.getFormat()))
                                themeimage = themeimage1;
                        } else
                        if(themeimage == null && !"MOV".equals(themeimage1.getFormat()))
                            themeimage = themeimage1;
                } while(true);
            }
        }
        return themeimage;
    }

    private static void enableInstantShareInternal(final Context context, final EsAccount esaccount, final String activeISEventId, final String eventId, String s2, long l, long l1)
    {
        ContentResolver contentresolver = context.getContentResolver();
        ContentValues contentvalues = new ContentValues();
        boolean flag;
        String s3;
        if(activeISEventId != null)
            flag = true;
        else
            flag = false;
        s3 = InstantUpload.getInstantShareEventId(context);
        if(flag)
        {
            contentvalues.put("auto_upload_account_name", esaccount.getName());
            contentvalues.put("auto_upload_account_type", AccountsUtil.ACCOUNT_TYPE);
        }
        contentvalues.put("instant_share_eventid", activeISEventId);
        contentvalues.put("instant_share_starttime", Long.valueOf(l));
        contentvalues.put("instant_share_endtime", Long.valueOf(l1));
        //contentresolver.update(InstantUploadFacade.SETTINGS_URI, contentvalues, null, null);
        if(flag)
        {
            NotificationUtils.notifyInstantShareEnabled(context, s2, Intents.getViewEventActivityNotificationIntent(context, esaccount, activeISEventId, eventId));
            InstantUpload.ensureSyncEnabled(esaccount);
        } else
        {
            NotificationUtils.cancelInstantShareEnabled(context);
        }
        InstantUpload.startMonitoring(context);
        if(!TextUtils.equals(activeISEventId, s3))
            (new Handler(Looper.getMainLooper())).post(new Runnable() {

                public final void run()
                {
                    OzViews ozviews = OzViews.getViewForLogging(context);
                    if(!TextUtils.isEmpty(activeISEventId))
                        EsAnalytics.recordActionEvent(context, esaccount, OzActions.EVENTS_PARTY_MODE_OFF, ozviews);
                    if(!TextUtils.isEmpty(eventId))
                        EsAnalytics.recordActionEvent(context, esaccount, OzActions.EVENTS_PARTY_MODE_ON, ozviews);
                }
            });
    }
    
    public static long timeUntilInstantShareAllowed(PlusEvent plusevent, String s, long l)
    {
        long l2;
        if(isInstantShareAllowed(plusevent, s, l))
        {
            l2 = 0L;
        } else
        {
            long l1 = getEventEndTime(plusevent);
            if(!canAddPhotos(plusevent, s) || l > l1)
                l2 = -1L;
            else
                l2 = plusevent.getStartTime().getTimeMs().longValue() - 0xa4cb80L - l;
        }
        return l2;
    }
    
    public static Cursor getEventActivities(Context context, EsAccount esaccount, String s, String as[])
    {
    	long l = 0L;
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        try {
        	l = DatabaseUtils.longForQuery(sqlitedatabase, "SELECT display_time FROM events WHERE event_id = ?", new String[] {
        			s
        	});
        } catch (SQLiteDoneException sqlitedoneexception) {
        	// TODO log
        }
        String as1[] = new String[2];
        as1[0] = s;
        as1[1] = Long.toString(l);
        return sqlitedatabase.query("event_activities", as, "event_id = ? AND timestamp >= ?", as1, null, null, "timestamp DESC");
    }
    
    public static Cursor getEventResolvedPeople(Context context, EsAccount esaccount, String s, String as[])
    {
        return EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("event_people_view", as, "event_id = ?", new String[] {
            s
        }, null, null, null);
    }
    
    public static String getEventName(Context context, EsAccount esaccount, String s)
    {
    	try {
	        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
	        return DatabaseUtils.stringForQuery(sqlitedatabase, "SELECT name FROM events WHERE event_id = ?", new String[] {
	            s
	        });
    	} catch (SQLiteDoneException sqlitedoneexception) {
    		return s;
    	}
    }
    
    public static void insertEventHomeList(Context context, EsAccount esaccount, List list, List list1, List list2, List list3)
    {
        SQLiteDatabase sqlitedatabase;
        ArrayList arraylist;
        String s;
        Set set;
        int ai[];
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        arraylist = new ArrayList();
        s = esaccount.getGaiaId();
        set = getMyEventIds(sqlitedatabase);
        ai = new int[5];
        
        try {
	        sqlitedatabase.beginTransaction();
	        insertEventListInTransaction(context, sqlitedatabase, s, list, set, ai, arraylist, list3);
	        insertEventListInTransaction(context, sqlitedatabase, s, list1, set, ai, arraylist, list3);
	        insertEventListInTransaction(context, sqlitedatabase, s, list2, set, ai, arraylist, list3);
	        StringBuilder stringbuilder = new StringBuilder();
	        String as[] = (String[])set.toArray(new String[0]);
	        stringbuilder.append("event_id IN (");
	        for(int i = 0; i < as.length; i++)
	        {
	            if(i != 0)
	                stringbuilder.append(',');
	            stringbuilder.append('?');
	        }
	
	        stringbuilder.append(')');
	        sqlitedatabase.delete("events", stringbuilder.toString(), as);
	        ai[3] = as.length;
	        ContentValues contentvalues = new ContentValues();
	        contentvalues.put("event_list_sync_time", Long.valueOf(System.currentTimeMillis()));
	        sqlitedatabase.update("account_status", contentvalues, null, null);
	        sqlitedatabase.setTransactionSuccessful();
	        if(EsLog.isLoggable("EsEventData", 3))
	            Log.d("EsEventData", (new StringBuilder("[INSERT_EVENT_LIST]; ")).append(ai[0]).append(" inserted, ").append(ai[1]).append(" changed, ").append(ai[2]).append(" not changed, ").append(ai[3]).append(" removed, ").append(ai[4]).append(" ignored").toString());
	        PlusEvent plusevent = getPlusEvent(context, esaccount, InstantUpload.getInstantShareEventId(context));
	        if(validateInstantShare(context, esaccount, plusevent))
	            enableInstantShare(context, true, plusevent);
	        Uri uri;
	        for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); context.getContentResolver().notifyChange(uri, null))
	            uri = (Uri)iterator.next();
	        context.getContentResolver().notifyChange(EsProvider.ACCOUNT_STATUS_URI, null);
	        context.getContentResolver().notifyChange(EsProvider.EVENTS_ALL_URI, null);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }
    
    private static void insertEventListInTransaction(Context context, SQLiteDatabase sqlitedatabase, String s, List list, Set set, int ai[], List list1, List list2)
    {
        if(list != null)
        {
            Iterator iterator = list.iterator();
            while(iterator.hasNext()) 
            {
                PlusEvent plusevent = (PlusEvent)iterator.next();
                if(!isMine(plusevent, s))
                {
                    ai[4] = 1 + ai[4];
                } else
                {
                    boolean flag = set.remove(plusevent.getId());
                    int i;
                    if(insertEventInTransaction(context, s, sqlitedatabase, plusevent.getId(), null, plusevent, null, list1, null, list2))
                    {
                        if(flag)
                            i = 1;
                        else
                            i = 0;
                    } else
                    {
                        i = 2;
                    }
                    ai[i] = 1 + ai[i];
                }
            }
        }
    }
    
    private static boolean isMine(PlusEvent plusevent, String s) {
    	
    	boolean isMine = s.equals(plusevent.creatorObfuscatedId) || (plusevent.viewerInfo != null && plusevent.viewerInfo.rsvpType != null);
    	if(isMine) {
    		return true;
    	}
    	
    	List<InviteeSummary> list = plusevent.inviteeSummary;
    	if(null != list) {
    		for(InviteeSummary inviteesummary : list) {
    			if(null != inviteesummary.setByViewer && inviteesummary.setByViewer.booleanValue()) {
    				return true;
    			}
    		}
    	}
    	return true;
    }
    
    public static void insertEvent(Context context, EsAccount esaccount, String s, PlusEvent plusevent, Update update)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ArrayList arraylist = new ArrayList();
        String s1 = plusevent.getId();
        try {
        	sqlitedatabase.beginTransaction();
        	insertEventInTransaction(context, esaccount.getGaiaId(), sqlitedatabase, s1, s, plusevent, update, arraylist, null, null);
        	sqlitedatabase.setTransactionSuccessful();
        	if(!TextUtils.isEmpty(s1) && TextUtils.equals(s1, InstantUpload.getInstantShareEventId(context)) && validateInstantShare(context, esaccount, plusevent))
        		enableInstantShare(context, true, plusevent);
        	Uri uri;
        	for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); context.getContentResolver().notifyChange(uri, null))
        		uri = (Uri)iterator.next();
        	context.getContentResolver().notifyChange(EsProvider.EVENTS_ALL_URI, null);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }
    
    public static void insertEventInviteeList(Context context, EsAccount esaccount, String s, List list)
    {
        if(EsLog.isLoggable("EsEventData", 3))
        {
            Invitee invitee;
            for(Iterator iterator = list.iterator(); iterator.hasNext(); Log.d("EsEventData", (new StringBuilder("[INSERT_EVENT_INVITEE]; ")).append(s).append(" ").append(invitee.toJsonString()).toString()))
                invitee = (Invitee)iterator.next();

        }
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        String as[] = {
            s
        };
        ContentValues contentvalues = new ContentValues();
        InviteeList inviteelist = new InviteeList();
        inviteelist.invitees = list;
        contentvalues.put("invitee_roster_timestamp", Long.valueOf(System.currentTimeMillis()));
        contentvalues.put("invitee_roster", JsonUtil.toByteArray(inviteelist));
        insertPeopleInInviteeSummaries(context, s, null, list, sqlitedatabase);
        sqlitedatabase.update("events", contentvalues, "event_id=?", as);
        context.getContentResolver().notifyChange(EsProvider.EVENTS_ALL_URI, null);
    }
    
    public static void insertEventActivities(Context context, EsAccount esaccount, String s, String s1, List list, boolean flag)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ArrayList arraylist = new ArrayList();
        try {
        	sqlitedatabase.beginTransaction();
        	insertResumeTokenInTransaction(sqlitedatabase, s, null);
        	if(list != null)
        		insertEventActivitiesInTransaction(context, sqlitedatabase, s, list, true, arraylist);
        	sqlitedatabase.setTransactionSuccessful();
        	Uri uri;
            for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); context.getContentResolver().notifyChange(uri, null))
                 uri = (Uri)iterator.next();
            context.getContentResolver().notifyChange(EsProvider.EVENTS_ALL_URI, null);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }
    
    private static void insertResumeTokenInTransaction(SQLiteDatabase sqlitedatabase, String s, String s1)
    {
        String as[];
        boolean flag;
        Cursor cursor = null;
        as = (new String[] {
            s
        });
        flag = true;
        try {
	        cursor = sqlitedatabase.query("events", new String[] {
	            "resume_token"
	        }, "event_id=?", as, null, null, null);
	        String s2;
	        String s3;
	        boolean flag1 = cursor.moveToFirst();
	        s2 = null;
	        if(flag1)
	        	s2 = cursor.getString(0);
	        flag = false;
	        cursor.close();
	        if(!TextUtils.equals(s2, s1))
	        {
	            ContentValues contentvalues = new ContentValues();
	            contentvalues.put("resume_token", s1);
	            Exception exception;
	            if(flag)
	                sqlitedatabase.insert("events", null, contentvalues);
	            else
	                sqlitedatabase.update("events", contentvalues, "event_id=?", as);
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
    
    private static void insertEventActivitiesInTransaction(Context context, SQLiteDatabase sqlitedatabase, String s, List list, boolean flag, List list1)
    {
        boolean flag1;
        Cursor cursor = null;
        String as[] = (new String[] {
            s
        });
        HashMap hashmap = new HashMap();
        flag1 = false;
        try {
	        cursor = sqlitedatabase.query("event_activities", new String[] {
	            "_id", "type", "owner_gaia_id", "timestamp", "fingerprint"
	        }, "event_id=?", as, null, null, null);
	        while(cursor.moveToNext()) 
	        {
	            EventActivityKey eventactivitykey = new EventActivityKey();
	            EventActivityStatus eventactivitystatus = new EventActivityStatus();
	            eventactivitystatus.id = cursor.getString(0);
	            eventactivitykey.type = cursor.getInt(1);
	            eventactivitykey.ownerGaiaId = cursor.getString(2);
	            eventactivitykey.timestamp = cursor.getLong(3);
	            eventactivitystatus.fingerprint = cursor.getInt(4);
	            hashmap.put(eventactivitykey, eventactivitystatus);
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
        ContentValues contentvalues = new ContentValues();
        EventActivityKey eventactivitykey1 = new EventActivityKey();
        HashMap hashmap1 = new HashMap();
        Iterator iterator = list.iterator();
        do
        {
            if(iterator.hasNext())
            {
                EventActivity eventactivity = (EventActivity)iterator.next();
                eventactivitykey1.type = eventactivity.activityType;
                eventactivitykey1.ownerGaiaId = eventactivity.ownerGaiaId;
                eventactivitykey1.timestamp = eventactivity.timestamp;
                EventActivityStatus eventactivitystatus2 = (EventActivityStatus)hashmap.get(eventactivitykey1);
                int i;
                String s1;
                DataPhoto dataphoto;
                String s2;
                if(eventactivity.data == null)
                    i = 0;
                else
                    i = eventactivity.data.hashCode();
                if(eventactivity.activityType == 100)
                {
                    dataphoto = (DataPhoto)JsonUtil.toBean(eventactivity.data, DataPhoto.class);
                    s1 = null;
                    s2 = null;
                    if(dataphoto != null)
                        s2 = dataphoto.original.url;
                } else
                {
                    int j = eventactivity.activityType;
                    s1 = null;
                    dataphoto = null;
                    s2 = null;
                    if(j == 5)
                    {
                        EventComment eventcomment = (EventComment)JsonUtil.toBean(eventactivity.data, EventComment.class);
                        s1 = null;
                        dataphoto = null;
                        s2 = null;
                        if(eventcomment != null)
                        {
                            s1 = eventcomment.text;
                            dataphoto = null;
                            s2 = null;
                        }
                    }
                }
                if(eventactivitystatus2 == null)
                {
                    contentvalues.clear();
                    contentvalues.put("event_id", s);
                    contentvalues.put("type", Integer.valueOf(eventactivity.activityType));
                    contentvalues.put("timestamp", Long.valueOf(eventactivity.timestamp));
                    contentvalues.put("owner_gaia_id", eventactivity.ownerGaiaId);
                    contentvalues.put("owner_name", eventactivity.ownerName);
                    contentvalues.put("data", eventactivity.data);
                    contentvalues.put("url", s2);
                    contentvalues.put("comment", s1);
                    contentvalues.put("fingerprint", Integer.valueOf(i));
                    sqlitedatabase.insert("event_activities", null, contentvalues);
                } else
                {
                    int k = eventactivitystatus2.fingerprint;
                    if(i != k)
                    {
                        contentvalues.clear();
                        contentvalues.put("data", eventactivity.data);
                        contentvalues.put("url", s2);
                        contentvalues.put("comment", s1);
                        contentvalues.put("fingerprint", Integer.valueOf(i));
                        String as2[] = new String[1];
                        as2[0] = eventactivitystatus2.id;
                        sqlitedatabase.update("event_activities", contentvalues, "_id=?", as2);
                    }
                    hashmap.remove(eventactivitykey1);
                }
                if(eventactivity.activityType == 100)
                {
                    EsPhotosDataApiary.insertEventPhotoInTransaction(sqlitedatabase, dataphoto, s, hashmap1, list1);
                    flag1 = true;
                }
                continue;
            }
            if(!flag)
            {
                String as1[];
                for(Iterator iterator1 = hashmap.values().iterator(); iterator1.hasNext(); sqlitedatabase.delete("event_activities", "_id=?", as1))
                {
                    EventActivityStatus eventactivitystatus1 = (EventActivityStatus)iterator1.next();
                    as1 = new String[1];
                    as1[0] = eventactivitystatus1.id;
                }

            }
            contentvalues.clear();
            contentvalues.put("activity_refresh_timestamp", Long.valueOf(System.currentTimeMillis()));
            sqlitedatabase.update("events", contentvalues, "event_id=?", as);
            if(flag1)
            {
                Uri uri = Uri.withAppendedPath(EsProvider.PHOTO_BY_EVENT_ID_URI, s);
                if(list1 != null)
                    list1.add(uri);
                else
                    context.getContentResolver().notifyChange(uri, null);
            }
            return;
        } while(true);
    }
    
    public static void insertEventThemes(Context context, EsAccount esaccount, List list)
    {
    	// TODO
    }
    
    
    public static void updateEventInviteeList(Context context, EsAccount esaccount, String s, boolean flag, String s1, String s2) {
    	
    	PlusEvent plusevent = null;
	    List list = null;
    	Cursor cursor = null;
    	try {
	        cursor = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("events", new String[] {
	            "event_data", "invitee_roster"
	        }, "event_id=?", new String[] {
	            s
	        }, null, null, null);
	       
	        boolean flag1 = cursor.moveToFirst();
	        if(flag1)
	        {
	            byte abyte1[] = cursor.getBlob(0);
	            byte abyte2[] = cursor.getBlob(1);
	            if(abyte2 != null)
	            {
	                plusevent = (PlusEvent)JsonUtil.fromByteArray(abyte1, PlusEvent.class);
	                list = ((InviteeList)JsonUtil.fromByteArray(abyte2, InviteeList.class)).invitees;
	            }
	        }
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
        
    	if(null == list || null == plusevent) {
    		return;
    	}
    	
    	Iterator iterator = list.iterator();
        Invitee invitee;
        do
        {
            boolean flag2 = iterator.hasNext();
            invitee = null;
            if(!flag2)
                break;
            Invitee invitee1 = (Invitee)iterator.next();
            if(invitee1.getInvitee() == null || !TextUtils.equals(s1, invitee1.getInvitee().getOwnerObfuscatedId()) || !TextUtils.equals(s2, invitee1.getInvitee().getEmail()))
                continue;
            invitee = invitee1;
            break;
        } while(true);
        if(invitee != null && (invitee.getIsAdminBlacklisted() == null || invitee.getIsAdminBlacklisted().booleanValue() != flag))
        {
            Boolean boolean1 = Boolean.valueOf(flag);
            invitee.setIsAdminBlacklisted(boolean1);
            insertEventInviteeList(context, esaccount, s, list);
            String s3 = invitee.getRsvpType();
            int i = 1 + invitee.getNumAdditionalGuests().intValue();
            if(plusevent != null && plusevent.getInviteeSummary() != null)
            {
                Iterator iterator1 = plusevent.getInviteeSummary().iterator();
                do
                {
                    if(!iterator1.hasNext())
                        break;
                    InviteeSummary inviteesummary = (InviteeSummary)iterator1.next();
                    byte byte0;
                    if(flag)
                        byte0 = -1;
                    else
                        byte0 = 1;
                    if(inviteesummary.getCount() != null && TextUtils.equals(inviteesummary.getRsvpType(), s3))
                        inviteesummary.setCount(Integer.valueOf(Math.max(inviteesummary.getCount().intValue() + byte0 * i, 0)));
                } while(true);
                SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
                String as[] = new String[1];
                as[0] = plusevent.getId();
                byte abyte0[] = JsonUtil.toByteArray(plusevent);
                ContentValues contentvalues = new ContentValues();
                contentvalues.put("event_data", abyte0);
                sqlitedatabase.update("events", contentvalues, "event_id=?", as);
                context.getContentResolver().notifyChange(EsProvider.EVENTS_ALL_URI, null);
            }
        }
    }
    
    
    public static boolean getEventFromServer(Context context, EsAccount esaccount, String s, String s1) {
    	
    	synchronized(sEventOperationSyncObject) {
    		 GetEventOperation geteventoperation = new GetEventOperation(context, esaccount, s, s1, null, null);
    		 geteventoperation.start();
    		 if(geteventoperation.hasError()) {
    			 geteventoperation.logError("EsEventData");
    			 return false;
    		 }
    		 return true;
    	}
    }
    
    public static void updateEventActivities(Context context, EsAccount esaccount, String s, PlusEvent plusevent, Update update, String s1, String s2, ArrayList arraylist,  boolean flag, long l, List list) {
        
       // TODO
    }
    
    public static boolean rsvpForEvent(Context context, EsAccount esaccount, String s, String s1, String s2) {
    	
    	synchronized(sEventOperationSyncObject) {
            PlusEvent plusevent = getPlusEvent(context, esaccount, s);
            String s3 = setRsvpType(context, esaccount, s, s1);
            if(plusevent != null && s3 == null)
            {
                return false;
            }
            SendEventRsvpOperation sendeventrsvpoperation = new SendEventRsvpOperation(context, esaccount, s, s2, s1, s3, null, null);
            sendeventrsvpoperation.start();
            return !sendeventrsvpoperation.hasError();
    	}
    }
    
    public static String setRsvpType(Context context, EsAccount esaccount, String s, String s1) {
    	
        boolean flag;
        String s2;
        Cursor cursor = null;
        PlusEvent plusevent = null;
        String s3;
        flag = "CHECKIN".equals(s1);
        boolean flag1;
        if(flag)
            s2 = "UNDO_CHECKIN";
        else
            s2 = "NOT_RESPONDED";
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        
        try {
	        cursor = sqlitedatabase.query("events", EVENT_QUERY_PROJECTION, "event_id=?", new String[] {
	            s
	        }, null, null, null);
	        if(cursor.moveToNext())
	        	plusevent = (PlusEvent)JsonUtil.fromByteArray(cursor.getBlob(0), PlusEvent.class);
	        if(plusevent == null) {
	        	context.getContentResolver().notifyChange(EsProvider.EVENTS_ALL_URI, null);
	            return s2;
	        }
	        
	        if(plusevent.getIsPublic() != null && plusevent.getIsPublic().booleanValue() || isMine(plusevent, esaccount.getGaiaId()) || !TextUtils.isEmpty(plusevent.getAuthKey())) {
	        	if(plusevent.getInviteeSummary() != null)
	            {
	                boolean flag2 = false;
	                Iterator iterator = plusevent.getInviteeSummary().iterator();
	                do
	                {
	                    if(!iterator.hasNext())
	                        break;
	                    InviteeSummary inviteesummary1 = (InviteeSummary)iterator.next();
	                    if(inviteesummary1.getRsvpType() != null)
	                    {
	                        boolean flag3;
	                        if(inviteesummary1.getSetByViewer() != null && inviteesummary1.getSetByViewer().booleanValue())
	                            flag3 = true;
	                        else
	                            flag3 = false;
	                        if(flag3 && !flag)
	                            s2 = inviteesummary1.getRsvpType();
	                        if(s1.equals(inviteesummary1.getRsvpType()))
	                        {
	                            flag2 = true;
	                            if(!flag3)
	                            {
	                                inviteesummary1.setSetByViewer(Boolean.valueOf(true));
	                                int i;
	                                if(inviteesummary1.getCount() == null)
	                                    i = 1;
	                                else
	                                    i = 1 + inviteesummary1.getCount().intValue();
	                                inviteesummary1.setCount(Integer.valueOf(i));
	                            }
	                        } else
	                        if(flag3)
	                        {
	                            boolean flag4 = true;
	                            if(inviteesummary1.getRsvpType().equals("CHECKIN"))
	                                flag4 = "UNDO_CHECKIN".equals(s1);
	                            if(flag4)
	                            {
	                                inviteesummary1.setSetByViewer(Boolean.valueOf(false));
	                                if(inviteesummary1.getCount() != null)
	                                {
	                                    inviteesummary1.setCount(Integer.valueOf(-1 + inviteesummary1.getCount().intValue()));
	                                    if(inviteesummary1.getCount().intValue() == 0)
	                                        iterator.remove();
	                                }
	                            }
	                        }
	                    }
	                } while(true);
	                if(!flag2)
	                {
	                    InviteeSummary inviteesummary = new InviteeSummary();
	                    inviteesummary.setRsvpType(s1);
	                    inviteesummary.setCount(Integer.valueOf(1));
	                    inviteesummary.setSetByViewer(Boolean.valueOf(true));
	                    plusevent.getInviteeSummary().add(0, inviteesummary);
	                }
	            }
	            setViewerInfoRsvp(plusevent, esaccount, s1);
	            ContentValues contentvalues = new ContentValues();
	            contentvalues.put("event_data", JsonUtil.toByteArray(plusevent));
	            contentvalues.put("refresh_timestamp", Long.valueOf(System.currentTimeMillis()));
	            sqlitedatabase.update("events", contentvalues, "event_id=?", new String[] {
	                s
	            });
	            context.getContentResolver().notifyChange(EsProvider.EVENTS_ALL_URI, null);
	            return s2;
	        } else { 
	        	return null;
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
    
    private static boolean insertEventInTransaction(Context context, String s, SQLiteDatabase sqlitedatabase, String s1, String s2, PlusEvent plusevent, Update update, List list, 
            Long long1, List list1)
    {
        return insertEventInTransaction(context, s, sqlitedatabase, s1, s2, plusevent, update, long1, list1, 1);
    }

    static boolean insertEventInTransaction(Context context, String s, SQLiteDatabase sqlitedatabase, String s1, String s2, PlusEvent plusevent, Update update, Long long1, 
            List list, int i)
    {
        long l;
        boolean flag;
        Cursor cursor;
        l = System.currentTimeMillis();
        flag = true;
        cursor = sqlitedatabase.query("events", new String[] {
            "fingerprint", "source", "can_comment"
        }, "event_id=?", new String[] {
            s1
        }, null, null, null);
        boolean flag1;
        int j;
        boolean flag2;
        boolean flag6;
        flag1 = cursor.moveToFirst();
        j = 0;
        flag2 = false;
        boolean flag7;
        if(flag1) {
        	int i1;
            int j1;
            j = cursor.getInt(0);
            i1 = cursor.getInt(1);
            j1 = cursor.getInt(2);
            if(j1 != 0)
                flag2 = true;
            else
                flag2 = false;
            flag = false;
            if(i1 == 1) {
                flag = false;
                if(i == 0) {
                	return false;
                }
            }
        }
        
        byte abyte0[];
        int k;
        ContentValues contentvalues;
        cursor.close();
        abyte0 = JsonUtil.toByteArray(plusevent);
        k = Arrays.hashCode(abyte0);
        contentvalues = new ContentValues();
        contentvalues.put("source", Integer.valueOf(i));
        if(!flag && j == k) {
        	flag6 = false;
            if(s2 != null)
            {
                contentvalues.put("activity_id", s2);
                sqlitedatabase.update("events", contentvalues, "event_id=?", new String[] {
                    s1
                });
                flag6 = false;
            }
        } else { 
            contentvalues.put("refresh_timestamp", Long.valueOf(l));
            contentvalues.put("name", plusevent.getName());
            contentvalues.put("event_data", abyte0);
            contentvalues.put("mine", Boolean.valueOf(isMine(plusevent, s)));
            boolean flag3;
            boolean flag4;
            boolean flag5;
            long l1;
            if(s.equals(plusevent.getCreatorObfuscatedId()) || plusevent.getEventOptions() != null && plusevent.getEventOptions().getOpenPhotoAcl() != null && plusevent.getEventOptions().getOpenPhotoAcl().booleanValue())
                flag3 = true;
            else
                flag3 = false;
            contentvalues.put("can_invite_people", Boolean.valueOf(flag3));
            if(s.equals(plusevent.getCreatorObfuscatedId()) || plusevent.getEventOptions() != null && plusevent.getEventOptions().getOpenPhotoAcl() != null && plusevent.getEventOptions().getOpenPhotoAcl().booleanValue())
                flag4 = true;
            else
                flag4 = false;
            contentvalues.put("can_post_photos", Boolean.valueOf(flag4));
            if(update != null)
                flag5 = PrimitiveUtils.safeBoolean(update.canViewerComment);
            else
                flag5 = flag2;
            contentvalues.put("can_comment", Boolean.valueOf(flag5));
            if(plusevent.getStartTime() != null)
                l1 = plusevent.getStartTime().getTimeMs().longValue();
            else
                l1 = l;
            contentvalues.put("start_time", Long.valueOf(l1));
            contentvalues.put("end_time", Long.valueOf(getEventEndTime(plusevent)));
            contentvalues.put("fingerprint", Integer.valueOf(k));
            if(flag || s2 != null)
                contentvalues.put("activity_id", s2);
            if(long1 != null)
                contentvalues.put("display_time", long1);
            if(flag)
            {
                contentvalues.put("event_id", s1);
                sqlitedatabase.insert("events", null, contentvalues);
            } else
            {
                sqlitedatabase.update("events", contentvalues, "event_id=?", new String[] {
                    s1
                });
            }
            flag6 = true;
        }
        	
        if(EsLog.isLoggable("EsEventData", 3))
        {
            Log.d("EsEventData", (new StringBuilder("[INSERT_EVENT], duration: ")).append(System.currentTimeMillis() - l).append("ms").toString());
            StringBuilder stringbuilder = new StringBuilder();
            String s3 = stringbuilder.toString();
            stringbuilder.setLength(0);
            StringBuilder stringbuilder1 = stringbuilder.append(s3).append("EVENT [id: ").append(plusevent.getId()).append(", owner: ");
            String s4;
            if(plusevent.getCreatorObfuscatedId() == null)
                s4 = "N/A";
            else
                s4 = plusevent.getCreatorObfuscatedId();
            stringbuilder1.append(s4);
            CharSequence charsequence = DateFormat.format("MMM dd, yyyy h:mmaa", new Date(plusevent.getStartTime().getTimeMs().longValue()));
            stringbuilder.append(", start: ").append(charsequence);
            if(plusevent.getEndTime() != null && plusevent.getEndTime().getTimeMs() != null)
            {
            	CharSequence charsequence1 = DateFormat.format("MMM dd, yyyy h:mmaa", new Date(plusevent.getEndTime().getTimeMs().longValue()));
                stringbuilder.append(", end: ").append(charsequence1);
            }
            stringbuilder.append(", \n").append(s3).append("      title: ").append(plusevent.getName());
            stringbuilder.append("]");
            stringbuilder.append("\n");
            stringbuilder.append("\n");
            EsLog.writeToLog(3, "EsEventData", stringbuilder.toString());
        }
        insertReferencedPeopleInTransaction(context, plusevent, list, sqlitedatabase);
        flag7 = flag6;
        
        return flag7;
        
    }
    
    private static void insertReferencedPeopleInTransaction(Context context, PlusEvent plusevent, List list, SQLiteDatabase sqlitedatabase)
    {
        String s = plusevent.getId();
        insertMentionedPersonInTransaction(context, s, plusevent.getCreator(), list, sqlitedatabase);
        if(plusevent.getInviteeSummary() != null)
        {
            for(Iterator iterator = plusevent.getInviteeSummary().iterator(); iterator.hasNext(); insertPeopleInInviteeSummaries(context, s, list, ((InviteeSummary)iterator.next()).getInvitee(), sqlitedatabase));
        }
    }
    
    private static void insertMentionedPersonInTransaction(Context context, String s, EmbedsPerson embedsperson, List list, SQLiteDatabase sqlitedatabase)
    {
        boolean flag = false;
        if(embedsperson != null)
        {
            flag = false;
            if(list != null)
            {
                String s1 = embedsperson.getOwnerObfuscatedId();
                flag = false;
                if(s1 != null)
                {
                    for(int i = 0; i < list.size() && !flag; i++)
                    {
                        EmbedsPerson embedsperson1 = (EmbedsPerson)list.get(i);
                        if(TextUtils.equals(embedsperson1.getOwnerObfuscatedId(), embedsperson.getOwnerObfuscatedId()))
                        {
                            insertPersonInTransaction(s, embedsperson1.getOwnerObfuscatedId(), embedsperson1.getName(), embedsperson1.getImageUrl(), sqlitedatabase);
                            flag = true;
                        }
                    }

                }
            }
        }
        if(!flag && embedsperson != null && embedsperson.getOwnerObfuscatedId() != null && embedsperson.getImageUrl() != null)
            insertPersonInTransaction(s, embedsperson.getOwnerObfuscatedId(), embedsperson.getName(), embedsperson.getImageUrl(), sqlitedatabase);
    }
    
    private static void insertPersonInTransaction(String s, String s1, String s2, String s3, SQLiteDatabase sqlitedatabase)
    {
        if(null == s1 || null == s3) {
        	return;
        }
        
        String as[];
        boolean flag = true;
        as = (new String[] {
            s, s1
        });
        try {
        	DatabaseUtils.longForQuery(sqlitedatabase, "SELECT event_id FROM event_people WHERE event_id=? AND gaia_id=?", as);
        	flag = false;
        } catch (SQLiteDoneException sqlitedoneexception) {
        	flag = true;
        }
        if(flag)
        {
            ContentValues contentvalues = new ContentValues();
            contentvalues.put("event_id", s);
            contentvalues.put("gaia_id", s1);
            sqlitedatabase.insert("event_people", null, contentvalues);
        }
        EsPeopleData.replaceUserInTransaction(sqlitedatabase, s1, s2, s3);
        
    }


    private static void insertPeopleInInviteeSummaries(Context context, String s, List list, List list1, SQLiteDatabase sqlitedatabase)
    {
        if(list1 != null)
        {
            Invitee invitee;
            for(Iterator iterator = list1.iterator(); iterator.hasNext(); insertMentionedPersonInTransaction(context, s, invitee.getInviter(), list, sqlitedatabase))
            {
                invitee = (Invitee)iterator.next();
                insertMentionedPersonInTransaction(context, s, invitee.getInvitee(), list, sqlitedatabase);
            }

        }
    }

    
    private static void setViewerInfoRsvp(PlusEvent plusevent, EsAccount esaccount, String s)
    {
        if(plusevent != null)
        {
            if(plusevent.getViewerInfo() == null)
            {
                plusevent.setViewerInfo(new Invitee());
                plusevent.getViewerInfo().setInvitee(new EmbedsPerson());
                plusevent.getViewerInfo().getInvitee().setOwnerObfuscatedId(esaccount.getGaiaId());
            }
            plusevent.getViewerInfo().setRsvpType(s);
        }
    }
    
    private static void ensureFreshEventThemes(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        if(syncstate != null && syncstate.isCanceled()) 
        	return; 
        synchronized(sEventThemesLock) {
        	long l =  -1;
        	try {
        		l = DatabaseUtils.longForQuery(EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase(), "SELECT event_themes_sync_time  FROM account_status", null);
        	} catch (SQLiteDoneException sqlitedoneexception) {
        		// TODO log
        		
        	}
        	if(System.currentTimeMillis() - l > 0x5265c00L)
            {
                GetEventThemesOperation geteventthemesoperation = new GetEventThemesOperation(context, esaccount, null, null);
                geteventthemesoperation.start();
                if(geteventthemesoperation.hasError())
                    geteventthemesoperation.logError("EsEventData");
            }
        }
    }
    
    public static boolean validateInstantShare(Context context, EsAccount esaccount)
    {
        return validateInstantShare(context, esaccount, getPlusEvent(context, esaccount, InstantUpload.getInstantShareEventId(context)));
    }

    private static boolean validateInstantShare(Context context, EsAccount esaccount, PlusEvent plusevent)
    {
        boolean flag = false;
        if(EsLog.isLoggable("EsEventData", 4))
            Log.i("EsEventData", (new StringBuilder("#validateInstantShare; now: ")).append(System.currentTimeMillis()).toString());
        
        if(esaccount == null || plusevent == null) {
        	flag = false;
        } else {
           
	        String s = esaccount.getGaiaId();
	        boolean flag1 = esaccount.isPlusPage();
	        String s1 = plusevent.getId();
	        AlarmManager alarmmanager = (AlarmManager)context.getSystemService("alarm");
	        alarmmanager.cancel(Intents.getEventFinishedIntent(context, null));
	        long l = System.currentTimeMillis();
	        boolean flag2 = EsLog.isLoggable("EsEventData", 4);
	        flag = false;
	        if(flag2)
	            Log.i("EsEventData", (new StringBuilder("#validateInstantShare; cur event: ")).append(plusevent.getId()).toString());
	        flag = false;
	        if(!flag1)
	        {
	            boolean flag3 = isInstantShareAllowed(plusevent, s, l);
	            flag = false;
	            if(flag3)
	            {
	                android.app.PendingIntent pendingintent = Intents.getViewEventActivityNotificationIntent(context, esaccount, s1, plusevent.getCreatorObfuscatedId());
	                NotificationUtils.notifyInstantShareEnabled(context, plusevent.getName(), pendingintent, false);
	                long l1 = getEventEndTime(plusevent);
	                alarmmanager.set(0, l1, Intents.getEventFinishedIntent(context, s1));
	                flag = true;
	                if(EsLog.isLoggable("EsEventData", 4))
	                    Log.i("EsEventData", (new StringBuilder("#validateInstantShare; keep IS; now: ")).append(l).append(", end: ").append(l1).append(", wake in: ").append(l1 - l).toString());
	                if(EsLog.isLoggable("EsEventData", 4))
	                {
	                    StringBuilder stringbuilder = new StringBuilder("Enable Instant Share; now: ");
	                    Date date = new Date(l);
	                    StringBuilder stringbuilder1 = stringbuilder.append(DateFormat.format("MMM dd, yyyy h:mmaa", date)).append(", alarm: ");
	                    Date date1 = new Date(l1);
	                    Log.i("EsEventData", stringbuilder1.append(DateFormat.format("MMM dd, yyyy h:mmaa", date1)).toString());
	                }
	            }
	        }
        }
        if(!flag)
        {
            disableInstantShare(context);
            NotificationUtils.cancelInstantShareEnabled(context);
            if(EsLog.isLoggable("EsEventData", 4))
                Log.i("EsEventData", "Disable Instant Share");
        }
        return flag;
        
    }
    
    public static boolean isInstantShareAllowed(PlusEvent plusevent, String s, long l)
    {
        boolean flag = true;
        boolean flag1 = canAddPhotos(plusevent, s);
        boolean flag2;
        long l1;
        long l2;
        if(plusevent.getViewerInfo() != null && plusevent.getViewerInfo().getRsvpType() != null)
            flag2 = flag;
        else
            flag2 = false;
        l1 = plusevent.getStartTime().getTimeMs().longValue() - 0xa4cb80L;
        l2 = getEventEndTime(plusevent) - 5000L;
        if(!flag1 || !flag2 || l <= l1 || l >= l2)
            flag = false;
        return flag;
    }
    
    public static void refreshEvent(final Context context, final EsAccount esaccount, final String eventId)
    {
        EsService.postOnUiThread(new Runnable() {

            public final void run()
            {
                EsService.getEvent(context, esaccount, eventId);
            }
        });
    }
    
	public static class EventActivity {

        public int activityType;
        public String data;
        public String ownerGaiaId;
        public String ownerName;
        public long timestamp;
    }

    private static final class EventActivityKey {

        public final boolean equals(Object obj)
        {
            EventActivityKey eventactivitykey = (EventActivityKey)obj;
            boolean flag;
            if(type == eventactivitykey.type && TextUtils.equals(ownerGaiaId, eventactivitykey.ownerGaiaId) && timestamp == eventactivitykey.timestamp)
                flag = true;
            else
                flag = false;
            return flag;
        }

        public final int hashCode()
        {
            int i = type;
            int j;
            if(ownerGaiaId == null)
                j = 0;
            else
                j = ownerGaiaId.hashCode();
            return (int)((long)(j + i) + timestamp);
        }

        public String ownerGaiaId;
        public long timestamp;
        public int type;

    }
    
    public static HttpOperation readEventFromServer(Context context, EsAccount esaccount, String s, String s1, String s2, String s3, String s4, boolean flag, 
            boolean flag1, EsSyncAdapterService.SyncState syncstate, HttpTransactionMetrics httptransactionmetrics)
    {
    	synchronized(sEventOperationSyncObject) {
    		EventReadOperation eventreadoperation = null;
    		if(flag1) {
    			eventreadoperation = new EventReadOperation(context, esaccount, s, s3, flag, null, null);
    		} else {
    	        eventreadoperation = new EventReadOperation(context, esaccount, s, s1, s2, s3, s4, flag, null, null);
    		}
    		
    		if(syncstate != null && httptransactionmetrics != null)
	            eventreadoperation.start(syncstate, httptransactionmetrics);
	        else
	            eventreadoperation.start();
	        if(eventreadoperation.hasError())
	            eventreadoperation.logError("EsEventData");
	        
	        return eventreadoperation;
    	}
    }
    
    public static boolean updateDataPhoto(Context context, EsAccount esaccount, String s, String s1, long l, String s2)
    {
        GetPhotoOperation getphotooperation = new GetPhotoOperation(context, esaccount, null, null, l, s2);
        getphotooperation.start();
        if(!getphotooperation.hasError())
        {
            DataPhoto dataphoto = getphotooperation.getDataPhoto();
            ContentValues contentvalues = new ContentValues();
            contentvalues.put("data", dataphoto.toJsonString());
            EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().update("event_activities", contentvalues, "event_id=? AND fingerprint=?", new String[] {
                s, s1
            });
        }
        return true;
    }
    
    public static long getDisplayTime(Context context, EsAccount esaccount, PlusEvent plusevent)
    {
    	if(null == plusevent) {
    		return 0L;
    	}
    	
    	Cursor cursor = null;
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        String as[] = new String[1];
        as[0] = plusevent.getId();
        try {
	        cursor = sqlitedatabase.query("events", new String[] {
	            "display_time"
	        }, "event_id=?", as, null, null, null);
	        if(cursor.moveToFirst()) {
	        	return cursor.getLong(0);
	        }
	        return 0L;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
    
    private static final class EventActivityStatus {

        public int fingerprint;
        public String id;

    }

    public static class EventCoalescedFrame extends GenericJson {

        public List people;

		public List getPeople() {
			return people;
		}

		public void setPeople(List people) {
			this.people = people;
		}
    }

    public static class EventComment extends GenericJson {

        public String commentId;
        public boolean ownedByViewer;
        public String text;
        public long totalPlusOnes;
        
		public String getCommentId() {
			return commentId;
		}
		public void setCommentId(String commentId) {
			this.commentId = commentId;
		}
		public boolean isOwnedByViewer() {
			return ownedByViewer;
		}
		public void setOwnedByViewer(boolean ownedByViewer) {
			this.ownedByViewer = ownedByViewer;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public long getTotalPlusOnes() {
			return totalPlusOnes;
		}
		public void setTotalPlusOnes(long totalPlusOnes) {
			this.totalPlusOnes = totalPlusOnes;
		}
    }

    public static class EventPerson extends GenericJson {

        public String gaiaId;
        public String name;
        public int numAdditionalGuests;
        
		public String getGaiaId() {
			return gaiaId;
		}
		public void setGaiaId(String gaiaId) {
			this.gaiaId = gaiaId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getNumAdditionalGuests() {
			return numAdditionalGuests;
		}
		public void setNumAdditionalGuests(int numAdditionalGuests) {
			this.numAdditionalGuests = numAdditionalGuests;
		}
        
    }

    public static class InviteeList extends GenericJson {

        public List invitees;

		public List getInvitees() {
			return invitees;
		}

		public void setInvitees(List invitees) {
			this.invitees = invitees;
		}
    }

    public static final class ResolvedPerson
    {

        public String avatarUrl;
        public String gaiaId;
        public String name;

		public ResolvedPerson(String s, String s1, String s2)
        {
            name = s;
            gaiaId = s1;
            avatarUrl = s2;
        }
		
		public String getAvatarUrl() {
			return avatarUrl;
		}

		public void setAvatarUrl(String avatarUrl) {
			this.avatarUrl = avatarUrl;
		}

		public String getGaiaId() {
			return gaiaId;
		}

		public void setGaiaId(String gaiaId) {
			this.gaiaId = gaiaId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
    }

    private static final class ThemeStatus {

        String imageUrl;
        boolean isDefault;
        boolean isFeatured;
        String placeholderPath;
        int sortOrder;
    }
}
