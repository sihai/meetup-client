/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import WriteReviewOperation.MediaRef;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.AlbumGridViewAdapter;
import com.galaxy.meetup.client.android.AlbumViewLoader;
import com.galaxy.meetup.client.android.CameraAlbumLoader;
import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.Pageable;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.StreamAdapter.ViewUseListener;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.HostActionBar.OnDoneButtonClickListener;
import com.galaxy.meetup.client.android.ui.view.PhotoAlbumView;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 *
 */
public class HostedPhotosFragment extends HostedEsFragment implements
		LoaderCallbacks, OnClickListener, OnLongClickListener,
		AlertDialogListener, AlbumGridViewAdapter.ViewUseListener, Pageable.LoadingListener, OnDoneButtonClickListener {

	private ActionMode mActionMode;
    private android.view.ActionMode.Callback mActionModeCallback;
    private AlbumGridViewAdapter mAdapter;
    private int mAlbumCount;
    private String mAlbumId;
    private String mAlbumName;
    private String mAlbumType;
    private PhotoAlbumView mAlbumView;
    private String mAuthkey;
    private int mCount;
    private DateFormat mDateFormat;
    private Integer mDeleteReqId;
    private final EsServiceListener mEsListener = new EsServiceListener() {

        public final void onDeletePhotosComplete(int i, ServiceResult serviceresult)
        {
            handlePhotoDelete(i, serviceresult);
        }

        public final void onGetAlbumComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

        public final void onGetPhotosOfUserComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

        public final void onGetStreamPhotosComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

        public final void onLocalPhotoDelete(int i, ArrayList arraylist, ServiceResult serviceresult)
        {
            handlePhotoDelete(i, serviceresult);
            getLoaderManager().restartLoader(1, null, HostedPhotosFragment.this);
        }

        public final void onReadEventComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

    };
    
    private String mEventId;
    private int mExcludedCount;
    private final ArrayList mExcludedPhotoMediaRefs = new ArrayList();
    private Bundle mExtras;
    private ColumnGridView mGridView;
    private boolean mHereFromNotification;
    private long mLastNotificationTime;
    private boolean mLoaderActive;
    private String mNotificationId;
    private String mOwnerId;
    private Pageable mPageableLoader;
    private String mPhotoOfUserId;
    private int mPickerMode;
    private boolean mPickerShareWithZeroSelected;
    private int mPickerTitleResourceId;
    private Integer mRefreshReqId;
    private boolean mRefreshable;
    private final HashSet mSelectedPhotoMediaRefs = new HashSet();
    private String mStreamId;
    private boolean mTakePhoto;
    private boolean mTakeVideo;
    
	
    public HostedPhotosFragment()
    {
        mAlbumCount = -1;
        mDateFormat = DateFormat.getDateInstance(2);
    }

    private void handlePhotoDelete(int i, ServiceResult serviceresult)
    {
        if(mDeleteReqId != null && mDeleteReqId.intValue() == i)
        {
            mDeleteReqId = null;
            if(serviceresult != null && serviceresult.hasError())
            {
                String s = getResources().getString(R.string.remove_photo_error);
                Toast.makeText(getActivity(), s, 0).show();
            }
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("progress_dialog");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            updatePickerMode(0);
        }
    }

    private void handleServiceCallback(int i, ServiceResult serviceresult)
    {
        if(mRefreshReqId != null && mRefreshReqId.intValue() == i)
        {
            mRefreshReqId = null;
            if(serviceresult != null && serviceresult.hasError())
            {
                String s = getResources().getString(R.string.refresh_photo_album_error);
                Toast.makeText(getActivity(), s, 0).show();
            }
            updateView(getView());
        }
    }

    private void invalidateContextualActionBar()
    {
        if(mActionMode != null)
            mActionMode.invalidate();
        else
            invalidateActionBar();
    }

    private boolean isInstantUploadAlbum()
    {
        return TextUtils.equals(mAlbumType, "from_my_phone");
    }

    private boolean isLocalCameraAlbum()
    {
        return TextUtils.equals(mAlbumType, "camera_photos");
    }

    private void loadAlbumName()
    {
        boolean flag;
        boolean flag1;
        boolean flag2;
        if(mOwnerId != null && mAlbumId != null)
            flag = true;
        else
            flag = false;
        flag1 = TextUtils.isEmpty(mAlbumName);
        if(mAlbumCount == -1)
            flag2 = true;
        else
            flag2 = false;
        if(flag && (flag1 || flag2))
            getLoaderManager().restartLoader(2, null, this);
        else
            invalidateActionBar();
    }

    private void showDeleteConfirmationDialog()
    {
        Resources resources = getResources();
        int i = mSelectedPhotoMediaRefs.size();
        int j;
        AlertFragmentDialog alertfragmentdialog;
        if(isLocalCameraAlbum())
            j = R.plurals.delete_local_photo_dialog_message;
        else
            j = R.plurals.delete_remote_photo_dialog_message;
        alertfragmentdialog = AlertFragmentDialog.newInstance(resources.getQuantityString(R.plurals.delete_photo_dialog_title, i), resources.getQuantityString(j, i), resources.getQuantityString(R.plurals.delete_photo, i), getString(R.string.cancel));
        alertfragmentdialog.setTargetFragment(this, 0);
        alertfragmentdialog.show(getFragmentManager(), "delete_dialog");
    }

    private void updatePickerMode(int i) {
        mPickerMode = i;
        switch(mPickerMode) {
        case 0:
        	mSelectedPhotoMediaRefs.clear();
            if(mGridView.isInSelectionMode())
                mGridView.endSelectionMode();
        	break;
        case 2:
        	if(!mGridView.isInSelectionMode())
                mGridView.startSelectionMode();
        	break;
        case 3:
        	if(!mGridView.isInSelectionMode())
                mGridView.startSelectionMode();
            if(android.os.Build.VERSION.SDK_INT >= 11 && mActionMode == null) {
                if(mActionModeCallback == null)
                    mActionModeCallback = new android.view.ActionMode.Callback() {

                        public final boolean onActionItemClicked(ActionMode actionmode, MenuItem menuitem) {
                            int j = menuitem.getItemId();
                            if(j == R.id.reshare) {
                                if(mSelectedPhotoMediaRefs.size() > 0)
                                    shareSelectedPhotos();
                                mActionMode.finish();
                            } else {
                                if(j == R.id.delete_photos)
                                	showDeleteConfirmationDialog();
                                else 
                                	return false;
                            }
                            return true;
                        }

                        public final boolean onCreateActionMode(ActionMode actionmode, Menu menu) {
                            actionmode.getMenuInflater().inflate(R.menu.photos_cab_menu, menu);
                            return true;
                        }

                        public final void onDestroyActionMode(ActionMode actionmode) {
                            mActionMode = null;
                            updatePickerMode(0);
                        }

                        public final boolean onPrepareActionMode(ActionMode actionmode, Menu menu) {
                            if(mPickerMode == 0) {
                                mActionMode.finish();
                            } else {
                                Resources resources = getActivity().getResources();
                                int j = mSelectedPhotoMediaRefs.size();
                                int k = R.plurals.from_your_phone_selected_count;
                                Object aobj[] = new Object[1];
                                aobj[0] = Integer.valueOf(j);
                                actionmode.setTitle(resources.getQuantityString(k, j, aobj));
                                MenuItem menuitem = menu.findItem(R.id.reshare);
                                MenuItem menuitem1 = menu.findItem(R.id.delete_photos);
                                if(j == 0) {
                                    menuitem.setVisible(false);
                                    menuitem1.setVisible(false);
                                } else {
                                    menuitem.setVisible(true);
                                    menuitem1.setVisible(true);
                                    menuitem1.setTitle(resources.getQuantityString(R.plurals.delete_photo, j));
                                }
                            }
                            return true;
                        }

                    };
                mActionMode = getActivity().startActionMode(mActionModeCallback);
            }
        	break;
        case 1:
        default:
        	break;
        }
        invalidateContextualActionBar();
        return;
    }

    private void updateView(View view) {
    	
    	if(null == view) {
    		return;
    	}
    	
    	boolean flag;
        Cursor cursor = mAdapter.getCursor();
        boolean flag1;
        if(cursor != null && cursor.getCount() > 0)
            flag = true;
        else
            flag = false;
        if(mRefreshReqId != null || cursor == null)
            flag1 = true;
        else
            flag1 = false;
        if(flag1 && !flag) {
        	showEmptyViewProgress(view);
        } else {
        	if(flag)
            {
                showContent(view);
            } else
            {
                boolean flag2;
                int i;
                if(mExcludedPhotoMediaRefs.size() > 0)
                    flag2 = true;
                else
                    flag2 = false;
                if(flag2)
                    i = R.string.no_photos_left;
                else
                    i = R.string.no_photos;
                showEmptyView(view, getString(i));
            }
        }
        updateSpinner();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTOS_HOME;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mAdapter == null)
            flag = true;
        else
            flag = mAdapter.isEmpty();
        return flag;
    }

    protected final boolean isProgressIndicatorVisible()
    {
        boolean flag;
        if(mRefreshReqId != null || mLoaderActive || super.isProgressIndicatorVisible())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onActionButtonClicked(int i) {
    	if(1 == i) {
    		if(mPickerMode == 3 || mHereFromNotification)
            {
                shareSelectedPhotos();
            } else
            {
                FragmentActivity fragmentactivity = getActivity();
                Intent intent = new Intent();
                intent.putExtra("mediarefs", new ArrayList(mSelectedPhotoMediaRefs));
                fragmentactivity.setResult(-1, intent);
                fragmentactivity.finish();
            }
    	} else if(2 == i) {
    		recordUserAction(OzActions.COMPOSE_TAKE_PHOTO);
            getActivity();
            startActivityForResult(Intents.getCameraIntentPhoto("camera-p.jpg"), 2);
    	} else if(3 == i) {
    		getActivity();
            startActivityForResult(Intents.getCameraIntentVideo(), 3);
    	}
    	
    }

    public final void onActivityResult(int i, int j, Intent intent) {
    	if(1 == i) {
    		if(j != 0)
            {
                getActivity().setResult(j, intent);
                getActivity().finish();
            }
    	} else if(2 == i) {
    		FragmentActivity fragmentactivity1 = getActivity();
            if(j == -1)
            {
                Integer integer = EsService.insertCameraPhoto(getSafeContext(), mAccount, "camera-p.jpg");
                Intent intent2 = new Intent();
                intent2.putExtra("insert_photo_request_id", integer);
                intent2.putExtra("media_taken", true);
                intent2.putExtra("mediarefs", new ArrayList(mSelectedPhotoMediaRefs));
                fragmentactivity1.setResult(-1, intent2);
            }
            fragmentactivity1.finish();
    	} else if(3 == i) {
    		FragmentActivity fragmentactivity = getActivity();
            if(intent != null && j == -1)
            {
                Intent intent1 = new Intent();
                MediaRef mediaref = new MediaRef(mAccount.getGaiaId(), 0L, null, intent.getData(), MediaRef.MediaType.VIDEO);
                mSelectedPhotoMediaRefs.add(mediaref);
                intent1.putExtra("mediarefs", new ArrayList(mSelectedPhotoMediaRefs));
                intent1.putExtra("media_taken", true);
                intent1.removeExtra("insert_photo_request_id");
                fragmentactivity.setResult(-1, intent1);
            }
            fragmentactivity.finish();
    	}
    	
    }

    public void onClick(View view) {
        MediaRef amediaref[];
        String s;
        String s1;
        String s2;
        int i;
        int j;
        int k;
        Cursor cursor;
        long l;
        String s3;
        String s4;
        boolean flag;
        MediaRef.MediaType mediatype;
        MediaRef mediaref;
        if(mExtras.containsKey("mediarefs"))
            amediaref = (MediaRef[])mExtras.getParcelableArray("mediarefs");
        else
            amediaref = null;
        if(mExtras.containsKey("album_id"))
            s = mExtras.getString("album_id");
        else
            s = null;
        if(mExtras.containsKey("stream_id"))
            s1 = mExtras.getString("stream_id");
        else
            s1 = null;
        if(mExtras.containsKey("photos_of_user_id"))
            s2 = mExtras.getString("photos_of_user_id");
        else
            s2 = null;
        i = mExtras.getInt("photo_picker_mode");
        j = mExtras.getInt("photo_picker_crop_mode", 0);
        k = ((Integer)view.getTag(R.id.tag_position)).intValue();
        cursor = mAdapter.getCursor();
        cursor.moveToPosition(k);
        l = cursor.getLong(8);
        s3 = cursor.getString(5);
        s4 = cursor.getString(9);
        if(cursor.getInt(12) != 0)
            flag = true;
        else
            flag = false;
        if(flag)
            mediatype = MediaRef.MediaType.PANORAMA;
        else
        if(!cursor.isNull(11))
            mediatype = MediaRef.MediaType.VIDEO;
        else
            mediatype = MediaRef.MediaType.IMAGE;
        if(TextUtils.equals(mAlbumType, "camera_photos") && amediaref == null)
        {
            MediaRef mediaref1 = new MediaRef(s3, l, null, Uri.parse(s4), mediatype);
            amediaref = (new MediaRef[] {
                mediaref1
            });
            mediaref = mediaref1;
        } else
        {
            mediaref = new MediaRef(s3, l, s4, null, mediatype);
        }
        if(i != 0)
        {
            String s5 = cursor.getString(7);
            startActivityForResult(Intents.getPhotoPickerIntent(getActivity(), mAccount, s5, mediaref, j), 1);
        } else
        {
            Loader loader = getLoaderManager().getLoader(1);
            int i1;
            Intents.PhotoViewIntentBuilder photoviewintentbuilder;
            if(loader instanceof Pageable)
                i1 = ((Pageable)loader).getCurrentPage();
            else
                i1 = -1;
            photoviewintentbuilder = Intents.newPhotoViewActivityIntentBuilder(getActivity());
            photoviewintentbuilder.setAccount(mAccount).setGaiaId(s3).setMediaRefs(amediaref).setAlbumName(mAlbumName).setAlbumId(s).setStreamId(s1).setPhotoOfUserId(s2).setEventId(mEventId).setPhotoRef(mediaref).setPageHint(Integer.valueOf(i1));
            if(mHereFromNotification && !TextUtils.isEmpty(mNotificationId))
                photoviewintentbuilder.setNotificationId(mNotificationId);
            startActivity(photoviewintentbuilder.build());
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Integer integer;
        boolean flag;
        if(bundle != null)
        {
            mExtras = new Bundle();
            mExtras.putAll(bundle.getBundle("INTENT"));
            if(bundle.containsKey("ALBUM_NAME"))
                mAlbumName = bundle.getString("ALBUM_NAME");
            boolean flag1 = bundle.containsKey("STATE_PICKER_MODE");
            integer = null;
            if(flag1)
            {
                integer = Integer.valueOf(bundle.getInt("STATE_PICKER_MODE"));
                mPickerTitleResourceId = bundle.getInt("STATE_PICKER_TITLE");
                mPickerShareWithZeroSelected = bundle.getBoolean("STATE_PICKER_SHARE_ON_ZERO");
            }
            if(bundle.containsKey("SELECTED_ITEMS"))
            {
                android.os.Parcelable aparcelable1[] = bundle.getParcelableArray("SELECTED_ITEMS");
                for(int k = 0; k < aparcelable1.length; k++)
                    mSelectedPhotoMediaRefs.add((MediaRef)aparcelable1[k]);

            }
            if(bundle.containsKey("refresh_request"))
                mRefreshReqId = Integer.valueOf(bundle.getInt("refresh_request"));
            if(bundle.containsKey("delete_request"))
                mDeleteReqId = Integer.valueOf(bundle.getInt("delete_request"));
            if(bundle.containsKey("loader_active"))
                mLoaderActive = bundle.getBoolean("loader_active");
        } else
        {
            mExtras = getArguments();
            mLastNotificationTime = EsAccountsData.queryLastPhotoNotificationTimestamp(getActivity(), mAccount);
            long i = mLastNotificationTime - 0L;
            integer = null;
            if(i < 0)
            {
                mLastNotificationTime = System.currentTimeMillis() - 0xdbba00L;
                integer = null;
            }
        }
        if(mExtras.containsKey("owner_id"))
            mOwnerId = mExtras.getString("owner_id");
        if(mExtras.containsKey("album_name") && mAlbumName == null)
            mAlbumName = mExtras.getString("album_name");
        if(mExtras.containsKey("album_id"))
            mAlbumId = mExtras.getString("album_id");
        if(mExtras.containsKey("auth_key"))
            mAuthkey = mExtras.getString("auth_key");
        if(mExtras.containsKey("album_type"))
            mAlbumType = mExtras.getString("album_type");
        if(mExtras.containsKey("stream_id"))
            mStreamId = mExtras.getString("stream_id");
        if(mExtras.containsKey("event_id"))
            mEventId = mExtras.getString("event_id");
        if(mExtras.containsKey("photos_of_user_id"))
            mPhotoOfUserId = mExtras.getString("photos_of_user_id");
        mNotificationId = mExtras.getString("notif_id");
        if(!TextUtils.isEmpty(mNotificationId))
            flag = true;
        else
            flag = false;
        mHereFromNotification = flag;
        if(integer == null && mExtras.containsKey("photo_picker_mode"))
            integer = Integer.valueOf(mExtras.getInt("photo_picker_mode", 0));
        if(integer != null)
            mPickerMode = integer.intValue();
        if(mExtras.containsKey("photo_picker_title"))
            mPickerTitleResourceId = mExtras.getInt("photo_picker_title");
        if(mExtras.containsKey("photo_picker_share_on_zero"))
            mPickerShareWithZeroSelected = mExtras.getBoolean("photo_picker_share_on_zero");
        if(mExtras.containsKey("take_photo"))
            mTakePhoto = mExtras.getBoolean("take_photo");
        if(mExtras.containsKey("take_video"))
            mTakeVideo = mExtras.getBoolean("take_video");
        if(mExtras.containsKey("photo_picker_selected"))
        {
            android.os.Parcelable aparcelable[] = mExtras.getParcelableArray("photo_picker_selected");
            for(int j = 0; j < aparcelable.length; j++)
                mExcludedPhotoMediaRefs.add((MediaRef)aparcelable[j]);

        }
        loadAlbumName();
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	
    	Loader loader = null;
    	if(1 == i) {
    		int j = mExcludedPhotoMediaRefs.size();
            MediaRef amediaref[] = null;
            if(j > 0)
                amediaref = (MediaRef[])mExcludedPhotoMediaRefs.toArray(new MediaRef[mExcludedPhotoMediaRefs.size()]);
            if(isLocalCameraAlbum())
            	loader = new CameraAlbumLoader(getActivity(), mAccount, amediaref);
            else
            	loader = new AlbumViewLoader(getActivity(), mAccount, mOwnerId, mAlbumId, mPhotoOfUserId, mStreamId, mEventId, mAuthkey, amediaref);
            mPageableLoader = (Pageable)loader;
            mPageableLoader.setLoadingListener(this);
    	} else if(2 == i) {
    		 Uri uri = EsProvider.appendAccountParameter(Uri.withAppendedPath(Uri.withAppendedPath(EsProvider.ALBUM_VIEW_BY_ALBUM_AND_OWNER_URI, mAlbumId), mOwnerId), mAccount);
    		 loader = new EsCursorLoader(getActivity(), uri, AlbumDetailsQuery.PROJECTION, null, null, null);
    	}
    	return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        mAlbumView = (PhotoAlbumView)layoutinflater.inflate(R.layout.hosted_album_view, viewgroup, false);
        mGridView = (ColumnGridView)mAlbumView.findViewById(R.id.grid);
        ScreenMetrics screenmetrics = ScreenMetrics.getInstance(getActivity());
        mGridView.setItemMargin(screenmetrics.itemMargin);
        mGridView.setPadding(screenmetrics.itemMargin, screenmetrics.itemMargin, screenmetrics.itemMargin, screenmetrics.itemMargin);
        mAdapter = new AlbumGridViewAdapter(getActivity(), null, mAlbumType, mGridView, this, this, this);
        mAdapter.setSelectedMediaRefs(mSelectedPhotoMediaRefs);
        if(mExtras.getInt("photo_picker_crop_mode", 0) == 2)
            mAdapter.setStateFilter(new AlbumGridViewAdapter.StateFilter() {

                public final int getState(int i)
                {
                    int j;
                    if(i < 470)
                        j = 1;
                    else
                        j = 0;
                    return j;
                }
            });
        mGridView.setAdapter(mAdapter);
        mGridView.setSelector(R.drawable.list_selected_holo);
        getLoaderManager().initLoader(1, null, this);
        updatePickerMode(mPickerMode);
        updateView(mAlbumView);
        setupEmptyView(mAlbumView, R.string.no_photos);
        mGridView.setOnScrollListener(new ColumnGridView.OnScrollListener() {

        	int mCachedFirstVisibleIndex = -1;
        	
            public final void onScroll(ColumnGridView columngridview, int i, int j, int k, int l, int i1) {
                if(k != 0 && mAdapter != null) {
                	int j1 = i + j;
                    if(mCachedFirstVisibleIndex != j1)
                    {
                        int k1 = Math.min(j1 + columngridview.getColumnCount(), l - 1);
                        long l1 = mAdapter.getTimestampForItem(k1);
                        mAlbumView.setDate(mDateFormat.format(Long.valueOf(l1)));
                        mCachedFirstVisibleIndex = j1;
                    }
                }
            }

            public final void onScrollStateChanged(ColumnGridView columngridview, int i) {
            	
            	int j;
                if(i == 0) { 
                	j = 8; 
                } else {
                	Cursor cursor = mAdapter.getCursor();
                    boolean flag;
                    if(cursor != null && cursor.getCount() > 0)
                        flag = true;
                    else
                        flag = false;
                    if(!flag) 
                    	j = 8; 
                    else 
                    	j = 0;
                }
                mAlbumView.setDateVisibility(j);
            }
        });
        mGridView.registerSelectionListener(new ColumnGridView.ItemSelectionListener() {

            public final void onItemDeselected(View view, int i)
            {
                MediaRef mediaref = null;
                if(view != null)
                    mediaref = (MediaRef)view.getTag();
                if(mediaref == null)
                    mediaref = mAdapter.getMediaRefForItem(i);
                mSelectedPhotoMediaRefs.remove(mediaref);
                invalidateContextualActionBar();
            }

            public final void onItemSelected(View view, int i)
            {
                MediaRef mediaref = null;
                if(view != null)
                    mediaref = (MediaRef)view.getTag();
                if(mediaref == null)
                {
                    mediaref = mAdapter.getMediaRefForItem(i);
                    if(view != null)
                        view.setTag(mediaref);
                }
                mSelectedPhotoMediaRefs.add(mediaref);
                invalidateContextualActionBar();
            }
        });
        mAlbumView.enableDateDisplay(true);
        return mAlbumView;
    }

    public final void onDataSourceLoading(boolean flag)
    {
        mLoaderActive = flag;
        updateSpinner();
    }

    public final void onDestroyView()
    {
        super.onDestroyView();
        mGridView.unregisterSelectionListener();
        mGridView.setOnScrollListener(null);
    }

    public final void onDialogCanceled(String s)
    {
    }

    public final void onDialogListClick(int i, Bundle bundle)
    {
    }

    public final void onDialogNegativeClick(String s)
    {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s) {
        if(!"delete_dialog".equals(s)) 
        	return;
        
        ArrayList arraylist;
        String s1;
        arraylist = new ArrayList();
        s1 = mAccount.getGaiaId();
        if(isLocalCameraAlbum()) {
        	 ArrayList arraylist1 = new ArrayList(mSelectedPhotoMediaRefs);
             mDeleteReqId = Integer.valueOf(EsService.deleteLocalPhotos(getActivity(), arraylist1));
             ProgressFragmentDialog.newInstance(null, getResources().getQuantityString(R.plurals.delete_photo_pending, mSelectedPhotoMediaRefs.size())).show(getFragmentManager(), "progress_dialog");
             return;
        }
        
        MediaRef mediaref;
        for(Iterator iterator = mSelectedPhotoMediaRefs.iterator(); iterator.hasNext();) {
        	mediaref = (MediaRef)iterator.next();
        	if(mediaref.hasPhotoId() && s1.equals(mediaref.getOwnerGaiaId())) {
        		arraylist.add(Long.valueOf(mediaref.getPhotoId()));
        	}
        }
        mDeleteReqId = Integer.valueOf(EsService.deletePhotos(getActivity(), mAccount, s1, arraylist));
        ProgressFragmentDialog.newInstance(null, getResources().getQuantityString(R.plurals.delete_photo_pending, mSelectedPhotoMediaRefs.size())).show(getFragmentManager(), "progress_dialog");
        return;
    }

    public final void onDoneButtonClick()
    {
        updatePickerMode(0);
    }

    public final void onLoadFinished(Loader loader, Object obj) {
        Cursor cursor = (Cursor)obj;
        int id = loader.getId();
        if(1 == id) {
        	mAdapter.swapCursor(cursor);
            updateView(getView());
            loadAlbumName();
            mExcludedCount = 0;
            if(!(mPageableLoader instanceof CameraAlbumLoader)) {
            	if(mPageableLoader instanceof AlbumViewLoader)
                    mExcludedCount = ((AlbumViewLoader)mPageableLoader).getExcludedCount(); 
            } else { 
            	mExcludedCount = ((CameraAlbumLoader)mPageableLoader).getExcludedCount();
            }
            
            if(cursor.getCount() == 0 && mExcludedCount == 0)
                refresh();
            int j = mExtras.getInt("photo_picker_crop_mode", 0);
            TextView textview = (TextView)getView().findViewById(R.id.message);
            textview.setVisibility(8);
            if(j == 2 && mAdapter.isAnyPhotoDisabled())
            {
                textview.setText(R.string.photo_picker_album_message_cover_photo);
                textview.setVisibility(0);
            }
            if(!mHereFromNotification || mLastNotificationTime <= 0L)
                return;
            long l = mLastNotificationTime;
            long l1 = mAdapter.getTimestampForItem(0);
            if(l1 <= l)
            	return;
            EsAccountsData.saveLastPhotoNotificationTimestamp(getActivity(), mAccount, l1);
            mLastNotificationTime = 0L;
            mSelectedPhotoMediaRefs.clear();
            mSelectedPhotoMediaRefs.add(mAdapter.getMediaRefForItem(0));
            int k = cursor.getCount();
            for(int i1 = 1; i1 < k && mAdapter.getTimestampForItem(i1) > l; i1++)
                mSelectedPhotoMediaRefs.add(mAdapter.getMediaRefForItem(i1));
            
        } else if(2 == id) {
        	if(cursor != null && cursor.moveToFirst())
            {
                String s = cursor.getString(0);
                if(mAlbumName == null)
                {
                    mAlbumName = s;
                    invalidateActionBar();
                }
                if(mAlbumCount == -1)
                {
                    int i;
                    if(cursor.isNull(1))
                        i = -2;
                    else
                        i = cursor.getInt(1);
                    mAlbumCount = i;
                }
            }
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public boolean onLongClick(View view)
    {
        boolean flag;
        if(mPickerMode == 0 && (isLocalCameraAlbum() || isInstantUploadAlbum()))
        {
            updatePickerMode(3);
            int i = ((Integer)view.getTag(R.id.tag_position)).intValue();
            mGridView.select(i);
            flag = true;
        } else
        {
            flag = false;
        }
        return flag;
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        int i = menuitem.getItemId();
        if(i == R.id.select_item)
        {
            updatePickerMode(3);
        } else
        {
            if(i != R.id.delete_photos)
                return false;
            showDeleteConfirmationDialog();
        }
        return true;
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mEsListener);
        mPageableLoader.setLoadingListener(null);
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        super.onPrepareActionBar(hostactionbar);
        hostactionbar.setOnDoneButtonClickListener(this);
        if(1 == mPickerMode) {
        	hostactionbar.showTitle(mPickerTitleResourceId);
            if(!isLocalCameraAlbum())
                hostactionbar.showRefreshButton();
        } else if(2 == mPickerMode) {
        	int k = mSelectedPhotoMediaRefs.size();
            if(mTakeVideo && k == 0)
                hostactionbar.showActionButton(3, R.drawable.icn_add_video, R.string.post_take_video_button);
            if(mTakePhoto && k == 0)
                hostactionbar.showActionButton(2, R.drawable.icn_events_add_photo, R.string.post_take_photo_button);
            if(k > 0 || mPickerShareWithZeroSelected)
            {
                Resources resources1 = getResources();
                int l = R.plurals.from_your_phone_selected_count;
                Object aobj1[] = new Object[1];
                aobj1[0] = Integer.valueOf(k);
                hostactionbar.showTitle(resources1.getQuantityString(l, k, aobj1));
                hostactionbar.showActionButton(1, R.drawable.ic_actionbar_reshare, R.string.from_your_phone_initiate_share);
            } else
            {
                hostactionbar.showTitle(mPickerTitleResourceId);
            }
        } else if(3 == mPickerMode) {
        	 hostactionbar.startContextActionMode();
             int i = mSelectedPhotoMediaRefs.size();
             Resources resources = getResources();
             int j = R.plurals.from_your_phone_selected_count;
             Object aobj[] = new Object[1];
             aobj[0] = Integer.valueOf(i);
             hostactionbar.showTitle(resources.getQuantityString(j, i, aobj));
             if(i > 0)
                 hostactionbar.showActionButton(1, R.drawable.ic_actionbar_reshare, R.string.from_your_phone_initiate_share);
        } else {
        	hostactionbar.finishContextActionMode();
            hostactionbar.showTitle(mAlbumName);
            if(!isLocalCameraAlbum())
                hostactionbar.showRefreshButton();
        }
        
    }

    public final void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem menuitem;
        MenuItem menuitem1;
        menuitem = menu.findItem(R.id.select_item);
        menuitem1 = menu.findItem(R.id.delete_photos);
        switch(mPickerMode) {
	        case 0:
	        	if(isLocalCameraAlbum() || isInstantUploadAlbum())
	            {
	                menuitem.setVisible(true);
	                menuitem1.setVisible(false);
	            }
	        	break;
	        case 3:
	        	menuitem.setVisible(false);
	            int i = mSelectedPhotoMediaRefs.size();
	            if(i > 0)
	            {
	                menuitem1.setTitle(getActivity().getResources().getQuantityString(R.plurals.delete_photo, i));
	                menuitem1.setVisible(true);
	            } else
	            {
	                menuitem1.setVisible(false);
	            }
	        	break;
	        case 1:
	        case 2:
	        default:
	        	break;
        }
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mEsListener);
        mPageableLoader = (Pageable)getLoaderManager().getLoader(1);
        mPageableLoader.setLoadingListener(this);
        if(mLoaderActive && !mPageableLoader.isDataSourceLoading())
            onDataSourceLoading(false);
        if(mRefreshReqId != null)
            if(EsService.isRequestPending(mRefreshReqId.intValue()))
            {
                if(isEmpty())
                    showEmptyViewProgress(getView());
            } else
            {
                ServiceResult serviceresult1 = EsService.removeResult(mRefreshReqId.intValue());
                handleServiceCallback(mRefreshReqId.intValue(), serviceresult1);
            }
        if(mDeleteReqId != null && !EsService.isRequestPending(mDeleteReqId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mDeleteReqId.intValue());
            handlePhotoDelete(mDeleteReqId.intValue(), serviceresult);
        }
        mAdapter.onResume();
        updateSpinner();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mExtras != null)
        {
            bundle.putParcelable("INTENT", mExtras);
            if(mAlbumName != null)
                bundle.putString("ALBUM_NAME", mAlbumName);
            if(mPickerMode != 0)
            {
                bundle.putInt("STATE_PICKER_MODE", mPickerMode);
                bundle.putInt("STATE_PICKER_TITLE", mPickerTitleResourceId);
                bundle.putBoolean("STATE_PICKER_SHARE_ON_ZERO", mPickerShareWithZeroSelected);
            }
            if(mSelectedPhotoMediaRefs.size() > 0)
            {
                MediaRef amediaref[] = new MediaRef[mSelectedPhotoMediaRefs.size()];
                mSelectedPhotoMediaRefs.toArray(amediaref);
                bundle.putParcelableArray("SELECTED_ITEMS", amediaref);
            }
            if(mRefreshReqId != null)
                bundle.putInt("refresh_request", mRefreshReqId.intValue());
            if(mDeleteReqId != null)
                bundle.putInt("delete_request", mDeleteReqId.intValue());
            if(mLoaderActive)
                bundle.putBoolean("loader_active", true);
        }
    }

    public final void onStop()
    {
        super.onStop();
        mAdapter.onStop();
    }

    public final void onViewUsed(int i) {
    	if(isPaused()) {
    		return;
    	}
    	// TODO
    	
    }

    public final void refresh() {
    	if(null != mRefreshReqId) {
    		return;
    	}
    	super.refresh();
        if(null != mStreamId) {
        	mRefreshReqId = Integer.valueOf(EsService.getStreamPhotos(getActivity(), mAccount, mOwnerId, mStreamId, Integer.valueOf(0), Integer.valueOf(500), mAuthkey));
        } else {
        	if(mPhotoOfUserId != null)
                mRefreshReqId = Integer.valueOf(EsService.getPhotosOfUser(getActivity(), mAccount, mPhotoOfUserId));
            else
            if(mAlbumId != null)
                mRefreshReqId = Integer.valueOf(EsService.getAlbumPhotos(getActivity(), mAccount, mAlbumId, mOwnerId, mAuthkey));
            else
            if(mEventId != null)
                mRefreshReqId = Integer.valueOf(EsService.readEvent(getActivity(), mAccount, mEventId, mOwnerId, (String)null, true));
        }
        updateView(getView());
    }

    protected final void shareSelectedPhotos()
    {
        ArrayList arraylist = new ArrayList(mSelectedPhotoMediaRefs);
        startActivity(Intents.getPostActivityIntent(getActivity(), mAccount, arraylist));
        if(mHereFromNotification)
            EsAnalytics.recordActionEvent(getActivity(), mAccount, OzActions.SHARE_INSTANT_UPLOAD_FROM_NOTIFICATION, OzViews.PHOTOS_HOME);
    }

	private static interface AlbumDetailsQuery {

        public static final String PROJECTION[] = {
            "title", "photo_count"
        };

    }
}
