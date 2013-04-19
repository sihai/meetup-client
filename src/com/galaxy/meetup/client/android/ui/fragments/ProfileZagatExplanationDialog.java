/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ProfileZagatExplanationDialog extends DialogFragment implements OnClickListener {

	public ProfileZagatExplanationDialog()
    {
    }

    public void onClick(View view)
    {
        dismiss();
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setStyle(1, R.style.ProfileLocalZagatExplanationDialog);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.zagat_explanation_dialog, viewgroup);
        ((Button)view.findViewById(R.id.hint_ok)).setOnClickListener(this);
        return view;
    }
}
