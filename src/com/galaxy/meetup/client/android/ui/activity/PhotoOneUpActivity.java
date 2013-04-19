/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import WriteReviewOperation.MediaRef;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.galaxy.meetup.client.android.CameraPhotoLoader;
import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.Pageable;
import com.galaxy.meetup.client.android.PhotoPagerAdapter;
import com.galaxy.meetup.client.android.PhotoPagerLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentPagerAdapter;
import com.galaxy.meetup.client.android.ui.fragments.HostedFragment;
import com.galaxy.meetup.client.android.ui.fragments.PhotoOneUpCallbacks;
import com.galaxy.meetup.client.android.ui.fragments.PhotoOneUpFragment;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.PhotoViewPager;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 * 
 */
public class PhotoOneUpActivity extends BaseActivity implements
		android.support.v4.app.LoaderManager.LoaderCallbacks,
		android.support.v4.view.ViewPager.OnPageChangeListener,
		PhotoOneUpCallbacks, EsFragmentPagerAdapter.OnFragmentPagerListener,
		HostActionBar.HostActionBarListener,
		HostActionBar.OnUpButtonClickListener,
		PhotoViewPager.OnInterceptTouchListener {

	private static final String EVENT_NAME_PROJECTION[] = {
        "name"
    };
    private EsAccount mAccount;
    private HostActionBar mActionBar;
    private PhotoPagerAdapter mAdapter;
    private int mAlbumCount;
    private String mAlbumId;
    private String mAlbumName;
    private String mAuthkey;
    private int mCurrentIndex;
    private MediaRef mCurrentRef;
    private String mEventId;
    private android.content.DialogInterface.OnClickListener mFailedListener;
    private boolean mFragmentIsLoading;
    private boolean mFullScreen;
    private HostedFragment mHostedFragment;
    private boolean mIsEmpty;
    private boolean mIsPaused;
    private boolean mKeyboardIsVisible;
    private MediaRef mMediaRefs[];
    private Set mMenuItemListeners;
    private String mOwnerGaiaId;
    private int mPageHint;
    private String mPhotoOfUserGaiaId;
    private MediaRef mPhotoRef;
    private String mPhotoUrl;
    private boolean mRestartLoader;
    private View mRootView;
    private Set mScreenListeners;
    private String mStreamId;
    private PhotoViewPager mViewPager;
    
    public PhotoOneUpActivity()
    {
        mPageHint = -1;
        mAlbumCount = -1;
        mScreenListeners = new HashSet();
        mMenuItemListeners = new HashSet();
        mIsPaused = true;
        mFailedListener = new android.content.DialogInterface.OnClickListener() {

            public final void onClick(DialogInterface dialoginterface, int i)
            {
                dialoginterface.dismiss();
            }

        };
    }

    private void updateTitleAndSubtitle()
    {
        int i = 1 + mViewPager.getCurrentItem();
        boolean flag;
        String s;
        if(mAlbumCount >= 0)
            flag = true;
        else
            flag = false;
        if(mIsEmpty || !flag || i <= 0)
        {
            if(mAlbumName != null)
                s = mAlbumName;
            else
                s = getResources().getString(R.string.photo_view_default_title);
        } else
        {
            Resources resources = getResources();
            int j = R.string.photo_view_count;
            Object aobj[] = new Object[2];
            aobj[0] = Integer.valueOf(i);
            aobj[1] = Integer.valueOf(mAlbumCount);
            s = resources.getString(j, aobj);
        }
        if(mHostedFragment instanceof PhotoOneUpFragment)
            ((PhotoOneUpFragment)mHostedFragment).setTitle(s);
        mActionBar.invalidateActionBar();
    }

    private void updateView(View view)
    {
        if(view != null)
            if(mFragmentIsLoading || mAdapter.getCursor() == null && !mIsEmpty)
            {
                view.findViewById(R.id.photo_activity_empty_text).setVisibility(8);
                view.findViewById(R.id.photo_activity_empty_progress).setVisibility(0);
                view.findViewById(R.id.photo_activity_empty).setVisibility(0);
            } else
            if(!mIsEmpty)
            {
                view.findViewById(R.id.photo_activity_empty).setVisibility(8);
            } else
            {
                String s = getResources().getString(R.string.camera_photo_error);
                view.findViewById(R.id.photo_activity_empty_progress).setVisibility(8);
                TextView textview = (TextView)view.findViewById(R.id.photo_activity_empty_text);
                textview.setText(s);
                textview.setVisibility(0);
                view.findViewById(R.id.photo_activity_empty).setVisibility(0);
            }
    }

    public final void addMenuItemListener(OnMenuItemListener onmenuitemlistener)
    {
        mMenuItemListeners.add(onmenuitemlistener);
    }

    public final void addScreenListener(OnScreenListener onscreenlistener)
    {
        mScreenListeners.add(onscreenlistener);
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTO;
    }

    public final boolean isFragmentActive(Fragment fragment) {
    	
        PhotoViewPager photoviewpager;
        photoviewpager = mViewPager;
        boolean flag = false;
        if(photoviewpager == null) {
        	return flag; 
        } else {
        	 PhotoPagerAdapter photopageradapter;
             photopageradapter = mAdapter;
             flag = false;
             if(photopageradapter != null) {
            	 int i = mViewPager.getCurrentItem();
                 int j = mAdapter.getItemPosition(fragment);
                 flag = false;
                 if(i == j)
                     flag = true; 
             }
             return flag;
        }
    }

    public final void onActionBarInvalidated()
    {
        if(mActionBar != null && mHostedFragment != null)
        {
            mActionBar.reset();
            mHostedFragment.attachActionBar(mActionBar);
            mActionBar.commit();
        }
    }

    public final void onActionButtonClicked(int i)
    {
        if(mHostedFragment != null)
            mHostedFragment.onActionButtonClicked(i);
    }

    public void onBackPressed()
    {
        if(mFullScreen)
            toggleFullScreen();
        else
            super.onBackPressed();
    }

    protected void onCreate(Bundle bundle)
    {
       // TODO
    }

    protected Dialog onCreateDialog(int i, Bundle bundle)
    {
        String s = bundle.getString("tag");
        Object obj;
        if(i == R.id.photo_view_pending_dialog)
        {
            obj = new ProgressDialog(this);
            ((ProgressDialog) (obj)).setMessage(bundle.getString("dialog_message"));
            ((ProgressDialog) (obj)).setProgressStyle(0);
            ((ProgressDialog) (obj)).setCancelable(false);
        } else
        if(i == R.id.photo_view_download_full_failed_dialog)
        {
            RetryDialogListener retrydialoglistener = new RetryDialogListener(s);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.download_photo_retry).setPositiveButton(R.string.yes, retrydialoglistener).setNegativeButton(R.string.no, retrydialoglistener);
            obj = builder.create();
        } else
        if(i == R.id.photo_view_download_nonfull_failed_dialog)
        {
            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
            builder1.setMessage(R.string.download_photo_error).setNeutralButton(R.string.ok, mFailedListener);
            obj = builder1.create();
        } else
        {
            obj = null;
        }
        return ((Dialog) (obj));
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
    	Loader loader = null;
    	if(1360862707 == i) {
    		loader = new CursorLoader(this) {

                public final Cursor loadInBackground()
                {
                    EsMatrixCursor esmatrixcursor = new EsMatrixCursor(PhotoOneUpActivity.EVENT_NAME_PROJECTION);
                    String s1 = EsEventData.getEventName(PhotoOneUpActivity.this, getAccount(), mEventId);
                    esmatrixcursor.newRow().add(s1);
                    return esmatrixcursor;
                }
            };
    	} else if(2131361833 == i) {
    		mFragmentIsLoading = true;
            MediaRef amediaref[] = mMediaRefs;
            boolean flag;
            if(amediaref != null && amediaref.length == 1)
            {
                String s = amediaref[0].getUrl();
                if(s == null)
                {
                    Uri uri = amediaref[0].getLocalUri();
                    if(uri != null)
                        s = uri.toString();
                }
                if(!TextUtils.isEmpty(s) && s.startsWith("content:"))
                    flag = true;
                else
                    flag = false;
            } else
            {
                flag = false;
            }
            if(flag)
            {
                loader = new CameraPhotoLoader(this);
            } else
            {
            	loader = new PhotoPagerLoader(this, mAccount, mOwnerGaiaId, mMediaRefs, mAlbumId, mPhotoOfUserGaiaId, mStreamId, mEventId, mPhotoUrl, mPageHint, mAuthkey);
            }
    	} else if(2131361834 == i) {
    		loader = new EsCursorLoader(this, EsProvider.appendAccountParameter(Uri.withAppendedPath(Uri.withAppendedPath(EsProvider.ALBUM_VIEW_BY_ALBUM_AND_OWNER_URI, mAlbumId), mOwnerGaiaId), mAccount), AlbumDetailsQuery.PROJECTION, null, null, null);
    	}
    	
    	return loader;
    	
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.photo_view_menu, menu);
        return true;
    }

    public final void onFragmentVisible(Fragment fragment)
    {
        if(mViewPager != null && mAdapter != null)
        {
            if(mViewPager.getCurrentItem() == mAdapter.getItemPosition(fragment))
                mFragmentIsLoading = false;
            updateView(mRootView);
        }
    }

    public final void onLoadFinished(final Loader loader, Object obj)
    {
    	boolean flag = true;
        final Cursor data = (Cursor)obj;
        int id = loader.getId();
        if(1360862707 == id) {
        	if(data == null || !data.moveToFirst()) {
        		return;
        	} else {
        		if(mAlbumName == null)
                    mAlbumName = data.getString(0);
        		updateTitleAndSubtitle();
        	}
        } else if(2131361833 == id) {
        	boolean flag1;
            boolean flag2;
            if(data == null || data.getCount() == 0)
            {
                mIsEmpty = flag;
                mFragmentIsLoading = false;
                updateView(mRootView);
            } else
            {
                (new Handler()).post(new Runnable() {

                    public final void run()
                    {
                        if(mIsPaused || data.isClosed())
                        {
                            mRestartLoader = true;
                        } else
                        {
                            mIsEmpty = false;
                            int j;
                            if(mCurrentRef != null)
                                j = PhotoOneUpActivity.access$900(PhotoOneUpActivity.this, data, mCurrentRef);
                            else
                                j = mCurrentIndex;
                            if(j < 0 && mPhotoRef != null)
                                j = PhotoOneUpActivity.access$900(PhotoOneUpActivity.this, data, mPhotoRef);
                            if(j < 0)
                                j = 0;
                            mAdapter.setPageable((Pageable)loader);
                            mAdapter.swapCursor(data);
                            updateView(mRootView);
                            mViewPager.setCurrentItem(j, false);
                        }
                    }

                });
            }
            if(mOwnerGaiaId != null && mAlbumId != null)
                flag1 = flag;
            else
                flag1 = false;
            flag2 = TextUtils.isEmpty(mAlbumName);
            if(mAlbumCount != -1)
                flag = false;
            if(!flag2 || mEventId == null) {
            	if(!flag1 || !flag2 && !flag) {
            		 if(flag2) { 
            			 return; 
            		 } else {
            			 updateTitleAndSubtitle();
            			 return;
            		 }
            	} else { 
            		getSupportLoaderManager().restartLoader(0x7f0a002a, null, this);
            	}
            } else { 
            	getSupportLoaderManager().restartLoader(0x511d1df3, null, this);
            	return;
            }
        } else if(2131361834 == id) {
        	if(data != null && data.moveToFirst())
            {
                String s = data.getString(0);
                if(mAlbumName == null)
                    mAlbumName = s;
                if(mAlbumCount == -1)
                {
                    int i;
                    if(data.isNull(1))
                        i = -2;
                    else
                        i = data.getInt(1);
                    mAlbumCount = i;
                }
                updateTitleAndSubtitle();
            }
        }
        
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        for(Iterator iterator = mMenuItemListeners.iterator(); iterator.hasNext();) {
        	 if(((OnMenuItemListener)iterator.next()).onOptionsItemSelected(menuitem)) 
        		return true;
        }
        return false;
    }

    public final void onPageActivated(Fragment fragment)
    {
        if(fragment instanceof HostedFragment)
        {
            mHostedFragment = (HostedFragment)fragment;
            for(Iterator iterator = mScreenListeners.iterator(); iterator.hasNext(); ((OnScreenListener)iterator.next()).onViewActivated());
            updateTitleAndSubtitle();
        } else
        {
            mHostedFragment = null;
        }
    }

    public final void onPageScrollStateChanged(int i)
    {
    }

    public final void onPageScrolled(int i, float f, int j)
    {
    }

    public final void onPageSelected(int i)
    {
        mCurrentIndex = i;
        PhotoPagerAdapter photopageradapter = mAdapter;
        Cursor cursor;
        MediaRef mediaref;
        if(photopageradapter.isDataValid())
            cursor = photopageradapter.getCursor();
        else
            cursor = null;
        if(cursor == null || cursor.isClosed() || !cursor.moveToPosition(i))
        {
            mediaref = null;
        } else
        {
            long l = cursor.getLong(1);
            String s = cursor.getString(2);
            String s1 = cursor.getString(3);
            boolean flag;
            MediaRef.MediaType mediatype;
            if(cursor.getInt(6) != 0)
                flag = true;
            else
                flag = false;
            if(flag)
                mediatype = MediaRef.MediaType.PANORAMA;
            else
                mediatype = MediaRef.MediaType.IMAGE;
            mediaref = new MediaRef(s1, l, s, null, mediatype);
        }
        mCurrentRef = mediaref;
    }

    protected void onPause()
    {
        mIsPaused = true;
        super.onPause();
    }

    public final void onPhotoRemoved()
    {
        Cursor cursor = mAdapter.getCursor();
        if(cursor != null)
            if(cursor.getCount() <= 1)
            {
                Intent intent = Intents.getHostNavigationActivityIntent(this, mAccount);
                intent.addFlags(0x4000000);
                startActivity(intent);
                finish();
            } else
            {
                getSupportLoaderManager().restartLoader(0x7f0a0029, null, this);
            }
    }

    protected void onPrepareDialog(int i, Dialog dialog, Bundle bundle)
    {
        super.onPrepareDialog(i, dialog, bundle);
        if(i == R.id.photo_view_pending_dialog && (dialog instanceof ProgressDialog))
            ((ProgressDialog)dialog).setMessage(bundle.getString("dialog_message"));
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        int i = menu.size();
        for(int j = 0; j < i; j++)
            menu.getItem(j).setVisible(false);

        for(Iterator iterator = mMenuItemListeners.iterator(); iterator.hasNext(); ((OnMenuItemListener)iterator.next()).onPrepareOptionsMenu(menu));
        return true;
    }

    public final void onPrimarySpinnerSelectionChange(int i)
    {
    }

    public final void onRefreshButtonClicked()
    {
        if(mHostedFragment != null)
            mHostedFragment.refresh();
    }

    protected void onResume()
    {
        super.onResume();
        EsAccount esaccount = (EsAccount)getIntent().getParcelableExtra("account");
        boolean flag;
        if(esaccount != null)
        {
            if(!esaccount.equals(EsService.getActiveAccount(this)))
            {
                if(EsLog.isLoggable("PhotoOneUp", 6))
                    Log.e("PhotoOneUp", (new StringBuilder("Activity finished because it is associated with a signed-out account: ")).append(getClass().getName()).toString());
                flag = false;
            } else
            {
                flag = true;
            }
        } else
        {
            flag = false;
        }
        if(flag)
        {
            mIsPaused = false;
            if(mRestartLoader)
            {
                mRestartLoader = false;
                getSupportLoaderManager().restartLoader(0x7f0a0029, null, this);
            }
        } else
        {
            finish();
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("com.google.android.apps.plus.PhotoViewFragment.ITEM", mViewPager.getCurrentItem());
        bundle.putBoolean("com.google.android.apps.plus.PhotoViewFragment.FULLSCREEN", mFullScreen);
        bundle.putParcelable("com.google.android.apps.plus.PhotoViewFragment.CURRENT_REF", mCurrentRef);
    }

    public final PhotoViewPager.InterceptType onTouchIntercept(float f, float f1)
    {
        boolean flag = false;
        boolean flag1 = false;
        OnScreenListener onscreenlistener;
        for(Iterator iterator = mScreenListeners.iterator(); iterator.hasNext(); onscreenlistener.onViewActivated())
        {
            onscreenlistener = (OnScreenListener)iterator.next();
            if(!flag)
                flag = onscreenlistener.onInterceptMoveLeft();
            if(!flag1)
                flag1 = onscreenlistener.onInterceptMoveRight();
        }

        PhotoViewPager.InterceptType intercepttype;
        if(flag)
        {
            if(flag1)
                intercepttype = PhotoViewPager.InterceptType.BOTH;
            else
                intercepttype = PhotoViewPager.InterceptType.LEFT;
        } else
        if(flag1)
            intercepttype = PhotoViewPager.InterceptType.RIGHT;
        else
            intercepttype = PhotoViewPager.InterceptType.NONE;
        return intercepttype;
    }

    public final void onUpButtonClick()
    {
    	Intent intent = null;
        TaskStackBuilder taskstackbuilder;
        if(!getIntent().getBooleanExtra("from_url_gateway", false) && !getIntent().getBooleanExtra("com.google.plus.analytics.intent.extra.FROM_NOTIFICATION", false)) {
        	onBackPressed();
        } else {
        	 taskstackbuilder = TaskStackBuilder.create(this);
        	 if(!mAccount.isMyGaiaId(mOwnerGaiaId))
                 taskstackbuilder.addNextIntent(Intents.getStreamActivityIntent(this, mAccount));
             taskstackbuilder.addNextIntent(Intents.getProfilePhotosActivityIntent(this, mAccount, (new StringBuilder("g:")).append(mOwnerGaiaId).toString()));
             if(mEventId != null) {
            	 intent = null; 
             } else {
            	 if(mStreamId == null) {
            		 if(mPhotoOfUserGaiaId != null || mAlbumId == null) 
            			 intent = null;
            		 else 
            			 intent = Intents.newPhotosActivityIntentBuilder(this).setAccount(mAccount).setAlbumId(mAlbumId).setAlbumName(mAlbumName).setGaiaId(mOwnerGaiaId).build();
            	 } else { 
            		 intent = Intents.newPhotosActivityIntentBuilder(this).setAccount(mAccount).setStreamId(mStreamId).setAlbumName(mAlbumName).setGaiaId(mOwnerGaiaId).build();
            	 }
             }
             
             if(intent != null)
                 taskstackbuilder.addNextIntent(intent);
             taskstackbuilder.startActivities();
             finish();
        }
        
    }

    public final void removeMenuItemListener(OnMenuItemListener onmenuitemlistener)
    {
        mMenuItemListeners.remove(onmenuitemlistener);
    }

    public final void removeScreenListener(OnScreenListener onscreenlistener)
    {
        mScreenListeners.remove(onscreenlistener);
    }

    public final void toggleFullScreen()
    {
        if(!mKeyboardIsVisible)
        {
            boolean flag;
            Iterator iterator;
            if(!mFullScreen)
                flag = true;
            else
                flag = false;
            mFullScreen = flag;
            iterator = mScreenListeners.iterator();
            while(iterator.hasNext()) 
                ((OnScreenListener)iterator.next()).onFullScreenChanged(mFullScreen);
        }
    }
    
    static int access$900(PhotoOneUpActivity photooneupactivity, Cursor cursor, MediaRef mediaref)
    {
        long l;
        int i = -1;
        l = mediaref.getPhotoId();
        Uri uri = mediaref.getLocalUri();
        Object obj;
        if(uri == null)
            obj = null;
        else
            obj = uri.toString();
        cursor.moveToPosition(-1);
        if(TextUtils.isEmpty(((CharSequence) (obj)))) {
        	if(l == 0L) 
        		return -1;
        	while(cursor.moveToNext()) {
        		if(l == cursor.getLong(1)) {
        			i = cursor.getPosition();
        		}
        	}
        } else {
        	while(cursor.moveToNext()) {
        		if(((String) (obj)).equals(cursor.getString(2))) {
        			i = cursor.getPosition();
        		}
        	}
        }
        
        return i;
    }
    
	
	private static interface AlbumDetailsQuery {

        public static final String PROJECTION[] = {
            "title", "photo_count"
        };

    }

    public static interface OnMenuItemListener {

        public abstract boolean onOptionsItemSelected(MenuItem menuitem);

        public abstract void onPrepareOptionsMenu(Menu menu);
    }

    public static interface OnScreenListener {

        public abstract void enableImageTransforms(boolean flag);

        public abstract void onFullScreenChanged(boolean flag);

        public abstract boolean onInterceptMoveLeft();

        public abstract boolean onInterceptMoveRight();

        public abstract void onViewActivated();
    }

    final class RetryDialogListener implements android.content.DialogInterface.OnClickListener {

        public final void onClick(DialogInterface dialoginterface, int i) {
        	if(-1 == i) {
        		Fragment fragment = getSupportFragmentManager().findFragmentByTag(mTag);
                if(fragment != null)
                    ((PhotoOneUpFragment)fragment).doDownload(PhotoOneUpActivity.this, false);
        	}
        	dialoginterface.dismiss();
        }

        final String mTag;

        public RetryDialogListener(String s)
        {
            super();
            mTag = s;
        }
    }
}
