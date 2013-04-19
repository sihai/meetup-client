/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.HashSet;
import java.util.Set;

import android.animation.Animator;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.galaxy.meetup.client.android.common.EsCompositeCursorAdapter;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.controller.ComposeBarController;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.DummyCardView;
import com.galaxy.meetup.client.android.ui.view.EmotiShareCardView;
import com.galaxy.meetup.client.android.ui.view.EventStreamCardView;
import com.galaxy.meetup.client.android.ui.view.HangoutCardView;
import com.galaxy.meetup.client.android.ui.view.ImageCardView;
import com.galaxy.meetup.client.android.ui.view.ItemClickListener;
import com.galaxy.meetup.client.android.ui.view.LinksCardView;
import com.galaxy.meetup.client.android.ui.view.PlaceReviewCardView;
import com.galaxy.meetup.client.android.ui.view.SkyjamCardView;
import com.galaxy.meetup.client.android.ui.view.SquareCardView;
import com.galaxy.meetup.client.android.ui.view.StreamCardView;
import com.galaxy.meetup.client.android.ui.view.TextCardView;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.client.util.ResourceRedirector;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 *
 */
public class StreamAdapter extends EsCompositeCursorAdapter implements
	StreamCardView.ViewedListener, TranslationAdapter.TranslationListAdapter {

	private static Interpolator sDecelerateInterpolator = new DecelerateInterpolator();
    protected static ScreenMetrics sScreenMetrics;
    protected EsAccount mAccount;
    private int mBoxLayout[][];
    private int mCardTypes[];
    private ComposeBarController mComposeBarController;
    private ItemClickListener mItemClickListener;
    protected boolean mLandscape;
    private boolean mMarkPostsAsRead;
    private android.view.View.OnClickListener mOnClickListener;
    private StreamCardView.StreamMediaClickListener mStreamMediaClickListener;
    private StreamCardView.StreamPlusBarClickListener mStreamPlusBarClickListener;
    private ViewUseListener mViewUseListener;
    private final Set mViewerHasReadPosts = new HashSet();
    protected int mVisibleIndex;
    
    public StreamAdapter(final Context context, ColumnGridView columngridview, EsAccount esaccount, View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, ViewUseListener viewuselistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener, ComposeBarController composebarcontroller)
    {
    	super(context);
        int i = 1;
        mVisibleIndex = 0x80000000;
        addPartition(false, false);
        addPartition(false, false);
        addPartition(false, false);
        mAccount = esaccount;
        mOnClickListener = onclicklistener;
        mItemClickListener = itemclicklistener;
        mStreamPlusBarClickListener = streamplusbarclicklistener;
        mStreamMediaClickListener = streammediaclicklistener;
        mViewUseListener = viewuselistener;
        mMarkPostsAsRead = false;
        int j = context.getResources().getConfiguration().orientation;
        boolean flag = false;
        if(j == 2)
            flag = true;
        mLandscape = flag;
        if(sScreenMetrics == null)
            sScreenMetrics = ScreenMetrics.getInstance(context);
        mComposeBarController = composebarcontroller;
        int k;
        if(mLandscape)
            k = i;
        else
            k = 2;
        columngridview.setOrientation(k);
        if(sScreenMetrics.screenDisplayType != 0)
            i = 2;
        columngridview.setColumnCount(i);
        columngridview.setItemMargin(sScreenMetrics.itemMargin);
        columngridview.setPadding(sScreenMetrics.itemMargin, sScreenMetrics.itemMargin, sScreenMetrics.itemMargin, sScreenMetrics.itemMargin);
        columngridview.setOnScrollListener(new ColumnGridView.OnScrollListener() {

            public final void onScroll(ColumnGridView columngridview1, int l, int i1, int j1, int k1, int l1)
            {
                if(mComposeBarController != null)
                    mComposeBarController.onScroll(columngridview1, l, i1, j1, k1, l1);
                if(android.os.Build.VERSION.SDK_INT >= 12 && j1 != 0)
                {
                    int i2 = 0x80000000;
                    int j2 = 50;
                    int k2 = 0;
                    while(k2 < j1) 
                    {
                        int l2 = l + k2;
                        if(l2 <= mVisibleIndex)
                            continue;
                        i2 = Math.max(i2, l2);
                        final View view = columngridview1.getChildAt(k2);
                        if(view.getId() == R.id.compose_bar)
                            continue;
                        ScreenMetrics screenmetrics = ScreenMetrics.getInstance(context);
                        boolean flag1;
                        int i3;
                        int j3;
                        float f;
                        float f1;
                        float f2;
                        float f3;
                        ViewPropertyAnimator viewpropertyanimator;
                        if(context.getResources().getConfiguration().orientation == 2)
                            flag1 = true;
                        else
                            flag1 = false;
                        i3 = (int)view.getTranslationX();
                        j3 = (int)view.getTranslationY();
                        if(flag1)
                            f = screenmetrics.longDimension / 3;
                        else
                            f = 0.0F;
                        view.setTranslationX(f);
                        if(flag1)
                            f1 = 0.0F;
                        else
                            f1 = screenmetrics.longDimension / 3;
                        view.setTranslationY(f1);
                        if(flag1)
                            f2 = 0.0F;
                        else
                            f2 = 10F;
                        view.setRotationX(f2);
                        if(flag1)
                            f3 = -10F;
                        else
                            f3 = 0.0F;
                        view.setRotationY(f3);
                        viewpropertyanimator = view.animate().rotationX(0.0F).rotationY(0.0F).translationX(i3).translationY(j3).setDuration(500L).setInterpolator(StreamAdapter.sDecelerateInterpolator);
                        viewpropertyanimator.setListener(new android.animation.Animator.AnimatorListener() {

                            public final void onAnimationCancel(Animator animator)
                            {
                            }

                            public final void onAnimationEnd(Animator animator)
                            {
                            	view.setTranslationX(0.0F);
                            	view.setTranslationY(0.0F);
                            	view.setRotationX(0.0F);
                            	view.setRotationY(0.0F);
                            	view.invalidate();
                            }

                            public final void onAnimationRepeat(Animator animator)
                            {
                            }

                            public final void onAnimationStart(Animator animator)
                            {
                            }
                        });
                        
                        if(android.os.Build.VERSION.SDK_INT >= 14)
                            viewpropertyanimator.setStartDelay(j2).start();
                        j2 += 50;
                        k2++;
                    }
                    mVisibleIndex = Math.max(mVisibleIndex, i2);
                }
            }

            public final void onScrollStateChanged(ColumnGridView columngridview1, int l)
            {
                if(mComposeBarController != null)
                    mComposeBarController.onScrollStateChanged(columngridview1, l);
            }
        });
        
        columngridview.setRecyclerListener(new ColumnGridView.RecyclerListener() {

            public final void onMovedToScrapHeap(View view) {
                if(view instanceof Recyclable)
                    ((Recyclable)view).onRecycle();
            }
        });
        
    }
    
    private boolean isBoxStart(int i)
    {
        // TODO
    	return false;
    }

    private void recreateBoxLayout()
    {
        // TODO
    	
    }

    public void bindStreamHeaderView(View view, Cursor cursor)
    {
    }

    public void bindStreamView(View view, Cursor cursor)
    {
        int i;
        int j;
        StreamCardView streamcardview;
        byte byte0;
        i = cursor.getPosition() + getPositionForPartition(1);
        j = mCardTypes[i];
        streamcardview = (StreamCardView)view;
        if(mLandscape)
            byte0 = 1;
        else
            byte0 = 2;
        
        int k;
        int l;
        
        switch(j) {
        case 0:
        	k = 1;
            l = 1;
        	break;
        case 1:
        	if(mLandscape)
                k = 2;
            else
                k = 1;
            if(mLandscape)
                l = 1;
            else
                l = 2;
        	break;
        case 2:
        	if(mLandscape)
                k = 1;
            else
                k = 2;
            if(mLandscape)
                l = 2;
            else
                l = 1;
        	break;
        case 3:
        	 k = 2;
             l = 2;
        	break;
        default:
        	throw new IllegalStateException();
        }
        
        ColumnGridView.LayoutParams layoutparams = new ColumnGridView.LayoutParams(byte0, -3, k, l);
        layoutparams.isBoxStart = isBoxStart(i);
        if(!mLandscape && sScreenMetrics.screenDisplayType == 0 && ((view instanceof TextCardView) || (view instanceof EventStreamCardView)))
            layoutparams.height = -2;
        streamcardview.setLayoutParams(layoutparams);
        streamcardview.init(cursor, j, 0, mOnClickListener, mItemClickListener, this, mStreamPlusBarClickListener, mStreamMediaClickListener);
        if(mViewUseListener != null)
            mViewUseListener.onViewUsed(i);
        return;
        
    }

    protected final void bindView(View view, int i, Cursor cursor, int j) {
        if(view instanceof ResourceConsumer)
            ((ResourceConsumer)view).unbindResources();
        if(0 == i) {
        	bindStreamHeaderView(view, cursor);
        } else if(1 == i) {
        	bindStreamView(view, cursor);
        } else {
        	 if(view instanceof ResourceConsumer)
                 ((ResourceConsumer)view).bindResources();
        }
    }

    public final void changeStreamCursor(Cursor cursor)
    {
        super.changeCursor(1, cursor);
        recreateBoxLayout();
    }

    public final void changeStreamHeaderCursor(Cursor cursor)
    {
        int i = getCount(0);
        super.changeCursor(0, cursor);
        if(getCount(0) != i)
            recreateBoxLayout();
    }

    public final int getColumnCount()
    {
        int i;
        if(sScreenMetrics.screenDisplayType == 0)
            i = 1;
        else
            i = 2;
        return i;
    }

    protected final int getItemViewType(int i, int j) {
    	int k = 0;
    	if(0 == i) {
    		k = getStreamHeaderViewType(j);
    	} else if (1 == i) {
    		k = getStreamItemViewType(j);
    	} else {
    		
    	}
    	
        return k;
    }

    public final int[][] getLayoutArray()
    {
        return mBoxLayout;
    }

    public int getStreamHeaderViewType(int i) {
        return 0;
    }

    public int getStreamItemViewType(int i) {
        int j;
        long l;
        j = 1;
        Cursor cursor = getCursor(j);
        cursor.moveToPosition(i);
        l = cursor.getLong(15);
        if((4096L & l) == 0L) 
        	if((8192L & l) != 0L)
                j = 5;
            else
            if((16384L & l) != 0L)
                j = 4;
            else
            if((0x400000L & l) != 0L)
                j = 9;
            else
            if((0x10000L & l) != 0L)
                j = 7;
            else
            if((0x300000L & l) != 0L)
                j = 8;
            else
            if((160L & l) != 0L)
            {
                if((32772L & l) != 0L)
                    j = 2;
                else
                    j = 3;
            } else
            if((15L & l) == 0L)
                j = 0; 
        else 
        	j = 6;
        return j;
    }

    public int getViewTypeCount()
    {
        return 10;
    }

    public boolean hasStableIds()
    {
        return false;
    }

    public boolean isEmpty()
    {
        boolean flag = true;
        if(getCount(1) != 0)
            flag = false;
        return flag;
    }

    public final boolean isHorizontal()
    {
        return mLandscape;
    }

    public View newStreamHeaderView(Context context, Cursor cursor)
    {
        return null;
    }

    public View newStreamView(Context context, Cursor cursor, ViewGroup viewgroup) {
        long l = cursor.getLong(15);
        Object obj;
        if((4096L & l) != 0L)
            obj = new EventStreamCardView(context);
        else
        if((8192L & l) != 0L)
            obj = new HangoutCardView(context);
        else
        if((16384L & l) != 0L) {
            obj = new SkyjamCardView(context);
        } else {
            ResourceRedirector.getInstance();
            if(Property.ENABLE_EMOTISHARE.getBoolean() && (0x400000L & l) != 0L)
                obj = new EmotiShareCardView(context);
            else
            if((0x10000L & l) != 0L)
                obj = new PlaceReviewCardView(context);
            else
            if((160L & l) != 0L)
            {
                if((32772L & l) != 0L)
                    obj = new LinksCardView(context);
                else
                    obj = new ImageCardView(context);
            } else
            if((0x300000L & l) != 0L)
                obj = new SquareCardView(context);
            else
            if((0x20000L & l) != 0L)
                obj = new LinksCardView(context);
            else
            if((15L & l) != 0L)
                obj = new TextCardView(context);
            else
                obj = new DummyCardView(context);
        }
        return ((View) (obj));
    }

    protected final View newView(Context context, int partion, Cursor cursor, int position, ViewGroup parent) {
    	View view = null;
    	if(0 == partion) {
    		view = newStreamHeaderView(context, cursor);
    	} else if(1 == partion) {
    		view = newStreamView(context, cursor, parent);
    	} else {
    		
    	}
    	return view;
    }

    public final void onPause()
    {
        if(!mViewerHasReadPosts.isEmpty())
        {
            EsService.markActivitiesAsRead(getContext(), mAccount, (String[])mViewerHasReadPosts.toArray(new String[mViewerHasReadPosts.size()]));
            mViewerHasReadPosts.clear();
        }
    }

    public final void onStreamCardViewed(String s)
    {
        if(mMarkPostsAsRead)
            mViewerHasReadPosts.add(s);
    }

    public void resetAnimationState()
    {
        mVisibleIndex = 0x80000000;
    }

    public final void setMarkPostsAsRead(boolean flag)
    {
        mMarkPostsAsRead = flag;
    }

    public static interface StreamQuery {

        String PROJECTION_ACTIVITY[] = {
            "_id", "activity_id", "author_id", "name", "avatar", "plus_one_data", "total_comment_count", "loc", "created", "public", 
            "spam", "has_read", "can_reshare", "event_data", "popular_post", "content_flags", "annotation_plaintext", "title_plaintext", "original_author_id", "original_author_name"
        };
        String PROJECTION_STREAM[] = {
            "_id", "activity_id", "author_id", "name", "avatar", "plus_one_data", "total_comment_count", "loc", "created", "public", 
            "spam", "has_read", "can_reshare", "event_data", "popular_post", "content_flags", "annotation_plaintext", "title_plaintext", "original_author_id", "original_author_name", 
            "last_activity", "source_name", "embed_media", "embed_skyjam", "embed_place_review", "embed_hangout", "embed_appinvite", "embed_square", "embed_emotishare"
        };

    }

    public static interface ViewUseListener {

        void onViewUsed(int i);
    }
}
