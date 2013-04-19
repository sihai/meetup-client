/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * 
 * @author sihai
 * 
 */
public abstract class TranslationAdapter implements ListAdapter {

	final TranslationListAdapter mInnerAdapter;

	public TranslationAdapter(TranslationListAdapter translationlistadapter) {
		mInnerAdapter = translationlistadapter;
	}

	public boolean areAllItemsEnabled() {
		return mInnerAdapter.areAllItemsEnabled();
	}

	public int getCount() {
		return mInnerAdapter.getCount();
	}

	public Object getItem(int i) {
		return mInnerAdapter.getItem(translate(i));
	}

	public long getItemId(int i) {
		return mInnerAdapter.getItemId(translate(i));
	}

	public int getItemViewType(int i) {
		return mInnerAdapter.getItemViewType(translate(i));
	}

	public View getView(int i, View view, ViewGroup viewgroup) {
		return mInnerAdapter.getView(translate(i), view, viewgroup);
	}

	public int getViewTypeCount() {
		return mInnerAdapter.getViewTypeCount();
	}

	public boolean hasStableIds() {
		return mInnerAdapter.hasStableIds();
	}

	public boolean isEmpty() {
		return mInnerAdapter.isEmpty();
	}

	public boolean isEnabled(int i) {
		return mInnerAdapter.isEnabled(translate(i));
	}

	public void registerDataSetObserver(DataSetObserver datasetobserver) {
		mInnerAdapter.registerDataSetObserver(datasetobserver);
	}

	protected abstract int translate(int i);

	public void unregisterDataSetObserver(DataSetObserver datasetobserver) {
		mInnerAdapter.unregisterDataSetObserver(datasetobserver);
	}

	public static interface TranslationListAdapter extends ListAdapter {

		int getColumnCount();

		int[][] getLayoutArray();

		boolean isHorizontal();
	}

}
