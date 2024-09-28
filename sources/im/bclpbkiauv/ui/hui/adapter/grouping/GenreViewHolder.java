package im.bclpbkiauv.ui.hui.adapter.grouping;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.expand.models.ExpandableGroup;
import im.bclpbkiauv.ui.expand.viewholders.GroupViewHolder;
import im.bclpbkiauv.ui.hcells.MryDividerCell;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.List;

public class GenreViewHolder extends GroupViewHolder {
    /* access modifiers changed from: private */
    public MryDividerCell divider;
    private int flatPosition;
    /* access modifiers changed from: private */
    public ExpandableGroup genre;
    /* access modifiers changed from: private */
    public List<? extends ExpandableGroup> groups;
    private ImageView ivArrow;
    private MryTextView tvGenreName;
    private MryTextView tvGenreOnlineNumber;

    public GenreViewHolder(View itemView) {
        super(itemView);
        this.tvGenreName = (MryTextView) itemView.findViewById(R.id.list_item_genre_name);
        this.tvGenreOnlineNumber = (MryTextView) itemView.findViewById(R.id.list_item_genre_online_number);
        this.ivArrow = (ImageView) itemView.findViewById(R.id.list_item_genre_arrow);
        this.divider = (MryDividerCell) itemView.findViewById(R.id.divider);
    }

    public void setGenreData(ExpandableGroup genre2, int flatPosition2, List<? extends ExpandableGroup> groups2) {
        this.genre = genre2;
        this.flatPosition = flatPosition2;
        this.groups = groups2;
        initData();
    }

    private void initData() {
        ExpandableGroup expandableGroup = this.genre;
        if (expandableGroup instanceof Genre) {
            this.tvGenreName.setText(expandableGroup.getTitle());
            MryTextView mryTextView = this.tvGenreOnlineNumber;
            mryTextView.setText(((Genre) this.genre).getOnlineCount() + "/" + this.genre.getItemCount());
        }
        if (this.groups.size() == 1) {
            this.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
            this.divider.setVisibility(8);
        } else if (this.flatPosition == 0) {
            this.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
        } else {
            ExpandableGroup expandableGroup2 = this.genre;
            List<? extends ExpandableGroup> list = this.groups;
            if (expandableGroup2 == list.get(list.size() - 1)) {
                this.divider.setVisibility(8);
                this.itemView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
            }
        }
    }

    public void expand() {
        animateExpand();
    }

    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        final ObjectAnimator animator = ObjectAnimator.ofFloat(this.ivArrow, "rotation", new float[]{0.0f, 90.0f});
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                if (GenreViewHolder.this.groups.size() == 1) {
                    GenreViewHolder.this.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (GenreViewHolder.this.genre == GenreViewHolder.this.groups.get(GenreViewHolder.this.groups.size() - 1)) {
                    GenreViewHolder.this.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                } else {
                    GenreViewHolder.this.divider.setVisibility(8);
                }
                animator.removeListener(this);
            }
        });
        animator.start();
    }

    private void animateCollapse() {
        final ObjectAnimator animator = ObjectAnimator.ofFloat(this.ivArrow, "rotation", new float[]{90.0f, 0.0f});
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (GenreViewHolder.this.groups.size() == 1) {
                    GenreViewHolder.this.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (GenreViewHolder.this.genre == GenreViewHolder.this.groups.get(GenreViewHolder.this.groups.size() - 1)) {
                    GenreViewHolder.this.itemView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else {
                    GenreViewHolder.this.divider.setVisibility(0);
                }
                animator.removeListener(this);
            }
        });
        animator.start();
    }
}
