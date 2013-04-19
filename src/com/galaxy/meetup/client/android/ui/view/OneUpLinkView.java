/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.LinksRenderUtils;
import com.galaxy.meetup.client.util.ViewUtils;

/**
 * 
 * @author sihai
 *
 */
public class OneUpLinkView extends View implements ResourceConsumer {

	private static Interpolator sDecelerateInterpolator;
    protected static Bitmap sLinkBitmap;
    protected static int sMaxWidth;
    private static int sMinExposureLand;
    private static int sMinExposurePort;
    private static boolean sOneUpLinkViewInitialized;
    protected static final Paint sResizePaint = new Paint(2);
    protected int mAvailableContentHeight;
    protected Rect mBackgroundDestRect;
    protected Rect mBackgroundSrcRect;
    protected ClickableItem mCurrentClickableItem;
    protected ClickableButton mDeepLinkButton;
    protected String mDeepLinkLabel;
    protected ClickableButton.ClickableButtonListener mDeepLinkListener;
    protected boolean mHasSeenImage;
    protected Rect mImageBorderRect;
    protected int mImageDimension;
    protected Rect mImageRect;
    protected Resource mImageResource;
    protected Rect mImageSourceRect;
    protected String mLinkTitle;
    protected StaticLayout mLinkTitleLayout;
    protected String mLinkUrl;
    protected StaticLayout mLinkUrlLayout;
    protected BackgroundViewLoadedListener mListener;
    protected MediaRef mMediaRef;
    protected int mType;
    
    public OneUpLinkView(Context context)
    {
        this(context, null);
    }

    public OneUpLinkView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public OneUpLinkView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        if(!sOneUpLinkViewInitialized)
        {
            sOneUpLinkViewInitialized = true;
            Resources resources = context.getResources();
            sLinkBitmap = ImageUtils.decodeResource(context.getResources(), R.drawable.ic_metadata_link);
            sMaxWidth = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_max_width);
            sMinExposureLand = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_min_height_land);
            sMinExposurePort = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_min_height_port);
        }
        mBackgroundSrcRect = new Rect();
        mBackgroundDestRect = new Rect();
        mImageRect = new Rect();
        mImageBorderRect = new Rect();
        mImageSourceRect = new Rect();
    }

    public static void onStart()
    {
    }

    public static void onStop()
    {
    }

    public final void bindResources()
    {
        if(ViewUtils.isViewAttached(this) && mMediaRef != null && mImageDimension != 0)
            mImageResource = ImageResourceManager.getInstance(getContext()).getMedia(mMediaRef, mImageDimension, mImageDimension, 0, this);
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent) {
        boolean flag;
        int i;
        int j;
        boolean flag1;
        int k;
        if(mDeepLinkListener != null && mDeepLinkButton != null)
            flag = true;
        else
            flag = false;
        i = (int)motionevent.getX();
        j = (int)motionevent.getY();
        k = motionevent.getAction();
        flag1 = false;
        
        switch(k) {
	        case 0:
	        	if(flag && mDeepLinkButton.handleEvent(i, j, 0))
	            {
	                mCurrentClickableItem = mDeepLinkButton;
	                invalidate();
	            }
	            flag1 = true;
	        	break;
	        case 1:
	        	mCurrentClickableItem = null;
	            boolean flag2;
	            if(flag && mDeepLinkButton.handleEvent(i, j, 1))
	                flag2 = true;
	            else
	                flag2 = false;
	            invalidate();
	            flag1 = false;
	            if(!flag2)
	            {
	                performClick();
	                flag1 = false;
	            }
	        	break;
	        case 2:
	        	break;
	        case 3:
	        	ClickableItem clickableitem = mCurrentClickableItem;
	            flag1 = false;
	            if(clickableitem != null)
	            {
	                mCurrentClickableItem.handleEvent(i, j, 3);
	                mCurrentClickableItem = null;
	                invalidate();
	                flag1 = true;
	            }
	        	break;
        	default:
        		break;
        }
        return flag1;
    }

    protected int getMinExposureLand()
    {
        return sMinExposureLand;
    }

    protected int getMinExposurePort()
    {
        return sMinExposurePort;
    }

    public final void init(MediaRef mediaref, int i, BackgroundViewLoadedListener backgroundviewloadedlistener, String s, String s1, ClickableButton.ClickableButtonListener clickablebuttonlistener, String s2)
    {
        mLinkTitle = s;
        mDeepLinkLabel = s1;
        mDeepLinkListener = clickablebuttonlistener;
        mLinkUrl = s2;
        mMediaRef = mediaref;
        mType = i;
        mListener = backgroundviewloadedlistener;
        if(mMediaRef != null && android.os.Build.VERSION.SDK_INT >= 12)
            setAlpha(0.001F);
        requestLayout();
        invalidate();
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        bindResources();
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        unbindResources();
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int i;
        int j1;
        if(mImageResource == null)
        {
            canvas.drawPaint(LinksRenderUtils.getAppInviteTopAreaBackgroundPaint());
        } else
        {
            Bitmap bitmap = (Bitmap)mImageResource.getResource();
            if(bitmap != null)
            {
                if(!mHasSeenImage)
                {
                    if(android.os.Build.VERSION.SDK_INT >= 12)
                    {
                        if(sDecelerateInterpolator == null)
                            sDecelerateInterpolator = new DecelerateInterpolator();
                        animate().alpha(1.0F).setDuration(500L).setInterpolator(sDecelerateInterpolator);
                    }
                    mHasSeenImage = true;
                }
                if(mImageSourceRect.isEmpty())
                {
                    LinksRenderUtils.createImageSourceRect(bitmap, mImageSourceRect);
                    LinksRenderUtils.createBackgroundSourceRect(bitmap, mBackgroundDestRect, mBackgroundSrcRect);
                }
                LinksRenderUtils.drawBitmap(canvas, bitmap, mImageSourceRect, mBackgroundSrcRect, mBackgroundDestRect, mImageRect, mImageBorderRect);
            }
        }
        i = mImageRect.width();
        if(mLinkTitleLayout != null || mLinkUrlLayout != null)
        {
            int j;
            int k;
            int l;
            int i1;
            if(mLinkTitleLayout != null)
                j = (int)mLinkTitleLayout.getPaint().descent();
            else
            if(mDeepLinkButton != null)
                j = 0;
            else
                j = (int)mLinkUrlLayout.getPaint().descent();
            if(mLinkTitleLayout == null)
                k = 0;
            else
                k = mLinkTitleLayout.getHeight();
            if(mDeepLinkButton == null)
                l = 0;
            else
                l = mDeepLinkButton.getRect().height();
            if(mLinkUrlLayout == null)
                i1 = 0;
            else
                i1 = mLinkUrlLayout.getHeight();
            j1 = j + (mAvailableContentHeight - k - l - i1) / 2;
        } else
        {
            j1 = 0;
        }
        LinksRenderUtils.drawTitleDeepLinkAndUrl(canvas, i, j1, mLinkTitleLayout, mDeepLinkButton, mLinkUrlLayout, sLinkBitmap);
    }

    public void onLayout(boolean flag, int i, int j, int k, int l) {
        super.onLayout(flag, i, j, k, l);
        int i1 = getMeasuredWidth();
        int j1 = getMeasuredHeight();
        int k1;
        StaticLayout staticlayout;
        int l1;
        if(i1 <= sMaxWidth)
            mAvailableContentHeight = j1;
        else
        if(getResources().getConfiguration().orientation == 2)
            mAvailableContentHeight = j1 - getMinExposureLand();
        else
            mAvailableContentHeight = j1 - getMinExposurePort();
        if(mMediaRef != null)
        {
            int i2 = LinksRenderUtils.getMaxImageDimension();
            if(mImageDimension == 0)
            {
                mImageDimension = Math.min((int)((float)i1 * LinksRenderUtils.getImageMaxWidthPercentage()), Math.min(i2, mAvailableContentHeight));
                bindResources();
            }
            LinksRenderUtils.createBackgroundDestRect(0, 0, i1, j1, mBackgroundDestRect);
            LinksRenderUtils.createImageRects(mAvailableContentHeight, mImageDimension, 0, 0, mImageRect, mImageBorderRect);
        } else
        {
            mImageDimension = mAvailableContentHeight;
        }
        k1 = i1 - mImageRect.width();
        mLinkTitleLayout = LinksRenderUtils.createTitle(mLinkTitle, mImageDimension, k1);
        staticlayout = mLinkTitleLayout;
        l1 = 0;
        if(staticlayout != null)
            l1 = 0 + mLinkTitleLayout.getHeight();
        if(!TextUtils.isEmpty(mDeepLinkLabel))
        {
            mDeepLinkButton = LinksRenderUtils.createDeepLinkButton(getContext(), mDeepLinkLabel, mImageRect.right, l1, k1, mDeepLinkListener);
            mDeepLinkButton.getRect().height();
        } else
        {
            mLinkUrlLayout = LinksRenderUtils.createUrl(mLinkUrl, mImageDimension, k1 - sLinkBitmap.getWidth(), l1);
        }
        mImageSourceRect.setEmpty();
        mBackgroundSrcRect.setEmpty();
    }

    public final void onResourceStatusChange(Resource resource) {
        invalidate();
        if(resource.getStatus() == 1 && mListener != null)
            mListener.onBackgroundViewLoaded(this);
    }

    public final void unbindResources() {
        if(mImageResource != null) {
            mImageResource.unregister(this);
            mImageResource = null;
        }
        mImageSourceRect.setEmpty();
    }
	
    
    public static interface BackgroundViewLoadedListener {

        void onBackgroundViewLoaded(OneUpLinkView oneuplinkview);
    }

}
