/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.DbEmbedSkyjam;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;

/**
 * 
 * @author sihai
 *
 */
public class SkyjamCardView extends StreamCardView implements ClickableImageButton.ClickableImageButtonListener {

	protected static Paint sAlbumBorderPaint;
    protected static Bitmap sEmptyThumbnailBitmap;
    private static Bitmap sGoogleMusicBitmap;
    protected static TextPaint sListenBuyTextPaint;
    protected static TextPaint sNonTitleTextPaint;
    private static Bitmap sPlayOverlayBitmap;
    private static boolean sSkyjamCardViewInitialized;
    protected static int sSkyjamMediaBorderDimension;
    protected static int sSkyjamMediaDimension;
    protected static CharSequence sSkyjamPlayButtonDescription;
    protected static TextPaint sTitleTextPaint;
    protected String mAlbum;
    protected StaticLayout mAlbumLayout;
    protected String mArtist;
    protected StaticLayout mArtistLayout;
    protected ClickableImageButton mAutoPlayButton;
    protected Resource mImageResource;
    protected StaticLayout mListenBuyLayout;
    protected String mThumbnailUrl;
    protected String mTitle;
    protected StaticLayout mTitleLayout;
    
    
    public SkyjamCardView(Context context)
    {
        this(context, null);
    }

    public SkyjamCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        if(!sSkyjamCardViewInitialized)
        {
            sSkyjamCardViewInitialized = true;
            Resources resources = context.getResources();
            TextPaint textpaint = new TextPaint();
            sTitleTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sTitleTextPaint.setColor(resources.getColor(R.color.card_skyjam_title));
            sTitleTextPaint.setTextSize(resources.getDimension(R.dimen.card_skyjam_title_text_size));
            TextPaintUtils.registerTextPaint(sTitleTextPaint, R.dimen.card_skyjam_title_text_size);
            TextPaint textpaint1 = new TextPaint();
            sNonTitleTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sNonTitleTextPaint.setColor(resources.getColor(R.color.card_skyjam_nontitle));
            sNonTitleTextPaint.setTextSize(resources.getDimension(R.dimen.card_skyjam_nontitle_text_size));
            TextPaintUtils.registerTextPaint(sNonTitleTextPaint, R.dimen.card_skyjam_nontitle_text_size);
            TextPaint textpaint2 = new TextPaint();
            sListenBuyTextPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sListenBuyTextPaint.setColor(resources.getColor(R.color.card_skyjam_listen_buy));
            sListenBuyTextPaint.setTextSize(resources.getDimension(R.dimen.card_skyjam_listen_buy_text_size));
            TextPaintUtils.registerTextPaint(sListenBuyTextPaint, R.dimen.card_skyjam_listen_buy_text_size);
            Paint paint = new Paint();
            sAlbumBorderPaint = paint;
            paint.setColor(resources.getColor(R.color.card_skyjam_album_border));
            sAlbumBorderPaint.setStrokeWidth(resources.getDimension(R.dimen.card_skyjam_album_border_size));
            sAlbumBorderPaint.setStyle(android.graphics.Paint.Style.STROKE);
            sEmptyThumbnailBitmap = ImageUtils.decodeResource(resources, R.drawable.empty_thumbnail);
            sGoogleMusicBitmap = ImageUtils.decodeResource(resources, R.drawable.google_music);
            sPlayOverlayBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_play_overlay);
            sSkyjamMediaDimension = (int)resources.getDimension(R.dimen.card_skyjam_media_size);
            sSkyjamMediaBorderDimension = (int)resources.getDimension(R.dimen.card_skyjam_media_border_size);
            sSkyjamPlayButtonDescription = resources.getString(R.string.skyjam_content_play_button_description);
        }
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        int i1 = (int)((float)(l + sYDoublePadding) * getMediaHeightPercentage());
        drawMediaTopAreaStageWithTiledBackground(canvas, k, i1);
        int j1 = (sSkyjamMediaBorderDimension - sSkyjamMediaDimension) / 2;
        canvas.drawRect(i, j, i + sSkyjamMediaBorderDimension, j + sSkyjamMediaBorderDimension, sAlbumBorderPaint);
        Bitmap bitmap;
        int k1;
        int l1;
        int i2;
        int j2;
        if(mImageResource == null)
            bitmap = null;
        else
            bitmap = (Bitmap)mImageResource.getResource();
        if(bitmap == null)
            bitmap = sEmptyThumbnailBitmap;
        sDrawRect.set(i + j1, j + j1, i + j1 + sSkyjamMediaDimension, j + j1 + sSkyjamMediaDimension);
        canvas.drawBitmap(bitmap, null, sDrawRect, sResizePaint);
        if(mAutoPlayButton != null)
            mAutoPlayButton.draw(canvas);
        drawMediaTopAreaShadow(canvas, k, l);
        k1 = i + (sSkyjamMediaBorderDimension + sContentXPadding);
        l1 = k - (sSkyjamMediaBorderDimension + sContentXPadding);
        if(mTitleLayout != null)
        {
            canvas.translate(k1, j);
            mTitleLayout.draw(canvas);
            canvas.translate(-k1, -j);
            j += mTitleLayout.getHeight();
        }
        if(mArtistLayout != null)
        {
            canvas.translate(k1, j);
            mArtistLayout.draw(canvas);
            canvas.translate(-k1, -j);
            j += mArtistLayout.getHeight();
        }
        if(mAlbumLayout != null)
        {
            canvas.translate(k1, j);
            mAlbumLayout.draw(canvas);
            canvas.translate(-k1, -j);
            j += mAlbumLayout.getHeight();
        }
        if(mListenBuyLayout != null)
        {
            canvas.translate(k1, j);
            mListenBuyLayout.draw(canvas);
            canvas.translate(-k1, -j);
            j += mListenBuyLayout.getHeight();
        }
        if(i1 - j >= sGoogleMusicBitmap.getHeight())
        {
            canvas.drawBitmap(sGoogleMusicBitmap, k1, j, null);
            sGoogleMusicBitmap.getHeight();
        }
        i2 = k1 - (sSkyjamMediaBorderDimension + sContentXPadding);
        j2 = l1 + (sSkyjamMediaBorderDimension + sContentXPadding);
        drawPlusOneBar(canvas);
        drawMediaBottomArea(canvas, i2, j2, l);
        drawCornerIcon(canvas);
        return l;
    }

    public final void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamCardView.ViewedListener viewedlistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener)
    {
        super.init(cursor, i, j, onclicklistener, itemclicklistener, viewedlistener, streamplusbarclicklistener, streammediaclicklistener);
        byte abyte0[] = cursor.getBlob(23);
        if(abyte0 != null)
        {
            DbEmbedSkyjam dbembedskyjam = DbEmbedSkyjam.deserialize(abyte0);
            if(dbembedskyjam != null)
            {
                if(dbembedskyjam.isAlbum())
                {
                    mTitle = dbembedskyjam.getAlbum();
                } else
                {
                    mTitle = dbembedskyjam.getSong();
                    mAlbum = dbembedskyjam.getAlbum();
                }
                mArtist = dbembedskyjam.getArtist();
                mThumbnailUrl = dbembedskyjam.getImageUrl();
            }
        }
    }

    protected final int layoutElements(int i, int j, int k, int l)
    {
        int i1 = (int)((float)(l + sYDoublePadding) * getMediaHeightPercentage());
        int j1 = j;
        mBackgroundRect.set(0, i1, getMeasuredWidth(), getMeasuredHeight());
        int k1 = (sSkyjamMediaBorderDimension - sSkyjamMediaDimension) / 2;
        if(mAlbum != null)
        {
            removeClickableItem(mAutoPlayButton);
            mAutoPlayButton = new ClickableImageButton(getContext(), sPlayOverlayBitmap, null, this, sSkyjamPlayButtonDescription);
            mAutoPlayButton.setPosition(i + k1, (j + k1 + sSkyjamMediaDimension) - sPlayOverlayBitmap.getHeight());
            addClickableItem(mAutoPlayButton);
        }
        int l1 = i + (sSkyjamMediaBorderDimension + sContentXPadding);
        int i2 = k - (sSkyjamMediaBorderDimension + sContentXPadding);
        if(!TextUtils.isEmpty(mTitle))
        {
            int k3 = (i1 - j) / (int)(sTitleTextPaint.descent() - sTitleTextPaint.ascent());
            if(k3 > 0)
            {
                mTitleLayout = TextPaintUtils.createConstrainedStaticLayout(sTitleTextPaint, mTitle, i2, k3);
                j += mTitleLayout.getHeight();
            }
        }
        if(!TextUtils.isEmpty(mArtist))
        {
            int j3 = (i1 - j) / (int)(sNonTitleTextPaint.descent() - sNonTitleTextPaint.ascent());
            if(j3 > 0)
            {
                mArtistLayout = TextPaintUtils.createConstrainedStaticLayout(sNonTitleTextPaint, mArtist, i2, j3);
                j += mArtistLayout.getHeight();
            }
        }
        if(!TextUtils.isEmpty(mAlbum))
        {
            int i3 = (i1 - j) / (int)(sNonTitleTextPaint.descent() - sNonTitleTextPaint.ascent());
            if(i3 > 0)
            {
                mAlbumLayout = TextPaintUtils.createConstrainedStaticLayout(sNonTitleTextPaint, mAlbum, i2, i3);
                j += mAlbumLayout.getHeight();
            }
        }
        int j2 = (i1 - j) / (int)(sListenBuyTextPaint.descent() - sListenBuyTextPaint.ascent());
        if(j2 > 0)
        {
            mListenBuyLayout = TextPaintUtils.createConstrainedStaticLayout(sListenBuyTextPaint, getResources().getString(R.string.skyjam_listen_buy), i2, j2);
            mListenBuyLayout.getHeight();
        }
        int k2 = l1 - (sSkyjamMediaBorderDimension + sContentXPadding);
        int l2 = i2 + (sSkyjamMediaBorderDimension + sContentXPadding);
        createPlusOneBar(k2, (i1 + sTopBorderPadding) - sYPadding, l2);
        createMediaBottomArea(k2, j1, l2, l);
        return l;
    }

    protected final void onBindResources()
    {
        super.onBindResources();
        if(mThumbnailUrl != null)
            mImageResource = ImageResourceManager.getInstance(getContext()).getMedia(new MediaRef(mThumbnailUrl, MediaRef.MediaType.IMAGE), 2, this);
    }

    public final void onClickableImageButtonClick(ClickableImageButton clickableimagebutton)
    {
        if(clickableimagebutton == mAutoPlayButton)
        {
            Context context = getContext();
            Intent intent = Intents.getStreamOneUpActivityIntent(context, EsService.getActiveAccount(context), mActivityId);
            intent.putExtra("auto_play_music", true);
            context.startActivity(intent);
        }
    }

    public void onRecycle()
    {
        super.onRecycle();
        mTitle = null;
        mArtist = null;
        mAlbum = null;
        mThumbnailUrl = null;
        mTitleLayout = null;
        mArtistLayout = null;
        mAlbumLayout = null;
        mListenBuyLayout = null;
        mAutoPlayButton = null;
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
