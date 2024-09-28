package com.google.android.exoplayer2.metadata;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.MimeTypes;

public interface MetadataDecoderFactory {
    public static final MetadataDecoderFactory DEFAULT = new MetadataDecoderFactory() {
        public boolean supportsFormat(Format format) {
            String mimeType = format.sampleMimeType;
            return MimeTypes.APPLICATION_ID3.equals(mimeType) || MimeTypes.APPLICATION_EMSG.equals(mimeType) || MimeTypes.APPLICATION_SCTE35.equals(mimeType) || MimeTypes.APPLICATION_ICY.equals(mimeType);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.google.android.exoplayer2.metadata.MetadataDecoder createDecoder(com.google.android.exoplayer2.Format r6) {
            /*
                r5 = this;
                java.lang.String r0 = r6.sampleMimeType
                int r1 = r0.hashCode()
                r2 = 3
                r3 = 2
                r4 = 1
                switch(r1) {
                    case -1348231605: goto L_0x002b;
                    case -1248341703: goto L_0x0021;
                    case 1154383568: goto L_0x0017;
                    case 1652648887: goto L_0x000d;
                    default: goto L_0x000c;
                }
            L_0x000c:
                goto L_0x0035
            L_0x000d:
                java.lang.String r1 = "application/x-scte35"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x000c
                r0 = 2
                goto L_0x0036
            L_0x0017:
                java.lang.String r1 = "application/x-emsg"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x000c
                r0 = 1
                goto L_0x0036
            L_0x0021:
                java.lang.String r1 = "application/id3"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x000c
                r0 = 0
                goto L_0x0036
            L_0x002b:
                java.lang.String r1 = "application/x-icy"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x000c
                r0 = 3
                goto L_0x0036
            L_0x0035:
                r0 = -1
            L_0x0036:
                if (r0 == 0) goto L_0x0058
                if (r0 == r4) goto L_0x0052
                if (r0 == r3) goto L_0x004c
                if (r0 != r2) goto L_0x0044
                com.google.android.exoplayer2.metadata.icy.IcyDecoder r0 = new com.google.android.exoplayer2.metadata.icy.IcyDecoder
                r0.<init>()
                return r0
            L_0x0044:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.String r1 = "Attempted to create decoder for unsupported format"
                r0.<init>(r1)
                throw r0
            L_0x004c:
                com.google.android.exoplayer2.metadata.scte35.SpliceInfoDecoder r0 = new com.google.android.exoplayer2.metadata.scte35.SpliceInfoDecoder
                r0.<init>()
                return r0
            L_0x0052:
                com.google.android.exoplayer2.metadata.emsg.EventMessageDecoder r0 = new com.google.android.exoplayer2.metadata.emsg.EventMessageDecoder
                r0.<init>()
                return r0
            L_0x0058:
                com.google.android.exoplayer2.metadata.id3.Id3Decoder r0 = new com.google.android.exoplayer2.metadata.id3.Id3Decoder
                r0.<init>()
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.metadata.MetadataDecoderFactory.AnonymousClass1.createDecoder(com.google.android.exoplayer2.Format):com.google.android.exoplayer2.metadata.MetadataDecoder");
        }
    };

    MetadataDecoder createDecoder(Format format);

    boolean supportsFormat(Format format);
}
