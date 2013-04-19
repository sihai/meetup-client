/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.util.EsLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.panorama.PanoramaClient;

/**
 * 
 * @author sihai
 *
 */
public class PanoramaDetector extends HandlerThread  implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

	private static PanoramaDetector sDetector;
    private static final Map<Uri, Boolean> sPanoramaMap = new HashMap<Uri, Boolean>();
    private PanoramaClient mClient;
    private Context mContext;
    private Handler mHandler;
    private List<DetectionRequest> mQueue;
    private boolean mWaitingForConnection;
	
	private PanoramaDetector(Context context) {
		super("PanoramaDetector", 10);
		mWaitingForConnection = true;
		mQueue = new ArrayList<DetectionRequest>();
		mContext = context.getApplicationContext();
	}

	public static void clearCache() {
		sPanoramaMap.clear();
	}

    private synchronized void connect() {
        mWaitingForConnection = true;
        (new Handler(Looper.getMainLooper())).post(new Runnable() {

            public final void run()
            {
                mClient = new PanoramaClient(mContext, PanoramaDetector.this, PanoramaDetector.this);
                mClient.connect();
            }
            
        });
    }

	public static synchronized void detectPanorama(Context context,
			Resource resource, Uri uri) {
		Boolean boolean1 = (Boolean) sPanoramaMap.get(uri);
		if (null != boolean1) {
			if (boolean1.booleanValue())
				resource.deliverResourceType(2);
			return;
		}
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == 0) {
			if (sDetector == null) {
				PanoramaDetector panoramadetector = new PanoramaDetector(
						context);
				sDetector = panoramadetector;
				panoramadetector.start();
			}
			sDetector.detectPanorama(resource, uri);
		}
	}

    private synchronized void detectPanorama(Resource resource, Uri uri) {
        if(mHandler != null)
            mHandler.removeMessages(1);
        DetectionRequest detectionrequest = new DetectionRequest(resource, uri);
        if((mClient == null || !mClient.isConnected()) && !mWaitingForConnection)
            connect();
        if(!mWaitingForConnection) {
        	mHandler.sendMessage(mHandler.obtainMessage(0, detectionrequest));
        	return;
        }
        mQueue.add(detectionrequest);
    }

    protected final void handleMessage(Message message) {
        if(0 == message.what) {
        	DetectionRequest detectionrequest = (DetectionRequest)message.obj;
            if(mClient != null) {
                if(EsLog.isLoggable("PanoramaDetector", 3))
                    Log.d("PanoramaDetector", (new StringBuilder("Detecting if the image is a panorama: ")).append(detectionrequest.mUri).toString());
                mClient.loadPanoramaInfo(detectionrequest, detectionrequest.mUri);
            }
        } else if(1 == message.what) {
        	if(mClient != null) {
                if(EsLog.isLoggable("PanoramaDetector", 3))
                    Log.d("PanoramaDetector", "Disconnecting from GooglePlayServices");
                mClient.disconnect();
            }
        } else {
        	
        }
    }

    public final synchronized void onConnected() {
        if(EsLog.isLoggable("PanoramaDetector", 3))
            Log.d("PanoramaDetector", "Connected to GooglePlayServices");
		int size = mQueue.size();
        for(int i = 0; i < size; i++)
            mHandler.sendMessage(mHandler.obtainMessage(0, mQueue.get(i)));

        mQueue.clear();
        mWaitingForConnection = false;
    }

    public final synchronized void onConnectionFailed(ConnectionResult result) {
        mClient = null;
        mQueue.clear();
    }

    public final void onDisconnected() {
        mClient = null;
        mQueue.clear();
    }

    public final synchronized void start() {
        super.start();
        mHandler = new Handler(getLooper()) {

            public final void handleMessage(Message message)
            {
                PanoramaDetector.this.handleMessage(message);
            }
        };
        connect();
    }

	private final class DetectionRequest implements
			PanoramaClient.OnPanoramaInfoLoadedListener {

		private Resource mResource;
		private Uri mUri;

		public DetectionRequest(Resource resource, Uri uri) {
			super();
			mResource = resource;
			mUri = uri;
		}
		
		public final void onPanoramaInfoLoaded(ConnectionResult result, Intent intent) {
			Resource resource = mResource;
			Uri uri = mUri;
			boolean flag;
			if (intent != null)
				flag = true;
			else
				flag = false;
			
			sPanoramaMap.put(uri, Boolean.valueOf(flag));
	        if(flag)
	            resource.deliverResourceType(2);
	        PanoramaDetector.this.mHandler.sendEmptyMessageDelayed(1, 3000L);
		}
	}
}
