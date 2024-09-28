package com.google.android.exoplayer2.video.spherical;

import com.google.android.exoplayer2.util.Assertions;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Projection {
    public static final int DRAW_MODE_TRIANGLES = 0;
    public static final int DRAW_MODE_TRIANGLES_FAN = 2;
    public static final int DRAW_MODE_TRIANGLES_STRIP = 1;
    public static final int POSITION_COORDS_PER_VERTEX = 3;
    public static final int TEXTURE_COORDS_PER_VERTEX = 2;
    public final Mesh leftMesh;
    public final Mesh rightMesh;
    public final boolean singleMesh;
    public final int stereoMode;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface DrawMode {
    }

    public static Projection createEquirectangular(int stereoMode2) {
        return createEquirectangular(50.0f, 36, 72, 180.0f, 360.0f, stereoMode2);
    }

    public static Projection createEquirectangular(float radius, int latitudes, int longitudes, float verticalFovDegrees, float horizontalFovDegrees, int stereoMode2) {
        int k;
        float f = radius;
        int i = latitudes;
        int i2 = longitudes;
        float f2 = verticalFovDegrees;
        float f3 = horizontalFovDegrees;
        Assertions.checkArgument(f > 0.0f);
        Assertions.checkArgument(i >= 1);
        Assertions.checkArgument(i2 >= 1);
        Assertions.checkArgument(f2 > 0.0f && f2 <= 180.0f);
        Assertions.checkArgument(f3 > 0.0f && f3 <= 360.0f);
        float verticalFovRads = (float) Math.toRadians((double) f2);
        float horizontalFovRads = (float) Math.toRadians((double) f3);
        float quadHeightRads = verticalFovRads / ((float) i);
        float quadWidthRads = horizontalFovRads / ((float) i2);
        int vertexCount = (((i2 + 1) * 2) + 2) * i;
        float[] vertexData = new float[(vertexCount * 3)];
        float[] textureData = new float[(vertexCount * 2)];
        int vOffset = 0;
        int tOffset = 0;
        int k2 = 0;
        while (k2 < i) {
            float phiLow = (((float) k2) * quadHeightRads) - (verticalFovRads / 2.0f);
            float phiHigh = (((float) (k2 + 1)) * quadHeightRads) - (verticalFovRads / 2.0f);
            int i3 = 0;
            while (i3 < i2 + 1) {
                int k3 = 0;
                while (k3 < 2) {
                    float phi = k3 == 0 ? phiLow : phiHigh;
                    float phiLow2 = phiLow;
                    float theta = ((((float) i3) * quadWidthRads) + 3.1415927f) - (horizontalFovRads / 2.0f);
                    int vOffset2 = vOffset + 1;
                    int vertexCount2 = vertexCount;
                    float phiHigh2 = phiHigh;
                    int k4 = k3;
                    vertexData[vOffset] = -((float) (((double) f) * Math.sin((double) theta) * Math.cos((double) phi)));
                    int vOffset3 = vOffset2 + 1;
                    float verticalFovRads2 = verticalFovRads;
                    int j = k2;
                    vertexData[vOffset2] = (float) (((double) f) * Math.sin((double) phi));
                    vOffset = vOffset3 + 1;
                    vertexData[vOffset3] = (float) (((double) f) * Math.cos((double) theta) * Math.cos((double) phi));
                    int tOffset2 = tOffset + 1;
                    textureData[tOffset] = (((float) i3) * quadWidthRads) / horizontalFovRads;
                    int tOffset3 = tOffset2 + 1;
                    textureData[tOffset2] = (((float) (j + k4)) * quadHeightRads) / verticalFovRads2;
                    if (i3 == 0 && k4 == 0) {
                        i2 = longitudes;
                        k = k4;
                    } else {
                        i2 = longitudes;
                        if (i3 == i2) {
                            k = k4;
                            if (k != 1) {
                            }
                        } else {
                            k = k4;
                        }
                        tOffset = tOffset3;
                        verticalFovRads = verticalFovRads2;
                        k3 = k + 1;
                        phiLow = phiLow2;
                        vertexCount = vertexCount2;
                        phiHigh = phiHigh2;
                        k2 = j;
                        float f4 = horizontalFovDegrees;
                    }
                    System.arraycopy(vertexData, vOffset - 3, vertexData, vOffset, 3);
                    System.arraycopy(textureData, tOffset3 - 2, textureData, tOffset3, 2);
                    tOffset = tOffset3 + 2;
                    vOffset += 3;
                    verticalFovRads = verticalFovRads2;
                    k3 = k + 1;
                    phiLow = phiLow2;
                    vertexCount = vertexCount2;
                    phiHigh = phiHigh2;
                    k2 = j;
                    float f42 = horizontalFovDegrees;
                }
                int j2 = k2;
                float f5 = phiLow;
                float f6 = phiHigh;
                int j3 = k3;
                float f7 = verticalFovRads;
                i3++;
                float f8 = horizontalFovDegrees;
                vertexCount = vertexCount;
                k2 = j2;
                float verticalFovRads3 = verticalFovDegrees;
            }
            float f9 = phiLow;
            float f10 = phiHigh;
            k2++;
            i = latitudes;
            float f11 = horizontalFovDegrees;
            vertexCount = vertexCount;
            float verticalFovRads4 = verticalFovDegrees;
        }
        int i4 = k2;
        return new Projection(new Mesh(new SubMesh(0, vertexData, textureData, 1)), stereoMode2);
    }

    public Projection(Mesh mesh, int stereoMode2) {
        this(mesh, mesh, stereoMode2);
    }

    public Projection(Mesh leftMesh2, Mesh rightMesh2, int stereoMode2) {
        this.leftMesh = leftMesh2;
        this.rightMesh = rightMesh2;
        this.stereoMode = stereoMode2;
        this.singleMesh = leftMesh2 == rightMesh2;
    }

    public static final class SubMesh {
        public static final int VIDEO_TEXTURE_ID = 0;
        public final int mode;
        public final float[] textureCoords;
        public final int textureId;
        public final float[] vertices;

        public SubMesh(int textureId2, float[] vertices2, float[] textureCoords2, int mode2) {
            this.textureId = textureId2;
            Assertions.checkArgument(((long) vertices2.length) * 2 == ((long) textureCoords2.length) * 3);
            this.vertices = vertices2;
            this.textureCoords = textureCoords2;
            this.mode = mode2;
        }

        public int getVertexCount() {
            return this.vertices.length / 3;
        }
    }

    public static final class Mesh {
        private final SubMesh[] subMeshes;

        public Mesh(SubMesh... subMeshes2) {
            this.subMeshes = subMeshes2;
        }

        public int getSubMeshCount() {
            return this.subMeshes.length;
        }

        public SubMesh getSubMesh(int index) {
            return this.subMeshes[index];
        }
    }
}
