package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import im.bclpbkiauv.messenger.R;

public class AddPictureRecyclerView<T, VH extends RecyclerView.ViewHolder> extends RecyclerView {
    private RecyItemTouchHelperCallBack<T, VH> callBack;
    /* access modifiers changed from: private */
    public DragListener<T, VH> dragListener;
    /* access modifiers changed from: private */
    public ItemTouchHelper itemTouchHelper;
    /* access modifiers changed from: private */
    public AddPictureTouchAdapter<T, VH> mAdapter;
    private int maxCount;
    private boolean moveEnable;
    private boolean nestedScrollEnable;
    private OnRecyclerItemTouchListener<VH> onRecyclerItemTouchListener;

    public AddPictureRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AddPictureRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddPictureRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void init(AttributeSet attrs) {
        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AddPictureRecyclerView);
        this.maxCount = mTypedArray.getInteger(0, 9);
        this.moveEnable = mTypedArray.getBoolean(1, true);
        this.nestedScrollEnable = mTypedArray.getBoolean(2, false);
        mTypedArray.recycle();
        setNestedScrollingEnabled(this.nestedScrollEnable);
        setLayoutManager(new GridLayoutManager(getContext(), 3, 1, false));
        if (this.moveEnable) {
            setClipChildren(false);
        }
        initTouchHelper();
    }

    private void initTouchHelper() {
        if (this.moveEnable) {
            initRecyclerItemTouch();
            addOnItemTouchListener(this.onRecyclerItemTouchListener);
        }
    }

    private void initRecyclerItemTouch() {
        if (this.onRecyclerItemTouchListener == null) {
            this.onRecyclerItemTouchListener = new OnRecyclerItemTouchListener<VH>(this) {
                public void onItemClick(VH vh) {
                }

                public void onItemLongClick(VH vh) {
                    if (vh != null && AddPictureRecyclerView.this.dragListener != null && AddPictureRecyclerView.this.mAdapter != null && AddPictureRecyclerView.this.dragListener.canDrag(vh, vh.getAdapterPosition(), AddPictureRecyclerView.this.mAdapter.getItem(vh.getAdapterPosition())) && AddPictureRecyclerView.this.itemTouchHelper != null) {
                        AddPictureRecyclerView.this.itemTouchHelper.startDrag(vh);
                    }
                }
            };
        }
        addOnItemTouchListener(this.onRecyclerItemTouchListener);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof AddPictureTouchAdapter) {
            this.mAdapter = (AddPictureTouchAdapter) adapter;
            setMaxCount(this.maxCount);
            super.setAdapter(adapter);
            setTouchHelperCallBack();
            return;
        }
        throw new IllegalArgumentException("Must set AddPictureTouchAdapter");
    }

    private void setTouchHelperCallBack() {
        if (this.moveEnable) {
            this.callBack = new RecyItemTouchHelperCallBack<>(this.mAdapter);
            setDragListener();
            ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(this.callBack);
            this.itemTouchHelper = itemTouchHelper2;
            itemTouchHelper2.attachToRecyclerView(this);
        }
    }

    private void setDragListener() {
        RecyItemTouchHelperCallBack<T, VH> recyItemTouchHelperCallBack = this.callBack;
        if (recyItemTouchHelperCallBack != null) {
            recyItemTouchHelperCallBack.setDragListener(this.dragListener);
        }
    }

    public void setDragListener(DragListener<T, VH> dragListener2) {
        this.dragListener = dragListener2;
        setDragListener();
    }

    public void setItemTouchHelperCallBack(RecyItemTouchHelperCallBack<T, VH> callBack2) {
        this.callBack = callBack2;
    }

    public RecyItemTouchHelperCallBack<T, VH> getItemTouchHelperCallBack() {
        return this.callBack;
    }

    public void setMaxCount(int maxCount2) {
        this.maxCount = maxCount2;
        AddPictureTouchAdapter<T, VH> addPictureTouchAdapter = this.mAdapter;
        if (addPictureTouchAdapter != null) {
            addPictureTouchAdapter.setMaxCount(maxCount2);
        }
    }

    public int getMaxCount() {
        return this.maxCount;
    }
}
