/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.CheckableListItemView;
import com.galaxy.meetup.client.android.ui.view.CircleListItemView;

/**
 * 
 * @author sihai
 *
 */
public class CircleSubscriptionsDialog extends AlertFragmentDialog implements
		OnClickListener, OnItemClickListener {

	private static CheckableListItemView.OnItemCheckedChangeListener mOnCheckedListener = new CheckableListItemView.OnItemCheckedChangeListener() {

        public final void onItemCheckedChanged(CheckableListItemView checkablelistitemview, boolean flag)
        {
            CircleInfo circleinfo = (CircleInfo)checkablelistitemview.getTag();
            byte byte0;
            if(flag)
                byte0 = 4;
            else
                byte0 = 2;
            circleinfo.setVolume(byte0);
        }

    };
    
    private ArrayList mCircleInfo;
    private ContextThemeWrapper mThemeContext;
    
    public CircleSubscriptionsDialog()
    {
    }

    private static ArrayList getCircleInfo(Bundle bundle)
    {
        return (ArrayList)bundle.getSerializable("circle_info");
    }

    public static CircleSubscriptionsDialog newInstance$51fb5134(ArrayList arraylist)
    {
        CircleSubscriptionsDialog circlesubscriptionsdialog = new CircleSubscriptionsDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("circle_info", arraylist);
        circlesubscriptionsdialog.setArguments(bundle);
        return circlesubscriptionsdialog;
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mThemeContext = new ContextThemeWrapper(activity, R.style.CircleSubscriptionList);
    }

    public void onCancel(DialogInterface dialoginterface)
    {
        dialoginterface.dismiss();
    }

    public void onClick(DialogInterface dialoginterface, int i) {
    	
    	if(-2 == i) {
    		dialoginterface.dismiss();
    	} else if(-1 == i) {
    		HashMap hashmap = new HashMap();
            int j = mCircleInfo.size();
            for(int k = 0; k < j; k++)
            {
                CircleInfo circleinfo = (CircleInfo)mCircleInfo.get(k);
                if(circleinfo.isVolumeChanged())
                    hashmap.put(circleinfo.getId(), Integer.valueOf(circleinfo.getVolume()));
            }

            if(hashmap.size() != 0)
            {
                ((HostedStreamFragment)getTargetFragment()).doCircleSubscriptions(hashmap);
                return;
            }
            dialoginterface.dismiss();
    	}
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
            mCircleInfo = getCircleInfo(bundle);
    }

    public final Dialog onCreateDialog(Bundle bundle) {
        View view;
        final boolean flag;
        ListView listview;
        android.app.AlertDialog.Builder builder;
        if(bundle != null)
            mCircleInfo = getCircleInfo(bundle);
        else
            mCircleInfo = getCircleInfo(getArguments());
        final LayoutInflater layoutinflater = LayoutInflater.from(mThemeContext);
        view = layoutinflater.inflate(R.layout.circle_subscriptions_dialog, null);
        if(mThemeContext.getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        if(flag)
            view.findViewById(R.id.message).setVisibility(8);
        listview = (ListView)view.findViewById(R.id.list);
        listview.setOnItemClickListener(this);
        listview.setAdapter(new ArrayAdapter(mThemeContext, 0, mCircleInfo) {

            public final int getCount()
            {
                int i = super.getCount();
                if(flag)
                    i++;
                return i;
            }

            public final int getItemViewType(int i)
            {
                int j = 1;
                if(i == 0 && flag)
                    j = 0;
                return j;
            }

            public final View getView(int i, View view1, ViewGroup viewgroup)
            {
                boolean flag1 = true;
                if(flag) {
                	; 
                } else { 
                	if(i != 0) {
                		i--; 
                	} else { 
                		return layoutinflater.inflate(R.layout.circle_subscriptions_dialog_message, null);
                	}
                }
                
                View view = new CircleListItemView(mThemeContext);
                ((CircleListItemView) (view)).setOnItemCheckedChangeListener(CircleSubscriptionsDialog.mOnCheckedListener);
                ((CircleListItemView) (view)).setAvatarStripVisible(false);
                ((CircleListItemView) (view)).setCheckBoxVisible(flag1);
                ((CircleListItemView) (view)).updateContentDescription();
                CircleInfo circleinfo = (CircleInfo)mCircleInfo.get(i);
                ((CircleListItemView) (view)).setTag(circleinfo);
                ((CircleListItemView) (view)).setCircle(circleinfo.getId(), 1, circleinfo.getName(), circleinfo.getMemberCount(), false);
                if(circleinfo.getVolume() != 4)
                    flag1 = false;
                ((CircleListItemView) (view)).setChecked(flag1);
                return view;
            }

            public final int getViewTypeCount()
            {
                byte byte0;
                if(flag)
                    byte0 = 2;
                else
                    byte0 = 1;
                return byte0;
            }

        });
        builder = new android.app.AlertDialog.Builder(mThemeContext);
        builder.setTitle(getString(R.string.circle_subscriptions_dialog_title));
        builder.setPositiveButton(0x104000a, this);
        builder.setNegativeButton(0x1040000, this);
        builder.setCancelable(true);
        builder.setView(view);
        return builder.create();
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        ((CircleListItemView)view).toggle();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("circle_info", mCircleInfo);
    }
    
    
    public static final class CircleInfo implements Serializable {
    	
    	private String mId;
        private int mMemberCount;
        private String mName;
        private int mOriginalVolume;
        private int mVolume;

        public CircleInfo(String s, String s1, int i, int j)
        {
            mId = s;
            mName = s1;
            mMemberCount = i;
            mOriginalVolume = j;
            mVolume = j;
        }

	    public final String getId()
	    {
	        return mId;
	    }
	
	    public final int getMemberCount()
	    {
	        return mMemberCount;
	    }
	
	    public final String getName()
	    {
	        return mName;
	    }
	
	    public final int getVolume()
	    {
	        return mVolume;
	    }
	
	    public final boolean isVolumeChanged()
	    {
	        boolean flag;
	        if(mVolume != mOriginalVolume)
	            flag = true;
	        else
	            flag = false;
	        return flag;
	    }
	
	    public final void setVolume(int i)
	    {
	        mVolume = i;
	    }
	
	    public final String toString()
	    {
	        return (new StringBuilder("{")).append(mId).append(", \"").append(mName).append("\", +").append(mMemberCount).append(", @").append(mVolume).append("}").toString();
	    }
	}
}
