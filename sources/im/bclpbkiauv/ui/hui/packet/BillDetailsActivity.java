package im.bclpbkiauv.ui.hui.packet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import im.bclpbkiauv.javaBean.PayBillOverBean;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPCRedpacket;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryImageView;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class BillDetailsActivity extends BaseFragment {
    private PayBillOverBean bean;
    @BindView(2131296749)
    ImageView ivIcon;
    @BindView(2131296750)
    ImageView ivIcon2;
    @BindView(2131296765)
    MryImageView ivRow6Copy;
    @BindView(2131296766)
    MryImageView ivRowCopy1;
    @BindView(2131296767)
    MryImageView ivRowCopy2;
    @BindView(2131296768)
    MryImageView ivRowCopy3;
    @BindView(2131296904)
    LinearLayout llContainer;
    @BindView(2131296905)
    LinearLayout llCurrencyContainer;
    @BindView(2131296906)
    LinearLayout llCurrencyInfo1;
    @BindView(2131296909)
    LinearLayout llFiatContainer;
    @BindView(2131296910)
    LinearLayout llFiatInfo1;
    @BindView(2131296911)
    LinearLayout llIconView;
    private Context mContext;
    private MessageObject mMessageObject;
    @BindView(2131297105)
    RelativeLayout rlCurrencyRow1;
    @BindView(2131297108)
    RelativeLayout rlCurrencyRow2;
    @BindView(2131297110)
    RelativeLayout rlCurrencyRow4;
    @BindView(2131297111)
    RelativeLayout rlCurrencyRow5;
    @BindView(2131297133)
    RelativeLayout rlRow6;
    @BindView(2131297135)
    RelativeLayout rlRow8;
    private Toast toast;
    @BindView(2131297442)
    TextView tvAmount;
    @BindView(2131297443)
    TextView tvAmount2;
    @BindView(2131297495)
    MryTextView tvFiatRow10Info;
    @BindView(2131297496)
    TextView tvFiatRow10Name;
    @BindView(2131297497)
    MryTextView tvFiatRow1Info;
    @BindView(2131297498)
    TextView tvFiatRow1Name;
    @BindView(2131297499)
    MryTextView tvFiatRow2Info;
    @BindView(2131297500)
    TextView tvFiatRow2Name;
    @BindView(2131297501)
    MryTextView tvFiatRow3Info;
    @BindView(2131297502)
    TextView tvFiatRow3Name;
    @BindView(2131297503)
    MryTextView tvFiatRow4Info;
    @BindView(2131297504)
    TextView tvFiatRow4Name;
    @BindView(2131297505)
    MryTextView tvFiatRow5Info;
    @BindView(2131297506)
    TextView tvFiatRow5Name;
    @BindView(2131297507)
    MryTextView tvFiatRow6Info;
    @BindView(2131297508)
    TextView tvFiatRow6Name;
    @BindView(2131297509)
    MryTextView tvFiatRow7Info;
    @BindView(2131297510)
    TextView tvFiatRow7Name;
    @BindView(2131297511)
    MryTextView tvFiatRow8Info;
    @BindView(2131297512)
    TextView tvFiatRow8Name;
    @BindView(2131297513)
    MryTextView tvFiatRow9Info;
    @BindView(2131297514)
    TextView tvFiatRow9Name;
    @BindView(2131297586)
    MryTextView tvRow10Info;
    @BindView(2131297587)
    TextView tvRow10Name;
    @BindView(2131297588)
    MryTextView tvRow11Info;
    @BindView(2131297589)
    TextView tvRow11Name;
    @BindView(2131297590)
    MryTextView tvRow1Info;
    @BindView(2131297591)
    TextView tvRow1Name;
    @BindView(2131297592)
    MryTextView tvRow2Info;
    @BindView(2131297593)
    TextView tvRow2Name;
    @BindView(2131297594)
    MryTextView tvRow3Info;
    @BindView(2131297595)
    TextView tvRow3Name;
    @BindView(2131297596)
    MryTextView tvRow4Info;
    @BindView(2131297597)
    TextView tvRow4Name;
    @BindView(2131297598)
    MryTextView tvRow5Info;
    @BindView(2131297599)
    TextView tvRow5Name;
    @BindView(2131297600)
    MryTextView tvRow6Info;
    @BindView(2131297601)
    TextView tvRow6Name;
    @BindView(2131297602)
    MryTextView tvRow7Info;
    @BindView(2131297603)
    TextView tvRow7Name;
    @BindView(2131297604)
    MryTextView tvRow8Info;
    @BindView(2131297605)
    TextView tvRow8Name;
    @BindView(2131297606)
    MryTextView tvRow9Info;
    @BindView(2131297607)
    TextView tvRow9Name;
    @BindView(2131297608)
    MryTextView tvRowAddress1;
    @BindView(2131297609)
    MryTextView tvRowAddress2;
    @BindView(2131297610)
    MryTextView tvRowAddress3;
    @BindView(2131297611)
    MryTextView tvRowAddress4;
    @BindView(2131297612)
    MryTextView tvRowAddress5;
    @BindView(2131297613)
    MryTextView tvRowAddress6;
    @BindView(2131297614)
    MryTextView tvRowAddress7;
    @BindView(2131297615)
    MryTextView tvRowAddress8;
    @BindView(2131297616)
    MryTextView tvRowName1;
    @BindView(2131297617)
    MryTextView tvRowName2;
    @BindView(2131297618)
    MryTextView tvRowName3;
    @BindView(2131297619)
    MryTextView tvRowName4;
    @BindView(2131297620)
    MryTextView tvRowName5;
    @BindView(2131297621)
    MryTextView tvRowName6;
    @BindView(2131297622)
    MryTextView tvRowName7;
    @BindView(2131297623)
    MryTextView tvRowName8;

    public BillDetailsActivity(MessageObject message) {
        this.mMessageObject = message;
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_bill_details_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        initActionBar();
        initViews();
        return this.fragmentView;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x066f  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x0682  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0702  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0715  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x09d8  */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x09eb  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0af7  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0b3e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initViews() {
        /*
            r20 = this;
            r0 = r20
            android.widget.LinearLayout r1 = r0.llContainer
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
            r1.setBackgroundColor(r2)
            android.widget.LinearLayout r1 = r0.llCurrencyInfo1
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
            r1.setBackgroundColor(r2)
            android.widget.LinearLayout r1 = r0.llFiatInfo1
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
            r1.setBackgroundColor(r2)
            im.bclpbkiauv.messenger.MessageObject r1 = r0.mMessageObject
            int r1 = r1.type
            r2 = 104(0x68, float:1.46E-43)
            if (r1 != r2) goto L_0x0e0a
            im.bclpbkiauv.messenger.MessageObject r1 = r0.mMessageObject
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            im.bclpbkiauv.tgnet.TLRPCRedpacket$CL_messagesPayBillOverMedia r1 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesPayBillOverMedia) r1
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r2 = r1.data
            java.lang.String r2 = im.bclpbkiauv.tgnet.TLJsonResolve.getData(r2)
            java.lang.Class<im.bclpbkiauv.javaBean.PayBillOverBean> r3 = im.bclpbkiauv.javaBean.PayBillOverBean.class
            java.lang.Object r3 = com.blankj.utilcode.util.GsonUtils.fromJson((java.lang.String) r2, r3)
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = (im.bclpbkiauv.javaBean.PayBillOverBean) r3
            r0.bean = r3
            java.lang.String r4 = ""
            java.lang.String r5 = ""
            java.lang.String r3 = r3.coin_code
            java.lang.String r6 = "-"
            boolean r3 = r3.contains(r6)
            r7 = 1
            r8 = 0
            if (r3 == 0) goto L_0x0060
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.coin_code
            java.lang.String[] r3 = r3.split(r6)
            r4 = r3[r8]
            r5 = r3[r7]
            goto L_0x0064
        L_0x0060:
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r4 = r3.coin_code
        L_0x0064:
            int r3 = r4.hashCode()
            java.lang.String r9 = "USDT"
            java.lang.String r10 = "LTC"
            java.lang.String r11 = "ETH"
            java.lang.String r12 = "BTC"
            r14 = 3
            r15 = 2
            switch(r3) {
                case 66097: goto L_0x008e;
                case 68985: goto L_0x0086;
                case 75707: goto L_0x007e;
                case 2614190: goto L_0x0076;
                default: goto L_0x0075;
            }
        L_0x0075:
            goto L_0x0096
        L_0x0076:
            boolean r3 = r4.equals(r9)
            if (r3 == 0) goto L_0x0075
            r3 = 2
            goto L_0x0097
        L_0x007e:
            boolean r3 = r4.equals(r10)
            if (r3 == 0) goto L_0x0075
            r3 = 3
            goto L_0x0097
        L_0x0086:
            boolean r3 = r4.equals(r11)
            if (r3 == 0) goto L_0x0075
            r3 = 1
            goto L_0x0097
        L_0x008e:
            boolean r3 = r4.equals(r12)
            if (r3 == 0) goto L_0x0075
            r3 = 0
            goto L_0x0097
        L_0x0096:
            r3 = -1
        L_0x0097:
            r13 = 2131558709(0x7f0d0135, float:1.8742741E38)
            r8 = 2131558703(0x7f0d012f, float:1.874273E38)
            if (r3 == 0) goto L_0x00be
            if (r3 == r7) goto L_0x00b5
            if (r3 == r15) goto L_0x00ac
            if (r3 == r14) goto L_0x00a6
            goto L_0x00c4
        L_0x00a6:
            android.widget.ImageView r3 = r0.ivIcon
            r3.setImageResource(r13)
            goto L_0x00c4
        L_0x00ac:
            android.widget.ImageView r3 = r0.ivIcon
            r13 = 2131558711(0x7f0d0137, float:1.8742746E38)
            r3.setImageResource(r13)
            goto L_0x00c4
        L_0x00b5:
            android.widget.ImageView r3 = r0.ivIcon
            r13 = 2131558707(0x7f0d0133, float:1.8742737E38)
            r3.setImageResource(r13)
            goto L_0x00c4
        L_0x00be:
            android.widget.ImageView r3 = r0.ivIcon
            r3.setImageResource(r8)
        L_0x00c4:
            int r3 = r5.hashCode()
            switch(r3) {
                case 66097: goto L_0x00e4;
                case 68985: goto L_0x00dc;
                case 75707: goto L_0x00d4;
                case 2614190: goto L_0x00cc;
                default: goto L_0x00cb;
            }
        L_0x00cb:
            goto L_0x00ec
        L_0x00cc:
            boolean r3 = r5.equals(r9)
            if (r3 == 0) goto L_0x00cb
            r3 = 2
            goto L_0x00ed
        L_0x00d4:
            boolean r3 = r5.equals(r10)
            if (r3 == 0) goto L_0x00cb
            r3 = 3
            goto L_0x00ed
        L_0x00dc:
            boolean r3 = r5.equals(r11)
            if (r3 == 0) goto L_0x00cb
            r3 = 1
            goto L_0x00ed
        L_0x00e4:
            boolean r3 = r5.equals(r12)
            if (r3 == 0) goto L_0x00cb
            r3 = 0
            goto L_0x00ed
        L_0x00ec:
            r3 = -1
        L_0x00ed:
            if (r3 == 0) goto L_0x0111
            if (r3 == r7) goto L_0x0108
            if (r3 == r15) goto L_0x00ff
            if (r3 == r14) goto L_0x00f6
            goto L_0x0117
        L_0x00f6:
            android.widget.ImageView r3 = r0.ivIcon2
            r13 = 2131558709(0x7f0d0135, float:1.8742741E38)
            r3.setImageResource(r13)
            goto L_0x0117
        L_0x00ff:
            android.widget.ImageView r3 = r0.ivIcon2
            r13 = 2131558711(0x7f0d0137, float:1.8742746E38)
            r3.setImageResource(r13)
            goto L_0x0117
        L_0x0108:
            android.widget.ImageView r3 = r0.ivIcon2
            r13 = 2131558707(0x7f0d0133, float:1.8742737E38)
            r3.setImageResource(r13)
            goto L_0x0117
        L_0x0111:
            android.widget.ImageView r3 = r0.ivIcon2
            r3.setImageResource(r8)
        L_0x0117:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName1
            r13 = 2131692873(0x7f0f0d49, float:1.9014858E38)
            java.lang.String r8 = "PayBillTargetAddress"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r13)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress1
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_with
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            java.lang.String r13 = ""
            if (r8 == 0) goto L_0x0135
            r8 = r13
            goto L_0x0139
        L_0x0135:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_with
        L_0x0139:
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName2
            java.lang.String r8 = "TxID"
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress2
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_txId
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0151
            r8 = r13
            goto L_0x0155
        L_0x0151:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_txId
        L_0x0155:
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName3
            r8 = 2131692849(0x7f0f0d31, float:1.901481E38)
            java.lang.String r14 = "PayBillOrderNumber"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress3
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.order_id
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0174
            r8 = r13
            goto L_0x0178
        L_0x0174:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.order_id
        L_0x0178:
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName4
            r8 = 2131692874(0x7f0f0d4a, float:1.901486E38)
            java.lang.String r14 = "PayBillTargetType"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress4
            r8 = 2131692889(0x7f0f0d59, float:1.901489E38)
            java.lang.String r14 = "PayBillWithdraw"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName5
            r8 = 2131692826(0x7f0f0d1a, float:1.9014763E38)
            java.lang.String r14 = "PayBillCurrencyType"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress5
            r3.setText(r4)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName6
            r8 = 2131692843(0x7f0f0d2b, float:1.9014798E38)
            java.lang.String r14 = "PayBillHandlingFee"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress6
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r14 = "₫"
            r8.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r14 = r0.bean
            java.lang.String r14 = r14.deal_fees
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            java.lang.String r16 = "0"
            if (r14 == 0) goto L_0x01d3
            r14 = r16
            goto L_0x01d7
        L_0x01d3:
            im.bclpbkiauv.javaBean.PayBillOverBean r14 = r0.bean
            java.lang.String r14 = r14.deal_fees
        L_0x01d7:
            java.lang.String r14 = r0.setMoneyFormat(r14)
            r8.append(r14)
            java.lang.String r8 = r8.toString()
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName7
            r8 = 2131692883(0x7f0f0d53, float:1.9014879E38)
            java.lang.String r14 = "PayBillTransferTime"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress7
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.getCreate24HEndTimeFormat()
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName8
            r8 = 2131692872(0x7f0f0d48, float:1.9014856E38)
            java.lang.String r14 = "PayBillSummary2"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress8
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x021a
            r8 = r13
            goto L_0x021e
        L_0x021a:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_message
        L_0x021e:
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvRow1Name
            r8 = 2131692834(0x7f0f0d22, float:1.901478E38)
            java.lang.String r14 = "PayBillEntryCurrency"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow1Info
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r14 = "+"
            r8.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r15 = r0.bean
            java.lang.String r15 = r15.deal_num
            boolean r15 = android.text.TextUtils.isEmpty(r15)
            if (r15 == 0) goto L_0x0248
            r15 = r16
            goto L_0x024c
        L_0x0248:
            im.bclpbkiauv.javaBean.PayBillOverBean r15 = r0.bean
            java.lang.String r15 = r15.deal_num
        L_0x024c:
            java.lang.String r15 = r0.setMoneyFormat(r15)
            r8.append(r15)
            java.lang.String r15 = " ("
            r8.append(r15)
            r8.append(r4)
            java.lang.String r7 = ")"
            r8.append(r7)
            java.lang.String r8 = r8.toString()
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvRow2Name
            r8 = 2131692852(0x7f0f0d34, float:1.9014816E38)
            r17 = r2
            java.lang.String r2 = "PayBillPaymentCurrency"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r8)
            r3.setText(r2)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvRow2Info
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_amount
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x028e
            r8 = r16
            goto L_0x0292
        L_0x028e:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_amount
        L_0x0292:
            java.lang.String r8 = r0.setMoneyFormat(r8)
            r3.append(r8)
            r3.append(r15)
            r3.append(r5)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvRow3Name
            r3 = 2131692879(0x7f0f0d4f, float:1.901487E38)
            java.lang.String r8 = "PayBillTransactionType"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvRow4Name
            r3 = 2131692810(0x7f0f0d0a, float:1.901473E38)
            java.lang.String r8 = "PayBillAverageBuyingPrice"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvRow4Info
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r8 = "1 "
            r3.append(r8)
            r3.append(r4)
            java.lang.String r8 = " = "
            r3.append(r8)
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.buy_avg
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x02e6
            r8 = r16
            goto L_0x02ea
        L_0x02e6:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.buy_avg
        L_0x02ea:
            r3.append(r8)
            java.lang.String r8 = " "
            r3.append(r8)
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvRow5Name
            r3 = 2131692843(0x7f0f0d2b, float:1.9014798E38)
            java.lang.String r8 = "PayBillHandlingFee"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvRow6Name
            r3 = 2131692849(0x7f0f0d31, float:1.901481E38)
            java.lang.String r8 = "PayBillOrderNumber"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvRow6Info
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.order_id
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0326
            r3 = r13
            goto L_0x032a
        L_0x0326:
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.order_id
        L_0x032a:
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvRow7Name
            r3 = 2131692823(0x7f0f0d17, float:1.9014757E38)
            java.lang.String r8 = "PayBillCommissionTime"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvRow7Info
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.getCreate24HDealTimeFormat()
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvRow8Name
            r3 = 2131692878(0x7f0f0d4e, float:1.9014869E38)
            java.lang.String r8 = "PayBillTransactionTime"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvRow8Info
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.getCreate24HEndTimeFormat()
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvRow9Name
            r3 = 2131692816(0x7f0f0d10, float:1.9014743E38)
            java.lang.String r8 = "PayBillCommission"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvRow9Info
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.mandate_num
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0380
            r8 = r13
            goto L_0x0384
        L_0x0380:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.mandate_num
        L_0x0384:
            r3.append(r8)
            r3.append(r15)
            r3.append(r4)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvRow10Name
            r3 = 2131692888(0x7f0f0d58, float:1.9014889E38)
            java.lang.String r8 = "PayBillVolume"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvRow10Info
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_num
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x03b8
            r8 = r13
            goto L_0x03bc
        L_0x03b8:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_num
        L_0x03bc:
            r3.append(r8)
            r3.append(r15)
            r3.append(r4)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvRow11Name
            r3 = 2131692872(0x7f0f0d48, float:1.9014856E38)
            java.lang.String r8 = "PayBillSummary2"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvRow11Info
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x03eb
            r3 = r13
            goto L_0x03ef
        L_0x03eb:
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_message
        L_0x03ef:
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvFiatRow1Name
            r3 = 2131692851(0x7f0f0d33, float:1.9014814E38)
            java.lang.String r8 = "PayBillPaymentAmount"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvFiatRow1Info
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_amount
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0414
            r8 = r16
            goto L_0x0418
        L_0x0414:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_amount
        L_0x0418:
            java.lang.String r8 = r0.setMoneyFormat(r8)
            r3.append(r8)
            r3.append(r15)
            r3.append(r5)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvFiatRow2Name
            r3 = 2131692814(0x7f0f0d0e, float:1.9014739E38)
            java.lang.String r8 = "PayBillBuyPrice2"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            im.bclpbkiauv.ui.hviews.MryTextView r2 = r0.tvFiatRow2Info
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.buy_avg
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0451
            r8 = r16
            goto L_0x0455
        L_0x0451:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.buy_avg
        L_0x0455:
            java.lang.String r8 = r0.setMoneyFormat(r8)
            r3.append(r8)
            r3.append(r15)
            r3.append(r5)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r2.setText(r3)
            android.widget.TextView r2 = r0.tvFiatRow3Name
            r3 = 2131692877(0x7f0f0d4d, float:1.9014867E38)
            java.lang.String r8 = "PayBillTransactionParty"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r2.setText(r3)
            java.lang.String r2 = ""
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_from
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0492
            r3 = 2131692875(0x7f0f0d4b, float:1.9014862E38)
            java.lang.String r8 = "PayBillTheStrongestCurrencyMerchant"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r19 = r13
            goto L_0x04c7
        L_0x0492:
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_from
            int r3 = r3.length()
            r8 = 13
            if (r3 <= r8) goto L_0x04bf
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_from
            r18 = r2
            r2 = 12
            r19 = r13
            r13 = 0
            java.lang.String r2 = r8.substring(r13, r2)
            r3.append(r2)
            java.lang.String r2 = "..."
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            goto L_0x04c7
        L_0x04bf:
            r18 = r2
            r19 = r13
            im.bclpbkiauv.javaBean.PayBillOverBean r2 = r0.bean
            java.lang.String r2 = r2.deal_from
        L_0x04c7:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvFiatRow3Info
            r3.setText(r2)
            android.widget.TextView r3 = r0.tvFiatRow4Name
            r8 = 2131692843(0x7f0f0d2b, float:1.9014798E38)
            java.lang.String r13 = "PayBillHandlingFee"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvFiatRow4Info
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_fees
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x04e9
            r8 = r16
            goto L_0x04ed
        L_0x04e9:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_fees
        L_0x04ed:
            java.lang.String r8 = r0.setMoneyFormat(r8)
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvFiatRow5Name
            r8 = 2131692849(0x7f0f0d31, float:1.901481E38)
            java.lang.String r13 = "PayBillOrderNumber"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvFiatRow5Info
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.order_id
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0511
            r8 = r16
            goto L_0x0515
        L_0x0511:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.order_id
        L_0x0515:
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvFiatRow6Name
            r8 = 2131692850(0x7f0f0d32, float:1.9014812E38)
            java.lang.String r13 = "PayBillOrderTime"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvFiatRow6Info
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.getCreate24HDealTimeFormat()
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvFiatRow7Name
            r8 = 2131692878(0x7f0f0d4e, float:1.9014869E38)
            java.lang.String r13 = "PayBillTransactionTime"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvFiatRow7Info
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.getCreate24HEndTimeFormat()
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvFiatRow8Name
            r8 = 2131692853(0x7f0f0d35, float:1.9014818E38)
            java.lang.String r13 = "PayBillPaymentTime"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvFiatRow8Info
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.payment_time
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0567
            r8 = r16
            goto L_0x056b
        L_0x0567:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.payment_time
        L_0x056b:
            java.lang.String r8 = r0.getDate(r8)
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvFiatRow9Name
            r8 = 2131692856(0x7f0f0d38, float:1.9014824E38)
            java.lang.String r13 = "PayBillReleaseTime"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvFiatRow9Info
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.pass_time
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x058f
            r8 = r16
            goto L_0x0593
        L_0x058f:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.pass_time
        L_0x0593:
            java.lang.String r8 = r0.getDate(r8)
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvFiatRow10Name
            r8 = 2131692871(0x7f0f0d47, float:1.9014854E38)
            java.lang.String r13 = "PayBillSummary"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r8)
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvFiatRow10Info
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x05b7
            r8 = r16
            goto L_0x05bb
        L_0x05b7:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_message
        L_0x05bb:
            r3.setText(r8)
            int r3 = r1.deal_code
            java.lang.String r13 = "("
            r8 = 8
            switch(r3) {
                case 1: goto L_0x0dc5;
                case 2: goto L_0x0d31;
                case 3: goto L_0x0dc5;
                case 4: goto L_0x0dc5;
                case 5: goto L_0x0c9c;
                case 6: goto L_0x0dc5;
                case 7: goto L_0x0d31;
                case 8: goto L_0x0a73;
                case 9: goto L_0x083e;
                case 10: goto L_0x073a;
                case 11: goto L_0x06a7;
                case 12: goto L_0x05c9;
                default: goto L_0x05c7;
            }
        L_0x05c7:
            goto L_0x0e0a
        L_0x05c9:
            android.widget.TextView r3 = r0.tvAmount
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_num
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x05e0
            r6 = r16
            goto L_0x05e4
        L_0x05e0:
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_num
        L_0x05e4:
            java.lang.String r6 = r0.setMoneyFormat(r6)
            r9.append(r6)
            java.lang.String r6 = r9.toString()
            r3.setText(r6)
            android.widget.TextView r3 = r0.tvFiatRow1Name
            r6 = 2131692808(0x7f0f0d08, float:1.9014727E38)
            java.lang.String r9 = "PayBillAmountReceived"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r6)
            r3.setText(r6)
            android.widget.TextView r3 = r0.tvFiatRow2Name
            r6 = 2131692867(0x7f0f0d43, float:1.9014846E38)
            java.lang.String r9 = "PayBillSellPrice"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r6)
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvFiatRow2Info
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.sell_avg
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x0622
            r9 = r16
            goto L_0x0626
        L_0x0622:
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.sell_avg
        L_0x0626:
            java.lang.String r9 = r0.setMoneyFormat(r9)
            r6.append(r9)
            r6.append(r15)
            r6.append(r5)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0690
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            int r6 = r3.hashCode()
            r7 = 49
            if (r6 == r7) goto L_0x0662
            r7 = 50
            if (r6 == r7) goto L_0x0658
        L_0x0657:
            goto L_0x066c
        L_0x0658:
            java.lang.String r6 = "2"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x0657
            r13 = 1
            goto L_0x066d
        L_0x0662:
            java.lang.String r6 = "1"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x0657
            r13 = 0
            goto L_0x066d
        L_0x066c:
            r13 = -1
        L_0x066d:
            if (r13 == 0) goto L_0x0682
            r3 = 1
            if (r13 == r3) goto L_0x0673
            goto L_0x0690
        L_0x0673:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692866(0x7f0f0d42, float:1.9014844E38)
            java.lang.String r7 = "PayBillSellLimit"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            goto L_0x0690
        L_0x0682:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692865(0x7f0f0d41, float:1.9014842E38)
            java.lang.String r7 = "PayBillSellAtMarketPrice"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
        L_0x0690:
            android.widget.LinearLayout r3 = r0.llIconView
            r3.setVisibility(r8)
            android.widget.LinearLayout r3 = r0.llContainer
            r3.setVisibility(r8)
            android.widget.LinearLayout r3 = r0.llCurrencyContainer
            r3.setVisibility(r8)
            android.widget.LinearLayout r3 = r0.llFiatContainer
            r6 = 0
            r3.setVisibility(r6)
            goto L_0x0e0a
        L_0x06a7:
            android.widget.TextView r3 = r0.tvAmount
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r7 = r0.bean
            java.lang.String r7 = r7.deal_num
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x06be
            r7 = r16
            goto L_0x06c2
        L_0x06be:
            im.bclpbkiauv.javaBean.PayBillOverBean r7 = r0.bean
            java.lang.String r7 = r7.deal_num
        L_0x06c2:
            java.lang.String r7 = r0.setMoneyFormat(r7)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0723
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            int r6 = r3.hashCode()
            r7 = 49
            if (r6 == r7) goto L_0x06f5
            r7 = 50
            if (r6 == r7) goto L_0x06eb
        L_0x06ea:
            goto L_0x06ff
        L_0x06eb:
            java.lang.String r6 = "2"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x06ea
            r13 = 1
            goto L_0x0700
        L_0x06f5:
            java.lang.String r6 = "1"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x06ea
            r13 = 0
            goto L_0x0700
        L_0x06ff:
            r13 = -1
        L_0x0700:
            if (r13 == 0) goto L_0x0715
            r3 = 1
            if (r13 == r3) goto L_0x0706
            goto L_0x0723
        L_0x0706:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692845(0x7f0f0d2d, float:1.9014802E38)
            java.lang.String r7 = "PayBillLimitBuy"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            goto L_0x0723
        L_0x0715:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692846(0x7f0f0d2e, float:1.9014804E38)
            java.lang.String r7 = "PayBillMarketBuy"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
        L_0x0723:
            android.widget.LinearLayout r3 = r0.llIconView
            r3.setVisibility(r8)
            android.widget.LinearLayout r3 = r0.llContainer
            r3.setVisibility(r8)
            android.widget.LinearLayout r3 = r0.llCurrencyContainer
            r3.setVisibility(r8)
            android.widget.LinearLayout r3 = r0.llFiatContainer
            r6 = 0
            r3.setVisibility(r6)
            goto L_0x0e0a
        L_0x073a:
            android.widget.TextView r3 = r0.tvAmount
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r7 = r0.bean
            java.lang.String r7 = r7.deal_amount
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x0751
            r7 = r16
            goto L_0x0755
        L_0x0751:
            im.bclpbkiauv.javaBean.PayBillOverBean r7 = r0.bean
            java.lang.String r7 = r7.deal_amount
        L_0x0755:
            java.lang.String r7 = r0.setMoneyFormat(r7)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x07e9
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            int r6 = r3.hashCode()
            switch(r6) {
                case 48626: goto L_0x0797;
                case 48627: goto L_0x078d;
                case 48628: goto L_0x0783;
                case 48629: goto L_0x0779;
                default: goto L_0x0778;
            }
        L_0x0778:
            goto L_0x07a1
        L_0x0779:
            java.lang.String r6 = "104"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x0778
            r13 = 3
            goto L_0x07a2
        L_0x0783:
            java.lang.String r6 = "103"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x0778
            r13 = 2
            goto L_0x07a2
        L_0x078d:
            java.lang.String r6 = "102"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x0778
            r13 = 1
            goto L_0x07a2
        L_0x0797:
            java.lang.String r6 = "101"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x0778
            r13 = 0
            goto L_0x07a2
        L_0x07a1:
            r13 = -1
        L_0x07a2:
            if (r13 == 0) goto L_0x07db
            r3 = 1
            if (r13 == r3) goto L_0x07cc
            r3 = 2
            if (r13 == r3) goto L_0x07bd
            r3 = 3
            if (r13 == r3) goto L_0x07ae
            goto L_0x07e9
        L_0x07ae:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692866(0x7f0f0d42, float:1.9014844E38)
            java.lang.String r7 = "PayBillSellLimit"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            goto L_0x07e9
        L_0x07bd:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692845(0x7f0f0d2d, float:1.9014802E38)
            java.lang.String r7 = "PayBillLimitBuy"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            goto L_0x07e9
        L_0x07cc:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692865(0x7f0f0d41, float:1.9014842E38)
            java.lang.String r7 = "PayBillSellAtMarketPrice"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            goto L_0x07e9
        L_0x07db:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692846(0x7f0f0d2e, float:1.9014804E38)
            java.lang.String r7 = "PayBillMarketBuy"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
        L_0x07e9:
            android.widget.TextView r3 = r0.tvRow5Name
            r6 = 2131692862(0x7f0f0d3e, float:1.9014836E38)
            java.lang.String r7 = "PayBillReturnType"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow5Info
            r6 = 2131692827(0x7f0f0d1b, float:1.9014765E38)
            java.lang.String r7 = "PayBillDelegateTimeout"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            android.widget.TextView r3 = r0.tvRow8Name
            r6 = 2131692859(0x7f0f0d3b, float:1.901483E38)
            java.lang.String r7 = "PayBillReturnTime"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            android.widget.LinearLayout r3 = r0.llIconView
            r3.setVisibility(r8)
            android.widget.RelativeLayout r3 = r0.rlCurrencyRow1
            r3.setVisibility(r8)
            android.widget.RelativeLayout r3 = r0.rlCurrencyRow2
            r3.setVisibility(r8)
            android.widget.RelativeLayout r3 = r0.rlCurrencyRow4
            r3.setVisibility(r8)
            android.widget.RelativeLayout r3 = r0.rlCurrencyRow5
            r3.setVisibility(r8)
            android.widget.LinearLayout r3 = r0.llContainer
            r3.setVisibility(r8)
            android.widget.LinearLayout r3 = r0.llCurrencyContainer
            r6 = 0
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llFiatContainer
            r3.setVisibility(r8)
            goto L_0x0e0a
        L_0x083e:
            int r3 = r5.hashCode()
            switch(r3) {
                case 66097: goto L_0x085e;
                case 68985: goto L_0x0856;
                case 75707: goto L_0x084e;
                case 2614190: goto L_0x0846;
                default: goto L_0x0845;
            }
        L_0x0845:
            goto L_0x0866
        L_0x0846:
            boolean r3 = r5.equals(r9)
            if (r3 == 0) goto L_0x0845
            r3 = 2
            goto L_0x0867
        L_0x084e:
            boolean r3 = r5.equals(r10)
            if (r3 == 0) goto L_0x0845
            r3 = 3
            goto L_0x0867
        L_0x0856:
            boolean r3 = r5.equals(r11)
            if (r3 == 0) goto L_0x0845
            r3 = 1
            goto L_0x0867
        L_0x085e:
            boolean r3 = r5.equals(r12)
            if (r3 == 0) goto L_0x0845
            r3 = 0
            goto L_0x0867
        L_0x0866:
            r3 = -1
        L_0x0867:
            if (r3 == 0) goto L_0x088e
            r8 = 1
            if (r3 == r8) goto L_0x0885
            r8 = 2
            if (r3 == r8) goto L_0x087c
            r8 = 3
            if (r3 == r8) goto L_0x0873
            goto L_0x0897
        L_0x0873:
            android.widget.ImageView r3 = r0.ivIcon
            r8 = 2131558709(0x7f0d0135, float:1.8742741E38)
            r3.setImageResource(r8)
            goto L_0x0897
        L_0x087c:
            android.widget.ImageView r3 = r0.ivIcon
            r8 = 2131558711(0x7f0d0137, float:1.8742746E38)
            r3.setImageResource(r8)
            goto L_0x0897
        L_0x0885:
            android.widget.ImageView r3 = r0.ivIcon
            r8 = 2131558707(0x7f0d0133, float:1.8742737E38)
            r3.setImageResource(r8)
            goto L_0x0897
        L_0x088e:
            android.widget.ImageView r3 = r0.ivIcon
            r8 = 2131558703(0x7f0d012f, float:1.874273E38)
            r3.setImageResource(r8)
        L_0x0897:
            int r3 = r4.hashCode()
            switch(r3) {
                case 66097: goto L_0x08b7;
                case 68985: goto L_0x08af;
                case 75707: goto L_0x08a7;
                case 2614190: goto L_0x089f;
                default: goto L_0x089e;
            }
        L_0x089e:
            goto L_0x08bf
        L_0x089f:
            boolean r3 = r4.equals(r9)
            if (r3 == 0) goto L_0x089e
            r3 = 2
            goto L_0x08c0
        L_0x08a7:
            boolean r3 = r4.equals(r10)
            if (r3 == 0) goto L_0x089e
            r3 = 3
            goto L_0x08c0
        L_0x08af:
            boolean r3 = r4.equals(r11)
            if (r3 == 0) goto L_0x089e
            r3 = 1
            goto L_0x08c0
        L_0x08b7:
            boolean r3 = r4.equals(r12)
            if (r3 == 0) goto L_0x089e
            r3 = 0
            goto L_0x08c0
        L_0x08bf:
            r3 = -1
        L_0x08c0:
            if (r3 == 0) goto L_0x08e7
            r8 = 1
            if (r3 == r8) goto L_0x08de
            r8 = 2
            if (r3 == r8) goto L_0x08d5
            r8 = 3
            if (r3 == r8) goto L_0x08cc
            goto L_0x08f0
        L_0x08cc:
            android.widget.ImageView r3 = r0.ivIcon2
            r8 = 2131558709(0x7f0d0135, float:1.8742741E38)
            r3.setImageResource(r8)
            goto L_0x08f0
        L_0x08d5:
            android.widget.ImageView r3 = r0.ivIcon2
            r8 = 2131558711(0x7f0d0137, float:1.8742746E38)
            r3.setImageResource(r8)
            goto L_0x08f0
        L_0x08de:
            android.widget.ImageView r3 = r0.ivIcon2
            r8 = 2131558707(0x7f0d0133, float:1.8742737E38)
            r3.setImageResource(r8)
            goto L_0x08f0
        L_0x08e7:
            android.widget.ImageView r3 = r0.ivIcon2
            r8 = 2131558703(0x7f0d012f, float:1.874273E38)
            r3.setImageResource(r8)
        L_0x08f0:
            android.widget.TextView r3 = r0.tvAmount
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.gain_amount
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x0907
            r9 = r16
            goto L_0x090b
        L_0x0907:
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.gain_amount
        L_0x090b:
            java.lang.String r9 = r0.setMoneyFormat(r9)
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvAmount2
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.deal_num
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x0930
            r9 = r16
            goto L_0x0934
        L_0x0930:
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.deal_num
        L_0x0934:
            java.lang.String r9 = r0.setMoneyFormat(r9)
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow1Info
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.gain_amount
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x0959
            r9 = r16
            goto L_0x095d
        L_0x0959:
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.gain_amount
        L_0x095d:
            java.lang.String r9 = r0.setMoneyFormat(r9)
            r8.append(r9)
            r8.append(r13)
            r8.append(r5)
            r8.append(r7)
            java.lang.String r8 = r8.toString()
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow2Info
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_num
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x098b
            r6 = r16
            goto L_0x098f
        L_0x098b:
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_num
        L_0x098f:
            java.lang.String r6 = r0.setMoneyFormat(r6)
            r8.append(r6)
            r8.append(r13)
            r8.append(r4)
            r8.append(r7)
            java.lang.String r6 = r8.toString()
            r3.setText(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x09f9
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            int r6 = r3.hashCode()
            r8 = 49
            if (r6 == r8) goto L_0x09cb
            r8 = 50
            if (r6 == r8) goto L_0x09c1
        L_0x09c0:
            goto L_0x09d5
        L_0x09c1:
            java.lang.String r6 = "2"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x09c0
            r13 = 1
            goto L_0x09d6
        L_0x09cb:
            java.lang.String r6 = "1"
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x09c0
            r13 = 0
            goto L_0x09d6
        L_0x09d5:
            r13 = -1
        L_0x09d6:
            if (r13 == 0) goto L_0x09eb
            r3 = 1
            if (r13 == r3) goto L_0x09dc
            goto L_0x09f9
        L_0x09dc:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692866(0x7f0f0d42, float:1.9014844E38)
            java.lang.String r8 = "PayBillSellLimit"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r6)
            r3.setText(r6)
            goto L_0x09f9
        L_0x09eb:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692865(0x7f0f0d41, float:1.9014842E38)
            java.lang.String r8 = "PayBillSellAtMarketPrice"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r6)
            r3.setText(r6)
        L_0x09f9:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow4Info
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "1 "
            r6.append(r8)
            r6.append(r4)
            java.lang.String r8 = " = "
            r6.append(r8)
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.sell_avg
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0a1a
            r8 = r16
            goto L_0x0a1e
        L_0x0a1a:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.sell_avg
        L_0x0a1e:
            r6.append(r8)
            java.lang.String r8 = " "
            r6.append(r8)
            r6.append(r5)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow5Info
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_fees
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0a44
            r8 = r16
            goto L_0x0a48
        L_0x0a44:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_fees
        L_0x0a48:
            java.lang.String r8 = r0.setMoneyFormat(r8)
            r6.append(r8)
            r6.append(r15)
            r6.append(r5)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            android.widget.LinearLayout r3 = r0.llContainer
            r6 = 8
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llCurrencyContainer
            r7 = 0
            r3.setVisibility(r7)
            android.widget.LinearLayout r3 = r0.llFiatContainer
            r3.setVisibility(r6)
            goto L_0x0e0a
        L_0x0a73:
            android.widget.TextView r3 = r0.tvAmount
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.gain_amount
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x0a8a
            r9 = r16
            goto L_0x0a8e
        L_0x0a8a:
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.gain_amount
        L_0x0a8e:
            java.lang.String r9 = r0.setMoneyFormat(r9)
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r3.setText(r8)
            android.widget.TextView r3 = r0.tvAmount2
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.deal_amount
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x0ab3
            r9 = r16
            goto L_0x0ab7
        L_0x0ab3:
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.deal_amount
        L_0x0ab7:
            java.lang.String r9 = r0.setMoneyFormat(r9)
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r3.setText(r8)
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0bfd
            im.bclpbkiauv.javaBean.PayBillOverBean r3 = r0.bean
            java.lang.String r3 = r3.deal_type
            int r8 = r3.hashCode()
            r9 = 49
            if (r8 == r9) goto L_0x0aea
            r9 = 50
            if (r8 == r9) goto L_0x0ae0
        L_0x0adf:
            goto L_0x0af4
        L_0x0ae0:
            java.lang.String r8 = "2"
            boolean r3 = r3.equals(r8)
            if (r3 == 0) goto L_0x0adf
            r3 = 1
            goto L_0x0af5
        L_0x0aea:
            java.lang.String r8 = "1"
            boolean r3 = r3.equals(r8)
            if (r3 == 0) goto L_0x0adf
            r3 = 0
            goto L_0x0af5
        L_0x0af4:
            r3 = -1
        L_0x0af5:
            if (r3 == 0) goto L_0x0b3e
            r8 = 1
            if (r3 == r8) goto L_0x0afc
            goto L_0x0bfd
        L_0x0afc:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow2Info
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_amount
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0b13
            r6 = r16
            goto L_0x0b17
        L_0x0b13:
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_amount
        L_0x0b17:
            java.lang.String r6 = r0.setMoneyFormat(r6)
            r8.append(r6)
            r8.append(r13)
            r8.append(r5)
            r8.append(r7)
            java.lang.String r6 = r8.toString()
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692845(0x7f0f0d2d, float:1.9014802E38)
            java.lang.String r8 = "PayBillLimitBuy"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r6)
            r3.setText(r6)
            goto L_0x0bfd
        L_0x0b3e:
            android.widget.TextView r3 = r0.tvAmount2
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.deal_num
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x0b55
            r9 = r16
            goto L_0x0b59
        L_0x0b55:
            im.bclpbkiauv.javaBean.PayBillOverBean r9 = r0.bean
            java.lang.String r9 = r9.deal_num
        L_0x0b59:
            java.lang.String r9 = r0.setMoneyFormat(r9)
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r3.setText(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow2Info
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_num
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0b7e
            r6 = r16
            goto L_0x0b82
        L_0x0b7e:
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_num
        L_0x0b82:
            java.lang.String r6 = r0.setMoneyFormat(r6)
            r8.append(r6)
            r8.append(r13)
            r8.append(r5)
            r8.append(r7)
            java.lang.String r6 = r8.toString()
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow3Info
            r6 = 2131692846(0x7f0f0d2e, float:1.9014804E38)
            java.lang.String r8 = "PayBillMarketBuy"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r6)
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow9Info
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.mandate_num
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0bbb
            r8 = r19
            goto L_0x0bbf
        L_0x0bbb:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.mandate_num
        L_0x0bbf:
            r6.append(r8)
            r6.append(r15)
            r6.append(r5)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow10Info
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_num
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0be6
            r8 = r19
            goto L_0x0bea
        L_0x0be6:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_num
        L_0x0bea:
            r6.append(r8)
            r6.append(r15)
            r6.append(r5)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
        L_0x0bfd:
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow1Info
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.gain_amount
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0c14
            r8 = r16
            goto L_0x0c18
        L_0x0c14:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.gain_amount
        L_0x0c18:
            java.lang.String r8 = r0.setMoneyFormat(r8)
            r6.append(r8)
            r6.append(r13)
            r6.append(r4)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow4Info
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "1 "
            r6.append(r8)
            r6.append(r4)
            java.lang.String r8 = " = "
            r6.append(r8)
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.buy_avg
            r6.append(r8)
            java.lang.String r8 = " "
            r6.append(r8)
            r6.append(r5)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRow5Info
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_fees
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0c6d
            r8 = r16
            goto L_0x0c71
        L_0x0c6d:
            im.bclpbkiauv.javaBean.PayBillOverBean r8 = r0.bean
            java.lang.String r8 = r8.deal_fees
        L_0x0c71:
            java.lang.String r8 = r0.setMoneyFormat(r8)
            r6.append(r8)
            r6.append(r15)
            r6.append(r4)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            android.widget.LinearLayout r3 = r0.llContainer
            r6 = 8
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llCurrencyContainer
            r7 = 0
            r3.setVisibility(r7)
            android.widget.LinearLayout r3 = r0.llFiatContainer
            r3.setVisibility(r6)
            goto L_0x0e0a
        L_0x0c9c:
            android.widget.TextView r3 = r0.tvAmount
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r7 = r0.bean
            java.lang.String r7 = r7.deal_amount
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x0cb3
            r7 = r16
            goto L_0x0cb7
        L_0x0cb3:
            im.bclpbkiauv.javaBean.PayBillOverBean r7 = r0.bean
            java.lang.String r7 = r7.deal_amount
        L_0x0cb7:
            java.lang.String r7 = r0.setMoneyFormat(r7)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName1
            r6 = 2131692868(0x7f0f0d44, float:1.9014848E38)
            java.lang.String r7 = "PayBillSourceAddress"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress1
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_from
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0ce2
            r13 = r19
            goto L_0x0ce6
        L_0x0ce2:
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r13 = r6.deal_from
        L_0x0ce6:
            r3.setText(r13)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName4
            r6 = 2131692862(0x7f0f0d3e, float:1.9014836E38)
            java.lang.String r7 = "PayBillReturnType"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress4
            r6 = 2131692876(0x7f0f0d4c, float:1.9014864E38)
            java.lang.String r7 = "PayBillTransactionFailed"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName7
            r6 = 2131692859(0x7f0f0d3b, float:1.901483E38)
            java.lang.String r7 = "PayBillReturnTime"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            android.widget.RelativeLayout r3 = r0.rlRow6
            r6 = 8
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llIconView
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llContainer
            r7 = 0
            r3.setVisibility(r7)
            android.widget.LinearLayout r3 = r0.llCurrencyContainer
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llFiatContainer
            r3.setVisibility(r6)
            goto L_0x0e0a
        L_0x0d31:
            android.widget.TextView r3 = r0.tvAmount
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r14)
            im.bclpbkiauv.javaBean.PayBillOverBean r7 = r0.bean
            java.lang.String r7 = r7.deal_amount
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x0d48
            r7 = r16
            goto L_0x0d4c
        L_0x0d48:
            im.bclpbkiauv.javaBean.PayBillOverBean r7 = r0.bean
            java.lang.String r7 = r7.deal_amount
        L_0x0d4c:
            java.lang.String r7 = r0.setMoneyFormat(r7)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName1
            r6 = 2131692868(0x7f0f0d44, float:1.9014848E38)
            java.lang.String r7 = "PayBillSourceAddress"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress1
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_from
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0d77
            r13 = r19
            goto L_0x0d7b
        L_0x0d77:
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r13 = r6.deal_from
        L_0x0d7b:
            r3.setText(r13)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName4
            r6 = 2131692884(0x7f0f0d54, float:1.901488E38)
            java.lang.String r7 = "PayBillTransferType"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowAddress4
            r6 = 2131692855(0x7f0f0d37, float:1.9014822E38)
            java.lang.String r7 = "PayBillRechargeTransferIn"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r3 = r0.tvRowName7
            r6 = 2131692809(0x7f0f0d09, float:1.9014729E38)
            java.lang.String r7 = "PayBillArrivalTime"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            android.widget.RelativeLayout r3 = r0.rlRow6
            r6 = 8
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llIconView
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llContainer
            r7 = 0
            r3.setVisibility(r7)
            android.widget.LinearLayout r3 = r0.llCurrencyContainer
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llFiatContainer
            r3.setVisibility(r6)
            goto L_0x0e0a
        L_0x0dc5:
            android.widget.TextView r3 = r0.tvAmount
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r6)
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_amount
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0ddc
            r6 = r16
            goto L_0x0de0
        L_0x0ddc:
            im.bclpbkiauv.javaBean.PayBillOverBean r6 = r0.bean
            java.lang.String r6 = r6.deal_amount
        L_0x0de0:
            java.lang.String r6 = r0.setMoneyFormat(r6)
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            r3.setText(r6)
            android.widget.RelativeLayout r3 = r0.rlRow6
            r6 = 0
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llIconView
            r7 = 8
            r3.setVisibility(r7)
            android.widget.LinearLayout r3 = r0.llContainer
            r3.setVisibility(r6)
            android.widget.LinearLayout r3 = r0.llCurrencyContainer
            r3.setVisibility(r7)
            android.widget.LinearLayout r3 = r0.llFiatContainer
            r3.setVisibility(r7)
        L_0x0e0a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.packet.BillDetailsActivity.initViews():void");
    }

    @OnClick({2131296766, 2131296767, 2131296768, 2131296765, 2131296741})
    public void onViewClicked(View view) {
        ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
        String dealTxId = "";
        int id = view.getId();
        if (id != R.id.ivFiat5Copy) {
            switch (id) {
                case R.id.ivRow6Copy /*2131296765*/:
                    dealTxId = this.bean.order_id;
                    break;
                case R.id.ivRowCopy1 /*2131296766*/:
                    dealTxId = this.bean.deal_with;
                    break;
                case R.id.ivRowCopy2 /*2131296767*/:
                    dealTxId = this.bean.deal_txId;
                    break;
                case R.id.ivRowCopy3 /*2131296768*/:
                    dealTxId = this.bean.order_id;
                    break;
            }
        } else {
            dealTxId = this.bean.order_id;
        }
        clipboard.setPrimaryClip(ClipData.newPlainText("label", dealTxId));
        ToastUtils.show((CharSequence) LocaleController.getString("CopySuccess", R.string.CopySuccess));
    }

    public String getDate(String data) {
        Integer date = Integer.valueOf(Integer.parseInt(data));
        if (date.intValue() < 60) {
            return date + LocaleController.getString("PayBillSecond", R.string.PayBillSecond);
        } else if (date.intValue() <= 60 || date.intValue() >= 3600) {
            return (date.intValue() / 3600) + LocaleController.getString("PayBillHour", R.string.PayBillHour) + ((date.intValue() % 3600) / 60) + LocaleController.getString("PayBillMinute", R.string.PayBillMinute) + ((date.intValue() % 3600) % 60) + LocaleController.getString("PayBillSecond", R.string.PayBillSecond);
        } else {
            return (date.intValue() / 60) + LocaleController.getString("PayBillMinute", R.string.PayBillMinute) + (date.intValue() % 60) + LocaleController.getString("PayBillSecond", R.string.PayBillSecond);
        }
    }

    public String setMoneyFormat(String data) {
        return data;
    }

    private void initActionBar() {
        if (this.mMessageObject.type == 104) {
            switch (((TLRPCRedpacket.CL_messagesPayBillOverMedia) this.mMessageObject.messageOwner.media).deal_code) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    this.actionBar.setTitle(LocaleController.getString("PayBillDetails", R.string.PayBillDetails));
                    break;
                case 8:
                    this.actionBar.setTitle(LocaleController.getString("PayBillEntrustedBuyDetails", R.string.PayBillEntrustedBuyDetails));
                    break;
                case 9:
                    this.actionBar.setTitle(LocaleController.getString("PayBillEntrustedSellingDetails", R.string.PayBillEntrustedSellingDetails));
                    break;
                case 10:
                    this.actionBar.setTitle(LocaleController.getString("PayBillEntrustedReturnDetails", R.string.PayBillEntrustedReturnDetails));
                    break;
                case 11:
                    this.actionBar.setTitle(LocaleController.getString("PayBillFiatPurchaseDetails", R.string.PayBillFiatPurchaseDetails));
                    break;
                case 12:
                    this.actionBar.setTitle(LocaleController.getString("PayBillFiatCurrencySaleDetails", R.string.PayBillFiatCurrencySaleDetails));
                    break;
            }
        }
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    BillDetailsActivity.this.finishFragment();
                }
            }
        });
    }
}
