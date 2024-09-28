package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import im.bclpbkiauv.ui.cells.DialogCell;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DialogsItemAnimator extends SimpleItemAnimator {
    private static final boolean DEBUG = false;
    private static final int changeDuration = 180;
    private static final int deleteDuration = 180;
    private static TimeInterpolator sDefaultInterpolator = new DecelerateInterpolator();
    private int bottomClip;
    ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
    ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();
    ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
    ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
    private DialogCell removingDialog;
    private int topClip;

    private static class MoveInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder holder;
        public int toX;
        public int toY;

        MoveInfo(RecyclerView.ViewHolder holder2, int fromX2, int fromY2, int toX2, int toY2) {
            this.holder = holder2;
            this.fromX = fromX2;
            this.fromY = fromY2;
            this.toX = toX2;
            this.toY = toY2;
        }
    }

    private static class ChangeInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder newHolder;
        public RecyclerView.ViewHolder oldHolder;
        public int toX;
        public int toY;

        private ChangeInfo(RecyclerView.ViewHolder oldHolder2, RecyclerView.ViewHolder newHolder2) {
            this.oldHolder = oldHolder2;
            this.newHolder = newHolder2;
        }

        ChangeInfo(RecyclerView.ViewHolder oldHolder2, RecyclerView.ViewHolder newHolder2, int fromX2, int fromY2, int toX2, int toY2) {
            this(oldHolder2, newHolder2);
            this.fromX = fromX2;
            this.fromY = fromY2;
            this.toX = toX2;
            this.toY = toY2;
        }

        public String toString() {
            return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    public void runPendingAnimations() {
        boolean removalsPending = !this.mPendingRemovals.isEmpty();
        boolean movesPending = !this.mPendingMoves.isEmpty();
        boolean changesPending = !this.mPendingChanges.isEmpty();
        boolean additionsPending = !this.mPendingAdditions.isEmpty();
        if (removalsPending || movesPending || additionsPending || changesPending) {
            Iterator<RecyclerView.ViewHolder> it = this.mPendingRemovals.iterator();
            while (it.hasNext()) {
                animateRemoveImpl(it.next());
            }
            this.mPendingRemovals.clear();
            if (movesPending) {
                ArrayList<MoveInfo> moves = new ArrayList<>(this.mPendingMoves);
                this.mMovesList.add(moves);
                this.mPendingMoves.clear();
                new Runnable(moves) {
                    private final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        DialogsItemAnimator.this.lambda$runPendingAnimations$0$DialogsItemAnimator(this.f$1);
                    }
                }.run();
            }
            if (changesPending) {
                ArrayList<ChangeInfo> changes = new ArrayList<>(this.mPendingChanges);
                this.mChangesList.add(changes);
                this.mPendingChanges.clear();
                new Runnable(changes) {
                    private final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        DialogsItemAnimator.this.lambda$runPendingAnimations$1$DialogsItemAnimator(this.f$1);
                    }
                }.run();
            }
            if (additionsPending) {
                ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>(this.mPendingAdditions);
                this.mAdditionsList.add(additions);
                this.mPendingAdditions.clear();
                new Runnable(additions) {
                    private final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        DialogsItemAnimator.this.lambda$runPendingAnimations$2$DialogsItemAnimator(this.f$1);
                    }
                }.run();
            }
        }
    }

    public /* synthetic */ void lambda$runPendingAnimations$0$DialogsItemAnimator(ArrayList moves) {
        Iterator it = moves.iterator();
        while (it.hasNext()) {
            MoveInfo moveInfo = (MoveInfo) it.next();
            animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
        }
        moves.clear();
        this.mMovesList.remove(moves);
    }

    public /* synthetic */ void lambda$runPendingAnimations$1$DialogsItemAnimator(ArrayList changes) {
        Iterator it = changes.iterator();
        while (it.hasNext()) {
            animateChangeImpl((ChangeInfo) it.next());
        }
        changes.clear();
        this.mChangesList.remove(changes);
    }

    public /* synthetic */ void lambda$runPendingAnimations$2$DialogsItemAnimator(ArrayList additions) {
        Iterator it = additions.iterator();
        while (it.hasNext()) {
            animateAddImpl((RecyclerView.ViewHolder) it.next());
        }
        additions.clear();
        this.mAdditionsList.remove(additions);
    }

    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        this.mPendingRemovals.add(holder);
        return true;
    }

    public void prepareForRemove() {
        this.topClip = Integer.MAX_VALUE;
        this.bottomClip = Integer.MAX_VALUE;
        this.removingDialog = null;
    }

    private void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        this.mRemoveAnimations.add(holder);
        if (view instanceof DialogCell) {
            final DialogCell dialogCell = (DialogCell) view;
            this.removingDialog = dialogCell;
            if (this.topClip != Integer.MAX_VALUE) {
                int measuredHeight = dialogCell.getMeasuredHeight();
                int i = this.topClip;
                this.bottomClip = measuredHeight - i;
                this.removingDialog.setTopClip(i);
                this.removingDialog.setBottomClip(this.bottomClip);
            } else if (this.bottomClip != Integer.MAX_VALUE) {
                int measuredHeight2 = dialogCell.getMeasuredHeight() - this.bottomClip;
                this.topClip = measuredHeight2;
                this.removingDialog.setTopClip(measuredHeight2);
                this.removingDialog.setBottomClip(this.bottomClip);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                dialogCell.setElevation(-1.0f);
                dialogCell.setOutlineProvider((ViewOutlineProvider) null);
            }
            ObjectAnimator animator = ObjectAnimator.ofFloat(dialogCell, AnimationProperties.CLIP_DIALOG_CELL_PROGRESS, new float[]{1.0f}).setDuration(180);
            animator.setInterpolator(sDefaultInterpolator);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    DialogsItemAnimator.this.dispatchRemoveStarting(holder);
                }

                public void onAnimationEnd(Animator animator) {
                    animator.removeAllListeners();
                    dialogCell.setClipProgress(0.0f);
                    if (Build.VERSION.SDK_INT >= 21) {
                        dialogCell.setElevation(0.0f);
                    }
                    DialogsItemAnimator.this.dispatchRemoveFinished(holder);
                    DialogsItemAnimator.this.mRemoveAnimations.remove(holder);
                    DialogsItemAnimator.this.dispatchFinishedWhenDone();
                }
            });
            animator.start();
            return;
        }
        final ViewPropertyAnimator animation = view.animate();
        animation.setDuration(180).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchRemoveStarting(holder);
            }

            public void onAnimationEnd(Animator animator) {
                animation.setListener((Animator.AnimatorListener) null);
                view.setAlpha(1.0f);
                DialogsItemAnimator.this.dispatchRemoveFinished(holder);
                DialogsItemAnimator.this.mRemoveAnimations.remove(holder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        if (holder.itemView instanceof DialogCell) {
            ((DialogCell) holder.itemView).setClipProgress(1.0f);
        } else {
            holder.itemView.setAlpha(0.0f);
        }
        this.mPendingAdditions.add(holder);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void animateAddImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        this.mAddAnimations.add(holder);
        if (view instanceof DialogCell) {
            final DialogCell dialogCell = (DialogCell) view;
            ObjectAnimator animator = ObjectAnimator.ofFloat(dialogCell, AnimationProperties.CLIP_DIALOG_CELL_PROGRESS, new float[]{0.0f}).setDuration(180);
            animator.setInterpolator(sDefaultInterpolator);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    DialogsItemAnimator.this.dispatchAddStarting(holder);
                }

                public void onAnimationCancel(Animator animator) {
                    dialogCell.setClipProgress(0.0f);
                }

                public void onAnimationEnd(Animator animator) {
                    animator.removeAllListeners();
                    DialogsItemAnimator.this.dispatchAddFinished(holder);
                    DialogsItemAnimator.this.mAddAnimations.remove(holder);
                    DialogsItemAnimator.this.dispatchFinishedWhenDone();
                }
            });
            animator.start();
            return;
        }
        final ViewPropertyAnimator animation = view.animate();
        animation.alpha(1.0f).setDuration(180).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchAddStarting(holder);
            }

            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1.0f);
            }

            public void onAnimationEnd(Animator animator) {
                animation.setListener((Animator.AnimatorListener) null);
                DialogsItemAnimator.this.dispatchAddFinished(holder);
                DialogsItemAnimator.this.mAddAnimations.remove(holder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        RecyclerView.ViewHolder viewHolder = holder;
        View view = viewHolder.itemView;
        int fromX2 = fromX + ((int) viewHolder.itemView.getTranslationX());
        int fromY2 = fromY + ((int) viewHolder.itemView.getTranslationY());
        resetAnimation(holder);
        int deltaX = toX - fromX2;
        int deltaY = toY - fromY2;
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaX != 0) {
            view.setTranslationX((float) (-deltaX));
        }
        if (deltaY != 0) {
            view.setTranslationY((float) (-deltaY));
        }
        this.mPendingMoves.add(new MoveInfo(holder, fromX2, fromY2, toX, toY));
        return true;
    }

    public void onListScroll(int dy) {
        if (!this.mPendingRemovals.isEmpty()) {
            int N = this.mPendingRemovals.size();
            for (int a = 0; a < N; a++) {
                RecyclerView.ViewHolder holder = this.mPendingRemovals.get(a);
                holder.itemView.setTranslationY(holder.itemView.getTranslationY() + ((float) dy));
            }
        }
        if (!this.mRemoveAnimations.isEmpty()) {
            int N2 = this.mRemoveAnimations.size();
            for (int a2 = 0; a2 < N2; a2++) {
                RecyclerView.ViewHolder holder2 = this.mRemoveAnimations.get(a2);
                holder2.itemView.setTranslationY(holder2.itemView.getTranslationY() + ((float) dy));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void animateMoveImpl(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        RecyclerView.ViewHolder viewHolder = holder;
        int i = fromY;
        int i2 = toY;
        View view = viewHolder.itemView;
        int deltaX = toX - fromX;
        int deltaY = i2 - i;
        if (deltaX != 0) {
            view.animate().translationX(0.0f);
        }
        if (deltaY != 0) {
            view.animate().translationY(0.0f);
        }
        if (i > i2) {
            this.bottomClip = i - i2;
        } else {
            this.topClip = i2 - i;
        }
        DialogCell dialogCell = this.removingDialog;
        if (dialogCell != null) {
            if (this.topClip != Integer.MAX_VALUE) {
                int measuredHeight = dialogCell.getMeasuredHeight();
                int i3 = this.topClip;
                this.bottomClip = measuredHeight - i3;
                this.removingDialog.setTopClip(i3);
                this.removingDialog.setBottomClip(this.bottomClip);
            } else if (this.bottomClip != Integer.MAX_VALUE) {
                int measuredHeight2 = dialogCell.getMeasuredHeight() - this.bottomClip;
                this.topClip = measuredHeight2;
                this.removingDialog.setTopClip(measuredHeight2);
                this.removingDialog.setBottomClip(this.bottomClip);
            }
        }
        ViewPropertyAnimator animation = view.animate();
        this.mMoveAnimations.add(viewHolder);
        ViewPropertyAnimator duration = animation.setDuration(180);
        final RecyclerView.ViewHolder viewHolder2 = holder;
        final int i4 = deltaX;
        final View view2 = view;
        final int i5 = deltaY;
        AnonymousClass5 r7 = r0;
        final ViewPropertyAnimator viewPropertyAnimator = animation;
        AnonymousClass5 r0 = new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchMoveStarting(viewHolder2);
            }

            public void onAnimationCancel(Animator animator) {
                if (i4 != 0) {
                    view2.setTranslationX(0.0f);
                }
                if (i5 != 0) {
                    view2.setTranslationY(0.0f);
                }
            }

            public void onAnimationEnd(Animator animator) {
                viewPropertyAnimator.setListener((Animator.AnimatorListener) null);
                DialogsItemAnimator.this.dispatchMoveFinished(viewHolder2);
                DialogsItemAnimator.this.mMoveAnimations.remove(viewHolder2);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
            }
        };
        duration.setListener(r7).start();
    }

    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        if (!(oldHolder.itemView instanceof DialogCell)) {
            return false;
        }
        resetAnimation(oldHolder);
        resetAnimation(newHolder);
        oldHolder.itemView.setAlpha(1.0f);
        newHolder.itemView.setAlpha(0.0f);
        newHolder.itemView.setTranslationX(0.0f);
        this.mPendingChanges.add(new ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY));
        return true;
    }

    /* access modifiers changed from: package-private */
    public void animateChangeImpl(final ChangeInfo changeInfo) {
        final RecyclerView.ViewHolder holder = changeInfo.oldHolder;
        RecyclerView.ViewHolder newHolder = changeInfo.newHolder;
        if (holder != null && newHolder != null) {
            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(holder.itemView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(newHolder.itemView, View.ALPHA, new float[]{1.0f})});
            this.mChangeAnimations.add(changeInfo.oldHolder);
            this.mChangeAnimations.add(changeInfo.newHolder);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    DialogsItemAnimator.this.dispatchChangeStarting(changeInfo.oldHolder, true);
                    DialogsItemAnimator.this.dispatchChangeStarting(changeInfo.newHolder, false);
                }

                public void onAnimationEnd(Animator animator) {
                    holder.itemView.setAlpha(1.0f);
                    animatorSet.removeAllListeners();
                    DialogsItemAnimator.this.dispatchChangeFinished(changeInfo.oldHolder, true);
                    DialogsItemAnimator.this.mChangeAnimations.remove(changeInfo.oldHolder);
                    DialogsItemAnimator.this.dispatchFinishedWhenDone();
                    DialogsItemAnimator.this.dispatchChangeFinished(changeInfo.newHolder, false);
                    DialogsItemAnimator.this.mChangeAnimations.remove(changeInfo.newHolder);
                    DialogsItemAnimator.this.dispatchFinishedWhenDone();
                }
            });
            animatorSet.start();
        }
    }

    private void endChangeAnimation(List<ChangeInfo> infoList, RecyclerView.ViewHolder item) {
        for (int i = infoList.size() - 1; i >= 0; i--) {
            ChangeInfo changeInfo = infoList.get(i);
            if (endChangeAnimationIfNecessary(changeInfo, item) && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                infoList.remove(changeInfo);
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder);
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, RecyclerView.ViewHolder item) {
        boolean oldItem = false;
        if (changeInfo.newHolder == item) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder != item) {
            return false;
        } else {
            changeInfo.oldHolder = null;
            oldItem = true;
        }
        item.itemView.setAlpha(1.0f);
        item.itemView.setTranslationX(0.0f);
        item.itemView.setTranslationY(0.0f);
        dispatchChangeFinished(item, oldItem);
        return true;
    }

    public void endAnimation(RecyclerView.ViewHolder item) {
        View view = item.itemView;
        view.animate().cancel();
        int i = this.mPendingMoves.size();
        while (true) {
            i--;
            if (i < 0) {
                break;
            } else if (this.mPendingMoves.get(i).holder == item) {
                view.setTranslationY(0.0f);
                view.setTranslationX(0.0f);
                dispatchMoveFinished(item);
                this.mPendingMoves.remove(i);
            }
        }
        endChangeAnimation(this.mPendingChanges, item);
        if (this.mPendingRemovals.remove(item)) {
            if (view instanceof DialogCell) {
                ((DialogCell) view).setClipProgress(0.0f);
            } else {
                view.setAlpha(1.0f);
            }
            dispatchRemoveFinished(item);
        }
        if (this.mPendingAdditions.remove(item)) {
            if (view instanceof DialogCell) {
                ((DialogCell) view).setClipProgress(0.0f);
            } else {
                view.setAlpha(1.0f);
            }
            dispatchAddFinished(item);
        }
        for (int i2 = this.mChangesList.size() - 1; i2 >= 0; i2--) {
            ArrayList<ChangeInfo> changes = this.mChangesList.get(i2);
            endChangeAnimation(changes, item);
            if (changes.isEmpty()) {
                this.mChangesList.remove(i2);
            }
        }
        for (int i3 = this.mMovesList.size() - 1; i3 >= 0; i3--) {
            ArrayList<MoveInfo> moves = this.mMovesList.get(i3);
            int j = moves.size() - 1;
            while (true) {
                if (j < 0) {
                    break;
                } else if (moves.get(j).holder == item) {
                    view.setTranslationY(0.0f);
                    view.setTranslationX(0.0f);
                    dispatchMoveFinished(item);
                    moves.remove(j);
                    if (moves.isEmpty()) {
                        this.mMovesList.remove(i3);
                    }
                } else {
                    j--;
                }
            }
        }
        for (int i4 = this.mAdditionsList.size() - 1; i4 >= 0; i4--) {
            ArrayList<RecyclerView.ViewHolder> additions = this.mAdditionsList.get(i4);
            if (additions.remove(item)) {
                if (view instanceof DialogCell) {
                    ((DialogCell) view).setClipProgress(1.0f);
                } else {
                    view.setAlpha(1.0f);
                }
                dispatchAddFinished(item);
                if (additions.isEmpty()) {
                    this.mAdditionsList.remove(i4);
                }
            }
        }
        this.mRemoveAnimations.remove(item);
        this.mAddAnimations.remove(item);
        this.mChangeAnimations.remove(item);
        this.mMoveAnimations.remove(item);
        dispatchFinishedWhenDone();
    }

    private void resetAnimation(RecyclerView.ViewHolder holder) {
        holder.itemView.animate().setInterpolator(sDefaultInterpolator);
        endAnimation(holder);
    }

    public boolean isRunning() {
        return !this.mPendingAdditions.isEmpty() || !this.mPendingChanges.isEmpty() || !this.mPendingMoves.isEmpty() || !this.mPendingRemovals.isEmpty() || !this.mMoveAnimations.isEmpty() || !this.mRemoveAnimations.isEmpty() || !this.mAddAnimations.isEmpty() || !this.mChangeAnimations.isEmpty() || !this.mMovesList.isEmpty() || !this.mAdditionsList.isEmpty() || !this.mChangesList.isEmpty();
    }

    /* access modifiers changed from: package-private */
    public void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
            onAllAnimationsDone();
        }
    }

    /* access modifiers changed from: protected */
    public void onAllAnimationsDone() {
    }

    public void endAnimations() {
        for (int i = this.mPendingMoves.size() - 1; i >= 0; i--) {
            MoveInfo item = this.mPendingMoves.get(i);
            View view = item.holder.itemView;
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            dispatchMoveFinished(item.holder);
            this.mPendingMoves.remove(i);
        }
        for (int i2 = this.mPendingRemovals.size() - 1; i2 >= 0; i2--) {
            RecyclerView.ViewHolder item2 = this.mPendingRemovals.get(i2);
            View view2 = item2.itemView;
            view2.setTranslationY(0.0f);
            view2.setTranslationX(0.0f);
            dispatchRemoveFinished(item2);
            this.mPendingRemovals.remove(i2);
        }
        for (int i3 = this.mPendingAdditions.size() - 1; i3 >= 0; i3--) {
            RecyclerView.ViewHolder item3 = this.mPendingAdditions.get(i3);
            if (item3.itemView instanceof DialogCell) {
                ((DialogCell) item3.itemView).setClipProgress(0.0f);
            } else {
                item3.itemView.setAlpha(1.0f);
            }
            dispatchAddFinished(item3);
            this.mPendingAdditions.remove(i3);
        }
        for (int i4 = this.mPendingChanges.size() - 1; i4 >= 0; i4--) {
            endChangeAnimationIfNecessary(this.mPendingChanges.get(i4));
        }
        this.mPendingChanges.clear();
        if (isRunning()) {
            for (int i5 = this.mMovesList.size() - 1; i5 >= 0; i5--) {
                ArrayList<MoveInfo> moves = this.mMovesList.get(i5);
                for (int j = moves.size() - 1; j >= 0; j--) {
                    MoveInfo moveInfo = moves.get(j);
                    View view3 = moveInfo.holder.itemView;
                    view3.setTranslationY(0.0f);
                    view3.setTranslationX(0.0f);
                    dispatchMoveFinished(moveInfo.holder);
                    moves.remove(j);
                    if (moves.isEmpty()) {
                        this.mMovesList.remove(moves);
                    }
                }
            }
            for (int i6 = this.mAdditionsList.size() - 1; i6 >= 0; i6--) {
                ArrayList<RecyclerView.ViewHolder> additions = this.mAdditionsList.get(i6);
                for (int j2 = additions.size() - 1; j2 >= 0; j2--) {
                    RecyclerView.ViewHolder item4 = additions.get(j2);
                    View view4 = item4.itemView;
                    if (view4 instanceof DialogCell) {
                        ((DialogCell) view4).setClipProgress(0.0f);
                    } else {
                        view4.setAlpha(1.0f);
                    }
                    dispatchAddFinished(item4);
                    additions.remove(j2);
                    if (additions.isEmpty()) {
                        this.mAdditionsList.remove(additions);
                    }
                }
            }
            for (int i7 = this.mChangesList.size() - 1; i7 >= 0; i7--) {
                ArrayList<ChangeInfo> changes = this.mChangesList.get(i7);
                for (int j3 = changes.size() - 1; j3 >= 0; j3--) {
                    endChangeAnimationIfNecessary(changes.get(j3));
                    if (changes.isEmpty()) {
                        this.mChangesList.remove(changes);
                    }
                }
            }
            cancelAll(this.mRemoveAnimations);
            cancelAll(this.mMoveAnimations);
            cancelAll(this.mAddAnimations);
            cancelAll(this.mChangeAnimations);
            dispatchAnimationsFinished();
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAll(List<RecyclerView.ViewHolder> viewHolders) {
        for (int i = viewHolders.size() - 1; i >= 0; i--) {
            viewHolders.get(i).itemView.animate().cancel();
        }
    }

    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder, List<Object> list) {
        return false;
    }
}
