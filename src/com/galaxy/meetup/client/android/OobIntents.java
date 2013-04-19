/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;

/**
 * 
 * @author sihai
 *
 */
public class OobIntents implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new OobIntents(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new OobIntents[i];
        }

    };
    
    private final boolean mInitial;
    private final int mStep;
    
	private OobIntents(int i, boolean flag)
    {
        mStep = i;
        mInitial = flag;
    }

    public OobIntents(Parcel parcel)
    {
        mStep = parcel.readInt();
        boolean flag;
        if(parcel.readInt() != 0)
            flag = true;
        else
            flag = false;
        mInitial = flag;
    }

    public static Intent getInitialIntent(Context context, EsAccount esaccount, MobileOutOfBoxResponse mobileoutofboxresponse, AccountSettingsData accountsettingsdata, String s)
    {
        Intent intent;
        if(mobileoutofboxresponse != null)
        {
            intent = Intents.getOutOfBoxActivityIntent(context, esaccount, new OobIntents(0, true), mobileoutofboxresponse, s);
        } else
        {
            int i = nextStep(context, esaccount, accountsettingsdata, 0);
            if(i == 5)
                intent = null;
            else
                intent = getStepIntent(context, esaccount, accountsettingsdata, new OobIntents(i, true));
        }
        return intent;
    }

    private static Intent getStepIntent(Context context, EsAccount esaccount, AccountSettingsData accountsettingsdata, OobIntents oobintents) {
    	Intent intent = null;
    	if(1 == oobintents.mStep) {
    		intent = Intents.getOobSelectPlusPageActivityIntent(context, esaccount, accountsettingsdata, oobintents);
    	} else if(2 == oobintents.mStep) {
    		intent = Intents.getOobSuggestedPeopleActivityIntent(context, esaccount, oobintents);
    	} else if (3 == oobintents.mStep) {
    		intent = Intents.getOobContactsSyncIntent(context, esaccount, oobintents);
    	} else if(4 == oobintents.mStep) {
    		intent = Intents.getOobInstantUploadIntent(context, esaccount, oobintents);
    	}
        return intent;
    }
    
    private static int nextStep(Context context, EsAccount esaccount, AccountSettingsData accountsettingsdata, int i) {
        byte byte0 = 5;
        switch(i) {
	        case 0:
	        	if(accountsettingsdata != null)
	            {
	                byte0 = 1;
	            }
	        	break;
	        case 1:
	        	if(!EsAccountsData.hasSeenWarmWelcome(context, esaccount) && !esaccount.isPlusPage())
	            {
	                byte0 = 2;
	            }
	        	break;
	        case 2:
	        	if(EsAccountsData.needContactSyncOob(context, esaccount) && !esaccount.isPlusPage())
	            {
	                byte0 = 3;
	            }
	        	break;
	        case 3:
	        	if(EsAccountsData.needInstantUploadOob(context, esaccount) && !esaccount.isPlusPage())
	        		byte0 = 4;
	            
	        	break;
	        case 4:
	        	break;
        	default:
        		break;
        }
        return byte0;
    }

    public int describeContents()
    {
        return 0;
    }

    public final Intent getNextIntent(Context context, EsAccount esaccount, AccountSettingsData accountsettingsdata)
    {
        int i = nextStep(context, esaccount, accountsettingsdata, mStep);
        Intent intent;
        if(i == 5)
        {
            intent = null;
        } else
        {
            boolean flag;
            if(mStep == 0)
                flag = true;
            else
                flag = false;
            intent = getStepIntent(context, esaccount, accountsettingsdata, new OobIntents(i, flag));
        }
        return intent;
    }

    public final boolean isInitialIntent()
    {
        return mInitial;
    }

    public final boolean isLastIntent(Context context, EsAccount esaccount, AccountSettingsData accountsettingsdata)
    {
        boolean flag;
        if(nextStep(context, esaccount, accountsettingsdata, mStep) == 5)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(mStep);
        int j;
        if(mInitial)
            j = 1;
        else
            j = 0;
        parcel.writeInt(j);
    }

}
