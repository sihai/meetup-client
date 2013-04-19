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
import java.util.List;

/**
 * 
 * @author sihai
 *
 */
public class DbAudienceData extends DbSerializer {

	private static AudienceData deserialize(ByteBuffer bytebuffer)
    {
        short word0 = bytebuffer.getShort();
        List arraylist = new ArrayList(word0);
        for(int i = 0; i < word0; i++)
            arraylist.add(new PersonData(getShortString(bytebuffer), getShortString(bytebuffer), getShortString(bytebuffer), getShortString(bytebuffer)));

        short word1 = bytebuffer.getShort();
        List arraylist1 = new ArrayList(word1);
        for(int j = 0; j < word1; j++)
        {
            String s = getShortString(bytebuffer);
            String s1 = getShortString(bytebuffer);
            arraylist1.add(new CircleData(s, bytebuffer.getInt(), s1, bytebuffer.getInt()));
        }

        short word2 = bytebuffer.getShort();
        List arraylist2 = new ArrayList(word2);
        for(int k = 0; k < word2; k++)
            arraylist2.add(new SquareTargetData(getShortString(bytebuffer), getShortString(bytebuffer), getShortString(bytebuffer), getShortString(bytebuffer)));

        return new AudienceData(arraylist, arraylist1, arraylist2, bytebuffer.getInt());
    }

    public static AudienceData deserialize(byte abyte0[])
    {
        return deserialize(ByteBuffer.wrap(abyte0));
    }

    public static List deserializeList(byte abyte0[])
    {
        ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
        int i = bytebuffer.getInt();
        List arraylist = new ArrayList(i);
        for(int j = 0; j < i; j++)
            arraylist.add(deserialize(bytebuffer));

        return arraylist;
    }

    public static byte[] serialize(AudienceData audiencedata)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = null;
        DataOutputStream dataoutputstream = null;
        try {
	        bytearrayoutputstream = new ByteArrayOutputStream();
	        dataoutputstream = new DataOutputStream(bytearrayoutputstream);
	        dataoutputstream.writeShort(audiencedata.getUserCount());
	        PersonData apersondata[] = audiencedata.getUsers();
	        int i = apersondata.length;
	        for(int j = 0; j < i; j++)
	        {
	            PersonData persondata = apersondata[j];
	            putShortString(dataoutputstream, persondata.getObfuscatedId());
	            putShortString(dataoutputstream, persondata.getName());
	            putShortString(dataoutputstream, persondata.getEmail());
	            putShortString(dataoutputstream, persondata.getCompressedPhotoUrl());
	        }
	
	        dataoutputstream.writeShort(audiencedata.getCircleCount());
	        CircleData acircledata[] = audiencedata.getCircles();
	        int k = acircledata.length;
	        for(int l = 0; l < k; l++)
	        {
	            CircleData circledata = acircledata[l];
	            putShortString(dataoutputstream, circledata.getId());
	            putShortString(dataoutputstream, circledata.getName());
	            dataoutputstream.writeInt(circledata.getType());
	            dataoutputstream.writeInt(circledata.getSize());
	        }
	
	        dataoutputstream.writeShort(audiencedata.getSquareTargetCount());
	        SquareTargetData asquaretargetdata[] = audiencedata.getSquareTargets();
	        int i1 = asquaretargetdata.length;
	        for(int j1 = 0; j1 < i1; j1++)
	        {
	            SquareTargetData squaretargetdata = asquaretargetdata[j1];
	            putShortString(dataoutputstream, squaretargetdata.getSquareId());
	            putShortString(dataoutputstream, squaretargetdata.getSquareName());
	            putShortString(dataoutputstream, squaretargetdata.getSquareStreamId());
	            putShortString(dataoutputstream, squaretargetdata.getSquareStreamName());
	        }
	
	        dataoutputstream.writeInt(audiencedata.getUserCount() + audiencedata.getHiddenUserCount());
	        return bytearrayoutputstream.toByteArray();
        } finally {
        	if(null != bytearrayoutputstream) {
        		bytearrayoutputstream.close();
        	}
        	if(null != dataoutputstream) {
        		dataoutputstream.close();
        	}
        }
    }

    public static byte[] serialize(List arraylist) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = null;
        DataOutputStream dataoutputstream = null;
        
        try {
	        bytearrayoutputstream = new ByteArrayOutputStream();
	        dataoutputstream = new DataOutputStream(bytearrayoutputstream);
	        int i;
	        int j;
	        i = arraylist.size();
	        dataoutputstream.writeInt(i);
	        for(j = 0; j < i; j++) {
	        	dataoutputstream.write(serialize((AudienceData)arraylist.get(j)));
	        }
	        return bytearrayoutputstream.toByteArray();
        } finally {
        	if(null != bytearrayoutputstream) {
        		bytearrayoutputstream.close();
        	}
        	if(null != dataoutputstream) {
        		dataoutputstream.close();
        	}
        }
    }
}
