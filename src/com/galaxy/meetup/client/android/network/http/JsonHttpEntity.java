/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network.http;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.util.EncodingUtils;

import com.galaxy.meetup.server.client.domain.GenericJson;

/**
 * 
 * @author sihai
 *
 */
public class JsonHttpEntity extends AbstractHttpEntity {

	private static final byte BOUNDARY_HYPHEN_BYTES[] = EncodingUtils.getAsciiBytes("--");
    private static final byte CONTENT_TYPE_BYTES[] = EncodingUtils.getAsciiBytes("Content-Type: ");
    private static final byte CRLF_BYTES[] = EncodingUtils.getAsciiBytes("\r\n");
    private static final byte DEFAULT_BOUNDARY_BYTES[] = "onetwothreefourfivesixseven".getBytes();
    private final byte mPayloadBytes[];
    private final GenericJson mRequest;
    
    public JsonHttpEntity(GenericJson genericjson)
    {
        this(genericjson, null);
    }

    public JsonHttpEntity(GenericJson genericjson, byte[] payloadBytes)
    {
        mRequest = genericjson;
        mPayloadBytes = payloadBytes;
        if(mPayloadBytes == null)
        {
            setContentType("application/octet-stream");
            setContentEncoding("gzip");
        } else
        {
            setContentType("multipart/related");
        }
    }

    private static void writeBoundary(OutputStream outputstream, boolean flag)
        throws IOException
    {
        outputstream.write(BOUNDARY_HYPHEN_BYTES);
        outputstream.write(DEFAULT_BOUNDARY_BYTES);
        if(flag)
            outputstream.write(BOUNDARY_HYPHEN_BYTES);
        outputstream.write(CRLF_BYTES);
    }

    private static void writeMetaData(OutputStream outputstream, byte[] metadataBytes) throws IOException
    {
        outputstream.write(CONTENT_TYPE_BYTES);
        outputstream.write(EncodingUtils.getAsciiBytes("application/json; charset=UTF-8"));
        outputstream.write(CRLF_BYTES);
        outputstream.write(CRLF_BYTES);
        outputstream.write(metadataBytes);
        outputstream.write(CRLF_BYTES);
    }

    private static void writePayload(OutputStream outputstream, byte[] payloadBytes) throws IOException
    {
        outputstream.write(CONTENT_TYPE_BYTES);
        outputstream.write(EncodingUtils.getAsciiBytes("image/jpeg"));
        outputstream.write(CRLF_BYTES);
        outputstream.write("Content-Transfer-Encoding: binary".getBytes());
        outputstream.write(CRLF_BYTES);
        outputstream.write(CRLF_BYTES);
        outputstream.write(payloadBytes);
        outputstream.write(CRLF_BYTES);
    }

    public final InputStream getContent() throws IOException
    {
        ByteArrayInputStream bytearrayinputstream;
        if(mRequest == null)
            bytearrayinputstream = new ByteArrayInputStream(new byte[0]);
        else
        if(mPayloadBytes == null)
        {
            bytearrayinputstream = new ByteArrayInputStream(mRequest.toByteArray());
        } else
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            writeBoundary(bytearrayoutputstream, false);
            writeMetaData(bytearrayoutputstream, mRequest.toByteArray());
            writeBoundary(bytearrayoutputstream, false);
            writePayload(bytearrayoutputstream, mPayloadBytes);
            writeBoundary(bytearrayoutputstream, true);
            bytearrayinputstream = new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
        }
        return bytearrayinputstream;
    }

    public final long getContentLength()
    {
        return -1L;
    }

    public final boolean isRepeatable()
    {
        return true;
    }

    public final boolean isStreaming()
    {
        return false;
    }

    public final void writeTo(OutputStream outputstream) throws IOException
    {
        if(mRequest != null && mPayloadBytes != null)
        {
            BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(outputstream);
            writeBoundary(bufferedoutputstream, false);
            writeMetaData(bufferedoutputstream, mRequest.toByteArray());
            writeBoundary(bufferedoutputstream, false);
            writePayload(bufferedoutputstream, mPayloadBytes);
            writeBoundary(bufferedoutputstream, true);
            bufferedoutputstream.flush();
            bufferedoutputstream.close();
        } else
        if(mRequest != null)
        {
            GZIPOutputStream gzipoutputstream = new GZIPOutputStream(new BufferedOutputStream(outputstream));
            gzipoutputstream.write(mRequest.toByteArray());
            gzipoutputstream.close();
        } else
        {
            throw new IllegalArgumentException("A mRequest was not found!");
        }
    }
}
