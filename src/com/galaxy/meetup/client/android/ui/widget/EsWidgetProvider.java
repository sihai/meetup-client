/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.widget;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class EsWidgetProvider extends AppWidgetProvider {

	public EsWidgetProvider()
    {
    }

    public static void configureHeaderButtons(Context context, EsAccount esaccount, int i, RemoteViews remoteviews, String s, boolean flag)
    {
        Intent intent = Intents.getStreamActivityIntent(context, esaccount);
        intent.setAction("com.google.android.apps.plus.widget.HOME_ACTION");
        PendingIntent pendingintent = PendingIntent.getActivity(context, 0, intent, 0x8000000);
        remoteviews.setOnClickPendingIntent(R.id.home_icon, pendingintent);
        boolean flag1;
        boolean flag2;
        int j;
        int k;
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        int k2;
        if(esaccount != null)
            flag1 = true;
        else
            flag1 = false;
        if(!TextUtils.isEmpty(s))
            flag2 = true;
        else
            flag2 = false;
        remoteviews.setViewVisibility(R.id.refresh_progress, 8);
        remoteviews.setViewVisibility(R.id.next_progress, 8);
        j = R.id.post_icon;
        if(flag1)
            k = 0;
        else
            k = 8;
        remoteviews.setViewVisibility(j, k);
        l = R.id.refresh_icon;
        if(flag)
            i1 = 0;
        else
            i1 = 8;
        remoteviews.setViewVisibility(l, i1);
        j1 = R.id.next_icon;
        if(flag2)
            k1 = 0;
        else
            k1 = 8;
        remoteviews.setViewVisibility(j1, k1);
        l1 = R.id.divider_1;
        if(flag1 && (flag || flag2))
            i2 = 0;
        else
            i2 = 8;
        remoteviews.setViewVisibility(l1, i2);
        j2 = R.id.divider_2;
        if(flag && flag2)
            k2 = 0;
        else
            k2 = 8;
        remoteviews.setViewVisibility(j2, k2);
        if(flag1)
        {
            Intent intent1 = Intents.getPostActivityIntent(context, esaccount, (ArrayList)null);
            intent1.setAction("com.google.android.apps.plus.widget.POST_ACTION");
            PendingIntent pendingintent3 = PendingIntent.getActivity(context, 0, intent1, 0x8000000);
            remoteviews.setOnClickPendingIntent(R.id.post_icon, pendingintent3);
        }
        if(flag)
        {
            PendingIntent pendingintent2 = PendingIntent.getService(context, 0, getWidgetUpdateIntent(context, i, null, true), 0x8000000);
            remoteviews.setOnClickPendingIntent(R.id.refresh_icon, pendingintent2);
        }
        if(flag2)
        {
            PendingIntent pendingintent1 = PendingIntent.getService(context, 0, getWidgetUpdateIntent(context, i, s, false), 0x8000000);
            remoteviews.setOnClickPendingIntent(R.id.next_icon, pendingintent1);
        }
    }

    public static void configureWidget(Context context, EsAccount esaccount, int i)
    {
        if(EsLog.isLoggable("EsWidget", 3))
            Log.d("EsWidget", (new StringBuilder("[")).append(i).append("] configureWidget").toString());
        if(esaccount == null)
        {
            showTapToConfigure(context, i);
        } else
        {
            showLoadingView(context, i);
            updateWidget(context, i, null);
        }
    }

    private static Intent getWidgetUpdateIntent(Context context, int i, String s, boolean flag)
    {
        Intent intent = new Intent(context, EsWidgetService.class);
        intent.putExtra("appWidgetId", i);
        if(!TextUtils.isEmpty(s))
            intent.putExtra("activity_id", s);
        if(flag)
            intent.putExtra("refresh", true);
        intent.setData(Uri.parse(intent.toUri(1)));
        return intent;
    }

    public static void showLoadingView(Context context, int i)
    {
        RemoteViews remoteviews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        configureHeaderButtons(context, null, i, remoteviews, null, false);
        remoteviews.setViewVisibility(R.id.next_progress, 0);
        remoteviews.setViewVisibility(R.id.widget_image_layout, 8);
        remoteviews.setViewVisibility(R.id.widget_text_layout, 8);
        remoteviews.setViewVisibility(R.id.widget_empty_layout, 0);
        remoteviews.setTextViewText(R.id.empty_view, context.getString(R.string.loading));
        AppWidgetManager.getInstance(context).updateAppWidget(i, remoteviews);
    }

    public static void showNoPostsFound(Context context, EsAccount esaccount, int i)
    {
        if(EsLog.isLoggable("EsWidget", 3))
            Log.d("EsWidget", (new StringBuilder("[")).append(i).append("] showNoPostsFound").toString());
        RemoteViews remoteviews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        configureHeaderButtons(context, esaccount, i, remoteviews, null, true);
        remoteviews.setViewVisibility(R.id.widget_image_layout, 8);
        remoteviews.setViewVisibility(R.id.widget_text_layout, 8);
        remoteviews.setViewVisibility(R.id.widget_empty_layout, 0);
        remoteviews.setTextViewText(R.id.empty_view, context.getString(R.string.no_posts));
        String s = EsWidgetUtils.loadCircleId(context, i);
        if(TextUtils.isEmpty(s))
            s = "v.all.circles";
        Intent intent = Intents.getCirclePostsActivityIntent(context, esaccount, s);
        intent.setAction((new StringBuilder("com.galaxy.meetup.client.android.ui.widget.CIRCLE_ACTION")).append(i).toString());
        PendingIntent pendingintent = PendingIntent.getActivity(context, 0, intent, 0x8000000);
        remoteviews.setOnClickPendingIntent(R.id.widget_main, pendingintent);
        AppWidgetManager.getInstance(context).updateAppWidget(i, remoteviews);
    }

    public static void showProgressIndicator(Context context, int i, boolean flag)
    {
        if(EsLog.isLoggable("EsWidget", 3))
            Log.d("EsWidget", (new StringBuilder("[")).append(i).append("] showProgressIndicator").toString());
        RemoteViews remoteviews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        remoteviews.setTextViewText(R.id.empty_view, context.getString(R.string.loading));
        AppWidgetManager appwidgetmanager;
        boolean flag1;
        if(flag)
        {
            remoteviews.setViewVisibility(R.id.refresh_icon, 8);
            remoteviews.setViewVisibility(R.id.refresh_progress, 0);
            PendingIntent pendingintent = PendingIntent.getService(context, 0, new Intent(context, EsWidgetService.class), 0);
            remoteviews.setOnClickPendingIntent(R.id.next_icon, pendingintent);
        } else
        {
            remoteviews.setViewVisibility(R.id.next_icon, 8);
            remoteviews.setViewVisibility(R.id.next_progress, 0);
        }
        appwidgetmanager = AppWidgetManager.getInstance(context);
        if(android.os.Build.VERSION.SDK_INT >= 11)
            flag1 = true;
        else
            flag1 = false;
        if(flag1)
        {
            appwidgetmanager.partiallyUpdateAppWidget(i, remoteviews);
        } else
        {
            remoteviews.setViewVisibility(R.id.widget_empty_layout, 0);
            remoteviews.setViewVisibility(R.id.widget_image_layout, 8);
            remoteviews.setViewVisibility(R.id.widget_text_layout, 8);
            appwidgetmanager.updateAppWidget(i, remoteviews);
        }
    }

    public static void showTapToConfigure(Context context, int i)
    {
        if(EsLog.isLoggable("EsWidget", 3))
            Log.d("EsWidget", (new StringBuilder("[")).append(i).append("] showTapToConfigure").toString());
        RemoteViews remoteviews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        configureHeaderButtons(context, null, i, remoteviews, null, false);
        remoteviews.setViewVisibility(R.id.widget_image_layout, 8);
        remoteviews.setViewVisibility(R.id.widget_text_layout, 8);
        remoteviews.setViewVisibility(R.id.widget_empty_layout, 0);
        remoteviews.setTextViewText(R.id.empty_view, context.getString(R.string.widget_tap_to_configure));
        PendingIntent pendingintent = PendingIntent.getActivity(context, 0, Intents.getStreamActivityIntent(context, null), 0x8000000);
        remoteviews.setOnClickPendingIntent(R.id.widget_main, pendingintent);
        AppWidgetManager.getInstance(context).updateAppWidget(i, remoteviews);
    }

    public static void updateWidget(Context context, int i, String s)
    {
        context.startService(getWidgetUpdateIntent(context, i, null, false));
    }

    public void onDeleted(Context context, int ai[])
    {
        int i = ai.length;
        int j = 0;
        while(j < i) 
        {
            int k = ai[j];
            if(EsLog.isLoggable("EsWidget", 3))
                Log.d("EsWidget", (new StringBuilder("[")).append(k).append("] onDeleted").toString());
            android.content.SharedPreferences.Editor editor = context.getSharedPreferences(EsWidgetUtils.class.getName(), 0).edit();
            editor.remove((new StringBuilder("circleId_")).append(k).toString());
            editor.remove((new StringBuilder("circleName_")).append(k).toString());
            if(android.os.Build.VERSION.SDK_INT < 9)
                editor.commit();
            else
                editor.apply();
            j++;
        }
    }

    public void onUpdate(Context context, AppWidgetManager appwidgetmanager, int ai[])
    {
        int i = ai.length;
        for(int j = 0; j < i; j++)
        {
            int k = ai[j];
            if(EsLog.isLoggable("EsWidget", 3))
                Log.d("EsWidget", (new StringBuilder("[")).append(k).append("] onUpdate").toString());
            showLoadingView(context, k);
            updateWidget(context, k, null);
        }

    }
}
