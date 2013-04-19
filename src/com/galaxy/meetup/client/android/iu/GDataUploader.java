/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.android.gallery3d.common.Fingerprint;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.picasa.HttpUtils;
import com.galaxy.picasa.store.MetricsUtils;

/**
 * 
 * @author sihai
 *
 */
public class GDataUploader implements Uploader {

	private static final Pattern RE_RANGE_HEADER = Pattern.compile("bytes=(\\d+)-(\\d+)");
    private static String sUserAgent;
    private Authorizer mAuthorizer;
    private Context mContext;
    private HttpClient mHttpClient;
    private Uploader.UploadProgressListener mListener;
    private UploadTaskEntry mUploadTask;
    private final UploadsDatabaseHelper mUploadsDbHelper;
    
    
    GDataUploader(Context context)
    {
        mContext = context;
        mHttpClient = HttpUtils.createHttpClient(getUserAgent(context));
        mUploadsDbHelper = UploadsDatabaseHelper.getInstance(context);
        mAuthorizer = new Authorizer(context);
    }

    private HttpResponse executeWithAuthRetry(HttpUriRequest httpurirequest, String s, String s1)
        throws ClientProtocolException, IOException, Uploader.UnauthorizedException
    {
        long l = SystemClock.elapsedRealtime();
        HttpResponse httpresponse = mHttpClient.execute(httpurirequest);
        MetricsUtils.incrementNetworkOpDuration(SystemClock.elapsedRealtime() - l);
        int i = httpresponse.getStatusLine().getStatusCode();
        if(i == 401 || i == 403)
        {
            String s2;
            try
            {
                s2 = mAuthorizer.getFreshAuthToken(s, "lh2", s1);
                if(s2 == null)
                    throw new Uploader.UnauthorizedException("null auth token");
            }
            catch(OperationCanceledException operationcanceledexception)
            {
                if(EsLog.isLoggable("iu.UploadsManager", 3))
                    Log.d("iu.UploadsManager", "authentication canceled", operationcanceledexception);
                throw new Uploader.UnauthorizedException(operationcanceledexception);
            }
            catch(IOException ioexception)
            {
                if(EsLog.isLoggable("iu.UploadsManager", 3))
                    Log.d("iu.UploadsManager", "authentication failed", ioexception);
                throw ioexception;
            }
            catch(AuthenticatorException authenticatorexception)
            {
                if(EsLog.isLoggable("iu.UploadsManager", 5))
                    Log.w("iu.UploadsManager", authenticatorexception);
                throw new Uploader.UnauthorizedException(authenticatorexception);
            }
            httpurirequest.setHeader("Authorization", (new StringBuilder("GoogleLogin auth=")).append(s2).toString());
            if(EsLog.isLoggable("iu.UploadsManager", 3))
                Log.d("iu.UploadsManager", "executeWithAuthRetry: attempt #2");
            long l1 = SystemClock.elapsedRealtime();
            httpresponse = mHttpClient.execute(httpurirequest);
            MetricsUtils.incrementNetworkOpDuration(SystemClock.elapsedRealtime() - l1);
        }
        return httpresponse;
    }

    private String getAuthToken(String s)
        throws IOException, Uploader.UnauthorizedException
    {
        String s1;
        try
        {
            s1 = mAuthorizer.getAuthToken(s, "lh2");
        }
        catch(OperationCanceledException operationcanceledexception)
        {
            if(EsLog.isLoggable("iu.UploadsManager", 4))
                Log.i("iu.UploadsManager", "authentication canceled", operationcanceledexception);
            throw new Uploader.UnauthorizedException(operationcanceledexception);
        }
        catch(IOException ioexception)
        {
            if(EsLog.isLoggable("iu.UploadsManager", 4))
                Log.i("iu.UploadsManager", "authentication failed", ioexception);
            throw ioexception;
        }
        catch(AuthenticatorException authenticatorexception)
        {
            if(EsLog.isLoggable("iu.UploadsManager", 5))
                Log.w("iu.UploadsManager", authenticatorexception);
            throw new Uploader.UnauthorizedException(authenticatorexception);
        }
        return s1;
    }

    private static HttpEntity getEntity(HttpResponse httpresponse)
        throws IOException
    {
        BufferedHttpEntity bufferedhttpentity = new BufferedHttpEntity(httpresponse.getEntity());
        if(bufferedhttpentity.getContentLength() == 0L)
        {
            safeConsumeContent(bufferedhttpentity);
            bufferedhttpentity = null;
        }
        return bufferedhttpentity;
    }

    private MediaRecordEntry getMediaRecordEntry(UploadTaskEntry uploadtaskentry, GDataResponse gdataresponse)
        throws Uploader.UploadException
    {
        MediaRecordEntry mediarecordentry = MediaRecordEntry.fromId(mUploadsDbHelper.getReadableDatabase(), uploadtaskentry.getMediaRecordId());
        if(mediarecordentry == null)
        {
            throw new Uploader.UploadException((new StringBuilder("could not find the media record for the uploaded task; ")).append(uploadtaskentry).toString());
        } else
        {
            mediarecordentry.setUploadId(gdataresponse.photoId).setUploadUrl(gdataresponse.photoUrl).setUploadTime(gdataresponse.timestamp).setBytesUploaded(uploadtaskentry.getBytesUploaded()).setState(300);
            return mediarecordentry;
        }
    }

    private static String getUserAgent(Context context)
    {
        if(sUserAgent == null)
        {
            PackageInfo packageinfo;
            Object aobj[];
            try
            {
                packageinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            }
            catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
            {
                throw new IllegalStateException("getPackageInfo failed");
            }
            aobj = new Object[10];
            aobj[0] = packageinfo.packageName;
            aobj[1] = packageinfo.versionName;
            aobj[2] = Build.BRAND;
            aobj[3] = Build.DEVICE;
            aobj[4] = Build.MODEL;
            aobj[5] = Build.ID;
            aobj[6] = android.os.Build.VERSION.SDK;
            aobj[7] = android.os.Build.VERSION.RELEASE;
            aobj[8] = android.os.Build.VERSION.INCREMENTAL;
            aobj[9] = Integer.valueOf(1);
            sUserAgent = String.format("%s/%s; %s/%s/%s/%s; %s/%s/%s/%d", aobj);
        }
        return sUserAgent;
    }

    private static boolean isIncompeteStatusCode(int i)
    {
        boolean flag;
        if(i == 308)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private static boolean isSuccessStatusCode(int i)
    {
        boolean flag;
        if(i == 200 || i == 201)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private static HashMap parseHeaders(String s)
    {
        HashMap hashmap = new HashMap();
        String as[] = s.split("\r\n");
        int i = as.length;
        for(int j = 0; j < i; j++)
        {
            String as1[] = as[j].split(":");
            if(as1.length == 2)
                hashmap.put(as1[0], as1[1]);
        }

        return hashmap;
    }

    private static GDataQuota parseQuotaResponse(HttpEntity httpentity) {
        // TODO
    	return null;
    }

    private static long parseRangeHeaderEndByte(String s) {
        if(s == null) { 
        	return -1; 
    	} else {
        	Matcher matcher = RE_RANGE_HEADER.matcher(s);
        	if(matcher.find()) {
        		return 1L + Long.parseLong(matcher.group(2));
        	}
        	return -1L;
        }
    }

    private static GDataResponse parseResult(HttpEntity httpentity) throws SAXException, IOException, Uploader.UploadException {
    	InputStream inputstream = null;
        if(httpentity == null)
            throw new Uploader.UploadException("null HttpEntity in response");
        GDataResponse gdataresponse = new GDataResponse();
        try {
        	inputstream = httpentity.getContent();
        	Xml.parse(inputstream, android.util.Xml.Encoding.UTF_8, gdataresponse);
        	gdataresponse.validateResult();
        	return gdataresponse;
        } finally {
        	if(null != inputstream) {
        		inputstream.close();
        	}
        }
        
    }

    private void resetUpload() {
        mUploadTask.setUploadUrl(null);
        mUploadTask.setBytesUploaded(0L);
    }

    private MediaRecordEntry resume(InputStream inputstream, String s, String s1)
        throws ClientProtocolException, IOException, Uploader.PicasaQuotaException, SAXException, Uploader.UploadException, Uploader.LocalIoException, Uploader.MediaFileChangedException, Uploader.RestartException, Uploader.UnauthorizedException
    {
        // TODO
    	return null;
    }

    private static void safeConsumeContent(HttpEntity httpentity) {
        if(httpentity == null)
            return;
        try {
        	httpentity.consumeContent();
        } catch (IOException e) {
        	// 
        }
    }

    private MediaRecordEntry start(InputStream inputstream, Uri uri, String s, String s1, String s2)
        throws ClientProtocolException, IOException, Uploader.PicasaQuotaException, SAXException, Uploader.UploadException, Uploader.MediaFileChangedException, Uploader.UnauthorizedException, Uploader.RestartException, Uploader.LocalIoException
    {
        // TODO
    	return null;
    }

    private static void throwIfQuotaError(GDataResponse gdataresponse)
        throws Uploader.PicasaQuotaException
    {
        if(gdataresponse != null && "LimitQuota".equals(gdataresponse.errorCode))
            throw new Uploader.PicasaQuotaException(gdataresponse.errorCode);
        else
            return;
    }

    private MediaRecordEntry uploadChunks(InputStream inputstream, String s, String s1)
        throws ClientProtocolException, IOException, Uploader.PicasaQuotaException, SAXException, Uploader.UploadException, Uploader.MediaFileChangedException, Uploader.RestartException, Uploader.LocalIoException, Uploader.UnauthorizedException
    {
        // TODO
    	return null;
    }

    public final void close()
    {
        mHttpClient = null;
        mAuthorizer = null;
    }

    final GDataQuota getQuota(String s)
    {
        // TODO 
    	return null;
    }

    public final MediaRecordEntry upload(UploadTaskEntry uploadtaskentry, Uploader.UploadProgressListener uploadprogresslistener)
        throws IOException, Uploader.UploadException, Uploader.RestartException, Uploader.LocalIoException, Uploader.MediaFileChangedException, Uploader.MediaFileUnavailableException, Uploader.UnauthorizedException, Uploader.PicasaQuotaException
    {
        // TODO
    	return null;
    }
    
	public static final class GDataQuota {

		public final String toString() {
			return (new StringBuilder("[GDataIUStats; limit: "))
					.append(quotaLimit).append(", used: ").append(quotaUsed)
					.append(", low quota? ").append(disableFullRes).append("]")
					.toString();
		}

		boolean disableFullRes;
		long quotaLimit;
		long quotaUsed;

		public GDataQuota() {
			quotaLimit = -1L;
			quotaUsed = -1L;
			disableFullRes = false;
		}
	}

    private static final class GDataResponse extends DefaultHandler {

    	String errorCode;
        Fingerprint fingerprint;
        GDataQuota iuStats;
        private HashMap mMap;
        private List<String> mStreamIdList;
        private StringBuilder mText;
        long photoId;
        String photoUrl;
        long timestamp;

        GDataResponse()
        {
            mMap = new HashMap();
            mStreamIdList = new ArrayList<String>();
        }
        
        private static GDataQuota getIUStatsAttrs(Attributes attributes)
        {
            GDataQuota gdataquota = new GDataQuota();
            int i = attributes.getLength();
            int j = 0;
            while(j < i) 
            {
                String s = attributes.getQName(j);
                if("quotaLimitMB".contentEquals(s))
                    try
                    {
                        gdataquota.quotaLimit = Long.parseLong(attributes.getValue(j));
                    }
                    catch(NumberFormatException numberformatexception1) { }
                else
                if("quotaUsedMB".contentEquals(s))
                    try
                    {
                        gdataquota.quotaUsed = Long.parseLong(attributes.getValue(j));
                    }
                    catch(NumberFormatException numberformatexception) { }
                else
                if("disableFullRes".contentEquals(s))
                    gdataquota.disableFullRes = Boolean.parseBoolean(attributes.getValue(j));
                j++;
            }
            return gdataquota;
        }

        public final void characters(char ac[], int i, int j)
        {
            if(mText != null)
                mText.append(ac, i, j);
        }

        public final void endElement(String s, String s1, String s2)
        {
            if("gphoto:streamId".contentEquals(s2) && mText.length() > 0)
                mStreamIdList.add(mText.toString());
            mText = null;
        }

        public final void startDocument()
        {
            mMap.clear();
            mMap.put("code", new StringBuilder());
            mMap.put("gphoto:id", new StringBuilder());
            mMap.put("gphoto:size", new StringBuilder());
            mMap.put("gphoto:streamId", new StringBuilder());
            mMap.put("gphoto:timestamp", new StringBuilder());
            photoUrl = "";
            mStreamIdList.clear();
        }

        public final void startElement(String s, String s1, String s2, Attributes attributes) {
            mText = (StringBuilder)mMap.get(s2);
            if(mText != null)
                return;
            if(!"media:content".contentEquals(s2)) {
            	if("gphoto:iuStats".contentEquals(s2))
                    iuStats = getIUStatsAttrs(attributes); 
            } else {
            	// TODO
            }
        }

        public final void validateResult() throws Uploader.UploadException {
            errorCode = ((StringBuilder)mMap.get("code")).toString();
            try
            {
                photoId = Long.parseLong(((StringBuilder)mMap.get("gphoto:id")).toString());
            }
            catch(NumberFormatException numberformatexception)
            {
                throw new Uploader.UploadException((new StringBuilder("error parsing photo ID: ")).append(mMap.get("gphoto:id")).toString());
            }
            try
            {
                timestamp = Long.parseLong(((StringBuilder)mMap.get("gphoto:timestamp")).toString());
            }
            catch(NumberFormatException numberformatexception1)
            {
                throw new Uploader.UploadException((new StringBuilder("error parsing timestamp: ")).append(mMap.get("gphoto:timestamp")).toString());
            }
            if(TextUtils.isEmpty(photoUrl))
                throw new Uploader.UploadException("photo URL missing");
            fingerprint = Fingerprint.extractFingerprint(mStreamIdList);
            if(fingerprint == null)
                throw new Uploader.UploadException((new StringBuilder("fingerprint missing: ")).append(mStreamIdList).toString());
            else
                return;
        }

    }
}
