/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout.crash;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * 
 * @author sihai
 *
 */
public class CrashTriggerActivity extends Activity {

	private class NativeCrashException extends RuntimeException
    {

        private NativeCrashException()
        {
        }

        NativeCrashException(byte byte0)
        {
            this();
        }
    }

    private class JavaCrashOnNativeThreadException extends RuntimeException
    {

        JavaCrashOnNativeThreadException(String s)
        {
            super(s);
        }
    }


    public CrashTriggerActivity()
    {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {

            public final void run()
            {
                Bundle bundle = CrashTriggerActivity.this.getIntent().getExtras();
            }

        }, 1000L);
    }
}
