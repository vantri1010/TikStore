package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLRPC;

public class DialogObject {
    public static boolean isChannel(TLRPC.Dialog dialog) {
        return (dialog == null || (dialog.flags & 1) == 0) ? false : true;
    }

    public static long makeSecretDialogId(int chatId) {
        return ((long) chatId) << 32;
    }

    public static long makeFolderDialogId(int folderId) {
        return ((long) folderId) | 8589934592L;
    }

    public static boolean isFolderDialogId(long dialogId) {
        return ((int) dialogId) != 0 && ((int) (dialogId >> 32)) == 2;
    }

    public static boolean isPeerDialogId(long dialogId) {
        int highId = (int) (dialogId >> 32);
        return (((int) dialogId) == 0 || highId == 2 || highId == 1) ? false : true;
    }

    public static boolean isSecretDialogId(long dialogId) {
        return ((int) dialogId) == 0;
    }

    public static void initDialog(TLRPC.Dialog dialog) {
        if (dialog != null && dialog.id == 0) {
            if (dialog instanceof TLRPC.TL_dialog) {
                if (dialog.peer != null) {
                    if (dialog.peer.user_id != 0) {
                        dialog.id = (long) dialog.peer.user_id;
                    } else if (dialog.peer.chat_id != 0) {
                        dialog.id = (long) (-dialog.peer.chat_id);
                    } else {
                        dialog.id = (long) (-dialog.peer.channel_id);
                    }
                }
            } else if (dialog instanceof TLRPC.TL_dialogFolder) {
                dialog.id = makeFolderDialogId(((TLRPC.TL_dialogFolder) dialog).folder.id);
            }
        }
    }

    public static long getPeerDialogId(TLRPC.Peer peer) {
        if (peer == null) {
            return 0;
        }
        if (peer.user_id != 0) {
            return (long) peer.user_id;
        }
        if (peer.chat_id != 0) {
            return (long) (-peer.chat_id);
        }
        return (long) (-peer.channel_id);
    }

    public static long getPeerDialogId(TLRPC.InputPeer peer) {
        if (peer == null) {
            return 0;
        }
        if (peer.user_id != 0) {
            return (long) peer.user_id;
        }
        if (peer.chat_id != 0) {
            return (long) (-peer.chat_id);
        }
        return (long) (-peer.channel_id);
    }
}
