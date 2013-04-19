/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.galaxy.meetup.client.android.service.AndroidContactsSync;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.CommonConfig;
import com.galaxy.meetup.server.client.domain.SimpleProfile;
import com.galaxy.meetup.server.client.domain.SocialGraphData;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class PeopleData {

	private static Factory sFactory = new Factory();
    private final Context mContext;
    private final SQLiteDatabase mDb;
    private final ContentResolver mResolver;
 
    PeopleData(Context context, EsAccount esaccount)
    {
        mContext = context;
        mDb = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        mResolver = context.getContentResolver();
    }

    public static Factory getFactory()
    {
        return sFactory;
    }

    public final void setBlockedState(String s, String s1, boolean flag)
    {
        if(EsLog.isLoggable("PeopleData", 3))
            Log.d("PeopleData", (new StringBuilder("setBlockedState - User: ")).append(s).append("; isMuted: ").append(flag).toString());
        
        try {
        	mDb.beginTransaction();
        	ContentValues contentvalues = new ContentValues();
        	contentvalues.put("blocked", Boolean.valueOf(flag));
        	if(flag)
                contentvalues.put("in_my_circles", Integer.valueOf(0));
            if(mDb.update("contacts", contentvalues, "person_id=?", new String[] {
                s
            }) != 0 || !flag) {
            } else { 
            	String s2 = null;
                contentvalues.put("person_id", s);
                if(s == null || !s.startsWith("g:")) {
                	s2 = null;
                } else {
                	s2 = s.substring(2);
                }
                
                contentvalues.put("gaia_id", s2);
                contentvalues.put("name", s1);
                mDb.insert("contacts", null, contentvalues);
            }
            
            if(flag)
            {
                mDb.delete("circle_contact", "link_person_id=?", new String[] {
                    s
                });
                EsPeopleData.updateMemberCounts(mDb);
            }
            mDb.setTransactionSuccessful();
            mResolver.notifyChange(EsProvider.CONTACTS_URI, null);
            if(flag)
            {
                mResolver.notifyChange(EsProvider.CIRCLES_URI, null);
                AndroidContactsSync.requestSync(mContext);
            }
        } finally {
        	mDb.endTransaction();
        }
        
    }

    public final boolean setMuteState(String s, boolean flag) {
    	
        if(EsLog.isLoggable("PeopleData", 3))
            Log.d("PeopleData", (new StringBuilder("setMuteState - User: ")).append(s).append("; isMuted: ").append(flag).toString());
        String s1 = (new StringBuilder("g:")).append(s).toString();
        Cursor cursor = null;
        boolean flag1 = false;
        try {
        	SimpleProfile simpleprofile = null;
        	mDb.beginTransaction();
        	byte abyte0[] = null;
            cursor = mDb.query("profiles", new String[] {
                "profile_proto"
            }, "profile_person_id=?", new String[] {
                s1
            }, null, null, null);
            if(cursor.moveToFirst()) {
            	abyte0 = cursor.getBlob(0);
            }
            if(abyte0 != null) { 
            	simpleprofile = (SimpleProfile)JsonUtil.fromByteArray(abyte0, SimpleProfile.class);
            }
            
            if(simpleprofile == null) {
            	mDb.setTransactionSuccessful();
                if(flag1)
                    mResolver.notifyChange(Uri.withAppendedPath(EsProvider.CONTACT_BY_PERSON_ID_URI, s1), null);
                return flag1; 
            } else { 
            	CommonConfig commonconfig;
                commonconfig = simpleprofile.config;
                flag1 = false;
                if(commonconfig == null) {
                	mDb.setTransactionSuccessful();
                    if(flag1)
                        mResolver.notifyChange(Uri.withAppendedPath(EsProvider.CONTACT_BY_PERSON_ID_URI, s1), null);
                    return flag1; 
                } else {
                	SocialGraphData socialgraphdata;
                    socialgraphdata = simpleprofile.config.socialGraphData;
                    flag1 = false;
                    if(socialgraphdata == null) {
                    	mDb.setTransactionSuccessful();
                        if(flag1)
                            mResolver.notifyChange(Uri.withAppendedPath(EsProvider.CONTACT_BY_PERSON_ID_URI, s1), null);
                        return flag1; 
                    } else {
                    	boolean flag2;
                        flag2 = PrimitiveUtils.safeBoolean(simpleprofile.config.socialGraphData.muted);
                        flag1 = false;
                        if(flag2 == flag) {
                        	mDb.setTransactionSuccessful();
                            if(flag1)
                                mResolver.notifyChange(Uri.withAppendedPath(EsProvider.CONTACT_BY_PERSON_ID_URI, s1), null);
                            return flag1; 
                        } else {
                        	ContentValues contentvalues;
                            simpleprofile.config.socialGraphData.muted = Boolean.valueOf(flag);
                            contentvalues = new ContentValues();
                            byte abyte1[] = JsonUtil.toByteArray(simpleprofile);
                            byte[] abyte2 = abyte1;
                            
                            contentvalues.put("profile_proto", abyte2);
                            mDb.update("profiles", contentvalues, "profile_person_id=?", new String[] {
                                s1
                            });
                            flag1 = true;
                            mDb.setTransactionSuccessful();
                            if(flag1)
                                mResolver.notifyChange(Uri.withAppendedPath(EsProvider.CONTACT_BY_PERSON_ID_URI, s1), null);
                            return flag1;
                        }
                    }
                }
            }
            
        } finally {
        	mDb.endTransaction();
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
    }
    
    
    public static final class Factory
    {

        public static PeopleData getInstance(Context context, EsAccount esaccount)
        {
            return new PeopleData(context, esaccount);
        }

    }
}
