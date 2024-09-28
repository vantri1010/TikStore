package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import java.nio.ByteBuffer;
import kotlin.jvm.internal.ByteCompanionObject;

public class TemporalLevelEntry extends GroupEntry {
    public static final String TYPE = "tele";
    private boolean levelIndependentlyDecodable;
    private short reserved;

    public String getType() {
        return TYPE;
    }

    public boolean isLevelIndependentlyDecodable() {
        return this.levelIndependentlyDecodable;
    }

    public void setLevelIndependentlyDecodable(boolean levelIndependentlyDecodable2) {
        this.levelIndependentlyDecodable = levelIndependentlyDecodable2;
    }

    public void parse(ByteBuffer byteBuffer) {
        this.levelIndependentlyDecodable = (byteBuffer.get() & ByteCompanionObject.MIN_VALUE) == 128;
    }

    public ByteBuffer get() {
        ByteBuffer content = ByteBuffer.allocate(1);
        content.put((byte) (this.levelIndependentlyDecodable ? 128 : 0));
        content.rewind();
        return content;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemporalLevelEntry that = (TemporalLevelEntry) o;
        if (this.levelIndependentlyDecodable == that.levelIndependentlyDecodable && this.reserved == that.reserved) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((int) this.levelIndependentlyDecodable) * true) + this.reserved;
    }

    public String toString() {
        return "TemporalLevelEntry" + "{levelIndependentlyDecodable=" + this.levelIndependentlyDecodable + '}';
    }
}
