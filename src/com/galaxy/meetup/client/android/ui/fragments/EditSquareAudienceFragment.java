/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.ApiUtils;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.PeopleListItemView;

/**
 * 
 * @author sihai
 *
 */
public class EditSquareAudienceFragment extends HostedEsFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks, android.widget.AbsListView.OnScrollListener, android.widget.AdapterView.OnItemClickListener {

	private static final String SQUARES_PROJECTION[] = {
        "_id", "square_id", "square_name", "photo_url"
    };
    private static Bitmap sDefaultSquareImage;
    private EditAudienceAdapter mAdapter;
    private ImageCache mAvatarCache;
    private ListView mListView;
    private boolean mLoaderError;
    private final EsServiceListener mServiceListener = new EsServiceListener() {

        public final void onGetSquaresComplete(int i, ServiceResult serviceresult)
        {
            if(mNewerReqId != null && i == mNewerReqId.intValue())
            {
                mNewerReqId = null;
                if(serviceresult.hasError() && !mLoaderError)
                    Toast.makeText(getActivity(), getString(R.string.people_list_error), 0).show();
                updateSpinner();
            }
        }
    };
    private boolean mSquaresLoaderActive;
    
    public EditSquareAudienceFragment()
    {
        mSquaresLoaderActive = true;
    }

    private boolean isLoading()
    {
        boolean flag;
        if(mAdapter == null || mAdapter.getCursor() == null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void updateView(View view)
    {
        View view1 = view.findViewById(0x102000a);
        View view2 = view.findViewById(R.id.server_error);
        if(mLoaderError)
        {
            view1.setVisibility(8);
            view2.setVisibility(0);
            showContent(view);
        } else
        if(isLoading())
        {
            view1.setVisibility(8);
            view2.setVisibility(8);
            showEmptyViewProgress(view);
        } else
        if(isEmpty())
        {
            view1.setVisibility(8);
            view2.setVisibility(8);
            showEmptyView(view, getString(R.string.no_squares));
        } else
        {
            view1.setVisibility(0);
            view2.setVisibility(8);
            showContent(view);
        }
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PEOPLE_PICKER;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(isLoading() || mAdapter.isEmpty())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final boolean isProgressIndicatorVisible()
    {
        boolean flag;
        if(super.isProgressIndicatorVisible() || mSquaresLoaderActive)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
        FragmentActivity fragmentactivity = getActivity();
        if(i == 0 && j == -1)
        {
            fragmentactivity.setResult(j, intent);
            fragmentactivity.finish();
        }
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mAdapter = new EditAudienceAdapter(activity);
        mAvatarCache = ImageCache.getInstance(activity);
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        getLoaderManager().initLoader(0, null, this);
        if(sDefaultSquareImage == null)
            sDefaultSquareImage = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_community_avatar)).getBitmap();
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
    	Object obj = null;
    	if(0 == i) {
    		obj = new SquareListLoader(getActivity(), getAccount(), SQUARES_PROJECTION);
    	}
    	return ((Loader) (obj));
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.edit_audience_fragment, viewgroup, false);
        mListView = (ListView)view.findViewById(0x102000a);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        return view;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        if(view instanceof PeopleListItemView)
        {
            PeopleListItemView peoplelistitemview = (PeopleListItemView)view;
            String s = peoplelistitemview.getGaiaId();
            String s1 = peoplelistitemview.getContactName();
            startActivityForResult(Intents.getSelectSquareCategoryActivityIntent(getActivity(), mAccount, s1, s, s1), 0);
        }
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        boolean flag;
        if(cursor == null)
            flag = true;
        else
            flag = false;
        mLoaderError = flag;
        if(0 == loader.getId()) {
        	mSquaresLoaderActive = false;
            if((loader instanceof SquareListLoader) && ((SquareListLoader)loader).isDataStale())
                refresh();
            mAdapter.changeCursor(cursor);
            updateView(getView());
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == R.id.refresh)
        {
            refresh();
            flag = true;
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        hostactionbar.showTitle(getActivity().getIntent().getStringExtra("title"));
        hostactionbar.showRefreshButton();
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        updateView(getView());
    }

    public void onScroll(AbsListView abslistview, int i, int j, int k)
    {
    }

    public void onScrollStateChanged(AbsListView abslistview, int i)
    {
        if(i == 2)
            mAvatarCache.pause();
        else
            mAvatarCache.resume();
    }

    public final void refresh()
    {
        super.refresh();
        if(mNewerReqId == null && getActivity() != null)
            mNewerReqId = Integer.valueOf(EsService.getSquares(getActivity(), mAccount));
        updateSpinner();
    }
    
    private final class EditAudienceAdapter extends EsCursorAdapter implements SectionIndexer {

    	private EsAlphabetIndexer mIndexer;
    	
	    public EditAudienceAdapter(Context context)
	    {
	        super(context, null);
	    }
	    
	    public final void bindView(View view, Context context, Cursor cursor)
	    {
	        PeopleListItemView peoplelistitemview = (PeopleListItemView)view;
	        peoplelistitemview.setGaiaIdAndAvatarUrl(cursor.getString(1), ApiUtils.prependProtocol(cursor.getString(3)));
	        peoplelistitemview.setContactName(cursor.getString(2));
	        peoplelistitemview.updateContentDescription();
	    }

	    public final void changeCursor(Cursor cursor)
	    {
	        if(cursor != null)
	            mIndexer = new EsAlphabetIndexer(cursor, 2);
	        super.changeCursor(cursor);
	    }
	
	    public final int getItemViewType(int i)
	    {
	        return 0;
	    }
	
	    public final int getPositionForSection(int i)
	    {
	        int j;
	        if(mIndexer == null)
	            j = 0;
	        else
	            j = mIndexer.getPositionForSection(i);
	        return j;
	    }
	
	    public final int getSectionForPosition(int i)
	    {
	        int j;
	        if(mIndexer == null || i < 0)
	            j = 0;
	        else
	            j = mIndexer.getSectionForPosition(i);
	        return j;
	    }
	
	    public final Object[] getSections()
	    {
	        Object aobj[];
	        if(mIndexer == null)
	            aobj = null;
	        else
	            aobj = mIndexer.getSections();
	        return aobj;
	    }
	
	    public final int getViewTypeCount()
	    {
	        return 1;
	    }
	
	    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
	    {
	        PeopleListItemView peoplelistitemview = PeopleListItemView.createInstance(context);
	        peoplelistitemview.setCheckBoxVisible(false);
	        peoplelistitemview.setDefaultAvatar(EditSquareAudienceFragment.sDefaultSquareImage);
	        return peoplelistitemview;
	    }
	}
}
