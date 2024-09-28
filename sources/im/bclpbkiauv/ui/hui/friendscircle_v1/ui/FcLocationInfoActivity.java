package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.javaBean.fc.FcLocationInfoBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;

public class FcLocationInfoActivity extends BaseFragment {
    private FcLocationInfoBean fcLocationInfoBean;
    private LinearLayout ll_location_address;
    private Context mContext;
    private TextView tv_location_address;
    private TextView tv_location_name;

    public FcLocationInfoActivity(FcLocationInfoBean fcLocationInfoBean2) {
        this.fcLocationInfoBean = fcLocationInfoBean2;
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_friends_cricle_location_info, (ViewGroup) null);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initView();
        initData();
        return this.fragmentView;
    }

    private void initData() {
        FcLocationInfoBean fcLocationInfoBean2 = this.fcLocationInfoBean;
        if (fcLocationInfoBean2 != null) {
            String locationName = fcLocationInfoBean2.getLocationName();
            String locationAddress = this.fcLocationInfoBean.getLocationAddress();
            String locationCity = this.fcLocationInfoBean.getLocationCity();
            if (TextUtils.isEmpty(locationName)) {
                return;
            }
            if (TextUtils.equals(locationName, locationAddress)) {
                this.tv_location_name.setText(locationName);
                this.ll_location_address.setVisibility(8);
                return;
            }
            if (!TextUtils.isEmpty(locationName)) {
                if (!TextUtils.isEmpty(locationCity) && !TextUtils.equals(locationName, locationCity)) {
                    locationName = locationCity.replace("市", "") + "·" + locationName;
                }
                this.tv_location_name.setText(locationName);
            }
            if (TextUtils.isEmpty(locationAddress)) {
                this.ll_location_address.setVisibility(8);
            } else {
                this.tv_location_address.setText(locationAddress);
            }
        }
    }

    private void initView() {
        this.tv_location_name = (TextView) this.fragmentView.findViewById(R.id.tv_location_name);
        this.ll_location_address = (LinearLayout) this.fragmentView.findViewById(R.id.ll_location_address);
        this.tv_location_address = (TextView) this.fragmentView.findViewById(R.id.tv_location_address);
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("friendscircle_location_info_title", R.string.friendscircle_location_info_title));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FcLocationInfoActivity.this.finishFragment();
                }
            }
        });
    }
}
