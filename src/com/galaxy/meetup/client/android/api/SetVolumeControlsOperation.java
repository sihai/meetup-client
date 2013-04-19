/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.VolumeControlKey;
import com.galaxy.meetup.server.client.domain.VolumeControlMap;
import com.galaxy.meetup.server.client.domain.VolumeControlPair;
import com.galaxy.meetup.server.client.domain.request.SetVolumeControlsRequest;
import com.galaxy.meetup.server.client.domain.response.SetVolumeControlsResponse;

/**
 * 
 * @author sihai
 *
 */
public class SetVolumeControlsOperation extends PlusiOperation {

	private String mCircleId;
    private HashMap mCircleToVolumeMap;
    private int mVolume;
    
	public SetVolumeControlsOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, int i)
    {
        super(context, esaccount, "setvolumecontrols", intent, operationlistener, SetVolumeControlsResponse.class);
        mCircleId = s;
        mVolume = i;
    }

    public SetVolumeControlsOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, HashMap hashmap)
    {
        super(context, esaccount, "setvolumecontrols", intent, operationlistener, SetVolumeControlsResponse.class);
        mCircleToVolumeMap = hashmap;
    }

    private static String volumeIntToString(int i)
    {
    	String s = null;
    	switch(i) {
	    	case 0:
	    		s = "NONE";
	    		break;
	    	case 1:
	    		s = "LESS";
	    		break;
	    	case 2:
	    		s = "NORMAL";
	    		break;
	    	case 3:
	    		s = "MORE";
	    		break;
	    	case 4:
	    		s = "NOTIFY";
	    		break;
    		default:
    			break;
    	}
    	return s;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        if(!((SetVolumeControlsResponse)genericjson).value.booleanValue())
            throw new IllegalStateException("SetVolumeControls: unexpected server failure.");
        if(mCircleToVolumeMap != null)
            EsPeopleData.setCircleVolumes(mContext, mAccount, mCircleToVolumeMap);
        else
            EsPeopleData.setCircleVolume(mContext, mAccount, mCircleId, mVolume);
    }

    protected final GenericJson populateRequest()
    {
        SetVolumeControlsRequest setvolumecontrolsrequest = new SetVolumeControlsRequest();
        ArrayList arraylist = new ArrayList();
        if(mCircleToVolumeMap != null)
        {
            VolumeControlPair volumecontrolpair1;
            for(Iterator iterator = mCircleToVolumeMap.entrySet().iterator(); iterator.hasNext(); arraylist.add(volumecontrolpair1))
            {
                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
                String s = (String)entry.getKey();
                Integer integer = (Integer)entry.getValue();
                volumecontrolpair1 = new VolumeControlPair();
                volumecontrolpair1.key = new VolumeControlKey();
                volumecontrolpair1.key.focusGroupId = EsPeopleData.getFocusCircleId(s);
                volumecontrolpair1.key.type = "CIRCLE";
                volumecontrolpair1.value = volumeIntToString(integer.intValue());
            }

        } else
        {
            VolumeControlPair volumecontrolpair = new VolumeControlPair();
            volumecontrolpair.key = new VolumeControlKey();
            volumecontrolpair.key.focusGroupId = EsPeopleData.getFocusCircleId(mCircleId);
            volumecontrolpair.key.type = "CIRCLE";
            volumecontrolpair.value = volumeIntToString(mVolume);
            arraylist.add(volumecontrolpair);
        }
        setvolumecontrolsrequest.values = new VolumeControlMap();
        setvolumecontrolsrequest.values.setting = arraylist;
        return setvolumecontrolsrequest;
    }

}
