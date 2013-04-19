/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.AnalyticsInfo;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsApiProvider;
import com.galaxy.meetup.client.android.network.ApiaryActivity;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.activity.BaseActivity;
import com.galaxy.meetup.client.android.ui.view.AvatarView;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.PlatformContractUtils;

/**
 * 
 * @author sihai
 *
 */
public class PlusOneFragment extends EsFragment {

	private EsAccount mAccount;
    private ApiaryApiInfo mApiaryApiInfo;
    private boolean mInsert;
    private boolean mLoggedPreview;
    private final android.support.v4.app.LoaderManager.LoaderCallbacks mPreviewLoaderCallbacks = new PreviewLoaderCallbacks();
    private ProgressBar mProgressView;
    private Integer mRequestId;
    private final EsServiceListener mServiceListener = new EsServiceListener() {

        public final void onPlusOneApplyResult(int i, ServiceResult serviceresult)
        {
            if(Integer.valueOf(i).equals(mRequestId))
                onFinishedWrite(serviceresult);
        }

    };
    
    private String mToken;
    private String mUrl;
    
    public PlusOneFragment()
    {
    }

    public final EsAccount getAccount()
    {
        return (EsAccount)getArguments().getParcelable("PlusOneFragment#mAccount");
    }

    protected final boolean isEmpty()
    {
        return false;
    }

    public final void onActivityCreated(Bundle bundle)
    {
        super.onActivityCreated(bundle);
        Bundle bundle1 = getArguments();
        mApiaryApiInfo = (ApiaryApiInfo)bundle1.getSerializable("PlusOneFragment#mApiaryApiInfo");
        mToken = bundle1.getString("PlusOneFragment#mToken");
        mUrl = bundle1.getString("PlusOneFragment#mUrl");
        mInsert = bundle1.getBoolean("PlusOneFragment#mInsert");
        mAccount = (EsAccount)bundle1.getParcelable("PlusOneFragment#mAccount");
        TextView textview;
        Resources resources;
        int i;
        Object aobj[];
        String s;
        PackageManager packagemanager;
        ImageView imageview;
        TextView textview1;
        Button button;
        Button button1;
        android.view.View.OnClickListener onclicklistener;
        android.view.View.OnClickListener onclicklistener1;
        if(bundle == null && mInsert)
        {
            BaseActivity instrumentedactivity = (BaseActivity)getActivity();
            PlatformContractUtils.getCallingPackageAnalytics(mApiaryApiInfo);
            AnalyticsInfo analyticsinfo = instrumentedactivity.getAnalyticsInfo();
            mRequestId = Integer.valueOf(EsService.applyPlusOne(getActivity(), mAccount, analyticsinfo, mApiaryApiInfo, mUrl, mInsert, mToken));
            mLoggedPreview = false;
        } else
        if(bundle != null)
        {
            Integer integer;
            if(bundle.containsKey("PlusOneFragment#mRequestId"))
                integer = Integer.valueOf(bundle.getInt("PlusOneFragment#mRequestId"));
            else
                integer = null;
            mRequestId = integer;
            mLoggedPreview = bundle.getBoolean("PlusOneFragment#mLoggedPreview");
        }
        ((AvatarView)getView().findViewById(R.id.plus_one_user_avatar)).setGaiaId(mAccount.getGaiaId());
        textview = (TextView)getView().findViewById(R.id.plus_one_user_name);
        resources = getResources();
        i = R.string.plus_one_title;
        aobj = new Object[1];
        aobj[0] = mAccount.getDisplayName();
        textview.setText(Html.fromHtml(resources.getString(i, aobj)));
        s = mApiaryApiInfo.getSourceInfo().getPackageName();
        packagemanager = getActivity().getPackageManager();
        imageview = (ImageView)getView().findViewById(R.id.plus_one_app_icon);
        textview1 = (TextView)getView().findViewById(R.id.plus_one_app_name);
        try
        {
            imageview.setImageDrawable(packagemanager.getApplicationIcon(s));
            CharSequence charsequence = packagemanager.getApplicationLabel(packagemanager.getApplicationInfo(s, 0));
            textview1.setText(Html.fromHtml(getResources().getString(R.string.plus_one_app, new Object[] {
                charsequence
            })));
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            textview1.setVisibility(4);
            imageview.setVisibility(4);
        }
        ((Button)getView().findViewById(R.id.share)).setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                PlusOneFragment.access$100(PlusOneFragment.this, OzActions.PLATFORM_CLICKED_SHARE_FROM_PLUSONE);
                ApiaryApiInfo apiaryapiinfo = mApiaryApiInfo.getSourceInfo();
                Intent intent = new Intent("com.google.android.apps.plus.SHARE_GOOGLE", Uri.parse(mUrl));
                intent.putExtra("com.google.android.apps.plus.API_KEY", apiaryapiinfo.getApiKey());
                intent.putExtra("com.google.android.apps.plus.CLIENT_ID", apiaryapiinfo.getClientId());
                intent.putExtra("com.google.android.apps.plus.VERSION", apiaryapiinfo.getSdkVersion());
                intent.putExtra("com.google.android.apps.plus.IS_FROM_PLUSONE", true);
                Intent intent1 = Intents.getTargetIntent(getActivity(), intent, mApiaryApiInfo.getSourceInfo().getPackageName());
                intent1.putExtra("from_signup", true);
                intent1.putExtra("start_editing", true);
                startActivityForResult(intent1, 1);
            }

        });
        
        button = (Button)getView().findViewById(R.id.plusone_confirm_button);
        button1 = (Button)getView().findViewById(R.id.plusone_cancel_button);
        onclicklistener = new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                AnalyticsInfo analyticsinfo1;
                FragmentActivity fragmentactivity;
                boolean flag;
                int j;
                if(mInsert)
                    PlusOneFragment.access$100(PlusOneFragment.this, OzActions.PLATFORM_PLUSONE_CANCELED);
                else
                    PlusOneFragment.access$100(PlusOneFragment.this, OzActions.PLATFORM_UNDO_PLUSONE_CONFIRMED);
                analyticsinfo1 = new AnalyticsInfo(OzViews.PLATFORM_PLUS_ONE, OzViews.PLATFORM_THIRD_PARTY_APP, System.currentTimeMillis(), PlatformContractUtils.getCallingPackageAnalytics(mApiaryApiInfo));
                EsService.applyPlusOne(getActivity(), mAccount, analyticsinfo1, mApiaryApiInfo, mUrl, false, mToken);
                fragmentactivity = getActivity();
                flag = mInsert;
                j = 0;
                if(!flag)
                    j = -1;
                fragmentactivity.setResult(j);
                getActivity().finish();
            }
        };
        
        onclicklistener1 = new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                FragmentActivity fragmentactivity;
                byte byte0;
                if(mInsert)
                    PlusOneFragment.access$100(PlusOneFragment.this, OzActions.PLATFORM_PLUSONE_CONFIRMED);
                else
                    PlusOneFragment.access$100(PlusOneFragment.this, OzActions.PLATFORM_UNDO_PLUSONE_CANCELED);
                fragmentactivity = getActivity();
                if(mInsert)
                    byte0 = -1;
                else
                    byte0 = 0;
                fragmentactivity.setResult(byte0);
                getActivity().finish();
            }

        };
        
        button.setText(R.string.plusone_ok);
        button.setOnClickListener(onclicklistener1);
        button1.setText(R.string.plusone_undo);
        button1.setOnClickListener(onclicklistener);
        getLoaderManager().initLoader(0, Bundle.EMPTY, mPreviewLoaderCallbacks);
        updateSpinner(mProgressView);
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
        if(i == 1)
        {
            getActivity().setResult(j, intent);
            getActivity().finish();
        }
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        return layoutinflater.inflate(R.layout.plus_one_fragment, viewgroup, false);
    }

    protected final void onFinishedWrite(ServiceResult serviceresult)
    {
        mRequestId = null;
        FragmentActivity fragmentactivity = getActivity();
        if(serviceresult.hasError())
            fragmentactivity.showDialog(1);
        updateSpinner(mProgressView);
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mRequestId != null && !EsService.isRequestPending(mRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mRequestId.intValue());
            if(serviceresult != null)
                onFinishedWrite(serviceresult);
            else
                mRequestId = null;
        }
        updateSpinner(mProgressView);
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mRequestId != null)
            bundle.putInt("PlusOneFragment#mRequestId", mRequestId.intValue());
        else
            bundle.remove("PlusOneFragment#mRequestId");
        bundle.putBoolean("PlusOneFragment#mLoggedPreview", mLoggedPreview);
    }

    public final void setProgressBar(ProgressBar progressbar)
    {
        mProgressView = progressbar;
        updateSpinner(mProgressView);
    }

    protected final void updateSpinner(ProgressBar progressbar)
    {
        if(mProgressView != null)
        {
            ProgressBar progressbar1 = mProgressView;
            byte byte0;
            if(mRequestId == null)
                byte0 = 8;
            else
                byte0 = 0;
            progressbar1.setVisibility(byte0);
        }
    }
    
    static void access$100(PlusOneFragment plusonefragment, OzActions ozactions)
    {
        if(plusonefragment.mAccount != null)
        {
            BaseActivity instrumentedactivity = (BaseActivity)plusonefragment.getActivity();
            PlatformContractUtils.getCallingPackageAnalytics(plusonefragment.mApiaryApiInfo);
            AnalyticsInfo analyticsinfo = instrumentedactivity.getAnalyticsInfo();
            EsAnalytics.recordEvent(plusonefragment.getActivity(), plusonefragment.getAccount(), analyticsinfo, ozactions);
        }
        return;
    }
    
    static int access$702(PlusOneFragment plusonefragment, boolean flag)
    {
        plusonefragment.mLoggedPreview = true;
        return 1;
    }
    
    
	final class PreviewLoaderCallbacks implements
			android.support.v4.app.LoaderManager.LoaderCallbacks {

		public final Loader onCreateLoader(int i, Bundle bundle) {
			EsCursorLoader escursorloader = new EsCursorLoader(getActivity());
			escursorloader.setUri(EsApiProvider.makePreviewUri(mApiaryApiInfo));
			String as[] = new String[1];
			as[0] = mUrl;
			escursorloader.setSelectionArgs(as);
			return escursorloader;
		}

		public final void onLoadFinished(Loader loader, Object obj) {
			Cursor cursor = (Cursor) obj;
			if (cursor != null && cursor.getExtras() != null) {
				android.os.Parcelable aparcelable[] = cursor
						.getExtras()
						.getParcelableArray(
								"com.google.android.apps.content.EXTRA_ACTIVITY");
				ApiaryActivity apiaryactivity;
				boolean flag;
				if (aparcelable != null && aparcelable.length > 0)
					apiaryactivity = (ApiaryActivity) aparcelable[0];
				else
					apiaryactivity = null;
				flag = false;
				if (apiaryactivity != null)
					flag = true;
				if (!flag && EsLog.isLoggable("PlusOneActivity", 3))
					Log.d("PlusOneActivity",
							(new StringBuilder(
									"Unable to url retrieve preview for: "))
									.append(mUrl).toString());
				if (!mLoggedPreview) {
					mLoggedPreview = true;
					BaseActivity instrumentedactivity = (BaseActivity) getActivity();
					PlatformContractUtils.getCallingPackageAnalytics(mApiaryApiInfo);
					AnalyticsInfo analyticsinfo = instrumentedactivity.getAnalyticsInfo();
					OzActions ozactions;
					if (flag)
						ozactions = OzActions.PLATFORM_PLUSONE_PREVIEW_SHOWN;
					else
						ozactions = OzActions.PLATFORM_SHARE_PREVIEW_ERROR;
					EsAnalytics.recordEvent(getActivity(), getAccount(),
							analyticsinfo, ozactions);
				}
				//ViewGroup viewgroup = (ViewGroup) this.getView().findViewById(access$702);
			}
		}

		public final void onLoaderReset(Loader loader) {
		}
	}

}
