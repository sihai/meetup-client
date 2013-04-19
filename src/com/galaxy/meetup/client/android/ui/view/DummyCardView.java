/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * 
 * @author sihai
 *
 */
public class DummyCardView extends StreamCardView {

	public DummyCardView(Context context)
    {
        this(context, null);
    }

    public DummyCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        drawAuthorImage(canvas);
        int i1 = j + sAvatarSize;
        return Math.max(drawAuthorName(canvas, i + sAvatarSize, j), i1);
    }

    protected final int layoutElements(int i, int j, int k, int l)
    {
        setAuthorImagePosition(i, j);
        int _tmp = sAvatarSize;
        return createNameLayout(j, k - sAvatarSize);
    }

}
