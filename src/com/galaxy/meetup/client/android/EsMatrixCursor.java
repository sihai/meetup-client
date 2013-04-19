/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.database.AbstractCursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;

/**
 * 
 * @author sihai
 *
 */
public class EsMatrixCursor extends AbstractCursor {

	private final int columnCount;
    private final String columnNames[];
    private Object data[];
    private final Bundle mExtras;
    private int rowCount;
    
    public EsMatrixCursor(String as[])
    {
        this(as, 16);
    }

    public EsMatrixCursor(String as[], int i)
    {
        rowCount = 0;
        mExtras = new Bundle();
        columnNames = as;
        columnCount = as.length;
        if(i <= 0)
            i = 1;
        data = new Object[i * columnCount];
    }

    private void ensureCapacity(int i)
    {
        if(i > data.length)
        {
            Object aobj[] = data;
            int j = 2 * data.length;
            if(j < i)
                j = i;
            data = new Object[j];
            System.arraycopy(((Object) (aobj)), 0, ((Object) (data)), 0, aobj.length);
        }
    }

    private Object get(int i)
    {
        if(i < 0 || i >= columnCount)
            throw new CursorIndexOutOfBoundsException((new StringBuilder("Requested column: ")).append(i).append(", # of columns: ").append(columnCount).toString());
        if(mPos < 0)
            throw new CursorIndexOutOfBoundsException("Before first row.");
        if(mPos >= rowCount)
            throw new CursorIndexOutOfBoundsException("After last row.");
        else
            return data[i + mPos * columnCount];
    }

    public final void addRow(Object aobj[])
    {
        if(aobj.length != columnCount)
        {
            throw new IllegalArgumentException((new StringBuilder("columnNames.length = ")).append(columnCount).append(", columnValues.length = ").append(aobj.length).toString());
        } else
        {
            int i = rowCount;
            rowCount = i + 1;
            int j = i * columnCount;
            ensureCapacity(j + columnCount);
            System.arraycopy(((Object) (aobj)), 0, ((Object) (data)), j, columnCount);
            return;
        }
    }

    public final byte[] getBlob(int i)
    {
        return (byte[])get(i);
    }

    public final String[] getColumnNames()
    {
        return columnNames;
    }

    public final int getCount()
    {
        return rowCount;
    }

    public final double getDouble(int i)
    {
        Object obj = get(i);
        double d;
        if(obj == null)
            d = 0.0D;
        else
        if(obj instanceof Number)
            d = ((Number)obj).doubleValue();
        else
            d = Double.parseDouble(obj.toString());
        return d;
    }

    public final Bundle getExtras()
    {
        return mExtras;
    }

    public final float getFloat(int i)
    {
        Object obj = get(i);
        float f;
        if(obj == null)
            f = 0.0F;
        else
        if(obj instanceof Number)
            f = ((Number)obj).floatValue();
        else
            f = Float.parseFloat(obj.toString());
        return f;
    }

    public final int getInt(int i)
    {
        Object obj = get(i);
        int j;
        if(obj == null)
            j = 0;
        else
        if(obj instanceof Number)
            j = ((Number)obj).intValue();
        else
            j = Integer.parseInt(obj.toString());
        return j;
    }

    public final long getLong(int i)
    {
        Object obj = get(i);
        long l;
        if(obj == null)
            l = 0L;
        else
        if(obj instanceof Number)
            l = ((Number)obj).longValue();
        else
            l = Long.parseLong(obj.toString());
        return l;
    }

    public final short getShort(int i)
    {
        Object obj = get(i);
        short word0;
        if(obj == null)
            word0 = 0;
        else
        if(obj instanceof Number)
            word0 = ((Number)obj).shortValue();
        else
            word0 = Short.parseShort(obj.toString());
        return word0;
    }

    public final String getString(int i)
    {
        Object obj = get(i);
        String s;
        if(obj == null)
            s = null;
        else
            s = obj.toString();
        return s;
    }

    public final int getType(int i)
    {
        Object obj = get(i);
        int j;
        if(obj == null)
            j = 0;
        else
        if(obj instanceof byte[])
            j = 4;
        else
        if((obj instanceof Float) || (obj instanceof Double))
            j = 2;
        else
        if((obj instanceof Long) || (obj instanceof Integer))
            j = 1;
        else
            j = 3;
        return j;
    }

    public final boolean isNull(int i)
    {
        boolean flag;
        if(get(i) == null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final RowBuilder newRow()
    {
        rowCount = 1 + rowCount;
        int i = rowCount * columnCount;
        ensureCapacity(i);
        return new RowBuilder(i - columnCount, i);
    }
    
    public final class RowBuilder
    {
    	
    	private final int endIndex;
        private int index;

        RowBuilder(int i, int j)
        {
            index = i;
            endIndex = j;
        }
        
        public final RowBuilder add(Object obj)
        {
            if(index == endIndex)
            {
                throw new CursorIndexOutOfBoundsException("No more columns left.");
            } else
            {
                Object aobj[] = data;
                int i = index;
                index = i + 1;
                aobj[i] = obj;
                return this;
            }
        }

    }

}
