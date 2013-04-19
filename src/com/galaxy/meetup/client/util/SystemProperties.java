/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.lang.reflect.Method;

/**
 * 
 * @author sihai
 *
 */
public class SystemProperties {

	public static String get(String s, String s1)
    {
        String s2;
        try
        {
            s2 = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] {
                String.class, String.class
            }).invoke(null, new Object[] {
                s, s1
            });
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            s2 = s1;
        }
        return s2;
    }

    public static int getInt(String s, int i)
    {
    	int j = 0;
    	try {
	        Class class1 = Class.forName("android.os.SystemProperties");
	        Class aclass[] = new Class[2];
	        aclass[0] = String.class;
	        aclass[1] = Integer.TYPE;
	        Method method = class1.getMethod("getInt", aclass);
	        Object aobj[] = new Object[2];
	        aobj[0] = s;
	        aobj[1] = Integer.valueOf(0);
	        return ((Integer)method.invoke(null, aobj)).intValue();
    	} catch (Exception exception) {
    		exception.printStackTrace();
    	}
    	
    	return j;
    }
}