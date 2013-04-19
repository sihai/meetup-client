/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsCursorLoader;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class InviteContactActivity extends EsProfileGatewayActivity implements
		LoaderCallbacks {

	private static final String ENTITY_PROJECTION[] = {
        "display_name", "mimetype", "data1"
    };
    private final Handler mHandler = new Handler();
    
    public InviteContactActivity()
    {
    }

    protected void onActivityResult(int i, int j, Intent intent)
    {
        if(i == 1)
        {
            boolean flag = false;
            if(j == -1)
            {
                mPersonId = intent.getStringExtra("person_id");
                String s = mPersonId;
                flag = false;
                if(s != null)
                {
                    mPersonName = ((PersonData)intent.getParcelableExtra("person_data")).getName();
                    showCirclePicker();
                    flag = true;
                }
            }
            if(!flag)
                finish();
        } else
        {
            super.onActivityResult(i, j, intent);
        }
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
        return new EsCursorLoader(this, Uri.withAppendedPath((Uri)bundle.getParcelable("data_uri"), "entities"), ENTITY_PROJECTION, "mimetype IN ('vnd.android.cursor.item/name','vnd.android.cursor.item/email_v2')", null, null);
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(cursor == null) {
        	finish();
        	return;
        }
        
        if(!mRedirected)
        {
            mRedirected = true;
            final ArrayList emails = new ArrayList();
            do
            {
                if(!cursor.moveToNext())
                    break;
                String s = cursor.getString(0);
                if(s != null)
                    mPersonName = s;
                if("vnd.android.cursor.item/email_v2".equals(cursor.getString(1)))
                {
                    String s1 = cursor.getString(2);
                    if(s1 != null)
                        s1 = s1.trim();
                    if(!emails.contains(s1))
                        emails.add(s1);
                }
            } while(true);
            Collections.sort(emails);
            int i = emails.size();
            if(i == 0)
                mHandler.post(new Runnable() {

                    public final void run()
                    {
                        showSearchActivity();
                    }

                });
            else
            if(i == 1)
                mHandler.post(new Runnable() {

                    public final void run()
                    {
                        showCirclePicker((String)emails.get(0));
                    }

                });
            else
                mHandler.post(new Runnable() {

                    public final void run()
                    {
                        show(emails);
                    }

                });
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void showCirclePicker(String s)
    {
        mPersonId = (new StringBuilder("e:")).append(s).toString();
        showCirclePicker();
    }

    protected final void showSearchActivity()
    {
        startActivityForResult(Intents.getPeopleSearchActivityIntent(this, mAccount, mPersonName, true, -1, true, false, true, false, false), 1);
    }
    
    void show(ArrayList arraylist)
    {
    	try {
    		(new EmailPickerDialog((String[])arraylist.toArray(new String[0]))).show(getSupportFragmentManager(), "pick_email");
    	} catch (Throwable throwable) {
    		if(EsLog.isLoggable("InviteContactActivity", 6))
                Log.e("InviteContactActivity", "Cannot show dialog", throwable);
    		finish();
    	}
    }
    
    private class EmailPickerDialog extends DialogFragment implements android.content.DialogInterface.OnClickListener {

	    public void onCancel(DialogInterface dialoginterface)
	    {
	        getActivity().finish();
	    }
	
	    public void onClick(DialogInterface dialoginterface, int i)
	    {
	        if(i == -2)
	        {
	            getActivity().finish();
	        } else
	        {
	            Bundle bundle = getArguments();
	            ((InviteContactActivity)getActivity()).showCirclePicker(bundle.getStringArray("emails")[i]);
	        }
	    }
	
	    public final Dialog onCreateDialog(Bundle bundle)
	    {
	        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_EmeraldSea));
	        builder.setTitle(R.string.add_to_circle_email_picker_title);
	        builder.setAdapter(new ArrayAdapter(getActivity(), 0x1090012, getArguments().getStringArray("emails")), this);
	        builder.setNegativeButton(0x1040000, this);
	        builder.setCancelable(true);
	        return builder.create();
	    }
	
	    public EmailPickerDialog()
	    {
	    }
	
	    public EmailPickerDialog(String as[])
	    {
	        Bundle bundle = new Bundle();
	        bundle.putStringArray("emails", as);
	        setArguments(bundle);
	    }
    }
}
