/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import WriteReviewOperation.MediaRef;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.android.gallery3d.common.Fingerprint;
import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbAudienceData;
import com.galaxy.meetup.client.android.content.DbEmbedEmotishare;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.SquareTargetData;
import com.galaxy.meetup.client.android.iu.InstantUploadFacade;
import com.galaxy.meetup.client.android.network.ApiaryActivity;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.android.network.PlatformHttpRequestConfiguration;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.MediaStoreUtils;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.client.util.ResourceRedirector;
import com.galaxy.meetup.server.client.domain.CommonPerson;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.NamespaceSpecificData;
import com.galaxy.meetup.server.client.domain.PhotoServiceShareActionData;
import com.galaxy.meetup.server.client.domain.RequestsPostActivityRequestAttribution;
import com.galaxy.meetup.server.client.domain.RequestsPostActivityRequestSquareStreamInfo;
import com.galaxy.meetup.server.client.domain.Update;
import com.galaxy.meetup.server.client.domain.UpdateMetadata;
import com.galaxy.meetup.server.client.domain.request.PostActivityRequest;
import com.galaxy.meetup.server.client.domain.response.PostActivityResponse;
import com.galaxy.picasa.store.PicasaStoreFacade;

/**
 * 
 * @author sihai
 *
 */
public class PostActivityOperation extends PlusiOperation {

	private final ApiaryActivity mActivity;
    private final ApiaryApiInfo mApiInfo;
    private final List mAttachments;
    private final AudienceData mAudience;
    private final BirthdayData mBirthdayData;
    private final String mContent;
    private final String mDeepLinkId;
    private final DbEmbedEmotishare mEmotiShare;
    private final String mExternalId;
    private final DbLocation mLocation;
    private final PackageManager mPackageManager;
    private List mPostedAttachments;
    private final boolean mSaveAcl;
    
    public PostActivityOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, ApiaryActivity apiaryactivity, String s, List list, 
            String s1, DbLocation dblocation, AudienceData audiencedata, ApiaryApiInfo apiaryapiinfo, String s2, boolean flag, BirthdayData birthdaydata, 
            DbEmbedEmotishare dbembedemotishare)
    {
        super(context, esaccount, "postactivity", intent, operationlistener, new PlatformHttpRequestConfiguration(context, esaccount, "oauth2:https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/plus.stream.read https://www.googleapis.com/auth/plus.stream.write https://www.googleapis.com/auth/plus.circles.write https://www.googleapis.com/auth/plus.circles.read https://www.googleapis.com/auth/plus.photos.readwrite https://www.googleapis.com/auth/plus.native", Property.PLUS_BACKEND_URL.get(), apiaryapiinfo), PostActivityResponse.class);
        mActivity = apiaryactivity;
        mContent = s;
        mAttachments = list;
        mExternalId = s1;
        mLocation = dblocation;
        mAudience = audiencedata;
        mApiInfo = apiaryapiinfo;
        mPackageManager = context.getPackageManager();
        mDeepLinkId = s2;
        mSaveAcl = flag;
        mBirthdayData = birthdaydata;
        mEmotiShare = dbembedemotishare;
    }
    
    private static Fingerprint getFingerprint(Context context, long l)
    {
        Cursor cursor = null;
        EsAccount esaccount = EsAccountsData.getActiveAccount(context);
        Uri uri = EsProvider.appendAccountParameter(ContentUris.withAppendedId(EsProvider.PHOTO_BY_PHOTO_ID_URI, l), esaccount);
        String as[] = {
            "fingerprint"
        };
        try {
        	cursor = context.getContentResolver().query(uri, as, null, null, null);
        	if(cursor == null || !cursor.moveToFirst() || cursor.isNull(0)) {
        		return null;
        	}
        	return new Fingerprint(cursor.getBlob(0));
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
    
    private static Fingerprint getFingerprint(Context context, Uri uri)
    {
        Cursor cursor = null;
        Uri uri1 = PicasaStoreFacade.get(context).getFingerprintUri(true, false);
        String as[] = new String[1];
        as[0] = uri.toString();
        
        try {
        	cursor = context.getContentResolver().query(uri1, as, null, null, null);
        	if(null == cursor || !cursor.moveToFirst() || cursor.isNull(0)) {
        		return null;
        	}
        	return new Fingerprint(cursor.getBlob(0));
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static long getPhotoIdFromStream(Context context, String s)
    {
        Cursor cursor = null;
        EsAccount esaccount = EsAccountsData.getActiveAccount(context);
        Uri uri = EsProvider.appendAccountParameter(Uri.withAppendedPath(EsProvider.PHOTO_BY_STREAM_ID_AND_OWNER_ID_URI.buildUpon().appendPath(s).build(), esaccount.getGaiaId()), esaccount);
        String as[] = {
            "photo_id"
        };
        try {
        	cursor = context.getContentResolver().query(uri, as, null, null, null);
        	if(null == cursor || !cursor.moveToFirst() || cursor.isNull(0)) {
        		return 0L;
        	}
        	return cursor.getLong(0);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
    
    
    private PhotoServiceShareActionData getPhotosShareData(List list) {
    	
    	if(list.isEmpty()) {
    		return null;
    	}
    	
    	// TODO
    	return null;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        PostActivityResponse postactivityresponse = (PostActivityResponse)genericjson;
        List list = postactivityresponse.stream.update;
        ContentResolver contentresolver = mContext.getContentResolver();
        String s = mAccount.getName();
        ContentValues contentvalues = new ContentValues(4);
        for(Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            Update update = (Update)iterator.next();
            contentvalues.clear();
            contentvalues.put("upload_account", s);
            String s2 = update.albumId;
            if(!InstantUpload.isEnabled(mContext) && !"0".equals(s2))
                contentvalues.put("album_id", Long.valueOf(Long.parseLong(s2)));
            if(mPostedAttachments != null)
            {
                Iterator iterator1 = mPostedAttachments.iterator();
                while(iterator1.hasNext()) 
                {
                    MediaRef mediaref = (MediaRef)iterator1.next();
                    if(!mediaref.hasPhotoId() && mediaref.hasLocalUri())
                    {
                        Uri uri = mediaref.getLocalUri();
                        if(EsLog.isLoggable("HttpTransaction", 3))
                            Log.d("HttpTransaction", (new StringBuilder("  -- on-demand upload; img: ")).append(uri.toString()).toString());
                        long l = MediaStoreUtils.getMediaId(contentresolver, uri);
                        if(l < 0L)
                        {
                            if(EsLog.isLoggable("HttpTransaction", 3))
                                Log.w("HttpTransaction", (new StringBuilder("  -- on-demand upload;  no media ID for img: ")).append(uri.toString()).toString());
                        } else
                        {
                            contentvalues.put("media_id", Long.valueOf(l));
                            contentvalues.put("media_url", uri.toString());
                            contentresolver.insert(InstantUploadFacade.UPLOADS_URI, contentvalues);
                        }
                    }
                }
            }
        }

        String s1 = EsPostsData.buildActivitiesStreamKey(null, null, null, false, 0);
        if(mAudience.getSquareTargetCount() > 0)
        {
            ArrayList arraylist = new ArrayList();
            arraylist.add(s1);
            SquareTargetData asquaretargetdata[] = mAudience.getSquareTargets();
            int i = asquaretargetdata.length;
            for(int j = 0; j < i; j++)
            {
                SquareTargetData squaretargetdata = asquaretargetdata[j];
                arraylist.add(EsPostsData.buildSquareStreamKey(squaretargetdata.getSquareId(), squaretargetdata.getSquareStreamId(), false));
            }

            EsPostsData.insertMultiStreamActivities(mContext, mAccount, arraylist, list, "DEFAULT");
        } else
        {
            EsPostsData.insertActivitiesAndOverwrite(mContext, mAccount, s1, list, "DEFAULT");
        }
        if(postactivityresponse.shareboxSettings == null) {
        	if(!EsAccountsData.hadSharingRoster())
            {
                AudienceData audiencedata = new AudienceData(null, Arrays.asList(mAudience.getCircles()));
                try
                {
                    EsAccountsData.saveAudience(mContext, mAccount, DbAudienceData.serialize(audiencedata));
                }
                catch(IOException ioexception)
                {
                    Log.e("HttpTransaction", "Error saving default audience");
                }
            }
        } else { 
        	EsAccountsData.savePostingPreferences(mContext, mAccount, postactivityresponse.shareboxSettings);
        }
        
        EsAccountsData.updateAudienceHistory(mContext, mAccount, mAudience);
    }

    protected final GenericJson populateRequest()
    {
    	PostActivityRequest postactivityrequest = new PostActivityRequest();
        postactivityrequest.updateText = mContent;
        postactivityrequest.externalId = mExternalId;
        if(null != mApiInfo && null != mApiInfo.getSourceInfo()) {
        	 String s1 = "Mobile";
        	 String s = mApiInfo.getSourceInfo().getPackageName();
             if(!TextUtils.isEmpty(s)) {
                 if(!"com.google.android.apps.social".equals(s))
                	 try {
                		 s1 = Html.fromHtml(mPackageManager.getApplicationLabel(mPackageManager.getApplicationInfo(s, 0)).toString()).toString();
                	 } catch (android.content.pm.PackageManager.NameNotFoundException namenotfoundexception) {
                		 // TODO log
                	 }
             }
             
             RequestsPostActivityRequestAttribution requestspostactivityrequestattribution = new RequestsPostActivityRequestAttribution();
             requestspostactivityrequestattribution.androidAppName = s1;
             postactivityrequest.attribution = requestspostactivityrequestattribution;
        }
        
        if(mActivity != null)
        {
            postactivityrequest.mediaJson = mActivity.getMediaJson();
            postactivityrequest.embed = mActivity.getEmbed(mDeepLinkId);
        }
        if(mAttachments != null)
        {
            mPostedAttachments = new ArrayList(mAttachments.size());
            PhotoServiceShareActionData photoserviceshareactiondata = getPhotosShareData(mAttachments);
            if(photoserviceshareactiondata != null)
                postactivityrequest.photosShareData = photoserviceshareactiondata;
        }
        if(mLocation != null)
            postactivityrequest.location = mLocation.toProtocolObject();
        ResourceRedirector.getInstance();
        if(Property.ENABLE_EMOTISHARE.getBoolean() && mEmotiShare != null)
            postactivityrequest.embed = mEmotiShare.createEmbed();
        postactivityrequest.sharingRoster = EsPeopleData.convertAudienceToSharingRoster(mAudience);
        postactivityrequest.saveDefaultAcl = Boolean.valueOf(mSaveAcl);
        if(mAudience.getSquareTargetCount() > 0)
        {
            postactivityrequest.squareStreams = new ArrayList();
            SquareTargetData asquaretargetdata[] = mAudience.getSquareTargets();
            int i = asquaretargetdata.length;
            for(int j = 0; j < i; j++)
            {
                SquareTargetData squaretargetdata = asquaretargetdata[j];
                RequestsPostActivityRequestSquareStreamInfo requestspostactivityrequestsquarestreaminfo = new RequestsPostActivityRequestSquareStreamInfo();
                requestspostactivityrequestsquarestreaminfo.squareId = squaretargetdata.getSquareId();
                requestspostactivityrequestsquarestreaminfo.streamId = squaretargetdata.getSquareStreamId();
                postactivityrequest.squareStreams.add(requestspostactivityrequestsquarestreaminfo);
            }

        }
        if(mBirthdayData != null)
        {
            postactivityrequest.updateMetadata = new UpdateMetadata();
            postactivityrequest.updateMetadata.namespace = "BIRTHDAY";
            postactivityrequest.updateMetadata.namespaceSpecificData = new NamespaceSpecificData();
            postactivityrequest.updateMetadata.namespaceSpecificData.birthdayData = new com.galaxy.meetup.server.client.domain.BirthdayData();
            postactivityrequest.updateMetadata.namespaceSpecificData.birthdayData.person = new CommonPerson();
            postactivityrequest.updateMetadata.namespaceSpecificData.birthdayData.person.obfuscatedId = mBirthdayData.getGaiaId();
            postactivityrequest.updateMetadata.namespaceSpecificData.birthdayData.person.userName = mBirthdayData.getName();
            postactivityrequest.updateMetadata.namespaceSpecificData.birthdayData.person.isContactSafe = Boolean.valueOf(false);
            postactivityrequest.updateMetadata.namespaceSpecificData.birthdayData.person.isViewerFollowing = Boolean.valueOf(false);
            postactivityrequest.updateMetadata.namespaceSpecificData.birthdayData.year = Integer.valueOf(mBirthdayData.getYear());
        }
        return postactivityrequest;
    }
}
