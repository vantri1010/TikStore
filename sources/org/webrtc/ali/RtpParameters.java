package org.webrtc.ali;

import java.util.LinkedList;
import org.webrtc.ali.MediaStreamTrack;

public class RtpParameters {
    public final LinkedList<Codec> codecs = new LinkedList<>();
    public final LinkedList<Encoding> encodings = new LinkedList<>();

    public static class Codec {
        public Integer clockRate;
        MediaStreamTrack.MediaType kind;
        public String name;
        public Integer numChannels;
        public int payloadType;
    }

    public static class Encoding {
        public boolean active = true;
        public Integer maxBitrateBps;
        public Long ssrc;
    }
}
