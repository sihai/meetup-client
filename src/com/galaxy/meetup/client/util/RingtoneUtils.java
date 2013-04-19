/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class RingtoneUtils {

	private static String getHangoutRingtoneFileName(Context context)
    {
        return (new StringBuilder()).append(context.getResources().getResourceEntryName(R.raw.hangout_ringtone)).append(".ogg").toString();
    }

    private static String getHangoutRingtonePath(Context context)
    {
        return (new StringBuilder()).append(Environment.getExternalStorageDirectory().toString()).append(File.separator).append(context.getString(R.string.hangout_ringtone_directory)).toString();
    }

    public static void registerHangoutRingtoneIfNecessary(Context context)
    {
        Cursor cursor = null;
        boolean flag;
        File file = new File(getHangoutRingtonePath(context), getHangoutRingtoneFileName(context));
        if(!file.exists())
        {
            flag = true;
        } else {
	        Uri uri = android.provider.MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
	        cursor = context.getContentResolver().query(uri, new String[] {
	            "_data"
	        }, (new StringBuilder("_data=\"")).append(file.getAbsolutePath()).append("\"").toString(), null, null);
	        if(cursor == null || cursor.getCount() == 0)
	            flag = true;
	        else
	            flag = false;
        }
        while(flag) 
        {
            String s = getHangoutRingtonePath(context);
            String s1 = getHangoutRingtoneFileName(context);
            String s2 = context.getString(R.string.hangout_ringtone_name);
            File file1 = new File(s);
            file1.mkdirs();
            if(!file1.exists())
            {
                if(EsLog.isLoggable("RingtoneUtils", 6))
                    Log.e("RingtoneUtils", String.format("Could not create the directory %s", new Object[] {
                        s
                    }));
                break;
            }
            try
            {
                InputStream inputstream = context.getResources().openRawResource(R.raw.hangout_ringtone);
                File file2;
                try
                {
                    byte abyte0[] = new byte[inputstream.available()];
                    inputstream.read(abyte0);
                    inputstream.close();
                    FileOutputStream fileoutputstream = new FileOutputStream((new StringBuilder()).append(s).append(File.separator).append(s1).toString());
                    fileoutputstream.write(abyte0);
                    fileoutputstream.close();
                }
                catch(IOException ioexception)
                {
                    if(EsLog.isLoggable("RingtoneUtils", 6))
                        Log.e("RingtoneUtils", "Could not create a file for the Hangout ringtone", ioexception);
                    break;
                }
                file2 = new File(s, s1);
                if(!file2.exists())
                {
                    if(EsLog.isLoggable("RingtoneUtils", 6))
                        Log.e("RingtoneUtils", String.format("Could not create the file %s/%s for the Hangout ringtone", new Object[] {
                            s, s1
                        }));
                } else
                {
                    ContentValues contentvalues = new ContentValues();
                    contentvalues.put("_data", file2.getAbsolutePath());
                    contentvalues.put("title", s2);
                    contentvalues.put("mime_type", "audio/ogg");
                    contentvalues.put("_size", Long.valueOf(file2.length()));
                    contentvalues.put("artist", Integer.valueOf(R.string.app_name));
                    contentvalues.put("is_ringtone", Boolean.valueOf(true));
                    contentvalues.put("is_notification", Boolean.valueOf(true));
                    contentvalues.put("is_alarm", Boolean.valueOf(true));
                    contentvalues.put("is_music", Boolean.valueOf(false));
                    Uri uri1 = android.provider.MediaStore.Audio.Media.getContentUriForPath(file2.getAbsolutePath());
                    ContentResolver contentresolver = context.getContentResolver();
                    contentresolver.delete(uri1, (new StringBuilder("_data=\"")).append(file2.getAbsolutePath()).append("\"").toString(), null);
                    contentresolver.delete(uri1, (new StringBuilder("title=\"")).append(s2).append("\"").toString(), null);
                    Uri uri2 = contentresolver.insert(uri1, contentvalues);
                    String s3 = context.getString(R.string.hangout_ringtone_setting_key);
                    android.content.SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    editor.putString(s3, uri2.toString());
                    editor.commit();
                }
                break;
            }
            // Misplaced declaration of an exception variable
            catch(Throwable throwable)
            {
                Log.e("RingtoneUtils", "Could not register the Hangout ringtone", throwable);
                break;
            }
        }
        return;
    }
}
