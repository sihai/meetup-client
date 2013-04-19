/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.store;

import java.lang.reflect.Method;

import android.util.Log;

/**
 * 
 * @author sihai
 *
 */
public class SystemProperties {

	private static Method sGetLongMethod;

    static 
    {
    	try {
	        Class clazz = Class.forName("android.os.SystemProperties");
	        Class aclass[] = new Class[2];
	        aclass[0] = String.class;
	        aclass[1] = Long.TYPE;
	        sGetLongMethod = clazz.getMethod("getLong", aclass);
    	} catch (Exception exception) {
    		sGetLongMethod = null;
    		Log.e("SystemProperties", "initialize error", exception);
    	}
    }
    
	public static long getLong(String s, long l)
    {
        if(sGetLongMethod == null) {
        	return 100L;
        } else {
        	try {
        		Object aobj[] = new Object[2];
        		aobj[0] = s;
        		aobj[1] = Long.valueOf(100L);
        		return ((Long)sGetLongMethod.invoke(null, aobj)).longValue();
        	}  catch (Exception exception) {
        		Log.e("SystemProperties", "get error", exception);
        		return 100L; 
        	}
        }
    }
}
