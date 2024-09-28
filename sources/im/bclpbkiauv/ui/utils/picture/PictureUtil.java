package im.bclpbkiauv.ui.utils.picture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.DrawableUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.utils.picture.PictureUtil;

public class PictureUtil {

    public interface CallBack {
        void loadPictureResult(boolean z, byte[] bArr, Bitmap bitmap, String str);
    }

    private static ConnectionsManager getConnectionsManager() {
        return ConnectionsManager.getInstance(UserConfig.selectedAccount);
    }

    public static void loadPictureByFileId(int classGuid, long fileId, long fileHash, int fileSize, CallBack callBack) {
        int i = fileSize;
        byte[] downloadBytes = new byte[i];
        int chunkSize = i > 1048576 ? 131072 : 32768;
        int i2 = 0;
        int count = (i / chunkSize) + (i % chunkSize != 0 ? 1 : 0);
        int index = 0;
        while (index < count) {
            TLRPC.TL_upload_getFile req = new TLRPC.TL_upload_getFile();
            req.location = new TLRPC.TL_inputDocumentFileLocation();
            req.location.file_reference = new byte[i2];
            req.location.thumb_size = "";
            req.location.access_hash = fileHash;
            req.location.id = fileId;
            req.offset = index * chunkSize;
            if (index == count - 1) {
                req.limit = i - req.offset;
            } else {
                req.limit = chunkSize;
            }
            ConnectionsManager connectionsManager = getConnectionsManager();
            $$Lambda$PictureUtil$mjgPsVPrhL16ebSB3tp9WTRgoI r10 = r1;
            ConnectionsManager connectionsManager2 = getConnectionsManager();
            $$Lambda$PictureUtil$mjgPsVPrhL16ebSB3tp9WTRgoI r1 = new RequestDelegate(downloadBytes, index, chunkSize, callBack, count) {
                private final /* synthetic */ byte[] f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ PictureUtil.CallBack f$4;
                private final /* synthetic */ int f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PictureUtil.lambda$loadPictureByFileId$3(TLRPC.TL_upload_getFile.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
                }
            };
            connectionsManager.bindRequestToGuid(connectionsManager2.sendRequest(req, r10), classGuid);
            index++;
            i = fileSize;
            downloadBytes = downloadBytes;
            i2 = 0;
        }
    }

    static /* synthetic */ void lambda$loadPictureByFileId$3(TLRPC.TL_upload_getFile req, byte[] downloadBytes, int finalIndex, int chunkSize, CallBack callBack, int count, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            if (response instanceof TLRPC.TL_upload_file) {
                TLRPC.TL_upload_file upload_file = (TLRPC.TL_upload_file) response;
                if (upload_file.bytes != null) {
                    try {
                        byte[] array = upload_file.bytes.readData(req.limit, true);
                        System.arraycopy(array, 0, downloadBytes, finalIndex * chunkSize, array.length);
                    } catch (Exception e) {
                        ToastUtils.show((int) R.string.LoadPictureFailed);
                        FileLog.e((Throwable) e);
                        AndroidUtilities.runOnUIThread(new Runnable(e) {
                            private final /* synthetic */ Exception f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PictureUtil.lambda$null$0(PictureUtil.CallBack.this, this.f$1);
                            }
                        });
                    }
                }
            }
            if (finalIndex == count - 1) {
                AndroidUtilities.runOnUIThread(new Runnable(downloadBytes) {
                    private final /* synthetic */ byte[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        PictureUtil.lambda$null$1(PictureUtil.CallBack.this, this.f$1);
                    }
                });
            }
        } else if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable(error) {
                private final /* synthetic */ TLRPC.TL_error f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PictureUtil.lambda$null$2(PictureUtil.CallBack.this, this.f$1);
                }
            });
            ToastUtils.show((int) R.string.LoadPictureFailed);
        }
    }

    static /* synthetic */ void lambda$null$0(CallBack callBack, Exception e) {
        if (callBack != null) {
            callBack.loadPictureResult(false, (byte[]) null, (Bitmap) null, e.getMessage());
        }
    }

    static /* synthetic */ void lambda$null$1(CallBack callBack, byte[] downloadBytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (callBack != null) {
            callBack.loadPictureResult(true, downloadBytes, DrawableUtils.getPicFromBytes(downloadBytes, options), (String) null);
        }
    }

    static /* synthetic */ void lambda$null$2(CallBack callBack, TLRPC.TL_error error) {
        if (callBack != null) {
            callBack.loadPictureResult(false, (byte[]) null, (Bitmap) null, error.text);
        }
    }
}
