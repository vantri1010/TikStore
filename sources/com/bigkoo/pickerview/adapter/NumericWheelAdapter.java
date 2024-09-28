package com.bigkoo.pickerview.adapter;

import com.contrarywind.adapter.WheelAdapter;

public class NumericWheelAdapter implements WheelAdapter {
    private int maxValue;
    private int minValue;

    public NumericWheelAdapter(int minValue2, int maxValue2) {
        this.minValue = minValue2;
        this.maxValue = maxValue2;
    }

    public Object getItem(int index) {
        if (index < 0 || index >= getItemsCount()) {
            return 0;
        }
        return Integer.valueOf(this.minValue + index);
    }

    public int getItemsCount() {
        return (this.maxValue - this.minValue) + 1;
    }

    public int indexOf(Object o) {
        try {
            return ((Integer) o).intValue() - this.minValue;
        } catch (Exception e) {
            return -1;
        }
    }
}
