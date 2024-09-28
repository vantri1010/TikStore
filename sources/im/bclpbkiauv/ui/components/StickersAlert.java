package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.FileRefController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ContentPreviewViewer;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.cells.FeaturedStickerSetInfoCell;
import im.bclpbkiauv.ui.cells.StickerEmojiCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StickersAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public GridAdapter adapter;
    /* access modifiers changed from: private */
    public boolean clearsInputField;
    /* access modifiers changed from: private */
    public StickersAlertDelegate delegate;
    /* access modifiers changed from: private */
    public FrameLayout emptyView;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    private TLRPC.InputStickerSet inputStickerSet;
    private StickersAlertInstallDelegate installDelegate;
    /* access modifiers changed from: private */
    public int itemSize;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private ActionBarMenuItem optionsButton;
    private Activity parentActivity;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    private TextView pickerBottomLayout;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate previewDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
        public /* synthetic */ void gifAddedOrDeleted() {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
        }

        public /* synthetic */ void sendGif(Object obj, boolean z, int i) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, z, i);
        }

        public void sendSticker(TLRPC.Document sticker, Object parent, boolean notify, int scheduleDate) {
            if (StickersAlert.this.delegate != null) {
                StickersAlert.this.delegate.onStickerSelected(sticker, parent, StickersAlert.this.clearsInputField, notify, scheduleDate);
                StickersAlert.this.dismiss();
            }
        }

        public boolean canSchedule() {
            return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
        }

        public boolean isInScheduleMode() {
            return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
        }

        public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
        }

        public boolean needSend() {
            return StickersAlert.this.previewSendButton.getVisibility() == 0;
        }

        public boolean needOpen() {
            return false;
        }
    };
    /* access modifiers changed from: private */
    public TextView previewSendButton;
    private View previewSendButtonShadow;
    private int reqId;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private TLRPC.Document selectedSticker;
    /* access modifiers changed from: private */
    public View[] shadow = new View[2];
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    /* access modifiers changed from: private */
    public boolean showEmoji;
    private TextView stickerEmojiTextView;
    private BackupImageView stickerImageView;
    /* access modifiers changed from: private */
    public FrameLayout stickerPreviewLayout;
    /* access modifiers changed from: private */
    public TLRPC.TL_messages_stickerSet stickerSet;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.StickerSetCovered> stickerSetCovereds;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private TextView titleTextView;
    private Pattern urlPattern;

    public interface StickersAlertDelegate {
        boolean canSchedule();

        boolean isInScheduleMode();

        void onStickerSelected(TLRPC.Document document, Object obj, boolean z, boolean z2, int i);
    }

    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public StickersAlert(Context context, Object parentObject, TLRPC.Photo photo) {
        super(context, false, 1);
        this.parentActivity = (Activity) context;
        TLRPC.TL_messages_getAttachedStickers req = new TLRPC.TL_messages_getAttachedStickers();
        TLRPC.TL_inputStickeredMediaPhoto inputStickeredMediaPhoto = new TLRPC.TL_inputStickeredMediaPhoto();
        inputStickeredMediaPhoto.id = new TLRPC.TL_inputPhoto();
        inputStickeredMediaPhoto.id.id = photo.id;
        inputStickeredMediaPhoto.id.access_hash = photo.access_hash;
        inputStickeredMediaPhoto.id.file_reference = photo.file_reference;
        if (inputStickeredMediaPhoto.id.file_reference == null) {
            inputStickeredMediaPhoto.id.file_reference = new byte[0];
        }
        req.media = inputStickeredMediaPhoto;
        this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(parentObject, req, new RequestDelegate(req) {
            private final /* synthetic */ TLRPC.TL_messages_getAttachedStickers f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersAlert.this.lambda$new$1$StickersAlert(this.f$1, tLObject, tL_error);
            }
        }) {
            private final /* synthetic */ Object f$1;
            private final /* synthetic */ TLRPC.TL_messages_getAttachedStickers f$2;
            private final /* synthetic */ RequestDelegate f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersAlert.this.lambda$new$2$StickersAlert(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        });
        init(context);
    }

    public /* synthetic */ void lambda$new$1$StickersAlert(TLRPC.TL_messages_getAttachedStickers req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, req) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_messages_getAttachedStickers f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                StickersAlert.this.lambda$null$0$StickersAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$StickersAlert(TLRPC.TL_error error, TLObject response, TLRPC.TL_messages_getAttachedStickers req) {
        this.reqId = 0;
        if (error == null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            if (vector.objects.isEmpty()) {
                dismiss();
            } else if (vector.objects.size() == 1) {
                TLRPC.StickerSetCovered set = (TLRPC.StickerSetCovered) vector.objects.get(0);
                TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
                this.inputStickerSet = tL_inputStickerSetID;
                tL_inputStickerSetID.id = set.set.id;
                this.inputStickerSet.access_hash = set.set.access_hash;
                loadStickerSet();
            } else {
                this.stickerSetCovereds = new ArrayList<>();
                for (int a = 0; a < vector.objects.size(); a++) {
                    this.stickerSetCovereds.add((TLRPC.StickerSetCovered) vector.objects.get(a));
                }
                this.gridView.setLayoutParams(LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                this.titleTextView.setVisibility(8);
                this.shadow[0].setVisibility(8);
                this.adapter.notifyDataSetChanged();
            }
        } else {
            AlertsCreator.processError(this.currentAccount, error, this.parentFragment, req, new Object[0]);
            dismiss();
        }
    }

    public /* synthetic */ void lambda$new$2$StickersAlert(Object parentObject, TLRPC.TL_messages_getAttachedStickers req, RequestDelegate requestDelegate, TLObject response, TLRPC.TL_error error) {
        if (error == null || !FileRefController.isFileRefError(error.text) || parentObject == null) {
            requestDelegate.run(response, error);
            return;
        }
        FileRefController.getInstance(this.currentAccount).requestReference(parentObject, req, requestDelegate);
    }

    public StickersAlert(Context context, BaseFragment baseFragment, TLRPC.InputStickerSet set, TLRPC.TL_messages_stickerSet loadedSet, StickersAlertDelegate stickersAlertDelegate) {
        super(context, false, 1);
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = set;
        this.stickerSet = loadedSet;
        this.parentFragment = baseFragment;
        loadStickerSet();
        init(context);
    }

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 2);
    }

    public void setClearsInputField(boolean value) {
        this.clearsInputField = value;
    }

    public boolean isClearsInputField() {
        return this.clearsInputField;
    }

    private void loadStickerSet() {
        TLRPC.InputStickerSet inputStickerSet2 = this.inputStickerSet;
        if (inputStickerSet2 != null) {
            if (this.stickerSet == null && inputStickerSet2.short_name != null) {
                this.stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(this.inputStickerSet.short_name);
            }
            if (this.stickerSet == null) {
                this.stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSetById(this.inputStickerSet.id);
            }
            if (this.stickerSet == null) {
                TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
                req.stickerset = this.inputStickerSet;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StickersAlert.this.lambda$loadStickerSet$4$StickersAlert(tLObject, tL_error);
                    }
                });
            } else if (this.adapter != null) {
                updateSendButton();
                updateFields();
                this.adapter.notifyDataSetChanged();
            }
        }
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSet;
        if (tL_messages_stickerSet != null) {
            this.showEmoji = !tL_messages_stickerSet.set.masks;
        }
    }

    public /* synthetic */ void lambda$loadStickerSet$4$StickersAlert(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                StickersAlert.this.lambda$null$3$StickersAlert(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$StickersAlert(TLRPC.TL_error error, TLObject response) {
        this.reqId = 0;
        if (error == null) {
            this.optionsButton.setVisibility(0);
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) response;
            this.stickerSet = tL_messages_stickerSet;
            this.showEmoji = !tL_messages_stickerSet.set.masks;
            updateSendButton();
            updateFields();
            this.adapter.notifyDataSetChanged();
            return;
        }
        ToastUtils.show((int) R.string.AddStickersNotFound);
        dismiss();
    }

    private void init(Context context) {
        Context context2 = context;
        this.containerView = new FrameLayout(context2) {
            private boolean fullHeight;
            private int lastNotifyWidth;
            private RectF rect = new RectF();

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || StickersAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) StickersAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(ev);
                }
                StickersAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !StickersAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int contentSize;
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                boolean z = true;
                if (Build.VERSION.SDK_INT >= 21) {
                    boolean unused = StickersAlert.this.ignoreLayout = true;
                    setPadding(StickersAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, StickersAlert.this.backgroundPaddingLeft, 0);
                    boolean unused2 = StickersAlert.this.ignoreLayout = false;
                }
                int unused3 = StickersAlert.this.itemSize = (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(36.0f)) / 5;
                if (StickersAlert.this.stickerSetCovereds != null) {
                    contentSize = AndroidUtilities.dp(56.0f) + (AndroidUtilities.dp(60.0f) * StickersAlert.this.stickerSetCovereds.size()) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop + AndroidUtilities.dp(24.0f);
                } else {
                    contentSize = AndroidUtilities.dp(96.0f) + (Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f)) : 0) * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop + AndroidUtilities.statusBarHeight;
                }
                int padding = ((double) contentSize) < ((double) (height / 5)) * 3.2d ? 0 : (height / 5) * 2;
                if (padding != 0 && contentSize < height) {
                    padding -= height - contentSize;
                }
                if (padding == 0) {
                    padding = StickersAlert.this.backgroundPaddingTop;
                }
                if (StickersAlert.this.stickerSetCovereds != null) {
                    padding += AndroidUtilities.dp(8.0f);
                }
                if (StickersAlert.this.gridView.getPaddingTop() != padding) {
                    boolean unused4 = StickersAlert.this.ignoreLayout = true;
                    StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), padding, AndroidUtilities.dp(10.0f), 0);
                    StickersAlert.this.emptyView.setPadding(0, padding, 0, 0);
                    boolean unused5 = StickersAlert.this.ignoreLayout = false;
                }
                if (contentSize < height) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), 1073741824));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                if (this.lastNotifyWidth != right - left) {
                    this.lastNotifyWidth = right - left;
                    if (!(StickersAlert.this.adapter == null || StickersAlert.this.stickerSetCovereds == null)) {
                        StickersAlert.this.adapter.notifyDataSetChanged();
                    }
                }
                super.onLayout(changed, left, top, right, bottom);
                StickersAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float radProgress;
                int statusBarHeight;
                int height;
                int top;
                int y;
                Canvas canvas2 = canvas;
                int y2 = (StickersAlert.this.scrollOffsetY - StickersAlert.this.backgroundPaddingTop) + AndroidUtilities.dp(6.0f);
                int top2 = (StickersAlert.this.scrollOffsetY - StickersAlert.this.backgroundPaddingTop) - AndroidUtilities.dp(13.0f);
                int height2 = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + StickersAlert.this.backgroundPaddingTop;
                float radProgress2 = 1.0f;
                if (Build.VERSION.SDK_INT >= 21) {
                    int top3 = top2 + AndroidUtilities.statusBarHeight;
                    int y3 = y2 + AndroidUtilities.statusBarHeight;
                    int height3 = height2 - AndroidUtilities.statusBarHeight;
                    if (this.fullHeight) {
                        if (StickersAlert.this.backgroundPaddingTop + top3 < AndroidUtilities.statusBarHeight * 2) {
                            int diff = Math.min(AndroidUtilities.statusBarHeight, ((AndroidUtilities.statusBarHeight * 2) - top3) - StickersAlert.this.backgroundPaddingTop);
                            top3 -= diff;
                            height3 += diff;
                            radProgress2 = 1.0f - Math.min(1.0f, ((float) (diff * 2)) / ((float) AndroidUtilities.statusBarHeight));
                        }
                        if (StickersAlert.this.backgroundPaddingTop + top3 < AndroidUtilities.statusBarHeight) {
                            y = y3;
                            top = top3;
                            height = height3;
                            statusBarHeight = Math.min(AndroidUtilities.statusBarHeight, (AndroidUtilities.statusBarHeight - top3) - StickersAlert.this.backgroundPaddingTop);
                            radProgress = radProgress2;
                        } else {
                            y = y3;
                            top = top3;
                            height = height3;
                            statusBarHeight = 0;
                            radProgress = radProgress2;
                        }
                    } else {
                        y = y3;
                        top = top3;
                        height = height3;
                        statusBarHeight = 0;
                        radProgress = 1.0f;
                    }
                } else {
                    y = y2;
                    top = top2;
                    height = height2;
                    statusBarHeight = 0;
                    radProgress = 1.0f;
                }
                StickersAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                StickersAlert.this.shadowDrawable.draw(canvas2);
                if (radProgress != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_dialogBackground));
                    this.rect.set((float) StickersAlert.this.backgroundPaddingLeft, (float) (StickersAlert.this.backgroundPaddingTop + top), (float) (getMeasuredWidth() - StickersAlert.this.backgroundPaddingLeft), (float) (StickersAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f)));
                    canvas2.drawRoundRect(this.rect, ((float) AndroidUtilities.dp(12.0f)) * radProgress, ((float) AndroidUtilities.dp(12.0f)) * radProgress, Theme.dialogs_onlineCirclePaint);
                }
                int w = AndroidUtilities.dp(36.0f);
                this.rect.set((float) ((getMeasuredWidth() - w) / 2), (float) y, (float) ((getMeasuredWidth() + w) / 2), (float) (AndroidUtilities.dp(4.0f) + y));
                Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_sheet_scrollUp));
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                if (statusBarHeight > 0) {
                    int color1 = Theme.getColor(Theme.key_dialogBackground);
                    Theme.dialogs_onlineCirclePaint.setColor(Color.argb(255, (int) (((float) Color.red(color1)) * 0.8f), (int) (((float) Color.green(color1)) * 0.8f), (int) (((float) Color.blue(color1)) * 0.8f)));
                    canvas.drawRect((float) StickersAlert.this.backgroundPaddingLeft, (float) (AndroidUtilities.statusBarHeight - statusBarHeight), (float) (getMeasuredWidth() - StickersAlert.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
                }
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(48.0f);
        this.shadow[0] = new View(context2);
        this.shadow[0].setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(1);
        this.containerView.addView(this.shadow[0], frameLayoutParams);
        AnonymousClass3 r6 = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, StickersAlert.this.gridView, 0, StickersAlert.this.previewDelegate);
                if (super.onInterceptTouchEvent(event) || result) {
                    return true;
                }
                return false;
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.gridView = r6;
        r6.setTag(14);
        RecyclerListView recyclerListView = this.gridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        this.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                if ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(position) instanceof Integer)) && position != StickersAlert.this.adapter.totalItems) {
                    return 1;
                }
                return StickersAlert.this.adapter.stickersPerRow;
            }
        });
        RecyclerListView recyclerListView2 = this.gridView;
        GridAdapter gridAdapter = new GridAdapter(context2);
        this.adapter = gridAdapter;
        recyclerListView2.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
                outRect.top = 0;
            }
        });
        this.gridView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.gridView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return StickersAlert.this.lambda$init$5$StickersAlert(view, motionEvent);
            }
        });
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                StickersAlert.this.updateLayout();
            }
        });
        $$Lambda$StickersAlert$eGvVRaQDdbmRhfWa8f0GiiIrMo r62 = new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                StickersAlert.this.lambda$init$6$StickersAlert(view, i);
            }
        };
        this.stickersOnItemClickListener = r62;
        this.gridView.setOnItemClickListener((RecyclerListView.OnItemClickListener) r62);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        this.emptyView = new FrameLayout(context2) {
            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener($$Lambda$StickersAlert$FoOuxexU_cmQRMHTb2BuJky7y0.INSTANCE);
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        this.titleTextView.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.containerView.addView(this.titleTextView, LayoutHelper.createFrame(-1.0f, 50.0f, 51, 0.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, 0, Theme.getColor(Theme.key_sheet_other));
        this.optionsButton = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.optionsButton.setSubMenuOpenSide(2);
        this.optionsButton.setIcon((int) R.drawable.ic_ab_other);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_player_actionBarSelector), 1));
        this.containerView.addView(this.optionsButton, LayoutHelper.createFrame(40.0f, 40.0f, 53, 0.0f, 5.0f, 5.0f, 0.0f));
        this.optionsButton.addSubItem(1, (int) R.drawable.msg_share, (CharSequence) LocaleController.getString("StickersShare", R.string.StickersShare));
        this.optionsButton.addSubItem(2, (int) R.drawable.msg_link, (CharSequence) LocaleController.getString("CopyLink", R.string.CopyLink));
        this.optionsButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickersAlert.this.lambda$init$8$StickersAlert(view);
            }
        });
        this.optionsButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
            public final void onItemClick(int i) {
                StickersAlert.this.onSubItemClick(i);
            }
        });
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.optionsButton.setVisibility(this.inputStickerSet != null ? 0 : 8);
        this.emptyView.addView(new RadialProgressView(context2), LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout.LayoutParams frameLayoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        frameLayoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context2);
        this.shadow[1].setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.containerView.addView(this.shadow[1], frameLayoutParams2);
        TextView textView2 = new TextView(context2);
        this.pickerBottomLayout = textView2;
        textView2.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(Theme.getColor(Theme.key_dialogBackground), Theme.getColor(Theme.key_listSelector)));
        this.pickerBottomLayout.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.pickerBottomLayout.setTextSize(1, 14.0f);
        this.pickerBottomLayout.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pickerBottomLayout.setGravity(17);
        this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.stickerPreviewLayout = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground) & -536870913);
        this.stickerPreviewLayout.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.stickerPreviewLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickersAlert.this.lambda$init$9$StickersAlert(view);
            }
        });
        BackupImageView backupImageView = new BackupImageView(context2);
        this.stickerImageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.stickerImageView.setLayerNum(3);
        this.stickerPreviewLayout.addView(this.stickerImageView);
        TextView textView3 = new TextView(context2);
        this.stickerEmojiTextView = textView3;
        textView3.setTextSize(1, 30.0f);
        this.stickerEmojiTextView.setGravity(85);
        this.stickerPreviewLayout.addView(this.stickerEmojiTextView);
        TextView textView4 = new TextView(context2);
        this.previewSendButton = textView4;
        textView4.setTextSize(1, 14.0f);
        this.previewSendButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickersAlert.this.lambda$init$10$StickersAlert(view);
            }
        });
        FrameLayout.LayoutParams frameLayoutParams3 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        frameLayoutParams3.bottomMargin = AndroidUtilities.dp(48.0f);
        View view = new View(context2);
        this.previewSendButtonShadow = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, frameLayoutParams3);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        updateFields();
        updateSendButton();
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ boolean lambda$init$5$StickersAlert(View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.gridView, 0, this.stickersOnItemClickListener, this.previewDelegate);
    }

    public /* synthetic */ void lambda$init$6$StickersAlert(View view, int position) {
        if (this.stickerSetCovereds != null) {
            TLRPC.StickerSetCovered pack = (TLRPC.StickerSetCovered) this.adapter.positionsToSets.get(position);
            if (pack != null) {
                dismiss();
                TLRPC.TL_inputStickerSetID inputStickerSetID = new TLRPC.TL_inputStickerSetID();
                inputStickerSetID.access_hash = pack.set.access_hash;
                inputStickerSetID.id = pack.set.id;
                new StickersAlert(this.parentActivity, this.parentFragment, inputStickerSetID, (TLRPC.TL_messages_stickerSet) null, (StickersAlertDelegate) null).show();
                return;
            }
            return;
        }
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSet;
        if (tL_messages_stickerSet != null && position >= 0 && position < tL_messages_stickerSet.documents.size()) {
            this.selectedSticker = this.stickerSet.documents.get(position);
            boolean set = false;
            int a = 0;
            while (true) {
                if (a >= this.selectedSticker.attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute = this.selectedSticker.attributes.get(a);
                if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                    a++;
                } else if (attribute.alt != null && attribute.alt.length() > 0) {
                    this.stickerEmojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                    set = true;
                }
            }
            if (!set) {
                this.stickerEmojiTextView.setText(Emoji.replaceEmoji(MediaDataController.getInstance(this.currentAccount).getEmojiForSticker(this.selectedSticker.id), this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
            }
            this.stickerImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.selectedSticker), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(this.selectedSticker.thumbs, 90), this.selectedSticker), (String) null, "webp", (Object) this.stickerSet, 1);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.stickerPreviewLayout.getLayoutParams();
            layoutParams.topMargin = this.scrollOffsetY;
            this.stickerPreviewLayout.setLayoutParams(layoutParams);
            this.stickerPreviewLayout.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, new float[]{0.0f, 1.0f})});
            animatorSet.setDuration(200);
            animatorSet.start();
        }
    }

    static /* synthetic */ boolean lambda$init$7(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$init$8$StickersAlert(View v) {
        this.optionsButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$init$9$StickersAlert(View v) {
        hidePreview();
    }

    public /* synthetic */ void lambda$init$10$StickersAlert(View v) {
        this.delegate.onStickerSelected(this.selectedSticker, this.stickerSet, this.clearsInputField, true, 0);
        dismiss();
    }

    private void updateSendButton() {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        int size = (int) (((float) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2)) / AndroidUtilities.density);
        if (this.delegate == null || ((tL_messages_stickerSet = this.stickerSet) != null && tL_messages_stickerSet.set.masks)) {
            this.previewSendButton.setText(LocaleController.getString("Close", R.string.Close).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, size, 17));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, size, 17));
            this.previewSendButton.setVisibility(8);
            this.previewSendButtonShadow.setVisibility(8);
            return;
        }
        this.previewSendButton.setText(LocaleController.getString("SendSticker", R.string.SendSticker).toUpperCase());
        this.stickerImageView.setLayoutParams(LayoutHelper.createFrame((float) size, (float) size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame((float) size, (float) size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        this.previewSendButton.setVisibility(0);
        this.previewSendButtonShadow.setVisibility(0);
    }

    public void setInstallDelegate(StickersAlertInstallDelegate stickersAlertInstallDelegate) {
        this.installDelegate = stickersAlertInstallDelegate;
    }

    /* access modifiers changed from: private */
    public void onSubItemClick(int id) {
        if (this.stickerSet != null) {
            String stickersUrl = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/" + this.stickerSet.set.short_name;
            if (id == 1) {
                ShareAlert shareAlert = new ShareAlert(getContext(), (ArrayList<MessageObject>) null, stickersUrl, false, stickersUrl, false);
                BaseFragment baseFragment = this.parentFragment;
                if (baseFragment != null) {
                    baseFragment.showDialog(shareAlert);
                } else {
                    shareAlert.show();
                }
            } else if (id == 2) {
                try {
                    AndroidUtilities.addToClipboard(stickersUrl);
                    ToastUtils.show((int) R.string.LinkCopied);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    private void updateFields() {
        String text;
        String text2;
        if (this.titleTextView != null) {
            if (this.stickerSet != null) {
                SpannableStringBuilder stringBuilder = null;
                try {
                    if (this.urlPattern == null) {
                        this.urlPattern = Pattern.compile("@[a-zA-Z\\d_]{1,32}");
                    }
                    Matcher matcher = this.urlPattern.matcher(this.stickerSet.set.title);
                    while (matcher.find()) {
                        if (stringBuilder == null) {
                            stringBuilder = new SpannableStringBuilder(this.stickerSet.set.title);
                            this.titleTextView.setMovementMethod(new LinkMovementMethodMy());
                        }
                        int start = matcher.start();
                        int end = matcher.end();
                        if (this.stickerSet.set.title.charAt(start) != '@') {
                            start++;
                        }
                        stringBuilder.setSpan(new URLSpanNoUnderline(this.stickerSet.set.title.subSequence(start + 1, end).toString()) {
                            public void onClick(View widget) {
                                MessagesController.getInstance(StickersAlert.this.currentAccount).openByUserName(getURL(), StickersAlert.this.parentFragment, 1);
                                StickersAlert.this.dismiss();
                            }
                        }, start, end, 0);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                this.titleTextView.setText(stringBuilder != null ? stringBuilder : this.stickerSet.set.title);
                if (this.stickerSet.set == null || !MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(this.stickerSet.set.id)) {
                    if (this.stickerSet.set.masks) {
                        text = LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("MasksCount", this.stickerSet.documents.size())).toUpperCase();
                    } else {
                        text = LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("Stickers", this.stickerSet.documents.size())).toUpperCase();
                    }
                    setButton(new View.OnClickListener() {
                        public final void onClick(View view) {
                            StickersAlert.this.lambda$updateFields$13$StickersAlert(view);
                        }
                    }, text, Theme.getColor(Theme.key_dialogTextBlue2));
                } else {
                    if (this.stickerSet.set.masks) {
                        text2 = LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("MasksCount", this.stickerSet.documents.size())).toUpperCase();
                    } else {
                        text2 = LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("Stickers", this.stickerSet.documents.size())).toUpperCase();
                    }
                    if (this.stickerSet.set.official) {
                        setButton(new View.OnClickListener() {
                            public final void onClick(View view) {
                                StickersAlert.this.lambda$updateFields$14$StickersAlert(view);
                            }
                        }, text2, Theme.getColor(Theme.key_dialogTextRed));
                    } else {
                        setButton(new View.OnClickListener() {
                            public final void onClick(View view) {
                                StickersAlert.this.lambda$updateFields$15$StickersAlert(view);
                            }
                        }, text2, Theme.getColor(Theme.key_dialogTextRed));
                    }
                }
                this.adapter.notifyDataSetChanged();
                return;
            }
            setButton(new View.OnClickListener() {
                public final void onClick(View view) {
                    StickersAlert.this.lambda$updateFields$16$StickersAlert(view);
                }
            }, LocaleController.getString("Close", R.string.Close).toUpperCase(), Theme.getColor(Theme.key_dialogTextBlue2));
        }
    }

    public /* synthetic */ void lambda$updateFields$13$StickersAlert(View v) {
        dismiss();
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetInstalled();
        }
        TLRPC.TL_messages_installStickerSet req = new TLRPC.TL_messages_installStickerSet();
        req.stickerset = this.inputStickerSet;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersAlert.this.lambda$null$12$StickersAlert(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$null$12$StickersAlert(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                StickersAlert.this.lambda$null$11$StickersAlert(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$11$StickersAlert(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            try {
                if (response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[0]);
                    if (!(this.parentFragment == null || this.parentFragment.getParentActivity() == null)) {
                        this.parentFragment.showDialog(new StickersArchiveAlert(this.parentFragment.getParentActivity(), this.parentFragment, ((TLRPC.TL_messages_stickerSetInstallResultArchive) response).sets).create());
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            ToastUtils.show((int) R.string.ErrorOccurred);
        }
        MediaDataController.installingStickerSetId = this.stickerSet.set.id;
        MediaDataController.getInstance(this.currentAccount).loadStickers(this.stickerSet.set.masks ? 1 : 0, false, true);
    }

    public /* synthetic */ void lambda$updateFields$14$StickersAlert(View v) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).removeStickersSet(getContext(), this.stickerSet.set, 1, this.parentFragment, true);
    }

    public /* synthetic */ void lambda$updateFields$15$StickersAlert(View v) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).removeStickersSet(getContext(), this.stickerSet.set, 0, this.parentFragment, true);
    }

    public /* synthetic */ void lambda$updateFields$16$StickersAlert(View v) {
        dismiss();
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            if (this.stickerSetCovereds == null) {
                this.titleTextView.setTranslationY((float) this.scrollOffsetY);
                this.optionsButton.setTranslationY((float) this.scrollOffsetY);
                this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            }
            this.containerView.invalidate();
            return;
        }
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(0, true);
        } else {
            newOffset = top;
            runShadowAnimation(0, false);
        }
        if (this.scrollOffsetY != newOffset) {
            RecyclerListView recyclerListView2 = this.gridView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            if (this.stickerSetCovereds == null) {
                this.titleTextView.setTranslationY((float) this.scrollOffsetY);
                this.optionsButton.setTranslationY((float) this.scrollOffsetY);
                this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            }
            this.containerView.invalidate();
        }
    }

    private void hidePreview() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, new float[]{0.0f})});
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                StickersAlert.this.stickerPreviewLayout.setVisibility(8);
            }
        });
        animatorSet.start();
    }

    private void runShadowAnimation(final int num, final boolean show) {
        if (this.stickerSetCovereds == null) {
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
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                View view = this.shadow[num];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[num].setDuration(150);
                this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (StickersAlert.this.shadowAnimation[num] != null && StickersAlert.this.shadowAnimation[num].equals(animation)) {
                            if (!show) {
                                StickersAlert.this.shadow[num].setVisibility(4);
                            }
                            StickersAlert.this.shadowAnimation[num] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (StickersAlert.this.shadowAnimation[num] != null && StickersAlert.this.shadowAnimation[num].equals(animation)) {
                            StickersAlert.this.shadowAnimation[num] = null;
                        }
                    }
                });
                this.shadowAnimation[num].start();
            }
        }
    }

    public void dismiss() {
        super.dismiss();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 2);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.gridView;
            if (recyclerListView != null) {
                int count = recyclerListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    this.gridView.getChildAt(a).invalidate();
                }
            }
            if (ContentPreviewViewer.getInstance().isVisible()) {
                ContentPreviewViewer.getInstance().close();
            }
            ContentPreviewViewer.getInstance().reset();
        }
    }

    private void setButton(View.OnClickListener onClickListener, String title, int color) {
        this.pickerBottomLayout.setTextColor(color);
        this.pickerBottomLayout.setText(title.toUpperCase());
        this.pickerBottomLayout.setOnClickListener(onClickListener);
    }

    private class GridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private Context context;
        /* access modifiers changed from: private */
        public SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int stickersRowCount;
        /* access modifiers changed from: private */
        public int totalItems;

        public GridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public int getItemViewType(int position) {
            if (StickersAlert.this.stickerSetCovereds == null) {
                return 0;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            return 2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                AnonymousClass1 r1 = new StickerEmojiCell(this.context) {
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                    }
                };
                r1.getImageView().setLayerNum(3);
                view = r1;
            } else if (viewType == 1) {
                view = new EmptyCell(this.context);
            } else if (viewType == 2) {
                view = new FeaturedStickerSetInfoCell(this.context, 8);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                int itemViewType = holder.getItemViewType();
                if (itemViewType == 0) {
                    ((StickerEmojiCell) holder.itemView).setSticker((TLRPC.Document) this.cache.get(position), this.positionsToSets.get(position), false);
                } else if (itemViewType == 1) {
                    ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                } else if (itemViewType == 2) {
                    ((FeaturedStickerSetInfoCell) holder.itemView).setStickerSet((TLRPC.StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(((Integer) this.cache.get(position)).intValue()), false);
                }
            } else {
                ((StickerEmojiCell) holder.itemView).setSticker(StickersAlert.this.stickerSet.documents.get(position), StickersAlert.this.stickerSet, StickersAlert.this.showEmoji);
            }
        }

        public void notifyDataSetChanged() {
            int count;
            int i;
            int i2 = 0;
            if (StickersAlert.this.stickerSetCovereds != null) {
                int width = StickersAlert.this.gridView.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
                StickersAlert.this.layoutManager.setSpanCount(this.stickersPerRow);
                this.cache.clear();
                this.positionsToSets.clear();
                this.totalItems = 0;
                this.stickersRowCount = 0;
                for (int a = 0; a < StickersAlert.this.stickerSetCovereds.size(); a++) {
                    TLRPC.StickerSetCovered pack = (TLRPC.StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(a);
                    if (!pack.covers.isEmpty() || pack.cover != null) {
                        this.stickersRowCount = (int) (((double) this.stickersRowCount) + Math.ceil((double) (((float) StickersAlert.this.stickerSetCovereds.size()) / ((float) this.stickersPerRow))));
                        this.positionsToSets.put(this.totalItems, pack);
                        SparseArray<Object> sparseArray = this.cache;
                        int i3 = this.totalItems;
                        this.totalItems = i3 + 1;
                        sparseArray.put(i3, Integer.valueOf(a));
                        int i4 = this.totalItems / this.stickersPerRow;
                        if (!pack.covers.isEmpty()) {
                            count = (int) Math.ceil((double) (((float) pack.covers.size()) / ((float) this.stickersPerRow)));
                            for (int b = 0; b < pack.covers.size(); b++) {
                                this.cache.put(this.totalItems + b, pack.covers.get(b));
                            }
                        } else {
                            count = 1;
                            this.cache.put(this.totalItems, pack.cover);
                        }
                        int b2 = 0;
                        while (true) {
                            i = this.stickersPerRow;
                            if (b2 >= count * i) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + b2, pack);
                            b2++;
                        }
                        this.totalItems += i * count;
                    }
                }
            } else {
                if (StickersAlert.this.stickerSet != null) {
                    i2 = StickersAlert.this.stickerSet.documents.size();
                }
                this.totalItems = i2;
            }
            super.notifyDataSetChanged();
        }
    }
}
