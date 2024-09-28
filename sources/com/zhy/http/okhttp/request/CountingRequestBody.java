package com.zhy.http.okhttp.request;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class CountingRequestBody extends RequestBody {
    protected CountingSink countingSink;
    protected RequestBody delegate;
    protected Listener listener;

    public interface Listener {
        void onRequestProgress(long j, long j2);
    }

    public CountingRequestBody(RequestBody delegate2, Listener listener2) {
        this.delegate = delegate2;
        this.listener = listener2;
    }

    public MediaType contentType() {
        return this.delegate.contentType();
    }

    public long contentLength() {
        try {
            return this.delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void writeTo(BufferedSink sink) throws IOException {
        CountingSink countingSink2 = new CountingSink(sink);
        this.countingSink = countingSink2;
        BufferedSink bufferedSink = Okio.buffer((Sink) countingSink2);
        this.delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            this.bytesWritten += byteCount;
            CountingRequestBody.this.listener.onRequestProgress(this.bytesWritten, CountingRequestBody.this.contentLength());
        }
    }
}
