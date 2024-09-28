package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import im.bclpbkiauv.messenger.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SegmenteView extends RadioGroup {
    private int defaultSelection;
    private boolean equalWidth;
    private String identifier;
    private LinkedHashMap<String, String> itemMap;
    private Context mCtx;
    /* access modifiers changed from: private */
    public OnSelectionChangedListener mListener;
    private int mSdk;
    private ArrayList<RadioButton> options;
    private int selectedColor;
    private int selectedTextColor;
    private RadioGroup.OnCheckedChangeListener selectionChangedlistener;
    private boolean stretch;
    private ColorStateList textColorStateList;
    private int unselectedColor;
    private int unselectedTextColor;

    public interface OnSelectionChangedListener {
        void onItemSelected(int i);
    }

    public SegmenteView(Context context) {
        super(context, (AttributeSet) null);
        this.selectedColor = Color.parseColor("#FFD63B52");
        this.unselectedColor = 0;
        this.unselectedTextColor = Color.parseColor("#FFD63B52");
        this.defaultSelection = -1;
        this.stretch = false;
        this.selectedTextColor = -1;
        this.equalWidth = false;
        this.identifier = "";
        this.itemMap = new LinkedHashMap<>();
        this.selectionChangedlistener = new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.findViewById(checkedId).isPressed() && SegmenteView.this.mListener != null) {
                    SegmenteView.this.mListener.onItemSelected(checkedId);
                }
            }
        };
        init(context);
        update();
    }

    /* JADX INFO: finally extract failed */
    public SegmenteView(Context context, AttributeSet attrs) throws Exception {
        super(context, attrs);
        this.selectedColor = Color.parseColor("#FFD63B52");
        this.unselectedColor = 0;
        this.unselectedTextColor = Color.parseColor("#FFD63B52");
        this.defaultSelection = -1;
        this.stretch = false;
        this.selectedTextColor = -1;
        this.equalWidth = false;
        this.identifier = "";
        this.itemMap = new LinkedHashMap<>();
        this.selectionChangedlistener = new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.findViewById(checkedId).isPressed() && SegmenteView.this.mListener != null) {
                    SegmenteView.this.mListener.onItemSelected(checkedId);
                }
            }
        };
        init(context);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MultipleSelectionButton, 0, 0);
        try {
            this.selectedColor = attributes.getColor(4, this.selectedColor);
            this.selectedTextColor = attributes.getColor(5, this.selectedTextColor);
            this.unselectedColor = attributes.getColor(7, this.unselectedColor);
            this.unselectedTextColor = attributes.getColor(8, this.selectedColor);
            this.textColorStateList = new ColorStateList(new int[][]{new int[]{-16842912}, new int[]{16842912}}, new int[]{this.unselectedTextColor, this.selectedTextColor});
            this.defaultSelection = attributes.getInt(0, this.defaultSelection);
            this.equalWidth = attributes.getBoolean(1, this.equalWidth);
            this.stretch = attributes.getBoolean(6, this.stretch);
            this.identifier = attributes.getString(2);
            CharSequence[] itemArray = attributes.getTextArray(3);
            CharSequence[] valueArray = attributes.getTextArray(9);
            if (isInEditMode()) {
                itemArray = new CharSequence[]{"YES", "NO", "MAYBE", "DON'T KNOW"};
            }
            if (!(itemArray == null || valueArray == null)) {
                if (itemArray.length != valueArray.length) {
                    throw new Exception("Item labels and value arrays must be the same size");
                }
            }
            if (itemArray != null) {
                if (valueArray != null) {
                    for (int i = 0; i < itemArray.length; i++) {
                        this.itemMap.put(itemArray[i].toString(), valueArray[i].toString());
                    }
                } else {
                    for (CharSequence item : itemArray) {
                        this.itemMap.put(item.toString(), item.toString());
                    }
                }
            }
            attributes.recycle();
            update();
        } catch (Throwable th) {
            attributes.recycle();
            throw th;
        }
    }

    private void init(Context context) {
        this.mCtx = context;
        this.mSdk = Build.VERSION.SDK_INT;
        setPadding(10, 10, 10, 10);
    }

    private void update() {
        removeAllViews();
        float f = 1.0f;
        int twoDP = (int) TypedValue.applyDimension(1, 1.0f, getResources().getDisplayMetrics());
        setOrientation(0);
        float textWidth = 0.0f;
        this.options = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, String> item : this.itemMap.entrySet()) {
            RadioButton rb = new RadioButton(this.mCtx);
            rb.setTextColor(this.textColorStateList);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(-2, -1);
            if (this.stretch) {
                params.weight = f;
            }
            if (i > 0) {
                params.setMargins(-twoDP, 0, 0, 0);
            }
            rb.setLayoutParams(params);
            rb.setButtonDrawable(new StateListDrawable());
            if (i == 0) {
                GradientDrawable leftUnselected = (GradientDrawable) this.mCtx.getResources().getDrawable(R.drawable.left_option).mutate();
                leftUnselected.setStroke(twoDP, this.selectedColor);
                leftUnselected.setColor(this.unselectedColor);
                GradientDrawable leftSelected = (GradientDrawable) this.mCtx.getResources().getDrawable(R.drawable.left_option_selected).mutate();
                leftSelected.setColor(this.selectedColor);
                leftSelected.setStroke(twoDP, this.selectedColor);
                StateListDrawable leftStateListDrawable = new StateListDrawable();
                leftStateListDrawable.addState(new int[]{-16842912}, leftUnselected);
                leftStateListDrawable.addState(new int[]{16842912}, leftSelected);
                if (this.mSdk < 16) {
                    rb.setBackgroundDrawable(leftStateListDrawable);
                } else {
                    rb.setBackground(leftStateListDrawable);
                }
            } else if (i == this.itemMap.size() - 1) {
                GradientDrawable rightUnselected = (GradientDrawable) this.mCtx.getResources().getDrawable(R.drawable.right_option).mutate();
                rightUnselected.setStroke(twoDP, this.selectedColor);
                rightUnselected.setColor(this.unselectedColor);
                GradientDrawable rightSelected = (GradientDrawable) this.mCtx.getResources().getDrawable(R.drawable.right_option_selected).mutate();
                rightSelected.setColor(this.selectedColor);
                rightSelected.setStroke(twoDP, this.selectedColor);
                StateListDrawable rightStateListDrawable = new StateListDrawable();
                rightStateListDrawable.addState(new int[]{-16842912}, rightUnselected);
                rightStateListDrawable.addState(new int[]{16842912}, rightSelected);
                if (this.mSdk < 16) {
                    rb.setBackgroundDrawable(rightStateListDrawable);
                } else {
                    rb.setBackground(rightStateListDrawable);
                }
            } else {
                GradientDrawable middleUnselected = (GradientDrawable) this.mCtx.getResources().getDrawable(R.drawable.middle_option).mutate();
                middleUnselected.setStroke(twoDP, this.selectedColor);
                middleUnselected.setDither(true);
                middleUnselected.setColor(this.unselectedColor);
                GradientDrawable middleSelected = (GradientDrawable) this.mCtx.getResources().getDrawable(R.drawable.middle_option_selected).mutate();
                middleSelected.setColor(this.selectedColor);
                middleSelected.setStroke(twoDP, this.selectedColor);
                StateListDrawable middleStateListDrawable = new StateListDrawable();
                middleStateListDrawable.addState(new int[]{-16842912}, middleUnselected);
                middleStateListDrawable.addState(new int[]{16842912}, middleSelected);
                if (this.mSdk < 16) {
                    rb.setBackgroundDrawable(middleStateListDrawable);
                } else {
                    rb.setBackground(middleStateListDrawable);
                }
            }
            rb.setLayoutParams(params);
            rb.setMinWidth(twoDP * 10);
            rb.setGravity(17);
            rb.setTypeface((Typeface) null, 1);
            rb.setText(item.getKey());
            textWidth = Math.max(rb.getPaint().measureText(item.getKey()), textWidth);
            this.options.add(rb);
            i++;
            f = 1.0f;
        }
        Iterator<RadioButton> it = this.options.iterator();
        while (it.hasNext()) {
            RadioButton option = it.next();
            if (this.equalWidth) {
                option.setWidth((int) (((float) (twoDP * 20)) + textWidth));
            }
            addView(option);
        }
        setOnCheckedChangeListener(this.selectionChangedlistener);
        int i2 = this.defaultSelection;
        if (i2 > -1) {
            check(((RadioButton) getChildAt(i2)).getId());
        }
    }

    public String[] getCheckedWithIdentifier() {
        return new String[]{this.identifier, this.itemMap.get(((RadioButton) findViewById(getCheckedRadioButtonId())).getText().toString())};
    }

    public String getChecked() {
        return this.itemMap.get(((RadioButton) findViewById(getCheckedRadioButtonId())).getText().toString());
    }

    public void setItems(String[] itemArray, String[] valueArray) throws Exception {
        this.itemMap.clear();
        if (itemArray == null || valueArray == null || itemArray.length == valueArray.length) {
            if (itemArray != null) {
                if (valueArray != null) {
                    for (int i = 0; i < itemArray.length; i++) {
                        this.itemMap.put(itemArray[i].toString(), valueArray[i].toString());
                    }
                } else {
                    for (CharSequence item : itemArray) {
                        this.itemMap.put(item.toString(), item.toString());
                    }
                }
            }
            update();
            return;
        }
        throw new Exception("Item labels and value arrays must be the same size");
    }

    public void setItems(String[] items, String[] values, int defaultSelection2) throws Exception {
        if (defaultSelection2 <= items.length - 1) {
            this.defaultSelection = defaultSelection2;
            setItems(items, values);
            return;
        }
        throw new Exception("Default selection cannot be greater than the number of items");
    }

    public void setDefaultSelection(int defaultSelection2) throws Exception {
        if (defaultSelection2 <= this.itemMap.size() - 1) {
            this.defaultSelection = defaultSelection2;
            update();
            return;
        }
        throw new Exception("Default selection cannot be greater than the number of items");
    }

    public void setColors(int primaryColor, int secondaryColor) {
        this.selectedColor = primaryColor;
        this.selectedTextColor = secondaryColor;
        this.unselectedColor = secondaryColor;
        this.unselectedTextColor = primaryColor;
        this.textColorStateList = new ColorStateList(new int[][]{new int[]{-16842912}, new int[]{16842912}}, new int[]{this.unselectedTextColor, this.selectedTextColor});
        update();
    }

    public void setColors(int selectedColor2, int selectedTextColor2, int unselectedColor2, int unselectedTextColor2) {
        this.selectedColor = selectedColor2;
        this.selectedTextColor = selectedTextColor2;
        this.unselectedColor = unselectedColor2;
        this.unselectedTextColor = unselectedTextColor2;
        this.textColorStateList = new ColorStateList(new int[][]{new int[]{-16842912}, new int[]{16842912}}, new int[]{unselectedTextColor2, selectedTextColor2});
        update();
    }

    public void setByValue(String value) {
        String buttonText = "";
        if (this.itemMap.containsValue(value)) {
            for (String entry : this.itemMap.keySet()) {
                if (this.itemMap.get(entry).equalsIgnoreCase(value)) {
                    buttonText = entry;
                }
            }
        }
        Iterator<RadioButton> it = this.options.iterator();
        while (it.hasNext()) {
            RadioButton option = it.next();
            if (option.getText().toString().equalsIgnoreCase(buttonText)) {
                check(option.getId());
            }
        }
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.mListener = listener;
    }

    public void setIdentifier(String identifier2) {
        this.identifier = identifier2;
    }

    public void setEqualWidth(boolean equalWidth2) {
        this.equalWidth = equalWidth2;
        update();
    }

    public void setStretch(boolean stretch2) {
        this.stretch = stretch2;
        update();
    }
}
