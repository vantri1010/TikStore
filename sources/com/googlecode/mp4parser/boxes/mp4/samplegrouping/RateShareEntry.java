package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class RateShareEntry extends GroupEntry {
    public static final String TYPE = "rash";
    private short discardPriority;
    private List<Entry> entries = new LinkedList();
    private int maximumBitrate;
    private int minimumBitrate;
    private short operationPointCut;
    private short targetRateShare;

    public String getType() {
        return TYPE;
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void parse(java.nio.ByteBuffer r6) {
        /*
            r5 = this;
            short r0 = r6.getShort()
            r5.operationPointCut = r0
            r1 = 1
            if (r0 != r1) goto L_0x0010
            short r0 = r6.getShort()
            r5.targetRateShare = r0
            goto L_0x0017
        L_0x0010:
            short r0 = r5.operationPointCut
        L_0x0013:
            int r1 = r0 + -1
            if (r0 > 0) goto L_0x0033
        L_0x0017:
            long r0 = com.coremedia.iso.IsoTypeReader.readUInt32(r6)
            int r0 = com.googlecode.mp4parser.util.CastUtils.l2i(r0)
            r5.maximumBitrate = r0
            long r0 = com.coremedia.iso.IsoTypeReader.readUInt32(r6)
            int r0 = com.googlecode.mp4parser.util.CastUtils.l2i(r0)
            r5.minimumBitrate = r0
            int r0 = com.coremedia.iso.IsoTypeReader.readUInt8(r6)
            short r0 = (short) r0
            r5.discardPriority = r0
            return
        L_0x0033:
            java.util.List<com.googlecode.mp4parser.boxes.mp4.samplegrouping.RateShareEntry$Entry> r0 = r5.entries
            com.googlecode.mp4parser.boxes.mp4.samplegrouping.RateShareEntry$Entry r2 = new com.googlecode.mp4parser.boxes.mp4.samplegrouping.RateShareEntry$Entry
            long r3 = com.coremedia.iso.IsoTypeReader.readUInt32(r6)
            int r3 = com.googlecode.mp4parser.util.CastUtils.l2i(r3)
            short r4 = r6.getShort()
            r2.<init>(r3, r4)
            r0.add(r2)
            r0 = r1
            goto L_0x0013
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.boxes.mp4.samplegrouping.RateShareEntry.parse(java.nio.ByteBuffer):void");
    }

    public ByteBuffer get() {
        short s = this.operationPointCut;
        ByteBuffer buf = ByteBuffer.allocate(s == 1 ? 13 : (s * 6) + 11);
        buf.putShort(this.operationPointCut);
        if (this.operationPointCut == 1) {
            buf.putShort(this.targetRateShare);
        } else {
            for (Entry entry : this.entries) {
                buf.putInt(entry.getAvailableBitrate());
                buf.putShort(entry.getTargetRateShare());
            }
        }
        buf.putInt(this.maximumBitrate);
        buf.putInt(this.minimumBitrate);
        IsoTypeWriter.writeUInt8(buf, this.discardPriority);
        buf.rewind();
        return buf;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RateShareEntry that = (RateShareEntry) o;
        if (this.discardPriority != that.discardPriority || this.maximumBitrate != that.maximumBitrate || this.minimumBitrate != that.minimumBitrate || this.operationPointCut != that.operationPointCut || this.targetRateShare != that.targetRateShare) {
            return false;
        }
        List<Entry> list = this.entries;
        if (list == null ? that.entries == null : list.equals(that.entries)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = ((this.operationPointCut * 31) + this.targetRateShare) * 31;
        List<Entry> list = this.entries;
        return ((((((result + (list != null ? list.hashCode() : 0)) * 31) + this.maximumBitrate) * 31) + this.minimumBitrate) * 31) + this.discardPriority;
    }

    public short getOperationPointCut() {
        return this.operationPointCut;
    }

    public void setOperationPointCut(short operationPointCut2) {
        this.operationPointCut = operationPointCut2;
    }

    public short getTargetRateShare() {
        return this.targetRateShare;
    }

    public void setTargetRateShare(short targetRateShare2) {
        this.targetRateShare = targetRateShare2;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public void setEntries(List<Entry> entries2) {
        this.entries = entries2;
    }

    public int getMaximumBitrate() {
        return this.maximumBitrate;
    }

    public void setMaximumBitrate(int maximumBitrate2) {
        this.maximumBitrate = maximumBitrate2;
    }

    public int getMinimumBitrate() {
        return this.minimumBitrate;
    }

    public void setMinimumBitrate(int minimumBitrate2) {
        this.minimumBitrate = minimumBitrate2;
    }

    public short getDiscardPriority() {
        return this.discardPriority;
    }

    public void setDiscardPriority(short discardPriority2) {
        this.discardPriority = discardPriority2;
    }

    public static class Entry {
        int availableBitrate;
        short targetRateShare;

        public Entry(int availableBitrate2, short targetRateShare2) {
            this.availableBitrate = availableBitrate2;
            this.targetRateShare = targetRateShare2;
        }

        public String toString() {
            return "{availableBitrate=" + this.availableBitrate + ", targetRateShare=" + this.targetRateShare + '}';
        }

        public int getAvailableBitrate() {
            return this.availableBitrate;
        }

        public void setAvailableBitrate(int availableBitrate2) {
            this.availableBitrate = availableBitrate2;
        }

        public short getTargetRateShare() {
            return this.targetRateShare;
        }

        public void setTargetRateShare(short targetRateShare2) {
            this.targetRateShare = targetRateShare2;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Entry entry = (Entry) o;
            if (this.availableBitrate == entry.availableBitrate && this.targetRateShare == entry.targetRateShare) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (this.availableBitrate * 31) + this.targetRateShare;
        }
    }
}
