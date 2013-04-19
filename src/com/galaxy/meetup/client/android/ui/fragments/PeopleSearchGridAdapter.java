/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.ui.view.PersonCardView;
import com.galaxy.meetup.client.android.ui.view.PersonCardView.OnPersonCardClickListener;

/**
 * 
 * @author sihai
 *
 */
public class PeopleSearchGridAdapter extends PeopleSearchAdapter implements
		OnPersonCardClickListener {

	private CircleSpinnerAdapter mCircleSpinnerAdapter;
    private boolean mShowMembership;
    private boolean mViewingAsPlusPage;
    
    public PeopleSearchGridAdapter(Context context, FragmentManager fragmentmanager, LoaderManager loadermanager, EsAccount esaccount)
    {
        super(context, fragmentmanager, loadermanager, esaccount);
        mViewingAsPlusPage = esaccount.isPlusPage();
    }

    protected final void bindView(View view, int i, Cursor cursor, int j) {
    	
    	switch(i) {
	    	case 0:
	    		PersonCardView personcardview2 = (PersonCardView)view;
	            personcardview2.setOnPersonCardClickListener(this);
	            personcardview2.setPosition(j + getPositionForPartition(i));
	            personcardview2.setCircleNameResolver(mCircleNameResolver);
	            personcardview2.setContactName(cursor.getString(3));
	            personcardview2.setPersonId(cursor.getString(1));
	            personcardview2.setGaiaIdAndAvatarUrl(cursor.getString(2), EsAvatarData.uncompressAvatarUrl(cursor.getString(4)));
	            if(mShowMembership)
	            {
	                String s2 = cursor.getString(5);
	                personcardview2.setPackedCircleIdsEmailAndDescription(s2, null, null, false, false);
	                boolean flag1;
	                if(cursor.getInt(6) != 0)
	                    flag1 = true;
	                else
	                    flag1 = false;
	                if(flag1)
	                {
	                    personcardview2.setForceAvatarDefault(true);
	                    personcardview2.setActionButtonVisible(true, R.string.person_card_unblock_button, 1);
	                    boolean flag2;
	                    if(cursor.getInt(7) == 2)
	                        flag2 = true;
	                    else
	                        flag2 = false;
	                    personcardview2.setPlusPage(flag2);
	                } else
	                if(TextUtils.isEmpty(s2) && !mViewingAsPlusPage)
	                    personcardview2.setActionButtonVisible(true, R.string.person_card_add_to_circles_button, 0);
	                else
	                    personcardview2.setActionButtonVisible(false, 0, 0);
	                personcardview2.setDismissActionButtonVisible(false);
	            } else
	            {
	                personcardview2.setPackedCircleIdsEmailAndDescription(null, null, null, false, false);
	                if(!mViewingAsPlusPage)
	                    personcardview2.setActionButtonVisible(true, R.string.person_card_add_to_circles_button, 0);
	                else
	                    personcardview2.setActionButtonVisible(false, 0, 0);
	                personcardview2.setDismissActionButtonVisible(true);
	            }
	    		break;
	    	case 1:
	    		
	    		break;
	    	case 2:
	    		PersonCardView personcardview = (PersonCardView)view;
	            personcardview.setOnPersonCardClickListener(this);
	            personcardview.setWellFormedEmail(mQuery);
	            personcardview.setActionButtonVisible(mAddToCirclesActionEnabled, R.string.person_card_add_to_circles_button, 0);
	            personcardview.updateContentDescription();
	    		break;
	    	case 3:
	    		
	    		break;
	    	case 4:
	    		PersonCardView personcardview1 = (PersonCardView)view;
	            personcardview1.setOnPersonCardClickListener(this);
	            personcardview1.setPosition(j + getPositionForPartition(i));
	            personcardview1.setHighlightedText(mQuery);
	            personcardview1.setCircleNameResolver(mCircleNameResolver);
	            personcardview1.setPersonId(cursor.getString(1));
	            personcardview1.setContactIdAndAvatarUrl(cursor.getString(3), cursor.getString(2), EsAvatarData.uncompressAvatarUrl(cursor.getString(6)));
	            personcardview1.setContactName(cursor.getString(4));
	            String s = cursor.getString(12);
	            if(s == null)
	            {
	                s = cursor.getString(8);
	                if(s == null)
	                    s = cursor.getString(9);
	            }
	            String s1 = cursor.getString(7);
	            int k = cursor.getInt(5);
	            personcardview1.setDescription(s, true, false);
	            if(!mViewingAsPlusPage)
	            {
	                CircleSpinnerAdapter circlespinneradapter = mCircleSpinnerAdapter;
	                boolean flag;
	                if(k == 1)
	                    flag = true;
	                else
	                    flag = false;
	                personcardview1.setOneClickCircles(s1, circlespinneradapter, flag);
	            }
	            if(j == -1 + cursor.getCount())
	                continueLoadingPublicProfiles();
	            personcardview1.updateContentDescription();
	            personcardview1.setDismissActionButtonVisible(false);
	    		break;
	    	case 5:
	    		byte byte0;
	            byte byte1;
	            byte byte2;
	            byte0 = 8;
	            byte1 = 8;
	            byte2 = 8;
	            int value = cursor.getInt(0);
	            if(1 == value) {
	            	
	            } else if(2 == value) {
	            	byte1 = 0;
	            } else if(3 == value) {
	            	byte2 = 0;
	            } else {
	            	
	            }
	            view.findViewById(R.id.loading).setVisibility(byte0);
	            view.findViewById(R.id.not_found).setVisibility(byte1);
	            view.findViewById(R.id.error).setVisibility(byte2);
	    		break;
    		default:
    			break;
    	}
    }

    public final void changeCircleMembers$2c8bde3e(Cursor cursor, boolean flag)
    {
        mShowMembership = flag;
        changeCursor(0, cursor);
    }

    public final boolean isCursorClosingEnabled()
    {
        return false;
    }

    protected final View newView(Context context, int partion, Cursor cursor, int position, ViewGroup parent) {
    	View view = null;
        if(5 == partion) {
        	view = LayoutInflater.from(context).inflate(R.layout.people_search_status_card, parent, false);
        } else {
        	view = new PersonCardView(context);
        }
        
        return view;
    }

    public final void onActionButtonClick(PersonCardView personcardview, int i) {
        if(mListener == null) {
        	return; 
        } 
        if(0 == i) {
        	if(!TextUtils.isEmpty(personcardview.getWellFormedEmail()))
                showPersonNameDialog("add_email_dialog");
            else
                mListener.onChangeCirclesAction(personcardview.getPersonId(), personcardview.getContactName());
        } else if(1 == i) {
        	mListener.onUnblockPersonAction(personcardview.getPersonId(), false);
        } 
    }

    public final void onChangeCircles(PersonCardView personcardview)
    {
        mListener.onChangeCirclesAction(personcardview.getPersonId(), personcardview.getContactName());
    }

    public final void onDismissButtonClick(PersonCardView personcardview)
    {
        if(mListener != null)
            mListener.onDismissSuggestionAction(personcardview.getPersonId(), personcardview.getSuggestionId());
    }

    public final void onItemClick(PersonCardView personcardview)
    {
        onItemClick(personcardview.getPosition());
    }

    public final void setCircleSpinnerAdapter(CircleSpinnerAdapter circlespinneradapter)
    {
        mCircleSpinnerAdapter = circlespinneradapter;
    }

}
