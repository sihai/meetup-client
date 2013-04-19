/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import WriteReviewOperation.MediaRef;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.ActionButton;
import com.galaxy.meetup.client.android.ui.view.PhotoHeaderView;
import com.galaxy.meetup.client.android.ui.view.PhotoHeaderView.OnImageListener;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class PhotoPickerFragment extends EsFragment implements OnClickListener,
		OnImageListener {

	private static Integer sPhotoSize;
    private Integer mCoverPhotoOffset;
    private int mCropMode;
    private Intent mIntent;
    private MediaRef mPhotoRef;
    private PhotoHeaderView mPhotoView;
    
	public PhotoPickerFragment()
    {
    }

    public PhotoPickerFragment(Intent intent)
    {
        this();
        mIntent = intent;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mPhotoView != null && !mPhotoView.isPhotoBound())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if(sPhotoSize == null)
        {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((WindowManager)activity.getSystemService("window")).getDefaultDisplay().getMetrics(displaymetrics);
            sPhotoSize = Integer.valueOf(Math.max(displaymetrics.heightPixels, displaymetrics.widthPixels));
        }
    }

    public void onClick(View view)
    {
        int i = view.getId();
        if(R.id.cancel_button == i) {
        	android.support.v4.app.FragmentActivity fragmentactivity1 = getActivity();
            fragmentactivity1.setResult(1);
            fragmentactivity1.finish();
        } else if(R.id.accept_button == i)
        {
            android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
            Intent intent = new Intent();
            if(mCropMode != 0)
            {
                intent.putExtra("data", ImageUtils.compressBitmap(mPhotoView.getCroppedPhoto(), 90, false));
                if(mCropMode == 2)
                {
                    intent.putExtra("top_offset", mPhotoView.getCoverPhotoTopOffset());
                    String s1 = mPhotoRef.getOwnerGaiaId();
                    if(s1 != null && s1.equals("115239603441691718952"))
                        intent.putExtra("is_gallery_photo", true);
                }
            }
            String s;
            if(mPhotoRef.hasLocalUri())
                s = mPhotoRef.getLocalUri().toString();
            else
                s = mPhotoRef.getUrl();
            if(s != null)
                intent.putExtra("photo_url", s);
            if(mPhotoRef.getPhotoId() != 0L)
                intent.putExtra("photo_id", mPhotoRef.getPhotoId());
            fragmentactivity.setResult(-1, intent);
            fragmentactivity.finish();
        }
        
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
            mIntent = (new Intent()).putExtras(bundle.getBundle("com.google.android.apps.plus.PhotoViewFragment.INTENT"));
        mPhotoRef = (MediaRef)mIntent.getParcelableExtra("mediarefs");
        mCropMode = mIntent.getIntExtra("photo_picker_crop_mode", 0);
        if(mIntent.hasExtra("top_offset"))
            mCoverPhotoOffset = Integer.valueOf(mIntent.getIntExtra("top_offset", 0));
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle, R.layout.photo_picker_fragment);
        mPhotoView = (PhotoHeaderView)view.findViewById(R.id.photo_header_view);
        mPhotoView.init(mPhotoRef, false);
        mPhotoView.enableImageTransforms(true);
        mPhotoView.setCropMode(mCropMode);
        mPhotoView.setCropModeCoverPhotoOffset(mCoverPhotoOffset);
        mPhotoView.setOnImageListener(this);
        showEmptyViewProgress(view);
        view.findViewById(R.id.cancel_button).setOnClickListener(this);
        String s;
        ActionButton actionbutton;
        if(mCropMode != 0)
            s = getString(R.string.photo_picker_save);
        else
            s = getString(R.string.photo_picker_select);
        actionbutton = (ActionButton)view.findViewById(R.id.accept_button);
        actionbutton.setText(s.toUpperCase());
        actionbutton.setTextAppearance(getActivity(), R.style.AlbumView_BottomActionBar_ActionButton_Disabled);
        return view;
    }

    public final void onImageLoadFinished(PhotoHeaderView photoheaderview)
    {
        View view = getView();
        ActionButton actionbutton = (ActionButton)view.findViewById(R.id.accept_button);
        if(isEmpty())
        {
            actionbutton.setOnClickListener(null);
            actionbutton.setEnabled(false);
            actionbutton.setTextAppearance(getActivity(), R.style.AlbumView_BottomActionBar_ActionButton_Disabled);
            setupEmptyView(view, R.string.photo_network_error);
            showEmptyView(view);
        } else
        {
            actionbutton.setOnClickListener(this);
            actionbutton.setEnabled(true);
            actionbutton.setTextAppearance(getActivity(), R.style.AlbumView_BottomActionBar_ActionButton);
            showContent(view);
        }
    }

    public final void onImageScaled(float f)
    {
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mIntent != null)
            bundle.putParcelable("com.google.android.apps.plus.PhotoViewFragment.INTENT", mIntent.getExtras());
    }

}
