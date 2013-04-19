/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.HttpOperation;

/**
 * 
 * @author sihai
 *
 */
public class SavePhotoOperation extends DownloadPhotoOperation {

	private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("attachment;\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*$", 2);
    private static final File SAVE_TO_DIRECTORY;
    private String mContentType;
    private String mSaveToName;

    static 
    {
        SAVE_TO_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }
    
    public SavePhotoOperation(Context context, EsAccount esaccount, Intent intent, String s, HttpOperation.OperationListener operationlistener)
    {
        super(context, "GET", s, esaccount, null, intent, operationlistener);
    }

    private static String parseContentDisposition(String s) {
    	
    	try {
	        Matcher matcher = CONTENT_DISPOSITION_PATTERN.matcher(s);
	        if(!matcher.find()) 
	        	return null;
	        
	        return  matcher.group(2);
    	} catch (IllegalStateException illegalstateexception) {
    		// TOTO log
    		return null;
    	}
    }

    public final String getContentType()
    {
        return mContentType;
    }

    public final File getSaveToFile()
    {
        File file;
        if(TextUtils.isEmpty(mSaveToName))
            file = null;
        else
            file = new File(SAVE_TO_DIRECTORY, mSaveToName);
        return file;
    }

    public final void onHttpReadFromStream(InputStream inputstream, String s, int i, Header aheader[]) throws IOException {
    	
        // TODO
    }
}
