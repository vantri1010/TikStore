package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.StickerSetCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.components.URLSpanNoUnderline;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;
import java.util.List;

public class GroupStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private int chatId;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public boolean donePressed;
    private EditText editText;
    /* access modifiers changed from: private */
    public ImageView eraseImageView;
    /* access modifiers changed from: private */
    public int headerRow;
    /* access modifiers changed from: private */
    public boolean ignoreTextChanges;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public int infoRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public LinearLayout nameContainer;
    /* access modifiers changed from: private */
    public int nameRow;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    private Runnable queryRunnable;
    private int reqId;
    /* access modifiers changed from: private */
    public int rowCount;
    private boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public int selectedStickerRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_messages_stickerSet selectedStickerSet;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersShadowRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;
    /* access modifiers changed from: private */
    public EditTextBoldCursor usernameTextView;

    public GroupStickersActivity(int id) {
        this.chatId = id;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupStickers", R.string.GroupStickers));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupStickersActivity.this.finishFragment();
                } else if (id == 1 && !GroupStickersActivity.this.donePressed) {
                    boolean unused = GroupStickersActivity.this.donePressed = true;
                    if (GroupStickersActivity.this.searching) {
                        GroupStickersActivity.this.showEditDoneProgress(true);
                    } else {
                        GroupStickersActivity.this.saveStickerSet();
                    }
                }
            }
        });
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.progressView = contextProgressView;
        contextProgressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass2 r4 = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), 1073741824));
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (GroupStickersActivity.this.selectedStickerSet != null) {
                    canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
                }
            }
        };
        this.nameContainer = r4;
        r4.setWeightSum(1.0f);
        this.nameContainer.setWillNotDraw(false);
        this.nameContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.nameContainer.setOrientation(0);
        this.nameContainer.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(14.0f), 0);
        EditText editText2 = new EditText(context2);
        this.editText = editText2;
        editText2.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/");
        this.editText.setTextSize(1, 17.0f);
        this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setFocusable(false);
        this.editText.setBackgroundDrawable((Drawable) null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setGravity(16);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        this.nameContainer.addView(this.editText, LayoutHelper.createLinear(-2, 42));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.usernameTextView = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 17.0f);
        this.usernameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.usernameTextView.setCursorWidth(1.5f);
        this.usernameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.usernameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setMaxLines(1);
        this.usernameTextView.setLines(1);
        this.usernameTextView.setBackgroundDrawable((Drawable) null);
        this.usernameTextView.setPadding(0, 0, 0, 0);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setGravity(16);
        this.usernameTextView.setInputType(163872);
        this.usernameTextView.setImeOptions(6);
        this.usernameTextView.setHint(LocaleController.getString("ChooseStickerSetPlaceholder", R.string.ChooseStickerSetPlaceholder));
        this.usernameTextView.addTextChangedListener(new TextWatcher() {
            boolean ignoreTextChange;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (GroupStickersActivity.this.eraseImageView != null) {
                    GroupStickersActivity.this.eraseImageView.setVisibility(s.length() > 0 ? 0 : 4);
                }
                if (!this.ignoreTextChange && !GroupStickersActivity.this.ignoreTextChanges) {
                    if (s.length() > 5) {
                        this.ignoreTextChange = true;
                        try {
                            Uri uri = Uri.parse(s.toString());
                            if (uri != null) {
                                List<String> segments = uri.getPathSegments();
                                if (segments.size() == 2 && segments.get(0).toLowerCase().equals("addstickers")) {
                                    GroupStickersActivity.this.usernameTextView.setText(segments.get(1));
                                    GroupStickersActivity.this.usernameTextView.setSelection(GroupStickersActivity.this.usernameTextView.length());
                                }
                            }
                        } catch (Exception e) {
                        }
                        this.ignoreTextChange = false;
                    }
                    GroupStickersActivity.this.resolveStickerSet();
                }
            }
        });
        this.nameContainer.addView(this.usernameTextView, LayoutHelper.createLinear(0, 42, 1.0f));
        ImageView imageView = new ImageView(context2);
        this.eraseImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.eraseImageView.setImageResource(R.drawable.ic_close_white);
        this.eraseImageView.setPadding(AndroidUtilities.dp(16.0f), 0, 0, 0);
        this.eraseImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), PorterDuff.Mode.MULTIPLY));
        this.eraseImageView.setVisibility(4);
        this.eraseImageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupStickersActivity.this.lambda$createView$0$GroupStickersActivity(view);
            }
        });
        this.nameContainer.addView(this.eraseImageView, LayoutHelper.createLinear(42, 42, 0.0f));
        TLRPC.ChatFull chatFull = this.info;
        if (!(chatFull == null || chatFull.stickerset == null)) {
            this.ignoreTextChanges = true;
            this.usernameTextView.setText(this.info.stickerset.short_name);
            EditTextBoldCursor editTextBoldCursor2 = this.usernameTextView;
            editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
            this.ignoreTextChanges = false;
        }
        this.listAdapter = new ListAdapter(context2);
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setFocusable(true);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        AnonymousClass4 r5 = new LinearLayoutManager(context2) {
            public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
                return false;
            }

            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r5;
        r5.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                GroupStickersActivity.this.lambda$createView$1$GroupStickersActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$GroupStickersActivity(View v) {
        this.searchWas = false;
        this.selectedStickerSet = null;
        this.usernameTextView.setText("");
        updateRows();
    }

    public /* synthetic */ void lambda$createView$1$GroupStickersActivity(View view, int position) {
        if (getParentActivity() != null) {
            int i = this.selectedStickerRow;
            if (position == i) {
                if (this.selectedStickerSet != null) {
                    showDialog(new StickersAlert(getParentActivity(), this, (TLRPC.InputStickerSet) null, this.selectedStickerSet, (StickersAlert.StickersAlertDelegate) null));
                }
            } else if (position >= this.stickersStartRow && position < this.stickersEndRow) {
                boolean needScroll = i == -1;
                int row = this.layoutManager.findFirstVisibleItemPosition();
                int top = Integer.MAX_VALUE;
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(row);
                if (holder != null) {
                    top = holder.itemView.getTop();
                }
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSets(0).get(position - this.stickersStartRow);
                this.selectedStickerSet = tL_messages_stickerSet;
                this.ignoreTextChanges = true;
                this.usernameTextView.setText(tL_messages_stickerSet.set.short_name);
                EditTextBoldCursor editTextBoldCursor = this.usernameTextView;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
                this.ignoreTextChanges = false;
                AndroidUtilities.hideKeyboard(this.usernameTextView);
                updateRows();
                if (needScroll && top != Integer.MAX_VALUE) {
                    this.layoutManager.scrollToPositionWithOffset(row + 1, top);
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (args[0].intValue() == 0) {
                updateRows();
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && chatFull.stickerset != null) {
                    this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
                }
                this.info = chatFull;
                updateRows();
            }
        } else if (id == NotificationCenter.groupStickersDidLoad) {
            long longValue = args[0].longValue();
            TLRPC.ChatFull chatFull2 = this.info;
            if (chatFull2 != null && chatFull2.stickerset != null && this.info.stickerset.id == ((long) id)) {
                updateRows();
            }
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null && chatFull.stickerset != null) {
            this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
        }
    }

    /* access modifiers changed from: private */
    public void resolveStickerSet() {
        if (this.listAdapter != null) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            Runnable runnable = this.queryRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.queryRunnable = null;
            }
            this.selectedStickerSet = null;
            if (this.usernameTextView.length() <= 0) {
                this.searching = false;
                this.searchWas = false;
                if (this.selectedStickerRow != -1) {
                    updateRows();
                    return;
                }
                return;
            }
            this.searching = true;
            this.searchWas = true;
            String query = this.usernameTextView.getText().toString();
            TLRPC.TL_messages_stickerSet existingSet = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(query);
            if (existingSet != null) {
                this.selectedStickerSet = existingSet;
            }
            int i = this.selectedStickerRow;
            if (i == -1) {
                updateRows();
            } else {
                this.listAdapter.notifyItemChanged(i);
            }
            if (existingSet != null) {
                this.searching = false;
                return;
            }
            $$Lambda$GroupStickersActivity$kFt3HwTSXw0F7ADrkvjLTyRInfA r2 = new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    GroupStickersActivity.this.lambda$resolveStickerSet$4$GroupStickersActivity(this.f$1);
                }
            };
            this.queryRunnable = r2;
            AndroidUtilities.runOnUIThread(r2, 500);
        }
    }

    public /* synthetic */ void lambda$resolveStickerSet$4$GroupStickersActivity(String query) {
        if (this.queryRunnable != null) {
            TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
            req.stickerset = new TLRPC.TL_inputStickerSetShortName();
            req.stickerset.short_name = query;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    GroupStickersActivity.this.lambda$null$3$GroupStickersActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$3$GroupStickersActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupStickersActivity.this.lambda$null$2$GroupStickersActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$GroupStickersActivity(TLObject response) {
        this.searching = false;
        if (response instanceof TLRPC.TL_messages_stickerSet) {
            this.selectedStickerSet = (TLRPC.TL_messages_stickerSet) response;
            if (this.donePressed) {
                saveStickerSet();
            } else {
                int i = this.selectedStickerRow;
                if (i != -1) {
                    this.listAdapter.notifyItemChanged(i);
                } else {
                    updateRows();
                }
            }
        } else {
            int i2 = this.selectedStickerRow;
            if (i2 != -1) {
                this.listAdapter.notifyItemChanged(i2);
            }
            if (this.donePressed) {
                this.donePressed = false;
                showEditDoneProgress(false);
                if (getParentActivity() != null) {
                    ToastUtils.show((int) R.string.AddStickersNotFound);
                }
            }
        }
        this.reqId = 0;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    GroupStickersActivity.this.lambda$onTransitionAnimationEnd$5$GroupStickersActivity();
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$5$GroupStickersActivity() {
        EditTextBoldCursor editTextBoldCursor = this.usernameTextView;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    /* access modifiers changed from: private */
    public void saveStickerSet() {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null || (!(chatFull.stickerset == null || (tL_messages_stickerSet = this.selectedStickerSet) == null || tL_messages_stickerSet.set.id != this.info.stickerset.id) || (this.info.stickerset == null && this.selectedStickerSet == null))) {
            finishFragment();
            return;
        }
        showEditDoneProgress(true);
        TLRPC.TL_channels_setStickers req = new TLRPC.TL_channels_setStickers();
        req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        if (this.selectedStickerSet == null) {
            req.stickerset = new TLRPC.TL_inputStickerSetEmpty();
        } else {
            SharedPreferences.Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
            edit.remove("group_hide_stickers_" + this.info.id).commit();
            req.stickerset = new TLRPC.TL_inputStickerSetID();
            req.stickerset.id = this.selectedStickerSet.set.id;
            req.stickerset.access_hash = this.selectedStickerSet.set.access_hash;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupStickersActivity.this.lambda$saveStickerSet$7$GroupStickersActivity(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$saveStickerSet$7$GroupStickersActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupStickersActivity.this.lambda$null$6$GroupStickersActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$GroupStickersActivity(TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.selectedStickerSet;
            if (tL_messages_stickerSet == null) {
                this.info.stickerset = null;
            } else {
                this.info.stickerset = tL_messages_stickerSet.set;
                MediaDataController.getInstance(this.currentAccount).putGroupStickerSet(this.selectedStickerSet);
            }
            if (this.info.stickerset == null) {
                this.info.flags |= 256;
            } else {
                this.info.flags &= -257;
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(this.info, false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, true, null);
            finishFragment();
            return;
        }
        ToastUtils.show((int) R.string.ErrorOccurred);
        this.donePressed = false;
        showEditDoneProgress(false);
    }

    private void updateRows() {
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.nameRow = 0;
        if (this.selectedStickerSet != null || this.searchWas) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.selectedStickerRow = i;
        } else {
            this.selectedStickerRow = -1;
        }
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.infoRow = i2;
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
        if (!stickerSets.isEmpty()) {
            int i3 = this.rowCount;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.headerRow = i3;
            this.stickersStartRow = i4;
            this.stickersEndRow = i4 + stickerSets.size();
            int size = this.rowCount + stickerSets.size();
            this.rowCount = size;
            this.rowCount = size + 1;
            this.stickersShadowRow = size;
        } else {
            this.headerRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        }
        LinearLayout linearLayout = this.nameContainer;
        if (linearLayout != null) {
            linearLayout.invalidate();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.usernameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    /* access modifiers changed from: private */
    public void showEditDoneProgress(boolean show) {
        final boolean z = show;
        if (this.doneItem != null) {
            AnimatorSet animatorSet = this.doneItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            if (z) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f})});
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animation)) {
                        if (!z) {
                            GroupStickersActivity.this.progressView.setVisibility(4);
                        } else {
                            GroupStickersActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animation)) {
                        AnimatorSet unused = GroupStickersActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long id;
            int itemViewType = holder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                ArrayList<TLRPC.TL_messages_stickerSet> arrayList = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0);
                int row = position - GroupStickersActivity.this.stickersStartRow;
                StickerSetCell cell = (StickerSetCell) holder.itemView;
                TLRPC.TL_messages_stickerSet set = arrayList.get(row);
                cell.setStickersSet(arrayList.get(row), row != arrayList.size() - 1);
                if (GroupStickersActivity.this.selectedStickerSet != null) {
                    id = GroupStickersActivity.this.selectedStickerSet.set.id;
                } else if (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) {
                    id = 0;
                } else {
                    id = GroupStickersActivity.this.info.stickerset.id;
                }
                if (set.set.id != id) {
                    z = false;
                }
                cell.setChecked(z);
            } else if (itemViewType != 1) {
                if (itemViewType == 4) {
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString("ChooseFromYourStickers", R.string.ChooseFromYourStickers));
                } else if (itemViewType == 5) {
                    StickerSetCell cell2 = (StickerSetCell) holder.itemView;
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        cell2.setStickersSet(GroupStickersActivity.this.selectedStickerSet, false);
                    } else if (GroupStickersActivity.this.searching) {
                        cell2.setText(LocaleController.getString("Loading", R.string.Loading), (String) null, 0, false);
                    } else {
                        cell2.setText(LocaleController.getString("ChooseStickerSetNotFound", R.string.ChooseStickerSetNotFound), LocaleController.getString("ChooseStickerSetNotFoundInfo", R.string.ChooseStickerSetNotFoundInfo), R.drawable.ic_smiles2_sad, false);
                    }
                }
            } else if (position == GroupStickersActivity.this.infoRow) {
                String text = LocaleController.getString("ChooseStickerSetMy", R.string.ChooseStickerSetMy);
                int index = text.indexOf("@stickers");
                if (index != -1) {
                    try {
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                        stringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                            public void onClick(View widget) {
                                MessagesController.getInstance(GroupStickersActivity.this.currentAccount).openByUserName("stickers", GroupStickersActivity.this, 1);
                            }
                        }, index, "@stickers".length() + index, 18);
                        ((TextInfoPrivacyCell) holder.itemView).setText(stringBuilder);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        ((TextInfoPrivacyCell) holder.itemView).setText(text);
                    }
                } else {
                    ((TextInfoPrivacyCell) holder.itemView).setText(text);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2 || type == 5;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
            if (r9 != 5) goto L_0x006b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
            /*
                r7 = this;
                r0 = 0
                java.lang.String r1 = "windowBackgroundWhite"
                r2 = 3
                r3 = 2
                if (r9 == 0) goto L_0x0057
                r4 = 1
                java.lang.String r5 = "windowBackgroundGrayShadow"
                r6 = 2131231061(0x7f080155, float:1.8078192E38)
                if (r9 == r4) goto L_0x0045
                if (r9 == r3) goto L_0x003e
                if (r9 == r2) goto L_0x002c
                r4 = 4
                if (r9 == r4) goto L_0x001c
                r4 = 5
                if (r9 == r4) goto L_0x0057
                goto L_0x006b
            L_0x001c:
                im.bclpbkiauv.ui.cells.HeaderCell r2 = new im.bclpbkiauv.ui.cells.HeaderCell
                android.content.Context r3 = r7.mContext
                r2.<init>(r3)
                r0 = r2
                int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
                r0.setBackgroundColor(r1)
                goto L_0x006b
            L_0x002c:
                im.bclpbkiauv.ui.cells.ShadowSectionCell r1 = new im.bclpbkiauv.ui.cells.ShadowSectionCell
                android.content.Context r2 = r7.mContext
                r1.<init>(r2)
                r0 = r1
                android.content.Context r1 = r7.mContext
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r1, (int) r6, (java.lang.String) r5)
                r0.setBackgroundDrawable(r1)
                goto L_0x006b
            L_0x003e:
                im.bclpbkiauv.ui.GroupStickersActivity r1 = im.bclpbkiauv.ui.GroupStickersActivity.this
                android.widget.LinearLayout r0 = r1.nameContainer
                goto L_0x006b
            L_0x0045:
                im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r1 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
                android.content.Context r2 = r7.mContext
                r1.<init>(r2)
                r0 = r1
                android.content.Context r1 = r7.mContext
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r1, (int) r6, (java.lang.String) r5)
                r0.setBackgroundDrawable(r1)
                goto L_0x006b
            L_0x0057:
                im.bclpbkiauv.ui.cells.StickerSetCell r4 = new im.bclpbkiauv.ui.cells.StickerSetCell
                android.content.Context r5 = r7.mContext
                if (r9 != 0) goto L_0x005e
                goto L_0x005f
            L_0x005e:
                r2 = 2
            L_0x005f:
                r4.<init>(r5, r2)
                r0 = r4
                int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
                r0.setBackgroundColor(r1)
            L_0x006b:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -1
                r3 = -2
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r1 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.GroupStickersActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public int getItemViewType(int i) {
            if (i >= GroupStickersActivity.this.stickersStartRow && i < GroupStickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == GroupStickersActivity.this.infoRow) {
                return 1;
            }
            if (i == GroupStickersActivity.this.nameRow) {
                return 2;
            }
            if (i == GroupStickersActivity.this.stickersShadowRow) {
                return 3;
            }
            if (i == GroupStickersActivity.this.headerRow) {
                return 4;
            }
            if (i == GroupStickersActivity.this.selectedStickerRow) {
                return 5;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteLinkText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.nameContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_stickers_menuSelector), new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_stickers_menu)};
    }
}
