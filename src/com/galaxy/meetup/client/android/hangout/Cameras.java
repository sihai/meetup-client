/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.hardware.Camera;

/**
 * 
 * @author sihai
 *
 */
public class Cameras {

	private static Method cameraGetCameraInfo;
    private static Method cameraGetNumberOfCameras;
    private static Class cameraInfoClass;
    private static Field cameraInfoFacing;
    private static int cameraInfoFrontFacingConstant;
    private static Field cameraInfoOrientation;
    private static Method cameraOpen;
    private static boolean gingerbreadCameraApiSupported;
    private static SelectedCameras selectedCameras;

    static 
    {
        try
        {
            cameraInfoClass = Class.forName("android.hardware.Camera$CameraInfo");
            Class aclass[] = new Class[1];
            aclass[0] = Integer.TYPE;
            cameraOpen = Camera.class.getMethod("open", aclass);
            cameraGetNumberOfCameras = Camera.class.getMethod("getNumberOfCameras", new Class[0]);
            Class aclass1[] = new Class[2];
            aclass1[0] = Integer.TYPE;
            aclass1[1] = cameraInfoClass;
            cameraGetCameraInfo = Camera.class.getMethod("getCameraInfo", aclass1);
            cameraInfoFacing = cameraInfoClass.getField("facing");
            cameraInfoOrientation = cameraInfoClass.getField("orientation");
            cameraInfoFrontFacingConstant = cameraInfoClass.getField("CAMERA_FACING_FRONT").getInt(null);
            gingerbreadCameraApiSupported = true;
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            throw new IllegalStateException(illegalaccessexception);
        }
        catch(NoSuchMethodException nosuchmethodexception) { }
        catch(NoSuchFieldException nosuchfieldexception) { }
        catch(ClassNotFoundException classnotfoundexception) { }
        if(gingerbreadCameraApiSupported)
            selectedCameras = gingerbreadSelectCameras();
    }
    
	private static CameraProperties gingerbreadGetCameraProperties(int i) {
        boolean flag = true;
        if(!gingerbreadCameraApiSupported)
            throw new IllegalStateException("Gingerbread camera API not supported");
        
        try {
        	Object obj = cameraInfoClass.newInstance();
        	Method method = cameraGetCameraInfo;
        	Object aobj[] = new Object[2];
        	aobj[0] = Integer.valueOf(i);
        	aobj[1] = obj;
        	method.invoke(null, aobj);
        	CameraProperties cameraproperties;
        	if(cameraInfoFacing.getInt(obj) != cameraInfoFrontFacingConstant)
        		flag = false;
        	cameraproperties = new CameraProperties(flag, cameraInfoOrientation.getInt(obj));
        	return cameraproperties;
        } catch (IllegalAccessException e) {
        	throw new IllegalStateException(e);
        } catch (InstantiationException e) {
        	throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
        	throw new IllegalStateException(e);
        }
    }

    private static int gingerbreadGetNumberOfCameras()
    {
        if(!gingerbreadCameraApiSupported)
            throw new IllegalStateException("Gingerbread camera API not supported");
        int i;
        try
        {
            i = ((Integer)cameraGetNumberOfCameras.invoke(null, new Object[0])).intValue();
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            throw new IllegalStateException(illegalaccessexception);
        }
        catch(InvocationTargetException invocationtargetexception)
        {
            throw new IllegalStateException(invocationtargetexception);
        }
        return i;
    }

    private static Camera gingerbreadOpenCamera(int i)
    {
        if(!gingerbreadCameraApiSupported)
            throw new IllegalStateException("Gingerbread camera API not supported");
        Camera camera;
        try
        {
            Method method = cameraOpen;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(i);
            camera = (Camera)method.invoke(null, aobj);
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            throw new IllegalStateException(illegalaccessexception);
        }
        catch(InvocationTargetException invocationtargetexception)
        {
            throw new IllegalStateException(invocationtargetexception);
        }
        return camera;
    }

    private static SelectedCameras gingerbreadSelectCameras()
    {
        if(!gingerbreadCameraApiSupported)
            throw new IllegalStateException("Gingerbread camera API not supported");
        int i = -1;
        CameraProperties cameraproperties = null;
        int j = -1;
        CameraProperties cameraproperties1 = null;
        int k = 0;
        while(k < gingerbreadGetNumberOfCameras()) 
        {
            CameraProperties cameraproperties2 = gingerbreadGetCameraProperties(k);
            if(cameraproperties2.isFrontFacing())
            {
                if(i == -1)
                {
                    i = k;
                    cameraproperties = cameraproperties2;
                }
            } else
            if(j == -1)
            {
                j = k;
                cameraproperties1 = cameraproperties2;
            }
            k++;
        }
        return new SelectedCameras(j, cameraproperties1, i, cameraproperties);
    }

    public static boolean isAnyCameraAvailable()
    {
        boolean flag;
        if(isFrontFacingCameraAvailable() || isRearFacingCameraAvailable())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static boolean isFrontFacingCameraAvailable()
    {
        boolean flag = gingerbreadCameraApiSupported;
        boolean flag1 = false;
        if(flag)
        {
            int i = selectedCameras.frontFacingCameraId;
            flag1 = false;
            if(i != -1)
                flag1 = true;
        }
        return flag1;
    }

    public static boolean isRearFacingCameraAvailable()
    {
        boolean flag = true;
        if(gingerbreadCameraApiSupported && selectedCameras.rearFacingCameraId == -1)
            flag = false;
        return flag;
    }

    public static CameraResult open(CameraType cameratype)
    {
        CameraResult cameraresult;
        if(gingerbreadCameraApiSupported)
        {
            if(cameratype == CameraType.FrontFacing && !isFrontFacingCameraAvailable() || cameratype == CameraType.RearFacing && !isRearFacingCameraAvailable())
                throw new IllegalArgumentException("Requested camera type not available");
            int i;
            CameraProperties cameraproperties;
            if(cameratype == CameraType.FrontFacing)
            {
                i = selectedCameras.frontFacingCameraId;
                cameraproperties = selectedCameras.frontFacingCameraProperties;
            } else
            if(cameratype == CameraType.RearFacing)
            {
                i = selectedCameras.rearFacingCameraId;
                cameraproperties = selectedCameras.rearFacingCameraProperties;
            } else
            {
                throw new IllegalArgumentException("Unknown camera type");
            }
            cameraresult = new CameraResult(gingerbreadOpenCamera(i), cameraproperties);
        } else
        {
            if(cameratype == CameraType.FrontFacing)
                throw new IllegalArgumentException("Requested camera type not available");
            cameraresult = new CameraResult(Camera.open(), CameraProperties.FROYO_CAMERA_PROPERTIES);
        }
        return cameraresult;
    }

    
	
	public static enum CameraType {
		FrontFacing,
		RearFacing;
	}
	
	public static final class CameraProperties {

		public static final CameraProperties FROYO_CAMERA_PROPERTIES = new CameraProperties(false, 90);
        private final boolean frontFacing;
        private final int orientation;

        public CameraProperties(boolean flag, int i)
        {
            frontFacing = flag;
            orientation = i;
        }
        
        public final int getOrientation()
        {
            return orientation;
        }

        public final boolean isFrontFacing()
        {
            return frontFacing;
        }

    }

    public static final class CameraResult {

    	private final Camera camera;
        private final CameraProperties properties;

        public CameraResult(Camera camera1, CameraProperties cameraproperties)
        {
            camera = camera1;
            properties = cameraproperties;
        }
        
        public final Camera getCamera()
        {
            return camera;
        }

        public final CameraProperties getProperties()
        {
            return properties;
        }
    }
    
    private static final class SelectedCameras {

        public final int frontFacingCameraId;
        public final CameraProperties frontFacingCameraProperties;
        public final int rearFacingCameraId;
        public final CameraProperties rearFacingCameraProperties;

        public SelectedCameras(int i, CameraProperties cameraproperties, int j, CameraProperties cameraproperties1)
        {
            rearFacingCameraId = i;
            rearFacingCameraProperties = cameraproperties;
            frontFacingCameraId = j;
            frontFacingCameraProperties = cameraproperties1;
        }
    }
}
