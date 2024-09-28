package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseFcAdapter<T> extends RecyclerView.Adapter<SmartViewHolder> implements ListAdapter {
    protected boolean flag;
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    protected int mLastPosition = -1;
    protected final int mLayoutId;
    protected final List<T> mList;
    protected AdapterView.OnItemClickListener mListener;
    protected boolean mOpenAnimationEnable = false;

    /* access modifiers changed from: protected */
    public abstract void onBindViewHolder(SmartViewHolder smartViewHolder, T t, int i);

    public BaseFcAdapter(int layoutId) {
        setHasStableIds(false);
        this.mList = new ArrayList();
        this.mLayoutId = layoutId;
    }

    public BaseFcAdapter(Collection<T> collection, int layoutId) {
        setHasStableIds(false);
        this.mList = new ArrayList(collection);
        this.mLayoutId = layoutId;
    }

    public BaseFcAdapter(Collection<T> collection, int layoutId, AdapterView.OnItemClickListener listener, boolean flag2) {
        setHasStableIds(false);
        setOnItemClickListener(listener);
        this.mList = new ArrayList(collection);
        this.mLayoutId = layoutId;
        this.flag = flag2;
    }

    public BaseFcAdapter(Collection<T> collection, int layoutId, AdapterView.OnItemClickListener listener) {
        setHasStableIds(false);
        setOnItemClickListener(listener);
        this.mList = new ArrayList(collection);
        this.mLayoutId = layoutId;
    }

    private void addAnimate(SmartViewHolder holder, int postion) {
        if (this.mOpenAnimationEnable && this.mLastPosition < postion) {
            holder.itemView.setAlpha(0.0f);
            holder.itemView.animate().alpha(1.0f).start();
            this.mLastPosition = postion;
        }
    }

    public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (this.flag) {
            return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.mLayoutId, (ViewGroup) null), this.mListener);
        }
        return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.mLayoutId, parent, false), this.mListener);
    }

    public void onBindViewHolder(SmartViewHolder holder, int position) {
        onBindViewHolder(holder, position < this.mList.size() ? this.mList.get(position) : null, position);
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public List<T> getDataList() {
        return this.mList;
    }

    public void onViewAttachedToWindow(SmartViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        addAnimate(holder, holder.getLayoutPosition());
    }

    public void setOpenAnimationEnable(boolean enabled) {
        this.mOpenAnimationEnable = enabled;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyListDataSetChanged() {
        this.mDataSetObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        this.mDataSetObservable.notifyInvalidated();
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int position) {
        return true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SmartViewHolder holder;
        if (convertView != null) {
            holder = (SmartViewHolder) convertView.getTag();
        } else {
            holder = onCreateViewHolder(parent, getItemViewType(position));
            convertView = holder.itemView;
            convertView.setTag(holder);
        }
        holder.setPosition(position);
        onBindViewHolder(holder, position);
        addAnimate(holder, position);
        return convertView;
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    public Object getItem(int position) {
        return this.mList.get(position);
    }

    public int getCount() {
        return this.mList.size();
    }

    public T get(int index) {
        return this.mList.get(index);
    }

    public BaseFcAdapter<T> setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mListener = listener;
        return this;
    }

    public BaseFcAdapter<T> refresh(Collection<T> collection) {
        this.mList.clear();
        this.mList.addAll(collection);
        notifyDataSetChanged();
        notifyListDataSetChanged();
        this.mLastPosition = -1;
        return this;
    }

    public BaseFcAdapter<T> loadMore(Collection<T> collection) {
        if (collection == null) {
            return this;
        }
        this.mList.addAll(collection);
        notifyDataSetChanged();
        notifyListDataSetChanged();
        return this;
    }

    public BaseFcAdapter<T> insert(Collection<T> collection) {
        this.mList.addAll(0, collection);
        notifyItemRangeInserted(0, collection.size());
        notifyListDataSetChanged();
        return this;
    }
}
