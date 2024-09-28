package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LruCache;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bjz.comm.net.utils.HttpUtils;
import com.bjz.comm.net.utils.RxHelper;
import com.bjz.comm.net.utils.TokenLoader;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.utils.status.StatusBarUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.FCTokenRequestCallback;
import im.bclpbkiauv.tgnet.ParamsUtil;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPC2;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.tgnet.TLRPCLogin;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.BaseVPAdapter;
import im.bclpbkiauv.ui.bottom.BottomBarItem;
import im.bclpbkiauv.ui.bottom.BottomBarLayout;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.fragments.AccountsAdapter;
import im.bclpbkiauv.ui.fragments.BaseFmts;
import im.bclpbkiauv.ui.fragments.ContactsFragment;
import im.bclpbkiauv.ui.fragments.DialogsFragment;
import im.bclpbkiauv.ui.fragments.DiscoveryFragment;
import im.bclpbkiauv.ui.fragments.MeFragmentV2;
import im.bclpbkiauv.ui.fragments.TabWebFragment;
import im.bclpbkiauv.ui.hcells.PopUserCell;
import im.bclpbkiauv.ui.hui.login.ChangePersonalInformationActivity;
import im.bclpbkiauv.ui.hui.login.LoginContronllerActivity;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.hviews.NoScrollViewPager;
import im.bclpbkiauv.ui.hviews.pop.EasyPopup;
import im.bclpbkiauv.ui.utils.AppUpdater;
import im.bclpbkiauv.utils.FingerprintUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class IndexActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DiscoveryFragment.Delegate {
    private static final String TAG = IndexActivity.class.getSimpleName();
    private BaseVPAdapter adapter = null;
    private int currentUnreadCount;
    /* access modifiers changed from: private */
    public int dialogsType;
    private TLRPC2.TL_DiscoveryPageSetting discoveryData;
    /* access modifiers changed from: private */
    public LruCache<Integer, BaseFmts> fragmentsCache;
    /* access modifiers changed from: private */
    public LinearLayout llDialogMenuLayout;
    /* access modifiers changed from: private */
    public BottomBarLayout mBottomBarLayout;
    private boolean mIsGettingFullUserInfo;
    private boolean mUserInfoIsCompleted;
    private NoScrollViewPager mVpContent;
    /* access modifiers changed from: private */
    public boolean needShowDisTab;
    private int reqDisToken;
    private TimerTask syncRemoteContactsTask = null;
    private Timer syncRemoteContactsTimer = null;
    private boolean timerInit = false;
    private TextView tvArchiveText;
    /* access modifiers changed from: private */
    public TextView tvCanReadText;
    /* access modifiers changed from: private */
    public TextView tvDeleteText;

    private void startTimer() {
        if (!this.timerInit) {
            if (this.syncRemoteContactsTimer == null) {
                this.syncRemoteContactsTimer = new Timer();
            }
            if (this.syncRemoteContactsTask == null) {
                this.syncRemoteContactsTask = new TimerTask() {
                    public void run() {
                        IndexActivity.this.getMessagesController().getContactsApplyDifferenceV2(true, false);
                    }
                };
            }
            this.syncRemoteContactsTimer.schedule(this.syncRemoteContactsTask, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, 30000);
            this.timerInit = true;
        }
    }

    private void stopTimer() {
        Timer timer = this.syncRemoteContactsTimer;
        if (timer != null) {
            timer.cancel();
            this.syncRemoteContactsTimer = null;
        }
        TimerTask timerTask = this.syncRemoteContactsTask;
        if (timerTask != null) {
            timerTask.cancel();
            this.syncRemoteContactsTask = null;
        }
        this.timerInit = false;
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        if (!AppUpdater.hasChecked && (getParentActivity() instanceof LaunchActivity)) {
            ((LaunchActivity) getParentActivity()).checkAppUpdate(false);
        }
    }

    public IndexActivity() {
    }

    public IndexActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.dialogsType = MessagesController.getMainSettings(this.currentAccount).getInt("dialogsType", 0);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactApplyUpdateCount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFriendsCircleUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.appDidLogout);
        HttpUtils.getInstance().clearCache();
        TokenLoader.getInstance().setCallBack(FCTokenRequestCallback.getInstance());
        getConnectionsManager().updateDcSettings();
        setNavigationBarColor(Theme.getColor(Theme.key_bottomBarBackground));
        return true;
    }

    public void rebuidView() {
        BaseVPAdapter baseVPAdapter = this.adapter;
        if (baseVPAdapter != null) {
            baseVPAdapter.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.actionBar.setAddToContainer(false);
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_index, (ViewGroup) null, false);
        this.mVpContent = (NoScrollViewPager) this.fragmentView.findViewById(R.id.vp_content);
        this.mBottomBarLayout = (BottomBarLayout) this.fragmentView.findViewById(R.id.btm_layout);
        LinearLayout linearLayout = (LinearLayout) this.fragmentView.findViewById(R.id.llDialogMenuLayout);
        this.llDialogMenuLayout = linearLayout;
        linearLayout.setBackgroundColor(Theme.getColor(Theme.key_bottomBarBackground));
        this.tvCanReadText = (TextView) this.fragmentView.findViewById(R.id.tvCanReadText);
        this.tvDeleteText = (TextView) this.fragmentView.findViewById(R.id.tvDeleteText);
        this.tvArchiveText = (TextView) this.fragmentView.findViewById(R.id.tvArchiveText);
        this.tvCanReadText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        this.tvCanReadText.setBackground(Theme.getSelectorDrawable(false));
        this.tvCanReadText.setText(LocaleController.getString(R.string.MarkAllAsRead));
        this.tvDeleteText.setText(LocaleController.getString("Delete", R.string.Delete));
        this.tvDeleteText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
        this.tvDeleteText.setBackground(Theme.getSelectorDrawable(false));
        this.tvArchiveText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tvArchiveText.setBackground(Theme.getSelectorDrawable(false));
        this.tvArchiveText.setVisibility(8);
        this.tvCanReadText.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                IndexActivity.this.lambda$createView$0$IndexActivity(view);
            }
        });
        this.tvDeleteText.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                IndexActivity.this.lambda$createView$1$IndexActivity(view);
            }
        });
        this.mBottomBarLayout.setBackgroundColor(Theme.getColor(Theme.key_bottomBarBackground));
        this.mBottomBarLayout.setOnItemLongClickListner(new BottomBarLayout.OnItemLongClickListner(context) {
            private final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemLongClick(BottomBarItem bottomBarItem, int i, int i2) {
                IndexActivity.this.lambda$createView$3$IndexActivity(this.f$1, bottomBarItem, i, i2);
            }
        });
        this.mBottomBarLayout.setOnItemSelectedListener(new BottomBarLayout.OnItemSelectedListener() {
            public final void onItemSelected(BottomBarItem bottomBarItem, int i, int i2) {
                IndexActivity.this.lambda$createView$4$IndexActivity(bottomBarItem, i, i2);
            }
        });
        int value = MessagesController.getMainSettings(this.currentAccount).getInt("contacts_apply_count", 0);
        BottomBarLayout bottomBarLayout = this.mBottomBarLayout;
        if (bottomBarLayout != null) {
            bottomBarLayout.setUnread(1, value);
        }
        if (Theme.getCurrentTheme().name.toLowerCase().contains("dark")) {
            StatusBarUtils.setStatusBarDarkTheme(getParentActivity(), false);
        } else {
            StatusBarUtils.setStatusBarDarkTheme(getParentActivity(), true);
        }
        initFragments();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$IndexActivity(View v) {
        BaseFmts f = getChildFragment(0);
        if (f instanceof DialogsFragment) {
            ((DialogsFragment) f).perfromSelectedDialogsAction(4);
        }
    }

    public /* synthetic */ void lambda$createView$1$IndexActivity(View v) {
        BaseFmts f = getChildFragment(0);
        if (f instanceof DialogsFragment) {
            ((DialogsFragment) f).showDeleteOrClearSheet();
        }
    }

    public /* synthetic */ void lambda$createView$3$IndexActivity(Context context, BottomBarItem bottomBarItem, int previous, int current) {
        if ((this.needShowDisTab && current == 4) || (!this.needShowDisTab && current == 3)) {
            EasyPopup accountPopup = (EasyPopup) ((EasyPopup) ((EasyPopup) ((EasyPopup) ((EasyPopup) ((EasyPopup) ((EasyPopup) EasyPopup.create(context).setContentView((int) R.layout.pop_index_layout)).setWidth(AndroidUtilities.dp(220.0f))).setHeight(AndroidUtilities.dp(270.0f))).setFocusAndOutsideEnable(true)).setBlurBackground(true)).setDimValue(0.6f)).apply();
            RecyclerListView accountList = (RecyclerListView) accountPopup.findViewById(R.id.rcyPopList);
            accountList.setLayoutManager(new LinearLayoutManager(context, 1, false));
            AccountsAdapter accountsAdapter = new AccountsAdapter(context);
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.shape_shadow_pop).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundGray), PorterDuff.Mode.MULTIPLY));
            accountList.setBackground(shadowDrawable);
            accountList.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(accountPopup) {
                private final /* synthetic */ EasyPopup f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(View view, int i) {
                    IndexActivity.this.lambda$null$2$IndexActivity(this.f$1, view, i);
                }
            });
            accountList.setAdapter(accountsAdapter);
            accountPopup.showAtAnchorView(bottomBarItem, 1, 4);
        }
    }

    public /* synthetic */ void lambda$null$2$IndexActivity(EasyPopup accountPopup, View view, int position) {
        if (position == 0) {
            int freeAccount = -1;
            int a = 0;
            while (true) {
                if (a >= 3) {
                    break;
                } else if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                } else {
                    a++;
                }
            }
            if (freeAccount >= 0) {
                presentFragment(new LoginContronllerActivity(freeAccount));
            }
        } else {
            ((LaunchActivity) getParentActivity()).switchToAccount(((PopUserCell) view).getAccountNumber(), true);
        }
        accountPopup.dismiss();
    }

    public /* synthetic */ void lambda$createView$4$IndexActivity(BottomBarItem bottomBarItem, int previousPosition, int currentPosition) {
        boolean z = false;
        if (previousPosition == 0) {
            BaseFmts f = getChildFragment(0);
            if (f instanceof DialogsFragment) {
                DialogsFragment dialogsFragment = (DialogsFragment) f;
                if (previousPosition == currentPosition) {
                    z = true;
                }
                dialogsFragment.closeSearchView(z);
            }
        } else if (previousPosition == 1) {
            BaseFmts f2 = getChildFragment(1);
            if (f2 instanceof ContactsFragment) {
                ContactsFragment contactsFragment = (ContactsFragment) f2;
                if (previousPosition == currentPosition) {
                    z = true;
                }
                contactsFragment.closeSearchView(z);
            }
        }
    }

    private void doChannelBind() {
        if (MessagesController.getMainSettings(this.currentAccount).getBoolean("need_channel_bind", true)) {
            bind((String) null, (String) null);
        }
    }

    private void bind(String channel, String custom) {
        if (!TextUtils.isEmpty(channel) || !TextUtils.isEmpty(custom)) {
            TLRPCLogin.TL_auth_signUpBind req = new TLRPCLogin.TL_auth_signUpBind();
            req.company = "Yixin";
            req.device = FingerprintUtil.getDeviceId(getParentActivity());
            req.userId = getUserConfig().clientUserId;
            TLRPC.TL_dataJSON dataJSON = new TLRPC.TL_dataJSON();
            dataJSON.data = ParamsUtil.toJson(new String[]{"op_channel", "op_data"}, channel, custom);
            req.extend = dataJSON;
            getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    IndexActivity.this.lambda$bind$5$IndexActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$bind$5$IndexActivity(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            FileLog.e("bind channel failed, error:" + error.text);
        } else if (response instanceof TLRPC.TL_boolTrue) {
            FileLog.d("bind channel success");
            MessagesController.getMainSettings(this.currentAccount).edit().putBoolean("need_channel_bind", false).commit();
        } else {
            FileLog.d("bind channel failed");
        }
    }

    private void initFragments() {
        this.mVpContent.setOffscreenPageLimit(4);
        this.fragmentsCache = new LruCache<>(5);
        AnonymousClass3 r0 = new BaseVPAdapter(getParentActivity().getSupportFragmentManager(), new Object[0]) {
            public void notifyDataSetChanged() {
                if (IndexActivity.this.fragmentsCache != null) {
                    IndexActivity.this.fragmentsCache.evictAll();
                }
                super.notifyDataSetChanged();
            }

            public Fragment getIMItem(int position) {
                BaseFmts fragment = (BaseFmts) IndexActivity.this.fragmentsCache.get(Integer.valueOf(position));
                if (fragment == null) {
                    if (position == 0) {
                        fragment = new DialogsFragment();
                        ((DialogsFragment) fragment).setDilogsType(IndexActivity.this.dialogsType);
                        ((DialogsFragment) fragment).setDelegate(new DialogsFragment.FmtConsumDelegate() {
                            public void changeUnreadCount(int count) {
                                if (IndexActivity.this.mBottomBarLayout != null) {
                                    IndexActivity.this.mBottomBarLayout.setUnread(0, count);
                                }
                            }

                            public void onEditModelChange(final boolean isEdit, boolean hasCanReadCount) {
                                if (IndexActivity.this.llDialogMenuLayout != null && IndexActivity.this.mBottomBarLayout != null) {
                                    if (isEdit) {
                                        IndexActivity.this.tvCanReadText.setText(LocaleController.getString(R.string.MarkAllAsRead));
                                        if (hasCanReadCount) {
                                            IndexActivity.this.tvCanReadText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                                            IndexActivity.this.tvCanReadText.setEnabled(true);
                                        } else {
                                            IndexActivity.this.tvCanReadText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                                            IndexActivity.this.tvCanReadText.setEnabled(false);
                                        }
                                    }
                                    IndexActivity.this.mBottomBarLayout.clearAnimation();
                                    IndexActivity.this.llDialogMenuLayout.clearAnimation();
                                    int pivotY = IndexActivity.this.mBottomBarLayout.getMeasuredHeight() / 2;
                                    IndexActivity.this.mBottomBarLayout.setPivotY((float) pivotY);
                                    IndexActivity.this.llDialogMenuLayout.setPivotY((float) pivotY);
                                    AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
                                        public void onAnimationStart(Animator animation) {
                                            if (isEdit) {
                                                IndexActivity.this.llDialogMenuLayout.setVisibility(0);
                                            } else {
                                                IndexActivity.this.mBottomBarLayout.setVisibility(0);
                                            }
                                        }

                                        public void onAnimationEnd(Animator animation) {
                                            if (!isEdit) {
                                                IndexActivity.this.mBottomBarLayout.setVisibility(0);
                                                IndexActivity.this.llDialogMenuLayout.setVisibility(8);
                                                return;
                                            }
                                            IndexActivity.this.llDialogMenuLayout.setVisibility(0);
                                            IndexActivity.this.mBottomBarLayout.setVisibility(8);
                                        }
                                    };
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    ArrayList<Animator> animators = new ArrayList<>();
                                    BottomBarLayout access$300 = IndexActivity.this.mBottomBarLayout;
                                    Property property = View.SCALE_Y;
                                    float[] fArr = new float[2];
                                    float f = 1.0f;
                                    fArr[0] = isEdit ? 1.0f : 0.1f;
                                    fArr[1] = isEdit ? 0.1f : 1.0f;
                                    Animator animator1 = ObjectAnimator.ofFloat(access$300, property, fArr);
                                    animator1.addListener(listener);
                                    animators.add(animator1);
                                    LinearLayout access$400 = IndexActivity.this.llDialogMenuLayout;
                                    Property property2 = View.SCALE_Y;
                                    float[] fArr2 = new float[2];
                                    fArr2[0] = isEdit ? 0.1f : 1.0f;
                                    if (!isEdit) {
                                        f = 0.1f;
                                    }
                                    fArr2[1] = f;
                                    Animator animator2 = ObjectAnimator.ofFloat(access$400, property2, fArr2);
                                    animator2.addListener(listener);
                                    animators.add(animator2);
                                    animatorSet.playTogether(animators);
                                    animatorSet.setDuration(250);
                                    animatorSet.start();
                                }
                            }

                            public void onUpdateState(boolean hasCanReadCount, int selectDialogsCount, int canReadCount) {
                                if (selectDialogsCount > 0 && canReadCount > 0) {
                                    IndexActivity.this.tvCanReadText.setEnabled(true);
                                    IndexActivity.this.tvCanReadText.setText(LocaleController.getString(R.string.MarkAsRead));
                                    IndexActivity.this.tvCanReadText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                                } else if (selectDialogsCount > 0 && canReadCount <= 0) {
                                    IndexActivity.this.tvCanReadText.setEnabled(true);
                                    IndexActivity.this.tvCanReadText.setText(LocaleController.getString(R.string.MarkAsRead));
                                    IndexActivity.this.tvCanReadText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                                } else if (hasCanReadCount) {
                                    IndexActivity.this.tvCanReadText.setEnabled(true);
                                    IndexActivity.this.tvCanReadText.setText(LocaleController.getString(R.string.MarkAllAsRead));
                                    IndexActivity.this.tvCanReadText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                                } else {
                                    IndexActivity.this.tvCanReadText.setEnabled(false);
                                    IndexActivity.this.tvCanReadText.setText(LocaleController.getString(R.string.MarkAllAsRead));
                                    IndexActivity.this.tvCanReadText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                                }
                                if (selectDialogsCount > 0) {
                                    IndexActivity.this.tvDeleteText.setEnabled(true);
                                    IndexActivity.this.tvDeleteText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                                    return;
                                }
                                IndexActivity.this.tvDeleteText.setEnabled(false);
                                IndexActivity.this.tvDeleteText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                            }
                        });
                    } else if (position == 1) {
                        fragment = new ContactsFragment();
                    } else if (position == 2) {
                        if (IndexActivity.this.needShowDisTab) {
                            fragment = new TabWebFragment();
                            ((TabWebFragment) fragment).setDelegate(IndexActivity.this);
                        } else {
                            fragment = new DiscoveryFragment();
                            ((DiscoveryFragment) fragment).setDelegate(IndexActivity.this);
                        }
                    } else if (position == 3) {
                        if (IndexActivity.this.needShowDisTab) {
                            fragment = new DiscoveryFragment();
                            ((DiscoveryFragment) fragment).setDelegate(IndexActivity.this);
                        } else {
                            fragment = new MeFragmentV2();
                        }
                    } else if (IndexActivity.this.needShowDisTab && position == 4) {
                        fragment = new MeFragmentV2();
                    }
                    IndexActivity.this.fragmentsCache.put(Integer.valueOf(position), fragment);
                }
                return fragment;
            }

            public int getCount() {
                return IndexActivity.this.needShowDisTab ? 5 : 4;
            }
        };
        this.adapter = r0;
        this.mVpContent.setAdapter(r0);
        this.mBottomBarLayout.setViewPager(this.mVpContent);
    }

    public void onResume() {
        super.onResume();
        getFcUnRead();
        getDiscoveryData();
        if (!BuildVars.DEBUG_VERSION) {
            getFcUrlFromServer();
        }
        callBackFragmentsLifeCycle(true);
    }

    public void onPause() {
        super.onPause();
        stopTimer();
        callBackFragmentsLifeCycle(false);
    }

    private void updateBottomItem() {
        BottomBarLayout bottomBarLayout = this.mBottomBarLayout;
        if (bottomBarLayout != null) {
            if (this.needShowDisTab) {
                if (bottomBarLayout.getChildCount() == 4) {
                    BottomBarItem.Builder builder = new BottomBarItem.Builder(getParentActivity());
                    builder.normalIcon(ContextCompat.getDrawable(getParentActivity(), R.drawable.ic_btm_web_normal));
                    this.mBottomBarLayout.addItem(builder.create(this.discoveryData.getS().get(0).getLogo(), R.drawable.ic_btm_web_normal, R.drawable.ic_btm_web_normal, this.discoveryData.getS().get(0).getTitle()), 2);
                }
            } else if (bottomBarLayout.getChildCount() == 5) {
                this.mBottomBarLayout.removeItem(2);
            }
        }
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showIncomingNotification(java.lang.String r17, java.lang.String r18, java.lang.String r19, im.bclpbkiauv.tgnet.TLRPC.User r20, boolean r21) {
        /*
            r16 = this;
            android.content.Intent r0 = new android.content.Intent
            androidx.fragment.app.FragmentActivity r1 = r16.getParentActivity()
            java.lang.Class<im.bclpbkiauv.ui.LaunchActivity> r2 = im.bclpbkiauv.ui.LaunchActivity.class
            r0.<init>(r1, r2)
            java.lang.String r1 = "im.bclpbkiauv.contacts.add"
            r0.setAction(r1)
            r1 = 805306368(0x30000000, float:4.656613E-10)
            r0.addFlags(r1)
            android.app.Notification$Builder r1 = new android.app.Notification$Builder
            androidx.fragment.app.FragmentActivity r2 = r16.getParentActivity()
            r1.<init>(r2)
            r2 = r17
            android.app.Notification$Builder r1 = r1.setContentTitle(r2)
            r3 = r18
            android.app.Notification$Builder r1 = r1.setContentText(r3)
            r4 = r19
            android.app.Notification$Builder r1 = r1.setSubText(r4)
            r5 = 1
            android.app.Notification$Builder r1 = r1.setAutoCancel(r5)
            long r6 = java.lang.System.currentTimeMillis()
            android.app.Notification$Builder r1 = r1.setWhen(r6)
            r6 = 2131558767(0x7f0d016f, float:1.874286E38)
            android.app.Notification$Builder r1 = r1.setSmallIcon(r6)
            androidx.fragment.app.FragmentActivity r6 = r16.getParentActivity()
            r7 = 0
            android.app.PendingIntent r6 = android.app.PendingIntent.getActivity(r6, r7, r0, r7)
            android.app.Notification$Builder r1 = r1.setContentIntent(r6)
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 17
            if (r6 < r8) goto L_0x005a
            r1.setShowWhen(r5)
        L_0x005a:
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 26
            if (r6 < r8) goto L_0x008d
            androidx.fragment.app.FragmentActivity r6 = r16.getParentActivity()
            java.lang.String r8 = "notification"
            java.lang.Object r6 = r6.getSystemService(r8)
            android.app.NotificationManager r6 = (android.app.NotificationManager) r6
            java.lang.String r8 = "10111213"
            android.app.NotificationChannel r9 = r6.getNotificationChannel(r8)
            r10 = 1
            if (r9 == 0) goto L_0x0076
            r10 = 0
        L_0x0076:
            if (r10 == 0) goto L_0x008a
            android.app.NotificationChannel r11 = new android.app.NotificationChannel
            r12 = 3
            java.lang.String r13 = "好友请求"
            r11.<init>(r8, r13, r12)
            r11.enableVibration(r7)
            r11.enableLights(r7)
            r6.createNotificationChannel(r11)
        L_0x008a:
            r1.setChannelId(r8)
        L_0x008d:
            if (r21 != 0) goto L_0x0195
            android.content.Intent r6 = new android.content.Intent
            androidx.fragment.app.FragmentActivity r8 = r16.getParentActivity()
            java.lang.Class<im.bclpbkiauv.messenger.voip.VoIPActionsReceiver> r9 = im.bclpbkiauv.messenger.voip.VoIPActionsReceiver.class
            r6.<init>(r8, r9)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            androidx.fragment.app.FragmentActivity r9 = r16.getParentActivity()
            java.lang.String r9 = r9.getPackageName()
            r8.append(r9)
            java.lang.String r9 = ".CANCEL_CONTACT_APPLY"
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r6.setAction(r8)
            r8 = 111(0x6f, float:1.56E-43)
            java.lang.String r9 = "call_id"
            r6.putExtra(r9, r8)
            r10 = 2131690308(0x7f0f0344, float:1.9009656E38)
            java.lang.String r11 = "Cancel"
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 24
            if (r11 < r12) goto L_0x00e4
            android.text.SpannableString r11 = new android.text.SpannableString
            r11.<init>(r10)
            r10 = r11
            r11 = r10
            android.text.SpannableString r11 = (android.text.SpannableString) r11
            android.text.style.ForegroundColorSpan r13 = new android.text.style.ForegroundColorSpan
            r14 = -769226(0xfffffffffff44336, float:NaN)
            r13.<init>(r14)
            int r14 = r10.length()
            r11.setSpan(r13, r7, r14, r7)
        L_0x00e4:
            androidx.fragment.app.FragmentActivity r11 = r16.getParentActivity()
            r13 = 268435456(0x10000000, float:2.5243549E-29)
            android.app.PendingIntent r11 = android.app.PendingIntent.getBroadcast(r11, r7, r6, r13)
            r14 = 2131231100(0x7f08017c, float:1.8078271E38)
            r1.addAction(r14, r10, r11)
            android.content.Intent r14 = new android.content.Intent
            androidx.fragment.app.FragmentActivity r15 = r16.getParentActivity()
            java.lang.Class<im.bclpbkiauv.messenger.voip.VoIPActionsReceiver> r5 = im.bclpbkiauv.messenger.voip.VoIPActionsReceiver.class
            r14.<init>(r15, r5)
            r5 = r14
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            androidx.fragment.app.FragmentActivity r15 = r16.getParentActivity()
            java.lang.String r15 = r15.getPackageName()
            r14.append(r15)
            java.lang.String r15 = ".AGREE_CONTACT_APPLY"
            r14.append(r15)
            java.lang.String r14 = r14.toString()
            r5.setAction(r14)
            r5.putExtra(r9, r8)
            r8 = 2131689726(0x7f0f00fe, float:1.9008476E38)
            java.lang.String r9 = "Agree"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
            int r9 = android.os.Build.VERSION.SDK_INT
            if (r9 < r12) goto L_0x0144
            android.text.SpannableString r9 = new android.text.SpannableString
            r9.<init>(r8)
            r8 = r9
            r9 = r8
            android.text.SpannableString r9 = (android.text.SpannableString) r9
            android.text.style.ForegroundColorSpan r12 = new android.text.style.ForegroundColorSpan
            r14 = -16733696(0xffffffffff00aa00, float:-1.7102387E38)
            r12.<init>(r14)
            int r14 = r8.length()
            r9.setSpan(r12, r7, r14, r7)
        L_0x0144:
            androidx.fragment.app.FragmentActivity r9 = r16.getParentActivity()
            android.app.PendingIntent r9 = android.app.PendingIntent.getBroadcast(r9, r7, r5, r13)
            r12 = 2131231099(0x7f08017b, float:1.807827E38)
            r1.addAction(r12, r8, r9)
            r12 = 2
            r1.setPriority(r12)
            int r12 = android.os.Build.VERSION.SDK_INT
            r13 = 21
            if (r12 < r13) goto L_0x0192
            r12 = -13851168(0xffffffffff2ca5e0, float:-2.2948849E38)
            r1.setColor(r12)
            long[] r12 = new long[r7]
            r1.setVibrate(r12)
            java.lang.String r12 = "call"
            r1.setCategory(r12)
            androidx.fragment.app.FragmentActivity r12 = r16.getParentActivity()
            android.app.PendingIntent r7 = android.app.PendingIntent.getActivity(r12, r7, r0, r7)
            r12 = 1
            r1.setFullScreenIntent(r7, r12)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r12 = "tel:"
            r7.append(r12)
            r12 = r20
            java.lang.String r13 = r12.phone
            r7.append(r13)
            java.lang.String r7 = r7.toString()
            r1.addPerson(r7)
            goto L_0x0197
        L_0x0192:
            r12 = r20
            goto L_0x0197
        L_0x0195:
            r12 = r20
        L_0x0197:
            im.bclpbkiauv.messenger.NotificationsController r5 = r16.getNotificationsController()
            androidx.core.app.NotificationManagerCompat r5 = r5.getNotificationManager()
            r6 = 10111213(0x9a48ed, float:1.4168827E-38)
            android.app.Notification r7 = r1.build()
            r5.notify(r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.IndexActivity.showIncomingNotification(java.lang.String, java.lang.String, java.lang.String, im.bclpbkiauv.tgnet.TLRPC$User, boolean):void");
    }

    public void updateTite(String titleOverlayText, int titleOverlayTextId, Runnable action) {
        if (this.fragmentsCache != null) {
            for (int i = 0; i < this.fragmentsCache.size(); i++) {
                BaseFmts f = this.fragmentsCache.get(Integer.valueOf(i));
                if (!(f == null || f.getActionBar() == null || !f.isAdded())) {
                    f.getActionBar().setTitleOverlayText2(titleOverlayText, titleOverlayTextId, action);
                }
            }
        }
    }

    private void getFcUnRead() {
    }

    private void getFcUrlFromServer() {
    }

    private void callBackFragmentsLifeCycle(boolean isResume) {
        if (this.fragmentsCache != null) {
            for (int i = 0; i < this.fragmentsCache.size(); i++) {
                BaseFmts f = this.fragmentsCache.get(Integer.valueOf(i));
                if (f != null && f.isAdded()) {
                    if (isResume) {
                        f.onResumeForBaseFragment();
                    } else {
                        f.onPauseForBaseFragment();
                    }
                }
            }
        }
    }

    private BaseFmts getChildFragment(int position) {
        BaseVPAdapter baseVPAdapter = this.adapter;
        if (baseVPAdapter == null) {
            return null;
        }
        Fragment f = baseVPAdapter.getItem(position);
        if (f instanceof BaseFmts) {
            return (BaseFmts) f;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id != NotificationCenter.userFriendsCircleUpdate) {
            if (id == NotificationCenter.contactApplyUpdateCount) {
                if (this.mBottomBarLayout != null) {
                    int count = args[0].intValue();
                    this.mBottomBarLayout.setUnread(1, count);
                    if (count == 0) {
                        ((NotificationManager) getParentActivity().getSystemService("notification")).cancel(10111213);
                        return;
                    }
                    showIncomingNotification(UserObject.getName(getUserConfig().getCurrentUser()), LocaleController.getString("NewContactApply", R.string.NewContactApply), (String) null, getUserConfig().getCurrentUser(), true);
                }
            } else if (id == NotificationCenter.userFullInfoDidLoad && getUserConfig().isClientActivated() && args[0].intValue() == getUserConfig().getClientUserId() && (args[1] instanceof TLRPCContacts.CL_userFull_v1)) {
                this.mIsGettingFullUserInfo = false;
            }
        }
    }

    private void checkHadCompletedUserInfo(TLRPC.UserFull full) {
        if (getUserConfig().isClientActivated() && !this.mIsGettingFullUserInfo && !this.mUserInfoIsCompleted) {
            if (!(getParentLayout() == null || getParentLayout().fragmentsStack == null)) {
                Iterator<BaseFragment> it = getParentLayout().fragmentsStack.iterator();
                while (it.hasNext()) {
                    if (ChangePersonalInformationActivity.class.getName().equals(it.next().getClass().getName())) {
                        return;
                    }
                }
            }
            this.mIsGettingFullUserInfo = true;
            if (full == null) {
                full = MessagesController.getInstance(this.currentAccount).getUserFull(getUserConfig().getClientUserId());
            }
            if (full instanceof TLRPCContacts.CL_userFull_v1) {
                TLRPCContacts.CL_userFull_v1 userInfo = (TLRPCContacts.CL_userFull_v1) full;
                if (userInfo.getExtendBean() == null) {
                    return;
                }
                if (userInfo.getExtendBean().needCompletedUserInfor()) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            IndexActivity.this.lambda$checkHadCompletedUserInfo$6$IndexActivity();
                        }
                    }, 1000);
                    this.mIsGettingFullUserInfo = false;
                    return;
                }
                this.mIsGettingFullUserInfo = false;
                this.mUserInfoIsCompleted = true;
            } else if (full == null) {
                MessagesController.getInstance(this.currentAccount).loadFullUser(getUserConfig().getClientUserId(), this.classGuid, true);
            }
        }
    }

    public /* synthetic */ void lambda$checkHadCompletedUserInfo$6$IndexActivity() {
        presentFragment(new ChangePersonalInformationActivity(this.currentAccount));
        this.mIsGettingFullUserInfo = false;
    }

    private void getDiscoveryData() {
        if (this.reqDisToken == 0 && this.discoveryData == null) {
            TLRPC2.TL_GetDiscoveryPageSetting req = new TLRPC2.TL_GetDiscoveryPageSetting();
            req.tag = "Yixin";
            FileLog.d(TAG, "start getData");
            ConnectionsManager connectionsManager = getConnectionsManager();
            int sendRequest = getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    IndexActivity.this.lambda$getDiscoveryData$8$IndexActivity(tLObject, tL_error);
                }
            });
            this.reqDisToken = sendRequest;
            connectionsManager.bindRequestToGuid(sendRequest, this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getDiscoveryData$8$IndexActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                IndexActivity.this.lambda$null$7$IndexActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$7$IndexActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null && (response instanceof TLRPC2.TL_DiscoveryPageSetting)) {
            this.discoveryData = (TLRPC2.TL_DiscoveryPageSetting) response;
            BaseVPAdapter baseVPAdapter = this.adapter;
            if (baseVPAdapter != null) {
                baseVPAdapter.notifyDataSetChanged();
            }
            updateBottomItem();
            FileLog.d(TAG, "getData success.");
        } else if (error != null) {
            WalletErrorUtil.parseErrorToast(error.text);
            String str = TAG;
            FileLog.e(str, "getData error:" + error.text);
        }
        this.reqDisToken = 0;
    }

    public TLRPC2.TL_DiscoveryPageSetting getDiscoveryPageData() {
        return this.discoveryData;
    }

    /* access modifiers changed from: protected */
    public void clearViews() {
        super.clearViews();
        this.mVpContent = null;
        this.mBottomBarLayout = null;
        LruCache<Integer, BaseFmts> lruCache = this.fragmentsCache;
        if (lruCache != null) {
            lruCache.evictAll();
        }
    }

    public boolean onBackPressed() {
        BaseFmts baseFmts;
        int currentItem = this.mBottomBarLayout.getCurrentItem();
        LruCache<Integer, BaseFmts> lruCache = this.fragmentsCache;
        if (lruCache == null || ((baseFmts = lruCache.get(Integer.valueOf(currentItem))) != null && !baseFmts.onBackPressed())) {
            return super.onBackPressed();
        }
        return false;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactApplyUpdateCount);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFriendsCircleUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        LruCache<Integer, BaseFmts> lruCache = this.fragmentsCache;
        if (lruCache != null) {
            lruCache.evictAll();
            this.fragmentsCache = null;
        }
        BaseVPAdapter baseVPAdapter = this.adapter;
        if (baseVPAdapter != null) {
            baseVPAdapter.destroy();
            this.adapter = null;
        }
        this.mVpContent = null;
        this.mBottomBarLayout = null;
        RxHelper.getInstance().lambda$sendSimpleRequest$0$RxHelper(TAG);
    }
}
