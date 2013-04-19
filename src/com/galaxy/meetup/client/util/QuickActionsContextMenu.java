/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class QuickActionsContextMenu extends Dialog implements ContextMenu {

	private final android.view.ContextMenu.ContextMenuInfo mContextMenuInfo;
    private final List mItems = new ArrayList();
    private boolean mLeftAligned;
    private final android.view.MenuItem.OnMenuItemClickListener mOnMenuItemClickListener;
    private boolean mShowAbove;
    private boolean mVertical;
    
    QuickActionsContextMenu(Context context, android.view.ContextMenu.ContextMenuInfo contextmenuinfo, android.view.MenuItem.OnMenuItemClickListener onmenuitemclicklistener, boolean flag, boolean flag1, boolean flag2)
    {
        super(context, R.style.QuickActions);
        mLeftAligned = flag;
        mVertical = flag2;
        mShowAbove = flag1;
        mContextMenuInfo = contextmenuinfo;
        mOnMenuItemClickListener = onmenuitemclicklistener;
        Window window = getWindow();
        window.clearFlags(2);
        window.setLayout(-1, -2);
        int i;
        if(flag)
        {
            if(flag1)
                i = R.drawable.tooltip_top_left_background;
            else
                i = R.drawable.tooltip_bottom_left_background;
        } else
        if(flag1)
            i = R.drawable.tooltip_top_right_background;
        else
            i = R.drawable.tooltip_bottom_right_background;
        window.setBackgroundDrawableResource(i);
        setCanceledOnTouchOutside(true);
    }

    public QuickActionsMenuItem add(int i, int j, int k, CharSequence charsequence)
    {
        QuickActionsMenuItem quickactionsmenuitem = new QuickActionsMenuItem(getContext(), i, j, k, charsequence, mContextMenuInfo, mOnMenuItemClickListener);
        mItems.add(quickactionsmenuitem);
        return quickactionsmenuitem;
    }

    public final MenuItem add(int i)
    {
        return add(0, 0, 0, i);
    }

    public final MenuItem add(int i, int j, int k, int l)
    {
        return add(i, j, k, getContext().getText(l));
    }

    public final MenuItem add(CharSequence charsequence)
    {
        return add(0, 0, 0, charsequence);
    }

    public final int addIntentOptions(int i, int j, int k, ComponentName componentname, Intent aintent[], Intent intent, int l, 
            MenuItem amenuitem[])
    {
        throw new UnsupportedOperationException();
    }

    public final SubMenu addSubMenu(int i)
    {
        return addSubMenu(0, 0, 0, i);
    }

    public final SubMenu addSubMenu(int i, int j, int k, int l)
    {
        return addSubMenu(i, j, k, getContext().getText(l));
    }

    public final SubMenu addSubMenu(int i, int j, int k, CharSequence charsequence)
    {
        Context context = getContext();
        QuickActionsMenuItem quickactionsmenuitem = add(i, j, k, charsequence);
        QuickActionsSubMenu quickactionssubmenu = new QuickActionsSubMenu(context, quickactionsmenuitem, mContextMenuInfo, mOnMenuItemClickListener);
        quickactionsmenuitem.setSubMenu(quickactionssubmenu);
        return quickactionssubmenu;
    }

    public final SubMenu addSubMenu(CharSequence charsequence)
    {
        return addSubMenu(0, 0, 0, charsequence);
    }

    public final void clear()
    {
        mItems.clear();
    }

    public final void clearHeader()
    {
    }

    public final void close()
    {
        dismiss();
    }

    public final MenuItem findItem(int i) {
    	
    	MenuItem menuitem;
    	QuickActionsMenuItem qm = null;
    	for(Iterator iterator = mItems.iterator(); iterator.hasNext();) {
    		qm = (QuickActionsMenuItem)iterator.next();
    		if(i == ((MenuItem) (qm)).getItemId()) {
    			return qm;
    		} else if(((MenuItem) (qm)).hasSubMenu()) {
    			menuitem = ((MenuItem) (qm)).getSubMenu().findItem(i);
    			if(null != menuitem) {
    				return menuitem;
    			}
    		}
    	}
    	return null;
    }

    public final MenuItem getItem(int i)
    {
        return (MenuItem)mItems.get(i);
    }

    public final boolean hasVisibleItems() {
    	
    	for(Iterator iterator = mItems.iterator(); iterator.hasNext();) {
    		if(((QuickActionsMenuItem)iterator.next()).isVisible()) {
    			return true;
    		}
    	}
    	return false;
    }

    public final boolean isShortcutKey(int i, KeyEvent keyevent)
    {
        return false;
    }

    protected final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        int i;
        int j;
        Context context;
        ViewGroup viewgroup;
        LayoutInflater layoutinflater;
        Iterator iterator;
        if(mVertical)
            i = R.layout.quick_actions_dialog_vertical;
        else
            i = R.layout.quick_actions_dialog;
        setContentView(i);
        if(mVertical)
            j = R.layout.quick_actions_item_vertical;
        else
            j = R.layout.quick_actions_item;
        context = getContext();
        viewgroup = (ViewGroup)findViewById(R.id.quick_actions_buttons);
        layoutinflater = LayoutInflater.from(context);
        iterator = mItems.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Drawable drawable;
            int k;
            final QuickActionsMenuItem item = (QuickActionsMenuItem)iterator.next();
            if(item.isVisible())
            {
                Button button;
                CharSequence charsequence;
                if(viewgroup.getChildCount() != 0)
                {
                    if(mVertical)
                        k = R.layout.quick_actions_divider_horizontal;
                    else
                        k = R.layout.quick_actions_divider_vertical;
                    layoutinflater.inflate(k, viewgroup, true);
                }
                button = (Button)layoutinflater.inflate(j, viewgroup, false);
                charsequence = item.getTitle();
                drawable = item.getIcon();
                if(drawable != null)
                {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    button.setText((new StringBuilder(" ")).append(charsequence).toString());
                } else
                {
                    button.setText(charsequence);
                }
                button.setCompoundDrawables(drawable, null, null, null);
                button.setEnabled(item.isEnabled());
                button.setOnClickListener(new android.view.View.OnClickListener() {

                    public final void onClick(View view)
                    {
                        item.invoke();
                        dismiss();
                    }
                });
                viewgroup.addView(button);
            }
        } while(true);
    }

    public final boolean onTouchEvent(MotionEvent motionevent)
    {
        boolean flag = true;
        if(!super.onTouchEvent(motionevent))
            if(motionevent.getAction() == 0)
                dismiss();
            else
                flag = false;
        return flag;
    }

    public final boolean performIdentifierAction(int i, int j)
    {
        return false;
    }

    public final boolean performShortcut(int i, KeyEvent keyevent, int j)
    {
        return false;
    }

    public final void removeGroup(int i)
    {
        throw new UnsupportedOperationException();
    }

    public final void removeItem(int i)
    {
        mItems.remove(findItem(i));
    }

    public final void setGroupCheckable(int i, boolean flag, boolean flag1)
    {
        throw new UnsupportedOperationException();
    }

    public final void setGroupEnabled(int i, boolean flag)
    {
        throw new UnsupportedOperationException();
    }

    public final void setGroupVisible(int i, boolean flag)
    {
        throw new UnsupportedOperationException();
    }

    public final ContextMenu setHeaderIcon(int i)
    {
        return this;
    }

    public final ContextMenu setHeaderIcon(Drawable drawable)
    {
        return this;
    }

    public final ContextMenu setHeaderTitle(int i)
    {
        return this;
    }

    public final ContextMenu setHeaderTitle(CharSequence charsequence)
    {
        return this;
    }

    public final ContextMenu setHeaderView(View view)
    {
        return this;
    }

    public final void setQwertyMode(boolean flag)
    {
    }

    final void showAnchoredAt(int i, int j)
    {
        Window window = getWindow();
        android.view.WindowManager.LayoutParams layoutparams = window.getAttributes();
        int k;
        int l;
        byte byte0;
        if(mShowAbove)
            k = 80;
        else
            k = 48;
        layoutparams.gravity = k;
        layoutparams.y = j;
        l = layoutparams.gravity;
        if(mLeftAligned)
            byte0 = 3;
        else
            byte0 = 5;
        layoutparams.gravity = byte0 | l;
        layoutparams.x = i;
        window.setAttributes(layoutparams);
        show();
    }

    public final int size()
    {
        return mItems.size();
    }

}
