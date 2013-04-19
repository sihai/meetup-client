/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.galaxy.meetup.client.android.common.EsCompositeCursorAdapter;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.SquareListItemView;

/**
 * 
 * @author sihai
 *
 */
public class SquareCardAdapter extends EsCompositeCursorAdapter {

	private static boolean sInitialized;
    private static int sInvitationMinHeight;
    private static int sItemMargin;
    private static float sLargeDisplayTypeSizeCutoff = 6.9F;
    private static int sMinHeight;
    private static int sMinWidth;
    private static int sScreenDisplayType;
    protected EsAccount mAccount;
    private int mCardType;
    private ColumnGridView mColumnGridView;
    private LayoutInflater mInflater;
    private boolean mLandscape;
    protected SquareListItemView.OnItemClickListener mOnItemClickListener;
    
    public SquareCardAdapter(Context context, EsAccount esaccount, SquareListItemView.OnItemClickListener onitemclicklistener, ColumnGridView columngridview) {
    	super(context);
        boolean flag = true;
        mCardType = 0;
        int i;
        boolean flag1;
        if(!sInitialized)
        {
            sInitialized = flag;
            WindowManager windowmanager = (WindowManager)context.getSystemService("window");
            DisplayMetrics displaymetrics = new DisplayMetrics();
            windowmanager.getDefaultDisplay().getMetrics(displaymetrics);
            float f = (float)displaymetrics.widthPixels / displaymetrics.xdpi;
            float f1 = (float)displaymetrics.heightPixels / displaymetrics.ydpi;
            
            int j;
            if(FloatMath.sqrt(f * f + f1 * f1) >= sLargeDisplayTypeSizeCutoff)
                j = ((flag) ? 1 : 0);
            else
                j = 0;
            sScreenDisplayType = j;
            sItemMargin = (int)(context.getResources().getDimension(R.dimen.card_margin_percentage) * (float)Math.min(displaymetrics.widthPixels, displaymetrics.heightPixels));
            sMinWidth = context.getResources().getDimensionPixelSize(R.dimen.square_card_min_width);
            sMinHeight = context.getResources().getDimensionPixelSize(R.dimen.square_card_min_height);
            sInvitationMinHeight = context.getResources().getDimensionPixelSize(R.dimen.square_card_invitation_min_height);
        }
        mAccount = esaccount;
        mOnItemClickListener = onitemclicklistener;
        mColumnGridView = columngridview;
        addPartition(false, false);
        addPartition(false, false);
        mInflater = LayoutInflater.from(context);
        i = context.getResources().getConfiguration().orientation;
        flag1 = false;
        if(i == 2)
            flag1 = flag;
        mLandscape = flag1;
        if(mLandscape)
        {
            columngridview.setOrientation(1);
            columngridview.setColumnCount(-1);
            columngridview.setMinColumnWidth(sMinHeight);
        } else
        {
            columngridview.setOrientation(2);
            if(sScreenDisplayType != 0)
            	columngridview.setColumnCount(2);
            else 
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

    private boolean showTallDescriptionHeader()
    {
        boolean flag = true;
        if(!mLandscape && sScreenDisplayType != 1)
            flag = false;
        return flag;
    }

    protected final void bindView(View view, int i, Cursor cursor, int j) {
        int k;
        int l;
        k = -2;
        l = 1;
        if(0 == i) {
        	int i1 = mColumnGridView.getColumnCount();
            if(!mLandscape)
                l = 2;
            if(mLandscape)
                k = sMinWidth;
            view.setLayoutParams(new ColumnGridView.LayoutParams(l, k, i1, i1));
        } else if(1 == i) {
        	if(cursor.getPosition() < getCount(l))
            {
                SquareListItemView squarelistitemview = (SquareListItemView)view;
                boolean flag;
                int j1;
                boolean flag1;
                int k1;
                int l1;
                if(mCardType != 3)
                    flag = true;
                else
                    flag = false;
                j1 = mCardType;
                flag1 = false;
                if(j1 == 2)
                    flag1 = true;
                squarelistitemview.init(cursor, mOnItemClickListener, flag, flag1);
                if(mLandscape)
                    k1 = l;
                else
                    k1 = 2;
                if(mLandscape)
                    l1 = sMinWidth;
                else
                    l1 = k;
                view.setLayoutParams(new ColumnGridView.LayoutParams(k1, l1, l, l));
            }
        }
       
    }

    public final void changeDescriptionHeaderCursor(Cursor cursor)
    {
        super.changeCursor(0, cursor);
    }

    public final void changeSquaresCursor(Cursor cursor, int i)
    {
        super.changeCursor(1, cursor);
        if(mCardType != i)
        {
            mCardType = i;
            if(mLandscape)
            {
                ColumnGridView columngridview = mColumnGridView;
                int j;
                if(mCardType == 1)
                    j = sInvitationMinHeight;
                else
                    j = sMinHeight;
                columngridview.setMinColumnWidth(j);
            }
            mColumnGridView.setSelectionToTop();
        }
    }

    protected final int getItemViewType(int i, int j) {
        int k = 1;
        if(0 == i) {
        	if(showTallDescriptionHeader())
                k = 3;
            else
                k = 2;
        } else {
        	if(mCardType != k)
                k = 0;
        }
        
        return k;
    }

    public final int getViewTypeCount()
    {
        return 4;
    }

    protected final View newView(Context context, int partion, Cursor cursor, int position, ViewGroup parent) {
    	View view = null;
    	if(0 == partion) {
    		if(showTallDescriptionHeader())
                view = mInflater.inflate(R.layout.square_description_header_tall, parent, false);
            else
                view = mInflater.inflate(R.layout.square_description_header, parent, false);
    	} else if(1 == partion) {
    		if(mCardType == 1)
                view = mInflater.inflate(R.layout.square_list_invitation_view, parent, false);
            else
                view = mInflater.inflate(R.layout.square_list_item_view, parent, false);
    	}
    	
        return view;
    }

}
