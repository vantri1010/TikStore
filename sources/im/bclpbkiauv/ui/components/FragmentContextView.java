package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.voip.VoIPService;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.IndexActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.NewLocationActivity;
import im.bclpbkiauv.ui.VoIPActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.SharingLocationsAlert;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentContextView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private FragmentContextView additionalContextView;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public Runnable checkLocationRunnable = new Runnable() {
        public void run() {
            FragmentContextView.this.checkLocationString();
            AndroidUtilities.runOnUIThread(FragmentContextView.this.checkLocationRunnable, 1000);
        }
    };
    private ImageView closeButton;
    private int currentStyle = -1;
    private boolean firstLocationsLoaded;
    private BaseFragment fragment;
    private FrameLayout frameLayout;
    private boolean isLocation;
    private int lastLocationSharingCount = -1;
    private MessageObject lastMessageObject;
    private String lastString;
    private boolean loadingSharingCount;
    private ImageView playButton;
    private ImageView playbackSpeedButton;
    private TextView titleTextView;
    private float topPadding;
    private boolean visible;
    private float yPosition;

    public FragmentContextView(Context context, BaseFragment parentFragment, boolean location) {
        super(context);
        this.fragment = parentFragment;
        this.visible = true;
        this.isLocation = location;
        ((ViewGroup) parentFragment.getFragmentView()).setClipToPadding(false);
        setTag(1);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout = frameLayout2;
        frameLayout2.setWillNotDraw(false);
        addView(this.frameLayout, LayoutHelper.createFrame(-1.0f, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow);
        addView(shadow, LayoutHelper.createFrame(-1.0f, 3.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.playButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.playButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_inappPlayerPlayPause), PorterDuff.Mode.MULTIPLY));
        addView(this.playButton, LayoutHelper.createFrame(36, 36, 51));
        this.playButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$0$FragmentContextView(view);
            }
        });
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setMaxLines(1);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setGravity(19);
        addView(this.titleTextView, LayoutHelper.createFrame(-1.0f, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        if (!location) {
            ImageView imageView2 = new ImageView(context);
            this.playbackSpeedButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.playbackSpeedButton.setImageResource(R.drawable.voice2x);
            this.playbackSpeedButton.setContentDescription(LocaleController.getString("AccDescrPlayerSpeed", R.string.AccDescrPlayerSpeed));
            if (AndroidUtilities.density >= 3.0f) {
                this.playbackSpeedButton.setPadding(0, 1, 0, 0);
            }
            addView(this.playbackSpeedButton, LayoutHelper.createFrame(36.0f, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
            this.playbackSpeedButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    FragmentContextView.this.lambda$new$1$FragmentContextView(view);
                }
            });
            updatePlaybackButton();
        }
        ImageView imageView3 = new ImageView(context);
        this.closeButton = imageView3;
        imageView3.setImageResource(R.drawable.miniplayer_close);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_inappPlayerClose), PorterDuff.Mode.MULTIPLY));
        this.closeButton.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36, 53));
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$3$FragmentContextView(view);
            }
        });
        setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$4$FragmentContextView(view);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$FragmentContextView(View v) {
        if (this.currentStyle != 0) {
            return;
        }
        if (MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        } else {
            MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    public /* synthetic */ void lambda$new$1$FragmentContextView(View v) {
        if (MediaController.getInstance().getPlaybackSpeed() > 1.0f) {
            MediaController.getInstance().setPlaybackSpeed(1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(1.8f);
        }
        updatePlaybackButton();
    }

    public /* synthetic */ void lambda$new$3$FragmentContextView(View v) {
        if (this.currentStyle == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof DialogsActivity) {
                builder.setMessage(LocaleController.getString("StopLiveLocationAlertAll", R.string.StopLiveLocationAlertAll));
            } else {
                ChatActivity activity = (ChatActivity) baseFragment;
                TLRPC.Chat chat = activity.getCurrentChat();
                TLRPC.User user = activity.getCurrentUser();
                if (chat != null) {
                    builder.setMessage(LocaleController.formatString("StopLiveLocationAlertToGroup", R.string.StopLiveLocationAlertToGroup, chat.title));
                } else if (user != null) {
                    builder.setMessage(LocaleController.formatString("StopLiveLocationAlertToUser", R.string.StopLiveLocationAlertToUser, UserObject.getFirstName(user)));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                }
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FragmentContextView.this.lambda$null$2$FragmentContextView(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            builder.show();
            return;
        }
        MediaController.getInstance().cleanupPlayer(true, true);
    }

    public /* synthetic */ void lambda$null$2$FragmentContextView(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            for (int a = 0; a < 3; a++) {
                LocationController.getInstance(a).removeAllLocationSharings();
            }
            return;
        }
        LocationController.getInstance(baseFragment.getCurrentAccount()).removeSharingLocation(((ChatActivity) this.fragment).getDialogId());
    }

    public /* synthetic */ void lambda$new$4$FragmentContextView(View v) {
        long did;
        int i = this.currentStyle;
        if (i == 0) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.fragment != null && messageObject != null) {
                if (messageObject.isMusic()) {
                    this.fragment.showDialog(new AudioPlayerAlert(getContext()));
                    return;
                }
                long dialog_id = 0;
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    dialog_id = ((ChatActivity) baseFragment).getDialogId();
                }
                if (messageObject.getDialogId() == dialog_id) {
                    ((ChatActivity) this.fragment).scrollToMessageId(messageObject.getId(), 0, false, 0, true);
                    return;
                }
                long dialog_id2 = messageObject.getDialogId();
                Bundle args = new Bundle();
                int lower_part = (int) dialog_id2;
                int high_id = (int) (dialog_id2 >> 32);
                if (lower_part == 0) {
                    args.putInt("enc_id", high_id);
                } else if (lower_part > 0) {
                    args.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    args.putInt("chat_id", -lower_part);
                }
                args.putInt("message_id", messageObject.getId());
                this.fragment.presentFragment(new ChatActivity(args), this.fragment instanceof ChatActivity);
            }
        } else if (i == 1) {
            Intent intent = new Intent(getContext(), VoIPActivity.class);
            intent.addFlags(805306368);
            getContext().startActivity(intent);
        } else if (i == 2) {
            long did2 = 0;
            int account = UserConfig.selectedAccount;
            BaseFragment baseFragment2 = this.fragment;
            if (baseFragment2 instanceof ChatActivity) {
                did = ((ChatActivity) baseFragment2).getDialogId();
                account = this.fragment.getCurrentAccount();
            } else if (LocationController.getLocationsCount() == 1) {
                int a = 0;
                while (true) {
                    if (a >= 3) {
                        break;
                    } else if (!LocationController.getInstance(a).sharingLocationsUI.isEmpty()) {
                        LocationController.SharingLocationInfo info = LocationController.getInstance(a).sharingLocationsUI.get(0);
                        did2 = info.did;
                        account = info.messageObject.currentAccount;
                        break;
                    } else {
                        a++;
                    }
                }
                did = did2;
            } else {
                did = 0;
            }
            if (did != 0) {
                openSharingLocation(LocationController.getInstance(account).getSharingLocationInfo(did));
            } else {
                this.fragment.showDialog(new SharingLocationsAlert(getContext(), new SharingLocationsAlert.SharingLocationsAlertDelegate() {
                    public final void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo) {
                        FragmentContextView.this.openSharingLocation(sharingLocationInfo);
                    }
                }));
            }
        }
    }

    private void updatePlaybackButton() {
        if (MediaController.getInstance().getPlaybackSpeed() > 1.0f) {
            this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_inappPlayerPlayPause), PorterDuff.Mode.MULTIPLY));
        } else {
            this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_inappPlayerClose), PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setAdditionalContextView(FragmentContextView contextView) {
        this.additionalContextView = contextView;
    }

    /* access modifiers changed from: private */
    public void openSharingLocation(LocationController.SharingLocationInfo info) {
        if (info != null && this.fragment.getParentActivity() != null) {
            LaunchActivity launchActivity = (LaunchActivity) this.fragment.getParentActivity();
            launchActivity.switchToAccount(info.messageObject.currentAccount, true);
            NewLocationActivity locationActivity = new NewLocationActivity(3, info.messageObject);
            locationActivity.setDelegate(new NewLocationActivity.LocationActivityDelegate(info.messageObject.getDialogId()) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                    SendMessagesHelper.getInstance(LocationController.SharingLocationInfo.this.messageObject.currentAccount).sendMessage(messageMedia, this.f$1, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
                }
            });
            launchActivity.lambda$runLinkRequest$28$LaunchActivity(locationActivity);
        }
    }

    public float getTopPadding() {
        return this.topPadding;
    }

    private void checkVisibility() {
        boolean show = false;
        int i = 0;
        if (this.isLocation) {
            BaseFragment baseFragment = this.fragment;
            boolean z = true;
            if (baseFragment instanceof DialogsActivity) {
                if (LocationController.getLocationsCount() == 0) {
                    z = false;
                }
                show = z;
            } else if (baseFragment instanceof IndexActivity) {
                if (LocationController.getLocationsCount() == 0) {
                    z = false;
                }
                show = z;
            } else {
                show = LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
            }
        } else if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (!(messageObject == null || messageObject.getId() == 0)) {
                show = true;
            }
        } else {
            show = true;
        }
        if (!show) {
            i = 8;
        }
        setVisibility(i);
    }

    public void setTopPadding(float value) {
        FragmentContextView fragmentContextView;
        this.topPadding = value;
        if (this.fragment != null && getParent() != null) {
            View view = this.fragment.getFragmentView();
            ActionBar actionBar = this.fragment.getActionBar();
            int additionalPadding = 0;
            FragmentContextView fragmentContextView2 = this.additionalContextView;
            if (!(fragmentContextView2 == null || fragmentContextView2.getVisibility() != 0 || this.additionalContextView.getParent() == null)) {
                additionalPadding = AndroidUtilities.dp(36.0f);
            }
            if (!(view == null || getParent() == null)) {
                view.setPadding(0, ((int) this.topPadding) + additionalPadding, 0, 0);
            }
            if (this.isLocation && (fragmentContextView = this.additionalContextView) != null) {
                ((FrameLayout.LayoutParams) fragmentContextView.getLayoutParams()).topMargin = (-AndroidUtilities.dp(36.0f)) - ((int) this.topPadding);
            }
        }
    }

    private void updateStyle(int style) {
        if (this.currentStyle != style) {
            this.currentStyle = style;
            if (style == 0 || style == 2) {
                this.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_inappPlayerBackground));
                this.frameLayout.setTag(Theme.key_inappPlayerBackground);
                this.titleTextView.setTextColor(Theme.getColor(Theme.key_inappPlayerTitle));
                this.titleTextView.setTag(Theme.key_inappPlayerTitle);
                this.closeButton.setVisibility(0);
                this.playButton.setVisibility(0);
                this.titleTextView.setTypeface(Typeface.DEFAULT);
                this.titleTextView.setTextSize(1, 15.0f);
                if (style == 0) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36.0f, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1.0f, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
                    ImageView imageView = this.playbackSpeedButton;
                    if (imageView != null) {
                        imageView.setVisibility(0);
                    }
                    this.closeButton.setContentDescription(LocaleController.getString("AccDescrClosePlayer", R.string.AccDescrClosePlayer));
                } else if (style == 2) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36.0f, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1.0f, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
                    this.closeButton.setContentDescription(LocaleController.getString("AccDescrStopLiveLocation", R.string.AccDescrStopLiveLocation));
                }
            } else if (style == 1) {
                this.titleTextView.setText(LocaleController.getString("ReturnToCall", R.string.ReturnToCall));
                this.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_returnToCallBackground));
                this.frameLayout.setTag(Theme.key_returnToCallBackground);
                this.titleTextView.setTextColor(Theme.getColor(Theme.key_returnToCallText));
                this.titleTextView.setTag(Theme.key_returnToCallText);
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleTextView.setTextSize(1, 14.0f);
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2.0f, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
                this.titleTextView.setPadding(0, 0, 0, 0);
                ImageView imageView2 = this.playbackSpeedButton;
                if (imageView2 != null) {
                    imageView2.setVisibility(8);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.topPadding = 0.0f;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsCacheChanged);
            return;
        }
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndedCall);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsCacheChanged);
            FragmentContextView fragmentContextView = this.additionalContextView;
            if (fragmentContextView != null) {
                fragmentContextView.checkVisibility();
            }
            checkLiveLocation(true);
            return;
        }
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidStart);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didStartedCall);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndedCall);
        FragmentContextView fragmentContextView2 = this.additionalContextView;
        if (fragmentContextView2 != null) {
            fragmentContextView2.checkVisibility();
        }
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            checkPlayer(true);
            updatePlaybackButton();
            return;
        }
        checkCall(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, AndroidUtilities.dp2(39.0f));
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.liveLocationsChanged) {
            checkLiveLocation(false);
        } else if (id == NotificationCenter.liveLocationsCacheChanged) {
            if (this.fragment instanceof ChatActivity) {
                if (((ChatActivity) this.fragment).getDialogId() == args[0].longValue()) {
                    checkLocationString();
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged || id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.didEndedCall) {
            checkPlayer(false);
        } else if (id == NotificationCenter.didStartedCall) {
            checkCall(false);
        } else {
            checkPlayer(false);
        }
    }

    private void checkLiveLocation(boolean create) {
        boolean show;
        String param;
        View fragmentView = this.fragment.getFragmentView();
        if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            create = true;
        }
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            show = LocationController.getLocationsCount() != 0;
        } else if (baseFragment instanceof IndexActivity) {
            show = LocationController.getLocationsCount() != 0;
        } else {
            show = LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
        }
        if (!show) {
            this.lastLocationSharingCount = -1;
            AndroidUtilities.cancelRunOnUIThread(this.checkLocationRunnable);
            if (this.visible) {
                this.visible = false;
                if (create) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            FragmentContextView.this.setVisibility(8);
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
                return;
            }
            return;
        }
        updateStyle(2);
        this.playButton.setImageDrawable(new ShareLocationDrawable(getContext(), 1));
        if (create && this.topPadding == 0.0f) {
            setTopPadding((float) AndroidUtilities.dp2(36.0f));
            this.yPosition = 0.0f;
        }
        if (!this.visible) {
            if (!create) {
                AnimatorSet animatorSet4 = this.animatorSet;
                if (animatorSet4 != null) {
                    animatorSet4.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet5 = new AnimatorSet();
                this.animatorSet = animatorSet5;
                animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)})});
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
            }
            this.visible = true;
            setVisibility(0);
        }
        if (this.fragment instanceof DialogsActivity) {
            String liveLocation = LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation);
            ArrayList<LocationController.SharingLocationInfo> infos = new ArrayList<>();
            for (int a = 0; a < 3; a++) {
                infos.addAll(LocationController.getInstance(a).sharingLocationsUI);
            }
            if (infos.size() == 1) {
                LocationController.SharingLocationInfo info = infos.get(0);
                int lower_id = (int) info.messageObject.getDialogId();
                if (lower_id > 0) {
                    param = UserObject.getFirstName(MessagesController.getInstance(info.messageObject.currentAccount).getUser(Integer.valueOf(lower_id)));
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(info.messageObject.currentAccount).getChat(Integer.valueOf(-lower_id));
                    if (chat != null) {
                        param = chat.title;
                    } else {
                        param = "";
                    }
                }
            } else {
                param = LocaleController.formatPluralString("Chats", infos.size());
            }
            String fullString = String.format(LocaleController.getString("AttachLiveLocationIsSharing", R.string.AttachLiveLocationIsSharing), new Object[]{liveLocation, param});
            int start = fullString.indexOf(liveLocation);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(fullString);
            this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor(Theme.key_inappPlayerPerformer)), start, liveLocation.length() + start, 18);
            this.titleTextView.setText(stringBuilder);
            return;
        }
        this.checkLocationRunnable.run();
        checkLocationString();
    }

    /* access modifiers changed from: private */
    public void checkLocationString() {
        String fullString;
        BaseFragment baseFragment = this.fragment;
        if ((baseFragment instanceof ChatActivity) && this.titleTextView != null) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            long dialogId = chatActivity.getDialogId();
            int currentAccount = chatActivity.getCurrentAccount();
            ArrayList<TLRPC.Message> messages = LocationController.getInstance(currentAccount).locationsCache.get(dialogId);
            if (!this.firstLocationsLoaded) {
                LocationController.getInstance(currentAccount).loadLiveLocations(dialogId);
                this.firstLocationsLoaded = true;
            }
            int locationSharingCount = 0;
            TLRPC.User notYouUser = null;
            if (messages != null) {
                int currentUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                int date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                for (int a = 0; a < messages.size(); a++) {
                    TLRPC.Message message = messages.get(a);
                    if (message.media != null && message.date + message.media.period > date) {
                        if (notYouUser == null && message.from_id != currentUserId) {
                            notYouUser = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(message.from_id));
                        }
                        locationSharingCount++;
                    }
                }
            }
            if (this.lastLocationSharingCount != locationSharingCount) {
                this.lastLocationSharingCount = locationSharingCount;
                String liveLocation = LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation);
                if (locationSharingCount == 0) {
                    fullString = liveLocation;
                } else {
                    int otherSharingCount = locationSharingCount - 1;
                    if (LocationController.getInstance(currentAccount).isSharingLocation(dialogId)) {
                        if (otherSharingCount == 0) {
                            fullString = String.format("%1$s - %2$s", new Object[]{liveLocation, LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName)});
                        } else if (otherSharingCount != 1 || notYouUser == null) {
                            fullString = String.format("%1$s - %2$s %3$s", new Object[]{liveLocation, LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName), LocaleController.formatPluralString("AndOther", otherSharingCount)});
                        } else {
                            fullString = String.format("%1$s - %2$s", new Object[]{liveLocation, LocaleController.formatString("SharingYouAndOtherName", R.string.SharingYouAndOtherName, UserObject.getFirstName(notYouUser))});
                        }
                    } else if (otherSharingCount != 0) {
                        fullString = String.format("%1$s - %2$s %3$s", new Object[]{liveLocation, UserObject.getFirstName(notYouUser), LocaleController.formatPluralString("AndOther", otherSharingCount)});
                    } else {
                        fullString = String.format("%1$s - %2$s", new Object[]{liveLocation, UserObject.getFirstName(notYouUser)});
                    }
                }
                if (!fullString.equals(this.lastString)) {
                    this.lastString = fullString;
                    int start = fullString.indexOf(liveLocation);
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(fullString);
                    this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
                    if (start >= 0) {
                        stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor(Theme.key_inappPlayerPerformer)), start, liveLocation.length() + start, 18);
                    }
                    this.titleTextView.setText(stringBuilder);
                }
            }
        }
    }

    private void checkPlayer(boolean create) {
        SpannableStringBuilder stringBuilder;
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        View fragmentView = this.fragment.getFragmentView();
        if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            create = true;
        }
        if (messageObject == null || messageObject.getId() == 0 || messageObject.isVideo()) {
            this.lastMessageObject = null;
            if ((VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) ? false : true) {
                checkCall(false);
            } else if (this.visible) {
                this.visible = false;
                if (create) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            FragmentContextView.this.setVisibility(8);
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
            }
        } else {
            int prevStyle = this.currentStyle;
            updateStyle(0);
            if (create && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
                FragmentContextView fragmentContextView = this.additionalContextView;
                if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
                    ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                } else {
                    ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                }
                this.yPosition = 0.0f;
            }
            if (!this.visible) {
                if (!create) {
                    AnimatorSet animatorSet4 = this.animatorSet;
                    if (animatorSet4 != null) {
                        animatorSet4.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    FragmentContextView fragmentContextView2 = this.additionalContextView;
                    if (fragmentContextView2 == null || fragmentContextView2.getVisibility() != 0) {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                    } else {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                    }
                    this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)})});
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                                AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                            }
                        }
                    });
                    this.animatorSet.start();
                }
                this.visible = true;
                setVisibility(0);
            }
            if (MediaController.getInstance().isMessagePaused()) {
                this.playButton.setImageResource(R.drawable.miniplayer_play);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
            } else {
                this.playButton.setImageResource(R.drawable.miniplayer_pause);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", R.string.AccActionPause));
            }
            if (this.lastMessageObject != messageObject || prevStyle != 0) {
                this.lastMessageObject = messageObject;
                if (messageObject.isVoice() || this.lastMessageObject.isRoundVideo()) {
                    ImageView imageView = this.playbackSpeedButton;
                    if (imageView != null) {
                        imageView.setAlpha(1.0f);
                        this.playbackSpeedButton.setEnabled(true);
                    }
                    this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                    stringBuilder = new SpannableStringBuilder(String.format("%s %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                    this.titleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                } else {
                    ImageView imageView2 = this.playbackSpeedButton;
                    if (imageView2 != null) {
                        imageView2.setAlpha(0.0f);
                        this.playbackSpeedButton.setEnabled(false);
                    }
                    this.titleTextView.setPadding(0, 0, 0, 0);
                    stringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                    this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
                }
                stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor(Theme.key_inappPlayerPerformer)), 0, messageObject.getMusicAuthor().length(), 18);
                this.titleTextView.setText(stringBuilder);
            }
        }
    }

    private void checkCall(boolean create) {
        View fragmentView = this.fragment.getFragmentView();
        if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            create = true;
        }
        if ((VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) ? false : true) {
            updateStyle(1);
            if (create && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
                FragmentContextView fragmentContextView = this.additionalContextView;
                if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
                    ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                } else {
                    ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                }
                this.yPosition = 0.0f;
            }
            if (!this.visible) {
                if (!create) {
                    AnimatorSet animatorSet2 = this.animatorSet;
                    if (animatorSet2 != null) {
                        animatorSet2.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    FragmentContextView fragmentContextView2 = this.additionalContextView;
                    if (fragmentContextView2 == null || fragmentContextView2.getVisibility() != 0) {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                    } else {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                    }
                    this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)})});
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                                AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                            }
                        }
                    });
                    this.animatorSet.start();
                }
                this.visible = true;
                setVisibility(0);
            }
        } else if (this.visible) {
            this.visible = false;
            if (create) {
                if (getVisibility() != 8) {
                    setVisibility(8);
                }
                setTopPadding(0.0f);
                return;
            }
            AnimatorSet animatorSet3 = this.animatorSet;
            if (animatorSet3 != null) {
                animatorSet3.cancel();
                this.animatorSet = null;
            }
            AnimatorSet animatorSet4 = new AnimatorSet();
            this.animatorSet = animatorSet4;
            animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
            this.animatorSet.setDuration(200);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                        FragmentContextView.this.setVisibility(8);
                        AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        }
    }
}
