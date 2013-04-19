/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.MultiAutoCompleteTextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.ui.fragments.PeopleSearchListAdapter;
import com.galaxy.meetup.client.android.ui.fragments.PeopleSearchResults;
import com.galaxy.meetup.client.util.MentionTokenizer;
import com.galaxy.meetup.client.util.ScreenMetrics;
import com.galaxy.meetup.client.util.SoftInput;

/**
 * 
 * @author sihai
 *
 */
public class MentionMultiAutoCompleteTextView extends MultiAutoCompleteTextView {

	private AudienceView mAudienceView;
    private PeopleSearchListAdapter mMentionCursorAdapter;
    private MentionTokenizer mMentionTokenizer;
    
	public MentionMultiAutoCompleteTextView(Context context)
    {
        super(themedApplicationContext(context, null));
        mMentionTokenizer = new MentionTokenizer();
    }

    public MentionMultiAutoCompleteTextView(Context context, AttributeSet attributeset)
    {
        super(themedApplicationContext(context, attributeset), attributeset);
        mMentionTokenizer = new MentionTokenizer();
    }

    public MentionMultiAutoCompleteTextView(Context context, AttributeSet attributeset, int i)
    {
        super(themedApplicationContext(context, attributeset), attributeset, i);
        mMentionTokenizer = new MentionTokenizer();
    }

    private void adjustInputMethod(boolean flag)
    {
        int i = getInputType();
        ScreenMetrics screenmetrics = ScreenMetrics.getInstance(getContext());
        int j;
        if(getResources().getConfiguration().orientation == 1 || screenmetrics.screenDisplayType == 1 || !flag)
            j = i & 0xfffeffff;
        else
            j = i | 0x10000;
        if(i != j)
        {
            setRawInputType(j);
            InputMethodManager inputmethodmanager = SoftInput.getInputMethodManager(getContext());
            if(inputmethodmanager != null)
                inputmethodmanager.restartInput(this);
        }
    }

    private List getPersonList() {
        Editable editable = getText();
        int i = editable.length();
        MentionSpan amentionspan[] = (MentionSpan[])editable.getSpans(0, editable.length(), MentionSpan.class);
        List arraylist = new ArrayList();
        Set hashset = new HashSet();
        int j = 0;
        for(int k = amentionspan.length; j < k; j++)
        {
            String s = amentionspan[j].getAggregateId();
            if(hashset.contains(s))
                continue;
            hashset.add(s);
            String s1 = editable.subSequence(editable.getSpanStart(amentionspan[j]), Math.min(i, 1 + editable.getSpanEnd(amentionspan[j]))).toString();
            if(s1.startsWith("+"))
                s1 = s1.substring(1);
            arraylist.add(EsPeopleData.buildPersonFromPersonIdAndName(s, s1));
        }

        return arraylist;
    }

    private static Context themedApplicationContext(Context context, AttributeSet attributeset)
    {
        int i;
        if(attributeset != null)
        {
            if("dark".equalsIgnoreCase(attributeset.getAttributeValue(null, "theme_style")))
                i = R.style.CircleBrowserTheme_DarkActionBar;
            else
                i = R.style.CircleBrowserTheme;
        } else
        {
            i = R.style.CircleBrowserTheme;
        }
        return new ContextThemeWrapper(context.getApplicationContext(), i);
    }

    protected final CharSequence convertSelectionToString(Object obj)
    {
        Cursor cursor = (Cursor)obj;
        SpannableString spannablestring = new SpannableString((new StringBuilder("+")).append(super.convertSelectionToString(obj)).toString());
        int i = cursor.getColumnIndex("person_id");
        if(i != -1)
            spannablestring.setSpan(new MentionSpan(cursor.getString(i)), 0, spannablestring.length(), 33);
        return spannablestring;
    }

    public final void destroy()
    {
        if(mMentionCursorAdapter != null)
        {
            mMentionCursorAdapter.close();
            mMentionCursorAdapter = null;
        }
        setAdapter(null);
        ((ViewGroup)getParent()).removeView(this);
    }

    public final int getCursorYPosition()
    {
        Layout layout = getLayout();
        int i;
        if(layout == null)
            i = 0;
        else
            i = layout.getLineBaseline(layout.getLineForOffset(getSelectionEnd()));
        return i;
    }

    public final int getCursorYTop()
    {
        Layout layout = getLayout();
        int i;
        if(layout == null)
            i = 0;
        else
            i = layout.getLineTop(layout.getLineForOffset(getSelectionEnd()));
        return i;
    }

    public final void init(Fragment fragment, EsAccount esaccount, String s, AudienceView audienceview)
    {
        mMentionCursorAdapter = new PeopleSearchListAdapter(getContext(), fragment.getFragmentManager(), fragment.getLoaderManager(), esaccount, 1);
        mMentionCursorAdapter.setPublicProfileSearchEnabled(true);
        mMentionCursorAdapter.setIncludePlusPages(true);
        mMentionCursorAdapter.setMention(s);
        mAudienceView = audienceview;
        setAdapter(mMentionCursorAdapter);
        setTokenizer(new MentionTokenizer());
        setThreshold(3);
        addTextChangedListener(new TextWatcher() {

            public final void afterTextChanged(Editable editable)
            {
            }

            public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
                if(charsequence instanceof Spannable)
                {
                    Spannable spannable = (Spannable)charsequence;
                    int i1 = -1 + (i + j);
                    boolean flag = false;
                    List list = getPersonList();
                    URLSpan aurlspan[] = (URLSpan[])spannable.getSpans(i, i1, URLSpan.class);
                    int j1 = aurlspan.length;
                    for(int k1 = 0; k1 < j1; k1++)
                    {
                        URLSpan urlspan = aurlspan[k1];
                        if(MentionSpan.isMention(urlspan))
                        {
                            spannable.removeSpan(urlspan);
                            flag = true;
                        }
                    }

                    if(flag)
                    {
                        List list1 = getPersonList();
                        updateMentionAcls(list, list1);
                    }
                }
                int l = (int)getContext().getResources().getDimension(R.dimen.plus_mention_suggestion_popup_offset);
                setDropDownVerticalOffset((l + getCursorYPosition()) - getHeight());
            }

            public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
                int l = getSelectionEnd();
                MentionMultiAutoCompleteTextView mentionmultiautocompletetextview = MentionMultiAutoCompleteTextView.this;
                boolean flag;
                if(1 + mMentionTokenizer.findTokenStart(charsequence, l) <= l)
                    flag = true;
                else
                    flag = false;
                mentionmultiautocompletetextview.adjustInputMethod(flag);
            }
        });
    }

    protected final void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if(mMentionCursorAdapter != null)
            mMentionCursorAdapter.onStart();
    }

    protected final void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if(mMentionCursorAdapter != null)
            mMentionCursorAdapter.onStop();
    }

    public final void onRestoreInstanceState(Parcelable parcelable)
    {
        SavedState savedstate = (SavedState)parcelable;
        super.onRestoreInstanceState(savedstate.getSuperState());
        if(mMentionCursorAdapter != null)
            mMentionCursorAdapter.onCreate(savedstate.adapterState);
        Editable editable = getEditableText();
        URLSpan aurlspan[] = (URLSpan[])editable.getSpans(0, editable.length(), URLSpan.class);
        int i = aurlspan.length;
        for(int j = 0; j < i; j++)
        {
            URLSpan urlspan = aurlspan[j];
            if(MentionSpan.isMention(urlspan))
            {
                MentionSpan mentionspan = new MentionSpan(urlspan);
                int k = editable.getSpanStart(urlspan);
                int l = editable.getSpanEnd(urlspan);
                int i1 = editable.getSpanFlags(urlspan);
                editable.removeSpan(urlspan);
                editable.setSpan(mentionspan, k, l, i1);
            }
        }

    }

    public final Parcelable onSaveInstanceState()
    {
        Parcelable parcelable = super.onSaveInstanceState();
        PeopleSearchListAdapter peoplesearchlistadapter = mMentionCursorAdapter;
        Bundle bundle = null;
        if(peoplesearchlistadapter != null)
        {
            bundle = new Bundle();
            mMentionCursorAdapter.onSaveInstanceState(bundle);
        }
        return new SavedState(parcelable, bundle);
    }

    protected final void replaceText(CharSequence charsequence)
    {
        List list = getPersonList();
        super.replaceText(charsequence);
        updateMentionAcls(list, getPersonList());
        adjustInputMethod(false);
    }

    public final void setHtml(String s) {
        Spanned spanned;
        Object aobj[];
        spanned = Html.fromHtml(s);
        aobj = spanned.getSpans(0, spanned.length(), Object.class);
        if(null == aobj) {
        	setText(spanned.toString());
        	return;
        }
        // TODO
    }

    protected final void updateMentionAcls(List list, List list1)
    {
        if(mAudienceView != null)
        {
            PersonData persondata1;
            for(Iterator iterator = list1.iterator(); iterator.hasNext(); mAudienceView.addPerson(persondata1))
                persondata1 = (PersonData)iterator.next();

            Iterator iterator1 = list.iterator();
            while(iterator1.hasNext()) 
            {
                PersonData persondata = (PersonData)iterator1.next();
                if(!EsPeopleData.isPersonInList(persondata, list1))
                    mAudienceView.removePerson(persondata);
            }
        }
    }
    
	public static class SavedState extends android.view.View.BaseSavedState {
		
		final Bundle adapterState;


	     SavedState(Parcel parcel)
	     {
	         super(parcel);
	         adapterState = (Bundle)parcel.readParcelable(PeopleSearchResults.class.getClassLoader());
	     }

	     SavedState(Parcelable parcelable, Bundle bundle)
	     {
	         super(parcelable);
	         adapterState = bundle;
	     }

        public String toString() {
            return (new StringBuilder("MentionMultiAutoComplete.SavedState{")).append(Integer.toHexString(System.identityHashCode(this))).append(" ").append(adapterState).append("}").toString();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeParcelable(adapterState, 0);
        }

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public final Object createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public final Object[] newArray(int i) {
                return new SavedState[i];
            }

        };
    }
}
