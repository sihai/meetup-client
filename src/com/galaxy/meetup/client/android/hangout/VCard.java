/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.io.Serializable;

/**
 * 
 * @author sihai
 *
 */
public class VCard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6379001659766346862L;
	
	private byte avatarData[];
    private String avatarHash;
    private String cellPhoneNumber;
    private String fullName;
    private String homePhoneNumber;
    private boolean isAvatarModified;
    private String workPhoneNumber;
    
    public VCard(String s, boolean flag, byte abyte0[], String s1, String s2, String s3, String s4)
    {
        fullName = s;
        isAvatarModified = flag;
        avatarData = abyte0;
        avatarHash = s1;
        homePhoneNumber = s2;
        workPhoneNumber = s3;
        cellPhoneNumber = s4;
    }

    public final byte[] getAvatarData()
    {
        return avatarData;
    }

    public final String getAvatarHash()
    {
        return avatarHash;
    }

    public final String getCellPhoneNumber()
    {
        return cellPhoneNumber;
    }

    public final String getFullName()
    {
        return fullName;
    }

    public final String getHomePhoneNumber()
    {
        return homePhoneNumber;
    }

    public final boolean getIsAvatarModified()
    {
        return isAvatarModified;
    }

    public final String getWorkPhoneNumber()
    {
        return workPhoneNumber;
    }
}
