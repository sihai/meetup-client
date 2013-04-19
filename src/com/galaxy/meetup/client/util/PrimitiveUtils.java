/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

/**
 * 
 * @author sihai
 *
 */
public class PrimitiveUtils {

	public static boolean safeBoolean(Boolean boolean1) {
		boolean flag;
		if (boolean1 == null)
			flag = false;
		else
			flag = boolean1.booleanValue();
		return flag;
	}

	public static double safeDouble(Double double1) {
		double d;
		if (double1 == null)
			d = 0.0D;
		else
			d = double1.doubleValue();
		return d;
	}

	public static int safeInt(Integer integer) {
		int i;
		if (integer == null)
			i = 0;
		else
			i = integer.intValue();
		return i;
	}

	public static long safeLong(Long long1) {
		long l;
		if (long1 == null)
			l = 0L;
		else
			l = long1.longValue();
		return l;
	}
}
