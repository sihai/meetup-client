/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.StreamAdapter;
import com.galaxy.meetup.client.android.content.DbEmbedMedia;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.MediaImageRequest;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.util.EsLog;

/*8
 * 
 */
public class EsWidgetService extends IntentService {

	private static final int TEXT_ONLY_VIEW_IDS[];
    private static Bitmap sAuthorBitmap;
    private static int sAutoTextColor;
    private static int sContentColor;
    private static boolean sInitialized;
    public static int sWidgetImageFetchSize;

    static 
    {
        int ai[] = new int[3];
        ai[0] = R.id.text_only_content_1;
        ai[1] = R.id.text_only_content_2;
        ai[2] = R.id.text_only_content_3;
        TEXT_ONLY_VIEW_IDS = ai;
    }
    
    public EsWidgetService()
    {
        super("EsWidgetService");
    }

    private static void fetchActivities(Context context, EsAccount esaccount, int i, String s)
    {
    	int j;
        j = 1;
        s = null;
        if(EsLog.isLoggable("EsWidget", 3))
            Log.d("EsWidget", (new StringBuilder("[")).append(i).append("] loadActivities").toString());
        if(!TextUtils.equals("v.whatshot", s)) {
        	 boolean flag = TextUtils.equals("v.all.circles", s);
             j = 0;
             if(flag)
             {
                 j = 0;
                 s = null;
             } 
        }
        
        EsSyncAdapterService.SyncState syncstate;
        String s1;
        syncstate = new EsSyncAdapterService.SyncState();
        try {
        	syncstate.setFullSync(true);
        	syncstate.onSyncStart((new StringBuilder("Get activities for widget circleId: ")).append(s).append(" view: ").append(j).toString());
        	syncstate.onStart("Activities:SyncStream");
        	s1 = s;
        	EsPostsData.doActivityStreamSync(context, esaccount, j, s1, null, null, true, null, 20, null, syncstate);
        } catch (Exception e) {
        	if(EsLog.isLoggable("EsWidget", 5))
                Log.w("EsWidget", (new StringBuilder("[")).append(i).append("] loadActivities failed: ").append(e).toString());
        } finally {
        	syncstate.onFinish();
            syncstate.onSyncFinish();
        }
    }

    private static String getAutoText(Context context, long l)
    {
        int i = EsPostsData.getDefaultText(l);
        String s;
        if(i != 0)
            s = context.getString(i);
        else
            s = null;
        return s;
    }

    private Cursor loadCursor(EsAccount esaccount, int i, String s)
    {
        if(EsLog.isLoggable("EsWidget", 3))
            Log.d("EsWidget", (new StringBuilder("[")).append(i).append("] loadCursor").toString());
        Uri uri;
        Uri uri1;
        if(TextUtils.isEmpty(s) || TextUtils.equals(s, "v.all.circles"))
            uri = EsProvider.buildStreamUri(esaccount, EsPostsData.buildActivitiesStreamKey(null, null, null, true, 0));
        else
        if(TextUtils.equals("v.whatshot", s))
            uri = EsProvider.buildStreamUri(esaccount, EsPostsData.buildActivitiesStreamKey(null, null, null, true, 1));
        else
            uri = EsProvider.buildStreamUri(esaccount, EsPostsData.buildActivitiesStreamKey(null, s, null, true, 0));
        uri1 = uri.buildUpon().appendQueryParameter("limit", Integer.toString(10)).build();
        return getContentResolver().query(uri1, StreamAdapter.StreamQuery.PROJECTION_STREAM, "content_flags&32933!=0 AND content_flags&16=0", null, "sort_index ASC");
    }

    private static MediaContent readMediaContent(Cursor cursor)
    {
        MediaContent mediacontent = new MediaContent();
        byte abyte0[] = cursor.getBlob(22);
        if(abyte0 != null)
        {
            DbEmbedMedia dbembedmedia = DbEmbedMedia.deserialize(abyte0);
            if(dbembedmedia != null && !TextUtils.isEmpty(dbembedmedia.getImageUrl()))
            {
                String s = (new StringBuilder()).append(dbembedmedia.getImageUrl()).append("&google_plus:widget").toString();
                byte byte0;
                if(dbembedmedia.isVideo())
                    byte0 = 2;
                else
                    byte0 = 3;
                mediacontent.imageRequest = new MediaImageRequest(s, byte0, sWidgetImageFetchSize, sWidgetImageFetchSize, true);
                if(!TextUtils.isEmpty(dbembedmedia.getTitle()))
                {
                    String s1 = dbembedmedia.getTitle();
                    if(s1 != null)
                        mediacontent.linkTitle = s1.trim();
                }
            }
        }
        return mediacontent;
    }

    private static void showText(RemoteViews remoteviews, int i, String s, int j)
    {
        remoteviews.setViewVisibility(i, 0);
        remoteviews.setTextViewText(i, s.trim());
        remoteviews.setTextColor(i, j);
    }

    private static void showTextLayoutContent(Context context, RemoteViews remoteviews, String s, String s1, String s2, long l)
    {
        int i = TEXT_ONLY_VIEW_IDS.length;
        boolean flag = TextUtils.isEmpty(s);
        int j = 0;
        if(!flag)
        {
            j = 0;
            if(i > 0)
            {
                int ai4[] = TEXT_ONLY_VIEW_IDS;
                j = 0 + 1;
                showText(remoteviews, ai4[0], s, sContentColor);
            }
        }
        if(!TextUtils.isEmpty(s1) && j < i)
        {
            int ai3[] = TEXT_ONLY_VIEW_IDS;
            int k1 = j + 1;
            showText(remoteviews, ai3[j], s1, sContentColor);
            j = k1;
        }
        int k;
        if(!TextUtils.isEmpty(s2) && j < i)
        {
            int ai2[] = TEXT_ONLY_VIEW_IDS;
            k = j + 1;
            showText(remoteviews, ai2[j], s2, sContentColor);
        } else
        {
            k = j;
        }
        if(k == 0)
        {
            String s3 = getAutoText(context, l);
            if(!TextUtils.isEmpty(s3))
            {
                int ai1[] = TEXT_ONLY_VIEW_IDS;
                int j1 = k + 1;
                showText(remoteviews, ai1[k], s3, sAutoTextColor);
                k = j1;
            }
        }
        int i1;
        for(; k < i; k = i1)
        {
            int ai[] = TEXT_ONLY_VIEW_IDS;
            i1 = k + 1;
            remoteviews.setViewVisibility(ai[k], 8);
        }

    }
    
    protected void onHandleIntent(Intent intent)
    {
       // TODO
    }

    public void onStart(Intent intent, int i)
    {
        super.onStart(intent, i);
        int j = intent.getIntExtra("appWidgetId", 0);
        if(j != 0)
        {
            String s = EsWidgetUtils.loadCircleId(this, j);
            if(EsAccountsData.getActiveAccount(this) == null)
                EsWidgetProvider.showTapToConfigure(this, j);
            else
            if(TextUtils.isEmpty(s))
                EsWidgetProvider.showLoadingView(this, j);
            else
                EsWidgetProvider.showProgressIndicator(this, j, intent.getBooleanExtra("refresh", false));
        }
    }
    
    private static final class MediaContent {

        public MediaImageRequest imageRequest;
        public String linkTitle;

        MediaContent()
        {
        }
    }
}
