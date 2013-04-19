/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import android.util.Log;

import com.android.gallery3d.common.Utils;

/**
 * 
 * @author sihai
 *
 */
public class R {

	public static final class id
    {

        public static int ps_progress;
        public static int ps_status;

        public id()
        {
        }
    }

    public static final class layout
    {

        public static int ps_cache_notification;

        public layout()
        {
        }
    }

    public static final class string
    {

        public static int ps_cache_done;
        public static int ps_cache_status;

        public string()
        {
        }
    }


    public R()
    {
    }

    private static void copyStaticMembers(Class class1, Class class2)
    {
        Field afield[] = class1.getDeclaredFields();
        int i = afield.length;
        int j = 0;
        do
        {
            if(j >= i)
                break;
            Field field = afield[j];
            try
            {
                Field field1 = class2.getDeclaredField(field.getName());
                Utils.assertTrue(Modifier.isStatic(field1.getModifiers()));
                field.set(null, field1.get(null));
            }
            catch(NoSuchFieldException nosuchfieldexception)
            {
                throw new AssertionError((new StringBuilder("resource not found: ")).append(field.getName()).toString());
            }
            catch(Exception exception)
            {
                Log.w("PicasaSync.R", "fail to set resource", exception);
                throw new AssertionError((new StringBuilder("cannot set resource : ")).append(field.getName()).toString());
            }
            j++;
        } while(true);
    }

    public static void init(Class class1)
    {
        HashMap hashmap = new HashMap();
        Class aclass[] = class1.getDeclaredClasses();
        int i = aclass.length;
        for(int j = 0; j < i; j++)
        {
            Class class4 = aclass[j];
            hashmap.put(class4.getSimpleName(), class4);
        }

        Class aclass1[] = R.class.getDeclaredClasses();
        int k = aclass1.length;
        for(int l = 0; l < k; l++)
        {
            Class class2 = aclass1[l];
            String s = class2.getSimpleName();
            Class class3 = (Class)hashmap.get(s);
            if(class3 == null)
                throw new AssertionError((new StringBuilder("resource not found: ")).append(s).toString());
            copyStaticMembers(class2, class3);
        }

    }
}
