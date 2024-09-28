package org.webrtc.model;

public class CollectStatus {
    private String ActualEncBitrate;
    private String AudioInputLevel;
    private String AudioPacketsSend;
    private String AudioPacketsSendLost;
    private String AudioSendBitrate;
    private String AudioSendCodec;
    private String AvailableBandWidth;
    private String ConnReceiveBitrate;
    private String ConnSendBitrate;
    private String FeedId;
    private String LocalCandidateType;
    private String MediaType;
    private String RemoteCandidateType;
    private String RoundTripTime;
    private String TargetEncBitrate;
    private String TransportType;
    private String VideoEncodedMs;
    private String VideoInputFps;
    private String VideoInputHeight;
    private String VideoInputWidth;
    private String VideoPacketsSendLost;
    private String VideoPacksetsSend;
    private String VideoSendBitrate;
    private String VideoSendCodec;
    private String VideoSendFps;
    private String VideoSendHeight;
    private String VideoSendWidth;

    public String getRemoteCandidateType() {
        return this.RemoteCandidateType;
    }

    public void setRemoteCandidateType(String remoteCandidateType) {
        this.RemoteCandidateType = remoteCandidateType;
    }

    public String getActualEncBitrate() {
        return this.ActualEncBitrate;
    }

    public void setActualEncBitrate(String actualEncBitrate) {
        this.ActualEncBitrate = actualEncBitrate;
    }

    public String getTargetEncBitrate() {
        return this.TargetEncBitrate;
    }

    public void setTargetEncBitrate(String targetEncBitrate) {
        this.TargetEncBitrate = targetEncBitrate;
    }

    public String getLocalCandidateType() {
        return this.LocalCandidateType;
    }

    public void setLocalCandidateType(String localCandidateType) {
        this.LocalCandidateType = localCandidateType;
    }

    public String getAudioPacketsSend() {
        return this.AudioPacketsSend;
    }

    public void setAudioPacketsSend(String audioPacketsSend) {
        this.AudioPacketsSend = audioPacketsSend;
    }

    public String getVideoSendBitrate() {
        return this.VideoSendBitrate;
    }

    public void setVideoSendBitrate(String videoSendBitrate) {
        this.VideoSendBitrate = videoSendBitrate;
    }

    public String getVideoInputHeight() {
        return this.VideoInputHeight;
    }

    public void setVideoInputHeight(String videoInputHeight) {
        this.VideoInputHeight = videoInputHeight;
    }

    public String getMediaType() {
        return this.MediaType;
    }

    public void setMediaType(String mediaType) {
        this.MediaType = mediaType;
    }

    public String getVideoInputWidth() {
        return this.VideoInputWidth;
    }

    public void setVideoInputWidth(String videoInputWidth) {
        this.VideoInputWidth = videoInputWidth;
    }

    public String getConnSendBitrate() {
        return this.ConnSendBitrate;
    }

    public void setConnSendBitrate(String connSendBitrate) {
        this.ConnSendBitrate = connSendBitrate;
    }

    public String getTransportType() {
        return this.TransportType;
    }

    public void setTransportType(String transportType) {
        this.TransportType = transportType;
    }

    public String getVideoSendWidth() {
        return this.VideoSendWidth;
    }

    public void setVideoSendWidth(String videoSendWidth) {
        this.VideoSendWidth = videoSendWidth;
    }

    public String getVideoPacksetsSend() {
        return this.VideoPacksetsSend;
    }

    public void setVideoPacksetsSend(String videoPacksetsSend) {
        this.VideoPacksetsSend = videoPacksetsSend;
    }

    public String getVideoSendCodec() {
        return this.VideoSendCodec;
    }

    public void setVideoSendCodec(String videoSendCodec) {
        this.VideoSendCodec = videoSendCodec;
    }

    public String getAvailableBandWidth() {
        return this.AvailableBandWidth;
    }

    public void setAvailableBandWidth(String availableBandWidth) {
        this.AvailableBandWidth = availableBandWidth;
    }

    public String getAudioInputLevel() {
        return this.AudioInputLevel;
    }

    public void setAudioInputLevel(String audioInputLevel) {
        this.AudioInputLevel = audioInputLevel;
    }

    public String getAudioPacketsSendLost() {
        return this.AudioPacketsSendLost;
    }

    public void setAudioPacketsSendLost(String audioPacketsSendLost) {
        this.AudioPacketsSendLost = audioPacketsSendLost;
    }

    public String getVideoSendHeight() {
        return this.VideoSendHeight;
    }

    public void setVideoSendHeight(String videoSendHeight) {
        this.VideoSendHeight = videoSendHeight;
    }

    public String getVideoPacketsSendLost() {
        return this.VideoPacketsSendLost;
    }

    public void setVideoPacketsSendLost(String videoPacketsSendLost) {
        this.VideoPacketsSendLost = videoPacketsSendLost;
    }

    public String getRoundTripTime() {
        return this.RoundTripTime;
    }

    public void setRoundTripTime(String roundTripTime) {
        this.RoundTripTime = roundTripTime;
    }

    public String getVideoInputFps() {
        return this.VideoInputFps;
    }

    public void setVideoInputFps(String videoInputFps) {
        this.VideoInputFps = videoInputFps;
    }

    public String getVideoSendFps() {
        return this.VideoSendFps;
    }

    public void setVideoSendFps(String videoSendFps) {
        this.VideoSendFps = videoSendFps;
    }

    public String getVideoEncodedMs() {
        return this.VideoEncodedMs;
    }

    public void setVideoEncodedMs(String videoEncodedMs) {
        this.VideoEncodedMs = videoEncodedMs;
    }

    public String getFeedId() {
        return this.FeedId;
    }

    public void setFeedId(String feedId) {
        this.FeedId = feedId;
    }

    public String getAudioSendBitrate() {
        return this.AudioSendBitrate;
    }

    public void setAudioSendBitrate(String audioSendBitrate) {
        this.AudioSendBitrate = audioSendBitrate;
    }

    public String getAudioSendCodec() {
        return this.AudioSendCodec;
    }

    public void setAudioSendCodec(String audioSendCodec) {
        this.AudioSendCodec = audioSendCodec;
    }

    public String getConnReceiveBitrate() {
        return this.ConnReceiveBitrate;
    }

    public void setConnReceiveBitrate(String connReceiveBitrate) {
        this.ConnReceiveBitrate = connReceiveBitrate;
    }

    public String toString() {
        return "CollectStatus{RemoteCandidateType='" + this.RemoteCandidateType + '\'' + ", ActualEncBitrate='" + this.ActualEncBitrate + '\'' + ", TargetEncBitrate='" + this.TargetEncBitrate + '\'' + ", LocalCandidateType='" + this.LocalCandidateType + '\'' + ", AudioPacketsSend='" + this.AudioPacketsSend + '\'' + ", VideoSendBitrate='" + this.VideoSendBitrate + '\'' + ", VideoInputHeight='" + this.VideoInputHeight + '\'' + ", MediaType='" + this.MediaType + '\'' + ", VideoInputWidth='" + this.VideoInputWidth + '\'' + ", ConnSendBitrate='" + this.ConnSendBitrate + '\'' + ", TransportType='" + this.TransportType + '\'' + ", VideoSendWidth='" + this.VideoSendWidth + '\'' + ", VideoPacksetsSend='" + this.VideoPacksetsSend + '\'' + ", VideoSendCodec='" + this.VideoSendCodec + '\'' + ", AvailableBandWidth='" + this.AvailableBandWidth + '\'' + ", AudioInputLevel='" + this.AudioInputLevel + '\'' + ", AudioPacketsSendLost='" + this.AudioPacketsSendLost + '\'' + ", VideoSendHeight='" + this.VideoSendHeight + '\'' + ", VideoPacketsSendLost='" + this.VideoPacketsSendLost + '\'' + ", RoundTripTime='" + this.RoundTripTime + '\'' + ", VideoInputFps='" + this.VideoInputFps + '\'' + ", VideoSendFps='" + this.VideoSendFps + '\'' + ", VideoEncodedMs='" + this.VideoEncodedMs + '\'' + ", FeedId='" + this.FeedId + '\'' + ", AudioSendBitrate='" + this.AudioSendBitrate + '\'' + ", AudioSendCodec='" + this.AudioSendCodec + '\'' + ", ConnReceiveBitrate='" + this.ConnReceiveBitrate + '\'' + '}';
    }
}
