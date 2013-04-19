/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.ByteArrayEntity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.galaxy.meetup.client.android.api.OzServerException;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.ApiaryHttpRequestConfiguration;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.MeetupRequest;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class ApiaryBatchOperation extends HttpOperation {

	private static final byte CONTENT_TYPE_APP_HTTP[] = "Content-Type: application/http\n".getBytes();
    private static final byte CONTENT_TYPE_JSON[] = "Content-Type: application/json\n".getBytes();
    private static final byte MULTIPART_BOUNDARY_END[] = "--MultiPartRequest--\n".getBytes();
    private static final byte MULTIPART_BOUNDARY_START[] = "--MultiPartRequest\n".getBytes();
    private static final Pattern PATTERN_CONTENT_LENGTH = Pattern.compile("Content-Length: (\\d+)");
    private static final Pattern PATTERN_ID = Pattern.compile("Content-ID: <response-item:(.+)>");
    private static final Pattern PATTERN_RESPONSE_CODE = Pattern.compile("HTTP/1\\.1 (\\d+) (.*)");
    private HttpOperation mCurrentOperation;
    private List mOperations;
    
	public ApiaryBatchOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener) {
		super(
				context,
				"POST",
				Property.ENABLE_DOGFOOD_FEATURES.getBoolean()
						&& Property.PLUS_FRONTEND_URL.get().startsWith("http:") ? (new StringBuilder())
						.append(Property.PLUS_FRONTEND_URL.get())
						.append("/batch").toString()
						: (new StringBuilder("https://"))
								.append(Property.PLUS_FRONTEND_URL.get())
								.append("/batch").toString(),
				new ApiaryHttpRequestConfiguration(
						context,
						esaccount,
						"oauth2:https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/plus.stream.read https://www.googleapis.com/auth/plus.stream.write https://www.googleapis.com/auth/plus.circles.write https://www.googleapis.com/auth/plus.circles.read https://www.googleapis.com/auth/plus.photos.readwrite https://www.googleapis.com/auth/plus.native",
						null, "multipart/mixed; boundary=MultiPartRequest"),
				esaccount, null, intent, operationlistener);
		mOperations = new ArrayList();
	}

    private void readPartBody(DataInputStream datainputstream) throws IOException {
        int i;
        int j;
        String s;
        ByteArrayInputStream bytearrayinputstream;
        Object obj;
        i = 0;
        j = 200;
        s = null;
        do
        {
            String s1 = datainputstream.readLine();
            if(s1.equals(""))
                break;
            Matcher matcher = PATTERN_CONTENT_LENGTH.matcher(s1);
            if(matcher.matches())
            {
                i = Integer.parseInt(matcher.group(1));
            } else
            {
                Matcher matcher1 = PATTERN_RESPONSE_CODE.matcher(s1);
                if(matcher1.matches())
                {
                    j = Integer.parseInt(matcher1.group(1));
                    s = matcher1.group(2);
                }
            }
        } while(true);
        byte abyte0[] = new byte[i];
        for(int k = 0; k < abyte0.length; k += datainputstream.read(abyte0, k, abyte0.length - k));
        datainputstream.readLine();
        bytearrayinputstream = new ByteArrayInputStream(abyte0);
        obj = null;
        if(j < 200 || j >= 300) { 
        	if(EsLog.isLoggable("HttpTransaction", 3))
                Log.d("HttpTransaction", (new StringBuilder("Error: ")).append(j).append("/").append(s).append(" [").append(mCurrentOperation.getName()).append("]").toString());
            if(j == 401)
                throw new HttpResponseException(j, s);
            try
            {
                mCurrentOperation.onHttpReadErrorFromStream(bytearrayinputstream, null, i, null, j);
            }
            catch(OzServerException ozserverexception)
            {
                obj = ozserverexception;
            }
            catch(Exception exception)
            {
                Log.e("HttpTransaction", (new StringBuilder("Failed to read error response: ")).append(exception).toString());
                obj = null;
            }
            if(obj == null)
                obj = new HttpResponseException(j, s); 
    	} else {
    		mCurrentOperation.onHttpHandleContentFromStream(bytearrayinputstream);
    		mCurrentOperation.onHttpOperationComplete(j, s, ((Exception) (obj)));
            mCurrentOperation.setErrorInfo(j, s, ((Exception) (obj)));
        }
    }

    private int readPartHeader(DataInputStream datainputstream) throws IOException {
        String s = null;
        String s1 = datainputstream.readLine();
        if(null == s1) {
        	return -1;
        }
        if(!s1.equals("")) {
        	Matcher matcher = PATTERN_ID.matcher(s1);
            if(matcher.matches()) {
                s = matcher.group(1);
                try
                {
                    return Integer.parseInt(s);
                }
                catch(NumberFormatException numberformatexception)
                {
                    throw new IOException((new StringBuilder("Invalid response format. Section ID = '")).append(s).append("'").toString());
                }
            }
        }
        return -1;
        
    }

    public final void add(HttpOperation httpoperation)
    {
        mOperations.add(httpoperation);
    }

    public final MeetupRequest createPostData() throws IOException
    {
        /*ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte abyte0[] = new byte[1024];
        for(int i = 0; i < mOperations.size(); i++)
        {
            HttpOperation httpoperation = (HttpOperation)mOperations.get(i);
            bytearrayoutputstream.write(MULTIPART_BOUNDARY_START);
            bytearrayoutputstream.write(CONTENT_TYPE_APP_HTTP);
            bytearrayoutputstream.write((new StringBuilder("Content-ID: <item:")).append(i).append(">\n").toString().getBytes());
            bytearrayoutputstream.write(10);
            bytearrayoutputstream.write(httpoperation.getMethod().getBytes());
            bytearrayoutputstream.write(32);
            bytearrayoutputstream.write(Uri.parse(httpoperation.getUrl()).getPath().getBytes());
            bytearrayoutputstream.write(10);
            InputStream inputstream = httpoperation.createPostData().getContent();
            ByteArrayOutputStream bytearrayoutputstream1 = new ByteArrayOutputStream();
            do
            {
                int j = inputstream.read(abyte0);
                if(j <= 0)
                    break;
                bytearrayoutputstream1.write(abyte0, 0, j);
            } while(true);
            int k = bytearrayoutputstream1.size();
            if(k > 0)
            {
                bytearrayoutputstream.write(CONTENT_TYPE_JSON);
                bytearrayoutputstream.write((new StringBuilder("Content-Length: ")).append(k).append("\n").toString().getBytes());
                bytearrayoutputstream.write(10);
                bytearrayoutputstream.write(bytearrayoutputstream1.toByteArray());
                bytearrayoutputstream.write(10);
            }
            bytearrayoutputstream.write(10);
        }

        bytearrayoutputstream.write(MULTIPART_BOUNDARY_END);
        return new ByteArrayEntity(bytearrayoutputstream.toByteArray());*/
    	return null;
    }

    public final boolean hasError() {
        boolean flag = true;
        if(super.hasError()) {
        	return true;
        }
        
        int i = mOperations.size();
        for(int j = 0; j < i; j++)
            if(((HttpOperation)mOperations.get(j)).hasError())
                return true;
        return false;
    }

    public final void logError(String s)
    {
        if(super.hasError())
            super.logError(s);
        int i = mOperations.size();
        int j = 0;
        while(j < i) 
        {
            HttpOperation httpoperation = (HttpOperation)mOperations.get(j);
            if(httpoperation.hasError())
                if(httpoperation.getException() != null)
                    Log.e(s, (new StringBuilder("[")).append(httpoperation.getName()).append("] failed due to exception: ").append(httpoperation.getException()).toString(), httpoperation.getException());
                else
                if(EsLog.isLoggable(s, 4))
                    Log.i(s, (new StringBuilder("[")).append(httpoperation.getName()).append("] failed due to error: ").append(httpoperation.getErrorCode()).append(" [").append(httpoperation.getReasonPhrase()).append("]").toString());
            j++;
        }
    }

    protected final void onHttpHandleContentFromStream$6508b088(InputStream inputstream)
        throws IOException
    {
        boolean aflag[] = new boolean[mOperations.size()];
        DataInputStream datainputstream = new DataInputStream(inputstream);
        do
        {
            int i = readPartHeader(datainputstream);
            if(i == -1)
            {
                for(int j = 0; j < aflag.length; j++)
                    if(!aflag[j])
                        throw new IOException((new StringBuilder("Incomplete response. Response missing for: ")).append(mOperations.get(j)).toString());

                break;
            }
            mCurrentOperation = (HttpOperation)mOperations.get(i);
            readPartBody(datainputstream);
            aflag[i] = true;
        } while(true);
    }
}
