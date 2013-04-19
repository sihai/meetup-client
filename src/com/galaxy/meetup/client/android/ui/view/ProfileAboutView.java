/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.xml.sax.XMLReader;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView.PressedHighlightable;
import com.galaxy.meetup.client.util.SpannableUtils;
import com.galaxy.meetup.server.client.domain.GoogleReviewProto;
import com.galaxy.meetup.server.client.domain.ZagatAspectRatingProto;
import com.galaxy.meetup.server.client.domain.ZagatAspectRatingsProto;

/**
 * 
 * @author sihai
 *
 */
public class ProfileAboutView extends EsScrollView implements OnClickListener,
		PressedHighlightable, Recyclable {

	private static Drawable mEducationBackground;
    private static Drawable mLocationsBackground;
    private static final DisplayPolicies sDefaultPolicy = new DisplayPolicies();
    private static Drawable sEmploymentBackground;
    private static int sIsExpandedMarginBottom;
    private static int sPlusOneStandardTextColor;
    private static float sPlusOneTextSize;
    private static int sPlusOnedByMeTextColor;
    private DetailsLayout mDetails;
    boolean mEditEnabled;
    private HeaderLayout mHeader;
    private final LayoutInflater mInflater;
    boolean mIsExpanded;
    private OnClickListener mOnClickListener;
    private DisplayPolicies mPolicy;
    private ProfileLayout mProfileLayout;
    
    
    private static final class DetailsLayout {

        public ViewGroup addresses;
        public View birthday;
        public ViewGroup contactSection;
        public View container;
        public ViewGroup educationSection;
        public View educationSectionLastLocation;
        public ViewGroup emails;
        public View gender;
        public View introduction;
        public ViewGroup links;
        public ViewGroup linksSection;
        public ViewGroup locations;
        public ViewGroup locationsSection;
        public ImageResourceView map;
        public ViewGroup personalSection;
        public ViewGroup phoneNumbers;
        public View tagLine;
        public ViewGroup workSection;
        public View workSectionLastLocation;

        DetailsLayout()
        {
        }
    }

    public static final class DisplayPolicies
    {

        public boolean hideButtons;
        public boolean showDetailsAlways;
        public boolean showExpandButtonText;
        public boolean showInfoIcons;

        public DisplayPolicies()
        {
            showInfoIcons = true;
            hideButtons = false;
            showExpandButtonText = false;
            showDetailsAlways = false;
        }
    }

    private static final class HeaderLayout
    {

        public CirclesButton addToCirclesButton;
        public TextView addedByCount;
        public ImageView avatarChoosePhotoIcon;
        public ImageResourceView avatarImage;
        public TextView blockedText;
        public View buttons;
        public CirclesButton circlesButton;
        public View container;
        public CoverPhotoImageView coverPhoto;
        public ImageView coverPhotoChoosePhotoIcon;
        public InfoRow education;
        public InfoRow employer;
        public TextView expandArea;
        public TextView familyName;
        public ConstrainedTextView fullName;
        public TextView givenName;
        public InfoRow location;
        public Button plusOneButton;
        public ProgressBar progressBar;
        public View scrapbookAlbum;
        public ImageResourceView scrapbookPhoto[];

        HeaderLayout()
        {
            scrapbookPhoto = new ImageResourceView[5];
        }

    }

    private static final class InfoRow
    {

        View container;
        public ImageView icon;
        public TextView text;

        public InfoRow(View view, int i)
        {
            container = view.findViewById(i);
            icon = (ImageView)container.findViewById(R.id.icon);
            text = (TextView)container.findViewById(R.id.text);
        }
    }

    public final class IntroductionTagHandler implements android.text.Html.TagHandler {
    	
    	private Stack mListStack;
    	 
        private void handleListTag(boolean flag, Editable editable, boolean flag1)
        {
            if(mListStack == null)
                mListStack = new Stack();
            if(!flag) {
            	if(!mListStack.isEmpty())
                    mListStack.pop();
            } else {
            	if(editable.length() == 0 || editable.charAt(-1 + editable.length()) != '\n')
                    editable.append("\n");
                Stack stack = mListStack;
                int i;
                if(flag1)
                    i = 0;
                else
                    i = -1;
                stack.push(Integer.valueOf(i));
            }
        }

        public final void handleTag(boolean flag, String s, Editable editable, XMLReader xmlreader) {
            boolean flag1 = true;
            
            if("ul".equals(s)) {
            	handleListTag(flag, editable, false);
            	return;
            }
            if("ol".equals(s)) {
                handleListTag(flag, editable, flag1);
            } else {
	            if("li".equals(s)) {
	                if(flag)
	                {
	                    int i;
	                    int size = 0;
	                    if(mListStack != null)
	                    	size = mListStack.size();
	                    for(i = 0; i < size; i++)
	                        editable.append("  ");
	
	                    String s1;
	                    if(mListStack == null || mListStack.isEmpty() || ((Integer)mListStack.peek()).intValue() == -1)
	                    {
	                        s1 = "? ";
	                    } else
	                    {
	                        int j = 1 + ((Integer)mListStack.pop()).intValue();
	                        mListStack.push(Integer.valueOf(j));
	                        s1 = (new StringBuilder()).append(j).append(". ").toString();
	                    }
	                    editable.append(s1);
	                } else
	                {
	                    editable.append("\n");
	                }
	            }
            }
        }
    }

    private static class Item {

        Item()
        {
        }
    }

    private final class ItemOnTouchListener implements android.view.View.OnTouchListener {
    	
    	MotionEvent mLastEvent;
    	
        public final boolean onTouch(final View view, final MotionEvent event) {
            switch(event.getAction()) {
	            case 0:
	            	 mLastEvent = event;
	                 postDelayed(new Runnable() {
	
	                     public final void run()
	                     {
	                         if(mLastEvent == event)
	                             view.setBackgroundColor(0xffcccccc);
	                         mLastEvent = null;
	                     }
	                 }, 100L);
	            	break;
	            case 1:
	            	 mLastEvent = null;
	                 view.setBackgroundColor(0);
	            	break;
	            case 2:
	            	break;
	            case 3:
	            	mLastEvent = null;
	                view.setBackgroundColor(0);
	            	break;
            	default:
            		break;
            }
            return false;
        }
    }

    private static final class LocalActionsItem extends Item
    {

        final String address;
        final String mapsCid;
        final String phone;
        final String title;

        public LocalActionsItem(String s, String s1, String s2, String s3)
        {
            super();
            phone = s;
            title = s1;
            mapsCid = s2;
            address = s3;
        }
    }

    private static final class LocalDetailsItem extends Item
    {

        final List knownForTerms;
        final String openingHoursFull;
        final String openingHoursSummary;
        final String phone;

        public LocalDetailsItem(List list, String s, String s1, String s2)
        {
            super();
            phone = s;
            knownForTerms = list;
            openingHoursSummary = s1;
            openingHoursFull = s2;
        }
    }

    private static final class LocalEditorialReviewItem extends Item
    {

        final String editorialText;
        final String priceLabel;
        final String priceValue;
        final int reviewCount;
        final ZagatAspectRatingsProto scores;

        public LocalEditorialReviewItem(ZagatAspectRatingsProto zagataspectratingsproto, String s, String s1, String s2, int i)
        {
            super();
            scores = zagataspectratingsproto;
            editorialText = s;
            priceLabel = s1;
            priceValue = s2;
            reviewCount = i;
        }
    }

    private static final class LocationItem extends Item
    {

        final String address;
        final boolean current;

        public LocationItem(String s, boolean flag)
        {
            super();
            address = s;
            current = flag;
        }
    }

    public static interface OnClickListener
    {

        public abstract void onAddressClicked(String s);

        public abstract void onAvatarClicked();

        public abstract void onCirclesButtonClicked();

        public abstract void onCoverPhotoClicked(int i);

        public abstract void onEditEducationClicked();

        public abstract void onEditEmploymentClicked();

        public abstract void onEditPlacesLivedClicked();

        public abstract void onEmailClicked(String s);

        public abstract void onExpandClicked(boolean flag);

        public abstract void onLinkClicked(String s);

        public abstract void onLocalCallClicked(String s);

        public abstract void onLocalDirectionsClicked(String s);

        public abstract void onLocalMapClicked(String s);

        public abstract void onLocalReviewClicked(int i, int j);

        public abstract void onLocationClicked(String s);

        public abstract void onPhoneNumberClicked(String s);

        public abstract void onPlusOneClicked();

        public abstract void onReviewAuthorAvatarClicked(String s);

        public abstract void onSendTextClicked(String s);

        public abstract void onZagatExplanationClicked();
    }

    private static final class ProfileLayout
    {

        private static final int SCRAPBOOK_PHOTO_IDS[];
        public DetailsLayout details;
        public TextView error;
        public HeaderLayout header;

        static 
        {
            int ai[] = new int[5];
            ai[0] = R.id.photo_1;
            ai[1] = R.id.photo_2;
            ai[2] = R.id.photo_3;
            ai[3] = R.id.photo_4;
            ai[4] = R.id.photo_5;
            SCRAPBOOK_PHOTO_IDS = ai;
        }

        public ProfileLayout(View view)
        {
            header = new HeaderLayout();
            details = new DetailsLayout();
            error = (TextView)view.findViewById(R.id.server_error);
            View view1 = view.findViewById(R.id.header);
            header.container = view1;
            header.coverPhoto = (CoverPhotoImageView)view1.findViewById(R.id.cover_photo_image);
            header.coverPhotoChoosePhotoIcon = (ImageView)view1.findViewById(R.id.choose_cover_photo_icon);
            header.coverPhoto.setResourceLoadingDrawable(R.drawable.profile_scrapbook_loading);
            header.coverPhoto.setResourceMissingDrawable(R.drawable.default_cover_photo);
            header.scrapbookAlbum = view1.findViewById(R.id.scrapbook_album);
            for(int i = 0; i < 5; i++)
            {
                header.scrapbookPhoto[i] = (ImageResourceView)header.scrapbookAlbum.findViewById(SCRAPBOOK_PHOTO_IDS[i]);
                header.scrapbookPhoto[i].setSizeCategory(2);
                header.scrapbookPhoto[i].setResourceLoadingDrawable(R.drawable.profile_scrapbook_loading);
                header.scrapbookPhoto[i].setResourceMissingDrawable(R.drawable.profile_scrapbook_loading);
            }

            header.avatarImage = (ImageResourceView)view1.findViewById(R.id.avatar_image);
            header.avatarImage.setResourceLoadingDrawable(R.drawable.profile_avatar_loading);
            header.avatarImage.setResourceMissingDrawable(new BitmapDrawable(view.getResources(), EsAvatarData.getMediumDefaultAvatar(view.getContext())));
            header.avatarChoosePhotoIcon = (ImageView)view1.findViewById(R.id.choose_photo_icon);
            header.addedByCount = (TextView)view1.findViewById(R.id.added_by_count);
            header.fullName = (ConstrainedTextView)view1.findViewById(R.id.full_name);
            header.givenName = (TextView)view1.findViewById(R.id.given_name);
            header.familyName = (TextView)view1.findViewById(R.id.family_name);
            header.employer = new InfoRow(view1, R.id.employer);
            header.education = new InfoRow(view1, R.id.education);
            header.location = new InfoRow(view1, R.id.location);
            header.buttons = view1.findViewById(R.id.buttons);
            header.circlesButton = (CirclesButton)view1.findViewById(R.id.circles_button);
            header.addToCirclesButton = (CirclesButton)view1.findViewById(R.id.add_to_circles_button);
            header.addToCirclesButton.setShowIcon(false);
            header.blockedText = (TextView)view1.findViewById(R.id.blocked);
            header.progressBar = (ProgressBar)view1.findViewById(R.id.progress_bar);
            header.plusOneButton = (Button)view1.findViewById(R.id.plus_one);
            header.plusOneButton.setTextSize(0, ProfileAboutView.sPlusOneTextSize);
            header.plusOneButton.setTypeface(Typeface.DEFAULT_BOLD);
            header.expandArea = (TextView)view1.findViewById(R.id.expand);
            View view2 = view.findViewById(R.id.details);
            details.container = view2;
            details.tagLine = view2.findViewById(R.id.tagline);
            details.introduction = view2.findViewById(R.id.intro);
            details.contactSection = (ViewGroup)view2.findViewById(R.id.contact);
            details.emails = (ViewGroup)details.contactSection.findViewById(R.id.email_content);
            details.phoneNumbers = (ViewGroup)details.contactSection.findViewById(R.id.phone_content);
            details.addresses = (ViewGroup)details.contactSection.findViewById(R.id.address_content);
            details.personalSection = (ViewGroup)view2.findViewById(R.id.personal);
            details.workSection = (ViewGroup)view2.findViewById(R.id.work_section);
            details.educationSection = (ViewGroup)view2.findViewById(R.id.education);
            details.locationsSection = (ViewGroup)view2.findViewById(R.id.places);
            details.map = (ImageResourceView)details.locationsSection.findViewById(R.id.map);
            details.locations = (ViewGroup)details.locationsSection.findViewById(R.id.content);
            details.linksSection = (ViewGroup)view2.findViewById(R.id.links);
            details.links = (ViewGroup)details.linksSection.findViewById(R.id.link_content);
        }
    }
    
    public ProfileAboutView(Context context)
    {
        super(context);
        mPolicy = sDefaultPolicy;
        mIsExpanded = false;
        mEditEnabled = false;
        mInflater = (LayoutInflater)getContext().getSystemService("layout_inflater");
        Resources resources = getContext().getResources();
        if(sPlusOnedByMeTextColor == 0)
        {
            sPlusOnedByMeTextColor = resources.getColor(R.color.card_plus_oned_text);
            sPlusOneStandardTextColor = resources.getColor(R.color.card_not_plus_oned_text);
            sPlusOneTextSize = resources.getDimension(R.dimen.card_plus_oned_text_size);
            sIsExpandedMarginBottom = resources.getDimensionPixelOffset(R.dimen.profile_card_bottom_padding);
            sEmploymentBackground = resources.getDrawable(R.drawable.profile_selectable_item_background);
            mEducationBackground = resources.getDrawable(R.drawable.profile_selectable_item_background);
            mLocationsBackground = resources.getDrawable(R.drawable.profile_selectable_item_background);
        }
    }

    public ProfileAboutView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mPolicy = sDefaultPolicy;
        mIsExpanded = false;
        mEditEnabled = false;
        mInflater = (LayoutInflater)getContext().getSystemService("layout_inflater");
        Resources resources = getContext().getResources();
        if(sPlusOnedByMeTextColor == 0)
        {
            sPlusOnedByMeTextColor = resources.getColor(R.color.card_plus_oned_text);
            sPlusOneStandardTextColor = resources.getColor(R.color.card_not_plus_oned_text);
            sPlusOneTextSize = resources.getDimension(R.dimen.card_plus_oned_text_size);
            sIsExpandedMarginBottom = resources.getDimensionPixelOffset(R.dimen.profile_card_bottom_padding);
            sEmploymentBackground = resources.getDrawable(R.drawable.profile_selectable_item_background);
            mEducationBackground = resources.getDrawable(R.drawable.profile_selectable_item_background);
            mLocationsBackground = resources.getDrawable(R.drawable.profile_selectable_item_background);
        }
    }

    public ProfileAboutView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mPolicy = sDefaultPolicy;
        mIsExpanded = false;
        mEditEnabled = false;
        mInflater = (LayoutInflater)getContext().getSystemService("layout_inflater");
        Resources resources = getContext().getResources();
        if(sPlusOnedByMeTextColor == 0)
        {
            sPlusOnedByMeTextColor = resources.getColor(R.color.card_plus_oned_text);
            sPlusOneStandardTextColor = resources.getColor(R.color.card_not_plus_oned_text);
            sPlusOneTextSize = resources.getDimension(R.dimen.card_plus_oned_text_size);
            sIsExpandedMarginBottom = resources.getDimensionPixelOffset(R.dimen.profile_card_bottom_padding);
            sEmploymentBackground = resources.getDrawable(R.drawable.profile_selectable_item_background);
            mEducationBackground = resources.getDrawable(R.drawable.profile_selectable_item_background);
            mLocationsBackground = resources.getDrawable(R.drawable.profile_selectable_item_background);
        }
    }

    private void addReviewToParent(GoogleReviewProto googlereviewproto, View view, int i)
    {
        LinearLayout linearlayout = (LinearLayout)view.findViewById(R.id.content);
        View view1 = mInflater.inflate(R.layout.profile_item_local_user_review, linearlayout, false);
        int j = linearlayout.getChildCount();
        LocalReviewListItemView localreviewlistitemview = (LocalReviewListItemView)view1.findViewById(R.id.local_review_item);
        boolean flag;
        boolean flag1;
        Integer ainteger[];
        if(j == 0)
            flag = true;
        else
            flag = false;
        if(!flag)
            flag1 = true;
        else
            flag1 = false;
        localreviewlistitemview.setTopBorderVisible(flag1);
        localreviewlistitemview.setReview(googlereviewproto);
        localreviewlistitemview.setAuthorAvatarOnClickListener(this);
        ainteger = new Integer[2];
        ainteger[0] = Integer.valueOf(i);
        ainteger[1] = Integer.valueOf(j);
        localreviewlistitemview.setTag(ainteger);
        localreviewlistitemview.setOnClickListener(this);
        linearlayout.addView(view1);
    }

    private void bindDataView(View view, int i, String s, String s1)
    {
        ImageView imageview = (ImageView)view.findViewById(0x1020006);
        if(i != 0)
            imageview.setImageResource(i);
        else
            imageview.setImageDrawable(null);
        ((TextView)view.findViewById(0x1020014)).setText(s);
        ((TextView)view.findViewById(0x1020015)).setText(s1.toUpperCase());
        setupContentDescription(view, s, s1);
    }

    private void bindExpandArea()
    {
        int i;
        int j;
        if(mPolicy.showExpandButtonText)
        {
            int k;
            if(mIsExpanded)
                k = R.string.profile_show_less;
            else
                k = R.string.profile_show_more;
            mHeader.expandArea.setText(k);
        }
        if(mIsExpanded)
        {
            i = R.drawable.icn_events_arrow_up;
            j = 0;
        } else
        {
            i = R.drawable.icn_events_arrow_down;
            j = sIsExpandedMarginBottom;
        }
        mHeader.expandArea.setCompoundDrawablesWithIntrinsicBounds(0, 0, i, 0);
        mHeader.expandArea.setVisibility(0);
        mHeader.container.setPadding(0, 0, 0, j);
    }

    private void bindIntroductionView(View view, String s)
    {
        TextView textview = (TextView)view;
        SpannableStringBuilder spannablestringbuilder = ClickableStaticLayout.buildStateSpans(s, new IntroductionTagHandler());
        int i = spannablestringbuilder.length();
        int j;
        for(j = 0; j != i && Character.isWhitespace(spannablestringbuilder.charAt(j)); j++);
        if(j != 0)
        {
            spannablestringbuilder.delete(0, j);
            i = spannablestringbuilder.length();
        }
        int k;
        for(k = i - 1; k >= 0 && Character.isWhitespace(spannablestringbuilder.charAt(k)); k--);
        if(k != i - 1)
            spannablestringbuilder.delete(k + 1, i);
        textview.setText(spannablestringbuilder);
        textview.setContentDescription(spannablestringbuilder);
        if(!(textview.getMovementMethod() instanceof LinkMovementMethod))
            textview.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void bindLinkView(View view, String s, String s1, String s2)
    {
        ((EsImageView)view.findViewById(0x1020006)).setUrl(s1);
        ((TextView)view.findViewById(0x1020014)).setText(s);
        TextView textview = (TextView)view.findViewById(0x1020015);
        if(s2 != null)
        {
            textview.setVisibility(0);
            textview.setText(s2.toUpperCase());
            setupContentDescription(view, s, s2);
        } else
        {
            textview.setVisibility(8);
            view.setContentDescription(s);
        }
    }

    private void bindSectionHeader(SectionHeaderView sectionheaderview, int i, boolean flag)
    {
        sectionheaderview.setText(i);
        sectionheaderview.setContentDescription(getString(i));
        sectionheaderview.enableEditIcon(flag);
    }

    private void enableAvatarChangePhotoIcon(boolean flag)
    {
        ImageView imageview = mHeader.avatarChoosePhotoIcon;
        int i;
        if(flag)
            i = 0;
        else
            i = 8;
        imageview.setVisibility(i);
    }

    private void enableCoverPhotoChangePhotoIcon(boolean flag)
    {
        ImageView imageview = mHeader.coverPhotoChoosePhotoIcon;
        int i;
        if(flag)
            i = 0;
        else
            i = 8;
        imageview.setVisibility(i);
    }

    private static void enableDivider(View view, boolean flag)
    {
        View view1 = view.findViewById(R.id.divider);
        if(view1 != null)
        {
            int i;
            if(flag)
                i = 0;
            else
                i = 8;
            view1.setVisibility(i);
        }
    }

    private View getLabeledStringView(ViewGroup viewgroup, View view, int i, int j, String s)
    {
        String s1 = getString(j);
        return getLabeledStringView(viewgroup, view, i, s1.toUpperCase(), s, s1);
    }

    private View getLabeledStringView(ViewGroup viewgroup, View view, int i, String s, String s1, String s2)
    {
        if(view == null)
            view = mInflater.inflate(i, viewgroup, false);
        ((TextView)view.findViewById(0x1020014)).setText(s1);
        TextView textview = (TextView)view.findViewById(0x1020015);
        if(TextUtils.isEmpty(s))
        {
            textview.setVisibility(8);
        } else
        {
            textview.setText(s);
            textview.setVisibility(0);
        }
        setupContentDescription(view, s1, s2);
        return view;
    }

    private String getString(int i)
    {
        return getContext().getString(i);
    }

    private void initProfileLayout()
    {
        mProfileLayout = new ProfileLayout(this);
        mHeader = mProfileLayout.header;
        mDetails = mProfileLayout.details;
    }

    private static void setupContentDescription(View view, CharSequence charsequence, CharSequence charsequence1)
    {
        StringBuilder stringbuilder = new StringBuilder();
        if(charsequence1 != null && charsequence1.length() > 0)
        {
            stringbuilder.append(charsequence1);
            stringbuilder.append(" ");
        }
        if(charsequence != null && charsequence.length() > 0)
            stringbuilder.append(charsequence);
        view.setContentDescription(stringbuilder.toString());
    }

    private void updateGenericListSectionDividers(ViewGroup viewgroup)
    {
        int i = viewgroup.getChildCount();
        if(i != 0)
        {
            int j = 0;
            View view;
            do
            {
                int k = j + 1;
                view = viewgroup.getChildAt(j);
                if(k == i)
                    break;
                enableDivider(view, true);
                j = k;
            } while(true);
            enableDivider(view, false);
        }
    }

    public final void addAddress(String s, String s1)
    {
        View view = mInflater.inflate(R.layout.profile_item_multi_line_with_icon, mDetails.addresses, false);
        boolean flag;
        int i;
        if(mDetails.addresses.getChildCount() == 0)
            flag = true;
        else
            flag = false;
        if(flag)
            i = R.drawable.profile_address;
        else
            i = 0;
        bindDataView(view, i, s, s1);
        mDetails.addresses.addView(view);
        view.setId(R.id.address_content);
        view.setTag(s);
        view.setOnTouchListener(new ItemOnTouchListener());
        view.setOnClickListener(this);
    }

    public final void addCircleReview(GoogleReviewProto googlereviewproto)
    {
        addReviewToParent(googlereviewproto, mDetails.container.findViewById(R.id.circle_activity), 2);
    }

    public final void addEducationLocation(String s, String s1)
    {
        View view = getLabeledStringView(mDetails.educationSection, null, R.layout.profile_item_multi_line, s1, s, (new StringBuilder()).append(s).append(", ").append(s1).toString());
        ((ViewGroup)mDetails.educationSection.findViewById(R.id.content)).addView(view);
        mDetails.educationSectionLastLocation = view;
    }

    public final void addEmail(String s, String s1)
    {
        View view = mInflater.inflate(R.layout.profile_item_two_line_with_icon, mDetails.emails, false);
        boolean flag;
        int i;
        if(mDetails.emails.getChildCount() == 0)
            flag = true;
        else
            flag = false;
        if(flag)
            i = R.drawable.profile_email;
        else
            i = 0;
        bindDataView(view, i, s, s1);
        mDetails.emails.addView(view);
        view.setId(R.id.email_content);
        view.setTag(s);
        view.setOnTouchListener(new ItemOnTouchListener());
        view.setOnClickListener(this);
    }

    public final void addEmploymentLocation(String s, String s1)
    {
        View view = getLabeledStringView(mDetails.workSection, null, R.layout.profile_item_multi_line, s1, s, (new StringBuilder()).append(s).append(", ").append(s1).toString());
        ((ViewGroup)mDetails.workSection.findViewById(R.id.content)).addView(view);
        mDetails.workSectionLastLocation = view;
    }

    public final void addLink(String s, String s1, String s2, String s3)
    {
        View view = mInflater.inflate(R.layout.profile_item_link, mDetails.locations, false);
        if(mDetails.links.getChildCount() != 0)
            s3 = null;
        bindLinkView(view, s1, s2, s3);
        mDetails.links.addView(view);
        view.setId(R.id.link_content);
        view.setTag(s);
        view.setOnTouchListener(new ItemOnTouchListener());
        view.setOnClickListener(this);
    }

    public final void addLocalReview(GoogleReviewProto googlereviewproto)
    {
        addReviewToParent(googlereviewproto, mDetails.container.findViewById(R.id.local_reviews), 0);
    }

    public final void addLocation(String s, boolean flag)
    {
        View view = mInflater.inflate(R.layout.profile_item_location, mDetails.locations, false);
        mDetails.locations.addView(view);
        LocationItem locationitem = new LocationItem(s, flag);
        View view1 = view.findViewById(0x1020006);
        int i;
        if(locationitem.current)
            i = 0;
        else
            i = 4;
        view1.setVisibility(i);
        ((TextView)view.findViewById(0x1020014)).setText(locationitem.address);
        view.setContentDescription(locationitem.address);
        view.setTag(locationitem);
        view.setOnTouchListener(new ItemOnTouchListener());
        view.setOnClickListener(this);
    }

    public final void addPhoneNumber(String s, String s1, boolean flag)
    {
        View view = mInflater.inflate(R.layout.profile_item_phone, mDetails.phoneNumbers, false);
        boolean flag1;
        String s2;
        int i;
        View view1;
        View view2;
        if(mDetails.phoneNumbers.getChildCount() == 0)
            flag1 = true;
        else
            flag1 = false;
        s2 = PhoneNumberUtils.formatNumber(s);
        if(flag1)
            i = R.drawable.profile_phone;
        else
            i = 0;
        bindDataView(view, i, s2, s1);
        mDetails.phoneNumbers.addView(view);
        view.setId(R.id.phone_content);
        view.setTag(s);
        view.setOnTouchListener(new ItemOnTouchListener());
        view.setOnClickListener(this);
        view1 = view.findViewById(R.id.send_text_button);
        view2 = view.findViewById(R.id.vertical_divider);
        if(flag)
        {
            view1.setVisibility(0);
            view2.setVisibility(0);
            view1.setTag(s);
            view1.setOnClickListener(this);
        } else
        {
            view1.setVisibility(8);
            view2.setVisibility(8);
        }
    }

    public final void addYourReview(GoogleReviewProto googlereviewproto)
    {
        addReviewToParent(googlereviewproto, mDetails.container.findViewById(R.id.user_activity), 1);
    }

    public final void clearAddresses()
    {
        mDetails.addresses.removeAllViews();
    }

    public final void clearAllReviews()
    {
        ((LinearLayout)mDetails.container.findViewById(R.id.user_activity).findViewById(R.id.content)).removeAllViews();
        ((LinearLayout)mDetails.container.findViewById(R.id.circle_activity).findViewById(R.id.content)).removeAllViews();
        ((LinearLayout)mDetails.container.findViewById(R.id.local_reviews).findViewById(R.id.content)).removeAllViews();
    }

    public final void clearEducationLocations()
    {
        ((ViewGroup)mDetails.educationSection.findViewById(R.id.content)).removeAllViews();
        mDetails.educationSectionLastLocation = null;
        mDetails.educationSection.findViewById(R.id.no_items).setVisibility(8);
    }

    public final void clearEmails()
    {
        mDetails.emails.removeAllViews();
    }

    public final void clearEmploymentLocations()
    {
        ((ViewGroup)mDetails.workSection.findViewById(R.id.content)).removeAllViews();
        mDetails.workSectionLastLocation = null;
        mDetails.workSection.findViewById(R.id.no_items).setVisibility(8);
    }

    public final void clearLinks()
    {
        mDetails.links.removeAllViews();
    }

    public final void clearLocations()
    {
        mDetails.locations.removeAllViews();
        mDetails.locationsSection.findViewById(R.id.no_items).setVisibility(8);
    }

    public final void clearPhoneNumbers()
    {
        mDetails.phoneNumbers.removeAllViews();
    }

    public final void enableContactSection(boolean flag)
    {
        if(flag)
        {
            mDetails.contactSection.setVisibility(0);
            bindSectionHeader((SectionHeaderView)mDetails.contactSection.findViewById(R.id.header), R.string.profile_section_contact, false);
        } else
        {
            mDetails.contactSection.setVisibility(8);
        }
    }

    public final void enableEducationSection(boolean flag)
    {
        if(flag)
        {
            mDetails.educationSection.setVisibility(0);
            SectionHeaderView sectionheaderview = (SectionHeaderView)mDetails.educationSection.findViewById(R.id.header);
            bindSectionHeader(sectionheaderview, R.string.profile_section_education, mEditEnabled);
            if(mDetails.educationSectionLastLocation != null)
                enableDivider(mDetails.educationSectionLastLocation, false);
            if(mEditEnabled)
            {
                sectionheaderview.findViewById(R.id.edit).setVisibility(0);
                sectionheaderview.setOnClickListener(this);
                sectionheaderview.setTag(Integer.valueOf(1101));
                sectionheaderview.setBackgroundDrawable(mEducationBackground);
            }
        } else
        {
            mDetails.educationSection.setVisibility(8);
        }
    }

    public final void enableHompageSection(boolean flag)
    {
        View view = mDetails.container.findViewById(R.id.homepage);
        int i;
        if(flag)
            i = 0;
        else
            i = 8;
        view.setVisibility(i);
        bindSectionHeader((SectionHeaderView)view.findViewById(R.id.homepage_header), R.string.profile_section_links, false);
    }

    public final void enableLinksSection(boolean flag)
    {
        if(flag)
        {
            mDetails.linksSection.setVisibility(0);
            bindSectionHeader((SectionHeaderView)mDetails.linksSection.findViewById(R.id.header), R.string.profile_section_links, false);
        } else
        {
            mDetails.linksSection.setVisibility(8);
        }
    }

    public final void enableLocalDetailsSection(boolean flag)
    {
        View view = mDetails.container.findViewById(R.id.local_details);
        int i;
        if(flag)
            i = 0;
        else
            i = 8;
        view.setVisibility(i);
        if(flag)
            bindSectionHeader((SectionHeaderView)view.findViewById(R.id.local_details_header), R.string.profile_local_section_details, false);
    }

    public final void enableLocalEditorialReviewsSection(boolean flag)
    {
        View view = mDetails.container.findViewById(R.id.zagat);
        int i;
        if(flag)
            i = 0;
        else
            i = 8;
        view.setVisibility(i);
    }

    public final void enableLocalReviewsSection(boolean flag)
    {
        View view = mDetails.container.findViewById(R.id.local_reviews);
        if(flag)
        {
            view.setVisibility(0);
            bindSectionHeader((SectionHeaderView)view.findViewById(R.id.header), R.string.profile_local_section_reviews, false);
        } else
        {
            view.setVisibility(8);
        }
    }

    public final void enableLocalYourActivitySection(boolean flag)
    {
        View view = mDetails.container.findViewById(R.id.user_activity);
        if(flag)
        {
            view.setVisibility(0);
            bindSectionHeader((SectionHeaderView)view.findViewById(R.id.header), R.string.profile_local_section_your_activity, false);
        } else
        {
            view.setVisibility(8);
        }
    }

    public final void enableLocalYourCirclesActivitySection(boolean flag)
    {
        View view = mDetails.container.findViewById(R.id.circle_activity);
        if(flag)
        {
            view.setVisibility(0);
            bindSectionHeader((SectionHeaderView)view.findViewById(R.id.header), R.string.profile_local_section_activity_from_your_circles, false);
        } else
        {
            view.setVisibility(8);
        }
    }

    public final void enableLocationsSection(boolean flag)
    {
        if(flag)
        {
            mDetails.locationsSection.setVisibility(0);
            SectionHeaderView sectionheaderview = (SectionHeaderView)mDetails.locationsSection.findViewById(R.id.header);
            bindSectionHeader(sectionheaderview, R.string.profile_section_places, false);
            if(mEditEnabled)
            {
                sectionheaderview.findViewById(R.id.edit).setVisibility(0);
                sectionheaderview.setOnClickListener(this);
                sectionheaderview.setTag(Integer.valueOf(1102));
                sectionheaderview.setBackgroundDrawable(mLocationsBackground);
            }
        } else
        {
            mDetails.locationsSection.setVisibility(8);
        }
    }

    public final void enablePersonalSection(boolean flag)
    {
        if(flag)
        {
            mDetails.personalSection.setVisibility(0);
            bindSectionHeader((SectionHeaderView)mDetails.personalSection.findViewById(R.id.header), R.string.profile_section_personal, false);
        } else
        {
            mDetails.personalSection.setVisibility(8);
        }
    }

    public final void enableWorkSection(boolean flag)
    {
        if(flag)
        {
            mDetails.workSection.setVisibility(0);
            SectionHeaderView sectionheaderview = (SectionHeaderView)mDetails.workSection.findViewById(R.id.header);
            bindSectionHeader(sectionheaderview, R.string.profile_section_employment, mEditEnabled);
            if(mDetails.workSectionLastLocation != null)
                enableDivider(mDetails.workSectionLastLocation, false);
            if(mEditEnabled)
            {
                sectionheaderview.findViewById(R.id.edit).setVisibility(0);
                sectionheaderview.setOnClickListener(this);
                sectionheaderview.setTag(Integer.valueOf(1100));
                sectionheaderview.setBackgroundDrawable(sEmploymentBackground);
            }
        } else
        {
            mDetails.workSection.setVisibility(8);
        }
    }

    public final void init(boolean flag, boolean flag1)
    {
        if(mProfileLayout == null)
            initProfileLayout();
        mHeader.coverPhoto.setVisibility(0);
        mHeader.scrapbookAlbum.setVisibility(8);
        mHeader.expandArea.setOnClickListener(this);
        mHeader.circlesButton.setOnClickListener(this);
        mHeader.addToCirclesButton.setOnClickListener(this);
        mHeader.plusOneButton.setOnClickListener(this);
        mIsExpanded = flag;
        mEditEnabled = flag1;
    }

    public void onClick(View view) {
        int i;
        i = view.getId();
        if(i == R.id.expand) {
            if(mIsExpanded) {
                mDetails.container.setVisibility(8);
                mIsExpanded = false;
                bindExpandArea();
                view.setContentDescription(getString(R.string.expand_more_info_content_description));
            } else {
                mDetails.container.setVisibility(0);
                mIsExpanded = true;
                bindExpandArea();
                view.setContentDescription(getString(R.string.collapse_more_info_content_description));
            }
            requestLayout();
        }
        
        if(null == mOnClickListener) {
        	return;
        }
        
        if(i == R.id.avatar_image)
        {
            mOnClickListener.onAvatarClicked();
        } else if(i == R.id.cover_photo_image) {
            mOnClickListener.onCoverPhotoClicked(0);
        } else if(i == R.id.photo_1) {
            mOnClickListener.onCoverPhotoClicked(1);
        } else if(i == R.id.photo_2) {
            mOnClickListener.onCoverPhotoClicked(2);
        } else if(i == R.id.photo_3) {
            mOnClickListener.onCoverPhotoClicked(3);
        } else if(i == R.id.photo_4) {
            mOnClickListener.onCoverPhotoClicked(4);
        } else if(i == R.id.photo_5) {
            mOnClickListener.onCoverPhotoClicked(5);
        } else if(i == R.id.location) {
            LocationItem locationitem = (LocationItem)view.getTag();
            mOnClickListener.onLocationClicked(locationitem.address);
        } else if(i == R.id.email_content) {
            mOnClickListener.onEmailClicked((String)view.getTag());
        } else if(i == R.id.phone_content) {
            mOnClickListener.onPhoneNumberClicked((String)view.getTag());
        } else if(i == R.id.send_text_button) {
            mOnClickListener.onSendTextClicked((String)view.getTag());
        } else if(i == R.id.address_content) {
            mOnClickListener.onAddressClicked((String)view.getTag());
        } else if(i == R.id.link_content) {
            mOnClickListener.onLinkClicked((String)view.getTag());
        } else if(i == R.id.circles_button || i == R.id.add_to_circles_button) {
            mOnClickListener.onCirclesButtonClicked();
        } else if(i == R.id.map_button) {
            mOnClickListener.onLocalMapClicked((String)view.getTag());
        } else if(i == R.id.directions_button) {
            mOnClickListener.onLocalDirectionsClicked((String)view.getTag());
        } else if(i == R.id.call_button) {
            mOnClickListener.onLocalCallClicked((String)view.getTag());
        } else if(i == R.id.zagat_explanation) {
            mOnClickListener.onZagatExplanationClicked();
        } else if(i == R.id.local_review_item) {
            int j = ((Integer[])view.getTag())[0].intValue();
            int k = ((Integer[])view.getTag())[1].intValue();
            mOnClickListener.onLocalReviewClicked(j, k);
        } if(i == R.id.author_avatar) {
            AvatarView avatarview = (AvatarView)view;
            mOnClickListener.onReviewAuthorAvatarClicked(avatarview.getGaiaId());
        } if(i == R.id.plus_one) {
            mOnClickListener.onPlusOneClicked();
        }
        if(i == R.id.expand) {
        	mOnClickListener.onExpandClicked(mIsExpanded);
        	return;
        }
        if(i != R.id.header) {
        	return;
        }
        Integer integer = (Integer)view.getTag();
        if(integer == null) {
        	return;
        }
        switch(integer.intValue())
        {
        case 1100: 
            mOnClickListener.onEditEmploymentClicked();
            break;

        case 1101: 
            mOnClickListener.onEditEducationClicked();
            break;

        case 1102: 
            mOnClickListener.onEditPlacesLivedClicked();
            break;
        }
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        initProfileLayout();
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(50);
    }

    public void onRecycle()
    {
        mOnClickListener = null;
        if(mHeader != null)
        {
            mHeader.coverPhoto.onRecycle();
            mHeader.avatarImage.onRecycle();
            for(int i = 0; i < 5; i++)
                mHeader.scrapbookPhoto[i].onRecycle();

            mHeader.expandArea.setOnClickListener(null);
            mHeader.coverPhoto.setOnClickListener(null);
            mHeader.avatarImage.setOnClickListener(null);
            mHeader.circlesButton.setOnClickListener(null);
            mHeader.addToCirclesButton.setOnClickListener(null);
            mHeader.plusOneButton.setOnClickListener(null);
        }
        mHeader = null;
        mDetails = null;
        mProfileLayout = null;
    }

    public void setAddedByCount(Integer integer)
    {
        if(integer != null)
        {
            NumberFormat numberformat = NumberFormat.getIntegerInstance();
            Resources resources = getContext().getResources();
            int i = R.plurals.profile_added_by;
            int j = integer.intValue();
            Object aobj[] = new Object[1];
            aobj[0] = numberformat.format(integer);
            String s = resources.getQuantityString(i, j, aobj);
            mHeader.addedByCount.setText(s);
            mHeader.addedByCount.setVisibility(0);
        } else
        {
            mHeader.addedByCount.setVisibility(8);
        }
    }

    public void setAvatarToDefault(boolean flag)
    {
        mHeader.avatarImage.setResourceMissing(true);
        mHeader.avatarImage.setOnClickListener(this);
        enableAvatarChangePhotoIcon(flag);
    }

    public void setAvatarUrl(String s, boolean flag)
    {
        mHeader.avatarImage.setMediaRef(new MediaRef(s, MediaRef.MediaType.IMAGE));
        mHeader.avatarImage.setOnClickListener(this);
        enableAvatarChangePhotoIcon(flag);
    }

    public void setBirthday(String s) {
    	
    	if(s == null) {
    		 if(mDetails.birthday != null)
    	            mDetails.birthday.setVisibility(8);
    	} else {
    		if(mDetails.birthday == null)
                mDetails.birthday = mDetails.personalSection.findViewById(1001);
            View view = getLabeledStringView(mDetails.personalSection, mDetails.birthday, R.layout.profile_item_two_line, R.string.profile_item_birthday, s);
            if(mDetails.birthday == null)
            {
                mDetails.birthday = view;
                mDetails.birthday.setId(1001);
                mDetails.personalSection.addView(view);
            }
            mDetails.birthday.setVisibility(0);
    	}
    }

    public void setCircles(List arraylist)
    {
        mHeader.circlesButton.setVisibility(0);
        mHeader.circlesButton.setCircles(arraylist);
        mHeader.addToCirclesButton.setVisibility(8);
        mHeader.blockedText.setVisibility(8);
        mHeader.progressBar.setVisibility(8);
    }

    public void setCoverPhotoToDefault(boolean flag)
    {
        mHeader.coverPhoto.setScaleMode(0);
        mHeader.coverPhoto.setResourceMissing(true);
        enableCoverPhotoChangePhotoIcon(flag);
        if(flag)
            mHeader.coverPhoto.setOnClickListener(this);
    }

    public void setCoverPhotoUrl(String s, int i, boolean flag)
    {
        if(s == null)
        {
            setCoverPhotoToDefault(flag);
        } else
        {
            mHeader.coverPhoto.setMediaRef(new MediaRef(s, MediaRef.MediaType.IMAGE));
            mHeader.coverPhoto.setTopOffset(i);
            mHeader.coverPhoto.setOnClickListener(this);
            enableCoverPhotoChangePhotoIcon(flag);
        }
    }

    public void setDisplayPolicies(DisplayPolicies displaypolicies)
    {
        int i = 0;
        mPolicy = displaypolicies;
        View view1;
        if(mPolicy.hideButtons)
            mHeader.buttons.setVisibility(8);
        else
            mHeader.buttons.setVisibility(0);
        if(mPolicy.showDetailsAlways)
        {
            mHeader.expandArea.setVisibility(8);
            view1 = mDetails.container;
        } else
        {
            bindExpandArea();
            View view = mDetails.container;
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
        requestLayout();
    }

    public void setEducation(String s)
    {
        if(!TextUtils.isEmpty(s))
        {
            mHeader.education.container.setVisibility(0);
            mHeader.education.text.setText(s);
            if(mPolicy.showInfoIcons)
                mHeader.education.icon.setVisibility(0);
            else
                mHeader.education.icon.setVisibility(8);
        } else
        {
            mHeader.education.container.setVisibility(8);
        }
    }

    public void setEmployer(String s)
    {
        if(!TextUtils.isEmpty(s))
        {
            mHeader.employer.container.setVisibility(0);
            mHeader.employer.text.setText(s);
            if(mPolicy.showInfoIcons)
                mHeader.employer.icon.setVisibility(0);
            else
                mHeader.employer.icon.setVisibility(8);
        } else
        {
            mHeader.employer.container.setVisibility(8);
        }
    }

    public void setGender(String s) {
    	
    	if(s == null) {
    		if(mDetails.gender != null)
                mDetails.gender.setVisibility(8);
    	} else {
    		 if(mDetails.gender == null)
    	            mDetails.gender = mDetails.personalSection.findViewById(1000);
    	        View view = getLabeledStringView(mDetails.personalSection, mDetails.gender, R.layout.profile_item_two_line, R.string.profile_item_gender, s);
    	        if(mDetails.gender == null)
    	        {
    	            mDetails.gender = view;
    	            mDetails.gender.setId(1000);
    	            mDetails.personalSection.addView(view);
    	        }
    	        mDetails.gender.setVisibility(0);
    	}
    }

    public void setHomepage(String s, String s1, String s2) {
        View view = mDetails.container.findViewById(R.id.homepage).findViewById(R.id.link_content);
        bindLinkView(view, s1, s2, null);
        enableDivider(view, false);
        view.setTag(s);
        view.setOnTouchListener(new ItemOnTouchListener());
        view.setOnClickListener(this);
    }

    public void setIntroduction(String s)
    {
        if(!TextUtils.isEmpty(s))
        {
            mDetails.introduction.setVisibility(0);
            bindSectionHeader((SectionHeaderView)mDetails.introduction.findViewById(R.id.header), R.string.profile_section_introduction, false);
            bindIntroductionView(mDetails.introduction.findViewById(R.id.content), s);
        } else
        {
            mDetails.introduction.setVisibility(8);
        }
    }

    public void setIsExpanded(boolean flag)
    {
        mIsExpanded = flag;
        bindExpandArea();
    }

    public void setLocalActions(String s, String s1, String s2, String s3)
    {
        View view = mDetails.container.findViewById(R.id.local_actions);
        view.setVisibility(0);
        LocalActionsItem localactionsitem = new LocalActionsItem(s1, s, s2, s3);
        String s4 = localactionsitem.title;
        String s5 = localactionsitem.mapsCid;
        StringBuilder stringbuilder = (new StringBuilder("http://maps.google.com/maps?cid=")).append(Uri.encode(s5));
        if(s4 != null)
            stringbuilder.append("&q=").append(Uri.encode(s4));
        String s6 = stringbuilder.toString();
        String s7 = localactionsitem.title;
        String s8 = localactionsitem.address;
        StringBuilder stringbuilder1 = new StringBuilder("http://maps.google.com/maps?daddr=");
        if(s7 != null)
            stringbuilder1.append(Uri.encode(s7)).append(", ");
        stringbuilder1.append(Uri.encode(s8));
        String s9 = stringbuilder1.toString();
        View view1 = view.findViewById(R.id.map_button);
        View view2 = view.findViewById(R.id.directions_button);
        View view3 = view.findViewById(R.id.vertical_divider_call);
        View view4 = view.findViewById(R.id.call_button);
        view1.setTag(s6);
        view1.setOnClickListener(this);
        view2.setTag(s9);
        view2.setOnClickListener(this);
        if(!TextUtils.isEmpty(localactionsitem.phone))
        {
            view3.setVisibility(0);
            view4.setVisibility(0);
            view4.setTag(localactionsitem.phone);
            view4.setOnClickListener(this);
        } else
        {
            view3.setVisibility(8);
            view4.setVisibility(8);
        }
    }

    public void setLocalDetails(List list, String s, String s1, String s2)
    {
        View view = mDetails.container.findViewById(R.id.local_details).findViewById(R.id.local_details_content);
        LocalDetailsItem localdetailsitem = new LocalDetailsItem(list, s, s1, s2);
        View view1 = view.findViewById(R.id.known_for_terms_row);
        View view2 = view.findViewById(R.id.phone_row);
        View view3 = view.findViewById(R.id.open_hours_row);
        StringBuilder stringbuilder = new StringBuilder();
        if(!localdetailsitem.knownForTerms.isEmpty())
        {
            Iterator iterator = localdetailsitem.knownForTerms.iterator();
            for(boolean flag = true; iterator.hasNext(); flag = flag)
            {
                String s4 = (String)iterator.next();
                if(TextUtils.isEmpty(s4))
                    continue;
                if(!flag)
                    stringbuilder.append(" \267 ");
                flag = false;
                stringbuilder.append(s4);
            }

        }
        String s3;
        if(stringbuilder.length() > 0)
        {
            view1.setVisibility(0);
            ((TextView)view1.findViewById(R.id.known_for_terms_value)).setText(stringbuilder);
        } else
        {
            view1.setVisibility(8);
        }
        if(!TextUtils.isEmpty(localdetailsitem.phone))
        {
            view2.setVisibility(0);
            ((TextView)view2.findViewById(R.id.phone_value)).setText(localdetailsitem.phone);
        } else
        {
            view2.setVisibility(8);
        }
        if(!TextUtils.isEmpty(localdetailsitem.openingHoursFull))
            s3 = localdetailsitem.openingHoursFull;
        else
            s3 = localdetailsitem.openingHoursSummary;
        if(!TextUtils.isEmpty(s3))
        {
            view3.setVisibility(0);
            ((TextView)view3.findViewById(R.id.open_hours_value)).setText(s3);
        } else
        {
            view3.setVisibility(8);
        }
    }

    public void setLocalEditorialReviews(ZagatAspectRatingsProto zagataspectratingsproto, String s, String s1, String s2, int i)
    {
        View view = mDetails.container.findViewById(R.id.zagat);
        LocalEditorialReviewItem localeditorialreviewitem = new LocalEditorialReviewItem(zagataspectratingsproto, s, s1, s2, i);
        boolean flag = "ZAGAT_OFFICIAL".equals(localeditorialreviewitem.scores.source);
        int j = localeditorialreviewitem.scores.aspectRating.size();
        View view1 = view.findViewById(R.id.zagat_logo);
        View view2 = view.findViewById(R.id.user_rated_logo);
        View aview[] = new View[4];
        aview[0] = view.findViewById(R.id.rating_item_1);
        aview[1] = view.findViewById(R.id.rating_item_2);
        aview[2] = view.findViewById(R.id.rating_item_3);
        aview[3] = view.findViewById(R.id.rating_item_4);
        TextView textview = (TextView)view.findViewById(R.id.zagat_editorial_text);
        TextView textview1 = (TextView)view.findViewById(R.id.review_count_and_price);
        int k;
        if(flag)
        {
            view1.setVisibility(0);
            view2.setVisibility(8);
        } else
        {
            view1.setVisibility(8);
            view2.setVisibility(0);
        }
        k = 0;
        while(k < 4) 
        {
            if(k < j)
            {
                aview[k].setVisibility(0);
                ((TextView)aview[k].findViewById(R.id.rating_label)).setText(((ZagatAspectRatingProto)localeditorialreviewitem.scores.aspectRating.get(k)).labelDisplay);
                ((TextView)aview[k].findViewById(R.id.rating_value)).setText(((ZagatAspectRatingProto)localeditorialreviewitem.scores.aspectRating.get(k)).valueDisplay);
            } else
            if(k == j && flag && localeditorialreviewitem.priceLabel != null && localeditorialreviewitem.priceValue != null)
            {
                aview[k].setVisibility(0);
                ((TextView)aview[k].findViewById(R.id.rating_label)).setText(localeditorialreviewitem.priceLabel);
                ((TextView)aview[k].findViewById(R.id.rating_value)).setText(localeditorialreviewitem.priceValue);
            } else
            {
                aview[k].setVisibility(8);
            }
            k++;
        }
        boolean flag1;
        if(localeditorialreviewitem.priceValue != null && localeditorialreviewitem.priceLabel == null)
            flag1 = true;
        else
            flag1 = false;
        if(flag && flag1)
        {
            textview1.setVisibility(0);
            textview1.setText(localeditorialreviewitem.priceValue);
        } else
        if(!flag && (localeditorialreviewitem.reviewCount > 0 || flag1))
        {
            StringBuilder stringbuilder = new StringBuilder();
            if(localeditorialreviewitem.reviewCount > 0)
            {
                Resources resources = getContext().getResources();
                int l = R.plurals.profile_local_review_count;
                int i1 = localeditorialreviewitem.reviewCount;
                Object aobj[] = new Object[1];
                aobj[0] = Integer.valueOf(localeditorialreviewitem.reviewCount);
                stringbuilder.append(resources.getQuantityString(l, i1, aobj));
            }
            if(flag1)
            {
                if(stringbuilder.length() > 0)
                    stringbuilder.append(" \267 ");
                stringbuilder.append(localeditorialreviewitem.priceValue);
            }
            textview1.setVisibility(0);
            textview1.setText(stringbuilder.toString());
        } else
        {
            textview1.setVisibility(8);
        }
        if(!TextUtils.isEmpty(localeditorialreviewitem.editorialText))
        {
            textview.setVisibility(0);
            SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder();
            SpannableUtils.appendWithSpan(spannablestringbuilder, getString(R.string.profile_local_from_zagat), new TextAppearanceSpan(getContext(), R.style.ProfileLocalEditorialRating_FromZagat));
            spannablestringbuilder.append(" ").append(localeditorialreviewitem.editorialText);
            textview.setText(spannablestringbuilder);
        } else
        {
            textview.setVisibility(8);
        }
        view.findViewById(R.id.zagat_explanation).setOnClickListener(this);
    }

    public void setLocation(String s, boolean flag)
    {
        if(!TextUtils.isEmpty(s))
        {
            mHeader.location.container.setVisibility(0);
            mHeader.location.text.setText(s);
            if(flag)
            {
                mHeader.location.text.setSingleLine(true);
            } else
            {
                mHeader.location.text.setSingleLine(false);
                mHeader.location.text.setMaxLines(2);
                mHeader.location.text.setEllipsize(android.text.TextUtils.TruncateAt.MARQUEE);
            }
            if(mPolicy.showInfoIcons)
                mHeader.location.icon.setVisibility(0);
            else
                mHeader.location.icon.setVisibility(8);
        } else
        {
            mHeader.location.container.setVisibility(8);
        }
    }

    public void setLocationUrl(String s)
    {
        ImageResourceView imageresourceview = mDetails.map;
        if(s != null)
        {
            imageresourceview.setMediaRef(new MediaRef(s, MediaRef.MediaType.IMAGE));
            imageresourceview.setVisibility(0);
        } else
        {
            imageresourceview.setVisibility(8);
        }
    }

    public void setName(String s, String s1, String s2)
    {
        byte byte0 = 8;
        boolean flag;
        ConstrainedTextView constrainedtextview;
        int i;
        TextView textview;
        byte byte1;
        TextView textview1;
        if(TextUtils.isEmpty(s1) && TextUtils.isEmpty(s2))
            flag = true;
        else
            flag = false;
        if(flag)
            mHeader.fullName.setText(s);
        else
        if(s != null && s2 != null && s.startsWith(s2))
        {
            mHeader.givenName.setText(s2);
            mHeader.familyName.setText(s1);
        } else
        {
            mHeader.givenName.setText(s1);
            mHeader.familyName.setText(s2);
        }
        constrainedtextview = mHeader.fullName;
        if(flag)
            i = 0;
        else
            i = byte0;
        constrainedtextview.setVisibility(i);
        textview = mHeader.givenName;
        if(flag)
            byte1 = byte0;
        else
            byte1 = 0;
        textview.setVisibility(byte1);
        textview1 = mHeader.familyName;
        if(!flag)
            byte0 = 0;
        textview1.setVisibility(byte0);
    }

    public void setNoEducationLocations()
    {
        clearEducationLocations();
        mDetails.educationSection.findViewById(R.id.no_items).setVisibility(0);
    }

    public void setNoEmploymentLocations()
    {
        clearEmploymentLocations();
        mDetails.workSection.findViewById(R.id.no_items).setVisibility(0);
    }

    public void setNoLocations()
    {
        clearLocations();
        mDetails.locationsSection.findViewById(R.id.no_items).setVisibility(0);
    }

    public void setOnClickListener(OnClickListener onclicklistener)
    {
        mOnClickListener = onclicklistener;
    }

    public void setPlusOneData(String s, boolean flag)
    {
        Button button = mHeader.plusOneButton;
        if(s != null)
        {
            button.setText(s);
            if(flag)
            {
                button.setTextColor(sPlusOnedByMeTextColor);
                button.setBackgroundResource(R.drawable.plusone_by_me_button);
            } else
            {
                button.setTextColor(sPlusOneStandardTextColor);
                button.setBackgroundResource(R.drawable.plusone_button);
            }
            button.setVisibility(0);
        } else
        {
            button.setVisibility(8);
        }
    }

    public void setScrapbookAlbumUrls(Long long1, String as[], boolean flag)
    {
        mHeader.coverPhoto.setVisibility(8);
        mHeader.scrapbookAlbum.setVisibility(0);
        mHeader.scrapbookAlbum.setTag(long1);
        int i = Math.min(as.length, 5);
        for(int j = 0; j < i; j++)
        {
            mHeader.scrapbookPhoto[j].setMediaRef(new MediaRef(as[j], MediaRef.MediaType.IMAGE));
            mHeader.scrapbookPhoto[j].setOnClickListener(this);
        }

        enableCoverPhotoChangePhotoIcon(flag);
    }

    public void setTagLine(String s)
    {
        if(!TextUtils.isEmpty(s))
        {
            mDetails.tagLine.setVisibility(0);
            bindSectionHeader((SectionHeaderView)mDetails.tagLine.findViewById(R.id.header), R.string.profile_section_tagline, false);
            bindIntroductionView(mDetails.tagLine.findViewById(R.id.content), s);
        } else
        {
            mDetails.tagLine.setVisibility(8);
        }
    }

    public final boolean shouldHighlightOnPress()
    {
        return false;
    }

    public final void showAddToCircles(boolean flag)
    {
        mHeader.circlesButton.setVisibility(8);
        mHeader.addToCirclesButton.setVisibility(0);
        if(flag)
            mHeader.addToCirclesButton.setText(getString(R.string.follow));
        else
            mHeader.addToCirclesButton.setText(getString(R.string.add_to_circles));
        mHeader.blockedText.setVisibility(8);
        mHeader.progressBar.setVisibility(8);
    }

    public final void showBlocked()
    {
        mHeader.circlesButton.setVisibility(8);
        mHeader.addToCirclesButton.setVisibility(8);
        mHeader.blockedText.setVisibility(0);
        mHeader.progressBar.setVisibility(8);
    }

    public final void showError(boolean flag, String s)
    {
        if(flag)
        {
            mHeader.container.setVisibility(8);
            mDetails.container.setVisibility(8);
            mProfileLayout.error.setVisibility(0);
            mProfileLayout.error.setText(s);
        } else
        {
            mHeader.container.setVisibility(0);
            mProfileLayout.error.setVisibility(8);
        }
    }

    public final void showNone()
    {
        mHeader.circlesButton.setVisibility(8);
        mHeader.addToCirclesButton.setVisibility(8);
        mHeader.blockedText.setVisibility(8);
        mHeader.progressBar.setVisibility(8);
    }

    public final void showProgress()
    {
        mHeader.circlesButton.setVisibility(8);
        mHeader.addToCirclesButton.setVisibility(8);
        mHeader.blockedText.setVisibility(8);
        mHeader.progressBar.setVisibility(0);
    }

    public final void updateContactSectionDividers()
    {
        int i = mDetails.addresses.getChildCount();
        View view = null;
        if(i > 0)
            view = mDetails.addresses.getChildAt(i - 1);
        if(view == null)
        {
            int k = mDetails.phoneNumbers.getChildCount();
            if(k > 0)
                view = mDetails.phoneNumbers.getChildAt(k - 1);
        }
        if(view == null)
        {
            int j = mDetails.emails.getChildCount();
            if(j > 0)
                view = mDetails.emails.getChildAt(j - 1);
        }
        if(view != null)
            enableDivider(view, false);
    }

    public final void updateLinksSectionDividers()
    {
        updateGenericListSectionDividers(mDetails.links);
    }

    public final void updateLocationsSectionDividers()
    {
        updateGenericListSectionDividers(mDetails.locations);
    }

    public final void updatePersonalSectionDividers()
    {
        View view = mDetails.gender;
        View view1 = null;
        if(view != null)
        {
            int i = mDetails.gender.getVisibility();
            view1 = null;
            if(i != 8)
            {
                enableDivider(mDetails.gender, true);
                view1 = mDetails.gender;
            }
        }
        if(mDetails.birthday != null && mDetails.birthday.getVisibility() != 8)
        {
            enableDivider(mDetails.birthday, true);
            view1 = mDetails.birthday;
        }
        if(view1 != null)
            enableDivider(view1, false);
    }

}
