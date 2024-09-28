package im.bclpbkiauv.ui.fragments.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
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
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FmtContactsAdapter extends RecyclerListView.SectionsAdapter {
    private SparseArray<?> checkedMap;
    private int classGuid;
    private int currentAccount = UserConfig.selectedAccount;
    private FmtContactsAdapterDelegate delegate;
    private boolean disableSections;
    private boolean hasGps;
    private OnContactHeaderItemClickListener headerListener;
    private SparseArray<TLRPC.User> ignoreUsers;
    private boolean isAdmin;
    private boolean isChannel;
    private Context mContext;
    private boolean needPhonebook;
    private ArrayList<TLRPC.Contact> onlineContacts;
    private int onlyUsers;
    private boolean scrolling;
    private int sortType;
    private HashMap<Integer, Integer> userPositionMap = new HashMap<>();

    public interface FmtContactsAdapterDelegate {
        void onDeleteItem(int i);
    }

    public interface OnContactHeaderItemClickListener {
        void onItemClick(View view);
    }

    public FmtContactsAdapter(Context mContext2, int onlyUsersType, boolean needPhonebook2, SparseArray<TLRPC.User> ignores, int flags, boolean hasGps2) {
        this.mContext = mContext2;
        this.onlyUsers = onlyUsersType;
        this.needPhonebook = needPhonebook2;
        this.ignoreUsers = ignores;
        boolean z = true;
        this.isAdmin = flags != 0;
        this.isChannel = flags != 2 ? false : z;
        this.hasGps = hasGps2;
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
                int i = 0;
                int len = this.onlineContacts.size();
                while (true) {
                    if (i >= len) {
                        break;
                    } else if (this.onlineContacts.get(i).user_id == selfId) {
                        this.onlineContacts.remove(i);
                        break;
                    } else {
                        i++;
                    }
                }
            }
            sortOnlineContacts();
            return;
        }
        notifyDataSetChanged();
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
                        return FmtContactsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, this.f$1, (TLRPC.Contact) obj, (TLRPC.Contact) obj2);
                    }
                });
                notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ int lambda$sortOnlineContacts$0(MessagesController messagesController, int currentTime, TLRPC.Contact objPre, TLRPC.Contact objNext) {
        TLRPC.User userPre = messagesController.getUser(Integer.valueOf(objPre.user_id));
        TLRPC.User userNext = messagesController.getUser(Integer.valueOf(objNext.user_id));
        int status1 = 0;
        int status2 = 0;
        if (userPre != null) {
            if (userPre.self) {
                status1 = currentTime + 50000;
            } else if (userPre.status != null) {
                status1 = userPre.status.expires;
            }
        }
        if (userNext != null) {
            if (userNext.self) {
                status2 = currentTime + 50000;
            } else if (userNext.status != null) {
                status2 = userNext.status.expires;
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

    public int getSectionCount() {
        int count = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).size();
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
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section == 0) {
            return 2;
        }
        if (section - 1 < sortedUsersSectionsArray.size() && (arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1))) != null) {
            int count = arr.size();
            if (section - 1 == sortedUsersSectionsArray.size() - 1) {
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
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                return false;
            }
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
        } else if (row < usersSectionsDict.get(sortedUsersSectionsArray.get(section)).size()) {
            return true;
        } else {
            return false;
        }
    }

    public int getItemViewType(int section, int position) {
        HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (section == 0) {
            if (position == 0) {
                return 1;
            }
            return 2;
        } else if (section - 1 >= sortedUsersSectionsArray.size()) {
            return 1;
        } else {
            ArrayList<TLRPC.Contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
            if (section - 1 == sortedUsersSectionsArray.size() - 1) {
                if (position < arr.size()) {
                    return 0;
                }
                return 5;
            } else if (position < arr.size()) {
                return 0;
            } else {
                return 4;
            }
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            View view2 = LayoutInflater.from(this.mContext).inflate(R.layout.item_contacts_layout, (ViewGroup) null);
            view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            view = view2;
        } else if (viewType == 1) {
            View view3 = LayoutInflater.from(this.mContext).inflate(R.layout.item_contacts_header, parent, false);
            view3.setMinimumHeight(AndroidUtilities.dp(105.0f));
            view3.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
            view = view3;
        } else if (viewType == 2) {
            view = new EmptyCell(this.mContext, AndroidUtilities.dp(10.0f));
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        } else if (viewType == 3) {
            view = new EmptyCell(this.mContext, AndroidUtilities.dp(46.0f));
            view.setBackgroundColor(16776960);
        } else if (viewType != 5) {
            view = new View(this.mContext);
            view.setBackgroundColor(16760097);
            view.setMinimumHeight(AndroidUtilities.dp(48.0f));
            float f = 28.0f;
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f);
            int dp2 = AndroidUtilities.dp(8.0f);
            if (LocaleController.isRTL) {
                f = 72.0f;
            }
            view.setPadding(dp, dp2, AndroidUtilities.dp(f), AndroidUtilities.dp(8.0f));
        } else {
            view = new MryTextView(this.mContext);
            view.setBackgroundColor(0);
            view.setMinimumHeight(AndroidUtilities.dp(70.0f));
            ((MryTextView) view).setGravity(17);
            ((MryTextView) view).setTextSize(13.0f);
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(view);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: int} */
    /* JADX WARNING: type inference failed for: r1v0 */
    /* JADX WARNING: type inference failed for: r1v7 */
    /* JADX WARNING: type inference failed for: r1v13 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(int r12, int r13, androidx.recyclerview.widget.RecyclerView.ViewHolder r14) {
        /*
            r11 = this;
            int r0 = r14.getItemViewType()
            r1 = 0
            r2 = 2
            r3 = 1
            if (r0 == 0) goto L_0x00df
            if (r0 == r3) goto L_0x006c
            r1 = 5
            if (r0 == r1) goto L_0x0010
            goto L_0x01f9
        L_0x0010:
            android.view.View r0 = r14.itemView
            im.bclpbkiauv.ui.hviews.MryTextView r0 = (im.bclpbkiauv.ui.hviews.MryTextView) r0
            java.lang.String r1 = "windowBackgroundWhiteGrayText8"
            r0.setTextColor(r1)
            int r1 = r11.onlyUsers
            if (r1 != r2) goto L_0x0026
            int r1 = r11.currentAccount
            im.bclpbkiauv.messenger.ContactsController r1 = im.bclpbkiauv.messenger.ContactsController.getInstance(r1)
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Contact>> r1 = r1.usersMutualSectionsDict
            goto L_0x002e
        L_0x0026:
            int r1 = r11.currentAccount
            im.bclpbkiauv.messenger.ContactsController r1 = im.bclpbkiauv.messenger.ContactsController.getInstance(r1)
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Contact>> r1 = r1.usersSectionsDict
        L_0x002e:
            r2 = 0
            if (r1 == 0) goto L_0x0051
            java.util.Set r3 = r1.entrySet()
            java.util.Iterator r3 = r3.iterator()
        L_0x0039:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0051
            java.lang.Object r4 = r3.next()
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4
            java.lang.Object r5 = r4.getValue()
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            int r5 = r5.size()
            int r2 = r2 + r5
            goto L_0x0039
        L_0x0051:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r4 = 2131690739(0x7f0f04f3, float:1.901053E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.setText(r3)
            goto L_0x01f9
        L_0x006c:
            if (r12 != 0) goto L_0x01f9
            boolean r0 = r11.needPhonebook
            if (r0 == 0) goto L_0x01f9
            android.view.View r0 = r14.itemView
            r2 = 2131296946(0x7f0902b2, float:1.8211823E38)
            android.view.View r0 = r0.findViewById(r2)
            android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0
            android.view.View r2 = r14.itemView
            r3 = 2131296942(0x7f0902ae, float:1.8211815E38)
            android.view.View r2 = r2.findViewById(r3)
            android.widget.LinearLayout r2 = (android.widget.LinearLayout) r2
            android.view.View r3 = r14.itemView
            r4 = 2131296941(0x7f0902ad, float:1.8211813E38)
            android.view.View r3 = r3.findViewById(r4)
            android.widget.LinearLayout r3 = (android.widget.LinearLayout) r3
            android.view.View r4 = r14.itemView
            r5 = 2131296940(0x7f0902ac, float:1.821181E38)
            android.view.View r4 = r4.findViewById(r5)
            android.widget.LinearLayout r4 = (android.widget.LinearLayout) r4
            android.view.View r5 = r14.itemView
            r6 = 2131296850(0x7f090252, float:1.8211628E38)
            android.view.View r5 = r5.findViewById(r6)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            int r6 = r11.currentAccount
            android.content.SharedPreferences r6 = im.bclpbkiauv.messenger.MessagesController.getMainSettings(r6)
            java.lang.String r7 = "contacts_apply_count"
            int r7 = r6.getInt(r7, r1)
            if (r7 <= 0) goto L_0x00b8
            goto L_0x00ba
        L_0x00b8:
            r1 = 8
        L_0x00ba:
            r5.setVisibility(r1)
            im.bclpbkiauv.ui.fragments.adapter.-$$Lambda$FmtContactsAdapter$hC-gCShkzgTgxhI52Yfh4lDbuwY r1 = new im.bclpbkiauv.ui.fragments.adapter.-$$Lambda$FmtContactsAdapter$hC-gCShkzgTgxhI52Yfh4lDbuwY
            r1.<init>()
            r0.setOnClickListener(r1)
            im.bclpbkiauv.ui.fragments.adapter.-$$Lambda$FmtContactsAdapter$AbYLZrVsIZI8A80Bpt5LUvFrDHM r1 = new im.bclpbkiauv.ui.fragments.adapter.-$$Lambda$FmtContactsAdapter$AbYLZrVsIZI8A80Bpt5LUvFrDHM
            r1.<init>()
            r2.setOnClickListener(r1)
            im.bclpbkiauv.ui.fragments.adapter.-$$Lambda$FmtContactsAdapter$IRbqBR4gYx4n72-SKCp7Vk6fubs r1 = new im.bclpbkiauv.ui.fragments.adapter.-$$Lambda$FmtContactsAdapter$IRbqBR4gYx4n72-SKCp7Vk6fubs
            r1.<init>()
            r3.setOnClickListener(r1)
            im.bclpbkiauv.ui.fragments.adapter.-$$Lambda$FmtContactsAdapter$JZr2wNWxkTVfRpkBEZ6Sadavb_A r1 = new im.bclpbkiauv.ui.fragments.adapter.-$$Lambda$FmtContactsAdapter$JZr2wNWxkTVfRpkBEZ6Sadavb_A
            r1.<init>()
            r4.setOnClickListener(r1)
            goto L_0x01f9
        L_0x00df:
            android.view.View r0 = r14.itemView
            r4 = 2131296476(0x7f0900dc, float:1.821087E38)
            android.view.View r0 = r0.findViewById(r4)
            im.bclpbkiauv.ui.hcells.ContactUserCell r0 = (im.bclpbkiauv.ui.hcells.ContactUserCell) r0
            int r4 = r11.sortType
            if (r4 == r2) goto L_0x00f6
            boolean r4 = r11.disableSections
            if (r4 == 0) goto L_0x00f3
            goto L_0x00f6
        L_0x00f3:
            r4 = 58
            goto L_0x00f7
        L_0x00f6:
            r4 = 6
        L_0x00f7:
            r0.setAvatarPadding(r4)
            int r4 = r11.sortType
            if (r4 != r2) goto L_0x0101
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Contact> r2 = r11.onlineContacts
            goto L_0x0143
        L_0x0101:
            int r4 = r11.onlyUsers
            if (r4 != r2) goto L_0x010e
            int r4 = r11.currentAccount
            im.bclpbkiauv.messenger.ContactsController r4 = im.bclpbkiauv.messenger.ContactsController.getInstance(r4)
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Contact>> r4 = r4.usersMutualSectionsDict
            goto L_0x0116
        L_0x010e:
            int r4 = r11.currentAccount
            im.bclpbkiauv.messenger.ContactsController r4 = im.bclpbkiauv.messenger.ContactsController.getInstance(r4)
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Contact>> r4 = r4.usersSectionsDict
        L_0x0116:
            int r5 = r11.onlyUsers
            if (r5 != r2) goto L_0x0123
            int r2 = r11.currentAccount
            im.bclpbkiauv.messenger.ContactsController r2 = im.bclpbkiauv.messenger.ContactsController.getInstance(r2)
            java.util.ArrayList<java.lang.String> r2 = r2.sortedUsersMutualSectionsArray
            goto L_0x012b
        L_0x0123:
            int r2 = r11.currentAccount
            im.bclpbkiauv.messenger.ContactsController r2 = im.bclpbkiauv.messenger.ContactsController.getInstance(r2)
            java.util.ArrayList<java.lang.String> r2 = r2.sortedUsersSectionsArray
        L_0x012b:
            int r5 = r11.onlyUsers
            if (r5 == 0) goto L_0x0135
            boolean r5 = r11.isAdmin
            if (r5 != 0) goto L_0x0135
            r5 = 0
            goto L_0x0136
        L_0x0135:
            r5 = 1
        L_0x0136:
            int r5 = r12 - r5
            java.lang.Object r5 = r2.get(r5)
            java.lang.Object r5 = r4.get(r5)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            r2 = r5
        L_0x0143:
            int r4 = r11.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            java.lang.Object r5 = r2.get(r13)
            im.bclpbkiauv.tgnet.TLRPC$Contact r5 = (im.bclpbkiauv.tgnet.TLRPC.Contact) r5
            int r5 = r5.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            im.bclpbkiauv.tgnet.TLRPC$User r10 = r4.getUser(r5)
            int r4 = r11.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            int r5 = r10.id
            im.bclpbkiauv.tgnet.TLRPC$UserFull r4 = r4.getUserFull(r5)
            if (r4 != 0) goto L_0x0173
            int r4 = r11.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            int r5 = r11.classGuid
            r4.loadUserInfo(r10, r3, r5)
            goto L_0x0182
        L_0x0173:
            int r4 = r11.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            int r5 = r10.id
            im.bclpbkiauv.tgnet.TLRPC$UserFull r4 = r4.getUserFull(r5)
            r0.setUserFull(r4)
        L_0x0182:
            int r4 = r11.getSectionCount()
            int r4 = r4 - r3
            if (r12 != r4) goto L_0x01b1
            int r4 = r2.size()
            int r4 = r4 - r3
            if (r13 != r4) goto L_0x01b1
            r4 = 0
            r0.setData(r10, r4, r4, r1)
            android.view.View r4 = r14.itemView
            r5 = 1084227584(0x40a00000, float:5.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            java.lang.String r7 = "windowBackgroundWhite"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r8 = 0
            android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r8, r8, r6, r5, r7)
            r4.setBackground(r5)
            goto L_0x01ba
        L_0x01b1:
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 1
            r4 = r0
            r5 = r10
            r4.setData(r5, r6, r7, r8, r9)
        L_0x01ba:
            android.util.SparseArray<?> r4 = r11.checkedMap
            if (r4 == 0) goto L_0x01cd
            int r5 = r10.id
            int r4 = r4.indexOfKey(r5)
            if (r4 < 0) goto L_0x01c7
            r1 = 1
        L_0x01c7:
            boolean r4 = r11.scrolling
            r3 = r3 ^ r4
            r0.setChecked(r1, r3)
        L_0x01cd:
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$User> r1 = r11.ignoreUsers
            if (r1 == 0) goto L_0x01e4
            int r3 = r10.id
            int r1 = r1.indexOfKey(r3)
            if (r1 < 0) goto L_0x01df
            r1 = 1056964608(0x3f000000, float:0.5)
            r0.setAlpha(r1)
            goto L_0x01e4
        L_0x01df:
            r1 = 1065353216(0x3f800000, float:1.0)
            r0.setAlpha(r1)
        L_0x01e4:
            java.util.HashMap<java.lang.Integer, java.lang.Integer> r1 = r11.userPositionMap
            int r3 = r10.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            int r4 = r11.getPositionForSection(r12)
            int r4 = r4 + r13
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r1.put(r3, r4)
        L_0x01f9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.fragments.adapter.FmtContactsAdapter.onBindViewHolder(int, int, androidx.recyclerview.widget.RecyclerView$ViewHolder):void");
    }

    public /* synthetic */ void lambda$onBindViewHolder$1$FmtContactsAdapter(View v) {
        OnContactHeaderItemClickListener onContactHeaderItemClickListener = this.headerListener;
        if (onContactHeaderItemClickListener != null) {
            onContactHeaderItemClickListener.onItemClick(v);
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$2$FmtContactsAdapter(View v) {
        OnContactHeaderItemClickListener onContactHeaderItemClickListener = this.headerListener;
        if (onContactHeaderItemClickListener != null) {
            onContactHeaderItemClickListener.onItemClick(v);
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$3$FmtContactsAdapter(View v) {
        OnContactHeaderItemClickListener onContactHeaderItemClickListener = this.headerListener;
        if (onContactHeaderItemClickListener != null) {
            onContactHeaderItemClickListener.onItemClick(v);
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$4$FmtContactsAdapter(View v) {
        OnContactHeaderItemClickListener onContactHeaderItemClickListener = this.headerListener;
        if (onContactHeaderItemClickListener != null) {
            onContactHeaderItemClickListener.onItemClick(v);
        }
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

    public void setDelegate(FmtContactsAdapterDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setOnContactHeaderItemClickListener(OnContactHeaderItemClickListener listener) {
        this.headerListener = listener;
    }

    public void setClassGuid(int classGuid2) {
        this.classGuid = classGuid2;
    }

    public int getItemPosition(int userId) {
        if (this.userPositionMap.get(Integer.valueOf(userId)) != null) {
            return this.userPositionMap.get(Integer.valueOf(userId)).intValue();
        }
        return -1;
    }
}
