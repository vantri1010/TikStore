package im.bclpbkiauv.ui.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.cells.PopMenuCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;

public class ChatsTypeAdapter extends RecyclerListView.SelectionAdapter {
    private ArrayList<Integer> iconsList = new ArrayList<>();
    private Context mContext;
    private ArrayList<String> namesList = new ArrayList<>();

    public ChatsTypeAdapter(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        if (this.iconsList.isEmpty()) {
            this.iconsList.add(Integer.valueOf(R.mipmap.ic_pop_user_selected));
            this.iconsList.add(Integer.valueOf(R.mipmap.ic_pop_groups_selected));
            this.iconsList.add(Integer.valueOf(R.mipmap.ic_pop_channels_selected));
            this.iconsList.add(Integer.valueOf(R.mipmap.ic_pop_unread_selected));
            this.iconsList.add(Integer.valueOf(R.mipmap.ic_pop_chats_selected));
        }
        if (this.namesList.isEmpty()) {
            this.namesList.add(LocaleController.getString(R.string.Users));
            this.namesList.add(LocaleController.getString(R.string.MyGroups));
            this.namesList.add(LocaleController.getString(R.string.MyChannels));
            this.namesList.add(LocaleController.getString(R.string.UnreadMsg));
            this.namesList.add(LocaleController.getString(R.string.Chats));
        }
    }

    public int getItemCount() {
        return this.iconsList.size();
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new PopMenuCell(this.mContext);
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PopMenuCell) holder.itemView).setTextAndIcon(this.namesList.get(position), this.iconsList.get(position).intValue());
    }

    public int getItemViewType(int i) {
        return 0;
    }
}
