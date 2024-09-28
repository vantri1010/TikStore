package im.bclpbkiauv.ui.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.cells.DrawerAddCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hcells.PopUserCell;
import java.util.ArrayList;
import java.util.Collections;

public class AccountsAdapter extends RecyclerListView.SelectionAdapter {
    private ArrayList<Integer> accountNumbers = new ArrayList<>();
    private Context mContext;

    public AccountsAdapter(Context context) {
        this.mContext = context;
        resetItems();
    }

    private void resetItems() {
        this.accountNumbers.clear();
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                this.accountNumbers.add(Integer.valueOf(a));
            }
        }
        Collections.sort(this.accountNumbers, $$Lambda$AccountsAdapter$Lii1zRUz1WQeGw2H11JOQq3nMDI.INSTANCE);
    }

    static /* synthetic */ int lambda$resetItems$0(Integer o1, Integer o2) {
        long l1 = (long) UserConfig.getInstance(o1.intValue()).loginTime;
        long l2 = (long) UserConfig.getInstance(o2.intValue()).loginTime;
        if (l1 > l2) {
            return 1;
        }
        if (l1 < l2) {
            return -1;
        }
        return 0;
    }

    public int getItemCount() {
        return 1 + this.accountNumbers.size();
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType != 0) {
            view = new PopUserCell(this.mContext);
        } else {
            view = new DrawerAddCell(this.mContext);
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            ((PopUserCell) holder.itemView).setAccount(this.accountNumbers.get(position - 1).intValue());
        }
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        return 1;
    }
}
