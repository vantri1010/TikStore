package com.google.android.exoplayer2.extractor.mkv;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import kotlin.UByte;

final class DefaultEbmlReader implements EbmlReader {
    private static final int ELEMENT_STATE_READ_CONTENT = 2;
    private static final int ELEMENT_STATE_READ_CONTENT_SIZE = 1;
    private static final int ELEMENT_STATE_READ_ID = 0;
    private static final int MAX_ID_BYTES = 4;
    private static final int MAX_INTEGER_ELEMENT_SIZE_BYTES = 8;
    private static final int MAX_LENGTH_BYTES = 8;
    private static final int VALID_FLOAT32_ELEMENT_SIZE_BYTES = 4;
    private static final int VALID_FLOAT64_ELEMENT_SIZE_BYTES = 8;
    private long elementContentSize;
    private int elementId;
    private int elementState;
    private final ArrayDeque<MasterElement> masterElementsStack = new ArrayDeque<>();
    private EbmlReaderOutput output;
    private final byte[] scratch = new byte[8];
    private final VarintReader varintReader = new VarintReader();

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    private @interface ElementState {
    }

    public void init(EbmlReaderOutput eventHandler) {
        this.output = eventHandler;
    }

    public void reset() {
        this.elementState = 0;
        this.masterElementsStack.clear();
        this.varintReader.reset();
    }

    public boolean read(ExtractorInput input) throws IOException, InterruptedException {
        Assertions.checkState(this.output != null);
        while (true) {
            if (this.masterElementsStack.isEmpty() || input.getPosition() < this.masterElementsStack.peek().elementEndPosition) {
                if (this.elementState == 0) {
                    long result = this.varintReader.readUnsignedVarint(input, true, false, 4);
                    if (result == -2) {
                        result = maybeResyncToNextLevel1Element(input);
                    }
                    if (result == -1) {
                        return false;
                    }
                    this.elementId = (int) result;
                    this.elementState = 1;
                }
                if (this.elementState == 1) {
                    this.elementContentSize = this.varintReader.readUnsignedVarint(input, false, true, 8);
                    this.elementState = 2;
                }
                int type = this.output.getElementType(this.elementId);
                if (type == 0) {
                    input.skipFully((int) this.elementContentSize);
                    this.elementState = 0;
                } else if (type == 1) {
                    long elementContentPosition = input.getPosition();
                    this.masterElementsStack.push(new MasterElement(this.elementId, elementContentPosition + this.elementContentSize));
                    this.output.startMasterElement(this.elementId, elementContentPosition, this.elementContentSize);
                    this.elementState = 0;
                    return true;
                } else if (type == 2) {
                    long j = this.elementContentSize;
                    if (j <= 8) {
                        this.output.integerElement(this.elementId, readInteger(input, (int) j));
                        this.elementState = 0;
                        return true;
                    }
                    throw new ParserException("Invalid integer size: " + this.elementContentSize);
                } else if (type == 3) {
                    long j2 = this.elementContentSize;
                    if (j2 <= 2147483647L) {
                        this.output.stringElement(this.elementId, readString(input, (int) j2));
                        this.elementState = 0;
                        return true;
                    }
                    throw new ParserException("String element size: " + this.elementContentSize);
                } else if (type == 4) {
                    this.output.binaryElement(this.elementId, (int) this.elementContentSize, input);
                    this.elementState = 0;
                    return true;
                } else if (type == 5) {
                    long j3 = this.elementContentSize;
                    if (j3 == 4 || j3 == 8) {
                        this.output.floatElement(this.elementId, readFloat(input, (int) this.elementContentSize));
                        this.elementState = 0;
                        return true;
                    }
                    throw new ParserException("Invalid float size: " + this.elementContentSize);
                } else {
                    throw new ParserException("Invalid element type " + type);
                }
            } else {
                this.output.endMasterElement(this.masterElementsStack.pop().elementId);
                return true;
            }
        }
    }

    private long maybeResyncToNextLevel1Element(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        while (true) {
            input.peekFully(this.scratch, 0, 4);
            int varintLength = VarintReader.parseUnsignedVarintLength(this.scratch[0]);
            if (varintLength != -1 && varintLength <= 4) {
                int potentialId = (int) VarintReader.assembleVarint(this.scratch, varintLength, false);
                if (this.output.isLevel1Element(potentialId)) {
                    input.skipFully(varintLength);
                    return (long) potentialId;
                }
            }
            input.skipFully(1);
        }
    }

    private long readInteger(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        input.readFully(this.scratch, 0, byteLength);
        long value = 0;
        for (int i = 0; i < byteLength; i++) {
            value = (value << 8) | ((long) (this.scratch[i] & UByte.MAX_VALUE));
        }
        return value;
    }

    private double readFloat(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        long integerValue = readInteger(input, byteLength);
        if (byteLength == 4) {
            return (double) Float.intBitsToFloat((int) integerValue);
        }
        return Double.longBitsToDouble(integerValue);
    }

    private String readString(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        if (byteLength == 0) {
            return "";
        }
        byte[] stringBytes = new byte[byteLength];
        input.readFully(stringBytes, 0, byteLength);
        int trimmedLength = byteLength;
        while (trimmedLength > 0 && stringBytes[trimmedLength - 1] == 0) {
            trimmedLength--;
        }
        return new String(stringBytes, 0, trimmedLength);
    }

    private static final class MasterElement {
        /* access modifiers changed from: private */
        public final long elementEndPosition;
        /* access modifiers changed from: private */
        public final int elementId;

        private MasterElement(int elementId2, long elementEndPosition2) {
            this.elementId = elementId2;
            this.elementEndPosition = elementEndPosition2;
        }
    }
}
