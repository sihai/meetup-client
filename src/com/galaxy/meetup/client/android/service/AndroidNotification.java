/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import WriteReviewOperation.MediaRef;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbDataAction;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsNotificationData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.content.PhotoTaggeeData;
import com.galaxy.meetup.server.client.domain.DataAction;
import com.galaxy.meetup.server.client.domain.DataActor;
import com.galaxy.meetup.server.client.domain.DataItem;
import com.galaxy.meetup.server.client.domain.DataPhoto;
import com.galaxy.meetup.server.client.domain.EntityPhotosData;
import com.galaxy.meetup.server.client.domain.EntitySquaresData;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class AndroidNotification {

	private static final int COMMENT_NOTIFICATION_TYPES[] = {
        2, 3, 14, 25, 26
    };
    private static final int NOTIFICATION_IDS[] = {
        1, 2, 3, 4, 5
    };
    
    public static synchronized void cancel(Context context, EsAccount esaccount, int i) {
        String s = buildNotificationTag(context, esaccount);
        ((NotificationManager)context.getSystemService("notification")).cancel(s, i);
    }
    
	public static synchronized void cancelAll(Context context, EsAccount esaccount) {
		NotificationManager notificationmanager;
		String s = buildNotificationTag(context, esaccount);
		notificationmanager = (NotificationManager) context.getSystemService("notification");
		for (int id : NOTIFICATION_IDS) {
			notificationmanager.cancel(s, id);
		}
	}
	
	public static void cancelFirstTimeFullSizeNotification(Context context, EsAccount esaccount) {
        ((NotificationManager)context.getSystemService("notification")).cancel(buildNotificationTag(context, esaccount), 5);
    }

    public static void cancelQuotaNotification(Context context, EsAccount esaccount) {
        ((NotificationManager)context.getSystemService("notification")).cancel(buildNotificationTag(context, esaccount), 4);
    }
    
    
    private static int countActorsForComment(Map map) {
        Set hashset = new HashSet();
        int ai[] = COMMENT_NOTIFICATION_TYPES;
        int i = ai.length;
        for(int j = 0; j < i; j++)
        {
            List list = (List)map.get(Integer.valueOf(ai[j]));
            if(list == null)
                continue;
            for(Iterator iterator = list.iterator(); iterator.hasNext(); hashset.add((String)iterator.next()));
        }

        return hashset.size();
    }

    private static AudienceData createAudienceData(List list) {
    	AudienceData audiencedata;
        if(list == null || list.isEmpty()) {
            audiencedata = null;
        } else {
            ArrayList arraylist = new ArrayList();
            PhotoTaggeeData.PhotoTaggee phototaggee;
            for(Iterator iterator = list.iterator(); iterator.hasNext(); arraylist.add(new PersonData(phototaggee.getId(), phototaggee.getName(), null)))
                phototaggee = (PhotoTaggeeData.PhotoTaggee)iterator.next();

            audiencedata = new AudienceData(arraylist, null);
        }
        return audiencedata;
    }

    private static AudienceData createAudienceDataForYourCircles(Context context, EsAccount esaccount) {
        return new AudienceData(EsPeopleData.getCircleData(context, esaccount, 5));
    }

    private static Notification createDigestNotification(Context context, EsAccount esaccount, Cursor cursor) {
        Notification notification;
        if(hasOnlyHangoutNotifications(cursor))
            notification = null;
        else
        if(!cursor.moveToFirst())
        {
            notification = null;
        } else
        {
            long l = 0x7fffffffffffffffL;
            int i = 0;
            android.app.Notification.InboxStyle inboxstyle = new android.app.Notification.InboxStyle();
            String s = null;
            do
                if(cursor.getInt(3) != 8)
                {
                    String s2 = cursor.getString(4);
                    inboxstyle.addLine(s2);
                    if(s == null)
                        s = s2;
                    l = Math.min(l, cursor.getLong(5) / 1000L);
                    i++;
                }
            while(cursor.moveToNext());
            int j = i;
            Resources resources = context.getResources();
            String s1 = resources.getQuantityString(R.plurals.notifications_ticker_text, j);
            Intent intent = Intents.getNotificationsIntent(context, esaccount, cursor);
            intent.addFlags(0x14000000);
            intent.putExtra("com.google.plus.analytics.intent.extra.FROM_NOTIFICATION", true);
            android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
            PendingIntent pendingintent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, 0);
            inboxstyle.setBigContentTitle(s1);
            builder.setTicker(s1).setContentTitle(s1).setWhen(l).setPriority(0).setNumber(j).setSmallIcon(R.drawable.ic_stat_gplus).setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.stat_notify_multiple_gplus)).setContentIntent(pendingintent).setDeleteIntent(EsService.getDeleteNotificationsIntent(context, esaccount, 1)).setStyle(inboxstyle);
            if(!TextUtils.isEmpty(s))
                builder.setContentText(s);
            if(hasRingtone(context))
                builder.setSound(getRingtone(context));
            else
                builder.setDefaults(1);
            notification = builder.build();
        }
        return notification;
    }
    
    private static String getActorNamesForDisplay(List list)
    {
        String s;
        if(list == null)
        {
            s = "";
        } else
        {
            StringBuilder stringbuilder = new StringBuilder();
            Iterator iterator = list.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                DataActor dataactor = (DataActor)iterator.next();
                if(dataactor != null && !TextUtils.isEmpty(dataactor.name))
                {
                    if(stringbuilder.length() > 0)
                        stringbuilder.append(", ");
                    stringbuilder.append(dataactor.name);
                }
            } while(true);
            s = stringbuilder.toString();
        }
        return s;
    }
    
    private static Map getActorsMap(List list)
    {
        HashMap hashmap = new HashMap();
        if(list != null)
        {
            for(Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                DataAction dataaction = (DataAction)iterator.next();
                if(dataaction != null)
                {
                    Iterator iterator1 = dataaction.item.iterator();
                    while(iterator1.hasNext()) 
                    {
                        DataItem dataitem = (DataItem)iterator1.next();
                        int i = EsNotificationData.getNotificationType(dataitem.notificationType);
                        if(dataitem.actor != null)
                        {
                            Object obj = (List)hashmap.get(Integer.valueOf(i));
                            if(obj == null)
                            {
                                obj = new ArrayList();
                                hashmap.put(Integer.valueOf(i), obj);
                            }
                            ((List) (obj)).add(dataitem.actor.name);
                        }
                    }
                }
            }

        }
        return hashmap;
    }

    private static String getNamesForDisplay(List list)
    {
        String s;
        if(list == null)
        {
            s = "";
        } else
        {
            StringBuilder stringbuilder = new StringBuilder();
            Iterator iterator = list.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                String s1 = (String)iterator.next();
                if(!TextUtils.isEmpty(s1))
                {
                    if(stringbuilder.length() > 0)
                        stringbuilder.append(", ");
                    stringbuilder.append(s1);
                }
            } while(true);
            s = stringbuilder.toString();
        }
        return s;
    }
    
    private static List getNamesForDisplay(Context context, Map map) {
    	List list = new ArrayList();
    	Set set = map.keySet();
    	Iterator iterator = set.iterator();
    	if(1 == set.size()) {
    		list.add(getNamesForDisplay((List)map.get(Integer.valueOf(((Integer)iterator.next()).intValue()))));
    	} else {
    		int i = 0;
    		while(iterator.hasNext()) {
    			i = (Integer)iterator.next();
    			switch(i) {
    			case 2:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_comment)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 3:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_comment)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 4:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 5:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_reshare)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 6:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 7:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 8:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 9:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 10:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 11:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 12:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 13:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 14:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_comment)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 15:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_mention)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 16:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_mention)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 17:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 18:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 19:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 20:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_plus_one)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 21:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_plus_one)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 22:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 23:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 24:
    				break;
    			case 25:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_comment)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			case 26:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_comment)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			default:
    				list.add((new StringBuilder()).append(context.getString(R.string.notifications_single_post_action_post)).append(" ").append(getNamesForDisplay((List)map.get(Integer.valueOf(i)))).toString());
    				break;
    			}
    		}
    	}
    	return list;
    }

    private static Intent getPhotosSelectionActivityIntent(Context context, EsAccount esaccount, String s, ArrayList arraylist, Map map, AudienceData audiencedata)
    {
        Intent intent;
        if(arraylist == null || map == null)
        {
            intent = null;
        } else
        {
            MediaRef amediaref[] = new MediaRef[arraylist.size()];
            arraylist.toArray(amediaref);
            intent = Intents.newPhotosSelectionActivityIntentBuilder(context).setAccount(esaccount).setGaiaId(esaccount.getGaiaId()).setMediaRefs(amediaref).setMediaRefUserMap(map).setAudience(audiencedata).setNotificationId(s).build();
        }
        return intent;
    }

    public static Uri getRingtone(Context context)
    {
        Resources resources = context.getResources();
        String s = resources.getString(R.string.notifications_preference_ringtone_key);
        String s1 = resources.getString(R.string.notifications_preference_ringtone_default_value);
        return Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString(s, s1));
    }

    private static boolean hasOnlyHangoutNotifications(Cursor cursor)
    {
        boolean flag = true;
        do
        {
            if(!cursor.moveToNext())
                break;
            if(cursor.getInt(3) == 8)
                continue;
            flag = false;
            break;
        } while(true);
        return flag;
    }

    public static boolean hasRingtone(Context context)
    {
        Resources resources = context.getResources();
        String s = resources.getString(R.string.notifications_preference_ringtone_key);
        String s1 = resources.getString(R.string.notifications_preference_ringtone_default_value);
        boolean flag;
        if(!PreferenceManager.getDefaultSharedPreferences(context).getString(s, s1).equals(s1))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private static boolean isRunningJellybeanOrLater()
    {
        boolean flag;
        if(android.os.Build.VERSION.SDK_INT >= 16)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static Intent newViewNotificationIntent(Context context, EsAccount esaccount, Cursor cursor)
    {
        int i = cursor.getInt(3);
        if(65535 == i) {
        	return null;
        }
        
        Intent intent = null;
        int j;
        String s;
        String s1;
        String s2;
        String s3;
        j = cursor.getInt(15);
        s = cursor.getString(4);
        s1 = context.getString(R.string.notification_photo_deleted);
        s2 = context.getString(R.string.notification_event_deleted);
        s3 = context.getString(R.string.notification_post_deleted);
        
        boolean flag1;
        String s9;
        byte abyte0[];
        EntitySquaresData entitysquaresdata;
        int k;
        int l;
        boolean flag2;
        String s10;
        String s11;
        String s12;
        long l1;
        MediaRef mediaref;
        Intents.PhotoViewIntentBuilder photoviewintentbuilder;
        String s13;
        byte abyte1[];
        EntityPhotosData entityphotosdata;
        String s14;
        List list;
        Object obj;
        Map map;
        AudienceData audiencedata;
        List list1;
        byte abyte2[];
        List list2;
        String s15;
        ArrayList arraylist2;
        Iterator iterator;
        byte abyte3[];
        int i1;
        Intent intent1;
        DataActor dataactor;
        String s16;
        
        if(!TextUtils.isEmpty(s) && !TextUtils.equals(s, s1) && !TextUtils.equals(s, s2) && !TextUtils.equals(s, s3)) { 
        	String s4;
            s4 = cursor.getString(1);
            String s5 = cursor.getString(13);
            String s6 = cursor.getString(14);
            boolean flag;
            ArrayList arraylist;
            String s7;
            ArrayList arraylist1;
            String s8;
            if(cursor.getInt(12) == 1)
                flag = true;
            else
                flag = false;
            intent = null;
            if(flag)
                intent = Intents.getHostedEventIntent(context, esaccount, s5, j, s6, null, s4, null);
            if(null == intent) {
            	switch(i) {
            	case 1:
            	case 8:
            		String tmp = cursor.getString(10);
                    if(tmp != null)
                        intent = Intents.getPostCommentsActivityIntent(context, esaccount, tmp, s4, i, true, false);
            		break;
            	case 2:
            		abyte2 = cursor.getBlob(6);
                    if(null != abyte2) {
                    	list2 = DbDataAction.getDataActorList(DbDataAction.deserializeDataActionList(abyte2));
                        s15 = esaccount.getGaiaId();
                        arraylist2 = new ArrayList(list2.size());
                        iterator = list2.iterator();
                        do
                        {
                            if(!iterator.hasNext())
                                break;
                            dataactor = (DataActor)iterator.next();
                            if(!s15.equals(dataactor.obfuscatedGaiaId))
                                arraylist2.add(dataactor);
                        } while(true);
                        try {
	                        abyte3 = DbDataAction.serializeDataActorList(arraylist2);
	                        if(!arraylist2.isEmpty()) {
	                        	i1 = arraylist2.size();
	                            if(i1 != 1) {
	                            	if(i1 > 1) {
	                            		intent = Intents.getAddedToCircleActivityIntent(context, esaccount, abyte3, s4);
	                            	}
	                            } else { 
	                            	intent = Intents.getProfileActivityByGaiaIdIntent(context, esaccount, ((DataActor)arraylist2.get(0)).obfuscatedGaiaId, s4);
	                            }
	                        }
                        } catch (IOException e) {
                        	// TODO log
                        }
                    }
            		break;
            	case 3:
            		if(j == 18)
                    {
                        s13 = cursor.getString(7);
                        abyte1 = cursor.getBlob(18);
                        entityphotosdata = (EntityPhotosData)JsonUtil.fromByteArray(abyte1, EntityPhotosData.class);
                        s14 = cursor.getString(23);
                        list = DbDataAction.deserializeDataActorList(cursor.getBlob(24));
                        if(entityphotosdata != null && entityphotosdata.photo != null)
                        {
                            list1 = entityphotosdata.photo;
                            if(list1 == null)
                                obj = null;
                            else
                                obj = createMediaRefList(s13, list1);
                        } else
                        {
                            obj = null;
                        }
                        map = PhotoTaggeeData.createMediaRefUserMap(((List) (obj)), list, s14);
                        audiencedata = createAudienceDataForYourCircles(context, esaccount);
                        if(obj != null)
                            if(((ArrayList) (obj)).size() == 1)
                            {
                                createAudienceData((List)map.get(((ArrayList) (obj)).get(0)));
                                intent = Intents.getPostActivityIntent(context, esaccount, ((ArrayList) (obj)), audiencedata);
                            } else
                            {
                                intent = getPhotosSelectionActivityIntent(context, esaccount, s4, ((ArrayList) (obj)), map, audiencedata);
                            }
                    } else
                    {
                        s11 = cursor.getString(8);
                        s12 = cursor.getString(7);
                        l1 = cursor.getLong(9);
                        if(l1 != 0L)
                        {
                            mediaref = new MediaRef(s12, l1, null, null, MediaRef.MediaType.IMAGE);
                            photoviewintentbuilder = Intents.newPhotoViewActivityIntentBuilder(context);
                            photoviewintentbuilder.setAccount(esaccount).setGaiaId(s12).setAlbumId(s11).setPhotoRef(mediaref).setNotificationId(s4).setForceLoadId(Long.valueOf(l1));
                            if(!TextUtils.isEmpty(cursor.getString(20)))
                                photoviewintentbuilder.setDisableComments(Boolean.valueOf(true));
                            intent = photoviewintentbuilder.build();
                        }
                    }
            		break;
            	case 4:
            		break;
            	case 5:
            		break;
            	case 6:
            		break;
            	case 7:
            		break;
            	case 9:
            		break;
            	case 10:
            		break;
            	case 11:
            		if(j == 49)
                    {
                        abyte0 = cursor.getBlob(19);
                        entitysquaresdata = (EntitySquaresData)JsonUtil.fromByteArray(abyte0, EntitySquaresData.class);
                        k = EsNotificationData.getNumSquarePosts(entitysquaresdata);
                        l = EsNotificationData.getUnreadSquarePosts(entitysquaresdata);
                        if(k == 1 || l == 1)
                        {
                            if(l == 1)
                                flag2 = true;
                            else
                                flag2 = false;
                            s10 = EsNotificationData.getSquarePostActivityId(entitysquaresdata, flag2);
                            if(s10 != null)
                                intent = Intents.getPostCommentsActivityIntent(context, esaccount, s10, s4, i, true, false);
                        }
                    }
                    if(intent == null)
                    {
                        s9 = cursor.getString(20);
                        if(s9 != null)
                            intent = Intents.getSquareStreamActivityIntent(context, esaccount, s9, null, s4);
                    }
            		break;
            	default:
            		break;
            	}
            }
            
            if(intent != null)
            {
                intent.putExtra("notif_id", s4);
                if(cursor.getInt(11) != 0)
                    flag1 = true;
                else
                    flag1 = false;
                intent.putExtra("com.google.plus.analytics.intent.extra.NOTIFICATION_READ", flag1);
                arraylist = new ArrayList(1);
                arraylist.add(Integer.valueOf(j));
                intent.putExtra("notif_types", arraylist);
                s7 = cursor.getString(2);
                arraylist1 = new ArrayList(1);
                arraylist1.add(s7);
                intent.putExtra("coalescing_codes", arraylist1);
                s8 = context.getPackageName();
                intent.setPackage(s8);
                intent.addFlags(0x14000000);
            }
            
            return intent;
        } else {
        	return null;
        }
    }

    private static PendingIntent newViewOneIntent(Context context, EsAccount esaccount, Cursor cursor) {
        Intent intent = newViewNotificationIntent(context, esaccount, cursor);
        PendingIntent pendingintent;
        if(intent != null && intent.resolveActivity(context.getPackageManager()) != null) {
            intent.putExtra("com.google.plus.analytics.intent.extra.FROM_NOTIFICATION", true);
            pendingintent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, 0);
        } else {
            Intent intent1 = Intents.getNotificationsIntent(context, esaccount, cursor);
            intent1.addFlags(0x14000000);
            intent1.putExtra("com.google.plus.analytics.intent.extra.FROM_NOTIFICATION", true);
            pendingintent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent1, 0);
        }
        return pendingintent;
    }

    public static boolean shouldNotify(Context context) {
        Resources resources = context.getResources();
        String s = resources.getString(R.string.notifications_preference_enabled_key);
        boolean flag = resources.getBoolean(R.bool.notifications_preference_enabled_default_value);
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(s, flag);
    }

    public static boolean shouldVibrate(Context context) {
        Resources resources = context.getResources();
        String s = resources.getString(R.string.notifications_preference_vibrate_key);
        boolean flag = resources.getBoolean(R.bool.notifications_preference_vibrate_default_value);
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(s, flag);
    }

    public static void showCircleAddFailedNotification(Context context, EsAccount esaccount, String s, String s1) {
        Intent intent = Intents.getProfileActivityIntent(context, esaccount, s, null);
        intent.setPackage(context.getPackageName());
        intent.addFlags(0x14000000);
        PendingIntent pendingintent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, 0);
        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context);
        builder.setSmallIcon(0x1080027);
        builder.setAutoCancel(true);
        builder.setContentTitle(context.getString(R.string.cannot_add_to_circle_error_title));
        builder.setContentText(context.getString(R.string.cannot_add_to_circle_error_message, new Object[] {
            s1
        }));
        builder.setContentIntent(pendingintent);
        ((NotificationManager)context.getSystemService("notification")).notify((new StringBuilder()).append(context.getPackageName()).append(":notifications:add:").append(s).toString(), 3, builder.getNotification());
    }

    public static void showFullSizeFirstTimeNotification(Context context, EsAccount esaccount) {
        Intent intent = Intents.getInstantUploadSettingsActivityIntent(context, esaccount);
        intent.setPackage(context.getPackageName());
        intent.addFlags(0x14000000);
        PendingIntent pendingintent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, 0);
        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.stat_notify_gplus);
        builder.setAutoCancel(true);
        builder.setContentTitle(context.getString(R.string.full_size_first_time_notification_title));
        builder.setTicker(context.getString(R.string.full_size_first_time_notification_text));
        builder.setContentText(context.getString(R.string.full_size_first_time_notification_text));
        builder.setContentIntent(pendingintent);
        ((NotificationManager)context.getSystemService("notification")).notify(buildNotificationTag(context, esaccount), 5, builder.getNotification());
    }

    public static void showQuotaNotification(Context context, EsAccount esaccount, int i, int j, boolean flag) {
        String s = InstantUpload.getSizeText(context, Math.max(j - i, 0));
        int k;
        int l;
        Intent intent;
        PendingIntent pendingintent;
        android.support.v4.app.NotificationCompat.Builder builder;
        if(flag)
            k = R.string.full_size_no_quota_text;
        else
            k = R.string.full_size_low_quota_text;
        if(flag)
            l = R.drawable.stat_notify_quota_exceed;
        else
            l = R.drawable.stat_notify_quota_warning;
        intent = Intents.getInstantUploadSettingsActivityIntent(context, esaccount);
        intent.setPackage(context.getPackageName());
        intent.addFlags(0x14000000);
        pendingintent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, 0);
        builder = new android.support.v4.app.NotificationCompat.Builder(context);
        builder.setSmallIcon(l);
        builder.setAutoCancel(true);
        builder.setContentTitle(context.getString(R.string.instant_upload_notification_title));
        builder.setTicker(context.getString(k, new Object[] {
            s
        }));
        builder.setContentText(context.getString(k, new Object[] {
            s
        }));
        builder.setContentIntent(pendingintent);
        ((NotificationManager)context.getSystemService("notification")).notify(buildNotificationTag(context, esaccount), 4, builder.getNotification());
    }

    public static void showUpgradeRequiredNotification(Context context) {
        NotificationManager notificationmanager = (NotificationManager)context.getSystemService("notification");
        long l = System.currentTimeMillis();
        String s = context.getString(R.string.signup_required_update_available);
        Notification notification = new Notification(R.drawable.ic_stat_gplus, s, l);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(0x80000);
        intent.setData(Uri.parse("market://details?id=com.google.android.apps.plus"));
        intent.addFlags(0x14000000);
        PendingIntent pendingintent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, 0);
        notification.setLatestEventInfo(context, context.getString(R.string.app_name), s, pendingintent);
        notification.flags = 0x10 | notification.flags;
        notification.defaults = 4 | notification.defaults;
        notificationmanager.notify((new StringBuilder()).append(context.getPackageName()).append(":notifications:upgrade").toString(), 2, notification);
    }

    public static synchronized void update(Context context, EsAccount esaccount) {
    	NotificationManager notificationmanager = (NotificationManager)context.getSystemService("notification");
        String s = buildNotificationTag(context, esaccount);
        boolean flag = shouldNotify(context);
        
        if(!flag) {
        	return;
        }
        Notification notification = createNotification(context, esaccount);
        if(notification == null)
            return;
        notification.flags = 0x10 | notification.flags;
        notification.flags = 1 | notification.flags;
        notification.flags = 8 | notification.flags;
        notification.ledARGB = -1;
        notification.ledOnMS = 500;
        notification.ledOffMS = 2000;
        if(shouldVibrate(context))
            notification.defaults = 2 | notification.defaults;
        notificationmanager.notify(s, 1, notification);
    }
    
    private static String buildNotificationTag(Context context, EsAccount esaccount) {
        return (new StringBuilder()).append(context.getPackageName()).append(":notifications:").append(esaccount.getName()).toString();
    }
    
    private static ArrayList createMediaRefList(String s, List list)
    {
    	DataPhoto dataphoto;
        ArrayList arraylist = new ArrayList();
        for(Iterator iterator = list.iterator(); iterator.hasNext();) {
        	dataphoto = (DataPhoto)iterator.next();
        	if(dataphoto == null || dataphoto.original == null || TextUtils.isEmpty(dataphoto.id) || TextUtils.isEmpty(dataphoto.original.url)) {
        		continue;
        	}
        	
        	long l = 0L;
        	MediaRef.MediaType mediatype = MediaRef.MediaType.VIDEO;
        	try
            {
        		l = Long.valueOf(dataphoto.id).longValue();
            } catch(NumberFormatException numberformatexception) {
                Log.e("AndroidNotification", (new StringBuilder("Cannot convert ")).append(dataphoto.id).append(" into Long.").toString());
                continue;
            } 
            if(dataphoto.video == null) {
                if(dataphoto.isPanorama != null && dataphoto.isPanorama.booleanValue())
                    mediatype = MediaRef.MediaType.PANORAMA;
                else
                    mediatype = MediaRef.MediaType.IMAGE;
            }
            
            arraylist.add(new MediaRef(s, l, dataphoto.original.url, null, mediatype));
        }
        return arraylist;
    }
    
    private static Notification createNotification(Context context, EsAccount esaccount)
    {
    	// TODO
    	return null;
    }
}
