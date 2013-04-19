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
import com.galaxy.meetup.client.android.ui.view.CircleListItemView;
import com.galaxy.meetup.client.android.ui.view.PeopleListItemView;
import com.galaxy.meetup.client.android.ui.view.PeopleListItemView.OnActionButtonClickListener;
import com.galaxy.meetup.client.util.AccountsUtil;

/**
 * 
 * @author sihai
 *
 */
public class PeopleSearchListAdapter extends PeopleSearchAdapter implements
		OnActionButtonClickListener {

	public PeopleSearchListAdapter(Context context, FragmentManager fragmentmanager, LoaderManager loadermanager, EsAccount esaccount)
    {
        super(context, fragmentmanager, loadermanager, esaccount);
    }

    public PeopleSearchListAdapter(Context context, FragmentManager fragmentmanager, LoaderManager loadermanager, EsAccount esaccount, int i)
    {
        super(context, fragmentmanager, loadermanager, esaccount, 1);
    }
    
    protected final void bindView(View view, int i, Cursor cursor, int j)
    {
        if(null == cursor || cursor.isClosed()) {
        	return;
        }
     
        byte byte0 = 0;
        byte byte1 = 0;
        byte byte2 = 0;
        switch(i)
        {
        case 1: // '\001'
            CircleListItemView circlelistitemview = (CircleListItemView)view;
            int k = cursor.getInt(2);
            circlelistitemview.setHighlightedText(mQuery);
            circlelistitemview.setCircle(cursor.getString(1), k, cursor.getString(3), cursor.getInt(4), AccountsUtil.isRestrictedCircleForAccount(mAccount, k));
            break;

        case 4: // '\004'
            PeopleListItemView peoplelistitemview2 = (PeopleListItemView)view;
            peoplelistitemview2.setHighlightedText(mQuery);
            peoplelistitemview2.setCircleNameResolver(mCircleNameResolver);
            String s = cursor.getString(1);
            peoplelistitemview2.setPersonId(s);
            String s1 = cursor.getString(3);
            String s2 = cursor.getString(2);
            peoplelistitemview2.setContactIdAndAvatarUrl(s1, s2, EsAvatarData.uncompressAvatarUrl(cursor.getString(6)));
            peoplelistitemview2.setContactName(cursor.getString(4));
            String s3 = cursor.getString(12);
            String s4 = cursor.getString(7);
            boolean flag;
            String s5;
            boolean flag1;
            String s6;
            boolean flag2;
            boolean flag3;
            if(!TextUtils.isEmpty(s4))
                flag = true;
            else
                flag = false;
            s5 = cursor.getString(9);
            flag1 = mIncludePhoneNumberContacts;
            s6 = null;
            if(flag1)
                s6 = cursor.getString(10);
            peoplelistitemview2.setPackedCircleIdsEmailAddressPhoneNumberAndSnippet(s4, s5, cursor.getString(8), s6, cursor.getString(11), s3);
            if(mAddToCirclesActionEnabled && !flag && !mAccount.getPersonId().equals(s))
                flag2 = true;
            else
                flag2 = false;
            peoplelistitemview2.setAddButtonVisible(flag2);
            if(mAddToCirclesActionEnabled && mListener != null)
                peoplelistitemview2.setOnActionButtonClickListener(this);
            flag3 = true;
            if(s2 != null)
                if(j == 0)
                    flag3 = true;
                else
                if(cursor.moveToPrevious())
                {
                    if(TextUtils.equals(s2, cursor.getString(2)))
                        flag3 = false;
                    cursor.moveToNext();
                }
            peoplelistitemview2.setFirstRow(flag3);
            if(j == -1 + cursor.getCount())
                continueLoadingPublicProfiles();
            peoplelistitemview2.updateContentDescription();
            break;

        case 2: // '\002'
            PeopleListItemView peoplelistitemview1 = (PeopleListItemView)view;
            peoplelistitemview1.setWellFormedEmail(mQuery);
            peoplelistitemview1.setAddButtonVisible(mAddToCirclesActionEnabled);
            if(mAddToCirclesActionEnabled && mListener != null)
                peoplelistitemview1.setOnActionButtonClickListener(this);
            peoplelistitemview1.updateContentDescription();
            break;

        case 3: // '\003'
            PeopleListItemView peoplelistitemview = (PeopleListItemView)view;
            peoplelistitemview.setWellFormedSms(mQuery);
            peoplelistitemview.setAddButtonVisible(mAddToCirclesActionEnabled);
            if(mAddToCirclesActionEnabled && mListener != null)
                peoplelistitemview.setOnActionButtonClickListener(this);
            peoplelistitemview.updateContentDescription();
            break;

        case 5: // '\005'
            byte0 = 8;
            byte1 = 8;
            byte2 = 8;
            break;
        default:
            break;
        }
        
        int value = cursor.getInt(0);
        if(3 == value) {
        	byte1 = 0;
        }
        view.findViewById(R.id.loading).setVisibility(byte0);
        view.findViewById(R.id.not_found).setVisibility(byte1);
        view.findViewById(R.id.error).setVisibility(byte2);
    }

    protected final View newView(Context context, int partion, Cursor cursor, int position, ViewGroup parent) {
    	
    	View view = null;
    	switch(partion) {
	    	case 1:
	    		view = new CircleListItemView(context);
	    		break;
	    	case 2:
	    	case 3:
	    	case 4:
	    		view = PeopleListItemView.createInstance(context);
	    		break;
	    	case 5:
	    		view = LayoutInflater.from(context).inflate(R.layout.people_search_item_public_profiles, parent, false);
	    		break;
    		default:
    			break;
    	}
    	return view;
    }

    public final void onActionButtonClick(PeopleListItemView peoplelistitemview, int i) {
        if(i == 0)
            if(!TextUtils.isEmpty(peoplelistitemview.getWellFormedEmail()))
                showPersonNameDialog("add_email_dialog");
            else
            if(!TextUtils.isEmpty(peoplelistitemview.getWellFormedSms()))
                showPersonNameDialog("add_sms_dialog");
            else
                mListener.onAddPersonToCirclesAction(peoplelistitemview.getPersonId(), null, true);
    }

}
