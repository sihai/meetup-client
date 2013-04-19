/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.PanoramaDetector;
import com.galaxy.meetup.client.util.FIFEUtil;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.MediaStoreUtils;
import com.galaxy.meetup.client.util.ScreenMetrics;
import com.galaxy.picasa.Config;
import com.galaxy.picasa.store.PicasaStoreFacade;
import com.galaxy.picasa.sync.PicasaFacade;

/**
 * 
 * @author sihai
 *
 */
public class ResourceDownloader {

	private static int sLandscapeHeight;
    private static int sLandscapeWidth;
    private static int sPortraitHeight;
    private static int sPortraitWidth;
    private static ResourceRequestFilter sRequestFilter = new ResourceRequestFilter();
    private Context mContext;
    private VolleyRequestQueue mRequestQueue;
    
    public ResourceDownloader(Context context, Handler handler) {
        mContext = context.getApplicationContext();
        int i = ScreenMetrics.getInstance(context).longDimension;
        int j = i / 2;
        sPortraitHeight = j;
        sPortraitWidth = j / context.getResources().getDimensionPixelSize(R.dimen.media_max_portrait_aspect_ratio);
        int k = i / 2;
        sLandscapeWidth = k;
        sLandscapeHeight = k / context.getResources().getDimensionPixelSize(R.dimen.media_min_landscape_aspect_ratio);
        mRequestQueue = new VolleyRequestQueue(context, handler);
    }
    
    
    public static void loadLocalResource(Context context, Resource resource, Uri uri, int i, int j, int k) {
        if(resource.isDebugLogEnabled())
            resource.logDebug((new StringBuilder("Loading local resource ")).append(uri).toString());
        boolean flag = ImageUtils.isVideoMimeType(ImageUtils.getMimeType(context.getContentResolver(), uri));
        boolean flag1 = MediaStoreUtils.isMediaStoreUri(uri);
        if(!flag)
            PanoramaDetector.detectPanorama(context, resource, uri);
        if(i == 2) {
        	k = Config.sThumbNailSize;
        	j = k;
        } else {
        	switch(i) {
	        	case 0:
	        	case 1:
	        		break;
	        	case 2:
	        		throw new UnsupportedOperationException();
	        	case 3:
	        	case 4:
	        	case 5:
	        		k = Config.sScreenNailSize;
	                j = k;
	        		break;
	        	default:
	        		throw new UnsupportedOperationException();
        	}
        }
        
        try {
	        android.graphics.Bitmap bitmap = null;
	        int l;
	        if(!flag1) {
	        	if(!flag) {
	        		if(i != 1) {
	        			l = Math.max(j, k);
	        		} else {
	        			l = 0;
	        		}
	        		
	        		bitmap = ImageUtils.createLocalBitmap(context.getContentResolver(), uri, l);
	        	} else { 
	        		int i1 = ImageUtils.getMaxThumbnailDimension(context, 3);
	                byte byte0;
	                if(j > i1 || k > i1)
	                    byte0 = 1;
	                else
	                    byte0 = 3;
	                bitmap = ImageUtils.createVideoThumbnail(context, uri, byte0);
	        	}
	        } else { 
	        	if(i != 2 && !flag) {
	        		if(i == 1)
	                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
	                else
	                    bitmap = ImageUtils.createLocalBitmap(context.getContentResolver(), uri, Math.max(j, k));
	        	} else { 
	        		bitmap = MediaStoreUtils.getThumbnail(context, uri, j, k);
	        	}
	        }
	        
	        if(bitmap == null) 
	        	resource.deliverDownloadError(4); 
	        else 
	        	resource.deliverResource(bitmap);
        } catch (FileNotFoundException e) {
	        	resource.deliverDownloadError(4);
        } catch (IOException e) {
        	resource.deliverDownloadError(6);
        }
    }
    
	public final void cancelDownload(Resource resource) {
		synchronized (sRequestFilter) {
			sRequestFilter.resource = resource;
			mRequestQueue.cancelAll(sRequestFilter);
		}
	}

    public final void downloadResource(Resource resource, MediaRef mediaref, int i, int j, int k, boolean flag) {
        String s = mediaref.getUrl();
        Uri uri;
        long l;
        if(s != null && !flag)
        {
            if(s.startsWith("//"))
                s = (new StringBuilder("http:")).append(s).toString();
            if(FIFEUtil.isFifeHostedUrl(s))
            {
                Uri uri1 = Uri.parse(s);
                String s3 = FIFEUtil.getImageUriOptions(uri1);
                if(!s3.contains("k"))
                {
                    String s4;
                    if(TextUtils.isEmpty(s3))
                        s4 = "k";
                    else
                        s4 = (new StringBuilder()).append(s3).append("-k").toString();
                    s = FIFEUtil.setImageUriOptions(s4, uri1).toString();
                }
            }
        }
        uri = mediaref.getLocalUri();
        l = mediaref.getPhotoId();
        if(l != 0L)
        {
            Context context = mContext;
            String s1 = mediaref.getOwnerGaiaId();
            boolean flag1;
            if(s1 == null)
            {
                flag1 = false;
            } else
            {
                EsAccount esaccount = EsService.getActiveAccount(context);
                if(esaccount == null)
                    flag1 = false;
                else
                    flag1 = esaccount.isMyGaiaId(s1);
            }
            if(flag1 && i != 0 && i != 1)
            {
                String s2;
                if(i == 2)
                    s2 = "thumbnail";
                else
                    s2 = "screennail";
                if(s != null && !s.startsWith("content:"))
                    uri = PicasaStoreFacade.get(mContext).getPhotoUri(l, s2, s);
                else
                    uri = PicasaFacade.get(mContext).getPhotoUri(l).buildUpon().appendQueryParameter("type", s2).build();
            }
        }
        mRequestQueue.add(new DownloadRequest(resource, uri, s, i, j, k));
    }

	public final void downloadResource(Resource resource, String s) {
		mRequestQueue.add(new DownloadRequest(resource, s));
	}
    
    private static final class DownloadRequest extends VolleyRequest {

    	private final boolean mConstructDownloadUrl;
        private String mDownloadUrl;
        private final int mHeight;
        private final Resource mResource;
        private final int mSizeCategory;
        private final String mUrl;
        private final int mWidth;

		public DownloadRequest(Resource resource, Uri uri, String s, int i,
				int j, int k) {
			this(resource, uri, s, i, j, k, true);
		}

		public DownloadRequest(Resource resource, Uri uri, String s, int i,
				int j, int k, boolean flag) {
			super(null, uri);
			mUrl = s;
			mResource = resource;
			mSizeCategory = i;
			mWidth = j;
			mHeight = k;
			mConstructDownloadUrl = flag;
			setShouldCache(false);
		}

		public DownloadRequest(Resource resource, String s) {
			this(resource, null, s, -1, 0, 0, false);
		}
        
		public final void deliverError(VolleyError volleyerror) {
			if (mResource.isDebugLogEnabled()) {
				Resource _tmp = mResource;
				Log.e("EsResource", (new StringBuilder("Failed to download "))
						.append(mUrl).toString(), volleyerror);
			}
			if (volleyerror instanceof VolleyOutOfMemoryError)
				mResource.deliverDownloadError(7);
			else if (volleyerror instanceof NoConnectionError) {
				mResource.deliverDownloadError(5);
			} else {
				int i;
				if (volleyerror.networkResponse != null)
					i = volleyerror.networkResponse.statusCode;
				else
					i = 0;
				mResource.deliverHttpError(i);
			}
		}

		public final void deliverResponse(Object obj) {
			deliverResponse((byte[]) obj);
		}

		public final void deliverResponse(byte abyte0[]) {
			mResource.deliverData(abyte0, true);
		}

        public final String getUrl() {
        	
        	if(!mConstructDownloadUrl || mDownloadUrl != null) {
        		mDownloadUrl = mUrl;
        		return mDownloadUrl;
        	}
        	
        	
        	String s = mUrl;
            if(s.startsWith("//"))
                s = (new StringBuilder("http:")).append(s).toString();
            
            switch(mSizeCategory) {
	            case 0:
	            	if(mWidth == 0 || mHeight == 0)
	                    mDownloadUrl = ImageUtils.getResizedUrl(mWidth, mHeight, s);
	                else
	                    mDownloadUrl = ImageUtils.getCenterCroppedAndResizedUrl(mWidth, mHeight, s);
	            	break;
	            case 1:
	            	mDownloadUrl = PicasaStoreFacade.convertImageUrl(s, 0, false);
	            	break;
	            case 2:
	            	mDownloadUrl = PicasaStoreFacade.convertImageUrl(s, Config.sThumbNailSize, true);
	            	break;
	            case 3:
	            	mDownloadUrl = PicasaStoreFacade.convertImageUrl(s, Config.sScreenNailSize, false);
	            	break;
	            case 4:
	            	mDownloadUrl = ImageUtils.getCenterCroppedAndResizedUrl(ResourceDownloader.sPortraitWidth, ResourceDownloader.sPortraitHeight, s);
	            	break;
	            case 5:
	            	mDownloadUrl = ImageUtils.getCenterCroppedAndResizedUrl(ResourceDownloader.sLandscapeWidth, ResourceDownloader.sLandscapeHeight, s);
	            	break;
            	default:
            		break;
            }
            return mDownloadUrl;
        }

    }
    
	private static final class ResourceRequestFilter implements
			com.android.volley.RequestQueue.RequestFilter {

		public Resource resource;

		private ResourceRequestFilter() {
		}
		
		public final boolean apply(Request request) {
			boolean flag;
			if (((DownloadRequest) request).mResource == resource)
				flag = true;
			else
				flag = false;
			return flag;
		}

	}
    
}
