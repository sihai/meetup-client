/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionProvider;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.activity.BaseActivity;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public abstract class EsFragmentActivity extends BaseActivity {

	private boolean mHideTitleBar;
    private final MenuItem mMenuItems[] = new MenuItem[3];
    private final View.OnClickListener mTitleClickListener = new TitleClickListener();
    
    public EsFragmentActivity() {
    }
    
    private static MenuItem getVisibleItem(Menu menu, int i) {
        // TODO
    	return null;
    }

    private void setupTitleButton1(MenuItem menuitem)
    {
        ImageButton imagebutton = (ImageButton)findViewById(R.id.title_button_1);
        if(menuitem != null)
        {
            imagebutton.setImageDrawable(menuitem.getIcon());
            imagebutton.setVisibility(0);
            imagebutton.setEnabled(menuitem.isEnabled());
            imagebutton.setOnClickListener(mTitleClickListener);
            imagebutton.setContentDescription(menuitem.getTitle());
        } else
        {
            imagebutton.setVisibility(8);
        }
        mMenuItems[0] = menuitem;
    }

    private void setupTitleButton2(MenuItem menuitem)
    {
        ImageButton imagebutton = (ImageButton)findViewById(R.id.title_button_2);
        if(menuitem != null)
        {
            imagebutton.setImageDrawable(menuitem.getIcon());
            imagebutton.setVisibility(0);
            imagebutton.setEnabled(menuitem.isEnabled());
            imagebutton.setOnClickListener(mTitleClickListener);
            imagebutton.setContentDescription(menuitem.getTitle());
        } else
        {
            imagebutton.setVisibility(8);
        }
        mMenuItems[1] = menuitem;
    }

    private void setupTitleButton3(MenuItem menuitem)
    {
        Button button = (Button)findViewById(R.id.title_button_3);
        if(menuitem != null)
        {
            button.setCompoundDrawablesWithIntrinsicBounds(menuitem.getIcon(), null, null, null);
            button.setVisibility(0);
            button.setEnabled(menuitem.isEnabled());
            button.setOnClickListener(mTitleClickListener);
            button.setContentDescription(menuitem.getTitle());
            CharSequence charsequence = getTitleButton3Text();
            if(!TextUtils.isEmpty(charsequence))
            {
                android.view.ViewGroup.LayoutParams layoutparams = button.getLayoutParams();
                layoutparams.width = -2;
                button.setLayoutParams(layoutparams);
                button.setText(charsequence);
                button.setPadding(10, 0, 10, 0);
            }
        } else
        {
            button.setVisibility(8);
        }
        mMenuItems[2] = menuitem;
    }

    public final void createTitlebarButtons(int i) {
        TitleMenu titlemenu;
        setupTitleButton1(null);
        setupTitleButton2(null);
        setupTitleButton3(null);
        titlemenu = new TitleMenu(this);
        getMenuInflater().inflate(i, titlemenu);
        onPrepareTitlebarButtons(titlemenu);
        int j = 0;
        for(int k = 0; k < titlemenu.size(); k++)
            if(titlemenu.getItem(k).isVisible())
                j++;
        switch(j) {
	        case 0:
	        	break;
	        case 1:
	        	setupTitleButton3(getVisibleItem(titlemenu, 0));
	        	break;
	        case 2:
	        	setupTitleButton2(getVisibleItem(titlemenu, 0));
	            setupTitleButton3(getVisibleItem(titlemenu, 1));
	            break;
	        case 3:
	        	setupTitleButton1(getVisibleItem(titlemenu, 0));
	            setupTitleButton2(getVisibleItem(titlemenu, 1));
	            setupTitleButton3(getVisibleItem(titlemenu, 2));
	            break;
	        default:
	        	Log.e("EsFragmentActivity", (new StringBuilder("Maximum title buttons is 3. You have ")).append(j).append(" visible menu items").toString());
	        	break;
        }
    }

    protected CharSequence getTitleButton3Text()
    {
        return null;
    }

    protected final void goHome(EsAccount esaccount)
    {
        Intent intent = getIntent();
        if(null == intent) {
        	onBackPressed();
        	return;
        }
        
        Bundle bundle = intent.getExtras();
        if(bundle == null || !bundle.containsKey("notif_id") || bundle.getString("notif_id") == null) 
        	onBackPressed();
        else {
        	Intent intent1 = Intents.getHostNavigationActivityIntent(this, esaccount);
            intent1.addFlags(0x4000000);
            startActivity(intent1);
            finish();
            return;
        }
    }

    protected final boolean isIntentAccountActive()
    {
        EsAccount esaccount = (EsAccount)getIntent().getParcelableExtra("account");
        boolean flag = false;
        if(esaccount != null)
            if(!esaccount.equals(EsService.getActiveAccount(this)))
            {
                boolean flag1 = EsLog.isLoggable("EsFragmentActivity", 6);
                flag = false;
                if(flag1)
                    Log.e("EsFragmentActivity", (new StringBuilder("Activity finished because it is associated with a signed-out account: ")).append(getClass().getName()).toString());
            } else
            {
                flag = true;
            }
        return flag;
    }

    protected void onPrepareTitlebarButtons(Menu menu)
    {
    }

    protected void onTitlebarLabelClick()
    {
    }

    protected final void setTitlebarSubtitle(String s)
    {
        TextView textview = (TextView)findViewById(R.id.titlebar_label_2);
        if(s == null)
        {
            textview.setVisibility(8);
        } else
        {
            textview.setVisibility(0);
            textview.setText(s);
            textview.setClickable(true);
            textview.setOnClickListener(mTitleClickListener);
        }
    }

    protected final void setTitlebarTitle(String s)
    {
        TextView textview = (TextView)findViewById(R.id.titlebar_label);
        textview.setText(s);
        textview.setClickable(true);
        textview.setOnClickListener(mTitleClickListener);
    }

    protected final void showTitlebar(boolean flag)
    {
        showTitlebar(false, flag);
    }

    protected void showTitlebar(boolean flag, boolean flag1)
    {
        View view = findViewById(R.id.title_layout);
        if(view.getVisibility() != 0)
        {
            mHideTitleBar = false;
            Animation animation = view.getAnimation();
            if(animation != null)
                animation.cancel();
            if(flag)
                view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            View view1 = view.findViewById(R.id.titlebar_up);
            int i;
            View view2;
            if(flag1)
                i = 0;
            else
                i = 8;
            view1.setVisibility(i);
            view2 = view.findViewById(R.id.titlebar_icon_layout);
            if(flag1)
            {
                view2.setOnClickListener(mTitleClickListener);
                view2.setContentDescription(getString(R.string.nav_up_content_description));
            } else
            {
                view2.setBackgroundColor(0);
            }
            view.setVisibility(0);
        }
    }
    
	@Override
	public OzViews getViewForLogging() {
		// TODO Auto-generated method stub
		return null;
	}

	//===========================================================================
    //						Inner class
    //===========================================================================
	private final class TitleClickListener implements View.OnClickListener {

	    public final void onClick(View view) {
	        int i = view.getId();
	        if(i != R.id.titlebar_icon_layout && i != R.id.titlebar_label && i != R.id.titlebar_label_2) {
	        	if(i == R.id.title_button_1) {
	                if(mMenuItems[0] != null)
	                    onOptionsItemSelected(mMenuItems[0]);
	            } else if(i == R.id.title_button_2) {
	                if(mMenuItems[1] != null)
	                    onOptionsItemSelected(mMenuItems[1]);
	            } else {
		            if(i == R.id.title_button_3 && mMenuItems[2] != null)
		                onOptionsItemSelected(mMenuItems[2]);
	            }
	        } else {
	        	onTitlebarLabelClick();
	        }
	    }
	}
	
	private static final class TitleMenu implements Menu {

		private final Context mContext;
	    private final List<TitleMenuItem> mItems = new ArrayList<TitleMenuItem>();

	    public TitleMenu(Context context)
	    {
	        mContext = context;
	    }
	    
	    public final MenuItem add(int i) {
	        TitleMenuItem titlemenuitem = new TitleMenuItem(mContext, 0, i);
	        mItems.add(titlemenuitem);
	        return titlemenuitem;
	    }
	
	    public final MenuItem add(int i, int j, int k, int l)
	    {
	        TitleMenuItem titlemenuitem = new TitleMenuItem(mContext, j, l);
	        mItems.add(titlemenuitem);
	        return titlemenuitem;
	    }
	
	    public final MenuItem add(int i, int j, int k, CharSequence charsequence)
	    {
	        TitleMenuItem titlemenuitem = new TitleMenuItem(mContext, j, charsequence);
	        mItems.add(titlemenuitem);
	        return titlemenuitem;
	    }
	
	    public final MenuItem add(CharSequence charsequence)
	    {
	        TitleMenuItem titlemenuitem = new TitleMenuItem(mContext, 0, charsequence);
	        mItems.add(titlemenuitem);
	        return titlemenuitem;
	    }
	
	    public final int addIntentOptions(int i, int j, int k, ComponentName componentname, Intent aintent[], Intent intent, int l, 
	            MenuItem amenuitem[])
	    {
	        return 0;
	    }
	
	    public final SubMenu addSubMenu(int i)
	    {
	        return null;
	    }
	
	    public final SubMenu addSubMenu(int i, int j, int k, int l)
	    {
	        return null;
	    }
	
	    public final SubMenu addSubMenu(int i, int j, int k, CharSequence charsequence)
	    {
	        return null;
	    }
	
	    public final SubMenu addSubMenu(CharSequence charsequence)
	    {
	        return null;
	    }
	
	    public final void clear()
	    {
	        mItems.clear();
	    }
	
	    public final void close()
	    {
	    }
	
	    public final MenuItem findItem(int i) {
	    	
	    	for(TitleMenuItem titlemenuitem : mItems) {
	    		if(titlemenuitem.getItemId() == i) {
	    			return titlemenuitem;
	    		}
	    	}
	    	return null;
	    }
	
	    public final MenuItem getItem(int i)
	    {
	        return (MenuItem)mItems.get(i);
	    }
	
	    public final boolean hasVisibleItems()
	    {
	        return false;
	    }
	
	    public final boolean isShortcutKey(int i, KeyEvent keyevent)
	    {
	        return false;
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
	    }
	
	    public final void removeItem(int i)
	    {
	    }
	
	    public final void setGroupCheckable(int i, boolean flag, boolean flag1)
	    {
	    }
	
	    public final void setGroupEnabled(int i, boolean flag)
	    {
	    }
	
	    public final void setGroupVisible(int i, boolean flag)
	    {
	    }
	
	    public final void setQwertyMode(boolean flag)
	    {
	    }
	
	    public final int size()
	    {
	        return mItems.size();
	    }
	}
	
	private static final class TitleMenuItem implements MenuItem {

		private int mActionEnum;
	    private boolean mEnabled;
	    private Drawable mIcon;
	    private final int mItemId;
	    private final Resources mResources;
	    private CharSequence mTitle;
	    private boolean mVisible;

	    public TitleMenuItem(Context context, int i, int j)
	    {
	        mResources = context.getResources();
	        mTitle = mResources.getString(j);
	        mItemId = i;
	    }

	    public TitleMenuItem(Context context, int i, CharSequence charsequence)
	    {
	        mResources = context.getResources();
	        mTitle = charsequence;
	        mItemId = i;
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
	        return null;
	    }
	
	    public final char getAlphabeticShortcut()
	    {
	        return '\0';
	    }
	
	    public final int getGroupId()
	    {
	        return 0;
	    }
	
	    public final Drawable getIcon()
	    {
	        return mIcon;
	    }
	
	    public final Intent getIntent()
	    {
	        return null;
	    }
	
	    public final int getItemId()
	    {
	        return mItemId;
	    }
	
	    public final android.view.ContextMenu.ContextMenuInfo getMenuInfo()
	    {
	        return null;
	    }
	
	    public final char getNumericShortcut()
	    {
	        return '\0';
	    }
	
	    public final int getOrder()
	    {
	        return 0;
	    }
	
	    public final SubMenu getSubMenu()
	    {
	        return null;
	    }
	
	    public final CharSequence getTitle()
	    {
	        return mTitle;
	    }
	
	    public final CharSequence getTitleCondensed()
	    {
	        return null;
	    }
	
	    public final boolean hasSubMenu()
	    {
	        return false;
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
	        return this;
	    }
	
	    public final MenuItem setActionView(View view)
	    {
	        return this;
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
	            mIcon = mResources.getDrawable(i);
	        return this;
	    }
	
	    public final MenuItem setIcon(Drawable drawable)
	    {
	        mIcon = drawable;
	        return this;
	    }
	
	    public final MenuItem setIntent(Intent intent)
	    {
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
	        return this;
	    }
	
	    public final MenuItem setShortcut(char c, char c1)
	    {
	        return this;
	    }
	
	    public final void setShowAsAction(int i)
	    {
	        mActionEnum = i;
	    }
	
	    public final MenuItem setShowAsActionFlags(int i)
	    {
	        return null;
	    }
	
	    public final MenuItem setTitle(int i)
	    {
	        mTitle = mResources.getString(i);
	        return this;
	    }
	
	    public final MenuItem setTitle(CharSequence charsequence)
	    {
	        mTitle = charsequence;
	        return this;
	    }
	
	    public final MenuItem setTitleCondensed(CharSequence charsequence)
	    {
	        return this;
	    }
	
	    public final MenuItem setVisible(boolean flag)
	    {
	        mVisible = flag;
	        return this;
	    }
	}
}
