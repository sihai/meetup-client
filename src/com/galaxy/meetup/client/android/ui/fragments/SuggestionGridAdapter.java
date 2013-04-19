/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.service.CircleMembershipManager;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.activity.OobDeviceActivity;
import com.galaxy.meetup.client.android.ui.view.PersonCardView;
import com.galaxy.meetup.server.client.domain.DataCircleMemberProperties;
import com.galaxy.meetup.server.client.domain.DataSugggestionExplanation;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class SuggestionGridAdapter extends EsCursorAdapter implements PersonCardView.OnPersonCardClickListener {

	public static final String PROJECTION[] = {
        "_id", "person_id", "gaia_id", "name", "avatar", "packed_circle_ids", "profile_type", "category", "category_label", "explanation", 
        "properties", "suggestion_id"
    };
    private final EsAccount mAccount;
    private List mCategories;
    private final DataSetObserver mCircleContentObserver = new DataSetObserver() {

        public final void onChanged()
        {
            if(getCursor() != null && !getCursor().isClosed())
                notifyDataSetChanged();
        }

    };
    private final CircleNameResolver mCircleNameResolver;
    private CircleSpinnerAdapter mCircleSpinnerAdapter;
    private SuggestionGridAdapterListener mListener;
    private final String mSuggestionUi;
    private String mTooltipPersonId;
    private boolean mValid;
    
    public SuggestionGridAdapter(Context context, LoaderManager loadermanager, EsAccount esaccount, int i)
    {
        super(context, null);
        mCategories = new ArrayList();
        mAccount = esaccount;
        mCircleNameResolver = new CircleNameResolver(context, loadermanager, esaccount, i);
        mCircleNameResolver.registerObserver(mCircleContentObserver);
        mCircleSpinnerAdapter = new CircleSpinnerAdapter(mContext);
        mCircleSpinnerAdapter.setNotifyOnChange(false);
        String s;
        if(context instanceof OobDeviceActivity)
            s = "SIGNUP";
        else
            s = "ANDROID_PEOPLE_SUGGESTIONS_PAGE";
        mSuggestionUi = s;
    }

    private void recordSuggestionAction(PersonCardView personcardview, String s)
    {
        List arraylist = new ArrayList();
        arraylist.add(personcardview.getPersonId());
        List arraylist1 = new ArrayList();
        arraylist1.add(personcardview.getSuggestionId());
        EsService.recordSuggestionAction(mContext, mAccount, mSuggestionUi, (ArrayList)arraylist, (ArrayList)arraylist1, s);
    }

    public final void bindView(View view, Context context, Cursor cursor)
    {
        String s = cursor.getString(1);
        String s1 = cursor.getString(2);
        String s2 = cursor.getString(4);
        String s3 = cursor.getString(11);
        PersonCardView personcardview = (PersonCardView)view;
        personcardview.setCircleNameResolver(mCircleNameResolver);
        personcardview.setOnPersonCardClickListener(this);
        personcardview.setContactName(cursor.getString(3));
        personcardview.setPersonId(s);
        personcardview.setGaiaIdAndAvatarUrl(s1, EsAvatarData.uncompressAvatarUrl(s2));
        personcardview.setSuggestionId(s3);
        Resources resources = context.getResources();
        byte abyte0[] = cursor.getBlob(9);
        String s4;
        String s6;
        String s7;
        boolean flag;
        boolean flag1;
        int i;
        if(abyte0 != null)
        {
            DataSugggestionExplanation datasugggestionexplanation = (DataSugggestionExplanation)JsonUtil.fromByteArray(abyte0, DataSugggestionExplanation.class);
            Integer integer = datasugggestionexplanation.numberOfCommonFriends;
            s4 = null;
            if(integer != null)
            {
                int k = datasugggestionexplanation.numberOfCommonFriends.intValue();
                s4 = null;
                if(k > 0)
                {
                    int l = R.plurals.common_friend_count;
                    int i1 = datasugggestionexplanation.numberOfCommonFriends.intValue();
                    Object aobj1[] = new Object[1];
                    aobj1[0] = datasugggestionexplanation.numberOfCommonFriends;
                    s4 = resources.getQuantityString(l, i1, aobj1);
                }
            }
        } else
        {
            byte abyte1[] = cursor.getBlob(10);
            s4 = null;
            if(abyte1 != null)
            {
                DataCircleMemberProperties datacirclememberproperties = (DataCircleMemberProperties)JsonUtil.fromByteArray(abyte1, DataCircleMemberProperties.class);
                if(datacirclememberproperties.tagLine != null)
                    s4 = datacirclememberproperties.tagLine;
                else
                if(datacirclememberproperties.company != null)
                {
                    if(datacirclememberproperties.occupation != null)
                    {
                        int j = R.string.people_search_job;
                        Object aobj[] = new Object[2];
                        aobj[0] = datacirclememberproperties.occupation;
                        aobj[1] = datacirclememberproperties.company;
                        s4 = resources.getString(j, aobj);
                    } else
                    {
                        s4 = datacirclememberproperties.company;
                    }
                } else
                if(datacirclememberproperties.occupation != null)
                    s4 = datacirclememberproperties.occupation;
                else
                if(datacirclememberproperties.school != null)
                {
                    s4 = datacirclememberproperties.school;
                } else
                {
                    String s5 = datacirclememberproperties.location;
                    s4 = null;
                    if(s5 != null)
                        s4 = datacirclememberproperties.location;
                }
            }
        }
        personcardview.setPackedCircleIdsEmailAndDescription(null, null, s4, false, false);
        s6 = cursor.getString(5);
        s7 = cursor.getString(7);
        if(CircleMembershipManager.isCircleMembershipRequestPending(s))
        {
            personcardview.setShowCircleChangePending(true);
        } else
        {
            personcardview.setShowCircleChangePending(false);
            personcardview.setOneClickCircles(s6, mCircleSpinnerAdapter, "#".equals(s7));
        }
        flag = TextUtils.equals(mTooltipPersonId, s);
        flag1 = false;
        if(flag)
        {
            boolean flag2 = TextUtils.isEmpty(s6);
            flag1 = false;
            if(!flag2)
            {
                flag1 = true;
                mTooltipPersonId = null;
            }
        }
        i = R.string.added_to_circles_tooltip;
        personcardview.setShowTooltip(flag1, i);
        personcardview.setDismissActionButtonVisible(TextUtils.isEmpty(s6));
    }

    public final List getCategories() {
    	List arraylist;
        if(mValid)
        {
            arraylist = mCategories;
        } else
        {
            Cursor cursor = getCursor();
            if(cursor == null)
            {
                mCategories.clear();
                mValid = true;
                arraylist = mCategories;
            } else
            {
                boolean flag = cursor.moveToFirst();
                SuggestionCategoryAdapter suggestioncategoryadapter = null;
                int i = 0;
                if(flag)
                    do
                    {
                        String s = cursor.getString(7);
                        if(suggestioncategoryadapter == null || !TextUtils.equals(s, suggestioncategoryadapter.mCategory))
                        {
                            if(suggestioncategoryadapter != null)
                                suggestioncategoryadapter.mCount = cursor.getPosition() - suggestioncategoryadapter.mOffset;
                            if(i < mCategories.size())
                            {
                                suggestioncategoryadapter = (SuggestionCategoryAdapter)mCategories.get(i);
                            } else
                            {
                                suggestioncategoryadapter = new SuggestionCategoryAdapter();
                                mCategories.add(suggestioncategoryadapter);
                            }
                            suggestioncategoryadapter.mCategory = s;
                            suggestioncategoryadapter.mCategoryLabel = cursor.getString(8);
                            suggestioncategoryadapter.mOffset = cursor.getPosition();
                            i++;
                        }
                    } while(cursor.moveToNext());
                if(suggestioncategoryadapter != null)
                    suggestioncategoryadapter.mCount = cursor.getCount() - suggestioncategoryadapter.mOffset;
                for(; mCategories.size() > i; mCategories.remove(-1 + mCategories.size()));
                mValid = true;
                arraylist = mCategories;
            }
        }
        return arraylist;
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        PersonCardView personcardview = new PersonCardView(context);
        personcardview.setAutoWidthForHorizontalScrolling();
        return personcardview;
    }

    public final void onActionButtonClick(PersonCardView personcardview, int i)
    {
    }

    public final void onChangeCircles(PersonCardView personcardview)
    {
        if(personcardview.isOneClickAdd())
        {
            mListener.onAddPersonToCirclesAction(personcardview.getPersonId(), personcardview.getContactName(), personcardview.isForSharing());
            if(!EsAccountsData.hasOneClickAddTooltipBeenShown(mContext, mAccount))
            {
                EsAccountsData.setOneClickAddTooltipShown(mContext, mAccount);
                mTooltipPersonId = personcardview.getPersonId();
            }
            recordSuggestionAction(personcardview, "ACCEPT");
        } else
        {
            mListener.onChangeCirclesAction(personcardview.getPersonId(), personcardview.getContactName());
        }
    }

    public final void onDismissButtonClick(PersonCardView personcardview)
    {
        mListener.onDismissSuggestionAction(personcardview.getPersonId(), personcardview.getSuggestionId());
    }

    public final void onItemClick(PersonCardView personcardview)
    {
        mListener.onPersonSelected(personcardview.getPersonId());
        recordSuggestionAction(personcardview, "CLICK");
    }

    public final void onStart()
    {
        mCircleNameResolver.initLoader();
    }

    public final void setCircleSpinnerAdapter(CircleSpinnerAdapter circlespinneradapter)
    {
        mCircleSpinnerAdapter = circlespinneradapter;
    }

    public final void setListener(SuggestionGridAdapterListener suggestiongridadapterlistener)
    {
        mListener = suggestiongridadapterlistener;
    }

    public final Cursor swapCursor(Cursor cursor)
    {
        mValid = false;
        return super.swapCursor(cursor);
    }
    
    public final class SuggestionCategoryAdapter extends BaseAdapter {
    	
    	private String mCategory;
        private String mCategoryLabel;
        private int mCount;
        private int mOffset;

        public final String getCategory()
        {
            return mCategory;
        }

        public final String getCategoryLabel()
        {
            return mCategoryLabel;
        }

        public final int getCount()
        {
            return mCount;
        }

        public final Object getItem(int i)
        {
            return SuggestionGridAdapter.this.getItem(i + mOffset);
        }

        public final long getItemId(int i)
        {
            return SuggestionGridAdapter.this.getItemId(i + mOffset);
        }

        public final View getView(int i, View view, ViewGroup viewgroup)
        {
            View view1 = SuggestionGridAdapter.this.getView(i + mOffset, view, viewgroup);
            if(view1 instanceof PersonCardView)
            {
                PersonCardView personcardview = (PersonCardView)view1;
                boolean flag;
                if(i == 0)
                    flag = true;
                else
                    flag = false;
                personcardview.setWideMargin(flag);
            }
            return view1;
        }

        public final String toString()
        {
            return (new StringBuilder()).append(mCategoryLabel).append(": ").append(mCount).toString();
        }

    }

    public static interface SuggestionGridAdapterListener
    {

        public abstract void onAddPersonToCirclesAction(String s, String s1, boolean flag);

        public abstract void onChangeCirclesAction(String s, String s1);

        public abstract void onDismissSuggestionAction(String s, String s1);

        public abstract void onPersonSelected(String s);
    }
}
