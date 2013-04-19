/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.PeopleSearchListAdapter;
import com.galaxy.meetup.client.util.SoftInput;

/**
 * 
 * @author sihai
 *
 */
public class TypeableAudienceView extends AudienceView implements TextWatcher,
		OnClickListener {

	static final boolean $assertionsDisabled;
    AudienceTextView mEditText;
    private int mEmptyAudienceHint;
    private int mMaxLines;
    ScrollView mScrollView;

    static 
    {
        boolean flag;
        if(!TypeableAudienceView.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
    public TypeableAudienceView(Context context)
    {
        this(context, null);
    }

    public TypeableAudienceView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public TypeableAudienceView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i, true);
        mMaxLines = -1;
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.AudienceView, i, 0);
        mMaxLines = typedarray.getInteger(0, -1);
        typedarray.recycle();
    }

    private void updateEditTextHint()
    {
        if(mEditText != null)
            if(mChips.isEmpty() && mEmptyAudienceHint != 0)
                mEditText.setHint(mEmptyAudienceHint);
            else
                mEditText.setHint("");
    }

    public void afterTextChanged(Editable editable)
    {
    }

    public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
    {
    }

    public final void clearText()
    {
        mEditText.setText("");
        updateEditTextHint();
    }

    protected final int getChipCount()
    {
        return -1 + mChipContainer.getChildCount();
    }

    protected final void init()
    {
        addView(inflate(R.layout.typeable_audience_view));
        mScrollView = (ScrollView)findViewById(R.id.audience_scrollview);
        mChipContainer = (MultiLineLayout)findViewById(R.id.people_audience_view_chip_container);
        mChipContainer.setOnClickListener(this);
        mEditText = (AudienceTextView)mChipContainer.getChildAt(0);
        mEditText.setThreshold(2);
        mEditText.setDropDownWidth(getResources().getDimensionPixelSize(R.dimen.audience_autocomplete_dropdown_width));
        mEditText.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

            public final void onItemClick(AdapterView adapterview, View view, int i, long l)
            {
                PeopleSearchListAdapter peoplesearchlistadapter = (PeopleSearchListAdapter)mEditText.getAdapter();
                if(peoplesearchlistadapter != null)
                {
                    peoplesearchlistadapter.onItemClick(i);
                    InputMethodManager inputmethodmanager = (InputMethodManager)getContext().getSystemService("input_method");
                    if(inputmethodmanager.isFullscreenMode())
                        inputmethodmanager.toggleSoftInput(0, 0);
                }
            }
        });
        mEditText.setOnKeyListener(new android.view.View.OnKeyListener() {

            public final boolean onKey(View view, int i, KeyEvent keyevent)
            {
                boolean flag = true;
                if(keyevent.getAction() != 0) {
                	return false; 
                } 
                
                InputMethodManager inputmethodmanager;
                boolean flag1;
                inputmethodmanager = (InputMethodManager)getContext().getSystemService("input_method");
                flag1 = inputmethodmanager.isFullscreenMode();
                if(66 == i) {
                	if(!flag1) { 
                		return false; 
                	} else { 
                		inputmethodmanager.toggleSoftInput(0, 0);
                		return flag;
                	}
                } else if(67 == i) {
                	if(mEditText.getSelectionStart() > 0 || mEditText.getSelectionEnd() > 0 || flag1) {
                		return false;
                	} else {
                		removeLastChip();
                		return flag;
                	}
                } else {
                	return false;
                }
                
            }

        });
        mEditText.setAudienceTextViewListener(new AudienceTextViewListener() {

            public final void onDeleteFromBeginning(AudienceTextView audiencetextview)
            {
                if(audiencetextview == mEditText)
                    removeLastChip();
            }

        });
        mEditText.addTextChangedListener(this);
        mEditText.setImeOptions(1);
        setEmptyAudienceHint(0);
    }

    public void onClick(View view)
    {
        Context context = getContext();
        OzViews ozviews = OzViews.getViewForLogging(context);
        EsAnalytics.recordActionEvent(context, mAccount, OzActions.PLATFORM_AUDIENCE_VIEW_CLICKED, ozviews);
        if(mChipContainer.indexOfChild(view) == -1)
        {
            if(!$assertionsDisabled && view != mChipContainer)
                throw new AssertionError();
            mEditText.requestFocus();
            SoftInput.show(mEditText);
        } else
        {
            super.onClick(view);
        }
    }

    public void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        if(!(mChipContainer instanceof MultiLineLayout)) {
        	return; 
        }
        
        MultiLineLayout multilinelayout;
        int i1;
        multilinelayout = (MultiLineLayout)mChipContainer;
        i1 = multilinelayout.getNumLines();
        if(mMaxLines == -1 || i1 < mMaxLines) {
        	if(mScrollView.getLayoutParams().height != -2)
                mScrollView.getLayoutParams().height = -2;
        } else { 
        	mScrollView.getLayoutParams().height = multilinelayout.getHeightForNumLines(mMaxLines);
            mScrollView.scrollTo(0, multilinelayout.getMeasuredHeight());
        }
        
    }

    protected void onRestoreInstanceState(Parcelable parcelable)
    {
        SavedState savedstate = (SavedState)parcelable;
        super.onRestoreInstanceState(savedstate.getSuperState());
        mEditText.setText(savedstate.text);
        mEditText.setSelection(savedstate.selectionStart, savedstate.selectionEnd);
    }

    protected Parcelable onSaveInstanceState()
    {
        SavedState savedstate = new SavedState(super.onSaveInstanceState());
        savedstate.text = mEditText.getText().toString();
        savedstate.selectionStart = mEditText.getSelectionStart();
        savedstate.selectionEnd = mEditText.getSelectionEnd();
        return savedstate;
    }

    public void onTextChanged(CharSequence charsequence, int i, int j, int k)
    {
        mEdited = true;
    }

    public void setAutoCompleteAdapter(PeopleSearchListAdapter peoplesearchlistadapter)
    {
        mEditText.setAdapter(peoplesearchlistadapter);
    }

    public void setEmptyAudienceHint(int i)
    {
        mEmptyAudienceHint = i;
        updateEditTextHint();
    }

    protected final void update()
    {
        super.update();
        updateEditTextHint();
    }
    
    
    //================================================================================
    //								Inner class
    //================================================================================
    public static class AudienceTextView extends AutoCompleteTextView {

    	private AudienceTextViewListener mListener;

        public AudienceTextView(Context context)
        {
            super(context);
        }

        public AudienceTextView(Context context, AttributeSet attributeset)
        {
            super(context, attributeset);
        }

        public AudienceTextView(Context context, AttributeSet attributeset, int i)
        {
            super(context, attributeset, i);
        }
        
        public boolean onCheckIsTextEditor()
        {
            return true;
        }

        public InputConnection onCreateInputConnection(EditorInfo editorinfo)
        {
            AudienceInputConnection audienceinputconnection = new AudienceInputConnection(super.onCreateInputConnection(editorinfo), true);
            audienceinputconnection.setAudienceTextView(this);
            return audienceinputconnection;
        }

        public void setAudienceTextViewListener(AudienceTextViewListener audiencetextviewlistener)
        {
            mListener = audiencetextviewlistener;
        }
        
        public final class AudienceInputConnection extends InputConnectionWrapper {

        	private AudienceTextView mAudienceTextView;

            public AudienceInputConnection(InputConnection inputconnection, boolean flag)
            {
                super(inputconnection, true);
            }
            
            public final boolean deleteSurroundingText(int i, int j)
            {
                int k = getSelectionStart();
                int l = getSelectionEnd();
                boolean flag;
                if(i > 0 && j <= 0 && k <= 0 && l <= 0 && mListener != null && mAudienceTextView != null)
                {
                    mListener.onDeleteFromBeginning(mAudienceTextView);
                    flag = true;
                } else
                {
                    flag = super.deleteSurroundingText(i, j);
                }
                return flag;
            }

            public final void setAudienceTextView(AudienceTextView audiencetextview)
            {
                mAudienceTextView = audiencetextview;
            }

        }

    }


    public static interface AudienceTextViewListener
    {

        public abstract void onDeleteFromBeginning(AudienceTextView audiencetextview);
    }

    public static class SavedState extends android.view.View.BaseSavedState {
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
         
         public int selectionEnd;
         public int selectionStart;
         public String text;

         public SavedState(Parcel parcel)
         {
             super(parcel.readParcelable(TypeableAudienceView.SavedState.class.getClassLoader()));
             text = parcel.readString();
             selectionStart = parcel.readInt();
             selectionEnd = parcel.readInt();
         }
         
         public SavedState(Parcelable parcelable)
         {
             super(parcelable);
         }

        public void writeToParcel(Parcel parcel, int i)
        {
            super.writeToParcel(parcel, i);
            parcel.writeString(text);
            parcel.writeInt(selectionStart);
            parcel.writeInt(selectionEnd);
        }
       
    }
}
