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
public class CircleSubscribeDialog extends DialogFragment implements
		OnClickListener {

	private int mAction;
    private String mCircleId;
    private String mCircleName;
    
    public CircleSubscribeDialog()
    {
    }

    public void onClick(DialogInterface dialoginterface, int i) {
    	if(-2 == i) {
    		dialoginterface.dismiss();
    	} else if(-1 == i) {
    		if(mAction == 2)
                ((HostedStreamFragment)getTargetFragment()).doCircleSubscribe(mCircleId, mCircleName);
            else
                ((HostedStreamFragment)getTargetFragment()).doCircleUnsubscribe(mCircleId, mCircleName);
    	}
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        Bundle bundle1 = getArguments();
        mAction = bundle1.getInt("do_subscribe");
        mCircleName = bundle1.getString("circle_name");
        mCircleId = bundle1.getString("circle_id");
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragmentactivity);
        int i;
        View view;
        TextView textview;
        int j;
        Object aobj[];
        if(mAction == 2)
            i = R.string.dialog_title_subscribe;
        else
            i = R.string.dialog_title_unsubscribe;
        builder.setTitle(getString(i));
        builder.setPositiveButton(0x104000a, this);
        builder.setNegativeButton(0x1040000, this);
        builder.setCancelable(true);
        view = LayoutInflater.from(fragmentactivity).inflate(R.layout.block_profile_confirm_dialog, null);
        textview = (TextView)view.findViewById(R.id.message);
        if(mAction == 2)
            j = R.string.dialog_content_subscribe;
        else
            j = R.string.dialog_content_unsubscribe;
        aobj = new Object[1];
        aobj[0] = mCircleName;
        textview.setText(getString(j, aobj));
        ((TextView)view.findViewById(R.id.explanation)).setVisibility(8);
        builder.setView(view);
        return builder.create();
    }
}
