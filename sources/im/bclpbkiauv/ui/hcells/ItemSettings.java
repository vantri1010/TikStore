package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class ItemSettings extends FrameLayout {
    private String desc;
    private TextView descTextView;
    private boolean divider;
    private Drawable leftDrawable;
    private boolean leftImageRender;
    private ImageView leftImageView;
    private Context mContext;
    private Drawable rightDrawable;
    private boolean rightImageRender;
    private ImageView rightImageView;
    private boolean showDesc;
    private boolean showLeftIamge;
    private boolean showRightIamge;
    private boolean showSubTitle;
    private String subTitle;
    private TextView subTitleTextView;
    private String title;
    private TextView titleTextView;

    public ItemSettings(Context context) {
        this(context, (AttributeSet) null);
    }

    public ItemSettings(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemSettings(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        boolean z = false;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ItemSettings);
            this.leftDrawable = ta.getDrawable(2);
            this.rightDrawable = ta.getDrawable(4);
            this.title = ta.getString(11);
            this.subTitle = ta.getString(10);
            this.desc = ta.getString(0);
            this.showLeftIamge = ta.getBoolean(9, true);
            this.showRightIamge = ta.getBoolean(8, true);
            this.showSubTitle = ta.getBoolean(9, true);
            this.showDesc = ta.getBoolean(6, true);
            this.leftImageRender = ta.getBoolean(3, false);
            this.rightImageRender = ta.getBoolean(5, false);
            this.divider = ta.getBoolean(1, false);
        }
        initView(attrs != null ? true : z);
    }

    private void initView(boolean attrs) {
        View view = View.inflate(this.mContext, R.layout.item_setting_layout, (ViewGroup) null);
        this.leftImageView = (ImageView) view.findViewById(R.id.ivLimage);
        this.rightImageView = (ImageView) view.findViewById(R.id.ivRimage);
        this.titleTextView = (TextView) view.findViewById(R.id.tvTitleText);
        this.subTitleTextView = (TextView) view.findViewById(R.id.tvSubTitleText);
        this.descTextView = (TextView) view.findViewById(R.id.tvDescText);
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleTextView.setTextSize(14.0f);
        this.subTitleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.subTitleTextView.setTextSize(13.0f);
        this.descTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        this.descTextView.setTextSize(12.0f);
        if (attrs) {
            int i = 0;
            this.leftImageView.setVisibility(this.showLeftIamge ? 0 : 8);
            this.rightImageView.setVisibility(this.showRightIamge ? 0 : 8);
            this.subTitleTextView.setVisibility(this.showSubTitle ? 0 : 8);
            TextView textView = this.descTextView;
            if (!this.showDesc) {
                i = 8;
            }
            textView.setVisibility(i);
            String str = this.title;
            if (str != null) {
                this.titleTextView.setText(str);
            }
            String str2 = this.subTitle;
            if (str2 != null) {
                this.subTitleTextView.setText(str2);
            }
            String str3 = this.desc;
            if (str3 != null) {
                this.descTextView.setText(str3);
            }
            if (this.leftDrawable != null) {
                if (this.leftImageRender) {
                    this.leftImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
                }
                this.leftImageView.setImageDrawable(this.leftDrawable);
            }
            if (this.rightDrawable != null) {
                if (this.rightImageRender) {
                    this.rightImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
                }
                this.rightImageView.setImageDrawable(this.rightDrawable);
            }
        }
    }

    public void setTitleTextView(CharSequence title2) {
        this.titleTextView.setText(title2);
    }
}
