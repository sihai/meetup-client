/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import WriteReviewOperation.MediaRef;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.view.PhotoHeaderView;
import com.galaxy.meetup.client.android.ui.view.PhotoHeaderView.OnImageListener;

/**
 * 
 * @author sihai
 *
 */
public class PhotoComposeFragment extends HostedFragment implements
		LoaderCallbacks, OnClickListener, OnImageListener {
	
	private EsAccount mAccount;
    private PhotoHeaderView mBackgroundView;
    private ImageButton mDeleteButton;
    private MediaRef mMediaRef;
    private RemoveImageListener mRemoveImageListener;
    
    public PhotoComposeFragment()
    {
    }

    public final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTO;
    }

    public void onClick(View view)
    {
        int i = view.getId();
        if(R.id.remove_image_button == i) {
        	if(mRemoveImageListener != null)
                mRemoveImageListener.onImageRemoved(mMediaRef);
        }
        if(i == R.id.background)
            if(mBackgroundView.isVideo())
            {
                if(mBackgroundView.isVideoReady())
                {
                    startActivity(Intents.getVideoViewActivityIntent(getActivity(), mAccount, mMediaRef.getOwnerGaiaId(), mMediaRef.getPhotoId(), mBackgroundView.getVideoData()));
                } else
                {
                    String s = getString(R.string.photo_view_video_not_ready);
                    Toast.makeText(getActivity(), s, 1).show();
                }
            } else
            if(mBackgroundView.isPanorama())
                startActivity(Intents.getViewPanoramaActivityIntent(getActivity(), mAccount, mMediaRef));
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Bundle bundle1 = getArguments();
        mAccount = (EsAccount)bundle1.getParcelable("account");
        mMediaRef = (MediaRef)bundle1.getParcelable("photo_ref");
        if(mMediaRef == null)
            getActivity().finish();
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        return new VideoDataLoader(getSafeContext(), mAccount, mMediaRef.getUrl(), mMediaRef.getPhotoId(), mMediaRef.getLocalUri());
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.photo_compose_fragment, viewgroup, false);
        mDeleteButton = (ImageButton)view.findViewById(R.id.remove_image_button);
        mDeleteButton.setOnClickListener(this);
        if(view.findViewById(R.id.stage) == null && mMediaRef != null)
        {
            View view1 = ((ViewStub)view.findViewById(R.id.stage_media)).inflate();
            view1.findViewById(R.id.loading).setVisibility(0);
            mBackgroundView = (PhotoHeaderView)view1.findViewById(R.id.background);
            mBackgroundView.init(mMediaRef, false);
            mBackgroundView.setOnClickListener(this);
            mBackgroundView.setOnImageListener(this);
            view1.invalidate();
        }
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    public final void onImageLoadFinished(PhotoHeaderView photoheaderview)
    {
        getView().findViewById(R.id.loading).setVisibility(8);
    }

    public final void onImageScaled(float f)
    {
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(cursor != null && cursor.moveToFirst())
        {
            byte abyte0[];
            if(cursor.isNull(0))
                abyte0 = null;
            else
                abyte0 = cursor.getBlob(0);
            mBackgroundView.setVideoBlob(abyte0);
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void setRemoveImageListener(RemoveImageListener removeimagelistener)
    {
        mRemoveImageListener = removeimagelistener;
    }
	
	public static interface RemoveImageListener {

        public abstract void onImageRemoved(MediaRef mediaref);
    }
}
