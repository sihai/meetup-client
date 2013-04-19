/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.content.SharedPreferences;

import com.galaxy.meetup.client.android.service.Hangout;

/**
 * 
 * @author sihai
 *
 */
public class ExitHistory {

	static final boolean $assertionsDisabled;

    static 
    {
        boolean flag;
        if(!ExitHistory.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
	public ExitHistory()
    {
    }

    public static boolean exitReported(Context context, Hangout.Info info)
    {
        SharedPreferences sharedpreferences = findPrefs(context, info);
        boolean flag = false;
        if(sharedpreferences != null)
            flag = sharedpreferences.getBoolean("EXIT_REPORTED", false);
        return flag;
    }

    public static boolean exitedNormally(Context context, Hangout.Info info)
    {
        SharedPreferences sharedpreferences = findPrefs(context, info);
        boolean flag = false;
        if(sharedpreferences != null)
        {
            int i = sharedpreferences.getInt("LAST_ERROR", -1);
            flag = false;
            if(i == -1)
                flag = true;
        }
        return flag;
    }

    private static SharedPreferences findPrefs(Context context, Hangout.Info info)
    {
        SharedPreferences sharedpreferences;
        Hangout.Info info1;
        SharedPreferences sharedpreferences1;
        sharedpreferences = context.getSharedPreferences(ExitHistory.class.getName(), 0);
        if(!sharedpreferences.getBoolean("INFO_HAS_INFO", false))
            info1 = null;
        else
            info1 = new Hangout.Info(Hangout.RoomType.values()[sharedpreferences.getInt("INFO_ROOM_TYPE", 0)], sharedpreferences.getString("INFO_DOMAIN", ""), null, sharedpreferences.getString("INFO_ID", ""), null, Hangout.LaunchSource.None, false);
        sharedpreferences1 = null;
        if(null == info1) {
        	return sharedpreferences1;
        }
        
        boolean flag = info1.equals(info);
        sharedpreferences1 = null;
        if(flag)
            sharedpreferences1 = sharedpreferences;
        return sharedpreferences1;
    }

    public static GCommNativeWrapper.Error getError(Context context, Hangout.Info info)
    {
        SharedPreferences sharedpreferences = findPrefs(context, info);
        GCommNativeWrapper.Error error = null;
        if(sharedpreferences != null)
        {
            int i = sharedpreferences.getInt("LAST_ERROR", -1);
            error = null;
            if(i != -1)
                error = GCommNativeWrapper.Error.values()[i];
        }
        return error;
    }

    public static void recordErrorExit(Context context, Hangout.Info info, GCommNativeWrapper.Error error, boolean flag)
    {
        if(!$assertionsDisabled && error.ordinal() == -1)
        {
            throw new AssertionError();
        } else
        {
            recordExit(context, info, error.ordinal(), flag);
            return;
        }
    }

    private static void recordExit(Context context, Hangout.Info info, int i, boolean flag)
    {
        if(info != null)
        {
            android.content.SharedPreferences.Editor editor = context.getSharedPreferences(ExitHistory.class.getName(), 0).edit();
            editor.putBoolean("INFO_HAS_INFO", true);
            editor.putInt("INFO_ROOM_TYPE", info.getRoomType().ordinal());
            editor.putString("INFO_DOMAIN", info.getDomain());
            editor.putString("INFO_ID", info.getId());
            editor.putInt("LAST_ERROR", i);
            editor.putBoolean("EXIT_REPORTED", flag);
            editor.commit();
        }
    }

    public static void recordExitReported(Context context, Hangout.Info info)
    {
        recordExit(context, info, -1, true);
    }

    public static void recordNormalExit(Context context, Hangout.Info info, boolean flag)
    {
        recordExit(context, info, -1, flag);
    }
}
