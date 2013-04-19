/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.OzServerException;
import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.AccountsListFragment;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;

/**
 * 
 * @author sihai
 *
 */
public abstract class BaseAccountSelectionActivity extends EsFragmentActivity implements
		OnCancelListener, OnClickListener {

	private AccountsAdder mAccountsAdder;
    private AccountsListFragment mAccountsListFragment;
    private Integer mAddAccountPendingRequestId;
    private final EsServiceListener mServiceListener = new ServiceListener();
    private boolean mShowOnAttach;
    private Integer mUpdateAccountIdPendingRequestId;
    
	public BaseAccountSelectionActivity() {
		mAccountsAdder = new AccountsAdder() {

			public final void addAccount(String s) {
				mAddAccountPendingRequestId = Integer.valueOf(EsService
						.addAccount(BaseAccountSelectionActivity.this,
								new EsAccount(s, null, null, false, false, -1),
								getUpgradeOrigin()));
				showDialog(10);
			}
		};
	}
    
	protected abstract void onAccountSet(
			MobileOutOfBoxResponse mobileoutofboxresponse, EsAccount esaccount,
			AccountSettingsData accountsettingsdata);

	private void chooseAccountManually() {
		Intent intent;
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			Bundle bundle = new Bundle();
			bundle.putBoolean("allowSkip", false);
			bundle.putCharSequence("introMessage",
					getString(R.string.create_account_prompt));
			intent = AccountManager.newChooseAccountIntent(null, null,
					new String[] { AccountsUtil.ACCOUNT_TYPE }, true, null, "webupdates",
					null, bundle);
		} else {
			intent = null;
		}
		startActivityForResult(intent, 0);
	}

    private void handleError(ServiceResult serviceresult) {
        Exception exception = serviceresult.getException();
        Bundle bundle = new Bundle();
        if(!(exception instanceof OzServerException)) {
        	bundle.putString("error_title", getString(R.string.signup_title_no_connection));
            bundle.putString("error_message", getString(R.string.signup_error_network));
            showDialog(0, bundle);
        } else {
        	switch(((OzServerException)exception).getErrorCode()) {
	        	case 1:
	        		 bundle.putString("error_message", getString(R.string.signup_authentication_error));
	        	        showDialog(2, bundle);
	        	        break;
	        	case 10:
	        		bundle.putString("error_message", getString(R.string.signup_required_update_available));
	                showDialog(1, bundle);
	                break;
	        	case 12:
	        		bundle.putString("error_message", getString(R.string.signup_profile_error));
	                showDialog(4, bundle);
	                break;
	        	case 14:
	        	case 15:
	        		bundle.putString("error_title", getString(R.string.signup_title_mobile_not_available));
	                bundle.putString("error_message", getString(R.string.signup_text_mobile_not_available));
	                showDialog(3, bundle);
	                break;
        		default:
        			bundle.putString("error_title", getString(R.string.signup_title_no_connection));
    		        bundle.putString("error_message", getString(R.string.signup_error_network));
    		        showDialog(0, bundle);
    		        break;
        	}
        }
        return;
    }

	private void handleResponse(int i, EsAccount esaccount) {
		if (esaccount != null)
			onAccountSet(EsService.removeIncompleteOutOfBoxResponse(i),
					esaccount, EsService.removeAccountSettingsResponse(i));
	}

    protected final EsAccount getAccount()
    {
        return null;
    }

    protected String getUpgradeOrigin()
    {
        return "DEFAULT";
    }

    public OzViews getViewForLogging()
    {
        return OzViews.UNKNOWN;
    }

    public final void handleUpgradeFailure()
    {
        Bundle bundle = new Bundle();
        bundle.putString("error_title", getString(R.string.signup_title_no_connection));
        bundle.putString("error_message", getString(R.string.signup_error_network));
        showDialog(5, bundle);
    }

    public final void handleUpgradeSuccess(EsAccount esaccount)
    {
        Intent intent = (Intent)getIntent().getParcelableExtra("intent");
        if(intent == null)
            intent = Intents.getStreamActivityIntent(this, esaccount);
        startActivity(intent);
        finish();
    }

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		mAccountsAdder.addAccount("sihai");
		/*if (0 != requestCode) {
			return;
		}
		if (resultCode == -1 && intent != null) {
			String s = intent.getStringExtra("authAccount");
			mAccountsAdder.addAccount(s);
		} else {
			setResult(0);
			finish();
		}*/
	}

	@Override
    public final void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
        if(fragment instanceof AccountsListFragment)
        {
            mAccountsListFragment = (AccountsListFragment)fragment;
            mAccountsListFragment.setAccountsAdder(mAccountsAdder);
            if(mShowOnAttach)
            {
                mAccountsListFragment.showList();
                mShowOnAttach = false;
            }
        }
    }

    public void onCancel(DialogInterface dialoginterface)
    {
        showAccountList();
    }

    public void onClick(DialogInterface dialoginterface, int i)
    {
        showAccountList();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            if(bundle.containsKey("aa_reqid"))
                mAddAccountPendingRequestId = Integer.valueOf(bundle.getInt("aa_reqid"));
            else
                mAddAccountPendingRequestId = null;
            if(bundle.containsKey("ua_reqid"))
                mUpdateAccountIdPendingRequestId = Integer.valueOf(bundle.getInt("ua_reqid"));
            else
                mUpdateAccountIdPendingRequestId = null;
        }
    }

    public Dialog onCreateDialog(int i, Bundle bundle) {
        String errorTitle;
        String errorMsg;
        if(bundle == null)
            errorTitle = null;
        else
            errorTitle = bundle.getString("error_title");
        if(bundle == null)
            errorMsg = null;
        else
            errorMsg = bundle.getString("error_message");
        
        Dialog dialog = null;
        switch(i) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        	android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
            builder1.setTitle(errorTitle);
            builder1.setMessage(errorMsg);
            builder1.setNeutralButton(R.string.ok, this);
            builder1.setOnCancelListener(this);
            dialog = builder1.create();
        	break;
        case 5:
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(errorTitle);
            builder.setMessage(errorMsg);
            builder.setNeutralButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

                public final void onClick(DialogInterface dialoginterface, int j)
                {
                    finish();
                }
            });
            builder.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

                public final void onCancel(DialogInterface dialoginterface)
                {
                    finish();
                }
            });
            dialog = builder.create();
        	break;
        case 6:
        	break;
        case 7:
        	break;
        case 8:
        	break;
        case 9:
        	break;
        case 10:
        	dialog = new ProgressDialog(this);
             ((ProgressDialog) (dialog)).setMessage(getString(R.string.signup_signing_in));
             ((ProgressDialog) (dialog)).setProgressStyle(0);
             ((ProgressDialog) (dialog)).setCancelable(false);
        	break;
        case 11:
        	dialog = new ProgressDialog(this);
            ((ProgressDialog) (dialog)).setMessage(getString(R.string.signup_upgrading));
            ((ProgressDialog) (dialog)).setProgressStyle(0);
            ((ProgressDialog) (dialog)).setCancelable(false);
        	break;
    	default:
    		break;
        }
       return dialog;
    }

    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if(android.os.Build.VERSION.SDK_INT < 14)
            setIntent(intent);
    }

    public void onPause()
    {
        EsService.unregisterListener(mServiceListener);
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mAddAccountPendingRequestId != null && !EsService.isRequestPending(mAddAccountPendingRequestId.intValue()))
        {
            dismissDialog(10);
            if(android.os.Build.VERSION.SDK_INT < 14)
                mAccountsListFragment.showList();
            ServiceResult serviceresult1 = EsService.removeResult(mAddAccountPendingRequestId.intValue());
            ServiceResult serviceresult;
            if(serviceresult1 != null)
                if(!serviceresult1.hasError() || EsService.isOutOfBoxError(serviceresult1.getException()))
                    handleResponse(mAddAccountPendingRequestId.intValue(), EsService.getActiveAccount(this));
                else
                    handleError(serviceresult1);
            mAddAccountPendingRequestId = null;
        }
        if(mUpdateAccountIdPendingRequestId != null && !EsService.isRequestPending(mUpdateAccountIdPendingRequestId.intValue()))
        {
            dismissDialog(11);
            ServiceResult serviceresult = EsService.removeResult(mUpdateAccountIdPendingRequestId.intValue());
            if(serviceresult != null)
                if(!serviceresult.hasError())
                    handleUpgradeSuccess(EsService.getActiveAccount(this));
                else
                    handleUpgradeFailure();
            mUpdateAccountIdPendingRequestId = null;
        }
        if(android.os.Build.VERSION.SDK_INT < 14)
            overridePendingTransition(0, 0);
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mAddAccountPendingRequestId != null)
            bundle.putInt("aa_reqid", mAddAccountPendingRequestId.intValue());
        if(mUpdateAccountIdPendingRequestId != null)
            bundle.putInt("ua_reqid", mUpdateAccountIdPendingRequestId.intValue());
    }

    protected final void showAccountList()
    {
        if(android.os.Build.VERSION.SDK_INT < 14)
            mAccountsListFragment.showList();
        else
            chooseAccountManually();
    }

	protected final void showAccountSelectionOrUpgradeAccount(Bundle bundle) {

		EsAccount esaccount = EsAccountsData.getActiveAccountUnsafe(this);
		if (esaccount == null || !EsAccountsData.isAccountUpgradeRequired(this, esaccount)) {
			if (android.os.Build.VERSION.SDK_INT < 14) {
				setContentView(R.layout.account_selection_activity);
				if (android.os.Build.VERSION.SDK_INT < 11) {
					showTitlebar(false);
					setTitlebarTitle(getString(R.string.app_name));
				}
				((TextView) findViewById(R.id.info_title))
						.setText(R.string.signup_select_account_title);
				((TextView) findViewById(R.id.info_desc))
						.setText(R.string.signup_select_account_desc);
			} else if (mAddAccountPendingRequestId == null && bundle == null && android.os.Build.VERSION.SDK_INT >= 14)
				if (!EsAccountsData.hasLoggedInThePast(this)
						&& AccountManager.get(this).getAccountsByType(
								AccountsUtil.ACCOUNT_TYPE).length == 1) {
					if (EsAccountsData.hasVisitedOob(this)) {
						EsAccountsData.setHasVisitedOob(this, false);
						finish();
					} else {
						AccountsAdder accountsadder = mAccountsAdder;
						Account aaccount[] = AccountManager.get(this).getAccountsByType(AccountsUtil.ACCOUNT_TYPE);
						String s;
						if (aaccount.length > 0)
							s = aaccount[0].name;
						else
							s = null;
						accountsadder.addAccount(s);
					}
				} else {
					chooseAccountManually();
				}
			return;
		} else {
			mUpdateAccountIdPendingRequestId = Integer.valueOf(EsService.upgradeAccount(this, esaccount));
			showDialog(11);
			return;
		}
	}

	//===========================================================================
    //						Inner class
    //===========================================================================
	public static interface AccountsAdder {
        void addAccount(String s);
    }

	private final class ServiceListener extends EsServiceListener {

		public final void onAccountAdded(int i, EsAccount esaccount, ServiceResult serviceresult) {
			if (mAddAccountPendingRequestId != null
					&& mAddAccountPendingRequestId.equals(Integer.valueOf(i))) {
				dismissDialog(10);
				//if (android.os.Build.VERSION.SDK_INT < 14)
					if (mAccountsListFragment != null)
						mAccountsListFragment.showList();
					else
						mShowOnAttach = true;
				/*if (!serviceresult.hasError() || EsService.isOutOfBoxError(serviceresult.getException()))
					handleResponse(i, esaccount);
				else
					handleError(serviceresult);*/
				// FIXME
				handleResponse(i, esaccount);
				mAddAccountPendingRequestId = null;
			}
		}

		public final void onAccountUpgraded(int i, EsAccount esaccount,
				ServiceResult serviceresult) {
			if (mUpdateAccountIdPendingRequestId != null
					&& mUpdateAccountIdPendingRequestId.equals(Integer
							.valueOf(i))) {
				mUpdateAccountIdPendingRequestId = null;
				dismissDialog(11);
				if (!serviceresult.hasError())
					handleUpgradeSuccess(esaccount);
				else
					handleUpgradeFailure();
			}
		}

	}
}
