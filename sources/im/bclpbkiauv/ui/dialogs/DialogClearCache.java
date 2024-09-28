package im.bclpbkiauv.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.settings.CacheControlSettingActivity;
import java.util.List;

public class DialogClearCache extends Dialog {
    /* access modifiers changed from: private */
    public ListAdapter mAdapter = null;
    /* access modifiers changed from: private */
    public boolean[] mArrByte;
    /* access modifiers changed from: private */
    public List<CacheControlSettingActivity.CacheInfo> mArrDataSet;
    private long miTotal = 0;
    private RecyclerListView recyclerListView;
    private TextView tvCancel;
    /* access modifiers changed from: private */
    public TextView tvClear;

    public interface CacheClearSelectCallback {
        void onCacheClearSelect(boolean[] zArr);
    }

    public DialogClearCache(Activity context, List<CacheControlSettingActivity.CacheInfo> arrList, final CacheClearSelectCallback callback) {
        super(context, R.style.commondialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_clear_cache, (ViewGroup) null);
        setContentView(view);
        Display d = context.getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(80);
        lp.width = d.getWidth();
        window.setAttributes(lp);
        setCancelable(true);
        this.mArrDataSet = arrList;
        this.mArrByte = new boolean[arrList.size()];
        this.tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        this.tvClear = (TextView) view.findViewById(R.id.tv_clear);
        RecyclerListView recyclerListView2 = (RecyclerListView) view.findViewById(R.id.rlv_list);
        this.recyclerListView = recyclerListView2;
        recyclerListView2.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView3 = this.recyclerListView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.mAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                DialogClearCache.this.mArrByte[position] = !DialogClearCache.this.mArrByte[position];
                long lsize = 0;
                for (int i = 0; i < DialogClearCache.this.mArrDataSet.size(); i++) {
                    if (DialogClearCache.this.mArrByte[i]) {
                        lsize += ((CacheControlSettingActivity.CacheInfo) DialogClearCache.this.mArrDataSet.get(i)).getMlCacheSize();
                    }
                }
                DialogClearCache.this.tvClear.setText(LocaleController.formatString("ClearFewChatsTitle", R.string.ClearFewChatsTitle, AndroidUtilities.formatFileSize(lsize)));
                DialogClearCache.this.mAdapter.notifyDataSetChanged();
            }
        });
        this.tvCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DialogClearCache.this.dismiss();
            }
        });
        this.tvClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DialogClearCache.this.dismiss();
                CacheClearSelectCallback cacheClearSelectCallback = callback;
                if (cacheClearSelectCallback != null) {
                    cacheClearSelectCallback.onCacheClearSelect(DialogClearCache.this.mArrByte);
                }
            }
        });
        this.tvClear.setText(LocaleController.formatString("ClearFewChatsTitle", R.string.ClearFewChatsTitle, AndroidUtilities.formatFileSize(0)));
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DialogClearCache.this.mArrDataSet.size();
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView.findViewById(R.id.tv_name)).setText(getNameByIndex(((CacheControlSettingActivity.CacheInfo) DialogClearCache.this.mArrDataSet.get(position)).getMiIndex()));
            ((TextView) holder.itemView.findViewById(R.id.tv_size)).setText(AndroidUtilities.formatFileSize(((CacheControlSettingActivity.CacheInfo) DialogClearCache.this.mArrDataSet.get(position)).getMlCacheSize()));
            if (DialogClearCache.this.mArrByte[position]) {
                ((ImageView) holder.itemView.findViewById(R.id.iv_choose)).setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.SRC_IN));
                ((ImageView) holder.itemView.findViewById(R.id.iv_choose)).setImageDrawable(this.mContext.getResources().getDrawable(R.mipmap.ic_selected));
                return;
            }
            ((ImageView) holder.itemView.findViewById(R.id.iv_choose)).setImageDrawable((Drawable) null);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(LayoutInflater.from(this.mContext).inflate(R.layout.item_dialog_clear_cache, (ViewGroup) null, false));
        }

        private String getNameByIndex(int a) {
            if (a == 0) {
                return LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache);
            }
            if (a == 1) {
                return LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache);
            }
            if (a == 2) {
                return LocaleController.getString("LocalDocumentCache", R.string.LocalDocumentCache);
            }
            if (a == 3) {
                return LocaleController.getString("LocalMusicCache", R.string.LocalMusicCache);
            }
            if (a == 4) {
                return LocaleController.getString("LocalAudioCache", R.string.LocalAudioCache);
            }
            if (a == 5) {
                return LocaleController.getString("LocalCache", R.string.LocalCache);
            }
            return "";
        }
    }
}
