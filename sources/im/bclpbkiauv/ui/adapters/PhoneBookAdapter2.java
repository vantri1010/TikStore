package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Marker;

public class PhoneBookAdapter2 extends RecyclerListView.SectionsAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private Context mContext;

    public PhoneBookAdapter2(Context context) {
        this.mContext = context;
    }

    public Object getItem(int section, int position) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section >= sortedUsersSectionsArray.size()) {
            return null;
        }
        ArrayList<TLRPC.Contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
        if (position < arr.size()) {
            return arr.get(position);
        }
        return null;
    }

    public boolean isEnabled(int section, int row) {
        return row < ContactsController.getInstance(this.currentAccount).usersSectionsDict.get(ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray.get(section)).size();
    }

    public int getSectionCount() {
        return ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray.size();
    }

    public int getCountForSection(int section) {
        ArrayList<TLRPC.Contact> arr;
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section >= sortedUsersSectionsArray.size() || (arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section))) == null) {
            return 0;
        }
        int count = arr.size();
        if (section != sortedUsersSectionsArray.size() - 1) {
            return count + 1;
        }
        return count;
    }

    public View getSectionHeaderView(int section, View view) {
        HashMap<String, ArrayList<TLRPC.Contact>> hashMap = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell cell = (LetterSectionCell) view;
        if (section < sortedUsersSectionsArray.size()) {
            cell.setLetter(sortedUsersSectionsArray.get(section));
        } else {
            cell.setLetter("");
        }
        return view;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType != 0) {
            view = new DividerCell(this.mContext);
            float f = 28.0f;
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f);
            int dp2 = AndroidUtilities.dp(8.0f);
            if (LocaleController.isRTL) {
                f = 72.0f;
            }
            view.setPadding(dp, dp2, AndroidUtilities.dp(f), AndroidUtilities.dp(8.0f));
        } else {
            view = new UserCell(this.mContext, 58, 1, false);
            ((UserCell) view).setNameTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() == 0) {
            UserCell userCell = (UserCell) holder.itemView;
            Object object = getItem(section, position);
            TLRPC.User user = null;
            String str = "";
            if (object instanceof ContactsController.Contact) {
                ContactsController.Contact contact = (ContactsController.Contact) object;
                if (contact.user != null) {
                    user = contact.user;
                } else {
                    userCell.setCurrentId(contact.contact_id);
                    userCell.setData((TLObject) null, ContactsController.formatName(contact.first_name, contact.last_name), contact.phones.isEmpty() ? str : PhoneFormat.getInstance().format(contact.phones.get(0)), 0);
                }
            } else if (object instanceof TLRPC.Contact) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.Contact) object).user_id));
            } else {
                user = (TLRPC.User) object;
            }
            if (user != null) {
                if (!TextUtils.isEmpty(user.phone)) {
                    PhoneFormat instance = PhoneFormat.getInstance();
                    str = instance.format(Marker.ANY_NON_NULL_MARKER + user.phone);
                }
                userCell.setData(user, (CharSequence) null, str, 0);
            }
        }
    }

    public int getItemViewType(int section, int position) {
        return position < ContactsController.getInstance(this.currentAccount).usersSectionsDict.get(ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray.get(section)).size() ? 0 : 1;
    }

    public String getLetter(int position) {
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        int section = getSectionForPosition(position);
        if (section == -1) {
            section = sortedUsersSectionsArray.size() - 1;
        }
        if (section < 0 || section >= sortedUsersSectionsArray.size()) {
            return null;
        }
        return sortedUsersSectionsArray.get(section);
    }

    public int getPositionForScrollProgress(float progress) {
        return (int) (((float) getItemCount()) * progress);
    }
}
