package im.bclpbkiauv.ui.hui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hcells.IndexTextCell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class NewChatAdapter extends RecyclerListView.SectionsAdapter {
    private SparseArray<?> checkedMap;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean disableSections;
    private Context mContext;
    private boolean needPhonebook = true;
    private ArrayList<TLRPC.Contact> onlineContacts;
    private boolean scrolling;
    private int sortType;

    public NewChatAdapter(Context context) {
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
                        return NewChatAdapter.lambda$sortOnlineContacts$0(MessagesController.this, this.f$1, (TLRPC.Contact) obj, (TLRPC.Contact) obj2);
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

    public void setCheckedMap(SparseArray<?> map) {
        this.checkedMap = map;
    }

    public void setIsScrolling(boolean value) {
        this.scrolling = value;
    }

    public Object getItem(int section, int position) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section == 0) {
            return null;
        }
        if (this.sortType == 2) {
            if (section == 1) {
                if (position < this.onlineContacts.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(position).user_id));
                }
                return null;
            }
        } else if (section - 1 < sortedUsersSectionsArray.size()) {
            ArrayList<TLRPC.Contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
            if (position < arr.size()) {
                return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arr.get(position).user_id));
            }
            return null;
        }
        if (this.needPhonebook) {
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
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
        if (section <= 0 || section > sortedUsersSectionsArray.size()) {
            return null;
        }
        return sortedUsersSectionsArray.get(section - 1);
    }

    public int getPositionForScrollProgress(float progress) {
        return (int) (((float) getItemCount()) * progress);
    }

    public int getSectionCount() {
        return ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray.size() + 1;
    }

    public int getCountForSection(int section) {
        ArrayList<TLRPC.Contact> arr;
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section == 0) {
            return this.needPhonebook ? 3 : 3;
        }
        if (section - 1 < sortedUsersSectionsArray.size() && (arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1))) != null) {
            int count = arr.size();
            if (section - 1 != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
                return count + 1;
            }
            return count;
        } else if (this.needPhonebook) {
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.size();
        } else {
            return 0;
        }
    }

    public boolean isEnabled(int section, int row) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section == 0) {
            if (this.needPhonebook) {
                if (row != 3) {
                    return true;
                }
                return false;
            } else if (row != 3) {
                return true;
            } else {
                return false;
            }
        } else if (section - 1 >= sortedUsersSectionsArray.size() || row < usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1)).size()) {
            return true;
        } else {
            return false;
        }
    }

    public int getItemViewType(int section, int position) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section != 0 && section - 1 < sortedUsersSectionsArray.size()) {
            return position < usersSectionsDict.get(sortedUsersSectionsArray.get(section + -1)).size() ? 0 : 3;
        }
        return 1;
    }

    public View getSectionHeaderView(int section, View view) {
        HashMap<String, ArrayList<TLRPC.Contact>> hashMap = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell cell = (LetterSectionCell) view;
        if (section == 0) {
            cell.setLetter("");
        } else if (section - 1 < sortedUsersSectionsArray.size()) {
            cell.setLetter(sortedUsersSectionsArray.get(section - 1));
        } else {
            cell.setLetter("");
        }
        return view;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = new UserCell(this.mContext, 58, 1, false);
        } else if (viewType == 1) {
            view = new IndexTextCell(this.mContext);
        } else if (viewType != 2) {
            view = new View(this.mContext);
        } else {
            view = new GraySectionCell(this.mContext);
        }
        return new RecyclerListView.Holder(view);
    }

    public int getSectionForChar(char section) {
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        for (int i = 0; i < getSectionCount() - 1; i++) {
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
        int itemViewType = holder.getItemViewType();
        boolean z = false;
        if (itemViewType == 0) {
            UserCell userCell = (UserCell) holder.itemView;
            userCell.setAvatarPadding((this.sortType == 2 || this.disableSections) ? 6 : 58);
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(ContactsController.getInstance(this.currentAccount).usersSectionsDict.get(ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray.get(section - 1)).get(position).user_id));
            userCell.setData(user, (CharSequence) null, (CharSequence) null, 0);
            SparseArray<?> sparseArray = this.checkedMap;
            if (sparseArray != null) {
                if (sparseArray.indexOfKey(user.id) >= 0) {
                    z = true;
                }
                userCell.setChecked(z, true ^ this.scrolling);
            }
        } else if (itemViewType == 1) {
            IndexTextCell textCell = (IndexTextCell) holder.itemView;
            if (section != 0) {
                ContactsController.Contact contact = ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
                if (contact.first_name != null && contact.last_name != null) {
                    textCell.setText(contact.first_name + " " + contact.last_name, false);
                } else if (contact.first_name == null || contact.last_name != null) {
                    textCell.setText(contact.last_name, false);
                } else {
                    textCell.setText(contact.first_name, false);
                }
            } else if (this.needPhonebook) {
                if (position == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.mipmap.icon_new_secret_chat, true);
                } else if (position == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("NewGroup", R.string.NewGroup), R.mipmap.fmt_contacts_groups, true);
                } else if (position == 2) {
                    textCell.setTextAndIcon(LocaleController.getString("NewChannel", R.string.NewChannel), R.mipmap.fmt_contacts_channel, true);
                }
                textCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        } else if (itemViewType == 2) {
            GraySectionCell sectionCell = (GraySectionCell) holder.itemView;
            int i = this.sortType;
            if (i == 0) {
                sectionCell.setText(LocaleController.getString("Contacts", R.string.Contacts));
            } else if (i == 1) {
                sectionCell.setText(LocaleController.getString("SortedByName", R.string.SortedByName));
            } else {
                sectionCell.setText(LocaleController.getString("SortedByLastSeen", R.string.SortedByLastSeen));
            }
        }
    }
}
