/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

/**
 * 
 * @author sihai
 *
 */
public class InstantMessage {

	static final boolean $assertionsDisabled;
    private final MeetingMember from;
    private final String fromMucJid;
    private final String message;

    static 
    {
        boolean flag;
        if(!InstantMessage.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
    InstantMessage(MeetingMember meetingmember, String s, String s1)
    {
        if(!$assertionsDisabled && meetingmember != null && !meetingmember.getMucJid().equals(s))
        {
            throw new AssertionError();
        } else
        {
            from = meetingmember;
            fromMucJid = s;
            message = s1;
            return;
        }
    }
}
