/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import WriteReviewOperation.MediaRef;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbEmotishareMetadata;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData.EventActivity;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.hangout.HangoutActivity;
import com.galaxy.meetup.client.android.hangout.HangoutParticipantListActivity;
import com.galaxy.meetup.client.android.hangout.HangoutRingingActivity;
import com.galaxy.meetup.client.android.hangout.HangoutTile;
import com.galaxy.meetup.client.android.oob.OutOfBoxResponseParcelable;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.service.EventFinishedReceiver;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.activity.AccountSelectionActivity;
import com.galaxy.meetup.client.android.ui.activity.AddedToCircleActivity;
import com.galaxy.meetup.client.android.ui.activity.CirclesMembershipActivity;
import com.galaxy.meetup.client.android.ui.activity.ContactsSyncConfigActivity;
import com.galaxy.meetup.client.android.ui.activity.ConversationActivity;
import com.galaxy.meetup.client.android.ui.activity.EditAudienceActivity;
import com.galaxy.meetup.client.android.ui.activity.EditCommentActivity;
import com.galaxy.meetup.client.android.ui.activity.EditEventActivity;
import com.galaxy.meetup.client.android.ui.activity.EditPostActivity;
import com.galaxy.meetup.client.android.ui.activity.EventLocationActivity;
import com.galaxy.meetup.client.android.ui.activity.EventThemePickerActivity;
import com.galaxy.meetup.client.android.ui.activity.HomeActivity;
import com.galaxy.meetup.client.android.ui.activity.HostAlbumsActivity;
import com.galaxy.meetup.client.android.ui.activity.HostEmotiShareChooserActivity;
import com.galaxy.meetup.client.android.ui.activity.HostEventInviteeListActivity;
import com.galaxy.meetup.client.android.ui.activity.HostPhotosActivity;
import com.galaxy.meetup.client.android.ui.activity.HostSquareSearchActivity;
import com.galaxy.meetup.client.android.ui.activity.HostSquareStreamActivity;
import com.galaxy.meetup.client.android.ui.activity.InvitationActivity;
import com.galaxy.meetup.client.android.ui.activity.LicenseActivity;
import com.galaxy.meetup.client.android.ui.activity.LocalReviewActivity;
import com.galaxy.meetup.client.android.ui.activity.LocationPickerActivity;
import com.galaxy.meetup.client.android.ui.activity.NetworkStatisticsActivity;
import com.galaxy.meetup.client.android.ui.activity.NetworkTransactionsActivity;
import com.galaxy.meetup.client.android.ui.activity.NewConversationActivity;
import com.galaxy.meetup.client.android.ui.activity.NewEventActivity;
import com.galaxy.meetup.client.android.ui.activity.OobContactsSyncActivity;
import com.galaxy.meetup.client.android.ui.activity.OobInstantUploadActivity;
import com.galaxy.meetup.client.android.ui.activity.OobSelectPlusPageActivity;
import com.galaxy.meetup.client.android.ui.activity.OobSuggestedPeopleActivity;
import com.galaxy.meetup.client.android.ui.activity.OutOfBoxActivity;
import com.galaxy.meetup.client.android.ui.activity.PanoramaViewerActivity;
import com.galaxy.meetup.client.android.ui.activity.ParticipantListActivity;
import com.galaxy.meetup.client.android.ui.activity.PeopleSearchActivity;
import com.galaxy.meetup.client.android.ui.activity.PhotoComposeActivity;
import com.galaxy.meetup.client.android.ui.activity.PhotoOneUpActivity;
import com.galaxy.meetup.client.android.ui.activity.PhotoPickerActivity;
import com.galaxy.meetup.client.android.ui.activity.PhotosSelectionActivity;
import com.galaxy.meetup.client.android.ui.activity.PlusOneActivity;
import com.galaxy.meetup.client.android.ui.activity.PostActivity;
import com.galaxy.meetup.client.android.ui.activity.PostSearchActivity;
import com.galaxy.meetup.client.android.ui.activity.PostTextActivity;
import com.galaxy.meetup.client.android.ui.activity.ProfileActivity;
import com.galaxy.meetup.client.android.ui.activity.ProfileEditActivity;
import com.galaxy.meetup.client.android.ui.activity.ReshareActivity;
import com.galaxy.meetup.client.android.ui.activity.SelectSquareCategoryActivity;
import com.galaxy.meetup.client.android.ui.activity.ShareActivity;
import com.galaxy.meetup.client.android.ui.activity.StreamOneUpActivity;
import com.galaxy.meetup.client.android.ui.activity.VideoViewActivity;
import com.galaxy.meetup.client.android.ui.activity.setting.InstantUploadSettingsActivity;
import com.galaxy.meetup.client.android.ui.activity.setting.SettingsActivity;
import com.galaxy.meetup.client.android.ui.fragments.PhotoComposeFragment;
import com.galaxy.meetup.client.android.ui.fragments.PhotoOneUpFragment;
import com.galaxy.meetup.client.android.ui.widget.EsWidgetCameraLauncherActivity;
import com.galaxy.meetup.client.util.MediaStoreUtils;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;
import com.galaxy.meetup.server.client.util.JsonUtil;
import com.galaxy.meetup.server.client.v2.domain.Location;

/**
 * 
 * @author sihai
 *
 */
public class Intents {

	private static final List INTERNAL_URLS = Arrays.asList(new String[] {
	        "youtube.com", "google.com"
	});
	
	public static PhotosIntentBuilder newPhotosActivityIntentBuilder(Context context)
    {
        return new PhotosIntentBuilder(context, HostPhotosActivity.class);
    }
	
	public static boolean isInitialOobIntent(Intent intent)
    {
        OobIntents oobintents = (OobIntents)intent.getParcelableExtra("oob_intents");
        boolean flag;
        if(oobintents == null)
            flag = true;
        else
            flag = oobintents.isInitialIntent();
        return flag;
    }
	
	public static boolean isLastOobIntent(Context context, EsAccount esaccount, AccountSettingsData accountsettingsdata, Intent intent)
    {
        OobIntents oobintents = (OobIntents)intent.getParcelableExtra("oob_intents");
        boolean flag;
        if(oobintents == null)
            flag = true;
        else
            flag = oobintents.isLastIntent(context, esaccount, accountsettingsdata);
        return flag;
    }
	
	public static String makeProfileUrl(String s)
    {
        return (new StringBuilder("#~loop:svt=person&view=stream&pid=")).append(s).toString();
    }
	
	public static boolean isProfileUrl(String s)
    {
        boolean flag;
        if(s.startsWith("#~loop:svt=person&") || s.matches("^https://plus\\.google\\.com/[0-9]*$"))
            flag = true;
        else
            flag = false;
        return flag;
    }
	
	public static boolean isCameraIntentRegistered(Context context)
    {
        Intent intent = getCameraIntentPhoto("camera-photo.jpg");
        boolean flag;
        if(context.getPackageManager().queryIntentActivities(intent, 0x10000).size() > 0)
            flag = true;
        else
            flag = false;
        return flag;
    }
	
	public static Intent getEditCommentActivityIntent(Context context, EsAccount esaccount, String s, String s1, String s2, Long long1, String s3)
    {
        Intent intent = new Intent(context, EditCommentActivity.class);
        intent.putExtra("account", esaccount);
        intent.putExtra("activity_id", s);
        intent.putExtra("comment_id", s1);
        intent.putExtra("comment", s2);
        if(long1 != null)
            intent.putExtra("photo_id", long1);
        if(s3 != null)
            intent.putExtra("gaia_id", s3);
        return intent;
    }
	
	public static Intent getNetworkRequestsIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, NetworkTransactionsActivity.class);
        intent.putExtra("account", esaccount);
        return intent;
    }

    public static Intent getNetworkStatisticsIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, NetworkStatisticsActivity.class);
        intent.putExtra("account", esaccount);
        return intent;
    }
    
    public static Intent getWidgetCameraLauncherActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, EsWidgetCameraLauncherActivity.class);
        intent.putExtra("account", (String)null);
        return intent;
    }
    
    public static Intent getMessengerActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("destination", 4);
        intent.putExtra("account", esaccount);
        return intent;
    }
	
    public static Intent getLicenseActivityIntent(Context context)
    {
        return new Intent(context, LicenseActivity.class);
    }

    
	public static Intent getEventsActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("destination", 2);
        return intent;
    }
	
	public static PendingIntent getEventFinishedIntent(Context context, String s)
    {
        Intent intent = new Intent(EventFinishedReceiver.sIntent);
        if(s != null)
            intent.putExtra("event_id", s);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
	
	public static Intent getNewHangoutActivityIntent(Context context, EsAccount esaccount, boolean flag, AudienceData audiencedata)
    {
        Intent intent = new Intent(context, HangoutActivity.class);
        intent.setAction((new StringBuilder("unique")).append(System.currentTimeMillis()).toString());
        intent.putExtra("account", esaccount);
        intent.putExtra("audience", audiencedata);
        intent.putExtra("hangout_ring_invitees", flag);
        intent.putExtra("hangout_skip_green_room", true);
        return intent;
    }
	
	public static Intent getLocalReviewActivityIntent(Context context, EsAccount esaccount, String s, int i, int j)
    {
        Intent intent = new Intent(context, LocalReviewActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("local_review_type", i);
        intent.putExtra("local_review_index", j);
        return intent;
    }
	
	public static Intent getNotificationsIntent(Context context, EsAccount esaccount, Cursor cursor)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("show_notifications", true);
        if(cursor != null && cursor.moveToFirst())
        {
            ArrayList arraylist = new ArrayList();
            ArrayList arraylist1 = new ArrayList();
            do
            {
                arraylist.add(Integer.valueOf(cursor.getInt(15)));
                arraylist1.add(cursor.getString(2));
            } while(cursor.moveToNext());
            if(!arraylist.isEmpty())
            {
                intent.putIntegerArrayListExtra("notif_types", arraylist);
                intent.putStringArrayListExtra("coalescing_codes", arraylist1);
            }
        }
        return intent;
    }
	
	public static PendingIntent getViewEventActivityNotificationIntent(Context context, EsAccount esaccount, String s, String s1)
    {
        return PendingIntent.getActivity(context, 0, getHostedEventIntent(context, esaccount, s, s1, null), 0x8000000);
    }
	
	public static Intent getFakeConversationActivityIntent(Context context, EsAccount esaccount, Data.Participant participant, boolean flag)
    {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.setAction((new StringBuilder("unique")).append(System.currentTimeMillis()).toString());
        intent.putExtra("account", esaccount);
        intent.putExtra("participant", participant);
        intent.putExtra("is_group", false);
        return intent;
    }
	
	public static Intent getEditPostActivityIntent(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        Intent intent = new Intent(context, EditPostActivity.class);
        intent.setAction("android.intent.action.EDIT");
        intent.putExtra("account", esaccount);
        intent.putExtra("activity_id", s);
        intent.putExtra("content", s1);
        intent.putExtra("reshare", flag);
        return intent;
    }
	
	public static Intent getHomeOobActivityIntent(Context context, EsAccount esaccount, Intent intent, MobileOutOfBoxResponse mobileoutofboxresponse, AccountSettingsData accountsettingsdata)
    {
        Intent intent1 = new Intent(context, HomeActivity.class);
        intent1.setAction("android.intent.action.VIEW");
        intent1.putExtra("account", esaccount);
        intent1.putExtra("run_oob", true);
        if(mobileoutofboxresponse != null)
            intent1.putExtra("network_oob", new OutOfBoxResponseParcelable(mobileoutofboxresponse));
        if(accountsettingsdata != null)
            intent1.putExtra("plus_pages", accountsettingsdata);
        if(intent != null)
            intent1.putExtra("intent", intent);
        return intent1;
    }
	
	public static Intent getEditEventActivityIntent(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = new Intent(context, EditEventActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("auth_key", s1);
        return intent;
    }
	
	public static Intent getEventInviteeListActivityIntent(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        Intent intent = new Intent(context, HostEventInviteeListActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("owner_id", s2);
        intent.putExtra("auth_key", s1);
        return intent;
    }
	
	public static Intent getOobIntent(Context context, EsAccount esaccount, MobileOutOfBoxResponse mobileoutofboxresponse, AccountSettingsData accountsettingsdata, String s) {
        return OobIntents.getInitialIntent(context, esaccount, mobileoutofboxresponse, accountsettingsdata, s);
    }
	
	public static Intent getAccountsActivityIntent(Context context, Intent intent) {
        Intent intent1 = new Intent(context, AccountSelectionActivity.class);
        intent1.setAction("android.intent.action.VIEW");
        if(intent != null)
            intent1.putExtra("intent", intent);
        return intent1;
    }
	
	public static Intent getHostNavigationActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        return intent;
    }
	
	public static Intent getStreamActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("destination", 0);
        return intent;
    }
	
	public static Intent getStreamOneUpActivityIntent(Context context, EsAccount esaccount, String s)
    {
        Intent intent = new Intent(context, StreamOneUpActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("activity_id", s);
        intent.putExtra("refresh", false);
        return intent;
    }
	
	public static Intent getCirclePostsActivityIntent(Context context, EsAccount esaccount, String s)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("destination", 0);
        intent.putExtra("circle_id", s);
        return intent;
    }
	
	public static Intent getPostActivityIntent(Context context, EsAccount esaccount, ArrayList arraylist)
    {
        return getPostActivityIntent(context, esaccount, arraylist, null);
    }
	
	public static Intent getContactsSyncConfigActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, ContactsSyncConfigActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        return intent;
    }

    public static Intent getPostActivityIntent(Context context, EsAccount esaccount, ArrayList arraylist, AudienceData audiencedata)
    {
        Intent intent = new Intent(context, PostActivity.class);
        intent.setAction("android.intent.action.SEND_MULTIPLE");
        if(arraylist != null && !arraylist.isEmpty())
            intent.putExtra("android.intent.extra.STREAM", arraylist);
        intent.putExtra("account", esaccount);
        intent.putExtra("is_internal", true);
        if(audiencedata != null)
            intent.putExtra("audience", audiencedata);
        return intent;
    }
    
    public static Intent getChooseEmotiShareObjectIntent(Context context, EsAccount esaccount, DbEmotishareMetadata dbemotisharemetadata)
    {
        Intent intent = new Intent(context, HostEmotiShareChooserActivity.class);
        intent.putExtra("account", esaccount);
        if(dbemotisharemetadata != null)
            intent.putExtra("typed_image_embed", dbemotisharemetadata);
        intent.setAction("android.intent.action.PICK");
        return intent;
    }
    
    public static Intent getChooseLocationIntent(Context context, EsAccount esaccount, boolean flag, DbLocation dblocation)
    {
        Intent intent = new Intent(context, LocationPickerActivity.class);
        intent.setAction("android.intent.action.PICK");
        intent.putExtra("account", esaccount);
        intent.putExtra("places_only", flag);
        if(dblocation != null)
            intent.putExtra("location", dblocation);
        return intent;
    }
    
    public static Intent getEditAudienceActivityIntent(Context context, EsAccount esaccount, String s, AudienceData audiencedata, int i, boolean flag, boolean flag1, boolean flag2, 
            boolean flag3)
    {
        return getEditAudienceActivityIntent(context, esaccount, s, audiencedata, i, flag, flag1, flag2, flag3, false);
    }

    public static Intent getEditAudienceActivityIntent(Context context, EsAccount esaccount, String s, AudienceData audiencedata, int i, boolean flag, boolean flag1, boolean flag2, 
            boolean flag3, boolean flag4)
    {
        Intent intent = new Intent(context, EditAudienceActivity.class);
        intent.putExtra("account", esaccount);
        intent.putExtra("title", s);
        intent.putExtra("audience", audiencedata);
        intent.putExtra("circle_usage_type", i);
        intent.putExtra("search_phones_enabled", flag);
        intent.putExtra("search_plus_pages_enabled", flag1);
        intent.putExtra("search_pub_profiles_enabled", flag2);
        intent.putExtra("filter_null_gaia_ids", flag3);
        intent.putExtra("audience_is_read_only", flag4);
        return intent;
    }
    
    public static Intent getPostCommentsActivityIntent(Context context, EsAccount esaccount, String s)
    {
        Intent intent = new Intent(context, StreamOneUpActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("activity_id", s);
        intent.putExtra("refresh", false);
        return intent;
    }

    public static Intent getPostCommentsActivityIntent(Context context, EsAccount esaccount, String s, String s1, int i, boolean flag, boolean flag1)
    {
        Intent intent = new Intent(context, StreamOneUpActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("activity_id", s);
        intent.putExtra("com.google.plus.analytics.intent.extra.FROM_NOTIFICATION", true);
        if(s1 != null)
            intent.putExtra("notif_id", s1);
        intent.putExtra("notif_category", i);
        intent.putExtra("refresh", true);
        intent.putExtra("enable_comment_action", flag1);
        return intent;
    }
    
    public static Intent getCreateEventActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, NewEventActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        return intent;
    }
    
    public static Intent getEventLocationActivityIntent(Context context, EsAccount esaccount, Location location)
    {
        Intent intent = new Intent(context, EventLocationActivity.class);
        intent.setAction("android.intent.action.PICK");
        intent.putExtra("account", esaccount);
        if(location != null)
            intent.putExtra("location", JsonUtil.toByteArray(location));
        return intent;
    }
    
    public static Intent getEventThemePickerIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, EventThemePickerActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        return intent;
    }
    
    public static Intent getHostedEventIntent(Context context, EsAccount esaccount, String s, int i, String s1, String s2, String s3, String s4)
    {
        return getHostedEventIntent(context, esaccount, s, i, s1, null, s3, null, null);
    }

    private static Intent getHostedEventIntent(Context context, EsAccount esaccount, String s, int i, String s1, String s2, String s3, String s4, 
            String s5)
    {
        Intent intent = new Intent(context, EventActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("event_id", s);
        intent.putExtra("account", esaccount);
        intent.putExtra("owner_id", s1);
        intent.putExtra("invitation_token", s2);
        intent.putExtra("auth_key", s5);
        intent.putExtra("notif_type", i);
        intent.putExtra("notif_id", s3);
        intent.putExtra("rsvp", s4);
        return intent;
    }

    public static Intent getHostedEventIntent(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        return getHostedEventIntent(context, esaccount, s, s1, null, null, null);
    }

    public static Intent getHostedEventIntent(Context context, EsAccount esaccount, String s, String s1, String s2, String s3, String s4)
    {
        Intent intent = getHostedEventIntent(context, esaccount, s, 0, s1, s2, null, s3, s4);
        intent.addFlags(0x4000000);
        return intent;
    }
    
    public static Intent getProfileActivityByGaiaIdIntent(Context context, EsAccount esaccount, String s, String s1)
    {
        return getProfileActivityIntent(context, esaccount, (new StringBuilder("g:")).append(s).toString(), s1, 0);
    }

    public static Intent getProfileActivityIntent(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("notif_id", (String)null);
        return intent;
    }

    public static Intent getProfileActivityIntent(Context context, EsAccount esaccount, String s, String s1, int i)
    {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("notif_id", s1);
        intent.putExtra("profile_view_type", i);
        return intent;
    }

    public static Intent getProfileEditActivityIntent(Context context, EsAccount esaccount, int i, String s, String s1)
    {
        Intent intent = new Intent(context, ProfileEditActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("profile_edit_mode", i);
        intent.putExtra("profile_edit_items_json", s);
        intent.putExtra("profile_edit_roster_json", s1);
        return intent;
    }

    public static Intent getProfilePhotosActivityIntent(Context context, EsAccount esaccount, String s)
    {
        Intent intent;
        if(esaccount.isMyGaiaId(EsPeopleData.extractGaiaId(s)))
            intent = getHostedProfileAlbumsIntent(context, esaccount, s, null);
        else
            intent = getProfileActivityIntent(context, esaccount, s, null, 1);
        return intent;
    }
    
    public static Intent getHostedProfileAlbumsIntent(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("destination", 7);
        intent.putExtra("account", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("notif_id", (String)null);
        intent.putExtra("photos_home", esaccount.isMyGaiaId(EsPeopleData.extractGaiaId(s)));
        return intent;
    }

    public static Intent getReshareActivityIntent(Context context, EsAccount esaccount, String s, boolean flag)
    {
        Intent intent = new Intent(context, ReshareActivity.class);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("account", esaccount);
        intent.putExtra("activity_id", s);
        intent.putExtra("limited", flag);
        return intent;
    }

    public static Intent getSelectSquareCategoryActivityIntent(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        Intent intent = new Intent(context, SelectSquareCategoryActivity.class);
        intent.putExtra("account", esaccount);
        intent.putExtra("title", s);
        intent.putExtra("square_id", s1);
        intent.putExtra("square_name", s2);
        return intent;
    }

    public static Intent getSettingsActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        return intent;
    }

    public static Intent getSquareSearchActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, HostSquareSearchActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        return intent;
    }

    public static Intent getSquareStreamActivityIntent(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        Intent intent = new Intent(context, HostSquareStreamActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("square_id", s);
        intent.putExtra("stream_id", s1);
        intent.putExtra("notif_id", s2);
        return intent;
    }

    public static Intent getSquaresActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("destination", 8);
        return intent;
    }

    public static Intent getSuggestedPeopleActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("account", esaccount);
        intent.putExtra("destination", 5);
        intent.putExtra("people_view_type", 0);
        return intent;
    }
    
    public static Intent getHangoutActivityAudienceIntent(Context context, EsAccount esaccount, Hangout.Info info, boolean flag, AudienceData audiencedata)
    {
        Intent intent = new Intent(context, HangoutActivity.class);
        intent.setAction((new StringBuilder("unique")).append(System.currentTimeMillis()).toString());
        intent.putExtra("account", esaccount);
        intent.putExtra("hangout_info", info);
        intent.putExtra("hangout_skip_green_room", flag);
        if(audiencedata != null)
            intent.putExtra("audience", audiencedata);
        return intent;
    }

    public static Intent getHangoutActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("destination", 3);
        return intent;
    }

    public static Intent getHangoutActivityIntent(Context context, EsAccount esaccount, Hangout.Info info, boolean flag, List arraylist)
    {
        Intent intent = new Intent(context, HangoutActivity.class);
        intent.setAction((new StringBuilder("unique")).append(System.currentTimeMillis()).toString());
        intent.putExtra("account", esaccount);
        intent.putExtra("hangout_info", info);
        intent.putExtra("hangout_skip_green_room", flag);
        intent.putExtra("hangout_participants", (ArrayList)arraylist);
        return intent;
    }

    public static Intent getHangoutActivityIntent(Context context, EsAccount esaccount, Hangout.RoomType roomtype, String s, String s1, String s2, String s3, Hangout.LaunchSource launchsource, 
            boolean flag, boolean flag1, List arraylist)
    {
        Intent intent = new Intent(context, HangoutActivity.class);
        intent.setAction((new StringBuilder("unique")).append(System.currentTimeMillis()).toString());
        intent.putExtra("account", esaccount);
        return getHangoutActivityIntent(context, esaccount, new Hangout.Info(roomtype, s, s1, s2, null, launchsource, false), flag1, arraylist);
    }

    public static Intent getHangoutParticipantListActivityIntent(Context context, EsAccount esaccount, List arraylist)
    {
        Intent intent = new Intent(context, HangoutParticipantListActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", -1L);
        intent.putExtra("hangout_participants", (ArrayList)arraylist);
        return intent;
    }

    public static Intent getHangoutRingingActivityIntent(Context context, EsAccount esaccount, String s, String s1, Hangout.Info info, boolean flag)
    {
        Intent intent = new Intent(context, HangoutRingingActivity.class);
        intent.setAction((new StringBuilder("unique")).append(System.currentTimeMillis()).toString());
        intent.putExtra("account", esaccount);
        intent.putExtra("hangout_inviter_id", s);
        intent.putExtra("hangout_inviter_name", s1);
        intent.putExtra("hangout_info", info);
        intent.putExtra("hangout_is_lite", flag);
        return intent;
    }
    
    public static Intent getNewConversationActivityIntent(Context context, EsAccount esaccount, AudienceData audiencedata)
    {
        Intent intent = new Intent(context, NewConversationActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("audience", audiencedata);
        return intent;
    }
    
    public static Intent getConversationInvititationActivityIntent(Context context, EsAccount esaccount, long l, String s, boolean flag)
    {
        Intent intent = new Intent(context, InvitationActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("inviter_id", s);
        intent.putExtra("is_group", flag);
        return intent;
    }
    
    public static Intent getConversationActivityHangoutTileIntent(Context context, EsAccount esaccount, long l, boolean flag)
    {
        Intent intent = getConversationActivityIntent(context, esaccount, l, flag);
        intent.putExtra("tile", HangoutTile.class.getName());
        return intent;
    }

    public static Intent getConversationActivityIntent(Context context, EsAccount esaccount, long l, boolean flag)
    {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.setAction((new StringBuilder("unique")).append(System.currentTimeMillis()).toString());
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("is_group", flag);
        return intent;
    }
    
    public static Intent getParticipantListActivityIntent(Context context, EsAccount esaccount, long l, String s, boolean flag, boolean flag1)
    {
        Intent intent = new Intent(context, ParticipantListActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("conversation_name", s);
        intent.putExtra("is_group", flag);
        if(flag1)
            intent.putExtra("tile", HangoutTile.class.getName());
        return intent;
    }
    
    public static Intent getNextOobIntent(Context context, EsAccount esaccount, AccountSettingsData accountsettingsdata, Intent intent)
    {
        OobIntents oobintents = (OobIntents)intent.getParcelableExtra("oob_intents");
        Intent intent1;
        if(oobintents == null)
            intent1 = null;
        else
            intent1 = oobintents.getNextIntent(context, esaccount, accountsettingsdata);
        return intent1;
    }
    
    public static Intent getCircleMembershipActivityIntent(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        Intent intent = new Intent(context, CirclesMembershipActivity.class);
        intent.putExtra("account", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("display_name", s1);
        intent.putExtra("empty_selection_allowed", flag);
        return intent;
    }
    
    public static Intent getHostedProfileIntent(Context context, EsAccount esaccount, String s, String s1, int i)
    {
        Intent intent;
        intent = new Intent(context, HomeActivity.class);
        intent.setAction("android.intent.action.VIEW");
        if(0 == i) {
        	intent.putExtra("destination", 1);
        } else if(1 == i) {
        	intent.putExtra("destination", 7);
        }
        
        intent.putExtra("account", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("notif_id", (String)null);
        return intent;
    }
    
    public static Intent getPostSearchActivityIntent(Context context, EsAccount esaccount, String s)
    {
        Intent intent = new Intent(context, PostSearchActivity.class);
        intent.putExtra("account", esaccount);
        if(s != null)
            intent.putExtra("query", s);
        return intent;
    }
    
    public static Intent getOutOfBoxActivityIntent(Context context, EsAccount esaccount, OobIntents oobintents, MobileOutOfBoxResponse mobileoutofboxresponse, String s)
    {
        Intent intent = new Intent(context, OutOfBoxActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("oob_intents", oobintents);
        intent.putExtra("network_oob", new OutOfBoxResponseParcelable(mobileoutofboxresponse));
        intent.putExtra("oob_origin", s);
        return intent;
    }
    
    public static Intent getOobSelectPlusPageActivityIntent(Context context, EsAccount esaccount, AccountSettingsData accountsettingsdata, OobIntents oobintents)
    {
        Intent intent = new Intent(context, OobSelectPlusPageActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("plus_pages", accountsettingsdata);
        intent.putExtra("oob_intents", oobintents);
        return intent;
    }
    
    public static Intent getOobSuggestedPeopleActivityIntent(Context context, EsAccount esaccount, OobIntents oobintents)
    {
        Intent intent = new Intent(context, OobSuggestedPeopleActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("oob_intents", oobintents);
        return intent;
    }

    public static Intent getOobContactsSyncIntent(Context context, EsAccount esaccount, OobIntents oobintents)
    {
        Intent intent = new Intent(context, OobContactsSyncActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("oob_intents", oobintents);
        return intent;
    }
    
    public static Intent getOobInstantUploadIntent(Context context, EsAccount esaccount, OobIntents oobintents)
    {
        Intent intent = new Intent(context, OobInstantUploadActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("oob_intents", oobintents);
        return intent;
    }
    
    public static Intent getPeopleSearchActivityIntent(Context context, EsAccount esaccount, String s, boolean flag, int i, boolean flag1, boolean flag2, boolean flag3, 
            boolean flag4, boolean flag5)
    {
        Intent intent = new Intent(context, PeopleSearchActivity.class);
        intent.putExtra("account", esaccount);
        intent.putExtra("picker_mode", flag);
        intent.putExtra("search_circles_usage", i);
        intent.putExtra("search_pub_profiles_enabled", flag1);
        intent.putExtra("search_phones_enabled", flag2);
        intent.putExtra("search_plus_pages_enabled", flag3);
        intent.putExtra("search_in_circles_enabled", flag4);
        intent.putExtra("query", s);
        intent.putExtra("filter_null_gaia_ids", flag5);
        return intent;
    }
    
    public static Intent getVideoViewActivityIntent(Context context, EsAccount esaccount, String s, long l, byte abyte0[])
    {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("owner_id", s);
        intent.putExtra("photo_id", l);
        intent.putExtra("data", abyte0);
        return intent;
    }
    
    public static Intent getInstantUploadSettingsActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, InstantUploadSettingsActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        return intent;
    }
    
    public static Intent getAddedToCircleActivityIntent(Context context, EsAccount esaccount, byte abyte0[], String s)
    {
        Intent intent = new Intent(context, AddedToCircleActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("circle_actor_data", abyte0);
        intent.putExtra("notif_id", s);
        return intent;
    }
    
    public static Intent getEventHangoutActivityIntent(Context context, EsAccount esaccount, String s)
    {
        return getHangoutActivityIntent(context, esaccount, Hangout.RoomType.EXTERNAL, null, "event", s, null, Hangout.LaunchSource.Event, false, false, null);
    }
    
    public static Intent getMissedHangoutCallbackIntent(Context context, EsAccount esaccount, Hangout.Info info, AudienceData audiencedata)
    {
        Intent intent = new Intent(context, HangoutActivity.class);
        intent.setAction((new StringBuilder("unique")).append(System.currentTimeMillis()).toString());
        intent.putExtra("account", esaccount);
        intent.putExtra("hangout_info", new Hangout.Info(info.getRoomType(), info.getDomain(), null, info.getId(), info.getNick(), Hangout.LaunchSource.MissedCall, true));
        intent.putExtra("hangout_skip_green_room", true);
        intent.putExtra("audience", audiencedata);
        return intent;
    }

    public static Intent getPeopleSearchActivityIntent(Context context, EsAccount esaccount, boolean flag, int i, boolean flag1, boolean flag2, boolean flag3, boolean flag4, 
            boolean flag5)
    {
        return getPeopleSearchActivityIntent(context, esaccount, null, flag, i, flag1, flag2, flag3, flag4, flag5);
    }
    
    public static PhotoViewIntentBuilder newPhotoComposeActivityIntentBuilder(Context context)
    {
        return new PhotoViewIntentBuilder(context, PhotoComposeActivity.class);
    }
    
    public static PhotoViewIntentBuilder newPhotoViewActivityIntentBuilder(Context context)
    {
        return new PhotoViewIntentBuilder(context, PhotoOneUpActivity.class);
    }
    
    public static PhotoViewIntentBuilder newPhotoViewFragmentIntentBuilder(Context context)
    {
        return new PhotoViewIntentBuilder(context, PhotoOneUpFragment.class);
    }
    
    public static PhotosIntentBuilder newAlbumsActivityIntentBuilder(Context context)
    {
        return new PhotosIntentBuilder(context, HostAlbumsActivity.class);
    }
    
    public static PhotoViewIntentBuilder newPhotoComposeFragmentIntentBuilder(Context context)
    {
        return new PhotoViewIntentBuilder(context, PhotoComposeFragment.class);
    }
    
    public static PhotosIntentBuilder newPhotosSelectionActivityIntentBuilder(Context context)
    {
        return new PhotosIntentBuilder(context, PhotosSelectionActivity.class);
    }
    
    public static Intent getLocationSettingActivityIntent() {
    	 String s;
         String s1;
    	boolean flag = true;
        if(android.os.Build.VERSION.SDK_INT >= 16) {
        	flag = false; 
        } else { 
        	s1 = android.os.Build.VERSION.RELEASE;
            if(!(TextUtils.isEmpty(s1) || s1.equals("4.1") || s1.startsWith("4.1.0") || s1.startsWith("4.1.1"))) 
            	flag = false;
        }
        
        if(flag)
            s = "android.settings.LOCATION_SOURCE_SETTINGS";
        else
            s = "com.google.android.gsf.GOOGLE_LOCATION_SETTINGS";
        return new Intent(s);
    }
    
    public static Intent getTargetIntent(Context context, Intent intent, String s)
    {
        Intent intent1 = new Intent(intent);
        intent1.putExtra("calling_package", s);
        intent1.putExtra("intent", intent);
        String s1 = intent1.getAction();
        if(TextUtils.isEmpty(s1))
            intent1 = null;
        else
        if(s1.equals("com.google.android.apps.plus.action.PLUSONE"))
            intent1.setComponent(new ComponentName(context, PlusOneActivity.class));
        else
        if(s1.equals("com.google.android.apps.plus.SHARE_GOOGLE") || s1.equals("android.intent.action.SEND") || s1.equals("android.intent.action.SEND_MULTIPLE") || s1.equals("com.google.android.apps.plus.GOOGLE_BIRTHDAY_POST") || s1.equals("com.google.android.apps.plus.GOOGLE_PLUS_SHARE"))
            intent1.setComponent(new ComponentName(context, ShareActivity.class));
        else
            intent1 = null;
        return intent1;
    }
    
    public static Intent getPostTextActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, PostTextActivity.class);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("account", esaccount);
        intent.putExtra("is_internal", true);
        intent.putExtra("start_editing", true);
        return intent;
    }
    
    public static Intent getCheckinActivityIntent(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, LocationPickerActivity.class);
        intent.putExtra("account", esaccount);
        intent.putExtra("places_only", true);
        return intent;
    }
    
    public static Intent getCameraIntentPhoto(String s)
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", Uri.fromFile(new File(Environment.getExternalStorageDirectory(), s)));
        return intent;
    }
    
    public static Intent getCameraIntentVideo()
    {
        return new Intent("android.media.action.VIDEO_CAPTURE");
    }
    
    public static Intent getEmotiShareActivityIntent(Context context, EsAccount esaccount, DbEmotishareMetadata dbemotisharemetadata)
    {
        Intent intent = new Intent(context, HostEmotiShareChooserActivity.class);
        intent.putExtra("account", esaccount);
        intent.setAction("android.intent.action.SEND");
        return intent;
    }
    
    public static Intent getPostActivityIntent(Context context, EsAccount esaccount, MediaRef mediaref)
    {
        Intent intent = new Intent(context, PostActivity.class);
        intent.setAction("android.intent.action.SEND");
        if(mediaref != null)
            intent.putExtra("android.intent.extra.STREAM", mediaref);
        intent.putExtra("account", esaccount);
        intent.putExtra("is_internal", true);
        return intent;
    }
    
    public static Intent getPostActivityIntent(Context context, EsAccount esaccount, DbEmotishareMetadata dbemotisharemetadata)
    {
        Intent intent = new Intent(context, PostActivity.class);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("account", esaccount);
        intent.putExtra("is_internal", true);
        if(dbemotisharemetadata != null)
            intent.putExtra("typed_image_embed", dbemotisharemetadata);
        return intent;
    }

    public static Intent getPostActivityIntent(Context context, EsAccount esaccount, DbLocation dblocation)
    {
        Intent intent = new Intent(context, PostActivity.class);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("account", esaccount);
        intent.putExtra("is_internal", true);
        if(dblocation != null)
            intent.putExtra("location", dblocation);
        return intent;
    }
    
    public static Intent getViewPanoramaActivityIntent(Context context, EsAccount esaccount, MediaRef mediaref)
    {
        Intent intent = new Intent(context, PanoramaViewerActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("mediaref", mediaref);
        return intent;
    }
    
    public static Intent getPhotoPickerIntent(Context context, EsAccount esaccount, String s, MediaRef mediaref, int i)
    {
        Intent intent = new Intent(context, PhotoPickerActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("account", esaccount);
        intent.putExtra("photo_picker_mode", 1);
        intent.putExtra("display_name", s);
        intent.putExtra("mediarefs", mediaref);
        intent.putExtra("photo_picker_crop_mode", i);
        return intent;
    }
    
    public static String getPersonIdFromProfileUrl(String s)
    {
        String s1 = getParameter(s, "pid=");
        if(s1 == null)
            s1 = s.substring(1 + s.lastIndexOf('/'));
        return s1;
    }
    
    public static void viewContent(Context context, EsAccount esaccount, String s)
    {
        viewContent(context, esaccount, s, null);
    }
    
    public static void viewContent(Context context, EsAccount esaccount, String s, String s1) {
        
    	if(!isProfileUrl(s)) { 
    		if(s.startsWith("#~loop:svt=album&")) {
    			String s4 = getParameter(s, "aid=");
                String s5 = getParameter(s, "eid=");
                String s6 = getParameter(s, "sid=");
                String s7 = getParameter(s, "oid=");
                String s8 = getParameter(s, "aname=");
                if(s5 != null)
                    context.startActivity(getHostedEventIntent(context, esaccount, Uri.decode(s5), s7, null));
                else
                if(s6 != null && s7 != null)
                    try
                    {
                        String s10 = Uri.decode(s6);
                        String s11 = Uri.decode(s8);
                        context.startActivity(newPhotosActivityIntentBuilder(context).setAccount(esaccount).setAlbumName(s11).setStreamId(s10).setGaiaId(s7).build());
                    }
                    catch(NumberFormatException numberformatexception2) { }
                else
                if(s4 != null && s7 != null)
                    try
                    {
                        String s9 = Uri.decode(s8);
                        context.startActivity(newPhotosActivityIntentBuilder(context).setAccount(esaccount).setAlbumName(s9).setAlbumId(s4).setGaiaId(s7).build());
                    }
                    catch(NumberFormatException numberformatexception1) { }
                
                return;
    		}
    		
    		List list = Uri.parse(s).getPathSegments();
            if(list.size() != 4 || !"photos".equals(list.get(0)) || !"albums".equals(list.get(2))) {
            	viewUrl(context, esaccount, s, s1);
            } else {
            	try
                {
                    String s2 = (String)list.get(1);
                    String s3 = (String)list.get(3);
                    context.startActivity(newPhotosActivityIntentBuilder(context).setAccount(esaccount).setAlbumId(s3).setGaiaId(s2).build());
                }
                catch(NumberFormatException numberformatexception) { }
            }
    	} else { 
    		String s12 = getPersonIdFromProfileUrl(s);
    		if(null != s12) {
    			context.startActivity(getProfileActivityByGaiaIdIntent(context, esaccount, s12, null));
    		}
    	}
    }
    
    public static void viewUrl(Context context, EsAccount esaccount, String s)
    {
        viewUrl(context, esaccount, s, null);
    }
    
    private static void viewUrl(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(s));
        intent.putExtra("com.android.browser.application_id", context.getPackageName());
        intent.addFlags(0x80000);
        Bundle bundle = new Bundle();
        bundle.putString("Referer", "http://plus.url.google.com/mobileapp");
        intent.putExtra("com.android.browser.headers", bundle);
        context.startActivity(intent);
        if(esaccount == null) {
        	return;
        }
        
        Bundle bundle1;
        if(!isExternalUrl(s)) {
        	bundle1 = null;
        } else { 
        	 bundle1 = new Bundle();
             bundle1.putString("extra_external_url", s);
             if(!TextUtils.isEmpty(s1))
                 bundle1.putString("extra_creation_source_id", s1);
        }
        
        try {
        	EsAnalytics.recordActionEvent(context, esaccount, OzActions.OPEN_URL, OzViews.getViewForLogging(context), bundle1);
        } catch (ActivityNotFoundException activitynotfoundexception) {
        	Log.w("Intents", (new StringBuilder("Unable to start activity for URL: ")).append(s).toString());
        }
        
    }
    
    public static String getParameter(String s, String s1)
    {
        String s2;
        int i;
        String s3;
        if(s1.endsWith("="))
            s2 = s1;
        else
            s2 = (new StringBuilder()).append(s1).append("=").toString();
        i = s.indexOf(s2);
        if(i != -1)
        {
            int j = i + s2.length();
            int k = s.indexOf('&', j);
            if(k == -1)
                s3 = s.substring(j);
            else
                s3 = s.substring(j, k);
        } else
        {
            s3 = null;
        }
        return s3;
    }
    
    private static boolean isExternalUrl(String s) {
        boolean flag = TextUtils.isEmpty(s);
        if(flag) 
        	return false;
        
        String s1 = s.toLowerCase();
        for(Iterator iterator = INTERNAL_URLS.iterator(); iterator.hasNext();)
            if(s1.contains((String)iterator.next()))
            {
                return false;
            }
        return true;
    }
	
	//===========================================================================
    //						Inner class
    //===========================================================================
	
	public static final class PhotoViewIntentBuilder
    {
		private EsAccount mAccount;
        private String mAlbumId;
        private String mAlbumName;
        private Boolean mAllowPlusOne;
        private String mAuthkey;
        private Boolean mDisableComments;
        private String mDisplayName;
        private String mEventId;
        private Long mForceLoadId;
        private String mGaiaId;
        private final Intent mIntent;
        private Boolean mIsPlaceholder;
        private MediaRef mMediaRefs[];
        private MediaRef.MediaType mMediaType;
        private String mNotificationId;
        private Integer mPageHint;
        private Long mPhotoId;
        private Integer mPhotoIndex;
        private String mPhotoOfUserId;
        private Boolean mPhotoOnly;
        private MediaRef mPhotoRef;
        private String mPhotoUrl;
        private String mRefreshAlbumId;
        private String mStreamId;

        private PhotoViewIntentBuilder(Context context, Class clazz) {
            mIntent = new Intent(context, clazz);
        }
		
        public final Intent build() {
            if(mAccount == null)
                throw new IllegalArgumentException("Account must be set");
            mIntent.setAction("android.intent.action.VIEW");
            mIntent.putExtra("account", mAccount);
            if(mAlbumId != null)
                mIntent.putExtra("album_id", mAlbumId);
            if(mAlbumName != null)
                mIntent.putExtra("album_name", mAlbumName);
            if(mAllowPlusOne != null)
                mIntent.putExtra("allow_plusone", mAllowPlusOne.booleanValue());
            if(mDisplayName != null)
                mIntent.putExtra("display_name", mDisplayName);
            if(mEventId != null)
                mIntent.putExtra("event_id", mEventId);
            if(mForceLoadId != null)
                mIntent.putExtra("force_load_id", mForceLoadId.longValue());
            if(mRefreshAlbumId != null)
                mIntent.putExtra("refresh_album_id", mRefreshAlbumId);
            if(mMediaRefs != null)
                mIntent.putExtra("mediarefs", mMediaRefs);
            if(mNotificationId != null)
                mIntent.putExtra("notif_id", mNotificationId);
            if(mGaiaId != null)
                mIntent.putExtra("owner_id", mGaiaId);
            if(mPageHint != null)
                mIntent.putExtra("page_hint", mPageHint.intValue());
            else
                mIntent.putExtra("page_hint", -1);
            if(mPhotoId != null)
                mIntent.putExtra("photo_id", mPhotoId.longValue());
            if(mPhotoIndex != null)
                mIntent.putExtra("photo_index", mPhotoIndex.intValue());
            if(mPhotoOfUserId != null)
                mIntent.putExtra("photos_of_user_id", mPhotoOfUserId);
            if(mPhotoOnly != null && mPhotoOnly.booleanValue() || mMediaRefs != null)
                mIntent.putExtra("show_photo_only", true);
            if(null != mPhotoRef) {
            	mIntent.putExtra("photo_ref", mPhotoRef);
            } else {
            	if(mPhotoId != null)
                {
                    Uri uri;
                    Uri uri1;
                    String s;
                    long l;
                    String s1;
                    MediaRef.MediaType mediatype;
                    MediaRef mediaref;
                    if(mPhotoUrl != null)
                        uri = Uri.parse(mPhotoUrl);
                    else
                        uri = null;
                    if(MediaStoreUtils.isMediaStoreUri(uri))
                        uri1 = uri;
                    else
                        uri1 = null;
                    s = mGaiaId;
                    l = mPhotoId.longValue();
                    s1 = mPhotoUrl;
                    if(mMediaType != null)
                        mediatype = mMediaType;
                    else
                        mediatype = MediaRef.MediaType.IMAGE;
                    mediaref = new MediaRef(s, l, s1, uri1, mediatype);
                    mIntent.putExtra("photo_ref", mediaref);
                }
            }
            if(mPhotoUrl != null)
                mIntent.putExtra("photo_url", mPhotoUrl);
            if(mStreamId != null)
                mIntent.putExtra("stream_id", mStreamId);
            if(mAuthkey != null)
                mIntent.putExtra("auth_key", mAuthkey);
            if(mDisableComments != null)
                mIntent.putExtra("disable_photo_comments", mDisableComments.booleanValue());
            if(mIsPlaceholder != null)
                mIntent.putExtra("is_placeholder", mIsPlaceholder.booleanValue());
            return mIntent;
        }

        public final PhotoViewIntentBuilder setAccount(EsAccount esaccount) {
            mAccount = esaccount;
            return this;
        }

        public final PhotoViewIntentBuilder setAlbumId(String s)
        {
            mAlbumId = s;
            return this;
        }

        public final PhotoViewIntentBuilder setAlbumName(String s)
        {
            mAlbumName = s;
            return this;
        }

        public final PhotoViewIntentBuilder setAllowPlusOne(Boolean boolean1)
        {
            mAllowPlusOne = boolean1;
            return this;
        }

        public final PhotoViewIntentBuilder setAuthkey(String s)
        {
            mAuthkey = s;
            return this;
        }

        public final PhotoViewIntentBuilder setDisableComments(Boolean boolean1)
        {
            mDisableComments = boolean1;
            return this;
        }

        public final PhotoViewIntentBuilder setDisplayName(String s)
        {
            mDisplayName = s;
            return this;
        }

        public final PhotoViewIntentBuilder setEventId(String s)
        {
            mEventId = s;
            return this;
        }

        public final PhotoViewIntentBuilder setForceLoadId(Long long1)
        {
            mForceLoadId = long1;
            return this;
        }

        public final PhotoViewIntentBuilder setGaiaId(String s)
        {
            mGaiaId = s;
            return this;
        }

        public final PhotoViewIntentBuilder setIsPlaceholder(Boolean boolean1)
        {
            mIsPlaceholder = boolean1;
            return this;
        }

        public final PhotoViewIntentBuilder setMediaRefs(MediaRef amediaref[])
        {
            mMediaRefs = amediaref;
            return this;
        }

        public final PhotoViewIntentBuilder setMediaType(MediaRef.MediaType mediatype)
        {
            mMediaType = mediatype;
            return this;
        }

        public final PhotoViewIntentBuilder setNotificationId(String s)
        {
            mNotificationId = s;
            return this;
        }

        public final PhotoViewIntentBuilder setPageHint(Integer integer)
        {
            mPageHint = integer;
            return this;
        }

        public final PhotoViewIntentBuilder setPhotoId(Long long1)
        {
            mPhotoId = long1;
            return this;
        }

        public final PhotoViewIntentBuilder setPhotoIndex(Integer integer)
        {
            mPhotoIndex = integer;
            return this;
        }

        public final PhotoViewIntentBuilder setPhotoOfUserId(String s)
        {
            mPhotoOfUserId = s;
            return this;
        }

        public final PhotoViewIntentBuilder setPhotoOnly(Boolean boolean1)
        {
            mPhotoOnly = boolean1;
            return this;
        }

        public final PhotoViewIntentBuilder setPhotoRef(MediaRef mediaref)
        {
            mPhotoRef = mediaref;
            return this;
        }

        public final PhotoViewIntentBuilder setPhotoUrl(String s)
        {
            mPhotoUrl = s;
            return this;
        }

        public final PhotoViewIntentBuilder setRefreshAlbumId(String s)
        {
            mRefreshAlbumId = s;
            return this;
        }

        public final PhotoViewIntentBuilder setStreamId(String s)
        {
            mStreamId = s;
            return this;
        }
    }

	
	public static final class PhotosIntentBuilder
    {

		private EsAccount mAccount;
        private String mAlbumId;
        private String mAlbumName;
        private String mAlbumType;
        private AudienceData mAudience;
        private String mAuthkey;
        private Integer mCropMode;
        private String mGaiaId;
        private final Intent mIntent;
        private Map mMediaRefUserMap;
        private MediaRef mMediaRefs[];
        private String mNotificationId;
        private String mPersonId;
        private String mPhotoOfUserId;
        private ArrayList mPhotoPickerMediaRefs;
        private Integer mPhotoPickerMode;
        private Integer mPhotoPickerTitleResourceId;
        private Boolean mPhotosHome;
        private Boolean mShowCameraAlbum;
        private String mStreamId;
        private Boolean mTakePhoto;
        private Boolean mTakeVideo;

        private PhotosIntentBuilder(Context context, Class clazz) {
            mIntent = new Intent(context, clazz);
        }

        public final Intent build()
        {
            if(mAccount == null)
                throw new IllegalArgumentException("Account must be set");
            mIntent.setAction("android.intent.action.VIEW");
            Bundle bundle = new Bundle();
            bundle.putParcelable("account", mAccount);
            if(mAlbumId != null)
                bundle.putString("album_id", mAlbumId);
            if(mAlbumName != null)
                bundle.putString("album_name", mAlbumName);
            if(mAlbumType != null)
                bundle.putString("album_type", mAlbumType);
            if(mMediaRefs != null)
                bundle.putParcelableArray("mediarefs", mMediaRefs);
            if(mNotificationId != null)
                bundle.putString("notif_id", mNotificationId);
            if(mGaiaId != null)
                bundle.putString("owner_id", mGaiaId);
            bundle.putInt("page_hint", -1);
            if(mPersonId != null)
                bundle.putString("person_id", mPersonId);
            if(mPhotoOfUserId != null)
                bundle.putString("photos_of_user_id", mPhotoOfUserId);
            if(mMediaRefs != null)
                bundle.putBoolean("show_photo_only", true);
            if(mPhotosHome != null)
                bundle.putBoolean("photos_home", mPhotosHome.booleanValue());
            if(mShowCameraAlbum != null)
                bundle.putBoolean("photos_show_camera_album", mShowCameraAlbum.booleanValue());
            if(mStreamId != null)
                bundle.putString("stream_id", mStreamId);
            if(mPhotoPickerMode != null)
                bundle.putInt("photo_picker_mode", mPhotoPickerMode.intValue());
            if(mPhotoPickerTitleResourceId != null)
                bundle.putInt("photo_picker_title", mPhotoPickerTitleResourceId.intValue());
            if(mPhotoPickerMediaRefs != null)
            {
                MediaRef amediaref[] = new MediaRef[mPhotoPickerMediaRefs.size()];
                mPhotoPickerMediaRefs.toArray(amediaref);
                bundle.putParcelableArray("photo_picker_selected", amediaref);
            }
            if(mCropMode != null)
                bundle.putInt("photo_picker_crop_mode", mCropMode.intValue());
            if(mAuthkey != null)
                bundle.putString("auth_key", mAuthkey);
            if(PrimitiveUtils.safeBoolean(mTakePhoto))
                bundle.putBoolean("take_photo", mTakePhoto.booleanValue());
            if(PrimitiveUtils.safeBoolean(mTakeVideo))
                bundle.putBoolean("take_video", mTakeVideo.booleanValue());
            if(mMediaRefUserMap != null)
                bundle.putSerializable("mediaref_user_map", (Serializable)mMediaRefUserMap);
            if(mAudience != null)
                bundle.putParcelable("audience", mAudience);
            mIntent.putExtras(bundle);
            return mIntent;
        }

        public final PhotosIntentBuilder setAccount(EsAccount esaccount)
        {
            mAccount = esaccount;
            return this;
        }

        public final PhotosIntentBuilder setAlbumId(String s)
        {
            mAlbumId = s;
            return this;
        }

        public final PhotosIntentBuilder setAlbumName(String s)
        {
            mAlbumName = s;
            return this;
        }

        public final PhotosIntentBuilder setAlbumType(String s)
        {
            mAlbumType = s;
            return this;
        }

        public final PhotosIntentBuilder setAudience(AudienceData audiencedata)
        {
            mAudience = audiencedata;
            return this;
        }

        public final PhotosIntentBuilder setAuthkey(String s)
        {
            mAuthkey = s;
            return this;
        }

        public final PhotosIntentBuilder setCropMode(Integer integer)
        {
            mCropMode = integer;
            return this;
        }

        public final PhotosIntentBuilder setGaiaId(String s)
        {
            mGaiaId = s;
            return this;
        }

        public final PhotosIntentBuilder setMediaRefUserMap(Map map)
        {
            if(map instanceof Serializable)
            {
                mMediaRefUserMap = map;
                return this;
            } else
            {
                throw new IllegalArgumentException("mediaRefUserMap must be serializable!");
            }
        }

        public final PhotosIntentBuilder setMediaRefs(MediaRef amediaref[])
        {
            mMediaRefs = amediaref;
            return this;
        }

        public final PhotosIntentBuilder setNotificationId(String s)
        {
            mNotificationId = s;
            return this;
        }

        public final PhotosIntentBuilder setPersonId(String s)
        {
            mPersonId = s;
            return this;
        }

        public final PhotosIntentBuilder setPhotoOfUserId(String s)
        {
            mPhotoOfUserId = s;
            return this;
        }

        public final PhotosIntentBuilder setPhotoPickerInitiallySelected(ArrayList arraylist)
        {
            mPhotoPickerMediaRefs = arraylist;
            return this;
        }

        public final PhotosIntentBuilder setPhotoPickerMode(Integer integer)
        {
            mPhotoPickerMode = integer;
            return this;
        }

        public final PhotosIntentBuilder setPhotoPickerTitleResourceId(Integer integer)
        {
            mPhotoPickerTitleResourceId = integer;
            return this;
        }

        public final PhotosIntentBuilder setPhotosHome(Boolean boolean1)
        {
            mPhotosHome = boolean1;
            return this;
        }

        public final PhotosIntentBuilder setShowCameraAlbum(Boolean boolean1)
        {
            mShowCameraAlbum = boolean1;
            return this;
        }

        public final PhotosIntentBuilder setStreamId(String s)
        {
            mStreamId = s;
            return this;
        }

        public final PhotosIntentBuilder setTakePhoto(boolean flag)
        {
            mTakePhoto = Boolean.valueOf(true);
            return this;
        }

        public final PhotosIntentBuilder setTakeVideo(boolean flag)
        {
            mTakeVideo = Boolean.valueOf(true);
            return this;
        }
    }

}
