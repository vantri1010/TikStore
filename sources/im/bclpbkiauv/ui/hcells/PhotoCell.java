package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hui.friendscircle_v1.glide.GlideUtils;
import java.util.ArrayList;
import java.util.Collections;

public class PhotoCell extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private View divider;
    private ImageView ivPic1;
    private ImageView ivPic2;
    private ImageView ivPic3;
    private ImageView ivPic4;
    private OnPhotoCellClickListener listener;
    private TextView tvPhotoCellName;
    private ArrayList<String> urls;

    public interface OnPhotoCellClickListener {
        void onPhotoClick(ImageView imageView, int i, String str);
    }

    public PhotoCell(Context context2) {
        this(context2, (AttributeSet) null);
    }

    public PhotoCell(Context context2, AttributeSet attrs) {
        this(context2, attrs, 0);
    }

    public PhotoCell(Context context2, AttributeSet attrs, int defStyleAttr) {
        super(context2, attrs, defStyleAttr);
        initCell(context2);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initCell(Context context2) {
        this.context = context2;
        LayoutInflater.from(context2).inflate(R.layout.item_photo_cell, this);
        TextView textView = (TextView) findViewById(R.id.tv_photo_cell_name);
        this.tvPhotoCellName = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tvPhotoCellName.setTextSize(14.0f);
        this.tvPhotoCellName.setGravity(LocaleController.isRTL ? 5 : 3);
        this.divider = findViewById(R.id.divider);
        this.ivPic1 = (ImageView) findViewById(R.id.iv_pic1);
        this.ivPic2 = (ImageView) findViewById(R.id.iv_pic2);
        this.ivPic3 = (ImageView) findViewById(R.id.iv_pic3);
        this.ivPic4 = (ImageView) findViewById(R.id.iv_pic4);
    }

    public void setData(ArrayList<String> urls2) {
        this.ivPic1.setVisibility(8);
        this.ivPic2.setVisibility(8);
        this.ivPic3.setVisibility(8);
        this.ivPic4.setVisibility(8);
        if (urls2 != null && urls2.size() > 0) {
            Collections.reverse(urls2);
            this.urls = urls2;
            for (int i = 0; i < urls2.size(); i++) {
                int i2 = 0;
                if (i == 0) {
                    this.ivPic1.setOnClickListener(this);
                    ImageView imageView = this.ivPic1;
                    if (TextUtils.isEmpty(urls2.get(i))) {
                        i2 = 8;
                    }
                    imageView.setVisibility(i2);
                    GlideUtils.getInstance().load(urls2.get(i), this.context, this.ivPic1, (int) R.drawable.fc_default_pic);
                } else if (i == 1) {
                    this.ivPic2.setOnClickListener(this);
                    ImageView imageView2 = this.ivPic2;
                    if (TextUtils.isEmpty(urls2.get(i))) {
                        i2 = 8;
                    }
                    imageView2.setVisibility(i2);
                    GlideUtils.getInstance().load(urls2.get(i), this.context, this.ivPic2, (int) R.drawable.fc_default_pic);
                } else if (i == 2) {
                    this.ivPic3.setOnClickListener(this);
                    ImageView imageView3 = this.ivPic3;
                    if (TextUtils.isEmpty(urls2.get(i))) {
                        i2 = 8;
                    }
                    imageView3.setVisibility(i2);
                    GlideUtils.getInstance().load(urls2.get(i), this.context, this.ivPic3, (int) R.drawable.fc_default_pic);
                } else if (i == 3) {
                    this.ivPic4.setOnClickListener(this);
                    ImageView imageView4 = this.ivPic4;
                    if (TextUtils.isEmpty(urls2.get(i))) {
                        i2 = 8;
                    }
                    imageView4.setVisibility(i2);
                    GlideUtils.getInstance().load(urls2.get(i), this.context, this.ivPic4, (int) R.drawable.fc_default_pic);
                }
            }
        }
    }

    public void setText(String name, boolean needDivider) {
        TextView textView = this.tvPhotoCellName;
        if (textView != null) {
            textView.setText(TextUtils.isEmpty(name) ? "" : name);
        }
        View view = this.divider;
        if (view != null) {
            view.setVisibility(needDivider ? 0 : 8);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pic1 /*2131296827*/:
                if (this.listener != null && this.urls.size() >= 1) {
                    this.listener.onPhotoClick(this.ivPic1, 0, this.urls.get(0));
                    return;
                }
                return;
            case R.id.iv_pic2 /*2131296828*/:
                if (this.listener != null && this.urls.size() >= 2) {
                    this.listener.onPhotoClick(this.ivPic2, 1, this.urls.get(1));
                    return;
                }
                return;
            case R.id.iv_pic3 /*2131296829*/:
                if (this.listener != null && this.urls.size() >= 3) {
                    this.listener.onPhotoClick(this.ivPic3, 2, this.urls.get(2));
                    return;
                }
                return;
            case R.id.iv_pic4 /*2131296830*/:
                if (this.listener != null && this.urls.size() >= 4) {
                    this.listener.onPhotoClick(this.ivPic4, 3, this.urls.get(3));
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void setListener(OnPhotoCellClickListener listener2) {
        this.listener = listener2;
    }
}
