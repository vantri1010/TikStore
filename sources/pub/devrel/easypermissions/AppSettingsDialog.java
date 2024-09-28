package pub.devrel.easypermissions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.exoplayer2.C;

public class AppSettingsDialog implements Parcelable {
    public static final Parcelable.Creator<AppSettingsDialog> CREATOR = new Parcelable.Creator<AppSettingsDialog>() {
        public AppSettingsDialog createFromParcel(Parcel in) {
            return new AppSettingsDialog(in);
        }

        public AppSettingsDialog[] newArray(int size) {
            return new AppSettingsDialog[size];
        }
    };
    public static final int DEFAULT_SETTINGS_REQ_CODE = 16061;
    static final String EXTRA_APP_SETTINGS = "extra_app_settings";
    private Object mActivityOrFragment;
    private Context mContext;
    private final int mIntentFlags;
    private final String mNegativeButtonText;
    private final String mPositiveButtonText;
    private final String mRationale;
    private final int mRequestCode;
    private final int mThemeResId;
    private final String mTitle;

    private AppSettingsDialog(Parcel in) {
        this.mThemeResId = in.readInt();
        this.mRationale = in.readString();
        this.mTitle = in.readString();
        this.mPositiveButtonText = in.readString();
        this.mNegativeButtonText = in.readString();
        this.mRequestCode = in.readInt();
        this.mIntentFlags = in.readInt();
    }

    private AppSettingsDialog(Object activityOrFragment, int themeResId, String rationale, String title, String positiveButtonText, String negativeButtonText, int requestCode, int intentFlags) {
        setActivityOrFragment(activityOrFragment);
        this.mThemeResId = themeResId;
        this.mRationale = rationale;
        this.mTitle = title;
        this.mPositiveButtonText = positiveButtonText;
        this.mNegativeButtonText = negativeButtonText;
        this.mRequestCode = requestCode;
        this.mIntentFlags = intentFlags;
    }

    static AppSettingsDialog fromIntent(Intent intent, Activity activity) {
        AppSettingsDialog dialog = (AppSettingsDialog) intent.getParcelableExtra(EXTRA_APP_SETTINGS);
        dialog.setActivityOrFragment(activity);
        return dialog;
    }

    private void setActivityOrFragment(Object activityOrFragment) {
        this.mActivityOrFragment = activityOrFragment;
        if (activityOrFragment instanceof Activity) {
            this.mContext = (Activity) activityOrFragment;
        } else if (activityOrFragment instanceof Fragment) {
            this.mContext = ((Fragment) activityOrFragment).getContext();
        } else {
            throw new IllegalStateException("Unknown object: " + activityOrFragment);
        }
    }

    private void startForResult(Intent intent) {
        Object obj = this.mActivityOrFragment;
        if (obj instanceof Activity) {
            ((Activity) obj).startActivityForResult(intent, this.mRequestCode);
        } else if (obj instanceof Fragment) {
            ((Fragment) obj).startActivityForResult(intent, this.mRequestCode);
        }
    }

    public void show() {
        startForResult(AppSettingsDialogHolderActivity.createShowDialogIntent(this.mContext, this));
    }

    /* access modifiers changed from: package-private */
    public AlertDialog showDialog(DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder;
        int i = this.mThemeResId;
        if (i > 0) {
            builder = new AlertDialog.Builder(this.mContext, i);
        } else {
            builder = new AlertDialog.Builder(this.mContext);
        }
        return builder.setCancelable(false).setTitle((CharSequence) this.mTitle).setMessage((CharSequence) this.mRationale).setPositiveButton((CharSequence) this.mPositiveButtonText, positiveListener).setNegativeButton((CharSequence) this.mNegativeButtonText, negativeListener).show();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mThemeResId);
        dest.writeString(this.mRationale);
        dest.writeString(this.mTitle);
        dest.writeString(this.mPositiveButtonText);
        dest.writeString(this.mNegativeButtonText);
        dest.writeInt(this.mRequestCode);
        dest.writeInt(this.mIntentFlags);
    }

    /* access modifiers changed from: package-private */
    public int getIntentFlags() {
        return this.mIntentFlags;
    }

    public static class Builder {
        private final Object mActivityOrFragment;
        private final Context mContext;
        private String mNegativeButtonText;
        private boolean mOpenInNewTask = false;
        private String mPositiveButtonText;
        private String mRationale;
        private int mRequestCode = -1;
        private int mThemeResId = -1;
        private String mTitle;

        public Builder(Activity activity) {
            this.mActivityOrFragment = activity;
            this.mContext = activity;
        }

        public Builder(Fragment fragment) {
            this.mActivityOrFragment = fragment;
            this.mContext = fragment.getContext();
        }

        public Builder setThemeResId(int themeResId) {
            this.mThemeResId = themeResId;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setTitle(int title) {
            this.mTitle = this.mContext.getString(title);
            return this;
        }

        public Builder setRationale(String rationale) {
            this.mRationale = rationale;
            return this;
        }

        public Builder setRationale(int rationale) {
            this.mRationale = this.mContext.getString(rationale);
            return this;
        }

        public Builder setPositiveButton(String text) {
            this.mPositiveButtonText = text;
            return this;
        }

        public Builder setPositiveButton(int textId) {
            this.mPositiveButtonText = this.mContext.getString(textId);
            return this;
        }

        public Builder setNegativeButton(String text) {
            this.mNegativeButtonText = text;
            return this;
        }

        public Builder setNegativeButton(int textId) {
            this.mNegativeButtonText = this.mContext.getString(textId);
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.mRequestCode = requestCode;
            return this;
        }

        public Builder setOpenInNewTask(boolean openInNewTask) {
            this.mOpenInNewTask = openInNewTask;
            return this;
        }

        public AppSettingsDialog build() {
            this.mRationale = TextUtils.isEmpty(this.mRationale) ? this.mContext.getString(R.string.rationale_ask_again) : this.mRationale;
            this.mTitle = TextUtils.isEmpty(this.mTitle) ? this.mContext.getString(R.string.title_settings_dialog) : this.mTitle;
            this.mPositiveButtonText = TextUtils.isEmpty(this.mPositiveButtonText) ? this.mContext.getString(17039370) : this.mPositiveButtonText;
            this.mNegativeButtonText = TextUtils.isEmpty(this.mNegativeButtonText) ? this.mContext.getString(17039360) : this.mNegativeButtonText;
            int i = this.mRequestCode;
            if (i <= 0) {
                i = AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE;
            }
            this.mRequestCode = i;
            int intentFlags = 0;
            if (this.mOpenInNewTask) {
                intentFlags = 0 | C.ENCODING_PCM_MU_LAW;
            }
            return new AppSettingsDialog(this.mActivityOrFragment, this.mThemeResId, this.mRationale, this.mTitle, this.mPositiveButtonText, this.mNegativeButtonText, this.mRequestCode, intentFlags);
        }
    }
}
