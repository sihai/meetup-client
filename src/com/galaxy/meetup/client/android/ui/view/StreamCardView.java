/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.animation.Interpolator;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.DbEmbedSquare;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.DbPlusOneData;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.util.AccessibilityUtils;
import com.galaxy.meetup.client.util.BackgroundPatternUtils;
import com.galaxy.meetup.client.util.Dates;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.PlusBarUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public abstract class StreamCardView extends CardView implements ClickableButton.ClickableButtonListener {

	protected static TextPaint sAttributionTextPaint;
    protected static Bitmap sAuthorBitmap;
    protected static int sAuthorNameYOffset;
    protected static TextPaint sAutoTextPaint;
    protected static int sAvatarSize;
    protected static Bitmap sCommentsBitmap;
    protected static Bitmap sCommunityBitmap;
    protected static int sContentXPadding;
    protected static int sContentYPadding;
    private static final Interpolator sDampingInterpolator = new Interpolator() {

        public final float getInterpolation(float f)
        {
            double d = 3.1415926535897931D * (double)(4F * f - 1.0F);
            return (float)(Math.sin(d) / d);
        }

    };
    protected static Bitmap sDeepLinkHintBitmap;
    protected static Paint sGraySpamBackgroundPaint;
    protected static int sGraySpamIconPadding;
    protected static TextPaint sGraySpamTextPaint;
    protected static Bitmap sGraySpamWarningBitmap;
    protected static float sMediaCardBigHeightPercentage;
    protected static float sMediaCardHeightPercentage;
    protected static NinePatchDrawable sMediaShadowDrawable;
    protected static Paint sMediaTopAreaBackgroundPaint;
    protected static TextPaint sNameTextPaint;
    protected static TextPaint sRelativeTimeTextPaint;
    protected static int sRelativeTimeYOffset;
    protected static Bitmap sReshareBitmap;
    protected static Bitmap sSquareBitmap;
    private static boolean sStreamCardViewInitialized;
    protected static Bitmap sTagAlbumBitmaps[];
    protected static int sTagBackgroundYPadding;
    protected static Drawable sTagDrawable;
    protected static Bitmap sTagHangoutBitmaps[];
    protected static int sTagIconXPadding;
    protected static int sTagIconYPaddingCheckin;
    protected static int sTagIconYPaddingLocation;
    protected static int sTagIconYPaddingWithPhoto;
    protected static Bitmap sTagLinkBitmaps[];
    protected static Bitmap sTagLocationBitmaps[];
    protected static Bitmap sTagMusicBitmaps[];
    protected static TextPaint sTagTextPaint;
    protected static int sTagTextXPadding;
    protected static Bitmap sTagVideoBitmaps[];
    protected static int sTagYOffset;
    protected static BitmapDrawable sTiledStageDrawable;
    protected static Bitmap sWhatsHotBitmap;
    protected String mActivityId;
    protected CharSequence mAttribution;
    protected StaticLayout mAttributionLayout;
    protected String mAuthorAvatarUrl;
    protected String mAuthorGaiaId;
    protected ClickableAvatar mAuthorImage;
    protected String mAuthorName;
    protected StaticLayout mAuthorNameLayout;
    protected int mAutoText;
    protected StaticLayout mAutoTextLayout;
    protected boolean mCanReshare;
    protected ClickableButton mCommentsButton;
    protected CharSequence mContent;
    protected StaticLayout mContentLayout;
    protected Bitmap mCornerIcon;
    protected String mEventId;
    protected String mEventOwnerId;
    protected CharSequence mFillerContent;
    protected StaticLayout mFillerContentLayout;
    protected StaticLayout mGraySpamLayout;
    protected boolean mInvisiblePlusOneButton;
    protected boolean mIsGraySpam;
    protected boolean mIsLimited;
    protected ClickableButton mOverridePlusOnedButton;
    protected boolean mOverridePlusOnedButtonDisplay;
    protected ClickableButton mPlusOneButton;
    protected DbPlusOneData mPlusOneData;
    protected String mRelativeTime;
    protected StaticLayout mRelativeTimeLayout;
    protected ClickableButton mReshareButton;
    private Runnable mShakeAnimation;
    protected String mSquareIdForOneUp;
    protected boolean mSquareMode;
    protected StreamMediaClickListener mStreamMediaClickListener;
    protected StreamPlusBarClickListener mStreamPlusBarClickListener;
    protected CharSequence mTag;
    protected Drawable mTagDrawableInstance;
    protected Bitmap mTagIcon;
    protected StaticLayout mTagLayout;
    protected int mTotalComments;
    private ViewedListener mViewedListener;
    protected boolean mViewerIsSquareAdmin;
    
    public StreamCardView(Context context)
    {
        this(context, null);
    }

    public StreamCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        Resources resources = context.getResources();
        if(!sStreamCardViewInitialized)
        {
            sStreamCardViewInitialized = true;
            sAuthorBitmap = EsAvatarData.getMediumDefaultAvatar(context, true);
            Bitmap abitmap[] = new Bitmap[2];
            abitmap[0] = ImageUtils.decodeResource(resources, R.drawable.ic_metadata_album);
            abitmap[1] = ImageUtils.decodeResource(resources, R.drawable.ic_album_blue);
            sTagAlbumBitmaps = abitmap;
            Bitmap abitmap1[] = new Bitmap[2];
            abitmap1[0] = ImageUtils.decodeResource(resources, R.drawable.ic_metadata_link);
            abitmap1[1] = ImageUtils.decodeResource(resources, R.drawable.ic_link_blue);
            sTagLinkBitmaps = abitmap1;
            Bitmap abitmap2[] = new Bitmap[2];
            abitmap2[0] = ImageUtils.decodeResource(resources, R.drawable.ic_metadata_location);
            abitmap2[1] = ImageUtils.decodeResource(resources, R.drawable.icn_location_card);
            sTagLocationBitmaps = abitmap2;
            Bitmap abitmap3[] = new Bitmap[2];
            abitmap3[0] = ImageUtils.decodeResource(resources, R.drawable.ic_metadata_music);
            abitmap3[1] = ImageUtils.decodeResource(resources, R.drawable.ic_music_blue);
            sTagMusicBitmaps = abitmap3;
            Bitmap abitmap4[] = new Bitmap[2];
            abitmap4[0] = ImageUtils.decodeResource(resources, R.drawable.ic_metadata_video);
            abitmap4[1] = ImageUtils.decodeResource(resources, R.drawable.ic_video_blue);
            sTagVideoBitmaps = abitmap4;
            Bitmap abitmap5[] = new Bitmap[2];
            abitmap5[0] = ImageUtils.decodeResource(resources, R.drawable.ic_metadata_hangouts);
            abitmap5[1] = null;
            sTagHangoutBitmaps = abitmap5;
            sCommentsBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_comments);
            sReshareBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_menu_reshare);
            sCommunityBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_community);
            sWhatsHotBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_whatshot);
            sDeepLinkHintBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_open_external_link);
            sSquareBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_community_share);
            sGraySpamWarningBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_error_white);
            sTagDrawable = resources.getDrawable(R.drawable.card_tag);
            sMediaShadowDrawable = (NinePatchDrawable)resources.getDrawable(R.drawable.taco_media_shadow);
            BitmapDrawable bitmapdrawable = (BitmapDrawable)resources.getDrawable(R.drawable.bg_taco_mediapattern);
            sTiledStageDrawable = bitmapdrawable;
            bitmapdrawable.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
            sTiledStageDrawable.setTileModeY(android.graphics.Shader.TileMode.REPEAT);
            TextPaint textpaint = new TextPaint();
            sNameTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sNameTextPaint.setColor(resources.getColor(R.color.card_author_name));
            sNameTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sNameTextPaint.setTextSize(resources.getDimension(R.dimen.card_author_name_text_size));
            TextPaintUtils.registerTextPaint(sNameTextPaint, R.dimen.card_author_name_text_size);
            TextPaint textpaint1 = new TextPaint();
            sRelativeTimeTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sRelativeTimeTextPaint.setColor(resources.getColor(R.color.card_relative_time_text));
            sRelativeTimeTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sRelativeTimeTextPaint.setTextSize(resources.getDimension(R.dimen.card_relative_time_text_size));
            TextPaintUtils.registerTextPaint(sRelativeTimeTextPaint, R.dimen.card_relative_time_text_size);
            TextPaint textpaint2 = new TextPaint();
            sTagTextPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sTagTextPaint.setColor(resources.getColor(R.color.card_tag_text));
            sTagTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sTagTextPaint.setTextSize(resources.getDimension(R.dimen.card_tag_text_size));
            sTagTextPaint.setShadowLayer(resources.getDimension(R.dimen.card_tag_text_shadow_radius), resources.getDimension(R.dimen.card_tag_text_shadow_x), resources.getDimension(R.dimen.card_tag_text_shadow_y), resources.getColor(R.color.card_tag_shadow_text));
            TextPaintUtils.registerTextPaint(sTagTextPaint, R.dimen.card_tag_text_size);
            TextPaint textpaint3 = new TextPaint();
            sAutoTextPaint = textpaint3;
            textpaint3.setAntiAlias(true);
            sAutoTextPaint.setColor(resources.getColor(R.color.card_auto_text));
            sAutoTextPaint.setTextSize(resources.getDimension(R.dimen.card_auto_text_size));
            TextPaintUtils.registerTextPaint(sAutoTextPaint, R.dimen.card_auto_text_size);
            TextPaint textpaint4 = new TextPaint();
            sAttributionTextPaint = textpaint4;
            textpaint4.setAntiAlias(true);
            sAttributionTextPaint.setColor(resources.getColor(R.color.card_attribution_text));
            sAttributionTextPaint.setTextSize(resources.getDimension(R.dimen.card_default_text_size));
            sAttributionTextPaint.linkColor = resources.getColor(R.color.card_link);
            TextPaintUtils.registerTextPaint(sAttributionTextPaint, R.dimen.card_default_text_size);
            TextPaint textpaint5 = new TextPaint();
            sGraySpamTextPaint = textpaint5;
            textpaint5.setAntiAlias(true);
            sGraySpamTextPaint.setColor(resources.getColor(R.color.card_gray_spam_text));
            sGraySpamTextPaint.setTextSize(resources.getDimension(R.dimen.card_default_text_size));
            TextPaintUtils.registerTextPaint(sGraySpamTextPaint, R.dimen.card_default_text_size);
            Paint paint = new Paint();
            sMediaTopAreaBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.solid_black));
            Paint paint1 = new Paint();
            sGraySpamBackgroundPaint = paint1;
            paint1.setColor(resources.getColor(R.color.card_gray_spam_background));
            sAvatarSize = (int)resources.getDimension(R.dimen.card_avatar_size);
            sAuthorNameYOffset = (int)resources.getDimension(R.dimen.card_author_name_y_padding);
            sRelativeTimeYOffset = (int)resources.getDimension(R.dimen.card_relative_time_y_offset);
            sContentXPadding = (int)resources.getDimension(R.dimen.card_content_x_padding);
            sContentYPadding = (int)resources.getDimension(R.dimen.card_content_y_padding);
            sTagYOffset = (int)resources.getDimension(R.dimen.card_tag_y_offset);
            sTagTextXPadding = (int)resources.getDimension(R.dimen.card_tag_text_x_padding);
            sTagBackgroundYPadding = (int)resources.getDimension(R.dimen.card_tag_background_y_padding);
            sTagIconXPadding = (int)resources.getDimension(R.dimen.card_tag_icon_x_padding);
            sTagIconYPaddingCheckin = (int)resources.getDimension(R.dimen.card_tag_icon_y_padding_checkin);
            sTagIconYPaddingLocation = (int)resources.getDimension(R.dimen.card_tag_icon_y_padding_location);
            sTagIconYPaddingWithPhoto = (int)resources.getDimension(R.dimen.card_tag_icon_y_padding_with_photo);
            sGraySpamIconPadding = (int)resources.getDimension(R.dimen.card_gray_spam_x_padding);
            sMediaCardHeightPercentage = resources.getDimension(R.dimen.media_card_height_percentage);
            sMediaCardBigHeightPercentage = resources.getDimension(R.dimen.media_card_big_height_percentage);
        }
        mTagDrawableInstance = sTagDrawable.getConstantState().newDrawable();
    }
	
    private void createSourceRectForMediaImage(Rect rect, int i, int j, int k, int l)
    {
        if(k == 0 || l == 0)
        {
            rect.setEmpty();
        } else
        {
            int i1 = k + sXDoublePadding;
            int j1 = (int)((float)(l + sYDoublePadding) * getMediaHeightPercentage());
            int k1 = i;
            int l1 = j;
            int i2;
            int j2;
            if(i * j1 > j * i1)
                k1 = (j * i1) / j1;
            else
                l1 = (i * j1) / i1;
            i2 = (i - k1) / 2;
            j2 = (j - l1) / 2;
            rect.set(i2, j2, i2 + k1, j2 + l1);
        }
    }

    protected static void drawMediaTopAreaStageWithTiledBackground(Canvas canvas, int i, int j)
    {
        sTiledStageDrawable.setBounds(sLeftBorderPadding, sTopBorderPadding, i + sXDoublePadding + sRightBorderPadding, j + sYPadding);
        sTiledStageDrawable.draw(canvas);
    }

    private void ensureOverridePlusOnedButton(int i)
    {
        if(mOverridePlusOnedButton == null)
        {
            Rect rect = mPlusOneButton.getRect();
            Resources resources = getResources();
            int j = R.string.stream_plus_one_count_with_plus;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(i);
            String s = resources.getString(j, aobj);
            mOverridePlusOnedButton = new ClickableButton(getContext(), s, PlusBarUtils.sPlusOnedTextPaint, PlusBarUtils.sPlusOnedDrawable, PlusBarUtils.sPlusOnedPressedDrawable, this, rect.left, rect.top);
        }
    }

    protected final int createAuthorNameAndRelativeTimeLayoutOnSameLine(int i, int j)
    {
        CharSequence charsequence = TextUtils.ellipsize(mAuthorName, sNameTextPaint, j, android.text.TextUtils.TruncateAt.END);
        int k = (int)sNameTextPaint.measureText(charsequence.toString());
        mAuthorNameLayout = new StaticLayout(charsequence, sNameTextPaint, k, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        int l = i + mAuthorNameLayout.getHeight();
        CharSequence charsequence1 = TextUtils.ellipsize(mRelativeTime, sRelativeTimeTextPaint, j, android.text.TextUtils.TruncateAt.END);
        int i1 = (int)sRelativeTimeTextPaint.measureText(charsequence1.toString());
        if(i1 < j - k)
            mRelativeTimeLayout = new StaticLayout(charsequence1, sRelativeTimeTextPaint, i1, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        return l;
    }

    protected final void createGraySpamBar(int i)
    {
        if(mIsGraySpam && i > 0)
        {
            int j;
            int k;
            if(mSquareMode && mViewerIsSquareAdmin)
                j = R.string.card_square_gray_spam_for_moderator;
            else
                j = R.string.card_square_gray_spam;
            k = i - (sGraySpamWarningBitmap.getWidth() + 2 * sGraySpamIconPadding);
            mGraySpamLayout = new StaticLayout(getResources().getString(j), sGraySpamTextPaint, k, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        }
    }

    protected final int createMediaBottomArea(int i, int j, int k, int l)
    {
        int i1 = (int)((float)(l + sYDoublePadding) * getMediaHeightPercentage()) + sTopBorderPadding + sAuthorNameYOffset;
        setAuthorImagePosition(i, i1 - sAvatarSize / 2);
        int _tmp = sAvatarSize;
        int _tmp1 = sContentXPadding;
        int j1 = k - (sAvatarSize + sContentXPadding);
        int k1 = createAuthorNameAndRelativeTimeLayoutOnSameLine(i1, j1) + sContentYPadding;
        boolean flag = TextUtils.isEmpty(mAttribution);
        boolean flag1 = false;
        if(!flag)
        {
            int k2 = ((l + j) - k1) / (int)(sAttributionTextPaint.descent() - sAttributionTextPaint.ascent());
            flag1 = false;
            if(k2 > 0)
            {
                mAttributionLayout = TextPaintUtils.createConstrainedStaticLayout(sAttributionTextPaint, mAttribution, j1, k2);
                k1 += mAttributionLayout.getHeight() + sContentYPadding;
                flag1 = true;
            }
        }
        if(!TextUtils.isEmpty(mContent))
        {
            int j2 = ((l + j) - k1) / (int)(sDefaultTextPaint.descent() - sDefaultTextPaint.ascent());
            if(j2 > 0)
            {
                mContentLayout = TextPaintUtils.createConstrainedStaticLayout(sDefaultTextPaint, mContent, j1, j2);
                k1 += mContentLayout.getHeight() + sContentYPadding;
                flag1 = true;
            }
        }
        if(!TextUtils.isEmpty(mFillerContent))
        {
            int i2 = ((l + j) - k1) / (int)(sDefaultTextPaint.descent() - sDefaultTextPaint.ascent());
            if(i2 > 0)
            {
                mFillerContentLayout = TextPaintUtils.createConstrainedStaticLayout(sDefaultTextPaint, mFillerContent, j1, i2);
                k1 += mFillerContentLayout.getHeight() + sContentYPadding;
                flag1 = true;
            }
        }
        if(!flag1 && mAutoText != 0)
        {
            int l1 = ((l + j) - k1) / (int)(sAutoTextPaint.descent() - sAutoTextPaint.ascent());
            if(l1 > 0)
            {
                mAutoTextLayout = TextPaintUtils.createConstrainedStaticLayout(sAutoTextPaint, getResources().getString(mAutoText), j1, l1);
                k1 += mAutoTextLayout.getHeight() + sContentYPadding;
            }
        }
        return k1;
    }

    protected final int createNameLayout(int i, int j)
    {
        mAuthorNameLayout = new StaticLayout(TextUtils.ellipsize(mAuthorName, sNameTextPaint, j, android.text.TextUtils.TruncateAt.END), sNameTextPaint, j, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        return i + mAuthorNameLayout.getHeight();
    }

    protected final int createPlusOneBar(int i, int j, int k)
    {
        Context context = getContext();
        int l = i + k;
        boolean flag;
        int i1;
        Resources resources;
        int j1;
        Object aobj[];
        String s;
        TextPaint textpaint;
        NinePatchDrawable ninepatchdrawable;
        NinePatchDrawable ninepatchdrawable1;
        int k1;
        int l1;
        if(mPlusOneData != null && mPlusOneData.isPlusOnedByMe())
            flag = true;
        else
            flag = false;
        if(mPlusOneData == null)
            i1 = 1;
        else
            i1 = mPlusOneData.getCount();
        resources = getResources();
        j1 = R.string.stream_plus_one_count_with_plus;
        aobj = new Object[1];
        aobj[0] = Integer.valueOf(Math.max(i1, 1));
        s = resources.getString(j1, aobj);
        removeClickableItem(mPlusOneButton);
        if(flag)
            textpaint = PlusBarUtils.sPlusOnedTextPaint;
        else
            textpaint = PlusBarUtils.sNotPlusOnedTextPaint;
        if(flag)
            ninepatchdrawable = PlusBarUtils.sPlusOnedDrawable;
        else
            ninepatchdrawable = PlusBarUtils.sButtonDrawable;
        if(flag)
            ninepatchdrawable1 = PlusBarUtils.sPlusOnedPressedDrawable;
        else
            ninepatchdrawable1 = PlusBarUtils.sButtonPressedDrawable;
        mPlusOneButton = new ClickableButton(context, s, textpaint, ninepatchdrawable, ninepatchdrawable1, this, l, j);
        k1 = l - mPlusOneButton.getRect().width();
        l1 = j - mPlusOneButton.getRect().height();
        mPlusOneButton.getRect().offsetTo(k1, l1);
        addClickableItem(mPlusOneButton);
        if(flag && mCanReshare)
        {
            removeClickableItem(mReshareButton);
            Bitmap bitmap1 = sReshareBitmap;
            NinePatchDrawable ninepatchdrawable4 = PlusBarUtils.sButtonDrawable;
            NinePatchDrawable ninepatchdrawable5 = PlusBarUtils.sButtonPressedDrawable;
            String s3 = getResources().getString(R.string.reshare_button_content_description);
            mReshareButton = new ClickableButton(context, bitmap1, ninepatchdrawable4, ninepatchdrawable5, this, k1, l1, s3);
            k1 -= mReshareButton.getRect().width() + PlusBarUtils.sPlusBarXPadding;
            mReshareButton.getRect().offsetTo(k1, l1);
            addClickableItem(mReshareButton);
        }
        if(mTotalComments > 0)
        {
            String s1 = String.valueOf(mTotalComments);
            removeClickableItem(mCommentsButton);
            Resources resources1 = getResources();
            int i2 = R.plurals.stream_one_up_comment_count;
            int j2 = mTotalComments;
            Object aobj1[] = new Object[1];
            aobj1[0] = Integer.valueOf(mTotalComments);
            String s2 = resources1.getQuantityString(i2, j2, aobj1);
            Bitmap bitmap = sCommentsBitmap;
            TextPaint textpaint1 = PlusBarUtils.sNotPlusOnedTextPaint;
            NinePatchDrawable ninepatchdrawable2 = PlusBarUtils.sButtonDrawable;
            NinePatchDrawable ninepatchdrawable3 = PlusBarUtils.sButtonPressedDrawable;
            Object obj;
            int k2;
            if(this instanceof EventStreamCardView)
                obj = null;
            else
                obj = this;
            mCommentsButton = new ClickableButton(context, bitmap, s1, textpaint1, ninepatchdrawable2, ninepatchdrawable3, ((ClickableButton.ClickableButtonListener) (obj)), k1, l1, s2);
            k2 = k1 - (mCommentsButton.getRect().width() + PlusBarUtils.sPlusBarXPadding);
            mCommentsButton.getRect().offsetTo(k2, l1);
            addClickableItem(mCommentsButton);
        }
        return l1 + mPlusOneButton.getRect().height();
    }

    protected final void createSourceRectForMediaImage(Rect rect, Bitmap bitmap, int i, int j)
    {
        if(bitmap == null)
            rect.setEmpty();
        else
            createSourceRectForMediaImage(rect, bitmap.getWidth(), bitmap.getHeight(), i, j);
    }

    protected final void createSourceRectForMediaImage(Rect rect, Drawable drawable, int i, int j)
    {
        if(drawable == null)
            rect.setEmpty();
        else
            createSourceRectForMediaImage(rect, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), i, j);
    }

    protected final int createTagBar(int i, int j, int k)
    {
        if(mTag != null)
        {
            int _tmp = sTagTextXPadding;
            int l = k - 2 * sTagTextXPadding;
            int i1 = j + sTagYOffset;
            if(mTagIcon != null)
            {
                mTagIcon.getWidth();
                int _tmp1 = sTagIconXPadding;
                l -= mTagIcon.getWidth() + sTagIconXPadding;
            }
            CharSequence charsequence = TextPaintUtils.smartEllipsize(mTag, sTagTextPaint, l, android.text.TextUtils.TruncateAt.END);
            int j1 = (int)sTagTextPaint.measureText(charsequence.toString());
            mTagLayout = new StaticLayout(charsequence, sTagTextPaint, j1, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            j = i1 + mTagLayout.getHeight();
        }
        return j;
    }
    
    protected final void drawAuthorImage(Canvas canvas) {
    	if(null == mAuthorImage) {
    		return;
    	}
    	Bitmap bitmap;
        if(mAuthorImage.getBitmap() != null)
            bitmap = mAuthorImage.getBitmap();
        else
            bitmap = sAuthorBitmap;
        canvas.drawBitmap(bitmap, null, mAuthorImage.getRect(), sResizePaint);
        if(mAuthorImage.isClicked())
            mAuthorImage.drawSelectionRect(canvas);
    }

    protected final int drawAuthorName(Canvas canvas, int i, int j)
    {
        if(mAuthorNameLayout != null)
        {
            canvas.translate(i, j);
            mAuthorNameLayout.draw(canvas);
            canvas.translate(-i, -j);
            j += mAuthorNameLayout.getHeight();
        }
        return j;
    }

    protected final void drawCornerIcon(Canvas canvas)
    {
        if(mCornerIcon != null)
        {
            int i = getHeight() - sBottomBorderPadding - mCornerIcon.getHeight();
            canvas.drawBitmap(mCornerIcon, sLeftBorderPadding, i, null);
        }
    }

    protected final int drawMediaBottomArea(Canvas canvas, int i, int j, int k)
    {
        int l = (int)((float)(k + 2 * sYPadding) * getMediaHeightPercentage()) + sTopBorderPadding + sAuthorNameYOffset;
        int _tmp = sAvatarSize;
        drawAuthorImage(canvas);
        int i1 = i + (sAvatarSize + sContentXPadding);
        int j1 = j - (sAvatarSize + sContentXPadding);
        int k1 = drawAuthorName(canvas, i1, l);
        if(mRelativeTimeLayout != null)
            drawRelativeTimeLayout(canvas, (i1 + j1) - mRelativeTimeLayout.getWidth(), k1 - mRelativeTimeLayout.getHeight() - sRelativeTimeYOffset);
        int l1 = k1 + sContentYPadding;
        if(mAttributionLayout != null)
        {
            canvas.translate(i1, l1);
            mAttributionLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            l1 += mAttributionLayout.getHeight() + sContentYPadding;
        }
        if(mContentLayout != null)
        {
            canvas.translate(i1, l1);
            mContentLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            l1 += mContentLayout.getHeight() + sContentYPadding;
        }
        if(mFillerContentLayout != null)
        {
            canvas.translate(i1, l1);
            mFillerContentLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            l1 += mFillerContentLayout.getHeight() + sContentYPadding;
        }
        if(mAutoTextLayout != null)
        {
            canvas.translate(i1, l1);
            mAutoTextLayout.draw(canvas);
            canvas.translate(-i1, -l1);
            l1 += mAutoTextLayout.getHeight() + sContentYPadding;
        }
        return l1;
    }

    protected final void drawMediaTopAreaShadow(Canvas canvas, int i, int j)
    {
        int k = i + 2 * sXPadding;
        int l = (int)((float)(j + 2 * sYPadding) * getMediaHeightPercentage());
        sMediaShadowDrawable.setBounds(sLeftBorderPadding, (l + sTopBorderPadding) - sMediaShadowDrawable.getIntrinsicHeight(), k + sLeftBorderPadding, l + sTopBorderPadding);
        sMediaShadowDrawable.draw(canvas);
    }

    protected final void drawMediaTopAreaStage(Canvas canvas, int i, int j, boolean flag, Rect rect, Paint paint)
    {
        int k = i + 2 * sXPadding;
        int l = (int)((float)(j + 2 * sYPadding) * getMediaHeightPercentage());
        if(flag)
        {
            if(rect == null || rect.width() < k || rect.height() < l)
                canvas.drawRect(sLeftBorderPadding, sTopBorderPadding, k + sLeftBorderPadding, l + sTopBorderPadding, paint);
        } else
        {
            BackgroundPatternUtils.getInstance(getContext());
            BitmapDrawable bitmapdrawable = BackgroundPatternUtils.getBackgroundPattern(mActivityId);
            bitmapdrawable.setBounds(sLeftBorderPadding, sTopBorderPadding, k + sLeftBorderPadding, l + sTopBorderPadding);
            bitmapdrawable.draw(canvas);
        }
    }

    protected final void drawPlusOneBar(Canvas canvas) {
    	
    	if(mInvisiblePlusOneButton) {
    		
    	} else {
    		if(mOverridePlusOnedButtonDisplay) {
    			if(mOverridePlusOnedButton != null)
    	            mOverridePlusOnedButton.draw(canvas);
    		} else {
    			mPlusOneButton.draw(canvas);
    		}
    	}
    	
    	if(mReshareButton != null)
            mReshareButton.draw(canvas);
        if(mCommentsButton != null)
            mCommentsButton.draw(canvas);
        return;
    }

    protected final int drawRelativeTimeLayout(Canvas canvas, int i, int j)
    {
        if(mRelativeTimeLayout != null)
        {
            canvas.translate(i, j);
            mRelativeTimeLayout.draw(canvas);
            canvas.translate(-i, -j);
            j += mRelativeTimeLayout.getHeight();
        }
        return j;
    }

    protected final void drawTagBarIconAndBackground(Canvas canvas, int i, int j)
    {
        if(mTagLayout != null)
        {
            int k = j + sTagYOffset;
            int l = mTagLayout.getWidth() + 2 * sTagTextXPadding;
            if(mTagIcon != null)
                l += mTagIcon.getWidth() + sTagIconXPadding;
            mTagDrawableInstance.setBounds(i, k - sTagBackgroundYPadding, i + l, k + mTagLayout.getHeight() + sTagBackgroundYPadding);
            mTagDrawableInstance.draw(canvas);
            int i1 = i + sTagTextXPadding;
            if(mTagIcon != null)
            {
                int j1 = k + (mTagLayout.getHeight() - mTagIcon.getHeight()) / 2 + sTagIconYPaddingWithPhoto;
                canvas.drawBitmap(mTagIcon, i1, j1, null);
                i1 += mTagIcon.getWidth() + sTagIconXPadding;
            }
            canvas.translate(i1, k);
            mTagLayout.draw(canvas);
            canvas.translate(-i1, -k);
        }
    }

    protected String formatLocationName(String s)
    {
        return s.toUpperCase();
    }

    public final String getActivityId()
    {
        return mActivityId;
    }

    public String getAlbumId()
    {
        return null;
    }

    public String getDeepLinkLabel()
    {
        return null;
    }

    public int getDesiredHeight()
    {
        return 0;
    }

    public int getDesiredWidth()
    {
        return 0;
    }

    public final String getEventId()
    {
        return mEventId;
    }

    public final String getEventOwnerId()
    {
        return mEventOwnerId;
    }

    public String getLinkTitle()
    {
        return null;
    }

    public String getLinkUrl()
    {
        return null;
    }

    protected final float getMediaHeightPercentage()
    {
        float f;
        if(mDisplaySizeType == 1 || mDisplaySizeType == 3)
            f = sMediaCardBigHeightPercentage;
        else
            f = sMediaCardHeightPercentage;
        return f;
    }

    public String getMediaLinkUrl()
    {
        return null;
    }

    public MediaRef getMediaRef()
    {
        return null;
    }

    public final Pair getPlusOneButtonAnimationCopies()
    {
        int i = (int)getX();
        int j = (int)getY();
        ClickableButton clickablebutton = mPlusOneButton.createAbsoluteCoordinatesCopy(i, j);
        int k;
        if(mPlusOneData == null)
            k = 1;
        else
            k = 1 + mPlusOneData.getCount();
        ensureOverridePlusOnedButton(k);
        return new Pair(clickablebutton, mOverridePlusOnedButton.createAbsoluteCoordinatesCopy(i, j));
    }

    public String getSquareId()
    {
        return null;
    }

    public final String getSquareIdForOneUp()
    {
        return mSquareIdForOneUp;
    }

    public void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, ViewedListener viewedlistener, StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamMediaClickListener streammediaclicklistener)
    {
        super.init(cursor, i, j, onclicklistener, itemclicklistener, viewedlistener, streamplusbarclicklistener, streammediaclicklistener);
        Context context = getContext();
        Resources resources = getResources();
        mStreamPlusBarClickListener = streamplusbarclicklistener;
        mStreamMediaClickListener = streammediaclicklistener;
        mActivityId = cursor.getString(1);
        mAuthorGaiaId = cursor.getString(2);
        mAuthorName = cursor.getString(3);
        if(mAuthorName == null)
            mAuthorName = "";
        mAuthorAvatarUrl = EsAvatarData.uncompressAvatarUrl(cursor.getString(4));
        if(mAuthorImage != null)
            removeClickableItem(mAuthorImage);
        mAuthorImage = new ClickableAvatar(this, mAuthorGaiaId, mAuthorAvatarUrl, mAuthorName, itemclicklistener, 2);
        addClickableItem(mAuthorImage);
        mRelativeTime = Dates.getRelativeTimeSpanString(context, cursor.getLong(8)).toString().toUpperCase();
        long l = cursor.getLong(15);
        String s;
        String s1;
        byte abyte0[];
        boolean flag;
        boolean flag1;
        boolean flag2;
        StringBuilder stringbuilder;
        int k;
        byte abyte1[];
        byte abyte2[];
        if((2L & l) != 0L)
            mContent = cursor.getString(16);
        else
            mContent = null;
        if((1L & l) != 0L)
            if(TextUtils.isEmpty(mContent))
                mContent = cursor.getString(17);
            else
                mFillerContent = cursor.getString(17);
        s = cursor.getString(18);
        s1 = cursor.getString(19);
        if(!TextUtils.isEmpty(s) && !TextUtils.isEmpty(s1))
            mAttribution = resources.getString(R.string.stream_original_author, new Object[] {
                s1
            });
        if(mTag == null && (8L & l) != 0L)
        {
            byte abyte3[] = cursor.getBlob(7);
            if(abyte3 != null)
            {
                mTag = formatLocationName(DbLocation.deserialize(abyte3).getLocationName());
                mTagIcon = sTagLocationBitmaps[0];
            }
        }
        mTotalComments = cursor.getInt(6);
        abyte0 = cursor.getBlob(5);
        if(abyte0 != null)
            mPlusOneData = DbPlusOneData.deserialize(abyte0);
        else
            mPlusOneData = null;
        if(cursor.getInt(11) != 1)
            mViewedListener = viewedlistener;
        if(!mSquareMode && (0x80000L & l) != 0L)
            mCornerIcon = sCommunityBitmap;
        else
        if(cursor.getInt(14) == 1)
            mCornerIcon = sWhatsHotBitmap;
        else
            mCornerIcon = null;
        if(cursor.getInt(9) == 0)
            flag = true;
        else
            flag = false;
        mIsLimited = flag;
        if(mViewerIsSquareAdmin && cursor.getInt(10) == 1 && (0x80000L & l) != 0L)
            flag1 = true;
        else
            flag1 = false;
        mIsGraySpam = flag1;
        if(cursor.getInt(12) != 0)
            flag2 = true;
        else
            flag2 = false;
        mCanReshare = flag2;
        mAutoText = EsPostsData.getDefaultText(l);
        stringbuilder = new StringBuilder();
        AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mAuthorName);
        if(mAutoText != 0)
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, resources.getString(mAutoText));
        AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mRelativeTime);
        AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mContent);
        AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mFillerContent);
        AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mAttribution);
        AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mTag);
        if(mTotalComments > 0)
        {
            int j1 = R.plurals.comments;
            int k1 = mTotalComments;
            Object aobj1[] = new Object[1];
            aobj1[0] = Integer.valueOf(mTotalComments);
            stringbuilder.append(resources.getQuantityString(j1, k1, aobj1)).append(". ");
        }
        if(mPlusOneData == null)
            k = 0;
        else
            k = mPlusOneData.getCount();
        if(k > 0)
        {
            int i1 = R.plurals.plus_one_accessibility_description;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(k);
            stringbuilder.append(resources.getQuantityString(i1, k, aobj));
        }
        abyte1 = cursor.getBlob(13);
        if(abyte1 != null)
        {
            PlusEvent plusevent = (PlusEvent)JsonUtil.fromByteArray(abyte1, PlusEvent.class);
            if(plusevent != null)
            {
                mEventId = plusevent.id;
                mEventOwnerId = plusevent.creatorObfuscatedId;
            }
        }
        abyte2 = cursor.getBlob(27);
        if(abyte2 != null)
            mSquareIdForOneUp = DbEmbedSquare.deserialize(abyte2).getSquareId();
        setContentDescription(stringbuilder.toString());
        setFocusable(true);
    }

    public boolean isAlbum()
    {
        return false;
    }

    protected void onBindResources()
    {
        super.onBindResources();
        if(mAuthorImage != null)
            mAuthorImage.bindResources();
    }

    public void onClickableButtonListenerClick(ClickableButton clickablebutton) {
        if(mStreamPlusBarClickListener == null) {
        	return; 
        } 
        
        if(clickablebutton != mPlusOneButton) {
        	if(clickablebutton == mReshareButton)
                mStreamPlusBarClickListener.onReshareClicked(mActivityId, mIsLimited);
            else
            if(clickablebutton == mCommentsButton)
            {
                StreamPlusBarClickListener streamplusbarclicklistener = mStreamPlusBarClickListener;
                streamplusbarclicklistener.onCommentsClicked(this);
            }
        } else {
        	mStreamPlusBarClickListener.onPlusOneClicked(mActivityId, mPlusOneData, this);
        }
        
    }

    protected void onDetachedFromWindow()
    {
        if(mShakeAnimation == null)
        {
            removeCallbacks(mShakeAnimation);
            mShakeAnimation = null;
        }
        clearAnimation();
        super.onDetachedFromWindow();
    }

    protected void onDraw(Canvas canvas)
    {
        if(mActivityId != null)
        {
            super.onDraw(canvas);
            int i = sLeftBorderPadding;
            int j = sTopBorderPadding;
            int k = getWidth() - sLeftBorderPadding - sRightBorderPadding;
            if(mGraySpamLayout != null)
            {
                int l = Math.max(sGraySpamWarningBitmap.getHeight(), mGraySpamLayout.getHeight());
                canvas.drawRect(i, j, k + i, j + l, sGraySpamBackgroundPaint);
                canvas.drawBitmap(sGraySpamWarningBitmap, i + sGraySpamIconPadding, j + (l - sGraySpamWarningBitmap.getHeight()) / 2, sResizePaint);
                int i1 = i + sGraySpamWarningBitmap.getWidth() + 2 * sGraySpamIconPadding;
                int j1 = j + (l - mGraySpamLayout.getHeight()) / 2;
                canvas.translate(i1, j1);
                mGraySpamLayout.draw(canvas);
                canvas.translate(-i1, -j1);
            }
            if(mViewedListener != null)
            {
                mViewedListener.onStreamCardViewed(mActivityId);
                mViewedListener = null;
            }
        }
    }

    protected void onMeasure(int i, int j)
    {
        if(mActivityId == null)
        {
            setMeasuredDimension(0, 0);
        } else
        {
            super.onMeasure(i, j);
            createGraySpamBar(getMeasuredWidth() - sLeftBorderPadding - sRightBorderPadding);
        }
    }

    public void onRecycle()
    {
        super.onRecycle();
        mActivityId = null;
        mAuthorGaiaId = null;
        mAuthorName = null;
        mAuthorAvatarUrl = null;
        mAuthorNameLayout = null;
        mRelativeTime = null;
        mRelativeTimeLayout = null;
        mTag = null;
        mTagIcon = null;
        mTagLayout = null;
        mContent = null;
        mContentLayout = null;
        mAttribution = null;
        mAttributionLayout = null;
        mFillerContent = null;
        mFillerContentLayout = null;
        mAutoText = 0;
        mAutoTextLayout = null;
        mGraySpamLayout = null;
        mCommentsButton = null;
        mReshareButton = null;
        mPlusOneButton = null;
        mOverridePlusOnedButton = null;
        mInvisiblePlusOneButton = false;
        mOverridePlusOnedButtonDisplay = false;
        mTotalComments = 0;
        mPlusOneData = null;
        mStreamPlusBarClickListener = null;
        mCornerIcon = null;
        mIsLimited = false;
        mIsGraySpam = false;
        mCanReshare = false;
        mSquareMode = false;
        mViewerIsSquareAdmin = false;
        mSquareIdForOneUp = null;
        mEventId = null;
        mEventOwnerId = null;
        mViewedListener = null;
    }

    protected void onUnbindResources()
    {
        super.onUnbindResources();
        if(mAuthorImage != null)
            mAuthorImage.unbindResources();
    }

    public final void overridePlusOnedButtonDisplay(boolean flag, int i)
    {
        mOverridePlusOnedButtonDisplay = flag;
        if(mOverridePlusOnedButtonDisplay)
        {
            ensureOverridePlusOnedButton(i);
            mPlusOneButton.setListener(null);
            mOverridePlusOnedButton.setListener(this);
            mInvisiblePlusOneButton = false;
        } else
        {
            mOverridePlusOnedButton = null;
        }
        invalidate();
    }

    protected final void setAuthorImagePosition(int i, int j)
    {
        if(mAuthorImage != null)
            mAuthorImage.setRect(i, j, i + sAvatarSize, j + sAvatarSize);
    }

    public void setSquareMode(boolean flag, boolean flag1)
    {
        mSquareMode = flag;
        mViewerIsSquareAdmin = flag1;
    }

    public final void startDelayedShakeAnimation()
    {
        final float rotX;
        final float rotY;
        mInvisiblePlusOneButton = true;
        invalidate();
        switch(mDisplaySizeType)
        {
        default:
            rotX = -1.5F;
            rotY = 2.0F;
            break;

        case 0: // '\0'
        	 if(/*this instanceof TextCardView*/ false)
                 rotX = -2.5F;
             else
                 rotX = -2F;
             rotY = 2.5F;
             break;
        }
        
        if(android.os.Build.VERSION.SDK_INT >= 14)
        {
            animate().setDuration(300L).rotationX(rotX).rotationY(rotY).scaleX(0.95F).scaleY(0.95F).setInterpolator(sDampingInterpolator).setStartDelay(615L);
        } else
        {
            mShakeAnimation = new Runnable() {

                public final void run()
                {
                    if(getHandler() != null)
                        animate().setDuration(300L).rotationX(rotX).rotationY(rotY).scaleX(0.95F).scaleY(0.95F).setInterpolator(StreamCardView.sDampingInterpolator);
                    mShakeAnimation = null;
                }
            };
            removeCallbacks(mShakeAnimation);
            postDelayed(mShakeAnimation, 615L);
        }
    }
    
	public static interface StreamMediaClickListener {

        void onMediaClicked(String s, String s1, MediaRef mediaref, boolean flag, StreamCardView streamcardview);
    }

    public static interface StreamPlusBarClickListener {

        void onCommentsClicked(StreamCardView streamcardview);

        void onPlusOneClicked(String s, DbPlusOneData dbplusonedata, StreamCardView streamcardview);

        void onReshareClicked(String s, boolean flag);
    }

    public static interface ViewedListener {

        void onStreamCardViewed(String s);
    }
}
