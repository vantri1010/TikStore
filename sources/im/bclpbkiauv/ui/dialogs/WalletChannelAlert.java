package im.bclpbkiauv.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ColorUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.wallet.cell.BankCardSelectCell;
import im.bclpbkiauv.ui.wallet.model.PayChannelBean;
import java.util.ArrayList;

public class WalletChannelAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    public static final int TYPE_TOP_UP = 1;
    public static final int TYPE_WITHDRAW = 0;
    private ListAdapter adapter;
    /* access modifiers changed from: private */
    public ChannelAlertDelegate delegate;
    private LinearLayout emptyLayout;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ImageView mIvBack;
    /* access modifiers changed from: private */
    public ArrayList<PayChannelBean> modelList;
    BaseFragment parentFragment;
    PayChannelBean selectedCard;
    private TextView tvTitle;
    int type;

    public interface ChannelAlertDelegate {
        void onSelected(PayChannelBean payChannelBean);
    }

    public WalletChannelAlert(Context context) {
        super(context, false, 1);
        init(context);
    }

    public WalletChannelAlert(Context context, BaseFragment baseFragment, ArrayList<PayChannelBean> list, PayChannelBean selectedCard2, int type2, ChannelAlertDelegate bankCardAlertDelegate) {
        super(context, false, 1);
        this.parentFragment = baseFragment;
        this.delegate = bankCardAlertDelegate;
        this.modelList = list;
        this.selectedCard = selectedCard2;
        this.type = type2;
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.wallet_channels_alert_layout, (ViewGroup) null);
        setCustomView(view);
        setCancelable(false);
        initView(context, view);
    }

    private void initView(Context context, View view) {
        setBackgroundColor(ColorUtils.getColor(R.color.window_background_gray));
        this.mIvBack = (ImageView) view.findViewById(R.id.iv_back);
        this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
        this.emptyLayout = (LinearLayout) view.findViewById(R.id.emptyLayout);
        this.listView = (RecyclerListView) view.findViewById(R.id.listView);
        int i = this.type;
        if (i == 0) {
            this.tvTitle.setText(LocaleController.getString(R.string.WithdrawalChannel));
        } else if (i == 1) {
            this.tvTitle.setText(LocaleController.getString(R.string.TopUpChannel));
        } else {
            this.tvTitle.setText(LocaleController.getString(R.string.SelectPayWayTitle));
        }
        this.layoutManager = new LinearLayoutManager(getContext());
        this.adapter = new ListAdapter(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setTag(13);
        this.listView.setClipToPadding(false);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.listView.setEmptyView(this.emptyLayout);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (WalletChannelAlert.this.modelList != null && WalletChannelAlert.this.delegate != null) {
                    WalletChannelAlert.this.dismiss();
                    WalletChannelAlert.this.delegate.onSelected((PayChannelBean) WalletChannelAlert.this.modelList.get(position));
                }
            }
        });
        this.mIvBack.setBackground(Theme.createSelectorDrawable(ColorUtils.getColor(R.color.click_selector)));
        this.mIvBack.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletChannelAlert.this.lambda$initView$0$WalletChannelAlert(view);
            }
        });
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$initView$0$WalletChannelAlert(View v) {
        dismiss();
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void dismiss() {
        super.dismiss();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private int totalItems;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new BankCardSelectCell(this.mContext);
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(70.0f)));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            BankCardSelectCell cell = (BankCardSelectCell) holder.itemView;
            PayChannelBean bean = (PayChannelBean) WalletChannelAlert.this.modelList.get(position);
            boolean checked = false;
            if (WalletChannelAlert.this.selectedCard != null) {
                checked = bean.getPayType().getPayType().equals(WalletChannelAlert.this.selectedCard.getPayType().getPayType());
            }
            cell.setText(bean.getPayType().getName(), checked);
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public void notifyDataSetChanged() {
            this.totalItems = WalletChannelAlert.this.modelList != null ? WalletChannelAlert.this.modelList.size() : 0;
            super.notifyDataSetChanged();
        }
    }
}
