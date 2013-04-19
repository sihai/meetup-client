/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsLocalPageData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.view.EsImageView;
import com.galaxy.meetup.server.client.domain.GoogleReviewProto;
import com.galaxy.meetup.server.client.domain.PriceLevelProto;
import com.galaxy.meetup.server.client.domain.PriceLevelsProto;
import com.galaxy.meetup.server.client.domain.PriceProto;
import com.galaxy.meetup.server.client.domain.ZagatAspectRatingProto;
import com.galaxy.meetup.server.client.domain.ZagatAspectRatingsProto;

/**
 * 
 * @author sihai
 *
 */
public class WriteReviewFragment extends HostedFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks, android.view.View.OnClickListener, android.widget.RadioGroup.OnCheckedChangeListener {

	private static final int BUCKETED_PRICE_DRAWABLES[];
    private static final HashMap sRatingValues;
    private static final HashMap sRatingViews;
    private EsAccount mAccount;
    private EditText mAspectCost;
    private TextWatcher mAspectCostTextWatcher;
    private LinearLayout mAspectRatings;
    private LinearLayout mBucketedPriceContainer;
    private RadioGroup mBucketedPriceGroup;
    private TextView mBucketedPriceTip;
    private RadioButton mBucketedPrices[];
    private TextView mBusinessAddress;
    private EsImageView mBusinessPhoto;
    private TextView mBusinessTitle;
    private Button mCancelButton;
    private String mCid;
    private LinearLayout mContinuousCostContainer;
    private TextView mCostCurrencySymbol;
    private TextView mCostExplanation;
    private TextView mCostLabel;
    private Integer mPendingDeleteRequestId;
    private Integer mPendingWriteRequestId;
    private String mPersonId;
    private TextView mPostingPubliclyNotice;
    private final HashMap mPriceLevels = new HashMap();
    private Button mPublishButton;
    private boolean mReviewExists;
    private final EsServiceListener mServiceListener = new ServiceListener();
    private EditText mWriteReview;
    private TextWatcher mWriteReviewTextWatcher;
    private GoogleReviewProto mYourReview;

    static 
    {
        int ai[] = new int[4];
        ai[0] = R.drawable.bucketed_price_one_coin;
        ai[1] = R.drawable.bucketed_price_two_coins;
        ai[2] = R.drawable.bucketed_price_three_coins;
        ai[3] = R.drawable.bucketed_price_four_coins;
        BUCKETED_PRICE_DRAWABLES = ai;
        sRatingViews = new HashMap();
        sRatingValues = new HashMap();
        sRatingViews.put("0", Integer.valueOf(R.id.aspect_rating_0));
        sRatingViews.put("1", Integer.valueOf(R.id.aspect_rating_1));
        sRatingViews.put("2", Integer.valueOf(R.id.aspect_rating_2));
        sRatingViews.put("3", Integer.valueOf(R.id.aspect_rating_3));
        sRatingValues.put(Integer.valueOf(R.id.aspect_rating_0), "0");
        sRatingValues.put(Integer.valueOf(R.id.aspect_rating_1), "1");
        sRatingValues.put(Integer.valueOf(R.id.aspect_rating_2), "2");
        sRatingValues.put(Integer.valueOf(R.id.aspect_rating_3), "3");
    }
    
    public WriteReviewFragment()
    {
        mBucketedPrices = new RadioButton[4];
        mWriteReviewTextWatcher = new TextWatcher() {

            public final void afterTextChanged(Editable editable)
            {
            }

            public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
                mYourReview.fullText = mWriteReview.getText().toString().trim();
            }

        };
        mAspectCostTextWatcher = new TextWatcher() {

            public final void afterTextChanged(Editable editable)
            {
            }

            public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
                mYourReview.price.valueDisplay = mAspectCost.getText().toString().trim();
            }

        };
    }

    private void handleDeleteReviewCallback(int i, ServiceResult serviceresult)
    {
        if(mPendingDeleteRequestId != null && mPendingDeleteRequestId.intValue() == i)
        {
            mPendingDeleteRequestId = null;
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("write_review_request_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            if(serviceresult != null && serviceresult.hasError())
            {
                Toast.makeText(getActivity(), R.string.delete_review_operation_failed, 0).show();
            } else
            {
                Toast.makeText(getActivity(), R.string.delete_review_operation_successful, 0).show();
                getActivity().finish();
            }
        }
    }

    private void handleWriteReviewCallback(int i, ServiceResult serviceresult)
    {
        if(mPendingWriteRequestId != null && mPendingWriteRequestId.intValue() == i)
        {
            mPendingWriteRequestId = null;
            DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("write_review_request_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            if(serviceresult != null && serviceresult.hasError())
            {
                Toast.makeText(getActivity(), R.string.write_review_operation_failed, 0).show();
            } else
            {
                Toast.makeText(getActivity(), R.string.write_review_operation_successful, 0).show();
                getActivity().finish();
            }
        }
    }

    private void showProgressDialog(String s)
    {
        ProgressFragmentDialog.newInstance(null, s, false).show(getFragmentManager(), "write_review_request_pending");
    }

    private void updateBucketedPriceViews(PriceLevelsProto pricelevelsproto)
    {
        mBucketedPriceContainer.setVisibility(0);
        mCostLabel.setText(pricelevelsproto.labelDisplay);
        mCostExplanation.setText(getString(R.string.write_review_optional));
        int i = 0;
        mPriceLevels.clear();
        Iterator iterator = pricelevelsproto.priceLevel.iterator();
        while(iterator.hasNext()) 
        {
            PriceLevelProto pricelevelproto = (PriceLevelProto)iterator.next();
            RadioButton radiobutton = mBucketedPrices[i];
            String s = pricelevelproto.labelDisplay;
            if(s != null)
                radiobutton.setText(s);
            else
                radiobutton.setBackgroundResource(BUCKETED_PRICE_DRAWABLES[i]);
            if(pricelevelsproto.ratedValueId != null && pricelevelsproto.ratedValueId.equals(pricelevelproto.valueId))
                radiobutton.setChecked(true);
            mPriceLevels.put(Integer.valueOf(radiobutton.getId()), pricelevelproto);
            i++;
        }
    }

    private void updateZagatAspectViews(List list)
    {
        mAspectRatings.removeAllViews();
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            ZagatAspectRatingProto zagataspectratingproto = (ZagatAspectRatingProto)iterator.next();
            LinearLayout linearlayout = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.write_review_aspect_rating, mAspectRatings, false);
            ((TextView)linearlayout.findViewById(R.id.aspect_label)).setText(zagataspectratingproto.labelDisplay);
            mAspectRatings.addView(linearlayout);
            RadioGroup radiogroup = (RadioGroup)linearlayout.findViewById(R.id.aspect_rating_group);
            radiogroup.setOnCheckedChangeListener(this);
            radiogroup.setTag(zagataspectratingproto);
            if(zagataspectratingproto.valueDisplay != null)
                ((RadioButton)linearlayout.findViewById(((Integer)sRatingViews.get(zagataspectratingproto.valueDisplay)).intValue())).setChecked(true);
        } while(true);
    }

    public final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.UNKNOWN;
    }

    public void onCheckedChanged(RadioGroup radiogroup, int i)
    {
        int j = radiogroup.getId();
        if(j != R.id.aspect_rating_group) {
        	if(j == R.id.bucketed_price_group)
            {
                mYourReview.priceLevel.ratedValueId = Long.valueOf(((PriceLevelProto)mPriceLevels.get(Integer.valueOf(i))).valueId.longValue());
                int k = radiogroup.getCheckedRadioButtonId();
                if(k != -1)
                {
                    PriceLevelProto pricelevelproto = (PriceLevelProto)mPriceLevels.get(Integer.valueOf(k));
                    mBucketedPriceTip.setVisibility(0);
                    mBucketedPriceTip.setText(pricelevelproto.labelHintDisplay);
                } else
                {
                    mBucketedPriceTip.setVisibility(8);
                }
            } 
        } else { 
        	((ZagatAspectRatingProto)radiogroup.getTag()).valueDisplay = (String)sRatingValues.get(Integer.valueOf(i));
        }
    }

    public void onClick(View view)
    {
        int i = view.getId();
        int count = mAspectRatings.getChildCount();
        if(i == R.id.publish_button) {
        	boolean flag = true;
        	for(int j = 0; j < count; j++) {
        		flag = false;
                int k = ((RadioGroup)mAspectRatings.getChildAt(j).findViewById(R.id.aspect_rating_group)).getCheckedRadioButtonId();
                if(k != -1) {
                	continue; 
                } else { 
                	break;
                }
        	}
        	
        	if(!flag)
            {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.write_review_submit_warning);
                builder.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {

                    public final void onClick(DialogInterface dialoginterface, int l)
                    {
                        dialoginterface.dismiss();
                    }
                });
                builder.show();
            } else
            {
                GoogleReviewProto googlereviewproto = mYourReview;
                String s = mCid;
                showProgressDialog(getString(R.string.write_review_operation_pending));
                mPendingWriteRequestId = Integer.valueOf(EsService.writeReview(getActivity(), mAccount, mPersonId, googlereviewproto, s));
            }
        } else if(i == R.id.cancel_button) {
            getActivity().finish();
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Bundle bundle1 = getArguments();
        mAccount = (EsAccount)bundle1.getParcelable("account");
        mPersonId = bundle1.getString("person_id");
        if(bundle != null)
        {
            if(bundle.containsKey("write_review_request_id"))
                mPendingWriteRequestId = Integer.valueOf(bundle.getInt("write_review_request_id"));
            if(bundle.containsKey("delete_review_request_id"))
                mPendingDeleteRequestId = Integer.valueOf(bundle.getInt("delete_review_request_id"));
        }
        getLoaderManager().initLoader(1, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        if(Log.isLoggable("WriteReviewFragment", 3))
            Log.d("WriteReviewFragment", "Loader<ProfileAndContactData> onCreateLoader()");
        return new ProfileLoader(getActivity(), mAccount, mPersonId, true);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.write_review_fragment, viewgroup, false);
        mBusinessPhoto = (EsImageView)view.findViewById(R.id.business_photo);
        mBusinessTitle = (TextView)view.findViewById(R.id.business_title);
        mBusinessAddress = (TextView)view.findViewById(R.id.business_address);
        mWriteReview = (EditText)view.findViewById(R.id.write_review);
        mAspectRatings = (LinearLayout)view.findViewById(R.id.aspect_ratings);
        mContinuousCostContainer = (LinearLayout)view.findViewById(R.id.continuous_cost_container);
        mBucketedPriceContainer = (LinearLayout)view.findViewById(R.id.bucketed_price_container);
        mBucketedPriceTip = (TextView)view.findViewById(R.id.bucketed_price_tip);
        mCostLabel = (TextView)view.findViewById(R.id.cost_label);
        mCostExplanation = (TextView)view.findViewById(R.id.cost_explanation);
        mCostCurrencySymbol = (TextView)view.findViewById(R.id.cost_currency_symbol);
        mAspectCost = (EditText)view.findViewById(R.id.aspect_cost);
        mPostingPubliclyNotice = (TextView)view.findViewById(R.id.posting_publicly_text);
        mPublishButton = (Button)view.findViewById(R.id.publish_button);
        mCancelButton = (Button)view.findViewById(R.id.cancel_button);
        mBucketedPriceGroup = (RadioGroup)view.findViewById(R.id.bucketed_price_group);
        mBucketedPrices[0] = (RadioButton)view.findViewById(R.id.bucketed_price_1);
        mBucketedPrices[1] = (RadioButton)view.findViewById(R.id.bucketed_price_2);
        mBucketedPrices[2] = (RadioButton)view.findViewById(R.id.bucketed_price_3);
        mBucketedPrices[3] = (RadioButton)view.findViewById(R.id.bucketed_price_4);
        String s = getString(R.string.write_review_publish).toUpperCase();
        mPublishButton.setText(s);
        mPublishButton.setOnClickListener(this);
        String s1 = getString(R.string.write_review_cancel).toUpperCase();
        mCancelButton.setText(s1);
        mCancelButton.setOnClickListener(this);
        mBucketedPriceGroup.setOnCheckedChangeListener(this);
        mAspectCost.addTextChangedListener(mAspectCostTextWatcher);
        mWriteReview.addTextChangedListener(mWriteReviewTextWatcher);
        return view;
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        EsPeopleData.ProfileAndContactData profileandcontactdata = (EsPeopleData.ProfileAndContactData)obj;
        mCid = EsLocalPageData.getCid(profileandcontactdata.profile);
        GoogleReviewProto googlereviewproto = EsLocalPageData.getYourReview(profileandcontactdata.profile);
        boolean flag;
        List list;
        GoogleReviewProto googlereviewproto1;
        GoogleReviewProto googlereviewproto2;
        String s;
        String s1;
        String s2;
        GoogleReviewProto googlereviewproto3;
        PriceProto priceproto;
        PriceLevelsProto pricelevelsproto;
        String s3;
        String s4;
        String s5;
        String s6;
        if(googlereviewproto != null)
            flag = true;
        else
            flag = false;
        mReviewExists = flag;
        list = EsLocalPageData.getUserActivityStory(profileandcontactdata.profile).reviewTemplate;
        if(list == null)
            googlereviewproto1 = null;
        else
            googlereviewproto1 = (GoogleReviewProto)list.get(0);
        if(googlereviewproto == null)
        {
            googlereviewproto2 = googlereviewproto1;
        } else
        {
            if(googlereviewproto1 != null)
            {
                ArrayList arraylist = new ArrayList();
                List list1 = EsLocalPageData.getZagatAspects(googlereviewproto);
                if(list1 != null)
                {
                    Iterator iterator1 = list1.iterator();
                    do
                    {
                        if(!iterator1.hasNext())
                            break;
                        ZagatAspectRatingProto zagataspectratingproto = (ZagatAspectRatingProto)iterator1.next();
                        Boolean boolean1 = zagataspectratingproto.isEditable;
                        if(boolean1 == null || !boolean1.booleanValue())
                            arraylist.add(zagataspectratingproto);
                    } while(true);
                }
                List list2 = EsLocalPageData.getZagatAspects(googlereviewproto1);
                if(list2 != null)
                {
                    for(Iterator iterator = list2.iterator(); iterator.hasNext(); arraylist.add((ZagatAspectRatingProto)iterator.next()));
                }
                if(googlereviewproto.zagatAspectRatings == null)
                    googlereviewproto.zagatAspectRatings = new ZagatAspectRatingsProto();
                googlereviewproto.zagatAspectRatings.aspectRating = arraylist;
                if(googlereviewproto.price == null)
                    googlereviewproto.price = googlereviewproto1.price;
                if(googlereviewproto.priceLevel == null)
                    googlereviewproto.priceLevel = googlereviewproto1.priceLevel;
                if(googlereviewproto.price != null)
                    googlereviewproto.priceLevel = null;
            }
            googlereviewproto2 = googlereviewproto;
        }
        mYourReview = googlereviewproto2;
        invalidateActionBar();
        s = profileandcontactdata.profile.content.photoUrl;
        s1 = profileandcontactdata.displayName;
        s2 = EsLocalPageData.getFullAddress(profileandcontactdata.profile);
        mBusinessPhoto.setUrl(s);
        mBusinessTitle.setText(s1);
        mBusinessAddress.setText(s2);
        googlereviewproto3 = mYourReview;
        updateZagatAspectViews(googlereviewproto3.zagatAspectRatings.aspectRating);
        priceproto = googlereviewproto3.price;
        pricelevelsproto = googlereviewproto3.priceLevel;
        if(priceproto == null)
        {
            if(pricelevelsproto != null)
                updateBucketedPriceViews(pricelevelsproto);
        } else
        {
            mContinuousCostContainer.setVisibility(0);
            mCostLabel.setText(priceproto.labelDisplay);
            mCostExplanation.setText(getString(R.string.write_review_optional));
            mCostCurrencySymbol.setText(priceproto.currency);
            mAspectCost.setText(priceproto.valueDisplay);
        }
        s3 = googlereviewproto3.fullText;
        s4 = googlereviewproto3.snippet;
        if(s3 == null || s3.isEmpty())
            s3 = s4;
        mWriteReview.setText(s3);
        s5 = mAccount.getDisplayName();
        s6 = getString(R.string.write_review_posting_publicly_text, new Object[] {
            s5
        });
        mPostingPubliclyNotice.setText(s6);
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == R.id.delete_review)
        {
            showProgressDialog(getString(R.string.delete_review_operation_pending));
            mPendingDeleteRequestId = Integer.valueOf(EsService.deleteReview(getActivity(), mAccount, mCid));
            flag = true;
        } else
        {
            flag = false;
        }
        return flag;
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public final void onPrepareOptionsMenu(Menu menu)
    {
        if(mReviewExists)
            menu.findItem(R.id.delete_review).setVisible(true);
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mPendingWriteRequestId != null && !EsService.isRequestPending(mPendingWriteRequestId.intValue()))
        {
            ServiceResult serviceresult1 = EsService.removeResult(mPendingWriteRequestId.intValue());
            handleWriteReviewCallback(mPendingWriteRequestId.intValue(), serviceresult1);
        }
        if(mPendingDeleteRequestId != null && !EsService.isRequestPending(mPendingDeleteRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingDeleteRequestId.intValue());
            handleDeleteReviewCallback(mPendingDeleteRequestId.intValue(), serviceresult);
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mPendingWriteRequestId != null)
            bundle.putInt("write_review_request_id", mPendingWriteRequestId.intValue());
        if(mPendingDeleteRequestId != null)
            bundle.putInt("delete_review_request_id", mPendingDeleteRequestId.intValue());
    }

    public final void recordNavigationAction()
    {
    }
    
    private final class ServiceListener extends EsServiceListener
    {

        public final void onDeleteReviewComplete(int i, ServiceResult serviceresult)
        {
            handleDeleteReviewCallback(i, serviceresult);
        }

        public final void onWriteReviewComplete(int i, ServiceResult serviceresult)
        {
            handleWriteReviewCallback(i, serviceresult);
        }


        ServiceListener()
        {
            super();
        }

    }
}
