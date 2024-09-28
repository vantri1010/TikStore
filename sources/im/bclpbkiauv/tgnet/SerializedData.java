package im.bclpbkiauv.tgnet;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class SerializedData extends AbstractSerializedData {
    private DataInputStream in;
    private ByteArrayInputStream inbuf;
    protected boolean isOut;
    private boolean justCalc;
    private int len;
    private DataOutputStream out;
    private ByteArrayOutputStream outbuf;

    public SerializedData() {
        this.isOut = true;
        this.justCalc = false;
        this.outbuf = new ByteArrayOutputStream();
        this.out = new DataOutputStream(this.outbuf);
    }

    public SerializedData(boolean calculate) {
        this.isOut = true;
        this.justCalc = false;
        if (!calculate) {
            this.outbuf = new ByteArrayOutputStream();
            this.out = new DataOutputStream(this.outbuf);
        }
        this.justCalc = calculate;
        this.len = 0;
    }

    public SerializedData(int size) {
        this.isOut = true;
        this.justCalc = false;
        this.outbuf = new ByteArrayOutputStream(size);
        this.out = new DataOutputStream(this.outbuf);
    }

    public SerializedData(byte[] data) {
        this.isOut = true;
        this.justCalc = false;
        this.isOut = false;
        this.inbuf = new ByteArrayInputStream(data);
        this.in = new DataInputStream(this.inbuf);
        this.len = 0;
    }

    public void cleanup() {
        try {
            if (this.inbuf != null) {
                this.inbuf.close();
                this.inbuf = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        try {
            if (this.outbuf != null) {
                this.outbuf.close();
                this.outbuf = null;
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        try {
            if (this.out != null) {
                this.out.close();
                this.out = null;
            }
        } catch (Exception e4) {
            FileLog.e((Throwable) e4);
        }
    }

    public SerializedData(File file) throws Exception {
        this.isOut = true;
        this.justCalc = false;
        FileInputStream is = new FileInputStream(file);
        byte[] data = new byte[((int) file.length())];
        new DataInputStream(is).readFully(data);
        is.close();
        this.isOut = false;
        this.inbuf = new ByteArrayInputStream(data);
        this.in = new DataInputStream(this.inbuf);
    }

    public void writeInt32(int x) {
        if (!this.justCalc) {
            writeInt32(x, this.out);
        } else {
            this.len += 4;
        }
    }

    private void writeInt32(int x, DataOutputStream out2) {
        int i = 0;
        while (i < 4) {
            try {
                out2.write(x >> (i * 8));
                i++;
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("write int32 error");
                    return;
                }
                return;
            }
        }
    }

    public void writeInt64(long i) {
        if (!this.justCalc) {
            writeInt64(i, this.out);
        } else {
            this.len += 8;
        }
    }

    private void writeInt64(long x, DataOutputStream out2) {
        int i = 0;
        while (i < 8) {
            try {
                out2.write((int) (x >> (i * 8)));
                i++;
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("write int64 error");
                    return;
                }
                return;
            }
        }
    }

    public void writeBool(boolean value) {
        if (this.justCalc) {
            this.len += 4;
        } else if (value) {
            writeInt32(-1720552011);
        } else {
            writeInt32(-1132882121);
        }
    }

    public void writeBytes(byte[] b) {
        try {
            if (!this.justCalc) {
                this.out.write(b);
            } else {
                this.len += b.length;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write raw error");
            }
        }
    }

    public void writeBytes(byte[] b, int offset, int count) {
        try {
            if (!this.justCalc) {
                this.out.write(b, offset, count);
            } else {
                this.len += count;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write bytes error");
            }
        }
    }

    public void writeByte(int i) {
        try {
            if (!this.justCalc) {
                this.out.writeByte((byte) i);
            } else {
                this.len++;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte error");
            }
        }
    }

    public void writeByte(byte b) {
        try {
            if (!this.justCalc) {
                this.out.writeByte(b);
            } else {
                this.len++;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte error");
            }
        }
    }

    public void writeByteArray(byte[] b) {
        try {
            if (b.length <= 253) {
                if (!this.justCalc) {
                    this.out.write(b.length);
                } else {
                    this.len++;
                }
            } else if (!this.justCalc) {
                this.out.write(254);
                this.out.write(b.length);
                this.out.write(b.length >> 8);
                this.out.write(b.length >> 16);
            } else {
                this.len += 4;
            }
            if (!this.justCalc) {
                this.out.write(b);
            } else {
                this.len += b.length;
            }
            for (int i = b.length <= 253 ? 1 : 4; (b.length + i) % 4 != 0; i++) {
                if (!this.justCalc) {
                    this.out.write(0);
                } else {
                    this.len++;
                }
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte array error");
            }
        }
    }

    public void writeString(String s) {
        try {
            writeByteArray(s.getBytes("UTF-8"));
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write string error");
            }
        }
    }

    public void writeByteArray(byte[] b, int offset, int count) {
        if (count <= 253) {
            try {
                if (!this.justCalc) {
                    this.out.write(count);
                } else {
                    this.len++;
                }
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("write byte array error");
                    return;
                }
                return;
            }
        } else if (!this.justCalc) {
            this.out.write(254);
            this.out.write(count);
            this.out.write(count >> 8);
            this.out.write(count >> 16);
        } else {
            this.len += 4;
        }
        if (!this.justCalc) {
            this.out.write(b, offset, count);
        } else {
            this.len += count;
        }
        for (int i = count <= 253 ? 1 : 4; (count + i) % 4 != 0; i++) {
            if (!this.justCalc) {
                this.out.write(0);
            } else {
                this.len++;
            }
        }
    }

    public void writeDouble(double d) {
        try {
            writeInt64(Double.doubleToRawLongBits(d));
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write double error");
            }
        }
    }

    public int length() {
        if (!this.justCalc) {
            return this.isOut ? this.outbuf.size() : this.inbuf.available();
        }
        return this.len;
    }

    /* access modifiers changed from: protected */
    public void set(byte[] newData) {
        this.isOut = false;
        this.inbuf = new ByteArrayInputStream(newData);
        this.in = new DataInputStream(this.inbuf);
    }

    public byte[] toByteArray() {
        return this.outbuf.toByteArray();
    }

    public void skip(int count) {
        if (count != 0) {
            if (!this.justCalc) {
                DataInputStream dataInputStream = this.in;
                if (dataInputStream != null) {
                    try {
                        dataInputStream.skipBytes(count);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } else {
                this.len += count;
            }
        }
    }

    public int getPosition() {
        return this.len;
    }

    public boolean readBool(boolean exception) {
        int consructor = readInt32(exception);
        if (consructor == -1720552011) {
            return true;
        }
        if (consructor == -1132882121) {
            return false;
        }
        if (!exception) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Not bool value!");
            }
            return false;
        }
        throw new RuntimeException("Not bool value!");
    }

    public void readBytes(byte[] b, boolean exception) {
        try {
            this.in.read(b);
            this.len += b.length;
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read bytes error", e);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e("read bytes error");
            }
        }
    }

    public byte[] readData(int count, boolean exception) {
        byte[] arr = new byte[count];
        readBytes(arr, exception);
        return arr;
    }

    public String readString(boolean exception) {
        int sl = 1;
        try {
            int l = this.in.read();
            this.len++;
            if (l >= 254) {
                l = this.in.read() | (this.in.read() << 8) | (this.in.read() << 16);
                this.len += 3;
                sl = 4;
            }
            byte[] b = new byte[l];
            this.in.read(b);
            this.len++;
            for (int i = sl; (l + i) % 4 != 0; i++) {
                this.in.read();
                this.len++;
            }
            return new String(b, "UTF-8");
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read string error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return null;
            } else {
                FileLog.e("read string error");
                return null;
            }
        }
    }

    public byte[] readByteArray(boolean exception) {
        int sl = 1;
        try {
            int l = this.in.read();
            this.len++;
            if (l >= 254) {
                l = this.in.read() | (this.in.read() << 8) | (this.in.read() << 16);
                this.len += 3;
                sl = 4;
            }
            byte[] b = new byte[l];
            this.in.read(b);
            this.len++;
            for (int i = sl; (l + i) % 4 != 0; i++) {
                this.in.read();
                this.len++;
            }
            return b;
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read byte array error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return null;
            } else {
                FileLog.e("read byte array error");
                return null;
            }
        }
    }

    public double readDouble(boolean exception) {
        try {
            return Double.longBitsToDouble(readInt64(exception));
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read double error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            } else {
                FileLog.e("read double error");
                return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            }
        }
    }

    public int readInt32(boolean exception) {
        int i = 0;
        int j = 0;
        while (j < 4) {
            try {
                i |= this.in.read() << (j * 8);
                this.len++;
                j++;
            } catch (Exception e) {
                if (exception) {
                    throw new RuntimeException("read int32 error", e);
                } else if (!BuildVars.LOGS_ENABLED) {
                    return 0;
                } else {
                    FileLog.e("read int32 error");
                    return 0;
                }
            }
        }
        return i;
    }

    public long readInt64(boolean exception) {
        long i = 0;
        int j = 0;
        while (j < 8) {
            try {
                i |= ((long) this.in.read()) << (j * 8);
                this.len++;
                j++;
            } catch (Exception e) {
                if (exception) {
                    throw new RuntimeException("read int64 error", e);
                } else if (!BuildVars.LOGS_ENABLED) {
                    return 0;
                } else {
                    FileLog.e("read int64 error");
                    return 0;
                }
            }
        }
        return i;
    }

    public void writeByteBuffer(NativeByteBuffer buffer) {
    }

    public NativeByteBuffer readByteBuffer(boolean exception) {
        return null;
    }

    public int remaining() {
        try {
            return this.in.available();
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}
