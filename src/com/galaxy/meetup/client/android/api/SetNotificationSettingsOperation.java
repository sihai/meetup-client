/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.NotificationSettingsCategory;
import com.galaxy.meetup.client.android.content.NotificationSettingsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.DataMobileSettings;
import com.galaxy.meetup.server.client.domain.DataNotificationSettings;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.OzDataSettings;
import com.galaxy.meetup.server.client.domain.request.SettingsSetRequest;
import com.galaxy.meetup.server.client.domain.response.SettingsSetResponse;

/**
 * 
 * @author sihai
 *
 */
public class SetNotificationSettingsOperation extends PlusiOperation {

	private NotificationSettingsData mSettings;
	 
	public SetNotificationSettingsOperation(Context context, EsAccount esaccount, NotificationSettingsData notificationsettingsdata, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "settingsset", intent, operationlistener, SettingsSetResponse.class);
        mSettings = notificationsettingsdata;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        SettingsSetResponse settingssetresponse = (SettingsSetResponse)genericjson;
        if(settingssetresponse.success != null && !settingssetresponse.success.booleanValue())
            throw new ProtocolException("SettingsSetRequest failed");
        else
            return;
    }

    protected final GenericJson populateRequest()
    {
        SettingsSetRequest settingssetrequest = new SettingsSetRequest();
        ArrayList arraylist = new ArrayList();
        int i = mSettings.getCategoriesCount();
        for(int j = 0; j < i; j++)
        {
            NotificationSettingsCategory notificationsettingscategory = mSettings.getCategory(j);
            int k = notificationsettingscategory.getSettingsCount();
            for(int l = 0; l < k; l++)
                arraylist.add(notificationsettingscategory.getSetting(l).getDeliveryOption());

        }

        DataNotificationSettings datanotificationsettings = new DataNotificationSettings();
        datanotificationsettings.deliveryOption = arraylist;
        datanotificationsettings.emailAddress = mSettings.getEmailAddress();
        DataMobileSettings datamobilesettings = new DataMobileSettings();
        datamobilesettings.mobileNotificationType = "PUSH";
        OzDataSettings ozdatasettings = new OzDataSettings();
        ozdatasettings.notificationSettings = datanotificationsettings;
        ozdatasettings.mobileSettings = datamobilesettings;
        settingssetrequest.settings = ozdatasettings;
        return settingssetrequest;
    }

}
