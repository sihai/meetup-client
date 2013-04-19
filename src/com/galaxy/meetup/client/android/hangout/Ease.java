/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.animation.TimeInterpolator;

/**
 * 
 * @author sihai
 *
 */
public class Ease {

	static final class Quad
    {

        public static final TimeInterpolator easeIn = new TimeInterpolator() {

            public final float getInterpolation(float f)
            {
                float f1 = f / 1.0F;
                return 0.0F + f1 * (1.0F * f1);
            }

        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {

            public final float getInterpolation(float f)
            {
                float f1 = f / 0.5F;
                float f3;
                if(f1 < 1.0F)
                {
                    f3 = 0.0F + f1 * (0.5F * f1);
                } else
                {
                    float f2 = f1 - 1.0F;
                    f3 = 0.0F + -0.5F * (f2 * (f2 - 2.0F) - 1.0F);
                }
                return f3;
            }

        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {

            public final float getInterpolation(float f)
            {
                float f1 = f / 1.0F;
                return 0.0F + -1F * f1 * (f1 - 2.0F);
            }

        };

    }

    static final class Quart
    {

        public static final TimeInterpolator easeIn = new TimeInterpolator() {

            public final float getInterpolation(float f)
            {
                float f1 = f / 1.0F;
                return 0.0F + f1 * (f1 * (f1 * (1.0F * f1)));
            }

        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {

            public final float getInterpolation(float f)
            {
                float f1 = f / 0.5F;
                float f3;
                if(f1 < 1.0F)
                {
                    f3 = 0.0F + f1 * (f1 * (f1 * (0.5F * f1)));
                } else
                {
                    float f2 = f1 - 2.0F;
                    f3 = 0.0F + -0.5F * (f2 * (f2 * (f2 * f2)) - 2.0F);
                }
                return f3;
            }

        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {

            public final float getInterpolation(float f)
            {
                float f1 = f / 1.0F - 1.0F;
                return 0.0F + -1F * (f1 * (f1 * (f1 * f1)) - 1.0F);
            }

        };

    }
}
