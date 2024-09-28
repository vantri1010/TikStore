package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.cells.DrawerActionCell;
import im.bclpbkiauv.ui.cells.DrawerAddCell;
import im.bclpbkiauv.ui.cells.DrawerProfileCell;
import im.bclpbkiauv.ui.cells.DrawerUserCell;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Collections;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {
    private ArrayList<Integer> accountNumbers = new ArrayList<>();
    private boolean accountsShowed;
    private ArrayList<Item> items = new ArrayList<>(11);
    private Context mContext;
    private DrawerProfileCell profileCell;

    public DrawerLayoutAdapter(Context context) {
        this.mContext = context;
        boolean z = true;
        this.accountsShowed = (UserConfig.getActivatedAccountsCount() <= 1 || !MessagesController.getGlobalMainSettings().getBoolean("accountsShowed", true)) ? false : z;
        Theme.createDialogsResources(context);
        resetItems();
    }

    private int getAccountRowsCount() {
        int count = this.accountNumbers.size() + 1;
        if (this.accountNumbers.size() < 3) {
            return count + 1;
        }
        return count;
    }

    public int getItemCount() {
        int count = this.items.size() + 2;
        if (this.accountsShowed) {
            return count + getAccountRowsCount();
        }
        return count;
    }

    public void setAccountsShowed(boolean value, boolean animated) {
        if (this.accountsShowed != value) {
            this.accountsShowed = value;
            DrawerProfileCell drawerProfileCell = this.profileCell;
            if (drawerProfileCell != null) {
                drawerProfileCell.setAccountsShowed(value);
            }
            MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShowed", this.accountsShowed).commit();
            if (!animated) {
                notifyDataSetChanged();
            } else if (this.accountsShowed) {
                notifyItemRangeInserted(2, getAccountRowsCount());
            } else {
                notifyItemRangeRemoved(2, getAccountRowsCount());
            }
        }
    }

    public boolean isAccountsShowed() {
        return this.accountsShowed;
    }

    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int itemType = holder.getItemViewType();
        return itemType == 3 || itemType == 4 || itemType == 5;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            DrawerProfileCell drawerProfileCell = new DrawerProfileCell(this.mContext);
            this.profileCell = drawerProfileCell;
            drawerProfileCell.setOnArrowClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    DrawerLayoutAdapter.this.lambda$onCreateViewHolder$0$DrawerLayoutAdapter(view);
                }
            });
            view = this.profileCell;
        } else if (viewType == 2) {
            view = new DividerCell(this.mContext);
        } else if (viewType == 3) {
            view = new DrawerActionCell(this.mContext);
        } else if (viewType == 4) {
            view = new DrawerUserCell(this.mContext);
        } else if (viewType != 5) {
            view = new EmptyCell(this.mContext, AndroidUtilities.dp(8.0f));
        } else {
            view = new DrawerAddCell(this.mContext);
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(view);
    }

    public /* synthetic */ void lambda$onCreateViewHolder$0$DrawerLayoutAdapter(View v) {
        setAccountsShowed(((DrawerProfileCell) v).isAccountsShowed(), true);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 0) {
            ((DrawerProfileCell) holder.itemView).setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShowed);
        } else if (itemViewType == 3) {
            int position2 = position - 2;
            if (this.accountsShowed) {
                position2 -= getAccountRowsCount();
            }
            DrawerActionCell drawerActionCell = (DrawerActionCell) holder.itemView;
            this.items.get(position2).bind(drawerActionCell);
            drawerActionCell.setPadding(0, 0, 0, 0);
        } else if (itemViewType == 4) {
            ((DrawerUserCell) holder.itemView).setAccount(this.accountNumbers.get(position - 2).intValue());
        }
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        int i2 = i - 2;
        if (this.accountsShowed) {
            if (i2 < this.accountNumbers.size()) {
                return 4;
            }
            if (this.accountNumbers.size() < 3) {
                if (i2 == this.accountNumbers.size()) {
                    return 5;
                }
                if (i2 == this.accountNumbers.size() + 1) {
                    return 2;
                }
            } else if (i2 == this.accountNumbers.size()) {
                return 2;
            }
            i2 -= getAccountRowsCount();
        }
        return i2 == 3 ? 2 : 3;
    }

    private void resetItems() {
        this.accountNumbers.clear();
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                this.accountNumbers.add(Integer.valueOf(a));
            }
        }
        Collections.sort(this.accountNumbers, $$Lambda$DrawerLayoutAdapter$bRu1zfisdWqkhdPxQF6rf8uB3w.INSTANCE);
        this.items.clear();
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            if (Theme.getEventType() == 0) {
                this.items.add(new Item(2, LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_groups_ny));
                this.items.add(new Item(3, LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret_ny));
                this.items.add(new Item(4, LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_channel_ny));
                this.items.add((Object) null);
                this.items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), R.drawable.menu_contacts_ny));
                this.items.add(new Item(11, LocaleController.getString("SavedMessages", R.string.SavedMessages), R.drawable.menu_bookmarks_ny));
                this.items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), R.drawable.menu_calls_ny));
                this.items.add(new Item(7, LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite_ny));
                this.items.add(new Item(8, LocaleController.getString("Settings", R.string.Settings), R.drawable.menu_settings_ny));
                this.items.add(new Item(9, LocaleController.getString("AppFaq", R.string.AppFaq), R.drawable.menu_help_ny));
                return;
            }
            this.items.add(new Item(2, LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_groups));
            this.items.add(new Item(3, LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret));
            this.items.add(new Item(4, LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast));
            this.items.add((Object) null);
            this.items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), R.drawable.menu_contacts));
            this.items.add(new Item(11, LocaleController.getString("SavedMessages", R.string.SavedMessages), R.drawable.menu_saved));
            this.items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), R.drawable.menu_calls));
            this.items.add(new Item(7, LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite));
            this.items.add(new Item(8, LocaleController.getString("Settings", R.string.Settings), R.drawable.menu_settings));
            this.items.add(new Item(9, LocaleController.getString("AppFaq", R.string.AppFaq), R.drawable.menu_help));
        }
    }

    static /* synthetic */ int lambda$resetItems$1(Integer o1, Integer o2) {
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

    public int getId(int position) {
        Item item;
        int position2 = position - 2;
        if (this.accountsShowed) {
            position2 -= getAccountRowsCount();
        }
        if (position2 < 0 || position2 >= this.items.size() || (item = this.items.get(position2)) == null) {
            return -1;
        }
        return item.id;
    }

    private class Item {
        public int icon;
        public int id;
        public String text;

        public Item(int id2, String text2, int icon2) {
            this.icon = icon2;
            this.id = id2;
            this.text = text2;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(this.text, this.icon);
        }
    }
}
