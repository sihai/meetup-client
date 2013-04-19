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
import android.util.Log;
import android.widget.RemoteViews;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class EsFroyoWidgetProvider extends AppWidgetProvider {

	public EsFroyoWidgetProvider()
    {
    }

    public void onUpdate(Context context, AppWidgetManager appwidgetmanager, int ai[])
    {
        if(EsLog.isLoggable("EsFroyoWidgetProvider", 3))
            Log.d("EsFroyoWidgetProvider", "onUpdate");
        for(int i = 0; i < ai.length; i++)
        {
            int j = ai[i];
            if(EsLog.isLoggable("EsFroyoWidgetProvider", 3))
                Log.d("EsFroyoWidgetProvider", (new StringBuilder("configureWidget(")).append(j).append(")").toString());
            RemoteViews remoteviews = new RemoteViews(context.getPackageName(), R.layout.widget_froyo_layout);
            Intent intent = Intents.getStreamActivityIntent(context, null);
            intent.setAction("com.google.android.apps.plus.widget.HOME_ACTION");
            PendingIntent pendingintent = PendingIntent.getActivity(context, 0, intent, 0x8000000);
            remoteviews.setOnClickPendingIntent(R.id.home_icon, pendingintent);
            // TODO ???
            Intent intent1 = Intents.getPostActivityIntent(context, null, (ArrayList)null);
            intent1.setAction("com.google.android.apps.plus.widget.POST_ACTION");
            intent1.removeExtra("account");
            PendingIntent pendingintent1 = PendingIntent.getActivity(context, 0, intent1, 0x8000000);
            remoteviews.setOnClickPendingIntent(R.id.new_post, pendingintent1);
            Intent intent2 = Intents.getWidgetCameraLauncherActivityIntent(context, null);
            intent2.setAction("com.google.android.apps.plus.widget.CAMERA_ACTION");
            intent2.removeExtra("account");
            PendingIntent pendingintent2 = PendingIntent.getActivity(context, 0, intent2, 0x8000000);
            remoteviews.setOnClickPendingIntent(R.id.camera_icon, pendingintent2);
            AppWidgetManager.getInstance(context).updateAppWidget(j, remoteviews);
        }

        super.onUpdate(context, appwidgetmanager, ai);
    }
}
