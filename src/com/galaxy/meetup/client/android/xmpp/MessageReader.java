/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.xmpp;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * 
 * @author sihai
 *
 */
public class MessageReader {

	private static String TAG = "MessageReader";
    private boolean mAuthenticationRequired;
    private boolean mBindAvailable;
    private String mEventData;
    private final LogInputStream mInputStream;
    private final XmlPullParser mParser;
    private StringBuilder mStringBuilder;
    private boolean mTlsRequired;
    
	public MessageReader(InputStream inputstream, boolean flag)
    {
        try
        {
            mParser = XmlPullParserFactory.newInstance().newPullParser();
            if(flag)
            {
                mInputStream = new LogInputStream(inputstream);
                mParser.setInput(mInputStream, null);
            } else
            {
                mInputStream = null;
                mParser.setInput(inputstream, null);
            }
        }
        catch(XmlPullParserException xmlpullparserexception)
        {
            throw new RuntimeException("Unable to create XML parser", xmlpullparserexception);
        }
    }
	
	private void updateEventData()
    {
        mEventData = mStringBuilder.toString();
        mStringBuilder = null;
    }

    public final String getEventData()
    {
        return mEventData;
    }
    
    public final Event read() {
    	// TODO
    	return null;
    }
    
	
	//======================================================================================
	//							Inner class
	//======================================================================================
	public static enum Event {
		UNEXPECTED_FEATURES,
		END_OF_STREAM,
		TLS_REQUIRED,
		PROCEED_WITH_TLS,
		AUTHENTICATION_REQUIRED,
		AUTHENTICATION_SUCCEEDED,
		AUTHENTICATION_FAILED,
		STREAM_READY,
		JID_AVAILABLE,
		DATA_RECEIVED;
	}
}
