/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.ContextThemeWrapper;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ProgressFragmentDialog extends DialogFragment {

	public ProgressFragmentDialog()
    {
    }

    public static ProgressFragmentDialog newInstance(String s, String s1)
    {
        return newInstance(s, s1, true);
    }

    public static ProgressFragmentDialog newInstance(String s, String s1, boolean flag)
    {
        Bundle bundle = new Bundle();
        if(s != null)
            bundle.putString("title", s);
        bundle.putString("message", s1);
        ProgressFragmentDialog progressfragmentdialog = new ProgressFragmentDialog();
        progressfragmentdialog.setArguments(bundle);
        progressfragmentdialog.setCancelable(flag);
        return progressfragmentdialog;
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        Bundle bundle1 = getArguments();
        ProgressDialog progressdialog = new ProgressDialog(new ContextThemeWrapper(getActivity(), R.style.Theme_EmeraldSea));
        if(bundle1.containsKey("title"))
            progressdialog.setTitle(bundle1.getString("title"));
        progressdialog.setMessage(bundle1.getString("message"));
        progressdialog.setCanceledOnTouchOutside(isCancelable());
        progressdialog.setProgressStyle(0);
        return progressdialog;
    }

    public final void show(FragmentManager fragmentmanager, String s)
    {
        super.show(fragmentmanager, s);
    }
}
