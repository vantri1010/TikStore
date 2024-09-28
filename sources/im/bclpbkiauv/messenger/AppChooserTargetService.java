package im.bclpbkiauv.messenger;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Icon;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import im.bclpbkiauv.ui.LaunchActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AppChooserTargetService extends ChooserTargetService {
    private RectF bitmapRect;
    private Paint roundPaint;

    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName, IntentFilter matchedFilter) {
        int currentAccount = UserConfig.selectedAccount;
        List<ChooserTarget> targets = new ArrayList<>();
        if (!UserConfig.getInstance(currentAccount).isClientActivated() || !MessagesController.getGlobalMainSettings().getBoolean("direct_share", true)) {
            return targets;
        }
        ImageLoader instance = ImageLoader.getInstance();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new Runnable(currentAccount, targets, new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName()), countDownLatch) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ List f$2;
            private final /* synthetic */ ComponentName f$3;
            private final /* synthetic */ CountDownLatch f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                AppChooserTargetService.this.lambda$onGetChooserTargets$0$AppChooserTargetService(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return targets;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0193, code lost:
        r14 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onGetChooserTargets$0$AppChooserTargetService(int r17, java.util.List r18, android.content.ComponentName r19, java.util.concurrent.CountDownLatch r20) {
        /*
            r16 = this;
            r1 = r16
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r2 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r4 = r0
            r5 = 0
            r6 = 1
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x00c8 }
            r0.<init>()     // Catch:{ Exception -> 0x00c8 }
            im.bclpbkiauv.messenger.UserConfig r7 = im.bclpbkiauv.messenger.UserConfig.getInstance(r17)     // Catch:{ Exception -> 0x00c8 }
            int r7 = r7.getClientUserId()     // Catch:{ Exception -> 0x00c8 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x00c8 }
            r0.add(r7)     // Catch:{ Exception -> 0x00c8 }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x00c8 }
            r7.<init>()     // Catch:{ Exception -> 0x00c8 }
            im.bclpbkiauv.messenger.MessagesStorage r8 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r17)     // Catch:{ Exception -> 0x00c8 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r8 = r8.getDatabase()     // Catch:{ Exception -> 0x00c8 }
            java.util.Locale r9 = java.util.Locale.US     // Catch:{ Exception -> 0x00c8 }
            java.lang.String r10 = "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d"
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x00c8 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x00c8 }
            r11[r5] = r12     // Catch:{ Exception -> 0x00c8 }
            r12 = 30
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ Exception -> 0x00c8 }
            r11[r6] = r12     // Catch:{ Exception -> 0x00c8 }
            java.lang.String r9 = java.lang.String.format(r9, r10, r11)     // Catch:{ Exception -> 0x00c8 }
            java.lang.Object[] r10 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x00c8 }
            im.bclpbkiauv.sqlite.SQLiteCursor r8 = r8.queryFinalized(r9, r10)     // Catch:{ Exception -> 0x00c8 }
        L_0x0056:
            boolean r9 = r8.next()     // Catch:{ Exception -> 0x00c8 }
            if (r9 == 0) goto L_0x00a0
            long r9 = r8.longValue(r5)     // Catch:{ Exception -> 0x00c8 }
            int r11 = (int) r9     // Catch:{ Exception -> 0x00c8 }
            r12 = 32
            long r12 = r9 >> r12
            int r13 = (int) r12     // Catch:{ Exception -> 0x00c8 }
            if (r11 == 0) goto L_0x0056
            if (r11 <= 0) goto L_0x007c
            java.lang.Integer r12 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x00c8 }
            boolean r12 = r0.contains(r12)     // Catch:{ Exception -> 0x00c8 }
            if (r12 != 0) goto L_0x008f
            java.lang.Integer r12 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x00c8 }
            r0.add(r12)     // Catch:{ Exception -> 0x00c8 }
            goto L_0x008f
        L_0x007c:
            int r12 = -r11
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ Exception -> 0x00c8 }
            boolean r12 = r7.contains(r12)     // Catch:{ Exception -> 0x00c8 }
            if (r12 != 0) goto L_0x008f
            int r12 = -r11
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ Exception -> 0x00c8 }
            r7.add(r12)     // Catch:{ Exception -> 0x00c8 }
        L_0x008f:
            java.lang.Integer r12 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x00c8 }
            r2.add(r12)     // Catch:{ Exception -> 0x00c8 }
            int r12 = r2.size()     // Catch:{ Exception -> 0x00c8 }
            r14 = 8
            if (r12 != r14) goto L_0x009f
            goto L_0x00a0
        L_0x009f:
            goto L_0x0056
        L_0x00a0:
            r8.dispose()     // Catch:{ Exception -> 0x00c8 }
            boolean r9 = r7.isEmpty()     // Catch:{ Exception -> 0x00c8 }
            java.lang.String r10 = ","
            if (r9 != 0) goto L_0x00b6
            im.bclpbkiauv.messenger.MessagesStorage r9 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r17)     // Catch:{ Exception -> 0x00c8 }
            java.lang.String r11 = android.text.TextUtils.join(r10, r7)     // Catch:{ Exception -> 0x00c8 }
            r9.getChatsInternal(r11, r3)     // Catch:{ Exception -> 0x00c8 }
        L_0x00b6:
            boolean r9 = r0.isEmpty()     // Catch:{ Exception -> 0x00c8 }
            if (r9 != 0) goto L_0x00c7
            im.bclpbkiauv.messenger.MessagesStorage r9 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r17)     // Catch:{ Exception -> 0x00c8 }
            java.lang.String r10 = android.text.TextUtils.join(r10, r0)     // Catch:{ Exception -> 0x00c8 }
            r9.getUsersInternal(r10, r4)     // Catch:{ Exception -> 0x00c8 }
        L_0x00c7:
            goto L_0x00cc
        L_0x00c8:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00cc:
            java.security.SecureRandom r0 = im.bclpbkiauv.messenger.Utilities.random
            long r7 = r0.nextLong()
            im.bclpbkiauv.messenger.SharedConfig.directShareHash = r7
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.String r7 = "mainconfig"
            android.content.SharedPreferences r0 = r0.getSharedPreferences(r7, r5)
            android.content.SharedPreferences$Editor r0 = r0.edit()
            long r7 = im.bclpbkiauv.messenger.SharedConfig.directShareHash
            java.lang.String r5 = "directShareHash"
            android.content.SharedPreferences$Editor r0 = r0.putLong(r5, r7)
            r0.commit()
            r0 = 0
        L_0x00ec:
            int r5 = r2.size()
            if (r0 >= r5) goto L_0x01bf
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            r7 = 0
            r8 = 0
            java.lang.Object r9 = r2.get(r0)
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r13 = r9.intValue()
            java.lang.String r9 = "hash"
            java.lang.String r10 = "dialogId"
            if (r13 <= 0) goto L_0x014b
            r11 = 0
        L_0x010a:
            int r12 = r4.size()
            if (r11 >= r12) goto L_0x0149
            java.lang.Object r12 = r4.get(r11)
            im.bclpbkiauv.tgnet.TLRPC$User r12 = (im.bclpbkiauv.tgnet.TLRPC.User) r12
            int r14 = r12.id
            if (r14 != r13) goto L_0x0146
            boolean r14 = r12.bot
            if (r14 != 0) goto L_0x0149
            long r14 = (long) r13
            r5.putLong(r10, r14)
            long r14 = im.bclpbkiauv.messenger.SharedConfig.directShareHash
            r5.putLong(r9, r14)
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r9 = r12.photo
            if (r9 == 0) goto L_0x013d
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r9 = r12.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            if (r9 == 0) goto L_0x013d
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r9 = r12.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            java.io.File r9 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r9, r6)
            android.graphics.drawable.Icon r7 = r1.createRoundBitmap(r9)
        L_0x013d:
            java.lang.String r9 = r12.first_name
            java.lang.String r10 = r12.last_name
            java.lang.String r8 = im.bclpbkiauv.messenger.ContactsController.formatName(r9, r10)
            goto L_0x0149
        L_0x0146:
            int r11 = r11 + 1
            goto L_0x010a
        L_0x0149:
            r14 = r8
            goto L_0x0194
        L_0x014b:
            r11 = 0
        L_0x014c:
            int r12 = r3.size()
            if (r11 >= r12) goto L_0x0193
            java.lang.Object r12 = r3.get(r11)
            im.bclpbkiauv.tgnet.TLRPC$Chat r12 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r12
            int r14 = r12.id
            int r15 = -r13
            if (r14 != r15) goto L_0x0190
            boolean r14 = im.bclpbkiauv.messenger.ChatObject.isNotInChat(r12)
            if (r14 != 0) goto L_0x0193
            boolean r14 = im.bclpbkiauv.messenger.ChatObject.isChannel(r12)
            if (r14 == 0) goto L_0x016d
            boolean r14 = r12.megagroup
            if (r14 == 0) goto L_0x0193
        L_0x016d:
            long r14 = (long) r13
            r5.putLong(r10, r14)
            long r14 = im.bclpbkiauv.messenger.SharedConfig.directShareHash
            r5.putLong(r9, r14)
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r9 = r12.photo
            if (r9 == 0) goto L_0x018c
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r9 = r12.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            if (r9 == 0) goto L_0x018c
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r9 = r12.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            java.io.File r9 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r9, r6)
            android.graphics.drawable.Icon r7 = r1.createRoundBitmap(r9)
        L_0x018c:
            java.lang.String r8 = r12.title
            r14 = r8
            goto L_0x0194
        L_0x0190:
            int r11 = r11 + 1
            goto L_0x014c
        L_0x0193:
            r14 = r8
        L_0x0194:
            if (r14 == 0) goto L_0x01b8
            if (r7 != 0) goto L_0x01a3
            android.content.Context r8 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r9 = 2131231226(0x7f0801fa, float:1.8078527E38)
            android.graphics.drawable.Icon r7 = android.graphics.drawable.Icon.createWithResource(r8, r9)
            r15 = r7
            goto L_0x01a4
        L_0x01a3:
            r15 = r7
        L_0x01a4:
            android.service.chooser.ChooserTarget r12 = new android.service.chooser.ChooserTarget
            r10 = 1065353216(0x3f800000, float:1.0)
            r7 = r12
            r8 = r14
            r9 = r15
            r11 = r19
            r6 = r12
            r12 = r5
            r7.<init>(r8, r9, r10, r11, r12)
            r8 = r18
            r8.add(r6)
            goto L_0x01ba
        L_0x01b8:
            r8 = r18
        L_0x01ba:
            int r0 = r0 + 1
            r6 = 1
            goto L_0x00ec
        L_0x01bf:
            r8 = r18
            r20.countDown()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.AppChooserTargetService.lambda$onGetChooserTargets$0$AppChooserTargetService(int, java.util.List, android.content.ComponentName, java.util.concurrent.CountDownLatch):void");
    }

    private Icon createRoundBitmap(File path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path.toString());
            if (bitmap == null) {
                return null;
            }
            Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            result.eraseColor(0);
            Canvas canvas = new Canvas(result);
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            if (this.roundPaint == null) {
                this.roundPaint = new Paint(1);
                this.bitmapRect = new RectF();
            }
            this.roundPaint.setShader(shader);
            this.bitmapRect.set(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
            canvas.drawRoundRect(this.bitmapRect, (float) bitmap.getWidth(), (float) bitmap.getHeight(), this.roundPaint);
            return Icon.createWithBitmap(result);
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        }
    }
}
