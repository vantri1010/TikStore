package im.bclpbkiauv.ui.cell;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class SettingsItem extends FrameLayout {
    private String desc;
    private View divLine;
    private boolean divider;
    private Drawable leftDrawable;
    private ImageView leftImageView;
    private Context mContext;
    private int paddingLeft;
    private Drawable rightDrawable;
    private ImageView rightImageView;
    private String subTitle;
    private String title;
    private TextView tvItemDescText;
    private TextView tvItemSubTitle;
    private TextView tvItemTitle;

    public SettingsItem(Context context) {
        this(context, (AttributeSet) null);
    }

    public SettingsItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = this.mContext.obtainStyledAttributes(attrs, R.styleable.SettingsItem);
        this.leftDrawable = ta.getDrawable(3);
        this.rightDrawable = ta.getDrawable(4);
        this.title = ta.getString(6);
        this.subTitle = ta.getString(5);
        this.desc = ta.getString(0);
        this.divider = ta.getBoolean(2, true);
        this.paddingLeft = ta.getDimensionPixelSize(1, 0);
        ta.recycle();
    }

    private View initInflate() {
        View view = View.inflate(this.mContext, R.layout.item_settings_layout, (ViewGroup) null);
        this.leftImageView = (ImageView) view.findViewById(R.id.ivLeftIcon);
        this.rightImageView = (ImageView) view.findViewById(R.id.ivRightIcon);
        this.tvItemTitle = (TextView) view.findViewById(R.id.tvItemTitle);
        this.tvItemSubTitle = (TextView) view.findViewById(R.id.tvItemSubTitle);
        this.tvItemDescText = (TextView) view.findViewById(R.id.tvItemDescText);
        this.tvItemDescText = (TextView) view.findViewById(R.id.tvItemDescText);
        this.divLine = view.findViewById(R.id.divLine);
        return view;
    }

    private void init() {
        addView(initInflate());
        setBackground(Theme.createSimpleSelectorRoundRectDrawable((float) AndroidUtilities.dp(24.0f), Color.parseColor("#FF268CFF"), Color.parseColor("#FF1E69BD")));
        boolean z = true;
        boolean flag = this.leftDrawable != null;
        if (flag) {
            this.leftImageView.setImageDrawable(this.leftDrawable);
        }
        int i = 8;
        this.leftImageView.setVisibility(flag ? 0 : 8);
        boolean flag2 = this.title != null;
        if (flag2) {
            this.tvItemTitle.setText(this.title);
        }
        this.tvItemTitle.setVisibility(flag2 ? 0 : 8);
        boolean flag3 = this.subTitle != null;
        if (flag3) {
            this.tvItemSubTitle.setText(this.subTitle);
        }
        this.tvItemSubTitle.setVisibility(flag3 ? 0 : 8);
        String str = this.subTitle;
        if (str != null) {
            this.tvItemSubTitle.setText(str);
        }
        String str2 = this.desc;
        if (str2 != null) {
            this.tvItemDescText.setText(str2);
        }
        boolean flag4 = this.desc != null;
        if (flag4) {
            this.tvItemDescText.setText(this.desc);
        }
        this.tvItemDescText.setVisibility(flag4 ? 0 : 8);
        if (this.rightDrawable == null) {
            z = false;
        }
        boolean flag5 = z;
        if (flag5) {
            this.rightImageView.setImageDrawable(this.rightDrawable);
        }
        this.rightImageView.setVisibility(flag5 ? 0 : 8);
        View view = this.divLine;
        if (this.divider) {
            i = 0;
        }
        view.setVisibility(i);
        if (this.paddingLeft != 0) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.divLine.getLayoutParams();
            layoutParams.setMargins(this.paddingLeft, 0, 0, 0);
            this.divLine.setLayoutParams(layoutParams);
        }
    }

    private void setData(int ltResId, String title2, String subTitle2, boolean arrow, boolean divider2) {
        setData(ltResId, title2, subTitle2, (String) null, arrow ? R.mipmap.icon_arrow_right : 0, divider2);
    }

    private void setData(int ltResId, String title2, boolean arrow, boolean divier) {
        setData(ltResId, title2, this.subTitle, (String) null, arrow ? R.mipmap.icon_arrow_right : 0, this.divider);
    }

    private void setData(int ltResId, String title2, String subTitle2, String desc2, int gtResId, boolean divider2) {
        boolean flag = true;
        boolean flag2 = ltResId > 0;
        int i = 8;
        this.leftImageView.setVisibility(flag2 ? 0 : 8);
        if (flag2) {
            this.leftImageView.setImageResource(ltResId);
        }
        this.tvItemTitle.setVisibility(0);
        this.tvItemTitle.setText(title2);
        boolean flag3 = subTitle2 != null;
        this.tvItemSubTitle.setVisibility(flag3 ? 0 : 8);
        if (flag3) {
            this.tvItemSubTitle.setText(subTitle2);
        }
        boolean flag4 = desc2 != null;
        this.tvItemDescText.setVisibility(flag4 ? 0 : 8);
        if (flag4) {
            this.tvItemDescText.setText(desc2);
        }
        if (gtResId <= 0) {
            flag = false;
        }
        this.rightImageView.setVisibility(flag ? 0 : 8);
        if (flag) {
            this.rightImageView.setImageResource(ltResId);
        }
        View view = this.divLine;
        if (divider2) {
            i = 0;
        }
        view.setVisibility(i);
        if (divider2 && this.paddingLeft > 0) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.divLine.getLayoutParams();
            layoutParams.setMargins(this.paddingLeft, 0, 0, 0);
            this.divLine.setLayoutParams(layoutParams);
        }
        invalidate();
    }
}
