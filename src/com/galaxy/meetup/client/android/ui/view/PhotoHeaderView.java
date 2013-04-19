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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.util.FIFEUtil;
import com.galaxy.meetup.client.util.GifDrawable;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.client.util.ViewUtils;
import com.galaxy.meetup.server.client.domain.DataVideo;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class PhotoHeaderView extends View implements OnDoubleTapListener,
		OnGestureListener, OnScaleGestureListener, ResourceConsumer {

	private static int sBackgroundColor;
    private static Bitmap sCommentBitmap;
    private static int sCommentCountLeftMargin;
    private static TextPaint sCommentCountPaint;
    private static int sCommentCountTextWidth;
    private static Paint sCropDimPaint;
    private static Paint sCropPaint;
    private static int sCropSizeCoverWidth;
    private static int sCropSizeProfile;
    private static boolean sHasMultitouchDistinct;
    private static ImageResourceManager sImageManager;
    private static boolean sInitialized;
    private static Bitmap sPanoramaImage;
    private static int sPhotoOverlayBottomPadding;
    private static int sPhotoOverlayRightPadding;
    private static Bitmap sPlusOneBitmap;
    private static int sPlusOneBottomMargin;
    private static int sPlusOneCountLeftMargin;
    private static TextPaint sPlusOneCountPaint;
    private static int sPlusOneCountTextWidth;
    private static Paint sProcessingMediaBackgroundPaint;
    private static String sProcessingMediaSubTitle;
    private static TextPaint sProcessingMediaSubTitleTextPaint;
    private static int sProcessingMediaSubTitleVerticalPosition;
    private static String sProcessingMediaTitle;
    private static TextPaint sProcessingMediaTitleTextPaint;
    private static int sProcessingMediaTitleVerticalPosition;
    private static Paint sTagPaint;
    private static Paint sTagTextBackgroundPaint;
    private static int sTagTextPadding;
    private static TextPaint sTagTextPaint;
    private static Bitmap sVideoImage;
    private static Bitmap sVideoNotReadyImage;
    private boolean mAllowCrop;
    private boolean mAnimate;
    private Resource mAnimatedResource;
    private int mBackgroundColor;
    private String mCommentText;
    private Integer mCoverPhotoOffset;
    private int mCropMode;
    private Rect mCropRect;
    private int mCropSizeHeight;
    private int mCropSizeWidth;
    private boolean mDoubleTapDebounce;
    private boolean mDoubleTapToZoomEnabled;
    private Matrix mDrawMatrix;
    private Drawable mDrawable;
    private View.OnClickListener mExternalClickListener;
    private int mFixedHeight;
    private boolean mFlingDebounce;
    private boolean mFullScreen;
    private GestureDetector mGestureDetector;
    private boolean mHaveLayout;
    private OnImageListener mImageListener;
    float mInitialTranslationY;
    private boolean mIsDoubleTouch;
    private boolean mIsPlaceHolder;
    private long mLastTwoFingerUp;
    private boolean mLoadAnimatedImage;
    private Matrix mMatrix;
    private float mMaxScale;
    MediaRef mMediaRef;
    private float mMinScale;
    float mOriginalAspectRatio;
    private Matrix mOriginalMatrix;
    private String mPlusOneText;
    private RotateRunnable mRotateRunnable;
    private float mRotation;
    private float mScaleFactor;
    private ScaleGestureDetector mScaleGetureDetector;
    private ScaleRunnable mScaleRunnable;
    private boolean mShouldTriggerViewLoaded;
    private boolean mShowTagShape;
    private SnapRunnable mSnapRunnable;
    private Resource mStaticResource;
    private CharSequence mTagName;
    private RectF mTagNameBackground;
    private RectF mTagShape;
    private RectF mTempDst;
    private RectF mTempSrc;
    private boolean mTransformNoScaling;
    private boolean mTransformVerticalOnly;
    private boolean mTransformsEnabled;
    private RectF mTranslateRect;
    private TranslateRunnable mTranslateRunnable;
    private float mValues[];
    private byte mVideoBlob[];
    private boolean mVideoReady;
    
    public PhotoHeaderView(Context context)
    {
        super(context);
        mShouldTriggerViewLoaded = true;
        mMatrix = new Matrix();
        mOriginalMatrix = new Matrix();
        mFixedHeight = -1;
        mCropRect = new Rect();
        mDoubleTapToZoomEnabled = true;
        mTempSrc = new RectF();
        mTempDst = new RectF();
        mTranslateRect = new RectF();
        mValues = new float[9];
        mTagNameBackground = new RectF();
        if(sImageManager == null)
            sImageManager = ImageResourceManager.getInstance(getContext());
        initialize();
    }

    public PhotoHeaderView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mShouldTriggerViewLoaded = true;
        mMatrix = new Matrix();
        mOriginalMatrix = new Matrix();
        mFixedHeight = -1;
        mCropRect = new Rect();
        mDoubleTapToZoomEnabled = true;
        mTempSrc = new RectF();
        mTempDst = new RectF();
        mTranslateRect = new RectF();
        mValues = new float[9];
        mTagNameBackground = new RectF();
        if(sImageManager == null)
            sImageManager = ImageResourceManager.getInstance(getContext());
        initialize();
    }

    public PhotoHeaderView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mShouldTriggerViewLoaded = true;
        mMatrix = new Matrix();
        mOriginalMatrix = new Matrix();
        mFixedHeight = -1;
        mCropRect = new Rect();
        mDoubleTapToZoomEnabled = true;
        mTempSrc = new RectF();
        mTempDst = new RectF();
        mTranslateRect = new RectF();
        mValues = new float[9];
        mTagNameBackground = new RectF();
        if(sImageManager == null)
            sImageManager = ImageResourceManager.getInstance(getContext());
        initialize();
    }

    private void clearDrawable()
    {
        if(mDrawable != null)
            mDrawable.setCallback(null);
        if(mDrawable instanceof Recyclable)
            ((Recyclable)mDrawable).onRecycle();
        mDrawable = null;
    }

    private void configureBounds(boolean flag)
    {
        if(mDrawable != null && mHaveLayout)
        {
        	float f;
            int i = mDrawable.getIntrinsicWidth();
            int j = mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(0, 0, i, j);
            if(flag || mMinScale == 0.0F && mDrawable != null && mHaveLayout)
            {
                int k = mDrawable.getIntrinsicWidth();
                int l = mDrawable.getIntrinsicHeight();
                int i1 = mCropRect.right - mCropRect.left;
                int j1 = mCropRect.bottom - mCropRect.top;
                mTempSrc.set(0.0F, 0.0F, k, l);
                if(mAllowCrop)
                {
                    mOriginalAspectRatio = (float)l / (float)k;
                    float f1 = (float)j1 / (float)i1;
                    if(mOriginalAspectRatio > f1)
                    {
                        int i2 = (mCropRect.top + mCropRect.bottom) / 2;
                        int j2 = Math.round((float)i1 * mOriginalAspectRatio) / 2;
                        mTempDst.set(mCropRect.left, i2 - j2, mCropRect.right, i2 + j2);
                    } else
                    {
                        int k1 = (mCropRect.right + mCropRect.left) / 2;
                        int l1 = Math.round((float)j1 / mOriginalAspectRatio) / 2;
                        mTempDst.set(k1 - l1, mCropRect.top, k1 + l1, mCropRect.bottom);
                    }
                } else
                {
                    mTempDst.set(0.0F, 0.0F, getWidth(), getHeight());
                }
                mMatrix.setRectToRect(mTempSrc, mTempDst, android.graphics.Matrix.ScaleToFit.CENTER);
                if(mCropMode == 2)
                {
                    mMatrix.getValues(mValues);
                    mInitialTranslationY = mValues[5];
                    if(mCoverPhotoOffset != null)
                    {
                        f = ((float)mCoverPhotoOffset.intValue() - getCoverPhotoTopOffset(mMatrix)) * ((float)i1 / 940F);
                        mMatrix.postTranslate(0.0F, f);
                    }
                }
                mOriginalMatrix.set(mMatrix);
                mMinScale = getScale();
                mMaxScale = Math.max(2.0F * mMinScale, Math.min(8F * mMinScale, 8F));
            }
            mDrawMatrix = mMatrix;
        }
    }

    private float getCoverPhotoTopOffset(Matrix matrix)
    {
        float f1;
        if(mCropMode != 2)
        {
            f1 = -1F;
        } else
        {
            matrix.getValues(mValues);
            int i = Math.round(mValues[5] - mInitialTranslationY);
            float f = mValues[4];
            int j = Math.round(f * (float)((BitmapDrawable)mDrawable).getBitmap().getHeight());
            int k = mCropRect.bottom - mCropRect.top;
            f1 = -((((float)j / 2.0F - (float)k / 2.0F - (float)i) / f) * (940F / (float)((BitmapDrawable)mDrawable).getBitmap().getWidth()));
        }
        return f1;
    }

    private float getScale()
    {
        mMatrix.getValues(mValues);
        return mValues[0];
    }

    private void initialize()
    {
        boolean flag = true;
        Context context = getContext();
        if(!sInitialized)
        {
            sInitialized = flag;
            Resources resources = context.getApplicationContext().getResources();
            sCommentBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_photodetail_comment);
            sPlusOneBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_photodetail_plus);
            sVideoImage = ImageUtils.decodeResource(resources, R.drawable.video_overlay);
            sVideoNotReadyImage = ImageUtils.decodeResource(resources, R.drawable.ic_loading_video);
            sPanoramaImage = ImageUtils.decodeResource(resources, R.drawable.overlay_lightcycle);
            sBackgroundColor = resources.getColor(R.color.photo_background_color);
            TextPaint textpaint = new TextPaint();
            sPlusOneCountPaint = textpaint;
            textpaint.setAntiAlias(flag);
            sPlusOneCountPaint.setColor(resources.getColor(R.color.photo_info_plusone_count_color));
            sPlusOneCountPaint.setTextSize(resources.getDimension(R.dimen.photo_info_plusone_text_size));
            TextPaintUtils.registerTextPaint(sPlusOneCountPaint, R.dimen.photo_info_plusone_text_size);
            TextPaint textpaint1 = new TextPaint();
            sCommentCountPaint = textpaint1;
            textpaint1.setAntiAlias(flag);
            sCommentCountPaint.setColor(resources.getColor(R.color.photo_info_comment_count_color));
            sCommentCountPaint.setTextSize(resources.getDimension(R.dimen.photo_info_comment_text_size));
            TextPaintUtils.registerTextPaint(sCommentCountPaint, R.dimen.photo_info_comment_text_size);
            Paint paint = new Paint();
            sTagPaint = paint;
            paint.setAntiAlias(flag);
            sTagPaint.setColor(resources.getColor(R.color.photo_tag_color));
            sTagPaint.setStyle(android.graphics.Paint.Style.STROKE);
            sTagPaint.setStrokeWidth(resources.getDimension(R.dimen.photo_tag_stroke_width));
            sTagPaint.setShadowLayer(resources.getDimension(R.dimen.photo_tag_shadow_radius), 0.0F, 0.0F, resources.getColor(R.color.photo_tag_shadow_color));
            sCropSizeProfile = resources.getDimensionPixelSize(R.dimen.photo_crop_profile_width);
            sCropSizeCoverWidth = resources.getDimensionPixelSize(R.dimen.photo_crop_cover_width);
            Paint paint1 = new Paint();
            sCropDimPaint = paint1;
            paint1.setAntiAlias(flag);
            sCropDimPaint.setColor(resources.getColor(R.color.photo_crop_dim_color));
            sCropDimPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint2 = new Paint();
            sCropPaint = paint2;
            paint2.setAntiAlias(flag);
            sCropPaint.setColor(resources.getColor(R.color.photo_crop_highlight_color));
            sCropPaint.setStyle(android.graphics.Paint.Style.STROKE);
            sCropPaint.setStrokeWidth(resources.getDimension(R.dimen.photo_crop_stroke_width));
            TextPaint textpaint2 = new TextPaint();
            sTagTextPaint = textpaint2;
            textpaint2.setAntiAlias(flag);
            sTagTextPaint.setColor(resources.getColor(R.color.photo_tag_text_color));
            sTagTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sTagTextPaint.setTextSize(resources.getDimension(R.dimen.photo_tag_text_size));
            sTagTextPaint.setShadowLayer(0.0F, 0.0F, 0.0F, 0xff000000);
            TextPaintUtils.registerTextPaint(sTagTextPaint, R.dimen.photo_tag_text_size);
            TextPaint textpaint3 = new TextPaint();
            sProcessingMediaTitleTextPaint = textpaint3;
            textpaint3.setColor(resources.getColor(R.color.photo_processing_text_color));
            sProcessingMediaTitleTextPaint.setTextSize(resources.getDimension(R.dimen.photo_processing_message_title_size));
            sProcessingMediaTitleTextPaint.setAntiAlias(flag);
            sProcessingMediaTitleTextPaint.setFakeBoldText(flag);
            sProcessingMediaTitleTextPaint.setStyle(android.graphics.Paint.Style.FILL);
            sProcessingMediaTitleTextPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
            TextPaint textpaint4 = new TextPaint();
            sProcessingMediaSubTitleTextPaint = textpaint4;
            textpaint4.setColor(resources.getColor(R.color.photo_processing_text_color));
            sProcessingMediaSubTitleTextPaint.setTextSize(resources.getDimension(R.dimen.photo_processing_message_subtitle_size));
            sProcessingMediaSubTitleTextPaint.setAntiAlias(flag);
            sProcessingMediaSubTitleTextPaint.setFakeBoldText(flag);
            sProcessingMediaSubTitleTextPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
            Paint paint3 = new Paint();
            sProcessingMediaBackgroundPaint = paint3;
            paint3.setColor(resources.getColor(R.color.photo_processing_background_color));
            sProcessingMediaTitle = resources.getString(R.string.media_processing_message);
            sProcessingMediaSubTitle = resources.getString(R.string.media_processing_message_subtitle);
            sProcessingMediaTitleVerticalPosition = (int)resources.getDimension(R.dimen.photo_processing_message_title_vertical_position);
            sProcessingMediaSubTitleVerticalPosition = (int)resources.getDimension(R.dimen.photo_processing_message_subtitle_vertical_position);
            Paint paint4 = new Paint();
            sTagTextBackgroundPaint = paint4;
            paint4.setColor(resources.getColor(R.color.photo_tag_text_background_color));
            sTagTextBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            sPhotoOverlayRightPadding = (int)resources.getDimension(R.dimen.photo_overlay_right_padding);
            sPhotoOverlayBottomPadding = (int)resources.getDimension(R.dimen.photo_overlay_bottom_padding);
            sCommentCountLeftMargin = (int)resources.getDimension(R.dimen.photo_info_comment_count_left_margin);
            sCommentCountTextWidth = (int)resources.getDimension(R.dimen.photo_info_comment_count_text_width);
            sPlusOneCountLeftMargin = (int)resources.getDimension(R.dimen.photo_info_plusone_count_left_margin);
            sPlusOneCountTextWidth = (int)resources.getDimension(R.dimen.photo_info_plusone_count_text_width);
            sPlusOneBottomMargin = (int)resources.getDimension(R.dimen.photo_info_plusone_bottom_margin);
            sTagTextPadding = (int)resources.getDimension(R.dimen.photo_tag_text_padding);
            sHasMultitouchDistinct = context.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch.distinct");
        }
        if(sHasMultitouchDistinct)
            flag = false;
        mGestureDetector = new GestureDetector(context, this, null, flag);
        mScaleGetureDetector = new ScaleGestureDetector(context, this);
        mScaleRunnable = new ScaleRunnable(this);
        mTranslateRunnable = new TranslateRunnable(this);
        mSnapRunnable = new SnapRunnable(this);
        mRotateRunnable = new RotateRunnable(this);
    }

    public static void onStart()
    {
    }

    public static void onStop()
    {
    }

    private void scale(float f, float f1, float f2)
    {
        mMatrix.postRotate(-mRotation, getWidth() / 2, getHeight() / 2);
        float f3 = Math.min(Math.max(f, mMinScale), mMaxScale);
        float f4 = f3 / getScale();
        mMatrix.postScale(f4, f4, f1, f2);
        snap();
        mMatrix.postRotate(mRotation, getWidth() / 2, getHeight() / 2);
        invalidate();
        if(mImageListener != null)
            mImageListener.onImageScaled(f3 / mMinScale);
    }

    private void snap()
    {
        mTranslateRect.set(mTempSrc);
        mMatrix.mapRect(mTranslateRect);
        float f;
        float f1;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        float f8;
        float f9;
        if(mAllowCrop)
            f = mCropRect.left;
        else
            f = 0.0F;
        if(mAllowCrop)
            f1 = mCropRect.right;
        else
            f1 = getWidth();
        f2 = mTranslateRect.left;
        f3 = mTranslateRect.right;
        if(f3 - f2 < f1 - f)
            f4 = f + (f1 - f - (f3 + f2)) / 2.0F;
        else
        if(f2 > f)
            f4 = f - f2;
        else
        if(f3 < f1)
            f4 = f1 - f3;
        else
            f4 = 0.0F;
        if(mAllowCrop)
            f5 = mCropRect.top;
        else
            f5 = 0.0F;
        if(mAllowCrop)
            f6 = mCropRect.bottom;
        else
            f6 = getHeight();
        f7 = mTranslateRect.top;
        f8 = mTranslateRect.bottom;
        if(f8 - f7 < f6 - f5)
            f9 = f5 + (f6 - f5 - (f8 + f7)) / 2.0F;
        else
        if(f7 > f5)
            f9 = f5 - f7;
        else
        if(f8 < f6)
            f9 = f6 - f8;
        else
            f9 = 0.0F;
        if(Math.abs(f4) > 20F || Math.abs(f9) > 20F)
        {
            mSnapRunnable.start(f4, f9);
        } else
        {
            mMatrix.postTranslate(f4, f9);
            invalidate();
        }
    }

    private boolean translate(float f, float f1)
    {
        mTranslateRect.set(mTempSrc);
        mMatrix.mapRect(mTranslateRect);
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        boolean flag;
        if(mAllowCrop)
            f2 = mCropRect.left;
        else
            f2 = 0.0F;
        if(mAllowCrop)
            f3 = mCropRect.right;
        else
            f3 = getWidth();
        f4 = mTranslateRect.left;
        f5 = mTranslateRect.right;
        if(mAllowCrop)
            f6 = Math.max(f2 - mTranslateRect.right, Math.min(f3 - mTranslateRect.left, f));
        else
        if(f5 - f4 < f3 - f2)
            f6 = f2 + (f3 - f2 - (f5 + f4)) / 2.0F;
        else
            f6 = Math.max(f3 - f5, Math.min(f2 - f4, f));
        if(mAllowCrop)
            f7 = mCropRect.top;
        else
            f7 = 0.0F;
        if(mAllowCrop)
            f8 = mCropRect.bottom;
        else
            f8 = getHeight();
        f9 = mTranslateRect.top;
        f10 = mTranslateRect.bottom;
        if(mAllowCrop)
            f11 = Math.max(f7 - mTranslateRect.bottom, Math.min(f8 - mTranslateRect.top, f1));
        else
        if(f10 - f9 < f8 - f7)
            f11 = f7 + (f8 - f7 - (f10 + f9)) / 2.0F;
        else
            f11 = Math.max(f8 - f10, Math.min(f7 - f9, f1));
        mMatrix.postTranslate(f6, f11);
        invalidate();
        if(f6 == f && f11 == f1)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void bindResources()
    {
        if(ViewUtils.isViewAttached(this) && mMediaRef != null)
        {
            MediaRef mediaref = mMediaRef;
            String s = mMediaRef.getUrl();
            if(FIFEUtil.isFifeHostedUrl(s) && !mAllowCrop)
            {
                mLoadAnimatedImage = true;
                mediaref = new MediaRef(mMediaRef.getOwnerGaiaId(), mMediaRef.getPhotoId(), s, mMediaRef.getLocalUri(), mMediaRef.getDisplayName(), mMediaRef.getType());
            }
            if(mCropMode == 2)
                mStaticResource = sImageManager.getMedia(mediaref, 940, 0, 0, this);
            else
                mStaticResource = sImageManager.getMedia(mediaref, 3, this);
        }
    }

    public final void bindTagData(RectF rectf, CharSequence charsequence)
    {
        mTagShape = rectf;
        mTagName = charsequence;
    }

    public final void destroy()
    {
        mGestureDetector = null;
        mScaleGetureDetector = null;
        mScaleRunnable.stop();
        mScaleRunnable = null;
        mTranslateRunnable.stop();
        mTranslateRunnable = null;
        mSnapRunnable.stop();
        mSnapRunnable = null;
        mRotateRunnable.stop();
        mRotateRunnable = null;
        setOnClickListener(null);
        mExternalClickListener = null;
        clearDrawable();
        unbindResources();
    }

    public final void doAnimate(boolean flag)
    {
        if(mAnimate != flag)
        {
            mAnimate = flag;
            if(mDrawable instanceof GifDrawable)
                ((GifDrawable)mDrawable).setAnimationEnabled(mAnimate);
            invalidate();
        }
    }

    public final void enableImageTransforms(boolean flag)
    {
        mTransformsEnabled = flag;
        if(!mTransformsEnabled)
            resetTransformations();
    }

    public final Bitmap getBitmap()
    {
        Bitmap bitmap;
        if(mDrawable instanceof BitmapDrawable)
            bitmap = ((BitmapDrawable)mDrawable).getBitmap();
        else
            bitmap = null;
        return bitmap;
    }

    public final int getCoverPhotoTopOffset()
    {
        return Math.round(getCoverPhotoTopOffset(mDrawMatrix));
    }

    public final Bitmap getCroppedPhoto() {
        if(!mAllowCrop) 
        	return null; 
        
        Bitmap bitmap = null;
        float f;
        float f1;
        float f2;
        float f3;
        Matrix matrix;
        int j;
        int k;
        if(2 == mCropMode) {
        	
            f = 256F;
            f1 = f;
            f3 = 256F / (float)(mCropRect.right - mCropRect.left);
            f2 = f3;
            j = -mCropRect.left;
            k = -mCropRect.top;
            matrix = mDrawMatrix;
        } else {
        	int i = ((BitmapDrawable)mDrawable).getBitmap().getWidth();
            if((float)i >= 1024F) {
            	bitmap = ((BitmapDrawable)mDrawable).getBitmap();
            	return bitmap;
            } else {
            	 f = 940F;
                 f1 = 940F * mOriginalAspectRatio;
                 f2 = 940F / (float)i;
                 f3 = f2;
                 matrix = null;
                 j = 0;
                 k = 0;
            }
        }
        
        bitmap = Bitmap.createBitmap((int)f, (int)f1, android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix1 = new Matrix(matrix);
        if(j + k != 0)
            matrix1.postTranslate(j, k);
        if(f3 + f2 != 0.0F)
            matrix1.postScale(f3, f2);
        canvas.drawColor(mBackgroundColor);
        if(mDrawable != null)
        {
            canvas.concat(matrix1);
            mDrawable.draw(canvas);
        }
        
        return bitmap;
        
    }

    public final byte[] getVideoData()
    {
        return mVideoBlob;
    }

    public final void init(MediaRef mediaref, int i)
    {
        if((mMediaRef == null || !mMediaRef.equals(mediaref)) && mMediaRef != mediaref)
        {
            unbindResources();
            clearDrawable();
            mMediaRef = mediaref;
            mBackgroundColor = i;
            bindResources();
            requestLayout();
            invalidate();
        }
    }

    public final void init(MediaRef mediaref, boolean flag)
    {
        mIsPlaceHolder = flag;
        init(mediaref, sBackgroundColor);
    }

    public void invalidateDrawable(Drawable drawable)
    {
        if(drawable == mDrawable)
            invalidate();
        else
            super.invalidateDrawable(drawable);
    }

    public final boolean isPanorama()
    {
    	if((mMediaRef == null || MediaRef.MediaType.PANORAMA != mMediaRef.getType()) && (mStaticResource == null || mStaticResource.getResourceType() != 2))
            return false;
    	else
    		return true;
    }

    public final boolean isPhotoBound()
    {
        boolean flag;
        if(mDrawable != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isVideo()
    {
        boolean flag;
        if(mVideoBlob != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isVideoReady()
    {
        boolean flag;
        if(mVideoBlob != null && mVideoReady)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public void jumpDrawablesToCurrentState()
    {
        super.jumpDrawablesToCurrentState();
        if(mDrawable != null)
            mDrawable.jumpToCurrentState();
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        bindResources();
        if(mDrawable != null)
        {
            Drawable drawable = mDrawable;
            boolean flag;
            if(getVisibility() == 0)
                flag = true;
            else
                flag = false;
            drawable.setVisible(flag, false);
        }
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        unbindResources();
        if(mDrawable != null)
            mDrawable.setVisible(false, false);
    }

    public boolean onDoubleTap(MotionEvent motionevent)
    {
        if(mDoubleTapToZoomEnabled && mTransformsEnabled)
        {
            if(!mDoubleTapDebounce)
            {
                float f = getScale();
                float f1 = f * 1.5F;
                float f2 = Math.max(mMinScale, f1);
                float f3 = Math.min(mMaxScale, f2);
                mScaleRunnable.start(f, f3, motionevent.getX(), motionevent.getY());
            }
            mDoubleTapDebounce = false;
        }
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent motionevent)
    {
        return true;
    }

    public boolean onDown(MotionEvent motionevent)
    {
        if(mTransformsEnabled)
        {
            mTranslateRunnable.stop();
            mSnapRunnable.stop();
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBackgroundColor);
        if(mIsPlaceHolder || null == mDrawable) {
        	canvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), sProcessingMediaBackgroundPaint);
            canvas.drawText(sProcessingMediaTitle, getWidth() / 2, sProcessingMediaTitleVerticalPosition, sProcessingMediaTitleTextPaint);
            canvas.drawText(sProcessingMediaSubTitle, getWidth() / 2, sProcessingMediaSubTitleVerticalPosition, sProcessingMediaSubTitleTextPaint);
        } else {
        
        	int l;
            float f;
            float f1;
            float f2;
            float f5;
            float f6;
            float f7;
            float f8;
            float f9;
            float f10;
            float f11;
            float f12;
            float f15;
            float f16;
            Bitmap bitmap;
            int l3;
            int i4;
            float f17;
            float f18;
	        int i = canvas.getSaveCount();
	        canvas.save();
	        if(mDrawMatrix != null)
	            canvas.concat(mDrawMatrix);
	        mDrawable.draw(canvas);
	        canvas.restoreToCount(i);
	        if(mVideoBlob == null) {
	        	int j;
                int k;
	        	 if(isPanorama())
	             {
	                 j = (getWidth() - sPanoramaImage.getWidth()) / 2;
	                 k = (getHeight() - sPanoramaImage.getHeight()) / 2;
	                 canvas.drawBitmap(sPanoramaImage, j, k, null);
	             }
	        	 mTranslateRect.set(mDrawable.getBounds());
	             if(mDrawMatrix != null)
	                 mDrawMatrix.mapRect(mTranslateRect);
	             if(mShowTagShape && mTagShape != null)
	             {
	                 f = mTranslateRect.width();
	                 f1 = mTranslateRect.height();
	                 f2 = f * mTagShape.left + mTranslateRect.left;
	                 float f3 = f1 * mTagShape.top + mTranslateRect.top;
	                 float f4 = f * mTagShape.right + mTranslateRect.left;
	                 f5 = f1 * mTagShape.bottom + mTranslateRect.top;
	                 canvas.drawRect(f2, f3, f4, f5, sTagPaint);
	                 if(mTagName != null)
	                 {
	                     f6 = 2.0F * (float)sTagTextPadding;
	                     f7 = f2 + (f4 - f2) / 2.0F;
	                     f8 = sTagTextPaint.measureText(mTagName, 0, mTagName.length());
	                     f9 = sTagTextPaint.descent() - sTagTextPaint.ascent();
	                     f10 = f8 + f6;
	                     f11 = f9 + f6;
	                     f12 = f7 - f10 / 2.0F;
	                     if(f12 < 0.0F)
	                         f12 = 0.0F;
	                     float f13 = f12 + f10;
	                     float f14;
	                     if(f13 > (float)getWidth())
	                         f12 = f4 - f10;
	                     else
	                         f4 = f13;
	                     f14 = f5 + f11;
	                     if(f14 > (float)getHeight())
	                         f5 = f3 - f11;
	                     else
	                         f3 = f14;
	                     f15 = f12 + (float)sTagTextPadding;
	                     f16 = f5 + (float)sTagTextPadding;
	                     mTagNameBackground.set(f12, f5, f4, f3);
	                     canvas.drawRoundRect(mTagNameBackground, 3F, 3F, sTagTextBackgroundPaint);
	                     canvas.drawText(mTagName, 0, mTagName.length(), f15, f16 - sTagTextPaint.ascent(), sTagTextPaint);
	                 }
	             }
	             if(mAllowCrop)
	             {
	                 l = canvas.getSaveCount();
	                 canvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), sCropDimPaint);
	                 canvas.save();
	                 canvas.clipRect(mCropRect);
	                 if(mDrawMatrix != null)
	                     canvas.concat(mDrawMatrix);
	                 mDrawable.draw(canvas);
	                 canvas.restoreToCount(l);
	                 canvas.drawRect(mCropRect, sCropPaint);
	             }
	        } else { 
	        	if(mVideoReady)
	                bitmap = sVideoImage;
	            else
	                bitmap = sVideoNotReadyImage;
	            l3 = (getWidth() - bitmap.getWidth()) / 2;
	            i4 = (getHeight() - bitmap.getHeight()) / 2;
	            f17 = l3;
	            f18 = i4;
	            canvas.drawBitmap(bitmap, f17, f18, null);
	        }
        }
        
        int i1 = getHeight() - sPhotoOverlayBottomPadding;
        if(mFullScreen && mCommentText != null && !mAllowCrop)
        {
            int k2 = (int)(sCommentCountPaint.ascent() - sCommentCountPaint.descent());
            int l2 = Math.max(sCommentBitmap.getHeight(), k2);
            int i3 = getWidth() - sPhotoOverlayRightPadding - sCommentCountTextWidth;
            int j3 = i1 - l2;
            canvas.drawText(mCommentText, i3, (float)j3 - sCommentCountPaint.ascent(), sCommentCountPaint);
            int k3 = i3 - (sCommentCountLeftMargin + sCommentBitmap.getWidth());
            canvas.drawBitmap(sCommentBitmap, k3, j3, null);
            i1 = j3 - sPlusOneBottomMargin;
        }
        if(mFullScreen && mPlusOneText != null && !mAllowCrop)
        {
            int j1 = (int)(sPlusOneCountPaint.ascent() - sPlusOneCountPaint.descent());
            int k1 = Math.max(sPlusOneBitmap.getHeight(), j1);
            int l1 = getWidth() - sPhotoOverlayRightPadding - sPlusOneCountTextWidth;
            int i2 = i1 - k1;
            canvas.drawText(mPlusOneText, l1, (float)i2 - sPlusOneCountPaint.ascent(), sPlusOneCountPaint);
            int j2 = l1 - (sPlusOneCountLeftMargin + sPlusOneBitmap.getWidth());
            canvas.drawBitmap(sPlusOneBitmap, j2, i2, null);
        }
        return;
        
    }

    public boolean onFling(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1)
    {
        if(mTransformsEnabled)
        {
            if(mTransformVerticalOnly)
                f = 0.0F;
            if(!mFlingDebounce)
                mTranslateRunnable.start(f, f1);
            mFlingDebounce = false;
        }
        return true;
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1;
        int j1;
        int k1;
        int l1;
        float f;
        super.onLayout(flag, i, j, k, l);
        mHaveLayout = true;
        i1 = getWidth();
        j1 = getHeight();
        if(!mAllowCrop) {
        	configureBounds(flag);
            return;
        } else { 
        	if(2 ==  mCropMode) {
        		k1 = i1 - (int)(0.1F * (float)i1);
                l1 = sCropSizeCoverWidth;
                f = 5.222222F;
        	} else {
    	        k1 = Math.min(i1, j1);
    	        l1 = sCropSizeProfile;
    	        f = 1.0F;
        	}
        	
        	mCropSizeWidth = Math.min(l1, k1);
            mCropSizeHeight = (int)((float)mCropSizeWidth / f);
            int i2 = (i1 - mCropSizeWidth) / 2;
            int j2 = (j1 - mCropSizeHeight) / 2;
            int k2 = i2 + mCropSizeWidth;
            int l2 = j2 + mCropSizeHeight;
            mCropRect.set(i2, j2, k2, l2);
            configureBounds(flag);
            return;
        }
    }

    public void onLongPress(MotionEvent motionevent)
    {
    }

    protected void onMeasure(int i, int j)
    {
        if(mFixedHeight != -1)
        {
            super.onMeasure(i, android.view.View.MeasureSpec.makeMeasureSpec(mFixedHeight, 0x80000000));
            setMeasuredDimension(getMeasuredWidth(), mFixedHeight);
        } else
        {
            super.onMeasure(i, j);
        }
    }

    public final void onResourceStatusChange(Resource resource) {
        // TODO
    }

    public boolean onScale(ScaleGestureDetector scalegesturedetector) {
        if(!mTransformNoScaling) {
        	float f = scalegesturedetector.getScaleFactor() - 1.0F;
            if(f < 0.0F && mScaleFactor > 0.0F || f > 0.0F && mScaleFactor < 0.0F)
                mScaleFactor = 0.0F;
            mScaleFactor = f + mScaleFactor;
            if(mTransformsEnabled && Math.abs(mScaleFactor) > 0.04F)
            {
                mIsDoubleTouch = false;
                scale(getScale() * scalegesturedetector.getScaleFactor(), scalegesturedetector.getFocusX(), scalegesturedetector.getFocusY());
            }
        }
        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector scalegesturedetector)
    {
        if(mTransformsEnabled)
        {
            mScaleRunnable.stop();
            mIsDoubleTouch = true;
            mScaleFactor = 0.0F;
        }
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector scalegesturedetector)
    {
        if(mTransformsEnabled && mIsDoubleTouch)
        {
            mDoubleTapDebounce = true;
            resetTransformations();
        }
        mFlingDebounce = true;
    }

    public boolean onScroll(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1)
    {
        long l = Math.abs(motionevent1.getEventTime() - mLastTwoFingerUp);
        if(mTransformVerticalOnly)
            f = 0.0F;
        if(l > 400L && mTransformsEnabled)
            translate(-f, -f1);
        return true;
    }

    public void onShowPress(MotionEvent motionevent)
    {
    }

    public boolean onSingleTapConfirmed(MotionEvent motionevent)
    {
        if(mExternalClickListener != null && !mIsDoubleTouch)
            mExternalClickListener.onClick(this);
        mIsDoubleTouch = false;
        return true;
    }

    public boolean onSingleTapUp(MotionEvent motionevent)
    {
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionevent) {
        if(mScaleGetureDetector != null && mGestureDetector != null) {
        	mScaleGetureDetector.onTouchEvent(motionevent);
            mGestureDetector.onTouchEvent(motionevent);
            if(6 == motionevent.getActionMasked()) {
            	if(motionevent.getPointerCount() == 2)
                    mLastTwoFingerUp = motionevent.getEventTime();
                else
                if(motionevent.getPointerCount() == 1)
                    mLastTwoFingerUp = 0L;
            }
            
            switch(motionevent.getAction())
            {
            case 1: // '\001'
            case 3: // '\003'
                if(!mTranslateRunnable.mRunning)
                    snap();
                break;
            }
        }
        return true;
    }

    public final void resetTransformations()
    {
        mMatrix.set(mOriginalMatrix);
        invalidate();
    }

    public void setCommentCount(int i)
    {
        String s = mCommentText;
        if(i >= 0) {
        	if(i == 0)
                mCommentText = null;
            else
            if(i > 99)
                mCommentText = getResources().getString(R.string.ninety_nine_plus);
            else
                mCommentText = Integer.toString(i);
            if(!TextUtils.equals(s, mCommentText))
                invalidate();
        }
    }

    public void setCropMode(int i)
    {
        boolean flag;
        if(i != 0)
            flag = true;
        else
            flag = false;
        if(flag && mHaveLayout)
            throw new IllegalArgumentException("Cannot set crop after view has been laid out");
        if(!flag && mAllowCrop)
            throw new IllegalArgumentException("Cannot unset crop mode");
        mAllowCrop = flag;
        mCropMode = i;
        if(mCropMode == 2)
        {
            mTransformVerticalOnly = true;
            mTransformNoScaling = true;
            mDoubleTapToZoomEnabled = false;
        }
    }

    public void setCropModeCoverPhotoOffset(Integer integer)
    {
        mCoverPhotoOffset = integer;
    }

    public void setFixedHeight(int i)
    {
        boolean flag;
        if(i != mFixedHeight)
            flag = true;
        else
            flag = false;
        mFixedHeight = i;
        setMeasuredDimension(getMeasuredWidth(), mFixedHeight);
        if(flag)
        {
            configureBounds(true);
            requestLayout();
        }
    }

    public void setFullScreen(boolean flag, boolean flag1)
    {
        if(flag != mFullScreen)
        {
            mFullScreen = flag;
            if(!mFullScreen)
            {
                mScaleRunnable.stop();
                mTranslateRunnable.stop();
                mRotateRunnable.stop();
            }
            requestLayout();
            invalidate();
        }
    }

    public void setOnClickListener(android.view.View.OnClickListener onclicklistener)
    {
        mExternalClickListener = onclicklistener;
    }

    public void setOnImageListener(OnImageListener onimagelistener)
    {
        mImageListener = onimagelistener;
    }

    public void setPlusOneCount(int i) {
        String s = mPlusOneText;
        if(i >= 0) {
        	if(i == 0)
                mPlusOneText = null;
            else
            if(i > 99)
                mPlusOneText = getResources().getString(R.string.ninety_nine_plus);
            else
                mPlusOneText = Integer.toString(i);
            if(!TextUtils.equals(s, mPlusOneText))
                invalidate();
        }
    }

    public void setVideoBlob(byte abyte0[])
    {
        mVideoBlob = abyte0;
        if(abyte0 != null)
        {
            DataVideo datavideo = (DataVideo)JsonUtil.fromByteArray(abyte0, DataVideo.class);
            boolean flag;
            if(TextUtils.equals("FINAL", datavideo.status) || TextUtils.equals("READY", datavideo.status))
                flag = true;
            else
                flag = false;
            mVideoReady = flag;
        }
    }

    public void setVisibility(int i)
    {
        super.setVisibility(i);
        if(mDrawable != null)
        {
            Drawable drawable = mDrawable;
            boolean flag;
            if(i == 0)
                flag = true;
            else
                flag = false;
            drawable.setVisible(flag, false);
        }
    }

    public final void showTagShape()
    {
        mShowTagShape = true;
        invalidate();
    }

    public final void unbindResources()
    {
        if(mStaticResource != null)
        {
            mStaticResource.unregister(this);
            mStaticResource = null;
        }
        if(mAnimatedResource != null)
        {
            mAnimatedResource.unregister(this);
            mAnimatedResource = null;
        }
    }

    protected boolean verifyDrawable(Drawable drawable)
    {
        boolean flag;
        if(mDrawable == drawable || super.verifyDrawable(drawable))
            flag = true;
        else
            flag = false;
        return flag;
    }
	
	public static interface OnImageListener {

        public abstract void onImageLoadFinished(PhotoHeaderView photoheaderview);

        public abstract void onImageScaled(float f);
    }
	
	private static final class RotateRunnable implements Runnable {
		
		private float mAppliedRotation;
	    private final PhotoHeaderView mHeader;
	    private long mLastRuntime;
	    private boolean mRunning;
	    private boolean mStop;
	    private float mTargetRotation;
	    private float mVelocity;

	    public RotateRunnable(PhotoHeaderView photoheaderview)
	    {
	        mHeader = photoheaderview;
	    }

	    public final void run() {
	    	if(mStop) {
	    		return;
	    	}
	    	
	        if(mAppliedRotation != mTargetRotation)
	        {
	            long l = System.currentTimeMillis();
	            long l1;
	            float f;
	            if(mLastRuntime != -1L)
	                l1 = l - mLastRuntime;
	            else
	                l1 = 0L;
	            f = mVelocity * (float)l1;
	            if(mAppliedRotation < mTargetRotation && f + mAppliedRotation > mTargetRotation || mAppliedRotation > mTargetRotation && f + mAppliedRotation < mTargetRotation)
	                f = mTargetRotation - mAppliedRotation;
	            mHeader.mRotation = 0;
	            mAppliedRotation = f + mAppliedRotation;
	            if(mAppliedRotation == mTargetRotation)
	                stop();
	            mLastRuntime = l;
	        }
	        if(!mStop)
	            mHeader.post(this);
	    }

	    public final void start(float f)
	    {
	        if(!mRunning)
	        {
	            mTargetRotation = f;
	            mVelocity = mTargetRotation / 500F;
	            mAppliedRotation = 0.0F;
	            mLastRuntime = -1L;
	            mStop = false;
	            mRunning = true;
	            mHeader.post(this);
	        }
	    }
	
	    public final void stop()
	    {
	        mRunning = false;
	        mStop = true;
	    }
	}
	
	private static final class ScaleRunnable implements Runnable {
		
		private float mCenterX;
	    private float mCenterY;
	    private final PhotoHeaderView mHeader;
	    private boolean mRunning;
	    private float mStartScale;
	    private long mStartTime;
	    private boolean mStop;
	    private float mTargetScale;
	    private float mVelocity;
	    private boolean mZoomingIn;

	    public ScaleRunnable(PhotoHeaderView photoheaderview)
	    {
	        mHeader = photoheaderview;
	    }

	    public final void run() {
	    	if(mStop) {
	    		return;
	    	}
	    	
	    	long l = System.currentTimeMillis() - mStartTime;
            float f = mStartScale + mVelocity * (float)l;
            mHeader.scale(f, mCenterX, mCenterY);
            if(f != mTargetScale)
            {
                boolean flag = mZoomingIn;
                boolean flag1;
                if(f > mTargetScale)
                    flag1 = true;
                else
                    flag1 = false;
                if(flag == flag1) {
                	mHeader.scale(mTargetScale, mCenterX, mCenterY);
                    stop();
                } else {
                	if(!mStop)
        	            mHeader.post(this);
                }
            }
	    }

	    public final boolean start(float f, float f1, float f2, float f3)
	    {
	        boolean flag = mRunning;
	        boolean flag1 = false;
	        if(!flag)
	        {
	            mCenterX = f2;
	            mCenterY = f3;
	            mTargetScale = f1;
	            mStartTime = System.currentTimeMillis();
	            mStartScale = f;
	            boolean flag2;
	            if(mTargetScale > mStartScale)
	                flag2 = true;
	            else
	                flag2 = false;
	            mZoomingIn = flag2;
	            mVelocity = (mTargetScale - mStartScale) / 300F;
	            mRunning = true;
	            mStop = false;
	            mHeader.post(this);
	            flag1 = true;
	        }
	        return flag1;
	    }

	    public final void stop()
	    {
	        mRunning = false;
	        mStop = true;
	    }
    
	}

	private static final class SnapRunnable implements Runnable {
	
		private final PhotoHeaderView mHeader;
	    private boolean mRunning;
	    private long mStartRunTime;
	    private boolean mStop;
	    private float mTranslateX;
	    private float mTranslateY;

	    public SnapRunnable(PhotoHeaderView photoheaderview)
	    {
	        mStartRunTime = -1L;
	        mHeader = photoheaderview;
	    }

		public final void run() {
	    	if(mStop) {
	    		return;
	    	}
	    	
	    	float f;
	        float f1;
	        float f2;
	        long l = System.currentTimeMillis();
	        if(mStartRunTime != -1L)
	            f = l - mStartRunTime;
	        else
	            f = 0.0F;
	        if(mStartRunTime == -1L)
	            mStartRunTime = l;
	        if(f < 100F) { 
	        	f1 = 10F * (mTranslateX / (100F - f));
	            f2 = 10F * (mTranslateY / (100F - f));
	            if(Math.abs(f1) > Math.abs(mTranslateX) || f1 == (0.0F / 0.0F))
	                f1 = mTranslateX;
	            if(Math.abs(f2) > Math.abs(mTranslateY) && f2 != (0.0F / 0.0F)) 
	            	f2 = mTranslateY;
	        } else { 
	        	f1 = mTranslateX;
	        	f2 = mTranslateY;
	        }
	        
	        mHeader.translate(f1, f2);
	        mTranslateX = mTranslateX - f1;
	        mTranslateY = mTranslateY - f2;
	        if(mTranslateX == 0.0F && mTranslateY == 0.0F)
	            stop();
	        if(!mStop)
	            mHeader.post(this);
	    }

	    public final boolean start(float f, float f1)
	    {
	        boolean flag = mRunning;
	        boolean flag1 = false;
	        if(!flag)
	        {
	            mStartRunTime = -1L;
	            mTranslateX = f;
	            mTranslateY = f1;
	            mStop = false;
	            mRunning = true;
	            mHeader.postDelayed(this, 250L);
	            flag1 = true;
	        }
	        return flag1;
	    }
	
	    public final void stop()
	    {
	        mRunning = false;
	        mStop = true;
	    }
	}

	private static final class TranslateRunnable implements Runnable {

		private final PhotoHeaderView mHeader;
		private long mLastRunTime;
		private boolean mRunning;
		private boolean mStop;
		private float mVelocityX;
		private float mVelocityY;


	    public TranslateRunnable(PhotoHeaderView photoheaderview)
	    {
	        mLastRunTime = -1L;
	        mHeader = photoheaderview;
	    }
    
	    public final void run() {
	    	if(mStop) {
	    		return;
	    	}
	    	
	    	float f1;
            long l = System.currentTimeMillis();
            float f;
            boolean flag;
            if(mLastRunTime != -1L)
                f = (float)(l - mLastRunTime) / 1000F;
            else
                f = 0.0F;
            flag = mHeader.translate(f * mVelocityX, f * mVelocityY);
            mLastRunTime = l;
            f1 = 1000F * f;
            if(mVelocityX > 0.0F)
            {
                mVelocityX = mVelocityX - f1;
                if(mVelocityX < 0.0F)
                    mVelocityX = 0.0F;
            } else
            {
                mVelocityX = f1 + mVelocityX;
                if(mVelocityX > 0.0F)
                    mVelocityX = 0.0F;
            }
            if(mVelocityY <= 0.0F) {
            	mVelocityY = f1 + mVelocityY;
                if(mVelocityY > 0.0F)
                    mVelocityY = 0.0F;
            } else {
            	 mVelocityY = mVelocityY - f1;
                 if(mVelocityY < 0.0F)
                     mVelocityY = 0.0F;
            }
           
            if(mVelocityX == 0.0F && mVelocityY == 0.0F || !flag)
            {
                stop();
                mHeader.snap();
            }
            if(!mStop)
                mHeader.post(this);
	    }

	    public final boolean start(float f, float f1)
	    {
	        boolean flag = mRunning;
	        boolean flag1 = false;
	        if(!flag)
	        {
	            mLastRunTime = -1L;
	            mVelocityX = f;
	            mVelocityY = f1;
	            mStop = false;
	            mRunning = true;
	            mHeader.post(this);
	            flag1 = true;
	        }
	        return flag1;
	    }
	
	    public final void stop()
	    {
	        mRunning = false;
	        mStop = true;
	    }

	}
}
