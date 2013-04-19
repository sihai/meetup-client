/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.OzServerException;
import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.oob.ActionCallback;
import com.galaxy.meetup.client.android.oob.BaseFieldLayout;
import com.galaxy.meetup.client.android.oob.OutOfBoxDialogInflater;
import com.galaxy.meetup.client.android.oob.OutOfBoxInflater;
import com.galaxy.meetup.client.android.oob.OutOfBoxRequestParcelable;
import com.galaxy.meetup.client.android.oob.OutOfBoxResponseParcelable;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.view.ActionButton;
import com.galaxy.meetup.client.android.ui.view.BottomActionBar;
import com.galaxy.meetup.client.util.SoftInput;
import com.galaxy.meetup.server.client.domain.OutOfBoxAction;
import com.galaxy.meetup.server.client.domain.request.MobileOutOfBoxRequest;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;

/**
 * 
 * @author sihai
 *
 */
public class OutOfBoxFragment extends Fragment implements AlertDialogListener, ActionCallback {

	static final boolean $assertionsDisabled;
    private static final String DIALOG_IDS[] = {
        "sending", "net_failure", "event", "server_error"
    };
    private EsAccount mAccount;
    private BottomActionBar mBottomActionBar;
    private final EsServiceListener mEsServiceListener = new OobEsServiceListener();
    private MobileOutOfBoxRequest mLastRequest;
    private ViewGroup mOobFields;
    private OutOfBoxDialogInflater mOutOfBoxDialogInflater;
    private OutOfBoxInflater mOutOfBoxInflater;
    private MobileOutOfBoxResponse mOutOfBoxResponse;
    private Integer mPendingRequestId;
    private ViewGroup mSignUpLayout;
    private String mUpgradeOrigin;
    private ViewSwitcher mViewSwitcher;

    static 
    {
        boolean flag;
        if(!OutOfBoxFragment.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
    public OutOfBoxFragment()
    {
    }

    private void close()
    {
        getActivity().setResult(0);
        getActivity().finish();
    }

    public static String createInitialTag()
    {
        return Integer.toString(0);
    }

    private void handleServiceCallback(int i, ServiceResult serviceresult) {
    	
    	if(null == mPendingRequestId || mPendingRequestId.intValue() != i) {
    		return;
    	}
    	
    	MobileOutOfBoxResponse mobileoutofboxresponse;
        AccountSettingsData accountsettingsdata;
        Exception exception;
        mPendingRequestId = null;
        mobileoutofboxresponse = EsService.removeOutOfBoxResponse(i);
        accountsettingsdata = EsService.removeAccountSettingsResponse(i);
        EsAccount esaccount = EsService.getActiveAccount(getActivity());
        if(!mAccount.equals(esaccount))
        {
            close();
            return;
        }
        
        if(mobileoutofboxresponse != null && !serviceresult.hasError()) {
        	String as[] = DIALOG_IDS;
            int j = as.length;
            for(int k = 0; k < j; k++)
            {
                String s3 = as[k];
                DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag(s3);
                if(dialogfragment != null)
                    dialogfragment.dismiss();
            }

            FragmentActivity fragmentactivity = getActivity();
            if(mobileoutofboxresponse.signupComplete != null && mobileoutofboxresponse.signupComplete.booleanValue())
            {
                mAccount = EsService.getActiveAccount(fragmentactivity);
                EsAccount esaccount1 = mAccount;
                Intent intent = null;
                if(esaccount1 != null)
                    intent = Intents.getNextOobIntent(fragmentactivity, mAccount, accountsettingsdata, fragmentactivity.getIntent());
                if(intent != null)
                {
                    startActivityForResult(intent, 1);
                } else
                {
                    fragmentactivity.setResult(-1);
                    fragmentactivity.finish();
                }
            } else
            {
                boolean flag;
                FragmentTransaction fragmenttransaction;
                String s2;
                if(isDialog())
                {
                    if(getFragmentManager().getBackStackEntryCount() > 0)
                    {
                        getFragmentManager().popBackStack();
                        flag = true;
                    } else
                    {
                        flag = false;
                    }
                } else
                {
                    flag = true;
                }
                if(getActivity().getCurrentFocus() != null)
                    SoftInput.hide(getActivity().getCurrentFocus());
                fragmenttransaction = getFragmentManager().beginTransaction();
                s2 = Integer.toString(1 + Integer.parseInt(getTag()));
                fragmenttransaction.add(R.id.oob_container, newInstance(mAccount, mobileoutofboxresponse, mUpgradeOrigin), s2);
                if(flag)
                    fragmenttransaction.addToBackStack(s2);
                fragmenttransaction.commit();
            }
        } else {
        	exception = serviceresult.getException();
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
        }
    	
    }

    private boolean isDialog()
    {
        boolean flag;
        if(mOutOfBoxResponse.view.dialog != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static OutOfBoxFragment newInstance(EsAccount esaccount, MobileOutOfBoxResponse mobileoutofboxresponse, String s)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", esaccount);
        bundle.putParcelable("oob_resp", new OutOfBoxResponseParcelable(mobileoutofboxresponse));
        bundle.putString("upgrade_origin", s);
        OutOfBoxFragment outofboxfragment = new OutOfBoxFragment();
        outofboxfragment.setArguments(bundle);
        return outofboxfragment;
    }

    private void updateActionButtons() {
    	
    	boolean flag = true;
    	int count = mOobFields.getChildCount();
    	BaseFieldLayout basefieldlayout1;
    	for(int i = 0; i < count; i++) {
    		basefieldlayout1 = (BaseFieldLayout)mOobFields.getChildAt(i);
    		if(basefieldlayout1.shouldPreventCompletionAction() && basefieldlayout1.isEmpty()) {
    			int j = 0;
    	        while(j < mOobFields.getChildCount()) 
    	        {
    	            BaseFieldLayout basefieldlayout = (BaseFieldLayout)mOobFields.getChildAt(j);
    	            if("CONTINUE".equals(basefieldlayout.getActionType()))
    	            {
    	                boolean flag2;
    	                if(!flag)
    	                    flag2 = true;
    	                else
    	                    flag2 = false;
    	                basefieldlayout.setActionEnabled(flag2);
    	            }
    	            j++;
    	        }
    		}
    	}
    	
    	Iterator iterator = mBottomActionBar.getButtons().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            ActionButton actionbutton = (ActionButton)iterator.next();
            if("CONTINUE".equals(((OutOfBoxAction)actionbutton.getTag()).type))
            {
                boolean flag1;
                if(!flag)
                    flag1 = true;
                else
                    flag1 = false;
                actionbutton.setEnabled(flag1);
            }
        } while(true);
    	// TODO
    }

    public final void onAction(OutOfBoxAction outofboxaction) {
    	
        if("URL".equals(outofboxaction.type)) {
        	Intents.viewUrl(getActivity(), mAccount, outofboxaction.url);
        	return;
        } else if("BACK".equals(outofboxaction.type)) {
        	if(!getFragmentManager().popBackStackImmediate())
                close();
        } else if("CLOSE".equals(outofboxaction.type)) {
        	close();
        } else {
        	MobileOutOfBoxRequest mobileoutofboxrequest = new MobileOutOfBoxRequest();
            mobileoutofboxrequest.input = new ArrayList();
            for(int i = 0; i < mOobFields.getChildCount(); i++)
            {
                BaseFieldLayout basefieldlayout = (BaseFieldLayout)mOobFields.getChildAt(i);
                if(basefieldlayout.getField().input != null)
                    mobileoutofboxrequest.input.add(basefieldlayout.newFieldFromInput());
            }

            mobileoutofboxrequest.action = new OutOfBoxAction();
            mobileoutofboxrequest.action.type = outofboxaction.type;
            sendOutOfBoxRequest(mobileoutofboxrequest);
        }
    }

    public final void onActionId(String s)
    {
    	String s1;
        int value = Integer.parseInt(s);
        switch(value) {
	        case 1:
	        	s1 = "CLOSE";
	        	break;
	        case 2:
	        	s1 = "CONTINUE";
	        	break;
	        case 3:
	        	s1 = "URL";
	        	break;
	        case 4:
	        	s1 = "BACK";
	        	break;
        	default:
        		s1 = null;
        		break;
        }
        
        if("BACK".equals(s1)) {
        	if(!getFragmentManager().popBackStackImmediate())
                close();
        	return;
        } else if("CLOSE".equals(s1)) {
        	close();
        	return;
        }
        
        try
        {
            MobileOutOfBoxRequest mobileoutofboxrequest = new MobileOutOfBoxRequest();
            OutOfBoxAction outofboxaction = new OutOfBoxAction();
            outofboxaction.type = s1;
            mobileoutofboxrequest.action = outofboxaction;
            sendOutOfBoxRequest(mobileoutofboxrequest);
        }
        catch(NumberFormatException numberformatexception)
        {
            Log.w("OutOfBoxFragment", (new StringBuilder("Unable to parse actionId: ")).append(s).append(", not calling action on this event.").toString(), numberformatexception);
        }
        
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
    	if(1 == i) {
    		getActivity().setResult(j);
            getActivity().finish();
    	} else {
    		super.onActivityResult(i, j, intent);
    	}
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        mAccount = (EsAccount)getArguments().getParcelable("account");
        mOutOfBoxResponse = ((OutOfBoxResponseParcelable)getArguments().getParcelable("oob_resp")).getResponse();
        mUpgradeOrigin = getArguments().getString("upgrade_origin");
        View view = layoutinflater.inflate(R.layout.out_of_box_fragment, viewgroup, false);
        mViewSwitcher = (ViewSwitcher)view.findViewById(R.id.switcher);
        mSignUpLayout = (ViewGroup)view.findViewById(R.id.signup_layout);
        mOobFields = (ViewGroup)view.findViewById(R.id.signup_items);
        mBottomActionBar = (BottomActionBar)view.findViewById(R.id.bottom_bar);
        mOutOfBoxInflater = new OutOfBoxInflater(mSignUpLayout, mOobFields, mBottomActionBar);
        mOutOfBoxDialogInflater = new OutOfBoxDialogInflater(getActivity(), (ViewGroup)view.findViewById(R.id.dialog_content), mOutOfBoxResponse.view, this);
        if(isDialog())
        {
            mOutOfBoxDialogInflater.inflate();
            int i = mViewSwitcher.indexOfChild(mViewSwitcher.findViewById(R.id.dialog_frame));
            mViewSwitcher.setDisplayedChild(i);
        } else
        {
            mOutOfBoxInflater.inflateFromResponse(mOutOfBoxResponse.view, this);
            updateActionButtons();
        }
        if(bundle != null)
        {
            OutOfBoxRequestParcelable outofboxrequestparcelable = (OutOfBoxRequestParcelable)bundle.getParcelable("last_request");
            if(outofboxrequestparcelable != null)
                mLastRequest = outofboxrequestparcelable.getRequest();
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
        if(!$assertionsDisabled && !"net_failure".equals(s))
        {
            throw new AssertionError();
        } else
        {
            close();
            return;
        }
    }

    public final void onDialogPositiveClick(Bundle bundle, String s)
    {
        if(!"net_failure".equals(s)) {
        	if("server_error".equals(s))
                close(); 
        } else { 
        	if(mLastRequest != null)
                sendOutOfBoxRequest(mLastRequest);
        }
    }

    public final void onInputChanged()
    {
        updateActionButtons();
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
        bundle.putParcelable("last_request", new OutOfBoxRequestParcelable(mLastRequest));
        if(mPendingRequestId != null)
            bundle.putInt("reqid", mPendingRequestId.intValue());
    }

    public final void sendOutOfBoxRequest(MobileOutOfBoxRequest mobileoutofboxrequest)
    {
        ProgressFragmentDialog.newInstance(null, getString(R.string.signup_sending), false).show(getFragmentManager(), "sending");
        mobileoutofboxrequest.upgradeOrigin = mUpgradeOrigin;
        mLastRequest = mobileoutofboxrequest;
        mPendingRequestId = Integer.valueOf(EsService.sendOutOfBoxRequest(getActivity(), mAccount, mobileoutofboxrequest));
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private final class OobEsServiceListener extends EsServiceListener
    {

        public final void onOobRequestComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

    }

}
