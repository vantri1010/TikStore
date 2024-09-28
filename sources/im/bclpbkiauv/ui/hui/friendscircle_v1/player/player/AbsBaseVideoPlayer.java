package im.bclpbkiauv.ui.hui.friendscircle_v1.player.player;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.IVideoPlayer;

public abstract class AbsBaseVideoPlayer implements IVideoPlayer, TextureView.SurfaceTextureListener {
    protected boolean blnLoop = true;
    protected boolean mEnableLog;
    protected IVideoPlayer.PlayCallback mPlayCallback;
    protected int mState = 0;
    protected SurfaceTexture mSurfaceTexture;
    protected TextureView mTextureView;
    protected String mUrl;
    protected int miVideoHeight;
    protected int miVideoWidth;

    /* access modifiers changed from: protected */
    public abstract void prepare();

    public int getMiVideoWidth() {
        return this.miVideoWidth;
    }

    public int getMiVideoHeight() {
        return this.miVideoHeight;
    }

    public void setLoopPlay(boolean blnLoop2) {
        this.blnLoop = blnLoop2;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (this.mEnableLog) {
            Log.i("ListVideoPlayer", "AbsBaseVideoPlayer onSurfaceTextureAvailable");
        }
        if (this.mSurfaceTexture == null && (getPlayerState() == 0 || getPlayerState() == 1)) {
            prepare();
        }
        this.mSurfaceTexture = surface;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (this.mEnableLog) {
            Log.i("ListVideoPlayer", "AbsBaseVideoPlayer onSurfaceTextureSizeChanged");
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (!this.mEnableLog) {
            return false;
        }
        Log.i("ListVideoPlayer", "AbsBaseVideoPlayer onSurfaceTextureDestroyed");
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (this.mEnableLog) {
            Log.i("ListVideoPlayer", "AbsBaseVideoPlayer onSurfaceTextureUpdated");
        }
    }

    public boolean isPlaying() {
        if (this.mEnableLog) {
            Log.i("ListVideoPlayer", "AbsBaseVideoPlayer isPlaying");
        }
        return (getPlayerState() == 2 || getPlayerState() == 3) && getCurrentPosition() < getDuration();
    }

    public void setPlayCallback(IVideoPlayer.PlayCallback playCallback) {
        if (this.mEnableLog) {
            Log.i("ListVideoPlayer", "AbsBaseVideoPlayer setPlayCallback");
        }
        this.mPlayCallback = playCallback;
    }

    public void setEnableLog(boolean enableLog) {
        this.mEnableLog = enableLog;
    }

    public void setTextureView(TextureView textureView) {
        if (this.mEnableLog) {
            Log.i("ListVideoPlayer", "AbsBaseVideoPlayer setTextureView");
        }
        TextureView textureView2 = this.mTextureView;
        if (textureView2 != null) {
            textureView2.setSurfaceTextureListener((TextureView.SurfaceTextureListener) null);
        }
        this.mSurfaceTexture = null;
        this.mTextureView = textureView;
        if (textureView != null) {
            textureView.setSurfaceTextureListener(this);
        }
    }
}
