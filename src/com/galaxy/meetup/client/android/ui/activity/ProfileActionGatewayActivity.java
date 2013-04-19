/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsCursorLoader;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;

/**
 * 
 * @author sihai
 *
 */
public class ProfileActionGatewayActivity extends EsProfileGatewayActivity
implements android.support.v4.app.LoaderManager.LoaderCallbacks {

	private static final String PROJECTION[] = {
        "sourceid", "data5", "display_name"
    };
	
	public ProfileActionGatewayActivity()
    {
    }

    private AudienceData createAudience()
    {
        String s;
        String s1;
        String s2;
        s = null;
        s1 = mPersonId;
        s2 = mPersonName;
        if(s1.startsWith("e:")) {
        	s1 = s1.substring(2);
        } else if(s1.startsWith("p:")) {
        	
        } else {
        	s = EsPeopleData.extractGaiaId(s1);
        }
        
        return new AudienceData(new PersonData(s, s2, s1));
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(!isFinishing())
        {
            Uri uri = getIntent().getData();
            if(uri == null)
            {
                finish();
            } else
            {
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("data_uri", uri);
                getSupportLoaderManager().initLoader(0, bundle1, this);
            }
        }
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        return new EsCursorLoader(this, (Uri)bundle.getParcelable("data_uri"), PROJECTION, null, null, null);
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(!mRedirected)
        {
            mRedirected = true;
            if(cursor == null || !cursor.moveToFirst())
            {
                Toast.makeText(this, R.string.profile_does_not_exist, 0).show();
                finish();
            } else
            {
                mPersonId = cursor.getString(0);
                if(TextUtils.isEmpty(mPersonId))
                {
                    Toast.makeText(this, R.string.profile_does_not_exist, 0).show();
                    finish();
                } else
                if(!mPersonId.startsWith("g:") && !mPersonId.startsWith("e:") && !mPersonId.startsWith("p:"))
                {
                    Log.e("ProfileActionGatewayActivity", (new StringBuilder("Unrecognized aggregate ID format: ")).append(mPersonId).toString());
                    Toast.makeText(this, R.string.profile_does_not_exist, 0).show();
                    finish();
                } else
                {
                    String s = cursor.getString(1);
                    mPersonName = cursor.getString(2);
                    if("conversation".equals(s))
                    {
                        Intent intent2 = Intents.getNewConversationActivityIntent(this, mAccount, createAudience());
                        intent2.addFlags(0x2000000);
                        startActivity(intent2);
                        finish();
                    } else
                    if("hangout".equals(s))
                    {
                        Intent intent1 = Intents.getNewHangoutActivityIntent(this, mAccount, true, createAudience());
                        intent1.addFlags(0x2000000);
                        startActivity(intent1);
                        finish();
                    } else
                    if("addtocircle".equals(s))
                    {
                        showCirclePicker();
                    } else
                    {
                        Intent intent = Intents.getProfileActivityIntent(this, mAccount, mPersonId, null);
                        intent.addFlags(0x2000000);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }
}
