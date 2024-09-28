package im.bclpbkiauv.tgnet;

public interface FileLoadOperationDelegate {
    void onFailed(int i);

    void onFinished(String str);

    void onProgressChanged(float f);
}
