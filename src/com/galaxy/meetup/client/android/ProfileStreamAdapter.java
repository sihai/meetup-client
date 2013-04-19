/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.animation.LayoutTransition;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsLocalPageData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.controller.ComposeBarController;
import com.galaxy.meetup.client.android.ui.fragments.CircleNameResolver;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.ItemClickListener;
import com.galaxy.meetup.client.android.ui.view.ProfileAboutView;
import com.galaxy.meetup.client.android.ui.view.StreamCardView;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.AuthorityPageProto;
import com.galaxy.meetup.server.client.domain.CommonContent;
import com.galaxy.meetup.server.client.domain.Contacts;
import com.galaxy.meetup.server.client.domain.DataPlusOne;
import com.galaxy.meetup.server.client.domain.DateInfo;
import com.galaxy.meetup.server.client.domain.Education;
import com.galaxy.meetup.server.client.domain.Educations;
import com.galaxy.meetup.server.client.domain.Employment;
import com.galaxy.meetup.server.client.domain.Employments;
import com.galaxy.meetup.server.client.domain.GoogleReviewProto;
import com.galaxy.meetup.server.client.domain.Locations;
import com.galaxy.meetup.server.client.domain.PlacePageLink;
import com.galaxy.meetup.server.client.domain.PlacePagePhoneNumber;
import com.galaxy.meetup.server.client.domain.ProfilesLink;
import com.galaxy.meetup.server.client.domain.ScrapBook;
import com.galaxy.meetup.server.client.domain.ScrapBookEntry;
import com.galaxy.meetup.server.client.domain.SimpleProfile;
import com.galaxy.meetup.server.client.domain.TaggedAddress;
import com.galaxy.meetup.server.client.domain.TaggedEmail;
import com.galaxy.meetup.server.client.domain.TaggedPhone;
import com.galaxy.meetup.server.client.domain.User;
import com.galaxy.meetup.server.client.domain.ZagatAspectRatingsProto;

/**
 * 
 * @author sihai
 *
 */
public class ProfileStreamAdapter extends StreamAdapter {

	private boolean mBlockRequestPending;
    CircleNameResolver mCircleNameResolver;
    List mCircleNames;
    private EsPeopleData.ProfileAndContactData mData;
    private String mErrorText;
    String mFamilyName;
    String mFullName;
    String mGender;
    String mGivenName;
    boolean mHasCoverPhotoUpgrade;
    boolean mHasProfile;
    boolean mIsBlocked;
    private boolean mIsEditEnabled;
    private boolean mIsLocalPlusPage;
    boolean mIsMuted;
    boolean mIsMyProfile;
    private boolean mIsPlusPage;
    boolean mIsSmsIntentRegistered;
    private boolean mIsUnclaimedLocalPlusPage;
    private String mPackedCircleIds;
    private String mPersonId;
    boolean mPlusOneByMe;
    int mPlusOnes;
    private SimpleProfile mProfile;
    boolean mProfileLoadFailed;
    private ProfileAboutView.OnClickListener mProfileViewOnClickListener;
    String mScrapbookAlbumId;
    String mScrapbookCoverPhotoId;
    int mScrapbookCoverPhotoOffset;
    String mScrapbookCoverPhotoOwnerType;
    String mScrapbookCoverPhotoUrl;
    String mScrapbookLayout;
    boolean mShowAddToCircles;
    boolean mShowBlocked;
    boolean mShowCircles;
    boolean mShowProgress;
    private boolean mViewIsExpanded;
    private boolean mViewingAsPlusPage;
    
    public ProfileStreamAdapter(Context context, ColumnGridView columngridview, EsAccount esaccount, View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamAdapter.ViewUseListener viewuselistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener, ComposeBarController composebarcontroller)
    {
        super(context, columngridview, esaccount, onclicklistener, itemclicklistener, viewuselistener, streamplusbarclicklistener, streammediaclicklistener, null);
        mPlusOnes = -1;
    }
    
    private void addDateInfo(DateInfo dateinfo, StringBuilder stringbuilder) {
        boolean flag;
        boolean flag2;
        flag = true;
        boolean flag1;
        if(dateinfo != null)
            flag1 = flag;
        else
            flag1 = false;
        if(flag1 && dateinfo.start != null && PrimitiveUtils.safeInt(dateinfo.start.year) != 0)
            flag2 = flag;
        else
            flag2 = false;
        if(!flag1 || dateinfo.end == null || PrimitiveUtils.safeInt(dateinfo.end.year) == 0)
            flag = false;
        if(flag2 || flag) {
        	if(stringbuilder.length() > 0)
                stringbuilder.append(", ");
            boolean flag3 = PrimitiveUtils.safeBoolean(dateinfo.current);
            if(flag2 && (flag || flag3))
            {
                stringbuilder.append(dateinfo.start.year);
                stringbuilder.append(" - ");
                if(flag3)
                    stringbuilder.append(getString(R.string.profile_end_date_for_current));
                else
                    stringbuilder.append(dateinfo.end.year);
            } else
            if(flag3)
                stringbuilder.append(getString(R.string.profile_end_date_for_current));
            else
            if(flag2)
                stringbuilder.append(dateinfo.start.year);
            else
            if(flag)
                stringbuilder.append(dateinfo.end.year); 
        } 
        return;
    }

    private void bindProfileAboutView(ProfileAboutView profileaboutview) {
    	
    	if(null == mData) {
    		return;
    	}
    	
    	String s;
        String s1;
        String s5;
        boolean flag;
        String s6;
        boolean flag1;
        if(mHasCoverPhotoUpgrade)
            profileaboutview.setCoverPhotoUrl(mScrapbookCoverPhotoUrl, mScrapbookCoverPhotoOffset, mIsMyProfile);
        else if(mProfile.content.scrapbook != null && mProfile.content.scrapbook.albumId != null && mProfile.content.scrapbook.plusiEntry != null) {
            int l1 = mProfile.content.scrapbook.plusiEntry.size();
            String as[] = new String[l1];
            for(int i2 = 0; i2 < l1; i2++)
                as[i2] = ((ScrapBookEntry)mProfile.content.scrapbook.plusiEntry.get(i2)).url;

            profileaboutview.setScrapbookAlbumUrls(Long.valueOf(Long.parseLong(mProfile.content.scrapbook.albumId)), as, mIsMyProfile);
        } else {
            profileaboutview.setCoverPhotoToDefault(mIsMyProfile);
        }
        s = mProfile.content.photoUrl;
        if(!TextUtils.isEmpty(s))
            profileaboutview.setAvatarUrl(s, mIsMyProfile);
        else
            profileaboutview.setAvatarToDefault(mIsMyProfile);
        profileaboutview.setName(mFullName, mGivenName, mFamilyName);
        if(mProfile.config != null && mProfile.config.incomingConnections != null && mProfile.config.incomingConnections.value != null)
            profileaboutview.setAddedByCount(mProfile.config.incomingConnections.value);
        else
            profileaboutview.setAddedByCount(null);
        if(mIsLocalPlusPage)
        {
            s1 = EsLocalPageData.getFullAddress(mProfile);
            profileaboutview.setLocation(s1, false);
        } else
        {
            User user = mProfile.user;
            s1 = null;
            if(user != null)
            {
                Employments employments = mProfile.user.employments;
                String s2 = null;
                if(employments != null)
                {
                    List list5 = mProfile.user.employments.employment;
                    s2 = null;
                    if(list5 != null)
                    {
                        Employment employment1 = (Employment)mProfile.user.employments.employment.get(0);
                        s2 = null;
                        if(employment1 != null)
                            s2 = employment1.employer;
                    }
                }
                profileaboutview.setEmployer(s2);
                Locations locations = mProfile.user.locations;
                String s3 = null;
                if(locations != null)
                    s3 = mProfile.user.locations.currentLocation;
                profileaboutview.setLocation(s3, true);
                Educations educations = mProfile.user.educations;
                String s4 = null;
                if(educations != null)
                {
                    List list4 = mProfile.user.educations.education;
                    s4 = null;
                    if(list4 != null)
                    {
                        Education education1 = (Education)mProfile.user.educations.education.get(0);
                        s4 = null;
                        if(education1 != null)
                            s4 = education1.school;
                    }
                }
                profileaboutview.setEducation(s4);
                s1 = null;
            }
        }
        if(mIsPlusPage && mPlusOnes != -1)
        {
            int k1 = R.string.stream_plus_one_count_with_plus;
            Object aobj1[] = new Object[1];
            aobj1[0] = Integer.valueOf(Math.max(mPlusOnes, 1));
            profileaboutview.setPlusOneData(getString(k1, aobj1), mPlusOneByMe);
        } else
        {
            profileaboutview.setPlusOneData(null, false);
        }
        if(mProfile.content != null && mProfile.content.tagLine != null)
            s5 = mProfile.content.tagLine.value;
        else
            s5 = null;
        if(!TextUtils.isEmpty(s5))
            profileaboutview.setTagLine(s5);
        else
            profileaboutview.setTagLine(null);
        flag = mHasProfile;
        s6 = null;
        if(!flag)
        {
            int j1 = R.string.profile_not_on_google_plus;
            Object aobj[] = new Object[1];
            aobj[0] = mFullName;
            s6 = getString(j1, aobj);
        }
        if(s6 == null && mProfile.content != null && mProfile.content.introduction != null)
            s6 = mProfile.content.introduction.value.trim();
        if(!TextUtils.isEmpty(s6))
            profileaboutview.setIntroduction(s6);
        if(mProfile.content != null && mProfile.content.links != null && mProfile.content.links.link != null && !mProfile.content.links.link.isEmpty())
            flag1 = true;
        else
            flag1 = false;
        if(mIsLocalPlusPage)
        {
            SimpleProfile simpleprofile;
            String s15;
            boolean flag19;
            SimpleProfile simpleprofile1;
            List list2;
            boolean flag20;
            boolean flag21;
            AuthorityPageProto authoritypageproto;
            PlacePageLink placepagelink;
            boolean flag22;
            boolean flag23;
            SimpleProfile simpleprofile4 = mProfile;
            String s16;
            ZagatAspectRatingsProto zagataspectratingsproto;
            String s17;
            String s18;
            boolean flag24;
            GoogleReviewProto googlereviewproto;
            Iterator iterator5;
            String s20;
            String s21;
            SimpleProfile simpleprofile3;
            Object obj;
            if(mIsUnclaimedLocalPlusPage)
            {
                if(simpleprofile4.content == null)
                    obj = null;
                else
                    obj = simpleprofile4.content.photoUrl;
                if(!TextUtils.isEmpty(((CharSequence) (obj))))
                    profileaboutview.setAvatarUrl(((String) (obj)), false);
            }
            simpleprofile = mProfile;
            if(simpleprofile.page.localInfo.paper.phone == null)
                s15 = null;
            else
            if(simpleprofile.page.localInfo.paper.phone.phoneNumber.size() == 0)
                s15 = null;
            else
                s15 = ((PlacePagePhoneNumber)simpleprofile.page.localInfo.paper.phone.phoneNumber.get(0)).formattedPhone;
            s16 = EsLocalPageData.getCid(mProfile);
            profileaboutview.setLocalActions(mFullName, s15, s16, s1);
            zagataspectratingsproto = mProfile.page.localInfo.paper.zagatAspectRatings;
            if(zagataspectratingsproto != null)
                flag19 = true;
            else
                flag19 = false;
            profileaboutview.enableLocalEditorialReviewsSection(flag19);
            if(zagataspectratingsproto != null)
            {
                SimpleProfile simpleprofile2 = mProfile;
                String s19;
                int i1;
                if(simpleprofile2.page.localInfo.paper.zagatEditorialReview == null)
                    s19 = null;
                else
                    s19 = simpleprofile2.page.localInfo.paper.zagatEditorialReview.text;
                s20 = EsLocalPageData.getPriceLabel(mProfile);
                s21 = EsLocalPageData.getPriceValue(mProfile);
                simpleprofile3 = mProfile;
                if(simpleprofile3.page.localInfo.paper.reviewsHeadline != null && simpleprofile3.page.localInfo.paper.reviewsHeadline.aggregatedReviews != null)
                    i1 = simpleprofile3.page.localInfo.paper.reviewsHeadline.aggregatedReviews.numReviews.intValue();
                else
                    i1 = 0;
                profileaboutview.setLocalEditorialReviews(zagataspectratingsproto, s19, s20, s21, i1);
            }
            simpleprofile1 = mProfile;
            if(simpleprofile1.page.localInfo.paper.knownForTerms == null || simpleprofile1.page.localInfo.paper.knownForTerms.term == null)
                list2 = Collections.emptyList();
            else
                list2 = simpleprofile1.page.localInfo.paper.knownForTerms.term;
            s17 = EsLocalPageData.getOpeningHoursSummary(mProfile);
            s18 = EsLocalPageData.getOpeningHoursFull(mProfile);
            if(!TextUtils.isEmpty(s17) || !TextUtils.isEmpty(s18))
                flag20 = true;
            else
                flag20 = false;
            if(!TextUtils.isEmpty(s15) || list2.size() > 0 || flag20)
                flag21 = true;
            else
                flag21 = false;
            profileaboutview.enableLocalDetailsSection(flag21);
            if(flag21)
                profileaboutview.setLocalDetails(list2, s15, s17, s18);
            authoritypageproto = mProfile.page.localInfo.paper.authorityPage;
            if(authoritypageproto == null)
                placepagelink = null;
            else
                placepagelink = authoritypageproto.authorityLink;
            if(placepagelink != null && !TextUtils.isEmpty(placepagelink.url))
                flag22 = true;
            else
                flag22 = false;
            if(flag22 && !flag1)
                flag23 = true;
            else
                flag23 = false;
            profileaboutview.enableHompageSection(flag23);
            if(flag23)
                profileaboutview.setHomepage(placepagelink.url, placepagelink.text, (new StringBuilder("https://www.google.com/s2/u/0/favicons?domain=")).append(Uri.parse(placepagelink.url).getHost()).toString());
            flag24 = EsLocalPageData.hasYourActivity(mProfile);
            profileaboutview.clearAllReviews();
            profileaboutview.enableLocalYourActivitySection(flag24);
            googlereviewproto = EsLocalPageData.getYourReview(mProfile);
            if(googlereviewproto != null)
                profileaboutview.addYourReview(googlereviewproto);
            profileaboutview.enableLocalYourCirclesActivitySection(EsLocalPageData.hasCircleActivity(mProfile));
            for(iterator5 = EsLocalPageData.getCircleReviews(mProfile).iterator(); iterator5.hasNext(); profileaboutview.addCircleReview((GoogleReviewProto)iterator5.next()));
            List list3 = EsLocalPageData.getReviews(mProfile);
            boolean flag25;
            if(!list3.isEmpty())
                flag25 = true;
            else
                flag25 = false;
            profileaboutview.enableLocalReviewsSection(flag25);
            for(Iterator iterator6 = list3.iterator(); iterator6.hasNext(); profileaboutview.addLocalReview((GoogleReviewProto)iterator6.next()));
        }
        Contacts contacts;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        if(mProfile.content == null)
            contacts = null;
        else
            contacts = mProfile.content.contacts;
        flag2 = false;
        flag3 = false;
        flag4 = false;
        Iterator iterator4;
        TaggedEmail taggedemail;
        boolean flag18;
        String s14;
        if(contacts != null)
        {
            if(contacts.email != null && !contacts.email.isEmpty())
                flag3 = true;
            else
                flag3 = false;
            if(contacts.phone != null && !contacts.phone.isEmpty())
                flag4 = true;
            else
                flag4 = false;
            if(contacts.address != null && !contacts.address.isEmpty())
                flag2 = true;
            else
                flag2 = false;
        }
        if(flag3 || flag4 || flag2)
            flag5 = true;
        else
            flag5 = false;
        profileaboutview.enableContactSection(flag5);
        profileaboutview.clearEmails();
        profileaboutview.clearPhoneNumbers();
        profileaboutview.clearAddresses();
        if(flag3)
            for(iterator4 = contacts.email.iterator(); iterator4.hasNext(); profileaboutview.addEmail(taggedemail.value, s14))
            {
                taggedemail = (TaggedEmail)iterator4.next();
                flag18 = mIsPlusPage;
                s14 = null;
                if(!flag18)
                    s14 = EsPeopleData.getStringForEmailType(mContext, taggedemail.tag);
                if(s14 == null)
                    s14 = getString(R.string.profile_item_email);
            }

        if(flag4)
        {
            Iterator iterator3 = contacts.phone.iterator();
            while(iterator3.hasNext()) 
            {
                TaggedPhone taggedphone = (TaggedPhone)iterator3.next();
                String s12;
                String s13;
                boolean flag17;
                if(mIsPlusPage)
                    s12 = EsPeopleData.getStringForPlusPagePhoneType(mContext, taggedphone.tag);
                else
                    s12 = EsPeopleData.getStringForPhoneType(mContext, taggedphone.tag);
                if(s12 == null)
                    s12 = getString(R.string.profile_item_phone);
                s13 = taggedphone.value;
                flag17 = mIsSmsIntentRegistered;
                profileaboutview.addPhoneNumber(s13, s12, flag17);
            }
        }
        if(flag2)
        {
            TaggedAddress taggedaddress;
            String s11;
            for(Iterator iterator2 = contacts.address.iterator(); iterator2.hasNext(); profileaboutview.addAddress(taggedaddress.value, s11))
            {
                taggedaddress = (TaggedAddress)iterator2.next();
                boolean flag16 = mIsPlusPage;
                s11 = null;
                if(!flag16)
                    s11 = EsPeopleData.getStringForAddress(mContext, taggedaddress.tag);
                if(s11 == null)
                    s11 = getString(R.string.profile_item_address);
            }

        }
        profileaboutview.updateContactSectionDividers();
        boolean flag6;
        String s10 = "";
        List list1;
        int k;
        int l;
        Employment employment;
        StringBuilder stringbuilder1;
        if(!mIsPlusPage)
        {
            boolean flag13;
            boolean flag14;
            boolean flag15;
            if(mGender != null && !"UNKNOWN".equals(mGender) && !"OTHER".equals(mGender))
                flag13 = true;
            else
                flag13 = false;
            if(mProfile.user != null && mProfile.user.birthday != null && !TextUtils.isEmpty(mProfile.user.birthday.value))
                flag14 = true;
            else
                flag14 = false;
            if(flag13 || flag14)
                flag15 = true;
            else
                flag15 = false;
            profileaboutview.enablePersonalSection(flag15);
            if(flag13)
            {
                if("MALE".equals(mGender))
                    s10 = getString(R.string.profile_item_gender_male);
                else
                if("FEMALE".equals(mGender))
                    s10 = getString(R.string.profile_item_gender_female);
                profileaboutview.setGender(s10);
            } else
            {
                profileaboutview.setGender(null);
            }
            if(flag14)
                profileaboutview.setBirthday(mProfile.user.birthday.value);
            else
                profileaboutview.setBirthday(null);
            profileaboutview.updatePersonalSectionDividers();
        }
        if(mProfile.user != null && mProfile.user.employments != null && mProfile.user.employments.employment != null && mProfile.user.employments.employment.size() > 0)
            flag6 = true;
        else
            flag6 = false;
        profileaboutview.clearEmploymentLocations();
        if(flag6)
        {
            list1 = mProfile.user.employments.employment;
            k = list1.size();
            for(l = 0; l < k; l++)
            {
                employment = (Employment)list1.get(l);
                stringbuilder1 = new StringBuilder();
                if(!TextUtils.isEmpty(employment.title))
                    stringbuilder1.append(employment.title);
                addDateInfo(employment.dateInfo, stringbuilder1);
                profileaboutview.addEmploymentLocation(employment.employer, stringbuilder1.toString());
            }

        } else
        if(mIsMyProfile)
            profileaboutview.setNoEmploymentLocations();
        boolean flag7;
        boolean flag8;
        if(flag6 || mIsEditEnabled)
            flag7 = true;
        else
            flag7 = false;
        profileaboutview.enableWorkSection(flag7);
        if(mProfile.user != null && mProfile.user.educations != null && mProfile.user.educations.education != null && mProfile.user.educations.education.size() > 0)
            flag8 = true;
        else
            flag8 = false;
        profileaboutview.clearEducationLocations();
        if(flag8)
        {
            List list = mProfile.user.educations.education;
            int i = list.size();
            for(int j = 0; j < i; j++)
            {
                Education education = (Education)list.get(j);
                StringBuilder stringbuilder = new StringBuilder();
                if(!TextUtils.isEmpty(education.majorConcentration))
                {
                    stringbuilder.append(education.majorConcentration);
                    addDateInfo(education.dateInfo, stringbuilder);
                }
                profileaboutview.addEducationLocation(education.school, stringbuilder.toString());
            }

        } else
        if(mIsMyProfile)
            profileaboutview.setNoEducationLocations();
        boolean flag9;
        boolean flag10;
        if(flag8 || mIsEditEnabled)
            flag9 = true;
        else
            flag9 = false;
        profileaboutview.enableEducationSection(flag9);
        if(mProfile.user != null && mProfile.user.locations != null && (!TextUtils.isEmpty(mProfile.user.locations.currentLocation) || mProfile.user.locations.otherLocation != null && !mProfile.user.locations.otherLocation.isEmpty()))
            flag10 = true;
        else
            flag10 = false;
        if(flag10)
        {
            profileaboutview.setLocationUrl(mProfile.user.locations.locationMapUrl);
            profileaboutview.clearLocations();
            if(mProfile.user.locations.currentLocation != null)
            {
                String s9 = mProfile.user.locations.currentLocation.trim();
                if(s9.length() != 0)
                    profileaboutview.addLocation(s9, true);
            }
            if(mProfile.user.locations.otherLocation != null)
            {
                Iterator iterator1 = mProfile.user.locations.otherLocation.iterator();
                do
                {
                    if(!iterator1.hasNext())
                        break;
                    String s8 = ((String)iterator1.next()).trim();
                    if(s8.length() != 0)
                        profileaboutview.addLocation(s8, false);
                } while(true);
            }
        } else
        if(mIsMyProfile)
        {
            profileaboutview.setLocationUrl(null);
            profileaboutview.setNoLocations();
        }
        boolean flag11;
        if(flag10 || mIsEditEnabled)
            flag11 = true;
        else
            flag11 = false;
        profileaboutview.enableLocationsSection(flag11);
        profileaboutview.updateLocationsSectionDividers();
        profileaboutview.enableLinksSection(flag1);
        profileaboutview.clearLinks();
        if(flag1)
        {
            Iterator iterator = mProfile.content.links.link.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                ProfilesLink profileslink = (ProfilesLink)iterator.next();
                if(profileslink.url != null)
                {
                    boolean flag12 = mIsPlusPage;
                    String s7 = null;
                    if(flag12)
                        s7 = getString(R.string.profile_item_website);
                    profileaboutview.addLink(profileslink.url, profileslink.label, profileslink.faviconImgUrl, s7);
                }
            } while(true);
        }
        profileaboutview.updateLinksSectionDividers();
        if(mShowCircles)
            profileaboutview.setCircles(mCircleNames);
        else
        if(mShowAddToCircles)
            profileaboutview.showAddToCircles(mIsPlusPage);
        else
        if(mShowBlocked)
            profileaboutview.showBlocked();
        else
        if(mShowProgress)
            profileaboutview.showProgress();
        else
            profileaboutview.showNone();
    }
    
    private String getString(int i)
    {
        return mContext.getString(i);
    }

    private String getString(int i, Object aobj[])
    {
        return mContext.getString(i, aobj);
    }
    
    public final void bindStreamView(View view, Cursor cursor)
    {
        ProfileAboutView profileaboutview;
        if(cursor.getPosition() != 0) {
        	super.bindStreamView(view, cursor);
        } else {
        	if(Log.isLoggable("ProfileAdapter", 3))
                Log.d("ProfileAdapter", (new StringBuilder("bindView(); ")).append(view).toString());
        }
        
        profileaboutview = (ProfileAboutView)view;
        profileaboutview.init(mViewIsExpanded, mIsEditEnabled);
        profileaboutview.showError(mProfileLoadFailed, mErrorText);
        if(mProfile == null) {
        	return;
        }
        ProfileAboutView.DisplayPolicies displaypolicies = new ProfileAboutView.DisplayPolicies();
        if(1 == sScreenMetrics.screenDisplayType) {
        	displaypolicies.showInfoIcons = true;
            if(mLandscape)
                displaypolicies.showDetailsAlways = true;
            else
                displaypolicies.showExpandButtonText = true;
        } else {
        	if(mLandscape)
            {
                displaypolicies.showInfoIcons = true;
                displaypolicies.showDetailsAlways = true;
            } else
            {
                displaypolicies.showInfoIcons = false;
            }
        }
        
        if(mIsUnclaimedLocalPlusPage && !mLandscape)
            displaypolicies.showDetailsAlways = true;
        if(mIsMyProfile && mLandscape)
            displaypolicies.hideButtons = true;
        profileaboutview.setDisplayPolicies(displaypolicies);
        bindProfileAboutView(profileaboutview);
        profileaboutview.setOnClickListener(mProfileViewOnClickListener);
    }

    public final void beginBlockInProgress()
    {
        mBlockRequestPending = true;
        updateCircleList();
        notifyDataSetChanged();
    }
    
    public final void endBlockInProgress(boolean flag)
    {
        mBlockRequestPending = false;
        if(flag)
        {
            boolean flag1 = mIsBlocked;
            boolean flag2 = false;
            if(!flag1)
                flag2 = true;
            mIsBlocked = flag2;
        }
        updateCircleList();
        notifyDataSetChanged();
    }

    public final int getCount()
    {
        return super.getCount();
    }

    public final String getEducationList()
    {
        String s;
        if(mProfile == null || mProfile.user == null || mProfile.user.educations == null)
            s = "{}";
        else
            s = mProfile.user.educations.toJsonString();
        return s;
    }

    public final String getEmploymentList()
    {
        String s;
        if(mProfile == null || mProfile.user == null || mProfile.user.employments == null)
            s = "{}";
        else
            s = mProfile.user.employments.toJsonString();
        return s;
    }

    public final String getFullName()
    {
        return mFullName;
    }

    public final String getGender()
    {
        return mGender;
    }

    public final String getGivenName()
    {
        return mGivenName;
    }

    public final long getItemId(int i)
    {
        long l;
        if(i == 0)
            l = 0L;
        else
            l = super.getItemId(i);
        return l;
    }

    public final String getPlacesLivedList()
    {
        String s;
        if(mProfile == null || mProfile.user == null || mProfile.user.locations == null)
            s = "{}";
        else
            s = mProfile.user.locations.toJsonString();
        return s;
    }

    public final String getScrapbookAlbumId()
    {
        return mScrapbookAlbumId;
    }

    public final Long getScrapbookCoverPhotoId()
    {
        Long long1;
        if(!TextUtils.isEmpty(mScrapbookCoverPhotoId))
            long1 = Long.valueOf(Long.parseLong(mScrapbookCoverPhotoId));
        else
            long1 = null;
        return long1;
    }

    public final int getScrapbookCoverPhotoOffset()
    {
        return mScrapbookCoverPhotoOffset;
    }

    public final String getScrapbookCoverPhotoOwnerId()
    {
        String s;
        if("GALLERY".equals(mScrapbookCoverPhotoOwnerType))
            s = "115239603441691718952";
        else
            s = mAccount.getGaiaId();
        return s;
    }

    public final String getScrapbookCoverPhotoUrl()
    {
        return mScrapbookCoverPhotoUrl;
    }

    public final String getScrapbookLayout()
    {
        return mScrapbookLayout;
    }

    public final Long getScrapbookPhotoId(int i)
    {
        SimpleProfile simpleprofile = mProfile;
        String s = null;
        Long long1;
        if(simpleprofile != null)
        {
            CommonContent commoncontent = mProfile.content;
            s = null;
            if(commoncontent != null)
            {
                ScrapBook scrapbook = mProfile.content.scrapbook;
                s = null;
                if(scrapbook != null)
                {
                    ScrapBook scrapbook1 = mProfile.content.scrapbook;
                    if(i == 0)
                    {
                        s = scrapbook1.coverPhotoEntry.photoId;
                    } else
                    {
                        List list = mProfile.content.scrapbook.plusiEntry;
                        s = null;
                        if(list != null)
                        {
                            int j = list.size();
                            int k = i - 1;
                            s = null;
                            if(j > k)
                                s = ((ScrapBookEntry)list.get(i - 1)).photoId;
                        }
                    }
                }
            }
        }
        if(s != null)
            long1 = Long.valueOf(Long.parseLong(s));
        else
            long1 = null;
        return long1;
    }

    public final String getSharingRosterData()
    {
        String s;
        if(mProfile == null || mProfile.rosterData == null)
            s = "{}";
        else
            s = mProfile.rosterData.toJsonString();
        return s;
    }

    public final int getStreamItemViewType(int i)
    {
        int j;
        if(i == 0)
        {
            if(mLandscape)
                j = 11;
            else
                j = 10;
        } else
        {
            j = super.getStreamItemViewType(i);
        }
        return j;
    }

    public final boolean getViewIsExpanded()
    {
        return mViewIsExpanded;
    }

    public final int getViewTypeCount()
    {
        return 2 + super.getViewTypeCount();
    }

    public final boolean hasCoverPhotoUpgrade()
    {
        return mHasCoverPhotoUpgrade;
    }

    public final void init(String s, boolean flag, boolean flag1, boolean flag2, CircleNameResolver circlenameresolver)
    {
        mPersonId = s;
        mIsMyProfile = flag;
        mHasProfile = flag1;
        mIsSmsIntentRegistered = flag2;
        mCircleNameResolver = circlenameresolver;
        mViewingAsPlusPage = mAccount.isPlusPage();
    }
    
    public final boolean isBlocked()
    {
        return mIsBlocked;
    }

    public final boolean isMuted()
    {
        return mIsMuted;
    }

    public final boolean isPlusOnedByMe()
    {
        return mPlusOneByMe;
    }

    public final boolean isPlusPage()
    {
        return mIsPlusPage;
    }
    
    public final View newStreamView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        int i;
        int j;
        int k;
        Object obj;
        i = -3;
        j = 2;
        k = 1;
        if(cursor.getPosition() != 0) {
        	obj = super.newStreamView(context, cursor, viewgroup);
        } else {
        	obj = (ProfileAboutView)((LayoutInflater)context.getSystemService("layout_inflater")).inflate(R.layout.profile_about_fragment, null);
        }
        
        int l;
        ColumnGridView.LayoutParams layoutparams;
        if(mLandscape)
            l = k;
        else
            l = j;
        
        int i1;
        if(1 == sScreenMetrics.screenDisplayType) {
        	if(mLandscape)
                i1 = (int)(0.69999999999999996D * (double)sScreenMetrics.longDimension);
            else
                i1 = i;
            i = i1;
            j = k;
        } else {
        	k = j;
        }
        
        layoutparams = new ColumnGridView.LayoutParams(l, i, k, j);
        if(!mLandscape)
            layoutparams.height = -2;
        ((ProfileAboutView) (obj)).setLayoutParams(layoutparams);
        if(android.os.Build.VERSION.SDK_INT >= 11)
            ((ProfileAboutView) (obj)).setLayoutTransition(new LayoutTransition());
        if(Log.isLoggable("ProfileAdapter", 3))
            Log.d("ProfileAdapter", (new StringBuilder("newView() -> ")).append(obj).toString());
        return ((View) (obj));
    }
    
    public final void setOnClickListener(ProfileAboutView.OnClickListener onclicklistener)
    {
        mProfileViewOnClickListener = onclicklistener;
    }
    
    public final void setProfileData(EsPeopleData.ProfileAndContactData profileandcontactdata)
    {
    	if(null == profileandcontactdata) {
    		return;
    	}
    	
    	boolean flag = true;
    	ScrapBook scrapbook = null;
    	boolean flag2;
        SimpleProfile simpleprofile;
        boolean flag3;
        DataPlusOne dataplusone;
    	mData = profileandcontactdata;
        mProfile = profileandcontactdata.profile;
        if(mProfile == null)
        {
            mProfile = new SimpleProfile();
            mProfile.profileType = "USER";
            if(mPersonId.startsWith("e:"))
            {
                mProfile.content = new CommonContent();
                mProfile.content.contacts = new Contacts();
                mProfile.content.contacts.email = new ArrayList();
                TaggedEmail taggedemail = new TaggedEmail();
                taggedemail.value = mPersonId.substring(2);
                mProfile.content.contacts.email.add(taggedemail);
            } else
            if(mPersonId.startsWith("p:"))
            {
                mProfile.content = new CommonContent();
                mProfile.content.contacts = new Contacts();
                mProfile.content.contacts.phone = new ArrayList();
                TaggedPhone taggedphone = new TaggedPhone();
                taggedphone.value = mPersonId.substring(2);
                mProfile.content.contacts.phone.add(taggedphone);
            }
        }
        if(null != mProfile.profileType) {
        	if(!"USER".equals(mProfile.profileType)) {
        		if("PLUSPAGE".equals(mProfile.profileType))
                {
                    mIsPlusPage = flag;
                    simpleprofile = mProfile;
                    if(simpleprofile == null)
                        flag3 = false;
                    else
                    if(!"PLUSPAGE".equals(simpleprofile.profileType))
                        flag3 = false;
                    else
                    if(simpleprofile.page == null)
                        flag3 = false;
                    else
                    if(!"LOCAL".equals(simpleprofile.page.type))
                        flag3 = false;
                    else
                    if(simpleprofile.page.localInfo == null || simpleprofile.page.localInfo.paper == null)
                        flag3 = false;
                    else
                        flag3 = flag;
                    mIsLocalPlusPage = flag3;
                    if(mIsLocalPlusPage)
                        mIsUnclaimedLocalPlusPage = "UNCLAIMED".equals(mProfile.page.localInfo.type);
                    dataplusone = profileandcontactdata.profile.page.plusone;
                    mPlusOnes = dataplusone.globalCount.intValue();
                    mPlusOneByMe = dataplusone.isPlusonedByViewer.booleanValue();
                }
        	} else { 
        		if(mProfile.user != null && mProfile.user.name != null)
                {
                    mGivenName = mProfile.user.name.given;
                    mFamilyName = mProfile.user.name.family;
                }
                mIsPlusPage = false;
        	}
        }
        
        String s;
        boolean flag1;
        if(TextUtils.isEmpty(mProfile.displayName))
            s = getString(R.string.profile_unknown_name);
        else
            s = mProfile.displayName;
        mFullName = s;
        mIsBlocked = profileandcontactdata.blocked;
        mPackedCircleIds = profileandcontactdata.packedCircleIds;
        if(mProfile.config != null && mProfile.config.socialGraphData != null && PrimitiveUtils.safeBoolean(mProfile.config.socialGraphData.muted))
            flag1 = flag;
        else
            flag1 = false;
        mIsMuted = flag1;
        updateCircleList();
        if(mProfile.content != null)
        {
            scrapbook = mProfile.content.scrapbook;
            if(scrapbook != null)
            {
                if(scrapbook.albumId != null)
                    mScrapbookAlbumId = scrapbook.albumId;
                if(scrapbook.coverPhotoEntry != null)
                {
                    mScrapbookCoverPhotoId = scrapbook.coverPhotoEntry.photoId;
                    mScrapbookCoverPhotoUrl = scrapbook.coverPhotoEntry.cropUrl;
                    if(mScrapbookCoverPhotoUrl == null && scrapbook.plusiEntry != null && scrapbook.plusiEntry.size() > 0)
                        mScrapbookCoverPhotoUrl = ((ScrapBookEntry)scrapbook.plusiEntry.get(0)).cropUrl;
                }
            }
            if(mProfile.content.scrapbookInfo != null)
            {
                mScrapbookLayout = mProfile.content.scrapbookInfo.layout;
                if("FULL_BLEED".equals(mScrapbookLayout) || "COVER".equals(mScrapbookLayout))
                    flag2 = flag;
                else
                    flag2 = false;
                mHasCoverPhotoUpgrade = flag2;
                if(mProfile.content.scrapbookInfo.fullBleedPhoto != null)
                {
                    if(mProfile.content.scrapbookInfo.fullBleedPhoto.offset != null)
                        mScrapbookCoverPhotoOffset = PrimitiveUtils.safeInt(mProfile.content.scrapbookInfo.fullBleedPhoto.offset.top);
                    mScrapbookCoverPhotoOwnerType = mProfile.content.scrapbookInfo.fullBleedPhoto.photoOwnerType;
                }
            }
        }
        if(mIsPlusPage)
            mGender = "OTHER";
        else
        if(mProfile.user != null && mProfile.user.gender != null && mProfile.user.gender.value != null)
            mGender = mProfile.user.gender.value;
        else
            mGender = "UNKNOWN";
        if(!mIsMyProfile || mIsPlusPage)
            flag = false;
        mIsEditEnabled = flag;
        if(mData != null)
            notifyDataSetChanged();
    }
    
    public final void setViewIsExpanded(boolean flag)
    {
        mViewIsExpanded = flag;
    }
    
    public final void showError(String s)
    {
        mProfileLoadFailed = true;
        mErrorText = s;
        notifyDataSetChanged();
    }
    
    public final void updateCircleList() {
        if(!mIsMyProfile && !mIsUnclaimedLocalPlusPage && mProfile != null && mCircleNameResolver.isLoaded()) {
        	// TODO
        } else { 
        	mShowProgress = false;
            mShowBlocked = false;
            mShowAddToCircles = false;
            mShowCircles = false;
        }
    }
    
    
}
