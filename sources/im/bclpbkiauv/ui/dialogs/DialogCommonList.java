package im.bclpbkiauv.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.List;

public class DialogCommonList extends Dialog {
    public static final int STYLE_BOTTOM_IN_AND_OUT = 2;
    public static final int STYLE_COMMON = 0;
    public static final int STYLE_TRANS = 1;
    private View divider;
    /* access modifiers changed from: private */
    public int[] iTextColorArr;
    private RecyclerviewItemClickCallBack itemClickCallBack;
    private ListAdapter mAdapter;
    /* access modifiers changed from: private */
    public List<String> mArrDataSet;
    /* access modifiers changed from: private */
    public List<Integer> mArrIcon;
    /* access modifiers changed from: private */
    public int miTextColor;
    private RecyclerListView recyclerListView;
    private TextView tvCancel;
    private TextView tvTitle;

    public interface RecyclerviewItemClickCallBack {
        void onRecyclerviewItemClick(int i);
    }

    public DialogCommonList(Activity context, int animStyle) {
        this(context, (List<String>) null, (List<Integer>) null, 0, (RecyclerviewItemClickCallBack) null, animStyle);
    }

    public DialogCommonList(Activity context, List<String> arrList, int iTextColor, RecyclerviewItemClickCallBack callback) {
        this(context, arrList, (List<Integer>) null, iTextColor, callback, 0);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public DialogCommonList(Activity context, List<String> arrList, List<Integer> arrIcon, int[] iTextColorArr2, RecyclerviewItemClickCallBack callback, int animStyle) {
        this(context, arrList, arrIcon, iTextColorArr2.length > 0 ? iTextColorArr2[0] : 0, callback, animStyle);
        this.iTextColorArr = iTextColorArr2;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DialogCommonList(Activity context, List<String> arrList, List<Integer> arrIcon, int iTextColor, RecyclerviewItemClickCallBack callback, int animStyle) {
        super(context, (animStyle == 0 || animStyle == 1) ? R.style.commondialog : R.style.DialogStyleBottomInAndOut);
        setItemTextList(arrList);
        setItemIconList(arrIcon);
        setItemTextColor(iTextColor);
        setRecyclerViewItemClickCallBack(callback);
        init(context, animStyle);
    }

    private void init(Activity context, int animStyle) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_common_list, (ViewGroup) null, false);
        setContentView(view);
        Display d = context.getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            window.setGravity(80);
            if (animStyle == 1) {
                window.setWindowAnimations(R.style.dialog_trans_animation);
            }
            lp.dimAmount = 0.3f;
            lp.width = d.getWidth();
            window.setAttributes(lp);
        }
        setCancelable(true);
        View findViewById = view.findViewById(R.id.containerContent);
        this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
        this.divider = view.findViewById(R.id.divider);
        this.recyclerListView = (RecyclerListView) view.findViewById(R.id.rlv_list);
        TextView textView = (TextView) view.findViewById(R.id.tv_cancel);
        this.tvCancel = textView;
        textView.setText(LocaleController.getString(R.string.Cancel));
        int i = this.miTextColor;
        if (i != 0) {
            this.tvCancel.setTextColor(i);
        }
        this.recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.recyclerListView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.mAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                DialogCommonList.this.lambda$init$0$DialogCommonList(view, i);
            }
        });
        this.tvCancel.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DialogCommonList.this.lambda$init$1$DialogCommonList(view);
            }
        });
    }

    public /* synthetic */ void lambda$init$0$DialogCommonList(View view12, int position) {
        dismiss();
        RecyclerviewItemClickCallBack recyclerviewItemClickCallBack = this.itemClickCallBack;
        if (recyclerviewItemClickCallBack != null) {
            recyclerviewItemClickCallBack.onRecyclerviewItemClick(position);
        }
    }

    public /* synthetic */ void lambda$init$1$DialogCommonList(View view1) {
        dismiss();
    }

    public DialogCommonList setTitle(CharSequence text, int color, int size) {
        this.tvTitle.setTextColor(color);
        this.tvTitle.setTextSize((float) size);
        this.tvTitle.setText(text);
        this.tvTitle.setVisibility(0);
        this.divider.setVisibility(0);
        return this;
    }

    public DialogCommonList setCancle(int color, int size) {
        if (color != 0) {
            this.tvCancel.setTextColor(color);
        }
        if (size != 0) {
            this.tvCancel.setTextSize((float) size);
        }
        return this;
    }

    public DialogCommonList setRvLayoutManager(RecyclerView.LayoutManager manager) {
        if (manager != null) {
            this.recyclerListView.setLayoutManager(manager);
        }
        return this;
    }

    public DialogCommonList setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            this.recyclerListView.setAdapter(adapter);
        }
        return this;
    }

    public DialogCommonList setItemTextList(List<String> textList) {
        this.mArrDataSet = textList;
        return this;
    }

    public DialogCommonList setItemIconList(List<Integer> iconList) {
        this.mArrIcon = iconList;
        return this;
    }

    public DialogCommonList setItemTextColor(int textColor) {
        this.miTextColor = textColor;
        return this;
    }

    public DialogCommonList setRecyclerViewItemClickCallBack(RecyclerviewItemClickCallBack callBack) {
        this.itemClickCallBack = callBack;
        return this;
    }

    public DialogCommonList setTitleDividerVisible(int visible) {
        this.divider.setVisibility(visible);
        return this;
    }

    public TextView getTitleView() {
        return this.tvTitle;
    }

    public TextView getCancelView() {
        return this.tvCancel;
    }

    public View getDivider() {
        return this.divider;
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DialogCommonList.this.mArrDataSet.size();
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (DialogCommonList.this.mArrIcon != null) {
                ((ImageView) holder.itemView.findViewById(R.id.iv_image)).setImageResource(((Integer) DialogCommonList.this.mArrIcon.get(position)).intValue());
            } else {
                ((ImageView) holder.itemView.findViewById(R.id.iv_image)).setVisibility(8);
            }
            TextView tvView = (TextView) holder.itemView.findViewById(R.id.tv_view);
            tvView.setText((CharSequence) DialogCommonList.this.mArrDataSet.get(position));
            if (DialogCommonList.this.miTextColor != 0) {
                if (DialogCommonList.this.iTextColorArr == null || position >= DialogCommonList.this.iTextColorArr.length) {
                    tvView.setTextColor(DialogCommonList.this.miTextColor);
                } else {
                    tvView.setTextColor(DialogCommonList.this.iTextColorArr[position]);
                }
            }
            if (position == DialogCommonList.this.mArrDataSet.size() - 1) {
                holder.itemView.findViewById(R.id.tv_bottom).setVisibility(4);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(LayoutInflater.from(this.mContext).inflate(R.layout.item_dialog_common_list, (ViewGroup) null, false));
        }
    }
}
