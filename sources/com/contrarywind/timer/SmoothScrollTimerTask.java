package com.contrarywind.timer;

import com.contrarywind.view.WheelView;
import java.util.TimerTask;

public final class SmoothScrollTimerTask extends TimerTask {
    private int offset;
    private int realOffset = 0;
    private int realTotalOffset = Integer.MAX_VALUE;
    private final WheelView wheelView;

    public SmoothScrollTimerTask(WheelView wheelView2, int offset2) {
        this.wheelView = wheelView2;
        this.offset = offset2;
    }

    public final void run() {
        if (this.realTotalOffset == Integer.MAX_VALUE) {
            this.realTotalOffset = this.offset;
        }
        int i = this.realTotalOffset;
        int i2 = (int) (((float) i) * 0.1f);
        this.realOffset = i2;
        if (i2 == 0) {
            if (i < 0) {
                this.realOffset = -1;
            } else {
                this.realOffset = 1;
            }
        }
        if (Math.abs(this.realTotalOffset) <= 1) {
            this.wheelView.cancelFuture();
            this.wheelView.getHandler().sendEmptyMessage(3000);
            return;
        }
        WheelView wheelView2 = this.wheelView;
        wheelView2.setTotalScrollY(wheelView2.getTotalScrollY() + ((float) this.realOffset));
        if (!this.wheelView.isLoop()) {
            float itemHeight = this.wheelView.getItemHeight();
            float bottom = ((float) ((this.wheelView.getItemsCount() - 1) - this.wheelView.getInitPosition())) * itemHeight;
            if (this.wheelView.getTotalScrollY() <= ((float) (-this.wheelView.getInitPosition())) * itemHeight || this.wheelView.getTotalScrollY() >= bottom) {
                WheelView wheelView3 = this.wheelView;
                wheelView3.setTotalScrollY(wheelView3.getTotalScrollY() - ((float) this.realOffset));
                this.wheelView.cancelFuture();
                this.wheelView.getHandler().sendEmptyMessage(3000);
                return;
            }
        }
        this.wheelView.getHandler().sendEmptyMessage(1000);
        this.realTotalOffset -= this.realOffset;
    }
}
