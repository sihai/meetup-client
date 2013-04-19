/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

/**
 * 
 * @author sihai
 *
 */
public interface ResourceConsumer {

	void bindResources();

    void onResourceStatusChange(Resource resource);

    void unbindResources();
}
