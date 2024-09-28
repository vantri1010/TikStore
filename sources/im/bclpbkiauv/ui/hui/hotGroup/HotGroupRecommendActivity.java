package im.bclpbkiauv.ui.hui.hotGroup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCHotChannel;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hui.chats.CreateGroupActivity;
import im.bclpbkiauv.ui.hui.decoration.TopBottomDecoration;
import im.bclpbkiauv.ui.hui.hotGroup.HotGroupRecommendActivity;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HotGroupRecommendActivity extends BaseFragment {
    private PageSelectionAdapter<Item, PageHolder> adapter;
    private RecyclerListView rv;

    public View createView(Context context) {
        initActionBar();
        initView(context);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.HotChannelRecommend));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.createMenu().addItem(1, (int) R.drawable.groups_create);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    HotGroupRecommendActivity.this.finishFragment();
                    return;
                }
                HotGroupRecommendActivity.this.presentFragment(new CreateGroupActivity(new Bundle()));
            }
        });
    }

    private void initView(Context context) {
        FrameLayout root = new FrameLayout(context);
        root.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f));
        this.fragmentView = root;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.rv = recyclerListView;
        root.addView(recyclerListView, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 10));
        this.rv.setLayoutManager(new LinearLayoutManager(context));
        this.rv.addItemDecoration(TopBottomDecoration.getDefaultTopBottomCornerBg(10, 10, 8.0f));
        AnonymousClass2 r1 = new PageSelectionAdapter<Item, PageHolder>(context) {
            public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
                return new PageHolder(LayoutInflater.from(getContext()).inflate(R.layout.hot_group_item_recommend_list, parent, false), 0);
            }

            public void onBindViewHolderForChild(PageHolder holder, int position, Item item) {
                boolean z;
                int i;
                PageHolder pageHolder = holder;
                Item item2 = item;
                BackupImageView ivAvatar = (BackupImageView) pageHolder.getView(R.id.ivAvatar);
                MryTextView tvTitle = (MryTextView) pageHolder.getView(R.id.tvTitle);
                MryTextView tvDescription = (MryTextView) pageHolder.getView(R.id.tvDescription);
                MryRoundButton tvTag = (MryRoundButton) pageHolder.getView(R.id.tvTag);
                MryRoundButton btn = (MryRoundButton) pageHolder.getView(R.id.btn);
                MryTextView tvCount = (MryTextView) pageHolder.getView(R.id.tvCount);
                View divider = pageHolder.getView(R.id.divider);
                tvCount.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                ivAvatar.setRoundRadius(AndroidUtilities.dp(5.0f));
                ivAvatar.setImageResource(R.drawable.bg_comment_grey_line);
                boolean isInThisGroup = false;
                if (item2.chat != null) {
                    BackupImageView backupImageView = ivAvatar;
                    z = false;
                    ivAvatar.setImage(ImageLocation.getForChat(item2.chat, false), "50_50", "", (Drawable) new AvatarDrawable(), (Object) item2.chat);
                    tvTitle.setText(item2.chat.title);
                    TLRPC.Chat targetChat = HotGroupRecommendActivity.this.getMessagesController().getChat(Integer.valueOf(item2.chat.id));
                    isInThisGroup = targetChat != null && !targetChat.left;
                    tvCount.setText(String.valueOf(item2.chat.participants_count));
                } else {
                    z = false;
                }
                if (isInThisGroup) {
                    btn.setPrimaryRadiusAdjustBoundsStrokeStyle();
                    btn.setStrokeColors(ColorStateList.valueOf(-2250382));
                    btn.setTextColor(-2250382);
                    btn.setText(LocaleController.getString(R.string.EnterGroup));
                } else {
                    btn.setBackgroundResource(R.mipmap.hot_group_list_btn);
                    btn.setTextColor(-1);
                    btn.setText(LocaleController.getString(R.string.JoinNow));
                }
                if (item2.groupAbout != null) {
                    if (!TextUtils.isEmpty(item2.groupAbout.about)) {
                        pageHolder.setGone((View) tvDescription, z);
                        tvDescription.setText(item2.groupAbout.about);
                    } else {
                        pageHolder.setGone((View) tvDescription, true);
                    }
                    if (!TextUtils.isEmpty(item2.groupAbout.groupType)) {
                        pageHolder.setGone((View) tvTag, z);
                        tvTag.setPrimaryRoundFillStyle((float) AndroidUtilities.dp(5.0f));
                        tvTag.setBackgroundColor(282962290);
                        tvTag.setTextColor(-2250382);
                        i = 1;
                        tvTag.setStrokeData(1, -2250382);
                        tvTag.setText(item2.groupAbout.groupType);
                    } else {
                        i = 1;
                        pageHolder.setGone((View) tvTag, true);
                    }
                } else {
                    i = 1;
                    pageHolder.setGone((View) tvTag, true);
                    pageHolder.setGone((View) tvDescription, true);
                }
                btn.setOnClickListener(new View.OnClickListener(item2) {
                    private final /* synthetic */ HotGroupRecommendActivity.Item f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        HotGroupRecommendActivity.AnonymousClass2.this.lambda$onBindViewHolderForChild$0$HotGroupRecommendActivity$2(this.f$1, view);
                    }
                });
                pageHolder.setGone(divider, position == getDataCount() - i);
            }

            public /* synthetic */ void lambda$onBindViewHolderForChild$0$HotGroupRecommendActivity$2(Item item, View v) {
                HotGroupRecommendActivity.this.clickBtn(item.chat);
            }

            public void loadData(int page) {
                HotGroupRecommendActivity.this.getData(page);
            }
        };
        this.adapter = r1;
        this.rv.setAdapter(r1);
        this.adapter.emptyAttachView(root);
        this.adapter.showLoading();
        getData(this.adapter.getStartPage());
    }

    /* access modifiers changed from: private */
    public void getData(int page) {
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(new TLRPCHotChannel.TL_GetHotGroups(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                HotGroupRecommendActivity.this.lambda$getData$1$HotGroupRecommendActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getData$1$HotGroupRecommendActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                HotGroupRecommendActivity.this.lambda$null$0$HotGroupRecommendActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$HotGroupRecommendActivity(TLRPC.TL_error error, TLObject response) {
        if (error != null || !(response instanceof TLRPCHotChannel.TL_HotGroups)) {
            ToastUtils.show((int) R.string.NetworkError);
            PageSelectionAdapter<Item, PageHolder> pageSelectionAdapter = this.adapter;
            if (pageSelectionAdapter != null) {
                pageSelectionAdapter.showError(LocaleController.getString(R.string.NetworkError));
                return;
            }
            return;
        }
        TLRPCHotChannel.TL_HotGroups res = (TLRPCHotChannel.TL_HotGroups) response;
        List<Item> data = new ArrayList<>();
        Iterator<TLRPCHotChannel.TL_HotGroupAbout> it = res.getPeers().iterator();
        while (it.hasNext()) {
            data.add(new Item(it.next()));
        }
        for (int i = 0; i < res.getChats().size(); i++) {
            if (i < data.size()) {
                data.get(i).chat = res.getChats().get(i);
            }
        }
        PageSelectionAdapter<Item, PageHolder> pageSelectionAdapter2 = this.adapter;
        if (pageSelectionAdapter2 != null) {
            pageSelectionAdapter2.addData(data);
        }
    }

    /* access modifiers changed from: private */
    public void clickBtn(TLRPC.Chat chat) {
        if (chat != null) {
            TLRPC.Chat targetChat = getMessagesController().getChat(Integer.valueOf(chat.id));
            if (targetChat != null) {
                if (targetChat.kicked) {
                    WalletDialogUtil.showConfirmBtnWalletDialog(this, LocaleController.getString(R.string.CannotJoinGroupWhenKickedOut));
                    return;
                } else if (!targetChat.left) {
                    Bundle args = new Bundle();
                    args.putInt("chat_id", targetChat.id);
                    presentFragment(new ChatActivity(args));
                    return;
                }
            }
            joinChannel(chat);
        }
    }

    private void joinChannel(TLRPC.Chat channel) {
        TLRPC.TL_channels_joinChannel req = new TLRPC.TL_channels_joinChannel();
        req.channel = MessagesController.getInputChannel(channel);
        int currentAccount = UserConfig.selectedAccount;
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                HotGroupRecommendActivity.this.lambda$joinChannel$2$HotGroupRecommendActivity(dialogInterface);
            }
        });
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate(progressDialog, currentAccount, channel) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ TLRPC.Chat f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                HotGroupRecommendActivity.this.lambda$joinChannel$6$HotGroupRecommendActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        })) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                HotGroupRecommendActivity.this.lambda$joinChannel$7$HotGroupRecommendActivity(this.f$1, dialogInterface);
            }
        });
    }

    public /* synthetic */ void lambda$joinChannel$2$HotGroupRecommendActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
    }

    public /* synthetic */ void lambda$joinChannel$6$HotGroupRecommendActivity(AlertDialog progressDialog, int currentAccount, TLRPC.Chat channel, TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    HotGroupRecommendActivity.this.lambda$null$3$HotGroupRecommendActivity(this.f$1);
                }
            });
            return;
        }
        boolean hasJoinMessage = false;
        TLRPC.Updates updates = (TLRPC.Updates) response;
        int a = 0;
        while (true) {
            if (a >= updates.updates.size()) {
                break;
            }
            TLRPC.Update update = updates.updates.get(a);
            if ((update instanceof TLRPC.TL_updateNewChannelMessage) && (((TLRPC.TL_updateNewChannelMessage) update).message.action instanceof TLRPC.TL_messageActionChatAddUser)) {
                hasJoinMessage = true;
                break;
            }
            a++;
        }
        MessagesController.getInstance(currentAccount).processUpdates(updates, false);
        if (!hasJoinMessage) {
            MessagesController.getInstance(currentAccount).generateJoinMessage(channel.id, true);
        }
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, currentAccount, channel) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ TLRPC.Chat f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                HotGroupRecommendActivity.this.lambda$null$5$HotGroupRecommendActivity(this.f$1, this.f$2, this.f$3);
            }
        }, 1000);
        MessagesStorage.getInstance(currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), (ArrayList<Long>) null, true, channel.id);
    }

    public /* synthetic */ void lambda$null$3$HotGroupRecommendActivity(AlertDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        WalletDialogUtil.showConfirmBtnWalletDialog(this, LocaleController.getString("Tips", R.string.Tips), LocaleController.getString(R.string.discovery_join_group_error), false, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
    }

    public /* synthetic */ void lambda$null$5$HotGroupRecommendActivity(AlertDialog progressDialog, int currentAccount, TLRPC.Chat channel) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        MessagesController.getInstance(currentAccount).loadFullChat(channel.id, 0, true);
        WalletDialogUtil.showSingleBtnWalletDialog(this, LocaleController.getString("Tips", R.string.Tips), LocaleController.getString(R.string.discovery_join_group_success), LocaleController.getString(R.string.OK), false, new DialogInterface.OnClickListener(channel) {
            private final /* synthetic */ TLRPC.Chat f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                HotGroupRecommendActivity.this.lambda$null$4$HotGroupRecommendActivity(this.f$1, dialogInterface, i);
            }
        }, (DialogInterface.OnDismissListener) null);
        getData(this.adapter.getStartPage());
    }

    public /* synthetic */ void lambda$null$4$HotGroupRecommendActivity(TLRPC.Chat channel, DialogInterface dialog, int which) {
        channel.left = false;
        Bundle args = new Bundle();
        args.putInt("chat_id", channel.id);
        presentFragment(new ChatActivity(args));
    }

    public /* synthetic */ void lambda$joinChannel$7$HotGroupRecommendActivity(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.rv = null;
        PageSelectionAdapter<Item, PageHolder> pageSelectionAdapter = this.adapter;
        if (pageSelectionAdapter != null) {
            pageSelectionAdapter.destroy();
            this.adapter = null;
        }
    }

    private static class Item {
        TLRPC.Chat chat;
        TLRPCHotChannel.TL_HotGroupAbout groupAbout;

        Item(TLRPCHotChannel.TL_HotGroupAbout groupAbout2) {
            this.groupAbout = groupAbout2;
        }
    }
}
