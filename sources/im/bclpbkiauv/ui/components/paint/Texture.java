package im.bclpbkiauv.ui.components.paint;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import im.bclpbkiauv.ui.components.Size;
import java.nio.Buffer;

public class Texture {
    private Bitmap bitmap;
    private int texture;

    public Texture(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public void cleanResources(boolean recycleBitmap) {
        int i = this.texture;
        if (i != 0) {
            GLES20.glDeleteTextures(1, new int[]{i}, 0);
            this.texture = 0;
            if (recycleBitmap) {
                this.bitmap.recycle();
            }
        }
    }

    private boolean isPOT(int x) {
        return ((x + -1) & x) == 0;
    }

    public int texture() {
        int i = this.texture;
        if (i != 0) {
            return i;
        }
        if (this.bitmap.isRecycled()) {
            return 0;
        }
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int i2 = textures[0];
        this.texture = i2;
        GLES20.glBindTexture(3553, i2);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        int i3 = 9729;
        GLES20.glTexParameteri(3553, 10240, 9729);
        if (0 != 0) {
            i3 = 9987;
        }
        GLES20.glTexParameteri(3553, 10241, i3);
        GLUtils.texImage2D(3553, 0, this.bitmap, 0);
        if (0 != 0) {
            GLES20.glGenerateMipmap(3553);
        }
        Utils.HasGLError();
        return this.texture;
    }

    public static int generateTexture(Size size) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int texture2 = textures[0];
        GLES20.glBindTexture(3553, texture2);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexImage2D(3553, 0, 6408, (int) size.width, (int) size.height, 0, 6408, 5121, (Buffer) null);
        return texture2;
    }
}
