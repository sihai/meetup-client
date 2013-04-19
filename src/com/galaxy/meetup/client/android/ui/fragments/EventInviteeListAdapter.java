/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.PeopleListItemView;
import com.galaxy.meetup.client.android.ui.view.PeopleListItemView.OnActionButtonClickListener;

/**
 * 
 * @author sihai
 *
 */			 
public class EventInviteeListAdapter extends CursorAdapter implements
		OnActionButtonClickListener {

	private OnActionListener mListener;
    private String mOwnerId;
    private String mViewerGaiaId;
    
    public EventInviteeListAdapter(Context context)
    {
        super(context, null, 0);
    }

    public final void bindView(View view, Context context, Cursor cursor)
    {
        int value = cursor.getInt(0);
        if(0 == value) {
        	String s2 = cursor.getString(9);
            boolean flag1;
            int j1;
            int k1;
            Context context1;
            Object aobj2[];
            String s3;
            if(cursor.getInt(10) == 1)
                flag1 = true;
            else
                flag1 = false;
            j1 = cursor.getInt(11);
            if("ATTENDING".equals(s2))
            {
                if(flag1)
                    k1 = R.string.event_invitee_list_section_attended;
                else
                    k1 = R.string.event_invitee_list_section_attending;
            } else
            if("MAYBE".equals(s2))
                k1 = R.string.event_invitee_list_section_maybe;
            else
            if("NOT_ATTENDING".equals(s2))
            {
                if(flag1)
                    k1 = R.string.event_invitee_list_section_didnt_go;
                else
                    k1 = R.string.event_invitee_list_section_not_attending;
            } else
            if("REMOVED".equals(s2))
                k1 = R.string.event_invitee_list_section_removed;
            else
            if(flag1)
                k1 = R.string.event_invitee_list_section_did_not_respond;
            else
                k1 = R.string.event_invitee_list_section_not_responded;
            context1 = mContext;
            aobj2 = new Object[1];
            aobj2[0] = Integer.valueOf(j1);
            s3 = context1.getString(k1, aobj2);
            ((TextView)view.findViewById(0x1020014)).setText(s3);
        } else if(1 == value) {
        	PeopleListItemView peoplelistitemview = (PeopleListItemView)view;
            peoplelistitemview.setOnActionButtonClickListener(this);
            String s1 = cursor.getString(3);
            peoplelistitemview.setPersonId(cursor.getString(2));
            peoplelistitemview.setGaiaId(s1);
            peoplelistitemview.setContactName(cursor.getString(4));
            peoplelistitemview.setWellFormedEmail(cursor.getString(5));
            int k = cursor.getInt(7);
            if(k > 0)
            {
                Resources resources1 = mContext.getResources();
                int i1 = R.plurals.event_invitee_other_count;
                Object aobj1[] = new Object[1];
                aobj1[0] = Integer.valueOf(k);
                peoplelistitemview.setCustomText(resources1.getQuantityString(i1, k, aobj1));
            }
            peoplelistitemview.setActionButtonLabel(R.string.accounts_title);
            if(!mOwnerId.equals(mViewerGaiaId) || mViewerGaiaId.equals(s1))
            {
                peoplelistitemview.setActionButtonVisible(false);
            } else
            {
                boolean flag;
                int l;
                if(cursor.getInt(8) != 0)
                    flag = true;
                else
                    flag = false;
                if(flag)
                    l = R.string.event_reinvite_invitee;
                else
                    l = R.string.event_remove_invitee;
                peoplelistitemview.setActionButtonLabel(l);
                peoplelistitemview.setActionButtonVisible(true);
            }
            peoplelistitemview.updateContentDescription();
        } else if(2 == value) {
        	int i = cursor.getInt(11);
            Resources resources = mContext.getResources();
            int j = R.plurals.event_invitee_other_count;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(i);
            String s = resources.getQuantityString(j, i, aobj);
            ((TextView)view.findViewById(0x1020014)).setText(s);
        }
    }
    
    public final int getItemViewType(int i)
    {
        byte byte0 = 0;
        int j = ((Cursor)getItem(i)).getInt(0);
        if(0 == j) {
        	
        } else if(1 == j) {
        	byte0 = 1;
        } else {
        	byte0 = 2;
        }
        return byte0;
    }

    public final int getViewTypeCount()
    {
        return 3;
    }

    public final boolean isEnabled(int i)
    {
        boolean flag = true;
        if(getItemViewType(i) != 1)
            flag = false;
        return flag;
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
    	View view = null;
        int value = cursor.getInt(0);
        if(0 == value) {
        	view = LayoutInflater.from(context).inflate(R.layout.section_header, viewgroup, false);
        } else if(1 == value) {
        	view = PeopleListItemView.createInstance(context);
        } else if(2 == value) {
        	view = LayoutInflater.from(context).inflate(R.layout.event_invitee_list_section_footer, viewgroup, false);
        }
        return view;
    }

    public final void onActionButtonClick(PeopleListItemView peoplelistitemview, int i)
    {
        if(i != 3 || mListener == null) 
        	return; 
        
        boolean flag;
        String s;
        String s1;
        Cursor cursor;
        s = peoplelistitemview.getGaiaId();
        s1 = peoplelistitemview.getWellFormedEmail();
        cursor = getCursor();
        if(!cursor.moveToFirst()) { 
        	flag = false; 
        } else { 
            String s2;
            String s3;
            if(!cursor.isNull(3))
                s2 = cursor.getString(3);
            else
                s2 = null;
            if(!cursor.isNull(5))
                s3 = cursor.getString(5);
            else
                s3 = null;
            if((s2 == null || !TextUtils.equals(s2, s)) && (s3 == null || !TextUtils.equals(s3, s1)))
                return;
            if(cursor.getInt(8) != 0)
                flag = true;
            else
                flag = false;
        }
        
        if(flag)
            mListener.onReInviteInvitee(s, s1);
        else
            mListener.onRemoveInvitee(s, s1);
    }

    public final void setEventOwnerId(String s)
    {
        mOwnerId = s;
    }

    public final void setOnActionListener(OnActionListener onactionlistener)
    {
        mListener = onactionlistener;
    }

    public final void setViewerGaiaId(String s)
    {
        mViewerGaiaId = s;
    }
    
	public static interface OnActionListener {

		public abstract void onReInviteInvitee(String s, String s1);

		public abstract void onRemoveInvitee(String s, String s1);
	}
}
