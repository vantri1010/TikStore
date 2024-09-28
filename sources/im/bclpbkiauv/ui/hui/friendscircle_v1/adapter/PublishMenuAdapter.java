package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baidu.mapapi.search.core.PoiInfo;
import com.bjz.comm.net.bean.RespTopicBean;
import com.bjz.comm.net.bean.TopicBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hui.decoration.DefaultItemDecoration;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.PublishMenuAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.PublishMenuAdapter.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishMenuAdapter<M extends Menu> extends PageSelectionAdapter<M, PageHolder> {
    private MenuMentionUserAdapter mentionUserAdapter;
    private RecyclerListView.OnItemClickListener menuUserItemClickListener;
    private RecyclerListView.OnItemClickListener onItemClickListener;

    public PublishMenuAdapter(Context context, int... openMenuRows) {
        super(context);
        setShowLoadMoreViewEnable(false);
        setData(createMenuData(openMenuRows));
    }

    public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
        return new PageHolder(LayoutInflater.from(getContext()).inflate(R.layout.fc_item_publish_menu, parent, false));
    }

    public void onBindViewHolderForChild(PageHolder holder, int position, M item) {
        PageHolder pageHolder = holder;
        M m = item;
        pageHolder.itemView.setOnClickListener(new View.OnClickListener(m) {
            private final /* synthetic */ PublishMenuAdapter.Menu f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                PublishMenuAdapter.this.lambda$onBindViewHolderForChild$0$PublishMenuAdapter(this.f$1, view);
            }
        });
        pageHolder.setGone((int) R.id.bottomDivider, position != getDataCount() - 1);
        int i = -13709571;
        pageHolder.setTextColor((int) R.id.tvTitleLeft, item.hasValue() ? -13709571 : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        pageHolder.setTextColor((int) R.id.tvTitleRight, Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        pageHolder.setImageResId((int) R.id.ivLeftIcon, m.icon);
        if (!item.hasValue()) {
            i = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6);
        }
        pageHolder.setImageColorFilter((int) R.id.ivLeftIcon, i, PorterDuff.Mode.SRC_IN);
        RecyclerListView rv = (RecyclerListView) pageHolder.getView(R.id.mentionUserRv);
        pageHolder.setText((int) R.id.tvTitleLeft, (CharSequence) (m.type != 1 || !item.hasValue()) ? m.leftTitle : item.getRightValue());
        if (m.type == 0) {
            pageHolder.setGone((int) R.id.tvTitleRight, true);
            pageHolder.setGone((View) rv, false);
            if (this.mentionUserAdapter == null) {
                this.mentionUserAdapter = new MenuMentionUserAdapter(getContext());
            }
            rv.setLayoutManager(new LinearLayoutManager(getContext(), 0, true));
            if (rv.getItemDecorationCount() == 0) {
                rv.addItemDecoration(new DefaultItemDecoration().setDividerWidth(AndroidUtilities.dp(5.0f)));
            }
            rv.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(m) {
                private final /* synthetic */ PublishMenuAdapter.Menu f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(View view, int i) {
                    PublishMenuAdapter.this.lambda$onBindViewHolderForChild$1$PublishMenuAdapter(this.f$1, view, i);
                }
            });
            rv.setAdapter(this.mentionUserAdapter);
            this.mentionUserAdapter.setData(((MenuMentionUser) m).users);
            return;
        }
        pageHolder.setGone((View) rv, true);
        if (m.type == 1) {
            pageHolder.setGone((int) R.id.tvTitleRight, true);
        } else {
            pageHolder.setGone((int) R.id.tvTitleRight, false);
            pageHolder.getView(R.id.tvTitleRight).setLayoutParams(LayoutHelper.createLinear(-2, -2, 2.0f, 5, 0, 0, 0));
        }
        pageHolder.setText((int) R.id.tvTitleRight, (CharSequence) item.hasValue() ? item.getRightValue() : "");
    }

    public /* synthetic */ void lambda$onBindViewHolderForChild$0$PublishMenuAdapter(Menu item, View v) {
        RecyclerListView.OnItemClickListener onItemClickListener2 = this.onItemClickListener;
        if (onItemClickListener2 != null) {
            onItemClickListener2.onItemClick(v, item.type);
        }
    }

    public /* synthetic */ void lambda$onBindViewHolderForChild$1$PublishMenuAdapter(Menu item, View view, int position1) {
        RecyclerListView.OnItemClickListener onItemClickListener2 = this.menuUserItemClickListener;
        if (onItemClickListener2 != null) {
            onItemClickListener2.onItemClick(view, item.type);
        }
    }

    public MenuMentionUser getMenuMentionUser() {
        Menu menu = getMenu(0);
        if (menu != null) {
            return (MenuMentionUser) menu;
        }
        return null;
    }

    public MenuLocation getMenuLocation() {
        Menu menu = getMenu(1);
        if (menu != null) {
            return (MenuLocation) menu;
        }
        return null;
    }

    public MenuTopic getMenuTopic() {
        Menu menu = getMenu(2);
        if (menu != null) {
            return (MenuTopic) menu;
        }
        return null;
    }

    public MenuWhoCanWatch getMenuWhoCanWatch() {
        Menu menu = getMenu(3);
        if (menu != null) {
            return (MenuWhoCanWatch) menu;
        }
        return null;
    }

    public void updateMentionUserRow(List<TLRPC.User> users) {
        MenuMentionUser menu = getMenuMentionUser();
        if (menu != null) {
            menu.users = users;
            notifyDataSetChanged();
        }
    }

    public List<TLRPC.User> getMentionRowUsers() {
        MenuMentionUser menu = getMenuMentionUser();
        return menu != null ? menu.users : new ArrayList();
    }

    public void updateLocationRow(PoiInfo info) {
        MenuLocation menu = getMenuLocation();
        if (menu != null) {
            if (info == null || !"不显示位置".equals(info.name)) {
                menu.locationInfo = info;
            } else {
                menu.locationInfo = null;
            }
            notifyDataSetChanged();
        }
    }

    public PoiInfo getLocationRowLocationInfo() {
        MenuLocation menu = getMenuLocation();
        if (menu != null) {
            return menu.locationInfo;
        }
        return null;
    }

    public void updateTopicRow(HashMap<String, RespTopicBean.Item> topicMap) {
        MenuTopic menu = getMenuTopic();
        if (menu != null) {
            menu.topicMap = topicMap;
            notifyDataSetChanged();
        }
    }

    public List<RespTopicBean.Item> getTopicRowRespTopicsList() {
        MenuTopic menu = getMenuTopic();
        if (menu == null) {
            return new ArrayList();
        }
        return menu.getRespTopics();
    }

    public ArrayList<TopicBean> getTopicRowTopicsBeanList() {
        MenuTopic menu = getMenuTopic();
        if (menu == null) {
            return new ArrayList<>();
        }
        return menu.getTopicBeans();
    }

    public void updateWhoCanWatchRow(List<TLRPC.User> users, int privicyType) {
        MenuWhoCanWatch menu = getMenuWhoCanWatch();
        if (menu != null) {
            menu.users = users;
            menu.privicyType = privicyType;
            notifyDataSetChanged();
        }
    }

    public List<TLRPC.User> getWhoCanWatchRowUsers() {
        MenuWhoCanWatch menu = getMenuWhoCanWatch();
        return menu != null ? menu.users : new ArrayList();
    }

    public int getWhoCanWatchRowPrivicyType() {
        MenuWhoCanWatch menu = getMenuWhoCanWatch();
        if (menu != null) {
            return menu.privicyType;
        }
        return 0;
    }

    public M getMenu(int type) {
        return getItem(type);
    }

    public M getItem(int type) {
        for (M m : getData()) {
            if (m.type == type) {
                return m;
            }
        }
        return null;
    }

    public PublishMenuAdapter setOnItemClickListener(RecyclerListView.OnItemClickListener onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
        return this;
    }

    public PublishMenuAdapter setMenuUserItemClickListener(RecyclerListView.OnItemClickListener menuUserItemClickListener2) {
        this.menuUserItemClickListener = menuUserItemClickListener2;
        return this;
    }

    public void destroy() {
        super.destroy();
        MenuMentionUserAdapter menuMentionUserAdapter = this.mentionUserAdapter;
        if (menuMentionUserAdapter != null) {
            menuMentionUserAdapter.destroy();
            this.mentionUserAdapter = null;
        }
        this.menuUserItemClickListener = null;
    }

    public static <M extends Menu> List<M> createMenuData(int... openMenuRows) {
        List<M> list = new ArrayList<>(openMenuRows.length);
        for (int i = 0; i < openMenuRows.length; i++) {
            M menu = null;
            int i2 = openMenuRows[i];
            if (i2 == 0) {
                menu = new MenuMentionUser();
                menu.icon = R.drawable.fc_icon_location_default;
                menu.leftTitle = LocaleController.getString(R.string.WhoToRemindToLook);
            } else if (i2 == 1) {
                menu = new MenuLocation();
                menu.icon = R.drawable.fc_icon_location_default;
                menu.leftTitle = LocaleController.getString(R.string.friendscircle_publish_choose_location);
            } else if (i2 == 2) {
                menu = new MenuTopic();
                menu.icon = R.drawable.fc_icon_topics_default;
                menu.leftTitle = LocaleController.getString(R.string.friendscircle_publish_choose_topics);
            } else if (i2 == 3) {
                menu = new MenuWhoCanWatch();
                menu.icon = R.drawable.fc_icon_location_default;
                menu.leftTitle = LocaleController.getString(R.string.WhoCanWatchIt);
            }
            if (menu != null) {
                menu.type = openMenuRows[i];
                list.add(menu);
            }
        }
        return list;
    }

    public static class MenuMentionUserAdapter extends PageSelectionAdapter<TLRPC.User, PageHolder> {
        public MenuMentionUserAdapter(Context context) {
            super(context);
            setShowLoadMoreViewEnable(false);
        }

        /* access modifiers changed from: protected */
        public boolean isEnableForChild(PageHolder holder) {
            return false;
        }

        public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
            BackupImageView ivAvatar = new BackupImageView(getContext());
            ivAvatar.setRoundRadius(AndroidUtilities.dp(5.0f));
            ivAvatar.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f)));
            return new PageHolder((View) ivAvatar, 0);
        }

        public void onBindViewHolderForChild(PageHolder holder, int position, TLRPC.User user) {
            AvatarDrawable drawable = new AvatarDrawable();
            drawable.setInfo(user);
            ((BackupImageView) holder.itemView).setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) drawable, (Object) user);
        }
    }

    public static abstract class Menu {
        public int icon;
        public String leftTitle;
        public String rightTitle;
        protected String rightValue;
        public int type;

        public abstract String getRightValue();

        public boolean hasValue() {
            return !TextUtils.isEmpty(this.rightValue);
        }
    }

    public static class MenuMentionUser extends Menu {
        public List<TLRPC.User> users = new ArrayList();

        public boolean hasValue() {
            return this.users.size() > 0;
        }

        public String getRightValue() {
            return null;
        }
    }

    public static class MenuLocation extends Menu {
        public PoiInfo locationInfo;

        public boolean hasValue() {
            return this.locationInfo != null;
        }

        public String getRightValue() {
            if (hasValue()) {
                String name = this.locationInfo.getName();
                String city = this.locationInfo.getCity();
                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(city) && !TextUtils.equals(name, city)) {
                        name = city.replace("市", "") + "·" + name;
                    }
                    this.rightValue = name;
                }
            } else if (!hasValue()) {
                this.locationInfo = null;
                this.rightValue = null;
            }
            return this.rightValue;
        }

        public boolean hasAddress() {
            PoiInfo poiInfo = this.locationInfo;
            return poiInfo != null && TextUtils.isEmpty(poiInfo.getAddress());
        }
    }

    public static class MenuTopic extends Menu {
        private ArrayList<TopicBean> topicBeans = new ArrayList<>();
        public HashMap<String, RespTopicBean.Item> topicMap = new HashMap<>();
        private List<RespTopicBean.Item> topics = new ArrayList();

        public boolean hasValue() {
            return this.topicMap.size() > 0;
        }

        public String getRightValue() {
            if (hasValue()) {
                this.rightValue = LocaleController.formatString("hasselectedtopics", R.string.hasselectedtopics, Integer.valueOf(this.topicMap.size()));
            } else if (!hasValue()) {
                this.rightValue = null;
            }
            return this.rightValue;
        }

        public List<RespTopicBean.Item> getRespTopics() {
            if (hasValue()) {
                this.topics.clear();
                for (Map.Entry<String, RespTopicBean.Item> entry : this.topicMap.entrySet()) {
                    this.topics.add(entry.getValue());
                }
            } else if (!hasValue()) {
                this.rightValue = null;
                this.topics.clear();
                this.topicMap.clear();
                this.topicBeans.clear();
            }
            return this.topics;
        }

        public ArrayList<TopicBean> getTopicBeans() {
            if (hasValue()) {
                this.topicBeans.clear();
                for (Map.Entry<String, RespTopicBean.Item> entry : this.topicMap.entrySet()) {
                    this.topicBeans.add(new TopicBean(entry.getValue().TopicName, entry.getValue().TopicID));
                }
            } else if (!hasValue()) {
                this.rightValue = null;
                this.topics.clear();
                this.topicMap.clear();
                this.topicBeans.clear();
            }
            return this.topicBeans;
        }
    }

    public static class MenuWhoCanWatch extends MenuMentionUser {
        public int privicyType;

        public String getRightValue() {
            if (hasValue()) {
                StringBuilder builder = new StringBuilder();
                for (TLRPC.User user : this.users) {
                    builder.append(UserObject.getName(user));
                }
                this.rightValue = builder.toString();
            } else if (!hasValue()) {
                this.rightValue = null;
                this.users.clear();
            }
            return this.rightValue;
        }
    }
}
