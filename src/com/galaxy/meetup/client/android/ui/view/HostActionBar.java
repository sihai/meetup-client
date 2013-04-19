/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.AccessibilityUtils;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 * 
 */
public class HostActionBar extends RelativeLayout implements View.OnClickListener,
		View.OnLongClickListener, AdapterView.OnItemSelectedListener {

	private static Handler sHandler;
    private ImageView mActionButton1;
    private boolean mActionButton1Visible;
    private ImageView mActionButton2;
    private boolean mActionButton2Visible;
    private int mActionId1;
    private int mActionId2;
    private boolean mActive;
    private ImageView mAppIcon;
    private boolean mContextActionMode;
    private String mCurrentButtonActionText;
    private SpinnerAdapter mDefaultPrimarySpinnerAdapter;
    private View mDoneButton;
    private OnDoneButtonClickListener mDoneButtonListener;
	private Runnable mInvalidateActionBarRunnable = new Runnable() {

		public final void run() {
			if (mListener != null)
				mListener.onActionBarInvalidated();
		}
	};
    private HostActionBarListener mListener;
    private int mNotificationCount;
    private View mNotificationCountOverflow;
    private TextView mNotificationCountText;
    private View mOverflowMenuButton;
    private PopupMenu mOverflowPopupMenu;
    private boolean mOverflowPopupMenuVisible;
    private Spinner mPrimarySpinner;
    private View mPrimarySpinnerContainer;
    private boolean mPrimarySpinnerVisible;
    private View mProgressIndicator;
    private boolean mProgressIndicatorVisible;
    private ImageView mRefreshButton;
    private boolean mRefreshButtonVisible;
    private boolean mRefreshButtonVisibleIfRoom;
    private boolean mRefreshHighlighted;
    private SearchViewAdapter mSearchViewAdapter;
    private View mSearchViewContainer;
    private boolean mSearchViewVisible;
    private View mShareMenuButton;
    private boolean mShareMenuVisible;
    private PopupMenu mSharePopupMenu;
    private boolean mSharePopupMenuVisible;
    private TextView mTitle;
    private boolean mTitleVisible;
    private View mUpButton;
    private OnUpButtonClickListener mUpButtonListener;
	
    //===========================================================================
    //						Constructor
    //===========================================================================
	public HostActionBar(Context context) {
		super(context);
	}

	public HostActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HostActionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	//===========================================================================
    //						Private function
    //===========================================================================
	private void configurePopupMenuListeners(PopupMenu popupMenu) {
		Object listener;
		if (Build.VERSION.SDK_INT >= 14) {
			listener = new PopupMenuListenerV14();
			popupMenu.setOnDismissListener((PopupMenuListenerV14) listener);
		} else {
			listener = new PopupMenuListener();
		}
		popupMenu.setOnMenuItemClickListener(((PopupMenu.OnMenuItemClickListener) (listener)));
	}
	
	private PopupMenu getOverflowPopupMenu() {
		if (mOverflowPopupMenu == null) {
			mOverflowPopupMenu = new PopupMenu(getContext(), mOverflowMenuButton);
			configurePopupMenuListeners(mOverflowPopupMenu);
			((Activity) getContext()).onCreateOptionsMenu(mOverflowPopupMenu.getMenu());
		}
		return mOverflowPopupMenu;
	}
	
	private boolean isOverflowMenuSupported() {
		boolean flag = true;
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (android.os.Build.VERSION.SDK_INT < 11) {
				return false;
			} else {
				return true;
			}
		} else {
			return !ViewConfiguration.get(getContext()).hasPermanentMenuKey();
		}
	}

	private boolean prepareOverflowMenu() {
		if(null == mOverflowPopupMenu) {
			return false;
		} else {
			boolean flag = false;
			Menu menu = ((PopupMenu)mOverflowPopupMenu).getMenu();
	        ((Activity)getContext()).onPrepareOptionsMenu(menu);
	        int i = menu.size();
	        for(int j = 0; j < i; j++) {
	        	if(menu.getItem(j).isVisible()) {
	        		flag = true;
	        		break;
	        	}
	        }
	        return flag;
		}
	}
    
	private boolean prepareSharePopupMenu() {
		boolean flag;
		if (mSharePopupMenu == null) {
			flag = false;
		} else {
			Menu menu = ((PopupMenu) mSharePopupMenu).getMenu();
			((Activity) getContext()).onPrepareOptionsMenu(menu);
			flag = false;
			int i = menu.size();
			int j = 0;
			for (; j < i; j++) {
				MenuItem menuitem = menu.getItem(j);
				flag = true;
				menuitem.setVisible(false);
			}
		}
        return flag;
    }
	
	private void showOverflowMenu() {
		mSharePopupMenuVisible = false;
		PopupMenu popupmenu = getOverflowPopupMenu();
		prepareOverflowMenu();
		mOverflowPopupMenuVisible = true;
		popupmenu.show();
	}

	private void showSharePopupMenu() {
		mOverflowPopupMenuVisible = false;
		if (mSharePopupMenu == null) {
			mSharePopupMenu = new PopupMenu(getContext(), mShareMenuButton);
			configurePopupMenuListeners(mSharePopupMenu);
			((Activity) getContext()).onCreateOptionsMenu(mSharePopupMenu
					.getMenu());
		}

		if (prepareSharePopupMenu()) {
			mSharePopupMenuVisible = true;
			mSharePopupMenu.show();
		}
	}
	
	private boolean showTooltip(View view, CharSequence charsequence) {
		int ai[] = new int[2];
		Rect rect = new Rect();
		view.getLocationOnScreen(ai);
		view.getWindowVisibleDisplayFrame(rect);
		Context context = getContext();
		int i = view.getWidth();
		int j = view.getHeight();
		int k = context.getResources().getDisplayMetrics().widthPixels;
		Toast toast = Toast.makeText(context, charsequence, 0);
		toast.setGravity(53, k - ai[0] - i / 2, j);
		toast.show();
		return true;
	}
	
	//===========================================================================
    //						Public function
    //===========================================================================
	public final void commit() {
        
        if(mContextActionMode)
        	mUpButton.setVisibility(View.GONE);
        else
        	mUpButton.setVisibility(View.VISIBLE);
        
        if(mContextActionMode)
        	mDoneButton.setVisibility(View.VISIBLE);
        else
        	mDoneButton.setVisibility(View.GONE);
        
        if(mTitleVisible)
        	mTitle.setVisibility(View.VISIBLE);
        else
        	mTitle.setVisibility(View.GONE);
        
        if(mPrimarySpinnerVisible && mPrimarySpinner.getAdapter().getCount() > 0)
        	mPrimarySpinnerContainer.setVisibility(View.VISIBLE);
        else
        	mPrimarySpinnerContainer.setVisibility(View.GONE);
        
        if(!mPrimarySpinnerVisible)
            mPrimarySpinner.setAdapter(mDefaultPrimarySpinnerAdapter);
        
        if(mSearchViewVisible)
        	mSearchViewContainer.setVisibility(View.VISIBLE);
        else
        	mSearchViewContainer.setVisibility(View.GONE);
        
        mSearchViewAdapter.setVisible(mSearchViewVisible);
        
        if(isRefreshButtonVisible() && !mProgressIndicatorVisible)
        	mRefreshButton.setVisibility(View.VISIBLE);
        else
        	mRefreshButton.setVisibility(View.GONE);
        
        Resources resources = getResources();
        if(mRefreshHighlighted)
        	mRefreshButton.setImageDrawable(resources.getDrawable(R.drawable.ic_refresh_blue));
        else
        	mRefreshButton.setImageDrawable(resources.getDrawable(R.drawable.ic_refresh));
        
        if(mProgressIndicatorVisible)
        	mProgressIndicator.setVisibility(View.VISIBLE);
        else
        	mProgressIndicator.setVisibility(View.GONE);
        
        if(mActionButton1Visible)
        	mActionButton1.setVisibility(View.VISIBLE);
        else
        	mActionButton1.setVisibility(View.GONE);
        
        if(mActionButton2Visible)
        	mActionButton2.setVisibility(View.VISIBLE);
        else
        	mActionButton2.setVisibility(View.GONE);
        
        mShareMenuButton.setVisibility(View.GONE);
        
        if(isOverflowMenuSupported()) {
            if(mSharePopupMenuVisible)
                prepareSharePopupMenu();
            else if(mOverflowPopupMenuVisible) {
                prepareOverflowMenu();
            } else {
                View view5 = mOverflowMenuButton;
                getOverflowPopupMenu();
                boolean flag = prepareOverflowMenu();
                if(!flag)
                	mOverflowMenuButton.setVisibility(View.GONE);
                else
                	mOverflowMenuButton.setVisibility(View.VISIBLE);
                
            }
        }
        mActive = true;
    }
	
	public final void dismissPopupMenus() {
		if (mOverflowPopupMenu != null)
			mOverflowPopupMenu.dismiss();
		if (mSharePopupMenu != null)
			mSharePopupMenu.dismiss();
	}

	public final void finishContextActionMode() {
		if (mContextActionMode) {
			mContextActionMode = false;
			if (mActive) {
				mUpButton.setVisibility(View.VISIBLE);
				mDoneButton.setVisibility(View.GONE);
			}
		}
	}

	public final SearchViewAdapter getSearchViewAdapter() {
		return mSearchViewAdapter;
	}

	public final void hideProgressIndicator() {
		mProgressIndicatorVisible = false;
		if (mActive) {
			mProgressIndicator.setVisibility(View.GONE);
			if (isRefreshButtonVisible())
				mRefreshButton.setVisibility(View.VISIBLE);
			else
				mRefreshButton.setVisibility(View.GONE);

			if (mActionButton1Visible)
				mActionButton1.setVisibility(View.VISIBLE);
			else
				mActionButton1.setVisibility(View.GONE);

			if (mActionButton2Visible) {
				mActionButton2.setVisibility(View.VISIBLE);
			} else {
				mActionButton2.setVisibility(View.GONE);
			}

		}
	}

	public final void invalidateActionBar() {
		if (sHandler == null)
			sHandler = new Handler(Looper.getMainLooper());
		sHandler.removeCallbacks(mInvalidateActionBarRunnable);
		sHandler.post(mInvalidateActionBarRunnable);
	}

	public final boolean isRefreshButtonVisible() {
		boolean flag = true;
		if (mRefreshButtonVisible) {
			return flag;
		}

		if (mRefreshButtonVisibleIfRoom) {
			if (mActionButton1Visible) {
				ScreenMetrics screenmetrics = ScreenMetrics
						.getInstance(getContext());
				boolean flag1;
				if (getResources().getConfiguration().orientation == 2)
					flag1 = flag;
				else
					flag1 = false;
				if (screenmetrics.screenDisplayType == 0 && !flag1)
					flag = false;
			}
		} else {
			flag = false;
		}
		return flag;
	}

	public void onClick(View view) {
		if (view == mUpButton && mUpButtonListener != null) {
			mUpButtonListener.onUpButtonClick();
		} else if (view == mDoneButton && mDoneButtonListener != null) {
			mDoneButtonListener.onDoneButtonClick();
		} else if (view == mOverflowMenuButton)
			showOverflowMenu();
		else if (view == mShareMenuButton)
			showSharePopupMenu();
		else if (view == mRefreshButton && mListener != null) {
			mListener.onRefreshButtonClicked();
		} else if (view == mActionButton1 && mListener != null) {
			mListener.onActionButtonClicked(mActionId1);
		} else if (view == mActionButton2 && mListener != null)
			mListener.onActionButtonClicked(mActionId2);
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		mUpButton = findViewById(R.id.up);
		mUpButton.setOnClickListener(this);
		mDoneButton = findViewById(R.id.done);
		mDoneButton.setOnClickListener(this);
		mAppIcon = (ImageView) findViewById(R.id.icon);
		mTitle = (TextView) findViewById(R.id.title);
		mPrimarySpinnerContainer = findViewById(R.id.primary_spinner_container);
		mPrimarySpinner = (Spinner) findViewById(R.id.primary_spinner);
		mPrimarySpinner.setOnItemSelectedListener(this);
		mDefaultPrimarySpinnerAdapter = new ArrayAdapter(getContext(),
				R.layout.simple_spinner_item);
		mPrimarySpinner.setAdapter(mDefaultPrimarySpinnerAdapter);
		mSearchViewContainer = findViewById(R.id.search_view_container);
		mSearchViewAdapter = SearchViewAdapter
				.createInstance(findViewById(R.id.search_src_text));
		mSearchViewAdapter.requestFocus(false);
		mShareMenuButton = findViewById(R.id.share_menu_anchor);
		if (isOverflowMenuSupported()) {
			mShareMenuButton.setOnClickListener(this);
			mShareMenuButton.setOnLongClickListener(this);
		}
		mRefreshButton = (ImageView) findViewById(R.id.refresh_button);
		mRefreshButton.setOnClickListener(this);
		mRefreshButton.setOnLongClickListener(this);
		mActionButton1 = (ImageView) findViewById(R.id.action_button_1);
		mActionButton1.setOnClickListener(this);
		mActionButton1.setOnLongClickListener(this);
		mActionButton2 = (ImageView) findViewById(R.id.action_button_2);
		mActionButton2.setOnClickListener(this);
		mActionButton2.setOnLongClickListener(this);
		mProgressIndicator = findViewById(R.id.progress_indicator);
		mNotificationCountText = (TextView) findViewById(R.id.notification_count);
		mNotificationCountText.setText("99");
		mNotificationCountText.setVisibility(View.GONE);
		mNotificationCountOverflow = findViewById(R.id.notification_count_overflow);
		mOverflowMenuButton = findViewById(R.id.menu);
		if (isOverflowMenuSupported())
			mOverflowMenuButton.setOnClickListener(this);
		else
			mOverflowMenuButton.setVisibility(View.GONE);
		mCurrentButtonActionText = "";
	}

	public void onItemSelected(AdapterView adapterview, View view, int i, long l) {
		if (mListener != null)
			mListener.onPrimarySpinnerSelectionChange(i);
	}

	public boolean onLongClick(View view) {
		boolean flag = true;
		if (view == mShareMenuButton)
			showTooltip(
					view,
					getContext().getString(
							R.string.share_menu_anchor_content_description));
		else if (view == mRefreshButton)
			showTooltip(view, getContext().getString(R.string.menu_refresh));
		else if (view == mActionButton1)
			showTooltip(view, mActionButton1.getContentDescription());
		else if (view == mActionButton2)
			showTooltip(view, mActionButton2.getContentDescription());
		else
			flag = false;
		return flag;
	}

	public void onNothingSelected(AdapterView adapterview) {
	}

	public void onRestoreInstanceState(Parcelable parcelable) {
		SavedState savedstate = (SavedState) parcelable;
		super.onRestoreInstanceState(savedstate.getSuperState());
		if (savedstate.overflowPopupMenuVisible)
			post(new Runnable() {

				public final void run() {
					showOverflowMenu();
				}

			});
		if (savedstate.sharePopupMenuVisible)
			post(new Runnable() {

				public final void run() {
					showSharePopupMenu();
				}

			});
	}

	public Parcelable onSaveInstanceState() {
		SavedState savedstate = new SavedState(super.onSaveInstanceState());
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			savedstate.overflowPopupMenuVisible = mOverflowPopupMenuVisible;
			savedstate.sharePopupMenuVisible = mSharePopupMenuVisible;
		}
		return savedstate;
	}

	public final void reset() {
		mActive = false;
		mContextActionMode = false;
		mTitleVisible = false;
		mPrimarySpinnerVisible = false;
		mSearchViewVisible = false;
		mRefreshButtonVisible = false;
		mRefreshButtonVisibleIfRoom = false;
		mActionButton1Visible = false;
		mActionButton2Visible = false;
		mProgressIndicatorVisible = false;
		mShareMenuVisible = false;
		mRefreshHighlighted = false;
	}

	public void setHostActionBarListener(HostActionBarListener hostactionbarlistener) {
		mListener = hostactionbarlistener;
	}

	public void setNotificationCount(int i) {
		mNotificationCount = i;
		if (mNotificationCount == 0) {
			mAppIcon.setVisibility(View.VISIBLE);
			mNotificationCountText.setVisibility(View.GONE);
			mNotificationCountOverflow.setVisibility(View.GONE);
		} else {
			mAppIcon.setVisibility(View.INVISIBLE);
			if (mNotificationCount <= 99) {
				mNotificationCountText.setText(Integer.toString(mNotificationCount));
				mNotificationCountText.setVisibility(View.VISIBLE);
				mNotificationCountOverflow.setVisibility(View.GONE);
			} else {
				mNotificationCountText.setVisibility(View.GONE);
				mNotificationCountOverflow.setVisibility(View.VISIBLE);
			}
		}
		setUpButtonContentDescription(null);
	}

	public void setOnDoneButtonClickListener(
			OnDoneButtonClickListener ondonebuttonclicklistener) {
		mDoneButtonListener = ondonebuttonclicklistener;
	}

	public void setOnUpButtonClickListener(
			OnUpButtonClickListener onupbuttonclicklistener) {
		mUpButtonListener = onupbuttonclicklistener;
	}

	public void setPrimarySpinnerSelection(int i) {
		if (i < mPrimarySpinner.getCount() && i >= 0)
			mPrimarySpinner.setSelection(i);
	}

    public void setUpButtonContentDescription(String s)
    {
        StringBuilder stringbuilder = new StringBuilder();
        if(!TextUtils.isEmpty(s))
        {
            mCurrentButtonActionText = s;
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, s);
        } else
        {
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mCurrentButtonActionText);
        }
        if(mNotificationCount > 0)
        {
            Resources resources = getResources();
            int i = R.plurals.accessibility_notification_count_description;
            int j = mNotificationCount;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(mNotificationCount);
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, resources.getQuantityString(i, j, aobj));
        }
        mUpButton.setContentDescription(stringbuilder.toString());
    }

    public final void showActionButton(int i, int j, int k) {
    	
    	if(mActionButton1Visible) {
    		if(mActionButton2Visible) {
    			throw new IllegalArgumentException("Only two action buttons are supported");
    		}
            mActionButton2Visible = true;
            mActionId2 = i;
            mActionButton2.setImageResource(j);
            mActionButton2.setContentDescription(getContext().getString(k));
            if(mActive)
            {
                ImageView imageview = mActionButton2;
                boolean flag = mActionButton2Visible;
                int l = 0;
                if(!flag)
                    l = 8;
                imageview.setVisibility(l);
            }
    	} else {
    		mActionButton1Visible = true;
            mActionId1 = i;
            mActionButton1.setImageResource(j);
            mActionButton1.setContentDescription(getContext().getString(k));
            if(!mActive) {
            	return; 
            } else {
            	int j1;
                ImageView imageview1 = mActionButton1;
                int i1;
                ImageView imageview2;
                boolean flag1;
                if(mActionButton1Visible)
                    i1 = 0;
                else
                    i1 = 8;
                imageview1.setVisibility(i1);
                imageview2 = mRefreshButton;
                if(!isRefreshButtonVisible()) { 
                	j1 = 8; 
                } else { 
                	flag1 = mProgressIndicatorVisible;
                    j1 = 0;
                    if(flag1) {
                    	j1 = 8; 
                    }
                }
                imageview2.setVisibility(j1);
            }
    	}
    }

	public final void showPrimarySpinner(SpinnerAdapter spinneradapter, int i) {
		mPrimarySpinnerVisible = true;
		mPrimarySpinner.setAdapter(spinneradapter);
		int j = spinneradapter.getCount();
		if (j > 0)
			mPrimarySpinner.setSelection(i);
		if (mActive) {
			if (j > 0)
				mPrimarySpinnerContainer.setVisibility(View.VISIBLE);
			else
				mPrimarySpinnerContainer.setVisibility(View.GONE);
		}
	}

	public final void showProgressIndicator() {
		mProgressIndicatorVisible = true;
		if (mActive) {
			mRefreshButton.setVisibility(View.GONE);
			if (mRefreshButtonVisibleIfRoom && !isRefreshButtonVisible()) {
				mActionButton1.setVisibility(View.GONE);
				mActionButton2.setVisibility(View.GONE);
			}
			mProgressIndicator.setVisibility(View.VISIBLE);
		}
	}

	public final void showRefreshButton() {
		mRefreshButtonVisible = true;
		if (mActive && !mProgressIndicatorVisible)
			mRefreshButton.setVisibility(View.VISIBLE);
	}

	public final void showRefreshButtonIfRoom() {
		mRefreshButtonVisibleIfRoom = true;
		if (mActive && !mProgressIndicatorVisible && isRefreshButtonVisible())
			mRefreshButton.setVisibility(View.VISIBLE);
	}

	public final void showSearchView() {
		mSearchViewVisible = true;
		if (mActive)
			mSearchViewContainer.setVisibility(View.VISIBLE);
	}

	public final void showTitle(int i) {
		if (i != 0)
			mTitleVisible = true;
		else
			mTitleVisible = false;
		mTitle.setText(i);
		if (mActive) {
			if (mTitleVisible) {
				mTitle.setVisibility(View.VISIBLE);
			} else {
				mTitle.setVisibility(View.GONE);
			}
		}
	}

	public final void showTitle(String s) {
		if (!TextUtils.isEmpty(s))
			mTitleVisible = true;
		else
			mTitleVisible = false;
		mTitle.setText(s);
		if (mActive) {
			if (mTitleVisible) {
				mTitle.setVisibility(View.VISIBLE);
			} else {
				mTitle.setVisibility(View.GONE);
			}
		}
	}

	public final void startContextActionMode() {
		if (!mContextActionMode) {
			mContextActionMode = true;
			if (mActive) {
				mUpButton.setVisibility(View.GONE);
				mDoneButton.setVisibility(View.VISIBLE);
			}
		}
	}

	public final void updateRefreshButtonIcon(boolean refreshHighlighted) {
		mRefreshHighlighted = refreshHighlighted;
		int i;
		if (mRefreshHighlighted)
			i = R.drawable.ic_refresh_blue;
		else
			i = R.drawable.ic_refresh;
		mRefreshButton.setImageDrawable(getResources().getDrawable(i));
	}
    
    //===========================================================================
    //						Inner class
    //===========================================================================
	public static interface HostActionBarListener {

		void onActionBarInvalidated();

		void onActionButtonClicked(int i);

		void onPrimarySpinnerSelectionChange(int i);

		void onRefreshButtonClicked();
	}
	
	public static interface OnDoneButtonClickListener {

		void onDoneButtonClick();
	}

	public static interface OnUpButtonClickListener {

		void onUpButtonClick();
	}
	
	private class PopupMenuListener implements PopupMenu.OnMenuItemClickListener {

		public boolean onMenuItemClick(MenuItem menuitem) {
			return ((Activity) getContext()).onOptionsItemSelected(menuitem);
		}
	}
	
	private final class PopupMenuListenerV14 extends PopupMenuListener
			implements PopupMenu.OnDismissListener {

		public final void onDismiss(PopupMenu popupmenu) {
			if (popupmenu == mOverflowPopupMenu)
				mOverflowPopupMenuVisible = false;
			else
				mSharePopupMenuVisible = false;
		}
	}
	
	static class SavedState extends View.BaseSavedState {

		boolean overflowPopupMenuVisible;
		boolean sharePopupMenuVisible;
		
		public String toString() {
			String s = Integer.toHexString(System.identityHashCode(this));
			return (new StringBuilder("HostActionBar.SavedState{")).append(s)
					.append(" overflowPopupMenuVisible=")
					.append(overflowPopupMenuVisible)
					.append(" sharePopupMenuVisible=")
					.append(sharePopupMenuVisible).append("}").toString();
		}

		public void writeToParcel(Parcel parcel, int i) {
			super.writeToParcel(parcel, i);
			
			if (overflowPopupMenuVisible)
				parcel.writeInt(1);
			else
				parcel.writeInt(0);
			
			if (sharePopupMenuVisible)
				parcel.writeInt(1);
			else
				parcel.writeInt(0);
		}

		public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

			public final Object createFromParcel(Parcel parcel) {
				return new SavedState(parcel, (byte) 0);
			}

			public final Object[] newArray(int i) {
				return new SavedState[i];
			}

		};

		private SavedState(Parcel parcel) {
			super(parcel);
			if (parcel.readInt() != 0)
				overflowPopupMenuVisible = true;
			else
				overflowPopupMenuVisible = false;
			
			if (parcel.readInt() == 0)
				sharePopupMenuVisible = false;
			else
				sharePopupMenuVisible = true;
		}

		SavedState(Parcel parcel, byte byte0) {
			this(parcel);
		}

		SavedState(Parcelable parcelable) {
			super(parcelable);
		}
	}
}
