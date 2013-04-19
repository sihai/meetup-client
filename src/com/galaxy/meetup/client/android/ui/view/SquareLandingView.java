/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView.PressedHighlightable;
import com.galaxy.meetup.client.util.HelpUrl;

/**
 * 
 * @author sihai
 *
 */
public class SquareLandingView extends EsScrollView implements OnClickListener,
		PressedHighlightable, Recyclable {

	private static Drawable sSelectableItemBackground;
    private boolean mAlwaysExpanded;
    private int mButtonAction;
    private boolean mIsExpanded;
    private OnClickListener mOnClickListener;
    private SquareLayout mSquareLayout;
    
    
    public SquareLandingView(Context context)
    {
        super(context);
        mAlwaysExpanded = false;
        mIsExpanded = false;
        if(sSelectableItemBackground == null)
        {
            android.content.res.Resources.Theme theme = getContext().getTheme();
            int ai[] = new int[1];
            ai[0] = R.attr.buttonSelectableBackground;
            TypedArray typedarray = theme.obtainStyledAttributes(ai);
            int i = typedarray.getResourceId(0, 0);
            sSelectableItemBackground = getResources().getDrawable(i);
            typedarray.recycle();
        }
    }

    public SquareLandingView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mAlwaysExpanded = false;
        mIsExpanded = false;
        if(sSelectableItemBackground == null)
        {
            android.content.res.Resources.Theme theme = getContext().getTheme();
            int ai[] = new int[1];
            ai[0] = R.attr.buttonSelectableBackground;
            TypedArray typedarray = theme.obtainStyledAttributes(ai);
            int i = typedarray.getResourceId(0, 0);
            sSelectableItemBackground = getResources().getDrawable(i);
            typedarray.recycle();
        }
    }

    public SquareLandingView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mAlwaysExpanded = false;
        mIsExpanded = false;
        if(sSelectableItemBackground == null)
        {
            android.content.res.Resources.Theme theme = getContext().getTheme();
            int ai[] = new int[1];
            ai[0] = R.attr.buttonSelectableBackground;
            TypedArray typedarray = theme.obtainStyledAttributes(ai);
            int j = typedarray.getResourceId(0, 0);
            sSelectableItemBackground = getResources().getDrawable(j);
            typedarray.recycle();
        }
    }

    private void bindExpandArea()
    {
        ImageView imageview = mSquareLayout.expandArea;
        int i;
        if(mIsExpanded)
            i = R.drawable.icn_events_arrow_up;
        else
            i = R.drawable.icn_events_arrow_down;
        imageview.setImageResource(i);
    }

    private String getString(int i)
    {
        return getResources().getString(i);
    }

    private void initSquareLayout()
    {
        if(mSquareLayout == null)
            mSquareLayout = new SquareLayout(this);
    }

    public final void hideBlockingExplanation()
    {
        mSquareLayout.blockingExplanation.setText(null);
        mSquareLayout.blockingExplanation.setMovementMethod(null);
        mSquareLayout.blockingExplanation.setVisibility(8);
    }

    public final void hideNotificationSwitch()
    {
        mSquareLayout.notificationSection.setVisibility(8);
    }

    public final void init(boolean flag, boolean flag1)
    {
        int i = 0;
        initSquareLayout();
        mSquareLayout.joinLeaveButton.setOnClickListener(this);
        mIsExpanded = flag;
        mAlwaysExpanded = flag1;
        View view1;
        if(mAlwaysExpanded)
        {
            mSquareLayout.expandArea.setVisibility(8);
            view1 = mSquareLayout.details;
        } else
        {
            bindExpandArea();
            mSquareLayout.header.setOnClickListener(this);
            mSquareLayout.expandArea.setVisibility(0);
            View view = mSquareLayout.details;
            if(mIsExpanded)
            {
                view1 = view;
                i = 0;
            } else
            {
                i = 8;
                view1 = view;
            }
        }
        view1.setVisibility(i);
        mSquareLayout.notificationSwitch.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

            public final void onCheckedChanged(CompoundButton compoundbutton, boolean flag2)
            {
                if(mOnClickListener != null)
                    mOnClickListener.onNotificationSwitchChanged(flag2);
            }
        });
        requestLayout();
    }

    public void onClick(View view)
    {
        int i;
        i = view.getId();
        if(i == R.id.header && !mAlwaysExpanded)
        {
            if(mIsExpanded)
            {
                mSquareLayout.details.setVisibility(8);
                mIsExpanded = false;
                view.setContentDescription(getString(R.string.expand_more_info_content_description));
            } else
            {
                mSquareLayout.details.setVisibility(0);
                mIsExpanded = true;
                view.setContentDescription(getString(R.string.collapse_more_info_content_description));
            }
            bindExpandArea();
            requestLayout();
        }
        
        if(null == mOnClickListener) {
        	return;
        }
        if(i == R.id.header)
            mOnClickListener.onExpandClicked(mIsExpanded);
        else
        if(i == R.id.join_leave_button)
            mOnClickListener.onJoinLeaveClicked(mButtonAction);
        
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        initSquareLayout();
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(50);
    }

    public void onRecycle()
    {
        mOnClickListener = null;
        if(mSquareLayout != null)
        {
            mSquareLayout.squarePhoto.onRecycle();
            mSquareLayout.header.setOnClickListener(null);
            mSquareLayout.joinLeaveButton.setOnClickListener(null);
            mSquareLayout.notificationSwitch.setOnCheckedChangeListener(null);
            setMemberVisibility(false);
            hideBlockingExplanation();
        }
        mSquareLayout = null;
    }

    public void setMemberVisibility(boolean flag)
    {
        String s = mSquareLayout.memberCount.getText().toString();
        if(flag)
        {
            Spannable spannable = android.text.Spannable.Factory.getInstance().newSpannable(s);
            spannable.setSpan(new ClickableSpan() {

                public final void onClick(View view)
                {
                    if(mOnClickListener != null)
                        mOnClickListener.onMembersClicked();
                }

                public final void updateDrawState(TextPaint textpaint)
                {
                    super.updateDrawState(textpaint);
                    textpaint.setUnderlineText(false);
                }
            }, 0, s.length(), 33);
            mSquareLayout.memberCount.setBackgroundDrawable(sSelectableItemBackground);
            mSquareLayout.memberCount.setText(spannable);
            mSquareLayout.memberCount.setMovementMethod(LinkMovementMethod.getInstance());
        } else
        {
            mSquareLayout.memberCount.setBackgroundDrawable(null);
            mSquareLayout.memberCount.setText(s);
            mSquareLayout.memberCount.setMovementMethod(null);
        }
    }

    public void setOnClickListener(OnClickListener onclicklistener)
    {
        mOnClickListener = onclicklistener;
    }

    public void setSquareAboutText(String s)
    {
        if(!TextUtils.isEmpty(s))
        {
            mSquareLayout.description.setText(s);
            mSquareLayout.description.setVisibility(0);
        } else
        {
            mSquareLayout.description.setVisibility(8);
        }
    }

    public void setSquareMemberCount(int i)
    {
        if(i == 0)
        {
            mSquareLayout.memberCount.setVisibility(8);
        } else
        {
            mSquareLayout.memberCount.setVisibility(0);
            Resources resources = getResources();
            int j = R.plurals.square_members_count;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(i);
            String s = resources.getQuantityString(j, i, aobj);
            mSquareLayout.memberCount.setText(s);
        }
    }

    public void setSquareName(String s)
    {
        mSquareLayout.squareName.setText(s);
    }

    public void setSquarePhoto(String s)
    {
        if(TextUtils.isEmpty(s))
            s = null;
        mSquareLayout.squarePhoto.setUrl(s);
    }

    public void setSquareVisibility(int i) {
    	
    	boolean flag;
        int j;
        int k;
        flag = false;
        
    	if(0 == i) {
    		flag = true;
            j = R.string.square_public;
            k = R.drawable.ic_public_small;
    	} else if(1 == i) {
    		flag = true;
            j = R.string.square_private;
            k = R.drawable.ic_private_small;
    	} else {
    		 j = 0;
    	     k = 0;
    	}
    	
    	if(flag)
        {
            mSquareLayout.squareVisibility.setVisibility(0);
            mSquareLayout.squareVisibility.setText(j);
            mSquareLayout.squareVisibility.setCompoundDrawablesWithIntrinsicBounds(k, 0, 0, 0);
        } else
        {
            mSquareLayout.squareVisibility.setVisibility(8);
        }
    	
    }

    public final boolean shouldHighlightOnPress()
    {
        return false;
    }

    public final void showBlockingExplanation()
    {
        String s = getResources().getString(R.string.url_param_help_privacy_block);
        Uri uri = HelpUrl.getHelpUrl(getContext(), s);
        Resources resources = getResources();
        int i = R.string.square_blocking_explanation;
        Object aobj[] = new Object[1];
        aobj[0] = uri.toString();
        Spanned spanned = Html.fromHtml(resources.getString(i, aobj));
        URLSpan aurlspan[] = (URLSpan[])spanned.getSpans(0, spanned.length(), URLSpan.class);
        if(aurlspan.length > 0)
        {
            SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder(spanned);
            final URLSpan urlSpan = aurlspan[0];
            int j = spanned.getSpanStart(urlSpan);
            int k = spanned.getSpanEnd(urlSpan);
            spannablestringbuilder.setSpan(new ClickableSpan() {

                public final void onClick(View view)
                {
                    if(mOnClickListener != null)
                        mOnClickListener.onBlockingHelpLinkClicked(Uri.parse(urlSpan.getURL()));
                }

                public final void updateDrawState(TextPaint textpaint)
                {
                    super.updateDrawState(textpaint);
                    textpaint.setUnderlineText(false);
                }

            }, j, k, 33);
            mSquareLayout.blockingExplanation.setText(spannablestringbuilder);
            mSquareLayout.blockingExplanation.setMovementMethod(LinkMovementMethod.getInstance());
            mSquareLayout.blockingExplanation.setVisibility(0);
        }
    }

    public final void showNotificationSwitch(boolean flag)
    {
        mSquareLayout.notificationSection.setVisibility(0);
        mSquareLayout.notificationSwitch.setChecked(flag);
    }

    public final void updateJoinLeaveButton(int i) {
        int j;
        int k;
        int l;
        boolean flag;
        mButtonAction = i;
        switch(i) {
        case 0:
        	j = R.string.square_invitation_required;
            k = 0xffcccccc;
            l = R.drawable.plusone_button;
            flag = false;
        	break;
        case 1:
        	j = R.string.square_join;
            k = -1;
            l = R.drawable.plusone_by_me_button;
            flag = true;
        	break;
        case 2:
        	j = R.string.square_accept_invitation;
            k = -1;
            l = R.drawable.plusone_by_me_button;
            flag = true;
        	break;
        case 3:
        	j = R.string.square_request_to_join;
            k = -1;
            l = R.drawable.plusone_by_me_button;
            flag = true;
        	break;
        case 4:
        	j = R.string.square_leave;
            k = 0xff444444;
            l = R.drawable.plusone_button;
            flag = true;
        	break;
        case 5:
        	j = R.string.square_cancel_join_request;
            k = 0xff444444;
            l = R.drawable.plusone_button;
            flag = true;
        	break;
        default:
        	j = R.string.square_leave;
            k = 0xffcccccc;
            l = R.drawable.plusone_button;
            flag = false;
        	break;
        }
        
        mSquareLayout.joinLeaveButton.setText(j);
        mSquareLayout.joinLeaveButton.setTextColor(k);
        mSquareLayout.joinLeaveButton.setBackgroundResource(l);
        mSquareLayout.joinLeaveButton.setEnabled(flag);
        return;
        
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	public static interface OnClickListener
    {

        public abstract void onBlockingHelpLinkClicked(Uri uri);

        public abstract void onExpandClicked(boolean flag);

        public abstract void onJoinLeaveClicked(int i);

        public abstract void onMembersClicked();

        public abstract void onNotificationSwitchChanged(boolean flag);
    }

    private static final class SquareLayout
    {

        public TextView blockingExplanation;
        public TextView description;
        public View details;
        public ImageView expandArea;
        public View header;
        public Button joinLeaveButton;
        public TextView memberCount;
        public View notificationSection;
        public CompoundButton notificationSwitch;
        public ConstrainedTextView squareName;
        public EsImageView squarePhoto;
        public TextView squareVisibility;

        public SquareLayout(View view)
        {
            header = view.findViewById(R.id.header);
            details = view.findViewById(R.id.details);
            squarePhoto = (EsImageView)header.findViewById(R.id.square_photo);
            squareName = (ConstrainedTextView)header.findViewById(R.id.square_name);
            squareVisibility = (TextView)header.findViewById(R.id.square_visibility);
            memberCount = (TextView)header.findViewById(R.id.member_count);
            expandArea = (ImageView)header.findViewById(R.id.expand);
            description = (TextView)details.findViewById(R.id.description);
            joinLeaveButton = (Button)details.findViewById(R.id.join_leave_button);
            notificationSection = details.findViewById(R.id.notification_section);
            notificationSwitch = (CompoundButton)details.findViewById(R.id.notification_switch);
            blockingExplanation = (TextView)details.findViewById(R.id.blocking_explanation);
        }
    }
}
