package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.ui.AudioSelectActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.AudioCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.PickerBottomLayout;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;

public class AudioSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ArrayList<MediaController.AudioEntry> audioEntries = new ArrayList<>();
    private PickerBottomLayout bottomLayout;
    private AudioSelectActivityDelegate delegate;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingAudio;
    private ChatActivity parentFragment;
    /* access modifiers changed from: private */
    public MessageObject playingAudio;
    private EmptyTextProgressView progressView;
    /* access modifiers changed from: private */
    public LongSparseArray<MediaController.AudioEntry> selectedAudios = new LongSparseArray<>();
    private View shadow;

    public interface AudioSelectActivityDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList, boolean z, int i);
    }

    public AudioSelectActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        loadAudio();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        if (this.playingAudio != null && MediaController.getInstance().isPlayingMessage(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", R.string.AttachMusic));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    AudioSelectActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.progressView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoAudio", R.string.NoAudio));
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.progressView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                AudioSelectActivity.this.lambda$createView$0$AudioSelectActivity(view, i);
            }
        });
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(context, false);
        this.bottomLayout = pickerBottomLayout;
        frameLayout.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.cancelButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AudioSelectActivity.this.lambda$createView$1$AudioSelectActivity(view);
            }
        });
        this.bottomLayout.doneButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AudioSelectActivity.this.lambda$createView$3$AudioSelectActivity(view);
            }
        });
        View shadow2 = new View(context);
        shadow2.setBackgroundResource(R.drawable.header_shadow_reverse);
        frameLayout.addView(shadow2, LayoutHelper.createFrame(-1.0f, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        if (this.loadingAudio) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        updateBottomLayoutCount();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$AudioSelectActivity(View view, int position) {
        AudioCell audioCell = (AudioCell) view;
        MediaController.AudioEntry audioEntry = audioCell.getAudioEntry();
        if (this.selectedAudios.indexOfKey(audioEntry.id) >= 0) {
            this.selectedAudios.remove(audioEntry.id);
            audioCell.setChecked(false);
        } else {
            this.selectedAudios.put(audioEntry.id, audioEntry);
            audioCell.setChecked(true);
        }
        updateBottomLayoutCount();
    }

    public /* synthetic */ void lambda$createView$1$AudioSelectActivity(View view) {
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$3$AudioSelectActivity(View view) {
        ArrayList<MessageObject> audios = new ArrayList<>();
        for (int a = 0; a < this.selectedAudios.size(); a++) {
            audios.add(this.selectedAudios.valueAt(a).messageObject);
        }
        if (this.parentFragment.isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(audios) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectDate(boolean z, int i) {
                    AudioSelectActivity.this.lambda$null$2$AudioSelectActivity(this.f$1, z, i);
                }
            });
            return;
        }
        this.delegate.didSelectAudio(audios, true, 0);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$2$AudioSelectActivity(ArrayList audios, boolean notify, int scheduleDate) {
        this.delegate.didSelectAudio(audios, notify, scheduleDate);
        finishFragment();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ListAdapter listAdapter;
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if ((id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged) && (listAdapter = this.listViewAdapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateBottomLayoutCount() {
        this.bottomLayout.updateSelectedCount(this.selectedAudios.size(), true);
    }

    public void setDelegate(AudioSelectActivityDelegate audioSelectActivityDelegate) {
        this.delegate = audioSelectActivityDelegate;
    }

    private void loadAudio() {
        this.loadingAudio = true;
        EmptyTextProgressView emptyTextProgressView = this.progressView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showProgress();
        }
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                AudioSelectActivity.this.lambda$loadAudio$5$AudioSelectActivity();
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0159, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x015a, code lost:
        if (r1 != null) goto L_0x015c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0164, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadAudio$5$AudioSelectActivity() {
        /*
            r15 = this;
            java.lang.String r0 = "_id"
            java.lang.String r1 = "artist"
            java.lang.String r2 = "title"
            java.lang.String r3 = "_data"
            java.lang.String r4 = "duration"
            java.lang.String r5 = "album"
            java.lang.String[] r8 = new java.lang.String[]{r0, r1, r2, r3, r4, r5}
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.content.Context r1 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0165 }
            android.content.ContentResolver r6 = r1.getContentResolver()     // Catch:{ Exception -> 0x0165 }
            android.net.Uri r7 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x0165 }
            java.lang.String r9 = "is_music != 0"
            r10 = 0
            java.lang.String r11 = "title"
            android.database.Cursor r1 = r6.query(r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0165 }
            r2 = -2000000000(0xffffffff88ca6c00, float:-1.2182823E-33)
        L_0x002b:
            boolean r3 = r1.moveToNext()     // Catch:{ all -> 0x0157 }
            if (r3 == 0) goto L_0x0151
            im.bclpbkiauv.messenger.MediaController$AudioEntry r3 = new im.bclpbkiauv.messenger.MediaController$AudioEntry     // Catch:{ all -> 0x0157 }
            r3.<init>()     // Catch:{ all -> 0x0157 }
            r4 = 0
            int r5 = r1.getInt(r4)     // Catch:{ all -> 0x0157 }
            long r5 = (long) r5     // Catch:{ all -> 0x0157 }
            r3.id = r5     // Catch:{ all -> 0x0157 }
            r5 = 1
            java.lang.String r6 = r1.getString(r5)     // Catch:{ all -> 0x0157 }
            r3.author = r6     // Catch:{ all -> 0x0157 }
            r6 = 2
            java.lang.String r6 = r1.getString(r6)     // Catch:{ all -> 0x0157 }
            r3.title = r6     // Catch:{ all -> 0x0157 }
            r6 = 3
            java.lang.String r7 = r1.getString(r6)     // Catch:{ all -> 0x0157 }
            r3.path = r7     // Catch:{ all -> 0x0157 }
            r7 = 4
            long r9 = r1.getLong(r7)     // Catch:{ all -> 0x0157 }
            r11 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 / r11
            int r7 = (int) r9     // Catch:{ all -> 0x0157 }
            r3.duration = r7     // Catch:{ all -> 0x0157 }
            r7 = 5
            java.lang.String r7 = r1.getString(r7)     // Catch:{ all -> 0x0157 }
            r3.genre = r7     // Catch:{ all -> 0x0157 }
            java.io.File r7 = new java.io.File     // Catch:{ all -> 0x0157 }
            java.lang.String r9 = r3.path     // Catch:{ all -> 0x0157 }
            r7.<init>(r9)     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$TL_message r9 = new im.bclpbkiauv.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x0157 }
            r9.<init>()     // Catch:{ all -> 0x0157 }
            r9.out = r5     // Catch:{ all -> 0x0157 }
            r9.id = r2     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$TL_peerUser r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0157 }
            r5.<init>()     // Catch:{ all -> 0x0157 }
            r9.to_id = r5     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r9.to_id     // Catch:{ all -> 0x0157 }
            int r10 = r15.currentAccount     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.messenger.UserConfig r10 = im.bclpbkiauv.messenger.UserConfig.getInstance(r10)     // Catch:{ all -> 0x0157 }
            int r10 = r10.getClientUserId()     // Catch:{ all -> 0x0157 }
            r9.from_id = r10     // Catch:{ all -> 0x0157 }
            r5.user_id = r10     // Catch:{ all -> 0x0157 }
            long r13 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0157 }
            long r13 = r13 / r11
            int r5 = (int) r13     // Catch:{ all -> 0x0157 }
            r9.date = r5     // Catch:{ all -> 0x0157 }
            java.lang.String r5 = ""
            r9.message = r5     // Catch:{ all -> 0x0157 }
            java.lang.String r5 = r3.path     // Catch:{ all -> 0x0157 }
            r9.attachPath = r5     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaDocument r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ all -> 0x0157 }
            r5.<init>()     // Catch:{ all -> 0x0157 }
            r9.media = r5     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r9.media     // Catch:{ all -> 0x0157 }
            int r10 = r5.flags     // Catch:{ all -> 0x0157 }
            r10 = r10 | r6
            r5.flags = r10     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$TL_document r10 = new im.bclpbkiauv.tgnet.TLRPC$TL_document     // Catch:{ all -> 0x0157 }
            r10.<init>()     // Catch:{ all -> 0x0157 }
            r5.document = r10     // Catch:{ all -> 0x0157 }
            int r5 = r9.flags     // Catch:{ all -> 0x0157 }
            r5 = r5 | 768(0x300, float:1.076E-42)
            r9.flags = r5     // Catch:{ all -> 0x0157 }
            java.lang.String r5 = im.bclpbkiauv.messenger.FileLoader.getFileExtension(r7)     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r10 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0157 }
            r11 = 0
            r10.id = r11     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r10 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0157 }
            r10.access_hash = r11     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r10 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0157 }
            byte[] r11 = new byte[r4]     // Catch:{ all -> 0x0157 }
            r10.file_reference = r11     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r10 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0157 }
            int r11 = r9.date     // Catch:{ all -> 0x0157 }
            r10.date = r11     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r10 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0157 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0157 }
            r11.<init>()     // Catch:{ all -> 0x0157 }
            java.lang.String r12 = "audio/"
            r11.append(r12)     // Catch:{ all -> 0x0157 }
            int r12 = r5.length()     // Catch:{ all -> 0x0157 }
            if (r12 <= 0) goto L_0x00f1
            r12 = r5
            goto L_0x00f3
        L_0x00f1:
            java.lang.String r12 = "mp3"
        L_0x00f3:
            r11.append(r12)     // Catch:{ all -> 0x0157 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0157 }
            r10.mime_type = r11     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r10 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0157 }
            long r11 = r7.length()     // Catch:{ all -> 0x0157 }
            int r12 = (int) r11     // Catch:{ all -> 0x0157 }
            r10.size = r12     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r10 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0157 }
            r10.dc_id = r4     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAudio r10 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ all -> 0x0157 }
            r10.<init>()     // Catch:{ all -> 0x0157 }
            int r11 = r3.duration     // Catch:{ all -> 0x0157 }
            r10.duration = r11     // Catch:{ all -> 0x0157 }
            java.lang.String r11 = r3.title     // Catch:{ all -> 0x0157 }
            r10.title = r11     // Catch:{ all -> 0x0157 }
            java.lang.String r11 = r3.author     // Catch:{ all -> 0x0157 }
            r10.performer = r11     // Catch:{ all -> 0x0157 }
            int r11 = r10.flags     // Catch:{ all -> 0x0157 }
            r6 = r6 | r11
            r10.flags = r6     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Document r6 = r6.document     // Catch:{ all -> 0x0157 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r6 = r6.attributes     // Catch:{ all -> 0x0157 }
            r6.add(r10)     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeFilename r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeFilename     // Catch:{ all -> 0x0157 }
            r6.<init>()     // Catch:{ all -> 0x0157 }
            java.lang.String r11 = r7.getName()     // Catch:{ all -> 0x0157 }
            r6.file_name = r11     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r11 = r9.media     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.tgnet.TLRPC$Document r11 = r11.document     // Catch:{ all -> 0x0157 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r11 = r11.attributes     // Catch:{ all -> 0x0157 }
            r11.add(r6)     // Catch:{ all -> 0x0157 }
            im.bclpbkiauv.messenger.MessageObject r11 = new im.bclpbkiauv.messenger.MessageObject     // Catch:{ all -> 0x0157 }
            int r12 = r15.currentAccount     // Catch:{ all -> 0x0157 }
            r11.<init>(r12, r9, r4)     // Catch:{ all -> 0x0157 }
            r3.messageObject = r11     // Catch:{ all -> 0x0157 }
            r0.add(r3)     // Catch:{ all -> 0x0157 }
            int r2 = r2 + -1
            goto L_0x002b
        L_0x0151:
            if (r1 == 0) goto L_0x0156
            r1.close()     // Catch:{ Exception -> 0x0165 }
        L_0x0156:
            goto L_0x0169
        L_0x0157:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0159 }
        L_0x0159:
            r3 = move-exception
            if (r1 == 0) goto L_0x0164
            r1.close()     // Catch:{ all -> 0x0160 }
            goto L_0x0164
        L_0x0160:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ Exception -> 0x0165 }
        L_0x0164:
            throw r3     // Catch:{ Exception -> 0x0165 }
        L_0x0165:
            r1 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0169:
            im.bclpbkiauv.ui.-$$Lambda$AudioSelectActivity$FLKTnDVniK9_dF4GE65rDiiZ1jY r1 = new im.bclpbkiauv.ui.-$$Lambda$AudioSelectActivity$FLKTnDVniK9_dF4GE65rDiiZ1jY
            r1.<init>(r0)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.AudioSelectActivity.lambda$loadAudio$5$AudioSelectActivity():void");
    }

    public /* synthetic */ void lambda$null$4$AudioSelectActivity(ArrayList newAudioEntries) {
        this.audioEntries = newAudioEntries;
        this.progressView.showTextView();
        this.listViewAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return AudioSelectActivity.this.audioEntries.size();
        }

        public Object getItem(int i) {
            return AudioSelectActivity.this.audioEntries.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            AudioCell view = new AudioCell(this.mContext);
            view.setDelegate(new AudioCell.AudioCellDelegate() {
                public final void startedPlayingAudio(MessageObject messageObject) {
                    AudioSelectActivity.ListAdapter.this.lambda$onCreateViewHolder$0$AudioSelectActivity$ListAdapter(messageObject);
                }
            });
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$AudioSelectActivity$ListAdapter(MessageObject messageObject) {
            MessageObject unused = AudioSelectActivity.this.playingAudio = messageObject;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) AudioSelectActivity.this.audioEntries.get(position);
            AudioCell audioCell = (AudioCell) holder.itemView;
            MediaController.AudioEntry audioEntry2 = (MediaController.AudioEntry) AudioSelectActivity.this.audioEntries.get(position);
            boolean z = true;
            boolean z2 = position != AudioSelectActivity.this.audioEntries.size() - 1;
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                z = false;
            }
            audioCell.setAudio(audioEntry2, z2, z);
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription((View) this.listView, 0, new Class[]{AudioCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{AudioCell.class}, new String[]{"genreTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{AudioCell.class}, new String[]{"authorTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{AudioCell.class}, new String[]{"timeTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{AudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_musicPicker_checkbox), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{AudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_musicPicker_checkboxCheck), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{AudioCell.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_musicPicker_buttonIcon), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{AudioCell.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_musicPicker_buttonBackground), new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription((View) this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PickerBottomLayout.class}, new String[]{"cancelButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_picker_enabledButton), new ThemeDescription((View) this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_picker_enabledButton), new ThemeDescription((View) this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_picker_disabledButton), new ThemeDescription((View) this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonBadgeTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_picker_badgeText), new ThemeDescription((View) this.bottomLayout, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonBadgeTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_picker_badge)};
    }
}
