package im.bclpbkiauv.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hviews.MryTextView;

public abstract class WalletSelect1LineDialog<T> extends WalletSelectAbsDialog<T, WalletSelect1LineDialog, Holder1Line> {
    public WalletSelect1LineDialog(Context context) {
        super(context);
    }

    public WalletSelect1LineDialog(Context context, boolean useNestScrollViewAsParent) {
        super(context, useNestScrollViewAsParent);
    }

    public WalletSelect1LineDialog(Context context, int backgroundType) {
        super(context, backgroundType);
    }

    public WalletSelect1LineDialog(Context context, int backgroundType, boolean useNestScrollViewAsParent) {
        super(context, backgroundType, useNestScrollViewAsParent);
    }

    public WalletSelect1LineDialog(Context context, boolean needFocus, int backgroundType, boolean useNestScrollViewAsParent) {
        super(context, needFocus, backgroundType, useNestScrollViewAsParent);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [im.bclpbkiauv.ui.dialogs.WalletSelectAbsDialog$OnConfirmClickListener, im.bclpbkiauv.ui.dialogs.WalletSelectAbsDialog$OnConfirmClickListener<T, im.bclpbkiauv.ui.dialogs.WalletSelect1LineDialog>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public im.bclpbkiauv.ui.dialogs.WalletSelect1LineDialog setOnConfrimClickListener(im.bclpbkiauv.ui.dialogs.WalletSelectAbsDialog.OnConfirmClickListener<T, im.bclpbkiauv.ui.dialogs.WalletSelect1LineDialog> r2) {
        /*
            r1 = this;
            im.bclpbkiauv.ui.dialogs.WalletSelectAbsDialog r0 = super.setOnConfrimClickListener(r2)
            im.bclpbkiauv.ui.dialogs.WalletSelect1LineDialog r0 = (im.bclpbkiauv.ui.dialogs.WalletSelect1LineDialog) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.dialogs.WalletSelect1LineDialog.setOnConfrimClickListener(im.bclpbkiauv.ui.dialogs.WalletSelectAbsDialog$OnConfirmClickListener):im.bclpbkiauv.ui.dialogs.WalletSelect1LineDialog");
    }

    public Holder1Line onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder1Line(LayoutInflater.from(getContext()).inflate(R.layout.wallet_dialog_item_select_1line, parent, false));
    }

    public void onBindViewHolder(RecyclerListView.SelectionAdapter adapter, Holder1Line holder, int position, T item) {
        super.onBindViewHolder(adapter, holder, position, item);
    }

    public static class Holder1Line extends PageHolder {
        public DividerCell divider;
        public ImageView ivIcon;
        public ImageView ivSelect;
        public MryTextView tvTitle;
        public MryTextView tvValue;

        public Holder1Line(View itemView) {
            super(itemView);
            this.ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            this.tvTitle = (MryTextView) itemView.findViewById(R.id.tvTitle);
            this.tvValue = (MryTextView) itemView.findViewById(R.id.tvValue);
            this.ivSelect = (ImageView) itemView.findViewById(R.id.ivSelect);
            this.divider = (DividerCell) itemView.findViewById(R.id.divider);
        }
    }
}
