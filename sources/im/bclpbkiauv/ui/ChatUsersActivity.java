package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatRightsEditActivity;
import im.bclpbkiauv.ui.ChatUsersActivity;
import im.bclpbkiauv.ui.GroupCreateActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.adapters.SearchAdapterHelper;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ManageChatTextCell;
import im.bclpbkiauv.ui.cells.ManageChatUserCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCheckCell2;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.UndoView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ChatUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_BANNED = 0;
    public static final int TYPE_KICKED = 3;
    public static final int TYPE_USERS = 2;
    private static final int done_button = 1;
    private static final int search_button = 0;
    /* access modifiers changed from: private */
    public int addNew2Row;
    /* access modifiers changed from: private */
    public int addNewRow;
    /* access modifiers changed from: private */
    public int addNewSectionRow;
    /* access modifiers changed from: private */
    public int addUsersRow;
    /* access modifiers changed from: private */
    public int blockedEmptyRow;
    /* access modifiers changed from: private */
    public int botEndRow;
    /* access modifiers changed from: private */
    public int botHeaderRow;
    /* access modifiers changed from: private */
    public int botStartRow;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> bots = new ArrayList<>();
    private boolean botsEndReached;
    private SparseArray<TLObject> botsMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public int changeInfoRow;
    /* access modifiers changed from: private */
    public int chatId = this.arguments.getInt("chat_id");
    /* access modifiers changed from: private */
    public ArrayList<TLObject> contacts = new ArrayList<>();
    private boolean contactsEndReached;
    /* access modifiers changed from: private */
    public int contactsEndRow;
    /* access modifiers changed from: private */
    public int contactsHeaderRow;
    private SparseArray<TLObject> contactsMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public int contactsStartRow;
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatBannedRights defaultBannedRights = new TLRPC.TL_chatBannedRights();
    private int delayResults;
    private ChatUsersActivityDelegate delegate;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public int embedLinksRow;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean firstLoaded;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    private String initialBannedRights;
    private int initialSlowmode;
    /* access modifiers changed from: private */
    public boolean isChannel;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loadingUsers;
    /* access modifiers changed from: private */
    public int membersHeaderRow;
    private boolean needOpenSearch = this.arguments.getBoolean("open_search");
    /* access modifiers changed from: private */
    public ArrayList<TLObject> participants = new ArrayList<>();
    /* access modifiers changed from: private */
    public int participantsDivider2Row;
    /* access modifiers changed from: private */
    public int participantsDividerRow;
    /* access modifiers changed from: private */
    public int participantsEndRow;
    /* access modifiers changed from: private */
    public int participantsInfoRow;
    /* access modifiers changed from: private */
    public SparseArray<TLObject> participantsMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public int participantsStartRow;
    /* access modifiers changed from: private */
    public int permissionsSectionRow;
    /* access modifiers changed from: private */
    public int pinMessagesRow;
    /* access modifiers changed from: private */
    public int recentActionsRow;
    /* access modifiers changed from: private */
    public int removedUsersRow;
    /* access modifiers changed from: private */
    public int restricted1SectionRow;
    /* access modifiers changed from: private */
    public int rowCount;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public SearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public int selectType = this.arguments.getInt("selectType");
    /* access modifiers changed from: private */
    public int selectedSlowmode;
    /* access modifiers changed from: private */
    public int sendMediaRow;
    /* access modifiers changed from: private */
    public int sendMessagesRow;
    /* access modifiers changed from: private */
    public int sendPollsRow;
    /* access modifiers changed from: private */
    public int sendStickersRow;
    /* access modifiers changed from: private */
    public int slowmodeInfoRow;
    /* access modifiers changed from: private */
    public int slowmodeRow;
    /* access modifiers changed from: private */
    public int slowmodeSelectRow;
    /* access modifiers changed from: private */
    public int type = this.arguments.getInt("type");
    private UndoView undoView;

    public interface ChatUsersActivityDelegate {
        void didAddParticipantToList(int i, TLObject tLObject);

        void didChangeOwner(TLRPC.User user);
    }

    private class ChooseView extends View {
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private Paint paint = new Paint(1);
        private int sideSide;
        private ArrayList<Integer> sizes = new ArrayList<>();
        private boolean startMoving;
        private int startMovingItem;
        private float startX;
        private ArrayList<String> strings = new ArrayList<>();
        private TextPaint textPaint;

        public ChooseView(Context context) {
            super(context);
            String string;
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
            for (int a = 0; a < 7; a++) {
                if (a == 0) {
                    string = LocaleController.getString("SlowmodeOff", R.string.SlowmodeOff);
                } else if (a == 1) {
                    string = LocaleController.formatString("SlowmodeSeconds", R.string.SlowmodeSeconds, 10);
                } else if (a == 2) {
                    string = LocaleController.formatString("SlowmodeSeconds", R.string.SlowmodeSeconds, 30);
                } else if (a == 3) {
                    string = LocaleController.formatString("SlowmodeMinutes", R.string.SlowmodeMinutes, 1);
                } else if (a == 4) {
                    string = LocaleController.formatString("SlowmodeMinutes", R.string.SlowmodeMinutes, 5);
                } else if (a != 5) {
                    string = LocaleController.formatString("SlowmodeHours", R.string.SlowmodeHours, 1);
                } else {
                    string = LocaleController.formatString("SlowmodeMinutes", R.string.SlowmodeMinutes, 15);
                }
                this.strings.add(string);
                this.sizes.add(Integer.valueOf((int) Math.ceil((double) this.textPaint.measureText(string))));
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            boolean z = false;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int a = 0;
                while (true) {
                    if (a >= this.strings.size()) {
                        break;
                    }
                    int i = this.sideSide;
                    int i2 = this.lineSize + (this.gapSize * 2);
                    int i3 = this.circleSize;
                    int cx = i + ((i2 + i3) * a) + (i3 / 2);
                    if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                        a++;
                    } else {
                        if (a == ChatUsersActivity.this.selectedSlowmode) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingItem = ChatUsersActivity.this.selectedSlowmode;
                    }
                }
            } else if (event.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    int a2 = 0;
                    while (true) {
                        if (a2 >= this.strings.size()) {
                            break;
                        }
                        int i4 = this.sideSide;
                        int i5 = this.lineSize;
                        int i6 = this.gapSize;
                        int i7 = this.circleSize;
                        int cx2 = i4 + (((i6 * 2) + i5 + i7) * a2) + (i7 / 2);
                        int diff = (i5 / 2) + (i7 / 2) + i6;
                        if (x <= ((float) (cx2 - diff)) || x >= ((float) (cx2 + diff))) {
                            a2++;
                        } else if (ChatUsersActivity.this.selectedSlowmode != a2) {
                            setItem(a2);
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (!this.moving) {
                    int a3 = 0;
                    while (true) {
                        if (a3 >= this.strings.size()) {
                            break;
                        }
                        int i8 = this.sideSide;
                        int i9 = this.lineSize + (this.gapSize * 2);
                        int i10 = this.circleSize;
                        int cx3 = i8 + ((i9 + i10) * a3) + (i10 / 2);
                        if (x <= ((float) (cx3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx3))) {
                            a3++;
                        } else if (ChatUsersActivity.this.selectedSlowmode != a3) {
                            setItem(a3);
                        }
                    }
                } else if (ChatUsersActivity.this.selectedSlowmode != this.startMovingItem) {
                    setItem(ChatUsersActivity.this.selectedSlowmode);
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        private void setItem(int index) {
            if (ChatUsersActivity.this.info != null) {
                int unused = ChatUsersActivity.this.selectedSlowmode = index;
                ChatUsersActivity.this.info.slowmode_seconds = ChatUsersActivity.this.getSecondsForIndex(index);
                ChatUsersActivity.this.info.flags |= 131072;
                for (int a = 0; a < 3; a++) {
                    RecyclerView.ViewHolder holder = ChatUsersActivity.this.listView.findViewHolderForAdapterPosition(ChatUsersActivity.this.slowmodeInfoRow);
                    if (holder != null) {
                        ChatUsersActivity.this.listViewAdapter.onBindViewHolder(holder, ChatUsersActivity.this.slowmodeInfoRow);
                    }
                }
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), 1073741824));
            int size = View.MeasureSpec.getSize(widthMeasureSpec);
            this.circleSize = AndroidUtilities.dp(6.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(22.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * this.strings.size())) - ((this.gapSize * 2) * (this.strings.size() - 1))) - (this.sideSide * 2)) / (this.strings.size() - 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int a = 0;
            while (a < this.strings.size()) {
                int i = this.sideSide;
                int i2 = this.lineSize + (this.gapSize * 2);
                int i3 = this.circleSize;
                int cx = i + ((i2 + i3) * a) + (i3 / 2);
                if (a <= ChatUsersActivity.this.selectedSlowmode) {
                    this.paint.setColor(Theme.getColor(Theme.key_switchTrackChecked));
                } else {
                    this.paint.setColor(Theme.getColor(Theme.key_switchTrack));
                }
                canvas.drawCircle((float) cx, (float) cy, (float) (a == ChatUsersActivity.this.selectedSlowmode ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    int width = this.lineSize;
                    if (a == ChatUsersActivity.this.selectedSlowmode || a == ChatUsersActivity.this.selectedSlowmode + 1) {
                        width -= AndroidUtilities.dp(3.0f);
                    }
                    if (a == ChatUsersActivity.this.selectedSlowmode + 1) {
                        x += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (x + width), (float) (AndroidUtilities.dp(1.0f) + cy), this.paint);
                }
                int size = this.sizes.get(a).intValue();
                String text = this.strings.get(a);
                if (a == 0) {
                    canvas.drawText(text, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (a == this.strings.size() - 1) {
                    canvas.drawText(text, (float) ((getMeasuredWidth() - size) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(text, (float) (cx - (size / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                }
                a++;
            }
        }
    }

    public ChatUsersActivity(Bundle args) {
        super(args);
        TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(this.chatId));
        this.currentChat = chat;
        if (!(chat == null || chat.default_banned_rights == null)) {
            this.defaultBannedRights.view_messages = this.currentChat.default_banned_rights.view_messages;
            this.defaultBannedRights.send_stickers = this.currentChat.default_banned_rights.send_stickers;
            this.defaultBannedRights.send_media = this.currentChat.default_banned_rights.send_media;
            this.defaultBannedRights.embed_links = this.currentChat.default_banned_rights.embed_links;
            this.defaultBannedRights.send_messages = this.currentChat.default_banned_rights.send_messages;
            this.defaultBannedRights.send_games = this.currentChat.default_banned_rights.send_games;
            this.defaultBannedRights.send_inline = this.currentChat.default_banned_rights.send_inline;
            this.defaultBannedRights.send_gifs = this.currentChat.default_banned_rights.send_gifs;
            this.defaultBannedRights.pin_messages = this.currentChat.default_banned_rights.pin_messages;
            this.defaultBannedRights.send_polls = this.currentChat.default_banned_rights.send_polls;
            this.defaultBannedRights.invite_users = this.currentChat.default_banned_rights.invite_users;
            this.defaultBannedRights.change_info = this.currentChat.default_banned_rights.change_info;
        }
        this.initialBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        this.isChannel = ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        TLRPC.ChatFull chatFull;
        TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(this.chatId));
        this.currentChat = chat;
        if (chat != null) {
            this.recentActionsRow = -1;
            this.addNewRow = -1;
            this.addNew2Row = -1;
            this.addNewSectionRow = -1;
            this.restricted1SectionRow = -1;
            this.participantsStartRow = -1;
            this.participantsDividerRow = -1;
            this.participantsDivider2Row = -1;
            this.participantsEndRow = -1;
            this.participantsInfoRow = -1;
            this.blockedEmptyRow = -1;
            this.permissionsSectionRow = -1;
            this.sendMessagesRow = -1;
            this.sendMediaRow = -1;
            this.sendStickersRow = -1;
            this.sendPollsRow = -1;
            this.embedLinksRow = -1;
            this.addUsersRow = -1;
            this.pinMessagesRow = -1;
            this.changeInfoRow = -1;
            this.removedUsersRow = -1;
            this.contactsHeaderRow = -1;
            this.contactsStartRow = -1;
            this.contactsEndRow = -1;
            this.botHeaderRow = -1;
            this.botStartRow = -1;
            this.botEndRow = -1;
            this.membersHeaderRow = -1;
            this.slowmodeRow = -1;
            this.slowmodeSelectRow = -1;
            this.slowmodeInfoRow = -1;
            this.rowCount = 0;
            int i = this.type;
            if (i == 3) {
                int i2 = 0 + 1;
                this.rowCount = i2;
                this.permissionsSectionRow = 0;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.sendMessagesRow = i2;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.sendMediaRow = i3;
                int i5 = i4 + 1;
                this.rowCount = i5;
                this.sendStickersRow = i4;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.sendPollsRow = i5;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.embedLinksRow = i6;
                int i8 = i7 + 1;
                this.rowCount = i8;
                this.addUsersRow = i7;
                int i9 = i8 + 1;
                this.rowCount = i9;
                this.pinMessagesRow = i8;
                this.rowCount = i9 + 1;
                this.changeInfoRow = i9;
                if ((!ChatObject.isChannel(chat) && this.currentChat.creator) || (this.currentChat.megagroup && ChatObject.canBlockUsers(this.currentChat))) {
                    int i10 = this.rowCount;
                    int i11 = i10 + 1;
                    this.rowCount = i11;
                    this.participantsDivider2Row = i10;
                    int i12 = i11 + 1;
                    this.rowCount = i12;
                    this.slowmodeRow = i11;
                    int i13 = i12 + 1;
                    this.rowCount = i13;
                    this.slowmodeSelectRow = i12;
                    this.rowCount = i13 + 1;
                    this.slowmodeInfoRow = i13;
                }
                if (ChatObject.isChannel(this.currentChat)) {
                    if (this.participantsDivider2Row == -1) {
                        int i14 = this.rowCount;
                        this.rowCount = i14 + 1;
                        this.participantsDivider2Row = i14;
                    }
                    int i15 = this.rowCount;
                    this.rowCount = i15 + 1;
                    this.removedUsersRow = i15;
                }
                int i16 = this.rowCount;
                this.rowCount = i16 + 1;
                this.participantsDividerRow = i16;
                if (ChatObject.canBlockUsers(this.currentChat)) {
                    int i17 = this.rowCount;
                    this.rowCount = i17 + 1;
                    this.addNewRow = i17;
                }
                if (!this.participants.isEmpty()) {
                    int i18 = this.rowCount;
                    this.participantsStartRow = i18;
                    int size = i18 + this.participants.size();
                    this.rowCount = size;
                    this.participantsEndRow = size;
                }
            } else if (i == 0) {
                if (ChatObject.canBlockUsers(chat)) {
                    int i19 = this.rowCount;
                    this.rowCount = i19 + 1;
                    this.addNewRow = i19;
                    if (!this.participants.isEmpty()) {
                        int i20 = this.rowCount;
                        this.rowCount = i20 + 1;
                        this.participantsInfoRow = i20;
                    }
                }
                if (!this.participants.isEmpty()) {
                    int i21 = this.rowCount;
                    int i22 = i21 + 1;
                    this.rowCount = i22;
                    this.restricted1SectionRow = i21;
                    this.participantsStartRow = i22;
                    int size2 = i22 + this.participants.size();
                    this.rowCount = size2;
                    this.participantsEndRow = size2;
                }
                if (this.participantsStartRow == -1) {
                    int i23 = this.rowCount;
                    this.rowCount = i23 + 1;
                    this.blockedEmptyRow = i23;
                } else if (this.participantsInfoRow == -1) {
                    int i24 = this.rowCount;
                    this.rowCount = i24 + 1;
                    this.participantsInfoRow = i24;
                }
            } else if (i == 1) {
                if (ChatObject.isChannel(chat) && this.currentChat.megagroup && ((chatFull = this.info) == null || chatFull.participants_count <= 200)) {
                    int i25 = this.rowCount;
                    int i26 = i25 + 1;
                    this.rowCount = i26;
                    this.recentActionsRow = i25;
                    this.rowCount = i26 + 1;
                    this.addNewSectionRow = i26;
                }
                if (ChatObject.canAddAdmins(this.currentChat)) {
                    int i27 = this.rowCount;
                    this.rowCount = i27 + 1;
                    this.addNewRow = i27;
                }
                if (!this.participants.isEmpty()) {
                    int i28 = this.rowCount;
                    this.participantsStartRow = i28;
                    int size3 = i28 + this.participants.size();
                    this.rowCount = size3;
                    this.participantsEndRow = size3;
                }
                if (this.currentChat.creator) {
                    int i29 = this.rowCount;
                    this.rowCount = i29 + 1;
                    this.participantsInfoRow = i29;
                }
            } else if (i == 2) {
                if (this.selectType == 0 && ChatObject.canAddUsers(chat)) {
                    int i30 = this.rowCount;
                    this.rowCount = i30 + 1;
                    this.addNewRow = i30;
                }
                boolean hasAnyOther = false;
                if (!this.contacts.isEmpty()) {
                    int i31 = this.rowCount;
                    int i32 = i31 + 1;
                    this.rowCount = i32;
                    this.contactsHeaderRow = i31;
                    this.contactsStartRow = i32;
                    int size4 = i32 + this.contacts.size();
                    this.rowCount = size4;
                    this.contactsEndRow = size4;
                    hasAnyOther = true;
                }
                if (!this.bots.isEmpty()) {
                    int i33 = this.rowCount;
                    int i34 = i33 + 1;
                    this.rowCount = i34;
                    this.botHeaderRow = i33;
                    this.botStartRow = i34;
                    int size5 = i34 + this.bots.size();
                    this.rowCount = size5;
                    this.botEndRow = size5;
                    hasAnyOther = true;
                }
                if (!this.participants.isEmpty()) {
                    if (hasAnyOther) {
                        int i35 = this.rowCount;
                        this.rowCount = i35 + 1;
                        this.membersHeaderRow = i35;
                    }
                    int i36 = this.rowCount;
                    this.participantsStartRow = i36;
                    int size6 = i36 + this.participants.size();
                    this.rowCount = size6;
                    this.participantsEndRow = size6;
                }
                int i37 = this.rowCount;
                if (i37 != 0 && this.isChannel && this.selectType == 0) {
                    this.rowCount = i37 + 1;
                    this.participantsInfoRow = i37;
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        loadChatParticipants(0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public View createView(Context context) {
        int i;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        int i2 = 1;
        this.actionBar.setAllowOverlayTitle(true);
        int i3 = this.type;
        if (i3 == 3) {
            this.actionBar.setTitle(LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions));
        } else if (i3 == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist));
        } else if (i3 == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators));
        } else if (i3 == 2) {
            int i4 = this.selectType;
            if (i4 == 0) {
                if (this.isChannel) {
                    this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                }
            } else if (i4 == 1) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddAdmin", R.string.ChannelAddAdmin));
            } else if (i4 == 2) {
                this.actionBar.setTitle(LocaleController.getString("ChannelBlockUser", R.string.ChannelBlockUser));
            } else if (i4 == 3) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddException", R.string.ChannelAddException));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatUsersActivity.this.checkDiscard()) {
                        ChatUsersActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    ChatUsersActivity.this.processDone();
                }
            }
        });
        if (this.selectType != 0 || (i = this.type) == 2 || i == 0 || i == 3) {
            this.searchListViewAdapter = new SearchAdapter(context);
            ActionBarMenu menu = this.actionBar.createMenu();
            ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    boolean unused = ChatUsersActivity.this.searching = true;
                    ChatUsersActivity.this.emptyView.setShowAtCenter(true);
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(8);
                    }
                }

                public void onSearchCollapse() {
                    ChatUsersActivity.this.searchListViewAdapter.searchDialogs((String) null);
                    boolean unused = ChatUsersActivity.this.searching = false;
                    boolean unused2 = ChatUsersActivity.this.searchWas = false;
                    ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                    ChatUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    ChatUsersActivity.this.listView.setFastScrollVisible(true);
                    ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(false);
                    ChatUsersActivity.this.emptyView.setShowAtCenter(false);
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(0);
                    }
                }

                public void onTextChanged(EditText editText) {
                    if (ChatUsersActivity.this.searchListViewAdapter != null) {
                        String text = editText.getText().toString();
                        if (text.length() != 0) {
                            boolean unused = ChatUsersActivity.this.searchWas = true;
                            if (!(ChatUsersActivity.this.listView == null || ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.searchListViewAdapter)) {
                                ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
                                ChatUsersActivity.this.searchListViewAdapter.notifyDataSetChanged();
                                ChatUsersActivity.this.listView.setFastScrollVisible(false);
                                ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
                            }
                        }
                        ChatUsersActivity.this.searchListViewAdapter.searchDialogs(text);
                    }
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            if (this.type == 3) {
                actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("ChannelSearchException", R.string.ChannelSearchException));
            } else {
                actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
            }
            if (this.type == 3) {
                this.doneItem = menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
            }
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        int i5 = this.type;
        if (i5 == 0 || i5 == 2 || i5 == 3) {
            this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        }
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i2 = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i2);
        this.listView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ChatUsersActivity.this.lambda$createView$2$ChatUsersActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ChatUsersActivity.this.lambda$createView$3$ChatUsersActivity(view, i);
            }
        });
        if (this.searchItem != null) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1) {
                        AndroidUtilities.hideKeyboard(ChatUsersActivity.this.getParentActivity().getCurrentFocus());
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                }
            });
        }
        UndoView undoView2 = new UndoView(context);
        this.undoView = undoView2;
        frameLayout.addView(undoView2, LayoutHelper.createFrame(-1.0f, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        if (this.loadingUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$ChatUsersActivity(View view, int position) {
        boolean canEditAdmin;
        String rank;
        TLRPC.TL_chatAdminRights adminRights;
        TLRPC.TL_chatBannedRights bannedRights;
        int user_id;
        TLObject participant;
        boolean z;
        TLRPC.Chat chat;
        int i;
        TLObject participant2;
        TLObject participant3;
        int i2 = position;
        boolean listAdapter = this.listView.getAdapter() == this.listViewAdapter;
        int i3 = 3;
        if (listAdapter) {
            if (i2 == this.addNewRow) {
                int i4 = this.type;
                if (i4 == 0 || i4 == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", this.chatId);
                    bundle.putInt("type", 2);
                    if (this.type == 0) {
                        i3 = 2;
                    }
                    bundle.putInt("selectType", i3);
                    ChatUsersActivity fragment = new ChatUsersActivity(bundle);
                    fragment.setInfo(this.info);
                    presentFragment(fragment);
                    return;
                } else if (i4 == 1) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("chat_id", this.chatId);
                    bundle2.putInt("type", 2);
                    bundle2.putInt("selectType", 1);
                    ChatUsersActivity fragment2 = new ChatUsersActivity(bundle2);
                    fragment2.setDelegate(new ChatUsersActivityDelegate() {
                        public void didAddParticipantToList(int uid, TLObject participant) {
                            if (participant != null && ChatUsersActivity.this.participantsMap.get(uid) == null) {
                                ChatUsersActivity.this.participants.add(participant);
                                Collections.sort(ChatUsersActivity.this.participants, new Comparator() {
                                    public final int compare(Object obj, Object obj2) {
                                        return ChatUsersActivity.AnonymousClass3.this.lambda$didAddParticipantToList$0$ChatUsersActivity$3((TLObject) obj, (TLObject) obj2);
                                    }
                                });
                                ChatUsersActivity.this.updateRows();
                                if (ChatUsersActivity.this.listViewAdapter != null) {
                                    ChatUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        public /* synthetic */ int lambda$didAddParticipantToList$0$ChatUsersActivity$3(TLObject lhs, TLObject rhs) {
                            int type1 = ChatUsersActivity.this.getChannelAdminParticipantType(lhs);
                            int type2 = ChatUsersActivity.this.getChannelAdminParticipantType(rhs);
                            if (type1 > type2) {
                                return 1;
                            }
                            if (type1 < type2) {
                                return -1;
                            }
                            return 0;
                        }

                        public void didChangeOwner(TLRPC.User user) {
                            ChatUsersActivity.this.onOwnerChaged(user);
                        }
                    });
                    fragment2.setInfo(this.info);
                    presentFragment(fragment2);
                    return;
                } else if (i4 == 2) {
                    Bundle args = new Bundle();
                    args.putBoolean("addToGroup", true);
                    args.putInt(this.isChannel ? "channelId" : "chatId", this.currentChat.id);
                    GroupCreateActivity fragment3 = new GroupCreateActivity(args);
                    fragment3.setInfo(this.info);
                    SparseArray<TLObject> sparseArray = this.contactsMap;
                    fragment3.setIgnoreUsers((sparseArray == null || sparseArray.size() == 0) ? this.participantsMap : this.contactsMap);
                    fragment3.setDelegate((GroupCreateActivity.ContactsAddActivityDelegate) new GroupCreateActivity.ContactsAddActivityDelegate() {
                        public void didSelectUsers(ArrayList<TLRPC.User> users, int fwdCount) {
                            int N = users.size();
                            for (int a = 0; a < N; a++) {
                                ChatUsersActivity.this.getMessagesController().addUserToChat(ChatUsersActivity.this.chatId, users.get(a), (TLRPC.ChatFull) null, fwdCount, (String) null, ChatUsersActivity.this, (Runnable) null);
                            }
                        }

                        public void needAddBot(TLRPC.User user) {
                            ChatUsersActivity.this.openRightsEdit(user.id, (TLObject) null, (TLRPC.TL_chatAdminRights) null, (TLRPC.TL_chatBannedRights) null, "", true, 0, false);
                        }
                    });
                    presentFragment(fragment3);
                    return;
                } else {
                    return;
                }
            } else if (i2 == this.recentActionsRow) {
                presentFragment(new ChannelAdminLogActivity(this.currentChat));
                return;
            } else if (i2 == this.removedUsersRow) {
                Bundle args2 = new Bundle();
                args2.putInt("chat_id", this.chatId);
                args2.putInt("type", 0);
                ChatUsersActivity fragment4 = new ChatUsersActivity(args2);
                fragment4.setInfo(this.info);
                presentFragment(fragment4);
                return;
            } else if (i2 == this.addNew2Row) {
                presentFragment(new GroupInviteActivity(this.chatId));
                return;
            } else if (i2 > this.permissionsSectionRow && i2 <= this.changeInfoRow) {
                TextCheckCell2 checkCell = (TextCheckCell2) view;
                if (checkCell.isEnabled()) {
                    if (!checkCell.hasIcon()) {
                        checkCell.setChecked(!checkCell.isChecked());
                        if (i2 == this.changeInfoRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights = this.defaultBannedRights;
                            tL_chatBannedRights.change_info = !tL_chatBannedRights.change_info;
                            return;
                        } else if (i2 == this.addUsersRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights2 = this.defaultBannedRights;
                            tL_chatBannedRights2.invite_users = !tL_chatBannedRights2.invite_users;
                            return;
                        } else if (i2 == this.pinMessagesRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights3 = this.defaultBannedRights;
                            tL_chatBannedRights3.pin_messages = !tL_chatBannedRights3.pin_messages;
                            return;
                        } else {
                            boolean disabled = !checkCell.isChecked();
                            if (i2 == this.sendMessagesRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights4 = this.defaultBannedRights;
                                tL_chatBannedRights4.send_messages = !tL_chatBannedRights4.send_messages;
                            } else if (i2 == this.sendMediaRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights5 = this.defaultBannedRights;
                                tL_chatBannedRights5.send_media = !tL_chatBannedRights5.send_media;
                            } else if (i2 == this.sendStickersRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights6 = this.defaultBannedRights;
                                boolean z2 = !tL_chatBannedRights6.send_stickers;
                                tL_chatBannedRights6.send_inline = z2;
                                tL_chatBannedRights6.send_gifs = z2;
                                tL_chatBannedRights6.send_games = z2;
                                tL_chatBannedRights6.send_stickers = z2;
                            } else if (i2 == this.embedLinksRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights7 = this.defaultBannedRights;
                                tL_chatBannedRights7.embed_links = !tL_chatBannedRights7.embed_links;
                            } else if (i2 == this.sendPollsRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights8 = this.defaultBannedRights;
                                tL_chatBannedRights8.send_polls = !tL_chatBannedRights8.send_polls;
                            }
                            if (disabled) {
                                if (this.defaultBannedRights.view_messages && !this.defaultBannedRights.send_messages) {
                                    this.defaultBannedRights.send_messages = true;
                                    RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                    if (holder != null) {
                                        ((TextCheckCell2) holder.itemView).setChecked(false);
                                    }
                                }
                                if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_media) {
                                    this.defaultBannedRights.send_media = true;
                                    RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                                    if (holder2 != null) {
                                        ((TextCheckCell2) holder2.itemView).setChecked(false);
                                    }
                                }
                                if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_polls) {
                                    this.defaultBannedRights.send_polls = true;
                                    RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                                    if (holder3 != null) {
                                        ((TextCheckCell2) holder3.itemView).setChecked(false);
                                    }
                                }
                                if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_stickers) {
                                    TLRPC.TL_chatBannedRights tL_chatBannedRights9 = this.defaultBannedRights;
                                    tL_chatBannedRights9.send_inline = true;
                                    tL_chatBannedRights9.send_gifs = true;
                                    tL_chatBannedRights9.send_games = true;
                                    tL_chatBannedRights9.send_stickers = true;
                                    RecyclerView.ViewHolder holder4 = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                                    if (holder4 != null) {
                                        ((TextCheckCell2) holder4.itemView).setChecked(false);
                                    }
                                }
                                if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.embed_links) {
                                    this.defaultBannedRights.embed_links = true;
                                    RecyclerView.ViewHolder holder5 = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                                    if (holder5 != null) {
                                        ((TextCheckCell2) holder5.itemView).setChecked(false);
                                        return;
                                    }
                                    return;
                                }
                                return;
                            } else if ((!this.defaultBannedRights.embed_links || !this.defaultBannedRights.send_inline || !this.defaultBannedRights.send_media || !this.defaultBannedRights.send_polls) && this.defaultBannedRights.send_messages) {
                                this.defaultBannedRights.send_messages = false;
                                RecyclerView.ViewHolder holder6 = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                if (holder6 != null) {
                                    ((TextCheckCell2) holder6.itemView).setChecked(true);
                                    return;
                                }
                                return;
                            } else {
                                return;
                            }
                        }
                    } else if (TextUtils.isEmpty(this.currentChat.username) || !(i2 == this.pinMessagesRow || i2 == this.changeInfoRow)) {
                        ToastUtils.show((int) R.string.EditCantEditPermissions);
                        return;
                    } else {
                        ToastUtils.show((int) R.string.EditCantEditPermissionsPublic);
                        return;
                    }
                } else {
                    return;
                }
            }
        }
        TLRPC.TL_chatBannedRights bannedRights2 = null;
        TLRPC.TL_chatAdminRights adminRights2 = null;
        String rank2 = "";
        int user_id2 = 0;
        boolean canEditAdmin2 = false;
        if (listAdapter) {
            TLObject participant4 = this.listViewAdapter.getItem(i2);
            if (participant4 instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) participant4;
                user_id2 = channelParticipant.user_id;
                bannedRights2 = channelParticipant.banned_rights;
                adminRights2 = channelParticipant.admin_rights;
                rank2 = channelParticipant.rank;
                canEditAdmin2 = ChatObject.canAddAdmins(this.currentChat) && !(participant4 instanceof TLRPC.TL_channelParticipantCreator) && (adminRights2 == null || !adminRights2.add_admins);
                if (participant4 instanceof TLRPC.TL_channelParticipantCreator) {
                    adminRights2 = new TLRPC.TL_chatAdminRights();
                    adminRights2.add_admins = true;
                    adminRights2.pin_messages = true;
                    adminRights2.invite_users = true;
                    adminRights2.ban_users = true;
                    adminRights2.delete_messages = true;
                    adminRights2.edit_messages = true;
                    adminRights2.post_messages = true;
                    adminRights2.change_info = true;
                }
            } else if (participant4 instanceof TLRPC.ChatParticipant) {
                int user_id3 = ((TLRPC.ChatParticipant) participant4).user_id;
                boolean canEditAdmin3 = this.currentChat.creator;
                if (participant4 instanceof TLRPC.TL_chatParticipantCreator) {
                    adminRights2 = new TLRPC.TL_chatAdminRights();
                    adminRights2.add_admins = true;
                    adminRights2.pin_messages = true;
                    adminRights2.invite_users = true;
                    adminRights2.ban_users = true;
                    adminRights2.delete_messages = true;
                    adminRights2.edit_messages = true;
                    adminRights2.post_messages = true;
                    adminRights2.change_info = true;
                }
                adminRights = adminRights2;
                rank = rank2;
                canEditAdmin = canEditAdmin3;
                participant = participant4;
                bannedRights = null;
                user_id = user_id3;
            }
            adminRights = adminRights2;
            rank = rank2;
            canEditAdmin = canEditAdmin2;
            participant = participant4;
            bannedRights = bannedRights2;
            user_id = user_id2;
        } else {
            TLObject object = this.searchListViewAdapter.getItem(i2);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                getMessagesController().putUser(user, false);
                int i5 = user.id;
                user_id2 = i5;
                participant3 = getAnyParticipant(i5);
            } else if ((object instanceof TLRPC.ChannelParticipant) || (object instanceof TLRPC.ChatParticipant)) {
                participant3 = object;
            } else {
                participant3 = null;
            }
            if (!(participant3 instanceof TLRPC.ChannelParticipant)) {
                if (participant3 instanceof TLRPC.ChatParticipant) {
                    if (!(participant3 instanceof TLRPC.TL_chatParticipantCreator)) {
                        user_id2 = ((TLRPC.ChatParticipant) participant3).user_id;
                        canEditAdmin2 = this.currentChat.creator;
                        bannedRights2 = null;
                        adminRights2 = null;
                    } else {
                        return;
                    }
                } else if (participant3 == null) {
                    bannedRights = null;
                    rank = rank2;
                    canEditAdmin = true;
                    participant = participant3;
                    adminRights = null;
                    user_id = user_id2;
                }
                bannedRights = bannedRights2;
                rank = rank2;
                canEditAdmin = canEditAdmin2;
                participant = participant3;
                adminRights = adminRights2;
                user_id = user_id2;
            } else if (!(participant3 instanceof TLRPC.TL_channelParticipantCreator)) {
                TLRPC.ChannelParticipant channelParticipant2 = (TLRPC.ChannelParticipant) participant3;
                int user_id4 = channelParticipant2.user_id;
                boolean canEditAdmin4 = ChatObject.canAddAdmins(this.currentChat) && (0 == 0 || !null.add_admins);
                TLRPC.TL_chatBannedRights bannedRights3 = channelParticipant2.banned_rights;
                TLRPC.TL_chatAdminRights adminRights3 = channelParticipant2.admin_rights;
                bannedRights = bannedRights3;
                rank = channelParticipant2.rank;
                canEditAdmin = canEditAdmin4;
                participant = participant3;
                adminRights = adminRights3;
                user_id = user_id4;
            } else {
                return;
            }
        }
        if (user_id != 0) {
            int i6 = this.selectType;
            if (i6 == 0) {
                TLObject participant5 = participant;
                String str = "OK";
                boolean z3 = listAdapter;
                int user_id5 = user_id;
                boolean canEdit = false;
                int i7 = this.type;
                if (i7 == 1) {
                    canEdit = user_id5 != getUserConfig().getClientUserId() && (this.currentChat.creator || canEditAdmin);
                } else if (i7 == 0 || i7 == 3) {
                    canEdit = ChatObject.canBlockUsers(this.currentChat);
                }
                int i8 = this.type;
                if (i8 == 0) {
                    z = true;
                } else if ((i8 == 1 || !this.isChannel) && !(this.type == 2 && this.selectType == 0)) {
                    if (bannedRights == null) {
                        bannedRights = new TLRPC.TL_chatBannedRights();
                        i = 1;
                        bannedRights.view_messages = true;
                        bannedRights.send_stickers = true;
                        bannedRights.send_media = true;
                        bannedRights.embed_links = true;
                        bannedRights.send_messages = true;
                        bannedRights.send_games = true;
                        bannedRights.send_inline = true;
                        bannedRights.send_gifs = true;
                        bannedRights.pin_messages = true;
                        bannedRights.send_polls = true;
                        bannedRights.invite_users = true;
                        bannedRights.change_info = true;
                    } else {
                        i = 1;
                    }
                    final TLObject participant6 = participant5;
                    ChatRightsEditActivity fragment5 = new ChatRightsEditActivity(user_id5, this.chatId, adminRights, this.defaultBannedRights, bannedRights, rank, this.type == i ? 0 : 1, canEdit, participant6 == null);
                    fragment5.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                        public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                            TLObject tLObject = participant6;
                            if (tLObject instanceof TLRPC.ChannelParticipant) {
                                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                                channelParticipant.admin_rights = rightsAdmin;
                                channelParticipant.banned_rights = rightsBanned;
                                channelParticipant.rank = rank;
                                ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, rightsAdmin, rightsBanned, 0, false);
                            }
                        }

                        public void didChangeOwner(TLRPC.User user) {
                            ChatUsersActivity.this.onOwnerChaged(user);
                        }
                    });
                    presentFragment(fragment5);
                    return;
                } else {
                    TLObject tLObject = participant5;
                    z = true;
                }
                if (this.currentChat instanceof TLRPC.TL_channelForbidden) {
                    getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
                    getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    AlertDialog dialog = new AlertDialog(getParentActivity(), 0);
                    dialog.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    dialog.setMessage(LocaleController.getString("DeleteThisGroup", R.string.DeleteThisGroup));
                    dialog.setPositiveButton(LocaleController.getString(str, R.string.OK), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatUsersActivity.this.lambda$null$1$ChatUsersActivity(dialogInterface, i);
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    showDialog(dialog);
                } else if (user_id5 != getUserConfig().getClientUserId()) {
                    TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(user_id5));
                    if (user2.self || (chat = this.currentChat) == null || !chat.megagroup || (this.currentChat.flags & ConnectionsManager.FileTypeVideo) == 0 || user2.mutual_contact || ChatObject.hasAdminRights(this.currentChat)) {
                        Bundle args3 = new Bundle();
                        args3.putInt("user_id", user_id5);
                        TLRPC.Chat chat2 = this.currentChat;
                        if (chat2 != null) {
                            if (!chat2.megagroup || (33554432 & this.currentChat.flags) == 0) {
                                z = false;
                            }
                            args3.putBoolean("forbid_add_contact", z);
                            args3.putBoolean("has_admin_right", ChatObject.hasAdminRights(this.currentChat));
                        }
                        args3.putInt("from_type", 2);
                        presentFragment(new NewProfileActivity(args3));
                        return;
                    }
                    ToastUtils.show((int) R.string.ForbidViewUserInfoTips);
                }
            } else if (i6 == 3 || i6 == 1) {
                if (this.selectType == 1 || !canEditAdmin) {
                    participant2 = participant;
                    boolean z4 = listAdapter;
                } else if ((participant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_chatParticipantAdmin)) {
                    TLRPC.User user3 = getMessagesController().getUser(Integer.valueOf(user_id));
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(user3.first_name, user3.last_name)));
                    String string = LocaleController.getString("OK", R.string.OK);
                    TLObject participant7 = participant;
                    boolean z5 = listAdapter;
                    $$Lambda$ChatUsersActivity$qRxwLtwTgkXuGuOukPuxeoWM9C8 r11 = r0;
                    AlertDialog.Builder builder2 = builder;
                    $$Lambda$ChatUsersActivity$qRxwLtwTgkXuGuOukPuxeoWM9C8 r0 = new DialogInterface.OnClickListener(user3, participant7, adminRights, bannedRights, rank, canEditAdmin) {
                        private final /* synthetic */ TLRPC.User f$1;
                        private final /* synthetic */ TLObject f$2;
                        private final /* synthetic */ TLRPC.TL_chatAdminRights f$3;
                        private final /* synthetic */ TLRPC.TL_chatBannedRights f$4;
                        private final /* synthetic */ String f$5;
                        private final /* synthetic */ boolean f$6;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatUsersActivity.this.lambda$null$0$ChatUsersActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, dialogInterface, i);
                        }
                    };
                    builder2.setPositiveButton(string, r11);
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    showDialog(builder2.create());
                    int i9 = user_id;
                    TLObject tLObject2 = participant7;
                    return;
                } else {
                    participant2 = participant;
                    boolean z6 = listAdapter;
                }
                int i10 = this.selectType == 1 ? 0 : 1;
                int i11 = this.selectType;
                int i12 = user_id;
                openRightsEdit(user_id, participant2, adminRights, bannedRights, rank, canEditAdmin, i10, i11 == 1 || i11 == 3);
                TLObject tLObject3 = participant2;
            } else {
                removeUser(user_id);
                TLObject tLObject4 = participant;
                boolean z7 = listAdapter;
                int i13 = user_id;
            }
        } else {
            boolean z8 = listAdapter;
            int i14 = user_id;
        }
    }

    public /* synthetic */ void lambda$null$0$ChatUsersActivity(TLRPC.User user, TLObject participant, TLRPC.TL_chatAdminRights ar, TLRPC.TL_chatBannedRights br, String rankFinal, boolean canEdit, DialogInterface dialog, int which) {
        openRightsEdit(user.id, participant, ar, br, rankFinal, canEdit, this.selectType == 1 ? 0 : 1, false);
    }

    public /* synthetic */ void lambda$null$1$ChatUsersActivity(DialogInterface dialog1, int which) {
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info, true, false);
        finishFragment();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
        r0 = r3.listView.getAdapter();
        r2 = r3.listViewAdapter;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ boolean lambda$createView$3$ChatUsersActivity(android.view.View r4, int r5) {
        /*
            r3 = this;
            androidx.fragment.app.FragmentActivity r0 = r3.getParentActivity()
            r1 = 0
            if (r0 == 0) goto L_0x001c
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r3.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            im.bclpbkiauv.ui.ChatUsersActivity$ListAdapter r2 = r3.listViewAdapter
            if (r0 != r2) goto L_0x001c
            im.bclpbkiauv.tgnet.TLObject r0 = r2.getItem(r5)
            boolean r0 = r3.createMenuForParticipant(r0, r1)
            if (r0 == 0) goto L_0x001c
            r1 = 1
        L_0x001c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatUsersActivity.lambda$createView$3$ChatUsersActivity(android.view.View, int):boolean");
    }

    /* access modifiers changed from: private */
    public void onOwnerChaged(TLRPC.User user) {
        TLRPC.User user2;
        ArrayList<TLObject> arrayList;
        SparseArray<TLObject> map;
        boolean foundAny;
        TLRPC.User user3 = user;
        this.undoView.showWithAction((long) (-this.chatId), this.isChannel ? 9 : 10, (Object) user3);
        boolean foundAny2 = false;
        this.currentChat.creator = false;
        int a = 0;
        while (a < 3) {
            boolean found = false;
            if (a == 0) {
                map = this.contactsMap;
                arrayList = this.contacts;
            } else if (a == 1) {
                map = this.botsMap;
                arrayList = this.bots;
            } else {
                map = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject object = map.get(user3.id);
            if (object instanceof TLRPC.ChannelParticipant) {
                TLRPC.TL_channelParticipantCreator creator = new TLRPC.TL_channelParticipantCreator();
                creator.user_id = user3.id;
                map.put(user3.id, creator);
                int index = arrayList.indexOf(object);
                if (index >= 0) {
                    arrayList.set(index, creator);
                }
                found = true;
                foundAny2 = true;
            }
            int selfUserId = getUserConfig().getClientUserId();
            TLObject object2 = map.get(selfUserId);
            if (object2 instanceof TLRPC.ChannelParticipant) {
                TLRPC.TL_channelParticipantAdmin admin = new TLRPC.TL_channelParticipantAdmin();
                admin.user_id = selfUserId;
                admin.self = true;
                admin.inviter_id = selfUserId;
                admin.promoted_by = selfUserId;
                admin.date = (int) (System.currentTimeMillis() / 1000);
                admin.admin_rights = new TLRPC.TL_chatAdminRights();
                TLRPC.TL_chatAdminRights tL_chatAdminRights = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights2 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights3 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights4 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights5 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights6 = admin.admin_rights;
                foundAny = foundAny2;
                TLRPC.TL_chatAdminRights tL_chatAdminRights7 = admin.admin_rights;
                boolean z = found;
                admin.admin_rights.add_admins = true;
                tL_chatAdminRights7.pin_messages = true;
                tL_chatAdminRights6.invite_users = true;
                tL_chatAdminRights5.ban_users = true;
                tL_chatAdminRights4.delete_messages = true;
                tL_chatAdminRights3.edit_messages = true;
                tL_chatAdminRights2.post_messages = true;
                tL_chatAdminRights.change_info = true;
                map.put(selfUserId, admin);
                int index2 = arrayList.indexOf(object2);
                if (index2 >= 0) {
                    arrayList.set(index2, admin);
                }
                found = true;
            } else {
                foundAny = foundAny2;
                boolean z2 = found;
            }
            if (found) {
                Collections.sort(arrayList, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return ChatUsersActivity.this.lambda$onOwnerChaged$4$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
                    }
                });
            }
            a++;
            user3 = user;
            foundAny2 = foundAny;
        }
        if (!foundAny2) {
            TLRPC.TL_channelParticipantCreator creator2 = new TLRPC.TL_channelParticipantCreator();
            user2 = user;
            creator2.user_id = user2.id;
            this.participantsMap.put(user2.id, creator2);
            this.participants.add(creator2);
            Collections.sort(this.participants, new Comparator() {
                public final int compare(Object obj, Object obj2) {
                    return ChatUsersActivity.this.lambda$onOwnerChaged$5$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
                }
            });
            updateRows();
        } else {
            user2 = user;
        }
        this.listViewAdapter.notifyDataSetChanged();
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didChangeOwner(user2);
        }
    }

    public /* synthetic */ int lambda$onOwnerChaged$4$ChatUsersActivity(TLObject lhs, TLObject rhs) {
        int type1 = getChannelAdminParticipantType(lhs);
        int type2 = getChannelAdminParticipantType(rhs);
        if (type1 > type2) {
            return 1;
        }
        if (type1 < type2) {
            return -1;
        }
        return 0;
    }

    public /* synthetic */ int lambda$onOwnerChaged$5$ChatUsersActivity(TLObject lhs, TLObject rhs) {
        int type1 = getChannelAdminParticipantType(lhs);
        int type2 = getChannelAdminParticipantType(rhs);
        if (type1 > type2) {
            return 1;
        }
        if (type1 < type2) {
            return -1;
        }
        return 0;
    }

    private void openRightsEdit2(final int userId, final int date, TLObject participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean canEditAdmin, int type2, boolean removeFragment) {
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(userId, this.chatId, adminRights, this.defaultBannedRights, bannedRights, rank, type2, true, false);
        int i = userId;
        int i2 = date;
        final int i3 = type2;
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                TLRPC.ChatParticipant newParticipant;
                TLRPC.ChannelParticipant newPart;
                int i = i3;
                if (i == 0) {
                    for (int a = 0; a < ChatUsersActivity.this.participants.size(); a++) {
                        TLObject p = (TLObject) ChatUsersActivity.this.participants.get(a);
                        if (p instanceof TLRPC.ChannelParticipant) {
                            if (((TLRPC.ChannelParticipant) p).user_id == userId) {
                                if (rights == 1) {
                                    newPart = new TLRPC.TL_channelParticipantAdmin();
                                } else {
                                    newPart = new TLRPC.TL_channelParticipant();
                                }
                                newPart.admin_rights = rightsAdmin;
                                newPart.banned_rights = rightsBanned;
                                newPart.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                                newPart.user_id = userId;
                                newPart.date = date;
                                newPart.flags |= 4;
                                newPart.rank = rank;
                                ChatUsersActivity.this.participants.set(a, newPart);
                                return;
                            }
                        } else if (p instanceof TLRPC.ChatParticipant) {
                            TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) p;
                            if (rights == 1) {
                                newParticipant = new TLRPC.TL_chatParticipantAdmin();
                            } else {
                                newParticipant = new TLRPC.TL_chatParticipant();
                            }
                            newParticipant.user_id = chatParticipant.user_id;
                            newParticipant.date = chatParticipant.date;
                            newParticipant.inviter_id = chatParticipant.inviter_id;
                            int index = ChatUsersActivity.this.info.participants.participants.indexOf(chatParticipant);
                            if (index >= 0) {
                                ChatUsersActivity.this.info.participants.participants.set(index, newParticipant);
                            }
                            ChatUsersActivity.this.loadChatParticipants(0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                        }
                    }
                } else if (i == 1 && rights == 0) {
                    ChatUsersActivity.this.removeParticipants(userId);
                }
            }

            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }
        });
        presentFragment(fragment);
    }

    /* access modifiers changed from: private */
    public void openRightsEdit(int user_id, TLObject participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean canEditAdmin, int type2, boolean removeFragment) {
        final TLObject tLObject = participant;
        final boolean z = removeFragment;
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user_id, this.chatId, adminRights, this.defaultBannedRights, bannedRights, rank, type2, canEditAdmin, tLObject == null);
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                TLObject tLObject = tLObject;
                if (tLObject instanceof TLRPC.ChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                    channelParticipant.admin_rights = rightsAdmin;
                    channelParticipant.banned_rights = rightsBanned;
                    channelParticipant.rank = rank;
                }
                if (z) {
                    ChatUsersActivity.this.removeSelfFromStack();
                }
            }

            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }
        });
        presentFragment(fragment, z);
    }

    private void removeUser(int userId) {
        if (ChatObject.isChannel(this.currentChat)) {
            getMessagesController().deleteUserFromChat(this.chatId, getMessagesController().getUser(Integer.valueOf(userId)), (TLRPC.ChatFull) null);
            finishFragment();
        }
    }

    private TLObject getAnyParticipant(int userId) {
        SparseArray<TLObject> map;
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                map = this.contactsMap;
            } else if (a == 1) {
                map = this.botsMap;
            } else {
                map = this.participantsMap;
            }
            TLObject p = map.get(userId);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    private void removeParticipants(TLObject object) {
        if (object instanceof TLRPC.ChatParticipant) {
            removeParticipants(((TLRPC.ChatParticipant) object).user_id);
        } else if (object instanceof TLRPC.ChannelParticipant) {
            removeParticipants(((TLRPC.ChannelParticipant) object).user_id);
        }
    }

    /* access modifiers changed from: private */
    public void removeParticipants(int userId) {
        ArrayList<TLObject> arrayList;
        SparseArray<TLObject> map;
        boolean updated = false;
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                map = this.contactsMap;
                arrayList = this.contacts;
            } else if (a == 1) {
                map = this.botsMap;
                arrayList = this.bots;
            } else {
                map = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject p = map.get(userId);
            if (p != null) {
                map.remove(userId);
                arrayList.remove(p);
                updated = true;
            }
        }
        if (updated) {
            updateRows();
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void updateParticipantWithRights(TLRPC.ChannelParticipant channelParticipant, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, int user_id, boolean withDelegate) {
        SparseArray<TLObject> map;
        ChatUsersActivityDelegate chatUsersActivityDelegate;
        boolean delegateCalled = false;
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                map = this.contactsMap;
            } else if (a == 1) {
                map = this.botsMap;
            } else {
                map = this.participantsMap;
            }
            TLObject p = map.get(channelParticipant.user_id);
            if (p instanceof TLRPC.ChannelParticipant) {
                channelParticipant = (TLRPC.ChannelParticipant) p;
                channelParticipant.admin_rights = rightsAdmin;
                channelParticipant.banned_rights = rightsBanned;
                if (withDelegate) {
                    channelParticipant.promoted_by = getUserConfig().getClientUserId();
                }
            }
            if (withDelegate && p != null && !delegateCalled && (chatUsersActivityDelegate = this.delegate) != null) {
                delegateCalled = true;
                chatUsersActivityDelegate.didAddParticipantToList(user_id, p);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x02ad, code lost:
        if (r11.add_admins != false) goto L_0x02d6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean createMenuForParticipant(im.bclpbkiauv.tgnet.TLObject r32, boolean r33) {
        /*
            r31 = this;
            r11 = r31
            r12 = r32
            r0 = 0
            if (r12 == 0) goto L_0x031e
            int r1 = r11.selectType
            if (r1 == 0) goto L_0x000f
            r8 = r11
            r10 = r12
            goto L_0x0320
        L_0x000f:
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.ChannelParticipant
            if (r1 == 0) goto L_0x002b
            r1 = r12
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r1 = (im.bclpbkiauv.tgnet.TLRPC.ChannelParticipant) r1
            int r2 = r1.user_id
            boolean r3 = r1.can_edit
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r4 = r1.banned_rights
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r5 = r1.admin_rights
            int r6 = r1.date
            java.lang.String r1 = r1.rank
            r13 = r1
            r14 = r2
            r15 = r3
            r16 = r4
            r10 = r5
            r17 = r6
            goto L_0x0057
        L_0x002b:
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.ChatParticipant
            if (r1 == 0) goto L_0x0049
            r1 = r12
            im.bclpbkiauv.tgnet.TLRPC$ChatParticipant r1 = (im.bclpbkiauv.tgnet.TLRPC.ChatParticipant) r1
            int r2 = r1.user_id
            int r6 = r1.date
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r11.currentChat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.canAddAdmins(r3)
            r4 = 0
            r5 = 0
            java.lang.String r1 = ""
            r13 = r1
            r14 = r2
            r15 = r3
            r16 = r4
            r10 = r5
            r17 = r6
            goto L_0x0057
        L_0x0049:
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r1 = 0
            r13 = r1
            r14 = r2
            r15 = r3
            r16 = r4
            r10 = r5
            r17 = r6
        L_0x0057:
            if (r14 == 0) goto L_0x0316
            im.bclpbkiauv.messenger.UserConfig r1 = r31.getUserConfig()
            int r1 = r1.getClientUserId()
            if (r14 != r1) goto L_0x006c
            r8 = r11
            r26 = r14
            r25 = r15
            r11 = r10
            r10 = r12
            goto L_0x031d
        L_0x006c:
            int r1 = r11.type
            java.lang.String r18 = "dialogRedIcon"
            java.lang.String r19 = "dialogTextRed2"
            r2 = 2131691011(0x7f0f0603, float:1.9011082E38)
            java.lang.String r3 = "EditAdminRights"
            r4 = 2
            r9 = 1
            if (r1 != r4) goto L_0x020f
            im.bclpbkiauv.messenger.MessagesController r1 = r31.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r14)
            im.bclpbkiauv.tgnet.TLRPC$User r20 = r1.getUser(r5)
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r11.currentChat
            boolean r1 = r1.creator
            if (r1 != 0) goto L_0x00a6
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r11.currentChat
            boolean r1 = im.bclpbkiauv.messenger.ChatObject.canAddAdmins(r1)
            if (r1 == 0) goto L_0x00a4
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantCreator
            if (r1 != 0) goto L_0x00a4
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_chatParticipantCreator
            if (r1 != 0) goto L_0x00a4
            if (r10 == 0) goto L_0x00a6
            boolean r1 = r10.add_admins
            if (r1 != 0) goto L_0x00a4
            goto L_0x00a6
        L_0x00a4:
            r1 = 0
            goto L_0x00a7
        L_0x00a6:
            r1 = 1
        L_0x00a7:
            r21 = r1
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r11.currentChat
            boolean r1 = im.bclpbkiauv.messenger.ChatObject.canBlockUsers(r1)
            if (r1 == 0) goto L_0x00c1
            if (r21 != 0) goto L_0x00bf
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipant
            if (r1 != 0) goto L_0x00bf
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantBanned
            if (r1 != 0) goto L_0x00bf
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_chatParticipant
            if (r1 == 0) goto L_0x00c1
        L_0x00bf:
            r1 = 1
            goto L_0x00c2
        L_0x00c1:
            r1 = 0
        L_0x00c2:
            r22 = r1
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantAdmin
            if (r1 != 0) goto L_0x00cf
            boolean r1 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_chatParticipantAdmin
            if (r1 == 0) goto L_0x00cd
            goto L_0x00cf
        L_0x00cd:
            r1 = 0
            goto L_0x00d0
        L_0x00cf:
            r1 = 1
        L_0x00d0:
            r23 = r1
            if (r33 != 0) goto L_0x00e6
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r8 = r1
            r7 = r5
            goto L_0x00eb
        L_0x00e6:
            r1 = 0
            r5 = 0
            r6 = 0
            r8 = r1
            r7 = r5
        L_0x00eb:
            if (r21 == 0) goto L_0x0114
            if (r33 == 0) goto L_0x00f0
            return r9
        L_0x00f0:
            if (r23 == 0) goto L_0x00f7
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0100
        L_0x00f7:
            r1 = 2131693880(0x7f0f1138, float:1.90169E38)
            java.lang.String r2 = "SetAsAdmin"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
        L_0x0100:
            r8.add(r1)
            r1 = 2131230815(0x7f08005f, float:1.8077693E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)
            r7.add(r1)
        L_0x0114:
            r1 = 0
            if (r22 == 0) goto L_0x017f
            if (r33 == 0) goto L_0x011a
            return r9
        L_0x011a:
            boolean r2 = r11.isChannel
            r3 = 2131230818(0x7f080062, float:1.80777E38)
            if (r2 != 0) goto L_0x0161
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r11.currentChat
            boolean r2 = im.bclpbkiauv.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x0146
            r2 = 2131690382(0x7f0f038e, float:1.9009806E38)
            java.lang.String r5 = "ChangePermissions"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r2)
            r8.add(r2)
            r2 = 2131230817(0x7f080061, float:1.8077697E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r6.add(r2)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r9)
            r7.add(r2)
        L_0x0146:
            r2 = 2131691750(0x7f0f08e6, float:1.901258E38)
            java.lang.String r5 = "KickFromGroup"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r2)
            r8.add(r2)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            r6.add(r2)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            r7.add(r2)
            goto L_0x017b
        L_0x0161:
            r2 = 2131690473(0x7f0f03e9, float:1.900999E38)
            java.lang.String r5 = "ChannelRemoveUser"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r2)
            r8.add(r2)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            r6.add(r2)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            r7.add(r2)
        L_0x017b:
            r1 = 1
            r24 = r1
            goto L_0x0181
        L_0x017f:
            r24 = r1
        L_0x0181:
            if (r7 == 0) goto L_0x0201
            boolean r1 = r7.isEmpty()
            if (r1 == 0) goto L_0x0198
            r27 = r6
            r28 = r7
            r29 = r8
            r30 = r10
            r8 = r11
            r26 = r14
            r25 = r15
            goto L_0x020e
        L_0x0198:
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r0 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder
            androidx.fragment.app.FragmentActivity r1 = r31.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r5 = r0
            int r0 = r7.size()
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r0]
            java.lang.Object[] r0 = r8.toArray(r0)
            r4 = r0
            java.lang.CharSequence[] r4 = (java.lang.CharSequence[]) r4
            int[] r3 = im.bclpbkiauv.messenger.AndroidUtilities.toIntArray(r6)
            im.bclpbkiauv.ui.-$$Lambda$ChatUsersActivity$kK7Ik_dgn0D4QiXzqB8lOHZgF4Q r2 = new im.bclpbkiauv.ui.-$$Lambda$ChatUsersActivity$kK7Ik_dgn0D4QiXzqB8lOHZgF4Q
            r0 = r2
            r1 = r31
            r25 = r15
            r15 = r2
            r2 = r7
            r12 = r3
            r3 = r20
            r11 = r4
            r4 = r14
            r26 = r14
            r14 = r5
            r5 = r21
            r27 = r6
            r6 = r32
            r28 = r7
            r7 = r17
            r29 = r8
            r8 = r10
            r9 = r16
            r30 = r10
            r10 = r13
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r14.setItems(r11, r12, r15)
            im.bclpbkiauv.ui.actionbar.AlertDialog r0 = r14.create()
            r8 = r31
            r8.showDialog(r0)
            if (r24 == 0) goto L_0x01f9
            int r1 = r29.size()
            r9 = 1
            int r1 = r1 - r9
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r19)
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r18)
            r0.setItemColor(r1, r2, r3)
            goto L_0x01fa
        L_0x01f9:
            r9 = 1
        L_0x01fa:
            r10 = r32
            r11 = r30
            r2 = 1
            goto L_0x0315
        L_0x0201:
            r27 = r6
            r28 = r7
            r29 = r8
            r30 = r10
            r8 = r11
            r26 = r14
            r25 = r15
        L_0x020e:
            return r0
        L_0x020f:
            r30 = r10
            r8 = r11
            r26 = r14
            r25 = r15
            r5 = 3
            r6 = 2131690420(0x7f0f03b4, float:1.9009883E38)
            java.lang.String r7 = "ChannelDeleteFromList"
            if (r1 != r5) goto L_0x0249
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r8.currentChat
            boolean r1 = im.bclpbkiauv.messenger.ChatObject.canBlockUsers(r1)
            if (r1 == 0) goto L_0x0249
            if (r33 == 0) goto L_0x0229
            return r9
        L_0x0229:
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r4]
            r2 = 2131690426(0x7f0f03ba, float:1.9009895E38)
            java.lang.String r3 = "ChannelEditPermissions"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r1[r0] = r2
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r1[r9] = r2
            int[] r2 = new int[r4]
            r2 = {2131230817, 2131230931} // fill-array
            r10 = r32
            r12 = r1
            r14 = r2
            r11 = r30
            goto L_0x02da
        L_0x0249:
            int r1 = r8.type
            if (r1 != 0) goto L_0x028b
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r8.currentChat
            boolean r1 = im.bclpbkiauv.messenger.ChatObject.canBlockUsers(r1)
            if (r1 == 0) goto L_0x028b
            if (r33 == 0) goto L_0x0258
            return r9
        L_0x0258:
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r4]
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r8.currentChat
            boolean r2 = im.bclpbkiauv.messenger.ChatObject.canAddUsers(r2)
            if (r2 == 0) goto L_0x0276
            boolean r2 = r8.isChannel
            if (r2 == 0) goto L_0x026c
            r2 = 2131690395(0x7f0f039b, float:1.9009832E38)
            java.lang.String r3 = "ChannelAddToChannel"
            goto L_0x0271
        L_0x026c:
            r2 = 2131690396(0x7f0f039c, float:1.9009834E38)
            java.lang.String r3 = "ChannelAddToGroup"
        L_0x0271:
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0277
        L_0x0276:
            r2 = 0
        L_0x0277:
            r1[r0] = r2
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r1[r9] = r2
            int[] r2 = new int[r4]
            r2 = {2131230816, 2131230931} // fill-array
            r10 = r32
            r12 = r1
            r14 = r2
            r11 = r30
            goto L_0x02da
        L_0x028b:
            int r1 = r8.type
            if (r1 != r9) goto L_0x02d2
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r8.currentChat
            boolean r1 = r1.creator
            if (r1 != 0) goto L_0x02b0
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r8.currentChat
            boolean r1 = im.bclpbkiauv.messenger.ChatObject.canAddAdmins(r1)
            if (r1 == 0) goto L_0x02d2
            r10 = r32
            boolean r1 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantCreator
            if (r1 != 0) goto L_0x02d4
            boolean r1 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_chatParticipantCreator
            if (r1 != 0) goto L_0x02d4
            r11 = r30
            if (r11 == 0) goto L_0x02b4
            boolean r1 = r11.add_admins
            if (r1 != 0) goto L_0x02d6
            goto L_0x02b4
        L_0x02b0:
            r10 = r32
            r11 = r30
        L_0x02b4:
            if (r33 == 0) goto L_0x02b7
            return r9
        L_0x02b7:
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r4]
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r1[r0] = r2
            r2 = 2131690474(0x7f0f03ea, float:1.9009993E38)
            java.lang.String r3 = "ChannelRemoveUserAdmin"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r1[r9] = r2
            int[] r2 = new int[r4]
            r2 = {2131230815, 2131230818} // fill-array
            r12 = r1
            r14 = r2
            goto L_0x02da
        L_0x02d2:
            r10 = r32
        L_0x02d4:
            r11 = r30
        L_0x02d6:
            r1 = 0
            r2 = 0
            r12 = r1
            r14 = r2
        L_0x02da:
            if (r12 != 0) goto L_0x02dd
            return r0
        L_0x02dd:
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r0 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder
            androidx.fragment.app.FragmentActivity r1 = r31.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r15 = r0
            im.bclpbkiauv.ui.-$$Lambda$ChatUsersActivity$iO1V5Wwcjdm9f63xtttK0Cg1lAk r7 = new im.bclpbkiauv.ui.-$$Lambda$ChatUsersActivity$iO1V5Wwcjdm9f63xtttK0Cg1lAk
            r0 = r7
            r1 = r31
            r2 = r12
            r3 = r26
            r4 = r11
            r5 = r13
            r6 = r32
            r9 = r7
            r7 = r16
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r15.setItems(r12, r14, r9)
            im.bclpbkiauv.ui.actionbar.AlertDialog r0 = r15.create()
            r8.showDialog(r0)
            int r1 = r8.type
            r2 = 1
            if (r1 != r2) goto L_0x0315
            int r1 = r12.length
            int r1 = r1 - r2
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r19)
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r18)
            r0.setItemColor(r1, r3, r4)
        L_0x0315:
            return r2
        L_0x0316:
            r8 = r11
            r26 = r14
            r25 = r15
            r11 = r10
            r10 = r12
        L_0x031d:
            return r0
        L_0x031e:
            r8 = r11
            r10 = r12
        L_0x0320:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatUsersActivity.createMenuForParticipant(im.bclpbkiauv.tgnet.TLObject, boolean):boolean");
    }

    public /* synthetic */ void lambda$createMenuForParticipant$7$ChatUsersActivity(ArrayList actions, TLRPC.User user, int userId, boolean canEditAdmin, TLObject participant, int date, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, DialogInterface dialogInterface, int i) {
        ArrayList arrayList = actions;
        TLRPC.User user2 = user;
        TLObject tLObject = participant;
        int i2 = i;
        if (((Integer) arrayList.get(i2)).intValue() == 2) {
            getMessagesController().deleteUserFromChat(this.chatId, user2, (TLRPC.ChatFull) null);
            removeParticipants(userId);
            if (this.searchItem == null || !this.actionBar.isSearchFieldVisible()) {
                ArrayList arrayList2 = arrayList;
                int i3 = i2;
                return;
            }
            this.actionBar.closeSearchField();
            ArrayList arrayList3 = arrayList;
            int i4 = i2;
            return;
        }
        int i5 = userId;
        if (((Integer) arrayList.get(i2)).intValue() != 1 || !canEditAdmin || (!(tLObject instanceof TLRPC.TL_channelParticipantAdmin) && !(tLObject instanceof TLRPC.TL_chatParticipantAdmin))) {
            openRightsEdit2(userId, date, participant, adminRights, bannedRights, rank, canEditAdmin, ((Integer) actions.get(i)).intValue(), false);
            return;
        }
        AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
        builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder2.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(user2.first_name, user2.last_name)));
        $$Lambda$ChatUsersActivity$K1nHhcrlwaplcAhPADRSXwuxv8 r13 = r0;
        String string = LocaleController.getString("OK", R.string.OK);
        AlertDialog.Builder builder22 = builder2;
        $$Lambda$ChatUsersActivity$K1nHhcrlwaplcAhPADRSXwuxv8 r0 = new DialogInterface.OnClickListener(userId, date, participant, adminRights, bannedRights, rank, canEditAdmin, actions, i) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ TLRPC.TL_chatAdminRights f$4;
            private final /* synthetic */ TLRPC.TL_chatBannedRights f$5;
            private final /* synthetic */ String f$6;
            private final /* synthetic */ boolean f$7;
            private final /* synthetic */ ArrayList f$8;
            private final /* synthetic */ int f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
                this.f$9 = r10;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatUsersActivity.this.lambda$null$6$ChatUsersActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, dialogInterface, i);
            }
        };
        builder22.setPositiveButton(string, r13);
        builder22.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showDialog(builder22.create());
        ArrayList arrayList4 = actions;
        int i6 = i;
    }

    public /* synthetic */ void lambda$null$6$ChatUsersActivity(int userId, int date, TLObject participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean canEditAdmin, ArrayList actions, int i, DialogInterface dialog, int which) {
        openRightsEdit2(userId, date, participant, adminRights, bannedRights, rank, canEditAdmin, ((Integer) actions.get(i)).intValue(), false);
    }

    public /* synthetic */ void lambda$createMenuForParticipant$10$ChatUsersActivity(CharSequence[] items, int userId, TLRPC.TL_chatAdminRights adminRights, String rank, TLObject participant, TLRPC.TL_chatBannedRights bannedRights, DialogInterface dialogInterface, int i) {
        int i2;
        TLObject tLObject;
        int i3;
        int i4 = userId;
        final TLObject tLObject2 = participant;
        int i5 = i;
        int i6 = this.type;
        if (i6 == 1) {
            if (i5 != 0) {
                CharSequence[] charSequenceArr = items;
            } else if (items.length == 2) {
                ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(userId, this.chatId, adminRights, (TLRPC.TL_chatBannedRights) null, (TLRPC.TL_chatBannedRights) null, rank, 0, true, false);
                chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                    public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                        TLObject tLObject = tLObject2;
                        if (tLObject instanceof TLRPC.ChannelParticipant) {
                            TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                            channelParticipant.admin_rights = rightsAdmin;
                            channelParticipant.banned_rights = rightsBanned;
                            channelParticipant.rank = rank;
                            ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, rightsAdmin, rightsBanned, 0, false);
                        }
                    }

                    public void didChangeOwner(TLRPC.User user) {
                        ChatUsersActivity.this.onOwnerChaged(user);
                    }
                });
                presentFragment(chatRightsEditActivity);
                int i7 = i5;
                TLObject tLObject3 = tLObject2;
                int i8 = i4;
                return;
            }
            int i9 = i4;
            getMessagesController().setUserAdminRole(this.chatId, getMessagesController().getUser(Integer.valueOf(userId)), new TLRPC.TL_chatAdminRights(), "", !this.isChannel, this, false);
            removeParticipants(i9);
            TLObject tLObject4 = tLObject2;
            int i10 = i5;
            int i11 = i9;
            return;
        }
        int i12 = i5;
        TLObject tLObject5 = tLObject2;
        int i13 = i4;
        if (i6 == 0 || i6 == 3) {
            if (i12 == 0) {
                int i14 = this.type;
                if (i14 == 3) {
                    final TLObject tLObject6 = tLObject5;
                    ChatRightsEditActivity chatRightsEditActivity2 = new ChatRightsEditActivity(userId, this.chatId, (TLRPC.TL_chatAdminRights) null, this.defaultBannedRights, bannedRights, rank, 1, true, false);
                    chatRightsEditActivity2.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                        public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                            TLObject tLObject = tLObject6;
                            if (tLObject instanceof TLRPC.ChannelParticipant) {
                                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                                channelParticipant.admin_rights = rightsAdmin;
                                channelParticipant.banned_rights = rightsBanned;
                                channelParticipant.rank = rank;
                                ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, rightsAdmin, rightsBanned, 0, false);
                            }
                        }

                        public void didChangeOwner(TLRPC.User user) {
                            ChatUsersActivity.this.onOwnerChaged(user);
                        }
                    });
                    presentFragment(chatRightsEditActivity2);
                    i3 = i12;
                    tLObject = tLObject6;
                    int i15 = i13;
                    i2 = 1;
                } else {
                    int i16 = i13;
                    TLObject tLObject7 = tLObject5;
                    int i17 = i12;
                    if (i14 == 0) {
                        i2 = 1;
                        i3 = i17;
                        tLObject = tLObject7;
                        int i18 = i16;
                        getMessagesController().addUserToChat(this.chatId, getMessagesController().getUser(Integer.valueOf(userId)), (TLRPC.ChatFull) null, 0, (String) null, this, (Runnable) null);
                    } else {
                        i3 = i17;
                        tLObject = tLObject7;
                        int i19 = i16;
                        i2 = 1;
                    }
                }
            } else {
                tLObject = tLObject5;
                i3 = i12;
                int i20 = i13;
                i2 = 1;
                if (i3 == 1) {
                    TLRPC.TL_channels_editBanned req = new TLRPC.TL_channels_editBanned();
                    req.user_id = getMessagesController().getInputUser(i20);
                    req.channel = getMessagesController().getInputChannel(this.chatId);
                    req.banned_rights = new TLRPC.TL_chatBannedRights();
                    getConnectionsManager().sendRequest(req, new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            ChatUsersActivity.this.lambda$null$9$ChatUsersActivity(tLObject, tL_error);
                        }
                    });
                    if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                        this.actionBar.closeSearchField();
                    }
                }
            }
            if ((i3 == 0 && this.type == 0) || i3 == i2) {
                removeParticipants(tLObject);
            }
        } else if (i12 == 0) {
            getMessagesController().deleteUserFromChat(this.chatId, getMessagesController().getUser(Integer.valueOf(userId)), (TLRPC.ChatFull) null);
            TLObject tLObject8 = tLObject5;
            int i21 = i12;
            int i22 = i13;
        } else {
            TLObject tLObject9 = tLObject5;
            int i23 = i12;
            int i24 = i13;
        }
    }

    public /* synthetic */ void lambda$null$9$ChatUsersActivity(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            getMessagesController().processUpdates(updates, false);
            if (!updates.chats.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable(updates) {
                    private final /* synthetic */ TLRPC.Updates f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ChatUsersActivity.this.lambda$null$8$ChatUsersActivity(this.f$1);
                    }
                }, 1000);
            }
        }
    }

    public /* synthetic */ void lambda$null$8$ChatUsersActivity(TLRPC.Updates updates) {
        getMessagesController().loadFullChat(updates.chats.get(0).id, 0, true);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            boolean hadInfo = false;
            TLRPC.ChatFull chatFull = args[0];
            boolean byChannelUsers = args[2].booleanValue();
            if (chatFull.id != this.chatId) {
                return;
            }
            if (!byChannelUsers || !ChatObject.isChannel(this.currentChat)) {
                if (this.info != null) {
                    hadInfo = true;
                }
                this.info = chatFull;
                if (!hadInfo) {
                    int currentSlowmode = getCurrentSlowmode();
                    this.initialSlowmode = currentSlowmode;
                    this.selectedSlowmode = currentSlowmode;
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        ChatUsersActivity.this.lambda$didReceivedNotification$11$ChatUsersActivity();
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$11$ChatUsersActivity() {
        loadChatParticipants(0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    public void setDelegate(ChatUsersActivityDelegate chatUsersActivityDelegate) {
        this.delegate = chatUsersActivityDelegate;
    }

    private int getCurrentSlowmode() {
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return 0;
        }
        if (chatFull.slowmode_seconds == 10) {
            return 1;
        }
        if (this.info.slowmode_seconds == 30) {
            return 2;
        }
        if (this.info.slowmode_seconds == 60) {
            return 3;
        }
        if (this.info.slowmode_seconds == 300) {
            return 4;
        }
        if (this.info.slowmode_seconds == 900) {
            return 5;
        }
        if (this.info.slowmode_seconds == 3600) {
            return 6;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public int getSecondsForIndex(int index) {
        if (index == 1) {
            return 10;
        }
        if (index == 2) {
            return 30;
        }
        if (index == 3) {
            return 60;
        }
        if (index == 4) {
            return 300;
        }
        if (index == 5) {
            return 900;
        }
        if (index == 6) {
            return 3600;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights) && this.initialSlowmode == this.selectedSlowmode) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", R.string.ChannelSettingsChangedAlert));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", R.string.GroupSettingsChangedAlert));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatUsersActivity.this.lambda$checkDiscard$12$ChatUsersActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatUsersActivity.this.lambda$checkDiscard$13$ChatUsersActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$12$ChatUsersActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$13$ChatUsersActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    public boolean hasSelectType() {
        return this.selectType != 0;
    }

    /* access modifiers changed from: private */
    public String formatUserPermissions(TLRPC.TL_chatBannedRights rights) {
        if (rights == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (rights.view_messages && this.defaultBannedRights.view_messages != rights.view_messages) {
            builder.append(LocaleController.getString("UserRestrictionsNoRead", R.string.UserRestrictionsNoRead));
        }
        if (rights.send_messages && this.defaultBannedRights.send_messages != rights.send_messages) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSend", R.string.UserRestrictionsNoSend));
        }
        if (rights.send_media && this.defaultBannedRights.send_media != rights.send_media) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendMedia", R.string.UserRestrictionsNoSendMedia));
        }
        if (rights.send_stickers && this.defaultBannedRights.send_stickers != rights.send_stickers) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendStickers", R.string.UserRestrictionsNoSendStickers));
        }
        if (rights.send_polls && this.defaultBannedRights.send_polls != rights.send_polls) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendPolls", R.string.UserRestrictionsNoSendPolls));
        }
        if (rights.embed_links && this.defaultBannedRights.embed_links != rights.embed_links) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoEmbedLinks", R.string.UserRestrictionsNoEmbedLinks));
        }
        if (rights.invite_users && this.defaultBannedRights.invite_users != rights.invite_users) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoInviteUsers", R.string.UserRestrictionsNoInviteUsers));
        }
        if (rights.pin_messages && this.defaultBannedRights.pin_messages != rights.pin_messages) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoPinMessages", R.string.UserRestrictionsNoPinMessages));
        }
        if (rights.change_info && this.defaultBannedRights.change_info != rights.change_info) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoChangeInfo", R.string.UserRestrictionsNoChangeInfo));
        }
        if (builder.length() != 0) {
            builder.replace(0, 1, builder.substring(0, 1).toUpperCase());
            builder.append('.');
        }
        return builder.toString();
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (this.type == 3) {
            if (ChatObject.isChannel(this.currentChat) || this.selectedSlowmode == this.initialSlowmode || this.info == null) {
                if (!ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights)) {
                    getMessagesController().setDefaultBannedRole(this.chatId, this.defaultBannedRights, ChatObject.isChannel(this.currentChat), this);
                    TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(this.chatId));
                    if (chat != null) {
                        chat.default_banned_rights = this.defaultBannedRights;
                    }
                }
                if (!(this.selectedSlowmode == this.initialSlowmode || this.info == null)) {
                    getMessagesController().setChannelSlowMode(this.chatId, this.info.slowmode_seconds);
                }
                finishFragment();
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.IntCallback() {
                public final void run(int i) {
                    ChatUsersActivity.this.lambda$processDone$14$ChatUsersActivity(i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$processDone$14$ChatUsersActivity(int param) {
        this.chatId = param;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(param));
        processDone();
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            int currentSlowmode = getCurrentSlowmode();
            this.initialSlowmode = currentSlowmode;
            this.selectedSlowmode = currentSlowmode;
        }
    }

    /* access modifiers changed from: private */
    public int getChannelAdminParticipantType(TLObject participant) {
        if ((participant instanceof TLRPC.TL_channelParticipantCreator) || (participant instanceof TLRPC.TL_channelParticipantSelf)) {
            return 0;
        }
        if ((participant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_channelParticipant)) {
            return 1;
        }
        return 2;
    }

    /* access modifiers changed from: private */
    public void loadChatParticipants(int offset, int count) {
        if (!this.loadingUsers) {
            this.contactsEndReached = false;
            this.botsEndReached = false;
            loadChatParticipants(offset, count, true);
        }
    }

    private void loadChatParticipants(int offset, int count, boolean reset) {
        TLRPC.Chat chat;
        if (!ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = false;
            this.participants.clear();
            this.bots.clear();
            this.contacts.clear();
            this.participantsMap.clear();
            this.contactsMap.clear();
            this.botsMap.clear();
            int i = this.type;
            if (i == 1) {
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull != null) {
                    int size = chatFull.participants.participants.size();
                    for (int a = 0; a < size; a++) {
                        TLRPC.ChatParticipant participant = this.info.participants.participants.get(a);
                        if ((participant instanceof TLRPC.TL_chatParticipantCreator) || (participant instanceof TLRPC.TL_chatParticipantAdmin)) {
                            this.participants.add(participant);
                        }
                        this.participantsMap.put(participant.user_id, participant);
                    }
                }
            } else if (i == 2 && this.info != null) {
                int selfUserId = getUserConfig().clientUserId;
                int size2 = this.info.participants.participants.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    TLRPC.ChatParticipant participant2 = this.info.participants.participants.get(a2);
                    if (this.selectType == 0 || participant2.user_id != selfUserId) {
                        if (this.selectType == 1) {
                            if (getContactsController().isContact(participant2.user_id)) {
                                this.contacts.add(participant2);
                                this.contactsMap.put(participant2.user_id, participant2);
                            } else {
                                this.participants.add(participant2);
                                this.participantsMap.put(participant2.user_id, participant2);
                            }
                        } else if (getContactsController().isContact(participant2.user_id)) {
                            this.contacts.add(participant2);
                            this.contactsMap.put(participant2.user_id, participant2);
                        } else {
                            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(participant2.user_id));
                            if (user == null || !user.bot) {
                                this.participants.add(participant2);
                                this.participantsMap.put(participant2.user_id, participant2);
                            } else {
                                this.bots.add(participant2);
                                this.botsMap.put(participant2.user_id, participant2);
                            }
                        }
                    }
                }
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            updateRows();
            ListAdapter listAdapter2 = this.listViewAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.loadingUsers = true;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null && !this.firstLoaded) {
            emptyTextProgressView.showProgress();
        }
        ListAdapter listAdapter3 = this.listViewAdapter;
        if (listAdapter3 != null) {
            listAdapter3.notifyDataSetChanged();
        }
        TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = getMessagesController().getInputChannel(this.chatId);
        int i2 = this.type;
        if (i2 == 0) {
            req.filter = new TLRPC.TL_channelParticipantsKicked();
        } else if (i2 == 1) {
            req.filter = new TLRPC.TL_channelParticipantsAdmins();
        } else if (i2 == 2) {
            TLRPC.ChatFull chatFull2 = this.info;
            if (chatFull2 != null && chatFull2.participants_count <= 200 && (chat = this.currentChat) != null && chat.megagroup) {
                req.filter = new TLRPC.TL_channelParticipantsRecent();
            } else if (this.selectType == 1) {
                if (!this.contactsEndReached) {
                    this.delayResults = 2;
                    req.filter = new TLRPC.TL_channelParticipantsContacts();
                    this.contactsEndReached = true;
                    loadChatParticipants(0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, false);
                } else {
                    req.filter = new TLRPC.TL_channelParticipantsRecent();
                }
            } else if (!this.contactsEndReached) {
                this.delayResults = 3;
                req.filter = new TLRPC.TL_channelParticipantsContacts();
                this.contactsEndReached = true;
                loadChatParticipants(0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, false);
            } else if (!this.botsEndReached) {
                req.filter = new TLRPC.TL_channelParticipantsBots();
                this.botsEndReached = true;
                loadChatParticipants(0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, false);
            } else {
                req.filter = new TLRPC.TL_channelParticipantsRecent();
            }
        } else if (i2 == 3) {
            req.filter = new TLRPC.TL_channelParticipantsBanned();
        }
        req.filter.q = "";
        req.offset = offset;
        req.limit = count;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(req) {
            private final /* synthetic */ TLRPC.TL_channels_getParticipants f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatUsersActivity.this.lambda$loadChatParticipants$18$ChatUsersActivity(this.f$1, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadChatParticipants$18$ChatUsersActivity(TLRPC.TL_channels_getParticipants req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, req) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_channels_getParticipants f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatUsersActivity.this.lambda$null$17$ChatUsersActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$17$ChatUsersActivity(TLRPC.TL_error error, TLObject response, TLRPC.TL_channels_getParticipants req) {
        SparseArray<TLObject> map;
        ArrayList<TLObject> objects;
        EmptyTextProgressView emptyTextProgressView;
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            if (this.type == 1) {
                getMessagesController().processLoadedAdminsResponse(this.chatId, (TLRPC.TL_channels_channelParticipants) response);
            }
            getMessagesController().putUsers(res.users, false);
            int selfId = getUserConfig().getClientUserId();
            if (this.selectType != 0) {
                int a = 0;
                while (true) {
                    if (a >= res.participants.size()) {
                        break;
                    } else if (((TLRPC.ChannelParticipant) res.participants.get(a)).user_id == selfId) {
                        res.participants.remove(a);
                        break;
                    } else {
                        a++;
                    }
                }
            }
            if (this.type == 2) {
                this.delayResults--;
                if (req.filter instanceof TLRPC.TL_channelParticipantsContacts) {
                    objects = this.contacts;
                    map = this.contactsMap;
                } else if (req.filter instanceof TLRPC.TL_channelParticipantsBots) {
                    objects = this.bots;
                    map = this.botsMap;
                } else {
                    objects = this.participants;
                    map = this.participantsMap;
                }
                if (this.delayResults <= 0 && (emptyTextProgressView = this.emptyView) != null) {
                    emptyTextProgressView.showTextView();
                }
            } else {
                objects = this.participants;
                map = this.participantsMap;
                this.participantsMap.clear();
                EmptyTextProgressView emptyTextProgressView2 = this.emptyView;
                if (emptyTextProgressView2 != null) {
                    emptyTextProgressView2.showTextView();
                }
            }
            objects.clear();
            objects.addAll(res.participants);
            int size = res.participants.size();
            for (int a2 = 0; a2 < size; a2++) {
                TLRPC.ChannelParticipant participant = (TLRPC.ChannelParticipant) res.participants.get(a2);
                map.put(participant.user_id, participant);
            }
            if (this.type == 2) {
                int a3 = 0;
                int N = this.participants.size();
                while (a3 < N) {
                    TLRPC.ChannelParticipant participant2 = (TLRPC.ChannelParticipant) this.participants.get(a3);
                    if (this.contactsMap.get(participant2.user_id) != null || this.botsMap.get(participant2.user_id) != null) {
                        this.participants.remove(a3);
                        this.participantsMap.remove(participant2.user_id);
                        a3--;
                        N--;
                    }
                    a3++;
                }
            }
            try {
                if ((this.type == 0 || this.type == 3 || this.type == 2) && this.currentChat != null && this.currentChat.megagroup && (this.info instanceof TLRPC.TL_channelFull) && this.info.participants_count <= 200) {
                    Collections.sort(objects, new Comparator(getConnectionsManager().getCurrentTime()) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final int compare(Object obj, Object obj2) {
                            return ChatUsersActivity.this.lambda$null$15$ChatUsersActivity(this.f$1, (TLObject) obj, (TLObject) obj2);
                        }
                    });
                } else if (this.type == 1) {
                    Collections.sort(this.participants, new Comparator() {
                        public final int compare(Object obj, Object obj2) {
                            return ChatUsersActivity.this.lambda$null$16$ChatUsersActivity((TLObject) obj, (TLObject) obj2);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (this.type != 2 || this.delayResults <= 0) {
            this.loadingUsers = false;
            this.firstLoaded = true;
        }
        updateRows();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public /* synthetic */ int lambda$null$15$ChatUsersActivity(int currentTime, TLObject lhs, TLObject rhs) {
        TLRPC.User user1 = getMessagesController().getUser(Integer.valueOf(((TLRPC.ChannelParticipant) lhs).user_id));
        TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(((TLRPC.ChannelParticipant) rhs).user_id));
        int status1 = 0;
        int status2 = 0;
        if (!(user1 == null || user1.status == null)) {
            status1 = user1.self ? currentTime + 50000 : user1.status.expires;
        }
        if (!(user2 == null || user2.status == null)) {
            status2 = user2.self ? currentTime + 50000 : user2.status.expires;
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                    return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
                }
                return -1;
            } else if (status1 > status2) {
                return 1;
            } else {
                return status1 < status2 ? -1 : 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            return status1 < status2 ? -1 : 0;
        }
    }

    public /* synthetic */ int lambda$null$16$ChatUsersActivity(TLObject lhs, TLObject rhs) {
        int type1 = getChannelAdminParticipantType(lhs);
        int type2 = getChannelAdminParticipantType(rhs);
        if (type1 > type2) {
            return 1;
        }
        if (type1 < type2) {
            return -1;
        }
        return 0;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.needOpenSearch) {
            this.searchItem.openSearch(true);
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int contactsStartRow;
        private int globalStartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<TLObject> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        private int totalCount;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate(ChatUsersActivity.this) {
                public /* synthetic */ SparseArray<TLRPC.User> getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public void onDataSetChanged() {
                    SearchAdapter.this.notifyDataSetChanged();
                }

                public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                }
            });
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, ChatUsersActivity.this.type != 0, false, true, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$ChatUsersActivity$SearchAdapter$GfBczKe0THkF8OmHVwnqsLsnA r1 = new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$searchDialogs$0$ChatUsersActivity$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchDialogs$0$ChatUsersActivity$SearchAdapter(String query) {
            AndroidUtilities.runOnUIThread(new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$processSearch$2$ChatUsersActivity$SearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$2$ChatUsersActivity$SearchAdapter(String query) {
            ArrayList<TLRPC.Contact> contactsCopy = null;
            this.searchRunnable = null;
            ArrayList<TLRPC.ChatParticipant> participantsCopy = (ChatUsersActivity.this.isChannel || ChatUsersActivity.this.info == null) ? null : new ArrayList<>(ChatUsersActivity.this.info.participants.participants);
            if (ChatUsersActivity.this.selectType == 1) {
                contactsCopy = new ArrayList<>(ChatUsersActivity.this.getContactsController().contacts);
            }
            this.searchAdapterHelper.queryServerSearch(query, ChatUsersActivity.this.selectType != 0, false, true, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type);
            if (participantsCopy != null || contactsCopy != null) {
                Utilities.searchQueue.postRunnable(new Runnable(query, participantsCopy, contactsCopy) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ ArrayList f$2;
                    private final /* synthetic */ ArrayList f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        ChatUsersActivity.SearchAdapter.this.lambda$null$1$ChatUsersActivity$SearchAdapter(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        }

        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x011d A[LOOP:1: B:28:0x00af->B:49:0x011d, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:93:0x00de A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$1$ChatUsersActivity$SearchAdapter(java.lang.String r21, java.util.ArrayList r22, java.util.ArrayList r23) {
            /*
                r20 = this;
                r0 = r20
                r1 = r22
                r2 = r23
                java.lang.String r3 = r21.trim()
                java.lang.String r3 = r3.toLowerCase()
                int r4 = r3.length()
                if (r4 != 0) goto L_0x0027
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                java.util.ArrayList r5 = new java.util.ArrayList
                r5.<init>()
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                r0.updateSearchResults(r4, r5, r6)
                return
            L_0x0027:
                im.bclpbkiauv.messenger.LocaleController r4 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                java.lang.String r4 = r4.getTranslitString(r3)
                boolean r5 = r3.equals(r4)
                if (r5 != 0) goto L_0x003b
                int r5 = r4.length()
                if (r5 != 0) goto L_0x003c
            L_0x003b:
                r4 = 0
            L_0x003c:
                r5 = 0
                r6 = 1
                if (r4 == 0) goto L_0x0042
                r7 = 1
                goto L_0x0043
            L_0x0042:
                r7 = 0
            L_0x0043:
                int r7 = r7 + r6
                java.lang.String[] r7 = new java.lang.String[r7]
                r7[r5] = r3
                if (r4 == 0) goto L_0x004c
                r7[r6] = r4
            L_0x004c:
                java.util.ArrayList r8 = new java.util.ArrayList
                r8.<init>()
                java.util.ArrayList r9 = new java.util.ArrayList
                r9.<init>()
                java.util.ArrayList r10 = new java.util.ArrayList
                r10.<init>()
                java.lang.String r12 = "@"
                if (r1 == 0) goto L_0x013d
                r13 = 0
            L_0x0060:
                int r14 = r22.size()
                if (r13 >= r14) goto L_0x0138
                java.lang.Object r14 = r1.get(r13)
                im.bclpbkiauv.tgnet.TLRPC$ChatParticipant r14 = (im.bclpbkiauv.tgnet.TLRPC.ChatParticipant) r14
                im.bclpbkiauv.ui.ChatUsersActivity r15 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.messenger.MessagesController r15 = r15.getMessagesController()
                int r5 = r14.user_id
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                im.bclpbkiauv.tgnet.TLRPC$User r5 = r15.getUser(r5)
                int r15 = r5.id
                im.bclpbkiauv.ui.ChatUsersActivity r11 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.messenger.UserConfig r11 = r11.getUserConfig()
                int r11 = r11.getClientUserId()
                if (r15 != r11) goto L_0x0090
                r17 = r3
                r18 = r4
                goto L_0x012c
            L_0x0090:
                java.lang.String r11 = r5.first_name
                java.lang.String r15 = r5.last_name
                java.lang.String r11 = im.bclpbkiauv.messenger.ContactsController.formatName(r11, r15)
                java.lang.String r11 = r11.toLowerCase()
                im.bclpbkiauv.messenger.LocaleController r15 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                java.lang.String r15 = r15.getTranslitString(r11)
                boolean r16 = r11.equals(r15)
                if (r16 == 0) goto L_0x00ab
                r15 = 0
            L_0x00ab:
                r16 = 0
                int r6 = r7.length
                r1 = 0
            L_0x00af:
                if (r1 >= r6) goto L_0x0128
                r17 = r3
                r3 = r7[r1]
                boolean r18 = r11.contains(r3)
                if (r18 != 0) goto L_0x00d9
                if (r15 == 0) goto L_0x00c6
                boolean r18 = r15.contains(r3)
                if (r18 == 0) goto L_0x00c6
                r18 = r4
                goto L_0x00db
            L_0x00c6:
                r18 = r4
                java.lang.String r4 = r5.username
                if (r4 == 0) goto L_0x00d6
                java.lang.String r4 = r5.username
                boolean r4 = r4.contains(r3)
                if (r4 == 0) goto L_0x00d6
                r4 = 2
                goto L_0x00dc
            L_0x00d6:
                r4 = r16
                goto L_0x00dc
            L_0x00d9:
                r18 = r4
            L_0x00db:
                r4 = 1
            L_0x00dc:
                if (r4 == 0) goto L_0x011d
                r1 = 1
                if (r4 != r1) goto L_0x00ef
                java.lang.String r1 = r5.first_name
                java.lang.String r6 = r5.last_name
                java.lang.CharSequence r1 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r1, r6, r3)
                r9.add(r1)
                r19 = r3
                goto L_0x0119
            L_0x00ef:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r12)
                java.lang.String r6 = r5.username
                r1.append(r6)
                java.lang.String r1 = r1.toString()
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r12)
                r6.append(r3)
                java.lang.String r6 = r6.toString()
                r19 = r3
                r3 = 0
                java.lang.CharSequence r1 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r1, r3, r6)
                r9.add(r1)
            L_0x0119:
                r10.add(r14)
                goto L_0x012c
            L_0x011d:
                r19 = r3
                int r1 = r1 + 1
                r16 = r4
                r3 = r17
                r4 = r18
                goto L_0x00af
            L_0x0128:
                r17 = r3
                r18 = r4
            L_0x012c:
                int r13 = r13 + 1
                r1 = r22
                r3 = r17
                r4 = r18
                r5 = 0
                r6 = 1
                goto L_0x0060
            L_0x0138:
                r17 = r3
                r18 = r4
                goto L_0x0141
            L_0x013d:
                r17 = r3
                r18 = r4
            L_0x0141:
                if (r2 == 0) goto L_0x01fe
                r1 = 0
            L_0x0144:
                int r3 = r23.size()
                if (r1 >= r3) goto L_0x01fe
                java.lang.Object r3 = r2.get(r1)
                im.bclpbkiauv.tgnet.TLRPC$Contact r3 = (im.bclpbkiauv.tgnet.TLRPC.Contact) r3
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.messenger.MessagesController r4 = r4.getMessagesController()
                int r5 = r3.user_id
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r5)
                int r5 = r4.id
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.messenger.UserConfig r6 = r6.getUserConfig()
                int r6 = r6.getClientUserId()
                if (r5 != r6) goto L_0x0171
                r2 = 0
                goto L_0x01f8
            L_0x0171:
                java.lang.String r5 = r4.first_name
                java.lang.String r6 = r4.last_name
                java.lang.String r5 = im.bclpbkiauv.messenger.ContactsController.formatName(r5, r6)
                java.lang.String r5 = r5.toLowerCase()
                im.bclpbkiauv.messenger.LocaleController r6 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                java.lang.String r6 = r6.getTranslitString(r5)
                boolean r11 = r5.equals(r6)
                if (r11 == 0) goto L_0x018c
                r6 = 0
            L_0x018c:
                r11 = 0
                int r13 = r7.length
                r14 = 0
            L_0x018f:
                if (r14 >= r13) goto L_0x01f7
                r15 = r7[r14]
                boolean r16 = r5.contains(r15)
                if (r16 != 0) goto L_0x01b1
                if (r6 == 0) goto L_0x01a2
                boolean r16 = r6.contains(r15)
                if (r16 == 0) goto L_0x01a2
                goto L_0x01b1
            L_0x01a2:
                java.lang.String r2 = r4.username
                if (r2 == 0) goto L_0x01b3
                java.lang.String r2 = r4.username
                boolean r2 = r2.contains(r15)
                if (r2 == 0) goto L_0x01b3
                r2 = 2
                r11 = r2
                goto L_0x01b3
            L_0x01b1:
                r2 = 1
                r11 = r2
            L_0x01b3:
                if (r11 == 0) goto L_0x01f1
                r2 = 1
                if (r11 != r2) goto L_0x01c5
                java.lang.String r13 = r4.first_name
                java.lang.String r14 = r4.last_name
                java.lang.CharSequence r13 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r13, r14, r15)
                r9.add(r13)
                r2 = 0
                goto L_0x01ed
            L_0x01c5:
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                r13.append(r12)
                java.lang.String r14 = r4.username
                r13.append(r14)
                java.lang.String r13 = r13.toString()
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                r14.append(r12)
                r14.append(r15)
                java.lang.String r14 = r14.toString()
                r2 = 0
                java.lang.CharSequence r13 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r13, r2, r14)
                r9.add(r13)
            L_0x01ed:
                r8.add(r4)
                goto L_0x01f8
            L_0x01f1:
                r2 = 0
                int r14 = r14 + 1
                r2 = r23
                goto L_0x018f
            L_0x01f7:
                r2 = 0
            L_0x01f8:
                int r1 = r1 + 1
                r2 = r23
                goto L_0x0144
            L_0x01fe:
                r0.updateSearchResults(r8, r9, r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatUsersActivity.SearchAdapter.lambda$null$1$ChatUsersActivity$SearchAdapter(java.lang.String, java.util.ArrayList, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> users, ArrayList<CharSequence> names, ArrayList<TLObject> participants) {
            AndroidUtilities.runOnUIThread(new Runnable(users, names, participants) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$updateSearchResults$3$ChatUsersActivity$SearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$3$ChatUsersActivity$SearchAdapter(ArrayList users, ArrayList names, ArrayList participants) {
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            if (!ChatUsersActivity.this.isChannel) {
                ArrayList<TLObject> search = this.searchAdapterHelper.getGroupSearch();
                search.clear();
                search.addAll(participants);
            }
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public int getItemCount() {
            int contactsCount = this.searchResult.size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int groupsCount = this.searchAdapterHelper.getGroupSearch().size();
            int count = 0;
            if (contactsCount != 0) {
                count = 0 + contactsCount + 1;
            }
            if (globalCount != 0) {
                count += globalCount + 1;
            }
            if (groupsCount != 0) {
                return count + groupsCount + 1;
            }
            return count;
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                this.groupStartRow = 0;
                this.totalCount += count + 1;
            } else {
                this.groupStartRow = -1;
            }
            int count2 = this.searchResult.size();
            if (count2 != 0) {
                int i = this.totalCount;
                this.contactsStartRow = i;
                this.totalCount = i + count2 + 1;
            } else {
                this.contactsStartRow = -1;
            }
            int count3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count3 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + count3 + 1;
            } else {
                this.globalStartRow = -1;
            }
            super.notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                if (count + 1 <= i) {
                    i -= count + 1;
                } else if (i == 0) {
                    return null;
                } else {
                    return this.searchAdapterHelper.getGroupSearch().get(i - 1);
                }
            }
            int count2 = this.searchResult.size();
            if (count2 != 0) {
                if (count2 + 1 <= i) {
                    i -= count2 + 1;
                } else if (i == 0) {
                    return null;
                } else {
                    return this.searchResult.get(i - 1);
                }
            }
            int count3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count3 == 0 || count3 + 1 <= i || i == 0) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(i - 1);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new GraySectionCell(this.mContext);
            } else {
                view = new ManageChatUserCell(this.mContext, 2, 2, ChatUsersActivity.this.selectType == 0);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((ManageChatUserCell) view).setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                        return ChatUsersActivity.SearchAdapter.this.lambda$onCreateViewHolder$4$ChatUsersActivity$SearchAdapter(manageChatUserCell, z);
                    }
                });
            }
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$4$ChatUsersActivity$SearchAdapter(ManageChatUserCell cell, boolean click) {
            if (cell == null || cell.getTag() == null || !(getItem(((Integer) cell.getTag()).intValue()) instanceof TLRPC.ChannelParticipant)) {
                return false;
            }
            return ChatUsersActivity.this.createMenuForParticipant((TLRPC.ChannelParticipant) getItem(((Integer) cell.getTag()).intValue()), !click);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:93:0x01de  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            /*
                r20 = this;
                r1 = r20
                r2 = r21
                r0 = r22
                int r3 = r21.getItemViewType()
                r4 = 1
                if (r3 == 0) goto L_0x008e
                if (r3 == r4) goto L_0x0011
                goto L_0x0215
            L_0x0011:
                android.view.View r3 = r2.itemView
                im.bclpbkiauv.ui.cells.GraySectionCell r3 = (im.bclpbkiauv.ui.cells.GraySectionCell) r3
                int r4 = r1.groupStartRow
                if (r0 != r4) goto L_0x006a
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != 0) goto L_0x002f
                r4 = 2131690409(0x7f0f03a9, float:1.900986E38)
                java.lang.String r5 = "ChannelBlockedUsers"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0215
            L_0x002f:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                r5 = 3
                if (r4 != r5) goto L_0x0046
                r4 = 2131690475(0x7f0f03eb, float:1.9009995E38)
                java.lang.String r5 = "ChannelRestrictedUsers"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0215
            L_0x0046:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x005c
                r4 = 2131690484(0x7f0f03f4, float:1.9010013E38)
                java.lang.String r5 = "ChannelSubscribers"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0215
            L_0x005c:
                r4 = 2131690438(0x7f0f03c6, float:1.900992E38)
                java.lang.String r5 = "ChannelMembers"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0215
            L_0x006a:
                int r4 = r1.globalStartRow
                if (r0 != r4) goto L_0x007c
                r4 = 2131691482(0x7f0f07da, float:1.9012037E38)
                java.lang.String r5 = "GlobalSearch"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0215
            L_0x007c:
                int r4 = r1.contactsStartRow
                if (r0 != r4) goto L_0x0215
                r4 = 2131690709(0x7f0f04d5, float:1.901047E38)
                java.lang.String r5 = "Contacts"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0215
            L_0x008e:
                im.bclpbkiauv.tgnet.TLObject r3 = r1.getItem(r0)
                boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.User
                if (r5 == 0) goto L_0x009a
                r5 = r3
                im.bclpbkiauv.tgnet.TLRPC$User r5 = (im.bclpbkiauv.tgnet.TLRPC.User) r5
                goto L_0x00c9
            L_0x009a:
                boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.ChannelParticipant
                if (r5 == 0) goto L_0x00b2
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.messenger.MessagesController r5 = r5.getMessagesController()
                r6 = r3
                im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r6 = (im.bclpbkiauv.tgnet.TLRPC.ChannelParticipant) r6
                int r6 = r6.user_id
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r6)
                goto L_0x00c9
            L_0x00b2:
                boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.ChatParticipant
                if (r5 == 0) goto L_0x0216
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.messenger.MessagesController r5 = r5.getMessagesController()
                r6 = r3
                im.bclpbkiauv.tgnet.TLRPC$ChatParticipant r6 = (im.bclpbkiauv.tgnet.TLRPC.ChatParticipant) r6
                int r6 = r6.user_id
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r6)
            L_0x00c9:
                if (r5 != 0) goto L_0x00cd
                goto L_0x0215
            L_0x00cd:
                java.lang.String r6 = r5.username
                r7 = 0
                r8 = 0
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r9 = r1.searchAdapterHelper
                java.util.ArrayList r9 = r9.getGroupSearch()
                int r9 = r9.size()
                r10 = 0
                r11 = 0
                if (r9 == 0) goto L_0x00ee
                int r12 = r9 + 1
                if (r12 <= r0) goto L_0x00eb
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r12 = r1.searchAdapterHelper
                java.lang.String r11 = r12.getLastFoundChannel()
                r10 = 1
                goto L_0x00ee
            L_0x00eb:
                int r12 = r9 + 1
                int r0 = r0 - r12
            L_0x00ee:
                java.lang.String r12 = "@"
                if (r10 != 0) goto L_0x0150
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r13 = r1.searchResult
                int r9 = r13.size()
                if (r9 == 0) goto L_0x0148
                int r13 = r9 + 1
                if (r13 <= r0) goto L_0x013d
                r10 = 1
                java.util.ArrayList<java.lang.CharSequence> r13 = r1.searchResultNames
                int r14 = r0 + -1
                java.lang.Object r13 = r13.get(r14)
                r8 = r13
                java.lang.CharSequence r8 = (java.lang.CharSequence) r8
                if (r8 == 0) goto L_0x0135
                boolean r13 = android.text.TextUtils.isEmpty(r6)
                if (r13 != 0) goto L_0x0135
                java.lang.String r13 = r8.toString()
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                r14.append(r12)
                r14.append(r6)
                java.lang.String r14 = r14.toString()
                boolean r13 = r13.startsWith(r14)
                if (r13 == 0) goto L_0x0135
                r7 = r8
                r8 = 0
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
                goto L_0x0157
            L_0x0135:
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
                goto L_0x0157
            L_0x013d:
                int r13 = r9 + 1
                int r0 = r0 - r13
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
                goto L_0x0157
            L_0x0148:
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
                goto L_0x0157
            L_0x0150:
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
            L_0x0157:
                java.lang.String r14 = "windowBackgroundWhiteBlueText4"
                r15 = -1
                if (r10 != 0) goto L_0x01da
                if (r6 == 0) goto L_0x01da
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r13 = r1.searchAdapterHelper
                java.util.ArrayList r13 = r13.getGlobalSearch()
                int r13 = r13.size()
                if (r13 == 0) goto L_0x01d6
                int r0 = r13 + 1
                if (r0 <= r7) goto L_0x01d3
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.lang.String r0 = r0.getLastFoundUsername()
                boolean r16 = r0.startsWith(r12)
                if (r16 == 0) goto L_0x0181
                java.lang.String r0 = r0.substring(r4)
                r4 = r0
                goto L_0x0182
            L_0x0181:
                r4 = r0
            L_0x0182:
                android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x01c8 }
                r0.<init>()     // Catch:{ Exception -> 0x01c8 }
                r0.append(r12)     // Catch:{ Exception -> 0x01c8 }
                r0.append(r6)     // Catch:{ Exception -> 0x01c8 }
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r6, r4)     // Catch:{ Exception -> 0x01c8 }
                r16 = r12
                if (r12 == r15) goto L_0x01c1
                int r12 = r4.length()     // Catch:{ Exception -> 0x01c8 }
                if (r16 != 0) goto L_0x01a0
                int r12 = r12 + 1
                r15 = r16
                goto L_0x01a4
            L_0x01a0:
                int r16 = r16 + 1
                r15 = r16
            L_0x01a4:
                android.text.style.ForegroundColorSpan r1 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x01c8 }
                r17 = r3
                int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)     // Catch:{ Exception -> 0x01bd }
                r1.<init>(r3)     // Catch:{ Exception -> 0x01bd }
                int r3 = r15 + r12
                r18 = r4
                r4 = 33
                r0.setSpan(r1, r15, r3, r4)     // Catch:{ Exception -> 0x01bb }
                r16 = r15
                goto L_0x01c5
            L_0x01bb:
                r0 = move-exception
                goto L_0x01cd
            L_0x01bd:
                r0 = move-exception
                r18 = r4
                goto L_0x01cd
            L_0x01c1:
                r17 = r3
                r18 = r4
            L_0x01c5:
                r8 = r0
                r0 = r13
                goto L_0x01dc
            L_0x01c8:
                r0 = move-exception
                r17 = r3
                r18 = r4
            L_0x01cd:
                r8 = r6
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
                r0 = r13
                goto L_0x01dc
            L_0x01d3:
                r17 = r3
                goto L_0x01d8
            L_0x01d6:
                r17 = r3
            L_0x01d8:
                r0 = r13
                goto L_0x01dc
            L_0x01da:
                r17 = r3
            L_0x01dc:
                if (r11 == 0) goto L_0x0205
                java.lang.String r1 = im.bclpbkiauv.messenger.UserObject.getName(r5)
                android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
                r3.<init>(r1)
                r9 = r3
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r1, r11)
                r4 = -1
                if (r3 == r4) goto L_0x0205
                r4 = r9
                android.text.SpannableStringBuilder r4 = (android.text.SpannableStringBuilder) r4
                android.text.style.ForegroundColorSpan r12 = new android.text.style.ForegroundColorSpan
                int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
                r12.<init>(r13)
                int r13 = r11.length()
                int r13 = r13 + r3
                r14 = 33
                r4.setSpan(r12, r3, r13, r14)
            L_0x0205:
                android.view.View r1 = r2.itemView
                im.bclpbkiauv.ui.cells.ManageChatUserCell r1 = (im.bclpbkiauv.ui.cells.ManageChatUserCell) r1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r7)
                r1.setTag(r3)
                r3 = 0
                r1.setData(r5, r9, r8, r3)
                r0 = r7
            L_0x0215:
                return
            L_0x0216:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatUsersActivity.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow) {
                return 1;
            }
            return 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 7) {
                return ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat);
            }
            if (type == 0) {
                TLObject object = ((ManageChatUserCell) holder.itemView).getCurrentObject();
                return !(object instanceof TLRPC.User) || !((TLRPC.User) object).self;
            } else if (type == 0 || type == 2 || type == 6) {
                return true;
            } else {
                return false;
            }
        }

        public int getItemCount() {
            if (!ChatUsersActivity.this.loadingUsers || ChatUsersActivity.this.firstLoaded) {
                return ChatUsersActivity.this.rowCount;
            }
            return 0;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: im.bclpbkiauv.ui.cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: im.bclpbkiauv.ui.cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: im.bclpbkiauv.ui.cells.ManageChatTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: im.bclpbkiauv.ui.cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: im.bclpbkiauv.ui.ChatUsersActivity$ListAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: im.bclpbkiauv.ui.cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: im.bclpbkiauv.ui.cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: im.bclpbkiauv.ui.cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: im.bclpbkiauv.ui.ChatUsersActivity$ChooseView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v16, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v18, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v19, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v20, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v23, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r1v1, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r17, int r18) {
            /*
                r16 = this;
                r0 = r16
                r1 = 1
                switch(r18) {
                    case 0: goto L_0x0141;
                    case 1: goto L_0x012f;
                    case 2: goto L_0x0127;
                    case 3: goto L_0x011f;
                    case 4: goto L_0x0042;
                    case 5: goto L_0x002c;
                    case 6: goto L_0x0023;
                    case 7: goto L_0x001a;
                    case 8: goto L_0x0011;
                    default: goto L_0x0006;
                }
            L_0x0006:
                im.bclpbkiauv.ui.ChatUsersActivity$ChooseView r1 = new im.bclpbkiauv.ui.ChatUsersActivity$ChooseView
                im.bclpbkiauv.ui.ChatUsersActivity r2 = im.bclpbkiauv.ui.ChatUsersActivity.this
                android.content.Context r3 = r0.mContext
                r1.<init>(r3)
                goto L_0x0187
            L_0x0011:
                im.bclpbkiauv.ui.cells.GraySectionCell r1 = new im.bclpbkiauv.ui.cells.GraySectionCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                goto L_0x0187
            L_0x001a:
                im.bclpbkiauv.ui.cells.TextCheckCell2 r1 = new im.bclpbkiauv.ui.cells.TextCheckCell2
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                goto L_0x0187
            L_0x0023:
                im.bclpbkiauv.ui.cells.TextSettingsCell r1 = new im.bclpbkiauv.ui.cells.TextSettingsCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                goto L_0x0187
            L_0x002c:
                im.bclpbkiauv.ui.cells.HeaderCell r1 = new im.bclpbkiauv.ui.cells.HeaderCell
                android.content.Context r3 = r0.mContext
                r4 = 0
                r5 = 21
                r6 = 11
                r7 = 0
                r2 = r1
                r2.<init>(r3, r4, r5, r6, r7)
                r2 = 43
                r1.setHeight(r2)
                r2 = r1
                goto L_0x0187
            L_0x0042:
                im.bclpbkiauv.ui.ChatUsersActivity$ListAdapter$1 r2 = new im.bclpbkiauv.ui.ChatUsersActivity$ListAdapter$1
                android.content.Context r3 = r0.mContext
                r2.<init>(r3)
                r3 = r2
                android.widget.FrameLayout r3 = (android.widget.FrameLayout) r3
                android.widget.LinearLayout r4 = new android.widget.LinearLayout
                android.content.Context r5 = r0.mContext
                r4.<init>(r5)
                r4.setOrientation(r1)
                r5 = -1073741824(0xffffffffc0000000, float:-2.0)
                r6 = -1073741824(0xffffffffc0000000, float:-2.0)
                r7 = 17
                r8 = 1101004800(0x41a00000, float:20.0)
                r9 = 0
                r10 = 1101004800(0x41a00000, float:20.0)
                r11 = 0
                android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
                r3.addView(r4, r5)
                android.widget.ImageView r5 = new android.widget.ImageView
                android.content.Context r6 = r0.mContext
                r5.<init>(r6)
                r6 = 2131231063(0x7f080157, float:1.8078196E38)
                r5.setImageResource(r6)
                android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
                r5.setScaleType(r6)
                android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
                java.lang.String r7 = "emptyListPlaceholder"
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
                r6.<init>(r8, r9)
                r5.setColorFilter(r6)
                r6 = -2
                android.widget.LinearLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r6, (int) r6, (int) r1)
                r4.addView(r5, r6)
                android.widget.TextView r6 = new android.widget.TextView
                android.content.Context r8 = r0.mContext
                r6.<init>(r8)
                r8 = 2131692194(0x7f0f0aa2, float:1.9013481E38)
                java.lang.String r9 = "NoBlockedUsers"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
                r6.setText(r8)
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r6.setTextColor(r8)
                r8 = 1098907648(0x41800000, float:16.0)
                r6.setTextSize(r1, r8)
                r6.setGravity(r1)
                java.lang.String r8 = "fonts/rmedium.ttf"
                android.graphics.Typeface r8 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r8)
                r6.setTypeface(r8)
                r9 = -2
                r10 = -2
                r11 = 1
                r12 = 0
                r13 = 10
                r14 = 0
                r15 = 0
                android.widget.LinearLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                r4.addView(r6, r8)
                android.widget.TextView r8 = new android.widget.TextView
                android.content.Context r9 = r0.mContext
                r8.<init>(r9)
                r6 = r8
                im.bclpbkiauv.ui.ChatUsersActivity r8 = im.bclpbkiauv.ui.ChatUsersActivity.this
                boolean r8 = r8.isChannel
                if (r8 == 0) goto L_0x00ea
                r8 = 2131692191(0x7f0f0a9f, float:1.9013475E38)
                java.lang.String r9 = "NoBlockedChannel2"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
                r6.setText(r8)
                goto L_0x00f6
            L_0x00ea:
                r8 = 2131692193(0x7f0f0aa1, float:1.901348E38)
                java.lang.String r9 = "NoBlockedGroup2"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
                r6.setText(r8)
            L_0x00f6:
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r6.setTextColor(r7)
                r7 = 1097859072(0x41700000, float:15.0)
                r6.setTextSize(r1, r7)
                r6.setGravity(r1)
                r8 = -2
                r9 = -2
                r10 = 1
                r11 = 0
                r12 = 10
                r13 = 0
                r14 = 0
                android.widget.LinearLayout$LayoutParams r1 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
                r4.addView(r6, r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r7 = -1
                r1.<init>((int) r7, (int) r7)
                r2.setLayoutParams(r1)
                r1 = r2
                goto L_0x0187
            L_0x011f:
                im.bclpbkiauv.ui.cells.ShadowSectionCell r1 = new im.bclpbkiauv.ui.cells.ShadowSectionCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                goto L_0x0187
            L_0x0127:
                im.bclpbkiauv.ui.cells.ManageChatTextCell r1 = new im.bclpbkiauv.ui.cells.ManageChatTextCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                goto L_0x0187
            L_0x012f:
                im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r1 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                java.lang.String r2 = "windowBackgroundGray"
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                r1.setBackgroundColor(r2)
                goto L_0x0187
            L_0x0141:
                im.bclpbkiauv.ui.cells.ManageChatUserCell r2 = new im.bclpbkiauv.ui.cells.ManageChatUserCell
                android.content.Context r3 = r0.mContext
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                r5 = 6
                r6 = 3
                if (r4 == 0) goto L_0x015a
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r6) goto L_0x0158
                goto L_0x015a
            L_0x0158:
                r4 = 6
                goto L_0x015b
            L_0x015a:
                r4 = 7
            L_0x015b:
                im.bclpbkiauv.ui.ChatUsersActivity r7 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r7 = r7.type
                if (r7 == 0) goto L_0x016d
                im.bclpbkiauv.ui.ChatUsersActivity r7 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r7 = r7.type
                if (r7 != r6) goto L_0x016c
                goto L_0x016d
            L_0x016c:
                r5 = 2
            L_0x016d:
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r6 = r6.selectType
                if (r6 != 0) goto L_0x0176
                goto L_0x0177
            L_0x0176:
                r1 = 0
            L_0x0177:
                r2.<init>(r3, r4, r5, r1)
                r1 = r2
                r2 = r1
                im.bclpbkiauv.ui.cells.ManageChatUserCell r2 = (im.bclpbkiauv.ui.cells.ManageChatUserCell) r2
                im.bclpbkiauv.ui.-$$Lambda$ChatUsersActivity$ListAdapter$OfWQgucgbJ2B6_CYAK8Bt0ugCvk r3 = new im.bclpbkiauv.ui.-$$Lambda$ChatUsersActivity$ListAdapter$OfWQgucgbJ2B6_CYAK8Bt0ugCvk
                r3.<init>()
                r2.setDelegate(r3)
            L_0x0187:
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r2 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r2.<init>(r1)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatUsersActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$0$ChatUsersActivity$ListAdapter(ManageChatUserCell cell, boolean click) {
            return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) cell.getTag()).intValue()), !click);
        }

        /* JADX WARNING: Removed duplicated region for block: B:251:0x068f  */
        /* JADX WARNING: Removed duplicated region for block: B:252:0x0691  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                int r3 = r19.getItemViewType()
                r4 = 3
                r5 = 2
                r6 = 0
                r7 = 0
                r8 = 1
                if (r3 == 0) goto L_0x05b3
                r9 = -1
                if (r3 == r8) goto L_0x044b
                if (r3 == r5) goto L_0x034b
                r5 = 5
                if (r3 == r5) goto L_0x02c8
                r5 = 6
                if (r3 == r5) goto L_0x0296
                r5 = 7
                if (r3 == r5) goto L_0x00b3
                r4 = 8
                if (r3 == r4) goto L_0x0025
                goto L_0x0751
            L_0x0025:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.GraySectionCell r3 = (im.bclpbkiauv.ui.cells.GraySectionCell) r3
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.membersHeaderRow
                if (r2 != r4) goto L_0x0063
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = im.bclpbkiauv.messenger.ChatObject.isChannel(r4)
                if (r4 == 0) goto L_0x0055
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = r4.megagroup
                if (r4 != 0) goto L_0x0055
                r4 = 2131690463(0x7f0f03df, float:1.900997E38)
                java.lang.String r5 = "ChannelOtherSubscribers"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x0055:
                r4 = 2131690461(0x7f0f03dd, float:1.9009966E38)
                java.lang.String r5 = "ChannelOtherMembers"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x0063:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.botHeaderRow
                if (r2 != r4) goto L_0x0079
                r4 = 2131690410(0x7f0f03aa, float:1.9009863E38)
                java.lang.String r5 = "ChannelBots"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x0079:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.contactsHeaderRow
                if (r2 != r4) goto L_0x0751
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = im.bclpbkiauv.messenger.ChatObject.isChannel(r4)
                if (r4 == 0) goto L_0x00a5
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = r4.megagroup
                if (r4 != 0) goto L_0x00a5
                r4 = 2131690416(0x7f0f03b0, float:1.9009875E38)
                java.lang.String r5 = "ChannelContacts"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x00a5:
                r4 = 2131691504(0x7f0f07f0, float:1.9012082E38)
                java.lang.String r5 = "GroupContacts"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x00b3:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.TextCheckCell2 r3 = (im.bclpbkiauv.ui.cells.TextCheckCell2) r3
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.changeInfoRow
                if (r2 != r5) goto L_0x00e8
                r5 = 2131694581(0x7f0f13f5, float:1.9018323E38)
                java.lang.String r6 = "UserRestrictionsChangeInfo"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r6 = r6.defaultBannedRights
                boolean r6 = r6.change_info
                if (r6 != 0) goto L_0x00e2
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r6 = r6.currentChat
                java.lang.String r6 = r6.username
                boolean r6 = android.text.TextUtils.isEmpty(r6)
                if (r6 == 0) goto L_0x00e2
                r6 = 1
                goto L_0x00e3
            L_0x00e2:
                r6 = 0
            L_0x00e3:
                r3.setTextAndCheck(r5, r6, r7)
                goto L_0x01ce
            L_0x00e8:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.addUsersRow
                if (r2 != r5) goto L_0x0107
                r5 = 2131694586(0x7f0f13fa, float:1.9018333E38)
                java.lang.String r6 = "UserRestrictionsInviteUsers"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r6 = r6.defaultBannedRights
                boolean r6 = r6.invite_users
                r6 = r6 ^ r8
                r3.setTextAndCheck(r5, r6, r8)
                goto L_0x01ce
            L_0x0107:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.pinMessagesRow
                if (r2 != r5) goto L_0x0138
                r5 = 2131694596(0x7f0f1404, float:1.9018353E38)
                java.lang.String r6 = "UserRestrictionsPinMessages"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r6 = r6.defaultBannedRights
                boolean r6 = r6.pin_messages
                if (r6 != 0) goto L_0x0132
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r6 = r6.currentChat
                java.lang.String r6 = r6.username
                boolean r6 = android.text.TextUtils.isEmpty(r6)
                if (r6 == 0) goto L_0x0132
                r6 = 1
                goto L_0x0133
            L_0x0132:
                r6 = 0
            L_0x0133:
                r3.setTextAndCheck(r5, r6, r8)
                goto L_0x01ce
            L_0x0138:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.sendMessagesRow
                if (r2 != r5) goto L_0x0157
                r5 = 2131694598(0x7f0f1406, float:1.9018357E38)
                java.lang.String r6 = "UserRestrictionsSend"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r6 = r6.defaultBannedRights
                boolean r6 = r6.send_messages
                r6 = r6 ^ r8
                r3.setTextAndCheck(r5, r6, r8)
                goto L_0x01ce
            L_0x0157:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.sendMediaRow
                if (r2 != r5) goto L_0x0175
                r5 = 2131694599(0x7f0f1407, float:1.901836E38)
                java.lang.String r6 = "UserRestrictionsSendMedia"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r6 = r6.defaultBannedRights
                boolean r6 = r6.send_media
                r6 = r6 ^ r8
                r3.setTextAndCheck(r5, r6, r8)
                goto L_0x01ce
            L_0x0175:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.sendStickersRow
                if (r2 != r5) goto L_0x0193
                r5 = 2131694601(0x7f0f1409, float:1.9018363E38)
                java.lang.String r6 = "UserRestrictionsSendStickers"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r6 = r6.defaultBannedRights
                boolean r6 = r6.send_stickers
                r6 = r6 ^ r8
                r3.setTextAndCheck(r5, r6, r8)
                goto L_0x01ce
            L_0x0193:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.embedLinksRow
                if (r2 != r5) goto L_0x01b1
                r5 = 2131694585(0x7f0f13f9, float:1.901833E38)
                java.lang.String r6 = "UserRestrictionsEmbedLinks"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r6 = r6.defaultBannedRights
                boolean r6 = r6.embed_links
                r6 = r6 ^ r8
                r3.setTextAndCheck(r5, r6, r8)
                goto L_0x01ce
            L_0x01b1:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.sendPollsRow
                if (r2 != r5) goto L_0x01ce
                r5 = 2131694600(0x7f0f1408, float:1.9018361E38)
                java.lang.String r6 = "UserRestrictionsSendPolls"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r6 = r6.defaultBannedRights
                boolean r6 = r6.send_polls
                r6 = r6 ^ r8
                r3.setTextAndCheck(r5, r6, r8)
            L_0x01ce:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.sendMediaRow
                if (r2 == r5) goto L_0x0204
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.sendStickersRow
                if (r2 == r5) goto L_0x0204
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.embedLinksRow
                if (r2 == r5) goto L_0x0204
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.sendPollsRow
                if (r2 != r5) goto L_0x01ef
                goto L_0x0204
            L_0x01ef:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.sendMessagesRow
                if (r2 != r5) goto L_0x021e
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.view_messages
                r5 = r5 ^ r8
                r3.setEnabled(r5)
                goto L_0x021e
            L_0x0204:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.send_messages
                if (r5 != 0) goto L_0x021a
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.view_messages
                if (r5 != 0) goto L_0x021a
                r5 = 1
                goto L_0x021b
            L_0x021a:
                r5 = 0
            L_0x021b:
                r3.setEnabled(r5)
            L_0x021e:
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r5.currentChat
                boolean r5 = im.bclpbkiauv.messenger.ChatObject.canBlockUsers(r5)
                if (r5 == 0) goto L_0x0291
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.addUsersRow
                if (r2 != r5) goto L_0x023e
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r5.currentChat
                boolean r4 = im.bclpbkiauv.messenger.ChatObject.canUserDoAdminAction(r5, r4)
                if (r4 == 0) goto L_0x0284
            L_0x023e:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.pinMessagesRow
                if (r2 != r4) goto L_0x0252
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = im.bclpbkiauv.messenger.ChatObject.canUserDoAdminAction(r4, r7)
                if (r4 == 0) goto L_0x0284
            L_0x0252:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.changeInfoRow
                if (r2 != r4) goto L_0x0266
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = im.bclpbkiauv.messenger.ChatObject.canUserDoAdminAction(r4, r8)
                if (r4 == 0) goto L_0x0284
            L_0x0266:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r4.currentChat
                java.lang.String r4 = r4.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x028c
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.pinMessagesRow
                if (r2 == r4) goto L_0x0284
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.changeInfoRow
                if (r2 != r4) goto L_0x028c
            L_0x0284:
                r4 = 2131231420(0x7f0802bc, float:1.807892E38)
                r3.setIcon(r4)
                goto L_0x0751
            L_0x028c:
                r3.setIcon(r7)
                goto L_0x0751
            L_0x0291:
                r3.setIcon(r7)
                goto L_0x0751
            L_0x0296:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.TextSettingsCell r3 = (im.bclpbkiauv.ui.cells.TextSettingsCell) r3
                r4 = 2131690407(0x7f0f03a7, float:1.9009857E38)
                java.lang.String r5 = "ChannelBlacklist"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                java.lang.Object[] r5 = new java.lang.Object[r8]
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r6 = r6.info
                if (r6 == 0) goto L_0x02b6
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r6 = r6.info
                int r6 = r6.kicked_count
                goto L_0x02b7
            L_0x02b6:
                r6 = 0
            L_0x02b7:
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r5[r7] = r6
                java.lang.String r6 = "%d"
                java.lang.String r5 = java.lang.String.format(r6, r5)
                r3.setTextAndValue(r4, r5, r7)
                goto L_0x0751
            L_0x02c8:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.HeaderCell r3 = (im.bclpbkiauv.ui.cells.HeaderCell) r3
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.restricted1SectionRow
                if (r2 != r4) goto L_0x031f
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != 0) goto L_0x0311
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r4 = r4.info
                if (r4 == 0) goto L_0x02ed
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r4 = r4.info
                int r4 = r4.kicked_count
                goto L_0x02f7
            L_0x02ed:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                java.util.ArrayList r4 = r4.participants
                int r4 = r4.size()
            L_0x02f7:
                if (r4 == 0) goto L_0x0303
                java.lang.String r5 = "RemovedUser"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r5, r4)
                r3.setText(r5)
                goto L_0x030f
            L_0x0303:
                r5 = 2131690409(0x7f0f03a9, float:1.900986E38)
                java.lang.String r6 = "ChannelBlockedUsers"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r3.setText(r5)
            L_0x030f:
                goto L_0x0751
            L_0x0311:
                r4 = 2131690475(0x7f0f03eb, float:1.9009995E38)
                java.lang.String r5 = "ChannelRestrictedUsers"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x031f:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.permissionsSectionRow
                if (r2 != r4) goto L_0x0335
                r4 = 2131690465(0x7f0f03e1, float:1.9009974E38)
                java.lang.String r5 = "ChannelPermissionsHeader"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x0335:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.slowmodeRow
                if (r2 != r4) goto L_0x0751
                r4 = 2131693986(0x7f0f11a2, float:1.9017116E38)
                java.lang.String r5 = "Slowmode"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x034b:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.ManageChatTextCell r3 = (im.bclpbkiauv.ui.cells.ManageChatTextCell) r3
                java.lang.String r10 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r11 = "windowBackgroundWhiteBlackText"
                r3.setColors(r10, r11)
                im.bclpbkiauv.ui.ChatUsersActivity r10 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r10 = r10.addNewRow
                if (r2 != r10) goto L_0x0419
                im.bclpbkiauv.ui.ChatUsersActivity r10 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r10 = r10.type
                r11 = 2131230816(0x7f080060, float:1.8077695E38)
                java.lang.String r12 = "windowBackgroundWhiteBlueButton"
                java.lang.String r13 = "windowBackgroundWhiteBlueIcon"
                if (r10 != r4) goto L_0x038b
                r3.setColors(r13, r12)
                r4 = 2131690392(0x7f0f0398, float:1.9009826E38)
                java.lang.String r5 = "ChannelAddException"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.participantsStartRow
                if (r5 == r9) goto L_0x0386
                r7 = 1
            L_0x0386:
                r3.setText(r4, r6, r11, r7)
                goto L_0x0751
            L_0x038b:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != 0) goto L_0x03a4
                r4 = 2131690408(0x7f0f03a8, float:1.9009859E38)
                java.lang.String r5 = "ChannelBlockUser"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r5 = 2131230819(0x7f080063, float:1.8077702E38)
                r3.setText(r4, r6, r5, r7)
                goto L_0x0751
            L_0x03a4:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r8) goto L_0x03c0
                r3.setColors(r13, r12)
                r4 = 2131690391(0x7f0f0397, float:1.9009824E38)
                java.lang.String r5 = "ChannelAddAdmin"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r5 = 2131230823(0x7f080067, float:1.807771E38)
                r3.setText(r4, r6, r5, r8)
                goto L_0x0751
            L_0x03c0:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r5) goto L_0x0751
                r3.setColors(r13, r12)
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x03f6
                r4 = 2131689703(0x7f0f00e7, float:1.9008429E38)
                java.lang.String r5 = "AddSubscriber"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.membersHeaderRow
                if (r5 != r9) goto L_0x03f1
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                java.util.ArrayList r5 = r5.participants
                boolean r5 = r5.isEmpty()
                if (r5 != 0) goto L_0x03f1
                r7 = 1
            L_0x03f1:
                r3.setText(r4, r6, r11, r7)
                goto L_0x0751
            L_0x03f6:
                r4 = 2131689685(0x7f0f00d5, float:1.9008392E38)
                java.lang.String r5 = "AddMember"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r5 = r5.membersHeaderRow
                if (r5 != r9) goto L_0x0414
                im.bclpbkiauv.ui.ChatUsersActivity r5 = im.bclpbkiauv.ui.ChatUsersActivity.this
                java.util.ArrayList r5 = r5.participants
                boolean r5 = r5.isEmpty()
                if (r5 != 0) goto L_0x0414
                r7 = 1
            L_0x0414:
                r3.setText(r4, r6, r11, r7)
                goto L_0x0751
            L_0x0419:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.recentActionsRow
                if (r2 != r4) goto L_0x0432
                r4 = 2131691161(0x7f0f0699, float:1.9011386E38)
                java.lang.String r5 = "EventLog"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r5 = 2131231066(0x7f08015a, float:1.8078203E38)
                r3.setText(r4, r6, r5, r7)
                goto L_0x0751
            L_0x0432:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.addNew2Row
                if (r2 != r4) goto L_0x0751
                r4 = 2131690429(0x7f0f03bd, float:1.9009901E38)
                java.lang.String r5 = "ChannelInviteViaLink"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r5 = 2131231474(0x7f0802f2, float:1.807903E38)
                r3.setText(r4, r6, r5, r8)
                goto L_0x0751
            L_0x044b:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r3 = (im.bclpbkiauv.ui.cells.TextInfoPrivacyCell) r3
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r6 = r6.participantsInfoRow
                if (r2 != r6) goto L_0x0519
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r6 = r6.type
                if (r6 == 0) goto L_0x04cf
                im.bclpbkiauv.ui.ChatUsersActivity r6 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r6 = r6.type
                if (r6 != r4) goto L_0x0468
                goto L_0x04cf
            L_0x0468:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                java.lang.String r6 = ""
                if (r4 != r8) goto L_0x04a3
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.addNewRow
                if (r4 == r9) goto L_0x049e
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x0490
                r4 = 2131690402(0x7f0f03a2, float:1.9009847E38)
                java.lang.String r5 = "ChannelAdminsInfo"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x0490:
                r4 = 2131691949(0x7f0f09ad, float:1.9012984E38)
                java.lang.String r5 = "MegaAdminsInfo"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x049e:
                r3.setText(r6)
                goto L_0x0751
            L_0x04a3:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r5) goto L_0x0751
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x04ca
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.selectType
                if (r4 == 0) goto L_0x04bc
                goto L_0x04ca
            L_0x04bc:
                r4 = 2131690439(0x7f0f03c7, float:1.9009922E38)
                java.lang.String r5 = "ChannelMembersInfo"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x04ca:
                r3.setText(r6)
                goto L_0x0751
            L_0x04cf:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = im.bclpbkiauv.messenger.ChatObject.canBlockUsers(r4)
                r5 = 2131692191(0x7f0f0a9f, float:1.9013475E38)
                java.lang.String r6 = "NoBlockedChannel2"
                r7 = 2131692193(0x7f0f0aa1, float:1.901348E38)
                java.lang.String r8 = "NoBlockedGroup2"
                if (r4 == 0) goto L_0x04ff
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x04f6
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r3.setText(r4)
                goto L_0x0751
            L_0x04f6:
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
                r3.setText(r4)
                goto L_0x0751
            L_0x04ff:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x0510
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r3.setText(r4)
                goto L_0x0751
            L_0x0510:
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
                r3.setText(r4)
                goto L_0x0751
            L_0x0519:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r4 = r4.slowmodeInfoRow
                if (r2 != r4) goto L_0x0751
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r4 = r4.info
                if (r4 == 0) goto L_0x05a5
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r4 = r4.info
                int r4 = r4.slowmode_seconds
                if (r4 != 0) goto L_0x0534
                goto L_0x05a5
            L_0x0534:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r4 = r4.info
                int r4 = r4.slowmode_seconds
                r5 = 2131693989(0x7f0f11a5, float:1.9017122E38)
                java.lang.String r6 = "SlowmodeInfoSelected"
                r9 = 60
                if (r4 >= r9) goto L_0x0560
                java.lang.Object[] r4 = new java.lang.Object[r8]
                im.bclpbkiauv.ui.ChatUsersActivity r8 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r8 = r8.info
                int r8 = r8.slowmode_seconds
                java.lang.String r9 = "Seconds"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r9, r8)
                r4[r7] = r8
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.formatString(r6, r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x0560:
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r4 = r4.info
                int r4 = r4.slowmode_seconds
                r10 = 3600(0xe10, float:5.045E-42)
                if (r4 >= r10) goto L_0x0588
                java.lang.Object[] r4 = new java.lang.Object[r8]
                im.bclpbkiauv.ui.ChatUsersActivity r8 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r8 = r8.info
                int r8 = r8.slowmode_seconds
                int r8 = r8 / r9
                java.lang.String r9 = "Minutes"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r9, r8)
                r4[r7] = r8
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.formatString(r6, r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x0588:
                java.lang.Object[] r4 = new java.lang.Object[r8]
                im.bclpbkiauv.ui.ChatUsersActivity r8 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.tgnet.TLRPC$ChatFull r8 = r8.info
                int r8 = r8.slowmode_seconds
                int r8 = r8 / r9
                int r8 = r8 / r9
                java.lang.String r9 = "Hours"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r9, r8)
                r4[r7] = r8
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.formatString(r6, r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x05a5:
                r4 = 2131693988(0x7f0f11a4, float:1.901712E38)
                java.lang.String r5 = "SlowmodeInfoOff"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0751
            L_0x05b3:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.ManageChatUserCell r3 = (im.bclpbkiauv.ui.cells.ManageChatUserCell) r3
                java.lang.Integer r9 = java.lang.Integer.valueOf(r20)
                r3.setTag(r9)
                im.bclpbkiauv.tgnet.TLObject r9 = r0.getItem(r2)
                im.bclpbkiauv.ui.ChatUsersActivity r10 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r10 = r10.participantsStartRow
                if (r2 < r10) goto L_0x05d9
                im.bclpbkiauv.ui.ChatUsersActivity r10 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r10 = r10.participantsEndRow
                if (r2 >= r10) goto L_0x05d9
                im.bclpbkiauv.ui.ChatUsersActivity r10 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r10 = r10.participantsEndRow
                goto L_0x05f6
            L_0x05d9:
                im.bclpbkiauv.ui.ChatUsersActivity r10 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r10 = r10.contactsStartRow
                if (r2 < r10) goto L_0x05f0
                im.bclpbkiauv.ui.ChatUsersActivity r10 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r10 = r10.contactsEndRow
                if (r2 >= r10) goto L_0x05f0
                im.bclpbkiauv.ui.ChatUsersActivity r10 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r10 = r10.contactsEndRow
                goto L_0x05f6
            L_0x05f0:
                im.bclpbkiauv.ui.ChatUsersActivity r10 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r10 = r10.botEndRow
            L_0x05f6:
                boolean r11 = r9 instanceof im.bclpbkiauv.tgnet.TLRPC.ChannelParticipant
                if (r11 == 0) goto L_0x060c
                r11 = r9
                im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r11 = (im.bclpbkiauv.tgnet.TLRPC.ChannelParticipant) r11
                int r12 = r11.user_id
                int r13 = r11.kicked_by
                int r14 = r11.promoted_by
                im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r15 = r11.banned_rights
                boolean r5 = r11 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantBanned
                boolean r7 = r11 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantCreator
                boolean r11 = r11 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantAdmin
                goto L_0x061c
            L_0x060c:
                r5 = r9
                im.bclpbkiauv.tgnet.TLRPC$ChatParticipant r5 = (im.bclpbkiauv.tgnet.TLRPC.ChatParticipant) r5
                int r12 = r5.user_id
                r13 = 0
                r14 = 0
                r15 = 0
                r7 = 0
                boolean r11 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_chatParticipantCreator
                boolean r8 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_chatParticipantAdmin
                r5 = r7
                r7 = r11
                r11 = r8
            L_0x061c:
                im.bclpbkiauv.ui.ChatUsersActivity r8 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.messenger.MessagesController r8 = r8.getMessagesController()
                java.lang.Integer r6 = java.lang.Integer.valueOf(r12)
                im.bclpbkiauv.tgnet.TLRPC$User r6 = r8.getUser(r6)
                if (r6 == 0) goto L_0x0715
                im.bclpbkiauv.ui.ChatUsersActivity r8 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r8 = r8.type
                if (r8 != r4) goto L_0x0649
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                java.lang.String r4 = r4.formatUserPermissions(r15)
                int r8 = r10 + -1
                if (r2 == r8) goto L_0x0640
                r8 = 1
                goto L_0x0641
            L_0x0640:
                r8 = 0
            L_0x0641:
                r1 = 0
                r3.setData(r6, r1, r4, r8)
                r17 = r5
                goto L_0x0717
            L_0x0649:
                im.bclpbkiauv.ui.ChatUsersActivity r1 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r1 = r1.type
                if (r1 != 0) goto L_0x0698
                r1 = 0
                if (r5 == 0) goto L_0x0685
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.messenger.MessagesController r4 = r4.getMessagesController()
                java.lang.Integer r8 = java.lang.Integer.valueOf(r13)
                im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r8)
                if (r4 == 0) goto L_0x0680
                r16 = r1
                r8 = 1
                java.lang.Object[] r1 = new java.lang.Object[r8]
                java.lang.String r8 = r4.first_name
                r17 = r5
                java.lang.String r5 = r4.last_name
                java.lang.String r5 = im.bclpbkiauv.messenger.ContactsController.formatName(r8, r5)
                r8 = 0
                r1[r8] = r5
                java.lang.String r5 = "UserRemovedBy"
                r8 = 2131694573(0x7f0f13ed, float:1.9018306E38)
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r8, r1)
                goto L_0x068b
            L_0x0680:
                r16 = r1
                r17 = r5
                goto L_0x0689
            L_0x0685:
                r16 = r1
                r17 = r5
            L_0x0689:
                r1 = r16
            L_0x068b:
                int r4 = r10 + -1
                if (r2 == r4) goto L_0x0691
                r4 = 1
                goto L_0x0692
            L_0x0691:
                r4 = 0
            L_0x0692:
                r5 = 0
                r3.setData(r6, r5, r1, r4)
                goto L_0x0717
            L_0x0698:
                r17 = r5
                im.bclpbkiauv.ui.ChatUsersActivity r1 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r1 = r1.type
                r4 = 1
                if (r1 != r4) goto L_0x06ff
                r1 = 0
                if (r7 == 0) goto L_0x06b0
                r4 = 2131690417(0x7f0f03b1, float:1.9009877E38)
                java.lang.String r5 = "ChannelCreator"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                goto L_0x06f3
            L_0x06b0:
                if (r11 == 0) goto L_0x06ef
                im.bclpbkiauv.ui.ChatUsersActivity r4 = im.bclpbkiauv.ui.ChatUsersActivity.this
                im.bclpbkiauv.messenger.MessagesController r4 = r4.getMessagesController()
                java.lang.Integer r5 = java.lang.Integer.valueOf(r14)
                im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r5)
                if (r4 == 0) goto L_0x06ec
                int r5 = r4.id
                int r8 = r6.id
                if (r5 != r8) goto L_0x06d2
                r5 = 2131690400(0x7f0f03a0, float:1.9009843E38)
                java.lang.String r8 = "ChannelAdministrator"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r5)
                goto L_0x06f3
            L_0x06d2:
                r8 = 1
                java.lang.Object[] r5 = new java.lang.Object[r8]
                java.lang.String r8 = r4.first_name
                r16 = r1
                java.lang.String r1 = r4.last_name
                java.lang.String r1 = im.bclpbkiauv.messenger.ContactsController.formatName(r8, r1)
                r8 = 0
                r5[r8] = r1
                java.lang.String r1 = "EditAdminPromotedBy"
                r8 = 2131691007(0x7f0f05ff, float:1.9011074E38)
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r1, r8, r5)
                goto L_0x06f3
            L_0x06ec:
                r16 = r1
                goto L_0x06f1
            L_0x06ef:
                r16 = r1
            L_0x06f1:
                r1 = r16
            L_0x06f3:
                int r4 = r10 + -1
                if (r2 == r4) goto L_0x06f9
                r8 = 1
                goto L_0x06fa
            L_0x06f9:
                r8 = 0
            L_0x06fa:
                r4 = 0
                r3.setData(r6, r4, r1, r8)
                goto L_0x0714
            L_0x06ff:
                im.bclpbkiauv.ui.ChatUsersActivity r1 = im.bclpbkiauv.ui.ChatUsersActivity.this
                int r1 = r1.type
                r4 = 2
                if (r1 != r4) goto L_0x0714
                int r1 = r10 + -1
                if (r2 == r1) goto L_0x070e
                r8 = 1
                goto L_0x070f
            L_0x070e:
                r8 = 0
            L_0x070f:
                r1 = 0
                r3.setData(r6, r1, r1, r8)
                goto L_0x0717
            L_0x0714:
                goto L_0x0717
            L_0x0715:
                r17 = r5
            L_0x0717:
                int r1 = r10 + -1
                if (r2 != r1) goto L_0x0751
                if (r2 == 0) goto L_0x0751
                android.content.Context r1 = r0.mContext
                android.content.res.Resources r1 = r1.getResources()
                r4 = 2131231534(0x7f08032e, float:1.8079152E38)
                android.graphics.drawable.Drawable r1 = r1.getDrawable(r4)
                android.graphics.drawable.LayerDrawable r1 = (android.graphics.drawable.LayerDrawable) r1
                r4 = 0
                android.graphics.drawable.Drawable r4 = r1.getDrawable(r4)
                android.graphics.drawable.GradientDrawable r4 = (android.graphics.drawable.GradientDrawable) r4
                java.lang.String r5 = "windowBackgroundGray"
                int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                r4.setColor(r5)
                r5 = 1
                android.graphics.drawable.Drawable r5 = r1.getDrawable(r5)
                android.graphics.drawable.GradientDrawable r5 = (android.graphics.drawable.GradientDrawable) r5
                java.lang.String r8 = "windowBackgroundWhite"
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                r5.setColor(r8)
                r3.setBackground(r1)
            L_0x0751:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatUsersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if (position == ChatUsersActivity.this.addNewRow || position == ChatUsersActivity.this.addNew2Row || position == ChatUsersActivity.this.recentActionsRow) {
                return 2;
            }
            if ((position >= ChatUsersActivity.this.participantsStartRow && position < ChatUsersActivity.this.participantsEndRow) || ((position >= ChatUsersActivity.this.botStartRow && position < ChatUsersActivity.this.botEndRow) || (position >= ChatUsersActivity.this.contactsStartRow && position < ChatUsersActivity.this.contactsEndRow))) {
                return 0;
            }
            if (position == ChatUsersActivity.this.addNewSectionRow || position == ChatUsersActivity.this.participantsDividerRow || position == ChatUsersActivity.this.participantsDivider2Row) {
                return 3;
            }
            if (position == ChatUsersActivity.this.restricted1SectionRow || position == ChatUsersActivity.this.permissionsSectionRow || position == ChatUsersActivity.this.slowmodeRow) {
                return 5;
            }
            if (position == ChatUsersActivity.this.participantsInfoRow || position == ChatUsersActivity.this.slowmodeInfoRow) {
                return 1;
            }
            if (position == ChatUsersActivity.this.blockedEmptyRow) {
                return 4;
            }
            if (position == ChatUsersActivity.this.removedUsersRow) {
                return 6;
            }
            if (position == ChatUsersActivity.this.changeInfoRow || position == ChatUsersActivity.this.addUsersRow || position == ChatUsersActivity.this.pinMessagesRow || position == ChatUsersActivity.this.sendMessagesRow || position == ChatUsersActivity.this.sendMediaRow || position == ChatUsersActivity.this.sendStickersRow || position == ChatUsersActivity.this.embedLinksRow || position == ChatUsersActivity.this.sendPollsRow) {
                return 7;
            }
            if (position == ChatUsersActivity.this.membersHeaderRow || position == ChatUsersActivity.this.contactsHeaderRow || position == ChatUsersActivity.this.botHeaderRow) {
                return 8;
            }
            if (position == ChatUsersActivity.this.slowmodeSelectRow) {
                return 9;
            }
            return 0;
        }

        public TLObject getItem(int position) {
            if (position >= ChatUsersActivity.this.participantsStartRow && position < ChatUsersActivity.this.participantsEndRow) {
                return (TLObject) ChatUsersActivity.this.participants.get(position - ChatUsersActivity.this.participantsStartRow);
            }
            if (position >= ChatUsersActivity.this.contactsStartRow && position < ChatUsersActivity.this.contactsEndRow) {
                return (TLObject) ChatUsersActivity.this.contacts.get(position - ChatUsersActivity.this.contactsStartRow);
            }
            if (position < ChatUsersActivity.this.botStartRow || position >= ChatUsersActivity.this.botEndRow) {
                return null;
            }
            return (TLObject) ChatUsersActivity.this.bots.get(position - ChatUsersActivity.this.botStartRow);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatUsersActivity.this.lambda$getThemeDescriptions$19$ChatUsersActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        UndoView undoView2 = this.undoView;
        UndoView undoView3 = undoView2;
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        RecyclerListView recyclerListView3 = this.listView;
        RecyclerListView recyclerListView4 = recyclerListView3;
        RecyclerListView recyclerListView5 = this.listView;
        RecyclerListView recyclerListView6 = recyclerListView5;
        RecyclerListView recyclerListView7 = this.listView;
        RecyclerListView recyclerListView8 = recyclerListView7;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class, ManageChatTextCell.class, TextCheckCell2.class, TextSettingsCell.class, ChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_graySectionText), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switch2Track), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switch2TrackChecked), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_background), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_cancelColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_cancelColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) undoView3, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) recyclerListView4, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) recyclerListView6, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueButton), new ThemeDescription((View) recyclerListView8, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueIcon)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$19$ChatUsersActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
