package im.bclpbkiauv.ui.hui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ColorTextView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hcells.UserBoxCell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CreateGroupAdapter extends RecyclerListView.SectionsAdapter {
    private SparseArray<?> checkedMap;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean disableSections;
    private Context mContext;
    private int miViewType = 0;
    private boolean needPhonebook = true;
    private ArrayList<TLRPC.Contact> onlineContacts;
    private boolean scrolling;
    private int sortType;

    public CreateGroupAdapter(Context context) {
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

    public void setMiViewType(int miViewType2) {
        this.miViewType = miViewType2;
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
                        return CreateGroupAdapter.lambda$sortOnlineContacts$0(MessagesController.this, this.f$1, (TLRPC.Contact) obj, (TLRPC.Contact) obj2);
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
        if (this.sortType == 2) {
            if (section == 1) {
                if (position < this.onlineContacts.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(position).user_id));
                }
                return null;
            }
        } else if (section < sortedUsersSectionsArray.size()) {
            ArrayList<TLRPC.Contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
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
        if (section < 0 || section > sortedUsersSectionsArray.size()) {
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
        if (section < sortedUsersSectionsArray.size() && (arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section))) != null) {
            int count = arr.size();
            if (section != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
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
        if (section >= sortedUsersSectionsArray.size() || row < usersSectionsDict.get(sortedUsersSectionsArray.get(section)).size()) {
            return true;
        }
        return false;
    }

    public int getItemViewType(int section, int position) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section < sortedUsersSectionsArray.size()) {
            return position < usersSectionsDict.get(sortedUsersSectionsArray.get(section)).size() ? 0 : 3;
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
        } else if (this.miViewType == 0) {
            view = new UserBoxCell(this.mContext, AndroidUtilities.dp(18.0f), 1, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recently_contacter, parent, false);
        }
        return new RecyclerListView.Holder(view);
    }

    public int getSectionForChar(char section) {
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        for (int i = 0; i <= getSectionCount() - 1; i++) {
            if (sortedUsersSectionsArray.get(i).toUpperCase().charAt(0) == section) {
                return i;
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
        TLRPC.User user;
        int i = section;
        int i2 = position;
        RecyclerView.ViewHolder viewHolder = holder;
        if (holder.getItemViewType() == 0) {
            if (this.miViewType == 0) {
                UserBoxCell userCell = (UserBoxCell) viewHolder.itemView;
                userCell.setAvatarPadding(45);
                ArrayList<TLRPC.Contact> arr = ContactsController.getInstance(this.currentAccount).usersSectionsDict.get(ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray.get(i));
                TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arr.get(i2).user_id));
                if (i == getSectionCount() - 1 && i2 == arr.size() - 1) {
                    userCell.setData(user2, (CharSequence) null, (CharSequence) null, 0);
                    user = user2;
                } else {
                    user = user2;
                    userCell.setData(user2, (CharSequence) null, (CharSequence) null, 0, true);
                }
                SparseArray<?> sparseArray = this.checkedMap;
                if (sparseArray != null) {
                    userCell.setChecked(sparseArray.indexOfKey(user.id) >= 0, true);
                    return;
                }
                return;
            }
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            ColorTextView colorTextView = (ColorTextView) viewHolder.itemView.findViewById(R.id.tv_name);
            ColorTextView tvstate = (ColorTextView) viewHolder.itemView.findViewById(R.id.tv_state);
            BackupImageView iv_Header = (BackupImageView) viewHolder.itemView.findViewById(R.id.iv_head_img);
            iv_Header.setRoundRadius(AndroidUtilities.dp(7.5f));
            TLRPC.User user3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(ContactsController.getInstance(this.currentAccount).usersSectionsDict.get(ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray.get(i)).get(i2).user_id));
            ((ColorTextView) viewHolder.itemView.findViewById(R.id.tv_person_name)).setText(user3.first_name);
            boolean[] booleans = {false};
            tvstate.setText(LocaleController.formatUserStatusNew(this.currentAccount, user3, booleans));
            if (booleans[0]) {
                tvstate.setTextColor(Color.parseColor("#42B71E"));
            } else {
                tvstate.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            }
            avatarDrawable.setInfo(user3);
            iv_Header.setImage(ImageLocation.getForUser(user3, false), "50_50", (Drawable) avatarDrawable, (Object) user3);
        }
    }
}
