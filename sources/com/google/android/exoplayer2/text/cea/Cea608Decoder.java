package com.google.android.exoplayer2.text.cea;

import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.baidu.location.BDLocation;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.text.SubtitleInputBuffer;
import com.google.android.exoplayer2.text.SubtitleOutputBuffer;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import im.bclpbkiauv.messenger.MessageObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.jvm.internal.ByteCompanionObject;

public final class Cea608Decoder extends CeaDecoder {
    private static final int[] BASIC_CHARACTER_SET = {32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 225, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 233, 93, 237, 243, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 231, 247, 209, 241, 9632};
    private static final int CC_FIELD_FLAG = 1;
    private static final byte CC_IMPLICIT_DATA_HEADER = -4;
    private static final int CC_MODE_PAINT_ON = 3;
    private static final int CC_MODE_POP_ON = 2;
    private static final int CC_MODE_ROLL_UP = 1;
    private static final int CC_MODE_UNKNOWN = 0;
    private static final int CC_TYPE_FLAG = 2;
    private static final int CC_VALID_FLAG = 4;
    private static final int[] COLUMN_INDICES = {0, 4, 8, 12, 16, 20, 24, 28};
    private static final byte CTRL_BACKSPACE = 33;
    private static final byte CTRL_CARRIAGE_RETURN = 45;
    private static final byte CTRL_DELETE_TO_END_OF_ROW = 36;
    private static final byte CTRL_END_OF_CAPTION = 47;
    private static final byte CTRL_ERASE_DISPLAYED_MEMORY = 44;
    private static final byte CTRL_ERASE_NON_DISPLAYED_MEMORY = 46;
    private static final byte CTRL_RESUME_CAPTION_LOADING = 32;
    private static final byte CTRL_RESUME_DIRECT_CAPTIONING = 41;
    private static final byte CTRL_ROLL_UP_CAPTIONS_2_ROWS = 37;
    private static final byte CTRL_ROLL_UP_CAPTIONS_3_ROWS = 38;
    private static final byte CTRL_ROLL_UP_CAPTIONS_4_ROWS = 39;
    private static final int DEFAULT_CAPTIONS_ROW_COUNT = 4;
    private static final int NTSC_CC_FIELD_1 = 0;
    private static final int NTSC_CC_FIELD_2 = 1;
    private static final boolean[] ODD_PARITY_BYTE_TABLE = {false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false};
    private static final int[] ROW_INDICES = {11, 1, 3, 12, 14, 5, 7, 9};
    private static final int[] SPECIAL_CHARACTER_SET = {174, 176, PsExtractor.PRIVATE_STREAM_1, 191, 8482, BDLocation.TypeServerDecryptError, 163, 9834, 224, 32, 232, 226, 234, 238, 244, 251};
    private static final int[] SPECIAL_ES_FR_CHARACTER_SET = {193, 201, 211, 218, 220, 252, 8216, BDLocation.TypeNetWorkLocation, 42, 39, 8212, 169, 8480, 8226, 8220, 8221, PsExtractor.AUDIO_STREAM, 194, 199, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 202, 203, 235, 206, MessageObject.TYPE_LIVE, 239, 212, 217, 249, 219, 171, 187};
    private static final int[] SPECIAL_PT_DE_CHARACTER_SET = {195, 227, 205, 204, 236, 210, 242, 213, 245, 123, 125, 92, 94, 95, 124, 126, 196, 228, 214, 246, 223, 165, 164, 9474, 197, 229, 216, 248, 9484, 9488, 9492, 9496};
    /* access modifiers changed from: private */
    public static final int[] STYLE_COLORS = {-1, -16711936, -16776961, -16711681, SupportMenu.CATEGORY_MASK, InputDeviceCompat.SOURCE_ANY, -65281};
    private static final int STYLE_ITALICS = 7;
    private static final int STYLE_UNCHANGED = 8;
    private int captionMode;
    private int captionRowCount;
    private boolean captionValid;
    private final ParsableByteArray ccData = new ParsableByteArray();
    private final ArrayList<CueBuilder> cueBuilders = new ArrayList<>();
    private List<Cue> cues;
    private CueBuilder currentCueBuilder = new CueBuilder(0, 4);
    private List<Cue> lastCues;
    private final int packetLength;
    private byte repeatableControlCc1;
    private byte repeatableControlCc2;
    private boolean repeatableControlSet;
    private final int selectedField;

    public /* bridge */ /* synthetic */ SubtitleInputBuffer dequeueInputBuffer() throws SubtitleDecoderException {
        return super.dequeueInputBuffer();
    }

    public /* bridge */ /* synthetic */ SubtitleOutputBuffer dequeueOutputBuffer() throws SubtitleDecoderException {
        return super.dequeueOutputBuffer();
    }

    public /* bridge */ /* synthetic */ void queueInputBuffer(SubtitleInputBuffer subtitleInputBuffer) throws SubtitleDecoderException {
        super.queueInputBuffer(subtitleInputBuffer);
    }

    public /* bridge */ /* synthetic */ void setPositionUs(long j) {
        super.setPositionUs(j);
    }

    public Cea608Decoder(String mimeType, int accessibilityChannel) {
        this.packetLength = MimeTypes.APPLICATION_MP4CEA608.equals(mimeType) ? 2 : 3;
        if (accessibilityChannel == 3 || accessibilityChannel == 4) {
            this.selectedField = 2;
        } else {
            this.selectedField = 1;
        }
        setCaptionMode(0);
        resetCueBuilders();
    }

    public String getName() {
        return "Cea608Decoder";
    }

    public void flush() {
        super.flush();
        this.cues = null;
        this.lastCues = null;
        setCaptionMode(0);
        setCaptionRowCount(4);
        resetCueBuilders();
        this.captionValid = false;
        this.repeatableControlSet = false;
        this.repeatableControlCc1 = 0;
        this.repeatableControlCc2 = 0;
    }

    public void release() {
    }

    /* access modifiers changed from: protected */
    public boolean isNewSubtitleDataAvailable() {
        return this.cues != this.lastCues;
    }

    /* access modifiers changed from: protected */
    public Subtitle createSubtitle() {
        List<Cue> list = this.cues;
        this.lastCues = list;
        return new CeaSubtitle(list);
    }

    /* access modifiers changed from: protected */
    public void decode(SubtitleInputBuffer inputBuffer) {
        byte ccHeader;
        this.ccData.reset(inputBuffer.data.array(), inputBuffer.data.limit());
        boolean captionDataProcessed = false;
        while (true) {
            int bytesLeft = this.ccData.bytesLeft();
            int i = this.packetLength;
            boolean z = true;
            if (bytesLeft < i) {
                break;
            }
            if (i == 2) {
                ccHeader = CC_IMPLICIT_DATA_HEADER;
            } else {
                ccHeader = (byte) this.ccData.readUnsignedByte();
            }
            int ccByte1 = this.ccData.readUnsignedByte();
            int ccByte2 = this.ccData.readUnsignedByte();
            if ((ccHeader & 2) == 0 && ((this.selectedField != 1 || (ccHeader & 1) == 0) && (this.selectedField != 2 || (ccHeader & 1) == 1))) {
                byte ccData1 = (byte) (ccByte1 & 127);
                byte ccData2 = (byte) (ccByte2 & 127);
                if (ccData1 != 0 || ccData2 != 0) {
                    boolean repeatedControlPossible = this.repeatableControlSet;
                    this.repeatableControlSet = false;
                    boolean previousCaptionValid = this.captionValid;
                    if ((ccHeader & 4) != 4) {
                        z = false;
                    }
                    this.captionValid = z;
                    if (z) {
                        captionDataProcessed = true;
                        boolean[] zArr = ODD_PARITY_BYTE_TABLE;
                        if (!zArr[ccByte1] || !zArr[ccByte2]) {
                            resetCueBuilders();
                        } else if ((ccData1 & 247) == 17 && (ccData2 & 240) == 48) {
                            this.currentCueBuilder.append(getSpecialChar(ccData2));
                        } else if ((ccData1 & 246) == 18 && (ccData2 & 224) == 32) {
                            this.currentCueBuilder.backspace();
                            if ((ccData1 & 1) == 0) {
                                this.currentCueBuilder.append(getExtendedEsFrChar(ccData2));
                            } else {
                                this.currentCueBuilder.append(getExtendedPtDeChar(ccData2));
                            }
                        } else if ((ccData1 & 224) == 0) {
                            handleCtrl(ccData1, ccData2, repeatedControlPossible);
                        } else {
                            this.currentCueBuilder.append(getChar(ccData1));
                            if ((ccData2 & 224) != 0) {
                                this.currentCueBuilder.append(getChar(ccData2));
                            }
                        }
                    } else if (previousCaptionValid) {
                        resetCueBuilders();
                        captionDataProcessed = true;
                    }
                }
            }
        }
        if (captionDataProcessed) {
            int i2 = this.captionMode;
            if (i2 == 1 || i2 == 3) {
                this.cues = getDisplayCues();
            }
        }
    }

    private void handleCtrl(byte cc1, byte cc2, boolean repeatedControlPossible) {
        if (isRepeatable(cc1)) {
            if (!repeatedControlPossible || this.repeatableControlCc1 != cc1 || this.repeatableControlCc2 != cc2) {
                this.repeatableControlSet = true;
                this.repeatableControlCc1 = cc1;
                this.repeatableControlCc2 = cc2;
            } else {
                return;
            }
        }
        if (isMidrowCtrlCode(cc1, cc2)) {
            handleMidrowCtrl(cc2);
        } else if (isPreambleAddressCode(cc1, cc2)) {
            handlePreambleAddressCode(cc1, cc2);
        } else if (isTabCtrlCode(cc1, cc2)) {
            int unused = this.currentCueBuilder.tabOffset = cc2 - 32;
        } else if (isMiscCode(cc1, cc2)) {
            handleMiscCode(cc2);
        }
    }

    private void handleMidrowCtrl(byte cc2) {
        this.currentCueBuilder.append(' ');
        boolean underline = true;
        if ((cc2 & 1) != 1) {
            underline = false;
        }
        CueBuilder cueBuilder = this.currentCueBuilder;
        cueBuilder.setStyle((cc2 >> 1) & 7, underline);
    }

    private void handlePreambleAddressCode(byte cc1, byte cc2) {
        int row = ROW_INDICES[cc1 & 7];
        boolean underline = false;
        if ((cc2 & CTRL_RESUME_CAPTION_LOADING) != 0) {
            row++;
        }
        if (row != this.currentCueBuilder.row) {
            if (this.captionMode != 1 && !this.currentCueBuilder.isEmpty()) {
                CueBuilder cueBuilder = new CueBuilder(this.captionMode, this.captionRowCount);
                this.currentCueBuilder = cueBuilder;
                this.cueBuilders.add(cueBuilder);
            }
            int unused = this.currentCueBuilder.row = row;
        }
        boolean isCursor = (cc2 & 16) == 16;
        if ((cc2 & 1) == 1) {
            underline = true;
        }
        int cursorOrStyle = (cc2 >> 1) & 7;
        this.currentCueBuilder.setStyle(isCursor ? 8 : cursorOrStyle, underline);
        if (isCursor) {
            int unused2 = this.currentCueBuilder.indent = COLUMN_INDICES[cursorOrStyle];
        }
    }

    private void handleMiscCode(byte cc2) {
        if (cc2 == 32) {
            setCaptionMode(2);
        } else if (cc2 != 41) {
            switch (cc2) {
                case 37:
                    setCaptionMode(1);
                    setCaptionRowCount(2);
                    return;
                case 38:
                    setCaptionMode(1);
                    setCaptionRowCount(3);
                    return;
                case 39:
                    setCaptionMode(1);
                    setCaptionRowCount(4);
                    return;
                default:
                    int i = this.captionMode;
                    if (i != 0) {
                        if (cc2 != 33) {
                            switch (cc2) {
                                case 44:
                                    this.cues = Collections.emptyList();
                                    int i2 = this.captionMode;
                                    if (i2 == 1 || i2 == 3) {
                                        resetCueBuilders();
                                        return;
                                    }
                                    return;
                                case 45:
                                    if (i == 1 && !this.currentCueBuilder.isEmpty()) {
                                        this.currentCueBuilder.rollUp();
                                        return;
                                    }
                                    return;
                                case 46:
                                    resetCueBuilders();
                                    return;
                                case 47:
                                    this.cues = getDisplayCues();
                                    resetCueBuilders();
                                    return;
                                default:
                                    return;
                            }
                        } else {
                            this.currentCueBuilder.backspace();
                            return;
                        }
                    } else {
                        return;
                    }
            }
        } else {
            setCaptionMode(3);
        }
    }

    private List<Cue> getDisplayCues() {
        int positionAnchor = 2;
        int cueBuilderCount = this.cueBuilders.size();
        List<Cue> cueBuilderCues = new ArrayList<>(cueBuilderCount);
        for (int i = 0; i < cueBuilderCount; i++) {
            Cue cue = this.cueBuilders.get(i).build(Integer.MIN_VALUE);
            cueBuilderCues.add(cue);
            if (cue != null) {
                positionAnchor = Math.min(positionAnchor, cue.positionAnchor);
            }
        }
        List<Cue> displayCues = new ArrayList<>(cueBuilderCount);
        for (int i2 = 0; i2 < cueBuilderCount; i2++) {
            Cue cue2 = cueBuilderCues.get(i2);
            if (cue2 != null) {
                if (cue2.positionAnchor != positionAnchor) {
                    cue2 = this.cueBuilders.get(i2).build(positionAnchor);
                }
                displayCues.add(cue2);
            }
        }
        return displayCues;
    }

    private void setCaptionMode(int captionMode2) {
        if (this.captionMode != captionMode2) {
            int oldCaptionMode = this.captionMode;
            this.captionMode = captionMode2;
            if (captionMode2 == 3) {
                for (int i = 0; i < this.cueBuilders.size(); i++) {
                    this.cueBuilders.get(i).setCaptionMode(captionMode2);
                }
                return;
            }
            resetCueBuilders();
            if (oldCaptionMode == 3 || captionMode2 == 1 || captionMode2 == 0) {
                this.cues = Collections.emptyList();
            }
        }
    }

    private void setCaptionRowCount(int captionRowCount2) {
        this.captionRowCount = captionRowCount2;
        this.currentCueBuilder.setCaptionRowCount(captionRowCount2);
    }

    private void resetCueBuilders() {
        this.currentCueBuilder.reset(this.captionMode);
        this.cueBuilders.clear();
        this.cueBuilders.add(this.currentCueBuilder);
    }

    private static char getChar(byte ccData2) {
        return (char) BASIC_CHARACTER_SET[(ccData2 & ByteCompanionObject.MAX_VALUE) - 32];
    }

    private static char getSpecialChar(byte ccData2) {
        return (char) SPECIAL_CHARACTER_SET[ccData2 & 15];
    }

    private static char getExtendedEsFrChar(byte ccData2) {
        return (char) SPECIAL_ES_FR_CHARACTER_SET[ccData2 & 31];
    }

    private static char getExtendedPtDeChar(byte ccData2) {
        return (char) SPECIAL_PT_DE_CHARACTER_SET[ccData2 & 31];
    }

    private static boolean isMidrowCtrlCode(byte cc1, byte cc2) {
        return (cc1 & 247) == 17 && (cc2 & 240) == 32;
    }

    private static boolean isPreambleAddressCode(byte cc1, byte cc2) {
        return (cc1 & 240) == 16 && (cc2 & 192) == 64;
    }

    private static boolean isTabCtrlCode(byte cc1, byte cc2) {
        return (cc1 & 247) == 23 && cc2 >= 33 && cc2 <= 35;
    }

    private static boolean isMiscCode(byte cc1, byte cc2) {
        return (cc1 & 247) == 20 && (cc2 & 240) == 32;
    }

    private static boolean isRepeatable(byte cc1) {
        return (cc1 & 240) == 16;
    }

    private static class CueBuilder {
        private static final int BASE_ROW = 15;
        private static final int SCREEN_CHARWIDTH = 32;
        private int captionMode;
        private int captionRowCount;
        private final StringBuilder captionStringBuilder = new StringBuilder();
        private final List<CueStyle> cueStyles = new ArrayList();
        /* access modifiers changed from: private */
        public int indent;
        private final List<SpannableString> rolledUpCaptions = new ArrayList();
        /* access modifiers changed from: private */
        public int row;
        /* access modifiers changed from: private */
        public int tabOffset;

        public CueBuilder(int captionMode2, int captionRowCount2) {
            reset(captionMode2);
            setCaptionRowCount(captionRowCount2);
        }

        public void reset(int captionMode2) {
            this.captionMode = captionMode2;
            this.cueStyles.clear();
            this.rolledUpCaptions.clear();
            this.captionStringBuilder.setLength(0);
            this.row = 15;
            this.indent = 0;
            this.tabOffset = 0;
        }

        public boolean isEmpty() {
            return this.cueStyles.isEmpty() && this.rolledUpCaptions.isEmpty() && this.captionStringBuilder.length() == 0;
        }

        public void setCaptionMode(int captionMode2) {
            this.captionMode = captionMode2;
        }

        public void setCaptionRowCount(int captionRowCount2) {
            this.captionRowCount = captionRowCount2;
        }

        public void setStyle(int style, boolean underline) {
            this.cueStyles.add(new CueStyle(style, underline, this.captionStringBuilder.length()));
        }

        public void backspace() {
            int length = this.captionStringBuilder.length();
            if (length > 0) {
                this.captionStringBuilder.delete(length - 1, length);
                int i = this.cueStyles.size() - 1;
                while (i >= 0) {
                    CueStyle style = this.cueStyles.get(i);
                    if (style.start == length) {
                        style.start--;
                        i--;
                    } else {
                        return;
                    }
                }
            }
        }

        public void append(char text) {
            this.captionStringBuilder.append(text);
        }

        public void rollUp() {
            this.rolledUpCaptions.add(buildCurrentLine());
            this.captionStringBuilder.setLength(0);
            this.cueStyles.clear();
            int numRows = Math.min(this.captionRowCount, this.row);
            while (this.rolledUpCaptions.size() >= numRows) {
                this.rolledUpCaptions.remove(0);
            }
        }

        public Cue build(int forcedPositionAnchor) {
            int positionAnchor;
            float position;
            int lineAnchor;
            int line;
            SpannableStringBuilder cueString = new SpannableStringBuilder();
            for (int i = 0; i < this.rolledUpCaptions.size(); i++) {
                cueString.append(this.rolledUpCaptions.get(i));
                cueString.append(10);
            }
            cueString.append(buildCurrentLine());
            if (cueString.length() == 0) {
                return null;
            }
            int startPadding = this.indent + this.tabOffset;
            int endPadding = (32 - startPadding) - cueString.length();
            int startEndPaddingDelta = startPadding - endPadding;
            if (forcedPositionAnchor != Integer.MIN_VALUE) {
                positionAnchor = forcedPositionAnchor;
            } else if (this.captionMode == 2 && (Math.abs(startEndPaddingDelta) < 3 || endPadding < 0)) {
                positionAnchor = 1;
            } else if (this.captionMode != 2 || startEndPaddingDelta <= 0) {
                positionAnchor = 0;
            } else {
                positionAnchor = 2;
            }
            if (positionAnchor == 1) {
                position = 0.5f;
            } else if (positionAnchor != 2) {
                position = (0.8f * (((float) startPadding) / 32.0f)) + 0.1f;
            } else {
                position = (0.8f * (((float) (32 - endPadding)) / 32.0f)) + 0.1f;
            }
            if (this.captionMode == 1 || this.row > 7) {
                lineAnchor = 2;
                line = (this.row - 15) - 2;
            } else {
                lineAnchor = 0;
                line = this.row;
            }
            int i2 = line;
            return new Cue(cueString, Layout.Alignment.ALIGN_NORMAL, (float) line, 1, lineAnchor, position, positionAnchor, Float.MIN_VALUE);
        }

        private SpannableString buildCurrentLine() {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.captionStringBuilder);
            int length = builder.length();
            int underlineStartPosition = -1;
            int italicStartPosition = -1;
            int colorStartPosition = 0;
            int color = -1;
            boolean nextItalic = false;
            int nextColor = -1;
            for (int i = 0; i < this.cueStyles.size(); i++) {
                CueStyle cueStyle = this.cueStyles.get(i);
                boolean underline = cueStyle.underline;
                int style = cueStyle.style;
                if (style != 8) {
                    nextItalic = style == 7;
                    nextColor = style == 7 ? nextColor : Cea608Decoder.STYLE_COLORS[style];
                }
                int position = cueStyle.start;
                if (position != (i + 1 < this.cueStyles.size() ? this.cueStyles.get(i + 1).start : length)) {
                    if (underlineStartPosition != -1 && !underline) {
                        setUnderlineSpan(builder, underlineStartPosition, position);
                        underlineStartPosition = -1;
                    } else if (underlineStartPosition == -1 && underline) {
                        underlineStartPosition = position;
                    }
                    if (italicStartPosition != -1 && !nextItalic) {
                        setItalicSpan(builder, italicStartPosition, position);
                        italicStartPosition = -1;
                    } else if (italicStartPosition == -1 && nextItalic) {
                        italicStartPosition = position;
                    }
                    if (nextColor != color) {
                        setColorSpan(builder, colorStartPosition, position, color);
                        color = nextColor;
                        colorStartPosition = position;
                    }
                }
            }
            if (!(underlineStartPosition == -1 || underlineStartPosition == length)) {
                setUnderlineSpan(builder, underlineStartPosition, length);
            }
            if (!(italicStartPosition == -1 || italicStartPosition == length)) {
                setItalicSpan(builder, italicStartPosition, length);
            }
            if (colorStartPosition != length) {
                setColorSpan(builder, colorStartPosition, length, color);
            }
            return new SpannableString(builder);
        }

        private static void setUnderlineSpan(SpannableStringBuilder builder, int start, int end) {
            builder.setSpan(new UnderlineSpan(), start, end, 33);
        }

        private static void setItalicSpan(SpannableStringBuilder builder, int start, int end) {
            builder.setSpan(new StyleSpan(2), start, end, 33);
        }

        private static void setColorSpan(SpannableStringBuilder builder, int start, int end, int color) {
            if (color != -1) {
                builder.setSpan(new ForegroundColorSpan(color), start, end, 33);
            }
        }

        private static class CueStyle {
            public int start;
            public final int style;
            public final boolean underline;

            public CueStyle(int style2, boolean underline2, int start2) {
                this.style = style2;
                this.underline = underline2;
                this.start = start2;
            }
        }
    }
}
