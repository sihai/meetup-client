/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.galaxy.meetup.server.client.domain.DataAction;
import com.galaxy.meetup.server.client.domain.DataActor;
import com.galaxy.meetup.server.client.domain.DataItem;

/**
 * 
 * @author sihai
 *
 */
public class DbDataAction extends DbSerializer {

	public static List deserializeDataActionList(byte abyte0[]) {
		Object obj;
		if (abyte0 == null) {
			obj = null;
		} else {
			ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
			obj = new ArrayList();
			int i = bytebuffer.getInt();
			int j = 0;
			while (j < i) {
				((List) (obj)).add(getDataAction(bytebuffer));
				j++;
			}
		}
		return ((List) (obj));
	}

	public static List deserializeDataActorList(byte abyte0[]) {
		Object obj;
		if (abyte0 == null) {
			obj = null;
		} else {
			ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
			obj = new ArrayList();
			int i = bytebuffer.getInt();
			int j = 0;
			while (j < i) {
				((List) (obj)).add(getDataActor(bytebuffer));
				j++;
			}
		}
		return ((List) (obj));
	}

    private static DataAction getDataAction(ByteBuffer bytebuffer) {
        DataAction dataaction = new DataAction();
        dataaction.type = getShortString(bytebuffer);
        List<DataItem> arraylist = new ArrayList<DataItem>();
        int i = bytebuffer.getInt();
		for (int j = 0; j < i; j++) {
			DataItem dataitem = new DataItem();
			dataitem.id = getShortString(bytebuffer);
			dataitem.notificationType = getShortString(bytebuffer);
			dataitem.actor = getDataActor(bytebuffer);
			arraylist.add(dataitem);
		}

        dataaction.item = arraylist;
        return dataaction;
    }

	private static DataActor getDataActor(ByteBuffer bytebuffer) {
		DataActor dataactor = new DataActor();
		dataactor.gender = getShortString(bytebuffer);
		dataactor.name = getShortString(bytebuffer);
		dataactor.obfuscatedGaiaId = getShortString(bytebuffer);
		dataactor.photoUrl = getShortString(bytebuffer);
		dataactor.profileType = getShortString(bytebuffer);
		dataactor.profileUrl = getShortString(bytebuffer);
		return dataactor;
	}

	public static List<DataActor> getDataActorList(List<DataAction> list) {
		Set<String> hashset = new HashSet<String>();
		List<DataActor> arraylist = new ArrayList<DataActor>();
		if (list != null && !list.isEmpty()) {
			Iterator<DataAction> iterator = list.iterator();
			while (iterator.hasNext()) {
				DataAction dataaction = iterator.next();
				if (dataaction != null && dataaction.item != null) {
					Iterator<DataItem> iterator1 = dataaction.item.iterator();
					while (iterator1.hasNext()) {
						DataItem dataitem = iterator1.next();
						if (dataitem != null && dataitem.actor != null) {
							DataActor dataactor = dataitem.actor;
							String s = dataactor.obfuscatedGaiaId;
							if (!hashset.contains(s)) {
								arraylist.add(dataactor);
								hashset.add(s);
							}
						}
					}
				}
			}
		}
		return arraylist;
	}

    private static void putDataAction(DataOutputStream dataoutputstream, DataAction dataaction)
        throws IOException
    {
        putShortString(dataoutputstream, dataaction.type);
        List list = dataaction.item;
        if(list == null)
        {
            dataoutputstream.writeInt(0);
        } else
        {
            dataoutputstream.writeInt(list.size());
            Iterator iterator = list.iterator();
            while(iterator.hasNext()) 
            {
                DataItem dataitem = (DataItem)iterator.next();
                putShortString(dataoutputstream, dataitem.id);
                putShortString(dataoutputstream, dataitem.notificationType);
                putDataActor(dataoutputstream, dataitem.actor);
            }
        }
    }

    private static void putDataActor(DataOutputStream dataoutputstream, DataActor dataactor)
        throws IOException
    {
        putShortString(dataoutputstream, dataactor.gender);
        putShortString(dataoutputstream, dataactor.name);
        putShortString(dataoutputstream, dataactor.obfuscatedGaiaId);
        putShortString(dataoutputstream, dataactor.photoUrl);
        putShortString(dataoutputstream, dataactor.profileType);
        putShortString(dataoutputstream, dataactor.profileUrl);
    }

    public static byte[] serializeDataActionList(List list)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        byte abyte0[];
        if(list == null)
        {
            abyte0 = null;
        } else
        {
            dataoutputstream.writeInt(list.size());
            for(Iterator iterator = list.iterator(); iterator.hasNext(); putDataAction(dataoutputstream, (DataAction)iterator.next()));
            abyte0 = bytearrayoutputstream.toByteArray();
            dataoutputstream.close();
        }
        return abyte0;
    }

    public static byte[] serializeDataActorList(List list)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        byte abyte0[];
        if(list == null)
        {
            abyte0 = null;
        } else
        {
            dataoutputstream.writeInt(list.size());
            for(Iterator iterator = list.iterator(); iterator.hasNext(); putDataActor(dataoutputstream, (DataActor)iterator.next()));
            abyte0 = bytearrayoutputstream.toByteArray();
            dataoutputstream.close();
        }
        return abyte0;
    }
}
