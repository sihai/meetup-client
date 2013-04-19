/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.oob;

import java.util.List;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.BottomActionBar;
import com.galaxy.meetup.server.client.domain.OutOfBoxField;
import com.galaxy.meetup.server.client.domain.OutOfBoxInputField;
import com.galaxy.meetup.server.client.domain.OutOfBoxView;

/**
 * 
 * @author sihai
 *
 */
public class OutOfBoxInflater {

	private final BottomActionBar mBottomActionBar;
    private final LayoutInflater mInflater;
    private final ViewGroup mOuterLayout;
    private final ViewGroup mViewGroup;
    
    public OutOfBoxInflater(ViewGroup viewgroup, ViewGroup viewgroup1, BottomActionBar bottomactionbar)
    {
        mOuterLayout = viewgroup;
        mViewGroup = viewgroup1;
        mBottomActionBar = bottomactionbar;
        mInflater = LayoutInflater.from(viewgroup1.getContext());
    }

    public final void inflateFromResponse(OutOfBoxView outofboxview, final ActionCallback actionCallback)
    {
        EditText edittext;
        int i;
        List list;
        int j;
        int k;
        int i1;
        mViewGroup.removeAllViews();
        if(outofboxview.title != null)
            ((TextView)mOuterLayout.findViewById(R.id.info_title)).setText(outofboxview.title);
        if(outofboxview.header != null)
            ((TextView)mOuterLayout.findViewById(R.id.info_header)).setText(outofboxview.header);
        edittext = null;
        i = R.id.oob_item_0;
        list = outofboxview.field;
        j = list.size();
        k = j;
        for(int l = j - 1; l >= 0 && ((OutOfBoxField)list.get(l)).action != null; l--)
            k--;

        OutOfBoxField outofboxfield;
        BaseFieldLayout basefieldlayout;
        boolean flag;
        OutOfBoxInputField outofboxinputfield;
        for(i1 = 0; i1 < k; i1++) {
        	outofboxfield = (OutOfBoxField)list.get(i1);
        	if(null != outofboxfield.input) {
        		 outofboxinputfield = outofboxfield.input;
        		 
        	}
        }
        
        // TODO
    }
}
