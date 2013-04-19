/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
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
public class NewFeaturesFragmentDialog extends DialogFragment {

	private static final boolean CONTACTS_SYNC_ENABLED;
    private CheckBox mContactsStatsSyncChoice;
    private CheckBox mContactsSyncChoice;
    private View mContactsSyncView;

    static 
    {
        boolean flag;
        if(android.os.Build.VERSION.SDK_INT >= 14)
            flag = true;
        else
            flag = false;
        CONTACTS_SYNC_ENABLED = flag;
    }
    
    public NewFeaturesFragmentDialog()
    {
    }

    public NewFeaturesFragmentDialog(EsAccount esaccount)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", esaccount);
        setArguments(bundle);
    }

    public final Dialog onCreateDialog(Bundle bundle)
    {
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        final EsAccount account = (EsAccount)getArguments().getParcelable("account");
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragmentactivity);
        View view = LayoutInflater.from(fragmentactivity).inflate(R.layout.whats_new_dialog, null);
        mContactsSyncView = view.findViewById(R.id.contacts_sync_view);
        View view1 = mContactsSyncView;
        int i;
        TextView textview;
        int j;
        TextView textview1;
        String s;
        SpannableString spannablestring;
        String s1;
        android.content.DialogInterface.OnClickListener onclicklistener;
        if(CONTACTS_SYNC_ENABLED)
            i = 0;
        else
            i = 8;
        view1.setVisibility(i);
        mContactsSyncChoice = (CheckBox)view.findViewById(R.id.contacts_sync_checkbox);
        mContactsSyncChoice.setChecked(true);
        mContactsStatsSyncChoice = (CheckBox)view.findViewById(R.id.contacts_stats_sync_checkbox);
        mContactsStatsSyncChoice.setChecked(true);
        ((TextView)view.findViewById(R.id.contacts_sync_checkbox_title)).setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view2)
            {
                mContactsSyncChoice.toggle();
            }

        });
        textview = (TextView)view.findViewById(R.id.contacts_stats_sync_checkbox_title);
        if(AndroidUtils.hasTelephony(fragmentactivity))
            j = R.string.contacts_stats_sync_preference_enabled_phone_summary;
        else
            j = R.string.contacts_stats_sync_preference_enabled_tablet_summary;
        textview.setText(getString(j));
        textview.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view2)
            {
                mContactsStatsSyncChoice.toggle();
            }

        });
        textview1 = (TextView)view.findViewById(R.id.contacts_stats_sync_checkbox_link);
        s = getString(R.string.contacts_stats_sync_preference_enabled_learn_more);
        spannablestring = new SpannableString(s);
        s1 = HelpUrl.getHelpUrl(fragmentactivity, getResources().getString(R.string.url_param_help_stats_sync)).toString();
        Linkify.addLinks(spannablestring, Pattern.compile(s), s1);
        textview1.setText(spannablestring);
        textview1.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(view);
        onclicklistener = new android.content.DialogInterface.OnClickListener() {

            public final void onClick(DialogInterface dialoginterface, int k)
            {
                android.support.v4.app.FragmentActivity fragmentactivity1 = getActivity();
                if(NewFeaturesFragmentDialog.CONTACTS_SYNC_ENABLED)
                {
                    boolean flag1 = mContactsSyncChoice.isChecked();
                    EsAccountsData.saveContactsSyncPreference(fragmentactivity1, account, flag1);
                }
                boolean flag = mContactsStatsSyncChoice.isChecked();
                EsAccountsData.saveContactsStatsSyncPreference(fragmentactivity1, account, flag);
                EsAnalytics.recordImproveSuggestionsPreferenceChange(fragmentactivity1, account, flag, OzViews.HOME);
                if(flag)
                    EsService.disableWipeoutStats(fragmentactivity1, account);
                else
                    EsService.enableAndPerformWipeoutStats(fragmentactivity1, account);
            }

        };
        
        builder.setPositiveButton(getString(0x104000a), onclicklistener);
        builder.setCancelable(false);
        return builder.create();
    }
}
