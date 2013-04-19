/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.OzServerException;
import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.activity.OobDeviceActivity;
import com.galaxy.meetup.client.android.ui.activity.OobSelectPlusPageActivity;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;

/**
 * 
 * @author sihai
 *
 */
public class OobSelectPlusPageFragment extends ListFragment implements
		AlertDialogListener {

	private static final String DIALOG_IDS[] = {
        "activation_progress", "net_failure", "server_error"
    };
    private AccountSettingsData mAccountSettings;
    private final EsServiceListener mEsServiceListener = new ServiceListener();
    private Integer mPendingRequestId;
    private int mSelectedAccountPosition;
    
    public OobSelectPlusPageFragment()
    {
        mSelectedAccountPosition = -1;
    }

    private String[] createAccountNameArray()
    {
        Resources resources = getActivity().getResources();
        int i = mAccountSettings.getNumPlusPages();
        String as[] = new String[i + 1];
        as[0] = mAccountSettings.getUserDisplayName();
        for(int j = 0; j < i; j++)
        {
            String s = mAccountSettings.getPlusPageName(j);
            as[j + 1] = resources.getString(R.string.oob_plus_page_name, new Object[] {
                s
            });
        }

        return as;
    }

    private void handleServiceCallback(int i, ServiceResult serviceresult) {
    	
    	if(mPendingRequestId == null || mPendingRequestId.intValue() != i) {
    		return;
    	}
    	
    	mPendingRequestId = null;
        String as[] = DIALOG_IDS;
        int j = as.length;
        for(int k = 0; k < j; k++)
        {
            String s2 = as[k];
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag(s2);
            if(dialogfragment != null)
                dialogfragment.dismiss();
        }

        if(serviceresult.hasError()) {
        	Exception exception = serviceresult.getException();
        	if(!(exception instanceof OzServerException)) {
        		AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.signup_title_no_connection), getString(R.string.signup_error_network), getString(R.string.signup_retry), getString(R.string.cancel));
                alertfragmentdialog.setCancelable(false);
                alertfragmentdialog.setTargetFragment(this, 0);
                alertfragmentdialog.show(getFragmentManager(), "net_failure"); 
                return;
        	} else {
        		String s;
                String s1;
        		switch(((OzServerException)exception).getErrorCode()) {
	        		case 1:
	        			s1 = getString(R.string.signup_authentication_error);
	        	        s = null;
	        			break;
	        		case 10:
	        			s1 = getString(R.string.signup_required_update_available);
	        	        s = null;
	        			break;
	        		case 12:
	        			s1 = getString(R.string.signup_profile_error);
	        	        s = null;
	        			break;
	        		case 14:
	        		case 15:
	        			s = getString(R.string.signup_title_mobile_not_available);
	        	        s1 = getString(R.string.signup_text_mobile_not_available);
	        			 break;
        			 default:
        				 s = getString(R.string.signup_title_no_connection);
        			     s1 = getString(R.string.signup_error_network);
        				 break;
        		}
        		AlertFragmentDialog alertfragmentdialog1 = AlertFragmentDialog.newInstance(s, s1, getString(R.string.ok), null);
                alertfragmentdialog1.setCancelable(false);
                alertfragmentdialog1.setTargetFragment(this, 0);
                alertfragmentdialog1.show(getFragmentManager(), "server_error");
                return;
        	}
        } else {
        	((OobDeviceActivity)getActivity()).onContinue();
        }
        
    }

    public final void activateAccount()
    {
        if(isAccountSelected())
        {
            FragmentActivity fragmentactivity = getActivity();
            EsAccount esaccount = (EsAccount)fragmentactivity.getIntent().getParcelableExtra("account");
            boolean flag;
            String s;
            String s1;
            String s2;
            if(mSelectedAccountPosition > 0)
                flag = true;
            else
                flag = false;
            if(flag)
            {
                int i = -1 + mSelectedAccountPosition;
                s = mAccountSettings.getPlusPageId(i);
                s1 = mAccountSettings.getPlusPageName(i);
                s2 = mAccountSettings.getPlusPagePhotoUrl(i);
            } else
            {
                s = mAccountSettings.getUserGaiaId();
                s1 = mAccountSettings.getUserDisplayName();
                s2 = mAccountSettings.getUserPhotoUrl();
            }
            ProgressFragmentDialog.newInstance(null, getString(R.string.signup_signing_in), false).show(getFragmentManager(), "activation_progress");
            mPendingRequestId = Integer.valueOf(EsService.activateAccount(fragmentactivity, esaccount, s, s1, s2, flag, mAccountSettings));
        }
    }

    public final boolean isAccountSelected()
    {
        boolean flag;
        if(mSelectedAccountPosition != -1)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        mAccountSettings = (AccountSettingsData)getActivity().getIntent().getParcelableExtra("plus_pages");
        View view = layoutinflater.inflate(R.layout.oob_select_plus_page_fragment, viewgroup, false);
        setListAdapter(new ArrayAdapter(getActivity(), 0x109000f, createAccountNameArray()));
        if(bundle != null)
        {
            mSelectedAccountPosition = bundle.getInt("selected_account", -1);
            if(bundle.containsKey("reqid"))
                mPendingRequestId = Integer.valueOf(bundle.getInt("reqid"));
        }
        return view;
    }

    public final void onDialogCanceled(String s)
    {
        throw new IllegalStateException("OOB dialog not cancelable");
    }

    public final void onDialogListClick(int i, Bundle bundle)
    {
    }

    public final void onDialogNegativeClick(String s)
    {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s)
    {
        if("net_failure".equals(s))
            activateAccount();
    }

    public final void onListItemClick(ListView listview, View view, int i, long l)
    {
        super.onListItemClick(listview, view, i, l);
        boolean flag = isAccountSelected();
        mSelectedAccountPosition = i;
        if(!flag)
            ((OobSelectPlusPageActivity)getActivity()).setContinueButtonEnabled(true);
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mEsServiceListener);
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mEsServiceListener);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingRequestId.intValue());
            if(serviceresult != null)
                handleServiceCallback(mPendingRequestId.intValue(), serviceresult);
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("selected_account", mSelectedAccountPosition);
        if(mPendingRequestId != null)
            bundle.putInt("reqid", mPendingRequestId.intValue());
    }

    public final void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        getListView().setChoiceMode(1);
    }
    
    private final class ServiceListener extends EsServiceListener
    {

        public final void onAccountActivated$6a63df5(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

    }
}
