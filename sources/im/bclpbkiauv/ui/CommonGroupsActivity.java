package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.cells.ProfileSearchCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;

public class CommonGroupsActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    private boolean firstLoaded;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loading;
    private int userId;

    public CommonGroupsActivity(int uid) {
        this.userId = uid;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getChats(0, 50);
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupsInCommonTitle", R.string.GroupsInCommonTitle));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CommonGroupsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoGroupsInCommon", R.string.NoGroupsInCommon));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        RecyclerListView recyclerListView4 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView4.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CommonGroupsActivity.this.lambda$createView$0$CommonGroupsActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = CommonGroupsActivity.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(CommonGroupsActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (visibleItemCount > 0) {
                    int totalItemCount = CommonGroupsActivity.this.listViewAdapter.getItemCount();
                    if (!CommonGroupsActivity.this.endReached && !CommonGroupsActivity.this.loading && !CommonGroupsActivity.this.chats.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                        CommonGroupsActivity commonGroupsActivity = CommonGroupsActivity.this;
                        commonGroupsActivity.getChats(((TLRPC.Chat) commonGroupsActivity.chats.get(CommonGroupsActivity.this.chats.size() - 1)).id, 100);
                    }
                }
            }
        });
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$CommonGroupsActivity(View view, int position) {
        if (position >= 0 && position < this.chats.size()) {
            Bundle args = new Bundle();
            args.putInt("chat_id", this.chats.get(position).id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args), true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void getChats(int max_id, int count) {
        if (!this.loading) {
            this.loading = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null && !this.firstLoaded) {
                emptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            TLRPC.TL_messages_getCommonChats req = new TLRPC.TL_messages_getCommonChats();
            req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.userId);
            if (!(req.user_id instanceof TLRPC.TL_inputUserEmpty)) {
                req.limit = count;
                req.max_id = max_id;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(count) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        CommonGroupsActivity.this.lambda$getChats$2$CommonGroupsActivity(this.f$1, tLObject, tL_error);
                    }
                }), this.classGuid);
            }
        }
    }

    public /* synthetic */ void lambda$getChats$2$CommonGroupsActivity(int count, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, count) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                CommonGroupsActivity.this.lambda$null$1$CommonGroupsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$CommonGroupsActivity(TLRPC.TL_error error, TLObject response, int count) {
        if (error == null) {
            TLRPC.messages_Chats res = (TLRPC.messages_Chats) response;
            MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
            this.endReached = res.chats.isEmpty() || res.chats.size() != count;
            this.chats.addAll(res.chats);
        } else {
            this.endReached = true;
        }
        this.loading = false;
        this.firstLoaded = true;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showTextView();
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getAdapterPosition() != CommonGroupsActivity.this.chats.size();
        }

        public int getItemCount() {
            int count = CommonGroupsActivity.this.chats.size();
            if (CommonGroupsActivity.this.chats.isEmpty()) {
                return count;
            }
            int count2 = count + 1;
            if (!CommonGroupsActivity.this.endReached) {
                return count2 + 1;
            }
            return count2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new ProfileSearchCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else if (viewType != 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else {
                View view3 = new LoadingCell(this.mContext);
                view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view3;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ProfileSearchCell cell = (ProfileSearchCell) holder.itemView;
                cell.setData((TLRPC.Chat) CommonGroupsActivity.this.chats.get(position), (TLRPC.EncryptedChat) null, (CharSequence) null, (CharSequence) null, false, false);
                boolean z = true;
                if (position == CommonGroupsActivity.this.chats.size() - 1 && CommonGroupsActivity.this.endReached) {
                    z = false;
                }
                cell.useSeparator = z;
                int i = 2;
                if (getItemCount() == (CommonGroupsActivity.this.endReached ? 2 : 3)) {
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (position == 0) {
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else {
                    int itemCount = getItemCount();
                    if (!CommonGroupsActivity.this.endReached) {
                        i = 3;
                    }
                    if (position == itemCount - i) {
                        cell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    }
                }
            }
        }

        public int getItemViewType(int i) {
            if (i < CommonGroupsActivity.this.chats.size()) {
                return 0;
            }
            if (CommonGroupsActivity.this.endReached || i != CommonGroupsActivity.this.chats.size()) {
                return 2;
            }
            return 1;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                CommonGroupsActivity.this.lambda$getThemeDescriptions$3$CommonGroupsActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LoadingCell.class, ProfileSearchCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_progressCircle), new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint, Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_name), new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint, Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_secretName), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$3$CommonGroupsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                }
            }
        }
    }
}
