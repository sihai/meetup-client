/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.content.SquareTargetData;
import com.galaxy.meetup.client.android.ui.fragments.CircleListLoader;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.PeopleUtils;

/**
 * 
 * @author sihai
 *
 */
public class AudienceView extends FrameLayout implements LoaderCallbacks,
		OnClickListener {

	static final boolean $assertionsDisabled;
    protected EsAccount mAccount;
    protected Runnable mAudienceChangedCallback;
    protected final boolean mCanRemoveChips;
    protected ViewGroup mChipContainer;
    protected final ArrayList mChips;
    protected boolean mEdited;

    static 
    {
        boolean flag;
        if(!AudienceView.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
    
    public AudienceView(Context context)
    {
        this(context, null);
    }

    public AudienceView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public AudienceView(Context context, AttributeSet attributeset, int i)
    {
        this(context, attributeset, i, false);
    }

    public AudienceView(Context context, AttributeSet attributeset, int i, boolean flag)
    {
        super(context, attributeset, i);
        mChips = new ArrayList();
        init();
        mCanRemoveChips = flag;
    }

    private int getCloseIcon()
    {
        int i;
        if(mCanRemoveChips)
            i = R.drawable.ic_acl_x;
        else
            i = 0;
        return i;
    }

    private void setAudience(AudienceData audiencedata) {
        List arraylist = new ArrayList(mChips);
        AudienceData audiencedata1 = getAudience();
        CircleData acircledata[] = audiencedata1.getCircles();
        PersonData apersondata[] = audiencedata1.getUsers();
        SquareTargetData asquaretargetdata[] = audiencedata1.getSquareTargets();
        mChips.clear();
        if(audiencedata != null)
        {
            int i = 0;
            for(int j = arraylist.size(); i < j; i++)
            {
                AudienceData audiencedata5 = (AudienceData)arraylist.get(i);
                if(PeopleUtils.in(audiencedata, audiencedata5))
                    mChips.add(audiencedata5);
            }

            CircleData acircledata1[] = audiencedata.getCircles();
            int k = acircledata1.length;
            for(int l = 0; l < k; l++)
            {
                CircleData circledata = acircledata1[l];
                if(PeopleUtils.in(acircledata, circledata))
                    continue;
                List arraylist3 = mChips;
                AudienceData audiencedata4 = new AudienceData(circledata);
                arraylist3.add(audiencedata4);
                if(circledata == null && EsLog.isLoggable("AudienceView", 4))
                    Log.i("AudienceView", (new StringBuilder("Added a null circle: ")).append(Thread.currentThread().getStackTrace()).toString());
            }

            PersonData apersondata1[] = audiencedata.getUsers();
            int i1 = apersondata1.length;
            for(int j1 = 0; j1 < i1; j1++)
            {
                PersonData persondata = apersondata1[j1];
                if(!PeopleUtils.in(apersondata, persondata))
                {
                    ArrayList arraylist2 = mChips;
                    AudienceData audiencedata3 = new AudienceData(persondata);
                    arraylist2.add(audiencedata3);
                }
            }

            SquareTargetData asquaretargetdata1[] = audiencedata.getSquareTargets();
            int k1 = asquaretargetdata1.length;
            for(int l1 = 0; l1 < k1; l1++)
            {
                SquareTargetData squaretargetdata = asquaretargetdata1[l1];
                if(!PeopleUtils.in(asquaretargetdata, squaretargetdata))
                {
                    List arraylist1 = mChips;
                    AudienceData audiencedata2 = new AudienceData(squaretargetdata);
                    arraylist1.add(audiencedata2);
                }
            }

        }
        update();
    }

    protected void addChip(int i) {
        View view = inflate(R.layout.people_audience_view_chip);
        if(mCanRemoveChips)
            view.setOnClickListener(this);
        mChipContainer.addView(view, i);
    }

    public final void addCircle(CircleData circledata) {
        mEdited = true;
        if(!PeopleUtils.in(getAudience().getCircles(), circledata))
        {
            Context context = getContext();
            OzViews ozviews = OzViews.getViewForLogging(context);
            EsAnalytics.recordActionEvent(context, mAccount, OzActions.PLATFORM_AUDIENCE_VIEW_CIRCLE_ADDED, ozviews);
            mChips.add(new AudienceData(circledata));
            if(circledata == null && EsLog.isLoggable("AudienceView", 4))
                Log.i("AudienceView", (new StringBuilder("Added a null circle: ")).append(Thread.currentThread().getStackTrace()).toString());
            update();
        }
    }

    public final void addPerson(PersonData persondata) {
        mEdited = true;
        if(!PeopleUtils.in(getAudience().getUsers(), persondata))
        {
            Context context = getContext();
            OzViews ozviews = OzViews.getViewForLogging(context);
            EsAnalytics.recordActionEvent(context, mAccount, OzActions.PLATFORM_AUDIENCE_VIEW_PERSON_ADDED, ozviews);
            mChips.add(new AudienceData(persondata));
            update();
        }
    }

    public final AudienceData getAudience() {
        List arraylist = mChips;
        List arraylist1 = new ArrayList();
        List arraylist2 = new ArrayList();
        List arraylist3 = new ArrayList();
        for(Iterator iterator = arraylist.iterator(); iterator.hasNext();)
        {
            AudienceData audiencedata = (AudienceData)iterator.next();
            PersonData apersondata[] = audiencedata.getUsers();
            int i = apersondata.length;
            for(int j = 0; j < i; j++)
                arraylist1.add(apersondata[j]);

            CircleData acircledata[] = audiencedata.getCircles();
            int k = acircledata.length;
            for(int l = 0; l < k; l++)
                arraylist2.add(acircledata[l]);

            SquareTargetData asquaretargetdata[] = audiencedata.getSquareTargets();
            int i1 = asquaretargetdata.length;
            int j1 = 0;
            while(j1 < i1) 
            {
                arraylist3.add(asquaretargetdata[j1]);
                j1++;
            }
        }

        return new AudienceData(arraylist1, arraylist2, arraylist3);
    }

    protected int getChipCount()
    {
        return mChipContainer.getChildCount();
    }

    protected final View inflate(int i)
    {
        return LayoutInflater.from(getContext()).inflate(i, this, false);
    }

    protected void init()
    {
        addView(inflate(R.layout.audience_view));
        mChipContainer = (ViewGroup)findViewById(R.id.people_audience_view_chip_container);
    }

    public final void initLoaders(LoaderManager loadermanager)
    {
        loadermanager.initLoader(R.id.audience_circle_name_loader_id, null, this);
    }

    public final boolean isEdited()
    {
        return mEdited;
    }

    public void onClick(View view) {
    	
    	if(!mCanRemoveChips) {
    		return;
    	}
    	Context context = getContext();
        OzViews ozviews = OzViews.getViewForLogging(context);
        EsAnalytics.recordActionEvent(context, mAccount, OzActions.PLATFORM_AUDIENCE_VIEW_CLICKED, ozviews);
        int i = mChipContainer.indexOfChild(view);
		if (i != -1) {
			mEdited = true;
			mChips.remove(i);
			update();
		}
	}

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        if(i == R.id.audience_circle_name_loader_id)
            return new CircleListLoader(getContext(), mAccount, 5, CirclesQuery.PROJECTION);
        else
            throw new AssertionError();
    }

    public final void onLoadFinished(Loader loader, Object obj) {
        int i;
        Cursor cursor;
        i = 0;
        cursor = (Cursor)obj;
        if(loader.getId() != R.id.audience_circle_name_loader_id) 
        	throw new AssertionError();
        else {
        	if(cursor == null) 
        		return;
        	
        	int j;
            List arraylist;
            int k;
            if(!$assertionsDisabled && !Arrays.equals(CirclesQuery.PROJECTION, cursor.getColumnNames()))
                throw new AssertionError();
            j = mChips.size();
            arraylist = new ArrayList(j);
            k = 0;
            
            // TODO
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    protected void onRestoreInstanceState(Parcelable parcelable)
    {
        SavedState savedstate = (SavedState)parcelable;
        super.onRestoreInstanceState(savedstate.getSuperState());
        mChips.clear();
        mChips.addAll(savedstate.audience);
        if(EsLog.isLoggable("AudienceView", 4))
        {
            Iterator iterator = savedstate.audience.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                AudienceData audiencedata = (AudienceData)iterator.next();
                if(audiencedata.getCircleCount() > 0)
                {
                    CircleData acircledata[] = audiencedata.getCircles();
                    int i = acircledata.length;
                    int j = 0;
                    while(j < i) 
                    {
                        if(acircledata[j] == null)
                            Log.i("AudienceView", (new StringBuilder("Added a null circle: ")).append(Thread.currentThread().getStackTrace()).toString());
                        j++;
                    }
                }
            } while(true);
        }
        mEdited = savedstate.edited;
        update();
    }

    protected Parcelable onSaveInstanceState()
    {
        SavedState savedstate = new SavedState(super.onSaveInstanceState());
        savedstate.audience = mChips;
        savedstate.edited = mEdited;
        return savedstate;
    }

    protected void removeLastChip()
    {
        if(!mChips.isEmpty())
        {
            mEdited = true;
            int i = -1 + mChips.size();
            mChips.remove(i);
            update();
        }
    }

    public final void removePerson(PersonData persondata)
    {
        mEdited = true;
        Iterator iterator = mChips.iterator();
        AudienceData audiencedata;
        do
        {
            boolean flag = iterator.hasNext();
            audiencedata = null;
            if(!flag)
                break;
            AudienceData audiencedata1 = (AudienceData)iterator.next();
            if(audiencedata1.getUserCount() != 1 || audiencedata1.getCircleCount() != 0 || !EsPeopleData.isSamePerson(audiencedata1.getUser(0), persondata))
                continue;
            audiencedata = audiencedata1;
            break;
        } while(true);
        if(audiencedata != null)
        {
            mChips.remove(audiencedata);
            update();
        }
    }

    public final void replaceAudience(AudienceData audiencedata)
    {
        mEdited = true;
        setAudience(audiencedata);
    }

    public void setAccount(EsAccount esaccount)
    {
        mAccount = esaccount;
    }

    public void setAudienceChangedCallback(Runnable runnable)
    {
        mAudienceChangedCallback = runnable;
    }

    public void setDefaultAudience(AudienceData audiencedata)
    {
        if(audiencedata != null)
            setAudience(audiencedata);
    }

    protected void update() {
    	// TODO
    }

    protected void updateChip(int i, int j, int k, String s, Object obj, boolean flag) {
        TextView textview;
        if(i > -1 + getChipCount())
            addChip(i);
        textview = (TextView)mChipContainer.getChildAt(i);
        textview.setCompoundDrawablesWithIntrinsicBounds(j, 0, k, 0);
        textview.setText(s);
        
        int l = 0;
        if(!flag) {
        	CircleData circledata;
            if(obj instanceof CircleData)
                circledata = (CircleData)obj;
            else
                circledata = null;
            if(circledata != null)
                switch(circledata.getType())
                {
                default:
                    l = R.drawable.chip_blue;
                    break;

                case 7: // '\007'
                case 8: // '\b'
                case 9: // '\t'
                    l = R.drawable.chip_green;
                    break;
                }
            else
                l = R.drawable.chip_blue;
        } else { 
        	l = R.drawable.chip_red;
        }
        textview.setBackgroundResource(l);
        if(textview.getVisibility() != 0)
            textview.setVisibility(0);
        textview.setTag(obj);
    }
    
	
	//==============================================================================
	//
	//==============================================================================
	
	protected static interface CirclesQuery
    {

        public static final String PROJECTION[] = {
            "circle_id", "circle_name"
        };

    }

    public static class SavedState extends android.view.View.BaseSavedState {

    	public ArrayList audience;
        public boolean edited;

        SavedState(Parcel parcel) {
            super(parcel);
            audience = parcel.createTypedArrayList(AudienceData.CREATOR);
            boolean flag;
            if(parcel.readInt() != 0)
                flag = true;
            else
                flag = false;
            edited = flag;
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeTypedList(audience);
            int j;
            if(edited)
                j = 1;
            else
                j = 0;
            parcel.writeInt(j);
        }

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public final Object createFromParcel(Parcel parcel)
            {
                return new SavedState(parcel);
            }

            public final Object[] newArray(int i)
            {
                return new SavedState[i];
            }

        };
    }

}
