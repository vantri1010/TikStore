package com.bigkoo.pickerview.adapter;

import com.contrarywind.adapter.WheelAdapter;
import java.util.List;

public class ArrayWheelAdapter<T> implements WheelAdapter {
    private List<T> items;

    public ArrayWheelAdapter(List<T> items2) {
        this.items = items2;
    }

    public Object getItem(int index) {
        if (index < 0 || index >= this.items.size()) {
            return "";
        }
        return this.items.get(index);
    }

    public int getItemsCount() {
        return this.items.size();
    }

    public int indexOf(Object o) {
        return this.items.indexOf(o);
    }
}
