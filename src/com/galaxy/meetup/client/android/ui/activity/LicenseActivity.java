/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class LicenseActivity extends Activity {

	public LicenseActivity()
    {
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.license_activity);
        ((WebView)findViewById(R.id.content)).loadUrl("file:///android_asset/licenses.html");
    }
}
