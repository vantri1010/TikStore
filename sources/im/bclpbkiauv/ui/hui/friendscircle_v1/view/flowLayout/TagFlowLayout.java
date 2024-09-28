package im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagAdapter;
import java.util.HashSet;
import java.util.Set;

public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener {
    private static final String KEY_CHOOSE_POS = "key_choose_pos";
    private static final String KEY_DEFAULT = "key_default";
    private static final String TAG = "TagFlowLayout";
    private OnSelectListener mOnSelectListener;
    /* access modifiers changed from: private */
    public OnTagClickListener mOnTagClickListener;
    private int mSelectedMax;
    private Set<Integer> mSelectedView;
    private TagAdapter mTagAdapter;

    public interface OnSelectListener {
        void onSelected(Set<Integer> set);
    }

    public interface OnTagClickListener {
        boolean onTagClick(View view, int i, FlowLayout flowLayout);
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSelectedMax = -1;
        this.mSelectedView = new HashSet();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        this.mSelectedMax = ta.getInt(0, -1);
        ta.recycle();
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            TagView tagView = (TagView) getChildAt(i);
            if (tagView.getVisibility() != 8 && tagView.getTagView().getVisibility() == 8) {
                tagView.setVisibility(8);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.mOnSelectListener = onSelectListener;
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.mOnTagClickListener = onTagClickListener;
    }

    public void setAdapter(TagAdapter adapter) {
        this.mTagAdapter = adapter;
        adapter.setOnDataChangedListener(this);
        this.mSelectedView.clear();
        changeAdapter();
    }

    private void changeAdapter() {
        removeAllViews();
        TagAdapter adapter = this.mTagAdapter;
        HashSet preCheckedList = this.mTagAdapter.getPreCheckedList();
        for (int i = 0; i < adapter.getCount(); i++) {
            View tagView = adapter.getView(this, i, adapter.getItem(i));
            TagView tagViewContainer = new TagView(getContext());
            tagView.setDuplicateParentStateEnabled(true);
            if (tagView.getLayoutParams() != null) {
                tagViewContainer.setLayoutParams(tagView.getLayoutParams());
            } else {
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(-2, -2);
                lp.setMargins(dip2px(getContext(), 5.0f), dip2px(getContext(), 5.0f), dip2px(getContext(), 5.0f), dip2px(getContext(), 5.0f));
                tagViewContainer.setLayoutParams(lp);
            }
            tagView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            tagViewContainer.addView(tagView);
            addView(tagViewContainer);
            if (preCheckedList.contains(Integer.valueOf(i))) {
                setChildChecked(i, tagViewContainer);
            }
            if (this.mTagAdapter.setSelected(i, adapter.getItem(i))) {
                setChildChecked(i, tagViewContainer);
            }
            tagView.setClickable(false);
            final TagView finalTagViewContainer = tagViewContainer;
            final int position = i;
            tagViewContainer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    TagFlowLayout.this.doSelect(finalTagViewContainer, position);
                    if (TagFlowLayout.this.mOnTagClickListener != null) {
                        TagFlowLayout.this.mOnTagClickListener.onTagClick(finalTagViewContainer, position, TagFlowLayout.this);
                    }
                }
            });
        }
        this.mSelectedView.addAll(preCheckedList);
    }

    public void setMaxSelectCount(int count) {
        if (this.mSelectedView.size() > count) {
            Log.w(TAG, "you has already select more than " + count + " views , so it will be clear .");
            this.mSelectedView.clear();
        }
        this.mSelectedMax = count;
    }

    public Set<Integer> getSelectedList() {
        return new HashSet(this.mSelectedView);
    }

    private void setChildChecked(int position, TagView view) {
        view.setChecked(true);
        this.mTagAdapter.onSelected(position, view.getTagView());
    }

    private void setChildUnChecked(int position, TagView view) {
        view.setChecked(false);
        this.mTagAdapter.unSelected(position, view.getTagView());
    }

    /* access modifiers changed from: private */
    public void doSelect(TagView child, int position) {
        if (child.isChecked()) {
            setChildUnChecked(position, child);
            this.mSelectedView.remove(Integer.valueOf(position));
        } else if (this.mSelectedMax == 1 && this.mSelectedView.size() == 1) {
            Integer preIndex = this.mSelectedView.iterator().next();
            setChildUnChecked(preIndex.intValue(), (TagView) getChildAt(preIndex.intValue()));
            setChildChecked(position, child);
            this.mSelectedView.remove(preIndex);
            this.mSelectedView.add(Integer.valueOf(position));
        } else if (this.mSelectedMax <= 0 || this.mSelectedView.size() < this.mSelectedMax) {
            setChildChecked(position, child);
            this.mSelectedView.add(Integer.valueOf(position));
        } else {
            return;
        }
        OnSelectListener onSelectListener = this.mOnSelectListener;
        if (onSelectListener != null) {
            onSelectListener.onSelected(new HashSet(this.mSelectedView));
        }
    }

    public TagAdapter getAdapter() {
        return this.mTagAdapter;
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());
        String selectPos = "";
        if (this.mSelectedView.size() > 0) {
            for (Integer intValue : this.mSelectedView) {
                selectPos = selectPos + intValue.intValue() + LogUtils.VERTICAL;
            }
            selectPos = selectPos.substring(0, selectPos.length() - 1);
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos);
        return bundle;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            String mSelectPos = bundle.getString(KEY_CHOOSE_POS);
            if (!TextUtils.isEmpty(mSelectPos)) {
                for (String pos : mSelectPos.split("\\|")) {
                    int index = Integer.parseInt(pos);
                    this.mSelectedView.add(Integer.valueOf(index));
                    TagView tagView = (TagView) getChildAt(index);
                    if (tagView != null) {
                        setChildChecked(index, tagView);
                    }
                }
            }
            super.onRestoreInstanceState(bundle.getParcelable(KEY_DEFAULT));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public void onChanged() {
        this.mSelectedView.clear();
        changeAdapter();
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
