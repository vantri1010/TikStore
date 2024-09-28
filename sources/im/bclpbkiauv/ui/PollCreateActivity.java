package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PollCreateActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.PollEditTextCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;

public class PollCreateActivity extends BaseFragment {
    private static final int MAX_ANSWER_LENGTH = 100;
    private static final int MAX_QUESTION_LENGTH = 255;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public int addAnswerRow;
    /* access modifiers changed from: private */
    public int answerHeaderRow;
    /* access modifiers changed from: private */
    public int answerSectionRow;
    /* access modifiers changed from: private */
    public int answerStartRow;
    /* access modifiers changed from: private */
    public String[] answers = new String[10];
    /* access modifiers changed from: private */
    public int answersCount = 1;
    /* access modifiers changed from: private */
    public PollCreateActivityDelegate delegate;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public int questionHeaderRow;
    /* access modifiers changed from: private */
    public int questionRow;
    /* access modifiers changed from: private */
    public int questionSectionRow;
    /* access modifiers changed from: private */
    public String questionString;
    /* access modifiers changed from: private */
    public int requestFieldFocusAtPosition = -1;
    /* access modifiers changed from: private */
    public int rowCount;

    public interface PollCreateActivityDelegate {
        void sendPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, boolean z, int i);
    }

    static /* synthetic */ int access$1410(PollCreateActivity x0) {
        int i = x0.answersCount;
        x0.answersCount = i - 1;
        return i;
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 5) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            PollCreateActivity.this.listAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                PollCreateActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public PollCreateActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("NewPoll", R.string.NewPoll));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (PollCreateActivity.this.checkDiscard()) {
                        PollCreateActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    TLRPC.TL_messageMediaPoll poll = new TLRPC.TL_messageMediaPoll();
                    poll.poll = new TLRPC.TL_poll();
                    TLRPC.TL_poll tL_poll = poll.poll;
                    PollCreateActivity pollCreateActivity = PollCreateActivity.this;
                    tL_poll.question = pollCreateActivity.getFixedString(pollCreateActivity.questionString);
                    for (int a = 0; a < PollCreateActivity.this.answers.length; a++) {
                        PollCreateActivity pollCreateActivity2 = PollCreateActivity.this;
                        if (!TextUtils.isEmpty(pollCreateActivity2.getFixedString(pollCreateActivity2.answers[a]))) {
                            TLRPC.TL_pollAnswer answer = new TLRPC.TL_pollAnswer();
                            PollCreateActivity pollCreateActivity3 = PollCreateActivity.this;
                            answer.text = pollCreateActivity3.getFixedString(pollCreateActivity3.answers[a]);
                            answer.option = new byte[1];
                            answer.option[0] = (byte) (poll.poll.answers.size() + 48);
                            poll.poll.answers.add(answer);
                        }
                    }
                    poll.results = new TLRPC.TL_pollResults();
                    if (PollCreateActivity.this.parentFragment.isInScheduleMode()) {
                        AlertsCreator.createScheduleDatePickerDialog(PollCreateActivity.this.getParentActivity(), UserObject.isUserSelf(PollCreateActivity.this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(poll) {
                            private final /* synthetic */ TLRPC.TL_messageMediaPoll f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void didSelectDate(boolean z, int i) {
                                PollCreateActivity.AnonymousClass1.this.lambda$onItemClick$0$PollCreateActivity$1(this.f$1, z, i);
                            }
                        });
                        return;
                    }
                    PollCreateActivity.this.delegate.sendPoll(poll, true, 0);
                    PollCreateActivity.this.finishFragment();
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$PollCreateActivity$1(TLRPC.TL_messageMediaPoll poll, boolean notify, int scheduleDate) {
                PollCreateActivity.this.delegate.sendPoll(poll, notify, scheduleDate);
                PollCreateActivity.this.finishFragment();
            }
        });
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
        ContextProgressView contextProgressView = new ContextProgressView(context, 1);
        this.progressView = contextProgressView;
        contextProgressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        AnonymousClass2 r4 = new RecyclerListView(context) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                rectangle.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthSpec, int heightSpec) {
                super.onMeasure(widthSpec, heightSpec);
            }

            public void requestLayout() {
                super.requestLayout();
            }
        };
        this.listView = r4;
        r4.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PollCreateActivity.this.lambda$createView$0$PollCreateActivity(view, i);
            }
        });
        checkDoneButton();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$PollCreateActivity(View view, int position) {
        if (position == this.addAnswerRow) {
            addNewField();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    PollCreateActivity.this.lambda$onTransitionAnimationEnd$1$PollCreateActivity();
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$1$PollCreateActivity() {
        RecyclerView.ViewHolder holder;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && (holder = recyclerListView.findViewHolderForAdapterPosition(this.questionRow)) != null) {
            EditTextBoldCursor editText = ((PollEditTextCell) holder.itemView).getTextView();
            editText.requestFocus();
            AndroidUtilities.showKeyboard(editText);
        }
    }

    /* access modifiers changed from: private */
    public String getFixedString(String text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        String text2 = AndroidUtilities.getTrimmedString(text).toString();
        while (text2.contains("\n\n\n")) {
            text2 = text2.replace("\n\n\n", "\n\n");
        }
        while (text2.startsWith("\n\n\n")) {
            text2 = text2.replace("\n\n\n", "\n\n");
        }
        return text2;
    }

    /* access modifiers changed from: private */
    public void checkDoneButton() {
        boolean enabled = true;
        if (TextUtils.isEmpty(getFixedString(this.questionString)) || this.questionString.length() > 255) {
            enabled = false;
        } else {
            int count = 0;
            int a = 0;
            while (true) {
                String[] strArr = this.answers;
                if (a >= strArr.length) {
                    break;
                }
                if (!TextUtils.isEmpty(getFixedString(strArr[a]))) {
                    if (this.answers[a].length() > 100) {
                        count = 0;
                        break;
                    }
                    count++;
                }
                a++;
            }
            if (count < 2) {
                enabled = false;
            }
        }
        this.doneItem.setEnabled(enabled);
        this.doneItem.setAlpha(enabled ? 1.0f : 0.5f);
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.questionHeaderRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.questionRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.questionSectionRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.answerHeaderRow = i3;
        int i5 = this.answersCount;
        if (i5 != 0) {
            this.answerStartRow = i4;
            this.rowCount = i4 + i5;
        } else {
            this.answerStartRow = -1;
        }
        if (this.answersCount != this.answers.length) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.addAnswerRow = i6;
        } else {
            this.addAnswerRow = -1;
        }
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.answerSectionRow = i7;
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        boolean allowDiscard = TextUtils.isEmpty(getFixedString(this.questionString));
        if (allowDiscard) {
            int a = 0;
            while (a < this.answersCount && (allowDiscard = TextUtils.isEmpty(getFixedString(this.answers[a])))) {
                a++;
            }
        }
        if (!allowDiscard) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("CancelPollAlertTitle", R.string.CancelPollAlertTitle));
            builder.setMessage(LocaleController.getString("CancelPollAlertText", R.string.CancelPollAlertText));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PollCreateActivity.this.lambda$checkDiscard$2$PollCreateActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
        return allowDiscard;
    }

    public /* synthetic */ void lambda$checkDiscard$2$PollCreateActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public void setDelegate(PollCreateActivityDelegate pollCreateActivityDelegate) {
        this.delegate = pollCreateActivityDelegate;
    }

    private void showEditDoneProgress(final boolean show) {
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.doneItemAnimation = new AnimatorSet();
        if (show) {
            this.progressView.setVisibility(0);
            this.doneItem.setEnabled(false);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f})});
        } else {
            this.doneItem.getContentView().setVisibility(0);
            this.doneItem.setEnabled(true);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (PollCreateActivity.this.doneItemAnimation != null && PollCreateActivity.this.doneItemAnimation.equals(animation)) {
                    if (!show) {
                        PollCreateActivity.this.progressView.setVisibility(4);
                    } else {
                        PollCreateActivity.this.doneItem.getContentView().setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (PollCreateActivity.this.doneItemAnimation != null && PollCreateActivity.this.doneItemAnimation.equals(animation)) {
                    AnimatorSet unused = PollCreateActivity.this.doneItemAnimation = null;
                }
            }
        });
        this.doneItemAnimation.setDuration(150);
        this.doneItemAnimation.start();
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View cell, int index) {
        boolean z = cell instanceof HeaderCell;
        String key = Theme.key_windowBackgroundWhiteRedText5;
        if (z) {
            HeaderCell headerCell = (HeaderCell) cell;
            if (index == -1) {
                String str = this.questionString;
                int left = 255 - (str != null ? str.length() : 0);
                if (((float) left) <= 76.5f) {
                    headerCell.setText2(String.format("%d", new Object[]{Integer.valueOf(left)}));
                    SimpleTextView textView = headerCell.getTextView2();
                    if (left >= 0) {
                        key = Theme.key_windowBackgroundWhiteGrayText3;
                    }
                    textView.setTextColor(Theme.getColor(key));
                    textView.setTag(key);
                    return;
                }
                headerCell.setText2("");
                return;
            }
            headerCell.setText2("");
        } else if ((cell instanceof PollEditTextCell) && index >= 0) {
            PollEditTextCell textCell = (PollEditTextCell) cell;
            String[] strArr = this.answers;
            int left2 = 100 - (strArr[index] != null ? strArr[index].length() : 0);
            if (((float) left2) <= 30.0f) {
                textCell.setText2(String.format("%d", new Object[]{Integer.valueOf(left2)}));
                SimpleTextView textView2 = textCell.getTextView2();
                if (left2 >= 0) {
                    key = Theme.key_windowBackgroundWhiteGrayText3;
                }
                textView2.setTextColor(Theme.getColor(key));
                textView2.setTag(key);
                return;
            }
            textCell.setText2("");
        }
    }

    /* access modifiers changed from: private */
    public void addNewField() {
        int i = this.answersCount + 1;
        this.answersCount = i;
        if (i == this.answers.length) {
            this.listAdapter.notifyItemRemoved(this.addAnswerRow);
        }
        this.listAdapter.notifyItemInserted(this.addAnswerRow);
        updateRows();
        this.requestFieldFocusAtPosition = (this.answerStartRow + this.answersCount) - 1;
        this.listAdapter.notifyItemChanged(this.answerSectionRow);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PollCreateActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell cell = (HeaderCell) holder.itemView;
                if (position == PollCreateActivity.this.questionHeaderRow) {
                    cell.setText(LocaleController.getString("Question", R.string.Question));
                } else if (position == PollCreateActivity.this.answerHeaderRow) {
                    cell.setText(LocaleController.getString("PollOptions", R.string.PollOptions));
                }
            } else if (itemViewType == 2) {
                TextInfoPrivacyCell cell2 = (TextInfoPrivacyCell) holder.itemView;
                cell2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                if (10 - PollCreateActivity.this.answersCount <= 0) {
                    cell2.setText(LocaleController.getString("AddAnOptionInfoMax", R.string.AddAnOptionInfoMax));
                } else {
                    cell2.setText(LocaleController.formatString("AddAnOptionInfo", R.string.AddAnOptionInfo, LocaleController.formatPluralString("Option", 10 - PollCreateActivity.this.answersCount)));
                }
            } else if (itemViewType == 3) {
                TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                textCell.setText(LocaleController.getString("AddAnOption", R.string.AddAnOption), false);
            } else if (itemViewType == 4) {
                PollEditTextCell textCell2 = (PollEditTextCell) holder.itemView;
                textCell2.setTag(1);
                textCell2.setTextAndHint(PollCreateActivity.this.questionString != null ? PollCreateActivity.this.questionString : "", LocaleController.getString("QuestionHint", R.string.QuestionHint), false);
                textCell2.setTag((Object) null);
            } else if (itemViewType == 5) {
                PollEditTextCell textCell3 = (PollEditTextCell) holder.itemView;
                textCell3.setTag(1);
                textCell3.setTextAndHint(PollCreateActivity.this.answers[position - PollCreateActivity.this.answerStartRow], LocaleController.getString("OptionHint", R.string.OptionHint), true);
                textCell3.setTag((Object) null);
                if (PollCreateActivity.this.requestFieldFocusAtPosition == position) {
                    EditTextBoldCursor editText = textCell3.getTextView();
                    editText.requestFocus();
                    AndroidUtilities.showKeyboard(editText);
                    int unused = PollCreateActivity.this.requestFieldFocusAtPosition = -1;
                }
                PollCreateActivity.this.setTextLeft(holder.itemView, position - PollCreateActivity.this.answerStartRow);
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            if (viewType == 0 || viewType == 5) {
                PollCreateActivity.this.setTextLeft(holder.itemView, holder.getAdapterPosition() == PollCreateActivity.this.questionHeaderRow ? -1 : 0);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getAdapterPosition() == PollCreateActivity.this.addAnswerRow;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: im.bclpbkiauv.ui.cells.PollEditTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r0v4, types: [im.bclpbkiauv.ui.cells.ShadowSectionCell] */
        /* JADX WARNING: type inference failed for: r0v5, types: [im.bclpbkiauv.ui.cells.TextInfoPrivacyCell] */
        /* JADX WARNING: type inference failed for: r1v7, types: [im.bclpbkiauv.ui.cells.TextSettingsCell, android.view.View] */
        /* JADX WARNING: type inference failed for: r2v7, types: [im.bclpbkiauv.ui.PollCreateActivity$ListAdapter$2, im.bclpbkiauv.ui.cells.PollEditTextCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                java.lang.String r0 = "windowBackgroundWhite"
                if (r10 == 0) goto L_0x0088
                r1 = 1
                if (r10 == r1) goto L_0x007f
                r2 = 2
                if (r10 == r2) goto L_0x0076
                r2 = 3
                if (r10 == r2) goto L_0x0067
                r2 = 4
                if (r10 == r2) goto L_0x004e
                im.bclpbkiauv.ui.PollCreateActivity$ListAdapter$2 r2 = new im.bclpbkiauv.ui.PollCreateActivity$ListAdapter$2
                android.content.Context r3 = r8.mContext
                im.bclpbkiauv.ui.-$$Lambda$PollCreateActivity$ListAdapter$zH1sDGVHWBUtzi8sKRAVhZh7w3s r4 = new im.bclpbkiauv.ui.-$$Lambda$PollCreateActivity$ListAdapter$zH1sDGVHWBUtzi8sKRAVhZh7w3s
                r4.<init>()
                r2.<init>(r3, r4)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r2.setBackgroundColor(r0)
                im.bclpbkiauv.ui.PollCreateActivity$ListAdapter$3 r0 = new im.bclpbkiauv.ui.PollCreateActivity$ListAdapter$3
                r0.<init>(r2)
                r2.addTextWatcher(r0)
                r2.setShowNextButton(r1)
                im.bclpbkiauv.ui.components.EditTextBoldCursor r0 = r2.getTextView()
                int r1 = r0.getImeOptions()
                r1 = r1 | 5
                r0.setImeOptions(r1)
                im.bclpbkiauv.ui.-$$Lambda$PollCreateActivity$ListAdapter$TC6EVmTaCpNaeAuQfCvWqpAe7-I r1 = new im.bclpbkiauv.ui.-$$Lambda$PollCreateActivity$ListAdapter$TC6EVmTaCpNaeAuQfCvWqpAe7-I
                r1.<init>(r2)
                r0.setOnEditorActionListener(r1)
                im.bclpbkiauv.ui.-$$Lambda$PollCreateActivity$ListAdapter$QszaLBgCCuXsDYoH4FeuZUavheA r1 = new im.bclpbkiauv.ui.-$$Lambda$PollCreateActivity$ListAdapter$QszaLBgCCuXsDYoH4FeuZUavheA
                r1.<init>()
                r0.setOnKeyListener(r1)
                r1 = r2
                goto L_0x009e
            L_0x004e:
                im.bclpbkiauv.ui.cells.PollEditTextCell r1 = new im.bclpbkiauv.ui.cells.PollEditTextCell
                android.content.Context r2 = r8.mContext
                r3 = 0
                r1.<init>(r2, r3)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                im.bclpbkiauv.ui.PollCreateActivity$ListAdapter$1 r0 = new im.bclpbkiauv.ui.PollCreateActivity$ListAdapter$1
                r0.<init>(r1)
                r1.addTextWatcher(r0)
                r0 = r1
                goto L_0x009e
            L_0x0067:
                im.bclpbkiauv.ui.cells.TextSettingsCell r1 = new im.bclpbkiauv.ui.cells.TextSettingsCell
                android.content.Context r2 = r8.mContext
                r1.<init>(r2)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x009e
            L_0x0076:
                im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
                android.content.Context r1 = r8.mContext
                r0.<init>(r1)
                r1 = r0
                goto L_0x009e
            L_0x007f:
                im.bclpbkiauv.ui.cells.ShadowSectionCell r0 = new im.bclpbkiauv.ui.cells.ShadowSectionCell
                android.content.Context r1 = r8.mContext
                r0.<init>(r1)
                r1 = r0
                goto L_0x009e
            L_0x0088:
                im.bclpbkiauv.ui.cells.HeaderCell r1 = new im.bclpbkiauv.ui.cells.HeaderCell
                android.content.Context r3 = r8.mContext
                r4 = 0
                r5 = 21
                r6 = 15
                r7 = 1
                r2 = r1
                r2.<init>(r3, r4, r5, r6, r7)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
            L_0x009e:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -1
                r3 = -2
                r0.<init>((int) r2, (int) r3)
                r1.setLayoutParams(r0)
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r0 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PollCreateActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PollCreateActivity$ListAdapter(View v) {
            if (v.getTag() == null) {
                v.setTag(1);
                RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder((View) v.getParent());
                if (holder != null) {
                    int position = holder.getAdapterPosition();
                    int index = position - PollCreateActivity.this.answerStartRow;
                    PollCreateActivity.this.listAdapter.notifyItemRemoved(holder.getAdapterPosition());
                    System.arraycopy(PollCreateActivity.this.answers, index + 1, PollCreateActivity.this.answers, index, (PollCreateActivity.this.answers.length - 1) - index);
                    PollCreateActivity.this.answers[PollCreateActivity.this.answers.length - 1] = null;
                    PollCreateActivity.access$1410(PollCreateActivity.this);
                    if (PollCreateActivity.this.answersCount == PollCreateActivity.this.answers.length - 1) {
                        PollCreateActivity.this.listAdapter.notifyItemInserted((PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answers.length) - 1);
                    }
                    RecyclerView.ViewHolder holder2 = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(position - 1);
                    if (holder2 != null && (holder2.itemView instanceof PollEditTextCell)) {
                        ((PollEditTextCell) holder2.itemView).getTextView().requestFocus();
                    }
                    PollCreateActivity.this.checkDoneButton();
                    PollCreateActivity.this.updateRows();
                    PollCreateActivity.this.listAdapter.notifyItemChanged(PollCreateActivity.this.answerSectionRow);
                }
            }
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$1$PollCreateActivity$ListAdapter(PollEditTextCell cell, TextView v, int actionId, KeyEvent event) {
            if (actionId != 5) {
                return false;
            }
            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(cell);
            if (holder != null) {
                int position = holder.getAdapterPosition();
                int index = position - PollCreateActivity.this.answerStartRow;
                if (index == PollCreateActivity.this.answersCount - 1 && PollCreateActivity.this.answersCount < 10) {
                    PollCreateActivity.this.addNewField();
                } else if (index == PollCreateActivity.this.answersCount - 1) {
                    AndroidUtilities.hideKeyboard(cell.getTextView());
                } else {
                    RecyclerView.ViewHolder holder2 = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(position + 1);
                    if (holder2 != null && (holder2.itemView instanceof PollEditTextCell)) {
                        ((PollEditTextCell) holder2.itemView).getTextView().requestFocus();
                    }
                }
            }
            return true;
        }

        static /* synthetic */ boolean lambda$onCreateViewHolder$2(PollEditTextCell cell, View v, int keyCode, KeyEvent event) {
            EditTextBoldCursor field = (EditTextBoldCursor) v;
            if (keyCode != 67 || event.getAction() != 0 || field.length() != 0) {
                return false;
            }
            cell.callOnDelete();
            return true;
        }

        public int getItemViewType(int position) {
            if (position == PollCreateActivity.this.questionHeaderRow || position == PollCreateActivity.this.answerHeaderRow) {
                return 0;
            }
            if (position == PollCreateActivity.this.questionSectionRow) {
                return 1;
            }
            if (position == PollCreateActivity.this.answerSectionRow) {
                return 2;
            }
            if (position == PollCreateActivity.this.addAnswerRow) {
                return 3;
            }
            if (position == PollCreateActivity.this.questionRow) {
                return 4;
            }
            return 5;
        }

        public void swapElements(int fromIndex, int toIndex) {
            int idx1 = fromIndex - PollCreateActivity.this.answerStartRow;
            int idx2 = toIndex - PollCreateActivity.this.answerStartRow;
            if (idx1 >= 0 && idx2 >= 0 && idx1 < PollCreateActivity.this.answersCount && idx2 < PollCreateActivity.this.answersCount) {
                String from = PollCreateActivity.this.answers[idx1];
                PollCreateActivity.this.answers[idx1] = PollCreateActivity.this.answers[idx2];
                PollCreateActivity.this.answers[idx2] = from;
                notifyItemMoved(fromIndex, toIndex);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, PollEditTextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText5), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteHintText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_stickers_menuSelector), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText5), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteHintText)};
    }
}
