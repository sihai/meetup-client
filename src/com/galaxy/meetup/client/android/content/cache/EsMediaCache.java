/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.galaxy.meetup.client.android.api.DownloadImageOperation;
import com.galaxy.meetup.client.android.content.AvatarImageRequest;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageDownloader;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class EsMediaCache {

	private static ImageCache sImageCache;
    private static long sMaxCacheSize;
    private static File sMediaCacheDir;
    
    public static void cleanup() {
    	
    	if(null == sMediaCacheDir) {
    		return;
    	}
    	
    	File afile[] = sMediaCacheDir.listFiles();
    	if(null == afile || 0 == afile.length) {
    		return;
    	}
    	
    	long l = System.currentTimeMillis();
    	long l1 = 0L;
    	List<MediaCacheFile> arraylist = new ArrayList<MediaCacheFile>();
    	for(int i = 0; i < afile.length; i++) {
            File afile1[] = afile[i].listFiles();
            if(null != afile1 && afile1.length > 0) {
	            for(int j1 = 0; j1 < afile1.length; j1++) {
	                MediaCacheFile mediacachefile2 = new MediaCacheFile(afile1[j1], l);
	                arraylist.add(mediacachefile2);
	                l1 += mediacachefile2.size;
	            }
            }
        }
    	
    	if(sMaxCacheSize == 0L) {
            if(android.os.Build.VERSION.SDK_INT >= 9) {
                long l2 = (long)(0.25D * (double)(l1 + sMediaCacheDir.getUsableSpace()));
                sMaxCacheSize = l2;
                if(l2 < 0x500000L)
                    sMaxCacheSize = 0x500000L;
                if(sMaxCacheSize > 0x6400000L)
                    sMaxCacheSize = 0x6400000L;
            } else {
                sMaxCacheSize = 0xf00000L;
            }
    	}
    	
    	if(l1 > sMaxCacheSize) {
            Collections.sort(arraylist);
            int j = arraylist.size();
            if(EsLog.isLoggable("ResourceCache", 3)) {
                Log.d("ResourceCache", "Media cache");
                int i1 = 0;
                while(i1 < j) {
                    MediaCacheFile mediacachefile1 = (MediaCacheFile)arraylist.get(i1);
                    StringBuilder stringbuilder = new StringBuilder();
                    String s;
                    if(mediacachefile1.recent)
                        s = "R ";
                    else
                        s = "  ";
                    Log.d("ResourceCache", stringbuilder.append(s).append(l - mediacachefile1.timestamp).append(" ms, ").append(mediacachefile1.size).append(" bytes").toString());
                    i1++;
                }
            }
            int k = 0;
            while(k < j && l1 > sMaxCacheSize) {
                MediaCacheFile mediacachefile = (MediaCacheFile)arraylist.get(k);
                if(mediacachefile.file.delete())
                    l1 -= mediacachefile.size;
                k++;
            }
        }
    }
    
    public static Bitmap cropPhoto(Bitmap bitmap, float f, float f1) {
        Bitmap bitmap1;
        if(bitmap != null) {
            float f2 = bitmap.getWidth();
            float f3 = bitmap.getHeight();
            int i;
            int j;
            Bitmap bitmap2;
            float f4;
            float f5;
            if(f2 / f3 > f / f1) {
                j = (int)((f2 * f1) / f3);
                i = (int)f1;
            } else {
                i = (int)((f3 * f) / f2);
                j = (int)f;
            }
            bitmap2 = Bitmap.createScaledBitmap(bitmap, j, i, true);
            f4 = (float)bitmap2.getWidth() - f;
            f5 = (float)bitmap2.getHeight() - f1;
            bitmap1 = Bitmap.createBitmap(bitmap2, (int)(f4 / 2.0F), (int)(f5 / 2.0F), (int)f, (int)f1);
        } else {
            bitmap1 = null;
        }
        return bitmap1;
    }
    
    public static boolean exists(Context context, String s, String s1) {
        return getMediaCacheFile(context, s, s1).exists();
    }

    public static byte[] getMedia(Context context, CachedImageRequest cachedimagerequest) {
        return read(context, cachedimagerequest.getCacheDir(), cachedimagerequest.getCacheFileName());
    }

    private static File getMediaCacheDir(Context context) {
        if(sMediaCacheDir == null)
            sMediaCacheDir = new File(context.getCacheDir(), "media");
        if(!sMediaCacheDir.exists())
            try{
                sMediaCacheDir.mkdir();
            } catch(Exception exception) {
                Log.e("ResourceCache", (new StringBuilder("Cannot create cache media directory: ")).append(sMediaCacheDir).toString(), exception);
            }
        return sMediaCacheDir;
    }

    public static File getMediaCacheFile(Context context, String s, String s1) {
        return new File(new File(getMediaCacheDir(context), s), s1);
    }

    public static void insertMedia(Context context, CachedImageRequest cachedimagerequest, byte[] abyte0) {
        if(sImageCache == null)
            sImageCache = ImageCache.getInstance(context);
        write(context, cachedimagerequest.getCacheDir(), cachedimagerequest.getCacheFileName(), abyte0);
        sImageCache.notifyMediaImageChange(cachedimagerequest, abyte0);
    }

    public static Map<CachedImageRequest, byte[]> loadMedia(Context context, List<CachedImageRequest> requestList) {
        Map<CachedImageRequest, byte[]> hashmap = new HashMap<CachedImageRequest, byte[]>();
        EsAccount esaccount = EsAccountsData.getActiveAccount(context);
        if(esaccount != null) {
            Iterator<CachedImageRequest> iterator = requestList.iterator();
            while(iterator.hasNext()) {
                CachedImageRequest cachedimagerequest = iterator.next();
                byte[] abyte0 = getMedia(context, cachedimagerequest);
                if(abyte0 != null)
                    hashmap.put(cachedimagerequest, abyte0);
                else
                    ImageDownloader.downloadImage(context, esaccount, cachedimagerequest);
            }
        }
        return hashmap;
    }

    public static Bitmap obtainAvatar(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        Bitmap bitmap = obtainImage(context, esaccount, new AvatarImageRequest(s, s1, 2, EsAvatarData.getMediumAvatarSize(context)));
        Bitmap bitmap1;
        if(bitmap != null && flag)
        {
            bitmap1 = ImageUtils.getRoundedBitmap(context, bitmap);
            bitmap.recycle();
        } else
        {
            bitmap1 = bitmap;
        }
        return bitmap1;
    }

    public static Bitmap obtainAvatarForMultipleUsers(List list)
    {
    	if(null == list || list.isEmpty()) {
    		return null;
    	}
    	
    	int i;
        int j;
        int k;
        Bitmap bitmap1;
        Canvas canvas;
        Paint paint;
        i = list.size();
        j = ((Bitmap)list.get(0)).getWidth();
        k = ((Bitmap)list.get(0)).getHeight();
        bitmap1 = Bitmap.createBitmap(j, k, android.graphics.Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap1);
        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setStrokeWidth(2.0F);
        if(i == 1)
        {
            return (Bitmap)list.get(0);
        } else if(i == 2) {
        	Bitmap bitmap9 = obtainAvatarWithHalfHeight((Bitmap)list.get(0));
            Bitmap bitmap10 = obtainAvatarWithHalfHeight((Bitmap)list.get(1));
            canvas.drawBitmap(bitmap9, 0.0F, 0.0F, paint);
            canvas.drawBitmap(bitmap10, 0.0F, k / 2, paint);
            canvas.drawLine(0.0F, k / 2, j, k / 2, paint);
        } else if(i == 3) {
        	Bitmap bitmap6 = obtainAvatarWithHalfHeight((Bitmap)list.get(0));
            Bitmap bitmap7 = obtainAvatarWithHalfHeightAndHalfWidth((Bitmap)list.get(1));
            Bitmap bitmap8 = obtainAvatarWithHalfHeightAndHalfWidth((Bitmap)list.get(2));
            canvas.drawBitmap(bitmap6, 0.0F, 0.0F, paint);
            canvas.drawBitmap(bitmap7, 0.0F, k / 2, paint);
            canvas.drawBitmap(bitmap8, j / 2, k / 2, paint);
            canvas.drawLine(j / 2, k / 2, j / 2, k, paint);
            canvas.drawLine(0.0F, k / 2, j, k / 2, paint);
        } else if(i >= 4)
        {
            Bitmap bitmap2 = obtainAvatarWithHalfHeightAndHalfWidth((Bitmap)list.get(0));
            Bitmap bitmap3 = obtainAvatarWithHalfHeightAndHalfWidth((Bitmap)list.get(1));
            Bitmap bitmap4 = obtainAvatarWithHalfHeightAndHalfWidth((Bitmap)list.get(2));
            Bitmap bitmap5 = obtainAvatarWithHalfHeightAndHalfWidth((Bitmap)list.get(3));
            canvas.drawBitmap(bitmap2, 0.0F, 0.0F, paint);
            canvas.drawBitmap(bitmap3, j / 2, 0.0F, paint);
            canvas.drawBitmap(bitmap4, 0.0F, k / 2, paint);
            canvas.drawBitmap(bitmap5, j / 2, k / 2, paint);
            canvas.drawLine(j / 2, 0.0F, j / 2, k, paint);
            canvas.drawLine(0.0F, k / 2, j, k / 2, paint);
        }
        
        return bitmap1;
    }

    private static Bitmap obtainAvatarWithHalfHeight(Bitmap bitmap)
    {
        Bitmap bitmap1;
        if(bitmap == null)
        {
            bitmap1 = null;
        } else
        {
            int i = bitmap.getWidth();
            int j = bitmap.getHeight();
            bitmap1 = Bitmap.createBitmap(bitmap, 0, j / 4, i, j / 2);
        }
        return bitmap1;
    }

    private static Bitmap obtainAvatarWithHalfHeightAndHalfWidth(Bitmap bitmap)
    {
        Bitmap bitmap1;
        if(bitmap == null)
        {
            bitmap1 = null;
        } else
        {
            int i = bitmap.getWidth();
            int j = bitmap.getHeight();
            bitmap1 = Bitmap.createScaledBitmap(bitmap, i / 2, j / 2, false);
        }
        return bitmap1;
    }

    public static Bitmap obtainImage(Context context, EsAccount esaccount, CachedImageRequest cachedimagerequest)
    {
        byte abyte0[] = getMedia(context, cachedimagerequest);
        if(abyte0 == null)
        {
            DownloadImageOperation downloadimageoperation = new DownloadImageOperation(context, esaccount, cachedimagerequest, null, null);
            downloadimageoperation.start();
            abyte0 = downloadimageoperation.getImageBytes();
        }
        Bitmap bitmap = null;
        if(abyte0 != null)
            bitmap = ImageUtils.decodeByteArray(abyte0, 0, abyte0.length);
        return bitmap;
    }

    public static byte[] read(Context context, String s, String s1)
    {
        FileInputStream fileinputstream = null;
        long l = System.currentTimeMillis();
        
        try {
        	File file = getMediaCacheFile(context, s, s1);
        	long i = l - file.lastModified() - 10000L;
        	 if(i > 0)
                 file.setLastModified(l);
	        fileinputstream = new FileInputStream(file);
	        byte abyte0[];
	        int k;
	        int i1;
	        int j = (int)file.length();
	        abyte0 = new byte[j];
	        k = 0;
	        i1 = j;
	        
	        while(i1 > 0) {
	        	int j1;
	            j1 = fileinputstream.read(abyte0, k, i1);
	            if(j1 < 0)
	                throw new IOException();
	            k += j1;
	            i1 -= j1;
	        }
	        
	        return abyte0;
        } catch (FileNotFoundException filenotfoundexception1) {
        	// TODO
        	return null;
        } catch (IOException ioexception2) {
        	Log.e("ResourceCache", (new StringBuilder("Cannot read file from cache: file:")).append(s1).append(" not exists").toString(), ioexception2);
        	return null;
        } finally {
        	if(fileinputstream != null)
                try
                {
                	fileinputstream.close();
                }
                catch(IOException ioexception) { }
        }
    }

    public static void write(Context context, String s, String s1, byte abyte0[])
    {
        File file1;
        File file = new File(getMediaCacheDir(context), s);
        FileOutputStream fileoutputstream = null;
        if(!file.exists())
            try
            {
                file.mkdir();
            }
            catch(Exception exception)
            {
                Log.e("ResourceCache", (new StringBuilder("Cannot create cache directory: ")).append(file).toString(), exception);
            }
        file1 = new File(file, s1);
        if(abyte0 == null)
            return;
        try {
	        fileoutputstream = new FileOutputStream(file1);
	        fileoutputstream.write(abyte0);
	        fileoutputstream.flush();
        } catch (IOException e) {
        	 Log.e("ResourceCache", (new StringBuilder("Cannot write file to cache: ")).append(file1.getPath()).toString(), e);
        	 file1.delete();
        } finally {
        	if(null != fileoutputstream) {
        		try {
        			fileoutputstream.close();
        		} catch (IOException e) {
        			// 
        		}
        	}
        }
    }
    
	//===========================================================================
    //						Inner class
    //===========================================================================
	private static final class MediaCacheFile implements Comparable {

		File file;
		boolean recent;
		long size;
		long timestamp;
		
		public final int compareTo(Object obj) {
			MediaCacheFile mediacachefile = (MediaCacheFile) obj;
			int i;
			if (recent) {
				if (!mediacachefile.recent)
					i = 1;
				else
					i = (int) (timestamp - mediacachefile.timestamp);
			} else if (mediacachefile.recent)
				i = -1;
			else
				i = (int) (mediacachefile.size - size);
			return i;
		}

		public MediaCacheFile(File file1, long l) {
			file = file1;
			timestamp = file1.lastModified();
			size = file1.length();
			boolean flag;
			if (l - timestamp < 0x1b7740L)
				flag = true;
			else
				flag = false;
			recent = flag;
		}
	}
}
