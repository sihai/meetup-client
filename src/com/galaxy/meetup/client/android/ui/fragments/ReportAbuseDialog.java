/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ReportAbuseDialog extends DialogFragment implements OnClickListener {

	private static final String ABUSE_TYPES[] = {
        "FAKE_USER", "HATE", "IMPERSONATION", "PORN", "SPAM", "COPYRIGHT"
    };
    private static final int ABUSE_TYPE_LABELS[];
    private int mAbuseType;

    static 
    {
        int ai[] = new int[6];
        ai[0] = R.string.report_abuse_reason_fake_profile;
        ai[1] = R.string.report_abuse_reason_hate_speech_or_violence;
        ai[2] = R.string.report_abuse_reason_impersonation;
        ai[3] = R.string.report_abuse_reason_nudity;
        ai[4] = R.string.report_abuse_reason_spam;
        ai[5] = R.string.report_abuse_reason_copyright;
        ABUSE_TYPE_LABELS = ai;
    }
    
    public ReportAbuseDialog()
    {
        mAbuseType = -1;
    }

    public void onClick(DialogInterface dialoginterface, int i)
    {
    	if(-2 == i) {
    		dialoginterface.dismiss();
    	} else if(-1 == i) {
    		 if(mAbuseType != -1)
    	            ((HostedProfileFragment)getTargetFragment()).reportAbuse(ABUSE_TYPES[mAbuseType]);
    	}
    	
    	if(i >= 0)
            mAbuseType = i;
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        if(bundle != null)
            mAbuseType = bundle.getInt("abuse_type");
        FragmentActivity fragmentactivity = getActivity();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragmentactivity);
        builder.setTitle(R.string.report_user_dialog_title);
        builder.setPositiveButton(0x104000a, this);
        builder.setNegativeButton(0x1040000, this);
        builder.setCancelable(true);
        String as[] = new String[ABUSE_TYPE_LABELS.length];
        for(int i = 0; i < ABUSE_TYPE_LABELS.length; i++)
            as[i] = fragmentactivity.getString(ABUSE_TYPE_LABELS[i]);

        builder.setSingleChoiceItems(as, mAbuseType, this);
        return builder.create();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("abuse_type", mAbuseType);
    }
}
