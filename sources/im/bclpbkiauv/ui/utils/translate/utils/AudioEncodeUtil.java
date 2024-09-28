package im.bclpbkiauv.ui.utils.translate.utils;

import im.bclpbkiauv.ui.utils.translate.common.AudioEditConstant;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioEncodeUtil {
    public static void convertWav2Pcm(String inWaveFilePath, String outPcmFilePath) {
        FileInputStream in = null;
        FileOutputStream out = null;
        byte[] data = new byte[1024];
        try {
            FileInputStream in2 = new FileInputStream(inWaveFilePath);
            FileOutputStream out2 = new FileOutputStream(outPcmFilePath);
            in2.read(new byte[44]);
            while (true) {
                int read = in2.read(data);
                int length = read;
                if (read > 0) {
                    out2.write(data, 0, length);
                } else {
                    try {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            in2.close();
            try {
                out2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static void convertPcm2Wav(String inPcmFilePath, String outWavFilePath) {
        convertPcm2Wav(inPcmFilePath, outWavFilePath, AudioEditConstant.ExportSampleRate, 1, 16);
    }

    public static void convertPcm2Wav(String inPcmFilePath, String outWavFilePath, int sampleRate, int channels, int bitNum) {
        FileInputStream in = null;
        FileOutputStream out = null;
        byte[] data = new byte[1024];
        try {
            FileInputStream in2 = new FileInputStream(inPcmFilePath);
            FileOutputStream out2 = new FileOutputStream(outWavFilePath);
            writeWaveFileHeader(out2, in2.getChannel().size(), sampleRate, channels, bitNum);
            while (true) {
                int read = in2.read(data);
                int length = read;
                if (read > 0) {
                    out2.write(data, 0, length);
                } else {
                    try {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            in2.close();
            try {
                out2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            throw th;
        }
    }

    private static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, int sampleRate, int channels, int bitNum) throws IOException {
        out.write(getWaveHeader(totalAudioLen, sampleRate, channels, bitNum), 0, 44);
    }

    public static byte[] getWaveHeader(long totalAudioLen, int sampleRate, int channels, int bitNum) throws IOException {
        int i = sampleRate;
        int i2 = channels;
        long totalDataLen = totalAudioLen + 36;
        long byteRate = (long) (((i * i2) * bitNum) / 8);
        return new byte[]{82, 73, 70, 70, (byte) ((int) (totalDataLen & 255)), (byte) ((int) ((totalDataLen >> 8) & 255)), (byte) ((int) ((totalDataLen >> 16) & 255)), (byte) ((int) ((totalDataLen >> 24) & 255)), 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, (byte) i2, 0, (byte) (i & 255), (byte) ((i >> 8) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 24) & 255), (byte) ((int) (byteRate & 255)), (byte) ((int) ((byteRate >> 8) & 255)), (byte) ((int) ((byteRate >> 16) & 255)), (byte) ((int) ((byteRate >> 24) & 255)), (byte) ((i2 * 16) / 8), 0, 16, 0, 100, 97, 116, 97, (byte) ((int) (totalAudioLen & 255)), (byte) ((int) ((totalAudioLen >> 8) & 255)), (byte) ((int) ((totalAudioLen >> 16) & 255)), (byte) ((int) ((totalAudioLen >> 24) & 255))};
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01ab A[EDGE_INSN: B:76:0x01ab->B:61:0x01ab ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void convertPcm2Acc(java.lang.String r26, java.lang.String r27, int r28, int r29, int r30) {
        /*
            java.lang.String r0 = "audio/mp4a-latm"
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            android.media.MediaCodec r10 = android.media.MediaCodec.createEncoderByType(r0)     // Catch:{ IOException -> 0x009a }
            r1 = r10
            r10 = 6
            int[] r10 = new int[r10]     // Catch:{ IOException -> 0x009a }
            r11 = 8000(0x1f40, float:1.121E-41)
            r10[r9] = r11     // Catch:{ IOException -> 0x009a }
            r11 = 11025(0x2b11, float:1.545E-41)
            r12 = 1
            r10[r12] = r11     // Catch:{ IOException -> 0x009a }
            r11 = 16000(0x3e80, float:2.2421E-41)
            r13 = 2
            r10[r13] = r11     // Catch:{ IOException -> 0x009a }
            r11 = 22050(0x5622, float:3.0899E-41)
            r14 = 3
            r10[r14] = r11     // Catch:{ IOException -> 0x009a }
            r11 = 4
            r15 = 44100(0xac44, float:6.1797E-41)
            r10[r11] = r15     // Catch:{ IOException -> 0x009a }
            r11 = 5
            r15 = 48000(0xbb80, float:6.7262E-41)
            r10[r11] = r15     // Catch:{ IOException -> 0x009a }
            int[] r11 = new int[r14]     // Catch:{ IOException -> 0x009a }
            r14 = 64000(0xfa00, float:8.9683E-41)
            r11[r9] = r14     // Catch:{ IOException -> 0x009a }
            r14 = 96000(0x17700, float:1.34525E-40)
            r11[r12] = r14     // Catch:{ IOException -> 0x009a }
            r14 = 128000(0x1f400, float:1.79366E-40)
            r11[r13] = r14     // Catch:{ IOException -> 0x009a }
            r14 = r28
            r15 = r29
            android.media.MediaFormat r16 = android.media.MediaFormat.createAudioFormat(r0, r14, r15)     // Catch:{ IOException -> 0x0096 }
            r17 = r16
            java.lang.String r9 = "mime"
            r12 = r17
            r12.setString(r9, r0)     // Catch:{ IOException -> 0x0096 }
            java.lang.String r0 = "aac-profile"
            r12.setInteger(r0, r13)     // Catch:{ IOException -> 0x0096 }
            java.lang.String r0 = "bitrate"
            r9 = 1
            r13 = r11[r9]     // Catch:{ IOException -> 0x0096 }
            r12.setInteger(r0, r13)     // Catch:{ IOException -> 0x0096 }
            java.lang.String r0 = "max-input-size"
            r9 = 4096(0x1000, float:5.74E-42)
            r12.setInteger(r0, r9)     // Catch:{ IOException -> 0x0096 }
            r0 = 0
            r9 = 1
            r1.configure(r12, r0, r0, r9)     // Catch:{ IOException -> 0x0096 }
            r1.start()     // Catch:{ IOException -> 0x0096 }
            java.nio.ByteBuffer[] r0 = r1.getInputBuffers()     // Catch:{ IOException -> 0x0096 }
            r2 = r0
            java.nio.ByteBuffer[] r0 = r1.getOutputBuffers()     // Catch:{ IOException -> 0x0096 }
            r3 = r0
            android.media.MediaCodec$BufferInfo r0 = new android.media.MediaCodec$BufferInfo     // Catch:{ IOException -> 0x0096 }
            r0.<init>()     // Catch:{ IOException -> 0x0096 }
            r4 = r0
            java.io.File r0 = new java.io.File     // Catch:{ IOException -> 0x0096 }
            r9 = r27
            r0.<init>(r9)     // Catch:{ IOException -> 0x0094 }
            java.io.FileOutputStream r13 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0094 }
            r13.<init>(r0)     // Catch:{ IOException -> 0x0094 }
            r7 = r13
            java.io.ByteArrayOutputStream r13 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x0094 }
            r13.<init>()     // Catch:{ IOException -> 0x0094 }
            r8 = r13
            goto L_0x00a4
        L_0x0094:
            r0 = move-exception
            goto L_0x00a1
        L_0x0096:
            r0 = move-exception
            r9 = r27
            goto L_0x00a1
        L_0x009a:
            r0 = move-exception
            r9 = r27
            r14 = r28
            r15 = r29
        L_0x00a1:
            r0.printStackTrace()
        L_0x00a4:
            r10 = 4096(0x1000, float:5.74E-42)
            byte[] r11 = new byte[r10]
            java.io.File r0 = new java.io.File
            r12 = r26
            r0.<init>(r12)
            r13 = r0
            r17 = 0
            r18 = 0
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ IOException -> 0x01d5, Exception -> 0x01ca }
            r0.<init>(r13)     // Catch:{ IOException -> 0x01d5, Exception -> 0x01ca }
            r24 = r0
        L_0x00bb:
            r0 = 2048(0x800, float:2.87E-42)
            r9 = r24
            r24 = r10
            r10 = 0
            int r0 = r9.read(r11, r10, r0)     // Catch:{ IOException -> 0x01c0, Exception -> 0x01b6 }
            r20 = r0
            if (r0 <= 0) goto L_0x01ab
            r25 = r9
            r9 = -1
            int r0 = r1.dequeueInputBuffer(r9)     // Catch:{ IOException -> 0x01a1, Exception -> 0x0197 }
            if (r0 < 0) goto L_0x010a
            r9 = r2[r0]     // Catch:{ IOException -> 0x00ff, Exception -> 0x00f4 }
            r9.clear()     // Catch:{ IOException -> 0x00ff, Exception -> 0x00f4 }
            r9.put(r11)     // Catch:{ IOException -> 0x00ff, Exception -> 0x00f4 }
            int r10 = r11.length     // Catch:{ IOException -> 0x00ff, Exception -> 0x00f4 }
            r9.limit(r10)     // Catch:{ IOException -> 0x00ff, Exception -> 0x00f4 }
            long r21 = computePresentationTime(r5)     // Catch:{ IOException -> 0x00ff, Exception -> 0x00f4 }
            r19 = 1024(0x400, float:1.435E-42)
            r23 = 0
            r17 = r1
            r18 = r0
            r17.queueInputBuffer(r18, r19, r20, r21, r23)     // Catch:{ IOException -> 0x00ff, Exception -> 0x00f4 }
            r17 = 1
            long r5 = r5 + r17
            goto L_0x010a
        L_0x00f4:
            r0 = move-exception
            r21 = r2
            r23 = r3
            r18 = r20
            r17 = r25
            goto L_0x01d1
        L_0x00ff:
            r0 = move-exception
            r21 = r2
            r23 = r3
            r18 = r20
            r17 = r25
            goto L_0x01dc
        L_0x010a:
            r9 = 0
            int r17 = r1.dequeueOutputBuffer(r4, r9)     // Catch:{ IOException -> 0x01a1, Exception -> 0x0197 }
            r9 = r17
        L_0x0112:
            if (r9 < 0) goto L_0x016a
            int r10 = r4.size     // Catch:{ IOException -> 0x01a1, Exception -> 0x0197 }
            r17 = r0
            int r0 = r10 + 7
            r21 = r3[r9]     // Catch:{ IOException -> 0x01a1, Exception -> 0x0197 }
            r22 = r21
            r21 = r2
            int r2 = r4.offset     // Catch:{ IOException -> 0x0161, Exception -> 0x0158 }
            r23 = r3
            r3 = r22
            r3.position(r2)     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            int r2 = r4.offset     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            int r2 = r2 + r10
            r3.limit(r2)     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            byte[] r2 = new byte[r0]     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            addADTStoPacket(r2, r0)     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r22 = r0
            r0 = 7
            r3.get(r2, r0, r10)     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            int r0 = r4.offset     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r3.position(r0)     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r8.write(r2)     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r0 = r2
            r2 = 0
            r1.releaseOutputBuffer(r9, r2)     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r16 = r3
            r2 = 0
            int r18 = r1.dequeueOutputBuffer(r4, r2)     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r9 = r18
            r0 = r17
            r2 = r21
            r3 = r23
            goto L_0x0112
        L_0x0158:
            r0 = move-exception
            r23 = r3
            r18 = r20
            r17 = r25
            goto L_0x01d1
        L_0x0161:
            r0 = move-exception
            r23 = r3
            r18 = r20
            r17 = r25
            goto L_0x01dc
        L_0x016a:
            r17 = r0
            r21 = r2
            r23 = r3
            byte[] r0 = r8.toByteArray()     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r7.write(r0)     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r8.flush()     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r8.reset()     // Catch:{ IOException -> 0x0191, Exception -> 0x018b }
            r9 = r27
            r18 = r20
            r2 = r21
            r3 = r23
            r10 = r24
            r24 = r25
            goto L_0x00bb
        L_0x018b:
            r0 = move-exception
            r18 = r20
            r17 = r25
            goto L_0x01d1
        L_0x0191:
            r0 = move-exception
            r18 = r20
            r17 = r25
            goto L_0x01dc
        L_0x0197:
            r0 = move-exception
            r21 = r2
            r23 = r3
            r18 = r20
            r17 = r25
            goto L_0x01d1
        L_0x01a1:
            r0 = move-exception
            r21 = r2
            r23 = r3
            r18 = r20
            r17 = r25
            goto L_0x01dc
        L_0x01ab:
            r21 = r2
            r23 = r3
            r25 = r9
            r18 = r20
            r17 = r25
            goto L_0x01e0
        L_0x01b6:
            r0 = move-exception
            r21 = r2
            r23 = r3
            r25 = r9
            r17 = r25
            goto L_0x01d1
        L_0x01c0:
            r0 = move-exception
            r21 = r2
            r23 = r3
            r25 = r9
            r17 = r25
            goto L_0x01dc
        L_0x01ca:
            r0 = move-exception
            r21 = r2
            r23 = r3
            r24 = r10
        L_0x01d1:
            r0.printStackTrace()
            goto L_0x01e0
        L_0x01d5:
            r0 = move-exception
            r21 = r2
            r23 = r3
            r24 = r10
        L_0x01dc:
            r0.printStackTrace()
        L_0x01e0:
            r1.stop()     // Catch:{ Exception -> 0x01f0 }
            r1.release()     // Catch:{ Exception -> 0x01f0 }
            r8.flush()     // Catch:{ Exception -> 0x01f0 }
            r8.close()     // Catch:{ Exception -> 0x01f0 }
            r7.close()     // Catch:{ Exception -> 0x01f0 }
            goto L_0x01f4
        L_0x01f0:
            r0 = move-exception
            r0.printStackTrace()
        L_0x01f4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.utils.translate.utils.AudioEncodeUtil.convertPcm2Acc(java.lang.String, java.lang.String, int, int, int):void");
    }

    private static void addADTStoPacket(byte[] packet, int packetLen) {
        packet[0] = -1;
        packet[1] = -7;
        packet[2] = (byte) (((2 - 1) << 6) + (4 << 2) + (2 >> 2));
        packet[3] = (byte) (((2 & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 2047) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 31);
        packet[6] = -4;
    }

    private static long computePresentationTime(long frameIndex) {
        return ((90000 * frameIndex) * 1024) / 44100;
    }
}
