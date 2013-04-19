/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Build;

import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class Compatibility {

	public static int getCameraOrientation(Cameras.CameraProperties cameraproperties)
    {
        boolean flag;
        int i;
        if(Build.MANUFACTURER.equals("HTC") && (Build.MODEL.equals("PC36100") || Build.MODEL.equals("myTouch_4G") || Build.MODEL.equals("HTC Glacier") || Build.MODEL.equals("ADR6400L") || Build.MODEL.equals("HTC Incredible S S710e") || Build.MODEL.equals("A9191")))
            flag = true;
        else
            flag = false;
        if(flag && cameraproperties.isFrontFacing())
            i = 270;
        else
            try
            {
                i = Integer.parseInt(Property.HANGOUT_CAMERA_ORIENTATION.get());
                Log.info((new StringBuilder("Using camera orientation of: ")).append(i).toString());
            }
            catch(NumberFormatException numberformatexception)
            {
                i = cameraproperties.getOrientation();
            }
        return i;
    }

    public static List getSupportedPreviewSizes(android.hardware.Camera.Parameters parameters, Cameras.CameraProperties cameraproperties)
    {
        List list = parameters.getSupportedPreviewSizes();
        boolean flag;
        Object obj;
        if(Build.MANUFACTURER.equals("motorola") && Build.MODEL.equals("DROID3"))
            flag = true;
        else
            flag = false;
        if(flag && cameraproperties.isFrontFacing())
        {
            obj = new ArrayList(-1 + list.size());
            Iterator iterator = list.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                android.hardware.Camera.Size size = (android.hardware.Camera.Size)iterator.next();
                if(size.width != 240 || size.height != 160)
                    ((ArrayList) (obj)).add(size);
            } while(true);
        } else
        {
            obj = list;
        }
        return ((List) (obj));
    }
}
