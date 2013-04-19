/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

/**
 * 
 * @author sihai
 *
 */
public class EsWidgetUtils {

	public static String loadCircleId(Context context, int i)
    {
        String s = context.getSharedPreferences(EsWidgetUtils.class.getName(), 0).getString((new StringBuilder("circleId_")).append(i).toString(), null);
        if(TextUtils.isEmpty(s))
            s = null;
        return s;
    }

    public static void saveCircleInfo(Context context, int i, String s, String s1)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(EsWidgetUtils.class.getName(), 0).edit();
        String s2 = (new StringBuilder("circleId_")).append(i).toString();
        if(s == null)
            s = "";
        editor.putString(s2, s);
        String s3 = (new StringBuilder("circleName_")).append(i).toString();
        if(s1 == null)
            s1 = "";
        editor.putString(s3, s1);
        if(android.os.Build.VERSION.SDK_INT < 9)
            editor.commit();
        else
            editor.apply();
    }

    public static void updateAllWidgets(Context context)
    {
        int ai[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, EsWidgetProvider.class));
        int i = ai.length;
        for(int j = 0; j < i; j++)
        {
            int k = ai[j];
            if(TextUtils.isEmpty(loadCircleId(context, k)))
                saveCircleInfo(context, k, "v.all.circles", null);
            EsWidgetProvider.updateWidget(context, k, null);
        }

    }
}
