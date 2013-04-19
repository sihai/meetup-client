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
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.EsApplication;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.DbEmbedEmotishare;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.util.GifDrawable;
import com.galaxy.meetup.client.util.GifImage;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class EmotiShareCardView extends StreamCardView {

	private static boolean sImageCardViewInitialized;
    private static ImageResourceManager sImageResourceManager;
    private Drawable mAnimatedDrawable;
    protected Resource mAnimatedImageResource;
    protected DbEmbedEmotishare mDbEmbedEmotiShare;
    protected Rect mDestRect;
    private RectF mDestRectF;
    private boolean mIsShowingBitmap;
    private boolean mIsShowingDrawable;
    private Matrix mMatrix;
    private Matrix mMatrixInverse;
    protected MediaRef mMediaRef;
    protected Rect mSrcRect;
    private RectF mSrcRectF;
    protected Resource mStaticImageResource;
    
    public EmotiShareCardView(Context context)
    {
        this(context, null);
    }

    public EmotiShareCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mSrcRect = new Rect();
        mDestRect = new Rect();
        mDestRectF = new RectF();
        mSrcRectF = new RectF();
        mMatrix = new Matrix();
        mMatrixInverse = new Matrix();
        if(!sImageCardViewInitialized)
        {
            sImageCardViewInitialized = true;
            sImageResourceManager = ImageResourceManager.getInstance(context);
        }
    }

    private boolean hasBitmap(Resource resource)
    {
        boolean flag;
        if(hasImage(resource) && (resource.getResource() instanceof Bitmap))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private static boolean hasImage(Resource resource)
    {
        boolean flag = true;
        if(resource == null || resource.getStatus() != 1)
            flag = false;
        return flag;
    }

    private static boolean isAnimationSupported()
    {
        boolean flag;
        if(Property.ENABLE_STREAM_GIF_ANIMATION.getBoolean() && EsApplication.sMemoryClass >= 64)
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        boolean flag;
        flag = hasBitmap(mStaticImageResource);
        boolean flag1;
        boolean flag2;
        Drawable drawable;
        if(mAnimatedDrawable == null)
        {
            Resource resource = mAnimatedImageResource;
            
            boolean flag3;
            if(hasImage(resource) && (resource.getResource() instanceof GifImage))
                flag3 = true;
            else
                flag3 = false;
            if(flag3)
            {
                mSrcRect.setEmpty();
                mAnimatedDrawable = new GifDrawable((GifImage)mAnimatedImageResource.getResource());
                ((GifDrawable)mAnimatedDrawable).setAnimationEnabled(isAnimationSupported());
                mAnimatedDrawable.setCallback(this);
            }
        }
        drawable = mAnimatedDrawable;
        if(drawable != null && (!(drawable instanceof GifDrawable) || ((GifDrawable)drawable).isValid()))
            flag1 = true;
        else
            flag1 = false;
        if(flag || flag1)
            flag2 = true;
        else
            flag2 = false;
        drawMediaTopAreaStage(canvas, k, l, flag2, mDestRect, sMediaTopAreaBackgroundPaint);
        if(!flag1) {
        	if(flag)
            {
                Bitmap bitmap;
                if(hasBitmap(mStaticImageResource))
                {
                    mSrcRect.setEmpty();
                    bitmap = (Bitmap)mStaticImageResource.getResource();
                } else
                {
                    bitmap = null;
                }
                if(bitmap != null)
                {
                    if(mIsShowingDrawable)
                        mSrcRect.setEmpty();
                    if(mSrcRect.isEmpty())
                        createSourceRectForMediaImage(mSrcRect, bitmap, k, l);
                    canvas.drawBitmap(bitmap, mSrcRect, mDestRect, sResizePaint);
                    mIsShowingBitmap = true;
                    mIsShowingDrawable = false;
                }
            }
        } else {
        	if(drawable != null)
            {
                if(mIsShowingBitmap)
                    mSrcRect.setEmpty();
                if(mSrcRect.isEmpty())
                {
                    createSourceRectForMediaImage(mSrcRect, drawable, k, l);
                    mSrcRectF.set(mSrcRect);
                    mDestRectF.set(mDestRect);
                    mMatrix.setRectToRect(mSrcRectF, mDestRectF, android.graphics.Matrix.ScaleToFit.CENTER);
                    if(!mMatrix.invert(mMatrixInverse))
                        mMatrixInverse.reset();
                }
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                canvas.concat(mMatrix);
                drawable.draw(canvas);
                canvas.concat(mMatrixInverse);
                mIsShowingBitmap = false;
                mIsShowingDrawable = true;
            }
        }
        drawMediaTopAreaShadow(canvas, k, l);
        drawTagBarIconAndBackground(canvas, i, j);
        drawPlusOneBar(canvas);
        drawMediaBottomArea(canvas, i, k, l);
        drawCornerIcon(canvas);
        return l;
    }

    public final void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamCardView.ViewedListener viewedlistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener)
    {
        super.init(cursor, i, j, onclicklistener, itemclicklistener, viewedlistener, streamplusbarclicklistener, streammediaclicklistener);
        byte abyte0[] = cursor.getBlob(28);
        if(abyte0 != null)
        {
            mDbEmbedEmotiShare = DbEmbedEmotishare.deserialize(abyte0);
            if(mDbEmbedEmotiShare != null)
                mMediaRef = mDbEmbedEmotiShare.getImageRef();
        }
    }

    public void invalidateDrawable(Drawable drawable)
    {
        if(drawable == mAnimatedDrawable)
            invalidate();
        else
            super.invalidateDrawable(drawable);
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
        {
            if(isAnimationSupported())
                mAnimatedImageResource = sImageResourceManager.getMedia(mMediaRef, 1, 4, this);
            mStaticImageResource = sImageResourceManager.getMedia(mMediaRef, 3, 0, this);
        }
    }

    public void onRecycle()
    {
        super.onRecycle();
        mDbEmbedEmotiShare = null;
        mMediaRef = null;
        mSrcRect.setEmpty();
        mDestRect.setEmpty();
        mSrcRectF.setEmpty();
        mDestRectF.setEmpty();
        mMatrix.reset();
    }

    protected final void onUnbindResources()
    {
        super.onUnbindResources();
        if(mStaticImageResource != null)
        {
            mStaticImageResource.unregister(this);
            mStaticImageResource = null;
        }
        if(mAnimatedImageResource != null)
        {
            mAnimatedImageResource.unregister(this);
            mAnimatedImageResource = null;
        }
        if(mAnimatedDrawable != null)
        {
            mAnimatedDrawable.setCallback(null);
            if(mAnimatedDrawable instanceof Recyclable)
                ((Recyclable)mAnimatedDrawable).onRecycle();
        }
        mAnimatedDrawable = null;
    }

    protected boolean verifyDrawable(Drawable drawable)
    {
        boolean flag;
        if(drawable == mAnimatedDrawable)
            flag = true;
        else
            flag = super.verifyDrawable(drawable);
        return flag;
    }
}
