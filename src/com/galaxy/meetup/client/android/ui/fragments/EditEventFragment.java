/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.TimeZoneSpinnerAdapter;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;
import com.galaxy.meetup.client.android.ui.fragments.PeopleSearchAdapter.SearchListAdapterListener;
import com.galaxy.meetup.client.android.ui.view.EsImageView.OnImageLoadedListener;
import com.galaxy.meetup.client.android.ui.view.EventThemeView;
import com.galaxy.meetup.client.android.ui.view.TypeableAudienceView;
import com.galaxy.meetup.client.util.EventDateUtils;
import com.galaxy.meetup.client.util.StringUtils;
import com.galaxy.meetup.client.util.TimeZoneHelper;
import com.galaxy.meetup.server.client.util.JsonUtil;
import com.galaxy.meetup.server.client.v2.domain.Event;
import com.galaxy.meetup.server.client.v2.domain.EventTime;
import com.galaxy.meetup.server.client.v2.domain.Location;
import com.galaxy.meetup.server.client.v2.domain.ThemeImage;
import com.galaxy.meetup.server.client.v2.domain.ThemeSpecification;

/**
 * 
 * @author sihai
 *
 */
public class EditEventFragment extends EsFragment implements
		LoaderCallbacks, OnClickListener, OnItemSelectedListener,
		AlertDialogListener, SearchListAdapterListener, OnImageLoadedListener {

	private static final String EVENT_COLUMNS[] = {
        "event_data"
    };
    private static final String THEME_COLUMNS[] = {
        "theme_id", "image_url", "placeholder_path"
    };
    private PeopleSearchListAdapter mAudienceAdapter;
    private TypeableAudienceView mAudienceView;
    private String mAuthKey;
    private boolean mChanged;
    private int mCurrentSpinnerPosition;
    private EditText mDescriptionView;
    private Button mEndDateView;
    private Button mEndTimeView;
    private boolean mError;
    private Event mEvent;
    private TextWatcher mEventDescriptionTextWatcher;
    private String mEventId;
    private boolean mEventLoaded;
    private TextWatcher mEventNameTextWatcher;
    private EditText mEventNameView;
    private Long mEventThemeId;
    private EventThemeView mEventThemeView;
    private String mExternalId;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private OnEditEventListener mListener;
    private TextView mLocationView;
    private boolean mNewEvent;
    private String mOwnerId;
    private Integer mPendingRequestId;
    private AudienceData mResultAudience;
    private final EsServiceListener mServiceListener = new EsServiceListener() {

        public final void onCreateEventComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

        public final void onUpdateEventComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }
    };
    private Button mStartDateView;
    private Button mStartTimeView;
    private ProgressBar mThemeProgressBar;
    private View mThemeSelectionButton;
    private TextView mThemeSelectionTextView;
    private TimeZoneHelper mTimeZoneHelper;
    private Spinner mTimeZoneSpinner;
    private TimeZoneSpinnerAdapter mTimeZoneSpinnerAdapter;

    public EditEventFragment() {
        mNewEvent = true;
        mEventNameTextWatcher = new TextWatcher() {

			public final void afterTextChanged(Editable editable) {
			}

			public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {
			}

			public final void onTextChanged(CharSequence charsequence, int i, int j, int k) {
				String s = mEventNameView.getText().toString().trim();
				if (!TextUtils.equals(mEvent.getName(), s)) {
					mEvent.setName(s);
					mChanged = true;
				}
			}
        };
        mEventDescriptionTextWatcher = new TextWatcher() {

			public final void afterTextChanged(Editable editable) {
			}

			public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {
			}

			public final void onTextChanged(CharSequence charsequence, int i, int j, int k) {
				String s = mDescriptionView.getText().toString().trim();
				if (!TextUtils.equals(mEvent.getDescription(), s)) {
					mEvent.setDescription(s);
					mChanged = true;
				}
			}
        };
    }

    private void bindEndDate() {
        if(null != mEvent.getEndTime())
            mEndDateView.setText(EventDateUtils.getSingleDateDisplayLine(getActivity(), mEvent.getEndTime().getTimeMs(), getTimeZone(mEvent.getEndTime())));
        else
            mEndDateView.setText(null);
    }

    private void bindEndTime() {
        if(null != mEvent.getEndTime() && getActivity() != null)
            mEndTimeView.setText(EventDateUtils.getDisplayTime(getActivity(), mEvent.getEndTime().getTimeMs(), getTimeZone(mEvent.getEndTime())));
        else
            mEndTimeView.setText(null);
    }
    
    private void bindStartDate() {
    	if(null != mEvent.getStartTime())
    		mStartDateView.setText(EventDateUtils.getSingleDateDisplayLine(getActivity(), mEvent.getStartTime().getTimeMs(), getTimeZone(mEvent.getStartTime())));
        else
        	mStartDateView.setText(null);
    }

    private void bindStartTime() {
        if(null != mEvent.getStartTime() && getActivity() != null) {
            mStartTimeView.setText(EventDateUtils.getDisplayTime(getActivity(), mEvent.getStartTime().getTimeMs(), getTimeZone(mEvent.getStartTime())));
    
        } else {
        	mStartTimeView.setText(null);
        }
    }

    private void bindEvent() {
        if(mEvent != null) {
            TypeableAudienceView typeableaudienceview = mAudienceView;
            int i;
            if(mNewEvent)
                i = 0;
            else
                i = 8;
            typeableaudienceview.setVisibility(i);
            mEventNameView.setText(mEvent.getName());
            mDescriptionView.setText(mEvent.getDescription());
            bindStartDate();
            bindEndDate();
            bindTimeZoneSpinner();
            bindStartTime();
            bindEndTime();
            bindLocation();
        }
    }

    private void bindLocation() {
        if(null != mEvent.getLocation())
            mLocationView.setText(mEvent.getLocation().buildAddress());
        else
            mLocationView.setText(null);
    }

    private void bindTimeZoneSpinner() {
        if(null != mEvent.getStartTime())
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mEvent.getStartTime().getTimeMs());
            TimeZoneHelper timezonehelper = mTimeZoneHelper;
            getActivity();
            timezonehelper.configure(calendar);
            mTimeZoneSpinnerAdapter.setTimeZoneHelper(mTimeZoneHelper);
            mCurrentSpinnerPosition = mTimeZoneHelper.getTimeZonePos(mEvent.getStartTime().getTimezone(), null);
            mTimeZoneSpinner.setSelection(mCurrentSpinnerPosition);
        }
    }

    private void clearEndTime() {
        mEvent.setEndTime(null);
    }

    private void enableEventPicker() {
        mHandler.post(new Runnable() {

            public final void run() {
                mThemeSelectionButton.setVisibility(0);
                mThemeSelectionTextView.setVisibility(0);
                mThemeProgressBar.setVisibility(8);
                mThemeSelectionButton.setLayoutParams(new android.widget.FrameLayout.LayoutParams(mEventThemeView.getMeasuredWidth(), mEventThemeView.getMeasuredHeight()));
            }
        });
    }

    private EsAccount getAccount() {
        return (EsAccount)getActivity().getIntent().getExtras().get("account");
    }

    private static long getDefaultEventTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(12, 90);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    private TimeZone getTimeZone(EventTime eventtime) {
        TimeZone timezone;
        if(eventtime != null)
            timezone = mTimeZoneHelper.getTimeZone(eventtime.timezone, null);
        else
            timezone = mTimeZoneHelper.getCurrentTimeZoneInfo().getTimeZone();
        return timezone;
    }

    private boolean isEmptyAudience() {
        AudienceData audiencedata = mAudienceView.getAudience();
        boolean flag;
        if(audiencedata.getCircleCount() + audiencedata.getUserCount() == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

	private void onAudienceChanged() {
		if (mListener != null) {
			OnEditEventListener _tmp = mListener;
		}
	}

    private void recordUserAction(OzActions ozactions) {
        FragmentActivity fragmentactivity = getActivity();
        EsAccount esaccount = getAccount();
        if(esaccount != null)
            EsAnalytics.recordActionEvent(fragmentactivity, esaccount, ozactions, OzViews.getViewForLogging(fragmentactivity));
    }

    private void setEndTime(Calendar calendar) {
        long l = calendar.getTimeInMillis();
        TimeZone timezone = calendar.getTimeZone();
        if(null == mEvent.getEndTime()) {
        	EventTime endTime = new EventTime();
        	endTime.setTimeMs(Long.valueOf(getDefaultEventTime()));
        	mEvent.setEndTime(endTime);
        }
        if(mEvent.getEndTime().getTimeMs().longValue() != l) {
        	mEvent.getEndTime().setTimeMs(Long.valueOf(l));
            mEvent.getEndTime().setTimezone(timezone.getID());
            mChanged = true;
        }
    }

    private void setEventTheme(long themeId, String imageURL, Uri imageURI, boolean flag) {
    	if(null == mEvent) {
    		return;
    	}
    	
    	if(null == mEvent.getThemeSpecification()) {
            mEvent.setThemeSpecification(new ThemeSpecification());
    	}
        if(flag || null == mEvent.getThemeSpecification().getThemeId()) {
            mEventThemeId = themeId;
            mEvent.getThemeSpecification().setThemeId(themeId);
            if(null != imageURI)
            {
                mEventThemeView.setDefaultImageUri(imageURI);
                enableEventPicker();
            }
            mEventThemeView.setImageUrl(imageURL);
        } else if(mEvent.getThemeSpecification().getThemeId().longValue() == themeId) {
            mEventThemeId = themeId;
            if(null != imageURI) {
                mEventThemeView.setDefaultImageUri(imageURI);
                enableEventPicker();
            }
            mEventThemeView.setImageUrl(imageURL);
        }
    }

    private void setStartTime(Calendar calendar) {
        long l = calendar.getTimeInMillis();
        TimeZone timezone = calendar.getTimeZone();
        boolean flag;
        if(mEvent.getStartTime().getTimezone() != null)
            flag = true;
        else
            flag = false;
        if(mEvent.getStartTime().getTimeMs().longValue() != l || !flag) {
            mEvent.getStartTime().setTimeMs(l);
            mEvent.getStartTime().setTimezone(timezone.getID());
            bindTimeZoneSpinner();
            mChanged = true;
        }
    }

    private void updateView(View view) {
        if(view != null && !mNewEvent) {
            TextView textview = (TextView)view.findViewById(R.id.server_error);
            View view1 = view.findViewById(R.id.content);
            if(mEvent != null) {
                textview.setVisibility(8);
                view1.setVisibility(0);
                showContent(view);
            } else if(!mEventLoaded) {
                view1.setVisibility(8);
                textview.setVisibility(8);
                showEmptyViewProgress(view);
            } else if(mError) {
                textview.setVisibility(0);
                textview.setText(R.string.event_details_error);
                view1.setVisibility(8);
                showContent(view);
            } else {
                textview.setVisibility(0);
                textview.setText(R.string.event_does_not_exist);
                view1.setVisibility(8);
                showContent(view);
            }
        }
    }

    public final void createEvent() {
        if(mEvent == null) {
            mEvent = new Event();
            mEvent.setPublisher(this.getAccount().getName());
            EventTime startTime = new EventTime();
            startTime.setTimeMs(getDefaultEventTime());
            TimeZoneHelper.TimeZoneInfo timezoneinfo = mTimeZoneHelper.getCurrentTimeZoneInfo();
            TimeZone timezone = timezoneinfo.getTimeZone();
            startTime.setTimezone(timezone.getID());
            mEvent.setStartTime(startTime);
            mExternalId = (new StringBuilder()).append(System.currentTimeMillis()).append(".").append(StringUtils.randomString(32)).toString();
            mEventThemeId = -1L;
        }
    }

    public final void editEvent(String eventId, String ownerName, String authKey) {
        mEventId = eventId;
        mOwnerId = ownerName;
        mAuthKey = authKey;
        mEventThemeId = -1L;
        mNewEvent = false;
    }

    protected final void handleServiceCallback(int requestId, ServiceResult serviceresult) {
    	
    	if(null == mPendingRequestId || requestId != mPendingRequestId.intValue()) {
    		return;
    	}
    	
    	DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
        if(dialogfragment != null)
            dialogfragment.dismiss();
        mPendingRequestId = null;
        FragmentActivity fragmentactivity = getActivity();
        int resourceId;
        if(serviceresult != null && serviceresult.hasError()) {
            if(mNewEvent)
                resourceId = R.string.create_event_server_error;
            else
                resourceId = R.string.transient_server_error;
            Toast.makeText(fragmentactivity, resourceId, 0).show();
        } else if(mListener != null) {
            if(mNewEvent)
            	resourceId = R.string.event_create_successful;
            else
            	resourceId = R.string.event_save_successful;
            Toast.makeText(fragmentactivity, resourceId, 0).show();
            mListener.onEventSaved();
        }
    }

    protected final boolean isEmpty() {
        boolean flag;
        if(mEvent != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onActivityResult(int i, int j, Intent intent) {
        super.onActivityResult(i, j, intent);
        if(-1 != j || null == intent) {
        	return;
        }
        
        switch(i)
        {
        case 0: // '\0'
            byte[] bytes = intent.getByteArrayExtra("location");
            if(null == bytes)
                mEvent.setLocation(null);
            else
                mEvent.setLocation((Location)JsonUtil.fromByteArray(bytes, Location.class));
            bindLocation();
            break;

        case 1: // '\001'
            int k = intent.getIntExtra("theme_id", -1);
            String s = intent.getStringExtra("theme_url");
            if(k != -1 && s != null)
            {
                setEventTheme(k, s, null, true);
                getLoaderManager().restartLoader(0, null, this);
            }
            break;

        case 2: // '\002'
            mResultAudience = (AudienceData)intent.getParcelableExtra("audience");
            break;
        }
        
    }

    public final void onAddPersonToCirclesAction(String s, String s1, boolean flag)
    {
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mTimeZoneHelper = new TimeZoneHelper(getActivity().getApplicationContext());
        mTimeZoneHelper.configure(Calendar.getInstance());
    }

    public final void onChangeCirclesAction(String s, String s1)
    {
    }

    public final void onCircleSelected(String s, CircleData circledata)
    {
        mAudienceView.addCircle(circledata);
        mAudienceView.clearText();
    }

    public void onClick(View view) {
        int viewId = view.getId();
        
        if(R.id.edit_audience == viewId) {
        	recordUserAction(OzActions.COMPOSE_CHANGE_ACL);
            startActivityForResult(Intents.getEditAudienceActivityIntent(getActivity(), getAccount(), getString(R.string.event_invite_activity_title), mAudienceView.getAudience(), 11, false, false, true, false), 2);
            return;
        } else if(viewId == R.id.start_date) {
            DatePickerFragmentDialog datepickerfragmentdialog = new DatePickerFragmentDialog(1);
            datepickerfragmentdialog.setTargetFragment(this, 0);
            Bundle bundle = new Bundle();
            bundle.putLong("date_time", mEvent.getStartTime().getTimeMs());
            bundle.putString("time_zone", mEvent.getStartTime().getTimezone());
            datepickerfragmentdialog.setArguments(bundle);
            datepickerfragmentdialog.show(getFragmentManager(), "date");
        } else if(viewId == R.id.end_date) {
            DatePickerFragmentDialog datepickerfragmentdialog1 = new DatePickerFragmentDialog(0);
            datepickerfragmentdialog1.setTargetFragment(this, 0);
            Bundle bundle = new Bundle();
            if(null != mEvent.getEndTime())
            	bundle.putLong("date_time", mEvent.getEndTime().getTimeMs());
            else
            	bundle.putLong("date_time", mEvent.getStartTime().getTimeMs());
            bundle.putString("time_zone", mEvent.getStartTime().getTimezone());
            datepickerfragmentdialog1.setArguments(bundle);
            datepickerfragmentdialog1.show(getFragmentManager(), "date");
        } else if(viewId == R.id.start_time) {
            TimePickerFragmentDialog timepickerfragmentdialog = new TimePickerFragmentDialog(1);
            timepickerfragmentdialog.setTargetFragment(this, 0);
            Bundle bundle = new Bundle();
            bundle.putLong("date_time", mEvent.getStartTime().getTimeMs());
            bundle.putString("time_zone", mEvent.getStartTime().getTimezone());
            timepickerfragmentdialog.setArguments(bundle);
            timepickerfragmentdialog.show(getFragmentManager(), "time");
        } else if(viewId == R.id.end_time) {
            TimePickerFragmentDialog timepickerfragmentdialog = new TimePickerFragmentDialog(0);
            timepickerfragmentdialog.setTargetFragment(this, 0);
            Bundle bundle = new Bundle();
            if(null != mEvent.getEndTime())
                bundle.putLong("date_time", mEvent.getEndTime().getTimeMs());
            else
                bundle.putLong("date_time", 0x6ddd00L + mEvent.getStartTime().getTimeMs());
            bundle.putString("time_zone", mEvent.getStartTime().getTimezone());
            timepickerfragmentdialog.setArguments(bundle);
            timepickerfragmentdialog.show(getFragmentManager(), "time");
        } else if(viewId == R.id.location_text) {
            recordUserAction(OzActions.COMPOSE_CHANGE_LOCATION);
            //startActivity(Intents.getChooseLocationIntent(getActivity(), getAccount(), false, null));
            startActivityForResult(Intents.getEventLocationActivityIntent(getActivity(), getAccount(), mEvent.getLocation()), 0);
        } else if(viewId == R.id.select_theme_button) {
            startActivityForResult(Intents.getEventThemePickerIntent(getActivity(), getAccount()), 1);
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if(bundle != null) {
            mNewEvent = bundle.getBoolean("new_event");
            mEventId = bundle.getString("event_id");
            mOwnerId = bundle.getString("owner_id");
            if(bundle.containsKey("event")) {
                byte[] bytes = bundle.getByteArray("event");
                mEvent = (Event)JsonUtil.fromByteArray(bytes, Event.class);
            }
            if(bundle.containsKey("request_id"))
                mPendingRequestId = Integer.valueOf(bundle.getInt("request_id"));
            mExternalId = bundle.getString("external_id");
            mChanged = bundle.getBoolean("changed");
        }
        getLoaderManager().initLoader(0, null, this);
        if(!mNewEvent && mEvent == null)
            getLoaderManager().initLoader(1, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
        final FragmentActivity context = getActivity();
        final EsAccount esaccount = getAccount();
        Loader loader = null;
        if(0 == i) {
        	loader = new EsCursorLoader(context) {

                public final Cursor esLoadInBackground()
                {
                    return EsEventData.getEventTheme(context, esaccount, mEventThemeId, EditEventFragment.THEME_COLUMNS);
                }
            };
        } else if(1 == i) {
        	loader = new EsCursorLoader(context) {

                public final Cursor esLoadInBackground()
                {
                    return EsEventData.retrieveEvent(context, esaccount, mEventId, mAuthKey, EditEventFragment.EVENT_COLUMNS);
                }
            };
        }
        
        return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.edit_event_fragment, viewgroup);
        mEventThemeView = (EventThemeView)view.findViewById(R.id.event_theme_image);
        mEventThemeView.setOnImageLoadedListener(this);
        mEventThemeView.setClickable(true);
        mEventThemeView.setOnClickListener(this);
        mThemeSelectionTextView = (TextView)view.findViewById(R.id.select_theme_text);
        mThemeSelectionTextView.setText(getString(R.string.event_change_theme).toUpperCase());
        mThemeProgressBar = (ProgressBar)view.findViewById(R.id.event_theme_progress_bar);
        mEventNameView = (EditText)view.findViewById(R.id.event_name);
        mEventNameView.addTextChangedListener(mEventNameTextWatcher);
        mStartDateView = (Button)view.findViewById(R.id.start_date);
        mStartDateView.setOnClickListener(this);
        mEndDateView = (Button)view.findViewById(R.id.end_date);
        mEndDateView.setOnClickListener(this);
        mStartTimeView = (Button)view.findViewById(R.id.start_time);
        mStartTimeView.setOnClickListener(this);
        mEndTimeView = (Button)view.findViewById(R.id.end_time);
        mEndTimeView.setOnClickListener(this);
        mLocationView = (TextView)view.findViewById(R.id.location_text);
        mLocationView.setOnClickListener(this);
        mAudienceView = (TypeableAudienceView)view.findViewById(R.id.audience_view);
        mAudienceView.setEmptyAudienceHint(R.string.event_invitees_hint);
        mAudienceView.setAudienceChangedCallback(new Runnable() {

            public final void run()
            {
                onAudienceChanged();
            }

        });
        mThemeSelectionButton = view.findViewById(R.id.select_theme_button);
        mThemeSelectionButton.setOnClickListener(this);
        mDescriptionView = (EditText)view.findViewById(R.id.description);
        mDescriptionView.addTextChangedListener(mEventDescriptionTextWatcher);
        ContextThemeWrapper contextthemewrapper = new ContextThemeWrapper(getActivity(), R.style.CircleBrowserTheme);
        mAudienceAdapter = new PeopleSearchListAdapter(contextthemewrapper, getFragmentManager(), getLoaderManager(), getAccount());
        mAudienceAdapter.setCircleUsageType(11);
        mAudienceAdapter.setShowPersonNameDialog(false);
        mAudienceAdapter.setListener(this);
        mAudienceAdapter.onCreate(bundle);
        mAudienceView.setAutoCompleteAdapter(mAudienceAdapter);
        mAudienceView.setAccount(getAccount());
        view.findViewById(R.id.edit_audience).setOnClickListener(this);
        mTimeZoneSpinnerAdapter = new TimeZoneSpinnerAdapter(contextthemewrapper);
        mTimeZoneSpinnerAdapter.setTimeZoneHelper(mTimeZoneHelper);
        mTimeZoneSpinner = (Spinner)view.findViewById(R.id.time_zone);
        mTimeZoneSpinner.setAdapter(mTimeZoneSpinnerAdapter);
        TimeZoneHelper.TimeZoneInfo timezoneinfo = mTimeZoneHelper.getCurrentTimeZoneInfo();
        int i;
        if(timezoneinfo != null)
            i = timezoneinfo.getPosition();
        else
            i = -1;
        mCurrentSpinnerPosition = i;
        mTimeZoneSpinner.setSelection(mCurrentSpinnerPosition);
        mTimeZoneSpinner.setOnItemSelectedListener(this);
        bindEvent();
        updateView(view);
        return view;
    }

    public final void onDialogCanceled(String s)
    {
    }

    public final void onDialogListClick(int i, Bundle bundle)
    {
    }

    public final void onDialogNegativeClick(String s)
    {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s) {
        if("quit".equals(s) && mListener != null)
            mListener.onEventClosed();
    }

    public final void onDiscard() {
        if(!mNewEvent) {
        	if(mChanged) {
                AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.edit_event_quit_title), getString(R.string.edit_event_quit_question), getString(R.string.yes), getString(R.string.no));
                alertfragmentdialog.setTargetFragment(this, 0);
                alertfragmentdialog.show(getFragmentManager(), "quit");
            } else if(mListener != null) {
                mListener.onEventClosed();
            }
        } else { 
        	boolean flag;
            if(!TextUtils.isEmpty(mEvent.getName()) || !TextUtils.isEmpty(mEvent.getDescription()) || !isEmptyAudience())
                flag = true;
            else
                flag = false;
            if(!flag) { 
            	if(mListener != null)
                    mListener.onEventClosed();
            	return;
            } else { 
            	AlertFragmentDialog alertfragmentdialog1 = AlertFragmentDialog.newInstance(getString(R.string.new_event_quit_title), getString(R.string.new_event_quit_question), getString(R.string.yes), getString(R.string.no));
                alertfragmentdialog1.setTargetFragment(this, 0);
                alertfragmentdialog1.show(getFragmentManager(), "quit");
            }
        }
    }

    public final void onDismissSuggestionAction(String s, String s1) {
    }

    public final void onEndDateCleared() {
        clearEndTime();
        bindEndDate();
        bindEndTime();
    }

    public final void onEndDateSet(int i, int j, int k) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(((TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem()).getTimeZone());
        if(null != mEvent.getEndTime())
            calendar.setTimeInMillis(mEvent.getEndTime().getTimeMs());
        else
            calendar.setTimeInMillis(0x6ddd00L + mEvent.getStartTime().getTimeMs());
        if(null == mEvent.getEndTime() || calendar.get(1) != i || calendar.get(2) != j || calendar.get(5) != k) {
            calendar.set(i, j, k);
            long l = calendar.getTimeInMillis();
            if(mEvent.getStartTime().getTimeMs().longValue() > l)
                mEvent.getStartTime().getTimeMs().longValue();
            setEndTime(calendar);
            bindEndDate();
            bindEndTime();
        }
    }

    public final void onEndTimeCleared() {
        clearEndTime();
        bindEndTime();
        bindEndDate();
    }

    public final void onEndTimeSet(int i, int j) {
        Calendar calendar = Calendar.getInstance();
        if(null != mEvent.getEndTime())
            calendar.setTimeInMillis(mEvent.getEndTime().getTimeMs());
        else
            calendar.setTimeInMillis(0x6ddd00L + mEvent.getStartTime().getTimeMs());
        if(null == mEvent.getEndTime() || calendar.get(11) != i || calendar.get(12) != j) {
            calendar.set(11, i);
            calendar.set(12, j);
            calendar.setTimeZone(((TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem()).getTimeZone());
            long l = calendar.getTimeInMillis();
            if(mEvent.getStartTime().getTimeMs().longValue() > l)
                mEvent.getStartTime().getTimeMs().longValue();
            setEndTime(calendar);
            bindEndTime();
            bindEndDate();
        }
    }

    public final void onImageLoaded() {
        enableEventPicker();
    }

    public void onItemSelected(AdapterView adapterview, View view, int position, long l) {
        if(position != mCurrentSpinnerPosition) {
            TimeZoneHelper.TimeZoneInfo timezoneinfo = (TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem();
            long l1 = timezoneinfo.getOffset();
            long l2 = mTimeZoneHelper.getCurrentTimeZoneInfo().getOffset();
            if(!TextUtils.isEmpty(mEvent.getStartTime().getTimezone())) {
                TimeZone timezone = TimeZoneHelper.getSystemTimeZone(mEvent.getStartTime().getTimezone());
                l2 = mTimeZoneHelper.getOffset(timezone);
            }
            long l3 = l2 - l1;
            mEvent.getStartTime().setTimezone(timezoneinfo.getTimeZone().getID());
            EventTime eventtime = mEvent.getStartTime();
            eventtime.setTimeMs(Long.valueOf(l3 + eventtime.getTimeMs().longValue()));
            if(null != mEvent.getEndTime() && null != mEvent.getEndTime().getTimeMs()) {
                EventTime eventtime1 = mEvent.getEndTime();
                eventtime1.setTimeMs(Long.valueOf(l3 + eventtime1.getTimeMs().longValue()));
                mEvent.getEndTime().setTimezone(mEvent.getStartTime().getTimezone());
            }
        }
    }

    public final void onLoadFinished(Loader loader, Object obj) {
        Cursor cursor = (Cursor)obj;
        int id = loader.getId();
        if(0 == id) {
        	if(cursor != null && cursor.moveToFirst()) {
                int themeId = cursor.getInt(0);
                String imageURL = cursor.getString(1);
                String s1 = cursor.getString(2);
                Uri uri;
				if (!TextUtils.isEmpty(s1)) {
					android.net.Uri.Builder builder = new android.net.Uri.Builder();
					builder.path(s1);
					uri = builder.build();
				} else {
					uri = null;
				}
                setEventTheme(themeId, imageURL, uri, false);
            } else if(mEvent != null && null != mEvent.getTheme()) {
            	ThemeImage themeimage = EsEventData.getThemeImage(mEvent.getTheme());
                if(themeimage != null)
                    setEventTheme(mEventThemeId, themeimage.getUrl(), null, true);
            }
        } else if(1 == id) {
        	mEventLoaded = true;
        	if(null == cursor) {
        		mError = true;
        	} else {
        		mError = false;
                if(cursor.moveToFirst()) {
                    mEvent = (Event)JsonUtil.fromByteArray(cursor.getBlob(0), Event.class);
                    mAuthKey = mEvent.getAuthKey();
                    Long themeId = -1L;
                    if(null != mEvent.getTheme())
                    	themeId = mEvent.getTheme().getId();
                    if(!themeId.equals(mEventThemeId))
                    {
                        mEventThemeId = themeId;
                        getLoaderManager().restartLoader(0, null, this);
                    }
                    bindEvent();
                }
        	}
        	updateView(getView());
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public void onNothingSelected(AdapterView adapterview)
    {
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public final void onPersonSelected(String s, String s1, PersonData persondata)
    {
        mAudienceView.addPerson(persondata);
        mAudienceView.clearText();
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingRequestId.intValue());
            handleServiceCallback(mPendingRequestId.intValue(), serviceresult);
            mPendingRequestId = null;
        }
        if(mResultAudience != null)
        {
            mAudienceView.replaceAudience(mResultAudience);
            mResultAudience = null;
            onAudienceChanged();
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        mAudienceAdapter.onSaveInstanceState(bundle);
        bundle.putBoolean("new_event", mNewEvent);
        bundle.putString("event_id", mEventId);
        bundle.putString("owner_id", mOwnerId);
        if(mEvent != null)
            bundle.putByteArray("event", JsonUtil.toByteArray(mEvent));
        if(mPendingRequestId != null)
            bundle.putInt("request_id", mPendingRequestId.intValue());
        bundle.putString("external_id", mExternalId);
        bundle.putBoolean("changed", mChanged);
    }

    public final void onSearchListAdapterStateChange(PeopleSearchAdapter peoplesearchadapter)
    {
    }

    public final void onStart()
    {
        super.onStart();
        if(mAudienceAdapter != null)
            mAudienceAdapter.onStart();
    }

    public final void onStartDateSet(int i, int j, int k) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(((TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem()).getTimeZone());
        calendar.setTimeInMillis(mEvent.getStartTime().getTimeMs());
        if(calendar.get(1) != i || calendar.get(2) != j || calendar.get(5) != k) {
            calendar.set(i, j, k);
            setStartTime(calendar);
            bindStartDate();
            bindStartTime();
            if(null != mEvent.getEndTime() && mEvent.getEndTime().getTimeMs().longValue() < calendar.getTimeInMillis()) {
                calendar.add(13, 7200);
                setEndTime(calendar);
                bindEndDate();
                bindEndTime();
            }
        }
    }

    public final void onStartTimeSet(int i, int j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(((TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem()).getTimeZone());
        calendar.setTimeInMillis(mEvent.getStartTime().getTimeMs().longValue());
        if(calendar.get(11) != i || calendar.get(12) != j) {
            calendar.set(11, i);
            calendar.set(12, j);
            long l = calendar.getTimeInMillis();
            setStartTime(calendar);
            bindStartTime();
            if(null != mEvent.getEndTime() && mEvent.getEndTime().getTimeMs().longValue() < l)
            {
                calendar.add(13, 7200);
                setEndTime(calendar);
                bindEndDate();
                bindEndTime();
            }
        }
    }

    public final void onStop() {
        super.onStop();
        if(mAudienceAdapter != null)
            mAudienceAdapter.onStop();
    }

	public final void onUnblockPersonAction(String s, boolean flag) {
	}

    /**
     * 保存
     */
	public final void save() {
        boolean flag = true;
        if(mEvent == null)
            flag = false;
        else if(TextUtils.isEmpty(mEvent.getName())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.event_no_title_hint), 0).show();
            flag = false;
        } else if(mNewEvent) {
            /*if(isEmptyAudience()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.event_no_audience_hint), 0).show();
                flag = false;
            }*/
        }
        if(flag)
        {
            ProgressFragmentDialog.newInstance(null, getString(R.string.event_update_operation_pending), false).show(getFragmentManager(), "req_pending");
            if(mNewEvent)
                mPendingRequestId = Integer.valueOf(EsService.createEvent(getActivity(), getAccount(), mEvent, mAudienceView.getAudience(), mExternalId));
            else
                mPendingRequestId = Integer.valueOf(EsService.updateEvent(getActivity(), getAccount(), mEvent));
        }
    }

    public final void setOnEventChangedListener(OnEditEventListener onediteventlistener)
    {
        mListener = onediteventlistener;
    }
    
    
    //==================================================================================================================
    //								Inner class
    //==================================================================================================================
    public static class DatePickerFragmentDialog extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener, android.content.DialogInterface.OnClickListener {
    	
    	private boolean mCancelled;
        private int mType;

        public DatePickerFragmentDialog()
        {
            mType = -1;
        }

        public DatePickerFragmentDialog(int i)
        {
            mType = -1;
            mType = i;
        }

	    public void onClick(DialogInterface dialoginterface, int i)
	    {
	        EditEventFragment editeventfragment = (EditEventFragment)getTargetFragment();
	        if(-2 == i) {
	        	editeventfragment.onEndDateCleared();
		        mCancelled = true;
	        }
	    }
	
	    public final Dialog onCreateDialog(Bundle bundle)
	    {
	        if(mType == -1)
	        {
	            mType = bundle.getInt("type");
	            mCancelled = bundle.getBoolean("cancelled", mCancelled);
	        }
	        long l = getArguments().getLong("date_time");
	        TimeZone timezone = TimeZoneHelper.getSystemTimeZone(getArguments().getString("time_zone"));
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTimeZone(timezone);
	        calendar.setTimeInMillis(l);
	        DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(), this, calendar.get(1), calendar.get(2), calendar.get(5));
	        if(mType == 0)
	            datepickerdialog.setButton(-2, getString(R.string.clear), this);
	        return datepickerdialog;
	    }
	
	    public void onDateSet(DatePicker datepicker, int i, int j, int k)
	    {
	        if(!mCancelled)
	            if(mType == 1)
	                ((EditEventFragment)getTargetFragment()).onStartDateSet(i, j, k);
	            else
	                ((EditEventFragment)getTargetFragment()).onEndDateSet(i, j, k);
	    }
	
	    public final void onSaveInstanceState(Bundle bundle)
	    {
	        super.onSaveInstanceState(bundle);
	        bundle.putInt("type", mType);
	        bundle.putBoolean("cancelled", mCancelled);
	    }
    }

	public static interface OnEditEventListener
	{
	
	    public abstract void onEventClosed();
	
	    public abstract void onEventSaved();
	}

	public static class TimePickerFragmentDialog extends DialogFragment implements android.app.TimePickerDialog.OnTimeSetListener, android.content.DialogInterface.OnClickListener {

		private boolean mCancelled;
	    private int mType;
	
	    public TimePickerFragmentDialog()
	    {
	        mType = -1;
	    }
	
	    public TimePickerFragmentDialog(int i)
	    {
	        mType = -1;
	        mType = i;
	    }
	    
	    public void onClick(DialogInterface dialoginterface, int i) {
	        EditEventFragment editeventfragment = (EditEventFragment)getTargetFragment();
	        if(-2 == i) {
	        	editeventfragment.onEndTimeCleared();
		        mCancelled = true;
	        }
	    }

	    public final Dialog onCreateDialog(Bundle bundle)
	    {
	        if(mType == -1)
	        {
	            mType = bundle.getInt("type", -1);
	            mCancelled = bundle.getBoolean("cancelled", mCancelled);
	        }
	        long l = getArguments().getLong("date_time");
	        String s = getArguments().getString("time_zone");
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTimeZone(TimeZoneHelper.getSystemTimeZone(s));
	        calendar.setTimeInMillis(l);
	        TimePickerDialog timepickerdialog = new TimePickerDialog(getActivity(), this, calendar.get(11), calendar.get(12), DateFormat.is24HourFormat(getActivity()));
	        if(mType == 0)
	            timepickerdialog.setButton(-2, getString(R.string.clear), this);
	        return timepickerdialog;
	    }

	    public final void onSaveInstanceState(Bundle bundle) {
	        super.onSaveInstanceState(bundle);
	        bundle.putInt("type", mType);
	        bundle.putBoolean("cancelled", mCancelled);
	    }

	    public void onTimeSet(TimePicker timepicker, int i, int j) {
	        if(!mCancelled)
	        {
	            EditEventFragment editeventfragment = (EditEventFragment)getTargetFragment();
	            if(mType == 1)
	                editeventfragment.onStartTimeSet(i, j);
	            else
	                editeventfragment.onEndTimeSet(i, j);
	        }
	    }
	}
}
