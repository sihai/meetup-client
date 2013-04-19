/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.galaxy.meetup.client.android.AuthData;
import com.galaxy.meetup.client.android.api.ApiUtils;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class GCommNativeWrapper {

	static boolean $assertionsDisabled = false;
    private static final int GCOMM_NATIVE_LIB_API_LEVEL = 6;
    public static final String HANGOUT = "HANGOUT";
    public static final String HANGOUT_SYNC = "HANGOUT_SYNC";
    public static final int INVALID_INCOMING_VIDEO_REQUEST_ID = 0;
    static final int MAX_INCOMING_AUDIO_LEVEL = 255;
    static final int MIN_INCOMING_AUDIO_LEVEL = 0;
    private static final String SELF_MUC_JID_BEFORE_ENTERING_HANGOUT = "";
    public static final String TRANSFER = "TRANSFER";
    private volatile EsAccount account;
    private volatile boolean clientInitiatedExit;
    private final Context context;
    private volatile boolean hadSomeConnectedParticipant;
    private volatile boolean hangoutCreated;
    private volatile Hangout.Info hangoutInfo;
    private volatile boolean isHangoutLite;
    private volatile Map memberMucJidToMeetingMember;
    private volatile int membersCount;
    private volatile long nativePeerObject;
    private volatile boolean retrySignin;
    private volatile boolean ringInvitees;
    private volatile List roomHistory;
    private volatile MeetingMember selfMeetingMember;
    private volatile String userJid;

    static 
    {
        boolean flag;
        if(!GCommNativeWrapper.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
        Log.info("GCommNativeWrapper loading gcomm_ini");
        System.loadLibrary("gcomm_jni");
        Log.info("GCommNativeWrapper done loading gcomm_ini");
    }
    
    
    public GCommNativeWrapper(Context context1)
    {
        retrySignin = true;
        context = context1;
        roomHistory = new ArrayList();
        memberMucJidToMeetingMember = Collections.synchronizedMap(new HashMap());
    }

    private static ArrayList ToArrayList(Object aobj[])
    {
        ArrayList arraylist = new ArrayList(aobj.length);
        int i = aobj.length;
        for(int j = 0; j < i; j++)
            arraylist.add(aobj[j]);

        return arraylist;
    }

    public static void initialize(Context context1, String s, String s1, String s2, boolean flag, String s3, String s4)
        throws LinkageError
    {
        int i = nativeStaticGetVersion();
        Log.debug((new StringBuilder("GComm native lib API version:     ")).append(i).toString());
        Log.debug("GComm native wrapper API version: 6");
        Log.debug((new StringBuilder("GComm native lib logging: ")).append(flag).append(" at level ").append(s4).toString());
        if(i != 6)
        {
            Log.error((new StringBuilder("GComm native lib version mismatch.  Expected 6 but got ")).append(i).toString());
            throw new UnsupportedClassVersionError();
        }
        if(!nativeStaticInitialize(context1, s, s1, s2, flag, s3, s4))
        {
            Log.error("GComm native lib initialization failed");
            throw new ExceptionInInitializerError();
        } else
        {
            return;
        }
    }

    private native void nativeBlockMedia(String s);

    private native void nativeConnectAndSignin(String s, String s1);

    private native void nativeConnectAndSigninFull(String s, String s1, String s2, String s3, boolean flag);

    private native void nativeCreateHangout();

    private native void nativeEnterMeeting(int i, String s, String s1, String s2, String s3, boolean flag, boolean flag1);

    private native void nativeEnterMeetingWithCachedGreenRoomInfo(boolean flag);

    private native void nativeExitMeeting();

    private native int nativeGetIncomingAudioVolume();

    private native boolean nativeInitializeIncomingVideoRenderer(int i);

    private native void nativeInviteToMeeting(String as[], String as1[], String s, boolean flag, boolean flag1);

    private native boolean nativeIsAudioMute();

    private native boolean nativeIsOutgoingVideoStarted();

    private native void nativeKickMeetingMember(String s, String s1);

    private native long nativePeerCreate();

    private native void nativePeerDestroy(long l);

    private native void nativeProvideOutgoingVideoFrame(byte abyte0[], long l, int i);

    private native void nativeRemoteMute(String s);

    private native boolean nativeRenderIncomingVideoFrame(int i);

    private native void nativeRequestVCard(String s, String s1);

    private native void nativeSendInstantMessage(String s);

    private native void nativeSendInstantMessageToUser(String s, String s1);

    private native void nativeSendRingStatus(String s, String s1, String s2);

    private native void nativeSetAudioMute(boolean flag);

    private native void nativeSetIncomingAudioVolume(int i);

    private native void nativeSetIncomingVideoParameters(int i, int j, int k, int l, int i1);

    private native boolean nativeSetIncomingVideoRendererSurfaceSize(int i, int j, int k);

    private native void nativeSetIncomingVideoSourceToSpeakerIndex(int i, int j);

    private native void nativeSetIncomingVideoSourceToUser(int i, String s);

    private native void nativeSetPresenceConnectionStatus(int i);

    private native void nativeSignoutAndDisconnect();

    public static native void nativeSimulateCrash();

    private native int nativeStartIncomingVideoForSpeakerIndex(int i, int j, int k, int l);

    private native int nativeStartIncomingVideoForUser(String s, int i, int j, int k);

    private native void nativeStartOutgoingVideo(int i, int j);

    public static native void nativeStaticCleanup();

    private static native int nativeStaticGetVersion();

    private static native boolean nativeStaticInitialize(Context context1, String s, String s1, String s2, boolean flag, String s3, String s4);

    public static native void nativeStaticSetDeviceCaptureType(int i);

    private native void nativeStopIncomingVideo(int i);

    private native void nativeStopOutgoingVideo();

    private native void nativeUploadCallgrokLog();

    private void onAudioMuteStateChanged(String s, boolean flag)
    {
    	MeetingMember meetingmember = null;
    	
        if(!s.equals("")) 
        	meetingmember = (MeetingMember)memberMucJidToMeetingMember.get(s); 
        GCommApp.sendObjectMessage(context, 101, Pair.create(meetingmember, Boolean.valueOf(flag)));
    }

    private void onCallgrokLogUploadCompleted(int i, String s)
    {
        GCommApp.sendObjectMessage(context, 60, Pair.create(Integer.valueOf(i), s));
    }

    private void onCurrentSpeakerChanged(String s)
    {
        GCommApp.sendObjectMessage(context, 102, memberMucJidToMeetingMember.get(s));
    }

    private void onError(int i)
    {
        Error error;
        Object aobj[] = new Object[1];
        aobj[0] = Integer.valueOf(i);
        Log.info("GCommNativeWrapper.onError: %d", aobj);
        error = Error.values()[i];
        if(error != Error.AUTHENTICATION) {
        	GCommApp.sendObjectMessage(context, -1, error);
        	return;
        }
        Log.info("Invalidating auth token...");
        try
        {
            AuthData.invalidateAuthToken(context, account.getName(), "webupdates");
        }
        catch(Exception exception) { }
        if(!retrySignin) 
        	GCommApp.sendObjectMessage(context, -1, error); 
        else {
        	GCommApp.getInstance(context).signinUser(getAccount());
            retrySignin = false;
        }
    }

    private void onHangoutCreated(String s)
    {
        hangoutCreated = true;
        Hangout.Info info = new Hangout.Info(Hangout.RoomType.CONSUMER, null, null, s, null, Hangout.LaunchSource.Creation, ringInvitees);
        GCommApp.sendObjectMessage(context, 50, info);
    }

    private void onIncomingVideoFrameDimensionsChanged(int i, int j, int k)
    {
        GCommApp.sendObjectMessage(context, 107, new FrameDimensionsChangedMessageParams(i, new RectangleDimensions(j, k)));
    }

    private void onIncomingVideoFrameReceived(int i)
    {
        GCommApp.sendObjectMessage(context, 106, Integer.valueOf(i));
    }

    private void onIncomingVideoStarted(int i)
    {
        GCommApp.sendObjectMessage(context, 104, Integer.valueOf(i));
    }

    private void onInstantMessageReceived(String s, String s1)
    {
        MeetingMember meetingmember = (MeetingMember)memberMucJidToMeetingMember.get(s);
        if(meetingmember == null)
            Log.error((new StringBuilder("onInstantMessageReceived missing fromMucJid: ")).append(s).toString());
        InstantMessage instantmessage = new InstantMessage(meetingmember, s, s1);
        GCommApp.sendObjectMessage(context, 59, instantmessage);
    }

    private void onMediaBlock(String s, String s1, boolean flag)
    {
        MeetingMember meetingmember;
        MeetingMember meetingmember1;
        meetingmember = (MeetingMember)memberMucJidToMeetingMember.get(s);
        meetingmember1 = (MeetingMember)memberMucJidToMeetingMember.get(s1);
        if(!meetingmember.isSelf()) {
        	if(meetingmember1.isSelf())
                meetingmember.setMediaBlocked(true); 
        } else { 
        	meetingmember1.setMediaBlocked(true);
        }
        if(!Property.ENABLE_HANGOUT_RECORD_ABUSE.getBoolean())
            flag = false;
        if(flag || meetingmember1 != null && meetingmember != null)
            GCommApp.sendObjectMessage(context, 110, Pair.create(Pair.create(meetingmember, meetingmember1), Boolean.valueOf(flag)));
        return;
    }

    private void onMeetingEnterError(int i)
    {
        MeetingEnterError meetingentererror = MeetingEnterError.values()[i];
        if(meetingentererror == MeetingEnterError.HANGOUT_OVER && hangoutInfo != null && hangoutInfo.getLaunchSource() == Hangout.LaunchSource.MissedCall)
        {
            hangoutInfo = null;
            GCommApp.sendEmptyMessage(context, 6);
        } else
        {
            clearMeetingState();
            GCommApp.sendObjectMessage(context, -3, meetingentererror);
        }
    }

    private void onMeetingExited()
    {
        boolean flag = clientInitiatedExit;
        clearMeetingState();
        Context context1 = context;
        Object obj;
        if(flag)
            obj = new Object();
        else
            obj = null;
        GCommApp.sendObjectMessage(context1, 54, obj);
    }

    private void onMeetingMediaStarted()
    {
        GCommApp.sendEmptyMessage(context, 53);
    }

    private void onMeetingMemberEntered(String s, String s1, String s2, int i)
    {
        if(!isHangoutLite && (s2 == null || "".equals(s2)))
        {
            Log.debug((new StringBuilder("Ignoring invalid user: JID=")).append(s).append(" nickname=").append(s1).append(" ID=<empty> status=").append(i).toString());
        } else
        {
            boolean flag = account.isMyGaiaId(s2);
            int j = membersCount;
            membersCount = j + 1;
            MeetingMember meetingmember = new MeetingMember(s, s1, s2, j, false, flag);
            PresenceConnectionStatus presenceconnectionstatus = PresenceConnectionStatus.values()[i];
            if(presenceconnectionstatus == PresenceConnectionStatus.CONNECTING || presenceconnectionstatus == PresenceConnectionStatus.JOINING)
                meetingmember.setCurrentStatus(MeetingMember.Status.CONNECTING);
            else
                meetingmember.setCurrentStatus(MeetingMember.Status.CONNECTED);
            memberMucJidToMeetingMember.put(s, meetingmember);
            if(!hadSomeConnectedParticipant && meetingmember.getCurrentStatus() == MeetingMember.Status.CONNECTED)
                hadSomeConnectedParticipant = true;
            nativeRequestVCard(s, "");
            GCommApp.sendObjectMessage(context, 55, meetingmember);
        }
    }

    private void onMeetingMemberExited(String s)
    {
        MeetingMember meetingmember = (MeetingMember)memberMucJidToMeetingMember.remove(s);
        if(meetingmember == null)
        {
            Log.error((new StringBuilder("onMeetingMemberExited missing memberMucJid: ")).append(s).toString());
        } else
        {
            meetingmember.setCurrentStatus(MeetingMember.Status.DISCONNECTED);
            GCommApp.sendObjectMessage(context, 57, meetingmember);
        }
    }

    private void onMeetingMemberPresenceConnectionStateChanged(String s, int i)
    {
        MeetingMember meetingmember = (MeetingMember)memberMucJidToMeetingMember.get(s);
        if(null == meetingmember) {
        	Log.error((new StringBuilder("onMeetingMemberPresenceConnectionStateChanged missing memberMucJid: ")).append(s).toString());
        	return;
        }
        PresenceConnectionStatus presenceconnectionstatus = PresenceConnectionStatus.values()[i];
        MeetingMember.Status status;
        if(presenceconnectionstatus == PresenceConnectionStatus.CONNECTING || presenceconnectionstatus == PresenceConnectionStatus.JOINING)
            status = MeetingMember.Status.CONNECTING;
        else
            status = MeetingMember.Status.CONNECTED;
        if(status != meetingmember.getCurrentStatus())
        {
            meetingmember.setCurrentStatus(status);
            if(!hadSomeConnectedParticipant && meetingmember.getCurrentStatus() == MeetingMember.Status.CONNECTED)
                hadSomeConnectedParticipant = true;
            GCommApp.sendObjectMessage(context, 56, meetingmember);
        }
    }

    private void onMucEntered(String s, String s1, boolean flag)
    {
        String s2 = account.getGaiaId();
        int i = membersCount;
        membersCount = i + 1;
        selfMeetingMember = new MeetingMember(s, s1, s2, i, true, true);
        memberMucJidToMeetingMember.put(s, selfMeetingMember);
        nativeRequestVCard(s, "");
        isHangoutLite = flag;
        GCommApp.sendObjectMessage(context, 52, selfMeetingMember);
    }

    public static void onNativeCrash()
    {
        Log.error("GCommNativeWrapper.onNativeCrash - Crash from native code!!!");
        GCommApp.reportNativeCrash();
    }

    private void onOutgoingVideoStarted()
    {
        GCommApp.sendEmptyMessage(context, 105);
    }

    private void onReceivedRoomHistory(String as[], String as1[])
    {
        if(!$assertionsDisabled && as.length != as1.length)
            throw new AssertionError();
        ArrayList arraylist = new ArrayList(as.length);
        String s = account.getName().split("@")[1];
        int i = 0;
        while(i < as.length) 
        {
            String as2[] = as[i].split("@");
            if(as2.length != 2)
            {
                Log.warn((new StringBuilder("Bad format for room history: ")).append(as[i]).toString());
            } else
            {
                String s1 = as2[0];
                String s2 = as2[1];
                String s3 = s1;
                if(!s2.equals(s))
                    s3 = String.format("%s (%s)", new Object[] {
                        s1, s2
                    });
                arraylist.add(new RoomEntry(s3));
            }
            i++;
        }
        roomHistory = arraylist;
        GCommApp.sendObjectMessage(context, 5, arraylist);
    }

    private void onRemoteMute(String s, String s1)
    {
        MeetingMember meetingmember = (MeetingMember)memberMucJidToMeetingMember.get(s);
        MeetingMember meetingmember1 = (MeetingMember)memberMucJidToMeetingMember.get(s1);
        if(meetingmember1 != null && meetingmember != null)
            GCommApp.sendObjectMessage(context, 109, Pair.create(meetingmember, meetingmember1));
    }

    private void onSignedIn(String s)
    {
        userJid = s;
        GCommApp.sendObjectMessage(context, 1, s);
    }

    private void onSignedOut()
    {
        GCommApp.sendEmptyMessage(context, 2);
    }

    private void onSigninTimeOutError()
    {
        GCommApp.sendEmptyMessage(context, -2);
    }

    public static void onUnhandledJavaException(Throwable throwable)
    {
        GCommApp.reportJavaCrashFromNativeCode(throwable);
    }

    private void onVCardResponse(String s, VCard vcard)
    {
        MeetingMember meetingmember = (MeetingMember)memberMucJidToMeetingMember.get(s);
        if(meetingmember == null)
        {
            Log.warn((new StringBuilder("onVCardResponse missing memberMucJid: ")).append(s).toString());
        } else
        {
            meetingmember.setVCard(vcard);
            GCommApp.sendObjectMessage(context, 3, meetingmember);
        }
    }

    private void onVideoPauseStateChanged(String s, boolean flag)
    {
        MeetingMember meetingmember = (MeetingMember)memberMucJidToMeetingMember.get(s);
        if(meetingmember != null)
        {
            meetingmember.setVideoPaused(flag);
            GCommApp.sendObjectMessage(context, 111, Pair.create(meetingmember, Boolean.valueOf(flag)));
        }
    }

    private void onVideoSourceChanged(int i, String s, boolean flag)
    {
        VideoSourceChangedMessageParams videosourcechangedmessageparams = new VideoSourceChangedMessageParams(i, (MeetingMember)memberMucJidToMeetingMember.get(s), flag);
        GCommApp.sendObjectMessage(context, 103, videosourcechangedmessageparams);
    }

    private void onVolumeChanged(String s, int i)
    {
        MeetingMember meetingmember = (MeetingMember)memberMucJidToMeetingMember.get(s);
        if(meetingmember != null)
            GCommApp.sendObjectMessage(context, 112, Pair.create(meetingmember, Integer.valueOf(i)));
    }

    public void blockMedia(MeetingMember meetingmember)
    {
        if(nativePeerObject != 0L)
            nativeBlockMedia(meetingmember.getMucJid());
    }

    void clearMeetingState()
    {
        hangoutInfo = null;
        selfMeetingMember = null;
        memberMucJidToMeetingMember.clear();
        membersCount = 0;
        hadSomeConnectedParticipant = false;
        hangoutCreated = false;
        ringInvitees = false;
        clientInitiatedExit = false;
    }

    public void connectAndSignin(EsAccount esaccount, String s)
    {
        boolean flag;
        if(nativePeerObject == 0L)
            flag = true;
        else
            flag = false;
        if(!flag)
        {
            throw new IllegalStateException();
        } else
        {
            retrySignin = true;
            nativePeerObject = nativePeerCreate();
            account = esaccount;
            Log.info((new StringBuilder("Created native peer: ")).append(nativePeerObject).toString());
            nativeConnectAndSignin(esaccount.getName(), s);
            return;
        }
    }

    public void createHangout(boolean flag)
    {
        if(nativePeerObject != 0L)
        {
            ringInvitees = flag;
            nativeCreateHangout();
        }
    }

    public void enterMeeting(Hangout.Info info, boolean flag, boolean flag1)
    {
        if(nativePeerObject != 0L)
        {
            clearMeetingState();
            hangoutInfo = info;
            String s;
            int i;
            String s1;
            String s2;
            String s3;
            if(info.getDomain() == null)
                s = "";
            else
                s = info.getDomain();
            i = info.getRoomType().ordinal();
            if(info.getServiceId() == null)
                s1 = "";
            else
                s1 = info.getServiceId();
            s2 = info.getId();
            if(info.getNick() == null)
                s3 = "";
            else
                s3 = info.getNick();
            nativeEnterMeeting(i, s, s1, s2, s3, flag, flag1);
        }
    }

    public void exitMeeting()
    {
        if(nativePeerObject != 0L)
        {
            clientInitiatedExit = true;
            nativeExitMeeting();
        }
    }

    public EsAccount getAccount()
    {
        return account;
    }

    public GCommAppState getCurrentState()
    {
        GCommAppState gcommappstate;
        if(nativePeerObject == 0L)
            gcommappstate = GCommAppState.NONE;
        else
            gcommappstate = GCommAppState.values()[nativeGetCurrentState()];
        return gcommappstate;
    }

    public boolean getHadSomeConnectedParticipantInPast()
    {
        return hadSomeConnectedParticipant;
    }

    public boolean getHangoutCreated()
    {
        return hangoutCreated;
    }

    public String getHangoutDomain()
    {
        return hangoutInfo.getDomain();
    }

    public String getHangoutId()
    {
        return hangoutInfo.getId();
    }

    public Hangout.Info getHangoutInfo()
    {
        return hangoutInfo;
    }

    public Hangout.RoomType getHangoutRoomType()
    {
        return hangoutInfo.getRoomType();
    }

    public boolean getHasSomeConnectedParticipant() {
        for(Iterator iterator = memberMucJidToMeetingMember.values().iterator(); iterator.hasNext();) {
        	if(((MeetingMember)iterator.next()).getCurrentStatus() == MeetingMember.Status.CONNECTED) {
        		return true;
        	}
        }
        return false;
    }

    public int getIncomingAudioVolume()
    {
        int i;
        if(nativePeerObject == 0L)
            i = 0;
        else
            i = nativeGetIncomingAudioVolume();
        return i;
    }

    public boolean getIsHangoutLite()
    {
        return isHangoutLite;
    }

    public MeetingMember getMeetingMember(String s)
    {
        return (MeetingMember)memberMucJidToMeetingMember.get(s);
    }

    public int getMeetingMemberCount()
    {
        return memberMucJidToMeetingMember.size();
    }

    public List getMeetingMembersOrderedByEntry()
    {
        ArrayList arraylist = new ArrayList(memberMucJidToMeetingMember.values());
        Collections.sort(arraylist, new MeetingMember.SortByEntryOrder());
        return arraylist;
    }

    public List getRoomHistory()
    {
        return roomHistory;
    }

    public MeetingMember getSelfMeetingMember()
    {
        return selfMeetingMember;
    }

    public String getUserJid()
    {
        return userJid;
    }

    public boolean initializeIncomingVideoRenderer(int i)
    {
        boolean flag;
        if(nativePeerObject == 0L)
            flag = false;
        else
            flag = nativeInitializeIncomingVideoRenderer(i);
        return flag;
    }

    void inviteToMeeting(AudienceData audiencedata, String s, boolean flag, boolean flag1)
    {
        if(nativePeerObject != 0L)
        {
            AudienceData audiencedata1 = ApiUtils.removeCircleIdNamespaces(audiencedata);
            HashSet hashset = new HashSet();
            Iterator iterator = memberMucJidToMeetingMember.values().iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                String s2 = EsPeopleData.extractGaiaId(((MeetingMember)iterator.next()).getId());
                if(s2 != null)
                    hashset.add(s2);
            } while(true);
            ArrayList arraylist = new ArrayList(audiencedata1.getUserCount());
            PersonData apersondata[] = audiencedata1.getUsers();
            int i = apersondata.length;
            int j = 0;
            while(j < i) 
            {
                PersonData persondata = apersondata[j];
                String s1 = persondata.getObfuscatedId();
                if(TextUtils.isEmpty(s1))
                    Log.error((new StringBuilder("Person object with no id: ")).append(persondata).toString());
                else
                if(hashset.contains(s1))
                    Log.debug((new StringBuilder("Skip adding: ")).append(s1).toString());
                else
                    arraylist.add(s1);
                j++;
            }
            ArrayList arraylist1 = new ArrayList(audiencedata1.getCircleCount());
            CircleData acircledata[] = audiencedata1.getCircles();
            int k = acircledata.length;
            for(int l = 0; l < k; l++)
                arraylist1.add(acircledata[l].getId());

            if(arraylist.size() == 0 && arraylist1.size() == 0 && s != "TRANSFER")
            {
                Log.debug("Skipping invite since no one to invite");
            } else
            {
                String as[] = new String[arraylist.size()];
                arraylist.toArray(as);
                String as1[] = new String[arraylist1.size()];
                arraylist1.toArray(as1);
                nativeInviteToMeeting(as, as1, s, flag, flag1);
            }
        }
    }

    public boolean isAudioMute()
    {
        boolean flag;
        if(nativePeerObject == 0L)
            flag = false;
        else
            flag = nativeIsAudioMute();
        return flag;
    }

    public boolean isInHangout(Hangout.Info info)
    {
        boolean flag;
        if(info == null)
            flag = false;
        else
            flag = info.equals(hangoutInfo);
        return flag;
    }

    public boolean isOutgoingVideoStarted()
    {
        boolean flag;
        if(nativePeerObject == 0L)
            flag = false;
        else
            flag = nativeIsOutgoingVideoStarted();
        return flag;
    }

    public void kickMeetingMember(String s, String s1)
    {
        if(nativePeerObject != 0L)
            nativeKickMeetingMember(s, s1);
    }

    public native int nativeGetCurrentState();

    public void provideOutgoingVideoFrame(byte abyte0[], long l, int i)
    {
        if(nativePeerObject != 0L && getCurrentState() == GCommAppState.IN_MEETING_WITH_MEDIA)
            nativeProvideOutgoingVideoFrame(abyte0, l, i);
    }

    public void remoteMute(MeetingMember meetingmember)
    {
        if(nativePeerObject != 0L)
            nativeRemoteMute(meetingmember.getMucJid());
    }

    public boolean renderIncomingVideoFrame(int i)
    {
        boolean flag;
        if(nativePeerObject == 0L)
            flag = false;
        else
            flag = nativeRenderIncomingVideoFrame(i);
        return flag;
    }

    public void sendInstantMessage(String s)
    {
        if(nativePeerObject != 0L)
            nativeSendInstantMessage(s);
    }

    public void sendInstantMessageToUser(String s, String s1)
    {
        if(nativePeerObject != 0L)
            nativeSendInstantMessageToUser(s, s1);
    }

    public void sendRingStatus(String s, String s1, String s2)
    {
        if(nativePeerObject != 0L)
            nativeSendRingStatus(s, s1, s2);
    }

    public void setAudioMute(boolean flag)
    {
        if(nativePeerObject != 0L)
            nativeSetAudioMute(flag);
    }

    public void setIncomingAudioVolume(int i)
    {
        if(nativePeerObject != 0L)
        {
            if(i < 0 || i > 255)
                throw new IllegalArgumentException((new StringBuilder("level is ")).append(i).toString());
            nativeSetIncomingAudioVolume(i);
        }
    }

    public void setIncomingVideoParameters(int i, int j, int k, ScalingMode scalingmode, int l)
    {
        if(nativePeerObject != 0L)
            nativeSetIncomingVideoParameters(i, j, k, scalingmode.ordinal(), l);
    }

    public boolean setIncomingVideoRendererSurfaceSize(int i, int j, int k)
    {
        boolean flag;
        if(nativePeerObject == 0L)
            flag = false;
        else
            flag = nativeSetIncomingVideoRendererSurfaceSize(i, j, k);
        return flag;
    }

    public void setIncomingVideoSourceToSpeakerIndex(int i, int j)
    {
        if(nativePeerObject != 0L)
            nativeSetIncomingVideoSourceToSpeakerIndex(i, j);
    }

    public void setIncomingVideoSourceToUser(int i, String s)
    {
        if(nativePeerObject != 0L)
            nativeSetIncomingVideoSourceToUser(i, s);
    }

    public void setPresenceConnectionStatus(PresenceConnectionStatus presenceconnectionstatus)
    {
        if(nativePeerObject != 0L)
            nativeSetPresenceConnectionStatus(presenceconnectionstatus.ordinal());
    }

    public void signoutAndDisconnect()
    {
    	if(0L == nativePeerObject) {
    		return;
    	}
    	nativeSignoutAndDisconnect();
        if(nativePeerObject != 0L)
        {
            nativePeerDestroy(nativePeerObject);
            nativePeerObject = 0L;
        }
    }

    public int startIncomingVideoForSpeakerIndex(int i, int j, int k, int l)
    {
        int i1;
        if(nativePeerObject == 0L)
            i1 = 0;
        else
            i1 = nativeStartIncomingVideoForSpeakerIndex(i, j, k, l);
        return i1;
    }

    public int startIncomingVideoForUser(String s, int i, int j, int k)
    {
        int l;
        if(nativePeerObject == 0L)
            l = 0;
        else
            l = nativeStartIncomingVideoForUser(s, i, j, k);
        return l;
    }

    public void startOutgoingVideo(int i, int j)
    {
        if(nativePeerObject != 0L)
            nativeStartOutgoingVideo(i, j);
    }

    public void stopIncomingVideo(int i)
    {
        if(nativePeerObject != 0L)
            nativeStopIncomingVideo(i);
    }

    public void stopOutgoingVideo()
    {
        if(nativePeerObject != 0L)
            nativeStopOutgoingVideo();
    }

    public void uploadCallgrokLog()
    {
        if(nativePeerObject != 0L)
            nativeUploadCallgrokLog();
    }
    
    //=============================================================================
    //							Inner class
    //=============================================================================
    public static enum DeviceCaptureType {
    	LOW_RESOLUTION,
    	MEDIUM_RESOLUTION;
    }
    
    public static enum Error {
    	FATAL,
    	INCONSISTENT_STATE,
    	NETWORK,
    	AUTHENTICATION,
    	AUDIO_VIDEO_SESSION,
    	UNKNOWN;
    }
    
    public static enum MeetingEnterError {
    	UNKNOWN,
    	TIMEOUT,
    	BLOCKED_BY_SOMEONE_IN_HANGOUT,
    	BLOCKING_SOMEONE_IN_HANGOUT,
    	MAX_USERS,
    	SERVER,
    	MEDIA_START_TIMEOUT,
    	AUDIO_VIDEO_SESSION,
    	GREEN_ROOM_INFO,
    	OUTDATED_CLIENT,
    	HANGOUT_OVER,
    	HANGOUT_ON_AIR;
    }
    
    public static enum GCommAppState {
    	NONE,
    	START,
    	SIGNING_IN,
    	SIGNED_IN,
    	ENTERING_MEETING,
    	IN_MEETING_WITHOUT_MEDIA,
    	IN_MEETING_WITH_MEDIA;
    }
    
    public static enum PresenceConnectionStatus {
    	UNKNOWN,
    	CONNECTING,
    	JOINING,
    	CONNECTED;
    }
    
    public static enum ScalingMode {
    	ZOOM_OUT_TO_FIT,
    	ZOOM_IN_TO_FILL,
    	AUTO_ZOOM;
    }
    
    public static class FrameDimensionsChangedMessageParams
    {

        public final RectangleDimensions getDimensions()
        {
            return dimensions;
        }

        public final int getRequestID()
        {
            return requestID;
        }

        private final RectangleDimensions dimensions;
        private final int requestID;

        public FrameDimensionsChangedMessageParams(int i, RectangleDimensions rectangledimensions)
        {
            requestID = i;
            dimensions = rectangledimensions;
        }
    }
    
    static class RoomEntry
    {

        public String toString()
        {
            return displayRoomName;
        }

        private final String displayRoomName;
        private final Date lastEnterTime = new Date();

        RoomEntry(String s)
        {
            displayRoomName = s;
        }
    }
    
    public static class VideoSourceChangedMessageParams
    {

    	private final int requestID;
        private final MeetingMember source;
        private final boolean videoAvailable;

        public VideoSourceChangedMessageParams(int i, MeetingMember meetingmember, boolean flag)
        {
            requestID = i;
            source = meetingmember;
            videoAvailable = flag;
        }
        
        public final int getRequestID()
        {
            return requestID;
        }

        public final MeetingMember getSource()
        {
            return source;
        }

        public final boolean isVideoAvailable()
        {
            return videoAvailable;
        }

    }
}
