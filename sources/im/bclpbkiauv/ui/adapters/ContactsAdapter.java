package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
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
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ContactsAdapter extends RecyclerListView.SectionsAdapter {
    private SparseArray<?> checkedMap;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean disableSections;
    private boolean hasGps;
    private SparseArray<TLRPC.User> ignoreUsers;
    private boolean isAdmin;
    private boolean isChannel;
    private Context mContext;
    private boolean needPhonebook;
    private ArrayList<TLRPC.Contact> onlineContacts;
    private int onlyUsers;
    private boolean scrolling;
    private int sortType;

    public ContactsAdapter(Context context, int onlyUsersType, boolean arg2, SparseArray<TLRPC.User> arg3, int arg4, boolean gps) {
        this.mContext = context;
        this.onlyUsers = onlyUsersType;
        this.needPhonebook = arg2;
        this.ignoreUsers = arg3;
        boolean z = true;
        this.isAdmin = arg4 != 0;
        this.isChannel = arg4 != 2 ? false : z;
        this.hasGps = gps;
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
                        return ContactsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, this.f$1, (TLRPC.Contact) obj, (TLRPC.Contact) obj2);
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
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (section < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.Contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
                if (position < arr.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arr.get(position).user_id));
                }
            }
            return null;
        } else if (section == 0) {
            return null;
        } else {
            if (this.sortType == 2) {
                if (section == 1) {
                    if (position < this.onlineContacts.size()) {
                        return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(position).user_id));
                    }
                    return null;
                }
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.Contact> arr2 = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                if (position < arr2.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arr2.get(position).user_id));
                }
                return null;
            }
            if (this.needPhonebook) {
                return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
            }
            return null;
        }
    }

    public boolean isEnabled(int section, int row) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section != 0) {
                if (this.sortType == 2) {
                    if (section != 1 || row < this.onlineContacts.size()) {
                        return true;
                    }
                    return false;
                } else if (section - 1 >= sortedUsersSectionsArray.size() || row < usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1)).size()) {
                    return true;
                } else {
                    return false;
                }
                return true;
            } else if (this.isAdmin) {
                if (row != 1) {
                    return true;
                }
                return false;
            } else if (this.needPhonebook) {
                if ((!this.hasGps || row == 2) && (this.hasGps || row == 1)) {
                    return false;
                }
                return true;
            } else if (row != 3) {
                return true;
            } else {
                return false;
            }
        } else if (row < usersSectionsDict.get(sortedUsersSectionsArray.get(section)).size()) {
            return true;
        } else {
            return false;
        }
    }

    public int getSectionCount() {
        int count;
        if (this.sortType == 2) {
            count = 1;
        } else {
            count = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).size();
        }
        if (this.onlyUsers == 0) {
            count++;
        }
        if (this.isAdmin) {
            return count + 1;
        }
        return count;
    }

    public int getCountForSection(int section) {
        ArrayList<TLRPC.Contact> arr;
        ArrayList<TLRPC.Contact> arr2;
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                if (this.isAdmin) {
                    return 2;
                }
                if (!this.needPhonebook) {
                    return 4;
                }
                if (this.hasGps) {
                    return 3;
                }
                return 2;
            } else if (this.sortType == 2) {
                if (section == 1) {
                    if (this.onlineContacts.isEmpty()) {
                        return 0;
                    }
                    return this.onlineContacts.size() + 1;
                }
            } else if (section - 1 < sortedUsersSectionsArray.size() && (arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1))) != null) {
                int count = arr.size();
                if (section - 1 != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
                    return count + 1;
                }
                return count;
            }
        } else if (section < sortedUsersSectionsArray.size() && (arr2 = usersSectionsDict.get(sortedUsersSectionsArray.get(section))) != null) {
            int count2 = arr2.size();
            if (section != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
                return count2 + 1;
            }
            return count2;
        }
        if (this.needPhonebook) {
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.size();
        }
        return 0;
    }

    public View getSectionHeaderView(int section, View view) {
        if (this.onlyUsers == 2) {
            HashMap<String, ArrayList<TLRPC.Contact>> hashMap = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
        } else {
            HashMap<String, ArrayList<TLRPC.Contact>> hashMap2 = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        }
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell cell = (LetterSectionCell) view;
        if (this.sortType == 2 || this.disableSections) {
            cell.setLetter("");
        } else if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                cell.setLetter("");
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                cell.setLetter(sortedUsersSectionsArray.get(section - 1));
            } else {
                cell.setLetter("");
            }
        } else if (section < sortedUsersSectionsArray.size()) {
            cell.setLetter(sortedUsersSectionsArray.get(section));
        } else {
            cell.setLetter("");
        }
        return view;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = new UserCell(this.mContext, 58, 1, false);
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        } else if (viewType == 1) {
            view = new TextCell(this.mContext);
        } else if (viewType != 2) {
            view = new View(this.mContext);
        } else {
            view = new GraySectionCell(this.mContext);
        }
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
        ArrayList<TLRPC.Contact> arr;
        int itemViewType = holder.getItemViewType();
        boolean z = false;
        if (itemViewType == 0) {
            UserCell userCell = (UserCell) holder.itemView;
            userCell.setAvatarPadding((this.sortType == 2 || this.disableSections) ? 6 : 58);
            if (this.sortType == 2) {
                arr = this.onlineContacts;
            } else {
                arr = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict).get((this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).get(section - ((this.onlyUsers == 0 || this.isAdmin) ? 1 : 0)));
            }
            if (arr != null) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arr.get(position).user_id));
                userCell.setData(user, (CharSequence) null, (CharSequence) null, 0, (section == getSectionCount() - 1 && position == arr.size() - 1) ? false : true);
                SparseArray<?> sparseArray = this.checkedMap;
                if (sparseArray != null) {
                    if (sparseArray.indexOfKey(user.id) >= 0) {
                        z = true;
                    }
                    userCell.setChecked(z, !this.scrolling);
                }
                SparseArray<TLRPC.User> sparseArray2 = this.ignoreUsers;
                if (sparseArray2 != null) {
                    if (sparseArray2.indexOfKey(user.id) >= 0) {
                        userCell.setAlpha(0.5f);
                    } else {
                        userCell.setAlpha(1.0f);
                    }
                }
                if (getItemCount() == 1) {
                    userCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (section == 0 && position == 0) {
                    userCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (section == getSectionCount() - 1 && position == arr.size() - 1) {
                    userCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            }
        } else if (itemViewType == 1) {
            TextCell textCell = (TextCell) holder.itemView;
            if (section != 0) {
                ContactsController.Contact contact = ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
                if (contact.first_name != null && contact.last_name != null) {
                    textCell.setText(contact.first_name + " " + contact.last_name, false);
                } else if (contact.first_name != null) {
                    textCell.setText(contact.first_name, false);
                } else {
                    textCell.setText(contact.last_name, false);
                }
            } else if (this.needPhonebook) {
                if (position == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite, false);
                } else if (position == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("AddPeopleNearby", R.string.AddPeopleNearby), R.drawable.menu_location, false);
                }
            } else if (this.isAdmin) {
                if (this.isChannel) {
                    textCell.setTextAndIcon(LocaleController.getString("ChannelInviteViaLink", R.string.ChannelInviteViaLink), R.drawable.profile_link, false);
                } else {
                    textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink), R.drawable.profile_link, false);
                }
            } else if (position == 0) {
                textCell.setTextAndIcon(LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_groups, false);
            } else if (position == 1) {
                textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret, false);
            } else if (position == 2) {
                textCell.setTextAndIcon(LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast, false);
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

    public int getItemViewType(int section, int position) {
        ArrayList<TLRPC.Contact> arr;
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            ArrayList<TLRPC.Contact> arr2 = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
            if (arr2 != null) {
                if (position < arr2.size()) {
                    return 0;
                }
                return 3;
            }
        } else if (section == 0) {
            if (this.isAdmin) {
                if (position == 1) {
                    return 2;
                }
            } else if (this.needPhonebook) {
                if ((!this.hasGps || position != 2) && (this.hasGps || position != 1)) {
                    return 1;
                }
                return 2;
            } else if (position == 3) {
                return 2;
            }
        } else if (this.sortType == 2) {
            if (section == 1) {
                if (position < this.onlineContacts.size()) {
                    return 0;
                }
                return 3;
            }
        } else if (section - 1 < sortedUsersSectionsArray.size() && (arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1))) != null) {
            if (position < arr.size()) {
                return 0;
            }
            return 3;
        }
        return 1;
    }

    public String getLetter(int position) {
        if (this.sortType == 2) {
            return null;
        }
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        int section = getSectionForPosition(position);
        if (section == -1) {
            section = sortedUsersSectionsArray.size() - 1;
        }
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section > 0 && section <= sortedUsersSectionsArray.size()) {
                return sortedUsersSectionsArray.get(section - 1);
            }
        } else if (section >= 0 && section < sortedUsersSectionsArray.size()) {
            return sortedUsersSectionsArray.get(section);
        }
        return null;
    }

    public int getPositionForScrollProgress(float progress) {
        return (int) (((float) getItemCount()) * progress);
    }
}
