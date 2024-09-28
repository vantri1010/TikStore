package im.bclpbkiauv.ui.hui.packet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.utils.DataTools;
import im.bclpbkiauv.messenger.utils.TextTool;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketBean;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketDetailRecord;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketDetailResponse;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.math.BigDecimal;
import java.util.ArrayList;

public class RedpktGroupStateActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public RedpacketDetailResponse bean;
    /* access modifiers changed from: private */
    public int headerRow = -1;
    /* access modifiers changed from: private */
    public int headerSessionRow = -1;
    /* access modifiers changed from: private */
    public boolean isRandom;
    private MessageObject messageObject;
    @BindView(2131297085)
    RecyclerListView rcyRpkHistory;
    private int recordEnd = -1;
    private int recordStart = -1;
    /* access modifiers changed from: private */
    public ArrayList<RedpacketDetailRecord> records;
    /* access modifiers changed from: private */
    public int rowCount = 0;
    private MryTextView rptView;
    private final int rpt_record = 101;
    private TLRPC.User targetUser;
    @BindView(2131297833)
    TextView tvRpkBackDesc;

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_redpkg_group_state_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(-328966);
        useButterKnife();
        initActionBar();
        updateRows();
        initViews();
        return this.fragmentView;
    }

    public void setBean(RedpacketDetailResponse bean2) {
        this.bean = bean2;
        this.records = bean2.getRecord();
        if (bean2.getRed() != null && bean2.getRed().getGrantType() != null) {
            this.isRandom = "1".equals(bean2.getRed().getGrantType());
        }
    }

    public void setMessageObject(MessageObject messageObject2) {
        this.messageObject = messageObject2;
    }

    public void setTargetUser(TLRPC.User targetUser2) {
        this.targetUser = targetUser2;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.DetailsOfRedPackets));
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsColor(-1, false);
        this.actionBar.setBackgroundColor(-2737326);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back_white);
        this.actionBar.getBackButton().setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RedpktGroupStateActivity.this.lambda$initActionBar$0$RedpktGroupStateActivity(view);
            }
        });
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    RedpktGroupStateActivity.this.finishFragment();
                } else if (id == 101) {
                    RedpktGroupStateActivity.this.presentFragment(new RedpktRecordsActivity());
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

    public /* synthetic */ void lambda$initActionBar$0$RedpktGroupStateActivity(View v) {
        finishFragment();
    }

    private void updateRows() {
        int i = this.rowCount;
        int i2 = i + 1;
        this.rowCount = i2;
        this.headerRow = i;
        this.rowCount = i2 + 1;
        this.headerSessionRow = i2;
        ArrayList<RedpacketDetailRecord> arrayList = this.records;
        if (arrayList != null && arrayList.size() > 0) {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.recordStart = i3;
            this.recordEnd = (i3 + this.records.size()) - 1;
            this.rowCount += this.records.size() - 1;
        }
    }

    private void initViews() {
        this.rcyRpkHistory.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        this.rcyRpkHistory.setAdapter(new ListAdapter(getParentActivity()));
        this.tvRpkBackDesc.setText(LocaleController.getString(R.string.RedPacketsWillReturnWhenNotReceivedIn24Hours));
        RedpacketDetailResponse redpacketDetailResponse = this.bean;
        if (redpacketDetailResponse != null && redpacketDetailResponse.getRecord() != null && this.bean.getRed() != null) {
            this.tvRpkBackDesc.setVisibility(this.bean.getRed().getNumber() == this.bean.getRecord().size() ? 8 : 0);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context mContext2) {
            this.mContext = mContext2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_redpkg_state_header_layout, (ViewGroup) null);
                view.setBackgroundColor(-1);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else if (viewType != 1) {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_red_packet_records, (ViewGroup) null);
                view.setBackgroundColor(-1);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(72.0f)));
            } else {
                view = new TextView(this.mContext);
                view.setBackgroundColor(-328966);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(48.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == RedpktGroupStateActivity.this.headerRow) {
                return 0;
            }
            if (position == RedpktGroupStateActivity.this.headerSessionRow) {
                return 1;
            }
            return 2;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CharSequence charSequence;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            if (i == 0) {
                BackupImageView ivRptAvatar = (BackupImageView) viewHolder.itemView.findViewById(R.id.ivRptAvatar);
                TextView tvRptName = (TextView) viewHolder.itemView.findViewById(R.id.tvRptName);
                TextView tvRptGreet = (TextView) viewHolder.itemView.findViewById(R.id.tvRptGreet);
                TextView tvReceivedCount = (TextView) viewHolder.itemView.findViewById(R.id.tvReceivedCount);
                TextView tvReceivedPromt = (TextView) viewHolder.itemView.findViewById(R.id.tvReceivedPromt);
                TLRPC.User sender = RedpktGroupStateActivity.this.getMessagesController().getUser(Integer.valueOf(Integer.parseInt(RedpktGroupStateActivity.this.bean.getRed().getInitiatorUserId())));
                if (sender != null) {
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
                    avatarDrawable.setInfo(sender);
                    ivRptAvatar.setRoundRadius(AndroidUtilities.dp(32.0f));
                    ivRptAvatar.getImageReceiver().setCurrentAccount(RedpktGroupStateActivity.this.currentAccount);
                    ivRptAvatar.setImage(ImageLocation.getForUser(sender, false), "50_50", (Drawable) avatarDrawable, (Object) sender);
                    tvRptName.setText(sender.first_name);
                }
                if (RedpktGroupStateActivity.this.bean.getRed().getRemarks() == null || TextUtils.isEmpty(RedpktGroupStateActivity.this.bean.getRed().getRemarks())) {
                    tvRptGreet.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
                    tvRptGreet.setTextColor(-1);
                } else {
                    tvRptGreet.setText(RedpktGroupStateActivity.this.bean.getRed().getRemarks());
                    tvRptGreet.setTextColor(-1);
                }
                if (RedpktGroupStateActivity.this.bean.getRed().getIsReceived() == 0) {
                    tvReceivedCount.setVisibility(8);
                    tvReceivedPromt.setVisibility(8);
                    return;
                }
                tvReceivedCount.setVisibility(0);
                tvReceivedPromt.setVisibility(0);
                TextTool.Builder textBuilder = TextTool.getBuilder("").setContext(RedpktGroupStateActivity.this.getParentActivity()).setBold().setAlign(Layout.Alignment.ALIGN_CENTER).append(DataTools.format2Decimals(new BigDecimal(String.valueOf(RedpktGroupStateActivity.this.bean.getRed().getMoney())).divide(new BigDecimal("100")).toString())).setForegroundColor(-2737326).setProportion(2.0f);
                textBuilder.append(LocaleController.getString(R.string.UnitCNY));
                textBuilder.into(tvReceivedCount);
                ClickableSpan jumpToWallet = new ClickableSpan() {
                    public void onClick(View widget) {
                    }

                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(-16745729);
                        ds.setUnderlineText(false);
                    }
                };
                tvReceivedPromt.setMovementMethod(LinkMovementMethod.getInstance());
                TextTool.getBuilder("").setContext(RedpktGroupStateActivity.this.getParentActivity()).setBold().setAlign(Layout.Alignment.ALIGN_CENTER).append(LocaleController.getString(R.string.SaveToBalanceAndDetails)).setForegroundColor(-6710887).append(LocaleController.getString(R.string.Wallet)).setForegroundColor(-14250753).setClickSpan(jumpToWallet).append(LocaleController.getString(R.string.TransferSeeIn)).setForegroundColor(-6710887).into(tvReceivedPromt);
            } else if (i != 1) {
                BackupImageView ivAvatar = (BackupImageView) viewHolder.itemView.findViewById(R.id.iv_avatar);
                TextView tvName = (TextView) viewHolder.itemView.findViewById(R.id.tv_type_and_from);
                TextView tvCGnum = (TextView) viewHolder.itemView.findViewById(R.id.tv_CG_num);
                TextView tvCollectTime = (TextView) viewHolder.itemView.findViewById(R.id.tv_time);
                TextView tvFeatureBest = (TextView) viewHolder.itemView.findViewById(R.id.tv_state);
                TextView tvMoneyUnit = (TextView) viewHolder.itemView.findViewById(R.id.tvMoneyUnit);
                RedpacketDetailRecord redpkgRecord = (RedpacketDetailRecord) RedpktGroupStateActivity.this.records.get(i - 2);
                ImageView ivFeatureImage = (ImageView) viewHolder.itemView.findViewById(R.id.iv_best);
                TLRPC.User receiver = RedpktGroupStateActivity.this.getMessagesController().getUser(Integer.valueOf(Integer.parseInt(redpkgRecord.getUserId())));
                if (receiver != null) {
                    AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                    avatarDrawable2.setTextSize(AndroidUtilities.dp(16.0f));
                    avatarDrawable2.setInfo(receiver);
                    ivAvatar.setRoundRadius(AndroidUtilities.dp(32.0f));
                    charSequence = "";
                    ivAvatar.getImageReceiver().setCurrentAccount(RedpktGroupStateActivity.this.currentAccount);
                    ivAvatar.setImage(ImageLocation.getForUser(receiver, false), "50_50", (Drawable) avatarDrawable2, (Object) receiver);
                    tvName.setText(receiver.first_name);
                } else {
                    charSequence = "";
                }
                tvCGnum.setText(DataTools.format2Decimals(new BigDecimal(redpkgRecord.getTotalFee()).divide(new BigDecimal("100")).toString()));
                tvMoneyUnit.setText(LocaleController.getString(R.string.UnitCNY));
                tvCollectTime.setText(redpkgRecord.getCreateTimeFormat());
                if (!"1".equals(redpkgRecord.getIsOptimum()) || !RedpktGroupStateActivity.this.isRandom) {
                    tvFeatureBest.setText(charSequence);
                    ivFeatureImage.setVisibility(8);
                    RecyclerView.ViewHolder viewHolder2 = holder;
                    return;
                }
                tvFeatureBest.setVisibility(0);
                ivFeatureImage.setVisibility(0);
                tvFeatureBest.setText(LocaleController.getString(R.string.BestOfLuck));
                tvFeatureBest.setTextColor(-2737326);
                RecyclerView.ViewHolder viewHolder3 = holder;
            } else {
                TextView cell = (TextView) holder.itemView;
                cell.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
                cell.setGravity(16);
                BigDecimal total = new BigDecimal(RedpktGroupStateActivity.this.bean.getRed().getTotalFee());
                BigDecimal subtract = total.subtract(new BigDecimal(RedpktGroupStateActivity.this.bean.getRed().getSurplusAmount()));
                int redType = Integer.parseInt(RedpktGroupStateActivity.this.bean.getRed().getRedType());
                int status = Integer.parseInt(RedpktGroupStateActivity.this.bean.getRed().getStatus());
                StringBuilder builder = new StringBuilder("");
                if (redType == 2) {
                    if (status == 0) {
                        builder.append("1");
                        builder.append(LocaleController.getString(R.string.redpacket_each));
                        builder.append(LocaleController.getString(R.string.RedPacket));
                        builder.append(LocaleController.getString(R.string.TotalSingleWords));
                        builder.append(DataTools.format2Decimals(total.divide(new BigDecimal("100")).toString()));
                        builder.append(LocaleController.getString(R.string.UnitCNY));
                        builder.append("，");
                        builder.append(LocaleController.getString(R.string.WaitToReceived));
                    } else if (status == 1) {
                        builder.append(LocaleController.getString(R.string.AlreadyReceive));
                        builder.append("1/1");
                        builder.append(LocaleController.getString(R.string.redpacket_each));
                        builder.append("，");
                        builder.append(LocaleController.getString(R.string.TotalSingleWords));
                        builder.append(DataTools.format2Decimals(total.divide(new BigDecimal("100")).toString()));
                        builder.append("/");
                        builder.append(DataTools.format2Decimals(total.divide(new BigDecimal("100")).toString()));
                        builder.append(LocaleController.getString(R.string.UnitCNY));
                    } else {
                        builder.append(LocaleController.getString(R.string.RedpacketHadExpired));
                        builder.append(LocaleController.getString(R.string.FullStop));
                        builder.append(LocaleController.getString(R.string.AlreadyReceive));
                        builder.append("0/1");
                        builder.append(LocaleController.getString(R.string.redpacket_each));
                        builder.append(LocaleController.getString(R.string.Comma));
                        builder.append(LocaleController.getString(R.string.TotalSingleWords));
                        builder.append("0.00/");
                        builder.append(DataTools.format2Decimals(total.divide(new BigDecimal("100")).toString()));
                        builder.append(LocaleController.getString(R.string.UnitCNY));
                    }
                } else if (status == 0) {
                    if (RedpktGroupStateActivity.this.bean.getRecord() == null || (RedpktGroupStateActivity.this.bean.getRecord() != null && RedpktGroupStateActivity.this.bean.getRecord().size() == 0)) {
                        builder.append(RedpktGroupStateActivity.this.bean.getRed().getNumber());
                        builder.append(LocaleController.getString(R.string.redpacket_each));
                        builder.append(LocaleController.getString(R.string.RedPacket));
                        builder.append(LocaleController.getString(R.string.TotalSingleWords));
                        builder.append(DataTools.format2Decimals(total.divide(new BigDecimal("100")).toString()));
                        builder.append(LocaleController.getString(R.string.UnitCNY));
                        builder.append(LocaleController.getString(R.string.Comma));
                        builder.append(LocaleController.getString(R.string.WaitToReceived));
                    } else {
                        builder.append(LocaleController.getString(R.string.AlreadyReceive));
                        builder.append(RedpktGroupStateActivity.this.bean.getRed().getNumber() - RedpktGroupStateActivity.this.bean.getRed().getSurplusNumber());
                        builder.append("/");
                        builder.append(RedpktGroupStateActivity.this.bean.getRed().getNumber());
                        builder.append(LocaleController.getString(R.string.TotalSingleWords));
                        builder.append(DataTools.format2Decimals(subtract.divide(new BigDecimal("100")).toString()));
                        builder.append("/");
                        builder.append(DataTools.format2Decimals(total.divide(new BigDecimal("100")).toString()));
                        builder.append(LocaleController.getString(R.string.UnitCNY));
                    }
                } else if (status == 1) {
                    RedpacketBean res = RedpktGroupStateActivity.this.bean.getRed();
                    builder.append(res.getNumber());
                    builder.append(LocaleController.getString(R.string.redpacket_each));
                    builder.append(LocaleController.getString(R.string.RedPacket));
                    builder.append(LocaleController.getString(R.string.Comma));
                    builder.append(res.getDuration());
                } else {
                    builder.append(LocaleController.getString(R.string.RedpacketHadExpired));
                    builder.append(LocaleController.getString(R.string.Comma));
                    builder.append(LocaleController.getString(R.string.AlreadyReceive));
                    builder.append(RedpktGroupStateActivity.this.bean.getRed().getNumber() - RedpktGroupStateActivity.this.bean.getRed().getSurplusNumber());
                    builder.append("/");
                    builder.append(RedpktGroupStateActivity.this.bean.getRed().getNumber());
                    builder.append(LocaleController.getString(R.string.Comma));
                    builder.append(LocaleController.getString(R.string.TotalSingleWords));
                    builder.append(DataTools.format2Decimals(subtract.divide(new BigDecimal("100")).toString()));
                    builder.append("/");
                    builder.append(DataTools.format2Decimals(total.divide(new BigDecimal("100")).toString()));
                    builder.append(LocaleController.getString(R.string.UnitCNY));
                }
                cell.setText(builder.toString());
            }
        }

        public int getItemCount() {
            return RedpktGroupStateActivity.this.rowCount;
        }
    }
}
