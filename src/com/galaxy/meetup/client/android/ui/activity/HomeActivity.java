/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.HostNavigationBarAdapter;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.MeetupFeedback;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ShakeDetector;
import com.galaxy.meetup.client.android.SignOnManager;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsNotificationData;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.hangout.GCommApp;
import com.galaxy.meetup.client.android.hangout.Log;
import com.galaxy.meetup.client.android.service.AndroidContactsSync;
import com.galaxy.meetup.client.android.service.AndroidNotification;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog;
import com.galaxy.meetup.client.android.ui.fragments.HostedAlbumsFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedEventListFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedHangoutFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedMessengerFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedPeopleFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedProfileFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedSquareListFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedStreamFragment;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.HostLayout;
import com.galaxy.meetup.client.android.ui.view.NewFeaturesFragmentDialog;
import com.galaxy.meetup.client.util.HelpUrl;
import com.galaxy.meetup.client.util.MapUtils;
import com.galaxy.meetup.client.util.Property;


/**
 * 
 * @author sihai
 * 
 */
public class HomeActivity extends BaseActivity implements
		LoaderManager.LoaderCallbacks, AdapterView.OnItemClickListener,
		EsAccountsData.ExperimentListener,
		HostActionBar.OnUpButtonClickListener, HostLayout.HostLayoutListener {

	private static final Uri REMOVE = Uri.parse("https://plus.google.com/downgrade/");
    private HostActionBar mActionBar;
    private Bundle mDestination;
    private Parcelable mDestinationState[];
    private boolean mDestinationsConfigured;
    private List mDialogTags;
    protected boolean mFirstLoad;
    private HostLayout mHostLayout;
    private ListView mNavigationBar;
    private HostNavigationBarAdapter mNavigationBarAdapter;
    private int mNavigationBarScrollPosition;
    private int mNotificationCount;
    private boolean mNotificationsLoaded;
    private Integer mRequestId;
    private EsServiceListener mServiceListener;
    private ShakeDetector.ShakeEventListener mShakeListener;
    private SignOnManager mSignOnManager;
    
    public HomeActivity() {
        mNavigationBarScrollPosition = -1;
        mDestinationState = new Parcelable[9];
        mFirstLoad = true;
        mServiceListener = new EsServiceListener() {

            public final void onSyncNotifications(int i, ServiceResult serviceresult) {
                handleServiceCallback(i, serviceresult);
            }

        };
        mSignOnManager = new SignOnManager(this);
    }
    
    private void buildDestinationBundleForIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mDestination = new Bundle();
        if(bundle == null) {
            mDestination.putInt("destination", 0);
        } else {
            mDestination.putAll(bundle);
            int i = intent.getIntExtra("destination", 0);
            mDestination.putInt("destination", i);
        }
        mDestination.putParcelable("account", mSignOnManager.getAccount());
    }
    
    private void configureDestinations() {
        mNavigationBarAdapter.removeAllDestinations();
        mNavigationBarAdapter.addDestination(0, R.drawable.ic_nav_home, R.string.home_stream_label);
        mNavigationBarAdapter.addDestination(5, R.drawable.ic_nav_circles, R.string.home_screen_people_label);
        EsAccount esaccount = mSignOnManager.getAccount();
        boolean flag;
        Intent intent;
        boolean flag1;
        boolean flag2;
        if(esaccount != null && esaccount.isPlusPage())
            flag = true;
        else
            flag = false;
        if(flag)
            mNavigationBarAdapter.addDestination(1, R.drawable.ic_nav_profile, esaccount.getDisplayName(), esaccount.getGaiaId());
        else
            mNavigationBarAdapter.addDestination(1, R.drawable.ic_nav_profile, R.string.home_screen_profile_label);
        mNavigationBarAdapter.addDestination(7, R.drawable.ic_nav_myphotos, R.string.home_screen_photos_label);
        if(Property.ENABLE_SQUARES.getBoolean())
            mNavigationBarAdapter.addDestination(8, R.drawable.ic_nav_communities, R.string.home_screen_squares_label);
        if(esaccount != null && !flag && Hangout.isHangoutCreationSupported(getApplicationContext(), esaccount))
            mNavigationBarAdapter.addDestination(3, R.drawable.ic_nav_hangouts, R.string.home_screen_hangout_label);
        mNavigationBarAdapter.addDestination(2, R.drawable.ic_nav_events, R.string.home_screen_events_label);
        if(!flag)
            mNavigationBarAdapter.addDestination(4, R.drawable.ic_nav_messenger, R.string.home_screen_huddle_label);
        intent = MapUtils.getPlacesActivityIntent();
        flag1 = getPackageManager().queryIntentActivities(intent, 0x10000).isEmpty();
        flag2 = false;
        if(!flag1)
            flag2 = true;
        if(flag2 && !flag)
            mNavigationBarAdapter.addDestination(6, R.drawable.ic_nav_local, R.string.home_screen_local_label);
        mNavigationBarAdapter.showDestinations();
        mDestinationsConfigured = true;
        restoreNavigationBarScrollPosition();
    }
    
	private void handleServiceCallback(int i, ServiceResult serviceresult) {
		if (mRequestId != null && mRequestId.intValue() == i) {
			mRequestId = null;
			updateNotificationsSpinner();
		}
	}
	
	private static boolean isLauncherIntent(Intent intent) {
		boolean flag;
		if ("android.intent.action.MAIN".equals(intent.getAction())
				&& intent.getCategories() != null
				&& intent.getCategories().contains(
						"android.intent.category.LAUNCHER")
				&& intent.getExtras() == null)
			flag = true;
		else
			flag = false;
		return flag;
	}
	
	private void navigateToDestination(int i, Bundle bundle, boolean flag, Fragment.SavedState savedstate) {
		EsAccount esaccount = mSignOnManager.getAccount();
        boolean flag1;
        if(esaccount != null && esaccount.isPlusPage())
            flag1 = true;
        else
            flag1 = false;
        if(i == 4 && flag1)
            i = 0;
        
        switch(i) {
        case 0:
        	HostedStreamFragment hostedstreamfragment = new HostedStreamFragment();
            hostedstreamfragment.setArguments(bundle);
            mHostLayout.showFragment(hostedstreamfragment, flag, savedstate);
        	break;
        case 1:
        	HostedProfileFragment hostedprofilefragment = new HostedProfileFragment();
            if(!bundle.containsKey("person_id"))
                bundle.putString("person_id", mSignOnManager.getAccount().getPersonId());
            hostedprofilefragment.setArguments(bundle);
            mHostLayout.showFragment(hostedprofilefragment, flag, savedstate);
        	break;
        case 2:
        	HostedEventListFragment hostedeventlistfragment = new HostedEventListFragment();
            bundle.putBoolean("refresh", true);
            hostedeventlistfragment.setArguments(bundle);
            mHostLayout.showFragment(hostedeventlistfragment, flag, savedstate);
        	break;
        case 3:
        	try {
	        	Context context;
	            context = getApplicationContext();
	            if(esaccount == null)
	                break;
	            if(Hangout.isHangoutCreationSupported(context, esaccount))
	            {
	                GCommApp.getInstance(context).getGCommNativeWrapper().getCurrentState();
	                HostedHangoutFragment hostedhangoutfragment = new HostedHangoutFragment();
	                hostedhangoutfragment.setArguments(bundle);
	                mHostLayout.showFragment(hostedhangoutfragment, flag, savedstate);
	            }
	            if(esaccount != null && Hangout.getSupportedStatus(context, esaccount) == Hangout.SupportStatus.SUPPORTED && GCommApp.getInstance(context).isInAHangout())
	            {
	                Intent intent = GCommApp.getInstance(this).getGCommService().getNotificationIntent();
	                if(intent != null)
	                    startActivity(intent);
	            }
        	} catch (LinkageError linkageerror) {
        		int j = R.string.hangout_native_lib_error;
                String s = getResources().getString(j);
                Object aobj[] = new Object[2];
                aobj[0] = s;
                aobj[1] = Boolean.valueOf(false);
                Log.debug("showError: message=%s finishOnOk=%s", aobj);
                AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(null, s, getResources().getString(R.string.ok), null, 0x1080027);
                alertfragmentdialog.setCancelable(false);
                final boolean finishOnOk = flag;
                alertfragmentdialog.setListener(new AlertFragmentDialog.AlertDialogListener() {

                    public final void onDialogCanceled(String s1)
                    {
                    }

                    public final void onDialogListClick(int k, Bundle bundle1)
                    {
                    }

                    public final void onDialogNegativeClick(String s1)
                    {
                    }

                    public final void onDialogPositiveClick(Bundle bundle1, String s1)
                    {
                        if(finishOnOk)
                        {
                            finish();
                        } else
                        {
                            if(mHostLayout.isNavigationBarVisible())
                                mHostLayout.hideNavigationBar();
                            mDestination = null;
                        }
                    }
                });
                alertfragmentdialog.show(getSupportFragmentManager(), "error");

        	}
        	break;
        case 4:
        	HostedMessengerFragment hostedmessengerfragment = new HostedMessengerFragment();
            hostedmessengerfragment.setArguments(bundle);
            mHostLayout.showFragment(hostedmessengerfragment, true, savedstate);
        	break;
        case 5:
        	HostedPeopleFragment hostedpeoplefragment = new HostedPeopleFragment(true);
            hostedpeoplefragment.setArguments(bundle);
            mHostLayout.showFragment(hostedpeoplefragment, flag, savedstate);
        	break;
        case 6:
        	
        	break;
        case 7:
        	HostedAlbumsFragment hostedalbumsfragment = new HostedAlbumsFragment();
            if(!bundle.containsKey("person_id"))
                bundle.putString("person_id", mSignOnManager.getAccount().getPersonId());
            if(!bundle.containsKey("photos_home"))
                bundle.putBoolean("photos_home", true);
            hostedalbumsfragment.setArguments(bundle);
            mHostLayout.showFragment(hostedalbumsfragment, flag, savedstate);
        	break;
        case 8:
        	HostedSquareListFragment hostedsquarelistfragment = new HostedSquareListFragment();
            bundle.putBoolean("refresh", true);
            hostedsquarelistfragment.setArguments(bundle);
            mHostLayout.showFragment(hostedsquarelistfragment, flag, null);
        	break;
        default:
        	break;
        }
    }
	
	private void refreshNotifications() {
        mRequestId = EsService.syncNotifications(this, mSignOnManager.getAccount());
        updateNotificationsSpinner();
    }

    private void restoreNavigationBarScrollPosition() {
        if(mNavigationBarScrollPosition != -1 && mDestinationsConfigured && mNotificationsLoaded) {
            mNavigationBar.setSelection(mNavigationBarScrollPosition);
            mNavigationBarScrollPosition = -1;
        }
    }

    private void saveDestinationState() {
        if(mDestination != null) {
            int i = mDestination.getInt("destination", -1);
            if(i != -1)
                mDestinationState[i] = mHostLayout.saveHostedFragmentState();
        }
    }

    private void showCurrentDestination() {
        if(mActionBar != null)
            mActionBar.dismissPopupMenus();
        if(mDialogTags != null && !mDialogTags.isEmpty()) {
            FragmentManager fragmentmanager = getSupportFragmentManager();
            Iterator iterator = mDialogTags.iterator();
            do {
                if(!iterator.hasNext())
                    break;
                DialogFragment dialogfragment = (DialogFragment)fragmentmanager.findFragmentByTag((String)iterator.next());
                if(dialogfragment != null)
                    dialogfragment.dismissAllowingStateLoss();
            } while(true);
            mDialogTags = null;
        }
        navigateToDestination(mDestination.getInt("destination"), mDestination, false, null);
    }

    private void updateNotificationsSpinner() {
        if(mNavigationBarAdapter != null)
            if(mRequestId != null)
                mNavigationBarAdapter.showProgressIndicator();
            else
                mNavigationBarAdapter.hideProgressIndicator();
    }

    protected final EsAccount getAccount() {
        return mSignOnManager.getAccount();
    }

    public final OzViews getViewForLogging() {
        HostedFragment hostedfragment;
        OzViews ozviews;
        if(mHostLayout == null)
            hostedfragment = null;
        else
            hostedfragment = mHostLayout.getCurrentHostedFragment();
        if(hostedfragment == null)
            ozviews = OzViews.HOME;
        else
            ozviews = hostedfragment.getViewForLogging();
        return ozviews;
    }

    public void onActivityResult(int i, int j, Intent intent) {
        if(!mSignOnManager.onActivityResult(i, j))
            super.onActivityResult(i, j, intent);
    }

    public final void onAttachFragment(Fragment fragment) {
        if(mHostLayout != null && (fragment instanceof HostedFragment))
            mHostLayout.onAttachFragment((HostedFragment)fragment);
        if(fragment instanceof DialogFragment) {
            if(mDialogTags == null)
                mDialogTags = new ArrayList();
            mDialogTags.add(fragment.getTag());
        }
    }
    
	public void onBackPressed() {
		if (mHostLayout.isNavigationBarVisible()) {
			mHostLayout.hideNavigationBar();
			return;
		}
		if (mHostLayout.getCurrentHostedFragment() == null
				|| !mHostLayout.getCurrentHostedFragment().onBackPressed())
			super.onBackPressed();
	}
	
	protected void onCreate(Bundle bundle) {
		if(bundle != null)
            bundle.setClassLoader(getClass().getClassLoader());
        super.onCreate(bundle);
        if(!isTaskRoot()) {
        	 int i = getIntent().getIntExtra("destination", 0);
        	 if(isLauncherIntent(getIntent()) || i == 4) {
        		 finish();
        		 return;
        	 }
        }
        
		mSignOnManager.onCreate(bundle, getIntent());
		if(isFinishing()) {
			// TODO
			return;
		}
		
		setContentView(R.layout.host_navigation_activity);
        mHostLayout = (HostLayout)findViewById(R.id.host);
        mHostLayout.setListener(this);
        mActionBar = mHostLayout.getActionBar();
        mActionBar.setOnUpButtonClickListener(this);
        mActionBar.setUpButtonContentDescription(getString(R.string.main_menu_content_description));
        mNavigationBar = (ListView)mHostLayout.getNavigationBar();
        mNavigationBarAdapter = new HostNavigationBarAdapter(this);
        mNavigationBarAdapter.setCollapsedMenuItemCount(mHostLayout.getCollapsedMenuItemCount());
        mNavigationBar.setAdapter(mNavigationBarAdapter);
        mNavigationBar.setOnItemClickListener(this);
        EsAccountsData.registerExperimentListener(this);
        configureDestinations();
        if(null != bundle) {
        	if(bundle.containsKey("reqId"))
        		mRequestId = Integer.valueOf(bundle.getInt("reqId"));
        	mNavigationBarScrollPosition = bundle.getInt("scrollPos");
        	mNavigationBarAdapter.setCollapsed(bundle.getBoolean("navBarCollapsed", true));
        	mHostLayout.attachActionBar();
        } else {
        	if(mSignOnManager.isSignedIn()) {
        		boolean flag;
		            buildDestinationBundleForIntent();
		            showCurrentDestination();
		            if(getIntent().getBooleanExtra("show_notifications", false))
		                mHostLayout.showNavigationBarDelayed();
		            EsAccount esaccount = mSignOnManager.getAccount();
		            boolean flag2;
		            if(esaccount != null && esaccount.isPlusPage())
		                flag = true;
		            else
		                flag = false;
		            if(!flag && EsAccountsData.isContactsStatsSyncPreferenceSet(this, mSignOnManager.getAccount())) {
		            	(new NewFeaturesFragmentDialog(mSignOnManager.getAccount())).show(getSupportFragmentManager(), "new_features");
		           } else {
		        	   if(!flag)
		               {
		                   boolean flag1;
		                   if(android.os.Build.VERSION.SDK_INT >= 14 && !EsAccountsData.isContactsSyncPreferenceSet(this, mSignOnManager.getAccount()) && AndroidContactsSync.isAndroidSyncSupported(this))
		                       flag1 = true;
		                   else
		                       flag1 = false;
		                   if(flag1)
		                       startActivity(Intents.getContactsSyncConfigActivityIntent(this, mSignOnManager.getAccount()));
		               }
		           }
        	}
        }
        
        if(mSignOnManager.getAccount() != null)
            getSupportLoaderManager().initLoader(0, null, this);
        mActionBar.setNotificationCount(mNotificationCount);
        ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
        if(shakedetector != null)
        {
            mShakeListener = new ShakeDetector.ShakeEventListener() {

                public final void onShakeDetected()
                {
                    EsAccount esaccount1 = getAccount();
                    Context context = getApplicationContext();
                    if(esaccount1 != null && Hangout.isHangoutCreationSupported(context, esaccount1) || Hangout.getSupportedStatus(context, esaccount1) == Hangout.SupportStatus.SUPPORTED && GCommApp.getInstance(context).isInAHangout())
                    {
                        Intent intent = Intents.getHangoutActivityIntent(context, esaccount1);
                        intent.addFlags(0x4000000);
                        intent.addFlags(0x10000000);
                        getApplicationContext().startActivity(intent);
                    }
                }

            };
            shakedetector.addEventListener(mShakeListener);
            shakedetector.start();
        }
    }
	
	public final Loader onCreateLoader(int i, Bundle bundle) {
		Loader loader = null;
		switch(i) {
		case 0:
			EsAccount esaccount = mSignOnManager.getAccount();
			loader = null;
	        if(esaccount != null)
	        	loader = new EsCursorLoader(this, EsProvider.appendAccountParameter(EsProvider.NOTIFICATIONS_URI, esaccount), EsNotificationData.NotificationQuery.PROJECTION, null, null, "timestamp DESC");
	        break;
		default:
			break;
		}
		return loader;
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.host_menu, menu);
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();
		ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
		if (shakedetector != null) {
			shakedetector.removeEventListener(mShakeListener);
			shakedetector.stop();
		}
		ImageResourceManager.getInstance(this).verifyEmpty();
		EsAccountsData.unregisterExperimentListener(this);
	}

	public final void onExperimentsChanged() {
		configureDestinations();
	}
	
	public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
		if(mNavigationBarAdapter.isNotificationHeader(i)) {
			refreshNotifications();
			return;
		}
		int j = mNavigationBarAdapter.getDestinationId(i);
        if(j != -1)
        {
            if(j == -2)
                mNavigationBarAdapter.setCollapsed(false);
            else
            if(j == 6)
            {
                startActivity(MapUtils.getPlacesActivityIntent());
                mHostLayout.hideNavigationBar();
            } else
            if(mDestination != null && mDestination.getInt("destination") == j)
            {
                mHostLayout.hideNavigationBar();
            } else
            {
                saveDestinationState();
                mDestination = new Bundle();
                mDestination.putParcelable("account", mSignOnManager.getAccount());
                mDestination.putInt("destination", j);
                navigateToDestination(j, mDestination, true, (android.support.v4.app.Fragment.SavedState)mDestinationState[j]);
            }
        } else
        {
            Cursor cursor = (Cursor)mNavigationBarAdapter.getItem(i);
            if(cursor != null)
            {
                Intent intent = AndroidNotification.newViewNotificationIntent(this, mSignOnManager.getAccount(), cursor);
                if(intent != null)
                {
                    String s = cursor.getString(1);
                    if(cursor.getInt(11) != 1)
                        EsService.markNotificationAsRead(this, mSignOnManager.getAccount(), s);
                    intent.putExtra("com.google.plus.analytics.intent.extra.START_VIEW", OzViews.NOTIFICATIONS_WIDGET);
                    intent.putExtra("com.google.plus.analytics.intent.extra.FROM_NOTIFICATION", true);
                    startActivity(intent);
                }
            }
        }
    }
	
	public final void onLoadFinished(Loader loader, Object obj) {
        Cursor cursor = (Cursor)obj;
        mNavigationBarAdapter.setNotifications(cursor);
        mNotificationCount = mNavigationBarAdapter.getUnreadNotificationCount();
        mActionBar.setNotificationCount(mNotificationCount);
        mNotificationsLoaded = true;
        restoreNavigationBarScrollPosition();
        if(cursor.getCount() == 0 && mFirstLoad)
            refreshNotifications();
        mFirstLoad = false;
    }

    public final void onLoaderReset(Loader loader) {
    }

    public final void onNavigationBarVisibilityChange(boolean flag) {
        if(!flag && mNavigationBarAdapter != null) {
            mNavigationBarAdapter.setCollapsed(true);
            if(mNavigationBarAdapter.getUnreadNotificationCount() > 0) {
                EsAccount esaccount = mSignOnManager.getAccount();
                if(esaccount != null)
                    EsService.tellServerNotificationsWereRead(this, esaccount);
            }
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(mSignOnManager.isSignedIn() && !isLauncherIntent(intent)) {
            setIntent(intent);
            buildDestinationBundleForIntent();
            showCurrentDestination();
            if(intent.getBooleanExtra("show_notifications", false))
                mHostLayout.showNavigationBarDelayed();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        boolean flag = true;
        if(!mHostLayout.onOptionsItemSelected(menuitem)) {
            int i = menuitem.getItemId();
            if(i == R.id.search)
                startActivity(Intents.getPostSearchActivityIntent(this, mSignOnManager.getAccount(), null));
            else if(i == R.id.feedback) {
                recordUserAction(OzActions.SETTINGS_FEEDBACK);
                MeetupFeedback.launch(this);
            } else if(i == R.id.settings)
                startActivity(Intents.getSettingsActivityIntent(this, mSignOnManager.getAccount()));
            else if(i == R.id.help)
                startExternalActivity(new Intent("android.intent.action.VIEW", HelpUrl.getHelpUrl(this, getResources().getString(R.string.url_param_help_stream))));
            else if(i == R.id.sign_out)
                mSignOnManager.signOut(false);
            else
                flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    protected void onPause() {
        super.onPause();
        mSignOnManager.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        int i = menu.size();
        int j = 0;
        while(j < i) {
            MenuItem menuitem = menu.getItem(j);
            int k = menuitem.getItemId();
            if(k == R.id.search || k == R.id.feedback || k == R.id.settings || k == R.id.help || k == R.id.sign_out)
                menuitem.setVisible(true);
            else
                menuitem.setVisible(false);
            j++;
        }
        mHostLayout.onPrepareOptionsMenu(menu);
        return true;
    }

    protected void onResume() {
        boolean flag;
        super.onResume();
        flag = mSignOnManager.onResume();
        EsService.registerListener(mServiceListener);
        if(mRequestId != null)
        {
            if(!EsService.isRequestPending(mRequestId.intValue()))
            {
                int i = mRequestId.intValue();
                handleServiceCallback(i, EsService.removeResult(mRequestId.intValue()));
            } else
            {
                updateNotificationsSpinner();
            }
        } else
        {
            updateNotificationsSpinner();
        }
        
        if(getIntent().getBooleanExtra("sign_out", false)) {
        	mSignOnManager.signOut(true);
            startExternalActivity(new Intent("android.intent.action.VIEW", REMOVE));
            finish();
            return;
        }
        
        if(mSignOnManager.getAccount() == null)
            Arrays.fill(mDestinationState, null);
        if(flag)
        {
            getSupportLoaderManager().initLoader(0, null, this);
            configureDestinations();
            buildDestinationBundleForIntent();
            showCurrentDestination();
        }
    }

	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		if (mRequestId != null)
			bundle.putInt("reqId", mRequestId.intValue());
		if (mNavigationBar != null) {
			bundle.putInt("scrollPos", mNavigationBar.getFirstVisiblePosition());
			bundle.putBoolean("navBarCollapsed",
					mNavigationBarAdapter.isCollapsed());
		}
		saveDestinationState();
	}

	protected void onStart() {
		super.onStart();
		EsPostsData.setSyncEnabled(false);
	}

	protected void onStop() {
		super.onStop();
		EsPostsData.setSyncEnabled(true);
	}

	public final void onUpButtonClick() {
		if (mHostLayout.isNavigationBarVisible()
				|| mHostLayout.getCurrentHostedFragment() == null
				|| !mHostLayout.getCurrentHostedFragment().onUpButtonClicked())
			mHostLayout.toggleNavigationBarVisibility();
	}
}