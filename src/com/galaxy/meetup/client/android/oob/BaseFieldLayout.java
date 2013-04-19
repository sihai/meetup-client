/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.oob;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.server.client.domain.MobileCoarseDate;
import com.galaxy.meetup.server.client.domain.OutOfBoxField;
import com.galaxy.meetup.server.client.domain.OutOfBoxFieldValue;
import com.galaxy.meetup.server.client.domain.OutOfBoxInputField;

/**
 * 
 * @author sihai
 *
 */
public abstract class BaseFieldLayout extends LinearLayout {

	protected ActionCallback mActionCallback;
    protected OutOfBoxField mField;
    private int mInputId;
    private int mLabelId;
    
    public BaseFieldLayout(Context context)
    {
        super(context);
    }

    public BaseFieldLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public BaseFieldLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public void bindToField(OutOfBoxField outofboxfield, int i, ActionCallback actioncallback)
    {
        View view = findViewById(R.id.label);
        if(view != null)
        {
            mLabelId = i;
            view.setId(mLabelId);
            i++;
        }
        View view1 = findViewById(R.id.input);
        if(view1 != null)
        {
            mInputId = i;
            view1.setId(mInputId);
        }
        mField = outofboxfield;
        mActionCallback = actioncallback;
        if(mField.input != null && mField.input.hasError != null && mField.input.hasError.booleanValue())
        {
            TextView textview = getLabelView();
            if(textview != null && (textview instanceof TextView))
                ((TextView)textview).setTextAppearance(getContext(), R.style.SignupErrorAppearance);
        }
    }

    public final String getActionType()
    {
        String s;
        if(mField.action != null)
            s = mField.action.type;
        else
            s = null;
        return s;
    }

    public final OutOfBoxField getField()
    {
        return mField;
    }

    public final View getInputView()
    {
        return findViewById(mInputId);
    }

    public final TextView getLabelView()
    {
        return (TextView)findViewById(mLabelId);
    }

    public final Boolean getServerBooleanValue()
    {
        OutOfBoxFieldValue outofboxfieldvalue = getServerValue();
        Boolean boolean1;
        if(outofboxfieldvalue != null)
            boolean1 = outofboxfieldvalue.boolValue;
        else
            boolean1 = null;
        return boolean1;
    }

    public final MobileCoarseDate getServerDateValue()
    {
        OutOfBoxFieldValue outofboxfieldvalue = getServerValue();
        MobileCoarseDate mobilecoarsedate;
        if(outofboxfieldvalue != null)
            mobilecoarsedate = outofboxfieldvalue.dateValue;
        else
            mobilecoarsedate = null;
        return mobilecoarsedate;
    }

    public final String getServerImageType()
    {
        String s;
        if(mField.image != null)
            s = mField.image.type;
        else
            s = null;
        return s;
    }

    public final String getServerStringValue()
    {
        OutOfBoxFieldValue outofboxfieldvalue = getServerValue();
        String s;
        if(outofboxfieldvalue != null)
            s = outofboxfieldvalue.stringValue;
        else
            s = null;
        return s;
    }

    public final OutOfBoxFieldValue getServerValue()
    {
        OutOfBoxFieldValue outofboxfieldvalue;
        if(mField.input != null)
            outofboxfieldvalue = mField.input.value;
        else
            outofboxfieldvalue = null;
        return outofboxfieldvalue;
    }

    public abstract boolean isEmpty();

    public abstract OutOfBoxInputField newFieldFromInput();

    public void setActionEnabled(boolean flag)
    {
    }

    public final boolean shouldPreventCompletionAction()
    {
        OutOfBoxInputField outofboxinputfield = mField.input;
        boolean flag = false;
        if(outofboxinputfield == null) {
        	return false; 
        } else {
        	boolean flag1;
            flag1 = "HIDDEN".equals(mField.input.type);
            flag = false;
            if(flag1) {
            	return false;
            }
            boolean flag2 = mField.input.mandatory.booleanValue();
            flag = false;
            if(flag2)
            {
                boolean flag3 = isEmpty();
                flag = false;
                if(flag3)
                    flag = true;
            }
            return flag;
        }
    }
}
