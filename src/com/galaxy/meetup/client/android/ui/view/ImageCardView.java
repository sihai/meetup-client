/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.DbEmbedMedia;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class ImageCardView extends StreamCardView implements ClickableRect.ClickableRectListener {

	private static Bitmap sAlbumBitmap;
    private static boolean sImageCardViewInitialized;
    private static ImageResourceManager sImageResourceManager;
    private static Bitmap sPanoramaBitmap;
    private static Bitmap sVideoBitmap;
    protected DbEmbedMedia mDbEmbedMedia;
    protected Rect mDestRect;
    protected Resource mImageResource;
    private int mImageSizeCategory;
    protected MediaRef mMediaRef;
    protected Rect mSrcRect;
    
    public ImageCardView(Context context)
    {
        this(context, null);
    }

    public ImageCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        if(!sImageCardViewInitialized)
        {
            sImageCardViewInitialized = true;
            sImageResourceManager = ImageResourceManager.getInstance(context);
            android.content.res.Resources resources = context.getResources();
            sPanoramaBitmap = ImageUtils.decodeResource(resources, R.drawable.overlay_lightcycle);
            sVideoBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_overlay_play);
            sAlbumBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_overlay_album);
        }
        mDestRect = new Rect();
        mSrcRect = new Rect();
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        Bitmap bitmap;
        boolean flag;
        Bitmap bitmap1;
        if(mImageResource == null)
            bitmap = null;
        else
            bitmap = (Bitmap)mImageResource.getResource();
        if(bitmap != null)
            flag = true;
        else
            flag = false;
        drawMediaTopAreaStage(canvas, k, l, flag, mDestRect, sMediaTopAreaBackgroundPaint);
        if(bitmap != null)
        {
            if(mSrcRect.isEmpty())
                createSourceRectForMediaImage(mSrcRect, bitmap, k, l);
            canvas.drawBitmap(bitmap, mSrcRect, mDestRect, sResizePaint);
        }
        if(mDbEmbedMedia.isAlbum())
            bitmap1 = sAlbumBitmap;
        else
        if(mDbEmbedMedia.isVideo())
            bitmap1 = sVideoBitmap;
        else
        if(mDbEmbedMedia.isPanorama())
            bitmap1 = sPanoramaBitmap;
        else
            bitmap1 = null;
        if(bitmap1 != null)
            canvas.drawBitmap(bitmap1, mDestRect.left + (mDestRect.width() - bitmap1.getWidth()) / 2, mDestRect.top + (mDestRect.height() - bitmap1.getHeight()) / 2, null);
        drawMediaTopAreaShadow(canvas, k, l);
        drawTagBarIconAndBackground(canvas, i, j);
        drawPlusOneBar(canvas);
        drawMediaBottomArea(canvas, i, k, l);
        drawCornerIcon(canvas);
        return l;
    }

    public final String getAlbumId()
    {
        return mDbEmbedMedia.getAlbumId();
    }

    public final int getDesiredHeight()
    {
        return mDbEmbedMedia.getHeight();
    }

    public final int getDesiredWidth()
    {
        return mDbEmbedMedia.getWidth();
    }

    public final String getMediaLinkUrl()
    {
        return mDbEmbedMedia.getContentUrl();
    }

    public final MediaRef getMediaRef()
    {
        return mMediaRef;
    }

    public final void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamCardView.ViewedListener viewedlistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener)
    {
        super.init(cursor, i, j, onclicklistener, itemclicklistener, viewedlistener, streamplusbarclicklistener, streammediaclicklistener);
        byte abyte0[] = cursor.getBlob(22);
        if(abyte0 != null)
        {
            mDbEmbedMedia = DbEmbedMedia.deserialize(abyte0);
            String s = mDbEmbedMedia.getImageUrl();
            String s1 = mDbEmbedMedia.getVideoUrl();
            if(mDbEmbedMedia.isVideo())
            {
                String s2 = ImageUtils.rewriteYoutubeMediaUrl(s1);
                if(!TextUtils.equals(s1, s2))
                    s = s2;
            }
            Uri uri;
            if(mDbEmbedMedia.isVideo())
                uri = Uri.parse(s1);
            else
                uri = null;
            mMediaRef = new MediaRef(mDbEmbedMedia.getOwnerId(), mDbEmbedMedia.getPhotoId(), s, uri, mDbEmbedMedia.getMediaType());
            mImageSizeCategory = 3;
            if(!mDbEmbedMedia.isVideo())
                if(mDisplaySizeType == 2)
                    mImageSizeCategory = 5;
                else
                if(mDisplaySizeType == 1)
                    mImageSizeCategory = 4;
            if(mTag == null && !TextUtils.isEmpty(mDbEmbedMedia.getTitle()))
            {
                mTag = mDbEmbedMedia.getTitle().toUpperCase();
                Bitmap bitmap;
                if(mDbEmbedMedia.isVideo())
                    bitmap = sTagVideoBitmaps[0];
                else
                    bitmap = sTagAlbumBitmaps[0];
                mTagIcon = bitmap;
            }
        }
    }

    public final boolean isAlbum()
    {
        return mDbEmbedMedia.isAlbum();
    }

    protected final int layoutElements(int i, int j, int k, int l)
    {
        int i1 = k + sXDoublePadding;
        int j1 = (int)((float)(l + sYDoublePadding) * getMediaHeightPercentage());
        mBackgroundRect.set(0, j1, getMeasuredWidth(), getMeasuredHeight());
        createTagBar(i, j, k);
        createPlusOneBar(i, (j1 + sTopBorderPadding) - sYPadding, k);
        createMediaBottomArea(i, j, k, l);
        mSrcRect.setEmpty();
        mDestRect.set(sLeftBorderPadding, sTopBorderPadding, i1 + sLeftBorderPadding, j1 + sTopBorderPadding);
        return l;
    }

    protected final void onBindResources()
    {
        super.onBindResources();
        if(mMediaRef != null)
            mImageResource = sImageResourceManager.getMedia(mMediaRef, mImageSizeCategory, this);
    }

    public final void onClickableRectClick()
    {
        if(mStreamMediaClickListener != null)
            mStreamMediaClickListener.onMediaClicked(mDbEmbedMedia.getAlbumId(), mDbEmbedMedia.getOwnerId(), mMediaRef, mDbEmbedMedia.isVideo(), this);
    }

    public void onRecycle()
    {
        super.onRecycle();
        mDbEmbedMedia = null;
        mMediaRef = null;
        mSrcRect.setEmpty();
        mDestRect.setEmpty();
    }

    protected final void onUnbindResources()
    {
        super.onUnbindResources();
        if(mImageResource != null)
        {
            mImageResource.unregister(this);
            mImageResource = null;
        }
    }

}
