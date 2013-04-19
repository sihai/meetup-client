/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.analytics.AnalyticsInfo;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.LinkPreviewOperation;
import com.galaxy.meetup.client.android.api.PlusOnesOperation;
import com.galaxy.meetup.client.android.network.ApiaryActivity;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.external.PlatformContract;
import com.galaxy.meetup.client.util.PlatformContractUtils;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.server.client.domain.Plusones;

/**
 * 
 * @author sihai
 *
 */
public class EsApiProvider extends ContentProvider {

	private static UriMatcher sMatcher;
    private static final LruCache sPlusoneCache = new LruCache(20);
    private static final LruCache sPreviewCache = new LruCache(20);

    static 
    {
        UriMatcher urimatcher = new UriMatcher(-1);
        sMatcher = urimatcher;
        urimatcher.addURI("com.galaxy.meetup.client.android.content.ApiProvider", "plusone", 1);
        sMatcher.addURI("com.galaxy.meetup.client.android.content.ApiProvider", "account", 2);
        sMatcher.addURI("com.galaxy.meetup.client.android.content.ApiProvider", "preview", 3);
    }
    
    public EsApiProvider()
    {
    }

    private Cursor buildPlusOneCursorFromCache(String as[], UpdateResults updateresults) {
    	EsMatrixCursor esmatrixcursor = new EsMatrixCursor(PlatformContract.PlusOneContent.COLUMNS);
        esmatrixcursor.getExtras().putInt("com.google.circles.platform.result.extra.ERROR_CODE", updateresults.mServiceResult.getErrorCode());
        esmatrixcursor.getExtras().putString("com.google.circles.platform.result.extra.ERROR_MESSAGE", updateresults.mServiceResult.getReasonPhrase());
        if(updateresults.mServiceResult.getErrorCode() != 200) {
        	return esmatrixcursor; 
        } 
        
        List arraylist = new ArrayList();
        LruCache lrucache = sPlusoneCache;
        int i = 0;
        int length = as.length;
        synchronized(lrucache) {
	        while(i < length) {
	        	Plusones plusones = (Plusones)updateresults.mResults.get(new PreviewRequestData(as[i], null));
	            if(plusones == null)
	                plusones = (Plusones)sPlusoneCache.get(Uri.parse(as[i]));
	            if(plusones == null)
	            {
	                Plusones plusones1 = new Plusones();
	                plusones1.id = as[i];
	                expandPlusOneToCursor(plusones1, esmatrixcursor);
	            } else
	            {
	                expandPlusOneToCursor((Plusones)sPlusoneCache.get(Uri.parse(as[i])), esmatrixcursor);
	                arraylist.add(plusones);
	            }
	            i++;
	        }
        }
        boolean flag;
        if(Binder.getCallingUid() == getContext().getApplicationInfo().uid)
            flag = true;
        else
            flag = false;
        if(flag)
        {
        	com.galaxy.meetup.server.client.domain.List list = new com.galaxy.meetup.server.client.domain.List();
            list.items = arraylist;
            esmatrixcursor.getExtras().putString("com.google.android.apps.content.EXTRA_PLUSONES", list.toJsonString());
        }
        
        return esmatrixcursor;
    }

    private static Cursor buildPreviewCursorFromCache(PreviewRequestData apreviewrequestdata[], UpdateResults updateresults) {
        EsMatrixCursor esmatrixcursor = new EsMatrixCursor(new String[] {
            "uri"
        });
        esmatrixcursor.getExtras().putInt("com.google.circles.platform.result.extra.ERROR_CODE", updateresults.mServiceResult.getErrorCode());
        esmatrixcursor.getExtras().putString("com.google.circles.platform.result.extra.ERROR_MESSAGE", updateresults.mServiceResult.getReasonPhrase());
        ApiaryActivity aapiaryactivity[] = new ApiaryActivity[apreviewrequestdata.length];
        for(int i = 0; i < aapiaryactivity.length; i++)
        {
            Object aobj[] = new Object[1];
            aobj[0] = apreviewrequestdata[i].uri;
            esmatrixcursor.addRow(aobj);
            aapiaryactivity[i] = (ApiaryActivity)updateresults.mResults.get(apreviewrequestdata[i]);
            if(aapiaryactivity[i] == null)
                aapiaryactivity[i] = (ApiaryActivity)sPreviewCache.get(apreviewrequestdata[i]);
        }

        if(aapiaryactivity.length > 0)
            esmatrixcursor.getExtras().putParcelableArray("com.google.android.apps.content.EXTRA_ACTIVITY", aapiaryactivity);
        return esmatrixcursor;
    }

    private static void expandPlusOneToCursor(Plusones plusones, EsMatrixCursor esmatrixcursor) {
        Boolean boolean1 = plusones.isSetByViewer;
        Integer integer;
        long l;
        Plusones.Metadata metadata;
        Object aobj[];
        if(boolean1 != null)
        {
            if(boolean1.booleanValue())
                integer = PlatformContract.PlusOneContent.STATE_PLUSONED;
            else
                integer = PlatformContract.PlusOneContent.STATE_NOTPLUSONED;
        } else
        {
            integer = PlatformContract.PlusOneContent.STATE_ANONYMOUS;
        }
        l = 0L;
        metadata = plusones.metadata;
        if(metadata != null)
        {
            Plusones.Metadata.GlobalCounts globalcounts = metadata.globalCounts;
            if(globalcounts != null)
                l = globalcounts.count.longValue();
        }
        aobj = new Object[4];
        aobj[0] = plusones.id;
        aobj[1] = Long.valueOf(l);
        aobj[2] = integer;
        aobj[3] = plusones.abtk;
        esmatrixcursor.addRow(aobj);
    }

    private ApiaryApiInfo getApiaryApiInfo(Uri uri, String s) {
        Context context = getContext();
        String s1 = uri.getQueryParameter("pkg");
        String s2;
        String s3;
        String s4;
        PackageManager packagemanager;
        String s5;
        String s6;
        String s7;
        String s8;
        String s9;
        if(Binder.getCallingUid() == context.getApplicationInfo().uid && !TextUtils.isEmpty(s1))
            s2 = s1;
        else
            s2 = context.getPackageManager().getPackagesForUid(Binder.getCallingUid())[0];
        s3 = Property.PLUS_CLIENTID.get();
        s4 = context.getPackageName();
        packagemanager = context.getPackageManager();
        s5 = PlatformContractUtils.getCertificate(s2, packagemanager);
        s6 = PlatformContractUtils.getCertificate(s4, packagemanager);
        s7 = uri.getQueryParameter("apiKey");
        s8 = uri.getQueryParameter("clientId");
        s9 = uri.getQueryParameter("apiVersion");
        return new ApiaryApiInfo(s, s3, s4, s6, s9, new ApiaryApiInfo(s7, s8, s2, s5, s9));
    }

    private Cursor getPreviews(Uri uri, String as[]) {
        boolean flag;
        if(Binder.getCallingUid() == getContext().getApplicationInfo().uid)
            flag = true;
        else
            flag = false;
        if(!flag)
            throw new SecurityException();
        PreviewRequestData apreviewrequestdata[] = new PreviewRequestData[as.length];
        for(int i = 0; i < as.length; i++)
            apreviewrequestdata[i] = PreviewRequestData.fromSelectionArg(as[i]);

        EsAccount esaccount = EsAccountsData.getActiveAccount(getContext());
        ApiaryApiInfo apiaryapiinfo = getApiaryApiInfo(uri, uri.getQueryParameter("hostKey"));
        List list = getUncachedKeys(Boolean.parseBoolean(uri.getQueryParameter("skipCache")), sPreviewCache, apreviewrequestdata);
        UpdateResults updateresults;
        if(list.size() > 0)
            updateresults = updatePreviewEntries(esaccount, apiaryapiinfo, list);
        else
            updateresults = new UpdateResults();
        return buildPreviewCursorFromCache(apreviewrequestdata, updateresults);
    }

    private static List getUncachedKeys(boolean flag, LruCache lrucache, PreviewRequestData apreviewrequestdata[]) {
        ArrayList arraylist = new ArrayList(apreviewrequestdata.length);
        if(flag) {
        	arraylist.addAll(Arrays.asList(apreviewrequestdata));
        	return arraylist;
        } else { 
        	synchronized(lrucache) {
        		int i = 0;
        		int length = apreviewrequestdata.length;
        		while(i < length) {
        			PreviewRequestData previewrequestdata = apreviewrequestdata[i];
        	        if(lrucache.get(previewrequestdata) == null)
        	            arraylist.add(previewrequestdata);
        			i++;
        		}
        	}
        }
        return arraylist;
    }

    private static List getUncachedUrls(boolean flag, LruCache lrucache, String as[]) {
        ArrayList arraylist = new ArrayList(as.length);
        if(flag) {
        	arraylist.addAll(Arrays.asList(as));
        	return arraylist;
        } else { 
        	int i;
            int j;
            i = as.length;
            j = 0;
        	synchronized(lrucache) {
        		
                while(j < i) {
                	String s = as[j];
                    if(lrucache.get(Uri.parse(s)) == null)
                        arraylist.add(s);
                	j++;
                }
        	}
        }
        
        return arraylist;
    }

    public static Uri makePreviewUri(ApiaryApiInfo apiaryapiinfo)
    {
        ApiaryApiInfo apiaryapiinfo1 = apiaryapiinfo.getSourceInfo();
        return Uri.parse(String.format("content://%s/preview", EsApiProvider.class.getName())).buildUpon().appendQueryParameter("apiKey", apiaryapiinfo1.getApiKey()).appendQueryParameter("clientId", apiaryapiinfo1.getClientId()).appendQueryParameter("apiVersion", apiaryapiinfo1.getSdkVersion()).appendQueryParameter("pkg", apiaryapiinfo1.getPackageName()).appendQueryParameter("hostKey", apiaryapiinfo.getApiKey()).build();
    }

    private UpdateResults updatePlusoneEntries(EsAccount esaccount, ApiaryApiInfo apiaryapiinfo, List list)
    {
    	
    	Context context = getContext();
        HashMap hashmap = new HashMap();
        UpdateResults updateresults;
        PlusOnesOperation plusonesoperation;
        
        for(Iterator iterator = list.iterator(); iterator.hasNext();) {
        	String s = (String)iterator.next();
        	 plusonesoperation = new PlusOnesOperation(context, esaccount, null, null, apiaryapiinfo, s);
             plusonesoperation.start();
             if(plusonesoperation.getErrorCode() == 200) {
            	 Plusones plusones = plusonesoperation.getPlusones();
                 if(plusones != null)
                 {
                     hashmap.put(Uri.parse(s), plusones);
                     synchronized(sPlusoneCache)
                     {
                         sPlusoneCache.put(Uri.parse(s), plusones);
                     }
                 }
             } else {
            	 return new UpdateResults(new ServiceResult(plusonesoperation.getErrorCode(), plusonesoperation.getReasonPhrase(), plusonesoperation.getException()));
             }
        }
        
        return new UpdateResults(new ServiceResult(), hashmap);
    }

    private UpdateResults updatePreviewEntries(EsAccount esaccount, ApiaryApiInfo apiaryapiinfo, List list)
    {
    	
    	Context context = getContext();
        HashMap hashmap = new HashMap();
        UpdateResults updateresults;
        LinkPreviewOperation linkpreviewoperation;
        for(Iterator iterator = list.iterator(); iterator.hasNext();)
        {
        	PreviewRequestData previewrequestdata = (PreviewRequestData)iterator.next();
            linkpreviewoperation = new LinkPreviewOperation(context, esaccount, null, null, previewrequestdata.uri.toString(), previewrequestdata.callToAction, apiaryapiinfo);
            linkpreviewoperation.start();
            ApiaryActivity apiaryactivity = linkpreviewoperation.getActivity();
            if(linkpreviewoperation.getErrorCode() == 200 && apiaryactivity == null)
                linkpreviewoperation.setErrorInfo(0, "null activity", null);
            if(linkpreviewoperation.getErrorCode() == 200) {
            	hashmap.put(previewrequestdata, apiaryactivity);
                synchronized(sPreviewCache)
                {
                    sPreviewCache.put(previewrequestdata, apiaryactivity);
                }
            } else {
            	return new UpdateResults(new ServiceResult(linkpreviewoperation.getErrorCode(), linkpreviewoperation.getReasonPhrase(), linkpreviewoperation.getException()));
            }
        }
        
        return new UpdateResults(new ServiceResult(), hashmap);
    }

    private boolean writePlusOne(Uri uri, ContentValues contentvalues, String s)
    {
        // TODO
    	return false;
    }

    public int delete(Uri uri, String s, String as[])
    {
        return 0;
    }

    public String getType(Uri uri) {
    	
    	String s = null;
        switch(sMatcher.match(uri)) {
	        case 1:
	        	s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.plusones";
	        	break;
	        case 2:
	        	s = "vnd.android.cursor.item/vnd.google.android.apps.plus.account";
	        	break;
	        case 3:
	        	s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.activitypreview";
	        	break;
	        default:
	        	break;
        }
        return s;
    }

    public Uri insert(Uri uri, ContentValues contentvalues)
    {
        return ContentUris.withAppendedId(uri, 0L);
    }

    public boolean onCreate()
    {
        return true;
    }

    public Cursor query(Uri uri, String as[], String s, final String urls[], String s1) {
    	
    	Cursor cursor = null;
    	Map map;
    	PackageManager packagemanager;
    	Object aobj[];
    	String as1[];
        AnalyticsInfo analyticsinfo;
        AnalyticsInfo analyticsinfo1;
        final boolean skip;
        final List urlList;
    	final EsAccount esaccount = EsAccountsData.getActiveAccount(getContext());
        switch(sMatcher.match(uri)) {
	        case 1:
	        	if(urls == null || urls.length <= 0) {
	        		cursor = null;
	        	} else { 
	                final ApiaryApiInfo info = getApiaryApiInfo(uri, null);
	                UpdateResults updateresults;
	                if(android.os.Process.myPid() != Binder.getCallingPid())
	                    analyticsinfo1 = new AnalyticsInfo(OzViews.PLATFORM_THIRD_PARTY_APP, OzViews.PLATFORM_THIRD_PARTY_APP, System.currentTimeMillis(), PlatformContractUtils.getCallingPackageAnalytics(info));
	                else
	                    analyticsinfo1 = null;
	                skip = Boolean.parseBoolean(uri.getQueryParameter("skipCache"));
	                urlList = getUncachedUrls(skip, sPlusoneCache, urls);
	                if(urlList.size() > 0)
	                    updateresults = updatePlusoneEntries(esaccount, info, urlList);
	                else
	                    updateresults = new UpdateResults();
	                if(esaccount != null && !Boolean.parseBoolean(uri.getQueryParameter("no_preview")))
	                    (new Thread(new Runnable() {

	                        public final void run()
	                        {
	                            PreviewRequestData apreviewrequestdata[] = new PreviewRequestData[urls.length];
	                            for(int i = 0; i < urls.length; i++)
	                                apreviewrequestdata[i] = new PreviewRequestData(urls[i], null);

	                            List list = EsApiProvider.getUncachedKeys(skip, EsApiProvider.sPreviewCache, apreviewrequestdata);
	                            if(urlList.size() > 0)
	                                updatePreviewEntries(esaccount, info, list);
	                        }

	                    })).start();
	                cursor = buildPlusOneCursorFromCache(urls, updateresults);
	                if(analyticsinfo1 != null)
	                {
	                    OzActions ozactions;
	                    if(!updateresults.mServiceResult.hasError())
	                        ozactions = OzActions.PLATFORM_READ_PLUSONES;
	                    else
	                        ozactions = OzActions.PLATFORM_READ_PLUSONES_ERROR;
	                    EsAnalytics.postRecordEvent(getContext(), esaccount, analyticsinfo1, ozactions);
	                }
	        	}
	        	break;
	        case 2:
	        	EsMatrixCursor esmatrixcursor = new EsMatrixCursor(PlatformContract.AccountContent.COLUMNS);
	            if(esaccount != null)
	            {
	                map = Collections.emptyMap();
	                packagemanager = getContext().getPackageManager();
	                as1 = packagemanager.getPackagesForUid(Binder.getCallingUid());
	                if(as1 != null && as1.length > 0)
	                    map = PlatformContractUtils.getCallingPackageAnalytics(new ApiaryApiInfo(null, null, null, null, null, new ApiaryApiInfo(null, null, as1[0], PlatformContractUtils.getCertificate(as1[0], packagemanager), null)));
	                analyticsinfo = new AnalyticsInfo(OzViews.PLATFORM_THIRD_PARTY_APP, OzViews.PLATFORM_THIRD_PARTY_APP, System.currentTimeMillis(), map);
	                EsAnalytics.postRecordEvent(getContext(), esaccount, analyticsinfo, OzActions.PLATFORM_GET_ACCOUNT);
	                aobj = new Object[1];
	                aobj[0] = esaccount.getName();
	                esmatrixcursor.addRow(aobj);
	            }
	            cursor = esmatrixcursor;
	        	break;
	        case 3:
	        	if(urls == null || urls.length <= 0) {
	        		cursor = null;
	        	} else { 
	        		cursor = getPreviews(uri, urls);
	        	}
	        	break;
	        default:
	        	break;
        }
        
        return cursor;
    }

    public int update(Uri uri, ContentValues contentvalues, String s, String as[])
    {
        int i;
        i = 1;
        int j;
        if(Binder.getCallingUid() == getContext().getApplicationInfo().uid)
            j = i;
        else
            j = 0;
        if(j == 0)
            throw new SecurityException();
        switch(sMatcher.match(uri)) {
	        case 1:
	        	if(null != as && as.length == i) {
	        		if(!writePlusOne(uri, contentvalues, as[0]))
	                    i = 0;
	        	}
	        	break;
	        case 2:
	        	 synchronized(sPlusoneCache)
	             {
	                 sPlusoneCache.evictAll();
	             }
	             synchronized(sPreviewCache)
	             {
	                 sPreviewCache.evictAll();
	             }
	             getContext().getContentResolver().notifyChange(Uri.parse("content://com.galaxy.meetup.client.android.content.ApiProvider/account"), null);
	             getContext().getContentResolver().notifyChange(Uri.parse("content://com.galaxy.meetup.client.android.content.ApiProvider/plusone"), null);
	             i = 0;
	        	break;
        	default:
        		i = 0;
        		break;
        }
        
        return i;
    }

	private static final class UpdateResults {

		public final Map mResults;
		public final ServiceResult mServiceResult;

		public UpdateResults() {
			this(new ServiceResult());
		}

		public UpdateResults(ServiceResult serviceresult) {
			this(serviceresult, Collections.emptyMap());
		}

		public UpdateResults(ServiceResult serviceresult, Map map) {
			mServiceResult = serviceresult;
			mResults = map;
		}
	}
}
