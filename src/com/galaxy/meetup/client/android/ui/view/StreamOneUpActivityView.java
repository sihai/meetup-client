/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.DbEmbedDeepLink;
import com.galaxy.meetup.client.android.content.DbEmbedHangout;
import com.galaxy.meetup.client.android.content.DbEmbedMedia;
import com.galaxy.meetup.client.android.content.DbEmbedSkyjam;
import com.galaxy.meetup.client.android.content.DbEmbedSquare;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.DbPlusOneData;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.android.ui.view.ClickableButton.ClickableButtonListener;
import com.galaxy.meetup.client.util.AccessibilityUtils;
import com.galaxy.meetup.client.util.Dates;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.PlusBarUtils;
import com.galaxy.meetup.client.util.SpannableUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.client.util.TouchExplorationHelper;
import com.galaxy.meetup.server.client.domain.PlaceReview;
import com.galaxy.meetup.server.client.domain.Rating;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpActivityView extends OneUpBaseView implements
		ResourceConsumer, ClickableButtonListener, Recyclable {

	
	private static Paint sActionBarBackgroundPaint;
    private static int sAvatarMarginLeft;
    private static int sAvatarMarginRight;
    private static int sAvatarMarginTop;
    private static Bitmap sAvatarOverlayBitmap;
    private static int sAvatarSize;
    private static Paint sBackgroundPaint;
    private static Bitmap sCheckInIconBitmap;
    private static TextPaint sContentPaint;
    private static TextPaint sDatePaint;
    private static Bitmap sDefaultAvatarBitmap;
    private static float sFontSpacing;
    private static int sIsMuteColor;
    private static Paint sLinkedBackgroundPaint;
    private static TextPaint sLinkedBodyPaint;
    private static Paint sLinkedBorderPaint;
    private static float sLinkedBorderRadius;
    private static int sLinkedBorderSize;
    private static TextPaint sLinkedHeaderPaint;
    private static Bitmap sLinkedIconBitmap;
    private static int sLinkedIconMarginRight;
    private static int sLinkedInnerMargin;
    private static Bitmap sLocationIconBitmap;
    private static int sLocationIconMarginRight;
    private static int sLocationIconMarginTop;
    private static TextPaint sLocationPaint;
    private static int sMarginBottom;
    private static int sMarginLeft;
    private static int sMarginRight;
    private static int sNameMarginTop;
    private static TextPaint sNamePaint;
    private static int sPlaceReviewAspectsMarginBottom;
    private static int sPlaceReviewAspectsMarginTop;
    private static int sPlaceReviewDividerMargin;
    private static Paint sPlaceReviewDividerPaint;
    private static int sPlusOneButtonMarginLeft;
    private static int sPlusOneButtonMarginRight;
    private static Paint sReshareBackgroundPaint;
    private static TextPaint sReshareBodyPaint;
    private static Paint sReshareBorderPaint;
    private static float sReshareBorderRadius;
    private static int sReshareBorderSize;
    private static TextPaint sReshareHeaderPaint;
    private static int sReshareInnerMargin;
    private static Paint sResizePaint;
    private static Bitmap sSkyjamIconBitmap;
    private static int sTitleMarginBottom;
    private final ClickableStaticLayout.SpanClickListener mAclClickListener;
    private String mAclText;
    private String mActivityId;
    private Spannable mAnnotation;
    private ClickableStaticLayout mAnnotationLayout;
    private String mAuthorId;
    private ClickableAvatar mAuthorImage;
    private PositionedStaticLayout mAuthorLayout;
    private String mAuthorName;
    private int mBackgroundOffset;
    private Set mClickableItems;
    private String mCreationSource;
    private ClickableItem mCurrentClickableItem;
    private String mDate;
    private ClickableStaticLayout mDateSourceAclLayout;
    private DbEmbedSquare mDbEmbedSquare;
    private boolean mEdited;
    private boolean mIsCheckin;
    private Spannable mLinkedBody;
    private ClickableStaticLayout mLinkedBodyLayout;
    private RectF mLinkedContentBorder;
    private Spannable mLinkedHeader;
    private ClickableStaticLayout mLinkedHeaderLayout;
    private Rect mLinkedIconRect;
    private Spannable mLocation;
    private final ClickableStaticLayout.SpanClickListener mLocationClickListener;
    private DbLocation mLocationData;
    private Bitmap mLocationIcon;
    private Rect mLocationIconRect;
    private ClickableStaticLayout mLocationLayout;
    private String mMuteState;
    private OneUpListener mOneUpListener;
    private PlaceReview mPlaceReview;
    private PositionedStaticLayout mPlaceReviewAspectsLayout;
    private PositionedStaticLayout mPlaceReviewBodyLayout;
    private Rect mPlaceReviewDividerRect;
    protected ClickableButton mPlusOneButton;
    private DbPlusOneData mPlusOneData;
    private Spannable mReshareBody;
    private ClickableStaticLayout mReshareBodyLayout;
    private RectF mReshareContentBorder;
    private Spannable mReshareHeader;
    private ClickableStaticLayout mReshareHeaderLayout;
    private final ClickableStaticLayout.SpanClickListener mSkyjamClickListener;
    private RectF mSkyjamContentBorder;
    private Spannable mSkyjamHeader;
    private ClickableStaticLayout mSkyjamHeaderLayout;
    private Rect mSkyjamIconRect;
    private Spannable mSkyjamSubheader1;
    private PositionedStaticLayout mSkyjamSubheader1Layout;
    private Spannable mSkyjamSubheader2;
    private ClickableStaticLayout mSkyjamSubheader2Layout;
    private String mSourceAppData;
    private final List mSourceAppPackages;
    private final ClickableStaticLayout.SpanClickListener mSourceClickListener;
    private Spannable mTitle;
    private ClickableStaticLayout mTitleLayout;
    private OneUpActivityTouchExplorer mTouchExplorer;
    
    public StreamOneUpActivityView(Context context)
    {
        super(context);
        mClickableItems = new HashSet();
        mSourceAppPackages = new ArrayList();
        mAclClickListener = new AclClickListener();
        mSourceClickListener = new SourceClickListener();
        mLocationClickListener = new LocationClickListener();
        mSkyjamClickListener = new SkyjamClickListener(); 
        if(sNamePaint == null)
        {
            Resources resources = getContext().getResources();
            sFontSpacing = resources.getDimension(R.dimen.stream_one_up_font_spacing);
            sAvatarSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_size);
            sMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_bottom);
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_right);
            sTitleMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_title_margin_bottom);
            sAvatarMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_top);
            sAvatarMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_left);
            sAvatarMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_right);
            sNameMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_name_margin_top);
            sLinkedInnerMargin = resources.getDimensionPixelOffset(R.dimen.stream_one_up_linked_inner_margin);
            sLinkedBorderSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_linked_border_size);
            sLinkedBorderRadius = resources.getDimension(R.dimen.stream_one_up_linked_border_radius);
            sLinkedIconMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_linked_icon_margin_right);
            sReshareInnerMargin = resources.getDimensionPixelOffset(R.dimen.stream_one_up_reshare_inner_margin);
            sReshareBorderSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_reshare_border_size);
            sReshareBorderRadius = resources.getDimension(R.dimen.stream_one_up_reshare_border_radius);
            sLocationIconMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_location_icon_margin_top);
            sLocationIconMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_location_icon_margin_right);
            int i = resources.getDimensionPixelOffset(R.dimen.stream_one_up_plus_one_button_margin_right);
            sPlusOneButtonMarginLeft = i;
            sPlusOneButtonMarginRight = i;
            sPlaceReviewDividerMargin = resources.getDimensionPixelOffset(R.dimen.stream_one_up_place_review_divider_margin);
            sPlaceReviewAspectsMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_place_review_aspects_margin_bottom);
            sPlaceReviewAspectsMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_place_review_aspects_margin_top);
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(getContext(), true);
            sLinkedIconBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_link_blue);
            sSkyjamIconBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_music_blue);
            sCheckInIconBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_checkin_small);
            sLocationIconBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_location_card);
            sAvatarOverlayBitmap = ImageUtils.decodeResource(resources, R.drawable.bg_taco_avatar);
            TextPaint textpaint = new TextPaint();
            sNamePaint = textpaint;
            textpaint.setAntiAlias(true);
            sNamePaint.setTypeface(Typeface.DEFAULT_BOLD);
            sNamePaint.setColor(resources.getColor(R.color.stream_one_up_name));
            sNamePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_name_text_size));
            TextPaintUtils.registerTextPaint(sNamePaint, R.dimen.stream_one_up_name_text_size);
            TextPaint textpaint1 = new TextPaint();
            sDatePaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sDatePaint.setColor(resources.getColor(R.color.stream_one_up_date));
            sDatePaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sDatePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_date_text_size));
            TextPaintUtils.registerTextPaint(sDatePaint, R.dimen.stream_one_up_date_text_size);
            sIsMuteColor = resources.getColor(R.color.stream_one_up_muted);
            TextPaint textpaint2 = new TextPaint();
            sContentPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sContentPaint.setColor(resources.getColor(R.color.stream_one_up_content));
            sContentPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sContentPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_content_text_size));
            TextPaintUtils.registerTextPaint(sContentPaint, R.dimen.stream_one_up_content_text_size);
            TextPaint textpaint3 = new TextPaint();
            sLinkedHeaderPaint = textpaint3;
            textpaint3.setAntiAlias(true);
            sLinkedHeaderPaint.setColor(resources.getColor(R.color.stream_one_up_linked_header));
            sLinkedHeaderPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sLinkedHeaderPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sLinkedHeaderPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_linked_header_text_size));
            TextPaintUtils.registerTextPaint(sLinkedHeaderPaint, R.dimen.stream_one_up_linked_header_text_size);
            TextPaint textpaint4 = new TextPaint();
            sLinkedBodyPaint = textpaint4;
            textpaint4.setAntiAlias(true);
            sLinkedBodyPaint.setColor(resources.getColor(R.color.stream_one_up_linked_body));
            sLinkedBodyPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sLinkedBodyPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_linked_body_text_size));
            TextPaintUtils.registerTextPaint(sLinkedBodyPaint, R.dimen.stream_one_up_linked_body_text_size);
            TextPaint textpaint5 = new TextPaint();
            sReshareHeaderPaint = textpaint5;
            textpaint5.setAntiAlias(true);
            sReshareHeaderPaint.setColor(resources.getColor(R.color.stream_one_up_reshare_header));
            sReshareHeaderPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sReshareHeaderPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_reshare_header_text_size));
            TextPaintUtils.registerTextPaint(sReshareHeaderPaint, R.dimen.stream_one_up_reshare_header_text_size);
            TextPaint textpaint6 = new TextPaint();
            sReshareBodyPaint = textpaint6;
            textpaint6.setAntiAlias(true);
            sReshareBodyPaint.setColor(resources.getColor(R.color.stream_one_up_reshare_body));
            sReshareBodyPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sReshareBodyPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_reshare_body_text_size));
            TextPaintUtils.registerTextPaint(sReshareBodyPaint, R.dimen.stream_one_up_reshare_body_text_size);
            TextPaint textpaint7 = new TextPaint();
            sLocationPaint = textpaint7;
            textpaint7.setAntiAlias(true);
            sLocationPaint.setColor(resources.getColor(R.color.stream_one_up_location));
            sLocationPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sLocationPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_location_text_size));
            TextPaintUtils.registerTextPaint(sLocationPaint, R.dimen.stream_one_up_location_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sLinkedBackgroundPaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_linked_background));
            sLinkedBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint2 = new Paint();
            sLinkedBorderPaint = paint2;
            paint2.setColor(resources.getColor(R.color.stream_one_up_linked_border));
            sLinkedBorderPaint.setStrokeWidth(sLinkedBorderSize);
            sLinkedBorderPaint.setStyle(android.graphics.Paint.Style.STROKE);
            Paint paint3 = new Paint();
            sReshareBackgroundPaint = paint3;
            paint3.setColor(resources.getColor(R.color.stream_one_up_reshare_background));
            sReshareBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint4 = new Paint();
            sReshareBorderPaint = paint4;
            paint4.setColor(resources.getColor(R.color.stream_one_up_reshare_border));
            sReshareBorderPaint.setStrokeWidth(sReshareBorderSize);
            sReshareBorderPaint.setStyle(android.graphics.Paint.Style.STROKE);
            Paint paint5 = new Paint();
            sActionBarBackgroundPaint = paint5;
            paint5.setColor(resources.getColor(R.color.stream_one_up_action_bar_background));
            sActionBarBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            sResizePaint = new Paint(2);
            Paint paint6 = new Paint();
            sPlaceReviewDividerPaint = paint6;
            paint6.setColor(resources.getColor(R.color.stream_one_up_place_review_divider));
            sPlaceReviewDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.stream_one_up_place_review_divider_stroke_width));
        }
        setupAccessibility(getContext());
    }

    public StreamOneUpActivityView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mClickableItems = new HashSet();
        mSourceAppPackages = new ArrayList();
        mAclClickListener = new AclClickListener();
        mSourceClickListener = new SourceClickListener();
        mLocationClickListener = new LocationClickListener();
        mSkyjamClickListener = new SkyjamClickListener(); 
        if(sNamePaint == null)
        {
            Resources resources = getContext().getResources();
            sFontSpacing = resources.getDimension(R.dimen.stream_one_up_font_spacing);
            sAvatarSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_size);
            sMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_bottom);
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_right);
            sTitleMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_title_margin_bottom);
            sAvatarMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_top);
            sAvatarMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_left);
            sAvatarMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_right);
            sNameMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_name_margin_top);
            sLinkedInnerMargin = resources.getDimensionPixelOffset(R.dimen.stream_one_up_linked_inner_margin);
            sLinkedBorderSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_linked_border_size);
            sLinkedBorderRadius = resources.getDimension(R.dimen.stream_one_up_linked_border_radius);
            sLinkedIconMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_linked_icon_margin_right);
            sReshareInnerMargin = resources.getDimensionPixelOffset(R.dimen.stream_one_up_reshare_inner_margin);
            sReshareBorderSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_reshare_border_size);
            sReshareBorderRadius = resources.getDimension(R.dimen.stream_one_up_reshare_border_radius);
            sLocationIconMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_location_icon_margin_top);
            sLocationIconMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_location_icon_margin_right);
            int i = resources.getDimensionPixelOffset(R.dimen.stream_one_up_plus_one_button_margin_right);
            sPlusOneButtonMarginLeft = i;
            sPlusOneButtonMarginRight = i;
            sPlaceReviewDividerMargin = resources.getDimensionPixelOffset(R.dimen.stream_one_up_place_review_divider_margin);
            sPlaceReviewAspectsMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_place_review_aspects_margin_bottom);
            sPlaceReviewAspectsMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_place_review_aspects_margin_top);
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(getContext(), true);
            sLinkedIconBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_link_blue);
            sSkyjamIconBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_music_blue);
            sCheckInIconBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_checkin_small);
            sLocationIconBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_location_card);
            sAvatarOverlayBitmap = ImageUtils.decodeResource(resources, R.drawable.bg_taco_avatar);
            TextPaint textpaint = new TextPaint();
            sNamePaint = textpaint;
            textpaint.setAntiAlias(true);
            sNamePaint.setTypeface(Typeface.DEFAULT_BOLD);
            sNamePaint.setColor(resources.getColor(R.color.stream_one_up_name));
            sNamePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_name_text_size));
            TextPaintUtils.registerTextPaint(sNamePaint, R.dimen.stream_one_up_name_text_size);
            TextPaint textpaint1 = new TextPaint();
            sDatePaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sDatePaint.setColor(resources.getColor(R.color.stream_one_up_date));
            sDatePaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sDatePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_date_text_size));
            TextPaintUtils.registerTextPaint(sDatePaint, R.dimen.stream_one_up_date_text_size);
            sIsMuteColor = resources.getColor(R.color.stream_one_up_muted);
            TextPaint textpaint2 = new TextPaint();
            sContentPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sContentPaint.setColor(resources.getColor(R.color.stream_one_up_content));
            sContentPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sContentPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_content_text_size));
            TextPaintUtils.registerTextPaint(sContentPaint, R.dimen.stream_one_up_content_text_size);
            TextPaint textpaint3 = new TextPaint();
            sLinkedHeaderPaint = textpaint3;
            textpaint3.setAntiAlias(true);
            sLinkedHeaderPaint.setColor(resources.getColor(R.color.stream_one_up_linked_header));
            sLinkedHeaderPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sLinkedHeaderPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sLinkedHeaderPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_linked_header_text_size));
            TextPaintUtils.registerTextPaint(sLinkedHeaderPaint, R.dimen.stream_one_up_linked_header_text_size);
            TextPaint textpaint4 = new TextPaint();
            sLinkedBodyPaint = textpaint4;
            textpaint4.setAntiAlias(true);
            sLinkedBodyPaint.setColor(resources.getColor(R.color.stream_one_up_linked_body));
            sLinkedBodyPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sLinkedBodyPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_linked_body_text_size));
            TextPaintUtils.registerTextPaint(sLinkedBodyPaint, R.dimen.stream_one_up_linked_body_text_size);
            TextPaint textpaint5 = new TextPaint();
            sReshareHeaderPaint = textpaint5;
            textpaint5.setAntiAlias(true);
            sReshareHeaderPaint.setColor(resources.getColor(R.color.stream_one_up_reshare_header));
            sReshareHeaderPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sReshareHeaderPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_reshare_header_text_size));
            TextPaintUtils.registerTextPaint(sReshareHeaderPaint, R.dimen.stream_one_up_reshare_header_text_size);
            TextPaint textpaint6 = new TextPaint();
            sReshareBodyPaint = textpaint6;
            textpaint6.setAntiAlias(true);
            sReshareBodyPaint.setColor(resources.getColor(R.color.stream_one_up_reshare_body));
            sReshareBodyPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sReshareBodyPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_reshare_body_text_size));
            TextPaintUtils.registerTextPaint(sReshareBodyPaint, R.dimen.stream_one_up_reshare_body_text_size);
            TextPaint textpaint7 = new TextPaint();
            sLocationPaint = textpaint7;
            textpaint7.setAntiAlias(true);
            sLocationPaint.setColor(resources.getColor(R.color.stream_one_up_location));
            sLocationPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sLocationPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_location_text_size));
            TextPaintUtils.registerTextPaint(sLocationPaint, R.dimen.stream_one_up_location_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sLinkedBackgroundPaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_linked_background));
            sLinkedBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint2 = new Paint();
            sLinkedBorderPaint = paint2;
            paint2.setColor(resources.getColor(R.color.stream_one_up_linked_border));
            sLinkedBorderPaint.setStrokeWidth(sLinkedBorderSize);
            sLinkedBorderPaint.setStyle(android.graphics.Paint.Style.STROKE);
            Paint paint3 = new Paint();
            sReshareBackgroundPaint = paint3;
            paint3.setColor(resources.getColor(R.color.stream_one_up_reshare_background));
            sReshareBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint4 = new Paint();
            sReshareBorderPaint = paint4;
            paint4.setColor(resources.getColor(R.color.stream_one_up_reshare_border));
            sReshareBorderPaint.setStrokeWidth(sReshareBorderSize);
            sReshareBorderPaint.setStyle(android.graphics.Paint.Style.STROKE);
            Paint paint5 = new Paint();
            sActionBarBackgroundPaint = paint5;
            paint5.setColor(resources.getColor(R.color.stream_one_up_action_bar_background));
            sActionBarBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            sResizePaint = new Paint(2);
            Paint paint6 = new Paint();
            sPlaceReviewDividerPaint = paint6;
            paint6.setColor(resources.getColor(R.color.stream_one_up_place_review_divider));
            sPlaceReviewDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.stream_one_up_place_review_divider_stroke_width));
        }
        setupAccessibility(getContext());
    }

    public StreamOneUpActivityView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mClickableItems = new HashSet();
        mSourceAppPackages = new ArrayList();
        mAclClickListener = new AclClickListener();
        mSourceClickListener = new SourceClickListener();
        mLocationClickListener = new LocationClickListener();
        mSkyjamClickListener = new SkyjamClickListener(); 
        if(sNamePaint == null)
        {
            Resources resources = getContext().getResources();
            sFontSpacing = resources.getDimension(R.dimen.stream_one_up_font_spacing);
            sAvatarSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_size);
            sMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_bottom);
            sMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_left);
            sMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_margin_right);
            sTitleMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_title_margin_bottom);
            sAvatarMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_top);
            sAvatarMarginLeft = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_left);
            sAvatarMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_avatar_margin_right);
            sNameMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_name_margin_top);
            sLinkedInnerMargin = resources.getDimensionPixelOffset(R.dimen.stream_one_up_linked_inner_margin);
            sLinkedBorderSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_linked_border_size);
            sLinkedBorderRadius = resources.getDimension(R.dimen.stream_one_up_linked_border_radius);
            sLinkedIconMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_linked_icon_margin_right);
            sReshareInnerMargin = resources.getDimensionPixelOffset(R.dimen.stream_one_up_reshare_inner_margin);
            sReshareBorderSize = resources.getDimensionPixelOffset(R.dimen.stream_one_up_reshare_border_size);
            sReshareBorderRadius = resources.getDimension(R.dimen.stream_one_up_reshare_border_radius);
            sLocationIconMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_location_icon_margin_top);
            sLocationIconMarginRight = resources.getDimensionPixelOffset(R.dimen.stream_one_up_location_icon_margin_right);
            int j = resources.getDimensionPixelOffset(R.dimen.stream_one_up_plus_one_button_margin_right);
            sPlusOneButtonMarginLeft = j;
            sPlusOneButtonMarginRight = j;
            sPlaceReviewDividerMargin = resources.getDimensionPixelOffset(R.dimen.stream_one_up_place_review_divider_margin);
            sPlaceReviewAspectsMarginBottom = resources.getDimensionPixelOffset(R.dimen.stream_one_up_place_review_aspects_margin_bottom);
            sPlaceReviewAspectsMarginTop = resources.getDimensionPixelOffset(R.dimen.stream_one_up_place_review_aspects_margin_top);
            sDefaultAvatarBitmap = EsAvatarData.getMediumDefaultAvatar(getContext(), true);
            sLinkedIconBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_link_blue);
            sSkyjamIconBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_music_blue);
            sCheckInIconBitmap = ImageUtils.decodeResource(resources, R.drawable.ic_checkin_small);
            sLocationIconBitmap = ImageUtils.decodeResource(resources, R.drawable.icn_location_card);
            sAvatarOverlayBitmap = ImageUtils.decodeResource(resources, R.drawable.bg_taco_avatar);
            TextPaint textpaint = new TextPaint();
            sNamePaint = textpaint;
            textpaint.setAntiAlias(true);
            sNamePaint.setTypeface(Typeface.DEFAULT_BOLD);
            sNamePaint.setColor(resources.getColor(R.color.stream_one_up_name));
            sNamePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_name_text_size));
            TextPaintUtils.registerTextPaint(sNamePaint, R.dimen.stream_one_up_name_text_size);
            TextPaint textpaint1 = new TextPaint();
            sDatePaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sDatePaint.setColor(resources.getColor(R.color.stream_one_up_date));
            sDatePaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sDatePaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_date_text_size));
            TextPaintUtils.registerTextPaint(sDatePaint, R.dimen.stream_one_up_date_text_size);
            sIsMuteColor = resources.getColor(R.color.stream_one_up_muted);
            TextPaint textpaint2 = new TextPaint();
            sContentPaint = textpaint2;
            textpaint2.setAntiAlias(true);
            sContentPaint.setColor(resources.getColor(R.color.stream_one_up_content));
            sContentPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sContentPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_content_text_size));
            TextPaintUtils.registerTextPaint(sContentPaint, R.dimen.stream_one_up_content_text_size);
            TextPaint textpaint3 = new TextPaint();
            sLinkedHeaderPaint = textpaint3;
            textpaint3.setAntiAlias(true);
            sLinkedHeaderPaint.setColor(resources.getColor(R.color.stream_one_up_linked_header));
            sLinkedHeaderPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sLinkedHeaderPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sLinkedHeaderPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_linked_header_text_size));
            TextPaintUtils.registerTextPaint(sLinkedHeaderPaint, R.dimen.stream_one_up_linked_header_text_size);
            TextPaint textpaint4 = new TextPaint();
            sLinkedBodyPaint = textpaint4;
            textpaint4.setAntiAlias(true);
            sLinkedBodyPaint.setColor(resources.getColor(R.color.stream_one_up_linked_body));
            sLinkedBodyPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sLinkedBodyPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_linked_body_text_size));
            TextPaintUtils.registerTextPaint(sLinkedBodyPaint, R.dimen.stream_one_up_linked_body_text_size);
            TextPaint textpaint5 = new TextPaint();
            sReshareHeaderPaint = textpaint5;
            textpaint5.setAntiAlias(true);
            sReshareHeaderPaint.setColor(resources.getColor(R.color.stream_one_up_reshare_header));
            sReshareHeaderPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sReshareHeaderPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_reshare_header_text_size));
            TextPaintUtils.registerTextPaint(sReshareHeaderPaint, R.dimen.stream_one_up_reshare_header_text_size);
            TextPaint textpaint6 = new TextPaint();
            sReshareBodyPaint = textpaint6;
            textpaint6.setAntiAlias(true);
            sReshareBodyPaint.setColor(resources.getColor(R.color.stream_one_up_reshare_body));
            sReshareBodyPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sReshareBodyPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_reshare_body_text_size));
            TextPaintUtils.registerTextPaint(sReshareBodyPaint, R.dimen.stream_one_up_reshare_body_text_size);
            TextPaint textpaint7 = new TextPaint();
            sLocationPaint = textpaint7;
            textpaint7.setAntiAlias(true);
            sLocationPaint.setColor(resources.getColor(R.color.stream_one_up_location));
            sLocationPaint.linkColor = resources.getColor(R.color.stream_one_up_link);
            sLocationPaint.setTextSize(resources.getDimension(R.dimen.stream_one_up_location_text_size));
            TextPaintUtils.registerTextPaint(sLocationPaint, R.dimen.stream_one_up_location_text_size);
            Paint paint = new Paint();
            sBackgroundPaint = paint;
            paint.setColor(resources.getColor(R.color.stream_one_up_list_background));
            sBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint1 = new Paint();
            sLinkedBackgroundPaint = paint1;
            paint1.setColor(resources.getColor(R.color.stream_one_up_linked_background));
            sLinkedBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint2 = new Paint();
            sLinkedBorderPaint = paint2;
            paint2.setColor(resources.getColor(R.color.stream_one_up_linked_border));
            sLinkedBorderPaint.setStrokeWidth(sLinkedBorderSize);
            sLinkedBorderPaint.setStyle(android.graphics.Paint.Style.STROKE);
            Paint paint3 = new Paint();
            sReshareBackgroundPaint = paint3;
            paint3.setColor(resources.getColor(R.color.stream_one_up_reshare_background));
            sReshareBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            Paint paint4 = new Paint();
            sReshareBorderPaint = paint4;
            paint4.setColor(resources.getColor(R.color.stream_one_up_reshare_border));
            sReshareBorderPaint.setStrokeWidth(sReshareBorderSize);
            sReshareBorderPaint.setStyle(android.graphics.Paint.Style.STROKE);
            Paint paint5 = new Paint();
            sActionBarBackgroundPaint = paint5;
            paint5.setColor(resources.getColor(R.color.stream_one_up_action_bar_background));
            sActionBarBackgroundPaint.setStyle(android.graphics.Paint.Style.FILL);
            sResizePaint = new Paint(2);
            Paint paint6 = new Paint();
            sPlaceReviewDividerPaint = paint6;
            paint6.setColor(resources.getColor(R.color.stream_one_up_place_review_divider));
            sPlaceReviewDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.stream_one_up_place_review_divider_stroke_width));
        }
        setupAccessibility(getContext());
    }

    private void clearLayoutState()
    {
        unbindResources();
        mAuthorLayout = null;
        mDateSourceAclLayout = null;
        mAnnotationLayout = null;
        mTitleLayout = null;
        mLinkedHeaderLayout = null;
        mLinkedBodyLayout = null;
        mReshareHeaderLayout = null;
        mReshareBodyLayout = null;
        mPlaceReviewBodyLayout = null;
        mPlaceReviewAspectsLayout = null;
        mAuthorImage = null;
        mLocationIcon = null;
        mClickableItems.clear();
        mCurrentClickableItem = null;
        mLinkedContentBorder = null;
        mSkyjamContentBorder = null;
        mLinkedIconRect = null;
        mSkyjamIconRect = null;
        mLocationIconRect = null;
        mReshareContentBorder = null;
        mLocationData = null;
        mPlusOneData = null;
        mAnnotation = null;
        mTitle = null;
        mLinkedHeader = null;
        mLinkedBody = null;
        mSkyjamHeader = null;
        mSkyjamSubheader1 = null;
        mSkyjamSubheader2 = null;
        mReshareHeader = null;
        mReshareBody = null;
        mLocation = null;
        mPlaceReview = null;
        mPlaceReviewDividerRect = null;
    }

    private int measureAndLayoutLinkedContent(int i, int j, int k)
    {
        if((!TextUtils.isEmpty(mLinkedHeader) || !TextUtils.isEmpty(mLinkedBody)) && mPlaceReview == null)
        {
            int l = i + sLinkedInnerMargin;
            int i1 = j + sLinkedInnerMargin;
            Object obj;
            Spannable spannable;
            int j1;
            int k1;
            int l1;
            int i2;
            if(mSourceAppPackages.isEmpty())
                obj = mOneUpListener;
            else
                obj = mSourceClickListener;
            spannable = mLinkedHeader;
            j1 = 0;
            k1 = 0;
            l1 = 0;
            i2 = 0;
            if(spannable != null)
            {
                int k2 = sLinkedIconBitmap.getWidth();
                int l2 = sLinkedIconBitmap.getHeight();
                mLinkedIconRect = new Rect(l, i1, l + k2, i1 + l2);
                int i3 = l + (k2 + sLinkedIconMarginRight);
                int j3 = k - 2 * sLinkedInnerMargin - k2 - sLinkedIconMarginRight;
                mClickableItems.remove(mLinkedHeaderLayout);
                mLinkedHeaderLayout = new ClickableStaticLayout(mLinkedHeader, sLinkedHeaderPaint, j3, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, ((ClickableStaticLayout.SpanClickListener) (obj)));
                mLinkedHeaderLayout.setPosition(i3, i1);
                mClickableItems.add(mLinkedHeaderLayout);
                k1 = mLinkedIconRect.left - sLinkedInnerMargin;
                i2 = mLinkedIconRect.top - sLinkedInnerMargin;
                l1 = mLinkedHeaderLayout.getRight() + sLinkedInnerMargin;
                j1 = mLinkedHeaderLayout.getBottom() + sLinkedInnerMargin;
                l = i3 - (k2 + sLinkedIconMarginRight);
                i1 = Math.max(i1 + l2, j1);
            }
            if(mLinkedBody != null)
            {
                int j2 = k - 2 * sLinkedInnerMargin;
                mClickableItems.remove(mLinkedBodyLayout);
                mLinkedBodyLayout = new ClickableStaticLayout(mLinkedBody, sLinkedBodyPaint, j2, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, ((ClickableStaticLayout.SpanClickListener) (obj)));
                mLinkedBodyLayout.setPosition(l, i1);
                mClickableItems.add(mLinkedBodyLayout);
                if(k1 == 0)
                {
                    k1 = mLinkedBodyLayout.getLeft() - sLinkedInnerMargin;
                    i2 = mLinkedBodyLayout.getTop() - sLinkedInnerMargin;
                }
                l1 = mLinkedBodyLayout.getRight() + sLinkedInnerMargin;
                j1 = mLinkedBodyLayout.getBottom() + sLinkedInnerMargin;
            }
            mLinkedContentBorder = new RectF(k1, i2, l1, j1);
            j = j1;
        }
        return j;
    }

    private int measureAndLayoutLocation(int i, int j, int k)
    {
        if(!TextUtils.isEmpty(mLocation))
        {
            int l = mLocationIcon.getWidth();
            int i1 = mLocationIcon.getHeight();
            int j1;
            int k1;
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            if(mIsCheckin)
                j1 = sLocationIconMarginTop;
            else
                j1 = 0;
            k1 = j + j1;
            l1 = i + l;
            i2 = j + i1;
            if(mIsCheckin)
                j2 = sLocationIconMarginTop;
            else
                j2 = 0;
            mLocationIconRect = new Rect(i, k1, l1, j2 + i2);
            k2 = i + (l + sLocationIconMarginRight);
            l2 = k - l - sLocationIconMarginRight;
            mClickableItems.remove(mLocationLayout);
            mLocationLayout = new ClickableStaticLayout(mLocation, sLocationPaint, l2, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mLocationClickListener);
            mLocationLayout.setPosition(k2, j);
            mClickableItems.add(mLocationLayout);
            j = Math.max(i1, mLocationLayout.getBottom());
        }
        return j;
    }

    private int measureAndLayoutPlaceReviewContent(int i, int j, int k)
    {
        if(mPlaceReview != null)
        {
            int l = j;
            SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder();
            if(mPlaceReview.reviewRating != null)
            {
                int j1 = 0;
                for(int k1 = mPlaceReview.reviewRating.size(); j1 < k1; j1++)
                {
                    Rating rating = (Rating)mPlaceReview.reviewRating.get(j1);
                    String s = rating.name;
                    String s1 = rating.ratingValue;
                    if(rating.clientDisplayData != null && !TextUtils.isEmpty(rating.clientDisplayData.renderedRatingText))
                        s1 = rating.clientDisplayData.renderedRatingText;
                    if(TextUtils.isEmpty(s) || TextUtils.isEmpty(s1))
                        continue;
                    SpannableUtils.appendWithSpan(spannablestringbuilder, s, new TextAppearanceSpan(getContext(), R.style.ProfileLocalUserRating_AspectLabel));
                    spannablestringbuilder.append("\240");
                    SpannableUtils.appendWithSpan(spannablestringbuilder, s1, new TextAppearanceSpan(getContext(), R.style.ProfileLocalUserRating_AspectValue));
                    if(j1 != k1 - 1)
                        spannablestringbuilder.append("  ");
                }

            }
            if(spannablestringbuilder.length() > 0)
            {
                int i1 = j + sPlaceReviewAspectsMarginTop;
                mPlaceReviewAspectsLayout = new PositionedStaticLayout(spannablestringbuilder, sContentPaint, k, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false);
                mPlaceReviewAspectsLayout.setPosition(i, i1);
                l = i1 + (mPlaceReviewAspectsLayout.getHeight() + sPlaceReviewAspectsMarginBottom);
            }
            if(!TextUtils.isEmpty(mPlaceReview.reviewBody))
            {
                mPlaceReviewBodyLayout = new PositionedStaticLayout(mPlaceReview.reviewBody, sContentPaint, k, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false);
                mPlaceReviewBodyLayout.setPosition(i, l);
                l += mPlaceReviewBodyLayout.getHeight();
            }
            j = l;
        }
        return j;
    }

    private int measureAndLayoutPlaceReviewDivider(int i, int j, int k)
    {
        if(mPlaceReview != null)
        {
            int l = j + sPlaceReviewDividerMargin;
            mPlaceReviewDividerRect = new Rect(i, l, i + k, l);
            j = l + sPlaceReviewDividerMargin;
        }
        return j;
    }

    private int measureAndLayoutSkyjamContent(int i, int j, int k)
    {
        if(!TextUtils.isEmpty(mSkyjamHeader))
        {
            int l = i + sLinkedInnerMargin;
            int i1 = j + sLinkedInnerMargin;
            int j1 = sSkyjamIconBitmap.getWidth();
            int k1 = sSkyjamIconBitmap.getHeight();
            mSkyjamIconRect = new Rect(l, i1, l + j1, i1 + k1);
            int l1 = l + (j1 + sLinkedIconMarginRight);
            int i2 = k - 2 * sLinkedInnerMargin - j1 - sLinkedIconMarginRight;
            mClickableItems.remove(mSkyjamHeaderLayout);
            mSkyjamHeaderLayout = new ClickableStaticLayout(mSkyjamHeader, sLinkedHeaderPaint, i2, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mSkyjamClickListener);
            mSkyjamHeaderLayout.setPosition(l1, i1);
            mClickableItems.add(mSkyjamHeaderLayout);
            int j2 = mSkyjamIconRect.left - sLinkedInnerMargin;
            int k2 = mSkyjamIconRect.top - sLinkedInnerMargin;
            int l2 = mSkyjamHeaderLayout.getRight() + sLinkedInnerMargin;
            int i3 = mSkyjamHeaderLayout.getBottom() + sLinkedInnerMargin;
            int j3 = l1 - (j1 + sLinkedIconMarginRight);
            int k3 = Math.max(i1 + k1, i3);
            int l3 = k - 2 * sLinkedInnerMargin;
            mSkyjamSubheader1Layout = new PositionedStaticLayout(mSkyjamSubheader1, sLinkedBodyPaint, l3, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false);
            mSkyjamSubheader1Layout.setPosition(j3, k3);
            if(j2 == 0)
            {
                j2 = mSkyjamSubheader1Layout.getLeft() - sLinkedInnerMargin;
                k2 = mSkyjamSubheader1Layout.getTop() - sLinkedInnerMargin;
            }
            int i4 = Math.max(l2, mSkyjamSubheader1Layout.getRight() + sLinkedInnerMargin);
            int j4 = mSkyjamSubheader1Layout.getBottom() + sLinkedInnerMargin;
            mClickableItems.remove(mSkyjamSubheader2Layout);
            mSkyjamSubheader2Layout = new ClickableStaticLayout(mSkyjamSubheader2, sLinkedBodyPaint, l3, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mSkyjamClickListener);
            mSkyjamSubheader2Layout.setPosition(j3, j4);
            mClickableItems.add(mSkyjamSubheader2Layout);
            int k4 = Math.max(i4, mSkyjamSubheader2Layout.getRight() + sLinkedInnerMargin);
            int l4 = mSkyjamSubheader2Layout.getBottom() + sLinkedInnerMargin;
            mSkyjamContentBorder = new RectF(j2, k2, k4, l4);
            j = l4;
        }
        return j;
    }

    private int measureAndLayoutTitle(int i, int j, int k)
    {
        if(!TextUtils.isEmpty(mTitle))
        {
            mClickableItems.remove(mTitleLayout);
            mTitleLayout = new ClickableStaticLayout(mTitle, sContentPaint, k, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mOneUpListener);
            mTitleLayout.setPosition(i, j);
            mClickableItems.add(mTitleLayout);
            j = mTitleLayout.getBottom();
        }
        return j;
    }

    private void setupAccessibility(Context context)
    {
        if(android.os.Build.VERSION.SDK_INT >= 16 && AccessibilityUtils.isAccessibilityEnabled(context) && mTouchExplorer == null)
        {
            mTouchExplorer = new OneUpActivityTouchExplorer(context);
            mTouchExplorer.install(this);
        }
    }

    private void updateAccessibility()
    {
        if(mTouchExplorer != null)
        {
            mTouchExplorer.invalidateItemCache();
            mTouchExplorer.invalidateParent();
        }
    }

    public final void bind(Cursor cursor)
    {
        unbindResources();
        Context context = getContext();
        Resources resources = getResources();
        clearLayoutState();
        String s = cursor.getString(2);
        String s1 = cursor.getString(4);
        long l = cursor.getLong(21);
        boolean flag;
        boolean flag1;
        String s2;
        String s3;
        String s4;
        String s5;
        boolean flag2;
        if((16L & l) != 0L)
            flag = true;
        else
            flag = false;
        mIsCheckin = flag;
        mDbEmbedSquare = DbEmbedSquare.deserialize(cursor.getBlob(32));
        mActivityId = s;
        mAclText = cursor.getString(3);
        mCreationSource = cursor.getString(14);
        mAuthorId = s1;
        mAuthorName = cursor.getString(5);
        if(mAuthorName == null)
        {
            mAuthorName = "";
            Log.w("StreamOneUp", (new StringBuilder("===> Author name was null for gaia id: ")).append(s1).toString());
        }
        mAuthorImage = new ClickableAvatar(this, s1, EsAvatarData.uncompressAvatarUrl(cursor.getString(6)), mAuthorName, mOneUpListener, 2);
        mClickableItems.add(mAuthorImage);
        mDate = Dates.getAbbreviatedRelativeTimeSpanString(context, cursor.getLong(10)).toString();
        if(1 == cursor.getInt(11))
            flag1 = true;
        else
            flag1 = false;
        mEdited = flag1;
        if(cursor.getInt(19) != 0)
            mMuteState = resources.getString(R.string.stream_one_up_is_muted);
        else
            mMuteState = null;
        s2 = cursor.getString(24);
        s3 = cursor.getString(25);
        if(!TextUtils.isEmpty(s2) && !TextUtils.isEmpty(s3))
        {
            String s14 = resources.getString(R.string.stream_one_up_reshare_header, new Object[] {
                s3
            });
            java.util.Locale locale = resources.getConfiguration().locale;
            String s15 = s14.toUpperCase(locale);
            String s16 = s3.toUpperCase(locale);
            ClickableStaticLayout.StateURLSpan stateurlspan4 = new ClickableStaticLayout.StateURLSpan(Intents.makeProfileUrl(s2));
            int j1 = s15.indexOf(s16);
            int k1 = j1 + s16.length();
            mReshareHeader = new SpannableStringBuilder(s15);
            mReshareHeader.setSpan(stateurlspan4, j1, k1, 33);
        }
        s4 = cursor.getString(22);
        if(!TextUtils.isEmpty(s4))
            mAnnotation = ClickableStaticLayout.buildStateSpans(s4);
        
        long i;
        byte abyte0[];
        byte abyte2[];
        byte abyte3[];
        StringBuilder stringbuilder;
        String s8;
        ClickableStaticLayout.StateURLSpan stateurlspan2;
        byte abyte4[];
        DbEmbedMedia dbembedmedia;
        String s11;
        String s12;
        byte abyte5[];
        DbEmbedDeepLink dbembeddeeplink;
        boolean flag3;
        
        if((8192L & l) != 0L)
        {
            byte abyte6[] = cursor.getBlob(31);
            if(abyte6 != null)
            {
                if(DbEmbedHangout.deserialize(abyte6).isInProgress())
                {
                    int i1 = R.string.card_hangout_state_active;
                    Object aobj1[] = new Object[1];
                    aobj1[0] = mAuthorName;
                    s5 = resources.getString(i1, aobj1);
                } else
                {
                    int k = R.string.card_hangout_state_inactive;
                    Object aobj[] = new Object[1];
                    aobj[0] = mAuthorName;
                    s5 = resources.getString(k, aobj);
                }
            } else
            {
                s5 = null;
            }
        } else
        {
            s5 = cursor.getString(23);
        }
        if(!TextUtils.isEmpty(s5))
            mTitle = ClickableStaticLayout.buildStateSpans(s5);
        mSourceAppPackages.clear();
        i = (32768L & l) - 0L;
        flag2 = false;
        if(i != 0)
        {
            abyte5 = cursor.getBlob(26);
            flag2 = false;
            if(abyte5 != null)
            {
                dbembeddeeplink = DbEmbedDeepLink.deserialize(abyte5);
                mSourceAppData = dbembeddeeplink.getDeepLinkId();
                mSourceAppPackages.addAll(dbembeddeeplink.getClientPackageNames());
                flag3 = mSourceAppPackages.isEmpty();
                flag2 = false;
                if(!flag3)
                    flag2 = true;
            }
        }
        abyte0 = cursor.getBlob(28);
        if(abyte0 != null)
        {
            dbembedmedia = DbEmbedMedia.deserialize(abyte0);
            s11 = dbembedmedia.getTitle();
            if(!TextUtils.isEmpty(s11))
            {
                String s13;
                if(!TextUtils.isEmpty(dbembedmedia.getContentUrl()))
                    s13 = dbembedmedia.getContentUrl();
                else
                if(flag2)
                    s13 = "";
                else
                    s13 = null;
                if(s13 != null)
                    s11 = (new StringBuilder("<a href=\"")).append(s13).append("\">").append(s11).append("</a>").toString();
                mLinkedHeader = ClickableStaticLayout.buildStateSpans(s11);
            }
            s12 = dbembedmedia.getDescription();
            if(!TextUtils.isEmpty(s12))
                mLinkedBody = ClickableStaticLayout.buildStateSpans(s12);
        } else
        {
            byte abyte1[] = cursor.getBlob(29);
            if(abyte1 != null)
            {
                DbEmbedSkyjam dbembedskyjam = DbEmbedSkyjam.deserialize(abyte1);
                String s6 = Uri.decode(dbembedskyjam.getMarketUrl());
                SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder();
                ClickableStaticLayout.StateURLSpan stateurlspan;
                if(dbembedskyjam.isAlbum())
                {
                    spannablestringbuilder.append(dbembedskyjam.getAlbum());
                    String s9 = dbembedskyjam.getPreviewUrl();
                    int j = s9.indexOf("https://");
                    if(j < 0)
                        j = s9.indexOf("https://");
                    if(j >= 0)
                    {
                        String s10 = s9.substring(j);
                        SpannableStringBuilder spannablestringbuilder1 = new SpannableStringBuilder(getResources().getString(R.string.skyjam_listen));
                        ClickableStaticLayout.StateURLSpan stateurlspan3 = new ClickableStaticLayout.StateURLSpan((new StringBuilder("skyjam:listen:")).append(s10).toString());
                        spannablestringbuilder1.setSpan(stateurlspan3, 0, spannablestringbuilder1.length(), 33);
                        mSkyjamSubheader2 = spannablestringbuilder1;
                    }
                } else
                {
                    spannablestringbuilder.append(dbembedskyjam.getSong());
                    mSkyjamSubheader2 = new SpannableString(dbembedskyjam.getAlbum());
                }
                stateurlspan = new ClickableStaticLayout.StateURLSpan((new StringBuilder("skyjam:buy:")).append(s6).toString());
                spannablestringbuilder.setSpan(stateurlspan, 0, spannablestringbuilder.length(), 33);
                mSkyjamHeader = spannablestringbuilder;
                mSkyjamSubheader1 = new SpannableString(dbembedskyjam.getArtist());
            }
        }
        abyte2 = cursor.getBlob(8);
        if(abyte2 != null)
            mPlusOneData = DbPlusOneData.deserialize(abyte2);
        if((0x10000L & l) != 0L)
        {
            abyte4 = cursor.getBlob(30);
            mPlaceReview = (PlaceReview)JsonUtil.fromByteArray(abyte4, PlaceReview.class);
        }
        abyte3 = cursor.getBlob(9);
        if(abyte3 != null)
        {
            mLocationData = DbLocation.deserialize(abyte3);
            if(mLocationIcon == null)
            {
                Bitmap bitmap;
                if(mIsCheckin)
                    bitmap = sCheckInIconBitmap;
                else
                    bitmap = sLocationIconBitmap;
                mLocationIcon = bitmap;
            }
            s8 = mLocationData.getLocationName();
            stateurlspan2 = new ClickableStaticLayout.StateURLSpan(s8);
            mLocation = new SpannableStringBuilder(s8);
            mLocation.setSpan(stateurlspan2, 0, s8.length(), 33);
        } else
        if(mPlaceReview != null && !TextUtils.isEmpty(mPlaceReview.name))
        {
            mLocationIcon = sLocationIconBitmap;
            String s7 = mPlaceReview.name;
            ClickableStaticLayout.StateURLSpan stateurlspan1 = new ClickableStaticLayout.StateURLSpan(s7);
            mLocation = new SpannableStringBuilder(s7);
            mLocation.setSpan(stateurlspan1, 0, s7.length(), 33);
        }
        if(android.os.Build.VERSION.SDK_INT < 16)
        {
            stringbuilder = new StringBuilder();
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mAuthorName);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mDate);
            if(mEdited)
                AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, context.getString(R.string.stream_one_up_is_edited));
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mCreationSource);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mAclText);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mMuteState);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mAnnotation);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mTitle);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mLinkedHeader);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mLinkedBody);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mSkyjamHeader);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mSkyjamSubheader1);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mSkyjamSubheader2);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mReshareHeader);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, null);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mLocation);
            setContentDescription(stringbuilder.toString());
            setFocusable(true);
        }
        bindResources();
        invalidate();
        requestLayout();
    }

    public final void bindResources()
    {
        if(mAuthorImage != null)
            mAuthorImage.bindResources();
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent)
    {
        boolean flag;
        int i;
        int j;
        flag = true;
        i = (int)motionevent.getX();
        j = (int)motionevent.getY();
        switch(motionevent.getAction()) {
        case 0:
        	Iterator iterator1 = mClickableItems.iterator();
            do
            {
                ClickableItem clickableitem = null;
                do
                {
                    if(!iterator1.hasNext()) {
                    	break;
                    }
                    clickableitem = (ClickableItem)iterator1.next();
                } while(!clickableitem.handleEvent(i, j, 0));
                mCurrentClickableItem = clickableitem;
                invalidate();
            } while(true);
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
        bindResources();
        updateAccessibility();
    }

    public final void onClickableButtonListenerClick(ClickableButton clickablebutton)
    {
        if(mOneUpListener != null && clickablebutton == mPlusOneButton)
        {
            mOneUpListener.onPlusOne(mActivityId, mPlusOneData);
            if(AccessibilityUtils.isAccessibilityEnabled(getContext()))
            {
                boolean flag;
                int i;
                AccessibilityEvent accessibilityevent;
                if(mPlusOneData != null && mPlusOneData.isPlusOnedByMe())
                    flag = true;
                else
                    flag = false;
                if(flag)
                    i = R.string.plus_one_removed_confirmation;
                else
                    i = R.string.plus_one_added_confirmation;
                accessibilityevent = AccessibilityEvent.obtain(16384);
                accessibilityevent.getText().add(getResources().getString(i));
                onInitializeAccessibilityEvent(accessibilityevent);
                accessibilityevent.setContentDescription(null);
                getParent().requestSendAccessibilityEvent(this, accessibilityevent);
            }
        }
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        unbindResources();
        if(mTouchExplorer != null)
        {
            mTouchExplorer.uninstall();
            mTouchExplorer = null;
        }
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int i;
        int j;
        int k;
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        int k2;
        int l2;
        int i3;
        int j3;
        int k3;
        int l3;
        int i4;
        int j4;
        int k4;
        int l4;
        int i5;
        int j5;
        int k5;
        int l5;
        int i6;
        int j6;
        Bitmap bitmap;
        int k6;
        int l6;
        int i7;
        int j7;
        canvas.drawRect(0.0F, mBackgroundOffset, getWidth(), getHeight(), sBackgroundPaint);
        if(mDateSourceAclLayout != null)
        {
            if(mAuthorImage.getBitmap() != null)
                bitmap = mAuthorImage.getBitmap();
            else
                bitmap = sDefaultAvatarBitmap;
            canvas.drawBitmap(bitmap, null, mAuthorImage.getRect(), sResizePaint);
            canvas.drawBitmap(sAvatarOverlayBitmap, null, mAuthorImage.getRect(), sResizePaint);
            if(mAuthorImage.isClicked())
                mAuthorImage.drawSelectionRect(canvas);
            mPlusOneButton.draw(canvas);
            k6 = mDateSourceAclLayout.getLeft();
            l6 = mDateSourceAclLayout.getTop();
            canvas.translate(k6, l6);
            mDateSourceAclLayout.draw(canvas);
            canvas.translate(-k6, -l6);
            i7 = mAuthorLayout.getLeft();
            j7 = mAuthorLayout.getTop();
            canvas.translate(i7, j7);
            mAuthorLayout.draw(canvas);
            canvas.translate(-i7, -j7);
        }
        if(mAnnotationLayout != null)
        {
            i6 = mAnnotationLayout.getLeft();
            j6 = mAnnotationLayout.getTop();
            canvas.translate(i6, j6);
            mAnnotationLayout.draw(canvas);
            canvas.translate(-i6, -j6);
        }
        if(mReshareHeaderLayout != null || mReshareBodyLayout != null)
        {
            canvas.drawRoundRect(mReshareContentBorder, sReshareBorderRadius, sReshareBorderRadius, sReshareBackgroundPaint);
            canvas.drawRoundRect(mReshareContentBorder, sReshareBorderRadius, sReshareBorderRadius, sReshareBorderPaint);
            if(mReshareHeaderLayout != null)
            {
                k5 = mReshareHeaderLayout.getLeft();
                l5 = mReshareHeaderLayout.getTop();
                canvas.translate(k5, l5);
                mReshareHeaderLayout.draw(canvas);
                canvas.translate(-k5, -l5);
            }
            if(mReshareBodyLayout != null)
            {
                i5 = mReshareBodyLayout.getLeft();
                j5 = mReshareBodyLayout.getTop();
                canvas.translate(i5, j5);
                mReshareBodyLayout.draw(canvas);
                canvas.translate(-i5, -j5);
            }
        }
        if(mTitleLayout != null)
        {
            k4 = mTitleLayout.getLeft();
            l4 = mTitleLayout.getTop();
            canvas.translate(k4, l4);
            mTitleLayout.draw(canvas);
            canvas.translate(-k4, -l4);
        }
        if(mPlaceReviewDividerRect != null)
        {
            i4 = mPlaceReviewDividerRect.left;
            j4 = mPlaceReviewDividerRect.top;
            canvas.drawLine(i4, j4, mPlaceReviewDividerRect.right, mPlaceReviewDividerRect.bottom, sPlaceReviewDividerPaint);
        }
        if(mLocationLayout != null)
        {
            canvas.drawBitmap(mLocationIcon, null, mLocationIconRect, null);
            k3 = mLocationLayout.getLeft();
            l3 = mLocationLayout.getTop();
            canvas.translate(k3, l3);
            mLocationLayout.draw(canvas);
            canvas.translate(-k3, -l3);
        }
        if(mLinkedHeaderLayout != null || mLinkedBodyLayout != null)
        {
            canvas.drawRoundRect(mLinkedContentBorder, sLinkedBorderRadius, sLinkedBorderRadius, sLinkedBackgroundPaint);
            canvas.drawRoundRect(mLinkedContentBorder, sLinkedBorderRadius, sLinkedBorderRadius, sLinkedBorderPaint);
            if(mLinkedHeaderLayout != null)
            {
                canvas.drawBitmap(sLinkedIconBitmap, null, mLinkedIconRect, null);
                i3 = mLinkedHeaderLayout.getLeft();
                j3 = mLinkedHeaderLayout.getTop();
                canvas.translate(i3, j3);
                mLinkedHeaderLayout.draw(canvas);
                canvas.translate(-i3, -j3);
            }
            if(mLinkedBodyLayout != null)
            {
                k2 = mLinkedBodyLayout.getLeft();
                l2 = mLinkedBodyLayout.getTop();
                canvas.translate(k2, l2);
                mLinkedBodyLayout.draw(canvas);
                canvas.translate(-k2, -l2);
            }
        }
        if(mSkyjamHeader != null)
        {
            canvas.drawRoundRect(mSkyjamContentBorder, sLinkedBorderRadius, sLinkedBorderRadius, sLinkedBackgroundPaint);
            canvas.drawRoundRect(mSkyjamContentBorder, sLinkedBorderRadius, sLinkedBorderRadius, sLinkedBorderPaint);
            if(mSkyjamHeaderLayout != null)
            {
                canvas.drawBitmap(sSkyjamIconBitmap, null, mSkyjamIconRect, null);
                i2 = mSkyjamHeaderLayout.getLeft();
                j2 = mSkyjamHeaderLayout.getTop();
                canvas.translate(i2, j2);
                mSkyjamHeaderLayout.draw(canvas);
                canvas.translate(-i2, -j2);
            }
            if(mSkyjamSubheader1Layout != null)
            {
                k1 = mSkyjamSubheader1Layout.getLeft();
                l1 = mSkyjamSubheader1Layout.getTop();
                canvas.translate(k1, l1);
                mSkyjamSubheader1Layout.draw(canvas);
                canvas.translate(-k1, -l1);
            }
            if(mSkyjamSubheader2Layout != null)
            {
                i1 = mSkyjamSubheader2Layout.getLeft();
                j1 = mSkyjamSubheader2Layout.getTop();
                canvas.translate(i1, j1);
                mSkyjamSubheader2Layout.draw(canvas);
                canvas.translate(-i1, -j1);
            }
        }
        if(mPlaceReviewAspectsLayout != null)
        {
            k = mPlaceReviewAspectsLayout.getLeft();
            l = mPlaceReviewAspectsLayout.getTop();
            canvas.translate(k, l);
            mPlaceReviewAspectsLayout.draw(canvas);
            canvas.translate(-k, -l);
        }
        if(mPlaceReviewBodyLayout != null)
        {
            i = mPlaceReviewBodyLayout.getLeft();
            j = mPlaceReviewBodyLayout.getTop();
            canvas.translate(i, j);
            mPlaceReviewBodyLayout.draw(canvas);
            canvas.translate(-i, -j);
        }
        updateAccessibility();
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = getPaddingLeft() + sMarginLeft;
        int l = getPaddingTop() - sAvatarMarginTop;
        int i1 = getMeasuredWidth();
        int j1 = i1 - k - getPaddingRight() - sMarginRight;
        mBackgroundOffset = l;
        Context context = getContext();
        int k1 = k + sAvatarMarginLeft;
        int l1 = l + sAvatarMarginTop;
        mAuthorImage.setRect(k1, l1, k1 + sAvatarSize, l1 + sAvatarSize);
        boolean flag;
        int i2;
        Resources resources;
        int j2;
        Object aobj[];
        String s;
        int k2;
        int l2;
        TextPaint textpaint;
        android.graphics.drawable.NinePatchDrawable ninepatchdrawable;
        android.graphics.drawable.NinePatchDrawable ninepatchdrawable1;
        int i3;
        int j3;
        int k3;
        int l3;
        java.util.Locale locale;
        SpannableStringBuilder spannablestringbuilder;
        int l4;
        boolean flag1;
        if(mPlusOneData != null && mPlusOneData.isPlusOnedByMe())
            flag = true;
        else
            flag = false;
        if(mPlusOneData == null)
            i2 = 1;
        else
            i2 = mPlusOneData.getCount();
        resources = getResources();
        j2 = R.string.stream_plus_one_count_with_plus;
        aobj = new Object[1];
        aobj[0] = Integer.valueOf(Math.max(i2, 1));
        s = resources.getString(j2, aobj);
        k2 = (k + j1) - sPlusOneButtonMarginRight;
        l2 = l + sNameMarginTop;
        mClickableItems.remove(mPlusOneButton);
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
        mPlusOneButton = new ClickableButton(context, s, textpaint, ninepatchdrawable, ninepatchdrawable1, this, 0, 0);
        i3 = k2 - mPlusOneButton.getRect().width();
        mPlusOneButton.getRect().offsetTo(i3, l2);
        mClickableItems.add(mPlusOneButton);
        j3 = k + sAvatarMarginLeft + sAvatarSize + sAvatarMarginRight;
        k3 = l + sNameMarginTop;
        l3 = j1 - j3 - mPlusOneButton.getRect().width() - sPlusOneButtonMarginLeft;
        mAuthorLayout = new PositionedStaticLayout(TextUtils.ellipsize(mAuthorName, sNamePaint, l3, android.text.TextUtils.TruncateAt.END), sNamePaint, l3, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false);
        mAuthorLayout.setPosition(j3, k3);
        locale = getContext().getResources().getConfiguration().locale;
        spannablestringbuilder = new SpannableStringBuilder(mDate.toUpperCase(locale));
        if(mEdited)
        {
            spannablestringbuilder.append("   ");
            spannablestringbuilder.append(context.getString(R.string.stream_one_up_is_edited));
        }
        spannablestringbuilder.append("   ");
        spannablestringbuilder.append(mCreationSource);
        int k4;
        int k7;
        int l7;
        int j8;
        if(mDbEmbedSquare != null)
        {
            boolean flag2;
            String s1;
            Object obj;
            if(!TextUtils.isEmpty(mDbEmbedSquare.getAboutSquareName()))
                flag2 = true;
            else
                flag2 = false;
            if(flag2)
                s1 = mDbEmbedSquare.getAboutSquareName();
            else
                s1 = mDbEmbedSquare.getSquareName();
            if(flag2)
                obj = null;
            else
                obj = mDbEmbedSquare.getSquareStreamName();
            if(!TextUtils.isEmpty(s1))
            {
                spannablestringbuilder.append("   ");
                int i8 = spannablestringbuilder.length();
                if(!TextUtils.isEmpty(((CharSequence) (obj))))
                    spannablestringbuilder.append(context.getString(R.string.square_oneup_acl_name_and_stream, new Object[] {
                        s1, obj
                    }));
                else
                    spannablestringbuilder.append(s1);
                j8 = spannablestringbuilder.length();
                spannablestringbuilder.setSpan(new ClickableStaticLayout.StateURLSpan("square"), i8, j8, 33);
            }
        } else
        if(!TextUtils.isEmpty(mAclText))
        {
            spannablestringbuilder.append("   ");
            int i4 = spannablestringbuilder.length();
            spannablestringbuilder.append(mAclText);
            int j4 = spannablestringbuilder.length();
            spannablestringbuilder.setSpan(new ClickableStaticLayout.StateURLSpan(mAclText), i4, j4, 33);
        }
        if(!TextUtils.isEmpty(mMuteState))
        {
            spannablestringbuilder.append("   ");
            k7 = spannablestringbuilder.length();
            spannablestringbuilder.append(mMuteState);
            l7 = spannablestringbuilder.length();
            spannablestringbuilder.setSpan(new ForegroundColorSpan(sIsMuteColor), k7, l7, 33);
        }
        spannablestringbuilder.append(" ");
        k4 = k3 + mAuthorLayout.getHeight();
        mClickableItems.remove(mDateSourceAclLayout);
        mDateSourceAclLayout = new ClickableStaticLayout(spannablestringbuilder, sDatePaint, l3, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mAclClickListener);
        mDateSourceAclLayout.setPosition(j3, k4);
        mClickableItems.add(mDateSourceAclLayout);
        l4 = Math.max(sAvatarSize, mDateSourceAclLayout.getBottom()) + sTitleMarginBottom;
        if(!TextUtils.isEmpty(mAnnotation))
        {
            mClickableItems.remove(mAnnotationLayout);
            mAnnotationLayout = new ClickableStaticLayout(mAnnotation, sContentPaint, j1, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mOneUpListener);
            mAnnotationLayout.setPosition(k, l4);
            mClickableItems.add(mAnnotationLayout);
            l4 = mAnnotationLayout.getBottom();
        }
        if(mReshareHeader != null)
            flag1 = true;
        else
            flag1 = false;
        if(flag1)
        {
            if(!TextUtils.isEmpty(null) || !TextUtils.isEmpty(mReshareHeader))
            {
                int i5 = j1 - 2 * sReshareInnerMargin;
                int j5 = k + sReshareInnerMargin;
                int k5 = l4 + sReshareInnerMargin;
                int l5;
                int i6;
                int j6;
                int k6;
                int l6;
                if(!TextUtils.isEmpty(mReshareHeader))
                {
                    mClickableItems.remove(mReshareHeaderLayout);
                    mReshareHeaderLayout = new ClickableStaticLayout(mReshareHeader, sReshareHeaderPaint, i5, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mOneUpListener);
                    mReshareHeaderLayout.setPosition(j5, k5);
                    mClickableItems.add(mReshareHeaderLayout);
                    int i7 = mReshareHeaderLayout.getLeft() - sReshareInnerMargin;
                    int j7 = mReshareHeaderLayout.getTop() - sReshareInnerMargin;
                    j6 = mReshareHeaderLayout.getRight() + sReshareInnerMargin;
                    i6 = mReshareHeaderLayout.getBottom() + sReshareInnerMargin;
                    k6 = j7;
                    l6 = i7;
                    l5 = i6;
                } else
                {
                    mClickableItems.remove(mReshareHeaderLayout);
                    mReshareHeaderLayout = null;
                    l5 = k5;
                    i6 = 0;
                    j6 = 0;
                    k6 = 0;
                    l6 = 0;
                }
                if(!TextUtils.isEmpty(null))
                {
                    mClickableItems.remove(mReshareBodyLayout);
                    mReshareBodyLayout = new ClickableStaticLayout(null, sReshareBodyPaint, i5, android.text.Layout.Alignment.ALIGN_NORMAL, sFontSpacing, 0.0F, false, mOneUpListener);
                    mReshareBodyLayout.setPosition(j5, l5);
                    mClickableItems.add(mReshareBodyLayout);
                    if(l6 == 0)
                    {
                        l6 = mReshareBodyLayout.getLeft() - sReshareInnerMargin;
                        k6 = mReshareBodyLayout.getTop() - sReshareInnerMargin;
                    }
                    j6 = mReshareBodyLayout.getRight() + sReshareInnerMargin;
                    i6 = mReshareBodyLayout.getBottom() + sReshareInnerMargin;
                    l5 = i6;
                }
                if(!TextUtils.isEmpty(mTitle))
                {
                    i6 = measureAndLayoutTitle(j5, l5, i5);
                    l5 = i6;
                }
                if(mPlaceReview != null)
                {
                    i6 = measureAndLayoutPlaceReviewDivider(j5, l5, i5);
                    l5 = i6;
                }
                if(!TextUtils.isEmpty(mLocation))
                {
                    i6 = measureAndLayoutLocation(j5, l5, i5);
                    l5 = i6;
                }
                if(!TextUtils.isEmpty(mLinkedHeader) || !TextUtils.isEmpty(mLinkedBody))
                {
                    l5 = measureAndLayoutLinkedContent(j5, l5, i5);
                    i6 = l5 + sReshareInnerMargin;
                }
                if(!TextUtils.isEmpty(mSkyjamHeader))
                {
                    l5 = measureAndLayoutSkyjamContent(j5, l5, i5);
                    i6 = l5 + sReshareInnerMargin;
                }
                if(mPlaceReview != null)
                    i6 = measureAndLayoutPlaceReviewContent(j5, l5, i5) + sReshareInnerMargin;
                mReshareContentBorder = new RectF(l6, k6, j6, i6);
                l4 = i6;
            }
        } else
        {
            l4 = measureAndLayoutPlaceReviewContent(k, measureAndLayoutSkyjamContent(k, measureAndLayoutLinkedContent(k, measureAndLayoutLocation(k, measureAndLayoutPlaceReviewDivider(k, measureAndLayoutTitle(k, l4, j1), j1), j1), j1), j1), j1);
        }
        setMeasuredDimension(i1, l4 + sMarginBottom + getPaddingBottom());
        if(mOnMeasuredListener != null)
            mOnMeasuredListener.onMeasured(this);
        if(mTouchExplorer != null)
            mTouchExplorer.invalidateItemCache();
    }

    public void onRecycle()
    {
        clearLayoutState();
        mOneUpListener = null;
        mIsCheckin = false;
    }

    public final void onResourceStatusChange(Resource resource)
    {
    }

    public void setOneUpClickListener(OneUpListener oneuplistener)
    {
        mOneUpListener = oneuplistener;
    }

    public final void unbindResources()
    {
        if(mAuthorImage != null)
            mAuthorImage.unbindResources();
    }
    
    //===========================================================================================
    //
    //===========================================================================================
    private final class OneUpActivityTouchExplorer extends TouchExplorationHelper {
    	
    	private boolean mIsItemCacheStale;
        private ArrayList mItemCache;

        public OneUpActivityTouchExplorer(Context context)
        {
            super(context);
            mIsItemCacheStale = true;
            mItemCache = new ArrayList(mClickableItems.size());
        }

        private void refreshItemCache()
        {
            if(mIsItemCacheStale)
            {
                mItemCache.clear();
                mItemCache.addAll(mClickableItems);
                Collections.sort(mItemCache, ClickableItem.sComparator);
                mIsItemCacheStale = false;
            }
        }

        protected final int getIdForItem(Object obj)
        {
            ClickableItem clickableitem = (ClickableItem)obj;
            refreshItemCache();
            return mItemCache.indexOf(clickableitem);
        }

        protected final Object getItemAt(float f, float f1) {
            int j;
            refreshItemCache();
            int size = mItemCache.size();
            ClickableItem clickableitem;
            for(j = 0; j < size; j++) {
            	clickableitem = (ClickableItem)mItemCache.get(j);
            	if(clickableitem.getRect().contains((int)f, (int)f1)) 
            		return clickableitem;
            }
            return null;
        }

        protected final Object getItemForId(int i)
        {
            ClickableItem clickableitem;
            if(i >= 0 && i < mItemCache.size())
            {
                refreshDrawableState();
                clickableitem = (ClickableItem)mItemCache.get(i);
            } else
            {
                clickableitem = null;
            }
            return clickableitem;
        }

        protected final void getVisibleItems(List list)
        {
            refreshItemCache();
            int i = 0;
            for(int j = mItemCache.size(); i < j; i++)
                list.add((ClickableItem)mItemCache.get(i));

        }

        public final void invalidateItemCache()
        {
            mIsItemCacheStale = true;
        }

        protected final boolean performActionForItem(Object obj, int i, Bundle bundle)
        {
            boolean flag = true;
            ClickableItem clickableitem = (ClickableItem)obj;
            if(i == 16)
            {
                clickableitem.handleEvent(clickableitem.getRect().centerX(), clickableitem.getRect().centerY(), 0);
                clickableitem.handleEvent(clickableitem.getRect().centerX(), clickableitem.getRect().centerY(), 1);
            } else
            {
                flag = false;
            }
            return flag;
        }

        protected final void populateEventForItem(Object obj, AccessibilityEvent accessibilityevent)
        {
            accessibilityevent.setContentDescription(((ClickableItem)obj).getContentDescription());
        }

        protected final void populateNodeForItem(Object obj, AccessibilityNodeInfoCompat accessibilitynodeinfocompat)
        {
            ClickableItem clickableitem = (ClickableItem)obj;
            accessibilitynodeinfocompat.setBoundsInParent(clickableitem.getRect());
            accessibilitynodeinfocompat.addAction(16);
            accessibilitynodeinfocompat.setText(clickableitem.getContentDescription());
        }

    }
    
    private final class AclClickListener implements ClickableStaticLayout.SpanClickListener {
    	
    	public final void onSpanClick(URLSpan urlspan)
        {
            if(mOneUpListener != null)
                if("square".equals(urlspan.getURL()) && mDbEmbedSquare != null)
                {
                    boolean flag;
                    String s;
                    String s1;
                    if(!TextUtils.isEmpty(mDbEmbedSquare.getAboutSquareId()))
                        flag = true;
                    else
                        flag = false;
                    if(flag)
                        s = mDbEmbedSquare.getAboutSquareId();
                    else
                        s = mDbEmbedSquare.getSquareId();
                    if(flag)
                        s1 = null;
                    else
                        s1 = mDbEmbedSquare.getSquareStreamId();
                    mOneUpListener.onSquareClick(s, s1);
                } else
                {
                    mOneUpListener.onSpanClick(new URLSpan((new StringBuilder("acl:")).append(mActivityId).toString()));
                }
        }
    }
    
    private final class SourceClickListener implements ClickableStaticLayout.SpanClickListener {
    	public final void onSpanClick(URLSpan urlspan)
        {
            if(mOneUpListener != null)
                mOneUpListener.onSourceAppContentClick(mCreationSource, mSourceAppPackages, mSourceAppData, urlspan.getURL(), mAuthorId);
        }
    }
    
    private final class LocationClickListener implements ClickableStaticLayout.SpanClickListener {
    	public final void onSpanClick(URLSpan urlspan)
        {
            if(mOneUpListener == null) 
            	return; 
           
            if(mLocationData == null) {
            	if(mPlaceReview != null && mPlaceReview.itemReviewed != null && mPlaceReview.itemReviewed.place != null && !TextUtils.isEmpty(mPlaceReview.itemReviewed.place.ownerObfuscatedId))
                    mOneUpListener.onPlaceClick(mPlaceReview.itemReviewed.place.ownerObfuscatedId);
            } else { 
            	OneUpListener oneuplistener = mOneUpListener;
                //mActivityId;
                oneuplistener.onLocationClick(mLocationData);
            }
        }
    }
    
    private final class SkyjamClickListener implements ClickableStaticLayout.SpanClickListener {
    	public final void onSpanClick(URLSpan urlspan)
        {
            if(mOneUpListener == null) 
            	return;
            
            String s = urlspan.getURL();
            if(!s.startsWith("skyjam:buy:")) {
            	if(s.startsWith("skyjam:listen:"))
                {
                    String s1 = s.substring(14);
                    mOneUpListener.onSkyjamListenClick(s1);
                } 
            } else { 
            	String s2 = s.substring(11);
                mOneUpListener.onSkyjamBuyClick(s2);
            }
        }
    }
}
