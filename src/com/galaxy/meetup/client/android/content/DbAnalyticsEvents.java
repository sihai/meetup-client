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
import java.util.Iterator;
import java.util.List;

import com.galaxy.meetup.server.client.domain.ClientOzEvent;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class DbAnalyticsEvents extends DbSerializer {

	public static List deserializeClientOzEventList(byte abyte0[])
    {
		if(null == abyte0) {
			return null;
		}
		
		ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
        List list = new ArrayList();
        int i = bytebuffer.getInt();
        for(int j = 0; j < i; j++) {
        	int k = bytebuffer.getInt();
        	byte abyte1[] = new byte[k];
            bytebuffer.get(abyte1, 0, k);
            list.add(JsonUtil.fromByteArray(abyte1, ClientOzEvent.class));
        }
		return list;
    }

    public static byte[] serializeClientOzEventList(List list) throws IOException
    {
        byte abyte0[] = null;
        if(list == null)
        {
            abyte0 = null;
        } else
        {
        	ByteArrayOutputStream bytearrayoutputstream = null;
        	DataOutputStream dataoutputstream = null;
        	
        	try {
	            bytearrayoutputstream = new ByteArrayOutputStream();
	            dataoutputstream = new DataOutputStream(bytearrayoutputstream);
	            dataoutputstream.writeInt(list.size());
	            for(Iterator iterator = list.iterator(); iterator.hasNext();)
	            {
	                byte abyte1[] = JsonUtil.toByteArray((ClientOzEvent)iterator.next());
	                if(abyte1 == null)
	                {
	                    dataoutputstream.writeInt(0);
	                } else
	                {
	                    dataoutputstream.writeInt(abyte1.length);
	                    dataoutputstream.write(abyte1);
	                }
	            }
	            abyte0 = bytearrayoutputstream.toByteArray();
        	} finally {
        		if(null != dataoutputstream) {
        			dataoutputstream.close();
        		}
        		if(null != bytearrayoutputstream) {
        			bytearrayoutputstream.close();
        		}
        	}
        }
        return abyte0;
    }
}
