package im.bclpbkiauv.ui.hui.adapter.grouping;

import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.expand.models.ExpandableGroup;
import java.util.List;

public class Genre extends ExpandableGroup<Artist> {
    private int groupId;
    private int orderId;

    public Genre(TLRPCContacts.TL_contactsGroupInfo groupInfo, List<Artist> items) {
        super(groupInfo.title, items);
        this.groupId = groupInfo.group_id;
        this.orderId = groupInfo.order_id;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getOrderId() {
        return this.orderId;
    }

    public int getOnlineCount() {
        int onlineCount = 0;
        MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        for (Artist artist : getItems()) {
            TLRPC.User user = messagesController.getUser(Integer.valueOf(artist.getUserId()));
            if (user.id == UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId() || ((user.status != null && user.status.expires > ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) || MessagesController.getInstance(UserConfig.selectedAccount).onlinePrivacy.containsKey(Integer.valueOf(user.id)))) {
                onlineCount++;
            }
        }
        return onlineCount;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof Genre) && this.groupId == ((Genre) o).getGroupId()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (getTitle() != null ? getTitle().hashCode() : 0) * 31;
    }
}
