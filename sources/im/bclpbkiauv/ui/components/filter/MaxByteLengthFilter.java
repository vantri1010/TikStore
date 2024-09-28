package im.bclpbkiauv.ui.components.filter;

import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import im.bclpbkiauv.messenger.AndroidUtilities;
import java.nio.charset.Charset;

public class MaxByteLengthFilter implements InputFilter {
    private boolean mAllowEmoji;
    private int mMaxAllowByteLength;

    public MaxByteLengthFilter() {
        this(48);
    }

    public MaxByteLengthFilter(int maxAllowByteLength) {
        this(maxAllowByteLength, true);
    }

    public MaxByteLengthFilter(int maxAllowByteLength, boolean allowEmoji) {
        this.mMaxAllowByteLength = maxAllowByteLength;
        this.mAllowEmoji = allowEmoji;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        boolean more = false;
        do {
            SpannableStringBuilder builder = new SpannableStringBuilder(dest).replace(dstart, dend, source.subSequence(start, end));
            if (!AndroidUtilities.containsEmoji(source) || this.mAllowEmoji) {
                more = builder.toString().getBytes(Charset.defaultCharset()).length > this.mMaxAllowByteLength;
                if (!more) {
                    continue;
                } else if (!AndroidUtilities.containsEmoji(source) || !this.mAllowEmoji) {
                    end--;
                    source = source.subSequence(start, end);
                    continue;
                } else {
                    end = 0;
                    source = "";
                    continue;
                }
            } else {
                source = "";
                continue;
            }
        } while (more);
        return source;
    }
}
