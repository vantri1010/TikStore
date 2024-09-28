package com.bigkoo.pickerview.view;

import android.view.View;
import androidx.exifinterface.media.ExifInterface;
import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.listener.ISelectTimeCallback;
import com.bigkoo.pickerview.utils.ChinaDate;
import com.bigkoo.pickerview.utils.LunarCalendar;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.king.zxing.util.LogUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class WheelTime {
    private static final int DEFAULT_END_DAY = 31;
    private static final int DEFAULT_END_MONTH = 12;
    private static final int DEFAULT_END_YEAR = 2100;
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFAULT_START_MONTH = 1;
    private static final int DEFAULT_START_YEAR = 1900;
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /* access modifiers changed from: private */
    public int currentYear;
    /* access modifiers changed from: private */
    public int endDay = 31;
    /* access modifiers changed from: private */
    public int endMonth = 12;
    /* access modifiers changed from: private */
    public int endYear = DEFAULT_END_YEAR;
    private int gravity;
    private boolean isLunarCalendar = false;
    /* access modifiers changed from: private */
    public ISelectTimeCallback mSelectChangeCallback;
    /* access modifiers changed from: private */
    public int startDay = 1;
    /* access modifiers changed from: private */
    public int startMonth = 1;
    /* access modifiers changed from: private */
    public int startYear = 1900;
    private int textSize;
    private boolean[] type;
    private View view;
    /* access modifiers changed from: private */
    public WheelView wv_day;
    private WheelView wv_hours;
    private WheelView wv_minutes;
    /* access modifiers changed from: private */
    public WheelView wv_month;
    private WheelView wv_seconds;
    /* access modifiers changed from: private */
    public WheelView wv_year;

    public WheelTime(View view2, boolean[] type2, int gravity2, int textSize2) {
        this.view = view2;
        this.type = type2;
        this.gravity = gravity2;
        this.textSize = textSize2;
    }

    public void setLunarMode(boolean isLunarCalendar2) {
        this.isLunarCalendar = isLunarCalendar2;
    }

    public boolean isLunarMode() {
        return this.isLunarCalendar;
    }

    public void setPicker(int year, int month, int day) {
        setPicker(year, month, day, 0, 0, 0);
    }

    public void setPicker(int year, int month, int day, int h, int m, int s) {
        if (this.isLunarCalendar) {
            int i = year;
            int i2 = day;
            int[] lunar = LunarCalendar.solarToLunar(year, month + 1, day);
            setLunar(lunar[0], lunar[1] - 1, lunar[2], lunar[3] == 1, h, m, s);
            return;
        }
        int i3 = year;
        int i4 = day;
        setSolar(year, month, day, h, m, s);
    }

    private void setLunar(int year, int month, int day, boolean isLeap, int h, int m, int s) {
        WheelView wheelView = (WheelView) this.view.findViewById(R.id.year);
        this.wv_year = wheelView;
        wheelView.setAdapter(new ArrayWheelAdapter(ChinaDate.getYears(this.startYear, this.endYear)));
        this.wv_year.setLabel("");
        this.wv_year.setCurrentItem(year - this.startYear);
        this.wv_year.setGravity(this.gravity);
        WheelView wheelView2 = (WheelView) this.view.findViewById(R.id.month);
        this.wv_month = wheelView2;
        wheelView2.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(year)));
        this.wv_month.setLabel("");
        int leapMonth = ChinaDate.leapMonth(year);
        if (leapMonth == 0 || (month <= leapMonth - 1 && !isLeap)) {
            this.wv_month.setCurrentItem(month);
        } else {
            this.wv_month.setCurrentItem(month + 1);
        }
        this.wv_month.setGravity(this.gravity);
        this.wv_day = (WheelView) this.view.findViewById(R.id.day);
        if (ChinaDate.leapMonth(year) == 0) {
            this.wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year, month))));
        } else {
            this.wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year))));
        }
        this.wv_day.setLabel("");
        this.wv_day.setCurrentItem(day - 1);
        this.wv_day.setGravity(this.gravity);
        WheelView wheelView3 = (WheelView) this.view.findViewById(R.id.hour);
        this.wv_hours = wheelView3;
        int i = 0;
        wheelView3.setAdapter(new NumericWheelAdapter(0, 23));
        this.wv_hours.setCurrentItem(h);
        this.wv_hours.setGravity(this.gravity);
        WheelView wheelView4 = (WheelView) this.view.findViewById(R.id.min);
        this.wv_minutes = wheelView4;
        wheelView4.setAdapter(new NumericWheelAdapter(0, 59));
        this.wv_minutes.setCurrentItem(m);
        this.wv_minutes.setGravity(this.gravity);
        WheelView wheelView5 = (WheelView) this.view.findViewById(R.id.second);
        this.wv_seconds = wheelView5;
        wheelView5.setAdapter(new NumericWheelAdapter(0, 59));
        this.wv_seconds.setCurrentItem(m);
        this.wv_seconds.setGravity(this.gravity);
        this.wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(int index) {
                int maxItem;
                int year_num = WheelTime.this.startYear + index;
                WheelTime.this.wv_month.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(year_num)));
                if (ChinaDate.leapMonth(year_num) == 0 || WheelTime.this.wv_month.getCurrentItem() <= ChinaDate.leapMonth(year_num) - 1) {
                    WheelTime.this.wv_month.setCurrentItem(WheelTime.this.wv_month.getCurrentItem());
                } else {
                    WheelTime.this.wv_month.setCurrentItem(WheelTime.this.wv_month.getCurrentItem() + 1);
                }
                int currentIndex = WheelTime.this.wv_day.getCurrentItem();
                if (ChinaDate.leapMonth(year_num) == 0 || WheelTime.this.wv_month.getCurrentItem() <= ChinaDate.leapMonth(year_num) - 1) {
                    WheelTime.this.wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, WheelTime.this.wv_month.getCurrentItem() + 1))));
                    maxItem = ChinaDate.monthDays(year_num, WheelTime.this.wv_month.getCurrentItem() + 1);
                } else if (WheelTime.this.wv_month.getCurrentItem() == ChinaDate.leapMonth(year_num) + 1) {
                    WheelTime.this.wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year_num))));
                    maxItem = ChinaDate.leapDays(year_num);
                } else {
                    WheelTime.this.wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, WheelTime.this.wv_month.getCurrentItem()))));
                    maxItem = ChinaDate.monthDays(year_num, WheelTime.this.wv_month.getCurrentItem());
                }
                if (currentIndex > maxItem - 1) {
                    WheelTime.this.wv_day.setCurrentItem(maxItem - 1);
                }
                if (WheelTime.this.mSelectChangeCallback != null) {
                    WheelTime.this.mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });
        this.wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(int index) {
                int maxItem;
                int month_num = index;
                int year_num = WheelTime.this.wv_year.getCurrentItem() + WheelTime.this.startYear;
                int currentIndex = WheelTime.this.wv_day.getCurrentItem();
                if (ChinaDate.leapMonth(year_num) == 0 || month_num <= ChinaDate.leapMonth(year_num) - 1) {
                    WheelTime.this.wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, month_num + 1))));
                    maxItem = ChinaDate.monthDays(year_num, month_num + 1);
                } else if (WheelTime.this.wv_month.getCurrentItem() == ChinaDate.leapMonth(year_num) + 1) {
                    WheelTime.this.wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year_num))));
                    maxItem = ChinaDate.leapDays(year_num);
                } else {
                    WheelTime.this.wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, month_num))));
                    maxItem = ChinaDate.monthDays(year_num, month_num);
                }
                if (currentIndex > maxItem - 1) {
                    WheelTime.this.wv_day.setCurrentItem(maxItem - 1);
                }
                if (WheelTime.this.mSelectChangeCallback != null) {
                    WheelTime.this.mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });
        setChangedListener(this.wv_day);
        setChangedListener(this.wv_hours);
        setChangedListener(this.wv_minutes);
        setChangedListener(this.wv_seconds);
        boolean[] zArr = this.type;
        if (zArr.length == 6) {
            this.wv_year.setVisibility(zArr[0] ? 0 : 8);
            this.wv_month.setVisibility(this.type[1] ? 0 : 8);
            this.wv_day.setVisibility(this.type[2] ? 0 : 8);
            this.wv_hours.setVisibility(this.type[3] ? 0 : 8);
            this.wv_minutes.setVisibility(this.type[4] ? 0 : 8);
            WheelView wheelView6 = this.wv_seconds;
            if (!this.type[5]) {
                i = 8;
            }
            wheelView6.setVisibility(i);
            setContentTextSize();
            return;
        }
        throw new RuntimeException("type[] length is not 6");
    }

    private void setSolar(int year, int month, int day, int h, int m, int s) {
        int i = year;
        int i2 = month;
        final List<String> list_big = Arrays.asList(new String[]{"1", ExifInterface.GPS_MEASUREMENT_3D, "5", "7", "8", "10", "12"});
        final List<String> list_little = Arrays.asList(new String[]{"4", "6", "9", "11"});
        this.currentYear = i;
        WheelView wheelView = (WheelView) this.view.findViewById(R.id.year);
        this.wv_year = wheelView;
        wheelView.setAdapter(new NumericWheelAdapter(this.startYear, this.endYear));
        this.wv_year.setCurrentItem(i - this.startYear);
        this.wv_year.setGravity(this.gravity);
        WheelView wheelView2 = (WheelView) this.view.findViewById(R.id.month);
        this.wv_month = wheelView2;
        int i3 = this.startYear;
        int i4 = this.endYear;
        if (i3 == i4) {
            wheelView2.setAdapter(new NumericWheelAdapter(this.startMonth, this.endMonth));
            this.wv_month.setCurrentItem((i2 + 1) - this.startMonth);
        } else if (i == i3) {
            wheelView2.setAdapter(new NumericWheelAdapter(this.startMonth, 12));
            this.wv_month.setCurrentItem((i2 + 1) - this.startMonth);
        } else if (i == i4) {
            wheelView2.setAdapter(new NumericWheelAdapter(1, this.endMonth));
            this.wv_month.setCurrentItem(i2);
        } else {
            wheelView2.setAdapter(new NumericWheelAdapter(1, 12));
            this.wv_month.setCurrentItem(i2);
        }
        this.wv_month.setGravity(this.gravity);
        this.wv_day = (WheelView) this.view.findViewById(R.id.day);
        int i5 = 0;
        boolean leapYear = (i % 4 == 0 && i % 100 != 0) || i % 400 == 0;
        int i6 = 29;
        if (this.startYear == this.endYear && this.startMonth == this.endMonth) {
            if (list_big.contains(String.valueOf(i2 + 1))) {
                if (this.endDay > 31) {
                    this.endDay = 31;
                }
                this.wv_day.setAdapter(new NumericWheelAdapter(this.startDay, this.endDay));
            } else if (list_little.contains(String.valueOf(i2 + 1))) {
                if (this.endDay > 30) {
                    this.endDay = 30;
                }
                this.wv_day.setAdapter(new NumericWheelAdapter(this.startDay, this.endDay));
            } else if (leapYear) {
                if (this.endDay > 29) {
                    this.endDay = 29;
                }
                this.wv_day.setAdapter(new NumericWheelAdapter(this.startDay, this.endDay));
            } else {
                if (this.endDay > 28) {
                    this.endDay = 28;
                }
                this.wv_day.setAdapter(new NumericWheelAdapter(this.startDay, this.endDay));
            }
            this.wv_day.setCurrentItem(day - this.startDay);
        } else if (i == this.startYear && i2 + 1 == this.startMonth) {
            if (list_big.contains(String.valueOf(i2 + 1))) {
                this.wv_day.setAdapter(new NumericWheelAdapter(this.startDay, 31));
            } else if (list_little.contains(String.valueOf(i2 + 1))) {
                this.wv_day.setAdapter(new NumericWheelAdapter(this.startDay, 30));
            } else {
                WheelView wheelView3 = this.wv_day;
                int i7 = this.startDay;
                if (!leapYear) {
                    i6 = 28;
                }
                wheelView3.setAdapter(new NumericWheelAdapter(i7, i6));
            }
            this.wv_day.setCurrentItem(day - this.startDay);
        } else if (i == this.endYear && i2 + 1 == this.endMonth) {
            if (list_big.contains(String.valueOf(i2 + 1))) {
                if (this.endDay > 31) {
                    this.endDay = 31;
                }
                this.wv_day.setAdapter(new NumericWheelAdapter(1, this.endDay));
            } else if (list_little.contains(String.valueOf(i2 + 1))) {
                if (this.endDay > 30) {
                    this.endDay = 30;
                }
                this.wv_day.setAdapter(new NumericWheelAdapter(1, this.endDay));
            } else if (leapYear) {
                if (this.endDay > 29) {
                    this.endDay = 29;
                }
                this.wv_day.setAdapter(new NumericWheelAdapter(1, this.endDay));
            } else {
                if (this.endDay > 28) {
                    this.endDay = 28;
                }
                this.wv_day.setAdapter(new NumericWheelAdapter(1, this.endDay));
            }
            this.wv_day.setCurrentItem(day - 1);
        } else {
            if (list_big.contains(String.valueOf(i2 + 1))) {
                this.wv_day.setAdapter(new NumericWheelAdapter(1, 31));
            } else if (list_little.contains(String.valueOf(i2 + 1))) {
                this.wv_day.setAdapter(new NumericWheelAdapter(1, 30));
            } else {
                WheelView wheelView4 = this.wv_day;
                int i8 = this.startDay;
                if (!leapYear) {
                    i6 = 28;
                }
                wheelView4.setAdapter(new NumericWheelAdapter(i8, i6));
            }
            this.wv_day.setCurrentItem(day - 1);
        }
        this.wv_day.setGravity(this.gravity);
        WheelView wheelView5 = (WheelView) this.view.findViewById(R.id.hour);
        this.wv_hours = wheelView5;
        wheelView5.setAdapter(new NumericWheelAdapter(0, 23));
        this.wv_hours.setCurrentItem(h);
        this.wv_hours.setGravity(this.gravity);
        WheelView wheelView6 = (WheelView) this.view.findViewById(R.id.min);
        this.wv_minutes = wheelView6;
        wheelView6.setAdapter(new NumericWheelAdapter(0, 59));
        this.wv_minutes.setCurrentItem(m);
        this.wv_minutes.setGravity(this.gravity);
        WheelView wheelView7 = (WheelView) this.view.findViewById(R.id.second);
        this.wv_seconds = wheelView7;
        wheelView7.setAdapter(new NumericWheelAdapter(0, 59));
        this.wv_seconds.setCurrentItem(s);
        this.wv_seconds.setGravity(this.gravity);
        this.wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(int index) {
                int year_num = WheelTime.this.startYear + index;
                int unused = WheelTime.this.currentYear = year_num;
                int currentMonthItem = WheelTime.this.wv_month.getCurrentItem();
                if (WheelTime.this.startYear == WheelTime.this.endYear) {
                    WheelTime.this.wv_month.setAdapter(new NumericWheelAdapter(WheelTime.this.startMonth, WheelTime.this.endMonth));
                    if (currentMonthItem > WheelTime.this.wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = WheelTime.this.wv_month.getAdapter().getItemsCount() - 1;
                        WheelTime.this.wv_month.setCurrentItem(currentMonthItem);
                    }
                    int monthNum = currentMonthItem + WheelTime.this.startMonth;
                    if (WheelTime.this.startMonth == WheelTime.this.endMonth) {
                        WheelTime wheelTime = WheelTime.this;
                        wheelTime.setReDay(year_num, monthNum, wheelTime.startDay, WheelTime.this.endDay, list_big, list_little);
                    } else if (monthNum == WheelTime.this.startMonth) {
                        WheelTime wheelTime2 = WheelTime.this;
                        wheelTime2.setReDay(year_num, monthNum, wheelTime2.startDay, 31, list_big, list_little);
                    } else if (monthNum == WheelTime.this.endMonth) {
                        WheelTime wheelTime3 = WheelTime.this;
                        wheelTime3.setReDay(year_num, monthNum, 1, wheelTime3.endDay, list_big, list_little);
                    } else {
                        WheelTime.this.setReDay(year_num, monthNum, 1, 31, list_big, list_little);
                    }
                } else if (year_num == WheelTime.this.startYear) {
                    WheelTime.this.wv_month.setAdapter(new NumericWheelAdapter(WheelTime.this.startMonth, 12));
                    if (currentMonthItem > WheelTime.this.wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = WheelTime.this.wv_month.getAdapter().getItemsCount() - 1;
                        WheelTime.this.wv_month.setCurrentItem(currentMonthItem);
                    }
                    int month = currentMonthItem + WheelTime.this.startMonth;
                    if (month == WheelTime.this.startMonth) {
                        WheelTime wheelTime4 = WheelTime.this;
                        wheelTime4.setReDay(year_num, month, wheelTime4.startDay, 31, list_big, list_little);
                    } else {
                        WheelTime.this.setReDay(year_num, month, 1, 31, list_big, list_little);
                    }
                } else if (year_num == WheelTime.this.endYear) {
                    WheelTime.this.wv_month.setAdapter(new NumericWheelAdapter(1, WheelTime.this.endMonth));
                    if (currentMonthItem > WheelTime.this.wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = WheelTime.this.wv_month.getAdapter().getItemsCount() - 1;
                        WheelTime.this.wv_month.setCurrentItem(currentMonthItem);
                    }
                    int monthNum2 = currentMonthItem + 1;
                    if (monthNum2 == WheelTime.this.endMonth) {
                        WheelTime wheelTime5 = WheelTime.this;
                        wheelTime5.setReDay(year_num, monthNum2, 1, wheelTime5.endDay, list_big, list_little);
                    } else {
                        WheelTime.this.setReDay(year_num, monthNum2, 1, 31, list_big, list_little);
                    }
                } else {
                    WheelTime.this.wv_month.setAdapter(new NumericWheelAdapter(1, 12));
                    WheelTime wheelTime6 = WheelTime.this;
                    wheelTime6.setReDay(year_num, 1 + wheelTime6.wv_month.getCurrentItem(), 1, 31, list_big, list_little);
                }
                if (WheelTime.this.mSelectChangeCallback != null) {
                    WheelTime.this.mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });
        this.wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(int index) {
                int month_num = index + 1;
                if (WheelTime.this.startYear == WheelTime.this.endYear) {
                    int month_num2 = (WheelTime.this.startMonth + month_num) - 1;
                    if (WheelTime.this.startMonth == WheelTime.this.endMonth) {
                        WheelTime wheelTime = WheelTime.this;
                        wheelTime.setReDay(wheelTime.currentYear, month_num2, WheelTime.this.startDay, WheelTime.this.endDay, list_big, list_little);
                    } else if (WheelTime.this.startMonth == month_num2) {
                        WheelTime wheelTime2 = WheelTime.this;
                        wheelTime2.setReDay(wheelTime2.currentYear, month_num2, WheelTime.this.startDay, 31, list_big, list_little);
                    } else if (WheelTime.this.endMonth == month_num2) {
                        WheelTime wheelTime3 = WheelTime.this;
                        wheelTime3.setReDay(wheelTime3.currentYear, month_num2, 1, WheelTime.this.endDay, list_big, list_little);
                    } else {
                        WheelTime wheelTime4 = WheelTime.this;
                        wheelTime4.setReDay(wheelTime4.currentYear, month_num2, 1, 31, list_big, list_little);
                    }
                } else if (WheelTime.this.currentYear == WheelTime.this.startYear) {
                    int month_num3 = (WheelTime.this.startMonth + month_num) - 1;
                    if (month_num3 == WheelTime.this.startMonth) {
                        WheelTime wheelTime5 = WheelTime.this;
                        wheelTime5.setReDay(wheelTime5.currentYear, month_num3, WheelTime.this.startDay, 31, list_big, list_little);
                    } else {
                        WheelTime wheelTime6 = WheelTime.this;
                        wheelTime6.setReDay(wheelTime6.currentYear, month_num3, 1, 31, list_big, list_little);
                    }
                } else if (WheelTime.this.currentYear != WheelTime.this.endYear) {
                    WheelTime wheelTime7 = WheelTime.this;
                    wheelTime7.setReDay(wheelTime7.currentYear, month_num, 1, 31, list_big, list_little);
                } else if (month_num == WheelTime.this.endMonth) {
                    WheelTime wheelTime8 = WheelTime.this;
                    wheelTime8.setReDay(wheelTime8.currentYear, WheelTime.this.wv_month.getCurrentItem() + 1, 1, WheelTime.this.endDay, list_big, list_little);
                } else {
                    WheelTime wheelTime9 = WheelTime.this;
                    wheelTime9.setReDay(wheelTime9.currentYear, WheelTime.this.wv_month.getCurrentItem() + 1, 1, 31, list_big, list_little);
                }
                if (WheelTime.this.mSelectChangeCallback != null) {
                    WheelTime.this.mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });
        setChangedListener(this.wv_day);
        setChangedListener(this.wv_hours);
        setChangedListener(this.wv_minutes);
        setChangedListener(this.wv_seconds);
        boolean[] zArr = this.type;
        if (zArr.length == 6) {
            this.wv_year.setVisibility(zArr[0] ? 0 : 8);
            this.wv_month.setVisibility(this.type[1] ? 0 : 8);
            this.wv_day.setVisibility(this.type[2] ? 0 : 8);
            this.wv_hours.setVisibility(this.type[3] ? 0 : 8);
            this.wv_minutes.setVisibility(this.type[4] ? 0 : 8);
            WheelView wheelView8 = this.wv_seconds;
            if (!this.type[5]) {
                i5 = 8;
            }
            wheelView8.setVisibility(i5);
            setContentTextSize();
            return;
        }
        throw new IllegalArgumentException("type[] length is not 6");
    }

    private void setChangedListener(WheelView wheelView) {
        if (this.mSelectChangeCallback != null) {
            wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(int index) {
                    WheelTime.this.mSelectChangeCallback.onTimeSelectChanged();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void setReDay(int year_num, int monthNum, int startD, int endD, List<String> list_big, List<String> list_little) {
        int currentItem = this.wv_day.getCurrentItem();
        if (list_big.contains(String.valueOf(monthNum))) {
            if (endD > 31) {
                endD = 31;
            }
            this.wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
        } else if (list_little.contains(String.valueOf(monthNum))) {
            if (endD > 30) {
                endD = 30;
            }
            this.wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
        } else if ((year_num % 4 != 0 || year_num % 100 == 0) && year_num % 400 != 0) {
            if (endD > 28) {
                endD = 28;
            }
            this.wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
        } else {
            if (endD > 29) {
                endD = 29;
            }
            this.wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
        }
        if (currentItem > this.wv_day.getAdapter().getItemsCount() - 1) {
            this.wv_day.setCurrentItem(this.wv_day.getAdapter().getItemsCount() - 1);
        }
    }

    private void setContentTextSize() {
        this.wv_day.setTextSize((float) this.textSize);
        this.wv_month.setTextSize((float) this.textSize);
        this.wv_year.setTextSize((float) this.textSize);
        this.wv_hours.setTextSize((float) this.textSize);
        this.wv_minutes.setTextSize((float) this.textSize);
        this.wv_seconds.setTextSize((float) this.textSize);
    }

    public void setLabels(String label_year, String label_month, String label_day, String label_hours, String label_mins, String label_seconds) {
        if (!this.isLunarCalendar) {
            if (label_year != null) {
                this.wv_year.setLabel(label_year);
            } else {
                this.wv_year.setLabel(this.view.getContext().getString(R.string.pickerview_year));
            }
            if (label_month != null) {
                this.wv_month.setLabel(label_month);
            } else {
                this.wv_month.setLabel(this.view.getContext().getString(R.string.pickerview_month));
            }
            if (label_day != null) {
                this.wv_day.setLabel(label_day);
            } else {
                this.wv_day.setLabel(this.view.getContext().getString(R.string.pickerview_day));
            }
            if (label_hours != null) {
                this.wv_hours.setLabel(label_hours);
            } else {
                this.wv_hours.setLabel(this.view.getContext().getString(R.string.pickerview_hours));
            }
            if (label_mins != null) {
                this.wv_minutes.setLabel(label_mins);
            } else {
                this.wv_minutes.setLabel(this.view.getContext().getString(R.string.pickerview_minutes));
            }
            if (label_seconds != null) {
                this.wv_seconds.setLabel(label_seconds);
            } else {
                this.wv_seconds.setLabel(this.view.getContext().getString(R.string.pickerview_seconds));
            }
        }
    }

    public void setTextXOffset(int x_offset_year, int x_offset_month, int x_offset_day, int x_offset_hours, int x_offset_minutes, int x_offset_seconds) {
        this.wv_year.setTextXOffset(x_offset_year);
        this.wv_month.setTextXOffset(x_offset_month);
        this.wv_day.setTextXOffset(x_offset_day);
        this.wv_hours.setTextXOffset(x_offset_hours);
        this.wv_minutes.setTextXOffset(x_offset_minutes);
        this.wv_seconds.setTextXOffset(x_offset_seconds);
    }

    public void setCyclic(boolean cyclic) {
        this.wv_year.setCyclic(cyclic);
        this.wv_month.setCyclic(cyclic);
        this.wv_day.setCyclic(cyclic);
        this.wv_hours.setCyclic(cyclic);
        this.wv_minutes.setCyclic(cyclic);
        this.wv_seconds.setCyclic(cyclic);
    }

    public String getTime() {
        if (this.isLunarCalendar) {
            return getLunarTime();
        }
        StringBuilder sb = new StringBuilder();
        if (this.currentYear == this.startYear) {
            int currentItem = this.wv_month.getCurrentItem();
            int i = this.startMonth;
            if (currentItem + i == i) {
                sb.append(this.wv_year.getCurrentItem() + this.startYear);
                sb.append("-");
                sb.append(this.wv_month.getCurrentItem() + this.startMonth);
                sb.append("-");
                sb.append(this.wv_day.getCurrentItem() + this.startDay);
                sb.append(" ");
                sb.append(this.wv_hours.getCurrentItem());
                sb.append(LogUtils.COLON);
                sb.append(this.wv_minutes.getCurrentItem());
                sb.append(LogUtils.COLON);
                sb.append(this.wv_seconds.getCurrentItem());
            } else {
                sb.append(this.wv_year.getCurrentItem() + this.startYear);
                sb.append("-");
                sb.append(this.wv_month.getCurrentItem() + this.startMonth);
                sb.append("-");
                sb.append(this.wv_day.getCurrentItem() + 1);
                sb.append(" ");
                sb.append(this.wv_hours.getCurrentItem());
                sb.append(LogUtils.COLON);
                sb.append(this.wv_minutes.getCurrentItem());
                sb.append(LogUtils.COLON);
                sb.append(this.wv_seconds.getCurrentItem());
            }
        } else {
            sb.append(this.wv_year.getCurrentItem() + this.startYear);
            sb.append("-");
            sb.append(this.wv_month.getCurrentItem() + 1);
            sb.append("-");
            sb.append(this.wv_day.getCurrentItem() + 1);
            sb.append(" ");
            sb.append(this.wv_hours.getCurrentItem());
            sb.append(LogUtils.COLON);
            sb.append(this.wv_minutes.getCurrentItem());
            sb.append(LogUtils.COLON);
            sb.append(this.wv_seconds.getCurrentItem());
        }
        return sb.toString();
    }

    private String getLunarTime() {
        int month;
        StringBuilder sb = new StringBuilder();
        int year = this.wv_year.getCurrentItem() + this.startYear;
        boolean isLeapMonth = false;
        if (ChinaDate.leapMonth(year) == 0) {
            month = this.wv_month.getCurrentItem() + 1;
        } else if ((this.wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) <= 0) {
            month = this.wv_month.getCurrentItem() + 1;
        } else if ((this.wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) == 1) {
            month = this.wv_month.getCurrentItem();
            isLeapMonth = true;
        } else {
            month = this.wv_month.getCurrentItem();
        }
        int[] solar = LunarCalendar.lunarToSolar(year, month, this.wv_day.getCurrentItem() + 1, isLeapMonth);
        sb.append(solar[0]);
        sb.append("-");
        sb.append(solar[1]);
        sb.append("-");
        sb.append(solar[2]);
        sb.append(" ");
        sb.append(this.wv_hours.getCurrentItem());
        sb.append(LogUtils.COLON);
        sb.append(this.wv_minutes.getCurrentItem());
        sb.append(LogUtils.COLON);
        sb.append(this.wv_seconds.getCurrentItem());
        return sb.toString();
    }

    public View getView() {
        return this.view;
    }

    public int getStartYear() {
        return this.startYear;
    }

    public void setStartYear(int startYear2) {
        this.startYear = startYear2;
    }

    public int getEndYear() {
        return this.endYear;
    }

    public void setEndYear(int endYear2) {
        this.endYear = endYear2;
    }

    public void setRangDate(Calendar startDate, Calendar endDate) {
        if (startDate == null && endDate != null) {
            int year = endDate.get(1);
            int month = endDate.get(2) + 1;
            int day = endDate.get(5);
            int i = this.startYear;
            if (year > i) {
                this.endYear = year;
                this.endMonth = month;
                this.endDay = day;
            } else if (year == i) {
                int i2 = this.startMonth;
                if (month > i2) {
                    this.endYear = year;
                    this.endMonth = month;
                    this.endDay = day;
                } else if (month == i2 && day > this.startDay) {
                    this.endYear = year;
                    this.endMonth = month;
                    this.endDay = day;
                }
            }
        } else if (startDate != null && endDate == null) {
            int year2 = startDate.get(1);
            int month2 = startDate.get(2) + 1;
            int day2 = startDate.get(5);
            int i3 = this.endYear;
            if (year2 < i3) {
                this.startMonth = month2;
                this.startDay = day2;
                this.startYear = year2;
            } else if (year2 == i3) {
                int i4 = this.endMonth;
                if (month2 < i4) {
                    this.startMonth = month2;
                    this.startDay = day2;
                    this.startYear = year2;
                } else if (month2 == i4 && day2 < this.endDay) {
                    this.startMonth = month2;
                    this.startDay = day2;
                    this.startYear = year2;
                }
            }
        } else if (startDate != null && endDate != null) {
            this.startYear = startDate.get(1);
            this.endYear = endDate.get(1);
            this.startMonth = startDate.get(2) + 1;
            this.endMonth = endDate.get(2) + 1;
            this.startDay = startDate.get(5);
            this.endDay = endDate.get(5);
        }
    }

    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.wv_day.setLineSpacingMultiplier(lineSpacingMultiplier);
        this.wv_month.setLineSpacingMultiplier(lineSpacingMultiplier);
        this.wv_year.setLineSpacingMultiplier(lineSpacingMultiplier);
        this.wv_hours.setLineSpacingMultiplier(lineSpacingMultiplier);
        this.wv_minutes.setLineSpacingMultiplier(lineSpacingMultiplier);
        this.wv_seconds.setLineSpacingMultiplier(lineSpacingMultiplier);
    }

    public void setDividerColor(int dividerColor) {
        this.wv_day.setDividerColor(dividerColor);
        this.wv_month.setDividerColor(dividerColor);
        this.wv_year.setDividerColor(dividerColor);
        this.wv_hours.setDividerColor(dividerColor);
        this.wv_minutes.setDividerColor(dividerColor);
        this.wv_seconds.setDividerColor(dividerColor);
    }

    public void setDividerType(WheelView.DividerType dividerType) {
        this.wv_day.setDividerType(dividerType);
        this.wv_month.setDividerType(dividerType);
        this.wv_year.setDividerType(dividerType);
        this.wv_hours.setDividerType(dividerType);
        this.wv_minutes.setDividerType(dividerType);
        this.wv_seconds.setDividerType(dividerType);
    }

    public void setTextColorCenter(int textColorCenter) {
        this.wv_day.setTextColorCenter(textColorCenter);
        this.wv_month.setTextColorCenter(textColorCenter);
        this.wv_year.setTextColorCenter(textColorCenter);
        this.wv_hours.setTextColorCenter(textColorCenter);
        this.wv_minutes.setTextColorCenter(textColorCenter);
        this.wv_seconds.setTextColorCenter(textColorCenter);
    }

    public void setTextColorOut(int textColorOut) {
        this.wv_day.setTextColorOut(textColorOut);
        this.wv_month.setTextColorOut(textColorOut);
        this.wv_year.setTextColorOut(textColorOut);
        this.wv_hours.setTextColorOut(textColorOut);
        this.wv_minutes.setTextColorOut(textColorOut);
        this.wv_seconds.setTextColorOut(textColorOut);
    }

    public void isCenterLabel(boolean isCenterLabel) {
        this.wv_day.isCenterLabel(isCenterLabel);
        this.wv_month.isCenterLabel(isCenterLabel);
        this.wv_year.isCenterLabel(isCenterLabel);
        this.wv_hours.isCenterLabel(isCenterLabel);
        this.wv_minutes.isCenterLabel(isCenterLabel);
        this.wv_seconds.isCenterLabel(isCenterLabel);
    }

    public void setSelectChangeCallback(ISelectTimeCallback mSelectChangeCallback2) {
        this.mSelectChangeCallback = mSelectChangeCallback2;
    }

    public void setItemsVisible(int itemsVisibleCount) {
        this.wv_day.setItemsVisibleCount(itemsVisibleCount);
        this.wv_month.setItemsVisibleCount(itemsVisibleCount);
        this.wv_year.setItemsVisibleCount(itemsVisibleCount);
        this.wv_hours.setItemsVisibleCount(itemsVisibleCount);
        this.wv_minutes.setItemsVisibleCount(itemsVisibleCount);
        this.wv_seconds.setItemsVisibleCount(itemsVisibleCount);
    }

    public void setAlphaGradient(boolean isAlphaGradient) {
        this.wv_day.setAlphaGradient(isAlphaGradient);
        this.wv_month.setAlphaGradient(isAlphaGradient);
        this.wv_year.setAlphaGradient(isAlphaGradient);
        this.wv_hours.setAlphaGradient(isAlphaGradient);
        this.wv_minutes.setAlphaGradient(isAlphaGradient);
        this.wv_seconds.setAlphaGradient(isAlphaGradient);
    }
}
