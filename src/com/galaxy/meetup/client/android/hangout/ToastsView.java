/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class ToastsView extends FrameLayout {

	private final Animation animIn;
    private final Animation animOut;
    private final EventHandler eventHandler = new EventHandler();
    private Runnable hideToastRunnable;
    private ImageView imageView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private TextView mTextView;
    
    public ToastsView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        View view = LayoutInflater.from(context).inflate(R.layout.hangout_toasts_view, this, true);
        imageView = (ImageView)view.findViewById(R.id.toast_icon);
        mTextView = (TextView)view.findViewById(R.id.toast_text);
        hideToastRunnable = new Runnable() {

            public final void run()
            {
                startAnimation(animIn);
            }

        };
        animIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animIn.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation)
            {
            }

            public final void onAnimationRepeat(Animation animation)
            {
            }

            public final void onAnimationStart(Animation animation)
            {
                setVisibility(0);
            }

        });
        animOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        animOut.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation)
            {
                setVisibility(8);
            }

            public final void onAnimationRepeat(Animation animation)
            {
            }

            public final void onAnimationStart(Animation animation)
            {
            }

        });
    }

    public final void addToast(int i)
    {
        addToast(((ToastInfo) (new StringToastInfo(getContext().getResources().getString(i)))));
    }

    public final void addToast(ToastInfo toastinfo)
    {
        imageView.setVisibility(0);
        toastinfo.populateView(imageView, mTextView);
        if(!TextUtils.isEmpty(mTextView.getText()))
        {
            mHandler.removeCallbacks(hideToastRunnable);
            mHandler.postDelayed(hideToastRunnable, 5000L);
            startAnimation(animIn);
        }
    }

    public final void onPause()
    {
        GCommApp.getInstance(getContext()).unregisterForEvents(getContext(), eventHandler, false);
    }

    public final void onResume()
    {
        GCommApp.getInstance(getContext()).registerForEvents(getContext(), eventHandler, false);
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private final class EventHandler extends GCommEventHandler
    {

        public final void onMediaBlock(MeetingMember meetingmember, MeetingMember meetingmember1, boolean flag)
        {
            super.onMediaBlock(meetingmember, meetingmember1, flag);
            addToast(new MediaBlockToast(meetingmember, meetingmember1, flag));
        }

        public final void onMeetingMemberExited(MeetingMember meetingmember)
        {
            super.onMeetingMemberExited(meetingmember);
            addToast(new MeetingMemberToast(meetingmember));
        }

        public final void onMeetingMemberPresenceConnectionStatusChanged(MeetingMember meetingmember)
        {
            if(GCommApp.getInstance(getContext()).shouldShowToastForMember(meetingmember))
                addToast(new MeetingMemberToast(meetingmember));
        }

        public final void onRemoteMute(MeetingMember meetingmember, MeetingMember meetingmember1)
        {
            super.onRemoteMute(meetingmember, meetingmember1);
            addToast(new RemoteMuteToast(meetingmember, meetingmember1));
        }

        public final void onVCardResponse(MeetingMember meetingmember)
        {
            super.onVCardResponse(meetingmember);
            if(GCommApp.getInstance(getContext()).shouldShowToastForMember(meetingmember))
                addToast(new MeetingMemberToast(meetingmember));
        }

    }

    private final class MediaBlockToast extends ToastInfo {
    	
    	private final MeetingMember mBlockee;
        private final MeetingMember mBlocker;
        private final boolean mIsRecording;

        MediaBlockToast(MeetingMember meetingmember, MeetingMember meetingmember1, boolean flag)
        {
            mBlockee = meetingmember;
            mBlocker = meetingmember1;
            if(Property.FORCE_HANGOUT_RECORD_ABUSE.getBoolean())
                flag = true;
            mIsRecording = flag;
        }

        final void populateView(ImageView imageview, TextView textview)
        {
            Resources resources = getResources();
            String s;
            if(mIsRecording)
            {
                s = resources.getString(R.string.hangout_recording_abuse);
                imageview.setVisibility(8);
            } else
            if(mBlockee != null && mBlocker != null)
            {
                Avatars.renderAvatar(getContext(), mBlockee, imageview);
                String s1 = mBlockee.getName(getContext());
                String s2 = mBlocker.getName(getContext());
                if(mBlocker.isSelf())
                    s = resources.getString(R.string.hangout_media_block_by_self, new Object[] {
                        s1
                    });
                else
                if(mBlockee.isSelf())
                    s = resources.getString(R.string.hangout_media_block_to_self, new Object[] {
                        s2
                    });
                else
                    s = resources.getString(R.string.hangout_media_block, new Object[] {
                        s2, s1
                    });
            } else
            {
                s = null;
            }
            textview.setText(s);
        }

    }

    private final class MeetingMemberToast extends ToastInfo {

    	private final MeetingMember meetingMember;
        private final int messageId;

        MeetingMemberToast(MeetingMember meetingmember) {
        	meetingMember = meetingmember;
        	switch(meetingmember.getCurrentStatus()) {
        	case DISCONNECTED:
        		if(meetingmember.getPreviousStatus() == MeetingMember.Status.CONNECTING)
                    messageId = R.string.hangout_member_unable_to_join;
                else
                    messageId = R.string.hangout_member_exiting_meeting;
        		break;
        	case CONNECTING:
        		messageId = -1;
        		break;
        	case CONNECTED:
        		messageId = R.string.hangout_member_entering_meeting;
        		break;
        	default:
        		messageId = -1;
        		break;
        	}
        }
        
        final void populateView(ImageView imageview, TextView textview)
        {
            if(messageId != -1)
            {
                Avatars.renderAvatar(getContext(), meetingMember, imageview);
                String s = meetingMember.getName(getContext());
                textview.setText(String.format(getResources().getString(messageId), new Object[] {
                    s
                }));
            }
        }
    }

    private final class RemoteMuteToast extends ToastInfo {

    	private final MeetingMember mutee;
        private final MeetingMember muter;

        RemoteMuteToast(MeetingMember meetingmember, MeetingMember meetingmember1)
        {
            mutee = meetingmember;
            muter = meetingmember1;
        }
        
        final void populateView(ImageView imageview, TextView textview)
        {
            Avatars.renderAvatar(getContext(), mutee, imageview);
            String s = mutee.getName(getContext());
            String s1 = muter.getName(getContext());
            String s2;
            if(muter.isSelf())
                s2 = getResources().getString(R.string.hangout_remote_mute_by_self, new Object[] {
                    s
                });
            else
            if(mutee.isSelf())
                s2 = getResources().getString(R.string.hangout_remote_mute_to_self, new Object[] {
                    s1
                });
            else
                s2 = getResources().getString(R.string.hangout_remote_mute, new Object[] {
                    s1, s
                });
            textview.setText(s2);
        }
    }

    private final class StringToastInfo extends ToastInfo {

    	private final String string;
    	
    	StringToastInfo(String s)
        {
            string = s;
        }
    	
        final void populateView(ImageView imageview, TextView textview)
        {
            imageview.setVisibility(8);
            textview.setText(string);
        }

    }

    private abstract class ToastInfo {

        abstract void populateView(ImageView imageview, TextView textview);

    }
}
