/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.c2dm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.hangout.HangoutRingingActivity;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class C2DMReceiver extends BroadcastReceiver {

	public C2DMReceiver()
    {
    }

    public static void requestC2DMRegistrationId(Context context)
    {
        if(EsLog.isLoggable("C2DMReceiver", 4))
            Log.i("C2DMReceiver", "requestC2DMReg");
        Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
        intent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        intent.putExtra("sender", "bunch.eng.c2dm@gmail.com");
        context.startService(intent);
    }

    public static void unregisterC2DM(Context context)
    {
        if(EsLog.isLoggable("C2DMReceiver", 4))
            Log.i("C2DMReceiver", "unregisterC2DMReg");
        Intent intent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        intent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        intent.putExtra("sender", "bunch.eng.c2dm@gmail.com");
        context.startService(intent);
    }

    public void onReceive(Context context, Intent intent)
    {
    	String s = intent.getStringExtra("focus_account_id");
        EsAccount esaccount = EsService.getActiveAccount(context);
        if(EsLog.isLoggable("C2DMReceiver", 4))
            Log.i("C2DMReceiver", (new StringBuilder("onReceive ")).append(intent).append(" ").append(intent.getAction()).toString());
        if(!intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
            if(intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
                if(esaccount != null && esaccount.hasGaiaId())
                {
                    String s1;
                    if(s.startsWith("g:"))
                        s1 = s.substring(2);
                    else
                        s1 = s;
                    if(!esaccount.isMyGaiaId(s1))
                    	if(EsLog.isLoggable("C2DMReceiver", 4))
                            Log.i("C2DMReceiver", "c2dm message for wrong account");
                }
            } else {
            	String s2 = intent.getStringExtra("type");
                if(s2 == null)
                {
                    String s3 = intent.getStringExtra("message_timestamp");
                    String s4 = intent.getStringExtra("conversation_id");
                    if(!TextUtils.isEmpty(s3))
                        try
                        {
                            long l = Long.valueOf(s3).longValue();
                            EsAnalytics.recordActionEvent(context, esaccount, OzActions.C2DM_MESSAGE_RECEIVED, OzViews.NOTIFICATIONS_WIDGET, l);
                        }
                        catch(NumberFormatException numberformatexception)
                        {
                            Log.e("C2DMReceiver", (new StringBuilder("C2DM timestamp value is invalid ")).append(s3).toString());
                        }
                    if(EsLog.isLoggable("C2DMReceiver", 4))
                        Log.i("C2DMReceiver", (new StringBuilder("handleMessage ")).append(s4).append(" ").append(s3).toString());
                    RealTimeChatService.connectIfLoggedIn(context, s, s4, s3);
                } else
                if(s2.equals("hangout"))
                    HangoutRingingActivity.onC2DMReceive(context, esaccount, intent);
            }
        } else {
        	String s5 = intent.getStringExtra("registration_id");
            if(intent.getStringExtra("error") != null)
            {
                if(EsLog.isLoggable("C2DMReceiver", 4))
                    Log.i("C2DMReceiver", "c2dm error");
                RealTimeChatService.handleC2DMRegistrationError(context, intent.getStringExtra("error"));
            } else
            if(intent.getStringExtra("unregistered") != null)
            {
                if(EsLog.isLoggable("C2DMReceiver", 4))
                    Log.i("C2DMReceiver", "c2dm unregistration");
                RealTimeChatService.handleC2DMUnregistration(context);
            } else
            if(s5 != null)
            {
                if(EsLog.isLoggable("C2DMReceiver", 4))
                    Log.i("C2DMReceiver", "c2dm registration");
                RealTimeChatService.handleC2DMRegistration(context, s5);
            }
        }
        
        setResultCode(-1);
    }
}
