/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;

/**
 * 
 * @author sihai
 * 
 */
public class AccountSelectionActivity extends BaseAccountSelectionActivity {

	public AccountSelectionActivity() {
	}

	protected final void onAccountSet(
			MobileOutOfBoxResponse mobileoutofboxresponse, EsAccount esaccount,
			AccountSettingsData accountsettingsdata) {
		startActivity(Intents.getHomeOobActivityIntent(this, esaccount,
				(Intent) getIntent().getParcelableExtra("intent"),
				mobileoutofboxresponse, accountsettingsdata));
		finish();
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		showAccountSelectionOrUpgradeAccount(bundle);
	}
}
