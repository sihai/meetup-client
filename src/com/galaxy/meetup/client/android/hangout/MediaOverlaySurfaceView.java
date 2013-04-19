/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * 
 * @author sihai
 *
 */
public class MediaOverlaySurfaceView extends SurfaceView {

	public MediaOverlaySurfaceView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        setZOrderMediaOverlay(true);
    }
}
