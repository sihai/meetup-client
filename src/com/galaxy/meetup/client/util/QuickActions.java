/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;

/**
 * 
 * @author sihai
 *
 */
public class QuickActions {

	public static Dialog show(View view, View view1, android.view.ContextMenu.ContextMenuInfo contextmenuinfo, android.view.View.OnCreateContextMenuListener oncreatecontextmenulistener, android.view.MenuItem.OnMenuItemClickListener onmenuitemclicklistener, boolean flag, boolean flag1)
    {
        if(oncreatecontextmenulistener == null)
            throw new NullPointerException();
        int ai[] = new int[2];
        view.getLocationOnScreen(ai);
        int i = ai[0];
        int j = ((WindowManager)view.getContext().getSystemService("window")).getDefaultDisplay().getWidth();
        boolean flag2;
        int k;
        int ai1[];
        int l;
        QuickActionsContextMenu quickactionscontextmenu;
        int i1;
        if(i < j / 2)
            flag2 = true;
        else
            flag2 = false;
        if(flag2)
            k = i;
        else
            k = j - (i + view.getWidth());
        ai1 = new int[2];
        view.getLocationOnScreen(ai1);
        l = ai1[1];
        if(view1 != null)
        {
            int ai2[] = new int[2];
            view1.getLocationOnScreen(ai2);
            l = Math.max(ai2[1] + view1.getHeight() / 2, ai1[1] + view.getHeight() / 2);
        }
        if(flag1)
            l = ((WindowManager)view.getContext().getSystemService("window")).getDefaultDisplay().getHeight() - l;
        quickactionscontextmenu = new QuickActionsContextMenu(view.getContext(), contextmenuinfo, onmenuitemclicklistener, flag2, flag1, flag);
        oncreatecontextmenulistener.onCreateContextMenu(quickactionscontextmenu, view, contextmenuinfo);
        i1 = (int)(0.5F + -6F * view.getContext().getResources().getDisplayMetrics().density);
        if(flag1)
            i1 = 0;
        quickactionscontextmenu.showAnchoredAt(k, l + i1);
        return quickactionscontextmenu;
    }
}
