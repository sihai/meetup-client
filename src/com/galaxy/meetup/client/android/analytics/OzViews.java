/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.analytics;

import android.content.Context;

import com.galaxy.meetup.client.android.ui.activity.BaseActivity;
import com.galaxy.meetup.server.client.domain.FavaDiagnosticsNamespacedType;
import com.galaxy.meetup.server.client.domain.OutputData;

/**
 * 
 * @author sihai
 *
 */
public enum OzViews {
	
	UNKNOWN("UNKNOWN", 0, "str", Integer.valueOf(0)),
    HOME("HOME", 1, "str", Integer.valueOf(1)),
    NOTIFICATIONS("NOTIFICATIONS", 2, "str", Integer.valueOf(8)),
    GENERAL_SETTINGS("GENERAL_SETTINGS", 3, "Settings", Integer.valueOf(1)),
    LOOP_EVERYONE("LOOP_EVERYONE", 4, "str", Integer.valueOf(1), Integer.valueOf(1)),
    LOOP_CIRCLES("LOOP_CIRCLES", 5, "str", Integer.valueOf(1), Integer.valueOf(2)),
    LOOP_NEARBY("LOOP_NEARBY", 6, "str", Integer.valueOf(1), Integer.valueOf(4)),
    LOOP_MANAGE("LOOP_MANAGE", 7, "str", Integer.valueOf(10)),
    LOOP_WHATS_HOT("LOOP_WHATS_HOT", 8, "xplr", Integer.valueOf(1)),
    LOOP_USER("LOOP_USER", 9, "pr", null, "pr", null, Integer.valueOf(7)),
    COMPOSE("COMPOSE", 10, "ttn", Integer.valueOf(1)),
    LOCATION_PICKER("LOCATION_PICKER", 11, "ttn", Integer.valueOf(3)),
    CIRCLE_PICKER("CIRCLE_PICKER", 12, "ttn", Integer.valueOf(2)),
    PEOPLE_PICKER("PEOPLE_PICKER", 13, "ttn", Integer.valueOf(4)),
    COMMENT("COMMENT", 14, "ttn", Integer.valueOf(5)),
    SHARE("SHARE", 15, "ttn", Integer.valueOf(1)),
    RESHARE("RESHARE", 16, "ttn", Integer.valueOf(1)),
    ACTIVITY("ACTIVITY", 17, "pr", null, "plu", null),
    PROFILE("PROFILE", 18, "pr", null, "pr", null, Integer.valueOf(2)),
    CIRCLE_SETTINGS("CIRCLE_SETTINGS", 19, "Settings", Integer.valueOf(11)),
    PEOPLE_IN_CIRCLES("PEOPLE_IN_CIRCLES", 20, "sg", Integer.valueOf(2)),
    ADD_CIRCLE("ADD_CIRCLE", 21, "sg", Integer.valueOf(8)),
    ADD_TO_CIRCLE("ADD_TO_CIRCLE", 22, "sg", Integer.valueOf(14)),
    PEOPLE_SEARCH("PEOPLE_SEARCH", 23, "pr", Integer.valueOf(10)),
    SEARCH("SEARCH", 24, "se", Integer.valueOf(1)),
    PLUSONE("PLUSONE", 25, "plusone", Integer.valueOf(1)),
    REMOVE_FROM_CIRCLE("REMOVE_FROM_CIRCLE", 26, "sg", Integer.valueOf(11)),
    ADD_PERSON_TO_CIRCLES("ADD_PERSON_TO_CIRCLES", 27, "sg", Integer.valueOf(6)),
    PEOPLE_BLOCKED("PEOPLE_BLOCKED", 28, "sg", Integer.valueOf(10)),
    WW_SUGGESTIONS("WW_SUGGESTIONS", 29, "getstarted", Integer.valueOf(2)),
    PHOTO("PHOTO", 30, "phst", Integer.valueOf(5)),
    PHOTOS_HOME("PHOTOS_HOME", 31, "phst", Integer.valueOf(6)),
    PHOTOS_LIST("PHOTOS_LIST", 32, "phst", Integer.valueOf(4)),
    PHOTO_PICKER("PHOTO_PICKER", 33, "ttn", Integer.valueOf(29)),
    VIDEO("VIDEO", 34, "lightbox2", Integer.valueOf(27)),
    ALBUMS_OF_USER("ALBUMS_OF_USER", 35, "pr", Integer.valueOf(3)),
    INSTANT_UPLOAD_GALLERY("INSTANT_UPLOAD_GALLERY", 36, "phst", Integer.valueOf(30)),
    CONVERSATIONS("CONVERSATIONS", 37, "messenger", Integer.valueOf(1)),
    CONVERSATION_GROUP("CONVERSATION_GROUP", 38, "messenger", Integer.valueOf(2)),
    CONVERSATION_ONE_ON_ONE("CONVERSATION_ONE_ON_ONE", 39, "messenger", Integer.valueOf(3)),
    CONVERSATION_START_NEW("CONVERSATION_START_NEW", 40, "messenger", Integer.valueOf(4)),
    CONVERSATION_PARTICIPANT_LIST("CONVERSATION_PARTICIPANT_LIST", 41, "messenger", Integer.valueOf(5)),
    CONVERSATION_INVITE("CONVERSATION_INVITE", 42, "messenger", Integer.valueOf(6)),
    HANGOUT("HANGOUT", 43, "h", Integer.valueOf(1)),
    HANGOUT_START_NEW("HANGOUT_START_NEW", 44, "h", Integer.valueOf(2)),
    HANGOUT_PARTICIPANTS("HANGOUT_PARTICIPANTS", 45, "h", Integer.valueOf(3)),
    NOTIFICATIONS_WIDGET("NOTIFICATIONS_WIDGET", 46, "nots", Integer.valueOf(1)),
    NOTIFICATIONS_CIRCLE("NOTIFICATIONS_CIRCLE", 47, "nots", Integer.valueOf(2)),
    NOTIFICATIONS_SYSTEM("NOTIFICATIONS_SYSTEM", 48, "nots", Integer.valueOf(3)),
    CONTACTS_CIRCLELIST("CONTACTS_CIRCLELIST", 49, "sg", Integer.valueOf(7)),
    CONTACTS_SYNC_CONFIG("CONTACTS_SYNC_CONFIG", 50, "settings", Integer.valueOf(10)),
    PLATFORM_PLUS_ONE("PLATFORM_PLUS_ONE", 51, "plusone", Integer.valueOf(3)),
    PLATFORM_THIRD_PARTY_APP("PLATFORM_THIRD_PARTY_APP", 52, "plusone", Integer.valueOf(2)),
    EVENT("EVENT", 53, "oevt", Integer.valueOf(6)),
    CREATE_EVENT("CREATE_EVENT", 54, "oevt", Integer.valueOf(8)),
    MY_EVENTS("MY_EVENTS", 55, "oevt", Integer.valueOf(5)),
    EVENT_THEMES("EVENT_THEMES", 56, "oevt", Integer.valueOf(10)),
    SQUARE_LANDING("SQUARE_LANDING", 57, "sq", Integer.valueOf(1)),
    SQUARE_HOME("SQUARE_HOME", 58, "sq", Integer.valueOf(3)),
    SQUARE_MEMBERS("SQUARE_MEMBERS", 59, "sq", Integer.valueOf(4)),
    SQUARE_SEARCH("SQUARE_SEARCH", 60, "sq", Integer.valueOf(8)),
    OOB_CAMERA_SYNC("OOB_CAMERA_SYNC", 61, "oob", Integer.valueOf(10)),
    OOB_ADD_PEOPLE_VIEW("OOB_ADD_PEOPLE_VIEW", 62, "oob", Integer.valueOf(18)),
    OOB_IMPROVE_CONTACTS_VIEW("OOB_IMPROVE_CONTACTS_VIEW", 63, "oob", Integer.valueOf(19));
    
	private final FavaDiagnosticsNamespacedType mFavaDiagnosticsNamespacedType;
    private final OutputData mViewData;
    
    private OzViews(String s, int i, String s1, Integer integer)
    {
        this(s, i, s1, integer, null, null);
    }

    private OzViews(String s, int i, String s1, Integer integer, Integer integer1)
    {
        this(s, i, s1, integer, null, integer1);
    }

    private OzViews(String s, int i, String s1, Integer integer, String s2, Integer integer1)
    {
        this(s, i, s1, integer, s2, integer1, null);
    }

    private OzViews(String s, int i, String s1, Integer integer, String s2, Integer integer1, Integer integer2)
    {
        mFavaDiagnosticsNamespacedType = new FavaDiagnosticsNamespacedType();
        mFavaDiagnosticsNamespacedType.namespace = s1;
        mFavaDiagnosticsNamespacedType.typeNum = integer;
        mFavaDiagnosticsNamespacedType.typeStr = s2;
        if(integer1 != null || integer2 != null)
        {
            mViewData = new OutputData();
            if(integer1 != null)
                mViewData.filterType = integer1;
            if(integer2 != null)
                mViewData.tab = integer2;
        } else
        {
            mViewData = null;
        }
    }

    public static String getName(OzViews ozviews)
    {
        String s;
        if(ozviews == null)
            s = null;
        else
            s = ozviews.name();
        return s;
    }

    public static OzViews getViewForLogging(Context context)
    {
        OzViews ozviews;
        if(context != null && (context instanceof BaseActivity))
            ozviews = ((BaseActivity)context).getViewForLogging();
        else
            ozviews = null;
        return ozviews;
    }

    public static OzViews valueOf(int i)
    {
        OzViews aozviews[] = values();
        OzViews ozviews;
        if(aozviews != null && i >= 0 && i < aozviews.length)
            ozviews = aozviews[i];
        else
            ozviews = UNKNOWN;
        return ozviews;
    }
    
    public final FavaDiagnosticsNamespacedType getFavaDiagnosticsNamespacedType()
    {
        return mFavaDiagnosticsNamespacedType;
    }

    public final OutputData getViewData()
    {
        return mViewData;
    }
}