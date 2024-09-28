package im.bclpbkiauv.ui.hui.packet.pop;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketBean;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse;
import im.bclpbkiauv.ui.hui.packet.pop.FrameAnimation;
import im.bclpbkiauv.ui.utils.number.TimeUtils;

public class RedPacketViewHolder implements View.OnClickListener {
    private Context mContext;
    private FrameAnimation mFrameAnimation;
    private int[] mImgResIds = {R.mipmap.icon_open_red_packet1, R.mipmap.icon_open_red_packet2, R.mipmap.icon_open_red_packet3, R.mipmap.icon_open_red_packet4, R.mipmap.icon_open_red_packet5, R.mipmap.icon_open_red_packet6, R.mipmap.icon_open_red_packet7, R.mipmap.icon_open_red_packet7, R.mipmap.icon_open_red_packet8, R.mipmap.icon_open_red_packet9, R.mipmap.icon_open_red_packet4, R.mipmap.icon_open_red_packet10, R.mipmap.icon_open_red_packet11};
    private BackupImageView mIvAvatar;
    private ImageView mIvClose;
    /* access modifiers changed from: private */
    public ImageView mIvOpen;
    private OnRedPacketDialogClickListener mListener;
    /* access modifiers changed from: private */
    public TextView mTvDetail;
    /* access modifiers changed from: private */
    public TextView mTvMsg;
    private TextView mTvName;
    private RedpacketResponse ret;

    public RedPacketViewHolder(Context context, View view) {
        this.mContext = context;
        ButterKnife.bind((Object) this, view);
        this.mIvClose = (ImageView) view.findViewById(R.id.iv_close);
        this.mIvAvatar = (BackupImageView) view.findViewById(R.id.iv_avatar);
        this.mTvName = (TextView) view.findViewById(R.id.tv_name);
        this.mTvMsg = (TextView) view.findViewById(R.id.tv_msg);
        this.mIvOpen = (ImageView) view.findViewById(R.id.iv_open);
        this.mTvDetail = (TextView) view.findViewById(R.id.tv_details);
        this.mTvMsg.setSelected(true);
        this.mIvOpen.setOnClickListener(this);
        this.mIvClose.setOnClickListener(this);
        this.mTvDetail.setOnClickListener(this);
    }

    public void setPromtText(final String text, final boolean showDetail) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                RedPacketViewHolder.this.mTvMsg.setText(text);
                RedPacketViewHolder.this.stopAnim();
                RedPacketViewHolder.this.mIvOpen.setVisibility(8);
                if (showDetail) {
                    RedPacketViewHolder.this.mTvDetail.setVisibility(0);
                }
            }
        });
    }

    public void setPromtText(String text) {
        setPromtText(text, false);
    }

    public void setRet(RedpacketResponse ret2) {
        this.ret = ret2;
    }

    public void clear() {
        this.ret = null;
    }

    public void onClick(View view) {
        OnRedPacketDialogClickListener onRedPacketDialogClickListener;
        int id = view.getId();
        if (id == R.id.iv_close) {
            stopAnim();
            OnRedPacketDialogClickListener onRedPacketDialogClickListener2 = this.mListener;
            if (onRedPacketDialogClickListener2 != null) {
                onRedPacketDialogClickListener2.onCloseClick();
            }
        } else if (id != R.id.iv_open) {
            if (id == R.id.tv_details && (onRedPacketDialogClickListener = this.mListener) != null) {
                onRedPacketDialogClickListener.toDetail(this.ret);
            }
        } else if (this.mFrameAnimation == null) {
            startAnim();
            OnRedPacketDialogClickListener onRedPacketDialogClickListener3 = this.mListener;
            if (onRedPacketDialogClickListener3 != null) {
                onRedPacketDialogClickListener3.onOpenClick();
            }
        }
    }

    public void setData(TLRPC.User sender, RedpacketResponse bean, boolean isChat) {
        TLRPC.User user = sender;
        if (bean != null && bean.getRed() != null) {
            RedpacketBean red = bean.getRed();
            if ("2".equals(red.getRedType())) {
                String userId = red.getRecipientUserId();
                boolean exclusive = UserConfig.getInstance(UserConfig.selectedAccount).clientUserId == Integer.parseInt(userId);
                TLRPC.User user2 = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(Integer.parseInt(userId)));
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
                avatarDrawable.setInfo(user2);
                this.mIvAvatar.setRoundRadius(AndroidUtilities.dp(7.0f));
                this.mIvAvatar.setImage(ImageLocation.getForUser(user2, false), "50_50", (Drawable) avatarDrawable, (Object) user2);
                TextView textView = this.mTvName;
                textView.setText(UserObject.getName(user2, 6) + LocaleController.getString(R.string.redpacket_group_exclusive));
                this.mTvName.setTextColor(-338532);
                if (exclusive) {
                    int status = Integer.parseInt(red.getStatus());
                    this.mTvMsg.setTextColor(-1);
                    if (status == 2) {
                        this.mTvMsg.setText(String.format("" + LocaleController.getString(R.string.ThisRedPacketsAlreadyBy) + "%s" + LocaleController.getString(R.string.Overdue), new Object[]{TimeUtils.getTimeString(Long.parseLong(red.getCreateTime()) + 86400000, LocaleController.getString(R.string.formatterMonthDayTime24H2))}));
                        this.mIvOpen.setVisibility(8);
                    } else {
                        this.mIvOpen.setVisibility(0);
                        if (bean.getRed().getRemarks() == null || TextUtils.isEmpty(bean.getRed().getRemarks())) {
                            this.mTvMsg.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
                            this.mTvMsg.setTextColor(-1);
                        } else {
                            this.mTvMsg.setText(bean.getRed().getRemarks());
                            this.mTvMsg.setTextColor(-1);
                        }
                    }
                    this.mTvDetail.setVisibility(0);
                    TextView textView2 = this.mTvDetail;
                    textView2.setText(LocaleController.getString(R.string.From) + user.first_name);
                    this.mTvDetail.setTextColor(-338532);
                    return;
                }
                this.mIvOpen.setVisibility(8);
                this.mTvMsg.setText(LocaleController.getString(R.string.ExclusiveRedBagCannotGetIt));
                this.mTvMsg.setTextColor(-1);
                this.mTvDetail.setVisibility(0);
                this.mTvDetail.setText(LocaleController.getString(R.string.CheckHotCoinCount));
                this.mTvDetail.setTextColor(-338532);
                return;
            }
            if (user != null) {
                AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                avatarDrawable2.setTextSize(AndroidUtilities.dp(16.0f));
                avatarDrawable2.setInfo(user);
                this.mIvAvatar.setRoundRadius(AndroidUtilities.dp(7.0f));
                this.mIvAvatar.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable2, (Object) user);
                if (isChat) {
                    StringBuilder builder = new StringBuilder(user.first_name);
                    int redType = bean.getRed().getRedTypeInt();
                    if (redType != 0) {
                        if (redType == 1) {
                            if (bean.getRed().getGrantTypeInt() == 0) {
                                builder.append(LocaleController.getString(R.string.SentANormalRedPackets));
                            } else {
                                builder.append(LocaleController.getString(R.string.SentALuckRedPackets));
                            }
                        } else if (redType == 2) {
                            builder.append(LocaleController.getString(R.string.SentALuckRExclusivePackets));
                        }
                    }
                    this.mTvName.setText(builder.toString());
                    this.mTvName.setTextColor(-338532);
                } else {
                    TextView textView3 = this.mTvName;
                    textView3.setText(user.first_name + LocaleController.getString(R.string.SentARedPacketsToYou));
                    this.mTvName.setTextColor(-338532);
                }
            }
            int status2 = Integer.parseInt(red.getStatus());
            if (status2 == 2) {
                TextView textView4 = this.mTvMsg;
                textView4.setText(String.format(LocaleController.getString(R.string.ThisRedPacketsAlreadyBy) + "%s" + LocaleController.getString(R.string.Overdue), new Object[]{TimeUtils.getTimeString(Long.parseLong(red.getCreateTime()) + 86400000, LocaleController.getString(R.string.formatterMonthDayTime24H2))}));
                this.mTvMsg.setTextColor(-1);
                this.mIvOpen.setVisibility(8);
                this.mTvDetail.setVisibility(isChat ? 0 : 8);
                TextView textView5 = this.mTvDetail;
                textView5.setText(LocaleController.getString(R.string.LookReceivedDetails) + " >");
                this.mTvDetail.setTextColor(-338532);
                this.mTvDetail.setEnabled(true);
            } else if (status2 == 1) {
                this.mTvMsg.setText(LocaleController.getString(R.string.ToLateRedPacketsIsAllHadBeenReceived));
                this.mTvMsg.setTextColor(-1);
                this.mIvOpen.setVisibility(8);
                this.mTvDetail.setVisibility(0);
                TextView textView6 = this.mTvDetail;
                textView6.setText(LocaleController.getString(R.string.LookReceivedDetails) + " >");
                this.mTvDetail.setTextColor(-338532);
                this.mTvDetail.setEnabled(true);
            } else {
                this.mIvOpen.setVisibility(0);
                TextView textView7 = this.mTvDetail;
                textView7.setText(LocaleController.getString(R.string.LookReceivedDetails) + " >");
                if (!isChat) {
                    this.mTvDetail.setVisibility(8);
                } else if (red.getIsReceived() != 0 || user == null || user.id == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                    this.mTvDetail.setVisibility(0);
                } else {
                    this.mTvDetail.setVisibility(8);
                }
                this.mTvDetail.setEnabled(true);
                this.mTvDetail.setTextColor(-338532);
                if (bean.getRed().getRemarks() == null || TextUtils.isEmpty(bean.getRed().getRemarks())) {
                    this.mTvMsg.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
                    this.mTvMsg.setTextColor(-1);
                    return;
                }
                this.mTvMsg.setText(bean.getRed().getRemarks());
                this.mTvMsg.setTextColor(-1);
            }
        }
    }

    public void startAnim() {
        FrameAnimation frameAnimation = new FrameAnimation(this.mIvOpen, this.mImgResIds, 125, true);
        this.mFrameAnimation = frameAnimation;
        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            public void onAnimationStart() {
                Log.i("", TtmlNode.START);
            }

            public void onAnimationEnd() {
                Log.i("", TtmlNode.END);
            }

            public void onAnimationRepeat() {
                Log.i("", "repeat");
            }

            public void onAnimationPause() {
                RedPacketViewHolder.this.mIvOpen.setBackgroundResource(R.mipmap.icon_open_red_packet1);
            }
        });
    }

    public void stopAnim() {
        FrameAnimation frameAnimation = this.mFrameAnimation;
        if (frameAnimation != null) {
            frameAnimation.release();
            this.mFrameAnimation = null;
        }
    }

    public void setOnRedPacketDialogClickListener(OnRedPacketDialogClickListener listener) {
        this.mListener = listener;
    }
}
