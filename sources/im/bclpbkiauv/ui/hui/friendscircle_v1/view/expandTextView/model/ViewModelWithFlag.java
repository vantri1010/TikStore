package im.bclpbkiauv.ui.hui.friendscircle_v1.view.expandTextView.model;

import com.bjz.comm.net.expandViewModel.ExpandableStatusFix;
import com.bjz.comm.net.expandViewModel.StatusType;

public class ViewModelWithFlag implements ExpandableStatusFix {
    private String content;
    private StatusType status;

    public ViewModelWithFlag(String content2) {
        this.content = content2;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content2) {
        this.content = content2;
    }

    public void setStatusType(StatusType statusType) {
        this.status = statusType;
    }

    public StatusType getStatusType() {
        return this.status;
    }
}
