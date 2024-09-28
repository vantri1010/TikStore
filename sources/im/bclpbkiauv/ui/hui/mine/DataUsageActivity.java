package im.bclpbkiauv.ui.hui.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hcells.MryTextCheckCell;
import im.bclpbkiauv.ui.hcells.TextSettingCell;
import im.bclpbkiauv.ui.hui.mine.DataUsageActivity;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import im.bclpbkiauv.ui.hviews.dialogs.XDialogStyle;

public class DataUsageActivity extends BaseFragment {
    private ListAdapter adapter;
    /* access modifiers changed from: private */
    public boolean[] clear = new boolean[2];
    /* access modifiers changed from: private */
    public int contacstSectionRow;
    private boolean currentSuggest;
    private boolean currentSync;
    /* access modifiers changed from: private */
    public int deleteDraftRow;
    /* access modifiers changed from: private */
    public int deletePayInfoDetailRow;
    /* access modifiers changed from: private */
    public int deletePayInfoRow;
    /* access modifiers changed from: private */
    public int dialogSectionEmptyRow;
    /* access modifiers changed from: private */
    public int dialogSectionRow;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public int likReviewDetailRow;
    /* access modifiers changed from: private */
    public int linkReviewRow;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public boolean newSuggest;
    /* access modifiers changed from: private */
    public boolean newSync;
    /* access modifiers changed from: private */
    public int paySectionRow;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public int recommenBusyContactsDetailRow;
    /* access modifiers changed from: private */
    public int recommendBusyContactRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int secureSectionRow;
    /* access modifiers changed from: private */
    public int sycnContactsRow;
    /* access modifiers changed from: private */
    public int syncContactsDetailRow;

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.emptyRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.contacstSectionRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.sycnContactsRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.syncContactsDetailRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.recommendBusyContactRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.recommenBusyContactsDetailRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.dialogSectionRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.deleteDraftRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.dialogSectionEmptyRow = i8;
        this.paySectionRow = -1;
        this.deletePayInfoRow = -1;
        this.deletePayInfoDetailRow = -1;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.secureSectionRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.linkReviewRow = i10;
        this.rowCount = i11 + 1;
        this.likReviewDetailRow = i11;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("DataUsageSetting", R.string.DataUsageSetting));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DataUsageActivity.this.finishFragment();
                }
            }
        });
    }

    public boolean onFragmentCreate() {
        boolean z = getUserConfig().syncContacts;
        this.newSync = z;
        this.currentSync = z;
        boolean z2 = getUserConfig().suggestContacts;
        this.newSuggest = z2;
        this.currentSuggest = z2;
        updateRows();
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.currentSync != this.newSync) {
            getUserConfig().syncContacts = this.newSync;
            getUserConfig().saveConfig(false);
            if (this.newSync) {
                getContactsController().forceImportContacts();
                if (getParentActivity() != null) {
                    ToastUtils.show((int) R.string.SyncContactsAdded);
                }
            }
        }
        boolean z = this.newSuggest;
        if (z != this.currentSuggest) {
            if (!z) {
                getMediaDataController().clearTopPeers();
            }
            getUserConfig().suggestContacts = this.newSuggest;
            getUserConfig().saveConfig(false);
            TLRPC.TL_contacts_toggleTopPeers req = new TLRPC.TL_contacts_toggleTopPeers();
            req.enabled = this.newSuggest;
            getConnectionsManager().sendRequest(req, $$Lambda$DataUsageActivity$a9PrNQTLAL4qpiFgU1n7JUEOMOE.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject response, TLRPC.TL_error error) {
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_listview_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initList();
        return this.fragmentView;
    }

    private void initList() {
        RecyclerListView recyclerListView = (RecyclerListView) this.fragmentView.findViewById(R.id.listview);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(this.mContext));
        ListAdapter listAdapter = new ListAdapter();
        this.adapter = listAdapter;
        this.listView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                boolean z = true;
                if (position == DataUsageActivity.this.sycnContactsRow) {
                    DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                    boolean unused = dataUsageActivity.newSync = true ^ dataUsageActivity.newSync;
                    if (view instanceof MryTextCheckCell) {
                        ((MryTextCheckCell) view).setChecked(DataUsageActivity.this.newSync);
                    }
                } else if (position == DataUsageActivity.this.recommendBusyContactRow) {
                    MryTextCheckCell cell = (MryTextCheckCell) view;
                    if (DataUsageActivity.this.newSuggest) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) DataUsageActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("SuggestContactsAlert", R.string.SuggestContactsAlert));
                        builder.setPositiveButton(LocaleController.getString("MuteDisable", R.string.MuteDisable), new DialogInterface.OnClickListener(cell) {
                            private final /* synthetic */ MryTextCheckCell f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                DataUsageActivity.AnonymousClass2.this.lambda$onItemClick$2$DataUsageActivity$2(this.f$1, dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                        DataUsageActivity.this.showDialog(builder.create());
                        return;
                    }
                    DataUsageActivity dataUsageActivity2 = DataUsageActivity.this;
                    boolean unused2 = dataUsageActivity2.newSuggest = true ^ dataUsageActivity2.newSuggest;
                    cell.setChecked(DataUsageActivity.this.newSuggest);
                } else if (position == DataUsageActivity.this.deleteDraftRow) {
                    XDialog.Builder builder2 = new XDialog.Builder(DataUsageActivity.this.getParentActivity());
                    builder2.setStyle(XDialogStyle.IOS);
                    builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder2.setMessage(LocaleController.getString("AreYouSureClearDrafts", R.string.AreYouSureClearDrafts));
                    builder2.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            DataUsageActivity.AnonymousClass2.this.lambda$onItemClick$5$DataUsageActivity$2(dialogInterface, i);
                        }
                    });
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    DataUsageActivity.this.showDialog(builder2.create());
                } else if (position == DataUsageActivity.this.deletePayInfoRow) {
                    BottomSheet.Builder builder3 = new BottomSheet.Builder(DataUsageActivity.this.getParentActivity());
                    builder3.setApplyTopPadding(false);
                    builder3.setApplyBottomPadding(false);
                    LinearLayout linearLayout = new LinearLayout(DataUsageActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    for (int a = 0; a < 2; a++) {
                        String name = null;
                        if (a == 0) {
                            name = LocaleController.getString("PrivacyClearShipping", R.string.PrivacyClearShipping);
                        } else if (a == 1) {
                            name = LocaleController.getString("PrivacyClearPayment", R.string.PrivacyClearPayment);
                        }
                        DataUsageActivity.this.clear[a] = true;
                        CheckBoxCell checkBoxCell = new CheckBoxCell(DataUsageActivity.this.getParentActivity(), 1, 21);
                        checkBoxCell.setTag(Integer.valueOf(a));
                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                        checkBoxCell.setText(name, (String) null, true, true);
                        checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                        checkBoxCell.setOnClickListener(new View.OnClickListener() {
                            public final void onClick(View view) {
                                DataUsageActivity.AnonymousClass2.this.lambda$onItemClick$6$DataUsageActivity$2(view);
                            }
                        });
                    }
                    BottomSheet.BottomSheetCell cell2 = new BottomSheet.BottomSheetCell(DataUsageActivity.this.getParentActivity(), 1);
                    cell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cell2.setTextAndIcon(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), 0);
                    cell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                    cell2.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            DataUsageActivity.AnonymousClass2.this.lambda$onItemClick$9$DataUsageActivity$2(view);
                        }
                    });
                    linearLayout.addView(cell2, LayoutHelper.createLinear(-1, 50));
                    builder3.setCustomView(linearLayout);
                    DataUsageActivity.this.showDialog(builder3.create());
                } else if (position == DataUsageActivity.this.linkReviewRow) {
                    if (DataUsageActivity.this.getMessagesController().secretWebpagePreview == 1) {
                        DataUsageActivity.this.getMessagesController().secretWebpagePreview = 0;
                    } else {
                        DataUsageActivity.this.getMessagesController().secretWebpagePreview = 1;
                    }
                    MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", DataUsageActivity.this.getMessagesController().secretWebpagePreview).commit();
                    if (view instanceof MryTextCheckCell) {
                        MryTextCheckCell mryTextCheckCell = (MryTextCheckCell) view;
                        if (DataUsageActivity.this.getMessagesController().secretWebpagePreview != 1) {
                            z = false;
                        }
                        mryTextCheckCell.setChecked(z);
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$2$DataUsageActivity$2(MryTextCheckCell cell, DialogInterface dialogInterface, int i) {
                TLRPC.TL_payments_clearSavedInfo req = new TLRPC.TL_payments_clearSavedInfo();
                req.credentials = DataUsageActivity.this.clear[1];
                req.info = DataUsageActivity.this.clear[0];
                DataUsageActivity.this.getUserConfig().tmpPassword = null;
                DataUsageActivity.this.getUserConfig().saveConfig(false);
                DataUsageActivity.this.getConnectionsManager().sendRequest(req, new RequestDelegate(cell) {
                    private final /* synthetic */ MryTextCheckCell f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        DataUsageActivity.AnonymousClass2.this.lambda$null$1$DataUsageActivity$2(this.f$1, tLObject, tL_error);
                    }
                });
            }

            public /* synthetic */ void lambda$null$1$DataUsageActivity$2(MryTextCheckCell cell, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable(cell) {
                    private final /* synthetic */ MryTextCheckCell f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        DataUsageActivity.AnonymousClass2.this.lambda$null$0$DataUsageActivity$2(this.f$1);
                    }
                });
            }

            public /* synthetic */ void lambda$null$0$DataUsageActivity$2(MryTextCheckCell cell) {
                DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                boolean unused = dataUsageActivity.newSuggest = !dataUsageActivity.newSuggest;
                cell.setChecked(DataUsageActivity.this.newSuggest);
            }

            public /* synthetic */ void lambda$onItemClick$5$DataUsageActivity$2(DialogInterface dialogInterface, int i) {
                DataUsageActivity.this.getConnectionsManager().sendRequest(new TLRPC.TL_messages_clearAllDrafts(), new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        DataUsageActivity.AnonymousClass2.this.lambda$null$4$DataUsageActivity$2(tLObject, tL_error);
                    }
                });
            }

            public /* synthetic */ void lambda$null$3$DataUsageActivity$2() {
                DataUsageActivity.this.getMediaDataController().clearAllDrafts();
            }

            public /* synthetic */ void lambda$null$4$DataUsageActivity$2(TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        DataUsageActivity.AnonymousClass2.this.lambda$null$3$DataUsageActivity$2();
                    }
                });
            }

            public /* synthetic */ void lambda$onItemClick$6$DataUsageActivity$2(View v) {
                CheckBoxCell cell = (CheckBoxCell) v;
                int num = ((Integer) cell.getTag()).intValue();
                DataUsageActivity.this.clear[num] = !DataUsageActivity.this.clear[num];
                cell.setChecked(DataUsageActivity.this.clear[num], true);
            }

            public /* synthetic */ void lambda$onItemClick$9$DataUsageActivity$2(View v) {
                try {
                    if (DataUsageActivity.this.visibleDialog != null) {
                        DataUsageActivity.this.visibleDialog.dismiss();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) DataUsageActivity.this.getParentActivity());
                builder1.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder1.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", R.string.PrivacyPaymentsClearAlert));
                builder1.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DataUsageActivity.AnonymousClass2.this.lambda$null$8$DataUsageActivity$2(dialogInterface, i);
                    }
                });
                builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                DataUsageActivity.this.showDialog(builder1.create());
            }

            public /* synthetic */ void lambda$null$8$DataUsageActivity$2(DialogInterface dialogInterface, int i) {
                TLRPC.TL_payments_clearSavedInfo req = new TLRPC.TL_payments_clearSavedInfo();
                req.credentials = DataUsageActivity.this.clear[1];
                req.info = DataUsageActivity.this.clear[0];
                DataUsageActivity.this.getUserConfig().tmpPassword = null;
                DataUsageActivity.this.getUserConfig().saveConfig(false);
                DataUsageActivity.this.getConnectionsManager().sendRequest(req, $$Lambda$DataUsageActivity$2$MBw1hScaWiHyyS0gC9owH2Z8KFg.INSTANCE);
            }

            static /* synthetic */ void lambda$null$7(TLObject response, TLRPC.TL_error error) {
            }
        });
    }

    class ListAdapter extends RecyclerListView.SelectionAdapter {
        ListAdapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == DataUsageActivity.this.sycnContactsRow || position == DataUsageActivity.this.recommendBusyContactRow || position == DataUsageActivity.this.deleteDraftRow || position == DataUsageActivity.this.deletePayInfoRow || position == DataUsageActivity.this.linkReviewRow;
        }

        public int getItemViewType(int position) {
            if (position == DataUsageActivity.this.contacstSectionRow || position == DataUsageActivity.this.dialogSectionRow || position == DataUsageActivity.this.paySectionRow || position == DataUsageActivity.this.secureSectionRow) {
                return 0;
            }
            if (position == DataUsageActivity.this.deleteDraftRow || position == DataUsageActivity.this.deletePayInfoRow) {
                return 1;
            }
            if (position == DataUsageActivity.this.sycnContactsRow || position == DataUsageActivity.this.recommendBusyContactRow || position == DataUsageActivity.this.linkReviewRow) {
                return 2;
            }
            if (position == DataUsageActivity.this.dialogSectionEmptyRow || position == DataUsageActivity.this.emptyRow) {
                return 4;
            }
            return 3;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new HeaderCell(DataUsageActivity.this.mContext);
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                layoutParams.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
                layoutParams.height = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams);
            } else if (viewType == 1) {
                view = new TextSettingCell(DataUsageActivity.this.mContext);
                RecyclerView.LayoutParams layoutParams2 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams2.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams2.rightMargin = AndroidUtilities.dp(10.0f);
                layoutParams2.height = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams2);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 2) {
                view = new MryTextCheckCell(DataUsageActivity.this.mContext);
                RecyclerView.LayoutParams layoutParams3 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams3.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams3.rightMargin = AndroidUtilities.dp(10.0f);
                layoutParams3.height = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams3);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType != 4) {
                view = new TextInfoPrivacyCell(DataUsageActivity.this.mContext);
                RecyclerView.LayoutParams layoutParams4 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams4.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams4.rightMargin = AndroidUtilities.dp(10.0f);
                layoutParams4.height = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams4);
            } else {
                view = new ShadowSectionCell(DataUsageActivity.this.mContext);
                RecyclerView.LayoutParams layoutParams5 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams5.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams5.rightMargin = AndroidUtilities.dp(10.0f);
                layoutParams5.height = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams5);
                view.setBackgroundColor(0);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = true;
                if (itemViewType == 1) {
                    TextSettingCell textCell = (TextSettingCell) holder.itemView;
                    if (position == DataUsageActivity.this.deleteDraftRow) {
                        textCell.setText(LocaleController.getString("PrivacyDeleteCloudDrafts", R.string.PrivacyDeleteCloudDrafts), false);
                        textCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    } else if (position == DataUsageActivity.this.deletePayInfoRow) {
                        textCell.setText(LocaleController.getString("PrivacyPaymentsClear", R.string.PrivacyPaymentsClear), false);
                    }
                } else if (itemViewType == 2) {
                    MryTextCheckCell textCheckCell = (MryTextCheckCell) holder.itemView;
                    if (position == DataUsageActivity.this.sycnContactsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SyncContacts", R.string.SyncContacts), DataUsageActivity.this.newSync, false);
                        textCheckCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    } else if (position == DataUsageActivity.this.recommendBusyContactRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SuggestContacts", R.string.SuggestContacts), DataUsageActivity.this.newSuggest, false);
                        textCheckCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    } else if (position == DataUsageActivity.this.linkReviewRow) {
                        String string = LocaleController.getString("LinkPreview", R.string.LinkPreview);
                        if (DataUsageActivity.this.getMessagesController().secretWebpagePreview != 1) {
                            z = false;
                        }
                        textCheckCell.setTextAndCheck(string, z, false);
                        textCheckCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    }
                } else if (itemViewType == 3) {
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == DataUsageActivity.this.syncContactsDetailRow) {
                        privacyCell.setText(LocaleController.getString("SyncContactsInfo", R.string.SyncContactsInfoOff));
                    } else if (position == DataUsageActivity.this.recommenBusyContactsDetailRow) {
                        privacyCell.setText(LocaleController.getString("SuggestContactsInfo", R.string.SuggestContactsInfo));
                    } else if (position == DataUsageActivity.this.deletePayInfoDetailRow) {
                        privacyCell.setText(LocaleController.getString("PrivacyPaymentsClearInfo", R.string.PrivacyPaymentsClearInfo));
                    } else if (position == DataUsageActivity.this.likReviewDetailRow) {
                        privacyCell.setText(LocaleController.getString("SecretWebPageInfo", R.string.SecretWebPageInfo));
                    }
                }
            } else {
                HeaderCell headerCell = (HeaderCell) holder.itemView;
                headerCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                if (position == DataUsageActivity.this.contacstSectionRow) {
                    headerCell.setText(LocaleController.getString("BlockUserContactsTitle", R.string.BlockUserContactsTitle));
                } else if (position == DataUsageActivity.this.dialogSectionRow) {
                    headerCell.setText(LocaleController.getString("BlockUserChatsTitle", R.string.BlockUserChatsTitle));
                } else if (position == DataUsageActivity.this.paySectionRow) {
                    headerCell.setText(LocaleController.getString("DataUsagePayment", R.string.DataUsagePayment));
                } else if (position == DataUsageActivity.this.secureSectionRow) {
                    headerCell.setText(LocaleController.getString("DataUsageSecretChat", R.string.DataUsageSecretChat));
                }
            }
        }

        public int getItemCount() {
            return DataUsageActivity.this.rowCount;
        }
    }
}
