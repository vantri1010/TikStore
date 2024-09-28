package com.googlecode.mp4parser.authoring.builder;

import com.googlecode.mp4parser.authoring.Track;
import java.util.Map;

public class StaticFragmentIntersectionFinderImpl implements FragmentIntersectionFinder {
    Map<Track, long[]> sampleNumbers;

    public StaticFragmentIntersectionFinderImpl(Map<Track, long[]> sampleNumbers2) {
        this.sampleNumbers = sampleNumbers2;
    }

    public long[] sampleNumbers(Track track) {
        return this.sampleNumbers.get(track);
    }
}
