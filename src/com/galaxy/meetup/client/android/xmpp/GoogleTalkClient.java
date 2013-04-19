/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.xmpp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.galaxy.meetup.client.android.AuthData;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public abstract class GoogleTalkClient {

	private boolean mActive;
    private final String mAddress;
    private final String mBackendAddress;
    private final Context mContext;
    private final boolean mDebugModeEnabled;
    private final EsAccount mEsAccount;
    private String mGoogleToken;
    private String mJabberId;
    private final String mResource;
    private Socket mSocket;
    private GoogleTalkThread mThread;
    private BufferedWriter mWriter;
    
    //======================================================================================
  	//							Inner class
  	//======================================================================================
    private final class GoogleTalkThread extends Thread {

    	private volatile boolean mConnected;

        public GoogleTalkThread()
        {
            super();
            mConnected = true;
        }
        
        public final void run() {
            try {
                mGoogleToken = AuthData.getAuthToken(mContext, mEsAccount.getName(), "webupdates");
            } catch(Exception exception) {
                disconnect(3);
                if(EsLog.isLoggable("GoogleTalkClient", 3))
                    Log.d("GoogleTalkClient", "authentication failed", exception);
                exception.printStackTrace();
            }
            if(null == mGoogleToken) {
            	if(EsLog.isLoggable("GoogleTalkClient", 3))
                    Log.d("GoogleTalkClient", "authentication failed, null token");
                disconnect(3);
                return;
            }
            
            try {
	            if(EsLog.isLoggable("GoogleTalkClient", 3))
	                Log.d("GoogleTalkClient", (new StringBuilder("token ")).append(mGoogleToken).toString());
	            mSocket = new Socket("talk.google.com", 5222);
	            resetWriter();
	            if(!mConnected) { 
	            	if(EsLog.isLoggable("GoogleTalkClient", 3))
	                    Log.d("GoogleTalkClient", "thread finished");
	            	return;
	            } else {
	            	MessageReader messagereader = new MessageReader(mSocket.getInputStream(), mDebugModeEnabled);
	            	while(mConnected) {
		            	switch(messagereader.read()) {
			            	case UNEXPECTED_FEATURES:
			            		if(EsLog.isLoggable("GoogleTalkClient", 4))
			                        Log.i("GoogleTalkClient", "unexpected features");
			                    disconnect(5);
			            		break;
			            	case END_OF_STREAM:
			            		if(EsLog.isLoggable("GoogleTalkClient", 4))
			                        Log.i("GoogleTalkClient", "end of stream");
			                    disconnect(4);
			            		break;
			            	case TLS_REQUIRED:
			            		if(EsLog.isLoggable("GoogleTalkClient", 4))
			                        Log.i("GoogleTalkClient", "TLS required");
			                    write("<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>");
			            		break;
			            	case PROCEED_WITH_TLS:
			            		if(EsLog.isLoggable("GoogleTalkClient", 4))
			                        Log.i("GoogleTalkClient", "Proceed with TLS");
			                    GoogleTalkClient.access$700(GoogleTalkClient.this);
			            		break;
			            	case AUTHENTICATION_REQUIRED:
			            		if(EsLog.isLoggable("GoogleTalkClient", 4))
			                        Log.i("GoogleTalkClient", "Authenticated required");
			                    write(Commands.authenticate(mGoogleToken));
			            		break;
			            	case AUTHENTICATION_SUCCEEDED:
			            		break;
			            	case AUTHENTICATION_FAILED:
			            		if(EsLog.isLoggable("GoogleTalkClient", 4))
			                        Log.i("GoogleTalkClient", "Authentication failed");
			                    AuthData.invalidateAuthToken(mContext, mEsAccount.getName(), "webupdates");
			                    mGoogleToken = null;
			                    disconnect(3);
			            		break;
			            	case STREAM_READY:
			            		if(EsLog.isLoggable("GoogleTalkClient", 4))
			                        Log.i("GoogleTalkClient", "Authenticated successfully");
			            		break;
			            	case JID_AVAILABLE:
			            		if(EsLog.isLoggable("GoogleTalkClient", 4))
			                        Log.i("GoogleTalkClient", "jid available");
			                    mJabberId = messagereader.getEventData();
			            		break;
			            	case DATA_RECEIVED:
			            		onMessageReceived(Base64.decode(messagereader.getEventData(), 0));
			            		break;
		            		default:
		            			break;
		            	}
	            	}
	        		if(EsLog.isLoggable("GoogleTalkClient", 3))
	                    Log.d("GoogleTalkClient", "thread finished");
	        		return;
	            }
            } catch (IOException e) {
            	if(EsLog.isLoggable("GoogleTalkClient", 3))
                    Log.d("GoogleTalkClient", "io exception");
                disconnect(3);
            } catch (AuthenticatorException e) {
            	if(EsLog.isLoggable("GoogleTalkClient", 3))
                    Log.d("GoogleTalkClient", "authenticator exception");
                disconnect(3);
            } catch (OperationCanceledException e) {
            	if(EsLog.isLoggable("GoogleTalkClient", 3))
                    Log.d("GoogleTalkClient", "operation canceled exception");
                disconnect(3);
            }
        }

        public final void setDisconnected()
        {
            mConnected = false;
        }

    }
    
    public GoogleTalkClient(EsAccount esaccount, Context context, String s, String s1, String s2) {
        mEsAccount = esaccount;
        mContext = context;
        mAddress = s;
        mBackendAddress = s1;
        mResource = s2;
        mActive = false;
        Resources resources = context.getResources();
        mDebugModeEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(resources.getString(R.string.realtimechat_notify_setting_key), resources.getBoolean(R.bool.realtimechat_notify_setting_default_value));
    }

    private void resetWriter() throws IOException {
        mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
    }

    public final boolean active() {
        return mActive;
    }

    public final synchronized void connect() {
        mActive = true;
        if(mThread == null) {
            mThread = new GoogleTalkThread();
            mThread.start();
        }
    }

    public void disconnect() {
        disconnect(1);
    }

    public final synchronized void disconnect(int i) {
        if(EsLog.isLoggable("GoogleTalkClient", 3))
            Log.d("GoogleTalkClient", (new StringBuilder("disconnect ")).append(i).toString());
        mActive = false;
        if(null != mThread) {
        	mThread.setDisconnected();
        }
        if(null != mSocket) {
	        try {
	            mSocket.close();
	        }
	        catch(IOException ioexception) { }
        }
        mSocket = null;
        mThread = null;
        onDisconnected(i);
    }

    public final EsAccount getAccount() {
        return mEsAccount;
    }

    public final Context getContext() {
        return mContext;
    }

    protected abstract void onConnected();

    protected abstract void onDisconnected(int i);

    protected abstract void onMessageReceived(byte abyte0[]);

    public final boolean sendMessage(byte abyte0[]) {
        String s = mJabberId;
        boolean flag = false;
        if(s != null) {
            String s1 = mJabberId.split("/")[0];
            String s2 = mJabberId;
            String s3 = mAddress;
            String s4 = Base64.encodeToString(abyte0, 0);
            flag = write((new StringBuilder("<message to='")).append(s1).append("' from='").append(s2).append("' type='headline'><push xmlns='google:push' channel='realtime-chat'><recipient to='").append(s3).append("' data=''/><data>").append(s4).append("</data></push></message>").toString());
        }
        return flag;
    }

    public final synchronized boolean write(String s) {
    	try {
	        mWriter.write(s);
	        mWriter.flush();
	        return true;
    	} catch (IOException e) {
    		if(EsLog.isLoggable("GoogleTalkClient", 4))
                Log.i("GoogleTalkClient", "IOException while writing message");
            disconnect(6);
            return false;
    	}
    }
    
    static void access$700(GoogleTalkClient googletalkclient) {
    	try {
	        SSLContext sslcontext = SSLContext.getInstance("TLS");
	        sslcontext.init(null, null, null);
	        Socket socket = sslcontext.getSocketFactory().createSocket(googletalkclient.mSocket, googletalkclient.mSocket.getInetAddress().getHostName(), googletalkclient.mSocket.getPort(), true);
	        googletalkclient.mSocket = socket;
	        googletalkclient.mSocket.setKeepAlive(true);
	        googletalkclient.mSocket.setSoTimeout(60000);
	        ((SSLSocket)socket).startHandshake();
	        googletalkclient.resetWriter();
    	} catch (Exception exception) {
    		if(EsLog.isLoggable("GoogleTalkClient", 5))
                Log.w("GoogleTalkClient", "Exception while starting TLS");
            googletalkclient.disconnect(2);
    	}
    }
}
