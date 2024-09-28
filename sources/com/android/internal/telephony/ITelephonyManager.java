package com.android.internal.telephony;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.telephony.NeighboringCellInfo;
import java.util.List;

public interface ITelephonyManager extends IInterface {
    void answerRingingCall() throws RemoteException;

    void call(String str) throws RemoteException;

    void cancelMissedCallsNotification() throws RemoteException;

    void dial(String str) throws RemoteException;

    int disableApnType(String str) throws RemoteException;

    boolean disableDataConnectivity() throws RemoteException;

    void disableLocationUpdates() throws RemoteException;

    int enableApnType(String str) throws RemoteException;

    boolean enableDataConnectivity() throws RemoteException;

    void enableLocationUpdates() throws RemoteException;

    boolean endCall() throws RemoteException;

    int getActivePhoneType() throws RemoteException;

    int getCallState() throws RemoteException;

    int getCdmaEriIconIndex() throws RemoteException;

    int getCdmaEriIconMode() throws RemoteException;

    String getCdmaEriText() throws RemoteException;

    boolean getCdmaNeedsProvisioning() throws RemoteException;

    Bundle getCellLocation() throws RemoteException;

    int getDataActivity() throws RemoteException;

    int getDataState() throws RemoteException;

    List<NeighboringCellInfo> getNeighboringCellInfo() throws RemoteException;

    int getNetworkType() throws RemoteException;

    int getVoiceMessageCount() throws RemoteException;

    boolean handlePinMmi(String str) throws RemoteException;

    boolean hasIccCard() throws RemoteException;

    boolean isDataConnectivityPossible() throws RemoteException;

    boolean isIdle() throws RemoteException;

    boolean isOffhook() throws RemoteException;

    boolean isRadioOn() throws RemoteException;

    boolean isRinging() throws RemoteException;

    boolean isSimPinEnabled() throws RemoteException;

    boolean setRadio(boolean z) throws RemoteException;

    boolean showCallScreen() throws RemoteException;

    boolean showCallScreenWithDialpad(boolean z) throws RemoteException;

    void silenceRinger() throws RemoteException;

    boolean supplyPin(String str) throws RemoteException;

    void toggleRadioOnOff() throws RemoteException;

    void updateServiceLocation() throws RemoteException;

    public static class Default implements ITelephonyManager {
        public void dial(String number) throws RemoteException {
        }

        public void call(String number) throws RemoteException {
        }

        public boolean showCallScreen() throws RemoteException {
            return false;
        }

        public boolean showCallScreenWithDialpad(boolean showDialpad) throws RemoteException {
            return false;
        }

        public boolean endCall() throws RemoteException {
            return false;
        }

        public void answerRingingCall() throws RemoteException {
        }

        public void silenceRinger() throws RemoteException {
        }

        public boolean isOffhook() throws RemoteException {
            return false;
        }

        public boolean isRinging() throws RemoteException {
            return false;
        }

        public boolean isIdle() throws RemoteException {
            return false;
        }

        public boolean isRadioOn() throws RemoteException {
            return false;
        }

        public boolean isSimPinEnabled() throws RemoteException {
            return false;
        }

        public void cancelMissedCallsNotification() throws RemoteException {
        }

        public boolean supplyPin(String pin) throws RemoteException {
            return false;
        }

        public boolean handlePinMmi(String dialString) throws RemoteException {
            return false;
        }

        public void toggleRadioOnOff() throws RemoteException {
        }

        public boolean setRadio(boolean turnOn) throws RemoteException {
            return false;
        }

        public void updateServiceLocation() throws RemoteException {
        }

        public void enableLocationUpdates() throws RemoteException {
        }

        public void disableLocationUpdates() throws RemoteException {
        }

        public int enableApnType(String type) throws RemoteException {
            return 0;
        }

        public int disableApnType(String type) throws RemoteException {
            return 0;
        }

        public boolean enableDataConnectivity() throws RemoteException {
            return false;
        }

        public boolean disableDataConnectivity() throws RemoteException {
            return false;
        }

        public boolean isDataConnectivityPossible() throws RemoteException {
            return false;
        }

        public Bundle getCellLocation() throws RemoteException {
            return null;
        }

        public List<NeighboringCellInfo> getNeighboringCellInfo() throws RemoteException {
            return null;
        }

        public int getCallState() throws RemoteException {
            return 0;
        }

        public int getDataActivity() throws RemoteException {
            return 0;
        }

        public int getDataState() throws RemoteException {
            return 0;
        }

        public int getActivePhoneType() throws RemoteException {
            return 0;
        }

        public int getCdmaEriIconIndex() throws RemoteException {
            return 0;
        }

        public int getCdmaEriIconMode() throws RemoteException {
            return 0;
        }

        public String getCdmaEriText() throws RemoteException {
            return null;
        }

        public boolean getCdmaNeedsProvisioning() throws RemoteException {
            return false;
        }

        public int getVoiceMessageCount() throws RemoteException {
            return 0;
        }

        public int getNetworkType() throws RemoteException {
            return 0;
        }

        public boolean hasIccCard() throws RemoteException {
            return false;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements ITelephonyManager {
        private static final String DESCRIPTOR = "com.android.internal.telephony.ITelephonyManager";
        static final int TRANSACTION_answerRingingCall = 6;
        static final int TRANSACTION_call = 2;
        static final int TRANSACTION_cancelMissedCallsNotification = 13;
        static final int TRANSACTION_dial = 1;
        static final int TRANSACTION_disableApnType = 22;
        static final int TRANSACTION_disableDataConnectivity = 24;
        static final int TRANSACTION_disableLocationUpdates = 20;
        static final int TRANSACTION_enableApnType = 21;
        static final int TRANSACTION_enableDataConnectivity = 23;
        static final int TRANSACTION_enableLocationUpdates = 19;
        static final int TRANSACTION_endCall = 5;
        static final int TRANSACTION_getActivePhoneType = 31;
        static final int TRANSACTION_getCallState = 28;
        static final int TRANSACTION_getCdmaEriIconIndex = 32;
        static final int TRANSACTION_getCdmaEriIconMode = 33;
        static final int TRANSACTION_getCdmaEriText = 34;
        static final int TRANSACTION_getCdmaNeedsProvisioning = 35;
        static final int TRANSACTION_getCellLocation = 26;
        static final int TRANSACTION_getDataActivity = 29;
        static final int TRANSACTION_getDataState = 30;
        static final int TRANSACTION_getNeighboringCellInfo = 27;
        static final int TRANSACTION_getNetworkType = 37;
        static final int TRANSACTION_getVoiceMessageCount = 36;
        static final int TRANSACTION_handlePinMmi = 15;
        static final int TRANSACTION_hasIccCard = 38;
        static final int TRANSACTION_isDataConnectivityPossible = 25;
        static final int TRANSACTION_isIdle = 10;
        static final int TRANSACTION_isOffhook = 8;
        static final int TRANSACTION_isRadioOn = 11;
        static final int TRANSACTION_isRinging = 9;
        static final int TRANSACTION_isSimPinEnabled = 12;
        static final int TRANSACTION_setRadio = 17;
        static final int TRANSACTION_showCallScreen = 3;
        static final int TRANSACTION_showCallScreenWithDialpad = 4;
        static final int TRANSACTION_silenceRinger = 7;
        static final int TRANSACTION_supplyPin = 14;
        static final int TRANSACTION_toggleRadioOnOff = 16;
        static final int TRANSACTION_updateServiceLocation = 18;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITelephonyManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ITelephonyManager)) {
                return new Proxy(obj);
            }
            return (ITelephonyManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code != 1598968902) {
                boolean _arg0 = false;
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        dial(data.readString());
                        reply.writeNoException();
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        call(data.readString());
                        reply.writeNoException();
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result = showCallScreen();
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = true;
                        }
                        boolean _result2 = showCallScreenWithDialpad(_arg0);
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result3 = endCall();
                        reply.writeNoException();
                        reply.writeInt(_result3);
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        answerRingingCall();
                        reply.writeNoException();
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        silenceRinger();
                        reply.writeNoException();
                        return true;
                    case 8:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result4 = isOffhook();
                        reply.writeNoException();
                        reply.writeInt(_result4);
                        return true;
                    case 9:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result5 = isRinging();
                        reply.writeNoException();
                        reply.writeInt(_result5);
                        return true;
                    case 10:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result6 = isIdle();
                        reply.writeNoException();
                        reply.writeInt(_result6);
                        return true;
                    case 11:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result7 = isRadioOn();
                        reply.writeNoException();
                        reply.writeInt(_result7);
                        return true;
                    case 12:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result8 = isSimPinEnabled();
                        reply.writeNoException();
                        reply.writeInt(_result8);
                        return true;
                    case 13:
                        data.enforceInterface(DESCRIPTOR);
                        cancelMissedCallsNotification();
                        reply.writeNoException();
                        return true;
                    case 14:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result9 = supplyPin(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result9);
                        return true;
                    case 15:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result10 = handlePinMmi(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result10);
                        return true;
                    case 16:
                        data.enforceInterface(DESCRIPTOR);
                        toggleRadioOnOff();
                        reply.writeNoException();
                        return true;
                    case 17:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = true;
                        }
                        boolean _result11 = setRadio(_arg0);
                        reply.writeNoException();
                        reply.writeInt(_result11);
                        return true;
                    case 18:
                        data.enforceInterface(DESCRIPTOR);
                        updateServiceLocation();
                        reply.writeNoException();
                        return true;
                    case 19:
                        data.enforceInterface(DESCRIPTOR);
                        enableLocationUpdates();
                        reply.writeNoException();
                        return true;
                    case 20:
                        data.enforceInterface(DESCRIPTOR);
                        disableLocationUpdates();
                        reply.writeNoException();
                        return true;
                    case 21:
                        data.enforceInterface(DESCRIPTOR);
                        int _result12 = enableApnType(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result12);
                        return true;
                    case 22:
                        data.enforceInterface(DESCRIPTOR);
                        int _result13 = disableApnType(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result13);
                        return true;
                    case 23:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result14 = enableDataConnectivity();
                        reply.writeNoException();
                        reply.writeInt(_result14);
                        return true;
                    case 24:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result15 = disableDataConnectivity();
                        reply.writeNoException();
                        reply.writeInt(_result15);
                        return true;
                    case 25:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result16 = isDataConnectivityPossible();
                        reply.writeNoException();
                        reply.writeInt(_result16);
                        return true;
                    case 26:
                        data.enforceInterface(DESCRIPTOR);
                        Bundle _result17 = getCellLocation();
                        reply.writeNoException();
                        if (_result17 != null) {
                            reply.writeInt(1);
                            _result17.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 27:
                        data.enforceInterface(DESCRIPTOR);
                        List<NeighboringCellInfo> _result18 = getNeighboringCellInfo();
                        reply.writeNoException();
                        reply.writeTypedList(_result18);
                        return true;
                    case 28:
                        data.enforceInterface(DESCRIPTOR);
                        int _result19 = getCallState();
                        reply.writeNoException();
                        reply.writeInt(_result19);
                        return true;
                    case 29:
                        data.enforceInterface(DESCRIPTOR);
                        int _result20 = getDataActivity();
                        reply.writeNoException();
                        reply.writeInt(_result20);
                        return true;
                    case 30:
                        data.enforceInterface(DESCRIPTOR);
                        int _result21 = getDataState();
                        reply.writeNoException();
                        reply.writeInt(_result21);
                        return true;
                    case 31:
                        data.enforceInterface(DESCRIPTOR);
                        int _result22 = getActivePhoneType();
                        reply.writeNoException();
                        reply.writeInt(_result22);
                        return true;
                    case 32:
                        data.enforceInterface(DESCRIPTOR);
                        int _result23 = getCdmaEriIconIndex();
                        reply.writeNoException();
                        reply.writeInt(_result23);
                        return true;
                    case 33:
                        data.enforceInterface(DESCRIPTOR);
                        int _result24 = getCdmaEriIconMode();
                        reply.writeNoException();
                        reply.writeInt(_result24);
                        return true;
                    case 34:
                        data.enforceInterface(DESCRIPTOR);
                        String _result25 = getCdmaEriText();
                        reply.writeNoException();
                        reply.writeString(_result25);
                        return true;
                    case 35:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result26 = getCdmaNeedsProvisioning();
                        reply.writeNoException();
                        reply.writeInt(_result26);
                        return true;
                    case 36:
                        data.enforceInterface(DESCRIPTOR);
                        int _result27 = getVoiceMessageCount();
                        reply.writeNoException();
                        reply.writeInt(_result27);
                        return true;
                    case 37:
                        data.enforceInterface(DESCRIPTOR);
                        int _result28 = getNetworkType();
                        reply.writeNoException();
                        reply.writeInt(_result28);
                        return true;
                    case 38:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result29 = hasIccCard();
                        reply.writeNoException();
                        reply.writeInt(_result29);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ITelephonyManager {
            public static ITelephonyManager sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void dial(String number) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(number);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().dial(number);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void call(String number) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(number);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().call(number);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean showCallScreen() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(3, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().showCallScreen();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean showCallScreenWithDialpad(boolean showDialpad) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = true;
                    _data.writeInt(showDialpad ? 1 : 0);
                    if (!this.mRemote.transact(4, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().showCallScreenWithDialpad(showDialpad);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean endCall() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(5, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().endCall();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void answerRingingCall() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().answerRingingCall();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void silenceRinger() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(7, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().silenceRinger();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isOffhook() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(8, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isOffhook();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isRinging() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(9, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isRinging();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isIdle() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(10, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isIdle();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isRadioOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(11, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isRadioOn();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isSimPinEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(12, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isSimPinEnabled();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void cancelMissedCallsNotification() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(13, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().cancelMissedCallsNotification();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean supplyPin(String pin) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pin);
                    boolean z = false;
                    if (!this.mRemote.transact(14, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().supplyPin(pin);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean handlePinMmi(String dialString) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(dialString);
                    boolean z = false;
                    if (!this.mRemote.transact(15, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().handlePinMmi(dialString);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void toggleRadioOnOff() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(16, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().toggleRadioOnOff();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setRadio(boolean turnOn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = true;
                    _data.writeInt(turnOn ? 1 : 0);
                    if (!this.mRemote.transact(17, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setRadio(turnOn);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void updateServiceLocation() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(18, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().updateServiceLocation();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void enableLocationUpdates() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(19, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().enableLocationUpdates();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void disableLocationUpdates() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(20, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().disableLocationUpdates();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int enableApnType(String type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    if (!this.mRemote.transact(21, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().enableApnType(type);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int disableApnType(String type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    if (!this.mRemote.transact(22, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().disableApnType(type);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean enableDataConnectivity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(23, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().enableDataConnectivity();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean disableDataConnectivity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(24, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().disableDataConnectivity();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isDataConnectivityPossible() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(25, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isDataConnectivityPossible();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getCellLocation() throws RemoteException {
                Bundle _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(26, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCellLocation();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<NeighboringCellInfo> getNeighboringCellInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(27, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getNeighboringCellInfo();
                    }
                    _reply.readException();
                    List<NeighboringCellInfo> _result = _reply.createTypedArrayList(NeighboringCellInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getCallState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(28, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCallState();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getDataActivity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(29, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDataActivity();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getDataState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(30, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDataState();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getActivePhoneType() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(31, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getActivePhoneType();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getCdmaEriIconIndex() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(32, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCdmaEriIconIndex();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getCdmaEriIconMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(33, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCdmaEriIconMode();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getCdmaEriText() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(34, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCdmaEriText();
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getCdmaNeedsProvisioning() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(35, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCdmaNeedsProvisioning();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getVoiceMessageCount() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(36, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getVoiceMessageCount();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getNetworkType() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(37, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getNetworkType();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean hasIccCard() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(38, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().hasIccCard();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _status = z;
                    _reply.recycle();
                    _data.recycle();
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ITelephonyManager impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static ITelephonyManager getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
