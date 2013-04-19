/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * 
 * @author sihai
 *
 */
public class QuickActionsSubMenu implements OnClickListener, SubMenu {

	private final Context mContext;
    private final android.view.ContextMenu.ContextMenuInfo mContextMenuInfo;
    private CharSequence mHeaderTitle;
    private final QuickActionsMenuItem mItem;
    private final List mItems = new ArrayList();
    private final android.view.MenuItem.OnMenuItemClickListener mOnMenuItemClickListener;
    
    public QuickActionsSubMenu(Context context, QuickActionsMenuItem quickactionsmenuitem, android.view.ContextMenu.ContextMenuInfo contextmenuinfo, android.view.MenuItem.OnMenuItemClickListener onmenuitemclicklistener)
    {
        mContext = context;
        mItem = quickactionsmenuitem;
        mContextMenuInfo = contextmenuinfo;
        mOnMenuItemClickListener = onmenuitemclicklistener;
    }

    private static List visible(List list)
    {
        int i = 0;
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            if(((MenuItem)iterator.next()).isVisible())
                i++;
        } while(true);
        if(i != list.size())
        {
            ArrayList arraylist = new ArrayList(i);
            Iterator iterator1 = list.iterator();
            do
            {
                if(!iterator1.hasNext())
                    break;
                MenuItem menuitem = (MenuItem)iterator1.next();
                if(menuitem.isVisible())
                    arraylist.add(menuitem);
            } while(true);
            list = arraylist;
        }
        return list;
    }

    public final MenuItem add(int i)
    {
        return add(0, 0, 0, i);
    }

    public final MenuItem add(int i, int j, int k, int l)
    {
        return add(i, j, k, mContext.getText(l));
    }

    public final MenuItem add(int i, int j, int k, CharSequence charsequence)
    {
        QuickActionsMenuItem quickactionsmenuitem = new QuickActionsMenuItem(mContext, i, j, k, charsequence, mContextMenuInfo, mOnMenuItemClickListener);
        mItems.add(quickactionsmenuitem);
        return quickactionsmenuitem;
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
        throw new UnsupportedOperationException();
    }

    public final SubMenu addSubMenu(int i, int j, int k, int l)
    {
        throw new UnsupportedOperationException();
    }

    public final SubMenu addSubMenu(int i, int j, int k, CharSequence charsequence)
    {
        throw new UnsupportedOperationException();
    }

    public final SubMenu addSubMenu(CharSequence charsequence)
    {
        throw new UnsupportedOperationException();
    }

    public final void clear()
    {
        mItems.clear();
    }

    public final void clearHeader()
    {
        mHeaderTitle = null;
    }

    public final void close()
    {
        throw new UnsupportedOperationException();
    }

    public final MenuItem findItem(int i) {
    	
    	for(Iterator iterator = mItems.iterator(); iterator.hasNext();) {
    		QuickActionsMenuItem quickactionsmenuitem = (QuickActionsMenuItem)iterator.next();
    		if(i == quickactionsmenuitem.getItemId()) {
    			return quickactionsmenuitem;
    		}
    	}
    	
    	return null;
    	
    }

    public final  MenuItem getItem()
    {
        return mItem;
    }

    public final  MenuItem getItem(int i)
    {
        return (QuickActionsMenuItem)mItems.get(i);
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

    public final void onClick(DialogInterface dialoginterface, int i)
    {
        ((QuickActionsMenuItem)((AlertDialog)dialoginterface).getListView().getAdapter().getItem(i)).invoke();
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

    public final SubMenu setHeaderIcon(int i)
    {
        throw new UnsupportedOperationException();
    }

    public final SubMenu setHeaderIcon(Drawable drawable)
    {
        throw new UnsupportedOperationException();
    }

    public final SubMenu setHeaderTitle(int i)
    {
        return setHeaderTitle(mContext.getText(i));
    }

    public final SubMenu setHeaderTitle(CharSequence charsequence)
    {
        mHeaderTitle = charsequence;
        return this;
    }

    public final SubMenu setHeaderView(View view)
    {
        throw new UnsupportedOperationException();
    }

    public final SubMenu setIcon(int i)
    {
        throw new UnsupportedOperationException();
    }

    public final SubMenu setIcon(Drawable drawable)
    {
        throw new UnsupportedOperationException();
    }

    public final void setQwertyMode(boolean flag)
    {
        throw new UnsupportedOperationException();
    }

    public final void show()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        List list = visible(mItems);
        builder.setAdapter(new ArrayAdapter(mContext, 0x1090003, 0x1020014, list), this);
        builder.setIcon(0x106000d);
        if(mHeaderTitle != null)
            builder.setTitle(mHeaderTitle);
        AlertDialog alertdialog = builder.create();
        alertdialog.setCanceledOnTouchOutside(true);
        alertdialog.show();
    }

    public final int size()
    {
        return mItems.size();
    }
}
