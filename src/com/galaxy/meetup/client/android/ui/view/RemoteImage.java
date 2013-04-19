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

/**
 * 
 * @author sihai
 *
 */
public class RemoteImage implements ImageConsumer {

	private static ImageCache sImageCache;
    private Bitmap mBitmap;
    private boolean mInvalidated;
    private boolean mLoaded;
    private final ImageRequest mRequest;
    private final View mView;
    
    public RemoteImage(View view, ImageRequest imagerequest)
    {
        mView = view;
        mRequest = imagerequest;
        if(sImageCache == null)
            sImageCache = ImageCache.getInstance(view.getContext());
    }

    public final Bitmap getBitmap()
    {
        return mBitmap;
    }

    public final ImageRequest getRequest()
    {
        return mRequest;
    }

    public final void invalidate()
    {
        mInvalidated = true;
        mView.invalidate();
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

    public final void setBitmap(Bitmap bitmap, boolean flag)
    {
        mBitmap = bitmap;
        boolean flag1;
        if(!flag)
            flag1 = true;
        else
            flag1 = false;
        mLoaded = flag1;
        mView.invalidate();
    }
}
