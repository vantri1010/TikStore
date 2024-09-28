package io.openinstall.sdk;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class as implements Cloneable {
    private final long a;
    private final List<ar<Integer, a>> b = new ArrayList();

    private static class a {
        byte[] a;

        public a(byte[] bArr) {
            this.a = bArr;
        }

        public byte[] a() {
            byte[] bArr = this.a;
            int length = bArr.length - 12;
            byte[] bArr2 = new byte[length];
            System.arraycopy(bArr, 12, bArr2, 0, length);
            return bArr2;
        }
    }

    public as(long j) {
        this.a = j;
    }

    private byte[] a(int i) {
        for (ar next : this.b) {
            if (((Integer) next.a).intValue() == i) {
                return ((a) next.b).a();
            }
        }
        return null;
    }

    private void b(int i) {
        Iterator<ar<Integer, a>> it = this.b.iterator();
        while (it.hasNext()) {
            if (((Integer) it.next().a).intValue() == i) {
                it.remove();
            }
        }
    }

    public long a() {
        return this.a + c();
    }

    public void a(int i, byte[] bArr) {
        int length = bArr.length + 8 + 4;
        byte[] bArr2 = new byte[length];
        ByteBuffer order = ByteBuffer.wrap(bArr2).order(ByteOrder.LITTLE_ENDIAN);
        order.putLong((long) (length - 8)).putInt(i);
        order.put(bArr);
        ar arVar = new ar(Integer.valueOf(i), new a(bArr2));
        ListIterator<ar<Integer, a>> listIterator = this.b.listIterator();
        while (listIterator.hasNext()) {
            if (((Integer) listIterator.next().a).intValue() == i) {
                listIterator.set(arVar);
                return;
            }
        }
        this.b.add(arVar);
    }

    public void a(byte[] bArr) {
        if (bArr == null) {
            b(987894612);
        } else {
            a(987894612, bArr);
        }
    }

    public long b() {
        return this.a;
    }

    public long c() {
        long j = 32;
        for (ar<Integer, a> arVar : this.b) {
            j += (long) ((a) arVar.b).a.length;
        }
        return j;
    }

    public byte[] d() {
        return a(987894612);
    }

    public ByteBuffer[] e() {
        ByteBuffer[] byteBufferArr = new ByteBuffer[(this.b.size() + 2)];
        long c = c() - 8;
        byteBufferArr[0] = (ByteBuffer) ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(c).flip();
        int i = 1;
        for (ar<Integer, a> arVar : this.b) {
            byteBufferArr[i] = ByteBuffer.wrap(((a) arVar.b).a);
            i++;
        }
        byteBufferArr[i] = (ByteBuffer) ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN).putLong(c).putLong(2334950737559900225L).putLong(3617552046287187010L).flip();
        return byteBufferArr;
    }

    /* renamed from: f */
    public as clone() {
        as asVar = new as(this.a);
        for (ar next : this.b) {
            asVar.b.add(new ar(next.a, next.b));
        }
        return asVar;
    }
}
