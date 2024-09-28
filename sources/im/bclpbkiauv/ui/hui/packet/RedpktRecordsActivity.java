package im.bclpbkiauv.ui.hui.packet;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.SpanUtils;
import im.bclpbkiauv.javaBean.hongbao.UnifyBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.utils.DataTools;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.ParamsUtil;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCRedpacket;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.BottomDialog;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hui.packet.bean.RecordBean;
import im.bclpbkiauv.ui.hui.packet.bean.RecordResponse;
import im.bclpbkiauv.ui.utils.number.StringUtils;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RedpktRecordsActivity extends BaseFragment implements View.OnClickListener {
    private static final int RECEIVER_TYPE = 1;
    private static final int SEND_TYPE = 0;
    /* access modifiers changed from: private */
    public int currentType = -1;
    private MyAdapter mAdapter;
    /* access modifiers changed from: private */
    public Context mContext;
    private View mIndicatorReceive;
    private View mIndicatorSend;
    private BackupImageView mIvAvatar;
    private RecyclerListView mListView;
    private LinearLayout mLlSelectTime;
    private TextView mTvCGNum;
    private TextView mTvLuckyTimes;
    private TextView mTvReceive;
    private TextView mTvRedPacketNum;
    private TextView mTvSend;
    private TextView mTvTimeText;
    private int requestId;
    private int selectedTime;
    private TextView tvMoneyUnit;
    private TLRPC.User user;

    public boolean onFragmentCreate() {
        this.user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_hongbao_records_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(-328966);
        this.fragmentView.setOnTouchListener($$Lambda$RedpktRecordsActivity$BpgZOoiOrVIxlBObfQQgJqbIqHY.INSTANCE);
        initActionBar();
        initView();
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("RedPacketRecords", R.string.RedPacketRecords));
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsColor(-1, false);
        this.actionBar.setBackground(this.gameActionBarBackgroundDrawable);
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back_white);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    RedpktRecordsActivity.this.finishFragment();
                }
            }
        });
    }

    private void initView() {
        this.mTvReceive = (TextView) this.fragmentView.findViewById(R.id.tv_receive);
        this.mTvSend = (TextView) this.fragmentView.findViewById(R.id.tv_send);
        this.mIndicatorReceive = this.fragmentView.findViewById(R.id.indicator_receive);
        this.mIndicatorSend = this.fragmentView.findViewById(R.id.indicator_send);
        this.mIndicatorReceive.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(3.0f), this.mContext.getResources().getColor(R.color.color_red_packet_default)));
        this.mIndicatorSend.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(3.0f), this.mContext.getResources().getColor(R.color.color_red_packet_default)));
        this.mLlSelectTime = (LinearLayout) this.fragmentView.findViewById(R.id.ll_select_time);
        this.mTvTimeText = (TextView) this.fragmentView.findViewById(R.id.tv_time_text);
        this.mIvAvatar = (BackupImageView) this.fragmentView.findViewById(R.id.iv_avatar);
        this.mTvRedPacketNum = (TextView) this.fragmentView.findViewById(R.id.tv_red_packet_num);
        this.mTvCGNum = (TextView) this.fragmentView.findViewById(R.id.tv_CG_num);
        this.mTvLuckyTimes = (TextView) this.fragmentView.findViewById(R.id.tv_lucky_times);
        this.tvMoneyUnit = (TextView) this.fragmentView.findViewById(R.id.tvMoneyUnit);
        this.mTvTimeText.setTextColor(this.mContext.getResources().getColor(R.color.color_red_packet_default));
        this.mTvReceive.setOnClickListener(this);
        this.mTvSend.setOnClickListener(this);
        this.mLlSelectTime.setOnClickListener(this);
        this.tvMoneyUnit.setText(LocaleController.getString(R.string.UnitCNY));
        initListView();
        initAvatar();
        switchUI(1);
    }

    private void initListView() {
        RecyclerListView recyclerListView = (RecyclerListView) this.fragmentView.findViewById(R.id.rv_records);
        this.mListView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(this.mContext));
        MyAdapter myAdapter = new MyAdapter(this.mContext);
        this.mAdapter = myAdapter;
        if (myAdapter.getAdapterStateView() != null) {
            this.mAdapter.getAdapterStateView().getView().setBackgroundColor(-1);
        }
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) $$Lambda$RedpktRecordsActivity$X9WoshgQyuAPKLganwvQ_0miII.INSTANCE);
    }

    static /* synthetic */ void lambda$initListView$1(View view, int position) {
    }

    private void initAvatar() {
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
        avatarDrawable.setInfo(this.user);
        avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
        this.mIvAvatar.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.mIvAvatar.getImageReceiver().setCurrentAccount(this.currentAccount);
        this.mIvAvatar.setImage(ImageLocation.getForUser(this.user, false), "32_32", (Drawable) avatarDrawable, (Object) this.user);
    }

    /* access modifiers changed from: private */
    public void requestRecords(int page) {
        int timeType;
        String startTime;
        AlertDialog dialog = null;
        if (page == 1) {
            dialog = new AlertDialog(this.mContext, 3);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    RedpktRecordsActivity.this.lambda$requestRecords$2$RedpktRecordsActivity(dialogInterface);
                }
            });
            showDialog(dialog);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(2);
        int day = calendar.get(5);
        String endTime = sdf.format(Long.valueOf(calendar.getTimeInMillis()));
        int i = this.selectedTime;
        if (i == 0 || i == 1 || i == 2) {
            int timeType2 = this.selectedTime;
            if (timeType2 == 0) {
                calendar.set(2, month - 5);
            } else if (timeType2 == 1) {
                calendar.set(2, month - 2);
            }
            startTime = new SimpleDateFormat("yyyy-MM").format(Long.valueOf(calendar.getTimeInMillis())) + "-01";
            timeType = 1;
        } else {
            calendar.set(5, day - 7);
            startTime = sdf.format(Long.valueOf(calendar.getTimeInMillis()));
            timeType = 2;
        }
        TLRPCRedpacket.CL_message_rpkTransferHistory req = new TLRPCRedpacket.CL_message_rpkTransferHistory();
        req.flags = 2;
        req.trans = 0;
        req.data = new TLRPC.TL_dataJSON();
        req.data.data = ParamsUtil.toUserIdJson(UnifyBean.BUSINESS_KEY_REDPACKET_RECORDS, new String[]{"type", "startTime", "endTime", "page", "size", "nonceStr", "timeType"}, Integer.valueOf(this.currentType), startTime, endTime, Integer.valueOf(page), 20, StringUtils.getRandomString(20) + getConnectionsManager().getCurrentTime(), Integer.valueOf(timeType));
        ConnectionsManager connectionsManager = getConnectionsManager();
        int sendRequest = getConnectionsManager().sendRequest(req, new RequestDelegate(dialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                RedpktRecordsActivity.this.lambda$requestRecords$4$RedpktRecordsActivity(this.f$1, tLObject, tL_error);
            }
        });
        this.requestId = sendRequest;
        connectionsManager.bindRequestToGuid(sendRequest, this.classGuid);
    }

    public /* synthetic */ void lambda$requestRecords$2$RedpktRecordsActivity(DialogInterface dialog1) {
        if (this.requestId != 0) {
            getConnectionsManager().cancelRequest(this.requestId, true);
        }
    }

    public /* synthetic */ void lambda$requestRecords$4$RedpktRecordsActivity(AlertDialog finalDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(finalDialog, error, response) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                RedpktRecordsActivity.this.lambda$null$3$RedpktRecordsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$RedpktRecordsActivity(AlertDialog finalDialog, TLRPC.TL_error error, TLObject response) {
        if (finalDialog != null) {
            finalDialog.dismiss();
        }
        if (error != null) {
            ToastUtils.show((CharSequence) error.text);
        } else if (response instanceof TLRPC.TL_updates) {
            TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
            if (updates.updates != null && updates.updates.size() > 0) {
                TLRPC.Update update = (TLRPC.Update) updates.updates.get(0);
                if (update instanceof TLRPCRedpacket.CL_updateRpkTransfer) {
                    TLApiModel<RecordResponse> parse = TLJsonResolve.parse((TLObject) ((TLRPCRedpacket.CL_updateRpkTransfer) update).data, (Class<?>) RecordResponse.class);
                    if ("0".equals(parse.code)) {
                        initData((RecordResponse) parse.model);
                    } else {
                        ToastUtils.show((CharSequence) parse.message);
                    }
                }
            }
        }
    }

    private void initData(RecordResponse response) {
        String RedPacketNum = "";
        int i = this.currentType;
        if (i == 0) {
            this.mTvCGNum.setText(DataTools.format2Decimals(new BigDecimal(response.getRedCount().getTotaFee()).divide(new BigDecimal("100")).toString()));
            this.mTvLuckyTimes.setVisibility(8);
            RedPacketNum = String.format(LocaleController.getString("TotalSentFormat", R.string.TotalSentFormat), new Object[]{UserObject.getName(this.user), Integer.valueOf(response.getRedCount().getRedCont())});
        } else if (i == 1) {
            this.mTvCGNum.setText(DataTools.format2Decimals(new BigDecimal(response.getRedCount().getTotaFee()).divide(new BigDecimal("100")).toString()));
            this.mTvLuckyTimes.setText(Html.fromHtml(String.format(LocaleController.getString("LuckyTimes", R.string.LuckyTimes), new Object[]{response.getRedCount().getIsOptimumCount()})));
            this.mTvLuckyTimes.setVisibility(0);
            RedPacketNum = String.format(LocaleController.getString("TotalReceivedFormat", R.string.TotalReceivedFormat), new Object[]{UserObject.getName(this.user), Integer.valueOf(response.getRedCount().getRedCont())});
        }
        this.mTvRedPacketNum.setText(Html.fromHtml(RedPacketNum));
        this.mAdapter.addData(response.getRecord());
    }

    private void switchUI(int state) {
        if (this.currentType != state) {
            this.currentType = state;
            if (state == 0) {
                this.mTvSend.setTextColor(this.mContext.getResources().getColor(R.color.color_red_packet_default));
                this.mTvReceive.setTextColor(-16777216);
                this.mTvSend.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.mTvReceive.setTypeface(Typeface.DEFAULT);
                this.mIndicatorSend.setVisibility(0);
                this.mIndicatorReceive.setVisibility(8);
            } else if (state == 1) {
                this.mTvReceive.setTextColor(this.mContext.getResources().getColor(R.color.color_red_packet_default));
                this.mTvSend.setTextColor(-16777216);
                this.mTvReceive.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.mTvSend.setTypeface(Typeface.DEFAULT);
                this.mIndicatorReceive.setVisibility(0);
                this.mIndicatorSend.setVisibility(8);
            }
            this.mAdapter.reLoadData();
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_select_time) {
            BottomDialog dialog = new BottomDialog(this.mContext);
            dialog.addDialogItem(new BottomDialog.NormalTextItem(0, LocaleController.getString(R.string.TimeWithinHalfAYear), true));
            dialog.addDialogItem(new BottomDialog.NormalTextItem(1, LocaleController.getString(R.string.TimeWithinThreeMonths), true));
            dialog.addDialogItem(new BottomDialog.NormalTextItem(2, LocaleController.getString(R.string.TimeWithinOneMonths), true));
            dialog.addDialogItem(new BottomDialog.NormalTextItem(3, LocaleController.getString(R.string.TimeWithinSevenDays), false));
            dialog.setOnItemClickListener(new BottomDialog.OnItemClickListener(dialog) {
                private final /* synthetic */ BottomDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(int i, View view) {
                    RedpktRecordsActivity.this.lambda$onClick$5$RedpktRecordsActivity(this.f$1, i, view);
                }
            });
            showDialog(dialog);
        } else if (id == R.id.tv_receive) {
            switchUI(1);
        } else if (id == R.id.tv_send) {
            switchUI(0);
        }
    }

    public /* synthetic */ void lambda$onClick$5$RedpktRecordsActivity(BottomDialog dialog, int id, View v1) {
        this.mTvTimeText.setText(((BottomDialog.NormalTextItem) v1).getContent());
        this.selectedTime = id;
        this.mAdapter.reLoadData();
        dialog.dismiss();
    }

    public void onFragmentDestroy() {
        MyAdapter myAdapter = this.mAdapter;
        if (myAdapter != null) {
            myAdapter.destroy();
            this.mAdapter = null;
        }
        super.onFragmentDestroy();
    }

    private class MyAdapter extends PageSelectionAdapter<RecordBean, ViewHolder> {
        MyAdapter(Context context) {
            super(context);
        }

        public ViewHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(RedpktRecordsActivity.this.mContext).inflate(R.layout.item_red_packet_records, parent, false);
            view.setBackgroundColor(-1);
            return new ViewHolder(view);
        }

        public void onBindViewHolderForChild(ViewHolder holder, int position, RecordBean item) {
            int redType = item.getRedType();
            int grantType = item.getGrantType();
            int status = item.getStatus();
            StringBuilder builder = new StringBuilder();
            int access$100 = RedpktRecordsActivity.this.currentType;
            if (access$100 == 0) {
                holder.ivAvatar.setVisibility(8);
                holder.ivAvatarDefault.setVisibility(0);
                if (redType == 0) {
                    holder.ivAvatarDefault.setImageResource(R.mipmap.icon_rpk_persernal);
                    holder.ivAvatarDefault.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(45.0f), -350121));
                } else if (redType != 1) {
                    holder.ivAvatarDefault.setImageResource(R.mipmap.icon_rpk_group_personal);
                    holder.ivAvatarDefault.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(45.0f), -8274));
                } else if (grantType == 0) {
                    holder.ivAvatarDefault.setImageResource(R.mipmap.icon_rpk_normal);
                    holder.ivAvatarDefault.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(45.0f), -2737326));
                } else {
                    holder.ivAvatarDefault.setImageResource(R.mipmap.icon_rpk_random);
                    holder.ivAvatarDefault.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(45.0f), -2737326));
                }
                if (redType == 0) {
                    builder.append(LocaleController.getString(R.string.redpacket_group_person));
                } else if (redType != 1) {
                    builder.append(LocaleController.getString(R.string.redpacket_group_exclusive));
                } else if (grantType == 0) {
                    builder.append(LocaleController.getString(R.string.redpacket_group_common));
                } else {
                    builder.append(LocaleController.getString(R.string.redpacket_group_random));
                }
                holder.tvTypeAndFrom.setText(builder.toString());
                holder.tvCGNum.setText(DataTools.format2Decimals(new BigDecimal(item.getTotalFee()).divide(new BigDecimal("100")).toString()));
                if (status == 1) {
                    holder.tvState.setText(LocaleController.getString(R.string.redpacket_all_already_received));
                } else if (status == 2) {
                    SpanUtils.with(holder.tvState).append(LocaleController.getString(R.string.ReturnSpace)).setForegroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3)).append(DataTools.format2Decimals(new BigDecimal(item.getSurplusAmount()).divide(new BigDecimal("100")).toString())).setForegroundColor(-2737326).append(" ").setForegroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3)).create();
                } else {
                    holder.tvState.setText(LocaleController.getString(R.string.Unclaimed));
                }
                holder.tvTime.setText(item.getCreateTime());
            } else if (access$100 == 1) {
                holder.ivAvatar.setVisibility(0);
                holder.ivAvatarDefault.setVisibility(8);
                TLRPC.User sender = RedpktRecordsActivity.this.getMessagesController().getUser(Integer.valueOf(Integer.parseInt(item.getInitiatorUserId())));
                if (sender != null) {
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
                    avatarDrawable.setInfo(sender);
                    holder.ivAvatar.setRoundRadius(AndroidUtilities.dp(7.0f));
                    holder.ivAvatar.getImageReceiver().setCurrentAccount(RedpktRecordsActivity.this.currentAccount);
                    holder.ivAvatar.setImage(ImageLocation.getForUser(sender, false), "50_50", (Drawable) avatarDrawable, (Object) sender);
                }
                if (redType == 0) {
                    builder.append(LocaleController.getString(R.string.RedPackagePersonaleFrom));
                } else if (redType != 1) {
                    builder.append(LocaleController.getString(R.string.RedPackageDesignatedFrom));
                } else if (grantType == 0) {
                    builder.append(LocaleController.getString(R.string.RedPackageNormalFrom));
                } else {
                    builder.append(LocaleController.getString(R.string.RedPackageLuckyFrom));
                }
                if (sender != null) {
                    builder.append(UserObject.getName(sender));
                } else {
                    builder.append(LocaleController.getString(R.string.UnKnown));
                }
                holder.tvTypeAndFrom.setText(builder.toString());
                holder.tvCGNum.setText(DataTools.format2Decimals(new BigDecimal(item.getReceiveTotalFee()).multiply(new BigDecimal("0.01")).toString()));
                holder.tvState.setText(LocaleController.getString("AlreadyReceived", R.string.AlreadyReceive));
                holder.tvTime.setText(item.getCreateTime());
            }
        }

        public void loadData(int page) {
            super.loadData(page);
            RedpktRecordsActivity.this.requestRecords(page);
        }
    }

    private static class ViewHolder extends PageHolder {
        BackupImageView ivAvatar;
        ImageView ivAvatarDefault;
        ImageView ivBest;
        TextView tvCGNum;
        TextView tvState;
        TextView tvTime;
        TextView tvTypeAndFrom;

        ViewHolder(View itemView) {
            super(itemView, 0);
            this.ivAvatar = (BackupImageView) itemView.findViewById(R.id.iv_avatar);
            this.ivAvatarDefault = (ImageView) itemView.findViewById(R.id.iv_default_avatar);
            this.tvTypeAndFrom = (TextView) itemView.findViewById(R.id.tv_type_and_from);
            this.tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            this.tvCGNum = (TextView) itemView.findViewById(R.id.tv_CG_num);
            this.tvState = (TextView) itemView.findViewById(R.id.tv_state);
            this.ivBest = (ImageView) itemView.findViewById(R.id.iv_best);
        }
    }
}
