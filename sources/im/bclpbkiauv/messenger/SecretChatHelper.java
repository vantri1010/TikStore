package im.bclpbkiauv.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.LongSparseArray;
import android.util.SparseArray;
import com.google.android.exoplayer2.util.MimeTypes;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.support.SparseLongArray;
import im.bclpbkiauv.sqlite.SQLiteCursor;
import im.bclpbkiauv.tgnet.AbstractSerializedData;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.NativeByteBuffer;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class SecretChatHelper extends BaseController {
    public static final int CURRENT_SECRET_CHAT_LAYER = 101;
    private static volatile SecretChatHelper[] Instance = new SecretChatHelper[3];
    private SparseArray<TLRPC.EncryptedChat> acceptingChats = new SparseArray<>();
    public ArrayList<TLRPC.Update> delayedEncryptedChatUpdates = new ArrayList<>();
    private ArrayList<Long> pendingEncMessagesToDelete = new ArrayList<>();
    private SparseArray<ArrayList<TL_decryptedMessageHolder>> secretHolesQueue = new SparseArray<>();
    private ArrayList<Integer> sendingNotifyLayer = new ArrayList<>();
    private boolean startingSecretChat = false;

    public static class TL_decryptedMessageHolder extends TLObject {
        public static int constructor = 1431655929;
        public int date;
        public int decryptedWithVersion;
        public TLRPC.EncryptedFile file;
        public TLRPC.TL_decryptedMessageLayer layer;
        public boolean new_key_used;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.layer = TLRPC.TL_decryptedMessageLayer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (stream.readBool(exception)) {
                this.file = TLRPC.EncryptedFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            this.new_key_used = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(0);
            stream.writeInt32(this.date);
            this.layer.serializeToStream(stream);
            stream.writeBool(this.file != null);
            TLRPC.EncryptedFile encryptedFile = this.file;
            if (encryptedFile != null) {
                encryptedFile.serializeToStream(stream);
            }
            stream.writeBool(this.new_key_used);
        }
    }

    public static SecretChatHelper getInstance(int num) {
        SecretChatHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (SecretChatHelper.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    SecretChatHelper[] secretChatHelperArr = Instance;
                    SecretChatHelper secretChatHelper = new SecretChatHelper(num);
                    localInstance = secretChatHelper;
                    secretChatHelperArr[num] = secretChatHelper;
                }
            }
        }
        return localInstance;
    }

    public SecretChatHelper(int instance) {
        super(instance);
    }

    public void cleanup() {
        this.sendingNotifyLayer.clear();
        this.acceptingChats.clear();
        this.secretHolesQueue.clear();
        this.delayedEncryptedChatUpdates.clear();
        this.pendingEncMessagesToDelete.clear();
        this.startingSecretChat = false;
    }

    /* access modifiers changed from: protected */
    public void processPendingEncMessages() {
        if (!this.pendingEncMessagesToDelete.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(new ArrayList<>(this.pendingEncMessagesToDelete)) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$processPendingEncMessages$0$SecretChatHelper(this.f$1);
                }
            });
            getMessagesStorage().markMessagesAsDeletedByRandoms(new ArrayList<>(this.pendingEncMessagesToDelete));
            this.pendingEncMessagesToDelete.clear();
        }
    }

    public /* synthetic */ void lambda$processPendingEncMessages$0$SecretChatHelper(ArrayList pendingEncMessagesToDeleteCopy) {
        for (int a = 0; a < pendingEncMessagesToDeleteCopy.size(); a++) {
            MessageObject messageObject = getMessagesController().dialogMessagesByRandomIds.get(((Long) pendingEncMessagesToDeleteCopy.get(a)).longValue());
            if (messageObject != null) {
                messageObject.deleted = true;
            }
        }
    }

    private TLRPC.TL_messageService createServiceSecretMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.DecryptedMessageAction decryptedMessage) {
        TLRPC.TL_messageService newMsg = new TLRPC.TL_messageService();
        newMsg.action = new TLRPC.TL_messageEncryptedAction();
        newMsg.action.encryptedAction = decryptedMessage;
        int newMessageId = getUserConfig().getNewMessageId();
        newMsg.id = newMessageId;
        newMsg.local_id = newMessageId;
        newMsg.from_id = getUserConfig().getClientUserId();
        newMsg.unread = true;
        newMsg.out = true;
        newMsg.flags = 256;
        newMsg.dialog_id = ((long) encryptedChat.id) << 32;
        newMsg.to_id = new TLRPC.TL_peerUser();
        newMsg.send_state = 1;
        if (encryptedChat.participant_id == getUserConfig().getClientUserId()) {
            newMsg.to_id.user_id = encryptedChat.admin_id;
        } else {
            newMsg.to_id.user_id = encryptedChat.participant_id;
        }
        if ((decryptedMessage instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (decryptedMessage instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
            newMsg.date = getConnectionsManager().getCurrentTime();
        } else {
            newMsg.date = 0;
        }
        newMsg.random_id = getSendMessagesHelper().getNextRandomId();
        getUserConfig().saveConfig(false);
        ArrayList<TLRPC.Message> arr = new ArrayList<>();
        arr.add(newMsg);
        getMessagesStorage().putMessages(arr, false, true, true, 0, false);
        return newMsg;
    }

    public void sendMessagesReadMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> random_ids, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionReadMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    /* access modifiers changed from: protected */
    public void processUpdateEncryption(TLRPC.TL_updateEncryption update, ConcurrentHashMap<Integer, TLRPC.User> usersDict) {
        TLRPC.EncryptedChat newChat = update.chat;
        long dialog_id = ((long) newChat.id) << 32;
        TLRPC.EncryptedChat existingChat = getMessagesController().getEncryptedChatDB(newChat.id, false);
        if ((newChat instanceof TLRPC.TL_encryptedChatRequested) && existingChat == null) {
            int user_id = newChat.participant_id;
            if (user_id == getUserConfig().getClientUserId()) {
                user_id = newChat.admin_id;
            }
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(user_id));
            if (user == null) {
                user = usersDict.get(Integer.valueOf(user_id));
            }
            newChat.user_id = user_id;
            TLRPC.Dialog dialog = new TLRPC.TL_dialog();
            dialog.id = dialog_id;
            dialog.unread_count = 0;
            dialog.top_message = 0;
            dialog.last_message_date = update.date;
            getMessagesController().putEncryptedChat(newChat, false);
            AndroidUtilities.runOnUIThread(new Runnable(dialog) {
                private final /* synthetic */ TLRPC.Dialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$processUpdateEncryption$1$SecretChatHelper(this.f$1);
                }
            });
            getMessagesStorage().putEncryptedChat(newChat, user, dialog);
            acceptSecretChat(newChat);
        } else if (!(newChat instanceof TLRPC.TL_encryptedChat)) {
            TLRPC.EncryptedChat exist = existingChat;
            if (exist != null) {
                newChat.user_id = exist.user_id;
                newChat.auth_key = exist.auth_key;
                newChat.key_create_date = exist.key_create_date;
                newChat.key_use_count_in = exist.key_use_count_in;
                newChat.key_use_count_out = exist.key_use_count_out;
                newChat.ttl = exist.ttl;
                newChat.seq_in = exist.seq_in;
                newChat.seq_out = exist.seq_out;
                newChat.admin_id = exist.admin_id;
                newChat.mtproto_seq = exist.mtproto_seq;
            }
            AndroidUtilities.runOnUIThread(new Runnable(exist, newChat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;
                private final /* synthetic */ TLRPC.EncryptedChat f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$processUpdateEncryption$2$SecretChatHelper(this.f$1, this.f$2);
                }
            });
        } else if ((existingChat instanceof TLRPC.TL_encryptedChatWaiting) && (existingChat.auth_key == null || existingChat.auth_key.length == 1)) {
            newChat.a_or_b = existingChat.a_or_b;
            newChat.user_id = existingChat.user_id;
            processAcceptedSecretChat(newChat);
        } else if (existingChat == null && this.startingSecretChat) {
            this.delayedEncryptedChatUpdates.add(update);
        }
    }

    public /* synthetic */ void lambda$processUpdateEncryption$1$SecretChatHelper(TLRPC.Dialog dialog) {
        getMessagesController().dialogs_dict.put(dialog.id, dialog);
        getMessagesController().allDialogs.add(dialog);
        getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdateEncryption$2$SecretChatHelper(TLRPC.EncryptedChat exist, TLRPC.EncryptedChat newChat) {
        if (exist != null) {
            getMessagesController().putEncryptedChat(newChat, false);
        }
        getMessagesStorage().updateEncryptedChat(newChat);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void sendMessagesDeleteMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> random_ids, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionDeleteMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendClearHistoryMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionFlushHistory();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendNotifyLayerMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if ((encryptedChat instanceof TLRPC.TL_encryptedChat) && !this.sendingNotifyLayer.contains(Integer.valueOf(encryptedChat.id))) {
            this.sendingNotifyLayer.add(Integer.valueOf(encryptedChat.id));
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionNotifyLayer();
                reqSend.action.layer = 101;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendRequestKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionRequestKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.g_a = encryptedChat.g_a;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendAcceptKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionAcceptKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                reqSend.action.g_b = encryptedChat.g_a_or_b;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendCommitKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionCommitKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendAbortKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage, long excange_id) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionAbortKey();
                reqSend.action.exchange_id = excange_id;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendNoopMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionNoop();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendTTLMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionSetMessageTTL();
                reqSend.action.ttl_seconds = encryptedChat.ttl;
                TLRPC.Message createServiceSecretMessage = createServiceSecretMessage(encryptedChat, reqSend.action);
                MessageObject newMsgObj = new MessageObject(this.currentAccount, createServiceSecretMessage, false);
                newMsgObj.messageOwner.send_state = 1;
                ArrayList<MessageObject> objArr = new ArrayList<>();
                objArr.add(newMsgObj);
                getMessagesController().updateInterfaceWithMessages(createServiceSecretMessage.dialog_id, objArr, false);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                message = createServiceSecretMessage;
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendScreenshotMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> random_ids, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionScreenshotMessages();
                reqSend.action.random_ids = random_ids;
                TLRPC.Message createServiceSecretMessage = createServiceSecretMessage(encryptedChat, reqSend.action);
                MessageObject newMsgObj = new MessageObject(this.currentAccount, createServiceSecretMessage, false);
                newMsgObj.messageOwner.send_state = 1;
                ArrayList<MessageObject> objArr = new ArrayList<>();
                objArr.add(newMsgObj);
                getMessagesController().updateInterfaceWithMessages(createServiceSecretMessage.dialog_id, objArr, false);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                message = createServiceSecretMessage;
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    private void updateMediaPaths(MessageObject newMsgObj, TLRPC.EncryptedFile file, TLRPC.DecryptedMessage decryptedMessage, String originalPath) {
        MessageObject messageObject = newMsgObj;
        TLRPC.EncryptedFile encryptedFile = file;
        TLRPC.DecryptedMessage decryptedMessage2 = decryptedMessage;
        TLRPC.Message newMsg = messageObject.messageOwner;
        if (encryptedFile == null) {
            return;
        }
        if ((newMsg.media instanceof TLRPC.TL_messageMediaPhoto) && newMsg.media.photo != null) {
            TLRPC.PhotoSize size = newMsg.media.photo.sizes.get(newMsg.media.photo.sizes.size() - 1);
            String fileName = size.location.volume_id + "_" + size.location.local_id;
            size.location = new TLRPC.TL_fileEncryptedLocation();
            size.location.key = decryptedMessage2.media.key;
            size.location.iv = decryptedMessage2.media.iv;
            size.location.dc_id = encryptedFile.dc_id;
            size.location.volume_id = encryptedFile.id;
            size.location.secret = encryptedFile.access_hash;
            size.location.local_id = encryptedFile.key_fingerprint;
            new File(FileLoader.getDirectory(4), fileName + ".jpg").renameTo(FileLoader.getPathToAttach(size));
            ImageLoader.getInstance().replaceImageInCache(fileName, size.location.volume_id + "_" + size.location.local_id, ImageLocation.getForPhoto(size, newMsg.media.photo), true);
            ArrayList<TLRPC.Message> arr = new ArrayList<>();
            arr.add(newMsg);
            getMessagesStorage().putMessages(arr, false, true, false, 0, false);
        } else if ((newMsg.media instanceof TLRPC.TL_messageMediaDocument) && newMsg.media.document != null) {
            TLRPC.Document document = newMsg.media.document;
            newMsg.media.document = new TLRPC.TL_documentEncrypted();
            newMsg.media.document.id = encryptedFile.id;
            newMsg.media.document.access_hash = encryptedFile.access_hash;
            newMsg.media.document.date = document.date;
            newMsg.media.document.attributes = document.attributes;
            newMsg.media.document.mime_type = document.mime_type;
            newMsg.media.document.size = encryptedFile.size;
            newMsg.media.document.key = decryptedMessage2.media.key;
            newMsg.media.document.iv = decryptedMessage2.media.iv;
            newMsg.media.document.thumbs = document.thumbs;
            newMsg.media.document.dc_id = encryptedFile.dc_id;
            if (newMsg.media.document.thumbs.isEmpty()) {
                TLRPC.PhotoSize thumb = new TLRPC.TL_photoSizeEmpty();
                thumb.type = "s";
                newMsg.media.document.thumbs.add(thumb);
            }
            if (newMsg.attachPath != null && newMsg.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath()) && new File(newMsg.attachPath).renameTo(FileLoader.getPathToAttach(newMsg.media.document))) {
                messageObject.mediaExists = messageObject.attachPathExists;
                messageObject.attachPathExists = false;
                newMsg.attachPath = "";
            }
            ArrayList<TLRPC.Message> arr2 = new ArrayList<>();
            arr2.add(newMsg);
            getMessagesStorage().putMessages(arr2, false, true, false, 0, false);
        }
    }

    public static boolean isSecretVisibleMessage(TLRPC.Message message) {
        return (message.action instanceof TLRPC.TL_messageEncryptedAction) && ((message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL));
    }

    public static boolean isSecretInvisibleMessage(TLRPC.Message message) {
        return (message.action instanceof TLRPC.TL_messageEncryptedAction) && !(message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) && !(message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL);
    }

    /* access modifiers changed from: protected */
    public void performSendEncryptedRequest(TLRPC.TL_messages_sendEncryptedMultiMedia req, SendMessagesHelper.DelayedMessage message) {
        for (int a = 0; a < req.files.size(); a++) {
            performSendEncryptedRequest(req.messages.get(a), message.messages.get(a), message.encryptedChat, req.files.get(a), message.originalPaths.get(a), message.messageObjects.get(a));
        }
    }

    /* access modifiers changed from: protected */
    public void performSendEncryptedRequest(TLRPC.DecryptedMessage req, TLRPC.Message newMsgObj, TLRPC.EncryptedChat chat, TLRPC.InputEncryptedFile encryptedFile, String originalPath, MessageObject newMsg) {
        TLRPC.EncryptedChat encryptedChat = chat;
        if (req == null || encryptedChat.auth_key == null || (encryptedChat instanceof TLRPC.TL_encryptedChatRequested)) {
            TLRPC.Message message = newMsgObj;
        } else if (encryptedChat instanceof TLRPC.TL_encryptedChatWaiting) {
            TLRPC.Message message2 = newMsgObj;
        } else {
            TLRPC.Message message3 = newMsgObj;
            getSendMessagesHelper().putToSendingMessages(newMsgObj, false);
            Utilities.stageQueue.postRunnable(new Runnable(chat, req, newMsgObj, encryptedFile, newMsg, originalPath) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;
                private final /* synthetic */ TLRPC.DecryptedMessage f$2;
                private final /* synthetic */ TLRPC.Message f$3;
                private final /* synthetic */ TLRPC.InputEncryptedFile f$4;
                private final /* synthetic */ MessageObject f$5;
                private final /* synthetic */ String f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$performSendEncryptedRequest$7$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v35, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncrypted} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v36, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$performSendEncryptedRequest$7$SecretChatHelper(im.bclpbkiauv.tgnet.TLRPC.EncryptedChat r29, im.bclpbkiauv.tgnet.TLRPC.DecryptedMessage r30, im.bclpbkiauv.tgnet.TLRPC.Message r31, im.bclpbkiauv.tgnet.TLRPC.InputEncryptedFile r32, im.bclpbkiauv.messenger.MessageObject r33, java.lang.String r34) {
        /*
            r28 = this;
            r8 = r29
            r9 = r30
            r10 = r31
            r11 = r32
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageLayer r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageLayer     // Catch:{ Exception -> 0x02a8 }
            r0.<init>()     // Catch:{ Exception -> 0x02a8 }
            int r1 = r8.layer     // Catch:{ Exception -> 0x02a8 }
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.getMyLayerVersion(r1)     // Catch:{ Exception -> 0x02a8 }
            r2 = 46
            int r1 = java.lang.Math.max(r2, r1)     // Catch:{ Exception -> 0x02a8 }
            r12 = r1
            int r1 = r8.layer     // Catch:{ Exception -> 0x02a8 }
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r1)     // Catch:{ Exception -> 0x02a8 }
            int r1 = java.lang.Math.max(r2, r1)     // Catch:{ Exception -> 0x02a8 }
            int r1 = java.lang.Math.min(r12, r1)     // Catch:{ Exception -> 0x02a8 }
            r0.layer = r1     // Catch:{ Exception -> 0x02a8 }
            r0.message = r9     // Catch:{ Exception -> 0x02a8 }
            r1 = 15
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x02a8 }
            r0.random_bytes = r1     // Catch:{ Exception -> 0x02a8 }
            java.security.SecureRandom r1 = im.bclpbkiauv.messenger.Utilities.random     // Catch:{ Exception -> 0x02a8 }
            byte[] r2 = r0.random_bytes     // Catch:{ Exception -> 0x02a8 }
            r1.nextBytes(r2)     // Catch:{ Exception -> 0x02a8 }
            r13 = r0
            int r1 = r8.layer     // Catch:{ Exception -> 0x02a8 }
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r1)     // Catch:{ Exception -> 0x02a8 }
            r2 = 73
            r3 = 1
            r4 = 2
            if (r1 < r2) goto L_0x0048
            r1 = 2
            goto L_0x0049
        L_0x0048:
            r1 = 1
        L_0x0049:
            r14 = r1
            int r1 = r8.seq_in     // Catch:{ Exception -> 0x02a8 }
            if (r1 != 0) goto L_0x0067
            int r1 = r8.seq_out     // Catch:{ Exception -> 0x02a8 }
            if (r1 != 0) goto L_0x0067
            int r1 = r8.admin_id     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.messenger.UserConfig r2 = r28.getUserConfig()     // Catch:{ Exception -> 0x02a8 }
            int r2 = r2.getClientUserId()     // Catch:{ Exception -> 0x02a8 }
            if (r1 != r2) goto L_0x0064
            r8.seq_out = r3     // Catch:{ Exception -> 0x02a8 }
            r1 = -2
            r8.seq_in = r1     // Catch:{ Exception -> 0x02a8 }
            goto L_0x0067
        L_0x0064:
            r1 = -1
            r8.seq_in = r1     // Catch:{ Exception -> 0x02a8 }
        L_0x0067:
            int r1 = r10.seq_in     // Catch:{ Exception -> 0x02a8 }
            r2 = 0
            if (r1 != 0) goto L_0x00e9
            int r1 = r10.seq_out     // Catch:{ Exception -> 0x02a8 }
            if (r1 != 0) goto L_0x00e9
            int r1 = r8.seq_in     // Catch:{ Exception -> 0x02a8 }
            if (r1 <= 0) goto L_0x0077
            int r1 = r8.seq_in     // Catch:{ Exception -> 0x02a8 }
            goto L_0x007a
        L_0x0077:
            int r1 = r8.seq_in     // Catch:{ Exception -> 0x02a8 }
            int r1 = r1 + r4
        L_0x007a:
            r0.in_seq_no = r1     // Catch:{ Exception -> 0x02a8 }
            int r1 = r8.seq_out     // Catch:{ Exception -> 0x02a8 }
            r0.out_seq_no = r1     // Catch:{ Exception -> 0x02a8 }
            int r1 = r8.seq_out     // Catch:{ Exception -> 0x02a8 }
            int r1 = r1 + r4
            r8.seq_out = r1     // Catch:{ Exception -> 0x02a8 }
            int r1 = r8.layer     // Catch:{ Exception -> 0x02a8 }
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r1)     // Catch:{ Exception -> 0x02a8 }
            r5 = 20
            if (r1 < r5) goto L_0x00ca
            int r1 = r8.key_create_date     // Catch:{ Exception -> 0x02a8 }
            if (r1 != 0) goto L_0x009d
            im.bclpbkiauv.tgnet.ConnectionsManager r1 = r28.getConnectionsManager()     // Catch:{ Exception -> 0x02a8 }
            int r1 = r1.getCurrentTime()     // Catch:{ Exception -> 0x02a8 }
            r8.key_create_date = r1     // Catch:{ Exception -> 0x02a8 }
        L_0x009d:
            short r1 = r8.key_use_count_out     // Catch:{ Exception -> 0x02a8 }
            int r1 = r1 + r3
            short r1 = (short) r1     // Catch:{ Exception -> 0x02a8 }
            r8.key_use_count_out = r1     // Catch:{ Exception -> 0x02a8 }
            short r1 = r8.key_use_count_out     // Catch:{ Exception -> 0x02a8 }
            r5 = 100
            if (r1 >= r5) goto L_0x00b9
            int r1 = r8.key_create_date     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.ConnectionsManager r5 = r28.getConnectionsManager()     // Catch:{ Exception -> 0x02a8 }
            int r5 = r5.getCurrentTime()     // Catch:{ Exception -> 0x02a8 }
            r6 = 604800(0x93a80, float:8.47505E-40)
            int r5 = r5 - r6
            if (r1 >= r5) goto L_0x00ca
        L_0x00b9:
            long r5 = r8.exchange_id     // Catch:{ Exception -> 0x02a8 }
            r15 = 0
            int r1 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r1 != 0) goto L_0x00ca
            long r5 = r8.future_key_fingerprint     // Catch:{ Exception -> 0x02a8 }
            int r1 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r1 != 0) goto L_0x00ca
            r28.requestNewSecretChatKey(r29)     // Catch:{ Exception -> 0x02a8 }
        L_0x00ca:
            im.bclpbkiauv.messenger.MessagesStorage r1 = r28.getMessagesStorage()     // Catch:{ Exception -> 0x02a8 }
            r1.updateEncryptedChatSeq(r8, r2)     // Catch:{ Exception -> 0x02a8 }
            if (r10 == 0) goto L_0x00f1
            int r1 = r0.in_seq_no     // Catch:{ Exception -> 0x02a8 }
            r10.seq_in = r1     // Catch:{ Exception -> 0x02a8 }
            int r1 = r0.out_seq_no     // Catch:{ Exception -> 0x02a8 }
            r10.seq_out = r1     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.messenger.MessagesStorage r1 = r28.getMessagesStorage()     // Catch:{ Exception -> 0x02a8 }
            int r5 = r10.id     // Catch:{ Exception -> 0x02a8 }
            int r6 = r10.seq_in     // Catch:{ Exception -> 0x02a8 }
            int r7 = r10.seq_out     // Catch:{ Exception -> 0x02a8 }
            r1.setMessageSeq(r5, r6, r7)     // Catch:{ Exception -> 0x02a8 }
            goto L_0x00f1
        L_0x00e9:
            int r1 = r10.seq_in     // Catch:{ Exception -> 0x02a8 }
            r0.in_seq_no = r1     // Catch:{ Exception -> 0x02a8 }
            int r1 = r10.seq_out     // Catch:{ Exception -> 0x02a8 }
            r0.out_seq_no = r1     // Catch:{ Exception -> 0x02a8 }
        L_0x00f1:
            boolean r1 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02a8 }
            if (r1 == 0) goto L_0x0118
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a8 }
            r1.<init>()     // Catch:{ Exception -> 0x02a8 }
            r1.append(r9)     // Catch:{ Exception -> 0x02a8 }
            java.lang.String r5 = " send message with in_seq = "
            r1.append(r5)     // Catch:{ Exception -> 0x02a8 }
            int r5 = r0.in_seq_no     // Catch:{ Exception -> 0x02a8 }
            r1.append(r5)     // Catch:{ Exception -> 0x02a8 }
            java.lang.String r5 = " out_seq = "
            r1.append(r5)     // Catch:{ Exception -> 0x02a8 }
            int r5 = r0.out_seq_no     // Catch:{ Exception -> 0x02a8 }
            r1.append(r5)     // Catch:{ Exception -> 0x02a8 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x02a8 }
        L_0x0118:
            int r1 = r13.getObjectSize()     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.NativeByteBuffer r5 = new im.bclpbkiauv.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x02a8 }
            int r6 = r1 + 4
            r5.<init>((int) r6)     // Catch:{ Exception -> 0x02a8 }
            r15 = r5
            r15.writeInt32(r1)     // Catch:{ Exception -> 0x02a8 }
            r13.serializeToStream(r15)     // Catch:{ Exception -> 0x02a8 }
            int r5 = r15.length()     // Catch:{ Exception -> 0x02a8 }
            r16 = r5
            int r1 = r16 % 16
            r5 = 16
            if (r1 == 0) goto L_0x013b
            int r1 = r16 % 16
            int r1 = 16 - r1
            goto L_0x013c
        L_0x013b:
            r1 = 0
        L_0x013c:
            if (r14 != r4) goto L_0x014b
            java.security.SecureRandom r6 = im.bclpbkiauv.messenger.Utilities.random     // Catch:{ Exception -> 0x02a8 }
            r7 = 3
            int r6 = r6.nextInt(r7)     // Catch:{ Exception -> 0x02a8 }
            int r6 = r6 + r4
            int r6 = r6 * 16
            int r1 = r1 + r6
            r7 = r1
            goto L_0x014c
        L_0x014b:
            r7 = r1
        L_0x014c:
            im.bclpbkiauv.tgnet.NativeByteBuffer r1 = new im.bclpbkiauv.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x02a8 }
            int r6 = r16 + r7
            r1.<init>((int) r6)     // Catch:{ Exception -> 0x02a8 }
            r6 = r1
            r15.position(r2)     // Catch:{ Exception -> 0x02a8 }
            r6.writeBytes((im.bclpbkiauv.tgnet.NativeByteBuffer) r15)     // Catch:{ Exception -> 0x02a8 }
            if (r7 == 0) goto L_0x0166
            byte[] r1 = new byte[r7]     // Catch:{ Exception -> 0x02a8 }
            java.security.SecureRandom r3 = im.bclpbkiauv.messenger.Utilities.random     // Catch:{ Exception -> 0x02a8 }
            r3.nextBytes(r1)     // Catch:{ Exception -> 0x02a8 }
            r6.writeBytes((byte[]) r1)     // Catch:{ Exception -> 0x02a8 }
        L_0x0166:
            byte[] r1 = new byte[r5]     // Catch:{ Exception -> 0x02a8 }
            r3 = r1
            if (r14 != r4) goto L_0x017a
            int r1 = r8.admin_id     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.messenger.UserConfig r18 = r28.getUserConfig()     // Catch:{ Exception -> 0x02a8 }
            int r2 = r18.getClientUserId()     // Catch:{ Exception -> 0x02a8 }
            if (r1 == r2) goto L_0x017a
            r17 = 1
            goto L_0x017c
        L_0x017a:
            r17 = 0
        L_0x017c:
            r2 = r17
            if (r14 != r4) goto L_0x01ad
            byte[] r4 = r8.auth_key     // Catch:{ Exception -> 0x02a8 }
            r17 = 88
            if (r2 == 0) goto L_0x0189
            r18 = 8
            goto L_0x018b
        L_0x0189:
            r18 = 0
        L_0x018b:
            int r21 = r17 + r18
            r22 = 32
            java.nio.ByteBuffer r1 = r6.buffer     // Catch:{ Exception -> 0x02a8 }
            r24 = 0
            java.nio.ByteBuffer r5 = r6.buffer     // Catch:{ Exception -> 0x02a8 }
            int r25 = r5.limit()     // Catch:{ Exception -> 0x02a8 }
            r20 = r4
            r23 = r1
            byte[] r1 = im.bclpbkiauv.messenger.Utilities.computeSHA256(r20, r21, r22, r23, r24, r25)     // Catch:{ Exception -> 0x02a8 }
            r20 = r0
            r0 = 0
            r4 = 8
            r5 = 16
            java.lang.System.arraycopy(r1, r4, r3, r0, r5)     // Catch:{ Exception -> 0x02a8 }
            r0 = r1
            goto L_0x01bf
        L_0x01ad:
            r20 = r0
            java.nio.ByteBuffer r0 = r15.buffer     // Catch:{ Exception -> 0x02a8 }
            byte[] r0 = im.bclpbkiauv.messenger.Utilities.computeSHA1((java.nio.ByteBuffer) r0)     // Catch:{ Exception -> 0x02a8 }
            r1 = r0
            int r0 = r1.length     // Catch:{ Exception -> 0x02a8 }
            r4 = 16
            int r0 = r0 - r4
            r5 = 0
            java.lang.System.arraycopy(r1, r0, r3, r5, r4)     // Catch:{ Exception -> 0x02a8 }
            r0 = r1
        L_0x01bf:
            r15.reuse()     // Catch:{ Exception -> 0x02a8 }
            byte[] r1 = r8.auth_key     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.messenger.MessageKeyData r1 = im.bclpbkiauv.messenger.MessageKeyData.generateMessageKeyData(r1, r3, r2, r14)     // Catch:{ Exception -> 0x02a8 }
            r5 = r1
            java.nio.ByteBuffer r1 = r6.buffer     // Catch:{ Exception -> 0x02a8 }
            byte[] r4 = r5.aesKey     // Catch:{ Exception -> 0x02a8 }
            r18 = r0
            byte[] r0 = r5.aesIv     // Catch:{ Exception -> 0x02a8 }
            r24 = 1
            r25 = 0
            r26 = 0
            int r27 = r6.limit()     // Catch:{ Exception -> 0x02a8 }
            r21 = r1
            r22 = r4
            r23 = r0
            im.bclpbkiauv.messenger.Utilities.aesIgeEncryption(r21, r22, r23, r24, r25, r26, r27)     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.NativeByteBuffer r0 = new im.bclpbkiauv.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x02a8 }
            int r1 = r3.length     // Catch:{ Exception -> 0x02a8 }
            r4 = 8
            int r1 = r1 + r4
            int r4 = r6.length()     // Catch:{ Exception -> 0x02a8 }
            int r1 = r1 + r4
            r0.<init>((int) r1)     // Catch:{ Exception -> 0x02a8 }
            r1 = 0
            r6.position(r1)     // Catch:{ Exception -> 0x02a8 }
            r4 = r2
            long r1 = r8.key_fingerprint     // Catch:{ Exception -> 0x02a8 }
            r0.writeInt64(r1)     // Catch:{ Exception -> 0x02a8 }
            r0.writeBytes((byte[]) r3)     // Catch:{ Exception -> 0x02a8 }
            r0.writeBytes((im.bclpbkiauv.tgnet.NativeByteBuffer) r6)     // Catch:{ Exception -> 0x02a8 }
            r6.reuse()     // Catch:{ Exception -> 0x02a8 }
            r1 = 0
            r0.position(r1)     // Catch:{ Exception -> 0x02a8 }
            if (r11 != 0) goto L_0x0259
            boolean r1 = r9 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageService     // Catch:{ Exception -> 0x02a8 }
            if (r1 == 0) goto L_0x0234
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedService r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedService     // Catch:{ Exception -> 0x02a8 }
            r1.<init>()     // Catch:{ Exception -> 0x02a8 }
            r1.data = r0     // Catch:{ Exception -> 0x02a8 }
            r17 = r3
            long r2 = r9.random_id     // Catch:{ Exception -> 0x02a8 }
            r1.random_id = r2     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x02a8 }
            r2.<init>()     // Catch:{ Exception -> 0x02a8 }
            r1.peer = r2     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x02a8 }
            int r3 = r8.id     // Catch:{ Exception -> 0x02a8 }
            r2.chat_id = r3     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x02a8 }
            r19 = r4
            long r3 = r8.access_hash     // Catch:{ Exception -> 0x02a8 }
            r2.access_hash = r3     // Catch:{ Exception -> 0x02a8 }
            r4 = r1
            goto L_0x0280
        L_0x0234:
            r17 = r3
            r19 = r4
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncrypted r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncrypted     // Catch:{ Exception -> 0x02a8 }
            r1.<init>()     // Catch:{ Exception -> 0x02a8 }
            r1.data = r0     // Catch:{ Exception -> 0x02a8 }
            long r2 = r9.random_id     // Catch:{ Exception -> 0x02a8 }
            r1.random_id = r2     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x02a8 }
            r2.<init>()     // Catch:{ Exception -> 0x02a8 }
            r1.peer = r2     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x02a8 }
            int r3 = r8.id     // Catch:{ Exception -> 0x02a8 }
            r2.chat_id = r3     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x02a8 }
            long r3 = r8.access_hash     // Catch:{ Exception -> 0x02a8 }
            r2.access_hash = r3     // Catch:{ Exception -> 0x02a8 }
            r4 = r1
            goto L_0x0280
        L_0x0259:
            r17 = r3
            r19 = r4
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedFile r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedFile     // Catch:{ Exception -> 0x02a8 }
            r1.<init>()     // Catch:{ Exception -> 0x02a8 }
            r1.data = r0     // Catch:{ Exception -> 0x02a8 }
            long r2 = r9.random_id     // Catch:{ Exception -> 0x02a8 }
            r1.random_id = r2     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x02a8 }
            r2.<init>()     // Catch:{ Exception -> 0x02a8 }
            r1.peer = r2     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x02a8 }
            int r3 = r8.id     // Catch:{ Exception -> 0x02a8 }
            r2.chat_id = r3     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x02a8 }
            long r3 = r8.access_hash     // Catch:{ Exception -> 0x02a8 }
            r2.access_hash = r3     // Catch:{ Exception -> 0x02a8 }
            r1.file = r11     // Catch:{ Exception -> 0x02a8 }
            r2 = r1
            r1 = r2
            r4 = r1
        L_0x0280:
            im.bclpbkiauv.tgnet.ConnectionsManager r3 = r28.getConnectionsManager()     // Catch:{ Exception -> 0x02a8 }
            im.bclpbkiauv.messenger.-$$Lambda$SecretChatHelper$cbMrFnv9hxi4xiEjYlOEOfggDM0 r2 = new im.bclpbkiauv.messenger.-$$Lambda$SecretChatHelper$cbMrFnv9hxi4xiEjYlOEOfggDM0     // Catch:{ Exception -> 0x02a8 }
            r1 = r2
            r21 = r0
            r0 = r2
            r2 = r28
            r8 = r3
            r3 = r30
            r9 = r4
            r4 = r29
            r22 = r5
            r5 = r31
            r23 = r6
            r6 = r33
            r24 = r7
            r7 = r34
            r1.<init>(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x02a8 }
            r1 = 64
            r8.sendRequest(r9, r0, r1)     // Catch:{ Exception -> 0x02a8 }
            goto L_0x02ac
        L_0x02a8:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02ac:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SecretChatHelper.lambda$performSendEncryptedRequest$7$SecretChatHelper(im.bclpbkiauv.tgnet.TLRPC$EncryptedChat, im.bclpbkiauv.tgnet.TLRPC$DecryptedMessage, im.bclpbkiauv.tgnet.TLRPC$Message, im.bclpbkiauv.tgnet.TLRPC$InputEncryptedFile, im.bclpbkiauv.messenger.MessageObject, java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$6$SecretChatHelper(TLRPC.DecryptedMessage req, TLRPC.EncryptedChat chat, TLRPC.Message newMsgObj, MessageObject newMsg, String originalPath, TLObject response, TLRPC.TL_error error) {
        int existFlags;
        TLRPC.EncryptedChat currentChat;
        TLRPC.DecryptedMessage decryptedMessage = req;
        TLRPC.EncryptedChat encryptedChat = chat;
        TLRPC.Message message = newMsgObj;
        MessageObject messageObject = newMsg;
        if (error == null && (decryptedMessage.action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer)) {
            TLRPC.EncryptedChat currentChat2 = getMessagesController().getEncryptedChat(Integer.valueOf(encryptedChat.id));
            if (currentChat2 == null) {
                currentChat = chat;
            } else {
                currentChat = currentChat2;
            }
            if (currentChat.key_hash == null) {
                currentChat.key_hash = AndroidUtilities.calcAuthKeyHash(currentChat.auth_key);
            }
            if (AndroidUtilities.getPeerLayerVersion(currentChat.layer) >= 46 && currentChat.key_hash.length == 16) {
                try {
                    byte[] sha256 = Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length);
                    byte[] key_hash = new byte[36];
                    System.arraycopy(encryptedChat.key_hash, 0, key_hash, 0, 16);
                    System.arraycopy(sha256, 0, key_hash, 16, 20);
                    currentChat.key_hash = key_hash;
                    getMessagesStorage().updateEncryptedChat(currentChat);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            this.sendingNotifyLayer.remove(Integer.valueOf(currentChat.id));
            currentChat.layer = AndroidUtilities.setMyLayerVersion(currentChat.layer, 101);
            getMessagesStorage().updateEncryptedChatLayer(currentChat);
        }
        if (message == null) {
            String str = originalPath;
        } else if (error == null) {
            String attachPath = message.attachPath;
            TLRPC.messages_SentEncryptedMessage res = (TLRPC.messages_SentEncryptedMessage) response;
            if (isSecretVisibleMessage(newMsgObj)) {
                message.date = res.date;
            }
            if (messageObject == null || !(res.file instanceof TLRPC.TL_encryptedFile)) {
                String str2 = originalPath;
                existFlags = 0;
            } else {
                updateMediaPaths(messageObject, res.file, decryptedMessage, originalPath);
                existFlags = newMsg.getMediaExistanceFlags();
            }
            DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
            $$Lambda$SecretChatHelper$AfwputQrk0ua8PHQuerm8OnIEXk r8 = r1;
            $$Lambda$SecretChatHelper$AfwputQrk0ua8PHQuerm8OnIEXk r1 = new Runnable(newMsgObj, res, existFlags, attachPath) {
                private final /* synthetic */ TLRPC.Message f$1;
                private final /* synthetic */ TLRPC.messages_SentEncryptedMessage f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ String f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$null$4$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            };
            storageQueue.postRunnable(r8);
        } else {
            String str3 = originalPath;
            getMessagesStorage().markMessageAsSendError(message, false);
            AndroidUtilities.runOnUIThread(new Runnable(message) {
                private final /* synthetic */ TLRPC.Message f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$null$5$SecretChatHelper(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$4$SecretChatHelper(TLRPC.Message newMsgObj, TLRPC.messages_SentEncryptedMessage res, int existFlags, String attachPath) {
        if (isSecretInvisibleMessage(newMsgObj)) {
            res.date = 0;
        }
        getMessagesStorage().updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(newMsgObj.id), newMsgObj.id, res.date, false, 0, 0);
        AndroidUtilities.runOnUIThread(new Runnable(newMsgObj, existFlags, attachPath) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$3$SecretChatHelper(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$SecretChatHelper(TLRPC.Message newMsgObj, int existFlags, String attachPath) {
        newMsgObj.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(newMsgObj.id), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), 0L, Integer.valueOf(existFlags), false);
        getSendMessagesHelper().processSentMessage(newMsgObj.id);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj)) {
            getSendMessagesHelper().stopVideoService(attachPath);
        }
        getSendMessagesHelper().removeFromSendingMessages(newMsgObj.id, false);
    }

    public /* synthetic */ void lambda$null$5$SecretChatHelper(TLRPC.Message newMsgObj) {
        newMsgObj.send_state = 2;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
        getSendMessagesHelper().processSentMessage(newMsgObj.id);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj)) {
            getSendMessagesHelper().stopVideoService(newMsgObj.attachPath);
        }
        getSendMessagesHelper().removeFromSendingMessages(newMsgObj.id, false);
    }

    private void applyPeerLayer(TLRPC.EncryptedChat chat, int newPeerLayer) {
        int currentPeerLayer = AndroidUtilities.getPeerLayerVersion(chat.layer);
        if (newPeerLayer > currentPeerLayer) {
            if (chat.key_hash.length == 16 && currentPeerLayer >= 46) {
                try {
                    byte[] sha256 = Utilities.computeSHA256(chat.auth_key, 0, chat.auth_key.length);
                    byte[] key_hash = new byte[36];
                    System.arraycopy(chat.key_hash, 0, key_hash, 0, 16);
                    System.arraycopy(sha256, 0, key_hash, 16, 20);
                    chat.key_hash = key_hash;
                    getMessagesStorage().updateEncryptedChat(chat);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            chat.layer = AndroidUtilities.setPeerLayerVersion(chat.layer, newPeerLayer);
            getMessagesStorage().updateEncryptedChatLayer(chat);
            if (currentPeerLayer < 101) {
                sendNotifyLayerMessage(chat, (TLRPC.Message) null);
            }
            AndroidUtilities.runOnUIThread(new Runnable(chat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$applyPeerLayer$8$SecretChatHelper(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyPeerLayer$8$SecretChatHelper(TLRPC.EncryptedChat chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, chat);
    }

    public TLRPC.Message processDecryptedObject(TLRPC.EncryptedChat chat, TLRPC.EncryptedFile file, int date, TLObject object, boolean new_key_used) {
        int i;
        TLRPC.TL_message newMessage;
        TLRPC.PhotoSize photoSize;
        TLRPC.PhotoSize photoSize2;
        TLRPC.EncryptedChat encryptedChat = chat;
        TLRPC.EncryptedFile encryptedFile = file;
        int i2 = date;
        TLObject tLObject = object;
        if (tLObject != null) {
            int from_id = encryptedChat.admin_id;
            if (from_id == getUserConfig().getClientUserId()) {
                from_id = encryptedChat.participant_id;
            }
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20 && encryptedChat.exchange_id == 0 && encryptedChat.future_key_fingerprint == 0 && encryptedChat.key_use_count_in >= 120) {
                requestNewSecretChatKey(chat);
            }
            if (encryptedChat.exchange_id == 0 && encryptedChat.future_key_fingerprint != 0 && !new_key_used) {
                encryptedChat.future_auth_key = new byte[256];
                encryptedChat.future_key_fingerprint = 0;
                getMessagesStorage().updateEncryptedChat(encryptedChat);
            } else if (encryptedChat.exchange_id != 0 && new_key_used) {
                encryptedChat.key_fingerprint = encryptedChat.future_key_fingerprint;
                encryptedChat.auth_key = encryptedChat.future_auth_key;
                encryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
                encryptedChat.future_auth_key = new byte[256];
                encryptedChat.future_key_fingerprint = 0;
                encryptedChat.key_use_count_in = 0;
                encryptedChat.key_use_count_out = 0;
                encryptedChat.exchange_id = 0;
                getMessagesStorage().updateEncryptedChat(encryptedChat);
            }
            if (tLObject instanceof TLRPC.TL_decryptedMessage) {
                TLRPC.TL_decryptedMessage decryptedMessage = (TLRPC.TL_decryptedMessage) tLObject;
                if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                    newMessage = new TLRPC.TL_message_secret();
                    newMessage.ttl = decryptedMessage.ttl;
                    newMessage.entities = decryptedMessage.entities;
                } else {
                    newMessage = new TLRPC.TL_message();
                    newMessage.ttl = encryptedChat.ttl;
                }
                newMessage.message = decryptedMessage.message;
                newMessage.date = i2;
                int newMessageId = getUserConfig().getNewMessageId();
                newMessage.id = newMessageId;
                newMessage.local_id = newMessageId;
                getUserConfig().saveConfig(false);
                newMessage.from_id = from_id;
                newMessage.to_id = new TLRPC.TL_peerUser();
                int i3 = from_id;
                newMessage.random_id = decryptedMessage.random_id;
                newMessage.to_id.user_id = getUserConfig().getClientUserId();
                newMessage.unread = true;
                newMessage.flags = 768;
                if (decryptedMessage.via_bot_name != null && decryptedMessage.via_bot_name.length() > 0) {
                    newMessage.via_bot_name = decryptedMessage.via_bot_name;
                    newMessage.flags |= 2048;
                }
                if (decryptedMessage.grouped_id != 0) {
                    newMessage.grouped_id = decryptedMessage.grouped_id;
                    newMessage.flags |= 131072;
                }
                newMessage.dialog_id = ((long) encryptedChat.id) << 32;
                if (decryptedMessage.reply_to_random_id != 0) {
                    newMessage.reply_to_random_id = decryptedMessage.reply_to_random_id;
                    newMessage.flags |= 8;
                }
                if (decryptedMessage.media == null || (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaEmpty)) {
                    newMessage.media = new TLRPC.TL_messageMediaEmpty();
                } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaWebPage) {
                    newMessage.media = new TLRPC.TL_messageMediaWebPage();
                    newMessage.media.webpage = new TLRPC.TL_webPageUrlPending();
                    newMessage.media.webpage.url = decryptedMessage.media.url;
                } else {
                    String str = "";
                    if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaContact) {
                        newMessage.media = new TLRPC.TL_messageMediaContact();
                        newMessage.media.last_name = decryptedMessage.media.last_name;
                        newMessage.media.first_name = decryptedMessage.media.first_name;
                        newMessage.media.phone_number = decryptedMessage.media.phone_number;
                        newMessage.media.user_id = decryptedMessage.media.user_id;
                        newMessage.media.vcard = str;
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaGeoPoint) {
                        newMessage.media = new TLRPC.TL_messageMediaGeo();
                        newMessage.media.geo = new TLRPC.TL_geoPoint();
                        newMessage.media.geo.lat = decryptedMessage.media.lat;
                        newMessage.media.geo._long = decryptedMessage.media._long;
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaPhoto) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaPhoto();
                        newMessage.media.flags |= 3;
                        if (decryptedMessage.media.caption != null) {
                            str = decryptedMessage.media.caption;
                        }
                        newMessage.message = str;
                        newMessage.media.photo = new TLRPC.TL_photo();
                        newMessage.media.photo.file_reference = new byte[0];
                        newMessage.media.photo.date = newMessage.date;
                        byte[] thumb = ((TLRPC.TL_decryptedMessageMediaPhoto) decryptedMessage.media).thumb;
                        if (thumb != null && thumb.length != 0 && thumb.length <= 6000 && decryptedMessage.media.thumb_w <= 100 && decryptedMessage.media.thumb_h <= 100) {
                            TLRPC.TL_photoCachedSize small = new TLRPC.TL_photoCachedSize();
                            small.w = decryptedMessage.media.thumb_w;
                            small.h = decryptedMessage.media.thumb_h;
                            small.bytes = thumb;
                            small.type = "s";
                            small.location = new TLRPC.TL_fileLocationUnavailable();
                            newMessage.media.photo.sizes.add(small);
                        }
                        if (newMessage.ttl != 0) {
                            newMessage.media.ttl_seconds = newMessage.ttl;
                            newMessage.media.flags |= 4;
                        }
                        TLRPC.TL_photoSize big = new TLRPC.TL_photoSize();
                        big.w = decryptedMessage.media.w;
                        big.h = decryptedMessage.media.h;
                        big.type = "x";
                        big.size = encryptedFile.size;
                        big.location = new TLRPC.TL_fileEncryptedLocation();
                        big.location.key = decryptedMessage.media.key;
                        big.location.iv = decryptedMessage.media.iv;
                        big.location.dc_id = encryptedFile.dc_id;
                        big.location.volume_id = encryptedFile.id;
                        big.location.secret = encryptedFile.access_hash;
                        big.location.local_id = encryptedFile.key_fingerprint;
                        newMessage.media.photo.sizes.add(big);
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaVideo) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        newMessage.media.document = new TLRPC.TL_documentEncrypted();
                        newMessage.media.document.key = decryptedMessage.media.key;
                        newMessage.media.document.iv = decryptedMessage.media.iv;
                        newMessage.media.document.dc_id = encryptedFile.dc_id;
                        if (decryptedMessage.media.caption != null) {
                            str = decryptedMessage.media.caption;
                        }
                        newMessage.message = str;
                        newMessage.media.document.date = i2;
                        newMessage.media.document.size = encryptedFile.size;
                        newMessage.media.document.id = encryptedFile.id;
                        newMessage.media.document.access_hash = encryptedFile.access_hash;
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = MimeTypes.VIDEO_MP4;
                        }
                        byte[] thumb2 = ((TLRPC.TL_decryptedMessageMediaVideo) decryptedMessage.media).thumb;
                        if (thumb2 == null || thumb2.length == 0 || thumb2.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                            photoSize2 = new TLRPC.TL_photoSizeEmpty();
                            photoSize2.type = "s";
                        } else {
                            photoSize2 = new TLRPC.TL_photoCachedSize();
                            photoSize2.bytes = thumb2;
                            photoSize2.w = decryptedMessage.media.thumb_w;
                            photoSize2.h = decryptedMessage.media.thumb_h;
                            photoSize2.type = "s";
                            photoSize2.location = new TLRPC.TL_fileLocationUnavailable();
                        }
                        newMessage.media.document.thumbs.add(photoSize2);
                        newMessage.media.document.flags |= 1;
                        TLRPC.TL_documentAttributeVideo attributeVideo = new TLRPC.TL_documentAttributeVideo();
                        attributeVideo.w = decryptedMessage.media.w;
                        attributeVideo.h = decryptedMessage.media.h;
                        attributeVideo.duration = decryptedMessage.media.duration;
                        attributeVideo.supports_streaming = false;
                        newMessage.media.document.attributes.add(attributeVideo);
                        if (newMessage.ttl != 0) {
                            newMessage.media.ttl_seconds = newMessage.ttl;
                            newMessage.media.flags |= 4;
                        }
                        if (newMessage.ttl != 0) {
                            newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                        }
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaDocument) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : str;
                        newMessage.media.document = new TLRPC.TL_documentEncrypted();
                        newMessage.media.document.id = encryptedFile.id;
                        newMessage.media.document.access_hash = encryptedFile.access_hash;
                        newMessage.media.document.date = i2;
                        if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaDocument_layer8) {
                            TLRPC.TL_documentAttributeFilename fileName = new TLRPC.TL_documentAttributeFilename();
                            fileName.file_name = decryptedMessage.media.file_name;
                            newMessage.media.document.attributes.add(fileName);
                        } else {
                            newMessage.media.document.attributes = decryptedMessage.media.attributes;
                        }
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        newMessage.media.document.size = decryptedMessage.media.size != 0 ? Math.min(decryptedMessage.media.size, encryptedFile.size) : encryptedFile.size;
                        newMessage.media.document.key = decryptedMessage.media.key;
                        newMessage.media.document.iv = decryptedMessage.media.iv;
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = str;
                        }
                        byte[] thumb3 = ((TLRPC.TL_decryptedMessageMediaDocument) decryptedMessage.media).thumb;
                        if (thumb3 == null || thumb3.length == 0 || thumb3.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                            photoSize = new TLRPC.TL_photoSizeEmpty();
                            photoSize.type = "s";
                        } else {
                            photoSize = new TLRPC.TL_photoCachedSize();
                            photoSize.bytes = thumb3;
                            photoSize.w = decryptedMessage.media.thumb_w;
                            photoSize.h = decryptedMessage.media.thumb_h;
                            photoSize.type = "s";
                            photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                        }
                        newMessage.media.document.thumbs.add(photoSize);
                        newMessage.media.document.flags |= 1;
                        newMessage.media.document.dc_id = encryptedFile.dc_id;
                        if (MessageObject.isVoiceMessage(newMessage) || MessageObject.isRoundVideoMessage(newMessage)) {
                            newMessage.media_unread = true;
                        }
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaExternalDocument) {
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        newMessage.message = str;
                        newMessage.media.document = new TLRPC.TL_document();
                        newMessage.media.document.id = decryptedMessage.media.id;
                        newMessage.media.document.access_hash = decryptedMessage.media.access_hash;
                        newMessage.media.document.file_reference = new byte[0];
                        newMessage.media.document.date = decryptedMessage.media.date;
                        newMessage.media.document.attributes = decryptedMessage.media.attributes;
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        newMessage.media.document.dc_id = decryptedMessage.media.dc_id;
                        newMessage.media.document.size = decryptedMessage.media.size;
                        newMessage.media.document.thumbs.add(((TLRPC.TL_decryptedMessageMediaExternalDocument) decryptedMessage.media).thumb);
                        newMessage.media.document.flags |= 1;
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = str;
                        }
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaAudio) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        newMessage.media.document = new TLRPC.TL_documentEncrypted();
                        newMessage.media.document.key = decryptedMessage.media.key;
                        newMessage.media.document.iv = decryptedMessage.media.iv;
                        newMessage.media.document.id = encryptedFile.id;
                        newMessage.media.document.access_hash = encryptedFile.access_hash;
                        newMessage.media.document.date = i2;
                        newMessage.media.document.size = encryptedFile.size;
                        newMessage.media.document.dc_id = encryptedFile.dc_id;
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        if (decryptedMessage.media.caption != null) {
                            str = decryptedMessage.media.caption;
                        }
                        newMessage.message = str;
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = "audio/ogg";
                        }
                        TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
                        attributeAudio.duration = decryptedMessage.media.duration;
                        attributeAudio.voice = true;
                        newMessage.media.document.attributes.add(attributeAudio);
                        if (newMessage.ttl != 0) {
                            newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                        }
                        if (newMessage.media.document.thumbs.isEmpty()) {
                            TLRPC.PhotoSize thumb4 = new TLRPC.TL_photoSizeEmpty();
                            thumb4.type = "s";
                            newMessage.media.document.thumbs.add(thumb4);
                        }
                    } else if (!(decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaVenue)) {
                        return null;
                    } else {
                        newMessage.media = new TLRPC.TL_messageMediaVenue();
                        newMessage.media.geo = new TLRPC.TL_geoPoint();
                        newMessage.media.geo.lat = decryptedMessage.media.lat;
                        newMessage.media.geo._long = decryptedMessage.media._long;
                        newMessage.media.title = decryptedMessage.media.title;
                        newMessage.media.address = decryptedMessage.media.address;
                        newMessage.media.provider = decryptedMessage.media.provider;
                        newMessage.media.venue_id = decryptedMessage.media.venue_id;
                        newMessage.media.venue_type = str;
                    }
                }
                if (newMessage.ttl != 0 && newMessage.media.ttl_seconds == 0) {
                    newMessage.media.ttl_seconds = newMessage.ttl;
                    newMessage.media.flags |= 4;
                }
                if (newMessage.message != null) {
                    newMessage.message = newMessage.message.replace(8238, ' ');
                }
                return newMessage;
            }
            int from_id2 = from_id;
            if (tLObject instanceof TLRPC.TL_decryptedMessageService) {
                TLRPC.TL_decryptedMessageService serviceMessage = (TLRPC.TL_decryptedMessageService) tLObject;
                if ((serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) || (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) {
                    TLRPC.TL_messageService newMessage2 = new TLRPC.TL_messageService();
                    if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                        newMessage2.action = new TLRPC.TL_messageEncryptedAction();
                        if (serviceMessage.action.ttl_seconds < 0 || serviceMessage.action.ttl_seconds > 31536000) {
                            serviceMessage.action.ttl_seconds = 31536000;
                        }
                        encryptedChat.ttl = serviceMessage.action.ttl_seconds;
                        newMessage2.action.encryptedAction = serviceMessage.action;
                        getMessagesStorage().updateEncryptedChatTTL(encryptedChat);
                    } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) {
                        newMessage2.action = new TLRPC.TL_messageEncryptedAction();
                        newMessage2.action.encryptedAction = serviceMessage.action;
                    }
                    int newMessageId2 = getUserConfig().getNewMessageId();
                    newMessage2.id = newMessageId2;
                    newMessage2.local_id = newMessageId2;
                    getUserConfig().saveConfig(false);
                    newMessage2.unread = true;
                    newMessage2.flags = 256;
                    newMessage2.date = i2;
                    newMessage2.from_id = from_id2;
                    newMessage2.to_id = new TLRPC.TL_peerUser();
                    newMessage2.to_id.user_id = getUserConfig().getClientUserId();
                    newMessage2.dialog_id = ((long) encryptedChat.id) << 32;
                    return newMessage2;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionFlushHistory) {
                    AndroidUtilities.runOnUIThread(new Runnable(((long) encryptedChat.id) << 32) {
                        private final /* synthetic */ long f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SecretChatHelper.this.lambda$processDecryptedObject$11$SecretChatHelper(this.f$1);
                        }
                    });
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionDeleteMessages) {
                    if (serviceMessage.action.random_ids.isEmpty()) {
                        return null;
                    }
                    this.pendingEncMessagesToDelete.addAll(serviceMessage.action.random_ids);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionReadMessages) {
                    if (serviceMessage.action.random_ids.isEmpty()) {
                        return null;
                    }
                    int time = getConnectionsManager().getCurrentTime();
                    getMessagesStorage().createTaskForSecretChat(encryptedChat.id, time, time, 1, serviceMessage.action.random_ids);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer) {
                    applyPeerLayer(encryptedChat, serviceMessage.action.layer);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionRequestKey) {
                    if (encryptedChat.exchange_id != 0) {
                        if (encryptedChat.exchange_id <= serviceMessage.action.exchange_id) {
                            sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, encryptedChat.exchange_id);
                        } else if (!BuildVars.LOGS_ENABLED) {
                            return null;
                        } else {
                            FileLog.d("we already have request key with higher exchange_id");
                            return null;
                        }
                    }
                    byte[] salt = new byte[256];
                    Utilities.random.nextBytes(salt);
                    BigInteger p = new BigInteger(1, getMessagesStorage().getSecretPBytes());
                    BigInteger g_b = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, salt), p);
                    BigInteger g_a = new BigInteger(1, serviceMessage.action.g_a);
                    if (!Utilities.isGoodGaAndGb(g_a, p)) {
                        sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                        return null;
                    }
                    byte[] g_b_bytes = g_b.toByteArray();
                    if (g_b_bytes.length > 256) {
                        byte[] correctedAuth = new byte[256];
                        i = 1;
                        System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                        g_b_bytes = correctedAuth;
                    } else {
                        i = 1;
                    }
                    byte[] authKey = g_a.modPow(new BigInteger(i, salt), p).toByteArray();
                    if (authKey.length > 256) {
                        byte[] correctedAuth2 = new byte[256];
                        System.arraycopy(authKey, authKey.length - 256, correctedAuth2, 0, 256);
                        authKey = correctedAuth2;
                    } else if (authKey.length < 256) {
                        byte[] correctedAuth3 = new byte[256];
                        System.arraycopy(authKey, 0, correctedAuth3, 256 - authKey.length, authKey.length);
                        for (int a = 0; a < 256 - authKey.length; a++) {
                            correctedAuth3[a] = 0;
                        }
                        authKey = correctedAuth3;
                    }
                    byte[] authKeyHash = Utilities.computeSHA1(authKey);
                    byte[] authKeyId = new byte[8];
                    System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                    byte[] bArr = salt;
                    BigInteger bigInteger = p;
                    encryptedChat.exchange_id = serviceMessage.action.exchange_id;
                    encryptedChat.future_auth_key = authKey;
                    encryptedChat.future_key_fingerprint = Utilities.bytesToLong(authKeyId);
                    encryptedChat.g_a_or_b = g_b_bytes;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                    sendAcceptKeyMessage(encryptedChat, (TLRPC.Message) null);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionAcceptKey) {
                    if (encryptedChat.exchange_id == serviceMessage.action.exchange_id) {
                        BigInteger p2 = new BigInteger(1, getMessagesStorage().getSecretPBytes());
                        BigInteger i_authKey = new BigInteger(1, serviceMessage.action.g_b);
                        if (!Utilities.isGoodGaAndGb(i_authKey, p2)) {
                            encryptedChat.future_auth_key = new byte[256];
                            encryptedChat.future_key_fingerprint = 0;
                            encryptedChat.exchange_id = 0;
                            getMessagesStorage().updateEncryptedChat(encryptedChat);
                            sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                            return null;
                        }
                        byte[] authKey2 = i_authKey.modPow(new BigInteger(1, encryptedChat.a_or_b), p2).toByteArray();
                        if (authKey2.length > 256) {
                            byte[] correctedAuth4 = new byte[256];
                            System.arraycopy(authKey2, authKey2.length - 256, correctedAuth4, 0, 256);
                            authKey2 = correctedAuth4;
                        } else if (authKey2.length < 256) {
                            byte[] correctedAuth5 = new byte[256];
                            byte b = 0;
                            System.arraycopy(authKey2, 0, correctedAuth5, 256 - authKey2.length, authKey2.length);
                            int a2 = 0;
                            while (a2 < 256 - authKey2.length) {
                                correctedAuth5[a2] = b;
                                a2++;
                                b = 0;
                            }
                            authKey2 = correctedAuth5;
                        }
                        byte[] authKeyHash2 = Utilities.computeSHA1(authKey2);
                        byte[] authKeyId2 = new byte[8];
                        System.arraycopy(authKeyHash2, authKeyHash2.length - 8, authKeyId2, 0, 8);
                        long fingerprint = Utilities.bytesToLong(authKeyId2);
                        if (serviceMessage.action.key_fingerprint == fingerprint) {
                            encryptedChat.future_auth_key = authKey2;
                            encryptedChat.future_key_fingerprint = fingerprint;
                            getMessagesStorage().updateEncryptedChat(encryptedChat);
                            sendCommitKeyMessage(encryptedChat, (TLRPC.Message) null);
                            return null;
                        }
                        encryptedChat.future_auth_key = new byte[256];
                        encryptedChat.future_key_fingerprint = 0;
                        encryptedChat.exchange_id = 0;
                        getMessagesStorage().updateEncryptedChat(encryptedChat);
                        sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                        return null;
                    }
                    encryptedChat.future_auth_key = new byte[256];
                    encryptedChat.future_key_fingerprint = 0;
                    encryptedChat.exchange_id = 0;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                    sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionCommitKey) {
                    if (encryptedChat.exchange_id == serviceMessage.action.exchange_id && encryptedChat.future_key_fingerprint == serviceMessage.action.key_fingerprint) {
                        long old_fingerpring = encryptedChat.key_fingerprint;
                        byte[] old_key = encryptedChat.auth_key;
                        encryptedChat.key_fingerprint = encryptedChat.future_key_fingerprint;
                        encryptedChat.auth_key = encryptedChat.future_auth_key;
                        encryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
                        encryptedChat.future_auth_key = old_key;
                        encryptedChat.future_key_fingerprint = old_fingerpring;
                        encryptedChat.key_use_count_in = 0;
                        encryptedChat.key_use_count_out = 0;
                        encryptedChat.exchange_id = 0;
                        getMessagesStorage().updateEncryptedChat(encryptedChat);
                        sendNoopMessage(encryptedChat, (TLRPC.Message) null);
                        return null;
                    }
                    encryptedChat.future_auth_key = new byte[256];
                    encryptedChat.future_key_fingerprint = 0;
                    encryptedChat.exchange_id = 0;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                    sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionAbortKey) {
                    if (encryptedChat.exchange_id != serviceMessage.action.exchange_id) {
                        return null;
                    }
                    encryptedChat.future_auth_key = new byte[256];
                    encryptedChat.future_key_fingerprint = 0;
                    encryptedChat.exchange_id = 0;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                    return null;
                } else if ((serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionNoop) || !(serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionResend) || serviceMessage.action.end_seq_no < encryptedChat.in_seq_no || serviceMessage.action.end_seq_no < serviceMessage.action.start_seq_no) {
                    return null;
                } else {
                    if (serviceMessage.action.start_seq_no < encryptedChat.in_seq_no) {
                        serviceMessage.action.start_seq_no = encryptedChat.in_seq_no;
                    }
                    resendMessages(serviceMessage.action.start_seq_no, serviceMessage.action.end_seq_no, encryptedChat);
                    return null;
                }
            } else {
                if (!BuildVars.LOGS_ENABLED) {
                    return null;
                }
                FileLog.e("unknown message " + tLObject);
                return null;
            }
        } else if (!BuildVars.LOGS_ENABLED) {
            return null;
        } else {
            FileLog.e("unknown TLObject");
            return null;
        }
    }

    public /* synthetic */ void lambda$processDecryptedObject$11$SecretChatHelper(long did) {
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
        if (dialog != null) {
            dialog.unread_count = 0;
            getMessagesController().dialogMessage.remove(dialog.id);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(did) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$10$SecretChatHelper(this.f$1);
            }
        });
        getMessagesStorage().deleteDialog(did, 1);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(did), false);
    }

    public /* synthetic */ void lambda$null$10$SecretChatHelper(long did) {
        AndroidUtilities.runOnUIThread(new Runnable(did) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$9$SecretChatHelper(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$SecretChatHelper(long did) {
        getNotificationsController().processReadMessages((SparseLongArray) null, did, 0, Integer.MAX_VALUE, false);
        LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray<>(1);
        dialogsToUpdate.put(did, 0);
        getNotificationsController().processDialogsUpdateRead(dialogsToUpdate);
    }

    private TLRPC.Message createDeleteMessage(int mid, int seq_out, int seq_in, long random_id, TLRPC.EncryptedChat encryptedChat) {
        TLRPC.TL_messageService newMsg = new TLRPC.TL_messageService();
        newMsg.action = new TLRPC.TL_messageEncryptedAction();
        newMsg.action.encryptedAction = new TLRPC.TL_decryptedMessageActionDeleteMessages();
        newMsg.action.encryptedAction.random_ids.add(Long.valueOf(random_id));
        newMsg.id = mid;
        newMsg.local_id = mid;
        newMsg.from_id = getUserConfig().getClientUserId();
        newMsg.unread = true;
        newMsg.out = true;
        newMsg.flags = 256;
        newMsg.dialog_id = ((long) encryptedChat.id) << 32;
        newMsg.to_id = new TLRPC.TL_peerUser();
        newMsg.send_state = 1;
        newMsg.seq_in = seq_in;
        newMsg.seq_out = seq_out;
        if (encryptedChat.participant_id == getUserConfig().getClientUserId()) {
            newMsg.to_id.user_id = encryptedChat.admin_id;
        } else {
            newMsg.to_id.user_id = encryptedChat.participant_id;
        }
        newMsg.date = 0;
        newMsg.random_id = random_id;
        return newMsg;
    }

    private void resendMessages(int startSeq, int endSeq, TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat != null && endSeq - startSeq >= 0) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(startSeq, encryptedChat, endSeq) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ TLRPC.EncryptedChat f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$resendMessages$14$SecretChatHelper(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$resendMessages$14$SecretChatHelper(int startSeq, TLRPC.EncryptedChat encryptedChat, int endSeq) {
        long dialog_id;
        boolean exists;
        ArrayList<TLRPC.Message> messages;
        SparseArray<TLRPC.Message> messagesToResend;
        int seq_out;
        TLRPC.Message message;
        TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
        int sSeq = startSeq;
        try {
            if (encryptedChat2.admin_id == getUserConfig().getClientUserId() && sSeq % 2 == 0) {
                sSeq++;
            }
            boolean z = false;
            int i = 1;
            int i2 = 3;
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", new Object[]{Integer.valueOf(encryptedChat2.id), Integer.valueOf(sSeq), Integer.valueOf(sSeq), Integer.valueOf(endSeq), Integer.valueOf(endSeq)}), new Object[0]);
            boolean exists2 = cursor.next();
            cursor.dispose();
            if (!exists2) {
                long dialog_id2 = ((long) encryptedChat2.id) << 32;
                SparseArray<TLRPC.Message> messagesToResend2 = new SparseArray<>();
                ArrayList<TLRPC.Message> messages2 = new ArrayList<>();
                for (int a = sSeq; a < endSeq; a += 2) {
                    messagesToResend2.put(a, (Object) null);
                }
                SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms as r ON r.mid = s.mid LEFT JOIN messages as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[]{Long.valueOf(dialog_id2), Integer.valueOf(sSeq), Integer.valueOf(endSeq)}), new Object[0]);
                while (cursor2.next()) {
                    long random_id = cursor2.longValue(i);
                    if (random_id == 0) {
                        random_id = Utilities.random.nextLong();
                    }
                    int seq_in = cursor2.intValue(2);
                    int seq_out2 = cursor2.intValue(i2);
                    long random_id2 = random_id;
                    int mid = cursor2.intValue(5);
                    NativeByteBuffer data = cursor2.byteBufferValue(z ? 1 : 0);
                    if (data != null) {
                        TLRPC.Message message2 = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                        message2.readAttachPath(data, getUserConfig().clientUserId);
                        data.reuse();
                        message2.random_id = random_id2;
                        message2.dialog_id = dialog_id2;
                        message2.seq_in = seq_in;
                        seq_out = seq_out2;
                        message2.seq_out = seq_out;
                        NativeByteBuffer data2 = data;
                        exists = exists2;
                        message2.ttl = cursor2.intValue(4);
                        messages = messages2;
                        dialog_id = dialog_id2;
                        message = message2;
                        NativeByteBuffer nativeByteBuffer = data2;
                        messagesToResend = messagesToResend2;
                    } else {
                        exists = exists2;
                        seq_out = seq_out2;
                        messages = messages2;
                        dialog_id = dialog_id2;
                        NativeByteBuffer nativeByteBuffer2 = data;
                        messagesToResend = messagesToResend2;
                        message = createDeleteMessage(mid, seq_out, seq_in, random_id2, encryptedChat);
                    }
                    messages.add(message);
                    messagesToResend.remove(seq_out);
                    int i3 = endSeq;
                    messagesToResend2 = messagesToResend;
                    messages2 = messages;
                    exists2 = exists;
                    dialog_id2 = dialog_id;
                    z = false;
                    i = 1;
                    i2 = 3;
                }
                SparseArray<TLRPC.Message> messagesToResend3 = messagesToResend2;
                boolean z2 = exists2;
                ArrayList<TLRPC.Message> messages3 = messages2;
                cursor2.dispose();
                if (messagesToResend3.size() != 0) {
                    for (int a2 = 0; a2 < messagesToResend3.size(); a2++) {
                        messages3.add(createDeleteMessage(getUserConfig().getNewMessageId(), messagesToResend3.keyAt(a2), 0, Utilities.random.nextLong(), encryptedChat));
                    }
                    getUserConfig().saveConfig(false);
                }
                Collections.sort(messages3, $$Lambda$SecretChatHelper$HQPSJa2JvYy6XrQB3JyDM4PlM.INSTANCE);
                ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
                encryptedChats.add(encryptedChat2);
                try {
                    AndroidUtilities.runOnUIThread(new Runnable(messages3) {
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SecretChatHelper.this.lambda$null$13$SecretChatHelper(this.f$1);
                        }
                    });
                    getSendMessagesHelper().processUnsentMessages(messages3, (ArrayList<TLRPC.Message>) null, new ArrayList(), new ArrayList(), encryptedChats);
                    getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[]{Integer.valueOf(encryptedChat2.id), Integer.valueOf(sSeq), Integer.valueOf(endSeq)})).stepThis().dispose();
                } catch (Exception e) {
                    e = e;
                }
            }
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$13$SecretChatHelper(ArrayList messages) {
        for (int a = 0; a < messages.size(); a++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC.Message) messages.get(a), false);
            messageObject.resendAsIs = true;
            getSendMessagesHelper().retrySendMessage(messageObject, true);
        }
    }

    public void checkSecretHoles(TLRPC.EncryptedChat chat, ArrayList<TLRPC.Message> messages) {
        ArrayList<TL_decryptedMessageHolder> holes = this.secretHolesQueue.get(chat.id);
        if (holes != null) {
            Collections.sort(holes, $$Lambda$SecretChatHelper$HoLrpyxnpt_qc81uJm8BJfxwok.INSTANCE);
            boolean update = false;
            int a = 0;
            while (a < holes.size()) {
                TL_decryptedMessageHolder holder = holes.get(a);
                if (holder.layer.out_seq_no != chat.seq_in && chat.seq_in != holder.layer.out_seq_no - 2) {
                    break;
                }
                applyPeerLayer(chat, holder.layer.layer);
                chat.seq_in = holder.layer.out_seq_no;
                chat.in_seq_no = holder.layer.in_seq_no;
                holes.remove(a);
                int a2 = a - 1;
                update = true;
                if (holder.decryptedWithVersion == 2) {
                    chat.mtproto_seq = Math.min(chat.mtproto_seq, chat.seq_in);
                }
                TLRPC.Message message = processDecryptedObject(chat, holder.file, holder.date, holder.layer.message, holder.new_key_used);
                if (message != null) {
                    messages.add(message);
                }
                a = a2 + 1;
            }
            if (holes.isEmpty() != 0) {
                this.secretHolesQueue.remove(chat.id);
            }
            if (update) {
                getMessagesStorage().updateEncryptedChatSeq(chat, true);
            }
        }
    }

    static /* synthetic */ int lambda$checkSecretHoles$15(TL_decryptedMessageHolder lhs, TL_decryptedMessageHolder rhs) {
        if (lhs.layer.out_seq_no > rhs.layer.out_seq_no) {
            return 1;
        }
        if (lhs.layer.out_seq_no < rhs.layer.out_seq_no) {
            return -1;
        }
        return 0;
    }

    private boolean decryptWithMtProtoVersion(NativeByteBuffer is, byte[] keyToDecrypt, byte[] messageKey, int version, boolean incoming, boolean encryptOnError) {
        boolean incoming2;
        NativeByteBuffer nativeByteBuffer = is;
        byte[] bArr = messageKey;
        int i = version;
        if (i == 1) {
            incoming2 = false;
        } else {
            incoming2 = incoming;
        }
        MessageKeyData keyData = MessageKeyData.generateMessageKeyData(keyToDecrypt, bArr, incoming2, i);
        Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, false, false, 24, is.limit() - 24);
        int len = nativeByteBuffer.readInt32(false);
        if (i == 2) {
            if (!Utilities.arraysEquals(bArr, 0, Utilities.computeSHA256(keyToDecrypt, (incoming2 ? 8 : 0) + 88, 32, nativeByteBuffer.buffer, 24, nativeByteBuffer.buffer.limit()), 8)) {
                if (encryptOnError) {
                    Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                    nativeByteBuffer.position(24);
                }
                return false;
            }
        } else {
            int l = len + 28;
            if (l < nativeByteBuffer.buffer.limit() - 15 || l > nativeByteBuffer.buffer.limit()) {
                l = nativeByteBuffer.buffer.limit();
            }
            byte[] messageKeyFull = Utilities.computeSHA1(nativeByteBuffer.buffer, 24, l);
            if (!Utilities.arraysEquals(bArr, 0, messageKeyFull, messageKeyFull.length - 16)) {
                if (encryptOnError) {
                    Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                    nativeByteBuffer.position(24);
                }
                return false;
            }
        }
        if (len <= 0 || len > is.limit() - 28) {
            return false;
        }
        int padding = (is.limit() - 28) - len;
        if ((i != 2 || (padding >= 12 && padding <= 1024)) && (i != 1 || padding <= 15)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0249 A[Catch:{ Exception -> 0x0296 }] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x026c A[Catch:{ Exception -> 0x0296 }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00ec A[Catch:{ Exception -> 0x0296 }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x011d A[Catch:{ Exception -> 0x0296 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.Message> decryptMessage(im.bclpbkiauv.tgnet.TLRPC.EncryptedMessage r25) {
        /*
            r24 = this;
            r14 = r24
            r15 = r25
            java.lang.String r0 = " out_seq = "
            im.bclpbkiauv.messenger.MessagesController r1 = r24.getMessagesController()
            int r2 = r15.chat_id
            r13 = 1
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r12 = r1.getEncryptedChatDB(r2, r13)
            r16 = 0
            if (r12 == 0) goto L_0x029e
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_encryptedChatDiscarded
            if (r1 == 0) goto L_0x001c
            r1 = r12
            goto L_0x029f
        L_0x001c:
            im.bclpbkiauv.tgnet.NativeByteBuffer r1 = new im.bclpbkiauv.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0298 }
            byte[] r2 = r15.bytes     // Catch:{ Exception -> 0x0298 }
            int r2 = r2.length     // Catch:{ Exception -> 0x0298 }
            r1.<init>((int) r2)     // Catch:{ Exception -> 0x0298 }
            r11 = r1
            byte[] r1 = r15.bytes     // Catch:{ Exception -> 0x0298 }
            r11.writeBytes((byte[]) r1)     // Catch:{ Exception -> 0x0298 }
            r10 = 0
            r11.position(r10)     // Catch:{ Exception -> 0x0298 }
            long r1 = r11.readInt64(r10)     // Catch:{ Exception -> 0x0298 }
            r17 = r1
            r1 = 0
            r2 = 0
            long r3 = r12.key_fingerprint     // Catch:{ Exception -> 0x0298 }
            int r5 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r5 != 0) goto L_0x0047
            byte[] r3 = r12.auth_key     // Catch:{ Exception -> 0x0043 }
            r1 = r3
            r19 = r1
            r9 = r2
            goto L_0x0060
        L_0x0043:
            r0 = move-exception
            r1 = r12
            goto L_0x029a
        L_0x0047:
            long r3 = r12.future_key_fingerprint     // Catch:{ Exception -> 0x0298 }
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x005d
            long r3 = r12.future_key_fingerprint     // Catch:{ Exception -> 0x0043 }
            int r5 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r5 != 0) goto L_0x005d
            byte[] r3 = r12.future_auth_key     // Catch:{ Exception -> 0x0043 }
            r1 = r3
            r2 = 1
            r19 = r1
            r9 = r2
            goto L_0x0060
        L_0x005d:
            r19 = r1
            r9 = r2
        L_0x0060:
            int r1 = r12.layer     // Catch:{ Exception -> 0x0298 }
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r1)     // Catch:{ Exception -> 0x0298 }
            r2 = 73
            r8 = 2
            if (r1 < r2) goto L_0x006d
            r1 = 2
            goto L_0x006e
        L_0x006d:
            r1 = 1
        L_0x006e:
            r7 = r1
            r5 = r7
            if (r19 == 0) goto L_0x0274
            r1 = 16
            byte[] r4 = r11.readData(r1, r10)     // Catch:{ Exception -> 0x0298 }
            int r1 = r12.admin_id     // Catch:{ Exception -> 0x0298 }
            im.bclpbkiauv.messenger.UserConfig r2 = r24.getUserConfig()     // Catch:{ Exception -> 0x0298 }
            int r2 = r2.getClientUserId()     // Catch:{ Exception -> 0x0298 }
            if (r1 != r2) goto L_0x0086
            r6 = 1
            goto L_0x0087
        L_0x0086:
            r6 = 0
        L_0x0087:
            r1 = 1
            if (r5 != r8) goto L_0x0092
            int r2 = r12.mtproto_seq     // Catch:{ Exception -> 0x0043 }
            if (r2 == 0) goto L_0x0092
            r1 = 0
            r20 = r1
            goto L_0x0094
        L_0x0092:
            r20 = r1
        L_0x0094:
            r1 = r24
            r2 = r11
            r3 = r19
            r21 = r5
            r5 = r7
            r13 = r7
            r7 = r20
            boolean r1 = r1.decryptWithMtProtoVersion(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0298 }
            if (r1 != 0) goto L_0x00ec
            if (r13 != r8) goto L_0x00d2
            r5 = 1
            if (r20 == 0) goto L_0x00c9
            r1 = 1
            r2 = 0
            r7 = r24
            r3 = 2
            r8 = r11
            r3 = r9
            r9 = r19
            r10 = r4
            r23 = r11
            r11 = r1
            r1 = r12
            r12 = r6
            r21 = r5
            r22 = r13
            r5 = 1
            r13 = r2
            boolean r2 = r7.decryptWithMtProtoVersion(r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0296 }
            if (r2 != 0) goto L_0x00c6
            goto L_0x00d1
        L_0x00c6:
            r2 = r21
            goto L_0x00f5
        L_0x00c9:
            r21 = r5
            r3 = r9
            r23 = r11
            r1 = r12
            r22 = r13
        L_0x00d1:
            return r16
        L_0x00d2:
            r3 = r9
            r23 = r11
            r1 = r12
            r22 = r13
            r5 = 1
            r2 = 2
            r11 = 2
            r7 = r24
            r8 = r23
            r9 = r19
            r10 = r4
            r12 = r6
            r13 = r20
            boolean r7 = r7.decryptWithMtProtoVersion(r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0296 }
            if (r7 != 0) goto L_0x00f5
            return r16
        L_0x00ec:
            r3 = r9
            r23 = r11
            r1 = r12
            r22 = r13
            r5 = 1
            r2 = r21
        L_0x00f5:
            im.bclpbkiauv.tgnet.TLClassStore r7 = im.bclpbkiauv.tgnet.TLClassStore.Instance()     // Catch:{ Exception -> 0x0296 }
            r13 = r23
            r8 = 0
            int r9 = r13.readInt32(r8)     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.tgnet.TLObject r7 = r7.TLdeserialize(r13, r9, r8)     // Catch:{ Exception -> 0x0296 }
            r13.reuse()     // Catch:{ Exception -> 0x0296 }
            if (r3 != 0) goto L_0x0119
            int r8 = r1.layer     // Catch:{ Exception -> 0x0296 }
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r8)     // Catch:{ Exception -> 0x0296 }
            r9 = 20
            if (r8 < r9) goto L_0x0119
            short r8 = r1.key_use_count_in     // Catch:{ Exception -> 0x0296 }
            int r8 = r8 + r5
            short r8 = (short) r8     // Catch:{ Exception -> 0x0296 }
            r1.key_use_count_in = r8     // Catch:{ Exception -> 0x0296 }
        L_0x0119:
            boolean r8 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageLayer     // Catch:{ Exception -> 0x0296 }
            if (r8 == 0) goto L_0x0249
            r8 = r7
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageLayer r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageLayer) r8     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.seq_in     // Catch:{ Exception -> 0x0296 }
            if (r9 != 0) goto L_0x013d
            int r9 = r1.seq_out     // Catch:{ Exception -> 0x0296 }
            if (r9 != 0) goto L_0x013d
            int r9 = r1.admin_id     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.messenger.UserConfig r10 = r24.getUserConfig()     // Catch:{ Exception -> 0x0296 }
            int r10 = r10.getClientUserId()     // Catch:{ Exception -> 0x0296 }
            if (r9 != r10) goto L_0x013a
            r1.seq_out = r5     // Catch:{ Exception -> 0x0296 }
            r9 = -2
            r1.seq_in = r9     // Catch:{ Exception -> 0x0296 }
            goto L_0x013d
        L_0x013a:
            r9 = -1
            r1.seq_in = r9     // Catch:{ Exception -> 0x0296 }
        L_0x013d:
            byte[] r9 = r8.random_bytes     // Catch:{ Exception -> 0x0296 }
            int r9 = r9.length     // Catch:{ Exception -> 0x0296 }
            r10 = 15
            if (r9 >= r10) goto L_0x014e
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0296 }
            if (r0 == 0) goto L_0x014d
            java.lang.String r0 = "got random bytes less than needed"
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0296 }
        L_0x014d:
            return r16
        L_0x014e:
            boolean r9 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0296 }
            if (r9 == 0) goto L_0x018e
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0296 }
            r9.<init>()     // Catch:{ Exception -> 0x0296 }
            java.lang.String r10 = "current chat in_seq = "
            r9.append(r10)     // Catch:{ Exception -> 0x0296 }
            int r10 = r1.seq_in     // Catch:{ Exception -> 0x0296 }
            r9.append(r10)     // Catch:{ Exception -> 0x0296 }
            r9.append(r0)     // Catch:{ Exception -> 0x0296 }
            int r10 = r1.seq_out     // Catch:{ Exception -> 0x0296 }
            r9.append(r10)     // Catch:{ Exception -> 0x0296 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.messenger.FileLog.d(r9)     // Catch:{ Exception -> 0x0296 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0296 }
            r9.<init>()     // Catch:{ Exception -> 0x0296 }
            java.lang.String r10 = "got message with in_seq = "
            r9.append(r10)     // Catch:{ Exception -> 0x0296 }
            int r10 = r8.in_seq_no     // Catch:{ Exception -> 0x0296 }
            r9.append(r10)     // Catch:{ Exception -> 0x0296 }
            r9.append(r0)     // Catch:{ Exception -> 0x0296 }
            int r0 = r8.out_seq_no     // Catch:{ Exception -> 0x0296 }
            r9.append(r0)     // Catch:{ Exception -> 0x0296 }
            java.lang.String r0 = r9.toString()     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x0296 }
        L_0x018e:
            int r0 = r8.out_seq_no     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.seq_in     // Catch:{ Exception -> 0x0296 }
            if (r0 > r9) goto L_0x0195
            return r16
        L_0x0195:
            if (r2 != r5) goto L_0x01a2
            int r0 = r1.mtproto_seq     // Catch:{ Exception -> 0x0296 }
            if (r0 == 0) goto L_0x01a2
            int r0 = r8.out_seq_no     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.mtproto_seq     // Catch:{ Exception -> 0x0296 }
            if (r0 < r9) goto L_0x01a2
            return r16
        L_0x01a2:
            int r0 = r1.seq_in     // Catch:{ Exception -> 0x0296 }
            int r9 = r8.out_seq_no     // Catch:{ Exception -> 0x0296 }
            r10 = 2
            int r9 = r9 - r10
            if (r0 == r9) goto L_0x0224
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0296 }
            if (r0 == 0) goto L_0x01b3
            java.lang.String r0 = "got hole"
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0296 }
        L_0x01b3:
            android.util.SparseArray<java.util.ArrayList<im.bclpbkiauv.messenger.SecretChatHelper$TL_decryptedMessageHolder>> r0 = r14.secretHolesQueue     // Catch:{ Exception -> 0x0296 }
            int r5 = r1.id     // Catch:{ Exception -> 0x0296 }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ Exception -> 0x0296 }
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ Exception -> 0x0296 }
            if (r0 != 0) goto L_0x01cc
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x0296 }
            r5.<init>()     // Catch:{ Exception -> 0x0296 }
            r0 = r5
            android.util.SparseArray<java.util.ArrayList<im.bclpbkiauv.messenger.SecretChatHelper$TL_decryptedMessageHolder>> r5 = r14.secretHolesQueue     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.id     // Catch:{ Exception -> 0x0296 }
            r5.put(r9, r0)     // Catch:{ Exception -> 0x0296 }
        L_0x01cc:
            int r5 = r0.size()     // Catch:{ Exception -> 0x0296 }
            r9 = 4
            if (r5 < r9) goto L_0x020d
            android.util.SparseArray<java.util.ArrayList<im.bclpbkiauv.messenger.SecretChatHelper$TL_decryptedMessageHolder>> r5 = r14.secretHolesQueue     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.id     // Catch:{ Exception -> 0x0296 }
            r5.remove(r9)     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.tgnet.TLRPC$TL_encryptedChatDiscarded r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_encryptedChatDiscarded     // Catch:{ Exception -> 0x0296 }
            r5.<init>()     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.id     // Catch:{ Exception -> 0x0296 }
            r5.id = r9     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.user_id     // Catch:{ Exception -> 0x0296 }
            r5.user_id = r9     // Catch:{ Exception -> 0x0296 }
            byte[] r9 = r1.auth_key     // Catch:{ Exception -> 0x0296 }
            r5.auth_key = r9     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.key_create_date     // Catch:{ Exception -> 0x0296 }
            r5.key_create_date = r9     // Catch:{ Exception -> 0x0296 }
            short r9 = r1.key_use_count_in     // Catch:{ Exception -> 0x0296 }
            r5.key_use_count_in = r9     // Catch:{ Exception -> 0x0296 }
            short r9 = r1.key_use_count_out     // Catch:{ Exception -> 0x0296 }
            r5.key_use_count_out = r9     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.seq_in     // Catch:{ Exception -> 0x0296 }
            r5.seq_in = r9     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.seq_out     // Catch:{ Exception -> 0x0296 }
            r5.seq_out = r9     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.messenger.-$$Lambda$SecretChatHelper$N916VzMMwQrc-EGrbBJrtEhfqfw r9 = new im.bclpbkiauv.messenger.-$$Lambda$SecretChatHelper$N916VzMMwQrc-EGrbBJrtEhfqfw     // Catch:{ Exception -> 0x0296 }
            r9.<init>(r5)     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r9)     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.id     // Catch:{ Exception -> 0x0296 }
            r14.declineSecretChat(r9)     // Catch:{ Exception -> 0x0296 }
            return r16
        L_0x020d:
            im.bclpbkiauv.messenger.SecretChatHelper$TL_decryptedMessageHolder r5 = new im.bclpbkiauv.messenger.SecretChatHelper$TL_decryptedMessageHolder     // Catch:{ Exception -> 0x0296 }
            r5.<init>()     // Catch:{ Exception -> 0x0296 }
            r5.layer = r8     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.tgnet.TLRPC$EncryptedFile r9 = r15.file     // Catch:{ Exception -> 0x0296 }
            r5.file = r9     // Catch:{ Exception -> 0x0296 }
            int r9 = r15.date     // Catch:{ Exception -> 0x0296 }
            r5.date = r9     // Catch:{ Exception -> 0x0296 }
            r5.new_key_used = r3     // Catch:{ Exception -> 0x0296 }
            r5.decryptedWithVersion = r2     // Catch:{ Exception -> 0x0296 }
            r0.add(r5)     // Catch:{ Exception -> 0x0296 }
            return r16
        L_0x0224:
            r0 = 2
            if (r2 != r0) goto L_0x0231
            int r0 = r1.mtproto_seq     // Catch:{ Exception -> 0x0296 }
            int r9 = r1.seq_in     // Catch:{ Exception -> 0x0296 }
            int r0 = java.lang.Math.min(r0, r9)     // Catch:{ Exception -> 0x0296 }
            r1.mtproto_seq = r0     // Catch:{ Exception -> 0x0296 }
        L_0x0231:
            int r0 = r8.layer     // Catch:{ Exception -> 0x0296 }
            r14.applyPeerLayer(r1, r0)     // Catch:{ Exception -> 0x0296 }
            int r0 = r8.out_seq_no     // Catch:{ Exception -> 0x0296 }
            r1.seq_in = r0     // Catch:{ Exception -> 0x0296 }
            int r0 = r8.in_seq_no     // Catch:{ Exception -> 0x0296 }
            r1.in_seq_no = r0     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.messenger.MessagesStorage r0 = r24.getMessagesStorage()     // Catch:{ Exception -> 0x0296 }
            r0.updateEncryptedChatSeq(r1, r5)     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessage r0 = r8.message     // Catch:{ Exception -> 0x0296 }
            r7 = r0
            goto L_0x0258
        L_0x0249:
            boolean r0 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageService     // Catch:{ Exception -> 0x0296 }
            if (r0 == 0) goto L_0x0273
            r0 = r7
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageService r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageService) r0     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageAction r0 = r0.action     // Catch:{ Exception -> 0x0296 }
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer     // Catch:{ Exception -> 0x0296 }
            if (r0 != 0) goto L_0x0257
            goto L_0x0273
        L_0x0257:
            r0 = r7
        L_0x0258:
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x0296 }
            r5.<init>()     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.tgnet.TLRPC$EncryptedFile r9 = r15.file     // Catch:{ Exception -> 0x0296 }
            int r10 = r15.date     // Catch:{ Exception -> 0x0296 }
            r7 = r24
            r8 = r1
            r11 = r0
            r12 = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r7.processDecryptedObject(r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x0296 }
            if (r7 == 0) goto L_0x026f
            r5.add(r7)     // Catch:{ Exception -> 0x0296 }
        L_0x026f:
            r14.checkSecretHoles(r1, r5)     // Catch:{ Exception -> 0x0296 }
            return r5
        L_0x0273:
            return r16
        L_0x0274:
            r21 = r5
            r22 = r7
            r3 = r9
            r13 = r11
            r1 = r12
            r5 = 1
            r8 = 0
            r13.reuse()     // Catch:{ Exception -> 0x0296 }
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0296 }
            if (r0 == 0) goto L_0x0295
            java.lang.String r0 = "fingerprint mismatch %x"
            java.lang.Object[] r2 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0296 }
            java.lang.Long r4 = java.lang.Long.valueOf(r17)     // Catch:{ Exception -> 0x0296 }
            r2[r8] = r4     // Catch:{ Exception -> 0x0296 }
            java.lang.String r0 = java.lang.String.format(r0, r2)     // Catch:{ Exception -> 0x0296 }
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0296 }
        L_0x0295:
            goto L_0x029d
        L_0x0296:
            r0 = move-exception
            goto L_0x029a
        L_0x0298:
            r0 = move-exception
            r1 = r12
        L_0x029a:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x029d:
            return r16
        L_0x029e:
            r1 = r12
        L_0x029f:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SecretChatHelper.decryptMessage(im.bclpbkiauv.tgnet.TLRPC$EncryptedMessage):java.util.ArrayList");
    }

    public /* synthetic */ void lambda$decryptMessage$16$SecretChatHelper(TLRPC.TL_encryptedChatDiscarded newChat) {
        getMessagesController().putEncryptedChat(newChat, false);
        getMessagesStorage().updateEncryptedChat(newChat);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void requestNewSecretChatKey(TLRPC.EncryptedChat encryptedChat) {
        if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20) {
            byte[] salt = new byte[256];
            Utilities.random.nextBytes(salt);
            byte[] g_a = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
            if (g_a.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_a, 1, correctedAuth, 0, 256);
                g_a = correctedAuth;
            }
            encryptedChat.exchange_id = getSendMessagesHelper().getNextRandomId();
            encryptedChat.a_or_b = salt;
            encryptedChat.g_a = g_a;
            getMessagesStorage().updateEncryptedChat(encryptedChat);
            sendRequestKeyMessage(encryptedChat, (TLRPC.Message) null);
        }
    }

    public void processAcceptedSecretChat(TLRPC.EncryptedChat encryptedChat) {
        BigInteger p = new BigInteger(1, getMessagesStorage().getSecretPBytes());
        BigInteger i_authKey = new BigInteger(1, encryptedChat.g_a_or_b);
        if (!Utilities.isGoodGaAndGb(i_authKey, p)) {
            declineSecretChat(encryptedChat.id);
            return;
        }
        byte[] authKey = i_authKey.modPow(new BigInteger(1, encryptedChat.a_or_b), p).toByteArray();
        if (authKey.length > 256) {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
            authKey = correctedAuth;
        } else if (authKey.length < 256) {
            byte[] correctedAuth2 = new byte[256];
            System.arraycopy(authKey, 0, correctedAuth2, 256 - authKey.length, authKey.length);
            for (int a = 0; a < 256 - authKey.length; a++) {
                correctedAuth2[a] = 0;
            }
            authKey = correctedAuth2;
        }
        byte[] authKeyHash = Utilities.computeSHA1(authKey);
        byte[] authKeyId = new byte[8];
        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
        if (encryptedChat.key_fingerprint == Utilities.bytesToLong(authKeyId)) {
            encryptedChat.auth_key = authKey;
            encryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
            encryptedChat.seq_in = -2;
            encryptedChat.seq_out = 1;
            getMessagesStorage().updateEncryptedChat(encryptedChat);
            getMessagesController().putEncryptedChat(encryptedChat, false);
            AndroidUtilities.runOnUIThread(new Runnable(encryptedChat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$processAcceptedSecretChat$17$SecretChatHelper(this.f$1);
                }
            });
            return;
        }
        TLRPC.TL_encryptedChatDiscarded newChat = new TLRPC.TL_encryptedChatDiscarded();
        newChat.id = encryptedChat.id;
        newChat.user_id = encryptedChat.user_id;
        newChat.auth_key = encryptedChat.auth_key;
        newChat.key_create_date = encryptedChat.key_create_date;
        newChat.key_use_count_in = encryptedChat.key_use_count_in;
        newChat.key_use_count_out = encryptedChat.key_use_count_out;
        newChat.seq_in = encryptedChat.seq_in;
        newChat.seq_out = encryptedChat.seq_out;
        newChat.admin_id = encryptedChat.admin_id;
        newChat.mtproto_seq = encryptedChat.mtproto_seq;
        getMessagesStorage().updateEncryptedChat(newChat);
        AndroidUtilities.runOnUIThread(new Runnable(newChat) {
            private final /* synthetic */ TLRPC.TL_encryptedChatDiscarded f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SecretChatHelper.this.lambda$processAcceptedSecretChat$18$SecretChatHelper(this.f$1);
            }
        });
        declineSecretChat(encryptedChat.id);
    }

    public /* synthetic */ void lambda$processAcceptedSecretChat$17$SecretChatHelper(TLRPC.EncryptedChat encryptedChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
        sendNotifyLayerMessage(encryptedChat, (TLRPC.Message) null);
    }

    public /* synthetic */ void lambda$processAcceptedSecretChat$18$SecretChatHelper(TLRPC.TL_encryptedChatDiscarded newChat) {
        getMessagesController().putEncryptedChat(newChat, false);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void declineSecretChat(int chat_id) {
        TLRPC.TL_messages_discardEncryption req = new TLRPC.TL_messages_discardEncryption();
        req.chat_id = chat_id;
        getConnectionsManager().sendRequest(req, $$Lambda$SecretChatHelper$yJYgWkM7x19MdGyVSHs6lGA7qno.INSTANCE);
    }

    static /* synthetic */ void lambda$declineSecretChat$19(TLObject response, TLRPC.TL_error error) {
    }

    public void acceptSecretChat(TLRPC.EncryptedChat encryptedChat) {
        if (this.acceptingChats.get(encryptedChat.id) == null) {
            this.acceptingChats.put(encryptedChat.id, encryptedChat);
            TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
            req.random_length = 256;
            req.version = getMessagesStorage().getLastSecretVersion();
            getConnectionsManager().sendRequest(req, new RequestDelegate(encryptedChat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.lambda$acceptSecretChat$22$SecretChatHelper(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$acceptSecretChat$22$SecretChatHelper(TLRPC.EncryptedChat encryptedChat, TLObject response, TLRPC.TL_error error) {
        TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
        TLObject tLObject = response;
        if (error == null) {
            TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(res.p, res.g)) {
                    this.acceptingChats.remove(encryptedChat2.id);
                    declineSecretChat(encryptedChat2.id);
                    return;
                }
                getMessagesStorage().setSecretPBytes(res.p);
                getMessagesStorage().setSecretG(res.g);
                getMessagesStorage().setLastSecretVersion(res.version);
                getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
            }
            byte[] salt = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
            }
            encryptedChat2.a_or_b = salt;
            encryptedChat2.seq_in = -1;
            encryptedChat2.seq_out = 0;
            BigInteger p = new BigInteger(1, getMessagesStorage().getSecretPBytes());
            BigInteger g_b = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, salt), p);
            BigInteger g_a = new BigInteger(1, encryptedChat2.g_a);
            if (!Utilities.isGoodGaAndGb(g_a, p)) {
                this.acceptingChats.remove(encryptedChat2.id);
                declineSecretChat(encryptedChat2.id);
                return;
            }
            byte[] g_b_bytes = g_b.toByteArray();
            if (g_b_bytes.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                g_b_bytes = correctedAuth;
            }
            byte[] authKey = g_a.modPow(new BigInteger(1, salt), p).toByteArray();
            if (authKey.length > 256) {
                byte[] correctedAuth2 = new byte[256];
                System.arraycopy(authKey, authKey.length - 256, correctedAuth2, 0, 256);
                authKey = correctedAuth2;
            } else if (authKey.length < 256) {
                byte[] correctedAuth3 = new byte[256];
                System.arraycopy(authKey, 0, correctedAuth3, 256 - authKey.length, authKey.length);
                for (int a2 = 0; a2 < 256 - authKey.length; a2++) {
                    correctedAuth3[a2] = 0;
                }
                authKey = correctedAuth3;
            }
            byte[] authKeyHash = Utilities.computeSHA1(authKey);
            byte[] authKeyId = new byte[8];
            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
            encryptedChat2.auth_key = authKey;
            encryptedChat2.key_create_date = getConnectionsManager().getCurrentTime();
            TLRPC.TL_messages_acceptEncryption req2 = new TLRPC.TL_messages_acceptEncryption();
            req2.g_b = g_b_bytes;
            req2.peer = new TLRPC.TL_inputEncryptedChat();
            req2.peer.chat_id = encryptedChat2.id;
            req2.peer.access_hash = encryptedChat2.access_hash;
            req2.key_fingerprint = Utilities.bytesToLong(authKeyId);
            FileLog.e("J----------------------> TL_messages_acceptEncryption req");
            getConnectionsManager().sendRequest(req2, new RequestDelegate(encryptedChat2) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.lambda$null$21$SecretChatHelper(this.f$1, tLObject, tL_error);
                }
            });
            return;
        }
        this.acceptingChats.remove(encryptedChat2.id);
    }

    public /* synthetic */ void lambda$null$21$SecretChatHelper(TLRPC.EncryptedChat encryptedChat, TLObject response1, TLRPC.TL_error error1) {
        this.acceptingChats.remove(encryptedChat.id);
        FileLog.e("J----------------------> TL_messages_acceptEncryption res");
        if (error1 == null) {
            TLRPC.EncryptedChat newChat = (TLRPC.EncryptedChat) response1;
            newChat.auth_key = encryptedChat.auth_key;
            newChat.user_id = encryptedChat.user_id;
            newChat.seq_in = encryptedChat.seq_in;
            newChat.seq_out = encryptedChat.seq_out;
            newChat.key_create_date = encryptedChat.key_create_date;
            newChat.key_use_count_in = encryptedChat.key_use_count_in;
            newChat.key_use_count_out = encryptedChat.key_use_count_out;
            getMessagesStorage().updateEncryptedChat(newChat);
            getMessagesController().putEncryptedChat(newChat, false);
            AndroidUtilities.runOnUIThread(new Runnable(newChat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$null$20$SecretChatHelper(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$20$SecretChatHelper(TLRPC.EncryptedChat newChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
        sendNotifyLayerMessage(newChat, (TLRPC.Message) null);
    }

    public void startSecretChat(Context context, TLRPC.User user) {
        if (user != null && context != null) {
            this.startingSecretChat = true;
            AlertDialog progressDialog = new AlertDialog(context, 3);
            TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
            req.random_length = 256;
            req.version = getMessagesStorage().getLastSecretVersion();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(req, new RequestDelegate(context, progressDialog, user) {
                private final /* synthetic */ Context f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ TLRPC.User f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.lambda$startSecretChat$29$SecretChatHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            }, 2)) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    SecretChatHelper.this.lambda$startSecretChat$30$SecretChatHelper(this.f$1, dialogInterface);
                }
            });
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }

    public /* synthetic */ void lambda$startSecretChat$29$SecretChatHelper(Context context, AlertDialog progressDialog, TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        byte[] g_a;
        Context context2 = context;
        AlertDialog alertDialog = progressDialog;
        TLObject tLObject = response;
        if (error == null) {
            TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(res.p, res.g)) {
                    AndroidUtilities.runOnUIThread(new Runnable(context2, alertDialog) {
                        private final /* synthetic */ Context f$0;
                        private final /* synthetic */ AlertDialog f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SecretChatHelper.lambda$null$23(this.f$0, this.f$1);
                        }
                    });
                    return;
                }
                getMessagesStorage().setSecretPBytes(res.p);
                getMessagesStorage().setSecretG(res.g);
                getMessagesStorage().setLastSecretVersion(res.version);
                getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
            }
            byte[] salt = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
            }
            byte[] g_a2 = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
            if (g_a2.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_a2, 1, correctedAuth, 0, 256);
                g_a = correctedAuth;
            } else {
                g_a = g_a2;
            }
            TLRPC.TL_messages_requestEncryption req2 = new TLRPC.TL_messages_requestEncryption();
            req2.g_a = g_a;
            req2.user_id = getMessagesController().getInputUser(user);
            req2.random_id = Utilities.random.nextInt();
            $$Lambda$SecretChatHelper$MCmqB_aj7qDZtiQQGard8ykGnFU r9 = r0;
            TLRPC.messages_DhConfig messages_dhconfig = res;
            ConnectionsManager connectionsManager = getConnectionsManager();
            $$Lambda$SecretChatHelper$MCmqB_aj7qDZtiQQGard8ykGnFU r0 = new RequestDelegate(context, progressDialog, salt, user) {
                private final /* synthetic */ Context f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ byte[] f$3;
                private final /* synthetic */ TLRPC.User f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.lambda$null$27$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                }
            };
            connectionsManager.sendRequest(req2, r9, 2);
            return;
        }
        TLRPC.User user2 = user;
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new Runnable(context2, alertDialog) {
            private final /* synthetic */ Context f$1;
            private final /* synthetic */ AlertDialog f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$28$SecretChatHelper(this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$null$23(Context context, AlertDialog progressDialog) {
        try {
            if (!((Activity) context).isFinishing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$27$SecretChatHelper(Context context, AlertDialog progressDialog, byte[] salt, TLRPC.User user, TLObject response1, TLRPC.TL_error error1) {
        if (error1 == null) {
            AndroidUtilities.runOnUIThread(new Runnable(context, progressDialog, response1, salt, user) {
                private final /* synthetic */ Context f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ byte[] f$4;
                private final /* synthetic */ TLRPC.User f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$null$25$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
            return;
        }
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new Runnable(context, progressDialog) {
            private final /* synthetic */ Context f$1;
            private final /* synthetic */ AlertDialog f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$26$SecretChatHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$25$SecretChatHelper(Context context, AlertDialog progressDialog, TLObject response1, byte[] salt, TLRPC.User user) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        TLRPC.EncryptedChat chat = (TLRPC.EncryptedChat) response1;
        chat.user_id = chat.participant_id;
        chat.seq_in = -2;
        chat.seq_out = 1;
        chat.a_or_b = salt;
        getMessagesController().putEncryptedChat(chat, false);
        TLRPC.Dialog dialog = new TLRPC.TL_dialog();
        dialog.id = DialogObject.makeSecretDialogId(chat.id);
        dialog.unread_count = 0;
        dialog.top_message = 0;
        dialog.last_message_date = getConnectionsManager().getCurrentTime();
        getMessagesController().dialogs_dict.put(dialog.id, dialog);
        getMessagesController().allDialogs.add(dialog);
        getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
        getMessagesStorage().putEncryptedChat(chat, user, dialog);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatCreated, chat);
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                SecretChatHelper.this.lambda$null$24$SecretChatHelper();
            }
        });
    }

    public /* synthetic */ void lambda$null$24$SecretChatHelper() {
        if (!this.delayedEncryptedChatUpdates.isEmpty()) {
            getMessagesController().processUpdateArray(this.delayedEncryptedChatUpdates, (ArrayList<TLRPC.User>) null, (ArrayList<TLRPC.Chat>) null, false, 0);
            this.delayedEncryptedChatUpdates.clear();
        }
    }

    public /* synthetic */ void lambda$null$26$SecretChatHelper(Context context, AlertDialog progressDialog) {
        if (!((Activity) context).isFinishing()) {
            this.startingSecretChat = false;
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("CreateEncryptedChatError", R.string.CreateEncryptedChatError));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            builder.show().setCanceledOnTouchOutside(true);
        }
    }

    public /* synthetic */ void lambda$null$28$SecretChatHelper(Context context, AlertDialog progressDialog) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$startSecretChat$30$SecretChatHelper(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }
}
