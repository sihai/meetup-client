/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.analytics;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sihai
 */
public class AnalyticsInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Map mCustomValues;
	private final OzViews mEndView;
	private final long mStartTime;
	private final OzViews mStartView;

	AnalyticsInfo() {
		this(null, null, System.currentTimeMillis(), Collections.emptyMap());
	}

	public AnalyticsInfo(OzViews ozviews) {
		mStartView = ozviews;
		mEndView = null;
		mStartTime = System.currentTimeMillis();
		mCustomValues = new HashMap();
	}

	public AnalyticsInfo(OzViews ozviews, OzViews ozviews1, long l) {
		this(ozviews, ozviews1, l, Collections.emptyMap());
	}

	public AnalyticsInfo(OzViews ozviews, OzViews ozviews1, long l, Map map) {
		mStartView = ozviews;
		mEndView = ozviews1;
		mStartTime = l;
		mCustomValues = new HashMap(map);
	}

	public final OzViews getEndView() {
		return mEndView;
	}

	public final long getStartTimeMsec() {
		return mStartTime;
	}

	public final OzViews getStartView() {
		return mStartView;
	}
}
