/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import com.galaxy.meetup.server.client.domain.GenericJson;

/**
 * 
 * @author sihai
 *
 */
public final class DataNotificationSettingsDeliveryOption extends GenericJson {

	public String bucketId;
	public String category;
	public String description;
	public Boolean enabled;
	public Boolean enabledForEmail;
	public Boolean enabledForPhone;
	public String offnetworkBucketId;

	public String getBucketId() {
		return bucketId;
	}

	public void setBucketId(String bucketId) {
		this.bucketId = bucketId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getEnabledForEmail() {
		return enabledForEmail;
	}

	public void setEnabledForEmail(Boolean enabledForEmail) {
		this.enabledForEmail = enabledForEmail;
	}

	public Boolean getEnabledForPhone() {
		return enabledForPhone;
	}

	public void setEnabledForPhone(Boolean enabledForPhone) {
		this.enabledForPhone = enabledForPhone;
	}

	public String getOffnetworkBucketId() {
		return offnetworkBucketId;
	}

	public void setOffnetworkBucketId(String offnetworkBucketId) {
		this.offnetworkBucketId = offnetworkBucketId;
	}
}
