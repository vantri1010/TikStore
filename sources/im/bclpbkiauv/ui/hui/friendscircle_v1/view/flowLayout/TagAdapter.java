package im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout;

import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TagAdapter<T> {
    @Deprecated
    private HashSet<Integer> mCheckedPosList = new HashSet<>();
    private OnDataChangedListener mOnDataChangedListener;
    private List<T> mTagDatas;

    interface OnDataChangedListener {
        void onChanged();
    }

    public abstract View getView(FlowLayout flowLayout, int i, T t);

    public TagAdapter(List<T> datas) {
        this.mTagDatas = datas;
    }

    @Deprecated
    public TagAdapter(T[] datas) {
        this.mTagDatas = new ArrayList(Arrays.asList(datas));
    }

    public void setData(List<T> mTagDatas2) {
        this.mTagDatas = mTagDatas2;
    }

    /* access modifiers changed from: package-private */
    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.mOnDataChangedListener = listener;
    }

    @Deprecated
    public void setSelectedList(int... poses) {
        Set<Integer> set = new HashSet<>();
        for (int pos : poses) {
            set.add(Integer.valueOf(pos));
        }
        setSelectedList(set);
    }

    @Deprecated
    public void setSelectedList(Set<Integer> set) {
        this.mCheckedPosList.clear();
        if (set != null) {
            this.mCheckedPosList.addAll(set);
        }
        notifyDataChanged();
    }

    /* access modifiers changed from: package-private */
    @Deprecated
    public HashSet<Integer> getPreCheckedList() {
        return this.mCheckedPosList;
    }

    public int getCount() {
        List<T> list = this.mTagDatas;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void notifyDataChanged() {
        OnDataChangedListener onDataChangedListener = this.mOnDataChangedListener;
        if (onDataChangedListener != null) {
            onDataChangedListener.onChanged();
        }
    }

    public T getItem(int position) {
        return this.mTagDatas.get(position);
    }

    public void onSelected(int position, View view) {
        Log.d("zhy", "onSelected " + position);
    }

    public void unSelected(int position, View view) {
        Log.d("zhy", "unSelected " + position);
    }

    public boolean setSelected(int position, T t) {
        return false;
    }
}
