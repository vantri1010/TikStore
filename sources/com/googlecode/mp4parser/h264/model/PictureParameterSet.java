package com.googlecode.mp4parser.h264.model;

import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.h264.write.CAVLCWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class PictureParameterSet extends BitstreamElement {
    public boolean bottom_field_pic_order_in_frame_present_flag;
    public int[] bottom_right;
    public int chroma_qp_index_offset;
    public boolean constrained_intra_pred_flag;
    public boolean deblocking_filter_control_present_flag;
    public boolean entropy_coding_mode_flag;
    public PPSExt extended;
    public int num_ref_idx_l0_active_minus1;
    public int num_ref_idx_l1_active_minus1;
    public int num_slice_groups_minus1;
    public int pic_init_qp_minus26;
    public int pic_init_qs_minus26;
    public int pic_parameter_set_id;
    public boolean redundant_pic_cnt_present_flag;
    public int[] run_length_minus1;
    public int seq_parameter_set_id;
    public boolean slice_group_change_direction_flag;
    public int slice_group_change_rate_minus1;
    public int[] slice_group_id;
    public int slice_group_map_type;
    public int[] top_left;
    public int weighted_bipred_idc;
    public boolean weighted_pred_flag;

    public static class PPSExt {
        public boolean[] pic_scaling_list_present_flag;
        public ScalingMatrix scalindMatrix = new ScalingMatrix();
        public int second_chroma_qp_index_offset;
        public boolean transform_8x8_mode_flag;

        public String toString() {
            return "PPSExt{transform_8x8_mode_flag=" + this.transform_8x8_mode_flag + ", scalindMatrix=" + this.scalindMatrix + ", second_chroma_qp_index_offset=" + this.second_chroma_qp_index_offset + ", pic_scaling_list_present_flag=" + this.pic_scaling_list_present_flag + '}';
        }
    }

    public static PictureParameterSet read(byte[] b) throws IOException {
        return read((InputStream) new ByteArrayInputStream(b));
    }

    public static PictureParameterSet read(InputStream is) throws IOException {
        int i;
        int NumberBitsPerSliceGroupId;
        CAVLCReader reader = new CAVLCReader(is);
        PictureParameterSet pps = new PictureParameterSet();
        pps.pic_parameter_set_id = reader.readUE("PPS: pic_parameter_set_id");
        pps.seq_parameter_set_id = reader.readUE("PPS: seq_parameter_set_id");
        pps.entropy_coding_mode_flag = reader.readBool("PPS: entropy_coding_mode_flag");
        pps.bottom_field_pic_order_in_frame_present_flag = reader.readBool("PPS: pic_order_present_flag");
        int readUE = reader.readUE("PPS: num_slice_groups_minus1");
        pps.num_slice_groups_minus1 = readUE;
        if (readUE > 0) {
            int iGroup = reader.readUE("PPS: slice_group_map_type");
            pps.slice_group_map_type = iGroup;
            int i2 = pps.num_slice_groups_minus1;
            pps.top_left = new int[(i2 + 1)];
            pps.bottom_right = new int[(i2 + 1)];
            pps.run_length_minus1 = new int[(i2 + 1)];
            if (iGroup == 0) {
                for (int iGroup2 = 0; iGroup2 <= pps.num_slice_groups_minus1; iGroup2++) {
                    pps.run_length_minus1[iGroup2] = reader.readUE("PPS: run_length_minus1");
                }
            } else if (iGroup == 2) {
                for (int iGroup3 = 0; iGroup3 < pps.num_slice_groups_minus1; iGroup3++) {
                    pps.top_left[iGroup3] = reader.readUE("PPS: top_left");
                    pps.bottom_right[iGroup3] = reader.readUE("PPS: bottom_right");
                }
            } else if (iGroup == 3 || iGroup == 4 || iGroup == 5) {
                pps.slice_group_change_direction_flag = reader.readBool("PPS: slice_group_change_direction_flag");
                pps.slice_group_change_rate_minus1 = reader.readUE("PPS: slice_group_change_rate_minus1");
            } else if (iGroup == 6) {
                if (i2 + 1 > 4) {
                    NumberBitsPerSliceGroupId = 3;
                } else if (i2 + 1 > 2) {
                    NumberBitsPerSliceGroupId = 2;
                } else {
                    NumberBitsPerSliceGroupId = 1;
                }
                int pic_size_in_map_units_minus1 = reader.readUE("PPS: pic_size_in_map_units_minus1");
                pps.slice_group_id = new int[(pic_size_in_map_units_minus1 + 1)];
                for (int i3 = 0; i3 <= pic_size_in_map_units_minus1; i3++) {
                    int[] iArr = pps.slice_group_id;
                    iArr[i3] = reader.readU(NumberBitsPerSliceGroupId, "PPS: slice_group_id [" + i3 + "]f");
                }
            }
        }
        pps.num_ref_idx_l0_active_minus1 = reader.readUE("PPS: num_ref_idx_l0_active_minus1");
        pps.num_ref_idx_l1_active_minus1 = reader.readUE("PPS: num_ref_idx_l1_active_minus1");
        pps.weighted_pred_flag = reader.readBool("PPS: weighted_pred_flag");
        pps.weighted_bipred_idc = (int) reader.readNBit(2, "PPS: weighted_bipred_idc");
        pps.pic_init_qp_minus26 = reader.readSE("PPS: pic_init_qp_minus26");
        pps.pic_init_qs_minus26 = reader.readSE("PPS: pic_init_qs_minus26");
        pps.chroma_qp_index_offset = reader.readSE("PPS: chroma_qp_index_offset");
        pps.deblocking_filter_control_present_flag = reader.readBool("PPS: deblocking_filter_control_present_flag");
        pps.constrained_intra_pred_flag = reader.readBool("PPS: constrained_intra_pred_flag");
        pps.redundant_pic_cnt_present_flag = reader.readBool("PPS: redundant_pic_cnt_present_flag");
        if (reader.moreRBSPData()) {
            PPSExt pPSExt = new PPSExt();
            pps.extended = pPSExt;
            pPSExt.transform_8x8_mode_flag = reader.readBool("PPS: transform_8x8_mode_flag");
            if (reader.readBool("PPS: pic_scaling_matrix_present_flag")) {
                int i4 = 0;
                while (true) {
                    if (pps.extended.transform_8x8_mode_flag) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    if (i4 >= (i * 2) + 6) {
                        break;
                    }
                    if (reader.readBool("PPS: pic_scaling_list_present_flag")) {
                        pps.extended.scalindMatrix.ScalingList4x4 = new ScalingList[8];
                        pps.extended.scalindMatrix.ScalingList8x8 = new ScalingList[8];
                        if (i4 < 6) {
                            pps.extended.scalindMatrix.ScalingList4x4[i4] = ScalingList.read(reader, 16);
                        } else {
                            pps.extended.scalindMatrix.ScalingList8x8[i4 - 6] = ScalingList.read(reader, 64);
                        }
                    }
                    i4++;
                }
            }
            pps.extended.second_chroma_qp_index_offset = reader.readSE("PPS: second_chroma_qp_index_offset");
        }
        reader.readTrailingBits();
        return pps;
    }

    public void write(OutputStream out) throws IOException {
        int i;
        int NumberBitsPerSliceGroupId;
        CAVLCWriter writer = new CAVLCWriter(out);
        writer.writeUE(this.pic_parameter_set_id, "PPS: pic_parameter_set_id");
        writer.writeUE(this.seq_parameter_set_id, "PPS: seq_parameter_set_id");
        writer.writeBool(this.entropy_coding_mode_flag, "PPS: entropy_coding_mode_flag");
        writer.writeBool(this.bottom_field_pic_order_in_frame_present_flag, "PPS: pic_order_present_flag");
        writer.writeUE(this.num_slice_groups_minus1, "PPS: num_slice_groups_minus1");
        if (this.num_slice_groups_minus1 > 0) {
            writer.writeUE(this.slice_group_map_type, "PPS: slice_group_map_type");
            int[] top_left2 = new int[1];
            int[] bottom_right2 = new int[1];
            int[] run_length_minus12 = new int[1];
            int iGroup = this.slice_group_map_type;
            if (iGroup == 0) {
                for (int iGroup2 = 0; iGroup2 <= this.num_slice_groups_minus1; iGroup2++) {
                    writer.writeUE(run_length_minus12[iGroup2], "PPS: ");
                }
            } else if (iGroup == 2) {
                for (int iGroup3 = 0; iGroup3 < this.num_slice_groups_minus1; iGroup3++) {
                    writer.writeUE(top_left2[iGroup3], "PPS: ");
                    writer.writeUE(bottom_right2[iGroup3], "PPS: ");
                }
            } else if (iGroup == 3 || iGroup == 4 || iGroup == 5) {
                writer.writeBool(this.slice_group_change_direction_flag, "PPS: slice_group_change_direction_flag");
                writer.writeUE(this.slice_group_change_rate_minus1, "PPS: slice_group_change_rate_minus1");
            } else if (iGroup == 6) {
                int NumberBitsPerSliceGroupId2 = this.num_slice_groups_minus1;
                if (NumberBitsPerSliceGroupId2 + 1 > 4) {
                    NumberBitsPerSliceGroupId = 3;
                } else if (NumberBitsPerSliceGroupId2 + 1 > 2) {
                    NumberBitsPerSliceGroupId = 2;
                } else {
                    NumberBitsPerSliceGroupId = 1;
                }
                writer.writeUE(this.slice_group_id.length, "PPS: ");
                int i2 = 0;
                while (true) {
                    int[] iArr = this.slice_group_id;
                    if (i2 > iArr.length) {
                        break;
                    }
                    writer.writeU(iArr[i2], NumberBitsPerSliceGroupId);
                    i2++;
                }
            }
        }
        writer.writeUE(this.num_ref_idx_l0_active_minus1, "PPS: num_ref_idx_l0_active_minus1");
        writer.writeUE(this.num_ref_idx_l1_active_minus1, "PPS: num_ref_idx_l1_active_minus1");
        writer.writeBool(this.weighted_pred_flag, "PPS: weighted_pred_flag");
        writer.writeNBit((long) this.weighted_bipred_idc, 2, "PPS: weighted_bipred_idc");
        writer.writeSE(this.pic_init_qp_minus26, "PPS: pic_init_qp_minus26");
        writer.writeSE(this.pic_init_qs_minus26, "PPS: pic_init_qs_minus26");
        writer.writeSE(this.chroma_qp_index_offset, "PPS: chroma_qp_index_offset");
        writer.writeBool(this.deblocking_filter_control_present_flag, "PPS: deblocking_filter_control_present_flag");
        writer.writeBool(this.constrained_intra_pred_flag, "PPS: constrained_intra_pred_flag");
        writer.writeBool(this.redundant_pic_cnt_present_flag, "PPS: redundant_pic_cnt_present_flag");
        PPSExt pPSExt = this.extended;
        if (pPSExt != null) {
            writer.writeBool(pPSExt.transform_8x8_mode_flag, "PPS: transform_8x8_mode_flag");
            writer.writeBool(this.extended.scalindMatrix != null, "PPS: scalindMatrix");
            if (this.extended.scalindMatrix != null) {
                int i3 = 0;
                while (true) {
                    if (this.extended.transform_8x8_mode_flag) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    if (i3 >= (i * 2) + 6) {
                        break;
                    }
                    if (i3 < 6) {
                        writer.writeBool(this.extended.scalindMatrix.ScalingList4x4[i3] != null, "PPS: ");
                        if (this.extended.scalindMatrix.ScalingList4x4[i3] != null) {
                            this.extended.scalindMatrix.ScalingList4x4[i3].write(writer);
                        }
                    } else {
                        writer.writeBool(this.extended.scalindMatrix.ScalingList8x8[i3 + -6] != null, "PPS: ");
                        if (this.extended.scalindMatrix.ScalingList8x8[i3 - 6] != null) {
                            this.extended.scalindMatrix.ScalingList8x8[i3 - 6].write(writer);
                        }
                    }
                    i3++;
                }
            }
            writer.writeSE(this.extended.second_chroma_qp_index_offset, "PPS: ");
        }
        writer.writeTrailingBits();
    }

    public int hashCode() {
        int i = 1231;
        int result = ((((((((((1 * 31) + Arrays.hashCode(this.bottom_right)) * 31) + this.chroma_qp_index_offset) * 31) + (this.constrained_intra_pred_flag ? 1231 : 1237)) * 31) + (this.deblocking_filter_control_present_flag ? 1231 : 1237)) * 31) + (this.entropy_coding_mode_flag ? 1231 : 1237)) * 31;
        PPSExt pPSExt = this.extended;
        int result2 = (((((((((((((((((((((((((((((((((result + (pPSExt == null ? 0 : pPSExt.hashCode())) * 31) + this.num_ref_idx_l0_active_minus1) * 31) + this.num_ref_idx_l1_active_minus1) * 31) + this.num_slice_groups_minus1) * 31) + this.pic_init_qp_minus26) * 31) + this.pic_init_qs_minus26) * 31) + (this.bottom_field_pic_order_in_frame_present_flag ? 1231 : 1237)) * 31) + this.pic_parameter_set_id) * 31) + (this.redundant_pic_cnt_present_flag ? 1231 : 1237)) * 31) + Arrays.hashCode(this.run_length_minus1)) * 31) + this.seq_parameter_set_id) * 31) + (this.slice_group_change_direction_flag ? 1231 : 1237)) * 31) + this.slice_group_change_rate_minus1) * 31) + Arrays.hashCode(this.slice_group_id)) * 31) + this.slice_group_map_type) * 31) + Arrays.hashCode(this.top_left)) * 31) + this.weighted_bipred_idc) * 31;
        if (!this.weighted_pred_flag) {
            i = 1237;
        }
        return result2 + i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PictureParameterSet other = (PictureParameterSet) obj;
        if (!Arrays.equals(this.bottom_right, other.bottom_right) || this.chroma_qp_index_offset != other.chroma_qp_index_offset || this.constrained_intra_pred_flag != other.constrained_intra_pred_flag || this.deblocking_filter_control_present_flag != other.deblocking_filter_control_present_flag || this.entropy_coding_mode_flag != other.entropy_coding_mode_flag) {
            return false;
        }
        PPSExt pPSExt = this.extended;
        if (pPSExt == null) {
            if (other.extended != null) {
                return false;
            }
        } else if (!pPSExt.equals(other.extended)) {
            return false;
        }
        if (this.num_ref_idx_l0_active_minus1 == other.num_ref_idx_l0_active_minus1 && this.num_ref_idx_l1_active_minus1 == other.num_ref_idx_l1_active_minus1 && this.num_slice_groups_minus1 == other.num_slice_groups_minus1 && this.pic_init_qp_minus26 == other.pic_init_qp_minus26 && this.pic_init_qs_minus26 == other.pic_init_qs_minus26 && this.bottom_field_pic_order_in_frame_present_flag == other.bottom_field_pic_order_in_frame_present_flag && this.pic_parameter_set_id == other.pic_parameter_set_id && this.redundant_pic_cnt_present_flag == other.redundant_pic_cnt_present_flag && Arrays.equals(this.run_length_minus1, other.run_length_minus1) && this.seq_parameter_set_id == other.seq_parameter_set_id && this.slice_group_change_direction_flag == other.slice_group_change_direction_flag && this.slice_group_change_rate_minus1 == other.slice_group_change_rate_minus1 && Arrays.equals(this.slice_group_id, other.slice_group_id) && this.slice_group_map_type == other.slice_group_map_type && Arrays.equals(this.top_left, other.top_left) && this.weighted_bipred_idc == other.weighted_bipred_idc && this.weighted_pred_flag == other.weighted_pred_flag) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "PictureParameterSet{\n       entropy_coding_mode_flag=" + this.entropy_coding_mode_flag + ",\n       num_ref_idx_l0_active_minus1=" + this.num_ref_idx_l0_active_minus1 + ",\n       num_ref_idx_l1_active_minus1=" + this.num_ref_idx_l1_active_minus1 + ",\n       slice_group_change_rate_minus1=" + this.slice_group_change_rate_minus1 + ",\n       pic_parameter_set_id=" + this.pic_parameter_set_id + ",\n       seq_parameter_set_id=" + this.seq_parameter_set_id + ",\n       pic_order_present_flag=" + this.bottom_field_pic_order_in_frame_present_flag + ",\n       num_slice_groups_minus1=" + this.num_slice_groups_minus1 + ",\n       slice_group_map_type=" + this.slice_group_map_type + ",\n       weighted_pred_flag=" + this.weighted_pred_flag + ",\n       weighted_bipred_idc=" + this.weighted_bipred_idc + ",\n       pic_init_qp_minus26=" + this.pic_init_qp_minus26 + ",\n       pic_init_qs_minus26=" + this.pic_init_qs_minus26 + ",\n       chroma_qp_index_offset=" + this.chroma_qp_index_offset + ",\n       deblocking_filter_control_present_flag=" + this.deblocking_filter_control_present_flag + ",\n       constrained_intra_pred_flag=" + this.constrained_intra_pred_flag + ",\n       redundant_pic_cnt_present_flag=" + this.redundant_pic_cnt_present_flag + ",\n       top_left=" + this.top_left + ",\n       bottom_right=" + this.bottom_right + ",\n       run_length_minus1=" + this.run_length_minus1 + ",\n       slice_group_change_direction_flag=" + this.slice_group_change_direction_flag + ",\n       slice_group_id=" + this.slice_group_id + ",\n       extended=" + this.extended + '}';
    }
}
