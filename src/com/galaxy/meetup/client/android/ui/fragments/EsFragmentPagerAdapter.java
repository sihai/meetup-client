/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * 
 * @author sihai
 *
 */
public abstract class EsFragmentPagerAdapter extends PagerAdapter {

	private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;
    private LruCache mFragmentCache;
    private final FragmentManager mFragmentManager;
    private OnFragmentPagerListener mPagerListener;
    
	public EsFragmentPagerAdapter(FragmentManager fragmentmanager)
    {
        mCurTransaction = null;
        mCurrentPrimaryItem = null;
        mFragmentCache = new FragmentCache(5);
        mFragmentManager = fragmentmanager;
    }

    public void destroyItem(View view, int i, Object obj)
    {
        if(mCurTransaction == null)
            mCurTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = (Fragment)obj;
        String s = fragment.getTag();
        if(s == null)
            s = makeFragmentName(view.getId(), i);
        mFragmentCache.put(s, fragment);
        mCurTransaction.detach(fragment);
    }

    public final void finishUpdate$3c7ec8c3()
    {
        if(mCurTransaction != null)
        {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    public abstract Fragment getItem(int i);

    public Object instantiateItem(View view, int i)
    {
        if(mCurTransaction == null)
            mCurTransaction = mFragmentManager.beginTransaction();
        String s = makeFragmentName(view.getId(), i);
        mFragmentCache.remove(s);
        Fragment fragment = mFragmentManager.findFragmentByTag(s);
        if(fragment != null)
        {
            mCurTransaction.attach(fragment);
        } else
        {
            fragment = getItem(i);
            mCurTransaction.add(view.getId(), fragment, s);
        }
        if(fragment != mCurrentPrimaryItem)
            fragment.setMenuVisibility(false);
        return fragment;
    }

    public final boolean isViewFromObject(View view, Object obj)
    {
        // TODO
    	return false;
    }

    protected String makeFragmentName(int i, int j)
    {
        return (new StringBuilder("android:switcher:")).append(i).append(":").append(j).toString();
    }

    public final void restoreState(Parcelable parcelable, ClassLoader classloader)
    {
    }

    public final Parcelable saveState()
    {
        return null;
    }

    public final void setFragmentPagerListener(OnFragmentPagerListener onfragmentpagerlistener)
    {
        mPagerListener = onfragmentpagerlistener;
    }

    public final void setPrimaryItem$7e55ba3e(Object obj)
    {
        Fragment fragment = (Fragment)obj;
        if(fragment != mCurrentPrimaryItem)
        {
            if(mCurrentPrimaryItem != null)
                mCurrentPrimaryItem.setMenuVisibility(false);
            if(fragment != null)
                fragment.setMenuVisibility(true);
            mCurrentPrimaryItem = fragment;
        }
        if(mPagerListener != null)
            mPagerListener.onPageActivated(fragment);
    }


    
    
    private final class FragmentCache extends LruCache
    {

        protected final void entryRemoved(boolean flag, Object obj, Object obj1, Object obj2)
        {
            Fragment fragment = (Fragment)obj1;
            Fragment fragment1 = (Fragment)obj2;
            if(flag || fragment1 != null && fragment != fragment1)
                mCurTransaction.remove(fragment);
        }

        public FragmentCache(int i)
        {
            super(5);
        }
    }

    public static interface OnFragmentPagerListener
    {

        public abstract void onPageActivated(Fragment fragment);
    }

}
