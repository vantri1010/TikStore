package im.bclpbkiauv.ui.components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SecretChatHelper;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.CacheControlActivity;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.LanguageSelectActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.NotificationsCustomSettingsActivity;
import im.bclpbkiauv.ui.NotificationsSettingsActivity;
import im.bclpbkiauv.ui.ProfileNotificationsActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.AccountSelectCell;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.cells.RadioColorCell;
import im.bclpbkiauv.ui.cells.TextColorCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.NumberPicker;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class AlertsCreator {

    public interface AccountSelectDelegate {
        void didSelectAccount(int i);
    }

    public interface DatePickerDelegate {
        void didSelectDate(int i, int i2, int i3);
    }

    public interface PaymentAlertDelegate {
        void didPressedNewCard();
    }

    public interface ScheduleDatePickerDelegate {
        void didSelectDate(boolean z, int i);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x03ce  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x044e  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0464  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.app.Dialog processError(int r16, im.bclpbkiauv.tgnet.TLRPC.TL_error r17, im.bclpbkiauv.ui.actionbar.BaseFragment r18, im.bclpbkiauv.tgnet.TLObject r19, java.lang.Object... r20) {
        /*
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = r20
            int r4 = r0.code
            r6 = 406(0x196, float:5.69E-43)
            if (r4 == r6) goto L_0x0673
            java.lang.String r4 = r0.text
            if (r4 != 0) goto L_0x0015
            r4 = 0
            goto L_0x0674
        L_0x0015:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_saveSecureValue
            java.lang.String r6 = "\n"
            java.lang.String r8 = "InvalidPhoneNumber"
            java.lang.String r9 = "PHONE_NUMBER_INVALID"
            java.lang.String r10 = "ErrorOccurred"
            r11 = 2131691144(0x7f0f0688, float:1.9011352E38)
            r12 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r13 = "FloodWait"
            java.lang.String r14 = "FLOOD_WAIT"
            if (r4 != 0) goto L_0x0611
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_getAuthorizationForm
            if (r4 == 0) goto L_0x0031
            goto L_0x0611
        L_0x0031:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channels_joinChannel
            java.lang.String r5 = "PEER_FLOOD"
            r15 = 0
            if (r4 != 0) goto L_0x05dc
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channels_editAdmin
            if (r4 != 0) goto L_0x05dc
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channels_inviteToChannel
            if (r4 != 0) goto L_0x05dc
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_addChatUser
            if (r4 != 0) goto L_0x05dc
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_startBot
            if (r4 != 0) goto L_0x05dc
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channels_editBanned
            if (r4 != 0) goto L_0x05dc
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights
            if (r4 != 0) goto L_0x05dc
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_editChatAdmin
            if (r4 != 0) goto L_0x05dc
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_migrateChat
            if (r4 == 0) goto L_0x005a
            goto L_0x05dc
        L_0x005a:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_createChat
            if (r4 == 0) goto L_0x0074
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x006d
            java.lang.String r4 = r0.text
            showFloodWaitAlert(r4, r1)
            goto L_0x0671
        L_0x006d:
            java.lang.String r4 = r0.text
            showAddUserAlert(r4, r1, r15, r2)
            goto L_0x0671
        L_0x0074:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPCChats.TL_channels_createChannel_v1
            java.lang.String r7 = "GROUP_REPEAT_CREATE"
            if (r4 == 0) goto L_0x00ba
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x0089
            java.lang.String r4 = r0.text
            showFloodWaitAlert(r4, r1)
            goto L_0x0671
        L_0x0089:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r7)
            if (r4 == 0) goto L_0x00b3
            r4 = r2
            im.bclpbkiauv.tgnet.TLRPCChats$TL_channels_createChannel_v1 r4 = (im.bclpbkiauv.tgnet.TLRPCChats.TL_channels_createChannel_v1) r4
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x00a3
            r5 = 2131694222(0x7f0f128e, float:1.9017594E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5)
            showSimpleAlert(r1, r5)
            goto L_0x00b1
        L_0x00a3:
            boolean r5 = r4.broadcast
            if (r5 == 0) goto L_0x00b1
            r5 = 2131694220(0x7f0f128c, float:1.901759E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5)
            showSimpleAlert(r1, r5)
        L_0x00b1:
            goto L_0x0671
        L_0x00b3:
            java.lang.String r4 = r0.text
            showAddUserAlert(r4, r1, r15, r2)
            goto L_0x0671
        L_0x00ba:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channels_createChannel
            if (r4 == 0) goto L_0x00fe
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x00cd
            java.lang.String r4 = r0.text
            showFloodWaitAlert(r4, r1)
            goto L_0x0671
        L_0x00cd:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r7)
            if (r4 == 0) goto L_0x00f7
            r4 = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_channels_createChannel r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_channels_createChannel) r4
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x00e7
            r5 = 2131694222(0x7f0f128e, float:1.9017594E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5)
            showSimpleAlert(r1, r5)
            goto L_0x00f5
        L_0x00e7:
            boolean r5 = r4.broadcast
            if (r5 == 0) goto L_0x00f5
            r5 = 2131694220(0x7f0f128c, float:1.901759E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5)
            showSimpleAlert(r1, r5)
        L_0x00f5:
            goto L_0x0671
        L_0x00f7:
            java.lang.String r4 = r0.text
            showAddUserAlert(r4, r1, r15, r2)
            goto L_0x0671
        L_0x00fe:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_editMessage
            if (r4 == 0) goto L_0x012a
            java.lang.String r4 = r0.text
            java.lang.String r5 = "MESSAGE_NOT_MODIFIED"
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L_0x0671
            if (r1 == 0) goto L_0x011c
            r4 = 2131691030(0x7f0f0616, float:1.901112E38)
            java.lang.String r5 = "EditMessageError"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x011c:
            r4 = 2131691030(0x7f0f0616, float:1.901112E38)
            java.lang.String r5 = "EditMessageError"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleToast(r1, r4)
            goto L_0x0671
        L_0x012a:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendMessage
            if (r4 != 0) goto L_0x0552
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendMedia
            if (r4 != 0) goto L_0x0552
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendInlineBotResult
            if (r4 != 0) goto L_0x0552
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_forwardMessages
            if (r4 != 0) goto L_0x0552
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendMultiMedia
            if (r4 != 0) goto L_0x0552
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendScheduledMessages
            if (r4 == 0) goto L_0x0144
            goto L_0x0552
        L_0x0144:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_importChatInvite
            if (r4 == 0) goto L_0x017f
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x0159
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r12)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x0159:
            java.lang.String r4 = r0.text
            java.lang.String r5 = "USERS_TOO_MUCH"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0171
            r4 = 2131691728(0x7f0f08d0, float:1.9012536E38)
            java.lang.String r5 = "JoinToGroupErrorFull"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x0171:
            r4 = 2131691729(0x7f0f08d1, float:1.9012538E38)
            java.lang.String r5 = "JoinToGroupErrorNotExist"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x017f:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_getAttachedStickers
            if (r4 == 0) goto L_0x0190
            if (r1 == 0) goto L_0x0671
            androidx.fragment.app.FragmentActivity r4 = r18.getParentActivity()
            if (r4 == 0) goto L_0x0671
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((int) r11)
            goto L_0x0671
        L_0x0190:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_confirmPhone
            java.lang.String r7 = "CodeExpired"
            java.lang.String r15 = "PHONE_CODE_EXPIRED"
            java.lang.String r11 = "PHONE_CODE_INVALID"
            java.lang.String r12 = "InvalidCode"
            java.lang.String r5 = "PHONE_CODE_EMPTY"
            if (r4 != 0) goto L_0x04e7
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_verifyPhone
            if (r4 != 0) goto L_0x04e7
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_verifyEmail
            if (r4 == 0) goto L_0x01a8
            goto L_0x04e7
        L_0x01a8:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_auth_resendCode
            if (r4 == 0) goto L_0x022b
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r9)
            if (r4 == 0) goto L_0x01c0
            r4 = 2131691683(0x7f0f08a3, float:1.9012445E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x01c0:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r5)
            if (r4 != 0) goto L_0x021f
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r11)
            if (r4 == 0) goto L_0x01d1
            goto L_0x021f
        L_0x01d1:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r15)
            if (r4 == 0) goto L_0x01e5
            r4 = 2131690634(0x7f0f048a, float:1.9010317E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x01e5:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x01f9
            r4 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x01f9:
            int r4 = r0.code
            r5 = -1000(0xfffffffffffffc18, float:NaN)
            if (r4 == r5) goto L_0x0671
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r5 = 2131691144(0x7f0f0688, float:1.9011352E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r5)
            r4.append(r5)
            r4.append(r6)
            java.lang.String r5 = r0.text
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x021f:
            r4 = 2131691678(0x7f0f089e, float:1.9012435E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x022b:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_sendConfirmPhoneCode
            if (r4 == 0) goto L_0x0267
            int r4 = r0.code
            r5 = 400(0x190, float:5.6E-43)
            if (r4 != r5) goto L_0x0243
            r4 = 2131690314(0x7f0f034a, float:1.9009668E38)
            java.lang.String r5 = "CancelLinkExpired"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0243:
            java.lang.String r4 = r0.text
            if (r4 == 0) goto L_0x0671
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x025b
            r4 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x025b:
            r4 = 2131691144(0x7f0f0688, float:1.9011352E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0267:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_changePhone
            if (r4 == 0) goto L_0x02cb
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r9)
            if (r4 == 0) goto L_0x027f
            r4 = 2131691683(0x7f0f08a3, float:1.9012445E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x027f:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r5)
            if (r4 != 0) goto L_0x02bf
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r11)
            if (r4 == 0) goto L_0x0290
            goto L_0x02bf
        L_0x0290:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r15)
            if (r4 == 0) goto L_0x02a4
            r4 = 2131690634(0x7f0f048a, float:1.9010317E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x02a4:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x02b8
            r4 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x02b8:
            java.lang.String r4 = r0.text
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x02bf:
            r4 = 2131691678(0x7f0f089e, float:1.9012435E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x02cb:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_sendChangePhoneCode
            if (r4 == 0) goto L_0x0387
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r9)
            if (r4 == 0) goto L_0x02e3
            r4 = 2131691683(0x7f0f08a3, float:1.9012445E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x02e3:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r5)
            if (r4 != 0) goto L_0x037b
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r11)
            if (r4 == 0) goto L_0x02f5
            goto L_0x037b
        L_0x02f5:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r15)
            if (r4 == 0) goto L_0x0309
            r4 = 2131690634(0x7f0f048a, float:1.9010317E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x0309:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x031d
            r4 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x031d:
            java.lang.String r4 = r0.text
            java.lang.String r5 = "PHONE_NUMBER_OCCUPIED"
            boolean r4 = r4.startsWith(r5)
            if (r4 == 0) goto L_0x033f
            r4 = 2131690388(0x7f0f0394, float:1.9009818E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r7 = r3[r6]
            java.lang.String r7 = (java.lang.String) r7
            r5[r6] = r7
            java.lang.String r6 = "ChangePhoneNumberOccupied"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.formatString(r6, r4, r5)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x033f:
            java.lang.String r4 = r0.text
            java.lang.String r5 = "IPORDE_LIMIT"
            boolean r4 = r4.contains(r5)
            if (r4 == 0) goto L_0x0357
            r4 = 2131691706(0x7f0f08ba, float:1.9012491E38)
            java.lang.String r5 = "IpOrDeLimit"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x0357:
            java.lang.String r4 = r0.text
            java.lang.String r5 = "INTERNAL"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x036f
            r4 = 2131691676(0x7f0f089c, float:1.901243E38)
            java.lang.String r5 = "InternalError"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x036f:
            r4 = 2131691144(0x7f0f0688, float:1.9011352E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x037b:
            r4 = 2131691678(0x7f0f089e, float:1.9012435E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x0387:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_updateUserName
            if (r4 == 0) goto L_0x03dd
            java.lang.String r4 = r0.text
            int r5 = r4.hashCode()
            r6 = 288843630(0x1137676e, float:1.4468026E-28)
            if (r5 == r6) goto L_0x03a6
            r6 = 533175271(0x1fc79be7, float:8.45377E-20)
            if (r5 == r6) goto L_0x039c
        L_0x039b:
            goto L_0x03b0
        L_0x039c:
            java.lang.String r5 = "USERNAME_OCCUPIED"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x039b
            r4 = 1
            goto L_0x03b1
        L_0x03a6:
            java.lang.String r5 = "USERNAME_INVALID"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x039b
            r4 = 0
            goto L_0x03b1
        L_0x03b0:
            r4 = -1
        L_0x03b1:
            if (r4 == 0) goto L_0x03ce
            r5 = 1
            if (r4 == r5) goto L_0x03c1
            r4 = 2131691144(0x7f0f0688, float:1.9011352E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r4)
            showSimpleAlert(r1, r4)
            goto L_0x03db
        L_0x03c1:
            r4 = 2131694611(0x7f0f1413, float:1.9018383E38)
            java.lang.String r5 = "UsernameInUse"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x03db
        L_0x03ce:
            r4 = 2131694612(0x7f0f1414, float:1.9018385E38)
            java.lang.String r5 = "UsernameInvalid"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
        L_0x03db:
            goto L_0x0671
        L_0x03dd:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_contacts_importContacts
            if (r4 == 0) goto L_0x0418
            if (r0 == 0) goto L_0x040c
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x03ec
            goto L_0x040c
        L_0x03ec:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r5 = 2131691144(0x7f0f0688, float:1.9011352E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r5)
            r4.append(r5)
            r4.append(r6)
            java.lang.String r5 = r0.text
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x040c:
            r4 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x0418:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_getPassword
            if (r4 != 0) goto L_0x04cd
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_getTmpPassword
            if (r4 == 0) goto L_0x0422
            goto L_0x04cd
        L_0x0422:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_payments_sendPaymentForm
            if (r4 == 0) goto L_0x0473
            java.lang.String r4 = r0.text
            int r5 = r4.hashCode()
            r6 = -1144062453(0xffffffffbbcefe0b, float:-0.0063169054)
            if (r5 == r6) goto L_0x0441
            r6 = -784238410(0xffffffffd14178b6, float:-5.1934618E10)
            if (r5 == r6) goto L_0x0437
        L_0x0436:
            goto L_0x044b
        L_0x0437:
            java.lang.String r5 = "PAYMENT_FAILED"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0436
            r4 = 1
            goto L_0x044c
        L_0x0441:
            java.lang.String r5 = "BOT_PRECHECKOUT_FAILED"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0436
            r4 = 0
            goto L_0x044c
        L_0x044b:
            r4 = -1
        L_0x044c:
            if (r4 == 0) goto L_0x0464
            r5 = 1
            if (r4 == r5) goto L_0x0457
            java.lang.String r4 = r0.text
            showSimpleToast(r1, r4)
            goto L_0x0471
        L_0x0457:
            r4 = 2131692937(0x7f0f0d89, float:1.9014988E38)
            java.lang.String r5 = "PaymentFailed"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleToast(r1, r4)
            goto L_0x0471
        L_0x0464:
            r4 = 2131692956(0x7f0f0d9c, float:1.9015027E38)
            java.lang.String r5 = "PaymentPrecheckoutFailed"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleToast(r1, r4)
        L_0x0471:
            goto L_0x0671
        L_0x0473:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_payments_validateRequestedInfo
            if (r4 == 0) goto L_0x04a5
            java.lang.String r4 = r0.text
            int r5 = r4.hashCode()
            r6 = 1758025548(0x68c9574c, float:7.606448E24)
            if (r5 == r6) goto L_0x0483
        L_0x0482:
            goto L_0x048d
        L_0x0483:
            java.lang.String r5 = "SHIPPING_NOT_AVAILABLE"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0482
            r15 = 0
            goto L_0x048e
        L_0x048d:
            r15 = -1
        L_0x048e:
            if (r15 == 0) goto L_0x0496
            java.lang.String r4 = r0.text
            showSimpleToast(r1, r4)
            goto L_0x04a3
        L_0x0496:
            r4 = 2131692941(0x7f0f0d8d, float:1.9014996E38)
            java.lang.String r5 = "PaymentNoShippingMethod"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleToast(r1, r4)
        L_0x04a3:
            goto L_0x0671
        L_0x04a5:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_updateUsername
            if (r4 == 0) goto L_0x0671
            java.lang.String r4 = r0.text
            java.lang.String r5 = "ALREDY_CHANGE"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x04c1
            r4 = 2131689759(0x7f0f011f, float:1.9008542E38)
            java.lang.String r5 = "AlreadyChangeAppNameCodeTips"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x04c1:
            r4 = 2131692506(0x7f0f0bda, float:1.9014114E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x04cd:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x04e0
            java.lang.String r4 = r0.text
            java.lang.String r4 = getFloodWaitString(r4)
            showSimpleToast(r1, r4)
            goto L_0x0671
        L_0x04e0:
            java.lang.String r4 = r0.text
            showSimpleToast(r1, r4)
            goto L_0x0671
        L_0x04e7:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r5)
            if (r4 != 0) goto L_0x0546
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r11)
            if (r4 != 0) goto L_0x0546
            java.lang.String r4 = r0.text
            java.lang.String r5 = "CODE_INVALID"
            boolean r4 = r4.contains(r5)
            if (r4 != 0) goto L_0x0546
            java.lang.String r4 = r0.text
            java.lang.String r5 = "CODE_EMPTY"
            boolean r4 = r4.contains(r5)
            if (r4 == 0) goto L_0x050c
            goto L_0x0546
        L_0x050c:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r15)
            if (r4 != 0) goto L_0x053a
            java.lang.String r4 = r0.text
            java.lang.String r5 = "EMAIL_VERIFY_EXPIRED"
            boolean r4 = r4.contains(r5)
            if (r4 == 0) goto L_0x051f
            goto L_0x053a
        L_0x051f:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x0533
            r4 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0533:
            java.lang.String r4 = r0.text
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x053a:
            r4 = 2131690634(0x7f0f048a, float:1.9010317E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0546:
            r4 = 2131691678(0x7f0f089e, float:1.9012435E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0552:
            java.lang.String r4 = r0.text
            int r6 = r4.hashCode()
            switch(r6) {
                case -1809401834: goto L_0x0578;
                case -454039871: goto L_0x0570;
                case -33935358: goto L_0x0566;
                case 1169786080: goto L_0x055c;
                default: goto L_0x055b;
            }
        L_0x055b:
            goto L_0x0582
        L_0x055c:
            java.lang.String r5 = "SCHEDULE_TOO_MUCH"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x055b
            r4 = 2
            goto L_0x0583
        L_0x0566:
            java.lang.String r5 = "MUTUALCONTACTNEED"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x055b
            r4 = 3
            goto L_0x0583
        L_0x0570:
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x055b
            r4 = 0
            goto L_0x0583
        L_0x0578:
            java.lang.String r5 = "USER_BANNED_IN_CHANNEL"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x055b
            r4 = 1
            goto L_0x0583
        L_0x0582:
            r4 = -1
        L_0x0583:
            if (r4 == 0) goto L_0x05c6
            r5 = 1
            if (r4 == r5) goto L_0x05b1
            r6 = 2
            if (r4 == r6) goto L_0x05a4
            r6 = 3
            if (r4 == r6) goto L_0x058f
            goto L_0x05da
        L_0x058f:
            im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r16)
            int r6 = im.bclpbkiauv.messenger.NotificationCenter.contactRelationShip
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 101(0x65, float:1.42E-43)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r8 = 0
            r5[r8] = r7
            r4.postNotificationName(r6, r5)
            goto L_0x05da
        L_0x05a4:
            r4 = 2131692011(0x7f0f09eb, float:1.901311E38)
            java.lang.String r5 = "MessageScheduledLimitReached"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            showSimpleToast(r1, r4)
            goto L_0x05da
        L_0x05b1:
            im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r16)
            int r5 = im.bclpbkiauv.messenger.NotificationCenter.needShowAlert
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 5
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r8 = 0
            r6[r8] = r7
            r4.postNotificationName(r5, r6)
            goto L_0x05da
        L_0x05c6:
            r6 = 1
            r8 = 0
            im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r16)
            int r5 = im.bclpbkiauv.messenger.NotificationCenter.needShowAlert
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)
            r6[r8] = r7
            r4.postNotificationName(r5, r6)
        L_0x05da:
            goto L_0x0671
        L_0x05dc:
            if (r1 == 0) goto L_0x05f5
            java.lang.String r4 = r0.text
            if (r3 == 0) goto L_0x05ef
            int r5 = r3.length
            if (r5 <= 0) goto L_0x05ef
            r5 = 0
            r5 = r3[r5]
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r15 = r5.booleanValue()
            goto L_0x05f0
        L_0x05ef:
            r15 = 0
        L_0x05f0:
            showAddUserAlert(r4, r1, r15, r2)
            goto L_0x0671
        L_0x05f5:
            java.lang.String r4 = r0.text
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0671
            im.bclpbkiauv.messenger.NotificationCenter r4 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r16)
            int r5 = im.bclpbkiauv.messenger.NotificationCenter.needShowAlert
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r8 = 0
            r7[r8] = r6
            r4.postNotificationName(r5, r7)
            goto L_0x0671
        L_0x0611:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r9)
            if (r4 == 0) goto L_0x0624
            r4 = 2131691683(0x7f0f08a3, float:1.9012445E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x0624:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r14)
            if (r4 == 0) goto L_0x0637
            r4 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            showSimpleAlert(r1, r4)
            goto L_0x0671
        L_0x0637:
            java.lang.String r4 = r0.text
            java.lang.String r5 = "APP_VERSION_OUTDATED"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0653
            androidx.fragment.app.FragmentActivity r4 = r18.getParentActivity()
            r5 = 2131694502(0x7f0f13a6, float:1.9018162E38)
            java.lang.String r6 = "UpdateAppAlert"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
            r6 = 1
            showUpdateAppAlert(r4, r5, r6)
            goto L_0x0671
        L_0x0653:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r5 = 2131691144(0x7f0f0688, float:1.9011352E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r5)
            r4.append(r5)
            r4.append(r6)
            java.lang.String r5 = r0.text
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            showSimpleAlert(r1, r4)
        L_0x0671:
            r4 = 0
            return r4
        L_0x0673:
            r4 = 0
        L_0x0674:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.AlertsCreator.processError(int, im.bclpbkiauv.tgnet.TLRPC$TL_error, im.bclpbkiauv.ui.actionbar.BaseFragment, im.bclpbkiauv.tgnet.TLObject, java.lang.Object[]):android.app.Dialog");
    }

    public static Toast showSimpleToast(BaseFragment baseFragment, String text) {
        ToastUtils.show((CharSequence) text);
        return ToastUtils.getToast();
    }

    public static AlertDialog showUpdateAppAlert(Context context, String text, boolean updateApp) {
        if (context == null || text == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        if (updateApp) {
            builder.setNegativeButton(LocaleController.getString("UpdateApp", R.string.UpdateApp), new DialogInterface.OnClickListener(context) {
                private final /* synthetic */ Context f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    Browser.openUrl(this.f$0, BuildVars.PLAYSTORE_APP_URL);
                }
            });
        }
        return builder.show();
    }

    public static AlertDialog.Builder createLanguageAlert(LaunchActivity activity, TLRPC.TL_langPackLanguage language) {
        String str;
        int end;
        if (language == null) {
            return null;
        }
        language.lang_code = language.lang_code.replace('-', '_').toLowerCase();
        language.plural_code = language.plural_code.replace('-', '_').toLowerCase();
        if (language.base_lang_code != null) {
            language.base_lang_code = language.base_lang_code.replace('-', '_').toLowerCase();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        if (LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals(language.lang_code)) {
            builder.setTitle(LocaleController.getString("Language", R.string.Language));
            str = LocaleController.formatString("LanguageSame", R.string.LanguageSame, language.name);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            builder.setNeutralButton(LocaleController.getString("SETTINGS", R.string.SETTINGS), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LaunchActivity.this.lambda$runLinkRequest$28$LaunchActivity(new LanguageSelectActivity());
                }
            });
        } else if (language.strings_count == 0) {
            builder.setTitle(LocaleController.getString("LanguageUnknownTitle", R.string.LanguageUnknownTitle));
            str = LocaleController.formatString("LanguageUnknownCustomAlert", R.string.LanguageUnknownCustomAlert, language.name);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        } else {
            builder.setTitle(LocaleController.getString("LanguageTitle", R.string.LanguageTitle));
            if (language.official) {
                str = LocaleController.formatString("LanguageAlert", R.string.LanguageAlert, language.name, Integer.valueOf((int) Math.ceil((double) ((((float) language.translated_count) / ((float) language.strings_count)) * 100.0f))));
            } else {
                str = LocaleController.formatString("LanguageCustomAlert", R.string.LanguageCustomAlert, language.name, Integer.valueOf((int) Math.ceil((double) ((((float) language.translated_count) / ((float) language.strings_count)) * 100.0f))));
            }
            builder.setPositiveButton(LocaleController.getString("Change", R.string.Change), new DialogInterface.OnClickListener(activity) {
                private final /* synthetic */ LaunchActivity f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createLanguageAlert$2(TLRPC.TL_langPackLanguage.this, this.f$1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        }
        SpannableStringBuilder spanned = new SpannableStringBuilder(AndroidUtilities.replaceTags(str));
        int start = TextUtils.indexOf(spanned, '[');
        if (start != -1) {
            end = TextUtils.indexOf(spanned, ']', start + 1);
            if (!(start == -1 || end == -1)) {
                spanned.delete(end, end + 1);
                spanned.delete(start, start + 1);
            }
        } else {
            end = -1;
        }
        if (!(start == -1 || end == -1)) {
            spanned.setSpan(new URLSpanNoUnderline(language.translations_url) {
                public void onClick(View widget) {
                    builder.getDismissRunnable().run();
                    super.onClick(widget);
                }
            }, start, end - 1, 33);
        }
        TextView message = new TextView(activity);
        message.setText(spanned);
        message.setTextSize(1, 16.0f);
        message.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        message.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
        message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        message.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        builder.setView(message);
        return builder;
    }

    static /* synthetic */ void lambda$createLanguageAlert$2(TLRPC.TL_langPackLanguage language, LaunchActivity activity, DialogInterface dialogInterface, int i) {
        String key;
        if (language.official) {
            key = "remote_" + language.lang_code;
        } else {
            key = "unofficial_" + language.lang_code;
        }
        LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getLanguageFromDict(key);
        if (localeInfo == null) {
            localeInfo = new LocaleController.LocaleInfo();
            localeInfo.name = language.native_name;
            localeInfo.nameEnglish = language.name;
            localeInfo.shortName = language.lang_code;
            localeInfo.baseLangCode = language.base_lang_code;
            localeInfo.pluralLangCode = language.plural_code;
            localeInfo.isRtl = language.rtl;
            if (language.official) {
                localeInfo.pathToFile = "remote";
            } else {
                localeInfo.pathToFile = "unofficial";
            }
        }
        LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, UserConfig.selectedAccount);
        activity.rebuildAllFragments(true);
    }

    public static boolean checkSlowMode(Context context, int currentAccount, long did, boolean few) {
        TLRPC.Chat chat;
        int lowerId = (int) did;
        if (lowerId < 0 && (chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(-lowerId))) != null && chat.slowmode_enabled && !ChatObject.hasAdminRights(chat)) {
            if (!few) {
                TLRPC.ChatFull chatFull = MessagesController.getInstance(currentAccount).getChatFull(chat.id);
                if (chatFull == null) {
                    chatFull = MessagesStorage.getInstance(currentAccount).loadChatInfo(chat.id, new CountDownLatch(1), false, false);
                }
                if (chatFull != null && chatFull.slowmode_next_send_date >= ConnectionsManager.getInstance(currentAccount).getCurrentTime()) {
                    few = true;
                }
            }
            if (few) {
                createSimpleAlert(context, chat.title, LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError)).show();
                return true;
            }
        }
        return false;
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String text) {
        return createSimpleAlert(context, (String) null, text);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String title, String text) {
        if (text == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title == null ? LocaleController.getString("AppName", R.string.AppName) : title);
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        return builder;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String text) {
        return showSimpleAlert(baseFragment, (String) null, text);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String title, String text) {
        if (text == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        Dialog dialog = createSimpleAlert(baseFragment.getParentActivity(), title, text).create();
        baseFragment.showDialog(dialog);
        return dialog;
    }

    public static void showBlockReportSpamAlert(BaseFragment fragment, long dialog_id, TLRPC.User currentUser, TLRPC.Chat currentChat, TLRPC.EncryptedChat encryptedChat, boolean isLocation, TLRPC.ChatFull chatInfo, MessagesStorage.IntCallback callback) {
        CheckBoxCell[] cells;
        CharSequence reportText;
        CharSequence reportText2;
        BaseFragment baseFragment = fragment;
        TLRPC.Chat chat = currentChat;
        TLRPC.ChatFull chatFull = chatInfo;
        if (baseFragment != null && fragment.getParentActivity() != null) {
            AccountInstance accountInstance = fragment.getAccountInstance();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            boolean showReport = MessagesController.getNotificationsSettings(fragment.getCurrentAccount()).getBoolean("dialog_bar_report" + dialog_id, false);
            int i = 1;
            if (currentUser != null) {
                builder.setTitle(LocaleController.formatString("BlockUserTitle", R.string.BlockUserTitle, UserObject.getFirstName(currentUser)));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserAlert", R.string.BlockUserAlert, UserObject.getFirstName(currentUser))));
                CharSequence reportText3 = LocaleController.getString("BlockContact", R.string.BlockContact);
                CheckBoxCell[] cells2 = new CheckBoxCell[2];
                LinearLayout linearLayout = new LinearLayout(fragment.getParentActivity());
                linearLayout.setOrientation(1);
                int a = 0;
                for (int i2 = 2; a < i2; i2 = 2) {
                    if (a != 0 || showReport) {
                        cells2[a] = new CheckBoxCell(fragment.getParentActivity(), i);
                        cells2[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        cells2[a].setTag(Integer.valueOf(a));
                        if (a == 0) {
                            reportText2 = reportText3;
                            cells2[a].setText(LocaleController.getString("DeleteReportSpam", R.string.DeleteReportSpam), "", true, false);
                        } else {
                            reportText2 = reportText3;
                            if (a == 1) {
                                cells2[a].setText(LocaleController.formatString("DeleteThisChat", R.string.DeleteThisChat, new Object[0]), "", true, false);
                            }
                        }
                        cells2[a].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                        linearLayout.addView(cells2[a], LayoutHelper.createLinear(-1, -2));
                        cells2[a].setOnClickListener(new View.OnClickListener(cells2) {
                            private final /* synthetic */ CheckBoxCell[] f$0;

                            {
                                this.f$0 = r1;
                            }

                            public final void onClick(View view) {
                                AlertsCreator.lambda$showBlockReportSpamAlert$3(this.f$0, view);
                            }
                        });
                    } else {
                        reportText2 = reportText3;
                    }
                    a++;
                    long j = dialog_id;
                    reportText3 = reportText2;
                    i = 1;
                }
                builder.setCustomViewOffset(12);
                builder.setView(linearLayout);
                cells = cells2;
                reportText = reportText3;
            } else {
                if (chat == null || !isLocation) {
                    builder.setTitle(LocaleController.getString("ReportSpamTitle", R.string.ReportSpamTitle));
                    if (!ChatObject.isChannel(currentChat) || chat.megagroup) {
                        builder.setMessage(LocaleController.getString("ReportSpamAlertGroup", R.string.ReportSpamAlertGroup));
                    } else {
                        builder.setMessage(LocaleController.getString("ReportSpamAlertChannel", R.string.ReportSpamAlertChannel));
                    }
                } else {
                    builder.setTitle(LocaleController.getString("ReportUnrelatedGroup", R.string.ReportUnrelatedGroup));
                    if (chatFull == null || !(chatFull.location instanceof TLRPC.TL_channelLocation)) {
                        builder.setMessage(LocaleController.getString("ReportUnrelatedGroupTextNoAddress", R.string.ReportUnrelatedGroupTextNoAddress));
                    } else {
                        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ReportUnrelatedGroupText", R.string.ReportUnrelatedGroupText, ((TLRPC.TL_channelLocation) chatFull.location).address)));
                    }
                }
                reportText = LocaleController.getString("ReportChat", R.string.ReportChat);
                cells = null;
            }
            $$Lambda$AlertsCreator$9yMG7N6p4q2TflPDJraAeNtmkU8 r11 = r1;
            $$Lambda$AlertsCreator$9yMG7N6p4q2TflPDJraAeNtmkU8 r1 = new DialogInterface.OnClickListener(accountInstance, cells, dialog_id, currentChat, encryptedChat, isLocation, callback) {
                private final /* synthetic */ AccountInstance f$1;
                private final /* synthetic */ CheckBoxCell[] f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ TLRPC.Chat f$4;
                private final /* synthetic */ TLRPC.EncryptedChat f$5;
                private final /* synthetic */ boolean f$6;
                private final /* synthetic */ MessagesStorage.IntCallback f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$showBlockReportSpamAlert$4(TLRPC.User.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, dialogInterface, i);
                }
            };
            builder.setPositiveButton(reportText, r11);
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            AlertDialog dialog = builder.create();
            baseFragment.showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$3(CheckBoxCell[] cells, View v) {
        Integer num = (Integer) v.getTag();
        cells[num.intValue()].setChecked(!cells[num.intValue()].isChecked(), true);
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$4(TLRPC.User currentUser, AccountInstance accountInstance, CheckBoxCell[] cells, long dialog_id, TLRPC.Chat currentChat, TLRPC.EncryptedChat encryptedChat, boolean isLocation, MessagesStorage.IntCallback callback, DialogInterface dialogInterface, int i) {
        TLRPC.User user = currentUser;
        long j = dialog_id;
        MessagesStorage.IntCallback intCallback = callback;
        if (user != null) {
            accountInstance.getMessagesController().blockUser(user.id);
        }
        if (cells == null || (cells[0] != null && cells[0].isChecked())) {
            accountInstance.getMessagesController().reportSpam(dialog_id, currentUser, currentChat, encryptedChat, currentChat != null && isLocation);
        }
        if (cells == null || cells[1].isChecked()) {
            if (currentChat == null) {
                accountInstance.getMessagesController().deleteDialog(j, 0);
            } else if (ChatObject.isNotInChat(currentChat)) {
                accountInstance.getMessagesController().deleteDialog(j, 0);
            } else {
                accountInstance.getMessagesController().deleteUserFromChat((int) (-j), accountInstance.getMessagesController().getUser(Integer.valueOf(accountInstance.getUserConfig().getClientUserId())), (TLRPC.ChatFull) null);
            }
            intCallback.run(1);
            return;
        }
        intCallback.run(0);
    }

    public static void showCustomNotificationsDialog(BaseFragment parentFragment, long did, int globalType, ArrayList<NotificationsSettingsActivity.NotificationException> exceptions, int currentAccount, MessagesStorage.IntCallback callback) {
        showCustomNotificationsDialog(parentFragment, did, globalType, exceptions, currentAccount, callback, (MessagesStorage.IntCallback) null);
    }

    public static void showCustomNotificationsDialog(BaseFragment parentFragment, long did, int globalType, ArrayList<NotificationsSettingsActivity.NotificationException> exceptions, int currentAccount, MessagesStorage.IntCallback callback, MessagesStorage.IntCallback resultCallback) {
        String str;
        Drawable drawable;
        String[] descriptions;
        boolean defaultEnabled;
        AlertDialog.Builder builder;
        LinearLayout linearLayout;
        int a;
        BaseFragment baseFragment = parentFragment;
        long j = did;
        if (baseFragment != null && parentFragment.getParentActivity() != null) {
            boolean defaultEnabled2 = NotificationsController.getInstance(currentAccount).isGlobalNotificationsEnabled(j);
            String[] strArr = new String[5];
            boolean z = false;
            strArr[0] = LocaleController.getString("NotificationsTurnOn", R.string.NotificationsTurnOn);
            boolean z2 = true;
            strArr[1] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
            strArr[2] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
            Drawable drawable2 = null;
            if (j != 0 || !(baseFragment instanceof NotificationsCustomSettingsActivity)) {
                str = LocaleController.getString("NotificationsCustomize", R.string.NotificationsCustomize);
            } else {
                str = null;
            }
            strArr[3] = str;
            strArr[4] = LocaleController.getString("NotificationsTurnOff", R.string.NotificationsTurnOff);
            String[] descriptions2 = strArr;
            int[] icons = {R.drawable.notifications_on, R.drawable.notifications_mute1h, R.drawable.notifications_mute2d, R.drawable.notifications_settings, R.drawable.notifications_off};
            LinearLayout linearLayout2 = new LinearLayout(parentFragment.getParentActivity());
            linearLayout2.setOrientation(1);
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) parentFragment.getParentActivity());
            int a2 = 0;
            while (a2 < descriptions2.length) {
                if (descriptions2[a2] == null) {
                    a = a2;
                    builder = builder2;
                    descriptions = descriptions2;
                    drawable = drawable2;
                    defaultEnabled = defaultEnabled2;
                    linearLayout = linearLayout2;
                } else {
                    TextView textView = new TextView(parentFragment.getParentActivity());
                    Drawable drawable3 = parentFragment.getParentActivity().getResources().getDrawable(icons[a2]);
                    if (a2 == descriptions2.length - (z2 ? 1 : 0)) {
                        textView.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
                        drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogRedIcon), PorterDuff.Mode.MULTIPLY));
                    } else {
                        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                        drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
                    }
                    textView.setTextSize(z2, 16.0f);
                    textView.setLines(z2);
                    textView.setMaxLines(z2);
                    textView.setCompoundDrawablesWithIntrinsicBounds(drawable3, drawable2, drawable2, drawable2);
                    textView.setTag(Integer.valueOf(a2));
                    textView.setBackgroundDrawable(Theme.getSelectorDrawable(z));
                    textView.setPadding(AndroidUtilities.dp(24.0f), z ? 1 : 0, AndroidUtilities.dp(24.0f), z);
                    textView.setSingleLine(z2);
                    textView.setGravity(19);
                    textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                    textView.setText(descriptions2[a2]);
                    linearLayout2.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                    $$Lambda$AlertsCreator$N3y5qhjKcXYe8Pb4LjNhlz3X8c r15 = r0;
                    Drawable drawable4 = drawable3;
                    a = a2;
                    builder = builder2;
                    defaultEnabled = defaultEnabled2;
                    linearLayout = linearLayout2;
                    descriptions = descriptions2;
                    drawable = drawable2;
                    $$Lambda$AlertsCreator$N3y5qhjKcXYe8Pb4LjNhlz3X8c r0 = new View.OnClickListener(did, currentAccount, defaultEnabled2, resultCallback, globalType, parentFragment, exceptions, callback, builder) {
                        private final /* synthetic */ long f$0;
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ boolean f$2;
                        private final /* synthetic */ MessagesStorage.IntCallback f$3;
                        private final /* synthetic */ int f$4;
                        private final /* synthetic */ BaseFragment f$5;
                        private final /* synthetic */ ArrayList f$6;
                        private final /* synthetic */ MessagesStorage.IntCallback f$7;
                        private final /* synthetic */ AlertDialog.Builder f$8;

                        {
                            this.f$0 = r1;
                            this.f$1 = r3;
                            this.f$2 = r4;
                            this.f$3 = r5;
                            this.f$4 = r6;
                            this.f$5 = r7;
                            this.f$6 = r8;
                            this.f$7 = r9;
                            this.f$8 = r10;
                        }

                        public final void onClick(View view) {
                            AlertsCreator.lambda$showCustomNotificationsDialog$5(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
                        }
                    };
                    textView.setOnClickListener(r15);
                }
                a2 = a + 1;
                long j2 = did;
                linearLayout2 = linearLayout;
                builder2 = builder;
                defaultEnabled2 = defaultEnabled;
                descriptions2 = descriptions;
                drawable2 = drawable;
                z2 = true;
                z = false;
            }
            boolean z3 = defaultEnabled2;
            AlertDialog.Builder builder3 = builder2;
            builder3.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
            builder3.setView(linearLayout2);
            baseFragment.showDialog(builder3.create());
        }
    }

    static /* synthetic */ void lambda$showCustomNotificationsDialog$5(long did, int currentAccount, boolean defaultEnabled, MessagesStorage.IntCallback resultCallback, int globalType, BaseFragment parentFragment, ArrayList exceptions, MessagesStorage.IntCallback callback, AlertDialog.Builder builder, View v) {
        long flags;
        long j = did;
        MessagesStorage.IntCallback intCallback = resultCallback;
        int i = globalType;
        BaseFragment baseFragment = parentFragment;
        MessagesStorage.IntCallback intCallback2 = callback;
        int i2 = ((Integer) v.getTag()).intValue();
        if (i2 == 0) {
            if (j != 0) {
                SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(currentAccount).edit();
                if (defaultEnabled) {
                    editor.remove("notify2_" + j);
                } else {
                    editor.putInt("notify2_" + j, 0);
                }
                MessagesStorage.getInstance(currentAccount).setDialogFlags(j, 0);
                editor.commit();
                TLRPC.Dialog dialog = MessagesController.getInstance(currentAccount).dialogs_dict.get(j);
                if (dialog != null) {
                    dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                }
                NotificationsController.getInstance(currentAccount).updateServerNotificationsSettings(j);
                if (intCallback != null) {
                    if (defaultEnabled) {
                        intCallback.run(0);
                    } else {
                        intCallback.run(1);
                    }
                }
                ArrayList arrayList = exceptions;
            } else {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(i, 0);
                ArrayList arrayList2 = exceptions;
            }
        } else if (i2 != 3) {
            ArrayList arrayList3 = exceptions;
            int untilTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            if (i2 == 1) {
                untilTime += 3600;
            } else if (i2 == 2) {
                untilTime += 172800;
            } else if (i2 == 4) {
                untilTime = Integer.MAX_VALUE;
            }
            if (j != 0) {
                SharedPreferences.Editor editor2 = MessagesController.getNotificationsSettings(currentAccount).edit();
                if (i2 != 4) {
                    editor2.putInt("notify2_" + j, 3);
                    editor2.putInt("notifyuntil_" + j, untilTime);
                    flags = (((long) untilTime) << 32) | 1;
                } else if (!defaultEnabled) {
                    editor2.remove("notify2_" + j);
                    flags = 0;
                } else {
                    editor2.putInt("notify2_" + j, 2);
                    flags = 1;
                }
                NotificationsController.getInstance(currentAccount).removeNotificationsForDialog(j);
                MessagesStorage.getInstance(currentAccount).setDialogFlags(j, flags);
                editor2.commit();
                TLRPC.Dialog dialog2 = MessagesController.getInstance(currentAccount).dialogs_dict.get(j);
                if (dialog2 != null) {
                    dialog2.notify_settings = new TLRPC.TL_peerNotifySettings();
                    if (i2 != 4 || defaultEnabled) {
                        dialog2.notify_settings.mute_until = untilTime;
                    }
                }
                NotificationsController.getInstance(currentAccount).updateServerNotificationsSettings(j);
                if (intCallback != null) {
                    if (i2 != 4 || defaultEnabled) {
                        intCallback.run(1);
                    } else {
                        intCallback.run(0);
                    }
                }
            } else if (i2 == 4) {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(i, Integer.MAX_VALUE);
            } else {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(i, untilTime);
            }
        } else if (j != 0) {
            Bundle args = new Bundle();
            args.putLong("dialog_id", j);
            baseFragment.presentFragment(new ProfileNotificationsActivity(args));
            ArrayList arrayList4 = exceptions;
        } else {
            baseFragment.presentFragment(new NotificationsCustomSettingsActivity(i, exceptions));
        }
        if (intCallback2 != null) {
            intCallback2.run(i2);
        }
        builder.getDismissRunnable().run();
    }

    public static AlertDialog showSecretLocationAlert(Context context, int currentAccount, Runnable onSelectRunnable, boolean inChat) {
        ArrayList<String> arrayList = new ArrayList<>();
        int providers = MessagesController.getInstance(currentAccount).availableMapProviders;
        if ((providers & 1) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderApp", R.string.MapPreviewProviderApp));
        }
        if ((providers & 2) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderGoogle", R.string.MapPreviewProviderGoogle));
        }
        if ((providers & 4) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex));
        }
        arrayList.add(LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody));
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(LocaleController.getString("ChooseMapPreviewProvider", R.string.ChooseMapPreviewProvider)).setItems((CharSequence[]) arrayList.toArray(new String[0]), new DialogInterface.OnClickListener(onSelectRunnable) {
            private final /* synthetic */ Runnable f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$showSecretLocationAlert$6(this.f$0, dialogInterface, i);
            }
        });
        if (!inChat) {
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        }
        AlertDialog dialog = builder.show();
        if (inChat) {
            dialog.setCanceledOnTouchOutside(false);
        }
        return dialog;
    }

    static /* synthetic */ void lambda$showSecretLocationAlert$6(Runnable onSelectRunnable, DialogInterface dialog, int which) {
        SharedConfig.setSecretMapPreviewType(which);
        if (onSelectRunnable != null) {
            onSelectRunnable.run();
        }
    }

    /* access modifiers changed from: private */
    public static void updateDayPicker(NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2, monthPicker.getValue());
        calendar.set(1, yearPicker.getValue());
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(calendar.getActualMaximum(5));
    }

    private static void checkPickerDate(NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        int currentMonth = calendar.get(2);
        int currentDay = calendar.get(5);
        if (currentYear > yearPicker.getValue()) {
            yearPicker.setValue(currentYear);
        }
        if (yearPicker.getValue() == currentYear) {
            if (currentMonth > monthPicker.getValue()) {
                monthPicker.setValue(currentMonth);
            }
            if (currentMonth == monthPicker.getValue() && currentDay > dayPicker.getValue()) {
                dayPicker.setValue(currentDay);
            }
        }
    }

    public static AlertDialog createSupportAlert(final BaseFragment fragment) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return null;
        }
        TextView message = new TextView(fragment.getParentActivity());
        Spannable spanned = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", R.string.AskAQuestionInfo).replace("\n", "<br>")));
        URLSpan[] spans = (URLSpan[]) spanned.getSpans(0, spanned.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = spanned.getSpanStart(span);
            int end = spanned.getSpanEnd(span);
            spanned.removeSpan(span);
            spanned.setSpan(new URLSpanNoUnderline(span.getURL()) {
                public void onClick(View widget) {
                    fragment.dismissCurrentDialog();
                    super.onClick(widget);
                }
            }, start, end, 0);
        }
        message.setText(spanned);
        message.setTextSize(1, 16.0f);
        message.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        message.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
        message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        message.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) fragment.getParentActivity());
        builder1.setView(message);
        builder1.setTitle(LocaleController.getString("AskAQuestion", R.string.AskAQuestion));
        builder1.setPositiveButton(LocaleController.getString("AskButton", R.string.AskButton), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.performAskAQuestion(BaseFragment.this);
            }
        });
        builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        return builder1.create();
    }

    /* access modifiers changed from: private */
    public static void performAskAQuestion(BaseFragment fragment) {
        String userString;
        int currentAccount = fragment.getCurrentAccount();
        SharedPreferences preferences = MessagesController.getMainSettings(currentAccount);
        int uid = preferences.getInt("support_id", 0);
        TLRPC.User supportUser = null;
        if (!(uid == 0 || (supportUser = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(uid))) != null || (userString = preferences.getString("support_user", (String) null)) == null)) {
            try {
                byte[] datacentersBytes = Base64.decode(userString, 0);
                if (datacentersBytes != null) {
                    SerializedData data = new SerializedData(datacentersBytes);
                    supportUser = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                    if (supportUser != null && supportUser.id == 333000) {
                        supportUser = null;
                    }
                    data.cleanup();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                supportUser = null;
            }
        }
        if (supportUser == null) {
            AlertDialog progressDialog = new AlertDialog(fragment.getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC.TL_help_getSupport(), new RequestDelegate(preferences, progressDialog, currentAccount, fragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ BaseFragment f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AlertsCreator.lambda$performAskAQuestion$10(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$performAskAQuestion$10(SharedPreferences preferences, AlertDialog progressDialog, int currentAccount, BaseFragment fragment, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(preferences, (TLRPC.TL_help_support) response, progressDialog, currentAccount, fragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ TLRPC.TL_help_support f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ BaseFragment f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    AlertsCreator.lambda$null$8(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    AlertsCreator.lambda$null$9(AlertDialog.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$8(SharedPreferences preferences, TLRPC.TL_help_support res, AlertDialog progressDialog, int currentAccount, BaseFragment fragment) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("support_id", res.user.id);
        SerializedData data = new SerializedData();
        res.user.serializeToStream(data);
        editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
        editor.commit();
        data.cleanup();
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(currentAccount).putUser(res.user, false);
        Bundle args = new Bundle();
        args.putInt("user_id", res.user.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$null$9(AlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment fragment, boolean clear, TLRPC.Chat chat, TLRPC.User user, boolean secret, MessagesStorage.BooleanCallback onProcessRunnable) {
        createClearOrDeleteDialogAlert(fragment, clear, false, false, chat, user, secret, onProcessRunnable);
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x029c  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x02ae  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x042d  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0439  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x04f5  */
    /* JADX WARNING: Removed duplicated region for block: B:173:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0275  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createClearOrDeleteDialogAlert(im.bclpbkiauv.ui.actionbar.BaseFragment r41, boolean r42, boolean r43, boolean r44, im.bclpbkiauv.tgnet.TLRPC.Chat r45, im.bclpbkiauv.tgnet.TLRPC.User r46, boolean r47, im.bclpbkiauv.messenger.MessagesStorage.BooleanCallback r48) {
        /*
            r11 = r41
            r12 = r45
            r13 = r46
            if (r11 == 0) goto L_0x04ff
            androidx.fragment.app.FragmentActivity r0 = r41.getParentActivity()
            if (r0 == 0) goto L_0x04ff
            if (r12 != 0) goto L_0x0015
            if (r13 != 0) goto L_0x0015
            r1 = r11
            goto L_0x0500
        L_0x0015:
            int r14 = r41.getCurrentAccount()
            androidx.fragment.app.FragmentActivity r15 = r41.getParentActivity()
            im.bclpbkiauv.ui.hviews.dialogs.XDialog$Builder r0 = new im.bclpbkiauv.ui.hviews.dialogs.XDialog$Builder
            r0.<init>(r15)
            r10 = r0
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r14)
            int r9 = r0.getClientUserId()
            r0 = 1
            im.bclpbkiauv.ui.cells.CheckBoxCell[] r8 = new im.bclpbkiauv.ui.cells.CheckBoxCell[r0]
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r15)
            r7 = r1
            java.lang.String r1 = "dialogTextBlack"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r7.setTextColor(r1)
            r1 = 1098907648(0x41800000, float:16.0)
            r7.setTextSize(r0, r1)
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0048
            r2 = 5
            goto L_0x0049
        L_0x0048:
            r2 = 3
        L_0x0049:
            r2 = r2 | 48
            r7.setGravity(r2)
            boolean r2 = im.bclpbkiauv.messenger.ChatObject.isChannel(r45)
            if (r2 == 0) goto L_0x005e
            java.lang.String r2 = r12.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x005e
            r2 = 1
            goto L_0x005f
        L_0x005e:
            r2 = 0
        L_0x005f:
            r16 = r2
            im.bclpbkiauv.ui.components.AlertsCreator$3 r2 = new im.bclpbkiauv.ui.components.AlertsCreator$3
            r2.<init>(r15, r8)
            r6 = r2
            r10.setView(r6)
            im.bclpbkiauv.ui.components.AvatarDrawable r2 = new im.bclpbkiauv.ui.components.AvatarDrawable
            r2.<init>()
            r17 = 1094713344(0x41400000, float:12.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            r2.setTextSize(r3)
            im.bclpbkiauv.ui.components.BackupImageView r3 = new im.bclpbkiauv.ui.components.BackupImageView
            r3.<init>(r15)
            r4 = 1101004800(0x41a00000, float:20.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r3.setRoundRadius(r1)
            r20 = 1109393408(0x42200000, float:40.0)
            r21 = 1109393408(0x42200000, float:40.0)
            boolean r1 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0090
            r1 = 5
            goto L_0x0091
        L_0x0090:
            r1 = 3
        L_0x0091:
            r22 = r1 | 48
            r23 = 1102053376(0x41b00000, float:22.0)
            r24 = 1097859072(0x41700000, float:15.0)
            r25 = 1102053376(0x41b00000, float:22.0)
            r26 = 0
            android.widget.FrameLayout$LayoutParams r1 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r6.addView(r3, r1)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r15)
            java.lang.String r20 = "actionBarDefaultSubmenuItem"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r20)
            r1.setTextColor(r5)
            r1.setTextSize(r0, r4)
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r4 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r4)
            r1.setTypeface(r4)
            r1.setLines(r0)
            r1.setMaxLines(r0)
            r1.setSingleLine(r0)
            boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x00cb
            r4 = 5
            goto L_0x00cc
        L_0x00cb:
            r4 = 3
        L_0x00cc:
            r4 = r4 | 16
            r1.setGravity(r4)
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r1.setEllipsize(r4)
            java.lang.String r5 = "ClearHistoryCache"
            java.lang.String r0 = "ClearHistory"
            java.lang.String r4 = "DeleteChatUser"
            if (r42 == 0) goto L_0x00fa
            if (r16 == 0) goto L_0x00ed
            r25 = r10
            r10 = 2131690616(0x7f0f0478, float:1.901028E38)
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r10)
            r1.setText(r11)
            goto L_0x0137
        L_0x00ed:
            r25 = r10
            r10 = 2131690615(0x7f0f0477, float:1.9010279E38)
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r0, r10)
            r1.setText(r11)
            goto L_0x0137
        L_0x00fa:
            r25 = r10
            if (r43 == 0) goto L_0x012d
            boolean r10 = im.bclpbkiauv.messenger.ChatObject.isChannel(r45)
            java.lang.String r11 = "DeleteMegaMenu"
            if (r10 == 0) goto L_0x0122
            boolean r10 = r12.megagroup
            if (r10 == 0) goto L_0x0115
            r10 = 2131690858(0x7f0f056a, float:1.9010771E38)
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            r1.setText(r10)
            goto L_0x0137
        L_0x0115:
            r10 = 2131690422(0x7f0f03b6, float:1.9009887E38)
            java.lang.String r11 = "ChannelDeleteMenu"
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            r1.setText(r10)
            goto L_0x0137
        L_0x0122:
            r10 = 2131690858(0x7f0f056a, float:1.9010771E38)
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            r1.setText(r10)
            goto L_0x0137
        L_0x012d:
            r10 = 2131690846(0x7f0f055e, float:1.9010747E38)
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r10)
            r1.setText(r11)
        L_0x0137:
            r26 = -1082130432(0xffffffffbf800000, float:-1.0)
            r27 = -1073741824(0xffffffffc0000000, float:-2.0)
            boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0141
            r10 = 5
            goto L_0x0142
        L_0x0141:
            r10 = 3
        L_0x0142:
            r28 = r10 | 48
            boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
            r11 = 21
            r29 = 76
            if (r10 == 0) goto L_0x014f
            r10 = 21
            goto L_0x0151
        L_0x014f:
            r10 = 76
        L_0x0151:
            float r10 = (float) r10
            r30 = 1101529088(0x41a80000, float:21.0)
            boolean r31 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r31 == 0) goto L_0x015a
            r11 = 76
        L_0x015a:
            float r11 = (float) r11
            r32 = 0
            r29 = r10
            r31 = r11
            android.widget.FrameLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r6.addView(r1, r10)
            r26 = -1073741824(0xffffffffc0000000, float:-2.0)
            r27 = -1073741824(0xffffffffc0000000, float:-2.0)
            boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0173
            r17 = 5
            goto L_0x0175
        L_0x0173:
            r17 = 3
        L_0x0175:
            r28 = r17 | 48
            r29 = 1103101952(0x41c00000, float:24.0)
            r30 = 1116078080(0x42860000, float:67.0)
            r31 = 1103101952(0x41c00000, float:24.0)
            r32 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r6.addView(r7, r10)
            if (r13 == 0) goto L_0x019a
            boolean r10 = r13.bot
            if (r10 != 0) goto L_0x019a
            int r10 = r13.id
            if (r10 == r9) goto L_0x019a
            im.bclpbkiauv.messenger.MessagesController r10 = im.bclpbkiauv.messenger.MessagesController.getInstance(r14)
            boolean r10 = r10.canRevokePmInbox
            if (r10 == 0) goto L_0x019a
            r10 = 1
            goto L_0x019b
        L_0x019a:
            r10 = 0
        L_0x019b:
            r11 = 0
            if (r13 == 0) goto L_0x01a5
            im.bclpbkiauv.messenger.MessagesController r10 = im.bclpbkiauv.messenger.MessagesController.getInstance(r14)
            int r10 = r10.revokeTimePmLimit
            goto L_0x01ab
        L_0x01a5:
            im.bclpbkiauv.messenger.MessagesController r10 = im.bclpbkiauv.messenger.MessagesController.getInstance(r14)
            int r10 = r10.revokeTimeLimit
        L_0x01ab:
            if (r47 != 0) goto L_0x01ba
            if (r13 == 0) goto L_0x01ba
            if (r11 == 0) goto L_0x01ba
            r17 = r1
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r10 != r1) goto L_0x01bc
            r1 = 1
            goto L_0x01bd
        L_0x01ba:
            r17 = r1
        L_0x01bc:
            r1 = 0
        L_0x01bd:
            r18 = r1
            r26 = r11
            r1 = 1
            boolean[] r11 = new boolean[r1]
            if (r44 != 0) goto L_0x0266
            if (r18 == 0) goto L_0x0266
            r27 = r10
            im.bclpbkiauv.ui.cells.CheckBoxCell r10 = new im.bclpbkiauv.ui.cells.CheckBoxCell
            r10.<init>(r15, r1)
            r1 = 0
            r8[r1] = r10
            r10 = r8[r1]
            r28 = r14
            android.graphics.drawable.Drawable r14 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r1)
            r10.setBackgroundDrawable(r14)
            java.lang.String r10 = ""
            if (r42 == 0) goto L_0x0200
            r14 = r8[r1]
            r30 = r15
            r1 = 1
            java.lang.Object[] r15 = new java.lang.Object[r1]
            java.lang.String r20 = im.bclpbkiauv.messenger.UserObject.getFirstName(r46)
            r1 = 0
            r15[r1] = r20
            r32 = r4
            java.lang.String r4 = "ClearHistoryOptionAlso"
            r33 = r0
            r0 = 2131690617(0x7f0f0479, float:1.9010283E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r15)
            r14.setText(r0, r10, r1, r1)
            goto L_0x021d
        L_0x0200:
            r33 = r0
            r32 = r4
            r30 = r15
            r0 = r8[r1]
            r4 = 2131690860(0x7f0f056c, float:1.9010776E38)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            java.lang.String r14 = im.bclpbkiauv.messenger.UserObject.getFirstName(r46)
            r15[r1] = r14
            java.lang.String r14 = "DeleteMessagesOptionAlso"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.formatString(r14, r4, r15)
            r0.setText(r4, r10, r1, r1)
        L_0x021d:
            r0 = r8[r1]
            boolean r1 = im.bclpbkiauv.messenger.LocaleController.isRTL
            r4 = 1090519040(0x41000000, float:8.0)
            if (r1 == 0) goto L_0x022c
            r1 = 1098907648(0x41800000, float:16.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            goto L_0x0230
        L_0x022c:
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
        L_0x0230:
            boolean r1 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0239
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            goto L_0x023f
        L_0x0239:
            r1 = 1098907648(0x41800000, float:16.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
        L_0x023f:
            r4 = 0
            r0.setPadding(r10, r4, r1, r4)
            r0 = r8[r4]
            r34 = -1082130432(0xffffffffbf800000, float:-1.0)
            r35 = 1111490560(0x42400000, float:48.0)
            r36 = 83
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            android.widget.FrameLayout$LayoutParams r1 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r34, r35, r36, r37, r38, r39, r40)
            r6.addView(r0, r1)
            r0 = 0
            r1 = r8[r0]
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$1qoNFomjdKxcaGCUKMY6Mr0nVLQ r0 = new im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$1qoNFomjdKxcaGCUKMY6Mr0nVLQ
            r0.<init>(r11)
            r1.setOnClickListener(r0)
            goto L_0x0270
        L_0x0266:
            r33 = r0
            r32 = r4
            r27 = r10
            r28 = r14
            r30 = r15
        L_0x0270:
            java.lang.String r0 = "50_50"
            r14 = 0
            if (r13 == 0) goto L_0x028d
            int r1 = r13.id
            if (r1 != r9) goto L_0x0281
            r0 = 2
            r2.setAvatarType(r0)
            r3.setImage((im.bclpbkiauv.messenger.ImageLocation) r14, (java.lang.String) r14, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r13)
            goto L_0x029a
        L_0x0281:
            r2.setInfo((im.bclpbkiauv.tgnet.TLRPC.User) r13)
            r1 = 0
            im.bclpbkiauv.messenger.ImageLocation r4 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r13, r1)
            r3.setImage((im.bclpbkiauv.messenger.ImageLocation) r4, (java.lang.String) r0, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r13)
            goto L_0x029a
        L_0x028d:
            r1 = 0
            if (r12 == 0) goto L_0x029a
            r2.setInfo((im.bclpbkiauv.tgnet.TLRPC.Chat) r12)
            im.bclpbkiauv.messenger.ImageLocation r4 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r12, r1)
            r3.setImage((im.bclpbkiauv.messenger.ImageLocation) r4, (java.lang.String) r0, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r12)
        L_0x029a:
            if (r44 == 0) goto L_0x02ae
            r0 = 2131690839(0x7f0f0557, float:1.9010733E38)
            java.lang.String r1 = "DeleteAllMessagesAlert"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x02ae:
            if (r42 == 0) goto L_0x0351
            if (r13 == 0) goto L_0x0302
            if (r47 == 0) goto L_0x02d0
            r0 = 2131689893(0x7f0f01a5, float:1.9008814E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r4 = im.bclpbkiauv.messenger.UserObject.getUserName(r46)
            r10 = 0
            r1[r10] = r4
            java.lang.String r4 = "AreYouSureClearHistoryWithSecretUser"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r1)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x02d0:
            int r0 = r13.id
            if (r0 != r9) goto L_0x02e6
            r0 = 2131689891(0x7f0f01a3, float:1.900881E38)
            java.lang.String r1 = "AreYouSureClearHistorySavedMessages"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x02e6:
            r0 = 2131689894(0x7f0f01a6, float:1.9008816E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r4 = im.bclpbkiauv.messenger.UserObject.getUserName(r46)
            r10 = 0
            r1[r10] = r4
            java.lang.String r4 = "AreYouSureClearHistoryWithUser"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r1)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x0302:
            if (r12 == 0) goto L_0x042b
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r45)
            if (r0 == 0) goto L_0x0337
            boolean r0 = r12.megagroup
            if (r0 == 0) goto L_0x0317
            java.lang.String r0 = r12.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0317
            goto L_0x0337
        L_0x0317:
            boolean r0 = r12.megagroup
            if (r0 == 0) goto L_0x0329
            r0 = 2131689890(0x7f0f01a2, float:1.9008808E38)
            java.lang.String r1 = "AreYouSureClearHistoryGroup"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x0329:
            r0 = 2131689888(0x7f0f01a0, float:1.9008804E38)
            java.lang.String r1 = "AreYouSureClearHistoryChannel"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x0337:
            r0 = 2131689892(0x7f0f01a4, float:1.9008812E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r4 = r12.title
            r10 = 0
            r1[r10] = r4
            java.lang.String r4 = "AreYouSureClearHistoryWithChat"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r1)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x0351:
            if (r43 == 0) goto L_0x0387
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r45)
            if (r0 == 0) goto L_0x0379
            boolean r0 = r12.megagroup
            if (r0 == 0) goto L_0x036b
            r0 = 2131691950(0x7f0f09ae, float:1.9012986E38)
            java.lang.String r1 = "MegaDeleteAlert"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x036b:
            r0 = 2131690419(0x7f0f03b3, float:1.9009881E38)
            java.lang.String r1 = "ChannelDeleteAlert"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x0379:
            r0 = 2131689895(0x7f0f01a7, float:1.9008818E38)
            java.lang.String r1 = "AreYouSureDeleteAndExit"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x0387:
            if (r13 == 0) goto L_0x03d7
            if (r47 == 0) goto L_0x03a7
            r0 = 2131689910(0x7f0f01b6, float:1.9008849E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r4 = im.bclpbkiauv.messenger.UserObject.getUserName(r46)
            r10 = 0
            r1[r10] = r4
            java.lang.String r4 = "AreYouSureDeleteThisChatWithSecretUser"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r1)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x03a7:
            int r0 = r13.id
            if (r0 != r9) goto L_0x03bc
            r0 = 2131689908(0x7f0f01b4, float:1.9008845E38)
            java.lang.String r1 = "AreYouSureDeleteThisChatSavedMessages"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x03bc:
            r0 = 2131689911(0x7f0f01b7, float:1.900885E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r4 = im.bclpbkiauv.messenger.UserObject.getUserName(r46)
            r10 = 0
            r1[r10] = r4
            java.lang.String r4 = "AreYouSureDeleteThisChatWithUser"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r1)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x03d7:
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r45)
            if (r0 == 0) goto L_0x0413
            boolean r0 = r12.megagroup
            if (r0 == 0) goto L_0x03fa
            r0 = 2131691953(0x7f0f09b1, float:1.9012992E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r4 = r12.title
            r10 = 0
            r1[r10] = r4
            java.lang.String r4 = "MegaLeaveAlertWithName"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r1)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x03fa:
            r1 = 1
            r10 = 0
            r0 = 2131690434(0x7f0f03c2, float:1.9009912E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r4 = r12.title
            r1[r10] = r4
            java.lang.String r4 = "ChannelLeaveAlertWithName"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r1)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x042b
        L_0x0413:
            r1 = 1
            r10 = 0
            r0 = 2131689896(0x7f0f01a8, float:1.900882E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r4 = r12.title
            r1[r10] = r4
            java.lang.String r4 = "AreYouSureDeleteAndExitName"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r1)
            android.text.SpannableStringBuilder r0 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
        L_0x042b:
            if (r44 == 0) goto L_0x0439
            r0 = 2131690837(0x7f0f0555, float:1.9010729E38)
            java.lang.String r1 = "DeleteAll"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r15 = r0
            goto L_0x04a7
        L_0x0439:
            if (r42 == 0) goto L_0x0452
            if (r16 == 0) goto L_0x0447
            r0 = 2131690616(0x7f0f0478, float:1.901028E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r0)
            r15 = r0
            goto L_0x04a7
        L_0x0447:
            r1 = r33
            r0 = 2131690615(0x7f0f0477, float:1.9010279E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r15 = r0
            goto L_0x04a7
        L_0x0452:
            if (r43 == 0) goto L_0x047d
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r45)
            java.lang.String r1 = "DeleteMega"
            if (r0 == 0) goto L_0x0474
            boolean r0 = r12.megagroup
            if (r0 == 0) goto L_0x0469
            r0 = 2131690857(0x7f0f0569, float:1.901077E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r15 = r0
            goto L_0x04a7
        L_0x0469:
            r0 = 2131690418(0x7f0f03b2, float:1.900988E38)
            java.lang.String r1 = "ChannelDelete"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r15 = r0
            goto L_0x04a7
        L_0x0474:
            r0 = 2131690857(0x7f0f0569, float:1.901077E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r15 = r0
            goto L_0x04a7
        L_0x047d:
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r45)
            if (r0 == 0) goto L_0x049d
            boolean r0 = r12.megagroup
            if (r0 == 0) goto L_0x0492
            r0 = 2131691805(0x7f0f091d, float:1.9012692E38)
            java.lang.String r1 = "LeaveMegaMenu"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r15 = r0
            goto L_0x04a7
        L_0x0492:
            r0 = 2131691804(0x7f0f091c, float:1.901269E38)
            java.lang.String r1 = "LeaveChannelMenu"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r15 = r0
            goto L_0x04a7
        L_0x049d:
            r1 = r32
            r0 = 2131690846(0x7f0f055e, float:1.9010747E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r15 = r0
        L_0x04a7:
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$CywoUF7Uut7wtECf8g4UmJUlvNg r10 = new im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$CywoUF7Uut7wtECf8g4UmJUlvNg
            r0 = r10
            r1 = r46
            r19 = r2
            r2 = r16
            r20 = r3
            r3 = r44
            r4 = r11
            r5 = r41
            r21 = r6
            r6 = r42
            r22 = r7
            r7 = r43
            r23 = r8
            r8 = r45
            r24 = r9
            r9 = r47
            r14 = r25
            r25 = r27
            r27 = r11
            r11 = r10
            r10 = r48
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r14.setPositiveButton(r15, r11)
            r0 = 2131690308(0x7f0f0344, float:1.9009656E38)
            java.lang.String r1 = "Cancel"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r1 = 0
            r14.setNegativeButton(r0, r1)
            im.bclpbkiauv.ui.hviews.dialogs.XDialog r0 = r14.create()
            r1 = r41
            r1.showDialog(r0)
            r2 = -1
            android.view.View r2 = r0.getButton(r2)
            android.widget.TextView r2 = (android.widget.TextView) r2
            if (r2 == 0) goto L_0x04fe
            java.lang.String r3 = "dialogTextRed2"
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r2.setTextColor(r3)
        L_0x04fe:
            return
        L_0x04ff:
            r1 = r11
        L_0x0500:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.AlertsCreator.createClearOrDeleteDialogAlert(im.bclpbkiauv.ui.actionbar.BaseFragment, boolean, boolean, boolean, im.bclpbkiauv.tgnet.TLRPC$Chat, im.bclpbkiauv.tgnet.TLRPC$User, boolean, im.bclpbkiauv.messenger.MessagesStorage$BooleanCallback):void");
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$11(boolean[] deleteForAll, View v) {
        deleteForAll[0] = !deleteForAll[0];
        ((CheckBoxCell) v).setChecked(deleteForAll[0], true);
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$13(TLRPC.User user, boolean clearingCache, boolean second, boolean[] deleteForAll, BaseFragment fragment, boolean clear, boolean admin, TLRPC.Chat chat, boolean secret, MessagesStorage.BooleanCallback onProcessRunnable, DialogInterface dialogInterface, int i) {
        TLRPC.User user2 = user;
        MessagesStorage.BooleanCallback booleanCallback = onProcessRunnable;
        boolean z = false;
        if (user2 != null && !clearingCache && !second && deleteForAll[0]) {
            MessagesStorage.getInstance(fragment.getCurrentAccount()).getMessagesCount((long) user2.id, new MessagesStorage.IntCallback(clear, admin, chat, user, secret, onProcessRunnable, deleteForAll) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ TLRPC.Chat f$3;
                private final /* synthetic */ TLRPC.User f$4;
                private final /* synthetic */ boolean f$5;
                private final /* synthetic */ MessagesStorage.BooleanCallback f$6;
                private final /* synthetic */ boolean[] f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run(int i) {
                    AlertsCreator.lambda$null$12(BaseFragment.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, i);
                }
            });
        } else if (booleanCallback != null) {
            if (second || deleteForAll[0]) {
                z = true;
            }
            booleanCallback.run(z);
        }
    }

    static /* synthetic */ void lambda$null$12(BaseFragment fragment, boolean clear, boolean admin, TLRPC.Chat chat, TLRPC.User user, boolean secret, MessagesStorage.BooleanCallback onProcessRunnable, boolean[] deleteForAll, int count) {
        MessagesStorage.BooleanCallback booleanCallback = onProcessRunnable;
        if (count >= 50) {
            createClearOrDeleteDialogAlert(fragment, clear, admin, true, chat, user, secret, onProcessRunnable);
        } else if (booleanCallback != null) {
            booleanCallback.run(deleteForAll[0]);
        }
    }

    public static AlertDialog.Builder createDatePickerDialog(Context context, int minYear, int maxYear, int currentYearDiff, int selectedDay, int selectedMonth, int selectedYear, String title, boolean checkMinDate, DatePickerDelegate datePickerDelegate) {
        Context context2 = context;
        int i = selectedDay;
        boolean z = checkMinDate;
        if (context2 == null) {
            return null;
        }
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        NumberPicker monthPicker = new NumberPicker(context2);
        NumberPicker dayPicker = new NumberPicker(context2);
        NumberPicker yearPicker = new NumberPicker(context2);
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        dayPicker.setOnScrollListener(new NumberPicker.OnScrollListener(z, dayPicker, monthPicker, yearPicker) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$14(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
            }
        });
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        linearLayout.addView(monthPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        monthPicker.setFormatter($$Lambda$AlertsCreator$g0a9aIjGB6oWpTTFqOBRwqtVHG0.INSTANCE);
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(monthPicker, yearPicker) {
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.updateDayPicker(NumberPicker.this, this.f$1, this.f$2);
            }
        });
        monthPicker.setOnScrollListener(new NumberPicker.OnScrollListener(z, dayPicker, monthPicker, yearPicker) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$17(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        yearPicker.setMinValue(currentYear + minYear);
        yearPicker.setMaxValue(currentYear + maxYear);
        yearPicker.setValue(currentYear + currentYearDiff);
        linearLayout.addView(yearPicker, LayoutHelper.createLinear(0, -2, 0.4f));
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(monthPicker, yearPicker) {
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.updateDayPicker(NumberPicker.this, this.f$1, this.f$2);
            }
        });
        yearPicker.setOnScrollListener(new NumberPicker.OnScrollListener(z, dayPicker, monthPicker, yearPicker) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$19(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
            }
        });
        updateDayPicker(dayPicker, monthPicker, yearPicker);
        if (z) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
        if (i != -1) {
            dayPicker.setValue(i);
            monthPicker.setValue(selectedMonth);
            yearPicker.setValue(selectedYear);
        } else {
            int i2 = selectedMonth;
            int i3 = selectedYear;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        builder.setTitle(title);
        builder.setView(linearLayout);
        $$Lambda$AlertsCreator$4sXDOh89oA0e2IdgpbPuQgBGOk r16 = r2;
        String string = LocaleController.getString("Set", R.string.Set);
        AlertDialog.Builder builder2 = builder;
        $$Lambda$AlertsCreator$4sXDOh89oA0e2IdgpbPuQgBGOk r2 = new DialogInterface.OnClickListener(checkMinDate, dayPicker, monthPicker, yearPicker, datePickerDelegate) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;
            private final /* synthetic */ AlertsCreator.DatePickerDelegate f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createDatePickerDialog$20(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
            }
        };
        builder2.setPositiveButton(string, r2);
        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        return builder2;
    }

    static /* synthetic */ void lambda$createDatePickerDialog$14(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    static /* synthetic */ String lambda$createDatePickerDialog$15(int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        calendar.set(2, value);
        return calendar.getDisplayName(2, 1, Locale.getDefault());
    }

    static /* synthetic */ void lambda$createDatePickerDialog$17(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$19(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$20(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, DatePickerDelegate datePickerDelegate, DialogInterface dialog, int which) {
        if (checkMinDate) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
        datePickerDelegate.didSelectDate(yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
    }

    /* access modifiers changed from: private */
    public static boolean checkScheduleDate(TextView button, boolean reminder, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker) {
        long currentTime;
        int num;
        TextView textView = button;
        int day = dayPicker.getValue();
        int hour = hourPicker.getValue();
        int minute = minutePicker.getValue();
        Calendar calendar = Calendar.getInstance();
        long systemTime = System.currentTimeMillis();
        calendar.setTimeInMillis(systemTime);
        int currentYear = calendar.get(1);
        int currentDay = calendar.get(6);
        calendar.setTimeInMillis(System.currentTimeMillis() + (((long) day) * 24 * 3600 * 1000));
        calendar.set(11, hour);
        calendar.set(12, minute);
        long currentTime2 = calendar.getTimeInMillis();
        if (currentTime2 <= systemTime + DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS) {
            currentTime = currentTime2;
            calendar.setTimeInMillis(systemTime + DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
            if (currentDay != calendar.get(6)) {
                day = 1;
                dayPicker.setValue(1);
            } else {
                NumberPicker numberPicker = dayPicker;
            }
            int i = calendar.get(11);
            hour = i;
            hourPicker.setValue(i);
            int i2 = calendar.get(12);
            minute = i2;
            minutePicker.setValue(i2);
        } else {
            NumberPicker numberPicker2 = dayPicker;
            NumberPicker numberPicker3 = minutePicker;
            currentTime = currentTime2;
            NumberPicker numberPicker4 = hourPicker;
        }
        int selectedYear = calendar.get(1);
        int currentYear2 = currentYear;
        calendar.setTimeInMillis(System.currentTimeMillis() + (((long) day) * 24 * 3600 * 1000));
        calendar.set(11, hour);
        calendar.set(12, minute);
        if (textView != null) {
            long time = calendar.getTimeInMillis();
            if (day == 0) {
                num = 0;
                int i3 = currentYear2;
            } else if (currentYear2 == selectedYear) {
                num = 1;
            } else {
                num = 2;
            }
            if (reminder) {
                num += 3;
            }
            int i4 = day;
            textView.setText(LocaleController.getInstance().formatterScheduleSend[num].format(time));
        } else {
            int i5 = currentYear2;
        }
        return currentTime - systemTime > DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS;
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, boolean reminder, ScheduleDatePickerDelegate datePickerDelegate) {
        return createScheduleDatePickerDialog(context, reminder, -1, datePickerDelegate, (Runnable) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, boolean reminder, ScheduleDatePickerDelegate datePickerDelegate, Runnable cancelRunnable) {
        return createScheduleDatePickerDialog(context, reminder, -1, datePickerDelegate, cancelRunnable);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, boolean reminder, long currentDate, ScheduleDatePickerDelegate datePickerDelegate, Runnable cancelRunnable) {
        String str;
        int i;
        TextView buttonTextView;
        Calendar calendar;
        Context context2 = context;
        boolean z = reminder;
        if (context2 == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false, 1);
        builder.setApplyBottomPadding(false);
        final NumberPicker dayPicker = new NumberPicker(context2);
        dayPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        dayPicker.setItemCount(5);
        final NumberPicker hourPicker = new NumberPicker(context2);
        hourPicker.setItemCount(5);
        hourPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker minutePicker = new NumberPicker(context2);
        minutePicker.setItemCount(5);
        minutePicker.setTextOffset(-AndroidUtilities.dp(34.0f));
        LinearLayout container = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                dayPicker.setItemCount(count);
                hourPicker.setItemCount(count);
                minutePicker.setItemCount(count);
                dayPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                hourPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                minutePicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        container.setOrientation(1);
        TextView titleView = new TextView(context2);
        if (z) {
            i = R.string.SetReminder;
            str = "SetReminder";
        } else {
            i = R.string.ScheduleMessage;
            str = "ScheduleMessage";
        }
        titleView.setText(LocaleController.getString(str, i));
        titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        container.addView(titleView, LayoutHelper.createLinear(-1, -2, 51, 22, 12, 22, 4));
        titleView.setOnTouchListener($$Lambda$AlertsCreator$ncpR1z6hR_Nd75IvugRjUZ1wAKM.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        container.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        long currentTime = System.currentTimeMillis();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(currentTime);
        int currentYear = calendar2.get(1);
        TextView buttonTextView2 = new TextView(context2);
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(365);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setFormatter(new NumberPicker.Formatter(currentTime, calendar2, currentYear) {
            private final /* synthetic */ long f$0;
            private final /* synthetic */ Calendar f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
            }

            public final String format(int i) {
                return AlertsCreator.lambda$createScheduleDatePickerDialog$22(this.f$0, this.f$1, this.f$2, i);
            }
        });
        int i2 = currentYear;
        Calendar calendar3 = calendar2;
        long j = currentTime;
        TextView textView = titleView;
        $$Lambda$AlertsCreator$xBZR5ztEvFqvlobbmlnBMyWNQ r1 = new NumberPicker.OnValueChangeListener(buttonTextView2, reminder, dayPicker, hourPicker, minutePicker) {
            private final /* synthetic */ TextView f$0;
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;
            private final /* synthetic */ NumberPicker f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.checkScheduleDate(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
            }
        };
        dayPicker.setOnValueChangedListener(r1);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        LinearLayout linearLayout2 = linearLayout;
        linearLayout2.addView(hourPicker, LayoutHelper.createLinear(0, 270, 0.2f));
        hourPicker.setFormatter($$Lambda$AlertsCreator$rQqan64PXXPErzPk8tKKI3IDp_k.INSTANCE);
        hourPicker.setOnValueChangedListener(r1);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(0);
        minutePicker.setFormatter($$Lambda$AlertsCreator$mpAahbP6zOvPMioKeQGXZ2cs0Ts.INSTANCE);
        linearLayout2.addView(minutePicker, LayoutHelper.createLinear(0, 270, 0.3f));
        minutePicker.setOnValueChangedListener(r1);
        if (currentDate > 0) {
            long currentDate2 = 1000 * currentDate;
            calendar = calendar3;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(11, 0);
            buttonTextView = buttonTextView2;
            int days = (int) ((currentDate2 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(currentDate2);
            if (days >= 0) {
                minutePicker.setValue(calendar.get(12));
                hourPicker.setValue(calendar.get(11));
                dayPicker.setValue(days);
            }
            long j2 = currentDate2;
        } else {
            buttonTextView = buttonTextView2;
            calendar = calendar3;
            long j3 = currentDate;
        }
        boolean[] canceled = {true};
        TextView buttonTextView3 = buttonTextView;
        checkScheduleDate(buttonTextView3, z, dayPicker, hourPicker, minutePicker);
        buttonTextView3.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView3.setGravity(17);
        buttonTextView3.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        buttonTextView3.setTextSize(1, 14.0f);
        buttonTextView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        buttonTextView3.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable((float) AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        container.addView(buttonTextView3, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        LinearLayout linearLayout3 = linearLayout2;
        $$Lambda$AlertsCreator$xBZR5ztEvFqvlobbmlnBMyWNQ r18 = r1;
        $$Lambda$AlertsCreator$yV5Yx5FSOoN3ZrAWedoTyE5Xuw r10 = r1;
        NumberPicker numberPicker = dayPicker;
        $$Lambda$AlertsCreator$yV5Yx5FSOoN3ZrAWedoTyE5Xuw r12 = new View.OnClickListener(canceled, reminder, dayPicker, hourPicker, minutePicker, calendar, datePickerDelegate, builder) {
            private final /* synthetic */ boolean[] f$0;
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;
            private final /* synthetic */ NumberPicker f$4;
            private final /* synthetic */ Calendar f$5;
            private final /* synthetic */ AlertsCreator.ScheduleDatePickerDelegate f$6;
            private final /* synthetic */ BottomSheet.Builder f$7;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void onClick(View view) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$26(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, view);
            }
        };
        buttonTextView3.setOnClickListener(r10);
        builder.setCustomView(container);
        builder.show().setOnDismissListener(new DialogInterface.OnDismissListener(cancelRunnable, canceled) {
            private final /* synthetic */ Runnable f$0;
            private final /* synthetic */ boolean[] f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$27(this.f$0, this.f$1, dialogInterface);
            }
        });
        return builder;
    }

    static /* synthetic */ boolean lambda$createScheduleDatePickerDialog$21(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ String lambda$createScheduleDatePickerDialog$22(long currentTime, Calendar calendar, int currentYear, int value) {
        if (value == 0) {
            return LocaleController.getString("MessageScheduleToday", R.string.MessageScheduleToday);
        }
        long date = (((long) value) * 86400000) + currentTime;
        calendar.setTimeInMillis(date);
        if (calendar.get(1) == currentYear) {
            return LocaleController.getInstance().formatterScheduleDay.format(date);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(date);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$26(boolean[] canceled, boolean reminder, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, Calendar calendar, ScheduleDatePickerDelegate datePickerDelegate, BottomSheet.Builder builder, View v) {
        Calendar calendar2 = calendar;
        canceled[0] = false;
        boolean z = reminder;
        NumberPicker numberPicker = dayPicker;
        boolean setSeconds = checkScheduleDate((TextView) null, reminder, dayPicker, hourPicker, minutePicker);
        calendar2.setTimeInMillis(System.currentTimeMillis() + (((long) dayPicker.getValue()) * 24 * 3600 * 1000));
        calendar2.set(11, hourPicker.getValue());
        calendar2.set(12, minutePicker.getValue());
        if (setSeconds) {
            calendar2.set(13, 0);
        }
        datePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$27(Runnable cancelRunnable, boolean[] canceled, DialogInterface dialog) {
        if (cancelRunnable != null && canceled[0]) {
            cancelRunnable.run();
        }
    }

    public static Dialog createMuteAlert(Context context, long dialog_id) {
        if (context == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
        builder.setItems(new CharSequence[]{LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 1)), LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 8)), LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Days", 2)), LocaleController.getString("MuteDisable", R.string.MuteDisable)}, new DialogInterface.OnClickListener(dialog_id) {
            private final /* synthetic */ long f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createMuteAlert$28(this.f$0, dialogInterface, i);
            }
        });
        return builder.create();
    }

    static /* synthetic */ void lambda$createMuteAlert$28(long dialog_id, DialogInterface dialogInterface, int i) {
        int setting;
        if (i == 0) {
            setting = 0;
        } else if (i == 1) {
            setting = 1;
        } else if (i == 2) {
            setting = 2;
        } else {
            setting = 3;
        }
        NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(dialog_id, setting);
    }

    public static void createReportAlert(Context context, long dialog_id, int messageId, BaseFragment parentFragment) {
        if (context != null && parentFragment != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(context);
            builder.setTitle(LocaleController.getString("ReportChat", R.string.ReportChat));
            builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", R.string.ReportChatSpam), LocaleController.getString("ReportChatViolence", R.string.ReportChatViolence), LocaleController.getString("ReportChatChild", R.string.ReportChatChild), LocaleController.getString("ReportChatPornography", R.string.ReportChatPornography), LocaleController.getString("ReportChatOther", R.string.ReportChatOther)}, new DialogInterface.OnClickListener(dialog_id, messageId, parentFragment) {
                private final /* synthetic */ long f$0;
                private final /* synthetic */ int f$1;
                private final /* synthetic */ BaseFragment f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r3;
                    this.f$2 = r4;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createReportAlert$30(this.f$0, this.f$1, this.f$2, dialogInterface, i);
                }
            });
            parentFragment.showDialog(builder.create());
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_report} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createReportAlert$30(long r7, int r9, im.bclpbkiauv.ui.actionbar.BaseFragment r10, android.content.DialogInterface r11, int r12) {
        /*
            r0 = 4
            if (r12 != r0) goto L_0x001c
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "dialog_id"
            r0.putLong(r1, r7)
            long r1 = (long) r9
            java.lang.String r3 = "message_id"
            r0.putLong(r3, r1)
            im.bclpbkiauv.ui.ReportOtherActivity r1 = new im.bclpbkiauv.ui.ReportOtherActivity
            r1.<init>(r0)
            r10.presentFragment(r1)
            return
        L_0x001c:
            int r0 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            int r1 = (int) r7
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r0 = r0.getInputPeer(r1)
            r1 = 3
            r2 = 2
            r3 = 1
            if (r9 == 0) goto L_0x0065
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_report r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_report
            r4.<init>()
            r4.peer = r0
            java.util.ArrayList<java.lang.Integer> r5 = r4.id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r9)
            r5.add(r6)
            if (r12 != 0) goto L_0x0046
            im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonSpam r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonSpam
            r1.<init>()
            r4.reason = r1
            goto L_0x0063
        L_0x0046:
            if (r12 != r3) goto L_0x0050
            im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonViolence r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonViolence
            r1.<init>()
            r4.reason = r1
            goto L_0x0063
        L_0x0050:
            if (r12 != r2) goto L_0x005a
            im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonChildAbuse r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r1.<init>()
            r4.reason = r1
            goto L_0x0063
        L_0x005a:
            if (r12 != r1) goto L_0x0063
            im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonPornography r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonPornography
            r1.<init>()
            r4.reason = r1
        L_0x0063:
            r1 = r4
            goto L_0x0094
        L_0x0065:
            im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_account_reportPeer
            r4.<init>()
            r4.peer = r0
            if (r12 != 0) goto L_0x0076
            im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonSpam r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonSpam
            r1.<init>()
            r4.reason = r1
            goto L_0x0093
        L_0x0076:
            if (r12 != r3) goto L_0x0080
            im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonViolence r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonViolence
            r1.<init>()
            r4.reason = r1
            goto L_0x0093
        L_0x0080:
            if (r12 != r2) goto L_0x008a
            im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonChildAbuse r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r1.<init>()
            r4.reason = r1
            goto L_0x0093
        L_0x008a:
            if (r12 != r1) goto L_0x0093
            im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonPornography r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputReportReasonPornography
            r1.<init>()
            r4.reason = r1
        L_0x0093:
            r1 = r4
        L_0x0094:
            int r2 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r2 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r2)
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$hmgOMs1GYEjL6b8qvkdsOTsmrhw r3 = im.bclpbkiauv.ui.components.$$Lambda$AlertsCreator$hmgOMs1GYEjL6b8qvkdsOTsmrhw.INSTANCE
            r2.sendRequest(r1, r3)
            r2 = 2131693456(0x7f0f0f90, float:1.901604E38)
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((int) r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.AlertsCreator.lambda$createReportAlert$30(long, int, im.bclpbkiauv.ui.actionbar.BaseFragment, android.content.DialogInterface, int):void");
    }

    static /* synthetic */ void lambda$null$29(TLObject response, TLRPC.TL_error error) {
    }

    private static String getFloodWaitString(String error) {
        String timeString;
        int time = Utilities.parseInt(error).intValue();
        if (time < 60) {
            timeString = LocaleController.formatPluralString("Seconds", time);
        } else {
            timeString = LocaleController.formatPluralString("Minutes", time / 60);
        }
        return LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString);
    }

    public static void showFloodWaitAlert(String error, BaseFragment fragment) {
        String timeString;
        if (error != null && error.startsWith("FLOOD_WAIT") && fragment != null && fragment.getParentActivity() != null) {
            int time = Utilities.parseInt(error).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
        }
    }

    public static void showSendMediaAlert(int result, BaseFragment fragment) {
        if (result != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (result == 1) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickers", R.string.ErrorSendRestrictedStickers));
            } else if (result == 2) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedMedia", R.string.ErrorSendRestrictedMedia));
            } else if (result == 3) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedPolls", R.string.ErrorSendRestrictedPolls));
            } else if (result == 4) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickersAll", R.string.ErrorSendRestrictedStickersAll));
            } else if (result == 5) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedMediaAll", R.string.ErrorSendRestrictedMediaAll));
            } else if (result == 6) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedPollsAll", R.string.ErrorSendRestrictedPollsAll));
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
        }
    }

    public static void showAddUserAlert(String error, BaseFragment fragment, boolean isChannel, TLObject request) {
        if (error != null && fragment != null && fragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            char c = 65535;
            switch (error.hashCode()) {
                case -2120721660:
                    if (error.equals("CHANNELS_ADMIN_LOCATED_TOO_MUCH")) {
                        c = 17;
                        break;
                    }
                    break;
                case -2012133105:
                    if (error.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                        c = 16;
                        break;
                    }
                    break;
                case -1763467626:
                    if (error.equals("USERS_TOO_FEW")) {
                        c = 9;
                        break;
                    }
                    break;
                case -538116776:
                    if (error.equals("USER_BLOCKED")) {
                        c = 1;
                        break;
                    }
                    break;
                case -512775857:
                    if (error.equals("USER_RESTRICTED")) {
                        c = 10;
                        break;
                    }
                    break;
                case -454039871:
                    if (error.equals("PEER_FLOOD")) {
                        c = 0;
                        break;
                    }
                    break;
                case -420079733:
                    if (error.equals("BOTS_TOO_MUCH")) {
                        c = 7;
                        break;
                    }
                    break;
                case 98635865:
                    if (error.equals("USER_KICKED")) {
                        c = 13;
                        break;
                    }
                    break;
                case 517420851:
                    if (error.equals("USER_BOT")) {
                        c = 2;
                        break;
                    }
                    break;
                case 845559454:
                    if (error.equals("YOU_BLOCKED_USER")) {
                        c = 11;
                        break;
                    }
                    break;
                case 916342611:
                    if (error.equals("USER_ADMIN_INVALID")) {
                        c = 15;
                        break;
                    }
                    break;
                case 1047173446:
                    if (error.equals("CHAT_ADMIN_BAN_REQUIRED")) {
                        c = 12;
                        break;
                    }
                    break;
                case 1167301807:
                    if (error.equals("USERS_TOO_MUCH")) {
                        c = 4;
                        break;
                    }
                    break;
                case 1227003815:
                    if (error.equals("USER_ID_INVALID")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1253103379:
                    if (error.equals("ADMINS_TOO_MUCH")) {
                        c = 6;
                        break;
                    }
                    break;
                case 1355367367:
                    if (error.equals("CHANNELS_TOO_MUCH")) {
                        c = 18;
                        break;
                    }
                    break;
                case 1623167701:
                    if (error.equals("USER_NOT_MUTUAL_CONTACT")) {
                        c = 5;
                        break;
                    }
                    break;
                case 1754587486:
                    if (error.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
                        c = 14;
                        break;
                    }
                    break;
                case 1916725894:
                    if (error.equals("USER_PRIVACY_RESTRICTED")) {
                        c = 8;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.setMessage(LocaleController.getString("NobodyLikesSpam2", R.string.NobodyLikesSpam2));
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", R.string.MoreInfo), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MessagesController.getInstance(BaseFragment.this.getCurrentAccount()).openByUserName("spambot", BaseFragment.this, 1);
                        }
                    });
                    break;
                case 1:
                case 2:
                case 3:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdd", R.string.GroupUserCantAdd));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdd", R.string.ChannelUserCantAdd));
                        break;
                    }
                case 4:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserAddLimit", R.string.GroupUserAddLimit));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserAddLimit", R.string.ChannelUserAddLimit));
                        break;
                    }
                case 5:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserLeftError", R.string.GroupUserLeftError));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserLeftError", R.string.ChannelUserLeftError));
                        break;
                    }
                case 6:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdmin", R.string.GroupUserCantAdmin));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdmin", R.string.ChannelUserCantAdmin));
                        break;
                    }
                case 7:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantBot", R.string.GroupUserCantBot));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantBot", R.string.ChannelUserCantBot));
                        break;
                    }
                case 8:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("InviteToGroupError", R.string.InviteToGroupError));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("InviteToChannelError", R.string.InviteToChannelError));
                        break;
                    }
                case 9:
                    builder.setMessage(LocaleController.getString("CreateGroupError", R.string.CreateGroupError));
                    break;
                case 10:
                    builder.setMessage(LocaleController.getString("UserRestricted", R.string.UserRestricted));
                    break;
                case 11:
                    builder.setMessage(LocaleController.getString("YouBlockedUser", R.string.YouBlockedUser));
                    break;
                case 12:
                case 13:
                    builder.setMessage(LocaleController.getString("AddAdminErrorBlacklisted", R.string.AddAdminErrorBlacklisted));
                    break;
                case 14:
                    builder.setMessage(LocaleController.getString("AddAdminErrorNotAMember", R.string.AddAdminErrorNotAMember));
                    break;
                case 15:
                    builder.setMessage(LocaleController.getString("AddBannedErrorAdmin", R.string.AddBannedErrorAdmin));
                    break;
                case 16:
                    builder.setMessage(LocaleController.getString("PublicChannelsTooMuch", R.string.PublicChannelsTooMuch));
                    break;
                case 17:
                    builder.setMessage(LocaleController.getString("LocatedChannelsTooMuch", R.string.LocatedChannelsTooMuch));
                    break;
                case 18:
                    if (!(request instanceof TLRPC.TL_channels_createChannel)) {
                        builder.setMessage(LocaleController.getString("ChannelTooMuchJoin", R.string.ChannelTooMuchJoin));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelTooMuch", R.string.ChannelTooMuch));
                        break;
                    }
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error);
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
        }
    }

    public static Dialog createColorSelectDialog(Activity parentActivity, long dialog_id, int globalType, Runnable onSelect) {
        int currentColor;
        Activity activity = parentActivity;
        long j = dialog_id;
        int i = globalType;
        Runnable runnable = onSelect;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        if (j != 0) {
            if (preferences.contains("color_" + j)) {
                currentColor = preferences.getInt("color_" + j, -16776961);
            } else if (((int) j) < 0) {
                currentColor = preferences.getInt("GroupLed", -16776961);
            } else {
                currentColor = preferences.getInt("MessagesLed", -16776961);
            }
        } else if (i == 1) {
            currentColor = preferences.getInt("MessagesLed", -16776961);
        } else if (i == 0) {
            currentColor = preferences.getInt("GroupLed", -16776961);
        } else {
            currentColor = preferences.getInt("ChannelLed", -16776961);
        }
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        String[] descriptions = {LocaleController.getString("ColorRed", R.string.ColorRed), LocaleController.getString("ColorOrange", R.string.ColorOrange), LocaleController.getString("ColorYellow", R.string.ColorYellow), LocaleController.getString("ColorGreen", R.string.ColorGreen), LocaleController.getString("ColorCyan", R.string.ColorCyan), LocaleController.getString("ColorBlue", R.string.ColorBlue), LocaleController.getString("ColorViolet", R.string.ColorViolet), LocaleController.getString("ColorPink", R.string.ColorPink), LocaleController.getString("ColorWhite", R.string.ColorWhite)};
        int[] selectedColor = {currentColor};
        int a = 0;
        for (int i2 = 9; a < i2; i2 = 9) {
            RadioColorCell cell = new RadioColorCell(activity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(TextColorCell.colors[a], TextColorCell.colors[a]);
            cell.setTextAndValue(descriptions[a], currentColor == TextColorCell.colorsToSave[a]);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener(linearLayout, selectedColor) {
                private final /* synthetic */ LinearLayout f$0;
                private final /* synthetic */ int[] f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createColorSelectDialog$32(this.f$0, this.f$1, view);
                }
            });
            a++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        builder.setTitle(LocaleController.getString("LedColor", R.string.LedColor));
        builder.setView(linearLayout);
        $$Lambda$AlertsCreator$bT2HJ3oSd47QJyjLVZ_HET35QNs r0 = r1;
        SharedPreferences sharedPreferences = preferences;
        String string = LocaleController.getString("Set", R.string.Set);
        String[] strArr = descriptions;
        AlertDialog.Builder builder2 = builder;
        $$Lambda$AlertsCreator$bT2HJ3oSd47QJyjLVZ_HET35QNs r1 = new DialogInterface.OnClickListener(dialog_id, selectedColor, globalType, onSelect) {
            private final /* synthetic */ long f$0;
            private final /* synthetic */ int[] f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createColorSelectDialog$33(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
            }
        };
        builder2.setPositiveButton(string, r0);
        builder2.setNeutralButton(LocaleController.getString("LedDisabled", R.string.LedDisabled), new DialogInterface.OnClickListener(j, i, runnable) {
            private final /* synthetic */ long f$0;
            private final /* synthetic */ int f$1;
            private final /* synthetic */ Runnable f$2;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createColorSelectDialog$34(this.f$0, this.f$1, this.f$2, dialogInterface, i);
            }
        });
        if (j != 0) {
            builder2.setNegativeButton(LocaleController.getString("Default", R.string.Default), new DialogInterface.OnClickListener(j, runnable) {
                private final /* synthetic */ long f$0;
                private final /* synthetic */ Runnable f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createColorSelectDialog$35(this.f$0, this.f$1, dialogInterface, i);
                }
            });
        }
        return builder2.create();
    }

    static /* synthetic */ void lambda$createColorSelectDialog$32(LinearLayout linearLayout, int[] selectedColor, View v) {
        int count = linearLayout.getChildCount();
        int a1 = 0;
        while (true) {
            boolean z = false;
            if (a1 < count) {
                RadioColorCell cell1 = (RadioColorCell) linearLayout.getChildAt(a1);
                if (cell1 == v) {
                    z = true;
                }
                cell1.setChecked(z, true);
                a1++;
            } else {
                selectedColor[0] = TextColorCell.colorsToSave[((Integer) v.getTag()).intValue()];
                return;
            }
        }
    }

    static /* synthetic */ void lambda$createColorSelectDialog$33(long dialog_id, int[] selectedColor, int globalType, Runnable onSelect, DialogInterface dialogInterface, int which) {
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialog_id != 0) {
            editor.putInt("color_" + dialog_id, selectedColor[0]);
        } else if (globalType == 1) {
            editor.putInt("MessagesLed", selectedColor[0]);
        } else if (globalType == 0) {
            editor.putInt("GroupLed", selectedColor[0]);
        } else {
            editor.putInt("ChannelLed", selectedColor[0]);
        }
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    static /* synthetic */ void lambda$createColorSelectDialog$34(long dialog_id, int globalType, Runnable onSelect, DialogInterface dialog, int which) {
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialog_id != 0) {
            editor.putInt("color_" + dialog_id, 0);
        } else if (globalType == 1) {
            editor.putInt("MessagesLed", 0);
        } else if (globalType == 0) {
            editor.putInt("GroupLed", 0);
        } else {
            editor.putInt("ChannelLed", 0);
        }
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    static /* synthetic */ void lambda$createColorSelectDialog$35(long dialog_id, Runnable onSelect, DialogInterface dialog, int which) {
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        editor.remove("color_" + dialog_id);
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, long dialog_id, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        String prefix;
        if (dialog_id != 0) {
            prefix = "vibrate_";
        } else {
            prefix = globalGroup ? "vibrate_group" : "vibrate_messages";
        }
        return createVibrationSelectDialog(parentActivity, dialog_id, prefix, onSelect);
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, long dialog_id, String prefKeyPrefix, Runnable onSelect) {
        String[] descriptions;
        Activity activity = parentActivity;
        long j = dialog_id;
        String str = prefKeyPrefix;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] selected = new int[1];
        int i = 0;
        if (j != 0) {
            selected[0] = preferences.getInt(str + j, 0);
            if (selected[0] == 3) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 3;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDefault", R.string.VibrationDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled)};
        } else {
            selected[0] = preferences.getInt(str, 0);
            if (selected[0] == 0) {
                selected[0] = 1;
            } else if (selected[0] == 1) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 0;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent)};
        }
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(activity);
            cell.setPadding(AndroidUtilities.dp(4.0f), i, AndroidUtilities.dp(4.0f), i);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[i] == a);
            linearLayout.addView(cell);
            $$Lambda$AlertsCreator$oMHStW3UpS_8Ln8XV2UBR2j7frY r0 = r1;
            AlertDialog.Builder builder2 = builder;
            $$Lambda$AlertsCreator$oMHStW3UpS_8Ln8XV2UBR2j7frY r1 = new View.OnClickListener(selected, dialog_id, prefKeyPrefix, builder, onSelect) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ long f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ AlertDialog.Builder f$3;
                private final /* synthetic */ Runnable f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                    this.f$4 = r6;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createVibrationSelectDialog$36(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, view);
                }
            };
            cell.setOnClickListener(r0);
            a++;
            i = 0;
            activity = parentActivity;
        }
        AlertDialog.Builder builder3 = builder;
        builder3.setTitle(LocaleController.getString("Vibrate", R.string.Vibrate));
        builder3.setView(linearLayout);
        builder3.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        return builder3.create();
    }

    static /* synthetic */ void lambda$createVibrationSelectDialog$36(int[] selected, long dialog_id, String prefKeyPrefix, AlertDialog.Builder builder, Runnable onSelect, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialog_id != 0) {
            if (selected[0] == 0) {
                editor.putInt(prefKeyPrefix + dialog_id, 0);
            } else if (selected[0] == 1) {
                editor.putInt(prefKeyPrefix + dialog_id, 1);
            } else if (selected[0] == 2) {
                editor.putInt(prefKeyPrefix + dialog_id, 3);
            } else if (selected[0] == 3) {
                editor.putInt(prefKeyPrefix + dialog_id, 2);
            }
        } else if (selected[0] == 0) {
            editor.putInt(prefKeyPrefix, 2);
        } else if (selected[0] == 1) {
            editor.putInt(prefKeyPrefix, 0);
        } else if (selected[0] == 2) {
            editor.putInt(prefKeyPrefix, 1);
        } else if (selected[0] == 3) {
            editor.putInt(prefKeyPrefix, 3);
        } else if (selected[0] == 4) {
            editor.putInt(prefKeyPrefix, 4);
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createLocationUpdateDialog(Activity parentActivity, TLRPC.User user, MessagesStorage.IntCallback callback) {
        Activity activity = parentActivity;
        int[] selected = new int[1];
        int i = 3;
        String[] descriptions = {LocaleController.getString("SendLiveLocationFor15m", R.string.SendLiveLocationFor15m), LocaleController.getString("SendLiveLocationFor1h", R.string.SendLiveLocationFor1h), LocaleController.getString("SendLiveLocationFor8h", R.string.SendLiveLocationFor8h)};
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(activity);
        if (user != null) {
            titleTextView.setText(LocaleController.formatString("LiveLocationAlertPrivate", R.string.LiveLocationAlertPrivate, UserObject.getFirstName(user)));
        } else {
            titleTextView.setText(LocaleController.getString("LiveLocationAlertGroup", R.string.LiveLocationAlertGroup));
        }
        titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(activity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener(selected, linearLayout) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ LinearLayout f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createLocationUpdateDialog$37(this.f$0, this.f$1, view);
                }
            });
            a++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        builder.setTopImage((Drawable) new ShareLocationDrawable(activity, 0), Theme.getColor(Theme.key_dialogTopBackground));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", R.string.ShareFile), new DialogInterface.OnClickListener(selected, callback) {
            private final /* synthetic */ int[] f$0;
            private final /* synthetic */ MessagesStorage.IntCallback f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createLocationUpdateDialog$38(this.f$0, this.f$1, dialogInterface, i);
            }
        });
        builder.setNeutralButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$37(int[] selected, LinearLayout linearLayout, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        int count = linearLayout.getChildCount();
        for (int a1 = 0; a1 < count; a1++) {
            View child = linearLayout.getChildAt(a1);
            if (child instanceof RadioColorCell) {
                ((RadioColorCell) child).setChecked(child == v, true);
            }
        }
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$38(int[] selected, MessagesStorage.IntCallback callback, DialogInterface dialog, int which) {
        int time;
        if (selected[0] == 0) {
            time = 900;
        } else if (selected[0] == 1) {
            time = 3600;
        } else {
            time = 28800;
        }
        callback.run(time);
    }

    public static AlertDialog.Builder createContactsPermissionDialog(Activity parentActivity, MessagesStorage.IntCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
        builder.setTopImage((int) R.drawable.permissions_contacts, Theme.getColor(Theme.key_dialogTopBackground));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", R.string.ContactsPermissionAlert)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", R.string.ContactsPermissionAlertContinue), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                MessagesStorage.IntCallback.this.run(1);
            }
        });
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                MessagesStorage.IntCallback.this.run(0);
            }
        });
        return builder;
    }

    public static Dialog createFreeSpaceDialog(LaunchActivity parentActivity) {
        LaunchActivity launchActivity = parentActivity;
        int[] selected = new int[1];
        int i = 3;
        if (SharedConfig.keepMedia == 2) {
            selected[0] = 3;
        } else if (SharedConfig.keepMedia == 0) {
            selected[0] = 1;
        } else if (SharedConfig.keepMedia == 1) {
            selected[0] = 2;
        } else if (SharedConfig.keepMedia == 3) {
            selected[0] = 0;
        }
        String[] descriptions = {LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("LowDiskSpaceNeverRemove", R.string.LowDiskSpaceNeverRemove)};
        LinearLayout linearLayout = new LinearLayout(launchActivity);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(launchActivity);
        titleTextView.setText(LocaleController.getString("LowDiskSpaceTitle2", R.string.LowDiskSpaceTitle2));
        titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(launchActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener(selected, linearLayout) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ LinearLayout f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createFreeSpaceDialog$41(this.f$0, this.f$1, view);
                }
            });
            a++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) launchActivity);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", R.string.LowDiskSpaceTitle));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", R.string.LowDiskSpaceMessage));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(selected) {
            private final /* synthetic */ int[] f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                SharedConfig.setKeepMedia(this.f$0[0]);
            }
        });
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                LaunchActivity.this.lambda$runLinkRequest$28$LaunchActivity(new CacheControlActivity());
            }
        });
        return builder.create();
    }

    static /* synthetic */ void lambda$createFreeSpaceDialog$41(int[] selected, LinearLayout linearLayout, View v) {
        int num = ((Integer) v.getTag()).intValue();
        if (num == 0) {
            selected[0] = 3;
        } else if (num == 1) {
            selected[0] = 0;
        } else if (num == 2) {
            selected[0] = 1;
        } else if (num == 3) {
            selected[0] = 2;
        }
        int count = linearLayout.getChildCount();
        for (int a1 = 0; a1 < count; a1++) {
            View child = linearLayout.getChildAt(a1);
            if (child instanceof RadioColorCell) {
                ((RadioColorCell) child).setChecked(child == v, true);
            }
        }
    }

    public static Dialog createPrioritySelectDialog(Activity parentActivity, long dialog_id, int globalType, Runnable onSelect) {
        String[] descriptions;
        Activity activity = parentActivity;
        long j = dialog_id;
        int i = globalType;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] selected = new int[1];
        int i2 = 0;
        if (j != 0) {
            selected[0] = preferences.getInt("priority_" + j, 3);
            if (selected[0] == 3) {
                selected[0] = 0;
            } else if (selected[0] == 4) {
                selected[0] = 1;
            } else if (selected[0] == 5) {
                selected[0] = 2;
            } else if (selected[0] == 0) {
                selected[0] = 3;
            } else {
                selected[0] = 4;
            }
            descriptions = new String[]{LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent)};
        } else {
            if (j == 0) {
                if (i == 1) {
                    selected[0] = preferences.getInt("priority_messages", 1);
                } else if (i == 0) {
                    selected[0] = preferences.getInt("priority_group", 1);
                } else if (i == 2) {
                    selected[0] = preferences.getInt("priority_channel", 1);
                }
            }
            if (selected[0] == 4) {
                selected[0] = 0;
            } else if (selected[0] == 5) {
                selected[0] = 1;
            } else if (selected[0] == 0) {
                selected[0] = 2;
            } else {
                selected[0] = 3;
            }
            descriptions = new String[]{LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent)};
        }
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(activity);
            cell.setPadding(AndroidUtilities.dp(4.0f), i2, AndroidUtilities.dp(4.0f), i2);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[i2] == a);
            linearLayout.addView(cell);
            $$Lambda$AlertsCreator$jXAsLOeW_hizvm7AO00qDTNV7uk r13 = r1;
            AlertDialog.Builder builder2 = builder;
            $$Lambda$AlertsCreator$jXAsLOeW_hizvm7AO00qDTNV7uk r1 = new View.OnClickListener(selected, dialog_id, globalType, preferences, builder, onSelect) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ long f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ SharedPreferences f$3;
                private final /* synthetic */ AlertDialog.Builder f$4;
                private final /* synthetic */ Runnable f$5;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createPrioritySelectDialog$44(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
                }
            };
            cell.setOnClickListener(r13);
            a++;
            i2 = 0;
            activity = parentActivity;
            linearLayout = linearLayout;
            long j2 = dialog_id;
        }
        AlertDialog.Builder builder3 = builder;
        builder3.setTitle(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance));
        builder3.setView(linearLayout);
        builder3.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        return builder3.create();
    }

    static /* synthetic */ void lambda$createPrioritySelectDialog$44(int[] selected, long dialog_id, int globalType, SharedPreferences preferences, AlertDialog.Builder builder, Runnable onSelect, View v) {
        int option;
        int option2;
        selected[0] = ((Integer) v.getTag()).intValue();
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialog_id != 0) {
            if (selected[0] == 0) {
                option2 = 3;
            } else if (selected[0] == 1) {
                option2 = 4;
            } else if (selected[0] == 2) {
                option2 = 5;
            } else if (selected[0] == 3) {
                option2 = 0;
            } else {
                option2 = 1;
            }
            editor.putInt("priority_" + dialog_id, option2);
        } else {
            if (selected[0] == 0) {
                option = 4;
            } else if (selected[0] == 1) {
                option = 5;
            } else if (selected[0] == 2) {
                option = 0;
            } else {
                option = 1;
            }
            if (globalType == 1) {
                editor.putInt("priority_messages", option);
                selected[0] = preferences.getInt("priority_messages", 1);
            } else if (globalType == 0) {
                editor.putInt("priority_group", option);
                selected[0] = preferences.getInt("priority_group", 1);
            } else if (globalType == 2) {
                editor.putInt("priority_channel", option);
                selected[0] = preferences.getInt("priority_channel", 1);
            }
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createPopupSelectDialog(Activity parentActivity, int globalType, Runnable onSelect) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] selected = new int[1];
        if (globalType == 1) {
            selected[0] = preferences.getInt("popupAll", 0);
        } else if (globalType == 0) {
            selected[0] = preferences.getInt("popupGroup", 0);
        } else {
            selected[0] = preferences.getInt("popupChannel", 0);
        }
        String[] descriptions = {LocaleController.getString("NoPopup", R.string.NoPopup), LocaleController.getString("OnlyWhenScreenOn", R.string.OnlyWhenScreenOn), LocaleController.getString("OnlyWhenScreenOff", R.string.OnlyWhenScreenOff), LocaleController.getString("AlwaysShowPopup", R.string.AlwaysShowPopup)};
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setTag(Integer.valueOf(a));
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener(selected, globalType, builder, onSelect) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ int f$1;
                private final /* synthetic */ AlertDialog.Builder f$2;
                private final /* synthetic */ Runnable f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createPopupSelectDialog$45(this.f$0, this.f$1, this.f$2, this.f$3, view);
                }
            });
            a++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", R.string.PopupNotification));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createPopupSelectDialog$45(int[] selected, int globalType, AlertDialog.Builder builder, Runnable onSelect, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (globalType == 1) {
            editor.putInt("popupAll", selected[0]);
        } else if (globalType == 0) {
            editor.putInt("popupGroup", selected[0]);
        } else {
            editor.putInt("popupChannel", selected[0]);
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createSingleChoiceDialog(Activity parentActivity, String[] options, String title, int selected, DialogInterface.OnClickListener listener) {
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
        for (int a = 0; a < options.length; a++) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            boolean z = false;
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            String str = options[a];
            if (selected == a) {
                z = true;
            }
            cell.setTextAndValue(str, z);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener(listener) {
                private final /* synthetic */ DialogInterface.OnClickListener f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createSingleChoiceDialog$46(AlertDialog.Builder.this, this.f$1, view);
                }
            });
        }
        builder.setTitle(title);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createSingleChoiceDialog$46(AlertDialog.Builder builder, DialogInterface.OnClickListener listener, View v) {
        int sel = ((Integer) v.getTag()).intValue();
        builder.getDismissRunnable().run();
        listener.onClick((DialogInterface) null, sel);
    }

    public static AlertDialog.Builder createTTLAlert(Context context, TLRPC.EncryptedChat encryptedChat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
        NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        if (encryptedChat.ttl > 0 && encryptedChat.ttl < 16) {
            numberPicker.setValue(encryptedChat.ttl);
        } else if (encryptedChat.ttl == 30) {
            numberPicker.setValue(16);
        } else if (encryptedChat.ttl == 60) {
            numberPicker.setValue(17);
        } else if (encryptedChat.ttl == 3600) {
            numberPicker.setValue(18);
        } else if (encryptedChat.ttl == 86400) {
            numberPicker.setValue(19);
        } else if (encryptedChat.ttl == 604800) {
            numberPicker.setValue(20);
        } else if (encryptedChat.ttl == 0) {
            numberPicker.setValue(0);
        }
        numberPicker.setFormatter($$Lambda$AlertsCreator$356S2YEfn9QMLEfVZMLam4vZAM.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener(numberPicker) {
            private final /* synthetic */ NumberPicker f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createTTLAlert$48(TLRPC.EncryptedChat.this, this.f$1, dialogInterface, i);
            }
        });
        return builder;
    }

    static /* synthetic */ String lambda$createTTLAlert$47(int value) {
        if (value == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
        }
        if (value >= 1 && value < 16) {
            return LocaleController.formatTTLString(value);
        }
        if (value == 16) {
            return LocaleController.formatTTLString(30);
        }
        if (value == 17) {
            return LocaleController.formatTTLString(60);
        }
        if (value == 18) {
            return LocaleController.formatTTLString(3600);
        }
        if (value == 19) {
            return LocaleController.formatTTLString(86400);
        }
        if (value == 20) {
            return LocaleController.formatTTLString(604800);
        }
        return "";
    }

    static /* synthetic */ void lambda$createTTLAlert$48(TLRPC.EncryptedChat encryptedChat, NumberPicker numberPicker, DialogInterface dialog, int which) {
        int oldValue = encryptedChat.ttl;
        int which2 = numberPicker.getValue();
        if (which2 >= 0 && which2 < 16) {
            encryptedChat.ttl = which2;
        } else if (which2 == 16) {
            encryptedChat.ttl = 30;
        } else if (which2 == 17) {
            encryptedChat.ttl = 60;
        } else if (which2 == 18) {
            encryptedChat.ttl = 3600;
        } else if (which2 == 19) {
            encryptedChat.ttl = 86400;
        } else if (which2 == 20) {
            encryptedChat.ttl = 604800;
        }
        if (oldValue != encryptedChat.ttl) {
            SecretChatHelper.getInstance(UserConfig.selectedAccount).sendTTLMessage(encryptedChat, (TLRPC.Message) null);
            MessagesStorage.getInstance(UserConfig.selectedAccount).updateEncryptedChatTTL(encryptedChat);
        }
    }

    public static AlertDialog createAccountSelectDialog(Activity parentActivity, AccountSelectDelegate delegate) {
        if (UserConfig.getActivatedAccountsCount() < 2) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
        Runnable dismissRunnable = builder.getDismissRunnable();
        AlertDialog[] alertDialog = new AlertDialog[1];
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).getCurrentUser() != null) {
                AccountSelectCell cell = new AccountSelectCell(parentActivity);
                cell.setAccount(a, false);
                cell.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
                cell.setOnClickListener(new View.OnClickListener(alertDialog, dismissRunnable, delegate) {
                    private final /* synthetic */ AlertDialog[] f$0;
                    private final /* synthetic */ Runnable f$1;
                    private final /* synthetic */ AlertsCreator.AccountSelectDelegate f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(View view) {
                        AlertsCreator.lambda$createAccountSelectDialog$49(this.f$0, this.f$1, this.f$2, view);
                    }
                });
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", R.string.SelectAccount));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        alertDialog[0] = create;
        return create;
    }

    static /* synthetic */ void lambda$createAccountSelectDialog$49(AlertDialog[] alertDialog, Runnable dismissRunnable, AccountSelectDelegate delegate, View v) {
        if (alertDialog[0] != null) {
            alertDialog[0].setOnDismissListener((DialogInterface.OnDismissListener) null);
        }
        dismissRunnable.run();
        delegate.didSelectAccount(((AccountSelectCell) v).getAccountNumber());
    }

    /* JADX WARNING: Removed duplicated region for block: B:203:0x044f  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x05bd  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x05ca  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x05e9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0621 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0666  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0683  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createDeleteMessagesAlert(im.bclpbkiauv.ui.actionbar.BaseFragment r49, im.bclpbkiauv.tgnet.TLRPC.User r50, im.bclpbkiauv.tgnet.TLRPC.Chat r51, im.bclpbkiauv.tgnet.TLRPC.EncryptedChat r52, im.bclpbkiauv.tgnet.TLRPC.ChatFull r53, long r54, im.bclpbkiauv.messenger.MessageObject r56, android.util.SparseArray<im.bclpbkiauv.messenger.MessageObject>[] r57, im.bclpbkiauv.messenger.MessageObject.GroupedMessages r58, boolean r59, int r60, java.lang.Runnable r61) {
        /*
            r14 = r49
            r15 = r50
            r13 = r51
            r12 = r52
            r11 = r56
            r10 = r58
            r9 = r60
            if (r14 == 0) goto L_0x06bf
            if (r15 != 0) goto L_0x001a
            if (r13 != 0) goto L_0x001a
            if (r12 != 0) goto L_0x001a
            r4 = r14
            r14 = r13
            goto L_0x06c1
        L_0x001a:
            androidx.fragment.app.FragmentActivity r7 = r49.getParentActivity()
            if (r7 != 0) goto L_0x0021
            return
        L_0x0021:
            int r8 = r49.getCurrentAccount()
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r0 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder
            r0.<init>((android.content.Context) r7)
            r6 = r0
            r0 = 1
            r1 = 0
            if (r10 == 0) goto L_0x0037
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r2 = r10.messages
            int r2 = r2.size()
            r5 = r2
            goto L_0x004a
        L_0x0037:
            if (r11 == 0) goto L_0x003c
            r2 = 1
            r5 = r2
            goto L_0x004a
        L_0x003c:
            r2 = r57[r1]
            int r2 = r2.size()
            r3 = r57[r0]
            int r3 = r3.size()
            int r2 = r2 + r3
            r5 = r2
        L_0x004a:
            if (r12 == 0) goto L_0x0055
            int r2 = r12.id
            long r2 = (long) r2
            r4 = 32
            long r2 = r2 << r4
            r31 = r2
            goto L_0x0063
        L_0x0055:
            if (r15 == 0) goto L_0x005d
            int r2 = r15.id
            long r2 = (long) r2
            r31 = r2
            goto L_0x0063
        L_0x005d:
            int r2 = r13.id
            int r2 = -r2
            long r2 = (long) r2
            r31 = r2
        L_0x0063:
            r2 = 3
            boolean[] r4 = new boolean[r2]
            boolean[] r3 = new boolean[r0]
            r16 = 0
            if (r15 == 0) goto L_0x0076
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r8)
            boolean r1 = r1.canRevokePmInbox
            if (r1 == 0) goto L_0x0076
            r1 = 1
            goto L_0x0077
        L_0x0076:
            r1 = 0
        L_0x0077:
            r33 = r1
            if (r15 == 0) goto L_0x0082
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r8)
            int r1 = r1.revokeTimePmLimit
            goto L_0x0088
        L_0x0082:
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r8)
            int r1 = r1.revokeTimeLimit
        L_0x0088:
            r17 = 0
            r18 = 0
            r19 = 0
            if (r12 != 0) goto L_0x009b
            if (r15 == 0) goto L_0x009b
            if (r33 == 0) goto L_0x009b
            r2 = 2147483647(0x7fffffff, float:NaN)
            if (r1 != r2) goto L_0x009b
            r2 = 1
            goto L_0x009c
        L_0x009b:
            r2 = 0
        L_0x009c:
            r34 = r2
            java.lang.String r2 = "DeleteForAll"
            java.lang.String r0 = "DeleteMessagesOption"
            r24 = r3
            r25 = 1098907648(0x41800000, float:16.0)
            r26 = 1090519040(0x41000000, float:8.0)
            java.lang.String r3 = ""
            if (r13 == 0) goto L_0x03d7
            r27 = r4
            boolean r4 = r13.megagroup
            if (r4 == 0) goto L_0x03c9
            if (r59 != 0) goto L_0x03c9
            boolean r28 = im.bclpbkiauv.messenger.ChatObject.canBlockUsers(r51)
            im.bclpbkiauv.tgnet.ConnectionsManager r4 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r8)
            int r29 = r4.getCurrentTime()
            if (r11 == 0) goto L_0x0135
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            if (r4 == 0) goto L_0x00ec
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionEmpty
            if (r4 != 0) goto L_0x00ec
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r4 != 0) goto L_0x00ec
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r4 != 0) goto L_0x00ec
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r4 == 0) goto L_0x00e9
            goto L_0x00ec
        L_0x00e9:
            r30 = r5
            goto L_0x00fe
        L_0x00ec:
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r8)
            r30 = r5
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r11.messageOwner
            int r5 = r5.from_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            im.bclpbkiauv.tgnet.TLRPC$User r16 = r4.getUser(r5)
        L_0x00fe:
            boolean r4 = r56.isSendError()
            if (r4 != 0) goto L_0x012a
            long r4 = r56.getDialogId()
            int r35 = (r4 > r54 ? 1 : (r4 == r54 ? 0 : -1))
            if (r35 != 0) goto L_0x012a
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            if (r4 == 0) goto L_0x011a
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionEmpty
            if (r4 == 0) goto L_0x012a
        L_0x011a:
            boolean r4 = r56.isOut()
            if (r4 == 0) goto L_0x012a
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r11.messageOwner
            int r4 = r4.date
            int r4 = r29 - r4
            if (r4 > r1) goto L_0x012a
            r4 = 1
            goto L_0x012b
        L_0x012a:
            r4 = 0
        L_0x012b:
            if (r4 == 0) goto L_0x012f
            int r19 = r19 + 1
        L_0x012f:
            r36 = r6
            r11 = r16
            goto L_0x01c3
        L_0x0135:
            r30 = r5
            r4 = -1
            r5 = 1
        L_0x0139:
            if (r5 < 0) goto L_0x017a
            r35 = 0
            r36 = 0
            r48 = r36
            r36 = r6
            r6 = r48
        L_0x0145:
            r37 = r57[r5]
            int r10 = r37.size()
            if (r6 >= r10) goto L_0x016d
            r10 = r57[r5]
            java.lang.Object r10 = r10.valueAt(r6)
            im.bclpbkiauv.messenger.MessageObject r10 = (im.bclpbkiauv.messenger.MessageObject) r10
            r11 = -1
            if (r4 != r11) goto L_0x015c
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r10.messageOwner
            int r4 = r11.from_id
        L_0x015c:
            if (r4 < 0) goto L_0x016c
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r10.messageOwner
            int r11 = r11.from_id
            if (r4 == r11) goto L_0x0165
            goto L_0x016c
        L_0x0165:
            int r6 = r6 + 1
            r11 = r56
            r10 = r58
            goto L_0x0145
        L_0x016c:
            r4 = -2
        L_0x016d:
            r6 = -2
            if (r4 != r6) goto L_0x0171
            goto L_0x017c
        L_0x0171:
            int r5 = r5 + -1
            r11 = r56
            r10 = r58
            r6 = r36
            goto L_0x0139
        L_0x017a:
            r36 = r6
        L_0x017c:
            r5 = 1
        L_0x017d:
            if (r5 < 0) goto L_0x01af
            r6 = 0
        L_0x0180:
            r10 = r57[r5]
            int r10 = r10.size()
            if (r6 >= r10) goto L_0x01ac
            r10 = r57[r5]
            java.lang.Object r10 = r10.valueAt(r6)
            im.bclpbkiauv.messenger.MessageObject r10 = (im.bclpbkiauv.messenger.MessageObject) r10
            r11 = 1
            if (r5 != r11) goto L_0x01a9
            boolean r11 = r10.isOut()
            if (r11 == 0) goto L_0x01a9
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r10.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r11 = r11.action
            if (r11 != 0) goto L_0x01a9
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r10.messageOwner
            int r11 = r11.date
            int r11 = r29 - r11
            if (r11 > r1) goto L_0x01a9
            int r19 = r19 + 1
        L_0x01a9:
            int r6 = r6 + 1
            goto L_0x0180
        L_0x01ac:
            int r5 = r5 + -1
            goto L_0x017d
        L_0x01af:
            r5 = -1
            if (r4 == r5) goto L_0x01c1
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r8)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r4)
            im.bclpbkiauv.tgnet.TLRPC$User r16 = r5.getUser(r6)
            r11 = r16
            goto L_0x01c3
        L_0x01c1:
            r11 = r16
        L_0x01c3:
            if (r11 == 0) goto L_0x0328
            int r4 = r11.id
            im.bclpbkiauv.messenger.UserConfig r5 = im.bclpbkiauv.messenger.UserConfig.getInstance(r8)
            int r5 = r5.getClientUserId()
            if (r4 == r5) goto L_0x0328
            r0 = 1
            if (r9 != r0) goto L_0x024a
            boolean r2 = r13.creator
            if (r2 != 0) goto L_0x024a
            im.bclpbkiauv.ui.actionbar.AlertDialog[] r0 = new im.bclpbkiauv.ui.actionbar.AlertDialog[r0]
            im.bclpbkiauv.ui.actionbar.AlertDialog r2 = new im.bclpbkiauv.ui.actionbar.AlertDialog
            r3 = 3
            r2.<init>(r7, r3)
            r3 = 0
            r0[r3] = r2
            r10 = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_channels_getParticipant r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_channels_getParticipant
            r0.<init>()
            r6 = r0
            im.bclpbkiauv.tgnet.TLRPC$InputChannel r0 = im.bclpbkiauv.messenger.MessagesController.getInputChannel((im.bclpbkiauv.tgnet.TLRPC.Chat) r51)
            r6.channel = r0
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r8)
            im.bclpbkiauv.tgnet.TLRPC$InputUser r0 = r0.getInputUser((im.bclpbkiauv.tgnet.TLRPC.User) r11)
            r6.user_id = r0
            im.bclpbkiauv.tgnet.ConnectionsManager r5 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r8)
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$NHNX4_eUddE3X-5qZ47wrEOL2Yc r4 = new im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$NHNX4_eUddE3X-5qZ47wrEOL2Yc
            r0 = r4
            r3 = r1
            r1 = r10
            r2 = r49
            r38 = r3
            r15 = r24
            r3 = r50
            r35 = r27
            r15 = r4
            r4 = r51
            r14 = r5
            r39 = r30
            r5 = r52
            r16 = r14
            r40 = r36
            r14 = r6
            r6 = r53
            r41 = r7
            r42 = r8
            r7 = r54
            r9 = r56
            r43 = r10
            r10 = r57
            r44 = r11
            r11 = r58
            r12 = r59
            r13 = r61
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13)
            r0 = r16
            int r0 = r0.sendRequest(r14, r15)
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$ND3tMoHmlRJEQ0PB7S4FlWWQS_s r1 = new im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$ND3tMoHmlRJEQ0PB7S4FlWWQS_s
            r4 = r49
            r5 = r42
            r2 = r43
            r1.<init>(r2, r5, r0, r4)
            r6 = 1000(0x3e8, double:4.94E-321)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r1, r6)
            return
        L_0x024a:
            r38 = r1
            r41 = r7
            r5 = r8
            r44 = r11
            r4 = r14
            r35 = r27
            r39 = r30
            r40 = r36
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r1 = r41
            r0.<init>(r1)
            r2 = 0
            r6 = 0
        L_0x0261:
            r7 = 3
            if (r6 >= r7) goto L_0x0315
            r8 = r60
            r9 = 2
            if (r8 == r9) goto L_0x026b
            if (r28 != 0) goto L_0x0273
        L_0x026b:
            if (r6 != 0) goto L_0x0273
            r12 = r35
            r11 = r44
            goto L_0x030d
        L_0x0273:
            im.bclpbkiauv.ui.cells.CheckBoxCell r9 = new im.bclpbkiauv.ui.cells.CheckBoxCell
            r10 = 1
            r9.<init>(r1, r10)
            r10 = 0
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r10)
            r9.setBackgroundDrawable(r11)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r6)
            r9.setTag(r11)
            if (r6 != 0) goto L_0x0299
            r11 = 2131690843(0x7f0f055b, float:1.9010741E38)
            java.lang.String r12 = "DeleteBanUser"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r9.setText(r11, r3, r10, r10)
            r11 = r44
            goto L_0x02cb
        L_0x0299:
            r11 = 1
            if (r6 != r11) goto L_0x02ab
            r12 = 2131690867(0x7f0f0573, float:1.901079E38)
            java.lang.String r13 = "DeleteReportSpam"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r12)
            r9.setText(r12, r3, r10, r10)
            r11 = r44
            goto L_0x02cb
        L_0x02ab:
            r12 = 2
            if (r6 != r12) goto L_0x02c9
            r12 = 2131690838(0x7f0f0556, float:1.901073E38)
            java.lang.Object[] r13 = new java.lang.Object[r11]
            r11 = r44
            java.lang.String r14 = r11.first_name
            java.lang.String r15 = r11.last_name
            java.lang.String r14 = im.bclpbkiauv.messenger.ContactsController.formatName(r14, r15)
            r13[r10] = r14
            java.lang.String r14 = "DeleteAllFrom"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.formatString(r14, r12, r13)
            r9.setText(r12, r3, r10, r10)
            goto L_0x02cb
        L_0x02c9:
            r11 = r44
        L_0x02cb:
            boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x02d4
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            goto L_0x02d8
        L_0x02d4:
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
        L_0x02d8:
            boolean r12 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x02e1
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
            goto L_0x02e5
        L_0x02e1:
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
        L_0x02e5:
            r13 = 0
            r9.setPadding(r10, r13, r12, r13)
            r41 = -1082130432(0xffffffffbf800000, float:-1.0)
            r42 = 1111490560(0x42400000, float:48.0)
            r43 = 51
            r44 = 0
            int r10 = r2 * 48
            float r10 = (float) r10
            r46 = 0
            r47 = 0
            r45 = r10
            android.widget.FrameLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r41, r42, r43, r44, r45, r46, r47)
            r0.addView(r9, r10)
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$v3jLbb1a-RT6agvljeNSDNBLQWU r10 = new im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$v3jLbb1a-RT6agvljeNSDNBLQWU
            r12 = r35
            r10.<init>(r12)
            r9.setOnClickListener(r10)
            int r2 = r2 + 1
        L_0x030d:
            int r6 = r6 + 1
            r44 = r11
            r35 = r12
            goto L_0x0261
        L_0x0315:
            r8 = r60
            r12 = r35
            r11 = r44
            r6 = r40
            r6.setView(r0)
            r14 = r51
            r16 = r11
            r13 = r24
            goto L_0x03b8
        L_0x0328:
            r38 = r1
            r1 = r7
            r5 = r8
            r8 = r9
            r4 = r14
            r12 = r27
            r39 = r30
            r6 = r36
            if (r18 != 0) goto L_0x03b1
            if (r19 <= 0) goto L_0x03b1
            r7 = 1
            android.widget.FrameLayout r9 = new android.widget.FrameLayout
            r9.<init>(r1)
            im.bclpbkiauv.ui.cells.CheckBoxCell r10 = new im.bclpbkiauv.ui.cells.CheckBoxCell
            r13 = 1
            r10.<init>(r1, r13)
            r13 = 0
            android.graphics.drawable.Drawable r14 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r13)
            r10.setBackgroundDrawable(r14)
            r14 = r51
            if (r14 == 0) goto L_0x035d
            if (r18 == 0) goto L_0x035d
            r0 = 2131690850(0x7f0f0562, float:1.9010755E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            r10.setText(r0, r3, r13, r13)
            goto L_0x0367
        L_0x035d:
            r2 = 2131690859(0x7f0f056b, float:1.9010774E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0, r2)
            r10.setText(r0, r3, r13, r13)
        L_0x0367:
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0370
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            goto L_0x0374
        L_0x0370:
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
        L_0x0374:
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x037d
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
            goto L_0x0381
        L_0x037d:
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
        L_0x0381:
            r3 = 0
            r10.setPadding(r0, r3, r2, r3)
            r40 = -1082130432(0xffffffffbf800000, float:-1.0)
            r41 = 1111490560(0x42400000, float:48.0)
            r42 = 51
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            android.widget.FrameLayout$LayoutParams r0 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r40, r41, r42, r43, r44, r45, r46)
            r9.addView(r10, r0)
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$qxGuOnLGp-CIjSXHjy7HQ3F9S8E r0 = new im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$qxGuOnLGp-CIjSXHjy7HQ3F9S8E
            r13 = r24
            r0.<init>(r13)
            r10.setOnClickListener(r0)
            r6.setView(r9)
            r0 = 9
            r6.setCustomViewOffset(r0)
            r17 = r7
            r16 = r11
            goto L_0x03b8
        L_0x03b1:
            r14 = r51
            r13 = r24
            r0 = 0
            r16 = r0
        L_0x03b8:
            r41 = r1
            r10 = r13
            r0 = r16
            r2 = r17
            r3 = r18
            r8 = r19
            r15 = r38
            r1 = r39
            goto L_0x058b
        L_0x03c9:
            r38 = r1
            r39 = r5
            r1 = r7
            r5 = r8
            r8 = r9
            r4 = r14
            r12 = r27
            r14 = r13
            r13 = r24
            goto L_0x03e3
        L_0x03d7:
            r38 = r1
            r12 = r4
            r39 = r5
            r1 = r7
            r5 = r8
            r8 = r9
            r4 = r14
            r14 = r13
            r13 = r24
        L_0x03e3:
            if (r59 != 0) goto L_0x057c
            boolean r7 = im.bclpbkiauv.messenger.ChatObject.isChannel(r51)
            if (r7 != 0) goto L_0x057c
            if (r52 != 0) goto L_0x057c
            im.bclpbkiauv.tgnet.ConnectionsManager r7 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r5)
            int r7 = r7.getCurrentTime()
            r9 = r50
            r10 = r13
            if (r9 == 0) goto L_0x040a
            int r11 = r9.id
            im.bclpbkiauv.messenger.UserConfig r13 = im.bclpbkiauv.messenger.UserConfig.getInstance(r5)
            int r13 = r13.getClientUserId()
            if (r11 == r13) goto L_0x040a
            boolean r11 = r9.bot
            if (r11 == 0) goto L_0x040c
        L_0x040a:
            if (r14 == 0) goto L_0x04c6
        L_0x040c:
            r11 = r56
            if (r11 == 0) goto L_0x045f
            boolean r13 = r56.isSendError()
            if (r13 != 0) goto L_0x044a
            im.bclpbkiauv.tgnet.TLRPC$Message r13 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r13 = r13.action
            if (r13 == 0) goto L_0x0430
            im.bclpbkiauv.tgnet.TLRPC$Message r13 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r13 = r13.action
            boolean r13 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionEmpty
            if (r13 != 0) goto L_0x0430
            im.bclpbkiauv.tgnet.TLRPC$Message r13 = r11.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r13 = r13.action
            boolean r13 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r13 == 0) goto L_0x042d
            goto L_0x0430
        L_0x042d:
            r15 = r38
            goto L_0x044c
        L_0x0430:
            boolean r13 = r56.isOut()
            if (r13 != 0) goto L_0x043e
            if (r33 != 0) goto L_0x043e
            boolean r13 = im.bclpbkiauv.messenger.ChatObject.hasAdminRights(r51)
            if (r13 == 0) goto L_0x042d
        L_0x043e:
            im.bclpbkiauv.tgnet.TLRPC$Message r13 = r11.messageOwner
            int r13 = r13.date
            int r13 = r7 - r13
            r15 = r38
            if (r13 > r15) goto L_0x044c
            r13 = 1
            goto L_0x044d
        L_0x044a:
            r15 = r38
        L_0x044c:
            r13 = 0
        L_0x044d:
            if (r13 == 0) goto L_0x0451
            int r19 = r19 + 1
        L_0x0451:
            boolean r20 = r56.isOut()
            r24 = 1
            r20 = r20 ^ 1
            r18 = r20
            r8 = r19
            goto L_0x04ca
        L_0x045f:
            r15 = r38
            r13 = 1
        L_0x0462:
            if (r13 < 0) goto L_0x04c3
            r20 = 0
            r8 = r20
        L_0x0468:
            r20 = r57[r13]
            int r9 = r20.size()
            if (r8 >= r9) goto L_0x04ba
            r9 = r57[r13]
            java.lang.Object r9 = r9.valueAt(r8)
            im.bclpbkiauv.messenger.MessageObject r9 = (im.bclpbkiauv.messenger.MessageObject) r9
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r9.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r11 = r11.action
            if (r11 == 0) goto L_0x048f
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r9.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r11 = r11.action
            boolean r11 = r11 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionEmpty
            if (r11 != 0) goto L_0x048f
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r9.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r11 = r11.action
            boolean r11 = r11 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r11 != 0) goto L_0x048f
            goto L_0x04b3
        L_0x048f:
            boolean r11 = r9.isOut()
            if (r11 != 0) goto L_0x049f
            if (r33 != 0) goto L_0x049f
            if (r14 == 0) goto L_0x04b3
            boolean r11 = im.bclpbkiauv.messenger.ChatObject.canBlockUsers(r51)
            if (r11 == 0) goto L_0x04b3
        L_0x049f:
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r9.messageOwner
            int r11 = r11.date
            int r11 = r7 - r11
            if (r11 > r15) goto L_0x04b3
            int r19 = r19 + 1
            if (r18 != 0) goto L_0x04b3
            boolean r11 = r9.isOut()
            if (r11 != 0) goto L_0x04b3
            r18 = 1
        L_0x04b3:
            int r8 = r8 + 1
            r9 = r50
            r11 = r56
            goto L_0x0468
        L_0x04ba:
            int r13 = r13 + -1
            r9 = r50
            r11 = r56
            r8 = r60
            goto L_0x0462
        L_0x04c3:
            r8 = r19
            goto L_0x04ca
        L_0x04c6:
            r15 = r38
            r8 = r19
        L_0x04ca:
            if (r8 <= 0) goto L_0x0571
            r17 = 1
            android.widget.FrameLayout r9 = new android.widget.FrameLayout
            r9.<init>(r1)
            im.bclpbkiauv.ui.cells.CheckBoxCell r11 = new im.bclpbkiauv.ui.cells.CheckBoxCell
            r13 = 1
            r11.<init>(r1, r13)
            r19 = 0
            android.graphics.drawable.Drawable r13 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r19)
            r11.setBackgroundDrawable(r13)
            if (r34 == 0) goto L_0x04ff
            r2 = 1
            java.lang.Object[] r13 = new java.lang.Object[r2]
            java.lang.String r2 = im.bclpbkiauv.messenger.UserObject.getFirstName(r50)
            r0 = 0
            r13[r0] = r2
            java.lang.String r2 = "DeleteMessagesOptionAlso"
            r41 = r1
            r1 = 2131690860(0x7f0f056c, float:1.9010776E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r1, r13)
            r11.setText(r1, r3, r0, r0)
            r1 = r39
            goto L_0x0527
        L_0x04ff:
            r41 = r1
            if (r14 == 0) goto L_0x051a
            if (r18 != 0) goto L_0x050c
            r1 = r39
            if (r8 != r1) goto L_0x050a
            goto L_0x050e
        L_0x050a:
            r2 = 0
            goto L_0x051d
        L_0x050c:
            r1 = r39
        L_0x050e:
            r0 = 2131690850(0x7f0f0562, float:1.9010755E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            r2 = 0
            r11.setText(r0, r3, r2, r2)
            goto L_0x0527
        L_0x051a:
            r1 = r39
            r2 = 0
        L_0x051d:
            r13 = 2131690859(0x7f0f056b, float:1.9010774E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0, r13)
            r11.setText(r0, r3, r2, r2)
        L_0x0527:
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0530
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            goto L_0x0534
        L_0x0530:
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
        L_0x0534:
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x053d
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
            goto L_0x0541
        L_0x053d:
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
        L_0x0541:
            r3 = 0
            r11.setPadding(r0, r3, r2, r3)
            r22 = -1082130432(0xffffffffbf800000, float:-1.0)
            r23 = 1111490560(0x42400000, float:48.0)
            r24 = 51
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            android.widget.FrameLayout$LayoutParams r0 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r9.addView(r11, r0)
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$BtEUB4STBnjkuwonYHaesp1f03g r0 = new im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$BtEUB4STBnjkuwonYHaesp1f03g
            r0.<init>(r10)
            r11.setOnClickListener(r0)
            r6.setView(r9)
            r0 = 9
            r6.setCustomViewOffset(r0)
            r0 = r16
            r2 = r17
            r3 = r18
            goto L_0x058b
        L_0x0571:
            r41 = r1
            r1 = r39
            r0 = r16
            r2 = r17
            r3 = r18
            goto L_0x058b
        L_0x057c:
            r41 = r1
            r10 = r13
            r15 = r38
            r1 = r39
            r0 = r16
            r2 = r17
            r3 = r18
            r8 = r19
        L_0x058b:
            r26 = r0
            r7 = 2131690831(0x7f0f054f, float:1.9010717E38)
            java.lang.String r9 = "Delete"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$MMGijZzsMWA7mghdzGUayfsxwss r9 = new im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$MMGijZzsMWA7mghdzGUayfsxwss
            r16 = r9
            r17 = r56
            r18 = r58
            r19 = r52
            r20 = r5
            r21 = r31
            r23 = r10
            r24 = r59
            r25 = r57
            r27 = r12
            r28 = r51
            r29 = r53
            r30 = r61
            r16.<init>(r18, r19, r20, r21, r23, r24, r25, r26, r27, r28, r29, r30)
            r6.setPositiveButton(r7, r9)
            java.lang.String r7 = "messages"
            r9 = 1
            if (r1 != r9) goto L_0x05ca
            r11 = 2131690868(0x7f0f0574, float:1.9010792E38)
            java.lang.String r13 = "DeleteSingleMessagesTitle"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r11)
            r6.setTitle(r11)
            goto L_0x05e0
        L_0x05ca:
            r11 = 2131690864(0x7f0f0570, float:1.9010784E38)
            java.lang.Object[] r13 = new java.lang.Object[r9]
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r7, r1)
            r16 = 0
            r13[r16] = r9
            java.lang.String r9 = "DeleteMessagesTitle"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatString(r9, r11, r13)
            r6.setTitle(r9)
        L_0x05e0:
            r9 = 2131689905(0x7f0f01b1, float:1.9008839E38)
            java.lang.String r11 = "AreYouSureDeleteSingleMessage"
            java.lang.String r13 = "AreYouSureDeleteFewMessages"
            if (r14 == 0) goto L_0x061f
            if (r3 == 0) goto L_0x061f
            if (r2 == 0) goto L_0x0607
            if (r8 == r1) goto L_0x0607
            r9 = 2131690863(0x7f0f056f, float:1.9010782E38)
            r11 = 1
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r7, r8)
            r13 = 0
            r11[r13] = r7
            java.lang.String r7 = "DeleteMessagesTextGroupPart"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r9, r11)
            r6.setMessage(r7)
            goto L_0x0698
        L_0x0607:
            r7 = 1
            if (r1 != r7) goto L_0x0613
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r9)
            r6.setMessage(r7)
            goto L_0x0698
        L_0x0613:
            r7 = 2131689900(0x7f0f01ac, float:1.9008828E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r7)
            r6.setMessage(r7)
            goto L_0x0698
        L_0x061f:
            if (r2 == 0) goto L_0x0660
            if (r34 != 0) goto L_0x0660
            if (r8 == r1) goto L_0x0660
            if (r14 == 0) goto L_0x063e
            r9 = 2131690862(0x7f0f056e, float:1.901078E38)
            r11 = 1
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r7, r8)
            r13 = 0
            r11[r13] = r7
            java.lang.String r7 = "DeleteMessagesTextGroup"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r9, r11)
            r6.setMessage(r7)
            goto L_0x0698
        L_0x063e:
            r13 = 0
            r9 = 2131690861(0x7f0f056d, float:1.9010778E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r7, r8)
            r11[r13] = r7
            java.lang.String r7 = im.bclpbkiauv.messenger.UserObject.getFirstName(r50)
            r13 = 1
            r11[r13] = r7
            java.lang.String r7 = "DeleteMessagesText"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r9, r11)
            android.text.SpannableStringBuilder r7 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r7)
            r6.setMessage(r7)
            goto L_0x0698
        L_0x0660:
            if (r14 == 0) goto L_0x0683
            boolean r7 = r14.megagroup
            if (r7 == 0) goto L_0x0683
            r7 = 1
            if (r1 != r7) goto L_0x0676
            r7 = 2131689906(0x7f0f01b2, float:1.900884E38)
            java.lang.String r9 = "AreYouSureDeleteSingleMessageMega"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
            r6.setMessage(r7)
            goto L_0x0698
        L_0x0676:
            r7 = 2131689901(0x7f0f01ad, float:1.900883E38)
            java.lang.String r9 = "AreYouSureDeleteFewMessagesMega"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
            r6.setMessage(r7)
            goto L_0x0698
        L_0x0683:
            r7 = 1
            if (r1 != r7) goto L_0x068e
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r9)
            r6.setMessage(r7)
            goto L_0x0698
        L_0x068e:
            r7 = 2131689900(0x7f0f01ac, float:1.9008828E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r7)
            r6.setMessage(r7)
        L_0x0698:
            r7 = 2131690308(0x7f0f0344, float:1.9009656E38)
            java.lang.String r9 = "Cancel"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
            r9 = 0
            r6.setNegativeButton(r7, r9)
            im.bclpbkiauv.ui.actionbar.AlertDialog r7 = r6.create()
            r4.showDialog(r7)
            r9 = -1
            android.view.View r9 = r7.getButton(r9)
            android.widget.TextView r9 = (android.widget.TextView) r9
            if (r9 == 0) goto L_0x06be
            java.lang.String r11 = "dialogTextRed2"
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            r9.setTextColor(r11)
        L_0x06be:
            return
        L_0x06bf:
            r4 = r14
            r14 = r13
        L_0x06c1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.AlertsCreator.createDeleteMessagesAlert(im.bclpbkiauv.ui.actionbar.BaseFragment, im.bclpbkiauv.tgnet.TLRPC$User, im.bclpbkiauv.tgnet.TLRPC$Chat, im.bclpbkiauv.tgnet.TLRPC$EncryptedChat, im.bclpbkiauv.tgnet.TLRPC$ChatFull, long, im.bclpbkiauv.messenger.MessageObject, android.util.SparseArray[], im.bclpbkiauv.messenger.MessageObject$GroupedMessages, boolean, int, java.lang.Runnable):void");
    }

    static /* synthetic */ void lambda$null$50(AlertDialog[] progressDialog, TLObject response, BaseFragment fragment, TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, TLRPC.ChatFull chatInfo, long mergeDialogId, MessageObject selectedMessage, SparseArray[] selectedMessages, MessageObject.GroupedMessages selectedGroup, boolean scheduled, Runnable onDelete) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        int loadType = 2;
        if (response != null) {
            TLRPC.TL_channels_channelParticipant participant = (TLRPC.TL_channels_channelParticipant) response;
            if (!(participant.participant instanceof TLRPC.TL_channelParticipantAdmin) && !(participant.participant instanceof TLRPC.TL_channelParticipantCreator)) {
                loadType = 0;
            }
        }
        createDeleteMessagesAlert(fragment, user, chat, encryptedChat, chatInfo, mergeDialogId, selectedMessage, selectedMessages, selectedGroup, scheduled, loadType, onDelete);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$53(AlertDialog[] progressDialog, int currentAccount, int requestId, BaseFragment fragment) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener(currentAccount, requestId) {
                private final /* synthetic */ int f$0;
                private final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    ConnectionsManager.getInstance(this.f$0).cancelRequest(this.f$1, true);
                }
            });
            fragment.showDialog(progressDialog[0]);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$54(boolean[] checks, View v) {
        if (v.isEnabled()) {
            CheckBoxCell cell13 = (CheckBoxCell) v;
            Integer num1 = (Integer) cell13.getTag();
            checks[num1.intValue()] = !checks[num1.intValue()];
            cell13.setChecked(checks[num1.intValue()], true);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$55(boolean[] deleteForAll, View v) {
        deleteForAll[0] = !deleteForAll[0];
        ((CheckBoxCell) v).setChecked(deleteForAll[0], true);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$56(boolean[] deleteForAll, View v) {
        deleteForAll[0] = !deleteForAll[0];
        ((CheckBoxCell) v).setChecked(deleteForAll[0], true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0131 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createDeleteMessagesAlert$58(im.bclpbkiauv.messenger.MessageObject r21, im.bclpbkiauv.messenger.MessageObject.GroupedMessages r22, im.bclpbkiauv.tgnet.TLRPC.EncryptedChat r23, int r24, long r25, boolean[] r27, boolean r28, android.util.SparseArray[] r29, im.bclpbkiauv.tgnet.TLRPC.User r30, boolean[] r31, im.bclpbkiauv.tgnet.TLRPC.Chat r32, im.bclpbkiauv.tgnet.TLRPC.ChatFull r33, java.lang.Runnable r34, android.content.DialogInterface r35, int r36) {
        /*
            r0 = r21
            r1 = r22
            r11 = r30
            r12 = r32
            r2 = 0
            r13 = 10
            r14 = 0
            r10 = 0
            if (r0 == 0) goto L_0x00a6
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r9 = r3
            r2 = 0
            if (r1 == 0) goto L_0x005b
            r3 = 0
        L_0x001a:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r4 = r1.messages
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0059
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r4 = r1.messages
            java.lang.Object r4 = r4.get(r3)
            im.bclpbkiauv.messenger.MessageObject r4 = (im.bclpbkiauv.messenger.MessageObject) r4
            int r5 = r4.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r9.add(r5)
            if (r23 == 0) goto L_0x0056
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            long r5 = r5.random_id
            int r7 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r7 == 0) goto L_0x0056
            int r5 = r4.type
            if (r5 == r13) goto L_0x0056
            if (r2 != 0) goto L_0x004b
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r2 = r5
        L_0x004b:
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            long r5 = r5.random_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            r2.add(r5)
        L_0x0056:
            int r3 = r3 + 1
            goto L_0x001a
        L_0x0059:
            r13 = r2
            goto L_0x0088
        L_0x005b:
            int r3 = r21.getId()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r9.add(r3)
            if (r23 == 0) goto L_0x0087
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r0.messageOwner
            long r3 = r3.random_id
            int r5 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x0087
            int r3 = r0.type
            if (r3 == r13) goto L_0x0087
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r2 = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r0.messageOwner
            long r3 = r3.random_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r2.add(r3)
            r13 = r2
            goto L_0x0088
        L_0x0087:
            r13 = r2
        L_0x0088:
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r24)
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r3 = r3.to_id
            int r8 = r3.channel_id
            boolean r14 = r27[r10]
            r3 = r9
            r4 = r13
            r5 = r23
            r6 = r25
            r15 = r9
            r9 = r14
            r14 = 0
            r10 = r28
            r2.deleteMessages(r3, r4, r5, r6, r8, r9, r10)
            r9 = r15
            r13 = 0
            goto L_0x015a
        L_0x00a6:
            r3 = 1
            r16 = r3
        L_0x00a9:
            if (r16 < 0) goto L_0x0158
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r9 = r3
            r2 = 0
        L_0x00b2:
            r3 = r29[r16]
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x00ca
            r3 = r29[r16]
            int r3 = r3.keyAt(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r9.add(r3)
            int r2 = r2 + 1
            goto L_0x00b2
        L_0x00ca:
            r2 = 0
            r3 = 0
            boolean r4 = r9.isEmpty()
            if (r4 != 0) goto L_0x00f7
            r4 = r29[r16]
            java.lang.Object r5 = r9.get(r10)
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            java.lang.Object r4 = r4.get(r5)
            im.bclpbkiauv.messenger.MessageObject r4 = (im.bclpbkiauv.messenger.MessageObject) r4
            if (r3 != 0) goto L_0x00f7
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id
            int r5 = r5.channel_id
            if (r5 == 0) goto L_0x00f7
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id
            int r3 = r5.channel_id
            r17 = r3
            goto L_0x00f9
        L_0x00f7:
            r17 = r3
        L_0x00f9:
            if (r23 == 0) goto L_0x012f
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r2 = r3
            r3 = 0
        L_0x0102:
            r4 = r29[r16]
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x012c
            r4 = r29[r16]
            java.lang.Object r4 = r4.valueAt(r3)
            im.bclpbkiauv.messenger.MessageObject r4 = (im.bclpbkiauv.messenger.MessageObject) r4
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            long r5 = r5.random_id
            int r7 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r7 == 0) goto L_0x0129
            int r5 = r4.type
            if (r5 == r13) goto L_0x0129
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            long r5 = r5.random_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            r2.add(r5)
        L_0x0129:
            int r3 = r3 + 1
            goto L_0x0102
        L_0x012c:
            r18 = r2
            goto L_0x0131
        L_0x012f:
            r18 = r2
        L_0x0131:
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r24)
            boolean r19 = r27[r10]
            r3 = r9
            r4 = r18
            r5 = r23
            r6 = r25
            r8 = r17
            r20 = r9
            r9 = r19
            r13 = 0
            r10 = r28
            r2.deleteMessages(r3, r4, r5, r6, r8, r9, r10)
            r2 = r29[r16]
            r2.clear()
            int r16 = r16 + -1
            r2 = r20
            r10 = 0
            r13 = 10
            goto L_0x00a9
        L_0x0158:
            r13 = 0
            r9 = r2
        L_0x015a:
            if (r11 == 0) goto L_0x01a0
            boolean r2 = r31[r13]
            if (r2 == 0) goto L_0x016c
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r24)
            int r3 = r12.id
            r4 = r33
            r2.deleteUserFromChat(r3, r11, r4)
            goto L_0x016e
        L_0x016c:
            r4 = r33
        L_0x016e:
            r2 = 1
            boolean r2 = r31[r2]
            if (r2 == 0) goto L_0x0193
            im.bclpbkiauv.tgnet.TLRPC$TL_channels_reportSpam r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_channels_reportSpam
            r2.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputChannel r3 = im.bclpbkiauv.messenger.MessagesController.getInputChannel((im.bclpbkiauv.tgnet.TLRPC.Chat) r32)
            r2.channel = r3
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r24)
            im.bclpbkiauv.tgnet.TLRPC$InputUser r3 = r3.getInputUser((im.bclpbkiauv.tgnet.TLRPC.User) r11)
            r2.user_id = r3
            r2.id = r9
            im.bclpbkiauv.tgnet.ConnectionsManager r3 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r24)
            im.bclpbkiauv.ui.components.-$$Lambda$AlertsCreator$g29vFAb-42Ow5fazJAP75qcx9eU r5 = im.bclpbkiauv.ui.components.$$Lambda$AlertsCreator$g29vFAb42Ow5fazJAP75qcx9eU.INSTANCE
            r3.sendRequest(r2, r5)
        L_0x0193:
            r2 = 2
            boolean r2 = r31[r2]
            if (r2 == 0) goto L_0x01a2
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r24)
            r2.deleteUserChannelHistory(r12, r11, r13)
            goto L_0x01a2
        L_0x01a0:
            r4 = r33
        L_0x01a2:
            if (r34 == 0) goto L_0x01a7
            r34.run()
        L_0x01a7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.AlertsCreator.lambda$createDeleteMessagesAlert$58(im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.messenger.MessageObject$GroupedMessages, im.bclpbkiauv.tgnet.TLRPC$EncryptedChat, int, long, boolean[], boolean, android.util.SparseArray[], im.bclpbkiauv.tgnet.TLRPC$User, boolean[], im.bclpbkiauv.tgnet.TLRPC$Chat, im.bclpbkiauv.tgnet.TLRPC$ChatFull, java.lang.Runnable, android.content.DialogInterface, int):void");
    }

    static /* synthetic */ void lambda$null$57(TLObject response, TLRPC.TL_error error) {
    }
}
