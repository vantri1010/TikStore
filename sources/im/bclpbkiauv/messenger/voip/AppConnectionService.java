package im.bclpbkiauv.messenger.voip;

import android.os.Bundle;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;

public class AppConnectionService extends ConnectionService {
    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.w("ConnectionService created");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.w("ConnectionService destroyed");
        }
    }

    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("onCreateIncomingConnection ");
        }
        Bundle extras = request.getExtras();
        if (extras.getInt("call_type") == 1) {
            VoIPService svc = VoIPService.getSharedInstance();
            if (svc != null && !svc.isOutgoing()) {
                return svc.getConnectionAndStartCall();
            }
            return null;
        }
        extras.getInt("call_type");
        return null;
    }

    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("onCreateIncomingConnectionFailed ");
        }
        if (VoIPBaseService.getSharedInstance() != null) {
            VoIPBaseService.getSharedInstance().callFailedFromConnectionService();
        }
    }

    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("onCreateOutgoingConnectionFailed ");
        }
        if (VoIPBaseService.getSharedInstance() != null) {
            VoIPBaseService.getSharedInstance().callFailedFromConnectionService();
        }
    }

    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("onCreateOutgoingConnection ");
        }
        Bundle extras = request.getExtras();
        if (extras.getInt("call_type") == 1) {
            VoIPService svc = VoIPService.getSharedInstance();
            if (svc == null) {
                return null;
            }
            return svc.getConnectionAndStartCall();
        }
        extras.getInt("call_type");
        return null;
    }
}
