/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class EsListPreference extends DialogPreference {

	private int mClickedDialogEntryIndex;
    private CharSequence mEntries[];
    private CharSequence mEntrySummaries[];
    private CharSequence mEntrySummaryArgument;
    private CharSequence mEntryValues[];
    private LayoutInflater mInflater;
    private String mValue;
    
    public EsListPreference(Context context)
    {
        this(context, null);
    }

    public EsListPreference(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.EsListPreference, 0, 0);
        mEntries = typedarray.getTextArray(0);
        mEntryValues = typedarray.getTextArray(1);
        mEntrySummaries = typedarray.getTextArray(2);
        typedarray.recycle();
        mInflater = (LayoutInflater)context.getSystemService("layout_inflater");
    }

    private int getValueIndex()
    {
        String s;
        int i;
        s = mValue;
        if(s == null || mEntryValues == null) {
        	i = -1 + mEntryValues.length;
        	if(i >= 0) {
        		if(!mEntryValues[i].equals(s)) {
        			i--;
        			return i;
        		}
        	}
        }
        i = -1;
        
        return i;
    }

    protected void onDialogClosed(boolean flag)
    {
        super.onDialogClosed(flag);
        if(flag && mClickedDialogEntryIndex >= 0 && mEntryValues != null)
        {
            String s = mEntryValues[mClickedDialogEntryIndex].toString();
            if(callChangeListener(s))
                setValue(s);
        }
    }

    protected Object onGetDefaultValue(TypedArray typedarray, int i)
    {
        return typedarray.getString(i);
    }

    protected void onPrepareDialogBuilder(android.app.AlertDialog.Builder builder)
    {
        super.onPrepareDialogBuilder(builder);
        if(mEntries == null || mEntryValues == null)
        {
            throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
        } else
        {
            mClickedDialogEntryIndex = getValueIndex();
            builder.setSingleChoiceItems(new ListAdapter() {

                public final boolean areAllItemsEnabled()
                {
                    return true;
                }

                public final int getCount()
                {
                    return mEntries.length;
                }

                public final Object getItem(int i)
                {
                    return mEntryValues[i];
                }

                public final long getItemId(int i)
                {
                    return (long)i;
                }

                public final int getItemViewType(int i)
                {
                    return 0;
                }

                public final View getView(int i, View view, ViewGroup viewgroup)
                {
                    boolean flag = true;
                    View view1;
                    TextView textview;
                    RadioButton radiobutton;
                    if(view == null)
                        view1 = mInflater.inflate(R.layout.simple_list_item_2_single_choice, viewgroup, false);
                    else
                        view1 = view;
                    ((TextView)view1.findViewById(0x1020014)).setText(mEntries[i]);
                    textview = (TextView)view1.findViewById(0x1020015);
                    if(mEntrySummaryArgument != null)
                    {
                        String s = mEntrySummaries[i].toString();
                        Object aobj[] = new Object[1];
                        aobj[0] = mEntrySummaryArgument;
                        textview.setText(String.format(s, aobj));
                    } else
                    {
                        textview.setText(mEntrySummaries[i]);
                    }
                    radiobutton = (RadioButton)view1.findViewById(R.id.radio);
                    if(i != mClickedDialogEntryIndex)
                        flag = false;
                    radiobutton.setChecked(flag);
                    return view1;
                }

                public final int getViewTypeCount()
                {
                    return 1;
                }

                public final boolean hasStableIds()
                {
                    return true;
                }

                public final boolean isEmpty()
                {
                    boolean flag;
                    if(mEntries.length == 0)
                        flag = true;
                    else
                        flag = false;
                    return flag;
                }

                public final boolean isEnabled(int i)
                {
                    return true;
                }

                public final void registerDataSetObserver(DataSetObserver datasetobserver)
                {
                }

                public final void unregisterDataSetObserver(DataSetObserver datasetobserver)
                {
                }

            }, mClickedDialogEntryIndex, new android.content.DialogInterface.OnClickListener() {

                public final void onClick(DialogInterface dialoginterface, int i)
                {
                    mClickedDialogEntryIndex = i;
                    EsListPreference.this.onClick(dialoginterface, -1);
                    dialoginterface.dismiss();
                }

            });
            builder.setPositiveButton(null, null);
            return;
        }
    }

    protected void onRestoreInstanceState(Parcelable parcelable)
    {
        if(parcelable == null || !parcelable.getClass().equals(SavedState.class))
        {
            super.onRestoreInstanceState(parcelable);
        } else
        {
            SavedState savedstate = (SavedState)parcelable;
            super.onRestoreInstanceState(savedstate.getSuperState());
            setValue(savedstate.value);
        }
    }

    protected Parcelable onSaveInstanceState()
    {
        Object obj = super.onSaveInstanceState();
        if(!isPersistent())
        {
            SavedState savedstate = new SavedState(((Parcelable) (obj)));
            savedstate.value = mValue;
            obj = savedstate;
        }
        return ((Parcelable) (obj));
    }

    protected void onSetInitialValue(boolean flag, Object obj)
    {
        String s;
        if(flag)
            s = getPersistedString(mValue);
        else
            s = (String)obj;
        setValue(s);
    }

    public final void setEntrySummaryArgument(CharSequence charsequence)
    {
        mEntrySummaryArgument = charsequence;
    }

    public final void setValue(String s)
    {
        mValue = s;
        persistString(s);
    }
    
    private static class SavedState extends android.preference.Preference.BaseSavedState {
    	
    	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public final Object createFromParcel(Parcel parcel)
            {
                return new SavedState(parcel);
            }

            public final Object[] newArray(int i)
            {
                return new SavedState[i];
            }

        };
        
    	String value;

        public SavedState(Parcel parcel)
        {
            super(parcel);
            value = parcel.readString();
        }

        public SavedState(Parcelable parcelable)
        {
            super(parcelable);
        }
        
        public void writeToParcel(Parcel parcel, int i)
        {
            super.writeToParcel(parcel, i);
            parcel.writeString(value);
        }

    }
}
