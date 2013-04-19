/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.common.EsCompositeCursorAdapter;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.ui.fragments.EventActiveState;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.EventActionListener;
import com.galaxy.meetup.client.android.ui.view.EventActivityFrameCardLayout;
import com.galaxy.meetup.client.android.ui.view.EventActivityPhotoCardLayout;
import com.galaxy.meetup.client.android.ui.view.EventActivityUpdateCardLayout;
import com.galaxy.meetup.client.android.ui.view.EventDetailsCardLayout;
import com.galaxy.meetup.client.util.ScreenMetrics;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EventDetailsActivityAdapter extends EsCompositeCursorAdapter {

    private static ScreenMetrics sScreenMetrics;
    private EventActionListener mActionListener;
    private EventActiveState mEventState;
    private boolean mLandscape;
    private HashMap mResolvedPeople;
    private ViewUseListener mViewUseListener;
    private boolean mWrapContent;
    
    public EventDetailsActivityAdapter(Context context, ColumnGridView columngridview, ViewUseListener viewuselistener, EventActionListener eventactionlistener)
    {
        super(context);
        int i = 1;
        addPartition(false, false);
        addPartition(false, false);
        mViewUseListener = viewuselistener;
        mActionListener = eventactionlistener;
        boolean flag;
        int j;
        boolean flag1;
        int k;
        if(context.getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        mLandscape = flag;
        if(sScreenMetrics == null)
            sScreenMetrics = ScreenMetrics.getInstance(context);
        j = sScreenMetrics.screenDisplayType;
        flag1 = false;
        if(j == 0)
        {
            boolean flag2 = mLandscape;
            flag1 = false;
            if(!flag2)
                flag1 = true;
        }
        mWrapContent = flag1;
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
        columngridview.setRecyclerListener(new ColumnGridView.RecyclerListener() {

            public final void onMovedToScrapHeap(View view)
            {
                if(view instanceof Recyclable)
                    ((Recyclable)view).onRecycle();
            }

        });
    }

    protected final void bindView(View view, int i, Cursor cursor, int j)
    {
        if(cursor.isClosed()) {
        	return;
        }
        
        if(0 == i) {
        	EventDetailsCardLayout eventdetailscardlayout = (EventDetailsCardLayout)view;
            byte abyte1[] = cursor.getBlob(1);
            PlusEvent plusevent = (PlusEvent)JsonUtil.fromByteArray(abyte1, PlusEvent.class);
            if(plusevent != null)
                eventdetailscardlayout.bind(plusevent, mEventState, mActionListener);
        } else if(1 == i) {
        	switch(cursor.getInt(1)) {
	        	case 1:
	        		break;
	        	case 2:
	        		break;
	        	case 3:
	        		break;
	        	case 4:
	        		EventActivityFrameCardLayout eventactivityframecardlayout = (EventActivityFrameCardLayout)view;
	                int k = cursor.getInt(1);
	                long l = cursor.getLong(4);
	                List list = ((EsEventData.EventCoalescedFrame)JsonUtil.fromByteArray(cursor.getBlob(5), EsEventData.EventCoalescedFrame.class)).people;
	                if(mResolvedPeople != null)
	                {
	                    for(int i1 = -1 + list.size(); i1 >= 0; i1--)
	                    {
	                        EsEventData.EventPerson eventperson = (EsEventData.EventPerson)list.get(i1);
	                        if(eventperson.gaiaId == null)
	                            continue;
	                        EsEventData.ResolvedPerson resolvedperson = (EsEventData.ResolvedPerson)mResolvedPeople.get(eventperson.gaiaId);
	                        if(resolvedperson != null)
	                            eventperson.name = resolvedperson.name;
	                    }

	                }
	                eventactivityframecardlayout.bind(k, l, list, mActionListener);
	        		break;
	        	case 5:
	        		EventActivityPhotoCardLayout eventactivityphotocardlayout = (EventActivityPhotoCardLayout)view;
	                String s = cursor.getString(2);
	                String s1 = cursor.getString(3);
	                String s2 = cursor.getString(8);
	                String s3 = cursor.getString(6);
	                byte abyte0[] = cursor.getBlob(5);
	                eventactivityphotocardlayout.bind(s1, s, cursor.getLong(4), s3, abyte0, mActionListener, s2);
	        		break;
        		default:
        			break;
        	}
        }
        
        if(mViewUseListener != null)
            mViewUseListener.onViewUsed(j);
    }

    public final void changeActivityCursor(Cursor cursor)
    {
        changeCursor(1, cursor);
    }

    public final void changeInfoCursor(Cursor cursor, EventActiveState eventactivestate)
    {
        changeCursor(0, cursor);
        mEventState = eventactivestate;
    }

    public final void setResolvedPeople(HashMap hashmap)
    {
        mResolvedPeople = hashmap;
    }
    
    protected final int getItemViewType(int i, int j)
    {
        int k = 0;
        if(0 == i) {
        	k = 0;
        } else if(1 == i) {
        	Cursor cursor = null;
            cursor = getCursor(1);
            if(null != cursor && !cursor.isClosed()) {
            	cursor.moveToPosition(j);
                switch(cursor.getInt(1))
                {
                default:
                    k = 0;
                    break;

                case 1: // '\001'
                case 2: // '\002'
                case 3: // '\003'
                case 4: // '\004'
                    k = 3;
                    break;

                case 5: // '\005'
                    k = 2;
                    break;

                case 100: // 'd'
                    k = 1;
                    break;
                }
            }
        }
        
        return k;
    }

    public final int getViewTypeCount()
    {
        return 4;
    }

    public final boolean hasStableIds()
    {
        return false;
    }

    public final boolean isWrapContentEnabled()
    {
        return mWrapContent;
    }

    protected final View newView(Context context, int partition, Cursor cursor, int position, ViewGroup parent)
    {
        View view = null;
        if(0 == partition) {
        	view = new EventDetailsCardLayout(context);
        } else if(1 == partition) {
        	switch(cursor.getInt(1))
            {
            default:
            	view = null;
                break;

            case 1: // '\001'
            case 2: // '\002'
            case 3: // '\003'
            case 4: // '\004'
            	view = new EventActivityFrameCardLayout(context);
                if(mWrapContent)
                    ((View) (view)).setLayoutParams(new android.view.ViewGroup.LayoutParams(-1, -2));
                break;

            case 5: // '\005'
            	view = new EventActivityUpdateCardLayout(context);
                if(mWrapContent)
                    ((View) (view)).setLayoutParams(new android.view.ViewGroup.LayoutParams(-1, -2));
                break;

            case 100: // 'd'
            	view = new EventActivityPhotoCardLayout(context);
                break;
            }
        }
        return view;
    }
    
    public static interface ViewUseListener
    {

        public abstract void onViewUsed(int i);
    }
}
