/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
 * @author sihai
 *
 */
public class UrlGatewayActivity extends EsUrlGatewayActivity {

	public UrlGatewayActivity()
    {
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(!isFinishing())
            if(mRequestType == 0)
                redirectToBrowser();
            else
            if(isReadyToRedirect())
            {
                redirect();
            } else
            {
                Intent intent = getIntent();
                intent.setComponent(new ComponentName(this, UrlGatewayLoaderActivity.class));
                intent.setFlags(0x42800000);
                startActivity(intent);
                finish();
            }
    }
}
