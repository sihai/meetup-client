/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * 
 * @author sihai
 *
 */
public abstract class TouchExplorationHelper extends AccessibilityNodeProviderCompat {

	private Object mCurrentItem;
    private final AccessibilityDelegateCompat mDelegate = new AccessibilityDelegateCompat() {

        public final AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View view)
        {
            return TouchExplorationHelper.this;
        }

    };
    private int mFocusedItemId;
    private final AccessibilityManager mManager;
    private final SparseArray mNodeCache = new SparseArray();
    private final android.view.View.OnHoverListener mOnHoverListener = new android.view.View.OnHoverListener() {

        public final boolean onHover(View view, MotionEvent motionevent)
        {
            boolean flag;
            boolean flag1;
            flag = AccessibilityManagerCompat.isTouchExplorationEnabled(mManager);
            flag1 = false;
            if(!flag) {
            	return false;
            }
            
            switch(motionevent.getAction())
            {
            case 8: // '\b'
            default:
                flag1 = false;
                break;

            case 7: // '\007'
            case 9: // '\t'
                Object obj = getItemAt(motionevent.getX(), motionevent.getY());
                TouchExplorationHelper.access$100(TouchExplorationHelper.this, obj);
                flag1 = true;
                break;

            case 10: // '\n'
                TouchExplorationHelper.access$100(TouchExplorationHelper.this, null);
                flag1 = true;
                break;
            }
            return flag1;

        }
    };
    private View mParentView;
    private final Rect mTempGlobalRect = new Rect();
    private final Rect mTempParentRect = new Rect();
    private final Rect mTempScreenRect = new Rect();
    
	public TouchExplorationHelper(Context context)
    {
        mFocusedItemId = 0x80000000;
        mCurrentItem = null;
        mManager = (AccessibilityManager)context.getSystemService("accessibility");
    }

    private void clearCache()
    {
        for(int i = 0; i < mNodeCache.size(); i++)
            ((AccessibilityNodeInfoCompat)mNodeCache.valueAt(i)).recycle();

        mNodeCache.clear();
    }

    private void sendEventForItem(Object obj, int i)
    {
        AccessibilityEvent accessibilityevent = AccessibilityEvent.obtain(i);
        AccessibilityRecordCompat accessibilityrecordcompat = new AccessibilityRecordCompat(accessibilityevent);
        int j = getIdForItem(obj);
        accessibilityevent.setEnabled(true);
        populateEventForItem(obj, accessibilityevent);
        if(accessibilityevent.getText().isEmpty() && TextUtils.isEmpty(accessibilityevent.getContentDescription()))
        {
            throw new RuntimeException("You must add text or a content description in populateEventForItem()");
        } else
        {
            accessibilityevent.setClassName(obj.getClass().getName());
            accessibilityevent.setPackageName(mParentView.getContext().getPackageName());
            accessibilityrecordcompat.setSource(mParentView, j);
            ((ViewGroup)mParentView.getParent()).requestSendAccessibilityEvent(mParentView, accessibilityevent);
            return;
        }
    }

    public final AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int i)
    {
        AccessibilityNodeInfoCompat accessibilitynodeinfocompat1;
        if(i == -1)
        {
            accessibilitynodeinfocompat1 = AccessibilityNodeInfoCompat.obtain(mParentView);
            ViewCompat.onInitializeAccessibilityNodeInfo(mParentView, accessibilitynodeinfocompat1);
            LinkedList linkedlist = new LinkedList();
            getVisibleItems(linkedlist);
            int i1;
            for(Iterator iterator = linkedlist.iterator(); iterator.hasNext(); accessibilitynodeinfocompat1.addChild(mParentView, i1))
                i1 = getIdForItem(iterator.next());

        } else
        {
            AccessibilityNodeInfoCompat accessibilitynodeinfocompat = (AccessibilityNodeInfoCompat)mNodeCache.get(i);
            if(accessibilitynodeinfocompat != null)
            {
                accessibilitynodeinfocompat1 = AccessibilityNodeInfoCompat.obtain(accessibilitynodeinfocompat);
            } else
            {
                Object obj = getItemForId(i);
                if(obj == null)
                {
                    accessibilitynodeinfocompat1 = null;
                } else
                {
                    accessibilitynodeinfocompat1 = AccessibilityNodeInfoCompat.obtain();
                    int j = getIdForItem(obj);
                    accessibilitynodeinfocompat1.setEnabled(true);
                    accessibilitynodeinfocompat1.setVisibleToUser(true);
                    populateNodeForItem(obj, accessibilitynodeinfocompat1);
                    if(TextUtils.isEmpty(accessibilitynodeinfocompat1.getText()) && TextUtils.isEmpty(accessibilitynodeinfocompat1.getContentDescription()))
                        throw new RuntimeException("You must add text or a content description in populateNodeForItem()");
                    accessibilitynodeinfocompat1.setPackageName(mParentView.getContext().getPackageName());
                    accessibilitynodeinfocompat1.setClassName(obj.getClass().getName());
                    accessibilitynodeinfocompat1.setParent(mParentView);
                    accessibilitynodeinfocompat1.setSource(mParentView, j);
                    if(mFocusedItemId == j)
                        accessibilitynodeinfocompat1.addAction(128);
                    else
                        accessibilitynodeinfocompat1.addAction(64);
                    accessibilitynodeinfocompat1.getBoundsInParent(mTempParentRect);
                    accessibilitynodeinfocompat1.getBoundsInScreen(mTempScreenRect);
                    if(mTempParentRect.isEmpty() && mTempScreenRect.isEmpty())
                        throw new RuntimeException("You must set parent or screen bounds in populateNodeForItem()");
                    if(mTempScreenRect.isEmpty() || mTempParentRect.isEmpty())
                    {
                        mParentView.getGlobalVisibleRect(mTempGlobalRect);
                        int k = mTempGlobalRect.left;
                        int l = mTempGlobalRect.top;
                        if(mTempScreenRect.isEmpty())
                        {
                            mTempScreenRect.set(mTempParentRect);
                            mTempScreenRect.offset(k, l);
                            accessibilitynodeinfocompat1.setBoundsInScreen(mTempScreenRect);
                        } else
                        {
                            mTempParentRect.set(mTempScreenRect);
                            mTempParentRect.offset(-k, -l);
                            accessibilitynodeinfocompat1.setBoundsInParent(mTempParentRect);
                        }
                    }
                    mNodeCache.put(i, AccessibilityNodeInfoCompat.obtain(accessibilitynodeinfocompat1));
                }
            }
        }
        return accessibilitynodeinfocompat1;
    }

    protected abstract int getIdForItem(Object obj);

    protected abstract Object getItemAt(float f, float f1);

    protected abstract Object getItemForId(int i);

    protected abstract void getVisibleItems(List list);

    public final void install(View view)
    {
        if(ViewCompat.getAccessibilityNodeProvider(view) instanceof TouchExplorationHelper)
        {
            throw new RuntimeException("Cannot install TouchExplorationHelper on a View that already has a helper installed.");
        } else
        {
            mParentView = view;
            mParentView.setOnHoverListener(mOnHoverListener);
            ViewCompat.setAccessibilityDelegate(mParentView, mDelegate);
            ViewCompat.setImportantForAccessibility(mParentView, 1);
            invalidateParent();
            return;
        }
    }

    public final void invalidateParent()
    {
        clearCache();
        ViewCompat.setAccessibilityDelegate(mParentView, mDelegate);
        mParentView.sendAccessibilityEvent(2048);
    }

    public final boolean performAction(int i, int j, Bundle bundle)
    {
    	if(-1 == i) {
    		return mDelegate.performAccessibilityAction(mParentView, j, bundle);
    	}
    	
    	// TODO
    	return false;
    }

    protected abstract boolean performActionForItem(Object obj, int i, Bundle bundle);

    protected abstract void populateEventForItem(Object obj, AccessibilityEvent accessibilityevent);

    protected abstract void populateNodeForItem(Object obj, AccessibilityNodeInfoCompat accessibilitynodeinfocompat);

    public final void uninstall()
    {
        if(mParentView == null)
        {
            throw new RuntimeException("Cannot uninstall TouchExplorationHelper on a View that does not have a helper installed.");
        } else
        {
            ViewCompat.setAccessibilityDelegate(mParentView, new AccessibilityDelegateCompat());
            ViewCompat.setImportantForAccessibility(mParentView, 0);
            clearCache();
            mParentView.setOnHoverListener(null);
            mParentView = null;
            return;
        }
    }
    
    
    static void access$100(TouchExplorationHelper touchexplorationhelper, Object obj)
    {
        if(touchexplorationhelper.mCurrentItem != obj)
        {
            if(touchexplorationhelper.mCurrentItem != null)
                touchexplorationhelper.sendEventForItem(touchexplorationhelper.mCurrentItem, 256);
            touchexplorationhelper.mCurrentItem = obj;
            if(touchexplorationhelper.mCurrentItem != null)
                touchexplorationhelper.sendEventForItem(touchexplorationhelper.mCurrentItem, 128);
        }
        return;
    }

}
