/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.widget;

import WriteReviewOperation.MediaRef;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class EsWidgetCameraLauncherActivity extends FragmentActivity {

	private Integer mInsertCameraPhotoRequestId;
    private EsServiceListener mServiceListener;
    
    public EsWidgetCameraLauncherActivity()
    {
        mServiceListener = new EsServiceListener() {

            public final void onInsertCameraPhotoComplete(int i, ServiceResult serviceresult)
            {
                if(mInsertCameraPhotoRequestId != null && mInsertCameraPhotoRequestId.intValue() == i)
                {
                    insertCameraPhoto(EsService.getLastCameraMediaLocation());
                    mInsertCameraPhotoRequestId = null;
                }
            }

        };
    }

    private void insertCameraPhoto(String s)
    {
        if(s != null)
        {
            Intent intent = Intents.getPostActivityIntent(this, null, new MediaRef(null, 0L, null, Uri.parse(s), MediaRef.MediaType.IMAGE));
            intent.removeExtra("account");
            Intent intent1 = getIntent();
            if(intent1.hasExtra("audience"))
                intent.putExtra("audience", (AudienceData)intent1.getParcelableExtra("audience"));
            startActivity(intent);
        } else
        {
            Toast.makeText(this, getString(R.string.camera_photo_error), 1).show();
        }
        dismissDialog(0x7f0a003e);
        finish();
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
    	if(1 == i) {
    		if(j == -1)
            {
                showDialog(0x7f0a003e);
                mInsertCameraPhotoRequestId = EsService.insertCameraPhoto(this, (EsAccount)getIntent().getParcelableExtra("account"), "camera-p.jpg");
            } else
            {
                finish();
            }
    	}
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null) {
        	if(bundle.containsKey("insert_camera_photo_req_id"))
                mInsertCameraPhotoRequestId = Integer.valueOf(bundle.getInt("insert_camera_photo_req_id")); 
        } else { 
        	startActivityForResult(Intents.getCameraIntentPhoto("camera-p.jpg"), 1);
        }
    }

    public Dialog onCreateDialog(int i, Bundle bundle)
    {
        Dialog dialog;
        if(i == 0x7f0a003e)
            dialog = ImageUtils.createInsertCameraPhotoDialog(this);
        else
            dialog = super.onCreateDialog(i, bundle);
        return dialog;
    }

    public void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mInsertCameraPhotoRequestId != null && !EsService.isRequestPending(mInsertCameraPhotoRequestId.intValue()))
        {
            EsService.removeResult(mInsertCameraPhotoRequestId.intValue());
            insertCameraPhoto(EsService.getLastCameraMediaLocation());
            mInsertCameraPhotoRequestId = null;
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mInsertCameraPhotoRequestId != null)
            bundle.putInt("insert_camera_photo_req_id", mInsertCameraPhotoRequestId.intValue());
    }
}
