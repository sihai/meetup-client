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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.DbEmbedSquare;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.util.LinksRenderUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;

/**
 * 
 * @author sihai
 *
 */
public class SquareCardView extends StreamCardView {

	private static ImageResourceManager sImageResourceManager;
    protected static TextPaint sInvitationTextPaint;
    private static boolean sSquareInviteCardViewInitialized;
    protected static TextPaint sSquareNameTextPaint;
    protected DbEmbedSquare mDbEmbedSquare;
    protected Rect mDestRect;
    protected Resource mImageResource;
    protected MediaRef mMediaRef;
    protected StaticLayout mSquareInvitationLayout;
    protected StaticLayout mSquareNameLayout;
    protected Rect mSrcRect;
    
    public SquareCardView(Context context)
    {
        this(context, null);
    }

    public SquareCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        if(!sSquareInviteCardViewInitialized)
        {
            sSquareInviteCardViewInitialized = true;
            sImageResourceManager = ImageResourceManager.getInstance(context);
            Resources resources = context.getResources();
            TextPaint textpaint = new TextPaint();
            sInvitationTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sInvitationTextPaint.setColor(resources.getColor(R.color.card_square_invite_invitation_text));
            sInvitationTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sInvitationTextPaint.setTextSize(resources.getDimension(R.dimen.card_square_invite_invitation_text_size));
            sInvitationTextPaint.setShadowLayer(resources.getDimension(R.dimen.card_square_invite_shadow_radius), resources.getDimension(R.dimen.card_square_invite_shadow_x), resources.getDimension(R.dimen.card_square_invite_shadow_y), resources.getColor(R.color.card_square_invite_shadow_text));
            TextPaintUtils.registerTextPaint(sInvitationTextPaint, R.dimen.card_square_invite_invitation_text_size);
            TextPaint textpaint1 = new TextPaint();
            sSquareNameTextPaint = textpaint1;
            textpaint1.setAntiAlias(true);
            sSquareNameTextPaint.setColor(resources.getColor(R.color.card_square_invite_name_text));
            sSquareNameTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sSquareNameTextPaint.setTextSize(resources.getDimension(R.dimen.card_square_invite_name_text_size));
            sSquareNameTextPaint.setShadowLayer(resources.getDimension(R.dimen.card_square_invite_shadow_radius), resources.getDimension(R.dimen.card_square_invite_shadow_x), resources.getDimension(R.dimen.card_square_invite_shadow_y), resources.getColor(R.color.card_square_invite_shadow_text));
            TextPaintUtils.registerTextPaint(sSquareNameTextPaint, R.dimen.card_square_invite_name_text_size);
        }
        mSrcRect = new Rect();
        mDestRect = new Rect();
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        Bitmap bitmap;
        boolean flag;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
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
        canvas.drawRect(mDestRect, LinksRenderUtils.getTransparentOverlayPaint());
        i1 = k + sXDoublePadding;
        j1 = (int)((float)(l + sYDoublePadding) * getMediaHeightPercentage());
        if(mPlusOneButton == null)
            k1 = j1;
        else
            k1 = j1 - mPlusOneButton.getRect().height();
        l1 = sSquareBitmap.getHeight() + sContentYPadding;
        if(mSquareInvitationLayout != null)
            l1 += mSquareInvitationLayout.getHeight() + sContentYPadding;
        if(mSquareNameLayout != null)
            l1 += mSquareNameLayout.getHeight() + sContentYPadding;
        i2 = (k1 - l1) / 2;
        canvas.drawBitmap(sSquareBitmap, (i1 - sSquareBitmap.getWidth()) / 2, i2, null);
        j2 = i2 + (sSquareBitmap.getHeight() + sContentYPadding);
        if(mSquareInvitationLayout != null)
        {
            canvas.translate(i, j2);
            mSquareInvitationLayout.draw(canvas);
            canvas.translate(-i, -j2);
            j2 += mSquareInvitationLayout.getHeight() + sContentYPadding;
        }
        if(mSquareNameLayout != null)
        {
            canvas.translate(i, j2);
            mSquareNameLayout.draw(canvas);
            canvas.translate(-i, -j2);
            mSquareNameLayout.getHeight();
            int _tmp = sContentYPadding;
        }
        drawMediaTopAreaShadow(canvas, k, l);
        drawPlusOneBar(canvas);
        drawMediaBottomArea(canvas, i, k, l);
        drawCornerIcon(canvas);
        return l;
    }

    public final String getSquareId()
    {
        return mDbEmbedSquare.getAboutSquareId();
    }

    public final void init(Cursor cursor, int i, int j, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamCardView.ViewedListener viewedlistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener)
    {
        super.init(cursor, i, j, onclicklistener, itemclicklistener, viewedlistener, streamplusbarclicklistener, streammediaclicklistener);
        byte abyte0[] = cursor.getBlob(27);
        if(abyte0 != null)
        {
            mDbEmbedSquare = DbEmbedSquare.deserialize(abyte0);
            String s = mDbEmbedSquare.getImageUrl();
            if(!TextUtils.isEmpty(s))
                mMediaRef = new MediaRef(s, MediaRef.MediaType.IMAGE);
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
        if(mPlusOneButton == null)
            k1 = j1;
        else
            k1 = j1 - mPlusOneButton.getRect().height();
        l1 = k1 - sSquareBitmap.getHeight() - 3 * sContentYPadding;
        if(!TextUtils.isEmpty(mDbEmbedSquare.getAboutSquareName()))
        {
            int j2 = (l1 - j) / (int)(sSquareNameTextPaint.descent() - sSquareNameTextPaint.ascent());
            if(j2 > 0)
            {
                mSquareNameLayout = TextPaintUtils.createConstrainedStaticLayout(sSquareNameTextPaint, mDbEmbedSquare.getAboutSquareName(), k, j2, android.text.Layout.Alignment.ALIGN_CENTER);
                j += mSquareNameLayout.getHeight() + sContentYPadding;
            }
        }
        if(mDbEmbedSquare.isInvitation())
        {
            int i2 = (l1 - j) / (int)(sInvitationTextPaint.descent() - sInvitationTextPaint.ascent());
            if(i2 > 0)
            {
                mSquareInvitationLayout = TextPaintUtils.createConstrainedStaticLayout(sInvitationTextPaint, getContext().getString(R.string.card_square_invite_invitation), k, i2, android.text.Layout.Alignment.ALIGN_CENTER);
                mSquareInvitationLayout.getHeight();
                int _tmp = sContentYPadding;
            }
        }
        mSrcRect.setEmpty();
        mDestRect.set(sLeftBorderPadding, sTopBorderPadding, i1 + sLeftBorderPadding, j1 + sTopBorderPadding);
        return l;
    }

    protected final void onBindResources()
    {
        super.onBindResources();
        if(mMediaRef != null)
            mImageResource = sImageResourceManager.getMedia(mMediaRef, 3, this);
    }

    public void onRecycle()
    {
        super.onRecycle();
        mDbEmbedSquare = null;
        mMediaRef = null;
        mSrcRect.setEmpty();
        mDestRect.setEmpty();
        mSquareInvitationLayout = null;
        mSquareNameLayout = null;
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
