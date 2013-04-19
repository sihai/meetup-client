// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.util;

import android.util.Log;

// Referenced classes of package com.google.android.apps.plus.util:
//            Property

public final class EsLog {

	public static void doWriteToLog(int i, String s, String s1) {
		int j = 0;
		for (int k = s1.indexOf('\n', 0); k != -1; k = s1.indexOf('\n', j)) {
			Log.println(i, s, s1.substring(j, k));
			j = k + 1;
		}

		Log.println(i, s, s1.substring(j));
	}

	public static boolean isLoggable(String s, int i) {
		boolean flag;
		if ((ENABLE_DOGFOOD_FEATURES || i == 6) && Log.isLoggable(s, i))
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static void writeToLog(int i, String s, String s1) {
		if (isLoggable(s, i))
			doWriteToLog(i, s, s1);
	}

	public static final boolean ENABLE_DOGFOOD_FEATURES;

	static {
		ENABLE_DOGFOOD_FEATURES = Boolean
				.parseBoolean(Property.ENABLE_DOGFOOD_FEATURES.get());
	}
}
