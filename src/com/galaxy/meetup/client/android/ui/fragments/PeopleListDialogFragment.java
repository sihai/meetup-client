/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.ui.view.AvatarView;

/**
 * 
 * @author sihai
 *
 */
public class PeopleListDialogFragment extends DialogFragment implements
		OnClickListener, OnItemClickListener {

	private EsAccount mAccount;
    private PeopleListAdapter mAdapter;
    
    public PeopleListDialogFragment()
    {
    }

    public void onClick(View view)
    {
        dismiss();
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.list_layout_acl, viewgroup);
        Bundle bundle1 = getArguments();
        ListView listview = (ListView)view.findViewById(0x102000a);
        AudienceData audiencedata = (AudienceData)bundle1.getParcelable("audience");
        mAccount = (EsAccount)bundle1.getParcelable("account");
        mAdapter = new PeopleListAdapter(getActivity(), audiencedata);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(this);
        view.findViewById(R.id.ok).setOnClickListener(this);
        view.findViewById(R.id.cancel).setVisibility(8);
        getDialog().setTitle(bundle1.getString("people_list_title"));
        return view;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        PeopleListItem peoplelistitem = (PeopleListItem)mAdapter.getItem(i);
        if(0 == peoplelistitem.mType) {
        	PersonData persondata = peoplelistitem.mPerson;
            boolean flag = TextUtils.isEmpty(persondata.getObfuscatedId());
            String s = null;
            if(!flag)
                s = persondata.getObfuscatedId();
            String s1 = null;
            if(s != null)
                s1 = (new StringBuilder("g:")).append(s).toString();
            if(s1 != null)
            {
                android.content.Intent intent = Intents.getProfileActivityIntent(getActivity(), mAccount, s1, null, 0);
                dismiss();
                startActivity(intent);
            }
        }
    }
    
    private static final class PeopleListAdapter extends BaseAdapter
    {

    	private final Context mContext;
        private final ArrayList mItems = new ArrayList();

        public PeopleListAdapter(Context context, AudienceData audiencedata)
        {
            PersonData apersondata[] = audiencedata.getUsers();
            int i = apersondata.length;
            for(int j = 0; j < i; j++)
            {
                PersonData persondata = apersondata[j];
                mItems.add(new PeopleListItem(persondata));
            }

            int k = audiencedata.getHiddenUserCount();
            if(k > 0)
            {
                Resources resources = context.getResources();
                int l = R.plurals.audience_hidden_user_count;
                Object aobj[] = new Object[1];
                aobj[0] = Integer.valueOf(k);
                String s = resources.getQuantityString(l, k, aobj);
                mItems.add(new PeopleListItem(1, s));
            }
            mContext = context;
        }
        
        public final int getCount()
        {
            return mItems.size();
        }

        public final Object getItem(int i)
        {
            return mItems.get(i);
        }

        public final long getItemId(int i)
        {
            return (long)i;
        }

        public final int getItemViewType(int i)
        {
            return ((PeopleListItem)mItems.get(i)).mType;
        }

        public final View getView(int i, View view, ViewGroup viewgroup)
        {
            PeopleListItem peoplelistitem;
            AvatarView avatarview;
            View view1;
            if(view == null)
                view1 = ((LayoutInflater)mContext.getSystemService("layout_inflater")).inflate(R.layout.acl_row_view, viewgroup, false);
            else
                view1 = view;
            peoplelistitem = (PeopleListItem)mItems.get(i);
            view1.setTag(peoplelistitem);
            avatarview = (AvatarView)view1.findViewById(R.id.avatar);
            if(0 == peoplelistitem.mType) {
            	if(peoplelistitem.mPerson != null && !TextUtils.isEmpty(peoplelistitem.mPerson.getObfuscatedId()))
                {
                    String s = peoplelistitem.mPerson.getCompressedPhotoUrl();
                    avatarview.setGaiaIdAndAvatarUrl(peoplelistitem.mPerson.getObfuscatedId(), EsAvatarData.uncompressAvatarUrl(s));
                }
                avatarview.setVisibility(0);
            } else if(1 == peoplelistitem.mType) {
            	avatarview.setVisibility(4);
            } 
            ((TextView)view1.findViewById(R.id.name)).setText(peoplelistitem.mContent);
            return view1;
        }

        public final int getViewTypeCount()
        {
            return 2;
        }

    }

    private static final class PeopleListItem
    {

        public final String mContent;
        public final PersonData mPerson;
        public final int mType;

        public PeopleListItem(int i, String s)
        {
            mType = 1;
            mContent = s;
            mPerson = null;
        }

        public PeopleListItem(PersonData persondata)
        {
            mType = 0;
            mPerson = persondata;
            mContent = persondata.getName();
        }
    }
}
