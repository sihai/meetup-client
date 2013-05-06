/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.MobileSettingsUser;
import com.galaxy.meetup.server.client.domain.MobileSettingsUserInfo;
import com.galaxy.meetup.server.client.domain.response.GetMobileSettingsResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetSettingsOperation extends PlusiOperation {

	private AccountSettingsData mSettings;
    private boolean mSetupAccount;
    
    public GetSettingsOperation(Context context, EsAccount esaccount, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getmobilesettings", null, null, GetMobileSettingsResponse.class);
        mSetupAccount = flag;
    }

    public final AccountSettingsData getAccountSettings()
    {
        return mSettings;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        MobileSettingsUser mobilesettingsuser;
        MobileSettingsUserInfo mobilesettingsuserinfo;
        String s;
        String s1;
        boolean flag;
        GetMobileSettingsResponse getmobilesettingsresponse = (GetMobileSettingsResponse)response;
        if(getmobilesettingsresponse.user == null || getmobilesettingsresponse.user.info == null || TextUtils.isEmpty(getmobilesettingsresponse.user.info.obfuscatedGaiaId))
        {
            Log.e("HttpTransaction", "Settings response missing gaid ID");
            throw new ProtocolException("Settings response missing gaid ID");
        }
        mobilesettingsuser = getmobilesettingsresponse.user;
        mobilesettingsuserinfo = mobilesettingsuser.info;
        s = mobilesettingsuserinfo.obfuscatedGaiaId;
        s1 = mobilesettingsuserinfo.displayName;
        if(mobilesettingsuser.isChild != null && mobilesettingsuser.isChild.booleanValue())
            flag = true;
        else
            flag = false;
        
        EsAccount esaccount;
        AccountSettingsData accountsettingsdata;
        if(mSetupAccount) {
        	if(mAccount.isPlusPage() || mobilesettingsuser.plusPageInfo == null || mobilesettingsuser.plusPageInfo.size() <= 0) {
                boolean flag1;
                if(mAccount.isPlusPage() || mobilesettingsuser.isPlusPage != null && mobilesettingsuser.isPlusPage.booleanValue())
                    flag1 = true;
                else
                    flag1 = false;
                esaccount = EsAccountsData.insertAccount(mContext, s, mAccount.getName(), s1, flag, flag1);
                EsAccountsData.activateAccount(mContext, esaccount, mobilesettingsuserinfo.photoUrl);
        	} else { 
        		mSettings = new AccountSettingsData(getmobilesettingsresponse);
        		return;
        	}
        } else {
        	esaccount = mAccount;
            EsAccountsData.updateAccount(mContext, mAccount, s, s1, flag);
        }
        
        accountsettingsdata = new AccountSettingsData(getmobilesettingsresponse);
        EsAccountsData.saveServerSettings(mContext, esaccount, accountsettingsdata);
        return;
    }

    public final boolean hasPlusPages()
    {
        boolean flag;
        if(mSettings != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final Request populateRequest()
    {
    	return new Request();
    }
}
