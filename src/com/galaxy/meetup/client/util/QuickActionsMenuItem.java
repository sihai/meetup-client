/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * 
 * @author sihai
 *
 */
public class QuickActionsMenuItem implements MenuItem {

	private final Context mContext;
    private boolean mEnabled;
    private final int mGroupId;
    private Drawable mIcon;
    private Intent mIntent;
    private final int mItemId;
    private final android.view.MenuItem.OnMenuItemClickListener mMenuClickListener;
    private final android.view.ContextMenu.ContextMenuInfo mMenuInfo;
    private android.view.MenuItem.OnMenuItemClickListener mMenuItemClickListener;
    private final int mOrder;
    private QuickActionsSubMenu mSubMenu;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;
    private boolean mVisible;
    
    public QuickActionsMenuItem(Context context, int i, int j, int k, CharSequence charsequence, android.view.ContextMenu.ContextMenuInfo contextmenuinfo, android.view.MenuItem.OnMenuItemClickListener onmenuitemclicklistener)
    {
        mContext = context;
        mGroupId = i;
        mItemId = j;
        mOrder = k;
        mTitle = charsequence;
        mMenuInfo = contextmenuinfo;
        mEnabled = true;
        mVisible = true;
        mMenuClickListener = onmenuitemclicklistener;
    }

    public final boolean collapseActionView()
    {
        return false;
    }

    public final boolean expandActionView()
    {
        return false;
    }

    public final ActionProvider getActionProvider()
    {
        return null;
    }

    public final View getActionView()
    {
        throw new UnsupportedOperationException();
    }

    public final char getAlphabeticShortcut()
    {
        return '\0';
    }

    public final int getGroupId()
    {
        return mGroupId;
    }

    public final Drawable getIcon()
    {
        return mIcon;
    }

    public final Intent getIntent()
    {
        return mIntent;
    }

    public final int getItemId()
    {
        return mItemId;
    }

    public final android.view.ContextMenu.ContextMenuInfo getMenuInfo()
    {
        return mMenuInfo;
    }

    public final char getNumericShortcut()
    {
        return '\0';
    }

    public final int getOrder()
    {
        return mOrder;
    }

    public final SubMenu getSubMenu()
    {
        return mSubMenu;
    }

    public final CharSequence getTitle()
    {
        return mTitle;
    }

    public final CharSequence getTitleCondensed()
    {
        CharSequence charsequence;
        if(mTitleCondensed != null)
            charsequence = mTitleCondensed;
        else
            charsequence = mTitle;
        return charsequence;
    }

    public final boolean hasSubMenu()
    {
        boolean flag;
        if(mSubMenu != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean invoke()
    {
        boolean flag = true;
        if((mMenuItemClickListener == null || !mMenuItemClickListener.onMenuItemClick(this)) && (mMenuClickListener == null || !mMenuClickListener.onMenuItemClick(this)))
            if(mIntent != null)
                try
                {
                    mContext.startActivity(mIntent);
                }
                catch(ActivityNotFoundException activitynotfoundexception)
                {
                    flag = false;
                }
            else
            if(hasSubMenu())
                mSubMenu.show();
            else
                flag = false;
        return flag;
    }

    public final boolean isActionViewExpanded()
    {
        return false;
    }

    public final boolean isCheckable()
    {
        return false;
    }

    public final boolean isChecked()
    {
        return false;
    }

    public final boolean isEnabled()
    {
        return mEnabled;
    }

    public final boolean isVisible()
    {
        return mVisible;
    }

    public final MenuItem setActionProvider(ActionProvider actionprovider)
    {
        return null;
    }

    public final MenuItem setActionView(int i)
    {
        throw new UnsupportedOperationException();
    }

    public final MenuItem setActionView(View view)
    {
        throw new UnsupportedOperationException();
    }

    public final MenuItem setAlphabeticShortcut(char c)
    {
        return this;
    }

    public final MenuItem setCheckable(boolean flag)
    {
        return this;
    }

    public final MenuItem setChecked(boolean flag)
    {
        return this;
    }

    public final MenuItem setEnabled(boolean flag)
    {
        mEnabled = flag;
        return this;
    }

    public final MenuItem setIcon(int i)
    {
        if(i != 0)
            mIcon = mContext.getResources().getDrawable(i);
        else
            mIcon = null;
        return this;
    }

    public final MenuItem setIcon(Drawable drawable)
    {
        mIcon = drawable;
        return this;
    }

    public final MenuItem setIntent(Intent intent)
    {
        mIntent = intent;
        return this;
    }

    public final MenuItem setNumericShortcut(char c)
    {
        return this;
    }

    public final MenuItem setOnActionExpandListener(android.view.MenuItem.OnActionExpandListener onactionexpandlistener)
    {
        return null;
    }

    public final MenuItem setOnMenuItemClickListener(android.view.MenuItem.OnMenuItemClickListener onmenuitemclicklistener)
    {
        mMenuItemClickListener = onmenuitemclicklistener;
        return this;
    }

    public final MenuItem setShortcut(char c, char c1)
    {
        return this;
    }

    public final void setShowAsAction(int i)
    {
        throw new UnsupportedOperationException();
    }

    public final MenuItem setShowAsActionFlags(int i)
    {
        return null;
    }

    final void setSubMenu(QuickActionsSubMenu quickactionssubmenu)
    {
        mSubMenu = quickactionssubmenu;
    }

    public final MenuItem setTitle(int i)
    {
        return setTitle(mContext.getText(i));
    }

    public final MenuItem setTitle(CharSequence charsequence)
    {
        mTitle = charsequence;
        return this;
    }

    public final MenuItem setTitleCondensed(CharSequence charsequence)
    {
        mTitleCondensed = charsequence;
        return this;
    }

    public final MenuItem setVisible(boolean flag)
    {
        mVisible = flag;
        return this;
    }

    public final String toString()
    {
        return mTitle.toString();
    }
}
