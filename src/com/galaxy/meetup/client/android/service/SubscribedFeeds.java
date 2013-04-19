/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 
 * @author sihai
 *
 */
public class SubscribedFeeds {

	public static final class Feeds implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.parse("content://subscribedfeeds/feeds");
		public static final Uri DELETED_CONTENT_URI = Uri.parse("content://subscribedfeeds/deleted_feeds");

	}
}
