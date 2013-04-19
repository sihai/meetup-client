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
import com.galaxy.meetup.client.android.ui.activity.EditCommentActivity;
import com.galaxy.meetup.client.android.ui.view.MentionMultiAutoCompleteTextView;
import com.galaxy.meetup.client.util.SoftInput;

/**
 * 
 * @author sihai
 *
 */
public class EditCommentFragment extends Fragment implements
		OnEditorActionListener {

	protected boolean mChanged;
    protected String mCommentId;
    protected MentionMultiAutoCompleteTextView mCommentTextView;
    protected Integer mPendingRequestId;
    protected EsServiceListener mServiceListener;
    
    public EditCommentFragment()
    {
        mServiceListener = new EsServiceListener() {

            public final void onEditComment(int i, ServiceResult serviceresult)
            {
                if(mPendingRequestId != null && mPendingRequestId.intValue() == i)
                    handleEditComment(serviceresult);
            }

            public final void onEditPhotoCommentComplete$6a63df5(int i, ServiceResult serviceresult)
            {
                if(mPendingRequestId != null && mPendingRequestId.intValue() == i)
                    handleEditComment(serviceresult);
            }
        };
    }

    private void handleEditComment(ServiceResult serviceresult)
    {
        mPendingRequestId = null;
        FragmentActivity fragmentactivity = getActivity();
        if(fragmentactivity != null)
        {
            fragmentactivity.dismissDialog(0x32c15);
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
                    Toast.makeText(fragmentactivity, R.string.comment_edit_error, 0).show();
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
            mChanged = bundle.getBoolean("changed", false);
            if(bundle.containsKey("request_id"))
                mPendingRequestId = Integer.valueOf(bundle.getInt("request_id"));
        }
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.edit_comment_fragment, viewgroup, false);
        mCommentTextView = (MentionMultiAutoCompleteTextView)view.findViewById(R.id.text);
        Intent intent = getActivity().getIntent();
        EsAccount esaccount = (EsAccount)intent.getParcelableExtra("account");
        String s = intent.getStringExtra("activity_id");
        mCommentId = intent.getStringExtra("comment_id");
        mCommentTextView.init(this, esaccount, s, null);
        if(bundle == null)
            mCommentTextView.setHtml(intent.getStringExtra("comment"));
        mCommentTextView.addTextChangedListener(new TextWatcher() {

            public final void afterTextChanged(Editable editable)
            {
                mChanged = true;
                EditCommentActivity editcommentactivity = (EditCommentActivity)EditCommentFragment.this.getActivity();
            }

            public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }
        });
        mCommentTextView.setOnEditorActionListener(this);
        return view;
    }

    public final void onDiscard()
    {
        SoftInput.hide(mCommentTextView);
        EditCommentActivity editcommentactivity = (EditCommentActivity)getActivity();
        if(mChanged)
        {
            editcommentactivity.showDialog(0xdc072);
        } else
        {
            editcommentactivity.setResult(0);
            editcommentactivity.finish();
        }
    }

    public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent) {
        if(textview != mCommentTextView) 
        	return false; 
        else {
        	if(6 == i) {
        		SoftInput.hide(textview);
        		return true;
        	} else {
        		return false;
        	}
        }
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public final void onPost()
    {
        SoftInput.hide(mCommentTextView);
        EditCommentActivity editcommentactivity = (EditCommentActivity)getActivity();
        if(!mChanged || TextUtils.isEmpty(mCommentTextView.getText()))
        {
            editcommentactivity.setResult(0);
            editcommentactivity.finish();
        } else
        {
            Intent intent = editcommentactivity.getIntent();
            EsAccount esaccount = (EsAccount)intent.getParcelableExtra("account");
            String s = intent.getStringExtra("activity_id");
            String s1 = ApiUtils.buildPostableString(mCommentTextView.getText());
            if(intent.hasExtra("photo_id"))
                mPendingRequestId = Integer.valueOf(EsService.editPhotoComment(editcommentactivity, esaccount, s, mCommentId, s1));
            else
                mPendingRequestId = Integer.valueOf(EsService.editComment(editcommentactivity, esaccount, s, mCommentId, s1));
            editcommentactivity.showDialog(0x32c15);
        }
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
            handleEditComment(EsService.removeResult(mPendingRequestId.intValue()));
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("changed", mChanged);
        if(mPendingRequestId != null)
            bundle.putInt("request_id", mPendingRequestId.intValue());
    }
}
