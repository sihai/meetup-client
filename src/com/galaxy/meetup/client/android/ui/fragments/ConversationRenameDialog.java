/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;

/**
 * 
 * @author sihai
 *
 */
public class ConversationRenameDialog extends DialogFragment implements
		OnClickListener, TextWatcher {

	public ConversationRenameDialog()
    {
    }

    public ConversationRenameDialog(String s, long l)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", s);
        bundle.putLong("row_id", l);
        setArguments(bundle);
    }

    public void afterTextChanged(Editable editable)
    {
    }

    public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
    {
    }

    public void onClick(DialogInterface dialoginterface, int i)
    {
    	if(-1 == i) {
    		FragmentActivity fragmentactivity = getActivity();
            EsAccount esaccount = EsAccountsData.getActiveAccount(fragmentactivity);
            if(esaccount != null)
            {
                OzViews ozviews = OzViews.getViewForLogging(fragmentactivity);
                EsAnalytics.recordActionEvent(fragmentactivity, esaccount, OzActions.GROUP_CONVERSATION_RENAME, ozviews);
                Dialog dialog = getDialog();
                if(dialog != null)
                {
                    String s = ((EditText)dialog.findViewById(R.id.conversation_rename_input)).getText().toString();
                    RealTimeChatService.setConversationName(fragmentactivity, esaccount, getArguments().getLong("row_id"), s);
                }
            }
    	}
    	dialoginterface.dismiss();
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        FragmentActivity fragmentactivity = getActivity();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragmentactivity);
        View view = fragmentactivity.getLayoutInflater().inflate(R.layout.conversation_rename, null);
        EditText edittext = (EditText)view.findViewById(R.id.conversation_rename_input);
        edittext.setText(getArguments().getString("name"));
        builder.setTitle(R.string.realtimechat_conversation_rename_dialog_title).setCancelable(false).setView(view).setPositiveButton(getString(R.string.realtimechat_conversation_rename_save_button_text), this).setNegativeButton(getString(R.string.realtimechat_conversation_rename_cancel_button_text), this);
        AlertDialog alertdialog = builder.create();
        edittext.addTextChangedListener(this);
        alertdialog.getWindow().setSoftInputMode(5);
        return alertdialog;
    }

    public void onTextChanged(CharSequence charsequence, int i, int j, int k)
    {
        AlertDialog alertdialog = (AlertDialog)getDialog();
        if(alertdialog != null)
        {
            Button button = alertdialog.getButton(-1);
            boolean flag;
            if(charsequence.toString().trim().length() > 0)
                flag = true;
            else
                flag = false;
            button.setEnabled(flag);
        }
    }

}
