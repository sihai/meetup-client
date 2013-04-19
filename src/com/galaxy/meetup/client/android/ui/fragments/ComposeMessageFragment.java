/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.realtimechat.Client;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.ChoosePhotoDialog.PhotoHandler;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class ComposeMessageFragment extends Fragment implements
		OnEditorActionListener, PhotoHandler {

	private boolean mAllowSendImages;
    private boolean mAllowSendMessage;
    private Client.Typing.Type mCurrentTypingStatus;
    private Handler mHandler;
    private Integer mInsertCameraPhotoRequestId;
    private Listener mListener;
    private EditText mMessageText;
    private View mSendButton;
    private EsServiceListener mServiceListener;
    private long mTimeSinceLastTypingEvent;
    private Runnable mTypingTimeoutRunnable;
    
    
    public ComposeMessageFragment()
    {
        mHandler = new Handler();
        mServiceListener = new EsServiceListener() {

            public final void onInsertCameraPhotoComplete(int i, ServiceResult serviceresult)
            {
                if(mInsertCameraPhotoRequestId != null && mInsertCameraPhotoRequestId.intValue() == i)
                {
                    insertCameraPhoto(EsService.getLastCameraMediaLocation());
                    mInsertCameraPhotoRequestId = null;
                }
            }

        };
        mTypingTimeoutRunnable = new Runnable() {

            public final void run()
            {
                ComposeMessageFragment.access$200(ComposeMessageFragment.this);
            }
        };
    }

    private void dispatchSendMessageEvent()
    {
        if(mListener != null)
        {
            String s;
            if(mMessageText == null)
                s = null;
            else
                s = mMessageText.getText().toString().trim();
            if(s != null && s.length() > 0)
                mListener.onSendTextMessage(s);
            mMessageText.setText("");
        }
    }

    private void dispatchSendPhotoEvent(String s, int i)
    {
        if(mListener != null)
            mListener.onSendPhoto(s, i);
    }

    private void dispatchTypingStatusChangedEvent(Client.Typing.Type type)
    {
        if(mListener != null)
            mListener.onTypingStatusChanged(type);
    }

    private EsAccount getAccount()
    {
        return (EsAccount)getActivity().getIntent().getParcelableExtra("account");
    }

    private void insertCameraPhoto(String s)
    {
        FragmentActivity fragmentactivity = getActivity();
        if(s != null)
            dispatchSendPhotoEvent(s, 2);
        else
            Toast.makeText(fragmentactivity, getString(R.string.camera_photo_error), 1).show();
        if(fragmentactivity instanceof ImageUtils.InsertCameraPhotoDialogDisplayer)
            ((ImageUtils.InsertCameraPhotoDialogDisplayer)fragmentactivity).hideInsertCameraPhotoDialog();
    }

    private void updateSendButtonState()
    {
        String s;
        boolean flag;
        if(mMessageText == null)
            s = null;
        else
            s = mMessageText.getText().toString().trim();
        if(mAllowSendMessage && s != null && s.length() > 0)
            flag = true;
        else
            flag = false;
        if(mSendButton != null && mSendButton.isEnabled() != flag)
            mSendButton.setEnabled(flag);
    }

    public final void allowSendingImages(boolean flag)
    {
        mAllowSendImages = flag;
        if(getView() != null)
        {
            View view = getView().findViewById(R.id.photo_button);
            if(flag)
                view.setVisibility(0);
            else
                view.setVisibility(8);
        }
    }

    public final void doPickPhotoFromAlbums(int i)
    {
        Intents.PhotosIntentBuilder photosintentbuilder = Intents.newAlbumsActivityIntentBuilder(getActivity());
        photosintentbuilder.setAccount(getAccount()).setPersonId(getAccount().getPersonId()).setPhotosHome(Boolean.valueOf(true)).setShowCameraAlbum(Boolean.valueOf(true)).setPhotoPickerMode(Integer.valueOf(1)).setPhotoPickerTitleResourceId(Integer.valueOf(R.string.photo_picker_album_label_messenger));
        startActivityForResult(photosintentbuilder.build(), 1);
    }

    public final void doRepositionCoverPhoto()
    {
    }

    public final void doTakePhoto()
    {
        getActivity();
        startActivityForResult(Intents.getCameraIntentPhoto("camera-p.jpg"), 2);
    }

    public final void onActivityResult(int i, int j, Intent intent) {
    	if(1 == i) {
    		if(j == -1 && intent != null)
            {
                String s = intent.getStringExtra("photo_url");
                if(s != null)
                    dispatchSendPhotoEvent(s, i);
            }
    	} else if(2 == i) {
    		if(j == -1)
            {
                FragmentActivity fragmentactivity = getActivity();
                if(fragmentactivity instanceof ImageUtils.InsertCameraPhotoDialogDisplayer)
                    ((ImageUtils.InsertCameraPhotoDialogDisplayer)fragmentactivity).showInsertCameraPhotoDialog();
                mInsertCameraPhotoRequestId = EsService.insertCameraPhoto(fragmentactivity, getAccount(), "camera-p.jpg");
            }
    	}
    	
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        if(bundle != null && bundle.containsKey("insert_camera_photo_req_id"))
            mInsertCameraPhotoRequestId = Integer.valueOf(bundle.getInt("insert_camera_photo_req_id"));
        View view = layoutinflater.inflate(R.layout.compose_message, viewgroup);
        mSendButton = view.findViewById(R.id.send_button);
        mCurrentTypingStatus = Client.Typing.Type.CLEAR;
        mMessageText = (EditText)view.findViewById(R.id.message_text);
        mMessageText.addTextChangedListener(new TextWatcher() {

            public final void afterTextChanged(Editable editable)
            {
            }

            public final void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public final void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
                updateSendButtonState();
                ComposeMessageFragment.access$200(ComposeMessageFragment.this);
            }

        });
        mMessageText.setOnEditorActionListener(this);
        view.findViewById(R.id.send_button).setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view2)
            {
                dispatchSendMessageEvent();
            }

        });
        View view1 = view.findViewById(R.id.photo_button);
        view1.setOnCreateContextMenuListener(this);
        if(mAllowSendImages)
            view1.setVisibility(0);
        else
            view1.setVisibility(8);
        view1.setOnClickListener(new android.view.View.OnClickListener() {
        	public final void onClick(View paramView)
            {
              ComposeMessageFragment.access$500(ComposeMessageFragment.this);
            }
        });
        mAllowSendMessage = false;
        updateSendButtonState();
        return view;
    }

    public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent)
    {
        if((i == 4 || keyevent != null && keyevent.getAction() == 0) && mMessageText.getText().length() > 0)
            dispatchSendMessageEvent();
        return true;
    }

    public final void onPause()
    {
        onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public final void onResume()
    {
        onResume();
        EsService.registerListener(mServiceListener);
        if(mInsertCameraPhotoRequestId != null && !EsService.isRequestPending(mInsertCameraPhotoRequestId.intValue()))
        {
            EsService.removeResult(mInsertCameraPhotoRequestId.intValue());
            insertCameraPhoto(EsService.getLastCameraMediaLocation());
            mInsertCameraPhotoRequestId = null;
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        onSaveInstanceState(bundle);
        if(mInsertCameraPhotoRequestId != null)
            bundle.putInt("insert_camera_photo_req_id", mInsertCameraPhotoRequestId.intValue());
    }

    public final void requestFocus()
    {
        mMessageText.requestFocus();
    }

    public final void setAllowSendMessage(boolean flag)
    {
        mAllowSendMessage = flag;
        updateSendButtonState();
    }

    public final void setListener(Listener listener)
    {
        mListener = listener;
    }
    
    static void access$200(ComposeMessageFragment composemessagefragment) {
        long l;
        int i;
        l = SystemClock.elapsedRealtime();
        if(composemessagefragment.mMessageText == null)
            i = 0;
        else
            i = composemessagefragment.mMessageText.getText().toString().trim().length();
        int number = composemessagefragment.mCurrentTypingStatus.getNumber();
        if(1 == number) {
        	if(i == 0)
            {
                composemessagefragment.mCurrentTypingStatus = Client.Typing.Type.CLEAR;
                composemessagefragment.dispatchTypingStatusChangedEvent(composemessagefragment.mCurrentTypingStatus);
            } else
            if(l - composemessagefragment.mTimeSinceLastTypingEvent > 5000L)
            {
                composemessagefragment.mCurrentTypingStatus = Client.Typing.Type.PAUSE;
                composemessagefragment.dispatchTypingStatusChangedEvent(composemessagefragment.mCurrentTypingStatus);
            }
        } else if(2 == number) {
        	if(i == 0)
            {
                composemessagefragment.mCurrentTypingStatus = Client.Typing.Type.CLEAR;
            } else
            {
                composemessagefragment.mCurrentTypingStatus = Client.Typing.Type.START;
                composemessagefragment.dispatchTypingStatusChangedEvent(composemessagefragment.mCurrentTypingStatus);
            }
        } else if(3 == number) {
        	if(i > 0)
            {
                composemessagefragment.mCurrentTypingStatus = Client.Typing.Type.START;
                composemessagefragment.dispatchTypingStatusChangedEvent(composemessagefragment.mCurrentTypingStatus);
            }
        }
        
        if(composemessagefragment.mCurrentTypingStatus == Client.Typing.Type.START)
        {
            composemessagefragment.mHandler.removeCallbacks(composemessagefragment.mTypingTimeoutRunnable);
            composemessagefragment.mHandler.postDelayed(composemessagefragment.mTypingTimeoutRunnable, 5000L);
            composemessagefragment.mTimeSinceLastTypingEvent = l;
        }
        return;
        
    }
    
    static void access$500(ComposeMessageFragment composemessagefragment)
    {
        ChoosePhotoDialog choosephotodialog = new ChoosePhotoDialog(R.string.menu_photo_chooser);
        choosephotodialog.setIsCameraSupported(Intents.isCameraIntentRegistered(composemessagefragment.getActivity()));
        choosephotodialog.setTargetFragment(composemessagefragment, 0);
        choosephotodialog.show(composemessagefragment.getFragmentManager(), "share_photo");
        return;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	public static interface Listener
    {

        public abstract void onSendPhoto(String s, int i);

        public abstract void onSendTextMessage(String s);

        public abstract void onTypingStatusChanged(Client.Typing.Type type);
    }
	
}
