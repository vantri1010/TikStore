package com.bigkoo.pickerview.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.configure.PickerOptions;
import java.util.List;

public class OptionsPickerView<T> extends BasePickerView implements View.OnClickListener {
    private static final String TAG_CANCEL = "cancel";
    private static final String TAG_SUBMIT = "submit";
    private WheelOptions<T> wheelOptions;

    public OptionsPickerView(PickerOptions pickerOptions) {
        super(pickerOptions.context);
        this.mPickerOptions = pickerOptions;
        initView(pickerOptions.context);
    }

    private void initView(Context context) {
        setDialogOutSideCancelable();
        initViews();
        initAnim();
        initEvents();
        if (this.mPickerOptions.customListener == null) {
            LayoutInflater.from(context).inflate(this.mPickerOptions.layoutRes, this.contentContainer);
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
            RelativeLayout rv_top_bar = (RelativeLayout) findViewById(R.id.rv_topbar);
            Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
            Button btnCancel = (Button) findViewById(R.id.btnCancel);
            btnSubmit.setTag(TAG_SUBMIT);
            btnCancel.setTag(TAG_CANCEL);
            btnSubmit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnSubmit.setText(TextUtils.isEmpty(this.mPickerOptions.textContentConfirm) ? context.getResources().getString(R.string.pickerview_submit) : this.mPickerOptions.textContentConfirm);
            btnCancel.setText(TextUtils.isEmpty(this.mPickerOptions.textContentCancel) ? context.getResources().getString(R.string.pickerview_cancel) : this.mPickerOptions.textContentCancel);
            tvTitle.setText(TextUtils.isEmpty(this.mPickerOptions.textContentTitle) ? "" : this.mPickerOptions.textContentTitle);
            btnSubmit.setTextColor(this.mPickerOptions.textColorConfirm);
            btnCancel.setTextColor(this.mPickerOptions.textColorCancel);
            tvTitle.setTextColor(this.mPickerOptions.textColorTitle);
            rv_top_bar.setBackgroundColor(this.mPickerOptions.bgColorTitle);
            btnSubmit.setTextSize((float) this.mPickerOptions.textSizeSubmitCancel);
            btnCancel.setTextSize((float) this.mPickerOptions.textSizeSubmitCancel);
            tvTitle.setTextSize((float) this.mPickerOptions.textSizeTitle);
        } else {
            this.mPickerOptions.customListener.customLayout(LayoutInflater.from(context).inflate(this.mPickerOptions.layoutRes, this.contentContainer));
        }
        LinearLayout optionsPicker = (LinearLayout) findViewById(R.id.optionspicker);
        optionsPicker.setBackgroundColor(this.mPickerOptions.bgColorWheel);
        this.wheelOptions = new WheelOptions<>(optionsPicker, this.mPickerOptions.isRestoreItem);
        if (this.mPickerOptions.optionsSelectChangeListener != null) {
            this.wheelOptions.setOptionsSelectChangeListener(this.mPickerOptions.optionsSelectChangeListener);
        }
        this.wheelOptions.setTextContentSize(this.mPickerOptions.textSizeContent);
        this.wheelOptions.setItemsVisible(this.mPickerOptions.itemsVisibleCount);
        this.wheelOptions.setAlphaGradient(this.mPickerOptions.isAlphaGradient);
        this.wheelOptions.setLabels(this.mPickerOptions.label1, this.mPickerOptions.label2, this.mPickerOptions.label3);
        this.wheelOptions.setTextXOffset(this.mPickerOptions.x_offset_one, this.mPickerOptions.x_offset_two, this.mPickerOptions.x_offset_three);
        this.wheelOptions.setCyclic(this.mPickerOptions.cyclic1, this.mPickerOptions.cyclic2, this.mPickerOptions.cyclic3);
        this.wheelOptions.setTypeface(this.mPickerOptions.font);
        setOutSideCancelable(this.mPickerOptions.cancelable);
        this.wheelOptions.setDividerColor(this.mPickerOptions.dividerColor);
        this.wheelOptions.setDividerType(this.mPickerOptions.dividerType);
        this.wheelOptions.setLineSpacingMultiplier(this.mPickerOptions.lineSpacingMultiplier);
        this.wheelOptions.setTextColorOut(this.mPickerOptions.textColorOut);
        this.wheelOptions.setTextColorCenter(this.mPickerOptions.textColorCenter);
        this.wheelOptions.isCenterLabel(this.mPickerOptions.isCenterLabel);
    }

    public void setTitleText(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }

    public void setSelectOptions(int option1) {
        this.mPickerOptions.option1 = option1;
        reSetCurrentItems();
    }

    public void setSelectOptions(int option1, int option2) {
        this.mPickerOptions.option1 = option1;
        this.mPickerOptions.option2 = option2;
        reSetCurrentItems();
    }

    public void setSelectOptions(int option1, int option2, int option3) {
        this.mPickerOptions.option1 = option1;
        this.mPickerOptions.option2 = option2;
        this.mPickerOptions.option3 = option3;
        reSetCurrentItems();
    }

    private void reSetCurrentItems() {
        WheelOptions<T> wheelOptions2 = this.wheelOptions;
        if (wheelOptions2 != null) {
            wheelOptions2.setCurrentItems(this.mPickerOptions.option1, this.mPickerOptions.option2, this.mPickerOptions.option3);
        }
    }

    public void setPicker(List<T> optionsItems) {
        setPicker(optionsItems, (List) null, (List) null);
    }

    public void setPicker(List<T> options1Items, List<List<T>> options2Items) {
        setPicker(options1Items, options2Items, (List) null);
    }

    public void setPicker(List<T> options1Items, List<List<T>> options2Items, List<List<List<T>>> options3Items) {
        this.wheelOptions.setPicker(options1Items, options2Items, options3Items);
        reSetCurrentItems();
    }

    public void setNPicker(List<T> options1Items, List<T> options2Items, List<T> options3Items) {
        this.wheelOptions.setLinkage(false);
        this.wheelOptions.setNPicker(options1Items, options2Items, options3Items);
        reSetCurrentItems();
    }

    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_SUBMIT)) {
            returnData();
        } else if (tag.equals(TAG_CANCEL) && this.mPickerOptions.cancelListener != null) {
            this.mPickerOptions.cancelListener.onClick(v);
        }
        dismiss();
    }

    public void returnData() {
        if (this.mPickerOptions.optionsSelectListener != null) {
            int[] optionsCurrentItems = this.wheelOptions.getCurrentItems();
            this.mPickerOptions.optionsSelectListener.onOptionsSelect(optionsCurrentItems[0], optionsCurrentItems[1], optionsCurrentItems[2], this.clickView);
        }
    }

    public boolean isDialog() {
        return this.mPickerOptions.isDialog;
    }
}
