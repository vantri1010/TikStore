package com.danikula.videocache;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyCacheUtils {
    static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final Logger LOG = LoggerFactory.getLogger("ProxyCacheUtils");
    static final int MAX_ARRAY_PREVIEW = 16;

    static String getSupposablyMime(String url) {
        MimeTypeMap mimes = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (TextUtils.isEmpty(extension)) {
            return null;
        }
        return mimes.getMimeTypeFromExtension(extension);
    }

    static void assertBuffer(byte[] buffer, long offset, int length) {
        Preconditions.checkNotNull(buffer, "Buffer must be not null!");
        boolean z = true;
        Preconditions.checkArgument(offset >= 0, "Data offset must be positive!");
        if (length < 0 || length > buffer.length) {
            z = false;
        }
        Preconditions.checkArgument(z, "Length must be in range [0..buffer.length]");
    }

    static String preview(byte[] data, int length) {
        int previewLength = Math.min(16, Math.max(length, 0));
        String preview = Arrays.toString(Arrays.copyOfRange(data, 0, previewLength));
        if (previewLength >= length) {
            return preview;
        }
        return preview.substring(0, preview.length() - 1) + ", ...]";
    }

    static String encode(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding url", e);
        }
    }

    static String decode(String url) {
        try {
            return URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error decoding url", e);
        }
    }

    static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOG.error("Error closing resource", (Throwable) e);
            }
        }
    }

    public static String computeMD5(String string) {
        try {
            return bytesToHexString(MessageDigest.getInstance("MD5").digest(string.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(bytes[i])}));
        }
        return sb.toString();
    }
}
