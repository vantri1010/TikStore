package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.utils.RegexUtils;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.ShareDialogCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ShareAlert;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ShareAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    private boolean copyLinkOnEnd;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private TLRPC.TL_exportedMessageLink exportedMessageLink;
    private FrameLayout frameLayout;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    private boolean isChannel;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private String linkToCopy;
    /* access modifiers changed from: private */
    public ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public TextView pickerBottomLayout;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public ShareSearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    private View selectedCountView;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.Dialog> selectedDialogs = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> sendingMessageObjects;
    private String sendingText;
    /* access modifiers changed from: private */
    public View[] shadow = new View[2];
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public int topBeforeSwitch;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;

    private class SearchField extends FrameLayout {
        private View backgroundView;
        /* access modifiers changed from: private */
        public ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        /* access modifiers changed from: private */
        public EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;

        public SearchField(Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(18.0f), Theme.getColor(Theme.key_dialogSearchBackground)));
            addView(this.searchBackground, LayoutHelper.createFrame(-1.0f, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogSearchIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36.0f, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogSearchIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36.0f, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ShareAlert.SearchField.this.lambda$new$0$ShareAlert$SearchField(view);
                }
            });
            AnonymousClass1 r0 = new EditTextBoldCursor(context, ShareAlert.this) {
                public boolean dispatchTouchEvent(MotionEvent event) {
                    MotionEvent e = MotionEvent.obtain(event);
                    e.setLocation(e.getRawX(), e.getRawY() - ShareAlert.this.containerView.getTranslationY());
                    ShareAlert.this.gridView.dispatchTouchEvent(e);
                    e.recycle();
                    return super.dispatchTouchEvent(event);
                }
            };
            this.searchEditText = r0;
            r0.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor(Theme.key_dialogSearchHint));
            this.searchEditText.setTextColor(Theme.getColor(Theme.key_dialogSearchText));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(268435459);
            this.searchEditText.setHint(LocaleController.getString("ShareSendTo", R.string.ShareSendTo));
            this.searchEditText.setCursorColor(Theme.getColor(Theme.key_featuredStickers_addedIcon));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1.0f, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(ShareAlert.this) {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    boolean showed = true;
                    boolean show = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                        showed = false;
                    }
                    if (show != showed) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (show) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(show ? 1.0f : 0.1f);
                        if (!show) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    String text = SearchField.this.searchEditText.getText().toString();
                    if (text.length() != 0) {
                        if (ShareAlert.this.searchEmptyView != null) {
                            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                        }
                    } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                        int top = ShareAlert.this.getCurrentTop();
                        ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", R.string.NoChats));
                        ShareAlert.this.searchEmptyView.showTextView();
                        ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
                        ShareAlert.this.listAdapter.notifyDataSetChanged();
                        if (top > 0) {
                            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -top);
                        }
                    }
                    if (ShareAlert.this.searchAdapter != null) {
                        ShareAlert.this.searchAdapter.searchDialogs(text);
                    }
                }
            });
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ShareAlert.SearchField.this.lambda$new$1$ShareAlert$SearchField(textView, i, keyEvent);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$ShareAlert$SearchField(View v) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public /* synthetic */ boolean lambda$new$1$ShareAlert$SearchField(TextView v, int actionId, KeyEvent event) {
            if (event == null) {
                return false;
            }
            if ((event.getAction() != 1 || event.getKeyCode() != 84) && (event.getAction() != 0 || event.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    public static ShareAlert createShareAlert(Context context, MessageObject messageObject, String text, boolean channel, String copyLink, boolean fullScreen) {
        ArrayList<MessageObject> arrayList;
        if (messageObject != null) {
            arrayList = new ArrayList<>();
            arrayList.add(messageObject);
        } else {
            arrayList = null;
        }
        return new ShareAlert(context, arrayList, text, channel, copyLink, fullScreen);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ShareAlert(android.content.Context r29, java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r30, java.lang.String r31, boolean r32, java.lang.String r33, boolean r34) {
        /*
            r28 = this;
            r0 = r28
            r1 = r29
            r2 = r30
            r3 = r32
            r4 = 1
            r0.<init>(r1, r4, r4)
            r5 = 2
            android.view.View[] r6 = new android.view.View[r5]
            r0.shadow = r6
            android.animation.AnimatorSet[] r5 = new android.animation.AnimatorSet[r5]
            r0.shadowAnimation = r5
            android.util.LongSparseArray r5 = new android.util.LongSparseArray
            r5.<init>()
            r0.selectedDialogs = r5
            android.graphics.RectF r5 = new android.graphics.RectF
            r5.<init>()
            r0.rect = r5
            android.graphics.Paint r5 = new android.graphics.Paint
            r5.<init>(r4)
            r0.paint = r5
            android.text.TextPaint r5 = new android.text.TextPaint
            r5.<init>(r4)
            r0.textPaint = r5
            int r5 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            r0.currentAccount = r5
            android.content.res.Resources r5 = r29.getResources()
            r6 = 2131231573(0x7f080355, float:1.807923E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0.shadowDrawable = r5
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            java.lang.String r7 = "dialogBackground"
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r8, r9)
            r5.setColorFilter(r6)
            r5 = r34
            r0.isFullscreen = r5
            r6 = r33
            r0.linkToCopy = r6
            r0.sendingMessageObjects = r2
            im.bclpbkiauv.ui.components.ShareAlert$ShareSearchAdapter r8 = new im.bclpbkiauv.ui.components.ShareAlert$ShareSearchAdapter
            r8.<init>(r1)
            r0.searchAdapter = r8
            r0.isChannel = r3
            r8 = r31
            r0.sendingText = r8
            r9 = 0
            if (r3 == 0) goto L_0x00d4
            r0.loadingLink = r4
            im.bclpbkiauv.tgnet.TLRPC$TL_channels_exportMessageLinkV2 r10 = new im.bclpbkiauv.tgnet.TLRPC$TL_channels_exportMessageLinkV2
            r10.<init>()
            java.lang.Object r11 = r2.get(r9)
            im.bclpbkiauv.messenger.MessageObject r11 = (im.bclpbkiauv.messenger.MessageObject) r11
            int r11 = r11.getId()
            r10.id = r11
            int r11 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r11 = im.bclpbkiauv.messenger.MessagesController.getInstance(r11)
            java.lang.Object r12 = r2.get(r9)
            im.bclpbkiauv.messenger.MessageObject r12 = (im.bclpbkiauv.messenger.MessageObject) r12
            im.bclpbkiauv.tgnet.TLRPC$Message r12 = r12.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r12 = r12.to_id
            int r12 = r12.channel_id
            im.bclpbkiauv.tgnet.TLRPC$InputChannel r11 = r11.getInputChannel((int) r12)
            r10.channel = r11
            java.lang.Object r11 = r2.get(r9)
            im.bclpbkiauv.messenger.MessageObject r11 = (im.bclpbkiauv.messenger.MessageObject) r11
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r11.messageOwner
            int r11 = r11.from_id
            if (r11 >= 0) goto L_0x00c6
            int r11 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r11 = im.bclpbkiauv.messenger.MessagesController.getInstance(r11)
            java.lang.Object r12 = r2.get(r9)
            im.bclpbkiauv.messenger.MessageObject r12 = (im.bclpbkiauv.messenger.MessageObject) r12
            im.bclpbkiauv.tgnet.TLRPC$Message r12 = r12.messageOwner
            int r12 = r12.from_id
            int r12 = -r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            im.bclpbkiauv.tgnet.TLRPC$Chat r11 = r11.getChat(r12)
            if (r11 == 0) goto L_0x00c6
            boolean r12 = r11.megagroup
            r10.isGroup = r12
        L_0x00c6:
            int r11 = r0.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r11 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r11)
            im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$mvBewK7Yt8x2TtDCfLyQa_lc8hs r12 = new im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$mvBewK7Yt8x2TtDCfLyQa_lc8hs
            r12.<init>(r1)
            r11.sendRequest(r10, r12)
        L_0x00d4:
            im.bclpbkiauv.ui.components.ShareAlert$1 r10 = new im.bclpbkiauv.ui.components.ShareAlert$1
            r10.<init>(r1)
            r0.containerView = r10
            android.view.ViewGroup r11 = r0.containerView
            r11.setWillNotDraw(r9)
            android.view.ViewGroup r11 = r0.containerView
            int r12 = r0.backgroundPaddingLeft
            int r13 = r0.backgroundPaddingLeft
            r11.setPadding(r12, r9, r13, r9)
            android.widget.FrameLayout r11 = new android.widget.FrameLayout
            r11.<init>(r1)
            r0.frameLayout = r11
            int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r11.setBackgroundColor(r12)
            im.bclpbkiauv.ui.components.ShareAlert$SearchField r11 = new im.bclpbkiauv.ui.components.ShareAlert$SearchField
            r11.<init>(r1)
            android.widget.FrameLayout r12 = r0.frameLayout
            r13 = 51
            r14 = -1
            android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r14, (int) r13)
            r12.addView(r11, r15)
            im.bclpbkiauv.ui.components.ShareAlert$2 r12 = new im.bclpbkiauv.ui.components.ShareAlert$2
            r12.<init>(r1)
            r0.gridView = r12
            r15 = 13
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r12.setTag(r15)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            r15 = 1111490560(0x42400000, float:48.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            r12.setPadding(r9, r9, r9, r13)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            r12.setClipToPadding(r9)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            androidx.recyclerview.widget.GridLayoutManager r13 = new androidx.recyclerview.widget.GridLayoutManager
            android.content.Context r15 = r28.getContext()
            r14 = 4
            r13.<init>(r15, r14)
            r0.layoutManager = r13
            r12.setLayoutManager(r13)
            androidx.recyclerview.widget.GridLayoutManager r12 = r0.layoutManager
            im.bclpbkiauv.ui.components.ShareAlert$3 r13 = new im.bclpbkiauv.ui.components.ShareAlert$3
            r13.<init>()
            r12.setSpanSizeLookup(r13)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            r12.setHorizontalScrollBarEnabled(r9)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            r12.setVerticalScrollBarEnabled(r9)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            im.bclpbkiauv.ui.components.ShareAlert$4 r13 = new im.bclpbkiauv.ui.components.ShareAlert$4
            r13.<init>()
            r12.addItemDecoration(r13)
            android.view.ViewGroup r12 = r0.containerView
            im.bclpbkiauv.ui.components.RecyclerListView r13 = r0.gridView
            r17 = -1082130432(0xffffffffbf800000, float:-1.0)
            r18 = -1082130432(0xffffffffbf800000, float:-1.0)
            r19 = 51
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 0
            android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r12.addView(r13, r15)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            im.bclpbkiauv.ui.components.ShareAlert$ShareDialogsAdapter r13 = new im.bclpbkiauv.ui.components.ShareAlert$ShareDialogsAdapter
            r13.<init>(r1)
            r0.listAdapter = r13
            r12.setAdapter(r13)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            java.lang.String r13 = "dialogScrollGlow"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r12.setGlowColor(r13)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$tXjhn_XW6Hf5F3W5HUDH9nQYW1M r13 = new im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$tXjhn_XW6Hf5F3W5HUDH9nQYW1M
            r13.<init>(r11)
            r12.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r13)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            im.bclpbkiauv.ui.components.ShareAlert$5 r13 = new im.bclpbkiauv.ui.components.ShareAlert$5
            r13.<init>()
            r12.setOnScrollListener(r13)
            im.bclpbkiauv.ui.components.EmptyTextProgressView r12 = new im.bclpbkiauv.ui.components.EmptyTextProgressView
            r12.<init>(r1)
            r0.searchEmptyView = r12
            r12.setShowAtCenter(r4)
            im.bclpbkiauv.ui.components.EmptyTextProgressView r12 = r0.searchEmptyView
            r12.showTextView()
            im.bclpbkiauv.ui.components.EmptyTextProgressView r12 = r0.searchEmptyView
            r13 = 2131692198(0x7f0f0aa6, float:1.901349E38)
            java.lang.String r15 = "NoChats"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r13)
            r12.setText(r13)
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.gridView
            im.bclpbkiauv.ui.components.EmptyTextProgressView r13 = r0.searchEmptyView
            r12.setEmptyView(r13)
            android.view.ViewGroup r12 = r0.containerView
            im.bclpbkiauv.ui.components.EmptyTextProgressView r13 = r0.searchEmptyView
            r21 = 1112539136(0x42500000, float:52.0)
            android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r12.addView(r13, r15)
            android.widget.FrameLayout$LayoutParams r12 = new android.widget.FrameLayout$LayoutParams
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            r14 = -1
            r15 = 51
            r12.<init>(r14, r13, r15)
            r13 = 1114112000(0x42680000, float:58.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r13
            android.view.View[] r13 = r0.shadow
            android.view.View r14 = new android.view.View
            r14.<init>(r1)
            r13[r9] = r14
            android.view.View[] r13 = r0.shadow
            r13 = r13[r9]
            java.lang.String r14 = "dialogShadowLine"
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r13.setBackgroundColor(r15)
            android.view.View[] r13 = r0.shadow
            r13 = r13[r9]
            r15 = 0
            r13.setAlpha(r15)
            android.view.View[] r13 = r0.shadow
            r13 = r13[r9]
            java.lang.Integer r15 = java.lang.Integer.valueOf(r4)
            r13.setTag(r15)
            android.view.ViewGroup r13 = r0.containerView
            android.view.View[] r15 = r0.shadow
            r15 = r15[r9]
            r13.addView(r15, r12)
            android.view.ViewGroup r13 = r0.containerView
            android.widget.FrameLayout r15 = r0.frameLayout
            r9 = 58
            r2 = -1
            r4 = 51
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r2, (int) r9, (int) r4)
            r13.addView(r15, r4)
            android.widget.FrameLayout$LayoutParams r4 = new android.widget.FrameLayout$LayoutParams
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            r13 = 83
            r4.<init>(r2, r9, r13)
            r2 = r4
            r4 = 1111490560(0x42400000, float:48.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r2.bottomMargin = r4
            android.view.View[] r4 = r0.shadow
            android.view.View r9 = new android.view.View
            r9.<init>(r1)
            r12 = 1
            r4[r12] = r9
            android.view.View[] r4 = r0.shadow
            r4 = r4[r12]
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r4.setBackgroundColor(r9)
            android.view.ViewGroup r4 = r0.containerView
            android.view.View[] r9 = r0.shadow
            r9 = r9[r12]
            r4.addView(r9, r2)
            boolean r4 = r0.isChannel
            java.lang.String r12 = "fonts/rmedium.ttf"
            if (r4 != 0) goto L_0x026b
            java.lang.String r4 = r0.linkToCopy
            if (r4 == 0) goto L_0x025f
            goto L_0x026b
        L_0x025f:
            android.view.View[] r4 = r0.shadow
            r14 = 1
            r4 = r4[r14]
            r14 = 0
            r4.setAlpha(r14)
            r16 = r2
            goto L_0x02e2
        L_0x026b:
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.pickerBottomLayout = r4
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            java.lang.String r15 = "listSelectorSDK21"
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r14 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorWithBackgroundDrawable(r14, r15)
            r4.setBackgroundDrawable(r14)
            android.widget.TextView r4 = r0.pickerBottomLayout
            java.lang.String r14 = "dialogTextBlue2"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r4.setTextColor(r14)
            android.widget.TextView r4 = r0.pickerBottomLayout
            r14 = 1096810496(0x41600000, float:14.0)
            r15 = 1
            r4.setTextSize(r15, r14)
            android.widget.TextView r4 = r0.pickerBottomLayout
            r14 = 1099956224(0x41900000, float:18.0)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r9 = 0
            r4.setPadding(r15, r9, r14, r9)
            android.widget.TextView r4 = r0.pickerBottomLayout
            android.graphics.Typeface r9 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r12)
            r4.setTypeface(r9)
            android.widget.TextView r4 = r0.pickerBottomLayout
            r9 = 17
            r4.setGravity(r9)
            android.widget.TextView r4 = r0.pickerBottomLayout
            r9 = 2131690736(0x7f0f04f0, float:1.9010524E38)
            java.lang.String r14 = "CopyLink"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r9)
            java.lang.String r9 = r9.toUpperCase()
            r4.setText(r9)
            android.widget.TextView r4 = r0.pickerBottomLayout
            im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$NLyAFpfmyOm-YDW0Q_qSuKCw7UY r9 = new im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$NLyAFpfmyOm-YDW0Q_qSuKCw7UY
            r9.<init>()
            r4.setOnClickListener(r9)
            android.view.ViewGroup r4 = r0.containerView
            android.widget.TextView r9 = r0.pickerBottomLayout
            r16 = r2
            r14 = 48
            r15 = -1
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r15, (int) r14, (int) r13)
            r4.addView(r9, r2)
        L_0x02e2:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.frameLayout2 = r2
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r2.setBackgroundColor(r4)
            android.widget.FrameLayout r2 = r0.frameLayout2
            r4 = 0
            r2.setAlpha(r4)
            android.widget.FrameLayout r2 = r0.frameLayout2
            r4 = 4
            r2.setVisibility(r4)
            android.view.ViewGroup r2 = r0.containerView
            android.widget.FrameLayout r4 = r0.frameLayout2
            r7 = 48
            r9 = -1
            android.widget.FrameLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r9, (int) r7, (int) r13)
            r2.addView(r4, r7)
            android.widget.FrameLayout r2 = r0.frameLayout2
            im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$ZTASlIDAw8A6cRNcp64ybSs9SJA r4 = im.bclpbkiauv.ui.components.$$Lambda$ShareAlert$ZTASlIDAw8A6cRNcp64ybSs9SJA.INSTANCE
            r2.setOnTouchListener(r4)
            im.bclpbkiauv.ui.components.EditTextEmoji r2 = new im.bclpbkiauv.ui.components.EditTextEmoji
            r4 = 0
            r7 = 1
            r2.<init>(r1, r10, r4, r7)
            r0.commentTextView = r2
            r4 = 2131693924(0x7f0f1164, float:1.901699E38)
            java.lang.String r7 = "ShareComment"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r4)
            r2.setHint(r4)
            im.bclpbkiauv.ui.components.EditTextEmoji r2 = r0.commentTextView
            r2.onResume()
            im.bclpbkiauv.ui.components.EditTextEmoji r2 = r0.commentTextView
            im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r2.getEditText()
            r4 = 1
            r2.setMaxLines(r4)
            r2.setSingleLine(r4)
            android.widget.FrameLayout r4 = r0.frameLayout2
            im.bclpbkiauv.ui.components.EditTextEmoji r7 = r0.commentTextView
            r21 = -1082130432(0xffffffffbf800000, float:-1.0)
            r22 = -1082130432(0xffffffffbf800000, float:-1.0)
            r23 = 51
            r24 = 0
            r25 = 0
            r26 = 1118306304(0x42a80000, float:84.0)
            r27 = 0
            android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r4.addView(r7, r9)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.writeButtonContainer = r4
            r7 = 4
            r4.setVisibility(r7)
            android.widget.FrameLayout r4 = r0.writeButtonContainer
            r7 = 1045220557(0x3e4ccccd, float:0.2)
            r4.setScaleX(r7)
            android.widget.FrameLayout r4 = r0.writeButtonContainer
            r4.setScaleY(r7)
            android.widget.FrameLayout r4 = r0.writeButtonContainer
            r9 = 0
            r4.setAlpha(r9)
            android.widget.FrameLayout r4 = r0.writeButtonContainer
            r9 = 2131693795(0x7f0f10e3, float:1.9016728E38)
            java.lang.String r13 = "Send"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r9)
            r4.setContentDescription(r9)
            android.view.ViewGroup r4 = r0.containerView
            android.widget.FrameLayout r9 = r0.writeButtonContainer
            r21 = 1114636288(0x42700000, float:60.0)
            r22 = 1114636288(0x42700000, float:60.0)
            r23 = 85
            r26 = 1086324736(0x40c00000, float:6.0)
            r27 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r4.addView(r9, r13)
            android.widget.FrameLayout r4 = r0.writeButtonContainer
            im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$ZyIUDQ741r2Cg_yOGmMRGQa55IA r9 = new im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$ZyIUDQ741r2Cg_yOGmMRGQa55IA
            r9.<init>()
            r4.setOnClickListener(r9)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            r9 = 1113587712(0x42600000, float:56.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            java.lang.String r14 = "dialogFloatingButton"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            java.lang.String r15 = "dialogFloatingButtonPressed"
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r13 = im.bclpbkiauv.ui.actionbar.Theme.createSimpleSelectorCircleDrawable(r13, r14, r15)
            int r14 = android.os.Build.VERSION.SDK_INT
            r15 = 21
            if (r14 >= r15) goto L_0x03ef
            android.content.res.Resources r14 = r29.getResources()
            r7 = 2131231019(0x7f08012b, float:1.8078107E38)
            android.graphics.drawable.Drawable r7 = r14.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            android.graphics.PorterDuffColorFilter r14 = new android.graphics.PorterDuffColorFilter
            r15 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r14.<init>(r15, r9)
            r7.setColorFilter(r14)
            im.bclpbkiauv.ui.components.CombinedDrawable r9 = new im.bclpbkiauv.ui.components.CombinedDrawable
            r14 = 0
            r9.<init>(r7, r13, r14, r14)
            r14 = 1113587712(0x42600000, float:56.0)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r17 = r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r9.setIconSize(r15, r2)
            r13 = r9
            goto L_0x03f3
        L_0x03ef:
            r17 = r2
            r14 = 1113587712(0x42600000, float:56.0)
        L_0x03f3:
            r4.setBackgroundDrawable(r13)
            r2 = 2131230835(0x7f080073, float:1.8077734E38)
            r4.setImageResource(r2)
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r7 = "dialogFloatingIcon"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r7, r9)
            r4.setColorFilter(r2)
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r2)
            int r2 = android.os.Build.VERSION.SDK_INT
            r7 = 21
            if (r2 < r7) goto L_0x041f
            im.bclpbkiauv.ui.components.ShareAlert$6 r2 = new im.bclpbkiauv.ui.components.ShareAlert$6
            r2.<init>()
            r4.setOutlineProvider(r2)
        L_0x041f:
            android.widget.FrameLayout r2 = r0.writeButtonContainer
            int r7 = android.os.Build.VERSION.SDK_INT
            r9 = 1114636288(0x42700000, float:60.0)
            r15 = 21
            if (r7 < r15) goto L_0x042c
            r21 = 1113587712(0x42600000, float:56.0)
            goto L_0x042e
        L_0x042c:
            r21 = 1114636288(0x42700000, float:60.0)
        L_0x042e:
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r7 < r15) goto L_0x0435
            r22 = 1113587712(0x42600000, float:56.0)
            goto L_0x0437
        L_0x0435:
            r22 = 1114636288(0x42700000, float:60.0)
        L_0x0437:
            r23 = 51
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r7 < r15) goto L_0x0442
            r14 = 1073741824(0x40000000, float:2.0)
            r24 = 1073741824(0x40000000, float:2.0)
            goto L_0x0444
        L_0x0442:
            r24 = 0
        L_0x0444:
            r25 = 0
            r26 = 0
            r27 = 0
            android.widget.FrameLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r2.addView(r4, r7)
            android.text.TextPaint r2 = r0.textPaint
            r7 = 1094713344(0x41400000, float:12.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = r0.textPaint
            android.graphics.Typeface r7 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r12)
            r2.setTypeface(r7)
            im.bclpbkiauv.ui.components.ShareAlert$7 r2 = new im.bclpbkiauv.ui.components.ShareAlert$7
            r2.<init>(r1)
            r0.selectedCountView = r2
            r7 = 0
            r2.setAlpha(r7)
            android.view.View r2 = r0.selectedCountView
            r7 = 1045220557(0x3e4ccccd, float:0.2)
            r2.setScaleX(r7)
            android.view.View r2 = r0.selectedCountView
            r2.setScaleY(r7)
            android.view.ViewGroup r2 = r0.containerView
            android.view.View r7 = r0.selectedCountView
            r21 = 1109917696(0x42280000, float:42.0)
            r22 = 1103101952(0x41c00000, float:24.0)
            r23 = 85
            r24 = 0
            r26 = -1056964608(0xffffffffc1000000, float:-8.0)
            r27 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r2.addView(r7, r9)
            r2 = 0
            r0.updateSelectedCount(r2)
            boolean[] r7 = im.bclpbkiauv.ui.DialogsActivity.dialogsLoaded
            int r9 = r0.currentAccount
            boolean r7 = r7[r9]
            if (r7 != 0) goto L_0x04ba
            im.bclpbkiauv.messenger.MessagesController r7 = im.bclpbkiauv.messenger.MessagesController.getInstance(r9)
            r9 = 100
            r12 = 1
            r7.loadDialogs(r2, r2, r9, r12)
            int r2 = r0.currentAccount
            im.bclpbkiauv.messenger.ContactsController r2 = im.bclpbkiauv.messenger.ContactsController.getInstance(r2)
            r2.checkInviteText()
            boolean[] r2 = im.bclpbkiauv.ui.DialogsActivity.dialogsLoaded
            int r7 = r0.currentAccount
            r2[r7] = r12
        L_0x04ba:
            im.bclpbkiauv.ui.components.ShareAlert$ShareDialogsAdapter r2 = r0.listAdapter
            java.util.ArrayList r2 = r2.dialogs
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x04d1
            int r2 = r0.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r2 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r2)
            int r7 = im.bclpbkiauv.messenger.NotificationCenter.dialogsNeedReload
            r2.addObserver(r0, r7)
        L_0x04d1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ShareAlert.<init>(android.content.Context, java.util.ArrayList, java.lang.String, boolean, java.lang.String, boolean):void");
    }

    public /* synthetic */ void lambda$new$1$ShareAlert(Context context, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response, context) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ Context f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ShareAlert.this.lambda$null$0$ShareAlert(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$ShareAlert(TLObject response, Context context) {
        if (response != null) {
            this.exportedMessageLink = (TLRPC.TL_exportedMessageLink) response;
            if (this.copyLinkOnEnd) {
                copyLink(context);
            }
        }
        this.loadingLink = false;
    }

    public /* synthetic */ void lambda$new$2$ShareAlert(SearchField searchView, View view, int position) {
        TLRPC.Dialog dialog;
        if (position >= 0) {
            RecyclerView.Adapter adapter = this.gridView.getAdapter();
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (adapter == shareDialogsAdapter) {
                dialog = shareDialogsAdapter.getItem(position);
            } else {
                dialog = this.searchAdapter.getItem(position);
            }
            if (dialog != null) {
                ShareDialogCell cell = (ShareDialogCell) view;
                if (this.selectedDialogs.indexOfKey(dialog.id) >= 0) {
                    this.selectedDialogs.remove(dialog.id);
                    cell.setChecked(false, true);
                    updateSelectedCount(1);
                    return;
                }
                this.selectedDialogs.put(dialog.id, dialog);
                cell.setChecked(true, true);
                updateSelectedCount(2);
                int selfUserId = UserConfig.getInstance(this.currentAccount).clientUserId;
                if (this.gridView.getAdapter() == this.searchAdapter) {
                    TLRPC.Dialog existingDialog = (TLRPC.Dialog) this.listAdapter.dialogsMap.get(dialog.id);
                    if (existingDialog == null) {
                        this.listAdapter.dialogsMap.put(dialog.id, dialog);
                        this.listAdapter.dialogs.add(true ^ this.listAdapter.dialogs.isEmpty() ? 1 : 0, dialog);
                    } else if (existingDialog.id != ((long) selfUserId)) {
                        this.listAdapter.dialogs.remove(existingDialog);
                        this.listAdapter.dialogs.add(true ^ this.listAdapter.dialogs.isEmpty() ? 1 : 0, existingDialog);
                    }
                    searchView.searchEditText.setText("");
                    this.gridView.setAdapter(this.listAdapter);
                    searchView.hideKeyboard();
                }
            }
        }
    }

    public /* synthetic */ void lambda$new$3$ShareAlert(View v) {
        if (this.selectedDialogs.size() != 0) {
            return;
        }
        if (this.isChannel || this.linkToCopy != null) {
            if (this.linkToCopy != null || !this.loadingLink) {
                copyLink(getContext());
            } else {
                this.copyLinkOnEnd = true;
                ToastUtils.show((int) R.string.Loading);
            }
            dismiss();
        }
    }

    static /* synthetic */ boolean lambda$new$4(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$new$5$ShareAlert(View v) {
        int a = 0;
        while (a < this.selectedDialogs.size()) {
            if (!AlertsCreator.checkSlowMode(getContext(), this.currentAccount, this.selectedDialogs.keyAt(a), this.frameLayout2.getTag() != null && this.commentTextView.length() > 0)) {
                a++;
            } else {
                return;
            }
        }
        if (this.sendingMessageObjects != null) {
            for (int a2 = 0; a2 < this.selectedDialogs.size(); a2++) {
                long key = this.selectedDialogs.keyAt(a2);
                if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), key, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingMessageObjects, key, true, 0);
            }
        } else if (this.sendingText != null) {
            for (int a3 = 0; a3 < this.selectedDialogs.size(); a3++) {
                long key2 = this.selectedDialogs.keyAt(a3);
                if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), key2, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingText, key2, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            }
        }
        dismiss();
    }

    /* access modifiers changed from: private */
    public int getCurrentTop() {
        if (this.gridView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.gridView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
            i = child.getTop();
        }
        return paddingTop - i;
    }

    public void dismissInternal() {
        super.dismissInternal();
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            super.onBackPressed();
        } else {
            this.commentTextView.hidePopup(true);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (shareDialogsAdapter != null) {
                shareDialogsAdapter.fetchDialogs();
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.gridView.getChildCount() > 0) {
            View child = this.gridView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
            int top = child.getTop() - AndroidUtilities.dp(8.0f);
            int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                runShadowAnimation(0, true);
            } else {
                newOffset = top;
                runShadowAnimation(0, false);
            }
            if (this.scrollOffsetY != newOffset) {
                RecyclerListView recyclerListView = this.gridView;
                this.scrollOffsetY = newOffset;
                recyclerListView.setTopGlowOffset(newOffset);
                this.frameLayout.setTranslationY((float) this.scrollOffsetY);
                this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
                this.containerView.invalidate();
            }
        }
    }

    private void runShadowAnimation(final int num, final boolean show) {
        if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
            this.shadow[num].setTag(show ? null : 1);
            if (show) {
                this.shadow[num].setVisibility(0);
            }
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[num] != null) {
                animatorSetArr[num].cancel();
            }
            this.shadowAnimation[num] = new AnimatorSet();
            AnimatorSet animatorSet2 = this.shadowAnimation[num];
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow[num];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation[num].setDuration(150);
            this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ShareAlert.this.shadowAnimation[num] != null && ShareAlert.this.shadowAnimation[num].equals(animation)) {
                        if (!show) {
                            ShareAlert.this.shadow[num].setVisibility(4);
                        }
                        ShareAlert.this.shadowAnimation[num] = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ShareAlert.this.shadowAnimation[num] != null && ShareAlert.this.shadowAnimation[num].equals(animation)) {
                        ShareAlert.this.shadowAnimation[num] = null;
                    }
                }
            });
            this.shadowAnimation[num].start();
        }
    }

    private void copyLink(Context context) {
        if (this.exportedMessageLink != null || this.linkToCopy != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.linkToCopy != null ? this.linkToCopy : this.exportedMessageLink.link));
                if (this.exportedMessageLink == null || !this.exportedMessageLink.link.contains("/c/")) {
                    ToastUtils.show((int) R.string.LinkCopied);
                } else {
                    ToastUtils.show((int) R.string.LinkCopiedPrivate);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private boolean showCommentTextView(final boolean show) {
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        this.animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        FrameLayout frameLayout3 = this.frameLayout2;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(frameLayout3, property, fArr));
        FrameLayout frameLayout4 = this.writeButtonContainer;
        Property property2 = View.SCALE_X;
        float[] fArr2 = new float[1];
        float f2 = 0.2f;
        fArr2[0] = show ? 1.0f : 0.2f;
        animators.add(ObjectAnimator.ofFloat(frameLayout4, property2, fArr2));
        FrameLayout frameLayout5 = this.writeButtonContainer;
        Property property3 = View.SCALE_Y;
        float[] fArr3 = new float[1];
        fArr3[0] = show ? 1.0f : 0.2f;
        animators.add(ObjectAnimator.ofFloat(frameLayout5, property3, fArr3));
        FrameLayout frameLayout6 = this.writeButtonContainer;
        Property property4 = View.ALPHA;
        float[] fArr4 = new float[1];
        fArr4[0] = show ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(frameLayout6, property4, fArr4));
        View view = this.selectedCountView;
        Property property5 = View.SCALE_X;
        float[] fArr5 = new float[1];
        fArr5[0] = show ? 1.0f : 0.2f;
        animators.add(ObjectAnimator.ofFloat(view, property5, fArr5));
        View view2 = this.selectedCountView;
        Property property6 = View.SCALE_Y;
        float[] fArr6 = new float[1];
        if (show) {
            f2 = 1.0f;
        }
        fArr6[0] = f2;
        animators.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
        View view3 = this.selectedCountView;
        Property property7 = View.ALPHA;
        float[] fArr7 = new float[1];
        fArr7[0] = show ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
        TextView textView = this.pickerBottomLayout;
        if (textView == null || textView.getVisibility() != 0) {
            View view4 = this.shadow[1];
            Property property8 = View.ALPHA;
            float[] fArr8 = new float[1];
            if (show) {
                f = 1.0f;
            }
            fArr8[0] = f;
            animators.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
        }
        this.animatorSet.playTogether(animators);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(180);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(ShareAlert.this.animatorSet)) {
                    if (!show) {
                        ShareAlert.this.frameLayout2.setVisibility(4);
                        ShareAlert.this.writeButtonContainer.setVisibility(4);
                    }
                    AnimatorSet unused = ShareAlert.this.animatorSet = null;
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (animation.equals(ShareAlert.this.animatorSet)) {
                    AnimatorSet unused = ShareAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        return true;
    }

    public void updateSelectedCount(int animated) {
        if (this.selectedDialogs.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true) || animated == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet2 = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = animated == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.selectedCountView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (animated != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
        animatorSet2.playTogether(animatorArr);
        animatorSet2.setInterpolator(new OvershootInterpolator());
        animatorSet2.setDuration(180);
        animatorSet2.start();
    }

    public void dismiss() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
        }
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    private class ShareDialogsAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int currentCount;
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>();
        /* access modifiers changed from: private */
        public LongSparseArray<TLRPC.Dialog> dialogsMap = new LongSparseArray<>();

        public ShareDialogsAdapter(Context context2) {
            this.context = context2;
            fetchDialogs();
        }

        public void fetchDialogs() {
            int lower_id;
            this.dialogs.clear();
            this.dialogsMap.clear();
            int selfUserId = UserConfig.getInstance(ShareAlert.this.currentAccount).clientUserId;
            if (!MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.isEmpty()) {
                TLRPC.Dialog dialog = MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.get(0);
                this.dialogs.add(dialog);
                this.dialogsMap.put(dialog.id, dialog);
            }
            ArrayList<TLRPC.Dialog> allDialogs = MessagesController.getInstance(ShareAlert.this.currentAccount).getAllDialogs();
            for (int a = 0; a < allDialogs.size(); a++) {
                TLRPC.Dialog dialog2 = allDialogs.get(a);
                if ((dialog2 instanceof TLRPC.TL_dialog) && (lower_id = (int) dialog2.id) != selfUserId) {
                    if (lower_id < 0) {
                        TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(-lower_id));
                        if (ChatObject.canSendMessages(chat)) {
                            if (ShareAlert.this.sendingMessageObjects != null) {
                                boolean continueTag = false;
                                Iterator it = ShareAlert.this.sendingMessageObjects.iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        break;
                                    }
                                    MessageObject m = (MessageObject) it.next();
                                    if ((RegexUtils.hasLink(m != null ? m.messageText.toString() : null) || ((m != null && !TextUtils.isEmpty(m.messageText) && m.messageText.toString().startsWith("@")) || m.type == 103)) && ChatObject.canSendEmbed(chat)) {
                                        continueTag = true;
                                        break;
                                    }
                                }
                                if (continueTag) {
                                }
                            }
                        }
                    }
                    int high_id = (int) (dialog2.id >> 32);
                    if (!(lower_id == 0 || high_id == 1)) {
                        if (lower_id > 0) {
                            this.dialogs.add(dialog2);
                            this.dialogsMap.put(dialog2.id, dialog2);
                        } else {
                            TLRPC.Chat chat2 = MessagesController.getInstance(ShareAlert.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                            if (chat2 != null && !ChatObject.isNotInChat(chat2) && (!ChatObject.isChannel(chat2) || chat2.creator || ((chat2.admin_rights != null && chat2.admin_rights.post_messages) || chat2.megagroup))) {
                                this.dialogs.add(dialog2);
                                this.dialogsMap.put(dialog2.id, dialog2);
                            }
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        public int getItemCount() {
            int count = this.dialogs.size();
            if (count != 0) {
                return count + 1;
            }
            return count;
        }

        public TLRPC.Dialog getItem(int position) {
            int position2 = position - 1;
            if (position2 < 0 || position2 >= this.dialogs.size()) {
                return null;
            }
            return this.dialogs.get(position2);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 1) {
                return false;
            }
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new ShareDialogCell(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ShareDialogCell cell = (ShareDialogCell) holder.itemView;
                TLRPC.Dialog dialog = getItem(position);
                cell.setDialog((int) dialog.id, ShareAlert.this.selectedDialogs.indexOfKey(dialog.id) >= 0, (CharSequence) null);
            }
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            return 0;
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int lastReqId;
        private int lastSearchId;
        private String lastSearchText;
        private int reqId;
        private ArrayList<DialogSearchResult> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        private class DialogSearchResult {
            public int date;
            public TLRPC.Dialog dialog;
            public CharSequence name;
            public TLObject object;

            private DialogSearchResult() {
                this.dialog = new TLRPC.TL_dialog();
            }
        }

        public ShareSearchAdapter(Context context2) {
            this.context = context2;
        }

        /* access modifiers changed from: private */
        /* renamed from: searchDialogsInternal */
        public void lambda$searchDialogs$3$ShareAlert$ShareSearchAdapter(String query, int searchId) {
            MessagesStorage.getInstance(ShareAlert.this.currentAccount).getStorageQueue().postRunnable(new Runnable(query, searchId) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:182:0x049b A[Catch:{ Exception -> 0x04df }, LOOP:7: B:151:0x03b9->B:182:0x049b, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:205:0x0180 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:225:0x0410 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x020a A[Catch:{ Exception -> 0x04df }, LOOP:2: B:46:0x0129->B:77:0x020a, LOOP_END] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(java.lang.String r29, int r30) {
            /*
                r28 = this;
                r1 = r28
                java.lang.String r0 = r29.trim()     // Catch:{ Exception -> 0x04df }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x04df }
                int r2 = r0.length()     // Catch:{ Exception -> 0x04df }
                r3 = -1
                if (r2 != 0) goto L_0x001e
                r1.lastSearchId = r3     // Catch:{ Exception -> 0x04df }
                java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x04df }
                r2.<init>()     // Catch:{ Exception -> 0x04df }
                int r3 = r1.lastSearchId     // Catch:{ Exception -> 0x04df }
                r1.updateSearchResults(r2, r3)     // Catch:{ Exception -> 0x04df }
                return
            L_0x001e:
                im.bclpbkiauv.messenger.LocaleController r2 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x04df }
                java.lang.String r2 = r2.getTranslitString(r0)     // Catch:{ Exception -> 0x04df }
                boolean r4 = r0.equals(r2)     // Catch:{ Exception -> 0x04df }
                if (r4 != 0) goto L_0x0032
                int r4 = r2.length()     // Catch:{ Exception -> 0x04df }
                if (r4 != 0) goto L_0x0033
            L_0x0032:
                r2 = 0
            L_0x0033:
                r4 = 1
                r5 = 0
                if (r2 == 0) goto L_0x0039
                r6 = 1
                goto L_0x003a
            L_0x0039:
                r6 = 0
            L_0x003a:
                int r6 = r6 + r4
                java.lang.String[] r6 = new java.lang.String[r6]     // Catch:{ Exception -> 0x04df }
                r6[r5] = r0     // Catch:{ Exception -> 0x04df }
                if (r2 == 0) goto L_0x0043
                r6[r4] = r2     // Catch:{ Exception -> 0x04df }
            L_0x0043:
                java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x04df }
                r7.<init>()     // Catch:{ Exception -> 0x04df }
                java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x04df }
                r8.<init>()     // Catch:{ Exception -> 0x04df }
                r9 = 0
                android.util.LongSparseArray r10 = new android.util.LongSparseArray     // Catch:{ Exception -> 0x04df }
                r10.<init>()     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.ui.components.ShareAlert r11 = im.bclpbkiauv.ui.components.ShareAlert.this     // Catch:{ Exception -> 0x04df }
                int r11 = r11.currentAccount     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.messenger.MessagesStorage r11 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r11)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.sqlite.SQLiteDatabase r11 = r11.getDatabase()     // Catch:{ Exception -> 0x04df }
                java.lang.String r12 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400"
                java.lang.Object[] r13 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.sqlite.SQLiteCursor r11 = r11.queryFinalized(r12, r13)     // Catch:{ Exception -> 0x04df }
            L_0x0069:
                boolean r12 = r11.next()     // Catch:{ Exception -> 0x04df }
                r13 = 0
                if (r12 == 0) goto L_0x00b9
                long r14 = r11.longValue(r5)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.ui.components.ShareAlert$ShareSearchAdapter$DialogSearchResult r12 = new im.bclpbkiauv.ui.components.ShareAlert$ShareSearchAdapter$DialogSearchResult     // Catch:{ Exception -> 0x04df }
                r12.<init>()     // Catch:{ Exception -> 0x04df }
                int r13 = r11.intValue(r4)     // Catch:{ Exception -> 0x04df }
                r12.date = r13     // Catch:{ Exception -> 0x04df }
                r10.put(r14, r12)     // Catch:{ Exception -> 0x04df }
                int r13 = (int) r14     // Catch:{ Exception -> 0x04df }
                r16 = 32
                r18 = r6
                long r5 = r14 >> r16
                int r6 = (int) r5     // Catch:{ Exception -> 0x04df }
                if (r13 == 0) goto L_0x00b5
                if (r6 == r4) goto L_0x00b5
                if (r13 <= 0) goto L_0x00a2
                java.lang.Integer r5 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x04df }
                boolean r5 = r7.contains(r5)     // Catch:{ Exception -> 0x04df }
                if (r5 != 0) goto L_0x00b5
                java.lang.Integer r5 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x04df }
                r7.add(r5)     // Catch:{ Exception -> 0x04df }
                goto L_0x00b5
            L_0x00a2:
                int r5 = -r13
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x04df }
                boolean r5 = r8.contains(r5)     // Catch:{ Exception -> 0x04df }
                if (r5 != 0) goto L_0x00b5
                int r5 = -r13
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x04df }
                r8.add(r5)     // Catch:{ Exception -> 0x04df }
            L_0x00b5:
                r6 = r18
                r5 = 0
                goto L_0x0069
            L_0x00b9:
                r18 = r6
                r11.dispose()     // Catch:{ Exception -> 0x04df }
                boolean r5 = r7.isEmpty()     // Catch:{ Exception -> 0x04df }
                java.lang.String r6 = ";;;"
                java.lang.String r14 = ","
                java.lang.String r15 = "@"
                java.lang.String r13 = " "
                if (r5 != 0) goto L_0x0238
                im.bclpbkiauv.ui.components.ShareAlert r5 = im.bclpbkiauv.ui.components.ShareAlert.this     // Catch:{ Exception -> 0x04df }
                int r5 = r5.currentAccount     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.messenger.MessagesStorage r5 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r5)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.sqlite.SQLiteDatabase r5 = r5.getDatabase()     // Catch:{ Exception -> 0x04df }
                java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x04df }
                java.lang.String r12 = "SELECT data, status, name FROM users WHERE uid IN(%s)"
                r19 = r0
                java.lang.Object[] r0 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x04df }
                java.lang.String r20 = android.text.TextUtils.join(r14, r7)     // Catch:{ Exception -> 0x04df }
                r4 = 0
                r0[r4] = r20     // Catch:{ Exception -> 0x04df }
                java.lang.String r0 = java.lang.String.format(r3, r12, r0)     // Catch:{ Exception -> 0x04df }
                java.lang.Object[] r3 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.sqlite.SQLiteCursor r0 = r5.queryFinalized(r0, r3)     // Catch:{ Exception -> 0x04df }
                r11 = r0
            L_0x00f4:
                boolean r0 = r11.next()     // Catch:{ Exception -> 0x04df }
                if (r0 == 0) goto L_0x022c
                r0 = 2
                java.lang.String r3 = r11.stringValue(r0)     // Catch:{ Exception -> 0x04df }
                r0 = r3
                im.bclpbkiauv.messenger.LocaleController r3 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x04df }
                java.lang.String r3 = r3.getTranslitString(r0)     // Catch:{ Exception -> 0x04df }
                boolean r4 = r0.equals(r3)     // Catch:{ Exception -> 0x04df }
                if (r4 == 0) goto L_0x010f
                r3 = 0
            L_0x010f:
                r4 = 0
                int r5 = r0.lastIndexOf(r6)     // Catch:{ Exception -> 0x04df }
                r12 = -1
                if (r5 == r12) goto L_0x011e
                int r12 = r5 + 3
                java.lang.String r12 = r0.substring(r12)     // Catch:{ Exception -> 0x04df }
                r4 = r12
            L_0x011e:
                r12 = 0
                r20 = r2
                r2 = r18
                r18 = r5
                int r5 = r2.length     // Catch:{ Exception -> 0x04df }
                r21 = r7
                r7 = 0
            L_0x0129:
                if (r7 >= r5) goto L_0x0218
                r22 = r2[r7]     // Catch:{ Exception -> 0x04df }
                r23 = r22
                r22 = r5
                r5 = r23
                boolean r23 = r0.startsWith(r5)     // Catch:{ Exception -> 0x04df }
                if (r23 != 0) goto L_0x017b
                r23 = r12
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r12.<init>()     // Catch:{ Exception -> 0x04df }
                r12.append(r13)     // Catch:{ Exception -> 0x04df }
                r12.append(r5)     // Catch:{ Exception -> 0x04df }
                java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x04df }
                boolean r12 = r0.contains(r12)     // Catch:{ Exception -> 0x04df }
                if (r12 != 0) goto L_0x017d
                if (r3 == 0) goto L_0x016e
                boolean r12 = r3.startsWith(r5)     // Catch:{ Exception -> 0x04df }
                if (r12 != 0) goto L_0x017d
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r12.<init>()     // Catch:{ Exception -> 0x04df }
                r12.append(r13)     // Catch:{ Exception -> 0x04df }
                r12.append(r5)     // Catch:{ Exception -> 0x04df }
                java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x04df }
                boolean r12 = r3.contains(r12)     // Catch:{ Exception -> 0x04df }
                if (r12 == 0) goto L_0x016e
                goto L_0x017d
            L_0x016e:
                if (r4 == 0) goto L_0x0178
                boolean r12 = r4.startsWith(r5)     // Catch:{ Exception -> 0x04df }
                if (r12 == 0) goto L_0x0178
                r12 = 2
                goto L_0x017e
            L_0x0178:
                r12 = r23
                goto L_0x017e
            L_0x017b:
                r23 = r12
            L_0x017d:
                r12 = 1
            L_0x017e:
                if (r12 == 0) goto L_0x020a
                r7 = 0
                im.bclpbkiauv.tgnet.NativeByteBuffer r17 = r11.byteBufferValue(r7)     // Catch:{ Exception -> 0x04df }
                r22 = r17
                r7 = r22
                if (r7 == 0) goto L_0x01ff
                r24 = r0
                r25 = r3
                r0 = 0
                int r3 = r7.readInt32(r0)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.tgnet.TLRPC$User r3 = im.bclpbkiauv.tgnet.TLRPC.User.TLdeserialize(r7, r3, r0)     // Catch:{ Exception -> 0x04df }
                r0 = r3
                r7.reuse()     // Catch:{ Exception -> 0x04df }
                int r3 = r0.id     // Catch:{ Exception -> 0x04df }
                r26 = r4
                long r3 = (long) r3     // Catch:{ Exception -> 0x04df }
                java.lang.Object r3 = r10.get(r3)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.ui.components.ShareAlert$ShareSearchAdapter$DialogSearchResult r3 = (im.bclpbkiauv.ui.components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r3     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.tgnet.TLRPC$UserStatus r4 = r0.status     // Catch:{ Exception -> 0x04df }
                if (r4 == 0) goto L_0x01b9
                im.bclpbkiauv.tgnet.TLRPC$UserStatus r4 = r0.status     // Catch:{ Exception -> 0x04df }
                r27 = r6
                r22 = r7
                r7 = 1
                int r6 = r11.intValue(r7)     // Catch:{ Exception -> 0x04df }
                r4.expires = r6     // Catch:{ Exception -> 0x04df }
                goto L_0x01bd
            L_0x01b9:
                r27 = r6
                r22 = r7
            L_0x01bd:
                r4 = 1
                if (r12 != r4) goto L_0x01cb
                java.lang.String r4 = r0.first_name     // Catch:{ Exception -> 0x04df }
                java.lang.String r6 = r0.last_name     // Catch:{ Exception -> 0x04df }
                java.lang.CharSequence r4 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r4, r6, r5)     // Catch:{ Exception -> 0x04df }
                r3.name = r4     // Catch:{ Exception -> 0x04df }
                goto L_0x01f2
            L_0x01cb:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r4.<init>()     // Catch:{ Exception -> 0x04df }
                r4.append(r15)     // Catch:{ Exception -> 0x04df }
                java.lang.String r6 = r0.username     // Catch:{ Exception -> 0x04df }
                r4.append(r6)     // Catch:{ Exception -> 0x04df }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04df }
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r6.<init>()     // Catch:{ Exception -> 0x04df }
                r6.append(r15)     // Catch:{ Exception -> 0x04df }
                r6.append(r5)     // Catch:{ Exception -> 0x04df }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x04df }
                r7 = 0
                java.lang.CharSequence r4 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r4, r7, r6)     // Catch:{ Exception -> 0x04df }
                r3.name = r4     // Catch:{ Exception -> 0x04df }
            L_0x01f2:
                r3.object = r0     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.tgnet.TLRPC$Dialog r4 = r3.dialog     // Catch:{ Exception -> 0x04df }
                int r6 = r0.id     // Catch:{ Exception -> 0x04df }
                long r6 = (long) r6     // Catch:{ Exception -> 0x04df }
                r4.id = r6     // Catch:{ Exception -> 0x04df }
                int r9 = r9 + 1
                goto L_0x0222
            L_0x01ff:
                r24 = r0
                r25 = r3
                r26 = r4
                r27 = r6
                r22 = r7
                goto L_0x0222
            L_0x020a:
                r24 = r0
                r25 = r3
                r26 = r4
                r27 = r6
                int r7 = r7 + 1
                r5 = r22
                goto L_0x0129
            L_0x0218:
                r24 = r0
                r25 = r3
                r26 = r4
                r27 = r6
                r23 = r12
            L_0x0222:
                r18 = r2
                r2 = r20
                r7 = r21
                r6 = r27
                goto L_0x00f4
            L_0x022c:
                r20 = r2
                r27 = r6
                r21 = r7
                r2 = r18
                r11.dispose()     // Catch:{ Exception -> 0x04df }
                goto L_0x0242
            L_0x0238:
                r19 = r0
                r20 = r2
                r27 = r6
                r21 = r7
                r2 = r18
            L_0x0242:
                boolean r0 = r8.isEmpty()     // Catch:{ Exception -> 0x04df }
                if (r0 != 0) goto L_0x0343
                im.bclpbkiauv.ui.components.ShareAlert r0 = im.bclpbkiauv.ui.components.ShareAlert.this     // Catch:{ Exception -> 0x04df }
                int r0 = r0.currentAccount     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.messenger.MessagesStorage r0 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x04df }
                java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x04df }
                java.lang.String r4 = "SELECT data, name FROM chats WHERE uid IN(%s)"
                r5 = 1
                java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x04df }
                java.lang.String r5 = android.text.TextUtils.join(r14, r8)     // Catch:{ Exception -> 0x04df }
                r7 = 0
                r6[r7] = r5     // Catch:{ Exception -> 0x04df }
                java.lang.String r3 = java.lang.String.format(r3, r4, r6)     // Catch:{ Exception -> 0x04df }
                java.lang.Object[] r4 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.sqlite.SQLiteCursor r0 = r0.queryFinalized(r3, r4)     // Catch:{ Exception -> 0x04df }
                r11 = r0
            L_0x026f:
                boolean r0 = r11.next()     // Catch:{ Exception -> 0x04df }
                if (r0 == 0) goto L_0x0340
                r0 = 1
                java.lang.String r3 = r11.stringValue(r0)     // Catch:{ Exception -> 0x04df }
                r0 = r3
                im.bclpbkiauv.messenger.LocaleController r3 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x04df }
                java.lang.String r3 = r3.getTranslitString(r0)     // Catch:{ Exception -> 0x04df }
                boolean r4 = r0.equals(r3)     // Catch:{ Exception -> 0x04df }
                if (r4 == 0) goto L_0x028a
                r3 = 0
            L_0x028a:
                r4 = 0
            L_0x028b:
                int r5 = r2.length     // Catch:{ Exception -> 0x04df }
                if (r4 >= r5) goto L_0x033b
                r5 = r2[r4]     // Catch:{ Exception -> 0x04df }
                boolean r6 = r0.startsWith(r5)     // Catch:{ Exception -> 0x04df }
                if (r6 != 0) goto L_0x02cc
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r6.<init>()     // Catch:{ Exception -> 0x04df }
                r6.append(r13)     // Catch:{ Exception -> 0x04df }
                r6.append(r5)     // Catch:{ Exception -> 0x04df }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x04df }
                boolean r6 = r0.contains(r6)     // Catch:{ Exception -> 0x04df }
                if (r6 != 0) goto L_0x02cc
                if (r3 == 0) goto L_0x02c9
                boolean r6 = r3.startsWith(r5)     // Catch:{ Exception -> 0x04df }
                if (r6 != 0) goto L_0x02cc
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r6.<init>()     // Catch:{ Exception -> 0x04df }
                r6.append(r13)     // Catch:{ Exception -> 0x04df }
                r6.append(r5)     // Catch:{ Exception -> 0x04df }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x04df }
                boolean r6 = r3.contains(r6)     // Catch:{ Exception -> 0x04df }
                if (r6 == 0) goto L_0x02c9
                goto L_0x02cc
            L_0x02c9:
                int r4 = r4 + 1
                goto L_0x028b
            L_0x02cc:
                r6 = 0
                im.bclpbkiauv.tgnet.NativeByteBuffer r7 = r11.byteBufferValue(r6)     // Catch:{ Exception -> 0x04df }
                if (r7 == 0) goto L_0x0335
                int r12 = r7.readInt32(r6)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.tgnet.TLRPC$Chat r12 = im.bclpbkiauv.tgnet.TLRPC.Chat.TLdeserialize(r7, r12, r6)     // Catch:{ Exception -> 0x04df }
                r6 = r12
                r7.reuse()     // Catch:{ Exception -> 0x04df }
                if (r6 == 0) goto L_0x032d
                boolean r12 = im.bclpbkiauv.messenger.ChatObject.isNotInChat(r6)     // Catch:{ Exception -> 0x04df }
                if (r12 != 0) goto L_0x032d
                boolean r12 = im.bclpbkiauv.messenger.ChatObject.isChannel(r6)     // Catch:{ Exception -> 0x04df }
                if (r12 == 0) goto L_0x0306
                boolean r12 = r6.creator     // Catch:{ Exception -> 0x04df }
                if (r12 != 0) goto L_0x0306
                im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r12 = r6.admin_rights     // Catch:{ Exception -> 0x04df }
                if (r12 == 0) goto L_0x02fb
                im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r12 = r6.admin_rights     // Catch:{ Exception -> 0x04df }
                boolean r12 = r12.post_messages     // Catch:{ Exception -> 0x04df }
                if (r12 != 0) goto L_0x0306
            L_0x02fb:
                boolean r12 = r6.megagroup     // Catch:{ Exception -> 0x04df }
                if (r12 == 0) goto L_0x0300
                goto L_0x0306
            L_0x0300:
                r14 = r3
                r18 = r4
                r22 = r5
                goto L_0x0334
            L_0x0306:
                int r12 = r6.id     // Catch:{ Exception -> 0x04df }
                r14 = r3
                r18 = r4
                long r3 = (long) r12     // Catch:{ Exception -> 0x04df }
                long r3 = -r3
                java.lang.Object r3 = r10.get(r3)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.ui.components.ShareAlert$ShareSearchAdapter$DialogSearchResult r3 = (im.bclpbkiauv.ui.components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r3     // Catch:{ Exception -> 0x04df }
                java.lang.String r4 = r6.title     // Catch:{ Exception -> 0x04df }
                r12 = 0
                java.lang.CharSequence r4 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r4, r12, r5)     // Catch:{ Exception -> 0x04df }
                r3.name = r4     // Catch:{ Exception -> 0x04df }
                r3.object = r6     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.tgnet.TLRPC$Dialog r4 = r3.dialog     // Catch:{ Exception -> 0x04df }
                int r12 = r6.id     // Catch:{ Exception -> 0x04df }
                int r12 = -r12
                r22 = r5
                r23 = r6
                long r5 = (long) r12     // Catch:{ Exception -> 0x04df }
                r4.id = r5     // Catch:{ Exception -> 0x04df }
                int r9 = r9 + 1
                goto L_0x0334
            L_0x032d:
                r14 = r3
                r18 = r4
                r22 = r5
                r23 = r6
            L_0x0334:
                goto L_0x033e
            L_0x0335:
                r14 = r3
                r18 = r4
                r22 = r5
                goto L_0x033e
            L_0x033b:
                r14 = r3
                r18 = r4
            L_0x033e:
                goto L_0x026f
            L_0x0340:
                r11.dispose()     // Catch:{ Exception -> 0x04df }
            L_0x0343:
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04df }
                r0.<init>(r9)     // Catch:{ Exception -> 0x04df }
                r3 = 0
            L_0x0349:
                int r4 = r10.size()     // Catch:{ Exception -> 0x04df }
                if (r3 >= r4) goto L_0x0363
                java.lang.Object r4 = r10.valueAt(r3)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.ui.components.ShareAlert$ShareSearchAdapter$DialogSearchResult r4 = (im.bclpbkiauv.ui.components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r4     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.tgnet.TLObject r5 = r4.object     // Catch:{ Exception -> 0x04df }
                if (r5 == 0) goto L_0x0360
                java.lang.CharSequence r5 = r4.name     // Catch:{ Exception -> 0x04df }
                if (r5 == 0) goto L_0x0360
                r0.add(r4)     // Catch:{ Exception -> 0x04df }
            L_0x0360:
                int r3 = r3 + 1
                goto L_0x0349
            L_0x0363:
                im.bclpbkiauv.ui.components.ShareAlert r3 = im.bclpbkiauv.ui.components.ShareAlert.this     // Catch:{ Exception -> 0x04df }
                int r3 = r3.currentAccount     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.messenger.MessagesStorage r3 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r3)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.sqlite.SQLiteDatabase r3 = r3.getDatabase()     // Catch:{ Exception -> 0x04df }
                java.lang.String r4 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid"
                r5 = 0
                java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.sqlite.SQLiteCursor r3 = r3.queryFinalized(r4, r6)     // Catch:{ Exception -> 0x04df }
            L_0x037a:
                boolean r4 = r3.next()     // Catch:{ Exception -> 0x04df }
                if (r4 == 0) goto L_0x04c9
                r4 = 3
                int r4 = r3.intValue(r4)     // Catch:{ Exception -> 0x04df }
                long r5 = (long) r4     // Catch:{ Exception -> 0x04df }
                int r5 = r10.indexOfKey(r5)     // Catch:{ Exception -> 0x04df }
                if (r5 < 0) goto L_0x038d
                goto L_0x037a
            L_0x038d:
                r5 = 2
                java.lang.String r6 = r3.stringValue(r5)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.messenger.LocaleController r7 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x04df }
                java.lang.String r7 = r7.getTranslitString(r6)     // Catch:{ Exception -> 0x04df }
                boolean r11 = r6.equals(r7)     // Catch:{ Exception -> 0x04df }
                if (r11 == 0) goto L_0x03a1
                r7 = 0
            L_0x03a1:
                r11 = 0
                r12 = r27
                int r14 = r6.lastIndexOf(r12)     // Catch:{ Exception -> 0x04df }
                r5 = -1
                if (r14 == r5) goto L_0x03b2
                int r5 = r14 + 3
                java.lang.String r5 = r6.substring(r5)     // Catch:{ Exception -> 0x04df }
                r11 = r5
            L_0x03b2:
                r5 = 0
                r18 = r4
                int r4 = r2.length     // Catch:{ Exception -> 0x04df }
                r22 = r5
                r5 = 0
            L_0x03b9:
                if (r5 >= r4) goto L_0x04b3
                r23 = r2[r5]     // Catch:{ Exception -> 0x04df }
                r24 = r23
                r23 = r2
                r2 = r24
                boolean r24 = r6.startsWith(r2)     // Catch:{ Exception -> 0x04df }
                if (r24 != 0) goto L_0x040b
                r24 = r4
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r4.<init>()     // Catch:{ Exception -> 0x04df }
                r4.append(r13)     // Catch:{ Exception -> 0x04df }
                r4.append(r2)     // Catch:{ Exception -> 0x04df }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04df }
                boolean r4 = r6.contains(r4)     // Catch:{ Exception -> 0x04df }
                if (r4 != 0) goto L_0x040d
                if (r7 == 0) goto L_0x03fe
                boolean r4 = r7.startsWith(r2)     // Catch:{ Exception -> 0x04df }
                if (r4 != 0) goto L_0x040d
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r4.<init>()     // Catch:{ Exception -> 0x04df }
                r4.append(r13)     // Catch:{ Exception -> 0x04df }
                r4.append(r2)     // Catch:{ Exception -> 0x04df }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04df }
                boolean r4 = r7.contains(r4)     // Catch:{ Exception -> 0x04df }
                if (r4 == 0) goto L_0x03fe
                goto L_0x040d
            L_0x03fe:
                if (r11 == 0) goto L_0x0408
                boolean r4 = r11.startsWith(r2)     // Catch:{ Exception -> 0x04df }
                if (r4 == 0) goto L_0x0408
                r4 = 2
                goto L_0x040e
            L_0x0408:
                r4 = r22
                goto L_0x040e
            L_0x040b:
                r24 = r4
            L_0x040d:
                r4 = 1
            L_0x040e:
                if (r4 == 0) goto L_0x049b
                r5 = 0
                im.bclpbkiauv.tgnet.NativeByteBuffer r17 = r3.byteBufferValue(r5)     // Catch:{ Exception -> 0x04df }
                r22 = r17
                r5 = r22
                if (r5 == 0) goto L_0x048e
                r25 = r6
                r17 = r7
                r6 = 0
                int r7 = r5.readInt32(r6)     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.tgnet.TLRPC$User r7 = im.bclpbkiauv.tgnet.TLRPC.User.TLdeserialize(r5, r7, r6)     // Catch:{ Exception -> 0x04df }
                r5.reuse()     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.ui.components.ShareAlert$ShareSearchAdapter$DialogSearchResult r6 = new im.bclpbkiauv.ui.components.ShareAlert$ShareSearchAdapter$DialogSearchResult     // Catch:{ Exception -> 0x04df }
                r24 = r5
                r5 = 0
                r6.<init>()     // Catch:{ Exception -> 0x04df }
                r5 = r6
                im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r7.status     // Catch:{ Exception -> 0x04df }
                if (r6 == 0) goto L_0x0446
                im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r7.status     // Catch:{ Exception -> 0x04df }
                r26 = r8
                r27 = r9
                r8 = 1
                int r9 = r3.intValue(r8)     // Catch:{ Exception -> 0x04df }
                r6.expires = r9     // Catch:{ Exception -> 0x04df }
                goto L_0x044a
            L_0x0446:
                r26 = r8
                r27 = r9
            L_0x044a:
                im.bclpbkiauv.tgnet.TLRPC$Dialog r6 = r5.dialog     // Catch:{ Exception -> 0x04df }
                int r8 = r7.id     // Catch:{ Exception -> 0x04df }
                long r8 = (long) r8     // Catch:{ Exception -> 0x04df }
                r6.id = r8     // Catch:{ Exception -> 0x04df }
                r5.object = r7     // Catch:{ Exception -> 0x04df }
                r6 = 1
                if (r4 != r6) goto L_0x0462
                java.lang.String r8 = r7.first_name     // Catch:{ Exception -> 0x04df }
                java.lang.String r9 = r7.last_name     // Catch:{ Exception -> 0x04df }
                java.lang.CharSequence r8 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r8, r9, r2)     // Catch:{ Exception -> 0x04df }
                r5.name = r8     // Catch:{ Exception -> 0x04df }
                r6 = 0
                goto L_0x0489
            L_0x0462:
                java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r8.<init>()     // Catch:{ Exception -> 0x04df }
                r8.append(r15)     // Catch:{ Exception -> 0x04df }
                java.lang.String r9 = r7.username     // Catch:{ Exception -> 0x04df }
                r8.append(r9)     // Catch:{ Exception -> 0x04df }
                java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x04df }
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04df }
                r9.<init>()     // Catch:{ Exception -> 0x04df }
                r9.append(r15)     // Catch:{ Exception -> 0x04df }
                r9.append(r2)     // Catch:{ Exception -> 0x04df }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x04df }
                r6 = 0
                java.lang.CharSequence r8 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r8, r6, r9)     // Catch:{ Exception -> 0x04df }
                r5.name = r8     // Catch:{ Exception -> 0x04df }
            L_0x0489:
                r0.add(r5)     // Catch:{ Exception -> 0x04df }
                r7 = 0
                goto L_0x04bf
            L_0x048e:
                r24 = r5
                r25 = r6
                r17 = r7
                r26 = r8
                r27 = r9
                r6 = 0
                r7 = 0
                goto L_0x04bf
            L_0x049b:
                r25 = r6
                r17 = r7
                r26 = r8
                r27 = r9
                r6 = 0
                r7 = 0
                int r5 = r5 + 1
                r22 = r4
                r7 = r17
                r2 = r23
                r4 = r24
                r6 = r25
                goto L_0x03b9
            L_0x04b3:
                r23 = r2
                r25 = r6
                r17 = r7
                r26 = r8
                r27 = r9
                r6 = 0
                r7 = 0
            L_0x04bf:
                r2 = r23
                r8 = r26
                r9 = r27
                r27 = r12
                goto L_0x037a
            L_0x04c9:
                r23 = r2
                r26 = r8
                r27 = r9
                r3.dispose()     // Catch:{ Exception -> 0x04df }
                im.bclpbkiauv.ui.components.-$$Lambda$ShareAlert$ShareSearchAdapter$vHFLc6BH2jaoPK3jEoHOcz4bZxo r2 = im.bclpbkiauv.ui.components.$$Lambda$ShareAlert$ShareSearchAdapter$vHFLc6BH2jaoPK3jEoHOcz4bZxo.INSTANCE     // Catch:{ Exception -> 0x04df }
                java.util.Collections.sort(r0, r2)     // Catch:{ Exception -> 0x04df }
                r2 = r30
                r1.updateSearchResults(r0, r2)     // Catch:{ Exception -> 0x04dd }
                goto L_0x04e5
            L_0x04dd:
                r0 = move-exception
                goto L_0x04e2
            L_0x04df:
                r0 = move-exception
                r2 = r30
            L_0x04e2:
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x04e5:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ShareAlert.ShareSearchAdapter.lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(java.lang.String, int):void");
        }

        static /* synthetic */ int lambda$null$0(DialogSearchResult lhs, DialogSearchResult rhs) {
            if (lhs.date < rhs.date) {
                return 1;
            }
            if (lhs.date > rhs.date) {
                return -1;
            }
            return 0;
        }

        private void updateSearchResults(ArrayList<DialogSearchResult> result, int searchId) {
            AndroidUtilities.runOnUIThread(new Runnable(searchId, result) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$updateSearchResults$2$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$2$ShareAlert$ShareSearchAdapter(int searchId, ArrayList result) {
            boolean isEmpty;
            if (searchId == this.lastSearchId) {
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                int a = 0;
                while (true) {
                    isEmpty = true;
                    if (a >= result.size()) {
                        break;
                    }
                    DialogSearchResult obj = (DialogSearchResult) result.get(a);
                    if (obj.object instanceof TLRPC.User) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putUser((TLRPC.User) obj.object, true);
                    } else if (obj.object instanceof TLRPC.Chat) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putChat((TLRPC.Chat) obj.object, true);
                    }
                    a++;
                }
                boolean becomeEmpty = !this.searchResult.isEmpty() && result.isEmpty();
                if (!this.searchResult.isEmpty() || !result.isEmpty()) {
                    isEmpty = false;
                }
                if (becomeEmpty) {
                    ShareAlert shareAlert2 = ShareAlert.this;
                    int unused2 = shareAlert2.topBeforeSwitch = shareAlert2.getCurrentTop();
                }
                this.searchResult = result;
                notifyDataSetChanged();
                if (!isEmpty && !becomeEmpty && ShareAlert.this.topBeforeSwitch > 0) {
                    ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                    int unused3 = ShareAlert.this.topBeforeSwitch = -1000;
                }
                ShareAlert.this.searchEmptyView.showTextView();
            }
        }

        public void searchDialogs(String query) {
            if (query == null || !query.equals(this.lastSearchText)) {
                this.lastSearchText = query;
                if (this.searchRunnable != null) {
                    Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                    this.searchRunnable = null;
                }
                if (query == null || query.length() == 0) {
                    this.searchResult.clear();
                    ShareAlert shareAlert = ShareAlert.this;
                    int unused = shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    notifyDataSetChanged();
                    return;
                }
                int searchId = this.lastSearchId + 1;
                this.lastSearchId = searchId;
                this.searchRunnable = new Runnable(query, searchId) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        ShareAlert.ShareSearchAdapter.this.lambda$searchDialogs$3$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                    }
                };
                Utilities.searchQueue.postRunnable(this.searchRunnable, 300);
            }
        }

        public int getItemCount() {
            int count = this.searchResult.size();
            if (count != 0) {
                return count + 1;
            }
            return count;
        }

        public TLRPC.Dialog getItem(int position) {
            int position2 = position - 1;
            if (position2 < 0 || position2 >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(position2).dialog;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 1) {
                return false;
            }
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new ShareDialogCell(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ShareDialogCell cell = (ShareDialogCell) holder.itemView;
                DialogSearchResult result = this.searchResult.get(position - 1);
                cell.setDialog((int) result.dialog.id, ShareAlert.this.selectedDialogs.indexOfKey(result.dialog.id) >= 0, result.name);
            }
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            return 0;
        }
    }
}
