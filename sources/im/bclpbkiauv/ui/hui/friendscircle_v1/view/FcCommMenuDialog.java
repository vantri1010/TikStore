package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.List;

public class FcCommMenuDialog extends Dialog {
    public static final int STYLE_COMMON = 0;
    public static final int STYLE_TRANS = 1;
    private final DividerCell divider;
    /* access modifiers changed from: private */
    public int[] iTextColorArr;
    private final ListAdapter mAdapter;
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

    public FcCommMenuDialog setTitle(CharSequence text, int color, int size) {
        this.tvTitle.setTextColor(color);
        this.tvTitle.setTextSize((float) size);
        this.tvTitle.setText(text);
        this.tvTitle.setVisibility(0);
        this.divider.setVisibility(0);
        return this;
    }

    public FcCommMenuDialog setCancle(int color, int size) {
        if (color != 0) {
            this.tvCancel.setTextColor(color);
        }
        if (size != 0) {
            this.tvCancel.setTextSize((float) size);
        }
        return this;
    }

    public FcCommMenuDialog(Activity context, List<String> arrList, int iTextColor, RecyclerviewItemClickCallBack callback) {
        this(context, arrList, (List<Integer>) null, iTextColor, callback, 0);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public FcCommMenuDialog(Activity context, List<String> arrList, List<Integer> arrIcon, int[] iTextColorArr2, RecyclerviewItemClickCallBack callback, int animStyle) {
        this(context, arrList, arrIcon, iTextColorArr2.length > 0 ? iTextColorArr2[0] : 0, callback, animStyle);
        this.iTextColorArr = iTextColorArr2;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FcCommMenuDialog(android.app.Activity r17, java.util.List<java.lang.String> r18, java.util.List<java.lang.Integer> r19, int r20, im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog.RecyclerviewItemClickCallBack r21, int r22) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = 2131755549(0x7f10021d, float:1.914198E38)
            r0.<init>(r1, r2)
            android.content.Context r2 = r16.getContext()
            android.view.LayoutInflater r2 = android.view.LayoutInflater.from(r2)
            r3 = 2131493014(0x7f0c0096, float:1.8609496E38)
            r4 = 0
            android.view.View r2 = r2.inflate(r3, r4)
            r0.setContentView(r2)
            android.view.WindowManager r3 = r17.getWindowManager()
            android.view.Display r4 = r3.getDefaultDisplay()
            android.view.Window r5 = r16.getWindow()
            android.view.WindowManager$LayoutParams r6 = r5.getAttributes()
            r7 = 80
            r5.setGravity(r7)
            r7 = 1
            r8 = r22
            if (r8 != r7) goto L_0x003d
            r9 = 2131755551(0x7f10021f, float:1.9141984E38)
            r5.setWindowAnimations(r9)
        L_0x003d:
            r9 = 1050253722(0x3e99999a, float:0.3)
            r6.dimAmount = r9
            int r9 = r4.getWidth()
            r6.width = r9
            r5.setAttributes(r6)
            r0.setCancelable(r7)
            r9 = r18
            r0.mArrDataSet = r9
            r10 = r19
            r0.mArrIcon = r10
            r11 = r20
            r0.miTextColor = r11
            r12 = 2131297730(0x7f0905c2, float:1.8213413E38)
            android.view.View r12 = r2.findViewById(r12)
            android.widget.TextView r12 = (android.widget.TextView) r12
            r0.tvCancel = r12
            r12 = 2131297862(0x7f090646, float:1.821368E38)
            android.view.View r12 = r2.findViewById(r12)
            android.widget.TextView r12 = (android.widget.TextView) r12
            r0.tvTitle = r12
            r12 = 2131296534(0x7f090116, float:1.8210987E38)
            android.view.View r12 = r2.findViewById(r12)
            im.bclpbkiauv.ui.cells.DividerCell r12 = (im.bclpbkiauv.ui.cells.DividerCell) r12
            r0.divider = r12
            java.lang.String r13 = "windowBackgroundGray"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r12.setBackgroundColor(r13)
            r12 = 2131296482(0x7f0900e2, float:1.8210882E38)
            android.view.View r12 = r2.findViewById(r12)
            java.lang.String r13 = "windowBackgroundWhite"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r15 = 1092616192(0x41200000, float:10.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            float r7 = (float) r7
            im.bclpbkiauv.messenger.utils.ShapeUtils$ShapeDrawable r7 = im.bclpbkiauv.messenger.utils.ShapeUtils.create(r14, r7)
            r12.setBackground(r7)
            android.widget.TextView r7 = r0.tvCancel
            int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            float r13 = (float) r13
            im.bclpbkiauv.messenger.utils.ShapeUtils$ShapeDrawable r12 = im.bclpbkiauv.messenger.utils.ShapeUtils.create(r12, r13)
            r7.setBackground(r12)
            r7 = 2131297227(0x7f0903cb, float:1.8212393E38)
            android.view.View r7 = r2.findViewById(r7)
            im.bclpbkiauv.ui.components.RecyclerListView r7 = (im.bclpbkiauv.ui.components.RecyclerListView) r7
            r0.recyclerListView = r7
            r12 = 0
            r7.setVerticalScrollBarEnabled(r12)
            im.bclpbkiauv.ui.components.RecyclerListView r7 = r0.recyclerListView
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog$ListAdapter r13 = new im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog$ListAdapter
            r13.<init>(r1)
            r0.mAdapter = r13
            r7.setAdapter(r13)
            im.bclpbkiauv.ui.components.RecyclerListView r7 = r0.recyclerListView
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog$1 r13 = new im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog$1
            r14 = 1
            r13.<init>(r1, r14, r12)
            r7.setLayoutManager(r13)
            im.bclpbkiauv.ui.components.RecyclerListView r7 = r0.recyclerListView
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog$2 r12 = new im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog$2
            r13 = r21
            r12.<init>(r13)
            r7.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r12)
            android.widget.TextView r7 = r0.tvCancel
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog$3 r12 = new im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog$3
            r12.<init>()
            r7.setOnClickListener(r12)
            android.widget.TextView r7 = r0.tvCancel
            java.lang.String r12 = "windowBackgroundWhiteBlackText"
            int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r7.setTextColor(r12)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog.<init>(android.app.Activity, java.util.List, java.util.List, int, im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog$RecyclerviewItemClickCallBack, int):void");
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return FcCommMenuDialog.this.mArrDataSet.size();
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LinearLayout linearLayout = (LinearLayout) holder.itemView.findViewById(R.id.ll_item_menu_container);
            if (FcCommMenuDialog.this.mArrIcon != null) {
                linearLayout.setGravity(GravityCompat.START);
                ((ImageView) holder.itemView.findViewById(R.id.iv_image)).setImageResource(((Integer) FcCommMenuDialog.this.mArrIcon.get(position)).intValue());
            } else {
                linearLayout.setGravity(17);
                ((ImageView) holder.itemView.findViewById(R.id.iv_image)).setVisibility(8);
            }
            TextView tvView = (TextView) holder.itemView.findViewById(R.id.tv_view);
            tvView.setText((CharSequence) FcCommMenuDialog.this.mArrDataSet.get(position));
            if (FcCommMenuDialog.this.miTextColor != 0) {
                if (FcCommMenuDialog.this.iTextColorArr == null || position >= FcCommMenuDialog.this.iTextColorArr.length) {
                    tvView.setTextColor(FcCommMenuDialog.this.miTextColor);
                } else {
                    tvView.setTextColor(FcCommMenuDialog.this.iTextColorArr[position]);
                }
            }
            holder.itemView.findViewById(R.id.tv_bottom).setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            if (position == FcCommMenuDialog.this.mArrDataSet.size() - 1) {
                holder.itemView.findViewById(R.id.tv_bottom).setVisibility(4);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(LayoutInflater.from(this.mContext).inflate(R.layout.item_dialog_fc_comm_menu, (ViewGroup) null, false));
        }
    }
}
