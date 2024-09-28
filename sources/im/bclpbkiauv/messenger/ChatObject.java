package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLRPC;

public class ChatObject {
    public static final int ACTION_ADD_ADMINS = 4;
    public static final int ACTION_BLOCK_USERS = 2;
    public static final int ACTION_CHANGE_INFO = 1;
    public static final int ACTION_DELETE_MESSAGES = 13;
    public static final int ACTION_EDIT_MESSAGES = 12;
    public static final int ACTION_EMBED_LINKS = 9;
    public static final int ACTION_INVITE = 3;
    public static final int ACTION_PIN = 0;
    public static final int ACTION_POST = 5;
    public static final int ACTION_SEND = 6;
    public static final int ACTION_SEND_MEDIA = 7;
    public static final int ACTION_SEND_POLLS = 10;
    public static final int ACTION_SEND_STICKERS = 8;
    public static final int ACTION_VIEW = 11;
    public static final int CHAT_TYPE_CHANNEL = 2;
    public static final int CHAT_TYPE_CHAT = 0;
    public static final int CHAT_TYPE_MEGAGROUP = 4;
    public static final int CHAT_TYPE_USER = 3;

    private static boolean isBannableAction(int action) {
        if (!(action == 0 || action == 1 || action == 3)) {
            switch (action) {
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private static boolean isAdminAction(int action) {
        if (action == 0 || action == 1 || action == 2 || action == 3 || action == 4 || action == 5 || action == 12 || action == 13) {
            return true;
        }
        return false;
    }

    private static boolean getBannedRight(TLRPC.TL_chatBannedRights rights, int action) {
        if (rights == null) {
            return false;
        }
        if (action == 0) {
            return rights.pin_messages;
        }
        if (action == 1) {
            return rights.change_info;
        }
        if (action == 3) {
            return rights.invite_users;
        }
        switch (action) {
            case 6:
                return rights.send_messages;
            case 7:
                return rights.send_media;
            case 8:
                return rights.send_stickers;
            case 9:
                return rights.embed_links;
            case 10:
                return rights.send_polls;
            case 11:
                return rights.view_messages;
            default:
                return false;
        }
    }

    public static boolean isActionBannedByDefault(TLRPC.Chat chat, int action) {
        if (getBannedRight(chat.banned_rights, action)) {
            return false;
        }
        return getBannedRight(chat.default_banned_rights, action);
    }

    public static boolean canUserDoAdminAction(TLRPC.Chat chat, int action) {
        boolean value;
        if (chat == null) {
            return false;
        }
        if (chat.creator) {
            return true;
        }
        if (chat.admin_rights != null) {
            if (action == 0) {
                value = chat.admin_rights.pin_messages;
            } else if (action == 1) {
                value = chat.admin_rights.change_info;
            } else if (action == 2) {
                value = chat.admin_rights.ban_users;
            } else if (action == 3) {
                value = chat.admin_rights.invite_users;
            } else if (action == 4) {
                value = chat.admin_rights.add_admins;
            } else if (action == 5) {
                value = chat.admin_rights.post_messages;
            } else if (action == 12) {
                value = chat.admin_rights.edit_messages;
            } else if (action != 13) {
                value = false;
            } else {
                value = chat.admin_rights.delete_messages;
            }
            if (value) {
                return true;
            }
        }
        return false;
    }

    public static boolean canUserDoAction(TLRPC.Chat chat, int action) {
        if (chat == null || canUserDoAdminAction(chat, action)) {
            return true;
        }
        if (getBannedRight(chat.banned_rights, action) || !isBannableAction(action)) {
            return false;
        }
        if (chat.admin_rights != null && hasAdminRights(chat) && !isAdminAction(action)) {
            return true;
        }
        if (chat.default_banned_rights == null && ((chat instanceof TLRPC.TL_chat_layer92) || (chat instanceof TLRPC.TL_chat_old) || (chat instanceof TLRPC.TL_chat_old2) || (chat instanceof TLRPC.TL_channel_layer92) || (chat instanceof TLRPC.TL_channel_layer77) || (chat instanceof TLRPC.TL_channel_layer72) || (chat instanceof TLRPC.TL_channel_layer67) || (chat instanceof TLRPC.TL_channel_layer48) || (chat instanceof TLRPC.TL_channel_old))) {
            return true;
        }
        if (chat.default_banned_rights == null || getBannedRight(chat.default_banned_rights, action)) {
            return false;
        }
        return true;
    }

    public static boolean isLeftFromChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.left || chat.deactivated;
    }

    public static boolean isKickedFromChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.kicked || chat.deactivated || (chat.banned_rights != null && chat.banned_rights.view_messages);
    }

    public static boolean isNotInChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.left || chat.kicked || chat.deactivated;
    }

    public static boolean isChannel(int chatId, int currentAccount) {
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(chatId));
        return (chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden);
    }

    public static boolean isChannel(TLRPC.Chat chat) {
        return (chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden);
    }

    public static boolean isMegagroup(TLRPC.Chat chat) {
        return ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) && chat.megagroup;
    }

    public static boolean hasAdminRights(TLRPC.Chat chat) {
        if (chat != null) {
            if (chat.creator) {
                return true;
            }
            if (chat.megagroup) {
                if (chat.admin_rights == null || (chat.admin_rights.flags & 542) == 0) {
                    return false;
                }
                return true;
            } else if (!isChannel(chat) || chat.admin_rights == null || chat.admin_rights.flags == 0 || chat.admin_rights.flags == 64) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean canChangeChatInfo(TLRPC.Chat chat) {
        return canUserDoAction(chat, 1);
    }

    public static boolean canAddAdmins(TLRPC.Chat chat) {
        return canUserDoAction(chat, 4);
    }

    public static boolean canBlockUsers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 2);
    }

    public static boolean canSendStickers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 8);
    }

    public static boolean canSendEmbed(TLRPC.Chat chat) {
        return canUserDoAction(chat, 9);
    }

    public static boolean canSendMedia(TLRPC.Chat chat) {
        return canUserDoAction(chat, 7);
    }

    public static boolean canSendPolls(TLRPC.Chat chat) {
        return canUserDoAction(chat, 10);
    }

    public static boolean canSendMessages(TLRPC.Chat chat) {
        return canUserDoAction(chat, 6);
    }

    public static boolean canPost(TLRPC.Chat chat) {
        return canUserDoAction(chat, 5);
    }

    public static boolean canAddUsers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 3);
    }

    public static boolean canPinMessages(TLRPC.Chat chat) {
        return canUserDoAction(chat, 0) || (isChannel(chat) && !chat.megagroup && chat.admin_rights != null && chat.admin_rights.edit_messages);
    }

    public static boolean isCanWriteToChannel(int chatId, int currentAccount) {
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(chatId));
        return canSendMessages(chat) || (chat != null && chat.megagroup);
    }

    public static boolean canWriteToChat(TLRPC.Chat chat) {
        return !isChannel(chat) || chat.creator || (chat.admin_rights != null && chat.admin_rights.post_messages) || !chat.broadcast;
    }

    public static String getBannedRightsString(TLRPC.TL_chatBannedRights bannedRights) {
        return (((((((((((("" + (bannedRights.view_messages ? 1 : 0)) + (bannedRights.send_messages ? 1 : 0)) + (bannedRights.send_media ? 1 : 0)) + (bannedRights.send_stickers ? 1 : 0)) + (bannedRights.send_gifs ? 1 : 0)) + (bannedRights.send_games ? 1 : 0)) + (bannedRights.send_inline ? 1 : 0)) + (bannedRights.embed_links ? 1 : 0)) + (bannedRights.send_polls ? 1 : 0)) + (bannedRights.invite_users ? 1 : 0)) + (bannedRights.change_info ? 1 : 0)) + (bannedRights.pin_messages ? 1 : 0)) + bannedRights.until_date;
    }

    public static TLRPC.Chat getChatByDialog(long did, int currentAccount) {
        int lower_id = (int) did;
        int i = (int) (did >> 32);
        if (lower_id < 0) {
            return MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(-lower_id));
        }
        return null;
    }
}
