/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.MeetupFeedback;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ShakeDetector;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.hangout.HangoutTile.HangoutTileActivity;
import com.galaxy.meetup.client.android.hangout.crash.CrashReport;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.view.Tile;
import com.galaxy.meetup.client.util.HelpUrl;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class HangoutActivity extends EsFragmentActivity implements ImageGetter,
		HangoutTileActivity {

	
	private Hangout.Info hangoutInfo;
    private final Tile.ParticipantPresenceListener hangoutParticipantPresenceListener = new HangoutParticipantPresenceListener();
    private EsAccount mAccount;
    HangoutTile mHangoutTile;
    private boolean mShakeDetectorWasRunning;
    private boolean mSkipGreenRoom;
    private boolean mSkipMinorWarning;
    
    
    public HangoutActivity()
    {
        mSkipGreenRoom = false;
    }

    private boolean canTransfer() {
    	
    	try {
    		GCommNativeWrapper.GCommAppState gcommappstate = GCommApp.getInstance(this).getGCommNativeWrapper().getCurrentState();
    		boolean flag;
    		boolean flag1 = Property.ENABLE_HANGOUT_SWITCH.getBoolean();
	        flag = false;
	        if(flag1)
	        {
	            GCommNativeWrapper.GCommAppState gcommappstate1 = GCommNativeWrapper.GCommAppState.IN_MEETING_WITH_MEDIA;
	            flag = false;
	            if(gcommappstate == gcommappstate1)
	                flag = true;
	        }
	        return flag;
    	} catch (LinkageError linkageerror) {
    		return false;
    	}

    }

    private void displayParticipantsInTray()
    {
        mHangoutTile.setParticipants(null, null);
    }

    public final void blockPerson(Serializable serializable)
    {
        mHangoutTile.blockPerson(serializable);
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public Drawable getDrawable(String s)
    {
        BitmapDrawable bitmapdrawable = null;
        Resources resources = getResources();
        Bitmap bitmap = null;
        if("block_icon".equals(s))
        {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.icn_drop_block_unpadded);
        } else if("exit_icon".equals(s))
        {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.hangout_ic_menu_exit_unpadded);
        }
        if(null != bitmap) {
	        bitmapdrawable = new BitmapDrawable(resources, bitmap);
	        bitmapdrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
        return bitmapdrawable;
    }

    public final Intent getGreenRoomParticipantListActivityIntent(List arraylist)
    {
        return Intents.getHangoutParticipantListActivityIntent(this, mHangoutTile.getAccount(), arraylist);
    }

    public final Intent getHangoutNotificationIntent()
    {
        GCommNativeWrapper gcommnativewrapper = GCommApp.getInstance(this).getGCommNativeWrapper();
        Intent intent = getIntent();
        boolean flag = intent.hasExtra("audience");
        AudienceData audiencedata = null;
        if(flag)
            audiencedata = (AudienceData)intent.getParcelableExtra("audience");
        Intent intent1 = Intents.getHangoutActivityAudienceIntent(this, gcommnativewrapper.getAccount(), gcommnativewrapper.getHangoutInfo(), mSkipGreenRoom, audiencedata);
        intent1.putExtra("hangout_skip_minor_warning", mSkipMinorWarning);
        return intent1;
    }

    public final Intent getParticipantListActivityIntent()
    {
        List list = GCommApp.getInstance(this).getGCommNativeWrapper().getMeetingMembersOrderedByEntry();
        ArrayList arraylist = new ArrayList(list.size());
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            MeetingMember meetingmember = (MeetingMember)iterator.next();
            if(!meetingmember.isSelf())
            {
                String s = "";
                if(meetingmember.getVCard() != null)
                    s = meetingmember.getVCard().getFullName();
                // TODO
                //arraylist.add(com.google.wireless.realtimechat.proto.Data.Participant.newBuilder().setParticipantId(meetingmember.getId()).setFullName(s).setFirstName(Hangout.getFirstNameFromFullName(s)).build());
            }
        } while(true);
        return Intents.getHangoutParticipantListActivityIntent(this, mHangoutTile.getAccount(), arraylist);
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.HANGOUT;
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
        mHangoutTile.onActivityResult(i, j, intent);
        super.onActivityResult(i, j, intent);
    }

    public final void onBlockCompleted(boolean flag)
    {
    }

    protected void onCreate(Bundle bundle) {
    	
    	int i;
	    String s;
    	View view;
    	AlertFragmentDialog alertfragmentdialog;
    	
    	try {
	        super.onCreate(bundle);
	        Log.debug((new StringBuilder("HangoutActivity.onCreate: ")).append(this).toString());
	        GCommApp.getInstance(this).getGCommNativeWrapper().getCurrentState();
	        ActionBar actionbar;
	        Intent intent = getIntent();
	        mAccount = (EsAccount)intent.getParcelableExtra("account");
	       
	        boolean flag;
	        boolean flag1;
	        boolean flag2;
	        List arraylist;
	        int j;
	        android.view.ViewGroup.LayoutParams layoutparams;
	        MinorWarningDialog minorwarningdialog;
	        AbuseWarningDialog abusewarningdialog;
	        if(mAccount.isChild() && !EsAccountsData.hasSeenMinorHangoutWarningDialog(this, mAccount) && !intent.getBooleanExtra("hangout_skip_minor_warning", false))
	            flag = true;
	        else
	            flag = false;
	        if(Property.ENABLE_HANGOUT_RECORD_ABUSE.getBoolean() && Property.ENABLE_HANGOUT_RECORD_ABUSE_INTERSTITIAL.getBoolean() && !EsAccountsData.hasSeenReportAbusetWarningDialog(this, mAccount))
	            flag1 = true;
	        else
	            flag1 = false;
	        if(!flag && !flag1 && intent.getBooleanExtra("hangout_skip_green_room", false))
	            flag2 = true;
	        else
	            flag2 = false;
	        mSkipGreenRoom = flag2;
	        hangoutInfo = (Hangout.Info)intent.getSerializableExtra("hangout_info");
	        arraylist = (ArrayList)intent.getSerializableExtra("hangout_participants");
	        if(hangoutInfo != null && (hangoutInfo.getLaunchSource() == Hangout.LaunchSource.Ring || hangoutInfo.getLaunchSource() == Hangout.LaunchSource.Transfer))
	            getWindow().addFlags(0x680080);
	        else
	            HangoutRingingActivity.stopRingActivity();
	        j = android.os.Build.VERSION.SDK_INT;
	        actionbar = null;
	        if(j >= 11)
	            actionbar = getActionBar();
	        if(actionbar != null)
	            actionbar.setDisplayHomeAsUpEnabled(true);
	        
	        if(!Hangout.isAdvancedUiSupported(this)) {
	        	mHangoutTile = new HangoutPhoneTile(this);
	            if(actionbar != null)
	                actionbar.hide();
	            
	        } else { 
	        	mHangoutTile = new HangoutTabletTile(this);
	        }
	        
	        layoutparams = new android.view.ViewGroup.LayoutParams(-1, -1);
	        setContentView(mHangoutTile, layoutparams);
	        mHangoutTile.setHangoutInfo(mAccount, hangoutInfo, arraylist, true, mSkipGreenRoom);
	        mHangoutTile.onCreate(bundle);
	        if(flag)
	        {
	            minorwarningdialog = new MinorWarningDialog();
	            minorwarningdialog.setCancelable(false);
	            minorwarningdialog.show(getSupportFragmentManager(), "warning");
	        }
	        if(flag1)
	        {
	            abusewarningdialog = new AbuseWarningDialog();
	            abusewarningdialog.setCancelable(false);
	            abusewarningdialog.show(getSupportFragmentManager(), "warning");
	        }
    	} catch (LinkageError linkageerror) {
    		view = new View(this);
            view.setBackgroundColor(getResources().getColor(R.color.clear));
            setContentView(view);
            i = R.string.hangout_native_lib_error;
            s = getResources().getString(i);
            Log.debug("showError: message=%s", new Object[] {
                s
            });
            alertfragmentdialog = AlertFragmentDialog.newInstance(null, s, getResources().getString(R.string.ok), null, 0x1080027);
            alertfragmentdialog.setCancelable(false);
            alertfragmentdialog.setListener(new AlertFragmentDialog.AlertDialogListener() {

                public final void onDialogCanceled(String s1)
                {
                }

                public final void onDialogListClick(int k, Bundle bundle1)
                {
                }

                public final void onDialogNegativeClick(String s1)
                {
                }

                public final void onDialogPositiveClick(Bundle bundle1, String s1)
                {
                    finish();
                }

            });
            
            alertfragmentdialog.show(getSupportFragmentManager(), "error");

    	}
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuinflater = getMenuInflater();
        menuinflater.inflate(R.menu.hangout_menu, menu);
        menuinflater.inflate(R.menu.hangout_transfer, menu);
        menu.findItem(R.id.hangout_transfer_menu_item).setVisible(canTransfer());
        menu.findItem(R.id.help).setVisible(false);
        menu.findItem(R.id.feedback).setVisible(false);
        if(GCommApp.isDebuggable(this))
        {
            android.view.SubMenu submenu = menu.addSubMenu("Debug");
            menuinflater.inflate(R.menu.hangout_debug, submenu);
        }
        if(mHangoutTile != null)
            mHangoutTile.onCreateOptionsMenu(menu, menuinflater);
        return super.onCreateOptionsMenu(menu);
    }

    public final void onMeetingMediaStarted()
    {
    }

    protected void onNewIntent(Intent intent)
    {
        Log.debug((new StringBuilder("onNewIntent:")).append(this).toString());
        setIntent(intent);
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(!mHangoutTile.onOptionsItemSelected(menuitem))
            if(i == 0x102002c)
                goHome(mAccount);
            else
            if(i == R.id.help)
                startExternalActivity(new Intent("android.intent.action.VIEW", Uri.parse(getResources().getString(R.string.hangout_help_url))));
            else
            if(i == R.id.feedback)
            {
                recordUserAction(OzActions.SETTINGS_FEEDBACK);
                MeetupFeedback.launch(this);
            } else
            if(i == R.id.menu_hangout_debug_upload_logs)
            {
                CrashReport crashreport = new CrashReport(flag);
                try
                {
                    throw new Exception("Dummy exception for testing crash reports");
                }
                catch(Exception exception)
                {
                    crashreport.generateReport(CrashReport.computeJavaCrashSignature(exception));
                }
                crashreport.send(this, false);
            } else
            if(i == R.id.menu_hangout_debug_simulate_network_error)
                GCommApp.getInstance(this).raiseNetworkError();
            else
            if(i == R.id.hangout_transfer_menu_item)
                mHangoutTile.transfer();
            else
                flag = super.onOptionsItemSelected(menuitem);
        return flag;
    }

    protected void onPause()
    {
        if(mHangoutTile != null)
        {
            mHangoutTile.onTilePause();
            mHangoutTile.onPause();
        }
        super.onPause();
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.hangout_transfer_menu_item).setVisible(canTransfer());
        if(mHangoutTile != null)
            mHangoutTile.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onResume()
    {
        super.onResume();
        if(mHangoutTile != null)
        {
            mHangoutTile.onResume();
            mHangoutTile.onTileResume();
        }
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mHangoutTile != null)
            mHangoutTile.onSaveInstanceState(bundle);
    }

    public void onStart()
    {
        super.onStart();
        if(mHangoutTile != null)
        {
            mHangoutTile.onStart();
            mHangoutTile.onTileStart();
            displayParticipantsInTray();
            mHangoutTile.addParticipantPresenceListener(hangoutParticipantPresenceListener);
        }
        ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
        if(shakedetector != null)
            mShakeDetectorWasRunning = shakedetector.stop();
    }

    protected void onStop()
    {
        super.onStop();
        if(mHangoutTile != null)
        {
            mHangoutTile.removeParticipantPresenceListener(hangoutParticipantPresenceListener);
            mHangoutTile.onTileStop();
            mHangoutTile.onStop();
        }
        if(mShakeDetectorWasRunning)
        {
            ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
            if(shakedetector != null)
                shakedetector.start();
        }
    }

    public final void stopHangoutTile()
    {
        finish();
    }
    
    
    //================================================================================
    //								Inner class
    //================================================================================
    private final class AbuseWarningDialog extends AlertFragmentDialog
    {

        public final Dialog onCreateDialog(Bundle bundle)
        {
            android.content.Context context = getDialogContext();
            View view = LayoutInflater.from(context).inflate(R.layout.hangout_abuse_dialog, null);
            TextView textview = (TextView)view.findViewById(R.id.reportAbuseLink);
            String s = getString(R.string.hangout_abuse_learn_more);
            SpannableString spannablestring = new SpannableString(s);
            final Uri url = HelpUrl.getHelpUrl(getActivity(), "plusone_promo_abuse");
            spannablestring.setSpan(new ClickableSpan() {
            	public final void onClick(View view)
                {
                    Intent intent = new Intent("android.intent.action.VIEW", url);
                    intent.addFlags(0x80000);
                    startActivity(intent);
                }
            }, 0, s.length(), 33);
            textview.setText(spannablestring);
            textview.setMovementMethod(LinkMovementMethod.getInstance());
            textview.setClickable(true);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setCancelable(false).setIcon(0x1080027).setView(view).setTitle(R.string.hangout_abuse_warning_header).setPositiveButton(R.string.hangout_abuse_ok_button_text, new android.content.DialogInterface.OnClickListener() {

                public final void onClick(DialogInterface dialoginterface, int i)
                {
                    EsAccountsData.saveReportAbuseWarningDialogSeenPreference(getActivity(), mAccount, true);
                }
            });
            return builder.create();
        }
    }

    private final class HangoutParticipantPresenceListener
        implements Tile.ParticipantPresenceListener
    {

        public final void onParticipantPresenceChanged()
        {
            displayParticipantsInTray();
        }
    }

    private final class MinorWarningDialog extends AlertFragmentDialog
    {

        public final Dialog onCreateDialog(Bundle bundle)
        {
            android.content.Context context = getDialogContext();
            View view = LayoutInflater.from(context).inflate(R.layout.hangout_minor_dialog, null);
            final CheckBox checkbox = (CheckBox)view.findViewById(R.id.minorHangoutDontShow);
            ((TextView)view.findViewById(R.id.minorHangoutMessage)).setText(Html.fromHtml(getResources().getString(R.string.hangout_minor_warning_message), HangoutActivity.this, null));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setCancelable(false).setIcon(0x1080027).setView(view).setTitle(R.string.hangout_minor_warning_header).setPositiveButton(R.string.hangout_minor_ok_button_text, new android.content.DialogInterface.OnClickListener() {

                public final void onClick(DialogInterface dialoginterface, int i)
                {
                    if(checkbox.isChecked())
                        EsAccountsData.saveMinorHangoutWarningDialogSeenPreference(getActivity(), mAccount, true);
                    mSkipMinorWarning = true;
                }
            
            });
            return builder.create();
        }
    }
}
