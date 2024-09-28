package im.bclpbkiauv.ui.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseVPAdapter<T> extends FragmentStatePagerAdapter {
    private List<T> mData;
    private List<Fragment> mFragments;

    public BaseVPAdapter(FragmentManager fm, T... mData2) {
        this(fm, new ArrayList(Arrays.asList(mData2)));
    }

    public BaseVPAdapter(FragmentManager fm, List<T> mData2) {
        this(fm, mData2, (List<Fragment>) null);
    }

    public BaseVPAdapter(FragmentManager fm, List<T> mData2, List<Fragment> mFragments2) {
        super(fm);
        this.mData = mData2;
        this.mFragments = mFragments2;
    }

    public Fragment getIMItem(int position) {
        return null;
    }

    public Fragment getItem(int position) {
        List<Fragment> list = this.mFragments;
        if (list == null || position < 0 || position >= list.size()) {
            return getIMItem(position);
        }
        return this.mFragments.get(position);
    }

    public int getItemPosition(Object object) {
        return -2;
    }

    public int getCount() {
        List<T> list = this.mData;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public CharSequence getPageTitle(int position) {
        List<T> list = this.mData;
        return list != null ? list.get(position).toString() : "";
    }

    public void setData(List<T> mData2) {
        this.mData = mData2;
    }

    public T getDataItem(int position) {
        List<T> list = this.mData;
        if (list == null || position < 0 || position > list.size()) {
            return null;
        }
        return this.mData.get(position);
    }

    public void setDataAndNotify(List<T> mData2) {
        this.mData = mData2;
        notifyDataSetChanged();
    }

    public void destroy() {
        if (this.mFragments != null) {
            this.mFragments = null;
        }
        if (this.mData != null) {
            this.mData = null;
        }
    }
}
