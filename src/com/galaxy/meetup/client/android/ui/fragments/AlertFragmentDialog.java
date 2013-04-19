/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;

/**
 * 
 * @author sihai
 *
 */
public class AlertFragmentDialog extends DialogFragment implements OnClickListener {

	private AlertDialogListener alertDialogListener;
	
	public AlertFragmentDialog() {
	}

	public static AlertFragmentDialog newInstance(String s, String s1,
			String s2, String s3) {
		return newInstance(s, s1, s2, s3, 0);
	}

	public static AlertFragmentDialog newInstance(String title, String message, String positive, String negative, int iconResourceId) {
		Bundle bundle = new Bundle();
		if (title != null)
			bundle.putString("title", title);
		bundle.putString("message", message);
		if (positive != null)
			bundle.putString("positive", positive);
		if (negative != null)
			bundle.putString("negative", negative);
		if (iconResourceId != 0)
			bundle.putInt("icon", iconResourceId);
		AlertFragmentDialog alertfragmentdialog = new AlertFragmentDialog();
		alertfragmentdialog.setArguments(bundle);
		return alertfragmentdialog;
	}

    public static AlertFragmentDialog newInstance(String title, String as[]) {
        Bundle bundle = new Bundle();
        if(title != null)
            bundle.putString("title", title);
        if(as != null)
            bundle.putStringArray("list", as);
        AlertFragmentDialog alertfragmentdialog = new AlertFragmentDialog();
        alertfragmentdialog.setArguments(bundle);
        return alertfragmentdialog;
    }

    public final Context getDialogContext() {
        Object obj;
        if(android.os.Build.VERSION.SDK_INT >= 11)
            obj = getActivity();
        else
            obj = new ContextThemeWrapper(getActivity(), 0x103000b);
        return ((Context) (obj));
    }

    public void onCancel(DialogInterface dialoginterface) {
        AlertDialogListener alertdialoglistener = alertDialogListener;
        if(alertdialoglistener == null)
            alertdialoglistener = (AlertDialogListener)getTargetFragment();
        if(alertdialoglistener != null) {
            getArguments();
            alertdialoglistener.onDialogCanceled(getTag());
        }
    }

    public void onClick(DialogInterface dialoginterface, int i) {
        AlertDialogListener alertdialoglistener;
        alertdialoglistener = alertDialogListener;
        if(alertdialoglistener == null && (getTargetFragment() instanceof AlertDialogListener))
            alertdialoglistener = (AlertDialogListener)getTargetFragment();
        if(alertdialoglistener == null) {
        	return;
        }
        switch(i) {
	        case -2 :
	        	 getArguments();
	             alertdialoglistener.onDialogNegativeClick(getTag());
	             break;
	        case -1:
	        	alertdialoglistener.onDialogPositiveClick(getArguments(), getTag());
	        	break;
	        default:
	        	Bundle bundle = getArguments();
	            if(bundle.containsKey("list") && i >= 0)
	            {
	                getTag();
	                alertdialoglistener.onDialogListClick(i, bundle);
	            }
	            break;
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
    	// XXX ? why not use bundle
        Bundle bundle1 = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(bundle1.containsKey("title"))
            builder.setTitle(bundle1.getString("title"));
        if(bundle1.containsKey("message"))
            builder.setMessage(bundle1.getString("message"));
        if(bundle1.containsKey("positive"))
            builder.setPositiveButton(bundle1.getString("positive"), this);
        if(bundle1.containsKey("negative"))
            builder.setNegativeButton(bundle1.getString("negative"), this);
        if(bundle1.containsKey("icon"))
            builder.setIcon(bundle1.getInt("icon"));
        if(bundle1.containsKey("list"))
            builder.setItems(bundle1.getStringArray("list"), this);
        return builder.create();
    }

    public final void setListener(AlertDialogListener alertdialoglistener) {
        alertDialogListener = alertdialoglistener;
    }
	
    
	public static interface AlertDialogListener {

		void onDialogCanceled(String s);

		void onDialogListClick(int i, Bundle bundle);

		void onDialogNegativeClick(String s);

		void onDialogPositiveClick(Bundle bundle, String s);
	}
}
