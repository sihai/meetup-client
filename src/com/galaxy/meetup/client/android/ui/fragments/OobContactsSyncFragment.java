/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.regex.Pattern;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.util.AndroidUtils;
import com.galaxy.meetup.client.util.HelpUrl;

/**
 * 
 * @author sihai
 *
 */
public class OobContactsSyncFragment extends Fragment {

	private ImageView mArrowContactsStatsSync;
    private ImageView mArrowContactsSync;
    private ImageView mCiclesImage;
    private ImageView mContactsImage;
    private CheckBox mContactsStatsSyncChoice;
    private CheckBox mContactsSyncChoice;
    private boolean statsSyncOnly;
    
	public OobContactsSyncFragment()
    {
    }

    public final boolean commit()
    {
        FragmentActivity fragmentactivity = getActivity();
        EsAccount esaccount = (EsAccount)fragmentactivity.getIntent().getParcelableExtra("account");
        if(!statsSyncOnly)
            EsAccountsData.saveContactsSyncPreference(fragmentactivity, esaccount, mContactsSyncChoice.isChecked());
        boolean flag = mContactsStatsSyncChoice.isChecked();
        EsAccountsData.saveContactsStatsSyncPreference(fragmentactivity, esaccount, flag);
        EsAnalytics.recordImproveSuggestionsPreferenceChange(fragmentactivity, esaccount, flag, OzViews.OOB_IMPROVE_CONTACTS_VIEW);
        if(flag)
            EsService.disableWipeoutStats(fragmentactivity, esaccount);
        else
            EsService.enableAndPerformWipeoutStats(fragmentactivity, esaccount);
        return true;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        boolean flag;
        View view;
        android.view.View.OnClickListener onclicklistener1;
        Resources resources;
        TextView textview;
        int i;
        TextView textview1;
        String s;
        SpannableString spannablestring;
        String s1;
        Uri uri;
        if(android.os.Build.VERSION.SDK_INT < 14)
            flag = true;
        else
            flag = false;
        statsSyncOnly = flag;
        view = layoutinflater.inflate(R.layout.oob_contacts_sync_fragment, viewgroup, false);
        mCiclesImage = (ImageView)view.findViewById(R.id.circles);
        mCiclesImage.setImageResource(R.drawable.home_screen_people_icon_default);
        mContactsImage = (ImageView)view.findViewById(R.id.contacts);
        mContactsImage.setImageResource(R.drawable.oob_contact_sync_icon_contacts);
        mArrowContactsStatsSync = (ImageView)view.findViewById(R.id.arrow_contacts_stats_sync);
        mContactsStatsSyncChoice = (CheckBox)view.findViewById(R.id.contacts_stats_sync_checkbox);
        mContactsStatsSyncChoice.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

            public final void onCheckedChanged(CompoundButton compoundbutton, boolean flag1)
            {
                ImageView imageview = mArrowContactsStatsSync;
                int j;
                if(flag1)
                    j = 0;
                else
                    j = 4;
                imageview.setVisibility(j);
            }

        });
        mContactsStatsSyncChoice.setChecked(true);
        if(statsSyncOnly)
        {
            mArrowContactsStatsSync.setImageResource(R.drawable.oob_contact_sync_icon_arrow);
        } else
        {
            mArrowContactsSync = (ImageView)view.findViewById(R.id.arrow_contacts_sync);
            mArrowContactsSync.setImageResource(R.drawable.oob_contact_sync_icon_arrow);
            mArrowContactsStatsSync.setImageResource(R.drawable.oob_contact_stats_sync_icon_arrow);
            mContactsSyncChoice = (CheckBox)view.findViewById(R.id.contacts_sync_checkbox);
            mContactsSyncChoice.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

                public final void onCheckedChanged(CompoundButton compoundbutton, boolean flag1)
                {
                    ImageView imageview = mArrowContactsSync;
                    int j;
                    if(flag1)
                        j = 0;
                    else
                        j = 4;
                    imageview.setVisibility(j);
                }

            });
            mContactsSyncChoice.setChecked(true);
            android.view.View.OnClickListener onclicklistener = new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    mContactsSyncChoice.toggle();
                }

            };
            view.findViewById(R.id.contacts_sync_checkbox_title).setOnClickListener(onclicklistener);
        }
        onclicklistener1 = new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                mContactsStatsSyncChoice.toggle();
            }

        };
        resources = getResources();
        textview = (TextView)view.findViewById(R.id.contacts_stats_sync_checkbox_title);
        if(AndroidUtils.hasTelephony(getActivity()))
            i = R.string.contacts_stats_sync_preference_enabled_phone_summary;
        else
            i = R.string.contacts_stats_sync_preference_enabled_tablet_summary;
        textview.setText(resources.getString(i));
        textview.setOnClickListener(onclicklistener1);
        textview1 = (TextView)view.findViewById(R.id.contacts_stats_sync_checkbox_link);
        s = resources.getString(R.string.contacts_stats_sync_preference_enabled_learn_more);
        spannablestring = new SpannableString(s);
        s1 = getResources().getString(R.string.url_param_help_stats_sync);
        uri = HelpUrl.getHelpUrl(getActivity(), s1);
        Linkify.addLinks(spannablestring, Pattern.compile(s), uri.toString());
        textview1.setText(spannablestring);
        textview1.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

}
