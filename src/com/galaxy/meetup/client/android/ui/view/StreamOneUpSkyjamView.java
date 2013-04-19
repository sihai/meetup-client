/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.MediaImageRequest;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ImageCache.OnMediaImageChangeListener;
import com.galaxy.meetup.client.android.service.SkyjamPlaybackService;
import com.galaxy.meetup.client.android.service.SkyjamPlaybackService.SkyjamPlaybackListener;
import com.galaxy.meetup.client.android.ui.view.ClickableRect.ClickableRectListener;
import com.galaxy.meetup.client.util.AccessibilityUtils;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.StringUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpSkyjamView extends View implements
		OnMediaImageChangeListener, SkyjamPlaybackListener, ClickableRectListener {

	private static int sActionBarHeight;
    protected static TextPaint sDefaultTextPaint;
    private static Bitmap sEmptyImage;
    private static Bitmap sGoogleMusic;
    private static ImageCache sImageCache;
    protected static int sMaxWidth;
    protected static int sNameMargin;
    private static Bitmap sPlayIcon;
    protected static int sPlayStopButtonPadding;
    private static Paint sPreviewPaint;
    private static Paint sResizePaint;
    private static CharSequence sSkyjamPlayButtonDesc;
    private static Bitmap sStopIcon;
    private static boolean sStreamOneUpSkyjamViewInitialized;
    private static Bitmap sTagIcon;
    private static Paint sTagPaint;
    protected static TextPaint sTagTextPaint;
    private Bitmap mActionIcon;
    private PointF mActionIconPoint;
    private ClickableRect mActionRect;
    private String mActivityId;
    private final Set mClickableItems;
    private ClickableItem mCurrentClickableItem;
    private Rect mGoogleMusicRect;
    private MediaImage mImage;
    private Rect mImageRect;
    private String mImageUrl;
    private boolean mIsAlbum;
    private String mMusicUrl;
    private RectF mPreviewBackground;
    private String mPreviewStatus;
    private PointF mPreviewStatusPoint;
    private RectF mTagBackground;
    private Rect mTagIconRect;
    private PositionedStaticLayout mTagLayout;
    private String mTagTitle;
    
    public StreamOneUpSkyjamView(Context context)
    {
        super(context);
        mClickableItems = new HashSet();
        Context context1 = getContext();
        if(!sStreamOneUpSkyjamViewInitialized)
        {
            sStreamOneUpSkyjamViewInitialized = true;
            sImageCache = ImageCache.getInstance(context1);
            Resources resources = getResources();
            sTagIcon = ImageUtils.decodeResource(resources, R.drawable.ic_metadata_music);
            sEmptyImage = ImageUtils.decodeResource(resources, R.drawable.empty_thumbnail);
            sGoogleMusic = ImageUtils.decodeResource(resources, R.drawable.google_music);
            sPlayIcon = ImageUtils.resizeToSquareBitmap(ImageUtils.decodeResource(resources, R.drawable.ic_play), sGoogleMusic.getHeight());
            sStopIcon = ImageUtils.resizeToSquareBitmap(ImageUtils.decodeResource(resources, R.drawable.ic_stop), sGoogleMusic.getHeight());
            sActionBarHeight = (int)resources.getDimension(R.dimen.host_action_bar_height);
            sMaxWidth = (int)resources.getDimension(R.dimen.stream_one_up_list_max_width);
            sNameMargin = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_name_margin);
            sPlayStopButtonPadding = (int)resources.getDimension(R.dimen.stream_one_up_stage_skyjam_play_stop_padding);
            sResizePaint = new Paint(2);
            Paint paint = new Paint();
            sTagPaint = paint;
            paint.setAntiAlias(true);
            sTagPaint.setColor(resources.getColor(R.color.stream_one_up_stage_skyjam_tag_background));
            sTagPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sPreviewPaint = paint1;
            paint1.setAntiAlias(true);
            sPreviewPaint.setColor(resources.getColor(R.color.stream_one_up_stage_skyjam_preview_background));
            sPreviewPaint.setStyle(android.graphics.Paint.Style.FILL);
            TextPaint textpaint = new TextPaint();
            sDefaultTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDefaultTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_skyjam_preview));
            sDefaultTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sDefaultTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            TextPaint textpaint1 = new TextPaint();
            sTagTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sTagTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_tag));
            sTagTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sTagTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            sSkyjamPlayButtonDesc = resources.getString(R.string.skyjam_content_play_button_description);
        }
    }

    public StreamOneUpSkyjamView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mClickableItems = new HashSet();
        Context context1 = getContext();
        if(!sStreamOneUpSkyjamViewInitialized)
        {
            sStreamOneUpSkyjamViewInitialized = true;
            sImageCache = ImageCache.getInstance(context1);
            Resources resources = getResources();
            sTagIcon = ImageUtils.decodeResource(resources, R.drawable.ic_metadata_music);
            sEmptyImage = ImageUtils.decodeResource(resources, R.drawable.empty_thumbnail);
            sGoogleMusic = ImageUtils.decodeResource(resources, R.drawable.google_music);
            sPlayIcon = ImageUtils.resizeToSquareBitmap(ImageUtils.decodeResource(resources, R.drawable.ic_play), sGoogleMusic.getHeight());
            sStopIcon = ImageUtils.resizeToSquareBitmap(ImageUtils.decodeResource(resources, R.drawable.ic_stop), sGoogleMusic.getHeight());
            sActionBarHeight = (int)resources.getDimension(R.dimen.host_action_bar_height);
            sMaxWidth = (int)resources.getDimension(R.dimen.stream_one_up_list_max_width);
            sNameMargin = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_name_margin);
            sPlayStopButtonPadding = (int)resources.getDimension(R.dimen.stream_one_up_stage_skyjam_play_stop_padding);
            sResizePaint = new Paint(2);
            Paint paint = new Paint();
            sTagPaint = paint;
            paint.setAntiAlias(true);
            sTagPaint.setColor(resources.getColor(R.color.stream_one_up_stage_skyjam_tag_background));
            sTagPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sPreviewPaint = paint1;
            paint1.setAntiAlias(true);
            sPreviewPaint.setColor(resources.getColor(R.color.stream_one_up_stage_skyjam_preview_background));
            sPreviewPaint.setStyle(android.graphics.Paint.Style.FILL);
            TextPaint textpaint = new TextPaint();
            sDefaultTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDefaultTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_skyjam_preview));
            sDefaultTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sDefaultTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            TextPaint textpaint1 = new TextPaint();
            sTagTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sTagTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_tag));
            sTagTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sTagTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            sSkyjamPlayButtonDesc = resources.getString(R.string.skyjam_content_play_button_description);
        }
    }

    public StreamOneUpSkyjamView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mClickableItems = new HashSet();
        Context context1 = getContext();
        if(!sStreamOneUpSkyjamViewInitialized)
        {
            sStreamOneUpSkyjamViewInitialized = true;
            sImageCache = ImageCache.getInstance(context1);
            Resources resources = getResources();
            sTagIcon = ImageUtils.decodeResource(resources, R.drawable.ic_metadata_music);
            sEmptyImage = ImageUtils.decodeResource(resources, R.drawable.empty_thumbnail);
            sGoogleMusic = ImageUtils.decodeResource(resources, R.drawable.google_music);
            sPlayIcon = ImageUtils.resizeToSquareBitmap(ImageUtils.decodeResource(resources, R.drawable.ic_play), sGoogleMusic.getHeight());
            sStopIcon = ImageUtils.resizeToSquareBitmap(ImageUtils.decodeResource(resources, R.drawable.ic_stop), sGoogleMusic.getHeight());
            sActionBarHeight = (int)resources.getDimension(R.dimen.host_action_bar_height);
            sMaxWidth = (int)resources.getDimension(R.dimen.stream_one_up_list_max_width);
            sNameMargin = (int)resources.getDimension(R.dimen.stream_one_up_stage_hangout_name_margin);
            sPlayStopButtonPadding = (int)resources.getDimension(R.dimen.stream_one_up_stage_skyjam_play_stop_padding);
            sResizePaint = new Paint(2);
            Paint paint = new Paint();
            sTagPaint = paint;
            paint.setAntiAlias(true);
            sTagPaint.setColor(resources.getColor(R.color.stream_one_up_stage_skyjam_tag_background));
            sTagPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sPreviewPaint = paint1;
            paint1.setAntiAlias(true);
            sPreviewPaint.setColor(resources.getColor(R.color.stream_one_up_stage_skyjam_preview_background));
            sPreviewPaint.setStyle(android.graphics.Paint.Style.FILL);
            TextPaint textpaint = new TextPaint();
            sDefaultTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDefaultTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_skyjam_preview));
            sDefaultTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sDefaultTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            TextPaint textpaint1 = new TextPaint();
            sTagTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sTagTextPaint.setColor(resources.getColor(R.color.stream_one_up_stage_tag));
            sTagTextPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_stage_default_text_size));
            TextPaintUtils.registerTextPaint(sTagTextPaint, R.dimen.stream_one_up_stage_default_text_size);
            sSkyjamPlayButtonDesc = resources.getString(R.string.skyjam_content_play_button_description);
        }
    }

    public final void bind(String s, String s1, String s2, String s3, String s4, String s5)
    {
        mClickableItems.clear();
        mCurrentClickableItem = null;
        mIsAlbum = TextUtils.isEmpty(s1);
        int i;
        if(mIsAlbum)
        {
            i = s4.indexOf("https://");
            if(i < 0)
                i = s4.indexOf("https://");
        } else
        {
            i = s3.indexOf("https://");
            if(i < 0)
                i = s3.indexOf("https://");
        }
        if(i >= 0)
        {
            mIsAlbum = TextUtils.isEmpty(s1);
            Context context;
            String s6;
            StringBuilder stringbuilder;
            if(mIsAlbum)
            {
                mMusicUrl = s4.substring(i);
            } else
            {
                mMusicUrl = s3.substring(i);
                if(mMusicUrl.contains("mode=inline"))
                    mMusicUrl = mMusicUrl.replace("mode=inline", "mode=streaming");
                else
                    mMusicUrl = (new StringBuilder()).append(mMusicUrl).append("&mode=streaming").toString();
            }
            mPreviewStatus = SkyjamPlaybackService.getPlaybackStatus(getContext(), mMusicUrl);
            context = getContext();
            if(mIsAlbum)
            {
                s6 = StringUtils.unescape(s);
            } else
            {
                int j = R.string.skyjam_from_the_album;
                Object aobj[] = new Object[1];
                aobj[0] = StringUtils.unescape(s).toUpperCase();
                s6 = context.getString(j, aobj);
            }
            if(!mIsAlbum)
                s6 = s1;
            mTagTitle = s6;
            if(!TextUtils.isEmpty(s2))
            {
                mImageUrl = s2;
                mImage = new MediaImage(this, new MediaImageRequest(s2, 3, 300));
                mImage.load();
            }
            stringbuilder = new StringBuilder(256);
            if(!TextUtils.isEmpty(s))
                stringbuilder.append(s).append('\n');
            if(!TextUtils.isEmpty(s1))
                stringbuilder.append(s1);
            setContentDescription(stringbuilder.toString());
            mActivityId = s5;
        }
        invalidate();
        requestLayout();
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent) {
        boolean flag = true;
        int i = (int)motionevent.getX();
        int j = (int)motionevent.getY();
        
        switch(motionevent.getAction()) {
	        case 0:
	        	for(Iterator iterator1 = mClickableItems.iterator(); iterator1.hasNext();)
	            {
	                ClickableItem clickableitem = (ClickableItem)iterator1.next();
	                if(clickableitem.handleEvent(i, j, 0))
	                {
	                    mCurrentClickableItem = clickableitem;
	                    invalidate();
	                    return flag;
	                }
	            }

	            flag = false;
	        	break;
	        case 1:
	        	mCurrentClickableItem = null;
	            for(Iterator iterator = mClickableItems.iterator(); iterator.hasNext(); ((ClickableItem)iterator.next()).handleEvent(i, j, 1));
	            invalidate();
	            flag = false;
	        	break;
	        case 2:
	        	flag = false;
	        	break;
	        case 3:
	        	if(mCurrentClickableItem != null)
	            {
	                mCurrentClickableItem.handleEvent(i, j, 3);
	                mCurrentClickableItem = null;
	                invalidate();
	            } else
	            {
	                flag = false;
	            }
	        	break;
        	default:
        		flag = false;
        		break;
        }
        
        return flag;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ImageCache _tmp = sImageCache;
        ImageCache.registerMediaImageChangeListener(this);
        SkyjamPlaybackService.registerListener(this);
    }

    public final void onClickableRectClick() {
        Context context = getContext();
        boolean flag;
        Intent intent;
        String s;
        if(!SkyjamPlaybackService.isPlaying(mMusicUrl))
            flag = true;
        else
            flag = false;
        intent = new Intent(context, SkyjamPlaybackService.class);
        if(flag)
            s = "com.google.android.apps.plus.service.SkyjamPlaybackService.PLAY";
        else
            s = "com.google.android.apps.plus.service.SkyjamPlaybackService.STOP";
        intent.setAction(s);
        intent.putExtra("music_account", EsService.getActiveAccount(context));
        intent.putExtra("music_url", mMusicUrl);
        intent.putExtra("song", mTagTitle);
        intent.putExtra("activity_id", mActivityId);
        context.startService(intent);
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        ImageCache _tmp = sImageCache;
        ImageCache.unregisterMediaImageChangeListener(this);
        SkyjamPlaybackService.unregisterListener(this);
    }

    protected void onDraw(Canvas canvas)
    {
        canvas.drawRoundRect(mTagBackground, 5F, 5F, sTagPaint);
        canvas.drawBitmap(sTagIcon, null, mTagIconRect, null);
        int i = mTagLayout.getLeft();
        int j = mTagLayout.getTop();
        canvas.translate(i, j);
        mTagLayout.draw(canvas);
        canvas.translate(-i, -j);
        if(mImage != null)
        {
            mImage.refreshIfInvalidated();
            Bitmap bitmap = mImage.getBitmap();
            if(bitmap == null)
                bitmap = sEmptyImage;
            canvas.drawBitmap(bitmap, null, mImageRect, sResizePaint);
        }
        if(mPreviewBackground != null)
        {
            canvas.drawRect(mPreviewBackground, sPreviewPaint);
            canvas.drawText(mPreviewStatus, mPreviewStatusPoint.x, mPreviewStatusPoint.y, sDefaultTextPaint);
            canvas.drawBitmap(mActionIcon, mActionIconPoint.x, mActionIconPoint.y, null);
            canvas.drawBitmap(sGoogleMusic, null, mGoogleMusicRect, null);
        }
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int value = android.view.View.MeasureSpec.getMode(i);
        int l = sMaxWidth;
        if(-2147483648 == value) {
        	l = Math.min(k, sMaxWidth);
        } else if(1073741824 == value) {
        	l = k;
        }
        
        super.onMeasure(android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000), j);
        int i1 = getPaddingLeft();
        int j1 = getPaddingTop();
        int k1 = getMeasuredWidth();
        int l1 = getMeasuredHeight();
        int i2 = k1 - i1 - getPaddingRight();
        int j2 = l1 - j1 - getPaddingBottom();
        int k2 = i1 + 13;
        int l2 = j1 + 13;
        int i3 = sTagIcon.getWidth();
        int j3 = sTagIcon.getHeight();
        int k3 = (int)sDefaultTextPaint.measureText(mTagTitle);
        int l3 = k3 + (i3 + 15);
        mTagBackground = new RectF(k2, l2, l3 + k2, l2 + 39);
        mTagLayout = new PositionedStaticLayout(mTagTitle, sTagTextPaint, k3, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        int i4 = mTagLayout.getHeight();
        int j4 = k2 + 5;
        int k4 = l2 + (39 - j3) / 2;
        mTagIconRect = new Rect(j4, k4, j4 + i3, k4 + j3);
        int l4 = j4 + (i3 + 5);
        int i5 = j1 + 13 + (39 - i4) / 2;
        mTagLayout.setPosition(l4, i5);
        int j5 = 13 + (j1 + 13 + (int)mTagBackground.height());
        int k5 = i2 - 52;
        int l5 = j1 + (j2 - j5);
        int i6;
        int j6;
        int k6;
        if(mImage != null)
        {
            int j9 = Math.min(l5, k5);
            mImage.refreshIfInvalidated();
            j6 = i1 + (26 + (k5 - j9) / 2);
            mImageRect = new Rect(j6, j5, j6 + j9, j5 + j9);
            i6 = j9;
        } else
        {
            i6 = k5;
            j6 = i1;
        }
        if(!mIsAlbum)
        {
            mPreviewBackground = new RectF(j6, j5, j6 + i6, j5 + 66);
            mClickableItems.remove(mActionRect);
            mActionRect = new ClickableRect(j6, j5 + sActionBarHeight, j6 + i6, j5 + 66, this, sSkyjamPlayButtonDesc);
            mClickableItems.add(mActionRect);
            Bitmap bitmap;
            int l6;
            int i7;
            int j7;
            int k7;
            int l7;
            int i8;
            int j8;
            int k8;
            int l8;
            int i9;
            if(SkyjamPlaybackService.isPlaying(mMusicUrl))
                bitmap = sStopIcon;
            else
                bitmap = sPlayIcon;
            mActionIcon = bitmap;
            l6 = mActionIcon.getHeight();
            i7 = 13 + (int)mPreviewBackground.left;
            j7 = (int)mPreviewBackground.top + (66 - l6) / 2;
            mActionIconPoint = new PointF(i7, j7);
            k7 = (int)(sDefaultTextPaint.descent() - sDefaultTextPaint.ascent());
            l7 = i7 + (13 + mActionIcon.getWidth());
            i8 = ((int)mPreviewBackground.top + (66 - k7) / 2) - (int)sDefaultTextPaint.ascent();
            mPreviewStatusPoint = new PointF(l7, i8);
            j8 = sGoogleMusic.getWidth();
            k8 = sGoogleMusic.getHeight();
            l8 = (int)mPreviewBackground.right - j8;
            i9 = (int)mPreviewBackground.top + (66 - k8) / 2;
            mGoogleMusicRect = new Rect(l8, i9, j8 + l8, k8 + i9);
        }
        k6 = 13 + (j1 + 13 + (int)mTagBackground.height());
        if(mImage != null)
            k6 += i6;
        else
        if(mPreviewBackground != null)
            k6 += 66;
        setMeasuredDimension(k1, k6 + getPaddingBottom());
        return;
    }

    public final void onMediaImageChanged(String s)
    {
        if(MediaImageRequest.areCanonicallyEqual(mImageUrl, s))
            mImage.invalidate();
        invalidate();
    }

    public final void onPlaybackStatusUpdate(String s, boolean flag, String s1)
    {
        Bitmap bitmap = mActionIcon;
        boolean flag1;
        if(flag && s != null && s.equals(mMusicUrl))
            mActionIcon = sStopIcon;
        else
            mActionIcon = sPlayIcon;
        if(mMusicUrl.equals(s) && !mPreviewStatus.equals(s1))
            flag1 = true;
        else
            flag1 = false;
        if(flag1)
            mPreviewStatus = s1;
        if(bitmap != mActionIcon || flag1)
            invalidate();
    }

    public final void processClick(float f, float f1)
    {
        if(mActionRect != null)
        {
            Rect rect = mActionRect.getRect();
            int ai[] = new int[2];
            getLocationOnScreen(ai);
            if(rect.contains((int)(f - (float)ai[0]), (int)(f1 - (float)ai[1])) || AccessibilityUtils.isAccessibilityEnabled(getContext()))
            {
                ClickableRect _tmp = mActionRect;
                onClickableRectClick();
            }
        }
    }

    public final void startAutoPlay()
    {
        if(!SkyjamPlaybackService.isPlaying(mMusicUrl))
            onClickableRectClick();
    }

}
