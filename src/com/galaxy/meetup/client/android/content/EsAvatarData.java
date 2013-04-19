/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.cache.EsMediaCache;
import com.galaxy.meetup.client.android.service.ImageDownloader;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class EsAvatarData {

	private static final String AVATAR_URL_PROJECTION[] = {
        "gaia_id", "avatar"
    };
	
    private static int sBackgroundColor;
    private static Bitmap sDefaultAvatarMedium;
    private static Bitmap sDefaultAvatarMediumRound;
    private static Bitmap sDefaultAvatarSmall;
    private static Bitmap sDefaultAvatarSmallRound;
    private static Bitmap sDefaultAvatarTiny;
    private static Bitmap sDefaultAvatarTinyRound;
    private static final Map sFifeAbbrs;
    private static final Map sFifeHosts;
    private static int sMediumAvatarSize;
    public static boolean sRoundAvatarsEnabled = true;
    private static int sSmallAvatarSize;
    private static int sTinyAvatarSize;

    static {
        sFifeHosts = new HashMap();
        sFifeAbbrs = new HashMap();
        sFifeHosts.put("lh3.googleusercontent.com", "~3");
        sFifeAbbrs.put("~3", "lh3.googleusercontent.com");
        sFifeHosts.put("lh4.googleusercontent.com", "~4");
        sFifeAbbrs.put("~4", "lh4.googleusercontent.com");
        sFifeHosts.put("lh5.googleusercontent.com", "~5");
        sFifeAbbrs.put("~5", "lh5.googleusercontent.com");
        sFifeHosts.put("lh6.googleusercontent.com", "~6");
        sFifeAbbrs.put("~6", "lh6.googleusercontent.com");
    }
    
    public static String compressAvatarUrl(String s) {
    	String result = null;
    	int start = 0;
    	if(TextUtils.isEmpty(s)) {
    		return result;
    	}
    	if(s.startsWith("https://")) {
    		start = 8;
    	} else if(s.startsWith("http://")) {
    		start = 7;
    	} else if(s.startsWith("//")) {
    		start = 2;
    	} else {
    		start = 0;
    	}
    	
    	int i = s.length();
        if(s.endsWith("/photo.jpg"))
            i -= 9;
        int j = s.indexOf('/', 8);
        String s1;
        if(j == -1)
            s1 = null;
        else
            s1 = (String)sFifeHosts.get(s.substring(8, j));
        if(s1 != null)
        	result = (new StringBuilder()).append(s1).append(s.substring(j, i)).toString();
        else
        	result = s.substring(8, i);
        return result;
    }
    
    private static int getAvatarBackgroundColor(Context context) {
        if(sBackgroundColor == 0)
            sBackgroundColor = context.getResources().getColor(R.color.avatar_background_color);
        return sBackgroundColor;
    }
    
    public static int getAvatarSizeInPx(Context context, int i) {
    	int size = 0;
    	switch(i) {
	    	case 0:
	    		size = getTinyAvatarSize(context);
	    		break;
	    	case 1:
	    		size = getSmallAvatarSize(context);
	    		break;
	    	case 2:
	    		size = getMediumAvatarSize(context);
	    		break;
	    	default:
	    		break;
    	}
    	return size;
    }
    
    public static int getAvatarUrlSignature(String s) {
    	if(null == s) {
    		return 1;
    	}
    	int hashCode = s.hashCode();
    	if(hashCode == 0 || hashCode == 1)
    		hashCode = 2;
    	return hashCode;
    }
    
    public static int getMediumAvatarSize(Context context) {
        if(sMediumAvatarSize == 0)
            sMediumAvatarSize = context.getApplicationContext().getResources().getDimensionPixelSize(R.dimen.medium_avatar_dimension);
        return sMediumAvatarSize;
    }

    public static Bitmap getMediumDefaultAvatar(Context context) {
        if(sDefaultAvatarMedium == null)
            sDefaultAvatarMedium = ((BitmapDrawable)context.getApplicationContext().getResources().getDrawable(R.drawable.ic_avatar)).getBitmap();
        return sDefaultAvatarMedium;
    }

    public static Bitmap getMediumDefaultAvatar(Context context, boolean flag) {
        Bitmap bitmap;
        if(flag && sRoundAvatarsEnabled) {
            if(sDefaultAvatarMediumRound == null)
                sDefaultAvatarMediumRound = ImageUtils.getRoundedBitmap(context, getMediumDefaultAvatar(context));
            bitmap = sDefaultAvatarMediumRound;
        } else {
            bitmap = getMediumDefaultAvatar(context);
        }
        return bitmap;
    }

    public static int getSmallAvatarSize(Context context) {
        if(sSmallAvatarSize == 0)
            sSmallAvatarSize = context.getApplicationContext().getResources().getDimensionPixelSize(R.dimen.avatar_dimension);
        return sSmallAvatarSize;
    }

    public static Bitmap getSmallDefaultAvatar(Context context) {
        if(sDefaultAvatarSmall == null)
            sDefaultAvatarSmall = ImageUtils.resizeToSquareBitmap(getMediumDefaultAvatar(context), getSmallAvatarSize(context), 0);
        return sDefaultAvatarSmall;
    }

    public static Bitmap getSmallDefaultAvatar(Context context, boolean flag) {
        Bitmap bitmap;
        if(flag && sRoundAvatarsEnabled) {
            if(sDefaultAvatarSmallRound == null)
                sDefaultAvatarSmallRound = ImageUtils.getRoundedBitmap(context, getSmallDefaultAvatar(context));
            bitmap = sDefaultAvatarSmallRound;
        } else {
            bitmap = getSmallDefaultAvatar(context);
        }
        return bitmap;
    }

    public static int getTinyAvatarSize(Context context) {
        if(sTinyAvatarSize == 0)
            sTinyAvatarSize = context.getApplicationContext().getResources().getDimensionPixelSize(R.dimen.tiny_avatar_dimension);
        return sTinyAvatarSize;
    }

    public static Bitmap getTinyDefaultAvatar(Context context) {
        if(sDefaultAvatarTiny == null)
            sDefaultAvatarTiny = ImageUtils.resizeToSquareBitmap(getMediumDefaultAvatar(context), getTinyAvatarSize(context), 0);
        return sDefaultAvatarTiny;
    }

    public static Bitmap getTinyDefaultAvatar(Context context, boolean flag) {
        Bitmap bitmap;
        if(flag && sRoundAvatarsEnabled) {
            if(sDefaultAvatarTinyRound == null)
                sDefaultAvatarTinyRound = ImageUtils.getRoundedBitmap(context, getTinyDefaultAvatar(context));
            bitmap = sDefaultAvatarTinyRound;
        } else {
            bitmap = getTinyDefaultAvatar(context);
        }
        return bitmap;
    }
    
    private static void loadAndroidContactAvatars(Context context, List list, HashMap hashmap)
    {
        // TODO
    }
    
    private static byte[] loadAvatar(Context context, EsAccount esaccount, String s, String s1, int i)
    {
        AvatarImageRequest avatarimagerequest;
        byte abyte0[];
        avatarimagerequest = new AvatarImageRequest(s, s1, i, getAvatarSizeInPx(context, i));
        abyte0 = EsMediaCache.getMedia(context, avatarimagerequest);
        if(null != abyte0) {
        	return abyte0;
        }
        
        switch(i)
        {
        default:
            break;

        case 0: // '\0'
        	abyte0 = loadAvatar(context, esaccount, s, s1, 2);
            if(abyte0 == null)
                abyte0 = loadAvatar(context, esaccount, s, s1, 1);
            if(abyte0 != null)
            {
                abyte0 = ImageUtils.resizeToSquareBitmap(abyte0, getTinyAvatarSize(context));
                if(abyte0 != null)
                    EsMediaCache.insertMedia(context, avatarimagerequest, abyte0);
            }
            break;
        case 1: // '\001'
        	abyte0 = loadAvatar(context, esaccount, s, s1, 2);
            if(abyte0 != null)
            {
                abyte0 = ImageUtils.resizeToSquareBitmap(abyte0, getSmallAvatarSize(context));
                if(abyte0 != null)
                    EsMediaCache.insertMedia(context, avatarimagerequest, abyte0);
            }
            break;
        }
        
        if(abyte0 == null)
            ImageDownloader.downloadImage(context, esaccount, avatarimagerequest);
        return abyte0;
    }

    public static String loadAvatarUrl(Context context, EsAccount esaccount, String s)
    {
    	Cursor cursor = null;
    	try {
	        cursor = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("contacts", AVATAR_URL_PROJECTION, "gaia_id=?", new String[] {
	            s
	        }, null, null, null);
	        if(cursor.moveToNext()) {
	        	 String s2 = cursor.getString(1);
	        	 if(!TextUtils.isEmpty(s2)) {
	        		 return uncompressAvatarUrl(s2);
	        	 }
	        }
	        return null;
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
    }
    
    public static Map loadAvatars(Context context, List list)
    {
        HashMap hashmap;
        EsAccount esaccount;
        hashmap = new HashMap();
        esaccount = EsAccountsData.getActiveAccount(context);
        if(null == esaccount) {
        	return hashmap;
        }
       
        loadGooglePlusAvatars(context, esaccount, list, hashmap);
        return hashmap;
    }
    
    private static void loadGooglePlusAvatars(Context context, EsAccount esaccount, List list, HashMap hashmap)
    {
        HashMap hashmap1;
        ArrayList arraylist = null;
        int i = list.size();
        for(int j = 0; j < i; j++)
        {
            AvatarRequest avatarrequest1 = (AvatarRequest)list.get(j);
            if(avatarrequest1.getAvatarUrl() != null)
                continue;
            if(arraylist == null)
                arraylist = new ArrayList();
            arraylist.add(avatarrequest1);
        }

        hashmap1 = null;
        if(null != arraylist) {
        	hashmap1 = new HashMap();
            SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
            int k = arraylist.size();
            StringBuilder stringbuilder = new StringBuilder();
            String as[] = new String[k];
            stringbuilder.append("gaia_id IN (");
            for(int l = 0; l < k; l++)
            {
                stringbuilder.append("?,");
                as[l] = ((AvatarRequest)arraylist.get(l)).getGaiaId();
            }

            stringbuilder.setLength(-1 + stringbuilder.length());
            stringbuilder.append(')');
            Cursor cursor = null;
            try {
	            cursor = sqlitedatabase.query("contacts", AVATAR_URL_PROJECTION, stringbuilder.toString(), as, null, null, null);
	            while(cursor.moveToNext()) 
	                hashmap1.put(cursor.getString(0), uncompressAvatarUrl(cursor.getString(1)));
            } finally {
            	if(null != cursor) {
            		cursor.close();
            	}
            }
        }
        
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            AvatarRequest avatarrequest = (AvatarRequest)iterator.next();
            String s = avatarrequest.getAvatarUrl();
            if(s == null && hashmap1 != null)
                s = (String)hashmap1.get(avatarrequest.getGaiaId());
            if(s == null)
            {
                hashmap.put(avatarrequest, null);
            } else
            {
                byte abyte0[] = loadAvatar(context, esaccount, avatarrequest.getGaiaId(), s, avatarrequest.getSize());
                if(abyte0 != null)
                {
                    if(avatarrequest.isRounded() && sRoundAvatarsEnabled)
                        abyte0 = ImageUtils.getRoundedBitmap(context, abyte0);
                    hashmap.put(avatarrequest, abyte0);
                }
            }
        } while(true);
    }

    public static String uncompressAvatarUrl(String s) {
        String s1;
        if(TextUtils.isEmpty(s)) {
            s1 = null;
        } else {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("https://");
            if(s.charAt(0) == '~') {
                int i = s.indexOf('/');
                stringbuilder.append((String)sFifeAbbrs.get(s.substring(0, i)));
                stringbuilder.append(s.substring(i));
            } else {
                stringbuilder.append(s);
            }
            if(s.endsWith("/"))
                stringbuilder.append("photo.jpg");
            s1 = stringbuilder.toString();
        }
        return s1;
    }
}
