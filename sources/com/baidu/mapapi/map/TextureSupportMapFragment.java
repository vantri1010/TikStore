package com.baidu.mapapi.map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class TextureSupportMapFragment extends Fragment {
    private static final String a = TextureSupportMapFragment.class.getSimpleName();
    private TextureMapView b;
    private BaiduMapOptions c;

    public TextureSupportMapFragment() {
    }

    private TextureSupportMapFragment(BaiduMapOptions baiduMapOptions) {
        this.c = baiduMapOptions;
    }

    public static TextureSupportMapFragment newInstance() {
        return new TextureSupportMapFragment();
    }

    public static TextureSupportMapFragment newInstance(BaiduMapOptions baiduMapOptions) {
        return new TextureSupportMapFragment(baiduMapOptions);
    }

    public BaiduMap getBaiduMap() {
        TextureMapView textureMapView = this.b;
        if (textureMapView == null) {
            return null;
        }
        return textureMapView.getMap();
    }

    public TextureMapView getMapView() {
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
        TextureMapView textureMapView = new TextureMapView((Context) getActivity(), this.c);
        this.b = textureMapView;
        return textureMapView;
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

    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
    }
}
