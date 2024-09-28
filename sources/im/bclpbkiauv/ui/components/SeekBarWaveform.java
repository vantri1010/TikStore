package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.ui.components.SeekBar;

public class SeekBarWaveform {
    private static Paint paintInner;
    private static Paint paintOuter;
    private SeekBar.SeekBarDelegate delegate;
    private int height;
    private int innerColor;
    private MessageObject messageObject;
    private int outerColor;
    private View parentView;
    private boolean pressed = false;
    private boolean selected;
    private int selectedColor;
    private boolean startDraging = false;
    private float startX;
    private int thumbDX = 0;
    private int thumbX = 0;
    private byte[] waveformBytes;
    private int width;

    public SeekBarWaveform(Context context) {
        if (paintInner == null) {
            paintInner = new Paint();
            paintOuter = new Paint();
        }
    }

    public void setDelegate(SeekBar.SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public void setColors(int inner, int outer, int selected2) {
        this.innerColor = inner;
        this.outerColor = outer;
        this.selectedColor = selected2;
    }

    public void setWaveform(byte[] waveform) {
        this.waveformBytes = waveform;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public void setMessageObject(MessageObject object) {
        this.messageObject = object;
    }

    public void setParentView(View view) {
        this.parentView = view;
    }

    public boolean isStartDraging() {
        return this.startDraging;
    }

    public boolean onTouch(int action, float x, float y) {
        SeekBar.SeekBarDelegate seekBarDelegate;
        if (action == 0) {
            if (0.0f <= x && x <= ((float) this.width) && y >= 0.0f && y <= ((float) this.height)) {
                this.startX = x;
                this.pressed = true;
                this.thumbDX = (int) (x - ((float) this.thumbX));
                this.startDraging = false;
                return true;
            }
        } else if (action == 1 || action == 3) {
            if (this.pressed) {
                if (action == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(((float) this.thumbX) / ((float) this.width));
                }
                this.pressed = false;
                return true;
            }
        } else if (action == 2 && this.pressed) {
            if (this.startDraging) {
                int i = (int) (x - ((float) this.thumbDX));
                this.thumbX = i;
                if (i < 0) {
                    this.thumbX = 0;
                } else {
                    int i2 = this.width;
                    if (i > i2) {
                        this.thumbX = i2;
                    }
                }
            }
            float f = this.startX;
            if (f != -1.0f && Math.abs(x - f) > AndroidUtilities.getPixelsInCM(0.2f, true)) {
                View view = this.parentView;
                if (!(view == null || view.getParent() == null)) {
                    this.parentView.getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.startDraging = true;
                this.startX = -1.0f;
            }
            return true;
        }
        return false;
    }

    public void setProgress(float progress) {
        int ceil = (int) Math.ceil((double) (((float) this.width) * progress));
        this.thumbX = ceil;
        if (ceil < 0) {
            this.thumbX = 0;
            return;
        }
        int i = this.width;
        if (ceil > i) {
            this.thumbX = i;
        }
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public void draw(Canvas canvas) {
        int i;
        float samplesPerBar;
        int samplesCount;
        float totalBarsCount;
        float barCounter;
        float samplesPerBar2;
        if (this.waveformBytes != null && (i = this.width) != 0) {
            float totalBarsCount2 = (float) (i / AndroidUtilities.dp(3.0f));
            if (totalBarsCount2 > 0.1f) {
                int i2 = 5;
                int samplesCount2 = (this.waveformBytes.length * 8) / 5;
                float samplesPerBar3 = ((float) samplesCount2) / totalBarsCount2;
                float barCounter2 = 0.0f;
                int nextBarNum = 0;
                Paint paint = paintInner;
                MessageObject messageObject2 = this.messageObject;
                paint.setColor((messageObject2 == null || messageObject2.isOutOwner() || !this.messageObject.isContentUnread()) ? this.selected ? this.selectedColor : this.innerColor : this.outerColor);
                paintOuter.setColor(this.outerColor);
                int i3 = 2;
                int y = (this.height - AndroidUtilities.dp(14.0f)) / 2;
                int barNum = 0;
                int a = 0;
                while (a < samplesCount2) {
                    if (a != nextBarNum) {
                        totalBarsCount = totalBarsCount2;
                        samplesCount = samplesCount2;
                        samplesPerBar = samplesPerBar3;
                    } else {
                        int drawBarCount = 0;
                        int lastBarNum = nextBarNum;
                        while (lastBarNum == nextBarNum) {
                            barCounter2 += samplesPerBar3;
                            nextBarNum = (int) barCounter2;
                            drawBarCount++;
                        }
                        int bitPointer = a * 5;
                        int byteNum = bitPointer / 8;
                        int byteBitOffset = bitPointer - (byteNum * 8);
                        int currentByteCount = 8 - byteBitOffset;
                        int nextByteRest = 5 - currentByteCount;
                        byte value = (byte) ((this.waveformBytes[byteNum] >> byteBitOffset) & ((i3 << (Math.min(i2, currentByteCount) - 1)) - 1));
                        if (nextByteRest > 0) {
                            int i4 = byteNum + 1;
                            byte[] bArr = this.waveformBytes;
                            totalBarsCount = totalBarsCount2;
                            if (i4 < bArr.length) {
                                value = (byte) ((bArr[byteNum + 1] & ((2 << (nextByteRest - 1)) - 1)) | ((byte) (value << nextByteRest)));
                            }
                        } else {
                            totalBarsCount = totalBarsCount2;
                        }
                        int b = 0;
                        while (b < drawBarCount) {
                            int x = barNum * AndroidUtilities.dp(3.0f);
                            int samplesCount3 = samplesCount2;
                            if (x >= this.thumbX || AndroidUtilities.dp(2.0f) + x >= this.thumbX) {
                                samplesPerBar2 = samplesPerBar3;
                                barCounter = barCounter2;
                                canvas.drawRect((float) x, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, (((float) value) * 14.0f) / 31.0f)) + y), (float) (AndroidUtilities.dp(2.0f) + x), (float) (y + AndroidUtilities.dp(14.0f)), paintInner);
                                if (x < this.thumbX) {
                                    canvas.drawRect((float) x, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, (((float) value) * 14.0f) / 31.0f)) + y), (float) this.thumbX, (float) (y + AndroidUtilities.dp(14.0f)), paintOuter);
                                }
                            } else {
                                samplesPerBar2 = samplesPerBar3;
                                barCounter = barCounter2;
                                canvas.drawRect((float) x, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, (((float) value) * 14.0f) / 31.0f)) + y), (float) (AndroidUtilities.dp(2.0f) + x), (float) (y + AndroidUtilities.dp(14.0f)), paintOuter);
                            }
                            barNum++;
                            b++;
                            samplesCount2 = samplesCount3;
                            samplesPerBar3 = samplesPerBar2;
                            barCounter2 = barCounter;
                        }
                        samplesCount = samplesCount2;
                        samplesPerBar = samplesPerBar3;
                        float f = barCounter2;
                    }
                    a++;
                    totalBarsCount2 = totalBarsCount;
                    samplesCount2 = samplesCount;
                    samplesPerBar3 = samplesPerBar;
                    i2 = 5;
                    i3 = 2;
                }
            }
        }
    }
}
