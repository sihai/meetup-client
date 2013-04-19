/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.util.ArrayList;
import java.util.Iterator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;

import com.galaxy.meetup.client.android.R;


/**
 * 
 * @author sihai
 *
 */
public class MultiWaveView extends View {

	private int mActiveTarget;
    private boolean mAnimatingTargets;
    private TimeInterpolator mChevronAnimationInterpolator;
    private ArrayList mChevronAnimations;
    private ArrayList mChevronDrawables;
    private ArrayList mDirectionDescriptions;
    private int mDirectionDescriptionsResourceId;
    private boolean mDragging;
    private int mFeedbackCount;
    private int mGrabbedState;
    private Tweener mHandleAnimation;
    private TargetDrawable mHandleDrawable;
    private float mHitRadius;
    private float mHorizontalOffset;
    private int mNewTargetResources;
    private OnTriggerListener mOnTriggerListener;
    private float mOuterRadius;
    private TargetDrawable mOuterRing;
    private GradientDrawable mOuterRingDrawable;
    private android.animation.Animator.AnimatorListener mResetListener;
    private android.animation.Animator.AnimatorListener mResetListenerWithPing;
    private int mScreenHeight;
    private int mScreenWidth;
    private float mSnapMargin;
    private float mTapRadius;
    private ArrayList mTargetAnimations;
    private ArrayList mTargetDescriptions;
    private int mTargetDescriptionsResourceId;
    private ArrayList mTargetDrawables;
    private int mTargetResourceId;
    private android.animation.Animator.AnimatorListener mTargetUpdateListener;
    private android.animation.ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private float mVerticalOffset;
    private int mVibrationDuration;
    private Vibrator mVibrator;
    private float mWaveCenterX;
    private float mWaveCenterY;

    public MultiWaveView(Context context)
    {
        this(context, null);
    }

    public MultiWaveView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mTargetDrawables = new ArrayList();
        mChevronDrawables = new ArrayList();
        mFeedbackCount = 3;
        mVibrationDuration = 0;
        mActiveTarget = -1;
        mOuterRadius = 0.0F;
        mHitRadius = 0.0F;
        mSnapMargin = 0.0F;
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            mChevronAnimationInterpolator = Ease.Quad.easeOut;
            mChevronAnimations = new ArrayList();
            mTargetAnimations = new ArrayList();
            mResetListener = new AnimatorListenerAdapter() {

                public final void onAnimationEnd(Animator animator)
                {
                    switchToState(0/*, mWaveCenterX, mWaveCenterY*/);
                }
            };
            mResetListenerWithPing = new AnimatorListenerAdapter() {

                public final void onAnimationEnd(Animator animator)
                {
                    ping();
                    switchToState(0/*, mWaveCenterX, mWaveCenterY*/);
                }
            };
            mUpdateListener = new android.animation.ValueAnimator.AnimatorUpdateListener() {

                public final void onAnimationUpdate(ValueAnimator valueanimator)
                {
                    invalidateGlobalRegion(mHandleDrawable);
                    invalidate();
                }
            };
            mTargetUpdateListener = new AnimatorListenerAdapter() {

                public final void onAnimationEnd(Animator animator)
                {
                    if(mNewTargetResources != 0)
                    {
                        internalSetTargetResources(mNewTargetResources);
                        mNewTargetResources = 0;
                        hideTargets(false);
                    }
                    mAnimatingTargets = false;
                }
            };
        }
        context.getResources();
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.MultiWaveView);
        mHorizontalOffset = typedarray.getDimension(14, mHorizontalOffset);
        mVerticalOffset = typedarray.getDimension(13, mVerticalOffset);
        mHitRadius = typedarray.getDimension(9, mHitRadius);
        mSnapMargin = typedarray.getDimension(11, mSnapMargin);
        mVibrationDuration = typedarray.getInt(10, mVibrationDuration);
        mFeedbackCount = typedarray.getInt(12, mFeedbackCount);
        mHandleDrawable = new TargetDrawable(typedarray.getDrawable(3));
        mTapRadius = mHandleDrawable.getWidth() / 2;
        WindowManager windowmanager = (WindowManager)context.getSystemService("window");
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowmanager.getDefaultDisplay().getMetrics(displaymetrics);
        mScreenWidth = displaymetrics.widthPixels;
        mScreenHeight = displaymetrics.heightPixels;
        float f = (0.9F * (float)Math.min(mScreenWidth, mScreenHeight)) / 2.0F;
        int i;
        int j;
        int ai[];
        int k;
        if(mTargetDrawables.size() > 0)
            i = ((TargetDrawable)mTargetDrawables.get(0)).getWidth() / 2;
        else
            i = 0;
        mOuterRadius = f - (float)i;
        j = (int)(2.0F * mOuterRadius);
        mOuterRingDrawable = (GradientDrawable)typedarray.getDrawable(8);
        ((GradientDrawable)mOuterRingDrawable.mutate()).setSize(j, j);
        mOuterRing = new TargetDrawable(mOuterRingDrawable);
        ai = (new int[] {
            4, 5, 6, 7
        });
        k = ai.length;
        for(int l = 0; l < k; l++)
        {
            Drawable drawable = typedarray.getDrawable(ai[l]);
            int k1 = 0;
            while(k1 < mFeedbackCount) 
            {
                ArrayList arraylist = mChevronDrawables;
                TargetDrawable targetdrawable;
                if(drawable != null)
                    targetdrawable = new TargetDrawable(drawable);
                else
                    targetdrawable = null;
                arraylist.add(targetdrawable);
                k1++;
            }
        }

        TypedValue typedvalue = new TypedValue();
        if(typedarray.getValue(0, typedvalue))
            internalSetTargetResources(typedvalue.resourceId);
        if(mTargetDrawables == null || mTargetDrawables.size() == 0)
            throw new IllegalStateException("Must specify at least one target drawable");
        if(typedarray.getValue(1, typedvalue))
        {
            int j1 = typedvalue.resourceId;
            if(j1 == 0)
                throw new IllegalStateException("Must specify target descriptions");
            setTargetDescriptionsResourceId(j1);
        }
        if(typedarray.getValue(2, typedvalue))
        {
            int i1 = typedvalue.resourceId;
            if(i1 == 0)
                throw new IllegalStateException("Must specify direction descriptions");
            setDirectionDescriptionsResourceId(i1);
        }
        typedarray.recycle();
        boolean flag;
        if(mVibrationDuration > 0)
            flag = true;
        else
            flag = false;
        setVibrateEnabled(flag);
    }

    private void announceText(String s)
    {
        setContentDescription(s);
        sendAccessibilityEvent(8);
        setContentDescription(null);
    }

    private void deactivateTargets()
    {
        for(Iterator iterator = mTargetDrawables.iterator(); iterator.hasNext(); ((TargetDrawable)iterator.next()).setState(TargetDrawable.STATE_INACTIVE));
        mActiveTarget = -1;
    }

    private String getTargetDescription(int i)
    {
    	String s;
        if(mTargetDescriptions != null && !mTargetDescriptions.isEmpty()) 
        	s = (String)mTargetDescriptions.get(i);
        else {
        	mTargetDescriptions = loadDescriptions(mTargetDescriptionsResourceId);
            if(mTargetDrawables.size() == mTargetDescriptions.size()) 
            	s = (String)mTargetDescriptions.get(i); 
            else {
            	android.util.Log.w("MultiWaveView", "The number of target drawables must be equal to the number of target descriptions.");
                s = null;
            }
        }
        return s;
    }

    private void handleMove(MotionEvent motionevent)
    {
        if(!mDragging)
        {
            trySwitchToFirstTouchState(motionevent);
        } else
        {
            int i = -1;
            int j = motionevent.getHistorySize();
            int k = 0;
            while(k < j + 1) 
            {
                float f;
                float f1;
                float f2;
                float f3;
                float f4;
                float f5;
                float f6;
                float f7;
                boolean flag;
                if(k < j)
                    f = motionevent.getHistoricalX(k);
                else
                    f = motionevent.getX();
                if(k < j)
                    f1 = motionevent.getHistoricalY(k);
                else
                    f1 = motionevent.getY();
                f2 = f - mWaveCenterX;
                f3 = f1 - mWaveCenterY;
                f4 = (float)Math.sqrt(f2 * f2 + f3 * f3);
                if(f4 > mOuterRadius)
                    f5 = mOuterRadius / f4;
                else
                    f5 = 1.0F;
                f6 = mWaveCenterX + f2 * f5;
                f7 = mWaveCenterY + f3 * f5;
                if(mTargetDrawables.size() == 1)
                    flag = true;
                else
                    flag = false;
                if(flag)
                {
                    if(f4 > mOuterRadius - mSnapMargin)
                    {
                        i = 0;
                        f = f6;
                        f1 = f7;
                    }
                } else
                {
                    float f8 = 3.402823E+038F;
                    float f9 = mHitRadius * mHitRadius;
                    for(int l = 0; l < mTargetDrawables.size(); l++)
                    {
                        TargetDrawable targetdrawable1 = (TargetDrawable)mTargetDrawables.get(l);
                        float f12 = f6 - targetdrawable1.getX();
                        float f13 = f7 - targetdrawable1.getY();
                        float f14 = f12 * f12 + f13 * f13;
                        if(targetdrawable1.isValid() && f14 < f9 && f14 < f8)
                        {
                            i = l;
                            f8 = f14;
                        }
                    }

                    f = f6;
                    f1 = f7;
                }
                if(i != -1)
                {
                    switchToState(3);
                    float f10;
                    float f11;
                    TargetDrawable targetdrawable;
                    if(flag)
                        f10 = f6;
                    else
                        f10 = ((TargetDrawable)mTargetDrawables.get(i)).getX();
                    if(flag)
                        f11 = f7;
                    else
                        f11 = ((TargetDrawable)mTargetDrawables.get(i)).getY();
                    moveHandleTo(f10, f11);
                    targetdrawable = (TargetDrawable)mTargetDrawables.get(i);
                    int[] _tmp = TargetDrawable.STATE_FOCUSED;
                    targetdrawable.hasState();
                } else
                {
                    switchToState(2);
                    moveHandleTo(f, f1);
                    mHandleDrawable.setAlpha(1.0F);
                }
                k++;
            }
            invalidateGlobalRegion(mHandleDrawable);
            if(mActiveTarget != i && i != -1)
            {
                vibrate();
                if(mOnTriggerListener != null)
                {
                    OnTriggerListener _tmp1 = mOnTriggerListener;
                }
                if(((AccessibilityManager)getContext().getSystemService("accessibility")).isEnabled())
                    announceText(getTargetDescription(i));
            }
            mActiveTarget = i;
        }
    }

    private void hideChevrons()
    {
        Iterator iterator = mChevronDrawables.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            TargetDrawable targetdrawable = (TargetDrawable)iterator.next();
            if(targetdrawable != null)
                targetdrawable.setAlpha(0.0F);
        } while(true);
    }

    private void hideTargets(boolean flag)
    {
        if(mTargetAnimations != null && mTargetAnimations.size() > 0)
            stopTargetAnimation();
        mAnimatingTargets = flag;
        if(flag && android.os.Build.VERSION.SDK_INT >= 11)
        {
            int i;
            Iterator iterator1;
            TargetDrawable targetdrawable2;
            ArrayList arraylist1;
            long l1;
            Object aobj1[];
            if(flag)
                i = 1200;
            else
                i = 0;
            for(iterator1 = mTargetDrawables.iterator(); iterator1.hasNext(); arraylist1.add(Tweener.to(targetdrawable2, l1, aobj1)))
            {
                targetdrawable2 = (TargetDrawable)iterator1.next();
                targetdrawable2.setState(TargetDrawable.STATE_INACTIVE);
                arraylist1 = mTargetAnimations;
                l1 = i;
                aobj1 = new Object[6];
                aobj1[0] = "alpha";
                aobj1[1] = Float.valueOf(0.0F);
                aobj1[2] = "delay";
                aobj1[3] = Integer.valueOf(200);
                aobj1[4] = "onUpdate";
                aobj1[5] = mUpdateListener;
            }

            ArrayList arraylist = mTargetAnimations;
            TargetDrawable targetdrawable1 = mOuterRing;
            long l = i;
            Object aobj[] = new Object[8];
            aobj[0] = "alpha";
            aobj[1] = Float.valueOf(0.0F);
            aobj[2] = "delay";
            aobj[3] = Integer.valueOf(200);
            aobj[4] = "onUpdate";
            aobj[5] = mUpdateListener;
            aobj[6] = "onComplete";
            aobj[7] = mTargetUpdateListener;
            arraylist.add(Tweener.to(targetdrawable1, l, aobj));
        } else
        {
            TargetDrawable targetdrawable;
            for(Iterator iterator = mTargetDrawables.iterator(); iterator.hasNext(); targetdrawable.setAlpha(0.0F))
            {
                targetdrawable = (TargetDrawable)iterator.next();
                targetdrawable.setState(TargetDrawable.STATE_INACTIVE);
            }

            mOuterRing.setAlpha(0.0F);
        }
    }

    private void hideUnselected(int i)
    {
        for(int j = 0; j < mTargetDrawables.size(); j++)
            if(j != i)
                ((TargetDrawable)mTargetDrawables.get(j)).setAlpha(0.0F);

        mOuterRing.setAlpha(0.0F);
    }

    private void internalSetTargetResources(int i)
    {
        TypedArray typedarray = getContext().getResources().obtainTypedArray(i);
        int j = typedarray.length();
        int k = 0;
        ArrayList arraylist = new ArrayList(j);
        for(int l = 0; l < j; l++)
        {
            Drawable drawable = typedarray.getDrawable(l);
            arraylist.add(new TargetDrawable(drawable));
            if(drawable != null && drawable.getIntrinsicWidth() > k)
                k = drawable.getIntrinsicWidth();
        }

        mOuterRadius = (0.9F * (float)Math.min(getWidth(), getHeight())) / 2.0F;
        int i1 = 2 * (int)mOuterRadius;
        ((GradientDrawable)mOuterRingDrawable.mutate()).setSize(i1, i1);
        mOuterRing = new TargetDrawable(mOuterRingDrawable);
        typedarray.recycle();
        mTargetResourceId = i;
        mTargetDrawables = arraylist;
        updateTargetPositions();
    }

    private ArrayList loadDescriptions(int i)
    {
        TypedArray typedarray = getContext().getResources().obtainTypedArray(i);
        int j = typedarray.length();
        ArrayList arraylist = new ArrayList(j);
        for(int k = 0; k < j; k++)
            arraylist.add(typedarray.getString(k));

        typedarray.recycle();
        return arraylist;
    }

    private void moveHandleTo(float f, float f1)
    {
        mHandleDrawable.setX(f);
        mHandleDrawable.setY(f1);
    }
    
    private static int resolveMeasured(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = k;
        int mode = android.view.View.MeasureSpec.getMode(i);
        if(-2147483648 == mode) {
        	l = Math.min(k, j);
        } else if(0 == mode) {
        	l = j;
        }
        return l;
    }

    private void setGrabbedState(int i)
    {
        if(i != mGrabbedState)
        {
            if(i != 0)
                vibrate();
            mGrabbedState = i;
            if(mOnTriggerListener != null)
            {
                OnTriggerListener _tmp = mOnTriggerListener;
                int _tmp1 = mGrabbedState;
            }
        }
    }

    private void stopChevronAnimation()
    {
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            for(Iterator iterator = mChevronAnimations.iterator(); iterator.hasNext(); ((Tweener)iterator.next()).animator.end());
            mChevronAnimations.clear();
        }
    }

    private void stopHandleAnimation()
    {
        if(android.os.Build.VERSION.SDK_INT >= 11 && mHandleAnimation != null)
        {
            mHandleAnimation.animator.end();
            mHandleAnimation = null;
        }
    }

    private void stopTargetAnimation()
    {
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            for(Iterator iterator = mTargetAnimations.iterator(); iterator.hasNext(); ((Tweener)iterator.next()).animator.end());
            mTargetAnimations.clear();
        }
    }

    private void switchToState(int i)
    {
    	// TODO
    	switch(i) {
    	case 0:
    		break;
    	case 1:
    		break;
    	case 2:
    		break;
    	case 3:
    		break;
    	case 4:
    		break;
    	default:
    		break;
    	}
    	
    }

    private boolean trySwitchToFirstTouchState(MotionEvent motionevent)
    {
        float f = motionevent.getX();
        float f1 = motionevent.getY();
        float f2 = f - mWaveCenterX;
        float f3 = f1 - mWaveCenterY;
        float f4 = f2 * f2 + f3 * f3;
        float f5;
        boolean flag;
        if(((AccessibilityManager)getContext().getSystemService("accessibility")).isEnabled())
            f5 = 1.3F * mTapRadius;
        else
            f5 = mTapRadius;
        if(f4 <= f5 * f5)
        {
            android.util.Log.v("MultiWaveView", "** Handle HIT");
            switchToState(1);
            moveHandleTo(f, f1);
            mDragging = true;
            flag = true;
        } else
        {
            flag = false;
        }
        return flag;
    }

    private void updateTargetPositions()
    {
        for(int i = 0; i < mTargetDrawables.size(); i++)
        {
            TargetDrawable targetdrawable = (TargetDrawable)mTargetDrawables.get(i);
            double d = (-6.2831853071795862D * (double)i) / (double)mTargetDrawables.size();
            float f = mWaveCenterX + mOuterRadius * (float)Math.cos(d);
            float f1 = mWaveCenterY + mOuterRadius * (float)Math.sin(d);
            targetdrawable.setX(f);
            targetdrawable.setY(f1);
        }

    }

    private void vibrate()
    {
        if(mVibrator != null)
            mVibrator.vibrate(mVibrationDuration);
    }

    protected int getSuggestedMinimumHeight()
    {
        int i = mOuterRing.getHeight();
        int j = mTargetDrawables.size();
        int k = 0;
        if(j > 0)
            k = ((TargetDrawable)mTargetDrawables.get(0)).getHeight() / 2;
        return k + i;
    }

    protected int getSuggestedMinimumWidth()
    {
        int i = mOuterRing.getWidth();
        int j = mTargetDrawables.size();
        int k = 0;
        if(j > 0)
            k = ((TargetDrawable)mTargetDrawables.get(0)).getWidth() / 2;
        return k + i;
    }

    final void invalidateGlobalRegion(TargetDrawable targetdrawable)
    {
        int i = targetdrawable.getWidth();
        int j = targetdrawable.getHeight();
        RectF rectf = new RectF(0.0F, 0.0F, i, j);
        rectf.offset(targetdrawable.getX() - (float)(i / 2), targetdrawable.getY() - (float)(j / 2));
        for(Object obj = this; ((View) (obj)).getParent() != null && (((View) (obj)).getParent() instanceof View); ((View) (obj)).invalidate((int)Math.floor(rectf.left), (int)Math.floor(rectf.top), (int)Math.ceil(rectf.right), (int)Math.ceil(rectf.bottom)))
        {
            obj = (View)((View) (obj)).getParent();
            if(android.os.Build.VERSION.SDK_INT >= 11)
                ((View) (obj)).getMatrix().mapRect(rectf);
        }

    }

    protected void onDraw(Canvas canvas)
    {
        mOuterRing.draw(canvas);
        Iterator iterator = mTargetDrawables.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            TargetDrawable targetdrawable1 = (TargetDrawable)iterator.next();
            if(targetdrawable1 != null)
                targetdrawable1.draw(canvas);
        } while(true);
        Iterator iterator1 = mChevronDrawables.iterator();
        do
        {
            if(!iterator1.hasNext())
                break;
            TargetDrawable targetdrawable = (TargetDrawable)iterator1.next();
            if(targetdrawable != null)
                targetdrawable.draw(canvas);
        } while(true);
        mHandleDrawable.draw(canvas);
    }

    public boolean onHoverEvent(MotionEvent motionevent)
    {
        if(!((AccessibilityManager)getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
        	return super.onHoverEvent(motionevent); 
        } else { 
        	int i = motionevent.getAction();
        	switch(i) {
	        	case  7:
	        		motionevent.setAction(2);
	        		break;
	        	case 9:
	        		motionevent.setAction(0);
	        		break;
	        	case 10:
	        		motionevent.setAction(1);
	        		break;
	        	case 8:
        		default:
        			 onTouchEvent(motionevent);
        		        motionevent.setAction(i);
        			break;
        	}
        	return true;
        }
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        int i1 = k - i;
        int j1 = l - j;
        float f = mHorizontalOffset + (float)(Math.max(i1, mOuterRing.getWidth()) / 2);
        float f1 = mVerticalOffset + (float)(Math.max(j1, mOuterRing.getHeight()) / 2);
        if(f != mWaveCenterX || f1 != mWaveCenterY)
        {
            if(mWaveCenterX == 0.0F && mWaveCenterY == 0.0F)
            {
                if(mOuterRadius == 0.0F)
                    mOuterRadius = 0.5F * (float)Math.sqrt(f * f + f1 * f1);
                if(mHitRadius == 0.0F)
                    mHitRadius = (float)((TargetDrawable)mTargetDrawables.get(0)).getWidth() / 2.0F;
                if(mSnapMargin == 0.0F)
                    mSnapMargin = TypedValue.applyDimension(1, 20F, getContext().getResources().getDisplayMetrics());
                hideChevrons();
                hideTargets(false);
                moveHandleTo(f, f1);
            }
            mWaveCenterX = f;
            mWaveCenterY = f1;
            mOuterRing.setX(mWaveCenterX);
            mOuterRing.setY(mWaveCenterY);
            updateTargetPositions();
        }
        android.util.Log.v("MultiWaveView", (new StringBuilder("Outer Radius = ")).append(mOuterRadius).toString());
        android.util.Log.v("MultiWaveView", (new StringBuilder("HitRadius = ")).append(mHitRadius).toString());
        android.util.Log.v("MultiWaveView", (new StringBuilder("SnapMargin = ")).append(mSnapMargin).toString());
        android.util.Log.v("MultiWaveView", (new StringBuilder("FeedbackCount = ")).append(mFeedbackCount).toString());
        android.util.Log.v("MultiWaveView", (new StringBuilder("VibrationDuration = ")).append(mVibrationDuration).toString());
        android.util.Log.v("MultiWaveView", (new StringBuilder("TapRadius = ")).append(mTapRadius).toString());
        android.util.Log.v("MultiWaveView", (new StringBuilder("WaveCenterX = ")).append(mWaveCenterX).toString());
        android.util.Log.v("MultiWaveView", (new StringBuilder("WaveCenterY = ")).append(mWaveCenterY).toString());
        android.util.Log.v("MultiWaveView", (new StringBuilder("HorizontalOffset = ")).append(mHorizontalOffset).toString());
        android.util.Log.v("MultiWaveView", (new StringBuilder("VerticalOffset = ")).append(mVerticalOffset).toString());
    }

    protected void onMeasure(int i, int j)
    {
        int k = getSuggestedMinimumWidth();
        int l = getSuggestedMinimumHeight();
        int i1 = resolveMeasured(i, k);
        int j1 = resolveMeasured(j, l);
        mOuterRadius = (0.9F * (float)Math.min(i1 - getPaddingLeft() - getPaddingRight(), j1 - getPaddingTop() - getPaddingBottom())) / 2.0F;
        int k1 = (int)(2.0F * mOuterRadius);
        ((GradientDrawable)mOuterRingDrawable.mutate()).setSize(k1, k1);
        getResources();
        mOuterRing = new TargetDrawable(mOuterRingDrawable);
        mOuterRing.setX(getWidth() / 2);
        mOuterRing.setY(getHeight() / 2);
        setMeasuredDimension(i1, j1);
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        int i = motionevent.getAction();
        boolean flag = false;
        switch(i) {
        case 0:
        	if(!trySwitchToFirstTouchState(motionevent))
            {
                mDragging = false;
                stopTargetAnimation();
                ping();
            }
            flag = true;
        	break;
        case 1:
        	handleMove(motionevent);
            if(mDragging)
            	android.util.Log.v("MultiWaveView", "** Handle RELEASE");
            motionevent.getX();
            motionevent.getY();
            switchToState(4);
            flag = true;
        	break;
        case 2:
        	handleMove(motionevent);
            flag = true;
        	break;
        case 3:
        	handleMove(motionevent);
            flag = true;
        	break;
        default:
        	break;
        }
        
        invalidate();
        boolean flag1;
        if(flag)
            flag1 = true;
        else
            flag1 = super.onTouchEvent(motionevent);
        return flag1;
    }

    public final void ping()
    {
        stopChevronAnimation();
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            float f = 0.4F * (float)mHandleDrawable.getWidth();
            float f1 = 0.9F * mOuterRadius;
            float af[][] = new float[4][];
            float af1[] = new float[2];
            af1[0] = mWaveCenterX - f;
            af1[1] = mWaveCenterY;
            af[0] = af1;
            float af2[] = new float[2];
            af2[0] = f + mWaveCenterX;
            af2[1] = mWaveCenterY;
            af[1] = af2;
            float af3[] = new float[2];
            af3[0] = mWaveCenterX;
            af3[1] = mWaveCenterY - f;
            af[2] = af3;
            float af4[] = new float[2];
            af4[0] = mWaveCenterX;
            af4[1] = f + mWaveCenterY;
            af[3] = af4;
            float af5[][] = new float[4][];
            float af6[] = new float[2];
            af6[0] = mWaveCenterX - f1;
            af6[1] = mWaveCenterY;
            af5[0] = af6;
            float af7[] = new float[2];
            af7[0] = f1 + mWaveCenterX;
            af7[1] = mWaveCenterY;
            af5[1] = af7;
            float af8[] = new float[2];
            af8[0] = mWaveCenterX;
            af8[1] = mWaveCenterY - f1;
            af5[2] = af8;
            float af9[] = new float[2];
            af9[0] = mWaveCenterX;
            af9[1] = f1 + mWaveCenterY;
            af5[3] = af9;
            mChevronAnimations.clear();
            for(int i = 0; i < 4; i++)
            {
                for(int j = 0; j < mFeedbackCount; j++)
                {
                    int k = j * 160;
                    TargetDrawable targetdrawable = (TargetDrawable)mChevronDrawables.get(j + i * mFeedbackCount);
                    if(targetdrawable != null)
                    {
                        ArrayList arraylist = mChevronAnimations;
                        Object aobj[] = new Object[16];
                        aobj[0] = "ease";
                        aobj[1] = mChevronAnimationInterpolator;
                        aobj[2] = "delay";
                        aobj[3] = Integer.valueOf(k);
                        aobj[4] = "x";
                        float af10[] = new float[2];
                        af10[0] = af[i][0];
                        af10[1] = af5[i][0];
                        aobj[5] = af10;
                        aobj[6] = "y";
                        float af11[] = new float[2];
                        af11[0] = af[i][1];
                        af11[1] = af5[i][1];
                        aobj[7] = af11;
                        aobj[8] = "alpha";
                        aobj[9] = (new float[] {
                            1.0F, 0.0F
                        });
                        aobj[10] = "scaleX";
                        aobj[11] = (new float[] {
                            0.5F, 2.0F
                        });
                        aobj[12] = "scaleY";
                        aobj[13] = (new float[] {
                            0.5F, 2.0F
                        });
                        aobj[14] = "onUpdate";
                        aobj[15] = mUpdateListener;
                        arraylist.add(Tweener.to(targetdrawable, 850L, aobj));
                    }
                }

            }

        }
    }

    public final void reset(boolean flag)
    {
        stopChevronAnimation();
        stopHandleAnimation();
        stopTargetAnimation();
        hideChevrons();
        hideTargets(false);
        mHandleDrawable.setX(mWaveCenterX);
        mHandleDrawable.setY(mWaveCenterY);
        mHandleDrawable.setState(TargetDrawable.STATE_INACTIVE);
        mHandleDrawable.setAlpha(1.0F);
        if(android.os.Build.VERSION.SDK_INT >= 11)
            Tweener.reset();
    }

    public void setDirectionDescriptionsResourceId(int i)
    {
        mDirectionDescriptionsResourceId = i;
        if(mDirectionDescriptions != null)
            mDirectionDescriptions.clear();
    }

    public void setOnTriggerListener(OnTriggerListener ontriggerlistener)
    {
        mOnTriggerListener = ontriggerlistener;
    }

    public void setTargetDescriptionsResourceId(int i)
    {
        mTargetDescriptionsResourceId = i;
        if(mTargetDescriptions != null)
            mTargetDescriptions.clear();
    }

    public void setTargetResources(int i)
    {
        if(mAnimatingTargets)
            mNewTargetResources = i;
        else
            internalSetTargetResources(i);
    }

    public void setVibrateEnabled(boolean flag)
    {
        if(!flag || mVibrator != null) {
        	mVibrator = null;
        } else { 
        	mVibrator = (Vibrator)getContext().getSystemService("vibrator");
        }
    }
    
    public static interface OnTriggerListener {

        public abstract void onTrigger(int i);
    }
}
