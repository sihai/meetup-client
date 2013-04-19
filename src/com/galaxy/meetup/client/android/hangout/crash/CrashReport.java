/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout.crash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.hangout.Log;
import com.galaxy.meetup.client.util.Utils;

/**
 * 
 * @author sihai
 * 
 */
public class CrashReport {

	private static final List LOG_TAGS = Arrays.asList(new String[] {
			"GoogleMeeting", "gcomm_native", "libjingle", "DEBUG" });
	private String crashProcessingError;
	private final boolean isTestCrash = true;
	private final Map params = new HashMap();
	private String reportText;
	private String signature;

	public CrashReport(boolean flag)
    {
        crashProcessingError = "uninitialized non-null value";
        signature = "";
    }

    private static void appendNonHangoutLog(StringBuilder stringbuilder, String s)
    {
        stringbuilder.append(s);
        stringbuilder.append("\n");
    }

    public static String computeJavaCrashSignature(Throwable throwable)
    {
        int ai[] = new int[10];
        int i = -1;
        StackTraceElement astacktraceelement[] = throwable.getStackTrace();
        int j = Math.min(astacktraceelement.length, 10);
        for(int k = 0; k < j; k++)
        {
            int j1 = i + 1;
            int k1 = j1 % ai.length;
            ai[k1] = ai[k1] ^ astacktraceelement[k].getClassName().hashCode();
            i = j1 + 1;
            int l1 = i % ai.length;
            ai[l1] = ai[l1] ^ astacktraceelement[k].getMethodName().hashCode();
        }

        StringBuilder stringbuilder = new StringBuilder();
        int l = ai.length;
        for(int i1 = 0; i1 < l; i1++)
            stringbuilder.append(Integer.toHexString(ai[i1]));

        return stringbuilder.toString();
    }

    private boolean getSystemLogs()
    {
        boolean flag = true;
        try
        {
            Process process = Runtime.getRuntime().exec(new String[] {
                "logcat", "-d", "-v", "threadtime"
            });
            StringBuilder stringbuilder = new StringBuilder();
            processLogs(stringbuilder, process.getInputStream());
            reportText = stringbuilder.toString();
            crashProcessingError = "Logs successfully processed";
            process.destroy();
        }
        catch(IOException ioexception)
        {
            Log.error(ioexception.toString());
            Object aobj[] = new Object[2];
            aobj[0] = ioexception.toString();
            aobj[1] = android.util.Log.getStackTraceString(ioexception);
            crashProcessingError = String.format("Error getting system logs: %s\n%s", aobj);
            flag = false;
        }
        return flag;
    }

    private void processLogs(StringBuilder stringbuilder, InputStream inputstream)
        throws IOException
    {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
        boolean flag = false;
        Pattern pattern = Pattern.compile("[\\p{Digit}-]+ \\p{Space}+[\\p{Digit}\\.:]+ \\p{Space}+[\\p{Digit}]+ \\p{Space}+[\\p{Digit}]+ \\p{Space}+\\p{Upper} \\p{Space}+([^:]+):((.*))", 4);
        Pattern pattern1 = Pattern.compile("\\p{Space}+\\# [\\p{Digit}]+ \\p{Space}+pc \\p{Space}+(([\\p{XDigit}]{8})) \\p{Space}+[\\p{Alnum}/\\._-}]*libgcomm_jni\\.so", 4);
        do
        {
            String s = bufferedreader.readLine();
            if(s == null)
                break;
            Matcher matcher = pattern.matcher(s);
            if(!matcher.matches())
            {
                appendNonHangoutLog(stringbuilder, s);
            } else
            {
                String s1 = matcher.group(1).trim();
                String s2 = matcher.group(2);
                if(LOG_TAGS.indexOf(s1) < 0)
                {
                    appendNonHangoutLog(stringbuilder, s);
                } else
                {
                    stringbuilder.append(s);
                    stringbuilder.append("\n");
                    if(s1.equals("DEBUG"))
                    {
                        if(s2.contains("*** *** *** *** ***"))
                        {
                            flag = true;
                            signature = "";
                        }
                        if(flag)
                        {
                            Matcher matcher1 = pattern1.matcher(s2);
                            if(matcher1.find() && signature.length() < 80)
                            {
                                if(signature.length() > 0)
                                    signature = (new StringBuilder()).append(signature).append(",").toString();
                                signature = (new StringBuilder()).append(signature).append(matcher1.group(1)).toString();
                            }
                        }
                    }
                }
            }
        } while(true);
    }

    public final boolean generateReport(String s)
    {
        getSystemLogs();
        if(s != null)
            signature = s;
        params.put("prod", "Google_Plus_Android");
        Map map = params;
        String s1;
        boolean flag;
        if(isTestCrash)
            s1 = (new StringBuilder("Test-")).append(Utils.getVersion()).toString();
        else
            s1 = Utils.getVersion();
        map.put("ver", s1);
        params.put("sig", signature);
        params.put("sig2", signature);
        params.put("should_process", "F");
        params.put("build_board", Build.BOARD);
        params.put("build_brand", Build.BRAND);
        params.put("build_device", Build.DEVICE);
        params.put("build_id", Build.ID);
        params.put("build_manufacturer", Build.MANUFACTURER);
        params.put("build_model", Build.MODEL);
        params.put("build_product", Build.PRODUCT);
        params.put("build_type", Build.TYPE);
        params.put("version_codename", android.os.Build.VERSION.CODENAME);
        params.put("version_incremental", android.os.Build.VERSION.INCREMENTAL);
        params.put("version_release", android.os.Build.VERSION.RELEASE);
        params.put("version_sdk_int", Integer.toString(android.os.Build.VERSION.SDK_INT));
        if(isTestCrash)
            params.put("testing", "true");
        if(reportText != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void send(final Activity activity, final boolean flag) {
        (new AsyncTask() {

            protected final Object doInBackground(Object aobj[])
            {
                Boolean boolean1;
                if(reportText == null)
                {
                    params.put("comments", "Report unavailable");
                    Activity _tmp = activity;
                    boolean1 = Boolean.valueOf(CrashSender.sendReport(params, "filtered_log", crashProcessingError.getBytes()));
                } else
                {
                    Activity _tmp1 = activity;
                    boolean1 = Boolean.valueOf(CrashSender.sendReport(params, "filtered_log", reportText.getBytes()));
                }
                return boolean1;
            }

            protected final void onPostExecute(Object obj)
            {
                Boolean boolean1 = (Boolean)obj;
                Activity activity1 = activity;
                Activity activity2 = activity;
                int i;
                if(boolean1.booleanValue())
                    i = R.string.hangout_crash_report_sent_succeeded;
                else
                    i = R.string.hangout_crash_report_sent_failed;
                Toast.makeText(activity1, activity2.getString(i), 1).show();
                if(flag)
                    activity.finish();
            }
        }).execute(new Void[0]);
    }
}
