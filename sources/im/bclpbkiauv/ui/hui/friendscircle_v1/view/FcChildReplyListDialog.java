package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.FcUserInfoBean;
import com.blankj.utilcode.util.ScreenUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.ShapeUtils;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDialogChildReplyAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils;
import java.util.ArrayList;

public class FcChildReplyListDialog extends BottomSheet {
    /* access modifiers changed from: private */
    public int currentUserId;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ChildReplyListListener listener;
    private SmartRefreshLayout mChildRefreshLayout;
    /* access modifiers changed from: private */
    public FcDialogChildReplyAdapter mChildReplyListAdapter;
    private RecyclerView rvReplyList;
    /* access modifiers changed from: private */
    public int scrollOffsetY;

    public interface ChildReplyListListener {
        void onChildReplyClick(View view, String str, FcReplyBean fcReplyBean, int i, int i2, boolean z);

        void onChildReplyListAction(View view, int i, int i2, Object obj);

        void onPresentFragment(BaseFragment baseFragment);

        void onReplyLoadMoreData(FcReplyBean fcReplyBean, int i);

        void onReplyRefreshData();
    }

    public FcChildReplyListDialog(Activity context) {
        this(context, false, 1);
        this.currentUserId = AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id;
    }

    public FcChildReplyListDialog(Context context, boolean needFocus, int backgroundType) {
        super(context, needFocus, backgroundType);
    }

    /* access modifiers changed from: protected */
    public void init(Context context, boolean needFocus, int backgroundType) {
        super.init(context, needFocus, backgroundType);
        setApplyBottomPadding(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_fc_reply_list, (ViewGroup) null);
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        initView(view);
    }

    private void initView(View view) {
        final int calHeight = ScreenUtils.getScreenHeight() - AndroidUtilities.dp(25.0f);
        this.containerView = new FrameLayout(getContext()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                if (height >= calHeight) {
                    height = calHeight;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, 1073741824));
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || FcChildReplyListDialog.this.scrollOffsetY == 0 || ev.getY() >= ((float) (FcChildReplyListDialog.this.scrollOffsetY - AndroidUtilities.dp(30.0f)))) {
                    return super.onInterceptTouchEvent(ev);
                }
                FcChildReplyListDialog.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !FcChildReplyListDialog.this.isDismissed() && super.onTouchEvent(e);
            }
        };
        this.containerView.setBackground(this.shadowDrawable);
        view.findViewById(R.id.fl_content).setBackground(ShapeUtils.createTop(Theme.getColor(Theme.key_windowBackgroundWhite), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f)));
        view.findViewById(R.id.view_divider).setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcChildReplyListDialog.this.dismiss();
            }
        });
        view.findViewById(R.id.btn_reply).setBackground(ShapeUtils.createStrokeAndFill(view.getResources().getColor(R.color.color_FFD8D8D8), (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(20.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        view.findViewById(R.id.btn_reply).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcReplyBean parentFcReplyBean;
                if (FcChildReplyListDialog.this.listener != null && FcChildReplyListDialog.this.mChildReplyListAdapter != null && (parentFcReplyBean = FcChildReplyListDialog.this.mChildReplyListAdapter.getParentFcReplyBean()) != null && ((long) FcChildReplyListDialog.this.currentUserId) != parentFcReplyBean.getCreateBy() && parentFcReplyBean.getCreator() != null) {
                    FcUserInfoBean fcUserInfoBean = parentFcReplyBean.getCreator();
                    FcChildReplyListDialog.this.listener.onChildReplyClick(v, StringUtils.handleTextName(ContactsController.formatName(fcUserInfoBean.getFirstName(), fcUserInfoBean.getLastName()), 12), FcChildReplyListDialog.this.mChildReplyListAdapter.getParentFcReplyBean(), FcChildReplyListDialog.this.mChildReplyListAdapter.getParentFcReplyPosition(), -1, false);
                }
            }
        });
        SmartRefreshLayout smartRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.smartRefreshLayout);
        this.mChildRefreshLayout = smartRefreshLayout;
        smartRefreshLayout.setEnableRefresh(false);
        this.mChildRefreshLayout.setEnableLoadMore(false);
        this.mChildRefreshLayout.setOnLoadMoreListener(new OnRefreshLoadMoreListener() {
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (FcChildReplyListDialog.this.listener != null) {
                    FcChildReplyListDialog.this.listener.onReplyLoadMoreData(FcChildReplyListDialog.this.mChildReplyListAdapter.getParentFcReplyBean(), FcChildReplyListDialog.this.mChildReplyListAdapter.getParentFcReplyPosition());
                }
            }

            public void onRefresh(RefreshLayout refreshLayout) {
            }
        });
        this.rvReplyList = (RecyclerView) view.findViewById(R.id.rv_reply_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.layoutManager = linearLayoutManager;
        this.rvReplyList.setLayoutManager(linearLayoutManager);
        FcDialogChildReplyAdapter fcDialogChildReplyAdapter = new FcDialogChildReplyAdapter(new ArrayList(), getContext(), 0, this.listener);
        this.mChildReplyListAdapter = fcDialogChildReplyAdapter;
        this.rvReplyList.setAdapter(fcDialogChildReplyAdapter);
        this.containerView.addView(view, LayoutHelper.createFrame(-1, -2.0f));
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public FcDialogChildReplyAdapter getChildReplyListAdapter() {
        return this.mChildReplyListAdapter;
    }

    public void setParentFcReplyData(FcReplyBean parentFcReplyBean, int parentFcReplyPosition) {
        FcDialogChildReplyAdapter fcDialogChildReplyAdapter = this.mChildReplyListAdapter;
        if (fcDialogChildReplyAdapter != null) {
            fcDialogChildReplyAdapter.setFcReplyBean(parentFcReplyBean, parentFcReplyPosition);
        }
    }

    public void loadData(ArrayList<FcReplyBean> list, int pageNo) {
        if (this.mChildReplyListAdapter == null) {
            return;
        }
        if (pageNo != 0) {
            if (list == null) {
                list = new ArrayList<>();
            }
            if (list.size() < 20) {
                list.add(new FcReplyBean());
                this.mChildRefreshLayout.finishLoadMore(0);
                this.mChildRefreshLayout.setEnableLoadMore(false);
            }
            this.mChildReplyListAdapter.loadMore(list);
        } else if (list == null || list.size() == 0) {
            this.mChildRefreshLayout.setEnableLoadMore(false);
        } else {
            if (list.size() < 20) {
                this.mChildRefreshLayout.setEnableLoadMore(false);
            } else {
                this.mChildRefreshLayout.setEnableLoadMore(true);
            }
            ArrayList<FcReplyBean> temp = new ArrayList<>();
            temp.add(new FcReplyBean());
            temp.addAll(list);
            if (list.size() < 20) {
                temp.add(new FcReplyBean());
            }
            this.mChildReplyListAdapter.refresh(temp);
        }
    }

    /* JADX WARNING: type inference failed for: r3v6, types: [android.view.View] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doLike(int r6, boolean r7, com.bjz.comm.net.bean.FcLikeBean r8) {
        /*
            r5 = this;
            androidx.recyclerview.widget.LinearLayoutManager r0 = r5.layoutManager
            android.view.View r0 = r0.findViewByPosition(r6)
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0019
            r3 = 2131296429(0x7f0900ad, float:1.8210774E38)
            android.view.View r3 = r0.findViewById(r3)
            r1 = r3
            im.bclpbkiauv.ui.hviews.MryTextView r1 = (im.bclpbkiauv.ui.hviews.MryTextView) r1
            if (r1 == 0) goto L_0x0019
            r1.setClickable(r2)
        L_0x0019:
            if (r8 == 0) goto L_0x007e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "------position"
            r3.append(r4)
            r3.append(r6)
            java.lang.String r4 = "  "
            r3.append(r4)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            com.socks.library.KLog.d(r3)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDialogChildReplyAdapter r3 = r5.mChildReplyListAdapter
            java.lang.Object r3 = r3.get(r6)
            com.bjz.comm.net.bean.FcReplyBean r3 = (com.bjz.comm.net.bean.FcReplyBean) r3
            r3.setHasThumb(r7)
            if (r7 == 0) goto L_0x0055
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDialogChildReplyAdapter r4 = r5.mChildReplyListAdapter
            java.lang.Object r4 = r4.get(r6)
            com.bjz.comm.net.bean.FcReplyBean r4 = (com.bjz.comm.net.bean.FcReplyBean) r4
            int r4 = r4.getThumbUp()
            int r4 = r4 + r2
            r3.setThumbUp(r4)
            goto L_0x0065
        L_0x0055:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDialogChildReplyAdapter r4 = r5.mChildReplyListAdapter
            java.lang.Object r4 = r4.get(r6)
            com.bjz.comm.net.bean.FcReplyBean r4 = (com.bjz.comm.net.bean.FcReplyBean) r4
            int r4 = r4.getThumbUp()
            int r4 = r4 - r2
            r3.setThumbUp(r4)
        L_0x0065:
            if (r1 == 0) goto L_0x007e
            int r2 = r3.getThumbUp()
            if (r2 <= 0) goto L_0x0076
            int r2 = r3.getThumbUp()
            java.lang.String r2 = java.lang.String.valueOf(r2)
            goto L_0x0078
        L_0x0076:
            java.lang.String r2 = "0"
        L_0x0078:
            r1.setText(r2)
            r1.setSelected(r7)
        L_0x007e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcChildReplyListDialog.doLike(int, boolean, com.bjz.comm.net.bean.FcLikeBean):void");
    }

    public void doReply(FcReplyBean data) {
        FcDialogChildReplyAdapter fcDialogChildReplyAdapter = this.mChildReplyListAdapter;
        if (fcDialogChildReplyAdapter == null) {
            return;
        }
        if (fcDialogChildReplyAdapter.getFooterSize() != 0) {
            this.mChildReplyListAdapter.getDataList().add(this.mChildReplyListAdapter.getItemCount() - 1, data);
            FcDialogChildReplyAdapter fcDialogChildReplyAdapter2 = this.mChildReplyListAdapter;
            fcDialogChildReplyAdapter2.notifyItemInserted(fcDialogChildReplyAdapter2.getItemCount() - 1);
            FcDialogChildReplyAdapter fcDialogChildReplyAdapter3 = this.mChildReplyListAdapter;
            fcDialogChildReplyAdapter3.notifyItemRangeChanged(fcDialogChildReplyAdapter3.getItemCount() - 1, this.mChildReplyListAdapter.getFooterSize());
            return;
        }
        ArrayList<FcReplyBean> moreList = new ArrayList<>();
        moreList.add(data);
        this.mChildReplyListAdapter.loadMore(moreList);
    }

    public void doDeleteReply(int position) {
        FcReplyBean fcReplyBean = this.mChildReplyListAdapter.getParentFcReplyBean();
        if (fcReplyBean != null) {
            fcReplyBean.setSubComments(fcReplyBean.getSubComments() - 1);
            this.mChildReplyListAdapter.setParentFcReplyBean(fcReplyBean);
        }
        if (position < this.mChildReplyListAdapter.getItemCount()) {
            this.mChildReplyListAdapter.getDataList().remove(position);
            this.mChildReplyListAdapter.notifyItemRemoved(position);
            FcDialogChildReplyAdapter fcDialogChildReplyAdapter = this.mChildReplyListAdapter;
            fcDialogChildReplyAdapter.notifyItemRangeChanged(position, fcDialogChildReplyAdapter.getItemCount() - position);
        }
    }

    public ArrayList<FcReplyBean> getRealDataList() {
        ArrayList<FcReplyBean> temp = null;
        FcDialogChildReplyAdapter fcDialogChildReplyAdapter = this.mChildReplyListAdapter;
        if (!(fcDialogChildReplyAdapter == null || fcDialogChildReplyAdapter.getDataList() == null)) {
            temp = new ArrayList<>(this.mChildReplyListAdapter.getDataList());
            if (this.mChildReplyListAdapter.getFooterSize() != 0) {
                temp.remove(temp.size() - 1);
            }
            temp.remove(0);
        }
        return temp;
    }

    public void setListener(ChildReplyListListener listener2) {
        FcDialogChildReplyAdapter fcDialogChildReplyAdapter;
        this.listener = listener2;
        if (listener2 != null && (fcDialogChildReplyAdapter = this.mChildReplyListAdapter) != null) {
            fcDialogChildReplyAdapter.setListener(listener2);
        }
    }
}
