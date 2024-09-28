package org.webrtc.ali;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.webrtc.ali.ThreadUtils;

class YuvConverter {
    private static final FloatBuffer DEVICE_RECTANGLE = GlUtil.createFloatBuffer(new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f});
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 interp_tc;\n\nuniform samplerExternalOES oesTex;\nuniform vec2 xUnit;\nuniform vec4 coeffs;\n\nvoid main() {\n  gl_FragColor.r = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc - 1.5 * xUnit).rgb);\n  gl_FragColor.g = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc - 0.5 * xUnit).rgb);\n  gl_FragColor.b = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc + 0.5 * xUnit).rgb);\n  gl_FragColor.a = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc + 1.5 * xUnit).rgb);\n}\n";
    private static final FloatBuffer TEXTURE_RECTANGLE = GlUtil.createFloatBuffer(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
    private static final String VERTEX_SHADER = "varying vec2 interp_tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\n\nuniform mat4 texMatrix;\n\nvoid main() {\n    gl_Position = in_pos;\n    interp_tc = (texMatrix * in_tc).xy;\n}\n";
    private final int coeffsLoc;
    private boolean released = false;
    private final GlShader shader;
    private final int texMatrixLoc;
    private final GlTextureFrameBuffer textureFrameBuffer;
    private final ThreadUtils.ThreadChecker threadChecker;
    private final int xUnitLoc;

    public YuvConverter() {
        ThreadUtils.ThreadChecker threadChecker2 = new ThreadUtils.ThreadChecker();
        this.threadChecker = threadChecker2;
        threadChecker2.checkIsOnValidThread();
        this.textureFrameBuffer = new GlTextureFrameBuffer(6408);
        GlShader glShader = new GlShader(VERTEX_SHADER, FRAGMENT_SHADER);
        this.shader = glShader;
        glShader.useProgram();
        this.texMatrixLoc = this.shader.getUniformLocation("texMatrix");
        this.xUnitLoc = this.shader.getUniformLocation("xUnit");
        this.coeffsLoc = this.shader.getUniformLocation("coeffs");
        GLES20.glUniform1i(this.shader.getUniformLocation("oesTex"), 0);
        GlUtil.checkNoGLES2Error("Initialize fragment shader uniform values.");
        this.shader.setVertexAttribArray("in_pos", 2, DEVICE_RECTANGLE);
        this.shader.setVertexAttribArray("in_tc", 2, TEXTURE_RECTANGLE);
    }

    public void convert(ByteBuffer buf, int width, int height, int stride, int srcTextureId, float[] transformMatrix) {
        int i = width;
        int i2 = height;
        int i3 = stride;
        this.threadChecker.checkIsOnValidThread();
        if (this.released) {
            float[] fArr = transformMatrix;
            throw new IllegalStateException("YuvConverter.convert called on released object");
        } else if (i3 % 8 != 0) {
            float[] fArr2 = transformMatrix;
            throw new IllegalArgumentException("Invalid stride, must be a multiple of 8");
        } else if (i3 >= i) {
            int y_width = (i + 3) / 4;
            int uv_width = (i + 7) / 8;
            int uv_height = (i2 + 1) / 2;
            int total_height = i2 + uv_height;
            if (buf.capacity() >= i3 * total_height) {
                float[] transformMatrix2 = RendererCommon.multiplyMatrices(transformMatrix, RendererCommon.verticalFlipMatrix());
                int frameBufferWidth = i3 / 4;
                int frameBufferHeight = total_height;
                this.textureFrameBuffer.setSize(frameBufferWidth, frameBufferHeight);
                GLES20.glBindFramebuffer(36160, this.textureFrameBuffer.getFrameBufferId());
                GlUtil.checkNoGLES2Error("glBindFramebuffer");
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(36197, srcTextureId);
                GLES20.glUniformMatrix4fv(this.texMatrixLoc, 1, false, transformMatrix2, 0);
                GLES20.glViewport(0, 0, y_width, i2);
                GLES20.glUniform2f(this.xUnitLoc, transformMatrix2[0] / ((float) i), transformMatrix2[1] / ((float) i));
                int i4 = y_width;
                GLES20.glUniform4f(this.coeffsLoc, 0.299f, 0.587f, 0.114f, 0.0f);
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glViewport(0, i2, uv_width, uv_height);
                GLES20.glUniform2f(this.xUnitLoc, (transformMatrix2[0] * 2.0f) / ((float) i), (transformMatrix2[1] * 2.0f) / ((float) i));
                GLES20.glUniform4f(this.coeffsLoc, -0.169f, -0.331f, 0.499f, 0.5f);
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glViewport(i3 / 8, i2, uv_width, uv_height);
                GLES20.glUniform4f(this.coeffsLoc, 0.499f, -0.418f, -0.0813f, 0.5f);
                GLES20.glDrawArrays(5, 0, 4);
                int i5 = frameBufferHeight;
                int i6 = frameBufferWidth;
                GLES20.glReadPixels(0, 0, frameBufferWidth, frameBufferHeight, 6408, 5121, buf);
                GlUtil.checkNoGLES2Error("YuvConverter.convert");
                GLES20.glBindFramebuffer(36160, 0);
                GLES20.glBindTexture(3553, 0);
                GLES20.glBindTexture(36197, 0);
                return;
            }
            throw new IllegalArgumentException("YuvConverter.convert called with too small buffer");
        } else {
            float[] fArr3 = transformMatrix;
            throw new IllegalArgumentException("Invalid stride, must >= width");
        }
    }

    public void release() {
        this.threadChecker.checkIsOnValidThread();
        this.released = true;
        this.shader.release();
        this.textureFrameBuffer.release();
    }
}
