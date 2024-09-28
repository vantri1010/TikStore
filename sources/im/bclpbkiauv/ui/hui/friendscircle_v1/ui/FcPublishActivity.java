package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FCEntitysRequest;
import com.bjz.comm.net.bean.FcEntitysBean;
import com.bjz.comm.net.bean.FcMediaBean;
import com.bjz.comm.net.bean.FcMediaResponseBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespTopicBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.premission.PermissionManager;
import com.bjz.comm.net.premission.observer.PermissionObserver;
import com.bjz.comm.net.utils.AppPreferenceUtil;
import com.bjz.comm.net.utils.RxHelper;
import com.blankj.utilcode.util.SizeUtils;
import com.google.android.exoplayer2.util.Log;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;
import im.bclpbkiauv.javaBean.fc.FriendsCirclePublishBean;
import im.bclpbkiauv.javaBean.fc.PublishFcBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.ImageUtils;
import im.bclpbkiauv.messenger.utils.TaskQueue;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.FcDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.PublishMenuAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.FcChooseLocationListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.glide.GlideUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ChooseAtContactsActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPublishActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImageSelectorActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.FcDialogUtil;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.method.AtUserMethod;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.method.MethodContext;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.AtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.FlowLayout;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagFlowLayout;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview.AddPictureRecyclerView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview.AddPictureTouchAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview.BottomDeleteDragListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hviews.MryRoundButtonDrawable;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dialogs.Util;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import io.reactivex.functions.Consumer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FcPublishActivity extends CommFcActivity implements FcChooseLocationListener, ChooseAtContactsActivity.ContactsActivityDelegate, PermissionObserver, NotificationCenter.NotificationCenterDelegate {
    private static final String[] NEEDED_PERMISSIONS = {PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, "android.permission.ACCESS_FINE_LOCATION"};
    private static final int REQUEST_LOCATION_PERMISSIONS = 1;
    /* access modifiers changed from: private */
    public static final String TAG = FcPublishActivity.class.getSimpleName();
    /* access modifiers changed from: private */
    public byte ATTACH_TYPE_IMAGE = 1;
    /* access modifiers changed from: private */
    public byte ATTACH_TYPE_NONE = 0;
    /* access modifiers changed from: private */
    public byte ATTACH_TYPE_VIDEO = 2;
    /* access modifiers changed from: private */
    public byte PUBLISH_BUTTON = 1;
    /* access modifiers changed from: private */
    public Adapter adapter = null;
    /* access modifiers changed from: private */
    public AddPictureRecyclerView addpicturerecycleview_photo;
    @BindView(2131296394)
    ImageView bivVideo;
    @BindView(2131296395)
    ImageView bivVideoH;
    /* access modifiers changed from: private */
    public ConstraintLayout constraintBottomParent;
    /* access modifiers changed from: private */
    public int currentSelectMediaType = 0;
    /* access modifiers changed from: private */
    public int currentUploadIndex = 0;
    @BindView(2131296594)
    EditText etContent;
    /* access modifiers changed from: private */
    public ImageSelectorActivity imageSelectorAlert;
    private int imgWidth;
    /* access modifiers changed from: private */
    public boolean isPublishing = false;
    /* access modifiers changed from: private */
    public ImageView ivBottmTrash;
    PublishHandler mHandler = new PublishHandler(this);
    /* access modifiers changed from: private */
    public TreeMap<String, String> mMapPhotos = new TreeMap<>();
    /* access modifiers changed from: private */
    public ArrayList<FcMediaBean> mMediasList = new ArrayList<>();
    /* access modifiers changed from: private */
    public String mStrContent = null;
    /* access modifiers changed from: private */
    public TaskQueue<String[]> mTaskQueue = new TaskQueue<>();
    /* access modifiers changed from: private */
    public int maxContentLen = 2000;
    /* access modifiers changed from: private */
    public byte mbytAttachType = this.ATTACH_TYPE_NONE;
    private PublishMenuAdapter menuAdapter;
    private MethodContext methodContext;
    /* access modifiers changed from: private */
    public int miVideoHeight;
    /* access modifiers changed from: private */
    public int miVideoWidth;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.PhotoEntry> photoEntries = new ArrayList<>();
    /* access modifiers changed from: private */
    public MediaController.PhotoEntry photoEntryVideo = null;
    /* access modifiers changed from: private */
    public ArrayList<Map.Entry<String, String>> photoList;
    private ImagePreSelectorActivity preSelectorActivity;
    /* access modifiers changed from: private */
    public AlertDialog progressDialog = null;
    /* access modifiers changed from: private */
    public MryTextView publishItemView;
    @BindView(2131297160)
    RelativeLayout rlContainer;
    private RelativeLayout rl_multimedia;
    @BindView(2131297234)
    RecyclerListView rvMenu;
    private HashMap<Object, Object> selectedPhotos;
    private ArrayList<Object> selectedPhotosOrder;
    private TagAdapter tagadapter;
    private TagFlowLayout tagflow_topics;
    /* access modifiers changed from: private */
    public TextView tvBottomTrash;
    private PublishFcBean unPublishFcBean;

    static /* synthetic */ int access$1308(FcPublishActivity x0) {
        int i = x0.currentUploadIndex;
        x0.currentUploadIndex = i + 1;
        return i;
    }

    public FcPublishActivity() {
    }

    public FcPublishActivity(PublishFcBean publishFcBean) {
        this.unPublishFcBean = publishFcBean;
    }

    public FcPublishActivity(ImagePreSelectorActivity preSelectorActivity2, HashMap<Object, Object> selectedPhotos2, ArrayList<Object> selectedPhotosOrder2, int currentSelectMediaType2) {
        this.preSelectorActivity = preSelectorActivity2;
        this.selectedPhotos = selectedPhotos2;
        this.selectedPhotosOrder = selectedPhotosOrder2;
        this.currentSelectMediaType = currentSelectMediaType2;
    }

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_friendscircle_publishv1;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.selectedTopicSuccessToPublish);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.selectedTopicSuccessToPublish);
        super.onFragmentDestroy();
    }

    /* access modifiers changed from: protected */
    public void initView() {
        initActionBar();
        useButterKnife();
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.addpicturerecycleview_photo = (AddPictureRecyclerView) this.fragmentView.findViewById(R.id.addpicturerecycleview_photo);
        this.constraintBottomParent = (ConstraintLayout) this.fragmentView.findViewById(R.id.constraintBottomParent);
        this.ivBottmTrash = (ImageView) this.fragmentView.findViewById(R.id.ivBottmTrash);
        this.tvBottomTrash = (TextView) this.fragmentView.findViewById(R.id.tvBottomTrash);
        this.rl_multimedia = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_multimedia);
        this.tvBottomTrash.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        TagFlowLayout tagFlowLayout = (TagFlowLayout) this.fragmentView.findViewById(R.id.tagflow_topics);
        this.tagflow_topics = tagFlowLayout;
        setTopicsInfo(tagFlowLayout);
        this.rl_multimedia.bringToFront();
        this.addpicturerecycleview_photo.bringToFront();
        Adapter adapter2 = new Adapter(this.mContext, getParentActivity());
        this.adapter = adapter2;
        this.addpicturerecycleview_photo.setAdapter(adapter2);
        this.addpicturerecycleview_photo.setLayoutManager(new GridLayoutManager(this.mContext, 3));
        this.adapter.setOnItemClickListener(new AddPictureTouchAdapter.AddPictureOnItemClickListener() {
            public final void onItemClick(Object obj, boolean z) {
                FcPublishActivity.this.lambda$initView$0$FcPublishActivity((SendMessagesHelper.SendingMediaInfo) obj, z);
            }
        });
        this.addpicturerecycleview_photo.setDragListener(new BottomDeleteDragListener<SendMessagesHelper.SendingMediaInfo, Holder>() {
            public boolean stateIsInSpecialArea(boolean isInArea, boolean isFingerUp, int position) {
                FcPublishActivity.this.ivBottmTrash.setSelected(isInArea);
                FcPublishActivity.this.constraintBottomParent.setSelected(isInArea);
                if (isInArea) {
                    FcPublishActivity.this.tvBottomTrash.setText(LocaleController.getString("griptodelete", R.string.griptodelete));
                    if (!isFingerUp || FcPublishActivity.this.adapter == null || FcPublishActivity.this.photoEntries.size() <= 0) {
                        return false;
                    }
                    FcPublishActivity.this.photoEntries.remove(position);
                    return false;
                }
                FcPublishActivity.this.tvBottomTrash.setText(LocaleController.getString("dragtheretodelete", R.string.dragtheretodelete));
                return false;
            }

            public boolean canDrag(Holder viewHolder, int position, SendMessagesHelper.SendingMediaInfo sendingMediaInfo) {
                return viewHolder.getItemViewType() != 1;
            }

            public void onPreDrag() {
                FcPublishActivity.doTransAniAfterGone(FcPublishActivity.this.constraintBottomParent, true);
                AndroidUtilities.hideKeyboard(FcPublishActivity.this.etContent);
            }

            public void onDraging() {
            }

            public void onReleasedDrag() {
                FcPublishActivity.doTransAniAfterGone(FcPublishActivity.this.constraintBottomParent, false);
                FcPublishActivity.this.setPublishButtonStatus();
            }
        });
        this.etContent.setHint(LocaleController.getString("friendscircle_publish_content_tip", R.string.friendscircle_publish_content_tip));
        initListenter();
        if (Theme.getCurrentTheme().isDark()) {
            this.etContent.setTextColor(this.mContext.getResources().getColor(R.color.white));
            this.etContent.setHintTextColor(this.mContext.getResources().getColor(R.color.white));
        } else {
            this.etContent.setTextColor(this.mContext.getResources().getColor(R.color.color_333333));
            this.etContent.setHintTextColor(this.mContext.getResources().getColor(R.color.color_D5D5D5));
        }
        MethodContext methodContext2 = new MethodContext();
        this.methodContext = methodContext2;
        methodContext2.setMethod(AtUserMethod.INSTANCE);
        this.methodContext.init(this.etContent);
        this.etContent.addTextChangedListener(new TextWatcher() {
            private int beforeCount;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                this.beforeCount = s.toString().length();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String setMsg = s.toString();
                if (setMsg.length() >= this.beforeCount && FcPublishActivity.this.etContent.getSelectionEnd() > 0 && setMsg.charAt(FcPublishActivity.this.etContent.getSelectionEnd() - 1) == '@') {
                    ChooseAtContactsActivity chooseAtContactsActivity = new ChooseAtContactsActivity(new Bundle());
                    chooseAtContactsActivity.setDelegate(FcPublishActivity.this);
                    FcPublishActivity.this.presentFragment(chooseAtContactsActivity, false);
                }
            }

            public void afterTextChanged(Editable s) {
                FcPublishActivity.this.setPublishButtonStatus();
            }
        });
        this.menuAdapter = new PublishMenuAdapter(getParentActivity(), 1).setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                FcPublishActivity.this.lambda$initView$1$FcPublishActivity(view, i);
            }
        }).setMenuUserItemClickListener($$Lambda$FcPublishActivity$6qIfQ4A23EaERw94A5MLUNdJPI.INSTANCE);
        this.rvMenu.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        this.rvMenu.setAdapter(this.menuAdapter);
    }

    public /* synthetic */ void lambda$initView$0$FcPublishActivity(SendMessagesHelper.SendingMediaInfo data, boolean isAddPictureItem) {
        if (isAddPictureItem) {
            openAttachMenu();
        }
    }

    public /* synthetic */ void lambda$initView$1$FcPublishActivity(View view, int type) {
        PublishMenuAdapter.Menu menu;
        if (type == 0) {
            ToastUtils.show((CharSequence) "developing");
        } else if (type == 1) {
            checkPermission();
        } else if (type == 2) {
            PublishMenuAdapter publishMenuAdapter = this.menuAdapter;
            if (publishMenuAdapter != null && (menu = publishMenuAdapter.getMenu(2)) != null) {
                presentFragment(new AddTopicActivity(((PublishMenuAdapter.MenuTopic) menu).topicMap));
            }
        } else if (type == 3) {
            ToastUtils.show((CharSequence) "developing");
        }
    }

    static /* synthetic */ void lambda$initView$2(View view, int type) {
    }

    private void initActionBar() {
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(R.drawable.ic_fc_back);
        ((ImageView) this.actionBar.getBackButton()).setColorFilter(Theme.getCurrentTheme().isLight() ? -6710887 : Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.SRC_IN);
        this.actionBar.setTitle(LocaleController.getString("friendscircle_publish_title", R.string.friendscircle_publish_title));
        this.actionBar.setDelegate(new ActionBar.ActionBarDelegate() {
            public final void onSearchFieldVisibilityChanged(boolean z) {
                FcPublishActivity.this.lambda$initActionBar$3$FcPublishActivity(z);
            }
        });
        MryTextView btnPublic = new MryTextView(this.mContext);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{this.mContext.getResources().getColor(R.color.color_87DFFA), this.mContext.getResources().getColor(R.color.color_2ECEFD)});
        btnPublic.setText(LocaleController.getString("publish", R.string.publish));
        btnPublic.setTextSize(13.0f);
        btnPublic.setTextColor(this.mContext.getResources().getColor(R.color.color_80FFFFFF));
        btnPublic.setGravity(17);
        gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(50.0f));
        gradientDrawable.setShape(0);
        btnPublic.setBackground(gradientDrawable);
        FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(56, 25.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(15.0f);
        layoutParams.topMargin = AndroidUtilities.dp(6.0f);
        this.publishItemView = (MryTextView) this.actionBar.createMenu().addItemView(1, btnPublic, layoutParams);
    }

    public /* synthetic */ void lambda$initActionBar$3$FcPublishActivity(boolean visible) {
        this.actionBar.getBackButton().setVisibility(visible ? 0 : 8);
    }

    /* access modifiers changed from: protected */
    public void initData() {
        if (this.unPublishFcBean != null) {
            loadUnPublishData();
            return;
        }
        HashMap<Object, Object> hashMap = this.selectedPhotos;
        if (hashMap != null && hashMap.size() > 0) {
            dealSelectPhotoResult(this.selectedPhotos, this.selectedPhotosOrder);
            setPublishButtonStatus();
        }
    }

    private void loadUnPublishData() {
        String content = this.unPublishFcBean.getContent();
        if (!TextUtils.isEmpty(content)) {
            this.etContent.setText(content);
            Iterator<FcEntitysBean> it = this.unPublishFcBean.getEntitys().iterator();
            while (it.hasNext()) {
                FcEntitysBean fcEntitysBean = it.next();
                if (fcEntitysBean != null) {
                    Editable text = this.etContent.getText();
                    if (text instanceof SpannableStringBuilder) {
                        text.replace(fcEntitysBean.getOffsetStart(), fcEntitysBean.getOffsetEnd(), this.methodContext.newSpannable(new AtUserSpan(fcEntitysBean.getUserID(), fcEntitysBean.getNickName(), fcEntitysBean.getUserName(), fcEntitysBean.getShowName(), fcEntitysBean.getAccessHash())));
                    }
                }
            }
        }
        HashMap<Integer, MediaController.PhotoEntry> selectedPhotos2 = this.unPublishFcBean.getSelectedPhotos();
        ArrayList<Integer> selectedPhotosOrder2 = this.unPublishFcBean.getSelectedPhotosOrder();
        if (selectedPhotos2 != null && !selectedPhotos2.isEmpty() && selectedPhotosOrder2 != null && !selectedPhotosOrder2.isEmpty()) {
            HashMap<Object, Object> sMap = new HashMap<>();
            ArrayList<Object> sOrder = new ArrayList<>();
            for (Map.Entry<Integer, MediaController.PhotoEntry> next : selectedPhotos2.entrySet()) {
                if (next != null) {
                    sMap.put(next.getKey(), next.getValue());
                    sOrder.add(next.getKey());
                }
            }
            this.selectedPhotos = sMap;
            this.selectedPhotosOrder = sOrder;
            this.currentSelectMediaType = this.unPublishFcBean.getCurrentSelectMediaType();
            dealSelectPhotoResult(this.selectedPhotos, this.selectedPhotosOrder);
        }
        PoiInfo locationInfo = this.unPublishFcBean.getLocationInfo();
        if (locationInfo != null) {
            doAfterChooseLocationSuccess(locationInfo);
        }
        HashMap<String, RespTopicBean.Item> topic = this.unPublishFcBean.getTopic();
        PublishMenuAdapter publishMenuAdapter = this.menuAdapter;
        if (publishMenuAdapter != null) {
            publishMenuAdapter.updateTopicRow(topic);
            this.tagadapter.setData(this.menuAdapter.getTopicRowRespTopicsList());
            this.tagadapter.notifyDataChanged();
        }
        setPublishButtonStatus();
    }

    public static void doTransAniAfterGone(final View view, final boolean show) {
        TranslateAnimation t;
        if (view != null) {
            if (show) {
                t = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) SizeUtils.dp2px(-60.0f));
            } else {
                t = new TranslateAnimation(0.0f, 0.0f, (float) SizeUtils.dp2px(-60.0f), 0.0f);
            }
            t.setFillAfter(true);
            t.setDuration(300);
            t.setRepeatCount(0);
            t.setRepeatMode(2);
            t.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    if (show) {
                        view.setVisibility(0);
                    }
                }

                public void onAnimationEnd(Animation animation) {
                    if (!show) {
                        view.setVisibility(8);
                        view.clearAnimation();
                    }
                }

                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(t);
        }
    }

    public void initListenter() {
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (FcPublishActivity.this.publishItemView.isSelected()) {
                        FcPublishActivity.this.exitProcess();
                    } else {
                        FcPublishActivity.this.finishFragment();
                    }
                } else if (id == FcPublishActivity.this.PUBLISH_BUTTON && !FcPublishActivity.this.isPublishing) {
                    if ((FcPublishActivity.this.photoEntryVideo == null || FcPublishActivity.this.photoEntryVideo.path.equals("")) && FcPublishActivity.this.photoEntries.size() == 0 && TextUtils.isEmpty(FcPublishActivity.this.etContent.getText().toString().trim())) {
                        FcToastUtils.show((CharSequence) LocaleController.getString("friendscircle_publish_tips_empty_content", R.string.friendscircle_publish_tips_empty_content));
                        return;
                    }
                    FcPublishActivity.this.mMapPhotos.clear();
                    FcPublishActivity.this.mMediasList.clear();
                    OkHttpUtils.getInstance().cancelTag("upload");
                    int unused = FcPublishActivity.this.currentUploadIndex = 0;
                    FcPublishActivity fcPublishActivity = FcPublishActivity.this;
                    if (!fcPublishActivity.isNetworkConnected(fcPublishActivity.mContext)) {
                        FcToastUtils.show((CharSequence) LocaleController.getString("error_net", R.string.error_net));
                    } else if (!TextUtils.isEmpty(FcPublishActivity.this.etContent.getText().toString().trim()) || ((FcPublishActivity.this.photoEntryVideo != null && !FcPublishActivity.this.photoEntryVideo.path.equals("")) || FcPublishActivity.this.photoEntries.size() > 0)) {
                        Editable text = FcPublishActivity.this.etContent.getText();
                        if (!TextUtils.isEmpty(text.toString().trim())) {
                            String unused2 = FcPublishActivity.this.mStrContent = text.toString().trim();
                        }
                        if (FcPublishActivity.this.mStrContent != null && FcPublishActivity.this.mStrContent.length() > FcPublishActivity.this.maxContentLen) {
                            WalletDialogUtil.showWalletDialog((Object) FcPublishActivity.this, "提示", (CharSequence) "你输入的内容不能超过2000个字符", "我知道了", false, (DialogInterface.OnClickListener) null).getPositiveButton().setTextColor(Color.parseColor("#FF2ECEFD"));
                        } else if (FcPublishActivity.this.photoEntryVideo != null && !FcPublishActivity.this.photoEntryVideo.path.equals("")) {
                            try {
                                if (FcPublishActivity.this.progressDialog == null) {
                                    AlertDialog unused3 = FcPublishActivity.this.progressDialog = new AlertDialog(FcPublishActivity.this.getParentActivity(), 3);
                                }
                                FcPublishActivity.this.progressDialog.show();
                                FcPublishActivity.this.progressDialog.setOnDismissListener($$Lambda$FcPublishActivity$4$4dsfUJUSWjNIJb20gxApZaqwkxY.INSTANCE);
                            } catch (Exception e) {
                            }
                            FcPublishActivity fcPublishActivity2 = FcPublishActivity.this;
                            byte unused4 = fcPublishActivity2.mbytAttachType = fcPublishActivity2.ATTACH_TYPE_VIDEO;
                            FcPublishActivity.this.processVideo();
                            if (FcPublishActivity.this.mMapPhotos.size() > 0) {
                                FcPublishActivity.this.mTaskQueue.inputQueue(new String[]{FcPublishActivity.this.photoEntryVideo.path, (String) FcPublishActivity.this.mMapPhotos.get(FcPublishActivity.this.photoEntryVideo.path)});
                                FcPublishActivity.this.readyUpload();
                            }
                        } else if (FcPublishActivity.this.photoEntries.size() > 0) {
                            try {
                                if (FcPublishActivity.this.progressDialog == null) {
                                    AlertDialog unused5 = FcPublishActivity.this.progressDialog = new AlertDialog(FcPublishActivity.this.getParentActivity(), 3);
                                }
                                FcPublishActivity.this.progressDialog.show();
                                FcPublishActivity.this.progressDialog.setOnDismissListener($$Lambda$FcPublishActivity$4$TcBFwxHHznY06darEiGvYbFptiA.INSTANCE);
                            } catch (Exception e2) {
                            }
                            FcPublishActivity fcPublishActivity3 = FcPublishActivity.this;
                            byte unused6 = fcPublishActivity3.mbytAttachType = fcPublishActivity3.ATTACH_TYPE_IMAGE;
                            FcPublishActivity fcPublishActivity4 = FcPublishActivity.this;
                            fcPublishActivity4.processPhotos(fcPublishActivity4.mMapPhotos);
                        } else if (TextUtils.isEmpty(FcPublishActivity.this.etContent.getText().toString().trim())) {
                            FcToastUtils.show((CharSequence) LocaleController.getString("friendscircle_publish_tips_empty_content", R.string.friendscircle_publish_tips_empty_content));
                        } else {
                            try {
                                if (FcPublishActivity.this.progressDialog == null) {
                                    AlertDialog unused7 = FcPublishActivity.this.progressDialog = new AlertDialog(FcPublishActivity.this.getParentActivity(), 3);
                                }
                                FcPublishActivity.this.progressDialog.show();
                                FcPublishActivity.this.progressDialog.setOnDismissListener($$Lambda$FcPublishActivity$4$94hU7wFRxmxLXDRyvYclLOi_ukA.INSTANCE);
                            } catch (Exception e3) {
                            }
                            FcPublishActivity fcPublishActivity5 = FcPublishActivity.this;
                            byte unused8 = fcPublishActivity5.mbytAttachType = fcPublishActivity5.ATTACH_TYPE_NONE;
                            FcPublishActivity.this.sendDataToServer();
                        }
                    }
                }
            }
        });
        this.etContent.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /* access modifiers changed from: private */
    public void sendDataToServer() {
        FriendsCirclePublishBean publishBean;
        this.isPublishing = true;
        FriendsCirclePublishBean publishBean2 = new FriendsCirclePublishBean();
        publishBean2.setUserID((long) getUserConfig().getCurrentUser().id);
        publishBean2.setPermission(1);
        String replaceStr = this.mStrContent;
        ArrayList<FCEntitysRequest> atUserBeanList = new ArrayList<>();
        Editable text = this.etContent.getText();
        AtUserSpan[] spans = (AtUserSpan[]) text.getSpans(0, text.length(), AtUserSpan.class);
        if (spans.length > 1) {
            Arrays.sort(spans, new Comparator(text) {
                private final /* synthetic */ Editable f$0;

                {
                    this.f$0 = r1;
                }

                public final int compare(Object obj, Object obj2) {
                    return FcPublishActivity.lambda$sendDataToServer$4(this.f$0, (AtUserSpan) obj, (AtUserSpan) obj2);
                }
            });
        }
        int length = spans.length;
        int i = 0;
        while (i < length) {
            AtUserSpan atUserSpan = spans[i];
            FriendsCirclePublishBean publishBean3 = publishBean2;
            atUserBeanList.add(new FCEntitysRequest("@" + atUserSpan.getNickName(), atUserSpan.getUserID(), atUserSpan.getAccessHash()));
            if (!TextUtils.isEmpty(atUserSpan.getUserName())) {
                String s = "@" + atUserSpan.getNickName() + SQLBuilder.PARENTHESES_LEFT + atUserSpan.getUserName() + SQLBuilder.PARENTHESES_RIGHT;
                if (replaceStr.contains(s)) {
                    replaceStr = replaceStr.replace(s, "@" + atUserSpan.getNickName());
                }
            }
            i++;
            publishBean2 = publishBean3;
        }
        FriendsCirclePublishBean publishBean4 = publishBean2;
        if (atUserBeanList.size() > 0) {
            publishBean = publishBean4;
            publishBean.setEntitys(atUserBeanList);
        } else {
            publishBean = publishBean4;
        }
        publishBean.setContent(replaceStr);
        ArrayList<FcMediaBean> arrayList = this.mMediasList;
        if (arrayList != null && arrayList.size() > 0) {
            if (this.mMediasList.size() > 1) {
                for (int i2 = 0; i2 < this.mMediasList.size(); i2++) {
                    this.mMediasList.get(i2).setSeq(i2);
                }
            }
            publishBean.setMedias(this.mMediasList);
            publishBean.setContentType(this.mMediasList.get(0).getExt());
        }
        PublishMenuAdapter publishMenuAdapter = this.menuAdapter;
        if (publishMenuAdapter != null) {
            PoiInfo locationInfo = publishMenuAdapter.getLocationRowLocationInfo();
            if (locationInfo != null) {
                LatLng ll = locationInfo.getLocation();
                String name = locationInfo.getName();
                if (!(TextUtils.isEmpty(name) || ll == null || ll.latitude == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE || ll.longitude == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE)) {
                    publishBean.setLatitude(ll.latitude);
                    publishBean.setLongitude(ll.longitude);
                    String city = locationInfo.getCity();
                    String address = locationInfo.getAddress();
                    publishBean.setLocationName(name);
                    publishBean.setLocationCity(city);
                    publishBean.setLocationAddress(address);
                }
            }
            publishBean.setTopics(this.menuAdapter.getTopicRowTopicsBeanList());
        }
        String json = new Gson().toJson((Object) publishBean);
        Log.d("publish", "json ：" + json);
        RxHelper.getInstance().sendRequest(TAG, ApiFactory.getInstance().getApiMomentForum().publish(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json)), new Consumer() {
            public final void accept(Object obj) {
                FcPublishActivity.this.lambda$sendDataToServer$8$FcPublishActivity((BResponse) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                FcPublishActivity.this.lambda$sendDataToServer$10$FcPublishActivity((Throwable) obj);
            }
        });
    }

    static /* synthetic */ int lambda$sendDataToServer$4(Editable text, AtUserSpan o1, AtUserSpan o2) {
        return text.getSpanStart(o1) - text.getSpanStart(o2);
    }

    public /* synthetic */ void lambda$sendDataToServer$8$FcPublishActivity(BResponse response) throws Exception {
        try {
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
            XDialog.Builder builder = new XDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("image_select_tip", R.string.image_select_tip));
            builder.setOutSideCancel(false);
            if (response.isState()) {
                RespFcListBean data = (RespFcListBean) response.Data;
                if (data == null || data.getForumID() <= 0) {
                    this.isPublishing = false;
                    FcDialogUtil.publishError(this, $$Lambda$FcPublishActivity$pTejqap4emTfvwPkOuF7KuwIcCo.INSTANCE, (DialogInterface.OnDismissListener) null);
                } else {
                    if (!(data.getMedias() == null || data.getMedias().size() == 0)) {
                        data.getMedias().get(0).setHeight(this.miVideoHeight);
                        data.getMedias().get(0).setWidth(this.miVideoWidth);
                        data.setComments(new ArrayList());
                    }
                    setPublishBack(data);
                    FcToastUtils.show((int) R.string.friendscircle_publish_success);
                    finishFragment();
                }
                return;
            }
            builder.setMessage(response.Message);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), $$Lambda$FcPublishActivity$IwnZitgvyqBo9gPwjjj1C6IaU80.INSTANCE);
            showDialog(builder.create());
            this.isPublishing = false;
        } catch (Exception e) {
            e.printStackTrace();
            resetData();
            FcDialogUtil.publishError(this, $$Lambda$FcPublishActivity$GC1bNXZEt86cU2YlLspGSVbScAg.INSTANCE, (DialogInterface.OnDismissListener) null);
        }
    }

    static /* synthetic */ void lambda$null$5(View dialog) {
    }

    static /* synthetic */ void lambda$null$6(DialogInterface dialogInterface, int i) {
    }

    static /* synthetic */ void lambda$null$7(View dialog) {
    }

    public /* synthetic */ void lambda$sendDataToServer$10$FcPublishActivity(Throwable throwable) throws Exception {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        resetData();
        FcDialogUtil.publishError(this, $$Lambda$FcPublishActivity$1_aTknKipq1KuZvhpkFOLLAkSok.INSTANCE, (DialogInterface.OnDismissListener) null);
    }

    static /* synthetic */ void lambda$null$9(View dialog) {
    }

    private void setPublishBack(RespFcListBean data) {
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcPublishSuccess, TAG, data);
    }

    private void resetData() {
        this.isPublishing = false;
        this.mbytAttachType = this.ATTACH_TYPE_NONE;
        this.mTaskQueue.cleanQueue();
        this.mMapPhotos.clear();
    }

    public void onResume() {
        super.onResume();
        getLocationController().startLocationLookupForPeopleNearby(false);
    }

    public void onPause() {
        super.onPause();
        getLocationController().startLocationLookupForPeopleNearby(true);
    }

    /* access modifiers changed from: protected */
    public void processPhotos(final Map<String, String> map) {
        new Thread(new Runnable() {
            public void run() {
                boolean z = true;
                if (FcPublishActivity.this.photoEntries != null) {
                    if (FcPublishActivity.this.photoEntries.size() != 1 || !((MediaController.PhotoEntry) FcPublishActivity.this.photoEntries.get(0)).path.toLowerCase().endsWith(".gif")) {
                        int i = 0;
                        while (i < FcPublishActivity.this.photoEntries.size()) {
                            String strThumb = "";
                            String strPhoto = "";
                            String strPath = ((MediaController.PhotoEntry) FcPublishActivity.this.photoEntries.get(i)).path;
                            Bitmap bitmap = ImageLoader.loadBitmap(strPath, (Uri) null, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), z);
                            if (bitmap == null) {
                                bitmap = ImageLoader.loadBitmap(strPath, (Uri) null, 800.0f, 800.0f, z);
                            }
                            TLRPC.PhotoSize size = ImageLoader.scaleAndSaveImage(bitmap, 500.0f, 500.0f, 80, z);
                            if (size != null) {
                                strThumb = (size.location.volume_id != -2147483648L ? FileLoader.getDirectory(0) : FileLoader.getDirectory(4)).getPath() + "/" + size.location.volume_id + "_" + size.location.local_id + ".jpg";
                            }
                            String str = "/";
                            String str2 = "_";
                            String str3 = ".jpg";
                            TLRPC.PhotoSize photoSize = size;
                            TLRPC.PhotoSize size2 = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
                            if (size2 != null) {
                                strPhoto = (size2.location.volume_id != -2147483648L ? FileLoader.getDirectory(0) : FileLoader.getDirectory(4)).getPath() + str + size2.location.volume_id + str2 + size2.location.local_id + str3;
                            }
                            if (!strPhoto.equals("")) {
                                KLog.e(FcPublishActivity.TAG, "添加map == " + strPhoto);
                                map.put(strPhoto, strThumb);
                            }
                            if (bitmap != null) {
                                bitmap.recycle();
                            }
                            i++;
                            z = true;
                        }
                    } else {
                        String strPath2 = ((MediaController.PhotoEntry) FcPublishActivity.this.photoEntries.get(0)).path;
                        if (!TextUtils.isEmpty(strPath2)) {
                            map.put(strPath2, strPath2);
                        }
                    }
                }
                Message msg = Message.obtain();
                msg.what = 1;
                FcPublishActivity.this.mHandler.sendMessage(msg);
            }
        }).start();
    }

    public void doAfterChooseLocationSuccess(PoiInfo locationInfo) {
        PublishMenuAdapter publishMenuAdapter = this.menuAdapter;
        if (publishMenuAdapter != null) {
            publishMenuAdapter.updateLocationRow(locationInfo);
        }
        setPublishButtonStatus();
    }

    public void didSelectContact(TLRPC.User user) {
        String str;
        if (user != null && !TextUtils.isEmpty(user.first_name)) {
            String nickName = user.first_name.trim();
            Editable text = this.etContent.getText();
            if (text instanceof SpannableStringBuilder) {
                int index = text.toString().indexOf("@", this.etContent.getSelectionEnd() - 1);
                if (index != -1) {
                    text.delete(index, index + 1);
                }
                AtUserSpan insertAtUserSpan = new AtUserSpan(user.id, nickName, user.username, "@" + nickName, user.access_hash);
                AtUserSpan[] spans = (AtUserSpan[]) text.getSpans(0, text.length(), AtUserSpan.class);
                Arrays.sort(spans, new Comparator(text) {
                    private final /* synthetic */ Editable f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return FcPublishActivity.lambda$didSelectContact$11(this.f$0, (AtUserSpan) obj, (AtUserSpan) obj2);
                    }
                });
                for (AtUserSpan result : spans) {
                    if (TextUtils.equals(result.getShowName(), insertAtUserSpan.getShowName())) {
                        if (result.getUserID() == insertAtUserSpan.getUserID()) {
                            insertAtUserSpan.setShowName(result.getShowName());
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append(insertAtUserSpan.getShowName());
                            if (TextUtils.isEmpty(insertAtUserSpan.getUserName())) {
                                str = "";
                            } else {
                                str = SQLBuilder.PARENTHESES_LEFT + insertAtUserSpan.getUserName() + SQLBuilder.PARENTHESES_RIGHT;
                            }
                            sb.append(str);
                            insertAtUserSpan.setShowName(sb.toString());
                        }
                    } else if (result.getUserID() == insertAtUserSpan.getUserID()) {
                        insertAtUserSpan.setShowName(result.getShowName());
                    }
                }
                this.etContent.getText().insert(this.etContent.getSelectionStart(), this.methodContext.newSpannable(insertAtUserSpan)).insert(this.etContent.getSelectionStart(), " ");
            }
        }
    }

    static /* synthetic */ int lambda$didSelectContact$11(Editable text, AtUserSpan o1, AtUserSpan o2) {
        return text.getSpanStart(o1) - text.getSpanStart(o2);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.selectedTopicSuccessToPublish) {
            PublishMenuAdapter publishMenuAdapter = this.menuAdapter;
            if (publishMenuAdapter != null) {
                publishMenuAdapter.updateTopicRow(args[0]);
                this.tagadapter.setData(this.menuAdapter.getTopicRowRespTopicsList());
                this.tagadapter.notifyDataChanged();
            }
            setPublishButtonStatus();
        }
    }

    static class PublishHandler extends Handler {
        private final WeakReference<FcPublishActivity> mActivity;

        public PublishHandler(FcPublishActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 && ((FcPublishActivity) this.mActivity.get()).mMapPhotos.size() > 0) {
                ((FcPublishActivity) this.mActivity.get()).readyUpload();
            }
        }
    }

    /* access modifiers changed from: private */
    public void processVideo() {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(this.photoEntryVideo.path, 1);
        if (thumb != null) {
            TLRPC.PhotoSize size = ImageLoader.scaleAndSaveImage(thumb, 500.0f, 500.0f, 90, true);
            if (size != null) {
                String strCache = FileLoader.getDirectory(size.location.volume_id != -2147483648L ? 0 : 4).getPath();
                TreeMap<String, String> treeMap = this.mMapPhotos;
                String str = this.photoEntryVideo.path;
                treeMap.put(str, strCache + "/" + size.location.volume_id + "_" + size.location.local_id + ".jpg");
                KLog.d(this.photoEntryVideo.path + "----缩略图----" + strCache + "/" + size.location.volume_id + "_" + size.location.local_id + ".jpg");
            }
            thumb.recycle();
        }
    }

    private void openAttachMenu() {
        if (getParentActivity() != null) {
            createChatAttachView();
            if (this.adapter.getData().size() == 0) {
                this.imageSelectorAlert.setCurrentSelectMediaType(0);
            } else {
                int i = this.currentSelectMediaType;
                if (i == 1) {
                    this.imageSelectorAlert.setCurrentSelectMediaType(i);
                }
            }
            this.imageSelectorAlert.loadGalleryPhotos();
            this.imageSelectorAlert.setMaxSelectedPhotos(9 - this.adapter.getData().size(), true);
            this.imageSelectorAlert.init();
            this.imageSelectorAlert.setCancelable(false);
            showDialog(this.imageSelectorAlert);
        }
    }

    /* access modifiers changed from: private */
    public void setPublishButtonStatus() {
        MediaController.PhotoEntry photoEntry;
        PublishMenuAdapter publishMenuAdapter;
        PublishMenuAdapter publishMenuAdapter2;
        if (!TextUtils.isEmpty(this.etContent.getText().toString()) || (((photoEntry = this.photoEntryVideo) != null && !photoEntry.path.equals("")) || this.photoEntries.size() > 0 || (!((publishMenuAdapter = this.menuAdapter) == null || publishMenuAdapter.getMenuLocation() == null || !this.menuAdapter.getMenuLocation().hasAddress()) || ((publishMenuAdapter2 = this.menuAdapter) != null && !publishMenuAdapter2.getTopicRowTopicsBeanList().isEmpty())))) {
            this.publishItemView.setSelected(true);
            this.publishItemView.setTextColor(this.mContext.getResources().getColor(R.color.white));
            return;
        }
        this.publishItemView.setSelected(false);
        this.publishItemView.setTextColor(this.mContext.getResources().getColor(R.color.color_80FFFFFF));
    }

    /* access modifiers changed from: private */
    public void createChatAttachView() {
        if (getParentActivity() != null && this.imageSelectorAlert == null) {
            AnonymousClass7 r0 = new ImageSelectorActivity(getParentActivity(), this, false) {
                public void dismissInternal() {
                    if (FcPublishActivity.this.imageSelectorAlert.isShowing()) {
                        AndroidUtilities.requestAdjustResize(FcPublishActivity.this.getParentActivity(), FcPublishActivity.this.classGuid);
                        for (int i = 0; i < FcPublishActivity.this.photoEntries.size(); i++) {
                            if (((MediaController.PhotoEntry) FcPublishActivity.this.photoEntries.get(i)).isVideo) {
                                super.dismissInternal();
                                return;
                            }
                        }
                        if (FcPublishActivity.this.adapter.getData().size() > 0) {
                            FcPublishActivity.this.setPublishButtonStatus();
                            FcPublishActivity.this.adapter.notifyDataSetChanged();
                        }
                    }
                    FcPublishActivity.this.addpicturerecycleview_photo.setVisibility(0);
                    FcPublishActivity.this.rlContainer.setVisibility(8);
                    super.dismissInternal();
                }
            };
            this.imageSelectorAlert = r0;
            ImagePreSelectorActivity imagePreSelectorActivity = this.preSelectorActivity;
            if (imagePreSelectorActivity != null) {
                r0.setImagePreSelectorActivity(imagePreSelectorActivity);
            }
            this.imageSelectorAlert.setDelegate(new ImageSelectorActivity.ChatAttachViewDelegate() {
                public void didPressedButton(int button, boolean arg, boolean notify, int scheduleDate) {
                    if (FcPublishActivity.this.getParentActivity() != null && FcPublishActivity.this.imageSelectorAlert != null) {
                        if (button == 8 || button == 7 || (button == 4 && !FcPublishActivity.this.imageSelectorAlert.getSelectedPhotos().isEmpty())) {
                            if (button != 8) {
                                FcPublishActivity.this.imageSelectorAlert.dismiss();
                            }
                            HashMap<Object, Object> selectedPhotos = FcPublishActivity.this.imageSelectorAlert.getSelectedPhotos();
                            ArrayList<Object> selectedPhotosOrder = FcPublishActivity.this.imageSelectorAlert.getSelectedPhotosOrder();
                            if (!selectedPhotos.isEmpty() && !selectedPhotosOrder.isEmpty()) {
                                FcPublishActivity.this.dealSelectPhotoResult(selectedPhotos, selectedPhotosOrder);
                            }
                            if (FcPublishActivity.this.imageSelectorAlert != null) {
                                FcPublishActivity fcPublishActivity = FcPublishActivity.this;
                                int unused = fcPublishActivity.currentSelectMediaType = fcPublishActivity.imageSelectorAlert.getCurrentSelectMediaType();
                            }
                        } else if (FcPublishActivity.this.imageSelectorAlert != null) {
                            FcPublishActivity.this.imageSelectorAlert.dismissWithButtonClick(button);
                        }
                    }
                }

                public void didSelectBot(TLRPC.User user) {
                }

                public void onCameraOpened() {
                }

                public View getRevealView() {
                    return null;
                }

                public void needEnterComment() {
                    AndroidUtilities.setAdjustResizeToNothing(FcPublishActivity.this.getParentActivity(), FcPublishActivity.this.classGuid);
                    FcPublishActivity.this.fragmentView.requestLayout();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void dealSelectPhotoResult(HashMap<Object, Object> selectedPhotos2, ArrayList<Object> selectedPhotosOrder2) {
        if (!selectedPhotos2.isEmpty() && !selectedPhotosOrder2.isEmpty()) {
            for (int a = 0; a < selectedPhotosOrder2.size(); a++) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) selectedPhotos2.get(selectedPhotosOrder2.get(a));
                if (photoEntry != null) {
                    SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                    MediaController.PhotoEntry photoEntry1 = new MediaController.PhotoEntry(photoEntry.bucketId, photoEntry.imageId, photoEntry.dateTaken, photoEntry.path, photoEntry.orientation, photoEntry.isVideo);
                    if (photoEntry.imagePath != null) {
                        photoEntry1.orientation = 0;
                        info.path = photoEntry.imagePath;
                    } else if (photoEntry.path != null) {
                        info.path = photoEntry.path;
                    }
                    photoEntry1.path = info.path;
                    info.isVideo = photoEntry.isVideo;
                    ArrayList<TLRPC.InputDocument> arrayList = null;
                    info.caption = photoEntry.caption != null ? photoEntry.caption.toString() : null;
                    info.entities = photoEntry.entities;
                    if (!photoEntry.stickers.isEmpty()) {
                        arrayList = new ArrayList<>(photoEntry.stickers);
                    }
                    info.masks = arrayList;
                    info.ttl = photoEntry.ttl;
                    info.videoEditedInfo = photoEntry.editedInfo;
                    info.canDeleteAfter = photoEntry.canDeleteAfter;
                    this.photoEntries.add(photoEntry1);
                    if (photoEntry.isVideo) {
                        showVideo(photoEntry);
                    } else {
                        if (info.path.toLowerCase().endsWith("gif")) {
                            this.adapter.setMaxCount(1);
                        } else {
                            this.adapter.setMaxCount(9);
                        }
                        this.adapter.addItem(info);
                    }
                    photoEntry.reset();
                }
            }
        }
    }

    private void showVideo(MediaController.PhotoEntry photoEntry) {
        this.addpicturerecycleview_photo.setVisibility(4);
        this.rlContainer.setVisibility(0);
        if (this.photoEntryVideo == null) {
            MediaController.PhotoEntry photoEntry2 = new MediaController.PhotoEntry(photoEntry.bucketId, photoEntry.imageId, photoEntry.dateTaken, photoEntry.path, photoEntry.orientation, photoEntry.isVideo);
            this.photoEntryVideo = photoEntry2;
            photoEntry2.editedInfo = photoEntry.editedInfo;
        }
        if (photoEntry.editedInfo != null) {
            this.photoEntryVideo.path = photoEntry.editedInfo.originalPath;
            if (photoEntry.editedInfo.rotationValue == 90 || photoEntry.editedInfo.rotationValue == 270) {
                this.miVideoWidth = photoEntry.editedInfo.resultHeight;
                this.miVideoHeight = photoEntry.editedInfo.resultWidth;
            } else {
                this.miVideoWidth = photoEntry.editedInfo.resultWidth;
                this.miVideoHeight = photoEntry.editedInfo.resultHeight;
            }
        } else {
            this.photoEntryVideo.path = photoEntry.path;
            this.miVideoWidth = 0;
            this.miVideoHeight = 0;
        }
        int i = this.miVideoWidth;
        if (i != 0) {
            if ((((float) i) * 1.0f) / ((float) this.miVideoHeight) > 1.0f) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.rlContainer.getLayoutParams();
                layoutParams.width = AndroidUtilities.dp(239.0f);
                layoutParams.height = AndroidUtilities.dp(180.0f);
                this.bivVideo.setVisibility(8);
                this.bivVideoH.setVisibility(0);
                ImageUtils.LoadRoundedCornerImg(getParentActivity(), this.bivVideoH, this.photoEntryVideo.path, AndroidUtilities.dp(10.0f));
            } else {
                RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.rlContainer.getLayoutParams();
                layoutParams2.width = AndroidUtilities.dp(213.0f);
                layoutParams2.height = AndroidUtilities.dp(300.0f);
                this.bivVideo.setVisibility(0);
                this.bivVideoH.setVisibility(8);
                ImageUtils.LoadRoundedCornerImg(getParentActivity(), this.bivVideo, this.photoEntryVideo.path, AndroidUtilities.dp(10.0f));
            }
        }
        setPublishButtonStatus();
    }

    /* access modifiers changed from: private */
    public void exitProcess() {
        FcDialogUtil.exitPublish(this, new FcDialog.OnConfirmClickListener() {
            public final void onClick(View view) {
                FcPublishActivity.this.lambda$exitProcess$12$FcPublishActivity(view);
            }
        }, new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                FcPublishActivity.this.lambda$exitProcess$13$FcPublishActivity(dialogInterface);
            }
        });
    }

    public /* synthetic */ void lambda$exitProcess$12$FcPublishActivity(View dialog) {
        saveUnPublishData();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                FcPublishActivity.this.finishFragment();
            }
        }, 1200);
    }

    public /* synthetic */ void lambda$exitProcess$13$FcPublishActivity(DialogInterface dialog) {
        finishFragment();
    }

    private void saveUnPublishData() {
        PublishMenuAdapter.Menu menu;
        PoiInfo locationInfo;
        PublishFcBean publishBean = new PublishFcBean();
        publishBean.setPermission(1);
        ArrayList<FcEntitysBean> atUserBeanList = new ArrayList<>();
        Editable text = this.etContent.getText();
        AtUserSpan[] spans = (AtUserSpan[]) text.getSpans(0, text.length(), AtUserSpan.class);
        if (spans.length > 1) {
            Arrays.sort(spans, new Comparator(text) {
                private final /* synthetic */ Editable f$0;

                {
                    this.f$0 = r1;
                }

                public final int compare(Object obj, Object obj2) {
                    return FcPublishActivity.lambda$saveUnPublishData$14(this.f$0, (AtUserSpan) obj, (AtUserSpan) obj2);
                }
            });
        }
        for (AtUserSpan atUserSpan : spans) {
            atUserBeanList.add(new FcEntitysBean(atUserSpan.getUserID(), atUserSpan.getNickName(), atUserSpan.getUserName(), atUserSpan.getShowName(), atUserSpan.getAccessHash(), text.getSpanStart(atUserSpan), text.getSpanEnd(atUserSpan)));
        }
        if (atUserBeanList.size() > 0) {
            publishBean.setEntitys(atUserBeanList);
        }
        if (!TextUtils.isEmpty(text.toString().trim())) {
            publishBean.setContent(text.toString().trim());
        }
        PublishMenuAdapter publishMenuAdapter = this.menuAdapter;
        if (!(publishMenuAdapter == null || (locationInfo = publishMenuAdapter.getLocationRowLocationInfo()) == null)) {
            publishBean.setLocationInfo(locationInfo);
        }
        HashMap<Integer, MediaController.PhotoEntry> selectedPhotos2 = new HashMap<>();
        ArrayList<Integer> selectedPhotosOrder2 = new ArrayList<>();
        MediaController.PhotoEntry photoEntry = this.photoEntryVideo;
        if (photoEntry == null || TextUtils.isEmpty(photoEntry.path)) {
            ArrayList<MediaController.PhotoEntry> arrayList = this.photoEntries;
            if (arrayList != null && arrayList.size() > 0) {
                Iterator<MediaController.PhotoEntry> it = this.photoEntries.iterator();
                while (it.hasNext()) {
                    MediaController.PhotoEntry photoEntry2 = it.next();
                    selectedPhotos2.put(Integer.valueOf(photoEntry2.imageId), photoEntry2);
                    selectedPhotosOrder2.add(Integer.valueOf(photoEntry2.imageId));
                }
            }
        } else {
            selectedPhotos2.put(Integer.valueOf(this.photoEntryVideo.imageId), this.photoEntryVideo);
            selectedPhotosOrder2.add(Integer.valueOf(this.photoEntryVideo.imageId));
        }
        if (selectedPhotos2.size() > 0) {
            publishBean.setSelectedPhotos(selectedPhotos2);
            publishBean.setSelectedPhotosOrder(selectedPhotosOrder2);
            publishBean.setCurrentSelectMediaType(this.currentSelectMediaType);
        }
        PublishMenuAdapter publishMenuAdapter2 = this.menuAdapter;
        if (!(publishMenuAdapter2 == null || (menu = publishMenuAdapter2.getMenu(2)) == null)) {
            publishBean.setTopic(((PublishMenuAdapter.MenuTopic) menu).topicMap);
        }
        AppPreferenceUtil.putString("PublishFcBean", new Gson().toJson((Object) publishBean));
    }

    static /* synthetic */ int lambda$saveUnPublishData$14(Editable text, AtUserSpan o1, AtUserSpan o2) {
        return text.getSpanStart(o1) - text.getSpanStart(o2);
    }

    public boolean onBackPressed() {
        if (!this.publishItemView.isSelected()) {
            return super.onBackPressed();
        }
        exitProcess();
        return false;
    }

    @OnClick({2131296394, 2131296395, 2131296794})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.biv_video /*2131296394*/:
            case R.id.biv_video_h /*2131296395*/:
                if (this.photoEntries != null) {
                    if (this.imageSelectorAlert == null) {
                        createChatAttachView();
                    }
                    this.photoEntries.clear();
                    this.photoEntries.add(this.photoEntryVideo);
                    this.imageSelectorAlert.previewSelectedPhotos(0, this.photoEntries);
                    return;
                }
                return;
            case R.id.iv_close /*2131296794*/:
                FcDialogUtil.isDeleteThisVideo(this, new FcDialog.OnConfirmClickListener() {
                    public final void onClick(View view) {
                        FcPublishActivity.this.lambda$onViewClicked$15$FcPublishActivity(view);
                    }
                }, (DialogInterface.OnDismissListener) null);
                return;
            default:
                return;
        }
    }

    public /* synthetic */ void lambda$onViewClicked$15$FcPublishActivity(View dialog) {
        this.photoEntries.clear();
        this.adapter.notifyDataSetChanged();
        this.rlContainer.setVisibility(8);
        this.addpicturerecycleview_photo.setVisibility(0);
        this.photoEntryVideo.path = "";
        setPublishButtonStatus();
    }

    private void checkPermission() {
        PermissionManager.getInstance(this.mContext).requestPermission(this, 1, NEEDED_PERMISSIONS);
    }

    public void onRequestPermissionSuccess(int flag) {
        if (flag == 1) {
            FcChooseLocationActivity friendsCircleSearchLocationActivity = new FcChooseLocationActivity();
            friendsCircleSearchLocationActivity.setFcChooseLocationListener(this);
            presentFragment(friendsCircleSearchLocationActivity);
        }
    }

    public void onRequestPermissionFail(int flag) {
    }

    public class Adapter extends AddPictureTouchAdapter<SendMessagesHelper.SendingMediaInfo, Holder> {
        int screenWidth;

        public Adapter(Context context, Activity mActivity) {
            super(context);
            this.screenWidth = Util.getScreenWidth(mActivity);
        }

        public int getItemViewType(int position) {
            if (checkIsFull()) {
                return 0;
            }
            if ((getData() == null || getDataCount() < 1 || position != 0 || !((SendMessagesHelper.SendingMediaInfo) getData().get(0)).path.toLowerCase().endsWith("gif")) && getItemCount() != 0 && position == getItemCount() - 1) {
                return 1;
            }
            return 0;
        }

        public int getItemCount() {
            if (getDataCount() >= 9) {
                return getDataCount();
            }
            if (getData() == null || getDataCount() != 1 || !((SendMessagesHelper.SendingMediaInfo) getData().get(0)).path.toLowerCase().endsWith("gif")) {
                return getDataCount() + 1;
            }
            return getDataCount();
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(FcPublishActivity.this.mContext).inflate(R.layout.item_friendscircle_publishv1, parent, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
            lp.leftMargin = AndroidUtilities.dp(5.0f);
            lp.topMargin = AndroidUtilities.dp(5.0f);
            lp.rightMargin = AndroidUtilities.dp(5.0f);
            lp.bottomMargin = AndroidUtilities.dp(5.0f);
            view.setMinimumHeight(AndroidUtilities.dp(115.0f));
            view.setLayoutParams(lp);
            return new Holder(view);
        }

        /* access modifiers changed from: protected */
        public void onBindViewHolder(Holder holder, int position, SendMessagesHelper.SendingMediaInfo data, boolean isAddPictureItem) {
            CardView rl_take_photos = (CardView) holder.itemView.findViewById(R.id.rl_take_photos);
            rl_take_photos.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_windowBackgroundGrayText)));
            ImageView iv_photo = (ImageView) holder.itemView.findViewById(R.id.iv_photo);
            ViewGroup.LayoutParams lp = rl_take_photos.getLayoutParams();
            lp.width = ((this.screenWidth - AndroidUtilities.dp(30.0f)) - AndroidUtilities.dp(20.0f)) / 3;
            lp.height = lp.width;
            rl_take_photos.setLayoutParams(lp);
            if (isAddPictureItem) {
                iv_photo.setVisibility(8);
                return;
            }
            iv_photo.setVisibility(0);
            GlideUtils.getInstance().loadLocal(data.path, FcPublishActivity.this.mContext, iv_photo, R.drawable.shape_fc_default_pic_bg);
            iv_photo.setOnClickListener(new View.OnClickListener(position) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    FcPublishActivity.Adapter.this.lambda$onBindViewHolder$0$FcPublishActivity$Adapter(this.f$1, view);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$FcPublishActivity$Adapter(int position, View v) {
            if (FcPublishActivity.this.imageSelectorAlert == null) {
                FcPublishActivity.this.createChatAttachView();
            }
            FcPublishActivity.this.imageSelectorAlert.previewSelectedPhotos(position, FcPublishActivity.this.photoEntries);
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public boolean isNetworkConnected(Context context) {
        NetworkInfo mNetworkInfo;
        if (context == null || (mNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo()) == null) {
            return false;
        }
        return mNetworkInfo.isAvailable();
    }

    /* access modifiers changed from: private */
    public void readyUpload() {
        if (this.mbytAttachType == this.ATTACH_TYPE_VIDEO) {
            uploadSource(this.photoEntryVideo.path, this.mMapPhotos.get(this.photoEntryVideo.path));
            return;
        }
        KLog.d("-----------上" + this.mMapPhotos.size());
        this.photoList = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.mMapPhotos.entrySet()) {
            String str = TAG;
            KLog.e(str, "添加list == " + entry.getKey());
            this.photoList.add(entry);
        }
        if (this.photoList.size() > 0) {
            Map.Entry<String, String> entry2 = this.photoList.get(0);
            uploadSource(entry2.getKey(), entry2.getValue());
        }
    }

    /* access modifiers changed from: private */
    public void uploadSource(final String sourcePath, final String thumbPath) {
        String str = TAG;
        KLog.e(str, "上传 == " + sourcePath);
        uploadFile(sourcePath, new DataListener<BResponse<FcMediaResponseBean>>() {
            public void onResponse(BResponse<FcMediaResponseBean> result) {
                if (result == null || !result.isState()) {
                    FcToastUtils.show((CharSequence) LocaleController.getString("error_server_data", R.string.error_server_data));
                    if (FcPublishActivity.this.progressDialog != null) {
                        FcPublishActivity.this.progressDialog.dismiss();
                    }
                } else if (TextUtils.equals(sourcePath, thumbPath)) {
                    FcMediaBean mMediasBean = new FcMediaBean();
                    String servicePath = ((FcMediaResponseBean) result.Data).getName();
                    mMediasBean.setSeq(0);
                    mMediasBean.setExt(3);
                    mMediasBean.setName(servicePath);
                    mMediasBean.setThum(servicePath);
                    FcPublishActivity.this.mMediasList.add(mMediasBean);
                    FcPublishActivity.this.sendDataToServer();
                } else {
                    FcMediaBean mMediasBean2 = new FcMediaBean();
                    String servicePath2 = ((FcMediaResponseBean) result.Data).getName();
                    mMediasBean2.setName(servicePath2);
                    if (FcPublishActivity.this.mMapPhotos.size() != 1 || !sourcePath.endsWith(".mp4")) {
                        mMediasBean2.setExt(1);
                        mMediasBean2.setName(servicePath2);
                    } else {
                        mMediasBean2.setSeq(0);
                        mMediasBean2.setExt(2);
                        mMediasBean2.setWidth(FcPublishActivity.this.miVideoWidth);
                        mMediasBean2.setHeight(FcPublishActivity.this.miVideoHeight);
                    }
                    FcPublishActivity.this.uploadThumb(thumbPath, mMediasBean2);
                }
            }

            public void onError(Throwable throwable) {
                FcToastUtils.show((CharSequence) RxHelper.getInstance().getErrorInfo(throwable));
                if (FcPublishActivity.this.progressDialog != null) {
                    FcPublishActivity.this.progressDialog.dismiss();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void uploadThumb(String thumb, final FcMediaBean mMediasBean) {
        uploadFile(thumb, new DataListener<BResponse<FcMediaResponseBean>>() {
            public void onResponse(BResponse<FcMediaResponseBean> result) {
                if (result == null || !result.isState()) {
                    FcToastUtils.show((CharSequence) LocaleController.getString("error_server_data", R.string.error_server_data));
                    if (FcPublishActivity.this.progressDialog != null) {
                        FcPublishActivity.this.progressDialog.dismiss();
                        return;
                    }
                    return;
                }
                FcPublishActivity.access$1308(FcPublishActivity.this);
                mMediasBean.setThum(((FcMediaResponseBean) result.Data).getName());
                FcPublishActivity.this.mMediasList.add(mMediasBean);
                if (FcPublishActivity.this.mMapPhotos.size() == FcPublishActivity.this.currentUploadIndex) {
                    FcPublishActivity.this.sendDataToServer();
                } else if (FcPublishActivity.this.photoList != null && FcPublishActivity.this.currentUploadIndex < FcPublishActivity.this.photoList.size() && FcPublishActivity.this.photoList.size() > 0) {
                    Map.Entry<String, String> entry = (Map.Entry) FcPublishActivity.this.photoList.get(FcPublishActivity.this.currentUploadIndex);
                    FcPublishActivity.this.uploadSource(entry.getKey(), entry.getValue());
                }
            }

            public void onError(Throwable throwable) {
                FcToastUtils.show((CharSequence) RxHelper.getInstance().getErrorInfo(throwable));
                if (FcPublishActivity.this.progressDialog != null) {
                    FcPublishActivity.this.progressDialog.dismiss();
                }
            }
        });
    }

    public void getUploadUrlFailed(String msg) {
        super.getUploadUrlFailed(msg);
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    public void onUploadFileError(String msg) {
        FcToastUtils.show((CharSequence) msg);
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    private void setTopicsInfo(TagFlowLayout viewTopics) {
        viewTopics.removeAllViews();
        viewTopics.setVisibility(0);
        AnonymousClass12 r0 = new TagAdapter<RespTopicBean.Item>(new ArrayList()) {
            public View getView(FlowLayout parent, int position, RespTopicBean.Item value) {
                MryTextView tv = (MryTextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_child_view_topics, (ViewGroup) null);
                MryRoundButtonDrawable bg = new MryRoundButtonDrawable();
                bg.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundGray)));
                bg.setIsRadiusAdjustBounds(true);
                bg.setStrokeWidth(0);
                tv.setBackground(bg);
                tv.setTextColor(Theme.key_windowBackgroundWhiteBlackText);
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(value.TopicName);
                stringBuilder.insert(0, "# ");
                stringBuilder.setSpan(new ForegroundColorSpan(FcPublishActivity.this.mContext.getResources().getColor(R.color.color_FF2ECEFD)), 0, 1, 18);
                tv.setText(stringBuilder);
                return tv;
            }
        };
        this.tagadapter = r0;
        viewTopics.setAdapter(r0);
    }
}
