package im.bclpbkiauv.ui.hui.adapter;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditInputFilter implements InputFilter {
    public static final int MAX_VALUE = 1000000;
    private static final String POINTER = ".";
    public static final int POINTER_LENGTH = 2;
    Pattern p;

    public EditInputFilter() {
        this.p = Pattern.compile("([0-9]|\\.)*");
    }

    public EditInputFilter(boolean posInteger) {
        if (posInteger) {
            this.p = Pattern.compile("([0-9])*");
        }
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        CharSequence charSequence = source;
        int i = dstart;
        String sourceText = source.toString();
        String destText = dest.toString();
        if (TextUtils.isEmpty(sourceText)) {
            return (i == 0 && destText.indexOf(POINTER) == 1) ? "0" : "";
        }
        Matcher matcher = this.p.matcher(source);
        if (destText.contains(POINTER)) {
            if (!matcher.matches() || POINTER.equals(source)) {
                return "";
            }
            int index = destText.indexOf(POINTER);
            if (destText.trim().length() - index > 2 && i > index) {
                return "";
            }
        } else if (!matcher.matches()) {
            return "";
        } else {
            if (POINTER.equals(source) && i == 0) {
                return "0.";
            }
            if ("0".equals(source) && i == 0) {
                return "0";
            }
        }
        String first = destText.substring(0, i);
        String second = destText.substring(i, destText.length());
        if (Double.parseDouble(first + sourceText + second) > 1000000.0d) {
            return dest.subSequence(dstart, dend);
        }
        return dest.subSequence(dstart, dend) + sourceText;
    }
}
