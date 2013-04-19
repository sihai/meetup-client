/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import WriteReviewOperation.MediaRef;
import android.text.TextUtils;

import com.galaxy.meetup.server.client.domain.DataActor;

/**
 * 
 * @author sihai
 *
 */
public class PhotoTaggeeData {

	public static List<DataActor> createDataActorList(Map<String, DataActor> map, String s) {
        List<String> list = new ArrayList<String>();
        if(!TextUtils.isEmpty(s)) {
            StringTokenizer stringtokenizer = new StringTokenizer(s, "|");
            do {
                if(!stringtokenizer.hasMoreTokens())
                    break;
                String s1 = stringtokenizer.nextToken();
                if(!TextUtils.isEmpty(s1))
                    list.add(s1);
            } while(true);
        }
        List<DataActor> list1 = new ArrayList<DataActor>();
        if(map != null && list != null) {
            Iterator<String> iterator = list.iterator();
            do {
                if(!iterator.hasNext())
                    break;
                DataActor dataactor = map.get(iterator.next());
                if(dataactor != null)
                	list1.add(dataactor);
            } while(true);
        }
        return list1;
    }

    public static Map createMediaRefUserMap(List list, List list1, String s)
    {
        Map map = new HashMap();
        List list2 = getPhotoIdList(s);
        if(list2 != null && list1 != null && list2.size() == list1.size())
        {
            for(int i = 0; i < list2.size(); i++)
            {
                List list4 = (List)list2.get(i);
                DataActor dataactor1 = (DataActor)list1.get(i);
                Iterator iterator2 = list4.iterator();
                while(iterator2.hasNext()) 
                {
                    String s2 = (String)iterator2.next();
                    Object obj;
                    if(map.containsKey(s2))
                    {
                        obj = (List)map.get(s2);
                    } else
                    {
                        ArrayList arraylist1 = new ArrayList();
                        map.put(s2, arraylist1);
                        obj = arraylist1;
                    }
                    ((List) (obj)).add(dataactor1);
                }
            }

        }
        HashMap hashmap1 = new HashMap();
        if(list != null)
        {
            Iterator iterator = list.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                MediaRef mediaref = (MediaRef)iterator.next();
                if(mediaref != null)
                {
                    String s1 = String.valueOf(mediaref.getPhotoId());
                    if(!TextUtils.isEmpty(s1) && map.containsKey(s1))
                    {
                        ArrayList arraylist = new ArrayList();
                        List list3 = (List)map.get(s1);
                        if(list3 != null)
                        {
                            Iterator iterator1 = list3.iterator();
                            do
                            {
                                if(!iterator1.hasNext())
                                    break;
                                DataActor dataactor = (DataActor)iterator1.next();
                                if(dataactor != null)
                                    arraylist.add(new PhotoTaggee(dataactor.obfuscatedGaiaId, dataactor.name));
                            } while(true);
                        }
                        if(!arraylist.isEmpty())
                            hashmap1.put(mediaref, arraylist);
                    }
                }
            } while(true);
        }
        return hashmap1;
    }

    private static List<String> getPhotoIdList(String s) {
        List<String> arraylist = new ArrayList<String>();
        if(!TextUtils.isEmpty(s)) {
            StringTokenizer stringtokenizer = new StringTokenizer(s, "|");
            do {
                if(!stringtokenizer.hasMoreTokens())
                    break;
                String s1 = stringtokenizer.nextToken();
                if(!TextUtils.isEmpty(s1)) {
                    List<String> arraylist1 = new ArrayList<String>();
                    StringTokenizer stringtokenizer1 = new StringTokenizer(s1, ":");
                    do {
                        if(!stringtokenizer1.hasMoreTokens())
                            break;
                        String s2 = stringtokenizer1.nextToken();
                        if(!TextUtils.isEmpty(s2))
                            arraylist1.add(s2);
                    } while(true);
                    if(!arraylist1.isEmpty())
                        arraylist.addAll(arraylist1);
                }
            } while(true);
        }
        return arraylist;
    }
    
	//===========================================================================
    //						Inner class
    //===========================================================================
	
	public static final class PhotoTaggee implements Serializable {

		private String mId;
	    private String mName;

	    public PhotoTaggee(String id, String name) {
	        mId = id;
	        mName = name;
	    }
	    
		public final String getId() {
			return mId;
		}

		public final String getName() {
			return mName;
		}
	}
}
