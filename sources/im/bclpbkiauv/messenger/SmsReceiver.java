package im.bclpbkiauv.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.king.zxing.util.LogUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String message = "";
            try {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                String hash = preferences.getString("sms_hash", (String) null);
                if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                    if (AndroidUtilities.isWaitingForSms()) {
                        message = (String) intent.getExtras().get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    } else {
                        return;
                    }
                }
                if (!TextUtils.isEmpty(message)) {
                    Matcher matcher = Pattern.compile("[0-9\\-]+").matcher(message);
                    if (matcher.find()) {
                        String code = matcher.group(0).replace("-", "");
                        if (code.length() >= 3) {
                            if (!(preferences == null || hash == null)) {
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("sms_hash_code", hash + LogUtils.VERTICAL + code).commit();
                            }
                            AndroidUtilities.runOnUIThread(new Runnable(code) {
                                private final /* synthetic */ String f$0;

                                {
                                    this.f$0 = r1;
                                }

                                public final void run() {
                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, this.f$0);
                                }
                            });
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }
}
