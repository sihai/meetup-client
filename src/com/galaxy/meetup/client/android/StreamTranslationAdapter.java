/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.database.DataSetObserver;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * 
 * @author sihai
 *
 */
public class StreamTranslationAdapter extends TranslationAdapter {

	private final AdapterDataSetObserver mObserver = new AdapterDataSetObserver();
    private SparseIntArray mTranslation;
    
    public StreamTranslationAdapter(TranslationAdapter.TranslationListAdapter translationlistadapter) {
        super(translationlistadapter);
        if(mInnerAdapter != null)
            mInnerAdapter.registerDataSetObserver(mObserver);
    }
    

    protected final int translate(int i)
    {
        if(mTranslation != null) {
        	return mTranslation.get(i, i); 
        } else { 
             mTranslation = new SparseIntArray();
             int j = mInnerAdapter.getColumnCount();
             boolean flag = mInnerAdapter.isHorizontal();
             int ai[][] = mInnerAdapter.getLayoutArray();
             if(ai != null) { 
            	 // TODO
            	 /**
            	  * if(j == 1)
            continue;
        int ai1[] = new int[j];
        Arrays.fill(ai1, -1);
        int k = 0;
        do
        {
            int l = getNextPosition(j, flag, ai, ai1);
            if(l < 0)
                continue;
            if(k == l)
            {
                k++;
            } else
            {
                mTranslation.put(k, l);
                k++;
            }
        } while(true);
            	  */
             } else { 
            	 Log.w("TranslationAdapter", "Building translation without an array. Did you forget to set the layout?");
             }
             return mTranslation.get(i, i);
        }
    }
    
    private static int getNextPosition(int i, boolean flag, int ai[][], int ai1[]) {
        // TODO
    	return 0;
    }

	private final class AdapterDataSetObserver extends DataSetObserver {

		public final void onChanged() {
			mTranslation = null;
		}

		public final void onInvalidated() {
		}

	}
}
