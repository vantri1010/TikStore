package im.bclpbkiauv.ui.hui.packet.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.R;

public class LoadingDialog extends Dialog {
    /* access modifiers changed from: private */
    public LoadingDialogDelegate delegate;
    private FrameLayout ffLoadingContainer;
    private Context mContext;
    private TextView tvLoadingView;

    public interface LoadingDialogDelegate {
        void onClick();
    }

    public void setDelegate(LoadingDialogDelegate delegate2) {
        this.delegate = delegate2;
    }

    public LoadingDialog(Context context) {
        super(context);
        this.mContext = context;
        initLyouat(context);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    private void initLyouat(Context context) {
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_loading_layout, (ViewGroup) null));
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable());
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = 17;
            window.setAttributes(params);
            this.tvLoadingView = (TextView) window.findViewById(R.id.tv_loading_text);
            FrameLayout frameLayout = (FrameLayout) window.findViewById(R.id.ff_loading_dialog_container);
            this.ffLoadingContainer = frameLayout;
            frameLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (LoadingDialog.this.delegate != null) {
                        LoadingDialog.this.delegate.onClick();
                    }
                }
            });
        }
    }

    public void setLoadingText(CharSequence text) {
        TextView textView = this.tvLoadingView;
        if (textView != null) {
            textView.setText(text);
        }
    }
}
