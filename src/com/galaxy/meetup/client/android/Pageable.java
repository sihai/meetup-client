/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

/**
 * 
 * @author sihai
 *
 */
public interface Pageable {

	public static interface LoadingListener
    {

        public abstract void onDataSourceLoading(boolean flag);
    }


    public abstract int getCurrentPage();

    public abstract boolean hasMore();

    public abstract boolean isDataSourceLoading();

    public abstract void loadMore();

    public abstract void setLoadingListener(LoadingListener loadinglistener);
}
