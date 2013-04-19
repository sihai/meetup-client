// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.android.network;

import java.io.IOException;

public final class NetworkException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7535893723248003594L;

	public NetworkException(String s) {
		super(s);
	}

	public NetworkException(String s, Throwable throwable) {
		super(s);
		initCause(throwable);
	}
}
