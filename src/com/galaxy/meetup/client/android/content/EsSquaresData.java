/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.IOException;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.client.util.StringUtils;
import com.galaxy.meetup.server.client.domain.SquareMember;
import com.galaxy.meetup.server.client.domain.SquareNotificationOptions;
import com.galaxy.meetup.server.client.domain.SquareProfile;
import com.galaxy.meetup.server.client.domain.SquareStream;
import com.galaxy.meetup.server.client.domain.ViewerSquare;
import com.galaxy.meetup.server.client.domain.ViewerSquareCalculatedMembershipProperties;
import com.galaxy.meetup.server.client.domain.ViewerSquareSquareActivityStats;
import com.galaxy.meetup.server.client.domain.ViewerSquareStreamList;

/**
 * 
 * @author sihai
 *
 */
public class EsSquaresData {

	public static final String SQUARES_PROJECTION[] = {
        "square_id", "square_name", "tagline", "photo_url", "about_text", "joinability", "member_count", "membership_status", "is_member", "suggested", 
        "post_visibility", "can_see_members", "can_see_posts", "can_join", "can_request_to_join", "can_share", "can_invite", "notifications_enabled", "square_streams", "sort_index", 
        "inviter_gaia_id", "last_sync", "last_members_sync", "auto_subscribe", "disable_subscription", "unread_count"
    };
    private static final String SQUARE_MEMBERS_PROJECTION[] = {
        "link_person_id", "membership_status"
    };
    private static final String UPDATE_SQUARE_MEMBERSHIP_PROJECTION[] = {
        "post_visibility", "joinability", "square_streams"
    };
    
    
    static void cleanupData(SQLiteDatabase sqlitedatabase)
    {
        sqlitedatabase.delete("squares", "is_member=0 AND membership_status NOT IN (4,5)", null);
    }

    private static void deleteSquareStreams(Context context, EsAccount esaccount, String s, DbSquareStream adbsquarestream[])
    {
        if(adbsquarestream != null)
        {
            int i = 0;
            for(int j = adbsquarestream.length; i < j; i++)
                EsPostsData.deleteActivityStream(context, esaccount, EsPostsData.buildSquareStreamKey(s, adbsquarestream[i].getStreamId(), false));

        }
        EsPostsData.deleteActivityStream(context, esaccount, EsPostsData.buildSquareStreamKey(s, null, false));
    }

    public static void dismissSquareInvitation(Context context, EsAccount esaccount, String s)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("invitation_dismissed", Integer.valueOf(1));
        if(sqlitedatabase.update("squares", contentvalues, "square_id=?", new String[] {s}) > 0)
            context.getContentResolver().notifyChange(EsProvider.SQUARES_URI, null);
    }

    public static Cursor getInvitedSquares(Context context, EsAccount esaccount, String as[], String s)
    {
        android.net.Uri uri = EsProvider.appendAccountParameter(EsProvider.SQUARES_URI, esaccount);
        ContentResolver contentresolver = context.getContentResolver();
        String as1[] = new String[1];
        as1[0] = String.valueOf(5);
        return contentresolver.query(uri, as, "membership_status=? AND is_member=0 AND invitation_dismissed=0", as1, null);
    }

    private static int getJoinability(String s)
    {
        int i;
        if("ANYONE".equals(s))
            i = 0;
        else
        if("REQUIRES_APPROVAL".equals(s))
            i = 1;
        else
        if("REQUIRES_INVITE".equals(s))
            i = 2;
        else
            i = -1;
        return i;
    }

    public static Cursor getJoinedSquares(Context context, EsAccount esaccount, String as[], String s)
    {
        android.net.Uri uri = EsProvider.appendAccountParameter(EsProvider.SQUARES_URI, esaccount);
        return context.getContentResolver().query(uri, as, "is_member!=0", null, s);
    }

    private static int getMembershipStatus(String s)
    {
        int i;
        if("NONE".equals(s))
            i = 0;
        else
        if("OWNER".equals(s))
            i = 1;
        else
        if("MODERATOR".equals(s))
            i = 2;
        else
        if("MEMBER".equals(s))
            i = 3;
        else
        if("PENDING".equals(s))
            i = 4;
        else
        if("INVITED".equals(s))
            i = 5;
        else
        if("BANNED".equals(s))
            i = 6;
        else
        if("IGNORED".equals(s))
            i = 7;
        else
            i = -1;
        return i;
    }

    public static Cursor getSuggestedSquares(Context context, EsAccount esaccount, String as[], String s)
    {
        android.net.Uri uri = EsProvider.appendAccountParameter(EsProvider.SQUARES_URI, esaccount);
        return context.getContentResolver().query(uri, as, "suggested!=0 AND is_member=0", null, "suggestion_sort_index");
    }

    private static int getVisibility(String s)
    {
        int i;
        if("PUBLIC".equals(s))
            i = 0;
        else
        if("MEMBERS_ONLY".equals(s))
            i = 1;
        else
            i = -1;
        return i;
    }

    private static boolean hasSquareChanged(Cursor cursor, ViewerSquare viewersquare)
    {
        int j;
        int l;
        int i1;
        boolean flag;
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        boolean flag6;
        boolean flag7;
        boolean flag8;
        boolean flag9;
        DbSquareStream adbsquarestream[];
        boolean flag10;
        String s = cursor.getString(1);
        String s1 = cursor.getString(2);
        String s2 = cursor.getString(3);
        String s3 = cursor.getString(4);
        int i = cursor.getInt(5);
        j = cursor.getInt(6);
        int k = cursor.getInt(7);
        l = cursor.getInt(10);
        i1 = cursor.getInt(25);
        SquareProfile squareprofile;
        if(cursor.getInt(8) != 0)
            flag = true;
        else
            flag = false;
        if(cursor.getInt(11) != 0)
            flag1 = true;
        else
            flag1 = false;
        if(cursor.getInt(12) != 0)
            flag2 = true;
        else
            flag2 = false;
        if(cursor.getInt(13) != 0)
            flag3 = true;
        else
            flag3 = false;
        if(cursor.getInt(14) != 0)
            flag4 = true;
        else
            flag4 = false;
        if(cursor.getInt(15) != 0)
            flag5 = true;
        else
            flag5 = false;
        if(cursor.getInt(16) != 0)
            flag6 = true;
        else
            flag6 = false;
        if(cursor.getInt(17) != 0)
            flag7 = true;
        else
            flag7 = false;
        if(cursor.getInt(23) != 0)
            flag8 = true;
        else
            flag8 = false;
        if(cursor.getInt(24) != 0)
            flag9 = true;
        else
            flag9 = false;
        adbsquarestream = DbSquareStream.deserialize(cursor.getBlob(18));
        squareprofile = viewersquare.square.profile;
        if(StringUtils.equals(s, squareprofile.name) && StringUtils.equals(s1, squareprofile.tagline) && StringUtils.equals(s2, squareprofile.photoUrl) && StringUtils.equals(s3, squareprofile.aboutText) && k == getMembershipStatus(viewersquare.viewerMembershipStatus) && i == getJoinability(viewersquare.square.joinability)) {
        	ViewerSquareCalculatedMembershipProperties viewersquarecalculatedmembershipproperties = viewersquare.calculatedMembershipProperties;
            if(viewersquarecalculatedmembershipproperties != null && (flag != PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.isMember) || flag1 != PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canSeeMemberList) || flag2 != PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canSeePosts) || flag3 != PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canJoin) || flag4 != PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canRequestToJoin) || flag5 != PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canShareSquare) || flag6 != PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canInviteToSquare)))
            {
                return true;
            }
            if(viewersquare.square.visibility != null && l != getVisibility(viewersquare.square.visibility.posts))
            {
                return true;
            }
            if(!TextUtils.isEmpty(viewersquare.viewerNotificationSettings))
            {
                boolean flag12 = "ENABLED".equals(viewersquare.viewerNotificationSettings);
                if(flag7 != flag12)
                {
                    return true;
                }
            }
            
            if(viewersquare.squareMemberStats != null && j != PrimitiveUtils.safeInt(viewersquare.squareMemberStats.memberCount))
            {
                return true;
            }
            if(viewersquare.streams == null) {
            	SquareNotificationOptions squarenotificationoptions = viewersquare.squareNotificationOptions;
                if(squarenotificationoptions != null && (flag8 != PrimitiveUtils.safeBoolean(squarenotificationoptions.autoSubscribeOnJoin) || flag9 != PrimitiveUtils.safeBoolean(squarenotificationoptions.disableSubscription)))
                {
                    flag10 = true;
                } else
                {
                    ViewerSquareSquareActivityStats viewersquaresquareactivitystats = viewersquare.squareActivityStats;
                    if(viewersquaresquareactivitystats != null && i1 != PrimitiveUtils.safeInt(viewersquaresquareactivitystats.unreadPostCount))
                        flag10 = true;
                    else
                        flag10 = false;
                }
                return flag10;
            } else { 
            	ViewerSquareStreamList viewersquarestreamlist = viewersquare.streams;
                if(viewersquarestreamlist != null && viewersquarestreamlist.squareStream != null && viewersquarestreamlist.squareStream.size() != 0) {
                	if(adbsquarestream == null)
                    {
                        return false;
                    }
                    List list = viewersquarestreamlist.squareStream;
                    if(list.size() != adbsquarestream.length)
                    {
                    	return false;
                    }
                    int j1 = adbsquarestream.length;
                    int k1 = 0;
                    int l1 = 0;
                    do
                    {
                        if(l1 >= j1)
                            break;
                        DbSquareStream dbsquarestream = adbsquarestream[l1];
                        int i2 = k1 + 1;
                        SquareStream squarestream = (SquareStream)list.get(k1);
                        if(!StringUtils.equals(dbsquarestream.getStreamId(), squarestream.id) || !StringUtils.equals(dbsquarestream.getName(), squarestream.name) || !StringUtils.equals(dbsquarestream.getDescription(), squarestream.description))
                        {
                        	return false;
                        }
                        l1++;
                        k1 = i2;
                    } while(true);
                    return true;
                } else { 
                	boolean flag11;
                    if(adbsquarestream == null || adbsquarestream.length == 0)
                        flag11 = true;
                    else
                        flag11 = false;
                    if(flag11) {
                    	return false;
                    }
                    return true;
                }
            }
    	} else {
        	return true;
        }
    }

    public static boolean insertSquare(Context context, EsAccount esaccount, ViewerSquare viewersquare) throws IOException
    {
        // TODO
    	return false;
    }

    public static int insertSquares(Context context, EsAccount esaccount, List list, List list1, List list2) throws IOException
    {
        // TODO
    	return 0;
    }

    public static long queryLastSquaresSyncTimestamp(Context context, EsAccount esaccount)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        try {
        	return DatabaseUtils.longForQuery(sqlitedatabase, "SELECT last_squares_sync_time  FROM account_status", null);
        } catch (SQLiteDoneException sqlitedoneexception) {
        	return -1L;
        }
    }

    private static ContentValues toContentValues(SquareData squaredata)
        throws IOException
    {
        ContentValues contentvalues = toContentValues(squaredata.viewerSquare);
        contentvalues.put("inviter_gaia_id", squaredata.getInviterGaiaId());
        int i;
        if(squaredata.suggested)
            i = 1;
        else
            i = 0;
        contentvalues.put("suggested", Integer.valueOf(i));
        contentvalues.put("sort_index", Integer.valueOf(squaredata.sortIndex));
        contentvalues.put("suggestion_sort_index", Integer.valueOf(squaredata.suggestionSortIndex));
        return contentvalues;
    }

    private static ContentValues toContentValues(ViewerSquare viewersquare)
        throws IOException
    {
    	List list;
        int i1;
        SquareStream squarestream;
        SquareProfile squareprofile = viewersquare.square.profile;
        ViewerSquareCalculatedMembershipProperties viewersquarecalculatedmembershipproperties = viewersquare.calculatedMembershipProperties;
        int i = getMembershipStatus(viewersquare.viewerMembershipStatus);
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("square_id", viewersquare.square.obfuscatedGaiaId);
        contentvalues.put("square_name", squareprofile.name);
        contentvalues.put("tagline", squareprofile.tagline);
        contentvalues.put("photo_url", squareprofile.photoUrl);
        contentvalues.put("about_text", squareprofile.aboutText);
        contentvalues.put("joinability", Integer.valueOf(getJoinability(viewersquare.square.joinability)));
        contentvalues.put("membership_status", Integer.valueOf(i));
        if(viewersquare.square.visibility != null)
            contentvalues.put("post_visibility", Integer.valueOf(getVisibility(viewersquare.square.visibility.posts)));
        if(viewersquarecalculatedmembershipproperties != null)
        {
            int k1;
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            int i3;
            if(PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.isMember))
                k1 = 1;
            else
                k1 = 0;
            contentvalues.put("is_member", Integer.valueOf(k1));
            if(PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canSeeMemberList))
                l1 = 1;
            else
                l1 = 0;
            contentvalues.put("can_see_members", Integer.valueOf(l1));
            if(PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canSeePosts))
                i2 = 1;
            else
                i2 = 0;
            contentvalues.put("can_see_posts", Integer.valueOf(i2));
            if(PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canJoin))
                j2 = 1;
            else
                j2 = 0;
            contentvalues.put("can_join", Integer.valueOf(j2));
            if(PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canRequestToJoin))
                k2 = 1;
            else
                k2 = 0;
            contentvalues.put("can_request_to_join", Integer.valueOf(k2));
            if(PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canShareSquare))
                l2 = 1;
            else
                l2 = 0;
            contentvalues.put("can_share", Integer.valueOf(l2));
            if(PrimitiveUtils.safeBoolean(viewersquarecalculatedmembershipproperties.canInviteToSquare))
                i3 = 1;
            else
                i3 = 0;
            contentvalues.put("can_invite", Integer.valueOf(i3));
        } else
        {
            int j;
            if(i == 3 || i == 2 || i == 1)
                j = 1;
            else
                j = 0;
            contentvalues.put("is_member", Integer.valueOf(j));
        }
        if(viewersquare.squareMemberStats != null)
            contentvalues.put("member_count", Integer.valueOf(PrimitiveUtils.safeInt(viewersquare.squareMemberStats.memberCount)));
        if(!TextUtils.isEmpty(viewersquare.viewerNotificationSettings))
        {
            int j1;
            if("ENABLED".equals(viewersquare.viewerNotificationSettings))
                j1 = 1;
            else
                j1 = 0;
            contentvalues.put("notifications_enabled", Integer.valueOf(j1));
        }
        if(viewersquare.streams != null && viewersquare.streams.squareStream != null)
        {
            list = viewersquare.streams.squareStream;
            DbSquareStream adbsquarestream[] = new DbSquareStream[list.size()];
            for(i1 = 0; i1 < adbsquarestream.length; i1++)
            {
                squarestream = (SquareStream)list.get(i1);
                adbsquarestream[i1] = new DbSquareStream(squarestream.id, squarestream.name, squarestream.description);
            }

            contentvalues.put("square_streams", DbSquareStream.serialize(adbsquarestream));
        }
        SquareNotificationOptions squarenotificationoptions = viewersquare.squareNotificationOptions;
        if(squarenotificationoptions != null)
        {
            ViewerSquareSquareActivityStats viewersquaresquareactivitystats;
            int k;
            int l;
            if(PrimitiveUtils.safeBoolean(squarenotificationoptions.autoSubscribeOnJoin))
                k = 1;
            else
                k = 0;
            contentvalues.put("auto_subscribe", Integer.valueOf(k));
            if(PrimitiveUtils.safeBoolean(squarenotificationoptions.disableSubscription))
                l = 1;
            else
                l = 0;
            contentvalues.put("disable_subscription", Integer.valueOf(l));
        }
        ViewerSquareSquareActivityStats  viewersquaresquareactivitystats = viewersquare.squareActivityStats;
        if(viewersquaresquareactivitystats != null)
            contentvalues.put("unread_count", Integer.valueOf(PrimitiveUtils.safeInt(viewersquaresquareactivitystats.unreadPostCount)));
        return contentvalues;
    }

    public static void updateSquareMembership(Context context, EsAccount esaccount, String s, String s1)
    {
        // TODO
    }

    private static boolean validateSquare(ViewerSquare viewersquare)
    {
        boolean flag;
        if(viewersquare != null && viewersquare.square != null && viewersquare.square.profile != null && !TextUtils.isEmpty(viewersquare.square.obfuscatedGaiaId))
        {
            flag = true;
        } else
        {
            if(EsLog.isLoggable("EsSquaresData", 6))
                Log.e("EsSquaresData", (new StringBuilder("Invalid ViewerSquare:\n")).append(viewersquare.toJsonString()).toString());
            flag = false;
        }
        return flag;
    }
	
	
	private static final class SquareData
    {
		public final SquareMember inviter;
        public final int sortIndex;
        public final boolean suggested;
        public final int suggestionSortIndex;
        public final ViewerSquare viewerSquare;

        public SquareData(ViewerSquare viewersquare, boolean flag, SquareMember squaremember, int i, int j)
        {
            viewerSquare = viewersquare;
            suggested = flag;
            inviter = squaremember;
            sortIndex = i;
            suggestionSortIndex = j;
        }
        
        public final String getInviterGaiaId()
        {
            String s;
            if(inviter != null)
                s = inviter.obfuscatedGaiaId;
            else
                s = null;
            return s;
        }

    }
}