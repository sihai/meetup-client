/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import WriteReviewOperation.MediaRef;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.GetActivityOperation;
import com.galaxy.meetup.client.android.content.DbEmbedMedia;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsCursorLoader;
import com.galaxy.meetup.client.android.content.EsProvider;

/**
 * 
 * @author sihai
 *
 */
public class ViewStreamItemPhotoActivity extends FragmentActivity implements
		LoaderCallbacks {

	private static final String ACTIVITY_RESULT_PROJECTION[] = {
        "person_id", "activity_id", "embed_media"
    };
    private static final String STREAM_ITEM_PHOTO_PROJECTION[] = {
        "raw_contact_source_id", "stream_item_photo_sync1", "stream_item_photo_sync2"
    };

    public ViewStreamItemPhotoActivity()
    {
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Uri uri = getIntent().getData();
        if(uri == null)
        {
            finish();
        } else
        {
            EsAccount esaccount = EsAccountsData.getActiveAccount(this);
            if(esaccount == null)
            {
                finish();
            } else
            {
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("account", esaccount);
                bundle1.putParcelable("stream_item_uri", uri);
                getSupportLoaderManager().initLoader(0, bundle1, this);
            }
        }
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        return new ActivityIdLoader(this, (EsAccount)bundle.getParcelable("account"), (Uri)bundle.getParcelable("stream_item_uri"));
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        EsAccount esaccount = EsAccountsData.getActiveAccount(this);
        if(esaccount != null)
        {
            cursor.moveToFirst();
            String s = cursor.getString(1);
            String s1 = cursor.getString(0);
            byte abyte0[] = cursor.getBlob(2);
            if(abyte0 != null && abyte0.length != 0)
            {
                DbEmbedMedia dbembedmedia = DbEmbedMedia.deserialize(abyte0);
                if(!TextUtils.isEmpty(dbembedmedia.getImageUrl()))
                {
                    Intents.PhotoViewIntentBuilder photoviewintentbuilder = Intents.newPhotoViewActivityIntentBuilder(this);
                    if(dbembedmedia.getPhotoId() != 0L)
                    {
                        String s2 = dbembedmedia.getOwnerId();
                        MediaRef mediaref = new MediaRef(s2, dbembedmedia.getPhotoId(), null, null, MediaRef.MediaType.IMAGE);
                        String s3 = dbembedmedia.getAlbumId();
                        photoviewintentbuilder.setAccount(esaccount).setPhotoRef(mediaref).setGaiaId(s2).setAlbumId(s3).setRefreshAlbumId(s3);
                    } else
                    {
                        photoviewintentbuilder.setAccount(esaccount).setPhotoUrl(dbembedmedia.getImageUrl()).setPhotoOnly(Boolean.valueOf(true));
                    }
                    startActivity(photoviewintentbuilder.build());
                } else
                {
                    Intents.viewContent(this, esaccount, dbembedmedia.getContentUrl());
                }
            } else
            if(s != null)
                startActivity(Intents.getPostCommentsActivityIntent(this, esaccount, s));
            else
            if(s1 != null)
                startActivity(Intents.getProfileActivityIntent(this, esaccount, s1, null));
            else
                Toast.makeText(this,R.string.profile_does_not_exist, 0).show();
        }
        finish();
    }

    public final void onLoaderReset(Loader loader)
    {
    }
    
    private static final class ActivityIdLoader extends EsCursorLoader {

    	private final EsAccount mAccount;
        private final Uri mStreamItemUri;

        public ActivityIdLoader(Context context, EsAccount esaccount, Uri uri)
        {
            super(context);
            mAccount = esaccount;
            mStreamItemUri = uri;
        }
        
        private byte[] loadMediaFromDatabase(ContentResolver contentresolver, String s)
        {
        	Cursor cursor = null;
        	try {
        		byte abyte0[] = null;
        		cursor = contentresolver.query(EsProvider.appendAccountParameter(Uri.withAppendedPath(EsProvider.ACTIVITY_VIEW_BY_ACTIVITY_ID_URI, s), mAccount), new String[] {
        			"embed_media"
        		}, null, null, null);
        		if(null != cursor && cursor.moveToFirst()) {
        			abyte0 = cursor.getBlob(0);
                    if(abyte0 == null) 
                    	abyte0 = new byte[0]; 
        		}
        		return abyte0;
        	} finally {
        		if(null != cursor) {
        			cursor.close();
        		}
        	}
        }

        public final Cursor esLoadInBackground()
        {
            Cursor cursor = null;
            ContentResolver contentresolver = getContext().getContentResolver();
            try {
            	String s1;
                String s2;
                String s;
            	cursor = contentresolver.query(mStreamItemUri, ViewStreamItemPhotoActivity.STREAM_ITEM_PHOTO_PROJECTION, null, null, null);
            	if(null != cursor && cursor.moveToFirst()) {
            		s1 = cursor.getString(0);
                    s2 = cursor.getString(1);
                    s = s2;
            	} else {
            		s = null;
            		s1 = null;
            	}
            	
            	byte abyte0[] = null;
                if(s != null)
                {
                    abyte0 = loadMediaFromDatabase(contentresolver, s);
                    if(abyte0 == null)
                    {
                        GetActivityOperation getactivityoperation = new GetActivityOperation(getContext(), mAccount, s, null, null, null, null);
                        getactivityoperation.start();
                        boolean flag;
                        if(getactivityoperation.getException() != null)
                        {
                            Log.e("ViewStreamItemActivity", "Cannot download activity", getactivityoperation.getException());
                            flag = false;
                        } else
                        if(getactivityoperation.hasError())
                        {
                            Log.e("ViewStreamItemActivity", (new StringBuilder("Cannot download activity: ")).append(getactivityoperation.getErrorCode()).toString());
                            flag = false;
                        } else
                        {
                            flag = true;
                        }
                        if(flag)
                            abyte0 = loadMediaFromDatabase(contentresolver, s);
                    }
                }
                EsMatrixCursor esmatrixcursor = new EsMatrixCursor(ViewStreamItemPhotoActivity.ACTIVITY_RESULT_PROJECTION);
                esmatrixcursor.addRow(new Object[] {
                    s1, s, abyte0
                });
                return esmatrixcursor;
            } finally {
        		if(null != cursor) {
        			cursor.close();
        		}
        	}
        }
    }
}
