package im.bclpbkiauv.ui.hui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class AddNewCallAdapter extends RecyclerListView.SectionsAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private boolean disableSections;
    private Context mContext;
    private ArrayList<TLRPC.Contact> onlineContacts;
    private int sortType;

    public AddNewCallAdapter(Context context) {
        this.mContext = context;
    }

    public void setDisableSections(boolean value) {
        this.disableSections = value;
    }

    public void setSortType(int value) {
        this.sortType = value;
        if (value == 2) {
            if (this.onlineContacts == null) {
                this.onlineContacts = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                int selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
                int a = 0;
                int N = this.onlineContacts.size();
                while (true) {
                    if (a >= N) {
                        break;
                    } else if (this.onlineContacts.get(a).user_id == selfId) {
                        this.onlineContacts.remove(a);
                        break;
                    } else {
                        a++;
                    }
                }
            }
            sortOnlineContacts();
            return;
        }
        notifyDataSetChanged();
    }

    public void sortOnlineContacts() {
        if (this.onlineContacts != null) {
            try {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                Collections.sort(this.onlineContacts, new Comparator(currentTime) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return AddNewCallAdapter.lambda$sortOnlineContacts$0(MessagesController.this, this.f$1, (TLRPC.Contact) obj, (TLRPC.Contact) obj2);
                    }
                });
                notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ int lambda$sortOnlineContacts$0(MessagesController messagesController, int currentTime, TLRPC.Contact o1, TLRPC.Contact o2) {
        TLRPC.User user1 = messagesController.getUser(Integer.valueOf(o2.user_id));
        TLRPC.User user2 = messagesController.getUser(Integer.valueOf(o1.user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                    return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
                }
                return -1;
            } else if (status1 > status2) {
                return 1;
            } else {
                return status1 < status2 ? -1 : 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            return status1 < status2 ? -1 : 0;
        }
    }

    public Object getItem(int section, int position) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.sortType == 2) {
            if (section != 0 || position >= this.onlineContacts.size()) {
                return null;
            }
            return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(position).user_id));
        } else if (section < sortedUsersSectionsArray.size()) {
            ArrayList<TLRPC.Contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
            if (position < arr.size()) {
                return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arr.get(position).user_id));
            }
            return null;
        }
        return null;
    }

    public String getLetter(int position) {
        if (this.sortType == 2) {
            return null;
        }
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

    public boolean isEnabled(int section, int row) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section >= sortedUsersSectionsArray.size() || row < usersSectionsDict.get(sortedUsersSectionsArray.get(section)).size()) {
            return true;
        }
        return false;
    }

    public int getItemViewType(int section, int position) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section >= sortedUsersSectionsArray.size() || position >= usersSectionsDict.get(sortedUsersSectionsArray.get(section)).size()) {
            return 1;
        }
        return 0;
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
            view = new View(this.mContext);
        } else {
            view = new UserCell(this.mContext, 58, 1, false);
        }
        return new RecyclerListView.Holder(view);
    }

    public int getSectionForChar(char section) {
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        for (int i = 0; i < getSectionCount(); i++) {
            if (sortedUsersSectionsArray.get(i).toUpperCase().charAt(0) == section) {
                return i + 1;
            }
        }
        return -1;
    }

    public int getPositionForSection(int section) {
        if (section == -1) {
            return -1;
        }
        int positionStart = 0;
        int N = getSectionCount();
        for (int i = 0; i < N; i++) {
            if (i >= section) {
                return positionStart;
            }
            positionStart += getCountForSection(i);
        }
        return -1;
    }

    public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() == 0) {
            UserCell userCell = (UserCell) holder.itemView;
            userCell.setStatusColors(-6710887, -12404962);
            userCell.setNameTypeface((Typeface) null);
            userCell.setAvatarPadding((this.sortType == 2 || this.disableSections) ? 6 : 58);
            ArrayList<TLRPC.Contact> arr = ContactsController.getInstance(this.currentAccount).usersSectionsDict.get(ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray.get(section));
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arr.get(position).user_id));
            if (section == getSectionCount() - 1 && position == arr.size() - 1) {
                userCell.setData(user, (CharSequence) null, (CharSequence) null, 0);
            } else {
                userCell.setData(user, (CharSequence) null, (CharSequence) null, 0, true);
            }
        }
    }
}
