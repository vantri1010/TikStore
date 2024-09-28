package im.bclpbkiauv.ui.hui.adapter.grouping;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.expand.models.ExpandableGroup;
import im.bclpbkiauv.ui.expand.viewholders.ChildViewHolder;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.slidemenu.SwipeLayout;
import java.util.List;

public class ArtistViewHolder extends ChildViewHolder {
    private GenreAdapter adapter;
    private Artist artist;
    private Genre genre;
    private BackupImageView ivAvatar;
    private MryTextView tvName;
    private MryTextView tvStatus;
    private TLRPC.User user;

    public ArtistViewHolder(View itemView) {
        super(itemView);
        SwipeLayout swipeLayout = (SwipeLayout) itemView;
        swipeLayout.setItemWidth(AndroidUtilities.dp(86.0f));
        View content = swipeLayout.getMainLayout();
        this.ivAvatar = (BackupImageView) content.findViewById(R.id.iv_item_artist_avatar);
        this.tvName = (MryTextView) content.findViewById(R.id.list_item_artist_name);
        this.tvStatus = (MryTextView) content.findViewById(R.id.list_item_artist_status);
        swipeLayout.setRightTexts(LocaleController.getString(R.string.RemoveFromGrouping));
        swipeLayout.setRightTextColors(-1);
        swipeLayout.setRightColors(-570319);
        swipeLayout.setTextSize(AndroidUtilities.sp2px(14.0f));
        swipeLayout.rebuildLayout();
        swipeLayout.setOnSwipeItemClickListener(new SwipeLayout.OnSwipeItemClickListener() {
            public final void onSwipeItemClick(boolean z, int i) {
                ArtistViewHolder.this.lambda$new$3$ArtistViewHolder(z, i);
            }
        });
        content.setOnClickListener(new View.OnClickListener(swipeLayout) {
            private final /* synthetic */ SwipeLayout f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ArtistViewHolder.this.lambda$new$4$ArtistViewHolder(this.f$1, view);
            }
        });
    }

    public /* synthetic */ void lambda$new$3$ArtistViewHolder(boolean left, int index) {
        if (!left && index == 0 && this.genre.getGroupId() != 0) {
            ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
            AlertDialog alertDialog = new AlertDialog(this.adapter.getActivity().getParentActivity(), 3);
            TLRPCContacts.TL_setUserGroup req = new TLRPCContacts.TL_setUserGroup();
            req.group_id = 0;
            TLRPCContacts.TL_inputPeerUserChange inputPeer = new TLRPCContacts.TL_inputPeerUserChange();
            inputPeer.access_hash = this.user.access_hash;
            inputPeer.user_id = this.user.id;
            inputPeer.fist_name = this.user.first_name;
            req.users.add(inputPeer);
            int reqId = connectionsManager.sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject) {
                        private final /* synthetic */ TLRPC.TL_error f$1;
                        private final /* synthetic */ TLObject f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            ArtistViewHolder.lambda$null$0(AlertDialog.this, this.f$1, this.f$2);
                        }
                    });
                }
            });
            connectionsManager.bindRequestToGuid(reqId, this.adapter.getActivity().getClassGuid());
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(reqId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onDismiss(DialogInterface dialogInterface) {
                    ConnectionsManager.this.cancelRequest(this.f$1, true);
                }
            });
            this.adapter.getActivity().showDialog(alertDialog);
        }
    }

    static /* synthetic */ void lambda$null$0(AlertDialog alertDialog, TLRPC.TL_error error, TLObject response) {
        alertDialog.dismiss();
        if (error != null) {
            ToastUtils.show((CharSequence) error.text);
        } else if (!(response instanceof TLRPC.TL_boolTrue)) {
            ToastUtils.show((CharSequence) "移出失败，请稍后重试");
        }
    }

    public /* synthetic */ void lambda$new$4$ArtistViewHolder(SwipeLayout swipeLayout, View v) {
        if (swipeLayout.isExpanded()) {
            swipeLayout.collapseAll(true);
        } else if (this.user != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", this.user.id);
            this.adapter.getActivity().presentFragment(new NewProfileActivity(bundle));
        }
    }

    public void setUserData(Artist artist2, Genre genre2, GenreAdapter genreAdapter) {
        this.artist = artist2;
        this.genre = genre2;
        this.adapter = genreAdapter;
        initData();
    }

    private void initData() {
        ((SwipeLayout) this.itemView).setSwipeEnabled(this.genre.getGroupId() != 0);
        List<? extends ExpandableGroup> groups = this.adapter.getGroups();
        if (this.genre.getGroupId() == ((Genre) groups.get(groups.size() - 1)).getGroupId()) {
            List<Artist> artists = this.genre.getItems();
            if (this.artist.getUserId() == artists.get(artists.size() - 1).getUserId()) {
                this.itemView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
            }
        } else {
            this.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        TLRPC.User user2 = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(this.artist.getUserId()));
        this.user = user2;
        if (user2 != null) {
            AvatarDrawable avatarDrawable = new AvatarDrawable(this.user);
            this.ivAvatar.setRoundRadius(AndroidUtilities.dp(7.5f));
            this.ivAvatar.setImage(ImageLocation.getForUser(this.user, false), "50_50", (Drawable) avatarDrawable, (Object) this.user);
            this.tvName.setText(UserObject.getName(this.user));
            if (this.user.id == UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId() || ((this.user.status != null && this.user.status.expires > ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) || MessagesController.getInstance(UserConfig.selectedAccount).onlinePrivacy.containsKey(Integer.valueOf(this.user.id)))) {
                this.tvStatus.setTextColor(Theme.getColor(Theme.key_color_42B71E));
                this.tvStatus.setText(LocaleController.getString("Online", R.string.Online));
                return;
            }
            this.tvStatus.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            this.tvStatus.setText(LocaleController.formatUserStatus(UserConfig.selectedAccount, this.user));
        }
    }
}
