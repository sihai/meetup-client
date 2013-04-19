/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class HangoutNotifications {

	private static File copyResourceToFile(int i, Context context)
    {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS);
        File file1 = new File(file, "hangout_dingtone.m4a");
        try
        {
            if(file1.exists())
            {
                if(EsLog.isLoggable("ExternalStorageUtils", 4))
                    android.util.Log.i("ExternalStorageUtils", "Notification sound already present");
            } else
            {
                file.mkdirs();
                if(EsLog.isLoggable("ExternalStorageUtils", 3))
                	android.util.Log.d("ExternalStorageUtils", (new StringBuilder("Copy notification to ")).append(file1.toString()).toString());
                InputStream inputstream = context.getResources().openRawResource(i);
                byte abyte0[] = new byte[inputstream.available()];
                inputstream.read(abyte0);
                inputstream.close();
                FileOutputStream fileoutputstream = new FileOutputStream(file1);
                fileoutputstream.write(abyte0);
                fileoutputstream.flush();
                fileoutputstream.close();
            }
        }
        catch(IOException ioexception)
        {
        	android.util.Log.w("ExternalStorageUtils", (new StringBuilder("Error writing to ")).append(file1.toString()).toString(), ioexception);
        }
        return file1;
    }
	
	public static Uri getDingtone(Context context)
    {
        String s1;
        String s = context.getString(R.string.hangout_dingtone_setting_key);
        s1 = PreferenceManager.getDefaultSharedPreferences(context).getString(s, null);
        Uri uri;
        if(s1 == null) {
        	 boolean flag = EsLog.isLoggable("ExternalStorageUtils", 4);
             uri = null;
             if(flag)
             {
            	 android.util.Log.i("ExternalStorageUtils", "Hangout dingtone not set");
                 uri = null;
             } 
        } else { 
        	if(EsLog.isLoggable("ExternalStorageUtils", 4))
        		android.util.Log.i("ExternalStorageUtils", (new StringBuilder("Hangout dingtone; uri: ")).append(s1).toString());
            uri = Uri.parse(s1);
        }
        
        return uri;
    }

    public static void registerHangoutSounds(final Context context)
    {
        File file = copyResourceToFile(R.raw.hangout_dingtone, context);
        String as[] = new String[1];
        as[0] = file.toString();
        MediaScannerConnection.scanFile(context, as, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {

            public final void onScanCompleted(String s, Uri uri)
            {
                if(EsLog.isLoggable("ExternalStorageUtils", 4))
                	android.util.Log.i("ExternalStorageUtils", (new StringBuilder("Scan complete; uri: ")).append(uri).toString());
                if(!HangoutNotifications.access$000(context))
                	HangoutNotifications.access$100(context, uri);
            }
        });
    }
    
    static boolean access$000(Context context)
    {
        boolean flag;
        if(getDingtone(context) != null)
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    static void access$100(Context context, Uri uri)
    {
        String s = context.getString(R.string.hangout_dingtone_setting_key);
        android.content.SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(s, uri.toString());
        editor.commit();
        if(EsLog.isLoggable("ExternalStorageUtils", 4))
        	android.util.Log.i("ExternalStorageUtils", (new StringBuilder("Hangout dingtone set; uri: ")).append(uri).toString());
        return;
    }
}