/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Looper;

import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class ShakeDetector implements SensorEventListener {
	
	private static volatile ShakeDetector mShakeDetector;
	private float mCurrentAcceleration;
	private ConcurrentLinkedQueue mEventListeners;
	private boolean mIsSensorRegistered;
	private SensorManager mSensorManager;
	private long mShakeStartTime;
	    
	private ShakeDetector(Context context)
    {
        mEventListeners = new ConcurrentLinkedQueue();
        mSensorManager = (SensorManager)context.getSystemService("sensor");
        mCurrentAcceleration = 0.0F;
    }

	
	public static synchronized ShakeDetector getInstance(Context context)
    {
        if(!Property.ENABLE_SHAKE_GLOBAL_ACTION.getBoolean()) {
        	return null;
        } else {
        	if(mShakeDetector != null) {
        		return mShakeDetector;
        	}
        	
        	mShakeDetector = new ShakeDetector(context);
        	return mShakeDetector;
        }
    }

    public final void addEventListener(ShakeEventListener shakeeventlistener)
    {
        if(Looper.getMainLooper().getThread() != Thread.currentThread())
        {
            throw new RuntimeException("startCommand must be called on the UI thread");
        } else
        {
            mEventListeners.add(shakeeventlistener);
            return;
        }
    }

    public final void onAccuracyChanged(Sensor sensor, int i)
    {
    }

    public final void onSensorChanged(SensorEvent sensorevent)
    {
        float f = sensorevent.values[0];
        float f1 = sensorevent.values[1];
        float f2 = sensorevent.values[2];
        mCurrentAcceleration = 0.2F * Math.abs((float)Math.sqrt(f * f + f1 * f1 + f2 * f2) - 9.80665F) + 0.8F * mCurrentAcceleration;
        if(mCurrentAcceleration < 8F) {
        	mShakeStartTime = 0L;
        	return;
        }
        
        long l = System.currentTimeMillis();
        if(mShakeStartTime == 0L)
            mShakeStartTime = l;
        else
        if(l - mShakeStartTime >= 250L)
        {
            stop();
            for(Iterator iterator = mEventListeners.iterator(); iterator.hasNext(); ((ShakeEventListener)iterator.next()).onShakeDetected());
            start();
        }
    }

    public final void removeEventListener(ShakeEventListener shakeeventlistener)
    {
        if(Looper.getMainLooper().getThread() != Thread.currentThread())
        {
            throw new RuntimeException("startCommand must be called on the UI thread");
        } else
        {
            mEventListeners.remove(shakeeventlistener);
            return;
        }
    }

    public final boolean start()
    {
        if(Looper.getMainLooper().getThread() != Thread.currentThread())
            throw new RuntimeException("startCommand must be called on the UI thread");
        if(mSensorManager != null && !mIsSensorRegistered)
            mIsSensorRegistered = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(1), 3);
        return mIsSensorRegistered;
    }

    public final boolean stop()
    {
        if(Looper.getMainLooper().getThread() != Thread.currentThread())
            throw new RuntimeException("startCommand must be called on the UI thread");
        SensorManager sensormanager = mSensorManager;
        boolean flag = false;
        if(sensormanager != null)
        {
            boolean flag1 = mIsSensorRegistered;
            flag = false;
            if(flag1)
            {
                mSensorManager.unregisterListener(this);
                mIsSensorRegistered = false;
                mCurrentAcceleration = 0.0F;
                flag = true;
            }
        }
        return flag;
    }
    
    public static interface ShakeEventListener
    {

        public abstract void onShakeDetected();
    }
}
