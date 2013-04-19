/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.io.Serializable;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class BlockPersonDialog extends DialogFragment implements
		OnClickListener {

	public BlockPersonDialog()
    {
        this(false);
    }

    public BlockPersonDialog(boolean flag)
    {
        this(flag, null);
    }

    public BlockPersonDialog(boolean flag, Serializable serializable)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean("plus_page", flag);
        bundle.putSerializable("callback_data", serializable);
        setArguments(bundle);
    }

    private void configureExplanationLink(TextView textview)
    {
        String s = getActivity().getString(R.string.what_does_this_mean_link);
        SpannableString spannablestring = new SpannableString(s);
        spannablestring.setSpan(new ClickableSpan() {

            public final void onClick(View view)
            {
            	// FIXME
                Intent intent = new Intent("android.intent.action.VIEW", /*url*/null);
                intent.addFlags(0x80000);
                startActivity(intent);
            }
        }, 0, s.length(), 33);
        textview.setText(spannablestring);
        textview.setMovementMethod(LinkMovementMethod.getInstance());
        textview.setClickable(true);
    }

    public void onClick(DialogInterface dialoginterface, int i) {
    	if(-2 == i) {
    		dialoginterface.dismiss();
    	} else if(-1 == i) {
    		Serializable serializable = getArguments().getSerializable("callback_data");
            android.support.v4.app.Fragment fragment = getTargetFragment();
            if(fragment instanceof PersonBlocker)
            {
                ((PersonBlocker)fragment).blockPerson(serializable);
            } else
            {
                FragmentActivity fragmentactivity = getActivity();
                if(fragmentactivity instanceof PersonBlocker)
                    ((PersonBlocker)fragmentactivity).blockPerson(serializable);
            }
    	} else {
    		
    	}
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        boolean flag = getArguments().getBoolean("plus_page");
        FragmentActivity fragmentactivity = getActivity();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragmentactivity);
        int i;
        View view;
        TextView textview;
        int j;
        if(flag)
            i = R.string.block_page_dialog_title;
        else
            i = R.string.block_person_dialog_title;
        builder.setTitle(i);
        builder.setPositiveButton(0x104000a, this);
        builder.setNegativeButton(0x1040000, this);
        builder.setCancelable(true);
        view = LayoutInflater.from(fragmentactivity).inflate(R.layout.block_profile_confirm_dialog, null);
        textview = (TextView)view.findViewById(R.id.message);
        if(flag)
            j = R.string.block_page_dialog_message;
        else
            j = R.string.block_person_dialog_message;
        textview.setText(j);
        configureExplanationLink((TextView)view.findViewById(R.id.explanation));
        builder.setView(view);
        return builder.create();
    }

    public final void onPause()
    {
        super.onPause();
        Dialog dialog = getDialog();
        if(dialog != null)
            ((TextView)dialog.findViewById(R.id.explanation)).setText(null);
    }

    public final void onResume()
    {
        super.onResume();
        Dialog dialog = getDialog();
        if(dialog != null)
            configureExplanationLink((TextView)dialog.findViewById(R.id.explanation));
    }

	public static interface PersonBlocker {

        void blockPerson(Serializable serializable);
    }
}
