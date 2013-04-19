/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.Iterator;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.server.client.domain.DataVideo;
import com.galaxy.meetup.server.client.domain.DataVideoStream;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class VideoViewFragment extends EsFragment implements OnErrorListener,
		OnInfoListener, OnPreparedListener, LoaderCallbacks, OnClickListener {

	private static final String PROJECTION[] = {
        "video_data"
    };
    private static final SparseBooleanArray sPlayableTypes;
    private EsAccount mAccount;
    private String mAuthkey;
    private boolean mError;
    private final EsServiceListener mEsListener;
    private Intent mIntent;
    private boolean mIsWiFiConnection;
    private boolean mLoading;
    private String mOwnerId;
    private boolean mPerformedRefetch;
    private long mPhotoId;
    private boolean mPlayOnResume;
    private VideoView mPlayerView;
    private int mPreviousOrientation;
    private DataVideo mVideoData;
    private int mVideoPosition;

    static 
    {
        SparseBooleanArray sparsebooleanarray = new SparseBooleanArray();
        sPlayableTypes = sparsebooleanarray;
        sparsebooleanarray.put(18, true);
        sPlayableTypes.put(22, true);
        sPlayableTypes.put(36, true);
    }
    
    
    public VideoViewFragment()
    {
        mPreviousOrientation = -1;
        mEsListener = new EsServiceListener() {

            public final void onGetPhoto(int i, long l)
            {
                if(l == mPhotoId)
                    getLoaderManager().restartLoader(0, null, VideoViewFragment.this);
            }

        };
    }

    public VideoViewFragment(Intent intent)
    {
        this();
        mIntent = intent;
    }

    private void startPlayback()
    {
        if(!TextUtils.equals("READY", mVideoData.status) && !TextUtils.equals("FINAL", mVideoData.status)) {
        	if(TextUtils.equals("PENDING", mVideoData.status))
                setupEmptyView(getView(), R.string.video_not_ready);
            else
                setupEmptyView(getView(), R.string.no_video);
        	return;
        } else { 
        	if(mPhotoId != 0L) {
        		String s = null;
        		Iterator iterator;
                DataVideoStream datavideostream;
                DataVideoStream datavideostream1;
                iterator = mVideoData.stream.iterator();
                datavideostream = null;
                while(iterator.hasNext()) {
                	 datavideostream1 = (DataVideoStream)iterator.next();
                     int i = datavideostream1.height.intValue();
                     if(!sPlayableTypes.get(datavideostream1.formatId.intValue()) || TextUtils.isEmpty(datavideostream1.url)) {
                    	 break;
                     } else {
                    	 boolean flag;
                         if(i <= 640)
                             flag = true;
                         else
                             flag = false;
                         if(datavideostream != null && (!mIsWiFiConnection || i <= 0) && (mIsWiFiConnection || !flag || i <= 0) && (mIsWiFiConnection || i >= 0))
                             break;
                     }
                     datavideostream = datavideostream1;
                }
                
                if(datavideostream == null)
                    s = null;
                else
                    s = datavideostream.url;
                if(s != null)
                {
                    mLoading = true;
                    mPlayerView.setVideoURI(Uri.parse(s));
                } else
                {
                    mError = true;
                    setupEmptyView(getView(), R.string.video_no_stream);
                }
        	} else { 
        		String s = ((DataVideoStream)mVideoData.stream.get(0)).url;
                if(s != null)
                {
                    mLoading = true;
                    mPlayerView.setVideoURI(Uri.parse(s));
                } else
                {
                    mError = true;
                    setupEmptyView(getView(), R.string.video_no_stream);
                }
                return;
        	}
        }
    }

    private void updateView(View view)
    {
        boolean flag;
        if(mVideoData != null && (TextUtils.equals("READY", mVideoData.status) || TextUtils.equals("FINAL", mVideoData.status)))
            flag = true;
        else
            flag = false;
        if(mLoading)
            showEmptyViewProgress(view);
        else
        if(flag && !mError)
            showContent(view);
        else
            showEmptyView(view);
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        boolean flag1;
        if(mVideoData != null && (TextUtils.equals("READY", mVideoData.status) || TextUtils.equals("FINAL", mVideoData.status)))
            flag = true;
        else
            flag = false;
        if(flag && !mError)
        {
            boolean flag2 = mLoading;
            flag1 = false;
            if(!flag2)
                return false;
        }
        flag1 = true;
        return flag1;
    }

    public void onClick(View view)
    {
        MediaController mediacontroller;
        Object obj = view.getTag();
        if(null == obj) {
        	mLoading = false;
            mError = true;
            View view1 = getView();
            if(view1 != null)
            {
                setupEmptyView(view1, R.string.video_no_stream);
                updateView(view1);
            }
            return;
        }
        if(obj instanceof MediaController) {
        	mediacontroller = (MediaController)obj;
            if(!mediacontroller.isShowing()) {
            	mediacontroller.show();
            }
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mIntent = (new Intent()).putExtras(bundle.getBundle("com.google.android.apps.plus.VideoViewFragment.INTENT"));
            mVideoPosition = bundle.getInt("com.google.android.apps.plus.VideoViewFragment.POSITION", 0);
            mPlayOnResume = bundle.getBoolean("com.google.android.apps.plus.VideoViewFragment.PLAY_ON_RESUME");
            mPreviousOrientation = bundle.getInt("com.google.android.apps.plus.VideoViewFragment.PREVIOUS_ORIENTATION");
        }
        int i = getResources().getConfiguration().orientation;
        if(mPreviousOrientation != i)
        {
            mPreviousOrientation = i;
            mPlayOnResume = true;
        }
        mAccount = (EsAccount)mIntent.getParcelableExtra("account");
        mPhotoId = mIntent.getLongExtra("photo_id", 0L);
        mOwnerId = mIntent.getStringExtra("owner_id");
        if(mIntent.hasExtra("data"))
        {
            byte abyte0[] = mIntent.getByteArrayExtra("data");
            mVideoData = null;
            if(abyte0 != null)
                mVideoData = (DataVideo)JsonUtil.fromByteArray(abyte0, DataVideo.class);
        }
        if(mIntent.hasExtra("auth_key"))
            mAuthkey = mIntent.getStringExtra("auth_key");
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        Uri uri = EsProvider.appendAccountParameter(ContentUris.withAppendedId(EsProvider.PHOTO_BY_PHOTO_ID_URI, mPhotoId), mAccount);
        return new EsCursorLoader(getActivity(), uri, PROJECTION, null, null, null);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle, R.layout.video_view_fragment);
        MediaController mediacontroller = new MediaController(getActivity());
        View view1 = view.findViewById(R.id.videolayout);
        view1.setOnClickListener(this);
        view1.setTag(mediacontroller);
        mediacontroller.setAnchorView(view1);
        mediacontroller.setLayoutParams(new android.widget.FrameLayout.LayoutParams(-1, -1));
        mPlayerView = (VideoView)view.findViewById(R.id.videoplayer);
        mPlayerView.setMediaController(mediacontroller);
        mPlayerView.setOnPreparedListener(this);
        mPlayerView.setOnErrorListener(this);
        if(mVideoData == null)
        {
            mLoading = true;
            getLoaderManager().initLoader(0, null, this);
        }
        setupEmptyView(view, R.string.no_video);
        updateView(view);
        return view;
    }

    public boolean onError(MediaPlayer mediaplayer, int i, int j)
    {
        if(mPerformedRefetch || i != 1) 
        	mError = true;
	        mLoading = false;
	        View view = getView();
	        if(view != null)
	        {
	            setupEmptyView(view, R.string.video_no_stream);
	            updateView(view);
	        }
        else {
        	mPerformedRefetch = true;
            EsService.getPhoto(getActivity(), mAccount, mOwnerId, mPhotoId, mAuthkey);
        }
	        
	    return true;

    }

    public boolean onInfo(MediaPlayer mediaplayer, int i, int j)
    {
    	switch(i) {
    	case 1:
    	case 100:
    	case 200:
    		mLoading = false;
            mError = true;
    		break;
    	case 701:
    		mLoading = true;
            mError = false;
    		break;
    	case 702:
    		mLoading = false;
            mError = false;
    		break;
    	default:
    		break;
    	}
    	
    	View view = getView();
        if(view != null)
            updateView(view);
        return true;
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        mLoading = false;
        if(cursor != null && cursor.moveToFirst())
        {
            byte abyte0[] = cursor.getBlob(0);
            mVideoData = null;
            if(abyte0 != null)
                mVideoData = (DataVideo)JsonUtil.fromByteArray(abyte0, DataVideo.class);
            if(mVideoData != null)
                startPlayback();
        }
        View view = getView();
        if(view != null)
            updateView(view);
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onPause()
    {
        super.onPause();
        if(mPlayerView != null && mPlayerView.isPlaying())
            if(mPlayerView.canPause())
                mPlayerView.pause();
            else
                mPlayerView.stopPlayback();
        EsService.unregisterListener(mEsListener);
    }

    public void onPrepared(MediaPlayer mediaplayer)
    {
        mLoading = false;
        mediaplayer.setOnInfoListener(this);
        View view;
        if(mVideoPosition == 0)
        {
            mPlayerView.start();
        } else
        {
            mPlayerView.seekTo(mVideoPosition);
            mPlayerView.start();
        }
        view = getView();
        if(view != null)
            updateView(view);
    }

    public final void onResume()
    {
        boolean flag = true;
        super.onResume();
        EsService.registerListener(mEsListener);
        NetworkInfo networkinfo = ((ConnectivityManager)getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        if(networkinfo == null || networkinfo.getType() != 1)
            flag = false;
        mIsWiFiConnection = flag;
        if(mVideoData != null && mPlayOnResume)
            startPlayback();
        mPlayOnResume = false;
        updateView(getView());
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mIntent != null)
        {
            bundle.putParcelable("com.google.android.apps.plus.VideoViewFragment.INTENT", mIntent.getExtras());
            bundle.putInt("com.google.android.apps.plus.VideoViewFragment.POSITION", mPlayerView.getCurrentPosition());
            bundle.putBoolean("com.google.android.apps.plus.VideoViewFragment.PLAY_ON_RESUME", mPlayOnResume);
            bundle.putInt("com.google.android.apps.plus.VideoViewFragment.PREVIOUS_ORIENTATION", mPreviousOrientation);
        }
    }
}
