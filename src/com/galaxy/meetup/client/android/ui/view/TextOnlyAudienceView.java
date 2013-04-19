/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.content.SquareTargetData;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class TextOnlyAudienceView extends AudienceView {

	private View mAudienceHint;
    private ImageView mAudienceIcon;
    private ConstrainedTextView mAudienceNames;
    private ChevronDirection mChevronDirection;
    private ImageView mChevronIcon;
    
    public TextOnlyAudienceView(Context context)
    {
        this(context, null);
    }

    public TextOnlyAudienceView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public TextOnlyAudienceView(Context context, AttributeSet attributeset, int i)
    {
        this(context, attributeset, i, false);
    }

    private TextOnlyAudienceView(Context context, AttributeSet attributeset, int i, boolean flag)
    {
        super(context, attributeset, i, false);
        mChevronDirection = ChevronDirection.POINT_DOWN;
    }

    protected final void addChip(int i)
    {
    }

    protected final int getChipCount()
    {
        return 0;
    }

    protected final void init()
    {
        addView(inflate(R.layout.audience_view_text_only));
        mAudienceNames = (ConstrainedTextView)findViewById(R.id.audience_names_container);
        mAudienceIcon = (ImageView)findViewById(R.id.audience_to_icon);
        mChevronIcon = (ImageView)findViewById(R.id.chevron_icon);
        mAudienceHint = findViewById(R.id.audience_to_text);
        update();
    }

    protected final void removeLastChip()
    {
    }

    public void setChevronDirection(ChevronDirection chevrondirection) {

       // TODO
    }

    public void setChevronVisibility(int i)
    {
        if(mChevronIcon != null)
            mChevronIcon.setVisibility(i);
    }

    protected final void update() {
        int i;
        Resources resources;
        String s;
        String s1;
        String s2;
        String s3;
        StringBuilder stringbuilder;
        i = mChips.size();
        resources = getResources();
        s = resources.getString(R.string.compose_acl_separator);
        s1 = resources.getString(0x104000e);
        s2 = resources.getString(R.string.loading);
        s3 = resources.getString(R.string.square_unknown);
        stringbuilder = new StringBuilder();
        
        if(0 == i) {
        	int l;
            byte byte0;
            l = 0;
            byte0 = 8;
            
            mAudienceNames.setText(stringbuilder.toString());
            mAudienceIcon.setVisibility(byte0);
            mAudienceHint.setVisibility(l);
            if(mAudienceChangedCallback != null)
                mAudienceChangedCallback.run();
            return;
        }
        
        // TODO
    }

    protected final void updateChip(int i, int j, int k, String s, Object obj, boolean flag)
    {
    }
    
    
	public static enum ChevronDirection {

		POINT_RIGHT, POINT_LEFT, POINT_UP, POINT_DOWN;
	}
}
