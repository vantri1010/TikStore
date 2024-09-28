package com.preview;

import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.preview.PhotoPreviewFragment;

public class PhotoPreviewPagerAdapter extends BaseFragmentPagerAdapter {
    private PhotoPreviewFragment currentFragment;
    private PhotoPreviewFragment.OnExitListener mFragmentOnExitListener;
    private OnUpdateFragmentDataListener mOnUpdateFragmentDataListener;
    private int size;

    public interface OnUpdateFragmentDataListener {
        void onUpdate(PhotoPreviewFragment photoPreviewFragment, int i);
    }

    public PhotoPreviewPagerAdapter(FragmentManager fm, int size2) {
        super(fm);
        this.size = size2;
    }

    public PhotoPreviewFragment getCurrentFragment() {
        return this.currentFragment;
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        this.currentFragment = (PhotoPreviewFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getItem(int position) {
        PhotoPreviewFragment fragment = new PhotoPreviewFragment();
        fragment.setOnExitListener(this.mFragmentOnExitListener);
        return fragment;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        OnUpdateFragmentDataListener onUpdateFragmentDataListener;
        Object item = super.instantiateItem(container, position);
        if ((item instanceof PhotoPreviewFragment) && (onUpdateFragmentDataListener = this.mOnUpdateFragmentDataListener) != null) {
            onUpdateFragmentDataListener.onUpdate((PhotoPreviewFragment) item, position);
        }
        return item;
    }

    public boolean dataIsChange(Object object) {
        return true;
    }

    public int getCount() {
        return this.size;
    }

    public void setOnUpdateFragmentDataListener(OnUpdateFragmentDataListener onUpdateFragmentDataListener) {
        this.mOnUpdateFragmentDataListener = onUpdateFragmentDataListener;
    }

    public void setFragmentOnExitListener(PhotoPreviewFragment.OnExitListener fragmentOnExitListener) {
        this.mFragmentOnExitListener = fragmentOnExitListener;
    }

    public void setData(int size2) {
        this.size = size2;
        notifyDataSetChanged();
    }
}
