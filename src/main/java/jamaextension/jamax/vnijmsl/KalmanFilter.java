/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 *
 * @author Feynman Perceptrons
 */
//public class KalmanFilter {

//}
import java.io.Serializable;

import jamaextension.jamax.Matrix;

public class KalmanFilter implements Serializable, Cloneable
{

    private int l_nb;
    private int l_n[] =
    {
        0
    };
    private double l_ss[] =
    {
        0.0D
    };
    private double l_alndet[] =
    {
        0.0D
    };
    private double l_b[];
    private double l_covb[];
    private int l_it;
    private int l_iq;
    private int l_update;
    private int l_ny;
    private double l_t[];
    private double l_q[];
    private double l_y[];
    private double l_z[];
    private double l_r[];
    private double l_covv[];
    private double l_v[];
    private double l_tol;

    public KalmanFilter(double ad[], double ad1[], int i, double d, double d1)
    {
        if (ad.length < 1)
        {
            Object aobj[] =
            {
                    "b.length", new Integer(0)
            };
            throw new IllegalArgumentException("NotLargeEnough");
        }
        l_b = (double[]) ad.clone();
        l_nb = ad.length;
        if (ad1.length != l_nb * l_nb)
        {
            Object aobj1[] =
            {
                    "covb", new Integer(l_nb * l_nb)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj1);
            throw new IllegalArgumentException("NotEqual");
        }
        l_covb = (double[]) ad1.clone();
        l_n[0] = i;
        l_ss[0] = d;
        l_alndet[0] = d1;
        l_tol = 2.2204460492503131E-014D;
        l_it = 1;
        l_iq = 1;
        l_update = 0;
        l_ny = 0;
    }

    public final void filter()
    {
        int i = l_nb;
        double ad[] = l_b;
        double ad1[] = l_covb;
        int ai[] = l_n;
        double ad2[] = l_ss;
        double ad3[] = l_alndet;
        double ad4[] = null;
        double ad7[] = l_y;
        double ad8[] = l_z;
        double ad9[] = l_r;
        int j = l_ny;
        double d = l_tol;
        double ad10[] = l_t;
        int k = l_it;
        double ad11[] = l_q;
        int l = l_iq;
        double ad12[] = null;
        boolean flag = false;
        boolean flag1 = true;
        double ad13[] = null;
        boolean flag2 = false;
        boolean flag3 = true;
        int i1 = l_update;
        int l2 = i;
        int i3 = 0;
        boolean flag4 = false;
        int j3 = i;
        int k3 = i;
        int l3 = i;
        if (!flag4)
        {
            i3 = j;
        }
        if (j > 0)
        {
            ad4 = new double[j * j];
        }
        double ad5[] = new double[i * i];
        double ad6[] = new double[i * j + Math.max(i, j)];
        if (i1 != 0)
        {
            if (!flag2)
            {
                ad13 = new double[j];
            }
            if (!flag)
            {
                ad12 = new double[j * j];
            }
        }
        if (ad11 != null)
        {
            l_m1ran(i, k3, ad11, ad11);
        }
        if (ad10 != null)
        {
            l_m1ran(i, j3, ad10, ad10);
        }
        l_m1ran(i, l3, ad1, ad1);
        if (j > 1)
        {
            l_m1ran(j, l2, ad8, ad8);
            l_m1ran(j, i3, ad9, ad9);
        }
        int j1 = j;
        int k1 = j;
        int l1 = i;
        int i2 = i;
        int j2 = i;
        int k2 = j;
        l_k2lmn(j, ad7, i, ad8, j1, ad9, k1, k, ad10, l1, l, ad11, i2, d, ad, ad1, j2, ai, ad2, ad3, ad13, ad12, k2,
                ad4, ad5, ad6);
        if (ad11 != null)
        {
            l_m1ran(k3, i, ad11, ad11);
        }
        if (ad10 != null)
        {
            l_m1ran(j3, i, ad10, ad10);
        }
        l_m1ran(l3, i, ad1, ad1);
        if (j > 1)
        {
            l_m1ran(l2, j, ad8, ad8);
            l_m1ran(i3, j, ad9, ad9);
        }
        if (flag3 && !flag2 && ad13 != null)
        {
            l_v = (double[]) ad13.clone();
        }
        if (flag1 && !flag && ad12 != null)
        {
            l_covv = (double[]) ad12.clone();
        }
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

    private void l_k2lmn(int i, double ad[], int j, double ad1[], int k, double ad2[], int l, int i1, double ad3[],
            int j1, int k1, double ad4[], int l1, double d, double ad5[], double ad6[], int i2, int ai[], double ad7[],
            double ad8[], double ad9[], double ad10[], int j2, double ad11[], double ad12[], double ad13[])
    {
        int ai1[] =
        {
            0
        };
        double ad14[] =
        {
            0.0D
        };
        int k9 = i * j + 1;
        boolean flag = false;
        if (i == 0)
        {
            flag = true;
        }
        if (!flag)
        {
            l_csfrg(j, ad6, i2);
            l_mrrrr(j, ad1, k, ad6, i2, i, j, ad13, i);
            for (int l5 = 1; l5 <= i; l5++)
            {
                int j7 = l5 - 1;
                for (int k3 = 1; k3 <= l5; k3++)
                {
                    int k4 = k3 - 1;
                    ad10[j7 * j2 + k4] = ad2[j7 * l + k4] + BLAS.dot(j, ad13, k4, i, ad1, j7, k);
                }

            }

            l_csfrg(i, ad10, j2);
            l_chfac(i, ad10, j2, d, ai1, ad11, i);
            ai[0] += ai1[0];
            int j9 = i + 1;
            int i9 = i * i;
            for (int i6 = i; i6 >= 1; i6--)
            {
                int k7 = i6 - 1;
                if (ad11[i9 - 1] != 0.0D)
                {
                    ad8[0] += Math.log(BLAS.dot(j9 - i6, ad11, i9 - 1, i, ad11, i9 - 1, i));
                    j9 = i6;
                }
                i9 -= i + 1;
            }

            l_girts(i, ad11, i, j, ad13, i, 2, ai1, ad13, 0, i, ad14, 1);
            int l8 = 1;
            for (int j6 = 1; j6 <= j; j6++)
            {
                int l7 = j6 - 1;
                int k5 = 1;
                for (int l3 = 1; l3 <= j6; l3++)
                {
                    int l4 = l3 - 1;
                    ad6[l7 * i2 + l4] -= BLAS.dot(i, ad13, k5 - 1, 1, ad13, l8 - 1, 1);
                    k5 += i;
                }

                l8 += i;
            }

            BLAS.copy(i, ad, ad9);
            double d1 = -1D;
            double d2 = 1.0D;
            int k2 = 1;
            int i3 = 1;
            l_gemv('N', 2, i, j, d1, ad1, k, ad5, 0, k2, d2, ad9, 0, i3);
            l_girts(i, ad11, i, 1, ad9, i, 2, ai1, ad13, k9 - 1, i, ad14, 1);
            d1 = 1.0D;
            k2 = 1;
            i3 = 1;
            l_gemv('T', 2, i, j, d1, ad13, i, ad13, k9 - 1, k2, d2, ad5, 0, i3);
            ad7[0] += BLAS.dot(i, ad13, k9 - 1, 1, ad13, k9 - 1, 1);
        }
        if (i1 == 0)
        {
            BLAS.copy(j, ad5, 0, 1, ad13, k9 - 1, 1);
            double d3 = 1.0D;
            int l2 = 1;
            double d4 = 0.0D;
            int j3 = 1;
            l_gemv('N', 2, j, j, d3, ad3, j1, ad13, k9 - 1, l2, d4, ad5, 0, j3);
            l_csfrg(j, ad6, i2);
            l_mrrrr(j, ad3, j1, ad6, i2, j, j, ad12, j);
            if (k1 == 0)
            {
                for (int k6 = 1; k6 <= j; k6++)
                {
                    int i8 = k6 - 1;
                    for (int i4 = 1; i4 <= k6; i4++)
                    {
                        int i5 = i4 - 1;
                        ad6[i8 * i2 + i5] = ad4[i8 * l1 + i5] + BLAS.dot(j, ad12, i5, j, ad3, i8, j1);
                    }

                }

            }
            else
            {
                for (int l6 = 1; l6 <= j; l6++)
                {
                    int j8 = l6 - 1;
                    for (int j4 = 1; j4 <= l6; j4++)
                    {
                        int j5 = j4 - 1;
                        ad6[j8 * i2 + j5] = BLAS.dot(j, ad12, j5, j, ad3, j8, j1);
                    }

                }

            }
        }
        else if (k1 == 0)
        {
            for (int i7 = 1; i7 <= j; i7++)
            {
                int k8 = i7 - 1;
                BLAS.axpy(i7, 1.0D, ad4, k8 * l1, 1, ad6, k8 * i2, 1);
            }

        }
        l_csfrg(j, ad6, i2);
    }

    private void l_mrrrr(int i, double ad[], int j, double ad1[], int k, int l, int i1, double ad2[], int j1)
    {
        double d = 1.0D;
        double d1 = 0.0D;
        l_gemm('N', 2, 'N', 2, l, i1, i, d, ad, j, ad1, k, d1, ad2, j1);
    }

    private void l_csfrg(int i, double ad[], int j)
    {
        for (int k = 1; k <= i - 1; k++)
        {
            BLAS.copy(i - k, ad, k * j + (k - 1), j, ad, (k - 1) * j + k, 1);
        }

    }

    private void l_chfac(int i, double ad[], int j, double d, int ai[], double ad1[], int k)
    {
        int ai1[] =
        {
            0
        };
        ai1[0] = 1;
        l_c1dim(1, i, 'N', j, ai1);
        l_c1dim(1, i, '*', k, ai1);
        int l = 0;
        for (int i1 = 1; i1 <= i; i1++)
        {
            BLAS.copy(i1, ad, j * (i1 - 1), 1, ad1, k * (i1 - 1), 1);
        }

        ai[0] = 0;
        for (int j1 = 1; j1 <= i; j1++)
        {
            double d1 = 0.0D;
            double d3 = d * Math.sqrt(Math.abs(ad1[(j1 + k * (j1 - 1)) - 1]));
            for (int k1 = 1; k1 <= j1 - 1; k1++)
            {
                double d2 = ad1[(k1 + k * (j1 - 1)) - 1] - BLAS.dot(k1 - 1, ad1, k * (k1 - 1), 1, ad1, k * (j1 - 1), 1);
                if (ad1[(k1 + k * (k1 - 1)) - 1] != 0.0D)
                {
                    d2 /= ad1[(k1 + k * (k1 - 1)) - 1];
                    ad1[(k1 + k * (j1 - 1)) - 1] = d2;
                    d1 += d2 * d2;
                    continue;
                }
                if (l == 0 && Math.abs(d2) > d3 * BLAS.nrm2(k1 - 1, ad1, k * (k1 - 1), 1))
                {
                    l = j1;
                }
                ad1[(k1 + k * (j1 - 1)) - 1] = 0.0D;
            }

            d1 = ad1[(j1 + k * (j1 - 1)) - 1] - d1;
            if (Math.abs(d1) <= d * Math.abs(ad1[(j1 + k * (j1 - 1)) - 1]))
            {
                d1 = 0.0D;
            }
            else if (d1 < 0.0D)
            {
                d1 = 0.0D;
                if (l == 0)
                {
                    l = j1;
                }
            }
            else
            {
                ai[0]++;
            }
            ad1[(j1 + k * (j1 - 1)) - 1] = Math.sqrt(d1);
        }

        if (l != 0) // Warning.print(this, "com.imsl.stat",
                    // "KalmanFilter.SubMatrix", null);
        {
            System.out.println("Warning : SubMatrix");
        }
        l_c1trg(i, ad1, k);
    }

    private void l_c1trg(int i, double ad[], int j)
    {
        for (int k = 1; k <= i - 1; k++)
        {
            BLAS.set(i - k, 0.0D, ad, (k + 1 + j * (k - 1)) - 1, 1);
        }

    }

    private void l_girts(int i, double ad[], int j, int k, double ad1[], int l, int i1, int ai[], double ad2[], int j1,
            int k1, double ad3[], int l1)
    {
        int ai1[] =
        {
            0
        };
        ai1[0] = 1;
        l_c1dim(1, i, 'n', j, ai1);
        ai1[0]++;
        ai1[0]++;
        l_c1r(i, ad, j, ai1);
        ai[0] = 0;
        for (int i2 = 1; i2 <= i; i2++)
        {
            if (ad[(i2 + j * (i2 - 1)) - 1] != 0.0D)
            {
                ai[0]++;
            }
        }

        for (int k3 = 1; k3 <= k; k3++)
        {
            BLAS.copy(i, ad1, l * (k3 - 1), 1, ad2, j1 + k1 * (k3 - 1), 1);
        }

        if (i1 == 1 || i1 == 3)
        {
            if (ai[0] < i)
            {
                for (int j2 = 1; j2 <= k; j2++)
                {
                    for (int l3 = i; l3 >= 1; l3--)
                    {
                        if (ad[(l3 + j * (l3 - 1)) - 1] == 0.0D)
                        {
                            if (ad2[(j1 + l3 + k1 * (j2 - 1)) - 1] != 0.0D)
                            {
                                Object aobj[] =
                                {
                                        new Integer(l3), new Integer(j2),
                                        new Double(ad2[(j1 + l3 + k1 * (j2 - 1)) - 1])
                                };
                                // Warning.print(this, "com.imsl.stat",
                                // "KalmanFilter.ToleranceInconsistent", aobj);
                                System.out.println("Warning : ToleranceInconsistent");
                            }
                            ad2[(j1 + l3 + k1 * (j2 - 1)) - 1] = 0.0D;
                        }
                        else
                        {
                            ad2[(j1 + l3 + k1 * (j2 - 1)) - 1] /= ad[(l3 + j * (l3 - 1)) - 1];
                            double d = -ad2[(j1 + l3 + k1 * (j2 - 1)) - 1];
                            BLAS.axpy(l3 - 1, d, ad, j * (l3 - 1), 1, ad2, j1 + k1 * (j2 - 1), 1);
                        }
                    }

                }

            }
            else
            {
                for (int k2 = 1; k2 <= k; k2++)
                {
                    l_trsv('U', 'N', 'N', i, ad, j, ad2, j1 + k1 * (k2 - 1), 1);
                }

            }
        }
        else if (i1 == 2 || i1 == 4)
        {
            if (ai[0] < i)
            {
                for (int l2 = 1; l2 <= k; l2++)
                {
                    for (int i4 = 1; i4 <= i; i4++)
                    {
                        double d1 = ad2[(j1 + i4 + k1 * (l2 - 1)) - 1]
                                - BLAS.dot(i4 - 1, ad, j * (i4 - 1), 1, ad2, j1 + k1 * (l2 - 1), 1);
                        if (ad[(i4 + j * (i4 - 1)) - 1] == 0.0D)
                        {
                            double d3 = Math.abs(ad2[(j1 + i4 + k1 * (l2 - 1)) - 1])
                                    + l_a1ot(i4 - 1, ad, j * (i4 - 1), 1, ad2, j1 + k1 * (l2 - 1), 1);
                            d3 *= 4.4408920985006262E-014D;
                            if (Math.abs(d1) > d3)
                            {
                                Object aobj1[] =
                                {
                                        new Integer(i4), new Integer(l2)
                                };
                                // Warning.print(this, "com.imsl.stat",
                                // "KalmanFilter.ToleranceInconsistent", aobj1);
                                System.out.println("Warning : ToleranceInconsistent");
                            }
                            ad2[(j1 + i4 + k1 * (l2 - 1)) - 1] = 0.0D;
                        }
                        else
                        {
                            ad2[(j1 + i4 + k1 * (l2 - 1)) - 1] = d1 / ad[(i4 + j * (i4 - 1)) - 1];
                        }
                    }

                }

            }
            else
            {
                for (int i3 = 1; i3 <= k; i3++)
                {
                    l_trsv('U', 'T', 'N', i, ad, j, ad2, j1 + k1 * (i3 - 1), 1);
                }

            }
        }
        if (i1 == 3 || i1 == 4)
        {
            for (int j4 = 1; j4 <= i; j4++)
            {
                BLAS.copy(j4, ad, j * (j4 - 1), 1, ad3, l1 * (j4 - 1), 1);
            }

            for (int k4 = 1; k4 <= i; k4++)
            {
                if (ad3[(k4 + l1 * (k4 - 1)) - 1] == 0.0D)
                {
                    BLAS.set(k4, 0.0D, ad3, l1 * (k4 - 1), 1);
                    BLAS.set(i - k4, 0.0D, ad3, (k4 + l1 * k4) - 1, l1);
                    continue;
                }
                ad3[(k4 + l1 * (k4 - 1)) - 1] = 1.0D / ad3[(k4 + l1 * (k4 - 1)) - 1];
                double d2 = -ad3[(k4 + l1 * (k4 - 1)) - 1];
                BLAS.scal(k4 - 1, d2, ad3, l1 * (k4 - 1), 1);
                if (k4 < i)
                {
                    l_ger(k4 - 1, i - k4, 1.0D, ad3, l1 * (k4 - 1), 1, ad3, (k4 + l1 * k4) - 1, l1, ad3, l1 * k4, l1);
                    BLAS.scal(i - k4, ad3[(k4 + l1 * (k4 - 1)) - 1], ad3, (k4 + l1 * k4) - 1, l1);
                }
            }

            for (int j3 = 1; j3 <= i - 1; j3++)
            {
                BLAS.set(i - j3, 0.0D, ad3, (j3 + 1 + l1 * (j3 - 1)) - 1, 1);
            }

        }
    }

    private void l_c1dim(int i, int j, char c, int k, int ai[])
    {
        if (c == '*')
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
        else
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
    }

    private void l_c1r(int i, double ad[], int j, int ai[])
    {
        for (int k = 1; k <= i; k++)
        {
            if (ad[(k + j * (k - 1)) - 1] != 0.0D)
            {
                continue;
            }
            for (int l = k + 1; l <= i; l++)
            {
                if (ad[(k + j * (l - 1)) - 1] != 0.0D) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                                       // "KalmanFilter.RmnElmntsNotZero",
                                                       // null);
                {
                    throw new IllegalArgumentException("RmnElmntsNotZero");
                }
            }

        }

        ai[0]++;
    }

    private double l_a1ot(int i, double ad[], int j, int k, double ad1[], int l, int i1)
    {
        double d = 0.0D;
        if (i > 0)
        {
            if (k != 1 || i1 != 1)
            {
                int l1 = 1;
                int i2 = 1;
                if (k < 0)
                {
                    l1 = (-i + 1) * k + 1;
                }
                if (i1 < 0)
                {
                    i2 = (-i + 1) * i1 + 1;
                }
                for (int j1 = 1; j1 <= i; j1++)
                {
                    d += Math.abs(ad[(j + l1) - 1] * ad1[(l + i2) - 1]);
                    l1 += k;
                    i2 += i1;
                }

            }
            else
            {
                for (int k1 = 1; k1 <= i; k1++)
                {
                    d += Math.abs(ad[(j + k1) - 1] * ad1[(l + k1) - 1]);
                }

            }
        }
        return d;
    }

    private int l_isanan(int i, double ad[], int j)
    {
        int l = 0;
        int i1 = 1;
        int k = 1;
        do
        {
            if (i1 > i)
            {
                break;
            }
            if (Double.isNaN(ad[k - 1]))
            {
                l = i1;
                break;
            }
            k += j;
            i1++;
        }
        while (true);
        return l;
    }

    private void l_trsv(char c, char c1, char c2, int i, double ad[], int j, double ad1[], int k, int l)
    {
        if (i == 0)
        {
            return;
        }
        boolean flag = c2 == 'N' || c2 == 'n';
        boolean flag2 = c == 'U' || c == 'u';
        boolean flag1 = c1 == 'T' || c1 == 't' || c1 == 'C' || c1 == 'c';
        if (flag2)
        {
            if (flag1)
            {
                if (l > 0)
                {
                    int i3 = 1;
                    for (int i1 = 1; i1 <= i; i1++)
                    {
                        ad1[(k + i3) - 1] -= BLAS.dot(i1 - 1, ad, j * (i1 - 1), 1, ad1, k, l);
                        if (flag)
                        {
                            ad1[(k + i3) - 1] /= ad[(i1 + j * (i1 - 1)) - 1];
                        }
                        i3 += l;
                    }

                }
                else
                {
                    int j3 = (-i + 1) * l + 1;
                    for (int j1 = 1; j1 <= i; j1++)
                    {
                        ad1[(k + j3) - 1] -= BLAS.dot(j1 - 1, ad, j * (j1 - 1), 1, ad1, (k + j3) - l - 1, l);
                        if (flag)
                        {
                            ad1[(k + j3) - 1] /= ad[(j1 + j * (j1 - 1)) - 1];
                        }
                        j3 += l;
                    }

                }
            }
            else if (l > 0)
            {
                int k3 = (i - 1) * l + 1;
                for (int k1 = i; k1 >= 1; k1--)
                {
                    if (k1 < i)
                    {
                        ad1[(k + k3) - 1] -= BLAS.dot(i - k1, ad, (k1 + j * k1) - 1, j, ad1, (k + k3 + l) - 1, l);
                    }
                    if (flag)
                    {
                        ad1[(k + k3) - 1] /= ad[(k1 + j * (k1 - 1)) - 1];
                    }
                    k3 -= l;
                }

            }
            else
            {
                int l3 = 1;
                for (int l1 = i; l1 >= 1; l1--)
                {
                    if (l1 < i)
                    {
                        ad1[(k + l3) - 1] -= BLAS.dot(i - l1, ad, (l1 + j * l1) - 1, j, ad1, k, l);
                    }
                    if (flag)
                    {
                        ad1[(k + l3) - 1] /= ad[(l1 + j * (l1 - 1)) - 1];
                    }
                    l3 -= l;
                }

            }
        }
        else if (flag1)
        {
            if (l > 0)
            {
                int i4 = (i - 1) * l + 1;
                for (int i2 = i; i2 >= 1; i2--)
                {
                    if (i2 < i)
                    {
                        ad1[(k + i4) - 1] -= BLAS.dot(i - i2, ad, i2 + j * (i2 - 1), 1, ad1, (k + i4 + l) - 1, l);
                    }
                    if (flag)
                    {
                        ad1[(k + i4) - 1] /= ad[(i2 + j * (i2 - 1)) - 1];
                    }
                    i4 -= l;
                }

            }
            else
            {
                int j4 = 1;
                for (int j2 = i; j2 >= 1; j2--)
                {
                    if (j2 < i)
                    {
                        ad1[(k + j4) - 1] -= BLAS.dot(i - j2, ad, j2 + j * (j2 - 1), 1, ad1, k, l);
                    }
                    if (flag)
                    {
                        ad1[(k + j4) - 1] /= ad[(j2 + j * (j2 - 1)) - 1];
                    }
                    j4 -= l;
                }

            }
        }
        else if (l > 0)
        {
            int k4 = 1;
            for (int k2 = 1; k2 <= i; k2++)
            {
                ad1[(k + k4) - 1] -= BLAS.dot(k2 - 1, ad, k2 - 1, j, ad1, k, l);
                if (flag)
                {
                    ad1[(k + k4) - 1] /= ad[(k2 + j * (k2 - 1)) - 1];
                }
                k4 += l;
            }

        }
        else
        {
            int l4 = (-i + 1) * l + 1;
            for (int l2 = 1; l2 <= i; l2++)
            {
                ad1[(k + l4) - 1] -= BLAS.dot(l2 - 1, ad, l2 - 1, j, ad1, (k + l4) - l - 1, l);
                if (flag)
                {
                    ad1[(k + l4) - 1] /= ad[(l2 + j * (l2 - 1)) - 1];
                }
                l4 += l;
            }

        }
    }

    private void l_gemv(char c, int i, int j, int k, double d, double ad[], int l, double ad1[], int i1, int j1,
            double d1, double ad2[], int k1, int l1)
    {
        boolean flag1 = c == 'N' || c == 'n';
        boolean flag2 = c == 'T' || c == 't';
        boolean flag = c == 'C' || c == 'c';
        if (j == 0 || k == 0 || d == 0.0D && d1 == 1.0D)
        {
            return;
        }
        int k3;
        int l3;
        if (flag1)
        {
            k3 = k;
            l3 = j;
        }
        else
        {
            k3 = j;
            l3 = k;
        }
        int k2 = 1;
        int l2 = 1;
        if (j1 < 0)
        {
            k2 = (-k3 + 1) * j1 + 1;
        }
        if (l1 < 0)
        {
            l2 = (-l3 + 1) * l1 + 1;
        }
        if (d1 != 1.0D)
        {
            if (l1 == 0)
            {
                if (d1 == 0.0D)
                {
                    ad2[k1 + 0] = 0.0D;
                }
                else
                {
                    ad2[k1 + 0] *= Math.pow(d1, l3);
                }
            }
            else if (d1 == 0.0D)
            {
                BLAS.set(l3, 0.0D, ad2, k1, Math.abs(l1));
            }
            else
            {
                BLAS.scal(l3, d1, ad2, k1, Math.abs(l1));
            }
        }
        if (d == 0.0D)
        {
            return;
        }
        if (flag1)
        {
            int i3 = k2;
            for (int i2 = 1; i2 <= k; i2++)
            {
                BLAS.axpy(j, d * ad1[(i1 + i3) - 1], ad, l * (i2 - 1), 1, ad2, k1, l1);
                i3 += j1;
            }

        }
        else
        {
            int j3 = l2;
            for (int j2 = 1; j2 <= k; j2++)
            {
                ad2[(k1 + j3) - 1] += d * BLAS.dot(j, ad, l * (j2 - 1), 1, ad1, i1, j1);
                j3 += l1;
            }

        }
    }

    private void l_gemm(char c, int i, char c1, int j, int k, int l, int i1, double d, double ad[], int j1,
            double ad1[], int k1, double d1, double ad2[], int l1)
    {
        boolean flag = c == 'N' || c == 'n';
        boolean flag1 = c1 == 'N' || c1 == 'n';
        boolean flag2 = c == 'T' || c == 't' || c == 'C' || c == 'c';
        boolean flag3 = c1 == 'T' || c1 == 't' || c1 == 'C' || c1 == 'c';
        if (k == 0 || l == 0 || (d == 0.0D || i1 == 0) && d1 == 1.0D)
        {
            return;
        }
        if (d1 == 0.0D)
        {
            for (int l3 = 1; l3 <= l; l3++)
            {
                for (int i2 = 1; i2 <= k; i2++)
                {
                    ad2[(l3 - 1) * l1 + (i2 - 1)] = 0.0D;
                }

            }

        }
        else if (d1 == -1D)
        {
            for (int i4 = 1; i4 <= l; i4++)
            {
                for (int j2 = 1; j2 <= k; j2++)
                {
                    ad2[(i4 - 1) * l1 + (j2 - 1)] = -ad2[(i4 - 1) * l1 + (j2 - 1)];
                }

            }

        }
        else if (d1 != 1.0D)
        {
            for (int j4 = 1; j4 <= l; j4++)
            {
                for (int k2 = 1; k2 <= k; k2++)
                {
                    ad2[(j4 - 1) * l1 + (k2 - 1)] *= d1;
                }

            }

        }
        if (i1 == 0 || d == 0.0D)
        {
            return;
        }
        if (flag1 && flag)
        {
            for (int k5 = 1; k5 <= i1; k5++)
            {
                for (int k4 = 1; k4 <= l; k4++)
                {
                    double d2 = d * ad1[(k4 - 1) * k1 + (k5 - 1)];
                    for (int l2 = 1; l2 <= k; l2++)
                    {
                        ad2[(k4 - 1) * l1 + (l2 - 1)] += d2 * ad[(k5 - 1) * j1 + (l2 - 1)];
                    }

                }

            }

        }
        else if (flag1 && flag2)
        {
            for (int l5 = 1; l5 <= i1; l5++)
            {
                for (int l4 = 1; l4 <= l; l4++)
                {
                    double d3 = d * ad1[(l4 - 1) * k1 + (l5 - 1)];
                    for (int i3 = 1; i3 <= k; i3++)
                    {
                        ad2[(l4 - 1) * l1 + (i3 - 1)] += d3 * ad[(i3 - 1) * j1 + (l5 - 1)];
                    }

                }

            }

        }
        else if (flag3 && flag2)
        {
            for (int i6 = 1; i6 <= i1; i6++)
            {
                for (int i5 = 1; i5 <= l; i5++)
                {
                    double d4 = d * ad1[(i6 - 1) * k1 + (i5 - 1)];
                    for (int j3 = 1; j3 <= k; j3++)
                    {
                        ad2[(i5 - 1) * l1 + (j3 - 1)] += d4 * ad[(j3 - 1) * j1 + (i6 - 1)];
                    }

                }

            }

        }
        else if (flag3 && flag)
        {
            for (int j6 = 1; j6 <= i1; j6++)
            {
                for (int j5 = 1; j5 <= l; j5++)
                {
                    double d5 = d * ad1[(j6 - 1) * k1 + (j5 - 1)];
                    for (int k3 = 1; k3 <= k; k3++)
                    {
                        ad2[(j5 - 1) * l1 + (k3 - 1)] += d5 * ad[(j6 - 1) * j1 + (k3 - 1)];
                    }

                }

            }

        }
    }

    private void l_ger(int i, int j, double d, double ad[], int k, int l, double ad1[], int i1, int j1, double ad2[],
            int k1, int l1)
    {
        if (i == 0 || j == 0 || d == 0.0D)
        {
            return;
        }
        int j2 = 1;
        if (j1 < 0)
        {
            j2 = (-j + 1) * j1 + 1;
        }
        int i2 = 1;
        for (int k2 = 1; k2 <= j; k2++)
        {
            BLAS.axpy(i, d * ad1[(i1 + j2) - 1], ad, k, l, ad2, (k1 + i2) - 1, 1);
            j2 += j1;
            i2 += l1;
        }

    }

    public double[] getCovB()
    {
        if (l_covb == null)
        {
            return null;
        }
        else
        {
            return (double[]) l_covb.clone();
        }
    }

    public double[] getStateVector()
    {
        if (l_b == null)
        {
            return null;
        }
        else
        {
            return (double[]) l_b.clone();
        }
    }

    public int getRank()
    {
        return l_n[0];
    }

    public double getSumOfSquares()
    {
        return l_ss[0];
    }

    public double getLogDeterminant()
    {
        return l_alndet[0];
    }

    public double[] getPredictionError()
    {
        if (l_v != null)
        {
            return (double[]) l_v.clone();
        }
        else
        {
            return null;
        }
    }

    public double[][] getCovV()
    {
        if (l_covv != null)
        {
            double ad[][] = new double[l_ny][l_ny];
            for (int i = 0; i < l_ny; i++)
            {
                BLAS.copy(l_ny, l_covv, i * l_ny, 1, ad[i], 0, 1);
            }

            return ad;
        }
        else
        {
            return (double[][]) null;
        }
    }

    public void update(double ad[], double ad1[][], double ad2[][])
    {
        if (ad.length < 1)
        {
            Object aobj[] =
            {
                    "y.length", new Integer(0)
            };
            throw new IllegalArgumentException("NotLargeEnough");

        }
        if (ad.length > 0 && l_isanan(ad.length, ad, 1) != 0)
        {
            Object aobj1[] =
            {
                    "y[" + (l_isanan(ad.length, ad, 1) - 1) + "]", "NaN"
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "CannotBe", aobj1);
            throw new IllegalArgumentException("CannotBe");
        }
        l_y = (double[]) ad.clone();
        l_ny = ad.length;
        new Matrix(ad1);// Matrix.checkMatrix(ad1);
        if (ad1.length != l_ny)
        {
            Object aobj2[] =
            {
                    "z", new Integer(l_ny)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj2);
            throw new IllegalArgumentException("NotEqual");
        }
        if (ad1[0].length != l_nb)
        {
            Object aobj3[] =
            {
                    "z[0]", new Integer(l_nb)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj3);
            throw new IllegalArgumentException("NotEqual");
        }
        l_z = new double[l_ny * l_nb];
        for (int i = 0; i < ad1.length; i++)
        {
            BLAS.copy(ad1[i].length, ad1[i], 0, 1, l_z, i * ad1[i].length, 1);
        }

        new Matrix(ad2);// Matrix.checkMatrix(ad2);
        if (ad2.length != l_ny)
        {
            Object aobj4[] =
            {
                    "r", new Integer(l_ny)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj4);
            throw new IllegalArgumentException("NotEqual");
        }
        if (ad2[0].length != l_ny)
        {
            Object aobj5[] =
            {
                    "r[0]", new Integer(l_ny)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj5);
            throw new IllegalArgumentException("NotEqual");
        }
        l_r = new double[l_ny * l_ny];
        for (int j = 0; j < ad2.length; j++)
        {
            BLAS.copy(ad2[j].length, ad2[j], 0, 1, l_r, j * ad2[j].length, 1);
        }

        l_update = 1;
    }

    public void setQ(double ad[][])
    {
        new Matrix(ad);// Matrix.checkMatrix(ad);
        if (ad.length != l_nb)
        {
            Object aobj[] =
            {
                    "q", new Integer(l_nb)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj);
            throw new IllegalArgumentException("NotEqual");
        }
        if (ad[0].length != l_nb)
        {
            Object aobj1[] =
            {
                    "q[0]", new Integer(l_nb)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj1);
            throw new IllegalArgumentException("NotEqual");
        }
        l_q = new double[l_nb * l_nb];
        for (int i = 0; i < ad.length; i++)
        {
            BLAS.copy(ad[i].length, ad[i], 0, 1, l_q, i * ad[i].length, 1);
        }

        l_iq = 0;
    }

    public void setTransitionMatrix(double ad[][])
    {
        new Matrix(ad);// Matrix.checkMatrix(ad);
        if (ad.length != l_nb)
        {
            Object aobj[] =
            {
                    "t", new Integer(l_nb)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj);
            throw new IllegalArgumentException("NotEqual");
        }
        if (ad[0].length != l_nb)
        {
            Object aobj1[] =
            {
                    "t[0]", new Integer(l_nb)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj1);
            throw new IllegalArgumentException("NotEqual");
        }
        l_t = new double[l_nb * l_nb];
        for (int i = 0; i < ad.length; i++)
        {
            BLAS.copy(ad[i].length, ad[i], 0, 1, l_t, i * ad[i].length, 1);
        }

        l_it = 0;
    }

    public void setTolerance(double d)
    {
        if (d < 0.0D || d > 1.0D)
        {
            Object aobj[] =
            {
                    "tolerance", new Double(d), new Double(0.0D), new Double(1.0D)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "OutOfRange", aobj);
            throw new IllegalArgumentException("OutOfRange");
        }
        l_tol = d;
    }
}
