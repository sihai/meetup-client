/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.File;
import java.util.ArrayList;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.NotificationSettingsData;

/**
 * 
 * @author sihai
 * 
 */
public class EsServiceListener {

	public EsServiceListener() {
	}

	public void onAccountActivated(int i, ServiceResult serviceresult) {
	}

	public void onAccountAdded(int i, EsAccount esaccount, ServiceResult serviceresult) {
	}

	public void onAccountUpgraded(int i, EsAccount esaccount, ServiceResult serviceresult) {
	}

	public void onAddPeopleToCirclesComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onChangeNotificationsRequestComplete(
			EsAccount esaccount, ServiceResult serviceresult) {
	}

	public void onCircleSyncComplete(int i, ServiceResult serviceresult) {
	}

	public void onCreateCircleRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onCreateComment(int i, ServiceResult serviceresult) {
	}

	public void onCreateEventComment(int i, ServiceResult serviceresult) {
	}

	public void onCreateEventComplete(int i, ServiceResult serviceresult) {
	}

	public void onCreatePhotoCommentComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onCreatePostPlusOne(ServiceResult serviceresult) {
	}

	public void onCreateProfilePlusOneRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onDeleteActivity(int i, ServiceResult serviceresult) {
	}

	public void onDeleteCirclesRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onDeleteComment(int i, ServiceResult serviceresult) {
	}

	public void onDeleteEventComplete(int i, ServiceResult serviceresult) {
	}

	public void onDeletePhotoCommentsComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onDeletePhotosComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onDeletePostPlusOne(ServiceResult serviceresult) {
	}

	public void onDeleteProfilePlusOneRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onDeleteReviewComplete(int i, ServiceResult serviceresult) {
	}

	public void onDismissSuggestedPeopleRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onEditActivity(int i, ServiceResult serviceresult) {
	}

	public void onEditComment(int i, ServiceResult serviceresult) {
	}

	public void onEditPhotoCommentComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onEditSquareMembershipComplete(int i, boolean flag,
			ServiceResult serviceresult) {
	}

	public void onEventHomeRequestComplete(int i) {
	}

	public void onEventInviteComplete(int i, ServiceResult serviceresult) {
	}

	public void onEventManageGuestComplete(int i, ServiceResult serviceresult) {
	}

	public void onGetActivities(int i, boolean flag, int j,
			ServiceResult serviceresult) {
	}

	public void onGetActivity(int i, String s,
			ServiceResult serviceresult) {
	}

	public void onGetActivityAudience(int i,
			AudienceData audiencedata, ServiceResult serviceresult) {
	}

	public void onGetAlbumComplete(int i, ServiceResult serviceresult) {
	}

	public void onGetAlbumListComplete(int i) {
	}

	public void onGetEventComplete(int i, ServiceResult serviceresult) {
	}

	public void onGetEventInviteesComplete(int i, ServiceResult serviceresult) {
	}

	public void onGetNotificationSettings(int i, EsAccount esaccount,
			NotificationSettingsData notificationsettingsdata) {
	}

	public void onGetPhoto(int i, long l) {
	}

	public void onGetPhotoSettings(int i, boolean flag) {
	}

	public void onGetPhotosOfUserComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onGetProfileAndContactComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onGetSquareComplete(int i, ServiceResult serviceresult) {
	}

	public void onGetSquaresComplete(int i, ServiceResult serviceresult) {
	}

	public void onGetStreamPhotosComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onImageThumbnailUploaded(int i, String s) {
	}

	public void onInsertCameraPhotoComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onLocalPhotoDelete(int i, ArrayList arraylist,
			ServiceResult serviceresult) {
	}

	public void onLocationQuery(int i, ServiceResult serviceresult) {
	}

	public void onModerateComment(int i, String s, boolean flag,
			ServiceResult serviceresult) {
	}

	public void onModifyCirclePropertiesRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onMutateProfileComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onMuteActivity(int i, ServiceResult serviceresult) {
	}

	public void onNameTagApprovalComplete(int i, long l,
			ServiceResult serviceresult) {
	}

	public void onOobRequestComplete(int i, ServiceResult serviceresult) {
	}

	public void onPhotoPlusOneComplete(int i, boolean flag,
			ServiceResult serviceresult) {
	}

	public void onPhotosHomeComplete(int i) {
	}

	public void onPlusOneApplyResult(int i, ServiceResult serviceresult) {
	}

	public void onPlusOneComment(boolean flag,
			ServiceResult serviceresult) {
	}

	public void onPostActivityResult(int i, ServiceResult serviceresult) {
	}

	public void onReadEventComplete(int i, ServiceResult serviceresult) {
	}

	public void onReadSquareMembersComplete(int i,
			AudienceData audiencedata, ServiceResult serviceresult) {
	}

	public void onRemovePeopleRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onReportAbuseRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onReportActivity(int i, ServiceResult serviceresult) {
	}

	public void onReportPhotoCommentsComplete(int i, String s,
			boolean flag, ServiceResult serviceresult) {
	}

	public void onReportPhotoComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onReshareActivity(int i, ServiceResult serviceresult) {
	}

	public void onSavePhoto(int i, File file, boolean flag, String s,
			String s1, ServiceResult serviceresult) {
	}

	public void onSearchActivitiesComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onSendEventRsvpComplete(int i, ServiceResult serviceresult) {
	}

	public void onSetBlockedRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onSetCircleMembershipComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onSetCoverPhotoComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onSetMutedRequestComplete(int i, boolean flag,
			ServiceResult serviceresult) {
	}

	public void onSetScrapbookInfoComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onSetVolumeControlsRequestComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onSharePhotosToEventComplete(int i, ServiceResult serviceresult) {
	}

	public void onSyncNotifications(int i, ServiceResult serviceresult) {
	}

	public void onTagSuggestionApprovalComplete(int i, String s,
			ServiceResult serviceresult) {
	}

	public void onUpdateEventComplete(int i, ServiceResult serviceresult) {
	}

	public void onUploadCoverPhotoComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onUploadProfilePhotoComplete(int i,
			ServiceResult serviceresult) {
	}

	public void onWriteReviewComplete(int i, ServiceResult serviceresult) {
	}
}
