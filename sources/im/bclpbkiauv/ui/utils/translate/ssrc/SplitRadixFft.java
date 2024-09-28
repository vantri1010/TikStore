package im.bclpbkiauv.ui.utils.translate.ssrc;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class SplitRadixFft {
    private static final int CDFT_RECURSIVE_N = 512;

    public void cdft(int n, int isgn, double[] a, int[] ip, double[] w) {
        int nw = ip[0];
        if (n > (nw << 2)) {
            nw = n >> 2;
            makewt(nw, ip, w);
        }
        if (isgn >= 0) {
            cftfsub(n, a, ip, 2, nw, w);
        } else {
            cftbsub(n, a, ip, 2, nw, w);
        }
    }

    public void rdft(int n, int isgn, double[] a, int[] ip, double[] w) {
        int nw;
        int nc;
        int i = n;
        int[] iArr = ip;
        double[] dArr = w;
        int nw2 = iArr[0];
        if (i > (nw2 << 2)) {
            int nw3 = i >> 2;
            makewt(nw3, iArr, dArr);
            nw = nw3;
        } else {
            nw = nw2;
        }
        int nc2 = iArr[1];
        if (i > (nc2 << 2)) {
            int nc3 = i >> 2;
            makect(nc3, iArr, dArr, nw);
            nc = nc3;
        } else {
            nc = nc2;
        }
        if (isgn >= 0) {
            if (i > 4) {
                int i2 = n;
                double[] dArr2 = a;
                int i3 = nw;
                cftfsub(i2, dArr2, ip, 2, i3, w);
                rftfsub(i2, dArr2, nc, w, i3);
            } else if (i == 4) {
                cftfsub(n, a, ip, 2, nw, w);
            }
            double xi = a[0] - a[1];
            a[0] = a[0] + a[1];
            a[1] = xi;
            return;
        }
        a[1] = (a[0] - a[1]) * 0.5d;
        a[0] = a[0] - a[1];
        if (i > 4) {
            int i4 = n;
            double[] dArr3 = a;
            int i5 = nw;
            rftbsub(i4, dArr3, nc, w, i5);
            cftbsub(i4, dArr3, ip, 2, i5, w);
        } else if (i == 4) {
            cftbsub(n, a, ip, 2, nw, w);
        }
    }

    public void ddct(int n, int isgn, double[] a, int[] ip, double[] w) {
        int nw;
        int nc;
        int i = n;
        int[] iArr = ip;
        double[] dArr = w;
        int nw2 = iArr[0];
        if (i > (nw2 << 2)) {
            int nw3 = i >> 2;
            makewt(nw3, iArr, dArr);
            nw = nw3;
        } else {
            nw = nw2;
        }
        int nc2 = iArr[1];
        if (i > nc2) {
            int nc3 = n;
            makect(nc3, iArr, dArr, nw);
            nc = nc3;
        } else {
            nc = nc2;
        }
        if (isgn < 0) {
            double xr = a[i - 1];
            int j = i - 2;
            while (j >= 2) {
                a[j + 1] = a[j] - a[j - 1];
                a[j] = a[j] + a[j - 1];
                j -= 2;
            }
            a[1] = a[0] - xr;
            a[0] = a[0] + xr;
            if (i > 4) {
                int i2 = n;
                double[] dArr2 = a;
                int i3 = nw;
                rftbsub(i2, dArr2, nc, w, i3);
                int i4 = j;
                cftbsub(i2, dArr2, ip, 2, i3, w);
            } else {
                if (i == 4) {
                    cftbsub(n, a, ip, 2, nw, w);
                }
            }
        }
        dctsub(n, a, nc, w, nw);
        if (isgn >= 0) {
            if (i > 4) {
                int i5 = n;
                double[] dArr3 = a;
                int i6 = nw;
                cftfsub(i5, dArr3, ip, 2, i6, w);
                rftfsub(i5, dArr3, nc, w, i6);
            } else if (i == 4) {
                cftfsub(n, a, ip, 2, nw, w);
            }
            double xr2 = a[0] - a[1];
            a[0] = a[0] + a[1];
            for (int j2 = 2; j2 < i; j2 += 2) {
                a[j2 - 1] = a[j2] - a[j2 + 1];
                a[j2] = a[j2] + a[j2 + 1];
            }
            a[i - 1] = xr2;
        }
    }

    public void ddst(int n, int isgn, double[] a, int[] ip, double[] w) {
        int nw;
        int nc;
        int i = n;
        int[] iArr = ip;
        double[] dArr = w;
        int nw2 = iArr[0];
        if (i > (nw2 << 2)) {
            int nw3 = i >> 2;
            makewt(nw3, iArr, dArr);
            nw = nw3;
        } else {
            nw = nw2;
        }
        int nc2 = iArr[1];
        if (i > nc2) {
            int nc3 = n;
            makect(nc3, iArr, dArr, nw);
            nc = nc3;
        } else {
            nc = nc2;
        }
        if (isgn < 0) {
            double xr = a[i - 1];
            int j = i - 2;
            while (j >= 2) {
                a[j + 1] = (-a[j]) - a[j - 1];
                a[j] = a[j] - a[j - 1];
                j -= 2;
            }
            a[1] = a[0] + xr;
            a[0] = a[0] - xr;
            if (i > 4) {
                int i2 = n;
                double[] dArr2 = a;
                int i3 = nw;
                rftbsub(i2, dArr2, nc, w, i3);
                int i4 = j;
                cftbsub(i2, dArr2, ip, 2, i3, w);
            } else {
                if (i == 4) {
                    cftbsub(n, a, ip, 2, nw, w);
                }
            }
        }
        dstsub(n, a, nc, w, nw);
        if (isgn >= 0) {
            if (i > 4) {
                int i5 = n;
                double[] dArr3 = a;
                int i6 = nw;
                cftfsub(i5, dArr3, ip, 2, i6, w);
                rftfsub(i5, dArr3, nc, w, i6);
            } else if (i == 4) {
                cftfsub(n, a, ip, 2, nw, w);
            }
            double xr2 = a[0] - a[1];
            a[0] = a[0] + a[1];
            for (int j2 = 2; j2 < i; j2 += 2) {
                a[j2 - 1] = (-a[j2]) - a[j2 + 1];
                a[j2] = a[j2] - a[j2 + 1];
            }
            a[i - 1] = -xr2;
        }
    }

    public void dfct(int n, double[] a, double[] t, int[] ip, double[] w) {
        int nw;
        int nc;
        int i;
        int i2;
        int i3 = n;
        int[] iArr = ip;
        double[] dArr = w;
        int nw2 = iArr[0];
        if (i3 > (nw2 << 3)) {
            int nw3 = i3 >> 3;
            makewt(nw3, iArr, dArr);
            nw = nw3;
        } else {
            nw = nw2;
        }
        int nc2 = iArr[1];
        if (i3 > (nc2 << 1)) {
            int nc3 = i3 >> 1;
            makect(nc3, iArr, dArr, nw);
            nc = nc3;
        } else {
            nc = nc2;
        }
        int m = i3 >> 1;
        double yi = a[m];
        double xi = a[0] + a[i3];
        a[0] = a[0] - a[i3];
        t[0] = xi - yi;
        t[m] = xi + yi;
        if (i3 > 2) {
            int mh = m >> 1;
            int j = 1;
            double yi2 = yi;
            double xi2 = xi;
            while (j < mh) {
                int k = m - j;
                double xr = a[j] - a[i3 - j];
                xi2 = a[j] + a[i3 - j];
                double yr = a[k] - a[i3 - k];
                yi2 = a[k] + a[i3 - k];
                a[j] = xr;
                a[k] = yr;
                t[j] = xi2 - yi2;
                t[k] = xi2 + yi2;
                j++;
            }
            t[mh] = a[mh] + a[i3 - mh];
            a[mh] = a[mh] - a[i3 - mh];
            int i4 = j;
            int mh2 = mh;
            dctsub(m, a, nc, w, nw);
            if (m > 4) {
                int i5 = m;
                double[] dArr2 = a;
                i = 4;
                int i6 = nw;
                cftfsub(i5, dArr2, ip, 2, i6, w);
                rftfsub(i5, dArr2, nc, w, i6);
            } else {
                i = 4;
                if (m == 4) {
                    cftfsub(m, a, ip, 2, nw, w);
                }
            }
            int i7 = 1;
            a[i3 - 1] = a[0] - a[1];
            a[1] = a[0] + a[1];
            int j2 = m - 2;
            while (true) {
                if (j2 < 2) {
                    break;
                }
                a[(j2 * 2) + i7] = a[j2] + a[j2 + 1];
                a[(j2 * 2) - i7] = a[j2] - a[j2 + 1];
                j2 -= 2;
                i7 = 1;
            }
            int m2 = j2;
            int l = 2;
            int m3 = mh2;
            for (i2 = 2; m3 >= i2; i2 = 2) {
                int m4 = m3;
                dctsub(m3, t, nc, w, nw);
                if (m4 > i) {
                    int i8 = m4;
                    double[] dArr3 = t;
                    int i9 = nw;
                    cftfsub(i8, dArr3, ip, 2, i9, w);
                    rftfsub(i8, dArr3, nc, w, i9);
                } else if (m4 == i) {
                    cftfsub(m4, t, ip, 2, nw, w);
                }
                a[i3 - l] = t[0] - t[1];
                a[l] = t[0] + t[1];
                int k2 = 0;
                for (int j3 = 2; j3 < m4; j3 += 2) {
                    k2 += l << 2;
                    a[k2 - l] = t[j3] - t[j3 + 1];
                    a[k2 + l] = t[j3] + t[j3 + 1];
                }
                l <<= 1;
                int mh3 = m4 >> 1;
                for (int j4 = 0; j4 < mh3; j4++) {
                    int k3 = m4 - j4;
                    t[j4] = t[m4 + k3] - t[m4 + j4];
                    t[k3] = t[m4 + k3] + t[m4 + j4];
                }
                t[mh3] = t[m4 + mh3];
                m3 = mh3;
                int i10 = mh3;
            }
            a[l] = t[0];
            a[i3] = t[2] - t[1];
            a[0] = t[2] + t[1];
            int j5 = m3;
            double d = yi2;
            double d2 = xi2;
            return;
        }
        a[1] = a[0];
        a[2] = t[0];
        a[0] = t[1];
    }

    public void dfst(int n, double[] a, double[] t, int[] ip, double[] w) {
        int nw;
        int nc;
        int m;
        int m2;
        int m3;
        int i = n;
        int[] iArr = ip;
        double[] dArr = w;
        int nw2 = iArr[0];
        if (i > (nw2 << 3)) {
            int nw3 = i >> 3;
            makewt(nw3, iArr, dArr);
            nw = nw3;
        } else {
            nw = nw2;
        }
        int nc2 = iArr[1];
        if (i > (nc2 << 1)) {
            int nc3 = i >> 1;
            makect(nc3, iArr, dArr, nw);
            nc = nc3;
        } else {
            nc = nc2;
        }
        if (i > 2) {
            int m4 = i >> 1;
            int mh = m4 >> 1;
            int j = 1;
            while (j < mh) {
                int k = m4 - j;
                double xr = a[j] + a[i - j];
                double xi = a[j] - a[i - j];
                double yr = a[k] + a[i - k];
                double yi = a[k] - a[i - k];
                a[j] = xr;
                a[k] = yr;
                t[j] = xi + yi;
                t[k] = xi - yi;
                j++;
            }
            t[0] = a[mh] - a[i - mh];
            a[mh] = a[mh] + a[i - mh];
            a[0] = a[m4];
            int i2 = j;
            int mh2 = mh;
            dstsub(m4, a, nc, w, nw);
            if (m4 > 4) {
                double[] dArr2 = a;
                m2 = 4;
                int i3 = nw;
                m = m4;
                cftfsub(m4, dArr2, ip, 2, i3, w);
                rftfsub(m, dArr2, nc, w, i3);
            } else {
                m2 = 4;
                if (m4 == 4) {
                    m = m4;
                    cftfsub(m4, a, ip, 2, nw, w);
                } else {
                    m = m4;
                }
            }
            a[i - 1] = a[1] - a[0];
            a[1] = a[0] + a[1];
            int j2 = m - 2;
            while (j2 >= 2) {
                a[(j2 * 2) + 1] = a[j2] - a[j2 + 1];
                a[(j2 * 2) - 1] = (-a[j2]) - a[j2 + 1];
                j2 -= 2;
            }
            int l = 2;
            int m5 = j2;
            int j3 = mh2;
            while (j3 >= 2) {
                dstsub(j3, t, nc, w, nw);
                if (j3 > m2) {
                    double[] dArr3 = t;
                    int i4 = nw;
                    int m6 = j3;
                    cftfsub(j3, dArr3, ip, 2, i4, w);
                    rftfsub(m6, dArr3, nc, w, i4);
                    m3 = m6;
                } else {
                    int m7 = j3;
                    if (j3 == m2) {
                        m3 = j3;
                        cftfsub(j3, t, ip, 2, nw, w);
                    } else {
                        m3 = j3;
                    }
                }
                a[i - l] = t[1] - t[0];
                a[l] = t[0] + t[1];
                int k2 = 0;
                for (int j4 = 2; j4 < m3; j4 += 2) {
                    k2 += l << 2;
                    a[k2 - l] = (-t[j4]) - t[j4 + 1];
                    a[k2 + l] = t[j4] - t[j4 + 1];
                }
                l <<= 1;
                int mh3 = m3 >> 1;
                int j5 = 1;
                while (j5 < mh3) {
                    int k3 = m3 - j5;
                    t[j5] = t[m3 + k3] + t[m3 + j5];
                    t[k3] = t[m3 + k3] - t[m3 + j5];
                    j5++;
                }
                t[0] = t[m3 + mh3];
                j3 = mh3;
                int i5 = j5;
                int i6 = mh3;
                m2 = 4;
            }
            int i7 = j3;
            a[l] = t[0];
        }
        a[0] = 0.0d;
    }

    private void makewt(int nw, int[] ip, double[] w) {
        int i = nw;
        ip[0] = i;
        ip[1] = 1;
        if (i > 2) {
            int nwh = i >> 1;
            double delta = 0.7853981633974483d / ((double) nwh);
            double wn4r = Math.cos(((double) nwh) * delta);
            w[0] = 1.0d;
            w[1] = wn4r;
            if (nwh >= 4) {
                w[2] = 0.5d / Math.cos(2.0d * delta);
                w[3] = 0.5d / Math.cos(6.0d * delta);
            }
            for (int j = 4; j < nwh; j += 4) {
                w[j] = Math.cos(((double) j) * delta);
                w[j + 1] = Math.sin(((double) j) * delta);
                w[j + 2] = Math.cos(delta * 3.0d * ((double) j));
                w[j + 3] = Math.sin(3.0d * delta * ((double) j));
            }
            int nw0 = 0;
            while (nwh > 2) {
                int nw1 = nw0 + nwh;
                nwh >>= 1;
                w[nw1] = 1.0d;
                w[nw1 + 1] = wn4r;
                if (nwh >= 4) {
                    double wk1r = w[nw0 + 4];
                    double wk3r = w[nw0 + 6];
                    w[nw1 + 2] = 0.5d / wk1r;
                    w[nw1 + 3] = 0.5d / wk3r;
                }
                for (int j2 = 4; j2 < nwh; j2 += 4) {
                    double wk1r2 = w[(j2 * 2) + nw0];
                    double wk1i = w[nw0 + (j2 * 2) + 1];
                    double wk3r2 = w[nw0 + (j2 * 2) + 2];
                    double wk3i = w[nw0 + (j2 * 2) + 3];
                    w[nw1 + j2] = wk1r2;
                    w[nw1 + j2 + 1] = wk1i;
                    w[nw1 + j2 + 2] = wk3r2;
                    w[nw1 + j2 + 3] = wk3i;
                }
                nw0 = nw1;
            }
        }
    }

    private void makect(int nc, int[] ip, double[] c, int cP) {
        ip[1] = nc;
        if (nc > 1) {
            int nch = nc >> 1;
            double delta = 0.7853981633974483d / ((double) nch);
            c[cP + 0] = Math.cos(((double) nch) * delta);
            c[cP + nch] = c[cP + 0] * 0.5d;
            for (int j = 1; j < nch; j++) {
                c[cP + j] = Math.cos(((double) j) * delta) * 0.5d;
                c[(cP + nc) - j] = Math.sin(((double) j) * delta) * 0.5d;
            }
        }
    }

    private void cftfsub(int n, double[] a, int[] ip, int ipP, int nw, double[] w) {
        int i = n;
        double[] dArr = a;
        double[] dArr2 = w;
        if (i > 32) {
            int m = i >> 2;
            cftf1st(n, a, dArr2, nw - m);
            if (i > 512) {
                int i2 = m;
                double[] dArr3 = a;
                int i3 = nw;
                double[] dArr4 = w;
                cftrec1(i2, dArr3, 0, i3, dArr4);
                cftrec2(i2, dArr3, m, i3, dArr4);
                cftrec1(i2, dArr3, m * 2, i3, dArr4);
                cftrec1(i2, dArr3, m * 3, i3, dArr4);
            } else if (m > 32) {
                cftexp1(n, a, 0, nw, w);
            } else {
                cftfx41(n, a, 0, nw, w);
            }
            int[] iArr = ip;
            int i4 = ipP;
            bitrv2(n, ip, ipP, a);
            return;
        }
        int[] iArr2 = ip;
        int i5 = ipP;
        if (i > 8) {
            if (i == 32) {
                cftf161(a, 0, dArr2, nw - 8);
                bitrv216(a);
                return;
            }
            cftf081(a, 0, dArr2, 0);
            bitrv208(a);
        } else if (i == 8) {
            cftf040(a);
        } else if (i == 4) {
            cftx020(a);
        }
    }

    private void cftbsub(int n, double[] a, int[] ip, int ipP, int nw, double[] w) {
        int i = n;
        double[] dArr = a;
        double[] dArr2 = w;
        if (i > 32) {
            int m = i >> 2;
            cftb1st(n, a, dArr2, nw - m);
            if (i > 512) {
                int i2 = m;
                double[] dArr3 = a;
                int i3 = nw;
                double[] dArr4 = w;
                cftrec1(i2, dArr3, 0, i3, dArr4);
                cftrec2(i2, dArr3, m, i3, dArr4);
                cftrec1(i2, dArr3, m * 2, i3, dArr4);
                cftrec1(i2, dArr3, m * 3, i3, dArr4);
            } else if (m > 32) {
                cftexp1(n, a, 0, nw, w);
            } else {
                cftfx41(n, a, 0, nw, w);
            }
            int[] iArr = ip;
            int i4 = ipP;
            bitrv2conj(n, ip, ipP, a);
            return;
        }
        int[] iArr2 = ip;
        int i5 = ipP;
        if (i > 8) {
            if (i == 32) {
                cftf161(a, 0, dArr2, nw - 8);
                bitrv216neg(a);
                return;
            }
            cftf081(a, 0, dArr2, 0);
            bitrv208neg(a);
        } else if (i == 8) {
            cftb040(a);
        } else if (i == 4) {
            cftx020(a);
        }
    }

    private final void bitrv2(int n, int[] ip, int ipP, double[] a) {
        ip[ipP + 0] = 0;
        int l = n;
        int m = 1;
        while ((m << 3) < l) {
            l >>= 1;
            for (int j = 0; j < m; j++) {
                ip[ipP + m + j] = ip[ipP + j] + l;
            }
            m <<= 1;
        }
        int j2 = m * 2;
        if ((m << 3) == l) {
            for (int k = 0; k < m; k++) {
                for (int j3 = 0; j3 < k; j3++) {
                    int j1 = (j3 * 2) + ip[ipP + k];
                    int k1 = (k * 2) + ip[ipP + j3];
                    double xr = a[j1];
                    double xi = a[j1 + 1];
                    double yr = a[k1];
                    double yi = a[k1 + 1];
                    a[j1] = yr;
                    a[j1 + 1] = yi;
                    a[k1] = xr;
                    a[k1 + 1] = xi;
                    int j12 = j1 + j2;
                    int k12 = k1 + (j2 * 2);
                    double xr2 = a[j12];
                    double xi2 = a[j12 + 1];
                    double yr2 = a[k12];
                    double yi2 = a[k12 + 1];
                    a[j12] = yr2;
                    a[j12 + 1] = yi2;
                    a[k12] = xr2;
                    a[k12 + 1] = xi2;
                    int j13 = j12 + j2;
                    int k13 = k12 - j2;
                    double xr3 = a[j13];
                    double xi3 = a[j13 + 1];
                    double yr3 = a[k13];
                    double yi3 = a[k13 + 1];
                    a[j13] = yr3;
                    a[j13 + 1] = yi3;
                    a[k13] = xr3;
                    a[k13 + 1] = xi3;
                    int j14 = j13 + j2;
                    int k14 = k13 + (j2 * 2);
                    double xr4 = a[j14];
                    double xi4 = a[j14 + 1];
                    double yr4 = a[k14];
                    double yi4 = a[k14 + 1];
                    a[j14] = yr4;
                    a[j14 + 1] = yi4;
                    a[k14] = xr4;
                    a[k14 + 1] = xi4;
                }
                int j15 = (k * 2) + j2 + ip[ipP + k];
                int k15 = j15 + j2;
                double xr5 = a[j15];
                double xi5 = a[j15 + 1];
                double yr5 = a[k15];
                double yi5 = a[k15 + 1];
                a[j15] = yr5;
                a[j15 + 1] = yi5;
                a[k15] = xr5;
                a[k15 + 1] = xi5;
            }
            return;
        }
        for (int k2 = 1; k2 < m; k2++) {
            for (int j4 = 0; j4 < k2; j4++) {
                int j16 = (j4 * 2) + ip[ipP + k2];
                int k16 = (k2 * 2) + ip[ipP + j4];
                double xr6 = a[j16];
                double xi6 = a[j16 + 1];
                double yr6 = a[k16];
                double yi6 = a[k16 + 1];
                a[j16] = yr6;
                a[j16 + 1] = yi6;
                a[k16] = xr6;
                a[k16 + 1] = xi6;
                int j17 = j16 + j2;
                int k17 = k16 + j2;
                double xr7 = a[j17];
                double xi7 = a[j17 + 1];
                double yr7 = a[k17];
                double yi7 = a[k17 + 1];
                a[j17] = yr7;
                a[j17 + 1] = yi7;
                a[k17] = xr7;
                a[k17 + 1] = xi7;
            }
        }
    }

    private final void bitrv2conj(int n, int[] ip, int ipP, double[] a) {
        ip[ipP + 0] = 0;
        int l = n;
        int m = 1;
        while ((m << 3) < l) {
            l >>= 1;
            for (int j = 0; j < m; j++) {
                ip[ipP + m + j] = ip[ipP + j] + l;
            }
            m <<= 1;
        }
        int j2 = m * 2;
        if ((m << 3) == l) {
            for (int k = 0; k < m; k++) {
                int j3 = 0;
                while (j3 < k) {
                    int j1 = (j3 * 2) + ip[ipP + k];
                    int k1 = (k * 2) + ip[ipP + j3];
                    double xr = a[j1];
                    double xi = -a[j1 + 1];
                    double yr = a[k1];
                    double yi = -a[k1 + 1];
                    a[j1] = yr;
                    a[j1 + 1] = yi;
                    a[k1] = xr;
                    a[k1 + 1] = xi;
                    int j12 = j1 + j2;
                    int k12 = k1 + (j2 * 2);
                    double xr2 = a[j12];
                    double d = xi;
                    double xi2 = -a[j12 + 1];
                    double yr2 = a[k12];
                    double d2 = yi;
                    double yi2 = -a[k12 + 1];
                    a[j12] = yr2;
                    a[j12 + 1] = yi2;
                    a[k12] = xr2;
                    a[k12 + 1] = xi2;
                    int j13 = j12 + j2;
                    int k13 = k12 - j2;
                    double xr3 = a[j13];
                    double d3 = xi2;
                    double xi3 = -a[j13 + 1];
                    double yr3 = a[k13];
                    double d4 = yi2;
                    double yi3 = -a[k13 + 1];
                    a[j13] = yr3;
                    a[j13 + 1] = yi3;
                    a[k13] = xr3;
                    a[k13 + 1] = xi3;
                    int j14 = j13 + j2;
                    int k14 = k13 + (j2 * 2);
                    double xr4 = a[j14];
                    double d5 = xi3;
                    double d6 = yi3;
                    a[j14] = a[k14];
                    a[j14 + 1] = -a[k14 + 1];
                    a[k14] = xr4;
                    a[k14 + 1] = -a[j14 + 1];
                    j3++;
                }
                int k15 = (k * 2) + ip[ipP + k];
                a[k15 + 1] = -a[k15 + 1];
                int j15 = k15 + j2;
                int k16 = j15 + j2;
                double xr5 = a[j15];
                a[j15] = a[k16];
                a[j15 + 1] = -a[k16 + 1];
                a[k16] = xr5;
                a[k16 + 1] = -a[j15 + 1];
                int k17 = k16 + j2;
                int i = j3;
                int i2 = k17;
                a[k17 + 1] = -a[k17 + 1];
            }
            return;
        }
        a[1] = -a[1];
        a[j2 + 1] = -a[j2 + 1];
        int k2 = 1;
        while (k2 < m) {
            int j4 = 0;
            while (j4 < k2) {
                int j16 = (j4 * 2) + ip[ipP + k2];
                int k18 = (k2 * 2) + ip[ipP + j4];
                double xr6 = a[j16];
                a[j16] = a[k18];
                a[j16 + 1] = -a[k18 + 1];
                a[k18] = xr6;
                a[k18 + 1] = -a[j16 + 1];
                int j17 = j16 + j2;
                int k19 = k18 + j2;
                double xr7 = a[j17];
                a[j17] = a[k19];
                a[j17 + 1] = -a[k19 + 1];
                a[k19] = xr7;
                a[k19 + 1] = -a[j17 + 1];
                j4++;
                k2 = k2;
            }
            int k3 = k2;
            int k110 = (k3 * 2) + ip[ipP + k3];
            a[k110 + 1] = -a[k110 + 1];
            a[k110 + j2 + 1] = -a[k110 + j2 + 1];
            k2 = k3 + 1;
        }
        int i3 = k2;
    }

    private void bitrv216(double[] a) {
        double x1r = a[2];
        double x1i = a[3];
        double x2r = a[4];
        double x2i = a[5];
        double x3r = a[6];
        double x3i = a[7];
        double x4r = a[8];
        double x4i = a[9];
        double x5r = a[10];
        double x5i = a[11];
        double x7r = a[14];
        double x7i = a[15];
        double x8r = a[16];
        double x8i = a[17];
        double x10r = a[20];
        double x10i = a[21];
        double x11r = a[22];
        double x11i = a[23];
        double x12r = a[24];
        double x12i = a[25];
        double x13r = a[26];
        double x13i = a[27];
        double x14r = a[28];
        double x14i = a[29];
        a[2] = x8r;
        a[3] = x8i;
        a[4] = x4r;
        a[5] = x4i;
        a[6] = x12r;
        a[7] = x12i;
        a[8] = x2r;
        a[9] = x2i;
        a[10] = x10r;
        a[11] = x10i;
        a[14] = x14r;
        a[15] = x14i;
        a[16] = x1r;
        a[17] = x1i;
        a[20] = x5r;
        a[21] = x5i;
        a[22] = x13r;
        a[23] = x13i;
        a[24] = x3r;
        a[25] = x3i;
        a[26] = x11r;
        a[27] = x11i;
        a[28] = x7r;
        a[29] = x7i;
    }

    private void bitrv216neg(double[] a) {
        double x1r = a[2];
        double x1i = a[3];
        double x2r = a[4];
        double x2i = a[5];
        double x3r = a[6];
        double x3i = a[7];
        double x4r = a[8];
        double x4i = a[9];
        double x5r = a[10];
        double x5i = a[11];
        double x6r = a[12];
        double x6i = a[13];
        double x7r = a[14];
        double x7i = a[15];
        double x8r = a[16];
        double x8i = a[17];
        double x9r = a[18];
        double x9i = a[19];
        double x10r = a[20];
        double x10i = a[21];
        double x11r = a[22];
        double x11i = a[23];
        double x12r = a[24];
        double x12i = a[25];
        double x13r = a[26];
        double x13i = a[27];
        double x14r = a[28];
        double x14i = a[29];
        double x15r = a[30];
        double x15i = a[31];
        a[2] = x15r;
        a[3] = x15i;
        a[4] = x7r;
        a[5] = x7i;
        a[6] = x11r;
        a[7] = x11i;
        a[8] = x3r;
        a[9] = x3i;
        a[10] = x13r;
        a[11] = x13i;
        a[12] = x5r;
        a[13] = x5i;
        a[14] = x9r;
        a[15] = x9i;
        a[16] = x1r;
        a[17] = x1i;
        a[18] = x14r;
        a[19] = x14i;
        a[20] = x6r;
        a[21] = x6i;
        a[22] = x10r;
        a[23] = x10i;
        a[24] = x2r;
        a[25] = x2i;
        a[26] = x12r;
        a[27] = x12i;
        a[28] = x4r;
        a[29] = x4i;
        a[30] = x8r;
        a[31] = x8i;
    }

    private void bitrv208(double[] a) {
        double x1r = a[2];
        double x1i = a[3];
        double x3r = a[6];
        double x3i = a[7];
        double x4r = a[8];
        double x4i = a[9];
        double x6r = a[12];
        double x6i = a[13];
        a[2] = x4r;
        a[3] = x4i;
        a[6] = x6r;
        a[7] = x6i;
        a[8] = x1r;
        a[9] = x1i;
        a[12] = x3r;
        a[13] = x3i;
    }

    private void bitrv208neg(double[] a) {
        double x1r = a[2];
        double x1i = a[3];
        double x2r = a[4];
        double x2i = a[5];
        double x3r = a[6];
        double x3i = a[7];
        double x4r = a[8];
        double x4i = a[9];
        double x5r = a[10];
        double x5i = a[11];
        double x6r = a[12];
        double x6i = a[13];
        double x7r = a[14];
        double x7i = a[15];
        a[2] = x7r;
        a[3] = x7i;
        a[4] = x3r;
        a[5] = x3i;
        a[6] = x5r;
        a[7] = x5i;
        a[8] = x1r;
        a[9] = x1i;
        a[10] = x6r;
        a[11] = x6i;
        a[12] = x2r;
        a[13] = x2i;
        a[14] = x4r;
        a[15] = x4i;
    }

    private void cftf1st(int n, double[] a, double[] w, int wP) {
        int mh = n >> 3;
        int m = mh * 2;
        int j1 = m;
        int j2 = j1 + m;
        int j3 = j2 + m;
        double x0r = a[0] + a[j2];
        double x0i = a[1] + a[j2 + 1];
        double x1r = a[0] - a[j2];
        double x1i = a[1] - a[j2 + 1];
        double x2r = a[j1] + a[j3];
        double x2i = a[j1 + 1] + a[j3 + 1];
        double x3r = a[j1] - a[j3];
        double x3i = a[j1 + 1] - a[j3 + 1];
        a[0] = x0r + x2r;
        a[1] = x0i + x2i;
        a[j1] = x0r - x2r;
        a[j1 + 1] = x0i - x2i;
        a[j2] = x1r - x3i;
        a[j2 + 1] = x1i + x3r;
        a[j3] = x1r + x3i;
        a[j3 + 1] = x1i - x3r;
        double d = x0i;
        double wn4r = w[wP + 1];
        double csc1 = w[wP + 2];
        double csc3 = w[wP + 3];
        double wd1r = 1.0d;
        double wd1i = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        double wd3r = 1.0d;
        double wd3i = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        int k = 0;
        int j = 2;
        while (true) {
            int i = j1;
            if (j < mh - 2) {
                k += 4;
                double wk1r = (wd1r + w[wP + k]) * csc1;
                double wk1i = (wd1i + w[wP + k + 1]) * csc1;
                double wk3r = (wd3r + w[wP + k + 2]) * csc3;
                double wk3i = (wd3i - w[(wP + k) + 3]) * csc3;
                wd1r = w[wP + k];
                wd1i = w[wP + k + 1];
                wd3r = w[wP + k + 2];
                int i2 = j2;
                double wd3i2 = -w[wP + k + 3];
                int j12 = j + m;
                int j22 = j12 + m;
                int j32 = j22 + m;
                double x0r2 = a[j] + a[j22];
                double x0i2 = a[j + 1] + a[j22 + 1];
                double x1r2 = a[j] - a[j22];
                double x1i2 = a[j + 1] - a[j22 + 1];
                double y0r = a[j + 2] + a[j22 + 2];
                double y0i = a[j + 3] + a[j22 + 3];
                double y1r = a[j + 2] - a[j22 + 2];
                double y1i = a[j + 3] - a[j22 + 3];
                double x2r2 = a[j12] + a[j32];
                double x2i2 = a[j12 + 1] + a[j32 + 1];
                double x3r2 = a[j12] - a[j32];
                double x3i2 = a[j12 + 1] - a[j32 + 1];
                double y2r = a[j12 + 2] + a[j32 + 2];
                double y2i = a[j12 + 3] + a[j32 + 3];
                double y3r = a[j12 + 2] - a[j32 + 2];
                double y3i = a[j12 + 3] - a[j32 + 3];
                a[j] = x0r2 + x2r2;
                a[j + 1] = x0i2 + x2i2;
                a[j + 2] = y0r + y2r;
                a[j + 3] = y0i + y2i;
                a[j12] = x0r2 - x2r2;
                a[j12 + 1] = x0i2 - x2i2;
                a[j12 + 2] = y0r - y2r;
                a[j12 + 3] = y0i - y2i;
                double x0r3 = x1r2 - x3i2;
                double x0i3 = x1i2 + x3r2;
                a[j22] = (wk1r * x0r3) - (wk1i * x0i3);
                a[j22 + 1] = (wk1r * x0i3) + (wk1i * x0r3);
                double x0r4 = y1r - y3i;
                double x0i4 = y1i + y3r;
                a[j22 + 2] = (wd1r * x0r4) - (wd1i * x0i4);
                a[j22 + 3] = (wd1r * x0i4) + (wd1i * x0r4);
                double x0r5 = x1r2 + x3i2;
                double x0i5 = x1i2 - x3r2;
                a[j32] = (wk3r * x0r5) + (wk3i * x0i5);
                a[j32 + 1] = (wk3r * x0i5) - (wk3i * x0r5);
                double x0r6 = y1r + y3i;
                double x0i6 = y1i - y3r;
                a[j32 + 2] = (wd3r * x0r6) + (wd3i2 * x0i6);
                a[j32 + 3] = (wd3r * x0i6) - (wd3i2 * x0r6);
                int j0 = m - j;
                int j13 = j0 + m;
                int j23 = j13 + m;
                int j33 = j23 + m;
                double x0r7 = a[j0] + a[j23];
                double x0i7 = a[j0 + 1] + a[j23 + 1];
                double x1r3 = a[j0] - a[j23];
                double x1i3 = a[j0 + 1] - a[j23 + 1];
                double y0r2 = a[j0 - 2] + a[j23 - 2];
                double y0i2 = a[j0 - 1] + a[j23 - 1];
                double y1r2 = a[j0 - 2] - a[j23 - 2];
                double y1i2 = a[j0 - 1] - a[j23 - 1];
                double x2r3 = a[j13] + a[j33];
                double x2i3 = a[j13 + 1] + a[j33 + 1];
                double x3r3 = a[j13] - a[j33];
                double x3i3 = a[j13 + 1] - a[j33 + 1];
                double y2r2 = a[j13 - 2] + a[j33 - 2];
                double y2i2 = a[j13 - 1] + a[j33 - 1];
                double y3r2 = a[j13 - 2] - a[j33 - 2];
                double y3i2 = a[j13 - 1] - a[j33 - 1];
                a[j0] = x0r7 + x2r3;
                a[j0 + 1] = x0i7 + x2i3;
                a[j0 - 2] = y0r2 + y2r2;
                a[j0 - 1] = y0i2 + y2i2;
                a[j13] = x0r7 - x2r3;
                a[j13 + 1] = x0i7 - x2i3;
                a[j13 - 2] = y0r2 - y2r2;
                a[j13 - 1] = y0i2 - y2i2;
                double x0r8 = x1r3 - x3i3;
                double x0i8 = x1i3 + x3r3;
                a[j23] = (wk1i * x0r8) - (wk1r * x0i8);
                a[j23 + 1] = (wk1i * x0i8) + (wk1r * x0r8);
                double x0r9 = y1r2 - y3i2;
                double x0i9 = y1i2 + y3r2;
                a[j23 - 2] = (wd1i * x0r9) - (wd1r * x0i9);
                a[j23 - 1] = (wd1i * x0i9) + (wd1r * x0r9);
                double x0r10 = x1r3 + x3i3;
                double x0i10 = x1i3 - x3r3;
                a[j33] = (wk3i * x0r10) + (wk3r * x0i10);
                a[j33 + 1] = (wk3i * x0i10) - (wk3r * x0r10);
                double x0r11 = y1r2 + y3i2;
                double x0i11 = y1i2 - y3r2;
                a[j33 - 2] = (wd3i2 * x0r11) + (wd3r * x0i11);
                a[j33 - 1] = (wd3i2 * x0i11) - (wd3r * x0r11);
                j += 4;
                double y0i3 = x1i3;
                double y2i3 = x3i3;
                double y0r3 = x0i11;
                double x1i4 = x1r3;
                double x3i4 = x3r3;
                double x1r4 = x0r11;
                double x3r4 = x2i3;
                double x2i4 = x2r3;
                double d2 = wd3i2;
                j1 = j13;
                j2 = j23;
                wd3i = d2;
            } else {
                double wk1r2 = (wd1r + wn4r) * csc1;
                double wk1i2 = (wd1i + wn4r) * csc1;
                double wk3r2 = (wd3r - wn4r) * csc3;
                double wk3i2 = (wd3i - wn4r) * csc3;
                int j02 = mh;
                int j14 = j02 + m;
                int j24 = j14 + m;
                int j34 = j24 + m;
                double x0r12 = a[j02 - 2] + a[j24 - 2];
                double x0i12 = a[j02 - 1] + a[j24 - 1];
                double x1r5 = a[j02 - 2] - a[j24 - 2];
                double x1i5 = a[j02 - 1] - a[j24 - 1];
                double x2r4 = a[j14 - 2] + a[j34 - 2];
                double x2i5 = a[j14 - 1] + a[j34 - 1];
                double x3r5 = a[j14 - 2] - a[j34 - 2];
                double x3i5 = a[j14 - 1] - a[j34 - 1];
                a[j02 - 2] = x0r12 + x2r4;
                a[j02 - 1] = x0i12 + x2i5;
                a[j14 - 2] = x0r12 - x2r4;
                a[j14 - 1] = x0i12 - x2i5;
                double x0r13 = x1r5 - x3i5;
                double x0i13 = x1i5 + x3r5;
                a[j24 - 2] = (wk1r2 * x0r13) - (wk1i2 * x0i13);
                a[j24 - 1] = (wk1r2 * x0i13) + (wk1i2 * x0r13);
                double x0r14 = x1r5 + x3i5;
                double x0i14 = x1i5 - x3r5;
                a[j34 - 2] = (wk3r2 * x0r14) + (wk3i2 * x0i14);
                a[j34 - 1] = (wk3r2 * x0i14) - (wk3i2 * x0r14);
                double x0r15 = a[j02] + a[j24];
                double x0i15 = a[j02 + 1] + a[j24 + 1];
                double x1r6 = a[j02] - a[j24];
                double x1i6 = a[j02 + 1] - a[j24 + 1];
                double x2r5 = a[j14] + a[j34];
                double x2i6 = a[j14 + 1] + a[j34 + 1];
                double x3r6 = a[j14] - a[j34];
                double x3i6 = a[j14 + 1] - a[j34 + 1];
                a[j02] = x0r15 + x2r5;
                a[j02 + 1] = x0i15 + x2i6;
                a[j14] = x0r15 - x2r5;
                a[j14 + 1] = x0i15 - x2i6;
                double x0r16 = x1r6 - x3i6;
                double x0i16 = x1i6 + x3r6;
                a[j24] = (x0r16 - x0i16) * wn4r;
                a[j24 + 1] = (x0i16 + x0r16) * wn4r;
                double x0r17 = x1r6 + x3i6;
                double x0i17 = x1i6 - x3r6;
                int i3 = mh;
                int i4 = m;
                a[j34] = (-wn4r) * (x0r17 + x0i17);
                int i5 = k;
                double d3 = x1r6;
                a[j34 + 1] = (-wn4r) * (x0i17 - x0r17);
                double x0r18 = a[j02 + 2] + a[j24 + 2];
                double x0i18 = a[j02 + 3] + a[j24 + 3];
                double x1r7 = a[j02 + 2] - a[j24 + 2];
                double x1i7 = a[j02 + 3] - a[j24 + 3];
                double x2r6 = a[j14 + 2] + a[j34 + 2];
                double x2i7 = a[j14 + 3] + a[j34 + 3];
                double x3r7 = a[j14 + 2] - a[j34 + 2];
                double x3i7 = a[j14 + 3] - a[j34 + 3];
                a[j02 + 2] = x0r18 + x2r6;
                a[j02 + 3] = x0i18 + x2i7;
                a[j14 + 2] = x0r18 - x2r6;
                a[j14 + 3] = x0i18 - x2i7;
                double x0r19 = x1r7 - x3i7;
                double x0i19 = x1i7 + x3r7;
                a[j24 + 2] = (wk1i2 * x0r19) - (wk1r2 * x0i19);
                a[j24 + 3] = (wk1i2 * x0i19) + (wk1r2 * x0r19);
                double x0r20 = x1r7 + x3i7;
                double x0i20 = x1i7 - x3r7;
                a[j34 + 2] = (wk3i2 * x0r20) + (wk3r2 * x0i20);
                a[j34 + 3] = (wk3i2 * x0i20) - (wk3r2 * x0r20);
                return;
            }
        }
    }

    private final void cftb1st(int n, double[] a, double[] w, int wP) {
        int mh = n >> 3;
        int m = mh * 2;
        int j1 = m;
        int j2 = j1 + m;
        int j3 = j2 + m;
        double x0r = a[0] + a[j2];
        double x0i = (-a[1]) - a[j2 + 1];
        double x1r = a[0] - a[j2];
        double x1i = (-a[1]) + a[j2 + 1];
        double x2r = a[j1] + a[j3];
        double x2i = a[j1 + 1] + a[j3 + 1];
        double x3r = a[j1] - a[j3];
        double x3i = a[j1 + 1] - a[j3 + 1];
        a[0] = x0r + x2r;
        a[1] = x0i - x2i;
        a[j1] = x0r - x2r;
        a[j1 + 1] = x0i + x2i;
        a[j2] = x1r + x3i;
        a[j2 + 1] = x1i + x3r;
        a[j3] = x1r - x3i;
        a[j3 + 1] = x1i - x3r;
        double d = x0i;
        double x1i2 = w[wP + 1];
        double csc1 = w[wP + 2];
        double csc3 = w[wP + 3];
        double wd1r = 1.0d;
        double wd1i = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        double wd3r = 1.0d;
        double wd3i = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        int k = 0;
        int j = 2;
        while (true) {
            int i = j1;
            if (j < mh - 2) {
                k += 4;
                double wk1r = (wd1r + w[wP + k]) * csc1;
                double wk1i = (wd1i + w[wP + k + 1]) * csc1;
                double wk3r = (wd3r + w[wP + k + 2]) * csc3;
                double wk3i = (wd3i - w[(wP + k) + 3]) * csc3;
                wd1r = w[wP + k];
                wd1i = w[wP + k + 1];
                wd3r = w[wP + k + 2];
                int i2 = j2;
                double wd3i2 = -w[wP + k + 3];
                int j12 = j + m;
                int j22 = j12 + m;
                int j32 = j22 + m;
                double x0r2 = a[j] + a[j22];
                double x0i2 = (-a[j + 1]) - a[j22 + 1];
                double x1r2 = a[j] - a[j22];
                double x1i3 = (-a[j + 1]) + a[j22 + 1];
                double y0r = a[j + 2] + a[j22 + 2];
                double wn4r = x1i2;
                double y0i = (-a[j + 3]) - a[j22 + 3];
                double y1r = a[j + 2] - a[j22 + 2];
                int mh2 = mh;
                int m2 = m;
                double y1i = (-a[j + 3]) + a[j22 + 3];
                double x2r2 = a[j12] + a[j32];
                double x2i2 = a[j12 + 1] + a[j32 + 1];
                double x3r2 = a[j12] - a[j32];
                double x3i2 = a[j12 + 1] - a[j32 + 1];
                double y2r = a[j12 + 2] + a[j32 + 2];
                double y2i = a[j12 + 3] + a[j32 + 3];
                double y3r = a[j12 + 2] - a[j32 + 2];
                double y3i = a[j12 + 3] - a[j32 + 3];
                a[j] = x0r2 + x2r2;
                a[j + 1] = x0i2 - x2i2;
                a[j + 2] = y0r + y2r;
                a[j + 3] = y0i - y2i;
                a[j12] = x0r2 - x2r2;
                a[j12 + 1] = x0i2 + x2i2;
                a[j12 + 2] = y0r - y2r;
                a[j12 + 3] = y0i + y2i;
                double x0r3 = x1r2 + x3i2;
                double x0i3 = x1i3 + x3r2;
                a[j22] = (wk1r * x0r3) - (wk1i * x0i3);
                a[j22 + 1] = (wk1r * x0i3) + (wk1i * x0r3);
                double x0r4 = y1r + y3i;
                double x0i4 = y1i + y3r;
                a[j22 + 2] = (wd1r * x0r4) - (wd1i * x0i4);
                a[j22 + 3] = (wd1r * x0i4) + (wd1i * x0r4);
                double x0r5 = x1r2 - x3i2;
                double x0i5 = x1i3 - x3r2;
                a[j32] = (wk3r * x0r5) + (wk3i * x0i5);
                a[j32 + 1] = (wk3r * x0i5) - (wk3i * x0r5);
                double x0r6 = y1r - y3i;
                double x0i6 = y1i - y3r;
                a[j32 + 2] = (wd3r * x0r6) + (wd3i2 * x0i6);
                a[j32 + 3] = (wd3r * x0i6) - (wd3i2 * x0r6);
                int j0 = m2 - j;
                int j13 = j0 + m2;
                int j23 = j13 + m2;
                int j33 = j23 + m2;
                double x0r7 = a[j0] + a[j23];
                double d2 = y1i;
                double x0i7 = (-a[j0 + 1]) - a[j23 + 1];
                double x1r3 = a[j0] - a[j23];
                double d3 = y0i;
                double x1i4 = (-a[j0 + 1]) + a[j23 + 1];
                double y0r2 = a[j0 - 2] + a[j23 - 2];
                double y0i2 = (-a[j0 - 1]) - a[j23 - 1];
                double y1r2 = a[j0 - 2] - a[j23 - 2];
                double wd3i3 = wd3i2;
                double y1i2 = (-a[j0 - 1]) + a[j23 - 1];
                double x2r3 = a[j13] + a[j33];
                double x2i3 = a[j13 + 1] + a[j33 + 1];
                double x3r3 = a[j13] - a[j33];
                double x3i3 = a[j13 + 1] - a[j33 + 1];
                double y2r2 = a[j13 - 2] + a[j33 - 2];
                double y2i2 = a[j13 - 1] + a[j33 - 1];
                double y3r2 = a[j13 - 2] - a[j33 - 2];
                double y3i2 = a[j13 - 1] - a[j33 - 1];
                a[j0] = x0r7 + x2r3;
                a[j0 + 1] = x0i7 - x2i3;
                a[j0 - 2] = y0r2 + y2r2;
                a[j0 - 1] = y0i2 - y2i2;
                a[j13] = x0r7 - x2r3;
                a[j13 + 1] = x0i7 + x2i3;
                a[j13 - 2] = y0r2 - y2r2;
                a[j13 - 1] = y0i2 + y2i2;
                double x0r8 = x1r3 + x3i3;
                double x0i8 = x1i4 + x3r3;
                a[j23] = (wk1i * x0r8) - (wk1r * x0i8);
                a[j23 + 1] = (wk1i * x0i8) + (wk1r * x0r8);
                double x0r9 = y1r2 + y3i2;
                double x0i9 = y1i2 + y3r2;
                a[j23 - 2] = (wd1i * x0r9) - (wd1r * x0i9);
                a[j23 - 1] = (wd1i * x0i9) + (wd1r * x0r9);
                double x0r10 = x1r3 - x3i3;
                double x0i10 = x1i4 - x3r3;
                a[j33] = (wk3i * x0r10) + (wk3r * x0i10);
                a[j33 + 1] = (wk3i * x0i10) - (wk3r * x0r10);
                double x0r11 = y1r2 - y3i2;
                double x0i11 = y1i2 - y3r2;
                a[j33 - 2] = (wd3i3 * x0r11) + (wd3r * x0i11);
                a[j33 - 1] = (wd3i3 * x0i11) - (wd3r * x0r11);
                j += 4;
                double y1r3 = x0i11;
                double y0r3 = x1r3;
                double y0i3 = x1i4;
                double y2i3 = x3i3;
                j1 = j13;
                j2 = j23;
                x1i2 = wn4r;
                wd3i = wd3i3;
                mh = mh2;
                m = m2;
                double x1r4 = x0r11;
                double x3i4 = x3r3;
                double x3r4 = x2i3;
                double x2i4 = x2r3;
            } else {
                int mh3 = mh;
                int m3 = m;
                double wn4r2 = x1i2;
                double wk1r2 = (wd1r + wn4r2) * csc1;
                double wk1i2 = (wd1i + wn4r2) * csc1;
                double wk3r2 = (wd3r - wn4r2) * csc3;
                double wk3i2 = (wd3i - wn4r2) * csc3;
                int j02 = mh3;
                int j14 = j02 + m3;
                int j24 = j14 + m3;
                int j34 = j24 + m3;
                double x0r12 = a[j02 - 2] + a[j24 - 2];
                double x0i12 = (-a[j02 - 1]) - a[j24 - 1];
                double x1r5 = a[j02 - 2] - a[j24 - 2];
                double x1i5 = (-a[j02 - 1]) + a[j24 - 1];
                double x2r4 = a[j14 - 2] + a[j34 - 2];
                double x2i5 = a[j14 - 1] + a[j34 - 1];
                double x3r5 = a[j14 - 2] - a[j34 - 2];
                double x3i5 = a[j14 - 1] - a[j34 - 1];
                a[j02 - 2] = x0r12 + x2r4;
                a[j02 - 1] = x0i12 - x2i5;
                a[j14 - 2] = x0r12 - x2r4;
                a[j14 - 1] = x0i12 + x2i5;
                double x0r13 = x1r5 + x3i5;
                double x0i13 = x1i5 + x3r5;
                a[j24 - 2] = (wk1r2 * x0r13) - (wk1i2 * x0i13);
                a[j24 - 1] = (wk1r2 * x0i13) + (wk1i2 * x0r13);
                double x0r14 = x1r5 - x3i5;
                double x0i14 = x1i5 - x3r5;
                a[j34 - 2] = (wk3r2 * x0r14) + (wk3i2 * x0i14);
                a[j34 - 1] = (wk3r2 * x0i14) - (wk3i2 * x0r14);
                double x0r15 = a[j02] + a[j24];
                int i3 = k;
                double d4 = x0i14;
                double x0i15 = (-a[j02 + 1]) - a[j24 + 1];
                double x1r6 = a[j02] - a[j24];
                int i4 = j;
                double d5 = x1i5;
                double x1i6 = (-a[j02 + 1]) + a[j24 + 1];
                double x2r5 = a[j14] + a[j34];
                double x2i6 = a[j14 + 1] + a[j34 + 1];
                double x3r6 = a[j14] - a[j34];
                double x3i6 = a[j14 + 1] - a[j34 + 1];
                a[j02] = x0r15 + x2r5;
                a[j02 + 1] = x0i15 - x2i6;
                a[j14] = x0r15 - x2r5;
                a[j14 + 1] = x0i15 + x2i6;
                double x0r16 = x1r6 + x3i6;
                double x0i16 = x1i6 + x3r6;
                a[j24] = (x0r16 - x0i16) * wn4r2;
                a[j24 + 1] = (x0i16 + x0r16) * wn4r2;
                double x0r17 = x1r6 - x3i6;
                double x0i17 = x1i6 - x3r6;
                double d6 = x1i6;
                double d7 = x2i6;
                double wn4r3 = wn4r2;
                a[j34] = (-wn4r3) * (x0r17 + x0i17);
                a[j34 + 1] = (-wn4r3) * (x0i17 - x0r17);
                double x0r18 = a[j02 + 2] + a[j24 + 2];
                double d8 = x0i17;
                double x0i18 = (-a[j02 + 3]) - a[j24 + 3];
                double x1r7 = a[j02 + 2] - a[j24 + 2];
                double d9 = wn4r3;
                double x1i7 = (-a[j02 + 3]) + a[j24 + 3];
                double x2r6 = a[j14 + 2] + a[j34 + 2];
                double x2i7 = a[j14 + 3] + a[j34 + 3];
                double x3r7 = a[j14 + 2] - a[j34 + 2];
                double x3i7 = a[j14 + 3] - a[j34 + 3];
                a[j02 + 2] = x0r18 + x2r6;
                a[j02 + 3] = x0i18 - x2i7;
                a[j14 + 2] = x0r18 - x2r6;
                a[j14 + 3] = x0i18 + x2i7;
                double x0r19 = x1r7 + x3i7;
                double x0i19 = x1i7 + x3r7;
                a[j24 + 2] = (wk1i2 * x0r19) - (wk1r2 * x0i19);
                a[j24 + 3] = (wk1i2 * x0i19) + (wk1r2 * x0r19);
                double x0r20 = x1r7 - x3i7;
                double x0i20 = x1i7 - x3r7;
                a[j34 + 2] = (wk3i2 * x0r20) + (wk3r2 * x0i20);
                a[j34 + 3] = (wk3i2 * x0i20) - (wk3r2 * x0r20);
                return;
            }
        }
    }

    private void cftrec1(int n, double[] a, int aP, int nw, double[] w) {
        int m = n >> 2;
        cftmdl1(n, a, aP, w, nw - (m * 2));
        if (n > 512) {
            int i = m;
            double[] dArr = a;
            int i2 = nw;
            double[] dArr2 = w;
            cftrec1(i, dArr, aP, i2, dArr2);
            cftrec2(i, dArr, aP + m, i2, dArr2);
            cftrec1(i, dArr, aP + (m * 2), i2, dArr2);
            cftrec1(i, dArr, aP + (m * 3), i2, dArr2);
            return;
        }
        cftexp1(n, a, aP, nw, w);
    }

    private void cftrec2(int n, double[] a, int aP, int nw, double[] w) {
        int m = n >> 2;
        cftmdl2(n, a, aP, w, nw - n);
        if (n > 512) {
            int i = m;
            double[] dArr = a;
            int i2 = nw;
            double[] dArr2 = w;
            cftrec1(i, dArr, aP, i2, dArr2);
            cftrec2(i, dArr, aP + m, i2, dArr2);
            cftrec1(i, dArr, aP + (m * 2), i2, dArr2);
            cftrec2(i, dArr, aP + (m * 3), i2, dArr2);
            return;
        }
        cftexp2(n, a, aP, nw, w);
    }

    private void cftexp1(int n, double[] a, int aP, int nw, double[] w) {
        int l = n >> 2;
        while (l > 128) {
            for (int k = l; k < n; k <<= 2) {
                for (int j = k - l; j < n; j += k * 4) {
                    int i = l;
                    double[] dArr = a;
                    double[] dArr2 = w;
                    cftmdl1(i, dArr, aP + j, dArr2, nw - (l >> 1));
                    cftmdl2(i, dArr, aP + k + j, dArr2, nw - l);
                    cftmdl1(i, dArr, (k * 2) + aP + j, dArr2, nw - (l >> 1));
                }
            }
            cftmdl1(l, a, (aP + n) - l, w, nw - (l >> 1));
            l >>= 2;
        }
        for (int k2 = l; k2 < n; k2 <<= 2) {
            for (int j2 = k2 - l; j2 < n; j2 += k2 * 4) {
                int i2 = l;
                double[] dArr3 = a;
                cftmdl1(i2, dArr3, aP + j2, w, nw - (l >> 1));
                cftfx41(i2, dArr3, aP + j2, nw, w);
                cftmdl2(i2, dArr3, aP + k2 + j2, w, nw - l);
                cftfx42(i2, dArr3, aP + k2 + j2, nw, w);
                cftmdl1(i2, dArr3, (k2 * 2) + aP + j2, w, nw - (l >> 1));
                cftfx41(i2, dArr3, (k2 * 2) + aP + j2, nw, w);
            }
        }
        int i3 = l;
        double[] dArr4 = a;
        cftmdl1(i3, dArr4, (aP + n) - l, w, nw - (l >> 1));
        cftfx41(i3, dArr4, (aP + n) - l, nw, w);
    }

    private void cftexp2(int n, double[] a, int aP, int nw, double[] w) {
        int m = n >> 1;
        int l = n >> 2;
        while (l > 128) {
            for (int k = l; k < m; k <<= 2) {
                for (int j = k - l; j < m; j += k * 2) {
                    int i = l;
                    double[] dArr = a;
                    double[] dArr2 = w;
                    cftmdl1(i, dArr, aP + j, dArr2, nw - (l >> 1));
                    cftmdl1(i, dArr, aP + m + j, dArr2, nw - (l >> 1));
                }
                for (int j2 = (k * 2) - l; j2 < m; j2 += k * 4) {
                    int i2 = l;
                    double[] dArr3 = a;
                    double[] dArr4 = w;
                    cftmdl2(i2, dArr3, aP + j2, dArr4, nw - l);
                    cftmdl2(i2, dArr3, aP + m + j2, dArr4, nw - l);
                }
            }
            l >>= 2;
        }
        for (int k2 = l; k2 < m; k2 <<= 2) {
            for (int j3 = k2 - l; j3 < m; j3 += k2 * 2) {
                int i3 = l;
                double[] dArr5 = a;
                cftmdl1(i3, dArr5, aP + j3, w, nw - (l >> 1));
                cftfx41(i3, dArr5, aP + j3, nw, w);
                cftmdl1(i3, dArr5, aP + m + j3, w, nw - (l >> 1));
                cftfx41(i3, dArr5, aP + m + j3, nw, w);
            }
            for (int j4 = (k2 * 2) - l; j4 < m; j4 += k2 * 4) {
                int i4 = l;
                double[] dArr6 = a;
                cftmdl2(i4, dArr6, aP + j4, w, nw - l);
                cftfx42(i4, dArr6, aP + j4, nw, w);
                cftmdl2(i4, dArr6, aP + m + j4, w, nw - l);
                cftfx42(i4, dArr6, aP + m + j4, nw, w);
            }
        }
    }

    private final void cftmdl1(int n, double[] a, int aP, double[] w, int wP) {
        int mh = n >> 3;
        int m = mh * 2;
        int j1 = m;
        int j2 = j1 + m;
        int j3 = j2 + m;
        double wk3i = a[aP + 0] + a[aP + j2];
        double x0i = a[aP + 1] + a[aP + j2 + 1];
        double x1r = a[aP + 0] - a[aP + j2];
        double x1i = a[aP + 1] - a[(aP + j2) + 1];
        double x2r = a[aP + j1] + a[aP + j3];
        double x2i = a[aP + j1 + 1] + a[aP + j3 + 1];
        double x3r = a[aP + j1] - a[aP + j3];
        double x3i = a[(aP + j1) + 1] - a[(aP + j3) + 1];
        a[aP + 0] = wk3i + x2r;
        a[aP + 1] = x0i + x2i;
        a[aP + j1] = wk3i - x2r;
        a[aP + j1 + 1] = x0i - x2i;
        a[aP + j2] = x1r - x3i;
        a[aP + j2 + 1] = x1i + x3r;
        a[aP + j3] = x1r + x3i;
        a[aP + j3 + 1] = x1i - x3r;
        int i = j1;
        int i2 = j2;
        double wn4r = w[wP + 1];
        int k = 0;
        int i3 = j3;
        int j = 2;
        while (j < mh) {
            k += 4;
            double wk1r = w[wP + k];
            double wk1i = w[wP + k + 1];
            double wk3r = w[wP + k + 2];
            double d = wk3i;
            double wk3i2 = -w[wP + k + 3];
            int j12 = j + m;
            int j22 = j12 + m;
            int j32 = j22 + m;
            double x0r = a[aP + j] + a[aP + j22];
            double x0i2 = a[aP + j + 1] + a[aP + j22 + 1];
            double x1r2 = a[aP + j] - a[aP + j22];
            double x1i2 = a[(aP + j) + 1] - a[(aP + j22) + 1];
            double x2r2 = a[aP + j12] + a[aP + j32];
            double x2i2 = a[aP + j12 + 1] + a[aP + j32 + 1];
            double x3r2 = a[aP + j12] - a[aP + j32];
            double x3i2 = a[(aP + j12) + 1] - a[(aP + j32) + 1];
            a[aP + j] = x0r + x2r2;
            a[aP + j + 1] = x0i2 + x2i2;
            a[aP + j12] = x0r - x2r2;
            a[aP + j12 + 1] = x0i2 - x2i2;
            double x0r2 = x1r2 - x3i2;
            double x0i3 = x1i2 + x3r2;
            a[aP + j22] = (wk1r * x0r2) - (wk1i * x0i3);
            a[aP + j22 + 1] = (wk1r * x0i3) + (wk1i * x0r2);
            double x0r3 = x1r2 + x3i2;
            double x0i4 = x1i2 - x3r2;
            a[aP + j32] = (wk3r * x0r3) + (wk3i2 * x0i4);
            a[aP + j32 + 1] = (wk3r * x0i4) - (wk3i2 * x0r3);
            int j0 = m - j;
            int j13 = j0 + m;
            int j23 = j13 + m;
            int j33 = j23 + m;
            double x0r4 = a[aP + j0] + a[aP + j23];
            double x0i5 = a[aP + j0 + 1] + a[aP + j23 + 1];
            double x1r3 = a[aP + j0] - a[aP + j23];
            double x1i3 = a[(aP + j0) + 1] - a[(aP + j23) + 1];
            double x2r3 = a[aP + j13] + a[aP + j33];
            double x2i3 = a[aP + j13 + 1] + a[aP + j33 + 1];
            double x3r3 = a[aP + j13] - a[aP + j33];
            double x3i3 = a[(aP + j13) + 1] - a[(aP + j33) + 1];
            a[aP + j0] = x0r4 + x2r3;
            a[aP + j0 + 1] = x0i5 + x2i3;
            a[aP + j13] = x0r4 - x2r3;
            a[aP + j13 + 1] = x0i5 - x2i3;
            double x0r5 = x1r3 - x3i3;
            double x0i6 = x1i3 + x3r3;
            a[aP + j23] = (wk1i * x0r5) - (wk1r * x0i6);
            a[aP + j23 + 1] = (wk1i * x0i6) + (wk1r * x0r5);
            double x0r6 = x1r3 + x3i3;
            double x0i7 = x1i3 - x3r3;
            a[aP + j33] = (wk3i2 * x0r6) + (wk3r * x0i7);
            a[aP + j33 + 1] = (wk3i2 * x0i7) - (wk3r * x0r6);
            j += 2;
            wk3i = x0r6;
            double d2 = x1r3;
            double x1r4 = x0i7;
            double x0i8 = x3i3;
            double x3i4 = x3r3;
            double x3r4 = x2i3;
            double x2i4 = x2r3;
            double x2r4 = x1i3;
            double x1i4 = d2;
        }
        double d3 = wk3i;
        int j02 = mh;
        int j14 = j02 + m;
        int j15 = j14 + m;
        int j24 = j15 + m;
        double x0r7 = a[aP + j02] + a[aP + j15];
        double x0i9 = a[aP + j02 + 1] + a[aP + j15 + 1];
        double x1r5 = a[aP + j02] - a[aP + j15];
        double x1i5 = a[(aP + j02) + 1] - a[(aP + j15) + 1];
        double x2r5 = a[aP + j14] + a[aP + j24];
        double x2i5 = a[aP + j14 + 1] + a[aP + j24 + 1];
        double x3r5 = a[aP + j14] - a[aP + j24];
        double x3i5 = a[(aP + j14) + 1] - a[(aP + j24) + 1];
        a[aP + j02] = x0r7 + x2r5;
        a[aP + j02 + 1] = x0i9 + x2i5;
        a[aP + j14] = x0r7 - x2r5;
        a[aP + j14 + 1] = x0i9 - x2i5;
        double x0r8 = x1r5 - x3i5;
        double x0r9 = x1i5 + x3r5;
        a[aP + j15] = (x0r8 - x0r9) * wn4r;
        a[aP + j15 + 1] = (x0r9 + x0r8) * wn4r;
        double x0r10 = x1r5 + x3i5;
        double x0i10 = x1i5 - x3r5;
        int i4 = mh;
        int i5 = m;
        a[aP + j24] = (-wn4r) * (x0r10 + x0i10);
        int i6 = j;
        int i7 = j02;
        a[aP + j24 + 1] = (-wn4r) * (x0i10 - x0r10);
    }

    private final void cftmdl2(int n, double[] a, int aP, double[] w, int wP) {
        int mh = n >> 3;
        int m = mh * 2;
        double wn4r = w[wP + 1];
        int j1 = m;
        int j2 = j1 + m;
        int j3 = j2 + m;
        double x0r = a[aP + 0] - a[(aP + j2) + 1];
        double x0i = a[aP + 1] + a[aP + j2];
        double x1r = a[aP + 0] + a[aP + j2 + 1];
        double x1i = a[aP + 1] - a[aP + j2];
        double x2r = a[aP + j1] - a[(aP + j3) + 1];
        double x2i = a[aP + j1 + 1] + a[aP + j3];
        double x3r = a[aP + j1] + a[aP + j3 + 1];
        double x3i = a[(aP + j1) + 1] - a[aP + j3];
        double y0r = (x2r - x2i) * wn4r;
        double y0i = (x2i + x2r) * wn4r;
        a[aP + 0] = x0r + y0r;
        a[aP + 1] = x0i + y0i;
        a[aP + j1] = x0r - y0r;
        a[aP + j1 + 1] = x0i - y0i;
        double y0r2 = (x3r - x3i) * wn4r;
        double y0i2 = (x3i + x3r) * wn4r;
        a[aP + j2] = x1r - y0i2;
        a[aP + j2 + 1] = x1i + y0r2;
        a[aP + j3] = x1r + y0i2;
        a[aP + j3 + 1] = x1i - y0r2;
        int k = 0;
        int kr = m * 2;
        double d = wn4r;
        int j = 2;
        double d2 = d;
        while (j < mh) {
            k += 4;
            double wk1r = w[wP + k];
            double wk1i = w[wP + k + 1];
            double wk3r = w[wP + k + 2];
            int i = j1;
            double wk3i = -w[wP + k + 3];
            kr -= 4;
            double wd1i = w[wP + kr];
            double wd1r = w[wP + kr + 1];
            double wd3i = w[wP + kr + 2];
            int i2 = j2;
            int i3 = j3;
            double wd3r = -w[wP + kr + 3];
            int j12 = j + m;
            int j22 = j12 + m;
            int j23 = j22 + m;
            double x0r2 = a[aP + j] - a[(aP + j22) + 1];
            double x0i2 = a[aP + j + 1] + a[aP + j22];
            double x1r2 = a[aP + j] + a[aP + j22 + 1];
            double x1i2 = a[(aP + j) + 1] - a[aP + j22];
            double x2r2 = a[aP + j12] - a[(aP + j23) + 1];
            double x2i2 = a[aP + j12 + 1] + a[aP + j23];
            double x3r2 = a[aP + j12] + a[aP + j23 + 1];
            double x3i2 = a[(aP + j12) + 1] - a[aP + j23];
            double y0r3 = (wk1r * x0r2) - (wk1i * x0i2);
            double y0i3 = (wk1r * x0i2) + (wk1i * x0r2);
            double y2r = (wd1r * x2r2) - (wd1i * x2i2);
            double y2i = (wd1r * x2i2) + (wd1i * x2r2);
            a[aP + j] = y0r3 + y2r;
            a[aP + j + 1] = y0i3 + y2i;
            a[aP + j12] = y0r3 - y2r;
            a[aP + j12 + 1] = y0i3 - y2i;
            double y0r4 = (wk3r * x1r2) + (wk3i * x1i2);
            double y0i4 = (wk3r * x1i2) - (wk3i * x1r2);
            double y2r2 = (wd3r * x3r2) + (wd3i * x3i2);
            double y2i2 = (wd3r * x3i2) - (wd3i * x3r2);
            a[aP + j22] = y0r4 + y2r2;
            a[aP + j22 + 1] = y0i4 + y2i2;
            a[aP + j23] = y0r4 - y2r2;
            a[aP + j23 + 1] = y0i4 - y2i2;
            int j0 = m - j;
            int j13 = j0 + m;
            int j24 = j13 + m;
            int j32 = j24 + m;
            double x0r3 = a[aP + j0] - a[(aP + j24) + 1];
            double x0i3 = a[aP + j0 + 1] + a[aP + j24];
            double x1r3 = a[aP + j0] + a[aP + j24 + 1];
            double x1i3 = a[(aP + j0) + 1] - a[aP + j24];
            double x2r3 = a[aP + j13] - a[(aP + j32) + 1];
            double x2i3 = a[aP + j13 + 1] + a[aP + j32];
            double x3r3 = a[aP + j13] + a[aP + j32 + 1];
            double x3i3 = a[(aP + j13) + 1] - a[aP + j32];
            double y0r5 = (wd1i * x0r3) - (wd1r * x0i3);
            double y0i5 = (wd1i * x0i3) + (wd1r * x0r3);
            double y2r3 = (wk1i * x2r3) - (wk1r * x2i3);
            double y2i3 = (wk1i * x2i3) + (wk1r * x2r3);
            a[aP + j0] = y0r5 + y2r3;
            a[aP + j0 + 1] = y0i5 + y2i3;
            a[aP + j13] = y0r5 - y2r3;
            a[aP + j13 + 1] = y0i5 - y2i3;
            double y0r6 = (wd3i * x1r3) + (wd3r * x1i3);
            double y0i6 = (wd3i * x1i3) - (wd3r * x1r3);
            double y2r4 = (wk3i * x3r3) + (wk3r * x3i3);
            double y2i4 = (wk3i * x3i3) - (wk3r * x3r3);
            a[aP + j24] = y0r6 + y2r4;
            a[aP + j24 + 1] = y0i6 + y2i4;
            a[aP + j32] = y0r6 - y2r4;
            a[aP + j32 + 1] = y0i6 - y2i4;
            j += 2;
            double y2i5 = x3i3;
            double d3 = y0r6;
            j1 = j13;
            j2 = j24;
            j3 = j32;
            double y0r7 = y0i6;
            double x3i4 = x3r3;
            double x3r4 = x2i3;
            double x2i4 = x2r3;
            double x2r4 = x1i3;
            double x1i4 = x1r3;
            double x1r4 = x0i3;
            double x0i4 = x0r3;
        }
        int j33 = j2;
        double wk1r2 = w[wP + m];
        double wk1i2 = w[wP + m + 1];
        int j02 = mh;
        int j14 = j02 + m;
        int j25 = j14 + m;
        int j34 = j25 + m;
        double x0r4 = a[aP + j02] - a[(aP + j25) + 1];
        double x0i5 = a[aP + j02 + 1] + a[aP + j25];
        double x1r5 = a[aP + j02] + a[aP + j25 + 1];
        double x1i5 = a[(aP + j02) + 1] - a[aP + j25];
        double x2r5 = a[aP + j14] - a[(aP + j34) + 1];
        double x2i5 = a[aP + j14 + 1] + a[aP + j34];
        double x3r5 = a[aP + j14] + a[aP + j34 + 1];
        double x3i5 = a[(aP + j14) + 1] - a[aP + j34];
        double y0r8 = (wk1r2 * x0r4) - (wk1i2 * x0i5);
        double y0i7 = (wk1r2 * x0i5) + (wk1i2 * x0r4);
        double y2r5 = (wk1i2 * x2r5) - (wk1r2 * x2i5);
        double d4 = (wk1i2 * x2i5) + (wk1r2 * x2r5);
        a[aP + j02] = y0r8 + y2r5;
        a[aP + j02 + 1] = y0i7 + d4;
        a[aP + j14] = y0r8 - y2r5;
        a[aP + j14 + 1] = y0i7 - d4;
        double y0r9 = (wk1i2 * x1r5) - (wk1r2 * x1i5);
        double y0i8 = (wk1i2 * x1i5) + (wk1r2 * x1r5);
        double y2r6 = (wk1r2 * x3r5) - (wk1i2 * x3i5);
        double y2i6 = (wk1r2 * x3i5) + (wk1i2 * x3r5);
        a[aP + j25] = y0r9 - y2r6;
        a[aP + j25 + 1] = y0i8 - y2i6;
        a[aP + j34] = y0r9 + y2r6;
        a[aP + j34 + 1] = y0i8 + y2i6;
    }

    private void cftfx41(int n, double[] a, int aP, int nw, double[] w) {
        if (n == 128) {
            cftf161(a, aP, w, nw - 8);
            cftf162(a, aP + 32, w, nw - 32);
            cftf161(a, aP + 64, w, nw - 8);
            cftf161(a, aP + 96, w, nw - 8);
            return;
        }
        cftf081(a, aP, w, nw - 16);
        cftf082(a, aP + 16, w, nw - 16);
        cftf081(a, aP + 32, w, nw - 16);
        cftf081(a, aP + 48, w, nw - 16);
    }

    private void cftfx42(int n, double[] a, int aP, int nw, double[] w) {
        if (n == 128) {
            cftf161(a, aP, w, nw - 8);
            cftf162(a, aP + 32, w, nw - 32);
            cftf161(a, aP + 64, w, nw - 8);
            cftf162(a, aP + 96, w, nw - 32);
            return;
        }
        cftf081(a, aP, w, nw - 16);
        cftf082(a, aP + 16, w, nw - 16);
        cftf081(a, aP + 32, w, nw - 16);
        cftf082(a, aP + 48, w, nw - 16);
    }

    private void cftf161(double[] a, int aP, double[] w, int wP) {
        double wn4r = w[wP + 1];
        double wk1i = w[wP + 2] * wn4r;
        double wk1r = w[wP + 2] + wk1i;
        double x0r = a[aP + 0] + a[aP + 16];
        double x0i = a[aP + 1] + a[aP + 17];
        double x1r = a[aP + 0] - a[aP + 16];
        double x1i = a[aP + 1] - a[aP + 17];
        double x2r = a[aP + 8] + a[aP + 24];
        double x2i = a[aP + 9] + a[aP + 25];
        double x3r = a[aP + 8] - a[aP + 24];
        double x3i = a[aP + 9] - a[aP + 25];
        double y0r = x0r + x2r;
        double y0i = x0i + x2i;
        double y4r = x0r - x2r;
        double y4i = x0i - x2i;
        double y8r = x1r - x3i;
        double y8i = x1i + x3r;
        double y12r = x1r + x3i;
        double y12i = x1i - x3r;
        double x0r2 = a[aP + 2] + a[aP + 18];
        double x0i2 = a[aP + 3] + a[aP + 19];
        double x1r2 = a[aP + 2] - a[aP + 18];
        double x1i2 = a[aP + 3] - a[aP + 19];
        double x2r2 = a[aP + 10] + a[aP + 26];
        double x2i2 = a[aP + 11] + a[aP + 27];
        double x3r2 = a[aP + 10] - a[aP + 26];
        double x3i2 = a[aP + 11] - a[aP + 27];
        double y1r = x0r2 + x2r2;
        double y1i = x0i2 + x2i2;
        double y5r = x0r2 - x2r2;
        double y5i = x0i2 - x2i2;
        double x0r3 = x1r2 - x3i2;
        double x0i3 = x1i2 + x3r2;
        double y9r = (wk1r * x0r3) - (wk1i * x0i3);
        double y9i = (wk1r * x0i3) + (wk1i * x0r3);
        double x0r4 = x1r2 + x3i2;
        double x0i4 = x1i2 - x3r2;
        double y13r = (wk1i * x0r4) - (wk1r * x0i4);
        double y13i = (wk1i * x0i4) + (wk1r * x0r4);
        double x0r5 = a[aP + 4] + a[aP + 20];
        double x0i5 = a[aP + 5] + a[aP + 21];
        double x1r3 = a[aP + 4] - a[aP + 20];
        double x1i3 = a[aP + 5] - a[aP + 21];
        double x2r3 = a[aP + 12] + a[aP + 28];
        double x2i3 = a[aP + 13] + a[aP + 29];
        double x3r3 = a[aP + 12] - a[aP + 28];
        double x3i3 = a[aP + 13] - a[aP + 29];
        double x3i4 = x0r5 + x2r3;
        double y2i = x0i5 + x2i3;
        double y6r = x0r5 - x2r3;
        double y6i = x0i5 - x2i3;
        double x0r6 = x1r3 - x3i3;
        double x0i6 = x1i3 + x3r3;
        double y10r = (x0r6 - x0i6) * wn4r;
        double y10i = (x0i6 + x0r6) * wn4r;
        double x0r7 = x1r3 + x3i3;
        double x0i7 = x1i3 - x3r3;
        double y14r = (x0r7 + x0i7) * wn4r;
        double y14i = (x0i7 - x0r7) * wn4r;
        double x0r8 = a[aP + 6] + a[aP + 22];
        double x0i8 = a[aP + 7] + a[aP + 23];
        double x1r4 = a[aP + 6] - a[aP + 22];
        double x1i4 = a[aP + 7] - a[aP + 23];
        double x2r4 = a[aP + 14] + a[aP + 30];
        double x2i4 = a[aP + 15] + a[aP + 31];
        double x3r4 = a[aP + 14] - a[aP + 30];
        double x3i5 = a[aP + 15] - a[aP + 31];
        double x3i6 = x0r8 + x2r4;
        double y3i = x0i8 + x2i4;
        double y7r = x0r8 - x2r4;
        double y7i = x0i8 - x2i4;
        double x0r9 = x1r4 - x3i5;
        double x0i9 = x1i4 + x3r4;
        double y11r = (wk1i * x0r9) - (wk1r * x0i9);
        double y11i = (wk1i * x0i9) + (wk1r * x0r9);
        double x0r10 = x1r4 + x3i5;
        double x0i10 = x1i4 - x3r4;
        double y15r = (wk1r * x0r10) - (wk1i * x0i10);
        double y15i = (wk1r * x0i10) + (wk1i * x0r10);
        double x0r11 = y12r - y14r;
        double x0i11 = y12i - y14i;
        double x1r5 = y12r + y14r;
        double x1i5 = y12i + y14i;
        double x2r5 = y13r - y15r;
        double x2i5 = y13i - y15i;
        double x3r5 = y13r + y15r;
        double x3i7 = y13i + y15i;
        a[aP + 24] = x0r11 + x2r5;
        a[aP + 25] = x0i11 + x2i5;
        a[aP + 26] = x0r11 - x2r5;
        a[aP + 27] = x0i11 - x2i5;
        a[aP + 28] = x1r5 - x3i7;
        a[aP + 29] = x1i5 + x3r5;
        a[aP + 30] = x1r5 + x3i7;
        a[aP + 31] = x1i5 - x3r5;
        double x0r12 = y8r + y10r;
        double x0i12 = y8i + y10i;
        double x1r6 = y8r - y10r;
        double x1i6 = y8i - y10i;
        double x2r6 = y9r + y11r;
        double x2i6 = y9i + y11i;
        double x3r6 = y9r - y11r;
        double x3i8 = y9i - y11i;
        a[aP + 16] = x0r12 + x2r6;
        a[aP + 17] = x0i12 + x2i6;
        a[aP + 18] = x0r12 - x2r6;
        a[aP + 19] = x0i12 - x2i6;
        a[aP + 20] = x1r6 - x3i8;
        a[aP + 21] = x1i6 + x3r6;
        a[aP + 22] = x1r6 + x3i8;
        a[aP + 23] = x1i6 - x3r6;
        double x0r13 = y5r - y7i;
        double x0i13 = y5i + y7r;
        double x2r7 = (x0r13 - x0i13) * wn4r;
        double x2i7 = (x0i13 + x0r13) * wn4r;
        double x0r14 = y5r + y7i;
        double x0i14 = y5i - y7r;
        double x3r7 = (x0r14 - x0i14) * wn4r;
        double x3i9 = (x0i14 + x0r14) * wn4r;
        double x0r15 = y4r - y6i;
        double x0i15 = y4i + y6r;
        double x1r7 = y4r + y6i;
        double x1i7 = y4i - y6r;
        a[aP + 8] = x0r15 + x2r7;
        a[aP + 9] = x0i15 + x2i7;
        a[aP + 10] = x0r15 - x2r7;
        a[aP + 11] = x0i15 - x2i7;
        a[aP + 12] = x1r7 - x3i9;
        a[aP + 13] = x1i7 + x3r7;
        a[aP + 14] = x1r7 + x3i9;
        a[aP + 15] = x1i7 - x3r7;
        double x0r16 = y0r + x3i4;
        double x0i16 = y0i + y2i;
        double x1r8 = y0r - x3i4;
        double x1i8 = y0i - y2i;
        double x2r8 = y1r + x3i6;
        double x2i8 = y1i + y3i;
        double x3r8 = y1r - x3i6;
        double x3i10 = y1i - y3i;
        a[aP + 0] = x0r16 + x2r8;
        a[aP + 1] = x0i16 + x2i8;
        a[aP + 2] = x0r16 - x2r8;
        a[aP + 3] = x0i16 - x2i8;
        a[aP + 4] = x1r8 - x3i10;
        a[aP + 5] = x1i8 + x3r8;
        a[aP + 6] = x1r8 + x3i10;
        a[aP + 7] = x1i8 - x3r8;
    }

    private void cftf162(double[] a, int aP, double[] w, int wP) {
        double wn4r = w[wP + 1];
        double wk1r = w[wP + 4];
        double wk1i = w[wP + 5];
        double wk3r = w[wP + 6];
        double wk3i = w[wP + 7];
        double wk2r = w[wP + 8];
        double wk2i = w[wP + 9];
        double x1r = a[aP + 0] - a[aP + 17];
        double x1i = a[aP + 1] + a[aP + 16];
        double x0r = a[aP + 8] - a[aP + 25];
        double x0i = a[aP + 9] + a[aP + 24];
        double x2r = (x0r - x0i) * wn4r;
        double x2i = (x0i + x0r) * wn4r;
        double y0r = x1r + x2r;
        double y0i = x1i + x2i;
        double y4r = x1r - x2r;
        double y4i = x1i - x2i;
        double x1r2 = a[aP + 0] + a[aP + 17];
        double x1i2 = a[aP + 1] - a[aP + 16];
        double x0r2 = a[aP + 8] + a[aP + 25];
        double x0i2 = a[aP + 9] - a[aP + 24];
        double x2r2 = (x0r2 - x0i2) * wn4r;
        double x2i2 = (x0i2 + x0r2) * wn4r;
        double y8r = x1r2 - x2i2;
        double y8i = x1i2 + x2r2;
        double y12r = x1r2 + x2i2;
        double y12i = x1i2 - x2r2;
        double x0r3 = a[aP + 2] - a[aP + 19];
        double x0i3 = a[aP + 3] + a[aP + 18];
        double x1r3 = (wk1r * x0r3) - (wk1i * x0i3);
        double x1i3 = (wk1r * x0i3) + (wk1i * x0r3);
        double x0r4 = a[aP + 10] - a[aP + 27];
        double x0i4 = a[aP + 11] + a[aP + 26];
        double x2r3 = (wk3i * x0r4) - (wk3r * x0i4);
        double x2i3 = (wk3i * x0i4) + (wk3r * x0r4);
        double y1r = x1r3 + x2r3;
        double y1i = x1i3 + x2i3;
        double y5r = x1r3 - x2r3;
        double y5i = x1i3 - x2i3;
        double x0r5 = a[aP + 2] + a[aP + 19];
        double x0i5 = a[aP + 3] - a[aP + 18];
        double x1r4 = (wk3r * x0r5) - (wk3i * x0i5);
        double x1i4 = (wk3r * x0i5) + (wk3i * x0r5);
        double x0r6 = a[aP + 10] + a[aP + 27];
        double x0i6 = a[aP + 11] - a[aP + 26];
        double x2r4 = (wk1r * x0r6) + (wk1i * x0i6);
        double x2i4 = (wk1r * x0i6) - (wk1i * x0r6);
        double y9r = x1r4 - x2r4;
        double y9i = x1i4 - x2i4;
        double y13r = x1r4 + x2r4;
        double y13i = x1i4 + x2i4;
        double x0r7 = a[aP + 4] - a[aP + 21];
        double x0i7 = a[aP + 5] + a[aP + 20];
        double x1r5 = (wk2r * x0r7) - (wk2i * x0i7);
        double x1i5 = (wk2r * x0i7) + (wk2i * x0r7);
        double x0r8 = a[aP + 12] - a[aP + 29];
        double x0i8 = a[aP + 13] + a[aP + 28];
        double x2r5 = (wk2i * x0r8) - (wk2r * x0i8);
        double x2i5 = (wk2i * x0i8) + (wk2r * x0r8);
        double x2i6 = x1r5 + x2r5;
        double y2i = x1i5 + x2i5;
        double y6r = x1r5 - x2r5;
        double y6i = x1i5 - x2i5;
        double x0r9 = a[aP + 4] + a[aP + 21];
        double x0i9 = a[aP + 5] - a[aP + 20];
        double x1r6 = (wk2i * x0r9) - (wk2r * x0i9);
        double x1i6 = (wk2i * x0i9) + (wk2r * x0r9);
        double x0r10 = a[aP + 12] + a[aP + 29];
        double x0i10 = a[aP + 13] - a[aP + 28];
        double x2r6 = (wk2r * x0r10) - (wk2i * x0i10);
        double x2i7 = (wk2r * x0i10) + (wk2i * x0r10);
        double x2i8 = x1r6 - x2r6;
        double y10i = x1i6 - x2i7;
        double y14r = x1r6 + x2r6;
        double y14i = x1i6 + x2i7;
        double x0r11 = a[aP + 6] - a[aP + 23];
        double x0i11 = a[aP + 7] + a[aP + 22];
        double x1r7 = (wk3r * x0r11) - (wk3i * x0i11);
        double x1i7 = (wk3r * x0i11) + (wk3i * x0r11);
        double x0r12 = a[aP + 14] - a[aP + 31];
        double x0i12 = a[aP + 15] + a[aP + 30];
        double x2r7 = (wk1i * x0r12) - (wk1r * x0i12);
        double x2i9 = (wk1i * x0i12) + (wk1r * x0r12);
        double x2i10 = x1r7 + x2r7;
        double y3i = x1i7 + x2i9;
        double y7r = x1r7 - x2r7;
        double y7i = x1i7 - x2i9;
        double x0r13 = a[aP + 6] + a[aP + 23];
        double x0i13 = a[aP + 7] - a[aP + 22];
        double x1r8 = (wk1i * x0r13) + (wk1r * x0i13);
        double x1i8 = (wk1i * x0i13) - (wk1r * x0r13);
        double x0r14 = a[aP + 14] + a[aP + 31];
        double x0i14 = a[aP + 15] - a[aP + 30];
        double x2r8 = (wk3i * x0r14) - (wk3r * x0i14);
        double x2i11 = (wk3i * x0i14) + (wk3r * x0r14);
        double y11r = x1r8 + x2r8;
        double y11i = x1i8 + x2i11;
        double y15r = x1r8 - x2r8;
        double y15i = x1i8 - x2i11;
        double x1r9 = y0r + x2i6;
        double x1i9 = y0i + y2i;
        double x2r9 = y1r + x2i10;
        double x2i12 = y1i + y3i;
        a[aP + 0] = x1r9 + x2r9;
        a[aP + 1] = x1i9 + x2i12;
        a[aP + 2] = x1r9 - x2r9;
        a[aP + 3] = x1i9 - x2i12;
        double x1r10 = y0r - x2i6;
        double x1i10 = y0i - y2i;
        double x2r10 = y1r - x2i10;
        double x2i13 = y1i - y3i;
        a[aP + 4] = x1r10 - x2i13;
        a[aP + 5] = x1i10 + x2r10;
        a[aP + 6] = x1r10 + x2i13;
        a[aP + 7] = x1i10 - x2r10;
        double x1r11 = y4r - y6i;
        double x1i11 = y4i + y6r;
        double x0r15 = y5r - y7i;
        double x0i15 = y5i + y7r;
        double x2r11 = (x0r15 - x0i15) * wn4r;
        double x2i14 = (x0i15 + x0r15) * wn4r;
        a[aP + 8] = x1r11 + x2r11;
        a[aP + 9] = x1i11 + x2i14;
        a[aP + 10] = x1r11 - x2r11;
        a[aP + 11] = x1i11 - x2i14;
        double x1r12 = y4r + y6i;
        double x1i12 = y4i - y6r;
        double x0r16 = y5r + y7i;
        double x0i16 = y5i - y7r;
        double x2r12 = (x0r16 - x0i16) * wn4r;
        double x2i15 = (x0i16 + x0r16) * wn4r;
        a[aP + 12] = x1r12 - x2i15;
        a[aP + 13] = x1i12 + x2r12;
        a[aP + 14] = x1r12 + x2i15;
        a[aP + 15] = x1i12 - x2r12;
        double x1r13 = y8r + x2i8;
        double x1i13 = y8i + y10i;
        double x2r13 = y9r - y11r;
        double x2i16 = y9i - y11i;
        a[aP + 16] = x1r13 + x2r13;
        a[aP + 17] = x1i13 + x2i16;
        a[aP + 18] = x1r13 - x2r13;
        a[aP + 19] = x1i13 - x2i16;
        double x1r14 = y8r - x2i8;
        double x1i14 = y8i - y10i;
        double x2r14 = y9r + y11r;
        double x2i17 = y9i + y11i;
        a[aP + 20] = x1r14 - x2i17;
        a[aP + 21] = x1i14 + x2r14;
        a[aP + 22] = x1r14 + x2i17;
        a[aP + 23] = x1i14 - x2r14;
        double x1r15 = y12r - y14i;
        double x1i15 = y12i + y14r;
        double x0r17 = y13r + y15i;
        double x0i17 = y13i - y15r;
        double x2r15 = (x0r17 - x0i17) * wn4r;
        double x2i18 = (x0i17 + x0r17) * wn4r;
        a[aP + 24] = x1r15 + x2r15;
        a[aP + 25] = x1i15 + x2i18;
        a[aP + 26] = x1r15 - x2r15;
        a[aP + 27] = x1i15 - x2i18;
        double x1r16 = y12r + y14i;
        double x1i16 = y12i - y14r;
        double x0r18 = y13r - y15i;
        double x0i18 = y13i + y15r;
        double x2r16 = (x0r18 - x0i18) * wn4r;
        double x2i19 = (x0i18 + x0r18) * wn4r;
        a[aP + 28] = x1r16 - x2i19;
        a[aP + 29] = x1i16 + x2r16;
        a[aP + 30] = x1r16 + x2i19;
        a[aP + 31] = x1i16 - x2r16;
    }

    private void cftf081(double[] a, int aP, double[] w, int wP) {
        double wn4r = w[wP + 1];
        double x0r = a[aP + 0] + a[aP + 8];
        double x0i = a[aP + 1] + a[aP + 9];
        double x1r = a[aP + 0] - a[aP + 8];
        double x1i = a[aP + 1] - a[aP + 9];
        double x2r = a[aP + 4] + a[aP + 12];
        double x2i = a[aP + 5] + a[aP + 13];
        double x3r = a[aP + 4] - a[aP + 12];
        double x3i = a[aP + 5] - a[aP + 13];
        double y0r = x0r + x2r;
        double y0i = x0i + x2i;
        double y2r = x0r - x2r;
        double y2i = x0i - x2i;
        double y1r = x1r - x3i;
        double y1i = x1i + x3r;
        double y3r = x1r + x3i;
        double y3i = x1i - x3r;
        double x0r2 = a[aP + 2] + a[aP + 10];
        double x0i2 = a[aP + 3] + a[aP + 11];
        double x1r2 = a[aP + 2] - a[aP + 10];
        double x1i2 = a[aP + 3] - a[aP + 11];
        double x2r2 = a[aP + 6] + a[aP + 14];
        double x2i2 = a[aP + 7] + a[aP + 15];
        double x3r2 = a[aP + 6] - a[aP + 14];
        double x3i2 = a[aP + 7] - a[aP + 15];
        double x3i3 = x0r2 + x2r2;
        double y4i = x0i2 + x2i2;
        double y6r = x0r2 - x2r2;
        double y6i = x0i2 - x2i2;
        double x0r3 = x1r2 - x3i2;
        double x0i3 = x1i2 + x3r2;
        double x2r3 = x1r2 + x3i2;
        double x2i3 = x1i2 - x3r2;
        double y5r = (x0r3 - x0i3) * wn4r;
        double y5i = (x0r3 + x0i3) * wn4r;
        double y7r = (x2r3 - x2i3) * wn4r;
        double y7i = (x2r3 + x2i3) * wn4r;
        a[aP + 8] = y1r + y5r;
        a[aP + 9] = y1i + y5i;
        a[aP + 10] = y1r - y5r;
        a[aP + 11] = y1i - y5i;
        a[aP + 12] = y3r - y7i;
        a[aP + 13] = y3i + y7r;
        a[aP + 14] = y3r + y7i;
        a[aP + 15] = y3i - y7r;
        a[aP + 0] = y0r + x3i3;
        a[aP + 1] = y0i + y4i;
        a[aP + 2] = y0r - x3i3;
        a[aP + 3] = y0i - y4i;
        a[aP + 4] = y2r - y6i;
        a[aP + 5] = y2i + y6r;
        a[aP + 6] = y2r + y6i;
        a[aP + 7] = y2i - y6r;
    }

    private void cftf082(double[] a, int aP, double[] w, int wP) {
        double wn4r = w[wP + 1];
        double wk1r = w[wP + 4];
        double wk1i = w[wP + 5];
        double y0r = a[aP + 0] - a[aP + 9];
        double y0i = a[aP + 1] + a[aP + 8];
        double y1r = a[aP + 0] + a[aP + 9];
        double y1i = a[aP + 1] - a[aP + 8];
        double x0r = a[aP + 4] - a[aP + 13];
        double x0i = a[aP + 5] + a[aP + 12];
        double y2r = (x0r - x0i) * wn4r;
        double y2i = (x0i + x0r) * wn4r;
        double x0r2 = a[aP + 4] + a[aP + 13];
        double x0i2 = a[aP + 5] - a[aP + 12];
        double y3r = (x0r2 - x0i2) * wn4r;
        double y3i = (x0i2 + x0r2) * wn4r;
        double x0r3 = a[aP + 2] - a[aP + 11];
        double x0i3 = a[aP + 3] + a[aP + 10];
        double y4r = (wk1r * x0r3) - (wk1i * x0i3);
        double y4i = (wk1r * x0i3) + (wk1i * x0r3);
        double x0r4 = a[aP + 2] + a[aP + 11];
        double x0i4 = a[aP + 3] - a[aP + 10];
        double y5r = (wk1i * x0r4) - (wk1r * x0i4);
        double y5i = (wk1i * x0i4) + (wk1r * x0r4);
        double x0r5 = a[aP + 6] - a[aP + 15];
        double x0i5 = a[aP + 7] + a[aP + 14];
        double y6r = (wk1i * x0r5) - (wk1r * x0i5);
        double y6i = (wk1i * x0i5) + (wk1r * x0r5);
        double x0r6 = a[aP + 6] + a[aP + 15];
        double x0i6 = a[aP + 7] - a[aP + 14];
        double y7r = (wk1r * x0r6) - (wk1i * x0i6);
        double y7i = (wk1r * x0i6) + (wk1i * x0r6);
        double x0r7 = y0r + y2r;
        double x0i7 = y0i + y2i;
        double x1r = y4r + y6r;
        double x1i = y4i + y6i;
        a[aP + 0] = x0r7 + x1r;
        a[aP + 1] = x0i7 + x1i;
        a[aP + 2] = x0r7 - x1r;
        a[aP + 3] = x0i7 - x1i;
        double x0r8 = y0r - y2r;
        double x0i8 = y0i - y2i;
        double x1r2 = y4r - y6r;
        double x1i2 = y4i - y6i;
        a[aP + 4] = x0r8 - x1i2;
        a[aP + 5] = x0i8 + x1r2;
        a[aP + 6] = x0r8 + x1i2;
        a[aP + 7] = x0i8 - x1r2;
        double x0r9 = y1r - y3i;
        double x0i9 = y1i + y3r;
        double x1r3 = y5r - y7r;
        double x1i3 = y5i - y7i;
        a[aP + 8] = x0r9 + x1r3;
        a[aP + 9] = x0i9 + x1i3;
        a[aP + 10] = x0r9 - x1r3;
        a[aP + 11] = x0i9 - x1i3;
        double x0r10 = y1r + y3i;
        double x0i10 = y1i - y3r;
        double x1r4 = y5r + y7r;
        double x1i4 = y5i + y7i;
        a[aP + 12] = x0r10 - x1i4;
        a[aP + 13] = x0i10 + x1r4;
        a[aP + 14] = x0r10 + x1i4;
        a[aP + 15] = x0i10 - x1r4;
    }

    private void cftf040(double[] a) {
        double x0r = a[0] + a[4];
        double x0i = a[1] + a[5];
        double x1r = a[0] - a[4];
        double x1i = a[1] - a[5];
        double x2r = a[2] + a[6];
        double x2i = a[3] + a[7];
        double x3r = a[2] - a[6];
        double x3i = a[3] - a[7];
        a[0] = x0r + x2r;
        a[1] = x0i + x2i;
        a[4] = x0r - x2r;
        a[5] = x0i - x2i;
        a[2] = x1r - x3i;
        a[3] = x1i + x3r;
        a[6] = x1r + x3i;
        a[7] = x1i - x3r;
    }

    private void cftb040(double[] a) {
        double x0r = a[0] + a[4];
        double x0i = a[1] + a[5];
        double x1r = a[0] - a[4];
        double x1i = a[1] - a[5];
        double x2r = a[2] + a[6];
        double x2i = a[3] + a[7];
        double x3r = a[2] - a[6];
        double x3i = a[3] - a[7];
        a[0] = x0r + x2r;
        a[1] = x0i + x2i;
        a[4] = x0r - x2r;
        a[5] = x0i - x2i;
        a[2] = x1r + x3i;
        a[3] = x1i - x3r;
        a[6] = x1r - x3i;
        a[7] = x1i + x3r;
    }

    private void cftx020(double[] a) {
        double x0i = a[1] - a[3];
        a[0] = a[0] + a[2];
        a[1] = a[1] + a[3];
        a[2] = a[0] - a[2];
        a[3] = x0i;
    }

    private void rftfsub(int n, double[] a, int nc, double[] c, int cP) {
        int m = n >> 1;
        int ks = (nc * 2) / m;
        int kk = 0;
        for (int j = 2; j < m; j += 2) {
            int k = n - j;
            kk += ks;
            double wkr = 0.5d - c[(cP + nc) - kk];
            double wki = c[cP + kk];
            double xr = a[j] - a[k];
            double xi = a[j + 1] + a[k + 1];
            double yr = (wkr * xr) - (wki * xi);
            double yi = (wkr * xi) + (wki * xr);
            a[j] = a[j] - yr;
            int i = j + 1;
            a[i] = a[i] - yi;
            a[k] = a[k] + yr;
            int i2 = k + 1;
            a[i2] = a[i2] - yi;
        }
    }

    private void rftbsub(int n, double[] a, int nc, double[] c, int cP) {
        int m = n >> 1;
        int ks = (nc * 2) / m;
        int kk = 0;
        for (int j = 2; j < m; j += 2) {
            int k = n - j;
            kk += ks;
            double wkr = 0.5d - c[(cP + nc) - kk];
            double wki = c[cP + kk];
            double xr = a[j] - a[k];
            double xi = a[j + 1] + a[k + 1];
            double yr = (wkr * xr) + (wki * xi);
            double yi = (wkr * xi) - (wki * xr);
            a[j] = a[j] - yr;
            int i = j + 1;
            a[i] = a[i] - yi;
            a[k] = a[k] + yr;
            int i2 = k + 1;
            a[i2] = a[i2] - yi;
        }
    }

    private void dctsub(int n, double[] a, int nc, double[] c, int cP) {
        int m = n >> 1;
        int ks = nc / n;
        int kk = 0;
        for (int j = 1; j < m; j++) {
            int k = n - j;
            kk += ks;
            double wkr = c[cP + kk] - c[(cP + nc) - kk];
            double wki = c[cP + kk] + c[(cP + nc) - kk];
            double xr = (a[j] * wki) - (a[k] * wkr);
            a[j] = (a[j] * wkr) + (a[k] * wki);
            a[k] = xr;
        }
        a[m] = a[m] * c[cP + 0];
    }

    private void dstsub(int n, double[] a, int nc, double[] c, int cP) {
        int m = n >> 1;
        int ks = nc / n;
        int kk = 0;
        for (int j = 1; j < m; j++) {
            int k = n - j;
            kk += ks;
            double wkr = c[cP + kk] - c[(cP + nc) - kk];
            double wki = c[cP + kk] + c[(cP + nc) - kk];
            double xr = (a[k] * wki) - (a[j] * wkr);
            a[k] = (a[k] * wkr) + (a[j] * wki);
            a[j] = xr;
        }
        a[m] = a[m] * c[cP + 0];
    }
}
