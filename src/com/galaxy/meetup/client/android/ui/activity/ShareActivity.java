/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.BirthdayData;
import com.galaxy.meetup.client.android.api.CallToActionData;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.ImageUtils.InsertCameraPhotoDialogDisplayer;
import com.galaxy.meetup.client.util.PlatformContractUtils;

/**
 * 
 * @author sihai
 *
 */
public class ShareActivity extends PostActivity implements
		InsertCameraPhotoDialogDisplayer {

	private android.content.DialogInterface.OnClickListener mDialogListener;
    private ApiaryApiInfo mInfo;
    private android.content.DialogInterface.OnClickListener mLocationDialogListener;
    
    public ShareActivity()
    {
        mDialogListener = new DialogListener();
        mLocationDialogListener = new LocationDialogListener();
    }

    private void copyStringExtraToArgs(String s, String s1, Bundle bundle)
    {
        String s2 = getIntent().getStringExtra(s);
        if(s2 == null)
        {
            CharSequence charsequence = getIntent().getCharSequenceExtra(s);
            if(charsequence != null)
                s2 = charsequence.toString();
        }
        if(s2 != null)
            bundle.putString(s1, s2);
    }

    private boolean isThirdPartyPackageSecure()
    {
        Intent intent = getIntent();
        String s = getCallingPackage();
        boolean flag = intent.getBooleanExtra("from_signup", false);
        String s1 = intent.getStringExtra("calling_package");
        boolean flag1 = TextUtils.isEmpty(s);
        boolean flag2 = false;
        if(!flag1)
        {
            boolean flag3 = TextUtils.isEmpty(s1);
            flag2 = false;
            if(!flag3)
            {
                boolean flag4 = s.equals(getPackageName());
                flag2 = false;
                if(flag4)
                {
                    flag2 = false;
                    if(flag)
                        flag2 = true;
                }
            }
        }
        return flag2;
    }

    protected final Bundle getPostFragmentArguments() {
        Bundle bundle = super.getPostFragmentArguments();
        if(null == bundle) {
        	return null;
        }
        
        Intent intent = getIntent();
        copyStringExtraToArgs("com.google.android.apps.plus.CID", "cid", bundle);
        copyStringExtraToArgs("com.google.android.apps.plus.LOCATION_NAME", "location_name", bundle);
        copyStringExtraToArgs("com.google.android.apps.plus.EXTERNAL_ID", "external_id", bundle);
        copyStringExtraToArgs("com.google.android.apps.plus.FOOTER", "footer", bundle);
        copyStringExtraToArgs("com.google.android.apps.plus.LATITUDE", "latitude", bundle);
        copyStringExtraToArgs("com.google.android.apps.plus.LONGITUDE", "longitude", bundle);
        copyStringExtraToArgs("com.google.android.apps.plus.ADDRESS", "address", bundle);
        copyStringExtraToArgs("com.google.android.apps.plus.CONTENT_DEEP_LINK_ID", "content_deep_link_id", bundle);
        Bundle bundle1 = intent.getBundleExtra("com.google.android.apps.plus.CONTENT_DEEP_LINK_METADATA");
        if(bundle1 != null)
            bundle.putBundle("content_deep_link_metadata", bundle1);
        if(getIntent().hasExtra("com.google.android.apps.plus.IS_FROM_PLUSONE"))
            bundle.putBoolean("is_from_plusone", getIntent().getBooleanExtra("com.google.android.apps.plus.IS_FROM_PLUSONE", false));
        copyStringExtraToArgs("android.intent.extra.TEXT", "android.intent.extra.TEXT", bundle);
        String s = intent.getDataString();
        if(s != null && "com.google.android.apps.plus.SHARE_GOOGLE".equals(intent.getAction()))
            bundle.putString("url", s);
        String s1 = intent.getAction();
        if("com.google.android.apps.plus.GOOGLE_BIRTHDAY_POST".equals(s1))
        {
            if(TextUtils.isEmpty(intent.getStringExtra("RECIPIENT_ID")) || TextUtils.isEmpty(intent.getStringExtra("RECIPIENT_NAME")) || intent.getIntExtra("com.google.android.apps.plus.BIRTHDAY_YEAR", 0) == 0)
            {
                return null;
            }
            bundle.putParcelable("birthday_data", new BirthdayData(intent.getStringExtra("RECIPIENT_ID"), intent.getStringExtra("RECIPIENT_NAME"), intent.getIntExtra("com.google.android.apps.plus.BIRTHDAY_YEAR", 0)));
            bundle.putParcelable("audience", new AudienceData(new PersonData(intent.getStringExtra("RECIPIENT_ID"), intent.getStringExtra("RECIPIENT_NAME"), null)));
        }
        if("com.google.android.apps.plus.GOOGLE_PLUS_SHARE".equals(s1) || "android.intent.action.SEND".equals(s1))
        {
            copyStringExtraToArgs("com.google.android.apps.plus.CONTENT_DEEP_LINK_ID", "content_deep_link_id", bundle);
            copyStringExtraToArgs("com.google.android.apps.plus.CONTENT_URL", "url", bundle);
            if(mAccount != null && mAccount.hasGaiaId() && TextUtils.equals(mAccount.getGaiaId(), intent.getStringExtra("com.google.android.apps.plus.SENDER_ID")))
            {
                ArrayList arraylist = intent.getStringArrayListExtra("RECIPIENT_IDS");
                ArrayList arraylist1 = intent.getStringArrayListExtra("RECIPIENT_DISPLAY_NAMES");
                int i;
                if(arraylist != null)
                    i = arraylist.size();
                else
                    i = 0;
                if(arraylist1 != null && i != 0)
                {
                    int j = arraylist1.size();
                    if(i == j)
                    {
                        ArrayList arraylist2 = new ArrayList(arraylist.size());
                        for(int k = 0; k < i; k++)
                        {
                            PersonData persondata = new PersonData((String)arraylist.get(k), (String)arraylist1.get(k), null);
                            arraylist2.add(persondata);
                        }

                        AudienceData audiencedata = new AudienceData(arraylist2, null);
                        bundle.putParcelable("audience", audiencedata);
                    }
                }
            }
        }
        if(intent.getBooleanExtra("com.google.android.apps.plus.GOOGLE_INTERACTIVE_POST", false))
        {
            String s2 = intent.getStringExtra("com.google.android.apps.plus.CONTENT_URL");
            String s3 = intent.getStringExtra("com.google.android.apps.plus.CONTENT_DEEP_LINK_ID");
            if(TextUtils.isEmpty(s2) && TextUtils.isEmpty(s3))
            {
                return null;
            }
            if(!isThirdPartyPackageSecure())
            {
            	return null;
            }
            CallToActionData calltoactiondata = CallToActionData.fromExtras(intent.getBundleExtra("com.google.android.apps.plus.CALL_TO_ACTION"));
            if(calltoactiondata == null)
            {
            	return null;
            }
            bundle.putParcelable("call_to_action", calltoactiondata);
            if(TextUtils.isEmpty(intent.getStringExtra("com.google.android.apps.plus.SENDER_ID")))
            {
            	return null;
            }
        }
        bundle.putSerializable("api_info", mInfo);
       
        return bundle;
    }

    protected final CharSequence getTitleButton3Text$9aa72f6()
    {
        return getResources().getText(R.string.post_share_button_text);
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.SHARE;
    }

    protected final int getViewId()
    {
        return R.layout.share_activity;
    }

    public final void invalidateMenu()
    {
        createTitlebarButtons(R.menu.share_menu);
        if(android.os.Build.VERSION.SDK_INT >= 11)
            invalidateOptionsMenu();
    }

    public void onCreate(Bundle bundle) {
    	
    	// TODO
    }

    public Dialog onCreateDialog(int i, Bundle bundle) {
    	
    	Dialog dialog = null;
    	switch(i) {
    	case 12763:
    		android.app.AlertDialog.Builder builder4 = new android.app.AlertDialog.Builder(this);
            builder4.setMessage(R.string.share_connection_error).setPositiveButton(0x104000a, mDialogListener).setCancelable(true);
            dialog = builder4.create();
            break;
    	case 16542:
    		dialog = new ProgressDialog(this);
    	    ((ProgressDialog) (dialog)).setMessage(getString(R.string.post_operation_pending));
    	    ((ProgressDialog) (dialog)).setProgressStyle(0);
    	    ((ProgressDialog) (dialog)).setCancelable(false);
    	    break;
    	case 21305:
    		android.app.AlertDialog.Builder builder5 = new android.app.AlertDialog.Builder(this);
            builder5.setMessage(R.string.share_incorrect_account).setNeutralButton(0x104000a, mDialogListener).setCancelable(false);
            dialog = builder5.create();
            break;
    	case 22689:
    		android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(this);
            builder2.setMessage(R.string.share_preview_post_error).setNeutralButton(0x104000a, mDialogListener).setCancelable(false);
            dialog = builder2.create();
    		break;
    	case 28199:
    		android.app.AlertDialog.Builder builder3 = new android.app.AlertDialog.Builder(this);
            builder3.setMessage(R.string.share_preview_error).setPositiveButton(0x104000a, mDialogListener).setCancelable(true);
            dialog = builder3.create();
    		break;
    	case 29341608:
    		android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
            builder1.setMessage(R.string.location_provider_disabled);
            builder1.setPositiveButton(R.string.yes, mLocationDialogListener);
            builder1.setNegativeButton(R.string.no, mLocationDialogListener);
            dialog = builder1.create();
    		break;
    	case 30875012:
    		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(R.string.post_location_dialog_title);
            builder.setMessage(R.string.sharebox_location_dialog_message);
            builder.setNeutralButton(0x104000a, mLocationDialogListener);
            builder.setCancelable(false);
            dialog = builder.create();
    		break;
    	case 2131361854:
    		dialog = ImageUtils.createInsertCameraPhotoDialog(this);
    		break;
    	default:
    		break;
    	}
    	
    	return dialog;
    	
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(i == 0x102002c || i == R.id.menu_discard)
            mFragment.onDiscard(flag);
        else
        if(i == R.id.menu_post)
            mFragment.post();
        else
            flag = false;
        return flag;
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.menu_post).setVisible(false);
        return true;
    }

    protected final void onPrepareTitlebarButtons(Menu menu)
    {
        int i = 0;
        while(i < menu.size()) 
        {
            MenuItem menuitem = menu.getItem(i);
            boolean flag;
            if(menuitem.getItemId() == R.id.menu_post)
                flag = true;
            else
                flag = false;
            menuitem.setVisible(flag);
            i++;
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mAccount != null)
            bundle.putParcelable("account", mAccount);
        if(mInfo != null)
            bundle.putSerializable("ShareActivity.mInfo", mInfo);
    }

    protected final void onTitlebarLabelClick()
    {
        finish();
    }

    protected final void recordLaunchEvent()
    {
        Bundle bundle;
        if(isFromThirdPartyApp(getIntent()))
        {
            bundle = new Bundle();
            bundle.putBoolean("extra_platform_event", true);
        } else
        {
            bundle = null;
        }
        PlatformContractUtils.getCallingPackageAnalytics(mInfo);
        recordUserAction(getAnalyticsInfo(), OzActions.PLATFORM_OPEN_SHAREBOX, bundle);
    }

    protected final void showTitlebar(boolean flag, boolean flag1)
    {
        super.showTitlebar(flag, false);
        findViewById(R.id.title_layout).setPadding(getResources().getDimensionPixelOffset(R.dimen.share_title_padding_left), 0, 0, 0);
    }

	private final class DialogListener implements
			android.content.DialogInterface.OnClickListener {

		public final void onClick(DialogInterface dialoginterface, int i) {
			if (-3 == i) {
				dialoginterface.dismiss();
				finish();
			} else {
				dialoginterface.dismiss();
			}
		}

	}

	private final class LocationDialogListener implements
			android.content.DialogInterface.OnClickListener {

		public final void onClick(DialogInterface dialoginterface, int i) {
			if (-3 == i) {
				EsAccountsData.saveLocationDialogSeenPreference(
						ShareActivity.this, mAccount, true);
			} else if (-2 == i) {
				mFragment.setLocationChecked(false);
			} else if (-1 == i) {
				Intent intent = Intents.getLocationSettingActivityIntent();
				startActivity(intent);
			}
		}
	}
}
