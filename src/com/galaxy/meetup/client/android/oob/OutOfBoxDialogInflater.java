/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.oob;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.server.client.domain.OutOfBoxAction;
import com.galaxy.meetup.server.client.domain.OutOfBoxDialog;
import com.galaxy.meetup.server.client.domain.OutOfBoxField;
import com.galaxy.meetup.server.client.domain.OutOfBoxView;
import com.galaxy.meetup.server.client.domain.request.MobileOutOfBoxRequest;

/**
 * 
 * @author sihai
 *
 */
public class OutOfBoxDialogInflater implements OnClickListener {

	private final ActionCallback mActionCallback;
    private final FragmentActivity mActivity;
    private final OutOfBoxView mOutOfBoxView;
    private final ViewGroup mParent;
    
    public OutOfBoxDialogInflater(FragmentActivity fragmentactivity, ViewGroup viewgroup, OutOfBoxView outofboxview, ActionCallback actioncallback)
    {
        mActivity = fragmentactivity;
        mParent = viewgroup;
        mOutOfBoxView = outofboxview;
        mActionCallback = actioncallback;
    }

    public final void inflate()
    {
        mParent.removeAllViews();
        ViewGroup viewgroup = mParent;
        OutOfBoxDialog outofboxdialog = mOutOfBoxView.dialog;
        android.content.Context context = (new android.app.AlertDialog.Builder(mActivity)).create().getContext();
        Dialog dialog = new Dialog(context);
        ViewGroup viewgroup1;
        if(outofboxdialog.header != null)
        {
            dialog.setTitle(outofboxdialog.header);
            TextView textview = (TextView)dialog.findViewById(0x1020016);
            if(textview != null)
                textview.setSingleLine(false);
        } else
        {
            dialog.requestWindowFeature(1);
        }
        dialog.setContentView(R.layout.oob_dialog);
        if(outofboxdialog.text != null)
            ((TextView)dialog.findViewById(R.id.message)).setText(outofboxdialog.text);
        viewgroup1 = (ViewGroup)dialog.findViewById(R.id.buttonPanel);
        if(outofboxdialog.action != null)
        {
            Button button;
            for(Iterator iterator = outofboxdialog.action.iterator(); iterator.hasNext(); viewgroup1.addView(button))
            {
                OutOfBoxAction outofboxaction = (OutOfBoxAction)iterator.next();
                button = (Button)LayoutInflater.from(context).inflate(R.layout.oob_dialog_button, viewgroup1, false);
                button.setText(outofboxaction.text);
                button.setTag(outofboxaction);
                button.setOnClickListener(this);
            }

        }
        viewgroup.addView(dialog.getWindow().getDecorView());
    }

    public final void onClick(View view)
    {
        OutOfBoxAction outofboxaction = (OutOfBoxAction)view.getTag();
        if("BACK".equals(outofboxaction.type))
        {
            if(!mActivity.getSupportFragmentManager().popBackStackImmediate())
            {
                mActivity.setResult(0);
                mActivity.finish();
            }
        } else
        if("CLOSE".equals(outofboxaction.type))
        {
            mActivity.setResult(0);
            mActivity.finish();
        } else
        if("URL".equals(outofboxaction.type))
        {
            Intents.viewUrl(mActivity, null, outofboxaction.url);
        } else
        {
            ActionCallback actioncallback = mActionCallback;
            MobileOutOfBoxRequest mobileoutofboxrequest = new MobileOutOfBoxRequest();
            mobileoutofboxrequest.input = new ArrayList();
            Iterator iterator = mOutOfBoxView.field.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                OutOfBoxField outofboxfield = (OutOfBoxField)iterator.next();
                if(outofboxfield.input != null)
                    mobileoutofboxrequest.input.add(outofboxfield.input);
            } while(true);
            mobileoutofboxrequest.action = new OutOfBoxAction();
            mobileoutofboxrequest.action.type = outofboxaction.type;
            actioncallback.sendOutOfBoxRequest(mobileoutofboxrequest);
        }
    }

}
