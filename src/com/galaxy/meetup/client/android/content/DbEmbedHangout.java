/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.galaxy.meetup.server.client.domain.EmbedsPerson;
import com.galaxy.meetup.server.client.domain.HangoutConsumer;

/**
 * 
 * @author sihai
 * 
 */
public class DbEmbedHangout extends DbSerializer {

	protected List mAttendeeAvatarUrls;
	protected List mAttendeeGaiaIds;
	protected List mAttendeeNames;
	protected String mHangoutId;
	protected String mStatus;
	protected String mYoutubeLiveId;

	protected DbEmbedHangout() {
	}

	private DbEmbedHangout(HangoutConsumer hangoutconsumer) {
		if (hangoutconsumer.startContext != null)
			mHangoutId = hangoutconsumer.startContext.hangoutId;
		else
			mHangoutId = null;
		mAttendeeGaiaIds = new ArrayList();
		mAttendeeNames = new ArrayList();
		mAttendeeAvatarUrls = new ArrayList();
		if (hangoutconsumer.attendees != null) {
			int i = 0;
			for (int j = hangoutconsumer.attendees.size(); i < j; i++) {
				EmbedsPerson embedsperson = hangoutconsumer.attendees.get(i);
				mAttendeeGaiaIds.add(embedsperson.getOwnerObfuscatedId());
				mAttendeeNames.add(embedsperson.getName());
				mAttendeeAvatarUrls.add(embedsperson.getImageUrl());
			}

		}
		mYoutubeLiveId = hangoutconsumer.youtubeLiveId;
		mStatus = hangoutconsumer.status;
	}

	public static DbEmbedHangout deserialize(byte abyte0[]) {
		DbEmbedHangout dbembedhangout;
		if (abyte0 == null) {
			dbembedhangout = null;
		} else {
			ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
			dbembedhangout = new DbEmbedHangout();
			dbembedhangout.mHangoutId = getShortString(bytebuffer);
			dbembedhangout.mAttendeeGaiaIds = (ArrayList) getShortStringList(bytebuffer);
			dbembedhangout.mAttendeeNames = (ArrayList) getShortStringList(bytebuffer);
			dbembedhangout.mAttendeeAvatarUrls = (ArrayList) getShortStringList(bytebuffer);
			dbembedhangout.mYoutubeLiveId = getShortString(bytebuffer);
			dbembedhangout.mStatus = getShortString(bytebuffer);
		}
		return dbembedhangout;
	}

	public static byte[] serialize(HangoutConsumer hangoutconsumer)
			throws IOException {
		DbEmbedHangout dbembedhangout = new DbEmbedHangout(hangoutconsumer);
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(
				256);
		DataOutputStream dataoutputstream = new DataOutputStream(
				bytearrayoutputstream);
		putShortString(dataoutputstream, dbembedhangout.mHangoutId);
		putShortStringList(dataoutputstream, dbembedhangout.mAttendeeGaiaIds);
		putShortStringList(dataoutputstream, dbembedhangout.mAttendeeNames);
		putShortStringList(dataoutputstream, dbembedhangout.mAttendeeAvatarUrls);
		putShortString(dataoutputstream, dbembedhangout.mYoutubeLiveId);
		putShortString(dataoutputstream, dbembedhangout.mStatus);
		byte abyte0[] = bytearrayoutputstream.toByteArray();
		dataoutputstream.close();
		return abyte0;
	}

	public final List getAttendeeAvatarUrls() {
		return mAttendeeAvatarUrls;
	}

	public final List getAttendeeGaiaIds() {
		return mAttendeeGaiaIds;
	}

	public final List getAttendeeNames() {
		return mAttendeeNames;
	}

	public final String getHangoutId() {
		return mHangoutId;
	}

	public final int getNumAttendees() {
		return mAttendeeGaiaIds.size();
	}

	public final String getYoutubeLiveId() {
		return mYoutubeLiveId;
	}

	public final boolean isInProgress() {
		return TextUtils.equals("ACTIVE", mStatus);
	}

	public final boolean isJoinable() {
		return TextUtils.isEmpty(mYoutubeLiveId);
	}
}
