/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.external;

/**
 * 
 * @author sihai
 *
 */
public class PlatformContract {

	public static final class AccountContent extends ContentBase
    {

        public static final String COLUMNS[] = {
            "account"
        };

    }

    public static class ContentBase
    {
    }

    public static final class PlusOneContent extends ContentBase
    {

        public static final String COLUMNS[] = {
            "uri", "count", "state", "token"
        };
        public static final Integer STATE_ANONYMOUS = Integer.valueOf(-1);
        public static final Integer STATE_NOTPLUSONED = Integer.valueOf(0);
        public static final Integer STATE_PLUSONED = Integer.valueOf(1);

    }
}
