/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ChoosePhotoDialog extends DialogFragment implements
		OnClickListener {

	private Long mCoverPhotoId;
    private boolean mHasScrapbook;
    private int mIndexToAction[];
    private boolean mIsCameraSupported;
    private boolean mIsForCoverPhoto;
    private boolean mIsOnlyFromInstantUpload;
    PhotoHandler mListener;
    private int mTitle;
    
	public ChoosePhotoDialog()
    {
        mIndexToAction = new int[5];
        mTitle = R.string.menu_choose_photo_from_gallery;
    }

    public ChoosePhotoDialog(int i)
    {
        mIndexToAction = new int[5];
        mTitle = R.string.menu_choose_photo_from_gallery;
        mTitle = i;
    }
    
    public void onClick(DialogInterface dialoginterface, int i)
    {
    	if(null != mListener) {
    		dismiss();
            switch(mIndexToAction[i])
            {
            case 1: // '\001'
                mListener.doRepositionCoverPhoto();
                break;

            case 2: // '\002'
                mListener.doTakePhoto();
                break;

            case 3: // '\003'
                mListener.doPickPhotoFromAlbums(0);
                break;

            case 4: // '\004'
                mListener.doPickPhotoFromAlbums(1);
                break;

            case 5: // '\005'
                mListener.doPickPhotoFromAlbums(2);
                break;
            }
    	}
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        super.onCreateDialog(bundle);
        if(bundle != null)
        {
            mIsCameraSupported = bundle.getBoolean("is_camera_supported");
            mTitle = bundle.getInt("title");
            mIsOnlyFromInstantUpload = bundle.getBoolean("only_instant_upload");
            mIsForCoverPhoto = bundle.getBoolean("has_scrapbook");
            mHasScrapbook = bundle.getBoolean("has_scrapbook");
            if(bundle.containsKey("cover_photo_id"))
                mCoverPhotoId = Long.valueOf(bundle.getLong("cover_photo_id"));
        }
        FragmentActivity fragmentactivity = getActivity();
        int i;
        int j;
        String as[];
        Long long1;
        int k;
        int i1;
        android.app.AlertDialog.Builder builder;
        if(mIsCameraSupported)
            i = 1;
        else
            i = 0;
        j = i + 1;
        if(mIsForCoverPhoto)
        {
            j++;
            if(mHasScrapbook)
                j++;
            if(mCoverPhotoId != null)
                j++;
        }
        as = new String[j];
        long1 = mCoverPhotoId;
        k = 0;
        if(long1 != null)
        {
            mIndexToAction[0] = 1;
            k = 0 + 1;
            as[0] = fragmentactivity.getString(R.string.change_photo_option_reposition);
        }
        if(mIsForCoverPhoto)
        {
            mIndexToAction[k] = 4;
            int l1 = k + 1;
            as[k] = fragmentactivity.getString(R.string.change_photo_option_select_gallery);
            k = l1;
        }
        if(mIsCameraSupported)
        {
            mIndexToAction[k] = 2;
            int k1 = k + 1;
            as[k] = fragmentactivity.getString(R.string.change_photo_option_take_photo);
            k = k1;
        }
        mIndexToAction[k] = 3;
        if(mIsOnlyFromInstantUpload)
        {
            int j1 = k + 1;
            as[k] = fragmentactivity.getString(R.string.change_photo_option_instant_upload);
            i1 = j1;
        } else
        {
            int l = k + 1;
            as[k] = fragmentactivity.getString(R.string.change_photo_option_your_photos);
            i1 = l;
        }
        if(mIsForCoverPhoto && mHasScrapbook)
        {
            mIndexToAction[i1] = 5;
            as[i1] = fragmentactivity.getString(R.string.change_photo_option_select_cover_photo);
        }
        if(getTargetFragment() instanceof PhotoHandler)
            mListener = (PhotoHandler)getTargetFragment();
        builder = new android.app.AlertDialog.Builder(fragmentactivity);
        builder.setTitle(mTitle);
        builder.setAdapter(new ArrayAdapter(fragmentactivity, 0x1090011, as), this);
        builder.setCancelable(true);
        return builder.create();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("is_camera_supported", mIsCameraSupported);
        bundle.putInt("title", mTitle);
        bundle.putBoolean("only_instant_upload", mIsOnlyFromInstantUpload);
        bundle.putBoolean("has_scrapbook", mIsForCoverPhoto);
        bundle.putBoolean("has_scrapbook", mHasScrapbook);
        if(mCoverPhotoId != null)
            bundle.putLong("cover_photo_id", mCoverPhotoId.longValue());
    }

    public final void setIsCameraSupported(boolean flag)
    {
        mIsCameraSupported = flag;
    }

    public final void setIsForCoverPhoto(boolean flag, boolean flag1, Long long1)
    {
        mIsForCoverPhoto = true;
        mHasScrapbook = flag1;
        mCoverPhotoId = long1;
    }

    public final void show(FragmentManager fragmentmanager, String s) {
    	if(mIsCameraSupported && !mIsForCoverPhoto) {
    		super.show(fragmentmanager, s);
    		return;
    	}
    	 if(mListener != null)
             mListener.doPickPhotoFromAlbums(0);
    }

	public static interface PhotoHandler {

        void doPickPhotoFromAlbums(int i);

        void doRepositionCoverPhoto();

        void doTakePhoto();
    }
}
