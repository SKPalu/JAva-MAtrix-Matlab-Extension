package jamaextension.jamax.vnijmsl;

/**
 *
 * @author Feynman Perceptrons
 */
import java.io.Serializable;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

public class Covariances implements Serializable, Cloneable
{
    /*
     * public static class DiffObsDeletedException extends IMSLException {
     * 
     * static final long serialVersionUID = 0xb41c32cee2f825c5L;
     * 
     * public DiffObsDeletedException(String s) { super(s); }
     * 
     * public DiffObsDeletedException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class MoreObsDelThanEnteredException extends IMSLException
     * {
     * 
     * static final long serialVersionUID = 0x4ac2ee92560f2071L;
     * 
     * public MoreObsDelThanEnteredException(String s) { super(s); }
     * 
     * public MoreObsDelThanEnteredException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class TooManyObsDeletedException extends IMSLException {
     * 
     * static final long serialVersionUID = 0xc15146ae4a1bfa22L;
     * 
     * public TooManyObsDeletedException(String s) { super(s); }
     * 
     * public TooManyObsDeletedException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class NonnegativeWeightException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x95b3053513afdb9dL;
     * 
     * public NonnegativeWeightException(String s) { super(s); }
     * 
     * public NonnegativeWeightException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * public static class NonnegativeFreqException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x494fdc61e0c666cfL;
     * 
     * public NonnegativeFreqException(String s) { super(s); }
     * 
     * public NonnegativeFreqException(String s, Object aobj[]) {
     * super("com.imsl.stat", s, aobj); } }
     * 
     * private static final int check = Messages.check(1);
     */

    static final long serialVersionUID = 0xe69abd1fec8d8c85L;

    /*
     * public static final int VARIANCE_COVARIANCE_MATRIX = 0; public static
     * final int CORRECTED_SSCP_MATRIX = 1; public static final int
     * CORRELATION_MATRIX = 2; public static final int STDEV_CORRELATION_MATRIX
     * = 3;
     */
    private int l_ipairm;
    private int l_ipairv;
    private int l_ipairw;
    private int l_ncol;
    private int l_nvar2;
    private int l_nvarsq;
    private int l_nrows;
    private int l_nvariables;
    private int l_mopt;
    private int l_nobs;
    private int l_ifreq;
    private int l_user_f;
    private int l_iwt;
    private int l_user_w;
    private int l_nrmiss;
    private double l_x[];
    private double l_means[];
    private double l_freq[];
    private double l_wt[];
    private int l_incd[];
    private double l_sumwt;

    public Covariances(double ad[][])
    {
        new Matrix(ad);// Matrix.checkMatrix(ad);
        l_nrows = ad.length;
        if (l_nrows <= 0)
        {
            Object aobj[] =
            {
                    "x.length", new Integer(ad.length)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        l_nvariables = ad[0].length;
        if (l_nvariables <= 0)
        {
            Object aobj1[] =
            {
                    "x[0].length", new Integer(ad[0].length)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotPositive", aobj1);
            throw new IllegalArgumentException("NotPositive");
        }
        l_x = new double[l_nrows * l_nvariables];
        for (int i = 0; i < l_nrows; i++)
        {
            System.arraycopy(ad[i], 0, l_x, i * l_nvariables, l_nvariables);
        }

    }

    public final Matrix compute(int i)
    { // throws NonnegativeFreqException, NonnegativeWeightException,
      // TooManyObsDeletedException, MoreObsDelThanEnteredException,
      // DiffObsDeletedException

        int j = l_nrows;
        int k = l_nvariables;
        double ad[] = l_x;
        int l = k;
        int i1 = k;
        int j1 = 0;
        double ad1[] = null;
        int k1 = 0;
        boolean flag = false;
        boolean flag1 = false;
        double ad2[] = null;
        double ad3[] =
        {
            0.0D
        };
        int ai[] = null;
        int ai1[] =
        {
            0
        };
        int ai2[] =
        {
            0
        };
        int l1 = 1;
        boolean flag2 = false;
        int i2 = l_iwt;
        int k2 = l_ifreq;
        double ad4[] = l_wt;
        double ad5[] = l_freq;
        boolean flag3 = false;
        Object obj = null;
        int l3 = l_mopt;
        int i4 = l_user_w;
        int j4 = l_user_f;
        double ad7[] = null;
        l_ipairm = l_ipairv = l_ipairw = l_ncol = l_nvar2 = l_nvarsq = 0;
        if (i < 0 || i > 3)
        {
            Object aobj[] =
            {
                    "matrixType", new Integer(i), new Integer(0), new Integer(3)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "OutOfRange", aobj);
            throw new IllegalArgumentException("OutOfRange");
        }
        j1 = i;
        k1++;
        if (l3 != 0)
        {
            l1 = k;
        }
        if (k2 != 0 && ad5 == null)
        {
            k2 = 0;
        }
        if (i2 != 0 && ad4 == null)
        {
            i2 = 0;
        }
        if (!flag2)
        {
            if (l3 == 0)
            {
                ai = new int[1];
            }
            else
            {
                ai = new int[k * k];
            }
        }
        if (i2 != 0)
        {
            switch (l3)
            {
            case 1: // '\001'
            case 2: // '\002'
                ad2 = new double[(k * (3 * k + 5)) / 2];
                break;

            case 3: // '\003'
                ad2 = new double[(5 * k * (k + 1)) / 2];
                break;

            default:
                ad2 = new double[4 * k];
                break;
            }
        }
        else
        {
            switch (l3)
            {
            case 1: // '\001'
            case 2: // '\002'
                ad2 = new double[k * (k + 2)];
                break;

            case 3: // '\003'
                ad2 = new double[2 * k * (k + 1)];
                break;

            default:
                ad2 = new double[3 * k];
                break;
            }
        }
        if (!flag1)
        {
            ad7 = new double[k * i1];
        }
        if (!flag)
        {
            ad1 = new double[k];
        }
        if (j4 != 0 || i4 != 0)
        {
            int i3 = k;
            int l2 = i3 + 1;
            int j2 = ++i3 + 1;
            double ad6[] = new double[++i3];
            l_c2rvc(1, 0, k, ad6, 1, l2, j2, l3, j1, ad1, ad7, i1, ai, l1, ai1, ai2, ad3, ad2);
            for (int j3 = 0; j3 < j; j3++)
            {
                System.arraycopy(ad, j3 * l, ad6, 0, k);
                if (j4 != 0)
                {
                    ad6[l2 - 1] = ad5[j3];
                }
                else
                {
                    ad6[l2 - 1] = 1.0D;
                }
                if (i4 != 0)
                {
                    ad6[j2 - 1] = ad4[j3];
                }
                else
                {
                    ad6[j2 - 1] = 1.0D;
                }
                l_c2rvc(2, 1, k, ad6, 1, l2, j2, l3, j1, ad1, ad7, i1, ai, l1, ai1, ai2, ad3, ad2);
            }

            l_c2rvc(3, 0, k, ad6, 1, l2, j2, l3, j1, ad1, ad7, i1, ai, l1, ai1, ai2, ad3, ad2);
            ad6 = null;
        }
        else
        {
            l_m1ran(j, l, ad, ad);
            l_c2rvc(0, j, k, ad, j, 0, 0, l3, j1, ad1, ad7, i1, ai, l1, ai1, ai2, ad3, ad2);
            l_m1ran(l, j, ad, ad);
        }
        if (!flag)
        {
            l_means = (double[]) ad1.clone();
        }
        if (!flag2)
        {
            l_incd = (int[]) ai.clone();
        }
        l_nobs = ai1[0];
        l_nrmiss = ai2[0];
        l_sumwt = ad3[0];
        double ad8[][] = new double[k][k];
        for (int k3 = 0; k3 < k; k3++)
        {
            System.arraycopy(ad7, k3 * k, ad8[k3], 0, k);
        }

        return new Matrix(ad8);
    }

    private void l_m1ran(int i, int j, double ad[], double ad1[])
    {
        boolean flag = false;
        int k1 = i * j - 1;
        if (i != j)
        {
            flag = true;
        }
        if (!flag)
        {
            if (ad1 != ad)
            {
                BLAS.copy(i * j, ad, ad1);
            }
            int l1 = 2;
            int i2 = i + 1;
            int j2 = i - 1;
            for (int k = i; k <= k1; k += i)
            {
                int k2 = l1 + j2;
                for (int i1 = l1; i1 <= k; i1++)
                {
                    double d = ad1[i1 - 1];
                    ad1[i1 - 1] = ad1[k2 - 1];
                    ad1[k2 - 1] = d;
                    k2 += i;
                }

                l1 += i2;
            }

            return;
        }
        double ad2[];
        if (ad == ad1)
        {
            ad2 = new double[j * i];
        }
        else
        {
            ad2 = ad1;
        }
        for (int l = 0; l < i; l++)
        {
            for (int j1 = 0; j1 < j; j1++)
            {
                ad2[l + i * j1] = ad[j1 + j * l];
            }

        }

        if (ad == ad1)
        {
            BLAS.copy(i * j, ad2, ad1);
        }
    }

    private void l_c2rvc(int i, int j, int k, double ad[], int l, int i1, int j1, int k1, int l1, double ad1[],
            double ad2[], int i2, int ai[], int j2, int ai1[], int ai2[], double ad3[], double ad4[]) // throws
                                                                                                      // NonnegativeFreqException,
                                                                                                      // NonnegativeWeightException,
                                                                                                      // TooManyObsDeletedException,
                                                                                                      // MoreObsDelThanEnteredException,
                                                                                                      // DiffObsDeletedException
    {
        int i6 = 1;
        int ai3[] =
        {
            0
        };
        int ai4[] =
        {
            0
        };
        double ad5[] =
        {
            0.0D
        };
        double ad6[] =
        {
            0.0D
        };
        int j13 = 0;
        ai3[0] = 1;
        double d13 = (0.0D / 0.0D);
        ai3[0]++;
        ai3[0]++;
        ai3[0]++;
        ai3[0]++;
        ai3[0]++;
        ai3[0]++;
        int l12 = j <= 0 ? -j : j;
        boolean flag2 = false;
        if (i == 3 && j == 0)
        {
            flag2 = true;
        }
        if (!flag2)
        {
            l_c1dim(0, l12, l, ai3);
            l_ncol = k;
            if (i1 > 0)
            {
                l_ncol++;
            }
            if (j1 > 0)
            {
                l_ncol++;
            }
            l_nvar2 = 2 * k;
            l_nvarsq = k * k;
            l_ipairm = l_nvar2;
            l_ipairw = l_nvar2 + l_nvarsq;
            l_ipairv = l_nvar2 + l_nvarsq;
            if (k1 == 3 && (double) j1 > 0.0D)
            {
                l_ipairv += (l_nvarsq + k) / 2;
            }
            if (i <= 1)
            {
                i6 = 1;
                ai[0] = 0;
                BLAS.set(k, 0.0D, ad1, 0, 1);
                for (int i7 = 1; i7 <= k; i7++)
                {
                    BLAS.set(k, 0.0D, ad2, (i7 - 1) * i2, 1);
                    if (k1 <= 0)
                    {
                        continue;
                    }
                    for (int l13 = 0; l13 < k; l13++)
                    {
                        ai[(i7 - 1) * j2 + l13] = 0;
                    }

                }

                if (k1 < 1)
                {
                    if (j1 <= 0)
                    {
                        j13 = 3 * k;
                    }
                    else
                    {
                        j13 = 4 * k;
                    }
                }
                else if (k1 == 1 || k1 == 2)
                {
                    if (j1 <= 0)
                    {
                        j13 = k * (k + 2);
                    }
                    else
                    {
                        j13 = (k * (3 * k + 5)) / 2;
                    }
                }
                else if (k1 == 3)
                {
                    if (j1 <= 0)
                    {
                        j13 = 2 * k * (k + 1);
                    }
                    else
                    {
                        j13 = (5 * k * (k + 1)) / 2;
                    }
                }
                BLAS.set(j13, 0.0D, ad4, 0, 1);
                boolean flag = true;
                BLAS.set(k, (0.0D / 0.0D), ad4, k, 1);
                ai1[0] = 0;
                ai2[0] = 0;
                ad3[0] = 0.0D;
            }
            else
            {
                i6++;
            }
            byte byte0;
            if (j < 0)
            {
                byte0 = -1;
            }
            else
            {
                byte0 = 1;
            }
            ad5[0] = 1.0D;
            for (int k2 = 1; k2 <= l12; k2++)
            {
                l_c1wfr(i, i6, ad, l, k2, byte0, i1, j1, d13, ai2, ad5, ad6, ai4);
                if (ai4[0] == 3)
                {
                    return;
                }
                if (ai4[0] == 1 || ai4[0] == 2)
                {
                    continue;
                }
                int i13 = ad5[0] >= 0.0D ? (int) (ad5[0] + 0.5D) : (int) (ad5[0] - 0.5D);
                double d14 = ad6[0] * (double) i13;
                int k6 = l_isanan(l_ncol, ad, k2 - 1, l);
                if (k6 != 0)
                {
                    ai2[0]++;
                }
                if (k1 < 1)
                {
                    if (k6 != 0)
                    {
                        continue;
                    }
                    ad3[0] += d14;
                    ai1[0] += i13;
                    if (ai1[0] < 0) // throw new
                                    // TooManyObsDeletedException("Covariances.TooManyObsDeleted",
                                    // null);
                    {
                        throw new IllegalArgumentException("Covariances.TooManyObsDeleted");
                    }
                    if (ad3[0] == 0.0D)
                    {
                        continue;
                    }
                    double d4 = d14 / ad3[0];
                    int l8 = 0;
                    double d6 = d14 * (1.0D - d4);
                    for (int j7 = 1; j7 <= k; j7++)
                    {
                        while (++l8 == i1 || l8 == j1)
                            ;
                        if (Double.isNaN(ad4[(k + j7) - 1]))
                        {
                            if (ad6[0] > 0.0D && !Double.isNaN(ad[(l8 - 1) * l + (k2 - 1)]))
                            {
                                ad4[(k + j7) - 1] = ad[(l8 - 1) * l + (k2 - 1)];
                            }
                        }
                        else if (ad6[0] > 0.0D && ad[(l8 - 1) * l + (k2 - 1)] != ad4[(k + j7) - 1])
                        {
                            ad4[j7 - 1] = 1.0D;
                        }
                        double d9 = ad[(l8 - 1) * l + (k2 - 1)] - ad1[j7 - 1];
                        ad4[(l_nvar2 + j7) - 1] = d9;
                        double d8 = d6 * d9;
                        for (int k13 = 0; k13 < j7; k13++)
                        {
                            ad2[(j7 - 1) + k13 * i2] += d8 * ad4[l_nvar2 + k13];
                        }

                        ad1[j7 - 1] += d9 * d4;
                    }

                    ai[0] = ai1[0];
                    continue;
                }
                ad3[0] += d14;
                ai1[0] += i13;
                if (ai1[0] < 0) // throw new
                                // TooManyObsDeletedException("Covariances.TooManyObsDeleted",
                                // null);
                {
                    throw new IllegalArgumentException("Covariances.TooManyObsDeleted");
                }
                int j9 = l_ipairm - k;
                int l9 = l_ipairv - k;
                int i9 = 0;
                for (int k7 = 1; k7 <= k; k7++)
                {
                    while (++i9 == i1 || i9 == j1)
                        ;
                    j9 += k;
                    l9 += k;
                    if (Double.isNaN(ad[(i9 - 1) * l + (k2 - 1)]))
                    {
                        continue;
                    }
                    if (Double.isNaN(ad4[(k + k7) - 1]))
                    {
                        if (ad6[0] > 0.0D && !Double.isNaN(ad[(i9 - 1) * l + (k2 - 1)]))
                        {
                            ad4[(k + k7) - 1] = ad[(i9 - 1) * l + (k2 - 1)];
                        }
                    }
                    else if (ad6[0] > 0.0D && ad[(i9 - 1) * l + (k2 - 1)] != ad4[(k + k7) - 1])
                    {
                        ad4[k7 - 1] = 1.0D;
                    }
                    int j11 = l_ipairm - k;
                    int k11 = l_ipairv - k;
                    int i11 = 0;
                    int l11 = (k7 * (k7 - 1)) / 2;
                    for (int j10 = 1; j10 <= k7; j10++)
                    {
                        while (++i11 == i1 || i11 == j1)
                            ;
                        l11++;
                        j11 += k;
                        k11 += k;
                        if (Double.isNaN(ad[(i11 - 1) * l + (k2 - 1)]))
                        {
                            continue;
                        }
                        ai[(j10 - 1) * j2 + (k7 - 1)] += i13;
                        if (ai[(j10 - 1) * j2 + (k7 - 1)] < 0)
                        {
                            Object aobj[] =
                            {
                                    new Integer(k7), new Double(j10)
                            };
                            // throw new
                            // MoreObsDelThanEnteredException("Covariances.MoreObsDelThanEntered",
                            // aobj);
                            throw new IllegalArgumentException("Covariances.MoreObsDelThanEntered");
                        }
                        double d5;
                        if ((double) j1 > 0.0D)
                        {
                            ad4[(l_ipairw + l11) - 1] += d14;
                            if (ad4[(l_ipairw + l11) - 1] == 0.0D)
                            {
                                continue;
                            }
                            d5 = d14 / ad4[(l_ipairw + l11) - 1];
                        }
                        else
                        {
                            if (ai[(j10 - 1) * j2 + (k7 - 1)] == 0)
                            {
                                continue;
                            }
                            d5 = d14 / (double) ai[(j10 - 1) * j2 + (k7 - 1)];
                        }
                        double d10 = ad[(i11 - 1) * l + (k2 - 1)] - ad4[(j9 + j10) - 1];
                        double d11 = ad[(i9 - 1) * l + (k2 - 1)] - ad4[(j11 + k7) - 1];
                        ad2[(j10 - 1) * i2 + (k7 - 1)] += d14 * d11 * d10 * (1.0D - d5);
                        ad4[(j9 + j10) - 1] += d10 * d5;
                        if (k1 == 3)
                        {
                            ad4[(l9 + j10) - 1] += d14 * d10 * d10 * (1.0D - d5);
                        }
                        if (k7 == j10)
                        {
                            continue;
                        }
                        ad4[(j11 + k7) - 1] += d11 * d5;
                        if (k1 == 3)
                        {
                            ad4[(k11 + k7) - 1] += d14 * d11 * d11 * (1.0D - d5);
                        }
                    }

                    if (j1 > 0)
                    {
                        double d12 = ad4[(l_ipairw + (k7 * (k7 + 1)) / 2) - 1];
                        if (d12 == 0.0D)
                        {
                            ad1[k7 - 1] = 0.0D;
                        }
                        else
                        {
                            ad1[k7 - 1] += (d14 * (ad[(i9 - 1) * l + (k2 - 1)] - ad1[k7 - 1])) / d12;
                        }
                        continue;
                    }
                    if ((double) ai[(k7 - 1) * j2 + (k7 - 1)] == 0.0D)
                    {
                        ad1[k7 - 1] = 0.0D;
                    }
                    else
                    {
                        ad1[k7 - 1] += (d14 * (ad[(i9 - 1) * l + (k2 - 1)] - ad1[k7 - 1]))
                                / (double) ai[(k7 - 1) * j2 + (k7 - 1)];
                    }
                }

            }

        }
        if (i == 0 || i == 3)
        {
            for (int l2 = 1; l2 <= k; l2++)
            {
                if (ad2[(l2 - 1) * i2 + (l2 - 1)] < -2.2204460492503131E-014D)
                {
                    Object aobj1[] =
                    {
                        new Integer(l2)
                    };
                    // throw new
                    // DiffObsDeletedException("Covariances.DiffObsDeleted",
                    // aobj1);
                    throw new IllegalArgumentException("Covariances.DiffObsDeleted");
                }
            }

            double d1 = ai1[0] - 1;
            if (ad3[0] < 2.2204460492503131E-015D)
            {
                BLAS.set(k, d13, ad1, 0, 1);
                if (l1 <= 1)
                {
                    for (int i3 = 1; i3 <= k; i3++)
                    {
                        BLAS.set(k, d13, ad2, (i3 - 1) * i2, 1);
                    }

                    // Warning.print(this, "com.imsl.stat",
                    // "Covariances.ZeroSumOfWeight2", null);
                    System.out.println("Covariances.ZeroSumOfWeight2");
                }
                else
                {
                    for (int j3 = 1; j3 <= k; j3++)
                    {
                        BLAS.set(k, d13, ad2, (j3 - 1) * i2, 1);
                    }

                    // Warning.print(this, "com.imsl.stat",
                    // "Covariances.ZeroSumOfWeight3", null);
                    System.out.println("Covariances.ZeroSumOfWeight3");
                }
                return;
            }
            for (int k3 = 1; k3 <= k; k3++)
            {
                if (ad4[k3 - 1] != 0.0D)
                {
                    continue;
                }
                ad2[(k3 - 1) * i2 + (k3 - 1)] = 0.0D;
                if (l1 >= 2)
                {
                    Object aobj2[] =
                    {
                        new Integer(k3)
                    };
                    // Warning.print(this, "com.imsl.stat",
                    // "Covariances.ConstantVariable", aobj2);
                    System.out.println("Covariances.ConstantVariable");
                }
            }

            if (k1 == 1)
            {
                int i12 = 0;
                int j6 = l_ipairm - k;
                for (int l3 = 1; l3 <= k; l3++)
                {
                    int k9 = l_ipairm - k;
                    j6 += k;
                    for (int l7 = 1; l7 <= l3; l7++)
                    {
                        i12++;
                        k9 += k;
                        double d;
                        if (j1 > 0)
                        {
                            d = ad4[(l_ipairw + i12) - 1];
                        }
                        else
                        {
                            d = ai[(l7 - 1) * j2 + (l3 - 1)];
                        }
                        ad2[(l7 - 1) * i2 + (l3 - 1)] += d
                                * ((ad1[l3 - 1] * ad1[l7 - 1] + ad4[(k9 + l3) - 1] * ad4[(j6 + l7) - 1]) - ad1[l3 - 1]
                                        * ad4[(j6 + l7) - 1] - ad1[l7 - 1] * ad4[(k9 + l3) - 1]);
                    }

                    if (ad2[(l3 - 1) * i2 + (l3 - 1)] < 0.0D)
                    {
                        ad2[(l3 - 1) * i2 + (l3 - 1)] = 0.0D;
                    }
                }

            }
            if (l1 == 0)
            {
                if (k1 == 0)
                {
                    if (ai[0] >= 2)
                    {
                        double d7 = 1.0D / d1;
                        for (int i4 = 1; i4 <= k; i4++)
                        {
                            BLAS.scal(i4, d7, ad2, i4 - 1, i2);
                        }

                    }
                    else
                    {
                        // Warning.print(this, "com.imsl.stat",
                        // "Covariances.InsufficientData", null);
                        System.out.println("Covariances.InsufficientData");
                        for (int j4 = 1; j4 <= k; j4++)
                        {
                            BLAS.set(j4, d13, ad2, j4 - 1, i2);
                        }

                    }
                }
                else
                {
                    boolean flag1 = false;
                    for (int k4 = 1; k4 <= k; k4++)
                    {
                        for (int i8 = 1; i8 <= k4; i8++)
                        {
                            if (ai[(i8 - 1) * j2 + (k4 - 1)] >= 2)
                            {
                                ad2[(i8 - 1) * i2 + (k4 - 1)] /= (double) ai[(i8 - 1) * j2 + (k4 - 1)] - 1.0D;
                            }
                            else
                            {
                                ad2[(i8 - 1) * i2 + (k4 - 1)] = d13;
                                flag1 = true;
                            }
                        }

                    }

                    if (flag1) // Warning.print(this, "com.imsl.stat",
                               // "Covariances.InsufficientData", null);
                    {
                        System.out.println("Covariances.InsufficientData");
                    }
                    if (k1 == 3)
                    {
                        int k12 = -k;
                        for (int l4 = 1; l4 <= k; l4++)
                        {
                            int k10 = l4 - 1;
                            int j12 = l4 - k;
                            k12 += -j12 + 1;
                            for (int j8 = 1; j8 <= k10; j8++)
                            {
                                j12 += k;
                                k12++;
                                if (ai[(j8 - 1) * j2 + (l4 - 1)] >= 2)
                                {
                                    ad4[(l_ipairv + j12) - 1] /= ai[(j8 - 1) * j2 + (l4 - 1)] - 1;
                                    ad4[(l_ipairv + k12) - 1] /= ai[(j8 - 1) * j2 + (l4 - 1)] - 1;
                                }
                                else
                                {
                                    ad4[(l_ipairv + j12) - 1] = 0.0D;
                                    ad4[(l_ipairv + k12) - 1] = 0.0D;
                                }
                            }

                            k12++;
                            if (ai[(l4 - 1) * j2 + (l4 - 1)] >= 2)
                            {
                                ad4[(l_ipairv + k12) - 1] /= ai[(l4 - 1) * j2 + (l4 - 1)] - 1;
                            }
                            else
                            {
                                ad4[(l_ipairv + k12) - 1] = 0.0D;
                            }
                        }

                    }
                }
            }
            else if (l1 >= 2 && k > 1)
            {
                int i10 = l_ipairv;
                for (int k8 = 2; k8 <= k; k8++)
                {
                    i10 += k;
                    int l10 = k8 - 1;
                    int l6 = l_ipairv - k;
                    for (int i5 = 1; i5 <= l10; i5++)
                    {
                        l6 += k;
                        if (ad2[(i5 - 1) * i2 + (i5 - 1)] == 0.0D || ad2[(k8 - 1) * i2 + (k8 - 1)] == 0.0D)
                        {
                            ad2[(i5 - 1) * i2 + (k8 - 1)] = d13;
                            if (ad4[i5 - 1] != 0.0D && ad4[k8 - 1] != 0.0D)
                            {
                                Object aobj3[] =
                                {
                                    new Integer(i5)
                                };
                                // Warning.print(this, "com.imsl.stat",
                                // "Covariances.ConstantVariable", aobj3);
                                System.out.println("Covariances.ConstantVariable");
                            }
                            continue;
                        }
                        if (k1 <= 0)
                        {
                            if (d1 < 1.0D)
                            {
                                ad2[(i5 - 1) * i2 + (k8 - 1)] = d13;
                                if (ad4[i5 - 1] != 0.0D && ad4[k8 - 1] != 0.0D) // Warning.print(this,
                                                                                // "com.imsl.stat",
                                                                                // "Covariances.TooFewValidObsCorrel",
                                                                                // null);
                                {
                                    System.out.println("Covariances.TooFewValidObsCorrel");
                                }
                                continue;
                            }
                            ad2[(i5 - 1) * i2 + (k8 - 1)] /= Math.sqrt(ad2[(k8 - 1) * i2 + (k8 - 1)])
                                    * Math.sqrt(ad2[(i5 - 1) * i2 + (i5 - 1)]);
                            if (Double.isNaN(ad2[(i5 - 1) * i2 + (k8 - 1)]))
                            {
                                continue;
                            }
                            if (ad2[(i5 - 1) * i2 + (k8 - 1)] > 1.0D)
                            {
                                ad2[(i5 - 1) * i2 + (k8 - 1)] = 1.0D;
                            }
                            if (ad2[(i5 - 1) * i2 + (k8 - 1)] < -1D)
                            {
                                ad2[(i5 - 1) * i2 + (k8 - 1)] = -1D;
                            }
                            continue;
                        }
                        if (ai[(i5 - 1) * j2 + (k8 - 1)] < 2)
                        {
                            ad2[(i5 - 1) * i2 + (k8 - 1)] = d13;
                            if (ad4[i5 - 1] != 0.0D && ad4[k8 - 1] != 0.0D) // Warning.print(this,
                                                                            // "com.imsl.stat",
                                                                            // "Covariances.TooFewValidObsCorrel",
                                                                            // null);
                            {
                                System.out.println("Covariances.TooFewValidObsCorrel");
                            }
                            continue;
                        }
                        if (k1 != 3)
                        {
                            ad2[(i5 - 1) * i2 + (k8 - 1)] = (ad2[(i5 - 1) * i2 + (k8 - 1)] * Math
                                    .sqrt(((double) ai[(k8 - 1) * j2 + (k8 - 1)] - 1.0D)
                                            * ((double) ai[(i5 - 1) * j2 + (i5 - 1)] - 1.0D)))
                                    / (((double) ai[(i5 - 1) * j2 + (k8 - 1)] - 1.0D)
                                            * Math.sqrt(ad2[(i5 - 1) * i2 + (i5 - 1)]) * Math.sqrt(ad2[(k8 - 1) * i2
                                            + (k8 - 1)]));
                        }
                        if (k1 == 3)
                        {
                            if (ad4[(i10 + i5) - 1] <= 0.0D || ad4[(l6 + k8) - 1] <= 0.0D)
                            {
                                ad2[(i5 - 1) * i2 + (k8 - 1)] = d13;
                                if (ad4[i5 - 1] != 0.0D && ad4[k8 - 1] != 0.0D) // Warning.print(this,
                                                                                // "com.imsl.stat",
                                                                                // "Covariances.TooFewValidObsCorrel",
                                                                                // null);
                                {
                                    System.out.println("Covariances.TooFewValidObsCorrel");
                                }
                                continue;
                            }
                            double d3 = Math.sqrt(ad4[(i10 + i5) - 1] * ad4[(l6 + k8) - 1]);
                            double ad7[] =
                            {
                                ad2[(i5 - 1) * i2 + (k8 - 1)]
                            };
                            l_c1div(ad2[(i5 - 1) * i2 + (k8 - 1)], d3, ad7);
                            ad2[(i5 - 1) * i2 + (k8 - 1)] = ad7[0];
                            if (Double.isNaN(ad2[(i5 - 1) * i2 + (k8 - 1)]))
                            {
                                if (ad4[i5 - 1] != 0.0D && ad4[k8 - 1] != 0.0D) // Warning.print(this,
                                                                                // "com.imsl.stat",
                                                                                // "Covariances.TooFewValidObsCorrel",
                                                                                // null);
                                {
                                    System.out.println("Covariances.TooFewValidObsCorrel");
                                }
                                continue;
                            }
                        }
                        if (Double.isNaN(ad2[(i5 - 1) * i2 + (k8 - 1)]))
                        {
                            continue;
                        }
                        if (ad2[(i5 - 1) * i2 + (k8 - 1)] > 1.0D)
                        {
                            ad2[(i5 - 1) * i2 + (k8 - 1)] = 1.0D;
                        }
                        if (ad2[(i5 - 1) * i2 + (k8 - 1)] < -1D)
                        {
                            ad2[(i5 - 1) * i2 + (k8 - 1)] = -1D;
                        }
                    }

                }

                if (l1 == 3)
                {
                    for (int j5 = 1; j5 <= k; j5++)
                    {
                        double d2 = ad2[(j5 - 1) * i2 + (j5 - 1)];
                        ad2[(j5 - 1) * i2 + (j5 - 1)] = d13;
                        if (k1 <= 0 && d1 >= 1.0D)
                        {
                            ad2[(j5 - 1) * i2 + (j5 - 1)] = Math.sqrt(d2 / d1);
                        }
                        if (k1 > 0 && ai[(j5 - 1) * j2 + (j5 - 1)] > 1)
                        {
                            ad2[(j5 - 1) * i2 + (j5 - 1)] = Math.sqrt(d2
                                    / ((double) ai[(j5 - 1) * j2 + (j5 - 1)] - 1.0D));
                        }
                    }

                }
                else
                {
                    for (int k5 = 1; k5 <= k; k5++)
                    {
                        ad2[(k5 - 1) * i2 + (k5 - 1)] = 1.0D;
                    }

                }
            }
        }
        for (int l5 = 1; l5 <= k - 1; l5++)
        {
            BLAS.copy(k - l5, ad2, (l5 - 1) * i2 + l5, 1, ad2, l5 * i2 + (l5 - 1), i2);
            if (k1 > 0)
            {
                BLAS.copy(k - l5, ai, (l5 - 1) * j2 + l5, 1, ai, l5 * j2 + (l5 - 1), j2);
            }
        }

    }

    private void l_c1div(double d, double d1, double ad[])
    {
        double d3 = (0.0D / 0.0D);
        if (Double.isNaN(d) || Double.isNaN(d1))
        {
            ad[0] = d3;
            return;
        }
        double d2 = Math.abs(d1);
        if (d2 <= 1.0D)
        {
            double d4 = 1.7976931348623157E+308D;
            if (Math.abs(d) < d4 * d2)
            {
                ad[0] = d / d1;
                return;
            }
            if (d == 0.0D)
            {
                ad[0] = d3;
            }
            else if (d1 >= 0.0D)
            {
                if (d >= 0.0D)
                {
                    ad[0] = (1.0D / 0.0D);
                }
                else
                {
                    ad[0] = (-1.0D / 0.0D);
                }
            }
            else if (d >= 0.0D)
            {
                ad[0] = (-1.0D / 0.0D);
            }
            else
            {
                ad[0] = (1.0D / 0.0D);
            }
            return;
        }
        double d5 = 2.2250738585072009E-308D;
        if (Math.abs(d) >= d5 * d2)
        {
            ad[0] = d / d1;
            return;
        }
        else
        {
            ad[0] = 0.0D;
            return;
        }
    }

    private void l_c1dim(int i, int j, int k, int ai[])
    {
        ai[0]++;
        ai[0]++;
        if (k >= 1)
        {
            ai[0]++;
        }
        else
        {
            ai[0]++;
        }
    }

    private void l_c1wfr(int i, int j, double ad[], int k, int l, int i1, int j1, int k1, double d, int ai[],
            double ad1[], double ad2[], int ai1[]) // throws
                                                   // NonnegativeFreqException,
                                                   // NonnegativeWeightException
    {
        ai1[0] = 0;
        if (j1 > 0)
        {
            ad1[0] = ad[((j1 - 1) * k + l) - 1];
            if (Double.isNaN(ad1[0]))
            {
                ai[0] += i1;
                ai1[0] = 2;
            }
            else if (ad1[0] == 0.0D)
            {
                ai1[0] = 1;
                return;
            }
        }
        if (k1 > 0)
        {
            ad2[0] = ad[(k * (k1 - 1) + l) - 1];
            if (Double.isNaN(ad2[0]) && ai1[0] != 2)
            {
                ai[0] += i1;
                ai1[0] = 2;
            }
        }
        if (j1 > 0)
        {
            if (!Double.isNaN(ad1[0]) && ad1[0] < 0.0D)
            {
                if (i > 0)
                {
                    Object aobj[] =
                    {
                            new Integer(l), new Double(ad1[0]), new Integer(j)
                    };
                    // throw new
                    // NonnegativeFreqException("Covariances.NonnegativeFreq",
                    // aobj);
                    throw new IllegalArgumentException("Covariances.NonnegativeFreq");
                }
                else
                {
                    Object aobj1[] =
                    {
                            new Integer(l), new Double(ad1[0])
                    };
                    // throw new
                    // NonnegativeFreqException("Covariances.NonnegativeFreq1",
                    // aobj1);
                    throw new IllegalArgumentException("Covariances.NonnegativeFreq1");
                }
            }
        }
        else
        {
            ad1[0] = 1.0D;
        }
        if (i1 == -1)
        {
            ad1[0] = -ad1[0];
        }
        if (k1 > 0)
        {
            if (!Double.isNaN(ad2[0]) && ad2[0] < 0.0D)
            {
                if (i > 0)
                {
                    Object aobj2[] =
                    {
                            new Integer(l), new Double(ad2[0]), new Integer(j)
                    };
                    // throw new
                    // NonnegativeWeightException("Covariances.NonnegativeWeight",
                    // aobj2);
                    throw new IllegalArgumentException("Covariances.NonnegativeWeight");
                }
                else
                {
                    Object aobj3[] =
                    {
                            new Integer(l), new Double(ad2[0])
                    };
                    // throw new
                    // NonnegativeWeightException("Covariances.NonnegativeWeight1",
                    // aobj3);
                    throw new IllegalArgumentException("Covariances.NonnegativeWeight1");
                }
            }
        }
        else
        {
            ad2[0] = 1.0D;
        }
    }

    private int l_isanan(int i, double ad[], int j, int k)
    {
        int i1 = 0;
        int j1 = 1;
        int l = 1;
        do
        {
            if (j1 > i)
            {
                break;
            }
            if (Double.isNaN(ad[(j + l) - 1]))
            {
                i1 = j1;
                break;
            }
            l += k;
            j1++;
        }
        while (true);
        return i1;
    }

    public int getNumRowMissing()
    {
        return l_nrmiss;
    }

    public double getSumOfWeights()
    {
        return l_sumwt;
    }

    public void setWeights(Matrix dat)
    {
        if (dat == null)
        {
            throw new IllegalArgumentException("setWeights : Parameter \"dat\" must be non-null.");
        }
        if (!dat.isVector())
        {
            throw new IllegalArgumentException(
                    "setWeights : Matrix parameter \"dat\" must be a vector and not a matrix.");
        }
        double[] ad = null;
        if (dat.isColVector())
        {
            ad = dat.getColumnPackedCopy();
        }
        else
        {
            ad = dat.getRowPackedCopy();
        }
        setWeights(ad);
    }

    public void setWeights(double ad[])
    {
        if (ad.length != l_nrows)
        {
            Object aobj[] =
            {
                    "weights", new Integer(ad.length), new Integer(l_nrows)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj);
            throw new IllegalArgumentException("NotEqual");
        }
        l_iwt = 1;
        l_user_w = 1;
        l_wt = (double[]) ad.clone();
    }

    public void setFrequencies(Matrix dat)
    {
        if (dat == null)
        {
            throw new IllegalArgumentException("setFrequencies : Parameter \"dat\" must be non-null.");
        }
        if (!dat.isVector())
        {
            throw new IllegalArgumentException(
                    "setFrequencies : Matrix parameter \"dat\" must be a vector and not a matrix.");
        }
        double[] ad = null;
        if (dat.isColVector())
        {
            ad = dat.getColumnPackedCopy();
        }
        else
        {
            ad = dat.getRowPackedCopy();
        }

        setFrequencies(ad);
    }

    public void setFrequencies(double ad[])
    {
        if (ad.length != l_nrows)
        {
            Object aobj[] =
            {
                    "frequencies", new Integer(ad.length), new Integer(l_nrows)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj);
            throw new IllegalArgumentException("NotEqual");
        }
        l_ifreq = 1;
        l_user_f = 1;
        l_freq = (double[]) ad.clone();
    }

    public Matrix getMeans()
    {
        if (l_means == null)
        {
            return null;
        }
        else
        {
            return new Matrix((double[]) l_means.clone());
        }
    }

    public int getObservations()
    {
        return l_nobs;
    }

    public void setMissingValueMethod(CovMissingValueMethod method)
    {
        if (method == null)
        {
            throw new IllegalArgumentException("setMissingValueMethod : Parameter \"method\" must be non-null.");
        }
        /*
         * if (method < 0 || method > 3) { Object aobj[] = {
         * "missingValueMethod", new Integer(method), new Integer(0), new
         * Integer(3) };
         * //Messages.throwIllegalArgumentException("com.imsl.stat",
         * "OutOfRange", aobj); throw new
         * IllegalArgumentException("OutOfRange"); }
         */
        l_mopt = method.getNum();
    }

    public Indices getIncidenceMatrix()
    {
        if (l_incd == null)
        {
            // return (int[][]) null;
            Indices nullInd = null;
            return nullInd;
        }
        int i;
        if (l_mopt == 0)
        {
            i = 1;
        }
        else
        {
            i = l_nvariables;
        }
        int ai[][] = new int[i][i];
        for (int j = 0; j < i; j++)
        {
            System.arraycopy(l_incd, j * i, ai[j], 0, i);
        }

        return new Indices(ai);
    }
}
