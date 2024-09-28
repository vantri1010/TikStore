package im.bclpbkiauv.ui.settings;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.settings.DataAndStoreSettingActivity;

public class CallSettingActivity extends BaseFragment {
    private static final int SAVE_BUTTON = 1;
    private ImageView mIvAlways;
    /* access modifiers changed from: private */
    public ImageView mIvCurrent;
    /* access modifiers changed from: private */
    public ImageView mIvMobile;
    /* access modifiers changed from: private */
    public ImageView mIvNever;
    /* access modifiers changed from: private */
    public DataAndStoreSettingActivity.CallSettingSelectedListener mListener = null;
    private int miSelected;

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.createMenu().addRightItemView(1, LocaleController.getString("Save", R.string.Save));
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_setting_call, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initView(context);
        initListener();
        return this.fragmentView;
    }

    public CallSettingActivity(int iSelected, DataAndStoreSettingActivity.CallSettingSelectedListener listener) {
        this.miSelected = iSelected;
        this.mListener = listener;
    }

    private void initView(Context context) {
        this.mIvNever = (ImageView) this.fragmentView.findViewById(R.id.iv_never);
        this.mIvMobile = (ImageView) this.fragmentView.findViewById(R.id.iv_mobile);
        this.mIvAlways = (ImageView) this.fragmentView.findViewById(R.id.iv_always);
        if (this.miSelected == 1) {
            this.miSelected = 0;
        }
        this.mIvNever.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.SRC_IN));
        this.mIvMobile.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.SRC_IN));
        this.mIvAlways.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.SRC_IN));
        int i = this.miSelected;
        if (i == 0) {
            ImageView imageView = this.mIvNever;
            this.mIvCurrent = imageView;
            imageView.setImageDrawable(getParentActivity().getResources().getDrawable(R.mipmap.ic_selected));
        } else if (i == 2) {
            ImageView imageView2 = this.mIvMobile;
            this.mIvCurrent = imageView2;
            imageView2.setImageDrawable(getParentActivity().getResources().getDrawable(R.mipmap.ic_selected));
        } else if (i == 3) {
            ImageView imageView3 = this.mIvAlways;
            this.mIvCurrent = imageView3;
            imageView3.setImageDrawable(getParentActivity().getResources().getDrawable(R.mipmap.ic_selected));
        }
        this.fragmentView.findViewById(R.id.rl_never).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_mobile).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_always).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    private void initListener() {
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                int iSeled;
                if (id == -1) {
                    CallSettingActivity.this.finishFragment();
                } else if (id == 1) {
                    if (CallSettingActivity.this.mListener != null) {
                        if (CallSettingActivity.this.mIvCurrent == CallSettingActivity.this.mIvNever) {
                            iSeled = 0;
                        } else if (CallSettingActivity.this.mIvCurrent == CallSettingActivity.this.mIvMobile) {
                            iSeled = 2;
                        } else {
                            iSeled = 3;
                        }
                        CallSettingActivity.this.mListener.onSeleted(iSeled);
                    }
                    CallSettingActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView.findViewById(R.id.rl_never).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CallSettingActivity.this.lambda$initListener$0$CallSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_mobile).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CallSettingActivity.this.lambda$initListener$1$CallSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_always).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CallSettingActivity.this.lambda$initListener$2$CallSettingActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initListener$0$CallSettingActivity(View view) {
        ImageView imageView = this.mIvCurrent;
        ImageView imageView2 = this.mIvNever;
        if (imageView != imageView2) {
            imageView2.setImageDrawable(getParentActivity().getResources().getDrawable(R.mipmap.ic_selected));
            this.mIvCurrent.setImageDrawable((Drawable) null);
            this.mIvCurrent = this.mIvNever;
        }
    }

    public /* synthetic */ void lambda$initListener$1$CallSettingActivity(View view) {
        ImageView imageView = this.mIvCurrent;
        ImageView imageView2 = this.mIvMobile;
        if (imageView != imageView2) {
            imageView2.setImageDrawable(getParentActivity().getResources().getDrawable(R.mipmap.ic_selected));
            this.mIvCurrent.setImageDrawable((Drawable) null);
            this.mIvCurrent = this.mIvMobile;
        }
    }

    public /* synthetic */ void lambda$initListener$2$CallSettingActivity(View view) {
        ImageView imageView = this.mIvCurrent;
        ImageView imageView2 = this.mIvAlways;
        if (imageView != imageView2) {
            imageView2.setImageDrawable(getParentActivity().getResources().getDrawable(R.mipmap.ic_selected));
            this.mIvCurrent.setImageDrawable((Drawable) null);
            this.mIvCurrent = this.mIvAlways;
        }
    }
}
