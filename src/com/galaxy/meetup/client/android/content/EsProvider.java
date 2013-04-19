/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.cache.EsMediaCache;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 *
 */
public class EsProvider extends ContentProvider {

	private static final HashMap ACCOUNTS_PROJECTION_MAP;
    public static final Uri ACCOUNT_STATUS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/account_status");
    private static final Uri ACTIVITIES_BY_CIRCLE_ID_URI;
    private static final Uri ACTIVITIES_STREAM_VIEW_URI;
    public static final Uri ACTIVITIES_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/activities");
    private static final HashMap ACTIVITIES_VIEW_PROJECTION_MAP;
    private static final HashMap ACTIVITY_SUMMARY_PROJECTION_MAP;
    public static final Uri ACTIVITY_SUMMARY_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/activities/summary");
    public static final Uri ACTIVITY_VIEW_BY_ACTIVITY_ID_URI;
    public static final Uri ACTIVITY_VIEW_URI;
    public static final Uri ALBUM_VIEW_BY_ALBUM_AND_OWNER_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/album_view_by_album_and_owner");
    public static final Uri ALBUM_VIEW_BY_OWNER_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/album_view_by_user");
    private static final HashMap ALBUM_VIEW_MAP;
    private static final HashMap CIRCLES_PROJECTION_MAP;
    public static final Uri CIRCLES_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/circles");
    public static final Uri COMMENTS_VIEW_BY_ACTIVITY_ID_URI;
    private static final HashMap COMMENTS_VIEW_PROJECTION_MAP;
    private static final Uri COMMENTS_VIEW_URI;
    public static final Uri CONTACTS_BY_CIRCLE_ID_URI;
    public static final Uri CONTACTS_BY_SQUARE_ID_URI;
    private static final HashMap CONTACTS_PROJECTION_MAP;
    public static final Uri CONTACTS_QUERY_URI;
    private static final HashMap CONTACTS_SEARCH_PROJECTION_MAP;
    private static final HashMap CONTACTS_SEARCH_WITH_PHONES_PROJECTION_MAP;
    public static final Uri CONTACTS_URI;
    public static final Uri CONTACT_BY_PERSON_ID_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/contacts/id");
    private static final HashMap CONVERSATIONS_PROJECTION_MAP;
    public static final Uri CONVERSATIONS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/conversations");
    private static final HashMap EMOTISHARE_PROJECTION_MAP;
    public static final Uri EMOTISHARE_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/emotishare_data");
    public static final Uri EVENTS_ALL_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/events");
    private static final HashMap EVENT_PEOPLE_VIEW_MAP;
    private static final HashMap HANGOUT_SUGGESTIONS_PROJECTION_MAP;
    public static final Uri HANGOUT_SUGGESTIONS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/hangout_suggestions");
    private static final HashMap LOCATION_QUERIES_VIEW_PROJECTION_MAP;
    private static final Uri LOCATION_QUERIES_VIEW_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/location_queries_view");
    private static final Uri MESSAGES_BY_CONVERSATION_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/messages/conversation");
    private static final HashMap MESSAGES_PROJECTION_MAP;
    private static final HashMap MESSAGE_NOTIFICATIONS_PROJECTION_MAP;
    public static final Uri MESSAGE_NOTIFICATIONS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/message_notifications_view");
    private static final HashMap MESSENGER_SUGGESTIONS_PROJECTION_MAP;
    public static final Uri MESSENGER_SUGGESTIONS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/messenger_suggestions");
    private static final HashMap NETWORK_DATA_STATS_PROJECTION_MAP;
    public static final Uri NETWORK_DATA_STATS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/network_data_stats");
    private static final HashMap NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP;
    public static final Uri NETWORK_DATA_TRANSACTIONS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/network_data_transactions");
    private static final HashMap NOTIFICATIONS_PROJECTION_MAP;
    public static final Uri NOTIFICATIONS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/notifications");
    public static final Uri PANORAMA_IMAGE_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/panorama_image");
    private static final HashMap PARTICIPANTS_PROJECTION_MAP;
    private static final Uri PARTICIPANTS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/participants");
    private static final HashMap PHOTOS_BY_ALBUM_VIEW_MAP;
    
    private static final String PHOTOS_BY_ALBUM_VIEW_SQL = String.format("CREATE VIEW %s AS SELECT photo._id as _id, photo.action_state as action_state, photo.album_id as album_id, photo.comment_count as comment_count, photo.description as description, photo.downloadable as downloadable, photo.entity_version as entity_version, photo.height as height, photo.owner_id as owner_id, photo.photo_id as photo_id, photo.fingerprint as fingerprint, photo.timestamp as timestamp, photo.title as title, photo.upload_status as upload_status, photo.url as url, photo.video_data as video_data, photo.is_panorama as is_panorama, photo.width as width, photo_plusone.plusone_count as plusone_count, photo_plusone.plusone_data as plusone_data, photo_plusone.plusone_by_me as plusone_by_me, photo_plusone.plusone_id as plusone_id, album.title as album_name, album.stream_id as album_stream, contacts.name as owner_name, contacts.avatar as owner_avatar_url, %s (SELECT a.status FROM account_status,photo_shape as a WHERE a.photo_id=photo.photo_id AND a.subject_id=account_status.user_id AND a.status='PENDING' LIMIT 1) AS pending_status FROM photo LEFT JOIN photo_plusone ON photo.photo_id=photo_plusone.photo_id LEFT JOIN album ON photo.album_id=album.album_id LEFT JOIN contacts ON photo.owner_id=contacts.gaia_id %s", new Object[] {
        "photos_by_album_view", "", "INNER JOIN photos_in_album ON photo.photo_id=photos_in_album.photo_id"
    });
    private static final HashMap PHOTOS_BY_EVENT_VIEW_MAP;
    private static final String PHOTOS_BY_EVENT_VIEW_SQL = String.format("CREATE VIEW %s AS SELECT photo._id as _id, photo.action_state as action_state, photo.album_id as album_id, photo.comment_count as comment_count, photo.description as description, photo.downloadable as downloadable, photo.entity_version as entity_version, photo.height as height, photo.owner_id as owner_id, photo.photo_id as photo_id, photo.fingerprint as fingerprint, photo.timestamp as timestamp, photo.title as title, photo.upload_status as upload_status, photo.url as url, photo.video_data as video_data, photo.is_panorama as is_panorama, photo.width as width, photo_plusone.plusone_count as plusone_count, photo_plusone.plusone_data as plusone_data, photo_plusone.plusone_by_me as plusone_by_me, photo_plusone.plusone_id as plusone_id, album.title as album_name, album.stream_id as album_stream, contacts.name as owner_name, contacts.avatar as owner_avatar_url, %s (SELECT a.status FROM account_status,photo_shape as a WHERE a.photo_id=photo.photo_id AND a.subject_id=account_status.user_id AND a.status='PENDING' LIMIT 1) AS pending_status FROM photo LEFT JOIN photo_plusone ON photo.photo_id=photo_plusone.photo_id LEFT JOIN album ON photo.album_id=album.album_id LEFT JOIN contacts ON photo.owner_id=contacts.gaia_id %s", new Object[] {
        "photos_by_event_view", "photos_in_event.event_id as event_id, ", "INNER JOIN photos_in_event ON photo.photo_id=photos_in_event.photo_id"
    });
    private static final HashMap PHOTOS_BY_STREAM_VIEW_MAP;
    private static final String PHOTOS_BY_STREAM_VIEW_SQL = String.format("CREATE VIEW %s AS SELECT photo._id as _id, photo.action_state as action_state, photo.album_id as album_id, photo.comment_count as comment_count, photo.description as description, photo.downloadable as downloadable, photo.entity_version as entity_version, photo.height as height, photo.owner_id as owner_id, photo.photo_id as photo_id, photo.fingerprint as fingerprint, photo.timestamp as timestamp, photo.title as title, photo.upload_status as upload_status, photo.url as url, photo.video_data as video_data, photo.is_panorama as is_panorama, photo.width as width, photo_plusone.plusone_count as plusone_count, photo_plusone.plusone_data as plusone_data, photo_plusone.plusone_by_me as plusone_by_me, photo_plusone.plusone_id as plusone_id, album.title as album_name, album.stream_id as album_stream, contacts.name as owner_name, contacts.avatar as owner_avatar_url, %s (SELECT a.status FROM account_status,photo_shape as a WHERE a.photo_id=photo.photo_id AND a.subject_id=account_status.user_id AND a.status='PENDING' LIMIT 1) AS pending_status FROM photo LEFT JOIN photo_plusone ON photo.photo_id=photo_plusone.photo_id LEFT JOIN album ON photo.album_id=album.album_id LEFT JOIN contacts ON photo.owner_id=contacts.gaia_id %s", new Object[] {
        "photos_by_stream_view", "photos_in_stream.stream_id as stream_id, ", "INNER JOIN photos_in_stream ON photo.photo_id=photos_in_stream.photo_id"
    });
    private static final HashMap PHOTOS_BY_USER_VIEW_MAP;
    private static final String PHOTOS_BY_USER_VIEW_SQL = String.format("CREATE VIEW %s AS SELECT photo._id as _id, photo.action_state as action_state, photo.album_id as album_id, photo.comment_count as comment_count, photo.description as description, photo.downloadable as downloadable, photo.entity_version as entity_version, photo.height as height, photo.owner_id as owner_id, photo.photo_id as photo_id, photo.fingerprint as fingerprint, photo.timestamp as timestamp, photo.title as title, photo.upload_status as upload_status, photo.url as url, photo.video_data as video_data, photo.is_panorama as is_panorama, photo.width as width, photo_plusone.plusone_count as plusone_count, photo_plusone.plusone_data as plusone_data, photo_plusone.plusone_by_me as plusone_by_me, photo_plusone.plusone_id as plusone_id, album.title as album_name, album.stream_id as album_stream, contacts.name as owner_name, contacts.avatar as owner_avatar_url, %s (SELECT a.status FROM account_status,photo_shape as a WHERE a.photo_id=photo.photo_id AND a.subject_id=account_status.user_id AND a.status='PENDING' LIMIT 1) AS pending_status FROM photo LEFT JOIN photo_plusone ON photo.photo_id=photo_plusone.photo_id LEFT JOIN album ON photo.album_id=album.album_id LEFT JOIN contacts ON photo.owner_id=contacts.gaia_id %s", new Object[] {
        "photos_by_user_view", "photos_of_user.photo_of_user_id as photo_of_user_id, ", "INNER JOIN photos_of_user ON photo.photo_id=photos_of_user.photo_id"
    });
    public static final Uri PHOTO_BY_ALBUM_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photos_by_album");
    public static final Uri PHOTO_BY_EVENT_ID_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photos_by_event");
    public static final Uri PHOTO_BY_PHOTO_ID_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photos_by_photo");
    public static final Uri PHOTO_BY_STREAM_ID_AND_OWNER_ID_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photos_by_stream_and_owner");
    public static final Uri PHOTO_COMMENTS_BY_PHOTO_ID_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photo_comment_by_photo");
    private static final HashMap PHOTO_COMMENTS_MAP;
    private static final HashMap PHOTO_HOME_MAP;
    public static final Uri PHOTO_HOME_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photo_home");
    public static final Uri PHOTO_NOTIFICATION_COUNT_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photo_notification_count");
    private static final HashMap PHOTO_NOTIFICATION_MAP;
    public static final Uri PHOTO_OF_USER_ID_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photos_by_user");
    public static final Uri PHOTO_SHAPES_BY_PHOTO_ID_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photo_shape_by_photo");
    private static final HashMap PHOTO_SHAPE_VIEW_MAP;
    public static final Uri PHOTO_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/photo");
    private static final HashMap PHOTO_VIEW_MAP;
    private static final String PHOTO_VIEW_SQL = String.format("CREATE VIEW %s AS SELECT photo._id as _id, photo.action_state as action_state, photo.album_id as album_id, photo.comment_count as comment_count, photo.description as description, photo.downloadable as downloadable, photo.entity_version as entity_version, photo.height as height, photo.owner_id as owner_id, photo.photo_id as photo_id, photo.fingerprint as fingerprint, photo.timestamp as timestamp, photo.title as title, photo.upload_status as upload_status, photo.url as url, photo.video_data as video_data, photo.is_panorama as is_panorama, photo.width as width, photo_plusone.plusone_count as plusone_count, photo_plusone.plusone_data as plusone_data, photo_plusone.plusone_by_me as plusone_by_me, photo_plusone.plusone_id as plusone_id, album.title as album_name, album.stream_id as album_stream, contacts.name as owner_name, contacts.avatar as owner_avatar_url, %s (SELECT a.status FROM account_status,photo_shape as a WHERE a.photo_id=photo.photo_id AND a.subject_id=account_status.user_id AND a.status='PENDING' LIMIT 1) AS pending_status FROM photo LEFT JOIN photo_plusone ON photo.photo_id=photo_plusone.photo_id LEFT JOIN album ON photo.album_id=album.album_id LEFT JOIN contacts ON photo.owner_id=contacts.gaia_id %s", new Object[] {
        "photo_view", "", ""
    });
    private static final HashMap PLATFORM_AUDIENCE_PROJECTION_MAP;
    public static final Uri PLATFORM_AUDIENCE_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/platform_audience");
    private static final HashMap PLUS_PAGES_PROJECTION_MAP;
    public static final Uri PLUS_PAGES_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/plus_pages");
    private static final HashMap SQUARES_PROJECTION_MAP;
    public static final Uri SQUARES_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/squares");
    private static final HashMap SQUARE_CONTACTS_PROJECTION_MAP;
    private static final HashMap SUGGESTED_PEOPLE_PROJECTION_MAP;
    public static final Uri SUGGESTED_PEOPLE_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/contacts/suggested");
    private static final UriMatcher URI_MATCHER;
    private static int sActivitiesFirstPageSize;
    private static int sActivitiesPageSize;
    
    static {
        ACTIVITIES_STREAM_VIEW_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/activities_stream_view");
        ACTIVITIES_BY_CIRCLE_ID_URI = Uri.parse((new StringBuilder()).append(ACTIVITIES_STREAM_VIEW_URI).append("_by_circle").toString());
        ACTIVITY_VIEW_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/activity_view");
        ACTIVITY_VIEW_BY_ACTIVITY_ID_URI = Uri.parse((new StringBuilder()).append(ACTIVITY_VIEW_URI).append("/activity").toString());
        COMMENTS_VIEW_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/comments_view");
        COMMENTS_VIEW_BY_ACTIVITY_ID_URI = Uri.parse((new StringBuilder()).append(COMMENTS_VIEW_URI).append("/activity").toString());
        CONTACTS_URI = Uri.parse("content://com.galaxy.meetup.client.android.content.EsProvider/contacts");
        CONTACTS_BY_CIRCLE_ID_URI = Uri.parse((new StringBuilder()).append(CONTACTS_URI).append("/circle").toString());
        CONTACTS_BY_SQUARE_ID_URI = Uri.parse((new StringBuilder()).append(CONTACTS_URI).append("/square").toString());
        CONTACTS_QUERY_URI = Uri.parse((new StringBuilder()).append(CONTACTS_URI).append("/query").toString());
        UriMatcher urimatcher = new UriMatcher(-1);
        URI_MATCHER = urimatcher;
        urimatcher.addURI("com.galaxy.meetup.client.android.content.EsProvider", "account_status", 1);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "activities", 20);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "activity_view/activity/*", 22);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "activities_stream_view/stream/*", 21);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "activities_stream_view_by_circle/*", 23);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "activities/summary", 24);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "comments_view/activity/*", 30);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "location_queries_view/query/*", 40);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "notifications", 50);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "circles", 60);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "contacts", 70);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "contacts/circle/*", 71);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "contacts/square/*", 75);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "contacts/query/*", 74);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "contacts/query", 74);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "contacts/id/*", 72);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "contacts/suggested", 73);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "circle_contact", 62);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "conversations", 100);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "participants/conversation/*", 110);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "message_notifications_view", 160);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "messages/conversation/*", 120);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "messenger_suggestions", 115);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "hangout_suggestions", 116);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "photo_home", 130);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "album_view/*", 131);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "album_view_by_user/*", 132);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "album_view_by_album_and_owner/*/*", 144);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "album_view_by_stream/*", 133);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "photos_by_photo/*", 134);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "photos_by_album/*", 135);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "photos_by_event/*", 145);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "photos_by_user/*", 139);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "photos_by_stream_and_owner/*/*", 138);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "photo_comment_by_photo/*", 141);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "photo_shape_by_photo/*", 143);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "photo_notification_count", 140);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "network_data_transactions", 180);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "network_data_stats", 181);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "platform_audience/*", 182);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "plus_pages", 190);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "panorama_image", 200);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "squares", 210);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "squares/*", 211);
        URI_MATCHER.addURI("com.galaxy.meetup.client.android.content.EsProvider", "emotishare_data", 212);
        HashMap hashmap = new HashMap();
        ACCOUNTS_PROJECTION_MAP = hashmap;
        hashmap.put("circle_sync_time", "circle_sync_time");
        ACCOUNTS_PROJECTION_MAP.put("last_sync_time", "last_sync_time");
        ACCOUNTS_PROJECTION_MAP.put("last_stats_sync_time", "last_stats_sync_time");
        ACCOUNTS_PROJECTION_MAP.put("last_contacted_time", "last_contacted_time");
        ACCOUNTS_PROJECTION_MAP.put("wipeout_stats", "wipeout_stats");
        ACCOUNTS_PROJECTION_MAP.put("people_sync_time", "people_sync_time");
        ACCOUNTS_PROJECTION_MAP.put("people_last_update_token", "people_last_update_token");
        ACCOUNTS_PROJECTION_MAP.put("avatars_downloaded", "avatars_downloaded");
        ACCOUNTS_PROJECTION_MAP.put("audience_data", "audience_data");
        ACCOUNTS_PROJECTION_MAP.put("audience_history", "audience_history");
        ACCOUNTS_PROJECTION_MAP.put("user_id", "user_id");
        ACCOUNTS_PROJECTION_MAP.put("contacts_sync_version", "contacts_sync_version");
        ACCOUNTS_PROJECTION_MAP.put("push_notifications", "push_notifications");
        ACCOUNTS_PROJECTION_MAP.put("last_analytics_sync_time", "last_analytics_sync_time");
        ACCOUNTS_PROJECTION_MAP.put("last_settings_sync_time", "last_settings_sync_time");
        ACCOUNTS_PROJECTION_MAP.put("last_squares_sync_time", "last_squares_sync_time");
        ACCOUNTS_PROJECTION_MAP.put("last_emotishare_sync_time", "last_emotishare_sync_time");
        HashMap hashmap1 = new HashMap();
        ACTIVITIES_VIEW_PROJECTION_MAP = hashmap1;
        hashmap1.put("_id", "_id");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("activity_id", "activity_id");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("data_state", "data_state");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("last_activity", "last_activity");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("token", "token");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("author_id", "author_id");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("name", "name");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("avatar", "avatar");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("source_id", "source_id");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("source_name", "source_name");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("total_comment_count", "total_comment_count");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("plus_one_data", "plus_one_data");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("public", "public");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("spam", "spam");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("acl_display", "acl_display");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("can_comment", "can_comment");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("can_reshare", "can_reshare");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("has_muted", "has_muted");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("has_read", "has_read");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("loc", "loc");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("created", "created");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("is_edited", "is_edited");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("modified", "modified");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("event_id", "event_id");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("photo_collection", "photo_collection");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("popular_post", "popular_post");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("content_flags", "content_flags");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("annotation", "annotation");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("annotation_plaintext", "annotation_plaintext");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("title", "title");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("title_plaintext", "title_plaintext");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("original_author_id", "original_author_id");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("original_author_name", "original_author_name");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("event_data", "event_data");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_deep_link", "embed_deep_link");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_media", "embed_media");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_photo_album", "embed_photo_album");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_checkin", "embed_checkin");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_place", "embed_place");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_place_review", "embed_place_review");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_skyjam", "embed_skyjam");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_appinvite", "embed_appinvite");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_hangout", "embed_hangout");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_square", "embed_square");
        ACTIVITIES_VIEW_PROJECTION_MAP.put("embed_emotishare", "embed_emotishare");
        HashMap hashmap2 = new HashMap();
        ACTIVITY_SUMMARY_PROJECTION_MAP = hashmap2;
        hashmap2.put("author_id", "author_id");
        ACTIVITY_SUMMARY_PROJECTION_MAP.put("activity_id", "activity_id");
        ACTIVITY_SUMMARY_PROJECTION_MAP.put("created", "created");
        ACTIVITY_SUMMARY_PROJECTION_MAP.put("is_edited", "is_edited");
        ACTIVITY_SUMMARY_PROJECTION_MAP.put("modified", "modified");
        HashMap hashmap3 = new HashMap();
        COMMENTS_VIEW_PROJECTION_MAP = hashmap3;
        hashmap3.put("_id", "_id");
        COMMENTS_VIEW_PROJECTION_MAP.put("activity_id", "activity_id");
        COMMENTS_VIEW_PROJECTION_MAP.put("comment_id", "comment_id");
        COMMENTS_VIEW_PROJECTION_MAP.put("author_id", "author_id");
        COMMENTS_VIEW_PROJECTION_MAP.put("content", "content");
        COMMENTS_VIEW_PROJECTION_MAP.put("created", "created");
        COMMENTS_VIEW_PROJECTION_MAP.put("name", "name");
        COMMENTS_VIEW_PROJECTION_MAP.put("avatar", "avatar");
        COMMENTS_VIEW_PROJECTION_MAP.put("plus_one_data", "plus_one_data");
        HashMap hashmap4 = new HashMap();
        LOCATION_QUERIES_VIEW_PROJECTION_MAP = hashmap4;
        hashmap4.put("_id", "_id");
        LOCATION_QUERIES_VIEW_PROJECTION_MAP.put("name", "name");
        LOCATION_QUERIES_VIEW_PROJECTION_MAP.put("location", "location");
        HashMap hashmap5 = new HashMap();
        NOTIFICATIONS_PROJECTION_MAP = hashmap5;
        hashmap5.put("_id", "_id");
        NOTIFICATIONS_PROJECTION_MAP.put("notif_id", "notif_id");
        NOTIFICATIONS_PROJECTION_MAP.put("category", "category");
        NOTIFICATIONS_PROJECTION_MAP.put("message", "message");
        NOTIFICATIONS_PROJECTION_MAP.put("timestamp", "timestamp");
        NOTIFICATIONS_PROJECTION_MAP.put("enabled", "enabled");
        NOTIFICATIONS_PROJECTION_MAP.put("read", "read");
        NOTIFICATIONS_PROJECTION_MAP.put("circle_data", "circle_data");
        NOTIFICATIONS_PROJECTION_MAP.put("pd_gaia_id", "pd_gaia_id");
        NOTIFICATIONS_PROJECTION_MAP.put("pd_album_id", "pd_album_id");
        NOTIFICATIONS_PROJECTION_MAP.put("pd_photo_id", "pd_photo_id");
        NOTIFICATIONS_PROJECTION_MAP.put("activity_id", "activity_id");
        NOTIFICATIONS_PROJECTION_MAP.put("ed_event", "ed_event");
        NOTIFICATIONS_PROJECTION_MAP.put("ed_event_id", "ed_event_id");
        NOTIFICATIONS_PROJECTION_MAP.put("ed_creator_id", "ed_creator_id");
        NOTIFICATIONS_PROJECTION_MAP.put("notification_type", "notification_type");
        NOTIFICATIONS_PROJECTION_MAP.put("coalescing_code", "coalescing_code");
        NOTIFICATIONS_PROJECTION_MAP.put("entity_type", "entity_type");
        NOTIFICATIONS_PROJECTION_MAP.put("entity_snippet", "entity_snippet");
        NOTIFICATIONS_PROJECTION_MAP.put("entity_photos_data", "entity_photos_data");
        NOTIFICATIONS_PROJECTION_MAP.put("entity_squares_data", "entity_squares_data");
        NOTIFICATIONS_PROJECTION_MAP.put("square_id", "square_id");
        NOTIFICATIONS_PROJECTION_MAP.put("square_name", "square_name");
        NOTIFICATIONS_PROJECTION_MAP.put("square_photo_url", "square_photo_url");
        NOTIFICATIONS_PROJECTION_MAP.put("taggee_photo_ids", "taggee_photo_ids");
        NOTIFICATIONS_PROJECTION_MAP.put("taggee_data_actors", "taggee_data_actors");
        HashMap hashmap6 = new HashMap();
        CIRCLES_PROJECTION_MAP = hashmap6;
        hashmap6.put("_id", "circles.rowid AS _id");
        CIRCLES_PROJECTION_MAP.put("circle_id", "circles.circle_id AS circle_id");
        CIRCLES_PROJECTION_MAP.put("circle_name", "circle_name");
        CIRCLES_PROJECTION_MAP.put("type", "type");
        CIRCLES_PROJECTION_MAP.put("semantic_hints", "semantic_hints");
        CIRCLES_PROJECTION_MAP.put("contact_count", "contact_count");
        CIRCLES_PROJECTION_MAP.put("member_ids", "group_concat(link_person_id, '|') AS member_ids");
        CIRCLES_PROJECTION_MAP.put("show_order", "show_order");
        CIRCLES_PROJECTION_MAP.put("volume", "volume");
        HashMap hashmap7 = new HashMap();
        CONTACTS_PROJECTION_MAP = hashmap7;
        hashmap7.put("_id", "contacts.rowid AS _id");
        CONTACTS_PROJECTION_MAP.put("person_id", "contacts.person_id AS person_id");
        CONTACTS_PROJECTION_MAP.put("gaia_id", "gaia_id");
        CONTACTS_PROJECTION_MAP.put("avatar", "avatar");
        CONTACTS_PROJECTION_MAP.put("name", "name");
        CONTACTS_PROJECTION_MAP.put("last_updated_time", "last_updated_time");
        CONTACTS_PROJECTION_MAP.put("profile_type", "profile_type");
        CONTACTS_PROJECTION_MAP.put("in_my_circles", "in_my_circles");
        CONTACTS_PROJECTION_MAP.put("for_sharing", "(CASE WHEN person_id IN (SELECT link_person_id FROM circle_contact WHERE link_circle_id IN (SELECT circle_id FROM circles WHERE semantic_hints & 64 != 0)) THEN 1 ELSE 0 END) AS for_sharing");
        CONTACTS_PROJECTION_MAP.put("blocked", "blocked");
        CONTACTS_PROJECTION_MAP.put("packed_circle_ids", "group_concat(link_circle_id, '|') AS packed_circle_ids");
        CONTACTS_PROJECTION_MAP.put("contact_update_time", "contact_update_time");
        CONTACTS_PROJECTION_MAP.put("contact_proto", "contact_proto");
        CONTACTS_PROJECTION_MAP.put("profile_update_time", "profile_update_time");
        CONTACTS_PROJECTION_MAP.put("profile_proto", "profile_proto");
        HashMap hashmap8 = new HashMap(CONTACTS_PROJECTION_MAP);
        CONTACTS_SEARCH_PROJECTION_MAP = hashmap8;
        hashmap8.put("email", "email");
        HashMap hashmap9 = new HashMap(CONTACTS_SEARCH_PROJECTION_MAP);
        CONTACTS_SEARCH_WITH_PHONES_PROJECTION_MAP = hashmap9;
        hashmap9.put("_id", "_id");
        CONTACTS_SEARCH_WITH_PHONES_PROJECTION_MAP.put("person_id", "person_id");
        CONTACTS_SEARCH_WITH_PHONES_PROJECTION_MAP.put("packed_circle_ids", "packed_circle_ids");
        CONTACTS_SEARCH_WITH_PHONES_PROJECTION_MAP.put("phone", "phone");
        CONTACTS_SEARCH_WITH_PHONES_PROJECTION_MAP.put("phone_type", "phone_type");
        HashMap hashmap10 = new HashMap(CONTACTS_PROJECTION_MAP);
        SUGGESTED_PEOPLE_PROJECTION_MAP = hashmap10;
        hashmap10.put("category", "category");
        SUGGESTED_PEOPLE_PROJECTION_MAP.put("category_label", "category_label");
        SUGGESTED_PEOPLE_PROJECTION_MAP.put("explanation", "explanation");
        SUGGESTED_PEOPLE_PROJECTION_MAP.put("properties", "properties");
        SUGGESTED_PEOPLE_PROJECTION_MAP.put("suggestion_id", "suggestion_id");
        HashMap hashmap11 = new HashMap();
        CONVERSATIONS_PROJECTION_MAP = hashmap11;
        hashmap11.put("_id", "_id");
        CONVERSATIONS_PROJECTION_MAP.put("conversation_id", "conversation_id");
        CONVERSATIONS_PROJECTION_MAP.put("is_muted", "is_muted");
        CONVERSATIONS_PROJECTION_MAP.put("is_visible", "is_visible");
        CONVERSATIONS_PROJECTION_MAP.put("latest_event_timestamp", "latest_event_timestamp");
        CONVERSATIONS_PROJECTION_MAP.put("latest_message_timestamp", "latest_message_timestamp");
        CONVERSATIONS_PROJECTION_MAP.put("earliest_event_timestamp", "earliest_event_timestamp");
        CONVERSATIONS_PROJECTION_MAP.put("has_older_events", "has_older_events");
        CONVERSATIONS_PROJECTION_MAP.put("unread_count", "unread_count");
        CONVERSATIONS_PROJECTION_MAP.put("name", "name");
        CONVERSATIONS_PROJECTION_MAP.put("generated_name", "generated_name");
        CONVERSATIONS_PROJECTION_MAP.put("latest_message_text", "latest_message_text");
        CONVERSATIONS_PROJECTION_MAP.put("latest_message_image_url", "latest_message_image_url");
        CONVERSATIONS_PROJECTION_MAP.put("latest_message_author_id", "latest_message_author_id");
        CONVERSATIONS_PROJECTION_MAP.put("latest_message_type", "latest_message_type");
        CONVERSATIONS_PROJECTION_MAP.put("latest_message_author_full_name", "latest_message_author_full_name");
        CONVERSATIONS_PROJECTION_MAP.put("latest_message_author_first_name", "latest_message_author_first_name");
        CONVERSATIONS_PROJECTION_MAP.put("latest_message_author_type", "latest_message_author_type");
        CONVERSATIONS_PROJECTION_MAP.put("is_group", "is_group");
        CONVERSATIONS_PROJECTION_MAP.put("is_pending_accept", "is_pending_accept");
        CONVERSATIONS_PROJECTION_MAP.put("inviter_id", "inviter_id");
        CONVERSATIONS_PROJECTION_MAP.put("inviter_full_name", "inviter_full_name");
        CONVERSATIONS_PROJECTION_MAP.put("inviter_first_name", "inviter_first_name");
        CONVERSATIONS_PROJECTION_MAP.put("inviter_type", "inviter_type");
        CONVERSATIONS_PROJECTION_MAP.put("fatal_error_type", "fatal_error_type");
        CONVERSATIONS_PROJECTION_MAP.put("is_pending_leave", "is_pending_leave");
        CONVERSATIONS_PROJECTION_MAP.put("is_awaiting_event_stream", "is_awaiting_event_stream");
        CONVERSATIONS_PROJECTION_MAP.put("is_awaiting_older_events", "is_awaiting_older_events");
        CONVERSATIONS_PROJECTION_MAP.put("first_event_timestamp", "first_event_timestamp");
        CONVERSATIONS_PROJECTION_MAP.put("packed_participants", "packed_participants");
        HashMap hashmap12 = new HashMap();
        PARTICIPANTS_PROJECTION_MAP = hashmap12;
        hashmap12.put("_id", "_id");
        PARTICIPANTS_PROJECTION_MAP.put("participant_id", "participant_id");
        PARTICIPANTS_PROJECTION_MAP.put("first_name", "first_name");
        PARTICIPANTS_PROJECTION_MAP.put("full_name", "full_name");
        PARTICIPANTS_PROJECTION_MAP.put("type", "type");
        PARTICIPANTS_PROJECTION_MAP.put("active", "active");
        PARTICIPANTS_PROJECTION_MAP.put("sequence", "sequence");
        PARTICIPANTS_PROJECTION_MAP.put("conversation_id", "conversation_id");
        HashMap hashmap13 = new HashMap();
        MESSENGER_SUGGESTIONS_PROJECTION_MAP = hashmap13;
        hashmap13.put("_id", "_id");
        MESSENGER_SUGGESTIONS_PROJECTION_MAP.put("participant_id", "participant_id");
        MESSENGER_SUGGESTIONS_PROJECTION_MAP.put("first_name", "first_name");
        MESSENGER_SUGGESTIONS_PROJECTION_MAP.put("full_name", "full_name");
        HashMap hashmap14 = new HashMap();
        HANGOUT_SUGGESTIONS_PROJECTION_MAP = hashmap14;
        hashmap14.put("_id", "_id");
        HANGOUT_SUGGESTIONS_PROJECTION_MAP.put("participant_id", "participant_id");
        HANGOUT_SUGGESTIONS_PROJECTION_MAP.put("first_name", "first_name");
        HANGOUT_SUGGESTIONS_PROJECTION_MAP.put("full_name", "full_name");
        HashMap hashmap15 = new HashMap();
        MESSAGES_PROJECTION_MAP = hashmap15;
        hashmap15.put("_id", "_id");
        MESSAGES_PROJECTION_MAP.put("message_id", "message_id");
        MESSAGES_PROJECTION_MAP.put("conversation_id", "conversation_id");
        MESSAGES_PROJECTION_MAP.put("author_id", "author_id");
        MESSAGES_PROJECTION_MAP.put("text", "text");
        MESSAGES_PROJECTION_MAP.put("timestamp", "timestamp");
        MESSAGES_PROJECTION_MAP.put("status", "status");
        MESSAGES_PROJECTION_MAP.put("type", "type");
        MESSAGES_PROJECTION_MAP.put("author_first_name", "author_first_name");
        MESSAGES_PROJECTION_MAP.put("author_full_name", "author_full_name");
        MESSAGES_PROJECTION_MAP.put("author_type", "author_type");
        MESSAGES_PROJECTION_MAP.put("image_url", "image_url");
        HashMap hashmap16 = new HashMap();
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP = hashmap16;
        hashmap16.put("_id", "_id");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("message_id", "message_id");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("conversation_id", "conversation_id");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("author_id", "author_id");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("text", "text");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("image_url", "image_url");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("timestamp", "timestamp");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("status", "status");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("type", "type");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("notification_seen", "notification_seen");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("author_full_name", "author_full_name");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("author_first_name", "author_first_name");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("author_type", "author_type");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("conversation_muted", "conversation_muted");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("conversation_group", "conversation_group");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("conversation_name", "conversation_name");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("generated_name", "generated_name");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("conversation_pending_accept", "conversation_pending_accept");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("conversation_pending_leave", "conversation_pending_leave");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("inviter_id", "inviter_id");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("inviter_full_name", "inviter_full_name");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("inviter_first_name", "inviter_first_name");
        MESSAGE_NOTIFICATIONS_PROJECTION_MAP.put("inviter_type", "inviter_type");
        HashMap hashmap17 = new HashMap();
        PHOTO_HOME_MAP = hashmap17;
        hashmap17.put("_id", "_id");
        PHOTO_HOME_MAP.put("photo_count", "photo_count");
        PHOTO_HOME_MAP.put("height", "height");
        PHOTO_HOME_MAP.put("image", "image");
        PHOTO_HOME_MAP.put("notification_count", "notification_count");
        PHOTO_HOME_MAP.put("photo_id", "photo_id");
        PHOTO_HOME_MAP.put("photo_home_key", "photo_home_key");
        PHOTO_HOME_MAP.put("size", "size");
        PHOTO_HOME_MAP.put("sort_order", "sort_order");
        PHOTO_HOME_MAP.put("timestamp", "timestamp");
        PHOTO_HOME_MAP.put("type", "type");
        PHOTO_HOME_MAP.put("url", "url");
        PHOTO_HOME_MAP.put("width", "width");
        HashMap hashmap18 = new HashMap();
        PHOTO_NOTIFICATION_MAP = hashmap18;
        hashmap18.put("_count", "notification_count");
        HashMap hashmap19 = new HashMap();
        ALBUM_VIEW_MAP = hashmap19;
        hashmap19.put("_id", "_id");
        ALBUM_VIEW_MAP.put("album_id", "album_id");
        ALBUM_VIEW_MAP.put("album_type", "album_type");
        ALBUM_VIEW_MAP.put("album_key", "album_key");
        ALBUM_VIEW_MAP.put("cover_photo_id", "cover_photo_id");
        ALBUM_VIEW_MAP.put("entity_version", "entity_version");
        ALBUM_VIEW_MAP.put("height", "height");
        ALBUM_VIEW_MAP.put("is_activity", "is_activity");
        ALBUM_VIEW_MAP.put("owner_id", "owner_id");
        ALBUM_VIEW_MAP.put("photo_count", "photo_count");
        ALBUM_VIEW_MAP.put("photo_id", "photo_id");
        ALBUM_VIEW_MAP.put("size", "size");
        ALBUM_VIEW_MAP.put("sort_order", "sort_order");
        ALBUM_VIEW_MAP.put("stream_id", "stream_id");
        ALBUM_VIEW_MAP.put("timestamp", "timestamp");
        ALBUM_VIEW_MAP.put("title", "title");
        ALBUM_VIEW_MAP.put("url", "url");
        ALBUM_VIEW_MAP.put("width", "width");
        HashMap hashmap20 = new HashMap();
        EVENT_PEOPLE_VIEW_MAP = hashmap20;
        hashmap20.put("_id", "_id");
        EVENT_PEOPLE_VIEW_MAP.put("event_id", "event_id");
        EVENT_PEOPLE_VIEW_MAP.put("gaia_id", "gaia_id");
        EVENT_PEOPLE_VIEW_MAP.put("person_id", "person_id");
        EVENT_PEOPLE_VIEW_MAP.put("name", "name");
        EVENT_PEOPLE_VIEW_MAP.put("sort_key", "sort_key");
        EVENT_PEOPLE_VIEW_MAP.put("avatar", "avatar");
        EVENT_PEOPLE_VIEW_MAP.put("last_updated_time", "last_updated_time");
        EVENT_PEOPLE_VIEW_MAP.put("profile_type", "profile_type");
        EVENT_PEOPLE_VIEW_MAP.put("profile_state", "profile_state");
        EVENT_PEOPLE_VIEW_MAP.put("in_my_circles", "in_my_circles");
        EVENT_PEOPLE_VIEW_MAP.put("blocked", "blocked");
        HashMap hashmap21 = new HashMap();
        PHOTO_VIEW_MAP = hashmap21;
        hashmap21.put("_id", "_id");
        PHOTO_VIEW_MAP.put("_count", "COUNT(*) AS _count");
        PHOTO_VIEW_MAP.put("action_state", "action_state");
        PHOTO_VIEW_MAP.put("album_id", "album_id");
        PHOTO_VIEW_MAP.put("album_name", "album_name");
        PHOTO_VIEW_MAP.put("album_stream", "album_stream");
        PHOTO_VIEW_MAP.put("comment_count", "comment_count");
        PHOTO_VIEW_MAP.put("description", "description");
        PHOTO_VIEW_MAP.put("downloadable", "downloadable");
        PHOTO_VIEW_MAP.put("entity_version", "entity_version");
        PHOTO_VIEW_MAP.put("height", "height");
        PHOTO_VIEW_MAP.put("owner_id", "owner_id");
        PHOTO_VIEW_MAP.put("owner_name", "owner_name");
        PHOTO_VIEW_MAP.put("owner_avatar_url", "owner_avatar_url");
        PHOTO_VIEW_MAP.put("pending_status", "pending_status");
        PHOTO_VIEW_MAP.put("photo_id", "photo_id");
        PHOTO_VIEW_MAP.put("plusone_by_me", "plusone_by_me");
        PHOTO_VIEW_MAP.put("plusone_count", "plusone_count");
        PHOTO_VIEW_MAP.put("plusone_data", "plusone_data");
        PHOTO_VIEW_MAP.put("plusone_id", "plusone_id");
        PHOTO_VIEW_MAP.put("fingerprint", "fingerprint");
        PHOTO_VIEW_MAP.put("timestamp", "timestamp");
        PHOTO_VIEW_MAP.put("title", "title");
        PHOTO_VIEW_MAP.put("upload_status", "upload_status");
        PHOTO_VIEW_MAP.put("url", "url");
        PHOTO_VIEW_MAP.put("video_data", "video_data");
        PHOTO_VIEW_MAP.put("is_panorama", "is_panorama");
        PHOTO_VIEW_MAP.put("width", "width");
        PHOTO_VIEW_MAP.put("_count", "count(*)  as _count");
        PHOTO_VIEW_MAP.put("photo_of_user_id", "NULL AS photo_of_user_id");
        PHOTOS_BY_ALBUM_VIEW_MAP = new HashMap(PHOTO_VIEW_MAP);
        HashMap hashmap22 = new HashMap(PHOTO_VIEW_MAP);
        PHOTOS_BY_EVENT_VIEW_MAP = hashmap22;
        hashmap22.put("event_id", "event_id");
        PHOTOS_BY_STREAM_VIEW_MAP = new HashMap(PHOTO_VIEW_MAP);
        HashMap hashmap23 = new HashMap(PHOTO_VIEW_MAP);
        PHOTOS_BY_USER_VIEW_MAP = hashmap23;
        hashmap23.put("photo_of_user_id", "photos_of_user.photo_of_user_id as photo_of_user_id");
        HashMap hashmap24 = new HashMap();
        PHOTO_SHAPE_VIEW_MAP = hashmap24;
        hashmap24.put("photo_id", "photo_id");
        PHOTO_SHAPE_VIEW_MAP.put("bounds", "bounds");
        PHOTO_SHAPE_VIEW_MAP.put("creator_id", "creator_id");
        PHOTO_SHAPE_VIEW_MAP.put("shape_id", "shape_id");
        PHOTO_SHAPE_VIEW_MAP.put("status", "status");
        PHOTO_SHAPE_VIEW_MAP.put("subject_id", "subject_id");
        PHOTO_SHAPE_VIEW_MAP.put("creator_name", "creator_name");
        PHOTO_SHAPE_VIEW_MAP.put("subject_name", "subject_name");
        HashMap hashmap25 = new HashMap();
        PHOTO_COMMENTS_MAP = hashmap25;
        hashmap25.put("_id", "_id");
        PHOTO_COMMENTS_MAP.put("author_id", "author_id");
        PHOTO_COMMENTS_MAP.put("comment_id", "comment_id");
        PHOTO_COMMENTS_MAP.put("content", "content");
        PHOTO_COMMENTS_MAP.put("create_time", "create_time");
        PHOTO_COMMENTS_MAP.put("plusone_data", "plusone_data");
        PHOTO_COMMENTS_MAP.put("truncated", "truncated");
        PHOTO_COMMENTS_MAP.put("update_time", "update_time");
        PHOTO_COMMENTS_MAP.put("owner_name", "contacts.name as owner_name");
        PHOTO_COMMENTS_MAP.put("avatar", "contacts.avatar as avatar");
        HashMap hashmap26 = new HashMap();
        NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP = hashmap26;
        hashmap26.put("_id", "_id");
        NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP.put("name", "name");
        NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP.put("time", "time");
        NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP.put("network_duration", "network_duration");
        NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP.put("process_duration", "process_duration");
        NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP.put("sent", "sent");
        NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP.put("recv", "recv");
        NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP.put("req_count", "req_count");
        NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP.put("exception", "exception");
        HashMap hashmap27 = new HashMap();
        NETWORK_DATA_STATS_PROJECTION_MAP = hashmap27;
        hashmap27.put("_id", "_id");
        NETWORK_DATA_STATS_PROJECTION_MAP.put("name", "name");
        NETWORK_DATA_STATS_PROJECTION_MAP.put("first", "first");
        NETWORK_DATA_STATS_PROJECTION_MAP.put("last", "last");
        NETWORK_DATA_STATS_PROJECTION_MAP.put("network_duration", "network_duration");
        NETWORK_DATA_STATS_PROJECTION_MAP.put("process_duration", "process_duration");
        NETWORK_DATA_STATS_PROJECTION_MAP.put("sent", "sent");
        NETWORK_DATA_STATS_PROJECTION_MAP.put("recv", "recv");
        NETWORK_DATA_STATS_PROJECTION_MAP.put("req_count", "req_count");
        HashMap hashmap28 = new HashMap();
        PLATFORM_AUDIENCE_PROJECTION_MAP = hashmap28;
        hashmap28.put("package_name", "package_name");
        PLATFORM_AUDIENCE_PROJECTION_MAP.put("audience_data", "audience_data");
        HashMap hashmap29 = new HashMap();
        PLUS_PAGES_PROJECTION_MAP = hashmap29;
        hashmap29.put("gaia_id", "gaia_id");
        PLUS_PAGES_PROJECTION_MAP.put("name", "name");
        HashMap hashmap30 = new HashMap();
        SQUARES_PROJECTION_MAP = hashmap30;
        hashmap30.put("_id", "_id");
        SQUARES_PROJECTION_MAP.put("square_id", "square_id");
        SQUARES_PROJECTION_MAP.put("square_name", "square_name");
        SQUARES_PROJECTION_MAP.put("tagline", "tagline");
        SQUARES_PROJECTION_MAP.put("photo_url", "photo_url");
        SQUARES_PROJECTION_MAP.put("about_text", "about_text");
        SQUARES_PROJECTION_MAP.put("joinability", "joinability");
        SQUARES_PROJECTION_MAP.put("member_count", "member_count");
        SQUARES_PROJECTION_MAP.put("membership_status", "membership_status");
        SQUARES_PROJECTION_MAP.put("is_member", "is_member");
        SQUARES_PROJECTION_MAP.put("suggested", "suggested");
        SQUARES_PROJECTION_MAP.put("post_visibility", "post_visibility");
        SQUARES_PROJECTION_MAP.put("can_see_members", "can_see_members");
        SQUARES_PROJECTION_MAP.put("can_see_posts", "can_see_posts");
        SQUARES_PROJECTION_MAP.put("can_join", "can_join");
        SQUARES_PROJECTION_MAP.put("can_request_to_join", "can_request_to_join");
        SQUARES_PROJECTION_MAP.put("can_share", "can_share");
        SQUARES_PROJECTION_MAP.put("can_invite", "can_invite");
        SQUARES_PROJECTION_MAP.put("notifications_enabled", "notifications_enabled");
        SQUARES_PROJECTION_MAP.put("square_streams", "square_streams");
        SQUARES_PROJECTION_MAP.put("inviter_gaia_id", "inviter_gaia_id");
        SQUARES_PROJECTION_MAP.put("inviter_name", "contacts.name as inviter_name");
        SQUARES_PROJECTION_MAP.put("inviter_photo_url", "contacts.avatar as inviter_photo_url");
        SQUARES_PROJECTION_MAP.put("sort_index", "sort_index");
        SQUARES_PROJECTION_MAP.put("last_sync", "last_sync");
        SQUARES_PROJECTION_MAP.put("last_members_sync", "last_members_sync");
        SQUARES_PROJECTION_MAP.put("invitation_dismissed", "invitation_dismissed");
        SQUARES_PROJECTION_MAP.put("suggestion_sort_index", "suggestion_sort_index");
        SQUARES_PROJECTION_MAP.put("auto_subscribe", "auto_subscribe");
        SQUARES_PROJECTION_MAP.put("disable_subscription", "disable_subscription");
        SQUARES_PROJECTION_MAP.put("unread_count", "unread_count");
        HashMap hashmap31 = new HashMap(CONTACTS_PROJECTION_MAP);
        SQUARE_CONTACTS_PROJECTION_MAP = hashmap31;
        hashmap31.put("membership_status", "membership_status");
        HashMap hashmap32 = new HashMap();
        EMOTISHARE_PROJECTION_MAP = hashmap32;
        hashmap32.put("_id", "_id");
        EMOTISHARE_PROJECTION_MAP.put("type", "type");
        EMOTISHARE_PROJECTION_MAP.put("data", "data");
        EMOTISHARE_PROJECTION_MAP.put("generation", "generation");
    }
    
    //===========================================================================
    //						Constructor
    //===========================================================================
    public EsProvider() {
    }
    
    public static void analyzeDatabase(Context context, EsAccount esaccount) {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        sqlitedatabase.execSQL("ANALYZE");
        sqlitedatabase.execSQL("ANALYZE sqlite_master");
    }
    
    public static Uri.Builder appendAccountParameter(Uri.Builder builder, EsAccount esaccount) {
        return builder.appendQueryParameter("account", String.valueOf(esaccount.getIndex()));
    }
    
    public static Uri appendAccountParameter(Uri uri, EsAccount esaccount) {
        return appendAccountParameter(uri.buildUpon(), esaccount).build();
    }
    
    public static Uri buildActivityViewUri(EsAccount esaccount, String s) {
        Uri.Builder builder = ACTIVITY_VIEW_BY_ACTIVITY_ID_URI.buildUpon();
        builder.appendPath(s);
        appendAccountParameter(builder, esaccount);
        return builder.build();
    }
    
    public static Uri buildLocationQueryUri(EsAccount esaccount, String s) {
        Uri.Builder builder = LOCATION_QUERIES_VIEW_URI.buildUpon();
        builder.appendPath("query").appendPath(s);
        appendAccountParameter(builder, esaccount);
        return builder.build();
    }
    
    public static Uri buildMessagesUri(EsAccount esaccount, long l) {
        Uri.Builder builder = MESSAGES_BY_CONVERSATION_URI.buildUpon();
        builder.appendPath(Long.toString(l));
        builder.appendQueryParameter("account", String.valueOf(esaccount.getIndex()));
        return builder.build();
    }
    
    public static Uri buildPanoramaUri(File file) {
        return PANORAMA_IMAGE_URI.buildUpon().appendQueryParameter("file", file.getPath()).build();
    }

    public static Uri buildParticipantsUri(EsAccount esaccount) {
        Uri.Builder builder = PARTICIPANTS_URI.buildUpon();
        builder.appendQueryParameter("account", String.valueOf(esaccount.getIndex()));
        return builder.build();
    }

    public static Uri buildParticipantsUri(EsAccount esaccount, long l){
        Uri.Builder builder = PARTICIPANTS_URI.buildUpon();
        builder.appendPath("conversation").appendPath(Long.toString(l));
        builder.appendQueryParameter("account", String.valueOf(esaccount.getIndex()));
        return builder.build();
    }
    
    public static Uri buildPeopleQueryUri(EsAccount esaccount, String s, boolean flag, boolean flag1, String s1, int i) {
        android.net.Uri.Builder builder = CONTACTS_QUERY_URI.buildUpon();
        if(s == null)
            s = "";
        builder.appendPath(s);
        builder.appendQueryParameter("limit", String.valueOf(i));
        builder.appendQueryParameter("self_gaia_id", esaccount.getGaiaId());
        String s2;
        String s3;
        if(flag)
            s2 = "true";
        else
            s2 = "false";
        builder.appendQueryParameter("plus_pages", s2);
        if(flag1)
            s3 = "true";
        else
            s3 = "false";
        builder.appendQueryParameter("in_circles", s3);
        if(s1 != null)
            builder.appendQueryParameter("activity_id", s1);
        appendAccountParameter(builder, esaccount);
        return builder.build();
    }

    public static Uri buildStreamUri(EsAccount esaccount, String s) {
        android.net.Uri.Builder builder = ACTIVITIES_STREAM_VIEW_URI.buildUpon();
        builder.appendPath("stream").appendPath(s);
        appendAccountParameter(builder, esaccount);
        return builder.build();
    }
    
    public static synchronized void cleanupData(Context context, EsAccount esaccount, boolean flag)
    {
    	if(!flag) {
    		long lastDatabaseCleanupTimestamp = EsAccountsData.getLastDatabaseCleanupTimestamp(context, esaccount);
    		if(lastDatabaseCleanupTimestamp < 0x25c3f80L) {
    			return;
    		}
    	}
    	
    	// cleanup
    	long start = System.currentTimeMillis();
    	EsMediaCache.cleanup();
    	SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        File file = new File(sqlitedatabase.getPath());
        long length = file.length();
        sqlitedatabase.beginTransaction();
        EsPostsData.cleanupData(sqlitedatabase);
        EsNotificationData.cleanupData(sqlitedatabase);
        EsSquaresData.cleanupData(sqlitedatabase);
        EsEmotiShareData.cleanupData(sqlitedatabase);
        EsPhotosData.cleanupData(sqlitedatabase, esaccount);
        EsConversationsData.cleanupData(sqlitedatabase);
        EsNetworkData.cleanupData();
        EsDeepLinkInstallsData.cleanupData(sqlitedatabase);
        EsPeopleData.cleanupData(sqlitedatabase, esaccount);
        sqlitedatabase.setTransactionSuccessful();
        sqlitedatabase.endTransaction();
        EsAccountsData.saveLastDatabaseCleanupTimestamp(context, esaccount, System.currentTimeMillis());
    	
        if(EsLog.isLoggable("EsProvider", 4)) {
            long l2 = (new File(sqlitedatabase.getPath())).length();
            StringBuffer stringbuffer = new StringBuffer();
            long l3 = System.currentTimeMillis() - start;
            stringbuffer.append(l3 / 1000L).append(".").append(l3 % 1000L).append(" seconds");
            Log.i("EsProvider", (new StringBuilder(">>>>> cleanup db took ")).append(stringbuffer.toString()).append(" old size: ").append(length).append(", new size: ").append(l2).toString());
        }
    }
    
	@Override
	public int delete(Uri uri, String s, String as[]) {
		throw new IllegalArgumentException((new StringBuilder("Delete not supported: ")).append(uri).toString());
	}

	@Override
	public String getType(Uri uri) {
		String s = null;
		switch(URI_MATCHER.match(uri)) {
		case 1:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.accounts";
			break;
		case 21:
		case 22:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.activities";
			break;
		case 30:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.comments";
			break;
		case 40:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.locations";
			break;
		case 50:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.notifications";
			break;
		case 70:
		case 72:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.contacts";
			break;
		case 100:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.conversations";
			break;
		case 110:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.participants";
			break;
		case 120:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.messages";
			break;
		case 160:
			s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.message_notifications";
			break;
		default:
			throw new IllegalArgumentException((new StringBuilder("Unknown URI: ")).append(uri).toString());
		}
        return s;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues)
    {
        throw new IllegalStateException((new StringBuilder("Insert not supported ")).append(uri).toString());
    }

	@Override
	public boolean onCreate() {
		return true;
	}
	
	private static String[] prependArgs(String as[], String as1[])
    {
        if(null == as1 || 0 == as1.length) {
        	return as;
        }
        
        int i;
        int j;
        String as2[] = as;
        if(as == null)
            i = 0;
        else
            i = as.length;
        j = as1.length;
        as2 = new String[i + j];
        System.arraycopy(as1, 0, as2, 0, j);
        if(i > 0)
            System.arraycopy(as, 0, as2, j, i);
        return as2;
    }
	
	private static boolean isInProjection(String as[], String as1[])
    {
        if(null == as) {
        	return true;
        }
        
        if(as1.length == 1)
        {
            String s1 = as1[0];
            int i1 = as.length;
            for(int j1 = 0; j1 < i1; j1++)
                if(s1.equals(as[j1]))
                    return true;

        } else
        {
            int i = as.length;
            for(int j = 0; j < i; j++)
            {
                String s = as[j];
                int k = as1.length;
                for(int l = 0; l < k; l++)
                    if(as1[l].equals(s))
                        return true;

            }

        }
        return false;
    }
	
	private static void preparePeopleSearchQuery(SQLiteQueryBuilder sqlitequerybuilder, String s, boolean flag, String s1, String s2, boolean flag1, String s3)
    {
        String as[] = s.toLowerCase().split("[\\u0009\\u000A\\u000B\\u000C\\u000D\\u0020\\u0085\\u00A0\\u1680\\u180E\\u2000\\u2001\\u2002\\u2003\\u2004\\u2005\\u2006\\u2007\\u2008\\u2009\\u200A\\u2028\\u2029\\u202F\\u205F\\u3000]");
        String s4 = "";
        for(int i = 0; i < as.length; i++)
            s4 = (new StringBuilder()).append(s4).append("SELECT contacts").append(".person_id").append(" AS filtered_person_id, MIN((CASE WHEN ").append("search_key_type=").append(1).append(" THEN search_key").append(" ELSE NULL END)) AS email").append(" FROM contacts").append(" JOIN contact_search").append(" ON (contacts").append(".person_id").append("=search_person_id").append(") WHERE ").append("search_key GLOB ").append(DatabaseUtils.sqlEscapeString((new StringBuilder()).append(as[i]).append('*').toString())).append(" AND in_my_circles").append("!=0 GROUP BY filtered_person_id, ").append("search_key_type INTERSECT ").toString();

        String s5 = s4.substring(0, -11 + s4.length());
        if(!TextUtils.isEmpty(s3))
            s5 = (new StringBuilder()).append(s5).append(" UNION SELECT ").append("contacts.").append("person_id AS filtered_person_id, ").append(" NULL AS email").append(" FROM contacts").append(" WHERE gaia_id").append(" IN (").append(s3).append(") AND (").append("name LIKE ").append(DatabaseUtils.sqlEscapeString((new StringBuilder()).append(s).append('%').toString())).append(" OR name").append(" LIKE ").append(DatabaseUtils.sqlEscapeString((new StringBuilder("% ")).append(s).append('%').toString())).append(")").toString();
        String s6 = (new StringBuilder()).append(s5).append(" LIMIT ").append(s1).toString();
        sqlitequerybuilder.setTables((new StringBuilder("contacts JOIN (")).append(s6).append(") ON (contacts").append(".person_id").append("=filtered_person_id) LEFT OUTER JOIN ").append("circle_contact ON (").append("contacts.").append("person_id=").append("circle_contact.").append("link_person_id)").toString());
        sqlitequerybuilder.appendWhere("gaia_id");
        sqlitequerybuilder.appendWhere(" != '");
        sqlitequerybuilder.appendWhere(s2);
        sqlitequerybuilder.appendWhere("'");
        if(!flag)
        {
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("profile_type");
            sqlitequerybuilder.appendWhere(" != ");
            sqlitequerybuilder.appendWhere(Integer.toString(2));
        }
        if(TextUtils.isEmpty(s))
        {
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("0");
        }
        if(!flag1)
        {
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("in_my_circles");
            sqlitequerybuilder.appendWhere(" = 0");
        }
    }

	@Override
	public Cursor query(Uri uri, String as[], String s, String as1[], String s1)
    {
        int i;
        String s3;
        String s4;
        String as2[];
        int j;
        String s2 = uri.getQueryParameter("account");
        if(s2 == null)
            throw new IllegalArgumentException((new StringBuilder("Every URI must have the 'account' parameter specified.\nURI: ")).append(uri).toString());
        i = Integer.parseInt(s2);
        s3 = null;
        s4 = uri.getQueryParameter("limit");
        as2 = as1;
        j = URI_MATCHER.match(uri);
        if(EsLog.isLoggable("EsProvider", 3))
            Log.d("EsProvider", (new StringBuilder("QUERY URI: ")).append(uri).append(" -> ").append(j).toString());
        SQLiteQueryBuilder sqlitequerybuilder = new SQLiteQueryBuilder();
        String s5;
        switch(j) {
        case 1:
            sqlitequerybuilder.setTables("account_status");
            sqlitequerybuilder.setProjectionMap(ACCOUNTS_PROJECTION_MAP);
            s5 = null;
        	break;
        case 20:
        	sqlitequerybuilder.setTables("activity_view");
            sqlitequerybuilder.setProjectionMap(ACTIVITIES_VIEW_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        case 21:
        	sqlitequerybuilder.setTables("activities_stream_view");
            sqlitequerybuilder.appendWhere("stream_key");
            sqlitequerybuilder.appendWhere("=?");
            String as21[] = new String[1];
            as21[0] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as21);
            sqlitequerybuilder.setProjectionMap(ACTIVITIES_VIEW_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        case 22:
        	sqlitequerybuilder.appendWhere("activity_id");
            sqlitequerybuilder.appendWhere("=?");
            String as22[] = new String[1];
            as22[0] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as22);
            sqlitequerybuilder.setTables("activity_view");
            sqlitequerybuilder.setProjectionMap(ACTIVITIES_VIEW_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        case 23:
        	 sqlitequerybuilder.setTables("activities_stream_view");
             sqlitequerybuilder.appendWhere("('g:'||author_id) IN ( SELECT link_person_id FROM circle_contact WHERE link_circle_id=?)");
             String as20[] = new String[1];
             as20[0] = (String)uri.getPathSegments().get(1);
             as2 = prependArgs(as2, as20);
             sqlitequerybuilder.setProjectionMap(ACTIVITIES_VIEW_PROJECTION_MAP);
             s5 = s1;
             if(s4 != null)
             {
                 long k = Long.parseLong(s4) - 20L;
                 s3 = null;
                 if(k > 0)
                 {
                     s4 = Long.toString(20L);
                     s3 = null;
                 }
             } else
             {
                 s4 = Long.toString(20L);
                 s3 = null;
             }
        	break;
        case 24:
        	sqlitequerybuilder.setTables("activities");
            sqlitequerybuilder.setProjectionMap(ACTIVITY_SUMMARY_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        case 30:
        	sqlitequerybuilder.setTables("comments_view");
            sqlitequerybuilder.appendWhere("activity_id");
            sqlitequerybuilder.appendWhere("=?");
            String as19[] = new String[1];
            as19[0] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as19);
            sqlitequerybuilder.setProjectionMap(COMMENTS_VIEW_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        case 40:
        	sqlitequerybuilder.setTables("location_queries_view");
            sqlitequerybuilder.appendWhere("key");
            sqlitequerybuilder.appendWhere("=?");
            String as18[] = new String[1];
            as18[0] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as18);
            sqlitequerybuilder.setProjectionMap(LOCATION_QUERIES_VIEW_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        case 50:
        	sqlitequerybuilder.setTables("notifications");
            sqlitequerybuilder.setProjectionMap(NOTIFICATIONS_PROJECTION_MAP);
            if(s4 != null)
            {
                if(Long.parseLong(s4) > 200L)
                    s4 = Long.toString(200L);
            } else
            {
                s4 = Long.toString(200L);
            }
            s3 = null;
            s5 = null;
        	break;
        case 60:
        	if(isInProjection(as, new String[] {
        		    "member_ids"
        		}))
        		        {
        		            sqlitequerybuilder.setTables((new StringBuilder("circles LEFT OUTER JOIN (")).append("SELECT link_circle_id,link_person_id FROM circle_contact JOIN contacts AS c  ON (c.person_id=link_person_id) ORDER BY c.sort_key, UPPER(c.name)").append(") AS ").append("circle_contact ON ( ").append("circle_contact.").append("link_circle_id = ").append("circles.").append("circle_id)").toString());
        		            s3 = "circle_id";
        		        } else
        		        {
        		            sqlitequerybuilder.setTables("circles");
        		            s3 = null;
        		        }
        		        sqlitequerybuilder.setProjectionMap(CIRCLES_PROJECTION_MAP);
        		        s5 = null;
        	break;
        case 70:
        	String s13;
            boolean flag5;
            if(isInProjection(as, new String[] {"contact_update_time", "contact_proto", "profile_update_time", "profile_proto"}))
                s13 = "contacts LEFT OUTER JOIN profiles ON (contacts.person_id=profiles.profile_person_id)";
            else
                s13 = "contacts";
            flag5 = isInProjection(as, new String[] {
                "packed_circle_ids"
            });
            s3 = null;
            if(flag5)
            {
                s13 = (new StringBuilder()).append(s13).append(" LEFT OUTER JOIN circle_contact ON ( circle_contact.link_person_id = contacts.person_id)").toString();
                s3 = "person_id";
            }
            sqlitequerybuilder.setTables(s13);
            sqlitequerybuilder.setProjectionMap(CONTACTS_PROJECTION_MAP);
            s5 = "sort_key, UPPER(name)";
        	break;
        case 71:
        	sqlitequerybuilder.setTables("contacts JOIN circle_contact ON (contacts.person_id=circle_contact.link_person_id) JOIN circles ON (circle_contact.link_circle_id = circles.circle_id)");
            sqlitequerybuilder.appendWhere("person_id");
            sqlitequerybuilder.appendWhere(" IN (");
            sqlitequerybuilder.appendWhere("SELECT ");
            sqlitequerybuilder.appendWhere("link_person_id");
            sqlitequerybuilder.appendWhere(" FROM ");
            sqlitequerybuilder.appendWhere("circle_contact");
            sqlitequerybuilder.appendWhere(" WHERE ");
            sqlitequerybuilder.appendWhere("link_circle_id");
            sqlitequerybuilder.appendWhere("=?");
            String as16[] = new String[1];
            as16[0] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as16);
            sqlitequerybuilder.appendWhere(")");
            sqlitequerybuilder.setProjectionMap(CONTACTS_PROJECTION_MAP);
            s5 = "UPPER(name)";
            s3 = "person_id";
        	break;
        case 72:
        	sqlitequerybuilder.appendWhere("person_id");
            sqlitequerybuilder.appendWhere("=?");
            String as17[] = new String[1];
            as17[0] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as17);
            if(isInProjection(as, new String[] {"contact_update_time", "contact_proto", "profile_update_time", "profile_proto"}))
                s13 = "contacts LEFT OUTER JOIN profiles ON (contacts.person_id=profiles.profile_person_id)";
            else
                s13 = "contacts";
            flag5 = isInProjection(as, new String[] {
                "packed_circle_ids"
            });
            s3 = null;
            if(flag5)
            {
                s13 = (new StringBuilder()).append(s13).append(" LEFT OUTER JOIN circle_contact ON ( circle_contact.link_person_id = contacts.person_id)").toString();
                s3 = "person_id";
            }
            sqlitequerybuilder.setTables(s13);
            sqlitequerybuilder.setProjectionMap(CONTACTS_PROJECTION_MAP);
            s5 = "sort_key, UPPER(name)";
        	break;
        case 73:
        	 String s11 = "contacts JOIN suggested_people ON (contacts.person_id=suggested_people.suggested_person_id)";
             boolean flag3 = isInProjection(as, new String[] {
                 "packed_circle_ids"
             });
             s3 = null;
             if(flag3)
             {
                 s11 = (new StringBuilder()).append(s11).append(" LEFT OUTER JOIN circle_contact ON ( circle_contact.link_person_id = contacts.person_id)").toString();
                 s3 = "suggested_people._id";
             }
             sqlitequerybuilder.setTables(s11);
             sqlitequerybuilder.setProjectionMap(SUGGESTED_PEOPLE_PROJECTION_MAP);
             s = "dismissed=0 AND blocked=0";
             s5 = "CAST (category_sort_key AS INTEGER),sort_order";
        	break;
        case 74:
        	List list = uri.getPathSegments();
            String s7;
            String s8;
            boolean flag;
            boolean flag1;
            String s9;
            String s10;
            boolean flag2;
            if(list.size() == 2)
                s7 = "";
            else
                s7 = ((String)list.get(2)).trim();
            s8 = uri.getQueryParameter("self_gaia_id");
            flag = "true".equals(uri.getQueryParameter("plus_pages"));
            flag1 = "true".equals(uri.getQueryParameter("in_circles"));
            s9 = uri.getQueryParameter("activity_id");
            s10 = null;
            if(s9 != null)
                s10 = (new StringBuilder()).append((new StringBuilder("SELECT author_id FROM activities WHERE activity_id =  ")).append(DatabaseUtils.sqlEscapeString(s9)).toString()).append(" UNION ").append((new StringBuilder("SELECT author_id FROM activity_comments WHERE activity_id = ")).append(DatabaseUtils.sqlEscapeString(s9)).toString()).toString();
            if(s7.startsWith("+") || s7.startsWith("@"))
                s7 = s7.substring(1);
            preparePeopleSearchQuery(sqlitequerybuilder, s7, flag, s4, s8, flag1, s10);
            s3 = "person_id";
            sqlitequerybuilder.setProjectionMap(CONTACTS_SEARCH_PROJECTION_MAP);
            s5 = "UPPER(name)";
            flag2 = TextUtils.isEmpty(s10);
            s4 = null;
            if(!flag2)
            {
                s5 = (new StringBuilder("gaia_id IN (")).append(s10).append(") DESC,").append(s5).toString();
                s4 = null;
            }
        	break;
        case 75:
        	 String s12 = "contacts INNER JOIN square_contact ON (contacts.person_id=square_contact.link_person_id)";
             boolean flag4 = isInProjection(as, new String[] {
                 "packed_circle_ids"
             });
             s3 = null;
             if(flag4)
             {
                 s12 = (new StringBuilder()).append(s12).append(" LEFT OUTER JOIN circle_contact ON ( circle_contact.link_person_id = contacts.person_id)").toString();
                 s3 = "person_id";
             }
             sqlitequerybuilder.setTables(s12);
             sqlitequerybuilder.appendWhere("person_id");
             sqlitequerybuilder.appendWhere(" IN (");
             sqlitequerybuilder.appendWhere("SELECT ");
             sqlitequerybuilder.appendWhere("link_person_id");
             sqlitequerybuilder.appendWhere(" FROM ");
             sqlitequerybuilder.appendWhere("square_contact");
             sqlitequerybuilder.appendWhere(" WHERE ");
             sqlitequerybuilder.appendWhere("link_square_id");
             sqlitequerybuilder.appendWhere("=?");
             String as15[] = new String[1];
             as15[0] = (String)uri.getPathSegments().get(2);
             as2 = prependArgs(as2, as15);
             sqlitequerybuilder.appendWhere(")");
             sqlitequerybuilder.setProjectionMap(SQUARE_CONTACTS_PROJECTION_MAP);
             s5 = "UPPER(name)";
        	break;
        case 100:
        	sqlitequerybuilder.setTables("conversations_view");
            sqlitequerybuilder.setProjectionMap(CONVERSATIONS_PROJECTION_MAP);
            s5 = s1;
            s3 = null;
        	break;
        case 110:
        	sqlitequerybuilder.setTables("participants_view");
            sqlitequerybuilder.appendWhere("conversation_id");
            sqlitequerybuilder.appendWhere("=?");
            String as14[] = new String[1];
            as14[0] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as14);
            sqlitequerybuilder.setProjectionMap(PARTICIPANTS_PROJECTION_MAP);
            s5 = s1;
            s3 = null;
        	break;
        case 115:
        	sqlitequerybuilder.setTables("messenger_suggestions");
            sqlitequerybuilder.setProjectionMap(MESSENGER_SUGGESTIONS_PROJECTION_MAP);
            s5 = s1;
            s3 = null;
        	break;
        case 116:
        	sqlitequerybuilder.setTables("hangout_suggestions");
            sqlitequerybuilder.setProjectionMap(HANGOUT_SUGGESTIONS_PROJECTION_MAP);
            s5 = s1;
            s3 = null;
        	break;
        case 120:
        	sqlitequerybuilder.setTables("messages_view");
            sqlitequerybuilder.appendWhere("conversation_id");
            sqlitequerybuilder.appendWhere("=?");
            String as13[] = new String[1];
            as13[0] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as13);
            sqlitequerybuilder.setProjectionMap(MESSAGES_PROJECTION_MAP);
            s5 = s1;
            s3 = null;
        	break;
        case 130:
        	sqlitequerybuilder.setTables("photo_home_view");
            sqlitequerybuilder.setProjectionMap(PHOTO_HOME_MAP);
            s5 = s1;
            s3 = null;
        	break;
        case 132:
        	sqlitequerybuilder.setTables("album_view");
            sqlitequerybuilder.appendWhere("owner_id");
            sqlitequerybuilder.appendWhere("=?");
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("title");
            sqlitequerybuilder.appendWhere(" IS NOT NULL");
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("url");
            sqlitequerybuilder.appendWhere(" IS NOT NULL");
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("is_activity");
            sqlitequerybuilder.appendWhere(" = 0");
            String as12[] = new String[1];
            as12[0] = (String)uri.getPathSegments().get(1);
            as2 = prependArgs(as2, as12);
            sqlitequerybuilder.setProjectionMap(ALBUM_VIEW_MAP);
            s5 = s1;
            s3 = null;
        	break;
        case 134:
        	sqlitequerybuilder.setTables("photo_view");
            sqlitequerybuilder.appendWhere("photo_id");
            sqlitequerybuilder.appendWhere("=?");
            String as10[] = new String[1];
            as10[0] = (String)uri.getPathSegments().get(1);
            as2 = prependArgs(as2, as10);
            sqlitequerybuilder.setProjectionMap(PHOTO_VIEW_MAP);
            sqlitequerybuilder.setDistinct(true);
            s4 = "1";
            s5 = s1;
            s3 = null;
        	break;
        case 135:
        	sqlitequerybuilder.setTables("photos_by_album_view");
            sqlitequerybuilder.appendWhere("album_id");
            sqlitequerybuilder.appendWhere("=?");
            String as9[] = new String[1];
            as9[0] = (String)uri.getPathSegments().get(1);
            as2 = prependArgs(as2, as9);
            sqlitequerybuilder.setProjectionMap(PHOTOS_BY_ALBUM_VIEW_MAP);
            sqlitequerybuilder.setDistinct(true);
            s5 = s1;
            s3 = null;
        	break;
        case 138:
        	sqlitequerybuilder.setTables("photos_by_stream_view");
            sqlitequerybuilder.appendWhere("stream_id");
            sqlitequerybuilder.appendWhere("=?");
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("owner_id");
            sqlitequerybuilder.appendWhere("=?");
            sqlitequerybuilder.setProjectionMap(PHOTOS_BY_STREAM_VIEW_MAP);
            sqlitequerybuilder.setDistinct(true);
            s5 = s1;
            String as6[] = new String[2];
            as6[0] = (String)uri.getPathSegments().get(1);
            as6[1] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as6);
            s3 = null;
        	break;
        case 139:
        	sqlitequerybuilder.setTables("photos_by_user_view");
            sqlitequerybuilder.appendWhere("photo_of_user_id");
            sqlitequerybuilder.appendWhere("=?");
            String as7[] = new String[1];
            as7[0] = (String)uri.getPathSegments().get(1);
            as2 = prependArgs(as2, as7);
            sqlitequerybuilder.setProjectionMap(PHOTOS_BY_USER_VIEW_MAP);
            sqlitequerybuilder.setDistinct(true);
            s5 = s1;
            s3 = null;
        	break;
        case 140:
        	sqlitequerybuilder.setTables("photo_home_view");
            sqlitequerybuilder.appendWhere("type");
            sqlitequerybuilder.appendWhere("='");
            sqlitequerybuilder.appendWhere("photos_of_me");
            sqlitequerybuilder.appendWhere("'");
            sqlitequerybuilder.setProjectionMap(PHOTO_NOTIFICATION_MAP);
            sqlitequerybuilder.setDistinct(true);
            s5 = s1;
            s3 = null;
        	break;
        case 141:
        	sqlitequerybuilder.setTables("photo_comment JOIN contacts ON photo_comment.author_id=contacts.gaia_id");
            sqlitequerybuilder.appendWhere("photo_id");
            sqlitequerybuilder.appendWhere("=?");
            String as5[] = new String[1];
            as5[0] = (String)uri.getPathSegments().get(1);
            as2 = prependArgs(as2, as5);
            sqlitequerybuilder.setProjectionMap(PHOTO_COMMENTS_MAP);
            sqlitequerybuilder.setDistinct(true);
            s5 = s1;
            s3 = null;
        	break;
        case 143:
        	sqlitequerybuilder.setTables("photo_shape_view");
            sqlitequerybuilder.appendWhere("photo_id");
            sqlitequerybuilder.appendWhere("=?");
            String as4[] = new String[1];
            as4[0] = (String)uri.getPathSegments().get(1);
            as2 = prependArgs(as2, as4);
            sqlitequerybuilder.setProjectionMap(PHOTO_SHAPE_VIEW_MAP);
            sqlitequerybuilder.setDistinct(true);
            s5 = s1;
            s3 = null;
        	break;
        case 144:
        	sqlitequerybuilder.setTables("album_view");
            sqlitequerybuilder.appendWhere("album_id");
            sqlitequerybuilder.appendWhere("=?");
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("owner_id");
            sqlitequerybuilder.appendWhere("=?");
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("title");
            sqlitequerybuilder.appendWhere(" IS NOT NULL");
            sqlitequerybuilder.appendWhere(" AND ");
            sqlitequerybuilder.appendWhere("is_activity");
            sqlitequerybuilder.appendWhere(" = 0");
            String as11[] = new String[2];
            as11[0] = (String)uri.getPathSegments().get(1);
            as11[1] = (String)uri.getPathSegments().get(2);
            as2 = prependArgs(as2, as11);
            sqlitequerybuilder.setProjectionMap(ALBUM_VIEW_MAP);
            s5 = s1;
            s3 = null;
        	break;
        case 145:
        	sqlitequerybuilder.setTables("photos_by_event_view");
            sqlitequerybuilder.appendWhere("event_id");
            sqlitequerybuilder.appendWhere("=?");
            String as8[] = new String[1];
            as8[0] = (String)uri.getPathSegments().get(1);
            as2 = prependArgs(as2, as8);
            sqlitequerybuilder.setProjectionMap(PHOTOS_BY_EVENT_VIEW_MAP);
            sqlitequerybuilder.setDistinct(true);
            s5 = s1;
            s3 = null;
        	break;
        case 160:
        	sqlitequerybuilder.setTables("message_notifications_view");
            sqlitequerybuilder.setProjectionMap(MESSAGE_NOTIFICATIONS_PROJECTION_MAP);
            s5 = s1;
            s3 = null;
        	break;
        case 180:
        	sqlitequerybuilder.setTables("network_data_transactions");
            sqlitequerybuilder.setProjectionMap(NETWORK_DATA_TRANSACTIONS_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        case 181:
        	sqlitequerybuilder.setTables("network_data_stats");
            sqlitequerybuilder.setProjectionMap(NETWORK_DATA_STATS_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        case 182:
        	sqlitequerybuilder.setTables("platform_audience");
            sqlitequerybuilder.appendWhere("package_name");
            sqlitequerybuilder.appendWhere("=");
            sqlitequerybuilder.appendWhereEscapeString(uri.getLastPathSegment());
            sqlitequerybuilder.setProjectionMap(PLATFORM_AUDIENCE_PROJECTION_MAP);
            sqlitequerybuilder.setDistinct(true);
            s3 = null;
            s5 = null;
        	break;
        case 190:
        	sqlitequerybuilder.setTables("plus_pages");
            sqlitequerybuilder.setProjectionMap(PLUS_PAGES_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        case 210:
        	String s6;
            if(isInProjection(as, new String[] {"inviter_name", "inviter_photo_url"}))
                s6 = "squares LEFT OUTER JOIN contacts ON (squares.inviter_gaia_id=contacts.gaia_id)";
            else
                s6 = "squares";
            sqlitequerybuilder.setTables(s6);
            sqlitequerybuilder.setProjectionMap(SQUARES_PROJECTION_MAP);
            if(!TextUtils.isEmpty(s1))
            {
                s5 = s1;
                s3 = null;
            } else
            {
                s5 = "sort_index";
                s3 = null;
            }
        	break;
        case 211:
        	sqlitequerybuilder.appendWhere("square_id");
            sqlitequerybuilder.appendWhere("=?");
            String as3[] = new String[1];
            as3[0] = (String)uri.getPathSegments().get(1);
            as2 = prependArgs(as2, as3);
            if(isInProjection(as, new String[] {"inviter_name", "inviter_photo_url"}))
                s6 = "squares LEFT OUTER JOIN contacts ON (squares.inviter_gaia_id=contacts.gaia_id)";
            else
                s6 = "squares";
            sqlitequerybuilder.setTables(s6);
            sqlitequerybuilder.setProjectionMap(SQUARES_PROJECTION_MAP);
            if(!TextUtils.isEmpty(s1))
            {
                s5 = s1;
                s3 = null;
            } else
            {
                s5 = "sort_index";
                s3 = null;
            }
        	break;
        case 212:
        	sqlitequerybuilder.setTables("emotishare_data");
            sqlitequerybuilder.setProjectionMap(EMOTISHARE_PROJECTION_MAP);
            s3 = null;
            s5 = null;
        	break;
        default:
        	throw new IllegalArgumentException((new StringBuilder("Unknown URI ")).append(uri).toString());
        }
        
        if(!TextUtils.isEmpty(s1))
            s5 = s1;
        if(EsLog.isLoggable("EsProvider", 3))
        {
            StringBuilder stringbuilder = new StringBuilder("QUERY: ");
            Log.d("EsProvider", stringbuilder.append(sqlitequerybuilder.buildQuery(as, s, as2, s3, null, s1, s4)).toString());
        }
        Cursor cursor = sqlitequerybuilder.query(EsDatabaseHelper.getDatabaseHelper(getContext(), i).getReadableDatabase(), as, s, as2, s3, null, s5, s4);
        if(EsLog.isLoggable("EsProvider", 3))
            Log.d("EsProvider", (new StringBuilder("QUERY results: ")).append(cursor.getCount()).toString());
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

	@Override
	public int update(Uri uri, ContentValues contentvalues, String s, String as[])
    {
        throw new IllegalArgumentException((new StringBuilder("Update not supported: ")).append(uri).toString());
    }
	
	public static void localeChanged(Context context)
    {
        EsAccount esaccount = EsService.getActiveAccount(context);
        if(null == esaccount) {
        	return;
        }
        
        EsDatabaseHelper esdatabasehelper = EsDatabaseHelper.getDatabaseHelper(context, esaccount);
        SQLiteDatabase sqlitedatabase = esdatabasehelper.getWritableDatabase();
        sqlitedatabase.setLocale(Locale.getDefault());
        try {
        	sqlitedatabase.beginTransaction();
        	esdatabasehelper.rebuildTables(sqlitedatabase, esaccount);
        	sqlitedatabase.setTransactionSuccessful();
        	ContentResolver.requestSync(AccountsUtil.newAccount(esaccount.getName()), EsProvider.class.getName(), new Bundle());
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }
	
	protected static String[] getTableSQLs()
    {
        return (new String[] {
            "CREATE TABLE account_status (user_id TEXT,last_sync_time INT DEFAULT(-1),last_stats_sync_time INT DEFAULT(-1),last_contacted_time INT DEFAULT(-1),wipeout_stats INT DEFAULT(0),circle_sync_time INT DEFAULT(-1),people_sync_time INT DEFAULT(-1),people_last_update_token TEXT,suggested_people_sync_time INT DEFAULT(-1),blocked_people_sync_time INT DEFAULT(-1),event_list_sync_time INT DEFAULT(-1),event_themes_sync_time INT DEFAULT(-1),avatars_downloaded INT DEFAULT(0),audience_data BLOB,audience_history BLOB,contacts_sync_version INT DEFAULT(0),push_notifications INT DEFAULT(0),last_analytics_sync_time INT DEFAULT(-1),last_settings_sync_time INT DEFAULT(-1),last_squares_sync_time INT DEFAULT(-1),last_emotishare_sync_time INT DEFAULT(-1));", "INSERT INTO account_status DEFAULT VALUES;", "CREATE TABLE activity_streams (stream_key TEXT NOT NULL,activity_id TEXT NOT NULL,sort_index INT NOT NULL,last_activity INT,token TEXT,PRIMARY KEY (stream_key,activity_id));", "CREATE TABLE activities (_id INTEGER PRIMARY KEY, activity_id TEXT UNIQUE NOT NULL, data_state INT NOT NULL DEFAULT (0), author_id TEXT NOT NULL, source_id TEXT, source_name TEXT, total_comment_count INT NOT NULL, plus_one_data BLOB, public INT NOT NULL, spam INT NOT NULL, acl_display TEXT, can_comment INT NOT NULL, can_reshare INT NOT NULL, has_muted INT NOT NULL, has_read INT NOT NULL, loc BLOB, created INT NOT NULL, is_edited INT NOT NULL DEFAULT(0), modified INT NOT NULL, popular_post INT NOT NULL DEFAULT(0), content_flags INT NOT NULL DEFAULT(0), annotation TEXT, annotation_plaintext TEXT, title TEXT, title_plaintext TEXT, original_author_id TEXT, original_author_name TEXT, event_id TEXT, photo_collection BLOB, embed_deep_link BLOB, album_id TEXT, embed_media BLOB, embed_photo_album BLOB, embed_checkin BLOB, embed_place BLOB, embed_place_review BLOB, embed_skyjam BLOB, embed_appinvite BLOB, embed_hangout BLOB, embed_square BLOB, embed_emotishare BLOB);", "CREATE TABLE media (_id INTEGER PRIMARY KEY,activity_id TEXT NOT NULL,thumbnail_url TEXT NOT NULL,FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE);", "CREATE TABLE activity_comments (_id INTEGER PRIMARY KEY,activity_id TEXT NOT NULL,comment_id TEXT UNIQUE NOT NULL,author_id TEXT NOT NULL,content TEXT,created INT NOT NULL,plus_one_data BLOB,FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE);", "CREATE TABLE locations (_id INTEGER PRIMARY KEY,qrid INT NOT NULL,name TEXT,location BLOB,FOREIGN KEY (qrid) REFERENCES location_queries(_id) ON DELETE CASCADE);", "CREATE TABLE location_queries (_id INTEGER PRIMARY KEY,key TEXT UNIQUE NOT NULL);", "CREATE TABLE notifications (_id INTEGER, notif_id TEXT UNIQUE NOT NULL, coalescing_code TEXT PRIMARY KEY, category INT NOT NULL DEFAULT(0), message TEXT, enabled INT, read INT NOT NULL, seen INT NOT NULL, timestamp INT NOT NULL, circle_data BLOB, pd_gaia_id TEXT, pd_album_id TEXT, pd_photo_id INT, activity_id TEXT, ed_event INT DEFAULT(0),ed_event_id TEXT, ed_creator_id TEXT, notification_type INT NOT NULL DEFAULT(0),entity_type INT NOT NULL DEFAULT(0),entity_snippet TEXT,entity_photos_data BLOB,entity_squares_data BLOB,square_id TEXT,square_name TEXT,square_photo_url TEXT,taggee_photo_ids TEXT,taggee_data_actors BLOB);", "CREATE TABLE contacts (person_id TEXT PRIMARY KEY,gaia_id TEXT,avatar TEXT,name TEXT,sort_key TEXT,last_updated_time INT,profile_type INT DEFAULT(0),profile_state INT DEFAULT(0),in_my_circles INT DEFAULT(0),blocked INT DEFAULT(0) );", 
            "CREATE TABLE circles (circle_id TEXT PRIMARY KEY,circle_name TEXT,sort_key TEXT,type INT, contact_count INT,semantic_hints INT NOT NULL DEFAULT(0),show_order INT,volume INT);", "CREATE TABLE circle_contact (link_circle_id TEXT NOT NULL,link_person_id TEXT NOT NULL,UNIQUE (link_circle_id, link_person_id), FOREIGN KEY (link_circle_id) REFERENCES circles(circle_id) ON DELETE CASCADE,FOREIGN KEY (link_person_id) REFERENCES contacts(person_id) ON DELETE CASCADE);", "CREATE TABLE suggested_people (_id INTEGER PRIMARY KEY, suggested_person_id TEXT NOT NULL,dismissed INT DEFAULT(0),sort_order INT DEFAULT(0),category TEXT NOT NULL,category_label TEXT,category_sort_key TEXT,explanation TEXT,properties TEXT,suggestion_id TEXT );", "CREATE TABLE circle_action (gaia_id TEXT NOT NULL,notification_id INT NOT NULL,UNIQUE (gaia_id, notification_id), FOREIGN KEY (notification_id) REFERENCES notifications(notif_id) ON DELETE CASCADE);", "CREATE TABLE photo_home (_id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT NOT NULL,photo_count INT,sort_order INT NOT NULL DEFAULT( 100 ),timestamp INT,notification_count INT);", "CREATE TABLE photo_home_cover (photo_home_key INT NOT NULL,photo_id INT,url TEXT NOT NULL,width INT,height INT,size INT,image BLOB, FOREIGN KEY (photo_home_key) REFERENCES photo_home(_id) ON DELETE CASCADE);", "CREATE TABLE profiles (profile_person_id TEXT PRIMARY KEY,contact_update_time INT,contact_proto BLOB,profile_update_time INT,profile_proto BLOB,FOREIGN KEY (profile_person_id) REFERENCES contacts(person_id) ON DELETE CASCADE);", "CREATE TABLE album ( _id INTEGER PRIMARY KEY AUTOINCREMENT, album_id TEXT UNIQUE NOT NULL, title TEXT, photo_count INT, sort_order INT NOT NULL DEFAULT( 100 ), owner_id TEXT, timestamp INT, entity_version INT, album_type TEXT NOT NULL DEFAULT('ALL_OTHERS'), cover_photo_id INT, stream_id TEXT, is_activity BOOLEAN DEFAULT '0' );", "CREATE TABLE album_cover (album_key INT NOT NULL,photo_id INT,url TEXT,width INT,height INT,size INT, FOREIGN KEY (album_key) REFERENCES album(_id) ON DELETE CASCADE);", "CREATE TABLE photo (_id INTEGER PRIMARY KEY AUTOINCREMENT, photo_id INT NOT NULL, url TEXT, title TEXT, description TEXT, action_state INT, comment_count INT, owner_id TEXT, plus_one_key INT NOT NULL, width INT, height INT, album_id TEXT NOT NULL, timestamp INT, entity_version INT, fingerprint BLOB, video_data BLOB, is_panorama INT DEFAULT(0), upload_status TEXT, downloadable BOOLEAN, UNIQUE (photo_id) FOREIGN KEY (album_id) REFERENCES album(album_id) ON DELETE CASCADE);", 
            "CREATE TABLE photo_comment (_id INTEGER PRIMARY KEY, photo_id INT NOT NULL, comment_id TEXT UNIQUE NOT NULL, author_id TEXT NOT NULL, content TEXT, create_time INT, truncated INT, update_time INT, plusone_data BLOB, FOREIGN KEY (photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE);", "CREATE TABLE photo_plusone (_id INTEGER PRIMARY KEY, photo_id INT NOT NULL, plusone_id TEXT, plusone_by_me BOOLEAN DEFAULT '0' NOT NULL, plusone_count INTEGER, plusone_data BLOB, FOREIGN KEY (photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE );", "CREATE TABLE photos_in_album (_id INTEGER PRIMARY KEY, photo_id INT NOT NULL, album_id INT NOT NULL, FOREIGN KEY (photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE);", "CREATE TABLE photos_of_user (photo_id INT NOT NULL, photo_of_user_id TEXT NOT NULL, FOREIGN KEY (photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE);", "CREATE TABLE photos_in_event (_id INTEGER PRIMARY KEY, photo_id INT NOT NULL, event_id TEXT NOT NULL, UNIQUE (photo_id, event_id) FOREIGN KEY (photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE);", "CREATE TABLE photos_in_stream (_id INTEGER PRIMARY KEY, photo_id INT NOT NULL, stream_id TEXT NOT NULL, FOREIGN KEY (photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE);", "CREATE TABLE photo_shape (shape_id INTEGER PRIMARY KEY, photo_id INT NOT NULL, subject_id TEXT, creator_id TEXT NOT NULL, status TEXT, bounds BLOB, FOREIGN KEY (photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE);", "CREATE TABLE conversations (_id INTEGER PRIMARY KEY, conversation_id TEXT, is_muted INT, is_visible INT, latest_event_timestamp INT, latest_message_timestamp INT, earliest_event_timestamp INT, has_older_events INT, unread_count INT, name TEXT, generated_name TEXT, latest_message_text TEXT, latest_message_image_url TEXT, latest_message_author_id TEXT, latest_message_type INT, is_group INT, is_pending_accept INT, inviter_id TEXT, fatal_error_type INT, is_pending_leave INT, is_awaiting_event_stream INT, is_awaiting_older_events INT, first_event_timestamp INT, packed_participants TEXT, UNIQUE (conversation_id ));", "CREATE TABLE conversation_participants (conversation_id INT, participant_id TEXT, active INT, sequence INT, UNIQUE (conversation_id,participant_id) ON CONFLICT REPLACE, FOREIGN KEY (conversation_id) REFERENCES conversations(_id) ON DELETE CASCADE, FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE);", "CREATE TABLE participants (_id INTEGER PRIMARY KEY, participant_id TEXT UNIQUE ON CONFLICT REPLACE, full_name TEXT, first_name TEXT,type INT);", 
            "CREATE TABLE messages (_id INTEGER PRIMARY KEY, message_id TEXT, conversation_id INT, author_id TEXT, text TEXT, timestamp INT, status INT, type INT, notification_seen INT, image_url TEXT, FOREIGN KEY (conversation_id) REFERENCES conversations(_id) ON DELETE CASCADE,FOREIGN KEY (author_id) REFERENCES participants(participant_id) ON DELETE CASCADE, UNIQUE (conversation_id,timestamp) ON CONFLICT REPLACE);", "CREATE TABLE messenger_suggestions (_id INTEGER PRIMARY KEY, participant_id TEXT UNIQUE ON CONFLICT REPLACE, full_name TEXT, first_name TEXT,sequence INT);", "CREATE TABLE hangout_suggestions (_id INTEGER PRIMARY KEY, participant_id TEXT UNIQUE ON CONFLICT REPLACE, full_name TEXT, first_name TEXT,sequence INT);", "CREATE TABLE realtimechat_metadata (key TEXT UNIQUE, value TEXT)", "CREATE TABLE analytics_events (event_data BLOB NOT NULL)", "CREATE TABLE search (search_key TEXT NOT NULL,continuation_token TEXT,PRIMARY KEY (search_key));", "CREATE TABLE contact_search(search_person_id TEXT NOT NULL,search_key_type TEXT NOT NULL DEFAULT(0),search_key TEXT,FOREIGN KEY (search_person_id) REFERENCES contacts(person_id) ON DELETE CASCADE);", "CREATE TABLE network_data_transactions(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,time INT,sent INT,recv INT,network_duration INT,process_duration INT,req_count INT,exception TEXT);", "CREATE TABLE network_data_stats(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,first INT,last INT,sent INT,recv INT,network_duration INT,process_duration INT,req_count INT);", "CREATE TABLE platform_audience(package_name TEXT PRIMARY KEY, audience_data BLOB);", 
            "CREATE TABLE events(_id INTEGER PRIMARY KEY AUTOINCREMENT, event_id TEXT UNIQUE NOT NULL, activity_id TEXT UNIQUE, name TEXT, source INT, update_timestamp INT, refresh_timestamp INT, activity_refresh_timestamp INT, invitee_roster_timestamp INT, fingerprint INT NOT NULL DEFAULT(0), start_time INT NOT NULL, end_time INT NOT NULL, can_invite_people INT DEFAULT (0), can_post_photos INT DEFAULT (0), can_comment INT DEFAULT(0) NOT NULL, mine INT DEFAULT (0) NOT NULL, polling_token TEXT,resume_token TEXT,display_time INT DEFAULT (0),event_data BLOB, invitee_roster BLOB);", "CREATE TABLE event_people(_id INTEGER PRIMARY KEY AUTOINCREMENT, event_id TEXT NOT NULL, gaia_id TEXT NOT NULL, CONSTRAINT uc_eventID UNIQUE (event_id, gaia_id) FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE);", "CREATE TABLE plus_pages(gaia_id TEXT PRIMARY KEY, name TEXT);", "CREATE TABLE event_activities(_id INTEGER PRIMARY KEY AUTOINCREMENT, event_id TEXT NOT NULL, type INT, owner_gaia_id TEXT, owner_name TEXT, timestamp INT, fingerprint INT NOT NULL DEFAULT(0), url TEXT,comment TEXT,data BLOB, FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE);", "CREATE TABLE event_themes(_id INTEGER PRIMARY KEY AUTOINCREMENT, theme_id INTEGER UNIQUE NOT NULL, is_default INT DEFAULT(0), is_featured INT DEFAULT(0), image_url TEXT NOT NULL, placeholder_path TEXT, sort_order INT DEFAULT(0));", "CREATE TABLE deep_link_installs(_id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp INT DEFAULT(0), package_name TEXT UNIQUE NOT NULL, launch_source TEXT NOT NULL, activity_id TEXT NOT NULL, author_id TEXT NOT NULL);", "CREATE TABLE squares (_id INTEGER PRIMARY KEY,square_id TEXT UNIQUE NOT NULL,square_name TEXT,tagline TEXT,photo_url TEXT,about_text TEXT,joinability INT NOT NULL DEFAULT(0),member_count INT NOT NULL DEFAULT(0),membership_status INT NOT NULL DEFAULT(0),is_member INT NOT NULL DEFAULT(0),suggested INT NOT NULL DEFAULT(0),post_visibility INT NOT NULL DEFAULT(-1),can_see_members INT NOT NULL DEFAULT(0),can_see_posts INT NOT NULL DEFAULT(0),can_join INT NOT NULL DEFAULT(0),can_request_to_join INT NOT NULL DEFAULT(0),can_share INT NOT NULL DEFAULT(0),can_invite INT NOT NULL DEFAULT(0),notifications_enabled INT NOT NULL DEFAULT(0),square_streams BLOB,inviter_gaia_id TEXT,sort_index INT NOT NULL DEFAULT(0),last_sync INT DEFAULT(-1),last_members_sync INT DEFAULT(-1),invitation_dismissed INT NOT NULL DEFAULT(0),suggestion_sort_index INT NOT NULL DEFAULT(0),auto_subscribe INT NOT NULL DEFAULT(0),disable_subscription INT NOT NULL DEFAULT(0),unread_count INT NOT NULL DEFAULT(0));", "CREATE TABLE square_contact (link_square_id TEXT NOT NULL,link_person_id TEXT NOT NULL,membership_status INT NOT NULL DEFAULT(0),UNIQUE (link_square_id, link_person_id), FOREIGN KEY (link_square_id) REFERENCES squares(square_id) ON DELETE CASCADE,FOREIGN KEY (link_person_id) REFERENCES contacts(person_id) ON DELETE CASCADE);", "CREATE TABLE emotishare_data (_id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT UNIQUE ON CONFLICT REPLACE,data BLOB,generation INT DEFAULT(-1));"
        });
    }

    static String[] getViewNames()
    {
        return (new String[] {
            "activities_stream_view", "activity_view", "comments_view", "location_queries_view", "conversations_view", "participants_view", "messages_view", "photo_home_view", "album_view", "photo_view", 
            "photos_by_album_view", "photos_by_event_view", "photos_by_stream_view", "photos_by_user_view", "photo_shape_view", "message_notifications_view", "deep_link_installs_view", "event_people_view"
        });
    }

    static String[] getViewSQLs()
    {
        String as[] = new String[18];
        as[0] = "CREATE VIEW activities_stream_view AS SELECT activity_streams.stream_key as stream_key,activity_streams.sort_index as sort_index,activity_streams.last_activity as last_activity,activity_streams.token as token,activities._id as _id,activities.activity_id as activity_id,activities.author_id as author_id,activities.source_id as source_id,activities.source_name as source_name,activities.total_comment_count as total_comment_count,activities.plus_one_data as plus_one_data,activities.public as public,activities.spam as spam,activities.acl_display as acl_display,activities.can_comment as can_comment,activities.can_reshare as can_reshare,activities.has_muted as has_muted,activities.has_read as has_read,activities.loc as loc,activities.created as created,activities.is_edited as is_edited,activities.modified as modified,activities.data_state as data_state,activities.event_id as event_id,activities.photo_collection as photo_collection,activities.popular_post as popular_post,activities.content_flags as content_flags,activities.annotation as annotation,activities.annotation_plaintext as annotation_plaintext,activities.title as title,activities.title_plaintext as title_plaintext,activities.original_author_id as original_author_id,activities.original_author_name as original_author_name,activities.embed_deep_link as embed_deep_link,activities.embed_media as embed_media,activities.embed_photo_album as embed_photo_album,activities.embed_checkin as embed_checkin,activities.embed_place as embed_place,activities.embed_place_review as embed_place_review,activities.embed_skyjam as embed_skyjam,activities.embed_appinvite as embed_appinvite,activities.embed_hangout as embed_hangout,activities.embed_square as embed_square,activities.embed_emotishare as embed_emotishare,events.event_data as event_data,contacts.name as name,contacts.avatar as avatar FROM activity_streams INNER JOIN activities ON activity_streams.activity_id=activities.activity_id INNER JOIN contacts ON activities.author_id=contacts.gaia_id LEFT OUTER JOIN events ON activities.event_id=events.event_id WHERE data_state    IN (1, 0)";
        as[1] = "CREATE VIEW activity_view AS SELECT activities._id as _id,activities.activity_id as activity_id,activities.author_id as author_id,activities.source_id as source_id,activities.source_name as source_name,activities.total_comment_count as total_comment_count,activities.plus_one_data as plus_one_data,activities.public as public,activities.spam as spam,activities.acl_display as acl_display,activities.can_comment as can_comment,activities.can_reshare as can_reshare,activities.has_muted as has_muted,activities.has_read as has_read,activities.loc as loc,activities.created as created,activities.is_edited as is_edited,activities.modified as modified,activities.data_state as data_state,contacts.name as name,contacts.avatar as avatar,activities.photo_collection as photo_collection,activities.popular_post as popular_post,activities.content_flags as content_flags,activities.annotation as annotation,activities.annotation_plaintext as annotation_plaintext,activities.title as title,activities.title_plaintext as title_plaintext,activities.original_author_id as original_author_id,activities.original_author_name as original_author_name,activities.embed_deep_link as embed_deep_link,activities.embed_media as embed_media,activities.embed_photo_album as embed_photo_album,activities.embed_checkin as embed_checkin,activities.embed_place as embed_place,activities.embed_place_review as embed_place_review,activities.embed_skyjam as embed_skyjam,activities.embed_appinvite as embed_appinvite,activities.embed_hangout as embed_hangout,activities.embed_square as embed_square,activities.embed_emotishare as embed_emotishare,events.event_data as event_data FROM activities JOIN contacts ON activities.author_id=contacts.gaia_id LEFT OUTER JOIN events ON activities.event_id=events.event_id";
        as[2] = "CREATE VIEW comments_view AS SELECT activity_comments._id as _id,activity_comments.activity_id as activity_id,activity_comments.comment_id as comment_id,activity_comments.author_id as author_id,activity_comments.content as content,activity_comments.created as created,activity_comments.plus_one_data as plus_one_data,contacts.name as name,contacts.avatar as avatar FROM activity_comments JOIN contacts ON activity_comments.author_id=contacts.gaia_id";
        as[3] = "CREATE VIEW location_queries_view AS SELECT location_queries.key as key,locations._id as _id,locations.name as name,locations.location as location FROM location_queries LEFT JOIN locations ON location_queries._id=locations.qrid";
        as[4] = "CREATE VIEW conversations_view AS SELECT conversations._id as _id, conversations.conversation_id as conversation_id, conversations.is_muted as is_muted, conversations.is_visible as is_visible, conversations.latest_event_timestamp as latest_event_timestamp, conversations.latest_message_timestamp as latest_message_timestamp, conversations.earliest_event_timestamp as earliest_event_timestamp, conversations.has_older_events as has_older_events, conversations.unread_count as unread_count, conversations.name as name, conversations.generated_name as generated_name, conversations.latest_message_text as latest_message_text, conversations.latest_message_image_url as latest_message_image_url, conversations.latest_message_author_id as latest_message_author_id, conversations.latest_message_type as latest_message_type, conversations.is_group as is_group, conversations.is_pending_accept as is_pending_accept, conversations.inviter_id as inviter_id, conversations.fatal_error_type as fatal_error_type, conversations.is_pending_leave as is_pending_leave, conversations.is_awaiting_event_stream as is_awaiting_event_stream, conversations.is_awaiting_older_events as is_awaiting_older_events, conversations.first_event_timestamp as first_event_timestamp, conversations.packed_participants as packed_participants, participants.full_name as latest_message_author_full_name, participants.first_name as latest_message_author_first_name, participants.type as latest_message_author_type, inviter_alias.full_name as inviter_full_name, inviter_alias.first_name as inviter_first_name, inviter_alias.type as inviter_type  FROM conversations LEFT JOIN participants ON conversations.latest_message_author_id=participants.participant_id LEFT JOIN participants inviter_alias ON conversations.inviter_id=inviter_alias.participant_id";
        as[5] = "CREATE VIEW participants_view AS SELECT participants._id as _id, participants.participant_id as participant_id, participants.full_name as full_name, participants.first_name as first_name, participants.type as type, conversation_participants.conversation_id as conversation_id, conversation_participants.active as active, conversation_participants.sequence as sequence FROM participants JOIN conversation_participants ON participants.participant_id=conversation_participants.participant_id";
        as[6] = "CREATE VIEW messages_view AS SELECT messages._id as _id, messages.message_id as message_id, messages.conversation_id as conversation_id, messages.author_id as author_id, messages.text as text, messages.timestamp as timestamp, messages.status as status, messages.type as type, messages.image_url as image_url, participants.full_name as author_full_name, participants.first_name as author_first_name, participants.type as author_type FROM messages LEFT JOIN participants ON messages.author_id=participants.participant_id";
        as[7] = "CREATE VIEW photo_home_view AS SELECT photo_home._id as _id, photo_home.photo_count as photo_count, photo_home.notification_count as notification_count, photo_home.sort_order as sort_order, photo_home.timestamp as timestamp, photo_home.type as type, photo_home_cover.height as height, photo_home_cover.image as image, photo_home_cover.photo_id as photo_id, photo_home_cover.photo_home_key as photo_home_key, photo_home_cover.size as size, photo_home_cover.url as url, photo_home_cover.width as width FROM photo_home LEFT JOIN photo_home_cover ON photo_home._id=photo_home_cover.photo_home_key";
        as[8] = "CREATE VIEW album_view AS SELECT album._id as _id, album.album_id as album_id, album.entity_version as entity_version, album.is_activity as is_activity, album.owner_id as owner_id, album.photo_count as photo_count, album.sort_order as sort_order, album.stream_id as stream_id, album.timestamp as timestamp, album.title as title, album.cover_photo_id as cover_photo_id, album.album_type as album_type, album_cover.album_key as album_key, album_cover.height as height, album_cover.photo_id as photo_id, album_cover.size as size, album_cover.url as url, album_cover.width as width FROM album LEFT JOIN album_cover ON album._id=album_cover.album_key";
        as[9] = PHOTO_VIEW_SQL;
        as[10] = PHOTOS_BY_ALBUM_VIEW_SQL;
        as[11] = PHOTOS_BY_EVENT_VIEW_SQL;
        as[12] = PHOTOS_BY_STREAM_VIEW_SQL;
        as[13] = PHOTOS_BY_USER_VIEW_SQL;
        as[14] = "CREATE VIEW photo_shape_view AS SELECT photo_shape.photo_id as photo_id, photo_shape.bounds as bounds, photo_shape.creator_id as creator_id, photo_shape.shape_id as shape_id, photo_shape.status as status, photo_shape.subject_id as subject_id, (SELECT a.name FROM contacts as a WHERE a.gaia_id=photo_shape.creator_id ) AS creator_name, (SELECT b.name FROM contacts as b WHERE b.gaia_id=photo_shape.subject_id ) AS subject_name FROM photo_shape";
        as[15] = "CREATE VIEW message_notifications_view AS SELECT messages._id as _id, messages.message_id as message_id, messages.conversation_id as conversation_id, messages.author_id as author_id, messages.text as text, messages.image_url as image_url, messages.timestamp as timestamp, messages.status as status, messages.type as type, messages.notification_seen as notification_seen, author_alias.full_name as author_full_name, author_alias.first_name as author_first_name, author_alias.type as author_type, conversations.is_muted as conversation_muted, conversations.is_visible as conversation_visible, conversations.is_group as conversation_group, conversations.is_pending_accept as conversation_pending_accept, conversations.is_pending_leave as conversation_pending_leave, conversations.name as conversation_name, conversations.generated_name as generated_name, inviter_alias.participant_id as inviter_id, inviter_alias.full_name as inviter_full_name, inviter_alias.first_name as inviter_first_name, inviter_alias.type as inviter_type FROM messages LEFT JOIN participants author_alias ON messages.author_id=author_alias.participant_id LEFT JOIN conversations ON messages.conversation_id=conversations._id LEFT JOIN participants inviter_alias ON conversations.inviter_id=inviter_alias.participant_id";
        as[16] = "CREATE VIEW deep_link_installs_view AS SELECT deep_link_installs._id as _id,deep_link_installs.timestamp as timestamp,deep_link_installs.package_name as package_name,deep_link_installs.launch_source as launch_source,contacts.name as name,activities.source_name as source_name,activities.embed_deep_link as embed_deep_link FROM deep_link_installs INNER JOIN activities ON deep_link_installs.activity_id=activities.activity_id INNER JOIN contacts ON deep_link_installs.author_id=contacts.gaia_id;";
        as[17] = "CREATE VIEW event_people_view AS SELECT event_people._id as _id,event_people.event_id as event_id,event_people.gaia_id as gaia_id,contacts.person_id as person_id,contacts.name as name,contacts.sort_key as sort_key,contacts.avatar as avatar,contacts.last_updated_time as last_updated_time,contacts.profile_type as profile_type,contacts.profile_state as profile_state,contacts.in_my_circles as in_my_circles,contacts.blocked as blocked FROM event_people INNER JOIN contacts ON event_people.gaia_id=contacts.gaia_id;";
        return as;
    }
    
    static String[] getIndexSQLs()
    {
        return (new String[] {
            "CREATE INDEX contacts_in_my_circles ON contacts(in_my_circles,person_id)", "CREATE INDEX contacts_name ON contacts(name)", "CREATE INDEX contacts_sort_key ON contacts(sort_key)", "CREATE INDEX contacts_gaia_id ON contacts(gaia_id)", "CREATE UNIQUE INDEX circle_contact_forward ON circle_contact(link_circle_id,link_person_id)", "CREATE UNIQUE INDEX circle_contact_backward ON circle_contact(link_person_id,link_circle_id)", "CREATE INDEX contact_search_key ON contact_search(search_key)", "CREATE INDEX album_album_id ON album(album_id)", "CREATE INDEX photo_photo_id ON photo(photo_id)", "CREATE INDEX photo_comment_photo_id ON photo_comment(photo_id,comment_id)", 
            "CREATE INDEX photo_shape_photo_id ON photo_shape(photo_id,shape_id)", "CREATE INDEX photos_in_stream_photo_id ON photos_in_stream(stream_id)", "CREATE INDEX photos_in_album_album_id ON photos_in_album(album_id)", "CREATE INDEX photos_in_event_event_id ON photos_in_event(event_id)", "CREATE INDEX photos_of_user_photo_id ON photo_comment(photo_id)", "CREATE INDEX activity_streams_activity_id ON activity_streams(activity_id)"
        });
    }

	private static void ensureActivitiesPageSizes(Context context)
    {
        if(sActivitiesPageSize == 0)
            if(ScreenMetrics.getInstance(context).screenDisplayType == 0)
            {
                sActivitiesPageSize = 15;
                sActivitiesFirstPageSize = 10;
            } else
            {
                sActivitiesPageSize = 24;
                sActivitiesFirstPageSize = 20;
            }
    }
	
	public static void insertVirtualCircles(Context context, SQLiteDatabase sqlitedatabase)
    {
        insertVirtualCircle(sqlitedatabase, "v.nearby", context.getString(R.string.stream_nearby), -1, 10000);
        insertVirtualCircle(sqlitedatabase, "v.all.circles", context.getString(R.string.stream_circles), -1, 0);
        insertVirtualCircle(sqlitedatabase, "v.whatshot", context.getString(R.string.stream_whats_hot), -1, 10);
    }
	
	private static void insertVirtualCircle(SQLiteDatabase sqlitedatabase, String s, String s1, int i, int j)
    {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("circle_id", s);
        contentvalues.put("circle_name", s1);
        contentvalues.put("type", Integer.valueOf(-1));
        contentvalues.put("contact_count", Integer.valueOf(0));
        contentvalues.put("semantic_hints", Integer.valueOf(11));
        contentvalues.put("show_order", Integer.valueOf(j));
        contentvalues.put("volume", Integer.valueOf(0));
        sqlitedatabase.insertWithOnConflict("circles", "circle_id", contentvalues, 4);
    }

	public static int getsActivitiesFirstPageSize(Context context)
    {
        ensureActivitiesPageSizes(context);
        return sActivitiesFirstPageSize;
    }
	
	public static int getActivitiesPageSize(Context context)
    {
        ensureActivitiesPageSizes(context);
        return sActivitiesPageSize;
    }
	
	public static void deleteDatabase(Context context, EsAccount esaccount)
    {
        EsDatabaseHelper.getDatabaseHelper(context, esaccount).deleteDatabase();
    }
}