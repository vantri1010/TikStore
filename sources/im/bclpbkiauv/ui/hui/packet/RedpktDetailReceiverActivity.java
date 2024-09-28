package im.bclpbkiauv.ui.hui.packet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.utils.DataTools;
import im.bclpbkiauv.messenger.utils.TextTool;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.math.BigDecimal;

public class RedpktDetailReceiverActivity extends BaseFragment {
    private RedpacketResponse bean;
    @BindView(2131296393)
    BackupImageView bivRpkAvatar;
    @BindView(2131296923)
    LinearLayout llUserLayout;
    private MryTextView rptView;
    private final int rpt_record = 101;
    private TLRPC.User sender;
    @BindView(2131297832)
    TextView tvRpkAmount;
    @BindView(2131297834)
    TextView tvRpkDesc;
    @BindView(2131297835)
    TextView tvRpkGreet;
    @BindView(2131297836)
    TextView tvRpkName;

    public void setBean(RedpacketResponse bean2) {
        this.bean = bean2;
        this.sender = getMessagesController().getUser(Integer.valueOf(Integer.parseInt(bean2.getRed().getInitiatorUserId())));
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_redpkg_receiver_state_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(-328966);
        useButterKnife();
        initActionBar();
        initViews();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setAddToContainer(false);
        ((FrameLayout) this.fragmentView).addView(this.actionBar);
        this.actionBar.setTitle(LocaleController.getString(R.string.DetailsOfRedPackets));
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsColor(-1, false);
        this.actionBar.setBackgroundColor(0);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back_white);
        this.actionBar.getBackButton().setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RedpktDetailReceiverActivity.this.lambda$initActionBar$0$RedpktDetailReceiverActivity(view);
            }
        });
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    RedpktDetailReceiverActivity.this.finishFragment();
                } else if (id == 101) {
                    RedpktDetailReceiverActivity.this.presentFragment(new RedpktRecordsActivity());
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        MryTextView mryTextView = new MryTextView(getParentActivity());
        this.rptView = mryTextView;
        mryTextView.setMryText(R.string.RedPacketRecords);
        this.rptView.setTextSize(1, 14.0f);
        this.rptView.setBold();
        this.rptView.setTextColor(-1);
        this.rptView.setGravity(17);
        menu.addItemView(101, this.rptView);
    }

    public /* synthetic */ void lambda$initActionBar$0$RedpktDetailReceiverActivity(View v) {
        finishFragment();
    }

    private void initViews() {
        if (this.sender != null) {
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
            avatarDrawable.setInfo(this.sender);
            this.bivRpkAvatar.setRoundRadius(AndroidUtilities.dp(7.0f));
            this.bivRpkAvatar.getImageReceiver().setCurrentAccount(this.currentAccount);
            this.bivRpkAvatar.setImage(ImageLocation.getForUser(this.sender, false), "50_50", (Drawable) avatarDrawable, (Object) this.sender);
            this.tvRpkName.setText(UserObject.getName(this.sender));
        }
        RedpacketResponse redpacketResponse = this.bean;
        if (redpacketResponse != null) {
            if (redpacketResponse.getRed().getRemarks() == null) {
                this.tvRpkGreet.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
            } else if (TextUtils.isEmpty(this.bean.getRed().getRemarks())) {
                this.tvRpkGreet.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
            } else {
                this.tvRpkGreet.setText(this.bean.getRed().getRemarks());
            }
            String transTotal = "";
            if (this.bean.getRed().getTotalFee() != null && !TextUtils.isEmpty(this.bean.getRed().getTotalFee())) {
                transTotal = DataTools.format2Decimals(new BigDecimal(this.bean.getRed().getTotalFee()).divide(new BigDecimal("100")).toString());
            }
            TextTool.Builder builder = TextTool.getBuilder("");
            builder.append(transTotal).setForegroundColor(-3416);
            builder.append(LocaleController.getString(R.string.UnitCNY));
            builder.setProportion(0.5f).into(this.tvRpkAmount);
            this.tvRpkDesc.setText(Html.fromHtml(LocaleController.getString(R.string.ViewWalletBalance)));
        }
    }
}
