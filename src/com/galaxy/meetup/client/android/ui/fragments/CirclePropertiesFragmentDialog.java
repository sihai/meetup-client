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
import android.widget.CheckBox;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class CirclePropertiesFragmentDialog extends AlertFragmentDialog
		implements TextWatcher {

	private TextView mInputTextView;
    private CheckBox mJustFollowingCheckBox;
    
    public CirclePropertiesFragmentDialog()
    {
    }

    private void checkPositiveButtonEnabled()
    {
        AlertDialog alertdialog = (AlertDialog)getDialog();
        if(alertdialog != null)
        {
            Button button = alertdialog.getButton(-1);
            boolean flag;
            if(!TextUtils.isEmpty(mInputTextView.getText().toString().trim()))
                flag = true;
            else
                flag = false;
            button.setEnabled(flag);
        }
    }

    private boolean isNewCircle()
    {
        Bundle bundle = getArguments();
        boolean flag;
        if(bundle == null || TextUtils.isEmpty(bundle.getString("circle_id")))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static CirclePropertiesFragmentDialog newInstance$47e87423()
    {
        return new CirclePropertiesFragmentDialog();
    }

    public static CirclePropertiesFragmentDialog newInstance$50fd8769(String s, String s1, boolean flag)
    {
        CirclePropertiesFragmentDialog circlepropertiesfragmentdialog = new CirclePropertiesFragmentDialog();
        Bundle bundle = new Bundle();
        bundle.putString("circle_id", s);
        bundle.putString("name", s1);
        bundle.putBoolean("just_following", flag);
        circlepropertiesfragmentdialog.setArguments(bundle);
        return circlepropertiesfragmentdialog;
    }

    public void afterTextChanged(Editable editable)
    {
    }

    public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
    {
    }

    public void onClick(DialogInterface dialoginterface, int i)
    {
        if(i == -1)
        {
            CirclePropertiesListener circlepropertieslistener = (CirclePropertiesListener)getTargetFragment();
            if(circlepropertieslistener == null)
                circlepropertieslistener = (CirclePropertiesListener)getActivity();
            String s;
            if(isNewCircle())
                s = null;
            else
                s = getArguments().getString("circle_id");
            circlepropertieslistener.onCirclePropertiesChange(s, mInputTextView.getText().toString().trim(), mJustFollowingCheckBox.isChecked());
        }
        super.onClick(dialoginterface, i);
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        android.content.Context context = getDialogContext();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.circle_properties_dialog, null);
        mInputTextView = (TextView)view.findViewById(R.id.text);
        mInputTextView.addTextChangedListener(this);
        mInputTextView.setHint(R.string.new_circle_dialog_hint);
        mJustFollowingCheckBox = (CheckBox)view.findViewById(R.id.just_following_checkbox);
        view.findViewById(R.id.just_following_layout).setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                mJustFollowingCheckBox.toggle();
            }
        });
        int i;
        if(bundle != null)
        {
            mInputTextView.setText(bundle.getCharSequence("name"));
            mJustFollowingCheckBox.setChecked(bundle.getBoolean("just_following"));
        } else
        if(!isNewCircle())
        {
            Bundle bundle1 = getArguments();
            mInputTextView.setText(bundle1.getCharSequence("name"));
            mJustFollowingCheckBox.setChecked(bundle1.getBoolean("just_following"));
        }
        builder.setView(view);
        if(isNewCircle())
            i = R.string.new_circle_dialog_title;
        else
            i = R.string.circle_properties_dialog_title;
        builder.setTitle(i);
        builder.setPositiveButton(R.string.ok, this);
        builder.setNegativeButton(R.string.cancel, this);
        return builder.create();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        bundle.putCharSequence("name", mInputTextView.getText());
        bundle.putBoolean("just_following", mJustFollowingCheckBox.isChecked());
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
    
    
    public static interface CirclePropertiesListener {

        public abstract void onCirclePropertiesChange(String s, String s1, boolean flag);
    }
}
