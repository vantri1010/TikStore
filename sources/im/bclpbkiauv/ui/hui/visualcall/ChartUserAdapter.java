package im.bclpbkiauv.ui.hui.visualcall;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.R;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartUserAdapter extends BaseRecyclerViewAdapter<ChartViewHolder> {
    private List<String> mList = new ArrayList();
    private Map<String, ChartUserBean> mMap = new LinkedHashMap();
    private OnSubConfigChangeListener mOnSubConfigChangeListener;

    public interface OnSubConfigChangeListener {
        void onFlipView(String str, int i, boolean z);

        void onShowVideoInfo(String str, int i);
    }

    public ChartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_content_userlist_item, parent, false));
    }

    public void onBindViewHolder(ChartViewHolder holder, int position) {
        holder.mScreenLayout.setVisibility(8);
        holder.mVideoLayout.setVisibility(8);
        if (!this.mList.isEmpty()) {
            ChartUserBean item = this.mMap.get(this.mList.get(position));
            holder.mSurfaceContainer.removeAllViews();
            holder.mScreenSurfaceContainer.removeAllViews();
            if (item != null) {
                if (item.mCameraSurface != null) {
                    holder.mVideoLayout.setVisibility(0);
                    ViewParent parent = item.mCameraSurface.getParent();
                    if (parent != null) {
                        if (parent instanceof FrameLayout) {
                            ((FrameLayout) parent).removeAllViews();
                        }
                        holder.mSurfaceContainer.removeAllViews();
                    }
                    holder.mSurfaceContainer.addView(item.mCameraSurface, new FrameLayout.LayoutParams(-1, -1));
                }
                if (item.mScreenSurface != null) {
                    holder.mScreenLayout.setVisibility(0);
                    ViewParent parent2 = item.mScreenSurface.getParent();
                    if (parent2 != null) {
                        if (parent2 instanceof FrameLayout) {
                            ((FrameLayout) parent2).removeAllViews();
                        }
                        holder.mScreenSurfaceContainer.removeAllViews();
                    }
                    holder.mScreenSurfaceContainer.addView(item.mScreenSurface, new FrameLayout.LayoutParams(-1, -1));
                }
                holder.mVideoFlip.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
                holder.mVideoFlip.setChecked(item.mIsCameraFlip);
                holder.mScreenFlip.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
                holder.mScreenFlip.setChecked(item.mIsScreenFlip);
                holder.mVideoFlip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(item) {
                    private final /* synthetic */ ChartUserBean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        ChartUserAdapter.this.lambda$onBindViewHolder$0$ChartUserAdapter(this.f$1, compoundButton, z);
                    }
                });
                holder.mScreenFlip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(item) {
                    private final /* synthetic */ ChartUserBean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        ChartUserAdapter.this.lambda$onBindViewHolder$1$ChartUserAdapter(this.f$1, compoundButton, z);
                    }
                });
                holder.mVideoMediaInfo.setOnClickListener(new View.OnClickListener(item) {
                    private final /* synthetic */ ChartUserBean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        ChartUserAdapter.this.lambda$onBindViewHolder$2$ChartUserAdapter(this.f$1, view);
                    }
                });
                holder.mScreenMediaInfo.setOnClickListener(new View.OnClickListener(item) {
                    private final /* synthetic */ ChartUserBean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        ChartUserAdapter.this.lambda$onBindViewHolder$3$ChartUserAdapter(this.f$1, view);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$ChartUserAdapter(ChartUserBean item, CompoundButton buttonView, boolean isChecked) {
        OnSubConfigChangeListener onSubConfigChangeListener = this.mOnSubConfigChangeListener;
        if (onSubConfigChangeListener != null) {
            onSubConfigChangeListener.onFlipView(item.mUserId, 1001, isChecked);
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$1$ChartUserAdapter(ChartUserBean item, CompoundButton buttonView, boolean isChecked) {
        OnSubConfigChangeListener onSubConfigChangeListener = this.mOnSubConfigChangeListener;
        if (onSubConfigChangeListener != null) {
            onSubConfigChangeListener.onFlipView(item.mUserId, 1002, isChecked);
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$2$ChartUserAdapter(ChartUserBean item, View v) {
        OnSubConfigChangeListener onSubConfigChangeListener = this.mOnSubConfigChangeListener;
        if (onSubConfigChangeListener != null) {
            onSubConfigChangeListener.onShowVideoInfo(item.mUserId, 1001);
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$3$ChartUserAdapter(ChartUserBean item, View v) {
        OnSubConfigChangeListener onSubConfigChangeListener = this.mOnSubConfigChangeListener;
        if (onSubConfigChangeListener != null) {
            onSubConfigChangeListener.onShowVideoInfo(item.mUserId, 1002);
        }
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public void setData(List<ChartUserBean> list, boolean notify) {
        this.mList.clear();
        this.mMap.clear();
        for (ChartUserBean item : list) {
            this.mList.add(item.mUserId);
            this.mMap.put(item.mUserId, item);
        }
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void addData(ChartUserBean data, boolean notify) {
        this.mList.add(data.mUserId);
        this.mMap.put(data.mUserId, data);
        if (notify) {
            notifyItemInserted(this.mList.size() - 1);
        }
    }

    public void removeData(String uid, boolean notify) {
        int index = this.mList.indexOf(uid);
        if (index >= 0) {
            this.mList.remove(uid);
            this.mMap.remove(uid);
            if (notify) {
                notifyItemRemoved(index);
            }
        }
    }

    public void updateData(ChartUserBean data, boolean notify) {
        if (this.mList.contains(data.mUserId)) {
            int index = this.mList.indexOf(data.mUserId);
            this.mMap.put(data.mUserId, data);
            if (notify) {
                notifyItemChanged(index);
                return;
            }
            return;
        }
        addData(data, notify);
    }

    public ChartUserBean createDataIfNull(String uid) {
        if (!TextUtils.isEmpty(uid)) {
            ChartUserBean chartUserBean = this.mMap.get(uid);
            ChartUserBean ret = chartUserBean;
            if (chartUserBean != null) {
                return ret;
            }
        }
        return new ChartUserBean();
    }

    public boolean containsUser(String uid) {
        if (this.mList.isEmpty() || !this.mList.contains(uid)) {
            return false;
        }
        return true;
    }

    public static class ChartViewHolder extends RecyclerView.ViewHolder {
        public SwitchCompat mScreenFlip;
        public LinearLayout mScreenLayout;
        public TextView mScreenMediaInfo;
        public FrameLayout mScreenSurfaceContainer;
        public FrameLayout mSurfaceContainer;
        public SwitchCompat mVideoFlip;
        public LinearLayout mVideoLayout;
        public TextView mVideoMediaInfo;

        public ChartViewHolder(View itemView) {
            super(itemView);
            this.mVideoLayout = (LinearLayout) itemView.findViewById(R.id.chart_content_userlist_item_video_layout);
            this.mSurfaceContainer = (FrameLayout) itemView.findViewById(R.id.chart_content_userlist_item_surface_container);
            this.mScreenLayout = (LinearLayout) itemView.findViewById(R.id.chart_content_userlist_item_screen_layout);
            this.mScreenSurfaceContainer = (FrameLayout) itemView.findViewById(R.id.chart_content_userlist_item2_surface_container);
            this.mVideoFlip = (SwitchCompat) itemView.findViewById(R.id.chart_userlist_item_video_flip);
            this.mVideoMediaInfo = (TextView) itemView.findViewById(R.id.chart_userlist_item_show_video_media_info);
            this.mScreenFlip = (SwitchCompat) itemView.findViewById(R.id.chart_userlist_item_screen_flip);
            this.mScreenMediaInfo = (TextView) itemView.findViewById(R.id.chart_userlist_item_show_screen_media_info);
        }
    }

    public void setOnSubConfigChangeListener(OnSubConfigChangeListener l) {
        this.mOnSubConfigChangeListener = l;
    }
}
