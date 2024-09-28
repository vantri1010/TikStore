package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.blankj.utilcode.util.TimeUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.tgnet.TLRPCLogin;
import im.bclpbkiauv.ui.SelectBirthdayActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.TimeWheelPickerDialog;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.Calendar;
import java.util.Date;

public class SelectBirthdayActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int item_done = 1;
    private TimeWheelPickerDialog.Builder mTimePickerBuilder;
    /* access modifiers changed from: private */
    public Date selectedDate;
    private MryTextView tvBirthday;
    /* access modifiers changed from: private */
    public TLRPCContacts.CL_userFull_v1 userFull;

    public SelectBirthdayActivity(TLRPCContacts.CL_userFull_v1 userFull2) {
        this.userFull = userFull2;
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_select_birthday, (ViewGroup) null);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initView();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString(R.string.SelectBirthday));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    SelectBirthdayActivity.this.finishFragment();
                } else if (id == SelectBirthdayActivity.this.item_done && SelectBirthdayActivity.this.userFull != null && SelectBirthdayActivity.this.userFull.getExtendBean() != null) {
                    if (!SelectBirthdayActivity.isTheSameDay(new Date(((long) SelectBirthdayActivity.this.userFull.getExtendBean().birthday) * 1000), SelectBirthdayActivity.this.selectedDate)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) SelectBirthdayActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString(R.string.AppName));
                        builder.setMessage(LocaleController.getString(R.string.UserBirthOnlyCanModifyOnceContuine));
                        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), (DialogInterface.OnClickListener) null);
                        builder.setPositiveButton(LocaleController.getString(R.string.Confirm), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                SelectBirthdayActivity.AnonymousClass1.this.lambda$onItemClick$0$SelectBirthdayActivity$1(dialogInterface, i);
                            }
                        });
                        SelectBirthdayActivity.this.showDialog(builder.create());
                        return;
                    }
                    SelectBirthdayActivity.this.finishFragment();
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$SelectBirthdayActivity$1(DialogInterface dialog, int which) {
                SelectBirthdayActivity.this.updateUserExtraInformation();
            }
        });
        this.actionBar.createMenu().addItem(this.item_done, (CharSequence) LocaleController.getString(R.string.Done));
    }

    private void initView() {
        RelativeLayout rlBirthdayContainer = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_birthday_container);
        rlBirthdayContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        rlBirthdayContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SelectBirthdayActivity.this.lambda$initView$0$SelectBirthdayActivity(view);
            }
        });
        this.tvBirthday = (MryTextView) this.fragmentView.findViewById(R.id.tv_birthday);
        ((TextView) this.fragmentView.findViewById(R.id.tv_birthday_prefix)).setText(LocaleController.getString("Birthday", R.string.Birthday));
        ((TextView) this.fragmentView.findViewById(R.id.tv_birthday_tips)).setText(LocaleController.getString("BirthdayTips", R.string.BirthdayTips));
        TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = this.userFull;
        if (cL_userFull_v1 != null && cL_userFull_v1.getExtendBean() != null) {
            Date date = new Date(((long) this.userFull.getExtendBean().birthday) * 1000);
            this.selectedDate = date;
            this.tvBirthday.setText(TimeUtils.millis2String(date.getTime(), LocaleController.getString("yyyy.mm.dd", R.string.formatterYear2)));
        }
    }

    public /* synthetic */ void lambda$initView$0$SelectBirthdayActivity(View v) {
        showSelectBirthDialog();
    }

    private void showSelectBirthDialog() {
        if (this.mTimePickerBuilder == null) {
            this.mTimePickerBuilder = TimeWheelPickerDialog.getDefaultBuilder(getParentActivity(), new OnTimeSelectListener() {
                public final void onTimeSelect(Date date, View view) {
                    SelectBirthdayActivity.this.lambda$showSelectBirthDialog$1$SelectBirthdayActivity(date, view);
                }
            });
        }
        if (this.selectedDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.selectedDate);
            this.mTimePickerBuilder.setDate(calendar);
        } else {
            this.mTimePickerBuilder.setDate(Calendar.getInstance());
        }
        showDialog(this.mTimePickerBuilder.build());
    }

    public /* synthetic */ void lambda$showSelectBirthDialog$1$SelectBirthdayActivity(Date date, View v) {
        this.selectedDate = date;
        this.tvBirthday.setText(TimeUtils.millis2String(date.getTime(), LocaleController.getString("yyyy.mm.dd", R.string.formatterYear2)));
    }

    /* access modifiers changed from: private */
    public void updateUserExtraInformation() {
        TLRPCLogin.TL_account_updateUserDetail req = new TLRPCLogin.TL_account_updateUserDetail();
        req.birthday = (int) (this.selectedDate.getTime() / 1000);
        if (req.birthday != 0) {
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SelectBirthdayActivity.this.lambda$updateUserExtraInformation$3$SelectBirthdayActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$updateUserExtraInformation$3$SelectBirthdayActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SelectBirthdayActivity.this.lambda$null$2$SelectBirthdayActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$SelectBirthdayActivity(TLRPC.TL_error error, TLObject response) {
        TLRPCContacts.CL_userFull_v1 cL_userFull_v1;
        if (error != null) {
            if (error.text == null) {
                return;
            }
            if (error.text.contains("ALREDY_CHANGE")) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString(R.string.AppName));
                builder.setMessage(LocaleController.getString(R.string.YouHadModifiedOnceCannotModifyAgain));
                builder.setPositiveButton(LocaleController.getString(R.string.OK), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            } else if (error.code == 400 || error.text.contains("rpcerror")) {
                ToastUtils.show((int) R.string.SetupFail);
            } else {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.OperationFailedPleaseTryAgain));
            }
        } else if ((response instanceof TLRPC.UserFull) && this.selectedDate != null && (cL_userFull_v1 = this.userFull) != null && cL_userFull_v1.getExtendBean() != null) {
            this.userFull.getExtendBean().birthday = (int) (this.selectedDate.getTime() / 1000);
        }
    }

    public static boolean isTheSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if (c1.get(1) == c2.get(1) && c1.get(2) == c2.get(2) && c1.get(5) == c2.get(5)) {
            return true;
        }
        return false;
    }
}
