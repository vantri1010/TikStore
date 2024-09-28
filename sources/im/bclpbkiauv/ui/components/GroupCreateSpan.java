package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.ui.actionbar.Theme;

public class GroupCreateSpan extends View {
    private static Paint backPaint = new Paint(1);
    private static TextPaint textPaint = new TextPaint(1);
    private AvatarDrawable avatarDrawable;
    private int[] colors;
    private ContactsController.Contact currentContact;
    private Drawable deleteDrawable;
    private boolean deleting;
    private ImageReceiver imageReceiver;
    private String key;
    private long lastUpdateTime;
    private StaticLayout nameLayout;
    private float progress;
    private RectF rect;
    private int textWidth;
    private float textX;
    private int uid;

    public GroupCreateSpan(Context context, TLObject object) {
        this(context, object, (ContactsController.Contact) null);
    }

    public GroupCreateSpan(Context context, ContactsController.Contact contact) {
        this(context, (TLObject) null, contact);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v21, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v22, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public GroupCreateSpan(android.content.Context r26, im.bclpbkiauv.tgnet.TLObject r27, im.bclpbkiauv.messenger.ContactsController.Contact r28) {
        /*
            r25 = this;
            r0 = r25
            r1 = r27
            r2 = r28
            r25.<init>(r26)
            android.graphics.RectF r3 = new android.graphics.RectF
            r3.<init>()
            r0.rect = r3
            r3 = 8
            int[] r3 = new int[r3]
            r0.colors = r3
            r0.currentContact = r2
            android.content.res.Resources r3 = r25.getResources()
            r4 = 2131230972(0x7f0800fc, float:1.8078012E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r4)
            r0.deleteDrawable = r3
            android.text.TextPaint r3 = textPaint
            r4 = 1096810496(0x41600000, float:14.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r3.setTextSize(r4)
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = new im.bclpbkiauv.ui.components.AvatarDrawable
            r3.<init>()
            r0.avatarDrawable = r3
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r3.setTextSize(r4)
            boolean r3 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.User
            r4 = 0
            if (r3 == 0) goto L_0x005c
            r3 = r1
            im.bclpbkiauv.tgnet.TLRPC$User r3 = (im.bclpbkiauv.tgnet.TLRPC.User) r3
            im.bclpbkiauv.ui.components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setInfo((im.bclpbkiauv.tgnet.TLRPC.User) r3)
            int r5 = r3.id
            r0.uid = r5
            java.lang.String r5 = im.bclpbkiauv.messenger.UserObject.getFirstName(r3)
            im.bclpbkiauv.messenger.ImageLocation r6 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r3, r4)
            goto L_0x0097
        L_0x005c:
            boolean r3 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.Chat
            if (r3 == 0) goto L_0x0075
            r3 = r1
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r3
            im.bclpbkiauv.ui.components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setInfo((im.bclpbkiauv.tgnet.TLRPC.Chat) r3)
            int r5 = r3.id
            int r5 = -r5
            r0.uid = r5
            java.lang.String r5 = r3.title
            im.bclpbkiauv.messenger.ImageLocation r6 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r3, r4)
            goto L_0x0097
        L_0x0075:
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r0.avatarDrawable
            java.lang.String r5 = r2.first_name
            java.lang.String r6 = r2.last_name
            r3.setInfo(r4, r5, r6)
            int r3 = r2.contact_id
            r0.uid = r3
            java.lang.String r3 = r2.key
            r0.key = r3
            java.lang.String r3 = r2.first_name
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0092
            java.lang.String r3 = r2.first_name
            r5 = r3
            goto L_0x0095
        L_0x0092:
            java.lang.String r3 = r2.last_name
            r5 = r3
        L_0x0095:
            r6 = 0
            r3 = 0
        L_0x0097:
            im.bclpbkiauv.messenger.ImageReceiver r7 = new im.bclpbkiauv.messenger.ImageReceiver
            r7.<init>()
            r0.imageReceiver = r7
            r8 = 1089470464(0x40f00000, float:7.5)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r7.setRoundRadius(r8)
            im.bclpbkiauv.messenger.ImageReceiver r7 = r0.imageReceiver
            r7.setParentView(r0)
            im.bclpbkiauv.messenger.ImageReceiver r7 = r0.imageReceiver
            r8 = 1107296256(0x42000000, float:32.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r7.setImageCoords(r4, r4, r9, r8)
            boolean r7 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r7 == 0) goto L_0x00cb
            r7 = 1136066560(0x43b70000, float:366.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 / 2
            r15 = r7
            goto L_0x00e1
        L_0x00cb:
            android.graphics.Point r7 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r7 = r7.x
            android.graphics.Point r8 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r8 = r8.y
            int r7 = java.lang.Math.min(r7, r8)
            r8 = 1126432768(0x43240000, float:164.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 - r8
            int r7 = r7 / 2
            r15 = r7
        L_0x00e1:
            r7 = 10
            r8 = 32
            java.lang.String r7 = r5.replace(r7, r8)
            android.text.TextPaint r8 = textPaint
            float r9 = (float) r15
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r24 = android.text.TextUtils.ellipsize(r7, r8, r9, r10)
            android.text.StaticLayout r7 = new android.text.StaticLayout
            android.text.TextPaint r18 = textPaint
            r19 = 1000(0x3e8, float:1.401E-42)
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_NORMAL
            r21 = 1065353216(0x3f800000, float:1.0)
            r22 = 0
            r23 = 0
            r16 = r7
            r17 = r24
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r0.nameLayout = r7
            int r7 = r7.getLineCount()
            if (r7 <= 0) goto L_0x0126
            android.text.StaticLayout r7 = r0.nameLayout
            float r7 = r7.getLineWidth(r4)
            double r7 = (double) r7
            double r7 = java.lang.Math.ceil(r7)
            int r7 = (int) r7
            r0.textWidth = r7
            android.text.StaticLayout r7 = r0.nameLayout
            float r4 = r7.getLineLeft(r4)
            float r4 = -r4
            r0.textX = r4
        L_0x0126:
            im.bclpbkiauv.messenger.ImageReceiver r7 = r0.imageReceiver
            im.bclpbkiauv.ui.components.AvatarDrawable r10 = r0.avatarDrawable
            r11 = 0
            r12 = 0
            r14 = 1
            java.lang.String r9 = "50_50"
            r8 = r6
            r13 = r3
            r7.setImage((im.bclpbkiauv.messenger.ImageLocation) r8, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (int) r11, (java.lang.String) r12, (java.lang.Object) r13, (int) r14)
            r25.updateColors()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.GroupCreateSpan.<init>(android.content.Context, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.messenger.ContactsController$Contact):void");
    }

    public void updateColors() {
        int color = Theme.getColor(Theme.key_avatar_backgroundGroupCreateSpanBlue);
        int back = Theme.getColor(Theme.key_groupcreate_spanBackground);
        int text = Theme.getColor(Theme.key_groupcreate_spanText);
        int delete = Theme.getColor(Theme.key_groupcreate_spanDelete);
        this.colors[0] = Color.red(back);
        this.colors[1] = Color.red(color);
        this.colors[2] = Color.green(back);
        this.colors[3] = Color.green(color);
        this.colors[4] = Color.blue(back);
        this.colors[5] = Color.blue(color);
        this.colors[6] = Color.alpha(back);
        this.colors[7] = Color.alpha(color);
        textPaint.setColor(text);
        this.deleteDrawable.setColorFilter(new PorterDuffColorFilter(delete, PorterDuff.Mode.MULTIPLY));
        backPaint.setColor(back);
        this.avatarDrawable.setColor(AvatarDrawable.getColorForId(5));
    }

    public boolean isDeleting() {
        return this.deleting;
    }

    public void startDeleteAnimation() {
        if (!this.deleting) {
            this.deleting = true;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }
    }

    public void cancelDeleteAnimation() {
        if (this.deleting) {
            this.deleting = false;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }
    }

    public int getUid() {
        return this.uid;
    }

    public String getKey() {
        return this.key;
    }

    public ContactsController.Contact getContact() {
        return this.currentContact;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(AndroidUtilities.dp(57.0f) + this.textWidth, AndroidUtilities.dp(32.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if ((this.deleting && this.progress != 1.0f) || (!this.deleting && this.progress != 0.0f)) {
            long dt = System.currentTimeMillis() - this.lastUpdateTime;
            if (dt < 0 || dt > 17) {
                dt = 17;
            }
            if (this.deleting) {
                float f = this.progress + (((float) dt) / 120.0f);
                this.progress = f;
                if (f >= 1.0f) {
                    this.progress = 1.0f;
                }
            } else {
                float f2 = this.progress - (((float) dt) / 120.0f);
                this.progress = f2;
                if (f2 < 0.0f) {
                    this.progress = 0.0f;
                }
            }
            invalidate();
        }
        canvas.save();
        this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(32.0f));
        Paint paint = backPaint;
        int[] iArr = this.colors;
        int i = iArr[6];
        float f3 = (float) (iArr[7] - iArr[6]);
        float f4 = this.progress;
        paint.setColor(Color.argb(i + ((int) (f3 * f4)), iArr[0] + ((int) (((float) (iArr[1] - iArr[0])) * f4)), iArr[2] + ((int) (((float) (iArr[3] - iArr[2])) * f4)), iArr[4] + ((int) (((float) (iArr[5] - iArr[4])) * f4))));
        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), backPaint);
        this.imageReceiver.draw(canvas);
        if (this.progress != 0.0f) {
            int color = this.avatarDrawable.getColor();
            backPaint.setColor(color);
            backPaint.setAlpha((int) (this.progress * 255.0f * (((float) Color.alpha(color)) / 255.0f)));
            canvas.drawCircle((float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), backPaint);
            canvas.save();
            canvas.rotate((1.0f - this.progress) * 45.0f, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f));
            this.deleteDrawable.setBounds(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(21.0f));
            this.deleteDrawable.setAlpha((int) (this.progress * 255.0f));
            this.deleteDrawable.draw(canvas);
            canvas.restore();
        }
        canvas.translate(this.textX + ((float) AndroidUtilities.dp(41.0f)), (float) AndroidUtilities.dp(8.0f));
        this.nameLayout.draw(canvas);
        canvas.restore();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setText(this.nameLayout.getText());
        if (isDeleting() && Build.VERSION.SDK_INT >= 21) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK.getId(), LocaleController.getString("Delete", R.string.Delete)));
        }
    }
}
