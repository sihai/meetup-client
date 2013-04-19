/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.view.ImageTextButton;
import com.galaxy.meetup.client.android.ui.view.TextOnlyAudienceView;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.client.util.SoftInput;
import com.galaxy.meetup.server.client.domain.CoarseDate;
import com.galaxy.meetup.server.client.domain.DataCircleMemberId;
import com.galaxy.meetup.server.client.domain.DateInfo;
import com.galaxy.meetup.server.client.domain.Education;
import com.galaxy.meetup.server.client.domain.Educations;
import com.galaxy.meetup.server.client.domain.Employment;
import com.galaxy.meetup.server.client.domain.Employments;
import com.galaxy.meetup.server.client.domain.Locations;
import com.galaxy.meetup.server.client.domain.Metadata;
import com.galaxy.meetup.server.client.domain.SharingRoster;
import com.galaxy.meetup.server.client.domain.SharingRosterData;
import com.galaxy.meetup.server.client.domain.SharingTarget;
import com.galaxy.meetup.server.client.domain.SharingTargetId;
import com.galaxy.meetup.server.client.domain.SimpleProfile;
import com.galaxy.meetup.server.client.domain.User;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class ProfileEditFragment extends Fragment implements OnClickListener {

	private EsAccount mAccount;
    TextView mAddItemView;
    private AudienceData mAudience;
    TextOnlyAudienceView mAudienceView;
    View mDeletedFieldView;
    private String mDomainId;
    private String mDomainName;
    private int mEditMode;
    View mFocusOverrideView;
    private boolean mHasPublicCircle;
    private ArrayList mItemViewIdsList;
    LinearLayout mItemViews;
    private String mItemsJson;
    private Set mModifiedViews;
    private AudienceData mOriginalAudience;
    private int mOriginalCount;
    private Educations mOriginalEducations;
    private Employments mOriginalEmployments;
    private String mOriginalItemsJson;
    private Locations mOriginalLocations;
    private String mOriginalRequiredScopeId;
    private final EsServiceListener mProfileEditServiceListener = new EsServiceListener() {

        public final void onMutateProfileComplete(int i, ServiceResult serviceresult)
        {
            if(mProfilePendingRequestId != null && mProfilePendingRequestId.intValue() == i) {
            	dismissProgressDialog();
                boolean flag = showErrorToast(serviceresult);
                mProfilePendingRequestId = null;
                if(!flag)
                    finishActivity(-1);
            }
        }
    };
    private Integer mProfilePendingRequestId;
    ImageTextButton mSaveButton;
    ScrollView mScollView;
    private SharingRosterData mSharingRosterData;
    private String mSharingRosterDataJson;
    private int mViewIdNextCurrent;
    private int mViewIdNextEndDate;
    private int mViewIdNextName;
    private int mViewIdNextStartDate;
    private int mViewIdNextTitleOrMajor;
    
    
    public ProfileEditFragment()
    {
        mViewIdNextName = 1000;
        mViewIdNextTitleOrMajor = 2000;
        mViewIdNextStartDate = 3000;
        mViewIdNextEndDate = 4000;
        mViewIdNextCurrent = 5000;
        mModifiedViews = new HashSet();
    }

    private void addChangedField(View view)
    {
        mModifiedViews.add(view);
        boolean flag;
        boolean flag1;
        if(mOriginalCount == 0 && mItemViews.getChildCount() == 0)
            flag = true;
        else
            flag = false;
        if(mModifiedViews.size() == 1 && view == mAudienceView)
            flag1 = true;
        else
            flag1 = false;
        if(!flag1 || !flag)
            mSaveButton.setEnabled(true);
    }

    private View addItem() {
        int i = mEditMode;
        View view = null;
        if(1 == i) {
        	view = getView(((Employment) (null)), null);
        } else if(2 == i) {
        	view = getView(((Education) (null)), null);
        } else if(3 == i) {
        	view = getView(null, null, null);
        }
        
        mItemViews.addView(view);
        updateViewsWithOriginalValues();
        return view;
    }

    private void configureDataInfo(View view, ItemViewIds itemviewids, DateInfo dateinfo)
    {
        EditText edittext = (EditText)view.findViewById(R.id.start);
        edittext.setId(itemviewids.startDate);
        if(dateinfo != null && dateinfo.start != null && dateinfo.start.year != null)
            edittext.setText(Integer.toString(dateinfo.start.year.intValue()));
        EditText edittext1 = (EditText)view.findViewById(R.id.end);
        edittext1.setId(itemviewids.endDate);
        if(dateinfo != null && dateinfo.end != null && dateinfo.end.year != null)
            edittext1.setText(Integer.toString(dateinfo.end.year.intValue()));
        CheckBox checkbox = (CheckBox)view.findViewById(R.id.current);
        checkbox.setId(itemviewids.current);
        checkbox.setTag(edittext1);
        if(dateinfo != null)
            checkbox.setChecked(PrimitiveUtils.safeBoolean(dateinfo.current));
        ImageView imageview = (ImageView)view.findViewById(R.id.delete_item);
        imageview.setOnClickListener(this);
        imageview.setTag(view);
    }

    private Educations createEducations()
    {
        Educations educations = new Educations();
        int i = mItemViews.getChildCount();
        educations.education = new ArrayList(i);
        for(int j = 0; j < i; j++)
        {
            View view = mItemViews.getChildAt(j);
            ItemViewIds itemviewids = (ItemViewIds)view.getTag();
            Education education = new Education();
            education.school = getEditedString(view, itemviewids.name);
            education.majorConcentration = getEditedString(view, itemviewids.titleOrMajor);
            education.dateInfo = new DateInfo();
            String s = getEditedString(view, itemviewids.startDate);
            if(!TextUtils.isEmpty(s))
            {
                education.dateInfo.start = new CoarseDate();
                education.dateInfo.start.year = Integer.valueOf(Integer.parseInt(s));
            }
            String s1 = getEditedString(view, itemviewids.endDate);
            if(!TextUtils.isEmpty(s1))
            {
                education.dateInfo.end = new CoarseDate();
                education.dateInfo.end.year = Integer.valueOf(Integer.parseInt(s1));
            }
            education.dateInfo.current = Boolean.valueOf(getCurrent(view, itemviewids.current));
            educations.education.add(education);
        }

        educations.metadata = createMetadata();
        return educations;
    }

    private Employments createEmployments()
    {
        Employments employments = new Employments();
        int i = mItemViews.getChildCount();
        employments.employment = new ArrayList(i);
        for(int j = 0; j < i; j++)
        {
            View view = mItemViews.getChildAt(j);
            ItemViewIds itemviewids = (ItemViewIds)view.getTag();
            Employment employment = new Employment();
            employment.employer = getEditedString(view, itemviewids.name);
            employment.title = getEditedString(view, itemviewids.titleOrMajor);
            employment.dateInfo = new DateInfo();
            String s = getEditedString(view, itemviewids.startDate);
            if(!TextUtils.isEmpty(s))
            {
                employment.dateInfo.start = new CoarseDate();
                employment.dateInfo.start.year = Integer.valueOf(Integer.parseInt(s));
            }
            String s1 = getEditedString(view, itemviewids.endDate);
            if(!TextUtils.isEmpty(s1))
            {
                employment.dateInfo.end = new CoarseDate();
                employment.dateInfo.end.year = Integer.valueOf(Integer.parseInt(s1));
            }
            employment.dateInfo.current = Boolean.valueOf(getCurrent(view, itemviewids.current));
            employments.employment.add(employment);
        }

        employments.metadata = createMetadata();
        return employments;
    }

    private Locations createLocations(boolean flag)
    {
        Locations locations = new Locations();
        String s = null;
        int i = mItemViews.getChildCount();
        for(int j = 0; j < i; j++)
        {
            View view = mItemViews.getChildAt(j);
            ItemViewIds itemviewids = (ItemViewIds)view.getTag();
            if(getCurrent(view, itemviewids.current))
            {
                s = getEditedString(view, itemviewids.name);
                if(!flag)
                    continue;
                s = (new StringBuilder("~~Internal~CurrentLocation.")).append(s).toString();
            }
            if(locations.otherLocation == null)
                locations.otherLocation = new ArrayList();
            locations.otherLocation.add(getEditedString(view, itemviewids.name));
        }

        if(s != null)
            locations.currentLocation = s;
        locations.metadata = createMetadata();
        return locations;
    }
    
    private Metadata createMetadata() {
        AudienceData audiencedata = mAudienceView.getAudience();
        Metadata metadata = new Metadata();
        int i = audiencedata.getCircleCount();
        int j = audiencedata.getUserCount();
        
        if(1 == i && 0 == j) {
        	int i1 = audiencedata.getCircle(0).getType();
        	if(1 != i1) {
        		if(i1 != 9) {
        			if(i1 == 8)
        	            metadata.scope = "DOMAIN";
        	        else
        	        if(i1 == 7)
        	            metadata.scope = "EXTENDED_CIRCLES";
        	        else
        	        if(i1 == 5)
        	            metadata.scope = "MY_CIRCLES";
        	        else
        	        if(i1 == 101)
        	            metadata.scope = "PRIVATE";
        		} else {
        			metadata.scope = "PUBLIC";
        		}
        		return metadata;
        	}
        }
        
        metadata.scope = "CUSTOM_CHIPS";
        metadata.sharingRoster = new SharingRoster();
        if(mOriginalRequiredScopeId != null)
            metadata.sharingRoster.requiredScopeId = (SharingTargetId)JsonUtil.toBean(mOriginalRequiredScopeId, SharingTargetId.class);
        metadata.sharingRoster.sharingTargetId = new ArrayList();
        int k = 0;
        CircleData circledata;
        SharingTargetId sharingtargetid1;
        if(k < i) {
        	circledata = audiencedata.getCircle(k);
        	sharingtargetid1 = new SharingTargetId();
        	switch(circledata.getType()) {
        	case 1:
        		sharingtargetid1.circleId = EsPeopleData.getFocusCircleId(circledata.getId());
        		break;
        	case 2:
        		
        		break;
        	case 3:
        		
        		break;
        	case 4:
        		
        		break;
        	case 5:
        		sharingtargetid1.groupType = "YOUR_CIRCLES";
        		break;
        	case 6:
        		
        		break;
        	case 7:
        		sharingtargetid1.groupType = "EXTENDED_CIRCLES";
        		break;
        	case 8:
        		sharingtargetid1.groupType = "DASHER_DOMAIN";
        		break;
        	case 9:
        		break;
        	default:
        		
        		break;
        	}
        	metadata.scope = "PUBLIC";
        	return metadata;
        } else {
        	int l = 0;
            while(l < j) 
            {
                PersonData persondata = audiencedata.getUser(l);
                SharingTargetId sharingtargetid = new SharingTargetId();
                sharingtargetid.personId = new DataCircleMemberId();
                sharingtargetid.personId.obfuscatedGaiaId = persondata.getObfuscatedId();
                metadata.sharingRoster.sharingTargetId.add(sharingtargetid);
                l++;
            }
            return metadata;
        }
    }

    private void dismissProgressDialog()
    {
        DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
        if(dialogfragment != null)
            dialogfragment.dismiss();
    }

    private void finishActivity(int i)
    {
        getActivity().setResult(i, null);
        getActivity().finish();
    }
    
    private AudienceData createAudience() {
    	AudienceData audiencedata;
        CircleData circledata;
        if(mHasPublicCircle)
            circledata = new CircleData("0", 9, getString(R.string.acl_public), 0);
        else
        if(mDomainName != null)
            circledata = new CircleData("v.domain", 8, mDomainName, 0);
        else
            circledata = new CircleData("1c", 5, getString(R.string.acl_your_circles), 0);
        audiencedata = new AudienceData(circledata);
        return audiencedata;
    }
    
    private AudienceData getAudience(Metadata metadata) {
    	
    	if(null == metadata || null == metadata.scope) {
    		return createAudience();
    	}
    	
    	if("PRIVATE".equals(metadata.scope)) {
    		return new AudienceData(new CircleData("v.private", 101, getString(R.string.acl_private), 1));
    	}
    	
    	if(metadata.sharingRoster != null && metadata.sharingRoster.sharingTargetId != null) {
    		ArrayList arraylist = new ArrayList();
            ArrayList arraylist1 = new ArrayList();
            List list = metadata.sharingRoster.sharingTargetId;
            int i = list.size();
            int j = 0;
            while(j < i) 
            {
                SharingTargetId sharingtargetid = (SharingTargetId)list.get(j);
                if(sharingtargetid.groupType != null)
                {
                    if("PUBLIC".equals(sharingtargetid.groupType))
                        arraylist1.add(new CircleData("0", 9, getString(R.string.acl_public), 0));
                    else
                    if("DASHER_DOMAIN".equals(sharingtargetid.groupType))
                        arraylist1.add(new CircleData(mDomainId, 8, mDomainName, 1));
                    else
                    if("EXTENDED_CIRCLES".equals(sharingtargetid.groupType))
                        arraylist1.add(new CircleData("1f", 7, getString(R.string.acl_extended_network), 0));
                    else
                    if("YOUR_CIRCLES".equals(sharingtargetid.groupType))
                        arraylist1.add(new CircleData("1c", 5, getString(R.string.acl_your_circles), 0));
                } else
                if(sharingtargetid.circleId != null)
                {
                    String s1 = getCircleNameFromSharingRoster(sharingtargetid.circleId);
                    arraylist1.add(new CircleData(EsPeopleData.getCircleId(sharingtargetid.circleId), 1, s1, 1));
                } else
                if(sharingtargetid.personId != null && sharingtargetid.personId != null && sharingtargetid.personId.obfuscatedGaiaId != null)
                {
                    String s = getPersonNameFromSharingRoster(sharingtargetid.personId.obfuscatedGaiaId);
                    arraylist.add(new PersonData(sharingtargetid.personId.obfuscatedGaiaId, s, null));
                }
                j++;
            }
            if(arraylist.isEmpty() && arraylist1.isEmpty())
                arraylist1.add(new CircleData("v.private", 101, getString(R.string.acl_private), 1));
            return new AudienceData(arraylist, arraylist1);
    	} else {
    		return createAudience();
    	}
    }

    private String getCircleNameFromSharingRoster(String s)
    {
        return getNameFromSharingRoster(1, s);
    }

    private static boolean getCurrent(View view, int i)
    {
        CheckBox checkbox = (CheckBox)view.findViewById(i);
        boolean flag;
        if(checkbox != null && checkbox.isChecked())
            flag = true;
        else
            flag = false;
        return flag;
    }

    private static String getEditedString(View view, int i)
    {
        EditText edittext = (EditText)view.findViewById(i);
        String s;
        if(edittext != null)
            s = edittext.getText().toString();
        else
            s = null;
        return s;
    }
    
    private String getNameFromSharingRoster(int i, String s) {
        if(mSharingRosterData == null && !TextUtils.isEmpty(mSharingRosterDataJson))
            mSharingRosterData = (SharingRosterData)JsonUtil.toBean(mSharingRosterDataJson, SharingRosterData.class);
        if(mSharingRosterData == null || mSharingRosterData.targets == null) 
        	return null;
        
        int size = mSharingRosterData.targets.size();
        for(int k = 0; k < size; k++) {
        	 SharingTarget sharingtarget = (SharingTarget)mSharingRosterData.targets.get(k);
             if(null != sharingtarget.id) {
            	 if(1 == i) {
            		 if(TextUtils.equals(sharingtarget.id.circleId, s)) {
            			 return sharingtarget.displayName;
            		 }
            	 } else if(2 == i) {
            		 if(null != sharingtarget.id.personId && TextUtils.equals(sharingtarget.id.personId.obfuscatedGaiaId, s)) {
            			 return sharingtarget.displayName;
            		 }
            	 } else if(3 == i) {
            		 if("DASHER_DOMAIN".equals(sharingtarget.id.groupType)) {
            			 return sharingtarget.displayName;
            		 }
            	 }
             }
        }
        
        return null;
    }

    private String getPersonNameFromSharingRoster(String s)
    {
        return getNameFromSharingRoster(2, s);
    }

    private View getView(Education education, ItemViewIds itemviewids)
    {
        if(itemviewids == null)
        {
            int i = mViewIdNextName;
            mViewIdNextName = i + 1;
            int j = mViewIdNextTitleOrMajor;
            mViewIdNextTitleOrMajor = j + 1;
            int k = mViewIdNextStartDate;
            mViewIdNextStartDate = k + 1;
            int l = mViewIdNextEndDate;
            mViewIdNextEndDate = l + 1;
            int i1 = mViewIdNextCurrent;
            mViewIdNextCurrent = i1 + 1;
            itemviewids = new ItemViewIds(i, j, k, l, i1);
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.profile_edit_item_education, null);
        view.setTag(itemviewids);
        EditText edittext = (EditText)view.findViewById(R.id.name);
        edittext.setId(itemviewids.name);
        String s;
        EditText edittext1;
        String s1;
        DateInfo dateinfo;
        if(education != null)
            s = education.school;
        else
            s = "";
        edittext.setText(s);
        edittext1 = (EditText)view.findViewById(R.id.title);
        edittext1.setId(itemviewids.titleOrMajor);
        if(education != null)
            s1 = education.majorConcentration;
        else
            s1 = "";
        edittext1.setText(s1);
        dateinfo = null;
        if(education != null)
            dateinfo = education.dateInfo;
        configureDataInfo(view, itemviewids, dateinfo);
        return view;
    }

    private View getView(Employment employment, ItemViewIds itemviewids)
    {
        if(itemviewids == null)
        {
            int i = mViewIdNextName;
            mViewIdNextName = i + 1;
            int j = mViewIdNextTitleOrMajor;
            mViewIdNextTitleOrMajor = j + 1;
            int k = mViewIdNextStartDate;
            mViewIdNextStartDate = k + 1;
            int l = mViewIdNextEndDate;
            mViewIdNextEndDate = l + 1;
            int i1 = mViewIdNextCurrent;
            mViewIdNextCurrent = i1 + 1;
            itemviewids = new ItemViewIds(i, j, k, l, i1);
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.profile_edit_item_employment, null);
        view.setTag(itemviewids);
        EditText edittext = (EditText)view.findViewById(R.id.name);
        edittext.setId(itemviewids.name);
        String s;
        EditText edittext1;
        String s1;
        DateInfo dateinfo;
        if(employment != null)
            s = employment.employer;
        else
            s = "";
        edittext.setText(s);
        edittext1 = (EditText)view.findViewById(R.id.title);
        edittext1.setId(itemviewids.titleOrMajor);
        if(employment != null)
            s1 = employment.title;
        else
            s1 = "";
        edittext1.setText(s1);
        dateinfo = null;
        if(employment != null)
            dateinfo = employment.dateInfo;
        configureDataInfo(view, itemviewids, dateinfo);
        return view;
    }

    private View getView(String s, String s1, ItemViewIds itemviewids)
    {
        if(itemviewids == null)
        {
            int i = mViewIdNextName;
            mViewIdNextName = i + 1;
            int j = mViewIdNextTitleOrMajor;
            mViewIdNextTitleOrMajor = j + 1;
            int k = mViewIdNextStartDate;
            mViewIdNextStartDate = k + 1;
            int l = mViewIdNextEndDate;
            mViewIdNextEndDate = l + 1;
            int i1 = mViewIdNextCurrent;
            mViewIdNextCurrent = i1 + 1;
            itemviewids = new ItemViewIds(i, j, k, l, i1);
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.profile_edit_item_location, null);
        view.setTag(itemviewids);
        EditText edittext = (EditText)view.findViewById(R.id.name);
        edittext.setId(itemviewids.name);
        edittext.setText(s);
        CheckBox checkbox = (CheckBox)view.findViewById(R.id.current);
        checkbox.setId(itemviewids.current);
        checkbox.setTag(view);
        boolean flag;
        ImageView imageview;
        if(s1 != null && s1.equals(s))
            flag = true;
        else
            flag = false;
        checkbox.setChecked(flag);
        imageview = (ImageView)view.findViewById(R.id.delete_item);
        imageview.setOnClickListener(this);
        imageview.setTag(view);
        return view;
    }

    private AudienceData normalizeAudience(AudienceData audiencedata)
    {
        Arrays.sort(audiencedata.getUsers(), new Comparator() {

            public final int compare(Object obj, Object obj1)
            {
                PersonData persondata = (PersonData)obj;
                PersonData persondata1 = (PersonData)obj1;
                return ProfileEditFragment.access$600(ProfileEditFragment.this, persondata.getObfuscatedId(), persondata1.getObfuscatedId());
            }

        });
        Arrays.sort(audiencedata.getCircles(), new Comparator() {

            public final int compare(Object obj, Object obj1)
            {
                CircleData circledata = (CircleData)obj;
                CircleData circledata1 = (CircleData)obj1;
                int i = circledata.getType() - circledata1.getType();
                if(i == 0)
                    if(circledata.getType() != 1)
                        i = 0;
                    else
                        i = ProfileEditFragment.access$600(ProfileEditFragment.this, circledata.getId(), circledata1.getId());
                return i;
            }

        });
        return audiencedata;
    }

    private void onCancel()
    {
        if(mSaveButton.isEnabled())
        {
            SoftInput.hide(mFocusOverrideView);
            AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.app_name), getString(R.string.profile_edit_items_exit_unsaved), getString(R.string.yes), getString(R.string.no));
            alertfragmentdialog.setListener(new AlertFragmentDialog.AlertDialogListener() {

                public final void onDialogCanceled(String s)
                {
                }

                public final void onDialogListClick(int i, Bundle bundle)
                {
                }

                public final void onDialogNegativeClick(String s)
                {
                }

                public final void onDialogPositiveClick(Bundle bundle, String s)
                {
                    finishActivity(0);
                }

            });
            alertfragmentdialog.show(getFragmentManager(), "quit");
        } else
        {
            finishActivity(0);
        }
    }

    private void removeChangedField(View view)
    {
        mModifiedViews.remove(view);
        boolean flag;
        boolean flag1;
        if(mOriginalCount == 0 && mItemViews.getChildCount() == 0)
            flag = true;
        else
            flag = false;
        if(mModifiedViews.size() == 1 && mModifiedViews.contains(mAudienceView))
            flag1 = true;
        else
            flag1 = false;
        if(mModifiedViews.size() == 0 || flag1 && flag)
            mSaveButton.setEnabled(false);
    }

    private void removeFocus()
    {
        if(mFocusOverrideView != null)
            mFocusOverrideView.requestFocus();
        SoftInput.hide(getView());
    }
    
    private boolean showErrorToast(ServiceResult serviceresult) {
        if(serviceresult != null && !serviceresult.hasError())
        {
            Exception exception = serviceresult.getException();
            if(exception == null)
                return false;
        }
        String s;
        if(serviceresult != null && serviceresult.getException() != null)
        {
            String s1 = serviceresult.getException().getMessage();
            s = getString(R.string.profile_edit_update_error, new Object[] {
                s1
            });
        } else
        {
            s = getString(R.string.transient_server_error);
        }
        Toast.makeText(getActivity(), s, 1).show();
        return true;
    }

    private void updateViewsWithDateInfoValues(View view, ItemViewIds itemviewids, DateInfo dateinfo)
    {
        boolean flag = true;
        boolean flag1;
        String s;
        EditText edittext;
        EditTextWatcher edittextwatcher;
        boolean flag2;
        String s1;
        EditText edittext1;
        EditTextWatcher edittextwatcher1;
        CheckBox checkbox;
        CheckboxWatcher checkboxwatcher;
        if(dateinfo != null && dateinfo.start != null && dateinfo.start.year != null)
            flag1 = flag;
        else
            flag1 = false;
        if(flag1)
            s = Integer.toString(dateinfo.start.year.intValue());
        else
            s = "";
        edittext = (EditText)view.findViewById(itemviewids.startDate);
        edittextwatcher = new EditTextWatcher(edittext, s);
        edittextwatcher.onTextChanged(edittext.getText(), 0, 0, 0);
        edittext.addTextChangedListener(edittextwatcher);
        if(dateinfo != null && dateinfo.end != null && dateinfo.end.year != null)
            flag2 = flag;
        else
            flag2 = false;
        if(flag2)
            s1 = Integer.toString(dateinfo.end.year.intValue());
        else
            s1 = "";
        edittext1 = (EditText)view.findViewById(itemviewids.endDate);
        edittextwatcher1 = new EditTextWatcher(edittext1, s1);
        if(dateinfo == null || !PrimitiveUtils.safeBoolean(dateinfo.current))
            flag = false;
        checkbox = (CheckBox)view.findViewById(itemviewids.current);
        checkboxwatcher = new CheckboxWatcher(edittext1, edittextwatcher1, flag);
        checkboxwatcher.onCheckedChanged(checkbox, checkbox.isChecked());
        checkbox.setOnCheckedChangeListener(checkboxwatcher);
    }

    private void updateViewsWithOriginalValues() {
        mModifiedViews.clear();
        mSaveButton.setEnabled(false);
        if(1 == mEditMode) {
        	int i2;
            int j2;
            if(mOriginalEmployments.employment != null)
                i2 = mOriginalEmployments.employment.size();
            else
                i2 = 0;
            j2 = mItemViews.getChildCount();
            if(j2 == 0)
            {
                if(i2 != 0)
                    mSaveButton.setEnabled(true);
            } else
            {
                int k2 = 0;
                while(k2 < j2) 
                {
                    Employment employment;
                    View view2;
                    ItemViewIds itemviewids2;
                    String s3;
                    EditText edittext3;
                    EditTextWatcher edittextwatcher3;
                    String s4;
                    EditText edittext4;
                    EditTextWatcher edittextwatcher4;
                    DateInfo dateinfo1;
                    if(k2 < i2)
                        employment = (Employment)mOriginalEmployments.employment.get(k2);
                    else
                        employment = null;
                    view2 = mItemViews.getChildAt(k2);
                    itemviewids2 = (ItemViewIds)view2.getTag();
                    if(employment != null)
                        s3 = employment.employer;
                    else
                        s3 = "";
                    edittext3 = (EditText)view2.findViewById(itemviewids2.name);
                    edittextwatcher3 = new EditTextWatcher(edittext3, s3);
                    edittextwatcher3.onTextChanged(edittext3.getText(), 0, 0, 0);
                    edittext3.addTextChangedListener(edittextwatcher3);
                    if(employment != null)
                        s4 = employment.title;
                    else
                        s4 = "";
                    edittext4 = (EditText)view2.findViewById(itemviewids2.titleOrMajor);
                    edittextwatcher4 = new EditTextWatcher(edittext4, s4);
                    edittextwatcher4.onTextChanged(edittext4.getText(), 0, 0, 0);
                    edittext4.addTextChangedListener(edittextwatcher4);
                    if(employment != null)
                        dateinfo1 = employment.dateInfo;
                    else
                        dateinfo1 = null;
                    updateViewsWithDateInfoValues(view2, itemviewids2, dateinfo1);
                    k2++;
                }
                if(i2 > j2)
                    addChangedField(mDeletedFieldView);
            }
        } else if(2 == mEditMode) {
        	int j1;
            int k1;
            if(mOriginalEducations.education != null)
                j1 = mOriginalEducations.education.size();
            else
                j1 = 0;
            k1 = mItemViews.getChildCount();
            if(k1 == 0)
            {
                if(j1 != 0)
                    mSaveButton.setEnabled(true);
            } else
            {
                int l1 = 0;
                while(l1 < k1) 
                {
                    Education education;
                    View view1;
                    ItemViewIds itemviewids1;
                    String s1;
                    EditText edittext1;
                    EditTextWatcher edittextwatcher1;
                    String s2;
                    EditText edittext2;
                    EditTextWatcher edittextwatcher2;
                    DateInfo dateinfo;
                    if(l1 < j1)
                        education = (Education)mOriginalEducations.education.get(l1);
                    else
                        education = null;
                    view1 = mItemViews.getChildAt(l1);
                    itemviewids1 = (ItemViewIds)view1.getTag();
                    if(education != null)
                        s1 = education.school;
                    else
                        s1 = "";
                    edittext1 = (EditText)view1.findViewById(itemviewids1.name);
                    edittextwatcher1 = new EditTextWatcher(edittext1, s1);
                    edittextwatcher1.onTextChanged(edittext1.getText(), 0, 0, 0);
                    edittext1.addTextChangedListener(edittextwatcher1);
                    if(education != null)
                        s2 = education.majorConcentration;
                    else
                        s2 = "";
                    edittext2 = (EditText)view1.findViewById(itemviewids1.titleOrMajor);
                    edittextwatcher2 = new EditTextWatcher(edittext2, s2);
                    edittextwatcher2.onTextChanged(edittext2.getText(), 0, 0, 0);
                    edittext2.addTextChangedListener(edittextwatcher2);
                    if(education != null)
                        dateinfo = education.dateInfo;
                    else
                        dateinfo = null;
                    updateViewsWithDateInfoValues(view1, itemviewids1, dateinfo);
                    l1++;
                }
                if(j1 > k1)
                    addChangedField(mDeletedFieldView);
            }
        } else if(3 == mEditMode) {
        	int i;
            int j;
            int k;
            if(mOriginalLocations.otherLocation != null)
                i = mOriginalLocations.otherLocation.size();
            else
                i = 0;
            j = mItemViews.getChildCount();
            k = 0;
            if(j == 0)
            {
                if(mOriginalLocations.currentLocation != null || mOriginalLocations.otherLocation != null && mOriginalLocations.otherLocation.size() > 0)
                    mSaveButton.setEnabled(true);
            } else
            {
                int l = 0;
                while(l < j) 
                {
                    String s;
                    boolean flag;
                    View view;
                    ItemViewIds itemviewids;
                    EditText edittext;
                    EditTextWatcher edittextwatcher;
                    CheckBox checkbox;
                    LocationCheckboxWatcher locationcheckboxwatcher;
                    if(l == 0 && !TextUtils.isEmpty(mOriginalLocations.currentLocation))
                    {
                        s = mOriginalLocations.currentLocation;
                        flag = true;
                        k = 1;
                    } else
                    {
                        int i1 = l - k;
                        if(i1 < i)
                            s = (String)mOriginalLocations.otherLocation.get(i1);
                        else
                            s = "";
                        flag = false;
                    }
                    view = mItemViews.getChildAt(l);
                    itemviewids = (ItemViewIds)view.getTag();
                    edittext = (EditText)view.findViewById(itemviewids.name);
                    edittextwatcher = new EditTextWatcher(edittext, s);
                    edittextwatcher.onTextChanged(edittext.getText(), 0, 0, 0);
                    edittext.addTextChangedListener(edittextwatcher);
                    checkbox = (CheckBox)view.findViewById(itemviewids.current);
                    locationcheckboxwatcher = new LocationCheckboxWatcher(flag);
                    locationcheckboxwatcher.onCheckedChanged(checkbox, checkbox.isChecked());
                    checkbox.setOnCheckedChangeListener(locationcheckboxwatcher);
                    l++;
                }
                if(i > j - k)
                    addChangedField(mDeletedFieldView);
            }
        }
        
    }
    
        
    public final void onActivityResult(int i, int j, Intent intent) {
        if(j == -1) {
        	switch(i)
            {
            case 1: // '\001'
                if(intent != null)
                {
                    AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
                    if(audiencedata != null)
                    {
                        mAudienceView.replaceAudience(audiencedata);
                        if(mOriginalAudience.equals(normalizeAudience(audiencedata.clone())))
                            removeChangedField(mAudienceView);
                        else
                            addChangedField(mAudienceView);
                    }
                }
                break;
            }
        }
    }

    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.cancel) {
        	onCancel();
        	return;
        } else if(i == R.id.save) {
        	removeFocus();
            SoftInput.hide(getView());
            if(1 == mEditMode || 3 == mEditMode) {
            	SimpleProfile simpleprofile;
            	if(1 == mEditMode) {
	                Employments employments = createEmployments();
	                simpleprofile = new SimpleProfile();
	                simpleprofile.user = new User();
	                simpleprofile.user.employments = employments;
	                simpleprofile.user.employments.metadata = createMetadata();
            	} else {
            		Locations locations = createLocations(false);
                    simpleprofile = new SimpleProfile();
                    simpleprofile.user = new User();
                    simpleprofile.user.locations = locations;
                    simpleprofile.user.locations.metadata = createMetadata();
            	}
            	mProfilePendingRequestId = EsService.mutateProfile(getActivity(), mAccount, simpleprofile);
                ProgressFragmentDialog.newInstance(null, getString(R.string.profile_edit_updating), false).show(getFragmentManager(), "req_pending");
            } else if(2 == mEditMode) {
            	Educations educations = createEducations();
            	SimpleProfile simpleprofile = new SimpleProfile();
                simpleprofile.user = new User();
                simpleprofile.user.educations = educations;
                simpleprofile.user.educations.metadata = createMetadata();
            }
        } else if(i == R.id.add_item) {
        	View view1 = addItem();
            if(view1 != null)
            {
                View view2 = view1.findViewById(((ItemViewIds)view1.getTag()).name);
                view2.requestFocus();
                SoftInput.show(view2);
            }
        } else if(i == R.id.delete_item) {
        	final View itemToBeRemoved = (View)view.getTag();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.profile_edit_item_remove_confirm);
            builder.setPositiveButton(R.string.profile_edit_item_remove, new android.content.DialogInterface.OnClickListener() {

                public final void onClick(DialogInterface dialoginterface, int j)
                {
                    mItemViews.removeView(itemToBeRemoved);
                    updateViewsWithOriginalValues();
                }
            });
            builder.setNegativeButton(0x1040000, new android.content.DialogInterface.OnClickListener() {

                public final void onClick(DialogInterface dialoginterface, int j)
                {
                    dialoginterface.dismiss();
                }

            });
            builder.show();
        } else if(i == R.id.audience) {
        	removeFocus();
            SimpleAudiencePickerDialog simpleaudiencepickerdialog = SimpleAudiencePickerDialog.newInstance(mDomainName, mDomainId, mHasPublicCircle);
            simpleaudiencepickerdialog.setTargetFragment(this, 0);
            simpleaudiencepickerdialog.show(getFragmentManager(), "simple_audience");
        }

    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mDomainName = bundle.getString("domain_name");
            mDomainId = bundle.getString("domain_id");
            mHasPublicCircle = bundle.getBoolean("has_public_circle");
            mItemsJson = bundle.getString("items_json");
            mAudience = (AudienceData)bundle.getParcelable("audience");
            mOriginalRequiredScopeId = bundle.getString("required_scope_id");
            mItemViewIdsList = (ArrayList)bundle.getSerializable("items");
            mViewIdNextName = bundle.getInt("next_name");
            mViewIdNextTitleOrMajor = bundle.getInt("next_title");
            mViewIdNextStartDate = bundle.getInt("next_start");
            mViewIdNextEndDate = bundle.getInt("next_end");
            mViewIdNextCurrent = bundle.getInt("next_current");
        }
        mEditMode = getArguments().getInt("profile_edit_mode");
        mAccount = (EsAccount)getArguments().getParcelable("account");
        mOriginalItemsJson = getArguments().getString("profile_edit_items_json");
        if(mItemsJson == null)
            mItemsJson = mOriginalItemsJson;
        mSharingRosterDataJson = getArguments().getString("profile_edit_roster_json");
        if(1 == mEditMode) {
        	mOriginalEmployments = (Employments)JsonUtil.toBean(mOriginalItemsJson, Employments.class);
            mOriginalAudience = normalizeAudience(getAudience(mOriginalEmployments.metadata));
            int l;
            if(mOriginalEmployments.employment != null)
                l = mOriginalEmployments.employment.size();
            else
                l = 0;
            mOriginalCount = l;
        } else if(2 == mEditMode) {
        	mOriginalEducations = (Educations)JsonUtil.toBean(mOriginalItemsJson, Educations.class);
            mOriginalAudience = normalizeAudience(getAudience(mOriginalEducations.metadata));
            List list1 = mOriginalEducations.education;
            int k = 0;
            if(list1 != null)
                k = mOriginalEducations.education.size();
            mOriginalCount = k;
        } else if(3 == mEditMode) {
        	mOriginalLocations = (Locations)JsonUtil.toBean(mOriginalItemsJson, Locations.class);
            mOriginalAudience = normalizeAudience(getAudience(mOriginalLocations.metadata));
            int i;
            List list;
            int j;
            if(!TextUtils.isEmpty(mOriginalLocations.currentLocation))
                i = 1;
            else
                i = 0;
            list = mOriginalLocations.otherLocation;
            j = 0;
            if(list != null)
                j = mOriginalLocations.otherLocation.size();
            mOriginalCount = i + j;
        }

    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        int i = mEditMode;
        Metadata metadata;
        int j = 0;
        View view = layoutinflater.inflate(R.layout.profile_edit_items, null);
        mFocusOverrideView = view.findViewById(R.id.focus_override);
        mDeletedFieldView = new View(getActivity());
        ((ImageTextButton)view.findViewById(R.id.cancel)).setOnClickListener(this);
        mSaveButton = (ImageTextButton)view.findViewById(R.id.save);
        mSaveButton.setOnClickListener(this);
        mScollView = (ScrollView)view.findViewById(R.id.scroller);
        mItemViews = (LinearLayout)view.findViewById(R.id.items);
        CircleData circledata = EsPeopleData.getCircleData(getActivity(), mAccount, 8);
        if(circledata != null)
        {
            mDomainName = circledata.getName();
            mDomainId = circledata.getId();
        }
        mHasPublicCircle = EsPeopleData.hasPublicCircle(getActivity(), mAccount);
        metadata = null;
        if(1 == i) {
        	boolean flag3 = TextUtils.isEmpty(mItemsJson);
            metadata = null;
            j = 0;
            if(!flag3)
            {
                Employments employments = (Employments)JsonUtil.toBean(mItemsJson, Employments.class);
                metadata = null;
                j = 0;
                if(employments != null)
                {
                    List list1 = employments.employment;
                    metadata = null;
                    j = 0;
                    if(list1 != null)
                    {
                        j = employments.employment.size();
                        int l1 = 0;
                        while(l1 < j) 
                        {
                            Employment employment = (Employment)employments.employment.get(l1);
                            ItemViewIds itemviewids2;
                            View view4;
                            if(mItemViewIdsList != null && mItemViewIdsList.size() > l1)
                                itemviewids2 = (ItemViewIds)mItemViewIdsList.get(l1);
                            else
                                itemviewids2 = null;
                            view4 = getView(employment, itemviewids2);
                            mItemViews.addView(view4);
                            l1++;
                        }
                        metadata = employments.metadata;
                    }
                }
            }
        } else if(2 == i) {
        	boolean flag2 = TextUtils.isEmpty(mItemsJson);
            metadata = null;
            j = 0;
            if(!flag2)
            {
                Educations educations = (Educations)JsonUtil.toBean(mItemsJson, Educations.class);
                metadata = null;
                j = 0;
                if(educations != null)
                {
                    List list = educations.education;
                    metadata = null;
                    j = 0;
                    if(list != null)
                    {
                        j = educations.education.size();
                        int k1 = 0;
                        while(k1 < j) 
                        {
                            Education education = (Education)educations.education.get(k1);
                            ItemViewIds itemviewids1;
                            View view3;
                            if(mItemViewIdsList != null && mItemViewIdsList.size() > k1)
                                itemviewids1 = (ItemViewIds)mItemViewIdsList.get(k1);
                            else
                                itemviewids1 = null;
                            view3 = getView(education, itemviewids1);
                            mItemViews.addView(view3);
                            k1++;
                        }
                        metadata = educations.metadata;
                    }
                }
            }
        } else if(3 == i) {
        	boolean flag = TextUtils.isEmpty(mItemsJson);
            metadata = null;
            j = 0;
            if(!flag)
            {
                Locations locations = (Locations)JsonUtil.toBean(mItemsJson, Locations.class);
                metadata = null;
                j = 0;
                if(locations != null)
                {
                    String s = locations.currentLocation;
                    boolean flag1 = TextUtils.isEmpty(s);
                    int k = 0;
                    if(!flag1)
                        if(s.startsWith("~~Internal~CurrentLocation."))
                        {
                            s = locations.currentLocation.substring(27);
                        } else
                        {
                            View view2 = getView(s, s, null);
                            mItemViews.addView(view2);
                            k = 1;
                        }
                    if(locations.otherLocation != null)
                    {
                        int i1 = locations.otherLocation.size();
                        k += i1;
                        int j1 = 0;
                        while(j1 < i1) 
                        {
                            ItemViewIds itemviewids;
                            View view1;
                            if(mItemViewIdsList != null && mItemViewIdsList.size() > j1)
                                itemviewids = (ItemViewIds)mItemViewIdsList.get(j1);
                            else
                                itemviewids = null;
                            view1 = getView((String)locations.otherLocation.get(j1), s, itemviewids);
                            mItemViews.addView(view1);
                            j1++;
                        }
                    }
                    metadata = locations.metadata;
                    j = k;
                }
            }
        }
        
        if(j == 0)
            addItem();
        int l = 0;
        if(1 == mEditMode) {
        	l = R.string.profile_add_a_job;
        } else if(2 == mEditMode) {
        	l = R.string.profile_add_a_school;
        } else if(3 == mEditMode) {
        	l = R.string.profile_add_a_place;
        }
        
        mAddItemView = (TextView)view.findViewById(R.id.add_item);
        mAddItemView.setText(l);
        mAddItemView.setOnClickListener(this);
        if(mAudience == null)
            mAudience = getAudience(metadata);
        mAudienceView = (TextOnlyAudienceView)view.findViewById(R.id.audience);
        mAudienceView.setAccount(mAccount);
        mAudienceView.setOnClickListener(this);
        mAudienceView.setChevronDirection(TextOnlyAudienceView.ChevronDirection.POINT_RIGHT);
        mAudienceView.replaceAudience(mAudience);
        if(bundle == null)
            mFocusOverrideView.requestFocus();
        updateViewsWithOriginalValues();
        return view;
    }

    public final void onDiscard()
    {
        onCancel();
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mProfileEditServiceListener);
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mProfileEditServiceListener);
        if(mProfilePendingRequestId != null && !EsService.isRequestPending(mProfilePendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mProfilePendingRequestId.intValue());
            mProfilePendingRequestId = null;
            dismissProgressDialog();
            if(!showErrorToast(serviceresult))
                finishActivity(-1);
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
    	String s = null;
    	bundle.putParcelable("audience", mAudience);
        if(1 == mEditMode) {
        	Employments employments = createEmployments();
            s = employments.toJsonString();
        } else if(2 == mEditMode) {
        	Educations educations = createEducations();
            s = educations.toJsonString();
        } else if(3 == mEditMode) {
        	Locations locations = createLocations(true);
            s = locations.toJsonString();
        }
        
        bundle.putString("items_json", s);
        if(mDomainName != null)
            bundle.putString("domain_name", mDomainName);
        if(mDomainId != null)
            bundle.putString("domain_id", mDomainId);
        bundle.putBoolean("has_public_circle", mHasPublicCircle);
        bundle.putString("required_scope_id", mOriginalRequiredScopeId);
        int i = mItemViews.getChildCount();
        if(i > 0) {
	        if(mItemViewIdsList == null)
	            mItemViewIdsList = new ArrayList();
	        mItemViewIdsList.clear();
	        for(int j = 0; j < i; j++)
	        {
	            View view = mItemViews.getChildAt(j);
	            mItemViewIdsList.add((ItemViewIds)view.getTag());
	        }
        }
        bundle.putSerializable("items", mItemViewIdsList);
        bundle.putInt("next_name", mViewIdNextName);
        bundle.putInt("next_title", mViewIdNextTitleOrMajor);
        bundle.putInt("next_start", mViewIdNextStartDate);
        bundle.putInt("next_end", mViewIdNextEndDate);
        bundle.putInt("next_current", mViewIdNextCurrent);
        super.onSaveInstanceState(bundle);
        return;
        
    }

    public final void onSetSimpleAudience(String s, int i, String s1)
    {
        if(i > 0)
        {
            AudienceData audiencedata = new AudienceData(new CircleData(s, i, s1, 1));
            mAudienceView.replaceAudience(audiencedata);
            mOriginalAudience.equals(audiencedata);
            addChangedField(mAudienceView);
        } else
        {
            AudienceData audiencedata1 = mAudienceView.getAudience();
            if(audiencedata1.getCircleCount() == 1 && "v.private".equals(audiencedata1.getCircle(0).getId()))
                audiencedata1 = null;
            Intent intent = Intents.getEditAudienceActivityIntent(getActivity(), mAccount, getString(R.string.profile_edit_item_acl_picker), audiencedata1, 5, false, true, true, false);
            SoftInput.hide(getView());
            startActivityForResult(intent, 1);
        }
    }
    
    static int access$600(ProfileEditFragment profileeditfragment, String s, String s1)
    {
        int i;
        if(s == null && s1 == null)
            i = 0;
        else
        if(s1 == null)
            i = -1;
        else
        if(s == null)
            i = 1;
        else
            i = s.compareTo(s1);
        return i;
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private final class CheckboxWatcher implements android.widget.CompoundButton.OnCheckedChangeListener {

    	private final EditText mLinkedEditText;
        private String mLinkedEditTextPreviousValue;
        private final EditTextWatcher mLinkedEditTextWatcher;
        private final boolean mOriginalCurrent;

        public CheckboxWatcher(EditText edittext, EditTextWatcher edittextwatcher, boolean flag)
        {
            super();
            mLinkedEditText = edittext;
            mLinkedEditTextWatcher = edittextwatcher;
            mOriginalCurrent = flag;
        }
        
	    public final void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
	    {
	        boolean flag1 = true;
	        EditText edittext;
	        if(flag)
	        {
	            mLinkedEditTextPreviousValue = mLinkedEditText.getText().toString();
	            removeChangedField(mLinkedEditText);
	            mLinkedEditText.removeTextChangedListener(mLinkedEditTextWatcher);
	            Calendar calendar = Calendar.getInstance();
	            mLinkedEditText.setText(Integer.toString(calendar.get(1)));
	        } else
	        {
	            if(mLinkedEditTextPreviousValue == null)
	                mLinkedEditTextPreviousValue = mLinkedEditText.getText().toString();
	            mLinkedEditText.addTextChangedListener(mLinkedEditTextWatcher);
	            mLinkedEditText.setText(mLinkedEditTextPreviousValue);
	        }
	        edittext = mLinkedEditText;
	        if(flag)
	            flag1 = false;
	        edittext.setEnabled(flag1);
	        if(mOriginalCurrent == flag)
	            removeChangedField(compoundbutton);
	        else
	            addChangedField(compoundbutton);
	    }
    }

	private final class EditTextWatcher implements TextWatcher {
	
		private final String mOriginalValue;
	    private final View mView;
	
	    public EditTextWatcher(View view, String s)
	    {
	        super();
	        mView = view;
	        mOriginalValue = s;
	    }
	    
	    public final void afterTextChanged(Editable editable)
	    {
	    }
	
	    public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
	    {
	    }
	
	    public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
	    {
	        if(charsequence.toString().equals(mOriginalValue))
	            removeChangedField(mView);
	        else
	            addChangedField(mView);
	    }
	}

	public static final class ItemViewIds implements Serializable {
	
	    public int current;
	    public int endDate;
	    public int name;
	    public int startDate;
	    public int titleOrMajor;
	
	    public ItemViewIds(int i, int j, int k, int l, int i1)
	    {
	        name = i;
	        titleOrMajor = j;
	        startDate = k;
	        endDate = l;
	        current = i1;
	    }
	}

	private final class LocationCheckboxWatcher implements android.widget.CompoundButton.OnCheckedChangeListener {

		private final boolean mOriginalValue;

	    public LocationCheckboxWatcher(boolean flag)
	    {
	        super();
	        mOriginalValue = flag;
	    }
	    
	    public final void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
	    {
	        if(flag)
	        {
	            View view = (View)compoundbutton.getTag();
	            LinearLayout linearlayout = (LinearLayout)view.getParent();
	            int i = linearlayout.getChildCount();
	            for(int j = 0; j < i; j++)
	            {
	                View view1 = linearlayout.getChildAt(j);
	                if(view1 != view)
	                    ((CheckBox)view1.findViewById(((ItemViewIds)view1.getTag()).current)).setChecked(false);
	            }
	
	        }
	        if(mOriginalValue == flag)
	            removeChangedField(compoundbutton);
	        else
	            addChangedField(compoundbutton);
	    }
	}
}
