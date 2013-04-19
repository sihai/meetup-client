/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.galaxy.meetup.client.android.EsApplication;
import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ImageUtils {

	private static final boolean USE_LOW_RES_IMAGES;
    private static Bitmap sAvatarOverlayBitmap;
    private static final Rect sAvatarOverlayRect = new Rect();
    private static final Paint sCropPaint;
    private static final Paint sMaskPaint;
    private static int sMicroKindMaxDimension = 0;
    private static int sMiniKindMaxDimension = 0;
    private static final Paint sResizePaint = new Paint(2);
    private static Bitmap sRoundMask;
    private static List sRoundMasks = new ArrayList();
    
    static {
        Paint paint = new Paint();
        sCropPaint = paint;
        paint.setAntiAlias(true);
        sCropPaint.setFilterBitmap(true);
        sCropPaint.setDither(true);
        Paint paint1 = new Paint(1);
        sMaskPaint = paint1;
        paint1.setColor(0xff000000);
        sMaskPaint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));
        if(android.os.Build.VERSION.SDK_INT >= 11)
            USE_LOW_RES_IMAGES = false;
        else
        if((long)EsApplication.sMemoryClass >= 24L)
            USE_LOW_RES_IMAGES = false;
        else
            USE_LOW_RES_IMAGES = true;
    }
    
    public static Object decodeMedia(byte bytes[]) {
		try {
			if (GifImage.isGif(bytes)) {
				return new GifDrawable(new GifImage(bytes));
			} else {
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			}
		} catch (OutOfMemoryError outofmemoryerror) {
			Log.e("ImageUtils", "ImageUtils#decodeMedia(byte[]) threw an OOME",
					outofmemoryerror);
		}
		return null;
    }
    
    public static Bitmap decodeResource(Resources resources, int i) {
    	try {
    		return BitmapFactory.decodeResource(resources, i);
    	} catch (OutOfMemoryError outofmemoryerror) {
    		Log.e("ImageUtils", "ImageUtils#decodeResource(Resources, int) threw an OOME", outofmemoryerror);
    	}
    	return null;
    }
    
    private static Bitmap decodeByteArray(byte abyte0[], int i, int j, android.graphics.BitmapFactory.Options options) {
    	
    	try {
    		return BitmapFactory.decodeByteArray(abyte0, 0, j, options);
    	} catch (OutOfMemoryError outofmemoryerror) {
    		Log.e("ImageUtils", "ImageUtils#decodeByteArray(byte[], int, int, Options) threw an OOME", outofmemoryerror);
    	}
    	return null;
    	
    }
    
    public static Bitmap decodeByteArray(byte abyte0[], int i, int j) {
    	try {
    		return BitmapFactory.decodeByteArray(abyte0, 0, j);
    	} catch (OutOfMemoryError outofmemoryerror) {
    		Log.e("ImageUtils", "ImageUtils#decodeByteArray(byte[], int, int) threw an OOME", outofmemoryerror);
    	}
    	return null;
    }

    public static String getCroppedAndResizedUrl(int i, String s) {
        String s1;
        if(FIFEUtil.isFifeHostedUrl(s))
            s1 = FIFEUtil.setImageUrlSize(i, s, true);
        else
            s1 = ImageProxyUtil.setImageUrlSize(i, s);
        return s1;
    }
    
    public static String getCenterCroppedAndResizedUrl(int i, int j, String s) {
        String s1;
        if(s == null)
            s1 = null;
        else
        if(FIFEUtil.isFifeHostedUrl(s)) {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("w").append(i).append("-h").append(j).append("-d-p");
            s1 = FIFEUtil.setImageUrlOptions(stringbuilder.toString(), s).toString();
        } else {
            s1 = ImageProxyUtil.setImageUrlSize(i, j, s);
        }
        return s1;
    }

    private static Bitmap decodeStream(InputStream inputstream, Rect rect, BitmapFactory.Options options) {
    	try {
    		return BitmapFactory.decodeStream(inputstream, null, options);
    	} catch (OutOfMemoryError outofmemoryerror) {
    		Log.e("ImageUtils", "ImageUtils#decodeStream(InputStream, Rect, Options) threw an OOME", outofmemoryerror);
    	}
    	return null;
    }
    
	public static byte[] compressBitmap(Bitmap bitmap) {
        return compressBitmap(bitmap, 90, true);
    }

    public static byte[] compressBitmap(Bitmap bitmap, int i, boolean flag) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      
		try {
			bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, i, bytearrayoutputstream);
			bytearrayoutputstream.flush();
			bytearrayoutputstream.close();
		}
        catch(IOException e) { }
        if(flag)
            bitmap.recycle();
        byte[] abyte0 = bytearrayoutputstream.toByteArray();
        if(EsLog.isLoggable("ImageUtils", 3))
            Log.d("ImageUtils", (new StringBuilder("compressBitmap: Image size: ")).append(abyte0.length).toString());
        return abyte0;
    }
    
    public static Dialog createInsertCameraPhotoDialog(Context context) {
        ProgressDialog progressdialog = new ProgressDialog(context);
        progressdialog.setProgressStyle(0);
        progressdialog.setCancelable(false);
        progressdialog.setMessage(context.getString(R.string.dialog_inserting_camera_photo));
        return progressdialog;
    }
    
	public static int getMaxThumbnailDimension(Context context, int i) {
		switch (i) {
			case 2:
			default:
				if (EsLog.isLoggable("ImageUtils", 3))
					Log.d("ImageUtils", (new StringBuilder("illegal kind="))
							.append(i).append(" specified; using MINI_KIND")
							.toString());
				return getThumbnailSize(context, 1);
			case 1:
				return getThumbnailSize(context, 1);
			case 3:
				return getThumbnailSize(context, 3);
		}
	}

    private static int getThumbnailSize(Context context, int i) {
    	switch (i) {
    		case 3:
    			if(sMicroKindMaxDimension == 0)
    	            sMicroKindMaxDimension = context.getResources().getDimensionPixelSize(R.dimen.micro_kind_max_dimension);
    			break;
    		default:
    			if(sMiniKindMaxDimension == 0)
    	            sMiniKindMaxDimension = context.getResources().getDimensionPixelSize(R.dimen.mini_kind_max_dimension);
    			break;
    	}
    	
    	return sMicroKindMaxDimension;
    }
    
    
	private static Point getImageBounds(byte abyte0[]) {
		android.graphics.BitmapFactory.Options options;
		ByteArrayInputStream bytearrayinputstream;
		options = new android.graphics.BitmapFactory.Options();
		bytearrayinputstream = new ByteArrayInputStream(abyte0);
		Point point;
		options.inJustDecodeBounds = true;
		decodeStream(bytearrayinputstream, null, options);
		point = new Point(options.outWidth, options.outHeight);
		try {
			bytearrayinputstream.close();
		} catch (IOException ioexception1) {
		}
		return point;
	}
	
	private static Point getImageBounds(ContentResolver contentresolver, Uri uri)
			throws IOException {
		android.graphics.BitmapFactory.Options options;
		InputStream inputstream;
		options = new android.graphics.BitmapFactory.Options();
		inputstream = null;
		Point point;
		options.inJustDecodeBounds = true;
		inputstream = contentresolver.openInputStream(uri);
		decodeStream(inputstream, null, options);
		point = new Point(options.outWidth, options.outHeight);
		try {
			inputstream.close();
		} catch (IOException ioexception1) {
		}
		return point;
	}

	public static Bitmap rotateBitmap(ContentResolver contentresolver, Uri uri, Bitmap bitmap)
    {
        if(bitmap != null && (MediaStoreUtils.isMediaStoreUri(uri) || isFileUri(uri)))
        {
            String s;
            int i;
            if(isFileUri(uri))
                s = uri.getPath();
            else
                s = MediaStoreUtils.getFilePath(contentresolver, uri);
            i = getExifRotation(s);
            if(i != 0)
                bitmap = rotateBitmap(bitmap, i);
        }
        return bitmap;
    }
	
	public static String getMimeType(ContentResolver contentresolver, Uri uri) {
		String s = null;
		try {
	        s = safeGetMimeType(contentresolver, uri);
	        if(TextUtils.isEmpty(s))
	        	s = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		} catch (Exception e) {
			if(EsLog.isLoggable("ImageUtils", 5))
		        Log.w("ImageUtils", (new StringBuilder("getMimeType failed for uri=")).append(uri).toString(), e);
		}
		return s;
	}
	
	public static String getResizedUrl(int i, int j, String s)
    {
        String s1;
        if(FIFEUtil.isFifeHostedUrl(s))
            s1 = FIFEUtil.setImageUrlSize(i, j, s, false, false);
        else
            s1 = ImageProxyUtil.setImageUrlSize(i, j, s);
        return s1;
    }
	
	public static Bitmap resizeToSquareBitmap(Bitmap bitmap, int i)
    {
        return resizeToSquareBitmap(bitmap, i, 0);
    }

    public static Bitmap resizeToSquareBitmap(Bitmap bitmap, int i, int j) {
        if(null == bitmap) 
        	return null;
        
        Bitmap bitmap1 = null;
        
        try {
	        if(EsLog.isLoggable("ImageUtils", 3))
	            Log.d("ImageUtils", (new StringBuilder("resizeToSquareBitmap: Input: ")).append(bitmap.getWidth()).append("x").append(bitmap.getHeight()).append(", output:").append(i).append("x").append(i).toString());
	        Bitmap bitmap2 = Bitmap.createBitmap(i, i, android.graphics.Bitmap.Config.ARGB_8888);
	        bitmap1 = bitmap2;
	        if(bitmap1 == null)
	        {
	            bitmap1 = null;
	        } else
	        {
	            Canvas canvas = new Canvas(bitmap1);
	            if(j != 0)
	                canvas.drawColor(j);
	            if(bitmap.getWidth() != i || bitmap.getHeight() != i)
	            {
	                Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	                Rect rect1 = new Rect(0, 0, i, i);
	                synchronized(sResizePaint)
	                {
	                    canvas.drawBitmap(bitmap, rect, rect1, sResizePaint);
	                }
	            } else
	            {
	                canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
	            }
	        }
        } catch (OutOfMemoryError outofmemoryerror) {
        	Log.w("ImageUtils", (new StringBuilder("resizeToSquareBitmap OutOfMemoryError for image size: ")).append(i).toString(), outofmemoryerror);
            bitmap1 = null;
        }
        
        return bitmap1;
    }

    public static byte[] resizeToSquareBitmap(byte abyte0[], int i) {
        return resizeToSquareBitmap(abyte0, i, 0);
    }

    public static byte[] resizeToSquareBitmap(byte abyte0[], int i, int j) {
        Bitmap bitmap = decodeAndScaleBitmap(abyte0, i, j);
        byte abyte1[];
        if(bitmap == null)
        {
            abyte1 = null;
        } else
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, bytearrayoutputstream);
            bitmap.recycle();
            abyte1 = bytearrayoutputstream.toByteArray();
        }
        return abyte1;
    }
	
	public static Bitmap resizeAndCropBitmap(Bitmap bitmap, int i, int j) {
		if(null == bitmap) {
			return null;
		}
		if(EsLog.isLoggable("ImageUtils", 3))
            Log.d("ImageUtils", (new StringBuilder("resizeAndCropBitmap: Input: ")).append(bitmap.getWidth()).append("x").append(bitmap.getHeight()).append(", output:").append(i).append("x").append(j).toString());
        if(bitmap.getWidth() == i && bitmap.getHeight() == j) {
        	return bitmap;
        }
        
        try {
	        Bitmap bitmap1 = Bitmap.createBitmap(i, j, android.graphics.Bitmap.Config.ARGB_8888);
	        if(bitmap1 == null)
	        {
	            return bitmap;
	        }
	        
	        Canvas canvas = new Canvas(bitmap1);
	        int k = bitmap.getWidth();
	        int l = bitmap.getHeight();
	        int i1;
	        int j1;
	        Rect rect;
	        Rect rect1;
	        if(j * bitmap.getWidth() > i * bitmap.getHeight())
	            k = (i * bitmap.getHeight()) / j;
	        else
	            l = (j * bitmap.getWidth()) / i;
	        i1 = (bitmap.getWidth() - k) / 2;
	        j1 = (bitmap.getHeight() - l) / 2;
	        rect = new Rect(i1, j1, i1 + k, j1 + l);
	        rect1 = new Rect(0, 0, i, j);
	        synchronized(sResizePaint)
	        {
	            canvas.drawBitmap(bitmap, rect, rect1, sResizePaint);
	        }
	        bitmap = bitmap1;
	        return bitmap;
        } catch (Throwable t) {
        	Log.w("ImageUtils", (new StringBuilder("resizeAndCropBitmap failed for image size: ")).append(i).append(" x ").append(j).toString(), t);
        	return bitmap;
        }
    }
	
	private static Bitmap decodeAndScaleBitmap(byte abyte0[], int i, int j)
    {
        if(null == abyte0) {
        	return null;
        }
        
        Bitmap bitmap1 = null;
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeByteArray(abyte0, 0, abyte0.length, options);
        int k = options.outWidth;
        int l = options.outHeight;
        if(EsLog.isLoggable("ImageUtils", 3))
            Log.d("ImageUtils", (new StringBuilder("resizeToSquareBitmap: Input: ")).append(k).append("x").append(l).append(", resize to: ").append(i).toString());
        int i1 = Math.min(k / i, l / i);
        Bitmap bitmap;
        if(i1 > 1)
        {
            android.graphics.BitmapFactory.Options options1 = new android.graphics.BitmapFactory.Options();
            options1.inSampleSize = i1;
            bitmap = decodeByteArray(abyte0, 0, abyte0.length, options1);
        } else
        {
            bitmap = decodeByteArray(abyte0, 0, abyte0.length);
        }
        if(bitmap == null)
        {
            bitmap1 = null;
        } else
        {
            bitmap1 = resizeToSquareBitmap(bitmap, i, j);
            bitmap.recycle();
            if(bitmap1 == null)
                bitmap1 = null;
        }
        return bitmap1;
    }
	
	public static Bitmap resizeBitmap(byte abyte0[], int i, int j)
    {
		try {
	        int k;
	        int l;
	        Bitmap bitmap;
	        Bitmap bitmap1;
	        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        decodeByteArray(abyte0, 0, abyte0.length, options);
	        k = options.outWidth;
	        l = options.outHeight;
	        if(EsLog.isLoggable("ImageUtils", 3))
	            Log.d("ImageUtils", (new StringBuilder("resizeBitmap: Input: ")).append(k).append("x").append(l).append(", resize to: ").append(i).append("x").append(j).toString());
	        if(k > i || l > j)
	        {
	            float f = (k * i) / l;
	            float f1 = (l * j) / k;
	            if((float)k / f > 1.0F || (float)l / f1 > 1.0F)
	            {
	                android.graphics.BitmapFactory.Options options1 = new android.graphics.BitmapFactory.Options();
	                options1.inSampleSize = Math.max(k / (int)f, l / (int)f1);
	                bitmap = decodeByteArray(abyte0, 0, abyte0.length, options1);
	            } else
	            {
	                bitmap = decodeByteArray(abyte0, 0, abyte0.length);
	            }
	        } else
	        {
	            bitmap = decodeByteArray(abyte0, 0, abyte0.length);
	        }
	        
	        if(null == bitmap) {
	        	return null;
	        }
	        
	        Bitmap bitmap2 = Bitmap.createBitmap(i, j, android.graphics.Bitmap.Config.ARGB_8888);
	        bitmap1 = bitmap2;
	        
	        if(bitmap1 == null)
	        {
	            bitmap.recycle();
	            bitmap1 = null;
	        } else
	        {
	            int i1 = bitmap.getWidth();
	            int j1 = bitmap.getHeight();
	            int k1 = i1;
	            int l1 = j1;
	            int i2;
	            int j2;
	            Rect rect;
	            Rect rect1;
	            Canvas canvas;
	            if(k * j > i * l)
	                k1 = (i * bitmap.getHeight()) / j;
	            else
	                l1 = (j * bitmap.getWidth()) / i;
	            if(EsLog.isLoggable("ImageUtils", 3))
	                Log.d("ImageUtils", (new StringBuilder("resizeBitmap: cropped: ")).append(k1).append("x").append(l1).toString());
	            i2 = (i1 - k1) / 2;
	            j2 = (j1 - l1) / 2;
	            rect = new Rect(i2, j2, i2 + k1, j2 + l1);
	            rect1 = new Rect(0, 0, i, j);
	            canvas = new Canvas(bitmap1);
	            canvas.drawColor(0xffe0e0e0);
	            synchronized(sResizePaint)
	            {
	                Paint paint1 = sResizePaint;
	                canvas.drawBitmap(bitmap, rect, rect1, paint1);
	            }
	            bitmap.recycle();
	        }
	        
	        return bitmap1;
		} catch (OutOfMemoryError outofmemoryerror) {
			Log.w("ImageUtils", (new StringBuilder("resizeBitmap OutOfMemoryError for image size: ")).append(i).append(" x ").append(j).toString(), outofmemoryerror);
		}
		
		return null;
    }
	
	public static boolean isImageMimeType(String s) {
		boolean flag;
		if (s != null && s.startsWith("image/"))
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static boolean isVideoMimeType(String s) {
		boolean flag;
		if (s != null && s.startsWith("video/"))
			flag = true;
		else
			flag = false;
		return flag;
	}
	
	private static Bitmap rotateBitmap(Bitmap bitmap, int i) {
        Matrix matrix;
        int j;
        int k;
        Bitmap bitmap1;
        if(i == 0 || bitmap == null) {
            return bitmap;
        }
        matrix = new Matrix();
        j = bitmap.getWidth();
        k = bitmap.getHeight();
        matrix.setRotate(i, (float)j / 2.0F, (float)k / 2.0F);
        bitmap1 = bitmap;
        Bitmap bitmap2;
        bitmap2 = Bitmap.createBitmap(bitmap1, 0, 0, j, k, matrix, true);
        if(bitmap == bitmap2)
            return bitmap;
        bitmap.recycle();
        bitmap = bitmap2;
        return bitmap;
    }
	
	private static int getExifRotation(String s)
    {
		
		try {
			ExifInterface exifinterface = new ExifInterface(s);
			switch (exifinterface.getAttributeInt("Orientation", 1)) {
			case 2: // '\002'
			case 4: // '\004'
			case 5: // '\005'
			case 7: // '\007'
			default:
				return 0;
			case 1: // '\001'
				return 0;
			case 6: // '\006'
				return 90;
			case 3: // '\003'
				return 180;
			case 8: // '\b'
				return 270;
			}
		} catch(IOException e) {
			 Log.w("ImageUtils", (new StringBuilder("failed to create ExifInterface for ")).append(s).toString());
		}
		return 0;
    }
	
	private static String safeGetMimeType(ContentResolver contentresolver, Uri uri) {
		try {
			return contentresolver.getType(uri);
		} catch (Exception e) {
			Log.w("ImageUtils", (new StringBuilder("safeGetMimeType failed for uri=")).append(uri).toString(), e);
		}
		return null;
    }
	
	public static boolean isFileUri(Uri uri) {
        boolean flag;
        if(uri != null && "file".equals(uri.getScheme()))
            flag = true;
        else
            flag = false;
        return flag;
    }
	
	public static String rewriteYoutubeMediaUrl(String s) {
        if(s.startsWith("http://www.youtube.com/watch?v=")) {
            String s1 = s.substring(31);
            int i = s1.indexOf("&");
            if(i >= 0)
                s1 = s1.substring(0, i);
            s = (new StringBuilder("https://img.youtube.com/vi/")).append(s1).append("/0.jpg").toString();
        }
        return s;
    }
	
	public static Bitmap createVideoThumbnail(Context context, Uri uri, int i)
    {
        MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
        
        try {
        	mediametadataretriever.setDataSource(context, uri);
        	Bitmap bitmap = mediametadataretriever.getFrameAtTime(-1L);
        	if(null != bitmap)
            {
                int j = getThumbnailSize(context, i);
                return ThumbnailUtils.extractThumbnail(bitmap, j, j, 2);
            }
        	return bitmap;
        } catch (Throwable t) {
        	// TODO log
        	return null;
        } finally {
        	try
            {
                mediametadataretriever.release();
            }
            catch(RuntimeException e) { }
        }
    }
	
	public static Bitmap createLocalBitmap(ContentResolver contentresolver, Uri uri, int i) throws IOException {
        InputStream inputstream = null;
        android.graphics.BitmapFactory.Options options = null;
        try {
        	Point point = getImageBounds(contentresolver, uri);
	        options = new android.graphics.BitmapFactory.Options();
	        if(i > 0) {
		        options.inSampleSize = Math.max(point.x / i, point.y / i);
	        }
	        inputstream = contentresolver.openInputStream(uri);
	        return rotateBitmap(contentresolver, uri, decodeStream(inputstream, null, options));
        } finally {
        	if(null != inputstream) {
        		try
	            {
	                inputstream.close();
	            }
	            catch(IOException ioexception1) { }
        	}
        }
	}
	
	public static String insertCameraPhoto(Context context, String s) throws FileNotFoundException {
		File file = new File(Environment.getExternalStorageDirectory(), s);
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        String s1 = (new SimpleDateFormat(context.getString(R.string.image_file_name_format))).format(date);
        ContentResolver contentresolver = context.getContentResolver();
        ContentValues contentvalues = new ContentValues(5);
        int i = getExifRotation(file.getAbsolutePath());
        contentvalues.put("title", s1);
        contentvalues.put("_display_name", (new StringBuilder()).append(s1).append(".jpg").toString());
        contentvalues.put("datetaken", Long.valueOf(l));
        contentvalues.put("mime_type", "image/jpeg");
        contentvalues.put("orientation", Integer.valueOf(i));
        Uri uri = contentresolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentvalues);
		
		OutputStream outputstream = null;
        FileInputStream fileinputstream = null;
		fileinputstream = new FileInputStream(file);
		byte abyte0[] = new byte[10240];
		try {
			try {
				outputstream = contentresolver.openOutputStream(uri);
				do
				{
					int j = fileinputstream.read(abyte0);
					if(j == -1)
						break;
					outputstream.write(abyte0, 0, j);
				} while(true);
				return uri.toString();
			} catch (Exception e) {
				// TODO
			} finally {
				if(null != outputstream) {
	        		try {
	        			outputstream.close();
	        		} catch (IOException e) {
	        			
	        		}
	        	}
	        	if(null != fileinputstream) {
	        		try {
	        			fileinputstream.close();
	        		} catch (IOException e) {
	        			
	        		}
	        	}
			}
			
			uri = contentresolver.insert(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI, contentvalues);
			
			try {
				outputstream = contentresolver.openOutputStream(uri);
				do
				{
					int j = fileinputstream.read(abyte0);
					if(j == -1)
						break;
					outputstream.write(abyte0, 0, j);
				} while(true);
				return uri.toString();
			} catch (Exception e) {
				// TODO
			} finally {
				if(null != outputstream) {
	        		try {
	        			outputstream.close();
	        		} catch (IOException e) {
	        			
	        		}
	        	}
	        	if(null != fileinputstream) {
	        		try {
	        			fileinputstream.close();
	        		} catch (IOException e) {
	        			
	        		}
	        	}
			}
			
			uri = contentresolver.insert(MediaStoreUtils.PHONE_STORAGE_IMAGES_URI, contentvalues);
			try {
				outputstream = contentresolver.openOutputStream(uri);
				do
				{
					int j = fileinputstream.read(abyte0);
					if(j == -1)
						break;
					outputstream.write(abyte0, 0, j);
				} while(true);
				return uri.toString();
			} catch (Exception e) {
				// TODO
				Log.e("ImageUtils", "Failed to save image", e);
				return null;
			} finally {
				if(null != outputstream) {
	        		try {
	        			outputstream.close();
	        		} catch (IOException e) {
	        			
	        		}
	        	}
	        	if(null != fileinputstream) {
	        		try {
	        			fileinputstream.close();
	        		} catch (IOException e) {
	        			
	        		}
	        	}
			}
		} finally {
			if(MediaStoreUtils.isExternalMediaStoreUri(uri)) {
				Bitmap bitmap = MediaStoreUtils.getThumbnail(context, uri, 1);
				if(bitmap != null)
					bitmap.recycle();
		    }
		    file.delete();
		}
    }

	public static interface InsertCameraPhotoDialogDisplayer {

		void hideInsertCameraPhotoDialog();

		void showInsertCameraPhotoDialog();
	}
	
	public static Bitmap getRoundedBitmap(Context context, Bitmap bitmap) {
		
        if(null == bitmap){
        	return null;
        }
        
        int i;
        int j;
        Bitmap bitmap1;
        i = bitmap.getWidth();
        j = bitmap.getHeight();
        bitmap1 = getRoundMask(context, i);
        if(bitmap1 == null)
        {
            return null;
        }
        
        try {
	        Bitmap bitmap2 = Bitmap.createBitmap(i, j, android.graphics.Bitmap.Config.ARGB_8888);
	        if(sAvatarOverlayBitmap == null)
	            sAvatarOverlayBitmap = decodeResource(context.getApplicationContext().getResources(), R.drawable.bg_taco_avatar);
	        sAvatarOverlayRect.set(0, 0, i, j);
	        Canvas canvas = new Canvas(bitmap2);
	        canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
	        synchronized(sMaskPaint)
	        {
	            canvas.drawBitmap(bitmap1, 0.0F, 0.0F, sMaskPaint);
	        }
	        synchronized(sResizePaint)
	        {
	            canvas.drawBitmap(sAvatarOverlayBitmap, null, sAvatarOverlayRect, sResizePaint);
	        }
	        return bitmap2;
        } catch (OutOfMemoryError outofmemoryerror) {
        	Log.w("ImageUtils", (new StringBuilder("roundedBitmap OutOfMemoryError for image size: ")).append(i).append("x").append(j).toString());
        	return null;
        }
    }
	
	private static Bitmap getRoundMask(Context context, int i) {
		
        int j = sRoundMasks.size();
        
        for(int k = 0; k < j; k++) {
        	RoundMask roundmask1 = (RoundMask)sRoundMasks.get(k);
        	if(roundmask1.size == i) {
        		return roundmask1.bitmap;
        	}
        }
        
        try {
	        Bitmap bitmap = null;
	        if(sRoundMask == null)
	            sRoundMask = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.round_mask)).getBitmap();
	        Bitmap bitmap1 = Bitmap.createBitmap(i, i, android.graphics.Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(bitmap1);
	        int l = sRoundMask.getWidth();
	        OutOfMemoryError outofmemoryerror;
	        synchronized(sResizePaint)
	        {
	            canvas.drawBitmap(sRoundMask, new Rect(0, 0, l, l), new Rect(0, 0, i, i), sResizePaint);
	        }
	        RoundMask roundmask = new RoundMask();
	        roundmask.size = i;
	        roundmask.bitmap = bitmap1;
	        sRoundMasks.add(roundmask);
	        return roundmask.bitmap;
        } catch (OutOfMemoryError outofmemoryerror) {
        	Log.w("ImageUtils", (new StringBuilder("getRoundMask OutOfMemoryError for image size: ")).append(i).toString());
        	return null;
        }
    }
	
	public static byte[] getRoundedBitmap(Context context, byte abyte0[])
    {
        byte abyte1[] = null;
        Bitmap bitmap = decodeByteArray(abyte0, 0, abyte0.length);
        if(null == bitmap) {
        	return null;
        }
        
        Bitmap bitmap1 = getRoundedBitmap(context, bitmap);
        bitmap.recycle();
        if(bitmap1 != null)
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            bitmap1.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
            bitmap1.recycle();
            abyte1 = bytearrayoutputstream.toByteArray();
        }
        return abyte1;
    }
	
	private static final class RoundMask {

		Bitmap bitmap;
		int size;

		private RoundMask() {
		}
	}
}
