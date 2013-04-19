/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import WriteReviewOperation.MediaRef;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.AuthData;
import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.analytics.AnalyticsInfo;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.BirthdayData;
import com.galaxy.meetup.client.android.api.BlockUserOperation;
import com.galaxy.meetup.client.android.api.CommentOptimisticPlusOneOperation;
import com.galaxy.meetup.client.android.api.CreateCircleOperation;
import com.galaxy.meetup.client.android.api.CreateEventOperation;
import com.galaxy.meetup.client.android.api.DeleteActivityOperation;
import com.galaxy.meetup.client.android.api.DeleteCirclesOperation;
import com.galaxy.meetup.client.android.api.DeleteCommentOperation;
import com.galaxy.meetup.client.android.api.DeleteEventOperation;
import com.galaxy.meetup.client.android.api.DeletePhotosOperation;
import com.galaxy.meetup.client.android.api.DeleteReviewOperation;
import com.galaxy.meetup.client.android.api.EditActivityOperation;
import com.galaxy.meetup.client.android.api.EditCommentStreamOperation;
import com.galaxy.meetup.client.android.api.EditSquareMembershipOperation;
import com.galaxy.meetup.client.android.api.EventHomePageOperation;
import com.galaxy.meetup.client.android.api.EventInviteOperation;
import com.galaxy.meetup.client.android.api.EventManageGuestOperation;
import com.galaxy.meetup.client.android.api.FindMorePeopleOperation;
import com.galaxy.meetup.client.android.api.GetActivityOperation;
import com.galaxy.meetup.client.android.api.GetAudienceOperation;
import com.galaxy.meetup.client.android.api.GetBlockedPeopleOperation;
import com.galaxy.meetup.client.android.api.GetEventInviteeListOperation;
import com.galaxy.meetup.client.android.api.GetEventThemesOperation;
import com.galaxy.meetup.client.android.api.GetNotificationSettingsOperation;
import com.galaxy.meetup.client.android.api.GetPhotoOperation;
import com.galaxy.meetup.client.android.api.GetSettingsOperation;
import com.galaxy.meetup.client.android.api.GetSquaresOperation;
import com.galaxy.meetup.client.android.api.GetViewerSquareOperation;
import com.galaxy.meetup.client.android.api.LocationQuery;
import com.galaxy.meetup.client.android.api.MarkItemReadOperation;
import com.galaxy.meetup.client.android.api.ModifyCirclePropertiesOperation;
import com.galaxy.meetup.client.android.api.MutateProfileOperation;
import com.galaxy.meetup.client.android.api.MuteActivityOperation;
import com.galaxy.meetup.client.android.api.MuteUserOperation;
import com.galaxy.meetup.client.android.api.OutOfBoxOperation;
import com.galaxy.meetup.client.android.api.OzServerException;
import com.galaxy.meetup.client.android.api.PhotosCreateCommentOperation;
import com.galaxy.meetup.client.android.api.PhotosInAlbumOperation;
import com.galaxy.meetup.client.android.api.PhotosNameTagApprovalOperation;
import com.galaxy.meetup.client.android.api.PhotosOfUserOperation;
import com.galaxy.meetup.client.android.api.PhotosPlusOneOperation;
import com.galaxy.meetup.client.android.api.PhotosReportAbuseOperation;
import com.galaxy.meetup.client.android.api.PhotosTagSuggestionApprovalOperation;
import com.galaxy.meetup.client.android.api.PostActivityOperation;
import com.galaxy.meetup.client.android.api.PostClientLogsOperation;
import com.galaxy.meetup.client.android.api.PostCommentStreamOperation;
import com.galaxy.meetup.client.android.api.PostEventCommentOperation;
import com.galaxy.meetup.client.android.api.PostOptimisticPlusOneOperation;
import com.galaxy.meetup.client.android.api.ProfileOptimisticPlusOneOperation;
import com.galaxy.meetup.client.android.api.ReadSquareMembersOperation;
import com.galaxy.meetup.client.android.api.RecordSuggestionActionOperation;
import com.galaxy.meetup.client.android.api.ReportAbuseActivityOperation;
import com.galaxy.meetup.client.android.api.ReportProfileAbuseOperation;
import com.galaxy.meetup.client.android.api.ReshareActivityOperation;
import com.galaxy.meetup.client.android.api.SavePhotoOperation;
import com.galaxy.meetup.client.android.api.SearchActivitiesOperation;
import com.galaxy.meetup.client.android.api.SetCircleMembershipOperation;
import com.galaxy.meetup.client.android.api.SetNotificationLastReadTimeOperation;
import com.galaxy.meetup.client.android.api.SetNotificationSettingsOperation;
import com.galaxy.meetup.client.android.api.SetScrapbookPhotoOperation;
import com.galaxy.meetup.client.android.api.SetVolumeControlsOperation;
import com.galaxy.meetup.client.android.api.SharePhotosToEventOperation;
import com.galaxy.meetup.client.android.api.SnapToPlaceOperation;
import com.galaxy.meetup.client.android.api.UpdateEventOperation;
import com.galaxy.meetup.client.android.api.UploadMediaOperation;
import com.galaxy.meetup.client.android.api.UserPhotoAlbumsOperation;
import com.galaxy.meetup.client.android.api.WriteReviewOperation;
import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbAnalyticsEvents;
import com.galaxy.meetup.client.android.content.DbEmbedEmotishare;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.content.EsDeepLinkInstallsData;
import com.galaxy.meetup.client.android.content.EsEmotiShareData;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EsNetworkData;
import com.galaxy.meetup.client.android.content.EsNotificationData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsPhotosData;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.EsSquaresData;
import com.galaxy.meetup.client.android.content.GooglePlaceReview;
import com.galaxy.meetup.client.android.content.LocalImageRequest;
import com.galaxy.meetup.client.android.content.NotificationSettingsData;
import com.galaxy.meetup.client.android.content.PeopleData;
import com.galaxy.meetup.client.android.iu.InstantUploadFacade;
import com.galaxy.meetup.client.android.network.ApiaryActivity;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.oob.OutOfBoxRequestParcelable;
import com.galaxy.meetup.client.android.realtimechat.BlockingC2DMClient;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.ui.widget.EsWidgetUtils;
import com.galaxy.meetup.client.external.PlatformContract;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.MediaStoreUtils;
import com.galaxy.meetup.client.util.ParticipantParcelable;
import com.galaxy.meetup.client.util.PlayStoreInstaller;
import com.galaxy.meetup.client.util.SearchUtils;
import com.galaxy.meetup.server.client.domain.ClientOzEvent;
import com.galaxy.meetup.server.client.domain.CommonContent;
import com.galaxy.meetup.server.client.domain.DataImage;
import com.galaxy.meetup.server.client.domain.DataPhoto;
import com.galaxy.meetup.server.client.domain.GoogleReviewProto;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.domain.ScrapbookInfo;
import com.galaxy.meetup.server.client.domain.ScrapbookInfoFullBleedPhoto;
import com.galaxy.meetup.server.client.domain.ScrapbookInfoOffset;
import com.galaxy.meetup.server.client.domain.SimpleProfile;
import com.galaxy.meetup.server.client.domain.request.MobileOutOfBoxRequest;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;
import com.galaxy.meetup.server.client.domain.response.UploadMediaResponse;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EsService extends Service implements ServiceThread.IntentProcessor {

	private static Map<Integer, AccountSettingsData> sAccountSettingsResponses = Collections.synchronizedMap(new ResultsLinkedHashMap<Integer, AccountSettingsData>());
    private static volatile EsAccount sActiveAccount;
    private static BlockUserOperation.Factory sBlockUserOperationFactory;
    private static final Object sDismissPeopleLock = new Object();
    private static Handler sHandler;
    private static final IntentPool sIntentPool = new IntentPool(8);
    private static String sLastCameraMediaLocation;
    private static Integer sLastRequestId = Integer.valueOf(0);
    private static final List sListeners = new ArrayList();
    private static MuteUserOperation.Factory sMuteUserOperationFactory;
    private static Map<Integer, MobileOutOfBoxResponse> sOutOfBoxResponses = Collections.synchronizedMap(new ResultsLinkedHashMap<Integer, MobileOutOfBoxResponse>());
    private static final Map sPendingIntents = new HashMap();
    private static PeopleData.Factory sPeopleDataFactory;
    private static final Map sResults = new ResultsLinkedHashMap();
    private final ServiceOperationListener mOperationListener = new ServiceOperationListener();
    private ServiceThread mServiceThread;
    private SnapToPlaceOperation mStapToPlaceOperation;
    
    private final Runnable mStopRunnable = new Runnable() {

        public final void run(){
            if(EsService.sPendingIntents.size() == 0){
                if(EsLog.isLoggable("EsService", 3))
                    Log.d("EsService", "Stop runnable: Stopping service");
                stopSelf();
            }
        }
    };
    
    public static void postOnServiceThread(Runnable runnable)
    {
        if(sHandler == null)
            sHandler = new Handler(Looper.getMainLooper());
        sHandler.post(runnable);
    }

    public static void postOnUiThread(Runnable runnable)
    {
        if(EsPeopleData.sHandler == null)
            EsPeopleData.sHandler = new Handler(Looper.getMainLooper());
        EsPeopleData.sHandler.post(runnable);
    }
    
    static int accountsChanged(Context context)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 4);
        return startCommand(context, intent);
    }
    
    public static Integer syncPeople(Context context, EsAccount esaccount, boolean flag) {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 500);
        intent.putExtra("acc", esaccount);
        intent.putExtra("refresh", false);
        return Integer.valueOf(startCommand(context, intent));
    }

	public static EsAccount getActiveAccount(Context context) {
		if(null != sActiveAccount) {
			return sActiveAccount;
		}
		
		sActiveAccount = EsAccountsData.getActiveAccount(context);
		return sActiveAccount;
    }
	
	public static boolean isRequestPending(int i) {
        return sPendingIntents.containsKey(Integer.valueOf(i));
    }
	
	public static boolean isPostPlusOnePending(String s) {
		
		Bundle bundle;
        int i;
        for(Iterator iterator = sPendingIntents.values().iterator(); iterator.hasNext();) {
        	 bundle = ((Intent)iterator.next()).getExtras();
        	 i = bundle.getInt("op");
        	 if(i != 16 && i != 17 || !bundle.getString("aid").equals(s)) 
        		 continue; 
        	 else 
        		 return true;
        }
        
        return false;
    }
	
	public static Integer changeNotificationSettings(Context context, EsAccount esaccount, NotificationSettingsData notificationsettingsdata)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 204);
        intent.putExtra("acc", esaccount);
        intent.putExtra("notification_settings", notificationsettingsdata);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static int notifyDeepLinkingInstall(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2600);
        intent.putExtra("acc", esaccount);
        intent.putExtra("package_name", s);
        return startCommand(context, intent);
    }
	
	public static Integer tellServerNotificationsWereRead(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 203);
        intent.putExtra("acc", esaccount);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static int deletePostPlusOne(Context context, EsAccount esaccount, String s) {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 17);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        return startCommand(context, intent);
    }
	
	public static int deleteReview(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2501);
        intent.putExtra("acc", esaccount);
        intent.putExtra("review_place_id", s);
        return startCommand(context, intent);
    }

	
	public static int writeReview(Context context, EsAccount esaccount, String s, GoogleReviewProto googlereviewproto, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2500);
        intent.putExtra("person_id", s);
        intent.putExtra("acc", esaccount);
        intent.putExtra("review_to_submit", new GooglePlaceReview(googlereviewproto));
        intent.putExtra("review_place_id", s1);
        return startCommand(context, intent);
    }
	
	public static int disableWipeoutStats(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2301);
        intent.putExtra("acc", esaccount);
        return startCommand(context, intent);
    }
	
	public static int enableAndPerformWipeoutStats(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2300);
        intent.putExtra("acc", esaccount);
        return startCommand(context, intent);
    }
	
	public static final int readSquareMembers(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2703);
        intent.putExtra("acc", esaccount);
        intent.putExtra("square_id", s);
        return startCommand(context, intent);
    }
	
	public static final int editSquareMembership(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2702);
        intent.putExtra("acc", esaccount);
        intent.putExtra("square_id", s);
        intent.putExtra("square_action", s1);
        return startCommand(context, intent);
    }

	public static int saveLastContactedTimestamp(Context context, EsAccount esaccount, long l)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2400);
        intent.putExtra("acc", esaccount);
        intent.putExtra("timestamp", -1L);
        return startCommand(context, intent);
    }
	
	public static Integer addPersonToCircle(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 702);
        intent.putExtra("acc", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("person_name", s1);
        intent.putExtra("circles_to_add", new String[] {
            s2
        });
        intent.putExtra("fire_and_forget", true);
        OzViews ozviews = OzViews.getViewForLogging(context);
        ArrayList arraylist = new ArrayList(1);
        arraylist.add(s);
        ArrayList arraylist1 = new ArrayList(1);
        arraylist1.add(s2);
        recordUpdateCircleAction(context, esaccount, ozviews, arraylist, arraylist1, true);
        Integer integer = Integer.valueOf(startCommand(context, intent));
        CircleMembershipManager.onStartAddToCircleRequest(context, esaccount, s);
        return integer;
    }
	
	public static Integer dismissSuggestedPeople(Context context, EsAccount esaccount, String s, List arraylist, List arraylist1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 711);
        intent.putExtra("acc", esaccount);
        intent.putExtra("suggestions_ui", s);
        intent.putExtra("person_ids", (ArrayList)arraylist);
        intent.putExtra("suggestion_ids", (ArrayList)arraylist1);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static void clearNetworkTransactionsData(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2001);
        intent.putExtra("acc", esaccount);
        startCommand(context, intent);
    }
	
	public static Integer createCircle(Context context, EsAccount esaccount, String s, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 708);
        intent.putExtra("acc", esaccount);
        intent.putExtra("circle_name", s);
        intent.putExtra("just_following", flag);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static int createComment(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 30);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("content", s1);
        return startCommand(context, intent);
    }
	
	public static Integer modifyCircleProperties(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 718);
        intent.putExtra("acc", esaccount);
        intent.putExtra("circle_id", s);
        intent.putExtra("circle_name", s1);
        intent.putExtra("just_following", flag);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static Integer deleteCircles(Context context, EsAccount esaccount, ArrayList arraylist)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 709);
        intent.putExtra("acc", esaccount);
        intent.putExtra("circle_ids", arraylist);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static int deleteActivity(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 20);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        return startCommand(context, intent);
    }
	
	public static int deleteComment(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 33);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("comment_id", s1);
        return startCommand(context, intent);
    }
	
	public static int deleteLocalPhotos(Context context, ArrayList arraylist)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 63);
        intent.putExtra("media_refs", arraylist);
        return startCommand(context, intent);
    }
	
	public static int deletePhotos(Context context, EsAccount esaccount, String s, ArrayList arraylist)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 61);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        intent.putExtra("photo_ids", arraylist);
        return startCommand(context, intent);
    }
	
	public static int moderateComment(Context context, EsAccount esaccount, String s, String s1, boolean flag, boolean flag1, boolean flag2)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 34);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("comment_id", s1);
        intent.putExtra("delete", flag);
        intent.putExtra("report", true);
        intent.putExtra("is_undo", flag2);
        return startCommand(context, intent);
    }
	
	public static int muteActivity(Context context, EsAccount esaccount, String s, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 18);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("mute_state", flag);
        return startCommand(context, intent);
    }
	
	public static int reportActivity(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 19);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("source_stream_id", s1);
        return startCommand(context, intent);
    }
	
	public static int getActivity(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 11);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("square_id", s1);
        return startCommand(context, intent);
    }
	
	public static int searchActivities(Context context, EsAccount esaccount, String s, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 1100);
        intent.putExtra("acc", esaccount);
        intent.putExtra("search_query", s);
        intent.putExtra("newer", false);
        return startCommand(context, intent);
    }
	
	public static int sendOutOfBoxRequest(Context context, EsAccount esaccount, MobileOutOfBoxRequest mobileoutofboxrequest)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 600);
        intent.putExtra("acc", esaccount);
        intent.putExtra("content", new OutOfBoxRequestParcelable(mobileoutofboxrequest));
        return startCommand(context, intent);
    }
	
	public static int createPostPlusOne(Context context, EsAccount esaccount, String s)
    {
        return startCommand(context, createPostPlusOneIntent(context, esaccount, s));
    }
	
	public static int plusOneComment(Context context, EsAccount esaccount, String s, long l, String s1, String s2, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 35);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("comment_id", s1);
        intent.putExtra("plusone_id", s2);
        intent.putExtra("plus_oned", flag);
        intent.putExtra("photo_id", l);
        return startCommand(context, intent);
    }
	
	public static int getActivityStream(Context context, EsAccount esaccount, int i, String s, String s1, String s2, String s3, boolean flag) {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 23);
        intent.putExtra("acc", esaccount);
        intent.putExtra("view", i);
        if(s != null)
            intent.putExtra("circle_id", s);
        if(s1 != null)
            intent.putExtra("gaia_id", s1);
        if(s2 != null)
            intent.putExtra("square_stream_id", s2);
        if(s3 != null)
            intent.putExtra("cont_token", s3);
        boolean flag1;
        if(s3 == null)
            flag1 = true;
        else
            flag1 = false;
        intent.putExtra("newer", flag1);
        intent.putExtra("from_widget", false);
        return startCommand(context, intent);
    }
	
	public static PendingIntent getDeleteNotificationsIntent(Context context, EsAccount esaccount, int i)
    {
        Intent intent = new Intent(context, EsService.class);
        intent.setAction("com.google.android.apps.plus.notif.clear");
        intent.putExtra("notif_id", 1);
        intent.putExtra("op", 206);
        intent.putExtra("acc", esaccount);
        return PendingIntent.getService(context, 0, intent, 0x10000000);
    }
	
	public static int getEvent(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 901);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event_id", s);
        return startCommand(context, intent);
    }
	
	public static int getPhotosHome(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 60);
        intent.putExtra("acc", esaccount);
        intent.putExtra("auth_key", s);
        return startCommand(context, intent);
    }
	
	public static int getAlbumList(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 50);
        intent.putExtra("acc", esaccount);
        if(s != null)
            intent.putExtra("gaia_id", s);
        return startCommand(context, intent);
    }
	
	public static int getStreamPhotos(Context context, EsAccount esaccount, String s, String s1, Integer integer, Integer integer1, String s2)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 52);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        intent.putExtra("stream_id", s1);
        intent.putExtra("offset", integer.intValue());
        intent.putExtra("max_count", integer1.intValue());
        intent.putExtra("auth_key", s2);
        return startCommand(context, intent);
    }
	
	public static int getPhotosOfUser(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 57);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        return startCommand(context, intent);
    }
	
	public static int getAlbumPhotos(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 51);
        intent.putExtra("acc", esaccount);
        intent.putExtra("album_id", s);
        intent.putExtra("gaia_id", s1);
        intent.putExtra("auth_key", s2);
        return startCommand(context, intent);
    }
	
	public static int createEvent(Context context, EsAccount esaccount, PlusEvent plusevent, AudienceData audiencedata, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 903);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event", JsonUtil.toByteArray(plusevent));
        intent.putExtra("audience", audiencedata);
        intent.putExtra("external_id", s);
        return startCommand(context, intent);
    }
	
	public static int updateEvent(Context context, EsAccount esaccount, PlusEvent plusevent)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 904);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event", JsonUtil.toByteArray(plusevent));
        return startCommand(context, intent);
    }
	
	public static int readEvent(Context context, EsAccount esaccount, String s, String s1, String s2, String s3, String s4, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 909);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("pollingtoken", s1);
        intent.putExtra("resumetoken", s2);
        intent.putExtra("invitationtoken", s3);
        intent.putExtra("event_auth_key", s4);
        intent.putExtra("fetchnewer", flag);
        return startCommand(context, intent);
    }
	
	public static int readEvent(Context context, EsAccount esaccount, String s, String s1, String s2, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 909);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("gaia_id", s1);
        intent.putExtra("event_auth_key", s2);
        intent.putExtra("fetchnewer", true);
        intent.putExtra("resolvetokens", true);
        return startCommand(context, intent);
    }
	
	public static int editActivity(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 14);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("content", s1);
        intent.putExtra("reshare", flag);
        return startCommand(context, intent);
    }
	
	public static int editComment(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 32);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("comment_id", s1);
        intent.putExtra("content", s2);
        return startCommand(context, intent);
    }
	
	public static int editPhotoComment(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 64);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("comment_id", s1);
        intent.putExtra("content", s2);
        return startCommand(context, intent);
    }

	public static int getNearbyActivities(Context context, EsAccount esaccount, int i, DbLocation dblocation, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 22);
        intent.putExtra("acc", esaccount);
        intent.putExtra("view", i);
        intent.putExtra("loc", dblocation);
        if(s != null)
            intent.putExtra("cont_token", s);
        boolean flag;
        if(s == null)
            flag = true;
        else
            flag = false;
        intent.putExtra("newer", flag);
        return startCommand(context, intent);
    }

    public static int getNearbyLocations(Context context, EsAccount esaccount, LocationQuery locationquery)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 41);
        intent.putExtra("acc", esaccount);
        intent.putExtra("loc_query", locationquery);
        return startCommand(context, intent);
    }

    public static int getNearbyLocations(Context context, EsAccount esaccount, LocationQuery locationquery, DbLocation dblocation)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 41);
        intent.putExtra("acc", esaccount);
        intent.putExtra("loc_query", locationquery);
        intent.putExtra("loc", dblocation);
        return startCommand(context, intent);
    }

    public static Integer getNotificationSettings(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 205);
        intent.putExtra("acc", esaccount);
        return Integer.valueOf(startCommand(context, intent));
    }
    
    public static Integer getEventHome(Context context, EsAccount esaccount) {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 900);
        intent.putExtra("acc", esaccount);
        return Integer.valueOf(startCommand(context, intent));
    }
    
    private static Intent createPostPlusOneIntent(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 16);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        return intent;
    }
    
    public static Integer setVolumeControl(Context context, EsAccount esaccount, String s, int i)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 719);
        intent.putExtra("acc", esaccount);
        intent.putExtra("circle_id", s);
        intent.putExtra("volume", i);
        return Integer.valueOf(startCommand(context, intent));
    }

    public static Integer setVolumeControl(Context context, EsAccount esaccount, Map hashmap)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 719);
        intent.putExtra("acc", esaccount);
        intent.putExtra("volume_map", (HashMap)hashmap);
        return Integer.valueOf(startCommand(context, intent));
    }
    
    public static int removeAccount(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 3);
        intent.putExtra("acc", esaccount);
        return startCommand(context, intent);
    }
    
    public static Integer recordSuggestionAction(Context context, EsAccount esaccount, String s, ArrayList arraylist, ArrayList arraylist1, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 721);
        intent.putExtra("acc", esaccount);
        intent.putExtra("suggestions_ui", s);
        intent.putExtra("person_ids", arraylist);
        intent.putExtra("suggestion_ids", arraylist1);
        intent.putExtra("action_type", s1);
        return Integer.valueOf(startCommand(context, intent));
    }
    
    private static void recordUpdateCircleAction(Context context, EsAccount esaccount, OzViews ozviews, ArrayList arraylist, ArrayList arraylist1, boolean flag)
    {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("extra_participant_ids", arraylist);
        bundle.putStringArrayList("extra_circle_ids", arraylist1);
        OzActions ozactions;
        if(flag)
            ozactions = OzActions.ADD_CIRCLE_MEMBERS;
        else
            ozactions = OzActions.REMOVE_CIRCLE_MEMBERS;
        EsAnalytics.recordActionEvent(context, esaccount, ozactions, ozviews, bundle);
    }

    private static void recordUpdateCircleAction(Context context, EsAccount esaccount, OzViews ozviews, ArrayList arraylist, String as[], boolean flag)
    {
        if(as != null && as.length > 0)
        {
            ArrayList arraylist1 = new ArrayList();
            int i = as.length;
            for(int j = 0; j < i; j++)
                arraylist1.add(as[j]);

            recordUpdateCircleAction(context, esaccount, ozviews, arraylist, arraylist1, flag);
        }
    }
    
    public static Integer setCircleMembership(Context context, EsAccount esaccount, String s, String s1, String as[], String as1[])
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 702);
        intent.putExtra("acc", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("person_name", s1);
        intent.putExtra("circles_to_add", as);
        intent.putExtra("circles_to_remove", as1);
        OzViews ozviews = OzViews.getViewForLogging(context);
        ArrayList arraylist = new ArrayList(1);
        arraylist.add(s);
        recordUpdateCircleAction(context, esaccount, ozviews, arraylist, as, true);
        recordUpdateCircleAction(context, esaccount, ozviews, arraylist, as1, false);
        return Integer.valueOf(startCommand(context, intent));
    }
    
    public static Integer markNotificationAsRead(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 201);
        intent.putExtra("acc", esaccount);
        intent.putExtra("notif_id", s);
        return Integer.valueOf(startCommand(context, intent));
    }
    
    public static Integer syncNotifications(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 202);
        intent.putExtra("acc", esaccount);
        return Integer.valueOf(startCommand(context, intent));
    }
    
    public static int getActivityAudience(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 12);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        return startCommand(context, intent);
    }
    
    public static final int getSquare(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2701);
        intent.putExtra("acc", esaccount);
        intent.putExtra("square_id", s);
        return startCommand(context, intent);
    }

    public static final int getSquares(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2700);
        intent.putExtra("acc", esaccount);
        return startCommand(context, intent);
    }
    
    public static Integer insertCameraPhoto(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 1120);
        intent.putExtra("acc", esaccount);
        intent.putExtra("filename", s);
        return Integer.valueOf(startCommand(context, intent));
    }
    
    public static void insertEvent(Context context, EsAccount esaccount, ClientOzEvent clientozevent)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 1008);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event", JsonUtil.toByteArray(clientozevent));
        startCommand(context, intent);
    }
    
    public static Integer mutateProfile(Context context, EsAccount esaccount, SimpleProfile simpleprofile)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 704);
        intent.putExtra("acc", esaccount);
        intent.putExtra("profile", simpleprofile.toJsonString());
        return Integer.valueOf(startCommand(context, intent));
    }
    
    public static int reshareActivity(Context context, EsAccount esaccount, String s, String s1, AudienceData audiencedata)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 21);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("content", s1);
        intent.putExtra("audience", audiencedata);
        return startCommand(context, intent);
    }
    
    public static int activateAccount(Context context, EsAccount esaccount, String s, String s1, String s2, boolean flag, AccountSettingsData accountsettingsdata)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 7);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        intent.putExtra("display_name", s1);
        intent.putExtra("photo_url", s2);
        intent.putExtra("is_plus_page", flag);
        intent.putExtra("account_settings", accountsettingsdata);
        return startCommand(context, intent);
    }
	
    public static void uploadChangedSettings(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 1110);
        intent.putExtra("acc", esaccount);
        startCommand(context, intent);
    }
    
    public static int savePhoto(Context context, EsAccount esaccount, String s, boolean flag, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 66);
        intent.putExtra("acc", esaccount);
        intent.putExtra("url", s);
        intent.putExtra("full_res", flag);
        intent.putExtra("description", s1);
        return startCommand(context, intent);
    }
    
    public static int getPhoto(Context context, EsAccount esaccount, String s, long l, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 62);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        intent.putExtra("photo_id", l);
        if(s1 != null)
            intent.putExtra("auth_key", s1);
        return startCommand(context, intent);
    }
    
    public static int createPhotoComment(Context context, EsAccount esaccount, String s, long l, String s1, String s2)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 53);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        intent.putExtra("photo_id", l);
        intent.putExtra("text", s1);
        intent.putExtra("auth_key", s2);
        return startCommand(context, intent);
    }
    
    public static int suggestedTagApproval(Context context, EsAccount esaccount, String s, String s1, String s2, String s3, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 59);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s1);
        intent.putExtra("taggee_id", s);
        intent.putExtra("photo_id", s2);
        intent.putExtra("shape_id", s3);
        intent.putExtra("approved", flag);
        return startCommand(context, intent);
    }
    
    public static int nameTagApproval(Context context, EsAccount esaccount, String s, Long long1, Long long2, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 58);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        intent.putExtra("photo_id", long1.longValue());
        intent.putExtra("shape_id", long2.longValue());
        intent.putExtra("approved", flag);
        return startCommand(context, intent);
    }
    
    public static int reportPhotoAbuse(Context context, EsAccount esaccount, long l, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 67);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        intent.putExtra("photo_id", l);
        return startCommand(context, intent);
    }
    
    public static int deletePhotoComment(Context context, EsAccount esaccount, Long long1, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 54);
        intent.putExtra("acc", esaccount);
        intent.putExtra("photo_id", long1.longValue());
        intent.putExtra("comment_id", s);
        return startCommand(context, intent);
    }
    
    public static int reportPhotoComment(Context context, EsAccount esaccount, Long long1, String s, boolean flag, boolean flag1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 55);
        intent.putExtra("acc", esaccount);
        intent.putExtra("photo_id", long1.longValue());
        intent.putExtra("comment_id", s);
        intent.putExtra("delete", flag);
        intent.putExtra("is_undo", flag1);
        return startCommand(context, intent);
    }
    
    public static int photoPlusOne(Context context, EsAccount esaccount, String s, String s1, long l, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 56);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        intent.putExtra("album_id", s1);
        intent.putExtra("photo_id", l);
        intent.putExtra("plus_oned", flag);
        return startCommand(context, intent);
    }
    
    public static int uploadProfilePhoto(Context context, EsAccount esaccount, byte abyte0[])
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 715);
        intent.putExtra("acc", esaccount);
        intent.putExtra("data", abyte0);
        return startCommand(context, intent);
    }
    
    public static int getPhotoSettings(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 65);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        return startCommand(context, intent);
    }
    
    public static boolean isPhotoPlusOnePending(String s, String s1, long l)
    {
    	Bundle bundle = null;
        for(Iterator iterator = sPendingIntents.values().iterator(); iterator.hasNext();) {
        	bundle = ((Intent)iterator.next()).getExtras();
        	if(bundle.getInt("op") != 56 || !TextUtils.equals(bundle.getString("gaia_id"), s) || !TextUtils.equals(bundle.getString("album_id"), s1) || bundle.getLong("photo_id", 0L) != l) 
        		continue; 
        	else 
        		return true;
        }
        
        return false;
    }
    
    public static Integer syncBlockedPeople(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 503);
        intent.putExtra("acc", esaccount);
        return Integer.valueOf(startCommand(context, intent));
    }
    
    private void completeRequest(final Intent intent, final ServiceResult serviceResult, final Object resultValue)
    {
        sHandler.post(new Runnable() {

            public final void run()
            {
                onIntentProcessed(intent, serviceResult, resultValue);
            }
        });
    }
    
    private static void setActiveAccount(EsAccount esaccount)
    {
        if(EsLog.isLoggable("EsService", 3))
        {
            StringBuilder stringbuilder = new StringBuilder("setActiveAccount: ");
            String s;
            if(esaccount == null)
                s = null;
            else
                s = (new StringBuilder()).append(esaccount.getName()).append(" ").append(esaccount.getPersonId()).toString();
            Log.d("EsService", stringbuilder.append(s).toString());
        }
        sActiveAccount = esaccount;
    }
    
    public static void scheduleSyncAlarm(Context context)
    {
        AlarmManager alarmmanager = (AlarmManager)context.getSystemService("alarm");
        Intent intent = new Intent(context, EsService.class);
        intent.setAction("com.google.android.apps.plus.content.sync");
        intent.putExtra("op", 1002);
        PendingIntent pendingintent = PendingIntent.getService(context, 0, intent, 0x10000000);
        alarmmanager.setInexactRepeating(2, 15000L + SystemClock.elapsedRealtime(), 0x36ee80L, pendingintent);
    }
    
    public static void scheduleUnconditionalSyncAlarm(Context context)
    {
        AlarmManager alarmmanager = (AlarmManager)context.getSystemService("alarm");
        Intent intent = new Intent(context, EsService.class);
        intent.setAction("com.google.android.apps.plus.content.unconditionalsync");
        intent.putExtra("op", 1005);
        PendingIntent pendingintent = PendingIntent.getService(context, 0, intent, 0x10000000);
        alarmmanager.setInexactRepeating(2, 10000L + SystemClock.elapsedRealtime(), 0x2932e00L, pendingintent);
    }
    
    public static void syncComplete(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 1004);
        intent.putExtra("acc", esaccount);
        intent.putExtra("content", s);
        startCommand(context, intent);
    }
    
    public static int sendEventRsvp(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 902);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("rsvp_type", s2);
        intent.putExtra("event_auth_key", s1);
        return startCommand(context, intent);
    }
    
    public static int sharePhotosToEvents(Context context, EsAccount esaccount, String s, List list)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        long al[] = new long[list.size()];
        for(int i = 0; i < list.size(); i++)
            al[i] = ((Long)list.get(i)).longValue();

        intent.putExtra("op", 911);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("photo_ids", al);
        return startCommand(context, intent);
    }
    
    public static int invitePeopleToEvent(Context context, EsAccount esaccount, String s, String s1, String s2, AudienceData audiencedata)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 907);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("auth_key", s1);
        intent.putExtra("gaia_id", s2);
        intent.putExtra("audience", audiencedata);
        return startCommand(context, intent);
    }
    
    public static int manageEventGuest(Context context, EsAccount esaccount, String s, String s1, boolean flag, String s2, String s3)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 1009);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("auth_key", s1);
        intent.putExtra("blacklist", flag);
        intent.putExtra("gaia_id", s2);
        intent.putExtra("email", s3);
        return startCommand(context, intent);
    }
    
    public static int getEventInvitees(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 910);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("auth_key", s1);
        intent.putExtra("include_blacklist", true);
        return startCommand(context, intent);
    }
    
    public static int deleteEvent(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 908);
        intent.putExtra("acc", esaccount);
        intent.putExtra("event_id", s);
        intent.putExtra("auth_key", s1);
        return startCommand(context, intent);
    }
    
    public static int updateEventPhoto(Context context, EsAccount esaccount, String s, String s1, String s2, String s3)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 905);
        intent.putExtra("event_id", s);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s1);
        intent.putExtra("photo_id", Long.parseLong(s2));
        intent.putExtra("fingerprint", s3);
        return startCommand(context, intent);
    }
    
    public static int createEventComment(Context context, EsAccount esaccount, String s, String s1, String s2, String s3)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 31);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", s);
        intent.putExtra("event_id", s1);
        intent.putExtra("auth_key", s2);
        intent.putExtra("content", s3);
        return startCommand(context, intent);
    }
    
    public final void onIntentProcessed(Intent intent, ServiceResult serviceresult, Object obj) {
    	
        int op = intent.getIntExtra("op", -1);
        int rid = intent.getIntExtra("rid", -1);
        switch(op) {
        case 1:
        	EsAccount esaccount7 = (EsAccount)obj;
        	// FIXME 4 Test
            EsAccountsData.activateAccount(getApplicationContext(), EsAccountsData.insertAccount(getApplicationContext(), "sihai", esaccount7.getName(), "sihai", false, false), "http://www.google.com.hk/images/srpr/logo4w.png");
            
            if(!serviceresult.hasError() || isOutOfBoxError(serviceresult.getException()))
            {
                setActiveAccount(esaccount7);
                EsWidgetUtils.updateAllWidgets(this);
                if(!sAccountSettingsResponses.containsKey(Integer.valueOf(rid)))
                    EsAccountsData.restoreAccountSettings(this, esaccount7);
            }
            Iterator iterator89 = sListeners.iterator();
            while(iterator89.hasNext()) 
                ((EsServiceListener)iterator89.next()).onAccountAdded(rid, esaccount7, serviceresult);
        	break;
        case 3:
        	setActiveAccount(null);
            for(Iterator iterator87 = sListeners.iterator(); iterator87.hasNext(); intent.getParcelableExtra("acc"))
                iterator87.next();

            EsWidgetUtils.updateAllWidgets(this);
        	break;
        case 4:
            List list1 = (List)obj;
            if(null != list1) {
            	if(EsLog.isLoggable("EsService", 3))
                {
                    String s;
                    for(Iterator iterator38 = list1.iterator(); iterator38.hasNext(); Log.d("EsService", (new StringBuilder("OP_ACCOUNTS_CHANGED removed: ")).append(s).toString()))
                        s = (String)iterator38.next();

                }
            	if(null != sActiveAccount) {
            		boolean doIt = false;
            		for(Iterator iterator37 = list1.iterator(); iterator37.hasNext();) {
            			if(((String)iterator37.next()).equals(sActiveAccount.getName())) {
            				doIt = true;
            				break;
            			}
            		}
            		
            		if(doIt) {
            			setActiveAccount(null);
                        EsWidgetUtils.updateAllWidgets(this);
            		}
            	}
            }
            serviceresult = null;
        	break;
        case 5:
        	serviceresult = null;
        	break;
        case 6:
        	setActiveAccount(null);
            Iterator iterator86 = sListeners.iterator();
            while(iterator86.hasNext()) 
                ((EsServiceListener)iterator86.next()).onAccountUpgraded(rid, (EsAccount)intent.getParcelableExtra("acc"), serviceresult);
        	break;
        case 7:
        	EsAccount esaccount6 = (EsAccount)obj;
            if(!serviceresult.hasError())
            {
                setActiveAccount(esaccount6);
                EsWidgetUtils.updateAllWidgets(this);
                EsAccountsData.restoreAccountSettings(this, esaccount6);
            }
            Iterator iterator88 = sListeners.iterator();
            while(iterator88.hasNext()) 
                ((EsServiceListener)iterator88.next()).onAccountActivated(rid, serviceresult);
        	break;
        case 11:
        	Iterator iterator84 = sListeners.iterator();
            while(iterator84.hasNext()) 
            {
                EsServiceListener esservicelistener52 = (EsServiceListener)iterator84.next();
                intent.getParcelableExtra("acc");
                String s4 = intent.getStringExtra("aid");
                intent.getStringExtra("square_id");
                esservicelistener52.onGetActivity(rid, s4, serviceresult);
            }
        	break;
        case 12:
        	AudienceData audiencedata1;
            Iterator iterator83;
            if(obj != null)
                audiencedata1 = ((GetAudienceOperation)obj).getAudience();
            else
                audiencedata1 = null;
            iterator83 = sListeners.iterator();
            while(iterator83.hasNext()) 
            {
                EsServiceListener esservicelistener51 = (EsServiceListener)iterator83.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                esservicelistener51.onGetActivityAudience(rid, audiencedata1, serviceresult);
            }
        	break;
        case 14:
        	Iterator iterator82 = sListeners.iterator();
            while(iterator82.hasNext()) 
            {
                EsServiceListener esservicelistener50 = (EsServiceListener)iterator82.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                esservicelistener50.onEditActivity(rid, serviceresult);
            }
        	break;
        case 16:
        	intent.getParcelableExtra("acc");
            intent.getStringExtra("aid");
            Iterator iterator53 = sListeners.iterator();
            while(iterator53.hasNext()) 
                ((EsServiceListener)iterator53.next()).onCreatePostPlusOne(serviceresult);
        	break;
        case 17:
        	intent.getParcelableExtra("acc");
            intent.getStringExtra("aid");
            Iterator iterator52 = sListeners.iterator();
            while(iterator52.hasNext()) 
                ((EsServiceListener)iterator52.next()).onDeletePostPlusOne(serviceresult);
        	break;
        case 18:
        	Iterator iterator79 = sListeners.iterator();
            while(iterator79.hasNext()) 
            {
                EsServiceListener esservicelistener47 = (EsServiceListener)iterator79.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                esservicelistener47.onMuteActivity(rid, serviceresult);
            }
        	break;
        case 19:
        	Iterator iterator78 = sListeners.iterator();
            while(iterator78.hasNext()) 
            {
                EsServiceListener esservicelistener46 = (EsServiceListener)iterator78.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                esservicelistener46.onReportActivity(rid, serviceresult);
            }
        	break;
        case 20:
        	Iterator iterator80 = sListeners.iterator();
            while(iterator80.hasNext()) 
            {
                EsServiceListener esservicelistener48 = (EsServiceListener)iterator80.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                esservicelistener48.onDeleteActivity(rid, serviceresult);
            }
        	break;
        case 21:
        	Iterator iterator81 = sListeners.iterator();
            while(iterator81.hasNext()) 
            {
                EsServiceListener esservicelistener49 = (EsServiceListener)iterator81.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                esservicelistener49.onReshareActivity(rid, serviceresult);
            }
        	break;
        case 22:
        case 23:
        	Iterator iterator85 = sListeners.iterator();
            while(iterator85.hasNext()) 
            {
                EsServiceListener esservicelistener53 = (EsServiceListener)iterator85.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("gaia_id");
                intent.getStringExtra("circle_id");
                intent.getIntExtra("view", 0);
                intent.getParcelableExtra("loc");
                esservicelistener53.onGetActivities(rid, intent.getBooleanExtra("newer", false), intent.getIntExtra("max_length", -1), serviceresult);
            }
        	break;
        case 25:
        	if(obj != null)
            {
                MarkItemReadOperation markitemreadoperation = (MarkItemReadOperation)obj;
                int l = markitemreadoperation.getErrorCode();
                Exception exception1 = markitemreadoperation.getException();
                if(l == 200 && exception1 == null && !markitemreadoperation.isNotificationType())
                {
                    List list2 = markitemreadoperation.getItemIds();
                    if(list2 != null && !list2.isEmpty())
                    {
                        Bundle bundle;
                        OzViews ozviews;
                        for(Iterator iterator44 = list2.iterator(); iterator44.hasNext(); EsAnalytics.recordActionEvent(this, (EsAccount)intent.getParcelableExtra("acc"), OzActions.STREAM_MARK_ACTIVITY_AS_READ, ozviews, bundle))
                        {
                            bundle = EsAnalyticsData.createExtras("extra_activity_id", (String)iterator44.next());
                            ozviews = OzViews.valueOf(intent.getIntExtra("start_view", OzViews.HOME.ordinal()));
                        }

                    }
                }
            }
            serviceresult = null;
        	break;
        case 30:
        	Iterator iterator59 = sListeners.iterator();
            while(iterator59.hasNext()) 
            {
                EsServiceListener esservicelistener35 = (EsServiceListener)iterator59.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                esservicelistener35.onCreateComment(rid, serviceresult);
            }
        	break;
        case 31:
        	Iterator iterator58 = sListeners.iterator();
            while(iterator58.hasNext()) 
            {
                EsServiceListener esservicelistener34 = (EsServiceListener)iterator58.next();
                intent.getParcelableExtra("acc");
                esservicelistener34.onCreateEventComment(rid, serviceresult);
            }
        	break;
        case 32:
        	Iterator iterator57 = sListeners.iterator();
            while(iterator57.hasNext()) 
            {
                EsServiceListener esservicelistener33 = (EsServiceListener)iterator57.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                intent.getStringExtra("comment_id");
                esservicelistener33.onEditComment(rid, serviceresult);
            }
        	break;
        case 33:
        	Iterator iterator56 = sListeners.iterator();
            while(iterator56.hasNext()) 
            {
                EsServiceListener esservicelistener32 = (EsServiceListener)iterator56.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                intent.getStringExtra("comment_id");
                esservicelistener32.onDeleteComment(rid, serviceresult);
            }
        	break;
        case 34:
        	Iterator iterator55 = sListeners.iterator();
            while(iterator55.hasNext()) 
            {
                EsServiceListener esservicelistener31 = (EsServiceListener)iterator55.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                esservicelistener31.onModerateComment(rid, intent.getStringExtra("comment_id"), intent.getBooleanExtra("is_undo", false), serviceresult);
            }
        	break;
        case 35:
        	boolean flag1 = intent.getBooleanExtra("plus_oned", true);
            Iterator iterator54 = sListeners.iterator();
            while(iterator54.hasNext()) 
            {
                EsServiceListener esservicelistener30 = (EsServiceListener)iterator54.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("aid");
                intent.getStringExtra("comment_id");
                esservicelistener30.onPlusOneComment(flag1, serviceresult);
            }
        	break;
        case 41:
        	Iterator iterator51 = sListeners.iterator();
            while(iterator51.hasNext()) 
            {
                EsServiceListener esservicelistener29 = (EsServiceListener)iterator51.next();
                intent.getParcelableExtra("acc");
                intent.getParcelableExtra("loc_query");
                esservicelistener29.onLocationQuery(rid, serviceresult);
            }
        	break;
        case 50:
        	Iterator iterator77 = sListeners.iterator();
            while(iterator77.hasNext()) 
            {
                EsServiceListener esservicelistener45 = (EsServiceListener)iterator77.next();
                intent.getParcelableExtra("acc");
                esservicelistener45.onGetAlbumListComplete(rid);
            }
        	break;
        case 51:
        	Iterator iterator76 = sListeners.iterator();
            while(iterator76.hasNext()) 
            {
                EsServiceListener esservicelistener44 = (EsServiceListener)iterator76.next();
                intent.getParcelableExtra("acc");
                esservicelistener44.onGetAlbumComplete(rid, serviceresult);
            }
        	break;
        case 52:
        	Iterator iterator75 = sListeners.iterator();
            while(iterator75.hasNext()) 
            {
                EsServiceListener esservicelistener43 = (EsServiceListener)iterator75.next();
                intent.getParcelableExtra("acc");
                esservicelistener43.onGetStreamPhotosComplete(rid, serviceresult);
            }
        	break;
        case 53:
        	Iterator iterator74 = sListeners.iterator();
            while(iterator74.hasNext()) 
            {
                EsServiceListener esservicelistener42 = (EsServiceListener)iterator74.next();
                intent.getParcelableExtra("acc");
                esservicelistener42.onCreatePhotoCommentComplete(rid, serviceresult);
            }
        	break;
        case 54:
        	Iterator iterator72 = sListeners.iterator();
            while(iterator72.hasNext()) 
            {
                EsServiceListener esservicelistener40 = (EsServiceListener)iterator72.next();
                intent.getParcelableExtra("acc");
                esservicelistener40.onDeletePhotoCommentsComplete(rid, serviceresult);
            }
        	break;
        case 55:
        	Iterator iterator71 = sListeners.iterator();
            while(iterator71.hasNext()) 
            {
                EsServiceListener esservicelistener39 = (EsServiceListener)iterator71.next();
                intent.getParcelableExtra("acc");
                esservicelistener39.onReportPhotoCommentsComplete(rid, intent.getStringExtra("comment_id"), intent.getBooleanExtra("is_undo", false), serviceresult);
            }
        	break;
        case 56:
        	boolean flag3 = intent.getBooleanExtra("plus_oned", true);
            intent.getParcelableExtra("acc");
            Iterator iterator70 = sListeners.iterator();
            while(iterator70.hasNext()) 
                ((EsServiceListener)iterator70.next()).onPhotoPlusOneComplete(rid, flag3, serviceresult);
        	break;
        case 57:
        	Iterator iterator69 = sListeners.iterator();
            while(iterator69.hasNext()) 
            {
                EsServiceListener esservicelistener38 = (EsServiceListener)iterator69.next();
                intent.getParcelableExtra("acc");
                esservicelistener38.onGetPhotosOfUserComplete(rid, serviceresult);
            }
        	break;
        case 58:
        	Iterator iterator68 = sListeners.iterator();
            while(iterator68.hasNext()) 
            {
                EsServiceListener esservicelistener37 = (EsServiceListener)iterator68.next();
                intent.getParcelableExtra("acc");
                esservicelistener37.onNameTagApprovalComplete(rid, intent.getLongExtra("photo_id", 0L), serviceresult);
            }
        	break;
        case 59:
        	Iterator iterator67 = sListeners.iterator();
            while(iterator67.hasNext()) 
            {
                EsServiceListener esservicelistener36 = (EsServiceListener)iterator67.next();
                intent.getParcelableExtra("acc");
                esservicelistener36.onTagSuggestionApprovalComplete(rid, intent.getStringExtra("photo_id"), serviceresult);
            }
        	break;
        case 60:
        	intent.getParcelableExtra("acc");
            Iterator iterator66 = sListeners.iterator();
            while(iterator66.hasNext()) 
                ((EsServiceListener)iterator66.next()).onPhotosHomeComplete(rid);
        	break;
        case 61:
        	intent.getParcelableExtra("acc");
            intent.getSerializableExtra("photo_ids");
            Iterator iterator65 = sListeners.iterator();
            while(iterator65.hasNext()) 
                ((EsServiceListener)iterator65.next()).onDeletePhotosComplete(rid, serviceresult);
        	break;
        case 62:
        	intent.getParcelableExtra("acc");
            long l1 = intent.getLongExtra("photo_id", 0L);
            Iterator iterator63 = sListeners.iterator();
            while(iterator63.hasNext()) 
                ((EsServiceListener)iterator63.next()).onGetPhoto(rid, l1);
        	break;
        case 63:
        	ArrayList arraylist = (ArrayList)intent.getSerializableExtra("media_refs");
            Iterator iterator60 = sListeners.iterator();
            while(iterator60.hasNext()) 
                ((EsServiceListener)iterator60.next()).onLocalPhotoDelete(rid, arraylist, serviceresult);
        	break;
        case 64:
        	Iterator iterator73 = sListeners.iterator();
            while(iterator73.hasNext()) 
            {
                EsServiceListener esservicelistener41 = (EsServiceListener)iterator73.next();
                intent.getParcelableExtra("acc");
                esservicelistener41.onEditPhotoCommentComplete(rid, serviceresult);
            }
        	break;
        case 65:
        	intent.getStringExtra("gaia_id");
            Iterator iterator62 = sListeners.iterator();
            while(iterator62.hasNext()) 
                ((EsServiceListener)iterator62.next()).onGetPhotoSettings(rid, false);
        	break;
        case 66:
        	boolean flag2 = intent.getBooleanExtra("full_res", false);
            String s2 = intent.getStringExtra("description");
            java.io.File file = null;
            String s3 = null;
            if(obj != null)
            {
                SavePhotoOperation savephotooperation = (SavePhotoOperation)obj;
                file = savephotooperation.getSaveToFile();
                s3 = savephotooperation.getContentType();
            }
            Iterator iterator61 = sListeners.iterator();
            while(iterator61.hasNext()) 
                ((EsServiceListener)iterator61.next()).onSavePhoto(rid, file, flag2, s2, s3, serviceresult);
        	break;
        case 67:
        	intent.getParcelableExtra("acc");
            intent.getLongExtra("photo_id", 0L);
            Iterator iterator64 = sListeners.iterator();
            while(iterator64.hasNext()) 
                ((EsServiceListener)iterator64.next()).onReportPhotoComplete(rid, serviceresult);
        	break;
        case 201:
        case 203:
        case 206:
        	serviceresult = null;
        	break;
        case 202:
        	EsServiceListener esservicelistener27;
            for(Iterator iterator41 = sListeners.iterator(); iterator41.hasNext(); esservicelistener27.onSyncNotifications(rid, serviceresult))
            {
                esservicelistener27 = (EsServiceListener)iterator41.next();
                intent.getParcelableExtra("acc");
            }

            serviceresult = null;
        	break;
        case 204:
        	Iterator iterator43 = sListeners.iterator();
            while(iterator43.hasNext()) 
                ((EsServiceListener)iterator43.next()).onChangeNotificationsRequestComplete((EsAccount)intent.getParcelableExtra("acc"), serviceresult);
        	break;
        case 205:
        	NotificationSettingsData notificationsettingsdata;
            Iterator iterator42;
            if(obj != null)
                notificationsettingsdata = ((GetNotificationSettingsOperation)obj).getNotificationSettings();
            else
                notificationsettingsdata = null;
            iterator42 = sListeners.iterator();
            while(iterator42.hasNext()) 
                ((EsServiceListener)iterator42.next()).onGetNotificationSettings(rid, (EsAccount)intent.getParcelableExtra("acc"), notificationsettingsdata);
        	break;
        case 500:
        	Iterator iterator23 = sListeners.iterator();
            while(iterator23.hasNext()) 
            {
                EsServiceListener esservicelistener12 = (EsServiceListener)iterator23.next();
                intent.getParcelableExtra("acc");
                esservicelistener12.onCircleSyncComplete(rid, serviceresult);
            }
        	break;
        case 503:
        	serviceresult = null;
        	break;
        case 600:
        	EsAccount esaccount5 = (EsAccount)obj;
            if(esaccount5 != null)
            {
                setActiveAccount(esaccount5);
                EsWidgetUtils.updateAllWidgets(this);
            }
            Iterator iterator40 = sListeners.iterator();
            while(iterator40.hasNext()) 
            {
                EsServiceListener esservicelistener26 = (EsServiceListener)iterator40.next();
                intent.getParcelableExtra("acc");
                esservicelistener26.onOobRequestComplete(rid, serviceresult);
            }
        	break;
        case 702:
        	Iterator iterator34 = sListeners.iterator();
            while(iterator34.hasNext()) 
            {
                EsServiceListener esservicelistener23 = (EsServiceListener)iterator34.next();
                intent.getParcelableExtra("acc");
                esservicelistener23.onSetCircleMembershipComplete(rid, serviceresult);
            }
        	break;
        case 703:
        	Iterator iterator36 = sListeners.iterator();
            while(iterator36.hasNext()) 
            {
                EsServiceListener esservicelistener25 = (EsServiceListener)iterator36.next();
                intent.getParcelableExtra("acc");
                intent.getStringExtra("person_id");
                esservicelistener25.onGetProfileAndContactComplete(rid, serviceresult);
            }
        	break;
        case 704:
        	Iterator iterator35 = sListeners.iterator();
            while(iterator35.hasNext()) 
            {
                EsServiceListener esservicelistener24 = (EsServiceListener)iterator35.next();
                intent.getParcelableExtra("acc");
                esservicelistener24.onMutateProfileComplete(rid, serviceresult);
            }
        	break;
        case 705:
        	Iterator iterator31 = sListeners.iterator();
            while(iterator31.hasNext()) 
            {
                EsServiceListener esservicelistener20 = (EsServiceListener)iterator31.next();
                intent.getParcelableExtra("acc");
                esservicelistener20.onSetMutedRequestComplete(rid, intent.getBooleanExtra("muted", false), serviceresult);
            }
        	break;
        case 706:
        	Iterator iterator30 = sListeners.iterator();
            while(iterator30.hasNext()) 
            {
                EsServiceListener esservicelistener19 = (EsServiceListener)iterator30.next();
                intent.getParcelableExtra("acc");
                esservicelistener19.onSetBlockedRequestComplete(rid, serviceresult);
            }
        	break;
        case 707:
        	Iterator iterator29 = sListeners.iterator();
            while(iterator29.hasNext()) 
            {
                EsServiceListener esservicelistener18 = (EsServiceListener)iterator29.next();
                intent.getParcelableExtra("acc");
                esservicelistener18.onReportAbuseRequestComplete(rid, serviceresult);
            }
        	break;
        case 708:
        	Iterator iterator28 = sListeners.iterator();
            while(iterator28.hasNext()) 
            {
                EsServiceListener esservicelistener17 = (EsServiceListener)iterator28.next();
                intent.getParcelableExtra("acc");
                esservicelistener17.onCreateCircleRequestComplete(rid, serviceresult);
            }
        	break;
        case 709:
        	Iterator iterator25 = sListeners.iterator();
            while(iterator25.hasNext()) 
            {
                EsServiceListener esservicelistener14 = (EsServiceListener)iterator25.next();
                intent.getParcelableExtra("acc");
                esservicelistener14.onDeleteCirclesRequestComplete(rid, serviceresult);
            }
        	break;
        case 710:
        	Iterator iterator33 = sListeners.iterator();
            while(iterator33.hasNext()) 
            {
                EsServiceListener esservicelistener22 = (EsServiceListener)iterator33.next();
                intent.getParcelableExtra("acc");
                esservicelistener22.onRemovePeopleRequestComplete(rid, serviceresult);
            }
        	break;
        case 711:
        	Iterator iterator24 = sListeners.iterator();
            while(iterator24.hasNext()) 
            {
                EsServiceListener esservicelistener13 = (EsServiceListener)iterator24.next();
                intent.getParcelableExtra("acc");
                esservicelistener13.onDismissSuggestedPeopleRequestComplete(rid, serviceresult);
            }
        	break;
        case 712:
        	Iterator iterator32 = sListeners.iterator();
            while(iterator32.hasNext()) 
            {
                EsServiceListener esservicelistener21 = (EsServiceListener)iterator32.next();
                intent.getParcelableExtra("acc");
                esservicelistener21.onAddPeopleToCirclesComplete(rid, serviceresult);
            }
        	break;
        case 713:
        	Iterator iterator20 = sListeners.iterator();
            while(iterator20.hasNext()) 
            {
                EsServiceListener esservicelistener9 = (EsServiceListener)iterator20.next();
                intent.getParcelableExtra("acc");
                esservicelistener9.onSetCoverPhotoComplete(rid, serviceresult);
            }
        	break;
        case 714:
        	Iterator iterator21 = sListeners.iterator();
            while(iterator21.hasNext()) 
            {
                EsServiceListener esservicelistener10 = (EsServiceListener)iterator21.next();
                intent.getParcelableExtra("acc");
                esservicelistener10.onUploadCoverPhotoComplete(rid, serviceresult);
            }
        	break;
        case 715:
        	Iterator iterator22 = sListeners.iterator();
            while(iterator22.hasNext()) 
            {
                EsServiceListener esservicelistener11 = (EsServiceListener)iterator22.next();
                intent.getParcelableExtra("acc");
                esservicelistener11.onUploadProfilePhotoComplete(rid, serviceresult);
            }
        	break;
        case 716:
        	Iterator iterator18 = sListeners.iterator();
            while(iterator18.hasNext()) 
            {
                EsServiceListener esservicelistener7 = (EsServiceListener)iterator18.next();
                intent.getParcelableExtra("acc");
                esservicelistener7.onCreateProfilePlusOneRequestComplete(rid, serviceresult);
            }
        	break;
        case 717:
        	Iterator iterator17 = sListeners.iterator();
            while(iterator17.hasNext()) 
            {
                EsServiceListener esservicelistener6 = (EsServiceListener)iterator17.next();
                intent.getParcelableExtra("acc");
                esservicelistener6.onDeleteProfilePlusOneRequestComplete(rid, serviceresult);
            }
        	break;
        case 718:
        	Iterator iterator27 = sListeners.iterator();
            while(iterator27.hasNext()) 
            {
                EsServiceListener esservicelistener16 = (EsServiceListener)iterator27.next();
                intent.getParcelableExtra("acc");
                esservicelistener16.onModifyCirclePropertiesRequestComplete(rid, serviceresult);
            }
        	break;
        case 719:
        	Iterator iterator26 = sListeners.iterator();
            while(iterator26.hasNext()) 
            {
                EsServiceListener esservicelistener15 = (EsServiceListener)iterator26.next();
                intent.getParcelableExtra("acc");
                esservicelistener15.onSetVolumeControlsRequestComplete(rid, serviceresult);
            }
        	break;
        case 720:
        	Iterator iterator19 = sListeners.iterator();
            while(iterator19.hasNext()) 
            {
                EsServiceListener esservicelistener8 = (EsServiceListener)iterator19.next();
                intent.getParcelableExtra("acc");
                esservicelistener8.onSetScrapbookInfoComplete(rid, serviceresult);
            }
        	break;
        case 721:
        	serviceresult = null;
        	break;
        case 800:
        	String s1 = null;
            if(obj != null)
            {
                UploadMediaResponse uploadmediaresponse = ((UploadMediaOperation)obj).getUploadMediaResponse();
                s1 = null;
                if(uploadmediaresponse != null)
                {
                    DataPhoto dataphoto = uploadmediaresponse.photo;
                    s1 = null;
                    if(dataphoto != null)
                    {
                        DataImage dataimage = uploadmediaresponse.photo.original;
                        s1 = null;
                        if(dataimage != null)
                            s1 = uploadmediaresponse.photo.original.url;
                    }
                }
            }
            Iterator iterator50 = sListeners.iterator();
            while(iterator50.hasNext()) 
            {
                EsServiceListener esservicelistener28 = (EsServiceListener)iterator50.next();
                intent.getParcelableExtra("acc");
                esservicelistener28.onImageThumbnailUploaded(rid, s1);
            }
        	break;
        case 900:
        	Iterator iterator49 = sListeners.iterator();
            while(iterator49.hasNext()) 
                ((EsServiceListener)iterator49.next()).onEventHomeRequestComplete(rid);
        	break;
        case 901:
        	Iterator iterator48 = sListeners.iterator();
            while(iterator48.hasNext()) 
                ((EsServiceListener)iterator48.next()).onGetEventComplete(rid, serviceresult);
        	break;
        case 902:
        	Iterator iterator12 = sListeners.iterator();
            while(iterator12.hasNext()) 
                ((EsServiceListener)iterator12.next()).onSendEventRsvpComplete(rid, serviceresult);
        	break;
        case 903:
        	Iterator iterator11 = sListeners.iterator();
            while(iterator11.hasNext()) 
                ((EsServiceListener)iterator11.next()).onCreateEventComplete(rid, serviceresult);
        	break;
        case 904:
        	Iterator iterator10 = sListeners.iterator();
            while(iterator10.hasNext()) 
                ((EsServiceListener)iterator10.next()).onUpdateEventComplete(rid, serviceresult);
        	break;
        case 906:
        	Iterator iterator8 = sListeners.iterator();
            while(iterator8.hasNext()) 
                iterator8.next();
        	break;
        case 907:
        	Iterator iterator7 = sListeners.iterator();
            while(iterator7.hasNext()) 
                ((EsServiceListener)iterator7.next()).onEventInviteComplete(rid, serviceresult);
        	break;
        case 908:
        	Iterator iterator9 = sListeners.iterator();
            while(iterator9.hasNext()) 
                ((EsServiceListener)iterator9.next()).onDeleteEventComplete(rid, serviceresult);
        	break;
        case 909:
        	Iterator iterator47 = sListeners.iterator();
            while(iterator47.hasNext()) 
                ((EsServiceListener)iterator47.next()).onReadEventComplete(rid, serviceresult);
        	break;
        case 910:
        	Iterator iterator46 = sListeners.iterator();
            while(iterator46.hasNext()) 
                ((EsServiceListener)iterator46.next()).onGetEventInviteesComplete(rid, serviceresult);
        	break;
        case 911:
        	Iterator iterator45 = sListeners.iterator();
            while(iterator45.hasNext()) 
                ((EsServiceListener)iterator45.next()).onSharePhotosToEventComplete(rid, serviceresult);
        	break;
        case 1002:
        	EsAccount esaccount2 = EsAccountsData.getActiveAccount(this);
            if(esaccount2 != null && obj != null)
            {
                OzActions ozactions1;
                if(((Boolean)obj).booleanValue())
                    ozactions1 = OzActions.CAMERA_SYNC_ENABLED;
                else
                    ozactions1 = OzActions.CAMERA_SYNC_DISABLED;
                EsAnalytics.recordActionEvent(this, esaccount2, ozactions1, OzViews.GENERAL_SETTINGS);
            }
            serviceresult = null;
        	break;
        case 1004:
        	for(Iterator iterator39 = sListeners.iterator(); iterator39.hasNext(); intent.getStringExtra("content"))
            {
                iterator39.next();
                intent.getParcelableExtra("acc");
            }

            EsWidgetUtils.updateAllWidgets(this);
            serviceresult = null;
        	break;
        case 1005:
        	serviceresult = null;
        	break;
        case 1008:
        	if(intent.getBooleanExtra("analytics_sync", false))
            {
                EsAccount esaccount4 = (EsAccount)intent.getParcelableExtra("acc");
                Intent intent2 = sIntentPool.get(this, EsService.class);
                intent2.putExtra("op", 1010);
                intent2.putExtra("acc", esaccount4);
                startCommand(this, intent2);
            }
            if(intent.hasExtra("analytics_sync"))
                intent.removeExtra("analytics_sync");
            serviceresult = null;
        	break;
        case 1009:
        	Iterator iterator6 = sListeners.iterator();
            while(iterator6.hasNext()) 
                ((EsServiceListener)iterator6.next()).onEventManageGuestComplete(rid, serviceresult);
        	break;
        case 1010:
        	if(null != obj) {
        		PostClientLogsOperation postclientlogsoperation;
    	        int k;
    	        Exception exception;
    	        postclientlogsoperation = (PostClientLogsOperation)obj;
    	        k = postclientlogsoperation.getErrorCode();
    	        exception = postclientlogsoperation.getException();
    	        if(k == 200 && exception == null) {
    	        	if(EsLog.isLoggable("EsService", 3))
    	                Log.d("EsService", "PostClientLogsOperation was successful"); 
    	        } else { 
    	        	Log.e("EsService", (new StringBuilder("PostClientLogsOperation failed ex ")).append(exception).append(" errorCode ").append(k).toString());
        	        List list = postclientlogsoperation.getClientOzEvents();
        	        if(list != null && !list.isEmpty())
        	        {
        	            EsAccount esaccount3 = (EsAccount)intent.getParcelableExtra("acc");
        	            Intent intent1 = sIntentPool.get(this, EsService.class);
        	            intent1.putExtra("op", 1011);
        	            intent1.putExtra("acc", esaccount3);
        	            try
        	            {
        	                intent1.putExtra("analytics_events", DbAnalyticsEvents.serializeClientOzEventList(list));
        	                startCommand(this, intent1);
        	            }
        	            catch(IOException ioexception)
        	            {
        	                Log.e("EsService", (new StringBuilder("insertAnalyticsEvents: Failed to serialize the analytics events. ")).append(ioexception).toString());
        	            }
        	            if(EsLog.isLoggable("EsService", 3))
        	                Log.d("EsService", (new StringBuilder("Insert ")).append(list.size()).append(" analytics events back to the database").toString());
        	        }
    	        }
        	}
        	
        	serviceresult = null;
        	break;
        case 1011:
        	serviceresult = null;
        	break;
        case 1100:
        	Iterator iterator16 = sListeners.iterator();
            while(iterator16.hasNext()) 
            {
                EsServiceListener esservicelistener5 = (EsServiceListener)iterator16.next();
                intent.getParcelableExtra("acc");
                esservicelistener5.onSearchActivitiesComplete(rid, serviceresult);
            }
        	break;
        case 1110:
        	serviceresult = null;
        	break;
        case 1120:
        	Iterator iterator15 = sListeners.iterator();
            while(iterator15.hasNext()) 
            {
                EsServiceListener esservicelistener4 = (EsServiceListener)iterator15.next();
                intent.getParcelableExtra("acc");
                esservicelistener4.onInsertCameraPhotoComplete(rid, serviceresult);
            }
        	break;
        case 2000:
        case 2001:
        	serviceresult = null;
        	break;
        case 2200:
        	for(Iterator iterator14 = sListeners.iterator(); iterator14.hasNext(); ((EsServiceListener)iterator14.next()).onPostActivityResult(rid, serviceresult));
            if(serviceresult.hasError())
            {
                AnalyticsInfo analyticsinfo1 = (AnalyticsInfo)intent.getSerializableExtra("analytics");
                EsAccount esaccount1 = (EsAccount)intent.getParcelableExtra("acc");
                if(analyticsinfo1 != null && esaccount1 != null)
                    EsAnalytics.postRecordEvent(this, esaccount1, analyticsinfo1, OzActions.PLATFORM_SHARE_POST_ERROR);
            }
        	break;
        case 2201:
        	for(Iterator iterator13 = sListeners.iterator(); iterator13.hasNext(); ((EsServiceListener)iterator13.next()).onPlusOneApplyResult(rid, serviceresult));
            OzActions ozactions;
            AnalyticsInfo analyticsinfo;
            EsAccount esaccount;
            if(!serviceresult.hasError())
                ozactions = OzActions.PLATFORM_WRITE_PLUSONE;
            else
                ozactions = OzActions.PLATFORM_WRITE_PLUSONE_ERROR;
            analyticsinfo = (AnalyticsInfo)intent.getSerializableExtra("analytics");
            esaccount = (EsAccount)intent.getParcelableExtra("acc");
            if(analyticsinfo != null && ozactions != null)
                EsAnalytics.postRecordEvent(this, esaccount, analyticsinfo, ozactions);
        	break;
        case 2300:
        case 2301:
        case 2400:
        case 2600:
        	break;
        case 2500:
        	Iterator iterator5 = sListeners.iterator();
            while(iterator5.hasNext()) 
                ((EsServiceListener)iterator5.next()).onWriteReviewComplete(rid, serviceresult);
        	break;
        case 2501:
        	Iterator iterator4 = sListeners.iterator();
            while(iterator4.hasNext()) 
                ((EsServiceListener)iterator4.next()).onDeleteReviewComplete(rid, serviceresult);
        	break;
        case 2700:
        	Iterator iterator3 = sListeners.iterator();
            while(iterator3.hasNext()) 
            {
                EsServiceListener esservicelistener3 = (EsServiceListener)iterator3.next();
                intent.getParcelableExtra("acc");
                esservicelistener3.onGetSquaresComplete(rid, serviceresult);
            }
        	break;
        case 2701:
        	Iterator iterator2 = sListeners.iterator();
            while(iterator2.hasNext()) 
            {
                EsServiceListener esservicelistener2 = (EsServiceListener)iterator2.next();
                intent.getParcelableExtra("acc");
                esservicelistener2.onGetSquareComplete(rid, serviceresult);
            }
        	break;
        case 2702:
        	boolean flag;
            Iterator iterator1;
            if(obj != null)
                flag = ((EditSquareMembershipOperation)obj).getIsBlockingModerator();
            else
                flag = false;
            iterator1 = sListeners.iterator();
            while(iterator1.hasNext()) 
            {
                EsServiceListener esservicelistener1 = (EsServiceListener)iterator1.next();
                intent.getParcelableExtra("acc");
                esservicelistener1.onEditSquareMembershipComplete(rid, flag, serviceresult);
            }
        	break;
        case 2703:
        	AudienceData audiencedata;
            if(obj != null)
                audiencedata = ((ReadSquareMembersOperation)obj).getSquareMembers();
            else
                audiencedata = null;
            Iterator iterator = sListeners.iterator();
            while(iterator.hasNext()) 
            {
                EsServiceListener esservicelistener = (EsServiceListener)iterator.next();
                intent.getParcelableExtra("acc");
                esservicelistener.onReadSquareMembersComplete(rid, audiencedata, serviceresult);
            }
        	break;
        case 2704:
        	serviceresult = null;
        	break;
        default:
        	Log.e("EsService", (new StringBuilder("onIntentProcessed: Unhandled op code: ")).append(op).toString());
        	break;
        }
        
        if(intent.hasExtra("rid"))
        {
            Integer integer = Integer.valueOf(intent.getIntExtra("rid", -1));
            sPendingIntents.remove(integer);
            if(serviceresult != null)
                sResults.put(integer, serviceresult);
            if(intent.getBooleanExtra("from_pool", false))
                sIntentPool.put(intent);
        }
        if(sPendingIntents.size() == 0)
        {
            sHandler.removeCallbacks(mStopRunnable);
            sHandler.postDelayed(mStopRunnable, 5000L);
            if(EsLog.isLoggable("EsService", 3))
                Log.d("EsService", "completeRequest: Stopping service in 5000 ms");
        }
        
    }

    @Override
	public void onCreate() {
		super.onCreate();
		if(sHandler == null)
        {
            sHandler = new Handler(Looper.getMainLooper());
            sPeopleDataFactory = PeopleData.getFactory();
            sMuteUserOperationFactory = MuteUserOperation.getFactory();
            sBlockUserOperationFactory = BlockUserOperation.getFactory();
        }
        mServiceThread = new ServiceThread(sHandler, "ServiceThread", this);
        mServiceThread.start();
	}
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void onDestroy()
    {
        super.onDestroy();
        if(mServiceThread != null)
        {
            mServiceThread.quit();
            mServiceThread = null;
        }
    }
	
	public final void onServiceThreadEnd() {
    }
	
	public final void processIntent(Intent intent) {
        Context context = getApplicationContext();
        int i = intent.getIntExtra("op", -1);
        int j = intent.getIntExtra("rid", -1);
        EsAccount esaccount = (EsAccount)intent.getParcelableExtra("acc");
        try {
            if(!processIntent1(context, esaccount, intent, i, j) && !processIntent2(context, esaccount, intent, i))
                throw new IllegalArgumentException((new StringBuilder("Unsupported op code: ")).append(i).toString());
        } catch(Exception e) {
            e.printStackTrace();
            completeRequest(intent, new ServiceResult(0, null, e), null);
        }
    }
	
	public static int addAccount(Context context, EsAccount esaccount, String s) {
		Intent intent = sIntentPool.get(context, EsService.class);
		intent.putExtra("op", 1);
		intent.putExtra("oob_origin", s);
		intent.putExtra("acc", esaccount);
		return startCommand(context, intent);
	}
	
	public static final int declineSquareInvitation(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2704);
        intent.putExtra("acc", esaccount);
        intent.putExtra("square_id", s);
        return startCommand(context, intent);
    }
	
	public static MobileOutOfBoxResponse removeIncompleteOutOfBoxResponse(int i) {
		MobileOutOfBoxResponse mobileoutofboxresponse = removeOutOfBoxResponse(i);
		if (mobileoutofboxresponse != null
				&& mobileoutofboxresponse.signupComplete != null
				&& mobileoutofboxresponse.signupComplete.booleanValue())
			mobileoutofboxresponse = null;
		return mobileoutofboxresponse;
	}
	
	public static MobileOutOfBoxResponse removeOutOfBoxResponse(int i) {
		return (MobileOutOfBoxResponse) sOutOfBoxResponses.remove(Integer
				.valueOf(i));
	}
	
	public static AccountSettingsData removeAccountSettingsResponse(int i) {
		return (AccountSettingsData) sAccountSettingsResponses.remove(Integer
				.valueOf(i));
	}
	
	public static void registerListener(EsServiceListener esservicelistener)
    {
        sListeners.add(esservicelistener);
    }
	
	public static void unregisterListener(EsServiceListener esservicelistener) {
		sListeners.remove(esservicelistener);
	}
	
	public static ServiceResult removeResult(int i)
    {
        return (ServiceResult)sResults.remove(Integer.valueOf(i));
    }
	
	public static boolean isOutOfBoxError(Throwable throwable)
    {
        boolean flag;
        if((throwable instanceof OzServerException) && ((OzServerException)throwable).getErrorCode() == 9)
            flag = true;
        else
            flag = false;
        return flag;
    }
	
	public static int upgradeAccount(Context context, EsAccount esaccount)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 6);
        intent.putExtra("acc", esaccount);
        return startCommand(context, intent);
    }
	
	public static int postActivity(Context context, EsAccount esaccount, AnalyticsInfo analyticsinfo, ApiaryApiInfo apiaryapiinfo, ApiaryActivity apiaryactivity, AudienceData audiencedata, String s, String s1, 
            List arraylist, DbLocation dblocation, String s2, boolean flag, BirthdayData birthdaydata, DbEmbedEmotishare dbembedemotishare)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2200);
        intent.putExtra("acc", esaccount);
        intent.putExtra("analytics", analyticsinfo);
        intent.putExtra("apiInfo", apiaryapiinfo);
        intent.putExtra("activity", apiaryactivity);
        intent.putExtra("audience", audiencedata);
        intent.putExtra("external_id", s);
        intent.putExtra("content", s1);
        intent.putExtra("loc", dblocation);
        intent.putExtra("content_deep_link_id", s2);
        intent.putExtra("save_post_acl", flag);
        intent.putExtra("birthdata", birthdaydata);
        intent.putExtra("emotishare_embed", dbembedemotishare);
        if(arraylist != null)
            intent.putParcelableArrayListExtra("media", (ArrayList)arraylist);
        return startCommand(context, intent);
    }
	
	public static Integer getProfileAndContact(Context context, EsAccount esaccount, String s, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 703);
        intent.putExtra("acc", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("refresh", true);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static int setScrapbookInfo(Context context, EsAccount esaccount, String s, int i, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 720);
        intent.putExtra("acc", esaccount);
        intent.putExtra("photo_id", s);
        intent.putExtra("top_offset", i);
        intent.putExtra("layout", s1);
        return startCommand(context, intent);
    }
	
	public static boolean isProfilePlusOnePending(String s)
    {
		Bundle bundle;
		int i;
        for(Iterator iterator = sPendingIntents.values().iterator(); iterator.hasNext();) {
        	bundle = ((Intent)iterator.next()).getExtras();
            i = bundle.getInt("op");
            if(i != 716 && i != 717 || !TextUtils.equals(bundle.getString("gaia_id"), s)) {
            	continue;
            }
            return true;
        }
        
        return false;
    }
	
	public int onStartCommand(Intent intent, int i, int j)
    {
        if(intent != null)
            mServiceThread.put(intent);
        return 2;
    }
	
	public static int applyPlusOne(Context context, EsAccount esaccount, AnalyticsInfo analyticsinfo, ApiaryApiInfo apiaryapiinfo, String s, boolean flag, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 2201);
        intent.putExtra("acc", esaccount);
        intent.putExtra("analytics", analyticsinfo);
        intent.putExtra("apiInfo", apiaryapiinfo);
        intent.putExtra("url", s);
        intent.putExtra("applyPlusOne", flag);
        intent.putExtra("token", s1);
        return startCommand(context, intent);
    }
	
	private static int startCommand(Context context, Intent intent)
    {
        if(Looper.getMainLooper().getThread() != Thread.currentThread())
            throw new RuntimeException("startCommand must be called on the UI thread");
        int i = generateRequestId();
        intent.putExtra("rid", i);
        Integer integer = getPendingRequestId(intent);
        if(integer != null)
        {
            sIntentPool.put(intent);
            i = integer.intValue();
        } else
        {
            sPendingIntents.put(Integer.valueOf(i), intent);
            context.startService(intent);
        }
        return i;
    }
	
	private static int generateRequestId()
    {
        int i;
        synchronized(sLastRequestId)
        {
            Integer integer1 = sLastRequestId;
            sLastRequestId = Integer.valueOf(1 + sLastRequestId.intValue());
            i = integer1.intValue();
        }
        return i;
    }
	
	private static Integer getPendingRequestId(Intent intent) {
		// TODO
		return null;
    }
	
	public static Integer setPersonBlocked(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 706);
        intent.putExtra("acc", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("person_name", s1);
        intent.putExtra("blocked", flag);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static Integer markActivitiesAsRead(Context context, EsAccount esaccount, String as[]) {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 25);
        intent.putExtra("acc", esaccount);
        intent.putExtra("aid", as);
        OzViews ozviews = OzViews.getViewForLogging(context);
        if(ozviews != null)
            intent.putExtra("start_view", ozviews.ordinal());
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static Integer reportProfileAbuse(Context context, EsAccount esaccount, String s, String s1)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 707);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        intent.putExtra("abuse_type", s1);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static int setCoverPhoto(Context context, EsAccount esaccount, String s, int i, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 713);
        intent.putExtra("acc", esaccount);
        intent.putExtra("photo_id", s);
        intent.putExtra("top_offset", i);
        intent.putExtra("is_gallery_photo", flag);
        return startCommand(context, intent);
    }
	
	public static int uploadCoverPhoto(Context context, EsAccount esaccount, byte abyte0[], int i)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 714);
        intent.putExtra("acc", esaccount);
        intent.putExtra("album_id", "scrapbook");
        intent.putExtra("data", abyte0);
        intent.putExtra("top_offset", i);
        return startCommand(context, intent);
    }
	
	public static Integer setPersonMuted(Context context, EsAccount esaccount, String s, boolean flag)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 705);
        intent.putExtra("acc", esaccount);
        intent.putExtra("person_id", s);
        intent.putExtra("muted", flag);
        return Integer.valueOf(startCommand(context, intent));
    }
	
	public static int createProfilePlusOne(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 716);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        return startCommand(context, intent);
    }
	
	public static int deleteProfilePlusOne(Context context, EsAccount esaccount, String s)
    {
        Intent intent = sIntentPool.get(context, EsService.class);
        intent.putExtra("op", 717);
        intent.putExtra("acc", esaccount);
        intent.putExtra("gaia_id", s);
        return startCommand(context, intent);
    }
	
	public static String getLastCameraMediaLocation() {
        return sLastCameraMediaLocation;
    }
	
	
	protected static void doDeclineSquareInvitation(Context context, EsAccount esaccount, Intent intent) throws IOException {
        String s = intent.getStringExtra("square_id");
        EsSquaresData.dismissSquareInvitation(context, esaccount, s);
        EditSquareMembershipOperation editsquaremembershipoperation = new EditSquareMembershipOperation(context, esaccount, s, "DECLINE_INVITATION", null, null);
        editsquaremembershipoperation.start();
        editsquaremembershipoperation.logAndThrowExceptionIfFailed("EsService");
    }
	
	private boolean fetchC2dmId() throws Exception
    {
        BlockingC2DMClient blockingc2dmclient = new BlockingC2DMClient(30000L);
        blockingc2dmclient.blockingGetC2dmToken(this);
        boolean flag;
        if(!blockingc2dmclient.hasError())
            flag = true;
        else
            flag = false;
        return flag;
    }
	
	private static void putAccountSettingsResponse(int i, AccountSettingsData accountsettingsdata)
    {
        sAccountSettingsResponses.put(Integer.valueOf(i), accountsettingsdata);
    }
	
	private static void updateEsApiProvider(Context context, String s)
    {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("account", s);
        context.getContentResolver().update(Uri.parse("content://com.galaxy.meetup.client.android.content.ApiProvider/account"), contentvalues, null, null);
    }
	
	private static void putOutOfBoxResponse(int i, MobileOutOfBoxResponse mobileoutofboxresponse)
    {
        sOutOfBoxResponses.put(Integer.valueOf(i), mobileoutofboxresponse);
    }
	
	public static void updateNotifications(final Context context, final EsAccount esaccount)
    {
        (new Thread(new Runnable() {

            public final void run()
            {
                AndroidNotification.update(context, esaccount);
            }

        })).start();
    }
	
	protected static void doReportAbuse(Context context, EsAccount esaccount, Intent intent) throws IOException {
		
        ReportProfileAbuseOperation reportprofileabuseoperation = new ReportProfileAbuseOperation(context, esaccount, intent.getStringExtra("gaia_id"), intent.getStringExtra("abuse_type"), null, null);
        reportprofileabuseoperation.start();
        Exception exception = reportprofileabuseoperation.getException();
        if(exception != null && android.os.Build.VERSION.SDK_INT >= 9)
            throw new IOException("Could not report abuse", exception);
        if(reportprofileabuseoperation.hasError())
        {
            throw new IOException((new StringBuilder("Could not report abuse: ")).append(reportprofileabuseoperation.getErrorCode()).toString());
        } else
        {
            EsSyncAdapterService.SyncState syncstate = new EsSyncAdapterService.SyncState();
            syncstate.onSyncStart("Post-report-abuse sync");
            EsPeopleData.syncPeople(context, esaccount, syncstate, null, true);
            syncstate.onFinish();
            return;
        }
	}
	
	protected static void doDeleteCircles(Context context, EsAccount esaccount, ArrayList arraylist) throws IOException {
		DeleteCirclesOperation deletecirclesoperation = new DeleteCirclesOperation(context, esaccount, arraylist, null, null);
		deletecirclesoperation.start();
		Exception exception = deletecirclesoperation.getException();
		if (exception != null && android.os.Build.VERSION.SDK_INT >= 9)
			throw new IOException("Could not delete circles", exception);
		if (deletecirclesoperation.hasError()) {
			throw new IOException((new StringBuilder(
					"Could not delete circles: ")).append(
					deletecirclesoperation.getErrorCode()).toString());
		} else {
			EsSyncAdapterService.SyncState syncstate = new EsSyncAdapterService.SyncState();
			syncstate.onSyncStart("People sync after circle deletion");
			EsPeopleData.syncPeople(context, esaccount, syncstate, null, true);
			syncstate.onSyncFinish();
			AndroidContactsSync.requestSync(context);
			return;
		}
	}
	
	protected static void doDismissSuggestedPeople(Context context, EsAccount esaccount, Intent intent) throws IOException {
        String s = intent.getStringExtra("suggestions_ui");
        ArrayList arraylist = intent.getStringArrayListExtra("person_ids");
        ArrayList arraylist1 = intent.getStringArrayListExtra("suggestion_ids");
        EsPeopleData.deleteFromSuggestedPeople(context, esaccount, arraylist);
        synchronized(sDismissPeopleLock) {
	        RecordSuggestionActionOperation recordsuggestionactionoperation = new RecordSuggestionActionOperation(context, esaccount, s, arraylist, arraylist1, "REJECT", null, null);
	        recordsuggestionactionoperation.start();
	        recordsuggestionactionoperation.logAndThrowExceptionIfFailed("EsService");
	        int i = 0;
	        int j;
	        for(Iterator iterator = sPendingIntents.values().iterator(); iterator.hasNext();) {
		        if(((Intent)iterator.next()).getExtras().getInt("op") == 711)
		        {
		            j = i + 1;
		            break;
		        } else {
		        	j = i;
			        i = j;
		        }
	        }
	        
	        if(i <= 1) {
	        	FindMorePeopleOperation findmorepeopleoperation = new FindMorePeopleOperation(context, esaccount, null, null);
		        findmorepeopleoperation.start();
		        findmorepeopleoperation.logAndThrowExceptionIfFailed("EsService");
	        }
        }
	}
	
	private boolean processIntent1(final Context context, final EsAccount account, final Intent intent, final int i, int j) throws Exception {
		
		boolean flag = false;
		
		final String gaiaId = intent.getStringExtra("gaia_id");
        final String squareStreamId = intent.getStringExtra("square_stream_id");
        final String circleId = intent.getStringExtra("circle_id");
        final String continuationToken = intent.getStringExtra("cont_token");
        final boolean fromWidget = flag;
		
		switch(i) {
		case 1:
			if(!fetchC2dmId() && RealTimeChatService.getStickyC2dmId(context) == null)
	        {
	            completeRequest(intent, new ServiceResult(0, "Failed to get C2DM registration.", null), account);
	            flag = true;
	        } else {
		        AuthData.invalidateAuthToken(context, account.getName(), "webupdates");
		        GetSettingsOperation getsettingsoperation2 = new GetSettingsOperation(context, account, true, null, null);
		        getsettingsoperation2.start();
		        EsAccount esaccount4;
		        if(!getsettingsoperation2.hasError())
		        {
		            if(getsettingsoperation2.hasPlusPages())
		            {
		                putAccountSettingsResponse(j, getsettingsoperation2.getAccountSettings());
		                esaccount4 = account;
		            } else
		            {
		                esaccount4 = EsAccountsData.getAccountByName(context, account.getName());
		                updateEsApiProvider(context, esaccount4.getName());
		            }
		        } else
		        if(isOutOfBoxError(getsettingsoperation2.getException()))
		            esaccount4 = account;
		        else
		            esaccount4 = null;
		        if(esaccount4 != null)
		        {
		            EsAccountsData.syncExperiments(context, account);
		            if(isOutOfBoxError(getsettingsoperation2.getException()))
		            {
		                String s70 = intent.getStringExtra("oob_origin");
		                MobileOutOfBoxRequest mobileoutofboxrequest1 = new MobileOutOfBoxRequest();
		                mobileoutofboxrequest1.upgradeOrigin = s70;
		                OutOfBoxOperation outofboxoperation1 = new OutOfBoxOperation(context, esaccount4, mobileoutofboxrequest1, null, null);
		                outofboxoperation1.start();
		                putOutOfBoxResponse(j, outofboxoperation1.getResponse());
		                completeRequest(intent, new ServiceResult(outofboxoperation1), esaccount4);
		            } else
		            {
		                completeRequest(intent, new ServiceResult(getsettingsoperation2), esaccount4);
		            }
		        } else
		        {
		            completeRequest(intent, new ServiceResult(getsettingsoperation2), null);
		        }
	        }
			flag = true;
			break;
		case 3:
			EsAccountsData.deactivateAccount(context, account.getName(), false);
	        updateEsApiProvider(context, null);
	        completeRequest(intent, new ServiceResult(), null);
	        flag = true;
			break;
		case 6:
			if(!fetchC2dmId() && RealTimeChatService.getStickyC2dmId(context) == null)
	        {
	            completeRequest(intent, new ServiceResult(0, "Failed to get C2DM registration.", null), null);
	        } else
	        {
	            EsAccountsData.upgradeAccount(context, account);
	            completeRequest(intent, new ServiceResult(), null);
	        }
			flag = true;
			break;
		case 7:
			String s67 = intent.getStringExtra("gaia_id");
	        String s68 = intent.getStringExtra("display_name");
	        String s69 = intent.getStringExtra("photo_url");
	        boolean flag16 = intent.getBooleanExtra("is_plus_page", false);
	        AccountSettingsData accountsettingsdata = (AccountSettingsData)intent.getParcelableExtra("account_settings");
	        if(flag16)
	        {
	            EsAccount esaccount1 = new EsAccount(account.getName(), s67, s68, false, true, -1);
	            GetSettingsOperation getsettingsoperation1 = new GetSettingsOperation(context, esaccount1, true, null, null);
	            getsettingsoperation1.start();
	            if(getsettingsoperation1.hasError() || getsettingsoperation1.getException() != null)
	            {
	                completeRequest(intent, new ServiceResult(getsettingsoperation1), null);
	            } else
	            {
	                EsAccount esaccount2 = EsAccountsData.getAccountByName(context, account.getName());
	                updateEsApiProvider(context, esaccount2.getName());
	                completeRequest(intent, new ServiceResult(getsettingsoperation1), esaccount2);
	            }
	        } else
	        {
	            EsAccount esaccount3 = EsAccountsData.insertAccount(context, s67, account.getName(), s68, accountsettingsdata.isChild(), flag16);
	            EsAccountsData.activateAccount(context, esaccount3, s69);
	            EsAccountsData.saveServerSettings(context, esaccount3, accountsettingsdata);
	            updateEsApiProvider(context, esaccount3.getName());
	            completeRequest(intent, new ServiceResult(), esaccount3);
	        }
	        flag = true;
			break;
		case 11:
			(new GetActivityOperation(context, account, intent.getStringExtra("aid"), null, intent.getStringExtra("square_id"), intent, mOperationListener)).startThreaded();
			flag = true;
			break;
		case 12:
			(new GetAudienceOperation(context, account, intent.getStringExtra("aid"), intent, mOperationListener)).startThreaded();
			flag = true;
			break;
		case 14:
			String s61 = intent.getStringExtra("aid");
	        String s62 = intent.getStringExtra("content");
	        boolean flag15 = intent.getBooleanExtra("reshare", false);
	        (new EditActivityOperation(context, account, intent, mOperationListener, s61, s62, flag15)).startThreaded();
	        flag = true;
	        break;
		case 16:
			String s58 = intent.getStringExtra("aid");
	        PostOptimisticPlusOneOperation postoptimisticplusoneoperation1 = new PostOptimisticPlusOneOperation(context, account, intent, mOperationListener, s58, true);
	        postoptimisticplusoneoperation1.startThreaded();
	        flag = true;
			break;
		case 17:
			String s57 = intent.getStringExtra("aid");
	        PostOptimisticPlusOneOperation postoptimisticplusoneoperation = new PostOptimisticPlusOneOperation(context, account, intent, mOperationListener, s57, false);
	        postoptimisticplusoneoperation.startThreaded();
	        flag = true;
			break;
		case 18:
			String s55 = intent.getStringExtra("aid");
	        boolean flag14 = intent.getBooleanExtra("mute_state", true);
	        MuteActivityOperation muteactivityoperation = new MuteActivityOperation(context, account, intent, mOperationListener, s55, flag14);
	        muteactivityoperation.startThreaded();
	        flag = true;
			break;
		case 19:
			String s53 = intent.getStringExtra("aid");
	        String s54 = intent.getStringExtra("source_stream_id");
	        ReportAbuseActivityOperation reportabuseactivityoperation2 = new ReportAbuseActivityOperation(context, account, intent, mOperationListener, s53, s54, "SPAM");
	        reportabuseactivityoperation2.startThreaded();
	        flag = true;
			break;
		case 20:
			String s56 = intent.getStringExtra("aid");
	        (new DeleteActivityOperation(context, account, intent, mOperationListener, s56)).startThreaded();
	        flag = true;
			break;
		case 21:
			String s59 = intent.getStringExtra("aid");
	        String s60 = intent.getStringExtra("content");
	        AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
	        ReshareActivityOperation reshareactivityoperation = new ReshareActivityOperation(context, account, intent, mOperationListener, s59, s60, audiencedata);
	        reshareactivityoperation.startThreaded();
	        flag = true;
			break;
		case 22:
			final DbLocation location = (DbLocation)intent.getParcelableExtra("loc");
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                android.os.Process.setThreadPriority(10);
	                EsService.access$300(EsService.this, context, account, location, intent.getStringExtra("cont_token"), intent);
	            }
	        })).start();
			flag = true;
			break;
		case 23:
	        (new Thread(new Runnable() {

	            public final void run()
	            {
	                android.os.Process.setThreadPriority(10);
	                EsService.access$400(EsService.this, context, account, i, circleId, gaiaId, squareStreamId, continuationToken, intent, fromWidget);
	            }

	        })).start();
	        flag = true;
			break;
		case 25:
			String as3[] = intent.getStringArrayExtra("aid");
	        MarkItemReadOperation markitemreadoperation1 = new MarkItemReadOperation(context, account, intent, mOperationListener, Arrays.asList(as3), false);
	        markitemreadoperation1.startThreaded();
	        flag = true;
			break;
		case 30:
			String s20 = intent.getStringExtra("aid");
	        String s21 = intent.getStringExtra("content");
	        (new PostCommentStreamOperation(context, account, intent, mOperationListener, s20, s21)).startThreaded();
	        flag = true;
			break;
		case 31:
			String s17 = intent.getStringExtra("aid");
	        String s18 = intent.getStringExtra("content");
	        String s19 = intent.getStringExtra("event_id");
	        intent.getStringExtra("auth_key");
	        PostEventCommentOperation posteventcommentoperation = new PostEventCommentOperation(context, account, intent, mOperationListener, s17, s19, s18);
	        posteventcommentoperation.startThreaded();
	        flag = true;
			break;
		case 32:
			String s14 = intent.getStringExtra("aid");
	        String s15 = intent.getStringExtra("comment_id");
	        String s16 = intent.getStringExtra("content");
	        EditCommentStreamOperation editcommentstreamoperation = new EditCommentStreamOperation(context, account, intent, mOperationListener, s14, s15, s16);
	        editcommentstreamoperation.startThreaded();
	        flag = true;
			break;
		case 33:
			String s13 = intent.getStringExtra("comment_id");
	        DeleteCommentOperation deletecommentoperation = new DeleteCommentOperation(context, account, intent, mOperationListener, s13);
	        deletecommentoperation.startThreaded();
	        flag = true;
			break;
		case 34:
			String s11 = intent.getStringExtra("aid");
	        String s12 = intent.getStringExtra("comment_id");
	        boolean flag5 = intent.getBooleanExtra("delete", false);
	        boolean flag6 = intent.getBooleanExtra("is_undo", false);
	        ReportAbuseActivityOperation reportabuseactivityoperation = new ReportAbuseActivityOperation(context, account, intent, mOperationListener, s11, s12, flag5, flag6);
	        reportabuseactivityoperation.startThreaded();
	        flag = true;
			break;
		case 35:
			String s9 = intent.getStringExtra("aid");
	        long l1 = intent.getLongExtra("photo_id", 0L);
	        String s10 = intent.getStringExtra("comment_id");
	        boolean flag4 = intent.getBooleanExtra("plus_oned", false);
	        CommentOptimisticPlusOneOperation commentoptimisticplusoneoperation = new CommentOptimisticPlusOneOperation(context, account, intent, mOperationListener, s9, l1, s10, flag4);
	        commentoptimisticplusoneoperation.startThreaded();
	        flag = true;
			break;
		case 41:
			if(mStapToPlaceOperation != null && !mStapToPlaceOperation.isAborted())
	            mStapToPlaceOperation.abort();
	        LocationQuery locationquery = (LocationQuery)intent.getParcelableExtra("loc_query");
	        DbLocation dblocation = (DbLocation)intent.getParcelableExtra("loc");
	        mStapToPlaceOperation = new SnapToPlaceOperation(context, account, intent, mOperationListener, locationquery, dblocation, true);
	        mStapToPlaceOperation.startThreaded();
	        flag = true;
			break;
		case 50:
			String s52 = intent.getStringExtra("gaia_id");
	        UserPhotoAlbumsOperation userphotoalbumsoperation = new UserPhotoAlbumsOperation(context, account, s52, intent, mOperationListener);
	        userphotoalbumsoperation.startThreaded();
	        flag = true;
			break;
		case 51:
			String s49 = intent.getStringExtra("gaia_id");
	        String s50 = intent.getStringExtra("album_id");
	        String s51 = intent.getStringExtra("auth_key");
	        PhotosInAlbumOperation photosinalbumoperation1 = new PhotosInAlbumOperation(context, account, s50, s49, intent, mOperationListener, s51);
	        photosinalbumoperation1.startThreaded();
	        flag = true;
			break;
		case 52:
			String s46 = intent.getStringExtra("gaia_id");
	        String s47 = intent.getStringExtra("stream_id");
	        String s48 = intent.getStringExtra("auth_key");
	        PhotosInAlbumOperation photosinalbumoperation = new PhotosInAlbumOperation(context, account, s47, s46, intent, mOperationListener, s48);
	        photosinalbumoperation.startThreaded();
	        flag = true;
			break;
		case 53:
			String s43 = intent.getStringExtra("text");
	        long l8 = intent.getLongExtra("photo_id", 0L);
	        String s44 = intent.getStringExtra("gaia_id");
	        String s45 = intent.getStringExtra("auth_key");
	        PhotosCreateCommentOperation photoscreatecommentoperation = new PhotosCreateCommentOperation(context, account, l8, s44, s43, s45, intent, mOperationListener);
	        photoscreatecommentoperation.startThreaded();
	        flag = true;
			break;
		case 54:
			String s39 = intent.getStringExtra("comment_id");
	        DeleteCommentOperation deletecommentoperation1 = new DeleteCommentOperation(context, account, intent, mOperationListener, s39);
	        deletecommentoperation1.startThreaded();
	        flag = true;
			break;
		case 55:
			String s38 = intent.getStringExtra("comment_id");
	        boolean flag12 = intent.getBooleanExtra("delete", false);
	        boolean flag13 = intent.getBooleanExtra("is_undo", false);
	        ReportAbuseActivityOperation reportabuseactivityoperation1 = new ReportAbuseActivityOperation(context, account, intent, mOperationListener, null, s38, flag12, flag13);
	        reportabuseactivityoperation1.startThreaded();
	        flag = true;
			break;
		case 56:
			String s36 = intent.getStringExtra("gaia_id");
	        String s37 = intent.getStringExtra("album_id");
	        long l6 = intent.getLongExtra("photo_id", 0L);
	        boolean flag11 = intent.getBooleanExtra("plus_oned", true);
	        long l7;
	        PhotosPlusOneOperation photosplusoneoperation;
	        if(s37 == null)
	            l7 = 0L;
	        else
	            l7 = Long.parseLong(s37);
	        photosplusoneoperation = new PhotosPlusOneOperation(context, account, l6, s36, l7, flag11, intent, mOperationListener);
	        photosplusoneoperation.startThreaded();
	        flag = true;
			break;
		case 57:
			String s35 = intent.getStringExtra("gaia_id");
	        PhotosOfUserOperation photosofuseroperation = new PhotosOfUserOperation(context, account, s35, intent, mOperationListener);
	        photosofuseroperation.startThreaded();
	        flag = true;
			break;
		case 58:
			String s34 = intent.getStringExtra("gaia_id");
	        long l4 = intent.getLongExtra("photo_id", 0L);
	        long l5 = intent.getLongExtra("shape_id", 0L);
	        boolean flag10 = intent.getBooleanExtra("approved", false);
	        PhotosNameTagApprovalOperation photosnametagapprovaloperation = new PhotosNameTagApprovalOperation(context, account, l4, s34, l5, flag10, intent, mOperationListener);
	        photosnametagapprovaloperation.startThreaded();
	        flag = true;
			break;
		case 59:
			String s30 = intent.getStringExtra("gaia_id");
	        String s31 = intent.getStringExtra("taggee_id");
	        String s32 = intent.getStringExtra("photo_id");
	        String s33 = intent.getStringExtra("shape_id");
	        boolean flag9 = intent.getBooleanExtra("approved", false);
	        PhotosTagSuggestionApprovalOperation photostagsuggestionapprovaloperation = new PhotosTagSuggestionApprovalOperation(context, account, s30, flag9, s32, s33, s31, intent, mOperationListener);
	        photostagsuggestionapprovaloperation.startThreaded();
	        flag = true;
			break;
		case 60:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                try {
		            	ServiceResult serviceresult1 = null;
		                boolean flag17;
		                serviceresult1 = new ServiceResult();
		                flag17 = true;
		                ServiceResult serviceresult3;
		                PhotosOfUserOperation photosofuseroperation1 = new PhotosOfUserOperation(context, account, null, gaiaId, true, null, null);
		                photosofuseroperation1.start();
		                boolean flag18 = photosofuseroperation1.hasError();
		                int k2 = 0;
		                String s71 = null;
		                if(flag18)
		                {
		                    flag17 = false;
		                    k2 = photosofuseroperation1.getErrorCode();
		                    s71 = photosofuseroperation1.getReasonPhrase();
		                    Log.w("EsService", (new StringBuilder("    #photosHome; failed user photo; code: ")).append(k2).append(", reason: ").append(s71).toString());
		                }
		                UserPhotoAlbumsOperation userphotoalbumsoperation1 = new UserPhotoAlbumsOperation(context, account, null, gaiaId, null, null);
		                userphotoalbumsoperation1.start();
		                if(userphotoalbumsoperation1.hasError())
		                {
		                    flag17 = false;
		                    k2 = userphotoalbumsoperation1.getErrorCode();
		                    s71 = userphotoalbumsoperation1.getReasonPhrase();
		                    Log.w("EsService", (new StringBuilder("    #photosHome; failed photo albums; code: ")).append(k2).append(", reason: ").append(s71).toString());
		                }
		                PhotosInAlbumOperation photosinalbumoperation2 = new PhotosInAlbumOperation(context, account, null, "camerasync", gaiaId, true, null, null);
		                photosinalbumoperation2.start();
		                if(photosinalbumoperation2.hasError())
		                {
		                    flag17 = false;
		                    k2 = photosinalbumoperation2.getErrorCode();
		                    s71 = photosinalbumoperation2.getReasonPhrase();
		                    Log.w("EsService", (new StringBuilder("    #photosHome; failed camera photos; code: ")).append(k2).append(", reason: ").append(s71).toString());
		                }
		                PhotosInAlbumOperation photosinalbumoperation3 = new PhotosInAlbumOperation(context, account, null, "posts", gaiaId, true, null, null);
		                photosinalbumoperation3.start();
		                if(photosinalbumoperation3.hasError())
		                {
		                    flag17 = false;
		                    k2 = photosinalbumoperation3.getErrorCode();
		                    s71 = photosinalbumoperation3.getReasonPhrase();
		                    Log.w("EsService", (new StringBuilder("    #photosHome; failed post photos; code: ")).append(k2).append(", reason: ").append(s71).toString());
		                }
		                if(!flag17) {
		                	serviceresult3 = new ServiceResult(k2, s71, null);
		 	                serviceresult1 = serviceresult3;
		 	                completeRequest(intent, serviceresult1, null);
		                } else {
		                	 completeRequest(intent, serviceresult1, null);
		                }
	                } catch (Exception e) {
	                	ServiceResult serviceresult2 = new ServiceResult(0, null, e);
		                completeRequest(intent, serviceresult2, null);
	                }
	            }})).start();
			flag = true;
			break;
		case 61:
			intent.getStringExtra("gaia_id");
	        ArrayList arraylist4 = (ArrayList)intent.getSerializableExtra("photo_ids");
	        DeletePhotosOperation deletephotosoperation = new DeletePhotosOperation(context, account, arraylist4, intent, mOperationListener);
	        deletephotosoperation.startThreaded();
	        flag = true;
			break;
		case 62:
			String s29 = intent.getStringExtra("gaia_id");
	        long l3 = intent.getLongExtra("photo_id", 0L);
	        GetPhotoOperation getphotooperation = new GetPhotoOperation(context, account, intent, mOperationListener, l3, s29);
	        getphotooperation.startThreaded();
			flag = true;
			break;
		case 63:
			ArrayList arraylist1 = (ArrayList)intent.getSerializableExtra("media_refs");
	        ArrayList arraylist2 = new ArrayList();
	        ArrayList arraylist3 = new ArrayList();
	        StringBuilder stringbuilder = new StringBuilder("media_url IN (");
	        Iterator iterator = arraylist1.iterator();
	        do
	        {
	            if(!iterator.hasNext())
	                break;
	            MediaRef mediaref1 = (MediaRef)iterator.next();
	            if(mediaref1.hasLocalUri())
	            {
	                Uri uri2 = mediaref1.getLocalUri();
	                arraylist2.add(uri2);
	                arraylist3.add(uri2.toString());
	                stringbuilder.append("?,");
	            }
	        } while(true);
	        boolean flag8 = true;
	        if(arraylist2.size() > 0)
	        {
	            int i2 = stringbuilder.length();
	            stringbuilder.replace(i2 - 1, i2, ")");
	            Uri uri = InstantUploadFacade.UPLOADS_URI;
	            context.getContentResolver().delete(uri, stringbuilder.toString(), (String[])arraylist3.toArray(new String[arraylist3.size()]));
	            for(Iterator iterator1 = arraylist2.iterator(); iterator1.hasNext();)
	            {
	                Uri uri1 = (Uri)iterator1.next();
	                flag8 &= MediaStoreUtils.deleteLocalFileAndMediaStore(context.getContentResolver(), uri1);
	            }

	        }
	        ServiceResult serviceresult;
	        if(flag8)
	            serviceresult = new ServiceResult();
	        else
	            serviceresult = new ServiceResult(0, null, null);
	        completeRequest(intent, serviceresult, null);
			flag = true;
			break;
		case 64:
			String s40 = intent.getStringExtra("comment_id");
	        String s41 = intent.getStringExtra("content");
	        int j2 = s40.indexOf('#');
	        String s42 = null;
	        if(j2 > 0)
	            s42 = s40.substring(0, j2);
	        EditCommentStreamOperation editcommentstreamoperation1 = new EditCommentStreamOperation(context, account, intent, mOperationListener, s42, s40, s41);
	        editcommentstreamoperation1.startThreaded();
			flag = true;
			break;
		case 65:
			completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 66:
			String s28 = intent.getStringExtra("url");
	        SavePhotoOperation savephotooperation = new SavePhotoOperation(context, account, intent, s28, mOperationListener);
	        savephotooperation.startThreaded();
			flag = true;
			break;
		case 67:
			String s27 = intent.getStringExtra("gaia_id");
	        long l2 = intent.getLongExtra("photo_id", 0L);
	        PhotosReportAbuseOperation photosreportabuseoperation = new PhotosReportAbuseOperation(context, account, l2, s27, intent, mOperationListener);
	        photosreportabuseoperation.startThreaded();
			flag = true;
			break;
		case 201:
			String s8 = intent.getStringExtra("notif_id");
	        EsNotificationData.markNotificationAsRead(context, account, s8);
	        MarkItemReadOperation markitemreadoperation = new MarkItemReadOperation(context, account, intent, mOperationListener, Arrays.asList(new String[] {
	            s8
	        }), true);
	        markitemreadoperation.startThreaded();
			flag = true;
			break;
		case 202:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                EsService.access$600(EsService.this, context, account, intent);
	            }

	        })).start();
			flag = true;
			break;
		case 203:
			EsNotificationData.markAllNotificationsAsRead(context, account);
	        double d = EsNotificationData.getLatestNotificationTimestamp(context, account);
	        if(d > 0.0D)
	        {
	            SetNotificationLastReadTimeOperation setnotificationlastreadtimeoperation = new SetNotificationLastReadTimeOperation(context, account, intent, mOperationListener, d);
	            setnotificationlastreadtimeoperation.startThreaded();
	        } else
	        {
	            completeRequest(intent, new ServiceResult(), null);
	        }
			flag = true;
			break;
		case 204:
			NotificationSettingsData notificationsettingsdata = (NotificationSettingsData)intent.getParcelableExtra("notification_settings");
	        SetNotificationSettingsOperation setnotificationsettingsoperation = new SetNotificationSettingsOperation(context, account, notificationsettingsdata, intent, mOperationListener);
	        setnotificationsettingsoperation.startThreaded();
			flag = true;
			break;
		case 205:
			GetNotificationSettingsOperation getnotificationsettingsoperation = new GetNotificationSettingsOperation(context, account, intent, mOperationListener);
	        getnotificationsettingsoperation.startThreaded();
			flag = true;
			break;
		case 206:
			EsNotificationData.markAllNotificationsAsSeen(context, account);
	        if(intent.hasExtra("notif_id"))
	            AndroidNotification.cancel(context, account, intent.getIntExtra("notif_id", 1));
	        else
	            AndroidNotification.cancelAll(context, account);
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 600:
			MobileOutOfBoxRequest mobileoutofboxrequest = ((OutOfBoxRequestParcelable)intent.getParcelableExtra("content")).getRequest();
	        OutOfBoxOperation outofboxoperation = new OutOfBoxOperation(context, account, mobileoutofboxrequest, null, null);
	        outofboxoperation.start();
	        MobileOutOfBoxResponse mobileoutofboxresponse = outofboxoperation.getResponse();
	        putOutOfBoxResponse(j, mobileoutofboxresponse);
	        if(mobileoutofboxresponse != null && mobileoutofboxresponse.signupComplete != null && mobileoutofboxresponse.signupComplete.booleanValue())
	        {
	            if(EsLog.isLoggable("EsService", 3))
	                Log.d("EsService", "Get account info after signup");
	            AuthData.invalidateAuthToken(context, account.getName(), "webupdates");
	            GetSettingsOperation getsettingsoperation = new GetSettingsOperation(context, account, true, null, null);
	            getsettingsoperation.start();
	            if(getsettingsoperation.hasError() || getsettingsoperation.getException() != null)
	            {
	                completeRequest(intent, new ServiceResult(getsettingsoperation), null);
	            } else
	            {
	                EsAccount esaccount;
	                if(getsettingsoperation.hasPlusPages())
	                {
	                    putAccountSettingsResponse(j, getsettingsoperation.getAccountSettings());
	                    esaccount = account;
	                } else
	                {
	                    esaccount = EsAccountsData.getAccountByName(context, account.getName());
	                    updateEsApiProvider(context, esaccount.getName());
	                }
	                completeRequest(intent, new ServiceResult(getsettingsoperation), esaccount);
	            }
	        } else
	        {
	            completeRequest(intent, new ServiceResult(outofboxoperation), null);
	        }
			flag = true;
			break;
		case 702:
			String s2 = intent.getStringExtra("person_id");
	        String s3 = intent.getStringExtra("person_name");
	        String as1[] = intent.getStringArrayExtra("circles_to_add");
	        String as2[] = intent.getStringArrayExtra("circles_to_remove");
	        boolean flag1 = intent.getBooleanExtra("fire_and_forget", false);
	        SetCircleMembershipOperation setcirclemembershipoperation = new SetCircleMembershipOperation(context, account, s2, s3, as1, as2, flag1, true, intent, mOperationListener);
	        setcirclemembershipoperation.startThreaded();
			flag = true;
			break;
		case 703:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                try
	                {
	                    String s71 = intent.getStringExtra("person_id");
	                    if(intent.getBooleanExtra("refresh", false))
	                        EsPeopleData.refreshProfile(context, account, s71);
	                    else
	                        EsPeopleData.getProfileAndContactData(context, account, s71, true);
	                    completeRequest(intent, new ServiceResult(), null);
	                }
	                catch(Exception exception)
	                {
	                    completeRequest(intent, new ServiceResult(0, null, exception), null);
	                }
	            }

	        })).start();
			flag = true;
			break;
		case 704:
			String s7 = intent.getStringExtra("profile");
	        SimpleProfile simpleprofile = (SimpleProfile)JsonUtil.toBean(s7, SimpleProfile.class);
	        MutateProfileOperation mutateprofileoperation = new MutateProfileOperation(context, account, intent, mOperationListener, simpleprofile);
	        mutateprofileoperation.startThreaded();
			flag = true;
			break;
		case 705:
			String s6 = intent.getStringExtra("person_id");
	        boolean flag3 = intent.getBooleanExtra("muted", false);
	        //sPeopleDataFactory;
	        PeopleData peopledata1 = PeopleData.Factory.getInstance(context, account);
	        //sMuteUserOperationFactory;
	        MuteUserOperation.Factory.build(context, account, intent, mOperationListener, peopledata1).startThreaded(s6, flag3);
			flag = true;
			break;
		case 706:
			String s4 = intent.getStringExtra("person_id");
	        String s5 = intent.getStringExtra("person_name");
	        boolean flag2 = intent.getBooleanExtra("blocked", false);
	        //sPeopleDataFactory;
	        PeopleData peopledata = PeopleData.Factory.getInstance(context, account);
	        //sBlockUserOperationFactory;
	        BlockUserOperation.Factory.build(context, account, intent, mOperationListener, peopledata).startThreaded(s4, s5, flag2);
			flag = true;
			break;
		case 707:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                try {
	                	EsService.doReportAbuse(context, account, intent);
	                	completeRequest(intent, new ServiceResult(), null);
	                } catch (Exception e) {
	                	completeRequest(intent, new ServiceResult(0, null, e), null);
	                }
	
	            }

	        })).start();
			flag = true;
			break;
		case 710:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                try {
		            	String s71 = intent.getStringExtra("circle_id");
		                ArrayList arraylist5 = intent.getStringArrayListExtra("person_ids");
		                String as4[] = {
		                    s71
		                };
		                for(Iterator iterator2 = arraylist5.iterator(); iterator2.hasNext(); (new SetCircleMembershipOperation(context, account, (String)iterator2.next(), null, null, as4, false, false, null, null)).start());
		                AndroidNotification.update(context, account);
	                    completeRequest(intent, new ServiceResult(), null);
	                } catch (Exception exception) {
	                	completeRequest(intent, new ServiceResult(0, null, exception), null);
	                }
	                
	            }})).start();
			flag = true;
			break;
		case 712:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                String as[] = null;
	                // TODO
	// JavaClassFileOutputException: get_local_var: index out of range

	            }})).start();
			flag = true;
			break;
		case 713:
			String s26 = intent.getStringExtra("photo_id");
	        int j1 = intent.getIntExtra("top_offset", 0);
	        boolean flag7 = intent.getBooleanExtra("is_gallery_photo", false);
	        SetScrapbookPhotoOperation setscrapbookphotooperation = new SetScrapbookPhotoOperation(context, account, intent, mOperationListener, s26, j1, flag7);
	        setscrapbookphotooperation.startThreaded();
			flag = true;
			break;
		case 714:
			byte abyte2[] = intent.getByteArrayExtra("data");
	        int k1 = intent.getIntExtra("top_offset", 0);
	        UploadMediaOperation uploadmediaoperation2 = new UploadMediaOperation(context, account, intent, mOperationListener, account.getGaiaId(), "scrapbook", abyte2, Integer.valueOf(k1));
	        uploadmediaoperation2.startThreaded();
			flag = true;
			break;
		case 715:
			byte abyte1[] = intent.getByteArrayExtra("data");
	        UploadMediaOperation uploadmediaoperation1 = new UploadMediaOperation(context, account, intent, mOperationListener, account.getGaiaId(), "profile", abyte1);
	        uploadmediaoperation1.startThreaded();
			flag = true;
			break;
		case 716:
			String s23 = intent.getStringExtra("gaia_id");
	        ProfileOptimisticPlusOneOperation profileoptimisticplusoneoperation1 = new ProfileOptimisticPlusOneOperation(context, account, intent, mOperationListener, s23, true);
	        profileoptimisticplusoneoperation1.startThreaded();
			flag = true;
			break;
		case 717:
			String s22 = intent.getStringExtra("gaia_id");
	        ProfileOptimisticPlusOneOperation profileoptimisticplusoneoperation = new ProfileOptimisticPlusOneOperation(context, account, intent, mOperationListener, s22, false);
	        profileoptimisticplusoneoperation.startThreaded();
			flag = true;
			break;
		case 720:
			String s24 = intent.getStringExtra("photo_id");
	        int i1 = intent.getIntExtra("top_offset", 0);
	        String s25 = intent.getStringExtra("layout");
	        ServiceOperationListener serviceoperationlistener = mOperationListener;
	        SimpleProfile simpleprofile1 = new SimpleProfile();
	        simpleprofile1.content = new CommonContent();
	        simpleprofile1.content.scrapbookInfo = new ScrapbookInfo();
	        ScrapbookInfo scrapbookinfo = simpleprofile1.content.scrapbookInfo;
	        MutateProfileOperation mutateprofileoperation1;
	        if(s25 == null)
	            s25 = "FULL_BLEED";
	        scrapbookinfo.layout = s25;
	        simpleprofile1.content.scrapbookInfo.fullBleedPhoto = new ScrapbookInfoFullBleedPhoto();
	        simpleprofile1.content.scrapbookInfo.fullBleedPhoto.id = s24;
	        simpleprofile1.content.scrapbookInfo.fullBleedPhoto.offset = new ScrapbookInfoOffset();
	        simpleprofile1.content.scrapbookInfo.fullBleedPhoto.offset.top = Integer.valueOf(i1);
	        mutateprofileoperation1 = new MutateProfileOperation(context, account, intent, serviceoperationlistener, simpleprofile1);
	        mutateprofileoperation1.startThreaded();
			flag = true;
			break;
		case 800:
			MediaRef mediaref = (MediaRef)intent.getParcelableExtra("media_ref");
	        String s = intent.getStringExtra("album_title");
	        String s1 = intent.getStringExtra("album_label");
	        String as[] = intent.getStringArrayExtra("stream_ids");
	        ArrayList arraylist = new ArrayList();
	        int k = 0;
	        do
	        {
	            int l = as.length;
	            if(k >= l)
	                break;
	            arraylist.add(as[k]);
	            k++;
	        } while(true);
	        try
	        {
	            LocalImageRequest localimagerequest = new LocalImageRequest(mediaref, 640, 640);
	            Bitmap bitmap = EsPhotosData.loadLocalBitmap(context, localimagerequest);
	            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
	            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, bytearrayoutputstream);
	            byte abyte0[] = bytearrayoutputstream.toByteArray();
	            bitmap.recycle();
	            UploadMediaOperation uploadmediaoperation = new UploadMediaOperation(context, account, intent, mOperationListener, account.getGaiaId(), "messenger", s, s1, abyte0);
	            uploadmediaoperation.startThreaded();
	        }
	        catch(OutOfMemoryError outofmemoryerror)
	        {
	            if(EsLog.isLoggable("EsService", 6))
	                Log.e("EsService", "Could not load image", outofmemoryerror);
	        }
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
			
		return flag;
	  
	}
	
	
	private boolean processIntent2(final Context context, final EsAccount account, final Intent intent, int i) throws IOException {
		boolean flag = false;
		switch(i) {
		case 4:
			List list2 = EsAccountsData.accountsChanged(context);
	        updateEsApiProvider(context, null);
	        completeRequest(intent, new ServiceResult(), list2);
	        flag = true;
			break;
		case 5:
			EsProvider.localeChanged(context);
	        completeRequest(intent, new ServiceResult(), null);
	        flag = true;
			break;
		case 500:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                EsService.access$800(EsService.this, context, account, intent.getBooleanExtra("refresh", false), intent);
	            }

	        })).start();
			flag = true;
			break;
		case 503:
			(new GetBlockedPeopleOperation(context, account, intent, mOperationListener)).startThreaded();
			flag = true;
			break;
		case 708:
			String s31 = intent.getStringExtra("circle_name");
	        boolean flag6 = intent.getBooleanExtra("just_following", false);
	        CreateCircleOperation createcircleoperation = new CreateCircleOperation(context, account, s31, flag6, intent, mOperationListener);
	        createcircleoperation.startThreaded();
			flag = true;
			break;
		case 709:
			final ArrayList circleIds = intent.getParcelableArrayListExtra("media");
			(new Thread(new Runnable() {

	            public final void run()
	            {
	            	try {
	                Context context1 = context;
	                EsAccount esaccount5 = account;
	                ArrayList arraylist4 = circleIds;
	                EsService.doDeleteCircles(context1, esaccount5, arraylist4);
	                completeRequest(intent, new ServiceResult(), null);
	            	} catch (Exception exception) {
	            		completeRequest(intent, new ServiceResult(0, null, exception), null);
	            	}
	            }

	        })).start();
			flag = true;
			break;
		case 711:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                try {
	                	EsService.doDismissSuggestedPeople(context, account, intent);
	                	completeRequest(intent, new ServiceResult(), null);
	                } catch (Exception exception) {
	                	completeRequest(intent, new ServiceResult(0, null, exception), null);
	                }
	            }

	        })).start();
			flag = true;
			break;
		case 718:
			String s29 = intent.getStringExtra("circle_id");
	        String s30 = intent.getStringExtra("circle_name");
	        boolean flag5 = intent.getBooleanExtra("just_following", false);
	        ModifyCirclePropertiesOperation modifycirclepropertiesoperation = new ModifyCirclePropertiesOperation(context, account, s29, s30, flag5, intent, mOperationListener);
	        modifycirclepropertiesoperation.startThreaded();
			flag = true;
			break;
		case 719:
			HashMap hashmap = (HashMap)intent.getSerializableExtra("volume_map");
	        SetVolumeControlsOperation setvolumecontrolsoperation;
	        if(hashmap != null)
	        {
	            ServiceOperationListener serviceoperationlistener1 = mOperationListener;
	            setvolumecontrolsoperation = new SetVolumeControlsOperation(context, account, intent, serviceoperationlistener1, hashmap);
	        } else
	        {
	            String s28 = intent.getStringExtra("circle_id");
	            int j = intent.getIntExtra("volume", 2);
	            ServiceOperationListener serviceoperationlistener = mOperationListener;
	            setvolumecontrolsoperation = new SetVolumeControlsOperation(context, account, intent, serviceoperationlistener, s28, j);
	        }
	        setvolumecontrolsoperation.startThreaded();
			flag = true;
			break;
		case 721:
			String s26 = intent.getStringExtra("suggestions_ui");
	        ArrayList arraylist1 = intent.getStringArrayListExtra("person_ids");
	        ArrayList arraylist2 = intent.getStringArrayListExtra("suggestion_ids");
	        String s27 = intent.getStringExtra("action_type");
	        RecordSuggestionActionOperation recordsuggestionactionoperation = new RecordSuggestionActionOperation(context, account, s26, arraylist1, arraylist2, s27, null, null);
	        recordsuggestionactionoperation.startThreaded();
			flag = true;
			break;
		case 900:
			(new EventHomePageOperation(context, account, intent, mOperationListener)).startThreaded();
			flag = true;
			break;
		case 901:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                try {
	                	android.os.Process.setThreadPriority(10);
	                	boolean flag8 = EsEventData.getEventFromServer(context, account, intent.getStringExtra("event_id"), intent.getStringExtra("auth_key"));
	                	completeRequest(intent, new ServiceResult(flag8), null);
	                } catch (Exception exception) {
	                	completeRequest(intent, new ServiceResult(0, null, exception), null);
	                }
	            }

	        })).start();
			flag = true;
			break;
		case 902:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                try {
	                	android.os.Process.setThreadPriority(10);
	                	boolean flag8 = EsEventData.rsvpForEvent(context, account, intent.getStringExtra("event_id"), intent.getStringExtra("rsvp_type"), intent.getStringExtra("event_auth_key"));
	                	completeRequest(intent, new ServiceResult(flag8), null);
	                } catch (Exception exception) {
	                	completeRequest(intent, new ServiceResult(0, null, exception), null);
	                }
	            }

	        })).start();
			flag = true;
			break;
		case 903:
			CreateEventOperation createeventoperation = new CreateEventOperation(
					context,
					account,
					(PlusEvent) JsonUtil.fromByteArray(intent.getByteArrayExtra("event"), PlusEvent.class),
					(AudienceData) intent.getParcelableExtra("audience"),
					intent.getStringExtra("external_id"), intent,
					mOperationListener);
			createeventoperation.startThreaded();
			flag = true;
			break;
		case 904:
			PlusEvent plusevent = (PlusEvent)JsonUtil.fromByteArray(intent.getByteArrayExtra("event"), PlusEvent.class);
	        UpdateEventOperation updateeventoperation = new UpdateEventOperation(context, account, plusevent, intent, mOperationListener);
	        updateeventoperation.startThreaded();
			flag = true;
			break;
		case 905:
	        (new Thread(new Runnable() {

	            public final void run()
	            {
	                try {
	                	EsEventData.updateDataPhoto(context, account, intent.getStringExtra("event_id"), intent.getStringExtra("fingerprint"), intent.getLongExtra("photo_id", 0L), intent.getStringExtra("gaia_id"));
	                	completeRequest(intent, new ServiceResult(), null);
	                } catch (Exception exception) {
	                	completeRequest(intent, new ServiceResult(0, null, exception), null);
	                }
	            }

	        })).start();
			flag = true;
			break;
		case 906:
			(new GetEventThemesOperation(context, account, intent, mOperationListener)).startThreaded();
			flag = true;
			break;
		case 907:
			String s10 = intent.getStringExtra("event_id");
	        String s11 = intent.getStringExtra("auth_key");
	        String s12 = intent.getStringExtra("gaia_id");
	        AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
	        EventInviteOperation eventinviteoperation = new EventInviteOperation(context, account, s10, s11, s12, audiencedata, intent, mOperationListener);
	        eventinviteoperation.startThreaded();
			flag = true;
			break;
		case 908:
			String s13 = intent.getStringExtra("event_id");
	        String s14 = intent.getStringExtra("auth_key");
	        DeleteEventOperation deleteeventoperation = new DeleteEventOperation(context, account, s13, s14, intent, mOperationListener);
	        deleteeventoperation.startThreaded();
			flag = true;
			break;
		case 909:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                try {
	                	android.os.Process.setThreadPriority(10);
	                	HttpOperation httpoperation = EsEventData.readEventFromServer(context, account, intent.getStringExtra("event_id"), intent.getStringExtra("pollingtoken"), intent.getStringExtra("resumetoken"), intent.getStringExtra("event_auth_key"), intent.getStringExtra("invitationtoken"), intent.getBooleanExtra("fetchnewer", false), intent.getBooleanExtra("resolvetokens", false), null, null);
	                	completeRequest(intent, new ServiceResult(httpoperation), null);
	                } catch (Exception exception) {
	                	completeRequest(intent, new ServiceResult(0, null, exception), null);
	                }
	            }

	        })).start();
			flag = true;
			break;
		case 910:
			boolean flag7 = intent.getBooleanExtra("include_blacklist", false);
	        (new GetEventInviteeListOperation(context, account, intent.getStringExtra("event_id"), intent.getStringExtra("auth_key"), flag7, intent, mOperationListener)).startThreaded();
			flag = true;
			break;
		case 911:
			String s32 = intent.getStringExtra("event_id");
	        long al[] = intent.getLongArrayExtra("photo_ids");
	        ArrayList arraylist3 = new ArrayList();
	        int k = al.length;
	        for(int i1 = 0; i1 < k; i1++)
	            arraylist3.add(Long.valueOf(al[i1]));

	        SharePhotosToEventOperation sharephotostoeventoperation = new SharePhotosToEventOperation(context, account, intent, arraylist3, s32, mOperationListener);
	        sharephotostoeventoperation.startThreaded();
			flag = true;
			break;
		case 1002:
			EsAccount esaccount4 = EsAccountsData.getActiveAccount(context);
	        if(esaccount4 != null)
	        {
	            long l3 = EsAccountsData.queryLastSyncTimestamp(context, esaccount4);
	            if(l3 < 0L || System.currentTimeMillis() - l3 > 0x36ee80L)
	            {
	                ContentResolver.requestSync(AccountsUtil.newAccount(esaccount4.getName()), EsProvider.class.getName(), new Bundle());
	                if(EsAccountsData.isContactsStatsWipeoutNeeded(context, esaccount4))
	                    ContactsStatsSync.wipeout(context, account, intent, null);
	            }
	        }
	        Boolean boolean1 = Boolean.valueOf(InstantUpload.isEnabled(context));
	        completeRequest(intent, new ServiceResult(), boolean1);
			flag = true;
			break;
		case 1004:
			completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 1005:
			EsAccount esaccount3 = EsAccountsData.getActiveAccount(context);
	        if(esaccount3 != null)
	        {
	            EsProvider.cleanupData(context, esaccount3, false);
	            EsAccountsData.syncExperiments(context, esaccount3);
	        }
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 1008:
			EsAccount esaccount2 = EsAccountsData.getActiveAccount(context);
	        if(esaccount2 != null && esaccount2.equals(account))
	        {
	            EsAnalyticsData.insert(context, account, intent.getByteArrayExtra("event"));
	            long l1 = EsAnalyticsData.queryLastAnalyticsSyncTimestamp(context, account);
	            long l2 = System.currentTimeMillis() - l1;
	            if(l1 < 0L || l2 > 0x5265c0L)
	            {
	                if(EsLog.isLoggable("EsService", 3))
	                {
	                    Object aobj[] = new Object[1];
	                    aobj[0] = Long.valueOf(l2);
	                    Log.d("EsService", String.format("%d has passed since the last analytics syncs. Send the analytics data.", aobj));
	                }
	                intent.putExtra("analytics_sync", true);
	            }
	        }
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 1009:
			String s6 = intent.getStringExtra("event_id");
	        String s7 = intent.getStringExtra("auth_key");
	        boolean flag2 = intent.getBooleanExtra("blacklist", false);
	        String s8 = intent.getStringExtra("gaia_id");
	        String s9 = intent.getStringExtra("email");
	        EventManageGuestOperation eventmanageguestoperation = new EventManageGuestOperation(context, account, s6, s7, flag2, s8, s9, intent, mOperationListener);
	        eventmanageguestoperation.startThreaded();
			flag = true;
			break;
		case 1010:
	            EsAccount esaccount1 = EsAccountsData.getActiveAccount(context);
	            if(null != esaccount1 && esaccount1.equals(account)) {
	            	List list1 = EsAnalyticsData.removeAll(context, account);
	            	if(!list1.isEmpty()) {
	            		if(EsLog.isLoggable("EsService", 3))
	    	                Log.d("EsService", (new StringBuilder("Sending ")).append(list1.size()).append(" analytics events").toString());
	    	            PostClientLogsOperation postclientlogsoperation = new PostClientLogsOperation(context, account, intent, mOperationListener);
	    	            postclientlogsoperation.setClientOzEvents(list1);
	    	            postclientlogsoperation.startThreaded();
	    	            EsAnalyticsData.saveLastAnalyticsSyncTimestamp(context, account, System.currentTimeMillis());
	    	            break;
	            	}
	            }
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 1011:
			EsAccount esaccount = EsAccountsData.getActiveAccount(context);
	        if(esaccount != null && esaccount.equals(account))
	        {
	            List list = DbAnalyticsEvents.deserializeClientOzEventList(intent.getByteArrayExtra("analytics_events"));
	            if(list != null && !list.isEmpty())
	                EsAnalyticsData.bulkInsert(context, account, list);
	        }
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 1100:
			String s24 = intent.getStringExtra("search_query");
	        String s25;
	        SearchActivitiesOperation searchactivitiesoperation;
	        if(!intent.getBooleanExtra("newer", true))
	            s25 = SearchUtils.getContinuationToken(this, account, s24);
	        else
	            s25 = null;
	        searchactivitiesoperation = new SearchActivitiesOperation(context, account, s24, s25, intent, mOperationListener);
	        searchactivitiesoperation.startThreaded();
			flag = true;
			break;
		case 1110:
			EsAccountsData.uploadChangedSettings(context, account);
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 1120:
			String s23 = intent.getStringExtra("filename");
	        try
	        {
	            sLastCameraMediaLocation = ImageUtils.insertCameraPhoto(context, s23);
	        }
	        catch(FileNotFoundException filenotfoundexception)
	        {
	            sLastCameraMediaLocation = null;
	        }
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 1200:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	                EsService.access$900(EsService.this, context, account, intent.getBooleanExtra("refresh", false), intent);
	            }

	        })).start();
			flag = true;
			break;
		case 2000:
			EsNetworkData.resetStatsData(context, account);
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 2001:
			EsNetworkData.clearTransactionData(context, account);
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 2200:
			ApiaryApiInfo apiaryapiinfo2 = (ApiaryApiInfo)intent.getSerializableExtra("apiInfo");
	        ApiaryActivity apiaryactivity = (ApiaryActivity)intent.getParcelableExtra("activity");
	        String s20 = intent.getStringExtra("external_id");
	        String s21 = intent.getStringExtra("content");
	        ArrayList arraylist = intent.getParcelableArrayListExtra("media");
	        AudienceData audiencedata2 = (AudienceData)intent.getParcelableExtra("audience");
	        DbLocation dblocation = (DbLocation)intent.getParcelableExtra("loc");
	        String s22 = intent.getStringExtra("content_deep_link_id");
	        boolean flag4 = intent.getBooleanExtra("save_post_acl", true);
	        BirthdayData birthdaydata = (BirthdayData)intent.getParcelableExtra("birthdata");
	        DbEmbedEmotishare dbembedemotishare = (DbEmbedEmotishare)intent.getParcelableExtra("emotishare_embed");
	        PostActivityOperation postactivityoperation = new PostActivityOperation(context, account, intent, mOperationListener, apiaryactivity, s21, arraylist, s20, dblocation, audiencedata2, apiaryapiinfo2, s22, flag4, birthdaydata, dbembedemotishare);
	        postactivityoperation.startThreaded();
			flag = true;
			break;
		case 2201:
			ApiaryApiInfo apiaryapiinfo = (ApiaryApiInfo)intent.getSerializableExtra("apiInfo");
	        boolean flag3 = intent.getBooleanExtra("applyPlusOne", true);
	        String s19 = intent.getStringExtra("token");
	        final ContentValues contentvalues = new ContentValues();
	        Integer integer;
	        final ApiaryApiInfo apiaryapiinfo1;
	        if(flag3)
	            integer = PlatformContract.PlusOneContent.STATE_PLUSONED;
	        else
	            integer = PlatformContract.PlusOneContent.STATE_NOTPLUSONED;
	        contentvalues.put("state", integer);
	        contentvalues.put("token", s19);
	        apiaryapiinfo1 = apiaryapiinfo.getSourceInfo();
	        (new Thread(new Runnable() {

	            public final void run()
	            {
	                ContentResolver contentresolver = context.getContentResolver();
	                Uri uri1 = Uri.parse("content://com.galaxy.meetup.client.android.content.ApiProvider/plusone").buildUpon().appendQueryParameter("apiKey", apiaryapiinfo1.getApiKey()).appendQueryParameter("clientId", apiaryapiinfo1.getClientId()).appendQueryParameter("apiVersion", apiaryapiinfo1.getSdkVersion()).appendQueryParameter("pkg", apiaryapiinfo1.getPackageName()).build();
	                String as[] = new String[1];
	                as[0] = intent.getStringExtra("url");
	                char c;
	                String s33;
	                if(contentresolver.update(uri1, contentvalues, null, as) == 1)
	                    c = '\310';
	                else
	                    c = '\uFFFF';
	                if(c == '\310')
	                    s33 = "Ok";
	                else
	                    s33 = "Error";
	                ServiceResult serviceresult = new ServiceResult(c, s33, null);
	                completeRequest(intent, serviceresult, null);
	            }

	        })).start();
			flag = true;
			break;
		case 2300:
			EsAccountsData.saveContactsStatsWipeoutNeeded(this, (EsAccount)intent.getParcelableExtra("acc"), true);
	        ContactsStatsSync.wipeout(context, account, intent, mOperationListener);
			flag = true;
			break;
		case 2301:
			EsAccountsData.saveContactsStatsWipeoutNeeded(this, (EsAccount)intent.getParcelableExtra("acc"), false);
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 2400:
			EsAccountsData.saveLastContactedTimestamp(this, (EsAccount)intent.getParcelableExtra("acc"), intent.getLongExtra("timestamp", -1L));
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 2500:
			WriteReviewOperation writereviewoperation = new WriteReviewOperation(this, (EsAccount)intent.getParcelableExtra("acc"), intent, mOperationListener, (GooglePlaceReview)intent.getParcelableExtra("review_to_submit"), intent.getStringExtra("review_place_id"));
	        writereviewoperation.startThreaded();
			flag = true;
			break;
		case 2501:
			DeleteReviewOperation deletereviewoperation = new DeleteReviewOperation(this, (EsAccount)intent.getParcelableExtra("acc"), intent, mOperationListener, intent.getStringExtra("review_place_id"));
	        deletereviewoperation.startThreaded();
			flag = true;
			break;
		case 2600:
			String s5 = intent.getStringExtra("package_name");
	        EsDeepLinkInstallsData.DeepLinkInstall deeplinkinstall = EsDeepLinkInstallsData.getByPackageName(context, account, s5);
	        if(deeplinkinstall != null)
	        {
	            PlayStoreInstaller.notifyCompletedInstall(context, deeplinkinstall.authorName, deeplinkinstall.creationSource, s5, deeplinkinstall.data, deeplinkinstall.launchSource);
	            EsDeepLinkInstallsData.removeByPackageName(context, account, s5);
	            boolean flag1 = "stream_install_interactive_post".equals(deeplinkinstall.launchSource);
	            AnalyticsInfo analyticsinfo = new AnalyticsInfo(OzViews.NOTIFICATIONS_SYSTEM);
	            OzActions ozactions;
	            if(flag1)
	                ozactions = OzActions.CALL_TO_ACTION_INSTALL_COMPLETED_NOTIFICATION;
	            else
	                ozactions = OzActions.DEEP_LINK_INSTALL_COMPLETED_NOTIFICATION;
	            EsAnalytics.postRecordEvent(context, account, analyticsinfo, ozactions);
	        }
	        completeRequest(intent, new ServiceResult(), null);
			flag = true;
			break;
		case 2700:
			GetSquaresOperation getsquaresoperation = new GetSquaresOperation(context, account, intent, mOperationListener, null);
	        getsquaresoperation.startThreaded();
			flag = true;
			break;
		case 2701:
			String s4 = intent.getStringExtra("square_id");
	        GetViewerSquareOperation getviewersquareoperation = new GetViewerSquareOperation(context, account, s4, intent, mOperationListener);
	        getviewersquareoperation.startThreaded();
			flag = true;
			break;
		case 2702:
			String s2 = intent.getStringExtra("square_id");
	        String s3 = intent.getStringExtra("square_action");
	        EditSquareMembershipOperation editsquaremembershipoperation = new EditSquareMembershipOperation(context, account, s2, s3, intent, mOperationListener);
	        editsquaremembershipoperation.startThreaded();
			flag = true;
			break;
		case 2703:
			String s = intent.getStringExtra("square_id");
	        String s1 = intent.getStringExtra("cont_token");
	        ReadSquareMembersOperation readsquaremembersoperation = new ReadSquareMembersOperation(context, account, s, s1, intent, mOperationListener);
	        readsquaremembersoperation.startThreaded();
			flag = true;
			break;
		case 2704:
			(new Thread(new Runnable() {

	            public final void run()
	            {
	            	try {
	            		EsService.doDeclineSquareInvitation(context, account, intent);
	            		completeRequest(intent, new ServiceResult(), null);
	            	} catch (Exception exception) {
	            		completeRequest(intent, new ServiceResult(0, null, exception), null);
	            	}
	            }
	        })).start();
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		
		return flag;
	}
	
	static void access$300(EsService esservice, Context context, EsAccount esaccount, DbLocation dblocation, String s, Intent intent)
    {
		EsSyncAdapterService.SyncState syncstate = new EsSyncAdapterService.SyncState();
		try {
	        syncstate.onSyncStart("Get nearby activities");
	        syncstate.onStart("Activities:SyncNearby");
	        esservice.completeRequest(intent, EsPostsData.doNearbyActivitiesSync(context, esaccount, dblocation, s, EsProvider.getActivitiesPageSize(context), null, syncstate), null);
		} catch (Exception e) {
			esservice.completeRequest(intent, new ServiceResult(0, null, e), null);
		} finally {
			syncstate.onFinish();
	        syncstate.onSyncFinish();
		}
    }
	
	static void access$400(EsService esservice, Context context, EsAccount esaccount, int i, String s, String s1, String s2, String s3, 
            Intent intent, boolean flag)
    {
        EsSyncAdapterService.SyncState syncstate = new EsSyncAdapterService.SyncState();
        try {
        	syncstate.setFullSync(flag);
        	syncstate.onSyncStart((new StringBuilder("Get activities for circleId: ")).append(s).append(" userId: ").append(s1).append(" view: ").append(i).toString());
        	syncstate.onStart("Activities:SyncStream");
        	int j;
        	if(flag)
        		j = 20;
        	else
        		if(s3 == null)
        			j = EsProvider.getsActivitiesFirstPageSize(context);
        		else
        			j = EsProvider.getActivitiesPageSize(context);
        	esservice.completeRequest(intent, EsPostsData.doActivityStreamSync(context, esaccount, i, s, s1, s2, flag, s3, j, null, syncstate), null);
        } catch (Exception e) {
        	esservice.completeRequest(intent, new ServiceResult(0, null, e), null);
        } finally {
        	syncstate.onFinish();
            syncstate.onSyncFinish();
        }
    }
	
	static void access$600(EsService esservice, Context context, EsAccount esaccount, Intent intent)
    {
        EsSyncAdapterService.SyncState syncstate = new EsSyncAdapterService.SyncState();
        try {
	        syncstate.onSyncStart("Notification sync");
	        EsNotificationData.syncNotifications(context, esaccount, syncstate, null, false);
	        esservice.completeRequest(intent, new ServiceResult(), null);
        } catch (Exception e) {
        	esservice.completeRequest(intent, new ServiceResult(0, null, e), null);
        } finally {
        	syncstate.onSyncFinish();
        }
    }
	
	static void access$700(EsService esservice, Context context, EsAccount esaccount, Intent intent) throws IOException {
		String as[] = intent.getStringArrayExtra("circles_to_add");
		String as1[] = intent.getStringArrayExtra("circles_to_remove");
		android.os.Parcelable aparcelable[] = intent.getParcelableArrayExtra("participant_array");
		ArrayList arraylist = new ArrayList();
		for (int i = 0; i < aparcelable.length; i++)
			arraylist.add(((ParticipantParcelable) aparcelable[i]).getParticipantId());

		for (int j = 0; j < aparcelable.length; j++) {
			ParticipantParcelable participantparcelable = (ParticipantParcelable) aparcelable[j];
			SetCircleMembershipOperation setcirclemembershipoperation = new SetCircleMembershipOperation(
					context, esaccount,
					participantparcelable.getParticipantId(),
					participantparcelable.getName(), as, as1, false, false,
					null, null);
			setcirclemembershipoperation.start();
			setcirclemembershipoperation.logAndThrowExceptionIfFailed("EsService");
		}

		AndroidNotification.update(context, esaccount);
		return;
	}
	
	static void access$800(EsService esservice, Context context, EsAccount esaccount, boolean flag, Intent intent)
    {
        EsSyncAdapterService.SyncState syncstate = new EsSyncAdapterService.SyncState();
        try {
        	syncstate.onSyncStart("People sync");
        	EsPeopleData.syncPeople(context, esaccount, syncstate, null, flag);
        	esservice.completeRequest(intent, new ServiceResult(), null);
        } catch (Exception e) {
        	esservice.completeRequest(intent, new ServiceResult(0, null, e), null);
        } finally {
        	syncstate.onSyncFinish();
        }
    }
	
	static void access$900(EsService esservice, Context context, EsAccount esaccount, boolean flag, Intent intent)
    {
		EsSyncAdapterService.SyncState syncstate = new EsSyncAdapterService.SyncState();
		try {
			syncstate.onSyncStart("Exp sync");
			EsEmotiShareData.syncAll(context, esaccount, syncstate, null, flag);
			esservice.completeRequest(intent, new ServiceResult(), null);
		} catch (Exception e) {
			esservice.completeRequest(intent, new ServiceResult(0, null, e), null);
		} finally {
        	syncstate.onSyncFinish();
        }
    }
	
	//==================================================================================================================
	//									Inner class
	//==================================================================================================================
	private static final class ResultsLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

		protected final boolean removeEldestEntry(Entry<K, V> entry) {
			boolean flag;
			if (size() > 32)
				flag = true;
			else
				flag = false;
			return flag;
		}

		private static final long serialVersionUID = 0xa03e0db341a6fd16L;

		private ResultsLinkedHashMap() {
		}

	}
	
	private final class ServiceOperationListener implements HttpOperation.OperationListener {

	    public final void onOperationComplete(HttpOperation httpoperation)
	    {
	        onIntentProcessed(httpoperation.getIntent(), new ServiceResult(httpoperation), httpoperation);
	    }

	}
}
