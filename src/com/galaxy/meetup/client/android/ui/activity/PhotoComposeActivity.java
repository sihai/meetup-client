/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.ArrayList;
import java.util.List;

import WriteReviewOperation.MediaRef;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.galaxy.meetup.client.android.PhotoComposePagerAdapter;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.PhotoComposeFragment.RemoveImageListener;

/**
 * 
 * @author sihai
 *
 */
public class PhotoComposeActivity extends BaseActivity implements
		RemoveImageListener, PhotoComposePagerAdapter.MediaRefProvider {

	private EsAccount mAccount;
    private PhotoComposePagerAdapter mAdapter;
    private List mMediaRefs;
    private List mMediaRefsToRemove;
    private int mStartingPosition;
    private ViewPager mViewPager;
    
	public PhotoComposeActivity()
    {
    }

    private void finishActivity()
    {
        Intent intent = new Intent();
        MediaRef amediaref[] = new MediaRef[mMediaRefsToRemove.size()];
        for(int i = 0; i < mMediaRefsToRemove.size(); i++)
            amediaref[i] = (MediaRef)mMediaRefsToRemove.get(i);

        intent.putExtra("photo_remove_from_compose", amediaref);
        setResult(-1, intent);
        finish();
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final int getCount()
    {
        return mMediaRefs.size();
    }

    public final MediaRef getItem(int i)
    {
        MediaRef mediaref;
        if(i >= 0 || i < mMediaRefs.size())
            mediaref = (MediaRef)mMediaRefs.get(i);
        else
            mediaref = null;
        return mediaref;
    }

    public final int getItemPosition(Object obj) {
    	int i = -2;
        if(obj instanceof MediaRef) {
        	i = mMediaRefs.indexOf(obj);
            if(i == -1)
                i = -2;
        } else {
        	i = -2;
        }
        return i;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTO;
    }

    public void onBackPressed()
    {
        if(mMediaRefsToRemove.size() > 0)
            finishActivity();
        else
            super.onBackPressed();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.photo_compose_activity);
        Intent intent = getIntent();
        mAccount = (EsAccount)intent.getParcelableExtra("account");
        mStartingPosition = intent.getIntExtra("photo_index", 0);
        if(intent.hasExtra("mediarefs"))
        {
            android.os.Parcelable aparcelable[] = intent.getParcelableArrayExtra("mediarefs");
            mMediaRefs = new ArrayList(aparcelable.length);
            for(int i = 0; i < aparcelable.length; i++)
                mMediaRefs.add((MediaRef)aparcelable[i]);

        } else
        {
            finish();
        }
        if(mStartingPosition < 0 || mStartingPosition >= mMediaRefs.size())
            mStartingPosition = 0;
        mAdapter = new PhotoComposePagerAdapter(this, getSupportFragmentManager(), mAccount, this, this);
        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mStartingPosition);
        mMediaRefsToRemove = new ArrayList();
    }

    public final void onImageRemoved(MediaRef mediaref)
    {
        mMediaRefsToRemove.add(mediaref);
        mMediaRefs.remove(mediaref);
        if(mMediaRefs.size() == 0)
            finishActivity();
        mAdapter.notifyDataSetChanged();
    }

}
