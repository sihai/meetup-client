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
import com.galaxy.meetup.server.client.domain.EventOptions;
import com.galaxy.meetup.server.client.domain.EventTime;
import com.galaxy.meetup.server.client.domain.Place;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.domain.ThemeImage;
import com.galaxy.meetup.server.client.domain.ThemeSpecification;
import com.galaxy.meetup.server.client.util.JsonUtil;

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
    private PlusEvent mEvent;
    private TextWatcher mEventDescriptionTextWatcher;
    private String mEventId;
    private boolean mEventLoaded;
    private TextWatcher mEventNameTextWatcher;
    private EditText mEventNameView;
    private int mEventThemeId;
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

    public EditEventFragment()
    {
        mNewEvent = true;
        mEventNameTextWatcher = new TextWatcher() {

            public final void afterTextChanged(Editable editable)
            {
            }

            public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
                String s = mEventNameView.getText().toString().trim();
                if(!TextUtils.equals(mEvent.name, s))
                {
                    mEvent.name = s;
                    mChanged = true;
                    if(mListener != null)
                    {
                        OnEditEventListener _tmp = mListener;
                    }
                }
            }
        };
        mEventDescriptionTextWatcher = new TextWatcher() {

            public final void afterTextChanged(Editable editable)
            {
            }

            public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
                String s = mDescriptionView.getText().toString().trim();
                if(!TextUtils.equals(mEvent.description, s))
                {
                    mEvent.description = s;
                    mChanged = true;
                    if(mListener != null)
                    {
                        OnEditEventListener _tmp = mListener;
                    }
                }
            }
        };
    }

    private void bindEndDate()
    {
        if(mEvent.endTime != null)
            mEndDateView.setText(EventDateUtils.getSingleDateDisplayLine(getActivity(), mEvent.endTime.timeMs.longValue(), getTimeZone(mEvent.endTime)));
        else
            mEndDateView.setText(null);
    }

    private void bindEndTime()
    {
        if(mEvent.endTime != null && getActivity() != null)
            mEndTimeView.setText(EventDateUtils.getDisplayTime(getActivity(), mEvent.endTime.timeMs.longValue(), getTimeZone(mEvent.endTime)));
        else
            mEndTimeView.setText(null);
    }

    private void bindEvent()
    {
        if(mEvent != null)
        {
            TypeableAudienceView typeableaudienceview = mAudienceView;
            int i;
            if(mNewEvent)
                i = 0;
            else
                i = 8;
            typeableaudienceview.setVisibility(i);
            mEventNameView.setText(mEvent.name);
            mDescriptionView.setText(mEvent.description);
            bindStartDate();
            bindEndDate();
            bindTimeZoneSpinner();
            bindStartTime();
            bindEndTime();
            bindLocation();
        }
    }

    private void bindLocation()
    {
        Place place = mEvent.location;
        if(place != null)
            mLocationView.setText(place.name);
        else
            mLocationView.setText(null);
    }

    private void bindStartDate()
    {
        mStartDateView.setText(EventDateUtils.getSingleDateDisplayLine(getActivity(), mEvent.startTime.timeMs.longValue(), getTimeZone(mEvent.startTime)));
    }

    private void bindStartTime()
    {
        if(mEvent.startTime != null && getActivity() != null)
            mStartTimeView.setText(EventDateUtils.getDisplayTime(getActivity(), mEvent.startTime.timeMs.longValue(), getTimeZone(mEvent.startTime)));
    }

    private void bindTimeZoneSpinner()
    {
        if(mEvent.startTime != null)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mEvent.startTime.timeMs.longValue());
            TimeZoneHelper timezonehelper = mTimeZoneHelper;
            getActivity();
            timezonehelper.configure(calendar);
            mTimeZoneSpinnerAdapter.setTimeZoneHelper(mTimeZoneHelper);
            mCurrentSpinnerPosition = mTimeZoneHelper.getTimeZonePos(mEvent.startTime.timezone, null);
            mTimeZoneSpinner.setSelection(mCurrentSpinnerPosition);
        }
    }

    private void clearEndTime()
    {
        mEvent.endTime = null;
    }

    private void enableEventPicker()
    {
        mHandler.post(new Runnable() {

            public final void run()
            {
                mThemeSelectionButton.setVisibility(0);
                mThemeSelectionTextView.setVisibility(0);
                mThemeProgressBar.setVisibility(8);
                mThemeSelectionButton.setLayoutParams(new android.widget.FrameLayout.LayoutParams(mEventThemeView.getMeasuredWidth(), mEventThemeView.getMeasuredHeight()));
            }
        });
    }

    private EsAccount getAccount()
    {
        return (EsAccount)getActivity().getIntent().getExtras().get("account");
    }

    private static long getDefaultEventTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(12, 90);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    private TimeZone getTimeZone(EventTime eventtime)
    {
        TimeZone timezone;
        if(eventtime != null)
            timezone = mTimeZoneHelper.getTimeZone(eventtime.timezone, null);
        else
            timezone = mTimeZoneHelper.getCurrentTimeZoneInfo().getTimeZone();
        return timezone;
    }

    private boolean isEmptyAudience()
    {
        AudienceData audiencedata = mAudienceView.getAudience();
        boolean flag;
        if(audiencedata.getCircleCount() + audiencedata.getUserCount() == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void onAudienceChanged()
    {
        if(mListener != null)
        {
            OnEditEventListener _tmp = mListener;
        }
    }

    private void recordUserAction(OzActions ozactions)
    {
        FragmentActivity fragmentactivity = getActivity();
        EsAccount esaccount = getAccount();
        if(esaccount != null)
            EsAnalytics.recordActionEvent(fragmentactivity, esaccount, ozactions, OzViews.getViewForLogging(fragmentactivity));
    }

    private void setEndTime(Calendar calendar)
    {
        long l = calendar.getTimeInMillis();
        TimeZone timezone = calendar.getTimeZone();
        if(mEvent.endTime == null)
        {
            mEvent.endTime = new EventTime();
            mEvent.endTime.timeMs = Long.valueOf(getDefaultEventTime());
        }
        if(mEvent.endTime.timeMs.longValue() != l)
        {
            mEvent.endTime.timeMs = Long.valueOf(l);
            mEvent.endTime.timezone = timezone.getID();
            mChanged = true;
        }
    }

    private void setEventTheme(int i, String s, Uri uri, boolean flag) {
    	if(null == mEvent) {
    		return;
    	}
    	
    	if(mEvent.themeSpecification == null)
            mEvent.themeSpecification = new ThemeSpecification();
        if(flag || mEvent.themeSpecification.themeId == null)
        {
            mEventThemeId = i;
            mEvent.themeSpecification.themeId = Integer.valueOf(i);
            if(uri != null)
            {
                mEventThemeView.setDefaultImageUri(uri);
                enableEventPicker();
            }
            mEventThemeView.setImageUrl(s);
        } else
        if(mEvent.themeSpecification.themeId.intValue() == i)
        {
            mEventThemeId = i;
            if(uri != null)
            {
                mEventThemeView.setDefaultImageUri(uri);
                enableEventPicker();
            }
            mEventThemeView.setImageUrl(s);
        }
    }

    private void setStartTime(Calendar calendar)
    {
        long l = calendar.getTimeInMillis();
        TimeZone timezone = calendar.getTimeZone();
        boolean flag;
        if(mEvent.startTime.timezone != null)
            flag = true;
        else
            flag = false;
        if(mEvent.startTime.timeMs.longValue() != l || !flag)
        {
            mEvent.startTime.timeMs = Long.valueOf(l);
            mEvent.startTime.timezone = timezone.getID();
            bindTimeZoneSpinner();
            mChanged = true;
        }
    }

    private void updateView(View view)
    {
        if(view != null && !mNewEvent)
        {
            TextView textview = (TextView)view.findViewById(R.id.server_error);
            View view1 = view.findViewById(R.id.content);
            if(mEvent != null)
            {
                textview.setVisibility(8);
                view1.setVisibility(0);
                showContent(view);
            } else
            if(!mEventLoaded)
            {
                view1.setVisibility(8);
                textview.setVisibility(8);
                showEmptyViewProgress(view);
            } else
            if(mError)
            {
                textview.setVisibility(0);
                textview.setText(R.string.event_details_error);
                view1.setVisibility(8);
                showContent(view);
            } else
            {
                textview.setVisibility(0);
                textview.setText(R.string.event_does_not_exist);
                view1.setVisibility(8);
                showContent(view);
            }
        }
    }

    public final void createEvent()
    {
        if(mEvent == null)
        {
            mEvent = new PlusEvent();
            mEvent.eventOptions = new EventOptions();
            mEvent.eventOptions.openEventAcl = Boolean.valueOf(true);
            mEvent.eventOptions.openPhotoAcl = Boolean.valueOf(true);
            mEvent.startTime = new EventTime();
            mEvent.startTime.timeMs = Long.valueOf(getDefaultEventTime());
            TimeZoneHelper.TimeZoneInfo timezoneinfo = mTimeZoneHelper.getCurrentTimeZoneInfo();
            EventTime _tmp = mEvent.startTime;
            TimeZone timezone = timezoneinfo.getTimeZone();
            mEvent.startTime.timezone = timezone.getID();
            mExternalId = (new StringBuilder()).append(System.currentTimeMillis()).append(".").append(StringUtils.randomString(32)).toString();
            mEventThemeId = -1;
        }
    }

    public final void editEvent(String s, String s1, String s2)
    {
        mEventId = s;
        mOwnerId = s1;
        mAuthKey = s2;
        mEventThemeId = -1;
        mNewEvent = false;
    }

    protected final void handleServiceCallback(int i, ServiceResult serviceresult) {
    	
    	if(null == mPendingRequestId || i != mPendingRequestId.intValue()) {
    		return;
    	}
    	
    	DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
        if(dialogfragment != null)
            dialogfragment.dismiss();
        mPendingRequestId = null;
        if(serviceresult != null && serviceresult.hasError())
        {
            FragmentActivity fragmentactivity1 = getActivity();
            int k;
            if(mNewEvent)
                k = R.string.create_event_server_error;
            else
                k = R.string.transient_server_error;
            Toast.makeText(fragmentactivity1, k, 0).show();
        } else
        if(mListener != null)
        {
            FragmentActivity fragmentactivity = getActivity();
            int j;
            if(mNewEvent)
                j = R.string.event_create_successful;
            else
                j = R.string.event_save_successful;
            Toast.makeText(fragmentactivity, j, 0).show();
            mListener.onEventSaved();
        }
    }

    protected final boolean isEmpty()
    {
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
            byte abyte0[] = intent.getByteArrayExtra("location");
            if(abyte0 == null)
                mEvent.location = null;
            else
                mEvent.location = (Place)JsonUtil.fromByteArray(abyte0, Place.class);
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
        TimeZoneHelper timezonehelper = mTimeZoneHelper;
        getActivity().getApplicationContext();
        timezonehelper.configure(Calendar.getInstance());
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
        int i = view.getId();
        if(R.id.edit_audience == i) {
        	recordUserAction(OzActions.COMPOSE_CHANGE_ACL);
            startActivityForResult(Intents.getEditAudienceActivityIntent(getActivity(), getAccount(), getString(R.string.event_invite_activity_title), mAudienceView.getAudience(), 11, false, false, true, false), 2);
            return;
        }
        
        if(i == R.id.start_date)
        {
            DatePickerFragmentDialog datepickerfragmentdialog = new DatePickerFragmentDialog(1);
            datepickerfragmentdialog.setTargetFragment(this, 0);
            Bundle bundle = new Bundle();
            bundle.putLong("date_time", mEvent.startTime.timeMs.longValue());
            bundle.putString("time_zone", mEvent.startTime.timezone);
            datepickerfragmentdialog.setArguments(bundle);
            datepickerfragmentdialog.show(getFragmentManager(), "date");
        } else if(i == R.id.end_date) {
            DatePickerFragmentDialog datepickerfragmentdialog1 = new DatePickerFragmentDialog(0);
            datepickerfragmentdialog1.setTargetFragment(this, 0);
            Bundle bundle1 = new Bundle();
            if(mEvent.endTime != null)
                bundle1.putLong("date_time", mEvent.endTime.timeMs.longValue());
            else
                bundle1.putLong("date_time", mEvent.startTime.timeMs.longValue());
            bundle1.putString("time_zone", mEvent.startTime.timezone);
            datepickerfragmentdialog1.setArguments(bundle1);
            datepickerfragmentdialog1.show(getFragmentManager(), "date");
        } else if(i == R.id.start_time) {
            TimePickerFragmentDialog timepickerfragmentdialog = new TimePickerFragmentDialog(1);
            timepickerfragmentdialog.setTargetFragment(this, 0);
            Bundle bundle2 = new Bundle();
            bundle2.putLong("date_time", mEvent.startTime.timeMs.longValue());
            bundle2.putString("time_zone", mEvent.startTime.timezone);
            timepickerfragmentdialog.setArguments(bundle2);
            timepickerfragmentdialog.show(getFragmentManager(), "time");
        } else if(i == R.id.end_time) {
            TimePickerFragmentDialog timepickerfragmentdialog1 = new TimePickerFragmentDialog(0);
            timepickerfragmentdialog1.setTargetFragment(this, 0);
            Bundle bundle3 = new Bundle();
            if(mEvent.endTime != null)
                bundle3.putLong("date_time", mEvent.endTime.timeMs.longValue());
            else
                bundle3.putLong("date_time", 0x6ddd00L + mEvent.startTime.timeMs.longValue());
            bundle3.putString("time_zone", mEvent.startTime.timezone);
            timepickerfragmentdialog1.setArguments(bundle3);
            timepickerfragmentdialog1.show(getFragmentManager(), "time");
        } else if(i == R.id.location_text) {
            recordUserAction(OzActions.COMPOSE_CHANGE_LOCATION);
            startActivityForResult(Intents.getEventLocationActivityIntent(getActivity(), getAccount(), mEvent.location), 0);
        } else if(i == R.id.select_theme_button) {
            startActivityForResult(Intents.getEventThemePickerIntent(getActivity(), getAccount()), 1);
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mNewEvent = bundle.getBoolean("new_event");
            mEventId = bundle.getString("event_id");
            mOwnerId = bundle.getString("owner_id");
            if(bundle.containsKey("event"))
            {
                byte abyte0[] = bundle.getByteArray("event");
                mEvent = (PlusEvent)JsonUtil.fromByteArray(abyte0, PlusEvent.class);
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

    public final void onDialogPositiveClick(Bundle bundle, String s)
    {
        if("quit".equals(s) && mListener != null)
            mListener.onEventClosed();
    }

    public final void onDiscard()
    {
        if(!mNewEvent) {
        	if(mChanged)
            {
                AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.edit_event_quit_title), getString(R.string.edit_event_quit_question), getString(R.string.yes), getString(R.string.no));
                alertfragmentdialog.setTargetFragment(this, 0);
                alertfragmentdialog.show(getFragmentManager(), "quit");
            } else
            if(mListener != null)
                mListener.onEventClosed();
        } else { 
        	boolean flag;
            if(!TextUtils.isEmpty(mEvent.name) || !TextUtils.isEmpty(mEvent.description) || !isEmptyAudience())
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

    public final void onDismissSuggestionAction(String s, String s1)
    {
    }

    public final void onEndDateCleared()
    {
        clearEndTime();
        bindEndDate();
        bindEndTime();
    }

    public final void onEndDateSet(int i, int j, int k)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(((TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem()).getTimeZone());
        if(mEvent.endTime != null)
            calendar.setTimeInMillis(mEvent.endTime.timeMs.longValue());
        else
            calendar.setTimeInMillis(0x6ddd00L + mEvent.startTime.timeMs.longValue());
        if(mEvent.endTime == null || calendar.get(1) != i || calendar.get(2) != j || calendar.get(5) != k)
        {
            calendar.set(i, j, k);
            long l = calendar.getTimeInMillis();
            if(mEvent.startTime.timeMs.longValue() > l)
                mEvent.startTime.timeMs.longValue();
            setEndTime(calendar);
            bindEndDate();
            bindEndTime();
            OnEditEventListener _tmp = mListener;
        }
    }

    public final void onEndTimeCleared()
    {
        clearEndTime();
        bindEndTime();
        bindEndDate();
    }

    public final void onEndTimeSet(int i, int j)
    {
        Calendar calendar = Calendar.getInstance();
        if(mEvent.endTime != null)
            calendar.setTimeInMillis(mEvent.endTime.timeMs.longValue());
        else
            calendar.setTimeInMillis(0x6ddd00L + mEvent.startTime.timeMs.longValue());
        if(mEvent.endTime == null || calendar.get(11) != i || calendar.get(12) != j)
        {
            calendar.set(11, i);
            calendar.set(12, j);
            calendar.setTimeZone(((TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem()).getTimeZone());
            long l = calendar.getTimeInMillis();
            if(mEvent.startTime.timeMs.longValue() > l)
                mEvent.startTime.timeMs.longValue();
            setEndTime(calendar);
            bindEndTime();
            bindEndDate();
            OnEditEventListener _tmp = mListener;
        }
    }

    public final void onImageLoaded()
    {
        enableEventPicker();
    }

    public void onItemSelected(AdapterView adapterview, View view, int i, long l)
    {
        if(i != mCurrentSpinnerPosition)
        {
            TimeZoneHelper.TimeZoneInfo timezoneinfo = (TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem();
            long l1 = timezoneinfo.getOffset();
            long l2 = mTimeZoneHelper.getCurrentTimeZoneInfo().getOffset();
            if(!TextUtils.isEmpty(mEvent.startTime.timezone))
            {
                TimeZone timezone = TimeZoneHelper.getSystemTimeZone(mEvent.startTime.timezone);
                l2 = mTimeZoneHelper.getOffset(timezone);
            }
            long l3 = l2 - l1;
            mEvent.startTime.timezone = timezoneinfo.getTimeZone().getID();
            EventTime eventtime = mEvent.startTime;
            eventtime.timeMs = Long.valueOf(l3 + eventtime.timeMs.longValue());
            if(mEvent.endTime != null && mEvent.endTime.timeMs != null)
            {
                EventTime eventtime1 = mEvent.endTime;
                eventtime1.timeMs = Long.valueOf(l3 + eventtime1.timeMs.longValue());
                mEvent.endTime.timezone = mEvent.startTime.timezone;
            }
        }
    }

    public final void onLoadFinished(Loader loader, Object obj) {
        Cursor cursor = (Cursor)obj;
        int id = loader.getId();
        ThemeImage themeimage;
        int i;
        if(0 == id) {
        	if(cursor != null && cursor.moveToFirst())
            {
                int j = cursor.getInt(0);
                String s = cursor.getString(1);
                String s1 = cursor.getString(2);
                Uri uri;
                if(!TextUtils.isEmpty(s1))
                {
                    android.net.Uri.Builder builder = new android.net.Uri.Builder();
                    builder.path(s1);
                    uri = builder.build();
                } else
                {
                    uri = null;
                }
                setEventTheme(j, s, uri, false);
            } else
            if(mEvent != null && mEvent.theme != null)
            {
                themeimage = EsEventData.getThemeImage(mEvent.theme);
                if(themeimage != null)
                    setEventTheme(mEventThemeId, themeimage.url, null, true);
            }
        } else if(1 == id) {
        	mEventLoaded = true;
        	if(null == cursor) {
        		mError = true;
        	} else {
        		mError = false;
                if(cursor.moveToFirst())
                {
                    mEvent = (PlusEvent)JsonUtil.fromByteArray(cursor.getBlob(0), PlusEvent.class);
                    mAuthKey = mEvent.authKey;
                    i = -1;
                    if(mEvent.theme != null)
                        i = mEvent.theme.themeId.intValue();
                    if(i != mEventThemeId)
                    {
                        mEventThemeId = i;
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

    public final void onStartDateSet(int i, int j, int k)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(((TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem()).getTimeZone());
        calendar.setTimeInMillis(mEvent.startTime.timeMs.longValue());
        if(calendar.get(1) != i || calendar.get(2) != j || calendar.get(5) != k)
        {
            calendar.set(i, j, k);
            setStartTime(calendar);
            bindStartDate();
            bindStartTime();
            if(mEvent.endTime != null && mEvent.endTime.timeMs.longValue() < calendar.getTimeInMillis())
            {
                calendar.add(13, 7200);
                setEndTime(calendar);
                bindEndDate();
                bindEndTime();
            }
            OnEditEventListener _tmp = mListener;
        }
    }

    public final void onStartTimeSet(int i, int j)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(((TimeZoneHelper.TimeZoneInfo)mTimeZoneSpinner.getSelectedItem()).getTimeZone());
        calendar.setTimeInMillis(mEvent.startTime.timeMs.longValue());
        if(calendar.get(11) != i || calendar.get(12) != j)
        {
            calendar.set(11, i);
            calendar.set(12, j);
            long l = calendar.getTimeInMillis();
            setStartTime(calendar);
            bindStartTime();
            if(mEvent.endTime != null && mEvent.endTime.timeMs.longValue() < l)
            {
                calendar.add(13, 7200);
                setEndTime(calendar);
                bindEndDate();
                bindEndTime();
            }
            OnEditEventListener _tmp = mListener;
        }
    }

    public final void onStop()
    {
        super.onStop();
        if(mAudienceAdapter != null)
            mAudienceAdapter.onStop();
    }

    public final void onUnblockPersonAction(String s, boolean flag)
    {
    }

    /**
     * 保存
     */
	public final void save() {
        boolean flag = true;
        if(mEvent == null)
            flag = false;
        else if(TextUtils.isEmpty(mEvent.name)) {
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

	    public final void onSaveInstanceState(Bundle bundle)
	    {
	        super.onSaveInstanceState(bundle);
	        bundle.putInt("type", mType);
	        bundle.putBoolean("cancelled", mCancelled);
	    }

	    public void onTimeSet(TimePicker timepicker, int i, int j)
	    {
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
