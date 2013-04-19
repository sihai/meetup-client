/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsProvider;

/**
 * 
 * @author sihai
 *
 */
public class DumpDatabase {

	private final Context mContext;
    private final Handler mHandler;
    private ProgressDialog mProgressDialog;
    
    private DumpDatabase(Context context, DatabaseAction databaseaction)
    {
        mHandler = new Handler() {

            public final void handleMessage(Message message)
            {
                int i = message.arg1;
                mProgressDialog.setProgress(i);
            }
        };
        mContext = context;
        if(databaseaction == DatabaseAction.DUMP) {
        	mProgressDialog = ProgressDialog.show(mContext, "Dump database", "Dumping ...", false, false, null);
            (new DumpTask()).execute(new Void[] {
                null
            });
        } else if(databaseaction == DatabaseAction.CLEAN) {
        	mProgressDialog = ProgressDialog.show(mContext, "Clean database", "Cleaning ...", false, false, null);
            (new CleanTask()).execute(new Void[] {
                null
            });
        }
    }

    public static void cleanNow(Context context)
    {
        new DumpDatabase(context, DatabaseAction.CLEAN);
    }

    public static void dumpNow(Context context)
    {
        new DumpDatabase(context, DatabaseAction.DUMP);
    }
    
    
    final class CleanTask extends AsyncTask
    {

        private Void doInBackground()
        {
            String s;
            long l;
            EsAccount esaccount = EsAccountsData.getActiveAccount(mContext);
            int i = esaccount.getIndex();
            s = (new StringBuilder("es")).append(i).append(".db").toString();
            File file = mContext.getDatabasePath(s);
            File file2;
            if(file.exists() && file.isFile())
                l = file.length();
            else
                l = 0L;
            EsProvider.cleanupData(mContext, esaccount, true);
            file2 = mContext.getDatabasePath(s);
            Log.i("DumpDatabase", (new StringBuilder("Clean complete; orig size: ")).append(l).append(", copy size: ").append(file2.length()).toString());
            mProgressDialog.dismiss();
            return null;
        }

        protected final Object doInBackground(Object aobj[])
        {
            return doInBackground();
        }
    }

    private static enum DatabaseAction
    {
    	DUMP,
    	CLEAN;
    }

    final class DumpTask extends AsyncTask
    {

        private File doDump(String s, String s1) throws IOException
        {
            BufferedOutputStream bufferedoutputstream = null;
            BufferedInputStream bufferedinputstream = null;
            File file;
            File file1;
            bufferedoutputstream = null;
            bufferedinputstream = null;
            file = new File(Environment.getExternalStorageDirectory(), s1);
            if(file.exists())
                file.delete();
            file1 = mContext.getDatabasePath(s);
            BufferedOutputStream bufferedoutputstream1;
            BufferedInputStream bufferedinputstream1;
            try
            {
                file.createNewFile();
                bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file));
                bufferedinputstream = new BufferedInputStream(new FileInputStream(file1));
                byte abyte0[] = new byte[16384];
                do
                {
                    int i = bufferedinputstream.read(abyte0);
                    if(i <= 0)
                        break;
                    bufferedoutputstream.write(abyte0, 0, i);
                    mTotalBytes = mTotalBytes + (long)i;
                    Message message = mHandler.obtainMessage();
                    message.arg1 = (int)mTotalBytes;
                    mHandler.sendMessage(message);
                } while(true);
                return file;
            }
            catch(IOException ioexception)
            {
                bufferedinputstream = null;
                bufferedoutputstream = null;
                return null;
            } finally {
            	if(bufferedoutputstream != null)
                    try
                    {
                        bufferedoutputstream.close();
                    }
                    catch(IOException ioexception4) { }
                if(bufferedinputstream != null)
                    try
                    {
                        bufferedinputstream.close();
                    }
                    catch(IOException ioexception3) { }
            }
        }

        protected final Object doInBackground(Object aobj[])
        {
            int i = 0;
            while(i < 4) 
            {
                long l = mOriginalSize[i];
                String s = mFromDbName[i];
                String s1 = mToDbName[i];
                if(l == 0L)
                {
                    Log.w("DumpDatabase", (new StringBuilder("Could not find database: ")).append(s).toString());
                } else
                {
                	try {
                		File file = doDump(s, s1);
                		Log.i("DumpDatabase", (new StringBuilder("Dump complete; orig size: ")).append(l).append(", copy size: ").append(file.length()).toString());
                	} catch (IOException e) {
                		// TODO
                	}
                }
                i++;
            }
            mProgressDialog.dismiss();
            return null;
        }

        protected final void onPreExecute()
        {
            int i = EsAccountsData.getActiveAccount(mContext).getIndex();
            long l = 0L;
            mFromDbName[0] = (new StringBuilder("es")).append(i).append(".db").toString();
            mFromDbName[1] = "picasa.db";
            mFromDbName[2] = "iu.picasa.db";
            mFromDbName[3] = "iu.upload.db";
            mToDbName[0] = (new StringBuilder("es")).append(i).append("_dump.bin").toString();
            mToDbName[1] = "picasa_dump.bin";
            mToDbName[2] = "iu.picasa_dump.bin";
            mToDbName[3] = "iu.upload_dump.bin";
            for(int j = 0; j < 4; j++)
            {
                String s = mFromDbName[j];
                File file = mContext.getDatabasePath(s);
                if(file.exists() && file.isFile())
                {
                    mOriginalSize[j] = file.length();
                    l += mOriginalSize[j];
                }
            }

            mProgressDialog.setMax((int)l);
        }

        private String mFromDbName[] = new String[4];
        private long mOriginalSize[] = new long[4];
        private String mToDbName[] = new String[4];
        private long mTotalBytes;
    }
}
