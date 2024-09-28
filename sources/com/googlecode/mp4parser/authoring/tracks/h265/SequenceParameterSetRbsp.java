package com.googlecode.mp4parser.authoring.tracks.h265;

import com.googlecode.mp4parser.h264.read.CAVLCReader;
import java.io.IOException;
import java.io.InputStream;

public class SequenceParameterSetRbsp {
    public SequenceParameterSetRbsp(InputStream is) throws IOException {
        CAVLCReader bsr = new CAVLCReader(is);
        bsr.readNBit(4, "sps_video_parameter_set_id");
        int sps_max_sub_layers_minus1 = (int) bsr.readNBit(3, "sps_max_sub_layers_minus1");
        bsr.readBool("sps_temporal_id_nesting_flag");
        profile_tier_level(sps_max_sub_layers_minus1, bsr);
        bsr.readUE("sps_seq_parameter_set_id");
        if (bsr.readUE("chroma_format_idc") == 3) {
            bsr.read1Bit();
            bsr.readUE("pic_width_in_luma_samples");
            bsr.readUE("pic_width_in_luma_samples");
            if (bsr.readBool("conformance_window_flag")) {
                bsr.readUE("conf_win_left_offset");
                bsr.readUE("conf_win_right_offset");
                bsr.readUE("conf_win_top_offset");
                bsr.readUE("conf_win_bottom_offset");
            }
        }
        bsr.readUE("bit_depth_luma_minus8");
        bsr.readUE("bit_depth_chroma_minus8");
        bsr.readUE("log2_max_pic_order_cnt_lsb_minus4");
        boolean sps_sub_layer_ordering_info_present_flag = bsr.readBool("sps_sub_layer_ordering_info_present_flag");
        int i = 0;
        int j = (sps_max_sub_layers_minus1 - (sps_sub_layer_ordering_info_present_flag ? 0 : sps_max_sub_layers_minus1)) + 1;
        int[] sps_max_dec_pic_buffering_minus1 = new int[j];
        int[] sps_max_num_reorder_pics = new int[j];
        int[] sps_max_latency_increase_plus1 = new int[j];
        for (i = !sps_sub_layer_ordering_info_present_flag ? sps_max_sub_layers_minus1 : i; i <= sps_max_sub_layers_minus1; i++) {
            sps_max_dec_pic_buffering_minus1[i] = bsr.readUE("sps_max_dec_pic_buffering_minus1[" + i + "]");
            sps_max_num_reorder_pics[i] = bsr.readUE("sps_max_num_reorder_pics[" + i + "]");
            sps_max_latency_increase_plus1[i] = bsr.readUE("sps_max_latency_increase_plus1[" + i + "]");
        }
        bsr.readUE("log2_min_luma_coding_block_size_minus3");
        bsr.readUE("log2_diff_max_min_luma_coding_block_size");
        bsr.readUE("log2_min_transform_block_size_minus2");
        bsr.readUE("log2_diff_max_min_transform_block_size");
        bsr.readUE("max_transform_hierarchy_depth_inter");
        bsr.readUE("max_transform_hierarchy_depth_intra");
        if (bsr.readBool("scaling_list_enabled_flag") && bsr.readBool("sps_scaling_list_data_present_flag")) {
            scaling_list_data(bsr);
        }
        bsr.readBool("amp_enabled_flag");
        bsr.readBool("sample_adaptive_offset_enabled_flag");
        if (bsr.readBool("pcm_enabled_flag")) {
            bsr.readNBit(4, "pcm_sample_bit_depth_luma_minus1");
            bsr.readNBit(4, "pcm_sample_bit_depth_chroma_minus1");
            bsr.readUE("log2_min_pcm_luma_coding_block_size_minus3");
        }
    }

    private void scaling_list_data(CAVLCReader bsr) throws IOException {
        boolean[][] scaling_list_pred_mode_flag = new boolean[4][];
        int[][] scaling_list_pred_matrix_id_delta = new int[4][];
        int[][] scaling_list_dc_coef_minus8 = new int[2][];
        int[][][] ScalingList = new int[4][][];
        int sizeId = 0;
        while (sizeId < 4) {
            int matrixId = 0;
            while (true) {
                int i = 6;
                if (matrixId >= (sizeId == 3 ? 2 : 6)) {
                    break;
                }
                scaling_list_pred_mode_flag[sizeId] = new boolean[(sizeId == 3 ? 2 : 6)];
                scaling_list_pred_matrix_id_delta[sizeId] = new int[(sizeId == 3 ? 2 : 6)];
                if (sizeId == 3) {
                    i = 2;
                }
                ScalingList[sizeId] = new int[i][];
                scaling_list_pred_mode_flag[sizeId][matrixId] = bsr.readBool();
                if (!scaling_list_pred_mode_flag[sizeId][matrixId]) {
                    int[] iArr = scaling_list_pred_matrix_id_delta[sizeId];
                    iArr[matrixId] = bsr.readUE("scaling_list_pred_matrix_id_delta[" + sizeId + "][" + matrixId + "]");
                } else {
                    int nextCoef = 8;
                    int coefNum = Math.min(64, 1 << ((sizeId << 1) + 4));
                    if (sizeId > 1) {
                        int[] iArr2 = scaling_list_dc_coef_minus8[sizeId - 2];
                        iArr2[matrixId] = bsr.readSE("scaling_list_dc_coef_minus8[" + sizeId + "- 2][" + matrixId + "]");
                        nextCoef = scaling_list_dc_coef_minus8[sizeId + -2][matrixId] + 8;
                    }
                    ScalingList[sizeId][matrixId] = new int[coefNum];
                    for (int i2 = 0; i2 < coefNum; i2++) {
                        nextCoef = ((nextCoef + bsr.readSE("scaling_list_delta_coef ")) + 256) % 256;
                        ScalingList[sizeId][matrixId][i2] = nextCoef;
                    }
                }
                matrixId++;
            }
            sizeId++;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: boolean[][]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void profile_tier_level(int r22, com.googlecode.mp4parser.h264.read.CAVLCReader r23) throws java.io.IOException {
        /*
            r21 = this;
            r0 = r22
            r1 = r23
            r2 = 2
            java.lang.String r3 = "general_profile_space"
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
            if (r6 < r4) goto L_0x01e5
            java.lang.String r6 = "general_progressive_source_flag"
            r1.readBool(r6)
            java.lang.String r6 = "general_interlaced_source_flag"
            r1.readBool(r6)
            java.lang.String r6 = "general_non_packed_constraint_flag"
            r1.readBool(r6)
            java.lang.String r6 = "general_frame_only_constraint_flag"
            r1.readBool(r6)
            r7 = 44
            java.lang.String r6 = "general_reserved_zero_44bits"
            r1.readNBit(r7, r6)
            r23.readByte()
            boolean[] r8 = new boolean[r0]
            boolean[] r9 = new boolean[r0]
            r6 = 0
        L_0x003f:
            java.lang.String r10 = "]"
            if (r6 < r0) goto L_0x01a7
            r11 = 8
            if (r0 <= 0) goto L_0x0068
            int[] r6 = new int[r11]
            r12 = r22
        L_0x004b:
            if (r12 < r11) goto L_0x004e
            goto L_0x0068
        L_0x004e:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            java.lang.String r14 = "reserved_zero_2bits["
            r13.<init>(r14)
            r13.append(r12)
            r13.append(r10)
            java.lang.String r13 = r13.toString()
            int r13 = r1.readU(r2, r13)
            r6[r12] = r13
            int r12 = r12 + 1
            goto L_0x004b
        L_0x0068:
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
            boolean[] r11 = new boolean[r0]
            boolean[] r7 = new boolean[r0]
            boolean[] r4 = new boolean[r0]
            long[] r3 = new long[r0]
            int[] r2 = new int[r0]
            r17 = 0
            r18 = r5
            r5 = r17
        L_0x0091:
            if (r5 < r0) goto L_0x0094
            return
        L_0x0094:
            boolean r17 = r8[r5]
            if (r17 == 0) goto L_0x0179
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r17 = r8
            java.lang.String r8 = "sub_layer_profile_space["
            r0.<init>(r8)
            r0.append(r5)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            r8 = 2
            int r0 = r1.readU(r8, r0)
            r12[r5] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r8 = "sub_layer_tier_flag["
            r0.<init>(r8)
            r0.append(r5)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r13[r5] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r8 = "sub_layer_profile_idc["
            r0.<init>(r8)
            r0.append(r5)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            r8 = 5
            int r0 = r1.readU(r8, r0)
            r14[r5] = r0
            r0 = 0
        L_0x00e2:
            r8 = 32
            if (r0 < r8) goto L_0x014d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r8 = "sub_layer_progressive_source_flag["
            r0.<init>(r8)
            r0.append(r5)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r6[r5] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r8 = "sub_layer_interlaced_source_flag["
            r0.<init>(r8)
            r0.append(r5)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r11[r5] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r8 = "sub_layer_non_packed_constraint_flag["
            r0.<init>(r8)
            r0.append(r5)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r7[r5] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r8 = "sub_layer_frame_only_constraint_flag["
            r0.<init>(r8)
            r0.append(r5)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r4[r5] = r0
            r8 = 44
            long r19 = r1.readNBit(r8)
            r3[r5] = r19
            r20 = r3
            goto L_0x017d
        L_0x014d:
            r8 = 44
            r16 = r15[r5]
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r20 = r3
            java.lang.String r3 = "sub_layer_profile_compatibility_flag["
            r8.<init>(r3)
            r8.append(r5)
            java.lang.String r3 = "]["
            r8.append(r3)
            r8.append(r0)
            r8.append(r10)
            java.lang.String r3 = r8.toString()
            boolean r3 = r1.readBool(r3)
            r16[r0] = r3
            int r0 = r0 + 1
            r3 = r20
            r8 = 5
            goto L_0x00e2
        L_0x0179:
            r20 = r3
            r17 = r8
        L_0x017d:
            boolean r0 = r9[r5]
            if (r0 == 0) goto L_0x019b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r3 = "sub_layer_level_idc["
            r0.<init>(r3)
            r0.append(r5)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            r3 = 8
            int r0 = r1.readU(r3, r0)
            r2[r5] = r0
            goto L_0x019d
        L_0x019b:
            r3 = 8
        L_0x019d:
            int r5 = r5 + 1
            r0 = r22
            r8 = r17
            r3 = r20
            goto L_0x0091
        L_0x01a7:
            r18 = r5
            r17 = r8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r2 = "sub_layer_profile_present_flag["
            r0.<init>(r2)
            r0.append(r6)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r17[r6] = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r2 = "sub_layer_level_present_flag["
            r0.<init>(r2)
            r0.append(r6)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            boolean r0 = r1.readBool(r0)
            r9[r6] = r0
            int r6 = r6 + 1
            r0 = r22
            r2 = 2
            r3 = 5
            r4 = 32
            r7 = 44
            goto L_0x003f
        L_0x01e5:
            r18 = r5
            boolean r0 = r23.readBool()
            r18[r6] = r0
            int r6 = r6 + 1
            r0 = r22
            r2 = 2
            r3 = 5
            r4 = 32
            goto L_0x001a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.h265.SequenceParameterSetRbsp.profile_tier_level(int, com.googlecode.mp4parser.h264.read.CAVLCReader):void");
    }
}
