/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.galaxy.meetup.client.android.Intents;

/**
 * 
 * @author sihai
 *
 */
public class ConversationListActivity extends Activity {

	public ConversationListActivity()
    {
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Intent intent = Intents.getMessengerActivityIntent(this, null);
        intent.addFlags(0x12010000);
        if(!ConversationActivity.hasInstance() && !NewConversationActivity.hasInstance())
            intent.addFlags(0x4000000);
        startActivity(intent);
        finish();
    }
}
