/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.widget.ImageView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class Avatars {

	public static void renderAvatar(Context context, MeetingMember meetingmember, ImageView imageview)
    {
        VCard vcard;
        android.graphics.Bitmap bitmap;
        if(meetingmember == null)
            vcard = null;
        else
            vcard = meetingmember.getVCard();
        bitmap = null;
        if(vcard != null)
        {
            byte abyte0[] = vcard.getAvatarData();
            bitmap = null;
            if(abyte0 != null)
            {
                byte abyte1[] = vcard.getAvatarData();
                bitmap = ImageUtils.decodeByteArray(abyte1, 0, abyte1.length);
            }
        }
        if(bitmap != null)
            imageview.setImageBitmap(bitmap);
        else
            imageview.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_avatar));
    }
}
