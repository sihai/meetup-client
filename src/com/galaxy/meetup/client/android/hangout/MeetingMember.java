/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.io.Serializable;
import java.util.Comparator;

import android.content.Context;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class MeetingMember implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7490149209421883263L;
	
	private static String BIG_NASTY_GAIA_ID_PREFIX = "g:";
    private static boolean isAnonymousMuc = false;
    private Status currentStatus;
    private final int entryOrder;
    private final String gaiaId;
    private boolean isMediaBlocked;
    private final boolean isSelf;
    private final boolean isSelfProfile;
    private boolean isVideoPaused;
    private final String memberMucJid;
    private final String nickName;
    private Status previousStatus;
    private VCard vCard;

    static 
    {
        isAnonymousMuc = true;
    }
    
    public MeetingMember(String s, String s1, String s2, int i, boolean flag, boolean flag1)
    {
        memberMucJid = s;
        nickName = s1;
        previousStatus = Status.DISCONNECTED;
        currentStatus = Status.DISCONNECTED;
        gaiaId = s2;
        entryOrder = i;
        isSelf = flag;
        isSelfProfile = flag1;
    }

    public final Status getCurrentStatus()
    {
        return currentStatus;
    }

    public final String getId()
    {
        return (new StringBuilder()).append(BIG_NASTY_GAIA_ID_PREFIX).append(gaiaId).toString();
    }

    public final String getMucJid()
    {
        return memberMucJid;
    }

    final String getName(Context context)
    {
        String s;
        if(vCard == null)
        {
            if(isAnonymousMuc)
                s = context.getResources().getString(R.string.hangout_anonymous_person);
            else
                s = nickName;
        } else
        {
            s = vCard.getFullName();
        }
        return s;
    }

    public final Status getPreviousStatus()
    {
        return previousStatus;
    }

    public final VCard getVCard()
    {
        return vCard;
    }

    public final boolean isMediaBlocked()
    {
        return isMediaBlocked;
    }

    public final boolean isSelf()
    {
        return isSelf;
    }

    public final boolean isSelfProfile()
    {
        return isSelfProfile;
    }

    public final boolean isVideoPaused()
    {
        return isVideoPaused;
    }

    public final void setCurrentStatus(Status status)
    {
        if(currentStatus != status)
        {
            previousStatus = currentStatus;
            currentStatus = status;
        }
    }

    public final void setMediaBlocked(boolean flag)
    {
        isMediaBlocked = true;
    }

    final void setVCard(VCard vcard)
    {
        vCard = vcard;
    }

    public final void setVideoPaused(boolean flag)
    {
        isVideoPaused = flag;
    }
    
    
	static final class SortByEntryOrder implements Comparator {

		public final int compare(Object obj, Object obj1) {
			MeetingMember meetingmember = (MeetingMember) obj;
			MeetingMember meetingmember1 = (MeetingMember) obj1;
			return meetingmember.entryOrder - meetingmember1.entryOrder;
		}

		SortByEntryOrder() {
		}
	}

	public static enum Status
	{
	        DISCONNECTED,
	        CONNECTING,
	        CONNECTED;
	}
}
