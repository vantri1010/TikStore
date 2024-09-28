package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.login.LoginContronllerActivity;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Collections;

public class AddAccountActivity extends BaseFragment {
    private ListAdapter listAdapter;
    private RecyclerListView listView;

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_add_account, (ViewGroup) null);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initList();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString(R.string.AddAccount2));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    AddAccountActivity.this.finishFragment();
                }
            }
        });
    }

    private void initList() {
        ((MryTextView) this.fragmentView.findViewById(R.id.tv_action_tips)).setText(LocaleController.getString("ActionTips", R.string.TouchTips));
        RecyclerListView recyclerListView = (RecyclerListView) this.fragmentView.findViewById(R.id.listView);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(getParentActivity(), 1);
        divider.setDrawable(getParentActivity().getResources().getDrawable(R.drawable.shape_transaction_list_divider));
        this.listView.addItemDecoration(divider);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter();
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                AddAccountActivity.this.lambda$initList$0$AddAccountActivity(view, i);
            }
        });
    }

    public /* synthetic */ void lambda$initList$0$AddAccountActivity(View view, int position) {
        if (position != this.listAdapter.getItemCount() - 1) {
            int accountNumber = this.listAdapter.getAccountNumber(position);
            if (accountNumber != -1) {
                ((LaunchActivity) getParentActivity()).switchToAccount(accountNumber, true);
            }
        } else if (ApplicationLoader.mbytAVideoCallBusy == 0) {
            int freeAccount = -1;
            int a = 0;
            while (true) {
                if (a >= 3) {
                    break;
                } else if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                } else {
                    a++;
                }
            }
            if (freeAccount >= 0) {
                presentFragment(new LoginContronllerActivity(freeAccount), true);
            }
        } else {
            ToastUtils.show((CharSequence) LocaleController.getString(R.string.visual_call_stop_add_account));
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<Integer> accountNumbers = new ArrayList<>();

        public ListAdapter() {
            resetItems();
        }

        private void resetItems() {
            this.accountNumbers.clear();
            for (int a = 0; a < 3; a++) {
                if (UserConfig.getInstance(a).isClientActivated()) {
                    this.accountNumbers.add(Integer.valueOf(a));
                }
            }
            Collections.sort(this.accountNumbers, $$Lambda$AddAccountActivity$ListAdapter$5JhSzankqypK27vQ6ErynjrNe50.INSTANCE);
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = LayoutInflater.from(AddAccountActivity.this.getParentActivity()).inflate(R.layout.item_login_account, parent, false);
            } else if (viewType == 1) {
                view = LayoutInflater.from(AddAccountActivity.this.getParentActivity()).inflate(R.layout.item_add_account, parent, false);
            }
            view.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                BackupImageView ivAvatar = (BackupImageView) holder.itemView.findViewById(R.id.iv_avatar);
                MryTextView tvPhone = (MryTextView) holder.itemView.findViewById(R.id.tv_phone);
                MryTextView tvCurrent = (MryTextView) holder.itemView.findViewById(R.id.tv_current);
                tvCurrent.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(2.0f), -1181185));
                tvCurrent.setText(LocaleController.getString("Currently used", R.string.CurrentUsed));
                TLRPC.User user = UserConfig.getInstance(this.accountNumbers.get(position).intValue()).getCurrentUser();
                if (user != null) {
                    ivAvatar.setRoundRadius(AndroidUtilities.dp(7.5f));
                    AvatarDrawable drawable = new AvatarDrawable();
                    drawable.setInfo(user);
                    int i = 0;
                    ivAvatar.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) drawable, (Object) user);
                    tvPhone.setText(UserObject.getName(user));
                    if (user.id != AddAccountActivity.this.getUserConfig().getClientUserId()) {
                        i = 8;
                    }
                    tvCurrent.setVisibility(i);
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return 1;
            }
            return 0;
        }

        public int getItemCount() {
            return 1 + this.accountNumbers.size();
        }

        public int getAccountNumber(int position) {
            if (position != getItemCount() - 1) {
                return this.accountNumbers.get(position).intValue();
            }
            return -1;
        }
    }
}
