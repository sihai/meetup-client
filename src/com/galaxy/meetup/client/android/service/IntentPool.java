/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class IntentPool {

	private final List mIntentPool;
	
	public IntentPool(int size)
    {
		mIntentPool = new ArrayList(size);
    }

    public final synchronized Intent get(Context context, Class class1)
    {
        Intent intent = null;
        if(mIntentPool.size() > 0) {
            intent = (Intent)mIntentPool.remove(0);
            for(Iterator iterator = intent.getExtras().keySet().iterator(); iterator.hasNext(); intent.removeExtra((String)iterator.next()));
        } else {
        	intent = new Intent();
            intent.setComponent(new ComponentName(context, class1));
            intent.putExtra("from_pool", true);
            if(EsLog.isLoggable("IntentPool", 3))
                Log.d("IntentPool", (new StringBuilder("Pool enlarged: ")).append(mIntentPool.size()).toString());
        }
        return intent;
    }

    public final synchronized void put(Intent intent)
    {
        mIntentPool.add(intent);
    }
}

