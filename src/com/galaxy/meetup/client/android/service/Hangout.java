/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.os.Build;
import android.view.TextureView;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.DbEmbedHangout;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.hangout.GCommApp;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class Hangout {

	static final boolean assertionsDisabled;
    public static final String CONSUMER_HANGOUT_DOMAIN = null;
    private static final Pattern HANGOUT_URL_PATTERN = Pattern.compile("http s? ://plus.google.com/hangouts/(    \\p{Alnum}+)", 6);
    private static EsAccount sAccountForCachedStatus;
    private static boolean sCachedIsCreationSupported;
    private static SupportStatus sCachedStatus;
    private static boolean sHangoutCreationSupportCacheIsDirty = true;
    private static boolean sHangoutSupportStatusCacheIsDirty = true;

    static {
        boolean flag;
        if(!Hangout.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        assertionsDisabled = flag;
    }
    
    public Hangout() {
    }

    public static void enterGreenRoom(EsAccount esaccount, Context context, String s, String s1, DbEmbedHangout dbembedhangout) {
        if(!assertionsDisabled && !dbembedhangout.isInProgress())
            throw new AssertionError();
        List arraylist = new ArrayList();
        arraylist.add(Data.Participant.newBuilder().setParticipantId((new StringBuilder("g:")).append(s).toString()).setFullName(s1).setFirstName(getFirstNameFromFullName(s1)).build());
        List arraylist1 = dbembedhangout.getAttendeeGaiaIds();
        List arraylist2 = dbembedhangout.getAttendeeNames();
        int i = 0;
        for(int j = dbembedhangout.getNumAttendees(); i < j; i++)
        {
            String s2 = (String)arraylist2.get(i);
            arraylist.add(Data.Participant.newBuilder().setParticipantId((new StringBuilder("g:")).append((String)arraylist1.get(i)).toString()).setFullName(s2).setFirstName(getFirstNameFromFullName(s2)).build());
        }

        Intent intent = Intents.getHangoutActivityIntent(context, esaccount, RoomType.CONSUMER, null, null, dbembedhangout.getHangoutId(), null, LaunchSource.Stream, false, false, arraylist);
        GCommApp gcommapp = GCommApp.getInstance(context);
        if(gcommapp.isInAHangout())
        {
            Intent intent1 = gcommapp.getGCommService().getNotificationIntent();
            if(intent1 != null)
            {
                Info info = (Info)intent1.getSerializableExtra("hangout_info");
                if(info != null && info.id.equals(dbembedhangout.getHangoutId()))
                    intent = intent1;
            }
        }
        context.startActivity(intent);
    }
    
    public static String getFirstNameFromFullName(String s) {
        int i = s.indexOf(' ');
        if(i != -1)
            s = s.substring(0, i);
        return s;
    }

    public static SupportStatus getSupportedStatus(Context context, EsAccount esaccount) {
        updateCacheDirtyFlags(esaccount);
        if(sHangoutSupportStatusCacheIsDirty) {
            SupportStatus supportstatus;
            if(android.os.Build.VERSION.SDK_INT < 8) {
                supportstatus = SupportStatus.OS_NOT_SUPPORTED;
            } else {
                FeatureInfo afeatureinfo[] = context.getPackageManager().getSystemAvailableFeatures();
                boolean flag;
                if(afeatureinfo != null && afeatureinfo.length > 0) {
                    int i = afeatureinfo.length;
                    int j = 0;
                    flag = false;
                    while(j < i)  {
                        FeatureInfo featureinfo = afeatureinfo[j];
                        if(featureinfo.name == null)
                            if((short)(featureinfo.reqGlEsVersion >> 16) >= 2)
                                flag = true;
                            else
                                flag = false;
                        j++;
                    }
                } else {
                    flag = false;
                }
                if(!flag)
                    supportstatus = SupportStatus.DEVICE_NOT_SUPPORTED;
                else
                if(!Build.CPU_ABI.equals("armeabi-v7a") && !Build.CPU_ABI2.equals("armeabi-v7a"))
                    supportstatus = SupportStatus.DEVICE_NOT_SUPPORTED;
                else
                if(esaccount == null || esaccount.getName() == null || esaccount.isPlusPage())
                    supportstatus = SupportStatus.ACCOUNT_NOT_CONFIGURED;
                else
                    supportstatus = SupportStatus.SUPPORTED;
            }
            sCachedStatus = supportstatus;
            sHangoutSupportStatusCacheIsDirty = false;
        }
        return sCachedStatus;
    }
    
    public static boolean isAdvancedUiSupported(Context context)
    {
        boolean flag = true;
        if(android.os.Build.VERSION.SDK_INT < 14 || !Property.ENABLE_ADVANCED_HANGOUTS.getBoolean() || (new TextureView(context)).getLayerType() != 2) 
        	return false; 
        else {
        	 boolean flag1;
             if(Build.MANUFACTURER.equals("samsung") && !Build.BRAND.equals("google") && android.os.Build.VERSION.SDK_INT <= 15)
                 flag1 = flag;
             else
                 flag1 = false;
             if(flag1) {
            	 return false;
             }
             return true;
        }
    }
    
    public static boolean isHangoutCreationSupported(Context context, EsAccount esaccount)
    {
        boolean flag = false;
        updateCacheDirtyFlags(esaccount);
        if(!sHangoutCreationSupportCacheIsDirty) 
        	return sCachedIsCreationSupported; 
        else {
            if(getSupportedStatus(context, esaccount) == SupportStatus.SUPPORTED) {
            	FeatureInfo afeatureinfo[] = context.getPackageManager().getSystemAvailableFeatures();
	            if(null != afeatureinfo) {
	            	for(FeatureInfo featureinfo : afeatureinfo) {
	            		if("android.hardware.camera.front".equals(featureinfo.name) || "android.hardware.camera".equals(featureinfo.name)) {
	            			flag = true;
	            		}
	            	}
	            }
            }
            
            sCachedIsCreationSupported = flag;
	        sHangoutCreationSupportCacheIsDirty = false;
	        return sCachedIsCreationSupported;
        }
    }

    private static void updateCacheDirtyFlags(EsAccount esaccount)
    {
        if(sAccountForCachedStatus == null || !sAccountForCachedStatus.equals(esaccount))
        {
            sHangoutCreationSupportCacheIsDirty = true;
            sHangoutSupportStatusCacheIsDirty = true;
            sAccountForCachedStatus = esaccount;
        }
    }
    
	public static interface ApplicationEventListener {
	}
	
	public static enum LaunchSource {
		None,
        Stream,
        Url,
        MissedCall,
        Ring,
        Ding,
        Creation,
        Messenger,
        Transfer,
        Event;
	}
	
	public static enum RoomType {
		CONSUMER,
        WITH_EXTRAS,
        EXTERNAL,
        UNKNOWN;
	}
	
	public static enum SupportStatus {
		OS_NOT_SUPPORTED,
		DEVICE_NOT_SUPPORTED,
		CHILD_NOT_SUPPORTED,
		ACCOUNT_NOT_CONFIGURED,
		TYPE_NOT_SUPPORTED,
		SUPPORTED;
		
		public String getErrorMessage(Context context) {
			String errorMsg = null;
			switch(this) {
			case OS_NOT_SUPPORTED:
				errorMsg = context.getResources().getString(R.string.hangout_not_supported_os);;
				break;
			case DEVICE_NOT_SUPPORTED:
				errorMsg = context.getResources().getString(R.string.hangout_not_supported_device);;
				break;
			case CHILD_NOT_SUPPORTED:
				errorMsg = context.getResources().getString(R.string.hangout_not_supported_child);;
				break;
			case ACCOUNT_NOT_CONFIGURED:
				errorMsg = context.getResources().getString(R.string.hangout_not_supported_account);;
				break;
			case TYPE_NOT_SUPPORTED:
				errorMsg = context.getResources().getString(R.string.hangout_not_supported_type);;
				break;
			}
			return errorMsg;
		}
	}
	
	public static class Info implements Serializable {

		private final String domain;
	    private final String id;
	    private final LaunchSource launchSource;
	    private final String nick;
	    private boolean ringInvitees;
	    private final RoomType roomType;
	    private final String serviceId;


	    public Info(RoomType roomtype, String s, String s1, String s2, String s3, LaunchSource launchsource, boolean flag)
	    {
	        ringInvitees = false;
	        roomType = roomtype;
	        domain = s;
	        serviceId = s1;
	        id = s2.toLowerCase();
	        nick = s3;
	        launchSource = launchsource;
	        ringInvitees = flag;
	    }
	    
    public boolean equals(Object obj) {
    	if(this == obj) {
    		return true;
    	}
    	if(!(obj instanceof Info)) {
    		return false;
    	}
    	
    	Info info = (Info)obj;
        boolean flag1;
        boolean flag2;
        if(domain == null || domain.equals(""))
            flag1 = true;
        else
            flag1 = false;
        if(info.domain == null || info.domain.equals(""))
            flag2 = true;
        else
            flag2 = false;
        if(roomType != info.roomType || (!flag1 || !flag2) && (domain == null || !domain.equals(info.domain)) || !id.equals(info.id))
            return false;
    	
    	return true;
    }

    public final String getDomain()
    {
        return domain;
    }

    public final String getId()
    {
        return id;
    }

    public final LaunchSource getLaunchSource()
    {
        return launchSource;
    }

    public final String getNick()
    {
        return nick;
    }

    public final boolean getRingInvitees()
    {
        return ringInvitees;
    }

    public final RoomType getRoomType()
    {
        return roomType;
    }

    public final String getServiceId()
    {
        return serviceId;
    }

    public int hashCode()
    {
        int i = roomType.hashCode() ^ id.hashCode();
        if(domain != null && !domain.equals(""))
            i ^= domain.hashCode();
        return i;
    }

    public String toString()
    {
        String s;
        if(serviceId == null)
        {
            Object aobj1[] = new Object[6];
            aobj1[0] = id;
            aobj1[1] = domain;
            aobj1[2] = roomType;
            aobj1[3] = nick;
            aobj1[4] = launchSource;
            aobj1[5] = Boolean.valueOf(ringInvitees);
            s = String.format("%s@%s %s (%s, %s, %s)", aobj1);
        } else
        {
            Object aobj[] = new Object[7];
            aobj[0] = serviceId;
            aobj[1] = id;
            aobj[2] = domain;
            aobj[3] = roomType;
            aobj[4] = nick;
            aobj[5] = launchSource;
            aobj[6] = Boolean.valueOf(ringInvitees);
            s = String.format("%s:%s@%s %s (%s, %s, %s)", aobj);
        }
        return s;
    }

    
	}
}
