/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.analytics;

import java.util.Iterator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.content.DbEmotishareMetadata;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.ClientOzEvent;
import com.galaxy.meetup.server.client.domain.FavaDiagnosticsNamespacedType;

public class EsAnalytics {

	public static Bundle addExtrasForLogging(Bundle bundle, DbEmotishareMetadata dbemotisharemetadata)
    {
        if(dbemotisharemetadata != null)
        {
            if(bundle == null)
                bundle = new Bundle();
            bundle.putBoolean("extra_has_emotishare", true);
            String s = dbemotisharemetadata.getImageUrl();
            if(!TextUtils.isEmpty(s))
            {
                android.net.Uri.Builder builder = Uri.parse(s).buildUpon();
                builder.appendQueryParameter("type", dbemotisharemetadata.getType());
                bundle.putString("extra_media_url", builder.toString());
            }
        }
        return bundle;
    }

    private static String getDisplayTextForExtras(Bundle bundle)
    {
        String s;
        if(bundle == null)
        {
            s = "none";
        } else
        {
            StringBuilder stringbuilder = new StringBuilder();
            String s1;
            for(Iterator iterator = bundle.keySet().iterator(); iterator.hasNext(); stringbuilder.append((new StringBuilder("(")).append(s1).append(":").append(bundle.get(s1)).append(")").toString()))
                s1 = (String)iterator.next();

            s = stringbuilder.toString();
        }
        return s;
    }

    public static void postRecordEvent(Context context, EsAccount esaccount, AnalyticsInfo analyticsinfo, OzActions ozactions)
    {
        postRecordEvent(context, esaccount, analyticsinfo, ozactions, null);
    }

    public static void postRecordEvent(Context context, EsAccount esaccount, AnalyticsInfo analyticsinfo, OzActions ozactions, Bundle bundle)
    {
        final Context context1 = context.getApplicationContext();
        final EsAccount account = esaccount;
        final AnalyticsInfo analytics = analyticsinfo;
        final OzActions action = ozactions;
        final Bundle extras = bundle;
        (new Handler(Looper.getMainLooper())).post(new Runnable() {

            public final void run()
            {
                Context context2 = context1;
                EsAccount esaccount1 = account;
                AnalyticsInfo analyticsinfo1;
                if(analytics == null)
                    analyticsinfo1 = new AnalyticsInfo();
                else
                    analyticsinfo1 = analytics;
                EsAnalytics.recordEvent(context2, esaccount1, analyticsinfo1, action, extras);
            }

        });
    }

    public static void recordActionEvent(Context context, EsAccount esaccount, OzActions ozactions, OzViews ozviews)
    {
        recordActionEvent(context, esaccount, ozactions, ozviews, ((Bundle) (null)));
    }

    public static void recordActionEvent(Context context, EsAccount esaccount, OzActions ozactions, OzViews ozviews, long l)
    {
        recordActionEvent(context, esaccount, ozactions, ozviews, l, System.currentTimeMillis(), null);
    }

    private static void recordActionEvent(Context context, EsAccount esaccount, OzActions ozactions, OzViews ozviews, long l, long l1, 
            Bundle bundle)
    {
        ClientOzEvent clientozevent = EsAnalyticsData.createClientOzEvent(ozactions, ozviews, null, l, l1, bundle);
        if(EsLog.isLoggable("EsAnalytics", 3))
            Log.d("EsAnalytics", (new StringBuilder("recordActionEvent action: ")).append(OzActions.getName(ozactions)).append(" startView: ").append(OzViews.getName(ozviews)).append(" startTime: ").append(l).append(" endTime: ").append(l1).append(" extras: ").append(getDisplayTextForExtras(bundle)).toString());
        recordEvent(context, esaccount, clientozevent);
    }

    public static void recordActionEvent(Context context, EsAccount esaccount, OzActions ozactions, OzViews ozviews, Bundle bundle)
    {
        long l = System.currentTimeMillis();
        recordActionEvent(context, esaccount, ozactions, ozviews, l, l, bundle);
    }

    public static long recordEvent(Context context, EsAccount esaccount, AnalyticsInfo analyticsinfo, OzActions ozactions)
    {
        return recordEvent(context, esaccount, analyticsinfo, ozactions, null);
    }

    public static long recordEvent(Context context, EsAccount esaccount, AnalyticsInfo analyticsinfo, OzActions ozactions, Bundle bundle)
    {
        long l = System.currentTimeMillis();
        if(EsLog.isLoggable("EsAnalytics", 3))
            Log.d("EsAnalytics", (new StringBuilder("recordEvent action: ")).append(OzActions.getName(ozactions)).append(" startView: ").append(OzViews.getName(analyticsinfo.getStartView())).append(" endView: ").append(OzViews.getName(analyticsinfo.getEndView())).append(" startTime: ").append(analyticsinfo.getStartTimeMsec()).append(" endTime: ").append(l).append(" extras: ").append(getDisplayTextForExtras(bundle)).toString());
        recordEvent(context, esaccount, EsAnalyticsData.createClientOzEvent(ozactions, analyticsinfo.getStartView(), analyticsinfo.getEndView(), analyticsinfo.getStartTimeMsec(), l, bundle));
        return l;
    }

    private static void recordEvent(Context context, EsAccount esaccount, ClientOzEvent clientozevent)
    {
        if(esaccount == null || clientozevent == null) 
        	return; 
        
        EsService.insertEvent(context, esaccount, clientozevent);
        if(EsLog.isLoggable("EsAnalytics", 3) && clientozevent != null && clientozevent.ozEvent != null && clientozevent.ozEvent.favaDiagnostics != null) {
        	Log.d("EsAnalytics", (new StringBuilder("logAction clientTimeMsec: ")).append(clientozevent.clientTimeMsec).append(" totalTimeMs: ").append(clientozevent.ozEvent.favaDiagnostics.totalTimeMs).toString());
            FavaDiagnosticsNamespacedType favadiagnosticsnamespacedtype = clientozevent.ozEvent.favaDiagnostics.startView;
            if(favadiagnosticsnamespacedtype != null)
                Log.d("EsAnalytics", (new StringBuilder("> startView namespace: ")).append(favadiagnosticsnamespacedtype.namespace).append(" typeNum: ").append(favadiagnosticsnamespacedtype.typeNum).toString());
            FavaDiagnosticsNamespacedType favadiagnosticsnamespacedtype1 = clientozevent.ozEvent.favaDiagnostics.endView;
            if(favadiagnosticsnamespacedtype1 != null)
                Log.d("EsAnalytics", (new StringBuilder("> endView namespace: ")).append(favadiagnosticsnamespacedtype1.namespace).append(" typeNum: ").append(favadiagnosticsnamespacedtype1.typeNum).toString());
            FavaDiagnosticsNamespacedType favadiagnosticsnamespacedtype2 = clientozevent.ozEvent.favaDiagnostics.actionType;
            if(favadiagnosticsnamespacedtype2 != null)
                Log.d("EsAnalytics", (new StringBuilder("> action namespace: ")).append(favadiagnosticsnamespacedtype2.namespace).append(" typeNum: ").append(favadiagnosticsnamespacedtype2.typeNum).toString());
        }
    }

    public static void recordImproveSuggestionsPreferenceChange(Context context, EsAccount esaccount, boolean flag, OzViews ozviews)
    {
        String s;
        OzActions ozactions;
        if(flag)
            ozactions = OzActions.ENABLE_IMPROVE_SUGGESTIONS;
        else
            ozactions = OzActions.DISABLE_IMPROVE_SUGGESTIONS;
        recordActionEvent(context, esaccount, ozactions, ozviews);
        if(!flag) 
        	return; 
        
        if(context == null) 
        	s = null;
        else {
        	TelephonyManager telephonymanager = (TelephonyManager)context.getSystemService("phone");
        	if(telephonymanager == null) {
        		s = null;
        	} else {
        		s = telephonymanager.getLine1Number();
        	}
        }
        
    }

    public static void recordNavigationEvent(Context context, EsAccount esaccount, OzViews ozviews, OzViews ozviews1, Long long1, Long long2, Bundle bundle, Bundle bundle1)
    {
        recordNavigationEvent(context, esaccount, ozviews, ozviews1, long1, null, bundle, bundle1, null);
    }

    public static void recordNavigationEvent(Context context, EsAccount esaccount, OzViews ozviews, OzViews ozviews1, Long long1, Long long2, Bundle bundle, Bundle bundle1, 
            Bundle bundle2)
    {
        long l = System.currentTimeMillis();
        if(long1 == null)
            long1 = Long.valueOf(l);
        if(long2 == null)
            long2 = Long.valueOf(l);
        if(bundle2 == null)
            bundle2 = new Bundle();
        if(bundle != null && !bundle.isEmpty())
            bundle2.putBundle("extra_start_view_extras", bundle);
        if(bundle1 != null && !bundle1.isEmpty())
            bundle2.putBundle("extra_end_view_extras", bundle1);
        long l1 = long1.longValue();
        long l2 = long2.longValue();
        Bundle bundle3;
        ClientOzEvent clientozevent;
        if(bundle2.isEmpty())
            bundle3 = null;
        else
            bundle3 = bundle2;
        clientozevent = EsAnalyticsData.createClientOzEvent(null, ozviews, ozviews1, l1, l2, bundle3);
        if(EsLog.isLoggable("EsAnalytics", 3))
            Log.d("EsAnalytics", (new StringBuilder("recordNavigationEvent startView: ")).append(OzViews.getName(ozviews)).append(" endView: ").append(OzViews.getName(ozviews1)).append(" startTime: ").append(long1).append(" endTime: ").append(long2).toString());
        recordEvent(context, esaccount, clientozevent);
    }
}
