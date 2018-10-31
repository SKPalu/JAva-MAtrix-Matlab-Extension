/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 *
 * @author Feynman Perceptrons
 */
//public class Garch {

//}

import java.io.Serializable;

import jamaextension.jamax.Matrix;

public class Garch implements Serializable, Cloneable
{

    static final long serialVersionUID = 0x74108c8a81b1e2b6L;

    private int l_p;
    private int l_q;
    private int l_m;
    private double l_y[];
    private double l_xguess[];
    private double lv_coef_vector[];
    private double l_var[];
    private double l_a;
    private double l_aic;
    private double l_max_sigma;

    public Garch(int i, int j, double ad[], double ad1[])
    {
        if (i < 0)
        {
            Object aobj[] =
            {
                    "p", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "Negative", aobj);
            throw new IllegalArgumentException("Negative");
        }
        l_p = i;
        if (j < 0)
        {
            Object aobj1[] =
            {
                    "q", new Integer(j)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "Negative", aobj1);
            throw new IllegalArgumentException("Negative");
        }
        l_q = j;
        if (ad.length <= 0)
        {
            Object aobj2[] =
            {
                    "y.length", new Integer(ad.length)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotPositive", aobj2);
            throw new IllegalArgumentException("NotPositive");
        }
        l_m = ad.length;
        l_y = (double[]) ad.clone();
        if (ad1.length != i + j + 1)
        {
            Object aobj3[] =
            {
                    "xguess", new Integer(ad1.length), "p+q+1"
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotEqual", aobj3);
            throw new IllegalArgumentException("NotEqual");
        }
        l_xguess = (double[]) ad1.clone();
        if (l_m < i + j + 1)
        {
            Object aobj4[] =
            {
                    "y.length", new Integer(ad.length), "p+q+1"
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj4);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        l_max_sigma = 10D;
    }

    public final void compute() // throws ConstrInconsistentException,
                                // EqConstrInconsistentException,
                                // NoVectorXException,
                                // TooManyIterationsException,
                                // VarsDeterminedException
    {
        int i = l_p;
        int j = l_q;
        int k = l_m;
        double ad[] = l_y;
        double ad1[] = l_xguess;
        boolean flag = false;
        boolean flag1 = false;
        int l = i + j + 1;
        int i1 = i + j + 1;
        double ad3[] =
        {
            0.0D
        };
        double ad4[] =
        {
            0.0D
        };
        double d = l_max_sigma;
        lv_coef_vector = new double[i1];
        l = i1;
        double ad2[] = new double[i1 * l];
        double ad6[] = new double[i1];
        double ad5[] = new double[i1];
        int ai[] = new int[2 * i1 + 3];
        double ad7[] = new double[6 * i1 + 3];
        double ad8[] = new double[i1 * i1 + 11 * i1 + 3];
        double ad9[] = new double[k];
        double ad10[] = new double[k];
        ad6[0] = 2.2204460492503131E-016D;
        ad5[0] = d;
        for (int j1 = 1; j1 < i1; j1++)
        {
            ad6[j1] = 0.0D;
            ad5[j1] = 1.0D;
        }

        fgarch(i, j, k, lv_coef_vector, ad, ad1, ad6, ad5, ad3, ad4, ad2, i1, ai, ad7, i1, i1 * 2, i1 * 3, i1 * 3 + i1
                * 3, ad9, ad10, ad8);
        l_a = ad3[0];
        l_aic = ad4[0];
        l_m1ran(l, i1, ad2, ad2);
        l_var = ad2;
    }

    private void fgarch(int i, int j, int k, double ad[], double ad1[], double ad2[], double ad3[], double ad4[],
            double ad5[], double ad6[], double ad7[], int l, int ai[], double ad8[], int i1, int j1, int k1, int l1,
            double ad9[], double ad10[], double ad11[]) // throws
                                                        // ConstrInconsistentException,
                                                        // EqConstrInconsistentException,
                                                        // NoVectorXException,
                                                        // TooManyIterationsException,
                                                        // VarsDeterminedException
    {
        int ai1[] =
        {
            0
        };
        int ai2[] =
        {
            0
        };
        int ai3[] =
        {
            0
        };
        double ad12[] =
        {
            0.0D
        };
        int i5 = i + j + 1;
        int k5 = i + j + 1;
        int j5 = k;
        byte byte2;
        if (i + j == 0)
        {
            byte2 = 1;
        }
        else
        {
            byte2 = 3;
        }
        double d4 = 0.0D;
        for (int k2 = 2; k2 <= i + j + 1; k2++)
        {
            int l3 = k2 - 1;
            d4 += ad2[l3];
        }

        if (d4 >= 1.0D)
        {
            throw new IllegalArgumentException("SumLTOne");
        }
        double d3 = 0.0D;
        for (int l2 = 2; l2 <= i + j + 1; l2++)
        {
            int i4 = l2 - 1;
            d3 += ad3[i4];
        }

        BLAS.copy(i + j + 1, ad2, 0, 1, ad, 0, 1);
        for (int i3 = 1; i3 <= k5; i3++)
        {
            int j4 = i3 - 1;
            ad8[j1 + j4] = 1.0D;
        }

        double d2 = 1.0D;
        int l5 = k5;
        byte byte1 = byte2;
        ad8[k1 + 0] = -1D;
        ad8[l1 + 0] = 0.001D;
        if (i + j > 0)
        {
            for (int j3 = 2; j3 <= k5; j3++)
            {
                int k4 = j3 - 1;
                ad8[k1 + k4 * 3] = 0.0D;
            }

            ad8[k1 + 1] = 0.0D;
            ad8[k1 + 2] = 0.0D;
            for (int k3 = 2; k3 <= k5; k3++)
            {
                int l4 = k3 - 1;
                ad8[k1 + l4 * 3 + 1] = 1.0D;
                ad8[k1 + l4 * 3 + 2] = -1D;
            }

            ad8[l1 + 1] = 0.98999999999999999D;
            ad8[l1 + 2] = 0.001D;
        }
        double d1;
        if (k <= 199)
        {
            d1 = 10D;
        }
        else
        {
            d1 = 0.001D;
        }
        ai3[0] = 1000;
        byte byte0 = 3;
        int i2 = 0;
        int j2 = 0;
        l_l2onf(i, j, j5, ad1, l5, byte2, i2, k1, byte0, l1, ad3, ad4, ad2, d1, ai3, ad, ad12, ai2, ai, ad8, j2, ai1,
                ad11, ad9);
        l_lfcn(i, j, k, ad, ad1, ad5, ad9);
        ad5[0] = -ad5[0];
        ad6[0] = -2D * ad5[0] + 2D * (double) k5;
        lgrad(i, j, k, ad, ad1, ad8, i1, ad9, ad10);
        double d = Math.sqrt(2.2204460492503131E-016D);
        l_g2hes(i, j, k, ad1, ad, ad8, j1, i1, d, ad7, l, ad11, ad9, ad10);
        if (k < 200) // Warning.print(this, "com.imsl.stat",
                     // "GARCH.SmallSeries", null);
        {
            System.out.println("Warning :- SmallSeries");
        }
    }

    private void l_l2onf(int i, int j, int k, double ad[], int l, int i1, int j1, int k1, int l1, int i2, double ad1[],
            double ad2[], double ad3[], double d, int ai[], double ad4[], double ad5[], int ai1[], int ai2[],
            double ad6[], int j2, int ai3[], double ad7[], double ad8[]) // throws
                                                                         // ConstrInconsistentException,
                                                                         // EqConstrInconsistentException,
                                                                         // NoVectorXException,
                                                                         // TooManyIterationsException,
                                                                         // VarsDeterminedException
    {
        BLAS.copy(l, ad3, 0, 1, ad4, 0, 1);
        int i3 = 1;
        int l3 = i3 + l;
        int l4 = l3 + l;
        int i4 = l4 + l * l;
        int j4 = i4 + l;
        int k2 = j4 + l;
        int l2 = k2 + i1 + l + l;
        int i5 = l2 + l;
        int j3 = i5 + l;
        int k4 = j3 + l;
        int k3 = k4 + l;
        ai3[0] = 0;
        l_l3onf(i, j, k, ad, l, i1, j1, k1, l1, i2, ad1, ad2, ad4, ad5, d, ai, ai2, ai1, ad6, j2, ai3, ad7, i3 - 1,
                l4 - 1, i4 - 1, j4 - 1, l3 - 1, k2 - 1, l2 - 1, i5 - 1, j3 - 1, k4 - 1, k3 - 1, ad8);
        if (ai3[0] == 5) // throw new
                         // ConstrInconsistentException("GARCH.ConstrInconsistent",
                         // null);
        {
            throw new IllegalArgumentException("ConstrInconsistent");
        }
        if (ai3[0] == 6) // throw new
                         // EqConstrInconsistentException("GARCH.EqConstrInconsistent",
                         // null);
        {
            throw new IllegalArgumentException("EqConstrInconsistent");
        }
        if (ai3[0] == 7) // throw new NoVectorXException("GARCH.NoVectorX",
                         // null);
        {
            throw new IllegalArgumentException("NoVectorX");
        }
        if (ai3[0] == 8)
        {
            Object aobj[] =
            {
                new Integer(ai[0])
            };
            // throw new TooManyIterationsException("GARCH.TooManyIterations",
            // aobj);
            throw new IllegalArgumentException("TooManyIterations");
        }
        if (ai3[0] == 9) // throw new
                         // VarsDeterminedException("GARCH.VarsDetermined",
                         // null);
        {
            throw new IllegalArgumentException("VarsDetermined");
        }
        else
        {
            return;
        }
    }

    private void l_l3onf(int i, int j, int k, double ad[], int l, int i1, int j1, int k1, int l1, int i2, double ad1[],
            double ad2[], double ad3[], double ad4[], double d, int ai[], int ai1[], int ai2[], double ad5[], int j2,
            int ai3[], double ad6[], int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4, int k4, int l4,
            int i5, double ad7[])
    {
        int ai7[];
        label0:
        {
            int ai4[] =
            {
                0
            };
            int ai5[] =
            {
                0
            };
            int ai6[] =
            {
                0
            };
            ai7 = (new int[]
            {
                0
            });
            double ad8[] =
            {
                0.0D
            };
            double ad9[] =
            {
                0.0D
            };
            double ad10[] =
            {
                0.0D
            };
            ad8[0] = -1D;
            ai4[0] = 0;
            ai7[0] = 0;
            int l6 = ai[0];
            ai3[0] = 4;
            l_l4onf(l, i1, ad1, ad2, ad3, ai1, ai6, ai3, ad6, l2, i3, j3, ad10);
            ad9[0] = Math.max(0.01D, 10D * ad10[0]);
            if (j1 > 0)
            {
                l_l5onf(l, i1, j1, ad5, k1, l1, i2, ad2, ai1, ai6, ai3, ad6, l2, i3, ad10[0], l4, i5);
                if (ai3[0] == 5)
                {
                    ai[0] = ai7[0];
                }
                return;
            }
            ai2[0] = ai6[0];
            ai5[0] = ai6[0];
            int k6 = ai2[0];
            for (int j5 = 1; j5 <= l; j5++)
            {
                int k5 = j5 - 1;
                if (ad1[k5] < ad2[k5])
                {
                    k6 += 2;
                    ai1[k6 - 2] = i1 + j5;
                    ai1[k6 - 1] = i1 + l + j5;
                }
            }

            l_l6onf(l, i1, k1, l1, i2, ad1, ad2, ad3, ai1, ai2, ad5, ai3, ad6, k2, l2, i3, j3, ad10[0], ad9, ai6[0],
                    ai5, k6, l3, i4, j4, k4, k3, l4, i5);
            if (ai5[0] < k6)
            {
                ai3[0] = 6;
                ai[0] = ai7[0];
                return;
            }
            if (i1 > j1)
            {
                int j6 = j1 + 1;
                for (int l5 = j6; l5 <= i1; l5++)
                {
                    int i6 = l5 - 1;
                    k6++;
                    ai1[k6 - 1] = l5;
                }

            }
            do
            {
                l_l6onf(l, i1, k1, l1, i2, ad1, ad2, ad3, ai1, ai2, ad5, ai3, ad6, k2, l2, i3, j3, ad10[0], ad9,
                        ai6[0], ai5, k6, l3, i4, j4, k4, k3, l4, i5);
                if (ai5[0] < k6)
                {
                    ai3[0] = 7;
                    ai[0] = ai7[0];
                    return;
                }
                if (ai6[0] == l)
                {
                    ai3[0] = 9;
                    ai[0] = ai7[0];
                    return;
                }
                l_l7onf(i, j, k, ad, l, i1, k1, l1, i2, ad1, ad2, ad3, ad4, d, ai1, ai2, ad5, j2, ai3, ad6, k2, l2, i3,
                        j3, ad10[0], ad8, ad9[0], ai6[0], k6, ai4, ai7, l6, k3, l3, i4, j4, k4, l4, i5, ad7);
                if (ad9[0] <= ad10[0] || ai2[0] <= 0)
                {
                    break label0;
                }
                if (ai7[0] == l6)
                {
                    break;
                }
                l_l8onf(l, i1, ad5, k1, l1, i2, ad1, ad2, ad3, ai1, ai2[0], ad6, j3, ad10[0], ad9, ai6[0]);
            }
            while (true);
            ai3[0] = 8;
        }
        ai[0] = ai7[0];
    }

    private void l_l4onf(int i, int j, double ad[], double ad1[], double ad2[], int ai[], int ai1[], int ai2[],
            double ad3[], int k, int l, int i1, double ad4[])
    {
        double d2 = 100D;
        ad4[0] = 1.0D;
        double d;
        double d1;
        do
        {
            ad4[0] *= 0.5D;
            d = d2 + 0.5D * ad4[0];
            d1 = d2 + ad4[0];
        }
        while (d2 < d && d < d1);
        ai1[0] = 0;
        for (int j1 = 1; j1 <= i; j1++)
        {
            int i2 = j1 - 1;
            if (ad[i2] > ad1[i2])
            {
                return;
            }
            if (ad[i2] == ad1[i2])
            {
                ai1[0]++;
            }
        }

        int j3 = 0;
        int k3 = i * i;
        for (int k1 = 1; k1 <= k3; k1++)
        {
            int j2 = k1 - 1;
            ad3[k + j2] = 0.0D;
        }

        int l2 = 0;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int k2 = l1 - 1;
            int i3;
            if (ad[k2] == ad1[k2])
            {
                ad2[k2] = ad1[k2];
                j3++;
                ad3[(l + j3) - 1] = 1.0D;
                ai[j3 - 1] = l1 + j + i;
                i3 = j3;
            }
            else
            {
                i3 = (l1 + ai1[0]) - j3;
            }
            ad3[(k + l2 + i3) - 1] = 1.0D;
            l2 += i;
            ad3[i1 + k2] = Math.abs(ad2[k2]);
        }

        ai2[0] = 1;
    }

    private void l_l5onf(int i, int j, int k, double ad[], int l, int i1, int j1, double ad1[], int ai[], int ai1[],
            int ai2[], double ad2[], int k1, int l1, double d, int i2, int j2)
    {
        for (int i5 = 1; i5 <= k; i5++)
        {
            int j5 = i5 - 1;
            if (ai1[0] < i)
            {
                int k5 = ai1[0] + 1;
                ai[k5 - 1] = i5;
                l_l9onf(i, j, ad, l, i1, ai, ai1, ad2, k1, l1, d, k5, i2, j2);
                if (ai1[0] == k5)
                {
                    continue;
                }
            }
            double d2 = ad[j1 + j5];
            double d3 = Math.abs(ad[j1 + j5]);
            if (ai1[0] > 0)
            {
                for (int k2 = 1; k2 <= i; k2++)
                {
                    int j3 = k2 - 1;
                    ad2[i2 + j3] = ad[l + j3 * i1 + j5];
                }

                int l4 = ai1[0];
                do
                {
                    double d4 = 0.0D;
                    int i4 = l4;
                    for (int l2 = 1; l2 <= i; l2++)
                    {
                        int k3 = l2 - 1;
                        d4 += ad2[(k1 + i4) - 1] * ad2[i2 + k3];
                        i4 += i;
                    }

                    d4 *= ad2[(l1 + l4) - 1];
                    int j4 = ai[l4 - 1];
                    double d1;
                    if (j4 <= j)
                    {
                        for (int i3 = 1; i3 <= i; i3++)
                        {
                            int l3 = i3 - 1;
                            ad2[i2 + l3] += -d4 * ad[l + l3 * i1 + (j4 - 1)];
                        }

                        d1 = ad[(j1 + j4) - 1];
                    }
                    else
                    {
                        int k4 = j4 - j - i;
                        ad2[(i2 + k4) - 1] -= d4;
                        d1 = ad1[k4 - 1];
                    }
                    d2 += -d1 * d4;
                    d3 += Math.abs(d1 * d4);
                }
                while (--l4 >= 1);
            }
            if (Math.abs(d2) > d * d3)
            {
                ai2[0] = 5;
                return;
            }
        }

    }

    private void l_l6onf(int i, int j, int k, int l, int i1, double ad[], double ad1[], double ad2[], int ai[],
            int ai1[], double ad3[], int ai2[], double ad4[], int j1, int k1, int l1, int i2, double d, double ad5[],
            int j2, int ai3[], int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4)
    {
        int ai4[] =
        {
            0
        };
        double ad6[] =
        {
            0.0D
        };
        double ad7[] =
        {
            0.0D
        };
        int j5 = 0;
        int i5 = 0;
        double d1 = 0.0D;
        ai2[0] = 0;
        boolean flag1;
        boolean flag = flag1 = true;
        do
        {
            if (flag)
            {
                l_l10nf(i, j, ad3, k, l, i1, ad, ad1, ad2, ai, ai1, ai2, ad4, k1, l1, i2, d, ad5[0], j2);
                if (ai2[0] > 0)
                {
                    ai3[0] = ai1[0];
                }
                if (ai3[0] == k2)
                {
                    return;
                }
            }
            if (flag1)
            {
                j5 = ai3[0];
                d1 = 0.0D;
            }
            l_l11nf(i, j, k, l, i1, ad, ad1, ad2, ai, ai1, ad3, ad4, j1, k1, l1, i2, l2, i3, j3, d, ad5[0], ad6, ad7,
                    j2, ai3, k2, ai4, k3, l3, i4, j4);
            if (ad6[0] > 0.0D)
            {
                for (int k4 = 1; k4 <= i; k4++)
                {
                    int l4 = k4 - 1;
                    ad2[l4] += ad6[0] * ad4[i3 + l4];
                    ad4[i2 + l4] = Math.max(ad4[i2 + l4], Math.abs(ad2[l4]));
                }

                l_l9onf(i, j, ad3, k, l, ai, ai1, ad4, k1, l1, d, ai4[0], l3, j4);
            }
            boolean flag2 = false;
            if (ai3[0] >= k2)
            {
                break;
            }
            if (ad6[0] == 0.0D)
            {
                flag2 = true;
            }
            if (!flag2)
            {
                if (j5 < ai3[0])
                {
                    flag = false;
                    flag1 = true;
                    continue;
                }
                if (d1 == 0.0D || ad7[0] < d1)
                {
                    d1 = ad7[0];
                    i5 = 0;
                }
                if (++i5 <= 2)
                {
                    flag = false;
                    flag1 = false;
                    continue;
                }
            }
            if (ad5[0] <= d)
            {
                break;
            }
            l_l8onf(i, j, ad3, k, l, i1, ad, ad1, ad2, ai, ai1[0], ad4, i2, d, ad5, j2);
            flag = flag1 = true;
        }
        while (true);
    }

    private void l_l7onf(int i, int j, int k, double ad[], int l, int i1, int j1, int k1, int l1, double ad1[],
            double ad2[], double ad3[], double ad4[], double d, int ai[], int ai1[], double ad5[], int i2, int ai2[],
            double ad6[], int j2, int k2, int l2, int i3, double d1, double ad7[], double d2, int j3, int k3,
            int ai3[], int ai4[], int l3, int i4, int j4, int k4, int l4, int i5, int j5, int k5, double ad8[])
    {
        int ai5[] =
        {
            0
        };
        int ai6[] =
        {
            0
        };
        double ad9[] =
        {
            0.0D
        };
        double ad10[] =
        {
            0.0D
        };
        double ad11[] =
        {
            0.0D
        };
        double ad12[] =
        {
            0.0D
        };
        double ad13[] =
        {
            0.0D
        };
        double ad14[] =
        {
            0.0D
        };
        ai5[0] = k3;
        int l6 = ai3[0];
        int k7 = ai4[0];
        double d3 = 0.0D;
        if (ai4[0] == 0 || ai2[0] == 1)
        {
            l_lfcn(i, j, k, ad3, ad, ad9, ad8);
            ad4[0] = ad9[0];
            l_l21nf(i, j, k, ad, l, ad3, ad9[0], ad6, j2, ad8);
            ai4[0]++;
        }
        double d4 = Math.abs(ad9[0] + ad9[0] + 1.0D);
        byte byte0 = -1;
        do
        {
            do
            {
                l_l11nf(l, i1, j1, k1, l1, ad1, ad2, ad3, ai, ai1, ad5, ad6, j2, k2, l2, i3, j4, k4, l4, d1, d2, ad10,
                        ad11, j3, ai5, k3, ai6, i5, i4, j5, k5);
                l_l12nf(l, i1, j1, k1, ai, ai1[0], ad5, ad6, j2, i4, k2, l2, j4, ad12, j3, ad13, j5, k5);
                if (ad13[0] <= d * d)
                {
                    ai2[0] = 1;
                    return;
                }
                if (ad11[0] >= 0.0D)
                {
                    ai2[0] = 2;
                    return;
                }
                boolean flag = false;
                if (ad9[0] >= d4)
                {
                    if ((d2 == d1 || ai1[0] == 0) && d3 > 0.0D)
                    {
                        flag = true;
                    }
                    if (!flag)
                    {
                        ai2[0] = 3;
                        return;
                    }
                }
                d3 = d4 - ad9[0];
                d4 = ad9[0];
                if (ai4[0] == l3)
                {
                    ai2[0] = 8;
                    return;
                }
                if (d2 > d1 && ai3[0] > l6 && 0.10000000000000001D * ad12[0] >= Math.max(d3, -0.5D * ad11[0]))
                {
                    return;
                }
                if (byte0 == ai3[0])
                {
                    int i7 = ai3[0] + Math.abs(i2);
                    return;
                }
                ai3[0]++;
                l_l13nf(i, j, k, ad, l, ad3, ad6, j2, k4, j5, k5, d1, ad10[0], ad11[0], ad9, ad14, ai4, l3, j4, ad4,
                        ad8);
                if (ad14[0] == 0.0D)
                {
                    ai2[0] = 3;
                    double d5 = 0.0D;
                    for (int l5 = 1; l5 <= l; l5++)
                    {
                        int j6 = l5 - 1;
                        d5 += Math.abs(ad6[k4 + j6] * ad6[k5 + j6]);
                    }

                    if (ad11[0] + d1 * d5 >= 0.0D)
                    {
                        ai2[0] = 2;
                    }
                    return;
                }
                for (int i6 = 1; i6 <= l; i6++)
                {
                    int k6 = i6 - 1;
                    ad6[i3 + k6] = Math.max(ad6[i3 + k6], Math.abs(ad3[k6]));
                }

                l_l14nf(l, ad3, ai1[0], ad6, j2, k2, l4, j5, k5, ad7);
            }
            while (ad14[0] != ad10[0]);
            int j7 = ai[ai6[0] - 1];
            if (j7 > i1)
            {
                j7 -= i1;
                if (j7 <= l)
                {
                    ad3[j7 - 1] = ad1[j7 - 1];
                }
                else
                {
                    ad3[j7 - l - 1] = ad2[j7 - l - 1];
                }
            }
            l_l9onf(l, i1, ad5, j1, k1, ai, ai1, ad6, k2, l2, d1, ai6[0], j5, k5);
        }
        while (true);
    }

    private void l_l8onf(int i, int j, double ad[], int k, int l, int i1, double ad1[], double ad2[], double ad3[],
            int ai[], int j1, double ad4[], int k1, double d, double ad5[], int l1)
    {
        double d3 = 0.0D;
        if (j1 > l1)
        {
            int i4 = l1 + 1;
            for (int k3 = i4; k3 <= j1; k3++)
            {
                int l3 = k3 - 1;
                int i3 = ai[l3];
                double d1;
                double d2;
                if (i3 <= j)
                {
                    d1 = ad[(i1 + i3) - 1];
                    d2 = Math.abs(ad[(i1 + i3) - 1]);
                    for (int i2 = 1; i2 <= i; i2++)
                    {
                        int k2 = i2 - 1;
                        d1 += -ad[k + k2 * l + (i3 - 1)] * ad3[k2];
                        d2 += Math.abs(ad[k + k2 * l + (i3 - 1)] * ad4[k1 + k2]);
                    }

                }
                else
                {
                    int j3 = i3 - j;
                    if (j3 <= i)
                    {
                        d1 = ad3[j3 - 1] - ad1[j3 - 1];
                        d2 = ad4[(k1 + j3) - 1] + Math.abs(ad1[j3 - 1]);
                    }
                    else
                    {
                        j3 -= i;
                        d1 = ad2[j3 - 1] - ad3[j3 - 1];
                        d2 = ad4[(k1 + j3) - 1] + Math.abs(ad2[j3 - 1]);
                    }
                }
                if (d1 > 0.0D)
                {
                    d3 = Math.max(d3, d1 / d2);
                }
            }

        }
        ad5[0] = 0.10000000000000001D * Math.min(ad5[0], d3);
        if (ad5[0] <= d + d)
        {
            ad5[0] = d;
            for (int j2 = 1; j2 <= i; j2++)
            {
                int l2 = j2 - 1;
                ad4[k1 + l2] = Math.abs(ad3[l2]);
            }

        }
    }

    private void l_l9onf(int i, int j, double ad[], int k, int l, int ai[], int ai1[], double ad1[], int i1, int j1,
            double d, int k1, int l1, int i2)
    {
        int i7 = 0;
        int l5 = 0;
        int l8 = ai1[0] + 1;
        int j5 = ai[k1 - 1];
        ai[k1 - 1] = ai[l8 - 1];
        ai[l8 - 1] = j5;
        if (j5 > j)
        {
            int k5 = j5 - j;
            double d4;
            if (k5 <= i)
            {
                d4 = -1D;
            }
            else
            {
                k5 -= i;
                d4 = 1.0D;
            }
            i7 = k5 * i - i;
            for (int j7 = 1; j7 <= i; j7++)
            {
                int i8 = j7 - 1;
                ad1[l1 + i8] = d4 * ad1[i1 + i7 + i8];
            }

        }
        else
        {
            for (int j2 = 1; j2 <= i; j2++)
            {
                int l3 = j2 - 1;
                ad1[i2 + l3] = ad[k + l3 * l + (j5 - 1)];
            }

            for (int k7 = 1; k7 <= i; k7++)
            {
                int j8 = k7 - 1;
                ad1[l1 + j8] = 0.0D;
                int i6 = k7;
                for (int k2 = 1; k2 <= i; k2++)
                {
                    int i4 = k2 - 1;
                    ad1[l1 + j8] += ad1[(i1 + i6) - 1] * ad1[i2 + i4];
                    i6 += i;
                }

            }

        }
        int l7 = i;
        do
        {
            int k8 = l7;
            if (--l7 <= ai1[0])
            {
                break;
            }
            if (ad1[(l1 + k8) - 1] != 0.0D)
            {
                double d5;
                if (Math.abs(ad1[(l1 + k8) - 1]) <= d * Math.abs(ad1[(l1 + l7) - 1]))
                {
                    d5 = Math.abs(ad1[(l1 + l7) - 1]);
                }
                else if (Math.abs(ad1[(l1 + l7) - 1]) <= d * Math.abs(ad1[(l1 + k8) - 1]))
                {
                    d5 = Math.abs(ad1[(l1 + k8) - 1]);
                }
                else
                {
                    d5 = Math.abs(ad1[(l1 + k8) - 1])
                            * Math.sqrt(1.0D + (ad1[(l1 + l7) - 1] / ad1[(l1 + k8) - 1])
                                    * (ad1[(l1 + l7) - 1] / ad1[(l1 + k8) - 1]));
                }
                double d11 = ad1[(l1 + l7) - 1] / d5;
                double d13 = ad1[(l1 + k8) - 1] / d5;
                ad1[(l1 + l7) - 1] = d5;
                int j6 = l7;
                if (j5 > j)
                {
                    for (int l2 = 1; l2 <= i; l2++)
                    {
                        int j4 = l2 - 1;
                        double d6 = d11 * ad1[i1 + j6] - d13 * ad1[(i1 + j6) - 1];
                        ad1[(i1 + j6) - 1] = d11 * ad1[(i1 + j6) - 1] + d13 * ad1[i1 + j6];
                        ad1[i1 + j6] = d6;
                        j6 += i;
                    }

                    ad1[(i1 + i7 + k8) - 1] = 0.0D;
                }
                else
                {
                    double d12 = 0.0D;
                    for (int i3 = 1; i3 <= i; i3++)
                    {
                        int k4 = i3 - 1;
                        double d9 = d11 * ad1[i1 + j6];
                        double d10 = d13 * ad1[(i1 + j6) - 1];
                        double d7 = Math.abs(ad1[i2 + k4]) * (Math.abs(d9) + Math.abs(d10));
                        if (d7 > d12)
                        {
                            d12 = d7;
                            l5 = i3;
                        }
                        ad1[(i1 + j6) - 1] = d11 * ad1[(i1 + j6) - 1] + d13 * ad1[i1 + j6];
                        ad1[i1 + j6] = d9 - d10;
                        j6 += i;
                    }

                    double d1 = 0.0D;
                    j6 = k8;
                    for (int j3 = 1; j3 <= i; j3++)
                    {
                        int l4 = j3 - 1;
                        d1 += ad1[(i1 + j6) - 1] * ad1[i2 + l4];
                        j6 += i;
                    }

                    if (d1 != 0.0D)
                    {
                        int k6 = (l5 * i - i) + k8;
                        ad1[(i1 + k6) - 1] += -d1 / ad1[(i2 + l5) - 1];
                    }
                }
            }
        }
        while (true);
        if (ad1[(l1 + l8) - 1] == 0.0D)
        {
            return;
        }
        if (j5 <= j)
        {
            double d2 = 0.0D;
            double d3 = 0.0D;
            int l6 = l8;
            for (int k3 = 1; k3 <= i; k3++)
            {
                int i5 = k3 - 1;
                double d8 = ad1[(i1 + l6) - 1] * ad1[i2 + i5];
                d2 += d8;
                d3 += Math.abs(d8);
                l6 += i;
            }

            if (Math.abs(d2) <= d * d3)
            {
                return;
            }
        }
        ad1[(j1 + l8) - 1] = 1.0D / ad1[(l1 + l8) - 1];
        ai1[0] = l8;
    }

    private void l_l10nf(int i, int j, double ad[], int k, int l, int i1, double ad1[], double ad2[], double ad3[],
            int ai[], int ai1[], int ai2[], double ad4[], int j1, int k1, int l1, double d, double d1, int i2)
    {
        int i4 = 0;
        double d5 = 0.0D;
        if (ai1[0] == 0)
        {
            return;
        }
        for (int j4 = 1; j4 <= ai1[0]; j4++)
        {
            int k4 = j4 - 1;
            int l3 = ai[k4];
            double d2;
            double d3;
            double d4;
            if (l3 <= j)
            {
                d2 = ad[(i1 + l3) - 1];
                d3 = Math.abs(ad[(i1 + l3) - 1]);
                d4 = d3;
                for (int j2 = 1; j2 <= i; j2++)
                {
                    int l2 = j2 - 1;
                    double d9 = ad[k + l2 * l + (l3 - 1)];
                    double d7 = d9 * ad3[l2];
                    d2 -= d7;
                    d3 += Math.abs(d7);
                    d4 += Math.abs(d9) * ad4[l1 + l2];
                }

            }
            else
            {
                i4 = l3 - j;
                if (i4 <= i)
                {
                    d2 = ad3[i4 - 1] - ad1[i4 - 1];
                    d3 = Math.abs(ad3[i4 - 1]) + Math.abs(ad1[i4 - 1]);
                    d4 = ad4[(l1 + i4) - 1] + Math.abs(ad1[i4 - 1]);
                    d5 = ad1[i4 - 1];
                }
                else
                {
                    i4 -= i;
                    d2 = ad2[i4 - 1] - ad3[i4 - 1];
                    d3 = Math.abs(ad3[i4 - 1]) + Math.abs(ad2[i4 - 1]);
                    d4 = ad4[(l1 + i4) - 1] + Math.abs(ad2[i4 - 1]);
                    d5 = ad2[i4 - 1];
                }
            }
            if (d2 == 0.0D)
            {
                continue;
            }
            double d8 = d2 / d3;
            if (j4 <= i2)
            {
                d8 = -Math.abs(d8);
            }
            if (d1 == d || d8 + d < 0.0D)
            {
                ai2[0] = 1;
                double d6 = d2 * ad4[k1 + k4];
                int k3 = j4;
                for (int k2 = 1; k2 <= i; k2++)
                {
                    int i3 = k2 - 1;
                    ad3[i3] += d6 * ad4[(j1 + k3) - 1];
                    k3 += i;
                    ad4[l1 + i3] = Math.max(ad4[l1 + i3], Math.abs(ad3[i3]));
                }

                if (l3 > j)
                {
                    ad3[i4 - 1] = d5;
                }
                continue;
            }
            if (d2 / d4 > d1)
            {
                ai[k4] = -ai[k4];
            }
        }

        int j3 = ai1[0];
        do
        {
            if (ai[j3 - 1] < 0)
            {
                ai[j3 - 1] = -ai[j3 - 1];
                l_l15nf(i, j, ad, k, l, ai, ai1, ad4, j1, k1, d, j3);
            }
        }
        while (--j3 > i2);
    }

    private void l_l11nf(int i, int j, int k, int l, int i1, double ad[], double ad1[], double ad2[], int ai[],
            int ai1[], double ad3[], double ad4[], int j1, int k1, int l1, int i2, int j2, int k2, int l2, double d,
            double d1, double ad5[], double ad6[], int i3, int ai2[], int j3, int ai3[], int k3, int l3, int i4, int j4)
    {
        double ad7[] =
        {
            0.0D
        };
        int k6 = j3 - ai2[0];
        if ((double) k6 > 0.0D)
        {
            for (int k4 = 1; k4 <= i; k4++)
            {
                int k5 = k4 - 1;
                ad4[j1 + k5] = 0.0D;
            }

            ad6[0] = 0.0D;
        }
        int k8 = ai2[0];
        int j8 = ai1[0];
        ai2[0] = ai1[0];
        int i8 = i3 + 1;
        for (int k7 = i8; k7 <= j3; k7++)
        {
            int l7 = k7 - 1;
            int l6 = ai[l7];
            double d2;
            double d3;
            if (l6 <= j)
            {
                d2 = ad3[(i1 + l6) - 1];
                d3 = Math.abs(ad3[(i1 + l6) - 1]);
                for (int l4 = 1; l4 <= i; l4++)
                {
                    int l5 = l4 - 1;
                    d2 += -ad2[l5] * ad3[k + l5 * l + (l6 - 1)];
                    d3 += Math.abs(ad4[i2 + l5] * ad3[k + l5 * l + (l6 - 1)]);
                }

            }
            else
            {
                int i7 = l6 - j;
                if (i7 <= i)
                {
                    d2 = ad2[i7 - 1] - ad[i7 - 1];
                    d3 = Math.abs(ad4[(i2 + i7) - 1]) + Math.abs(ad[i7 - 1]);
                }
                else
                {
                    i7 -= i;
                    d2 = ad1[i7 - 1] - ad2[i7 - 1];
                    d3 = Math.abs(ad4[(i2 + i7) - 1]) + Math.abs(ad1[i7 - 1]);
                }
            }
            ad4[(j2 + l6) - 1] = d2;
            double d5 = 0.0D;
            if (d3 != 0.0D)
            {
                d5 = d2 / d3;
            }
            if (k7 > k8 && d5 < 0.0D && d5 + d >= 0.0D)
            {
                double d4;
                if (l6 <= j)
                {
                    d4 = Math.abs(ad3[(i1 + l6) - 1]);
                    for (int i5 = 1; i5 <= i; i5++)
                    {
                        int i6 = i5 - 1;
                        d4 += Math.abs(ad2[i6] * ad3[k + i6 * l + (l6 - 1)]);
                    }

                }
                else
                {
                    int j7 = l6 - j;
                    if (j7 <= i)
                    {
                        d4 = Math.abs(ad2[j7 - 1]) + Math.abs(ad[j7 - 1]);
                    }
                    else
                    {
                        d4 = Math.abs(ad2[j7 - i - 1]) + Math.abs(ad1[j7 - i - 1]);
                    }
                }
                if (Math.abs(d2) <= d4 * d)
                {
                    d5 = 0.0D;
                }
            }
            if (k7 <= ai1[0])
            {
                continue;
            }
            if (k7 <= k8 || d5 >= 0.0D)
            {
                ai2[0]++;
                if (ai2[0] < k7)
                {
                    ai[l7] = ai[ai2[0] - 1];
                }
                if (d5 > d1)
                {
                    ai[ai2[0] - 1] = l6;
                }
                else
                {
                    j8++;
                    ai[ai2[0] - 1] = ai[j8 - 1];
                    ai[j8 - 1] = l6;
                }
                continue;
            }
            if (l6 <= j)
            {
                for (int j5 = 1; j5 <= i; j5++)
                {
                    int j6 = j5 - 1;
                    ad4[j1 + j6] += ad3[k + j6 * l + (l6 - 1)];
                }

            }
            else
            {
                l6 -= j;
                if (l6 <= i)
                {
                    ad4[(j1 + l6) - 1]--;
                }
                else
                {
                    ad4[(j1 + l6) - i - 1]++;
                }
            }
            ad6[0] += Math.abs(d2);
        }

        ad5[0] = 0.0D;
        if (k6 > 0 && ai2[0] == j3)
        {
            return;
        }
        l_l16nf(i, j, k, l, ai, ai1, ad3, ad4, j1, k1, l1, k2, l2, d, ad7, i3, j8, k3, l3, i4, j4);
        if (ad7[0] < 0.0D)
        {
            l_l17nf(i, j, ad3, k, l, ai, ad4, j2, k2, ad5, ad7, j8, ai2, j3, ai3);
        }
        if (k6 == 0)
        {
            ad6[0] = ad7[0];
        }
    }

    private void l_l12nf(int i, int j, int k, int l, int ai[], int i1, double ad[], double ad1[], int j1, int k1,
            int l1, int i2, int j2, double ad2[], int k2, double ad3[], int l2, int i3)
    {
        double d = 0.0D;
        for (int j3 = 1; j3 <= i; j3++)
        {
            int i5 = j3 - 1;
            ad1[k1 + i5] = ad1[j1 + i5];
        }

        if (i1 > 0)
        {
            boolean flag = false;
            do
            {
                for (int k9 = 1; k9 <= i1; k9++)
                {
                    int l9 = k9 - 1;
                    int l7 = (i1 + 1) - k9;
                    int i7 = ai[l7 - 1];
                    double d1 = 0.0D;
                    int l6 = l7;
                    for (int k3 = 1; k3 <= i; k3++)
                    {
                        int j5 = k3 - 1;
                        d1 += ad1[(l1 + l6) - 1] * ad1[k1 + j5];
                        l6 += i;
                    }

                    d1 *= ad1[(i2 + l7) - 1];
                    if (!flag)
                    {
                        ad[l7 - 1] = 0.0D;
                    }
                    if (l7 <= k2 || ad[l7 - 1] + d1 < 0.0D)
                    {
                        ad[l7 - 1] += d1;
                    }
                    else
                    {
                        d1 = -ad[l7 - 1];
                        ad[l7 - 1] = 0.0D;
                    }
                    if (d1 == 0.0D)
                    {
                        continue;
                    }
                    if (i7 <= j)
                    {
                        for (int l3 = 1; l3 <= i; l3++)
                        {
                            int k5 = l3 - 1;
                            ad1[k1 + k5] += -d1 * ad[k + k5 * l + (i7 - 1)];
                        }

                        continue;
                    }
                    int k7 = i7 - j;
                    if (k7 <= i)
                    {
                        ad1[(k1 + k7) - 1] += d1;
                    }
                    else
                    {
                        ad1[(k1 + k7) - i - 1] -= d1;
                    }
                }

                ad3[0] = 0.0D;
                if (i1 == i)
                {
                    return;
                }
                for (int i4 = 1; i4 <= i; i4++)
                {
                    int l5 = i4 - 1;
                    ad3[0] += ad1[k1 + l5] * ad1[k1 + l5];
                }

                if (flag)
                {
                    break;
                }
                flag = true;
                for (int i8 = 1; i8 <= i1; i8++)
                {
                    int l8 = i8 - 1;
                    ad1[l2 + l8] = ad[l8];
                }

                for (int j4 = 1; j4 <= i; j4++)
                {
                    int i6 = j4 - 1;
                    ad1[i3 + i6] = ad1[k1 + i6];
                }

                d = ad3[0];
            }
            while (true);
            if (d < ad3[0])
            {
                for (int j8 = 1; j8 <= i1; j8++)
                {
                    int i9 = j8 - 1;
                    ad[i9] = ad1[l2 + i9];
                }

                for (int k4 = 1; k4 <= i; k4++)
                {
                    int j6 = k4 - 1;
                    ad1[k1 + j6] = ad1[i3 + j6];
                }

                ad3[0] = d;
            }
        }
        else
        {
            ad3[0] = 0.0D;
            for (int l4 = 1; l4 <= i; l4++)
            {
                int k6 = l4 - 1;
                ad3[0] += ad1[j1 + k6] * ad1[j1 + k6];
            }

        }
        ad2[0] = 0.0D;
        if (k2 < i1)
        {
            int i10 = k2 + 1;
            for (int k8 = i10; k8 <= i1; k8++)
            {
                int j9 = k8 - 1;
                int j7 = ai[j9];
                if (ad1[(j2 + j7) - 1] > 0.0D)
                {
                    ad2[0] += -ad[j9] * ad1[(j2 + j7) - 1];
                }
            }

        }
    }

    private void l_l13nf(int i, int j, int k, double ad[], int l, double ad1[], double ad2[], int i1, int j1, int k1,
            int l1, double d, double d1, double d2, double ad3[], double ad4[], int ai[], int i2, int j2, double ad5[],
            double ad6[])
    {
        double d10 = 0.0D;
        double d4 = 0.0D;
        double d15 = 0.90000000000000002D;
        int i5 = 0;
        double d13 = -1D;
        for (int k2 = 1; k2 <= l; k2++)
        {
            int l3 = k2 - 1;
            ad2[k1 + l3] = ad1[l3];
            ad2[l1 + l3] = ad2[i1 + l3];
            ad2[j2 + l3] = ad2[i1 + l3];
            if (ad2[j1 + l3] == 0.0D)
            {
                continue;
            }
            double d21 = Math.abs(ad1[l3] / ad2[j1 + l3]);
            if (d13 < 0.0D || d21 < d13)
            {
                d13 = d21;
            }
        }

        ad4[0] = Math.min(1.0D, d1);
        double d19 = Math.max(d * d13, 2.2204460492503131E-014D * ad4[0]);
        ad4[0] = Math.max(d19, ad4[0]);
        double d16 = 0.0D;
        double d9 = ad3[0];
        double d3 = d2;
        double d18 = 0.0D;
        double d11 = ad3[0];
        double d6 = d2;
        double d17 = 0.0D;
        double d20 = 0.0D;
        double d12 = ad3[0];
        double d8 = Math.abs(d2);
        label0: do
        {
            do
            {
                for (int l2 = 1; l2 <= l; l2++)
                {
                    int i4 = l2 - 1;
                    ad1[i4] = ad2[k1 + i4] + ad4[0] * ad2[j1 + i4];
                }

                l_lfcn(i, j, k, ad1, ad, ad3, ad6);
                ad5[0] = ad3[0];
                l_l21nf(i, j, k, ad, l, ad1, ad3[0], ad2, i1, ad6);
                i5++;
                double d7 = 0.0D;
                for (int i3 = 1; i3 <= l; i3++)
                {
                    int j4 = i3 - 1;
                    d7 += ad2[j1 + j4] * ad2[i1 + j4];
                }

                if (ad3[0] <= d12 && (ad3[0] < d12 || Math.abs(d7) < d8))
                {
                    d20 = ad4[0];
                    d12 = ad3[0];
                    for (int j3 = 1; j3 <= l; j3++)
                    {
                        int k4 = j3 - 1;
                        ad2[j2 + k4] = ad2[i1 + k4];
                    }

                    d8 = Math.abs(d7);
                }
                if (ai[0] + i5 == i2)
                {
                    break label0;
                }
                boolean flag = false;
                if (ad3[0] >= d9 + 0.10000000000000001D * (ad4[0] - d16) * d3)
                {
                    if (d17 > 0.0D || ad3[0] > d9 || d7 > 0.5D * d2)
                    {
                        d17 = ad4[0];
                        d10 = ad3[0];
                        d4 = d7;
                        flag = true;
                    }
                    if (!flag)
                    {
                        d16 = ad4[0];
                        d9 = ad3[0];
                        d3 = d7;
                    }
                }
                if (!flag)
                {
                    if (d7 >= 0.69999999999999996D * d3)
                    {
                        break label0;
                    }
                    d18 = ad4[0];
                    d11 = ad3[0];
                    d6 = d7;
                }
                if (d17 > 0.0D && d18 >= d15 * d17)
                {
                    break label0;
                }
                if (d17 == 0.0D)
                {
                    if (ad4[0] == d1)
                    {
                        break label0;
                    }
                    double d22 = 10D;
                    if (d7 > 0.90000000000000002D * d2)
                    {
                        d22 = d2 / (d2 - d7);
                    }
                    ad4[0] = Math.min(d22 * ad4[0], d1);
                    continue;
                }
                if (i5 != 1 && d18 <= 0.0D)
                {
                    break;
                }
                double d5 = (2D * (d10 - d11)) / (d17 - d18) - 0.5D * (d6 + d4);
                double d14;
                if (d5 >= 0.0D)
                {
                    d14 = Math.max(0.10000000000000001D, (0.5D * d6) / (d6 - d5));
                }
                else
                {
                    d14 = (0.5D * d4 - d5) / (d4 - d5);
                }
                ad4[0] = d18 + d14 * (d17 - d18);
            }
            while (true);
            ad4[0] *= 0.10000000000000001D;
        }
        while (ad4[0] >= d19);
        if (ad4[0] != d20)
        {
            ad4[0] = d20;
            ad3[0] = d12;
            for (int k3 = 1; k3 <= l; k3++)
            {
                int l4 = k3 - 1;
                ad1[l4] = ad2[k1 + l4] + ad4[0] * ad2[j1 + l4];
                ad2[i1 + l4] = ad2[j2 + l4];
            }

        }
        ai[0] += i5;
    }

    private void l_l14nf(int i, double ad[], int j, double ad1[], int k, int l, int i1, int j1, int k1, double ad2[])
    {
        double d = 0.0D;
        double d1 = 0.0D;
        double d3 = 0.0D;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int j3 = l1 - 1;
            ad1[j1 + j3] = ad[j3] - ad1[j1 + j3];
            d += ad1[j1 + j3] * ad1[j1 + j3];
            d3 += ad1[k1 + j3] * ad1[j1 + j3];
            ad1[k1 + j3] = ad1[k + j3] - ad1[k1 + j3];
            d1 += ad1[k1 + j3] * ad1[j1 + j3];
        }

        if (d1 < 0.10000000000000001D * Math.abs(d3))
        {
            return;
        }
        int k5 = i;
        do
        {
            int k6 = k5;
            if (--k5 <= j)
            {
                break;
            }
            if (ad1[(i1 + k6) - 1] != 0.0D)
            {
                d3 = Math.abs(ad1[(i1 + k6) - 1])
                        * Math.sqrt(1.0D + (ad1[(i1 + k5) - 1] / ad1[(i1 + k6) - 1])
                                * (ad1[(i1 + k5) - 1] / ad1[(i1 + k6) - 1]));
                double d5 = ad1[(i1 + k5) - 1] / d3;
                double d6 = ad1[(i1 + k6) - 1] / d3;
                ad1[(i1 + k5) - 1] = d3;
                int l4 = k5;
                int i2 = 1;
                while (i2 <= i)
                {
                    int k3 = i2 - 1;
                    d3 = d5 * ad1[l + l4] - d6 * ad1[(l + l4) - 1];
                    ad1[(l + l4) - 1] = d5 * ad1[(l + l4) - 1] + d6 * ad1[l + l4];
                    ad1[l + l4] = d3;
                    l4 += i;
                    i2++;
                }
            }
        }
        while (true);
        if (ad2[0] < 0.0D)
        {
            ad2[0] = d / d1;
        }
        else
        {
            d3 = Math.sqrt((ad2[0] * d) / d1);
            ad2[0] = Math.min(ad2[0], d3);
            ad2[0] = Math.max(ad2[0], 0.10000000000000001D * d3);
        }
        int l6 = j + 1;
        d3 = Math.sqrt(d1);
        int i5 = l6;
        for (int j2 = 1; j2 <= i; j2++)
        {
            int l3 = j2 - 1;
            ad1[(l + i5) - 1] = ad1[j1 + l3] / d3;
            i5 += i;
        }

        if (l6 < i)
        {
            int j6 = l6 + 1;
            for (int l5 = j6; l5 <= i; l5++)
            {
                int i6 = l5 - 1;
                double d4 = 0.0D;
                int j5 = l5;
                for (int k2 = 1; k2 <= i; k2++)
                {
                    int i4 = k2 - 1;
                    d4 += ad1[k1 + i4] * ad1[(l + j5) - 1];
                    j5 += i;
                }

                d4 /= d1;
                double d2 = 0.0D;
                j5 = l5;
                for (int l2 = 1; l2 <= i; l2++)
                {
                    int j4 = l2 - 1;
                    ad1[(l + j5) - 1] += -d4 * ad1[j1 + j4];
                    d2 += ad1[(l + j5) - 1] * ad1[(l + j5) - 1];
                    j5 += i;
                }

                if (d2 >= ad2[0])
                {
                    continue;
                }
                d4 = Math.sqrt(ad2[0] / d2);
                j5 = l5;
                for (int i3 = 1; i3 <= i; i3++)
                {
                    int k4 = i3 - 1;
                    ad1[(l + j5) - 1] *= d4;
                    j5 += i;
                }

            }

        }
    }

    private void l_l15nf(int i, int j, double ad[], int k, int l, int ai[], int ai1[], double ad1[], int i1, int j1,
            double d, int k1)
    {
        int k5 = 0;
        int j4 = 0;
        int k6 = ai1[0] - 1;
        if (k1 == ai1[0])
        {
            ai1[0] = k6;
            return;
        }
        int k4 = ai[k1 - 1];
        for (int l5 = k1; l5 <= k6; l5++)
        {
            int i6 = l5 - 1;
            int j6 = l5 + 1;
            int i4 = ai[j6 - 1];
            ai[i6] = i4;
            double d2;
            if (i4 <= j)
            {
                d2 = 0.0D;
                int l4 = l5;
                for (int l1 = 1; l1 <= i; l1++)
                {
                    int l2 = l1 - 1;
                    d2 += ad1[(i1 + l4) - 1] * ad[k + l2 * l + (i4 - 1)];
                    l4 += i;
                }

            }
            else
            {
                int l3 = i4 - j;
                if (l3 <= i)
                {
                    k5 = l3 * i - i;
                    d2 = -ad1[i1 + k5 + i6];
                }
                else
                {
                    l3 -= i;
                    k5 = l3 * i - i;
                    d2 = ad1[i1 + k5 + i6];
                }
            }
            double d9 = ad1[(j1 + j6) - 1];
            double d4 = d2 * d9;
            double d1 = Math.abs(d4);
            if (d1 * d < 1.0D)
            {
                d1 = Math.sqrt(1.0D + d1 * d1);
            }
            double d10 = d4 / d1;
            double d12 = 1.0D / d1;
            int i5 = l5;
            if (i4 > j)
            {
                for (int i2 = 1; i2 <= i; i2++)
                {
                    int i3 = i2 - 1;
                    double d5 = d10 * ad1[i1 + i5] - d12 * ad1[(i1 + i5) - 1];
                    ad1[(i1 + i5) - 1] = d10 * ad1[(i1 + i5) - 1] + d12 * ad1[i1 + i5];
                    ad1[i1 + i5] = d5;
                    i5 += i;
                }

                ad1[(i1 + k5 + j6) - 1] = 0.0D;
            }
            else
            {
                double d11 = 0.0D;
                for (int j2 = 1; j2 <= i; j2++)
                {
                    int j3 = j2 - 1;
                    double d7 = d10 * ad1[i1 + i5];
                    double d8 = d12 * ad1[(i1 + i5) - 1];
                    double d6 = Math.abs(ad[k + j3 * l + (i4 - 1)]) * (Math.abs(d7) + Math.abs(d8));
                    if (d6 > d11)
                    {
                        d11 = d6;
                        j4 = j2;
                    }
                    ad1[(i1 + i5) - 1] = d10 * ad1[(i1 + i5) - 1] + d12 * ad1[i1 + i5];
                    ad1[i1 + i5] = d7 - d8;
                    i5 += i;
                }

                double d3 = 0.0D;
                i5 = j6;
                for (int k2 = 1; k2 <= i; k2++)
                {
                    int k3 = k2 - 1;
                    d3 += ad1[(i1 + i5) - 1] * ad[k + k3 * l + (i4 - 1)];
                    i5 += i;
                }

                if (d3 != 0.0D)
                {
                    int j5 = (j4 * i - i) + j6;
                    ad1[(i1 + j5) - 1] += -d3 / ad[k + (j4 - 1) * l + (i4 - 1)];
                }
            }
            ad1[(j1 + j6) - 1] = -d1 * ad1[j1 + i6];
            ad1[j1 + i6] = d9 / d1;
        }

        ai[ai1[0] - 1] = k4;
        ai1[0] = k6;
    }

    private void l_l16nf(int i, int j, int k, int l, int ai[], int ai1[], double ad[], double ad1[], int i1, int j1,
            int k1, int l1, int i2, double d, double ad2[], int j2, int k2, int l2, int i3, int j3, int k3)
    {
        double ad3[] =
        {
            0.0D
        };
        int k6 = 0;
        boolean flag = true;
        do
        {
            if (flag)
            {
                for (int l3 = 1; l3 <= i; l3++)
                {
                    int l4 = l3 - 1;
                    ad1[l2 + l4] = ad1[i1 + l4];
                }

                k6 = ai1[0];
            }
            if (k6 <= 0)
            {
                break;
            }
            double d1 = 0.0D;
            int l5 = k6;
            for (int i4 = 1; i4 <= i; i4++)
            {
                int i5 = i4 - 1;
                d1 += ad1[(j1 + l5) - 1] * ad1[l2 + i5];
                l5 += i;
            }

            d1 *= ad1[(k1 + k6) - 1];
            if (k6 > j2 && d1 > 0.0D)
            {
                l_l15nf(i, j, ad, k, l, ai, ai1, ad1, j1, k1, d, k6);
                flag = true;
            }
            else
            {
                int i6 = ai[k6 - 1];
                if (i6 <= j)
                {
                    for (int j4 = 1; j4 <= i; j4++)
                    {
                        int j5 = j4 - 1;
                        ad1[l2 + j5] += -d1 * ad[k + j5 * l + (i6 - 1)];
                    }

                }
                else
                {
                    int j6 = i6 - j;
                    if (j6 <= i)
                    {
                        ad1[(l2 + j6) - 1] += d1;
                    }
                    else
                    {
                        ad1[(l2 + j6) - i - 1] -= d1;
                    }
                }
                ad[k6 - 1] = d1;
                k6--;
                flag = false;
            }
        }
        while (true);
        ad2[0] = 0.0D;
        if (ai1[0] < i)
        {
            l_l18nf(i, j, k, l, ai, ai1, ad, ad1, j1, k1, l1, i2, l2, d, ad3, j2, k2, i3, j3, k3);
            if (ad3[0] < 0.0D)
            {
                for (int k4 = 1; k4 <= i; k4++)
                {
                    int k5 = k4 - 1;
                    ad2[0] += ad1[l1 + k5] * ad1[i1 + k5];
                }

            }
        }
    }

    private void l_l17nf(int i, int j, double ad[], int k, int l, int ai[], double ad1[], int i1, int j1, double ad2[],
            double ad3[], int k1, int ai1[], int l1, int ai2[])
    {
        int k2 = 0;
        double d = 0.0D;
        boolean flag = false;
        ad2[0] = 0.0D;
        ai2[0] = 0;
        int i3 = k1;
        boolean flag2;
        boolean flag3;
        boolean flag1 = flag2 = flag3 = true;
        do
        {
            if (flag1 && ++i3 > l1)
            {
                flag2 = false;
                flag3 = true;
            }
            if (flag2)
            {
                k2 = ai[i3 - 1];
                if (k2 <= j)
                {
                    d = 0.0D;
                    for (int i2 = 1; i2 <= i; i2++)
                    {
                        int j2 = i2 - 1;
                        d += ad1[j1 + j2] * ad[k + j2 * l + (k2 - 1)];
                    }

                }
                else
                {
                    int l2 = k2 - j;
                    if (l2 <= i)
                    {
                        d = -ad1[(j1 + l2) - 1];
                    }
                    else
                    {
                        d = ad1[(j1 + l2) - i - 1];
                    }
                }
                if (flag)
                {
                    flag3 = false;
                }
                if (flag3)
                {
                    if (d * ad1[(i1 + k2) - 1] <= 0.0D)
                    {
                        ad1[(i1 + k2) - 1] = 0.0D;
                    }
                    else
                    {
                        ad1[(i1 + k2) - 1] /= d;
                        if (ad2[0] == 0.0D || ad1[(i1 + k2) - 1] < ad2[0])
                        {
                            ad2[0] = ad1[(i1 + k2) - 1];
                            ai2[0] = i3;
                        }
                    }
                    flag1 = flag2 = flag3 = true;
                    continue;
                }
            }
            if (flag3)
            {
                if (ai2[0] <= ai1[0])
                {
                    return;
                }
                flag = true;
                i3 = ai2[0];
                flag1 = false;
                flag2 = flag3 = true;
                continue;
            }
            ai1[0]++;
            ai[ai2[0] - 1] = ai[ai1[0] - 1];
            ai[ai1[0] - 1] = k2;
            ad1[(i1 + k2) - 1] = 0.0D;
            ai2[0] = ai1[0];
            ad3[0] -= d;
            if (ad3[0] >= 0.0D || ai1[0] >= l1)
            {
                break;
            }
            double d1 = 0.0D;
            int k3 = k1 + 1;
            for (i3 = k3; i3 <= l1; i3++)
            {
                int j3 = i3 - 1;
                k2 = ai[j3];
                if (ad1[(i1 + k2) - 1] > 0.0D && (d1 == 0.0D || ad1[(i1 + k2) - 1] < d1))
                {
                    d1 = ad1[(i1 + k2) - 1];
                    ai2[0] = i3;
                }
            }

            if (d1 <= 0.0D)
            {
                break;
            }
            ad2[0] = d1;
            flag1 = flag2 = false;
            flag3 = true;
        }
        while (true);
    }

    private void l_l18nf(int i, int j, int k, int l, int ai[], int ai1[], double ad[], double ad1[], int i1, int j1,
            int k1, int l1, int i2, double d, double ad2[], int j2, int k2, int l2, int i3, int j3)
    {
        boolean flag = false;
        int k5 = 0;
        int k8 = j2 + 1;
        double d1 = 0.0D;
        do
        {
            l_l19nf(i, ai1[0], ad1, i1, k1, l1, i2, d, ad2);
            if (ad2[0] == 0.0D)
            {
                return;
            }
            if (ai1[0] == k2)
            {
                return;
            }
            int l8 = ai1[0] + 1;
            double d3 = 0.0D;
            for (int i6 = l8; i6 <= i; i6++)
            {
                int k6 = i6 - 1;
                d3 += ad1[l1 + k6] * ad1[l1 + k6];
            }

            if (d1 > 0.0D && d3 >= d1)
            {
                if (flag)
                {
                    return;
                }
                flag = true;
            }
            else
            {
                d1 = d3;
                flag = false;
            }
            int i7 = ai1[0];
            l_l20nf(i, j, ad, k, l, ai, ai1, ad1, i1, j1, k1, d, k2, l2, i3, j3);
            if (ai1[0] == i7)
            {
                return;
            }
            ad[ai1[0] - 1] = 0.0D;
            do
            {
                for (int k3 = 1; k3 <= i; k3++)
                {
                    int k4 = k3 - 1;
                    ad1[l2 + k4] = ad1[i2 + k4];
                }

                int j7 = ai1[0];
                do
                {
                    double d4 = 0.0D;
                    int l5 = j7;
                    for (int l3 = 1; l3 <= i; l3++)
                    {
                        int l4 = l3 - 1;
                        d4 += ad1[(i1 + l5) - 1] * ad1[l2 + l4];
                        l5 += i;
                    }

                    d4 *= ad1[(j1 + j7) - 1];
                    ad1[(i3 + j7) - 1] = ad[j7 - 1] + d4;
                    if (j7 == ai1[0])
                    {
                        ad1[(i3 + j7) - 1] = Math.min(ad1[(i3 + j7) - 1], 0.0D);
                    }
                    int j6 = ai[j7 - 1];
                    if (j6 <= j)
                    {
                        for (int i4 = 1; i4 <= i; i4++)
                        {
                            int i5 = i4 - 1;
                            ad1[l2 + i5] += -d4 * ad[k + i5 * l + (j6 - 1)];
                        }

                    }
                    else
                    {
                        int l6 = j6 - j;
                        if (l6 <= i)
                        {
                            ad1[(l2 + l6) - 1] += d4;
                        }
                        else
                        {
                            ad1[(l2 + l6) - i - 1] -= d4;
                        }
                    }
                }
                while (--j7 > j2);
                double d2 = 0.0D;
                if (k8 < ai1[0])
                {
                    int j8 = ai1[0] - 1;
                    for (j7 = k8; j7 <= j8; j7++)
                    {
                        int k7 = j7 - 1;
                        if (ad1[i3 + k7] > 0.0D)
                        {
                            d2 = ad1[i3 + k7] / (ad1[i3 + k7] - ad[k7]);
                            k5 = j7;
                        }
                    }

                }
                double d5 = 1.0D - d2;
                for (j7 = k8; j7 <= ai1[0]; j7++)
                {
                    int l7 = j7 - 1;
                    ad[l7] = Math.min(d5 * ad1[i3 + l7] + d2 * ad[l7], 0.0D);
                }

                for (int j4 = 1; j4 <= i; j4++)
                {
                    int j5 = j4 - 1;
                    ad1[i2 + j5] = d5 * ad1[l2 + j5] + d2 * ad1[i2 + j5];
                }

                if (d2 <= 0.0D)
                {
                    break;
                }
                l_l15nf(i, j, ad, k, l, ai, ai1, ad1, i1, j1, d, k5);
                j7 = k5;
                while (j7 <= ai1[0])
                {
                    int i8 = j7 - 1;
                    ad[i8] = ad[i8 + 1];
                    j7++;
                }
            }
            while (true);
        }
        while (ai1[0] < i);
        ad2[0] = 0.0D;
    }

    private void l_l19nf(int i, int j, double ad[], int k, int l, int i1, int j1, double d, double ad1[])
    {
        ad1[0] = 0.0D;
        if (j >= i)
        {
            return;
        }
        int k4 = j + 1;
        for (int k3 = k4; k3 <= i; k3++)
        {
            int i4 = k3 - 1;
            double d1 = 0.0D;
            double d3 = 0.0D;
            int i3 = k3;
            for (int k1 = 1; k1 <= i; k1++)
            {
                int j2 = k1 - 1;
                double d6 = ad[(k + i3) - 1] * ad[j1 + j2];
                d1 += d6;
                d3 += Math.abs(d6);
                i3 += i;
            }

            if (Math.abs(d1) <= d * d3)
            {
                d1 = 0.0D;
            }
            ad[i1 + i4] = d1;
        }

        int j3 = 0;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int k2 = l1 - 1;
            double d2 = 0.0D;
            double d4 = 0.0D;
            for (int l3 = k4; l3 <= i; l3++)
            {
                int j4 = l3 - 1;
                double d7 = ad[k + j3 + j4] * ad[i1 + j4];
                d2 -= d7;
                d4 += Math.abs(d7);
            }

            if (Math.abs(d2) <= d * d4)
            {
                d2 = 0.0D;
            }
            ad[l + k2] = d2;
            j3 += i;
        }

        double d5 = 0.0D;
        for (int i2 = 1; i2 <= i; i2++)
        {
            int l2 = i2 - 1;
            double d8 = ad[l + l2] * ad[j1 + l2];
            ad1[0] += d8;
            d5 += Math.abs(d8);
        }

        if (ad1[0] + d * d5 >= 0.0D)
        {
            ad1[0] = 0.0D;
        }
    }

    private void l_l20nf(int i, int j, double ad[], int k, int l, int ai[], int ai1[], double ad1[], int i1, int j1,
            int k1, double d, int l1, int i2, int j2, int k2)
    {
        int j6 = 0;
        double d4 = 0.0D;
        double d3 = 0.0D;
        int k10 = ai1[0] + 1;
        int j10 = l1;
        int k6 = 0;
        for (int l2 = 1; l2 <= i; l2++)
        {
            int k4 = l2 - 1;
            ad1[i2 + k4] = 0.0D;
            for (int i7 = k10; i7 <= i; i7++)
            {
                int i8 = i7 - 1;
                ad1[i2 + k4] += ad1[i1 + k6 + i8] * ad1[i1 + k6 + i8];
            }

            k6 += i;
        }

        boolean flag = true;
        do
        {
            double d2 = 0.0D;
            for (int i9 = k10; i9 <= j10; i9++)
            {
                int i10 = i9 - 1;
                int j7 = ai[i10];
                double d5;
                double d7;
                double d9;
                if (j7 <= j)
                {
                    d5 = 0.0D;
                    d7 = 0.0D;
                    d9 = 0.0D;
                    for (int i3 = 1; i3 <= i; i3++)
                    {
                        int l4 = i3 - 1;
                        double d10 = ad1[k1 + l4] * ad[k + l4 * l + (j7 - 1)];
                        d5 += d10;
                        d7 += Math.abs(d10);
                        d9 += ad1[i2 + l4] * ad[k + l4 * l + (j7 - 1)] * ad[k + l4 * l + (j7 - 1)];
                    }

                }
                else
                {
                    int j8 = j7 - j;
                    if (j8 <= i)
                    {
                        d5 = -ad1[(k1 + j8) - 1];
                    }
                    else
                    {
                        j8 -= i;
                        d5 = ad1[(k1 + j8) - 1];
                    }
                    d7 = Math.abs(d5);
                    d9 = ad1[(i2 + j8) - 1];
                }
                if (d5 <= d * d7)
                {
                    continue;
                }
                double d1 = (d5 * d5) / d9;
                if (d1 > d2)
                {
                    d2 = d1;
                    j6 = i9;
                    d4 = d5;
                    d3 = d7;
                }
            }

            if (d2 <= 0.0D)
            {
                return;
            }
            if (ai1[0] != 0)
            {
                int k7 = ai[j6 - 1];
                int l8;
                if (k7 <= j)
                {
                    l8 = 0;
                    for (int j3 = 1; j3 <= i; j3++)
                    {
                        int i5 = j3 - 1;
                        ad1[j2 + i5] = ad[k + i5 * l + (k7 - 1)];
                    }

                }
                else
                {
                    l8 = k7 - j;
                    for (int k3 = 1; k3 <= i; k3++)
                    {
                        int j5 = k3 - 1;
                        ad1[j2 + j5] = 0.0D;
                    }

                    if (l8 <= i)
                    {
                        ad1[(j2 + l8) - 1] = -1D;
                    }
                    else
                    {
                        l8 -= i;
                        ad1[(j2 + l8) - 1] = 1.0D;
                    }
                }
                int j9 = ai1[0];
                do
                {
                    double d11 = 0.0D;
                    int l6 = j9;
                    for (int l3 = 1; l3 <= i; l3++)
                    {
                        int k5 = l3 - 1;
                        d11 += ad1[(i1 + l6) - 1] * ad1[j2 + k5];
                        l6 += i;
                    }

                    d11 *= ad1[(j1 + j9) - 1];
                    int l7 = ai[j9 - 1];
                    if (l7 <= j)
                    {
                        for (int i4 = 1; i4 <= i; i4++)
                        {
                            int l5 = i4 - 1;
                            ad1[j2 + l5] += -d11 * ad[k + l5 * l + (l7 - 1)];
                        }

                    }
                    else
                    {
                        int k8 = l7 - j;
                        if (k8 <= i)
                        {
                            ad1[(j2 + k8) - 1] += d11;
                        }
                        else
                        {
                            ad1[(j2 + k8) - i - 1] -= d11;
                        }
                    }
                    double d6 = 0.0D;
                    double d8 = 0.0D;
                    for (int j4 = 1; j4 <= i; j4++)
                    {
                        int i6 = j4 - 1;
                        double d12 = ad1[k1 + i6] * ad1[j2 + i6];
                        d6 += d12;
                        d8 += Math.abs(d12);
                    }

                    d4 = Math.min(d4, d6);
                    d3 = Math.max(d3, d8);
                }
                while (--j9 >= 1);
                if (l8 > 0)
                {
                    ad1[(k1 + l8) - 1] = 0.0D;
                }
                if (d4 <= d * d3)
                {
                    flag = false;
                }
            }
            if (flag)
            {
                int k9 = ai1[0];
                l_l9onf(i, j, ad, k, l, ai, ai1, ad1, i1, j1, d, j6, j2, k2);
                if (ai1[0] > k9)
                {
                    return;
                }
                j6 = k10;
            }
            if (k10 < j10)
            {
                int l9 = ai[j10 - 1];
                ai[j10 - 1] = ai[j6 - 1];
                ai[j6 - 1] = l9;
                j10--;
                flag = true;
            }
            else
            {
                return;
            }
        }
        while (true);
    }

    private void l_l21nf(int i, int j, int k, double ad[], int l, double ad1[], double d, double ad2[], int i1,
            double ad3[])
    {
        double ad4[] =
        {
            0.0D
        };
        double d1 = Math.sqrt(Math.max(0.0D, 2.2204460492503131E-016D));
        for (int j1 = 1; j1 <= l; j1++)
        {
            int k1 = j1 - 1;
            double d2 = d1 * Math.max(Math.abs(ad1[k1]), 1.0D);
            if (ad1[k1] < 0.0D)
            {
                d2 = -d2;
            }
            double d3 = ad1[k1];
            ad1[k1] = d3 + d2;
            l_lfcn(i, j, k, ad1, ad, ad4, ad3);
            ad1[k1] = d3;
            ad2[i1 + k1] = (ad4[0] - d) / d2;
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

    private void l_lfcn(int i, int j, int k, double ad[], double ad1[], double ad2[], double ad3[])
    {
        double d4 = 8.9884656743115785E+307D;
        int l4 = Math.max(i, j);
        l4 = Math.max(l4, 1);
        double d1 = 3.1415926535897931D;
        for (int l = 1; l <= i + j + 1; l++)
        {
            int j2 = l - 1;
            if (ad[j2] < 0.0D)
            {
                ad[j2] = 0.0D;
            }
        }

        double d8 = 0.0D;
        double d9 = 0.0D;
        for (int i1 = 1; i1 <= k; i1++)
        {
            int k2 = i1 - 1;
            d8 += ad1[k2] / (double) k;
        }

        for (int j1 = 1; j1 <= k; j1++)
        {
            int l2 = j1 - 1;
            d9 += (ad1[l2] - d8) * (ad1[l2] - d8);
        }

        double d5 = d9 / ((double) k - 1.0D);
        for (int k1 = 1; k1 <= l4; k1++)
        {
            int i3 = k1 - 1;
            ad3[i3] = d5;
        }

        for (int l1 = l4 + 1; l1 <= k; l1++)
        {
            int j3 = l1 - 1;
            double d2 = 0.0D;
            double d3 = 0.0D;
            if (j >= 1)
            {
                for (int l3 = 1; l3 <= j; l3++)
                {
                    int j4 = l3 - 1;
                    d2 += ad[j4 + 1] * ad1[j3 - l3] * ad1[j3 - l3];
                }

            }
            if (i >= 1)
            {
                for (int i4 = 1; i4 <= i; i4++)
                {
                    int k4 = i4 - 1;
                    d3 += ad[k4 + j + 1] * ad3[j3 - i4];
                }

            }
            if (d2 > d4 || d3 > d4)
            {
                ad3[j3] = d4;
            }
            else
            {
                ad3[j3] = ad[0] + d2 + d3;
            }
        }

        double d = ((double) (-k) / 2D) * Math.log(2D * d1);
        double d6 = 0.0D;
        double d7 = 0.0D;
        for (int i2 = 1; i2 <= k; i2++)
        {
            int k3 = i2 - 1;
            d6 += (ad1[k3] * ad1[k3]) / ad3[k3];
            d7 += Math.log(Math.sqrt(ad3[k3]));
        }

        ad2[0] = d - d6 / 2D - d7;
        ad2[0] = -ad2[0];
    }

    private void lgrad(int i, int j, int k, double ad[], double ad1[], double ad2[], int l, double ad3[], double ad4[])
    {
        int k9 = Math.max(i, j);
        k9 = Math.max(k9, 1);
        double d10 = 0.0D;
        double d11 = 0.0D;
        for (int i1 = 1; i1 <= k; i1++)
        {
            int k3 = i1 - 1;
            d10 += ad1[k3] / (double) k;
        }

        for (int j1 = 1; j1 <= k; j1++)
        {
            int l3 = j1 - 1;
            d11 += (ad1[l3] - d10) * (ad1[l3] - d10);
        }

        double d2 = d11 / ((double) k - 1.0D);
        for (int k1 = 1; k1 <= k9; k1++)
        {
            int i4 = k1 - 1;
            ad3[i4] = d2;
        }

        for (int l1 = k9 + 1; l1 <= k; l1++)
        {
            int j4 = l1 - 1;
            double d = 0.0D;
            double d1 = 0.0D;
            if (j >= 1)
            {
                for (int i6 = 1; i6 <= j; i6++)
                {
                    int j7 = i6 - 1;
                    d += ad[j7 + 1] * ad1[j4 - i6] * ad1[j4 - i6];
                }

            }
            if (i >= 1)
            {
                for (int j6 = 1; j6 <= i; j6++)
                {
                    int k7 = j6 - 1;
                    d1 += ad[k7 + j + 1] * ad3[j4 - j6];
                }

            }
            ad3[j4] = ad[0] + d + d1;
        }

        for (int i2 = 1; i2 <= k9; i2++)
        {
            int k4 = i2 - 1;
            ad4[k4] = 0.0D;
        }

        double d3 = 0.0D;
        double d7 = 0.0D;
        for (int j2 = k9 + 1; j2 <= k; j2++)
        {
            int l4 = j2 - 1;
            double d4 = 0.0D;
            if (i >= 1)
            {
                for (int k6 = 1; k6 <= i; k6++)
                {
                    int l7 = k6 - 1;
                    d4 += ad[l7 + j + 1] * ad4[l4 - k6];
                }

            }
            ad4[l4] = 1.0D + d4;
            d7 += (((ad1[l4] * ad1[l4]) / ad3[l4] - 1.0D) * ad4[l4]) / ad3[l4];
        }

        ad2[l + 0] = -d7 / 2D;
        for (int k2 = 1; k2 <= k9; k2++)
        {
            int i5 = k2 - 1;
            ad4[i5] = 0.0D;
        }

        if (j >= 1)
        {
            for (int k8 = 2; k8 <= j + 1; k8++)
            {
                int i9 = k8 - 1;
                double d8 = 0.0D;
                for (int l2 = k9 + 1; l2 <= k; l2++)
                {
                    int j5 = l2 - 1;
                    double d5 = 0.0D;
                    if (i >= 1)
                    {
                        for (int l6 = 1; l6 <= i; l6++)
                        {
                            int i8 = l6 - 1;
                            d5 += ad[i8 + j + 1] * ad4[j5 - l6];
                        }

                    }
                    ad4[j5] = ad1[(j5 + 1) - k8] * ad1[(j5 + 1) - k8] + d5;
                    d8 += (((ad1[j5] * ad1[j5]) / ad3[j5] - 1.0D) * ad4[j5]) / ad3[j5];
                }

                ad2[l + i9] = -d8 / 2D;
            }

        }
        for (int i3 = 1; i3 <= k9; i3++)
        {
            int k5 = i3 - 1;
            ad4[k5] = 0.0D;
        }

        if (i >= 1)
        {
            for (int l8 = j + 2; l8 <= i + j + 1; l8++)
            {
                int j9 = l8 - 1;
                double d9 = 0.0D;
                for (int j3 = k9 + 1; j3 <= k; j3++)
                {
                    int l5 = j3 - 1;
                    double d6 = 0.0D;
                    if (i >= 1)
                    {
                        for (int i7 = 1; i7 <= i; i7++)
                        {
                            int j8 = i7 - 1;
                            d6 += ad[j8 + j + 1] * ad4[l5 - i7];
                        }

                    }
                    ad4[l5] = ad3[(l5 + j + 1) - l8] + d6;
                    d9 += (((ad1[l5] * ad1[l5]) / ad3[l5] - 1.0D) * ad4[l5]) / ad3[l5];
                }

                ad2[l + j9] = -d9 / 2D;
            }

        }
    }

    private void l_g2hes(int i, int j, int k, double ad[], double ad1[], double ad2[], int l, int i1, double d,
            double ad3[], int j1, double ad4[], double ad5[], double ad6[])
    {
        int k3 = i + j + 1;
        double d1 = Math.sqrt(Math.max(d, 2.2204460492503131E-016D));
        for (int k2 = 1; k2 <= k3; k2++)
        {
            int i3 = k2 - 1;
            double d2 = d1 * Math.max(Math.abs(ad1[i3]), 1.0D / ad2[l + i3]);
            if (ad1[i3] < 0.0D)
            {
                d2 = -d2;
            }
            double d3 = ad1[i3];
            ad1[i3] = d3 + d2;
            lgrad(i, j, k, ad1, ad, ad4, 0, ad5, ad6);
            ad1[i3] = d3;
            for (int k1 = 1; k1 <= k3; k1++)
            {
                int i2 = k1 - 1;
                ad3[i3 * j1 + i2] = (ad4[i2] - ad2[i1 + i2]) / d2;
            }

        }

        for (int l2 = 1; l2 <= k3 - 1; l2++)
        {
            int j3 = l2 - 1;
            for (int l1 = l2 + 1; l1 <= k3; l1++)
            {
                int j2 = l1 - 1;
                ad3[j3 * j1 + j2] = (ad3[j3 * j1 + j2] + ad3[j2 * j1 + j3]) / 2D;
            }

        }

    }

    public void setMaxSigma(double d)
    {
        if (2.2204460492503131E-016D > d)
        {
            Object aobj[] =
            {
                    "maxSigma", new Double(d), new Double(2.2204460492503131E-016D)
            };
            throw new IllegalArgumentException("NotLargeEnough");// "com.imsl.stat",
                                                                 // "NotLargeEnough",
                                                                 // aobj);
        }
        l_max_sigma = d;
    }

    public double getSigma()
    {
        if (lv_coef_vector == null)
        {
            return (0.0D / 0.0D);
        }
        else
        {
            return lv_coef_vector[0];
        }
    }

    public double[] getAR()
    {
        if (lv_coef_vector == null)
        {
            return null;
        }
        else
        {
            double ad[] = new double[l_p];
            System.arraycopy(lv_coef_vector, 1, ad, 0, l_p);
            return ad;
        }
    }

    public double[] getMA()
    {
        if (lv_coef_vector == null)
        {
            return null;
        }
        else
        {
            double ad[] = new double[l_q];
            System.arraycopy(lv_coef_vector, l_p + 1, ad, 0, l_q);
            return ad;
        }
    }

    public Matrix getVarCovarMatrix()
    {
        Matrix AD = null;
        if (l_var == null)
        {
            return AD;
        }
        double ad[][] = new double[l_p + l_q + 1][l_p + l_q + 1];
        for (int i = 0; i < l_p + l_q + 1; i++)
        {
            BLAS.copy(l_p + l_q + 1, l_var, i * (l_p + l_q + 1), 1, ad[i], 0, 1);
        }

        return new Matrix(ad);
    }

    public double getAkaike()
    {
        return l_aic;
    }

    public double getLogLikelihood()
    {
        return l_a;
    }

    public double[] getX()
    {
        if (lv_coef_vector == null)
        {
            return null;
        }
        else
        {
            return (double[]) lv_coef_vector.clone();
        }
    }
}
