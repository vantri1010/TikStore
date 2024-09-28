package com.googlecode.mp4parser.authoring.tracks.h265;

import com.googlecode.mp4parser.h264.read.CAVLCReader;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoParameterSet {
    ByteBuffer vps;
    int vps_parameter_set_id;

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v0, resolved type: boolean[][]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public VideoParameterSet(java.nio.ByteBuffer r22) throws java.io.IOException {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r21.<init>()
            r0.vps = r1
            com.googlecode.mp4parser.h264.read.CAVLCReader r2 = new com.googlecode.mp4parser.h264.read.CAVLCReader
            com.googlecode.mp4parser.util.ByteBufferByteChannel r3 = new com.googlecode.mp4parser.util.ByteBufferByteChannel
            r4 = 0
            java.nio.Buffer r5 = r1.position(r4)
            java.nio.ByteBuffer r5 = (java.nio.ByteBuffer) r5
            r3.<init>(r5)
            java.io.InputStream r3 = java.nio.channels.Channels.newInputStream(r3)
            r2.<init>(r3)
            r3 = 4
            java.lang.String r5 = "vps_parameter_set_id"
            int r3 = r2.readU(r3, r5)
            r0.vps_parameter_set_id = r3
            r3 = 2
            java.lang.String r5 = "vps_reserved_three_2bits"
            r2.readU(r3, r5)
            r5 = 6
            java.lang.String r6 = "vps_max_layers_minus1"
            r2.readU(r5, r6)
            r6 = 3
            java.lang.String r7 = "vps_max_sub_layers_minus1"
            int r6 = r2.readU(r6, r7)
            java.lang.String r7 = "vps_temporal_id_nesting_flag"
            r2.readBool(r7)
            r7 = 16
            java.lang.String r8 = "vps_reserved_0xffff_16bits"
            r2.readU(r7, r8)
            r0.profile_tier_level(r6, r2)
            java.lang.String r7 = "vps_sub_layer_ordering_info_present_flag"
            boolean r7 = r2.readBool(r7)
            r8 = 1
            if (r7 == 0) goto L_0x0054
            r9 = 1
            goto L_0x0056
        L_0x0054:
            int r9 = r6 + 1
        L_0x0056:
            int[] r9 = new int[r9]
            if (r7 == 0) goto L_0x005c
            r10 = 1
            goto L_0x005e
        L_0x005c:
            int r10 = r6 + 1
        L_0x005e:
            int[] r10 = new int[r10]
            if (r7 == 0) goto L_0x0064
            r11 = 1
            goto L_0x0066
        L_0x0064:
            int r11 = r6 + 1
        L_0x0066:
            int[] r11 = new int[r11]
            if (r7 == 0) goto L_0x006c
            r12 = 0
            goto L_0x006d
        L_0x006c:
            r12 = r6
        L_0x006d:
            java.lang.String r13 = "]"
            if (r12 <= r6) goto L_0x015e
            java.lang.String r12 = "vps_max_layer_id"
            int r14 = r2.readU(r5, r12)
            java.lang.String r5 = "vps_num_layer_sets_minus1"
            int r15 = r2.readUE(r5)
            int[] r3 = new int[r3]
            r3[r8] = r14
            r3[r4] = r15
            java.lang.Class<boolean> r5 = boolean.class
            java.lang.Object r3 = java.lang.reflect.Array.newInstance(r5, r3)
            r16 = r3
            boolean[][] r16 = (boolean[][]) r16
            r3 = 1
        L_0x008e:
            if (r3 <= r15) goto L_0x012b
            java.lang.String r3 = "vps_timing_info_present_flag"
            boolean r3 = r2.readBool(r3)
            if (r3 == 0) goto L_0x010f
            r5 = 32
            java.lang.String r12 = "vps_num_units_in_tick"
            r2.readU(r5, r12)
            java.lang.String r12 = "vps_time_scale"
            r2.readU(r5, r12)
            java.lang.String r5 = "vps_poc_proportional_to_timing_flag"
            boolean r5 = r2.readBool(r5)
            if (r5 == 0) goto L_0x00b1
            java.lang.String r12 = "vps_num_ticks_poc_diff_one_minus1"
            r2.readUE(r12)
        L_0x00b1:
            java.lang.String r12 = "vps_num_hrd_parameters"
            int r12 = r2.readUE(r12)
            int[] r4 = new int[r12]
            boolean[] r8 = new boolean[r12]
            r19 = 0
            r1 = r19
        L_0x00bf:
            if (r1 < r12) goto L_0x00c4
            r19 = r3
            goto L_0x0111
        L_0x00c4:
            r19 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r20 = r5
            java.lang.String r5 = "hrd_layer_set_idx["
            r3.<init>(r5)
            r3.append(r1)
            r3.append(r13)
            java.lang.String r3 = r3.toString()
            int r3 = r2.readUE(r3)
            r4[r1] = r3
            if (r1 <= 0) goto L_0x00fd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            java.lang.String r5 = "cprms_present_flag["
            r3.<init>(r5)
            r3.append(r1)
            r3.append(r13)
            java.lang.String r3 = r3.toString()
            boolean r3 = r2.readBool(r3)
            r8[r1] = r3
            r17 = 0
            r18 = 1
            goto L_0x0103
        L_0x00fd:
            r17 = 0
            r18 = 1
            r8[r17] = r18
        L_0x0103:
            boolean r3 = r8[r1]
            r0.hrd_parameters(r3, r6, r2)
            int r1 = r1 + 1
            r3 = r19
            r5 = r20
            goto L_0x00bf
        L_0x010f:
            r19 = r3
        L_0x0111:
            java.lang.String r1 = "vps_extension_flag"
            boolean r1 = r2.readBool(r1)
            if (r1 == 0) goto L_0x0127
        L_0x011a:
            boolean r3 = r2.moreRBSPData()
            if (r3 != 0) goto L_0x0121
            goto L_0x0127
        L_0x0121:
            java.lang.String r3 = "vps_extension_data_flag"
            r2.readBool(r3)
            goto L_0x011a
        L_0x0127:
            r2.readTrailingBits()
            return
        L_0x012b:
            r17 = 0
            r18 = 1
            r1 = 0
        L_0x0130:
            if (r1 <= r14) goto L_0x013a
            int r3 = r3 + 1
            r1 = r22
            r4 = 0
            r8 = 1
            goto L_0x008e
        L_0x013a:
            r4 = r16[r3]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            java.lang.String r8 = "layer_id_included_flag["
            r5.<init>(r8)
            r5.append(r3)
            java.lang.String r8 = "]["
            r5.append(r8)
            r5.append(r1)
            r5.append(r13)
            java.lang.String r5 = r5.toString()
            boolean r5 = r2.readBool(r5)
            r4[r1] = r5
            int r1 = r1 + 1
            goto L_0x0130
        L_0x015e:
            r17 = 0
            r18 = 1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r4 = "vps_max_dec_pic_buffering_minus1["
            r1.<init>(r4)
            r1.append(r12)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            int r1 = r2.readUE(r1)
            r9[r12] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>(r4)
            r1.append(r12)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            int r1 = r2.readUE(r1)
            r10[r12] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>(r4)
            r1.append(r12)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            int r1 = r2.readUE(r1)
            r11[r12] = r1
            int r12 = r12 + 1
            r1 = r22
            r4 = 0
            r8 = 1
            goto L_0x006d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.h265.VideoParameterSet.<init>(java.nio.ByteBuffer):void");
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
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.h265.VideoParameterSet.profile_tier_level(int, com.googlecode.mp4parser.h264.read.CAVLCReader):void");
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

    public ByteBuffer toByteBuffer() {
        return this.vps;
    }
}
