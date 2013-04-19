/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import WriteReviewOperation.MediaRef;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.AlbumGridViewAdapter;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.Pageable;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.Pageable.LoadingListener;
import com.galaxy.meetup.client.android.PhotosSelectionLoader;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.content.PhotoTaggeeData;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.PhotoAlbumView;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 *
 */
public class PhotosSelectionFragment extends HostedEsFragment implements
		LoaderCallbacks, LoadingListener {

	private AlbumGridViewAdapter mAdapter;
    private PhotoAlbumView mAlbumView;
    private DateFormat mDateFormat;
    private AudienceData mDefaultAudience;
    private ColumnGridView mGridView;
    private boolean mLoaderActive;
    private Map mMediaRefUserMap;
    private List mMediaRefs;
    private View mNextButton;
    private String mOwnerId;
    private Pageable mPageableLoader;
    private final HashSet mSelectedMediaRefs = new HashSet();
    
    public PhotosSelectionFragment()
    {
        mDateFormat = DateFormat.getDateInstance(2);
    }

    private AudienceData createAudienceData()
    {
        if(mSelectedMediaRefs != null && !mSelectedMediaRefs.isEmpty() && mMediaRefUserMap != null && !mMediaRefUserMap.isEmpty()) {
        	HashSet hashset = new HashSet();
            ArrayList arraylist = new ArrayList();
            Set set = mMediaRefUserMap.keySet();
            for(Iterator iterator = mSelectedMediaRefs.iterator(); iterator.hasNext();)
            {
                MediaRef mediaref = (MediaRef)iterator.next();
                if(set.contains(mediaref))
                {
                    List list = (List)mMediaRefUserMap.get(mediaref);
                    if(list != null && !list.isEmpty())
                    {
                        Iterator iterator1 = list.iterator();
                        while(iterator1.hasNext()) 
                        {
                            PhotoTaggeeData.PhotoTaggee phototaggee = (PhotoTaggeeData.PhotoTaggee)iterator1.next();
                            String s = phototaggee.getId();
                            if(!hashset.contains(s))
                            {
                                hashset.add(s);
                                arraylist.add(new PersonData(s, phototaggee.getName(), null));
                            }
                        }
                    }
                }
            }

            if(arraylist.isEmpty())
                return mDefaultAudience;
            else
                return new AudienceData(arraylist, null); 
        } else { 
        	return mDefaultAudience;
        }
    }

    private void updatePostUI()
    {
        if(mNextButton != null)
        {
            View view = mNextButton;
            boolean flag;
            if(mSelectedMediaRefs != null && mSelectedMediaRefs.size() > 0)
                flag = true;
            else
                flag = false;
            view.setEnabled(flag);
            mNextButton.invalidate();
        }
    }
    
    private void updateView(View view) {
    	
    	if(null == view) {
    		return;
    	}
    	
    	boolean flag;
    	Cursor cursor = mAdapter.getCursor();
    	if(cursor != null && cursor.getCount() > 0)
            flag = true;
        else
            flag = false;
        if(flag) {
        	showContent(view);
        } else {
        	if(mLoaderActive)
                showEmptyViewProgress(view);
            else
                showEmptyView(view, getString(R.string.no_photos));
        }
        updateSpinner();	
    }

    public final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTOS_LIST;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mAdapter == null || mAdapter.isEmpty())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final boolean isProgressIndicatorVisible()
    {
        boolean flag;
        if(mLoaderActive || super.isProgressIndicatorVisible())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onCreate(Bundle bundle)
    {
    	int i;
    	android.os.Parcelable aparcelable[];
        super.onCreate(bundle);
        Bundle bundle1 = getArguments();
        if(bundle1.containsKey("mediarefs"))
        {
            android.os.Parcelable aparcelable1[] = bundle1.getParcelableArray("mediarefs");
            mMediaRefs = new ArrayList(aparcelable1.length);
            for(int j = 0; j < aparcelable1.length; j++)
                mMediaRefs.add((MediaRef)aparcelable1[j]);

        }
        if(bundle1.containsKey("owner_id"))
            mOwnerId = bundle1.getString("owner_id");
        if(bundle1.containsKey("mediaref_user_map"))
            try
            {
                mMediaRefUserMap = (Map)bundle1.getSerializable("mediaref_user_map");
            }
            catch(ClassCastException classcastexception1)
            {
                mMediaRefUserMap = null;
            }
        if(bundle1.containsKey("audience"))
        {
            android.os.Parcelable parcelable = bundle1.getParcelable("audience");
            try
            {
                mDefaultAudience = (AudienceData)parcelable;
            }
            catch(ClassCastException classcastexception)
            {
                mDefaultAudience = null;
            }
        }
        if(bundle != null)
        {
            if(bundle.containsKey("SELECTED_ITEMS"))
            {
                aparcelable = bundle.getParcelableArray("SELECTED_ITEMS");
                for(i = 0; i < aparcelable.length; i++)
                    mSelectedMediaRefs.add((MediaRef)aparcelable[i]);

            }
        } else
        if(mMediaRefUserMap != null && !mMediaRefUserMap.isEmpty())
            mSelectedMediaRefs.addAll(mMediaRefUserMap.keySet());
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        mLoaderActive = true;
        PhotosSelectionLoader photosselectionloader = new PhotosSelectionLoader(getActivity(), mAccount, mOwnerId, mMediaRefs);
        mPageableLoader = (Pageable)photosselectionloader;
        mPageableLoader.setLoadingListener(this);
        return photosselectionloader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.photos_selection_fragment, viewgroup, false);
        mAlbumView = (PhotoAlbumView)view.findViewById(R.id.album_view);
        mGridView = (ColumnGridView)view.findViewById(R.id.grid);
        ScreenMetrics screenmetrics = ScreenMetrics.getInstance(getActivity());
        mGridView.setItemMargin(screenmetrics.itemMargin);
        mGridView.setPadding(screenmetrics.itemMargin, screenmetrics.itemMargin, screenmetrics.itemMargin, screenmetrics.itemMargin);
        mAdapter = new AlbumGridViewAdapter(getActivity(), null, "from_my_phone", mGridView, null, null, null);
        mAdapter.setSelectedMediaRefs(mSelectedMediaRefs);
        mGridView.setAdapter(mAdapter);
        mGridView.setSelector(R.drawable.list_selected_holo);
        getLoaderManager().initLoader(0, null, this);
        mGridView.startSelectionMode();
        updateView(mAlbumView);
        setupEmptyView(mAlbumView, R.string.no_photos);
        mGridView.setOnScrollListener(new ColumnGridView.OnScrollListener() {

        	int mCachedFirstVisibleIndex = -1;
        	
            public final void onScroll(ColumnGridView columngridview, int i, int j, int k, int l, int i1)
            {
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

            public final void onScrollStateChanged(ColumnGridView columngridview, int i)
            {
            	int j = 0;
                if(i == 0) {
                	j = 8; 
                } else { 
                    Cursor cursor = mAdapter.getCursor();
                    boolean flag;
                    if(cursor != null && cursor.getCount() > 0)
                        flag = true;
                    else
                        flag = false;
                    if(!flag) {
                    	j = 8;
                    } else { 
                    	 j = 0;
                    }
                }
                
                mAlbumView.setDateVisibility(j);
            }

        });
        mGridView.registerSelectionListener(new ColumnGridView.ItemSelectionListener() {

            public final void onItemDeselected(View view2, int i)
            {
                MediaRef mediaref = null;
                if(view2 != null)
                    mediaref = (MediaRef)view2.getTag();
                if(mediaref == null)
                    mediaref = mAdapter.getMediaRefForItem(i);
                mSelectedMediaRefs.remove(mediaref);
                updatePostUI();
            }

            public final void onItemSelected(View view2, int i)
            {
                MediaRef mediaref = null;
                if(view2 != null)
                    mediaref = (MediaRef)view2.getTag();
                if(mediaref == null)
                {
                    mediaref = mAdapter.getMediaRefForItem(i);
                    if(view2 != null)
                        view2.setTag(mediaref);
                }
                mSelectedMediaRefs.add(mediaref);
                updatePostUI();
            }

        });
        mAlbumView.enableDateDisplay(true);
        View view1 = view.findViewById(R.id.cancel_button);
        if(view1 != null)
            view1.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view2)
                {
                    getActivity().finish();
                }

            });
        mNextButton = view.findViewById(R.id.next_button);
        if(mNextButton != null)
            mNextButton.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view2)
                {
                    boolean _tmp = PhotosSelectionFragment.access$500(PhotosSelectionFragment.this);
                }
            });
        updatePostUI();
        return view;
    }

    public final void onDataSourceLoading(boolean flag)
    {
        mLoaderActive = flag;
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        mAdapter.swapCursor(cursor);
        updateView(getView());
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onPause()
    {
        super.onPause();
        mPageableLoader.setLoadingListener(null);
    }

    public final void onResume()
    {
        super.onResume();
        mPageableLoader = (Pageable)getLoaderManager().getLoader(0);
        mPageableLoader.setLoadingListener(this);
        if(mLoaderActive && !mPageableLoader.isDataSourceLoading())
            mLoaderActive = false;
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mSelectedMediaRefs.size() > 0)
        {
            MediaRef amediaref[] = new MediaRef[mSelectedMediaRefs.size()];
            mSelectedMediaRefs.toArray(amediaref);
            bundle.putParcelableArray("SELECTED_ITEMS", amediaref);
        }
    }
    
    static boolean access$500(PhotosSelectionFragment photosselectionfragment)
    {
        boolean flag;
        if(photosselectionfragment.mSelectedMediaRefs == null || photosselectionfragment.mSelectedMediaRefs.size() <= 0)
        {
            flag = false;
        } else
        {
            ArrayList arraylist = new ArrayList(photosselectionfragment.mSelectedMediaRefs);
            AudienceData audiencedata = photosselectionfragment.createAudienceData();
            photosselectionfragment.startActivity(Intents.getPostActivityIntent(photosselectionfragment.getActivity(), photosselectionfragment.mAccount, arraylist, audiencedata));
            photosselectionfragment.getActivity().finish();
            flag = true;
        }
        return flag;
    }
}
