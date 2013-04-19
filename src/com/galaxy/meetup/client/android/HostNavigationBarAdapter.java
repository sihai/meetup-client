/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.common.EsCompositeCursorAdapter;
import com.galaxy.meetup.client.android.content.DbDataAction;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.EsNotificationData;
import com.galaxy.meetup.client.android.ui.view.AvatarView;
import com.galaxy.meetup.server.client.domain.DataAction;
import com.galaxy.meetup.server.client.domain.DataActor;
import com.galaxy.meetup.server.client.domain.DataItem;

/**
 * 
 * @author sihai
 *
 */
public class HostNavigationBarAdapter extends EsCompositeCursorAdapter {

	private static final String DESTINATIONS_PROJECTION[] = {
        "id", "icon", "text", "gaia_id"
    };
    private static MatrixCursor sEmptyCursor;
    private boolean mCollapsed;
    private MatrixCursor mCollapsedCursor;
    private int mCollapsedMenuItemCount;
    private int mColorRead;
    private int mColorUnread;
    private	List mDestinationRows;
    private MatrixCursor mDestinationsCursor;
    private LayoutInflater mInflater;
    private View mNotificationProgressIndicator;
    private View mNotificationRefreshButton;
    private int mUnreadNotificationCount;
    
    public HostNavigationBarAdapter(Context context) {
        super(context);
        mDestinationRows = new ArrayList();
        mCollapsed = true;
        mInflater = LayoutInflater.from(context);
        addPartition(false, false);
        addPartition(true, true);
        addPartition(false, false);
        Resources resources = context.getResources();
        mColorRead = resources.getColor(R.color.notifications_text_color_read);
        mColorUnread = resources.getColor(R.color.notifications_text_color_unread);
    }
    
    public final void removeAllDestinations()
    {
        mDestinationsCursor = new MatrixCursor(DESTINATIONS_PROJECTION);
        mDestinationRows.clear();
        mCollapsedCursor = null;
    }

    public final void setCollapsed(boolean flag)
    {
        if(mCollapsed != flag)
        {
            mCollapsed = flag;
            showDestinations();
        }
    }

    public final void setCollapsedMenuItemCount(int i)
    {
        if(mCollapsedMenuItemCount != i)
        {
            mCollapsedMenuItemCount = i;
            mCollapsedCursor = null;
            showDestinations();
        }
    }

    public final void setNotifications(Cursor cursor)
    {
        changeCursor(1, cursor);
        boolean flag;
        MatrixCursor matrixcursor;
        if(cursor == null || cursor.getCount() == 0)
            flag = true;
        else
            flag = false;
        if(flag)
        {
            if(sEmptyCursor == null)
            {
                MatrixCursor matrixcursor1 = new MatrixCursor(new String[] {
                    "empty"
                });
                sEmptyCursor = matrixcursor1;
                matrixcursor1.addRow(new Object[] {
                    "empty"
                });
            }
            matrixcursor = sEmptyCursor;
        } else
        {
            matrixcursor = null;
        }
        changeCursor(2, matrixcursor);
        mUnreadNotificationCount = 0;
        if(cursor != null && cursor.moveToFirst())
            do
                if(cursor.getInt(11) != 1)
                    mUnreadNotificationCount = 1 + mUnreadNotificationCount;
            while(cursor.moveToNext());
    }

    public final void showDestinations()
    {
        if(mCollapsed && mDestinationsCursor != null && mCollapsedMenuItemCount != 0 && mDestinationsCursor.getCount() > mCollapsedMenuItemCount)
        {
            if(mCollapsedCursor == null)
            {
                mCollapsedCursor = new MatrixCursor(DESTINATIONS_PROJECTION);
                for(int i = 0; i < -1 + mCollapsedMenuItemCount; i++)
                    mCollapsedCursor.addRow((Object[])mDestinationRows.get(i));

                String s = getContext().getString(R.string.expand_menu_label);
                MatrixCursor matrixcursor = mCollapsedCursor;
                Object aobj[] = new Object[4];
                aobj[0] = Integer.valueOf(-2);
                aobj[1] = Integer.valueOf(R.drawable.ic_down_white);
                aobj[2] = s;
                aobj[3] = null;
                matrixcursor.addRow(aobj);
            }
            changeCursor(0, mCollapsedCursor);
        } else
        {
            changeCursor(0, mDestinationsCursor);
        }
    }

    public final void showProgressIndicator() {
        if(mNotificationProgressIndicator != null)
            mNotificationProgressIndicator.setVisibility(0);
        if(mNotificationRefreshButton != null)
            mNotificationRefreshButton.setVisibility(8);
    }
    
    private static void bindNotificationUserAvatar(Cursor cursor, AvatarView avatarview, ImageView imageview, boolean flag) {
        byte abyte0[] = cursor.getBlob(6);
        String s = null;
        String s1 = null;
        if(abyte0 != null)
        {
            List list = DbDataAction.deserializeDataActionList(abyte0);
            s = null;
            s1 = null;
            if(list != null)
            {
                boolean flag1 = list.isEmpty();
                s = null;
                s1 = null;
                if(!flag1)
                {
                    DataAction dataaction = (DataAction)list.get(0);
                    s = null;
                    s1 = null;
                    if(dataaction != null)
                    {
                        List list1 = dataaction.item;
                        s = null;
                        s1 = null;
                        if(list1 != null)
                        {
                            boolean flag2 = dataaction.item.isEmpty();
                            s = null;
                            s1 = null;
                            if(!flag2)
                            {
                                DataItem dataitem = (DataItem)dataaction.item.get(0);
                                s = null;
                                s1 = null;
                                if(dataitem != null)
                                {
                                    DataActor dataactor = dataitem.actor;
                                    s = null;
                                    s1 = null;
                                    if(dataactor != null)
                                    {
                                        DataActor dataactor1 = dataitem.actor;
                                        s1 = dataactor1.obfuscatedGaiaId;
                                        s = EsAvatarData.uncompressAvatarUrl(dataactor1.photoUrl);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        avatarview.setRounded(true);
        avatarview.setGaiaIdAndAvatarUrl(s1, s);
        imageview.setVisibility(8);
        avatarview.setVisibility(0);
        avatarview.setDimmed(flag);
    }
    
    public final void addDestination(int i, int j, int k) {
        addDestination(i, j, mContext.getResources().getText(k), null);
    }

    public final void addDestination(int i, int j, CharSequence charsequence, String s) {
        Object aobj[] = new Object[4];
        aobj[0] = Integer.valueOf(i);
        aobj[1] = Integer.valueOf(j);
        aobj[2] = charsequence;
        aobj[3] = s;
        mDestinationRows.add(((Object) (aobj)));
        mDestinationsCursor.addRow(aobj);
    }
    
    protected final void bindHeaderView(View view) {
        TextView textview = (TextView)view.findViewById(R.id.text);
        View view1 = view.findViewById(R.id.separator);
        mNotificationRefreshButton = view.findViewById(R.id.refresh_button);
        mNotificationProgressIndicator = view.findViewById(R.id.progress_indicator);
        Resources resources = getContext().getResources();
        int i;
        int j;
        if(mUnreadNotificationCount > 0)
            i = R.color.notifications_header_has_messages;
        else
            i = R.color.notifications_header_no_messages;
        j = resources.getColor(i);
        textview.setTextColor(j);
        view1.setBackgroundColor(j);
    }
    
    protected final void bindView(View view, int i, Cursor cursor, int j) {
    	int k = 1;
    	String gaiaId;
    	switch(i) {
    	case 0:
    		int k1 = cursor.getInt(k);
    		gaiaId = cursor.getString(3);
            ImageView imageview1 = (ImageView)view.findViewById(R.id.icon);
            AvatarView avatarview1 = (AvatarView)view.findViewById(R.id.avatar);
            if(gaiaId != null) {
                avatarview1.setGaiaId(gaiaId);
                avatarview1.setVisibility(0);
                imageview1.setVisibility(8);
            } else {
                imageview1.setImageResource(k1);
                imageview1.setVisibility(0);
                avatarview1.setVisibility(8);
            }
            ((TextView)view.findViewById(R.id.text)).setText(cursor.getString(2));
            break;
    	case 1:
    		boolean flag;
            ImageView imageview;
            AvatarView avatarview;
            int i1;
            boolean flag1;
            TextView textview;
            if(cursor.getInt(11) == k)
                flag = true;
            else
                flag = false;
            imageview = (ImageView)view.findViewById(0x1020006);
            avatarview = (AvatarView)view.findViewById(R.id.avatar);
            imageview.setVisibility(0);
            if(!flag)
                flag1 = true;
            else
                flag1 = false;
            imageview.setEnabled(flag1);
            avatarview.setVisibility(8);
            if(cursor.getInt(12) != k)
                k = 0;
            int l = cursor.getInt(3);
            i1 = cursor.getInt(15);
            
            switch(l) {
            case 1:
            	if(k != 0 || EsNotificationData.isEventNotificationType(i1))
                    imageview.setImageResource(R.drawable.ic_notification_event);
                else
                if(EsNotificationData.isCommentNotificationType(i1))
                    imageview.setImageResource(R.drawable.ic_notification_comment);
                else
                    imageview.setImageResource(R.drawable.ic_notification_post);
            	break;
            case 2:
            	bindNotificationUserAvatar(cursor, avatarview, imageview, flag);
            	break;
            case 3:
            	imageview.setImageResource(R.drawable.ic_notification_photo);
            	break;
            case 4:
            	imageview.setImageResource(R.drawable.ic_notification_games);
            	break;
            case 5:
            	imageview.setImageResource(R.drawable.ic_notification_alert);
            	break;
            case 6:
            	break;
            case 7:
            	break;
            case 8:
            	imageview.setImageResource(R.drawable.ic_notification_post);
            	break;
            case 9:
            	break;
            case 10:
            	imageview.setImageResource(R.drawable.ic_notification_event);
            	break;
            case 11:
            	if(i1 == 48) {
                    bindNotificationUserAvatar(cursor, avatarview, imageview, flag);
                } else {
                    avatarview.setRounded(false);
                    avatarview.setGaiaIdAndAvatarUrl(cursor.getString(20), EsAvatarData.uncompressAvatarUrl(cursor.getString(22)));
                    imageview.setVisibility(8);
                    avatarview.setVisibility(0);
                    avatarview.setDimmed(flag);
                }
            	break;
            default:
            	break;
            }
            
            textview = (TextView)view.findViewById(0x1020014);
            textview.setText(cursor.getString(4));
            if(flag)
            	textview.setTextColor(mColorRead);
            else
            	textview.setTextColor(mColorUnread);
            view.setContentDescription(textview.getText());
            break;
    	default:
    		break;	
    	}
    	
    	return;
    }

	public final int getDestinationId(int i) {
		if (getPartitionForPosition(i) != 0) {
			return -1;
		}
		Cursor cursor = (Cursor) getItem(i);
		if (cursor == null) {
			return -1;
		}
		return cursor.getInt(0);

	}

    protected final int getItemViewType(int i, int j) {
        return i;
    }

    public final int getItemViewTypeCount() {
        return 3;
    }

    public final int getUnreadNotificationCount() {
        return mUnreadNotificationCount;
    }

    public final void hideProgressIndicator() {
        if(mNotificationRefreshButton != null)
            mNotificationRefreshButton.setVisibility(0);
        if(mNotificationProgressIndicator != null)
            mNotificationProgressIndicator.setVisibility(8);
    }

    public final boolean isCollapsed() {
        return mCollapsed;
    }

    public final boolean isEnabled(int i) {
        boolean flag;
        if(super.isEnabled(i))
            flag = true;
        else
            flag = isNotificationHeader(i);
        return flag;
    }

    protected final boolean isEnabledPartition(int i) {
        boolean flag = true;
        if(i != 0 && i != 1)
            flag = false;
        return flag;
    }

    public final boolean isNotificationHeader(int i) {
        boolean flag = true;
        if(getPartitionForPosition(i) != 1 || getOffsetInPartition(i) != -1)
            flag = false;
        return flag;
    }

    @Override
    protected final View newHeaderView(Context context, int partion, Cursor curosr, ViewGroup viewgroup) {
        return mInflater.inflate(R.layout.host_notification_header, viewgroup, false);
    }
    
    protected final View newView(Context context, int partition, Cursor cursor, int position, ViewGroup parent) {
    	View view = null;
    	switch(partition) {
	    	case 0:
	    		view = mInflater.inflate(R.layout.host_navigation_item, parent, false);
	    		break;
	    	case 1:
	    		view = mInflater.inflate(R.layout.notification_row_view, parent, false);
	    		break;
	    	case 2:
	    		 view = mInflater.inflate(R.layout.no_notifications, parent, false);
	    		 break;
	    	default:
	    		view = null;
	    		break;
    	}
    	return view;
    }
}
