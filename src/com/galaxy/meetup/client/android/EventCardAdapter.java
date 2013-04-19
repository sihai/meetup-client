/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.EventDestinationCardView;
import com.galaxy.meetup.client.android.ui.view.ItemClickListener;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EventCardAdapter extends EsCursorAdapter {

	private static boolean sInitialized;
    private static int sItemMargin;
    private static double sLargeDisplayTypeSizeCutoff = 6.9000000000000004D;
    private static int sScreenDisplayType;
    protected EsAccount mAccount;
    protected ItemClickListener mItemClickListener;
    private boolean mLandscape;
    protected android.view.View.OnClickListener mOnClickListener;
    
    public EventCardAdapter(Context context, EsAccount esaccount, Cursor cursor, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, ColumnGridView columngridview) {
        super(context, null);
        boolean flag = true;
        mAccount = esaccount;
        mOnClickListener = onclicklistener;
        mItemClickListener = itemclicklistener;
        int j;
        int i;
        boolean flag1;
        int k;
        if(!sInitialized)
        {
            sInitialized = true;
            WindowManager windowmanager = (WindowManager)context.getSystemService("window");
            DisplayMetrics displaymetrics = new DisplayMetrics();
            windowmanager.getDefaultDisplay().getMetrics(displaymetrics);
            float f = (float)displaymetrics.widthPixels / displaymetrics.xdpi;
            float f1 = (float)displaymetrics.heightPixels / displaymetrics.ydpi;
            if(Math.sqrt(f * f + f1 * f1) >= sLargeDisplayTypeSizeCutoff)
                k = ((true) ? 1 : 0);
            else
                k = 0;
            sScreenDisplayType = k;
            sItemMargin = (int)(context.getResources().getDimension(R.dimen.card_margin_percentage) * (float)Math.min(displaymetrics.widthPixels, displaymetrics.heightPixels));
        }
        i = context.getResources().getConfiguration().orientation;
        flag1 = false;
        if(i == 2)
            flag1 = true;
        mLandscape = flag1;
        if(mLandscape)
            j = ((true) ? 1 : 0);
        else
            j = 2;
        columngridview.setOrientation(j);
        if(sScreenDisplayType != 0) {
        	columngridview.setColumnCount(2);
        } else {
        	columngridview.setColumnCount(1);
        }
        
        columngridview.setItemMargin(sItemMargin);
        columngridview.setPadding(sItemMargin, sItemMargin, sItemMargin, sItemMargin);
        columngridview.setRecyclerListener(new ColumnGridView.RecyclerListener() {

            public final void onMovedToScrapHeap(View view)
            {
                if(view instanceof Recyclable)
                    ((Recyclable)view).onRecycle();
            }

        });
    }

    public final void bindView(View view, Context context, Cursor cursor)
    {
        if(cursor.getPosition() < getCount())
        {
            EventDestinationCardView eventdestinationcardview = (EventDestinationCardView)view;
            byte abyte0[] = cursor.getBlob(1);
            PlusEvent plusevent = (PlusEvent)JsonUtil.fromByteArray(abyte0, PlusEvent.class);
            eventdestinationcardview.init(cursor, sScreenDisplayType, 0, mOnClickListener, mItemClickListener, null, null, null);
            eventdestinationcardview.bindData(mAccount, plusevent);
            int i;
            ColumnGridView.LayoutParams layoutparams;
            if(mLandscape)
                i = 1;
            else
                i = 2;
            layoutparams = new ColumnGridView.LayoutParams(i, -3, 1, 1);
            if(!mLandscape && sScreenDisplayType == 0)
                layoutparams.height = -2;
            eventdestinationcardview.setLayoutParams(layoutparams);
        }
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        return new EventDestinationCardView(context);
    }
}
