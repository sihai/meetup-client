/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.PhotoAlbumsAdapter;
import com.galaxy.meetup.client.android.PhotosHomeGridLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.ui.activity.ProfileActivity;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 *
 */
public class HostedAlbumsFragment extends HostedEsFragment implements
		LoaderCallbacks, OnClickListener {

	private PhotoAlbumsAdapter mAdapter;
    private String mAuthkey;
    private boolean mControlPrimarySpinner;
    private int mCurrentSpinnerPosition;
    private final EsServiceListener mEsListener = new EsServiceListener() {

        public final void onGetAlbumListComplete(int i)
        {
            if(mOlderReqId != null && mOlderReqId.intValue() == i)
            {
                mOlderReqId = null;
                updateView(getView());
            }
        }

        public final void onPhotosHomeComplete(int i)
        {
            if(mOlderReqId != null && mOlderReqId.intValue() == i)
            {
                mOlderReqId = null;
                updateView(getView());
            }
        }
    };
    private Bundle mExtras;
    private String mGaiaId;
    private String mPersonId;
    private boolean mPhotosHome;
    private int mPickerMode;
    private boolean mShowLocalCameraAlbum;
    private String mUserName;

    public HostedAlbumsFragment()
    {
        mControlPrimarySpinner = true;
    }

    private void updateView(View view)
    {
        Cursor cursor = mAdapter.getCursor();
        boolean flag;
        boolean flag1;
        if(cursor != null && cursor.getCount() > 0)
            flag = true;
        else
            flag = false;
        if(mOlderReqId != null || cursor == null)
            flag1 = true;
        else
            flag1 = false;
        if(flag1 && !flag)
            showEmptyViewProgress(view);
        else
        if(flag)
            showContent(view);
        else
            showEmptyView(view, getString(R.string.no_albums));
        updateSpinner();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.ALBUMS_OF_USER;
    }

    protected final boolean isEmpty()
    {
        return mAdapter.isEmpty();
    }

    protected final boolean needsAsyncData()
    {
        return true;
    }
    
    public final void onActivityResult(int i, int j, Intent intent) {
    	if(1 == i) {
    		if(j != 0)
            {
                getActivity().setResult(j, intent);
                getActivity().finish();
            }
    	}
    }

    public void onClick(View view)
    {
        Cursor cursor = (Cursor)mAdapter.getItem(((Integer)view.getTag()).intValue());
        if(cursor != null)
        {
            String s;
            String s1;
            Object obj;
            String s2;
            String s3;
            String s4;
            int i;
            Intents.PhotosIntentBuilder photosintentbuilder;
            if(cursor.isNull(8))
                s = getResources().getString(R.string.photos_home_unknown_label);
            else
                s = cursor.getString(8);
            if(cursor.isNull(5))
                s1 = null;
            else
                s1 = cursor.getString(5);
            if(cursor.isNull(4))
                obj = null;
            else
                obj = cursor.getString(4);
            if(cursor.isNull(6))
                s2 = null;
            else
                s2 = cursor.getString(6);
            if(cursor.isNull(7))
                s3 = null;
            else
                s3 = cursor.getString(7);
            if(!TextUtils.equals(((CharSequence) (obj)), "photos_of_me"))
                s4 = null;
            else
                s4 = mGaiaId;
            i = mExtras.getInt("photo_picker_mode", 0);
            photosintentbuilder = Intents.newPhotosActivityIntentBuilder(getActivity()).setAccount(mAccount).setAlbumName(s).setAlbumId(s1).setGaiaId(s2).setStreamId(s3).setPhotoOfUserId(s4).setAlbumType(((String) (obj))).setAuthkey(mAuthkey);
            if(i != 0)
            {
                Integer integer;
                Integer integer1;
                if(mExtras.containsKey("photo_picker_crop_mode"))
                    integer = Integer.valueOf(mExtras.getInt("photo_picker_crop_mode"));
                else
                    integer = null;
                if(mExtras.containsKey("photo_picker_title"))
                    integer1 = Integer.valueOf(mExtras.getInt("photo_picker_title"));
                else
                    integer1 = null;
                photosintentbuilder.setPhotoPickerMode(Integer.valueOf(i)).setPhotoPickerTitleResourceId(integer1).setCropMode(integer);
                startActivityForResult(photosintentbuilder.build(), 1);
            } else
            {
                startActivity(photosintentbuilder.build());
            }
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mExtras = new Bundle();
            mExtras.putAll(bundle.getBundle("com.google.android.apps.plus.PhotosHomeFragment.INTENT"));
            mUserName = bundle.getString("com.google.android.apps.plus.PhotosHomeFragment.USER_NAME");
        } else
        {
            mExtras = getArguments();
        }
        mPersonId = mExtras.getString("person_id");
        mGaiaId = EsPeopleData.extractGaiaId(mPersonId);
        mPhotosHome = mExtras.getBoolean("photos_home", false);
        mShowLocalCameraAlbum = mExtras.getBoolean("photos_show_camera_album", false);
        mPickerMode = mExtras.getInt("photo_picker_mode", 0);
        mAuthkey = mExtras.getString("auth_key");
        if(mPickerMode != 0)
            invalidateActionBar();
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        return new PhotosHomeGridLoader(getActivity(), mAccount, mGaiaId, mUserName, mPhotosHome, mShowLocalCameraAlbum);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.photo_home_view, viewgroup, false);
        ColumnGridView columngridview = (ColumnGridView)view.findViewById(R.id.grid);
        ScreenMetrics screenmetrics = ScreenMetrics.getInstance(getActivity());
        int i;
        if(screenmetrics.screenDisplayType == 0)
            i = 1;
        else
            i = 2;
        columngridview.setColumnCount(i);
        columngridview.setItemMargin(screenmetrics.itemMargin);
        columngridview.setPadding(screenmetrics.itemMargin, screenmetrics.itemMargin, screenmetrics.itemMargin, screenmetrics.itemMargin);
        mAdapter = new PhotoAlbumsAdapter(getActivity(), null, columngridview, this);
        columngridview.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        columngridview.setOnClickListener(this);
        setupEmptyView(view, R.string.no_albums);
        updateView(view);
        return view;
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        mAdapter.swapCursor(cursor);
        updateView(getView());
        onAsyncData();
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mEsListener);
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        if(mControlPrimarySpinner)
            if(mPickerMode == 0)
            {
                android.widget.ArrayAdapter arrayadapter = ProfileActivity.createSpinnerAdapter(getActivity());
                mCurrentSpinnerPosition = 1;
                hostactionbar.showPrimarySpinner(arrayadapter, mCurrentSpinnerPosition);
            } else
            {
                hostactionbar.showTitle(R.string.photo_picker_photos_home_label);
            }
        hostactionbar.showRefreshButton();
        hostactionbar.showProgressIndicator();
    }

    public final void onPrimarySpinnerSelectionChange(int i) {
        if(!mControlPrimarySpinner || mCurrentSpinnerPosition == i) {
        	return; 
        }
        
        if(0 == i) {
        	startActivity(Intents.getHostedProfileIntent(getActivity(), mAccount, mPersonId, null, 0));
        }
        mCurrentSpinnerPosition = i;
        return;
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mEsListener);
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mExtras != null)
        {
            bundle.putParcelable("com.google.android.apps.plus.PhotosHomeFragment.INTENT", mExtras);
            bundle.putString("com.google.android.apps.plus.PhotosHomeFragment.USER_NAME", mUserName);
        }
    }

    public final void refresh()
    {
        super.refresh();
        if(mOlderReqId == null)
        {
            if(mPhotosHome)
                mOlderReqId = Integer.valueOf(EsService.getPhotosHome(getActivity(), mAccount, mAuthkey));
            else
                mOlderReqId = Integer.valueOf(EsService.getAlbumList(getActivity(), mAccount, mGaiaId));
            updateView(getView());
        }
    }

    public final void relinquishPrimarySpinner()
    {
        mControlPrimarySpinner = false;
    }
}
