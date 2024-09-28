package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.List;

public class TrackBox extends AbstractContainerBox {
    public static final String TYPE = "trak";
    private SampleTableBox sampleTableBox;

    public TrackBox() {
        super(TYPE);
    }

    public TrackHeaderBox getTrackHeaderBox() {
        for (Box box : getBoxes()) {
            if (box instanceof TrackHeaderBox) {
                return (TrackHeaderBox) box;
            }
        }
        return null;
    }

    public SampleTableBox getSampleTableBox() {
        MediaInformationBox minf;
        SampleTableBox sampleTableBox2 = this.sampleTableBox;
        if (sampleTableBox2 != null) {
            return sampleTableBox2;
        }
        MediaBox mdia = getMediaBox();
        if (mdia == null || (minf = mdia.getMediaInformationBox()) == null) {
            return null;
        }
        SampleTableBox sampleTableBox3 = minf.getSampleTableBox();
        this.sampleTableBox = sampleTableBox3;
        return sampleTableBox3;
    }

    public MediaBox getMediaBox() {
        for (Box box : getBoxes()) {
            if (box instanceof MediaBox) {
                return (MediaBox) box;
            }
        }
        return null;
    }

    public void setBoxes(List<Box> boxes) {
        super.setBoxes(boxes);
        this.sampleTableBox = null;
    }
}
