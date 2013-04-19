/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbEmotishareMetadata;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEmotiShareData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.ImageResourceView;

/**
 * 
 * @author sihai
 *
 */
public class HostedEmotiShareChooserFragment extends HostedEsFragment implements
		LoaderCallbacks, OnClickListener {

	private EmotiShareGridViewAdapter mAdapter;
    private Bundle mExtras;
    private ColumnGridView mGridView;
    private View mMainView;
    private DbEmotishareMetadata mSelectedObject;
    
    public HostedEmotiShareChooserFragment()
    {
    }

    private void updateView(View view)
    {
        if(view != null)
        {
            Cursor cursor = mAdapter.getCursor();
            boolean flag;
            if(cursor != null && cursor.getCount() > 0)
                flag = true;
            else
                flag = false;
            if(flag)
                showContent(view);
            else
                showEmptyViewProgress(view);
        }
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.COMPOSE;
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

    public void onClick(View view)
    {
    	if(null == view) {
    		return;
    	}
    	
    	DbEmotishareMetadata dbemotisharemetadata;
        FragmentActivity fragmentactivity;
        dbemotisharemetadata = (DbEmotishareMetadata)view.getTag();
        if(dbemotisharemetadata == null)
        {
            Integer integer = (Integer)view.getTag(R.id.tag_position);
            if(integer != null)
                dbemotisharemetadata = mAdapter.getEmotiShareForItem(integer.intValue());
        }
        if(dbemotisharemetadata == null) {
           return;
        }
        fragmentactivity = getActivity();
        if("android.intent.action.PICK".equals(getActivity().getIntent().getAction())) {
	        EsAnalytics.recordActionEvent(fragmentactivity, mAccount, OzActions.EMOTISHARE_SELECTED, OzViews.COMPOSE, EsAnalytics.addExtrasForLogging(getExtrasForLogging(), dbemotisharemetadata));
	        Intent intent1 = new Intent();
	        intent1.putExtra("typed_image_embed", dbemotisharemetadata);
	        fragmentactivity.setResult(-1, intent1);
        } else {
        	Intent intent = Intents.getPostActivityIntent(getActivity(), mAccount, dbemotisharemetadata);
            AudienceData audiencedata;
            if(getActivity() != null && getActivity().getIntent() != null)
                audiencedata = (AudienceData)getActivity().getIntent().getParcelableExtra("audience");
            else
                audiencedata = null;
            if(audiencedata != null)
                intent.putExtra("audience", audiencedata);
            startActivity(intent);
        }
        fragmentactivity.finish();
    	
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mExtras = new Bundle();
            mExtras.putAll(bundle.getBundle("INTENT"));
        } else
        {
            mExtras = getArguments();
        }
        if(mExtras.containsKey("typed_image_embed"))
            mSelectedObject = (DbEmotishareMetadata)mExtras.getParcelable("typed_image_embed");
        getLoaderManager().initLoader(1, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        Object obj = null;
        if(i == 1)
        {
            EsAccount esaccount = mAccount;
            obj = null;
            if(esaccount != null)
                obj = new EsCursorLoader(getSafeContext(), EsProvider.appendAccountParameter(EsProvider.EMOTISHARE_URI, mAccount), EsEmotiShareData.EMOTISHARE_PROJECTION, null, null, null) {

                    public final Cursor esLoadInBackground()
                    {
                        EsEmotiShareData.ensureSynced(getSafeContext(), mAccount);
                        return super.esLoadInBackground();
                    }
                };
        }
        return ((Loader) (obj));
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        mMainView = layoutinflater.inflate(R.layout.hosted_emotishare_chooser_view, viewgroup, false);
        mGridView = (ColumnGridView)mMainView.findViewById(R.id.grid);
        Resources resources = getSafeContext().getResources();
        int i = resources.getInteger(R.integer.emotishare_icon_columns);
        int j = resources.getDimensionPixelOffset(R.dimen.emotishare_item_margin);
        mGridView.setColumnCount(i);
        mGridView.setItemMargin(j);
        mGridView.setPadding(j, j, j, j);
        mAdapter = new EmotiShareGridViewAdapter(getActivity(), null, mGridView, this);
        mGridView.setAdapter(mAdapter);
        mGridView.setSelector(R.drawable.list_selected_holo);
        if(mGridView.isInSelectionMode())
            mGridView.endSelectionMode();
        setupEmptyView(mMainView, R.string.no_emotishares);
        updateView(mMainView);
        return mMainView;
    }

    public final void onDestroyView()
    {
        super.onDestroyView();
        mGridView.unregisterSelectionListener();
        mGridView.setOnScrollListener(null);
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(loader.getId() == 1)
        {
            mAdapter.swapCursor(cursor);
            updateView(getView());
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onPause()
    {
        super.onPause();
        EmotiShareGridViewAdapter _tmp = mAdapter;
        EmotiShareGridViewAdapter.onPause();
    }

    public final void onResume()
    {
        super.onResume();
        mAdapter.onResume();
        updateView(getView());
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mExtras != null)
            bundle.putParcelable("INTENT", mExtras);
    }

    public final void onStop()
    {
        super.onStop();
        mAdapter.onStop();
    }
    
    private static DbEmotishareMetadata createEmotiShareFromCursor(Cursor cursor)
    {
        DbEmotishareMetadata dbemotisharemetadata = null;
        if(cursor != null)
        {
            byte abyte0[] = cursor.getBlob(2);
            dbemotisharemetadata = null;
            if(abyte0 != null)
                dbemotisharemetadata = DbEmotishareMetadata.deserialize(abyte0);
        }
        return dbemotisharemetadata;
    }
    
    private final class EmotiShareGridViewAdapter extends EsCursorAdapter
    {
        public final void bindView(View view, Context context, Cursor cursor)
        {
            view.setOnClickListener(mClickListener);
            int i = cursor.getPosition();
            int j = R.string.emotishare_in_list_count;
            Object aobj[] = new Object[2];
            aobj[0] = Integer.valueOf(i + 1);
            aobj[1] = Integer.valueOf(cursor.getCount());
            view.setContentDescription(context.getString(j, aobj));
            view.setTag(R.id.tag_position, Integer.valueOf(i));
            DbEmotishareMetadata dbemotisharemetadata = createEmotiShareFromCursor(cursor);
            ImageResourceView imageresourceview = (ImageResourceView)view.findViewById(R.id.image_view);
            if(dbemotisharemetadata != null)
            {
                imageresourceview.setMediaRef(dbemotisharemetadata.getIconRef());
                if(mSelectedObject != null && dbemotisharemetadata.getId() == mSelectedObject.getId())
                    view.findViewById(R.id.selector_view).setBackgroundResource(R.drawable.list_selected_holo);
            }
            imageresourceview.setTag(dbemotisharemetadata);
            ((TextView)view.findViewById(R.id.image_label)).setText(dbemotisharemetadata.getName());
            int k;
            if(mLandscape)
                k = 1;
            else
                k = 2;
            view.setLayoutParams(new ColumnGridView.LayoutParams(k, -3));
        }

        public final DbEmotishareMetadata getEmotiShareForItem(int i)
        {
            Cursor cursor = getCursor();
            DbEmotishareMetadata dbemotisharemetadata;
            if(cursor != null && cursor.moveToPosition(i))
                dbemotisharemetadata = createEmotiShareFromCursor(cursor);
            else
                dbemotisharemetadata = null;
            return dbemotisharemetadata;
        }

        public final boolean hasStableIds()
        {
            return false;
        }

        public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
        {
            return LayoutInflater.from(context).inflate(R.layout.emotishare_view, null);
        }

        public final void onResume()
        {
            super.onResume();
            if(mGrid != null)
            {
                int i = 0;
                for(int j = mGrid.getChildCount(); i < j; i++)
                    ((ImageResourceView)mGrid.getChildAt(i).findViewById(R.id.image_view)).onResume();

                mGrid.onResume();
            }
        }

        public final void onStop()
        {
            super.onStop();
            int i = 0;
            for(int j = mGrid.getChildCount(); i < j; i++)
                ((ImageResourceView)mGrid.getChildAt(i).findViewById(R.id.image_view)).onStop();

        }

        private final android.view.View.OnClickListener mClickListener;
        private final ColumnGridView mGrid;
        private final boolean mLandscape;

        public EmotiShareGridViewAdapter(Context context, Cursor cursor, ColumnGridView columngridview, android.view.View.OnClickListener onclicklistener)
        {
            super(context, null);
            int i = 1;
            boolean flag;
            if(context.getResources().getConfiguration().orientation == 2)
                flag = true;
            else
                flag = false;
            mLandscape = flag;
            if(!mLandscape)
                i = 2;
            columngridview.setOrientation(i);
            mClickListener = onclicklistener;
            mGrid = columngridview;
        }
    }

}
