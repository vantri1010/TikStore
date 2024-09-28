package im.bclpbkiauv.ui.components.paint;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.opengl.GLES20;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.ui.components.Size;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.UUID;

public class Painting {
    /* access modifiers changed from: private */
    public Path activePath;
    /* access modifiers changed from: private */
    public RectF activeStrokeBounds;
    /* access modifiers changed from: private */
    public Slice backupSlice;
    private Texture bitmapTexture;
    /* access modifiers changed from: private */
    public Brush brush;
    /* access modifiers changed from: private */
    public Texture brushTexture;
    private int[] buffers = new int[1];
    private ByteBuffer dataBuffer;
    /* access modifiers changed from: private */
    public PaintingDelegate delegate;
    private int paintTexture;
    /* access modifiers changed from: private */
    public boolean paused;
    /* access modifiers changed from: private */
    public float[] projection;
    private float[] renderProjection;
    /* access modifiers changed from: private */
    public RenderState renderState = new RenderState();
    private RenderView renderView;
    private int reusableFramebuffer;
    /* access modifiers changed from: private */
    public Map<String, Shader> shaders;
    /* access modifiers changed from: private */
    public Size size;
    private int suppressChangesCounter;
    /* access modifiers changed from: private */
    public ByteBuffer textureBuffer;
    /* access modifiers changed from: private */
    public ByteBuffer vertexBuffer;

    public interface PaintingDelegate {
        void contentChanged(RectF rectF);

        DispatchQueue requestDispatchQueue();

        UndoStore requestUndoStore();

        void strokeCommited();
    }

    public class PaintingData {
        public Bitmap bitmap;
        public ByteBuffer data;

        PaintingData(Bitmap b, ByteBuffer buffer) {
            this.bitmap = b;
            this.data = buffer;
        }
    }

    public Painting(Size sz) {
        this.size = sz;
        this.dataBuffer = ByteBuffer.allocateDirect(((int) sz.width) * ((int) this.size.height) * 4);
        this.projection = GLMatrix.LoadOrtho(0.0f, this.size.width, 0.0f, this.size.height, -1.0f, 1.0f);
        if (this.vertexBuffer == null) {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(32);
            this.vertexBuffer = allocateDirect;
            allocateDirect.order(ByteOrder.nativeOrder());
        }
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(this.size.width);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(this.size.height);
        this.vertexBuffer.putFloat(this.size.width);
        this.vertexBuffer.putFloat(this.size.height);
        this.vertexBuffer.rewind();
        if (this.textureBuffer == null) {
            ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(32);
            this.textureBuffer = allocateDirect2;
            allocateDirect2.order(ByteOrder.nativeOrder());
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.rewind();
        }
    }

    public void setDelegate(PaintingDelegate paintingDelegate) {
        this.delegate = paintingDelegate;
    }

    public void setRenderView(RenderView view) {
        this.renderView = view;
    }

    public Size getSize() {
        return this.size;
    }

    public RectF getBounds() {
        return new RectF(0.0f, 0.0f, this.size.width, this.size.height);
    }

    /* access modifiers changed from: private */
    public boolean isSuppressingChanges() {
        return this.suppressChangesCounter > 0;
    }

    /* access modifiers changed from: private */
    public void beginSuppressingChanges() {
        this.suppressChangesCounter++;
    }

    /* access modifiers changed from: private */
    public void endSuppressingChanges() {
        this.suppressChangesCounter--;
    }

    public void setBitmap(Bitmap bitmap) {
        if (this.bitmapTexture == null) {
            this.bitmapTexture = new Texture(bitmap);
        }
    }

    /* access modifiers changed from: private */
    public void update(RectF bounds, Runnable action) {
        PaintingDelegate paintingDelegate;
        GLES20.glBindFramebuffer(36160, getReusableFramebuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, getTexture(), 0);
        if (GLES20.glCheckFramebufferStatus(36160) == 36053) {
            GLES20.glViewport(0, 0, (int) this.size.width, (int) this.size.height);
            action.run();
        }
        GLES20.glBindFramebuffer(36160, 0);
        if (!isSuppressingChanges() && (paintingDelegate = this.delegate) != null) {
            paintingDelegate.contentChanged(bounds);
        }
    }

    public void paintStroke(final Path path, final boolean clearBuffer, final Runnable action) {
        this.renderView.performInContext(new Runnable() {
            public void run() {
                Path unused = Painting.this.activePath = path;
                RectF bounds = null;
                GLES20.glBindFramebuffer(36160, Painting.this.getReusableFramebuffer());
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, Painting.this.getPaintTexture(), 0);
                Utils.HasGLError();
                if (GLES20.glCheckFramebufferStatus(36160) == 36053) {
                    GLES20.glViewport(0, 0, (int) Painting.this.size.width, (int) Painting.this.size.height);
                    if (clearBuffer) {
                        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                        GLES20.glClear(16384);
                    }
                    if (Painting.this.shaders != null) {
                        Shader shader = (Shader) Painting.this.shaders.get(Painting.this.brush.isLightSaber() ? "brushLight" : "brush");
                        if (shader != null) {
                            GLES20.glUseProgram(shader.program);
                            if (Painting.this.brushTexture == null) {
                                Painting painting = Painting.this;
                                Texture unused2 = painting.brushTexture = new Texture(painting.brush.getStamp());
                            }
                            GLES20.glActiveTexture(33984);
                            GLES20.glBindTexture(3553, Painting.this.brushTexture.texture());
                            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(Painting.this.projection));
                            GLES20.glUniform1i(shader.getUniform("texture"), 0);
                            bounds = Render.RenderPath(path, Painting.this.renderState);
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                GLES20.glBindFramebuffer(36160, 0);
                if (Painting.this.delegate != null) {
                    Painting.this.delegate.contentChanged(bounds);
                }
                if (Painting.this.activeStrokeBounds != null) {
                    Painting.this.activeStrokeBounds.union(bounds);
                } else {
                    RectF unused3 = Painting.this.activeStrokeBounds = bounds;
                }
                Runnable runnable = action;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    public void commitStroke(final int color) {
        this.renderView.performInContext(new Runnable() {
            public void run() {
                Painting painting = Painting.this;
                painting.registerUndo(painting.activeStrokeBounds);
                Painting.this.beginSuppressingChanges();
                Painting.this.update((RectF) null, new Runnable() {
                    public void run() {
                        if (Painting.this.shaders != null) {
                            Shader shader = (Shader) Painting.this.shaders.get(Painting.this.brush.isLightSaber() ? "compositeWithMaskLight" : "compositeWithMask");
                            if (shader != null) {
                                GLES20.glUseProgram(shader.program);
                                GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(Painting.this.projection));
                                GLES20.glUniform1i(shader.getUniform("mask"), 0);
                                Shader.SetColorUniform(shader.getUniform(TtmlNode.ATTR_TTS_COLOR), color);
                                GLES20.glActiveTexture(33984);
                                GLES20.glBindTexture(3553, Painting.this.getPaintTexture());
                                GLES20.glBlendFuncSeparate(770, 771, 770, 1);
                                GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, Painting.this.vertexBuffer);
                                GLES20.glEnableVertexAttribArray(0);
                                GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, Painting.this.textureBuffer);
                                GLES20.glEnableVertexAttribArray(1);
                                GLES20.glDrawArrays(5, 0, 4);
                            }
                        }
                    }
                });
                Painting.this.endSuppressingChanges();
                Painting.this.renderState.reset();
                RectF unused = Painting.this.activeStrokeBounds = null;
                Path unused2 = Painting.this.activePath = null;
            }
        });
    }

    /* access modifiers changed from: private */
    public void registerUndo(RectF rect) {
        if (rect != null && rect.setIntersect(rect, getBounds())) {
            final Slice slice = new Slice(getPaintingData(rect, true).data, rect, this.delegate.requestDispatchQueue());
            this.delegate.requestUndoStore().registerUndo(UUID.randomUUID(), new Runnable() {
                public void run() {
                    Painting.this.restoreSlice(slice);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void restoreSlice(final Slice slice) {
        this.renderView.performInContext(new Runnable() {
            public void run() {
                ByteBuffer buffer = slice.getData();
                GLES20.glBindTexture(3553, Painting.this.getTexture());
                GLES20.glTexSubImage2D(3553, 0, slice.getX(), slice.getY(), slice.getWidth(), slice.getHeight(), 6408, 5121, buffer);
                if (!Painting.this.isSuppressingChanges() && Painting.this.delegate != null) {
                    Painting.this.delegate.contentChanged(slice.getBounds());
                }
                slice.cleanResources();
            }
        });
    }

    public void setRenderProjection(float[] proj) {
        this.renderProjection = proj;
    }

    public void render() {
        if (this.shaders != null) {
            if (this.activePath != null) {
                render(getPaintTexture(), this.activePath.getColor());
            } else {
                renderBlit();
            }
        }
    }

    private void render(int mask, int color) {
        Shader shader = this.shaders.get(this.brush.isLightSaber() ? "blitWithMaskLight" : "blitWithMask");
        if (shader != null) {
            GLES20.glUseProgram(shader.program);
            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.renderProjection));
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glUniform1i(shader.getUniform("mask"), 1);
            Shader.SetColorUniform(shader.getUniform(TtmlNode.ATTR_TTS_COLOR), color);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, mask);
            GLES20.glBlendFunc(1, 771);
            GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(0);
            GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(1);
            GLES20.glDrawArrays(5, 0, 4);
            Utils.HasGLError();
        }
    }

    private void renderBlit() {
        Shader shader = this.shaders.get("blit");
        if (shader != null) {
            GLES20.glUseProgram(shader.program);
            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.renderProjection));
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
            GLES20.glBlendFunc(1, 771);
            GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(0);
            GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(1);
            GLES20.glDrawArrays(5, 0, 4);
            Utils.HasGLError();
        }
    }

    public PaintingData getPaintingData(RectF rect, boolean undo) {
        PaintingData data;
        RectF rectF = rect;
        int minX = (int) rectF.left;
        int minY = (int) rectF.top;
        int width = (int) rect.width();
        int height = (int) rect.height();
        GLES20.glGenFramebuffers(1, this.buffers, 0);
        int framebuffer = this.buffers[0];
        GLES20.glBindFramebuffer(36160, framebuffer);
        GLES20.glGenTextures(1, this.buffers, 0);
        int texture = this.buffers[0];
        GLES20.glBindTexture(3553, texture);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        int texture2 = texture;
        int framebuffer2 = framebuffer;
        GLES20.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, (Buffer) null);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, texture2, 0);
        GLES20.glViewport(0, 0, (int) this.size.width, (int) this.size.height);
        Map<String, Shader> map = this.shaders;
        if (map == null) {
            return null;
        }
        Shader shader = map.get(undo ? "nonPremultipliedBlit" : "blit");
        if (shader == null) {
            return null;
        }
        GLES20.glUseProgram(shader.program);
        Matrix translate = new Matrix();
        translate.preTranslate((float) (-minX), (float) (-minY));
        float[] effective = GLMatrix.LoadGraphicsMatrix(translate);
        GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(GLMatrix.MultiplyMat4f(this.projection, effective)));
        if (undo) {
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
        } else {
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.bitmapTexture.texture());
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
        }
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(16384);
        GLES20.glBlendFunc(1, 771);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glDrawArrays(5, 0, 4);
        this.dataBuffer.limit(width * height * 4);
        float[] fArr = effective;
        Matrix matrix = translate;
        GLES20.glReadPixels(0, 0, width, height, 6408, 5121, this.dataBuffer);
        if (undo) {
            data = new PaintingData((Bitmap) null, this.dataBuffer);
        } else {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(this.dataBuffer);
            data = new PaintingData(bitmap, (ByteBuffer) null);
        }
        int[] iArr = this.buffers;
        iArr[0] = framebuffer2;
        GLES20.glDeleteFramebuffers(1, iArr, 0);
        int[] iArr2 = this.buffers;
        iArr2[0] = texture2;
        GLES20.glDeleteTextures(1, iArr2, 0);
        return data;
    }

    public void setBrush(Brush value) {
        this.brush = value;
        Texture texture = this.brushTexture;
        if (texture != null) {
            texture.cleanResources(true);
            this.brushTexture = null;
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void onPause(final Runnable completionRunnable) {
        this.renderView.performInContext(new Runnable() {
            public void run() {
                boolean unused = Painting.this.paused = true;
                Painting painting = Painting.this;
                Slice unused2 = Painting.this.backupSlice = new Slice(painting.getPaintingData(painting.getBounds(), true).data, Painting.this.getBounds(), Painting.this.delegate.requestDispatchQueue());
                Painting.this.cleanResources(false);
                Runnable runnable = completionRunnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    public void onResume() {
        restoreSlice(this.backupSlice);
        this.backupSlice = null;
        this.paused = false;
    }

    public void cleanResources(boolean recycle) {
        int i = this.reusableFramebuffer;
        if (i != 0) {
            int[] iArr = this.buffers;
            iArr[0] = i;
            GLES20.glDeleteFramebuffers(1, iArr, 0);
            this.reusableFramebuffer = 0;
        }
        this.bitmapTexture.cleanResources(recycle);
        int i2 = this.paintTexture;
        if (i2 != 0) {
            int[] iArr2 = this.buffers;
            iArr2[0] = i2;
            GLES20.glDeleteTextures(1, iArr2, 0);
            this.paintTexture = 0;
        }
        Texture texture = this.brushTexture;
        if (texture != null) {
            texture.cleanResources(true);
            this.brushTexture = null;
        }
        Map<String, Shader> map = this.shaders;
        if (map != null) {
            for (Shader shader : map.values()) {
                shader.cleanResources();
            }
            this.shaders = null;
        }
    }

    /* access modifiers changed from: private */
    public int getReusableFramebuffer() {
        if (this.reusableFramebuffer == 0) {
            int[] buffers2 = new int[1];
            GLES20.glGenFramebuffers(1, buffers2, 0);
            this.reusableFramebuffer = buffers2[0];
            Utils.HasGLError();
        }
        return this.reusableFramebuffer;
    }

    /* access modifiers changed from: private */
    public int getTexture() {
        Texture texture = this.bitmapTexture;
        if (texture != null) {
            return texture.texture();
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public int getPaintTexture() {
        if (this.paintTexture == 0) {
            this.paintTexture = Texture.generateTexture(this.size);
        }
        return this.paintTexture;
    }

    public void setupShaders() {
        this.shaders = ShaderSet.setup();
    }
}
