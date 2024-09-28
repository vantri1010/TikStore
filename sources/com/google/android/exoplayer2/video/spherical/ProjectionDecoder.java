package com.google.android.exoplayer2.video.spherical;

import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.spherical.Projection;
import java.util.ArrayList;
import java.util.zip.Inflater;

public final class ProjectionDecoder {
    private static final int MAX_COORDINATE_COUNT = 10000;
    private static final int MAX_TRIANGLE_INDICES = 128000;
    private static final int MAX_VERTEX_COUNT = 32000;
    private static final int TYPE_DFL8 = Util.getIntegerCodeForString("dfl8");
    private static final int TYPE_MESH = Util.getIntegerCodeForString("mesh");
    private static final int TYPE_MSHP = Util.getIntegerCodeForString("mshp");
    private static final int TYPE_PROJ = Util.getIntegerCodeForString("proj");
    private static final int TYPE_RAW = Util.getIntegerCodeForString("raw ");
    private static final int TYPE_YTMP = Util.getIntegerCodeForString("ytmp");

    private ProjectionDecoder() {
    }

    public static Projection decode(byte[] projectionData, int stereoMode) {
        ParsableByteArray input = new ParsableByteArray(projectionData);
        ArrayList<Projection.Mesh> meshes = null;
        try {
            meshes = isProj(input) ? parseProj(input) : parseMshp(input);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        if (meshes == null) {
            return null;
        }
        int size = meshes.size();
        if (size == 1) {
            return new Projection(meshes.get(0), stereoMode);
        }
        if (size != 2) {
            return null;
        }
        return new Projection(meshes.get(0), meshes.get(1), stereoMode);
    }

    private static boolean isProj(ParsableByteArray input) {
        input.skipBytes(4);
        int type = input.readInt();
        input.setPosition(0);
        if (type == TYPE_PROJ) {
            return true;
        }
        return false;
    }

    private static ArrayList<Projection.Mesh> parseProj(ParsableByteArray input) {
        int childEnd;
        input.skipBytes(8);
        int position = input.getPosition();
        int limit = input.limit();
        while (position < limit && (childEnd = input.readInt() + position) > position && childEnd <= limit) {
            int childAtomType = input.readInt();
            if (childAtomType == TYPE_YTMP || childAtomType == TYPE_MSHP) {
                input.setLimit(childEnd);
                return parseMshp(input);
            }
            position = childEnd;
            input.setPosition(position);
        }
        return null;
    }

    private static ArrayList<Projection.Mesh> parseMshp(ParsableByteArray input) {
        if (input.readUnsignedByte() != 0) {
            return null;
        }
        input.skipBytes(7);
        int encoding = input.readInt();
        if (encoding == TYPE_DFL8) {
            ParsableByteArray output = new ParsableByteArray();
            Inflater inflater = new Inflater(true);
            try {
                if (!Util.inflate(input, output, inflater)) {
                    return null;
                }
                inflater.end();
                input = output;
            } finally {
                inflater.end();
            }
        } else if (encoding != TYPE_RAW) {
            return null;
        }
        return parseRawMshpData(input);
    }

    private static ArrayList<Projection.Mesh> parseRawMshpData(ParsableByteArray input) {
        ArrayList<Projection.Mesh> meshes = new ArrayList<>();
        int position = input.getPosition();
        int limit = input.limit();
        while (position < limit) {
            int childEnd = input.readInt() + position;
            if (childEnd <= position || childEnd > limit) {
                return null;
            }
            if (input.readInt() == TYPE_MESH) {
                Projection.Mesh mesh = parseMesh(input);
                if (mesh == null) {
                    return null;
                }
                meshes.add(mesh);
            }
            position = childEnd;
            input.setPosition(position);
        }
        return meshes;
    }

    private static Projection.Mesh parseMesh(ParsableByteArray input) {
        int coordinateCount = input.readInt();
        if (coordinateCount > 10000) {
            return null;
        }
        float[] coordinates = new float[coordinateCount];
        for (int coordinate = 0; coordinate < coordinateCount; coordinate++) {
            coordinates[coordinate] = input.readFloat();
        }
        int coordinate2 = input.readInt();
        if (coordinate2 > MAX_VERTEX_COUNT) {
            return null;
        }
        double log2 = Math.log(2.0d);
        int coordinateCountSizeBits = (int) Math.ceil(Math.log(((double) coordinateCount) * 2.0d) / log2);
        ParsableBitArray bitInput = new ParsableBitArray(input.data);
        int i = 8;
        bitInput.setPosition(input.getPosition() * 8);
        float[] vertices = new float[(coordinate2 * 5)];
        int i2 = 5;
        int[] coordinateIndices = new int[5];
        int vertexIndex = 0;
        int vertex = 0;
        while (vertex < coordinate2) {
            int i3 = 0;
            while (i3 < i2) {
                int coordinateIndex = coordinateIndices[i3] + decodeZigZag(bitInput.readBits(coordinateCountSizeBits));
                if (coordinateIndex >= coordinateCount || coordinateIndex < 0) {
                    return null;
                }
                vertices[vertexIndex] = coordinates[coordinateIndex];
                coordinateIndices[i3] = coordinateIndex;
                i3++;
                vertexIndex++;
                i2 = 5;
            }
            vertex++;
            i2 = 5;
        }
        bitInput.setPosition((bitInput.getPosition() + 7) & -8);
        int subMeshCount = bitInput.readBits(32);
        Projection.SubMesh[] subMeshes = new Projection.SubMesh[subMeshCount];
        int i4 = 0;
        while (i4 < subMeshCount) {
            int textureId = bitInput.readBits(i);
            int coordinateCount2 = coordinateCount;
            int drawMode = bitInput.readBits(i);
            float[] coordinates2 = coordinates;
            int triangleIndexCount = bitInput.readBits(32);
            if (triangleIndexCount > MAX_TRIANGLE_INDICES) {
                return null;
            }
            int[] coordinateIndices2 = coordinateIndices;
            int vertexIndex2 = vertexIndex;
            int vertexCountSizeBits = (int) Math.ceil(Math.log(((double) coordinate2) * 2.0d) / log2);
            int index = 0;
            int subMeshCount2 = subMeshCount;
            float[] triangleVertices = new float[(triangleIndexCount * 3)];
            double log22 = log2;
            float[] textureCoords = new float[(triangleIndexCount * 2)];
            for (int counter = 0; counter < triangleIndexCount; counter++) {
                index += decodeZigZag(bitInput.readBits(vertexCountSizeBits));
                if (index < 0 || index >= coordinate2) {
                    return null;
                }
                triangleVertices[counter * 3] = vertices[index * 5];
                triangleVertices[(counter * 3) + 1] = vertices[(index * 5) + 1];
                triangleVertices[(counter * 3) + 2] = vertices[(index * 5) + 2];
                textureCoords[counter * 2] = vertices[(index * 5) + 3];
                textureCoords[(counter * 2) + 1] = vertices[(index * 5) + 4];
            }
            subMeshes[i4] = new Projection.SubMesh(textureId, triangleVertices, textureCoords, drawMode);
            i4++;
            coordinateIndices = coordinateIndices2;
            coordinates = coordinates2;
            coordinateCount = coordinateCount2;
            vertexIndex = vertexIndex2;
            subMeshCount = subMeshCount2;
            log2 = log22;
            i = 8;
        }
        return new Projection.Mesh(subMeshes);
    }

    private static int decodeZigZag(int n) {
        return (n >> 1) ^ (-(n & 1));
    }
}
