/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 
 * @author sihai
 *
 */
public class ConfirmIntentDialog extends DialogFragment implements OnClickListener {

	public ConfirmIntentDialog()
    {
    }

    public static DialogFragment newInstance(CharSequence charsequence, CharSequence charsequence1, CharSequence charsequence2, Intent intent)
    {
        Bundle bundle = new Bundle();
        bundle.putCharSequence("title", charsequence);
        bundle.putCharSequence("message", charsequence1);
        bundle.putCharSequence("positive", charsequence2);
        bundle.putParcelable("intent", intent);
        ConfirmIntentDialog confirmintentdialog = new ConfirmIntentDialog();
        confirmintentdialog.setArguments(bundle);
        return confirmintentdialog;
    }

    public void onClick(DialogInterface dialoginterface, int i) {
        if(-1 == i) {
        	startActivity((Intent)getArguments().getParcelable("intent"));
        }
        dismiss();
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        Bundle bundle1 = getArguments();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle(bundle1.getCharSequence("title"));
        builder.setMessage(bundle1.getCharSequence("message"));
        builder.setPositiveButton(bundle1.getCharSequence("positive"), this);
        return builder.create();
    }

}
