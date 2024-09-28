package im.bclpbkiauv.ui.hui.packet;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.google.gson.Gson;
import im.bclpbkiauv.javaBean.hongbao.RedTransOperation;
import im.bclpbkiauv.javaBean.hongbao.UnifyBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.utils.DataTools;
import im.bclpbkiauv.messenger.utils.TextTool;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCRedpacket;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketBean;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketDetailRecord;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketDetailResponse;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.utils.number.StringUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class RedpktGroupDetailActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public RedpacketBean bean;
    private TLRPC.Chat chat;
    /* access modifiers changed from: private */
    public int headerRow = -1;
    /* access modifiers changed from: private */
    public int headerSessionRow = -1;
    /* access modifiers changed from: private */
    public boolean isLoaded;
    /* access modifiers changed from: private */
    public boolean isLoading;
    /* access modifiers changed from: private */
    public boolean isRandom;
    private ListAdapter listAdapter;
    private int messageId;
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

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
        if (isOpen) {
            getRecords();
        }
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
                RedpktGroupDetailActivity.this.lambda$initActionBar$0$RedpktGroupDetailActivity(view);
            }
        });
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    RedpktGroupDetailActivity.this.finishFragment();
                } else if (id == 101) {
                    RedpktGroupDetailActivity.this.presentFragment(new RedpktRecordsActivity());
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

    public /* synthetic */ void lambda$initActionBar$0$RedpktGroupDetailActivity(View v) {
        finishFragment();
    }

    public void setBean(RedpacketBean bean2) {
        this.bean = bean2;
        if (bean2 != null && bean2.getGrantType() != null) {
            this.isRandom = "1".equals(bean2.getGrantType());
        }
    }

    public void setChat(TLRPC.Chat chat2) {
        this.chat = chat2;
    }

    public void setMessageId(int mid) {
        this.messageId = mid;
    }

    private void updateRows() {
        this.headerRow = -1;
        this.headerSessionRow = -1;
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.headerRow = 0;
        this.rowCount = i + 1;
        this.headerSessionRow = i;
        ArrayList<RedpacketDetailRecord> arrayList = this.records;
        if (arrayList != null && arrayList.size() > 0) {
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.recordStart = i2;
            this.recordEnd = (i2 + this.records.size()) - 1;
            this.rowCount += this.records.size() - 1;
        }
    }

    private void initViews() {
        this.rcyRpkHistory.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        ListAdapter listAdapter2 = new ListAdapter(getParentActivity());
        this.listAdapter = listAdapter2;
        this.rcyRpkHistory.setAdapter(listAdapter2);
        this.tvRpkBackDesc.setText(LocaleController.getString(R.string.RedPacketsWillReturnWhenNotReceivedIn24Hours));
    }

    private void getRecords() {
        String str;
        if (!this.isLoading) {
            this.isLoading = true;
            TLRPCRedpacket.CL_message_rpkTransferCheck req = new TLRPCRedpacket.CL_message_rpkTransferCheck();
            req.trans = 0;
            if (this.chat != null) {
                int redType = this.bean.getRedTypeInt();
                if (redType == 0) {
                    req.type = 2;
                } else if (redType == 1) {
                    req.type = 1;
                } else if (redType == 2) {
                    req.type = 3;
                }
            } else {
                req.type = 0;
            }
            req.flags = 2;
            TLRPC.InputPeer inputPeer = new TLRPC.TL_inputPeerChannel();
            inputPeer.channel_id = this.chat.id;
            inputPeer.access_hash = this.chat.access_hash;
            req.peer = inputPeer;
            req.id = this.messageId;
            String serialCode = this.bean.getSerialCode();
            String str2 = getUserConfig().clientUserId + "";
            TLRPC.Chat chat2 = this.chat;
            if (chat2 == null) {
                str = "";
            } else {
                str = String.valueOf(chat2.id);
            }
            RedTransOperation redTransOperation = new RedTransOperation(serialCode, str2, str, StringUtils.getNonceStr(getConnectionsManager().getCurrentTime()), UnifyBean.BUSINESS_KEY_REDPACKET_DETAIL, "0.0.1");
            TLRPC.TL_dataJSON dataJSON = new TLRPC.TL_dataJSON();
            dataJSON.data = new Gson().toJson((Object) redTransOperation);
            req.data = dataJSON;
            XAlertDialog progressView = new XAlertDialog(getParentActivity(), 5);
            int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(progressView) {
                private final /* synthetic */ XAlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    RedpktGroupDetailActivity.this.lambda$getRecords$3$RedpktGroupDetailActivity(this.f$1, tLObject, tL_error);
                }
            });
            getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
            progressView.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    RedpktGroupDetailActivity.this.lambda$getRecords$4$RedpktGroupDetailActivity(this.f$1, dialogInterface);
                }
            });
            progressView.show();
        }
    }

    public /* synthetic */ void lambda$getRecords$3$RedpktGroupDetailActivity(XAlertDialog progressView, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                XAlertDialog.this.dismiss();
            }
        });
        this.isLoading = false;
        if (error != null) {
            if (BuildVars.RELEASE_VERSION) {
                WalletDialogUtil.showConfirmBtnWalletDialog(this, WalletErrorUtil.getErrorDescription(error.text));
            } else {
                AlertsCreator.showSimpleToast(this, WalletErrorUtil.getErrorDescription(LocaleController.formatString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater, new Object[0]), error.text));
            }
        } else if (response instanceof TLRPC.TL_updates) {
            Iterator it = ((TLRPC.TL_updates) response).updates.iterator();
            while (it.hasNext()) {
                TLRPC.Update update = (TLRPC.Update) it.next();
                if (update instanceof TLRPCRedpacket.CL_updateRpkTransfer) {
                    TLApiModel<RedpacketDetailResponse> parse = TLJsonResolve.parse((TLObject) ((TLRPCRedpacket.CL_updateRpkTransfer) update).data, (Class<?>) RedpacketDetailResponse.class);
                    if (parse.isSuccess() || "20004".equals(parse.code) || "20013".equals(parse.code) || "20008".equals(parse.code)) {
                        RedpacketDetailResponse retBean = (RedpacketDetailResponse) parse.model;
                        if (retBean != null) {
                            this.isLoaded = true;
                            this.bean = retBean.getRed();
                            ArrayList<RedpacketDetailRecord> record = retBean.getRecord();
                            this.records = record;
                            groupsRecords(record);
                            updateRows();
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    RedpktGroupDetailActivity.this.lambda$null$2$RedpktGroupDetailActivity();
                                }
                            });
                            return;
                        }
                        return;
                    } else if (BuildVars.RELEASE_VERSION) {
                        WalletErrorUtil.parseErrorDialog((Object) this, LocaleController.getString(R.string.SystemIsBusyAndTryAgainLater));
                        return;
                    } else {
                        WalletErrorUtil.parseErrorDialog((Object) this, parse.code, parse.message);
                        return;
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$2$RedpktGroupDetailActivity() {
        this.listAdapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$getRecords$4$RedpktGroupDetailActivity(int reqId, DialogInterface hintDialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    private void groupsRecords(ArrayList<RedpacketDetailRecord> record) {
        if (record != null && record.size() > 0) {
            Collections.sort(record, $$Lambda$RedpktGroupDetailActivity$XoZeusCkTtNXpgbCQJL5tRfL6g.INSTANCE);
        }
    }

    static /* synthetic */ int lambda$groupsRecords$5(RedpacketDetailRecord o1, RedpacketDetailRecord o2) {
        if (o1.getCreatTimeLong() > o2.getCreatTimeLong()) {
            return 1;
        }
        if (o1.getCreatTimeLong() < o2.getCreatTimeLong()) {
            return -1;
        }
        return 0;
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
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_red_packet_group_info_layout, (ViewGroup) null);
                view.setBackgroundColor(-1);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(70.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == RedpktGroupDetailActivity.this.headerRow) {
                return 0;
            }
            if (position == RedpktGroupDetailActivity.this.headerSessionRow) {
                return 1;
            }
            return 2;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String str;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            if (i == 0) {
                BackupImageView ivRptAvatar = (BackupImageView) viewHolder.itemView.findViewById(R.id.ivRptAvatar);
                BackupImageView zsAvatar = (BackupImageView) viewHolder.itemView.findViewById(R.id.zsAvatar);
                LinearLayout zsLayout = (LinearLayout) viewHolder.itemView.findViewById(R.id.zsLayout);
                TextView zsText = (TextView) viewHolder.itemView.findViewById(R.id.zsText);
                TextView tvRptName = (TextView) viewHolder.itemView.findViewById(R.id.tvRptName);
                TextView tvRptGreet = (TextView) viewHolder.itemView.findViewById(R.id.tvRptGreet);
                TextView tvReceivedCount = (TextView) viewHolder.itemView.findViewById(R.id.tvReceivedCount);
                TextView tvReceivedPromt = (TextView) viewHolder.itemView.findViewById(R.id.tvReceivedPromt);
                ImageView ivHotCoinLogo = (ImageView) viewHolder.itemView.findViewById(R.id.ivHotCoinLogo);
                TLRPC.User sender = RedpktGroupDetailActivity.this.getMessagesController().getUser(Integer.valueOf(Integer.parseInt(RedpktGroupDetailActivity.this.bean.getInitiatorUserId())));
                if (sender != null) {
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
                    avatarDrawable.setInfo(sender);
                    ivRptAvatar.setRoundRadius(AndroidUtilities.dp(7.5f));
                    str = "100";
                    ivRptAvatar.getImageReceiver().setCurrentAccount(RedpktGroupDetailActivity.this.currentAccount);
                    ivRptAvatar.setImage(ImageLocation.getForUser(sender, false), "50_50", (Drawable) avatarDrawable, (Object) sender);
                    tvRptName.setText(UserObject.getName(sender, 16));
                    int redType = RedpktGroupDetailActivity.this.bean.getRedTypeInt();
                    if (redType == 2) {
                        int i2 = redType;
                        if (sender.id == RedpktGroupDetailActivity.this.getUserConfig().getCurrentUser().id || !(sender.id == RedpktGroupDetailActivity.this.getUserConfig().getCurrentUser().id || RedpktGroupDetailActivity.this.bean.getRecipientUserIdInt() == RedpktGroupDetailActivity.this.getUserConfig().getCurrentUser().id)) {
                            TLRPC.User recvUser = RedpktGroupDetailActivity.this.getMessagesController().getUser(Integer.valueOf(RedpktGroupDetailActivity.this.bean.getRecipientUserIdInt()));
                            if (recvUser != null) {
                                zsLayout.setVisibility(0);
                                AvatarDrawable recvDrawable = new AvatarDrawable();
                                BackupImageView backupImageView = ivRptAvatar;
                                recvDrawable.setTextSize(AndroidUtilities.dp(16.0f));
                                recvDrawable.setInfo(sender);
                                zsAvatar.setRoundRadius(AndroidUtilities.dp(3.0f));
                                TLRPC.User user = sender;
                                zsAvatar.getImageReceiver().setCurrentAccount(RedpktGroupDetailActivity.this.currentAccount);
                                zsAvatar.setImage(ImageLocation.getForUser(recvUser, false), "50_50", (Drawable) recvDrawable, (Object) recvUser);
                                zsText.setText(UserObject.getName(recvUser, 10) + LocaleController.getString("Exclusive", R.string.Exclusive));
                            } else {
                                TLRPC.User user2 = sender;
                            }
                        } else {
                            BackupImageView backupImageView2 = ivRptAvatar;
                            TLRPC.User user3 = sender;
                        }
                    } else {
                        BackupImageView backupImageView3 = ivRptAvatar;
                        TLRPC.User user4 = sender;
                    }
                } else {
                    TLRPC.User user5 = sender;
                    str = "100";
                }
                if (RedpktGroupDetailActivity.this.bean.getRemarks() == null || TextUtils.isEmpty(RedpktGroupDetailActivity.this.bean.getRemarks())) {
                    tvRptGreet.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
                    tvRptGreet.setTextColor(-1);
                } else {
                    tvRptGreet.setText(RedpktGroupDetailActivity.this.bean.getRemarks());
                    tvRptGreet.setTextColor(-1);
                }
                if (RedpktGroupDetailActivity.this.bean.getIsReceived() == 0 || RedpktGroupDetailActivity.this.isLoading || !RedpktGroupDetailActivity.this.isLoaded) {
                    tvReceivedCount.setVisibility(8);
                    tvReceivedPromt.setVisibility(8);
                    ivHotCoinLogo.setVisibility(8);
                    return;
                }
                tvReceivedCount.setVisibility(0);
                tvReceivedPromt.setVisibility(0);
                ivHotCoinLogo.setVisibility(0);
                TextTool.getBuilder("").setContext(RedpktGroupDetailActivity.this.getParentActivity()).setBold().setAlign(Layout.Alignment.ALIGN_CENTER).append(DataTools.format2Decimals(new BigDecimal(String.valueOf(RedpktGroupDetailActivity.this.bean.getMoney())).divide(new BigDecimal(str)).toString())).setForegroundColor(-3416).setProportion(2.0f).append(LocaleController.getString(R.string.UnitCNY)).into(tvReceivedCount);
                tvReceivedPromt.setText(Html.fromHtml(LocaleController.getString(R.string.ViewWalletBalance)));
            } else if (i != 1) {
                BackupImageView ivAvatar = (BackupImageView) viewHolder.itemView.findViewById(R.id.iv_avatar);
                TextView tvName = (TextView) viewHolder.itemView.findViewById(R.id.tv_type_and_from);
                TextView tvCGnum = (TextView) viewHolder.itemView.findViewById(R.id.tv_CG_num);
                TextView tvCollectTime = (TextView) viewHolder.itemView.findViewById(R.id.tv_time);
                TextView tvFeatureBest = (TextView) viewHolder.itemView.findViewById(R.id.tv_state);
                RedpacketDetailRecord redpkgRecord = (RedpacketDetailRecord) RedpktGroupDetailActivity.this.records.get(i - 2);
                ImageView ivFeatureImage = (ImageView) viewHolder.itemView.findViewById(R.id.iv_best);
                TLRPC.User receiver = RedpktGroupDetailActivity.this.getMessagesController().getUser(Integer.valueOf(Integer.parseInt(redpkgRecord.getUserId())));
                if (receiver != null) {
                    AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                    avatarDrawable2.setTextSize(AndroidUtilities.dp(16.0f));
                    avatarDrawable2.setInfo(receiver);
                    ivAvatar.setRoundRadius(AndroidUtilities.dp(7.5f));
                    ivAvatar.getImageReceiver().setCurrentAccount(RedpktGroupDetailActivity.this.currentAccount);
                    ivAvatar.setImage(ImageLocation.getForUser(receiver, false), "50_50", (Drawable) avatarDrawable2, (Object) receiver);
                    tvName.setText(receiver.first_name);
                }
                tvCGnum.setText(DataTools.format2Decimals(new BigDecimal(redpkgRecord.getTotalFee()).divide(new BigDecimal("100")).toString()));
                tvCollectTime.setText(redpkgRecord.getCreateTimeFormat());
                if (!"1".equals(redpkgRecord.getIsOptimum()) || !RedpktGroupDetailActivity.this.isRandom) {
                    tvFeatureBest.setText("");
                    ivFeatureImage.setVisibility(8);
                    return;
                }
                tvFeatureBest.setVisibility(0);
                ivFeatureImage.setVisibility(0);
                tvFeatureBest.setText(LocaleController.getString(R.string.BestOfLuck));
                tvFeatureBest.setTextColor(-85688);
            } else {
                TextView cell = (TextView) holder.itemView.findViewById(R.id.tvRecvStatus);
                cell.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
                cell.setGravity(16);
                BigDecimal total = new BigDecimal(RedpktGroupDetailActivity.this.bean.getTotalFee());
                BigDecimal subtract = total.subtract(new BigDecimal(RedpktGroupDetailActivity.this.bean.getSurplusAmount()));
                int redType2 = Integer.parseInt(RedpktGroupDetailActivity.this.bean.getRedType());
                int status = Integer.parseInt(RedpktGroupDetailActivity.this.bean.getStatus());
                StringBuilder builder = new StringBuilder("");
                if (redType2 == 2) {
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
                        RedpktGroupDetailActivity.this.tvRpkBackDesc.setVisibility(8);
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
                    if (RedpktGroupDetailActivity.this.records == null || RedpktGroupDetailActivity.this.records.size() == 0) {
                        builder.append(RedpktGroupDetailActivity.this.bean.getNumber());
                        builder.append(LocaleController.getString(R.string.redpacket_each));
                        builder.append(LocaleController.getString(R.string.RedPacket));
                        builder.append(LocaleController.getString(R.string.TotalSingleWords));
                        builder.append(DataTools.format2Decimals(total.divide(new BigDecimal("100")).toString()));
                        builder.append(LocaleController.getString(R.string.UnitCNY));
                        builder.append(LocaleController.getString(R.string.Comma));
                        builder.append(LocaleController.getString(R.string.WaitToReceived));
                    } else {
                        builder.append(LocaleController.getString(R.string.AlreadyReceive));
                        builder.append(RedpktGroupDetailActivity.this.bean.getNumber() - RedpktGroupDetailActivity.this.bean.getSurplusNumber());
                        builder.append("/");
                        builder.append(RedpktGroupDetailActivity.this.bean.getNumber());
                        builder.append(LocaleController.getString("Each", R.string.redpacket_each));
                        builder.append(",");
                        builder.append(LocaleController.getString(R.string.TotalSingleWords));
                        builder.append(DataTools.format2Decimals(subtract.divide(new BigDecimal("100")).toString()));
                        builder.append("/");
                        builder.append(DataTools.format2Decimals(total.divide(new BigDecimal("100")).toString()));
                        builder.append(LocaleController.getString(R.string.UnitCNY));
                    }
                } else if (status == 1) {
                    builder.append(RedpktGroupDetailActivity.this.bean.getNumber());
                    builder.append(LocaleController.getString(R.string.redpacket_each));
                    builder.append(LocaleController.getString(R.string.RedPacket));
                    builder.append(LocaleController.getString(R.string.Comma));
                    builder.append(RedpktGroupDetailActivity.this.bean.getDuration());
                    RedpktGroupDetailActivity.this.tvRpkBackDesc.setVisibility(8);
                } else {
                    builder.append(LocaleController.getString(R.string.RedpacketHadExpired));
                    builder.append(LocaleController.getString(R.string.Comma));
                    builder.append(LocaleController.getString(R.string.AlreadyReceive));
                    builder.append(RedpktGroupDetailActivity.this.bean.getNumber() - RedpktGroupDetailActivity.this.bean.getSurplusNumber());
                    builder.append("/");
                    builder.append(RedpktGroupDetailActivity.this.bean.getNumber());
                    builder.append(LocaleController.getString(R.string.Comma));
                    builder.append(LocaleController.getString(R.string.TotalSingleWords));
                    builder.append(DataTools.format2Decimals(subtract.multiply(new BigDecimal("0.01")).toString()));
                    builder.append("/");
                    builder.append(DataTools.format2Decimals(total.multiply(new BigDecimal("0.01")).toString()));
                    builder.append(LocaleController.getString(R.string.UnitCNY));
                }
                cell.setText(builder.toString());
            }
        }

        public int getItemCount() {
            return RedpktGroupDetailActivity.this.rowCount;
        }
    }
}
