package im.bclpbkiauv.ui.utils.translate.ssrc;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import io.reactivex.annotations.SchedulerSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import kotlin.UByte;
import kotlin.jvm.internal.ByteCompanionObject;

public class SSRC {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int POOLSIZE = 97;
    private static final int RANDBUFLEN = 65536;
    private static final String VERSION = "1.30";
    private static final double[] presets = {0.7d, 0.9d, 0.18d};
    private static final int[] samp = {8, 18, 27, 8, 8, 8, 10, 9};
    private static final int[] scoeffreq = {0, 48000, 44100, 37800, 32000, 22050, 48000, 44100};
    private static final int[] scoeflen = {1, 16, 20, 16, 16, 15, 16, 15};
    private static final double[][] shapercoefs = {new double[]{-1.0d}, new double[]{-2.87207293510437d, 5.041323184967041d, -6.244299411773682d, 5.848398685455322d, -3.706754207611084d, 1.0495119094848633d, 1.1830236911773682d, -2.1126792430877686d, 1.9094531536102295d, -0.9991308450698853d, 0.17090806365013123d, 0.32615602016448975d, -0.39127644896507263d, 0.2687646150588989d, -0.0976761057972908d, 0.023473845794796944d}, new double[]{-2.6773197650909424d, 4.830892562866211d, -6.570110321044922d, 7.4572014808654785d, -6.726327419281006d, 4.848165035247803d, -2.0412089824676514d, -0.7006359100341797d, 2.95375657081604d, -4.080038547515869d, 4.184521675109863d, -3.331181287765503d, 2.117992639541626d, -0.879302978515625d, 0.031759146600961685d, 0.4238278865814209d, -0.4788210391998291d, 0.35490813851356506d, -0.1749683916568756d, 0.06090816855430603d}, new double[]{-1.6335992813110352d, 2.261549234390259d, -2.407702922821045d, 2.634171724319458d, -2.144036293029785d, 1.8153258562088013d, -1.0816224813461304d, 0.703026533126831d, -0.15991993248462677d, -0.04154951870441437d, 0.2941657602787018d, -0.25183168053627014d, 0.27766478061676025d, -0.15785403549671173d, 0.10165894031524658d, -0.016833892092108727d}, new double[]{-0.8290129899978638d, 0.9892265796661377d, -0.5982571244239807d, 1.0028809309005737d, -0.5993821620941162d, 0.7950245141983032d, -0.42723315954208374d, 0.5449252724647522d, -0.3079260587692261d, 0.3687179982662201d, -0.187920480966568d, 0.2261127084493637d, -0.10573341697454453d, 0.11435490846633911d, -0.0388006791472435d, 0.040842197835445404d}, new double[]{-0.06522997468709946d, 0.5498126149177551d, 0.4027854800224304d, 0.3178376853466034d, 0.2820179760456085d, 0.16985194385051727d, 0.15433363616466522d, 0.12507140636444092d, 0.08903945237398148d, 0.06441012024879456d, 0.04714600369334221d, 0.03280523791909218d, 0.028495194390416145d, 0.011695005930960178d, 0.011831838637590408d}, new double[]{-2.3925774097442627d, 3.4350297451019287d, -3.185370922088623d, 1.8117271661758423d, 0.2012477070093155d, -1.4759907722473145d, 1.7210904359817505d, -0.9774670004844666d, 0.13790138065814972d, 0.38185903429985046d, -0.27421241998672485d, -0.06658421456813812d, 0.35223302245140076d, -0.37672343850135803d, 0.23964276909828186d, -0.06867482513189316d}, new double[]{-2.0833916664123535d, 3.0418450832366943d, -3.204789876937866d, 2.757192611694336d, -1.4978630542755127d, 0.34275946021080017d, 0.7173374891281128d, -1.073705792427063d, 1.0225815773010254d, -0.5664999485015869d, 0.20968692004680634d, 0.06537853181362152d, -0.10322438180446625d, 0.06744202226400375d, 0.00495197344571352d}};
    private double AA;
    private double DF;
    private int FFTFIRLEN;
    private ByteOrder byteOrder;
    private SplitRadixFft fft;
    private long lastshowed;
    private int lastshowed2;
    private boolean quiet;
    private double[] randbuf;
    private int randptr;
    private double[][] shapebuf;
    private int shaper_clipmax;
    private int shaper_clipmin;
    private int shaper_len;
    private int shaper_type;
    private long starttime;

    private int RINT(double x) {
        return (int) (x >= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 0.5d + x : x - 0.5d);
    }

    public int init_shaper(int freq, int nch, int min, int max, int dtype, int pdf, double noiseamp) {
        int[] pool;
        int i = nch;
        int i2 = dtype;
        int i3 = pdf;
        int i4 = 97;
        int[] pool2 = new int[97];
        int i5 = 1;
        while (true) {
            if (i5 >= 6) {
                int i6 = freq;
                break;
            } else if (freq == scoeffreq[i5]) {
                break;
            } else {
                i5++;
            }
        }
        if ((i2 == 3 || i2 == 4) && i5 == 6) {
            System.err.printf("Warning: ATH based noise shaping for destination frequency %dHz is not available, using triangular dither\n", new Object[]{Integer.valueOf(freq)});
        }
        if (i2 == 2 || i5 == 6) {
            i5 = 0;
        }
        if (i2 == 4 && (i5 == 1 || i5 == 2)) {
            i5 += 5;
        }
        this.shaper_type = i5;
        this.shapebuf = new double[i][];
        this.shaper_len = scoeflen[i5];
        for (int i7 = 0; i7 < i; i7++) {
            this.shapebuf[i7] = new double[this.shaper_len];
        }
        this.shaper_clipmin = min;
        this.shaper_clipmax = max;
        this.randbuf = new double[65536];
        Random random = new Random(System.currentTimeMillis());
        for (int i8 = 0; i8 < 97; i8++) {
            pool2[i8] = random.nextInt();
        }
        if (i3 == 0) {
            int[] pool3 = pool2;
            for (int i9 = 0; i9 < 65536; i9++) {
                int p = random.nextInt() % 97;
                int r = pool3[p];
                pool3[p] = random.nextInt();
                this.randbuf[i9] = ((((double) r) / 2.147483647E9d) - 0.5d) * noiseamp;
            }
        } else if (i3 == 1) {
            int[] pool4 = pool2;
            int i10 = 0;
            while (i10 < 65536) {
                int p2 = random.nextInt() % 97;
                int r1 = pool4[p2];
                pool4[p2] = random.nextInt();
                int p3 = random.nextInt() % 97;
                int r2 = pool4[p3];
                pool4[p3] = random.nextInt();
                int i11 = r1;
                this.randbuf[i10] = noiseamp * ((((double) r1) / 2.147483647E9d) - (((double) r2) / 2.147483647E9d));
                i10++;
                int i12 = min;
            }
        } else if (i3 != 2) {
            int[] iArr = pool2;
        } else {
            int sw = 0;
            double t = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            double u = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            int i13 = 0;
            for (int i14 = 65536; i13 < i14; i14 = 65536) {
                if (sw == 0) {
                    sw = 1;
                    int p4 = random.nextInt() % i4;
                    double r3 = ((double) pool2[p4]) / 2.147483647E9d;
                    pool2[p4] = random.nextInt();
                    if (r3 == 1.0d) {
                        r3 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
                    }
                    t = Math.sqrt(Math.log(1.0d - r3) * -2.0d);
                    int p5 = random.nextInt() % i4;
                    pool = pool2;
                    pool[p5] = random.nextInt();
                    double u2 = 6.283185307179586d * (((double) pool2[p5]) / 2.147483647E9d);
                    this.randbuf[i13] = noiseamp * t * Math.cos(u2);
                    u = u2;
                } else {
                    pool = pool2;
                    this.randbuf[i13] = noiseamp * t * Math.sin(u);
                    sw = 0;
                }
                i13++;
                pool2 = pool;
                i4 = 97;
            }
        }
        this.randptr = 0;
        if (i2 == 0 || i2 == 1) {
            return 1;
        }
        return samp[this.shaper_type];
    }

    public int do_shaping(double s, double[] peak, int dtype, int ch) {
        int i;
        double s2;
        if (dtype == 1) {
            double[] dArr = this.randbuf;
            int i2 = this.randptr;
            this.randptr = i2 + 1;
            double s3 = s + dArr[65535 & i2];
            int i3 = this.shaper_clipmin;
            if (s3 < ((double) i3)) {
                double d = s3 / ((double) i3);
                peak[0] = peak[0] < d ? d : peak[0];
                s3 = (double) this.shaper_clipmin;
            }
            int i4 = this.shaper_clipmax;
            if (s3 > ((double) i4)) {
                double d2 = s3 / ((double) i4);
                peak[0] = peak[0] < d2 ? d2 : peak[0];
                s3 = (double) this.shaper_clipmax;
            }
            return RINT(s3);
        }
        double h = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        int i5 = 0;
        while (true) {
            i = this.shaper_len;
            if (i5 >= i) {
                break;
            }
            h += shapercoefs[this.shaper_type][i5] * this.shapebuf[ch][i5];
            i5++;
        }
        double s4 = s + h;
        double u = s4;
        double[] dArr2 = this.randbuf;
        int i6 = this.randptr;
        this.randptr = i6 + 1;
        double s5 = s4 + dArr2[65535 & i6];
        for (int i7 = i - 2; i7 >= 0; i7--) {
            double[][] dArr3 = this.shapebuf;
            dArr3[ch][i7 + 1] = dArr3[ch][i7];
        }
        int i8 = this.shaper_clipmin;
        if (s5 < ((double) i8)) {
            double d3 = s5 / ((double) i8);
            peak[0] = peak[0] < d3 ? d3 : peak[0];
            s2 = (double) this.shaper_clipmin;
            double[][] dArr4 = this.shapebuf;
            dArr4[ch][0] = s2 - u;
            if (dArr4[ch][0] > 1.0d) {
                dArr4[ch][0] = 1.0d;
            }
            double[][] dArr5 = this.shapebuf;
            if (dArr5[ch][0] < -1.0d) {
                dArr5[ch][0] = -1.0d;
            }
        } else {
            int i9 = this.shaper_clipmax;
            if (s5 > ((double) i9)) {
                double d4 = s5 / ((double) i9);
                peak[0] = peak[0] < d4 ? d4 : peak[0];
                s2 = (double) this.shaper_clipmax;
                double[][] dArr6 = this.shapebuf;
                dArr6[ch][0] = s2 - u;
                if (dArr6[ch][0] > 1.0d) {
                    dArr6[ch][0] = 1.0d;
                }
                double[][] dArr7 = this.shapebuf;
                if (dArr7[ch][0] < -1.0d) {
                    dArr7[ch][0] = -1.0d;
                }
            } else {
                s2 = (double) RINT(s5);
                this.shapebuf[ch][0] = s2 - u;
            }
        }
        return (int) s2;
    }

    private void quit_shaper(int nch) {
    }

    private double alpha(double a) {
        if (a <= 21.0d) {
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
        if (a <= 50.0d) {
            return (Math.pow(a - 21.0d, 0.4d) * 0.5842d) + ((a - 21.0d) * 0.07886d);
        }
        return (a - 8.7d) * 0.1102d;
    }

    private double win(double n, int len, double alp, double iza) {
        return I0Bessel.value(Math.sqrt(1.0d - (((4.0d * n) * n) / ((((double) len) - 1.0d) * (((double) len) - 1.0d)))) * alp) / iza;
    }

    private double sinc(double x) {
        if (x == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            return 1.0d;
        }
        return Math.sin(x) / x;
    }

    private double hn_lpf(int n, double lpf, double fs) {
        double t = 1.0d / fs;
        return 2.0d * lpf * t * sinc(((double) n) * 6.283185307179586d * lpf * t);
    }

    private void usage() {
        System.err.printf("http://shibatch.sourceforge.net/\n\n", new Object[0]);
        System.err.printf("usage: ssrc [<options>] <source wav file> <destination wav file>\n", new Object[0]);
        System.err.printf("options : --rate <sampling rate>     output sample rate\n", new Object[0]);
        System.err.printf("          --att <attenuation(dB)>    attenuate signal\n", new Object[0]);
        System.err.printf("          --bits <number of bits>    output quantization bit length\n", new Object[0]);
        System.err.printf("          --tmpfile <file name>      specify temporal file\n", new Object[0]);
        System.err.printf("          --twopass                  two pass processing to avoid clipping\n", new Object[0]);
        System.err.printf("          --normalize                normalize the wave file\n", new Object[0]);
        System.err.printf("          --quiet                    nothing displayed except error\n", new Object[0]);
        System.err.printf("          --dither [<type>]          dithering\n", new Object[0]);
        System.err.printf("                                       0 : no dither\n", new Object[0]);
        System.err.printf("                                       1 : no noise shaping\n", new Object[0]);
        System.err.printf("                                       2 : triangular spectral shape\n", new Object[0]);
        System.err.printf("                                       3 : ATH based noise shaping\n", new Object[0]);
        System.err.printf("                                       4 : less dither amplitude than type 3\n", new Object[0]);
        System.err.printf("          --pdf <type> [<amp>]       select p.d.f. of noise\n", new Object[0]);
        System.err.printf("                                       0 : rectangular\n", new Object[0]);
        System.err.printf("                                       1 : triangular\n", new Object[0]);
        System.err.printf("                                       2 : Gaussian\n", new Object[0]);
        System.err.printf("          --profile <type>           specify profile\n", new Object[0]);
        System.err.printf("                                       standard : the default quality\n", new Object[0]);
        System.err.printf("                                       fast     : fast, not so bad quality\n", new Object[0]);
    }

    private void fmterr(int x) {
        throw new IllegalStateException("unknown error " + x);
    }

    private void setstarttime() {
        this.starttime = System.currentTimeMillis();
        this.lastshowed = 0;
        this.lastshowed2 = -1;
    }

    private void showprogress(double p) {
        int eta;
        if (!this.quiet) {
            long t = System.currentTimeMillis() - this.starttime;
            if (p == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                eta = 0;
            } else {
                eta = (int) ((((double) t) * (1.0d - p)) / p);
            }
            int pc = (int) (100.0d * p);
            if (!(pc == this.lastshowed2 && t == this.lastshowed)) {
                System.err.printf(" %3d%% processed", new Object[]{Integer.valueOf(pc)});
                this.lastshowed2 = pc;
            }
            if (t != this.lastshowed) {
                System.err.printf(", ETA =%4dmsec", new Object[]{Integer.valueOf(eta)});
                this.lastshowed = t;
            }
            System.err.printf("\r", new Object[0]);
            System.err.flush();
        }
    }

    private int gcd(int x, int y) {
        while (y != 0) {
            int t = x % y;
            x = y;
            y = t;
        }
        return x;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v0, resolved type: double[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v0, resolved type: double[][]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v0, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v29, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v30, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v1, resolved type: double[][]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v31, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v32, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v2, resolved type: double[][]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: double[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v59, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v1, resolved type: double[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v16, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v2, resolved type: double[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v95, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v29, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v30, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v31, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v32, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v33, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v34, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v35, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v101, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v65, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v41, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v44, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v45, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v46, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v48, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v52, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v53, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v54, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v55, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v56, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v57, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v58, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v59, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v12, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v14, resolved type: double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r69v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v133, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v134, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r71v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v145, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r69v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v147, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v148, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v149, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r69v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v151, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v152, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v153, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v154, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v155, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r69v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r69v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v156, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v65, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v157, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v159, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v100, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v162, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v163, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v164, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v166, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v167, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v168, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v170, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v171, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v172, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v173, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v122, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v126, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v194, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v3, resolved type: int} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0aae  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0aba  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public double upsample(java.io.InputStream r77, java.io.OutputStream r78, int r79, int r80, int r81, int r82, int r83, double r84, int r86, boolean r87, int r88) throws java.io.IOException {
        /*
            r76 = this;
            r8 = r76
            r9 = r78
            r10 = r79
            r11 = r80
            r12 = r81
            r13 = r82
            r14 = r83
            java.lang.Class<double> r15 = double.class
            r0 = 0
            r16 = 0
            r17 = 0
            r6 = 1
            double[] r1 = new double[r6]
            r18 = 0
            r7 = 0
            r1[r7] = r18
            r20 = r1
            r21 = 0
            int r4 = r8.FFTFIRLEN
            double r1 = r8.AA
            r22 = 4611686018427387904(0x4000000000000000, double:2.0)
            int r24 = r8.gcd(r13, r14)
            int r3 = r13 / r24
            int r5 = r3 * r14
            int r3 = r5 / r14
            r25 = 5
            r9 = 2
            r6 = 1
            if (r3 != r6) goto L_0x003b
            r0 = 1
            r30 = r0
            goto L_0x004d
        L_0x003b:
            int r3 = r5 / r14
            int r3 = r3 % r9
            if (r3 != 0) goto L_0x0044
            r0 = 2
            r30 = r0
            goto L_0x004d
        L_0x0044:
            int r3 = r5 / r14
            r6 = 3
            int r3 = r3 % r6
            if (r3 != 0) goto L_0x0b04
            r0 = 3
            r30 = r0
        L_0x004d:
            int r0 = r14 * r30
            int r0 = r0 / r9
            int r3 = r13 / 2
            int r0 = r0 - r3
            int r0 = r0 * 2
            double r7 = (double) r0
            double r31 = r7 / r22
            int r0 = r13 / 2
            double r7 = (double) r0
            int r0 = r14 * r30
            int r0 = r0 / r9
            int r3 = r13 / 2
            int r0 = r0 - r3
            double r9 = (double) r0
            double r9 = r9 / r22
            double r8 = r7 + r9
            r34 = 4624273579385888440(0x402cb851eb851eb8, double:14.36)
            r36 = 4620636922686786765(0x401fcccccccccccd, double:7.95)
            r38 = 4626604192193052672(0x4035000000000000, double:21.0)
            int r0 = (r1 > r38 ? 1 : (r1 == r38 ? 0 : -1))
            if (r0 > 0) goto L_0x007c
            r40 = 4606481658697998559(0x3fed82a9930be0df, double:0.9222)
            goto L_0x0080
        L_0x007c:
            double r40 = r1 - r36
            double r40 = r40 / r34
        L_0x0080:
            double r6 = (double) r5
            double r6 = r6 / r31
            double r6 = r6 * r40
            r10 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r6 = r6 + r10
            int r0 = (int) r6
            int r3 = r0 % 2
            if (r3 != 0) goto L_0x0092
            int r0 = r0 + 1
            r43 = r0
            goto L_0x0094
        L_0x0092:
            r43 = r0
        L_0x0094:
            r6 = r76
            double r44 = r6.alpha(r1)
            double r46 = im.bclpbkiauv.ui.utils.translate.ssrc.I0Bessel.value(r44)
            int r48 = r5 / r13
            int r0 = r43 / r48
            r3 = 1
            int r7 = r0 + 1
            int r0 = r48 * r30
            int[] r3 = new int[r0]
            r0 = 0
        L_0x00aa:
            int r10 = r48 * r30
            if (r0 >= r10) goto L_0x00cb
            int r10 = r5 / r13
            int r11 = r14 * r30
            int r11 = r5 / r11
            int r11 = r11 * r0
            int r49 = r5 / r13
            int r11 = r11 % r49
            int r10 = r10 - r11
            r3[r0] = r10
            r10 = r3[r0]
            int r11 = r5 / r13
            if (r10 != r11) goto L_0x00c6
            r10 = 0
            r3[r0] = r10
        L_0x00c6:
            int r0 = r0 + 1
            r10 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x00aa
        L_0x00cb:
            int r10 = r48 * r30
            int[] r10 = new int[r10]
            r0 = 0
        L_0x00d0:
            int r11 = r48 * r30
            if (r0 >= r11) goto L_0x00f4
            r11 = r3[r0]
            int r49 = r14 * r30
            r50 = r1
            int r1 = r5 / r49
            if (r11 >= r1) goto L_0x00e1
            r1 = r79
            goto L_0x00e2
        L_0x00e1:
            r1 = 0
        L_0x00e2:
            r10[r0] = r1
            r1 = r3[r0]
            int r2 = r5 / r13
            if (r1 != r2) goto L_0x00ee
            r11 = 0
            r3[r0] = r11
            goto L_0x00ef
        L_0x00ee:
            r11 = 0
        L_0x00ef:
            int r0 = r0 + 1
            r1 = r50
            goto L_0x00d0
        L_0x00f4:
            r50 = r1
            r11 = 0
            r1 = 2
            int[] r2 = new int[r1]
            r26 = 1
            r2[r26] = r7
            r2[r11] = r48
            java.lang.Object r1 = java.lang.reflect.Array.newInstance(r15, r2)
            r27 = r1
            double[][] r27 = (double[][]) r27
            int r1 = r43 / 2
            int r0 = -r1
            r1 = r0
        L_0x010c:
            int r0 = r43 / 2
            if (r1 > r0) goto L_0x015d
            int r0 = r43 / 2
            int r0 = r0 + r1
            int r0 = r0 % r48
            r49 = r27[r0]
            int r0 = r43 / 2
            int r0 = r0 + r1
            int r52 = r0 / r48
            double r11 = (double) r1
            r0 = r76
            r54 = r1
            r1 = r11
            r11 = r3
            r3 = r43
            r12 = r4
            r55 = r10
            r10 = r5
            r4 = r44
            r57 = r7
            r56 = r11
            r29 = r15
            r15 = 0
            r11 = r6
            r6 = r46
            double r6 = r0.win(r1, r3, r4, r6)
            double r4 = (double) r10
            r1 = r54
            r2 = r8
            double r0 = r0.hn_lpf(r1, r2, r4)
            double r6 = r6 * r0
            double r0 = (double) r10
            double r6 = r6 * r0
            double r0 = (double) r13
            double r6 = r6 / r0
            r49[r52] = r6
            int r1 = r54 + 1
            r5 = r10
            r6 = r11
            r4 = r12
            r15 = r29
            r10 = r55
            r3 = r56
            r7 = r57
            r11 = 0
            r26 = 1
            r12 = r81
            goto L_0x010c
        L_0x015d:
            r54 = r1
            r56 = r3
            r12 = r4
            r11 = r6
            r57 = r7
            r55 = r10
            r29 = r15
            r15 = 0
            r10 = r5
            double r8 = r11.AA
            int r0 = (r8 > r38 ? 1 : (r8 == r38 ? 0 : -1))
            if (r0 > 0) goto L_0x0179
            r0 = 4606481658697998559(0x3fed82a9930be0df, double:0.9222)
            r31 = r0
            goto L_0x017f
        L_0x0179:
            double r0 = r8 - r36
            double r0 = r0 / r34
            r31 = r0
        L_0x017f:
            int r6 = r14 * r30
            r0 = 1
        L_0x0182:
            int r4 = r12 * r0
            int r1 = r4 % 2
            if (r1 != 0) goto L_0x018c
            int r4 = r4 + -1
            r7 = r4
            goto L_0x018d
        L_0x018c:
            r7 = r4
        L_0x018d:
            double r1 = (double) r6
            double r1 = r1 * r31
            int r3 = r7 + -1
            double r3 = (double) r3
            double r22 = r1 / r3
            int r1 = r13 / 2
            double r4 = (double) r1
            double r1 = r11.DF
            int r3 = (r22 > r1 ? 1 : (r22 == r1 ? 0 : -1))
            if (r3 >= 0) goto L_0x0ae5
            double r34 = r11.alpha(r8)
            double r36 = im.bclpbkiauv.ui.utils.translate.ssrc.I0Bessel.value(r34)
            r1 = 1
        L_0x01a8:
            if (r1 >= r7) goto L_0x01ad
            int r1 = r1 * 2
            goto L_0x01a8
        L_0x01ad:
            r2 = 2
            int r3 = r1 * 2
            double[] r1 = new double[r3]
            int r2 = r7 / 2
            int r0 = -r2
            r2 = r0
        L_0x01b6:
            int r0 = r7 / 2
            r38 = 4611686018427387904(0x4000000000000000, double:2.0)
            if (r2 > r0) goto L_0x01f8
            int r0 = r7 / 2
            int r40 = r2 + r0
            r41 = r4
            r5 = r3
            double r3 = (double) r2
            r0 = r76
            r45 = r1
            r44 = r2
            r1 = r3
            r4 = r5
            r3 = r7
            r15 = r4
            r4 = r34
            r59 = r8
            r8 = r6
            r9 = r7
            r6 = r36
            double r6 = r0.win(r1, r3, r4, r6)
            double r4 = (double) r8
            r1 = r44
            r2 = r41
            double r0 = r0.hn_lpf(r1, r2, r4)
            double r6 = r6 * r0
            double r0 = (double) r15
            double r6 = r6 / r0
            double r6 = r6 * r38
            r45[r40] = r6
            int r2 = r44 + 1
            r6 = r8
            r7 = r9
            r3 = r15
            r4 = r41
            r1 = r45
            r8 = r59
            r15 = 0
            goto L_0x01b6
        L_0x01f8:
            r45 = r1
            r44 = r2
            r15 = r3
            r41 = r4
            r59 = r8
            r8 = r6
            r9 = r7
            double r0 = (double) r15
            double r0 = java.lang.Math.sqrt(r0)
            double r0 = r0 + r38
            int r0 = (int) r0
            int[] r6 = new int[r0]
            r1 = 0
            r6[r1] = r1
            int r3 = r15 / 2
            double[] r7 = new double[r3]
            im.bclpbkiauv.ui.utils.translate.ssrc.SplitRadixFft r1 = r11.fft
            r51 = 1
            r49 = r1
            r50 = r15
            r52 = r45
            r53 = r6
            r54 = r7
            r49.rdft(r50, r51, r52, r53, r54)
            r76.setstarttime()
            int r5 = r15 / 2
            r0 = 0
            r1 = 0
            int r2 = r5 / r30
            r3 = 1
            int r2 = r2 + r3
            r16 = r0
            r4 = 2
            int[] r0 = new int[r4]
            r0[r3] = r2
            r2 = r79
            r17 = 0
            r0[r17] = r2
            r3 = r29
            java.lang.Object r0 = java.lang.reflect.Array.newInstance(r3, r0)
            r29 = r0
            double[][] r29 = (double[][]) r29
            int[] r0 = new int[r4]
            r4 = 1
            r0[r4] = r15
            r0[r17] = r2
            java.lang.Object r0 = java.lang.reflect.Array.newInstance(r3, r0)
            r34 = r0
            double[][] r34 = (double[][]) r34
            r3 = r57
            int r0 = r5 + r3
            int r0 = r0 * r2
            r4 = r80
            r35 = r6
            r36 = r7
            r6 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r0 = r0 * r4
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocate(r0)
            int r17 = r5 / r30
            r22 = 1
            int r17 = r17 + 1
            int r17 = r17 * r2
            r6 = r81
            int r17 = r17 * r6
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r17)
            int r17 = r5 + r3
            r23 = r0
            int r0 = r2 * r17
            double[] r0 = new double[r0]
            int r17 = r5 / r30
            int r17 = r17 + 1
            r22 = r1
            int r1 = r2 * r17
            double[] r1 = new double[r1]
            r17 = 0
            r31 = 0
            r32 = 0
            r37 = 0
            r40 = 1
            r41 = 0
            int r42 = r43 / 2
            int r47 = r10 / r13
            int r42 = r42 / r47
            r26 = 1
            int r42 = r42 + 1
            r47 = r7
            double r6 = (double) r9
            double r6 = r6 / r38
            r57 = r9
            int r9 = r8 / r14
            r61 = r8
            double r8 = (double) r9
            double r6 = r6 / r8
            int r6 = (int) r6
            r7 = 0
            r8 = r7
            r9 = r7
            r62 = r12
            r22 = r21
            r7 = r23
            r12 = r42
            r21 = r17
            r17 = r9
            r9 = r8
            r8 = r6
            r6 = r86
        L_0x02c3:
            r23 = r8
            r86 = r9
            double r8 = (double) r5
            r42 = r0
            r59 = r1
            double r0 = (double) r13
            double r8 = r8 * r0
            int r0 = r14 * r30
            double r0 = (double) r0
            double r8 = r8 / r0
            double r0 = java.lang.Math.floor(r8)
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r0 = r0 + r8
            double r8 = (double) r3
            double r0 = r0 + r8
            double r8 = (double) r12
            double r0 = r0 - r8
            int r0 = (int) r0
            r1 = r0
            r8 = r0
            int r0 = r1 + r17
            if (r0 <= r6) goto L_0x02e9
            int r1 = r6 - r17
            r9 = r1
            goto L_0x02ea
        L_0x02e9:
            r9 = r1
        L_0x02ea:
            r0 = 0
            r7.position(r0)
            int r0 = r7.limit()
            int r1 = r4 * r2
            int r1 = r1 * r9
            int r0 = java.lang.Math.min(r0, r1)
            r7.limit(r0)
            int r0 = r7.limit()
            byte[] r1 = new byte[r0]
            r0 = r77
            int r49 = r0.read(r1)
            if (r49 >= 0) goto L_0x0310
            r49 = 0
            r0 = r49
            goto L_0x0312
        L_0x0310:
            r0 = r49
        L_0x0312:
            r49 = r6
            int r6 = r7.limit()
            if (r0 >= r6) goto L_0x0321
            int r6 = r0 / r4
            int r6 = r6 * r2
            int r6 = r17 + r6
            goto L_0x0323
        L_0x0321:
            r6 = r49
        L_0x0323:
            r7.limit(r0)
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.wrap(r1)
            r7.position(r0)
            r7.flip()
            int r49 = r4 * r2
            int r60 = r0 / r49
            r63 = 8
            r0 = 1
            if (r4 == r0) goto L_0x03eb
            r0 = 2
            if (r4 == r0) goto L_0x03be
            r0 = 3
            if (r4 == r0) goto L_0x0381
            r0 = 4
            if (r4 == r0) goto L_0x034b
            r64 = r1
            r65 = r15
            r0 = r44
            r15 = r3
            goto L_0x040d
        L_0x034b:
            r44 = 0
            r0 = r44
        L_0x034f:
            r64 = r1
            int r1 = r60 * r2
            if (r0 >= r1) goto L_0x037c
            java.nio.ByteOrder r1 = r11.byteOrder
            java.nio.ByteBuffer r1 = r7.order(r1)
            java.nio.IntBuffer r1 = r1.asIntBuffer()
            int r1 = r1.get(r0)
            int r44 = r2 * r12
            int r44 = r44 + r0
            r49 = 4467570830353629184(0x3e00000000200000, double:4.656612875245797E-10)
            r65 = r15
            double r14 = (double) r1
            double r14 = r14 * r49
            r42[r44] = r14
            int r0 = r0 + 1
            r14 = r83
            r1 = r64
            r15 = r65
            goto L_0x034f
        L_0x037c:
            r65 = r15
            r15 = r3
            goto L_0x040d
        L_0x0381:
            r64 = r1
            r65 = r15
            r0 = 0
        L_0x0386:
            int r1 = r60 * r2
            if (r0 >= r1) goto L_0x03bc
            int r1 = r2 * r12
            int r1 = r1 + r0
            int r14 = r0 * 3
            byte r14 = r7.get(r14)
            r15 = 0
            int r14 = r14 << r15
            int r15 = r0 * 3
            r26 = 1
            int r15 = r15 + 1
            byte r15 = r7.get(r15)
            int r15 = r15 << 8
            r14 = r14 | r15
            int r15 = r0 * 3
            r33 = 2
            int r15 = r15 + 2
            byte r15 = r7.get(r15)
            int r15 = r15 << 16
            r14 = r14 | r15
            double r14 = (double) r14
            r49 = 4503599627907366976(0x3e80000020000040, double:1.1920930376163766E-7)
            double r14 = r14 * r49
            r42[r1] = r14
            int r0 = r0 + 1
            goto L_0x0386
        L_0x03bc:
            r15 = r3
            goto L_0x040d
        L_0x03be:
            r64 = r1
            r65 = r15
            r0 = 0
        L_0x03c3:
            int r1 = r60 * r2
            if (r0 >= r1) goto L_0x03e9
            java.nio.ByteOrder r1 = r11.byteOrder
            java.nio.ByteBuffer r1 = r7.order(r1)
            java.nio.ShortBuffer r1 = r1.asShortBuffer()
            short r1 = r1.get(r0)
            int r14 = r2 * r12
            int r14 = r14 + r0
            r49 = 4539628561832607872(0x3f00002000400080, double:3.051850947599719E-5)
            r15 = r3
            double r3 = (double) r1
            double r3 = r3 * r49
            r42[r14] = r3
            int r0 = r0 + 1
            r4 = r80
            r3 = r15
            goto L_0x03c3
        L_0x03e9:
            r15 = r3
            goto L_0x040d
        L_0x03eb:
            r64 = r1
            r65 = r15
            r15 = r3
            r0 = 0
        L_0x03f1:
            int r1 = r60 * r2
            if (r0 >= r1) goto L_0x040d
            int r1 = r2 * r12
            int r1 = r1 + r0
            byte r14 = r7.get(r0)
            double r3 = (double) r14
            r51 = 4638707616191610880(0x4060000000000000, double:128.0)
            double r3 = r3 - r51
            r49 = 4575692682822812680(0x3f80204081020408, double:0.007874015748031496)
            double r3 = r3 * r49
            r42[r1] = r3
            int r0 = r0 + 1
            goto L_0x03f1
        L_0x040d:
            int r1 = r2 * r8
            if (r0 >= r1) goto L_0x0419
            int r1 = r2 * r12
            int r1 = r1 + r0
            r42[r1] = r18
            int r0 = r0 + 1
            goto L_0x040d
        L_0x0419:
            int r12 = r12 + r8
            int r14 = r17 + r60
            if (r14 < r6) goto L_0x0420
            r1 = 1
            goto L_0x0421
        L_0x0420:
            r1 = 0
        L_0x0421:
            r41 = r1
            r4 = r5
            int r1 = r31 + -1
            int r1 = r1 * r13
            int r1 = r1 + r10
            int r1 = r1 / r10
            int r1 = r1 * r2
            r17 = r21
            r44 = r1
            r66 = r37
            r3 = 0
            r75 = r16
            r16 = r1
            r1 = r75
        L_0x0439:
            r67 = r7
            if (r3 >= r2) goto L_0x06b8
            r68 = r3
            int r7 = r48 * r30
            r21 = r17
            int r16 = r44 + r3
            r50 = r0
            r0 = 7
            if (r15 == r0) goto L_0x0543
            r0 = 9
            if (r15 == r0) goto L_0x0491
            r0 = 0
        L_0x044f:
            if (r0 >= r4) goto L_0x0487
            r51 = 0
            r49 = r16
            r53 = r56[r21]
            r50 = 0
            r69 = r8
            r8 = r50
        L_0x045d:
            if (r8 >= r15) goto L_0x046e
            r50 = r27[r53]
            r70 = r50[r8]
            r72 = r42[r49]
            double r70 = r70 * r72
            double r51 = r51 + r70
            int r49 = r49 + r2
            int r8 = r8 + 1
            goto L_0x045d
        L_0x046e:
            r50 = r34[r3]
            r50[r0] = r51
            r50 = r55[r21]
            int r16 = r16 + r50
            r50 = r8
            int r8 = r21 + 1
            if (r8 != r7) goto L_0x0480
            r8 = 0
            r21 = r8
            goto L_0x0482
        L_0x0480:
            r21 = r8
        L_0x0482:
            int r0 = r0 + 1
            r8 = r69
            goto L_0x044f
        L_0x0487:
            r69 = r8
            r70 = r9
            r8 = r50
            r58 = 4
            goto L_0x05d2
        L_0x0491:
            r69 = r8
            r0 = 0
        L_0x0494:
            if (r0 >= r4) goto L_0x053b
            r8 = r56[r21]
            r51 = r34[r3]
            r52 = r27[r8]
            r46 = 0
            r53 = r52[r46]
            int r52 = r2 * 0
            int r52 = r16 + r52
            r70 = r42[r52]
            double r53 = r53 * r70
            r52 = r27[r8]
            r26 = 1
            r70 = r52[r26]
            int r52 = r2 * 1
            int r52 = r16 + r52
            r72 = r42[r52]
            double r70 = r70 * r72
            double r53 = r53 + r70
            r52 = r27[r8]
            r33 = 2
            r70 = r52[r33]
            int r52 = r2 * 2
            int r52 = r16 + r52
            r72 = r42[r52]
            double r70 = r70 * r72
            double r53 = r53 + r70
            r52 = r27[r8]
            r28 = 3
            r70 = r52[r28]
            int r52 = r2 * 3
            int r52 = r16 + r52
            r72 = r42[r52]
            double r70 = r70 * r72
            double r53 = r53 + r70
            r52 = r27[r8]
            r58 = 4
            r70 = r52[r58]
            int r52 = r2 * 4
            int r52 = r16 + r52
            r72 = r42[r52]
            double r70 = r70 * r72
            double r53 = r53 + r70
            r52 = r27[r8]
            r70 = r52[r25]
            int r52 = r2 * 5
            int r52 = r16 + r52
            r72 = r42[r52]
            double r70 = r70 * r72
            double r53 = r53 + r70
            r52 = r27[r8]
            r70 = r9
            r9 = 6
            r71 = r52[r9]
            int r52 = r2 * 6
            int r52 = r16 + r52
            r73 = r42[r52]
            double r71 = r71 * r73
            double r53 = r53 + r71
            r52 = r27[r8]
            r49 = 7
            r71 = r52[r49]
            int r52 = r2 * 7
            int r52 = r16 + r52
            r73 = r42[r52]
            double r71 = r71 * r73
            double r53 = r53 + r71
            r52 = r27[r8]
            r71 = r52[r63]
            int r52 = r2 * 8
            int r52 = r16 + r52
            r73 = r42[r52]
            double r71 = r71 * r73
            double r53 = r53 + r71
            r51[r0] = r53
            r51 = r55[r21]
            int r16 = r16 + r51
            int r9 = r21 + 1
            if (r9 != r7) goto L_0x0533
            r9 = 0
            r21 = r9
            goto L_0x0535
        L_0x0533:
            r21 = r9
        L_0x0535:
            int r0 = r0 + 1
            r9 = r70
            goto L_0x0494
        L_0x053b:
            r70 = r9
            r8 = r50
            r58 = 4
            goto L_0x05d2
        L_0x0543:
            r69 = r8
            r70 = r9
            r0 = 0
        L_0x0548:
            if (r0 >= r4) goto L_0x05ce
            r8 = r56[r21]
            r9 = r34[r3]
            r49 = r27[r8]
            r46 = 0
            r51 = r49[r46]
            int r49 = r2 * 0
            int r49 = r16 + r49
            r53 = r42[r49]
            double r51 = r51 * r53
            r49 = r27[r8]
            r26 = 1
            r53 = r49[r26]
            int r49 = r2 * 1
            int r49 = r16 + r49
            r71 = r42[r49]
            double r53 = r53 * r71
            double r51 = r51 + r53
            r49 = r27[r8]
            r33 = 2
            r53 = r49[r33]
            int r49 = r2 * 2
            int r49 = r16 + r49
            r71 = r42[r49]
            double r53 = r53 * r71
            double r51 = r51 + r53
            r49 = r27[r8]
            r28 = 3
            r53 = r49[r28]
            int r49 = r2 * 3
            int r49 = r16 + r49
            r71 = r42[r49]
            double r53 = r53 * r71
            double r51 = r51 + r53
            r49 = r27[r8]
            r58 = 4
            r53 = r49[r58]
            int r49 = r2 * 4
            int r49 = r16 + r49
            r71 = r42[r49]
            double r53 = r53 * r71
            double r51 = r51 + r53
            r49 = r27[r8]
            r53 = r49[r25]
            int r49 = r2 * 5
            int r49 = r16 + r49
            r71 = r42[r49]
            double r53 = r53 * r71
            double r51 = r51 + r53
            r49 = r27[r8]
            r53 = 6
            r71 = r49[r53]
            int r49 = r2 * 6
            int r49 = r16 + r49
            r53 = r42[r49]
            double r71 = r71 * r53
            double r51 = r51 + r71
            r9[r0] = r51
            r9 = r55[r21]
            int r16 = r16 + r9
            int r9 = r21 + 1
            if (r9 != r7) goto L_0x05c8
            r9 = 0
            r21 = r9
            goto L_0x05ca
        L_0x05c8:
            r21 = r9
        L_0x05ca:
            int r0 = r0 + 1
            goto L_0x0548
        L_0x05ce:
            r58 = 4
            r8 = r50
        L_0x05d2:
            r9 = r66
            r0 = r4
        L_0x05d5:
            r71 = r7
            r7 = r65
            if (r0 >= r7) goto L_0x05e6
            r37 = r34[r3]
            r37[r0] = r18
            int r0 = r0 + 1
            r65 = r7
            r7 = r71
            goto L_0x05d5
        L_0x05e6:
            r37 = r0
            im.bclpbkiauv.ui.utils.translate.ssrc.SplitRadixFft r0 = r11.fft
            r51 = 1
            r52 = r34[r3]
            r49 = r0
            r50 = r7
            r53 = r35
            r54 = r36
            r49.rdft(r50, r51, r52, r53, r54)
            r0 = r34[r3]
            r46 = 0
            r49 = r45[r46]
            r51 = r34[r3]
            r52 = r51[r46]
            double r49 = r49 * r52
            r0[r46] = r49
            r0 = r34[r3]
            r26 = 1
            r49 = r45[r26]
            r51 = r34[r3]
            r52 = r51[r26]
            double r49 = r49 * r52
            r0[r26] = r49
            r0 = 1
        L_0x0616:
            int r8 = r7 / 2
            if (r0 >= r8) goto L_0x066d
            int r8 = r0 * 2
            r49 = r45[r8]
            r8 = r34[r3]
            int r51 = r0 * 2
            r51 = r8[r51]
            double r49 = r49 * r51
            int r8 = r0 * 2
            r26 = 1
            int r8 = r8 + 1
            r51 = r45[r8]
            r8 = r34[r3]
            int r53 = r0 * 2
            int r53 = r53 + 1
            r53 = r8[r53]
            double r51 = r51 * r53
            double r49 = r49 - r51
            int r8 = r0 * 2
            int r8 = r8 + 1
            r51 = r45[r8]
            r8 = r34[r3]
            int r53 = r0 * 2
            r53 = r8[r53]
            double r51 = r51 * r53
            int r8 = r0 * 2
            r53 = r45[r8]
            r8 = r34[r3]
            int r65 = r0 * 2
            r26 = 1
            int r65 = r65 + 1
            r72 = r8[r65]
            double r53 = r53 * r72
            double r51 = r51 + r53
            r8 = r34[r3]
            int r53 = r0 * 2
            r8[r53] = r49
            r8 = r34[r3]
            int r53 = r0 * 2
            r26 = 1
            int r53 = r53 + 1
            r8[r53] = r51
            int r0 = r0 + 1
            goto L_0x0616
        L_0x066d:
            im.bclpbkiauv.ui.utils.translate.ssrc.SplitRadixFft r8 = r11.fft
            r51 = -1
            r52 = r34[r3]
            r49 = r8
            r50 = r7
            r53 = r35
            r54 = r36
            r49.rdft(r50, r51, r52, r53, r54)
            r0 = r9
            r8 = 0
        L_0x0680:
            if (r0 >= r5) goto L_0x0697
            r49 = r29[r3]
            r50 = r49[r8]
            r49 = r34[r3]
            r52 = r49[r0]
            double r50 = r50 + r52
            int r49 = r8 * r2
            int r49 = r68 + r49
            r59[r49] = r50
            int r0 = r0 + r30
            int r8 = r8 + 1
            goto L_0x0680
        L_0x0697:
            r1 = r8
            int r9 = r0 - r5
            r8 = 0
        L_0x069b:
            if (r0 >= r7) goto L_0x06aa
            r49 = r29[r3]
            r50 = r34[r3]
            r51 = r50[r0]
            r49[r8] = r51
            int r0 = r0 + r30
            int r8 = r8 + 1
            goto L_0x069b
        L_0x06aa:
            int r3 = r3 + 1
            r65 = r7
            r37 = r9
            r7 = r67
            r8 = r69
            r9 = r70
            goto L_0x0439
        L_0x06b8:
            r50 = r0
            r69 = r8
            r70 = r9
            r7 = r65
            r58 = 4
            int r0 = r13 / r24
            int r0 = r0 * r4
            int r0 = r0 / r30
            int r31 = r31 + r0
            r47.clear()
            if (r87 == 0) goto L_0x0727
            r0 = 0
        L_0x06d0:
            int r8 = r1 * r2
            if (r0 >= r8) goto L_0x0707
            r8 = r59[r0]
            int r50 = (r8 > r18 ? 1 : (r8 == r18 ? 0 : -1))
            if (r50 <= 0) goto L_0x06dd
            r8 = r59[r0]
            goto L_0x06e0
        L_0x06dd:
            r8 = r59[r0]
            double r8 = -r8
        L_0x06e0:
            r46 = 0
            r50 = r20[r46]
            int r52 = (r50 > r8 ? 1 : (r50 == r8 ? 0 : -1))
            if (r52 >= 0) goto L_0x06eb
            r50 = r8
            goto L_0x06ed
        L_0x06eb:
            r50 = r20[r46]
        L_0x06ed:
            r20[r46] = r50
            r51 = r3
            java.nio.DoubleBuffer r3 = r47.asDoubleBuffer()
            r53 = r4
            r52 = r5
            r4 = r59[r0]
            r3.put(r0, r4)
            int r0 = r0 + 1
            r3 = r51
            r5 = r52
            r4 = r53
            goto L_0x06d0
        L_0x0707:
            r51 = r3
            r53 = r4
            r52 = r5
            r8 = r81
            r50 = r0
            r65 = r7
            r63 = r42
            r58 = r59
            r59 = r64
            r54 = 4
            r7 = r2
            r64 = r15
            r15 = r1
            r75 = r47
            r47 = r6
            r6 = r75
            goto L_0x094a
        L_0x0727:
            r51 = r3
            r53 = r4
            r52 = r5
            r8 = r81
            r0 = 1
            if (r8 == r0) goto L_0x08c7
            r0 = 2
            if (r8 == r0) goto L_0x0827
            r9 = 3
            if (r8 == r9) goto L_0x0750
            r65 = r7
            r63 = r42
            r3 = r51
            r58 = r59
            r59 = r64
            r54 = 4
            r7 = r2
            r64 = r15
            r15 = r1
            r75 = r47
            r47 = r6
            r6 = r75
            goto L_0x094a
        L_0x0750:
            r71 = 4710765209155796992(0x415fffffc0000000, double:8388607.0)
            double r73 = r84 * r71
            r0 = 0
            r3 = 0
            r28 = r0
            r5 = r3
        L_0x075c:
            int r0 = r1 * r2
            if (r5 >= r0) goto L_0x080c
            if (r88 == 0) goto L_0x0784
            r3 = r59[r5]
            double r3 = r3 * r73
            r9 = r42
            r54 = 4
            r0 = r76
            r65 = r7
            r58 = r59
            r59 = r64
            r7 = r2
            r64 = r15
            r15 = r1
            r1 = r3
            r3 = r20
            r4 = r88
            r50 = r5
            r5 = r28
            int r0 = r0.do_shaping(r1, r3, r4, r5)
            goto L_0x07cb
        L_0x0784:
            r50 = r5
            r65 = r7
            r9 = r42
            r58 = r59
            r59 = r64
            r54 = 4
            r7 = r2
            r64 = r15
            r15 = r1
            r0 = r58[r50]
            double r0 = r0 * r73
            int r0 = r11.RINT(r0)
            r1 = -8388608(0xffffffffff800000, float:-Infinity)
            if (r0 >= r1) goto L_0x07b3
            double r1 = (double) r0
            r3 = -4512606826625236992(0xc160000000000000, double:-8388608.0)
            double r1 = r1 / r3
            r3 = 0
            r4 = r20[r3]
            int r46 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r46 >= 0) goto L_0x07ad
            r4 = r1
            goto L_0x07af
        L_0x07ad:
            r4 = r20[r3]
        L_0x07af:
            r20[r3] = r4
            r0 = -8388608(0xffffffffff800000, float:-Infinity)
        L_0x07b3:
            r1 = 8388607(0x7fffff, float:1.1754942E-38)
            if (r1 >= r0) goto L_0x07cb
            double r1 = (double) r0
            double r1 = r1 / r71
            r3 = 0
            r4 = r20[r3]
            int r46 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r46 >= 0) goto L_0x07c4
            r4 = r1
            goto L_0x07c6
        L_0x07c4:
            r4 = r20[r3]
        L_0x07c6:
            r20[r3] = r4
            r0 = 8388607(0x7fffff, float:1.1754942E-38)
        L_0x07cb:
            int r5 = r50 * 3
            r1 = r0 & 255(0xff, float:3.57E-43)
            byte r1 = (byte) r1
            r4 = r47
            r4.put(r5, r1)
            int r0 = r0 >> 8
            int r5 = r50 * 3
            r1 = 1
            int r5 = r5 + r1
            r1 = r0 & 255(0xff, float:3.57E-43)
            byte r1 = (byte) r1
            r4.put(r5, r1)
            int r0 = r0 >> 8
            int r5 = r50 * 3
            r1 = 2
            int r5 = r5 + r1
            r1 = r0 & 255(0xff, float:3.57E-43)
            byte r1 = (byte) r1
            r4.put(r5, r1)
            int r1 = r28 + 1
            if (r1 != r7) goto L_0x07f5
            r1 = 0
            r28 = r1
            goto L_0x07f7
        L_0x07f5:
            r28 = r1
        L_0x07f7:
            int r5 = r50 + 1
            r47 = r4
            r2 = r7
            r42 = r9
            r1 = r15
            r15 = r64
            r7 = r65
            r9 = 3
            r64 = r59
            r59 = r58
            r58 = 4
            goto L_0x075c
        L_0x080c:
            r50 = r5
            r65 = r7
            r9 = r42
            r4 = r47
            r58 = r59
            r59 = r64
            r54 = 4
            r7 = r2
            r64 = r15
            r15 = r1
            r47 = r6
            r63 = r9
            r3 = r28
            r6 = r4
            goto L_0x094a
        L_0x0827:
            r65 = r7
            r9 = r42
            r4 = r47
            r58 = r59
            r59 = r64
            r54 = 4
            r7 = r2
            r64 = r15
            r15 = r1
            r71 = 4674736138332667904(0x40dfffc000000000, double:32767.0)
            double r73 = r84 * r71
            r0 = 0
            r1 = 0
            r28 = r0
            r5 = r1
        L_0x0843:
            int r1 = r15 * r7
            if (r5 >= r1) goto L_0x08bb
            if (r88 == 0) goto L_0x0860
            r0 = r58[r5]
            double r1 = r0 * r73
            r0 = r76
            r3 = r20
            r47 = r6
            r6 = r4
            r4 = r88
            r63 = r9
            r9 = r5
            r5 = r28
            int r0 = r0.do_shaping(r1, r3, r4, r5)
            goto L_0x089b
        L_0x0860:
            r47 = r6
            r63 = r9
            r6 = r4
            r9 = r5
            r0 = r58[r9]
            double r0 = r0 * r73
            int r0 = r11.RINT(r0)
            r1 = -32768(0xffffffffffff8000, float:NaN)
            if (r0 >= r1) goto L_0x0885
            double r1 = (double) r0
            r3 = -4548635623644200960(0xc0e0000000000000, double:-32768.0)
            double r1 = r1 / r3
            r3 = 0
            r4 = r20[r3]
            int r46 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r46 >= 0) goto L_0x087f
            r4 = r1
            goto L_0x0881
        L_0x087f:
            r4 = r20[r3]
        L_0x0881:
            r20[r3] = r4
            r0 = -32768(0xffffffffffff8000, float:NaN)
        L_0x0885:
            r1 = 32767(0x7fff, float:4.5916E-41)
            if (r1 >= r0) goto L_0x089b
            double r1 = (double) r0
            double r1 = r1 / r71
            r3 = 0
            r4 = r20[r3]
            int r46 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r46 >= 0) goto L_0x0895
            r4 = r1
            goto L_0x0897
        L_0x0895:
            r4 = r20[r3]
        L_0x0897:
            r20[r3] = r4
            r0 = 32767(0x7fff, float:4.5916E-41)
        L_0x089b:
            java.nio.ByteOrder r1 = r11.byteOrder
            java.nio.ByteBuffer r1 = r6.order(r1)
            java.nio.ShortBuffer r1 = r1.asShortBuffer()
            short r2 = (short) r0
            r1.put(r9, r2)
            int r1 = r28 + 1
            if (r1 != r7) goto L_0x08b1
            r1 = 0
            r28 = r1
            goto L_0x08b3
        L_0x08b1:
            r28 = r1
        L_0x08b3:
            int r5 = r9 + 1
            r4 = r6
            r6 = r47
            r9 = r63
            goto L_0x0843
        L_0x08bb:
            r47 = r6
            r63 = r9
            r6 = r4
            r9 = r5
            r50 = r9
            r3 = r28
            goto L_0x094a
        L_0x08c7:
            r65 = r7
            r63 = r42
            r58 = r59
            r59 = r64
            r54 = 4
            r7 = r2
            r64 = r15
            r15 = r1
            r75 = r47
            r47 = r6
            r6 = r75
            r71 = 4638637247447433216(0x405fc00000000000, double:127.0)
            double r73 = r84 * r71
            r0 = 0
            r1 = 0
            r28 = r0
            r9 = r1
        L_0x08e7:
            int r1 = r15 * r7
            if (r9 >= r1) goto L_0x0946
            if (r88 == 0) goto L_0x08fe
            r0 = r58[r9]
            double r1 = r0 * r73
            r0 = r76
            r3 = r20
            r4 = r88
            r5 = r28
            int r0 = r0.do_shaping(r1, r3, r4, r5)
            goto L_0x0933
        L_0x08fe:
            r0 = r58[r9]
            double r0 = r0 * r73
            int r0 = r11.RINT(r0)
            r1 = -128(0xffffffffffffff80, float:NaN)
            if (r0 >= r1) goto L_0x091d
            double r1 = (double) r0
            r3 = -4584664420663164928(0xc060000000000000, double:-128.0)
            double r1 = r1 / r3
            r3 = 0
            r4 = r20[r3]
            int r46 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r46 >= 0) goto L_0x0917
            r4 = r1
            goto L_0x0919
        L_0x0917:
            r4 = r20[r3]
        L_0x0919:
            r20[r3] = r4
            r0 = -128(0xffffffffffffff80, float:NaN)
        L_0x091d:
            r1 = 127(0x7f, float:1.78E-43)
            if (r1 >= r0) goto L_0x0933
            double r1 = (double) r0
            double r1 = r1 / r71
            r3 = 0
            r4 = r20[r3]
            int r46 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r46 >= 0) goto L_0x092d
            r4 = r1
            goto L_0x092f
        L_0x092d:
            r4 = r20[r3]
        L_0x092f:
            r20[r3] = r4
            r0 = 127(0x7f, float:1.78E-43)
        L_0x0933:
            int r1 = r0 + 128
            byte r1 = (byte) r1
            r6.put(r9, r1)
            int r1 = r28 + 1
            if (r1 != r7) goto L_0x0941
            r1 = 0
            r28 = r1
            goto L_0x0943
        L_0x0941:
            r28 = r1
        L_0x0943:
            int r9 = r9 + 1
            goto L_0x08e7
        L_0x0946:
            r50 = r9
            r3 = r28
        L_0x094a:
            if (r40 != 0) goto L_0x09e3
            if (r41 == 0) goto L_0x09b7
            double r0 = (double) r14
            r2 = r83
            double r4 = (double) r2
            double r0 = r0 * r4
            double r4 = (double) r13
            double r0 = r0 / r4
            double r0 = r0 + r38
            int r9 = r86 + r15
            double r4 = (double) r9
            int r9 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r9 <= 0) goto L_0x0982
            r0 = 0
            r6.position(r0)
            int r1 = r8 * r7
            int r1 = r1 * r15
            r6.limit(r1)
            r1 = r78
            r4 = 3
            r11.writeBuffers(r1, r6)
            int r9 = r86 + r15
            r8 = r3
            r28 = r10
            r10 = r11
            r86 = r12
            r4 = r13
            r13 = r14
            r7 = r15
            r11 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r14 = r9
            r9 = r23
            goto L_0x0a8c
        L_0x0982:
            r1 = r78
            r0 = 0
            r6.position(r0)
            int r0 = r8 * r7
            double r4 = (double) r0
            r28 = r10
            double r9 = (double) r14
            r0 = r14
            r42 = r15
            double r14 = (double) r2
            double r9 = r9 * r14
            double r14 = (double) r13
            double r9 = r9 / r14
            double r9 = java.lang.Math.floor(r9)
            double r9 = r9 + r38
            r14 = r86
            r86 = r12
            double r12 = (double) r14
            double r9 = r9 - r12
            double r4 = r4 * r9
            int r4 = (int) r4
            if (r4 <= 0) goto L_0x09b2
            r6.limit(r4)
            r11.writeBuffers(r1, r6)
            r4 = r82
            r10 = r11
            goto L_0x0a63
        L_0x09b2:
            r4 = r82
            r10 = r11
            goto L_0x0a63
        L_0x09b7:
            r1 = r78
            r2 = r83
            r28 = r10
            r0 = r14
            r42 = r15
            r4 = 3
            r14 = r86
            r86 = r12
            r5 = 0
            r6.position(r5)
            int r5 = r8 * r7
            int r5 = r5 * r42
            r6.limit(r5)
            r11.writeBuffers(r1, r6)
            int r9 = r14 + r42
            r4 = r82
            r13 = r0
            r8 = r3
            r14 = r9
            r10 = r11
            r9 = r23
            r7 = r42
            r11 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0a8c
        L_0x09e3:
            r1 = r78
            r2 = r83
            r28 = r10
            r0 = r14
            r42 = r15
            r4 = 3
            r14 = r86
            r86 = r12
            r9 = r23
            r5 = r42
            if (r5 >= r9) goto L_0x0a02
            int r9 = r9 - r5
            r4 = r82
            r13 = r0
            r8 = r3
            r7 = r5
            r10 = r11
            r11 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0a8c
        L_0x0a02:
            if (r41 == 0) goto L_0x0a6c
            double r12 = (double) r0
            r15 = r5
            double r4 = (double) r2
            double r12 = r12 * r4
            r4 = r82
            double r10 = (double) r4
            double r12 = r12 / r10
            double r12 = r12 + r38
            int r10 = r14 + r15
            int r10 = r10 - r9
            double r10 = (double) r10
            int r23 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r23 <= 0) goto L_0x0a36
            int r10 = r8 * r7
            int r10 = r10 * r9
            r6.position(r10)
            int r10 = r8 * r7
            int r11 = r15 - r9
            int r10 = r10 * r11
            r6.limit(r10)
            r10 = r76
            r10.writeBuffers(r1, r6)
            int r11 = r15 - r9
            int r11 = r11 + r14
            r13 = r0
            r8 = r3
            r14 = r11
            r7 = r15
            r11 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0a8c
        L_0x0a36:
            r10 = r76
            int r5 = r8 * r7
            int r5 = r5 * r9
            r6.position(r5)
            int r5 = r8 * r7
            double r11 = (double) r5
            double r7 = (double) r0
            r13 = r0
            double r0 = (double) r2
            double r7 = r7 * r0
            double r0 = (double) r4
            double r7 = r7 / r0
            double r0 = java.lang.Math.floor(r7)
            double r0 = r0 + r38
            double r7 = (double) r14
            double r0 = r0 + r7
            r8 = r3
            r7 = r15
            double r2 = (double) r7
            double r0 = r0 + r2
            double r2 = (double) r9
            double r0 = r0 - r2
            double r11 = r11 * r0
            int r0 = (int) r11
            r6.limit(r0)
            r1 = r78
            r10.writeBuffers(r1, r6)
        L_0x0a63:
            r11 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r10.showprogress(r11)
            r0 = 0
            r2 = r20[r0]
            return r2
        L_0x0a6c:
            r4 = r82
            r13 = r0
            r8 = r3
            r7 = r5
            r10 = r11
            r11 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r0 = r81 * r79
            int r0 = r0 * r9
            r6.position(r0)
            int r0 = r81 * r79
            int r0 = r0 * r7
            r6.limit(r0)
            r10.writeBuffers(r1, r6)
            int r0 = r7 - r9
            int r0 = r0 + r14
            r2 = 0
            r14 = r0
            r40 = r2
        L_0x0a8c:
            int r0 = r31 + -1
            int r2 = r28 / r4
            int r32 = r0 / r2
            int r0 = r79 * r32
            int r2 = r86 - r32
            int r2 = r2 * r79
            r15 = r63
            r3 = 0
            java.lang.System.arraycopy(r15, r0, r15, r3, r2)
            int r0 = r86 - r32
            int r2 = r28 / r4
            int r2 = r2 * r32
            int r31 = r31 - r2
            int r2 = r22 + 1
            r3 = r22 & 7
            r5 = 7
            if (r3 != r5) goto L_0x0aba
            r3 = r13
            double r11 = (double) r3
            r86 = r0
            r5 = r47
            double r0 = (double) r5
            double r11 = r11 / r0
            r10.showprogress(r11)
            goto L_0x0abf
        L_0x0aba:
            r86 = r0
            r3 = r13
            r5 = r47
        L_0x0abf:
            r12 = r86
            r22 = r2
            r17 = r3
            r13 = r4
            r47 = r6
            r16 = r7
            r8 = r9
            r11 = r10
            r9 = r14
            r0 = r15
            r10 = r28
            r44 = r50
            r1 = r58
            r3 = r64
            r15 = r65
            r7 = r67
            r2 = r79
            r4 = r80
            r14 = r83
            r6 = r5
            r5 = r52
            goto L_0x02c3
        L_0x0ae5:
            r41 = r4
            r61 = r6
            r59 = r8
            r28 = r10
            r10 = r11
            r62 = r12
            r4 = r13
            r3 = r29
            r64 = r57
            r54 = 4
            r57 = r7
            int r0 = r0 * 2
            r14 = r83
            r10 = r28
            r57 = r64
            r15 = 0
            goto L_0x0182
        L_0x0b04:
            r50 = r1
            r62 = r4
            r28 = r5
            r4 = r13
            r54 = 4
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            r2 = 6
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r82)
            r5 = 0
            r2[r5] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r83)
            r5 = 1
            r2[r5] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r82)
            r5 = 2
            r2[r5] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r82)
            r5 = 3
            r2[r5] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r83)
            r2[r54] = r3
            int r5 = r28 / r83
            java.lang.Integer r3 = java.lang.Integer.valueOf(r5)
            r2[r25] = r3
            java.lang.String r3 = "Resampling from %dHz to %dHz is not supported.\n%d/gcd(%d,%d)=%d must be divided by 2 or 3.\n"
            java.lang.String r2 = java.lang.String.format(r3, r2)
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.utils.translate.ssrc.SSRC.upsample(java.io.InputStream, java.io.OutputStream, int, int, int, int, int, double, int, boolean, int):double");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v0, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v1, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v2, resolved type: double[][]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v0, resolved type: double[][]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v62, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v72, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v97, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v98, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v117, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v130, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v131, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v132, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v134, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v135, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v140, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v144, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r55v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v145, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v36, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v40, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v166, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v167, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v1, resolved type: double[][]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v41, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v37, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v2, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v109, resolved type: int} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0ac0  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0ac4 A[LOOP:25: B:267:0x0ac2->B:268:0x0ac4, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0ae4 A[LOOP:26: B:270:0x0ae2->B:271:0x0ae4, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0af7  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0b04  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public double downsample(java.io.InputStream r77, java.io.OutputStream r78, int r79, int r80, int r81, int r82, int r83, double r84, int r86, boolean r87, int r88) throws java.io.IOException {
        /*
            r76 = this;
            r8 = r76
            r9 = r78
            r10 = r79
            r11 = r80
            r12 = r81
            r13 = r82
            r14 = r83
            java.lang.Class<double> r15 = double.class
            r0 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r6 = 1
            double[] r1 = new double[r6]
            r19 = 0
            r7 = 0
            r1[r7] = r19
            r21 = r1
            int r4 = r8.FFTFIRLEN
            double r1 = r8.AA
            int r22 = r8.gcd(r13, r14)
            int r3 = r14 / r22
            r9 = 2
            if (r3 != r6) goto L_0x0031
            r0 = 1
            r3 = r0
            goto L_0x0043
        L_0x0031:
            int r3 = r14 / r22
            int r3 = r3 % r9
            if (r3 != 0) goto L_0x0039
            r0 = 2
            r3 = r0
            goto L_0x0043
        L_0x0039:
            int r3 = r14 / r22
            r23 = 3
            int r3 = r3 % 3
            if (r3 != 0) goto L_0x0b50
            r0 = 3
            r3 = r0
        L_0x0043:
            int r0 = r13 * r3
            r24 = 4624273579385888440(0x402cb851eb851eb8, double:14.36)
            r26 = 4620636922686786765(0x401fcccccccccccd, double:7.95)
            r28 = 4626604192193052672(0x4035000000000000, double:21.0)
            int r30 = (r1 > r28 ? 1 : (r1 == r28 ? 0 : -1))
            if (r30 > 0) goto L_0x005b
            r30 = 4606481658697998559(0x3fed82a9930be0df, double:0.9222)
            goto L_0x005f
        L_0x005b:
            double r30 = r1 - r26
            double r30 = r30 / r24
        L_0x005f:
            r32 = r4
            r33 = 1
        L_0x0063:
            int r32 = r4 * r33
            int r34 = r32 % 2
            if (r34 != 0) goto L_0x006e
            int r32 = r32 + -1
            r9 = r32
            goto L_0x0070
        L_0x006e:
            r9 = r32
        L_0x0070:
            double r5 = (double) r0
            double r5 = r5 * r30
            int r7 = r9 + -1
            r38 = r3
            r37 = r4
            double r3 = (double) r7
            double r39 = r5 / r3
            double r3 = (double) r14
            double r3 = r3 - r39
            r41 = 4611686018427387904(0x4000000000000000, double:2.0)
            double r43 = r3 / r41
            double r3 = r8.DF
            int r5 = (r39 > r3 ? 1 : (r39 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x0b2c
            double r45 = r8.alpha(r1)
            double r47 = im.bclpbkiauv.ui.utils.translate.ssrc.I0Bessel.value(r45)
            java.lang.String r3 = "须打log, 不打的话在某些机型上会卡住"
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r3)
            r3 = 1
        L_0x0098:
            if (r3 >= r9) goto L_0x009d
            int r3 = r3 * 2
            goto L_0x0098
        L_0x009d:
            r4 = 2
            int r6 = r3 * 2
            double[] r7 = new double[r6]
            int r3 = r9 / 2
            int r3 = -r3
            r4 = r3
        L_0x00a6:
            int r3 = r9 / 2
            if (r4 > r3) goto L_0x00f0
            int r3 = r9 / 2
            int r33 = r4 + r3
            r49 = r1
            double r1 = (double) r4
            r5 = r0
            r0 = r76
            r55 = r49
            r12 = r38
            r3 = r9
            r35 = r4
            r38 = r9
            r9 = r5
            r4 = r45
            r11 = r6
            r58 = r7
            r6 = r47
            double r6 = r0.win(r1, r3, r4, r6)
            double r4 = (double) r9
            r1 = r35
            r2 = r43
            double r0 = r0.hn_lpf(r1, r2, r4)
            double r6 = r6 * r0
            double r0 = (double) r9
            double r6 = r6 * r0
            double r0 = (double) r13
            double r6 = r6 / r0
            double r0 = (double) r11
            double r6 = r6 / r0
            double r6 = r6 * r41
            r58[r33] = r6
            int r4 = r35 + 1
            r0 = r9
            r6 = r11
            r9 = r38
            r1 = r55
            r7 = r58
            r11 = r80
            r38 = r12
            r12 = r81
            goto L_0x00a6
        L_0x00f0:
            r55 = r1
            r35 = r4
            r11 = r6
            r58 = r7
            r12 = r38
            r38 = r9
            r9 = r0
            double r0 = (double) r11
            double r0 = java.lang.Math.sqrt(r0)
            double r0 = r0 + r41
            int r0 = (int) r0
            int[] r6 = new int[r0]
            r1 = 0
            r6[r1] = r1
            int r1 = r11 / 2
            double[] r7 = new double[r1]
            im.bclpbkiauv.ui.utils.translate.ssrc.SplitRadixFft r2 = r8.fft
            r51 = 1
            r49 = r2
            r50 = r11
            r52 = r58
            r53 = r6
            r54 = r7
            r49.rdft(r50, r51, r52, r53, r54)
            r0 = 1
            if (r12 != r0) goto L_0x0165
            int r1 = r13 / r22
            int r1 = r1 * r14
            r2 = 1
            r3 = r0
            r16 = r0
            int[] r4 = new int[r0]
            r5 = 0
            r4[r5] = r5
            int[] r5 = new int[r0]
            int r24 = r13 / r14
            r25 = 0
            r5[r25] = r24
            r24 = r1
            r26 = r2
            r1 = 2
            int[] r2 = new int[r1]
            r1 = 1
            r2[r1] = r3
            r2[r25] = r0
            java.lang.Object r1 = java.lang.reflect.Array.newInstance(r15, r2)
            double[][] r1 = (double[][]) r1
            r2 = r1[r25]
            r16 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r2[r25] = r16
            r48 = r1
            r46 = r5
            r45 = r6
            r61 = r7
            r8 = r24
            r6 = r26
            r5 = r0
            r7 = r3
            r75 = r35
            r35 = r4
            r4 = r75
            goto L_0x0291
        L_0x0165:
            double r4 = r8.AA
            r30 = 4611686018427387904(0x4000000000000000, double:2.0)
            int r0 = r13 / r22
            int r3 = r0 * r14
            int r0 = r9 / 2
            int r1 = r13 / 2
            int r0 = r0 - r1
            r1 = 2
            int r0 = r0 * 2
            double r0 = (double) r0
            double r39 = r0 / r30
            int r0 = r13 / 2
            double r0 = (double) r0
            int r2 = r9 / 2
            int r33 = r13 / 2
            int r2 = r2 - r33
            r33 = r6
            r45 = r7
            double r6 = (double) r2
            double r6 = r6 / r30
            double r43 = r0 + r6
            int r0 = (r4 > r28 ? 1 : (r4 == r28 ? 0 : -1))
            if (r0 > 0) goto L_0x0196
            r0 = 4606481658697998559(0x3fed82a9930be0df, double:0.9222)
            r24 = r0
            goto L_0x019c
        L_0x0196:
            double r0 = r4 - r26
            double r0 = r0 / r24
            r24 = r0
        L_0x019c:
            double r0 = (double) r3
            double r0 = r0 / r39
            double r0 = r0 * r24
            r6 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r0 = r0 + r6
            int r0 = (int) r0
            int r1 = r0 % 2
            if (r1 != 0) goto L_0x01ae
            int r0 = r0 + 1
            r16 = r0
            goto L_0x01b0
        L_0x01ae:
            r16 = r0
        L_0x01b0:
            double r26 = r8.alpha(r4)
            double r28 = im.bclpbkiauv.ui.utils.translate.ssrc.I0Bessel.value(r26)
            int r1 = r3 / r9
            int r0 = r16 / r1
            r2 = 1
            int r17 = r0 + 1
            int[] r2 = new int[r1]
            r0 = 0
        L_0x01c2:
            if (r0 >= r1) goto L_0x01e0
            int r35 = r3 / r9
            int r46 = r3 / r14
            int r46 = r46 * r0
            int r47 = r3 / r9
            int r46 = r46 % r47
            int r35 = r35 - r46
            r2[r0] = r35
            r6 = r2[r0]
            int r7 = r3 / r9
            if (r6 != r7) goto L_0x01db
            r6 = 0
            r2[r0] = r6
        L_0x01db:
            int r0 = r0 + 1
            r6 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x01c2
        L_0x01e0:
            int[] r6 = new int[r1]
            r0 = 0
        L_0x01e3:
            if (r0 >= r1) goto L_0x020f
            int r7 = r3 / r14
            r35 = r2[r0]
            int r7 = r7 - r35
            int r35 = r3 / r9
            int r7 = r7 / r35
            r32 = 1
            int r7 = r7 + 1
            r6[r0] = r7
            int r7 = r0 + 1
            if (r7 != r1) goto L_0x01fb
            r7 = 0
            goto L_0x01fd
        L_0x01fb:
            int r7 = r0 + 1
        L_0x01fd:
            r7 = r2[r7]
            if (r7 != 0) goto L_0x020a
            r7 = r6[r0]
            r32 = 1
            int r7 = r7 + -1
            r6[r0] = r7
            goto L_0x020c
        L_0x020a:
            r32 = 1
        L_0x020c:
            int r0 = r0 + 1
            goto L_0x01e3
        L_0x020f:
            r32 = 1
            r35 = r0
            r7 = 2
            int[] r0 = new int[r7]
            r0[r32] = r17
            r7 = 0
            r0[r7] = r1
            java.lang.Object r0 = java.lang.reflect.Array.newInstance(r15, r0)
            r48 = r0
            double[][] r48 = (double[][]) r48
            int r0 = r16 / 2
            int r0 = -r0
            r7 = r0
        L_0x0227:
            int r0 = r16 / 2
            if (r7 > r0) goto L_0x0276
            int r0 = r16 / 2
            int r0 = r0 + r7
            int r0 = r0 % r1
            r35 = r48[r0]
            int r0 = r16 / 2
            int r0 = r0 + r7
            int r49 = r0 / r1
            r50 = r1
            r51 = r2
            double r1 = (double) r7
            r0 = r76
            r8 = r3
            r3 = r16
            r46 = r4
            r4 = r26
            r52 = r7
            r61 = r45
            r45 = r33
            r33 = r6
            r6 = r28
            double r6 = r0.win(r1, r3, r4, r6)
            double r4 = (double) r8
            r1 = r52
            r2 = r43
            double r0 = r0.hn_lpf(r1, r2, r4)
            double r6 = r6 * r0
            double r0 = (double) r8
            double r6 = r6 * r0
            double r0 = (double) r9
            double r6 = r6 / r0
            r35[r49] = r6
            int r7 = r52 + 1
            r3 = r8
            r6 = r33
            r33 = r45
            r4 = r46
            r1 = r50
            r2 = r51
            r45 = r61
            r8 = r76
            goto L_0x0227
        L_0x0276:
            r50 = r1
            r51 = r2
            r8 = r3
            r46 = r4
            r52 = r7
            r61 = r45
            r45 = r33
            r33 = r6
            r6 = r16
            r7 = r17
            r46 = r33
            r5 = r50
            r35 = r51
            r4 = r52
        L_0x0291:
            r76.setstarttime()
            int r3 = r11 / 2
            r0 = 0
            r47 = 0
            r1 = 0
            r16 = r0
            r2 = 2
            int[] r0 = new int[r2]
            r17 = 1
            r0[r17] = r11
            r24 = 0
            r0[r24] = r10
            java.lang.Object r0 = java.lang.reflect.Array.newInstance(r15, r0)
            r32 = r0
            double[][] r32 = (double[][]) r32
            int r0 = r7 + 1
            int r0 = r0 + r3
            r25 = r1
            int[] r1 = new int[r2]
            r1[r17] = r0
            r1[r24] = r10
            java.lang.Object r0 = java.lang.reflect.Array.newInstance(r15, r1)
            r62 = r0
            double[][] r62 = (double[][]) r62
            int r0 = r3 / r12
            int r0 = r0 + r12
            int r0 = r0 + 1
            int r0 = r0 * r10
            r15 = r80
            r1 = 1
            int r0 = r0 * r15
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocate(r0)
            double r1 = (double) r3
            r24 = r4
            r26 = r5
            double r4 = (double) r14
            double r1 = r1 * r4
            double r4 = (double) r13
            double r1 = r1 / r4
            r4 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r1 = r1 + r4
            r59 = r12
            r12 = r81
            int r4 = r12 * r10
            double r4 = (double) r4
            double r1 = r1 * r4
            int r1 = (int) r1
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocate(r1)
            int r1 = r3 / r59
            int r1 = r1 + r59
            r2 = 1
            int r1 = r1 + r2
            int r1 = r1 * r10
            double[] r4 = new double[r1]
            double r1 = (double) r10
            r60 = r11
            double r11 = (double) r3
            r30 = r4
            r29 = r5
            double r4 = (double) r14
            double r11 = r11 * r4
            double r4 = (double) r13
            double r11 = r11 / r4
            r4 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r11 = r11 + r4
            double r1 = r1 * r11
            int r1 = (int) r1
            double[] r11 = new double[r1]
            r12 = 0
            r1 = 0
            r2 = 0
            r27 = 0
            r28 = 0
            r31 = 0
            r33 = 1
            r39 = 0
            r13 = r38
            double r4 = (double) r13
            double r4 = r4 / r41
            r38 = r0
            r40 = r1
            double r0 = (double) r9
            r63 = r11
            r64 = r12
            double r11 = (double) r14
            double r0 = r0 / r11
            double r4 = r4 / r0
            double r0 = (double) r6
            double r0 = r0 / r41
            double r11 = (double) r8
            r66 = r8
            r65 = r9
            double r8 = (double) r14
            double r11 = r11 / r8
            double r0 = r0 / r11
            double r4 = r4 + r0
            int r0 = (int) r4
            r1 = 0
            r4 = r1
            r5 = r1
            r8 = r0
            r9 = r4
            r4 = r24
            r1 = r38
            r0 = r86
        L_0x0342:
            r11 = 0
            int r12 = r3 - r11
            r17 = 1
            int r12 = r12 + -1
            int r12 = r12 / r59
            int r12 = r12 + 1
            r86 = r2
            int r2 = r12 + r5
            if (r2 <= r0) goto L_0x0355
            int r12 = r0 - r5
        L_0x0355:
            r2 = 0
            r1.position(r2)
            int r2 = r15 * r10
            int r2 = r2 * r12
            r1.limit(r2)
            int r2 = r1.limit()
            byte[] r2 = new byte[r2]
            r38 = r6
            r6 = r77
            int r24 = r6.read(r2)
            if (r24 >= 0) goto L_0x0377
            r24 = 0
            r25 = r0
            r0 = r24
            goto L_0x037b
        L_0x0377:
            r25 = r0
            r0 = r24
        L_0x037b:
            r24 = r4
            int r4 = r1.limit()
            if (r0 >= r4) goto L_0x0389
            int r4 = r0 / r15
            int r4 = r4 * r10
            int r4 = r4 + r5
            goto L_0x038b
        L_0x0389:
            r4 = r25
        L_0x038b:
            r1.limit(r0)
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.wrap(r2)
            r1.position(r0)
            r1.flip()
            int r25 = r15 * r10
            int r25 = r0 / r25
            r0 = 1
            if (r15 == r0) goto L_0x046e
            r0 = 2
            if (r15 == r0) goto L_0x043b
            r0 = 3
            if (r15 == r0) goto L_0x03f3
            r0 = 4
            if (r15 == r0) goto L_0x03b6
            r27 = r2
            r55 = r8
            r56 = r9
            r0 = r24
            r2 = r76
            r24 = r13
            goto L_0x049a
        L_0x03b6:
            r24 = 0
            r0 = r24
        L_0x03ba:
            r27 = r2
            int r2 = r25 * r10
            if (r0 >= r2) goto L_0x03e9
            r2 = r76
            java.nio.ByteOrder r6 = r2.byteOrder
            java.nio.ByteBuffer r6 = r1.order(r6)
            int r6 = r6.getInt(r0)
            int r24 = r10 * r47
            int r24 = r24 + r0
            r49 = 4467570830353629184(0x3e00000000200000, double:4.656612875245797E-10)
            r55 = r8
            r56 = r9
            double r8 = (double) r6
            double r8 = r8 * r49
            r30[r24] = r8
            int r0 = r0 + 1
            r6 = r77
            r2 = r27
            r8 = r55
            r9 = r56
            goto L_0x03ba
        L_0x03e9:
            r2 = r76
            r55 = r8
            r56 = r9
            r24 = r13
            goto L_0x049a
        L_0x03f3:
            r27 = r2
            r55 = r8
            r56 = r9
            r2 = r76
            r0 = 0
        L_0x03fc:
            int r6 = r25 * r10
            if (r0 >= r6) goto L_0x0438
            int r6 = r10 * r47
            int r6 = r6 + r0
            int r8 = r0 * 3
            byte r8 = r1.get(r8)
            r8 = r8 & 255(0xff, float:3.57E-43)
            r9 = 0
            int r8 = r8 << r9
            int r9 = r0 * 3
            r17 = 1
            int r9 = r9 + 1
            byte r9 = r1.get(r9)
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r9 = r9 << 8
            r8 = r8 | r9
            int r9 = r0 * 3
            r24 = 2
            int r9 = r9 + 2
            byte r9 = r1.get(r9)
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r9 = r9 << 16
            r8 = r8 | r9
            double r8 = (double) r8
            r49 = 4503599627907366976(0x3e80000020000040, double:1.1920930376163766E-7)
            double r8 = r8 * r49
            r30[r6] = r8
            int r0 = r0 + 1
            goto L_0x03fc
        L_0x0438:
            r24 = r13
            goto L_0x049a
        L_0x043b:
            r27 = r2
            r55 = r8
            r56 = r9
            r2 = r76
            r0 = 0
        L_0x0444:
            int r6 = r25 * r10
            if (r0 >= r6) goto L_0x046a
            java.nio.ByteOrder r6 = r2.byteOrder
            java.nio.ByteBuffer r6 = r1.order(r6)
            java.nio.ShortBuffer r6 = r6.asShortBuffer()
            short r6 = r6.get(r0)
            int r8 = r10 * r47
            int r8 = r8 + r0
            r49 = 4539628561832607872(0x3f00002000400080, double:3.051850947599719E-5)
            r9 = r13
            double r13 = (double) r6
            double r13 = r13 * r49
            r30[r8] = r13
            int r0 = r0 + 1
            r14 = r83
            r13 = r9
            goto L_0x0444
        L_0x046a:
            r9 = r13
            r24 = r9
            goto L_0x049a
        L_0x046e:
            r27 = r2
            r55 = r8
            r56 = r9
            r9 = r13
            r2 = r76
            r0 = 0
        L_0x0478:
            int r6 = r25 * r10
            if (r0 >= r6) goto L_0x0498
            int r6 = r10 * r47
            int r6 = r6 + r0
            r13 = 4575692682822812680(0x3f80204081020408, double:0.007874015748031496)
            byte r8 = r1.get(r0)
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r8 = r8 + -128
            r24 = r9
            double r8 = (double) r8
            double r8 = r8 * r13
            r30[r6] = r8
            int r0 = r0 + 1
            r9 = r24
            goto L_0x0478
        L_0x0498:
            r24 = r9
        L_0x049a:
            int r6 = r10 * r12
            if (r0 >= r6) goto L_0x04a3
            r30[r0] = r19
            int r0 = r0 + 1
            goto L_0x049a
        L_0x04a3:
            int r6 = r5 + r25
            int r5 = r77.available()
            if (r5 < 0) goto L_0x04b0
            if (r6 < r4) goto L_0x04ae
            goto L_0x04b0
        L_0x04ae:
            r5 = 0
            goto L_0x04b1
        L_0x04b0:
            r5 = 1
        L_0x04b1:
            r39 = r5
            r8 = r11
            r9 = r40
            r5 = 0
            r14 = r11
            r13 = r16
            r11 = r86
        L_0x04bc:
            if (r5 >= r10) goto L_0x066c
            r14 = r8
            r16 = 0
            r86 = r0
            r0 = r16
        L_0x04c5:
            if (r0 >= r14) goto L_0x04ce
            r16 = r32[r5]
            r16[r0] = r19
            int r0 = r0 + 1
            goto L_0x04c5
        L_0x04ce:
            r16 = r14
            r49 = 0
            r86 = r0
            r0 = r16
            r16 = r49
        L_0x04d8:
            if (r0 >= r3) goto L_0x0505
            r49 = r32[r5]
            int r50 = r16 * r10
            int r50 = r50 + r5
            r50 = r30[r50]
            r49[r0] = r50
            int r49 = r0 + 1
            r67 = r1
            r1 = r49
        L_0x04eb:
            r68 = r4
            int r4 = r0 + r59
            if (r1 >= r4) goto L_0x04fa
            r4 = r32[r5]
            r4[r1] = r19
            int r1 = r1 + 1
            r4 = r68
            goto L_0x04eb
        L_0x04fa:
            int r0 = r0 + r59
            int r16 = r16 + 1
            r86 = r1
            r1 = r67
            r4 = r68
            goto L_0x04d8
        L_0x0505:
            r67 = r1
            r68 = r4
            r1 = r3
        L_0x050a:
            r4 = r60
            if (r1 >= r4) goto L_0x0517
            r49 = r32[r5]
            r49[r1] = r19
            int r1 = r1 + 1
            r60 = r4
            goto L_0x050a
        L_0x0517:
            int r14 = r0 - r3
            int r11 = r11 + r16
            r60 = r0
            im.bclpbkiauv.ui.utils.translate.ssrc.SplitRadixFft r0 = r2.fft
            r51 = 1
            r52 = r32[r5]
            r49 = r0
            r50 = r4
            r53 = r45
            r54 = r61
            r49.rdft(r50, r51, r52, r53, r54)
            r0 = r32[r5]
            r36 = 0
            r49 = r58[r36]
            r51 = r32[r5]
            r52 = r51[r36]
            double r49 = r49 * r52
            r0[r36] = r49
            r0 = r32[r5]
            r17 = 1
            r49 = r58[r17]
            r51 = r32[r5]
            r52 = r51[r17]
            double r49 = r49 * r52
            r0[r17] = r49
            r0 = 1
        L_0x054b:
            if (r0 >= r3) goto L_0x05a0
            int r49 = r0 * 2
            r49 = r58[r49]
            r51 = r32[r5]
            int r52 = r0 * 2
            r52 = r51[r52]
            double r49 = r49 * r52
            int r51 = r0 * 2
            r17 = 1
            int r51 = r51 + 1
            r51 = r58[r51]
            r53 = r32[r5]
            int r54 = r0 * 2
            int r54 = r54 + 1
            r69 = r53[r54]
            double r51 = r51 * r69
            double r49 = r49 - r51
            int r51 = r0 * 2
            int r51 = r51 + 1
            r51 = r58[r51]
            r53 = r32[r5]
            int r54 = r0 * 2
            r69 = r53[r54]
            double r51 = r51 * r69
            int r53 = r0 * 2
            r53 = r58[r53]
            r60 = r32[r5]
            int r69 = r0 * 2
            r17 = 1
            int r69 = r69 + 1
            r69 = r60[r69]
            double r53 = r53 * r69
            double r51 = r51 + r53
            r53 = r32[r5]
            int r54 = r0 * 2
            r53[r54] = r49
            r53 = r32[r5]
            int r54 = r0 * 2
            r17 = 1
            int r54 = r54 + 1
            r53[r54] = r51
            int r0 = r0 + 1
            goto L_0x054b
        L_0x05a0:
            r60 = r0
            im.bclpbkiauv.ui.utils.translate.ssrc.SplitRadixFft r0 = r2.fft
            r51 = -1
            r52 = r32[r5]
            r49 = r0
            r50 = r4
            r53 = r45
            r54 = r61
            r49.rdft(r50, r51, r52, r53, r54)
            r0 = 0
        L_0x05b4:
            if (r0 >= r3) goto L_0x05c9
            r49 = r62[r5]
            int r50 = r7 + 1
            int r50 = r50 + r0
            r51 = r49[r50]
            r53 = r32[r5]
            r69 = r53[r0]
            double r51 = r51 + r69
            r49[r50] = r51
            int r0 = r0 + 1
            goto L_0x05b4
        L_0x05c9:
            int r49 = r66 / r65
            int r49 = r31 / r49
            int r50 = r66 / r65
            int r50 = r31 % r50
            if (r50 == 0) goto L_0x05d5
            int r49 = r49 + 1
        L_0x05d5:
            r50 = r0
            r36 = 0
            r0 = r62[r36]
            int r0 = r0.length
            int r0 = r0 * r5
            int r0 = r0 + r49
            r40 = r9
            r49 = 0
        L_0x05e4:
            r51 = r1
            r1 = r62[r36]
            int r1 = r1.length
            int r1 = r1 * r5
            int r1 = r0 - r1
            int r2 = r3 + 1
            if (r1 >= r2) goto L_0x0652
            r1 = 0
            r52 = r0
            r53 = r35[r40]
            r54 = r46[r40]
            int r0 = r0 + r54
            r86 = r0
            int r0 = r40 + 1
            r54 = r8
            r8 = r26
            if (r0 != r8) goto L_0x0609
            r0 = 0
            r40 = r0
            goto L_0x060b
        L_0x0609:
            r40 = r0
        L_0x060b:
            r0 = 0
        L_0x060d:
            if (r0 >= r7) goto L_0x0634
            r26 = r48[r53]
            r69 = r26[r0]
            r60 = r3
            r26 = 0
            r3 = r62[r26]
            int r3 = r3.length
            int r3 = r52 / r3
            r3 = r62[r3]
            r71 = r4
            r4 = r62[r26]
            int r4 = r4.length
            int r4 = r52 % r4
            r72 = r3[r4]
            double r69 = r69 * r72
            double r1 = r1 + r69
            int r52 = r52 + 1
            int r0 = r0 + 1
            r3 = r60
            r4 = r71
            goto L_0x060d
        L_0x0634:
            r60 = r3
            r71 = r4
            int r3 = r49 * r10
            int r3 = r64 + r3
            int r3 = r3 + r5
            r63[r3] = r1
            int r49 = r49 + 1
            r2 = r76
            r50 = r0
            r26 = r8
            r1 = r51
            r8 = r54
            r3 = r60
            r36 = 0
            r0 = r86
            goto L_0x05e4
        L_0x0652:
            r60 = r3
            r71 = r4
            r54 = r8
            r8 = r26
            r13 = r49
            int r5 = r5 + 1
            r2 = r76
            r0 = r50
            r8 = r54
            r1 = r67
            r4 = r68
            r60 = r71
            goto L_0x04bc
        L_0x066c:
            r86 = r0
            r67 = r1
            r68 = r4
            r54 = r8
            r8 = r26
            r71 = r60
            r60 = r3
            r4 = r83
            int r0 = r66 / r4
            int r0 = r0 * r13
            int r31 = r31 + r0
            r29.clear()
            if (r87 == 0) goto L_0x06d8
            r0 = 0
        L_0x0688:
            int r1 = r13 * r10
            if (r0 >= r1) goto L_0x06b4
            r1 = r63[r0]
            int r3 = (r1 > r19 ? 1 : (r1 == r19 ? 0 : -1))
            if (r3 <= 0) goto L_0x0695
            r1 = r63[r0]
            goto L_0x0698
        L_0x0695:
            r1 = r63[r0]
            double r1 = -r1
        L_0x0698:
            r3 = 0
            r49 = r21[r3]
            int r16 = (r49 > r1 ? 1 : (r49 == r1 ? 0 : -1))
            if (r16 >= 0) goto L_0x06a2
            r49 = r1
            goto L_0x06a4
        L_0x06a2:
            r49 = r21[r3]
        L_0x06a4:
            r21[r3] = r49
            java.nio.DoubleBuffer r3 = r29.asDoubleBuffer()
            r49 = r1
            r1 = r63[r0]
            r3.put(r0, r1)
            int r0 = r0 + 1
            goto L_0x0688
        L_0x06b4:
            r72 = r8
            r51 = r9
            r52 = r11
            r43 = r14
            r14 = r29
            r9 = r60
            r57 = r67
            r60 = r71
            r53 = 4
            r67 = 3
            r69 = 1
            r8 = r76
            r11 = r4
            r4 = r0
            r75 = r30
            r30 = r12
            r12 = r68
            r68 = r75
            goto L_0x096f
        L_0x06d8:
            r3 = r81
            r1 = 1
            if (r3 == r1) goto L_0x08cc
            r0 = 2
            if (r3 == r0) goto L_0x081f
            r2 = 3
            if (r3 == r2) goto L_0x0708
            r72 = r8
            r51 = r9
            r52 = r11
            r43 = r14
            r14 = r29
            r9 = r60
            r57 = r67
            r60 = r71
            r53 = 4
            r67 = 3
            r69 = 1
            r8 = r76
            r11 = r4
            r4 = r86
            r75 = r30
            r30 = r12
            r12 = r68
            r68 = r75
            goto L_0x096f
        L_0x0708:
            r16 = 4710765209155796992(0x415fffffc0000000, double:8388607.0)
            double r49 = r84 * r16
            r0 = 0
            r5 = 0
            r23 = r0
        L_0x0713:
            int r0 = r13 * r10
            if (r5 >= r0) goto L_0x07f8
            if (r88 == 0) goto L_0x0752
            r51 = r63[r5]
            double r51 = r51 * r49
            r53 = 4
            r0 = r76
            r26 = r8
            r57 = r67
            r67 = 3
            r69 = 1
            r8 = r76
            r1 = r51
            r51 = r9
            r9 = r60
            r3 = r21
            r52 = r11
            r43 = r14
            r60 = r71
            r14 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r11 = r4
            r75 = r30
            r30 = r12
            r12 = r68
            r68 = r75
            r4 = r88
            r15 = r5
            r72 = r26
            r14 = r29
            r5 = r23
            int r0 = r0.do_shaping(r1, r3, r4, r5)
            goto L_0x07ab
        L_0x0752:
            r15 = r5
            r72 = r8
            r51 = r9
            r52 = r11
            r43 = r14
            r14 = r29
            r9 = r60
            r57 = r67
            r60 = r71
            r53 = 4
            r67 = 3
            r69 = 1
            r8 = r76
            r11 = r4
            r75 = r30
            r30 = r12
            r12 = r68
            r68 = r75
            r0 = r63[r15]
            double r0 = r0 * r49
            int r0 = r8.RINT(r0)
            r1 = -8388608(0xffffffffff800000, float:-Infinity)
            if (r0 >= r1) goto L_0x0793
            double r1 = (double) r0
            r3 = -4512606826625236992(0xc160000000000000, double:-8388608.0)
            double r1 = r1 / r3
            r3 = 0
            r4 = r21[r3]
            int r26 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r26 >= 0) goto L_0x078d
            r4 = r1
            goto L_0x078f
        L_0x078d:
            r4 = r21[r3]
        L_0x078f:
            r21[r3] = r4
            r0 = -8388608(0xffffffffff800000, float:-Infinity)
        L_0x0793:
            r1 = 8388607(0x7fffff, float:1.1754942E-38)
            if (r1 >= r0) goto L_0x07ab
            double r1 = (double) r0
            double r1 = r1 / r16
            r3 = 0
            r4 = r21[r3]
            int r26 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r26 >= 0) goto L_0x07a4
            r4 = r1
            goto L_0x07a6
        L_0x07a4:
            r4 = r21[r3]
        L_0x07a6:
            r21[r3] = r4
            r0 = 8388607(0x7fffff, float:1.1754942E-38)
        L_0x07ab:
            int r5 = r15 * 3
            r1 = r0 & 255(0xff, float:3.57E-43)
            byte r1 = (byte) r1
            r14.put(r5, r1)
            int r0 = r0 >> 8
            int r5 = r15 * 3
            int r5 = r5 + 1
            r1 = r0 & 255(0xff, float:3.57E-43)
            byte r1 = (byte) r1
            r14.put(r5, r1)
            int r0 = r0 >> 8
            int r5 = r15 * 3
            r1 = 2
            int r5 = r5 + r1
            r1 = r0 & 255(0xff, float:3.57E-43)
            byte r1 = (byte) r1
            r14.put(r5, r1)
            int r1 = r23 + 1
            if (r1 != r10) goto L_0x07d3
            r1 = 0
            r23 = r1
            goto L_0x07d5
        L_0x07d3:
            r23 = r1
        L_0x07d5:
            int r5 = r15 + 1
            r15 = r80
            r3 = r81
            r4 = r11
            r29 = r14
            r14 = r43
            r11 = r52
            r67 = r57
            r71 = r60
            r8 = r72
            r1 = 1
            r2 = 3
            r60 = r9
            r9 = r51
            r75 = r68
            r68 = r12
            r12 = r30
            r30 = r75
            goto L_0x0713
        L_0x07f8:
            r15 = r5
            r72 = r8
            r51 = r9
            r52 = r11
            r43 = r14
            r14 = r29
            r9 = r60
            r57 = r67
            r60 = r71
            r53 = 4
            r67 = 3
            r69 = 1
            r8 = r76
            r11 = r4
            r75 = r30
            r30 = r12
            r12 = r68
            r68 = r75
            r4 = r15
            r5 = r23
            goto L_0x096f
        L_0x081f:
            r72 = r8
            r51 = r9
            r52 = r11
            r43 = r14
            r14 = r29
            r9 = r60
            r57 = r67
            r60 = r71
            r53 = 4
            r67 = 3
            r69 = 1
            r8 = r76
            r11 = r4
            r75 = r30
            r30 = r12
            r12 = r68
            r68 = r75
            r15 = 4674736138332667904(0x40dfffc000000000, double:32767.0)
            double r49 = r84 * r15
            r0 = 0
            r1 = 0
            r17 = r0
            r5 = r1
        L_0x084c:
            int r0 = r13 * r10
            if (r5 >= r0) goto L_0x08c6
            if (r88 == 0) goto L_0x0869
            r0 = r63[r5]
            double r1 = r0 * r49
            r0 = r76
            r3 = r21
            r4 = r88
            r15 = r5
            r5 = r17
            int r0 = r0.do_shaping(r1, r3, r4, r5)
            r3 = 4674736138332667904(0x40dfffc000000000, double:32767.0)
            goto L_0x08aa
        L_0x0869:
            r15 = r5
            r0 = r63[r15]
            double r0 = r0 * r49
            int r0 = r8.RINT(r0)
            r1 = -32768(0xffffffffffff8000, float:NaN)
            if (r0 >= r1) goto L_0x0889
            double r1 = (double) r0
            r3 = -4548635623644200960(0xc0e0000000000000, double:-32768.0)
            double r1 = r1 / r3
            r3 = 0
            r4 = r21[r3]
            int r16 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r16 >= 0) goto L_0x0883
            r4 = r1
            goto L_0x0885
        L_0x0883:
            r4 = r21[r3]
        L_0x0885:
            r21[r3] = r4
            r0 = -32768(0xffffffffffff8000, float:NaN)
        L_0x0889:
            r1 = 32767(0x7fff, float:4.5916E-41)
            if (r1 >= r0) goto L_0x08a5
            double r1 = (double) r0
            r3 = 4674736138332667904(0x40dfffc000000000, double:32767.0)
            double r1 = r1 / r3
            r5 = 0
            r73 = r21[r5]
            int r16 = (r73 > r1 ? 1 : (r73 == r1 ? 0 : -1))
            if (r16 >= 0) goto L_0x089e
            r73 = r1
            goto L_0x08a0
        L_0x089e:
            r73 = r21[r5]
        L_0x08a0:
            r21[r5] = r73
            r0 = 32767(0x7fff, float:4.5916E-41)
            goto L_0x08aa
        L_0x08a5:
            r3 = 4674736138332667904(0x40dfffc000000000, double:32767.0)
        L_0x08aa:
            java.nio.ByteOrder r1 = r8.byteOrder
            java.nio.ByteBuffer r1 = r14.order(r1)
            java.nio.ShortBuffer r1 = r1.asShortBuffer()
            short r2 = (short) r0
            r1.put(r15, r2)
            int r1 = r17 + 1
            if (r1 != r10) goto L_0x08c0
            r1 = 0
            r17 = r1
            goto L_0x08c2
        L_0x08c0:
            r17 = r1
        L_0x08c2:
            int r5 = r15 + 1
            r15 = r3
            goto L_0x084c
        L_0x08c6:
            r15 = r5
            r4 = r15
            r5 = r17
            goto L_0x096f
        L_0x08cc:
            r72 = r8
            r51 = r9
            r52 = r11
            r43 = r14
            r14 = r29
            r9 = r60
            r57 = r67
            r60 = r71
            r53 = 4
            r67 = 3
            r69 = 1
            r8 = r76
            r11 = r4
            r75 = r30
            r30 = r12
            r12 = r68
            r68 = r75
            r15 = 4638637247447433216(0x405fc00000000000, double:127.0)
            double r49 = r84 * r15
            r0 = 0
            r1 = 0
            r17 = r0
            r5 = r1
        L_0x08f9:
            int r0 = r13 * r10
            if (r5 >= r0) goto L_0x096b
            if (r88 == 0) goto L_0x0916
            r0 = r63[r5]
            double r1 = r0 * r49
            r0 = r76
            r3 = r21
            r4 = r88
            r15 = r5
            r5 = r17
            int r0 = r0.do_shaping(r1, r3, r4, r5)
            r3 = 4638637247447433216(0x405fc00000000000, double:127.0)
            goto L_0x0957
        L_0x0916:
            r15 = r5
            r0 = r63[r15]
            double r0 = r0 * r49
            int r0 = r8.RINT(r0)
            r1 = -128(0xffffffffffffff80, float:NaN)
            if (r0 >= r1) goto L_0x0936
            double r1 = (double) r0
            r3 = -4584664420663164928(0xc060000000000000, double:-128.0)
            double r1 = r1 / r3
            r3 = 0
            r4 = r21[r3]
            int r16 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r16 >= 0) goto L_0x0930
            r4 = r1
            goto L_0x0932
        L_0x0930:
            r4 = r21[r3]
        L_0x0932:
            r21[r3] = r4
            r0 = -128(0xffffffffffffff80, float:NaN)
        L_0x0936:
            r1 = 127(0x7f, float:1.78E-43)
            if (r1 >= r0) goto L_0x0952
            double r1 = (double) r0
            r3 = 4638637247447433216(0x405fc00000000000, double:127.0)
            double r1 = r1 / r3
            r5 = 0
            r73 = r21[r5]
            int r16 = (r73 > r1 ? 1 : (r73 == r1 ? 0 : -1))
            if (r16 >= 0) goto L_0x094b
            r73 = r1
            goto L_0x094d
        L_0x094b:
            r73 = r21[r5]
        L_0x094d:
            r21[r5] = r73
            r0 = 127(0x7f, float:1.78E-43)
            goto L_0x0957
        L_0x0952:
            r3 = 4638637247447433216(0x405fc00000000000, double:127.0)
        L_0x0957:
            int r1 = r0 + 128
            byte r1 = (byte) r1
            r14.put(r15, r1)
            int r1 = r17 + 1
            if (r1 != r10) goto L_0x0965
            r1 = 0
            r17 = r1
            goto L_0x0967
        L_0x0965:
            r17 = r1
        L_0x0967:
            int r5 = r15 + 1
            r15 = r3
            goto L_0x08f9
        L_0x096b:
            r15 = r5
            r4 = r15
            r5 = r17
        L_0x096f:
            if (r33 != 0) goto L_0x0a0f
            if (r39 == 0) goto L_0x09e1
            double r0 = (double) r6
            double r2 = (double) r11
            double r0 = r0 * r2
            r2 = r82
            r86 = r4
            r15 = r24
            double r3 = (double) r2
            double r0 = r0 / r3
            double r0 = r0 + r41
            int r3 = r56 + r13
            double r3 = (double) r3
            int r16 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r16 <= 0) goto L_0x09ad
            r0 = 0
            r14.position(r0)
            int r1 = r81 * r10
            int r1 = r1 * r13
            r14.limit(r1)
            r1 = r78
            r3 = 3
            r8.writeBuffers(r1, r14)
            int r4 = r56 + r13
            r23 = r5
            r17 = r6
            r49 = r7
            r6 = r10
            r16 = r12
            r0 = r13
            r5 = r55
            r13 = r9
            r9 = r4
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0ab8
        L_0x09ad:
            r1 = r78
            r0 = 0
            r14.position(r0)
            int r0 = r81 * r10
            double r3 = (double) r0
            r16 = r12
            r0 = r13
            double r12 = (double) r6
            r23 = r5
            r17 = r6
            double r5 = (double) r11
            double r12 = r12 * r5
            double r5 = (double) r2
            double r12 = r12 / r5
            double r5 = java.lang.Math.floor(r12)
            double r5 = r5 + r41
            r13 = r9
            r12 = r56
            double r9 = (double) r12
            double r5 = r5 - r9
            double r3 = r3 * r5
            int r3 = (int) r3
            if (r3 <= 0) goto L_0x09dd
            r14.limit(r3)
            r8.writeBuffers(r1, r14)
            r49 = r7
            goto L_0x0a91
        L_0x09dd:
            r49 = r7
            goto L_0x0a91
        L_0x09e1:
            r1 = r78
            r2 = r82
            r86 = r4
            r23 = r5
            r17 = r6
            r16 = r12
            r0 = r13
            r15 = r24
            r12 = r56
            r3 = 3
            r13 = r9
            r4 = 0
            r14.position(r4)
            r4 = r79
            int r5 = r81 * r4
            int r5 = r5 * r0
            r14.limit(r5)
            r8.writeBuffers(r1, r14)
            int r9 = r12 + r0
            r6 = r4
            r49 = r7
            r5 = r55
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0ab8
        L_0x0a0f:
            r1 = r78
            r2 = r82
            r86 = r4
            r23 = r5
            r17 = r6
            r4 = r10
            r16 = r12
            r0 = r13
            r15 = r24
            r12 = r56
            r3 = 3
            r13 = r9
            r5 = r55
            if (r0 >= r5) goto L_0x0a30
            int r5 = r5 - r0
            r6 = r4
            r49 = r7
            r9 = r12
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0ab8
        L_0x0a30:
            if (r39 == 0) goto L_0x0a9a
            r6 = r17
            double r9 = (double) r6
            double r3 = (double) r11
            double r9 = r9 * r3
            double r3 = (double) r2
            double r9 = r9 / r3
            double r9 = r9 + r41
            int r3 = r12 + r0
            int r3 = r3 - r5
            double r3 = (double) r3
            int r17 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r17 <= 0) goto L_0x0a65
            r3 = r79
            int r4 = r81 * r3
            int r4 = r4 * r5
            r14.position(r4)
            int r4 = r81 * r3
            int r9 = r0 - r5
            int r4 = r4 * r9
            r14.limit(r4)
            r8.writeBuffers(r1, r14)
            int r4 = r0 - r5
            int r9 = r12 + r4
            r17 = r6
            r49 = r7
            r6 = r3
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0ab8
        L_0x0a65:
            r3 = r79
            int r4 = r81 * r3
            int r4 = r4 * r5
            r14.position(r4)
            int r4 = r81 * r3
            double r9 = (double) r4
            double r3 = (double) r6
            r17 = r6
            r49 = r7
            double r6 = (double) r11
            double r3 = r3 * r6
            double r6 = (double) r2
            double r3 = r3 / r6
            double r3 = java.lang.Math.floor(r3)
            double r3 = r3 + r41
            double r6 = (double) r12
            double r3 = r3 + r6
            double r6 = (double) r0
            double r3 = r3 + r6
            double r6 = (double) r5
            double r3 = r3 - r6
            double r9 = r9 * r3
            int r3 = (int) r9
            r14.limit(r3)
            r8.writeBuffers(r1, r14)
        L_0x0a91:
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r8.showprogress(r3)
            r0 = 0
            r3 = r21[r0]
            return r3
        L_0x0a9a:
            r49 = r7
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r6 = r79
            int r7 = r81 * r6
            int r7 = r7 * r5
            r14.position(r7)
            int r7 = r81 * r6
            int r7 = r7 * r0
            r14.limit(r7)
            r8.writeBuffers(r1, r14)
            int r7 = r0 - r5
            int r9 = r12 + r7
            r7 = 0
            r33 = r7
        L_0x0ab8:
            int r7 = r31 + -1
            int r10 = r66 / r65
            int r7 = r7 / r10
            r10 = r13
            if (r7 <= r10) goto L_0x0ac1
            r7 = r10
        L_0x0ac1:
            r12 = 0
        L_0x0ac2:
            if (r12 >= r6) goto L_0x0ad9
            r13 = r62[r12]
            r3 = r62[r12]
            int r4 = r49 + 1
            int r4 = r4 + r10
            int r4 = r4 - r7
            r23 = r0
            r0 = 0
            java.lang.System.arraycopy(r13, r7, r3, r0, r4)
            int r12 = r12 + 1
            r0 = r23
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0ac2
        L_0x0ad9:
            r23 = r0
            int r0 = r66 / r65
            int r0 = r0 * r7
            int r31 = r31 - r0
            r0 = 0
        L_0x0ae2:
            if (r0 >= r6) goto L_0x0af0
            r3 = r32[r0]
            r4 = r62[r0]
            int r12 = r49 + 1
            java.lang.System.arraycopy(r3, r10, r4, r12, r10)
            int r0 = r0 + 1
            goto L_0x0ae2
        L_0x0af0:
            int r3 = r18 + 1
            r4 = r18 & 7
            r12 = 7
            if (r4 != r12) goto L_0x0b04
            r4 = r17
            double r12 = (double) r4
            r17 = r0
            r0 = r16
            double r1 = (double) r0
            double r12 = r12 / r1
            r8.showprogress(r12)
            goto L_0x0b0a
        L_0x0b04:
            r4 = r17
            r17 = r0
            r0 = r16
        L_0x0b0a:
            r18 = r3
            r8 = r5
            r28 = r7
            r3 = r10
            r29 = r14
            r13 = r15
            r16 = r23
            r27 = r43
            r7 = r49
            r2 = r52
            r1 = r57
            r30 = r68
            r26 = r72
            r15 = r80
            r5 = r4
            r10 = r6
            r14 = r11
            r6 = r38
            r4 = r86
            goto L_0x0342
        L_0x0b2c:
            r65 = r0
            r55 = r1
            r6 = r10
            r11 = r14
            r1 = r15
            r59 = r38
            r53 = 4
            r69 = 1
            r15 = r9
            int r33 = r33 * 2
            r12 = r81
            r13 = r82
            r32 = r15
            r4 = r37
            r3 = r59
            r6 = 1
            r7 = 0
            r9 = 2
            r11 = r80
            r15 = r1
            r1 = r55
            goto L_0x0063
        L_0x0b50:
            r55 = r1
            r37 = r4
            r11 = r14
            r53 = 4
            r69 = 1
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            r2 = 6
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r82)
            r4 = 0
            r2[r4] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r83)
            r2[r69] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r83)
            r4 = 2
            r2[r4] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r82)
            r4 = 3
            r2[r4] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r83)
            r2[r53] = r3
            r3 = 5
            int r4 = r11 / r22
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r2[r3] = r4
            java.lang.String r3 = "Resampling from %dHz to %dHz is not supported.\n%d/gcd(%d,%d)=%d must be divided by 2 or 3."
            java.lang.String r2 = java.lang.String.format(r3, r2)
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.utils.translate.ssrc.SSRC.downsample(java.io.InputStream, java.io.OutputStream, int, int, int, int, int, double, int, boolean, int):double");
    }

    public double no_src(InputStream fpi, OutputStream fpo, int nch, int bps, int dbps, double gain, int chanklen, boolean twopass, int dither) throws IOException {
        ByteBuffer leos;
        ByteBuffer buf;
        int sumread;
        ByteBuffer leos2;
        int s;
        InputStream inputStream = fpi;
        OutputStream outputStream = fpo;
        int i = nch;
        int i2 = bps;
        int i3 = dbps;
        int i4 = 1;
        byte b = 0;
        double[] peak = {0.0d};
        setstarttime();
        if (twopass) {
            leos = ByteBuffer.allocate(8);
        } else {
            leos = null;
        }
        int i5 = 4;
        ByteBuffer buf2 = ByteBuffer.allocate(4);
        int ch = 0;
        int sumread2 = 0;
        while (true) {
            if (sumread2 >= chanklen * i) {
                int i6 = sumread2;
                ByteBuffer byteBuffer = leos;
                break;
            }
            double f = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            if (i2 == i4) {
                buf2.position(0);
                buf2.limit(1);
                byte[] tempData = new byte[buf2.limit()];
                inputStream.read(tempData);
                ByteBuffer buf3 = ByteBuffer.wrap(tempData);
                buf3.position(buf3.limit());
                buf3.flip();
                buf = buf3;
                f = 0.007874015748031496d * ((double) (buf3.get(0) + ByteCompanionObject.MIN_VALUE));
            } else if (i2 == 2) {
                buf2.position(0);
                buf2.limit(2);
                byte[] tempData2 = new byte[buf2.limit()];
                inputStream.read(tempData2);
                ByteBuffer buf4 = ByteBuffer.wrap(tempData2);
                buf4.position(buf4.limit());
                buf4.flip();
                f = ((double) buf4.order(this.byteOrder).asShortBuffer().get(0)) * 3.051850947599719E-5d;
                buf = buf4;
            } else if (i2 == 3) {
                buf2.position(b);
                buf2.limit(3);
                byte[] tempData3 = new byte[buf2.limit()];
                inputStream.read(tempData3);
                ByteBuffer buf5 = ByteBuffer.wrap(tempData3);
                buf5.position(buf5.limit());
                buf5.flip();
                f = ((double) (((buf5.get(1) & UByte.MAX_VALUE) << 8) | ((buf5.get(b) & UByte.MAX_VALUE) << b) | ((buf5.get(2) & UByte.MAX_VALUE) << 16))) * 1.1920930376163766E-7d;
                buf = buf5;
            } else if (i2 != i5) {
                buf = buf2;
            } else {
                buf2.position(b);
                buf2.limit(i5);
                byte[] tempData4 = new byte[buf2.limit()];
                inputStream.read(tempData4);
                ByteBuffer buf6 = ByteBuffer.wrap(tempData4);
                buf6.position(buf6.limit());
                buf6.flip();
                f = ((double) buf6.order(this.byteOrder).asIntBuffer().get(b)) * 4.656612875245797E-10d;
                buf = buf6;
            }
            if (fpi.available() == 0) {
                int i7 = sumread2;
                ByteBuffer byteBuffer2 = leos;
                ByteBuffer byteBuffer3 = buf;
                break;
            }
            double f2 = f * gain;
            if (twopass) {
                sumread = sumread2;
                leos2 = leos;
                double p = f2 > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? f2 : -f2;
                peak[0] = peak[0] < p ? p : peak[0];
                leos2.position(0);
                leos2.putDouble(f2);
                leos2.flip();
                writeBuffers(outputStream, leos2);
            } else if (i3 == 1) {
                sumread = sumread2;
                leos2 = leos;
                double f3 = f2 * 127.0d;
                int s2 = dither != 0 ? do_shaping(f3, peak, dither, ch) : RINT(f3);
                buf.position(0);
                buf.limit(1);
                buf.put(0, (byte) (s2 + 128));
                buf.flip();
                writeBuffers(outputStream, buf);
                double d = f3;
            } else if (i3 == 2) {
                sumread = sumread2;
                leos2 = leos;
                double f4 = f2 * 32767.0d;
                int s3 = dither != 0 ? do_shaping(f4, peak, dither, ch) : RINT(f4);
                buf.position(0);
                buf.limit(2);
                buf.asShortBuffer().put(0, (short) s3);
                buf.flip();
                writeBuffers(outputStream, buf);
                double d2 = f4;
            } else if (i3 != 3) {
                sumread = sumread2;
                leos2 = leos;
            } else {
                double f5 = 8388607.0d * f2;
                if (dither != 0) {
                    sumread = sumread2;
                    leos2 = leos;
                    s = do_shaping(f5, peak, dither, ch);
                } else {
                    sumread = sumread2;
                    leos2 = leos;
                    s = RINT(f5);
                }
                buf.position(0);
                buf.limit(3);
                buf.put(0, (byte) (s & 255));
                int s4 = s >> 8;
                buf.put(1, (byte) (s4 & 255));
                buf.put(2, (byte) ((s4 >> 8) & 255));
                buf.flip();
                writeBuffers(outputStream, buf);
                double d3 = f5;
            }
            int ch2 = ch + 1;
            if (ch2 == i) {
                ch = 0;
            } else {
                ch = ch2;
            }
            int sumread3 = sumread + 1;
            if ((262143 & sumread3) == 0) {
                showprogress(((double) sumread3) / ((double) (chanklen * i)));
            }
            sumread2 = sumread3;
            leos = leos2;
            buf2 = buf;
            i5 = 4;
            i4 = 1;
            b = 0;
            inputStream = fpi;
        }
        showprogress(1.0d);
        return peak[0];
    }

    public static void main(String[] args) throws Exception {
        new SSRC(args);
    }

    public SSRC() {
        this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        this.fft = new SplitRadixFft();
        this.AA = 170.0d;
        this.DF = 100.0d;
        this.FFTFIRLEN = 65536;
        this.quiet = false;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v1, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v2, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v0, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v3, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v1, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v4, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v2, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v2, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v0, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v3, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v4, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v12, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v5, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v1, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v2, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v6, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v7, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v29, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v0, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v3, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v4, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v1, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v5, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v25, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v8, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v6, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v9, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v7, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v29, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v13, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v8, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v9, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v9, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v10, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v10, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v11, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v11, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v7, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v79, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v45, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v46, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v47, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v62, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: short} */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0128, code lost:
        throw new java.lang.IllegalArgumentException("unrecognized dither type : " + r14[r10 + 1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        r12 = new java.lang.StringBuilder();
        r12.append("unrecognized p.d.f. type : ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0175, code lost:
        r30 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
        r12.append(r14[r10 + 1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0185, code lost:
        throw new java.lang.IllegalArgumentException(r12.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0187, code lost:
        r21 = r13;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    SSRC(java.lang.String[] r79) throws java.io.IOException {
        /*
            r78 = this;
            r15 = r78
            r14 = r79
            java.lang.String r1 = "unrecognized p.d.f. type : "
            r78.<init>()
            java.nio.ByteOrder r0 = java.nio.ByteOrder.LITTLE_ENDIAN
            r15.byteOrder = r0
            im.bclpbkiauv.ui.utils.translate.ssrc.SplitRadixFft r0 = new im.bclpbkiauv.ui.utils.translate.ssrc.SplitRadixFft
            r0.<init>()
            r15.fft = r0
            r2 = 4640185359819341824(0x4065400000000000, double:170.0)
            r15.AA = r2
            r2 = 4636737291354636288(0x4059000000000000, double:100.0)
            r15.DF = r2
            r0 = 65536(0x10000, float:9.18355E-41)
            r15.FFTFIRLEN = r0
            r13 = 0
            r15.quiet = r13
            r0 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r16 = 0
            r17 = 0
            r10 = 0
            r12 = 1
            double[] r5 = new double[r12]
            r6 = 0
            r5[r13] = r6
            r18 = r5
            r5 = -1
            r6 = 0
            r8 = -1
            r9 = 0
            r11 = 0
            r19 = 0
            r20 = 0
            r21 = 4595653203753948938(0x3fc70a3d70a3d70a, double:0.18)
            r23 = 0
            r24 = r10
            r10 = r23
            r22 = r21
            r21 = r20
            r20 = r11
            r11 = r0
            r76 = r6
            r6 = r8
            r7 = r19
            r19 = r9
            r8 = r76
        L_0x005d:
            int r0 = r14.length
            r12 = 2
            if (r10 >= r0) goto L_0x023d
            r0 = r14[r10]
            char r0 = r0.charAt(r13)
            r13 = 45
            if (r0 == r13) goto L_0x006f
            r30 = r2
            goto L_0x023f
        L_0x006f:
            r0 = r14[r10]
            java.lang.String r13 = "--rate"
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x0086
            int r10 = r10 + 1
            r0 = r14[r10]
            int r0 = java.lang.Integer.parseInt(r0)
            r5 = r0
            r30 = r2
            goto L_0x0201
        L_0x0086:
            r0 = r14[r10]
            java.lang.String r13 = "--att"
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x009d
            int r10 = r10 + 1
            r0 = r14[r10]
            float r0 = java.lang.Float.parseFloat(r0)
            double r8 = (double) r0
            r30 = r2
            goto L_0x0201
        L_0x009d:
            r0 = r14[r10]
            java.lang.String r13 = "--bits"
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x00cb
            int r10 = r10 + 1
            r0 = r14[r10]
            int r0 = java.lang.Integer.parseInt(r0)
            r6 = 8
            if (r0 == r6) goto L_0x00c4
            r6 = 16
            if (r0 == r6) goto L_0x00c4
            r6 = 24
            if (r0 != r6) goto L_0x00bc
            goto L_0x00c4
        L_0x00bc:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "Error: Only 8bit, 16bit and 24bit PCM are supported."
            r1.<init>(r6)
            throw r1
        L_0x00c4:
            int r0 = r0 / 8
            r6 = r0
            r30 = r2
            goto L_0x0201
        L_0x00cb:
            r0 = r14[r10]
            java.lang.String r13 = "--twopass"
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x00dc
            r0 = 1
            r19 = r0
            r30 = r2
            goto L_0x0201
        L_0x00dc:
            r0 = r14[r10]
            java.lang.String r13 = "--normalize"
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x00f0
            r0 = 1
            r12 = 1
            r19 = r0
            r30 = r2
            r20 = r12
            goto L_0x0201
        L_0x00f0:
            r0 = r14[r10]
            java.lang.String r13 = "--dither"
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x0130
            int r0 = r10 + 1
            r0 = r14[r0]     // Catch:{ NumberFormatException -> 0x0129 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0129 }
            r7 = r0
            if (r7 < 0) goto L_0x010e
            r0 = 4
            if (r7 > r0) goto L_0x010e
            int r10 = r10 + 1
            r30 = r2
            goto L_0x0201
        L_0x010e:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ NumberFormatException -> 0x0129 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x0129 }
            r12.<init>()     // Catch:{ NumberFormatException -> 0x0129 }
            java.lang.String r13 = "unrecognized dither type : "
            r12.append(r13)     // Catch:{ NumberFormatException -> 0x0129 }
            int r13 = r10 + 1
            r13 = r14[r13]     // Catch:{ NumberFormatException -> 0x0129 }
            r12.append(r13)     // Catch:{ NumberFormatException -> 0x0129 }
            java.lang.String r12 = r12.toString()     // Catch:{ NumberFormatException -> 0x0129 }
            r0.<init>(r12)     // Catch:{ NumberFormatException -> 0x0129 }
            throw r0     // Catch:{ NumberFormatException -> 0x0129 }
        L_0x0129:
            r0 = move-exception
            r0 = -1
            r7 = r0
            r30 = r2
            goto L_0x0201
        L_0x0130:
            r0 = r14[r10]
            java.lang.String r13 = "--pdf"
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x01ac
            int r0 = r10 + 1
            r0 = r14[r0]     // Catch:{ NumberFormatException -> 0x0190 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0190 }
            r13 = r0
            if (r13 < 0) goto L_0x016b
            if (r13 > r12) goto L_0x016b
            int r10 = r10 + 1
            int r0 = r10 + 1
            r0 = r14[r0]     // Catch:{ NumberFormatException -> 0x015e }
            double r26 = java.lang.Double.parseDouble(r0)     // Catch:{ NumberFormatException -> 0x015e }
            r21 = r26
            int r10 = r10 + 1
            r30 = r2
            r22 = r21
            r21 = r13
            goto L_0x0201
        L_0x015e:
            r0 = move-exception
            double[] r12 = presets
            r21 = r12[r13]
            r30 = r2
            r22 = r21
            r21 = r13
            goto L_0x0201
        L_0x016b:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ NumberFormatException -> 0x018a }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x018a }
            r12.<init>()     // Catch:{ NumberFormatException -> 0x018a }
            r12.append(r1)     // Catch:{ NumberFormatException -> 0x018a }
            int r21 = r10 + 1
            r30 = r2
            r2 = r14[r21]     // Catch:{ NumberFormatException -> 0x0186 }
            r12.append(r2)     // Catch:{ NumberFormatException -> 0x0186 }
            java.lang.String r2 = r12.toString()     // Catch:{ NumberFormatException -> 0x0186 }
            r0.<init>(r2)     // Catch:{ NumberFormatException -> 0x0186 }
            throw r0     // Catch:{ NumberFormatException -> 0x0186 }
        L_0x0186:
            r0 = move-exception
            r21 = r13
            goto L_0x0193
        L_0x018a:
            r0 = move-exception
            r30 = r2
            r21 = r13
            goto L_0x0193
        L_0x0190:
            r0 = move-exception
            r30 = r2
        L_0x0193:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r1)
            int r1 = r10 + 1
            r1 = r14[r1]
            r12.append(r1)
            java.lang.String r1 = r12.toString()
            r2.<init>(r1)
            throw r2
        L_0x01ac:
            r30 = r2
            r0 = r14[r10]
            java.lang.String r2 = "--quiet"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x01bc
            r2 = 1
            r15.quiet = r2
            goto L_0x0201
        L_0x01bc:
            r0 = r14[r10]
            java.lang.String r2 = "--tmpfile"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x01cc
            int r10 = r10 + 1
            r0 = r14[r10]
            r11 = r0
            goto L_0x0201
        L_0x01cc:
            r0 = r14[r10]
            java.lang.String r2 = "--profile"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0224
            int r0 = r10 + 1
            r0 = r14[r0]
            java.lang.String r2 = "fast"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x01f2
            r12 = 4636455816377925632(0x4058000000000000, double:96.0)
            r15.AA = r12
            r12 = 4665518107723300864(0x40bf400000000000, double:8000.0)
            r15.DF = r12
            r0 = 1024(0x400, float:1.435E-42)
            r15.FFTFIRLEN = r0
            goto L_0x01fe
        L_0x01f2:
            int r0 = r10 + 1
            r0 = r14[r0]
            java.lang.String r2 = "standard"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0209
        L_0x01fe:
            int r10 = r10 + 1
        L_0x0201:
            r2 = 1
            int r10 = r10 + r2
            r2 = r30
            r12 = 1
            r13 = 0
            goto L_0x005d
        L_0x0209:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "unrecognized profile : "
            r1.append(r2)
            int r2 = r10 + 1
            r2 = r14[r2]
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0224:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "unrecognized option : "
            r1.append(r2)
            r2 = r14[r10]
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x023d:
            r30 = r2
        L_0x023f:
            boolean r0 = r15.quiet
            if (r0 != 0) goto L_0x024d
            java.io.PrintStream r0 = java.lang.System.err
            r1 = 0
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r1 = "Shibatch sampling rate converter version 1.30(high precision/nio)\n\n"
            r0.printf(r1, r2)
        L_0x024d:
            int r0 = r14.length
            int r0 = r0 - r10
            if (r0 != r12) goto L_0x0d9c
            r13 = r14[r10]
            int r0 = r10 + 1
            r2 = r14[r0]
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0d85 }
            r0.<init>(r13)     // Catch:{ IOException -> 0x0d85 }
            r1 = r0
            r0 = 256(0x100, float:3.59E-43)
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocate(r0)
            java.nio.ByteOrder r12 = java.nio.ByteOrder.LITTLE_ENDIAN
            java.nio.ByteBuffer r0 = r0.order(r12)
            r12 = 36
            r0.limit(r12)
            java.nio.channels.FileChannel r12 = r1.getChannel()
            r12.read(r0)
            r0.flip()
            java.io.PrintStream r12 = java.lang.System.err
            r32 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r33 = r4
            java.lang.String r4 = "p: "
            r3.append(r4)
            int r4 = r0.position()
            r3.append(r4)
            java.lang.String r4 = ", l: "
            r3.append(r4)
            int r4 = r0.limit()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r12.println(r3)
            byte r3 = r0.get()
            r4 = 82
            if (r3 == r4) goto L_0x02b0
            r3 = 1
            r15.fmterr(r3)
            goto L_0x02b1
        L_0x02b0:
            r3 = 1
        L_0x02b1:
            byte r4 = r0.get()
            r12 = 73
            if (r4 == r12) goto L_0x02bc
            r15.fmterr(r3)
        L_0x02bc:
            byte r4 = r0.get()
            r12 = 70
            if (r4 == r12) goto L_0x02c7
            r15.fmterr(r3)
        L_0x02c7:
            byte r4 = r0.get()
            if (r4 == r12) goto L_0x02d0
            r15.fmterr(r3)
        L_0x02d0:
            int r3 = r0.getInt()
            byte r4 = r0.get()
            r12 = 87
            if (r4 == r12) goto L_0x02e1
            r4 = 2
            r15.fmterr(r4)
            goto L_0x02e2
        L_0x02e1:
            r4 = 2
        L_0x02e2:
            byte r12 = r0.get()
            r4 = 65
            if (r12 == r4) goto L_0x02ef
            r4 = 2
            r15.fmterr(r4)
            goto L_0x02f0
        L_0x02ef:
            r4 = 2
        L_0x02f0:
            byte r12 = r0.get()
            r4 = 86
            if (r12 == r4) goto L_0x02fd
            r4 = 2
            r15.fmterr(r4)
            goto L_0x02fe
        L_0x02fd:
            r4 = 2
        L_0x02fe:
            byte r12 = r0.get()
            r4 = 69
            if (r12 == r4) goto L_0x030b
            r4 = 2
            r15.fmterr(r4)
            goto L_0x030c
        L_0x030b:
            r4 = 2
        L_0x030c:
            byte r12 = r0.get()
            r4 = 102(0x66, float:1.43E-43)
            if (r12 == r4) goto L_0x0319
            r4 = 2
            r15.fmterr(r4)
            goto L_0x031a
        L_0x0319:
            r4 = 2
        L_0x031a:
            byte r12 = r0.get()
            r4 = 109(0x6d, float:1.53E-43)
            if (r12 == r4) goto L_0x0327
            r4 = 2
            r15.fmterr(r4)
            goto L_0x0328
        L_0x0327:
            r4 = 2
        L_0x0328:
            byte r12 = r0.get()
            r4 = 116(0x74, float:1.63E-43)
            if (r12 == r4) goto L_0x0335
            r12 = 2
            r15.fmterr(r12)
            goto L_0x0336
        L_0x0335:
            r12 = 2
        L_0x0336:
            byte r4 = r0.get()
            r12 = 32
            if (r4 == r12) goto L_0x0342
            r4 = 2
            r15.fmterr(r4)
        L_0x0342:
            int r4 = r0.getInt()
            short r12 = r0.getShort()
            r34 = r3
            r3 = 1
            if (r12 != r3) goto L_0x0d78
            short r12 = r0.getShort()
            int r3 = r0.getInt()
            int r35 = r0.getInt()
            int r36 = r35 % r3
            int r36 = r36 * r12
            if (r36 == 0) goto L_0x0368
            r36 = r10
            r10 = 4
            r15.fmterr(r10)
            goto L_0x036a
        L_0x0368:
            r36 = r10
        L_0x036a:
            short r10 = r0.getShort()
            short r10 = r0.getShort()
            int r37 = r3 * r12
            r38 = r10
            int r10 = r35 / r37
            r35 = r13
            r13 = 16
            if (r4 <= r13) goto L_0x03ad
            r13 = 0
            r0.position(r13)
            r13 = 2
            r0.limit(r13)
            byte[] r13 = r15.getDataFromByteBuffer(r0)
            r1.read(r13)
            r0.flip()
            short r13 = r0.getShort()
            r26 = r4
            java.nio.channels.FileChannel r4 = r1.getChannel()
            java.nio.channels.FileChannel r37 = r1.getChannel()
            long r39 = r37.position()
            r37 = r11
            r41 = r12
            long r11 = (long) r13
            long r11 = r39 + r11
            r4.position(r11)
            goto L_0x03b3
        L_0x03ad:
            r26 = r4
            r37 = r11
            r41 = r12
        L_0x03b3:
            r4 = 0
            r0.position(r4)
            r12 = 8
            r0.limit(r12)
            java.nio.channels.FileChannel r4 = r1.getChannel()
            r4.read(r0)
            r0.flip()
            byte r4 = r0.get()
            byte r11 = r0.get()
            byte r13 = r0.get()
            byte r12 = r0.get()
            int r14 = r0.getInt()
            r39 = r0
            java.io.PrintStream r0 = java.lang.System.err
            r40 = r2
            r42 = r14
            r2 = 4
            java.lang.Object[] r14 = new java.lang.Object[r2]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            r29 = 0
            r14[r29] = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r11)
            r25 = 1
            r14[r25] = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r13)
            r31 = 2
            r14[r31] = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r12)
            r43 = r8
            r8 = 3
            r14[r8] = r2
            java.lang.String r2 = "chunk: %c%c%c%c\n"
            r0.printf(r2, r14)
            r0 = 100
            if (r4 != r0) goto L_0x041a
            r0 = 97
            if (r11 != r0) goto L_0x041a
            r2 = 116(0x74, float:1.63E-43)
            if (r13 != r2) goto L_0x041c
            if (r12 != r0) goto L_0x041c
            goto L_0x0431
        L_0x041a:
            r2 = 116(0x74, float:1.63E-43)
        L_0x041c:
            java.nio.channels.FileChannel r0 = r1.getChannel()
            long r45 = r0.position()
            java.nio.channels.FileChannel r0 = r1.getChannel()
            long r47 = r0.size()
            int r0 = (r45 > r47 ? 1 : (r45 == r47 ? 0 : -1))
            if (r0 != 0) goto L_0x0d36
        L_0x0431:
            java.nio.channels.FileChannel r0 = r1.getChannel()
            long r11 = r0.position()
            java.nio.channels.FileChannel r0 = r1.getChannel()
            long r13 = r0.size()
            int r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0d2e
            r2 = 1
            if (r10 == r2) goto L_0x0459
            r0 = 2
            if (r10 == r0) goto L_0x0459
            if (r10 == r8) goto L_0x0459
            r0 = 4
            if (r10 != r0) goto L_0x0451
            goto L_0x0459
        L_0x0451:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r2 = "Error : Only 8bit, 16bit, 24bit and 32bit PCM are supported."
            r0.<init>(r2)
            throw r0
        L_0x0459:
            r0 = -1
            if (r6 != r0) goto L_0x046c
            r2 = 1
            if (r10 == r2) goto L_0x0462
            r2 = r10
            r6 = r2
            goto L_0x0464
        L_0x0462:
            r2 = 2
            r6 = r2
        L_0x0464:
            r2 = 4
            if (r6 != r2) goto L_0x046a
            r6 = 3
            r14 = r6
            goto L_0x046d
        L_0x046a:
            r14 = r6
            goto L_0x046d
        L_0x046c:
            r14 = r6
        L_0x046d:
            if (r5 != r0) goto L_0x0472
            r5 = r3
            r13 = r5
            goto L_0x0473
        L_0x0472:
            r13 = r5
        L_0x0473:
            if (r7 != r0) goto L_0x0486
            if (r14 >= r10) goto L_0x0482
            r2 = 1
            if (r14 != r2) goto L_0x047e
            r7 = 4
            r26 = r7
            goto L_0x0488
        L_0x047e:
            r7 = 3
            r26 = r7
            goto L_0x0488
        L_0x0482:
            r7 = 1
            r26 = r7
            goto L_0x0488
        L_0x0486:
            r26 = r7
        L_0x0488:
            boolean r0 = r15.quiet
            java.lang.String r12 = "\n"
            if (r0 != 0) goto L_0x054d
            java.lang.String r0 = "none"
            java.lang.String r2 = "no noise shaping"
            java.lang.String r4 = "triangular spectral shape"
            java.lang.String r5 = "ATH based noise shaping"
            java.lang.String r6 = "ATH based noise shaping(less amplitude)"
            java.lang.String[] r0 = new java.lang.String[]{r0, r2, r4, r5, r6}
            java.lang.String r2 = "rectangular"
            java.lang.String r4 = "triangular"
            java.lang.String r5 = "gaussian"
            java.lang.String[] r2 = new java.lang.String[]{r2, r4, r5}
            java.io.PrintStream r4 = java.lang.System.err
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r3)
            r7 = 0
            r6[r7] = r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r13)
            r9 = 1
            r6[r9] = r5
            java.lang.String r5 = "frequency : %d -> %d\n"
            r4.printf(r5, r6)
            java.io.PrintStream r4 = java.lang.System.err
            java.lang.Object[] r5 = new java.lang.Object[r9]
            java.lang.Double r6 = java.lang.Double.valueOf(r43)
            r5[r7] = r6
            java.lang.String r6 = "attenuation : %gdB\n"
            r4.printf(r6, r5)
            java.io.PrintStream r4 = java.lang.System.err
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            int r5 = r10 * 8
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6[r7] = r5
            int r5 = r14 * 8
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r9 = 1
            r6[r9] = r5
            java.lang.String r5 = "bits per sample : %d -> %d\n"
            r4.printf(r5, r6)
            java.io.PrintStream r4 = java.lang.System.err
            java.lang.Object[] r5 = new java.lang.Object[r9]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r41)
            r5[r7] = r6
            java.lang.String r6 = "nchannels : %d\n"
            r4.printf(r6, r5)
            java.io.PrintStream r4 = java.lang.System.err
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r42)
            r6[r7] = r5
            r11 = r42
            double r8 = (double) r11
            r34 = r14
            double r14 = (double) r10
            double r8 = r8 / r14
            r15 = r10
            r14 = r41
            double r10 = (double) r14
            double r8 = r8 / r10
            double r10 = (double) r3
            double r8 = r8 / r10
            java.lang.Double r5 = java.lang.Double.valueOf(r8)
            r7 = 1
            r6[r7] = r5
            java.lang.String r5 = "length : %d bytes, %g secs\n"
            r4.printf(r5, r6)
            if (r26 != 0) goto L_0x0529
            java.io.PrintStream r4 = java.lang.System.err
            r5 = 0
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r7 = "dither type : none\n"
            r4.printf(r7, r6)
            goto L_0x0544
        L_0x0529:
            r5 = 0
            java.io.PrintStream r4 = java.lang.System.err
            r6 = 3
            java.lang.Object[] r7 = new java.lang.Object[r6]
            r6 = r0[r26]
            r7[r5] = r6
            r5 = r2[r21]
            r6 = 1
            r7[r6] = r5
            java.lang.Double r5 = java.lang.Double.valueOf(r22)
            r6 = 2
            r7[r6] = r5
            java.lang.String r5 = "dither type : %s, %s p.d.f, amp = %g\n"
            r4.printf(r5, r7)
        L_0x0544:
            java.io.PrintStream r4 = java.lang.System.err
            r5 = 0
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r4.printf(r12, r6)
            goto L_0x0552
        L_0x054d:
            r15 = r10
            r34 = r14
            r14 = r41
        L_0x0552:
            java.io.File r0 = new java.io.File     // Catch:{ IOException -> 0x0d0c }
            r2 = r40
            r0.<init>(r2)     // Catch:{ IOException -> 0x0cf2 }
            r11 = r0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0cd6 }
            r0.<init>(r11)     // Catch:{ IOException -> 0x0cd6 }
            r10 = r0
            r0 = 44
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocate(r0)
            java.nio.ByteOrder r4 = java.nio.ByteOrder.LITTLE_ENDIAN
            java.nio.ByteBuffer r0 = r0.order(r4)
            java.lang.String r4 = "RIFF"
            byte[] r4 = r4.getBytes()
            r0.put(r4)
            r4 = 0
            r0.putInt(r4)
            java.lang.String r5 = "WAVEfmt "
            byte[] r5 = r5.getBytes()
            r0.put(r5)
            r4 = 16
            r0.putInt(r4)
            r5 = 1
            r0.putShort(r5)
            short r5 = (short) r14
            r0.putShort(r5)
            r4 = r13
            r0.putInt(r4)
            int r6 = r13 * r14
            int r6 = r6 * r34
            r0.putInt(r6)
            int r4 = r34 * r14
            short r4 = (short) r4
            r0.putShort(r4)
            int r5 = r34 * 8
            short r4 = (short) r5
            r0.putShort(r4)
            java.lang.String r5 = "data"
            byte[] r5 = r5.getBytes()
            r0.put(r5)
            r5 = 0
            r0.putInt(r5)
            r0.flip()
            r8 = r78
            r8.writeBuffers(r10, r0)
            if (r26 == 0) goto L_0x0611
            r0 = 0
            r4 = 0
            r9 = r34
            r7 = 1
            if (r9 != r7) goto L_0x05ca
            r0 = -128(0xffffffffffffff80, float:NaN)
            r4 = 127(0x7f, float:1.78E-43)
        L_0x05ca:
            r6 = 2
            if (r9 != r6) goto L_0x05d1
            r0 = -32768(0xffffffffffff8000, float:NaN)
            r4 = 32767(0x7fff, float:4.5916E-41)
        L_0x05d1:
            r5 = 3
            if (r9 != r5) goto L_0x05d9
            r0 = -8388608(0xffffffffff800000, float:-Infinity)
            r4 = 8388607(0x7fffff, float:1.1754942E-38)
        L_0x05d9:
            r25 = r10
            r10 = 4
            if (r9 != r10) goto L_0x05e6
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            r4 = 2147483647(0x7fffffff, float:NaN)
            r27 = r4
            goto L_0x05e8
        L_0x05e6:
            r27 = r4
        L_0x05e8:
            r30 = r1
            r1 = r78
            r4 = r2
            r2 = r13
            r10 = r3
            r3 = r14
            r49 = r4
            r4 = r0
            r32 = 3
            r5 = r27
            r33 = 2
            r6 = r26
            r34 = 1
            r7 = r21
            r39 = r0
            r32 = r9
            r38 = r12
            r50 = r43
            r0 = 3
            r12 = r8
            r8 = r22
            int r1 = r1.init_shaper(r2, r3, r4, r5, r6, r7, r8)
            r9 = r1
            goto L_0x0626
        L_0x0611:
            r30 = r1
            r49 = r2
            r25 = r10
            r38 = r12
            r32 = r34
            r50 = r43
            r0 = 3
            r33 = 2
            r34 = 1
            r10 = r3
            r12 = r8
            r9 = r24
        L_0x0626:
            r7 = 4621819117588971520(0x4024000000000000, double:10.0)
            r39 = 4626322717216342016(0x4034000000000000, double:20.0)
            r5 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            if (r19 == 0) goto L_0x0b5e
            r43 = 0
            r24 = 0
            boolean r1 = r12.quiet
            if (r1 != 0) goto L_0x0641
            java.io.PrintStream r1 = java.lang.System.err
            r4 = 0
            java.lang.Object[] r2 = new java.lang.Object[r4]
            java.lang.String r3 = "Pass 1\n"
            r1.printf(r3, r2)
            goto L_0x0642
        L_0x0641:
            r4 = 0
        L_0x0642:
            if (r37 == 0) goto L_0x067e
            java.io.File r1 = new java.io.File     // Catch:{ IOException -> 0x0665 }
            r2 = r37
            r1.<init>(r2)     // Catch:{ IOException -> 0x064c }
            goto L_0x0688
        L_0x064c:
            r0 = move-exception
            r29 = r2
            r53 = r9
            r67 = r10
            r41 = r11
            r10 = r12
            r66 = r13
            r2 = r14
            r31 = r15
            r14 = r25
            r15 = r32
            r25 = r36
            r68 = r42
            goto L_0x0b56
        L_0x0665:
            r0 = move-exception
            r53 = r9
            r67 = r10
            r41 = r11
            r10 = r12
            r66 = r13
            r2 = r14
            r31 = r15
            r14 = r25
            r15 = r32
            r25 = r36
            r29 = r37
            r68 = r42
            goto L_0x0b56
        L_0x067e:
            r2 = r37
            java.lang.String r1 = "ssrc_"
            java.lang.String r3 = ".tmp"
            java.io.File r1 = java.io.File.createTempFile(r1, r3)     // Catch:{ IOException -> 0x0b3f }
        L_0x0688:
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0b23 }
            r3.<init>(r1)     // Catch:{ IOException -> 0x0b23 }
            if (r20 == 0) goto L_0x0771
            if (r10 >= r13) goto L_0x06e2
            r16 = 8
            r45 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r17 = r42 / r15
            int r17 = r17 / r14
            r27 = r1
            r1 = r78
            r29 = r2
            r2 = r30
            r37 = 0
            r4 = r14
            r5 = r15
            r6 = r16
            r7 = r10
            r8 = r13
            r53 = r9
            r31 = r15
            r52 = r25
            r25 = r36
            r16 = 4
            r15 = r10
            r9 = r45
            r41 = r11
            r36 = r42
            r11 = r17
            r54 = r38
            r0 = 2
            r28 = 4
            r42 = 8
            r12 = r19
            r17 = r13
            r16 = r14
            r14 = 0
            r13 = r26
            double r1 = r1.upsample(r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r13)
            r18[r14] = r1
            r67 = r15
            r2 = r16
            r66 = r17
            r56 = r32
            r68 = r36
            r69 = r50
            r1 = 0
            goto L_0x0857
        L_0x06e2:
            r27 = r1
            r29 = r2
            r53 = r9
            r41 = r11
            r17 = r13
            r16 = r14
            r31 = r15
            r52 = r25
            r25 = r36
            r54 = r38
            r36 = r42
            r0 = 2
            r14 = 0
            r28 = 4
            r42 = 8
            r15 = r10
            r1 = r17
            if (r15 <= r1) goto L_0x073a
            r9 = 8
            r12 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r2 = r36 / r31
            int r2 = r2 / r16
            r4 = r78
            r5 = r30
            r6 = r3
            r7 = r16
            r8 = r31
            r10 = r15
            r11 = r1
            r57 = r1
            r1 = r16
            r56 = r32
            r55 = r36
            r14 = r2
            r2 = r78
            r58 = r15
            r15 = r19
            r16 = r26
            double r4 = r4.downsample(r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16)
            r15 = 0
            r18[r15] = r4
            r2 = r1
            r69 = r50
            r68 = r55
            r66 = r57
            r67 = r58
            r1 = 0
            goto L_0x0857
        L_0x073a:
            r2 = r78
            r57 = r1
            r58 = r15
            r1 = r16
            r56 = r32
            r55 = r36
            r15 = 0
            r9 = 8
            r10 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r14 = r55
            int r4 = r14 / r31
            int r12 = r4 / r1
            r4 = r78
            r5 = r30
            r6 = r3
            r7 = r1
            r8 = r31
            r13 = r19
            r59 = r14
            r14 = r26
            double r4 = r4.no_src(r5, r6, r7, r8, r9, r10, r12, r13, r14)
            r18[r15] = r4
            r2 = r1
            r69 = r50
            r66 = r57
            r67 = r58
            r68 = r59
            r1 = 0
            goto L_0x0857
        L_0x0771:
            r27 = r1
            r29 = r2
            r53 = r9
            r58 = r10
            r41 = r11
            r2 = r12
            r57 = r13
            r1 = r14
            r31 = r15
            r52 = r25
            r56 = r32
            r25 = r36
            r54 = r38
            r59 = r42
            r0 = 2
            r15 = 0
            r28 = 4
            r42 = 8
            r12 = r57
            r14 = r58
            if (r14 >= r12) goto L_0x07da
            r9 = 8
            r10 = r50
            double r4 = -r10
            double r4 = r4 / r39
            r7 = 4621819117588971520(0x4024000000000000, double:10.0)
            double r16 = java.lang.Math.pow(r7, r4)
            r13 = r59
            int r4 = r13 / r31
            int r32 = r4 / r1
            r4 = r78
            r5 = r30
            r6 = r3
            r7 = r1
            r8 = r31
            r34 = r3
            r2 = r10
            r10 = r14
            r11 = r12
            r61 = r12
            r60 = r13
            r12 = r16
            r62 = r14
            r14 = r32
            r15 = r19
            r16 = r26
            double r4 = r4.upsample(r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16)
            r15 = 0
            r18[r15] = r4
            r69 = r2
            r3 = r34
            r68 = r60
            r66 = r61
            r67 = r62
            r2 = r1
            r1 = 0
            goto L_0x0857
        L_0x07da:
            r34 = r3
            r61 = r12
            r62 = r14
            r2 = r50
            r60 = r59
            if (r14 <= r12) goto L_0x0822
            r9 = 8
            double r4 = -r2
            double r4 = r4 / r39
            r10 = 4621819117588971520(0x4024000000000000, double:10.0)
            double r16 = java.lang.Math.pow(r10, r4)
            r13 = r60
            int r4 = r13 / r31
            int r32 = r4 / r1
            r4 = r78
            r5 = r30
            r7 = r2
            r3 = r34
            r6 = r3
            r63 = r7
            r7 = r1
            r8 = r31
            r2 = r1
            r0 = r10
            r10 = r14
            r11 = r12
            r66 = r12
            r65 = r13
            r12 = r16
            r67 = r14
            r14 = r32
            r1 = 0
            r15 = r19
            r16 = r26
            double r4 = r4.downsample(r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16)
            r18[r1] = r4
            r69 = r63
            r68 = r65
            goto L_0x0857
        L_0x0822:
            r63 = r2
            r66 = r12
            r67 = r14
            r3 = r34
            r65 = r60
            r2 = r1
            r1 = 0
            r9 = 8
            r14 = r63
            double r4 = -r14
            double r4 = r4 / r39
            r6 = 4621819117588971520(0x4024000000000000, double:10.0)
            double r10 = java.lang.Math.pow(r6, r4)
            r13 = r65
            int r0 = r13 / r31
            int r12 = r0 / r2
            r4 = r78
            r5 = r30
            r6 = r3
            r7 = r2
            r8 = r31
            r68 = r13
            r13 = r19
            r69 = r14
            r14 = r26
            double r4 = r4.no_src(r5, r6, r7, r8, r9, r10, r12, r13, r14)
            r18[r1] = r4
        L_0x0857:
            r3.close()
            r10 = r78
            boolean r0 = r10.quiet
            if (r0 != 0) goto L_0x0879
            java.io.PrintStream r0 = java.lang.System.err
            r14 = 1
            java.lang.Object[] r4 = new java.lang.Object[r14]
            r5 = r18[r1]
            double r5 = java.lang.Math.log10(r5)
            double r5 = r5 * r39
            java.lang.Double r5 = java.lang.Double.valueOf(r5)
            r4[r1] = r5
            java.lang.String r5 = "\npeak : %gdB\n"
            r0.printf(r5, r4)
            goto L_0x087a
        L_0x0879:
            r14 = 1
        L_0x087a:
            if (r20 != 0) goto L_0x08a1
            r4 = r18[r1]
            r12 = r69
            double r6 = -r12
            double r6 = r6 / r39
            r8 = 4621819117588971520(0x4024000000000000, double:10.0)
            double r6 = java.lang.Math.pow(r8, r6)
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 >= 0) goto L_0x0892
            r5 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r18[r1] = r5
            goto L_0x08b1
        L_0x0892:
            r5 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r15 = r18[r1]
            double r5 = r12 / r39
            double r4 = java.lang.Math.pow(r8, r5)
            double r15 = r15 * r4
            r18[r1] = r15
            goto L_0x08b1
        L_0x08a1:
            r12 = r69
            r8 = 4621819117588971520(0x4024000000000000, double:10.0)
            r4 = r18[r1]
            double r6 = r12 / r39
            double r6 = java.lang.Math.pow(r8, r6)
            double r4 = r4 * r6
            r18[r1] = r4
        L_0x08b1:
            boolean r0 = r10.quiet
            if (r0 != 0) goto L_0x08be
            java.io.PrintStream r0 = java.lang.System.err
            java.lang.Object[] r4 = new java.lang.Object[r1]
            java.lang.String r5 = "\nPass 2\n"
            r0.printf(r5, r4)
        L_0x08be:
            r4 = 4710765209155796992(0x415fffffc0000000, double:8388607.0)
            r6 = 4674736138332667904(0x40dfffc000000000, double:32767.0)
            r8 = 4638637247447433216(0x405fc00000000000, double:127.0)
            if (r26 == 0) goto L_0x095e
            r15 = r56
            if (r15 == r14) goto L_0x0933
            r0 = 2
            if (r15 == r0) goto L_0x090b
            r0 = 3
            if (r15 == r0) goto L_0x08dd
            r11 = r53
            goto L_0x095b
        L_0x08dd:
            if (r20 != 0) goto L_0x08f9
            r6 = r18[r1]
            r0 = 8388607(0x7fffff, float:1.1754942E-38)
            r11 = r53
            int r0 = r0 - r11
            double r8 = (double) r0
            double r8 = r8 / r4
            int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r0 < 0) goto L_0x08f0
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x08fd
        L_0x08f0:
            r6 = r18[r1]
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r6 = r8 / r6
            double r6 = r6 * r4
            goto L_0x0908
        L_0x08f9:
            r11 = r53
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
        L_0x08fd:
            r4 = r18[r1]
            double r5 = r8 / r4
            r0 = 8388607(0x7fffff, float:1.1754942E-38)
            int r0 = r0 - r11
            double r7 = (double) r0
            double r6 = r5 * r7
        L_0x0908:
            r43 = r6
            goto L_0x095b
        L_0x090b:
            r11 = r53
            if (r20 != 0) goto L_0x0925
            r4 = r18[r1]
            int r0 = 32767 - r11
            double r8 = (double) r0
            double r8 = r8 / r6
            int r0 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r0 < 0) goto L_0x091c
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0927
        L_0x091c:
            r4 = r18[r1]
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r4 = r8 / r4
            double r4 = r4 * r6
            goto L_0x0930
        L_0x0925:
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
        L_0x0927:
            r4 = r18[r1]
            double r5 = r8 / r4
            int r0 = 32767 - r11
            double r7 = (double) r0
            double r4 = r5 * r7
        L_0x0930:
            r43 = r4
            goto L_0x095b
        L_0x0933:
            r11 = r53
            if (r20 != 0) goto L_0x094d
            r4 = r18[r1]
            int r0 = 127 - r11
            double r6 = (double) r0
            double r6 = r6 / r8
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 < 0) goto L_0x0944
            r6 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x094f
        L_0x0944:
            r4 = r18[r1]
            r6 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r4 = r6 / r4
            double r4 = r4 * r8
            goto L_0x0958
        L_0x094d:
            r6 = 4607182418800017408(0x3ff0000000000000, double:1.0)
        L_0x094f:
            r4 = r18[r1]
            double r4 = r6 / r4
            int r0 = 127 - r11
            double r6 = (double) r0
            double r4 = r4 * r6
        L_0x0958:
            r43 = r4
        L_0x095b:
            r16 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0988
        L_0x095e:
            r11 = r53
            r15 = r56
            if (r15 == r14) goto L_0x097f
            r0 = 2
            if (r15 == r0) goto L_0x0976
            r0 = 3
            if (r15 == r0) goto L_0x096d
            r16 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x0988
        L_0x096d:
            r6 = r18[r1]
            r16 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r6 = r16 / r6
            double r43 = r6 * r4
            goto L_0x0988
        L_0x0976:
            r16 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r4 = r18[r1]
            double r4 = r16 / r4
            double r43 = r4 * r6
            goto L_0x0988
        L_0x097f:
            r16 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r4 = r18[r1]
            double r5 = r16 / r4
            double r43 = r5 * r8
        L_0x0988:
            r10.randptr = r1
            r78.setstarttime()
            long r4 = r27.length()
            r6 = 8
            long r4 = r4 / r6
            int r0 = (int) r4
            java.io.FileInputStream r4 = new java.io.FileInputStream
            r9 = r27
            r4.<init>(r9)
            java.nio.channels.FileChannel r8 = r4.getChannel()
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r42)
            r4 = 0
        L_0x09a5:
            if (r4 >= r0) goto L_0x0acf
            r7.clear()
            r8.read(r7)
            r7.flip()
            double r5 = r7.getDouble()
            double r5 = r5 * r43
            int r4 = r4 + 1
            if (r15 == r14) goto L_0x0a6e
            r1 = 2
            if (r15 == r1) goto L_0x0a2b
            r1 = 3
            if (r15 == r1) goto L_0x09d4
            r1 = r4
            r16 = r7
            r17 = r8
            r27 = r9
            r53 = r11
            r50 = r12
            r14 = r52
            r32 = 3
            r36 = 2
            r11 = r5
            goto L_0x0aa6
        L_0x09d4:
            if (r26 == 0) goto L_0x09ee
            r1 = r4
            r4 = r78
            r45 = r5
            r16 = r7
            r7 = r18
            r17 = r8
            r8 = r26
            r27 = r9
            r9 = r24
            int r4 = r4.do_shaping(r5, r7, r8, r9)
            r8 = r45
            goto L_0x09fd
        L_0x09ee:
            r1 = r4
            r45 = r5
            r16 = r7
            r17 = r8
            r27 = r9
            r8 = r45
            int r4 = r10.RINT(r8)
        L_0x09fd:
            r32 = 3
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocate(r32)
            r6 = r4 & 255(0xff, float:3.57E-43)
            byte r6 = (byte) r6
            r5.put(r6)
            int r4 = r4 >> 8
            r6 = r4 & 255(0xff, float:3.57E-43)
            byte r6 = (byte) r6
            r5.put(r6)
            int r4 = r4 >> 8
            r6 = r4 & 255(0xff, float:3.57E-43)
            byte r6 = (byte) r6
            r5.put(r6)
            r5.flip()
            r7 = r52
            r10.writeBuffers(r7, r5)
            r14 = r7
            r53 = r11
            r50 = r12
            r36 = 2
            r11 = r8
            goto L_0x0aa6
        L_0x0a2b:
            r1 = r4
            r16 = r7
            r17 = r8
            r27 = r9
            r7 = r52
            r32 = 3
            r8 = r5
            if (r26 == 0) goto L_0x0a4d
            r4 = r78
            r5 = r8
            r14 = r7
            r7 = r18
            r53 = r11
            r50 = r12
            r11 = r8
            r8 = r26
            r9 = r24
            int r4 = r4.do_shaping(r5, r7, r8, r9)
            goto L_0x0a57
        L_0x0a4d:
            r14 = r7
            r53 = r11
            r50 = r12
            r11 = r8
            int r4 = r10.RINT(r11)
        L_0x0a57:
            r36 = 2
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocate(r36)
            java.nio.ByteOrder r6 = java.nio.ByteOrder.LITTLE_ENDIAN
            java.nio.ByteBuffer r5 = r5.order(r6)
            short r6 = (short) r4
            r5.putShort(r6)
            r5.flip()
            r10.writeBuffers(r14, r5)
            goto L_0x0aa6
        L_0x0a6e:
            r1 = r4
            r16 = r7
            r17 = r8
            r27 = r9
            r53 = r11
            r50 = r12
            r14 = r52
            r32 = 3
            r36 = 2
            r11 = r5
            if (r26 == 0) goto L_0x0a90
            r4 = r78
            r5 = r11
            r7 = r18
            r8 = r26
            r9 = r24
            int r4 = r4.do_shaping(r5, r7, r8, r9)
            goto L_0x0a94
        L_0x0a90:
            int r4 = r10.RINT(r11)
        L_0x0a94:
            r5 = 1
            java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocate(r5)
            int r5 = r4 + 128
            byte r5 = (byte) r5
            r6.put(r5)
            r6.flip()
            r10.writeBuffers(r14, r6)
        L_0x0aa6:
            int r4 = r24 + 1
            if (r4 != r2) goto L_0x0aae
            r4 = 0
            r24 = r4
            goto L_0x0ab0
        L_0x0aae:
            r24 = r4
        L_0x0ab0:
            r4 = 262143(0x3ffff, float:3.6734E-40)
            r4 = r4 & r1
            if (r4 != 0) goto L_0x0abc
            double r4 = (double) r1
            double r6 = (double) r0
            double r4 = r4 / r6
            r10.showprogress(r4)
        L_0x0abc:
            r4 = r1
            r52 = r14
            r7 = r16
            r8 = r17
            r9 = r27
            r12 = r50
            r11 = r53
            r1 = 0
            r14 = 1
            r16 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            goto L_0x09a5
        L_0x0acf:
            r16 = r7
            r17 = r8
            r27 = r9
            r53 = r11
            r50 = r12
            r14 = r52
            r12 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r10.showprogress(r12)
            boolean r1 = r10.quiet
            if (r1 != 0) goto L_0x0aee
            java.io.PrintStream r1 = java.lang.System.err
            r5 = 0
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r11 = r54
            r1.printf(r11, r6)
        L_0x0aee:
            r17.close()
            if (r27 == 0) goto L_0x0b09
            boolean r1 = r27.delete()
            if (r1 != 0) goto L_0x0b07
            java.io.PrintStream r1 = java.lang.System.err
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r5 = 0
            r6[r5] = r27
            java.lang.String r7 = "Failed to remove %s\n"
            r1.printf(r7, r6)
            goto L_0x0b0a
        L_0x0b07:
            r5 = 0
            goto L_0x0b0a
        L_0x0b09:
            r5 = 0
        L_0x0b0a:
            r17 = r3
            r1 = r10
            r32 = r12
            r52 = r14
            r56 = r15
            r16 = r27
            r12 = r50
            r24 = r53
            r27 = r66
            r58 = r67
            r55 = r68
            r14 = 0
            r15 = r2
            goto L_0x0c5e
        L_0x0b23:
            r0 = move-exception
            r27 = r1
            r29 = r2
            r53 = r9
            r67 = r10
            r41 = r11
            r10 = r12
            r66 = r13
            r2 = r14
            r31 = r15
            r14 = r25
            r15 = r32
            r25 = r36
            r68 = r42
            r16 = r27
            goto L_0x0b56
        L_0x0b3f:
            r0 = move-exception
            r29 = r2
            r53 = r9
            r67 = r10
            r41 = r11
            r10 = r12
            r66 = r13
            r2 = r14
            r31 = r15
            r14 = r25
            r15 = r32
            r25 = r36
            r68 = r42
        L_0x0b56:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r3 = "cannot open temporary file."
            r1.<init>(r3)
            throw r1
        L_0x0b5e:
            r53 = r9
            r67 = r10
            r41 = r11
            r10 = r12
            r66 = r13
            r2 = r14
            r31 = r15
            r14 = r25
            r15 = r32
            r25 = r36
            r29 = r37
            r11 = r38
            r68 = r42
            r28 = 4
            r12 = r5
            r5 = 0
            r8 = r66
            r9 = r67
            if (r9 >= r8) goto L_0x0bcb
            r6 = r50
            double r0 = -r6
            double r0 = r0 / r39
            r3 = 4621819117588971520(0x4024000000000000, double:10.0)
            double r32 = java.lang.Math.pow(r3, r0)
            r4 = r68
            int r0 = r4 / r31
            int r0 = r0 / r2
            r5 = r2
            r3 = 0
            r1 = r78
            r2 = r30
            r3 = r14
            r52 = r14
            r14 = r4
            r4 = r5
            r71 = r5
            r5 = r31
            r36 = r6
            r6 = r15
            r7 = r9
            r57 = r8
            r56 = r15
            r15 = r9
            r9 = r32
            r72 = r11
            r24 = r53
            r11 = r0
            r32 = r12
            r73 = r36
            r12 = r19
            r13 = r26
            double r0 = r1.upsample(r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r13)
            r13 = 0
            r18[r13] = r0
            r55 = r14
            r58 = r15
            r27 = r57
            r15 = r71
            r12 = r73
            r14 = 0
            goto L_0x0c4f
        L_0x0bcb:
            r71 = r2
            r57 = r8
            r72 = r11
            r32 = r12
            r52 = r14
            r56 = r15
            r73 = r50
            r24 = r53
            r14 = r68
            r13 = 0
            r15 = r9
            r12 = r57
            if (r15 <= r12) goto L_0x0c1e
            r9 = r73
            double r0 = -r9
            double r0 = r0 / r39
            r2 = 4621819117588971520(0x4024000000000000, double:10.0)
            double r36 = java.lang.Math.pow(r2, r0)
            int r0 = r14 / r31
            r11 = r71
            int r0 = r0 / r11
            r1 = r78
            r2 = r30
            r3 = r52
            r4 = r11
            r5 = r31
            r6 = r56
            r7 = r15
            r8 = r12
            r55 = r14
            r58 = r15
            r14 = r9
            r9 = r36
            r75 = r11
            r11 = r0
            r27 = r12
            r12 = r19
            r50 = r14
            r14 = 0
            r13 = r26
            double r0 = r1.downsample(r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r13)
            r18[r14] = r0
            r12 = r50
            r15 = r75
            goto L_0x0c4f
        L_0x0c1e:
            r27 = r12
            r55 = r14
            r58 = r15
            r75 = r71
            r50 = r73
            r14 = 0
            r12 = r50
            double r0 = -r12
            double r0 = r0 / r39
            r2 = 4621819117588971520(0x4024000000000000, double:10.0)
            double r7 = java.lang.Math.pow(r2, r0)
            int r0 = r55 / r31
            r15 = r75
            int r9 = r0 / r15
            r1 = r78
            r2 = r30
            r3 = r52
            r4 = r15
            r5 = r31
            r6 = r56
            r10 = r19
            r11 = r26
            double r0 = r1.no_src(r2, r3, r4, r5, r6, r7, r9, r10, r11)
            r18[r14] = r0
        L_0x0c4f:
            r1 = r78
            boolean r0 = r1.quiet
            if (r0 != 0) goto L_0x0c5e
            java.io.PrintStream r0 = java.lang.System.err
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r3 = r72
            r0.printf(r3, r2)
        L_0x0c5e:
            if (r26 == 0) goto L_0x0c63
            r1.quit_shaper(r15)
        L_0x0c63:
            if (r19 != 0) goto L_0x0c87
            r2 = r18[r14]
            int r0 = (r2 > r32 ? 1 : (r2 == r32 ? 0 : -1))
            if (r0 <= 0) goto L_0x0c87
            boolean r0 = r1.quiet
            if (r0 != 0) goto L_0x0c87
            java.io.PrintStream r0 = java.lang.System.err
            r3 = 1
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r3 = r18[r14]
            double r3 = java.lang.Math.log10(r3)
            double r3 = r3 * r39
            java.lang.Double r3 = java.lang.Double.valueOf(r3)
            r2[r14] = r3
            java.lang.String r3 = "clipping detected : %gdB\n"
            r0.printf(r3, r2)
        L_0x0c87:
            r52.close()
            java.io.File r0 = new java.io.File
            r8 = r49
            r0.<init>(r8)
            long r2 = r0.length()
            int r3 = (int) r2
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile
            java.lang.String r4 = "rw"
            r2.<init>(r0, r4)
            java.nio.channels.FileChannel r2 = r2.getChannel()
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r28)
            java.nio.ByteOrder r5 = java.nio.ByteOrder.LITTLE_ENDIAN
            java.nio.ByteBuffer r4 = r4.order(r5)
            int r5 = r3 + -8
            r4.position(r14)
            r9 = 4
            r4.limit(r9)
            r4.putInt(r5)
            r4.flip()
            r6 = 4
            r2.write(r4, r6)
            int r5 = r3 + -44
            r4.position(r14)
            r4.limit(r9)
            r4.putInt(r5)
            r4.flip()
            r6 = 40
            r2.write(r4, r6)
            r2.close()
            return
        L_0x0cd6:
            r0 = move-exception
            r30 = r1
            r8 = r2
            r58 = r3
            r41 = r11
            r27 = r13
            r31 = r15
            r56 = r34
            r25 = r36
            r29 = r37
            r55 = r42
            r12 = r43
            r1 = r78
            r15 = r14
            r3 = r41
            goto L_0x0d26
        L_0x0cf2:
            r0 = move-exception
            r30 = r1
            r8 = r2
            r58 = r3
            r27 = r13
            r31 = r15
            r56 = r34
            r25 = r36
            r29 = r37
            r55 = r42
            r12 = r43
            r1 = r78
            r15 = r14
            r3 = r32
            goto L_0x0d26
        L_0x0d0c:
            r0 = move-exception
            r30 = r1
            r58 = r3
            r27 = r13
            r31 = r15
            r56 = r34
            r25 = r36
            r29 = r37
            r8 = r40
            r55 = r42
            r12 = r43
            r1 = r78
            r15 = r14
            r3 = r32
        L_0x0d26:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r4 = "cannot open output file."
            r2.<init>(r4)
            throw r2
        L_0x0d2e:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r2 = "Couldn't find data chank"
            r0.<init>(r2)
            throw r0
        L_0x0d36:
            r30 = r1
            r58 = r3
            r31 = r10
            r1 = r15
            r25 = r36
            r29 = r37
            r8 = r40
            r15 = r41
            r55 = r42
            r27 = r43
            r3 = 1
            r9 = 4
            r14 = 0
            r36 = 2
            r42 = 8
            java.nio.channels.FileChannel r0 = r30.getChannel()
            java.nio.channels.FileChannel r10 = r30.getChannel()
            long r40 = r10.position()
            r10 = r55
            long r2 = (long) r10
            long r2 = r40 + r2
            r0.position(r2)
            r14 = r79
            r2 = r8
            r41 = r15
            r36 = r25
            r8 = r27
            r10 = r31
            r0 = r39
            r3 = r58
            r15 = r1
            r1 = r30
            goto L_0x03b3
        L_0x0d78:
            r39 = r0
            r27 = r8
            r8 = r2
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r2 = "Error: Only PCM is supported."
            r0.<init>(r2)
            throw r0
        L_0x0d85:
            r0 = move-exception
            r32 = r3
            r33 = r4
            r27 = r8
            r25 = r10
            r29 = r11
            r35 = r13
            r1 = r15
            r8 = r2
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "cannot open input file."
            r2.<init>(r3)
            throw r2
        L_0x0d9c:
            r1 = r15
            r78.usage()
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r2 = "too few arguments"
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.utils.translate.ssrc.SSRC.<init>(java.lang.String[]):void");
    }

    public SSRC(InputStream fpi, OutputStream fpo, int sfrq, int dfrq, int bps, int dbps, int nch, int length, double att, int dither, boolean quiet_) throws IOException {
        int dither2;
        int dfrq2;
        String str;
        int dbps2;
        char c;
        int dfrq3;
        int max;
        int min;
        int i = sfrq;
        int i2 = bps;
        int i3 = nch;
        int i4 = length;
        double d = att;
        int dither3 = dither;
        this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        this.fft = new SplitRadixFft();
        this.AA = 170.0d;
        this.DF = 100.0d;
        this.FFTFIRLEN = 65536;
        this.quiet = false;
        double[] peak = {0.0d};
        if (dither3 < 0 || dither3 > 4) {
            int i5 = dfrq;
            int i6 = dbps;
            int i7 = i3;
            double d2 = d;
            throw new IllegalArgumentException("unrecognized dither type : " + dither3);
        }
        this.quiet = quiet_;
        if (i2 == 1 || i2 == 2 || i2 == 3 || i2 == 4) {
            int dbps3 = dbps;
            if (dbps3 == -1) {
                if (i2 != 1) {
                    dbps3 = bps;
                } else {
                    dbps3 = 2;
                }
                if (dbps3 == 4) {
                    dbps3 = 3;
                }
            }
            int dfrq4 = dfrq;
            dfrq4 = dfrq4 == -1 ? sfrq : dfrq4;
            if (dither3 != -1) {
                dither2 = dither3;
            } else if (dbps3 >= i2) {
                dither2 = 1;
            } else if (dbps3 == 1) {
                dither2 = 4;
            } else {
                dither2 = 3;
            }
            if (!this.quiet) {
                String[] dtype = {SchedulerSupport.NONE, "no noise shaping", "triangular spectral shape", "ATH based noise shaping", "ATH based noise shaping(less amplitude)"};
                String[] ptype = {"rectangular", "triangular", "gaussian"};
                dfrq2 = dfrq4;
                System.err.printf("frequency : %d -> %d\n", new Object[]{Integer.valueOf(sfrq), Integer.valueOf(dfrq4)});
                System.err.printf("attenuation : %gdB\n", new Object[]{Double.valueOf(att)});
                System.err.printf("bits per sample : %d -> %d\n", new Object[]{Integer.valueOf(i2 * 8), Integer.valueOf(dbps3 * 8)});
                System.err.printf("nchannels : %d\n", new Object[]{Integer.valueOf(nch)});
                System.err.printf("length : %d bytes, %g secs\n", new Object[]{Integer.valueOf(length), Double.valueOf(((((double) i4) / ((double) i2)) / ((double) i3)) / ((double) i))});
                if (dither2 == 0) {
                    System.err.printf("dither type : none\n", new Object[0]);
                } else {
                    System.err.printf("dither type : %s, %s p.d.f, amp = %g\n", new Object[]{dtype[dither2], ptype[0], Double.valueOf(0.18d)});
                }
                System.err.printf("\n", new Object[0]);
            } else {
                dfrq2 = dfrq4;
            }
            if (dither2 != 0) {
                int min2 = 0;
                int max2 = 0;
                if (dbps3 == 1) {
                    min2 = -128;
                    max2 = 127;
                }
                if (dbps3 == 2) {
                    min2 = -32768;
                    max2 = 32767;
                }
                if (dbps3 == 3) {
                    min2 = -8388608;
                    max2 = 8388607;
                }
                if (dbps3 == 4) {
                    min = Integer.MIN_VALUE;
                    max = Integer.MAX_VALUE;
                } else {
                    min = min2;
                    max = max2;
                }
                dfrq3 = dfrq2;
                str = "\n";
                dbps2 = dbps3;
                c = 0;
                int samp2 = init_shaper(dfrq3, nch, min, max, dither2, 0, 0.18d);
            } else {
                dfrq3 = dfrq2;
                str = "\n";
                dbps2 = dbps3;
                c = 0;
            }
            if (i < dfrq3) {
                int dfrq5 = dfrq3;
                peak[c] = upsample(fpi, fpo, nch, bps, dbps2, sfrq, dfrq3, Math.pow(10.0d, (-att) / 20.0d), (length / i2) / i3, false, dither2);
                double d3 = att;
                int i8 = dfrq5;
            } else {
                int dfrq6 = dfrq3;
                if (i > dfrq6) {
                    int i9 = dfrq6;
                    peak[c] = downsample(fpi, fpo, nch, bps, dbps2, sfrq, dfrq6, Math.pow(10.0d, (-att) / 20.0d), (length / i2) / nch, false, dither2);
                    double d4 = att;
                } else {
                    peak[c] = no_src(fpi, fpo, nch, bps, dbps2, Math.pow(10.0d, (-att) / 20.0d), (length / i2) / nch, false, dither2);
                }
            }
            if (!this.quiet) {
                System.err.printf(str, new Object[0]);
            }
            if (dither2 != 0) {
                quit_shaper(nch);
            } else {
                int i10 = nch;
            }
            if (0 == 0 && peak[0] > 1.0d && !this.quiet) {
                System.err.printf("clipping detected : %gdB\n", new Object[]{Double.valueOf(Math.log10(peak[0]) * 20.0d)});
                return;
            }
            return;
        }
        throw new IllegalStateException("Error : Only 8bit, 16bit, 24bit and 32bit PCM are supported.");
    }

    /* access modifiers changed from: protected */
    public byte[] getDataFromByteBuffer(ByteBuffer rawoutbuf) {
        byte[] tempDataWrt = new byte[(rawoutbuf.limit() - rawoutbuf.position())];
        rawoutbuf.get(tempDataWrt, 0, tempDataWrt.length);
        return tempDataWrt;
    }

    /* access modifiers changed from: protected */
    public void writeBuffers(OutputStream fpo, ByteBuffer rawoutbuf) {
        try {
            fpo.write(getDataFromByteBuffer(rawoutbuf));
        } catch (IOException e) {
        }
    }
}
