package im.bclpbkiauv.ui.components.banner.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.banner.holder.IViewHolder;
import im.bclpbkiauv.ui.components.banner.listener.OnBannerListener;
import im.bclpbkiauv.ui.components.banner.util.BannerUtils;
import java.util.ArrayList;
import java.util.List;

public abstract class BannerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements IViewHolder<T, VH> {
    protected List<T> mDatas = new ArrayList();
    private int mIncreaseCount = 2;
    private OnBannerListener<T> mOnBannerListener;
    private VH mViewHolder;

    public BannerAdapter(List<T> datas) {
        setDatas(datas);
    }

    public void setDatas(List<T> datas) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    public T getData(int position) {
        return this.mDatas.get(position);
    }

    public T getRealData(int position) {
        return this.mDatas.get(getRealPosition(position));
    }

    public final void onBindViewHolder(VH holder, int position) {
        this.mViewHolder = holder;
        int real = getRealPosition(position);
        T data = this.mDatas.get(real);
        holder.itemView.setTag(R.id.banner_data_key, data);
        holder.itemView.setTag(R.id.banner_pos_key, Integer.valueOf(real));
        onBindView(holder, this.mDatas.get(real), real, getRealCount());
        if (this.mOnBannerListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener(data, real) {
                private final /* synthetic */ Object f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    BannerAdapter.this.lambda$onBindViewHolder$0$BannerAdapter(this.f$1, this.f$2, view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$BannerAdapter(Object data, int real, View view) {
        this.mOnBannerListener.OnBannerClick(data, real);
    }

    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH vh = (RecyclerView.ViewHolder) onCreateHolder(parent, viewType);
        vh.itemView.setOnClickListener(new View.OnClickListener(vh) {
            private final /* synthetic */ RecyclerView.ViewHolder f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                BannerAdapter.this.lambda$onCreateViewHolder$1$BannerAdapter(this.f$1, view);
            }
        });
        return vh;
    }

    public /* synthetic */ void lambda$onCreateViewHolder$1$BannerAdapter(RecyclerView.ViewHolder vh, View v) {
        if (this.mOnBannerListener != null) {
            this.mOnBannerListener.OnBannerClick(vh.itemView.getTag(R.id.banner_data_key), ((Integer) vh.itemView.getTag(R.id.banner_pos_key)).intValue());
        }
    }

    public int getItemCount() {
        return getRealCount() > 1 ? getRealCount() + this.mIncreaseCount : getRealCount();
    }

    public int getRealCount() {
        List<T> list = this.mDatas;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public int getRealPosition(int position) {
        return BannerUtils.getRealPosition(this.mIncreaseCount == 2, position, getRealCount());
    }

    public void setOnBannerListener(OnBannerListener<T> listener) {
        this.mOnBannerListener = listener;
    }

    public VH getViewHolder() {
        return this.mViewHolder;
    }

    public void setIncreaseCount(int increaseCount) {
        this.mIncreaseCount = increaseCount;
    }
}
