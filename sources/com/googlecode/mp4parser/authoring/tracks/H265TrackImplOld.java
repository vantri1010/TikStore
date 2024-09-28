package com.googlecode.mp4parser.authoring.tracks;

import com.baidu.mapapi.UIMsg;
import com.coremedia.iso.IsoTypeReader;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class H265TrackImplOld {
    public static final int AUD_NUT = 35;
    private static final int BLA_N_LP = 18;
    private static final int BLA_W_LP = 16;
    private static final int BLA_W_RADL = 17;
    private static final long BUFFER = 1048576;
    private static final int CRA_NUT = 21;
    private static final int IDR_N_LP = 20;
    private static final int IDR_W_RADL = 19;
    public static final int PPS_NUT = 34;
    public static final int PREFIX_SEI_NUT = 39;
    private static final int RADL_N = 6;
    private static final int RADL_R = 7;
    private static final int RASL_N = 8;
    private static final int RASL_R = 9;
    public static final int RSV_NVCL41 = 41;
    public static final int RSV_NVCL42 = 42;
    public static final int RSV_NVCL43 = 43;
    public static final int RSV_NVCL44 = 44;
    public static final int SPS_NUT = 33;
    private static final int STSA_N = 4;
    private static final int STSA_R = 5;
    private static final int TRAIL_N = 0;
    private static final int TRAIL_R = 1;
    private static final int TSA_N = 2;
    private static final int TSA_R = 3;
    public static final int UNSPEC48 = 48;
    public static final int UNSPEC49 = 49;
    public static final int UNSPEC50 = 50;
    public static final int UNSPEC51 = 51;
    public static final int UNSPEC52 = 52;
    public static final int UNSPEC53 = 53;
    public static final int UNSPEC54 = 54;
    public static final int UNSPEC55 = 55;
    public static final int VPS_NUT = 32;
    LinkedHashMap<Long, ByteBuffer> pictureParamterSets = new LinkedHashMap<>();
    List<Sample> samples = new ArrayList();
    LinkedHashMap<Long, ByteBuffer> sequenceParamterSets = new LinkedHashMap<>();
    List<Long> syncSamples = new ArrayList();
    LinkedHashMap<Long, ByteBuffer> videoParamterSets = new LinkedHashMap<>();

    public static class NalUnitHeader {
        int forbiddenZeroFlag;
        int nalUnitType;
        int nuhLayerId;
        int nuhTemporalIdPlusOne;
    }

    public enum PARSE_STATE {
        AUD_SEI_SLICE,
        SEI_SLICE,
        SLICE_OES_EOB
    }

    public H265TrackImplOld(DataSource ds) throws IOException {
        LookAhead la = new LookAhead(ds);
        long sampleNo = 1;
        List<ByteBuffer> accessUnit = new ArrayList<>();
        int accessUnitNalType = 0;
        while (true) {
            ByteBuffer findNextNal = findNextNal(la);
            ByteBuffer nal = findNextNal;
            char c = 0;
            if (findNextNal == null) {
                System.err.println("");
                HevcDecoderConfigurationRecord hvcC = new HevcDecoderConfigurationRecord();
                hvcC.setArrays(getArrays());
                hvcC.setAvgFrameRate(0);
                return;
            }
            NalUnitHeader nalUnitHeader = getNalUnitHeader(nal);
            switch (nalUnitHeader.nalUnitType) {
                case 32:
                    this.videoParamterSets.put(Long.valueOf(sampleNo), nal);
                    break;
                case 33:
                    this.sequenceParamterSets.put(Long.valueOf(sampleNo), nal);
                    break;
                case 34:
                    this.pictureParamterSets.put(Long.valueOf(sampleNo), nal);
                    break;
            }
            accessUnitNalType = nalUnitHeader.nalUnitType < 32 ? nalUnitHeader.nalUnitType : accessUnitNalType;
            if (isFirstOfAU(nalUnitHeader.nalUnitType, nal, accessUnit) && !accessUnit.isEmpty()) {
                System.err.println("##########################");
                for (ByteBuffer byteBuffer : accessUnit) {
                    NalUnitHeader _nalUnitHeader = getNalUnitHeader(byteBuffer);
                    PrintStream printStream = System.err;
                    Object[] objArr = new Object[4];
                    objArr[c] = Integer.valueOf(_nalUnitHeader.nalUnitType);
                    objArr[1] = Integer.valueOf(_nalUnitHeader.nuhLayerId);
                    objArr[2] = Integer.valueOf(_nalUnitHeader.nuhTemporalIdPlusOne);
                    objArr[3] = Integer.valueOf(byteBuffer.limit());
                    printStream.println(String.format("type: %3d - layer: %3d - tempId: %3d - size: %3d", objArr));
                    c = 0;
                }
                System.err.println("                          ##########################");
                this.samples.add(createSample(accessUnit));
                accessUnit.clear();
                sampleNo++;
            }
            accessUnit.add(nal);
            if (accessUnitNalType >= 16 && accessUnitNalType <= 21) {
                this.syncSamples.add(Long.valueOf(sampleNo));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new H265TrackImplOld(new FileDataSourceImpl("c:\\content\\test-UHD-HEVC_01_FMV_Med_track1.hvc"));
    }

    private ByteBuffer findNextNal(LookAhead la) throws IOException {
        while (!la.nextThreeEquals001()) {
            try {
                la.discardByte();
            } catch (EOFException e) {
                return null;
            }
        }
        la.discardNext3AndMarkStart();
        while (!la.nextThreeEquals000or001orEof()) {
            la.discardByte();
        }
        return la.getNal();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: boolean[][]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void profile_tier_level(int r20, com.googlecode.mp4parser.h264.read.CAVLCReader r21) throws java.io.IOException {
        /*
            r19 = this;
            r0 = r20
            r1 = r21
            r2 = 2
            java.lang.String r3 = "general_profile_space "
            r1.readU(r2, r3)
            java.lang.String r3 = "general_tier_flag"
            r1.readBool(r3)
            r3 = 5
            java.lang.String r4 = "general_profile_idc"
            r1.readU(r3, r4)
            r4 = 32
            boolean[] r5 = new boolean[r4]
            r6 = 0
        L_0x001a:
            java.lang.String r7 = "]"
            if (r6 < r4) goto L_0x01c1
            java.lang.String r6 = "general_progressive_source_flag"
            r1.readBool(r6)
            java.lang.String r6 = "general_interlaced_source_flag"
            r1.readBool(r6)
            java.lang.String r6 = "general_non_packed_constraint_flag"
            r1.readBool(r6)
            java.lang.String r6 = "general_frame_only_constraint_flag"
            r1.readBool(r6)
            r8 = 44
            java.lang.String r6 = "general_reserved_zero_44bits"
            r1.readU(r8, r6)
            r9 = 8
            java.lang.String r6 = "general_level_idc"
            r1.readU(r9, r6)
            boolean[] r10 = new boolean[r0]
            boolean[] r11 = new boolean[r0]
            r6 = 0
        L_0x0045:
            if (r6 < r0) goto L_0x0181
            if (r0 <= 0) goto L_0x0056
            r6 = r20
        L_0x004b:
            if (r6 < r9) goto L_0x004e
            goto L_0x0056
        L_0x004e:
            java.lang.String r12 = "reserved_zero_2bits"
            r1.readU(r2, r12)
            int r6 = r6 + 1
            goto L_0x004b
        L_0x0056:
            int[] r12 = new int[r0]
            boolean[] r13 = new boolean[r0]
            int[] r14 = new int[r0]
            int[] r6 = new int[r2]
            r15 = 1
            r6[r15] = r4
            r15 = 0
            r6[r15] = r0
            java.lang.Class<boolean> r15 = boolean.class
            java.lang.Object r6 = java.lang.reflect.Array.newInstance(r15, r6)
            r15 = r6
            boolean[][] r15 = (boolean[][]) r15
            boolean[] r6 = new boolean[r0]
            boolean[] r9 = new boolean[r0]
            boolean[] r8 = new boolean[r0]
            boolean[] r4 = new boolean[r0]
            int[] r3 = new int[r0]
            r17 = 0
            r2 = r17
        L_0x007b:
            if (r2 < r0) goto L_0x007e
            return
        L_0x007e:
            boolean r17 = r10[r2]
            if (r17 == 0) goto L_0x0162
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r17 = r5
            java.lang.String r5 = "sub_layer_profile_space["
            r0.<init>(r5)
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            r5 = 2
            int r0 = r1.readU(r5, r0)
            r12[r2] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r5 = "sub_layer_tier_flag["
            r0.<init>(r5)
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r13[r2] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r5 = "sub_layer_profile_idc["
            r0.<init>(r5)
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            r5 = 5
            int r0 = r1.readU(r5, r0)
            r14[r2] = r0
            r0 = 0
        L_0x00cc:
            r5 = 32
            if (r0 < r5) goto L_0x0136
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r5 = "sub_layer_progressive_source_flag["
            r0.<init>(r5)
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r6[r2] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r5 = "sub_layer_interlaced_source_flag["
            r0.<init>(r5)
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r9[r2] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r5 = "sub_layer_non_packed_constraint_flag["
            r0.<init>(r5)
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r8[r2] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r5 = "sub_layer_frame_only_constraint_flag["
            r0.<init>(r5)
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r4[r2] = r0
            java.lang.String r0 = "reserved"
            r5 = 44
            r1.readNBit(r5, r0)
            r18 = r4
            goto L_0x0166
        L_0x0136:
            r5 = 44
            r16 = r15[r2]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r18 = r4
            java.lang.String r4 = "sub_layer_profile_compatibility_flag["
            r5.<init>(r4)
            r5.append(r2)
            java.lang.String r4 = "]["
            r5.append(r4)
            r5.append(r0)
            r5.append(r7)
            java.lang.String r4 = r5.toString()
            boolean r4 = r1.readBool(r4)
            r16[r0] = r4
            int r0 = r0 + 1
            r4 = r18
            r5 = 5
            goto L_0x00cc
        L_0x0162:
            r18 = r4
            r17 = r5
        L_0x0166:
            boolean r0 = r11[r2]
            if (r0 == 0) goto L_0x0175
            java.lang.String r0 = "sub_layer_level_idc"
            r4 = 8
            int r0 = r1.readU(r4, r0)
            r3[r2] = r0
            goto L_0x0177
        L_0x0175:
            r4 = 8
        L_0x0177:
            int r2 = r2 + 1
            r0 = r20
            r5 = r17
            r4 = r18
            goto L_0x007b
        L_0x0181:
            r17 = r5
            r4 = 8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r2 = "sub_layer_profile_present_flag["
            r0.<init>(r2)
            r0.append(r6)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r10[r6] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r2 = "sub_layer_level_present_flag["
            r0.<init>(r2)
            r0.append(r6)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r11[r6] = r0
            int r6 = r6 + 1
            r0 = r20
            r2 = 2
            r3 = 5
            r4 = 32
            r8 = 44
            r9 = 8
            goto L_0x0045
        L_0x01c1:
            r17 = r5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r2 = "general_profile_compatibility_flag["
            r0.<init>(r2)
            r0.append(r6)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r17[r6] = r0
            int r6 = r6 + 1
            r0 = r20
            r2 = 2
            r3 = 5
            r4 = 32
            goto L_0x001a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.H265TrackImplOld.profile_tier_level(int, com.googlecode.mp4parser.h264.read.CAVLCReader):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v0, resolved type: boolean[][]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getFrameRate(java.nio.ByteBuffer r20) throws java.io.IOException {
        /*
            r19 = this;
            r0 = r19
            com.googlecode.mp4parser.h264.read.CAVLCReader r1 = new com.googlecode.mp4parser.h264.read.CAVLCReader
            com.googlecode.mp4parser.util.ByteBufferByteChannel r2 = new com.googlecode.mp4parser.util.ByteBufferByteChannel
            r3 = 0
            r4 = r20
            java.nio.Buffer r5 = r4.position(r3)
            java.nio.ByteBuffer r5 = (java.nio.ByteBuffer) r5
            r2.<init>(r5)
            java.io.InputStream r2 = java.nio.channels.Channels.newInputStream(r2)
            r1.<init>(r2)
            r2 = 4
            java.lang.String r5 = "vps_parameter_set_id"
            r1.readU(r2, r5)
            r2 = 2
            java.lang.String r5 = "vps_reserved_three_2bits"
            r1.readU(r2, r5)
            r5 = 6
            java.lang.String r6 = "vps_max_layers_minus1"
            r1.readU(r5, r6)
            r6 = 3
            java.lang.String r7 = "vps_max_sub_layers_minus1"
            int r6 = r1.readU(r6, r7)
            java.lang.String r7 = "vps_temporal_id_nesting_flag"
            r1.readBool(r7)
            r7 = 16
            java.lang.String r8 = "vps_reserved_0xffff_16bits"
            r1.readU(r7, r8)
            r0.profile_tier_level(r6, r1)
            java.lang.String r7 = "vps_sub_layer_ordering_info_present_flag"
            boolean r7 = r1.readBool(r7)
            if (r7 == 0) goto L_0x004b
            r8 = 0
            goto L_0x004c
        L_0x004b:
            r8 = r6
        L_0x004c:
            int[] r8 = new int[r8]
            if (r7 == 0) goto L_0x0052
            r9 = 0
            goto L_0x0053
        L_0x0052:
            r9 = r6
        L_0x0053:
            int[] r9 = new int[r9]
            if (r7 == 0) goto L_0x0059
            r10 = 0
            goto L_0x005a
        L_0x0059:
            r10 = r6
        L_0x005a:
            int[] r10 = new int[r10]
            if (r7 == 0) goto L_0x0060
            r11 = 0
            goto L_0x0061
        L_0x0060:
            r11 = r6
        L_0x0061:
            java.lang.String r12 = "]"
            if (r11 <= r6) goto L_0x014a
            java.lang.String r11 = "vps_max_layer_id"
            int r13 = r1.readU(r5, r11)
            java.lang.String r5 = "vps_num_layer_sets_minus1"
            int r14 = r1.readUE(r5)
            int[] r2 = new int[r2]
            r15 = 1
            r2[r15] = r13
            r2[r3] = r14
            java.lang.Class<boolean> r5 = boolean.class
            java.lang.Object r2 = java.lang.reflect.Array.newInstance(r5, r2)
            r16 = r2
            boolean[][] r16 = (boolean[][]) r16
            r2 = 1
        L_0x0083:
            if (r2 <= r14) goto L_0x011a
            java.lang.String r2 = "vps_timing_info_present_flag"
            boolean r2 = r1.readBool(r2)
            if (r2 == 0) goto L_0x00fd
            r5 = 32
            java.lang.String r11 = "vps_num_units_in_tick"
            r1.readU(r5, r11)
            java.lang.String r11 = "vps_time_scale"
            r1.readU(r5, r11)
            java.lang.String r5 = "vps_poc_proportional_to_timing_flag"
            boolean r5 = r1.readBool(r5)
            if (r5 == 0) goto L_0x00a6
            java.lang.String r11 = "vps_num_ticks_poc_diff_one_minus1"
            r1.readUE(r11)
        L_0x00a6:
            java.lang.String r11 = "vps_num_hrd_parameters"
            int r11 = r1.readUE(r11)
            int[] r3 = new int[r11]
            boolean[] r15 = new boolean[r11]
            r17 = 0
            r18 = r2
            r2 = r17
        L_0x00b6:
            if (r2 < r11) goto L_0x00b9
            goto L_0x00ff
        L_0x00b9:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r17 = r5
            java.lang.String r5 = "hrd_layer_set_idx["
            r4.<init>(r5)
            r4.append(r2)
            r4.append(r12)
            java.lang.String r4 = r4.toString()
            int r4 = r1.readUE(r4)
            r3[r2] = r4
            if (r2 <= 0) goto L_0x00ed
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            java.lang.String r5 = "cprms_present_flag["
            r4.<init>(r5)
            r4.append(r2)
            r4.append(r12)
            java.lang.String r4 = r4.toString()
            boolean r4 = r1.readBool(r4)
            r15[r2] = r4
            r4 = 1
            goto L_0x00f1
        L_0x00ed:
            r4 = 1
            r5 = 0
            r15[r5] = r4
        L_0x00f1:
            boolean r5 = r15[r2]
            r0.hrd_parameters(r5, r6, r1)
            int r2 = r2 + 1
            r4 = r20
            r5 = r17
            goto L_0x00b6
        L_0x00fd:
            r18 = r2
        L_0x00ff:
            java.lang.String r2 = "vps_extension_flag"
            boolean r2 = r1.readBool(r2)
            if (r2 == 0) goto L_0x0115
        L_0x0108:
            boolean r3 = r1.moreRBSPData()
            if (r3 != 0) goto L_0x010f
            goto L_0x0115
        L_0x010f:
            java.lang.String r3 = "vps_extension_data_flag"
            r1.readBool(r3)
            goto L_0x0108
        L_0x0115:
            r1.readTrailingBits()
            r3 = 0
            return r3
        L_0x011a:
            r4 = 1
            r5 = 0
        L_0x011c:
            if (r5 <= r13) goto L_0x0125
            int r2 = r2 + 1
            r4 = r20
            r15 = 1
            goto L_0x0083
        L_0x0125:
            r11 = r16[r2]
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            java.lang.String r3 = "layer_id_included_flag["
            r15.<init>(r3)
            r15.append(r2)
            java.lang.String r3 = "]["
            r15.append(r3)
            r15.append(r5)
            r15.append(r12)
            java.lang.String r3 = r15.toString()
            boolean r3 = r1.readBool(r3)
            r11[r5] = r3
            int r5 = r5 + 1
            r3 = 0
            goto L_0x011c
        L_0x014a:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            java.lang.String r4 = "vps_max_dec_pic_buffering_minus1["
            r3.<init>(r4)
            r3.append(r11)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            int r3 = r1.readUE(r3)
            r8[r11] = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>(r4)
            r3.append(r11)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            int r3 = r1.readUE(r3)
            r9[r11] = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>(r4)
            r3.append(r11)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            int r3 = r1.readUE(r3)
            r10[r11] = r3
            int r11 = r11 + 1
            r4 = r20
            r3 = 0
            goto L_0x0061
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.H265TrackImplOld.getFrameRate(java.nio.ByteBuffer):int");
    }

    private void hrd_parameters(boolean commonInfPresentFlag, int maxNumSubLayersMinus1, CAVLCReader r) throws IOException {
        boolean nal_hrd_parameters_present_flag = false;
        boolean vcl_hrd_parameters_present_flag = false;
        boolean sub_pic_hrd_params_present_flag = false;
        if (commonInfPresentFlag) {
            nal_hrd_parameters_present_flag = r.readBool("nal_hrd_parameters_present_flag");
            vcl_hrd_parameters_present_flag = r.readBool("vcl_hrd_parameters_present_flag");
            if (nal_hrd_parameters_present_flag || vcl_hrd_parameters_present_flag) {
                sub_pic_hrd_params_present_flag = r.readBool("sub_pic_hrd_params_present_flag");
                if (sub_pic_hrd_params_present_flag) {
                    r.readU(8, "tick_divisor_minus2");
                    r.readU(5, "du_cpb_removal_delay_increment_length_minus1");
                    r.readBool("sub_pic_cpb_params_in_pic_timing_sei_flag");
                    r.readU(5, "dpb_output_delay_du_length_minus1");
                }
                r.readU(4, "bit_rate_scale");
                r.readU(4, "cpb_size_scale");
                if (sub_pic_hrd_params_present_flag) {
                    r.readU(4, "cpb_size_du_scale");
                }
                r.readU(5, "initial_cpb_removal_delay_length_minus1");
                r.readU(5, "au_cpb_removal_delay_length_minus1");
                r.readU(5, "dpb_output_delay_length_minus1");
            }
        }
        boolean[] fixed_pic_rate_general_flag = new boolean[maxNumSubLayersMinus1];
        boolean[] fixed_pic_rate_within_cvs_flag = new boolean[maxNumSubLayersMinus1];
        boolean[] low_delay_hrd_flag = new boolean[maxNumSubLayersMinus1];
        int[] cpb_cnt_minus1 = new int[maxNumSubLayersMinus1];
        int[] elemental_duration_in_tc_minus1 = new int[maxNumSubLayersMinus1];
        for (int i = 0; i <= maxNumSubLayersMinus1; i++) {
            fixed_pic_rate_general_flag[i] = r.readBool("fixed_pic_rate_general_flag[" + i + "]");
            if (!fixed_pic_rate_general_flag[i]) {
                fixed_pic_rate_within_cvs_flag[i] = r.readBool("fixed_pic_rate_within_cvs_flag[" + i + "]");
            }
            if (fixed_pic_rate_within_cvs_flag[i]) {
                elemental_duration_in_tc_minus1[i] = r.readUE("elemental_duration_in_tc_minus1[" + i + "]");
            } else {
                low_delay_hrd_flag[i] = r.readBool("low_delay_hrd_flag[" + i + "]");
            }
            if (!low_delay_hrd_flag[i]) {
                cpb_cnt_minus1[i] = r.readUE("cpb_cnt_minus1[" + i + "]");
            }
            if (nal_hrd_parameters_present_flag) {
                sub_layer_hrd_parameters(i, cpb_cnt_minus1[i], sub_pic_hrd_params_present_flag, r);
            }
            if (vcl_hrd_parameters_present_flag) {
                sub_layer_hrd_parameters(i, cpb_cnt_minus1[i], sub_pic_hrd_params_present_flag, r);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void sub_layer_hrd_parameters(int subLayerId, int cpbCnt, boolean sub_pic_hrd_params_present_flag, CAVLCReader r) throws IOException {
        int[] bit_rate_value_minus1 = new int[cpbCnt];
        int[] cpb_size_value_minus1 = new int[cpbCnt];
        int[] cpb_size_du_value_minus1 = new int[cpbCnt];
        int[] bit_rate_du_value_minus1 = new int[cpbCnt];
        boolean[] cbr_flag = new boolean[cpbCnt];
        for (int i = 0; i <= cpbCnt; i++) {
            bit_rate_value_minus1[i] = r.readUE("bit_rate_value_minus1[" + i + "]");
            cpb_size_value_minus1[i] = r.readUE("cpb_size_value_minus1[" + i + "]");
            if (sub_pic_hrd_params_present_flag) {
                cpb_size_du_value_minus1[i] = r.readUE("cpb_size_du_value_minus1[" + i + "]");
                bit_rate_du_value_minus1[i] = r.readUE("bit_rate_du_value_minus1[" + i + "]");
            }
            cbr_flag[i] = r.readBool("cbr_flag[" + i + "]");
        }
    }

    private List<HevcDecoderConfigurationRecord.Array> getArrays() {
        HevcDecoderConfigurationRecord.Array vpsArray = new HevcDecoderConfigurationRecord.Array();
        vpsArray.array_completeness = true;
        vpsArray.nal_unit_type = 32;
        vpsArray.nalUnits = new ArrayList();
        for (ByteBuffer byteBuffer : this.videoParamterSets.values()) {
            byte[] ps = new byte[byteBuffer.limit()];
            byteBuffer.position(0);
            byteBuffer.get(ps);
            vpsArray.nalUnits.add(ps);
        }
        HevcDecoderConfigurationRecord.Array spsArray = new HevcDecoderConfigurationRecord.Array();
        spsArray.array_completeness = true;
        spsArray.nal_unit_type = 33;
        spsArray.nalUnits = new ArrayList();
        for (ByteBuffer byteBuffer2 : this.sequenceParamterSets.values()) {
            byte[] ps2 = new byte[byteBuffer2.limit()];
            byteBuffer2.position(0);
            byteBuffer2.get(ps2);
            spsArray.nalUnits.add(ps2);
        }
        HevcDecoderConfigurationRecord.Array ppsArray = new HevcDecoderConfigurationRecord.Array();
        ppsArray.array_completeness = true;
        ppsArray.nal_unit_type = 33;
        ppsArray.nalUnits = new ArrayList();
        for (ByteBuffer byteBuffer3 : this.pictureParamterSets.values()) {
            byte[] ps3 = new byte[byteBuffer3.limit()];
            byteBuffer3.position(0);
            byteBuffer3.get(ps3);
            ppsArray.nalUnits.add(ps3);
        }
        return Arrays.asList(new HevcDecoderConfigurationRecord.Array[]{vpsArray, spsArray, ppsArray});
    }

    /* access modifiers changed from: package-private */
    public boolean isFirstOfAU(int nalUnitType, ByteBuffer nalUnit, List<ByteBuffer> accessUnit) {
        if (accessUnit.isEmpty()) {
            return true;
        }
        boolean vclPresentInCurrentAU = getNalUnitHeader(accessUnit.get(accessUnit.size() - 1)).nalUnitType <= 31;
        switch (nalUnitType) {
            case 32:
            case 33:
            case 34:
            case 35:
            case 39:
            case 41:
            case 42:
            case 43:
            case 44:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
                if (vclPresentInCurrentAU) {
                    return true;
                }
                break;
        }
        switch (nalUnitType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                break;
            default:
                switch (nalUnitType) {
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                        break;
                    default:
                        return false;
                }
        }
        nalUnit.position(0);
        nalUnit.get(new byte[50]);
        nalUnit.position(2);
        int firstRsbp8Bit = IsoTypeReader.readUInt8(nalUnit);
        if (!vclPresentInCurrentAU || (firstRsbp8Bit & 128) <= 0) {
            return false;
        }
        return true;
    }

    public NalUnitHeader getNalUnitHeader(ByteBuffer nal) {
        nal.position(0);
        int nal_unit_header = IsoTypeReader.readUInt16(nal);
        NalUnitHeader nalUnitHeader = new NalUnitHeader();
        nalUnitHeader.forbiddenZeroFlag = (32768 & nal_unit_header) >> 15;
        nalUnitHeader.nalUnitType = (nal_unit_header & 32256) >> 9;
        nalUnitHeader.nuhLayerId = (nal_unit_header & UIMsg.d_ResultType.LOC_INFO_UPLOAD) >> 3;
        nalUnitHeader.nuhTemporalIdPlusOne = nal_unit_header & 7;
        return nalUnitHeader;
    }

    /* access modifiers changed from: protected */
    public Sample createSample(List<ByteBuffer> nals) {
        byte[] sizeInfo = new byte[(nals.size() * 4)];
        ByteBuffer sizeBuf = ByteBuffer.wrap(sizeInfo);
        for (ByteBuffer b : nals) {
            sizeBuf.putInt(b.remaining());
        }
        ByteBuffer[] data = new ByteBuffer[(nals.size() * 2)];
        for (int i = 0; i < nals.size(); i++) {
            data[i * 2] = ByteBuffer.wrap(sizeInfo, i * 4, 4);
            data[(i * 2) + 1] = nals.get(i);
        }
        return new SampleImpl(data);
    }

    class LookAhead {
        ByteBuffer buffer;
        long bufferStartPos = 0;
        DataSource dataSource;
        int inBufferPos = 0;
        long start;

        LookAhead(DataSource dataSource2) throws IOException {
            this.dataSource = dataSource2;
            fillBuffer();
        }

        public void fillBuffer() throws IOException {
            DataSource dataSource2 = this.dataSource;
            this.buffer = dataSource2.map(this.bufferStartPos, Math.min(dataSource2.size() - this.bufferStartPos, H265TrackImplOld.BUFFER));
        }

        /* access modifiers changed from: package-private */
        public boolean nextThreeEquals001() throws IOException {
            int limit = this.buffer.limit();
            int i = this.inBufferPos;
            if (limit - i >= 3) {
                if (this.buffer.get(i) == 0 && this.buffer.get(this.inBufferPos + 1) == 0 && this.buffer.get(this.inBufferPos + 2) == 1) {
                    return true;
                }
                return false;
            } else if (this.bufferStartPos + ((long) i) == this.dataSource.size()) {
                throw new EOFException();
            } else {
                throw new RuntimeException("buffer repositioning require");
            }
        }

        /* access modifiers changed from: package-private */
        public boolean nextThreeEquals000or001orEof() throws IOException {
            int limit = this.buffer.limit();
            int i = this.inBufferPos;
            if (limit - i >= 3) {
                return this.buffer.get(i) == 0 && this.buffer.get(this.inBufferPos + 1) == 0 && (this.buffer.get(this.inBufferPos + 2) == 0 || this.buffer.get(this.inBufferPos + 2) == 1);
            }
            if (this.bufferStartPos + ((long) i) + 3 > this.dataSource.size()) {
                return this.bufferStartPos + ((long) this.inBufferPos) == this.dataSource.size();
            }
            this.bufferStartPos = this.start;
            this.inBufferPos = 0;
            fillBuffer();
            return nextThreeEquals000or001orEof();
        }

        /* access modifiers changed from: package-private */
        public void discardByte() {
            this.inBufferPos++;
        }

        /* access modifiers changed from: package-private */
        public void discardNext3AndMarkStart() {
            int i = this.inBufferPos + 3;
            this.inBufferPos = i;
            this.start = this.bufferStartPos + ((long) i);
        }

        public ByteBuffer getNal() {
            long j = this.start;
            long j2 = this.bufferStartPos;
            if (j >= j2) {
                this.buffer.position((int) (j - j2));
                ByteBuffer slice = this.buffer.slice();
                slice.limit((int) (((long) this.inBufferPos) - (this.start - this.bufferStartPos)));
                return slice;
            }
            throw new RuntimeException("damn! NAL exceeds buffer");
        }
    }
}
