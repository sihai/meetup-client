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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class MuteProfileDialog extends DialogFragment implements OnClickListener {

	private String mGender;
    private String mName;
    private boolean mTargetMuteState;
    
    public MuteProfileDialog()
    {
    }

    public void onClick(DialogInterface dialoginterface, int i)
    {
    	if(-2 == i) {
    		dialoginterface.dismiss();
    	} else if(-1 == i) {
    		((HostedProfileFragment)getTargetFragment()).setPersonMuted(mTargetMuteState);
    	}
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        Bundle bundle1 = getArguments();
        mName = bundle1.getString("name");
        mGender = bundle1.getString("gender");
        mTargetMuteState = bundle1.getBoolean("target_mute");
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragmentactivity);
        int i;
        Object aobj[];
        View view;
        TextView textview;
        String s;
        if(mTargetMuteState)
            i = R.string.mute_dialog_title;
        else
            i = R.string.unmute_dialog_title;
        aobj = new Object[1];
        aobj[0] = mName;
        builder.setTitle(getString(i, aobj));
        builder.setPositiveButton(0x104000a, this);
        builder.setNegativeButton(0x1040000, this);
        builder.setCancelable(true);
        view = LayoutInflater.from(fragmentactivity).inflate(R.layout.block_profile_confirm_dialog, null);
        textview = (TextView)view.findViewById(R.id.message);
        if(mGender.equals("MALE"))
        {
            int l;
            if(mTargetMuteState)
                l = R.string.mute_dialog_content_male;
            else
                l = R.string.unmute_dialog_content_male;
            s = getString(l);
        } else
        if(mGender.equals("FEMALE"))
        {
            int k;
            if(mTargetMuteState)
                k = R.string.mute_dialog_content_female;
            else
                k = R.string.unmute_dialog_content_female;
            s = getString(k);
        } else
        {
            int j;
            if(mTargetMuteState)
                j = R.string.mute_dialog_content_general;
            else
                j = R.string.unmute_dialog_content_general;
            s = getString(j);
        }
        textview.setText(s);
        ((TextView)view.findViewById(R.id.explanation)).setVisibility(8);
        builder.setView(view);
        return builder.create();
    }
}
