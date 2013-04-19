/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.EditCommentFragment;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;

/**
 * 
 * @author sihai
 *
 */
public class EditCommentActivity extends EsFragmentActivity implements
		OnClickListener {

	protected EditCommentFragment mEditCommentFragment;
	
	public EditCommentActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.COMMENT;
    }

    public final void invalidateMenu()
    {
        createTitlebarButtons(R.menu.edit_comment_menu);
    }

    public final void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
        if(fragment instanceof EditCommentFragment)
            mEditCommentFragment = (EditCommentFragment)fragment;
    }

    public void onBackPressed()
    {
        mEditCommentFragment.onDiscard();
    }

    public void onClick(DialogInterface dialoginterface, int i) {
    	if(-1 == i) {
    		setResult(0);
            finish();
    	}
    	
    	dialoginterface.dismiss();
        return;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.edit_comment_activity);
        showTitlebar(true);
        setTitlebarTitle(getString(R.string.edit_comment));
        createTitlebarButtons(R.menu.edit_comment_menu);
    }

    public Dialog onCreateDialog(int i, Bundle bundle) {
    	Dialog dialog = null;
    	if(207893 == i) {
    		dialog = new ProgressDialog(this);
            ((ProgressDialog) (dialog)).setProgressStyle(0);
            ((ProgressDialog) (dialog)).setMessage(getString(R.string.post_operation_pending));
            ((ProgressDialog) (dialog)).setCancelable(false);
    	} else if(901234 == i) {
    		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.edit_comment_cancel_prompt);
            builder.setPositiveButton(R.string.yes, this);
            builder.setNegativeButton(R.string.no, this);
            builder.setCancelable(true);
            dialog = builder.create();
    	}
    	return dialog;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.edit_comment_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(i == 0x102002c)
            mEditCommentFragment.onDiscard();
        else
        if(i == R.id.menu_post)
            mEditCommentFragment.onPost();
        else
        if(i == R.id.menu_discard)
            mEditCommentFragment.onDiscard();
        else
            flag = false;
        return flag;
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.menu_post).setVisible(false);
        return true;
    }

    protected final void onPrepareTitlebarButtons(Menu menu)
    {
        int i = 0;
        while(i < menu.size()) 
        {
            MenuItem menuitem = menu.getItem(i);
            boolean flag;
            if(menuitem.getItemId() == R.id.menu_post)
                flag = true;
            else
                flag = false;
            menuitem.setVisible(flag);
            i++;
        }
    }

    protected final void onTitlebarLabelClick()
    {
        if(mEditCommentFragment != null)
            mEditCommentFragment.onDiscard();
    }
}
