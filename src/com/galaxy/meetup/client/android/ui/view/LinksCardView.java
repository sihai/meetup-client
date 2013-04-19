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
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.content.DbEmbedDeepLink;
import com.galaxy.meetup.client.android.content.DbEmbedMedia;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.util.LinksRenderUtils;

/**
 * 
 * @author sihai
 *
 */
public class LinksCardView extends StreamCardView {

	protected Rect mBackgroundDestRect;
    protected Rect mBackgroundSrcRect;
    protected String mCreationSource;
    protected DbEmbedDeepLink mDbEmbedAppInvite;
    protected DbEmbedMedia mDbEmbedMedia;
    protected ClickableButton mDeepLinkButton;
    protected Rect mImageBorderRect;
    protected int mImageDimension;
    protected Rect mImageRect;
    protected Resource mImageResource;
    protected Rect mImageSourceRect;
    protected boolean mIsReshare;
    protected StaticLayout mLinkTitleLayout;
    protected String mLinkUrl;
    protected StaticLayout mLinkUrlLayout;
    protected MediaRef mMediaRef;
    
    public LinksCardView(Context context)
    {
        this(context, null);
    }

    public LinksCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mImageDimension = 0;
        mBackgroundSrcRect = new Rect();
        mBackgroundDestRect = new Rect();
        mImageRect = new Rect();
        mImageBorderRect = new Rect();
        mImageSourceRect = new Rect();
    }

    public static String makeLinkUrl(String s) {
        if(TextUtils.isEmpty(s)) 
        	return null;
        String s2 = Uri.parse(s).getHost();
        if(TextUtils.isEmpty(s2)) {
        	return null;
        }
        String s1;
        if(s2.startsWith("www."))
            s2 = s2.substring(4);
        return s2.toLowerCase();
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        Bitmap bitmap;
        int l2;
        if(mImageResource == null)
            bitmap = null;
        else
            bitmap = (Bitmap)mImageResource.getResource();
        int i1;
        Rect rect;
        Rect rect1;
        Rect rect2;
        Rect rect3;
        Rect rect4;
        boolean flag;
        if(mMediaRef != null)
        {
            if(bitmap != null)
                flag = true;
            else
                flag = false;
            drawMediaTopAreaStage(canvas, k, l, flag, mBackgroundDestRect, LinksRenderUtils.getLinksTopAreaBackgroundPaint());
        } else
        {
            canvas.drawRect(mBackgroundDestRect, LinksRenderUtils.getAppInviteTopAreaBackgroundPaint());
        }
        if(bitmap != null)
        {
            if(mImageSourceRect.isEmpty())
            {
                LinksRenderUtils.createImageSourceRect(bitmap, mImageSourceRect);
                LinksRenderUtils.createBackgroundSourceRect(bitmap, mBackgroundDestRect, mBackgroundSrcRect);
            }
            rect = mImageSourceRect;
            rect1 = mBackgroundSrcRect;
            rect2 = mBackgroundDestRect;
            rect3 = mImageRect;
            rect4 = mImageBorderRect;
            LinksRenderUtils.drawBitmap(canvas, bitmap, rect, rect1, rect2, rect3, rect4);
        }
        i1 = sLeftBorderPadding + mImageRect.width();
        if(mLinkTitleLayout != null || mDeepLinkButton != null || mLinkUrlLayout != null)
        {
            int j1 = (int)((float)(l + sYDoublePadding) * getMediaHeightPercentage());
            int k1;
            int l1;
            int i2;
            int j2;
            int k2;
            if(mPlusOneButton == null)
                k1 = j1;
            else
                k1 = j1 - mPlusOneButton.getRect().height();
            if(mLinkTitleLayout != null)
                l1 = (int)mLinkTitleLayout.getPaint().descent();
            else
            if(mDeepLinkButton != null)
                l1 = 0;
            else
                l1 = (int)mLinkUrlLayout.getPaint().descent();
            if(mLinkTitleLayout == null)
                i2 = 0;
            else
                i2 = mLinkTitleLayout.getHeight();
            if(mDeepLinkButton == null)
                j2 = 0;
            else
                j2 = mDeepLinkButton.getRect().height();
            if(mLinkUrlLayout == null)
                k2 = 0;
            else
                k2 = mLinkUrlLayout.getHeight();
            l2 = l1 + (k1 - i2 - j2 - k2) / 2;
        } else
        {
            l2 = 0;
        }
        LinksRenderUtils.drawTitleDeepLinkAndUrl(canvas, i1, l2, mLinkTitleLayout, mDeepLinkButton, mLinkUrlLayout, sTagLinkBitmaps[0]);
        drawMediaTopAreaShadow(canvas, k, l);
        drawPlusOneBar(canvas);
        drawMediaBottomArea(canvas, i, k, l);
        drawCornerIcon(canvas);
        return l;
    }

    public final String getDeepLinkLabel()
    {
        String s;
        if(mDbEmbedAppInvite == null)
            s = null;
        else
            s = mDbEmbedAppInvite.getLabelOrDefault(getContext());
        return s;
    }

    public final String getLinkTitle()
    {
        String s3;
        if(mDbEmbedMedia == null)
        {
            s3 = null;
        } else
        {
            android.content.res.Resources resources = getResources();
            String s = mCreationSource;
            String s1 = mDbEmbedMedia.getTitle();
            String s2 = mDbEmbedMedia.getDescription();
            boolean flag = mIsReshare;
            boolean flag1;
            if(mDbEmbedAppInvite != null)
                flag1 = true;
            else
                flag1 = false;
            s3 = LinksRenderUtils.getLinkTitle(resources, s, s1, s2, flag, flag1);
        }
        return s3;
    }

    public final String getLinkUrl()
    {
        return mLinkUrl;
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
        mCreationSource = cursor.getString(21);
        boolean flag;
        byte abyte0[];
        if(!TextUtils.isEmpty(cursor.getString(18)))
            flag = true;
        else
            flag = false;
        mIsReshare = flag;
        abyte0 = cursor.getBlob(22);
        if(abyte0 != null)
        {
            mDbEmbedMedia = DbEmbedMedia.deserialize(abyte0);
            if(mDbEmbedMedia.getImageUrl() != null)
                mMediaRef = new MediaRef(null, 0L, mDbEmbedMedia.getImageUrl(), null, MediaRef.MediaType.IMAGE);
            mLinkUrl = makeLinkUrl(mDbEmbedMedia.getContentUrl());
        }
        if((0x20000L & cursor.getLong(15)) != 0L)
        {
            byte abyte1[] = cursor.getBlob(26);
            if(abyte1 != null)
                mDbEmbedAppInvite = DbEmbedDeepLink.deserialize(abyte1);
        }
    }

    protected final int layoutElements(int i, int j, int k, int l)
    {
        int i1 = k + sXDoublePadding;
        int j1 = (int)((float)(l + sYDoublePadding) * getMediaHeightPercentage());
        mBackgroundRect.set(0, j1, getMeasuredWidth(), getMeasuredHeight());
        createPlusOneBar(i, (j1 + sTopBorderPadding) - sYPadding, k);
        createMediaBottomArea(i, j, k, l);
        int k1;
        int l1;
        int i2;
        StaticLayout staticlayout;
        int j2;
        if(mPlusOneButton == null)
            k1 = j1;
        else
            k1 = j1 - mPlusOneButton.getRect().height();
        l1 = LinksRenderUtils.getMaxImageDimension();
        if(mImageDimension == 0)
        {
            mImageDimension = Math.min((int)((float)i1 * LinksRenderUtils.getImageMaxWidthPercentage()), Math.min(l1, k1));
            bindResources();
        }
        LinksRenderUtils.createBackgroundDestRect(sLeftBorderPadding, sTopBorderPadding, i1 + sLeftBorderPadding, j1 + sTopBorderPadding, mBackgroundDestRect);
        if(mMediaRef == null)
        {
            mImageRect.setEmpty();
            mImageBorderRect.setEmpty();
        } else
        {
            LinksRenderUtils.createImageRects(k1, mImageDimension, sLeftBorderPadding, sTopBorderPadding, mImageRect, mImageBorderRect);
        }
        i2 = i1 - 2 * sLeftBorderPadding - mImageRect.width();
        mLinkTitleLayout = LinksRenderUtils.createTitle(getLinkTitle(), mImageDimension, i2);
        staticlayout = mLinkTitleLayout;
        j2 = 0;
        if(staticlayout != null)
            j2 = 0 + mLinkTitleLayout.getHeight();
        if(mDbEmbedAppInvite != null)
        {
            Context context = getContext();
            mDeepLinkButton = LinksRenderUtils.createDeepLinkButton(context, mDbEmbedAppInvite.getLabelOrDefault(context), mImageRect.right + sLeftBorderPadding, j2 + mImageRect.top, i2, null);
        } else
        {
            mLinkUrlLayout = LinksRenderUtils.createUrl(mLinkUrl, mImageDimension, i2 - sTagLinkBitmaps[0].getWidth(), j2);
        }
        mImageSourceRect.setEmpty();
        mBackgroundSrcRect.setEmpty();
        return l;
    }

    protected final void onBindResources()
    {
        super.onBindResources();
        if(mMediaRef != null && mImageDimension != 0)
            mImageResource = ImageResourceManager.getInstance(getContext()).getMedia(mMediaRef, mImageDimension, mImageDimension, 0, this);
    }

    public void onRecycle()
    {
        super.onRecycle();
        mCreationSource = null;
        mIsReshare = false;
        mDbEmbedMedia = null;
        mDbEmbedAppInvite = null;
        mLinkTitleLayout = null;
        mDeepLinkButton = null;
        mLinkUrl = null;
        mLinkUrlLayout = null;
        mMediaRef = null;
        mBackgroundSrcRect.setEmpty();
        mBackgroundDestRect.setEmpty();
        mImageSourceRect.setEmpty();
        mImageRect.setEmpty();
        mImageBorderRect.setEmpty();
    }

    protected final void onUnbindResources()
    {
        super.onUnbindResources();
        if(mImageResource != null)
        {
            mImageResource.unregister(this);
            mImageResource = null;
        }
        mImageSourceRect.setEmpty();
    }

}
