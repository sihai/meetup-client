/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class EditFragmentDialog extends AlertFragmentDialog implements
		TextWatcher {

	private EditText mInputTextView;
	
	public EditFragmentDialog()
    {
    }

    private void checkPositiveButtonEnabled()
    {
    	AlertDialog alertdialog = (AlertDialog)getDialog();
        if(alertdialog != null && !getArguments().getBoolean("allow_empty"))
        {
            Button button = alertdialog.getButton(-1);
            boolean flag;
            if(!TextUtils.isEmpty(mInputTextView.getText().toString().trim()))
                flag = true;
            else
                flag = false;
            button.setEnabled(flag);
        }
        return;
    }

    public static EditFragmentDialog newInstance(String s, String s1, String s2, String s3, String s4, boolean flag)
    {
        Bundle bundle = new Bundle();
        if(s != null)
            bundle.putString("title", s);
        bundle.putString("message", null);
        bundle.putString("hint", s2);
        if(s3 != null)
            bundle.putString("positive", s3);
        if(s4 != null)
            bundle.putString("negative", s4);
        bundle.putBoolean("allow_empty", false);
        EditFragmentDialog editfragmentdialog = new EditFragmentDialog();
        editfragmentdialog.setArguments(bundle);
        return editfragmentdialog;
    }

    public void afterTextChanged(Editable editable)
    {
    }

    public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
    {
    }

    public void onClick(DialogInterface dialoginterface, int i)
    {
        getArguments().putString("message", mInputTextView.getText().toString().trim());
        super.onClick(dialoginterface, i);
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        Bundle bundle1 = getArguments();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = ((LayoutInflater)getActivity().getSystemService("layout_inflater")).inflate(R.layout.text_input_dialog, null);
        mInputTextView = (EditText)view.findViewById(R.id.text_input);
        if(!bundle1.getBoolean("allow_empty"))
            mInputTextView.addTextChangedListener(this);
        if(bundle != null)
            mInputTextView.setText(bundle.getString("message"));
        else
            mInputTextView.setText(bundle1.getString("message"));
        mInputTextView.setHint(bundle1.getString("hint"));
        builder.setView(view);
        if(bundle1.containsKey("title"))
            builder.setTitle(bundle1.getString("title"));
        if(bundle1.containsKey("positive"))
            builder.setPositiveButton(bundle1.getString("positive"), this);
        if(bundle1.containsKey("negative"))
            builder.setNegativeButton(bundle1.getString("negative"), this);
        return builder.create();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        bundle.putString("message", mInputTextView.getText().toString());
    }

    public final void onStart()
    {
        super.onStart();
        checkPositiveButtonEnabled();
    }

    public void onTextChanged(CharSequence charsequence, int i, int j, int k)
    {
        checkPositiveButtonEnabled();
    }

}
