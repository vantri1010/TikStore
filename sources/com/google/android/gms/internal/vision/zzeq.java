package com.google.android.gms.internal.vision;

import java.lang.Comparable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class zzeq<K extends Comparable<K>, V> extends AbstractMap<K, V> {
    private boolean zzhv;
    private final int zzof;
    /* access modifiers changed from: private */
    public List<zzex> zzog;
    /* access modifiers changed from: private */
    public Map<K, V> zzoh;
    private volatile zzez zzoi;
    /* access modifiers changed from: private */
    public Map<K, V> zzoj;
    private volatile zzet zzok;

    private zzeq(int i) {
        this.zzof = i;
        this.zzog = Collections.emptyList();
        this.zzoh = Collections.emptyMap();
        this.zzoj = Collections.emptyMap();
    }

    /* synthetic */ zzeq(int i, zzer zzer) {
        this(i);
    }

    private final int zza(K k) {
        int size = this.zzog.size() - 1;
        if (size >= 0) {
            int compareTo = k.compareTo((Comparable) this.zzog.get(size).getKey());
            if (compareTo > 0) {
                return -(size + 2);
            }
            if (compareTo == 0) {
                return size;
            }
        }
        int i = 0;
        while (i <= size) {
            int i2 = (i + size) / 2;
            int compareTo2 = k.compareTo((Comparable) this.zzog.get(i2).getKey());
            if (compareTo2 < 0) {
                size = i2 - 1;
            } else if (compareTo2 <= 0) {
                return i2;
            } else {
                i = i2 + 1;
            }
        }
        return -(i + 1);
    }

    static <FieldDescriptorType extends zzcl<FieldDescriptorType>> zzeq<FieldDescriptorType, Object> zzam(int i) {
        return new zzer(i);
    }

    /* access modifiers changed from: private */
    public final V zzao(int i) {
        zzdo();
        V value = this.zzog.remove(i).getValue();
        if (!this.zzoh.isEmpty()) {
            Iterator it = zzdp().entrySet().iterator();
            this.zzog.add(new zzex(this, (Map.Entry) it.next()));
            it.remove();
        }
        return value;
    }

    /* access modifiers changed from: private */
    public final void zzdo() {
        if (this.zzhv) {
            throw new UnsupportedOperationException();
        }
    }

    private final SortedMap<K, V> zzdp() {
        zzdo();
        if (this.zzoh.isEmpty() && !(this.zzoh instanceof TreeMap)) {
            TreeMap treeMap = new TreeMap();
            this.zzoh = treeMap;
            this.zzoj = treeMap.descendingMap();
        }
        return (SortedMap) this.zzoh;
    }

    public void clear() {
        zzdo();
        if (!this.zzog.isEmpty()) {
            this.zzog.clear();
        }
        if (!this.zzoh.isEmpty()) {
            this.zzoh.clear();
        }
    }

    public boolean containsKey(Object obj) {
        Comparable comparable = (Comparable) obj;
        return zza(comparable) >= 0 || this.zzoh.containsKey(comparable);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        if (this.zzoi == null) {
            this.zzoi = new zzez(this, (zzer) null);
        }
        return this.zzoi;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzeq)) {
            return super.equals(obj);
        }
        zzeq zzeq = (zzeq) obj;
        int size = size();
        if (size != zzeq.size()) {
            return false;
        }
        int zzdl = zzdl();
        if (zzdl != zzeq.zzdl()) {
            return entrySet().equals(zzeq.entrySet());
        }
        for (int i = 0; i < zzdl; i++) {
            if (!zzan(i).equals(zzeq.zzan(i))) {
                return false;
            }
        }
        if (zzdl != size) {
            return this.zzoh.equals(zzeq.zzoh);
        }
        return true;
    }

    public V get(Object obj) {
        Comparable comparable = (Comparable) obj;
        int zza = zza(comparable);
        return zza >= 0 ? this.zzog.get(zza).getValue() : this.zzoh.get(comparable);
    }

    public int hashCode() {
        int zzdl = zzdl();
        int i = 0;
        for (int i2 = 0; i2 < zzdl; i2++) {
            i += this.zzog.get(i2).hashCode();
        }
        return this.zzoh.size() > 0 ? i + this.zzoh.hashCode() : i;
    }

    public final boolean isImmutable() {
        return this.zzhv;
    }

    public V remove(Object obj) {
        zzdo();
        Comparable comparable = (Comparable) obj;
        int zza = zza(comparable);
        if (zza >= 0) {
            return zzao(zza);
        }
        if (this.zzoh.isEmpty()) {
            return null;
        }
        return this.zzoh.remove(comparable);
    }

    public int size() {
        return this.zzog.size() + this.zzoh.size();
    }

    /* renamed from: zza */
    public final V put(K k, V v) {
        zzdo();
        int zza = zza(k);
        if (zza >= 0) {
            return this.zzog.get(zza).setValue(v);
        }
        zzdo();
        if (this.zzog.isEmpty() && !(this.zzog instanceof ArrayList)) {
            this.zzog = new ArrayList(this.zzof);
        }
        int i = -(zza + 1);
        if (i >= this.zzof) {
            return zzdp().put(k, v);
        }
        int size = this.zzog.size();
        int i2 = this.zzof;
        if (size == i2) {
            zzex remove = this.zzog.remove(i2 - 1);
            zzdp().put((Comparable) remove.getKey(), remove.getValue());
        }
        this.zzog.add(i, new zzex(this, k, v));
        return null;
    }

    public final Map.Entry<K, V> zzan(int i) {
        return this.zzog.get(i);
    }

    public void zzao() {
        if (!this.zzhv) {
            this.zzoh = this.zzoh.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.zzoh);
            this.zzoj = this.zzoj.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.zzoj);
            this.zzhv = true;
        }
    }

    public final int zzdl() {
        return this.zzog.size();
    }

    public final Iterable<Map.Entry<K, V>> zzdm() {
        return this.zzoh.isEmpty() ? zzeu.zzdr() : this.zzoh.entrySet();
    }

    /* access modifiers changed from: package-private */
    public final Set<Map.Entry<K, V>> zzdn() {
        if (this.zzok == null) {
            this.zzok = new zzet(this, (zzer) null);
        }
        return this.zzok;
    }
}
