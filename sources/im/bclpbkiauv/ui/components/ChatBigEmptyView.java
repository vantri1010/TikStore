package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import java.util.ArrayList;

public class ChatBigEmptyView extends LinearLayout {
    public static final int EMPTY_VIEW_TYPE_GROUP = 1;
    public static final int EMPTY_VIEW_TYPE_SAVED = 2;
    public static final int EMPTY_VIEW_TYPE_SECRET = 0;
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private TextView statusTextView;
    private ArrayList<TextView> textViews = new ArrayList<>();

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatBigEmptyView(Context context, int type) {
        super(context);
        Context context2 = context;
        int i = type;
        setBackgroundResource(R.drawable.system);
        getBackground().setColorFilter(Theme.colorFilter);
        setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        setOrientation(1);
        if (i == 0) {
            TextView textView = new TextView(context2);
            this.statusTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else if (i == 1) {
            TextView textView2 = new TextView(context2);
            this.statusTextView = textView2;
            textView2.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else {
            ImageView imageView = new ImageView(context2);
            imageView.setImageResource(R.drawable.cloud_big);
            addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
        }
        TextView textView3 = new TextView(context2);
        if (i == 0) {
            textView3.setText(LocaleController.getString("EncryptedDescriptionTitle", R.string.EncryptedDescriptionTitle));
            textView3.setTextSize(1, 15.0f);
        } else if (i == 1) {
            textView3.setText(LocaleController.getString("GroupEmptyTitle2", R.string.GroupEmptyTitle2));
            textView3.setTextSize(1, 15.0f);
        } else {
            textView3.setText(LocaleController.getString("ChatYourSelfTitle", R.string.ChatYourSelfTitle));
            textView3.setTextSize(1, 16.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView3.setGravity(1);
        }
        textView3.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
        this.textViews.add(textView3);
        textView3.setMaxWidth(AndroidUtilities.dp(260.0f));
        addView(textView3, LayoutHelper.createLinear(-2, -2, (i != 2 ? LocaleController.isRTL ? 5 : 3 : 1) | 48, 0, 8, 0, i != 2 ? 0 : 8));
        for (int a = 0; a < 4; a++) {
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            ImageView imageView2 = new ImageView(context2);
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_serviceText), PorterDuff.Mode.MULTIPLY));
            if (i == 0) {
                imageView2.setImageResource(R.drawable.ic_lock_white);
            } else if (i == 2) {
                imageView2.setImageResource(R.drawable.list_circle);
            } else {
                imageView2.setImageResource(R.drawable.groups_overview_check);
            }
            this.imageViews.add(imageView2);
            TextView textView4 = new TextView(context2);
            textView4.setTextSize(1, 15.0f);
            textView4.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
            this.textViews.add(textView4);
            textView4.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView4.setMaxWidth(AndroidUtilities.dp(260.0f));
            if (a != 0) {
                if (a != 1) {
                    if (a != 2) {
                        if (a == 3) {
                            if (i == 0) {
                                textView4.setText(LocaleController.getString("EncryptedDescription4", R.string.EncryptedDescription4));
                            } else if (i == 2) {
                                textView4.setText(LocaleController.getString("ChatYourSelfDescription4", R.string.ChatYourSelfDescription4));
                            } else {
                                textView4.setText(LocaleController.getString("GroupDescription4", R.string.GroupDescription4));
                            }
                        }
                    } else if (i == 0) {
                        textView4.setText(LocaleController.getString("EncryptedDescription3", R.string.EncryptedDescription3));
                    } else if (i == 2) {
                        textView4.setText(LocaleController.getString("ChatYourSelfDescription3", R.string.ChatYourSelfDescription3));
                    } else {
                        textView4.setText(LocaleController.getString("GroupDescription3", R.string.GroupDescription3));
                    }
                } else if (i == 0) {
                    textView4.setText(LocaleController.getString("EncryptedDescription2", R.string.EncryptedDescription2));
                } else if (i == 2) {
                    textView4.setText(LocaleController.getString("ChatYourSelfDescription2", R.string.ChatYourSelfDescription2));
                } else {
                    textView4.setText(LocaleController.getString("GroupDescription2", R.string.GroupDescription2));
                }
            } else if (i == 0) {
                textView4.setText(LocaleController.getString("EncryptedDescription1", R.string.EncryptedDescription1));
            } else if (i == 2) {
                textView4.setText(LocaleController.getString("ChatYourSelfDescription1", R.string.ChatYourSelfDescription1));
            } else {
                textView4.setText(LocaleController.getString("GroupDescription1", R.string.GroupDescription1));
            }
            if (LocaleController.isRTL) {
                linearLayout.addView(textView4, LayoutHelper.createLinear(-2, -2));
                if (i == 0) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                } else if (i == 2) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                }
            } else {
                if (i == 0) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                } else if (i == 2) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                }
                linearLayout.addView(textView4, LayoutHelper.createLinear(-2, -2));
            }
        }
    }

    public void setTextColor(int color) {
        for (int a = 0; a < this.textViews.size(); a++) {
            this.textViews.get(a).setTextColor(color);
        }
        for (int a2 = 0; a2 < this.imageViews.size(); a2++) {
            this.imageViews.get(a2).setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_serviceText), PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setStatusText(CharSequence text) {
        this.statusTextView.setText(text);
    }
}
