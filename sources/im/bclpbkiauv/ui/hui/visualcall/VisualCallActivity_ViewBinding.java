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

public class VisualCallActivity_ViewBinding implements Unbinder {
    private VisualCallActivity target;
    private View view7f0900ca;
    private View view7f0901aa;
    private View view7f0901ab;
    private View view7f0901ac;
    private View view7f0901b6;
    private View view7f0902a2;
    private View view7f090673;

    public VisualCallActivity_ViewBinding(VisualCallActivity target2) {
        this(target2, target2.getWindow().getDecorView());
    }

    public VisualCallActivity_ViewBinding(final VisualCallActivity target2, View source) {
        this.target = target2;
        View view = Utils.findRequiredView(source, R.id.img_operate_a, "field 'img_operate_a' and method 'onclick'");
        target2.img_operate_a = (ImageView) Utils.castView(view, R.id.img_operate_a, "field 'img_operate_a'", ImageView.class);
        this.view7f0901aa = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onclick(p0);
            }
        });
        target2.lin_operate_a = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.lin_operate_a, "field 'lin_operate_a'", LinearLayout.class);
        target2.txt_operate_a = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_operate_a, "field 'txt_operate_a'", ColorTextView.class);
        View view2 = Utils.findRequiredView(source, R.id.img_operate_b, "field 'img_operate_b' and method 'onclick'");
        target2.img_operate_b = (ImageView) Utils.castView(view2, R.id.img_operate_b, "field 'img_operate_b'", ImageView.class);
        this.view7f0901ab = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onclick(p0);
            }
        });
        target2.lin_operate_b = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.lin_operate_b, "field 'lin_operate_b'", LinearLayout.class);
        target2.txt_operate_b = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_operate_b, "field 'txt_operate_b'", ColorTextView.class);
        View view3 = Utils.findRequiredView(source, R.id.img_operate_c, "field 'img_operate_c' and method 'onclick'");
        target2.img_operate_c = (ImageView) Utils.castView(view3, R.id.img_operate_c, "field 'img_operate_c'", ImageView.class);
        this.view7f0901ac = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onclick(p0);
            }
        });
        target2.lin_operate_c = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.lin_operate_c, "field 'lin_operate_c'", LinearLayout.class);
        target2.txt_operate_c = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_operate_c, "field 'txt_operate_c'", ColorTextView.class);
        target2.rel_video_user = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rel_video_user, "field 'rel_video_user'", RelativeLayout.class);
        target2.rel_voice_user = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rel_voice_user, "field 'rel_voice_user'", RelativeLayout.class);
        target2.rel_visual_call_b = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rel_visual_call_b, "field 'rel_visual_call_b'", RelativeLayout.class);
        target2.img_pre_receive = (ImageView) Utils.findRequiredViewAsType(source, R.id.img_pre_receive, "field 'img_pre_receive'", ImageView.class);
        target2.rel_visual_call_a = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.rel_visual_call_a, "field 'rel_visual_call_a'", LinearLayout.class);
        View view4 = Utils.findRequiredView(source, R.id.txt_pre_change_to_voice, "field 'txt_pre_change_to_voice' and method 'onclick'");
        target2.txt_pre_change_to_voice = (TextView) Utils.castView(view4, R.id.txt_pre_change_to_voice, "field 'txt_pre_change_to_voice'", TextView.class);
        this.view7f090673 = view4;
        view4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onclick(p0);
            }
        });
        target2.mLocalView = (SophonSurfaceView) Utils.findRequiredViewAsType(source, R.id.sf_local_view, "field 'mLocalView'", SophonSurfaceView.class);
        target2.chartUserListView = (RecyclerView) Utils.findRequiredViewAsType(source, R.id.chart_content_userlist, "field 'chartUserListView'", RecyclerView.class);
        View view5 = Utils.findRequiredView(source, R.id.chart_video_container, "field 'chart_video_container' and method 'onclick'");
        target2.chart_video_container = (DragFrameLayout) Utils.castView(view5, R.id.chart_video_container, "field 'chart_video_container'", DragFrameLayout.class);
        this.view7f0900ca = view5;
        view5.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onclick(p0);
            }
        });
        target2.txtTip = (TextView) Utils.findRequiredViewAsType(source, R.id.txt_tip, "field 'txtTip'", TextView.class);
        target2.chrVisualcallTime = (Chronometer) Utils.findRequiredViewAsType(source, R.id.chr_visualcall_time, "field 'chrVisualcallTime'", Chronometer.class);
        target2.txtVisualcallStatus = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_visualcall_status, "field 'txtVisualcallStatus'", ColorTextView.class);
        View view6 = Utils.findRequiredView(source, R.id.ll_big_window, "field 'llBigWindow' and method 'onclick'");
        target2.llBigWindow = (LinearLayout) Utils.castView(view6, R.id.ll_big_window, "field 'llBigWindow'", LinearLayout.class);
        this.view7f0902a2 = view6;
        view6.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onclick(p0);
            }
        });
        target2.imgVideoUserHead = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.img_video_user_head, "field 'imgVideoUserHead'", BackupImageView.class);
        target2.imgUserHead = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.img_user_head, "field 'imgUserHead'", BackupImageView.class);
        target2.txtVideoName = (TextView) Utils.findRequiredViewAsType(source, R.id.txt_video_name, "field 'txtVideoName'", TextView.class);
        target2.txtCallName = (TextView) Utils.findRequiredViewAsType(source, R.id.txt_call_name, "field 'txtCallName'", TextView.class);
        target2.txtVideoStatus = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_video_status, "field 'txtVideoStatus'", ColorTextView.class);
        target2.txtCallStatus = (ColorTextView) Utils.findRequiredViewAsType(source, R.id.txt_call_status, "field 'txtCallStatus'", ColorTextView.class);
        target2.llBigRemoteView = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_big_remote_view, "field 'llBigRemoteView'", LinearLayout.class);
        target2.sfSmallView = (SophonSurfaceView) Utils.findRequiredViewAsType(source, R.id.sf_small_view, "field 'sfSmallView'", SophonSurfaceView.class);
        target2.llSmallRemoteView = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_small_remote_view, "field 'llSmallRemoteView'", LinearLayout.class);
        View view7 = Utils.findRequiredView(source, R.id.img_visualcall, "field 'imgVisualcall' and method 'onclick'");
        target2.imgVisualcall = (ImageView) Utils.castView(view7, R.id.img_visualcall, "field 'imgVisualcall'", ImageView.class);
        this.view7f0901b6 = view7;
        view7.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onclick(p0);
            }
        });
    }

    public void unbind() {
        VisualCallActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.img_operate_a = null;
            target2.lin_operate_a = null;
            target2.txt_operate_a = null;
            target2.img_operate_b = null;
            target2.lin_operate_b = null;
            target2.txt_operate_b = null;
            target2.img_operate_c = null;
            target2.lin_operate_c = null;
            target2.txt_operate_c = null;
            target2.rel_video_user = null;
            target2.rel_voice_user = null;
            target2.rel_visual_call_b = null;
            target2.img_pre_receive = null;
            target2.rel_visual_call_a = null;
            target2.txt_pre_change_to_voice = null;
            target2.mLocalView = null;
            target2.chartUserListView = null;
            target2.chart_video_container = null;
            target2.txtTip = null;
            target2.chrVisualcallTime = null;
            target2.txtVisualcallStatus = null;
            target2.llBigWindow = null;
            target2.imgVideoUserHead = null;
            target2.imgUserHead = null;
            target2.txtVideoName = null;
            target2.txtCallName = null;
            target2.txtVideoStatus = null;
            target2.txtCallStatus = null;
            target2.llBigRemoteView = null;
            target2.sfSmallView = null;
            target2.llSmallRemoteView = null;
            target2.imgVisualcall = null;
            this.view7f0901aa.setOnClickListener((View.OnClickListener) null);
            this.view7f0901aa = null;
            this.view7f0901ab.setOnClickListener((View.OnClickListener) null);
            this.view7f0901ab = null;
            this.view7f0901ac.setOnClickListener((View.OnClickListener) null);
            this.view7f0901ac = null;
            this.view7f090673.setOnClickListener((View.OnClickListener) null);
            this.view7f090673 = null;
            this.view7f0900ca.setOnClickListener((View.OnClickListener) null);
            this.view7f0900ca = null;
            this.view7f0902a2.setOnClickListener((View.OnClickListener) null);
            this.view7f0902a2 = null;
            this.view7f0901b6.setOnClickListener((View.OnClickListener) null);
            this.view7f0901b6 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
