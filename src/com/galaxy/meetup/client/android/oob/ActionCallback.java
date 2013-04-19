/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.oob;

import com.galaxy.meetup.server.client.domain.OutOfBoxAction;
import com.galaxy.meetup.server.client.domain.request.MobileOutOfBoxRequest;

/**
 * 
 * @author sihai
 *
 */
public interface ActionCallback {

	public abstract void onAction(OutOfBoxAction outofboxaction);

    public abstract void onActionId(String s);

    public abstract void onInputChanged();

    public abstract void sendOutOfBoxRequest(MobileOutOfBoxRequest mobileoutofboxrequest);
}
