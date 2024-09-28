package im.bclpbkiauv.ui.components.toast;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class BaseToast extends Toast {
    private TextView mMessageView;

    public BaseToast(Context context) {
        super(context);
    }

    public void setView(View view) {
        super.setView(view);
        this.mMessageView = getMessageView(view);
    }

    public void setText(CharSequence s) {
        this.mMessageView.setText(s);
    }

    private static TextView getMessageView(View view) {
        TextView textView;
        if (view instanceof TextView) {
            return (TextView) view;
        }
        if (view.findViewById(16908299) instanceof TextView) {
            return (TextView) view.findViewById(16908299);
        }
        if ((view instanceof ViewGroup) && (textView = findTextView((ViewGroup) view)) != null) {
            return textView;
        }
        throw new IllegalArgumentException("The layout must contain a TextView");
    }

    private static TextView findTextView(ViewGroup group) {
        TextView textView;
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof TextView) {
                return (TextView) view;
            }
            if ((view instanceof ViewGroup) && (textView = findTextView((ViewGroup) view)) != null) {
                return textView;
            }
        }
        return null;
    }
}
