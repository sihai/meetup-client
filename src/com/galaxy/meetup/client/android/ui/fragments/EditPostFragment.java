/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.ApiUtils;
import com.galaxy.meetup.client.android.api.OzServerException;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.activity.EditPostActivity;
import com.galaxy.meetup.client.android.ui.view.MentionMultiAutoCompleteTextView;
import com.galaxy.meetup.client.util.SoftInput;

/**
 * 
 * @author sihai
 *
 */
public class EditPostFragment extends Fragment implements
		OnEditorActionListener {
	
	private boolean mChanged;
    private Integer mEditRequestId;
    private MentionMultiAutoCompleteTextView mPostTextView;
    private EsServiceListener mServiceListener;
    
    public EditPostFragment()
    {
        mServiceListener = new EsServiceListener() {

            public final void onEditActivity(int i, ServiceResult serviceresult)
            {
                if(mEditRequestId != null && mEditRequestId.intValue() == i)
                    handleEditPost(serviceresult);
            }
        };
    }

    private void handleEditPost(ServiceResult serviceresult)
    {
        mEditRequestId = null;
        FragmentActivity fragmentactivity = getActivity();
        if(fragmentactivity != null)
        {
            fragmentactivity.dismissDialog(0x48ba7);
            if(serviceresult.hasError())
            {
                Exception exception = serviceresult.getException();
                if((exception instanceof OzServerException) && ((OzServerException)exception).getErrorCode() == 14)
                {
                    AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.post_not_sent_title), getString(R.string.post_restricted_mention_error), getString(R.string.ok), null);
                    alertfragmentdialog.setTargetFragment(getTargetFragment(), 0);
                    alertfragmentdialog.show(getFragmentManager(), "StreamPostRestrictionsNotSupported");
                } else
                {
                    Toast.makeText(fragmentactivity, R.string.edit_post_error, 0).show();
                }
            } else
            {
                fragmentactivity.finish();
            }
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            if(bundle.containsKey("edit_request_id"))
                mEditRequestId = Integer.valueOf(bundle.getInt("edit_request_id"));
            mChanged = bundle.getBoolean("changed", false);
        }
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.edit_comment_fragment, viewgroup, false);
        mPostTextView = (MentionMultiAutoCompleteTextView)view.findViewById(R.id.text);
        Intent intent = getActivity().getIntent();
        EsAccount esaccount = (EsAccount)intent.getParcelableExtra("account");
        String s = intent.getStringExtra("activity_id");
        mPostTextView.init(this, esaccount, s, null);
        String s1 = intent.getStringExtra("content");
        MentionMultiAutoCompleteTextView mentionmultiautocompletetextview = mPostTextView;
        if(s1 == null)
            s1 = "";
        mentionmultiautocompletetextview.setHtml(s1);
        mPostTextView.addTextChangedListener(new TextWatcher() {

            public final void afterTextChanged(Editable editable)
            {
                mChanged = true;
                EditPostActivity editpostactivity = (EditPostActivity)EditPostFragment.this.getActivity();
            }

            public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }
        });
        mPostTextView.setOnEditorActionListener(this);
        return view;
    }

    public final void onDiscard()
    {
        SoftInput.hide(mPostTextView);
        EditPostActivity editpostactivity = (EditPostActivity)getActivity();
        if(mChanged)
        {
            editpostactivity.showDialog(0xdc073);
        } else
        {
            editpostactivity.setResult(0);
            editpostactivity.finish();
        }
    }

    public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent)
    {
        if(textview != mPostTextView) 
        	return false; 
        if(6 == i) {
        	SoftInput.hide(textview);
        	return true;
        } else {
        	return false;
        }
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public final void onPost()
    {
        SoftInput.hide(mPostTextView);
        EditPostActivity editpostactivity = (EditPostActivity)getActivity();
        if(!mChanged || TextUtils.isEmpty(mPostTextView.getText()))
        {
            editpostactivity.setResult(0);
            editpostactivity.finish();
        } else
        {
            Intent intent = editpostactivity.getIntent();
            EsAccount esaccount = (EsAccount)intent.getParcelableExtra("account");
            String s = intent.getStringExtra("activity_id");
            String s1 = ApiUtils.buildPostableString(mPostTextView.getText());
            boolean flag = intent.getBooleanExtra("reshare", false);
            editpostactivity.showDialog(0x48ba7);
            mEditRequestId = Integer.valueOf(EsService.editActivity(editpostactivity, esaccount, s, s1, flag));
        }
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mEditRequestId != null && !EsService.isRequestPending(mEditRequestId.intValue()))
            handleEditPost(EsService.removeResult(mEditRequestId.intValue()));
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mEditRequestId != null)
            bundle.putInt("edit_request_id", mEditRequestId.intValue());
        bundle.putBoolean("changed", mChanged);
    }
}
