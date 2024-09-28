package org.webrtc.alirtcInterface;

public class PublisherInfo {
    public String audio_track_label;
    public String call_id;
    public String display;
    public String session;
    public String stream_label;
    public String user_id;
    public String[] video_track_labels;

    public String getCall_id() {
        return this.call_id;
    }

    public void setCall_id(String call_id2) {
        this.call_id = call_id2;
    }

    public String getDisplay() {
        return this.display;
    }

    public void setDisplay(String display2) {
        this.display = display2;
    }

    public String getStream_label() {
        return this.stream_label;
    }

    public void setStream_label(String stream_label2) {
        this.stream_label = stream_label2;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id2) {
        this.user_id = user_id2;
    }

    public String getSession() {
        return this.session;
    }

    public void setSession(String session2) {
        this.session = session2;
    }

    public String getAudio_track_label() {
        return this.audio_track_label;
    }

    public void setAudio_track_label(String audio_track_label2) {
        this.audio_track_label = audio_track_label2;
    }

    public String[] getVideo_track_labels() {
        return this.video_track_labels;
    }

    public void setVideo_track_labels(String[] video_track_labels2) {
        this.video_track_labels = video_track_labels2;
    }

    public String toString() {
        return super.toString();
    }
}
