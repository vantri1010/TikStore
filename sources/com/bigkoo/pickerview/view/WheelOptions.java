package com.bigkoo.pickerview.view;

import android.graphics.Typeface;
import android.view.View;
import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import java.util.List;

public class WheelOptions<T> {
    /* access modifiers changed from: private */
    public boolean isRestoreItem;
    private boolean linkage = true;
    private List<T> mOptions1Items;
    /* access modifiers changed from: private */
    public List<List<T>> mOptions2Items;
    /* access modifiers changed from: private */
    public List<List<List<T>>> mOptions3Items;
    /* access modifiers changed from: private */
    public OnOptionsSelectChangeListener optionsSelectChangeListener;
    private View view;
    private OnItemSelectedListener wheelListener_option1;
    /* access modifiers changed from: private */
    public OnItemSelectedListener wheelListener_option2;
    /* access modifiers changed from: private */
    public WheelView wv_option1;
    /* access modifiers changed from: private */
    public WheelView wv_option2;
    /* access modifiers changed from: private */
    public WheelView wv_option3;

    public View getView() {
        return this.view;
    }

    public void setView(View view2) {
        this.view = view2;
    }

    public WheelOptions(View view2, boolean isRestoreItem2) {
        this.isRestoreItem = isRestoreItem2;
        this.view = view2;
        this.wv_option1 = (WheelView) view2.findViewById(R.id.options1);
        this.wv_option2 = (WheelView) view2.findViewById(R.id.options2);
        this.wv_option3 = (WheelView) view2.findViewById(R.id.options3);
    }

    public void setPicker(List<T> options1Items, List<List<T>> options2Items, List<List<List<T>>> options3Items) {
        this.mOptions1Items = options1Items;
        this.mOptions2Items = options2Items;
        this.mOptions3Items = options3Items;
        this.wv_option1.setAdapter(new ArrayWheelAdapter(options1Items));
        this.wv_option1.setCurrentItem(0);
        List<List<T>> list = this.mOptions2Items;
        if (list != null) {
            this.wv_option2.setAdapter(new ArrayWheelAdapter(list.get(0)));
        }
        WheelView wheelView = this.wv_option2;
        wheelView.setCurrentItem(wheelView.getCurrentItem());
        List<List<List<T>>> list2 = this.mOptions3Items;
        if (list2 != null) {
            this.wv_option3.setAdapter(new ArrayWheelAdapter((List) list2.get(0).get(0)));
        }
        WheelView wheelView2 = this.wv_option3;
        wheelView2.setCurrentItem(wheelView2.getCurrentItem());
        this.wv_option1.setIsOptions(true);
        this.wv_option2.setIsOptions(true);
        this.wv_option3.setIsOptions(true);
        if (this.mOptions2Items == null) {
            this.wv_option2.setVisibility(8);
        } else {
            this.wv_option2.setVisibility(0);
        }
        if (this.mOptions3Items == null) {
            this.wv_option3.setVisibility(8);
        } else {
            this.wv_option3.setVisibility(0);
        }
        this.wheelListener_option1 = new OnItemSelectedListener() {
            public void onItemSelected(int index) {
                int opt2Select = 0;
                if (WheelOptions.this.mOptions2Items != null) {
                    if (!WheelOptions.this.isRestoreItem) {
                        int opt2Select2 = WheelOptions.this.wv_option2.getCurrentItem();
                        opt2Select = opt2Select2 >= ((List) WheelOptions.this.mOptions2Items.get(index)).size() + -1 ? ((List) WheelOptions.this.mOptions2Items.get(index)).size() - 1 : opt2Select2;
                    }
                    WheelOptions.this.wv_option2.setAdapter(new ArrayWheelAdapter((List) WheelOptions.this.mOptions2Items.get(index)));
                    WheelOptions.this.wv_option2.setCurrentItem(opt2Select);
                    if (WheelOptions.this.mOptions3Items != null) {
                        WheelOptions.this.wheelListener_option2.onItemSelected(opt2Select);
                    } else if (WheelOptions.this.optionsSelectChangeListener != null) {
                        WheelOptions.this.optionsSelectChangeListener.onOptionsSelectChanged(index, opt2Select, 0);
                    }
                } else if (WheelOptions.this.optionsSelectChangeListener != null) {
                    WheelOptions.this.optionsSelectChangeListener.onOptionsSelectChanged(WheelOptions.this.wv_option1.getCurrentItem(), 0, 0);
                }
            }
        };
        this.wheelListener_option2 = new OnItemSelectedListener() {
            public void onItemSelected(int index) {
                if (WheelOptions.this.mOptions3Items != null) {
                    int opt1Select = WheelOptions.this.wv_option1.getCurrentItem();
                    int opt1Select2 = opt1Select >= WheelOptions.this.mOptions3Items.size() + -1 ? WheelOptions.this.mOptions3Items.size() - 1 : opt1Select;
                    int index2 = index >= ((List) WheelOptions.this.mOptions2Items.get(opt1Select2)).size() + -1 ? ((List) WheelOptions.this.mOptions2Items.get(opt1Select2)).size() - 1 : index;
                    int opt3 = 0;
                    if (!WheelOptions.this.isRestoreItem) {
                        opt3 = WheelOptions.this.wv_option3.getCurrentItem() >= ((List) ((List) WheelOptions.this.mOptions3Items.get(opt1Select2)).get(index2)).size() + -1 ? ((List) ((List) WheelOptions.this.mOptions3Items.get(opt1Select2)).get(index2)).size() - 1 : WheelOptions.this.wv_option3.getCurrentItem();
                    }
                    WheelOptions.this.wv_option3.setAdapter(new ArrayWheelAdapter((List) ((List) WheelOptions.this.mOptions3Items.get(WheelOptions.this.wv_option1.getCurrentItem())).get(index2)));
                    WheelOptions.this.wv_option3.setCurrentItem(opt3);
                    if (WheelOptions.this.optionsSelectChangeListener != null) {
                        WheelOptions.this.optionsSelectChangeListener.onOptionsSelectChanged(WheelOptions.this.wv_option1.getCurrentItem(), index2, opt3);
                    }
                } else if (WheelOptions.this.optionsSelectChangeListener != null) {
                    WheelOptions.this.optionsSelectChangeListener.onOptionsSelectChanged(WheelOptions.this.wv_option1.getCurrentItem(), index, 0);
                }
            }
        };
        if (options1Items != null && this.linkage) {
            this.wv_option1.setOnItemSelectedListener(this.wheelListener_option1);
        }
        if (options2Items != null && this.linkage) {
            this.wv_option2.setOnItemSelectedListener(this.wheelListener_option2);
        }
        if (options3Items != null && this.linkage && this.optionsSelectChangeListener != null) {
            this.wv_option3.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(int index) {
                    WheelOptions.this.optionsSelectChangeListener.onOptionsSelectChanged(WheelOptions.this.wv_option1.getCurrentItem(), WheelOptions.this.wv_option2.getCurrentItem(), index);
                }
            });
        }
    }

    public void setNPicker(List<T> options1Items, List<T> options2Items, List<T> options3Items) {
        this.wv_option1.setAdapter(new ArrayWheelAdapter(options1Items));
        this.wv_option1.setCurrentItem(0);
        if (options2Items != null) {
            this.wv_option2.setAdapter(new ArrayWheelAdapter(options2Items));
        }
        WheelView wheelView = this.wv_option2;
        wheelView.setCurrentItem(wheelView.getCurrentItem());
        if (options3Items != null) {
            this.wv_option3.setAdapter(new ArrayWheelAdapter(options3Items));
        }
        WheelView wheelView2 = this.wv_option3;
        wheelView2.setCurrentItem(wheelView2.getCurrentItem());
        this.wv_option1.setIsOptions(true);
        this.wv_option2.setIsOptions(true);
        this.wv_option3.setIsOptions(true);
        if (this.optionsSelectChangeListener != null) {
            this.wv_option1.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(int index) {
                    WheelOptions.this.optionsSelectChangeListener.onOptionsSelectChanged(index, WheelOptions.this.wv_option2.getCurrentItem(), WheelOptions.this.wv_option3.getCurrentItem());
                }
            });
        }
        if (options2Items == null) {
            this.wv_option2.setVisibility(8);
        } else {
            this.wv_option2.setVisibility(0);
            if (this.optionsSelectChangeListener != null) {
                this.wv_option2.setOnItemSelectedListener(new OnItemSelectedListener() {
                    public void onItemSelected(int index) {
                        WheelOptions.this.optionsSelectChangeListener.onOptionsSelectChanged(WheelOptions.this.wv_option1.getCurrentItem(), index, WheelOptions.this.wv_option3.getCurrentItem());
                    }
                });
            }
        }
        if (options3Items == null) {
            this.wv_option3.setVisibility(8);
            return;
        }
        this.wv_option3.setVisibility(0);
        if (this.optionsSelectChangeListener != null) {
            this.wv_option3.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(int index) {
                    WheelOptions.this.optionsSelectChangeListener.onOptionsSelectChanged(WheelOptions.this.wv_option1.getCurrentItem(), WheelOptions.this.wv_option2.getCurrentItem(), index);
                }
            });
        }
    }

    public void setTextContentSize(int textSize) {
        this.wv_option1.setTextSize((float) textSize);
        this.wv_option2.setTextSize((float) textSize);
        this.wv_option3.setTextSize((float) textSize);
    }

    private void setLineSpacingMultiplier() {
    }

    public void setLabels(String label1, String label2, String label3) {
        if (label1 != null) {
            this.wv_option1.setLabel(label1);
        }
        if (label2 != null) {
            this.wv_option2.setLabel(label2);
        }
        if (label3 != null) {
            this.wv_option3.setLabel(label3);
        }
    }

    public void setTextXOffset(int x_offset_one, int x_offset_two, int x_offset_three) {
        this.wv_option1.setTextXOffset(x_offset_one);
        this.wv_option2.setTextXOffset(x_offset_two);
        this.wv_option3.setTextXOffset(x_offset_three);
    }

    public void setCyclic(boolean cyclic) {
        this.wv_option1.setCyclic(cyclic);
        this.wv_option2.setCyclic(cyclic);
        this.wv_option3.setCyclic(cyclic);
    }

    public void setTypeface(Typeface font) {
        this.wv_option1.setTypeface(font);
        this.wv_option2.setTypeface(font);
        this.wv_option3.setTypeface(font);
    }

    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        this.wv_option1.setCyclic(cyclic1);
        this.wv_option2.setCyclic(cyclic2);
        this.wv_option3.setCyclic(cyclic3);
    }

    public int[] getCurrentItems() {
        int[] currentItems = new int[3];
        int i = 0;
        currentItems[0] = this.wv_option1.getCurrentItem();
        List<List<T>> list = this.mOptions2Items;
        if (list == null || list.size() <= 0) {
            currentItems[1] = this.wv_option2.getCurrentItem();
        } else {
            currentItems[1] = this.wv_option2.getCurrentItem() > this.mOptions2Items.get(currentItems[0]).size() - 1 ? 0 : this.wv_option2.getCurrentItem();
        }
        List<List<List<T>>> list2 = this.mOptions3Items;
        if (list2 == null || list2.size() <= 0) {
            currentItems[2] = this.wv_option3.getCurrentItem();
        } else {
            if (this.wv_option3.getCurrentItem() <= ((List) this.mOptions3Items.get(currentItems[0]).get(currentItems[1])).size() - 1) {
                i = this.wv_option3.getCurrentItem();
            }
            currentItems[2] = i;
        }
        return currentItems;
    }

    public void setCurrentItems(int option1, int option2, int option3) {
        if (this.linkage) {
            itemSelected(option1, option2, option3);
            return;
        }
        this.wv_option1.setCurrentItem(option1);
        this.wv_option2.setCurrentItem(option2);
        this.wv_option3.setCurrentItem(option3);
    }

    private void itemSelected(int opt1Select, int opt2Select, int opt3Select) {
        if (this.mOptions1Items != null) {
            this.wv_option1.setCurrentItem(opt1Select);
        }
        List<List<T>> list = this.mOptions2Items;
        if (list != null) {
            this.wv_option2.setAdapter(new ArrayWheelAdapter(list.get(opt1Select)));
            this.wv_option2.setCurrentItem(opt2Select);
        }
        List<List<List<T>>> list2 = this.mOptions3Items;
        if (list2 != null) {
            this.wv_option3.setAdapter(new ArrayWheelAdapter((List) list2.get(opt1Select).get(opt2Select)));
            this.wv_option3.setCurrentItem(opt3Select);
        }
    }

    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.wv_option1.setLineSpacingMultiplier(lineSpacingMultiplier);
        this.wv_option2.setLineSpacingMultiplier(lineSpacingMultiplier);
        this.wv_option3.setLineSpacingMultiplier(lineSpacingMultiplier);
    }

    public void setDividerColor(int dividerColor) {
        this.wv_option1.setDividerColor(dividerColor);
        this.wv_option2.setDividerColor(dividerColor);
        this.wv_option3.setDividerColor(dividerColor);
    }

    public void setDividerType(WheelView.DividerType dividerType) {
        this.wv_option1.setDividerType(dividerType);
        this.wv_option2.setDividerType(dividerType);
        this.wv_option3.setDividerType(dividerType);
    }

    public void setTextColorCenter(int textColorCenter) {
        this.wv_option1.setTextColorCenter(textColorCenter);
        this.wv_option2.setTextColorCenter(textColorCenter);
        this.wv_option3.setTextColorCenter(textColorCenter);
    }

    public void setTextColorOut(int textColorOut) {
        this.wv_option1.setTextColorOut(textColorOut);
        this.wv_option2.setTextColorOut(textColorOut);
        this.wv_option3.setTextColorOut(textColorOut);
    }

    public void isCenterLabel(boolean isCenterLabel) {
        this.wv_option1.isCenterLabel(isCenterLabel);
        this.wv_option2.isCenterLabel(isCenterLabel);
        this.wv_option3.isCenterLabel(isCenterLabel);
    }

    public void setOptionsSelectChangeListener(OnOptionsSelectChangeListener optionsSelectChangeListener2) {
        this.optionsSelectChangeListener = optionsSelectChangeListener2;
    }

    public void setLinkage(boolean linkage2) {
        this.linkage = linkage2;
    }

    public void setItemsVisible(int itemsVisible) {
        this.wv_option1.setItemsVisibleCount(itemsVisible);
        this.wv_option2.setItemsVisibleCount(itemsVisible);
        this.wv_option3.setItemsVisibleCount(itemsVisible);
    }

    public void setAlphaGradient(boolean isAlphaGradient) {
        this.wv_option1.setAlphaGradient(isAlphaGradient);
        this.wv_option2.setAlphaGradient(isAlphaGradient);
        this.wv_option3.setAlphaGradient(isAlphaGradient);
    }
}
