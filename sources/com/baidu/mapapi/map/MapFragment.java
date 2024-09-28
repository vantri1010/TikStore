package com.baidu.mapapi.map;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {
    private static final String a = MapFragment.class.getSimpleName();
    private MapView b;
    private BaiduMapOptions c;

    public MapFragment() {
    }

    private MapFragment(BaiduMapOptions baiduMapOptions) {
        this.c = baiduMapOptions;
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public static MapFragment newInstance(BaiduMapOptions baiduMapOptions) {
        return new MapFragment(baiduMapOptions);
    }

    public BaiduMap getBaiduMap() {
        MapView mapView = this.b;
        if (mapView == null) {
            return null;
        }
        return mapView.getMap();
    }

    public MapView getMapView() {
        return this.b;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        MapView mapView = new MapView((Context) getActivity(), this.c);
        this.b = mapView;
        return mapView;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.b.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
    }

    public void onPause() {
        super.onPause();
        this.b.onPause();
    }

    public void onResume() {
        super.onResume();
        this.b.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }
}
