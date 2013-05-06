/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.DataNotificationSettingsDeliveryOption;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.NotificationSetting;
import com.galaxy.meetup.client.android.content.NotificationSettingsCategory;
import com.galaxy.meetup.client.android.content.NotificationSettingsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.DataMobileSettings;
import com.galaxy.meetup.server.client.domain.DataNotificationSettings;
import com.galaxy.meetup.server.client.domain.DataNotificationSettingsFetchParams;
import com.galaxy.meetup.server.client.domain.DataNotificationSettingsNotificationsSettingsCategoryInfo;
import com.galaxy.meetup.server.client.domain.request.SettingsFetchRequest;
import com.galaxy.meetup.server.client.domain.response.SettingsFetchResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetNotificationSettingsOperation extends PlusiOperation {

	private NotificationSettingsData mNotificationSettings;
	
	public GetNotificationSettingsOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "settingsfetch", intent, operationlistener, SettingsFetchResponse.class);
    }

    public final NotificationSettingsData getNotificationSettings()
    {
        return mNotificationSettings;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        int i = 0;
        SettingsFetchResponse settingsfetchresponse = (SettingsFetchResponse)response;
        if(settingsfetchresponse.settings == null || settingsfetchresponse.settings.notificationSettings == null)
            throw new ProtocolException("Notification settings missing from response");
        DataNotificationSettings datanotificationsettings = settingsfetchresponse.settings.notificationSettings;
        DataMobileSettings datamobilesettings = settingsfetchresponse.settings.mobileSettings;
        if(datanotificationsettings.deliveryOption == null || datanotificationsettings.categoryInfo == null || datamobilesettings == null || datamobilesettings.mobileNotificationType == null)
            throw new ProtocolException("Invalid notification settings response");
        HashMap hashmap = new HashMap();
        List list = datanotificationsettings.deliveryOption;
        int j = list.size();
        for(int k = 0; k < j; k++)
        {
            DataNotificationSettingsDeliveryOption datanotificationsettingsdeliveryoption = (DataNotificationSettingsDeliveryOption)list.get(k);
            String s = datanotificationsettingsdeliveryoption.category;
            if(TextUtils.isEmpty(s) || TextUtils.isEmpty(datanotificationsettingsdeliveryoption.description))
                continue;
            Object obj = (List)hashmap.get(s);
            if(obj == null)
                obj = new ArrayList();
            ((List) (obj)).add(new NotificationSetting(datanotificationsettingsdeliveryoption));
            hashmap.put(s, obj);
        }

        List list1 = datanotificationsettings.categoryInfo;
        int l = list1.size();
        ArrayList arraylist = new ArrayList(l);
        for(; i < l; i++)
        {
            DataNotificationSettingsNotificationsSettingsCategoryInfo datanotificationsettingsnotificationssettingscategoryinfo = (DataNotificationSettingsNotificationsSettingsCategoryInfo)list1.get(i);
            if(!TextUtils.isEmpty(datanotificationsettingsnotificationssettingscategoryinfo.description))
            {
                List list2 = (List)hashmap.get(datanotificationsettingsnotificationssettingscategoryinfo.category);
                arraylist.add(new NotificationSettingsCategory(datanotificationsettingsnotificationssettingscategoryinfo.description, list2));
            }
        }

        mNotificationSettings = new NotificationSettingsData(datanotificationsettings.emailAddress, datamobilesettings.mobileNotificationType, arraylist);
    }

    protected final Request populateRequest()
    {
        SettingsFetchRequest settingsfetchrequest = new SettingsFetchRequest();
        DataNotificationSettingsFetchParams datanotificationsettingsfetchparams = new DataNotificationSettingsFetchParams();
        datanotificationsettingsfetchparams.fetchSettingsDescription = Boolean.valueOf(true);
        datanotificationsettingsfetchparams.fetchPlusPageSettings = Boolean.valueOf(false);
        datanotificationsettingsfetchparams.fetchAlternateEmailAddress = Boolean.valueOf(false);
        datanotificationsettingsfetchparams.fetchWhoCanNotifyMe = Boolean.valueOf(false);
        datanotificationsettingsfetchparams.typeGroupToFetch = GetNotificationsOperation.TYPE_GROUP_TO_FETCH;
        settingsfetchrequest.notificationSettingsFetchParams = datanotificationsettingsfetchparams;
        return settingsfetchrequest;
    }

}
