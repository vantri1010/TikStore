package im.bclpbkiauv.ui.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ColorUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.List;

public class PasswordKeyboardAdapter extends RecyclerListView.SelectionAdapter {
    private Context mContext;
    private List<Integer> mNumbers = new ArrayList();

    public PasswordKeyboardAdapter(Context context, List<Integer> list) {
        this.mContext = context;
        this.mNumbers = list;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return false;
    }

    public int getItemCount() {
        return this.mNumbers.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.item_payment_password_number, parent, false);
        } else if (viewType == 1) {
            view = new View(this.mContext);
        } else if (viewType != 2) {
            view = new View(this.mContext);
        } else {
            view = new View(this.mContext);
        }
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            TextView tvNumber = (TextView) holder.itemView.findViewById(R.id.btn_number);
            ImageView ivDelete = (ImageView) holder.itemView.findViewById(R.id.iv_delete);
            tvNumber.setText(String.valueOf(this.mNumbers.get(position)));
            tvNumber.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(10.0f), ColorUtils.getColor(R.color.dialog_password_bg)));
            ivDelete.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(10.0f), ColorUtils.getColor(R.color.dialog_password_bg)));
            if (position == 11) {
                tvNumber.setVisibility(8);
                ivDelete.setVisibility(0);
                return;
            }
            ivDelete.setVisibility(8);
            tvNumber.setVisibility(0);
        }
    }

    public int getItemViewType(int i) {
        if (i == 9) {
            return 1;
        }
        return 0;
    }
}
