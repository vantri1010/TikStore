package com.google.android.exoplayer2.extractor.flv;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

final class ScriptTagPayloadReader extends TagPayloadReader {
    private static final int AMF_TYPE_BOOLEAN = 1;
    private static final int AMF_TYPE_DATE = 11;
    private static final int AMF_TYPE_ECMA_ARRAY = 8;
    private static final int AMF_TYPE_END_MARKER = 9;
    private static final int AMF_TYPE_NUMBER = 0;
    private static final int AMF_TYPE_OBJECT = 3;
    private static final int AMF_TYPE_STRICT_ARRAY = 10;
    private static final int AMF_TYPE_STRING = 2;
    private static final String KEY_DURATION = "duration";
    private static final String NAME_METADATA = "onMetaData";
    private long durationUs = C.TIME_UNSET;

    public ScriptTagPayloadReader() {
        super((TrackOutput) null);
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public void seek() {
    }

    /* access modifiers changed from: protected */
    public boolean parseHeader(ParsableByteArray data) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void parsePayload(ParsableByteArray data, long timeUs) throws ParserException {
        if (readAmfType(data) != 2) {
            throw new ParserException();
        } else if (NAME_METADATA.equals(readAmfString(data)) && readAmfType(data) == 8) {
            Map<String, Object> metadata = readAmfEcmaArray(data);
            if (metadata.containsKey(KEY_DURATION)) {
                double durationSeconds = ((Double) metadata.get(KEY_DURATION)).doubleValue();
                if (durationSeconds > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                    this.durationUs = (long) (1000000.0d * durationSeconds);
                }
            }
        }
    }

    private static int readAmfType(ParsableByteArray data) {
        return data.readUnsignedByte();
    }

    private static Boolean readAmfBoolean(ParsableByteArray data) {
        boolean z = true;
        if (data.readUnsignedByte() != 1) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    private static Double readAmfDouble(ParsableByteArray data) {
        return Double.valueOf(Double.longBitsToDouble(data.readLong()));
    }

    private static String readAmfString(ParsableByteArray data) {
        int size = data.readUnsignedShort();
        int position = data.getPosition();
        data.skipBytes(size);
        return new String(data.data, position, size);
    }

    private static ArrayList<Object> readAmfStrictArray(ParsableByteArray data) {
        int count = data.readUnsignedIntToInt();
        ArrayList<Object> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(readAmfData(data, readAmfType(data)));
        }
        return list;
    }

    private static HashMap<String, Object> readAmfObject(ParsableByteArray data) {
        HashMap<String, Object> array = new HashMap<>();
        while (true) {
            String key = readAmfString(data);
            int type = readAmfType(data);
            if (type == 9) {
                return array;
            }
            array.put(key, readAmfData(data, type));
        }
    }

    private static HashMap<String, Object> readAmfEcmaArray(ParsableByteArray data) {
        int count = data.readUnsignedIntToInt();
        HashMap<String, Object> array = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            array.put(readAmfString(data), readAmfData(data, readAmfType(data)));
        }
        return array;
    }

    private static Date readAmfDate(ParsableByteArray data) {
        Date date = new Date((long) readAmfDouble(data).doubleValue());
        data.skipBytes(2);
        return date;
    }

    private static Object readAmfData(ParsableByteArray data, int type) {
        if (type == 0) {
            return readAmfDouble(data);
        }
        if (type == 1) {
            return readAmfBoolean(data);
        }
        if (type == 2) {
            return readAmfString(data);
        }
        if (type == 3) {
            return readAmfObject(data);
        }
        if (type == 8) {
            return readAmfEcmaArray(data);
        }
        if (type == 10) {
            return readAmfStrictArray(data);
        }
        if (type != 11) {
            return null;
        }
        return readAmfDate(data);
    }
}
