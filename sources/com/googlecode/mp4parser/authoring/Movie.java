package com.googlecode.mp4parser.authoring;

import com.googlecode.mp4parser.util.Matrix;
import java.util.LinkedList;
import java.util.List;

public class Movie {
    Matrix matrix = Matrix.ROTATE_0;
    List<Track> tracks = new LinkedList();

    public Movie() {
    }

    public Movie(List<Track> tracks2) {
        this.tracks = tracks2;
    }

    public List<Track> getTracks() {
        return this.tracks;
    }

    public void setTracks(List<Track> tracks2) {
        this.tracks = tracks2;
    }

    public void addTrack(Track nuTrack) {
        if (getTrackByTrackId(nuTrack.getTrackMetaData().getTrackId()) != null) {
            nuTrack.getTrackMetaData().setTrackId(getNextTrackId());
        }
        this.tracks.add(nuTrack);
    }

    public String toString() {
        String s = "Movie{ ";
        for (Track track : this.tracks) {
            s = String.valueOf(s) + "track_" + track.getTrackMetaData().getTrackId() + " (" + track.getHandler() + ") ";
        }
        return String.valueOf(s) + '}';
    }

    public long getNextTrackId() {
        long nextTrackId = 0;
        for (Track track : this.tracks) {
            nextTrackId = nextTrackId < track.getTrackMetaData().getTrackId() ? track.getTrackMetaData().getTrackId() : nextTrackId;
        }
        long j = 1 + nextTrackId;
        long nextTrackId2 = j;
        return j;
    }

    public Track getTrackByTrackId(long trackId) {
        for (Track track : this.tracks) {
            if (track.getTrackMetaData().getTrackId() == trackId) {
                return track;
            }
        }
        return null;
    }

    public long getTimescale() {
        long timescale = getTracks().iterator().next().getTrackMetaData().getTimescale();
        for (Track track : getTracks()) {
            timescale = gcd(track.getTrackMetaData().getTimescale(), timescale);
        }
        return timescale;
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    public void setMatrix(Matrix matrix2) {
        this.matrix = matrix2;
    }

    public static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
}
