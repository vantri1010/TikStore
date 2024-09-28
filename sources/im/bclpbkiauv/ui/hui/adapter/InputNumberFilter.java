package im.bclpbkiauv.ui.hui.adapter;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import java.util.regex.Pattern;

public class InputNumberFilter implements InputFilter {
    private int max = 500;
    Pattern p = Pattern.compile("([0-9])*");

    public InputNumberFilter(int max2) {
        this.max = max2;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int i = dstart;
        String sourceText = source.toString();
        String destText = dest.toString();
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }
        CharSequence charSequence = source;
        if (!this.p.matcher(source).matches()) {
            return "";
        }
        if ("0".equals(source.toString()) && i == 0) {
            return "";
        }
        String first = destText.substring(0, i);
        String second = destText.substring(i);
        double sumText = Double.parseDouble(first + sourceText + second);
        int i2 = this.max;
        if (i2 > -1 && sumText > ((double) i2)) {
            return dest.subSequence(dstart, dend);
        }
        return dest.subSequence(dstart, dend) + sourceText;
    }
}
