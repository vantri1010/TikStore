package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;

public class AppTextView extends AppCompatTextView {
    public AppTextView(Context context) {
        super(context);
    }

    public AppTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AppTextView, defStyleAttr, 0);
        int textColor = ta.getColor(2, -1);
        int textHintColor = ta.getColor(0, -1);
        String autoTextKey = ta.getString(3);
        String autoHintKey = ta.getString(1);
        ta.recycle();
        if (textColor != -1) {
            setTextColor(textColor);
        }
        if (textHintColor != -1) {
            setHintTextColor(textHintColor);
        }
        if (!TextUtils.isEmpty(autoTextKey)) {
            setText(LocaleController.getString(autoTextKey, getResources().getIdentifier(autoTextKey, "string", context.getPackageName())));
        }
        if (!TextUtils.isEmpty(autoHintKey)) {
            setHint(LocaleController.getString(autoHintKey, getResources().getIdentifier(autoHintKey, "string", context.getPackageName())));
        }
    }
}
