package im.bclpbkiauv.ui.wallet;

import im.bclpbkiauv.messenger.BaseController;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletAccountInfo;

public class WalletController extends BaseController {
    private static volatile WalletController[] Instance = new WalletController[3];
    private WalletAccountInfo accountInfo;
    private final Object sync = new Object();

    public static WalletController getInstance(int num) {
        WalletController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (WalletController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    WalletController[] walletControllerArr = Instance;
                    WalletController walletController = new WalletController(num);
                    localInstance = walletController;
                    walletControllerArr[num] = walletController;
                }
            }
        }
        return localInstance;
    }

    public WalletController(int num) {
        super(num);
    }

    public void cleanup() {
        this.accountInfo = null;
    }

    public void setAccountInfo(WalletAccountInfo newAccountInfo) {
        synchronized (this.sync) {
            this.accountInfo = newAccountInfo;
        }
    }

    public WalletAccountInfo getAccountInfo() {
        WalletAccountInfo walletAccountInfo;
        synchronized (this.sync) {
            walletAccountInfo = this.accountInfo;
        }
        return walletAccountInfo;
    }
}
