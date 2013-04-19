/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.ActionTarget;
import com.galaxy.meetup.server.client.domain.ActivityDetails;
import com.galaxy.meetup.server.client.domain.ClientActionData;
import com.galaxy.meetup.server.client.domain.ClientLoggedCircle;
import com.galaxy.meetup.server.client.domain.ClientLoggedCircleMember;
import com.galaxy.meetup.server.client.domain.ClientLoggedShareboxInfo;
import com.galaxy.meetup.server.client.domain.ClientLoggedSquare;
import com.galaxy.meetup.server.client.domain.ClientOutputData;
import com.galaxy.meetup.server.client.domain.ClientOzEvent;
import com.galaxy.meetup.server.client.domain.ClientOzExtension;
import com.galaxy.meetup.server.client.domain.ClientUserInfo;
import com.galaxy.meetup.server.client.domain.FavaDiagnostics;
import com.galaxy.meetup.server.client.domain.FavaDiagnosticsMemoryStats;
import com.galaxy.meetup.server.client.domain.FavaDiagnosticsNamespacedType;
import com.galaxy.meetup.server.client.domain.NotificationTypes;
import com.galaxy.meetup.server.client.domain.OutputData;
import com.galaxy.meetup.server.client.domain.OzEvent;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EsAnalyticsData {

	private static Boolean mIsTabletDevice;
    private static final Runtime runtime = Runtime.getRuntime();
    
	public static ClientOzEvent createClientOzEvent(OzActions ozactions, OzViews ozviews, OzViews ozviews1, long l, long l1, Bundle bundle)
    {
        ClientOzEvent clientozevent = new ClientOzEvent();
        clientozevent.clientTimeMsec = Long.valueOf(l);
        OzEvent ozevent = new OzEvent();
        FavaDiagnostics favadiagnostics = new FavaDiagnostics();
        String s;
        String s1;
        String s2;
        String s3;
        if(l > 0L && l1 >= l)
            favadiagnostics.totalTimeMs = Integer.valueOf((int)(l1 - l));
        else
            favadiagnostics.totalTimeMs = Integer.valueOf(0);
        s = null;
        s1 = null;
        if(ozactions != null)
        {
            favadiagnostics.actionType = ozactions.getFavaDiagnosticsNamespacedType();
            FavaDiagnosticsNamespacedType favadiagnosticsnamespacedtype2 = favadiagnostics.actionType;
            s = null;
            s1 = null;
            if(favadiagnosticsnamespacedtype2 != null)
            {
                s1 = favadiagnostics.actionType.namespace;
                boolean flag5 = EsLog.isLoggable("EsAnalyticsData", 3);
                s = null;
                if(flag5)
                {
                    Log.d("EsAnalyticsData", (new StringBuilder("Action name: ")).append(ozactions).append(" namespace: ").append(favadiagnostics.actionType.namespace).append(" typeNum: ").append(favadiagnostics.actionType.typeNum).toString());
                    s = ozactions.name();
                }
            }
        }
        s2 = null;
        if(ozviews != null)
        {
            OutputData outputdata1 = ozviews.getViewData();
            if(outputdata1 != null)
                ozevent.startViewData = outputdata1;
            favadiagnostics.startView = ozviews.getFavaDiagnosticsNamespacedType();
            FavaDiagnosticsNamespacedType favadiagnosticsnamespacedtype1 = favadiagnostics.startView;
            s2 = null;
            if(favadiagnosticsnamespacedtype1 != null)
            {
                boolean flag4 = EsLog.isLoggable("EsAnalyticsData", 3);
                s2 = null;
                if(flag4)
                {
                    Log.d("EsAnalyticsData", (new StringBuilder("StartView name: ")).append(ozviews).append(" namespace: ").append(favadiagnostics.startView.namespace).append(" typeNum: ").append(favadiagnostics.startView.typeNum).append(" filterType: ").append(getFilterType(outputdata1)).append(" tab: ").append(getTab(outputdata1)).toString());
                    s2 = ozviews.name();
                }
            }
        }
        s3 = null;
        if(ozviews1 != null)
        {
            OutputData outputdata = ozviews1.getViewData();
            if(outputdata != null)
                ozevent.endViewData = outputdata;
            favadiagnostics.endView = ozviews1.getFavaDiagnosticsNamespacedType();
            FavaDiagnosticsNamespacedType favadiagnosticsnamespacedtype = favadiagnostics.endView;
            s3 = null;
            if(favadiagnosticsnamespacedtype != null)
            {
                s1 = favadiagnostics.endView.namespace;
                boolean flag3 = EsLog.isLoggable("EsAnalyticsData", 3);
                s3 = null;
                if(flag3)
                {
                    Log.d("EsAnalyticsData", (new StringBuilder("EndView name: ")).append(ozviews1).append(" namespace: ").append(favadiagnostics.endView.namespace).append(" typeNum: ").append(favadiagnostics.endView.typeNum).append(" filterType: ").append(getFilterType(outputdata)).append(" tab: ").append(getTab(outputdata)).toString());
                    s3 = ozviews1.name();
                }
            }
        }
        ArrayList arraylist6;
        Iterator iterator1;
        String s10;
        ClientLoggedCircleMember clientloggedcirclemember;
        String s11;
        ArrayList arraylist8;
        String s12;
        String s13;
        if((favadiagnostics.actionType != null || favadiagnostics.endView != null) && favadiagnostics.startView == null)
        {
            if(s1 == null)
            {
                favadiagnostics.startView = OzViews.UNKNOWN.getFavaDiagnosticsNamespacedType();
                s2 = OzViews.UNKNOWN.name();
            } else
            {
                favadiagnostics.startView = new FavaDiagnosticsNamespacedType();
                favadiagnostics.startView.namespace = s1;
                favadiagnostics.startView.typeNum = Integer.valueOf(0);
                s2 = (new StringBuilder("UNKNOWN:")).append(s1).toString();
            }
            if(EsLog.isLoggable("EsAnalyticsData", 3))
                Log.d("EsAnalyticsData", (new StringBuilder("StartView name: ")).append(ozviews).append(" namespace: ").append(favadiagnostics.startView.namespace).append(" typeNum: ").append(favadiagnostics.startView.typeNum).toString());
        }
        if(bundle != null)
        {
            if(bundle.containsKey("extra_start_view_extras"))
            {
                s13 = getGaiaId(bundle.getBundle("extra_start_view_extras"));
                if(!TextUtils.isEmpty(s13))
                {
                    clientozevent.startViewData = new ClientOutputData();
                    setUserInfo(clientozevent.startViewData, s13);
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: start view target gaiaId: ")).append(s13).toString());
                }
            }
            if(bundle.containsKey("extra_end_view_extras"))
            {
                s12 = getGaiaId(bundle.getBundle("extra_end_view_extras"));
                if(!TextUtils.isEmpty(s12))
                {
                    clientozevent.endViewData = new ClientOutputData();
                    setUserInfo(clientozevent.endViewData, s12);
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: end view target gaiaId: ")).append(s12).toString());
                }
            }
            if(bundle.containsKey("extra_platform_event"))
            {
                ozevent.isNativePlatformEvent = Boolean.valueOf(bundle.getBoolean("extra_platform_event", false));
                if(EsLog.isLoggable("EsAnalyticsData", 3))
                    Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: isPlatform: ")).append(ozevent.isNativePlatformEvent).toString());
            }
            if(bundle.containsKey("extra_gaia_id") || bundle.containsKey("extra_participant_ids") || bundle.containsKey("extra_circle_ids") || bundle.containsKey("extra_square_id") || bundle.containsKey("extra_posting_mode"))
                clientozevent.actionData = new ClientActionData();
            if(bundle.containsKey("extra_gaia_id"))
            {
                s11 = bundle.getString("extra_gaia_id");
                if(!TextUtils.isEmpty(s11))
                {
                    arraylist8 = new ArrayList(1);
                    arraylist8.add(s11);
                    clientozevent.actionData.obfuscatedGaiaId = arraylist8;
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: target gaiaId: ")).append(s11).toString());
                }
            }
            if(bundle.containsKey("extra_participant_ids"))
            {
                arraylist6 = bundle.getStringArrayList("extra_participant_ids");
                if(arraylist6 != null && !arraylist6.isEmpty())
                {
                    ArrayList arraylist7 = new ArrayList();
                    StringBuilder stringbuilder3 = new StringBuilder();
                    for(iterator1 = arraylist6.iterator(); iterator1.hasNext(); stringbuilder3.append((new StringBuilder()).append(s10).append(" ").toString()))
                    {
                        s10 = (String)iterator1.next();
                        clientloggedcirclemember = new ClientLoggedCircleMember();
                        clientloggedcirclemember.obfuscatedGaiaId = s10;
                        arraylist7.add(clientloggedcirclemember);
                    }

                    clientozevent.actionData.circleMember = arraylist7;
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: participants: ")).append(stringbuilder3.toString()).toString());
                }
            }
            if(bundle.containsKey("extra_circle_ids"))
            {
                ArrayList arraylist4 = bundle.getStringArrayList("extra_circle_ids");
                if(arraylist4 != null && !arraylist4.isEmpty())
                {
                    ArrayList arraylist5 = new ArrayList();
                    StringBuilder stringbuilder2 = new StringBuilder();
                    String s9;
                    for(Iterator iterator = arraylist4.iterator(); iterator.hasNext(); stringbuilder2.append((new StringBuilder()).append(s9).append(" ").toString()))
                    {
                        s9 = (String)iterator.next();
                        ClientLoggedCircle clientloggedcircle = new ClientLoggedCircle();
                        clientloggedcircle.circleId = s9;
                        arraylist5.add(clientloggedcircle);
                    }

                    clientozevent.actionData.circle = arraylist5;
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: circleIds: ")).append(stringbuilder2.toString()).toString());
                }
            }
            if(bundle.containsKey("extra_square_id"))
            {
                String s8 = bundle.getString("extra_square_id");
                if(!TextUtils.isEmpty(s8))
                {
                    clientozevent.actionData.square = new ClientLoggedSquare();
                    clientozevent.actionData.square.obfuscatedGaiaId = s8;
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: target squareId: ")).append(s8).toString());
                }
            }
            if(bundle.containsKey("extra_posting_mode"))
            {
                String s7 = bundle.getString("extra_posting_mode");
                if(!TextUtils.isEmpty(s7))
                {
                    clientozevent.actionData.shareboxInfo = new ClientLoggedShareboxInfo();
                    clientozevent.actionData.shareboxInfo.postingMode = s7;
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: postingMode: ")).append(s7).toString());
                }
            }
            if(bundle.containsKey("extra_activity_id") || bundle.containsKey("extra_comment_id") || bundle.containsKey("extra_notification_read") || bundle.containsKey("extra_notification_types") || bundle.containsKey("extra_coalescing_codes") || bundle.containsKey("extra_num_unread_notifi") || bundle.containsKey("extra_media_url") || bundle.containsKey("extra_has_emotishare") || bundle.containsKey("extra_external_url") || bundle.containsKey("extra_prev_num_unread_noti") || bundle.containsKey("extra_creation_source_id"))
                ozevent.actionTarget = new ActionTarget();
            if(bundle.containsKey("extra_activity_id"))
            {
                String s6 = bundle.getString("extra_activity_id");
                if(!TextUtils.isEmpty(s6))
                {
                    ozevent.actionTarget.activityId = s6;
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: activityId: ")).append(s6).toString());
                }
            }
            if(bundle.containsKey("extra_comment_id"))
            {
                String s5 = bundle.getString("extra_comment_id");
                if(!TextUtils.isEmpty(s5))
                {
                    ozevent.actionTarget.commentId = s5;
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: commentId: ")).append(s5).toString());
                }
            }
            ArrayList arraylist;
            ArrayList arraylist1;
            int i;
            int j;
            NotificationTypes notificationtypes;
            ArrayList arraylist3;
            String s4;
            int i1;
            int j1;
            boolean flag2;
            if(bundle.containsKey("extra_notification_read"))
            {
                boolean flag = bundle.getBoolean("extra_notification_read", false);
                ActionTarget actiontarget = ozevent.actionTarget;
                boolean flag1;
                if(!flag)
                    flag1 = true;
                else
                    flag1 = false;
                actiontarget.isUnreadNotification = Boolean.valueOf(flag1);
                if(EsLog.isLoggable("EsAnalyticsData", 3))
                {
                    StringBuilder stringbuilder1 = new StringBuilder("createClientOzEvent: isUnreadNotification: ");
                    if(!flag)
                        flag2 = true;
                    else
                        flag2 = false;
                    Log.d("EsAnalyticsData", stringbuilder1.append(flag2).toString());
                }
            }
            if(bundle.containsKey("extra_num_unread_notifi"))
            {
                j1 = bundle.getInt("extra_num_unread_notifi");
                ozevent.actionTarget.numUnreadNotifications = Integer.valueOf(j1);
                if(EsLog.isLoggable("EsAnalyticsData", 3))
                    Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: numUnreadNotifications: ")).append(j1).toString());
            }
            if(bundle.containsKey("extra_prev_num_unread_noti"))
            {
                i1 = bundle.getInt("extra_prev_num_unread_noti");
                ozevent.actionTarget.previousNumUnreadNotifications = Integer.valueOf(i1);
                if(EsLog.isLoggable("EsAnalyticsData", 3))
                    Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: previousNumUnreadNotifications: ")).append(i1).toString());
            }
            if(bundle.containsKey("extra_notification_types") && bundle.containsKey("extra_coalescing_codes"))
            {
                StringBuilder stringbuilder = new StringBuilder();
                arraylist = bundle.getIntegerArrayList("extra_notification_types");
                arraylist1 = bundle.getStringArrayList("extra_coalescing_codes");
                if(arraylist != null && arraylist1 != null && !arraylist.isEmpty() && arraylist.size() == arraylist1.size())
                {
                    ArrayList arraylist2 = new ArrayList();
                    i = 0;
                    do
                    {
                        j = arraylist.size();
                        if(i >= j)
                            break;
                        notificationtypes = new NotificationTypes();
                        arraylist3 = new ArrayList(1);
                        Integer integer = (Integer)arraylist.get(i);
                        int k;
                        if(integer == null)
                            k = 0;
                        else
                            k = integer.intValue();
                        arraylist3.add(Integer.valueOf(k));
                        notificationtypes.type = arraylist3;
                        s4 = (String)arraylist1.get(i);
                        if(!TextUtils.isEmpty(s4))
                            notificationtypes.coalescingCode = s4;
                        stringbuilder.append((new StringBuilder("(")).append(arraylist3.get(0)).append(":").append(s4).append(") ").toString());
                        arraylist2.add(notificationtypes);
                        i++;
                    } while(true);
                    if(EsLog.isLoggable("EsAnalyticsData", 3))
                        Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: notificationTypes: ")).append(stringbuilder.toString()).toString());
                    ozevent.actionTarget.notificationTypes = arraylist2;
                }
            }
            if(bundle.containsKey("extra_external_url"))
            {
                ozevent.actionTarget.externalUrl = bundle.getString("extra_external_url");
                if(EsLog.isLoggable("EsAnalyticsData", 3))
                    Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: externalUrl: ")).append(ozevent.actionTarget.externalUrl).toString());
            }
            if(bundle.containsKey("extra_has_emotishare") || bundle.containsKey("extra_media_url") || bundle.containsKey("extra_creation_source_id"))
                ozevent.actionTarget.activityDetails = new ActivityDetails();
            if(bundle.containsKey("extra_has_emotishare"))
            {
                ozevent.actionTarget.activityDetails.embedType = Integer.valueOf(334);
                if(EsLog.isLoggable("EsAnalyticsData", 3))
                    Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: embedType: ")).append(ozevent.actionTarget.activityDetails.embedType).toString());
            }
            if(bundle.containsKey("extra_media_url"))
            {
                ozevent.actionTarget.activityDetails.mediaUrl = bundle.getString("extra_media_url");
                if(EsLog.isLoggable("EsAnalyticsData", 3))
                    Log.d("EsAnalyticsData", (new StringBuilder("createClientOzEvent: mediaUrl: ")).append(ozevent.actionTarget.activityDetails.mediaUrl).toString());
            }
            if(bundle.containsKey("extra_creation_source_id"))
                ozevent.actionTarget.activityDetails.sourceStreamId = bundle.getString("extra_creation_source_id");
        }
        if(EsLog.ENABLE_DOGFOOD_FEATURES)
        {
            FavaDiagnosticsMemoryStats favadiagnosticsmemorystats = new FavaDiagnosticsMemoryStats();
            favadiagnosticsmemorystats.jsHeapSizeLimit = Long.valueOf(runtime.maxMemory());
            favadiagnosticsmemorystats.totalJsHeapSize = Long.valueOf(runtime.totalMemory());
            long l2 = runtime.freeMemory();
            favadiagnosticsmemorystats.usedJsHeapSize = Long.valueOf(favadiagnosticsmemorystats.totalJsHeapSize.longValue() - l2);
            if(EsLog.isLoggable("EsAnalyticsData", 3))
                Log.d("EsAnalyticsData", (new StringBuilder("MemoryStats Max: ")).append(favadiagnosticsmemorystats.jsHeapSizeLimit).append(" Total: ").append(favadiagnosticsmemorystats.totalJsHeapSize).append(" Used: ").append(favadiagnosticsmemorystats.usedJsHeapSize).append(" Free: ").append(l2).toString());
            favadiagnostics.memoryStats = favadiagnosticsmemorystats;
        }
        if(EsLog.isLoggable("EsAnalyticsData", 3))
            if(s == null)
                Log.d("EsAnalyticsData", String.format("EVENT SUMMARY: %s -> %s", new Object[] {
                    s2, s3
                }));
            else
            if(s3 == null)
                Log.d("EsAnalyticsData", String.format("EVENT SUMMARY: %s in %s", new Object[] {
                    s, s2
                }));
            else
                Log.d("EsAnalyticsData", String.format("EVENT SUMMARY: %s in %s (unexpected endView: %s)", new Object[] {
                    s, s2, ozviews1
                }));
        ozevent.favaDiagnostics = favadiagnostics;
        clientozevent.ozEvent = ozevent;
        return clientozevent;
    }

    public static ClientOzExtension createClientOzExtension(Context context)
    {
        ClientOzExtension clientozextension = new ClientOzExtension();
        clientozextension.sendTimeMsec = Long.valueOf(System.currentTimeMillis());
        boolean flag;
        ClientId clientid;
        if(mIsTabletDevice != null)
        {
            flag = mIsTabletDevice.booleanValue();
        } else
        {
            mIsTabletDevice = Boolean.valueOf(false);
            if(android.os.Build.VERSION.SDK_INT < 11)
            {
                flag = mIsTabletDevice.booleanValue();
            } else
            {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                WindowManager windowmanager = (WindowManager)context.getSystemService("window");
                if(windowmanager == null)
                {
                    flag = mIsTabletDevice.booleanValue();
                } else
                {
                    Display display = windowmanager.getDefaultDisplay();
                    if(display == null)
                    {
                        flag = mIsTabletDevice.booleanValue();
                    } else
                    {
                        display.getMetrics(displaymetrics);
                        if((double)displaymetrics.xdpi == 0.0D || (double)displaymetrics.ydpi == 0.0D)
                        {
                            flag = mIsTabletDevice.booleanValue();
                        } else
                        {
                            double d = (float)displaymetrics.widthPixels / displaymetrics.xdpi;
                            double d1 = (float)displaymetrics.heightPixels / displaymetrics.ydpi;
                            boolean flag1;
                            Boolean boolean1;
                            if(Math.sqrt(d * d + d1 * d1) >= 5D)
                                flag1 = true;
                            else
                                flag1 = false;
                            boolean1 = Boolean.valueOf(flag1);
                            mIsTabletDevice = boolean1;
                            flag = boolean1.booleanValue();
                        }
                    }
                }
            }
        }
        if(flag)
            clientid = ClientId.ANDROID_TABLET;
        else
            clientid = ClientId.ANDROID_OS;
        clientozextension.clientId = clientid.value();
        if(EsLog.isLoggable("EsAnalyticsData", 3))
            Log.d("EsAnalyticsData", (new StringBuilder("Set the client id to ")).append(clientid.name()).append(" ").append(clientid.value()).toString());
        try
        {
            PackageManager packagemanager = context.getPackageManager();
            String s = context.getPackageName();
            clientozextension.callingApplication = s;
            clientozextension.clientVersion = packagemanager.getPackageInfo(s, 0).versionName;
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            namenotfoundexception.printStackTrace();
        }
        return clientozextension;
    }

    public static Bundle createExtras(String s, String s1)
    {
        Bundle bundle;
        if(TextUtils.isEmpty(s) || TextUtils.isEmpty(s1))
        {
            bundle = null;
        } else
        {
            bundle = new Bundle();
            bundle.putString(s, s1);
        }
        return bundle;
    }

    private static Integer getFilterType(OutputData outputdata)
    {
        Integer integer;
        if(outputdata == null || outputdata.filterType == null)
            integer = null;
        else
            integer = outputdata.filterType;
        return integer;
    }

    private static String getGaiaId(Bundle bundle)
    {
        String s;
        if(bundle == null)
            s = null;
        else
            s = bundle.getString("extra_gaia_id");
        return s;
    }

    private static Integer getTab(OutputData outputdata)
    {
        Integer integer;
        if(outputdata == null || outputdata.tab == null)
            integer = null;
        else
            integer = outputdata.tab;
        return integer;
    }

    public static void insert(Context context, EsAccount esaccount, byte abyte0[])
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("event_data", abyte0);
        sqlitedatabase.insert("analytics_events", null, contentvalues);
    }
    
    public static void bulkInsert(Context context, EsAccount esaccount, List list)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
        	sqlitedatabase.beginTransaction();
        	ContentValues contentvalues = new ContentValues();
        	for(Iterator iterator = list.iterator(); iterator.hasNext(); sqlitedatabase.insert("analytics_events", null, contentvalues))
        		contentvalues.put("event_data", JsonUtil.toByteArray((ClientOzEvent)iterator.next()));
        	sqlitedatabase.setTransactionSuccessful();
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public static long queryLastAnalyticsSyncTimestamp(Context context, EsAccount esaccount)
    {
    	try {
    		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
    		return DatabaseUtils.longForQuery(sqlitedatabase, "SELECT last_analytics_sync_time  FROM account_status", null);
    	} catch (SQLiteDoneException sqlitedoneexception) {
    		// TODO log
    		return -1L;
    	}
    }

    public static List removeAll(Context context, EsAccount esaccount)
    {
        ArrayList arraylist = new ArrayList();
        Cursor cursor = null;
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
        	sqlitedatabase.beginTransaction();
        	cursor = sqlitedatabase.query("analytics_events", new String[] {
        			"event_data"
        	}, null, null, null, null, null);
        	if(null != cursor) {
        		int i;
                int j;
                i = cursor.getColumnIndexOrThrow("event_data");
                j = 0;
                while(cursor.moveToPosition(j++)) {
                	byte abyte0[] = cursor.getBlob(i);
                    ClientOzEvent clientozevent = (ClientOzEvent)JsonUtil.fromByteArray(abyte0, ClientOzEvent.class);
                    if(clientozevent != null)
                        arraylist.add(clientozevent);
                }
                sqlitedatabase.delete("analytics_events", null, null);
        	}
        	sqlitedatabase.setTransactionSuccessful();
        } finally {
        	sqlitedatabase.endTransaction();
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        return arraylist;
    }

    public static void saveLastAnalyticsSyncTimestamp(Context context, EsAccount esaccount, long l)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("last_analytics_sync_time", Long.valueOf(l));
        sqlitedatabase.update("account_status", contentvalues, null, null);
    }

    private static void setUserInfo(ClientOutputData clientoutputdata, String s)
    {
        if(!TextUtils.isEmpty(s) && clientoutputdata != null)
        {
            ClientUserInfo clientuserinfo = new ClientUserInfo();
            clientuserinfo.obfuscatedGaiaId = s;
            clientoutputdata.userInfo = new ArrayList(1);
            clientoutputdata.userInfo.add(clientuserinfo);
        }
    }
    
    
    static enum ClientId {
    	ANDROID_OS("4"),
    	ANDROID_TABLET("10");
    	
    	private final String mValue;
    	
    	private ClientId(String value) {
    		this.mValue = value;
    	}
    	
    	public final String value()
        {
            return mValue;
        }
    }
}
