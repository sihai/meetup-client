/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.graphics.Bitmap;
import android.view.View;

import com.galaxy.meetup.client.android.content.cache.ImageRequest;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageCache.ImageConsumer;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class MediaImage implements ImageConsumer {

	private static ImageCache sImageCache;
    private Bitmap mBitmap;
    private boolean mInvalidated;
    private final int mPostHeight;
    private final int mPostWidth;
    private final ImageRequest mRequest;
    private final View mView;
    
    public MediaImage(View view, ImageRequest imagerequest)
    {
        this(view, imagerequest, 0, 0);
    }

    private MediaImage(View view, ImageRequest imagerequest, int i, int j)
    {
        mView = view;
        mRequest = imagerequest;
        if(sImageCache == null)
            sImageCache = ImageCache.getInstance(view.getContext());
        mPostWidth = 0;
        mPostHeight = 0;
    }

    public final Bitmap getBitmap()
    {
        return mBitmap;
    }

    public final void invalidate()
    {
        mInvalidated = true;
    }

    public final void load()
    {
        if(mRequest != null)
            sImageCache.loadImage(this, mRequest);
    }

    public final void refreshIfInvalidated()
    {
        if(mInvalidated)
        {
            mInvalidated = false;
            if(mRequest != null)
                sImageCache.refreshImage(this, mRequest);
        }
    }

	public final void setBitmap(Bitmap bitmap, boolean flag) {
		if (bitmap != null && mPostWidth != 0 && mPostHeight != 0) {
			mBitmap = ImageUtils.resizeAndCropBitmap(bitmap, mPostWidth,
					mPostHeight);
			if (mBitmap != null)
				return;
		}
		mBitmap = bitmap;
		mView.invalidate();
	}

}
