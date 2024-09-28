package im.bclpbkiauv.ui.hui.packet;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse;
import java.math.BigDecimal;

public class RedpktDetailActivity extends BaseFragment {
    private RedpacketResponse bean;
    @BindView(2131296392)
    BackupImageView bivReceiverAvatar;
    @BindView(2131296633)
    FrameLayout flRpkRecordLayout;
    @BindView(2131296769)
    BackupImageView ivRptAvatar;
    @BindView(2131296923)
    LinearLayout llUserLayout;
    private TextView rptView;
    private final int rpt_record = 101;
    private TLRPC.User sender;
    @BindView(2131297832)
    TextView tvRpkAmount;
    @BindView(2131297833)
    TextView tvRpkBackDesc;
    @BindView(2131297836)
    TextView tvRpkName;
    @BindView(2131297838)
    TextView tvRpkReceiveTime;
    @BindView(2131297626)
    TextView tvRptGreet;
    @BindView(2131297629)
    TextView tvRptName;
    @BindView(2131297631)
    TextView tvRptState;

    public void setBean(RedpacketResponse bean2) {
        this.bean = bean2;
        this.sender = getMessagesController().getUser(Integer.valueOf(Integer.parseInt(bean2.getRed().getInitiatorUserId())));
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_redpkg_state_layout, (ViewGroup) null, false);
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
                RedpktDetailActivity.this.lambda$initActionBar$0$RedpktDetailActivity(view);
            }
        });
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    RedpktDetailActivity.this.finishFragment();
                } else if (id == 101) {
                    RedpktDetailActivity.this.presentFragment(new RedpktRecordsActivity());
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        TextView textView = new TextView(getParentActivity());
        this.rptView = textView;
        textView.setText(LocaleController.getString(R.string.RedPacketRecords));
        this.rptView.setTextSize(1, 14.0f);
        this.rptView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.rptView.setTextColor(-1);
        this.rptView.setGravity(17);
        menu.addItemView(101, this.rptView);
    }

    public /* synthetic */ void lambda$initActionBar$0$RedpktDetailActivity(View v) {
        finishFragment();
    }

    private void initViews() {
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
        avatarDrawable.setInfo(this.sender);
        this.ivRptAvatar.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.ivRptAvatar.getImageReceiver().setCurrentAccount(this.currentAccount);
        this.ivRptAvatar.setImage(ImageLocation.getForUser(this.sender, false), "50_50", (Drawable) avatarDrawable, (Object) this.sender);
        this.tvRptName.setText(UserObject.getName(this.sender));
        this.tvRpkBackDesc.setText(LocaleController.getString(R.string.RedPacketsWillReturnWhenNotReceivedIn24Hours));
        RedpacketResponse redpacketResponse = this.bean;
        if (redpacketResponse != null) {
            if (redpacketResponse.getRed().getRemarks() == null) {
                this.tvRptGreet.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
            } else if (TextUtils.isEmpty(this.bean.getRed().getRemarks())) {
                this.tvRptGreet.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
            } else {
                this.tvRptGreet.setText(this.bean.getRed().getRemarks());
            }
            String transTotal = "";
            if (this.bean.getRed().getTotalFee() != null && !TextUtils.isEmpty(this.bean.getRed().getTotalFee())) {
                transTotal = DataTools.format2Decimals(new BigDecimal(this.bean.getRed().getTotalFee()).multiply(new BigDecimal("0.01")).toString());
            }
            if (this.bean.getRed().getStatus() != null && !TextUtils.isEmpty(this.bean.getRed().getStatus())) {
                if ("0".equals(this.bean.getRed().getStatus())) {
                    this.tvRptState.setText(String.format("" + "1" + LocaleController.getString(R.string.redpacket_each) + LocaleController.getString(R.string.RedPacket) + LocaleController.getString(R.string.TotalSingleWords) + "%s" + LocaleController.getString(R.string.UnitCNY) + LocaleController.getString(R.string.Comma) + "%s", new Object[]{transTotal, LocaleController.getString(R.string.WaitToReceived)}));
                } else if ("1".equals(this.bean.getRed().getStatus())) {
                    this.tvRptState.setText(String.format("" + LocaleController.getString(R.string.AlreadyReceive) + "1/1" + LocaleController.getString(R.string.redpacket_each) + LocaleController.getString(R.string.Comma) + LocaleController.getString(R.string.TotalSingleWords) + "%s" + LocaleController.getString(R.string.UnitCNY) + "/%s" + LocaleController.getString(R.string.UnitCNY), new Object[]{transTotal, transTotal}));
                    this.flRpkRecordLayout.setVisibility(0);
                    TLRPC.User receiver = getMessagesController().getUser(Integer.valueOf(Integer.parseInt(this.bean.getRed().getRecipientUserId())));
                    AvatarDrawable recvAvatar = new AvatarDrawable();
                    recvAvatar.setTextSize(AndroidUtilities.dp(16.0f));
                    recvAvatar.setInfo(receiver);
                    this.bivReceiverAvatar.setRoundRadius(AndroidUtilities.dp(32.0f));
                    this.bivReceiverAvatar.getImageReceiver().setCurrentAccount(this.currentAccount);
                    this.bivReceiverAvatar.setImage(ImageLocation.getForUser(receiver, false), "50_50", (Drawable) recvAvatar, (Object) receiver);
                    this.tvRpkName.setText(receiver.first_name);
                    this.tvRpkAmount.setText(transTotal);
                    if (!(this.bean.getRed() == null || this.bean.getRed().getCompleteTime() == null)) {
                        this.tvRpkReceiveTime.setText(this.bean.getRed().getCompleteTimeFormat());
                    }
                    this.tvRpkBackDesc.setVisibility(8);
                } else {
                    this.tvRptState.setText(String.format("" + LocaleController.getString(R.string.RedpacketHadExpired) + LocaleController.getString(R.string.FullStop) + LocaleController.getString(R.string.AlreadyReceive) + "0/1" + LocaleController.getString(R.string.redpacket_each) + LocaleController.getString(R.string.Comma) + LocaleController.getString(R.string.TotalSingleWords) + "0.00/%s" + LocaleController.getString(R.string.UnitCNY), new Object[]{transTotal}));
                }
            }
        }
    }
}
