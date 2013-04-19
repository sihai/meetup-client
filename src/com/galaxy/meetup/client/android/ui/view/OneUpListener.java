/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.List;

import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.DbPlusOneData;
import com.galaxy.meetup.client.android.ui.view.ClickableStaticLayout.SpanClickListener;
import com.galaxy.meetup.client.android.ui.view.ClickableUserImage.UserImageClickListener;

/**
 * 
 * @author sihai
 *
 */
public interface OneUpListener extends SpanClickListener, UserImageClickListener {

	public abstract void onLocationClick(DbLocation dblocation);

    public abstract void onPlaceClick(String s);

    public abstract void onPlusOne(String s, DbPlusOneData dbplusonedata);

    public abstract void onSkyjamBuyClick(String s);

    public abstract void onSkyjamListenClick(String s);

    public abstract void onSourceAppContentClick(String s, List list, String s1, String s2, String s3);

    public abstract void onSquareClick(String s, String s1);

}
