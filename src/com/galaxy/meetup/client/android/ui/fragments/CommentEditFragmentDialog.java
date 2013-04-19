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
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.MentionMultiAutoCompleteTextView;

/**
 * 
 * @author sihai
 *
 */
public class CommentEditFragmentDialog extends AlertFragmentDialog implements
		TextWatcher {

	private MentionMultiAutoCompleteTextView mInputTextView;

	public CommentEditFragmentDialog()
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

    public static CommentEditFragmentDialog newInstance(int i)
    {
        Bundle bundle = new Bundle();
        bundle.putString("comment_id", "");
        bundle.putString("comment_text", "");
        bundle.putInt("title_id", i);
        CommentEditFragmentDialog commenteditfragmentdialog = new CommentEditFragmentDialog();
        commenteditfragmentdialog.setArguments(bundle);
        return commenteditfragmentdialog;
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
            SpannableStringBuilder spannablestringbuilder;
            for(spannablestringbuilder = new SpannableStringBuilder(mInputTextView.getText()); spannablestringbuilder.length() > 0 && Character.isWhitespace(spannablestringbuilder.charAt(0)); spannablestringbuilder.delete(0, 1));
            for(; spannablestringbuilder.length() > 0 && Character.isWhitespace(spannablestringbuilder.charAt(-1 + spannablestringbuilder.length())); spannablestringbuilder.delete(-1 + spannablestringbuilder.length(), spannablestringbuilder.length()));
            CommentEditDialogListener commenteditdialoglistener = (CommentEditDialogListener)getTargetFragment();
            getArguments().getString("comment_id");
            commenteditdialoglistener.onCommentEditComplete(spannablestringbuilder);
        }
        super.onClick(dialoginterface, i);
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        Bundle bundle1 = getArguments();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = ((LayoutInflater)getActivity().getSystemService("layout_inflater")).inflate(R.layout.comment_edit_dialog, null);
        mInputTextView = (MentionMultiAutoCompleteTextView)view.findViewById(R.id.text);
        mInputTextView.addTextChangedListener(this);
        if(bundle != null)
            mInputTextView.setText(bundle.getCharSequence("comment_text"));
        else
            mInputTextView.setHtml(bundle1.getString("comment_text"));
        builder.setView(view);
        builder.setTitle(bundle1.getInt("title_id", R.string.menu_edit_comment));
        builder.setPositiveButton(R.string.ok, this);
        builder.setNegativeButton(R.string.cancel, this);
        return builder.create();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        bundle.putCharSequence("comment_text", mInputTextView.getText());
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
    
    public static interface CommentEditDialogListener
    {
        public abstract void onCommentEditComplete(Spannable spannable);
    }
}
