/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.galaxy.meetup.client.android.EsApplication;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.cache.EsMediaCache;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.GifImage;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class ImageResourceManager extends ResourceManager {

	private static ImageResourceManager sInstance;
    private final Map mActiveResources = new HashMap();
    private AvatarIdentifier mAvatarIdPool;
    private final boolean mBitmapPackingEnabled;
    private final LruCache mImageCache;
    private int mLargeImageThreshold;
    private MediaIdentifier mMediaIdPool;
    private ResourceDownloader mResourceDownloader;
    
    private ImageResourceManager(Context context) {
        super(context);
        mAvatarIdPool = null;
        mMediaIdPool = null;
        Resources resources = context.getApplicationContext().getResources();
        int i;
        boolean flag;
        if(EsApplication.sMemoryClass >= 48)
            i = Math.max(resources.getInteger(R.integer.config_image_cache_max_bytes_decoded_large), 1024 * (1024 * (EsApplication.sMemoryClass / 4)));
        else
            i = resources.getInteger(R.integer.config_image_cache_max_bytes_decoded_small);
        mLargeImageThreshold = i / 3;
        mImageCache = new LruCache(i) {

            protected final void entryRemoved(boolean flag1, Object obj, Object obj1, Object obj2)
            {
                ImageResourceIdentifier imageresourceidentifier = (ImageResourceIdentifier)obj;
                ImageResource imageresource = (ImageResource)obj1;
                if(flag1)
                {
                    if(imageresource.isDebugLogEnabled())
                        imageresource.logDebug((new StringBuilder("Evicted image from cache: ")).append(imageresourceidentifier).toString());
                    imageresource.recycle();
                }
            }

            protected final int sizeOf(Object obj, Object obj1) {
                return ((ImageResource)obj1).getSizeInBytes();
            }

        };
        
        if(android.os.Build.VERSION.SDK_INT < 11)
            flag = true;
        else
            flag = false;
        mBitmapPackingEnabled = flag;
    }
    
    static String buildShortFileName(String s) {
        long l = 0x3ffffffffffe5L;
        int i = s.length();
        for(int j = 0; j < i; j++)
            l = 31L * l + (long)s.charAt(j);

        return Long.toHexString(0xfffffffffffffffL & l >> 4);
    }
    
    public static synchronized ImageResourceManager getInstance(Context context) {
        ImageResourceManager imageresourcemanager;
        if(sInstance == null)
            sInstance = new ImageResourceManager(context.getApplicationContext());
        imageresourcemanager = sInstance;
        return imageresourcemanager;
    }
    
    private Resource getMedia(MediaRef mediaref, int i, int j, int k, int l, ResourceConsumer resourceconsumer)
    {
        MediaIdentifier mediaidentifier;
        MediaResource mediaresource;
        if(mMediaIdPool != null)
        {
            mediaidentifier = mMediaIdPool;
            mMediaIdPool = mMediaIdPool.getNextInPool();
            mediaidentifier.setNextInPool(null);
        } else
        {
            mediaidentifier = new MediaIdentifier();
        }
        mediaidentifier.init(l, mediaref, i, j, k);
        mediaresource = (MediaResource)mActiveResources.get(mediaidentifier);
        if(mediaresource == null)
        {
            mediaresource = (MediaResource)mImageCache.get(mediaidentifier);
            if(mediaresource != null)
            {
                if(mediaresource.isDebugLogEnabled())
                    mediaresource.logDebug((new StringBuilder("getMedia [CACHED]: ")).append(mediaidentifier).toString());
                mImageCache.remove(mediaidentifier);
            } else
            {
                mediaresource = new MediaResource(this, mediaidentifier);
                if(mediaresource.isDebugLogEnabled())
                    mediaresource.logDebug((new StringBuilder("getMedia [NOT CACHED]: ")).append(mediaidentifier).toString());
            }
            mActiveResources.put(mediaidentifier, mediaresource);
        } else
        {
            if(mediaresource.isDebugLogEnabled())
                mediaresource.logDebug((new StringBuilder("getMedia [ACTIVE]: ")).append(mediaidentifier).toString());
            mediaidentifier.setNextInPool(mMediaIdPool);
            mMediaIdPool = mediaidentifier;
        }
        mediaresource.register(resourceconsumer);
        return mediaresource;
    }

    public final Resource getAvatar(String s, int i, boolean flag, ResourceConsumer resourceconsumer)
    {
        AvatarIdentifier avataridentifier;
        Object obj;
        if(mAvatarIdPool != null)
        {
            avataridentifier = mAvatarIdPool;
            mAvatarIdPool = mAvatarIdPool.getNextInPool();
            avataridentifier.setNextInPool(null);
        } else
        {
            avataridentifier = new AvatarIdentifier();
        }
        avataridentifier.init(s, i, true);
        obj = (ImageResource)mActiveResources.get(avataridentifier);
        if(obj == null)
        {
            obj = (ImageResource)mImageCache.get(avataridentifier);
            if(obj != null)
            {
                if(((ImageResource) (obj)).isDebugLogEnabled())
                    ((ImageResource) (obj)).logDebug((new StringBuilder("getAvatar [CACHED]: ")).append(avataridentifier).toString());
                mImageCache.remove(avataridentifier);
            } else
            {
                obj = new UrlImageResource(this, avataridentifier, ImageUtils.getCroppedAndResizedUrl(EsAvatarData.getAvatarSizeInPx(getContext(), i), s));
                if(((ImageResource) (obj)).isDebugLogEnabled())
                    ((ImageResource) (obj)).logDebug((new StringBuilder("getAvatar [NOT CACHED]: ")).append(avataridentifier).toString());
            }
            mActiveResources.put(avataridentifier, obj);
        } else
        {
            if(((ImageResource) (obj)).isDebugLogEnabled())
                ((ImageResource) (obj)).logDebug((new StringBuilder("getAvatar [ACTIVE]: ")).append(avataridentifier).toString());
            avataridentifier.setNextInPool(mAvatarIdPool);
            mAvatarIdPool = avataridentifier;
        }
        ((ImageResource) (obj)).register(resourceconsumer);
        return ((Resource) (obj));
    }

    public final Resource getMedia(MediaRef mediaref, int i, int j, int k, ResourceConsumer resourceconsumer)
    {
        return getMedia(mediaref, 0, i, j, k, resourceconsumer);
    }

    public final Resource getMedia(MediaRef mediaref, int i, int j, ResourceConsumer resourceconsumer)
    {
        return getMedia(mediaref, i, 0, 0, j, resourceconsumer);
    }

    public final Resource getMedia(MediaRef mediaref, int i, ResourceConsumer resourceconsumer)
    {
        return getMedia(mediaref, i, 0, resourceconsumer);
    }

    protected final ResourceDownloader getResourceDownloader()
    {
        if(mResourceDownloader == null)
            mResourceDownloader = new ResourceDownloader(getContext(), new Handler());
        return mResourceDownloader;
    }

    public final void onEnvironmentChanged()
    {
        NetworkInfo networkinfo;
        if(mActiveResources.size() != 0)
            if((networkinfo = ((ConnectivityManager)getContext().getSystemService("connectivity")).getActiveNetworkInfo()) != null && networkinfo.isConnected())
            {
                Iterator iterator = mActiveResources.values().iterator();
                while(iterator.hasNext()) 
                {
                    Resource resource = (Resource)iterator.next();
                    if(resource.getStatus() == 5)
                    {
                        deliverResourceStatus(resource, 2);
                        loadResource(resource);
                    }
                }
            }
    }

    public final void onFirstConsumerRegistered(Resource resource) {
    	
        if(!mActiveResources.containsKey(resource.getIdentifier()))
            throw new IllegalStateException((new StringBuilder("Resource is not active: ")).append(resource.getIdentifier()).toString());
        
        ImageResource imageresource = (ImageResource)resource;
        
        switch(imageresource.getStatus()) {
	        case 0:
	        	if(imageresource.isDebugLogEnabled())
	                imageresource.logDebug((new StringBuilder("Requesting image load: ")).append(imageresource.mId).toString());
	            imageresource.mStatus = 2;
	            loadResource(resource);
	        	break;
	        case 1:
	        	break;
	        case 2:
	        	throw new IllegalStateException((new StringBuilder("Illegal resource state: ")).append(resource.getStatusAsString()).toString());
	        case 3:
	        	throw new IllegalStateException((new StringBuilder("Illegal resource state: ")).append(resource.getStatusAsString()).toString());
	        case 4:
	        	throw new IllegalStateException((new StringBuilder("Illegal resource state: ")).append(resource.getStatusAsString()).toString());
	        case 5:
	        	if(imageresource.isDebugLogEnabled())
	                imageresource.logDebug((new StringBuilder("Requesting image load: ")).append(imageresource.mId).toString());
	            imageresource.mStatus = 2;
	            loadResource(resource);
	        	break;
	        case 6:
	        	throw new IllegalStateException((new StringBuilder("Illegal resource state: ")).append(resource.getStatusAsString()).toString());
	        case 7:
	        	break;
	        case 8:
	        	if(imageresource.isDebugLogEnabled())
	                imageresource.logDebug((new StringBuilder("Requesting image load: ")).append(imageresource.mId).toString());
	            imageresource.mStatus = 2;
	            loadResource(resource);
	        	break;
	        case 9:
	        	if(mBitmapPackingEnabled)
	                imageresource.unpack();
	            else
	                resource.mStatus = 1;
	        	break;
	        default:
	        	throw new IllegalStateException((new StringBuilder("Illegal resource state: ")).append(resource.getStatusAsString()).toString());
        }
    }

    public final void onLastConsumerUnregistered(Resource resource) {
        ImageResource imageresource = (ImageResource)resource;
        ImageResourceIdentifier imageresourceidentifier = (ImageResourceIdentifier)imageresource.mId;
        if(imageresource.isDebugLogEnabled())
            imageresource.logDebug((new StringBuilder("Deactivating image resource: ")).append(imageresourceidentifier).toString());
        int status = imageresource.getStatus();
        if(status == 2 || status == 3)
        {
            imageresource.mStatus = 8;
            if(mResourceDownloader != null)
                mResourceDownloader.cancelDownload(imageresource);
        }
        mActiveResources.remove(imageresourceidentifier);
        if(status == 1 && (2 & imageresourceidentifier.mFlags) == 0)
        {
            boolean flag;
            if(!(((Resource) (imageresource)).mResource instanceof Bitmap) || !mBitmapPackingEnabled)
                flag = true;
            else
            if(((Bitmap)((Resource) (imageresource)).mResource).getConfig() != null)
                flag = true;
            else
                flag = false;
            if(flag)
            {
                int j = imageresource.getSizeInBytes();
                boolean flag1;
                if(j == -1 || j >= mLargeImageThreshold)
                    flag1 = true;
                else
                    flag1 = false;
                if(!flag1)
                {
                    if(mBitmapPackingEnabled)
                        imageresource.pack();
                    mImageCache.put(imageresourceidentifier, imageresource);
                }
            }
        }
    }

    public final void verifyEmpty()
    {
        if(!mActiveResources.isEmpty())
        {
            Log.i("ImageResourceManager", (new StringBuilder("ImageResourceManager contains ")).append(mActiveResources.size()).append(" resources").toString());
            Resource resource;
            for(Iterator iterator = mActiveResources.values().iterator(); iterator.hasNext(); EsLog.writeToLog(4, "ImageResourceManager", (new StringBuilder()).append(resource.toString()).append("\n").toString()))
                resource = (Resource)iterator.next();

        }
    }

    
	private static abstract class ImageResourceIdentifier extends Resource.ResourceIdentifier {
		protected int mFlags;

		public abstract String getShortFileName();

		public final void init(int i) {
			mFlags = i;
		}
	}
    
	private abstract class UrlImageResourceIdentifier extends ImageResourceIdentifier {

		protected String mUrl;

		protected final void init(int i, String s) {
			super.init(0);
			mUrl = s;
		}
	}
    
    private final class AvatarIdentifier extends UrlImageResourceIdentifier {
    	
    	private int mHashCode;
        private AvatarIdentifier mNextInPool;
        private boolean mRounded;
        private int mSizeCategory;
        
		public final boolean equals(Object obj) {

			if (obj == this) {
				return true;
			}

			if (!(obj instanceof AvatarIdentifier)) {
				return false;
			}

			AvatarIdentifier avataridentifier = (AvatarIdentifier) obj;
			if (mSizeCategory != avataridentifier.mSizeCategory
					|| mRounded != avataridentifier.mRounded
					|| !mUrl.equals(avataridentifier.mUrl)) {
				return false;
			}
			return true;

		}

        public final AvatarIdentifier getNextInPool() {
            return mNextInPool;
        }

		public final String getShortFileName() {
			StringBuilder stringbuilder;
			stringbuilder = new StringBuilder();
			stringbuilder.append(ImageResourceManager.buildShortFileName(mUrl));
			if (0 == mSizeCategory) {
				stringbuilder.append("-at");
			} else if (1 == mSizeCategory) {
				stringbuilder.append("-as");
			} else if (2 == mSizeCategory) {
				stringbuilder.append("-am");
			} else {

			}
			return stringbuilder.toString();
		}

        public final int hashCode() {
            if(mHashCode == 0) {
                mHashCode = mUrl.hashCode() ^ mSizeCategory;
                if(mRounded)
                    mHashCode = 1 + mHashCode;
            }
            return mHashCode;
        }

        public final void init(String s, int i, boolean flag) {
            super.init(0, s);
            mSizeCategory = i;
            mRounded = flag;
            mHashCode = 0;
        }

        public final void setNextInPool(AvatarIdentifier avataridentifier) {
            mNextInPool = avataridentifier;
        }

        public final String toString() {
        	
        	String s = null;
        	if (0 == mSizeCategory) {
        		s = "tiny";
			} else if (1 == mSizeCategory) {
				s = "small";
			} else if (2 == mSizeCategory) {
				s = "medium";
			} else {

			}
        	
        	StringBuilder stringbuilder = (new StringBuilder("{")).append(mUrl).append(" (").append(s).append(")");
            String s1;
            if(mRounded)
                s1 = " (rounded)";
            else
                s1 = "";
            return stringbuilder.append(s1).append("}").toString();
        }

    }
    
    private final class MediaIdentifier extends ImageResourceIdentifier {

    	private int mHashCode;
        private int mHeight;
        private MediaRef mMediaRef;
        private MediaIdentifier mNextInPool;
        private int mSizeCategory;
        private int mWidth;

		public final MediaIdentifier getNextInPool() {
			return mNextInPool;
		}

		public final void init(int i, MediaRef mediaref, int j, int k, int l) {
			init(i);
			mMediaRef = mediaref;
			mSizeCategory = j;
			mWidth = k;
			mHeight = l;
			mHashCode = 0;
		}

		public final void setNextInPool(MediaIdentifier mediaidentifier) {
			mNextInPool = mediaidentifier;
		}

        public final String getShortFileName() {
        	StringBuilder stringbuilder = new StringBuilder();
            if(mMediaRef.hasUrl())
                stringbuilder.append(ImageResourceManager.buildShortFileName(mMediaRef.getUrl()));
            else if(mMediaRef.hasLocalUri())
                stringbuilder.append(ImageResourceManager.buildShortFileName(mMediaRef.getLocalUri().toString()));
            else
                throw new IllegalStateException("A media ref should have a URI");
            
            switch(mSizeCategory) {
	            case 0:
	            	stringbuilder.append('-').append(mWidth).append('x').append(mHeight);
	            	break;
	            case 1:
	            	if((4 & mFlags) != 0)
	                    stringbuilder.append("-a");
	            	break;
	            case 2:
	            	stringbuilder.append("-t");
	            	break;
	            case 3:
	            	stringbuilder.append("-b");
	            	break;
	            case 4:
	            	stringbuilder.append("-p");
	            	break;
	            case 5:
	            	stringbuilder.append("-l");
	            	break;
            	default:
            		if((4 & mFlags) != 0)
                        stringbuilder.append("-a");
            		break;
            }
            return stringbuilder.toString();
        }

        
        public final boolean equals(Object obj) {
        	
        	if(obj == this) {
        		return true;
        	}
        	if(!(obj instanceof MediaIdentifier)) {
        		return false;
        	}
        	MediaIdentifier mediaidentifier = (MediaIdentifier)obj;
        	if(mSizeCategory != mediaidentifier.mSizeCategory || mFlags != mediaidentifier.mFlags || !mMediaRef.equals(mediaidentifier.mMediaRef) || mWidth != mediaidentifier.mWidth || mHeight != mediaidentifier.mHeight) {
        		return false;
        	}
        	return true;
        }
        
        public final int hashCode()
        {
            if(mHashCode == 0)
                mHashCode = mMediaRef.hashCode() + mSizeCategory + mFlags;
            return mHashCode;
        }

        public final String toString() {
        	String s = null;
        	switch(mSizeCategory){
	        	case 0:
	        		s = (new StringBuilder()).append(mWidth).append("x").append(mHeight).toString();
	        		break;
	        	case 1:
	        		s = "full";
	        		break;
	        	case 2:
	        		s = "thumbnail";
	        		break;
	        	case 3:
	        		s = "large";
	        		break;
	        	case 4:
	        		s = "portrait";
	        		break;
	        	case 5:
	        		s = "landscape";
	        		break;
        		default:
        			break;
        	}
        	return s;
        }
    }
    
    private static final class MediaResource extends ImageResource {

    	public MediaResource(ImageResourceManager imageresourcemanager, MediaIdentifier mediaidentifier) {
            super(imageresourcemanager, mediaidentifier);
        }
    	
        protected final void downloadResource() {
            MediaIdentifier mediaidentifier = (MediaIdentifier)mId;
            int sizeCategory = mediaidentifier.mSizeCategory;
            int width = mediaidentifier.mWidth;
            int height = mediaidentifier.mHeight;
            MediaRef mediaref = mediaidentifier.mMediaRef;
            
            if(mediaref.hasLocalUri()) {
                Uri uri = mediaref.getLocalUri();
                String s = uri.getScheme();
                if(s != null && !s.startsWith("http")) {
                	ResourceDownloader.loadLocalResource(mManager.getContext(), this, uri, sizeCategory, width, height);
                	return;
                }
            }
            ResourceDownloader resourcedownloader = ((ImageResourceManager)mManager).getResourceDownloader();
            boolean flag;
            if((4 & mediaidentifier.mFlags) != 0)
                flag = true;
            else
                flag = false;
            resourcedownloader.downloadResource(this, mediaref, sizeCategory, width, height, flag);
        }
    }
    
    private static abstract class ImageResource extends Resource {

    	protected final String mCacheDir;
        protected final String mCacheFile;

        protected ImageResource(ImageResourceManager imageresourcemanager, ImageResourceIdentifier imageresourceidentifier) {
            super(imageresourcemanager, imageresourceidentifier);
            mCacheFile = imageresourceidentifier.getShortFileName();
            mCacheDir = mCacheFile.substring(0, 1);
        }
        
    	protected abstract void downloadResource();
    	
        public final void deliverData(byte abyte0[], boolean flag) {
        	ImageResourceIdentifier imageresourceidentifier = (ImageResourceIdentifier)mId;
            boolean flag1;
            if((2 & imageresourceidentifier.mFlags) != 0)
                flag1 = true;
            else
            if((1 & imageresourceidentifier.mFlags) != 0)
                flag1 = false;
            else
                flag1 = flag;
            if(flag1) {
                if(isDebugLogEnabled())
                    logDebug((new StringBuilder("Saving image in file cache: ")).append(mId).toString());
                EsMediaCache.write(mManager.getContext(), mCacheDir, mCacheFile, abyte0);
            }
            
            if(mStatus == 2 || mStatus == 3) {
            	if((2 & imageresourceidentifier.mFlags) != 0)
                {
                    if(isDebugLogEnabled())
                        logDebug((new StringBuilder("Image decoding disabled. Delivering null to consumers: ")).append(mId).toString());
                    mManager.deliverResourceContent(this, 1, null);
                    return;
                }
            	if((4 & imageresourceidentifier.mFlags) != 0 && GifImage.isGif(abyte0))
                {
                    mManager.deliverResourceContent(this, 1, new GifImage(abyte0));
                    return;
                }
            	
            	try
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length);
                    if(bitmap != null && (mId instanceof AvatarIdentifier) && ((AvatarIdentifier)mId).mRounded)
                    {
                        Bitmap bitmap1 = ImageUtils.getRoundedBitmap(mManager.getContext(), bitmap);
                        if(bitmap != bitmap1)
                        {
                            bitmap.recycle();
                            bitmap = bitmap1;
                        }
                    }
                    if(bitmap != null)
                    {
                        if(isDebugLogEnabled())
                            logDebug((new StringBuilder("Delivering image to consumers: ")).append(mId).toString());
                        mManager.deliverResourceContent(this, 1, bitmap);
                    } else
                    {
                        if(isDebugLogEnabled())
                            logDebug((new StringBuilder("Bad image; cannot decode: ")).append(mId).toString());
                        mManager.deliverResourceStatus(this, 6);
                    }
                }
                catch(OutOfMemoryError outofmemoryerror)
                {
                    if(isDebugLogEnabled())
                        logDebug((new StringBuilder("Out of memory while decoding image: ")).append(mId).toString());
                    mManager.deliverResourceContent(this, 7, null);
                }
            } else {
            	if(isDebugLogEnabled())
                    logDebug((new StringBuilder("Request no longer needed, not delivering: ")).append(mId).append(", status: ").append(getStatusAsString()).toString());
            }
        }

        public final File getCacheFileName() {
            File file;
            if((1 & ((ImageResourceIdentifier)mId).mFlags) != 0)
                file = null;
            else
                file = EsMediaCache.getMediaCacheFile(mManager.getContext(), mCacheDir, mCacheFile);
            return file;
        }

		public final int getSizeInBytes() {
			int i = -1;
			if (null == mResource) {
				return i;
			}
			if (mResource instanceof Bitmap) {
				Bitmap bitmap = (Bitmap) mResource;
				i = bitmap.getRowBytes() * bitmap.getHeight();
			} else if (mResource instanceof GifImage)
				i = ((GifImage) mResource).getSizeEstimate();
			else if (mResource instanceof PackedBitmap)
				i = ((PackedBitmap) mResource).sizeInBytes;
			return i;
		}

        public final void load() {
            ImageResourceIdentifier imageresourceidentifier = (ImageResourceIdentifier)mId;
            if((2 & imageresourceidentifier.mFlags) != 0 && EsMediaCache.exists(mManager.getContext(), mCacheDir, mCacheFile)) {
                mManager.deliverResourceContent(this, 1, null);
            } else {
                int i = 1 & imageresourceidentifier.mFlags;
                byte abyte0[] = null;
                if(i == 0)
                    abyte0 = EsMediaCache.read(mManager.getContext(), mCacheDir, mCacheFile);
                if(abyte0 != null) {
                    if(isDebugLogEnabled())
                        logDebug((new StringBuilder("Loaded image from file: ")).append(mId).toString());
                    deliverData(abyte0, false);
                } else {
                    if(isDebugLogEnabled())
                        logDebug((new StringBuilder("Requesting network download: ")).append(mId).toString());
                    mManager.deliverResourceStatus(this, 3);
                    downloadResource();
                }
            }
        }

        public final void pack()
        {
            if(mStatus == 1 && (mResource instanceof Bitmap))
            {
                Bitmap bitmap = (Bitmap)mResource;
                android.graphics.Bitmap.Config config = bitmap.getConfig();
                if(config != null)
                    try
                    {
                        PackedBitmap packedbitmap = new PackedBitmap();
                        packedbitmap.config = config;
                        packedbitmap.width = bitmap.getWidth();
                        packedbitmap.height = bitmap.getHeight();
                        packedbitmap.sizeInBytes = bitmap.getRowBytes() * bitmap.getHeight();
                        packedbitmap.buffer = ByteBuffer.allocate(packedbitmap.sizeInBytes);
                        bitmap.copyPixelsToBuffer(packedbitmap.buffer);
                        bitmap.recycle();
                        mResource = packedbitmap;
                        mStatus = 9;
                    }
                    catch(OutOfMemoryError outofmemoryerror)
                    {
                        mStatus = 7;
                    }
            }
        }

        public final void recycle() {
            if(mResource instanceof Bitmap)
                ((Bitmap)mResource).recycle();
            super.recycle();
        }

        public final void unpack() {
            PackedBitmap packedbitmap;
            if(mStatus != 9 || !(mResource instanceof PackedBitmap))
                return;
            try {
	            packedbitmap = (PackedBitmap)mResource;
	            Bitmap bitmap = Bitmap.createBitmap(packedbitmap.width, packedbitmap.height, packedbitmap.config);
	            packedbitmap.buffer.rewind();
	            bitmap.copyPixelsFromBuffer(packedbitmap.buffer);
	            mResource = bitmap;
	            mStatus = 1;
            } catch (OutOfMemoryError outofmemoryerror) {
            	 mResource = null;
                 mStatus = 7;
            }
        }
    }
    
    private static final class UrlImageResource extends ImageResource {

    	private String mDownloadUrl;

        public UrlImageResource(ImageResourceManager imageresourcemanager, UrlImageResourceIdentifier urlimageresourceidentifier, String s) {
            super(imageresourcemanager, urlimageresourceidentifier);
            mDownloadUrl = s;
        }
        
        protected final void downloadResource() {
            ((ImageResourceManager)mManager).getResourceDownloader().downloadResource(this, mDownloadUrl);
        }

    }
    
	private static final class PackedBitmap {

		public ByteBuffer buffer;
		public android.graphics.Bitmap.Config config;
		public int height;
		public int sizeInBytes;
		public int width;

		private PackedBitmap() {
		}
	}

}
