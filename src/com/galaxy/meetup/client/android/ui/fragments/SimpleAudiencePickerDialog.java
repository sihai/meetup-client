/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.io.Serializable;
import java.util.ArrayList;

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
import com.galaxy.meetup.client.android.ui.view.CircleListItemView;

/**
 * 
 * @author sihai
 *
 */
public class SimpleAudiencePickerDialog extends AlertFragmentDialog implements
		OnClickListener, OnItemClickListener {

	private ArrayList mOptions;
    private ContextThemeWrapper mThemeContext;
    
    public SimpleAudiencePickerDialog()
    {
        mOptions = new ArrayList();
    }

    public static SimpleAudiencePickerDialog newInstance(String s, String s1, boolean flag)
    {
        SimpleAudiencePickerDialog simpleaudiencepickerdialog = new SimpleAudiencePickerDialog();
        Bundle bundle = new Bundle();
        bundle.putString("domain_name", s);
        bundle.putString("domain_id", s1);
        bundle.putBoolean("has_public_circle", flag);
        simpleaudiencepickerdialog.setArguments(bundle);
        return simpleaudiencepickerdialog;
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
    	}
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        Bundle bundle1 = getArguments();
        String s = bundle1.getString("domain_name");
        String s1 = bundle1.getString("domain_id");
        boolean flag = bundle1.getBoolean("has_public_circle");
        View view = LayoutInflater.from(mThemeContext).inflate(R.layout.simple_audience_picker_dialog, null);
        mOptions.add(new CircleInfo("1f", 7, getString(R.string.acl_extended_network)));
        if(s != null)
            mOptions.add(new CircleInfo(s1, 8, s));
        if(flag)
            mOptions.add(new CircleInfo("0", 9, getString(R.string.acl_public)));
        mOptions.add(new CircleInfo("1c", 5, getString(R.string.acl_your_circles)));
        mOptions.add(new CircleInfo("v.private", 101, getString(R.string.acl_private)));
        mOptions.add(new CircleInfo("v.custom", -3, getString(R.string.post_create_custom_acl)));
        ListView listview = (ListView)view.findViewById(R.id.list);
        listview.setOnItemClickListener(this);
        listview.setAdapter(new ArrayAdapter(mThemeContext, 0, mOptions) {

            public final int getItemViewType(int i)
            {
                return 0;
            }

            public final View getView(int i, View view1, ViewGroup viewgroup)
            {
                CircleListItemView circlelistitemview = new CircleListItemView(mThemeContext);
                circlelistitemview.setAvatarStripVisible(false);
                circlelistitemview.setCheckBoxVisible(false);
                circlelistitemview.setMemberCountVisible(false);
                circlelistitemview.updateContentDescription();
                CircleInfo circleinfo = (CircleInfo)getItem(i);
                circlelistitemview.setTag(circleinfo);
                circlelistitemview.setCircle(circleinfo.getId(), circleinfo.getType(), circleinfo.getName(), 0, false);
                return circlelistitemview;
            }

            public final int getViewTypeCount()
            {
                return 1;
            }
        });
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mThemeContext);
        builder.setTitle(R.string.profile_edit_item_visibility);
        builder.setNegativeButton(0x1040000, this);
        builder.setCancelable(true);
        builder.setView(view);
        return builder.create();
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        CircleInfo circleinfo = (CircleInfo)view.getTag();
        ((ProfileEditFragment)getTargetFragment()).onSetSimpleAudience(circleinfo.getId(), circleinfo.getType(), circleinfo.getName());
        getDialog().dismiss();
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
	public static final class CircleInfo implements Serializable {

		private String mId;
		private String mName;
		private int mType;

		public CircleInfo(String s, int i, String s1) {
			mId = s;
			mName = s1;
			mType = i;
		}
		
		public final String getId() {
			return mId;
		}

		public final String getName() {
			return mName;
		}

		public final int getType() {
			return mType;
		}
	}
}
