package im.bclpbkiauv.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.WebFile;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ContentPreviewViewer;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.ContextLinkCell;
import im.bclpbkiauv.ui.cells.StickerCell;
import im.bclpbkiauv.ui.cells.StickerEmojiCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;

public class ContentPreviewViewer {
    private static final int CONTENT_TYPE_GIF = 1;
    private static final int CONTENT_TYPE_NONE = -1;
    private static final int CONTENT_TYPE_STICKER = 0;
    private static volatile ContentPreviewViewer Instance = null;
    private static TextPaint textPaint;
    /* access modifiers changed from: private */
    public boolean animateY;
    private ColorDrawable backgroundDrawable = new ColorDrawable(1895825408);
    private ImageReceiver centerImage = new ImageReceiver();
    /* access modifiers changed from: private */
    public boolean clearsInputField;
    /* access modifiers changed from: private */
    public FrameLayoutDrawer containerView;
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public int currentContentType;
    /* access modifiers changed from: private */
    public TLRPC.Document currentDocument;
    private float currentMoveY;
    /* access modifiers changed from: private */
    public float currentMoveYProgress;
    private View currentPreviewCell;
    /* access modifiers changed from: private */
    public TLRPC.InputStickerSet currentStickerSet;
    /* access modifiers changed from: private */
    public ContentPreviewViewerDelegate delegate;
    /* access modifiers changed from: private */
    public float finalMoveY;
    /* access modifiers changed from: private */
    public TLRPC.BotInlineResult inlineResult;
    private boolean isVisible = false;
    private int keyboardHeight = AndroidUtilities.dp(200.0f);
    private WindowInsets lastInsets;
    private float lastTouchY;
    private long lastUpdateTime;
    /* access modifiers changed from: private */
    public float moveY = 0.0f;
    private Runnable openPreviewRunnable;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public Object parentObject;
    private float showProgress;
    private Runnable showSheetRunnable = new Runnable() {
        public void run() {
            boolean canDelete;
            String str;
            int i;
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (ContentPreviewViewer.this.currentContentType == 0) {
                    boolean inFavs = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).isStickerInFavorites(ContentPreviewViewer.this.currentDocument);
                    BottomSheet.Builder builder = new BottomSheet.Builder(ContentPreviewViewer.this.parentActivity);
                    ArrayList<CharSequence> items = new ArrayList<>();
                    ArrayList<Integer> actions = new ArrayList<>();
                    ArrayList<Integer> icons = new ArrayList<>();
                    if (ContentPreviewViewer.this.delegate != null) {
                        if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                            items.add(LocaleController.getString("SendStickerPreview", R.string.SendStickerPreview));
                            icons.add(Integer.valueOf(R.drawable.outline_send));
                            actions.add(0);
                        }
                        if (ContentPreviewViewer.this.delegate.canSchedule()) {
                            items.add(LocaleController.getString("Schedule", R.string.Schedule));
                            icons.add(Integer.valueOf(R.drawable.photo_timer));
                            actions.add(3);
                        }
                        if (ContentPreviewViewer.this.currentStickerSet != null && ContentPreviewViewer.this.delegate.needOpen()) {
                            items.add(LocaleController.formatString("ViewPackPreview", R.string.ViewPackPreview, new Object[0]));
                            icons.add(Integer.valueOf(R.drawable.outline_pack));
                            actions.add(1);
                        }
                    }
                    if (!MessageObject.isMaskDocument(ContentPreviewViewer.this.currentDocument) && (inFavs || MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).canAddStickerToFavorites())) {
                        if (inFavs) {
                            i = R.string.DeleteFromFavorites;
                            str = "DeleteFromFavorites";
                        } else {
                            i = R.string.AddToFavorites;
                            str = "AddToFavorites";
                        }
                        items.add(LocaleController.getString(str, i));
                        icons.add(Integer.valueOf(inFavs ? R.drawable.outline_unfave : R.drawable.outline_fave));
                        actions.add(2);
                    }
                    if (!items.isEmpty()) {
                        int[] ic = new int[icons.size()];
                        for (int a = 0; a < icons.size(); a++) {
                            ic[a] = icons.get(a).intValue();
                        }
                        builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), ic, new DialogInterface.OnClickListener(actions, inFavs) {
                            private final /* synthetic */ ArrayList f$1;
                            private final /* synthetic */ boolean f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ContentPreviewViewer.AnonymousClass1.this.lambda$run$1$ContentPreviewViewer$1(this.f$1, this.f$2, dialogInterface, i);
                            }
                        });
                        builder.setDimBehind(false);
                        BottomSheet unused = ContentPreviewViewer.this.visibleDialog = builder.create();
                        ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            public final void onDismiss(DialogInterface dialogInterface) {
                                ContentPreviewViewer.AnonymousClass1.this.lambda$run$2$ContentPreviewViewer$1(dialogInterface);
                            }
                        });
                        ContentPreviewViewer.this.visibleDialog.show();
                        ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    }
                } else if (ContentPreviewViewer.this.delegate != null) {
                    boolean unused2 = ContentPreviewViewer.this.animateY = true;
                    BottomSheet unused3 = ContentPreviewViewer.this.visibleDialog = new BottomSheet(ContentPreviewViewer.this.parentActivity, false, 0) {
                        /* access modifiers changed from: protected */
                        public void onContainerTranslationYChanged(float translationY) {
                            if (ContentPreviewViewer.this.animateY) {
                                ViewGroup sheetContainer = getSheetContainer();
                                if (ContentPreviewViewer.this.finalMoveY == 0.0f) {
                                    float unused = ContentPreviewViewer.this.finalMoveY = 0.0f;
                                    float unused2 = ContentPreviewViewer.this.startMoveY = ContentPreviewViewer.this.moveY;
                                }
                                float unused3 = ContentPreviewViewer.this.currentMoveYProgress = 1.0f - Math.min(1.0f, translationY / ((float) this.containerView.getMeasuredHeight()));
                                float unused4 = ContentPreviewViewer.this.moveY = ContentPreviewViewer.this.startMoveY + ((ContentPreviewViewer.this.finalMoveY - ContentPreviewViewer.this.startMoveY) * ContentPreviewViewer.this.currentMoveYProgress);
                                ContentPreviewViewer.this.containerView.invalidate();
                                if (ContentPreviewViewer.this.currentMoveYProgress == 1.0f) {
                                    boolean unused5 = ContentPreviewViewer.this.animateY = false;
                                }
                            }
                        }
                    };
                    ArrayList<CharSequence> items2 = new ArrayList<>();
                    ArrayList<Integer> actions2 = new ArrayList<>();
                    ArrayList<Integer> icons2 = new ArrayList<>();
                    if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                        items2.add(LocaleController.getString("SendGifPreview", R.string.SendGifPreview));
                        icons2.add(Integer.valueOf(R.drawable.outline_send));
                        actions2.add(0);
                    }
                    if (ContentPreviewViewer.this.delegate.canSchedule()) {
                        items2.add(LocaleController.getString("Schedule", R.string.Schedule));
                        icons2.add(Integer.valueOf(R.drawable.photo_timer));
                        actions2.add(3);
                    }
                    if (ContentPreviewViewer.this.currentDocument != null) {
                        boolean hasRecentGif = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).hasRecentGif(ContentPreviewViewer.this.currentDocument);
                        canDelete = hasRecentGif;
                        if (hasRecentGif) {
                            items2.add(LocaleController.formatString("Delete", R.string.Delete, new Object[0]));
                            icons2.add(Integer.valueOf(R.drawable.chats_delete));
                            actions2.add(1);
                        } else {
                            items2.add(LocaleController.formatString("SaveToGIFs", R.string.SaveToGIFs, new Object[0]));
                            icons2.add(Integer.valueOf(R.drawable.outline_add_gif));
                            actions2.add(2);
                        }
                    } else {
                        canDelete = false;
                    }
                    int[] ic2 = new int[icons2.size()];
                    for (int a2 = 0; a2 < icons2.size(); a2++) {
                        ic2[a2] = icons2.get(a2).intValue();
                    }
                    ContentPreviewViewer.this.visibleDialog.setItems((CharSequence[]) items2.toArray(new CharSequence[0]), ic2, new DialogInterface.OnClickListener(actions2) {
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ContentPreviewViewer.AnonymousClass1.this.lambda$run$4$ContentPreviewViewer$1(this.f$1, dialogInterface, i);
                        }
                    });
                    ContentPreviewViewer.this.visibleDialog.setDimBehind(false);
                    ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public final void onDismiss(DialogInterface dialogInterface) {
                            ContentPreviewViewer.AnonymousClass1.this.lambda$run$5$ContentPreviewViewer$1(dialogInterface);
                        }
                    });
                    ContentPreviewViewer.this.visibleDialog.show();
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    if (canDelete) {
                        ContentPreviewViewer.this.visibleDialog.setItemColor(items2.size() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
                    }
                }
            }
        }

        public /* synthetic */ void lambda$run$1$ContentPreviewViewer$1(ArrayList actions, boolean inFavs, DialogInterface dialog, int which) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) actions.get(which)).intValue() == 0) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.sendSticker(ContentPreviewViewer.this.currentDocument, ContentPreviewViewer.this.parentObject, true, 0);
                    }
                } else if (((Integer) actions.get(which)).intValue() == 1) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.openSet(ContentPreviewViewer.this.currentStickerSet, ContentPreviewViewer.this.clearsInputField);
                    }
                } else if (((Integer) actions.get(which)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(2, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), inFavs);
                } else if (((Integer) actions.get(which)).intValue() == 3) {
                    TLRPC.Document sticker = ContentPreviewViewer.this.currentDocument;
                    Object parent = ContentPreviewViewer.this.parentObject;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, false, new AlertsCreator.ScheduleDatePickerDelegate(sticker, parent) {
                        private final /* synthetic */ TLRPC.Document f$1;
                        private final /* synthetic */ Object f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            ContentPreviewViewer.ContentPreviewViewerDelegate.this.sendSticker(this.f$1, this.f$2, z, i);
                        }
                    });
                }
            }
        }

        public /* synthetic */ void lambda$run$2$ContentPreviewViewer$1(DialogInterface dialog) {
            BottomSheet unused = ContentPreviewViewer.this.visibleDialog = null;
            ContentPreviewViewer.this.close();
        }

        public /* synthetic */ void lambda$run$4$ContentPreviewViewer$1(ArrayList actions, DialogInterface dialog, int which) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) actions.get(which)).intValue() == 0) {
                    ContentPreviewViewer.this.delegate.sendGif(ContentPreviewViewer.this.currentDocument != null ? ContentPreviewViewer.this.currentDocument : ContentPreviewViewer.this.inlineResult, true, 0);
                } else if (((Integer) actions.get(which)).intValue() == 1) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).removeRecentGif(ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) actions.get(which)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentGif(ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000));
                    MessagesController.getInstance(ContentPreviewViewer.this.currentAccount).saveGif("gif", ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) actions.get(which)).intValue() == 3) {
                    TLRPC.Document document = ContentPreviewViewer.this.currentDocument;
                    TLRPC.BotInlineResult result = ContentPreviewViewer.this.inlineResult;
                    Object access$1500 = ContentPreviewViewer.this.parentObject;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, false, new AlertsCreator.ScheduleDatePickerDelegate(document, result) {
                        private final /* synthetic */ TLRPC.Document f$1;
                        private final /* synthetic */ TLRPC.BotInlineResult f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            ContentPreviewViewer.AnonymousClass1.lambda$null$3(ContentPreviewViewer.ContentPreviewViewerDelegate.this, this.f$1, this.f$2, z, i);
                        }
                    });
                }
            }
        }

        /* JADX WARNING: type inference failed for: r3v0, types: [im.bclpbkiauv.tgnet.TLRPC$BotInlineResult] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static /* synthetic */ void lambda$null$3(im.bclpbkiauv.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r1, im.bclpbkiauv.tgnet.TLRPC.Document r2, im.bclpbkiauv.tgnet.TLRPC.BotInlineResult r3, boolean r4, int r5) {
            /*
                if (r2 == 0) goto L_0x0004
                r0 = r2
                goto L_0x0005
            L_0x0004:
                r0 = r3
            L_0x0005:
                r1.sendGif(r0, r4, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ContentPreviewViewer.AnonymousClass1.lambda$null$3(im.bclpbkiauv.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, im.bclpbkiauv.tgnet.TLRPC$Document, im.bclpbkiauv.tgnet.TLRPC$BotInlineResult, boolean, int):void");
        }

        public /* synthetic */ void lambda$run$5$ContentPreviewViewer$1(DialogInterface dialog) {
            BottomSheet unused = ContentPreviewViewer.this.visibleDialog = null;
            ContentPreviewViewer.this.close();
        }
    };
    private Drawable slideUpDrawable;
    /* access modifiers changed from: private */
    public float startMoveY;
    private int startX;
    private int startY;
    private StaticLayout stickerEmojiLayout;
    /* access modifiers changed from: private */
    public BottomSheet visibleDialog;
    private WindowManager.LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            ContentPreviewViewer.this.onDraw(canvas);
        }
    }

    public interface ContentPreviewViewerDelegate {
        boolean canSchedule();

        void gifAddedOrDeleted();

        boolean isInScheduleMode();

        boolean needOpen();

        boolean needSend();

        void openSet(TLRPC.InputStickerSet inputStickerSet, boolean z);

        void sendGif(Object obj, boolean z, int i);

        void sendSticker(TLRPC.Document document, Object obj, boolean z, int i);

        /* renamed from: im.bclpbkiauv.ui.ContentPreviewViewer$ContentPreviewViewerDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$needOpen(ContentPreviewViewerDelegate _this) {
                return true;
            }

            public static void $default$sendGif(ContentPreviewViewerDelegate _this, Object gif, boolean notify, int scheduleDate) {
            }

            public static void $default$gifAddedOrDeleted(ContentPreviewViewerDelegate _this) {
            }
        }
    }

    public static ContentPreviewViewer getInstance() {
        ContentPreviewViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    ContentPreviewViewer contentPreviewViewer = new ContentPreviewViewer();
                    localInstance = contentPreviewViewer;
                    Instance = contentPreviewViewer;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public void reset() {
        Runnable runnable = this.openPreviewRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.openPreviewRunnable = null;
        }
        View view = this.currentPreviewCell;
        if (view != null) {
            if (view instanceof StickerEmojiCell) {
                ((StickerEmojiCell) view).setScaled(false);
            } else if (view instanceof StickerCell) {
                ((StickerCell) view).setScaled(false);
            } else if (view instanceof ContextLinkCell) {
                ((ContextLinkCell) view).setScaled(false);
            }
            this.currentPreviewCell = null;
        }
    }

    public boolean onTouch(MotionEvent event, RecyclerListView listView, int height, Object listener, ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        View view;
        int contentType;
        boolean z;
        RecyclerListView recyclerListView = listView;
        this.delegate = contentPreviewViewerDelegate;
        boolean z2 = false;
        if (this.openPreviewRunnable == null && !isVisible()) {
            Object obj = listener;
            return false;
        } else if (event.getAction() == 1 || event.getAction() == 3 || event.getAction() == 6) {
            AndroidUtilities.runOnUIThread(new Runnable(listener) {
                private final /* synthetic */ Object f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContentPreviewViewer.lambda$onTouch$0(RecyclerListView.this, this.f$1);
                }
            }, 150);
            Runnable runnable = this.openPreviewRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.openPreviewRunnable = null;
                return false;
            } else if (!isVisible()) {
                return false;
            } else {
                close();
                View view2 = this.currentPreviewCell;
                if (view2 == null) {
                    return false;
                }
                if (view2 instanceof StickerEmojiCell) {
                    ((StickerEmojiCell) view2).setScaled(false);
                } else if (view2 instanceof StickerCell) {
                    ((StickerCell) view2).setScaled(false);
                } else if (view2 instanceof ContextLinkCell) {
                    ((ContextLinkCell) view2).setScaled(false);
                }
                this.currentPreviewCell = null;
                return false;
            }
        } else if (event.getAction() == 0) {
            Object obj2 = listener;
            return false;
        } else if (this.isVisible) {
            if (event.getAction() != 2) {
                return true;
            }
            if (this.currentContentType == 1) {
                if (this.visibleDialog == null && this.showProgress == 1.0f) {
                    if (this.lastTouchY == -10000.0f) {
                        this.lastTouchY = event.getY();
                        this.currentMoveY = 0.0f;
                        this.moveY = 0.0f;
                    } else {
                        float newY = event.getY();
                        float f = this.currentMoveY + (newY - this.lastTouchY);
                        this.currentMoveY = f;
                        this.lastTouchY = newY;
                        if (f > 0.0f) {
                            this.currentMoveY = 0.0f;
                        } else if (f < ((float) (-AndroidUtilities.dp(60.0f)))) {
                            this.currentMoveY = (float) (-AndroidUtilities.dp(60.0f));
                        }
                        this.moveY = rubberYPoisition(this.currentMoveY, (float) AndroidUtilities.dp(200.0f));
                        this.containerView.invalidate();
                        if (this.currentMoveY <= ((float) (-AndroidUtilities.dp(55.0f)))) {
                            AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                            this.showSheetRunnable.run();
                            return true;
                        }
                    }
                }
                return true;
            }
            int x = (int) event.getX();
            int y = (int) event.getY();
            int count = listView.getChildCount();
            int a = 0;
            while (a < count) {
                if (recyclerListView instanceof RecyclerListView) {
                    view = recyclerListView.getChildAt(a);
                } else {
                    view = null;
                }
                if (view == null) {
                    return z2;
                }
                int top = view.getTop();
                int bottom = view.getBottom();
                int left = view.getLeft();
                int right = view.getRight();
                if (top > y || bottom < y || left > x) {
                    int i = left;
                    int i2 = bottom;
                } else if (right >= x) {
                    if (view instanceof StickerEmojiCell) {
                        this.centerImage.setRoundRadius(z2);
                        contentType = 0;
                    } else if (view instanceof StickerCell) {
                        this.centerImage.setRoundRadius(z2);
                        contentType = 0;
                    } else {
                        if (view instanceof ContextLinkCell) {
                            ContextLinkCell cell = (ContextLinkCell) view;
                            if (cell.isSticker()) {
                                this.centerImage.setRoundRadius(z2 ? 1 : 0);
                                contentType = 0;
                            } else if (cell.isGif()) {
                                this.centerImage.setRoundRadius(AndroidUtilities.dp(6.0f));
                                contentType = 1;
                            }
                        }
                        contentType = -1;
                    }
                    if (contentType != -1) {
                        View view3 = this.currentPreviewCell;
                        if (view == view3) {
                            return true;
                        }
                        if (view3 instanceof StickerEmojiCell) {
                            z = false;
                            ((StickerEmojiCell) view3).setScaled(false);
                        } else {
                            z = false;
                            if (view3 instanceof StickerCell) {
                                ((StickerCell) view3).setScaled(false);
                            } else if (view3 instanceof ContextLinkCell) {
                                ((ContextLinkCell) view3).setScaled(false);
                            }
                        }
                        this.currentPreviewCell = view;
                        setKeyboardHeight(height);
                        this.clearsInputField = z;
                        View view4 = this.currentPreviewCell;
                        if (view4 instanceof StickerEmojiCell) {
                            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view4;
                            TLRPC.Document sticker = stickerEmojiCell.getSticker();
                            boolean isRecent = stickerEmojiCell.isRecent();
                            Object parentObject2 = stickerEmojiCell.getParentObject();
                            StickerEmojiCell stickerEmojiCell2 = stickerEmojiCell;
                            TLRPC.Document document = sticker;
                            int i3 = right;
                            int i4 = left;
                            boolean z3 = isRecent;
                            int i5 = bottom;
                            open(document, (TLRPC.BotInlineResult) null, contentType, z3, parentObject2);
                            stickerEmojiCell2.setScaled(true);
                            return true;
                        }
                        int i6 = left;
                        int i7 = bottom;
                        if (view4 instanceof StickerCell) {
                            StickerCell stickerCell = (StickerCell) view4;
                            open(stickerCell.getSticker(), (TLRPC.BotInlineResult) null, contentType, false, stickerCell.getParentObject());
                            stickerCell.setScaled(true);
                            this.clearsInputField = stickerCell.isClearsInputField();
                            return true;
                        } else if (!(view4 instanceof ContextLinkCell)) {
                            return true;
                        } else {
                            ContextLinkCell contextLinkCell = (ContextLinkCell) view4;
                            open(contextLinkCell.getDocument(), contextLinkCell.getBotInlineResult(), contentType, false, (Object) null);
                            if (contentType == 1) {
                                return true;
                            }
                            contextLinkCell.setScaled(true);
                            return true;
                        }
                    } else {
                        int i8 = left;
                        int i9 = bottom;
                        return true;
                    }
                }
                a++;
                ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = contentPreviewViewerDelegate;
                z2 = false;
            }
            return true;
        } else if (this.openPreviewRunnable == null) {
            Object obj3 = listener;
            return false;
        } else if (event.getAction() != 2) {
            AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
            this.openPreviewRunnable = null;
            Object obj4 = listener;
            return false;
        } else if (Math.hypot((double) (((float) this.startX) - event.getX()), (double) (((float) this.startY) - event.getY())) > ((double) AndroidUtilities.dp(10.0f))) {
            AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
            this.openPreviewRunnable = null;
            Object obj5 = listener;
            return false;
        } else {
            Object obj6 = listener;
            return false;
        }
    }

    static /* synthetic */ void lambda$onTouch$0(RecyclerListView listView, Object listener) {
        if (listView instanceof RecyclerListView) {
            listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) listener);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event, RecyclerListView listView, int height, ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        ContentPreviewViewer contentPreviewViewer = this;
        RecyclerListView recyclerListView = listView;
        contentPreviewViewer.delegate = contentPreviewViewerDelegate;
        boolean z = false;
        if (event.getAction() == 0) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int count = listView.getChildCount();
            int a = 0;
            while (a < count) {
                View view = null;
                if (recyclerListView instanceof RecyclerListView) {
                    view = recyclerListView.getChildAt(a);
                }
                if (view == null) {
                    return z;
                }
                int top = view.getTop();
                int bottom = view.getBottom();
                int left = view.getLeft();
                int right = view.getRight();
                if (top > y || bottom < y || left > x || right < x) {
                    int i = height;
                    a++;
                    z = false;
                    contentPreviewViewer = this;
                    recyclerListView = listView;
                } else {
                    int contentType = -1;
                    if (view instanceof StickerEmojiCell) {
                        if (((StickerEmojiCell) view).showingBitmap()) {
                            contentType = 0;
                            contentPreviewViewer.centerImage.setRoundRadius(z);
                        }
                    } else if (view instanceof StickerCell) {
                        if (((StickerCell) view).showingBitmap()) {
                            contentType = 0;
                            contentPreviewViewer.centerImage.setRoundRadius(z);
                        }
                    } else if (view instanceof ContextLinkCell) {
                        ContextLinkCell cell = (ContextLinkCell) view;
                        if (cell.showingBitmap()) {
                            if (cell.isSticker()) {
                                contentType = 0;
                                contentPreviewViewer.centerImage.setRoundRadius(z ? 1 : 0);
                            } else if (cell.isGif()) {
                                contentType = 1;
                                contentPreviewViewer.centerImage.setRoundRadius(AndroidUtilities.dp(6.0f));
                            }
                        }
                    }
                    if (contentType == -1) {
                        return false;
                    }
                    contentPreviewViewer.startX = x;
                    contentPreviewViewer.startY = y;
                    contentPreviewViewer.currentPreviewCell = view;
                    $$Lambda$ContentPreviewViewer$yNzUioO6_2xhDsHkZ106D6yHWec r14 = new Runnable(recyclerListView, height, contentType) {
                        private final /* synthetic */ RecyclerListView f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ int f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void run() {
                            ContentPreviewViewer.this.lambda$onInterceptTouchEvent$1$ContentPreviewViewer(this.f$1, this.f$2, this.f$3);
                        }
                    };
                    contentPreviewViewer.openPreviewRunnable = r14;
                    AndroidUtilities.runOnUIThread(r14, 200);
                    return true;
                }
            }
            int i2 = height;
            return false;
        }
        int i3 = height;
        return false;
    }

    public /* synthetic */ void lambda$onInterceptTouchEvent$1$ContentPreviewViewer(RecyclerListView listView, int height, int contentTypeFinal) {
        if (this.openPreviewRunnable != null) {
            listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) null);
            listView.requestDisallowInterceptTouchEvent(true);
            this.openPreviewRunnable = null;
            setParentActivity((Activity) listView.getContext());
            setKeyboardHeight(height);
            this.clearsInputField = false;
            View view = this.currentPreviewCell;
            if (view instanceof StickerEmojiCell) {
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                open(stickerEmojiCell.getSticker(), (TLRPC.BotInlineResult) null, contentTypeFinal, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject());
                stickerEmojiCell.setScaled(true);
            } else if (view instanceof StickerCell) {
                StickerCell stickerCell = (StickerCell) view;
                open(stickerCell.getSticker(), (TLRPC.BotInlineResult) null, contentTypeFinal, false, stickerCell.getParentObject());
                stickerCell.setScaled(true);
                this.clearsInputField = stickerCell.isClearsInputField();
            } else if (view instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                open(contextLinkCell.getDocument(), contextLinkCell.getBotInlineResult(), contentTypeFinal, false, (Object) null);
                if (contentTypeFinal != 1) {
                    contextLinkCell.setScaled(true);
                }
            }
        }
    }

    public void setDelegate(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        this.delegate = contentPreviewViewerDelegate;
    }

    public void setParentActivity(Activity activity) {
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.centerImage.setCurrentAccount(i);
        this.centerImage.setLayerNum(7);
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.slideUpDrawable = activity.getResources().getDrawable(R.drawable.preview_arrow);
            FrameLayout frameLayout = new FrameLayout(activity);
            this.windowView = frameLayout;
            frameLayout.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            if (Build.VERSION.SDK_INT >= 21) {
                this.windowView.setFitsSystemWindows(true);
                this.windowView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        return ContentPreviewViewer.this.lambda$setParentActivity$2$ContentPreviewViewer(view, windowInsets);
                    }
                });
            }
            FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(activity);
            this.containerView = frameLayoutDrawer;
            frameLayoutDrawer.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return ContentPreviewViewer.this.lambda$setParentActivity$3$ContentPreviewViewer(view, motionEvent);
                }
            });
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            if (Build.VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -2147417848;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.centerImage.setAspectFit(true);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setParentView(this.containerView);
        }
    }

    public /* synthetic */ WindowInsets lambda$setParentActivity$2$ContentPreviewViewer(View v, WindowInsets insets) {
        this.lastInsets = insets;
        return insets;
    }

    public /* synthetic */ boolean lambda$setParentActivity$3$ContentPreviewViewer(View v, MotionEvent event) {
        if (event.getAction() == 1 || event.getAction() == 6 || event.getAction() == 3) {
            close();
        }
        return true;
    }

    public void setKeyboardHeight(int height) {
        this.keyboardHeight = height;
    }

    public void open(TLRPC.Document document, TLRPC.BotInlineResult botInlineResult, int contentType, boolean isRecent, Object parent) {
        TLRPC.Document document2 = document;
        TLRPC.BotInlineResult botInlineResult2 = botInlineResult;
        int i = contentType;
        if (this.parentActivity == null) {
            Object obj = parent;
        } else if (this.windowView == null) {
            Object obj2 = parent;
        } else {
            this.stickerEmojiLayout = null;
            if (i == 0) {
                if (document2 != null) {
                    if (textPaint == null) {
                        TextPaint textPaint2 = new TextPaint(1);
                        textPaint = textPaint2;
                        textPaint2.setTextSize((float) AndroidUtilities.dp(24.0f));
                    }
                    TLRPC.InputStickerSet newSet = null;
                    int a = 0;
                    while (true) {
                        if (a >= document2.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = document2.attributes.get(a);
                        if ((attribute instanceof TLRPC.TL_documentAttributeSticker) && attribute.stickerset != null) {
                            newSet = attribute.stickerset;
                            break;
                        }
                        a++;
                    }
                    if (newSet != null) {
                        try {
                            if (this.visibleDialog != null) {
                                this.visibleDialog.setOnDismissListener((DialogInterface.OnDismissListener) null);
                                this.visibleDialog.dismiss();
                                this.visibleDialog = null;
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300);
                    }
                    this.currentStickerSet = newSet;
                    this.parentObject = parent;
                    this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90), document2), (String) null, "webp", (Object) this.currentStickerSet, 1);
                    int a2 = 0;
                    while (true) {
                        if (a2 >= document2.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute2 = document2.attributes.get(a2);
                        if ((attribute2 instanceof TLRPC.TL_documentAttributeSticker) && !TextUtils.isEmpty(attribute2.alt)) {
                            this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(attribute2.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            break;
                        }
                        a2++;
                    }
                } else {
                    return;
                }
            } else {
                Object obj3 = parent;
                if (document2 != null) {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
                    ImageReceiver imageReceiver = this.centerImage;
                    ImageLocation forDocument = ImageLocation.getForDocument(document);
                    ImageLocation forDocument2 = ImageLocation.getForDocument(thumb, document2);
                    int i2 = document2.size;
                    imageReceiver.setImage(forDocument, (String) null, forDocument2, "90_90_b", i2, (String) null, "gif" + document2, 0);
                } else if (botInlineResult2 != null && botInlineResult2.content != null) {
                    ImageReceiver imageReceiver2 = this.centerImage;
                    ImageLocation forWebFile = ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.content));
                    ImageLocation forWebFile2 = ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.thumb));
                    int i3 = botInlineResult2.content.size;
                    imageReceiver2.setImage(forWebFile, (String) null, forWebFile2, "90_90_b", i3, (String) null, "gif" + botInlineResult2, 1);
                } else {
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                AndroidUtilities.runOnUIThread(this.showSheetRunnable, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
            this.currentContentType = i;
            this.currentDocument = document2;
            this.inlineResult = botInlineResult2;
            this.containerView.invalidate();
            if (!this.isVisible) {
                AndroidUtilities.lockOrientation(this.parentActivity);
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
                this.isVisible = true;
                this.showProgress = 0.0f;
                this.lastTouchY = -10000.0f;
                this.currentMoveYProgress = 0.0f;
                this.finalMoveY = 0.0f;
                this.currentMoveY = 0.0f;
                this.moveY = 0.0f;
                this.lastUpdateTime = System.currentTimeMillis();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
            }
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void close() {
        if (this.parentActivity != null && this.visibleDialog == null) {
            AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
            this.showProgress = 1.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            this.containerView.invalidate();
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.currentDocument = null;
            this.currentStickerSet = null;
            this.delegate = null;
            this.isVisible = false;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
        }
    }

    public void destroy() {
        FrameLayout frameLayout;
        this.isVisible = false;
        this.delegate = null;
        this.currentDocument = null;
        this.currentStickerSet = null;
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (this.parentActivity != null && (frameLayout = this.windowView) != null) {
            try {
                if (frameLayout.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            Instance = null;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
        }
    }

    private float rubberYPoisition(float offset, float factor) {
        float f = 1.0f;
        float f2 = -((1.0f - (1.0f / (((0.55f * Math.abs(offset)) / factor) + 1.0f))) * factor);
        if (offset >= 0.0f) {
            f = -1.0f;
        }
        return f2 * f;
    }

    /* access modifiers changed from: private */
    public void onDraw(Canvas canvas) {
        ColorDrawable colorDrawable;
        int top;
        int size;
        Drawable drawable;
        WindowInsets windowInsets;
        if (this.containerView != null && (colorDrawable = this.backgroundDrawable) != null) {
            colorDrawable.setAlpha((int) (this.showProgress * 180.0f));
            int i = 0;
            this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
            this.backgroundDrawable.draw(canvas);
            canvas.save();
            int insets = 0;
            if (Build.VERSION.SDK_INT < 21 || (windowInsets = this.lastInsets) == null) {
                top = AndroidUtilities.statusBarHeight;
            } else {
                insets = windowInsets.getStableInsetBottom() + this.lastInsets.getStableInsetTop();
                top = this.lastInsets.getStableInsetTop();
            }
            if (this.currentContentType == 1) {
                size = Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets) - AndroidUtilities.dp(40.0f);
            } else {
                size = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets)) / 1.8f);
            }
            float width = (float) (this.containerView.getWidth() / 2);
            float f = this.moveY;
            int i2 = (size / 2) + top;
            if (this.stickerEmojiLayout != null) {
                i = AndroidUtilities.dp(40.0f);
            }
            canvas.translate(width, f + ((float) Math.max(i2 + i, ((this.containerView.getHeight() - insets) - this.keyboardHeight) / 2)));
            float f2 = this.showProgress;
            int size2 = (int) (((float) size) * ((f2 * 0.8f) / 0.8f));
            this.centerImage.setAlpha(f2);
            this.centerImage.setImageCoords((-size2) / 2, (-size2) / 2, size2, size2);
            this.centerImage.draw(canvas);
            if (this.currentContentType == 1 && (drawable = this.slideUpDrawable) != null) {
                int w = drawable.getIntrinsicWidth();
                int h = this.slideUpDrawable.getIntrinsicHeight();
                int y = (int) (this.centerImage.getDrawRegion().top - ((float) AndroidUtilities.dp(((this.currentMoveY / ((float) AndroidUtilities.dp(60.0f))) * 6.0f) + 17.0f)));
                this.slideUpDrawable.setAlpha((int) ((1.0f - this.currentMoveYProgress) * 255.0f));
                this.slideUpDrawable.setBounds((-w) / 2, (-h) + y, w / 2, y);
                this.slideUpDrawable.draw(canvas);
            }
            if (this.stickerEmojiLayout != null) {
                canvas.translate((float) (-AndroidUtilities.dp(50.0f)), (float) (((-this.centerImage.getImageHeight()) / 2) - AndroidUtilities.dp(30.0f)));
                this.stickerEmojiLayout.draw(canvas);
            }
            canvas.restore();
            if (this.isVisible) {
                if (this.showProgress != 1.0f) {
                    long newTime = System.currentTimeMillis();
                    this.lastUpdateTime = newTime;
                    this.showProgress += ((float) (newTime - this.lastUpdateTime)) / 120.0f;
                    this.containerView.invalidate();
                    if (this.showProgress > 1.0f) {
                        this.showProgress = 1.0f;
                    }
                }
            } else if (this.showProgress != 0.0f) {
                long newTime2 = System.currentTimeMillis();
                this.lastUpdateTime = newTime2;
                this.showProgress -= ((float) (newTime2 - this.lastUpdateTime)) / 120.0f;
                this.containerView.invalidate();
                if (this.showProgress < 0.0f) {
                    this.showProgress = 0.0f;
                }
                if (this.showProgress == 0.0f) {
                    this.centerImage.setImageBitmap((Drawable) null);
                    AndroidUtilities.unlockOrientation(this.parentActivity);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            ContentPreviewViewer.this.lambda$onDraw$4$ContentPreviewViewer();
                        }
                    });
                    try {
                        if (this.windowView.getParent() != null) {
                            ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$onDraw$4$ContentPreviewViewer() {
        this.centerImage.setImageBitmap((Bitmap) null);
    }
}
