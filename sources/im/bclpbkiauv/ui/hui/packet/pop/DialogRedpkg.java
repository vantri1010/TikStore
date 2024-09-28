package im.bclpbkiauv.ui.hui.packet.pop;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DialogRedpkg extends Dialog {
    private static int default_height = 120;
    private static int default_width = 160;

    public DialogRedpkg(Context context, View layout, int style) {
        this(context, default_width, default_height, layout, style);
    }

    public DialogRedpkg(Context context, int width, int height, View layout, int style) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = 17;
        window.setAttributes(params);
    }
}
