package im.bclpbkiauv.ui.hui.packet.pop;

import im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse;

public interface OnRedPacketDialogClickListener {
    void onCloseClick();

    void onOpenClick();

    void toDetail(RedpacketResponse redpacketResponse);
}
