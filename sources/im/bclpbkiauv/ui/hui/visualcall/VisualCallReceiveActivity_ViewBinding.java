package im.bclpbkiauv.ui.hui.visualcall;

import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ColorTextView;
import im.bclpbkiauv.ui.hviews.DragFrameLayout;
import org.webrtc.sdk.SophonSurfaceView;

public class VisualCallReceiveActivity_ViewBinding implements Unbinder {
    private VisualCallReceiveActivity target;
    private View view7f0900ca;
    private View view7f0901aa;
    private View view7f0901ab;
    private View view7f0901ac;
    private View view7f0901ae;
    private View view7f0901b6;
    private View view7f090241;
    private View view7f0902a2;
    private View view7f090673;

    public VisualCallReceiveActivity_ViewBinding(VisualCallReceiveActivity target2) {
        this(target2, target2.getWindow().getDecorView());
    }

    public VisualCallReceiveActivity_ViewBinding(final VisualCallReceiveActivity target2, View source) {
        this.target = target2;
        View view = Utils.findRequiredView(source, R.id.img_operate_a, "field 'imgOperateA' and method 'onViewClicked'");
        target2.imgOperateA = (ImageView) Utils.castView(view, R.id.img_operate_a, "field 'imgOperateA'", ImageView.class);
        this.view7f0901aa = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.linOperateA = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.lin_operate_a, "field 'linOperateA'", LinearLayout.class);
        target2.linOperateB = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.lin_operate_b, "field 'linOperateB'", LinearLayout.class);
        View view2 = Utils.findRequiredView(source, R.id.img_operate_c, "field 'imgOperateC' and method 'onViewClicked'");
        target2.imgOperateC = (ImageView) Utils.castView(view2, R.id.img_operate_c, "field 'imgOperateC'", ImageView.class);
        this.view7f0901ac = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.linOperateC = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.lin_operate_c, "field 'linOperateC'", LinearLayout.class);
        target2.relVideoUser = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rel_video_user, "field 'relVideoUser'", RelativeLayout.class);
        target2.sfLocalView = (SophonSurfaceView) Utils.findRequiredViewAsType(source, R.id.sf_local_view, "field 'sfLocalView'", SophonSurfaceView.class);
        target2.imgVideoUserHead = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.img_video_user_head, "field 'imgVideoUserHead'", BackupImageView.class);
        target2.txtVideoName = (TextView) Utils.findRequiredViewAsType(source, R.id.txt_video_name, "field 'txtVideoName'", TextView.class);
        target2.txtVideoStatus = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_video_status, "field 'txtVideoStatus'", ColorTextView.class);
        View view3 = Utils.findRequiredView(source, R.id.img_visualcall, "field 'imgVisualcall' and method 'onViewClicked'");
        target2.imgVisualcall = (ImageView) Utils.castView(view3, R.id.img_visualcall, "field 'imgVisualcall'", ImageView.class);
        this.view7f0901b6 = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.imgUserHead = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.img_user_head, "field 'imgUserHead'", BackupImageView.class);
        target2.txtCallName = (TextView) Utils.findRequiredViewAsType(source, R.id.txt_call_name, "field 'txtCallName'", TextView.class);
        target2.txtCallStatus = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_call_status, "field 'txtCallStatus'", ColorTextView.class);
        target2.relVoiceUser = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rel_voice_user, "field 'relVoiceUser'", RelativeLayout.class);
        target2.chartContentUserlist = (RecyclerView) Utils.findRequiredViewAsType(source, R.id.chart_content_userlist, "field 'chartContentUserlist'", RecyclerView.class);
        View view4 = Utils.findRequiredView(source, R.id.txt_pre_change_to_voice, "field 'txtPreChangeToVoice' and method 'onViewClicked'");
        target2.txtPreChangeToVoice = (ColorTextView) Utils.castView(view4, R.id.txt_pre_change_to_voice, "field 'txtPreChangeToVoice'", ColorTextView.class);
        this.view7f090673 = view4;
        view4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.txtVisualcallStatus = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_visualcall_status, "field 'txtVisualcallStatus'", ColorTextView.class);
        target2.txtOperateA = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_operate_a, "field 'txtOperateA'", ColorTextView.class);
        View view5 = Utils.findRequiredView(source, R.id.img_operate_b, "field 'imgOperateB' and method 'onViewClicked'");
        target2.imgOperateB = (ImageView) Utils.castView(view5, R.id.img_operate_b, "field 'imgOperateB'", ImageView.class);
        this.view7f0901ab = view5;
        view5.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.txtOperateB = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_operate_b, "field 'txtOperateB'", ColorTextView.class);
        target2.txtOperateC = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_operate_c, "field 'txtOperateC'", ColorTextView.class);
        target2.relVisualCallA = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.rel_visual_call_a, "field 'relVisualCallA'", LinearLayout.class);
        target2.linPreRefuse = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.lin_pre_refuse, "field 'linPreRefuse'", LinearLayout.class);
        View view6 = Utils.findRequiredView(source, R.id.img_pre_receive, "field 'imgPreReceive' and method 'onViewClicked'");
        target2.imgPreReceive = (ImageView) Utils.castView(view6, R.id.img_pre_receive, "field 'imgPreReceive'", ImageView.class);
        this.view7f0901ae = view6;
        view6.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.linPreReceive = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.lin_pre_receive, "field 'linPreReceive'", LinearLayout.class);
        target2.relVisualCallB = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rel_visual_call_b, "field 'relVisualCallB'", RelativeLayout.class);
        target2.rootView = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.root_view, "field 'rootView'", RelativeLayout.class);
        View view7 = Utils.findRequiredView(source, R.id.chart_video_container, "field 'chartVideoContainer' and method 'onViewClicked'");
        target2.chartVideoContainer = (DragFrameLayout) Utils.castView(view7, R.id.chart_video_container, "field 'chartVideoContainer'", DragFrameLayout.class);
        this.view7f0900ca = view7;
        view7.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        View view8 = Utils.findRequiredView(source, R.id.ll_big_window, "field 'llBigWindow' and method 'onViewClicked'");
        target2.llBigWindow = (LinearLayout) Utils.castView(view8, R.id.ll_big_window, "field 'llBigWindow'", LinearLayout.class);
        this.view7f0902a2 = view8;
        view8.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.chrVisualcallTime = (Chronometer) Utils.findRequiredViewAsType(source, R.id.chr_visualcall_time, "field 'chrVisualcallTime'", Chronometer.class);
        target2.txtTip = (TextView) Utils.findRequiredViewAsType(source, R.id.txt_tip, "field 'txtTip'", TextView.class);
        View view9 = Utils.findRequiredView(source, R.id.iv_pre_refuse, "field 'ivPreRefuse' and method 'onViewClicked'");
        target2.ivPreRefuse = (ImageView) Utils.castView(view9, R.id.iv_pre_refuse, "field 'ivPreRefuse'", ImageView.class);
        this.view7f090241 = view9;
        view9.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.txtMask = (TextView) Utils.findRequiredViewAsType(source, R.id.txt_mask, "field 'txtMask'", TextView.class);
        target2.llBigRemoteView = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_big_remote_view, "field 'llBigRemoteView'", LinearLayout.class);
        target2.sfSmallView = (SophonSurfaceView) Utils.findRequiredViewAsType(source, R.id.sf_small_view, "field 'sfSmallView'", SophonSurfaceView.class);
        target2.llSmallRemoteView = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_small_remote_view, "field 'llSmallRemoteView'", LinearLayout.class);
    }

    public void unbind() {
        VisualCallReceiveActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.imgOperateA = null;
            target2.linOperateA = null;
            target2.linOperateB = null;
            target2.imgOperateC = null;
            target2.linOperateC = null;
            target2.relVideoUser = null;
            target2.sfLocalView = null;
            target2.imgVideoUserHead = null;
            target2.txtVideoName = null;
            target2.txtVideoStatus = null;
            target2.imgVisualcall = null;
            target2.imgUserHead = null;
            target2.txtCallName = null;
            target2.txtCallStatus = null;
            target2.relVoiceUser = null;
            target2.chartContentUserlist = null;
            target2.txtPreChangeToVoice = null;
            target2.txtVisualcallStatus = null;
            target2.txtOperateA = null;
            target2.imgOperateB = null;
            target2.txtOperateB = null;
            target2.txtOperateC = null;
            target2.relVisualCallA = null;
            target2.linPreRefuse = null;
            target2.imgPreReceive = null;
            target2.linPreReceive = null;
            target2.relVisualCallB = null;
            target2.rootView = null;
            target2.chartVideoContainer = null;
            target2.llBigWindow = null;
            target2.chrVisualcallTime = null;
            target2.txtTip = null;
            target2.ivPreRefuse = null;
            target2.txtMask = null;
            target2.llBigRemoteView = null;
            target2.sfSmallView = null;
            target2.llSmallRemoteView = null;
            this.view7f0901aa.setOnClickListener((View.OnClickListener) null);
            this.view7f0901aa = null;
            this.view7f0901ac.setOnClickListener((View.OnClickListener) null);
            this.view7f0901ac = null;
            this.view7f0901b6.setOnClickListener((View.OnClickListener) null);
            this.view7f0901b6 = null;
            this.view7f090673.setOnClickListener((View.OnClickListener) null);
            this.view7f090673 = null;
            this.view7f0901ab.setOnClickListener((View.OnClickListener) null);
            this.view7f0901ab = null;
            this.view7f0901ae.setOnClickListener((View.OnClickListener) null);
            this.view7f0901ae = null;
            this.view7f0900ca.setOnClickListener((View.OnClickListener) null);
            this.view7f0900ca = null;
            this.view7f0902a2.setOnClickListener((View.OnClickListener) null);
            this.view7f0902a2 = null;
            this.view7f090241.setOnClickListener((View.OnClickListener) null);
            this.view7f090241 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
