package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import java.util.ArrayList;

public class GroupedPhotosListView extends View implements GestureDetector.OnGestureListener {
    private boolean animateAllLine;
    private int animateToDX;
    private int animateToDXStart;
    private int animateToItem = -1;
    private Paint backgroundPaint = new Paint();
    private long currentGroupId;
    private int currentImage;
    private float currentItemProgress = 1.0f;
    private ArrayList<Object> currentObjects = new ArrayList<>();
    public ArrayList<ImageLocation> currentPhotos = new ArrayList<>();
    private GroupedPhotosListViewDelegate delegate;
    private int drawDx;
    private GestureDetector gestureDetector;
    private boolean ignoreChanges;
    private ArrayList<ImageReceiver> imagesToDraw = new ArrayList<>();
    private int itemHeight;
    private int itemSpacing;
    private int itemWidth;
    private int itemY;
    private long lastUpdateTime;
    private float moveLineProgress;
    private boolean moving;
    private int nextImage;
    private float nextItemProgress = 0.0f;
    private int nextPhotoScrolling = -1;
    private Scroller scroll;
    private boolean scrolling;
    private boolean stopedScrolling;
    private ArrayList<ImageReceiver> unusedReceivers = new ArrayList<>();

    public interface GroupedPhotosListViewDelegate {
        int getAvatarsDialogId();

        int getCurrentAccount();

        int getCurrentIndex();

        ArrayList<MessageObject> getImagesArr();

        ArrayList<ImageLocation> getImagesArrLocations();

        ArrayList<TLRPC.PageBlock> getPageBlockArr();

        Object getParentObject();

        int getSlideshowMessageId();

        void setCurrentIndex(int i);
    }

    public GroupedPhotosListView(Context context) {
        super(context);
        this.gestureDetector = new GestureDetector(context, this);
        this.scroll = new Scroller(context);
        this.itemWidth = AndroidUtilities.dp(42.0f);
        this.itemHeight = AndroidUtilities.dp(56.0f);
        this.itemSpacing = AndroidUtilities.dp(1.0f);
        this.itemY = AndroidUtilities.dp(3.0f);
        this.backgroundPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
    }

    public void clear() {
        this.currentPhotos.clear();
        this.currentObjects.clear();
        this.imagesToDraw.clear();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v0, resolved type: im.bclpbkiauv.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: im.bclpbkiauv.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: im.bclpbkiauv.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$PageBlock} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$PageBlock} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: im.bclpbkiauv.messenger.ImageLocation} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fillList() {
        /*
            r21 = this;
            r0 = r21
            boolean r1 = r0.ignoreChanges
            r2 = 0
            if (r1 == 0) goto L_0x000a
            r0.ignoreChanges = r2
            return
        L_0x000a:
            im.bclpbkiauv.ui.components.GroupedPhotosListView$GroupedPhotosListViewDelegate r1 = r0.delegate
            int r1 = r1.getCurrentIndex()
            im.bclpbkiauv.ui.components.GroupedPhotosListView$GroupedPhotosListViewDelegate r3 = r0.delegate
            java.util.ArrayList r3 = r3.getImagesArrLocations()
            im.bclpbkiauv.ui.components.GroupedPhotosListView$GroupedPhotosListViewDelegate r4 = r0.delegate
            java.util.ArrayList r4 = r4.getImagesArr()
            im.bclpbkiauv.ui.components.GroupedPhotosListView$GroupedPhotosListViewDelegate r5 = r0.delegate
            java.util.ArrayList r5 = r5.getPageBlockArr()
            im.bclpbkiauv.ui.components.GroupedPhotosListView$GroupedPhotosListViewDelegate r6 = r0.delegate
            int r6 = r6.getSlideshowMessageId()
            im.bclpbkiauv.ui.components.GroupedPhotosListView$GroupedPhotosListViewDelegate r7 = r0.delegate
            int r7 = r7.getCurrentAccount()
            r8 = 0
            r9 = 0
            r10 = 0
            if (r3 == 0) goto L_0x004a
            boolean r11 = r3.isEmpty()
            if (r11 != 0) goto L_0x004a
            java.lang.Object r11 = r3.get(r1)
            im.bclpbkiauv.messenger.ImageLocation r11 = (im.bclpbkiauv.messenger.ImageLocation) r11
            int r9 = r3.size()
            r10 = r11
            r17 = r3
            r16 = r7
            goto L_0x0132
        L_0x004a:
            if (r4 == 0) goto L_0x00d6
            boolean r11 = r4.isEmpty()
            if (r11 != 0) goto L_0x00d6
            java.lang.Object r11 = r4.get(r1)
            im.bclpbkiauv.messenger.MessageObject r11 = (im.bclpbkiauv.messenger.MessageObject) r11
            r10 = r11
            long r12 = r11.getGroupIdForUse()
            long r14 = r0.currentGroupId
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 == 0) goto L_0x0070
            r8 = 1
            long r12 = r11.getGroupIdForUse()
            r0.currentGroupId = r12
            r17 = r3
            r16 = r7
            goto L_0x00d5
        L_0x0070:
            int r12 = r1 + 10
            int r13 = r4.size()
            int r12 = java.lang.Math.min(r12, r13)
            r13 = r1
        L_0x007b:
            if (r13 >= r12) goto L_0x009d
            java.lang.Object r14 = r4.get(r13)
            im.bclpbkiauv.messenger.MessageObject r14 = (im.bclpbkiauv.messenger.MessageObject) r14
            if (r6 != 0) goto L_0x0092
            long r15 = r14.getGroupIdForUse()
            r17 = r3
            long r2 = r0.currentGroupId
            int r18 = (r15 > r2 ? 1 : (r15 == r2 ? 0 : -1))
            if (r18 != 0) goto L_0x009f
            goto L_0x0094
        L_0x0092:
            r17 = r3
        L_0x0094:
            int r9 = r9 + 1
            int r13 = r13 + 1
            r3 = r17
            r2 = 0
            goto L_0x007b
        L_0x009d:
            r17 = r3
        L_0x009f:
            int r2 = r1 + -10
            r3 = 0
            int r2 = java.lang.Math.max(r2, r3)
            int r3 = r1 + -1
        L_0x00a8:
            if (r3 < r2) goto L_0x00cf
            java.lang.Object r13 = r4.get(r3)
            im.bclpbkiauv.messenger.MessageObject r13 = (im.bclpbkiauv.messenger.MessageObject) r13
            if (r6 != 0) goto L_0x00c1
            long r14 = r13.getGroupIdForUse()
            r16 = r7
            r18 = r8
            long r7 = r0.currentGroupId
            int r19 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1))
            if (r19 != 0) goto L_0x00d3
            goto L_0x00c5
        L_0x00c1:
            r16 = r7
            r18 = r8
        L_0x00c5:
            int r9 = r9 + 1
            int r3 = r3 + -1
            r7 = r16
            r8 = r18
            goto L_0x00a8
        L_0x00cf:
            r16 = r7
            r18 = r8
        L_0x00d3:
            r8 = r18
        L_0x00d5:
            goto L_0x0132
        L_0x00d6:
            r17 = r3
            r16 = r7
            r18 = r8
            if (r5 == 0) goto L_0x0130
            boolean r2 = r5.isEmpty()
            if (r2 != 0) goto L_0x0130
            java.lang.Object r2 = r5.get(r1)
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r2 = (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r2
            r10 = r2
            int r3 = r2.groupId
            long r7 = (long) r3
            long r11 = r0.currentGroupId
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 == 0) goto L_0x00fb
            r8 = 1
            int r3 = r2.groupId
            long r11 = (long) r3
            r0.currentGroupId = r11
            goto L_0x0132
        L_0x00fb:
            r3 = r1
            int r7 = r5.size()
        L_0x0100:
            if (r3 >= r7) goto L_0x0117
            java.lang.Object r8 = r5.get(r3)
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r8 = (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r8
            int r11 = r8.groupId
            long r11 = (long) r11
            long r13 = r0.currentGroupId
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 != 0) goto L_0x0117
            int r9 = r9 + 1
            int r3 = r3 + 1
            goto L_0x0100
        L_0x0117:
            int r3 = r1 + -1
        L_0x0119:
            if (r3 < 0) goto L_0x0130
            java.lang.Object r7 = r5.get(r3)
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r7 = (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r7
            int r8 = r7.groupId
            long r11 = (long) r8
            long r13 = r0.currentGroupId
            int r8 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r8 != 0) goto L_0x0130
            int r9 = r9 + 1
            int r3 = r3 + -1
            goto L_0x0119
        L_0x0130:
            r8 = r18
        L_0x0132:
            if (r10 != 0) goto L_0x0135
            return
        L_0x0135:
            r2 = -1
            r3 = 1
            if (r8 != 0) goto L_0x018c
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r7 = r0.currentPhotos
            int r7 = r7.size()
            if (r9 != r7) goto L_0x018b
            java.util.ArrayList<java.lang.Object> r7 = r0.currentObjects
            int r7 = r7.indexOf(r10)
            if (r7 != r2) goto L_0x014a
            goto L_0x018b
        L_0x014a:
            java.util.ArrayList<java.lang.Object> r7 = r0.currentObjects
            int r7 = r7.indexOf(r10)
            int r11 = r0.currentImage
            if (r11 == r7) goto L_0x018c
            if (r7 == r2) goto L_0x018c
            boolean r12 = r0.animateAllLine
            if (r12 == 0) goto L_0x0178
            r0.animateToItem = r7
            r0.nextImage = r7
            int r11 = r11 - r7
            int r12 = r0.itemWidth
            int r13 = r0.itemSpacing
            int r12 = r12 + r13
            int r11 = r11 * r12
            r0.animateToDX = r11
            r0.moving = r3
            r11 = 0
            r0.animateAllLine = r11
            long r11 = java.lang.System.currentTimeMillis()
            r0.lastUpdateTime = r11
            r21.invalidate()
            r11 = 0
            goto L_0x0188
        L_0x0178:
            int r11 = r11 - r7
            int r12 = r0.itemWidth
            int r13 = r0.itemSpacing
            int r12 = r12 + r13
            int r11 = r11 * r12
            r0.fillImages(r3, r11)
            r0.currentImage = r7
            r11 = 0
            r0.moving = r11
        L_0x0188:
            r0.drawDx = r11
            goto L_0x018c
        L_0x018b:
            r8 = 1
        L_0x018c:
            if (r8 == 0) goto L_0x02f5
            r7 = 0
            r0.animateAllLine = r7
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r7 = r0.currentPhotos
            r7.clear()
            java.util.ArrayList<java.lang.Object> r7 = r0.currentObjects
            r7.clear()
            if (r17 == 0) goto L_0x01b8
            boolean r7 = r17.isEmpty()
            if (r7 != 0) goto L_0x01b8
            java.util.ArrayList<java.lang.Object> r7 = r0.currentObjects
            r11 = r17
            r7.addAll(r11)
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r7 = r0.currentPhotos
            r7.addAll(r11)
            r0.currentImage = r1
            r0.animateToItem = r2
            r17 = r4
            r14 = r6
            goto L_0x02dd
        L_0x01b8:
            r11 = r17
            r12 = 0
            if (r4 == 0) goto L_0x0267
            boolean r7 = r4.isEmpty()
            if (r7 != 0) goto L_0x0267
            long r14 = r0.currentGroupId
            int r7 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r7 != 0) goto L_0x01d2
            if (r6 == 0) goto L_0x01cd
            goto L_0x01d2
        L_0x01cd:
            r17 = r4
            r14 = r6
            goto L_0x02dd
        L_0x01d2:
            int r7 = r1 + 10
            int r12 = r4.size()
            int r7 = java.lang.Math.min(r7, r12)
            r12 = r1
        L_0x01dd:
            r13 = 56
            if (r12 >= r7) goto L_0x020f
            java.lang.Object r14 = r4.get(r12)
            im.bclpbkiauv.messenger.MessageObject r14 = (im.bclpbkiauv.messenger.MessageObject) r14
            if (r6 != 0) goto L_0x01f3
            long r17 = r14.getGroupIdForUse()
            long r2 = r0.currentGroupId
            int r20 = (r17 > r2 ? 1 : (r17 == r2 ? 0 : -1))
            if (r20 != 0) goto L_0x020f
        L_0x01f3:
            java.util.ArrayList<java.lang.Object> r2 = r0.currentObjects
            r2.add(r14)
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r2 = r0.currentPhotos
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r3 = r14.photoThumbs
            r15 = 1
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r13, r15)
            im.bclpbkiauv.tgnet.TLObject r13 = r14.photoThumbsObject
            im.bclpbkiauv.messenger.ImageLocation r3 = im.bclpbkiauv.messenger.ImageLocation.getForObject(r3, r13)
            r2.add(r3)
            int r12 = r12 + 1
            r2 = -1
            r3 = 1
            goto L_0x01dd
        L_0x020f:
            r2 = 0
            r0.currentImage = r2
            r3 = -1
            r0.animateToItem = r3
            int r3 = r1 + -10
            int r3 = java.lang.Math.max(r3, r2)
            int r2 = r1 + -1
        L_0x021d:
            if (r2 < r3) goto L_0x0260
            java.lang.Object r12 = r4.get(r2)
            im.bclpbkiauv.messenger.MessageObject r12 = (im.bclpbkiauv.messenger.MessageObject) r12
            if (r6 != 0) goto L_0x0236
            long r14 = r12.getGroupIdForUse()
            r18 = r3
            r17 = r4
            long r3 = r0.currentGroupId
            int r20 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
            if (r20 != 0) goto L_0x0264
            goto L_0x023a
        L_0x0236:
            r18 = r3
            r17 = r4
        L_0x023a:
            java.util.ArrayList<java.lang.Object> r3 = r0.currentObjects
            r4 = 0
            r3.add(r4, r12)
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r3 = r0.currentPhotos
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r14 = r12.photoThumbs
            r15 = 1
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r14 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r14, r13, r15)
            im.bclpbkiauv.tgnet.TLObject r13 = r12.photoThumbsObject
            im.bclpbkiauv.messenger.ImageLocation r13 = im.bclpbkiauv.messenger.ImageLocation.getForObject(r14, r13)
            r3.add(r4, r13)
            int r3 = r0.currentImage
            int r3 = r3 + r15
            r0.currentImage = r3
            int r2 = r2 + -1
            r4 = r17
            r3 = r18
            r13 = 56
            goto L_0x021d
        L_0x0260:
            r18 = r3
            r17 = r4
        L_0x0264:
            r14 = r6
            goto L_0x02dd
        L_0x0267:
            r17 = r4
            if (r5 == 0) goto L_0x02dc
            boolean r2 = r5.isEmpty()
            if (r2 != 0) goto L_0x02dc
            long r2 = r0.currentGroupId
            int r4 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r4 == 0) goto L_0x02da
            r2 = r1
            int r3 = r5.size()
        L_0x027c:
            if (r2 >= r3) goto L_0x02a4
            java.lang.Object r4 = r5.get(r2)
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r4 = (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r4
            int r7 = r4.groupId
            long r12 = (long) r7
            r14 = r6
            long r6 = r0.currentGroupId
            int r18 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r18 != 0) goto L_0x02a5
            java.util.ArrayList<java.lang.Object> r6 = r0.currentObjects
            r6.add(r4)
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r6 = r0.currentPhotos
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r4.thumb
            im.bclpbkiauv.tgnet.TLObject r12 = r4.thumbObject
            im.bclpbkiauv.messenger.ImageLocation r7 = im.bclpbkiauv.messenger.ImageLocation.getForObject(r7, r12)
            r6.add(r7)
            int r2 = r2 + 1
            r6 = r14
            goto L_0x027c
        L_0x02a4:
            r14 = r6
        L_0x02a5:
            r2 = 0
            r0.currentImage = r2
            r2 = -1
            r0.animateToItem = r2
            int r2 = r1 + -1
        L_0x02ad:
            if (r2 < 0) goto L_0x02dd
            java.lang.Object r3 = r5.get(r2)
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r3 = (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r3
            int r4 = r3.groupId
            long r6 = (long) r4
            long r12 = r0.currentGroupId
            int r4 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r4 != 0) goto L_0x02dd
            java.util.ArrayList<java.lang.Object> r4 = r0.currentObjects
            r6 = 0
            r4.add(r6, r3)
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r4 = r0.currentPhotos
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r3.thumb
            im.bclpbkiauv.tgnet.TLObject r12 = r3.thumbObject
            im.bclpbkiauv.messenger.ImageLocation r7 = im.bclpbkiauv.messenger.ImageLocation.getForObject(r7, r12)
            r4.add(r6, r7)
            int r4 = r0.currentImage
            r6 = 1
            int r4 = r4 + r6
            r0.currentImage = r4
            int r2 = r2 + -1
            goto L_0x02ad
        L_0x02da:
            r14 = r6
            goto L_0x02dd
        L_0x02dc:
            r14 = r6
        L_0x02dd:
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r2 = r0.currentPhotos
            int r2 = r2.size()
            r3 = 1
            if (r2 != r3) goto L_0x02f0
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r2 = r0.currentPhotos
            r2.clear()
            java.util.ArrayList<java.lang.Object> r2 = r0.currentObjects
            r2.clear()
        L_0x02f0:
            r2 = 0
            r0.fillImages(r2, r2)
            goto L_0x02fa
        L_0x02f5:
            r14 = r6
            r11 = r17
            r17 = r4
        L_0x02fa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.GroupedPhotosListView.fillList():void");
    }

    public void setMoveProgress(float progress) {
        if (!this.scrolling && this.animateToItem < 0) {
            if (progress > 0.0f) {
                this.nextImage = this.currentImage - 1;
            } else {
                this.nextImage = this.currentImage + 1;
            }
            int i = this.nextImage;
            if (i < 0 || i >= this.currentPhotos.size()) {
                this.currentItemProgress = 1.0f;
            } else {
                this.currentItemProgress = 1.0f - Math.abs(progress);
            }
            this.nextItemProgress = 1.0f - this.currentItemProgress;
            this.moving = progress != 0.0f;
            invalidate();
            if (this.currentPhotos.isEmpty()) {
                return;
            }
            if (progress < 0.0f && this.currentImage == this.currentPhotos.size() - 1) {
                return;
            }
            if (progress <= 0.0f || this.currentImage != 0) {
                int i2 = (int) (((float) (this.itemWidth + this.itemSpacing)) * progress);
                this.drawDx = i2;
                fillImages(true, i2);
            }
        }
    }

    private ImageReceiver getFreeReceiver() {
        ImageReceiver receiver;
        if (this.unusedReceivers.isEmpty()) {
            receiver = new ImageReceiver(this);
        } else {
            receiver = this.unusedReceivers.get(0);
            this.unusedReceivers.remove(0);
        }
        this.imagesToDraw.add(receiver);
        receiver.setCurrentAccount(this.delegate.getCurrentAccount());
        return receiver;
    }

    private void fillImages(boolean move, int dx) {
        int addLeftIndex;
        int addRightIndex;
        Object parent;
        Object parent2;
        int i = 0;
        if (!move && !this.imagesToDraw.isEmpty()) {
            this.unusedReceivers.addAll(this.imagesToDraw);
            this.imagesToDraw.clear();
            this.moving = false;
            this.moveLineProgress = 1.0f;
            this.currentItemProgress = 1.0f;
            this.nextItemProgress = 0.0f;
        }
        invalidate();
        if (getMeasuredWidth() != 0 && !this.currentPhotos.isEmpty()) {
            int width = getMeasuredWidth();
            int startX = (getMeasuredWidth() / 2) - (this.itemWidth / 2);
            if (move) {
                addRightIndex = Integer.MIN_VALUE;
                addLeftIndex = Integer.MAX_VALUE;
                int count = this.imagesToDraw.size();
                int a = 0;
                while (a < count) {
                    ImageReceiver receiver = this.imagesToDraw.get(a);
                    int num = receiver.getParam();
                    int i2 = this.itemWidth;
                    int x = ((num - this.currentImage) * (this.itemSpacing + i2)) + startX + dx;
                    if (x > width || i2 + x < 0) {
                        this.unusedReceivers.add(receiver);
                        this.imagesToDraw.remove(a);
                        count--;
                        a--;
                    }
                    addLeftIndex = Math.min(addLeftIndex, num - 1);
                    addRightIndex = Math.max(addRightIndex, num + 1);
                    a++;
                }
            } else {
                addRightIndex = this.currentImage;
                addLeftIndex = this.currentImage - 1;
            }
            if (addRightIndex != Integer.MIN_VALUE) {
                int count2 = this.currentPhotos.size();
                int a2 = addRightIndex;
                while (a2 < count2) {
                    int x2 = ((a2 - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX + dx;
                    if (x2 >= width) {
                        break;
                    }
                    ImageLocation location = this.currentPhotos.get(a2);
                    ImageReceiver receiver2 = getFreeReceiver();
                    receiver2.setImageCoords(x2, this.itemY, this.itemWidth, this.itemHeight);
                    if (this.currentObjects.get(i) instanceof MessageObject) {
                        parent2 = this.currentObjects.get(a2);
                    } else if (this.currentObjects.get(i) instanceof TLRPC.PageBlock) {
                        parent2 = this.delegate.getParentObject();
                    } else {
                        parent2 = "avatar_" + this.delegate.getAvatarsDialogId();
                    }
                    receiver2.setImage((ImageLocation) null, (String) null, location, "80_80", 0, (String) null, parent2, 1);
                    receiver2.setParam(a2);
                    a2++;
                    i = 0;
                }
            }
            if (addLeftIndex != Integer.MAX_VALUE) {
                int a3 = addLeftIndex;
                while (a3 >= 0) {
                    int i3 = this.itemWidth;
                    int x3 = ((a3 - this.currentImage) * (this.itemSpacing + i3)) + startX + dx + i3;
                    if (x3 > 0) {
                        ImageLocation location2 = this.currentPhotos.get(a3);
                        ImageReceiver receiver3 = getFreeReceiver();
                        receiver3.setImageCoords(x3, this.itemY, this.itemWidth, this.itemHeight);
                        if (this.currentObjects.get(0) instanceof MessageObject) {
                            parent = this.currentObjects.get(a3);
                        } else if (this.currentObjects.get(0) instanceof TLRPC.PageBlock) {
                            parent = this.delegate.getParentObject();
                        } else {
                            parent = "avatar_" + this.delegate.getAvatarsDialogId();
                        }
                        receiver3.setImage((ImageLocation) null, (String) null, location2, "80_80", 0, (String) null, parent, 1);
                        receiver3.setParam(a3);
                        a3--;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public boolean onDown(MotionEvent e) {
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        this.animateToItem = -1;
        return true;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<ImageLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        ArrayList<TLRPC.PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        stopScrolling();
        int count = this.imagesToDraw.size();
        int a = 0;
        while (a < count) {
            ImageReceiver receiver = this.imagesToDraw.get(a);
            if (receiver.isInsideImage(e.getX(), e.getY())) {
                int num = receiver.getParam();
                if (num < 0 || num >= this.currentObjects.size()) {
                    return true;
                }
                if (imagesArr != null && !imagesArr.isEmpty()) {
                    int idx = imagesArr.indexOf((MessageObject) this.currentObjects.get(num));
                    if (currentIndex == idx) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx);
                    return false;
                } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                    int idx2 = pageBlockArr.indexOf((TLRPC.PageBlock) this.currentObjects.get(num));
                    if (currentIndex == idx2) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx2);
                    return false;
                } else if (imagesArrLocations == null || imagesArrLocations.isEmpty()) {
                    return false;
                } else {
                    int idx3 = imagesArrLocations.indexOf((ImageLocation) this.currentObjects.get(num));
                    if (currentIndex == idx3) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx3);
                    return false;
                }
            } else {
                a++;
            }
        }
        return false;
    }

    private void updateAfterScroll() {
        int dx;
        int indexChange;
        int indexChange2 = 0;
        int dx2 = this.drawDx;
        int abs = Math.abs(dx2);
        int i = this.itemWidth;
        int i2 = this.itemSpacing;
        if (abs > (i / 2) + i2) {
            if (dx2 > 0) {
                dx = dx2 - ((i / 2) + i2);
                indexChange = 0 + 1;
            } else {
                dx = dx2 + (i / 2) + i2;
                indexChange = 0 - 1;
            }
            indexChange2 = indexChange + (dx / (this.itemWidth + (this.itemSpacing * 2)));
        }
        this.nextPhotoScrolling = this.currentImage - indexChange2;
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<ImageLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        ArrayList<TLRPC.PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        int i3 = this.nextPhotoScrolling;
        if (currentIndex != i3 && i3 >= 0 && i3 < this.currentPhotos.size()) {
            Object photo = this.currentObjects.get(this.nextPhotoScrolling);
            int nextPhoto = -1;
            if (imagesArr != null && !imagesArr.isEmpty()) {
                nextPhoto = imagesArr.indexOf((MessageObject) photo);
            } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                nextPhoto = pageBlockArr.indexOf((TLRPC.PageBlock) photo);
            } else if (imagesArrLocations != null && !imagesArrLocations.isEmpty()) {
                nextPhoto = imagesArrLocations.indexOf((ImageLocation) photo);
            }
            if (nextPhoto >= 0) {
                this.ignoreChanges = true;
                this.delegate.setCurrentIndex(nextPhoto);
            }
        }
        if (!this.scrolling) {
            this.scrolling = true;
            this.stopedScrolling = false;
        }
        fillImages(true, this.drawDx);
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        this.drawDx = (int) (((float) this.drawDx) - distanceX);
        int min = getMinScrollX();
        int max = getMaxScrollX();
        int i = this.drawDx;
        if (i < min) {
            this.drawDx = min;
        } else if (i > max) {
            this.drawDx = max;
        }
        updateAfterScroll();
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.scroll.abortAnimation();
        if (this.currentPhotos.size() < 10) {
            return false;
        }
        this.scroll.fling(this.drawDx, 0, Math.round(velocityX), 0, getMinScrollX(), getMaxScrollX(), 0, 0);
        return false;
    }

    private void stopScrolling() {
        this.scrolling = false;
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        int i = this.nextPhotoScrolling;
        if (i >= 0 && i < this.currentObjects.size()) {
            this.stopedScrolling = true;
            int i2 = this.nextPhotoScrolling;
            this.animateToItem = i2;
            this.nextImage = i2;
            this.animateToDX = (this.currentImage - i2) * (this.itemWidth + this.itemSpacing);
            this.animateToDXStart = this.drawDx;
            this.moveLineProgress = 1.0f;
            this.nextPhotoScrolling = -1;
        }
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean z = false;
        if (this.currentPhotos.isEmpty() || getAlpha() != 1.0f) {
            return false;
        }
        if (this.gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)) {
            z = true;
        }
        boolean result = z;
        if (this.scrolling && event.getAction() == 1 && this.scroll.isFinished()) {
            stopScrolling();
        }
        return result;
    }

    private int getMinScrollX() {
        return (-((this.currentPhotos.size() - this.currentImage) - 1)) * (this.itemWidth + (this.itemSpacing * 2));
    }

    private int getMaxScrollX() {
        return this.currentImage * (this.itemWidth + (this.itemSpacing * 2));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        fillImages(false, 0);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int trueWidth;
        int nextTrueWidth;
        int maxItemWidth;
        int count;
        if (!this.imagesToDraw.isEmpty()) {
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
            int count2 = this.imagesToDraw.size();
            int moveX = this.drawDx;
            int maxItemWidth2 = (int) (((float) this.itemWidth) * 2.0f);
            int padding = AndroidUtilities.dp(8.0f);
            ImageLocation object = this.currentPhotos.get(this.currentImage);
            if (object == null || object.photoSize == null) {
                trueWidth = this.itemHeight;
            } else {
                trueWidth = Math.max(this.itemWidth, (int) (((float) object.photoSize.w) * (((float) this.itemHeight) / ((float) object.photoSize.h))));
            }
            int trueWidth2 = Math.min(maxItemWidth2, trueWidth);
            float f = this.currentItemProgress;
            int currentPaddings = (int) (((float) (padding * 2)) * f);
            int i = this.itemWidth;
            int trueWidth3 = i + ((int) (((float) (trueWidth2 - i)) * f)) + currentPaddings;
            int trueWidth4 = this.nextImage;
            if (trueWidth4 < 0 || trueWidth4 >= this.currentPhotos.size()) {
                nextTrueWidth = this.itemWidth;
            } else {
                ImageLocation object2 = this.currentPhotos.get(this.nextImage);
                if (object2 == null || object2.photoSize == null) {
                    nextTrueWidth = this.itemHeight;
                } else {
                    nextTrueWidth = Math.max(this.itemWidth, (int) (((float) object2.photoSize.w) * (((float) this.itemHeight) / ((float) object2.photoSize.h))));
                }
            }
            int nextTrueWidth2 = Math.min(maxItemWidth2, nextTrueWidth);
            float f2 = this.nextItemProgress;
            int nextPaddings = (int) (((float) (padding * 2)) * f2);
            int moveX2 = (int) (((float) moveX) + (((float) (((nextTrueWidth2 + nextPaddings) - this.itemWidth) / 2)) * f2 * ((float) (this.nextImage > this.currentImage ? -1 : 1))));
            int i2 = this.itemWidth;
            int nextTrueWidth3 = i2 + ((int) (((float) (nextTrueWidth2 - i2)) * this.nextItemProgress)) + nextPaddings;
            int startX = (getMeasuredWidth() - trueWidth3) / 2;
            int a = 0;
            while (a < count2) {
                ImageReceiver receiver = this.imagesToDraw.get(a);
                int num = receiver.getParam();
                int i3 = this.currentImage;
                if (num == i3) {
                    receiver.setImageX(startX + moveX2 + (currentPaddings / 2));
                    receiver.setImageWidth(trueWidth3 - currentPaddings);
                    count = count2;
                    maxItemWidth = maxItemWidth2;
                } else {
                    int i4 = this.nextImage;
                    if (i4 >= i3) {
                        count = count2;
                        maxItemWidth = maxItemWidth2;
                        if (num < i3) {
                            receiver.setImageX(((receiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX + moveX2);
                        } else if (num <= i4) {
                            receiver.setImageX(startX + trueWidth3 + this.itemSpacing + (((receiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing)) + moveX2);
                        } else {
                            int i5 = this.itemWidth;
                            int i6 = this.itemSpacing;
                            receiver.setImageX(startX + trueWidth3 + this.itemSpacing + (((receiver.getParam() - this.currentImage) - 2) * (i5 + i6)) + i6 + nextTrueWidth3 + moveX2);
                        }
                    } else if (num >= i3) {
                        count = count2;
                        maxItemWidth = maxItemWidth2;
                        receiver.setImageX(startX + trueWidth3 + this.itemSpacing + (((receiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing)) + moveX2);
                    } else if (num <= i4) {
                        int i7 = this.itemWidth;
                        count = count2;
                        int count3 = this.itemSpacing;
                        receiver.setImageX((((((receiver.getParam() - this.currentImage) + 1) * (i7 + count3)) + startX) - (count3 + nextTrueWidth3)) + moveX2);
                        maxItemWidth = maxItemWidth2;
                    } else {
                        count = count2;
                        receiver.setImageX(((receiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX + moveX2);
                        maxItemWidth = maxItemWidth2;
                    }
                    if (num == this.nextImage) {
                        receiver.setImageWidth(nextTrueWidth3 - nextPaddings);
                        receiver.setImageX(receiver.getImageX() + (nextPaddings / 2));
                    } else {
                        receiver.setImageWidth(this.itemWidth);
                    }
                }
                receiver.draw(canvas);
                a++;
                count2 = count;
                maxItemWidth2 = maxItemWidth;
            }
            int i8 = maxItemWidth2;
            Canvas canvas2 = canvas;
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            int i9 = this.animateToItem;
            if (i9 >= 0) {
                float f3 = this.moveLineProgress;
                if (f3 > 0.0f) {
                    this.moveLineProgress = f3 - (((float) dt) / 200.0f);
                    if (i9 == this.currentImage) {
                        float f4 = this.currentItemProgress;
                        if (f4 < 1.0f) {
                            float f5 = f4 + (((float) dt) / 200.0f);
                            this.currentItemProgress = f5;
                            if (f5 > 1.0f) {
                                this.currentItemProgress = 1.0f;
                            }
                        }
                        int i10 = this.animateToDXStart;
                        int i11 = moveX2;
                        this.drawDx = i10 + ((int) Math.ceil((double) (this.currentItemProgress * ((float) (this.animateToDX - i10)))));
                    } else {
                        this.nextItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - this.moveLineProgress);
                        if (this.stopedScrolling) {
                            float f6 = this.currentItemProgress;
                            if (f6 > 0.0f) {
                                float f7 = f6 - (((float) dt) / 200.0f);
                                this.currentItemProgress = f7;
                                if (f7 < 0.0f) {
                                    this.currentItemProgress = 0.0f;
                                }
                            }
                            int i12 = this.animateToDXStart;
                            this.drawDx = i12 + ((int) Math.ceil((double) (this.nextItemProgress * ((float) (this.animateToDX - i12)))));
                        } else {
                            this.currentItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.moveLineProgress);
                            this.drawDx = (int) Math.ceil((double) (this.nextItemProgress * ((float) this.animateToDX)));
                        }
                    }
                    if (this.moveLineProgress <= 0.0f) {
                        this.currentImage = this.animateToItem;
                        this.moveLineProgress = 1.0f;
                        this.currentItemProgress = 1.0f;
                        this.nextItemProgress = 0.0f;
                        this.moving = false;
                        this.stopedScrolling = false;
                        this.drawDx = 0;
                        this.animateToItem = -1;
                    }
                }
                fillImages(true, this.drawDx);
                invalidate();
            }
            if (this.scrolling) {
                float f8 = this.currentItemProgress;
                if (f8 > 0.0f) {
                    float f9 = f8 - (((float) dt) / 200.0f);
                    this.currentItemProgress = f9;
                    if (f9 < 0.0f) {
                        this.currentItemProgress = 0.0f;
                    }
                    invalidate();
                }
            }
            if (!this.scroll.isFinished()) {
                if (this.scroll.computeScrollOffset()) {
                    this.drawDx = this.scroll.getCurrX();
                    updateAfterScroll();
                    invalidate();
                }
                if (this.scroll.isFinished()) {
                    stopScrolling();
                }
            }
        }
    }

    public void setDelegate(GroupedPhotosListViewDelegate groupedPhotosListViewDelegate) {
        this.delegate = groupedPhotosListViewDelegate;
    }
}
