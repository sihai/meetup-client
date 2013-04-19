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

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class UnblockPersonDialog extends DialogFragment implements
		OnClickListener {

	public UnblockPersonDialog()
    {
    }

    public UnblockPersonDialog(String s, boolean flag)
    {
        Bundle bundle = new Bundle();
        bundle.putString("person_id", s);
        bundle.putBoolean("plus_page", flag);
        setArguments(bundle);
    }

    public void onClick(DialogInterface dialoginterface, int i) {
    	if(-2 == i) {
    		dialoginterface.dismiss();
    	} else if(-1 == i) {
    		String s = getArguments().getString("person_id");
            if(getTargetFragment() instanceof PersonUnblocker)
                ((PersonUnblocker)getTargetFragment()).unblockPerson(s);
            else
                ((PersonUnblocker)getActivity()).unblockPerson(s);
    	} else {
    		
    	}
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        boolean flag = getArguments().getBoolean("plus_page");
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragmentactivity);
        int i;
        int j;
        if(flag)
            i = R.string.unblock_page_dialog_title;
        else
            i = R.string.unblock_person_dialog_title;
        builder.setTitle(i);
        if(flag)
            j = R.string.unblock_page_dialog_message;
        else
            j = R.string.unblock_person_dialog_message;
        builder.setMessage(j);
        builder.setPositiveButton(0x104000a, this);
        builder.setNegativeButton(0x1040000, this);
        builder.setCancelable(true);
        return builder.create();
    }

	public static interface PersonUnblocker {

        void unblockPerson(String s);
    }
}
