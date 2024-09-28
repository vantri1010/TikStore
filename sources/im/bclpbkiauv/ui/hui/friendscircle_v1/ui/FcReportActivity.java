package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import im.bclpbkiauv.javaBean.fc.RequestFcReportBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.ColorTextView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.utils.Utils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;

public class FcReportActivity extends CommFcActivity {
    private static final String TAG = FcReportActivity.class.getSimpleName();
    private long ForumID;
    private int ReportType = 0;
    private FrameLayout container;
    private ImageView img_report1;
    private ImageView img_report2;
    private ImageView img_report3;
    private ImageView img_report4;
    private LinearLayout lin_report1;
    private LinearLayout lin_report2;
    private LinearLayout lin_report3;
    private LinearLayout lin_report4;
    private RequestFcReportBean mRequestFcReportBean;
    private ColorTextView submit;

    public FcReportActivity(long ForumID2) {
        this.ForumID = ForumID2;
    }

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_friends_cricle_report;
    }

    /* access modifiers changed from: protected */
    public void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("friends_circle_report", R.string.friends_circle_report));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FcReportActivity.this.finishFragment();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void initView() {
        initActionBar();
        FrameLayout frameLayout = (FrameLayout) this.fragmentView.findViewById(R.id.container);
        this.container = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.submit = (ColorTextView) this.fragmentView.findViewById(R.id.submit);
        this.lin_report1 = (LinearLayout) this.fragmentView.findViewById(R.id.lin_report1);
        this.lin_report2 = (LinearLayout) this.fragmentView.findViewById(R.id.lin_report2);
        this.lin_report3 = (LinearLayout) this.fragmentView.findViewById(R.id.lin_report3);
        this.lin_report4 = (LinearLayout) this.fragmentView.findViewById(R.id.lin_report4);
        this.lin_report1.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.lin_report2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.lin_report3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.lin_report4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.img_report1 = (ImageView) this.fragmentView.findViewById(R.id.img_report1);
        this.img_report2 = (ImageView) this.fragmentView.findViewById(R.id.img_report2);
        this.img_report3 = (ImageView) this.fragmentView.findViewById(R.id.img_report3);
        this.img_report4 = (ImageView) this.fragmentView.findViewById(R.id.img_report4);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{this.mContext.getResources().getColor(R.color.color_87DFFA), this.mContext.getResources().getColor(R.color.color_2ECEFD)});
        gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(50.0f));
        gradientDrawable.setShape(0);
        this.submit.setBackground(gradientDrawable);
        this.submit.setAlpha(0.5f);
        this.fragmentView.findViewById(R.id.lin_report1).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcReportActivity.this.changeImgStatus(1);
            }
        });
        this.fragmentView.findViewById(R.id.lin_report2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcReportActivity.this.changeImgStatus(2);
            }
        });
        this.fragmentView.findViewById(R.id.lin_report3).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcReportActivity.this.changeImgStatus(3);
            }
        });
        this.fragmentView.findViewById(R.id.lin_report4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcReportActivity.this.changeImgStatus(4);
            }
        });
        this.fragmentView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcReportActivity.this.doReport();
            }
        });
    }

    /* access modifiers changed from: private */
    public void changeImgStatus(int position) {
        this.submit.setAlpha(1.0f);
        this.img_report1.setVisibility(8);
        this.img_report2.setVisibility(8);
        this.img_report3.setVisibility(8);
        this.img_report4.setVisibility(8);
        if (position == 1) {
            this.img_report1.setVisibility(0);
            this.ReportType = 1;
        } else if (position == 2) {
            this.img_report2.setVisibility(0);
            this.ReportType = 2;
        } else if (position == 3) {
            this.img_report3.setVisibility(0);
            this.ReportType = 3;
        } else if (position == 4) {
            this.img_report4.setVisibility(0);
            this.ReportType = 4;
        }
    }

    /* access modifiers changed from: protected */
    public void initData() {
    }

    /* access modifiers changed from: private */
    public void doReport() {
        if (this.ReportType == 0) {
            FcToastUtils.show((int) R.string.firendscircle_choose_report_type);
        } else if (Utils.isConnected(this.mContext)) {
            FcToastUtils.show((CharSequence) LocaleController.getString("fc_reportsuc", R.string.fc_reportsuc));
        } else {
            FcToastUtils.show((CharSequence) LocaleController.getString("fc_reportfail", R.string.fc_reportfail));
        }
    }
}
