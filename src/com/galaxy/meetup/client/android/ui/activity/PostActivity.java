/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import WriteReviewOperation.MediaRef;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ShakeDetector;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.DbEmotishareMetadata;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.PostFragment;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.GalleryUtils;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.ImageUtils.InsertCameraPhotoDialogDisplayer;
import com.galaxy.meetup.client.util.MediaStoreUtils;

/**
 * 
 * @author sihai
 *
 */
public class PostActivity extends EsFragmentActivity implements
		OnClickListener, InsertCameraPhotoDialogDisplayer {

	protected EsAccount mAccount;
    protected PostFragment mFragment;
    private boolean mShakeDetectorWasRunning;
    private View mShareButton;
    
    public PostActivity() {
    }

    private boolean buildPostFragment(EsAccount esaccount) {
        mAccount = esaccount;
        Bundle bundle = getPostFragmentArguments();
        boolean flag;
        if(bundle == null)
        {
            flag = false;
        } else
        {
            bundle.putParcelable("account", esaccount);
            PostFragment postfragment = new PostFragment();
            postfragment.setArguments(bundle);
            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
            fragmenttransaction.add(R.id.post_container, postfragment, "post_tag");
            fragmenttransaction.commit();
            flag = true;
        }
        return flag;
    }

    protected final EsAccount getAccount() {
        return mAccount;
    }

    protected Bundle getPostFragmentArguments() {
        boolean flag1;
        boolean flag2;
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        String s = intent.getAction();
        if(EsLog.isLoggable("PostActivity", 3))
            Log.d("PostActivity", (new StringBuilder("Intent action: ")).append(s).toString());
        bundle.putString("action", s);
        if(intent.hasExtra("android.intent.extra.TEXT"))
        {
            String s3 = intent.getStringExtra("android.intent.extra.TEXT");
            if(s3 == null)
            {
                CharSequence charsequence = intent.getCharSequenceExtra("android.intent.extra.TEXT");
                if(charsequence != null)
                    s3 = charsequence.toString();
            }
            if(EsLog.isLoggable("PostActivity", 3))
                Log.d("PostActivity", (new StringBuilder("    EXTRA_TEXT: ")).append(s3).toString());
            bundle.putString("android.intent.extra.TEXT", s3);
        }
        if(intent.hasExtra("activity_id"))
        {
            if(EsLog.isLoggable("PostActivity", 3))
                Log.d("PostActivity", (new StringBuilder("    EXTRA_ACTIVITY_ID: ")).append(intent.getStringExtra("activity_id")).toString());
            String s2 = intent.getStringExtra("activity_id");
            bundle.putString("activity_id", s2);
        }
        if(intent.hasExtra("location"))
        {
            DbLocation dblocation = (DbLocation)intent.getParcelableExtra("location");
            if(EsLog.isLoggable("PostActivity", 3))
                Log.d("PostActivity", (new StringBuilder("    EXTRA_LOCATION: ")).append(dblocation).toString());
            bundle.putParcelable("location", dblocation);
        }
        if(intent.hasExtra("typed_image_embed"))
        {
            DbEmotishareMetadata dbemotisharemetadata = (DbEmotishareMetadata)intent.getParcelableExtra("typed_image_embed");
            if(EsLog.isLoggable("PostActivity", 3))
                Log.d("PostActivity", (new StringBuilder("    EXTRA_EMOTISHARE: ")).append(dbemotisharemetadata).toString());
            bundle.putParcelable("typed_image_embed", dbemotisharemetadata);
        }
        boolean flag = intent.hasExtra("android.intent.extra.STREAM");
        flag1 = false;
        flag2 = false;
        
        int i;
        Parcelable parcelable1;
        if(flag) {
        	
	        List arraylist = new ArrayList();
	        if(!"android.intent.action.SEND_MULTIPLE".equals(s)) {
	        	Parcelable parcelable = intent.getExtras().getParcelable("android.intent.extra.STREAM");
	            if(parcelable instanceof MediaRef)
	            {
	                arraylist.add((MediaRef)parcelable);
	            } else
	            {
	                boolean flag3 = parcelable instanceof Uri;
	                flag1 = false;
	                flag2 = false;
	                if(flag3)
	                {
	                    Uri uri = (Uri)parcelable;
	                    if(MediaStoreUtils.isMediaStoreUri(uri))
	                    {
	                        arraylist.add(new MediaRef(mAccount.getGaiaId(), 0L, null, uri, MediaRef.MediaType.IMAGE));
	                        flag1 = false;
	                        flag2 = false;
	                    } else
	                    if(GalleryUtils.isGalleryContentUri(uri))
	                    {
	                        arraylist.add(new MediaRef(null, 0L, uri.toString(), null, MediaRef.MediaType.IMAGE));
	                        flag1 = false;
	                        flag2 = false;
	                    } else
	                    if("content".equals(uri.getScheme()))
	                    {
	                        arraylist.add(new MediaRef(mAccount.getGaiaId(), 0L, null, uri, MediaRef.MediaType.IMAGE));
	                        flag1 = false;
	                        flag2 = false;
	                    } else
	                    if("file".equals(uri.getScheme()))
	                    {
	                        arraylist.add(new MediaRef(mAccount.getGaiaId(), 0L, null, uri, MediaRef.MediaType.IMAGE));
	                        flag1 = false;
	                        flag2 = false;
	                    } else
	                    {
	                        flag2 = true;
	                        flag1 = true;
	                    }
	                }
	            } 
	        } else { 
	        	List arraylist1 = intent.getExtras().getParcelableArrayList("android.intent.extra.STREAM");
	            if(arraylist1.size() <= 250) {
	            	Iterator iterator = arraylist1.iterator();
	                do
	                {
	                    boolean flag4 = iterator.hasNext();
	                    flag2 = false;
	                    if(!flag4)
	                        break;
	                    Parcelable parcelable2 = (Parcelable)iterator.next();
	                    if(parcelable2 instanceof MediaRef)
	                        arraylist.add((MediaRef)parcelable2);
	                    else
	                    if(parcelable2 instanceof Uri)
	                    {
	                        Uri uri1 = (Uri)parcelable2;
	                        if(MediaStoreUtils.isMediaStoreUri(uri1))
	                            arraylist.add(new MediaRef(mAccount.getGaiaId(), 0L, null, uri1, MediaRef.MediaType.IMAGE));
	                        else
	                        if(GalleryUtils.isGalleryContentUri(uri1))
	                            arraylist.add(new MediaRef(null, 0L, uri1.toString(), null, MediaRef.MediaType.IMAGE));
	                        else
	                        if("content".equals(uri1.getScheme()))
	                            arraylist.add(new MediaRef(mAccount.getGaiaId(), 0L, null, uri1, MediaRef.MediaType.IMAGE));
	                        else
	                        if("file".equals(uri1.getScheme()))
	                            arraylist.add(new MediaRef(mAccount.getGaiaId(), 0L, null, uri1, MediaRef.MediaType.IMAGE));
	                        else
	                            flag1 = true;
	                    }
	                } while(true); 
	            } else {
	            	int j = R.string.post_max_photos;
	                Object aobj[] = new Object[1];
	                aobj[0] = Integer.valueOf(250);
	                Toast.makeText(this, getString(j, aobj), 1).show();
	                return null;
	            }
	        }
	        bundle.putParcelableArrayList("android.intent.extra.STREAM", (ArrayList)arraylist);
        }
        
        if(intent.hasExtra("insert_photo_request_id"))
        {
            i = intent.getIntExtra("insert_photo_request_id", 0);
            bundle.putInt("insert_photo_request_id", i);
        }
        if(intent.hasExtra("audience"))
        {
            parcelable1 = intent.getParcelableExtra("audience");
            bundle.putParcelable("audience", parcelable1);
        }
        if(flag1)
        {
            String s1;
            if(flag2)
                s1 = getString(R.string.post_invalid_photos_unsupported);
            else
                s1 = getString(R.string.post_invalid_photos_remote);
            Toast.makeText(this, s1, 1).show();
        }
        
        return bundle;
    }

    public OzViews getViewForLogging()
    {
        return OzViews.COMPOSE;
    }

    protected int getViewId()
    {
        return R.layout.post_activity;
    }

    public final void hideInsertCameraPhotoDialog()
    {
        dismissDialog(0x7f0a003e);
    }

    public void invalidateMenu()
    {
        if(mShareButton != null)
        {
            mShareButton.setEnabled(mFragment.canPost());
            mShareButton.invalidate();
        }
    }

    public final void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
        if(fragment instanceof PostFragment)
            mFragment = (PostFragment)fragment;
    }

    public void onBackPressed()
    {
        mFragment.onDiscard(false);
    }

    public void onClick(DialogInterface dialoginterface, int i) {
    	
    	if(-3 == i) {
    		EsAccountsData.saveLocationDialogSeenPreference(this, mAccount, true);
    	} else if(-2 == i) {
    		mFragment.setLocationChecked(false);
    	} else if(-1 == i) {
    		startActivity(Intents.getLocationSettingActivityIntent());
    	}
    }

    public void onCreate(Bundle bundle) {
    	Intent intent;
        super.onCreate(bundle);
        setContentView(getViewId());
        View view = findViewById(R.id.cancel_button);
        if(view != null)
            view.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    if(mFragment != null)
                        mFragment.onDiscard(true);
                }
            });
        
        mShareButton = findViewById(R.id.share_button);
        if(mShareButton != null)
            mShareButton.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    if(mFragment != null)
                        mFragment.post();
                }
            });
        
        EsAccount esaccount = EsService.getActiveAccount(this);
        String s;
        boolean flag;
        int i;
        Object aobj[];
        String s1;
        if(null == bundle) {
        	intent = getIntent();
        	if(intent.hasExtra("account")) {
                if(esaccount != null && esaccount.hasGaiaId()) {
                    if(!buildPostFragment(esaccount)) {
                    	finish();
                    	return;
                    }
                } else {
                    Intent intent1 = getIntent();
                    intent1.setPackage(PostActivity.class.getPackage().getName());
                    startActivity(Intents.getAccountsActivityIntent(this, intent1));
                    finish();
                    return;
                } 
                
        	} else { 
        		if(buildPostFragment((EsAccount)intent.getParcelableExtra("account"))) {
        			recordLaunchEvent();
        			ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
        	        if(shakedetector != null)
        	            mShakeDetectorWasRunning = shakedetector.stop();
        	        return;
        		} else {
        			finish();
        			return;
        		}
        	}
        	s = intent.getStringExtra("com.google.android.apps.plus.SENDER_ID");
            flag = "com.google.android.apps.plus.GOOGLE_BIRTHDAY_POST".equals(intent.getAction());
            if((flag || !TextUtils.isEmpty(s)) && !TextUtils.equals(esaccount.getGaiaId(), s))
            {
                i = R.string.share_account_warning;
                aobj = new Object[1];
                if(flag)
                    s1 = esaccount.getDisplayName();
                else
                    s1 = esaccount.getName();
                aobj[0] = s1;
                Toast.makeText(this, getString(i, aobj), 1).show();
            }
            recordLaunchEvent();
            ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
	        if(shakedetector != null)
	            mShakeDetectorWasRunning = shakedetector.stop();
	        return;
        } else {
        	// TODO
        }
    }

    public Dialog onCreateDialog(int i, Bundle bundle) {
    	
    	if(29341608 == i) {
    		android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
            builder1.setMessage(R.string.location_provider_disabled);
            builder1.setPositiveButton(R.string.yes, this);
            builder1.setNegativeButton(R.string.no, this);
            return builder1.create();
    	} else  if(30875012 == i) {
    		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(R.string.post_location_dialog_title);
            builder.setMessage(R.string.post_location_dialog_message);
            builder.setNeutralButton(0x104000a, this);
            builder.setCancelable(false);
            return builder.create();
    	} else if(2131361854 == i) {
    		return ImageUtils.createInsertCameraPhotoDialog(this);
    	} else {
    		return null;
    	}
    	
    }

    protected void onDestroy() {
        super.onDestroy();
        if(mShakeDetectorWasRunning)
        {
            ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
            if(shakedetector != null)
                shakedetector.start();
        }
    }

    protected void onResume()
    {
        super.onResume();
        if(!SignOnActivity.finishIfNoAccount(this, mAccount));
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mAccount != null)
            bundle.putParcelable("account", mAccount);
    }

    protected void recordLaunchEvent()
    {
        recordUserAction(OzActions.PLATFORM_OPEN_SHAREBOX);
    }

    public final void showInsertCameraPhotoDialog()
    {
        showDialog(0x7f0a003e);
    }
}
