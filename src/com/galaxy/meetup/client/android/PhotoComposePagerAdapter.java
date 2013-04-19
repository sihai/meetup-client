/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.PhotoComposeFragment;

/**
 * 
 * @author sihai
 *
 */
public class PhotoComposePagerAdapter extends FragmentStatePagerAdapter {
	
	private final EsAccount mAccount;
    private final Context mContext;
    private final MediaRefProvider mMediaRefProvider;
    private final PhotoComposeFragment.RemoveImageListener mRemoveImageListener;
    
    public PhotoComposePagerAdapter(Context context, FragmentManager fragmentmanager, EsAccount esaccount, PhotoComposeFragment.RemoveImageListener removeimagelistener, MediaRefProvider mediarefprovider)
    {
        super(fragmentmanager);
        if(mediarefprovider == null)
        {
            throw new IllegalArgumentException("MediaRefProvider was null!");
        } else
        {
            mContext = context;
            mAccount = esaccount;
            mMediaRefProvider = mediarefprovider;
            mRemoveImageListener = removeimagelistener;
            return;
        }
    }

    public final int getCount()
    {
        return mMediaRefProvider.getCount();
    }

    public final Fragment getItem(int i)
    {
        Intents.PhotoViewIntentBuilder photoviewintentbuilder = Intents.newPhotoComposeFragmentIntentBuilder(mContext);
        Object obj;
        if(i < 0 || i >= mMediaRefProvider.getCount())
        {
            obj = null;
        } else
        {
            MediaRef mediaref = mMediaRefProvider.getItem(i);
            photoviewintentbuilder.setAccount(mAccount).setPhotoRef(mediaref);
            obj = new PhotoComposeFragment();
            ((PhotoComposeFragment) (obj)).setArguments(photoviewintentbuilder.build().getExtras());
            ((PhotoComposeFragment) (obj)).setRemoveImageListener(mRemoveImageListener);
        }
        return ((Fragment) (obj));
    }

    public final int getItemPosition(Object obj)
    {
        return mMediaRefProvider.getItemPosition(obj);
    }

	public static interface MediaRefProvider {

        public abstract int getCount();

        public abstract MediaRef getItem(int i);

        public abstract int getItemPosition(Object obj);
    }
}
