/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccountsData;

/**
 * 
 * @author sihai
 *
 */
public enum Property {
	
	ENABLE_DOGFOOD_FEATURES("ENABLE_DOGFOOD_FEATURES", 0, "debug.plus.dogfood", "false", true),
	AUTH_URL("AUTH_URL", 1, "debug.plus.auth.url", "http://192.168.10.101:8080/gateway.jhtml"),
    AUTH_EMAIL("AUTH_EMAIL", 2, "debug.plus.auth.email", "sihai"),
    AUTH_PASSWORD("AUTH_PASSWORD", 3, "debug.plus.auth.password"),
    PLUS_APIARY_AUTH_TOKEN("PLUS_APIARY_AUTH_TOKEN", 23, "debug.plus.apiary_token"),
    ENABLE_SQUARES("ENABLE_SQUARES", 50, "debug.plus.enable_squares", "FALSE", "1128676a", false),
    PLUS_BACKEND_URL("PLUS_BACKEND_URL", 24, "debug.plus.backend.url"),
    PLUS_FRONTEND_URL("PLUS_FRONTEND_URL", 22, "debug.plus.frontend.url", "http://192.168.10.101:8080/gateway.jhtml"),
    PLUS_FRONTEND_PORT("PLUS_FRONTEND_PORT", 100, "debug.plus.frontend.port", "8080"),
    PLUS_FRONTEND_PATH("PLUS_FRONTEND_PATH", 21, "debug.plus.frontend.path", "/gateway.jhtml"),
    TRACING_TOKEN("TRACING_TOKEN", 32, "debug.plus.tracing_token"),
    TRACING_TOKEN_2("TRACING_TOKEN_2", 33, "debug.plus.tracing_token2"),
    TRACING_PATH("TRACING_PATH", 34, "debug.plus.tracing_path", ".*"),
    TRACING_LEVEL("TRACING_LEVEL", 35, "debug.plus.tracing_level"),
    ENABLE_EMOTISHARE("ENABLE_EMOTISHARE", 43, "debug.plus.enable.emotishare", "FALSE", "2dab999b", false),
    ENABLE_STREAM_GIF_ANIMATION("ENABLE_STREAM_GIF_ANIMATION", 47, "debug.plus.enable.streamanim", "TRUE"),
    LOCATION_DEBUGGING("LOCATION_DEBUGGING", 29, "debug.plus.location.toast", "FALSE"),
    PLUS_CLIENTID("PLUS_CLIENTID", 19, "debug.plus.clientid", "862067606707.apps.googleusercontent.com"),
    NATIVE_HANGOUT_LOG("NATIVE_HANGOUT_LOG", 5, "debug.plus.hangout.native", "FALSE"),
    NATIVE_WRAPPER_HANGOUT_LOG_LEVEL("NATIVE_WRAPPER_HANGOUT_LOG_LEVEL", 6, "debug.plus.hangout.tag.wrapper", "WARNING"),
    ENABLE_HANGOUT_RECORD_ABUSE("ENABLE_HANGOUT_RECORD_ABUSE", 14, "debug.plus.enable.rec_abuse", "FALSE"),
    HANGOUT_CAMERA_ORIENTATION("HANGOUT_CAMERA_ORIENTATION", 7, "debug.plus.camera.orientation", ""),
    HANGOUT_CAMERA_MIRRORED("HANGOUT_CAMERA_MIRRORED", 8, "debug.plus.camera.mirrored", "FALSE"),
    ENABLE_HANGOUT_SWITCH("ENABLE_HANGOUT_SWITCH", 10, "debug.plus.hangout.switch", "FALSE"),
    HANGOUT_STRESS_MODE("HANGOUT_STRESS_MODE", 9, "debug.plus.hangout.stress", "FALSE"),
    ENABLE_HANGOUT_RECORD_ABUSE_INTERSTITIAL("ENABLE_HANGOUT_RECORD_ABUSE_INTERSTITIAL", 15, "debug.plus.rec_abuse.warning", "FALSE"),
    ACTIVE_HANGOUT_MODE("ACTIVE_HANGOUT_MODE", 17, "debug.plus.hangout.active_mode", "DISABLE"),
    WARM_WELCOME_ON_LOGIN("WARM_WELCOME_ON_LOGIN", 28, "debug.plus.warm.welcome", "FALSE"),
    PLUS_STATICMAPS_API_KEY("PLUS_STATICMAPS_API_KEY", 20, "debug.plus.staticmaps.api_key", "AIzaSyAYfoSs86LzFMXNWJhyeGtZp0ijdZb_uGU", false),
    FORCE_HANGOUT_RECORD_ABUSE("FORCE_HANGOUT_RECORD_ABUSE", 16, "debug.plus.force.rec_abuse", "FALSE"),
    ENABLE_HANGOUT_FILMSTRIP_STATUS("ENABLE_HANGOUT_FILMSTRIP_STATUS", 13, "debug.plus.hangout.strip_icon", "FALSE"),
    ENABLE_HANGOUT_STAGE_STATUS("ENABLE_HANGOUT_STAGE_STATUS", 12, "debug.plus.hangout.stage_icon", "FALSE"),
    EMOTISHARE_GEN1_DATE("EMOTISHARE_GEN1_DATE", 44, "debug.plus.emotishare.gen1", "0", false),
    EMOTISHARE_GEN2_DATE("EMOTISHARE_GEN2_DATE", 45, "debug.plus.emotishare.gen2", Long.toString((new GregorianCalendar(2012, 11, 27)).getTimeInMillis()), false),
    EMOTISHARE_GEN3_DATE("EMOTISHARE_GEN3_DATE", 46, "debug.plus.emotishare.gen3", Long.toString((new GregorianCalendar(2013, 1, 1)).getTimeInMillis()), false),
    POS_FRONTEND_URL("POS_FRONTEND_URL", 25, "debug.pos.frontend.url", "www.googleapis.com"),
    POS_BACKEND_URL("POS_BACKEND_URL", 27, "debug.pos.backend.url"),
    POS_FRONTEND_PATH("POS_FRONTEND_PATH", 26, "debug.pos.frontend.path", "/pos/v1/"),
    ENABLE_VOLLEY_IMAGE_DOWNLOAD("ENABLE_VOLLEY_IMAGE_DOWNLOAD", 49, "debug.plus.volley_images", "FALSE"),
    ENABLE_ADVANCED_HANGOUTS("ENABLE_ADVANCED_HANGOUTS", 11, "debug.plus.hangout.enable_adv", "TRUE"),
    ENABLE_SHAKE_GLOBAL_ACTION("ENABLE_SHAKE_GLOBAL_ACTION", 36, "debug.plus.enable_shake_action", "FALSE"),
    ENABLE_REWIEWS("ENABLE_REWIEWS", 39, "debug.plus.enable_reviews", "FALSE");
	
	private static Properties sProperties;
	
	private final boolean mCanOverride;
    private final String mDefaultValue;
    private final String mExperimentId;
    private final String mKey;
    
	private Property(String s, int i, String s1) {
		this(s, i, s1, null, true);
	}

	private Property(String s, int i, String s1, String s2) {
		this(s, i, s1, s2, true);
	}

	private Property(String s, int i, String s1, String s2, boolean flag) {
		this(s, i, s1, s2, null, flag);
	}
	
	private Property(String s, int i, String s1, String s2, String s3, boolean flag) {
		mKey = s1;
		mDefaultValue = getDefaultProperty(s1, s2);
		mExperimentId = s3;
		mCanOverride = flag;
	}
	
	private static String getDefaultProperty(String s, String s1) {
		if (sProperties == null) {
			sProperties = new Properties();
			InputStream inputstream = Property.class.getClassLoader().getResourceAsStream("com/google/android/apps/plusone/debug.prop");
			if (inputstream != null)
				try {
					sProperties.load(inputstream);
				} catch (IOException ioexception) {
					Log.e("EsProperty", "Cannot load debug.prop");
				}
		}
		if (sProperties.containsKey(s))
			s1 = sProperties.getProperty(s);
		return s1;
    }
	
	public static List getExperimentIds() {
		List list = new ArrayList();
		Property aproperty[] = values();
		int i = aproperty.length;
		for (int j = 0; j < i; j++) {
			Property property = aproperty[j];
			if (property.mExperimentId != null)
				list.add(property.mExperimentId);
		}

		return list;
	}
	
	public final String get() {
		String s;
		if (mExperimentId != null)
			s = EsAccountsData.getExperiment(mExperimentId, mDefaultValue);
		else
			s = mDefaultValue;
		if (mCanOverride)
			s = SystemProperties.get(mKey, s);
		return s;
	}

	public final boolean getBoolean() {
		return "TRUE".equalsIgnoreCase(get());
	}
}
