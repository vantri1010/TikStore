package im.bclpbkiauv.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.contrarywind.view.WheelView;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.Calendar;

public class TimeWheelPickerDialog extends BottomSheet {
    private PickerOptions mPickerOptions;

    public TimeWheelPickerDialog(Context context, PickerOptions mPickerOptions2, int backgroundType) {
        super(context, R.style.TransparentDialog);
        this.mPickerOptions = mPickerOptions2;
        init(context, false, backgroundType);
    }

    /* access modifiers changed from: protected */
    public void init(Context context, boolean needFocus, int backgroundType) {
        super.init(context, needFocus, backgroundType);
        setApplyTopPadding(false);
        TimePickerView mTimePickerView = new TimePickerView(this.mPickerOptions) {
            public void dismiss() {
                TimeWheelPickerDialog.this.dismiss();
            }
        };
        View customView = mTimePickerView.getDialogContainerLayout();
        customView.findViewById(R.id.rv_topbar).setLayoutParams(LayoutHelper.createLinear(-1, 60));
        customView.findViewById(R.id.timepicker).setLayoutParams(LayoutHelper.createLinear(-1, 230));
        setCustomView(mTimePickerView.getDialogContainerLayout());
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public static Builder getDefaultBuilder(Activity context, OnTimeSelectListener listener) {
        Builder builder = new Builder(context, listener);
        Calendar startCal = Calendar.getInstance();
        startCal.set(1970, 0, 1);
        builder.setLabel("", "", "", "", "", "").setBgColor(0).setCancelText(LocaleController.getString(R.string.Cancel)).setCancelColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3)).setSubCalSize(16).setTitleBgColor(0).setSubmitText(LocaleController.getString(R.string.OK)).setSubmitColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText)).setDividerColor(Theme.getColor(Theme.key_divider)).setTextColorCenter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText)).setTextColorOut(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3)).setContentTextSize(16).setOutSideColor(0).setOutSideCancelable(false).isDialog(false).setRangDate(startCal, Calendar.getInstance()).setType(new boolean[]{true, true, true, false, false, false});
        builder.mPickerOptions.itemsVisibleCount = 5;
        builder.mPickerOptions.lineSpacingMultiplier = 3.5f;
        return builder;
    }

    public static class Builder {
        public PickerOptions mPickerOptions;

        public Builder(Context context, OnTimeSelectListener listener) {
            PickerOptions pickerOptions = new PickerOptions(2);
            this.mPickerOptions = pickerOptions;
            pickerOptions.context = context;
            this.mPickerOptions.timeSelectListener = listener;
        }

        public Builder setGravity(int gravity) {
            this.mPickerOptions.textGravity = gravity;
            return this;
        }

        public Builder addOnCancelClickListener(View.OnClickListener cancelListener) {
            this.mPickerOptions.cancelListener = cancelListener;
            return this;
        }

        public Builder setType(boolean[] type) {
            this.mPickerOptions.type = type;
            return this;
        }

        public Builder setSubmitText(String textContentConfirm) {
            this.mPickerOptions.textContentConfirm = textContentConfirm;
            return this;
        }

        public Builder isDialog(boolean isDialog) {
            this.mPickerOptions.isDialog = isDialog;
            return this;
        }

        public Builder setCancelText(String textContentCancel) {
            this.mPickerOptions.textContentCancel = textContentCancel;
            return this;
        }

        public Builder setTitleText(String textContentTitle) {
            this.mPickerOptions.textContentTitle = textContentTitle;
            return this;
        }

        public Builder setSubmitColor(int textColorConfirm) {
            this.mPickerOptions.textColorConfirm = textColorConfirm;
            return this;
        }

        public Builder setCancelColor(int textColorCancel) {
            this.mPickerOptions.textColorCancel = textColorCancel;
            return this;
        }

        public Builder setDecorView(ViewGroup decorView) {
            this.mPickerOptions.decorView = decorView;
            return this;
        }

        public Builder setBgColor(int bgColorWheel) {
            this.mPickerOptions.bgColorWheel = bgColorWheel;
            return this;
        }

        public Builder setTitleBgColor(int bgColorTitle) {
            this.mPickerOptions.bgColorTitle = bgColorTitle;
            return this;
        }

        public Builder setTitleColor(int textColorTitle) {
            this.mPickerOptions.textColorTitle = textColorTitle;
            return this;
        }

        public Builder setSubCalSize(int textSizeSubmitCancel) {
            this.mPickerOptions.textSizeSubmitCancel = textSizeSubmitCancel;
            return this;
        }

        public Builder setTitleSize(int textSizeTitle) {
            this.mPickerOptions.textSizeTitle = textSizeTitle;
            return this;
        }

        public Builder setContentTextSize(int textSizeContent) {
            this.mPickerOptions.textSizeContent = textSizeContent;
            return this;
        }

        public Builder setItemVisibleCount(int count) {
            this.mPickerOptions.itemsVisibleCount = count;
            return this;
        }

        public Builder isAlphaGradient(boolean isAlphaGradient) {
            this.mPickerOptions.isAlphaGradient = isAlphaGradient;
            return this;
        }

        public Builder setDate(Calendar date) {
            this.mPickerOptions.date = date;
            return this;
        }

        public Builder setLayoutRes(int res, CustomListener customListener) {
            this.mPickerOptions.layoutRes = res;
            this.mPickerOptions.customListener = customListener;
            return this;
        }

        public Builder setRangDate(Calendar startDate, Calendar endDate) {
            this.mPickerOptions.startDate = startDate;
            this.mPickerOptions.endDate = endDate;
            return this;
        }

        public Builder setLineSpacingMultiplier(float lineSpacingMultiplier) {
            this.mPickerOptions.lineSpacingMultiplier = lineSpacingMultiplier;
            return this;
        }

        public Builder setDividerColor(int dividerColor) {
            this.mPickerOptions.dividerColor = dividerColor;
            return this;
        }

        public Builder setDividerType(WheelView.DividerType dividerType) {
            this.mPickerOptions.dividerType = dividerType;
            return this;
        }

        @Deprecated
        public Builder setBackgroundId(int backgroundId) {
            this.mPickerOptions.outSideColor = backgroundId;
            return this;
        }

        public Builder setOutSideColor(int outSideColor) {
            this.mPickerOptions.outSideColor = outSideColor;
            return this;
        }

        public Builder setTextColorCenter(int textColorCenter) {
            this.mPickerOptions.textColorCenter = textColorCenter;
            return this;
        }

        public Builder setTextColorOut(int textColorOut) {
            this.mPickerOptions.textColorOut = textColorOut;
            return this;
        }

        public Builder isCyclic(boolean cyclic) {
            this.mPickerOptions.cyclic = cyclic;
            return this;
        }

        public Builder setOutSideCancelable(boolean cancelable) {
            this.mPickerOptions.cancelable = cancelable;
            return this;
        }

        public Builder setLunarCalendar(boolean lunarCalendar) {
            this.mPickerOptions.isLunarCalendar = lunarCalendar;
            return this;
        }

        public Builder setLabel(String label_year, String label_month, String label_day, String label_hours, String label_mins, String label_seconds) {
            this.mPickerOptions.label_year = label_year;
            this.mPickerOptions.label_month = label_month;
            this.mPickerOptions.label_day = label_day;
            this.mPickerOptions.label_hours = label_hours;
            this.mPickerOptions.label_minutes = label_mins;
            this.mPickerOptions.label_seconds = label_seconds;
            return this;
        }

        public Builder setTextXOffset(int x_offset_year, int x_offset_month, int x_offset_day, int x_offset_hours, int x_offset_minutes, int x_offset_seconds) {
            this.mPickerOptions.x_offset_year = x_offset_year;
            this.mPickerOptions.x_offset_month = x_offset_month;
            this.mPickerOptions.x_offset_day = x_offset_day;
            this.mPickerOptions.x_offset_hours = x_offset_hours;
            this.mPickerOptions.x_offset_minutes = x_offset_minutes;
            this.mPickerOptions.x_offset_seconds = x_offset_seconds;
            return this;
        }

        public Builder isCenterLabel(boolean isCenterLabel) {
            this.mPickerOptions.isCenterLabel = isCenterLabel;
            return this;
        }

        public Builder setTimeSelectChangeListener(OnTimeSelectChangeListener listener) {
            this.mPickerOptions.timeSelectChangeListener = listener;
            return this;
        }

        public TimeWheelPickerDialog build() {
            return build(1);
        }

        public TimeWheelPickerDialog build(int backgroundType) {
            this.mPickerOptions.isDialog = false;
            return new TimeWheelPickerDialog(this.mPickerOptions.context, this.mPickerOptions, backgroundType);
        }
    }
}
