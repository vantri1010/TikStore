package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.EditTextSettingsCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;

@Deprecated
public class QuickRepliesSettingsActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int explanationRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int reply1Row;
    /* access modifiers changed from: private */
    public int reply2Row;
    /* access modifiers changed from: private */
    public int reply3Row;
    /* access modifiers changed from: private */
    public int reply4Row;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public EditTextSettingsCell[] textCells = new EditTextSettingsCell[4];

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.reply1Row = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.reply2Row = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.reply3Row = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.reply4Row = i3;
        this.rowCount = i4 + 1;
        this.explanationRow = i4;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    QuickRepliesSettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        return this.fragmentView;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        SharedPreferences.Editor editor = getParentActivity().getSharedPreferences("mainconfig", 0).edit();
        int i = 0;
        while (true) {
            EditTextSettingsCell[] editTextSettingsCellArr = this.textCells;
            if (i < editTextSettingsCellArr.length) {
                if (editTextSettingsCellArr[i] != null) {
                    String text = editTextSettingsCellArr[i].getTextView().getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        editor.putString("quick_reply_msg" + (i + 1), text);
                    } else {
                        editor.remove("quick_reply_msg" + (i + 1));
                    }
                }
                i++;
            } else {
                editor.commit();
                return;
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return QuickRepliesSettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                cell.setText(LocaleController.getString("VoipQuickRepliesExplain", R.string.VoipQuickRepliesExplain));
            } else if (itemViewType == 1) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
            } else if (itemViewType != 4) {
                switch (itemViewType) {
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        EditTextSettingsCell textCell = (EditTextSettingsCell) holder.itemView;
                        String settingsKey = null;
                        String defValue = null;
                        if (position == QuickRepliesSettingsActivity.this.reply1Row) {
                            settingsKey = "quick_reply_msg1";
                            defValue = LocaleController.getString("QuickReplyDefault1", R.string.QuickReplyDefault1);
                        } else if (position == QuickRepliesSettingsActivity.this.reply2Row) {
                            settingsKey = "quick_reply_msg2";
                            defValue = LocaleController.getString("QuickReplyDefault2", R.string.QuickReplyDefault2);
                        } else if (position == QuickRepliesSettingsActivity.this.reply3Row) {
                            settingsKey = "quick_reply_msg3";
                            defValue = LocaleController.getString("QuickReplyDefault3", R.string.QuickReplyDefault3);
                        } else if (position == QuickRepliesSettingsActivity.this.reply4Row) {
                            settingsKey = "quick_reply_msg4";
                            defValue = LocaleController.getString("QuickReplyDefault4", R.string.QuickReplyDefault4);
                        }
                        textCell.setTextAndHint(QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getString(settingsKey, ""), defValue, true);
                        return;
                    default:
                        return;
                }
            } else {
                ((TextCheckCell) holder.itemView).setTextAndCheck(LocaleController.getString("AllowCustomQuickReply", R.string.AllowCustomQuickReply), QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getBoolean("quick_reply_allow_custom", true), false);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == QuickRepliesSettingsActivity.this.reply1Row || position == QuickRepliesSettingsActivity.this.reply2Row || position == QuickRepliesSettingsActivity.this.reply3Row || position == QuickRepliesSettingsActivity.this.reply4Row;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (viewType == 1) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType != 4) {
                switch (viewType) {
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        view = new EditTextSettingsCell(this.mContext);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        QuickRepliesSettingsActivity.this.textCells[viewType - 9] = (EditTextSettingsCell) view;
                        break;
                }
            } else {
                view = new TextCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == QuickRepliesSettingsActivity.this.explanationRow) {
                return 0;
            }
            if (position == QuickRepliesSettingsActivity.this.reply1Row || position == QuickRepliesSettingsActivity.this.reply2Row || position == QuickRepliesSettingsActivity.this.reply3Row || position == QuickRepliesSettingsActivity.this.reply4Row) {
                return (position - QuickRepliesSettingsActivity.this.reply1Row) + 9;
            }
            return 1;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, EditTextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText)};
    }
}
