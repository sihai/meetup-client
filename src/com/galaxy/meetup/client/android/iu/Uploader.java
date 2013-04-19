/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

/**
 * 
 * @author sihai
 *
 */
public interface Uploader {

	public static final class LocalIoException extends Exception
    {

        private static final long serialVersionUID = 0xecc8fdff8a35df4fL;

        public LocalIoException(Throwable throwable)
        {
            super(throwable);
        }
    }

    public static final class MediaFileChangedException extends Exception
    {

        private static final long serialVersionUID = 0x85108150b94d7d1bL;

        public MediaFileChangedException(String s)
        {
            super(s);
        }
    }

    public static final class MediaFileUnavailableException extends Exception
    {

        private static final long serialVersionUID = 0x595e5cbc2f7089cfL;

        public MediaFileUnavailableException(Throwable throwable)
        {
            super(throwable);
        }
    }

    public static final class PicasaQuotaException extends Exception
    {

        private static final long serialVersionUID = 0xffaff9003807cb94L;

        public PicasaQuotaException(String s)
        {
            super(s);
        }
    }

    public static final class RestartException extends Exception
    {

        private static final long serialVersionUID = 0xdc41cfb411ac01f7L;

        public RestartException(String s)
        {
            super(s);
        }
    }

    public static final class UnauthorizedException extends Exception
    {

        private static final long serialVersionUID = 0x67c1af976118ea4bL;

        public UnauthorizedException(String s)
        {
            super(s);
        }

        public UnauthorizedException(Throwable throwable)
        {
            super(throwable);
        }
    }

    public static final class UploadException extends Exception
    {

        private static final long serialVersionUID = 0x3f648ea2c7cd3e6dL;

        public UploadException(String s)
        {
            super(s);
        }

        public UploadException(String s, Throwable throwable)
        {
            super(s, throwable);
        }
    }

    public static interface UploadProgressListener
    {

        public abstract void onFileChanged(UploadTaskEntry uploadtaskentry);

        public abstract void onProgress(UploadTaskEntry uploadtaskentry);
    }
}
