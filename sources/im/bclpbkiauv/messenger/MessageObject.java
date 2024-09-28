package im.bclpbkiauv.messenger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.SparseArray;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.exoplayer2.util.MimeTypes;
import com.king.zxing.util.CodeUtils;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.tgnet.TLRPCRedpacket;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.TextStyleSpan;
import im.bclpbkiauv.ui.components.URLSpanBotCommand;
import im.bclpbkiauv.ui.components.URLSpanBrowser;
import im.bclpbkiauv.ui.components.URLSpanMono;
import im.bclpbkiauv.ui.components.URLSpanNoUnderline;
import im.bclpbkiauv.ui.components.URLSpanNoUnderlineBold;
import im.bclpbkiauv.ui.components.URLSpanReplacement;
import im.bclpbkiauv.ui.components.URLSpanUserMention;
import im.bclpbkiauv.ui.hui.sysnotify.SysNotifyAtTextClickableSpan;
import im.bclpbkiauv.ui.utils.number.MoneyUtil;
import im.bclpbkiauv.ui.utils.number.NumberUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Marker;

public class MessageObject {
    private static final int LINES_PER_BLOCK = 10;
    public static final int MESSAGE_SEND_STATE_EDITING = 3;
    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    public static final int POSITION_FLAG_BOTTOM = 8;
    public static final int POSITION_FLAG_LEFT = 1;
    public static final int POSITION_FLAG_RIGHT = 2;
    public static final int POSITION_FLAG_TOP = 4;
    public static final int TYPE_ANIMATED_STICKER = 15;
    public static final int TYPE_CARD = 103;
    public static final int TYPE_LIVE = 207;
    public static final int TYPE_PAYBILL = 104;
    public static final int TYPE_POLL = 17;
    public static final int TYPE_REDPKG = 101;
    public static final int TYPE_ROUND_VIDEO = 5;
    public static final int TYPE_STICKER = 13;
    public static final int TYPE_SYSTEM_NOTIFY = 105;
    public static final int TYPE_TRANSF = 102;
    static final String[] excludeWords = {" vs. ", " vs ", " versus ", " ft. ", " ft ", " featuring ", " feat. ", " feat ", " presents ", " pres. ", " pres ", " and ", " & ", " . "};
    public static Pattern instagramUrlPattern;
    public static Pattern urlPattern;
    public static Pattern videoTimeUrlPattern;
    public boolean attachPathExists;
    public int audioPlayerDuration;
    public float audioProgress;
    public int audioProgressMs;
    public int audioProgressSec;
    public StringBuilder botButtonsLayout;
    public float bufferedProgress;
    public boolean cancelEditing;
    public CharSequence caption;
    public int contentType;
    public int currentAccount;
    public TLRPC.TL_channelAdminLogEvent currentEvent;
    public String customReplyName;
    public String dateKey;
    public Delegate delegate;
    public boolean deleted;
    public CharSequence editingMessage;
    public ArrayList<TLRPC.MessageEntity> editingMessageEntities;
    public TLRPC.Document emojiAnimatedSticker;
    public String emojiAnimatedStickerColor;
    private int emojiOnlyCount;
    ArrayList<TLRPC.MessageEntity> entitiesCopy;
    public long eventId;
    public float forceSeekTo;
    public boolean forceUpdate;
    private float generatedWithDensity;
    private int generatedWithMinSize;
    public float gifState;
    public boolean hadAnimationNotReadyLoading;
    public boolean hasRtl;
    public boolean isDateObject;
    public boolean isRestrictedMessage;
    private int isRoundVideoCached;
    public int lastLineWidth;
    private boolean layoutCreated;
    public int linesCount;
    public CharSequence linkDescription;
    public boolean localChannel;
    public boolean localEdit;
    public long localGroupId;
    public String localName;
    public long localSentGroupId;
    public int localType;
    public String localUserName;
    public boolean mediaExists;
    public TLRPC.Message messageOwner;
    public CharSequence messageText;
    public String monthKey;
    public ArrayList<TLRPC.PhotoSize> photoThumbs;
    public ArrayList<TLRPC.PhotoSize> photoThumbs2;
    public TLObject photoThumbsObject;
    public TLObject photoThumbsObject2;
    public long pollLastCheckTime;
    public boolean pollVisibleOnScreen;
    public String previousAttachPath;
    public String previousCaption;
    public ArrayList<TLRPC.MessageEntity> previousCaptionEntities;
    public TLRPC.MessageMedia previousMedia;
    public MessageObject replyMessageObject;
    public boolean resendAsIs;
    public boolean scheduled;
    public int textHeight;
    public ArrayList<TextLayoutBlock> textLayoutBlocks;
    public int textWidth;
    public float textXOffset;
    public int transHeight;
    public int transWidth;
    public int type;
    public boolean useCustomPhoto;
    public CharSequence vCardData;
    public VideoEditedInfo videoEditedInfo;
    public boolean viewsReloaded;
    public int wantedBotKeyboardWidth;

    public interface Delegate {
        void onClickRed();
    }

    public static class VCardData {
        private String company;
        private ArrayList<String> emails = new ArrayList<>();
        private ArrayList<String> phones = new ArrayList<>();

        public static CharSequence parse(String data) {
            boolean finished;
            VCardData currentData;
            boolean finished2;
            byte[] bytes;
            try {
                BufferedReader bufferedReader = new BufferedReader(new StringReader(data));
                String pendingLine = null;
                finished = false;
                currentData = null;
                while (true) {
                    String readLine = bufferedReader.readLine();
                    String line = readLine;
                    String originalLine = readLine;
                    if (readLine == null) {
                        break;
                    } else if (!originalLine.startsWith("PHOTO")) {
                        if (originalLine.indexOf(58) >= 0) {
                            if (originalLine.startsWith("BEGIN:VCARD")) {
                                currentData = new VCardData();
                            } else if (originalLine.startsWith("END:VCARD") && currentData != null) {
                                finished = true;
                            }
                        }
                        if (pendingLine != null) {
                            line = pendingLine + line;
                            pendingLine = null;
                        }
                        int i = 0;
                        if (line.contains("=QUOTED-PRINTABLE")) {
                            if (line.endsWith("=")) {
                                pendingLine = line.substring(0, line.length() - 1);
                            }
                        }
                        int idx = line.indexOf(LogUtils.COLON);
                        String[] args = idx >= 0 ? new String[]{line.substring(0, idx), line.substring(idx + 1).trim()} : new String[]{line.trim()};
                        if (args.length < 2) {
                            int i2 = idx;
                            finished2 = finished;
                        } else if (currentData == null) {
                            finished2 = finished;
                        } else if (args[0].startsWith("ORG")) {
                            String nameEncoding = null;
                            String nameCharset = null;
                            String[] params = args[0].split(";");
                            int length = params.length;
                            while (i < length) {
                                int idx2 = idx;
                                String[] args2 = params[i].split("=");
                                boolean finished3 = finished;
                                if (args2.length == 2) {
                                    if (args2[0].equals("CHARSET")) {
                                        nameCharset = args2[1];
                                    } else if (args2[0].equals("ENCODING")) {
                                        nameEncoding = args2[1];
                                    }
                                }
                                i++;
                                String str = data;
                                idx = idx2;
                                finished = finished3;
                            }
                            finished2 = finished;
                            currentData.company = args[1];
                            if (!(nameEncoding == null || !nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") || (bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(currentData.company))) == null || bytes.length == 0)) {
                                currentData.company = new String(bytes, nameCharset);
                            }
                            currentData.company = currentData.company.replace(';', ' ');
                        } else {
                            finished2 = finished;
                            if (args[0].startsWith("TEL")) {
                                if (args[1].length() > 0) {
                                    currentData.phones.add(args[1]);
                                }
                            } else if (args[0].startsWith("EMAIL")) {
                                String email = args[1];
                                if (email.length() > 0) {
                                    currentData.emails.add(email);
                                }
                            }
                        }
                        String str2 = data;
                        finished = finished2;
                    }
                }
                bufferedReader.close();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable th) {
                return null;
            }
            if (!finished) {
                return null;
            }
            StringBuilder result = new StringBuilder();
            for (int a = 0; a < currentData.phones.size(); a++) {
                if (result.length() > 0) {
                    result.append(10);
                }
                String phone = currentData.phones.get(a);
                if (!phone.contains("#")) {
                    if (!phone.contains("*")) {
                        result.append(PhoneFormat.getInstance().format(phone));
                    }
                }
                result.append(phone);
            }
            for (int a2 = 0; a2 < currentData.emails.size(); a2++) {
                if (result.length() > 0) {
                    result.append(10);
                }
                result.append(PhoneFormat.getInstance().format(currentData.emails.get(a2)));
            }
            if (!TextUtils.isEmpty(currentData.company)) {
                if (result.length() > 0) {
                    result.append(10);
                }
                result.append(currentData.company);
            }
            return result;
        }
    }

    public static class TextLayoutBlock {
        public int charactersEnd;
        public int charactersOffset;
        public byte directionFlags;
        public int height;
        public int heightByOffset;
        public StaticLayout textLayout;
        public float textYOffset;

        public boolean isRtl() {
            byte b = this.directionFlags;
            return (b & 1) != 0 && (b & 2) == 0;
        }
    }

    public static class GroupedMessagePosition {
        public float aspectRatio;
        public boolean edge;
        public int flags;
        public boolean last;
        public int leftSpanOffset;
        public byte maxX;
        public byte maxY;
        public byte minX;
        public byte minY;
        public float ph;
        public int pw;
        public float[] siblingHeights;
        public int spanSize;

        public void set(int minX2, int maxX2, int minY2, int maxY2, int w, float h, int flags2) {
            this.minX = (byte) minX2;
            this.maxX = (byte) maxX2;
            this.minY = (byte) minY2;
            this.maxY = (byte) maxY2;
            this.pw = w;
            this.spanSize = w;
            this.ph = h;
            this.flags = (byte) flags2;
        }
    }

    public static class GroupedMessages {
        private int firstSpanAdditionalSize = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        public long groupId;
        public boolean hasSibling;
        private int maxSizeWidth = CodeUtils.DEFAULT_REQ_HEIGHT;
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public ArrayList<GroupedMessagePosition> posArray = new ArrayList<>();
        public HashMap<MessageObject, GroupedMessagePosition> positions = new HashMap<>();

        public int getMaxSizeWidth() {
            return this.maxSizeWidth;
        }

        private class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(int i1, int i2, float f1, float f2) {
                this.lineCounts = new int[]{i1, i2};
                this.heights = new float[]{f1, f2};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, float f1, float f2, float f3) {
                this.lineCounts = new int[]{i1, i2, i3};
                this.heights = new float[]{f1, f2, f3};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, int i4, float f1, float f2, float f3, float f4) {
                this.lineCounts = new int[]{i1, i2, i3, i4};
                this.heights = new float[]{f1, f2, f3, f4};
            }
        }

        private float multiHeight(float[] array, int start, int end) {
            float sum = 0.0f;
            for (int a = start; a < end; a++) {
                sum += array[a];
            }
            return ((float) this.maxSizeWidth) / sum;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v52, resolved type: boolean} */
        /* JADX WARNING: Code restructure failed: missing block: B:189:0x0830, code lost:
            if (r4.lineCounts[2] > r4.lineCounts[3]) goto L_0x0834;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:229:0x08f9  */
        /* JADX WARNING: Removed duplicated region for block: B:335:0x09ea A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void calculate() {
            /*
                r46 = this;
                r10 = r46
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                r0.clear()
                java.util.HashMap<im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.positions
                r0.clear()
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r0 = r10.messages
                int r11 = r0.size()
                r12 = 1
                if (r11 > r12) goto L_0x0016
                return
            L_0x0016:
                r13 = 1145798656(0x444b8000, float:814.0)
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r14 = r0
                r0 = 1065353216(0x3f800000, float:1.0)
                r1 = 0
                r15 = 0
                r2 = 0
                r3 = 0
                r9 = 0
                r10.hasSibling = r9
                r4 = 0
                r16 = r1
                r17 = r2
                r18 = r3
            L_0x002f:
                r1 = 1073741824(0x40000000, float:2.0)
                r19 = 1067030938(0x3f99999a, float:1.2)
                if (r4 >= r11) goto L_0x00e2
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r3 = r10.messages
                java.lang.Object r3 = r3.get(r4)
                im.bclpbkiauv.messenger.MessageObject r3 = (im.bclpbkiauv.messenger.MessageObject) r3
                if (r4 != 0) goto L_0x0081
                boolean r5 = r3.isOutOwner()
                if (r5 != 0) goto L_0x007c
                im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner
                im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
                if (r6 == 0) goto L_0x0054
                im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner
                im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
                im.bclpbkiauv.tgnet.TLRPC$Peer r6 = r6.saved_from_peer
                if (r6 != 0) goto L_0x007a
            L_0x0054:
                im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner
                int r6 = r6.from_id
                if (r6 <= 0) goto L_0x007c
                im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner
                im.bclpbkiauv.tgnet.TLRPC$Peer r6 = r6.to_id
                int r6 = r6.channel_id
                if (r6 != 0) goto L_0x007a
                im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner
                im.bclpbkiauv.tgnet.TLRPC$Peer r6 = r6.to_id
                int r6 = r6.chat_id
                if (r6 != 0) goto L_0x007a
                im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner
                im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r6.media
                boolean r6 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
                if (r6 != 0) goto L_0x007a
                im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner
                im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r6.media
                boolean r6 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaInvoice
                if (r6 == 0) goto L_0x007c
            L_0x007a:
                r6 = 1
                goto L_0x007d
            L_0x007c:
                r6 = 0
            L_0x007d:
                r16 = r5
                r18 = r6
            L_0x0081:
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r5 = r3.photoThumbs
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.getPhotoSize()
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r5 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r6 = new im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition
                r6.<init>()
                int r7 = r11 + -1
                if (r4 != r7) goto L_0x0096
                r7 = 1
                goto L_0x0097
            L_0x0096:
                r7 = 0
            L_0x0097:
                r6.last = r7
                if (r5 != 0) goto L_0x009e
                r2 = 1065353216(0x3f800000, float:1.0)
                goto L_0x00a5
            L_0x009e:
                int r2 = r5.w
                float r2 = (float) r2
                int r7 = r5.h
                float r7 = (float) r7
                float r2 = r2 / r7
            L_0x00a5:
                r6.aspectRatio = r2
                float r2 = r6.aspectRatio
                int r2 = (r2 > r19 ? 1 : (r2 == r19 ? 0 : -1))
                if (r2 <= 0) goto L_0x00b4
                java.lang.String r2 = "w"
                r14.append(r2)
                goto L_0x00c8
            L_0x00b4:
                float r2 = r6.aspectRatio
                r7 = 1061997773(0x3f4ccccd, float:0.8)
                int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                if (r2 >= 0) goto L_0x00c3
                java.lang.String r2 = "n"
                r14.append(r2)
                goto L_0x00c8
            L_0x00c3:
                java.lang.String r2 = "q"
                r14.append(r2)
            L_0x00c8:
                float r2 = r6.aspectRatio
                float r0 = r0 + r2
                float r2 = r6.aspectRatio
                int r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
                if (r1 <= 0) goto L_0x00d4
                r1 = 1
                r17 = r1
            L_0x00d4:
                java.util.HashMap<im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r1 = r10.positions
                r1.put(r3, r6)
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                r1.add(r6)
                int r4 = r4 + 1
                goto L_0x002f
            L_0x00e2:
                if (r18 == 0) goto L_0x00f0
                int r3 = r10.maxSizeWidth
                int r3 = r3 + -50
                r10.maxSizeWidth = r3
                int r3 = r10.firstSpanAdditionalSize
                int r3 = r3 + 50
                r10.firstSpanAdditionalSize = r3
            L_0x00f0:
                r3 = 1123024896(0x42f00000, float:120.0)
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                android.graphics.Point r4 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r4 = r4.x
                android.graphics.Point r5 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r5 = r5.y
                int r4 = java.lang.Math.min(r4, r5)
                float r4 = (float) r4
                int r5 = r10.maxSizeWidth
                float r5 = (float) r5
                float r4 = r4 / r5
                float r3 = r3 / r4
                int r7 = (int) r3
                r3 = 1109393408(0x42200000, float:40.0)
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                android.graphics.Point r4 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r4 = r4.x
                android.graphics.Point r5 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r5 = r5.y
                int r4 = java.lang.Math.min(r4, r5)
                float r4 = (float) r4
                int r5 = r10.maxSizeWidth
                float r6 = (float) r5
                float r4 = r4 / r6
                float r3 = r3 / r4
                int r6 = (int) r3
                float r3 = (float) r5
                float r5 = r3 / r13
                float r3 = (float) r11
                float r4 = r0 / r3
                r0 = 1120403456(0x42c80000, float:100.0)
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                float r3 = r0 / r13
                r0 = 4
                r2 = 3
                r1 = 2
                if (r17 != 0) goto L_0x05d5
                if (r11 == r1) goto L_0x0151
                if (r11 == r2) goto L_0x0151
                if (r11 != r0) goto L_0x0143
                goto L_0x0151
            L_0x0143:
                r34 = r3
                r33 = r4
                r21 = r5
                r36 = r14
                r35 = r15
                r22 = 2
                goto L_0x05e1
            L_0x0151:
                r19 = 1053609165(0x3ecccccd, float:0.4)
                if (r11 != r1) goto L_0x02a5
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                java.lang.Object r0 = r0.get(r9)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r0 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r0
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                java.lang.Object r2 = r2.get(r12)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r2
                java.lang.String r9 = r14.toString()
                java.lang.String r12 = "ww"
                boolean r22 = r9.equals(r12)
                if (r22 == 0) goto L_0x01e0
                r32 = r2
                double r1 = (double) r4
                r22 = 4608983858650965606(0x3ff6666666666666, double:1.4)
                r34 = r3
                r33 = r4
                double r3 = (double) r5
                double r3 = r3 * r22
                int r22 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r22 <= 0) goto L_0x01dd
                float r1 = r0.aspectRatio
                r2 = r32
                float r3 = r2.aspectRatio
                float r1 = r1 - r3
                double r3 = (double) r1
                r22 = 4596373779694328218(0x3fc999999999999a, double:0.2)
                int r1 = (r3 > r22 ? 1 : (r3 == r22 ? 0 : -1))
                if (r1 >= 0) goto L_0x01e4
                int r1 = r10.maxSizeWidth
                float r1 = (float) r1
                float r3 = r0.aspectRatio
                float r1 = r1 / r3
                int r3 = r10.maxSizeWidth
                float r3 = (float) r3
                float r4 = r2.aspectRatio
                float r3 = r3 / r4
                r4 = 1073741824(0x40000000, float:2.0)
                float r4 = r13 / r4
                float r3 = java.lang.Math.min(r3, r4)
                float r1 = java.lang.Math.min(r1, r3)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r1 = r1 / r13
                r23 = 0
                r24 = 0
                r25 = 0
                r26 = 0
                int r3 = r10.maxSizeWidth
                r29 = 7
                r22 = r0
                r27 = r3
                r28 = r1
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r25 = 1
                r26 = 1
                int r3 = r10.maxSizeWidth
                r29 = 11
                r22 = r2
                r27 = r3
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r21 = r5
                goto L_0x0293
            L_0x01dd:
                r2 = r32
                goto L_0x01e4
            L_0x01e0:
                r34 = r3
                r33 = r4
            L_0x01e4:
                boolean r1 = r9.equals(r12)
                if (r1 != 0) goto L_0x0257
                java.lang.String r1 = "qq"
                boolean r1 = r9.equals(r1)
                if (r1 == 0) goto L_0x01f5
                r21 = r5
                goto L_0x0259
            L_0x01f5:
                int r1 = r10.maxSizeWidth
                float r3 = (float) r1
                float r3 = r3 * r19
                float r1 = (float) r1
                float r4 = r0.aspectRatio
                float r1 = r1 / r4
                float r4 = r0.aspectRatio
                r12 = 1065353216(0x3f800000, float:1.0)
                float r4 = r12 / r4
                r21 = r5
                float r5 = r2.aspectRatio
                float r5 = r12 / r5
                float r4 = r4 + r5
                float r1 = r1 / r4
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r1 = java.lang.Math.max(r3, r1)
                int r1 = (int) r1
                int r3 = r10.maxSizeWidth
                int r3 = r3 - r1
                if (r3 >= r7) goto L_0x021f
                int r4 = r7 - r3
                r3 = r7
                int r1 = r1 - r4
            L_0x021f:
                float r4 = (float) r3
                float r5 = r0.aspectRatio
                float r4 = r4 / r5
                float r5 = (float) r1
                float r12 = r2.aspectRatio
                float r5 = r5 / r12
                float r4 = java.lang.Math.min(r4, r5)
                int r4 = java.lang.Math.round(r4)
                float r4 = (float) r4
                float r4 = java.lang.Math.min(r13, r4)
                float r4 = r4 / r13
                r23 = 0
                r24 = 0
                r25 = 0
                r26 = 0
                r29 = 13
                r22 = r0
                r27 = r3
                r28 = r4
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r23 = 1
                r24 = 1
                r29 = 14
                r22 = r2
                r27 = r1
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r15 = 1
                goto L_0x0293
            L_0x0257:
                r21 = r5
            L_0x0259:
                int r1 = r10.maxSizeWidth
                r3 = 2
                int r1 = r1 / r3
                float r3 = (float) r1
                float r4 = r0.aspectRatio
                float r3 = r3 / r4
                float r4 = (float) r1
                float r5 = r2.aspectRatio
                float r4 = r4 / r5
                float r4 = java.lang.Math.min(r4, r13)
                float r3 = java.lang.Math.min(r3, r4)
                int r3 = java.lang.Math.round(r3)
                float r3 = (float) r3
                float r3 = r3 / r13
                r23 = 0
                r24 = 0
                r25 = 0
                r26 = 0
                r29 = 13
                r22 = r0
                r27 = r1
                r28 = r3
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r23 = 1
                r24 = 1
                r29 = 14
                r22 = r2
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r15 = 1
            L_0x0293:
                r20 = r6
                r26 = r8
                r22 = r11
                r32 = r13
                r36 = r14
                r6 = r15
                r24 = r33
                r0 = r34
                r15 = r7
                goto L_0x08f2
            L_0x02a5:
                r34 = r3
                r33 = r4
                r21 = r5
                r1 = 1059648963(0x3f28f5c3, float:0.66)
                if (r11 != r2) goto L_0x03fc
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                java.lang.Object r0 = r0.get(r9)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r0 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r0
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                r3 = 1
                java.lang.Object r2 = r2.get(r3)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r2
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r3 = r10.posArray
                r4 = 2
                java.lang.Object r3 = r3.get(r4)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r3 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r3
                char r4 = r14.charAt(r9)
                r5 = 110(0x6e, float:1.54E-43)
                if (r4 != r5) goto L_0x037f
                r1 = 1056964608(0x3f000000, float:0.5)
                float r4 = r13 * r1
                float r5 = r2.aspectRatio
                int r12 = r10.maxSizeWidth
                float r12 = (float) r12
                float r5 = r5 * r12
                float r12 = r3.aspectRatio
                float r9 = r2.aspectRatio
                float r12 = r12 + r9
                float r5 = r5 / r12
                int r5 = java.lang.Math.round(r5)
                float r5 = (float) r5
                float r4 = java.lang.Math.min(r4, r5)
                float r5 = r13 - r4
                float r9 = (float) r7
                int r12 = r10.maxSizeWidth
                float r12 = (float) r12
                float r12 = r12 * r1
                float r1 = r3.aspectRatio
                float r1 = r1 * r4
                r35 = r15
                float r15 = r2.aspectRatio
                float r15 = r15 * r5
                float r1 = java.lang.Math.min(r1, r15)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r1 = java.lang.Math.min(r12, r1)
                float r1 = java.lang.Math.max(r9, r1)
                int r1 = (int) r1
                float r9 = r0.aspectRatio
                float r9 = r9 * r13
                float r12 = (float) r6
                float r9 = r9 + r12
                int r12 = r10.maxSizeWidth
                int r12 = r12 - r1
                float r12 = (float) r12
                float r9 = java.lang.Math.min(r9, r12)
                int r9 = java.lang.Math.round(r9)
                r23 = 0
                r24 = 0
                r25 = 0
                r26 = 1
                r28 = 1065353216(0x3f800000, float:1.0)
                r29 = 13
                r22 = r0
                r27 = r9
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r23 = 1
                r24 = 1
                r26 = 0
                float r28 = r5 / r13
                r29 = 6
                r22 = r2
                r27 = r1
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r23 = 0
                r25 = 1
                r26 = 1
                float r28 = r4 / r13
                r29 = 10
                r22 = r3
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                int r12 = r10.maxSizeWidth
                r3.spanSize = r12
                r12 = 2
                float[] r15 = new float[r12]
                float r12 = r4 / r13
                r19 = 0
                r15[r19] = r12
                float r12 = r5 / r13
                r19 = 1
                r15[r19] = r12
                r0.siblingHeights = r15
                if (r16 == 0) goto L_0x0372
                int r12 = r10.maxSizeWidth
                int r12 = r12 - r1
                r0.spanSize = r12
                goto L_0x0379
            L_0x0372:
                int r12 = r10.maxSizeWidth
                int r12 = r12 - r9
                r2.spanSize = r12
                r3.leftSpanOffset = r9
            L_0x0379:
                r12 = 1
                r10.hasSibling = r12
                r1 = 1
                r15 = r1
                goto L_0x03ea
            L_0x037f:
                r35 = r15
                int r4 = r10.maxSizeWidth
                float r4 = (float) r4
                float r5 = r0.aspectRatio
                float r4 = r4 / r5
                float r1 = r1 * r13
                float r1 = java.lang.Math.min(r4, r1)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r1 = r1 / r13
                r23 = 0
                r24 = 1
                r25 = 0
                r26 = 0
                int r4 = r10.maxSizeWidth
                r29 = 7
                r22 = r0
                r27 = r4
                r28 = r1
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                int r4 = r10.maxSizeWidth
                r5 = 2
                int r4 = r4 / r5
                float r5 = r13 - r1
                float r9 = (float) r4
                float r12 = r2.aspectRatio
                float r9 = r9 / r12
                float r12 = (float) r4
                float r15 = r3.aspectRatio
                float r12 = r12 / r15
                float r9 = java.lang.Math.min(r9, r12)
                int r9 = java.lang.Math.round(r9)
                float r9 = (float) r9
                float r5 = java.lang.Math.min(r5, r9)
                float r5 = r5 / r13
                int r9 = (r5 > r34 ? 1 : (r5 == r34 ? 0 : -1))
                if (r9 >= 0) goto L_0x03ca
                r5 = r34
            L_0x03ca:
                r23 = 0
                r24 = 0
                r25 = 1
                r26 = 1
                r29 = 9
                r22 = r2
                r27 = r4
                r28 = r5
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r23 = 1
                r24 = 1
                r29 = 10
                r22 = r3
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r9 = 1
                r15 = r9
            L_0x03ea:
                r20 = r6
                r26 = r8
                r22 = r11
                r32 = r13
                r36 = r14
                r6 = r15
                r24 = r33
                r0 = r34
                r15 = r7
                goto L_0x08f2
            L_0x03fc:
                r35 = r15
                if (r11 != r0) goto L_0x05c0
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                r3 = 0
                java.lang.Object r0 = r0.get(r3)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r0 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r0
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r3 = r10.posArray
                r4 = 1
                java.lang.Object r3 = r3.get(r4)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r3 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r3
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r4 = r10.posArray
                r5 = 2
                java.lang.Object r4 = r4.get(r5)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r4 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r4
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r5 = r10.posArray
                java.lang.Object r5 = r5.get(r2)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r5 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r5
                r9 = 0
                char r12 = r14.charAt(r9)
                r9 = 119(0x77, float:1.67E-43)
                if (r12 != r9) goto L_0x04ee
                int r2 = r10.maxSizeWidth
                float r2 = (float) r2
                float r9 = r0.aspectRatio
                float r2 = r2 / r9
                float r1 = r1 * r13
                float r1 = java.lang.Math.min(r2, r1)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r1 = r1 / r13
                r23 = 0
                r24 = 2
                r25 = 0
                r26 = 0
                int r2 = r10.maxSizeWidth
                r29 = 7
                r22 = r0
                r27 = r2
                r28 = r1
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                int r2 = r10.maxSizeWidth
                float r2 = (float) r2
                float r9 = r3.aspectRatio
                float r12 = r4.aspectRatio
                float r9 = r9 + r12
                float r12 = r5.aspectRatio
                float r9 = r9 + r12
                float r2 = r2 / r9
                int r2 = java.lang.Math.round(r2)
                float r2 = (float) r2
                float r9 = (float) r7
                int r12 = r10.maxSizeWidth
                float r12 = (float) r12
                float r12 = r12 * r19
                float r15 = r3.aspectRatio
                float r15 = r15 * r2
                float r12 = java.lang.Math.min(r12, r15)
                float r9 = java.lang.Math.max(r9, r12)
                int r9 = (int) r9
                float r12 = (float) r7
                int r15 = r10.maxSizeWidth
                float r15 = (float) r15
                r19 = 1051260355(0x3ea8f5c3, float:0.33)
                float r15 = r15 * r19
                float r12 = java.lang.Math.max(r12, r15)
                float r15 = r5.aspectRatio
                float r15 = r15 * r2
                float r12 = java.lang.Math.max(r12, r15)
                int r12 = (int) r12
                int r15 = r10.maxSizeWidth
                int r15 = r15 - r9
                int r15 = r15 - r12
                r19 = 1114112000(0x42680000, float:58.0)
                r36 = r14
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
                if (r15 >= r14) goto L_0x04ae
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
                int r14 = r14 - r15
                int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
                int r19 = r14 / 2
                int r9 = r9 - r19
                int r19 = r14 / 2
                int r19 = r14 - r19
                int r12 = r12 - r19
            L_0x04ae:
                float r14 = r13 - r1
                float r2 = java.lang.Math.min(r14, r2)
                float r2 = r2 / r13
                int r14 = (r2 > r34 ? 1 : (r2 == r34 ? 0 : -1))
                if (r14 >= 0) goto L_0x04bb
                r2 = r34
            L_0x04bb:
                r23 = 0
                r24 = 0
                r25 = 1
                r26 = 1
                r29 = 9
                r22 = r3
                r27 = r9
                r28 = r2
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r23 = 1
                r24 = 1
                r29 = 8
                r22 = r4
                r27 = r15
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r23 = 2
                r24 = 2
                r29 = 10
                r22 = r5
                r27 = r12
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r1 = 2
                r15 = r1
                r22 = 2
                goto L_0x05b0
            L_0x04ee:
                r36 = r14
                float r1 = r3.aspectRatio
                r9 = 1065353216(0x3f800000, float:1.0)
                float r1 = r9 / r1
                float r12 = r4.aspectRatio
                float r12 = r9 / r12
                float r1 = r1 + r12
                float r12 = r5.aspectRatio
                float r12 = r9 / r12
                float r1 = r1 + r12
                float r1 = r13 / r1
                int r1 = java.lang.Math.round(r1)
                int r1 = java.lang.Math.max(r7, r1)
                float r9 = (float) r8
                float r12 = (float) r1
                float r14 = r3.aspectRatio
                float r12 = r12 / r14
                float r9 = java.lang.Math.max(r9, r12)
                float r9 = r9 / r13
                r12 = 1051260355(0x3ea8f5c3, float:0.33)
                float r9 = java.lang.Math.min(r12, r9)
                float r14 = (float) r8
                float r15 = (float) r1
                float r2 = r4.aspectRatio
                float r15 = r15 / r2
                float r2 = java.lang.Math.max(r14, r15)
                float r2 = r2 / r13
                float r2 = java.lang.Math.min(r12, r2)
                r12 = 1065353216(0x3f800000, float:1.0)
                float r12 = r12 - r9
                float r12 = r12 - r2
                float r14 = r0.aspectRatio
                float r14 = r14 * r13
                float r15 = (float) r6
                float r14 = r14 + r15
                int r15 = r10.maxSizeWidth
                int r15 = r15 - r1
                float r15 = (float) r15
                float r14 = java.lang.Math.min(r14, r15)
                int r14 = java.lang.Math.round(r14)
                r23 = 0
                r24 = 0
                r25 = 0
                r26 = 2
                float r15 = r9 + r2
                float r28 = r15 + r12
                r29 = 13
                r22 = r0
                r27 = r14
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r23 = 1
                r24 = 1
                r26 = 0
                r29 = 6
                r22 = r3
                r27 = r1
                r28 = r9
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r23 = 0
                r25 = 1
                r26 = 1
                r29 = 2
                r22 = r4
                r28 = r2
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                int r15 = r10.maxSizeWidth
                r4.spanSize = r15
                r25 = 2
                r26 = 2
                r29 = 10
                r22 = r5
                r28 = r12
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                int r15 = r10.maxSizeWidth
                r5.spanSize = r15
                if (r16 == 0) goto L_0x0591
                int r15 = r10.maxSizeWidth
                int r15 = r15 - r1
                r0.spanSize = r15
                goto L_0x059a
            L_0x0591:
                int r15 = r10.maxSizeWidth
                int r15 = r15 - r14
                r3.spanSize = r15
                r4.leftSpanOffset = r14
                r5.leftSpanOffset = r14
            L_0x059a:
                r15 = 3
                float[] r15 = new float[r15]
                r19 = 0
                r15[r19] = r9
                r19 = r1
                r1 = 1
                r15[r1] = r2
                r22 = 2
                r15[r22] = r12
                r0.siblingHeights = r15
                r10.hasSibling = r1
                r1 = 1
                r15 = r1
            L_0x05b0:
                r20 = r6
                r26 = r8
                r22 = r11
                r32 = r13
                r6 = r15
                r24 = r33
                r0 = r34
                r15 = r7
                goto L_0x08f2
            L_0x05c0:
                r36 = r14
                r22 = 2
                r20 = r6
                r15 = r7
                r26 = r8
                r22 = r11
                r32 = r13
                r24 = r33
                r0 = r34
                r6 = r35
                goto L_0x08f2
            L_0x05d5:
                r34 = r3
                r33 = r4
                r21 = r5
                r36 = r14
                r35 = r15
                r22 = 2
            L_0x05e1:
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                int r1 = r1.size()
                float[] r12 = new float[r1]
                r1 = 0
            L_0x05ea:
                if (r1 >= r11) goto L_0x062d
                r2 = 1066192077(0x3f8ccccd, float:1.1)
                int r2 = (r33 > r2 ? 1 : (r33 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x0606
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                java.lang.Object r2 = r2.get(r1)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r2
                float r2 = r2.aspectRatio
                r3 = 1065353216(0x3f800000, float:1.0)
                float r2 = java.lang.Math.max(r3, r2)
                r12[r1] = r2
                goto L_0x0618
            L_0x0606:
                r3 = 1065353216(0x3f800000, float:1.0)
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                java.lang.Object r2 = r2.get(r1)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r2
                float r2 = r2.aspectRatio
                float r2 = java.lang.Math.min(r3, r2)
                r12[r1] = r2
            L_0x0618:
                r2 = 1059760867(0x3f2aaae3, float:0.66667)
                r4 = 1071225242(0x3fd9999a, float:1.7)
                r5 = r12[r1]
                float r4 = java.lang.Math.min(r4, r5)
                float r2 = java.lang.Math.max(r2, r4)
                r12[r1] = r2
                int r1 = r1 + 1
                goto L_0x05ea
            L_0x062d:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                r14 = r1
                r1 = 1
                r9 = r1
            L_0x0635:
                int r1 = r12.length
                if (r9 >= r1) goto L_0x0682
                int r1 = r12.length
                int r15 = r1 - r9
                r2 = 3
                if (r9 > r2) goto L_0x066e
                if (r15 <= r2) goto L_0x0649
                r22 = r11
                r24 = r33
                r37 = r34
                r23 = 4
                goto L_0x0676
            L_0x0649:
                im.bclpbkiauv.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r5 = new im.bclpbkiauv.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r1 = 0
                float r4 = r10.multiHeight(r12, r1, r9)
                int r1 = r12.length
                float r20 = r10.multiHeight(r12, r9, r1)
                r23 = 4
                r0 = r5
                r3 = 2
                r1 = r46
                r22 = r11
                r11 = 3
                r2 = r9
                r37 = r34
                r3 = r15
                r24 = r33
                r11 = r5
                r5 = r20
                r0.<init>(r2, r3, r4, r5)
                r14.add(r11)
                goto L_0x0676
            L_0x066e:
                r22 = r11
                r24 = r33
                r37 = r34
                r23 = 4
            L_0x0676:
                int r9 = r9 + 1
                r11 = r22
                r33 = r24
                r34 = r37
                r0 = 4
                r22 = 2
                goto L_0x0635
            L_0x0682:
                r22 = r11
                r24 = r33
                r37 = r34
                r23 = 4
                r0 = 1
                r9 = r0
            L_0x068c:
                int r0 = r12.length
                r1 = 1
                int r0 = r0 - r1
                if (r9 >= r0) goto L_0x06fb
                r0 = 1
                r11 = r0
            L_0x0693:
                int r0 = r12.length
                int r0 = r0 - r9
                if (r11 >= r0) goto L_0x06f3
                int r0 = r12.length
                int r0 = r0 - r9
                int r15 = r0 - r11
                r0 = 3
                if (r9 > r0) goto L_0x06e4
                r0 = 1062836634(0x3f59999a, float:0.85)
                int r0 = (r24 > r0 ? 1 : (r24 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x06a7
                r0 = 4
                goto L_0x06a8
            L_0x06a7:
                r0 = 3
            L_0x06a8:
                if (r11 > r0) goto L_0x06e4
                r0 = 3
                if (r15 <= r0) goto L_0x06b5
                r20 = r6
                r27 = r8
                r25 = r15
                r15 = r7
                goto L_0x06eb
            L_0x06b5:
                im.bclpbkiauv.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r5 = new im.bclpbkiauv.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r0 = 0
                float r20 = r10.multiHeight(r12, r0, r9)
                int r0 = r9 + r11
                float r25 = r10.multiHeight(r12, r9, r0)
                int r0 = r9 + r11
                int r1 = r12.length
                float r26 = r10.multiHeight(r12, r0, r1)
                r0 = r5
                r1 = r46
                r2 = r9
                r3 = r11
                r4 = r15
                r27 = r8
                r8 = r5
                r5 = r20
                r20 = r6
                r6 = r25
                r25 = r15
                r15 = r7
                r7 = r26
                r0.<init>(r2, r3, r4, r5, r6, r7)
                r14.add(r8)
                goto L_0x06eb
            L_0x06e4:
                r20 = r6
                r27 = r8
                r25 = r15
                r15 = r7
            L_0x06eb:
                int r11 = r11 + 1
                r7 = r15
                r6 = r20
                r8 = r27
                goto L_0x0693
            L_0x06f3:
                r20 = r6
                r15 = r7
                r27 = r8
                int r9 = r9 + 1
                goto L_0x068c
            L_0x06fb:
                r20 = r6
                r15 = r7
                r27 = r8
                r0 = 1
                r11 = r0
            L_0x0702:
                int r0 = r12.length
                r9 = 2
                int r0 = r0 - r9
                if (r11 >= r0) goto L_0x07bd
                r0 = 1
                r8 = r0
            L_0x0709:
                int r0 = r12.length
                int r0 = r0 - r11
                if (r8 >= r0) goto L_0x07aa
                r0 = 1
                r7 = r0
            L_0x070f:
                int r0 = r12.length
                int r0 = r0 - r11
                int r0 = r0 - r8
                if (r7 >= r0) goto L_0x0794
                int r0 = r12.length
                int r0 = r0 - r11
                int r0 = r0 - r8
                int r6 = r0 - r7
                r0 = 3
                if (r11 > r0) goto L_0x0778
                if (r8 > r0) goto L_0x0778
                if (r7 > r0) goto L_0x0778
                if (r6 <= r0) goto L_0x0732
                r33 = r6
                r25 = r7
                r31 = r12
                r32 = r13
                r26 = r27
                r12 = 2
                r28 = 0
                r27 = r8
                goto L_0x0787
            L_0x0732:
                im.bclpbkiauv.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r5 = new im.bclpbkiauv.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r4 = 0
                float r25 = r10.multiHeight(r12, r4, r11)
                int r0 = r11 + r8
                float r26 = r10.multiHeight(r12, r11, r0)
                int r0 = r11 + r8
                int r1 = r11 + r8
                int r1 = r1 + r7
                float r28 = r10.multiHeight(r12, r0, r1)
                int r0 = r11 + r8
                int r0 = r0 + r7
                int r1 = r12.length
                float r29 = r10.multiHeight(r12, r0, r1)
                r0 = r5
                r1 = r46
                r2 = r11
                r3 = r8
                r31 = 0
                r4 = r7
                r32 = r13
                r13 = r5
                r5 = r6
                r33 = r6
                r6 = r25
                r25 = r7
                r7 = r26
                r26 = r27
                r27 = r8
                r8 = r28
                r31 = r12
                r12 = 2
                r28 = 0
                r9 = r29
                r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
                r14.add(r13)
                goto L_0x0787
            L_0x0778:
                r33 = r6
                r25 = r7
                r31 = r12
                r32 = r13
                r26 = r27
                r12 = 2
                r28 = 0
                r27 = r8
            L_0x0787:
                int r7 = r25 + 1
                r8 = r27
                r12 = r31
                r13 = r32
                r9 = 2
                r27 = r26
                goto L_0x070f
            L_0x0794:
                r25 = r7
                r31 = r12
                r32 = r13
                r26 = r27
                r12 = 2
                r28 = 0
                r27 = r8
                int r8 = r27 + 1
                r27 = r26
                r12 = r31
                r9 = 2
                goto L_0x0709
            L_0x07aa:
                r31 = r12
                r32 = r13
                r26 = r27
                r12 = 2
                r28 = 0
                r27 = r8
                int r11 = r11 + 1
                r27 = r26
                r12 = r31
                goto L_0x0702
            L_0x07bd:
                r31 = r12
                r32 = r13
                r26 = r27
                r12 = 2
                r28 = 0
                r0 = 0
                r1 = 0
                int r2 = r10.maxSizeWidth
                r3 = 3
                int r2 = r2 / r3
                int r2 = r2 * 4
                float r2 = (float) r2
                r3 = 0
            L_0x07d0:
                int r4 = r14.size()
                if (r3 >= r4) goto L_0x084c
                java.lang.Object r4 = r14.get(r3)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r4 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessages.MessageGroupedLayoutAttempt) r4
                r5 = 0
                r6 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r7 = 0
            L_0x07e1:
                float[] r8 = r4.heights
                int r8 = r8.length
                if (r7 >= r8) goto L_0x07fa
                float[] r8 = r4.heights
                r8 = r8[r7]
                float r5 = r5 + r8
                float[] r8 = r4.heights
                r8 = r8[r7]
                int r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r8 >= 0) goto L_0x07f7
                float[] r8 = r4.heights
                r6 = r8[r7]
            L_0x07f7:
                int r7 = r7 + 1
                goto L_0x07e1
            L_0x07fa:
                float r7 = r5 - r2
                float r7 = java.lang.Math.abs(r7)
                int[] r8 = r4.lineCounts
                int r8 = r8.length
                r9 = 1
                if (r8 <= r9) goto L_0x0837
                int[] r8 = r4.lineCounts
                r8 = r8[r28]
                int[] r13 = r4.lineCounts
                r13 = r13[r9]
                if (r8 > r13) goto L_0x0833
                int[] r8 = r4.lineCounts
                int r8 = r8.length
                if (r8 <= r12) goto L_0x0822
                int[] r8 = r4.lineCounts
                r8 = r8[r9]
                int[] r9 = r4.lineCounts
                r9 = r9[r12]
                if (r8 > r9) goto L_0x0820
                goto L_0x0822
            L_0x0820:
                r9 = 3
                goto L_0x0834
            L_0x0822:
                int[] r8 = r4.lineCounts
                int r8 = r8.length
                r9 = 3
                if (r8 <= r9) goto L_0x0838
                int[] r8 = r4.lineCounts
                r8 = r8[r12]
                int[] r13 = r4.lineCounts
                r13 = r13[r9]
                if (r8 <= r13) goto L_0x0838
                goto L_0x0834
            L_0x0833:
                r9 = 3
            L_0x0834:
                float r7 = r7 * r19
                goto L_0x0838
            L_0x0837:
                r9 = 3
            L_0x0838:
                float r8 = (float) r15
                int r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r8 >= 0) goto L_0x0841
                r8 = 1069547520(0x3fc00000, float:1.5)
                float r7 = r7 * r8
            L_0x0841:
                if (r0 == 0) goto L_0x0847
                int r8 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
                if (r8 >= 0) goto L_0x0849
            L_0x0847:
                r0 = r4
                r1 = r7
            L_0x0849:
                int r3 = r3 + 1
                goto L_0x07d0
            L_0x084c:
                if (r0 != 0) goto L_0x084f
                return
            L_0x084f:
                r3 = 0
                r4 = 0
                r5 = 0
                r6 = r35
            L_0x0854:
                int[] r7 = r0.lineCounts
                int r7 = r7.length
                if (r5 >= r7) goto L_0x08ea
                int[] r7 = r0.lineCounts
                r7 = r7[r5]
                float[] r8 = r0.heights
                r8 = r8[r5]
                int r9 = r10.maxSizeWidth
                r13 = 0
                int r12 = r7 + -1
                int r6 = java.lang.Math.max(r6, r12)
                r12 = 0
            L_0x086b:
                if (r12 >= r7) goto L_0x08cc
                r23 = r31[r3]
                r25 = r1
                float r1 = r23 * r8
                int r1 = (int) r1
                int r9 = r9 - r1
                r27 = r2
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                java.lang.Object r2 = r2.get(r3)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r2
                r28 = 0
                if (r5 != 0) goto L_0x0885
                r28 = r28 | 4
            L_0x0885:
                r29 = r6
                int[] r6 = r0.lineCounts
                int r6 = r6.length
                r30 = 1
                int r6 = r6 + -1
                if (r5 != r6) goto L_0x0892
                r28 = r28 | 8
            L_0x0892:
                if (r12 != 0) goto L_0x0899
                r28 = r28 | 1
                if (r16 == 0) goto L_0x0899
                r13 = r2
            L_0x0899:
                int r6 = r7 + -1
                if (r12 != r6) goto L_0x08a3
                r28 = r28 | 2
                if (r16 != 0) goto L_0x08a3
                r6 = r2
                r13 = r6
            L_0x08a3:
                float r6 = r8 / r32
                r33 = r0
                r0 = r37
                float r44 = java.lang.Math.max(r0, r6)
                r38 = r2
                r39 = r12
                r40 = r12
                r41 = r5
                r42 = r5
                r43 = r1
                r45 = r28
                r38.set(r39, r40, r41, r42, r43, r44, r45)
                int r3 = r3 + 1
                int r12 = r12 + 1
                r1 = r25
                r2 = r27
                r6 = r29
                r0 = r33
                goto L_0x086b
            L_0x08cc:
                r33 = r0
                r25 = r1
                r27 = r2
                r29 = r6
                r0 = r37
                int r1 = r13.pw
                int r1 = r1 + r9
                r13.pw = r1
                int r1 = r13.spanSize
                int r1 = r1 + r9
                r13.spanSize = r1
                float r4 = r4 + r8
                int r5 = r5 + 1
                r1 = r25
                r0 = r33
                r12 = 2
                goto L_0x0854
            L_0x08ea:
                r33 = r0
                r25 = r1
                r27 = r2
                r0 = r37
            L_0x08f2:
                r1 = 108(0x6c, float:1.51E-43)
                r2 = 0
            L_0x08f5:
                r3 = r22
                if (r2 >= r3) goto L_0x09ea
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r4 = r10.posArray
                java.lang.Object r4 = r4.get(r2)
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r4 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r4
                if (r16 == 0) goto L_0x0918
                byte r5 = r4.minX
                if (r5 != 0) goto L_0x090e
                int r5 = r4.spanSize
                int r7 = r10.firstSpanAdditionalSize
                int r5 = r5 + r7
                r4.spanSize = r5
            L_0x090e:
                int r5 = r4.flags
                r7 = 2
                r5 = r5 & r7
                if (r5 == 0) goto L_0x0931
                r5 = 1
                r4.edge = r5
                goto L_0x0931
            L_0x0918:
                byte r5 = r4.maxX
                if (r5 == r6) goto L_0x0922
                int r5 = r4.flags
                r7 = 2
                r5 = r5 & r7
                if (r5 == 0) goto L_0x0929
            L_0x0922:
                int r5 = r4.spanSize
                int r7 = r10.firstSpanAdditionalSize
                int r5 = r5 + r7
                r4.spanSize = r5
            L_0x0929:
                int r5 = r4.flags
                r7 = 1
                r5 = r5 & r7
                if (r5 == 0) goto L_0x0931
                r4.edge = r7
            L_0x0931:
                java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r5 = r10.messages
                java.lang.Object r5 = r5.get(r2)
                im.bclpbkiauv.messenger.MessageObject r5 = (im.bclpbkiauv.messenger.MessageObject) r5
                r7 = 1000(0x3e8, float:1.401E-42)
                if (r16 != 0) goto L_0x096f
                boolean r8 = r5.needDrawAvatarInternal()
                if (r8 == 0) goto L_0x096f
                boolean r8 = r4.edge
                if (r8 == 0) goto L_0x0956
                int r8 = r4.spanSize
                if (r8 == r7) goto L_0x0950
                int r8 = r4.spanSize
                int r8 = r8 + r1
                r4.spanSize = r8
            L_0x0950:
                int r8 = r4.pw
                int r8 = r8 + r1
                r4.pw = r8
                goto L_0x096f
            L_0x0956:
                int r8 = r4.flags
                r9 = 2
                r8 = r8 & r9
                if (r8 == 0) goto L_0x096f
                int r8 = r4.spanSize
                if (r8 == r7) goto L_0x0966
                int r8 = r4.spanSize
                int r8 = r8 - r1
                r4.spanSize = r8
                goto L_0x096f
            L_0x0966:
                int r8 = r4.leftSpanOffset
                if (r8 == 0) goto L_0x096f
                int r8 = r4.leftSpanOffset
                int r8 = r8 + r1
                r4.leftSpanOffset = r8
            L_0x096f:
                if (r16 != 0) goto L_0x09ac
                boolean r8 = r5.needDrawAvatar()
                if (r8 == 0) goto L_0x09ac
                boolean r8 = r4.edge
                if (r8 == 0) goto L_0x098c
                int r8 = r4.spanSize
                if (r8 == r7) goto L_0x0984
                int r7 = r4.spanSize
                int r7 = r7 + r1
                r4.spanSize = r7
            L_0x0984:
                int r7 = r4.pw
                int r7 = r7 + r1
                r4.pw = r7
                r9 = 2
                r11 = 1
                goto L_0x09e4
            L_0x098c:
                int r8 = r4.flags
                r9 = 2
                r8 = r8 & r9
                if (r8 == 0) goto L_0x09aa
                int r8 = r4.spanSize
                if (r8 == r7) goto L_0x099d
                int r7 = r4.spanSize
                int r7 = r7 - r1
                r4.spanSize = r7
                r11 = 1
                goto L_0x09e4
            L_0x099d:
                int r7 = r4.leftSpanOffset
                if (r7 == 0) goto L_0x09a8
                int r7 = r4.leftSpanOffset
                int r7 = r7 + r1
                r4.leftSpanOffset = r7
                r11 = 1
                goto L_0x09e4
            L_0x09a8:
                r11 = 1
                goto L_0x09e4
            L_0x09aa:
                r11 = 1
                goto L_0x09e4
            L_0x09ac:
                r9 = 2
                if (r16 == 0) goto L_0x09e3
                boolean r8 = r5.needDrawAvatar()
                if (r8 == 0) goto L_0x09e3
                boolean r8 = r4.edge
                if (r8 == 0) goto L_0x09c9
                int r8 = r4.spanSize
                if (r8 == r7) goto L_0x09c2
                int r7 = r4.spanSize
                int r7 = r7 + r1
                r4.spanSize = r7
            L_0x09c2:
                int r7 = r4.pw
                int r7 = r7 + r1
                r4.pw = r7
                r11 = 1
                goto L_0x09e4
            L_0x09c9:
                int r8 = r4.flags
                r11 = 1
                r8 = r8 & r11
                if (r8 == 0) goto L_0x09e4
                int r8 = r4.spanSize
                if (r8 == r7) goto L_0x09d9
                int r7 = r4.spanSize
                int r7 = r7 - r1
                r4.spanSize = r7
                goto L_0x09e4
            L_0x09d9:
                int r7 = r4.leftSpanOffset
                if (r7 == 0) goto L_0x09e4
                int r7 = r4.leftSpanOffset
                int r7 = r7 + r1
                r4.leftSpanOffset = r7
                goto L_0x09e4
            L_0x09e3:
                r11 = 1
            L_0x09e4:
                int r2 = r2 + 1
                r22 = r3
                goto L_0x08f5
            L_0x09ea:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MessageObject.GroupedMessages.calculate():void");
        }
    }

    public MessageObject(int accountNum, TLRPC.Message message, String formattedMessage, String name, String userName, boolean localMessage, boolean isChannel, boolean edit) {
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.entitiesCopy = new ArrayList<>();
        this.localType = localMessage ? 2 : 1;
        this.currentAccount = accountNum;
        this.localName = name;
        this.localUserName = userName;
        this.messageText = formattedMessage;
        this.messageOwner = message;
        this.localChannel = isChannel;
        this.localEdit = edit;
    }

    public MessageObject(int accountNum, TLRPC.Message message, AbstractMap<Integer, TLRPC.User> users, boolean generateLayout) {
        this(accountNum, message, users, (AbstractMap<Integer, TLRPC.Chat>) null, generateLayout);
    }

    public MessageObject(int accountNum, TLRPC.Message message, SparseArray<TLRPC.User> users, boolean generateLayout) {
        this(accountNum, message, users, (SparseArray<TLRPC.Chat>) null, generateLayout);
    }

    public MessageObject(int accountNum, TLRPC.Message message, boolean generateLayout) {
        this(accountNum, message, (MessageObject) null, (AbstractMap<Integer, TLRPC.User>) null, (AbstractMap<Integer, TLRPC.Chat>) null, (SparseArray<TLRPC.User>) null, (SparseArray<TLRPC.Chat>) null, generateLayout, 0);
    }

    public MessageObject(int accountNum, TLRPC.Message message, MessageObject replyToMessage, boolean generateLayout) {
        this(accountNum, message, replyToMessage, (AbstractMap<Integer, TLRPC.User>) null, (AbstractMap<Integer, TLRPC.Chat>) null, (SparseArray<TLRPC.User>) null, (SparseArray<TLRPC.Chat>) null, generateLayout, 0);
    }

    public MessageObject(int accountNum, TLRPC.Message message, AbstractMap<Integer, TLRPC.User> users, AbstractMap<Integer, TLRPC.Chat> chats, boolean generateLayout) {
        this(accountNum, message, users, chats, generateLayout, 0);
    }

    public MessageObject(int accountNum, TLRPC.Message message, SparseArray<TLRPC.User> users, SparseArray<TLRPC.Chat> chats, boolean generateLayout) {
        this(accountNum, message, (MessageObject) null, (AbstractMap<Integer, TLRPC.User>) null, (AbstractMap<Integer, TLRPC.Chat>) null, users, chats, generateLayout, 0);
    }

    public MessageObject(int accountNum, TLRPC.Message message, AbstractMap<Integer, TLRPC.User> users, AbstractMap<Integer, TLRPC.Chat> chats, boolean generateLayout, long eid) {
        this(accountNum, message, (MessageObject) null, users, chats, (SparseArray<TLRPC.User>) null, (SparseArray<TLRPC.Chat>) null, generateLayout, eid);
    }

    public MessageObject(int accountNum, TLRPC.Message message, MessageObject replyToMessage, AbstractMap<Integer, TLRPC.User> users, AbstractMap<Integer, TLRPC.Chat> chats, SparseArray<TLRPC.User> sUsers, SparseArray<TLRPC.Chat> sChats, boolean generateLayout, long eid) {
        TLRPC.User fromUser;
        boolean z;
        int[] iArr;
        int i;
        TextPaint paint;
        TLRPC.Message message2 = message;
        AbstractMap<Integer, TLRPC.User> abstractMap = users;
        SparseArray<TLRPC.User> sparseArray = sUsers;
        boolean z2 = generateLayout;
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.entitiesCopy = new ArrayList<>();
        Theme.createChatResources((Context) null, true);
        this.currentAccount = accountNum;
        this.messageOwner = message2;
        this.replyMessageObject = replyToMessage;
        this.eventId = eid;
        if (message2.replyMessage != null) {
            MessageObject messageObject = r9;
            MessageObject messageObject2 = new MessageObject(this.currentAccount, message2.replyMessage, (MessageObject) null, users, chats, sUsers, sChats, false, eid);
            this.replyMessageObject = messageObject;
        }
        TLRPC.User fromUser2 = null;
        if (message2.from_id > 0) {
            if (abstractMap != null) {
                fromUser2 = abstractMap.get(Integer.valueOf(message2.from_id));
            } else if (sparseArray != null) {
                fromUser2 = sparseArray.get(message2.from_id);
            }
            if (fromUser2 == null) {
                fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(message2.from_id));
            } else {
                fromUser = fromUser2;
            }
        } else {
            fromUser = null;
        }
        updateMessageText(abstractMap, chats, sparseArray, sChats);
        if (isMediaEmpty()) {
            i = 1;
            iArr = null;
            z = z2;
            this.messageText = updateMetionText(this.messageText, this.messageOwner.entities, users, chats, sUsers, sChats);
        } else {
            iArr = null;
            z = z2;
            i = 1;
        }
        setType();
        measureInlineBotButtons();
        Calendar rightNow = new GregorianCalendar();
        rightNow.setTimeInMillis(((long) this.messageOwner.date) * 1000);
        int dateDay = rightNow.get(6);
        int dateYear = rightNow.get(i);
        int dateMonth = rightNow.get(2);
        Object[] objArr = new Object[3];
        objArr[0] = Integer.valueOf(dateYear);
        objArr[i] = Integer.valueOf(dateMonth);
        objArr[2] = Integer.valueOf(dateDay);
        this.dateKey = String.format("%d_%02d_%02d", objArr);
        Object[] objArr2 = new Object[2];
        objArr2[0] = Integer.valueOf(dateYear);
        objArr2[i] = Integer.valueOf(dateMonth);
        this.monthKey = String.format("%d_%02d", objArr2);
        createMessageSendInfo();
        generateCaption();
        if (z) {
            if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                paint = Theme.chat_msgGameTextPaint;
            } else {
                paint = Theme.chat_msgTextPaint;
            }
            int[] emojiOnly = SharedConfig.allowBigEmoji ? new int[i] : iArr;
            this.messageText = Emoji.replaceEmoji(this.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly);
            checkEmojiOnly(emojiOnly);
            this.emojiAnimatedSticker = null;
            if (this.emojiOnlyCount == 1 && !(message2.media instanceof TLRPC.TL_messageMediaWebPage) && message2.entities.isEmpty()) {
                CharSequence emoji = this.messageText;
                int indexOf = TextUtils.indexOf(emoji, "🏻");
                int index = indexOf;
                if (indexOf >= 0) {
                    this.emojiAnimatedStickerColor = "_c1";
                    emoji = emoji.subSequence(0, index);
                } else {
                    int indexOf2 = TextUtils.indexOf(emoji, "🏼");
                    int index2 = indexOf2;
                    if (indexOf2 >= 0) {
                        this.emojiAnimatedStickerColor = "_c2";
                        emoji = emoji.subSequence(0, index2);
                    } else {
                        int indexOf3 = TextUtils.indexOf(emoji, "🏽");
                        int index3 = indexOf3;
                        if (indexOf3 >= 0) {
                            this.emojiAnimatedStickerColor = "_c3";
                            emoji = emoji.subSequence(0, index3);
                        } else {
                            int indexOf4 = TextUtils.indexOf(emoji, "🏾");
                            int index4 = indexOf4;
                            if (indexOf4 >= 0) {
                                this.emojiAnimatedStickerColor = "_c4";
                                emoji = emoji.subSequence(0, index4);
                            } else {
                                int indexOf5 = TextUtils.indexOf(emoji, "🏿");
                                int index5 = indexOf5;
                                if (indexOf5 >= 0) {
                                    this.emojiAnimatedStickerColor = "_c5";
                                    emoji = emoji.subSequence(0, index5);
                                } else {
                                    this.emojiAnimatedStickerColor = "";
                                }
                            }
                        }
                    }
                }
                this.emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoji);
            }
            if (this.emojiAnimatedSticker == null) {
                generateLayout(fromUser);
            } else {
                this.type = 1000;
                if (isSticker()) {
                    this.type = 13;
                } else if (isAnimatedSticker()) {
                    this.type = 15;
                }
            }
        }
        this.layoutCreated = z;
        generateThumbs(false);
        checkMediaExistance();
    }

    public void renderText() {
        entityCopy(this.messageOwner.entities);
        addEntitiesToText(this.messageText, false);
    }

    private void entityCopy(ArrayList<TLRPC.MessageEntity> entities) {
        if (entities != null && entities.size() != 0) {
            this.entitiesCopy.clear();
            Iterator<TLRPC.MessageEntity> it = entities.iterator();
            while (it.hasNext()) {
                TLRPC.MessageEntity entity = it.next();
                if (entity instanceof TLRPC.TL_messageEntityTextUrl) {
                    TLRPC.TL_messageEntityTextUrl item = new TLRPC.TL_messageEntityTextUrl();
                    item.offset = entity.offset;
                    item.length = entity.length;
                    item.url = entity.url;
                    this.entitiesCopy.add(item);
                } else if (entity instanceof TLRPC.TL_messageEntityBotCommand) {
                    TLRPC.TL_messageEntityBotCommand item2 = new TLRPC.TL_messageEntityBotCommand();
                    item2.offset = entity.offset;
                    item2.length = entity.length;
                    this.entitiesCopy.add(item2);
                } else if (entity instanceof TLRPC.TL_messageEntityEmail) {
                    TLRPC.TL_messageEntityEmail item3 = new TLRPC.TL_messageEntityEmail();
                    item3.offset = entity.offset;
                    item3.length = entity.length;
                    this.entitiesCopy.add(item3);
                } else if (entity instanceof TLRPC.TL_messageEntityPre) {
                    TLRPC.TL_messageEntityPre item4 = new TLRPC.TL_messageEntityPre();
                    item4.offset = entity.offset;
                    item4.length = entity.length;
                    item4.language = entity.language;
                    this.entitiesCopy.add(item4);
                } else if (entity instanceof TLRPC.TL_messageEntityUnknown) {
                    TLRPC.TL_messageEntityUnknown item5 = new TLRPC.TL_messageEntityUnknown();
                    item5.offset = entity.offset;
                    item5.length = entity.length;
                    this.entitiesCopy.add(item5);
                } else if (entity instanceof TLRPC.TL_messageEntityUrl) {
                    TLRPC.TL_messageEntityUrl item6 = new TLRPC.TL_messageEntityUrl();
                    item6.offset = entity.offset;
                    item6.length = entity.length;
                    this.entitiesCopy.add(item6);
                } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                    TLRPC.TL_messageEntityItalic item7 = new TLRPC.TL_messageEntityItalic();
                    item7.offset = entity.offset;
                    item7.length = entity.length;
                    this.entitiesCopy.add(item7);
                } else if (entity instanceof TLRPC.TL_messageEntityMention) {
                    TLRPC.TL_messageEntityMention item8 = new TLRPC.TL_messageEntityMention();
                    item8.offset = entity.offset;
                    item8.length = entity.length;
                    this.entitiesCopy.add(item8);
                } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                    TLRPC.TL_messageEntityMentionName item9 = new TLRPC.TL_messageEntityMentionName();
                    item9.offset = entity.offset;
                    item9.length = entity.length;
                    item9.user_id = ((TLRPC.TL_messageEntityMentionName) entity).user_id;
                    this.entitiesCopy.add(item9);
                } else if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    TLRPC.TL_inputMessageEntityMentionName item10 = new TLRPC.TL_inputMessageEntityMentionName();
                    item10.offset = entity.offset;
                    item10.length = entity.length;
                    item10.user_id = ((TLRPC.TL_inputMessageEntityMentionName) entity).user_id;
                    this.entitiesCopy.add(item10);
                } else if (entity instanceof TLRPC.TL_messageEntityCashtag) {
                    TLRPC.TL_messageEntityCashtag item11 = new TLRPC.TL_messageEntityCashtag();
                    item11.offset = entity.offset;
                    item11.length = entity.length;
                    this.entitiesCopy.add(item11);
                } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                    TLRPC.TL_messageEntityBold item12 = new TLRPC.TL_messageEntityBold();
                    item12.offset = entity.offset;
                    item12.length = entity.length;
                    this.entitiesCopy.add(item12);
                } else if (entity instanceof TLRPC.TL_messageEntityHashtag) {
                    TLRPC.TL_messageEntityHashtag item13 = new TLRPC.TL_messageEntityHashtag();
                    item13.offset = entity.offset;
                    item13.length = entity.length;
                    this.entitiesCopy.add(item13);
                } else if (entity instanceof TLRPC.TL_messageEntityCode) {
                    TLRPC.TL_messageEntityCode item14 = new TLRPC.TL_messageEntityCode();
                    item14.offset = entity.offset;
                    item14.length = entity.length;
                    this.entitiesCopy.add(item14);
                } else if (entity instanceof TLRPC.TL_messageEntityStrike) {
                    TLRPC.TL_messageEntityStrike item15 = new TLRPC.TL_messageEntityStrike();
                    item15.offset = entity.offset;
                    item15.length = entity.length;
                    this.entitiesCopy.add(item15);
                } else if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                    TLRPC.TL_messageEntityBlockquote item16 = new TLRPC.TL_messageEntityBlockquote();
                    item16.offset = entity.offset;
                    item16.length = entity.length;
                    this.entitiesCopy.add(item16);
                } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                    TLRPC.TL_messageEntityUnderline item17 = new TLRPC.TL_messageEntityUnderline();
                    item17.offset = entity.offset;
                    item17.length = entity.length;
                    this.entitiesCopy.add(item17);
                } else if (entity instanceof TLRPC.TL_messageEntityPhone) {
                    TLRPC.TL_messageEntityPhone item18 = new TLRPC.TL_messageEntityPhone();
                    item18.offset = entity.offset;
                    item18.length = entity.length;
                    this.entitiesCopy.add(item18);
                }
            }
        }
    }

    public CharSequence updateMetionText(CharSequence text, ArrayList<TLRPC.MessageEntity> entities) {
        return updateMetionText(text, entities, (AbstractMap<Integer, TLRPC.User>) null, (AbstractMap<Integer, TLRPC.Chat>) null, (SparseArray<TLRPC.User>) null, (SparseArray<TLRPC.Chat>) null);
    }

    public CharSequence updateMetionText(CharSequence text, ArrayList<TLRPC.MessageEntity> entities, AbstractMap<Integer, TLRPC.User> users, AbstractMap<Integer, TLRPC.Chat> abstractMap, SparseArray<TLRPC.User> sUsers, SparseArray<TLRPC.Chat> sparseArray) {
        byte t;
        SpannableString result;
        Spannable spannable;
        SpannableString result2;
        MessageObject messageObject = this;
        ArrayList<TLRPC.MessageEntity> arrayList = entities;
        AbstractMap<Integer, TLRPC.User> abstractMap2 = users;
        SparseArray<TLRPC.User> sparseArray2 = sUsers;
        if (arrayList == null || entities.isEmpty()) {
            return text;
        }
        Spannable spannable2 = SpannableString.valueOf(text);
        CharSequence result3 = new SpannableString("");
        URLSpan[] spans = (URLSpan[]) spannable2.getSpans(0, text.length(), URLSpan.class);
        ArrayList<TextStyleSpan.TextStyleRun> runs = new ArrayList<>();
        messageObject.entityCopy(arrayList);
        Collections.sort(messageObject.entitiesCopy, $$Lambda$MessageObject$ig_mkAirjvBa9WjYELj4vXDGjyA.INSTANCE);
        int a = 0;
        int N = messageObject.entitiesCopy.size();
        while (a < N) {
            TLRPC.MessageEntity entity = messageObject.entitiesCopy.get(a);
            if (entity.length <= 0 || entity.offset < 0) {
                spannable = spannable2;
                result = result3;
            } else if (entity.offset >= text.length()) {
                spannable = spannable2;
                result = result3;
            } else {
                if (entity.offset + entity.length > text.length()) {
                    entity.length = text.length() - entity.offset;
                }
                if (((entity instanceof TLRPC.TL_messageEntityBold) || (entity instanceof TLRPC.TL_messageEntityItalic) || (entity instanceof TLRPC.TL_messageEntityStrike) || (entity instanceof TLRPC.TL_messageEntityUnderline) || (entity instanceof TLRPC.TL_messageEntityBlockquote) || (entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityPre) || (entity instanceof TLRPC.TL_messageEntityMentionName) || (entity instanceof TLRPC.TL_inputMessageEntityMentionName) || (entity instanceof TLRPC.TL_messageEntityTextUrl)) && spans != null && spans.length > 0) {
                    for (int b = 0; b < spans.length; b++) {
                        if (spans[b] != null) {
                            int start = spannable2.getSpanStart(spans[b]);
                            int end = spannable2.getSpanEnd(spans[b]);
                            if ((entity.offset <= start && entity.offset + entity.length >= start) || (entity.offset <= end && entity.offset + entity.length >= end)) {
                                spannable2.removeSpan(spans[b]);
                                spans[b] = null;
                            }
                        }
                    }
                }
                TextStyleSpan.TextStyleRun newRun = new TextStyleSpan.TextStyleRun();
                newRun.start = entity.offset;
                newRun.end = newRun.start + entity.length;
                if (entity instanceof TLRPC.TL_messageEntityStrike) {
                    newRun.flags = 8;
                } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                    newRun.flags = 16;
                } else if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                    newRun.flags = 32;
                } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                    newRun.flags = 1;
                } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                    newRun.flags = 2;
                } else if ((entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityPre)) {
                    newRun.flags = 4;
                } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                    newRun.flags = 64;
                    newRun.urlEntity = entity;
                } else if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    newRun.flags = 64;
                    newRun.urlEntity = entity;
                }
                int b2 = 0;
                int N2 = runs.size();
                while (b2 < N2) {
                    TextStyleSpan.TextStyleRun run = runs.get(b2);
                    Spannable spannable3 = spannable2;
                    if (newRun.start > run.start) {
                        if (newRun.start < run.end) {
                            if (newRun.end < run.end) {
                                TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                                r.merge(run);
                                int b3 = b2 + 1;
                                runs.add(b3, r);
                                TextStyleSpan.TextStyleRun r2 = new TextStyleSpan.TextStyleRun(run);
                                r2.start = newRun.end;
                                b2 = b3 + 1;
                                N2 = N2 + 1 + 1;
                                runs.add(b2, r2);
                            } else if (newRun.end >= run.end) {
                                TextStyleSpan.TextStyleRun r3 = new TextStyleSpan.TextStyleRun(newRun);
                                r3.merge(run);
                                r3.end = run.end;
                                b2++;
                                N2++;
                                runs.add(b2, r3);
                            }
                            int temp = newRun.start;
                            newRun.start = run.end;
                            run.end = temp;
                            result2 = result3;
                            b2++;
                            ArrayList<TLRPC.MessageEntity> arrayList2 = entities;
                            spannable2 = spannable3;
                            result3 = result2;
                        }
                    } else if (run.start < newRun.end) {
                        int temp2 = run.start;
                        result2 = result3;
                        if (newRun.end == run.end) {
                            run.merge(newRun);
                        } else if (newRun.end < run.end) {
                            TextStyleSpan.TextStyleRun r4 = new TextStyleSpan.TextStyleRun(run);
                            r4.merge(newRun);
                            r4.end = newRun.end;
                            b2++;
                            N2++;
                            runs.add(b2, r4);
                            run.start = newRun.end;
                        } else {
                            TextStyleSpan.TextStyleRun r5 = new TextStyleSpan.TextStyleRun(newRun);
                            r5.start = run.end;
                            b2++;
                            N2++;
                            runs.add(b2, r5);
                            run.merge(newRun);
                        }
                        newRun.end = temp2;
                        b2++;
                        ArrayList<TLRPC.MessageEntity> arrayList22 = entities;
                        spannable2 = spannable3;
                        result3 = result2;
                    }
                    result2 = result3;
                    b2++;
                    ArrayList<TLRPC.MessageEntity> arrayList222 = entities;
                    spannable2 = spannable3;
                    result3 = result2;
                }
                spannable = spannable2;
                result = result3;
                if (newRun.start < newRun.end) {
                    runs.add(newRun);
                }
            }
            a++;
            ArrayList<TLRPC.MessageEntity> arrayList3 = entities;
            spannable2 = spannable;
            result3 = result;
        }
        Spannable spannable4 = spannable2;
        CharSequence result4 = result3;
        int count = runs.size();
        byte t2 = 1;
        int a2 = 0;
        while (a2 < count) {
            TextStyleSpan.TextStyleRun run2 = runs.get(a2);
            if (run2.urlEntity instanceof TLRPC.TL_messageEntityMentionName) {
                int uid = ((TLRPC.TL_messageEntityMentionName) run2.urlEntity).user_id;
                TLRPC.User user = null;
                if (!(abstractMap2 == null && sparseArray2 == null)) {
                    user = abstractMap2 != null ? abstractMap2.get(Integer.valueOf(uid)) : sparseArray2.get(uid);
                }
                if (user == null) {
                    TLRPC.User user2 = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(uid));
                }
                TLRPC.User user3 = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(((TLRPC.TL_messageEntityMentionName) run2.urlEntity).user_id));
                if (user3 != null) {
                    CharSequence content = text;
                    if (!TextUtils.isEmpty(result4)) {
                        content = result4;
                    }
                    SpannableStringBuilder builder = new SpannableStringBuilder(content);
                    String name = "@" + UserObject.getName(user3);
                    if (run2.start < 0) {
                        run2.start = 0;
                    }
                    builder.replace(run2.start, run2.end, name);
                    int oriEnd = run2.end;
                    run2.end = run2.start + name.length();
                    int newOffset = oriEnd - run2.end;
                    int j = 0;
                    while (j < messageObject.entitiesCopy.size()) {
                        TLRPC.MessageEntity messageEntity = messageObject.entitiesCopy.get(j);
                        byte t3 = t2;
                        if (messageEntity.offset >= run2.start) {
                            if (messageEntity.offset == run2.start) {
                                messageEntity.length = name.length();
                            } else {
                                messageEntity.offset -= newOffset;
                            }
                        }
                        j++;
                        messageObject = this;
                        SparseArray<TLRPC.User> sparseArray3 = sUsers;
                        t2 = t3;
                    }
                    t = t2;
                    for (int j2 = a2; j2 < count; j2++) {
                        TextStyleSpan.TextStyleRun nextRun = runs.get(j2);
                        nextRun.start -= newOffset;
                        nextRun.end -= newOffset;
                    }
                    spannable4 = SpannableString.valueOf(builder);
                } else {
                    t = t2;
                    FileLog.e(getClass().getSimpleName(), "load msg, u is null.");
                }
                result4 = SpannableString.valueOf(spannable4);
            } else {
                t = t2;
            }
            a2++;
            messageObject = this;
            abstractMap2 = users;
            sparseArray2 = sUsers;
            t2 = t;
        }
        if (!TextUtils.isEmpty(result4) || TextUtils.isEmpty(text)) {
            return result4;
        }
        return SpannableString.valueOf(text);
    }

    static /* synthetic */ int lambda$updateMetionText$0(TLRPC.MessageEntity o1, TLRPC.MessageEntity o2) {
        if (o1.offset > o2.offset) {
            return 1;
        }
        if (o1.offset < o2.offset) {
            return -1;
        }
        return 0;
    }

    public SpannableStringBuilder updateMetionText2(CharSequence text, ArrayList<TLRPC.MessageEntity> entities, BaseFragment baseFragment) {
        byte t;
        URLSpan[] spans;
        Spannable spannable;
        MessageObject messageObject = this;
        ArrayList<TLRPC.MessageEntity> arrayList = entities;
        if (arrayList == null) {
            BaseFragment baseFragment2 = baseFragment;
        } else if (entities.isEmpty()) {
            BaseFragment baseFragment3 = baseFragment;
        } else {
            Spannable spannable2 = SpannableString.valueOf(text);
            SpannableStringBuilder result = new SpannableStringBuilder("");
            URLSpan[] spans2 = (URLSpan[]) spannable2.getSpans(0, text.length(), URLSpan.class);
            ArrayList<TextStyleSpan.TextStyleRun> runs = new ArrayList<>();
            messageObject.entityCopy(arrayList);
            Collections.sort(messageObject.entitiesCopy, $$Lambda$MessageObject$vz5BHkvKTw7PitrlU5kfp9npkw.INSTANCE);
            int a = 0;
            int N = messageObject.entitiesCopy.size();
            while (a < N) {
                TLRPC.MessageEntity entity = messageObject.entitiesCopy.get(a);
                if (entity.length > 0 && entity.offset >= 0 && entity.offset < text.length()) {
                    if (entity.offset + entity.length > text.length()) {
                        entity.length = text.length() - entity.offset;
                    }
                    if (((entity instanceof TLRPC.TL_messageEntityBold) || (entity instanceof TLRPC.TL_messageEntityItalic) || (entity instanceof TLRPC.TL_messageEntityStrike) || (entity instanceof TLRPC.TL_messageEntityUnderline) || (entity instanceof TLRPC.TL_messageEntityBlockquote) || (entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityPre) || (entity instanceof TLRPC.TL_messageEntityMentionName) || (entity instanceof TLRPC.TL_inputMessageEntityMentionName) || (entity instanceof TLRPC.TL_messageEntityTextUrl)) && spans2 != null && spans2.length > 0) {
                        for (int b = 0; b < spans2.length; b++) {
                            if (spans2[b] != null) {
                                int start = spannable2.getSpanStart(spans2[b]);
                                int end = spannable2.getSpanEnd(spans2[b]);
                                if ((entity.offset <= start && entity.offset + entity.length >= start) || (entity.offset <= end && entity.offset + entity.length >= end)) {
                                    spannable2.removeSpan(spans2[b]);
                                    spans2[b] = null;
                                }
                            }
                        }
                    }
                    TextStyleSpan.TextStyleRun newRun = new TextStyleSpan.TextStyleRun();
                    newRun.start = entity.offset;
                    newRun.end = newRun.start + entity.length;
                    if (entity instanceof TLRPC.TL_messageEntityStrike) {
                        newRun.flags = 8;
                    } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                        newRun.flags = 16;
                    } else if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                        newRun.flags = 32;
                    } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                        newRun.flags = 1;
                    } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                        newRun.flags = 2;
                    } else if ((entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityPre)) {
                        newRun.flags = 4;
                    } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                        newRun.flags = 64;
                        newRun.urlEntity = entity;
                    } else if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                        newRun.flags = 64;
                        newRun.urlEntity = entity;
                    }
                    int b2 = 0;
                    int N2 = runs.size();
                    while (b2 < N2) {
                        TextStyleSpan.TextStyleRun run = runs.get(b2);
                        if (newRun.start > run.start) {
                            if (newRun.start < run.end) {
                                if (newRun.end < run.end) {
                                    TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                                    r.merge(run);
                                    int b3 = b2 + 1;
                                    runs.add(b3, r);
                                    TextStyleSpan.TextStyleRun r2 = new TextStyleSpan.TextStyleRun(run);
                                    r2.start = newRun.end;
                                    b2 = b3 + 1;
                                    N2 = N2 + 1 + 1;
                                    runs.add(b2, r2);
                                } else if (newRun.end >= run.end) {
                                    TextStyleSpan.TextStyleRun r3 = new TextStyleSpan.TextStyleRun(newRun);
                                    r3.merge(run);
                                    r3.end = run.end;
                                    b2++;
                                    N2++;
                                    runs.add(b2, r3);
                                }
                                int temp = newRun.start;
                                newRun.start = run.end;
                                run.end = temp;
                            }
                        } else if (run.start < newRun.end) {
                            int temp2 = run.start;
                            if (newRun.end == run.end) {
                                run.merge(newRun);
                            } else if (newRun.end < run.end) {
                                TextStyleSpan.TextStyleRun r4 = new TextStyleSpan.TextStyleRun(run);
                                r4.merge(newRun);
                                r4.end = newRun.end;
                                b2++;
                                N2++;
                                runs.add(b2, r4);
                                run.start = newRun.end;
                            } else {
                                TextStyleSpan.TextStyleRun r5 = new TextStyleSpan.TextStyleRun(newRun);
                                r5.start = run.end;
                                b2++;
                                N2++;
                                runs.add(b2, r5);
                                run.merge(newRun);
                            }
                            newRun.end = temp2;
                        }
                        b2++;
                        ArrayList<TLRPC.MessageEntity> arrayList2 = entities;
                    }
                    if (newRun.start < newRun.end) {
                        runs.add(newRun);
                    }
                }
                a++;
                ArrayList<TLRPC.MessageEntity> arrayList3 = entities;
            }
            int count = runs.size();
            byte t2 = true;
            int a2 = 0;
            while (a2 < count) {
                TextStyleSpan.TextStyleRun run2 = runs.get(a2);
                if (run2.urlEntity instanceof TLRPC.TL_messageEntityMentionName) {
                    TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(((TLRPC.TL_messageEntityMentionName) run2.urlEntity).user_id));
                    if (user != null) {
                        CharSequence content = text;
                        if (!TextUtils.isEmpty(result)) {
                            content = result;
                        }
                        SpannableStringBuilder builder = new SpannableStringBuilder(content);
                        String name = "@" + UserObject.getName(user) + " ";
                        builder.replace(run2.start, run2.end, name);
                        int oriEnd = run2.end;
                        run2.end = run2.start + name.length();
                        spannable = spannable2;
                        spans = spans2;
                        builder.setSpan(new ForegroundColorSpan(-13915656), run2.start, run2.end, 33);
                        SysNotifyAtTextClickableSpan sysNotifyAtTextClickableSpan = new SysNotifyAtTextClickableSpan(user.id, baseFragment);
                        t = t2;
                        builder.setSpan(sysNotifyAtTextClickableSpan, run2.start, run2.end, 33);
                        int newOffset = oriEnd - run2.end;
                        int j = 0;
                        while (j < messageObject.entitiesCopy.size()) {
                            TLRPC.MessageEntity messageEntity = messageObject.entitiesCopy.get(j);
                            SysNotifyAtTextClickableSpan sysNotifyAtTextClickableSpan2 = sysNotifyAtTextClickableSpan;
                            if (messageEntity.offset >= run2.start) {
                                if (messageEntity.offset == run2.start) {
                                    messageEntity.length = name.length();
                                } else {
                                    messageEntity.offset -= newOffset;
                                }
                            }
                            j++;
                            messageObject = this;
                            sysNotifyAtTextClickableSpan = sysNotifyAtTextClickableSpan2;
                        }
                        for (int j2 = a2; j2 < count; j2++) {
                            TextStyleSpan.TextStyleRun nextRun = runs.get(j2);
                            nextRun.start -= newOffset;
                            nextRun.end -= newOffset;
                        }
                        result = builder;
                    } else {
                        BaseFragment baseFragment4 = baseFragment;
                        spannable = spannable2;
                        spans = spans2;
                        t = t2;
                    }
                } else {
                    BaseFragment baseFragment5 = baseFragment;
                    spannable = spannable2;
                    spans = spans2;
                    t = t2;
                }
                a2++;
                messageObject = this;
                spannable2 = spannable;
                spans2 = spans;
                t2 = t;
            }
            BaseFragment baseFragment6 = baseFragment;
            Spannable spannable3 = spannable2;
            URLSpan[] uRLSpanArr = spans2;
            byte b4 = t2;
            if (!TextUtils.isEmpty(result) || TextUtils.isEmpty(text)) {
                return result;
            }
            return SpannableStringBuilder.valueOf(text);
        }
        return new SpannableStringBuilder(text);
    }

    static /* synthetic */ int lambda$updateMetionText2$1(TLRPC.MessageEntity o1, TLRPC.MessageEntity o2) {
        if (o1.offset > o2.offset) {
            return 1;
        }
        if (o1.offset < o2.offset) {
            return -1;
        }
        return 0;
    }

    private void createDateArray(int accountNum, TLRPC.TL_channelAdminLogEvent event, ArrayList<MessageObject> messageObjects, HashMap<String, ArrayList<MessageObject>> messagesByDays) {
        if (messagesByDays.get(this.dateKey) == null) {
            messagesByDays.put(this.dateKey, new ArrayList());
            TLRPC.TL_message dateMsg = new TLRPC.TL_message();
            dateMsg.message = LocaleController.formatDateChat((long) event.date);
            dateMsg.id = 0;
            dateMsg.date = event.date;
            MessageObject dateObj = new MessageObject(accountNum, dateMsg, false);
            dateObj.type = 10;
            dateObj.contentType = 1;
            dateObj.isDateObject = true;
            messageObjects.add(dateObj);
        }
    }

    public void checkForScam() {
    }

    private void checkEmojiOnly(int[] emojiOnly) {
        int size;
        TextPaint emojiPaint;
        if (emojiOnly != null && emojiOnly[0] >= 1 && emojiOnly[0] <= 3) {
            int i = emojiOnly[0];
            if (i == 1) {
                emojiPaint = Theme.chat_msgTextPaintOneEmoji;
                int size2 = AndroidUtilities.dp(32.0f);
                this.emojiOnlyCount = 1;
                size = size2;
            } else if (i != 2) {
                emojiPaint = Theme.chat_msgTextPaintThreeEmoji;
                size = AndroidUtilities.dp(24.0f);
                this.emojiOnlyCount = 3;
            } else {
                emojiPaint = Theme.chat_msgTextPaintTwoEmoji;
                int size3 = AndroidUtilities.dp(28.0f);
                this.emojiOnlyCount = 2;
                size = size3;
            }
            CharSequence charSequence = this.messageText;
            Emoji.EmojiSpan[] spans = (Emoji.EmojiSpan[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), Emoji.EmojiSpan.class);
            if (spans != null && spans.length > 0) {
                for (Emoji.EmojiSpan replaceFontMetrics : spans) {
                    replaceFontMetrics.replaceFontMetrics(emojiPaint.getFontMetricsInt(), size);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:256:0x078e  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x07a8 A[LOOP:0: B:242:0x0749->B:264:0x07a8, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x1008  */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x1054  */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x1073  */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x10d9  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x10e1  */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x114b A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:577:0x07c9 A[EDGE_INSN: B:577:0x07c9->B:267:0x07c9 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MessageObject(int r33, im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEvent r34, java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r35, java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject>> r36, im.bclpbkiauv.tgnet.TLRPC.Chat r37, int[] r38) {
        /*
            r32 = this;
            r0 = r32
            r1 = r34
            r2 = r35
            r3 = r36
            r4 = r37
            r32.<init>()
            r5 = 1000(0x3e8, float:1.401E-42)
            r0.type = r5
            r5 = -1082130432(0xffffffffbf800000, float:-1.0)
            r0.forceSeekTo = r5
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r0.entitiesCopy = r5
            r0.currentEvent = r1
            r5 = r33
            r0.currentAccount = r5
            r6 = 0
            int r7 = r1.user_id
            if (r7 <= 0) goto L_0x0039
            if (r6 != 0) goto L_0x0039
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r7 = im.bclpbkiauv.messenger.MessagesController.getInstance(r7)
            int r8 = r1.user_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            im.bclpbkiauv.tgnet.TLRPC$User r6 = r7.getUser(r8)
        L_0x0039:
            java.util.GregorianCalendar r7 = new java.util.GregorianCalendar
            r7.<init>()
            int r8 = r1.date
            long r8 = (long) r8
            r10 = 1000(0x3e8, double:4.94E-321)
            long r8 = r8 * r10
            r7.setTimeInMillis(r8)
            r8 = 6
            int r8 = r7.get(r8)
            r9 = 1
            int r10 = r7.get(r9)
            r11 = 2
            int r12 = r7.get(r11)
            r13 = 3
            java.lang.Object[] r14 = new java.lang.Object[r13]
            java.lang.Integer r15 = java.lang.Integer.valueOf(r10)
            r13 = 0
            r14[r13] = r15
            java.lang.Integer r15 = java.lang.Integer.valueOf(r12)
            r14[r9] = r15
            java.lang.Integer r15 = java.lang.Integer.valueOf(r8)
            r14[r11] = r15
            java.lang.String r15 = "%d_%02d_%02d"
            java.lang.String r14 = java.lang.String.format(r15, r14)
            r0.dateKey = r14
            java.lang.Object[] r14 = new java.lang.Object[r11]
            java.lang.Integer r15 = java.lang.Integer.valueOf(r10)
            r14[r13] = r15
            java.lang.Integer r15 = java.lang.Integer.valueOf(r12)
            r14[r9] = r15
            java.lang.String r15 = "%d_%02d"
            java.lang.String r14 = java.lang.String.format(r15, r14)
            r0.monthKey = r14
            im.bclpbkiauv.tgnet.TLRPC$TL_peerChannel r14 = new im.bclpbkiauv.tgnet.TLRPC$TL_peerChannel
            r14.<init>()
            int r15 = r4.id
            r14.channel_id = r15
            r15 = 0
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r11 = r1.action
            boolean r11 = r11 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle
            java.lang.String r13 = ""
            java.lang.String r9 = "un1"
            if (r11 == 0) goto L_0x00e7
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r11 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle r11 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle) r11
            java.lang.String r11 = r11.new_value
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x00c5
            r19 = r7
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]
            r17 = 0
            r7[r17] = r11
            java.lang.String r5 = "EventLogEditedGroupTitle"
            r20 = r8
            r8 = 2131691183(0x7f0f06af, float:1.901143E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r8, r7)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            goto L_0x00df
        L_0x00c5:
            r19 = r7
            r20 = r8
            r17 = 0
            r5 = 2131691180(0x7f0f06ac, float:1.9011425E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            r8[r17] = r11
            java.lang.String r7 = "EventLogEditedChannelTitle"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r5, r8)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
        L_0x00df:
            r21 = r10
            r22 = r12
            r27 = r15
            goto L_0x1002
        L_0x00e7:
            r19 = r7
            r20 = r8
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangePhoto
            if (r5 == 0) goto L_0x0180
            im.bclpbkiauv.tgnet.TLRPC$TL_messageService r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageService
            r5.<init>()
            r0.messageOwner = r5
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$Photo r5 = r5.new_photo
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photoEmpty
            if (r5 == 0) goto L_0x013b
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$TL_messageActionChatDeletePhoto r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            r7.<init>()
            r5.action = r7
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x0124
            r5 = 2131691233(0x7f0f06e1, float:1.9011532E38)
            java.lang.String r7 = "EventLogRemovedWGroupPhoto"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            r21 = r10
            r22 = r12
            r27 = r15
            goto L_0x1002
        L_0x0124:
            r5 = 2131691227(0x7f0f06db, float:1.901152E38)
            java.lang.String r7 = "EventLogRemovedChannelPhoto"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            r21 = r10
            r22 = r12
            r27 = r15
            goto L_0x1002
        L_0x013b:
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$TL_messageActionChatEditPhoto r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageActionChatEditPhoto
            r7.<init>()
            r5.action = r7
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r5 = r5.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$Photo r7 = r7.new_photo
            r5.photo = r7
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x0169
            r5 = 2131691182(0x7f0f06ae, float:1.9011429E38)
            java.lang.String r7 = "EventLogEditedGroupPhoto"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            r21 = r10
            r22 = r12
            r27 = r15
            goto L_0x1002
        L_0x0169:
            r5 = 2131691179(0x7f0f06ab, float:1.9011423E38)
            java.lang.String r7 = "EventLogEditedChannelPhoto"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            r21 = r10
            r22 = r12
            r27 = r15
            goto L_0x1002
        L_0x0180:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoin
            r7 = 2131691201(0x7f0f06c1, float:1.9011467E38)
            java.lang.String r8 = "EventLogGroupJoined"
            java.lang.String r11 = "EventLogChannelJoined"
            if (r5 == 0) goto L_0x01b8
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x01a3
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            r21 = r10
            r22 = r12
            r27 = r15
            goto L_0x1002
        L_0x01a3:
            r5 = 2131691172(0x7f0f06a4, float:1.9011408E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            r21 = r10
            r22 = r12
            r27 = r15
            goto L_0x1002
        L_0x01b8:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantLeave
            if (r5 == 0) goto L_0x0206
            im.bclpbkiauv.tgnet.TLRPC$TL_messageService r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageService
            r5.<init>()
            r0.messageOwner = r5
            im.bclpbkiauv.tgnet.TLRPC$TL_messageActionChatDeleteUser r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageActionChatDeleteUser
            r7.<init>()
            r5.action = r7
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r5 = r5.action
            int r7 = r1.user_id
            r5.user_id = r7
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x01ef
            r5 = 2131691206(0x7f0f06c6, float:1.9011477E38)
            java.lang.String r7 = "EventLogLeftGroup"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            r21 = r10
            r22 = r12
            r27 = r15
            goto L_0x1002
        L_0x01ef:
            r5 = 2131691205(0x7f0f06c5, float:1.9011475E38)
            java.lang.String r7 = "EventLogLeftChannel"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            r21 = r10
            r22 = r12
            r27 = r15
            goto L_0x1002
        L_0x0206:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantInvite
            java.lang.String r7 = "un2"
            if (r5 == 0) goto L_0x027a
            im.bclpbkiauv.tgnet.TLRPC$TL_messageService r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageService
            r5.<init>()
            r0.messageOwner = r5
            r21 = r10
            im.bclpbkiauv.tgnet.TLRPC$TL_messageActionChatAddUser r10 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageActionChatAddUser
            r10.<init>()
            r5.action = r10
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r10 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r10 = r10.participant
            int r10 = r10.user_id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r10)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r10 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r10 = r10.participant
            int r10 = r10.user_id
            r22 = r12
            im.bclpbkiauv.tgnet.TLRPC$Message r12 = r0.messageOwner
            int r12 = r12.from_id
            if (r10 != r12) goto L_0x0261
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x0253
            r7 = 2131691201(0x7f0f06c1, float:1.9011467E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r9, r6)
            r0.messageText = r7
            goto L_0x0276
        L_0x0253:
            r7 = 2131691172(0x7f0f06a4, float:1.9011408E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r7)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r9, r6)
            r0.messageText = r7
            goto L_0x0276
        L_0x0261:
            r8 = 2131691162(0x7f0f069a, float:1.9011388E38)
            java.lang.String r10 = "EventLogAdded"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r8)
            java.lang.CharSequence r7 = r0.replaceWithLink(r8, r7, r5)
            r0.messageText = r7
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r9, r6)
            r0.messageText = r7
        L_0x0276:
            r27 = r15
            goto L_0x1002
        L_0x027a:
            r21 = r10
            r22 = r12
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin
            java.lang.String r8 = "%1$s"
            if (r5 == 0) goto L_0x04f2
            im.bclpbkiauv.tgnet.TLRPC$TL_message r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_message
            r5.<init>()
            r0.messageOwner = r5
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r7 = r7.prev_participant
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r7)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r7 = r7.prev_participant
            boolean r7 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantCreator
            if (r7 != 0) goto L_0x02da
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r7 = r7.new_participant
            boolean r7 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantCreator
            if (r7 == 0) goto L_0x02da
            r7 = 2131691170(0x7f0f06a2, float:1.9011404E38)
            java.lang.String r9 = "EventLogChangedOwnership"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
            int r8 = r7.indexOf(r8)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]
            im.bclpbkiauv.tgnet.TLRPC$Message r10 = r0.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            java.lang.String r10 = r0.getUserName(r5, r10, r8)
            r12 = 0
            r11[r12] = r10
            java.lang.String r10 = java.lang.String.format(r7, r11)
            r9.<init>(r10)
            r7 = r9
            r16 = r5
            goto L_0x04e8
        L_0x02da:
            r7 = 2131691214(0x7f0f06ce, float:1.9011494E38)
            java.lang.String r9 = "EventLogPromoted"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
            int r8 = r7.indexOf(r8)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r12 = 1
            java.lang.Object[] r10 = new java.lang.Object[r12]
            im.bclpbkiauv.tgnet.TLRPC$Message r12 = r0.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r12 = r12.entities
            java.lang.String r12 = r0.getUserName(r5, r12, r8)
            r16 = 0
            r10[r16] = r12
            java.lang.String r10 = java.lang.String.format(r7, r10)
            r9.<init>(r10)
            java.lang.String r10 = "\n"
            r9.append(r10)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r10 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r10 = r10.prev_participant
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r10 = r10.admin_rights
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r12 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r12 = r12.new_participant
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r12 = r12.admin_rights
            if (r10 != 0) goto L_0x0319
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r16 = new im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights
            r16.<init>()
            r10 = r16
        L_0x0319:
            if (r12 != 0) goto L_0x0322
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r16 = new im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights
            r16.<init>()
            r12 = r16
        L_0x0322:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r11 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r11 = r11.prev_participant
            java.lang.String r11 = r11.rank
            r16 = r5
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r5 = r5.new_participant
            java.lang.String r5 = r5.rank
            boolean r5 = android.text.TextUtils.equals(r11, r5)
            if (r5 != 0) goto L_0x038b
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r5 = r5.new_participant
            java.lang.String r5 = r5.rank
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0360
            r5 = 10
            r9.append(r5)
            r11 = 45
            r9.append(r11)
            r11 = 32
            r9.append(r11)
            r11 = 2131691224(0x7f0f06d8, float:1.9011514E38)
            java.lang.String r5 = "EventLogPromotedRemovedTitle"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r11)
            r9.append(r5)
            r25 = r7
            goto L_0x038d
        L_0x0360:
            r5 = 10
            r9.append(r5)
            r5 = 43
            r9.append(r5)
            r11 = 32
            r9.append(r11)
            r5 = 1
            java.lang.Object[] r11 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r5 = r5.new_participant
            java.lang.String r5 = r5.rank
            r18 = 0
            r11[r18] = r5
            java.lang.String r5 = "EventLogPromotedTitle"
            r25 = r7
            r7 = 2131691225(0x7f0f06d9, float:1.9011516E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r7, r11)
            r9.append(r5)
            goto L_0x038d
        L_0x038b:
            r25 = r7
        L_0x038d:
            boolean r5 = r10.change_info
            boolean r7 = r12.change_info
            if (r5 == r7) goto L_0x03c3
            r5 = 10
            r9.append(r5)
            boolean r5 = r12.change_info
            if (r5 == 0) goto L_0x039f
            r5 = 43
            goto L_0x03a1
        L_0x039f:
            r5 = 45
        L_0x03a1:
            r9.append(r5)
            r5 = 32
            r9.append(r5)
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x03b7
            r5 = 2131691219(0x7f0f06d3, float:1.9011504E38)
            java.lang.String r7 = "EventLogPromotedChangeGroupInfo"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            goto L_0x03c0
        L_0x03b7:
            r5 = 2131691218(0x7f0f06d2, float:1.9011502E38)
            java.lang.String r7 = "EventLogPromotedChangeChannelInfo"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
        L_0x03c0:
            r9.append(r5)
        L_0x03c3:
            boolean r5 = r4.megagroup
            if (r5 != 0) goto L_0x0417
            boolean r5 = r10.post_messages
            boolean r7 = r12.post_messages
            if (r5 == r7) goto L_0x03ef
            r5 = 10
            r9.append(r5)
            boolean r5 = r12.post_messages
            if (r5 == 0) goto L_0x03d9
            r5 = 43
            goto L_0x03db
        L_0x03d9:
            r5 = 45
        L_0x03db:
            r9.append(r5)
            r5 = 32
            r9.append(r5)
            r5 = 2131691223(0x7f0f06d7, float:1.9011512E38)
            java.lang.String r7 = "EventLogPromotedPostMessages"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            r9.append(r5)
        L_0x03ef:
            boolean r5 = r10.edit_messages
            boolean r7 = r12.edit_messages
            if (r5 == r7) goto L_0x0417
            r5 = 10
            r9.append(r5)
            boolean r5 = r12.edit_messages
            if (r5 == 0) goto L_0x0401
            r5 = 43
            goto L_0x0403
        L_0x0401:
            r5 = 45
        L_0x0403:
            r9.append(r5)
            r5 = 32
            r9.append(r5)
            r5 = 2131691221(0x7f0f06d5, float:1.9011508E38)
            java.lang.String r7 = "EventLogPromotedEditMessages"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            r9.append(r5)
        L_0x0417:
            boolean r5 = r10.delete_messages
            boolean r7 = r12.delete_messages
            if (r5 == r7) goto L_0x043f
            r5 = 10
            r9.append(r5)
            boolean r5 = r12.delete_messages
            if (r5 == 0) goto L_0x0429
            r5 = 43
            goto L_0x042b
        L_0x0429:
            r5 = 45
        L_0x042b:
            r9.append(r5)
            r5 = 32
            r9.append(r5)
            r5 = 2131691220(0x7f0f06d4, float:1.9011506E38)
            java.lang.String r7 = "EventLogPromotedDeleteMessages"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            r9.append(r5)
        L_0x043f:
            boolean r5 = r10.add_admins
            boolean r7 = r12.add_admins
            if (r5 == r7) goto L_0x0467
            r5 = 10
            r9.append(r5)
            boolean r5 = r12.add_admins
            if (r5 == 0) goto L_0x0451
            r5 = 43
            goto L_0x0453
        L_0x0451:
            r5 = 45
        L_0x0453:
            r9.append(r5)
            r5 = 32
            r9.append(r5)
            r5 = 2131691215(0x7f0f06cf, float:1.9011496E38)
            java.lang.String r7 = "EventLogPromotedAddAdmins"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            r9.append(r5)
        L_0x0467:
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x0493
            boolean r5 = r10.ban_users
            boolean r7 = r12.ban_users
            if (r5 == r7) goto L_0x0493
            r5 = 10
            r9.append(r5)
            boolean r5 = r12.ban_users
            if (r5 == 0) goto L_0x047d
            r5 = 43
            goto L_0x047f
        L_0x047d:
            r5 = 45
        L_0x047f:
            r9.append(r5)
            r5 = 32
            r9.append(r5)
            r5 = 2131691217(0x7f0f06d1, float:1.90115E38)
            java.lang.String r7 = "EventLogPromotedBanUsers"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            r9.append(r5)
        L_0x0493:
            boolean r5 = r10.invite_users
            boolean r7 = r12.invite_users
            if (r5 == r7) goto L_0x04bb
            r5 = 10
            r9.append(r5)
            boolean r5 = r12.invite_users
            if (r5 == 0) goto L_0x04a5
            r5 = 43
            goto L_0x04a7
        L_0x04a5:
            r5 = 45
        L_0x04a7:
            r9.append(r5)
            r5 = 32
            r9.append(r5)
            r5 = 2131691216(0x7f0f06d0, float:1.9011498E38)
            java.lang.String r7 = "EventLogPromotedAddUsers"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            r9.append(r5)
        L_0x04bb:
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x04e7
            boolean r5 = r10.pin_messages
            boolean r7 = r12.pin_messages
            if (r5 == r7) goto L_0x04e7
            r5 = 10
            r9.append(r5)
            boolean r5 = r12.pin_messages
            if (r5 == 0) goto L_0x04d1
            r5 = 43
            goto L_0x04d3
        L_0x04d1:
            r5 = 45
        L_0x04d3:
            r9.append(r5)
            r5 = 32
            r9.append(r5)
            r5 = 2131691222(0x7f0f06d6, float:1.901151E38)
            java.lang.String r7 = "EventLogPromotedPinMessages"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            r9.append(r5)
        L_0x04e7:
            r7 = r9
        L_0x04e8:
            java.lang.String r5 = r7.toString()
            r0.messageText = r5
            r27 = r15
            goto L_0x1002
        L_0x04f2:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights
            if (r5 == 0) goto L_0x06c9
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights) r5
            im.bclpbkiauv.tgnet.TLRPC$TL_message r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_message
            r7.<init>()
            r0.messageOwner = r7
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r7 = r5.prev_banned_rights
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r8 = r5.new_banned_rights
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r10 = 2131691175(0x7f0f06a7, float:1.9011414E38)
            java.lang.String r11 = "EventLogDefaultPermissions"
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            r9.<init>(r10)
            r10 = 0
            if (r7 != 0) goto L_0x051e
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r11 = new im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights
            r11.<init>()
            r7 = r11
        L_0x051e:
            if (r8 != 0) goto L_0x0526
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r11 = new im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights
            r11.<init>()
            r8 = r11
        L_0x0526:
            boolean r11 = r7.send_messages
            boolean r12 = r8.send_messages
            if (r11 == r12) goto L_0x0557
            if (r10 != 0) goto L_0x0535
            r11 = 10
            r9.append(r11)
            r10 = 1
            goto L_0x0537
        L_0x0535:
            r11 = 10
        L_0x0537:
            r9.append(r11)
            boolean r11 = r8.send_messages
            if (r11 != 0) goto L_0x0541
            r11 = 43
            goto L_0x0543
        L_0x0541:
            r11 = 45
        L_0x0543:
            r9.append(r11)
            r11 = 32
            r9.append(r11)
            r11 = 2131691240(0x7f0f06e8, float:1.9011546E38)
            java.lang.String r12 = "EventLogRestrictedSendMessages"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r9.append(r11)
        L_0x0557:
            boolean r11 = r7.send_stickers
            boolean r12 = r8.send_stickers
            if (r11 != r12) goto L_0x056f
            boolean r11 = r7.send_inline
            boolean r12 = r8.send_inline
            if (r11 != r12) goto L_0x056f
            boolean r11 = r7.send_gifs
            boolean r12 = r8.send_gifs
            if (r11 != r12) goto L_0x056f
            boolean r11 = r7.send_games
            boolean r12 = r8.send_games
            if (r11 == r12) goto L_0x059a
        L_0x056f:
            if (r10 != 0) goto L_0x0578
            r11 = 10
            r9.append(r11)
            r10 = 1
            goto L_0x057a
        L_0x0578:
            r11 = 10
        L_0x057a:
            r9.append(r11)
            boolean r11 = r8.send_stickers
            if (r11 != 0) goto L_0x0584
            r11 = 43
            goto L_0x0586
        L_0x0584:
            r11 = 45
        L_0x0586:
            r9.append(r11)
            r11 = 32
            r9.append(r11)
            r11 = 2131691242(0x7f0f06ea, float:1.901155E38)
            java.lang.String r12 = "EventLogRestrictedSendStickers"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r9.append(r11)
        L_0x059a:
            boolean r11 = r7.send_media
            boolean r12 = r8.send_media
            if (r11 == r12) goto L_0x05cb
            if (r10 != 0) goto L_0x05a9
            r11 = 10
            r9.append(r11)
            r10 = 1
            goto L_0x05ab
        L_0x05a9:
            r11 = 10
        L_0x05ab:
            r9.append(r11)
            boolean r11 = r8.send_media
            if (r11 != 0) goto L_0x05b5
            r11 = 43
            goto L_0x05b7
        L_0x05b5:
            r11 = 45
        L_0x05b7:
            r9.append(r11)
            r11 = 32
            r9.append(r11)
            r11 = 2131691239(0x7f0f06e7, float:1.9011544E38)
            java.lang.String r12 = "EventLogRestrictedSendMedia"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r9.append(r11)
        L_0x05cb:
            boolean r11 = r7.send_polls
            boolean r12 = r8.send_polls
            if (r11 == r12) goto L_0x05fc
            if (r10 != 0) goto L_0x05da
            r11 = 10
            r9.append(r11)
            r10 = 1
            goto L_0x05dc
        L_0x05da:
            r11 = 10
        L_0x05dc:
            r9.append(r11)
            boolean r11 = r8.send_polls
            if (r11 != 0) goto L_0x05e6
            r11 = 43
            goto L_0x05e8
        L_0x05e6:
            r11 = 45
        L_0x05e8:
            r9.append(r11)
            r11 = 32
            r9.append(r11)
            r11 = 2131691241(0x7f0f06e9, float:1.9011548E38)
            java.lang.String r12 = "EventLogRestrictedSendPolls"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r9.append(r11)
        L_0x05fc:
            boolean r11 = r7.embed_links
            boolean r12 = r8.embed_links
            if (r11 == r12) goto L_0x062d
            if (r10 != 0) goto L_0x060b
            r11 = 10
            r9.append(r11)
            r10 = 1
            goto L_0x060d
        L_0x060b:
            r11 = 10
        L_0x060d:
            r9.append(r11)
            boolean r11 = r8.embed_links
            if (r11 != 0) goto L_0x0617
            r11 = 43
            goto L_0x0619
        L_0x0617:
            r11 = 45
        L_0x0619:
            r9.append(r11)
            r11 = 32
            r9.append(r11)
            r11 = 2131691238(0x7f0f06e6, float:1.9011542E38)
            java.lang.String r12 = "EventLogRestrictedSendEmbed"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r9.append(r11)
        L_0x062d:
            boolean r11 = r7.change_info
            boolean r12 = r8.change_info
            if (r11 == r12) goto L_0x065e
            if (r10 != 0) goto L_0x063c
            r11 = 10
            r9.append(r11)
            r10 = 1
            goto L_0x063e
        L_0x063c:
            r11 = 10
        L_0x063e:
            r9.append(r11)
            boolean r11 = r8.change_info
            if (r11 != 0) goto L_0x0648
            r11 = 43
            goto L_0x064a
        L_0x0648:
            r11 = 45
        L_0x064a:
            r9.append(r11)
            r11 = 32
            r9.append(r11)
            r11 = 2131691234(0x7f0f06e2, float:1.9011534E38)
            java.lang.String r12 = "EventLogRestrictedChangeInfo"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r9.append(r11)
        L_0x065e:
            boolean r11 = r7.invite_users
            boolean r12 = r8.invite_users
            if (r11 == r12) goto L_0x068f
            if (r10 != 0) goto L_0x066d
            r11 = 10
            r9.append(r11)
            r10 = 1
            goto L_0x066f
        L_0x066d:
            r11 = 10
        L_0x066f:
            r9.append(r11)
            boolean r11 = r8.invite_users
            if (r11 != 0) goto L_0x0679
            r11 = 43
            goto L_0x067b
        L_0x0679:
            r11 = 45
        L_0x067b:
            r9.append(r11)
            r11 = 32
            r9.append(r11)
            r11 = 2131691235(0x7f0f06e3, float:1.9011536E38)
            java.lang.String r12 = "EventLogRestrictedInviteUsers"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r9.append(r11)
        L_0x068f:
            boolean r11 = r7.pin_messages
            boolean r12 = r8.pin_messages
            if (r11 == r12) goto L_0x06bf
            if (r10 != 0) goto L_0x069d
            r11 = 10
            r9.append(r11)
            goto L_0x069f
        L_0x069d:
            r11 = 10
        L_0x069f:
            r9.append(r11)
            boolean r11 = r8.pin_messages
            if (r11 != 0) goto L_0x06a9
            r11 = 43
            goto L_0x06ab
        L_0x06a9:
            r11 = 45
        L_0x06ab:
            r9.append(r11)
            r11 = 32
            r9.append(r11)
            r11 = 2131691236(0x7f0f06e4, float:1.9011538E38)
            java.lang.String r12 = "EventLogRestrictedPinMessages"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r9.append(r11)
        L_0x06bf:
            java.lang.String r11 = r9.toString()
            r0.messageText = r11
            r27 = r15
            goto L_0x1002
        L_0x06c9:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan
            if (r5 == 0) goto L_0x0a0d
            im.bclpbkiauv.tgnet.TLRPC$TL_message r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_message
            r5.<init>()
            r0.messageOwner = r5
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r7 = r7.prev_participant
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r7)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r7 = r7.prev_participant
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r7 = r7.banned_rights
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r9 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r9 = r9.new_participant
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r9 = r9.banned_rights
            boolean r11 = r4.megagroup
            if (r11 == 0) goto L_0x09d6
            if (r9 == 0) goto L_0x070f
            boolean r11 = r9.view_messages
            if (r11 == 0) goto L_0x070f
            if (r9 == 0) goto L_0x070b
            if (r7 == 0) goto L_0x070b
            int r11 = r9.until_date
            int r12 = r7.until_date
            if (r11 == r12) goto L_0x070b
            goto L_0x070f
        L_0x070b:
            r27 = r15
            goto L_0x09d8
        L_0x070f:
            if (r9 == 0) goto L_0x07b8
            boolean r11 = im.bclpbkiauv.messenger.AndroidUtilities.isBannedForever(r9)
            if (r11 != 0) goto L_0x07b8
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            int r12 = r9.until_date
            int r10 = r1.date
            int r12 = r12 - r10
            int r10 = r12 / 60
            r25 = 60
            int r10 = r10 / 60
            int r10 = r10 / 24
            int r26 = r10 * 60
            int r26 = r26 * 60
            int r26 = r26 * 24
            int r12 = r12 - r26
            int r26 = r12 / 60
            r27 = r15
            int r15 = r26 / 60
            int r26 = r15 * 60
            int r26 = r26 * 60
            int r12 = r12 - r26
            int r2 = r12 / 60
            r25 = 0
            r26 = 0
            r31 = r26
            r26 = r12
            r12 = r31
        L_0x0749:
            r3 = 3
            if (r12 >= r3) goto L_0x07b3
            r16 = 0
            if (r12 != 0) goto L_0x0761
            if (r10 == 0) goto L_0x0786
            java.lang.String r3 = "Days"
            java.lang.String r16 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r3, r10)
            int r25 = r25 + 1
            r3 = r16
            r16 = r2
            r2 = r25
            goto L_0x078c
        L_0x0761:
            r3 = 1
            if (r12 != r3) goto L_0x0775
            if (r15 == 0) goto L_0x0786
            java.lang.String r3 = "Hours"
            java.lang.String r16 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r3, r15)
            int r25 = r25 + 1
            r3 = r16
            r16 = r2
            r2 = r25
            goto L_0x078c
        L_0x0775:
            if (r2 == 0) goto L_0x0786
            java.lang.String r3 = "Minutes"
            java.lang.String r16 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r3, r2)
            int r25 = r25 + 1
            r3 = r16
            r16 = r2
            r2 = r25
            goto L_0x078c
        L_0x0786:
            r3 = r16
            r16 = r2
            r2 = r25
        L_0x078c:
            if (r3 == 0) goto L_0x07a2
            int r25 = r11.length()
            if (r25 <= 0) goto L_0x079c
            r29 = r10
            java.lang.String r10 = ", "
            r11.append(r10)
            goto L_0x079e
        L_0x079c:
            r29 = r10
        L_0x079e:
            r11.append(r3)
            goto L_0x07a4
        L_0x07a2:
            r29 = r10
        L_0x07a4:
            r10 = 2
            if (r2 != r10) goto L_0x07a8
            goto L_0x07b7
        L_0x07a8:
            int r12 = r12 + 1
            r3 = r36
            r25 = r2
            r2 = r16
            r10 = r29
            goto L_0x0749
        L_0x07b3:
            r16 = r2
            r29 = r10
        L_0x07b7:
            goto L_0x07c9
        L_0x07b8:
            r27 = r15
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = 2131694603(0x7f0f140b, float:1.9018367E38)
            java.lang.String r10 = "UserRestrictionsUntilForever"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r3)
            r2.<init>(r3)
            r11 = r2
        L_0x07c9:
            r2 = 2131691243(0x7f0f06eb, float:1.9011552E38)
            java.lang.String r3 = "EventLogRestrictedUntil"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            int r3 = r2.indexOf(r8)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]
            im.bclpbkiauv.tgnet.TLRPC$Message r12 = r0.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r12 = r12.entities
            java.lang.String r12 = r0.getUserName(r5, r12, r3)
            r15 = 0
            r10[r15] = r12
            java.lang.String r12 = r11.toString()
            r15 = 1
            r10[r15] = r12
            java.lang.String r10 = java.lang.String.format(r2, r10)
            r8.<init>(r10)
            r10 = 0
            if (r7 != 0) goto L_0x07fd
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights
            r12.<init>()
            r7 = r12
        L_0x07fd:
            if (r9 != 0) goto L_0x0805
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights
            r12.<init>()
            r9 = r12
        L_0x0805:
            boolean r12 = r7.view_messages
            boolean r15 = r9.view_messages
            if (r12 == r15) goto L_0x0836
            if (r10 != 0) goto L_0x0814
            r12 = 10
            r8.append(r12)
            r10 = 1
            goto L_0x0816
        L_0x0814:
            r12 = 10
        L_0x0816:
            r8.append(r12)
            boolean r12 = r9.view_messages
            if (r12 != 0) goto L_0x0820
            r12 = 43
            goto L_0x0822
        L_0x0820:
            r12 = 45
        L_0x0822:
            r8.append(r12)
            r12 = 32
            r8.append(r12)
            r12 = 2131691237(0x7f0f06e5, float:1.901154E38)
            java.lang.String r15 = "EventLogRestrictedReadMessages"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r12)
            r8.append(r12)
        L_0x0836:
            boolean r12 = r7.send_messages
            boolean r15 = r9.send_messages
            if (r12 == r15) goto L_0x0867
            if (r10 != 0) goto L_0x0845
            r12 = 10
            r8.append(r12)
            r10 = 1
            goto L_0x0847
        L_0x0845:
            r12 = 10
        L_0x0847:
            r8.append(r12)
            boolean r12 = r9.send_messages
            if (r12 != 0) goto L_0x0851
            r12 = 43
            goto L_0x0853
        L_0x0851:
            r12 = 45
        L_0x0853:
            r8.append(r12)
            r12 = 32
            r8.append(r12)
            r12 = 2131691240(0x7f0f06e8, float:1.9011546E38)
            java.lang.String r15 = "EventLogRestrictedSendMessages"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r12)
            r8.append(r12)
        L_0x0867:
            boolean r12 = r7.send_stickers
            boolean r15 = r9.send_stickers
            if (r12 != r15) goto L_0x087f
            boolean r12 = r7.send_inline
            boolean r15 = r9.send_inline
            if (r12 != r15) goto L_0x087f
            boolean r12 = r7.send_gifs
            boolean r15 = r9.send_gifs
            if (r12 != r15) goto L_0x087f
            boolean r12 = r7.send_games
            boolean r15 = r9.send_games
            if (r12 == r15) goto L_0x08aa
        L_0x087f:
            if (r10 != 0) goto L_0x0888
            r12 = 10
            r8.append(r12)
            r10 = 1
            goto L_0x088a
        L_0x0888:
            r12 = 10
        L_0x088a:
            r8.append(r12)
            boolean r12 = r9.send_stickers
            if (r12 != 0) goto L_0x0894
            r12 = 43
            goto L_0x0896
        L_0x0894:
            r12 = 45
        L_0x0896:
            r8.append(r12)
            r12 = 32
            r8.append(r12)
            r12 = 2131691242(0x7f0f06ea, float:1.901155E38)
            java.lang.String r15 = "EventLogRestrictedSendStickers"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r12)
            r8.append(r12)
        L_0x08aa:
            boolean r12 = r7.send_media
            boolean r15 = r9.send_media
            if (r12 == r15) goto L_0x08db
            if (r10 != 0) goto L_0x08b9
            r12 = 10
            r8.append(r12)
            r10 = 1
            goto L_0x08bb
        L_0x08b9:
            r12 = 10
        L_0x08bb:
            r8.append(r12)
            boolean r12 = r9.send_media
            if (r12 != 0) goto L_0x08c5
            r12 = 43
            goto L_0x08c7
        L_0x08c5:
            r12 = 45
        L_0x08c7:
            r8.append(r12)
            r12 = 32
            r8.append(r12)
            r12 = 2131691239(0x7f0f06e7, float:1.9011544E38)
            java.lang.String r15 = "EventLogRestrictedSendMedia"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r12)
            r8.append(r12)
        L_0x08db:
            boolean r12 = r7.send_polls
            boolean r15 = r9.send_polls
            if (r12 == r15) goto L_0x090c
            if (r10 != 0) goto L_0x08ea
            r12 = 10
            r8.append(r12)
            r10 = 1
            goto L_0x08ec
        L_0x08ea:
            r12 = 10
        L_0x08ec:
            r8.append(r12)
            boolean r12 = r9.send_polls
            if (r12 != 0) goto L_0x08f6
            r12 = 43
            goto L_0x08f8
        L_0x08f6:
            r12 = 45
        L_0x08f8:
            r8.append(r12)
            r12 = 32
            r8.append(r12)
            r12 = 2131691241(0x7f0f06e9, float:1.9011548E38)
            java.lang.String r15 = "EventLogRestrictedSendPolls"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r12)
            r8.append(r12)
        L_0x090c:
            boolean r12 = r7.embed_links
            boolean r15 = r9.embed_links
            if (r12 == r15) goto L_0x093d
            if (r10 != 0) goto L_0x091b
            r12 = 10
            r8.append(r12)
            r10 = 1
            goto L_0x091d
        L_0x091b:
            r12 = 10
        L_0x091d:
            r8.append(r12)
            boolean r12 = r9.embed_links
            if (r12 != 0) goto L_0x0927
            r12 = 43
            goto L_0x0929
        L_0x0927:
            r12 = 45
        L_0x0929:
            r8.append(r12)
            r12 = 32
            r8.append(r12)
            r12 = 2131691238(0x7f0f06e6, float:1.9011542E38)
            java.lang.String r15 = "EventLogRestrictedSendEmbed"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r12)
            r8.append(r12)
        L_0x093d:
            boolean r12 = r7.change_info
            boolean r15 = r9.change_info
            if (r12 == r15) goto L_0x096e
            if (r10 != 0) goto L_0x094c
            r12 = 10
            r8.append(r12)
            r10 = 1
            goto L_0x094e
        L_0x094c:
            r12 = 10
        L_0x094e:
            r8.append(r12)
            boolean r12 = r9.change_info
            if (r12 != 0) goto L_0x0958
            r12 = 43
            goto L_0x095a
        L_0x0958:
            r12 = 45
        L_0x095a:
            r8.append(r12)
            r12 = 32
            r8.append(r12)
            r12 = 2131691234(0x7f0f06e2, float:1.9011534E38)
            java.lang.String r15 = "EventLogRestrictedChangeInfo"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r12)
            r8.append(r12)
        L_0x096e:
            boolean r12 = r7.invite_users
            boolean r15 = r9.invite_users
            if (r12 == r15) goto L_0x099f
            if (r10 != 0) goto L_0x097d
            r12 = 10
            r8.append(r12)
            r10 = 1
            goto L_0x097f
        L_0x097d:
            r12 = 10
        L_0x097f:
            r8.append(r12)
            boolean r12 = r9.invite_users
            if (r12 != 0) goto L_0x0989
            r12 = 43
            goto L_0x098b
        L_0x0989:
            r12 = 45
        L_0x098b:
            r8.append(r12)
            r12 = 32
            r8.append(r12)
            r12 = 2131691235(0x7f0f06e3, float:1.9011536E38)
            java.lang.String r15 = "EventLogRestrictedInviteUsers"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r12)
            r8.append(r12)
        L_0x099f:
            boolean r12 = r7.pin_messages
            boolean r15 = r9.pin_messages
            if (r12 == r15) goto L_0x09cf
            if (r10 != 0) goto L_0x09ad
            r12 = 10
            r8.append(r12)
            goto L_0x09af
        L_0x09ad:
            r12 = 10
        L_0x09af:
            r8.append(r12)
            boolean r12 = r9.pin_messages
            if (r12 != 0) goto L_0x09b9
            r12 = 43
            goto L_0x09bb
        L_0x09b9:
            r12 = 45
        L_0x09bb:
            r8.append(r12)
            r12 = 32
            r8.append(r12)
            r12 = 2131691236(0x7f0f06e4, float:1.9011538E38)
            java.lang.String r15 = "EventLogRestrictedPinMessages"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r12)
            r8.append(r12)
        L_0x09cf:
            java.lang.String r12 = r8.toString()
            r0.messageText = r12
            goto L_0x0a0b
        L_0x09d6:
            r27 = r15
        L_0x09d8:
            if (r9 == 0) goto L_0x09ea
            if (r7 == 0) goto L_0x09e0
            boolean r2 = r9.view_messages
            if (r2 == 0) goto L_0x09ea
        L_0x09e0:
            r2 = 2131691173(0x7f0f06a5, float:1.901141E38)
            java.lang.String r3 = "EventLogChannelRestricted"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x09f3
        L_0x09ea:
            r2 = 2131691174(0x7f0f06a6, float:1.9011412E38)
            java.lang.String r3 = "EventLogChannelUnrestricted"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
        L_0x09f3:
            int r3 = r2.indexOf(r8)
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r0.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r8 = r8.entities
            java.lang.String r8 = r0.getUserName(r5, r8, r3)
            r11 = 0
            r10[r11] = r8
            java.lang.String r8 = java.lang.String.format(r2, r10)
            r0.messageText = r8
        L_0x0a0b:
            goto L_0x1002
        L_0x0a0d:
            r27 = r15
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionUpdatePinned
            if (r2 == 0) goto L_0x0a85
            r2 = 2131691254(0x7f0f06f6, float:1.9011575E38)
            java.lang.String r3 = "EventLogUnpinnedMessages"
            r5 = 2131691210(0x7f0f06ca, float:1.9011485E38)
            java.lang.String r7 = "EventLogPinnedMessages"
            if (r6 == 0) goto L_0x0a65
            int r8 = r6.id
            r10 = 136817688(0x827ac18, float:5.045703E-34)
            if (r8 != r10) goto L_0x0a65
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.message
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r8 = r8.fwd_from
            if (r8 == 0) goto L_0x0a65
            int r8 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r8 = im.bclpbkiauv.messenger.MessagesController.getInstance(r8)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r10 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$Message r10 = r10.message
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r10 = r10.fwd_from
            int r10 = r10.channel_id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            im.bclpbkiauv.tgnet.TLRPC$Chat r8 = r8.getChat(r10)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r10 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$Message r10 = r10.message
            boolean r10 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageEmpty
            if (r10 == 0) goto L_0x0a59
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r8)
            r0.messageText = r2
            goto L_0x0a63
        L_0x0a59:
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r8)
            r0.messageText = r2
        L_0x0a63:
            goto L_0x1002
        L_0x0a65:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.message
            boolean r8 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageEmpty
            if (r8 == 0) goto L_0x0a79
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0a79:
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0a85:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionStopPoll
            if (r2 == 0) goto L_0x0a9c
            r2 = 2131691245(0x7f0f06ed, float:1.9011556E38)
            java.lang.String r3 = "EventLogStopPoll"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0a9c:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures
            if (r2 == 0) goto L_0x0acc
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures) r2
            boolean r2 = r2.new_value
            if (r2 == 0) goto L_0x0abb
            r2 = 2131691251(0x7f0f06f3, float:1.9011569E38)
            java.lang.String r3 = "EventLogToggledSignaturesOn"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0abb:
            r2 = 2131691250(0x7f0f06f2, float:1.9011567E38)
            java.lang.String r3 = "EventLogToggledSignaturesOff"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0acc:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites
            if (r2 == 0) goto L_0x0afc
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites) r2
            boolean r2 = r2.new_value
            if (r2 == 0) goto L_0x0aeb
            r2 = 2131691249(0x7f0f06f1, float:1.9011565E38)
            java.lang.String r3 = "EventLogToggledInvitesOn"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0aeb:
            r2 = 2131691248(0x7f0f06f0, float:1.9011562E38)
            java.lang.String r3 = "EventLogToggledInvitesOff"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0afc:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionDeleteMessage
            if (r2 == 0) goto L_0x0b13
            r2 = 2131691176(0x7f0f06a8, float:1.9011416E38)
            java.lang.String r3 = "EventLogDeletedMessages"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0b13:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat
            if (r2 == 0) goto L_0x0bbe
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r2
            int r2 = r2.new_value
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r3 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r3
            int r3 = r3.prev_value
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x0b73
            if (r2 != 0) goto L_0x0b4f
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r3)
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r5.getChat(r8)
            r8 = 2131691229(0x7f0f06dd, float:1.9011524E38)
            java.lang.String r10 = "EventLogRemovedLinkedChannel"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r8)
            java.lang.CharSequence r8 = r0.replaceWithLink(r8, r9, r6)
            r0.messageText = r8
            java.lang.CharSequence r7 = r0.replaceWithLink(r8, r7, r5)
            r0.messageText = r7
            goto L_0x0bbc
        L_0x0b4f:
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r5.getChat(r8)
            r8 = 2131691167(0x7f0f069f, float:1.9011398E38)
            java.lang.String r10 = "EventLogChangedLinkedChannel"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r8)
            java.lang.CharSequence r8 = r0.replaceWithLink(r8, r9, r6)
            r0.messageText = r8
            java.lang.CharSequence r7 = r0.replaceWithLink(r8, r7, r5)
            r0.messageText = r7
            goto L_0x0bbc
        L_0x0b73:
            if (r2 != 0) goto L_0x0b99
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r3)
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r5.getChat(r8)
            r8 = 2131691230(0x7f0f06de, float:1.9011526E38)
            java.lang.String r10 = "EventLogRemovedLinkedGroup"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r8)
            java.lang.CharSequence r8 = r0.replaceWithLink(r8, r9, r6)
            r0.messageText = r8
            java.lang.CharSequence r7 = r0.replaceWithLink(r8, r7, r5)
            r0.messageText = r7
            goto L_0x0bbc
        L_0x0b99:
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r5.getChat(r8)
            r8 = 2131691168(0x7f0f06a0, float:1.90114E38)
            java.lang.String r10 = "EventLogChangedLinkedGroup"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r8)
            java.lang.CharSequence r8 = r0.replaceWithLink(r8, r9, r6)
            r0.messageText = r8
            java.lang.CharSequence r7 = r0.replaceWithLink(r8, r7, r5)
            r0.messageText = r7
        L_0x0bbc:
            goto L_0x1002
        L_0x0bbe:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden
            if (r2 == 0) goto L_0x0bee
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden) r2
            boolean r2 = r2.new_value
            if (r2 == 0) goto L_0x0bdd
            r2 = 2131691246(0x7f0f06ee, float:1.9011558E38)
            java.lang.String r3 = "EventLogToggledInvitesHistoryOff"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0bdd:
            r2 = 2131691247(0x7f0f06ef, float:1.901156E38)
            java.lang.String r3 = "EventLogToggledInvitesHistoryOn"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            goto L_0x1002
        L_0x0bee:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout
            if (r2 == 0) goto L_0x0c85
            boolean r2 = r4.megagroup
            if (r2 == 0) goto L_0x0c02
            r2 = 2131691181(0x7f0f06ad, float:1.9011427E38)
            java.lang.String r3 = "EventLogEditedGroupDescription"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c0b
        L_0x0c02:
            r2 = 2131691178(0x7f0f06aa, float:1.901142E38)
            java.lang.String r3 = "EventLogEditedChannelDescription"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
        L_0x0c0b:
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r9, r6)
            r0.messageText = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_message r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_message
            r2.<init>()
            r15 = r2
            r2 = 0
            r15.out = r2
            r15.unread = r2
            int r2 = r1.user_id
            r15.from_id = r2
            r15.to_id = r14
            int r2 = r1.date
            r15.date = r2
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r2
            java.lang.String r2 = r2.new_value
            r15.message = r2
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r2
            java.lang.String r2 = r2.prev_value
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0c7c
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaWebPage r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaWebPage
            r2.<init>()
            r15.media = r2
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$TL_webPage r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_webPage
            r3.<init>()
            r2.webpage = r3
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r2 = r2.webpage
            r3 = 10
            r2.flags = r3
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r2 = r2.webpage
            r2.display_url = r13
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r2 = r2.webpage
            r2.url = r13
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r2 = r2.webpage
            r3 = 2131691211(0x7f0f06cb, float:1.9011487E38)
            java.lang.String r5 = "EventLogPreviousGroupDescription"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r2.site_name = r3
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r2 = r2.webpage
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r3 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r3
            java.lang.String r3 = r3.prev_value
            r2.description = r3
            goto L_0x1004
        L_0x0c7c:
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty
            r2.<init>()
            r15.media = r2
            goto L_0x1004
        L_0x0c85:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername
            if (r2 == 0) goto L_0x0da0
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r2
            java.lang.String r2 = r2.new_value
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0cb6
            boolean r3 = r4.megagroup
            if (r3 == 0) goto L_0x0ca5
            r3 = 2131691166(0x7f0f069e, float:1.9011396E38)
            java.lang.String r5 = "EventLogChangedGroupLink"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            goto L_0x0cae
        L_0x0ca5:
            r3 = 2131691165(0x7f0f069d, float:1.9011394E38)
            java.lang.String r5 = "EventLogChangedChannelLink"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
        L_0x0cae:
            java.lang.CharSequence r3 = r0.replaceWithLink(r3, r9, r6)
            r0.messageText = r3
            goto L_0x0cd4
        L_0x0cb6:
            boolean r3 = r4.megagroup
            if (r3 == 0) goto L_0x0cc4
            r3 = 2131691228(0x7f0f06dc, float:1.9011522E38)
            java.lang.String r5 = "EventLogRemovedGroupLink"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            goto L_0x0ccd
        L_0x0cc4:
            r3 = 2131691226(0x7f0f06da, float:1.9011518E38)
            java.lang.String r5 = "EventLogRemovedChannelLink"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
        L_0x0ccd:
            java.lang.CharSequence r3 = r0.replaceWithLink(r3, r9, r6)
            r0.messageText = r3
        L_0x0cd4:
            im.bclpbkiauv.tgnet.TLRPC$TL_message r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_message
            r3.<init>()
            r15 = r3
            r3 = 0
            r15.out = r3
            r15.unread = r3
            int r3 = r1.user_id
            r15.from_id = r3
            r15.to_id = r14
            int r3 = r1.date
            r15.date = r3
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0d13
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "https://"
            r3.append(r5)
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            java.lang.String r5 = r5.linkPrefix
            r3.append(r5)
            java.lang.String r5 = "/"
            r3.append(r5)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            r15.message = r3
            goto L_0x0d15
        L_0x0d13:
            r15.message = r13
        L_0x0d15:
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityUrl r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityUrl
            r3.<init>()
            r5 = 0
            r3.offset = r5
            java.lang.String r5 = r15.message
            int r5 = r5.length()
            r3.length = r5
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r5 = r15.entities
            r5.add(r3)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r5
            java.lang.String r5 = r5.prev_value
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0d97
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaWebPage r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaWebPage
            r5.<init>()
            r15.media = r5
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$TL_webPage r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_webPage
            r7.<init>()
            r5.webpage = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            r7 = 10
            r5.flags = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            r5.display_url = r13
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            r5.url = r13
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            r7 = 2131691212(0x7f0f06cc, float:1.901149E38)
            java.lang.String r8 = "EventLogPreviousLink"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r5.site_name = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "https://"
            r7.append(r8)
            int r8 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r8 = im.bclpbkiauv.messenger.MessagesController.getInstance(r8)
            java.lang.String r8 = r8.linkPrefix
            r7.append(r8)
            java.lang.String r8 = "/"
            r7.append(r8)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r8
            java.lang.String r8 = r8.prev_value
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r5.description = r7
            goto L_0x0d9e
        L_0x0d97:
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty
            r5.<init>()
            r15.media = r5
        L_0x0d9e:
            goto L_0x1004
        L_0x0da0:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage
            if (r2 == 0) goto L_0x0f14
            im.bclpbkiauv.tgnet.TLRPC$TL_message r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_message
            r2.<init>()
            r15 = r2
            r2 = 0
            r15.out = r2
            r15.unread = r2
            int r2 = r1.user_id
            r15.from_id = r2
            r15.to_id = r14
            int r2 = r1.date
            r15.date = r2
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage) r2
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r2.new_message
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r3 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage) r3
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.prev_message
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r2.media
            if (r5 == 0) goto L_0x0ea2
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r2.media
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaEmpty
            if (r5 != 0) goto L_0x0ea2
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r2.media
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaWebPage
            if (r5 != 0) goto L_0x0ea2
            java.lang.String r5 = r2.message
            java.lang.String r7 = r3.message
            boolean r5 = android.text.TextUtils.equals(r5, r7)
            if (r5 != 0) goto L_0x0de3
            r5 = 1
            goto L_0x0de4
        L_0x0de3:
            r5 = 0
        L_0x0de4:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r7 = r2.media
            java.lang.Class r7 = r7.getClass()
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r3.media
            java.lang.Class r8 = r8.getClass()
            if (r7 != r8) goto L_0x0e2d
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r7 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$Photo r7 = r7.photo
            if (r7 == 0) goto L_0x0e0e
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r7 = r3.media
            im.bclpbkiauv.tgnet.TLRPC$Photo r7 = r7.photo
            if (r7 == 0) goto L_0x0e0e
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r7 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$Photo r7 = r7.photo
            long r7 = r7.id
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r10 = r3.media
            im.bclpbkiauv.tgnet.TLRPC$Photo r10 = r10.photo
            long r10 = r10.id
            int r12 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x0e2d
        L_0x0e0e:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r7 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r7.document
            if (r7 == 0) goto L_0x0e2b
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r7 = r3.media
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r7.document
            if (r7 == 0) goto L_0x0e2b
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r7 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r7.document
            long r7 = r7.id
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r10 = r3.media
            im.bclpbkiauv.tgnet.TLRPC$Document r10 = r10.document
            long r10 = r10.id
            int r12 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x0e2b
            goto L_0x0e2d
        L_0x0e2b:
            r7 = 0
            goto L_0x0e2e
        L_0x0e2d:
            r7 = 1
        L_0x0e2e:
            if (r7 == 0) goto L_0x0e42
            if (r5 == 0) goto L_0x0e42
            r8 = 2131691185(0x7f0f06b1, float:1.9011435E38)
            java.lang.String r10 = "EventLogEditedMediaCaption"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r8)
            java.lang.CharSequence r8 = r0.replaceWithLink(r8, r9, r6)
            r0.messageText = r8
            goto L_0x0e63
        L_0x0e42:
            if (r5 == 0) goto L_0x0e54
            r8 = 2131691177(0x7f0f06a9, float:1.9011418E38)
            java.lang.String r10 = "EventLogEditedCaption"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r8)
            java.lang.CharSequence r8 = r0.replaceWithLink(r8, r9, r6)
            r0.messageText = r8
            goto L_0x0e63
        L_0x0e54:
            r8 = 2131691184(0x7f0f06b0, float:1.9011433E38)
            java.lang.String r10 = "EventLogEditedMedia"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r8)
            java.lang.CharSequence r8 = r0.replaceWithLink(r8, r9, r6)
            r0.messageText = r8
        L_0x0e63:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r2.media
            r15.media = r8
            if (r5 == 0) goto L_0x0ea1
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$TL_webPage r9 = new im.bclpbkiauv.tgnet.TLRPC$TL_webPage
            r9.<init>()
            r8.webpage = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r8 = r8.webpage
            r9 = 2131691207(0x7f0f06c7, float:1.901148E38)
            java.lang.String r10 = "EventLogOriginalCaption"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            r8.site_name = r9
            java.lang.String r8 = r3.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0e99
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r8 = r8.webpage
            r9 = 2131691208(0x7f0f06c8, float:1.9011481E38)
            java.lang.String r10 = "EventLogOriginalCaptionEmpty"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            r8.description = r9
            goto L_0x0ea1
        L_0x0e99:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r8 = r8.webpage
            java.lang.String r9 = r3.message
            r8.description = r9
        L_0x0ea1:
            goto L_0x0ef4
        L_0x0ea2:
            r5 = 2131691186(0x7f0f06b2, float:1.9011437E38)
            java.lang.String r7 = "EventLogEditedMessages"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            java.lang.String r5 = r2.message
            r15.message = r5
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaWebPage r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaWebPage
            r5.<init>()
            r15.media = r5
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$TL_webPage r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_webPage
            r7.<init>()
            r5.webpage = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            r7 = 2131691209(0x7f0f06c9, float:1.9011483E38)
            java.lang.String r8 = "EventLogOriginalMessages"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r5.site_name = r7
            java.lang.String r5 = r3.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0eec
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            r7 = 2131691208(0x7f0f06c8, float:1.9011481E38)
            java.lang.String r8 = "EventLogOriginalCaptionEmpty"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r5.description = r7
            goto L_0x0ef4
        L_0x0eec:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            java.lang.String r7 = r3.message
            r5.description = r7
        L_0x0ef4:
            im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup r5 = r2.reply_markup
            r15.reply_markup = r5
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            if (r5 == 0) goto L_0x0f12
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            r7 = 10
            r5.flags = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            r5.display_url = r13
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r15.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r5 = r5.webpage
            r5.url = r13
        L_0x0f12:
            goto L_0x1004
        L_0x0f14:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet
            if (r2 == 0) goto L_0x0f4e
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r2
            im.bclpbkiauv.tgnet.TLRPC$InputStickerSet r2 = r2.new_stickerset
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r3 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r3
            im.bclpbkiauv.tgnet.TLRPC$InputStickerSet r3 = r3.new_stickerset
            if (r2 == 0) goto L_0x0f3d
            boolean r5 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputStickerSetEmpty
            if (r5 == 0) goto L_0x0f2d
            goto L_0x0f3d
        L_0x0f2d:
            r5 = 2131691171(0x7f0f06a3, float:1.9011406E38)
            java.lang.String r7 = "EventLogChangedStickersSet"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
            goto L_0x0f4c
        L_0x0f3d:
            r5 = 2131691232(0x7f0f06e0, float:1.901153E38)
            java.lang.String r7 = "EventLogRemovedStickersSet"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
        L_0x0f4c:
            goto L_0x1002
        L_0x0f4e:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation
            if (r2 == 0) goto L_0x0f8b
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation) r2
            im.bclpbkiauv.tgnet.TLRPC$ChannelLocation r3 = r2.new_value
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelLocationEmpty
            if (r3 == 0) goto L_0x0f6e
            r3 = 2131691231(0x7f0f06df, float:1.9011528E38)
            java.lang.String r5 = "EventLogRemovedLocation"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            java.lang.CharSequence r3 = r0.replaceWithLink(r3, r9, r6)
            r0.messageText = r3
            goto L_0x0f89
        L_0x0f6e:
            im.bclpbkiauv.tgnet.TLRPC$ChannelLocation r3 = r2.new_value
            im.bclpbkiauv.tgnet.TLRPC$TL_channelLocation r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelLocation) r3
            r5 = 2131691169(0x7f0f06a1, float:1.9011402E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r7 = r3.address
            r10 = 0
            r8[r10] = r7
            java.lang.String r7 = "EventLogChangedLocation"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r5, r8)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
        L_0x0f89:
            goto L_0x1002
        L_0x0f8b:
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode
            if (r2 == 0) goto L_0x0fec
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode) r2
            int r3 = r2.new_value
            if (r3 != 0) goto L_0x0fa9
            r3 = 2131691252(0x7f0f06f4, float:1.901157E38)
            java.lang.String r5 = "EventLogToggledSlowmodeOff"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            java.lang.CharSequence r3 = r0.replaceWithLink(r3, r9, r6)
            r0.messageText = r3
            goto L_0x0feb
        L_0x0fa9:
            int r3 = r2.new_value
            r5 = 60
            if (r3 >= r5) goto L_0x0fb8
            int r3 = r2.new_value
            java.lang.String r5 = "Seconds"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r5, r3)
            goto L_0x0fd6
        L_0x0fb8:
            int r3 = r2.new_value
            r5 = 3600(0xe10, float:5.045E-42)
            if (r3 >= r5) goto L_0x0fca
            int r3 = r2.new_value
            r5 = 60
            int r3 = r3 / r5
            java.lang.String r5 = "Minutes"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r5, r3)
            goto L_0x0fd6
        L_0x0fca:
            r5 = 60
            int r3 = r2.new_value
            int r3 = r3 / r5
            int r3 = r3 / r5
            java.lang.String r5 = "Hours"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r5, r3)
        L_0x0fd6:
            r5 = 2131691253(0x7f0f06f5, float:1.9011573E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            r7 = 0
            r8[r7] = r3
            java.lang.String r7 = "EventLogToggledSlowmodeOn"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r5, r8)
            java.lang.CharSequence r5 = r0.replaceWithLink(r5, r9, r6)
            r0.messageText = r5
        L_0x0feb:
            goto L_0x1002
        L_0x0fec:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "unsupported "
            r2.append(r3)
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r3 = r1.action
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.messageText = r2
        L_0x1002:
            r15 = r27
        L_0x1004:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            if (r2 != 0) goto L_0x100f
            im.bclpbkiauv.tgnet.TLRPC$TL_messageService r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageService
            r2.<init>()
            r0.messageOwner = r2
        L_0x100f:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            r2.message = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            int r3 = r1.user_id
            r2.from_id = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            int r3 = r1.date
            r2.date = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            r3 = 0
            r5 = r38[r3]
            int r7 = r5 + 1
            r38[r3] = r7
            r2.id = r5
            long r7 = r1.id
            r0.eventId = r7
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            r2.out = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$TL_peerChannel r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_peerChannel
            r3.<init>()
            r2.to_id = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r2 = r2.to_id
            int r3 = r4.id
            r2.channel_id = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            r3 = 0
            r2.unread = r3
            boolean r2 = r4.megagroup
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r2 == 0) goto L_0x105b
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            int r5 = r2.flags
            r5 = r5 | r3
            r2.flags = r5
        L_0x105b:
            im.bclpbkiauv.messenger.MediaController r2 = im.bclpbkiauv.messenger.MediaController.getInstance()
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.message
            if (r5 == 0) goto L_0x1071
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.message
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageEmpty
            if (r5 != 0) goto L_0x1071
            im.bclpbkiauv.tgnet.TLRPC$ChannelAdminLogEventAction r5 = r1.action
            im.bclpbkiauv.tgnet.TLRPC$Message r15 = r5.message
        L_0x1071:
            if (r15 == 0) goto L_0x10d9
            r5 = 0
            r15.out = r5
            r7 = r38[r5]
            int r8 = r7 + 1
            r38[r5] = r8
            r15.id = r7
            r15.reply_to_msg_id = r5
            int r5 = r15.flags
            r7 = -32769(0xffffffffffff7fff, float:NaN)
            r5 = r5 & r7
            r15.flags = r5
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x1091
            int r5 = r15.flags
            r3 = r3 | r5
            r15.flags = r3
        L_0x1091:
            im.bclpbkiauv.messenger.MessageObject r3 = new im.bclpbkiauv.messenger.MessageObject
            int r5 = r0.currentAccount
            r26 = 0
            r27 = 0
            r28 = 1
            long r7 = r0.eventId
            r23 = r3
            r24 = r5
            r25 = r15
            r29 = r7
            r23.<init>((int) r24, (im.bclpbkiauv.tgnet.TLRPC.Message) r25, (java.util.AbstractMap<java.lang.Integer, im.bclpbkiauv.tgnet.TLRPC.User>) r26, (java.util.AbstractMap<java.lang.Integer, im.bclpbkiauv.tgnet.TLRPC.Chat>) r27, (boolean) r28, (long) r29)
            int r5 = r3.contentType
            if (r5 < 0) goto L_0x10d1
            boolean r5 = r2.isPlayingMessage(r3)
            if (r5 == 0) goto L_0x10be
            im.bclpbkiauv.messenger.MessageObject r5 = r2.getPlayingMessageObject()
            float r7 = r5.audioProgress
            r3.audioProgress = r7
            int r7 = r5.audioProgressSec
            r3.audioProgressSec = r7
        L_0x10be:
            int r5 = r0.currentAccount
            r7 = r35
            r8 = r36
            r0.createDateArray(r5, r1, r7, r8)
            int r5 = r35.size()
            r9 = 1
            int r5 = r5 - r9
            r7.add(r5, r3)
            goto L_0x10dd
        L_0x10d1:
            r7 = r35
            r8 = r36
            r5 = -1
            r0.contentType = r5
            goto L_0x10dd
        L_0x10d9:
            r7 = r35
            r8 = r36
        L_0x10dd:
            int r3 = r0.contentType
            if (r3 < 0) goto L_0x114b
            int r3 = r0.currentAccount
            r0.createDateArray(r3, r1, r7, r8)
            int r3 = r35.size()
            r5 = 1
            int r3 = r3 - r5
            r7.add(r3, r0)
            java.lang.CharSequence r3 = r0.messageText
            if (r3 != 0) goto L_0x10f5
            r0.messageText = r13
        L_0x10f5:
            r32.setType()
            r32.measureInlineBotButtons()
            r32.generateCaption()
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x1109
            android.text.TextPaint r3 = im.bclpbkiauv.ui.actionbar.Theme.chat_msgGameTextPaint
            goto L_0x110b
        L_0x1109:
            android.text.TextPaint r3 = im.bclpbkiauv.ui.actionbar.Theme.chat_msgTextPaint
        L_0x110b:
            boolean r5 = im.bclpbkiauv.messenger.SharedConfig.allowBigEmoji
            if (r5 == 0) goto L_0x1113
            r5 = 1
            int[] r9 = new int[r5]
            goto L_0x1114
        L_0x1113:
            r9 = 0
        L_0x1114:
            r5 = r9
            java.lang.CharSequence r9 = r0.messageText
            android.graphics.Paint$FontMetricsInt r10 = r3.getFontMetricsInt()
            r11 = 1101004800(0x41a00000, float:20.0)
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
            r12 = 0
            java.lang.CharSequence r9 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r9, r10, r11, r12, r5)
            r0.messageText = r9
            r0.checkEmojiOnly(r5)
            boolean r9 = r2.isPlayingMessage(r0)
            if (r9 == 0) goto L_0x113d
            im.bclpbkiauv.messenger.MessageObject r9 = r2.getPlayingMessageObject()
            float r10 = r9.audioProgress
            r0.audioProgress = r10
            int r10 = r9.audioProgressSec
            r0.audioProgressSec = r10
        L_0x113d:
            r0.generateLayout(r6)
            r9 = 1
            r0.layoutCreated = r9
            r9 = 0
            r0.generateThumbs(r9)
            r32.checkMediaExistance()
            return
        L_0x114b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MessageObject.<init>(int, im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEvent, java.util.ArrayList, java.util.HashMap, im.bclpbkiauv.tgnet.TLRPC$Chat, int[]):void");
    }

    private String getUserName(TLRPC.User user, ArrayList<TLRPC.MessageEntity> entities, int offset) {
        String name;
        if (user == null) {
            name = "";
        } else {
            name = ContactsController.formatName(user.first_name, user.last_name);
        }
        if (offset >= 0) {
            TLRPC.TL_messageEntityMentionName entity = new TLRPC.TL_messageEntityMentionName();
            entity.user_id = user.id;
            entity.offset = offset;
            entity.length = name.length();
            entities.add(entity);
        }
        if (TextUtils.isEmpty(user.username)) {
            return name;
        }
        if (offset >= 0) {
            TLRPC.TL_messageEntityMentionName entity2 = new TLRPC.TL_messageEntityMentionName();
            entity2.user_id = user.id;
            entity2.offset = name.length() + offset + 2;
            entity2.length = user.username.length() + 1;
            entities.add(entity2);
        }
        return String.format("%1$s (@%2$s)", new Object[]{name, user.username});
    }

    public void applyNewText() {
        TextPaint paint;
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            TLRPC.User fromUser = null;
            if (isFromUser()) {
                fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            this.messageText = this.messageOwner.message;
            if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                paint = Theme.chat_msgGameTextPaint;
            } else {
                paint = Theme.chat_msgTextPaint;
            }
            int[] emojiOnly = SharedConfig.allowBigEmoji ? new int[1] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly);
            checkEmojiOnly(emojiOnly);
            generateLayout(fromUser);
        }
    }

    public void generateGameMessageText(TLRPC.User fromUser) {
        if (fromUser == null && this.messageOwner.from_id > 0) {
            fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        TLRPC.TL_game game = null;
        MessageObject messageObject = this.replyMessageObject;
        if (!(messageObject == null || messageObject.messageOwner.media == null || this.replyMessageObject.messageOwner.media.game == null)) {
            game = this.replyMessageObject.messageOwner.media.game;
        }
        if (game != null) {
            if (fromUser == null || fromUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", R.string.ActionUserScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", fromUser);
            } else {
                this.messageText = LocaleController.formatString("ActionYouScoredInGame", R.string.ActionYouScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
            }
            this.messageText = replaceWithLink(this.messageText, "un2", game);
        } else if (fromUser == null || fromUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", R.string.ActionUserScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", fromUser);
        } else {
            this.messageText = LocaleController.formatString("ActionYouScored", R.string.ActionYouScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
        }
    }

    public boolean hasValidReplyMessageObject() {
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            return !(message instanceof TLRPC.TL_messageEmpty) && !(message.action instanceof TLRPC.TL_messageActionHistoryClear);
        }
    }

    public void generatePaymentSentMessageText(TLRPC.User fromUser) {
        String name;
        if (fromUser == null) {
            fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) getDialogId()));
        }
        if (fromUser != null) {
            name = UserObject.getFirstName(fromUser);
        } else {
            name = "";
        }
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null || !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
            this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", R.string.PaymentSuccessfullyPaidNoItem, LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), name);
            return;
        }
        this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", R.string.PaymentSuccessfullyPaid, LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), name, this.replyMessageObject.messageOwner.media.title);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v26, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v30, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v34, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v38, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v42, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v46, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v50, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v54, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v58, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v62, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v66, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generatePinMessageText(im.bclpbkiauv.tgnet.TLRPC.User r8, im.bclpbkiauv.tgnet.TLRPC.Chat r9) {
        /*
            r7 = this;
            if (r8 != 0) goto L_0x0032
            if (r9 != 0) goto L_0x0032
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r7.messageOwner
            int r0 = r0.from_id
            if (r0 <= 0) goto L_0x001c
            int r0 = r7.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r7.messageOwner
            int r1 = r1.from_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            im.bclpbkiauv.tgnet.TLRPC$User r8 = r0.getUser(r1)
        L_0x001c:
            if (r8 != 0) goto L_0x0032
            int r0 = r7.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r7.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r0.getChat(r1)
        L_0x0032:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            r1 = 2131689627(0x7f0f009b, float:1.9008275E38)
            java.lang.String r2 = "ActionPinnedNoText"
            java.lang.String r3 = "un1"
            if (r0 == 0) goto L_0x0281
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r4 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageEmpty
            if (r4 != 0) goto L_0x0281
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r0 == 0) goto L_0x004c
            goto L_0x0281
        L_0x004c:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x006a
            r0 = 2131689626(0x7f0f009a, float:1.9008273E38)
            java.lang.String r1 = "ActionPinnedMusic"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x0061
            r1 = r8
            goto L_0x0062
        L_0x0061:
            r1 = r9
        L_0x0062:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x006a:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0088
            r0 = 2131689633(0x7f0f00a1, float:1.9008287E38)
            java.lang.String r1 = "ActionPinnedVideo"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x007f
            r1 = r8
            goto L_0x0080
        L_0x007f:
            r1 = r9
        L_0x0080:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x0088:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x00a6
            r0 = 2131689625(0x7f0f0099, float:1.900827E38)
            java.lang.String r1 = "ActionPinnedGif"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x009d
            r1 = r8
            goto L_0x009e
        L_0x009d:
            r1 = r9
        L_0x009e:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x00a6:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x00c4
            r0 = 2131689634(0x7f0f00a2, float:1.9008289E38)
            java.lang.String r1 = "ActionPinnedVoice"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x00bb
            r1 = r8
            goto L_0x00bc
        L_0x00bb:
            r1 = r9
        L_0x00bc:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x00c4:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isRoundVideo()
            if (r0 == 0) goto L_0x00e2
            r0 = 2131689630(0x7f0f009e, float:1.900828E38)
            java.lang.String r1 = "ActionPinnedRound"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x00d9
            r1 = r8
            goto L_0x00da
        L_0x00d9:
            r1 = r9
        L_0x00da:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x00e2:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isSticker()
            if (r0 != 0) goto L_0x026c
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isAnimatedSticker()
            if (r0 == 0) goto L_0x00f4
            goto L_0x026c
        L_0x00f4:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r0 == 0) goto L_0x0114
            r0 = 2131689621(0x7f0f0095, float:1.9008263E38)
            java.lang.String r1 = "ActionPinnedFile"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x010b
            r1 = r8
            goto L_0x010c
        L_0x010b:
            r1 = r9
        L_0x010c:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x0114:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo
            if (r0 == 0) goto L_0x0134
            r0 = 2131689623(0x7f0f0097, float:1.9008267E38)
            java.lang.String r1 = "ActionPinnedGeo"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x012b
            r1 = r8
            goto L_0x012c
        L_0x012b:
            r1 = r9
        L_0x012c:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x0134:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r0 == 0) goto L_0x0154
            r0 = 2131689624(0x7f0f0098, float:1.9008269E38)
            java.lang.String r1 = "ActionPinnedGeoLive"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x014b
            r1 = r8
            goto L_0x014c
        L_0x014b:
            r1 = r9
        L_0x014c:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x0154:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact
            if (r0 == 0) goto L_0x0174
            r0 = 2131689620(0x7f0f0094, float:1.900826E38)
            java.lang.String r1 = "ActionPinnedContact"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x016b
            r1 = r8
            goto L_0x016c
        L_0x016b:
            r1 = r9
        L_0x016c:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x0174:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x0194
            r0 = 2131689629(0x7f0f009d, float:1.9008279E38)
            java.lang.String r1 = "ActionPinnedPoll"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x018b
            r1 = r8
            goto L_0x018c
        L_0x018b:
            r1 = r9
        L_0x018c:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x0194:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r0 == 0) goto L_0x01b4
            r0 = 2131689628(0x7f0f009c, float:1.9008277E38)
            java.lang.String r1 = "ActionPinnedPhoto"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x01ab
            r1 = r8
            goto L_0x01ac
        L_0x01ab:
            r1 = r9
        L_0x01ac:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x01b4:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            r4 = 1101004800(0x41a00000, float:20.0)
            r5 = 1
            r6 = 0
            if (r0 == 0) goto L_0x0208
            r0 = 2131689622(0x7f0f0096, float:1.9008265E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "🎮 "
            r2.append(r5)
            im.bclpbkiauv.messenger.MessageObject r5 = r7.replyMessageObject
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r5 = r5.game
            java.lang.String r5 = r5.title
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r1[r6] = r2
            java.lang.String r2 = "ActionPinnedGame"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r1)
            if (r8 == 0) goto L_0x01ef
            r1 = r8
            goto L_0x01f0
        L_0x01ef:
            r1 = r9
        L_0x01f0:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            android.text.TextPaint r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            java.lang.CharSequence r0 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r0, r1, r2, r6)
            r7.messageText = r0
            goto L_0x0290
        L_0x0208:
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            if (r0 == 0) goto L_0x025c
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x025c
            im.bclpbkiauv.messenger.MessageObject r0 = r7.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0235
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.CharSequence r2 = r0.subSequence(r6, r2)
            r1.append(r2)
            java.lang.String r2 = "..."
            r1.append(r2)
            java.lang.String r0 = r1.toString()
        L_0x0235:
            android.text.TextPaint r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            java.lang.CharSequence r0 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r0, r1, r2, r6)
            r1 = 2131689632(0x7f0f00a0, float:1.9008285E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r6] = r0
            java.lang.String r4 = "ActionPinnedText"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r1, r2)
            if (r8 == 0) goto L_0x0254
            r2 = r8
            goto L_0x0255
        L_0x0254:
            r2 = r9
        L_0x0255:
            java.lang.CharSequence r1 = r7.replaceWithLink(r1, r3, r2)
            r7.messageText = r1
            goto L_0x0290
        L_0x025c:
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            if (r8 == 0) goto L_0x0264
            r1 = r8
            goto L_0x0265
        L_0x0264:
            r1 = r9
        L_0x0265:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x026c:
            r0 = 2131689631(0x7f0f009f, float:1.9008283E38)
            java.lang.String r1 = "ActionPinnedSticker"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x0279
            r1 = r8
            goto L_0x027a
        L_0x0279:
            r1 = r9
        L_0x027a:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
            goto L_0x0290
        L_0x0281:
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            if (r8 == 0) goto L_0x0289
            r1 = r8
            goto L_0x028a
        L_0x0289:
            r1 = r9
        L_0x028a:
            java.lang.CharSequence r0 = r7.replaceWithLink(r0, r3, r1)
            r7.messageText = r0
        L_0x0290:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MessageObject.generatePinMessageText(im.bclpbkiauv.tgnet.TLRPC$User, im.bclpbkiauv.tgnet.TLRPC$Chat):void");
    }

    public static void updateReactions(TLRPC.Message message, TLRPC.TL_messageReactions reactions) {
        if (message != null && reactions != null) {
            if (reactions.min && message.reactions != null) {
                int a = 0;
                int N = message.reactions.results.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.TL_reactionCount reaction = message.reactions.results.get(a);
                    if (reaction.chosen) {
                        int b = 0;
                        int N2 = reactions.results.size();
                        while (true) {
                            if (b >= N2) {
                                break;
                            }
                            TLRPC.TL_reactionCount newReaction = reactions.results.get(b);
                            if (reaction.reaction.equals(newReaction.reaction)) {
                                newReaction.chosen = true;
                                break;
                            }
                            b++;
                        }
                    } else {
                        a++;
                    }
                }
            }
            message.reactions = reactions;
            message.flags |= 1048576;
        }
    }

    public boolean hasReactions() {
        return this.messageOwner.reactions != null && !this.messageOwner.reactions.results.isEmpty();
    }

    public static void updatePollResults(TLRPC.TL_messageMediaPoll media, TLRPC.TL_pollResults results) {
        if ((results.flags & 2) != 0) {
            byte[] chosen = null;
            if (results.min && media.results.results != null) {
                int b = 0;
                int N2 = media.results.results.size();
                while (true) {
                    if (b >= N2) {
                        break;
                    }
                    TLRPC.TL_pollAnswerVoters answerVoters = media.results.results.get(b);
                    if (answerVoters.chosen) {
                        chosen = answerVoters.option;
                        break;
                    }
                    b++;
                }
            }
            media.results.results = results.results;
            if (chosen != null) {
                int b2 = 0;
                int N22 = media.results.results.size();
                while (true) {
                    if (b2 >= N22) {
                        break;
                    }
                    TLRPC.TL_pollAnswerVoters answerVoters2 = media.results.results.get(b2);
                    if (Arrays.equals(answerVoters2.option, chosen)) {
                        answerVoters2.chosen = true;
                        break;
                    }
                    b2++;
                }
            }
            media.results.flags |= 2;
        }
        if ((results.flags & 4) != 0) {
            media.results.total_voters = results.total_voters;
            media.results.flags |= 4;
        }
    }

    public boolean isPollClosed() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC.TL_messageMediaPoll) this.messageOwner.media).poll.closed;
    }

    public boolean isVoted() {
        if (this.type != 17) {
            return false;
        }
        TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) this.messageOwner.media;
        if (mediaPoll.results == null || mediaPoll.results.results.isEmpty()) {
            return false;
        }
        int N = mediaPoll.results.results.size();
        for (int a = 0; a < N; a++) {
            if (mediaPoll.results.results.get(a).chosen) {
                return true;
            }
        }
        return false;
    }

    public long getPollId() {
        if (this.type != 17) {
            return 0;
        }
        return ((TLRPC.TL_messageMediaPoll) this.messageOwner.media).poll.id;
    }

    private TLRPC.Photo getPhotoWithId(TLRPC.WebPage webPage, long id) {
        if (webPage == null || webPage.cached_page == null) {
            return null;
        }
        if (webPage.photo != null && webPage.photo.id == id) {
            return webPage.photo;
        }
        for (int a = 0; a < webPage.cached_page.photos.size(); a++) {
            TLRPC.Photo photo = webPage.cached_page.photos.get(a);
            if (photo.id == id) {
                return photo;
            }
        }
        return null;
    }

    private TLRPC.Document getDocumentWithId(TLRPC.WebPage webPage, long id) {
        if (webPage == null || webPage.cached_page == null) {
            return null;
        }
        if (webPage.document != null && webPage.document.id == id) {
            return webPage.document;
        }
        for (int a = 0; a < webPage.cached_page.documents.size(); a++) {
            TLRPC.Document document = webPage.cached_page.documents.get(a);
            if (document.id == id) {
                return document;
            }
        }
        return null;
    }

    private MessageObject getMessageObjectForBlock(TLRPC.WebPage webPage, TLRPC.PageBlock pageBlock) {
        TLRPC.TL_message message = null;
        if (pageBlock instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.Photo photo = getPhotoWithId(webPage, ((TLRPC.TL_pageBlockPhoto) pageBlock).photo_id);
            if (photo == webPage.photo) {
                return this;
            }
            message = new TLRPC.TL_message();
            message.media = new TLRPC.TL_messageMediaPhoto();
            message.media.photo = photo;
        } else if (pageBlock instanceof TLRPC.TL_pageBlockVideo) {
            TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) pageBlock;
            if (getDocumentWithId(webPage, pageBlockVideo.video_id) == webPage.document) {
                return this;
            }
            message = new TLRPC.TL_message();
            message.media = new TLRPC.TL_messageMediaDocument();
            message.media.document = getDocumentWithId(webPage, pageBlockVideo.video_id);
        }
        message.message = "";
        message.realId = getId();
        message.id = Utilities.random.nextInt();
        message.date = this.messageOwner.date;
        message.to_id = this.messageOwner.to_id;
        message.out = this.messageOwner.out;
        message.from_id = this.messageOwner.from_id;
        return new MessageObject(this.currentAccount, message, false);
    }

    public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> array, ArrayList<TLRPC.PageBlock> blocksToSearch) {
        ArrayList<MessageObject> messageObjects = array == null ? new ArrayList<>() : array;
        if (this.messageOwner.media == null || this.messageOwner.media.webpage == null) {
            return messageObjects;
        }
        TLRPC.WebPage webPage = this.messageOwner.media.webpage;
        if (webPage.cached_page == null) {
            return messageObjects;
        }
        ArrayList<TLRPC.PageBlock> blocks = blocksToSearch == null ? webPage.cached_page.blocks : blocksToSearch;
        for (int a = 0; a < blocks.size(); a++) {
            TLRPC.PageBlock block = blocks.get(a);
            if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow slideshow = (TLRPC.TL_pageBlockSlideshow) block;
                for (int b = 0; b < slideshow.items.size(); b++) {
                    messageObjects.add(getMessageObjectForBlock(webPage, slideshow.items.get(b)));
                }
            } else if (block instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage slideshow2 = (TLRPC.TL_pageBlockCollage) block;
                for (int b2 = 0; b2 < slideshow2.items.size(); b2++) {
                    messageObjects.add(getMessageObjectForBlock(webPage, slideshow2.items.get(b2)));
                }
            }
        }
        return messageObjects;
    }

    public void createMessageSendInfo() {
        if (this.messageOwner.message == null) {
            return;
        }
        if ((this.messageOwner.id < 0 || isEditing()) && this.messageOwner.params != null) {
            String str = this.messageOwner.params.get("ve");
            String param = str;
            if (str != null && (isVideo() || isNewGif() || isRoundVideo())) {
                VideoEditedInfo videoEditedInfo2 = new VideoEditedInfo();
                this.videoEditedInfo = videoEditedInfo2;
                if (!videoEditedInfo2.parseString(param)) {
                    this.videoEditedInfo = null;
                } else {
                    this.videoEditedInfo.roundVideo = isRoundVideo();
                }
            }
            if (this.messageOwner.send_state == 3) {
                String str2 = this.messageOwner.params.get("prevMedia");
                String param2 = str2;
                if (str2 != null) {
                    SerializedData serializedData = new SerializedData(Base64.decode(param2, 0));
                    this.previousMedia = TLRPC.MessageMedia.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                    this.previousCaption = serializedData.readString(false);
                    this.previousAttachPath = serializedData.readString(false);
                    int count = serializedData.readInt32(false);
                    this.previousCaptionEntities = new ArrayList<>(count);
                    for (int a = 0; a < count; a++) {
                        this.previousCaptionEntities.add(TLRPC.MessageEntity.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                    }
                    serializedData.cleanup();
                }
            }
        }
    }

    public void measureInlineBotButtons() {
        CharSequence text;
        this.wantedBotKeyboardWidth = 0;
        if ((this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || (this.messageOwner.reactions != null && !this.messageOwner.reactions.results.isEmpty())) {
            Theme.createChatResources((Context) null, true);
            StringBuilder sb = this.botButtonsLayout;
            if (sb == null) {
                this.botButtonsLayout = new StringBuilder();
            } else {
                sb.setLength(0);
            }
        }
        float f = 2000.0f;
        float f2 = 15.0f;
        if (this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) {
            int a = 0;
            while (a < this.messageOwner.reply_markup.rows.size()) {
                TLRPC.TL_keyboardButtonRow row = this.messageOwner.reply_markup.rows.get(a);
                int maxButtonSize = 0;
                int size = row.buttons.size();
                int b = 0;
                while (b < size) {
                    TLRPC.KeyboardButton button = row.buttons.get(b);
                    StringBuilder sb2 = this.botButtonsLayout;
                    sb2.append(a);
                    sb2.append(b);
                    if (!(button instanceof TLRPC.TL_keyboardButtonBuy) || (this.messageOwner.media.flags & 4) == 0) {
                        text = Emoji.replaceEmoji(button.text, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(f2), false);
                    } else {
                        text = LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt);
                    }
                    StaticLayout staticLayout = new StaticLayout(text, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (staticLayout.getLineCount() > 0) {
                        float width = staticLayout.getLineWidth(0);
                        float left = staticLayout.getLineLeft(0);
                        if (left < width) {
                            width -= left;
                        }
                        maxButtonSize = Math.max(maxButtonSize, ((int) Math.ceil((double) width)) + AndroidUtilities.dp(4.0f));
                    }
                    b++;
                    f = 2000.0f;
                    f2 = 15.0f;
                }
                this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((AndroidUtilities.dp(12.0f) + maxButtonSize) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
                a++;
                f = 2000.0f;
                f2 = 15.0f;
            }
        } else if (this.messageOwner.reactions != null) {
            int size2 = this.messageOwner.reactions.results.size();
            for (int a2 = 0; a2 < size2; a2++) {
                TLRPC.TL_reactionCount reactionCount = this.messageOwner.reactions.results.get(a2);
                int maxButtonSize2 = 0;
                StringBuilder sb3 = this.botButtonsLayout;
                sb3.append(0);
                sb3.append(a2);
                StaticLayout staticLayout2 = new StaticLayout(Emoji.replaceEmoji(String.format("%d %s", new Object[]{Integer.valueOf(reactionCount.count), reactionCount.reaction}), Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (staticLayout2.getLineCount() > 0) {
                    float width2 = staticLayout2.getLineWidth(0);
                    float left2 = staticLayout2.getLineLeft(0);
                    if (left2 < width2) {
                        width2 -= left2;
                    }
                    maxButtonSize2 = Math.max(0, ((int) Math.ceil((double) width2)) + AndroidUtilities.dp(4.0f));
                }
                this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((AndroidUtilities.dp(12.0f) + maxButtonSize2) * size2) + (AndroidUtilities.dp(5.0f) * (size2 - 1)));
            }
        }
    }

    public boolean isFcmMessage() {
        return this.localType != 0;
    }

    public void setDelegate(Delegate delegate2) {
        this.delegate = delegate2;
    }

    public String setMoneyFormat(String data) {
        if (!NumberUtil.isNumber(data)) {
            return "";
        }
        if (!data.contains(".")) {
            return MoneyUtil.formatToString(new BigDecimal(String.valueOf(data)).multiply(new BigDecimal("1")).toString(), 0);
        }
        String[] split = data.split("\\.");
        String number1 = split[0];
        String number2 = split[1];
        String res = MoneyUtil.formatToString(new BigDecimal(String.valueOf(number1)).multiply(new BigDecimal("1")).toString(), 0);
        if (number2.length() > 8) {
            number2 = number2.substring(0, 8);
        }
        return res + "." + number2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:498:0x0ee6  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0f2d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMessageText(java.util.AbstractMap<java.lang.Integer, im.bclpbkiauv.tgnet.TLRPC.User> r20, java.util.AbstractMap<java.lang.Integer, im.bclpbkiauv.tgnet.TLRPC.Chat> r21, android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC.User> r22, android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC.Chat> r23) {
        /*
            r19 = this;
            r6 = r19
            r7 = r20
            r8 = r21
            r9 = r22
            r10 = r23
            r0 = 0
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            int r1 = r1.from_id
            if (r1 <= 0) goto L_0x0048
            if (r7 == 0) goto L_0x0023
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            int r1 = r1.from_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.lang.Object r1 = r7.get(r1)
            r0 = r1
            im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
            goto L_0x0030
        L_0x0023:
            if (r9 == 0) goto L_0x0030
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            int r1 = r1.from_id
            java.lang.Object r1 = r9.get(r1)
            r0 = r1
            im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
        L_0x0030:
            if (r0 != 0) goto L_0x0046
            int r1 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r1)
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            int r2 = r2.from_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.getUser(r2)
            r11 = r0
            goto L_0x0049
        L_0x0046:
            r11 = r0
            goto L_0x0049
        L_0x0048:
            r11 = r0
        L_0x0049:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            int r0 = r0.from_id
            r1 = 777000(0xbdb28, float:1.088809E-39)
            if (r0 != r1) goto L_0x0059
            java.lang.String r0 = "số hệ thống"
            r11.first_name = r0
            r11.last_name = r0
        L_0x0059:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            boolean r1 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageService
            r2 = -1
            java.lang.String r12 = ""
            r3 = 2
            r4 = 1
            r5 = 0
            if (r1 == 0) goto L_0x0b98
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            if (r0 == 0) goto L_0x12c7
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer
            if (r0 == 0) goto L_0x00ff
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            im.bclpbkiauv.tgnet.TLRPCRedpacket$CL_messagesActionReceivedRpkTransfer r0 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer) r0
            int r1 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r1)
            im.bclpbkiauv.tgnet.TLRPC$Peer r2 = r0.receiver
            int r2 = r2.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$User r1 = r1.getUser(r2)
            int r2 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
            im.bclpbkiauv.tgnet.TLRPC$Peer r3 = r0.sender
            int r3 = r3.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r2.getUser(r3)
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r3 = r0.data
            java.lang.Class<im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse> r4 = im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse.class
            im.bclpbkiauv.tgnet.TLApiModel r3 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r3, (java.lang.Class<?>) r4)
            T r4 = r3.model
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse r4 = (im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse) r4
            if (r11 == 0) goto L_0x00fd
            r5 = -1
            im.bclpbkiauv.tgnet.TLRPC$Peer r13 = r0.sender
            int r13 = r13.user_id
            int r14 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r14 = im.bclpbkiauv.messenger.UserConfig.getInstance(r14)
            int r14 = r14.clientUserId
            if (r13 != r14) goto L_0x00e0
            if (r4 == 0) goto L_0x00e0
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketBean r13 = r4.getRed()
            if (r13 == 0) goto L_0x00e0
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketBean r13 = r4.getRed()
            java.lang.String r13 = r13.getStatus()
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 != 0) goto L_0x00e0
            boolean r13 = r19.isFromChat()
            if (r13 == 0) goto L_0x00e0
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketBean r13 = r4.getRed()
            java.lang.String r13 = r13.getStatus()
            int r5 = java.lang.Integer.parseInt(r13)
        L_0x00e0:
            boolean r13 = r19.isOut()
            r14 = 0
            if (r13 == 0) goto L_0x00ea
            r6.messageText = r14
            goto L_0x00fd
        L_0x00ea:
            if (r2 == 0) goto L_0x00fb
            int r13 = r2.id
            int r15 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r15 = im.bclpbkiauv.messenger.UserConfig.getInstance(r15)
            int r15 = r15.clientUserId
            if (r13 != r15) goto L_0x00fb
            r6.messageText = r14
            goto L_0x00fd
        L_0x00fb:
            r6.messageText = r14
        L_0x00fd:
            goto L_0x12c7
        L_0x00ff:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionCustomAction
            if (r0 == 0) goto L_0x012c
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r0 = r0.message
            java.lang.String r1 = "MUTUALCONTACTS"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0122
            r0 = 2131692081(0x7f0f0a31, float:1.9013252E38)
            java.lang.String r1 = "MutualContactsText"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0122:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r0 = r0.message
            r6.messageText = r0
            goto L_0x12c7
        L_0x012c:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatCreate
            java.lang.String r13 = "un1"
            if (r0 == 0) goto L_0x015b
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x014a
            r0 = 2131689643(0x7f0f00ab, float:1.9008307E38)
            java.lang.String r1 = "ActionYouCreateGroup"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x014a:
            r0 = 2131689611(0x7f0f008b, float:1.9008242E38)
            java.lang.String r1 = "ActionCreateGroup"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x12c7
        L_0x015b:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatDeleteUser
            java.lang.String r1 = "un2"
            if (r0 == 0) goto L_0x021f
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            int r2 = r2.from_id
            if (r0 != r2) goto L_0x0196
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x0185
            r0 = 2131689645(0x7f0f00ad, float:1.9008311E38)
            java.lang.String r1 = "ActionYouLeftUser"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0185:
            r0 = 2131689617(0x7f0f0091, float:1.9008254E38)
            java.lang.String r1 = "ActionLeftUser"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0196:
            r0 = 0
            if (r7 == 0) goto L_0x01ab
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            java.lang.Object r2 = r7.get(r2)
            r0 = r2
            im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
            goto L_0x01ba
        L_0x01ab:
            if (r9 == 0) goto L_0x01ba
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.user_id
            java.lang.Object r2 = r9.get(r2)
            r0 = r2
            im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
        L_0x01ba:
            if (r0 != 0) goto L_0x01d0
            int r2 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r3 = r3.action
            int r3 = r3.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r2.getUser(r3)
        L_0x01d0:
            boolean r2 = r19.isOut()
            if (r2 == 0) goto L_0x01e6
            r2 = 2131689644(0x7f0f00ac, float:1.900831E38)
            java.lang.String r3 = "ActionYouKickUser"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r1 = r6.replaceWithLink(r2, r1, r0)
            r6.messageText = r1
            goto L_0x021d
        L_0x01e6:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.user_id
            int r3 = r6.currentAccount
            im.bclpbkiauv.messenger.UserConfig r3 = im.bclpbkiauv.messenger.UserConfig.getInstance(r3)
            int r3 = r3.getClientUserId()
            if (r2 != r3) goto L_0x0208
            r1 = 2131689616(0x7f0f0090, float:1.9008252E38)
            java.lang.String r2 = "ActionKickUserYou"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = r6.replaceWithLink(r1, r13, r11)
            r6.messageText = r1
            goto L_0x021d
        L_0x0208:
            r2 = 2131689615(0x7f0f008f, float:1.900825E38)
            java.lang.String r3 = "ActionKickUser"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r1 = r6.replaceWithLink(r2, r1, r0)
            r6.messageText = r1
            java.lang.CharSequence r1 = r6.replaceWithLink(r1, r13, r11)
            r6.messageText = r1
        L_0x021d:
            goto L_0x12c7
        L_0x021f:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r0 == 0) goto L_0x03ae
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            if (r0 != 0) goto L_0x024d
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            int r2 = r2.size()
            if (r2 != r4) goto L_0x024d
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            java.lang.Object r2 = r2.get(r5)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r0 = r2.intValue()
            r14 = r0
            goto L_0x024e
        L_0x024d:
            r14 = r0
        L_0x024e:
            r0 = 2131689640(0x7f0f00a8, float:1.9008301E38)
            java.lang.String r2 = "ActionYouAddUser"
            java.lang.String r4 = "ActionAddUser"
            if (r14 == 0) goto L_0x036a
            r5 = 0
            if (r7 == 0) goto L_0x0266
            java.lang.Integer r15 = java.lang.Integer.valueOf(r14)
            java.lang.Object r15 = r7.get(r15)
            r5 = r15
            im.bclpbkiauv.tgnet.TLRPC$User r5 = (im.bclpbkiauv.tgnet.TLRPC.User) r5
            goto L_0x026f
        L_0x0266:
            if (r9 == 0) goto L_0x026f
            java.lang.Object r15 = r9.get(r14)
            r5 = r15
            im.bclpbkiauv.tgnet.TLRPC$User r5 = (im.bclpbkiauv.tgnet.TLRPC.User) r5
        L_0x026f:
            if (r5 != 0) goto L_0x027f
            int r15 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r15 = im.bclpbkiauv.messenger.MessagesController.getInstance(r15)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r14)
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r15.getUser(r3)
        L_0x027f:
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r6.messageOwner
            int r3 = r3.from_id
            if (r14 != r3) goto L_0x02fb
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x02a0
            boolean r0 = r19.isMegagroup()
            if (r0 != 0) goto L_0x02a0
            r0 = 2131690432(0x7f0f03c0, float:1.9009907E38)
            java.lang.String r1 = "ChannelJoined"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0369
        L_0x02a0:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x02d8
            boolean r0 = r19.isMegagroup()
            if (r0 == 0) goto L_0x02d8
            int r0 = r6.currentAccount
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)
            int r0 = r0.getClientUserId()
            if (r14 != r0) goto L_0x02c7
            r0 = 2131690437(0x7f0f03c5, float:1.9009918E38)
            java.lang.String r1 = "ChannelMegaJoined"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0369
        L_0x02c7:
            r0 = 2131689586(0x7f0f0072, float:1.9008192E38)
            java.lang.String r1 = "ActionAddUserSelfMega"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x0369
        L_0x02d8:
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x02eb
            r0 = 2131689587(0x7f0f0073, float:1.9008194E38)
            java.lang.String r1 = "ActionAddUserSelfYou"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0369
        L_0x02eb:
            r0 = 2131689585(0x7f0f0071, float:1.900819E38)
            java.lang.String r1 = "ActionAddUserSelf"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x0369
        L_0x02fb:
            boolean r3 = r19.isOut()
            if (r3 == 0) goto L_0x030c
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r1, r5)
            r6.messageText = r0
            goto L_0x0369
        L_0x030c:
            int r0 = r6.currentAccount
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)
            int r0 = r0.getClientUserId()
            if (r14 != r0) goto L_0x0356
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x0346
            boolean r0 = r19.isMegagroup()
            if (r0 == 0) goto L_0x0336
            r0 = 2131691948(0x7f0f09ac, float:1.9012982E38)
            java.lang.String r1 = "MegaAddedBy"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x0369
        L_0x0336:
            r0 = 2131690397(0x7f0f039d, float:1.9009836E38)
            java.lang.String r1 = "ChannelAddedBy"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x0369
        L_0x0346:
            r0 = 2131689588(0x7f0f0074, float:1.9008196E38)
            java.lang.String r1 = "ActionAddUserYou"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x0369
        L_0x0356:
            r0 = 2131689584(0x7f0f0070, float:1.9008188E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r1, r5)
            r6.messageText = r0
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
        L_0x0369:
            goto L_0x03ac
        L_0x036a:
            boolean r1 = r19.isOut()
            if (r1 == 0) goto L_0x038a
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            java.util.ArrayList<java.lang.Integer> r3 = r0.users
            java.lang.String r2 = "un2"
            r0 = r19
            r4 = r20
            r5 = r22
            java.lang.CharSequence r0 = r0.replaceWithLink((java.lang.CharSequence) r1, (java.lang.String) r2, (java.util.ArrayList<java.lang.Integer>) r3, (java.util.AbstractMap<java.lang.Integer, im.bclpbkiauv.tgnet.TLRPC.User>) r4, (android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC.User>) r5)
            r6.messageText = r0
            goto L_0x03ac
        L_0x038a:
            r0 = 2131689584(0x7f0f0070, float:1.9008188E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r0)
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            java.util.ArrayList<java.lang.Integer> r3 = r0.users
            java.lang.String r2 = "un2"
            r0 = r19
            r4 = r20
            r5 = r22
            java.lang.CharSequence r0 = r0.replaceWithLink((java.lang.CharSequence) r1, (java.lang.String) r2, (java.util.ArrayList<java.lang.Integer>) r3, (java.util.AbstractMap<java.lang.Integer, im.bclpbkiauv.tgnet.TLRPC.User>) r4, (android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC.User>) r5)
            r6.messageText = r0
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
        L_0x03ac:
            goto L_0x12c7
        L_0x03ae:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r0 == 0) goto L_0x03da
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x03c9
            r0 = 2131689614(0x7f0f008e, float:1.9008248E38)
            java.lang.String r1 = "ActionInviteYou"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x03c9:
            r0 = 2131689613(0x7f0f008d, float:1.9008246E38)
            java.lang.String r1 = "ActionInviteUser"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x12c7
        L_0x03da:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r0 == 0) goto L_0x0421
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x03fd
            boolean r0 = r19.isMegagroup()
            if (r0 != 0) goto L_0x03fd
            r0 = 2131689607(0x7f0f0087, float:1.9008234E38)
            java.lang.String r1 = "ActionChannelChangedPhoto"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x03fd:
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x0410
            r0 = 2131689641(0x7f0f00a9, float:1.9008303E38)
            java.lang.String r1 = "ActionYouChangedPhoto"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0410:
            r0 = 2131689605(0x7f0f0085, float:1.900823E38)
            java.lang.String r1 = "ActionChangedPhoto"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0421:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatEditTitle
            if (r0 == 0) goto L_0x0486
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x044e
            boolean r0 = r19.isMegagroup()
            if (r0 != 0) goto L_0x044e
            r0 = 2131689608(0x7f0f0088, float:1.9008236E38)
            java.lang.String r2 = "ActionChannelChangedTitle"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            java.lang.String r2 = r2.title
            java.lang.String r0 = r0.replace(r1, r2)
            r6.messageText = r0
            goto L_0x12c7
        L_0x044e:
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x046b
            r0 = 2131689642(0x7f0f00aa, float:1.9008305E38)
            java.lang.String r2 = "ActionYouChangedTitle"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            java.lang.String r2 = r2.title
            java.lang.String r0 = r0.replace(r1, r2)
            r6.messageText = r0
            goto L_0x12c7
        L_0x046b:
            r0 = 2131689606(0x7f0f0086, float:1.9008232E38)
            java.lang.String r2 = "ActionChangedTitle"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            java.lang.String r2 = r2.title
            java.lang.String r0 = r0.replace(r1, r2)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0486:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatDeletePhoto
            if (r0 == 0) goto L_0x04cd
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x04a9
            boolean r0 = r19.isMegagroup()
            if (r0 != 0) goto L_0x04a9
            r0 = 2131689609(0x7f0f0089, float:1.9008238E38)
            java.lang.String r1 = "ActionChannelRemovedPhoto"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x04a9:
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x04bc
            r0 = 2131689646(0x7f0f00ae, float:1.9008313E38)
            java.lang.String r1 = "ActionYouRemovedPhoto"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x04bc:
            r0 = 2131689635(0x7f0f00a3, float:1.900829E38)
            java.lang.String r1 = "ActionRemovedPhoto"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x12c7
        L_0x04cd:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionTTLChange
            java.lang.String r1 = "MessageLifetimeYouRemoved"
            r14 = 2131691996(0x7f0f09dc, float:1.901308E38)
            java.lang.String r15 = "MessageLifetimeRemoved"
            if (r0 == 0) goto L_0x0547
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.ttl
            if (r0 == 0) goto L_0x0526
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x0505
            r0 = 2131691994(0x7f0f09da, float:1.9013076E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.ttl
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatTTLString(r2)
            r1[r5] = r2
            java.lang.String r2 = "MessageLifetimeChangedOutgoing"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0505:
            r0 = 2131691993(0x7f0f09d9, float:1.9013074E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = im.bclpbkiauv.messenger.UserObject.getFirstName(r11)
            r1[r5] = r2
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.ttl
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatTTLString(r2)
            r1[r4] = r2
            java.lang.String r2 = "MessageLifetimeChanged"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0526:
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x0537
            r0 = 2131691998(0x7f0f09de, float:1.9013084E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0537:
            java.lang.Object[] r0 = new java.lang.Object[r4]
            java.lang.String r1 = im.bclpbkiauv.messenger.UserObject.getFirstName(r11)
            r0[r5] = r1
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r15, r14, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0547:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionLoginUnknownLocation
            if (r0 == 0) goto L_0x0611
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            int r0 = r0.date
            long r0 = (long) r0
            r13 = 1000(0x3e8, double:4.94E-321)
            long r0 = r0 * r13
            im.bclpbkiauv.messenger.LocaleController r2 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            im.bclpbkiauv.messenger.time.FastDateFormat r2 = r2.formatterDay
            if (r2 == 0) goto L_0x058c
            im.bclpbkiauv.messenger.LocaleController r2 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            im.bclpbkiauv.messenger.time.FastDateFormat r2 = r2.formatterYear
            if (r2 == 0) goto L_0x058c
            r2 = 2131695131(0x7f0f161b, float:1.9019438E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            im.bclpbkiauv.messenger.LocaleController r14 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            im.bclpbkiauv.messenger.time.FastDateFormat r14 = r14.formatterYear
            java.lang.String r14 = r14.format((long) r0)
            r13[r5] = r14
            im.bclpbkiauv.messenger.LocaleController r14 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            im.bclpbkiauv.messenger.time.FastDateFormat r14 = r14.formatterDay
            java.lang.String r14 = r14.format((long) r0)
            r13[r4] = r14
            java.lang.String r14 = "formatDateAtTime"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r14, r2, r13)
            goto L_0x059f
        L_0x058c:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r12)
            im.bclpbkiauv.tgnet.TLRPC$Message r13 = r6.messageOwner
            int r13 = r13.date
            r2.append(r13)
            java.lang.String r2 = r2.toString()
        L_0x059f:
            int r13 = r6.currentAccount
            im.bclpbkiauv.messenger.UserConfig r13 = im.bclpbkiauv.messenger.UserConfig.getInstance(r13)
            im.bclpbkiauv.tgnet.TLRPC$User r13 = r13.getCurrentUser()
            if (r13 != 0) goto L_0x05e4
            if (r7 == 0) goto L_0x05bf
            im.bclpbkiauv.tgnet.TLRPC$Message r14 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r14 = r14.to_id
            int r14 = r14.user_id
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.Object r14 = r7.get(r14)
            r13 = r14
            im.bclpbkiauv.tgnet.TLRPC$User r13 = (im.bclpbkiauv.tgnet.TLRPC.User) r13
            goto L_0x05ce
        L_0x05bf:
            if (r9 == 0) goto L_0x05ce
            im.bclpbkiauv.tgnet.TLRPC$Message r14 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r14 = r14.to_id
            int r14 = r14.user_id
            java.lang.Object r14 = r9.get(r14)
            r13 = r14
            im.bclpbkiauv.tgnet.TLRPC$User r13 = (im.bclpbkiauv.tgnet.TLRPC.User) r13
        L_0x05ce:
            if (r13 != 0) goto L_0x05e4
            int r14 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r14 = im.bclpbkiauv.messenger.MessagesController.getInstance(r14)
            im.bclpbkiauv.tgnet.TLRPC$Message r15 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r15 = r15.to_id
            int r15 = r15.user_id
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            im.bclpbkiauv.tgnet.TLRPC$User r13 = r14.getUser(r15)
        L_0x05e4:
            if (r13 == 0) goto L_0x05eb
            java.lang.String r14 = im.bclpbkiauv.messenger.UserObject.getFirstName(r13)
            goto L_0x05ec
        L_0x05eb:
            r14 = r12
        L_0x05ec:
            r15 = 4
            java.lang.Object[] r15 = new java.lang.Object[r15]
            r15[r5] = r14
            r15[r4] = r2
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            java.lang.String r4 = r4.title
            r15[r3] = r4
            r3 = 3
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            java.lang.String r4 = r4.address
            r15[r3] = r4
            java.lang.String r3 = "NotificationUnrecognizedDevice"
            r4 = 2131692394(0x7f0f0b6a, float:1.9013887E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r4, r15)
            r6.messageText = r3
            goto L_0x12c7
        L_0x0611:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionUserJoined
            if (r0 != 0) goto L_0x0b82
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r0 == 0) goto L_0x0623
            goto L_0x0b82
        L_0x0623:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto
            if (r0 == 0) goto L_0x0640
            r0 = 2131692330(0x7f0f0b2a, float:1.9013757E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = im.bclpbkiauv.messenger.UserObject.getName(r11)
            r1[r5] = r2
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0640:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageEncryptedAction
            if (r0 == 0) goto L_0x06e6
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageAction r0 = r0.encryptedAction
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages
            if (r0 == 0) goto L_0x0678
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x0667
            r0 = 2131689637(0x7f0f00a5, float:1.9008295E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = "ActionTakeScreenshootYou"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0667:
            r0 = 2131689636(0x7f0f00a4, float:1.9008293E38)
            java.lang.String r1 = "ActionTakeScreenshoot"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0678:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageAction r0 = r0.encryptedAction
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL
            if (r0 == 0) goto L_0x12c7
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageAction r0 = r0.encryptedAction
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL) r0
            int r2 = r0.ttl_seconds
            if (r2 == 0) goto L_0x06c6
            boolean r1 = r19.isOut()
            if (r1 == 0) goto L_0x06aa
            r1 = 2131691994(0x7f0f09da, float:1.9013076E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            int r3 = r0.ttl_seconds
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatTTLString(r3)
            r2[r5] = r3
            java.lang.String r3 = "MessageLifetimeChangedOutgoing"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r1, r2)
            r6.messageText = r1
            goto L_0x06e4
        L_0x06aa:
            r1 = 2131691993(0x7f0f09d9, float:1.9013074E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r3 = im.bclpbkiauv.messenger.UserObject.getFirstName(r11)
            r2[r5] = r3
            int r3 = r0.ttl_seconds
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatTTLString(r3)
            r2[r4] = r3
            java.lang.String r3 = "MessageLifetimeChanged"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r1, r2)
            r6.messageText = r1
            goto L_0x06e4
        L_0x06c6:
            boolean r2 = r19.isOut()
            if (r2 == 0) goto L_0x06d6
            r2 = 2131691998(0x7f0f09de, float:1.9013084E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r2)
            r6.messageText = r1
            goto L_0x06e4
        L_0x06d6:
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = im.bclpbkiauv.messenger.UserObject.getFirstName(r11)
            r1[r5] = r2
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r15, r14, r1)
            r6.messageText = r1
        L_0x06e4:
            goto L_0x12c7
        L_0x06e6:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionScreenshotTaken
            if (r0 == 0) goto L_0x0714
            boolean r0 = r19.isOut()
            if (r0 == 0) goto L_0x0703
            r0 = 2131689637(0x7f0f00a5, float:1.9008295E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = "ActionTakeScreenshootYou"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0703:
            r0 = 2131689636(0x7f0f00a4, float:1.9008293E38)
            java.lang.String r1 = "ActionTakeScreenshoot"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = r6.replaceWithLink(r0, r13, r11)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0714:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionCreatedBroadcastList
            if (r0 == 0) goto L_0x072b
            r0 = 2131694851(0x7f0f1503, float:1.901887E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = "YouCreatedBroadcastList"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x12c7
        L_0x072b:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r0 == 0) goto L_0x0753
            boolean r0 = r19.isMegagroup()
            if (r0 == 0) goto L_0x0746
            r0 = 2131689612(0x7f0f008c, float:1.9008244E38)
            java.lang.String r1 = "ActionCreateMega"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0746:
            r0 = 2131689610(0x7f0f008a, float:1.900824E38)
            java.lang.String r1 = "ActionCreateChannel"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0753:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r0 == 0) goto L_0x0768
            r0 = 2131689618(0x7f0f0092, float:1.9008256E38)
            java.lang.String r1 = "ActionMigrateFromGroup"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0768:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x077d
            r0 = 2131689618(0x7f0f0092, float:1.9008256E38)
            java.lang.String r1 = "ActionMigrateFromGroup"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x077d:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPinMessage
            if (r0 == 0) goto L_0x07b1
            if (r11 != 0) goto L_0x07ab
            if (r8 == 0) goto L_0x079a
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            java.lang.Object r0 = r8.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r0
            goto L_0x07ac
        L_0x079a:
            if (r10 == 0) goto L_0x07a9
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            java.lang.Object r0 = r10.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r0
            goto L_0x07ac
        L_0x07a9:
            r0 = 0
            goto L_0x07ac
        L_0x07ab:
            r0 = 0
        L_0x07ac:
            r6.generatePinMessageText(r11, r0)
            goto L_0x12c7
        L_0x07b1:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionLiveStart_layer105
            if (r0 == 0) goto L_0x0819
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            im.bclpbkiauv.tgnet.TLRPC$TL_messageActionLiveStart_layer105 r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageActionLiveStart_layer105) r0
            int r1 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r1)
            int r2 = r0.live_user
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r1 == 0) goto L_0x0817
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r1.first_name
            int r3 = r3.length()
            r4 = 6
            if (r3 <= r4) goto L_0x07f8
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = r1.first_name
            r13 = 6
            java.lang.String r4 = r4.substring(r5, r13)
            r3.append(r4)
            java.lang.String r4 = "..."
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            goto L_0x07fa
        L_0x07f8:
            java.lang.String r3 = r1.first_name
        L_0x07fa:
            r2.append(r3)
            r3 = 2131695356(0x7f0f16fc, float:1.9019895E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
            r2.append(r3)
            java.lang.String r3 = ": "
            r2.append(r3)
            java.lang.String r3 = r0.live_name
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r6.messageText = r2
        L_0x0817:
            goto L_0x12c7
        L_0x0819:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionLiveStop_layer105
            if (r0 == 0) goto L_0x0848
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            im.bclpbkiauv.tgnet.TLRPC$TL_messageActionLiveStop_layer105 r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageActionLiveStop_layer105) r0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 2131695284(0x7f0f16b4, float:1.9019748E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r2)
            r1.append(r2)
            java.lang.String r2 = ": "
            r1.append(r2)
            java.lang.String r2 = r0.live_name
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r6.messageText = r1
            goto L_0x12c7
        L_0x0848:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r0 == 0) goto L_0x085d
            r0 = 2131691583(0x7f0f083f, float:1.9012242E38)
            java.lang.String r1 = "HistoryCleared"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x085d:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionGameScore
            if (r0 == 0) goto L_0x086a
            r6.generateGameMessageText(r11)
            goto L_0x12c7
        L_0x086a:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x0982
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            im.bclpbkiauv.tgnet.TLRPC$TL_messageActionPhoneCall r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPhoneCall) r0
            im.bclpbkiauv.tgnet.TLRPC$PhoneCallDiscardReason r1 = r0.reason
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r6.messageOwner
            int r3 = r3.from_id
            int r4 = r6.currentAccount
            im.bclpbkiauv.messenger.UserConfig r4 = im.bclpbkiauv.messenger.UserConfig.getInstance(r4)
            int r4 = r4.getClientUserId()
            if (r3 != r4) goto L_0x08a6
            if (r1 == 0) goto L_0x089a
            r3 = 2131690291(0x7f0f0333, float:1.9009621E38)
            java.lang.String r4 = "CallMessageOutgoingMissed"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r6.messageText = r3
            goto L_0x08d1
        L_0x089a:
            r3 = 2131690290(0x7f0f0332, float:1.900962E38)
            java.lang.String r4 = "CallMessageOutgoing"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r6.messageText = r3
            goto L_0x08d1
        L_0x08a6:
            if (r1 == 0) goto L_0x08b4
            r3 = 2131690289(0x7f0f0331, float:1.9009617E38)
            java.lang.String r4 = "CallMessageIncomingMissed"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r6.messageText = r3
            goto L_0x08d1
        L_0x08b4:
            im.bclpbkiauv.tgnet.TLRPC$PhoneCallDiscardReason r3 = r0.reason
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy
            if (r3 == 0) goto L_0x08c6
            r3 = 2131690287(0x7f0f032f, float:1.9009613E38)
            java.lang.String r4 = "CallMessageIncomingDeclined"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r6.messageText = r3
            goto L_0x08d1
        L_0x08c6:
            r3 = 2131690286(0x7f0f032e, float:1.9009611E38)
            java.lang.String r4 = "CallMessageIncoming"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r6.messageText = r3
        L_0x08d1:
            int r3 = r0.flags
            r3 = r3 & 4
            if (r3 == 0) goto L_0x0905
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "["
            r3.append(r4)
            r4 = 2131690017(0x7f0f0221, float:1.9009066E38)
            java.lang.String r13 = "AutoDownloadVideosOn"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            r3.append(r4)
            r4 = 2131690303(0x7f0f033f, float:1.9009646E38)
            java.lang.String r13 = "Calls"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            r3.append(r4)
            java.lang.String r4 = "]"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r6.messageText = r3
            goto L_0x0933
        L_0x0905:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "["
            r3.append(r4)
            r4 = 2131695632(0x7f0f1810, float:1.9020454E38)
            java.lang.String r13 = "visual_call_voice"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            r3.append(r4)
            r4 = 2131690303(0x7f0f033f, float:1.9009646E38)
            java.lang.String r13 = "Calls"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r4)
            r3.append(r4)
            java.lang.String r4 = "]"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r6.messageText = r3
        L_0x0933:
            int r3 = r0.duration
            if (r3 <= 0) goto L_0x0980
            int r3 = r0.duration
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatCallDuration(r3)
            java.lang.CharSequence r4 = r6.messageText
            java.lang.String r4 = r4.toString()
            int r13 = r4.indexOf(r3)
            if (r13 == r2) goto L_0x0980
            android.text.SpannableString r2 = new android.text.SpannableString
            java.lang.CharSequence r14 = r6.messageText
            r2.<init>(r14)
            int r14 = r3.length()
            int r14 = r14 + r13
            if (r13 <= 0) goto L_0x0963
            int r15 = r13 + -1
            char r15 = r4.charAt(r15)
            r5 = 40
            if (r15 != r5) goto L_0x0963
            int r13 = r13 + -1
        L_0x0963:
            int r5 = r4.length()
            if (r14 >= r5) goto L_0x0973
            char r5 = r4.charAt(r14)
            r15 = 41
            if (r5 != r15) goto L_0x0973
            int r14 = r14 + 1
        L_0x0973:
            im.bclpbkiauv.ui.components.TypefaceSpan r5 = new im.bclpbkiauv.ui.components.TypefaceSpan
            android.graphics.Typeface r15 = android.graphics.Typeface.DEFAULT
            r5.<init>(r15)
            r15 = 0
            r2.setSpan(r5, r13, r14, r15)
            r6.messageText = r2
        L_0x0980:
            goto L_0x12c7
        L_0x0982:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPaymentSent
            if (r0 == 0) goto L_0x09bc
            r0 = 0
            long r1 = r19.getDialogId()
            int r2 = (int) r1
            if (r7 == 0) goto L_0x099e
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            java.lang.Object r1 = r7.get(r1)
            r0 = r1
            im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
            goto L_0x09a7
        L_0x099e:
            if (r9 == 0) goto L_0x09a7
            java.lang.Object r1 = r9.get(r2)
            r0 = r1
            im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
        L_0x09a7:
            if (r0 != 0) goto L_0x09b7
            int r1 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.getUser(r3)
        L_0x09b7:
            r6.generatePaymentSentMessageText(r0)
            goto L_0x12c7
        L_0x09bc:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionBotAllowed
            if (r0 == 0) goto L_0x0a0f
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            im.bclpbkiauv.tgnet.TLRPC$TL_messageActionBotAllowed r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageActionBotAllowed) r0
            java.lang.String r0 = r0.domain
            r1 = 2131689589(0x7f0f0075, float:1.9008198E38)
            java.lang.String r2 = "ActionBotAllowed"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = "%1$s"
            int r2 = r1.indexOf(r2)
            android.text.SpannableString r3 = new android.text.SpannableString
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r0
            java.lang.String r4 = java.lang.String.format(r1, r4)
            r3.<init>(r4)
            if (r2 < 0) goto L_0x0a0b
            im.bclpbkiauv.ui.components.URLSpanNoUnderlineBold r4 = new im.bclpbkiauv.ui.components.URLSpanNoUnderlineBold
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r13 = "http://"
            r5.append(r13)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            r4.<init>(r5)
            int r5 = r0.length()
            int r5 = r5 + r2
            r13 = 33
            r3.setSpan(r4, r2, r5, r13)
        L_0x0a0b:
            r6.messageText = r3
            goto L_0x0b80
        L_0x0a0f:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionSecureValuesSent
            if (r0 == 0) goto L_0x0b80
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            im.bclpbkiauv.tgnet.TLRPC$TL_messageActionSecureValuesSent r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageActionSecureValuesSent) r0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureValueType> r5 = r0.types
            int r5 = r5.size()
        L_0x0a29:
            if (r2 >= r5) goto L_0x0b24
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureValueType> r13 = r0.types
            java.lang.Object r13 = r13.get(r2)
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r13 = (im.bclpbkiauv.tgnet.TLRPC.SecureValueType) r13
            int r14 = r1.length()
            if (r14 <= 0) goto L_0x0a3e
            java.lang.String r14 = ", "
            r1.append(r14)
        L_0x0a3e:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePhone
            if (r14 == 0) goto L_0x0a50
            r14 = 2131689599(0x7f0f007f, float:1.9008218E38)
            java.lang.String r15 = "ActionBotDocumentPhone"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0a50:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeEmail
            if (r14 == 0) goto L_0x0a62
            r14 = 2131689593(0x7f0f0079, float:1.9008206E38)
            java.lang.String r15 = "ActionBotDocumentEmail"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0a62:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeAddress
            if (r14 == 0) goto L_0x0a74
            r14 = 2131689590(0x7f0f0076, float:1.90082E38)
            java.lang.String r15 = "ActionBotDocumentAddress"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0a74:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePersonalDetails
            if (r14 == 0) goto L_0x0a86
            r14 = 2131689594(0x7f0f007a, float:1.9008208E38)
            java.lang.String r15 = "ActionBotDocumentIdentity"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0a86:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePassport
            if (r14 == 0) goto L_0x0a98
            r14 = 2131689597(0x7f0f007d, float:1.9008214E38)
            java.lang.String r15 = "ActionBotDocumentPassport"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0a98:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeDriverLicense
            if (r14 == 0) goto L_0x0aaa
            r14 = 2131689592(0x7f0f0078, float:1.9008204E38)
            java.lang.String r15 = "ActionBotDocumentDriverLicence"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0aaa:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeIdentityCard
            if (r14 == 0) goto L_0x0abb
            r14 = 2131689595(0x7f0f007b, float:1.900821E38)
            java.lang.String r15 = "ActionBotDocumentIdentityCard"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0abb:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeUtilityBill
            if (r14 == 0) goto L_0x0acc
            r14 = 2131689603(0x7f0f0083, float:1.9008226E38)
            java.lang.String r15 = "ActionBotDocumentUtilityBill"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0acc:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeBankStatement
            if (r14 == 0) goto L_0x0add
            r14 = 2131689591(0x7f0f0077, float:1.9008202E38)
            java.lang.String r15 = "ActionBotDocumentBankStatement"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0add:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeRentalAgreement
            if (r14 == 0) goto L_0x0aee
            r14 = 2131689601(0x7f0f0081, float:1.9008222E38)
            java.lang.String r15 = "ActionBotDocumentRentalAgreement"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0aee:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeInternalPassport
            if (r14 == 0) goto L_0x0aff
            r14 = 2131689596(0x7f0f007c, float:1.9008212E38)
            java.lang.String r15 = "ActionBotDocumentInternalPassport"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0aff:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePassportRegistration
            if (r14 == 0) goto L_0x0b10
            r14 = 2131689598(0x7f0f007e, float:1.9008216E38)
            java.lang.String r15 = "ActionBotDocumentPassportRegistration"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
            goto L_0x0b20
        L_0x0b10:
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeTemporaryRegistration
            if (r14 == 0) goto L_0x0b20
            r14 = 2131689602(0x7f0f0082, float:1.9008224E38)
            java.lang.String r15 = "ActionBotDocumentTemporaryRegistration"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r1.append(r14)
        L_0x0b20:
            int r2 = r2 + 1
            goto L_0x0a29
        L_0x0b24:
            r2 = 0
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id
            if (r5 == 0) goto L_0x0b64
            if (r7 == 0) goto L_0x0b3f
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id
            int r5 = r5.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            java.lang.Object r5 = r7.get(r5)
            r2 = r5
            im.bclpbkiauv.tgnet.TLRPC$User r2 = (im.bclpbkiauv.tgnet.TLRPC.User) r2
            goto L_0x0b4e
        L_0x0b3f:
            if (r9 == 0) goto L_0x0b4e
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id
            int r5 = r5.user_id
            java.lang.Object r5 = r9.get(r5)
            r2 = r5
            im.bclpbkiauv.tgnet.TLRPC$User r2 = (im.bclpbkiauv.tgnet.TLRPC.User) r2
        L_0x0b4e:
            if (r2 != 0) goto L_0x0b64
            int r5 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            im.bclpbkiauv.tgnet.TLRPC$Message r13 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r13 = r13.to_id
            int r13 = r13.user_id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r5.getUser(r13)
        L_0x0b64:
            r5 = 2131689604(0x7f0f0084, float:1.9008228E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r13 = im.bclpbkiauv.messenger.UserObject.getFirstName(r2)
            r14 = 0
            r3[r14] = r13
            java.lang.String r13 = r1.toString()
            r3[r4] = r13
            java.lang.String r4 = "ActionBotDocuments"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r5, r3)
            r6.messageText = r3
            goto L_0x12c7
        L_0x0b80:
            goto L_0x12c7
        L_0x0b82:
            r0 = 2131692329(0x7f0f0b29, float:1.9013755E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = im.bclpbkiauv.messenger.UserObject.getName(r11)
            r5 = 0
            r1[r5] = r2
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x12c7
        L_0x0b98:
            r6.isRestrictedMessage = r5
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            java.lang.String r0 = im.bclpbkiauv.messenger.MessagesController.getRestrictionReason(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x0bac
            r6.messageText = r0
            r6.isRestrictedMessage = r4
            goto L_0x12c7
        L_0x0bac:
            boolean r1 = r19.isMediaEmpty()
            if (r1 != 0) goto L_0x12bf
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll
            if (r1 == 0) goto L_0x0bc7
            r1 = 2131693130(0x7f0f0e4a, float:1.901538E38)
            java.lang.String r2 = "Poll"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x12c7
        L_0x0bc7:
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r1 == 0) goto L_0x0bf7
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            int r1 = r1.ttl_seconds
            if (r1 == 0) goto L_0x0bea
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_message_secret
            if (r1 != 0) goto L_0x0bea
            r1 = 2131689942(0x7f0f01d6, float:1.9008914E38)
            java.lang.String r2 = "AttachDestructingPhoto"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x12c7
        L_0x0bea:
            r1 = 2131689957(0x7f0f01e5, float:1.9008944E38)
            java.lang.String r2 = "AttachPhoto"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x12c7
        L_0x0bf7:
            boolean r1 = r19.isVideo()
            if (r1 != 0) goto L_0x1297
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r1 == 0) goto L_0x0c19
            im.bclpbkiauv.tgnet.TLRPC$Document r1 = r19.getDocument()
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentEmpty
            if (r1 == 0) goto L_0x0c19
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            int r1 = r1.ttl_seconds
            if (r1 == 0) goto L_0x0c19
            r17 = r0
            goto L_0x1299
        L_0x0c19:
            boolean r1 = r19.isVoice()
            if (r1 == 0) goto L_0x0c2c
            r1 = 2131689937(0x7f0f01d1, float:1.9008903E38)
            java.lang.String r2 = "AttachAudio"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x12c7
        L_0x0c2c:
            boolean r1 = r19.isRoundVideo()
            if (r1 == 0) goto L_0x0c3f
            r1 = 2131689959(0x7f0f01e7, float:1.9008948E38)
            java.lang.String r2 = "AttachRound"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x12c7
        L_0x0c3f:
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo
            if (r1 != 0) goto L_0x1289
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue
            if (r1 == 0) goto L_0x0c53
            r17 = r0
            goto L_0x128b
        L_0x0c53:
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r1 == 0) goto L_0x0c68
            r1 = 2131689951(0x7f0f01df, float:1.9008932E38)
            java.lang.String r2 = "AttachLiveLocation"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x12c7
        L_0x0c68:
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact
            if (r1 == 0) goto L_0x0c95
            r1 = 2131689939(0x7f0f01d3, float:1.9008908E38)
            java.lang.String r2 = "AttachContact"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            java.lang.String r1 = r1.vcard
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x12c7
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            java.lang.String r1 = r1.vcard
            java.lang.CharSequence r1 = im.bclpbkiauv.messenger.MessageObject.VCardData.parse(r1)
            r6.vCardData = r1
            goto L_0x12c7
        L_0x0c95:
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r1 == 0) goto L_0x0ca5
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            java.lang.String r1 = r1.message
            r6.messageText = r1
            goto L_0x12c7
        L_0x0ca5:
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaInvoice
            if (r1 == 0) goto L_0x0cb7
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            java.lang.String r1 = r1.description
            r6.messageText = r1
            goto L_0x12c7
        L_0x0cb7:
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaUnsupported
            if (r1 == 0) goto L_0x0ccc
            r1 = 2131694497(0x7f0f13a1, float:1.9018152E38)
            java.lang.String r2 = "UnsupportedMedia"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x12c7
        L_0x0ccc:
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r1 == 0) goto L_0x0d59
            boolean r1 = r19.isSticker()
            if (r1 != 0) goto L_0x0d27
            boolean r1 = r19.isAnimatedSticker()
            if (r1 == 0) goto L_0x0ce1
            goto L_0x0d27
        L_0x0ce1:
            boolean r1 = r19.isMusic()
            if (r1 == 0) goto L_0x0cf4
            r1 = 2131689956(0x7f0f01e4, float:1.9008942E38)
            java.lang.String r2 = "AttachMusic"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x12c7
        L_0x0cf4:
            boolean r1 = r19.isGif()
            if (r1 == 0) goto L_0x0d07
            r1 = 2131689946(0x7f0f01da, float:1.9008922E38)
            java.lang.String r2 = "AttachGif"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x12c7
        L_0x0d07:
            im.bclpbkiauv.tgnet.TLRPC$Document r1 = r19.getDocument()
            java.lang.String r1 = im.bclpbkiauv.messenger.FileLoader.getDocumentFileName(r1)
            if (r1 == 0) goto L_0x0d1a
            int r2 = r1.length()
            if (r2 <= 0) goto L_0x0d1a
            r6.messageText = r1
            goto L_0x0d25
        L_0x0d1a:
            r2 = 2131689944(0x7f0f01d8, float:1.9008918E38)
            java.lang.String r3 = "AttachDocument"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r6.messageText = r2
        L_0x0d25:
            goto L_0x12c7
        L_0x0d27:
            java.lang.String r1 = r19.getStrickerChar()
            if (r1 == 0) goto L_0x0d4c
            int r2 = r1.length()
            if (r2 <= 0) goto L_0x0d4c
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r3 = 0
            r2[r3] = r1
            r3 = 2131689960(0x7f0f01e8, float:1.900895E38)
            java.lang.String r5 = "AttachSticker"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r2[r4] = r3
            java.lang.String r3 = "%s %s"
            java.lang.String r2 = java.lang.String.format(r3, r2)
            r6.messageText = r2
            goto L_0x0d57
        L_0x0d4c:
            r2 = 2131689960(0x7f0f01e8, float:1.900895E38)
            java.lang.String r3 = "AttachSticker"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r6.messageText = r2
        L_0x0d57:
            goto L_0x12c7
        L_0x0d59:
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesPayBillOverMedia
            if (r1 == 0) goto L_0x103c
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            im.bclpbkiauv.tgnet.TLRPCRedpacket$CL_messagesPayBillOverMedia r1 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesPayBillOverMedia) r1
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r3 = r1.data
            java.lang.String r3 = im.bclpbkiauv.tgnet.TLJsonResolve.getData(r3)
            java.lang.Class<im.bclpbkiauv.javaBean.PayBillOverBean> r5 = im.bclpbkiauv.javaBean.PayBillOverBean.class
            java.lang.Object r5 = com.blankj.utilcode.util.GsonUtils.fromJson((java.lang.String) r3, r5)
            im.bclpbkiauv.javaBean.PayBillOverBean r5 = (im.bclpbkiauv.javaBean.PayBillOverBean) r5
            java.lang.String r13 = ""
            java.lang.String r14 = ""
            java.lang.String r15 = r5.coin_code
            java.lang.String r2 = "-"
            boolean r2 = r15.contains(r2)
            if (r2 == 0) goto L_0x0d8f
            java.lang.String r2 = r5.coin_code
            java.lang.String r15 = "-"
            java.lang.String[] r2 = r2.split(r15)
            r15 = 0
            r2 = r2[r15]
            goto L_0x0d91
        L_0x0d8f:
            java.lang.String r2 = r5.coin_code
        L_0x0d91:
            int r14 = r1.deal_code
            java.lang.String r4 = "PayBillClickToView"
            java.lang.String r15 = "PayBillA"
            java.lang.String r16 = "0"
            switch(r14) {
                case 1: goto L_0x0ff7;
                case 2: goto L_0x0fb4;
                case 3: goto L_0x0ff7;
                case 4: goto L_0x0ff7;
                case 5: goto L_0x0f70;
                case 6: goto L_0x0ff7;
                case 7: goto L_0x0fb4;
                case 8: goto L_0x0eb4;
                case 9: goto L_0x0e6e;
                case 10: goto L_0x0e26;
                case 11: goto L_0x0de4;
                case 12: goto L_0x0da2;
                default: goto L_0x0d9c;
            }
        L_0x0d9c:
            r17 = r0
            r18 = r1
            goto L_0x1038
        L_0x0da2:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r17 = r0
            r0 = 2131692840(0x7f0f0d28, float:1.9014791E38)
            r18 = r1
            java.lang.String r1 = "PayBillFiatCurrencySell2"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r14.append(r0)
            java.lang.String r0 = r5.deal_num
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0dc2
            r0 = r16
            goto L_0x0dc4
        L_0x0dc2:
            java.lang.String r0 = r5.deal_num
        L_0x0dc4:
            r14.append(r0)
            r0 = 2131692806(0x7f0f0d06, float:1.9014722E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r0)
            r14.append(r0)
            r14.append(r2)
            r0 = 2131692815(0x7f0f0d0f, float:1.901474E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r0)
            r14.append(r0)
            java.lang.String r13 = r14.toString()
            goto L_0x1038
        L_0x0de4:
            r17 = r0
            r18 = r1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 2131692836(0x7f0f0d24, float:1.9014783E38)
            java.lang.String r14 = "PayBillFiatCurrencyBuy2"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r1)
            r0.append(r1)
            java.lang.String r1 = r5.deal_num
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0e04
            r1 = r16
            goto L_0x0e06
        L_0x0e04:
            java.lang.String r1 = r5.deal_num
        L_0x0e06:
            r0.append(r1)
            r1 = 2131692806(0x7f0f0d06, float:1.9014722E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r1)
            r0.append(r1)
            r0.append(r2)
            r1 = 2131692815(0x7f0f0d0f, float:1.901474E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r13 = r0.toString()
            goto L_0x1038
        L_0x0e26:
            r17 = r0
            r18 = r1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 2131692820(0x7f0f0d14, float:1.901475E38)
            java.lang.String r4 = "PayBillCommissionReturn2"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r1 = r5.deal_amount
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0e46
            r1 = r16
            goto L_0x0e48
        L_0x0e46:
            java.lang.String r1 = r5.deal_amount
        L_0x0e48:
            java.lang.String r1 = r6.setMoneyFormat(r1)
            r0.append(r1)
            r1 = 2131692806(0x7f0f0d06, float:1.9014722E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r1)
            r0.append(r1)
            r0.append(r2)
            r1 = 2131692857(0x7f0f0d39, float:1.9014826E38)
            java.lang.String r4 = "PayBillReturnClickToView"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r13 = r0.toString()
            goto L_0x1038
        L_0x0e6e:
            r17 = r0
            r18 = r1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 2131692822(0x7f0f0d16, float:1.9014755E38)
            java.lang.String r14 = "PayBillCommissionSell2"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r1)
            r0.append(r1)
            java.lang.String r1 = r5.deal_num
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0e8e
            r1 = r16
            goto L_0x0e90
        L_0x0e8e:
            java.lang.String r1 = r5.deal_num
        L_0x0e90:
            java.lang.String r1 = r6.setMoneyFormat(r1)
            r0.append(r1)
            r1 = 2131692806(0x7f0f0d06, float:1.9014722E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r1)
            r0.append(r1)
            r0.append(r2)
            r1 = 2131692815(0x7f0f0d0f, float:1.901474E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r13 = r0.toString()
            goto L_0x1038
        L_0x0eb4:
            r17 = r0
            r18 = r1
            java.lang.String r0 = r5.deal_type
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x1038
            java.lang.String r0 = r5.deal_type
            int r1 = r0.hashCode()
            r14 = 49
            if (r1 == r14) goto L_0x0ed9
            r14 = 50
            if (r1 == r14) goto L_0x0ecf
        L_0x0ece:
            goto L_0x0ee3
        L_0x0ecf:
            java.lang.String r1 = "2"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0ece
            r0 = 1
            goto L_0x0ee4
        L_0x0ed9:
            java.lang.String r1 = "1"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0ece
            r0 = 0
            goto L_0x0ee4
        L_0x0ee3:
            r0 = -1
        L_0x0ee4:
            if (r0 == 0) goto L_0x0f2d
            r1 = 1
            if (r0 == r1) goto L_0x0eeb
            goto L_0x0f6e
        L_0x0eeb:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 2131692818(0x7f0f0d12, float:1.9014747E38)
            java.lang.String r14 = "PayBillCommissionBuy2"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r1)
            r0.append(r1)
            java.lang.String r1 = r5.deal_num
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0f07
            r1 = r16
            goto L_0x0f09
        L_0x0f07:
            java.lang.String r1 = r5.deal_num
        L_0x0f09:
            java.lang.String r1 = r6.setMoneyFormat(r1)
            r0.append(r1)
            r1 = 2131692806(0x7f0f0d06, float:1.9014722E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r1)
            r0.append(r1)
            r0.append(r2)
            r1 = 2131692815(0x7f0f0d0f, float:1.901474E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r13 = r0
            goto L_0x0f6e
        L_0x0f2d:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 2131692818(0x7f0f0d12, float:1.9014747E38)
            java.lang.String r14 = "PayBillCommissionBuy2"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r1)
            r0.append(r1)
            java.lang.String r1 = r5.deal_amount
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0f49
            r1 = r16
            goto L_0x0f4b
        L_0x0f49:
            java.lang.String r1 = r5.deal_amount
        L_0x0f4b:
            java.lang.String r1 = r6.setMoneyFormat(r1)
            r0.append(r1)
            r1 = 2131692806(0x7f0f0d06, float:1.9014722E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r1)
            r0.append(r1)
            r0.append(r2)
            r1 = 2131692815(0x7f0f0d0f, float:1.901474E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r13 = r0
        L_0x0f6e:
            goto L_0x1038
        L_0x0f70:
            r17 = r0
            r18 = r1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 2131692863(0x7f0f0d3f, float:1.9014838E38)
            java.lang.String r4 = "PayBillRogerThat"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r1 = r5.deal_amount
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0f90
            r1 = r16
            goto L_0x0f92
        L_0x0f90:
            java.lang.String r1 = r5.deal_amount
        L_0x0f92:
            r0.append(r1)
            r1 = 2131692806(0x7f0f0d06, float:1.9014722E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r1)
            r0.append(r1)
            r0.append(r2)
            r1 = 2131692857(0x7f0f0d39, float:1.9014826E38)
            java.lang.String r4 = "PayBillReturnClickToView"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r13 = r0.toString()
            goto L_0x1038
        L_0x0fb4:
            r17 = r0
            r18 = r1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 2131692863(0x7f0f0d3f, float:1.9014838E38)
            java.lang.String r4 = "PayBillRogerThat"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r1 = r5.deal_amount
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0fd4
            r1 = r16
            goto L_0x0fd6
        L_0x0fd4:
            java.lang.String r1 = r5.deal_amount
        L_0x0fd6:
            r0.append(r1)
            r1 = 2131692806(0x7f0f0d06, float:1.9014722E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r1)
            r0.append(r1)
            r0.append(r2)
            r1 = 2131692881(0x7f0f0d51, float:1.9014875E38)
            java.lang.String r4 = "PayBillTransferInView"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r13 = r0.toString()
            goto L_0x1038
        L_0x0ff7:
            r17 = r0
            r18 = r1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 2131692870(0x7f0f0d46, float:1.9014852E38)
            java.lang.String r14 = "PayBillSuccessfullyTransferredOut"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r1)
            r0.append(r1)
            java.lang.String r1 = r5.deal_amount
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x1017
            r1 = r16
            goto L_0x1019
        L_0x1017:
            java.lang.String r1 = r5.deal_amount
        L_0x1019:
            r0.append(r1)
            r1 = 2131692806(0x7f0f0d06, float:1.9014722E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r1)
            r0.append(r1)
            r0.append(r2)
            r1 = 2131692815(0x7f0f0d0f, float:1.901474E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
            r0.append(r1)
            java.lang.String r13 = r0.toString()
        L_0x1038:
            r6.messageText = r13
            goto L_0x12c7
        L_0x103c:
            r17 = r0
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesRpkTransferMedia
            if (r0 == 0) goto L_0x11ae
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            im.bclpbkiauv.tgnet.TLRPCRedpacket$CL_messagesRpkTransferMedia r0 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesRpkTransferMedia) r0
            int r1 = r0.trans
            if (r1 != 0) goto L_0x10a3
            r1 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r2 = r0.data
            if (r2 == 0) goto L_0x1062
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r2 = r0.data
            java.lang.Class<im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse> r3 = im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse.class
            im.bclpbkiauv.tgnet.TLApiModel r2 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r2, (java.lang.Class<?>) r3)
            T r3 = r2.model
            r1 = r3
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse r1 = (im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse) r1
        L_0x1062:
            if (r1 == 0) goto L_0x1099
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketBean r2 = r1.getRed()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 2131693367(0x7f0f0f37, float:1.901586E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r3.append(r4)
            java.lang.String r4 = r2.getRemarks()
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x108b
            r4 = 2131695484(0x7f0f177c, float:1.9020154E38)
            java.lang.String r5 = "redpacket_greetings_tip"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            goto L_0x108f
        L_0x108b:
            java.lang.String r4 = r2.getRemarks()
        L_0x108f:
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r6.messageText = r3
            goto L_0x10ad
        L_0x1099:
            r2 = 2131693359(0x7f0f0f2f, float:1.9015844E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r2)
            r6.messageText = r2
            goto L_0x10ad
        L_0x10a3:
            int r1 = r0.trans
            r2 = 1
            if (r1 == r2) goto L_0x10af
            int r1 = r0.trans
            if (r1 != r3) goto L_0x10ad
            goto L_0x10af
        L_0x10ad:
            goto L_0x11ac
        L_0x10af:
            r1 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r2 = r0.data
            if (r2 == 0) goto L_0x10c1
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r2 = r0.data
            java.lang.Class<im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse> r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.class
            im.bclpbkiauv.tgnet.TLApiModel r2 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r2, (java.lang.Class<?>) r3)
            T r3 = r2.model
            r1 = r3
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse r1 = (im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse) r1
        L_0x10c1:
            if (r1 == 0) goto L_0x11a3
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r2 = r1.getState()
            boolean r3 = r19.isOutOwner()
            if (r3 == 0) goto L_0x1139
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.WAITING
            if (r2 != r3) goto L_0x10dc
            r3 = 2131694417(0x7f0f1351, float:1.901799E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
            r6.messageText = r3
            goto L_0x11a2
        L_0x10dc:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.RECEIVED
            if (r2 != r3) goto L_0x1103
            int r3 = r1.getInitiatorUserIdInt()
            int r4 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r4 = im.bclpbkiauv.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            if (r3 != r4) goto L_0x10f8
            r4 = 2131694364(0x7f0f131c, float:1.9017882E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r6.messageText = r4
            goto L_0x1101
        L_0x10f8:
            r4 = 2131694856(0x7f0f1508, float:1.901888E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r6.messageText = r4
        L_0x1101:
            goto L_0x11a2
        L_0x1103:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.REFUSED
            if (r2 != r3) goto L_0x1129
            int r3 = r1.getInitiatorUserIdInt()
            int r4 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r4 = im.bclpbkiauv.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            if (r3 != r4) goto L_0x111f
            r4 = 2131694356(0x7f0f1314, float:1.9017866E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r6.messageText = r4
            goto L_0x1138
        L_0x111f:
            r4 = 2131694860(0x7f0f150c, float:1.9018889E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r6.messageText = r4
            goto L_0x1138
        L_0x1129:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.TIMEOUT
            if (r2 != r3) goto L_0x1138
            r3 = 2131694357(0x7f0f1315, float:1.9017868E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
            r6.messageText = r3
            goto L_0x11a2
        L_0x1138:
            goto L_0x11a2
        L_0x1139:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.WAITING
            if (r2 != r3) goto L_0x1147
            r3 = 2131694376(0x7f0f1328, float:1.9017907E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
            r6.messageText = r3
            goto L_0x11a2
        L_0x1147:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.RECEIVED
            if (r2 != r3) goto L_0x116d
            int r3 = r1.getInitiatorUserIdInt()
            int r4 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r4 = im.bclpbkiauv.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            if (r3 != r4) goto L_0x1163
            r4 = 2131694364(0x7f0f131c, float:1.9017882E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r6.messageText = r4
            goto L_0x116c
        L_0x1163:
            r4 = 2131694856(0x7f0f1508, float:1.901888E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r6.messageText = r4
        L_0x116c:
            goto L_0x11a2
        L_0x116d:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.REFUSED
            if (r2 != r3) goto L_0x1193
            int r3 = r1.getInitiatorUserIdInt()
            int r4 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r4 = im.bclpbkiauv.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            if (r3 != r4) goto L_0x1189
            r4 = 2131694356(0x7f0f1314, float:1.9017866E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r6.messageText = r4
            goto L_0x11a1
        L_0x1189:
            r4 = 2131694860(0x7f0f150c, float:1.9018889E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r6.messageText = r4
            goto L_0x11a1
        L_0x1193:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.TIMEOUT
            if (r2 != r3) goto L_0x11a1
            r3 = 2131694357(0x7f0f1315, float:1.9017868E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
            r6.messageText = r3
            goto L_0x11a2
        L_0x11a1:
        L_0x11a2:
            goto L_0x11ac
        L_0x11a3:
            r2 = 2131694363(0x7f0f131b, float:1.901788E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r2)
            r6.messageText = r2
        L_0x11ac:
            goto L_0x12c7
        L_0x11ae:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaShareContact
            if (r0 == 0) goto L_0x1282
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaShareContact r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaShareContact) r0
            int r1 = r6.currentAccount
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r1)
            int r2 = r0.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$User r1 = r1.getUser(r2)
            java.lang.String r2 = "%s"
            if (r1 == 0) goto L_0x1221
            boolean r3 = r19.isOutOwner()
            if (r3 == 0) goto L_0x11fc
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 2131695532(0x7f0f17ac, float:1.9020251E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r3.append(r4)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r1.first_name
            r5 = 0
            r3[r5] = r4
            java.lang.String r2 = java.lang.String.format(r2, r3)
            r6.messageText = r2
            goto L_0x1288
        L_0x11fc:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 2131695533(0x7f0f17ad, float:1.9020254E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r3.append(r4)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r1.first_name
            r5 = 0
            r3[r5] = r4
            java.lang.String r2 = java.lang.String.format(r2, r3)
            r6.messageText = r2
            goto L_0x1288
        L_0x1221:
            if (r7 == 0) goto L_0x1288
            int r3 = r0.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            java.lang.Object r3 = r7.get(r3)
            r1 = r3
            im.bclpbkiauv.tgnet.TLRPC$User r1 = (im.bclpbkiauv.tgnet.TLRPC.User) r1
            if (r1 == 0) goto L_0x1288
            boolean r3 = r19.isOutOwner()
            if (r3 == 0) goto L_0x125d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 2131695532(0x7f0f17ac, float:1.9020251E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r3.append(r4)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r1.first_name
            r5 = 0
            r3[r5] = r4
            java.lang.String r2 = java.lang.String.format(r2, r3)
            r6.messageText = r2
            goto L_0x1288
        L_0x125d:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 2131695533(0x7f0f17ad, float:1.9020254E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r3.append(r4)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r1.first_name
            r5 = 0
            r3[r5] = r4
            java.lang.String r2 = java.lang.String.format(r2, r3)
            r6.messageText = r2
            goto L_0x1288
        L_0x1282:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaShare
        L_0x1288:
            goto L_0x12c7
        L_0x1289:
            r17 = r0
        L_0x128b:
            r0 = 2131689953(0x7f0f01e1, float:1.9008936E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x1297:
            r17 = r0
        L_0x1299:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x12b3
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_message_secret
            if (r0 != 0) goto L_0x12b3
            r0 = 2131689943(0x7f0f01d7, float:1.9008916E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x12b3:
            r0 = 2131689963(0x7f0f01eb, float:1.9008956E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x12c7
        L_0x12bf:
            r17 = r0
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r6.messageOwner
            java.lang.String r0 = r0.message
            r6.messageText = r0
        L_0x12c7:
            java.lang.CharSequence r0 = r6.messageText
            if (r0 != 0) goto L_0x12cd
            r6.messageText = r12
        L_0x12cd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MessageObject.updateMessageText(java.util.AbstractMap, java.util.AbstractMap, android.util.SparseArray, android.util.SparseArray):void");
    }

    public void setType() {
        int oldType = this.type;
        this.isRoundVideoCached = 0;
        TLRPC.Message message = this.messageOwner;
        if ((message instanceof TLRPC.TL_message) || (message instanceof TLRPC.TL_messageForwarded_old2)) {
            if (this.isRestrictedMessage) {
                this.type = 0;
            } else if (this.emojiAnimatedSticker != null) {
                if (isSticker()) {
                    this.type = 13;
                } else {
                    this.type = 15;
                }
            } else if (isMediaEmpty()) {
                this.type = 0;
                if (TextUtils.isEmpty(this.messageText) && this.eventId == 0) {
                    this.messageText = "Empty message";
                }
            } else if (this.messageOwner.media.ttl_seconds != 0 && ((this.messageOwner.media.photo instanceof TLRPC.TL_photoEmpty) || (getDocument() instanceof TLRPC.TL_documentEmpty))) {
                this.contentType = 1;
                this.type = 10;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
                this.type = 1;
            } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaVenue) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive)) {
                this.type = 4;
            } else if (isRoundVideo()) {
                this.type = 5;
            } else if (isVideo()) {
                this.type = 3;
            } else if (isVoice()) {
                this.type = 2;
            } else if (isMusic()) {
                this.type = 14;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaContact) {
                this.type = 12;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPoll) {
                this.type = 17;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaUnsupported) {
                this.type = 0;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
                TLRPC.Document document = getDocument();
                if (document == null || document.mime_type == null) {
                    this.type = 9;
                } else if (isGifDocument(document)) {
                    this.type = 8;
                } else if (isSticker()) {
                    this.type = 13;
                } else if (isAnimatedSticker()) {
                    this.type = 15;
                } else {
                    this.type = 9;
                }
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                this.type = 0;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
                this.type = 0;
            } else if (this.messageOwner.media instanceof TLRPCRedpacket.CL_messagesRpkTransferMedia) {
                TLRPCRedpacket.CL_messagesRpkTransferMedia media = (TLRPCRedpacket.CL_messagesRpkTransferMedia) this.messageOwner.media;
                if (media.trans == 0) {
                    this.type = 101;
                } else if (media.trans == 1 || media.trans == 2) {
                    this.type = 102;
                }
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaShareContact) {
                this.type = 103;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaShare) {
                this.type = TYPE_LIVE;
            } else if (this.messageOwner.media instanceof TLRPCRedpacket.CL_messagesPayBillOverMedia) {
                this.contentType = 5;
                this.type = 104;
            } else if (this.messageOwner.media instanceof TLRPCContacts.TL_messageMediaSysNotify) {
                this.type = 105;
            }
        } else if (message instanceof TLRPC.TL_messageService) {
            if (message.action instanceof TLRPC.TL_messageActionLoginUnknownLocation) {
                this.type = 0;
            } else if ((this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto) || (this.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto)) {
                this.contentType = 1;
                this.type = 11;
            } else if (this.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction) {
                if ((this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
                    this.contentType = 1;
                    this.type = 10;
                } else {
                    this.contentType = -1;
                    this.type = -1;
                }
            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear) {
                this.contentType = -1;
                this.type = -1;
            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                this.type = 16;
            } else {
                this.contentType = 1;
                this.type = 10;
            }
        }
        if (oldType != 1000 && oldType != this.type) {
            updateMessageText(MessagesController.getInstance(this.currentAccount).getUsers(), MessagesController.getInstance(this.currentAccount).getChats(), (SparseArray<TLRPC.User>) null, (SparseArray<TLRPC.Chat>) null);
            if (isMediaEmpty()) {
                this.messageText = updateMetionText(this.messageText, this.messageOwner.entities);
            }
            generateThumbs(false);
        }
    }

    public boolean checkLayout() {
        CharSequence charSequence;
        TextPaint paint;
        if (this.type != 0 || this.messageOwner.to_id == null || (charSequence = this.messageText) == null || charSequence.length() == 0) {
            return false;
        }
        if (this.layoutCreated) {
            if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f) || this.generatedWithDensity != AndroidUtilities.density) {
                this.layoutCreated = false;
            }
        }
        if (this.layoutCreated != 0) {
            return false;
        }
        this.layoutCreated = true;
        TLRPC.User fromUser = null;
        if (isFromUser()) {
            fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
            paint = Theme.chat_msgGameTextPaint;
        } else {
            paint = Theme.chat_msgTextPaint;
        }
        int[] emojiOnly = SharedConfig.allowBigEmoji ? new int[1] : null;
        this.messageText = Emoji.replaceEmoji(this.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly);
        checkEmojiOnly(emojiOnly);
        generateLayout(fromUser);
        return true;
    }

    public void resetLayout() {
        this.layoutCreated = false;
    }

    public String getMimeType() {
        TLRPC.Document document = getDocument();
        if (document != null) {
            return document.mime_type;
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
            TLRPC.WebDocument photo = ((TLRPC.TL_messageMediaInvoice) this.messageOwner.media).photo;
            if (photo != null) {
                return photo.mime_type;
            }
            return "";
        } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            return "image/jpeg";
        } else {
            if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || this.messageOwner.media.webpage.photo == null) {
                return "";
            }
            return "image/jpeg";
        }
    }

    public boolean canPreviewDocument() {
        return canPreviewDocument(getDocument());
    }

    public static boolean isGifDocument(WebFile document) {
        return document != null && (document.mime_type.equals("image/gif") || isNewGifDocument(document));
    }

    public static boolean isGifDocument(TLRPC.Document document) {
        return (document == null || document.mime_type == null || (!document.mime_type.equals("image/gif") && !isNewGifDocument(document))) ? false : true;
    }

    public static boolean isDocumentHasThumb(TLRPC.Document document) {
        if (document == null || document.thumbs.isEmpty()) {
            return false;
        }
        int N = document.thumbs.size();
        for (int a = 0; a < N; a++) {
            TLRPC.PhotoSize photoSize = document.thumbs.get(a);
            if (photoSize != null && !(photoSize instanceof TLRPC.TL_photoSizeEmpty) && !(photoSize.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canPreviewDocument(TLRPC.Document document) {
        if (!(document == null || document.mime_type == null)) {
            String mime = document.mime_type.toLowerCase();
            if (isDocumentHasThumb(document) && (mime.equals("image/png") || mime.equals("image/jpg") || mime.equals("image/jpeg"))) {
                for (int a = 0; a < document.attributes.size(); a++) {
                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        TLRPC.TL_documentAttributeImageSize size = (TLRPC.TL_documentAttributeImageSize) attribute;
                        if (size.w >= 6000 || size.h >= 6000) {
                            return false;
                        }
                        return true;
                    }
                }
            } else if (BuildVars.DEBUG_PRIVATE_VERSION) {
                String fileName = FileLoader.getDocumentFileName(document);
                if (!fileName.startsWith("tg_secret_sticker") || !fileName.endsWith("json")) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isRoundVideoDocument(TLRPC.Document document) {
        if (document == null || !MimeTypes.VIDEO_MP4.equals(document.mime_type)) {
            return false;
        }
        int width = 0;
        int height = 0;
        boolean round = false;
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                width = attribute.w;
                height = attribute.w;
                round = attribute.round_message;
            }
        }
        if (!round || width > 1280 || height > 1280) {
            return false;
        }
        return true;
    }

    public static boolean isNewGifDocument(WebFile document) {
        if (document == null || !MimeTypes.VIDEO_MP4.equals(document.mime_type)) {
            return false;
        }
        int width = 0;
        int height = 0;
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeAnimated)) {
                if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                    width = attribute.w;
                    height = attribute.w;
                }
            }
        }
        if (width > 1280 || height > 1280) {
            return false;
        }
        return true;
    }

    public static boolean isNewGifDocument(TLRPC.Document document) {
        if (document == null || !MimeTypes.VIDEO_MP4.equals(document.mime_type)) {
            return false;
        }
        int width = 0;
        int height = 0;
        boolean animated = false;
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAnimated) {
                animated = true;
            } else if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                width = attribute.w;
                height = attribute.w;
            }
        }
        if (!animated || width > 1280 || height > 1280) {
            return false;
        }
        return true;
    }

    public void generateThumbs(boolean update) {
        ArrayList<TLRPC.PhotoSize> arrayList;
        ArrayList<TLRPC.PhotoSize> arrayList2;
        ArrayList<TLRPC.PhotoSize> arrayList3;
        ArrayList<TLRPC.PhotoSize> arrayList4;
        ArrayList<TLRPC.PhotoSize> arrayList5;
        ArrayList<TLRPC.PhotoSize> arrayList6;
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_messageService) {
            if (message.action instanceof TLRPC.TL_messageActionChatEditPhoto) {
                TLRPC.Photo photo = this.messageOwner.action.photo;
                if (!update) {
                    this.photoThumbs = new ArrayList<>(photo.sizes);
                } else {
                    ArrayList<TLRPC.PhotoSize> arrayList7 = this.photoThumbs;
                    if (arrayList7 != null && !arrayList7.isEmpty()) {
                        for (int a = 0; a < this.photoThumbs.size(); a++) {
                            TLRPC.PhotoSize photoObject = this.photoThumbs.get(a);
                            int b = 0;
                            while (true) {
                                if (b >= photo.sizes.size()) {
                                    break;
                                }
                                TLRPC.PhotoSize size = photo.sizes.get(b);
                                if (!(size instanceof TLRPC.TL_photoSizeEmpty) && size.type.equals(photoObject.type)) {
                                    photoObject.location = size.location;
                                    break;
                                }
                                b++;
                            }
                        }
                    }
                }
                if (photo.dc_id != 0) {
                    int N = this.photoThumbs.size();
                    for (int a2 = 0; a2 < N; a2++) {
                        TLRPC.FileLocation location = this.photoThumbs.get(a2).location;
                        location.dc_id = photo.dc_id;
                        location.file_reference = photo.file_reference;
                    }
                }
                this.photoThumbsObject = this.messageOwner.action.photo;
            }
        } else if (this.emojiAnimatedSticker != null) {
            if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) && isDocumentHasThumb(this.emojiAnimatedSticker)) {
                if (!update || (arrayList6 = this.photoThumbs) == null) {
                    ArrayList<TLRPC.PhotoSize> arrayList8 = new ArrayList<>();
                    this.photoThumbs = arrayList8;
                    arrayList8.addAll(this.emojiAnimatedSticker.thumbs);
                } else if (arrayList6 != null && !arrayList6.isEmpty()) {
                    updatePhotoSizeLocations(this.photoThumbs, this.emojiAnimatedSticker.thumbs);
                }
                this.photoThumbsObject = this.emojiAnimatedSticker;
            }
        } else if (message.media != null && !(this.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) {
            if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
                TLRPC.Photo photo2 = this.messageOwner.media.photo;
                if (!update || !((arrayList5 = this.photoThumbs) == null || arrayList5.size() == photo2.sizes.size())) {
                    this.photoThumbs = new ArrayList<>(photo2.sizes);
                } else {
                    ArrayList<TLRPC.PhotoSize> arrayList9 = this.photoThumbs;
                    if (arrayList9 != null && !arrayList9.isEmpty()) {
                        for (int a3 = 0; a3 < this.photoThumbs.size(); a3++) {
                            TLRPC.PhotoSize photoObject2 = this.photoThumbs.get(a3);
                            if (photoObject2 != null) {
                                int b2 = 0;
                                while (true) {
                                    if (b2 < photo2.sizes.size()) {
                                        TLRPC.PhotoSize size2 = photo2.sizes.get(b2);
                                        if (size2 != null && !(size2 instanceof TLRPC.TL_photoSizeEmpty) && size2.type.equals(photoObject2.type)) {
                                            photoObject2.location = size2.location;
                                            break;
                                        }
                                        b2++;
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                this.photoThumbsObject = this.messageOwner.media.photo;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
                TLRPC.Document document = getDocument();
                if (isDocumentHasThumb(document)) {
                    if (!update || (arrayList4 = this.photoThumbs) == null) {
                        ArrayList<TLRPC.PhotoSize> arrayList10 = new ArrayList<>();
                        this.photoThumbs = arrayList10;
                        arrayList10.addAll(document.thumbs);
                    } else if (arrayList4 != null && !arrayList4.isEmpty()) {
                        updatePhotoSizeLocations(this.photoThumbs, document.thumbs);
                    }
                    this.photoThumbsObject = document;
                }
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                TLRPC.Document document2 = this.messageOwner.media.game.document;
                if (document2 != null && isDocumentHasThumb(document2)) {
                    if (!update) {
                        ArrayList<TLRPC.PhotoSize> arrayList11 = new ArrayList<>();
                        this.photoThumbs = arrayList11;
                        arrayList11.addAll(document2.thumbs);
                    } else {
                        ArrayList<TLRPC.PhotoSize> arrayList12 = this.photoThumbs;
                        if (arrayList12 != null && !arrayList12.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, document2.thumbs);
                        }
                    }
                    this.photoThumbsObject = document2;
                }
                TLRPC.Photo photo3 = this.messageOwner.media.game.photo;
                if (photo3 != null) {
                    if (!update || (arrayList3 = this.photoThumbs2) == null) {
                        this.photoThumbs2 = new ArrayList<>(photo3.sizes);
                    } else if (!arrayList3.isEmpty()) {
                        updatePhotoSizeLocations(this.photoThumbs2, photo3.sizes);
                    }
                    this.photoThumbsObject2 = photo3;
                }
                if (this.photoThumbs == null && (arrayList2 = this.photoThumbs2) != null) {
                    this.photoThumbs = arrayList2;
                    this.photoThumbs2 = null;
                    this.photoThumbsObject = this.photoThumbsObject2;
                    this.photoThumbsObject2 = null;
                }
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
                TLRPC.Photo photo4 = this.messageOwner.media.webpage.photo;
                TLRPC.Document document3 = this.messageOwner.media.webpage.document;
                if (photo4 != null) {
                    if (!update || (arrayList = this.photoThumbs) == null) {
                        this.photoThumbs = new ArrayList<>(photo4.sizes);
                    } else if (!arrayList.isEmpty()) {
                        updatePhotoSizeLocations(this.photoThumbs, photo4.sizes);
                    }
                    this.photoThumbsObject = photo4;
                } else if (document3 != null && isDocumentHasThumb(document3)) {
                    if (!update) {
                        ArrayList<TLRPC.PhotoSize> arrayList13 = new ArrayList<>();
                        this.photoThumbs = arrayList13;
                        arrayList13.addAll(document3.thumbs);
                    } else {
                        ArrayList<TLRPC.PhotoSize> arrayList14 = this.photoThumbs;
                        if (arrayList14 != null && !arrayList14.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, document3.thumbs);
                        }
                    }
                    this.photoThumbsObject = document3;
                }
            }
        }
    }

    private static void updatePhotoSizeLocations(ArrayList<TLRPC.PhotoSize> o, ArrayList<TLRPC.PhotoSize> n) {
        int N = o.size();
        for (int a = 0; a < N; a++) {
            TLRPC.PhotoSize photoObject = o.get(a);
            int b = 0;
            int N2 = n.size();
            while (true) {
                if (b >= N2) {
                    break;
                }
                TLRPC.PhotoSize size = n.get(b);
                if (!(size instanceof TLRPC.TL_photoSizeEmpty) && !(size instanceof TLRPC.TL_photoCachedSize) && size.type.equals(photoObject.type)) {
                    photoObject.location = size.location;
                    break;
                }
                b++;
            }
        }
    }

    public CharSequence replaceWithLink(CharSequence source, String param, ArrayList<Integer> uids, AbstractMap<Integer, TLRPC.User> usersDict, SparseArray<TLRPC.User> sUsersDict) {
        if (TextUtils.indexOf(source, param) < 0) {
            return source;
        }
        SpannableStringBuilder names = new SpannableStringBuilder("");
        for (int a = 0; a < uids.size(); a++) {
            TLRPC.User user = null;
            if (usersDict != null) {
                user = usersDict.get(uids.get(a));
            } else if (sUsersDict != null) {
                user = sUsersDict.get(uids.get(a).intValue());
            }
            if (user == null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(uids.get(a));
            }
            if (user != null) {
                String name = UserObject.getName(user);
                int start = names.length();
                if (names.length() != 0) {
                    names.append(", ");
                }
                names.append(name);
                names.setSpan(new URLSpanNoUnderlineBold("" + user.id), start, name.length() + start, 33);
            }
        }
        return TextUtils.replace(source, new String[]{param}, new CharSequence[]{names});
    }

    public CharSequence replaceWithLink(CharSequence source, String param, TLObject object, int status, ClickableSpan clickableSpan) {
        String name;
        String id;
        CharSequence charSequence = source;
        TLObject tLObject = object;
        ClickableSpan clickableSpan2 = clickableSpan;
        int start = TextUtils.indexOf(source, param);
        if (start >= 0) {
            if (tLObject instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) tLObject;
                if (user.id == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                    name = LocaleController.getString("YouSelf", R.string.YouSelf);
                } else {
                    name = UserObject.getName((TLRPC.User) tLObject);
                }
                id = "" + user.id;
            } else if (tLObject instanceof TLRPC.Chat) {
                name = ((TLRPC.Chat) tLObject).title;
                id = "" + (-((TLRPC.Chat) tLObject).id);
            } else if (tLObject instanceof TLRPC.TL_game) {
                name = ((TLRPC.TL_game) tLObject).title;
                id = "game";
            } else {
                name = "";
                id = "0";
            }
            String name2 = TextUtils.ellipsize(name.replace(10, ' '), Theme.chat_actionTextPaint, (float) AndroidUtilities.dp(150.0f), TextUtils.TruncateAt.END).toString();
            SpannableStringBuilder builder = new SpannableStringBuilder(TextUtils.replace(charSequence, new String[]{param}, new String[]{name2}));
            Drawable drawable = Theme.chat_redpkgSamllIcon;
            if (drawable == null) {
                Theme.chat_redpkgSamllIcon = ApplicationLoader.applicationContext.getResources().getDrawable(R.mipmap.ic_red_small).mutate();
                drawable = Theme.chat_redpkgSamllIcon;
            }
            drawable.setBounds(0, 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(18.0f));
            builder.setSpan(new ImageSpan(drawable), 0, 1, 33);
            builder.setSpan(new URLSpanNoUnderlineBold("" + id), start, name2.length() + start, 33);
            builder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chat_redpacketLinkServiceText)), start, name2.length() + start, 33);
            if (clickableSpan2 != null) {
                builder.setSpan(clickableSpan2, builder.length() - 2, builder.length(), 33);
                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFE5548")), builder.length() - 2, builder.length(), 33);
            }
            if (status == 1) {
                builder.append(LocaleController.getString(R.string.YouPacketComplete));
            }
            return builder;
        }
        int i = status;
        return charSequence;
    }

    public CharSequence replaceWithLink(CharSequence source, String param, TLObject object) {
        return replaceWithLink(source, param, object, false);
    }

    public CharSequence replaceWithLink(CharSequence source, String param, TLObject object, boolean forRedpacket) {
        String id;
        String name;
        if (source == null) {
            return "";
        }
        int start = TextUtils.indexOf(source, param);
        if (start < 0) {
            return source;
        }
        if (object instanceof TLRPC.User) {
            name = UserObject.getName((TLRPC.User) object);
            id = "" + ((TLRPC.User) object).id;
        } else if (object instanceof TLRPC.Chat) {
            name = ((TLRPC.Chat) object).title;
            id = "" + (-((TLRPC.Chat) object).id);
        } else if (object instanceof TLRPC.TL_game) {
            id = "game";
            name = ((TLRPC.TL_game) object).title;
        } else {
            name = "";
            id = "0";
        }
        String name2 = name.replace(10, ' ');
        if (forRedpacket) {
            name2 = TextUtils.ellipsize(name2, Theme.chat_actionTextPaint, (float) AndroidUtilities.dp(150.0f), TextUtils.TruncateAt.END).toString();
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(TextUtils.replace(source, new String[]{param}, new String[]{name2}));
        builder.setSpan(new URLSpanNoUnderlineBold("" + id), start, name2.length() + start, 33);
        if (forRedpacket) {
            builder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chat_redpacketLinkServiceText)), start, name2.length() + start, 33);
        }
        return builder;
    }

    public CharSequence replaceRedStrWithLink(CharSequence source, String param, TLObject object, boolean forRedpacket) {
        String name;
        String id;
        if (source == null) {
            return "";
        }
        int start = TextUtils.indexOf(source, param);
        if (start < 0) {
            return source;
        }
        if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            if (user.id == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                name = LocaleController.getString("YouSelf", R.string.YouSelf);
            } else {
                name = UserObject.getName((TLRPC.User) object);
            }
            id = "" + user.id;
        } else if (object instanceof TLRPC.Chat) {
            name = ((TLRPC.Chat) object).title;
            id = "" + (-((TLRPC.Chat) object).id);
        } else if (object instanceof TLRPC.TL_game) {
            name = ((TLRPC.TL_game) object).title;
            id = "game";
        } else {
            name = "";
            id = "0";
        }
        String name2 = name.replace(10, ' ');
        if (forRedpacket) {
            name2 = TextUtils.ellipsize(name2, Theme.chat_actionTextPaint, (float) AndroidUtilities.dp(150.0f), TextUtils.TruncateAt.END).toString();
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(TextUtils.replace(source, new String[]{param}, new String[]{name2}));
        builder.setSpan(new URLSpanNoUnderlineBold("" + id), start, name2.length() + start, 33);
        if (forRedpacket) {
            builder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chat_redpacketLinkServiceText)), start, name2.length() + start, 33);
        }
        return builder;
    }

    public CharSequence replaceRedStrWithLink(CharSequence source, String param, TLObject object, int status, ClickableSpan clickableSpan) {
        int start = TextUtils.indexOf(source, param);
        if (start < 0) {
            return source;
        }
        String name = LocaleController.getString(R.string.CgCoinRedpacket).replace(10, ' ');
        SpannableStringBuilder builder = new SpannableStringBuilder(TextUtils.replace(source, new String[]{param}, new String[]{name}));
        Drawable drawable = Theme.chat_redpkgSamllIcon;
        if (drawable == null) {
            Theme.chat_redpkgSamllIcon = ApplicationLoader.applicationContext.getResources().getDrawable(R.mipmap.ic_red_small).mutate();
            drawable = Theme.chat_redpkgSamllIcon;
        }
        drawable.setBounds(0, 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(18.0f));
        builder.setSpan(new ImageSpan(drawable), 0, 1, 33);
        if (clickableSpan != null) {
            builder.setSpan(clickableSpan, start, name.length() + start, 33);
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFE5548")), start, name.length() + start, 33);
        }
        if (status == 1) {
            builder.append(LocaleController.getString(R.string.YouPacketComplete));
        }
        return builder;
    }

    public String getExtension() {
        String fileName = getFileName();
        int idx = fileName.lastIndexOf(46);
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0) {
            ext = getDocument().mime_type;
        }
        if (ext == null) {
            ext = "";
        }
        return ext.toUpperCase();
    }

    public String getFileName() {
        TLRPC.PhotoSize sizeFull;
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(getDocument());
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            ArrayList<TLRPC.PhotoSize> sizes = this.messageOwner.media.photo.sizes;
            if (sizes.size() <= 0 || (sizeFull = FileLoader.getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize())) == null) {
                return "";
            }
            return FileLoader.getAttachFileName(sizeFull);
        } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
            return FileLoader.getAttachFileName(this.messageOwner.media.webpage.document);
        } else {
            return "";
        }
    }

    public int getFileType() {
        if (isVideo()) {
            return 2;
        }
        if (isVoice()) {
            return 1;
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
            return 3;
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            return 0;
        }
        return 4;
    }

    private static boolean containsUrls(CharSequence message) {
        if (message == null || message.length() < 2 || message.length() > 20480) {
            return false;
        }
        int length = message.length();
        int digitsInRow = 0;
        int schemeSequence = 0;
        int dotSequence = 0;
        char lastChar = 0;
        for (int i = 0; i < length; i++) {
            char c = message.charAt(i);
            if (c >= '0' && c <= '9') {
                digitsInRow++;
                if (digitsInRow >= 6) {
                    return true;
                }
                schemeSequence = 0;
                dotSequence = 0;
            } else if (c == ' ' || digitsInRow <= 0) {
                digitsInRow = 0;
            }
            if (((c == '@' || c == '#' || c == '/' || c == '$') && i == 0) || (i != 0 && (message.charAt(i - 1) == ' ' || message.charAt(i - 1) == 10))) {
                return true;
            }
            if (c == ':') {
                if (schemeSequence == 0) {
                    schemeSequence = 1;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '/') {
                if (schemeSequence == 2) {
                    return true;
                }
                if (schemeSequence == 1) {
                    schemeSequence++;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '.') {
                if (dotSequence != 0 || lastChar == ' ') {
                    dotSequence = 0;
                } else {
                    dotSequence++;
                }
            } else if (c != ' ' && lastChar == '.' && dotSequence == 1) {
                return true;
            } else {
                dotSequence = 0;
            }
            lastChar = c;
        }
        return false;
    }

    public void generateLinkDescription() {
        if (this.linkDescription == null) {
            int hashtagsType = 0;
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TLRPC.TL_webPage) && this.messageOwner.media.webpage.description != null) {
                this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.webpage.description);
                String siteName = this.messageOwner.media.webpage.site_name;
                if (siteName != null) {
                    siteName = siteName.toLowerCase();
                }
                if ("instagram".equals(siteName)) {
                    hashtagsType = 1;
                } else if ("twitter".equals(siteName)) {
                    hashtagsType = 2;
                }
            } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) && this.messageOwner.media.game.description != null) {
                this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.game.description);
            } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) && this.messageOwner.media.description != null) {
                this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.description);
            }
            if (!TextUtils.isEmpty(this.linkDescription)) {
                if (containsUrls(this.linkDescription)) {
                    try {
                        Linkify.addLinks((Spannable) this.linkDescription, 1);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                CharSequence replaceEmoji = Emoji.replaceEmoji(this.linkDescription, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                this.linkDescription = replaceEmoji;
                if (hashtagsType != 0) {
                    if (!(replaceEmoji instanceof Spannable)) {
                        this.linkDescription = new SpannableStringBuilder(this.linkDescription);
                    }
                    addUrlsByPattern(isOutOwner(), this.linkDescription, false, hashtagsType, 0);
                }
            }
        }
    }

    public void generateCaption() {
        boolean hasEntities;
        if (this.caption == null && !isRoundVideo() && !isMediaEmpty() && !(this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) && !TextUtils.isEmpty(this.messageOwner.message)) {
            CharSequence replaceEmoji = Emoji.replaceEmoji(this.messageOwner.message, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            this.caption = replaceEmoji;
            if (replaceEmoji != null) {
                this.caption = updateMetionText(replaceEmoji, this.messageOwner.entities);
            }
            if (this.messageOwner.send_state != 0) {
                hasEntities = false;
                int a = 0;
                while (true) {
                    if (a >= this.messageOwner.entities.size()) {
                        break;
                    } else if (!(this.messageOwner.entities.get(a) instanceof TLRPC.TL_inputMessageEntityMentionName)) {
                        hasEntities = true;
                        break;
                    } else {
                        a++;
                    }
                }
            } else {
                hasEntities = !this.messageOwner.entities.isEmpty();
            }
            boolean useManualParse = !hasEntities && (this.eventId != 0 || (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto_old) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto_layer68) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto_layer74) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument_old) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument_layer68) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument_layer74) || ((isOut() && this.messageOwner.send_state != 0) || this.messageOwner.id < 0));
            if (useManualParse) {
                if (containsUrls(this.caption)) {
                    try {
                        Linkify.addLinks((Spannable) this.caption, 5);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                addUrlsByPattern(isOutOwner(), this.caption, true, 0, 0);
            }
            addEntitiesToText(this.caption, useManualParse);
            if (isVideo()) {
                addUrlsByPattern(isOutOwner(), this.caption, true, 3, getDuration());
            }
        }
    }

    private static void addUrlsByPattern(boolean isOut, CharSequence charSequence, boolean botCommands, int patternType, int duration) {
        Matcher matcher;
        Matcher matcher2;
        URLSpanNoUnderline url;
        int seconds;
        CharSequence charSequence2 = charSequence;
        int i = patternType;
        int s3 = 3;
        int seconds2 = 1;
        if (i == 3) {
            try {
                if (videoTimeUrlPattern == null) {
                    videoTimeUrlPattern = Pattern.compile("\\b(?:(\\d{1,2}):)?(\\d{1,3}):([0-5][0-9])\\b");
                }
                matcher = videoTimeUrlPattern.matcher(charSequence2);
            } catch (Exception e) {
                e = e;
                int i2 = duration;
                FileLog.e((Throwable) e);
                return;
            }
        } else if (i == 1) {
            if (instagramUrlPattern == null) {
                instagramUrlPattern = Pattern.compile("(^|\\s|\\()@[a-zA-Z\\d_.]{1,32}|(^|\\s|\\()#[\\w.]+");
            }
            matcher = instagramUrlPattern.matcher(charSequence2);
        } else {
            if (urlPattern == null) {
                urlPattern = Pattern.compile("");
            }
            matcher = urlPattern.matcher(charSequence2);
        }
        Spannable spannable = (Spannable) charSequence2;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (i == s3) {
                URLSpan[] spans = (URLSpan[]) spannable.getSpans(start, end, URLSpan.class);
                if (spans == null || spans.length <= 0) {
                    int groupCount = matcher.groupCount();
                    int s1 = matcher.start(seconds2);
                    int e1 = matcher.end(seconds2);
                    int s2 = matcher.start(2);
                    int e2 = matcher.end(2);
                    int s32 = matcher.start(s3);
                    int e3 = matcher.end(s3);
                    int minutes = Utilities.parseInt(charSequence2.subSequence(s2, e2)).intValue();
                    int s33 = s32;
                    int seconds3 = Utilities.parseInt(charSequence2.subSequence(s33, e3)).intValue();
                    int hours = (s1 < 0 || e1 < 0) ? -1 : Utilities.parseInt(charSequence2.subSequence(s1, e1)).intValue();
                    int seconds4 = seconds3 + (minutes * 60);
                    if (hours > 0) {
                        seconds = seconds4 + (hours * 60 * 60);
                    } else {
                        seconds = seconds4;
                    }
                    int seconds5 = e2;
                    if (seconds > duration) {
                        s3 = 3;
                        seconds2 = 1;
                    } else {
                        int i3 = s33;
                        try {
                            matcher2 = matcher;
                            url = new URLSpanNoUnderline("video?" + seconds);
                        } catch (Exception e4) {
                            e = e4;
                            FileLog.e((Throwable) e);
                            return;
                        }
                    }
                }
            } else {
                int i4 = duration;
                matcher2 = matcher;
                char ch = charSequence2.charAt(start);
                if (start < charSequence.length()) {
                    if (i != 0) {
                        if (!(ch == '@' || ch == '#')) {
                            start++;
                        }
                        ch = charSequence2.charAt(start);
                        if (!(ch == '@' || ch == '#')) {
                            matcher = matcher2;
                            s3 = 3;
                            seconds2 = 1;
                        }
                    } else if (!(ch == '@' || ch == '#' || ch == '/' || ch == '$')) {
                        start++;
                    }
                    if (i == 1) {
                        if (ch == '@') {
                            url = new URLSpanNoUnderline("https://instagram.com/" + charSequence2.subSequence(start + 1, end).toString());
                        } else if (ch == '#') {
                            url = new URLSpanNoUnderline("https://www.instagram.com/explore/tags/" + charSequence2.subSequence(start + 1, end).toString());
                        }
                    } else if (i == 2) {
                        if (ch == '@') {
                            url = new URLSpanNoUnderline("https://twitter.com/" + charSequence2.subSequence(start + 1, end).toString());
                        } else if (ch == '#') {
                            url = new URLSpanNoUnderline("https://twitter.com/hashtag/" + charSequence2.subSequence(start + 1, end).toString());
                        }
                    } else if (end > start && start >= 0) {
                        if (charSequence2.charAt(start) != '/') {
                            url = new URLSpanNoUnderline(charSequence2.subSequence(start, end).toString());
                        } else if (botCommands) {
                            url = new URLSpanBotCommand(charSequence2.subSequence(start, end).toString(), isOut ? 1 : 0);
                        }
                    }
                    url = null;
                } else {
                    url = null;
                }
            }
            if (url != null) {
                spannable.setSpan(url, start, end, 0);
            }
            matcher = matcher2;
            s3 = 3;
            seconds2 = 1;
        }
        int i5 = duration;
        Matcher matcher3 = matcher;
    }

    public static int[] getWebDocumentWidthAndHeight(TLRPC.WebDocument document) {
        if (document == null) {
            return null;
        }
        int a = 0;
        int size = document.attributes.size();
        while (a < size) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                return new int[]{attribute.w, attribute.h};
            } else if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return new int[]{attribute.w, attribute.h};
            } else {
                a++;
            }
        }
        return null;
    }

    public static int getWebDocumentDuration(TLRPC.WebDocument document) {
        if (document == null) {
            return 0;
        }
        int size = document.attributes.size();
        for (int a = 0; a < size; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return attribute.duration;
            }
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return attribute.duration;
            }
        }
        return 0;
    }

    public static int[] getInlineResultWidthAndHeight(TLRPC.BotInlineResult inlineResult) {
        int[] result = getWebDocumentWidthAndHeight(inlineResult.content);
        if (result != null) {
            return result;
        }
        int[] result2 = getWebDocumentWidthAndHeight(inlineResult.thumb);
        if (result2 == null) {
            return new int[]{0, 0};
        }
        return result2;
    }

    public static int getInlineResultDuration(TLRPC.BotInlineResult inlineResult) {
        int result = getWebDocumentDuration(inlineResult.content);
        if (result == 0) {
            return getWebDocumentDuration(inlineResult.thumb);
        }
        return result;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r0 = r5.photoThumbs;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasValidGroupId() {
        /*
            r5 = this;
            long r0 = r5.getGroupId()
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0016
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r0 = r5.photoThumbs
            if (r0 == 0) goto L_0x0016
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0016
            r0 = 1
            goto L_0x0017
        L_0x0016:
            r0 = 0
        L_0x0017:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MessageObject.hasValidGroupId():boolean");
    }

    public long getGroupIdForUse() {
        long j = this.localSentGroupId;
        return j != 0 ? j : this.messageOwner.grouped_id;
    }

    public long getGroupId() {
        long j = this.localGroupId;
        return j != 0 ? j : getGroupIdForUse();
    }

    public static void addLinks(boolean isOut, CharSequence messageText2) {
        addLinks(isOut, messageText2, true);
    }

    public static void addLinks(boolean isOut, CharSequence messageText2, boolean botCommands) {
        if ((messageText2 instanceof Spannable) && containsUrls(messageText2)) {
            if (messageText2.length() < 1000) {
                try {
                    Linkify.addLinks((Spannable) messageText2, 5);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                try {
                    Linkify.addLinks((Spannable) messageText2, 1);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            addUrlsByPattern(isOut, messageText2, botCommands, 0, 0);
        }
    }

    public void resetPlayingProgress() {
        this.audioProgress = 0.0f;
        this.audioProgressSec = 0;
        this.bufferedProgress = 0.0f;
    }

    private boolean addEntitiesToText(CharSequence text, boolean useManualParse) {
        return addEntitiesToText(text, false, useManualParse);
    }

    public boolean addEntitiesToText(CharSequence text, boolean photoViewer, boolean useManualParse) {
        if (this.isRestrictedMessage) {
            ArrayList<TLRPC.MessageEntity> entities = new ArrayList<>();
            TLRPC.TL_messageEntityItalic entityItalic = new TLRPC.TL_messageEntityItalic();
            entityItalic.offset = 0;
            entityItalic.length = text.length();
            entities.add(entityItalic);
            return addEntitiesToText(text, entities, isOutOwner(), this.type, true, photoViewer, useManualParse);
        }
        return addEntitiesToText(text, this.entitiesCopy, isOutOwner(), this.type, true, photoViewer, useManualParse);
    }

    public static boolean addEntitiesToText(CharSequence text, ArrayList<TLRPC.MessageEntity> entities, boolean out, int type2, boolean usernames, boolean photoViewer, boolean useManualParse) {
        byte t;
        String url;
        int i;
        TextStyleSpan.TextStyleRun run;
        int N;
        boolean hasUrls;
        TLRPC.MessageEntity entity;
        CharSequence charSequence = text;
        boolean hasUrls2 = false;
        if (!(charSequence instanceof Spannable)) {
            return false;
        }
        Spannable spannable = (Spannable) charSequence;
        URLSpan[] spans = (URLSpan[]) spannable.getSpans(0, text.length(), URLSpan.class);
        if (spans != null && spans.length > 0) {
            hasUrls2 = true;
        }
        if (entities.isEmpty()) {
            return hasUrls2;
        }
        if (photoViewer) {
            t = 2;
        } else if (out) {
            t = 1;
        } else {
            t = 0;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(entities);
        Collections.sort(arrayList2, $$Lambda$MessageObject$nrSF_MU9dAInTH9PEUlN2j5L18A.INSTANCE);
        int a = 0;
        int N2 = arrayList2.size();
        while (a < N2) {
            TLRPC.MessageEntity entity2 = (TLRPC.MessageEntity) arrayList2.get(a);
            if (entity2.length <= 0 || entity2.offset < 0) {
                hasUrls = hasUrls2;
                N = N2;
                TLRPC.MessageEntity messageEntity = entity2;
            } else if (entity2.offset >= text.length()) {
                hasUrls = hasUrls2;
                N = N2;
            } else {
                if (entity2.offset + entity2.length > text.length()) {
                    entity2.length = text.length() - entity2.offset;
                }
                if ((!useManualParse || (entity2 instanceof TLRPC.TL_messageEntityBold) || (entity2 instanceof TLRPC.TL_messageEntityItalic) || (entity2 instanceof TLRPC.TL_messageEntityStrike) || (entity2 instanceof TLRPC.TL_messageEntityUnderline) || (entity2 instanceof TLRPC.TL_messageEntityBlockquote) || (entity2 instanceof TLRPC.TL_messageEntityCode) || (entity2 instanceof TLRPC.TL_messageEntityPre) || (entity2 instanceof TLRPC.TL_messageEntityMentionName) || (entity2 instanceof TLRPC.TL_inputMessageEntityMentionName) || (entity2 instanceof TLRPC.TL_messageEntityTextUrl)) && spans != null && spans.length > 0) {
                    for (int b = 0; b < spans.length; b++) {
                        if (spans[b] != null) {
                            int start = spannable.getSpanStart(spans[b]);
                            int end = spannable.getSpanEnd(spans[b]);
                            if ((entity2.offset <= start && entity2.offset + entity2.length >= start) || (entity2.offset <= end && entity2.offset + entity2.length >= end)) {
                                spannable.removeSpan(spans[b]);
                                spans[b] = null;
                            }
                        }
                    }
                }
                TextStyleSpan.TextStyleRun newRun = new TextStyleSpan.TextStyleRun();
                newRun.start = entity2.offset;
                newRun.end = newRun.start + entity2.length;
                if (entity2 instanceof TLRPC.TL_messageEntityStrike) {
                    newRun.flags = 8;
                } else if (entity2 instanceof TLRPC.TL_messageEntityUnderline) {
                    newRun.flags = 16;
                } else if (entity2 instanceof TLRPC.TL_messageEntityBlockquote) {
                    newRun.flags = 32;
                } else if (entity2 instanceof TLRPC.TL_messageEntityBold) {
                    newRun.flags = 1;
                } else if (entity2 instanceof TLRPC.TL_messageEntityItalic) {
                    newRun.flags = 2;
                } else if ((entity2 instanceof TLRPC.TL_messageEntityCode) || (entity2 instanceof TLRPC.TL_messageEntityPre)) {
                    newRun.flags = 4;
                } else if (entity2 instanceof TLRPC.TL_messageEntityMentionName) {
                    if (!usernames) {
                        hasUrls = hasUrls2;
                        N = N2;
                    } else {
                        newRun.flags = 64;
                        newRun.urlEntity = entity2;
                    }
                } else if (entity2 instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    if (!usernames) {
                        hasUrls = hasUrls2;
                        N = N2;
                    } else {
                        newRun.flags = 64;
                        newRun.urlEntity = entity2;
                    }
                } else if (useManualParse && !(entity2 instanceof TLRPC.TL_messageEntityTextUrl)) {
                    hasUrls = hasUrls2;
                    N = N2;
                } else if (((entity2 instanceof TLRPC.TL_messageEntityUrl) || (entity2 instanceof TLRPC.TL_messageEntityTextUrl)) && Browser.isPassportUrl(entity2.url)) {
                    hasUrls = hasUrls2;
                    N = N2;
                } else if (!(entity2 instanceof TLRPC.TL_messageEntityMention) || usernames) {
                    newRun.flags = 128;
                    newRun.urlEntity = entity2;
                } else {
                    hasUrls = hasUrls2;
                    N = N2;
                }
                int b2 = 0;
                int N22 = arrayList.size();
                while (b2 < N22) {
                    TextStyleSpan.TextStyleRun run2 = (TextStyleSpan.TextStyleRun) arrayList.get(b2);
                    boolean hasUrls3 = hasUrls2;
                    int N3 = N2;
                    if (newRun.start > run2.start) {
                        if (newRun.start < run2.end) {
                            if (newRun.end < run2.end) {
                                TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                                r.merge(run2);
                                int b3 = b2 + 1;
                                arrayList.add(b3, r);
                                TextStyleSpan.TextStyleRun r2 = new TextStyleSpan.TextStyleRun(run2);
                                r2.start = newRun.end;
                                b2 = b3 + 1;
                                N22 = N22 + 1 + 1;
                                arrayList.add(b2, r2);
                            } else if (newRun.end >= run2.end) {
                                TextStyleSpan.TextStyleRun r3 = new TextStyleSpan.TextStyleRun(newRun);
                                r3.merge(run2);
                                r3.end = run2.end;
                                b2++;
                                N22++;
                                arrayList.add(b2, r3);
                            }
                            int temp = newRun.start;
                            newRun.start = run2.end;
                            run2.end = temp;
                            entity = entity2;
                            b2++;
                            entity2 = entity;
                            hasUrls2 = hasUrls3;
                            N2 = N3;
                        }
                    } else if (run2.start < newRun.end) {
                        int temp2 = run2.start;
                        entity = entity2;
                        if (newRun.end == run2.end) {
                            run2.merge(newRun);
                        } else if (newRun.end < run2.end) {
                            TextStyleSpan.TextStyleRun r4 = new TextStyleSpan.TextStyleRun(run2);
                            r4.merge(newRun);
                            r4.end = newRun.end;
                            b2++;
                            N22++;
                            arrayList.add(b2, r4);
                            run2.start = newRun.end;
                        } else {
                            TextStyleSpan.TextStyleRun r5 = new TextStyleSpan.TextStyleRun(newRun);
                            r5.start = run2.end;
                            b2++;
                            N22++;
                            arrayList.add(b2, r5);
                            run2.merge(newRun);
                        }
                        newRun.end = temp2;
                        b2++;
                        entity2 = entity;
                        hasUrls2 = hasUrls3;
                        N2 = N3;
                    }
                    entity = entity2;
                    b2++;
                    entity2 = entity;
                    hasUrls2 = hasUrls3;
                    N2 = N3;
                }
                hasUrls = hasUrls2;
                N = N2;
                TLRPC.MessageEntity messageEntity2 = entity2;
                if (newRun.start < newRun.end) {
                    arrayList.add(newRun);
                }
            }
            a++;
            hasUrls2 = hasUrls;
            N2 = N;
        }
        boolean hasUrls4 = hasUrls2;
        int i2 = N2;
        String str = null;
        int count = arrayList.size();
        int a2 = 0;
        while (a2 < count) {
            TextStyleSpan.TextStyleRun run3 = (TextStyleSpan.TextStyleRun) arrayList.get(a2);
            String url2 = run3.urlEntity != null ? TextUtils.substring(charSequence, run3.urlEntity.offset, run3.urlEntity.offset + run3.urlEntity.length) : str;
            if (run3.urlEntity instanceof TLRPC.TL_messageEntityBotCommand) {
                spannable.setSpan(new URLSpanBotCommand(url2, t, run3), run3.start, run3.end, 33);
            } else {
                if ((run3.urlEntity instanceof TLRPC.TL_messageEntityHashtag) || (run3.urlEntity instanceof TLRPC.TL_messageEntityMention)) {
                    url = url2;
                    run = run3;
                    i = 33;
                } else if (run3.urlEntity instanceof TLRPC.TL_messageEntityCashtag) {
                    url = url2;
                    run = run3;
                    i = 33;
                } else if (run3.urlEntity instanceof TLRPC.TL_messageEntityEmail) {
                    spannable.setSpan(new URLSpanReplacement("mailto:" + url2, run3), run3.start, run3.end, 33);
                } else if (run3.urlEntity instanceof TLRPC.TL_messageEntityUrl) {
                    if (url2.toLowerCase().startsWith("http") || url2.toLowerCase().startsWith("hchat://")) {
                        spannable.setSpan(new URLSpanBrowser(url2, run3), run3.start, run3.end, 33);
                    } else {
                        spannable.setSpan(new URLSpanBrowser("http://" + url2, run3), run3.start, run3.end, 33);
                    }
                    hasUrls4 = true;
                } else if (run3.urlEntity instanceof TLRPC.TL_messageEntityPhone) {
                    String tel = PhoneFormat.stripExceptNumbers(url2);
                    if (url2.startsWith(Marker.ANY_NON_NULL_MARKER)) {
                        tel = Marker.ANY_NON_NULL_MARKER + tel;
                    }
                    spannable.setSpan(new URLSpanBrowser("tel:" + tel, run3), run3.start, run3.end, 33);
                    hasUrls4 = true;
                } else if (run3.urlEntity instanceof TLRPC.TL_messageEntityTextUrl) {
                    spannable.setSpan(new URLSpanReplacement(run3.urlEntity.url, run3), run3.start, run3.end, 33);
                } else if (run3.urlEntity instanceof TLRPC.TL_messageEntityMentionName) {
                    spannable.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_messageEntityMentionName) run3.urlEntity).user_id, t, run3), run3.start, run3.end, 33);
                } else if (run3.urlEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    spannable.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName) run3.urlEntity).user_id.user_id, t, run3), run3.start, run3.end, 33);
                } else if ((run3.flags & 4) != 0) {
                    String str2 = url2;
                    TextStyleSpan.TextStyleRun run4 = run3;
                    spannable.setSpan(new URLSpanMono(spannable, run3.start, run3.end, t, run3), run4.start, run4.end, 33);
                } else {
                    TextStyleSpan.TextStyleRun run5 = run3;
                    spannable.setSpan(new TextStyleSpan(run5), run5.start, run5.end, 33);
                }
                spannable.setSpan(new URLSpanNoUnderline(url, run), run.start, run.end, i);
            }
            a2++;
            str = null;
            charSequence = text;
        }
        return hasUrls4;
    }

    static /* synthetic */ int lambda$addEntitiesToText$2(TLRPC.MessageEntity o1, TLRPC.MessageEntity o2) {
        if (o1.offset > o2.offset) {
            return 1;
        }
        if (o1.offset < o2.offset) {
            return -1;
        }
        return 0;
    }

    public boolean needDrawShareButton() {
        int i;
        TLRPC.Chat chat;
        if (this.scheduled || this.eventId != 0) {
            return false;
        }
        if (this.messageOwner.fwd_from != null && !isOutOwner() && this.messageOwner.fwd_from.saved_from_peer != null && getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId())) {
            return true;
        }
        int i2 = this.type;
        if (i2 == 13 || i2 == 15) {
            return false;
        }
        if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.channel_id != 0 && !isOutOwner()) {
            return true;
        }
        if (isFromUser()) {
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty) || this.messageOwner.media == null || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && !(this.messageOwner.media.webpage instanceof TLRPC.TL_webPage))) {
                return false;
            }
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
            if (user != null && user.bot) {
                return true;
            }
            if (!isOut()) {
                if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
                    return true;
                }
                if (!isMegagroup() || (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id))) == null || chat.username == null || chat.username.length() <= 0 || (this.messageOwner.media instanceof TLRPC.TL_messageMediaContact) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) {
                    return false;
                }
                return true;
            }
        } else if ((this.messageOwner.from_id < 0 || this.messageOwner.post) && this.messageOwner.to_id.channel_id != 0 && ((this.messageOwner.via_bot_id == 0 && this.messageOwner.reply_to_msg_id == 0) || !((i = this.type) == 13 || i == 15))) {
            return true;
        }
        return false;
    }

    public boolean isYouTubeVideo() {
        return (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && this.messageOwner.media.webpage != null && !TextUtils.isEmpty(this.messageOwner.media.webpage.embed_url) && "YouTube".equals(this.messageOwner.media.webpage.site_name);
    }

    public int getMaxMessageTextWidth() {
        int maxWidth;
        int maxWidth2 = 0;
        if (!AndroidUtilities.isTablet() || this.eventId == 0) {
            this.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x;
        } else {
            this.generatedWithMinSize = AndroidUtilities.dp(530.0f);
        }
        this.generatedWithDensity = AndroidUtilities.density;
        if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && this.messageOwner.media.webpage != null && "app_background".equals(this.messageOwner.media.webpage.type)) {
            try {
                Uri uri = Uri.parse(this.messageOwner.media.webpage.url);
                if (uri.getQueryParameter("bg_color") != null) {
                    maxWidth2 = AndroidUtilities.dp(220.0f);
                } else if (uri.getLastPathSegment().length() == 6) {
                    maxWidth2 = AndroidUtilities.dp(200.0f);
                }
            } catch (Exception e) {
            }
        } else if (isAndroidTheme()) {
            maxWidth2 = AndroidUtilities.dp(200.0f);
        }
        if (maxWidth2 != 0) {
            return maxWidth2;
        }
        int maxWidth3 = this.generatedWithMinSize - AndroidUtilities.dp(147.0f);
        if (needDrawShareButton() == 0 || isOutOwner()) {
            maxWidth = maxWidth3;
        } else {
            maxWidth = maxWidth3 - AndroidUtilities.dp(10.0f);
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
            return maxWidth - AndroidUtilities.dp(10.0f);
        }
        return maxWidth;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x007c, code lost:
        if (r1.messageOwner.send_state == 0) goto L_0x007e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x008a, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaUnsupported) == false) goto L_0x008e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01f7  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0210  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0387 A[SYNTHETIC, Splitter:B:147:0x0387] */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x03b8  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x009c  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0110 A[SYNTHETIC, Splitter:B:73:0x0110] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0140 A[SYNTHETIC, Splitter:B:78:0x0140] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0169 A[SYNTHETIC, Splitter:B:85:0x0169] */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01c2  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01d5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateLayout(im.bclpbkiauv.tgnet.TLRPC.User r32) {
        /*
            r31 = this;
            r1 = r31
            int r0 = r1.type
            r2 = 2
            if (r0 == 0) goto L_0x0013
            if (r0 == r2) goto L_0x0013
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.trans
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x059f
        L_0x0013:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            if (r0 == 0) goto L_0x059f
            java.lang.CharSequence r0 = r1.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0023
            goto L_0x059f
        L_0x0023:
            r31.generateLinkDescription()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.textLayoutBlocks = r0
            r3 = 0
            r1.textWidth = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.send_state
            r4 = 1
            if (r0 == 0) goto L_0x003a
            r0 = 0
            r5 = r0
            goto L_0x0044
        L_0x003a:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r4
            r5 = r0
        L_0x0044:
            if (r5 != 0) goto L_0x008e
            long r6 = r1.eventId
            r8 = 0
            int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r0 != 0) goto L_0x008c
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            boolean r6 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_message_old
            if (r6 != 0) goto L_0x008c
            boolean r6 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_message_old2
            if (r6 != 0) goto L_0x008c
            boolean r6 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_message_old3
            if (r6 != 0) goto L_0x008c
            boolean r6 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_message_old4
            if (r6 != 0) goto L_0x008c
            boolean r6 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageForwarded_old
            if (r6 != 0) goto L_0x008c
            boolean r6 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageForwarded_old2
            if (r6 != 0) goto L_0x008c
            boolean r6 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_message_secret
            if (r6 != 0) goto L_0x008c
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaInvoice
            if (r0 != 0) goto L_0x008c
            boolean r0 = r31.isOut()
            if (r0 == 0) goto L_0x007e
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.send_state
            if (r0 != 0) goto L_0x008c
        L_0x007e:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.id
            if (r0 < 0) goto L_0x008c
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaUnsupported
            if (r0 == 0) goto L_0x008e
        L_0x008c:
            r0 = 1
            goto L_0x008f
        L_0x008e:
            r0 = 0
        L_0x008f:
            r6 = r0
            if (r6 == 0) goto L_0x009c
            boolean r0 = r31.isOutOwner()
            java.lang.CharSequence r7 = r1.messageText
            addLinks(r0, r7)
            goto L_0x00b7
        L_0x009c:
            java.lang.CharSequence r0 = r1.messageText
            boolean r7 = r0 instanceof android.text.Spannable
            if (r7 == 0) goto L_0x00b7
            int r0 = r0.length()
            r7 = 1000(0x3e8, float:1.401E-42)
            if (r0 >= r7) goto L_0x00b7
            java.lang.CharSequence r0 = r1.messageText     // Catch:{ all -> 0x00b3 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ all -> 0x00b3 }
            r7 = 4
            android.text.util.Linkify.addLinks(r0, r7)     // Catch:{ all -> 0x00b3 }
            goto L_0x00b7
        L_0x00b3:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00b7:
            boolean r0 = r31.isYouTubeVideo()
            r7 = 3
            if (r0 != 0) goto L_0x00e3
            im.bclpbkiauv.messenger.MessageObject r0 = r1.replyMessageObject
            if (r0 == 0) goto L_0x00c9
            boolean r0 = r0.isYouTubeVideo()
            if (r0 == 0) goto L_0x00c9
            goto L_0x00e3
        L_0x00c9:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.replyMessageObject
            if (r0 == 0) goto L_0x00ef
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x00ef
            boolean r0 = r31.isOutOwner()
            java.lang.CharSequence r8 = r1.messageText
            im.bclpbkiauv.messenger.MessageObject r9 = r1.replyMessageObject
            int r9 = r9.getDuration()
            addUrlsByPattern(r0, r8, r3, r7, r9)
            goto L_0x00ef
        L_0x00e3:
            boolean r0 = r31.isOutOwner()
            java.lang.CharSequence r8 = r1.messageText
            r9 = 2147483647(0x7fffffff, float:NaN)
            addUrlsByPattern(r0, r8, r3, r7, r9)
        L_0x00ef:
            java.lang.CharSequence r0 = r1.messageText
            boolean r8 = r1.addEntitiesToText(r0, r6)
            int r15 = r31.getMaxMessageTextWidth()
            r17 = 0
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x0107
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.chat_msgGameTextPaint
            r14 = r0
            goto L_0x010a
        L_0x0107:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.chat_msgTextPaint
            r14 = r0
        L_0x010a:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0591 }
            r13 = 24
            if (r0 < r13) goto L_0x0140
            java.lang.CharSequence r0 = r1.messageText     // Catch:{ Exception -> 0x0134 }
            java.lang.CharSequence r9 = r1.messageText     // Catch:{ Exception -> 0x0134 }
            int r9 = r9.length()     // Catch:{ Exception -> 0x0134 }
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r0, r3, r9, r14, r15)     // Catch:{ Exception -> 0x0134 }
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r4)     // Catch:{ Exception -> 0x0134 }
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r3)     // Catch:{ Exception -> 0x0134 }
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0134 }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r9)     // Catch:{ Exception -> 0x0134 }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x0134 }
            r2 = r14
            r4 = r15
            r7 = 24
            r15 = r0
            goto L_0x015f
        L_0x0134:
            r0 = move-exception
            r22 = r5
            r26 = r6
            r27 = r8
            r29 = r14
            r7 = r15
            goto L_0x059b
        L_0x0140:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0591 }
            java.lang.CharSequence r10 = r1.messageText     // Catch:{ Exception -> 0x0591 }
            android.text.Layout$Alignment r16 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0591 }
            r18 = 1065353216(0x3f800000, float:1.0)
            r19 = 0
            r20 = 0
            r9 = r0
            r11 = r14
            r12 = r15
            r7 = 24
            r13 = r16
            r2 = r14
            r14 = r18
            r4 = r15
            r15 = r19
            r16 = r20
            r9.<init>(r10, r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x0586 }
            r15 = r0
        L_0x015f:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner     // Catch:{ Exception -> 0x0586 }
            java.lang.String r0 = r0.trans     // Catch:{ Exception -> 0x0586 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0586 }
            if (r0 != 0) goto L_0x01c2
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x01b6 }
            if (r0 < r7) goto L_0x0197
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner     // Catch:{ Exception -> 0x01b6 }
            java.lang.String r0 = r0.trans     // Catch:{ Exception -> 0x01b6 }
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r1.messageOwner     // Catch:{ Exception -> 0x01b6 }
            java.lang.String r9 = r9.trans     // Catch:{ Exception -> 0x01b6 }
            int r9 = r9.length()     // Catch:{ Exception -> 0x01b6 }
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r0, r3, r9, r2, r4)     // Catch:{ Exception -> 0x01b6 }
            r9 = 1
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r9)     // Catch:{ Exception -> 0x01b6 }
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r3)     // Catch:{ Exception -> 0x01b6 }
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x01b6 }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r9)     // Catch:{ Exception -> 0x01b6 }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x01b6 }
            r17 = r0
            r19 = r15
            r15 = r17
            goto L_0x01c6
        L_0x0197:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x01b6 }
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r1.messageOwner     // Catch:{ Exception -> 0x01b6 }
            java.lang.String r10 = r9.trans     // Catch:{ Exception -> 0x01b6 }
            android.text.Layout$Alignment r13 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x01b6 }
            r14 = 1065353216(0x3f800000, float:1.0)
            r16 = 0
            r18 = 0
            r9 = r0
            r11 = r2
            r12 = r4
            r19 = r15
            r15 = r16
            r16 = r18
            r9.<init>(r10, r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x01b6 }
            r17 = r0
            r15 = r17
            goto L_0x01c6
        L_0x01b6:
            r0 = move-exception
            r29 = r2
            r7 = r4
            r22 = r5
            r26 = r6
            r27 = r8
            goto L_0x059b
        L_0x01c2:
            r19 = r15
            r15 = r17
        L_0x01c6:
            int r0 = r19.getHeight()
            r1.textHeight = r0
            int r0 = r19.getLineCount()
            r1.linesCount = r0
            if (r15 == 0) goto L_0x01f3
            int r0 = r15.getHeight()
            r1.transHeight = r0
            int r0 = r15.getLineCount()
            r9 = 0
        L_0x01e0:
            if (r9 >= r0) goto L_0x01f3
            float r10 = r15.getLineMax(r9)
            int r11 = r1.transWidth
            float r11 = (float) r11
            float r11 = java.lang.Math.max(r11, r10)
            int r11 = (int) r11
            r1.transWidth = r11
            int r9 = r9 + 1
            goto L_0x01e0
        L_0x01f3:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r7) goto L_0x01fa
            r0 = 1
            r14 = r0
            goto L_0x0207
        L_0x01fa:
            int r0 = r1.linesCount
            float r0 = (float) r0
            r9 = 1092616192(0x41200000, float:10.0)
            float r0 = r0 / r9
            double r9 = (double) r0
            double r9 = java.lang.Math.ceil(r9)
            int r0 = (int) r9
            r14 = r0
        L_0x0207:
            r0 = 0
            r9 = 0
            r10 = 0
            r13 = r0
            r20 = r9
            r12 = r10
        L_0x020e:
            if (r12 >= r14) goto L_0x0585
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r7) goto L_0x0218
            int r0 = r1.linesCount
            r11 = r0
            goto L_0x0222
        L_0x0218:
            r0 = 10
            int r9 = r1.linesCount
            int r9 = r9 - r13
            int r0 = java.lang.Math.min(r0, r9)
            r11 = r0
        L_0x0222:
            im.bclpbkiauv.messenger.MessageObject$TextLayoutBlock r0 = new im.bclpbkiauv.messenger.MessageObject$TextLayoutBlock
            r0.<init>()
            r10 = r0
            r9 = 0
            r7 = 1
            if (r14 != r7) goto L_0x029e
            r7 = r19
            r10.textLayout = r7
            r10.textYOffset = r9
            r10.charactersOffset = r3
            int r0 = r1.emojiOnlyCount
            if (r0 == 0) goto L_0x0288
            r9 = 1
            if (r0 == r9) goto L_0x0271
            r9 = 2
            if (r0 == r9) goto L_0x025a
            r9 = 3
            if (r0 == r9) goto L_0x0242
            goto L_0x0288
        L_0x0242:
            int r0 = r1.textHeight
            r17 = 1082549862(0x40866666, float:4.2)
            int r18 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 - r18
            r1.textHeight = r0
            float r0 = r10.textYOffset
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            float r9 = (float) r9
            float r0 = r0 - r9
            r10.textYOffset = r0
            goto L_0x0288
        L_0x025a:
            int r0 = r1.textHeight
            r9 = 1083179008(0x40900000, float:4.5)
            int r17 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r17
            r1.textHeight = r0
            float r0 = r10.textYOffset
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r0 = r0 - r9
            r10.textYOffset = r0
            goto L_0x0288
        L_0x0271:
            int r0 = r1.textHeight
            r9 = 1084856730(0x40a9999a, float:5.3)
            int r17 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r17
            r1.textHeight = r0
            float r0 = r10.textYOffset
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r0 = r0 - r9
            r10.textYOffset = r0
        L_0x0288:
            int r0 = r1.textHeight
            r10.height = r0
            r29 = r2
            r22 = r5
            r26 = r6
            r27 = r8
            r6 = r10
            r8 = r12
            r3 = r13
            r2 = r14
            r30 = r15
            r25 = 3
            goto L_0x03dc
        L_0x029e:
            r7 = r19
            int r9 = r7.getLineStart(r13)
            int r0 = r13 + r11
            r17 = 1
            int r0 = r0 + -1
            int r3 = r7.getLineEnd(r0)
            if (r3 >= r9) goto L_0x02c7
            r29 = r2
            r22 = r5
            r26 = r6
            r16 = r7
            r27 = r8
            r8 = r12
            r18 = r13
            r2 = r14
            r30 = r15
            r9 = 1
            r25 = 3
            r7 = r4
            r4 = 2
            goto L_0x056e
        L_0x02c7:
            r10.charactersOffset = r9
            r10.charactersEnd = r3
            if (r8 == 0) goto L_0x0348
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x032a }
            r22 = r5
            r5 = 24
            if (r0 < r5) goto L_0x034a
            java.lang.CharSequence r0 = r1.messageText     // Catch:{ Exception -> 0x030e }
            r17 = 1073741824(0x40000000, float:2.0)
            int r17 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)     // Catch:{ Exception -> 0x030e }
            int r5 = r4 + r17
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r0, r9, r3, r2, r5)     // Catch:{ Exception -> 0x030e }
            r5 = 1
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r5)     // Catch:{ Exception -> 0x030e }
            r5 = 0
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r5)     // Catch:{ Exception -> 0x030e }
            android.text.Layout$Alignment r5 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x030e }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r5)     // Catch:{ Exception -> 0x030e }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x030e }
            r10.textLayout = r0     // Catch:{ Exception -> 0x030e }
            r29 = r2
            r28 = r3
            r26 = r6
            r27 = r8
            r21 = r9
            r6 = r10
            r5 = r11
            r8 = r12
            r3 = r13
            r2 = r14
            r30 = r15
            r25 = 3
            goto L_0x037e
        L_0x030e:
            r0 = move-exception
            r29 = r2
            r28 = r3
            r26 = r6
            r16 = r7
            r27 = r8
            r21 = r9
            r6 = r10
            r5 = r11
            r8 = r12
            r18 = r13
            r2 = r14
            r30 = r15
            r9 = 1
            r25 = 3
            r7 = r4
            r4 = 2
            goto L_0x056a
        L_0x032a:
            r0 = move-exception
            r22 = r5
            r29 = r2
            r28 = r3
            r26 = r6
            r16 = r7
            r27 = r8
            r21 = r9
            r6 = r10
            r5 = r11
            r8 = r12
            r18 = r13
            r2 = r14
            r30 = r15
            r9 = 1
            r25 = 3
            r7 = r4
            r4 = 2
            goto L_0x056a
        L_0x0348:
            r22 = r5
        L_0x034a:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0550 }
            java.lang.CharSequence r5 = r1.messageText     // Catch:{ Exception -> 0x0550 }
            android.text.Layout$Alignment r17 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0550 }
            r18 = 1065353216(0x3f800000, float:1.0)
            r23 = 0
            r24 = 0
            r26 = r6
            r21 = r9
            r6 = 0
            r25 = 3
            r9 = r0
            r6 = r10
            r10 = r5
            r5 = r11
            r11 = r21
            r27 = r8
            r8 = r12
            r12 = r3
            r28 = r3
            r3 = r13
            r13 = r2
            r29 = r2
            r2 = r14
            r14 = r4
            r30 = r15
            r15 = r17
            r16 = r18
            r17 = r23
            r18 = r24
            r9.<init>(r10, r11, r12, r13, r14, r15, r16, r17, r18)     // Catch:{ Exception -> 0x0547 }
            r6.textLayout = r0     // Catch:{ Exception -> 0x0547 }
        L_0x037e:
            int r0 = r7.getLineTop(r3)     // Catch:{ Exception -> 0x0547 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x0547 }
            r6.textYOffset = r0     // Catch:{ Exception -> 0x0547 }
            if (r8 == 0) goto L_0x0399
            float r0 = r6.textYOffset     // Catch:{ Exception -> 0x038f }
            float r0 = r0 - r20
            int r0 = (int) r0     // Catch:{ Exception -> 0x038f }
            r6.height = r0     // Catch:{ Exception -> 0x038f }
            goto L_0x0399
        L_0x038f:
            r0 = move-exception
            r18 = r3
            r16 = r7
            r9 = 1
            r7 = r4
            r4 = 2
            goto L_0x056a
        L_0x0399:
            int r0 = r6.height     // Catch:{ Exception -> 0x0547 }
            android.text.StaticLayout r9 = r6.textLayout     // Catch:{ Exception -> 0x0547 }
            android.text.StaticLayout r10 = r6.textLayout     // Catch:{ Exception -> 0x0547 }
            int r10 = r10.getLineCount()     // Catch:{ Exception -> 0x0547 }
            r11 = 1
            int r10 = r10 - r11
            int r9 = r9.getLineBottom(r10)     // Catch:{ Exception -> 0x0547 }
            int r0 = java.lang.Math.max(r0, r9)     // Catch:{ Exception -> 0x0547 }
            r6.height = r0     // Catch:{ Exception -> 0x0547 }
            float r0 = r6.textYOffset     // Catch:{ Exception -> 0x0547 }
            r20 = r0
            int r14 = r2 + -1
            if (r8 != r14) goto L_0x03db
            android.text.StaticLayout r0 = r6.textLayout
            int r0 = r0.getLineCount()
            int r11 = java.lang.Math.max(r5, r0)
            int r0 = r1.textHeight     // Catch:{ Exception -> 0x03d6 }
            float r5 = r6.textYOffset     // Catch:{ Exception -> 0x03d6 }
            android.text.StaticLayout r9 = r6.textLayout     // Catch:{ Exception -> 0x03d6 }
            int r9 = r9.getHeight()     // Catch:{ Exception -> 0x03d6 }
            float r9 = (float) r9     // Catch:{ Exception -> 0x03d6 }
            float r5 = r5 + r9
            int r5 = (int) r5     // Catch:{ Exception -> 0x03d6 }
            int r0 = java.lang.Math.max(r0, r5)     // Catch:{ Exception -> 0x03d6 }
            r1.textHeight = r0     // Catch:{ Exception -> 0x03d6 }
            goto L_0x03dc
        L_0x03d6:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x03dc
        L_0x03db:
            r11 = r5
        L_0x03dc:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$TextLayoutBlock> r0 = r1.textLayoutBlocks
            r0.add(r6)
            android.text.StaticLayout r0 = r6.textLayout     // Catch:{ Exception -> 0x03f4 }
            int r5 = r11 + -1
            float r0 = r0.getLineLeft(r5)     // Catch:{ Exception -> 0x03f4 }
            if (r8 != 0) goto L_0x03f2
            r5 = 0
            int r9 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r9 < 0) goto L_0x03f2
            r1.textXOffset = r0     // Catch:{ Exception -> 0x03f4 }
        L_0x03f2:
            r5 = r0
            goto L_0x03fe
        L_0x03f4:
            r0 = move-exception
            r5 = 0
            if (r8 != 0) goto L_0x03fb
            r9 = 0
            r1.textXOffset = r9
        L_0x03fb:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03fe:
            android.text.StaticLayout r0 = r6.textLayout     // Catch:{ Exception -> 0x0408 }
            int r9 = r11 + -1
            float r0 = r0.getLineWidth(r9)     // Catch:{ Exception -> 0x0408 }
            r9 = r0
            goto L_0x040d
        L_0x0408:
            r0 = move-exception
            r9 = 0
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x040d:
            double r12 = (double) r9
            double r12 = java.lang.Math.ceil(r12)
            int r0 = (int) r12
            int r15 = r4 + 80
            if (r0 <= r15) goto L_0x0418
            r0 = r4
        L_0x0418:
            int r14 = r2 + -1
            if (r8 != r14) goto L_0x041e
            r1.lastLineWidth = r0
        L_0x041e:
            float r10 = r9 + r5
            double r12 = (double) r10
            double r12 = java.lang.Math.ceil(r12)
            int r10 = (int) r12
            r12 = r10
            r13 = 1
            if (r11 <= r13) goto L_0x04fd
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = r9
            r9 = r16
            r16 = r7
            r7 = r15
            r15 = r14
            r14 = r13
            r13 = r10
            r10 = r0
        L_0x043a:
            if (r9 >= r11) goto L_0x04d4
            android.text.StaticLayout r0 = r6.textLayout     // Catch:{ Exception -> 0x0443 }
            float r0 = r0.getLineWidth(r9)     // Catch:{ Exception -> 0x0443 }
            goto L_0x044b
        L_0x0443:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r18 = 0
            r0 = r18
        L_0x044b:
            r18 = r3
            int r3 = r4 + 20
            float r3 = (float) r3
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x0457
            float r0 = (float) r4
            r3 = r0
            goto L_0x0458
        L_0x0457:
            r3 = r0
        L_0x0458:
            android.text.StaticLayout r0 = r6.textLayout     // Catch:{ Exception -> 0x0462 }
            float r0 = r0.getLineLeft(r9)     // Catch:{ Exception -> 0x0462 }
            r23 = r11
            r11 = r0
            goto L_0x046c
        L_0x0462:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r21 = 0
            r23 = r11
            r11 = r21
        L_0x046c:
            r21 = 0
            int r0 = (r11 > r21 ? 1 : (r11 == r21 ? 0 : -1))
            if (r0 <= 0) goto L_0x0486
            float r0 = r1.textXOffset
            float r0 = java.lang.Math.min(r0, r11)
            r1.textXOffset = r0
            byte r0 = r6.directionFlags
            r24 = r4
            r4 = 1
            r0 = r0 | r4
            byte r0 = (byte) r0
            r6.directionFlags = r0
            r1.hasRtl = r4
            goto L_0x048f
        L_0x0486:
            r24 = r4
            byte r0 = r6.directionFlags
            r4 = 2
            r0 = r0 | r4
            byte r0 = (byte) r0
            r6.directionFlags = r0
        L_0x048f:
            if (r14 != 0) goto L_0x04a5
            r4 = 0
            int r0 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x04a5
            android.text.StaticLayout r0 = r6.textLayout     // Catch:{ Exception -> 0x04a1 }
            int r0 = r0.getParagraphDirection(r9)     // Catch:{ Exception -> 0x04a1 }
            r4 = 1
            if (r0 != r4) goto L_0x04a5
            r14 = 1
            goto L_0x04a5
        L_0x04a1:
            r0 = move-exception
            r4 = 1
            r14 = r4
            goto L_0x04a6
        L_0x04a5:
        L_0x04a6:
            float r15 = java.lang.Math.max(r15, r3)
            float r0 = r3 + r11
            float r7 = java.lang.Math.max(r7, r0)
            r0 = r14
            r4 = r15
            double r14 = (double) r3
            double r14 = java.lang.Math.ceil(r14)
            int r14 = (int) r14
            int r10 = java.lang.Math.max(r10, r14)
            float r14 = r3 + r11
            double r14 = (double) r14
            double r14 = java.lang.Math.ceil(r14)
            int r14 = (int) r14
            int r13 = java.lang.Math.max(r13, r14)
            int r9 = r9 + 1
            r14 = r0
            r15 = r4
            r3 = r18
            r11 = r23
            r4 = r24
            goto L_0x043a
        L_0x04d4:
            r18 = r3
            r24 = r4
            r23 = r11
            if (r14 == 0) goto L_0x04e4
            r15 = r7
            int r0 = r2 + -1
            if (r8 != r0) goto L_0x04ea
            r1.lastLineWidth = r12
            goto L_0x04ea
        L_0x04e4:
            int r0 = r2 + -1
            if (r8 != r0) goto L_0x04ea
            r1.lastLineWidth = r10
        L_0x04ea:
            int r0 = r1.textWidth
            double r3 = (double) r15
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            int r0 = java.lang.Math.max(r0, r3)
            r1.textWidth = r0
            r7 = r24
            r4 = 2
            r9 = 1
            goto L_0x0543
        L_0x04fd:
            r18 = r3
            r24 = r4
            r16 = r7
            r17 = r9
            r23 = r11
            r3 = 0
            int r4 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x052b
            float r4 = r1.textXOffset
            float r4 = java.lang.Math.min(r4, r5)
            r1.textXOffset = r4
            int r3 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x051b
            float r3 = (float) r0
            float r3 = r3 + r5
            int r0 = (int) r3
        L_0x051b:
            r9 = 1
            if (r2 == r9) goto L_0x0520
            r3 = 1
            goto L_0x0521
        L_0x0520:
            r3 = 0
        L_0x0521:
            r1.hasRtl = r3
            byte r3 = r6.directionFlags
            r3 = r3 | r9
            byte r3 = (byte) r3
            r6.directionFlags = r3
            r4 = 2
            goto L_0x0533
        L_0x052b:
            r9 = 1
            byte r3 = r6.directionFlags
            r4 = 2
            r3 = r3 | r4
            byte r3 = (byte) r3
            r6.directionFlags = r3
        L_0x0533:
            int r3 = r1.textWidth
            r7 = r24
            int r11 = java.lang.Math.min(r7, r0)
            int r3 = java.lang.Math.max(r3, r11)
            r1.textWidth = r3
            r13 = r10
            r10 = r0
        L_0x0543:
            int r0 = r18 + r23
            r13 = r0
            goto L_0x0570
        L_0x0547:
            r0 = move-exception
            r18 = r3
            r16 = r7
            r9 = 1
            r7 = r4
            r4 = 2
            goto L_0x056a
        L_0x0550:
            r0 = move-exception
            r29 = r2
            r28 = r3
            r26 = r6
            r16 = r7
            r27 = r8
            r21 = r9
            r6 = r10
            r5 = r11
            r8 = r12
            r18 = r13
            r2 = r14
            r30 = r15
            r9 = 1
            r25 = 3
            r7 = r4
            r4 = 2
        L_0x056a:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x056e:
            r13 = r18
        L_0x0570:
            int r12 = r8 + 1
            r14 = r2
            r4 = r7
            r19 = r16
            r5 = r22
            r6 = r26
            r8 = r27
            r2 = r29
            r15 = r30
            r3 = 0
            r7 = 24
            goto L_0x020e
        L_0x0585:
            return
        L_0x0586:
            r0 = move-exception
            r29 = r2
            r7 = r4
            r22 = r5
            r26 = r6
            r27 = r8
            goto L_0x059b
        L_0x0591:
            r0 = move-exception
            r22 = r5
            r26 = r6
            r27 = r8
            r29 = r14
            r7 = r15
        L_0x059b:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            return
        L_0x059f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MessageObject.generateLayout(im.bclpbkiauv.tgnet.TLRPC$User):void");
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        if (!this.messageOwner.out || this.messageOwner.from_id <= 0 || this.messageOwner.post) {
            return false;
        }
        if (this.messageOwner.fwd_from == null) {
            return true;
        }
        int selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (getDialogId() == ((long) selfUserId)) {
            if ((this.messageOwner.fwd_from.from_id != selfUserId || (this.messageOwner.fwd_from.saved_from_peer != null && this.messageOwner.fwd_from.saved_from_peer.user_id != selfUserId)) && (this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.user_id != selfUserId)) {
                return false;
            }
            return true;
        } else if (this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.user_id == selfUserId) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needDrawAvatar() {
        return (!isFromUser() && this.eventId == 0 && (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null)) ? false : true;
    }

    public boolean isNewSupport() {
        TLRPC.Message message = this.messageOwner;
        return (message == null || message.media == null || !(this.messageOwner.media instanceof TLRPCContacts.TL_messageMediaSysNotify)) ? false : true;
    }

    /* access modifiers changed from: private */
    public boolean needDrawAvatarInternal() {
        return ((!isFromChat() || !isFromUser()) && this.eventId == 0 && (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null)) ? false : true;
    }

    public boolean isFromChat() {
        TLRPC.Chat chat;
        if (getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).clientUserId) || isMegagroup() || (this.messageOwner.to_id != null && this.messageOwner.to_id.chat_id != 0)) {
            return true;
        }
        if (this.messageOwner.to_id == null || this.messageOwner.to_id.channel_id == 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id))) == null || !chat.megagroup) {
            return false;
        }
        return true;
    }

    public boolean isFromUser() {
        return this.messageOwner.from_id > 0 && !this.messageOwner.post;
    }

    public boolean isForwardedChannelPost() {
        return (this.messageOwner.from_id > 0 || this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.channel_post == 0) ? false : true;
    }

    public boolean isUnread() {
        return this.messageOwner.unread;
    }

    public boolean isContentUnread() {
        return this.messageOwner.media_unread;
    }

    public void setIsRead() {
        this.messageOwner.unread = false;
    }

    public int getUnradFlags() {
        return getUnreadFlags(this.messageOwner);
    }

    public static int getUnreadFlags(TLRPC.Message message) {
        int flags = 0;
        if (!message.unread) {
            flags = 0 | 1;
        }
        if (!message.media_unread) {
            return flags | 2;
        }
        return flags;
    }

    public void setContentIsRead() {
        this.messageOwner.media_unread = false;
    }

    public int getId() {
        return this.messageOwner.id;
    }

    public String getTranslate() {
        return this.messageOwner.trans;
    }

    public boolean isTranslating() {
        return this.messageOwner.istransing;
    }

    public int getRealId() {
        return this.messageOwner.realId != 0 ? this.messageOwner.realId : this.messageOwner.id;
    }

    public static int getMessageSize(TLRPC.Message message) {
        TLRPC.Document document;
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            document = message.media.webpage.document;
        } else if (message.media instanceof TLRPC.TL_messageMediaGame) {
            document = message.media.game.document;
        } else {
            document = message.media != null ? message.media.document : null;
        }
        if (document != null) {
            return document.size;
        }
        return 0;
    }

    public int getSize() {
        return getMessageSize(this.messageOwner);
    }

    public long getIdWithChannel() {
        long id = (long) this.messageOwner.id;
        if (this.messageOwner.to_id == null || this.messageOwner.to_id.channel_id == 0) {
            return id;
        }
        return id | (((long) this.messageOwner.to_id.channel_id) << 32);
    }

    public int getChannelId() {
        if (this.messageOwner.to_id != null) {
            return this.messageOwner.to_id.channel_id;
        }
        return 0;
    }

    public static boolean shouldEncryptPhotoOrVideo(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || isVideoMessage(message)) && message.ttl > 0 && message.ttl <= 60) {
                return true;
            }
            return false;
        } else if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (message.media instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldEncryptPhotoOrVideo() {
        return shouldEncryptPhotoOrVideo(this.messageOwner);
    }

    public static boolean isSecretPhotoOrVideo(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || isRoundVideoMessage(message) || isVideoMessage(message)) && message.ttl > 0 && message.ttl <= 60) {
                return true;
            }
            return false;
        } else if (!(message instanceof TLRPC.TL_message)) {
            return false;
        } else {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (message.media instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public static boolean isSecretMedia(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || isRoundVideoMessage(message) || isVideoMessage(message)) && message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        } else if (!(message instanceof TLRPC.TL_message)) {
            return false;
        } else {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (message.media instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean needDrawBluredPreview() {
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_message_secret) {
            int ttl = Math.max(message.ttl, this.messageOwner.media.ttl_seconds);
            if (ttl <= 0 || (((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && !isVideo() && !isGif()) || ttl > 60) && !isRoundVideo())) {
                return false;
            }
            return true;
        } else if (!(message instanceof TLRPC.TL_message)) {
            return false;
        } else {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) && this.messageOwner.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean isSecretMedia() {
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_message_secret) {
            if ((((message.media instanceof TLRPC.TL_messageMediaPhoto) || isGif()) && this.messageOwner.ttl > 0 && this.messageOwner.ttl <= 60) || isVoice() || isRoundVideo() || isVideo()) {
                return true;
            }
            return false;
        } else if (!(message instanceof TLRPC.TL_message)) {
            return false;
        } else {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) && this.messageOwner.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public static void setUnreadFlags(TLRPC.Message message, int flag) {
        boolean z = false;
        message.unread = (flag & 1) == 0;
        if ((flag & 2) == 0) {
            z = true;
        }
        message.media_unread = z;
    }

    public static boolean isUnread(TLRPC.Message message) {
        return message.unread;
    }

    public static boolean isContentUnread(TLRPC.Message message) {
        return message.media_unread;
    }

    public boolean isMegagroup() {
        return isMegagroup(this.messageOwner);
    }

    public boolean isSavedFromMegagroup() {
        if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.channel_id == 0) {
            return false;
        }
        return ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));
    }

    public static boolean isMegagroup(TLRPC.Message message) {
        return (message.flags & Integer.MIN_VALUE) != 0;
    }

    public static boolean isOut(TLRPC.Message message) {
        return message.out;
    }

    public long getDialogId() {
        return getDialogId(this.messageOwner);
    }

    public boolean canStreamVideo() {
        TLRPC.Document document = getDocument();
        if (document == null || (document instanceof TLRPC.TL_documentEncrypted)) {
            return false;
        }
        if (SharedConfig.streamAllVideo) {
            return true;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return attribute.supports_streaming;
            }
            if ((attribute instanceof TLRPC.TL_documentAttributeFilename) && document.mime_type.toLowerCase().startsWith("video/")) {
                return true;
            }
        }
        if (SharedConfig.streamMkv == 0 || !"video/x-matroska".equals(document.mime_type)) {
            return false;
        }
        return true;
    }

    public static long getDialogId(TLRPC.Message message) {
        if (message.dialog_id == 0 && message.to_id != null) {
            if (message.to_id.chat_id != 0) {
                message.dialog_id = (long) (-message.to_id.chat_id);
            } else if (message.to_id.channel_id != 0) {
                message.dialog_id = (long) (-message.to_id.channel_id);
            } else if (isOut(message)) {
                message.dialog_id = (long) message.to_id.user_id;
            } else {
                message.dialog_id = (long) message.from_id;
            }
        }
        return message.dialog_id;
    }

    public boolean isSending() {
        return this.messageOwner.send_state == 1 && this.messageOwner.id < 0;
    }

    public boolean isEditing() {
        return this.messageOwner.send_state == 3 && this.messageOwner.id > 0;
    }

    public boolean isSendError() {
        return (this.messageOwner.send_state == 2 && this.messageOwner.id < 0) || (this.scheduled && this.messageOwner.id > 0 && this.messageOwner.date < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + -60);
    }

    public boolean isSent() {
        return this.messageOwner.send_state == 0 || this.messageOwner.id > 0;
    }

    public int getSecretTimeLeft() {
        int secondsLeft = this.messageOwner.ttl;
        if (this.messageOwner.destroyTime != 0) {
            return Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
        }
        return secondsLeft;
    }

    public String getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        int secondsLeft = getSecretTimeLeft();
        if (secondsLeft < 60) {
            return secondsLeft + "s";
        }
        return (secondsLeft / 60) + "m";
    }

    public String getDocumentName() {
        return FileLoader.getDocumentFileName(getDocument());
    }

    public static boolean isStickerDocument(TLRPC.Document document) {
        if (document != null) {
            int a = 0;
            while (a < document.attributes.size()) {
                if (!(document.attributes.get(a) instanceof TLRPC.TL_documentAttributeSticker)) {
                    a++;
                } else if ("image/webp".equals(document.mime_type) || "application/x-tgsticker".equals(document.mime_type)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isAnimatedStickerDocument(TLRPC.Document document) {
        return document != null && "application/x-tgsticker".equals(document.mime_type) && !document.thumbs.isEmpty();
    }

    public static boolean canAutoplayAnimatedSticker(TLRPC.Document document) {
        return isAnimatedStickerDocument(document) && SharedConfig.getDevicePerfomanceClass() != 0;
    }

    public static boolean isMaskDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if ((attribute instanceof TLRPC.TL_documentAttributeSticker) && attribute.mask) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVoiceDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return attribute.voice;
            }
        }
        return false;
    }

    public static boolean isVoiceWebDocument(WebFile webDocument) {
        return webDocument != null && webDocument.mime_type.equals("audio/ogg");
    }

    public static boolean isImageWebDocument(WebFile webDocument) {
        return webDocument != null && !isGifDocument(webDocument) && webDocument.mime_type.startsWith("image/");
    }

    public static boolean isVideoWebDocument(WebFile webDocument) {
        return webDocument != null && webDocument.mime_type.startsWith("video/");
    }

    public static boolean isMusicDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return true ^ attribute.voice;
            }
        }
        if (TextUtils.isEmpty(document.mime_type)) {
            return false;
        }
        String mime = document.mime_type.toLowerCase();
        if (mime.equals(MimeTypes.AUDIO_FLAC) || mime.equals("audio/ogg") || mime.equals(MimeTypes.AUDIO_OPUS) || mime.equals("audio/x-opus+ogg")) {
            return true;
        }
        if (!mime.equals("application/octet-stream") || !FileLoader.getDocumentFileName(document).endsWith(".opus")) {
            return false;
        }
        return true;
    }

    public static boolean isVideoDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        boolean isAnimated = false;
        boolean isVideo = false;
        int width = 0;
        int height = 0;
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                if (attribute.round_message) {
                    return false;
                }
                isVideo = true;
                width = attribute.w;
                height = attribute.h;
            } else if (attribute instanceof TLRPC.TL_documentAttributeAnimated) {
                isAnimated = true;
            }
        }
        if (isAnimated && (width > 1280 || height > 1280)) {
            isAnimated = false;
        }
        if (SharedConfig.streamMkv && !isVideo && "video/x-matroska".equals(document.mime_type)) {
            isVideo = true;
        }
        if (!isVideo || isAnimated) {
            return false;
        }
        return true;
    }

    public TLRPC.Document getDocument() {
        TLRPC.Document document = this.emojiAnimatedSticker;
        if (document != null) {
            return document;
        }
        return getDocument(this.messageOwner);
    }

    public static TLRPC.Document getDocument(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return message.media.webpage.document;
        }
        if (message.media instanceof TLRPC.TL_messageMediaGame) {
            return message.media.game.document;
        }
        if (message.media != null) {
            return message.media.document;
        }
        return null;
    }

    public static TLRPC.Photo getPhoto(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return message.media.webpage.photo;
        }
        if (message.media != null) {
            return message.media.photo;
        }
        return null;
    }

    public static boolean isStickerMessage(TLRPC.Message message) {
        return message.media != null && isStickerDocument(message.media.document);
    }

    public static boolean isAnimatedStickerMessage(TLRPC.Message message) {
        return message.media != null && isAnimatedStickerDocument(message.media.document);
    }

    public static boolean isLocationMessage(TLRPC.Message message) {
        return (message.media instanceof TLRPC.TL_messageMediaGeo) || (message.media instanceof TLRPC.TL_messageMediaGeoLive) || (message.media instanceof TLRPC.TL_messageMediaVenue);
    }

    public static boolean isMaskMessage(TLRPC.Message message) {
        return message.media != null && isMaskDocument(message.media.document);
    }

    public static boolean isMusicMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isMusicDocument(message.media.webpage.document);
        }
        return message.media != null && isMusicDocument(message.media.document);
    }

    public static boolean isGifMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isGifDocument(message.media.webpage.document);
        }
        return message.media != null && isGifDocument(message.media.document);
    }

    public static boolean isRoundVideoMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isRoundVideoDocument(message.media.webpage.document);
        }
        return message.media != null && isRoundVideoDocument(message.media.document);
    }

    public static boolean isPhoto(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return (message.media.webpage.photo instanceof TLRPC.TL_photo) && !(message.media.webpage.document instanceof TLRPC.TL_document);
        }
        return message.media instanceof TLRPC.TL_messageMediaPhoto;
    }

    public static boolean isVoiceMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isVoiceDocument(message.media.webpage.document);
        }
        return message.media != null && isVoiceDocument(message.media.document);
    }

    public static boolean isNewGifMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isNewGifDocument(message.media.webpage.document);
        }
        return message.media != null && isNewGifDocument(message.media.document);
    }

    public static boolean isLiveLocationMessage(TLRPC.Message message) {
        return message.media instanceof TLRPC.TL_messageMediaGeoLive;
    }

    public static boolean isVideoMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isVideoDocument(message.media.webpage.document);
        }
        return message.media != null && isVideoDocument(message.media.document);
    }

    public static boolean isGameMessage(TLRPC.Message message) {
        return message.media instanceof TLRPC.TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(TLRPC.Message message) {
        return message.media instanceof TLRPC.TL_messageMediaInvoice;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Message message) {
        if (message.media == null || message.media.document == null) {
            return null;
        }
        return getInputStickerSet(message.media.document);
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        int a = 0;
        int N = document.attributes.size();
        while (a < N) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                return null;
            } else {
                return attribute.stickerset;
            }
        }
        return null;
    }

    public static long getStickerSetId(TLRPC.Document document) {
        if (document == null) {
            return -1;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                return -1;
            } else {
                return attribute.stickerset.id;
            }
        }
        return -1;
    }

    public String getStrickerChar() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return null;
        }
        Iterator<TLRPC.DocumentAttribute> it = document.attributes.iterator();
        while (it.hasNext()) {
            TLRPC.DocumentAttribute attribute = it.next();
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                return attribute.alt;
            }
        }
        return null;
    }

    public int getApproximateHeight() {
        float maxWidth;
        int photoWidth;
        int i = this.type;
        if (i == 0) {
            int height = this.textHeight + ((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || !(this.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) ? 0 : AndroidUtilities.dp(100.0f));
            if (isReply()) {
                return height + AndroidUtilities.dp(42.0f);
            }
            return height;
        } else if (i == 2) {
            return AndroidUtilities.dp(72.0f);
        } else {
            if (i == 12) {
                return AndroidUtilities.dp(71.0f);
            }
            if (i == 9) {
                return AndroidUtilities.dp(100.0f);
            }
            if (i == 4) {
                return AndroidUtilities.dp(114.0f);
            }
            if (i == 14) {
                return AndroidUtilities.dp(82.0f);
            }
            if (i == 10) {
                return AndroidUtilities.dp(30.0f);
            }
            if (i == 11) {
                return AndroidUtilities.dp(50.0f);
            }
            if (i == 5) {
                return AndroidUtilities.roundMessageSize;
            }
            if (i == 13 || i == 15) {
                float maxHeight = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                if (AndroidUtilities.isTablet()) {
                    maxWidth = ((float) AndroidUtilities.getMinTabletSide()) * 0.5f;
                } else {
                    maxWidth = ((float) AndroidUtilities.displaySize.x) * 0.5f;
                }
                int photoHeight = 0;
                int photoWidth2 = 0;
                TLRPC.Document document = getDocument();
                int a = 0;
                int N = document.attributes.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        photoWidth2 = attribute.w;
                        photoHeight = attribute.h;
                        break;
                    }
                    a++;
                }
                if (photoWidth2 == 0) {
                    photoHeight = (int) maxHeight;
                    photoWidth2 = photoHeight + AndroidUtilities.dp(100.0f);
                }
                if (((float) photoHeight) > maxHeight) {
                    photoWidth2 = (int) (((float) photoWidth2) * (maxHeight / ((float) photoHeight)));
                    photoHeight = (int) maxHeight;
                }
                if (((float) photoWidth2) > maxWidth) {
                    photoHeight = (int) (((float) photoHeight) * (maxWidth / ((float) photoWidth2)));
                }
                return AndroidUtilities.dp(14.0f) + photoHeight;
            }
            if (AndroidUtilities.isTablet()) {
                photoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
            } else {
                photoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            }
            int photoHeight2 = AndroidUtilities.dp(100.0f) + photoWidth;
            if (photoWidth > AndroidUtilities.getPhotoSize()) {
                photoWidth = AndroidUtilities.getPhotoSize();
            }
            if (photoHeight2 > AndroidUtilities.getPhotoSize()) {
                photoHeight2 = AndroidUtilities.getPhotoSize();
            }
            TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            if (currentPhotoObject != null) {
                int h = (int) (((float) currentPhotoObject.h) / (((float) currentPhotoObject.w) / ((float) photoWidth)));
                if (h == 0) {
                    h = AndroidUtilities.dp(100.0f);
                }
                if (h > photoHeight2) {
                    h = photoHeight2;
                } else if (h < AndroidUtilities.dp(120.0f)) {
                    h = AndroidUtilities.dp(120.0f);
                }
                if (needDrawBluredPreview()) {
                    if (AndroidUtilities.isTablet()) {
                        h = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                    } else {
                        h = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f);
                    }
                }
                photoHeight2 = h;
            }
            return AndroidUtilities.dp(14.0f) + photoHeight2;
        }
    }

    public String getStickerEmoji() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return null;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.alt == null || attribute.alt.length() <= 0) {
                return null;
            } else {
                return attribute.alt;
            }
        }
        return null;
    }

    public boolean isAnimatedEmoji() {
        return this.emojiAnimatedSticker != null;
    }

    public boolean isSticker() {
        int i = this.type;
        if (i != 1000) {
            return i == 13;
        }
        return isStickerDocument(getDocument());
    }

    public boolean isAnimatedSticker() {
        int i = this.type;
        if (i != 1000) {
            return i == 15;
        }
        return isAnimatedStickerDocument(getDocument());
    }

    public boolean isAnyKindOfSticker() {
        int i = this.type;
        return i == 13 || i == 15;
    }

    public boolean shouldDrawWithoutBackground() {
        int i = this.type;
        return i == 13 || i == 15 || i == 5;
    }

    public boolean isLocation() {
        return isLocationMessage(this.messageOwner);
    }

    public boolean isMask() {
        return isMaskMessage(this.messageOwner);
    }

    public boolean isMusic() {
        return isMusicMessage(this.messageOwner);
    }

    public boolean isVoice() {
        return isVoiceMessage(this.messageOwner);
    }

    public boolean isVideo() {
        return isVideoMessage(this.messageOwner);
    }

    public boolean isPhoto() {
        return isPhoto(this.messageOwner);
    }

    public boolean isLiveLocation() {
        return isLiveLocationMessage(this.messageOwner);
    }

    public boolean isGame() {
        return isGameMessage(this.messageOwner);
    }

    public boolean isInvoice() {
        return isInvoiceMessage(this.messageOwner);
    }

    public boolean isRoundVideo() {
        if (this.isRoundVideoCached == 0) {
            this.isRoundVideoCached = (this.type == 5 || isRoundVideoMessage(this.messageOwner)) ? 1 : 2;
        }
        if (this.isRoundVideoCached == 1) {
            return true;
        }
        return false;
    }

    public boolean hasPhotoStickers() {
        return (this.messageOwner.media == null || this.messageOwner.media.photo == null || !this.messageOwner.media.photo.has_stickers) ? false : true;
    }

    public boolean isGif() {
        return isGifMessage(this.messageOwner);
    }

    public boolean isWebpageDocument() {
        return (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && this.messageOwner.media.webpage.document != null && !isGifDocument(this.messageOwner.media.webpage.document);
    }

    public boolean isWebpage() {
        return this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage;
    }

    public boolean isNewGif() {
        return this.messageOwner.media != null && isNewGifDocument(this.messageOwner.media.document);
    }

    public boolean isAndroidTheme() {
        if (this.messageOwner.media == null || this.messageOwner.media.webpage == null) {
            return false;
        }
        ArrayList<TLRPC.Document> documents = this.messageOwner.media.webpage.documents;
        int N = documents.size();
        for (int a = 0; a < N; a++) {
            if ("application/x-tgtheme-android".equals(documents.get(a).mime_type)) {
                return true;
            }
        }
        return false;
    }

    public String getMusicTitle() {
        return getMusicTitle(true);
    }

    public String getMusicTitle(boolean unknown) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            int a = 0;
            while (a < document.attributes.size()) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (!attribute.voice) {
                        String title = attribute.title;
                        if (title != null && title.length() != 0) {
                            return title;
                        }
                        String title2 = FileLoader.getDocumentFileName(document);
                        if (!TextUtils.isEmpty(title2) || !unknown) {
                            return title2;
                        }
                        return LocaleController.getString("AudioUnknownTitle", R.string.AudioUnknownTitle);
                    } else if (!unknown) {
                        return null;
                    } else {
                        return LocaleController.formatDateAudio((long) this.messageOwner.date);
                    }
                } else if ((attribute instanceof TLRPC.TL_documentAttributeVideo) && attribute.round_message) {
                    return LocaleController.formatDateAudio((long) this.messageOwner.date);
                } else {
                    a++;
                }
            }
            String fileName = FileLoader.getDocumentFileName(document);
            if (!TextUtils.isEmpty(fileName)) {
                return fileName;
            }
        }
        return LocaleController.getString("AudioUnknownTitle", R.string.AudioUnknownTitle);
    }

    public int getDuration() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return 0;
        }
        int i = this.audioPlayerDuration;
        if (i > 0) {
            return i;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return attribute.duration;
            }
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return attribute.duration;
            }
        }
        return this.audioPlayerDuration;
    }

    public String getArtworkUrl(boolean small) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            int i = 0;
            int N = document.attributes.size();
            while (i < N) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(i);
                if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                    i++;
                } else if (attribute.voice) {
                    return null;
                } else {
                    String performer = attribute.performer;
                    String title = attribute.title;
                    if (!TextUtils.isEmpty(performer)) {
                        int a = 0;
                        while (true) {
                            String[] strArr = excludeWords;
                            if (a >= strArr.length) {
                                break;
                            }
                            performer = performer.replace(strArr[a], " ");
                            a++;
                        }
                    }
                    if (TextUtils.isEmpty(performer) != 0 && TextUtils.isEmpty(title)) {
                        return null;
                    }
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("athumb://itunes.apple.com/search?term=");
                        sb.append(URLEncoder.encode(performer + " - " + title, "UTF-8"));
                        sb.append("&entity=song&limit=4");
                        sb.append(small ? "&s=1" : "");
                        return sb.toString();
                    } catch (Exception e) {
                    }
                }
            }
        }
        return null;
    }

    public String getMusicAuthor() {
        return getMusicAuthor(true);
    }

    public String getMusicAuthor(boolean unknown) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            boolean isVoice = false;
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (attribute.voice) {
                        isVoice = true;
                    } else {
                        String performer = attribute.performer;
                        if (!TextUtils.isEmpty(performer) || !unknown) {
                            return performer;
                        }
                        return LocaleController.getString("AudioUnknownArtist", R.string.AudioUnknownArtist);
                    }
                } else if ((attribute instanceof TLRPC.TL_documentAttributeVideo) && attribute.round_message) {
                    isVoice = true;
                }
                if (isVoice) {
                    if (!unknown) {
                        return null;
                    }
                    if (isOutOwner() || (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId())) {
                        return LocaleController.getString("FromYou", R.string.FromYou);
                    }
                    TLRPC.User user = null;
                    TLRPC.Chat chat = null;
                    if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.channel_id != 0) {
                        chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                    } else if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_id != 0) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                    } else if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_name != null) {
                        return this.messageOwner.fwd_from.from_name;
                    } else {
                        if (this.messageOwner.from_id < 0) {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-this.messageOwner.from_id));
                        } else if (this.messageOwner.from_id != 0 || this.messageOwner.to_id.channel_id == 0) {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                        } else {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
                        }
                    }
                    if (user != null) {
                        return UserObject.getName(user);
                    }
                    if (chat != null) {
                        return chat.title;
                    }
                }
            }
        }
        return LocaleController.getString("AudioUnknownArtist", R.string.AudioUnknownArtist);
    }

    public TLRPC.InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    public boolean needDrawForwarded() {
        return ((this.messageOwner.flags & 4) == 0 || this.messageOwner.fwd_from == null || (this.messageOwner.fwd_from.saved_from_peer != null && this.messageOwner.fwd_from.saved_from_peer.channel_id == this.messageOwner.fwd_from.channel_id) || ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()) == getDialogId()) ? false : true;
    }

    public static boolean isForwardedMessage(TLRPC.Message message) {
        return ((message.flags & 4) == 0 || message.fwd_from == null) ? false : true;
    }

    public boolean isReply() {
        MessageObject messageObject = this.replyMessageObject;
        return (messageObject == null || !(messageObject.messageOwner instanceof TLRPC.TL_messageEmpty)) && !((this.messageOwner.reply_to_msg_id == 0 && this.messageOwner.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0);
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(this.messageOwner);
    }

    public boolean isMediaEmptyWebpage() {
        return isMediaEmptyWebpage(this.messageOwner);
    }

    public static boolean isMediaEmpty(TLRPC.Message message) {
        return message == null || message.media == null || (message.media instanceof TLRPC.TL_messageMediaEmpty) || (message.media instanceof TLRPC.TL_messageMediaWebPage);
    }

    public static boolean isMediaEmptyWebpage(TLRPC.Message message) {
        return message == null || message.media == null || (message.media instanceof TLRPC.TL_messageMediaEmpty);
    }

    public boolean canEditMessage(TLRPC.Chat chat) {
        return false;
    }

    public boolean canEditMessageScheduleTime(TLRPC.Chat chat) {
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0012, code lost:
        r0 = r2.type;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean canForwardMessage() {
        /*
            r2 = this;
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r2.messageOwner
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_message_secret
            if (r0 != 0) goto L_0x0022
            boolean r0 = r2.needDrawBluredPreview()
            if (r0 != 0) goto L_0x0022
            boolean r0 = r2.isLiveLocation()
            if (r0 != 0) goto L_0x0022
            int r0 = r2.type
            r1 = 16
            if (r0 == r1) goto L_0x0022
            r1 = 101(0x65, float:1.42E-43)
            if (r0 == r1) goto L_0x0022
            r1 = 102(0x66, float:1.43E-43)
            if (r0 == r1) goto L_0x0022
            r0 = 1
            goto L_0x0023
        L_0x0022:
            r0 = 0
        L_0x0023:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MessageObject.canForwardMessage():boolean");
    }

    public boolean canEditMedia() {
        if (isSecretMedia()) {
            return false;
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            return true;
        }
        if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) || isVoice() || isSticker() || isAnimatedSticker() || isRoundVideo()) {
            return false;
        }
        return true;
    }

    public boolean canEditMessageAnytime(TLRPC.Chat chat) {
        return canEditMessageAnytime(this.currentAccount, this.messageOwner, chat);
    }

    public static boolean canEditMessageAnytime(int currentAccount2, TLRPC.Message message, TLRPC.Chat chat) {
        if (message == null || message.to_id == null || ((message.media != null && (isRoundVideoDocument(message.media.document) || isStickerDocument(message.media.document) || isAnimatedStickerDocument(message.media.document))) || ((message.action != null && !(message.action instanceof TLRPC.TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0))) {
            return false;
        }
        if (message.from_id == message.to_id.user_id && message.from_id == UserConfig.getInstance(currentAccount2).getClientUserId() && !isLiveLocationMessage(message)) {
            return true;
        }
        if ((chat != null || message.to_id.channel_id == 0 || (chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(message.to_id.channel_id))) != null) && message.out && chat != null && chat.megagroup && (chat.creator || (chat.admin_rights != null && chat.admin_rights.pin_messages))) {
            return true;
        }
        return false;
    }

    public static boolean canEditMessageScheduleTime(int currentAccount2, TLRPC.Message message, TLRPC.Chat chat) {
        if (chat == null && message.to_id.channel_id != 0 && (chat = MessagesController.getInstance(currentAccount2).getChat(Integer.valueOf(message.to_id.channel_id))) == null) {
            return false;
        }
        if (!ChatObject.isChannel(chat) || chat.megagroup || chat.creator) {
            return true;
        }
        if (chat.admin_rights == null || (!chat.admin_rights.edit_messages && !message.out)) {
            return false;
        }
        return true;
    }

    public static boolean canEditMessage(int currentAccount2, TLRPC.Message message, TLRPC.Chat chat, boolean scheduled2) {
        if (scheduled2 && message.date < ConnectionsManager.getInstance(currentAccount2).getCurrentTime() - 60) {
            return false;
        }
        if ((chat != null && (chat.left || chat.kicked)) || message == null || message.to_id == null || ((message.media != null && (isRoundVideoDocument(message.media.document) || isStickerDocument(message.media.document) || isAnimatedStickerDocument(message.media.document) || isLocationMessage(message))) || ((message.action != null && !(message.action instanceof TLRPC.TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0))) {
            return false;
        }
        if (message.from_id == message.to_id.user_id && message.from_id == UserConfig.getInstance(currentAccount2).getClientUserId() && !isLiveLocationMessage(message) && !(message.media instanceof TLRPC.TL_messageMediaContact)) {
            return true;
        }
        if (chat == null && message.to_id.channel_id != 0 && (chat = MessagesController.getInstance(currentAccount2).getChat(Integer.valueOf(message.to_id.channel_id))) == null) {
            return false;
        }
        if (message.media != null && !(message.media instanceof TLRPC.TL_messageMediaEmpty) && !(message.media instanceof TLRPC.TL_messageMediaPhoto) && !(message.media instanceof TLRPC.TL_messageMediaDocument) && !(message.media instanceof TLRPC.TL_messageMediaWebPage)) {
            return false;
        }
        if (message.out && chat != null && chat.megagroup && (chat.creator || (chat.admin_rights != null && chat.admin_rights.pin_messages))) {
            return true;
        }
        if (!scheduled2 && Math.abs(message.date - ConnectionsManager.getInstance(currentAccount2).getCurrentTime()) > MessagesController.getInstance(currentAccount2).maxEditTime) {
            return false;
        }
        if (message.to_id.channel_id == 0) {
            if (!message.out && message.from_id != UserConfig.getInstance(currentAccount2).getClientUserId()) {
                return false;
            }
            if ((message.media instanceof TLRPC.TL_messageMediaPhoto) || (((message.media instanceof TLRPC.TL_messageMediaDocument) && !isStickerMessage(message) && !isAnimatedStickerMessage(message)) || (message.media instanceof TLRPC.TL_messageMediaEmpty) || (message.media instanceof TLRPC.TL_messageMediaWebPage) || message.media == null)) {
                return true;
            }
            return false;
        } else if (((!chat.megagroup || !message.out) && (chat.megagroup || ((!chat.creator && (chat.admin_rights == null || (!chat.admin_rights.edit_messages && (!message.out || !chat.admin_rights.post_messages)))) || !message.post))) || (!(message.media instanceof TLRPC.TL_messageMediaPhoto) && ((!(message.media instanceof TLRPC.TL_messageMediaDocument) || isStickerMessage(message) || isAnimatedStickerMessage(message)) && !(message.media instanceof TLRPC.TL_messageMediaEmpty) && !(message.media instanceof TLRPC.TL_messageMediaWebPage) && message.media != null))) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canDeleteMessage(boolean inScheduleMode, TLRPC.Chat chat) {
        return false;
    }

    public static boolean canDeleteMessage(int currentAccount2, boolean inScheduleMode, TLRPC.Message message, TLRPC.Chat chat) {
        return false;
    }

    public String getForwardedName() {
        if (this.messageOwner.fwd_from == null) {
            return null;
        }
        if (this.messageOwner.fwd_from.channel_id != 0) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
            if (chat != null) {
                return chat.title;
            }
            return null;
        } else if (this.messageOwner.fwd_from.from_id != 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
            if (user != null) {
                return UserObject.getName(user);
            }
            return null;
        } else if (this.messageOwner.fwd_from.from_name != null) {
            return this.messageOwner.fwd_from.from_name;
        } else {
            return null;
        }
    }

    public int getFromId() {
        if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null) {
            if (this.messageOwner.from_id != 0) {
                return this.messageOwner.from_id;
            }
            if (this.messageOwner.post) {
                return this.messageOwner.to_id.channel_id;
            }
            return 0;
        } else if (this.messageOwner.fwd_from.saved_from_peer.user_id != 0) {
            if (this.messageOwner.fwd_from.from_id != 0) {
                return this.messageOwner.fwd_from.from_id;
            }
            return this.messageOwner.fwd_from.saved_from_peer.user_id;
        } else if (this.messageOwner.fwd_from.saved_from_peer.channel_id != 0) {
            if (isSavedFromMegagroup() && this.messageOwner.fwd_from.from_id != 0) {
                return this.messageOwner.fwd_from.from_id;
            }
            if (this.messageOwner.fwd_from.channel_id != 0) {
                return -this.messageOwner.fwd_from.channel_id;
            }
            return -this.messageOwner.fwd_from.saved_from_peer.channel_id;
        } else if (this.messageOwner.fwd_from.saved_from_peer.chat_id == 0) {
            return 0;
        } else {
            if (this.messageOwner.fwd_from.from_id != 0) {
                return this.messageOwner.fwd_from.from_id;
            }
            if (this.messageOwner.fwd_from.channel_id != 0) {
                return -this.messageOwner.fwd_from.channel_id;
            }
            return -this.messageOwner.fwd_from.saved_from_peer.chat_id;
        }
    }

    public boolean isWallpaper() {
        return (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && this.messageOwner.media.webpage != null && "app_background".equals(this.messageOwner.media.webpage.type);
    }

    public int getMediaExistanceFlags() {
        int flags = 0;
        if (this.attachPathExists) {
            flags = 0 | 1;
        }
        if (this.mediaExists) {
            return flags | 2;
        }
        return flags;
    }

    public void applyMediaExistanceFlags(int flags) {
        if (flags == -1) {
            checkMediaExistance();
            return;
        }
        boolean z = false;
        this.attachPathExists = (flags & 1) != 0;
        if ((flags & 2) != 0) {
            z = true;
        }
        this.mediaExists = z;
    }

    public void checkMediaExistance() {
        TLRPC.PhotoSize currentPhotoObject;
        this.attachPathExists = false;
        this.mediaExists = false;
        int i = this.type;
        if (i == 1) {
            if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
                File file = FileLoader.getPathToMessage(this.messageOwner);
                if (needDrawBluredPreview()) {
                    this.mediaExists = new File(file.getAbsolutePath() + ".enc").exists();
                }
                if (!this.mediaExists) {
                    this.mediaExists = file.exists();
                }
            }
        } else if (i == 8 || i == 3 || i == 9 || i == 2 || i == 14 || i == 5) {
            if (this.messageOwner.attachPath != null && this.messageOwner.attachPath.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                File file2 = FileLoader.getPathToMessage(this.messageOwner);
                if (this.type == 3 && needDrawBluredPreview()) {
                    this.mediaExists = new File(file2.getAbsolutePath() + ".enc").exists();
                }
                if (!this.mediaExists) {
                    this.mediaExists = file2.exists();
                }
            }
        } else {
            TLRPC.Document document = getDocument();
            if (document != null) {
                if (isWallpaper()) {
                    this.mediaExists = FileLoader.getPathToAttach(document, true).exists();
                } else {
                    this.mediaExists = FileLoader.getPathToAttach(document).exists();
                }
            } else if (this.type == 0 && (currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize())) != null && currentPhotoObject != null) {
                this.mediaExists = FileLoader.getPathToAttach(currentPhotoObject, true).exists();
            }
        }
    }

    public boolean equals(MessageObject obj) {
        return getId() == obj.getId() && getDialogId() == obj.getDialogId();
    }
}
