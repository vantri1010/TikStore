package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.RespTopicBean;
import com.bjz.comm.net.bean.RespTopicTypeBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.utils.RxHelper;
import com.blankj.utilcode.util.SpanUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.utils.DrawableUtils;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.fragments.BaseFmts;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryTextView;
import io.reactivex.functions.Consumer;
import java.util.HashMap;
import java.util.List;

public class AddTopicFragment extends BaseFmts {
    private static final String TAG = "AddTopicFragment";
    private final String TOPICTYPEID = "TOPICTYPEID";
    private String TopicName = "";
    private long TopicTypeID;
    /* access modifiers changed from: private */
    public HashMap<String, RespTopicBean.Item> cacheSelectedHashmap;
    private FrameLayout frame_container;
    private GridLayoutManager gridLayoutManager;
    private RecyclerListView listView;
    private MyAdapter myAdapter;
    private List<RespTopicBean.Item> respTopicBeans;

    public AddTopicFragment(RespTopicTypeBean TopicTypes, HashMap<String, RespTopicBean.Item> cacheSelectedHashmap2) {
        this.cacheSelectedHashmap = cacheSelectedHashmap2;
        Bundle args = new Bundle();
        args.putLong("TOPICTYPEID", TopicTypes.TopicTypeID);
        setArguments(args);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.TopicTypeID = getArguments().getLong("TOPICTYPEID");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.fragmentView = inflater.inflate(R.layout.fragment_fc_add_topic, (ViewGroup) null);
        this.frame_container = (FrameLayout) this.fragmentView.findViewById(R.id.frame_container);
        RecyclerListView recyclerListView = (RecyclerListView) this.fragmentView.findViewById(R.id.listview);
        this.listView = recyclerListView;
        recyclerListView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView2 = this.listView;
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this.context, 2);
        this.gridLayoutManager = gridLayoutManager2;
        recyclerListView2.setLayoutManager(gridLayoutManager2);
        RecyclerListView recyclerListView3 = this.listView;
        MyAdapter myAdapter2 = new MyAdapter(this.context);
        this.myAdapter = myAdapter2;
        recyclerListView3.setAdapter(myAdapter2);
        this.myAdapter.emptyAttachView(this.frame_container);
        this.myAdapter.showLoading();
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                AddTopicFragment.this.lambda$onCreateView$0$AddTopicFragment(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$onCreateView$0$AddTopicFragment(View view, int position) {
        RespTopicBean.Item selectedtopic = this.respTopicBeans.get(position);
        String key = selectedtopic.ID + "" + selectedtopic.TypeID;
        if (this.cacheSelectedHashmap.get(key) != null) {
            this.cacheSelectedHashmap.remove(key);
            getAccountInstance().getNotificationCenter();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.selectedTopicSuccess, new Object[0]);
        } else if (this.cacheSelectedHashmap.size() >= 3) {
            FcToastUtils.show((CharSequence) LocaleController.getString("selcetthreetopic", R.string.selcetthreetopic));
        } else {
            this.cacheSelectedHashmap.put(key, selectedtopic);
            getAccountInstance().getNotificationCenter();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.selectedTopicSuccess, new Object[0]);
        }
    }

    /* access modifiers changed from: protected */
    public void lazyLoadData() {
        super.lazyLoadData();
        getTopics(0);
    }

    /* access modifiers changed from: protected */
    public void onVisible() {
        super.onVisible();
    }

    private class MyAdapter extends PageSelectionAdapter<RespTopicBean.Item, PageHolder> {
        public MyAdapter(Context context) {
            super(context);
        }

        public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
            return new PageHolder(LayoutInflater.from(AddTopicFragment.this.context).inflate(R.layout.item_fc_topic, parent, false));
        }

        public void onBindViewHolderForChild(PageHolder holder, int position, RespTopicBean.Item item) {
            PageHolder pageHolder = holder;
            int i = position;
            RespTopicBean.Item item2 = item;
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(70.0f));
            if (i % 2 == 0) {
                layoutParams.topMargin = AndroidUtilities.dp(15.0f);
                layoutParams.leftMargin = AndroidUtilities.dp(15.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
            } else {
                layoutParams.rightMargin = AndroidUtilities.dp(15.0f);
                layoutParams.topMargin = AndroidUtilities.dp(15.0f);
            }
            if (getItemCount() % 2 == 0) {
                if (i == getItemCount() - 1 || i == getItemCount() - 2) {
                    layoutParams.bottomMargin = AndroidUtilities.dp(15.0f);
                }
            } else if (i == getItemCount() - 1) {
                layoutParams.bottomMargin = AndroidUtilities.dp(15.0f);
            }
            pageHolder.itemView.setLayoutParams(layoutParams);
            HashMap access$100 = AddTopicFragment.this.cacheSelectedHashmap;
            if (access$100.get(item2.ID + "" + item2.TypeID) == null) {
                pageHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (Theme.getCurrentTheme().isLight()) {
                pageHolder.itemView.setBackground(DrawableUtils.createLayerDrawable(AddTopicFragment.this.context.getResources().getColor(R.color.color_F0FCFF), AddTopicFragment.this.context.getResources().getColor(R.color.color_2ECEFD), 0.0f));
            } else {
                pageHolder.itemView.setBackground(DrawableUtils.createLayerDrawable(AndroidUtilities.alphaColor(0.1f, -983809), AddTopicFragment.this.context.getResources().getColor(R.color.color_2ECEFD), 0.0f));
            }
            MryTextView tv_subtitle = (MryTextView) pageHolder.itemView.findViewById(R.id.tv_subtitle);
            MryTextView tv_tag = (MryTextView) pageHolder.itemView.findViewById(R.id.tv_tag);
            tv_subtitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            tv_tag.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            SpanUtils.with((MryTextView) pageHolder.itemView.findViewById(R.id.tv_title)).append("#").setForegroundColor(-13709571).append(item2.TopicName).setForegroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText)).create();
            tv_subtitle.setText(item2.Subtitle);
            if (item2.Tag == 1) {
                tv_tag.setText(LocaleController.getString("fc_new", R.string.fc_new));
                tv_tag.setBackground(DrawableUtils.getGradientDrawable(new float[]{(float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f}, AddTopicFragment.this.context.getResources().getColor(R.color.color_FFFD8A94), AddTopicFragment.this.context.getResources().getColor(R.color.color_FFFD6FCB)));
            } else if (item2.Tag == 2) {
                tv_tag.setText(LocaleController.getString("fc_recommend", R.string.fc_recommend));
                tv_tag.setBackground(DrawableUtils.getGradientDrawable(new float[]{(float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f}, AddTopicFragment.this.context.getResources().getColor(R.color.color_FF50F7FD), AddTopicFragment.this.context.getResources().getColor(R.color.color_FF2ED2FE)));
            } else {
                tv_tag.setVisibility(8);
            }
        }
    }

    private void getTopics(int pageNo) {
        RxHelper.getInstance().sendRequest(TAG, ApiFactory.getInstance().getApiMomentForum().getFcTopic(this.TopicTypeID, this.TopicName, pageNo * 20, 20), new Consumer() {
            public final void accept(Object obj) {
                AddTopicFragment.this.lambda$getTopics$1$AddTopicFragment((BResponse) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                AddTopicFragment.this.lambda$getTopics$2$AddTopicFragment((Throwable) obj);
            }
        });
    }

    public /* synthetic */ void lambda$getTopics$1$AddTopicFragment(BResponse response) throws Exception {
        if (response != null && response.isState()) {
            if (response.Data != null) {
                List<RespTopicBean.Item> topics = ((RespTopicBean) response.Data).getTopics();
                this.respTopicBeans = topics;
                this.myAdapter.addData(topics);
                return;
            }
            this.myAdapter.showEmpty();
        }
    }

    public /* synthetic */ void lambda$getTopics$2$AddTopicFragment(Throwable throwable) throws Exception {
        this.myAdapter.showError(LocaleController.getString("request_fialed", R.string.fc_request_fialed));
    }

    public void setselectedItemCount(HashMap<String, RespTopicBean.Item> cacheSelectedHashmap2) {
        this.cacheSelectedHashmap = cacheSelectedHashmap2;
        MyAdapter myAdapter2 = this.myAdapter;
        if (myAdapter2 != null) {
            myAdapter2.notifyDataSetChanged();
        }
    }
}
