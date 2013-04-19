/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 
 * @author sihai
 *
 */
public class InstantUploadFacade {

	private static final Uri BASE_URI;
    public static final Uri INSTANT_UPLOAD_URI;
    public static final Uri PHOTOS_URI;
    public static final Uri SETTINGS_URI;
    public static final Uri UPLOADS_URI;
    public static final Uri UPLOAD_ALL_URI;
    private static Class sNetworkReceiver;
    
    static  {
        Uri uri = Uri.parse("content://com.galaxy.meetup.client.android.iu.EsGalaxyIuProvider");
        BASE_URI = uri;
        UPLOADS_URI = Uri.withAppendedPath(uri, "uploads");
        UPLOAD_ALL_URI = Uri.withAppendedPath(BASE_URI, "upload_all");
        INSTANT_UPLOAD_URI = Uri.withAppendedPath(BASE_URI, "iu");
        SETTINGS_URI = Uri.withAppendedPath(BASE_URI, "settings");
        PHOTOS_URI = Uri.withAppendedPath(BASE_URI, "photos");
    }
    
    public static void broadcastOperationReport(Context context, String s, long l, long l1, int i, long l2, long l3) {
        if(sNetworkReceiver != null) {
            Intent intent = new Intent(context, sNetworkReceiver);
            intent.setAction("com.google.android.apps.plus.iu.op_report");
            intent.putExtra("op_name", s);
            intent.putExtra("total_time", l);
            intent.putExtra("net_duration", l1);
            intent.putExtra("transaction_count", i);
            intent.putExtra("sent_bytes", l2);
            intent.putExtra("received_bytes", l3);
            context.sendBroadcast(intent);
        }
    }

    public static Uri getUploadUri(long l) {
        return UPLOADS_URI.buildUpon().appendPath(String.valueOf(l)).build();
    }

    public static boolean isOutOfQuota(int i, int j) {
        boolean flag;
        if(i - j < 5)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static void requestUploadSync(Context context) {
        InstantUploadSyncManager.getInstance(context).updateTasks(0L);
    }

    public static void setNetworkReceiver(Class class1) {
        sNetworkReceiver = class1;
    }
    
    public static String stateToString(int i)
    {
    	String s = null;
    	switch(i) {
    		default:
    			s = "unknown";
    			break;
    		case 0:
    			s = "SYNC_STATE_IDLE";
    			break;
    		case 1:
    			 s = "SYNC_STATE_SYNCING";
    			 break;
    		case 2:
   			 	s = "SYNC_STATE_REJECT_ON_WIFI";
   			 	break;
    		case 3:
    			s = "SYNC_STATE_REJECT_ON_ROAMING";
    			break;
    		case 4:
    			s = "SYNC_STATE_REJECT_ON_POWER";
    			break;
    		case 5:
    			s = "SYNC_STATE_REJECT_ON_USER_AUTH";
    			break;
    		case 6:
    			s = "SYNC_STATE_REJECT_ON_AUTO_SYNC";
    			break;
    		case 7:
    			 s = "SYNC_STATE_REJECT_ON_DISABLED_DOWNSYNC";
    			break;
    		case 8:
    			s = "SYNC_STATE_REJECT_ON_BACKGROUND_DATA";
    			break;
    		case 9:
    			s = "SYNC_STATE_STOP_ON_QUOTA_REACHED";
    			break;
    		case 10:
    			s = "SYNC_STATE_STOP_ON_USER_AUTH";
    			break;
    		case 11:
    			s = "SYNC_STATE_WAIT_ON_SDCARD";
    			break;
    		case 12 :
    			s = "SYNC_STATE_STOP_ON_SDCARD";
    			break;
    		case 13:
    			s = "SYNC_STATE_YIELD";
    			break;
    		case 14:
    			s = "SYNC_STATE_STOP_ON_NETWORK";
    			break;
    		case 15:
    			s = "SYNC_STATE_STOP_ON_IOE";
    			break;
    	}
        return s;
    }
}
