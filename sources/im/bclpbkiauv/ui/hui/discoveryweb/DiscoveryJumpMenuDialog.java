package im.bclpbkiauv.ui.hui.discoveryweb;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.MryAlphaLinearLayout;
import im.bclpbkiauv.ui.hviews.MryLinearLayout;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class DiscoveryJumpMenuDialog extends Dialog {
    private RecyclerListView rv;

    public DiscoveryJumpMenuDialog(Activity context) {
        super(context, R.style.DialogStyleBottomInAndOut);
        init(context);
    }

    private void init(final Activity context) {
        MryLinearLayout rootView = new MryLinearLayout(context);
        rootView.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
        rootView.setRadius(AndroidUtilities.dp(10.0f), 3);
        setContentView(rootView, new ViewGroup.LayoutParams(-1, -2));
        Display d = context.getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        int width = 0;
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            window.setGravity(80);
            lp.width = d.getWidth();
            width = lp.width;
            lp.dimAmount = 0.5f;
            window.setAttributes(lp);
        }
        setCancelable(true);
        rootView.setOrientation(1);
        LinearLayout llGameInfo = new LinearLayout(context);
        llGameInfo.setOrientation(0);
        rootView.addView(llGameInfo, LayoutHelper.createLinear(-1, 55));
        rootView.addView(new DividerCell(context), LayoutHelper.createLinear(-1.0f, 0.5f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.rv = recyclerListView;
        recyclerListView.setMinimumHeight(AndroidUtilities.dp(143.0f));
        rootView.addView(this.rv, new LinearLayout.LayoutParams(width, -2));
        this.rv.setLayoutManager(new GridLayoutManager(context, 4));
        this.rv.setAdapter(new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MryAlphaLinearLayout ll = new MryAlphaLinearLayout(context);
                ll.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                ll.setOrientation(1);
                ImageView iv = new ImageView(context);
                iv.setTag(Integer.valueOf(Holder.TAG_IV));
                ll.addView(iv, LayoutHelper.createLinear(-1, -2, 1));
                MryTextView tv = new MryTextView(context);
                tv.setTextSize(0, AndroidUtilities.sp2px(12.0f));
                tv.setTag(Integer.valueOf(Holder.TAG_TV));
                tv.setTextColor(Theme.key_windowBackgroundWhiteGrayText3);
                ll.addView(tv, LayoutHelper.createLinear(-2, -2, 1));
                return new Holder(ll);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
                int resId;
                String text;
                Holder holder = (Holder) holder1;
                if (position == 0) {
                    text = LocaleController.getString(R.string.Fold);
                    resId = R.mipmap.ic_folder;
                } else {
                    text = LocaleController.getString(R.string.Refresh);
                    resId = R.mipmap.ic_refresh;
                }
                holder.iv.setImageResource(resId);
                holder.tv.setText(text);
            }

            public int getItemCount() {
                return 2;
            }
        });
        MryTextView btnCancel = new MryTextView(context);
        btnCancel.setGravity(17);
        btnCancel.setTextSize(0, AndroidUtilities.sp2px(15.0f));
        btnCancel.setText(LocaleController.getString(R.string.Cancel));
        btnCancel.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DiscoveryJumpMenuDialog.this.lambda$init$0$DiscoveryJumpMenuDialog(view);
            }
        });
        rootView.addView(btnCancel, LayoutHelper.createLinear(-1, 50));
    }

    public /* synthetic */ void lambda$init$0$DiscoveryJumpMenuDialog(View v) {
        dismiss();
    }

    public void setOnItemClickListener(RecyclerListView.OnItemClickListener listener) {
        RecyclerListView recyclerListView = this.rv;
        if (recyclerListView != null) {
            recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(listener) {
                private final /* synthetic */ RecyclerListView.OnItemClickListener f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(View view, int i) {
                    DiscoveryJumpMenuDialog.this.lambda$setOnItemClickListener$1$DiscoveryJumpMenuDialog(this.f$1, view, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setOnItemClickListener$1$DiscoveryJumpMenuDialog(RecyclerListView.OnItemClickListener listener, View view, int position) {
        dismiss();
        if (listener != null) {
            listener.onItemClick(view, position);
        }
    }

    private static class Holder extends RecyclerListView.Holder {
        static int TAG_IV = 1;
        static int TAG_TV = 2;
        ImageView iv;
        MryTextView tv;

        public Holder(View itemView) {
            super(itemView);
            this.iv = (ImageView) itemView.findViewWithTag(Integer.valueOf(TAG_IV));
            this.tv = (MryTextView) itemView.findViewWithTag(Integer.valueOf(TAG_TV));
        }
    }
}
