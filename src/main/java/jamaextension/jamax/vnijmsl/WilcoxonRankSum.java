/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 *
 * @author Feynman Perceptrons
 */
import java.io.Serializable;

public class WilcoxonRankSum implements Serializable, Cloneable
{

    static final long serialVersionUID = 0xce4a4e1b192b2af5L;
    private int l_nobsx;
    private int l_nobsy;
    private int l_user_fuzz;
    private double l_x[];
    private double l_y[];
    private double l_stat[];
    private double l_fuzz;

    public WilcoxonRankSum(double ad[], double ad2[])
    {
        if (ad.length < 1)
        {
            Object aobj[] =
            {
                    "x.length", new Integer(ad.length), new Integer(0)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        if (ad2.length < 1)
        {
            Object aobj1[] =
            {
                    "y.length", new Integer(ad2.length), new Integer(0)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj1);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        if (ad.length + ad2.length < 3)
        {
            Object aobj2[] =
            {
                    "x.length+y.length", new Integer(ad.length + ad2.length), new Integer(2)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj2);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        l_nobsx = ad.length;
        l_nobsy = ad2.length;
        l_x = (double[]) ad.clone();
        l_y = (double[]) ad2.clone();
    }

    public final double compute()
    {
        int i = l_nobsx;
        int j = l_nobsy;
        double ad[] = l_x;
        double ad1[] = l_y;
        double d2 = l_fuzz;
        int k = l_user_fuzz;
        int ai[] =
        {
            0
        };
        int ai1[] =
        {
            0
        };
        int ai2[] = null;
        double ad2[] = null;
        double ad3[] = null;
        if (k == 0)
        {
            double d = ad[BLAS.ismax(i, ad, 0, 1) - 1];
            double d1 = ad1[BLAS.ismax(j, ad1, 0, 1) - 1];
            if (d >= d1)
            {
                d2 = 2.2204460492503131E-014D * d;
            }
            else
            {
                d2 = 2.2204460492503131E-014D * d1;
            }
        }
        ad2 = new double[3 * (i + j)];
        ai2 = new int[i + j];
        ad3 = new double[i + j];
        l_r2ksm(i, ad, j, ad1, d2, ad2, ai, ai1, ai2, ad3);
        l_stat = new double[10];
        System.arraycopy(ad2, 0, l_stat, 0, 10);
        return ad2[9];
    }

    private void l_r2ksm(int i, double ad[], int j, double ad1[], double d, double ad2[], int ai[], int ai1[],
            int ai2[], double ad3[])
    {
        double ad4[] = new double[1];
        double ad5[] = new double[1];
        double ad6[] = new double[2];
        double ad7[] = new double[2];
        double ad8[] = new double[1];
        double ad9[] = new double[2];
        double ad10[] = new double[2];
        double ad11[] =
        {
            0.0D
        };
        int k5 = i + j;
        int k4 = 0;
        for (int k = 1; k <= i; k++)
        {
            if (!Double.isNaN(ad[k - 1]))
            {
                k4++;
                ad3[k4 - 1] = ad[k - 1];
            }
        }

        ai[0] = i - k4;
        int i4 = k4;
        for (int l = 1; l <= j; l++)
        {
            if (!Double.isNaN(ad1[l - 1]))
            {
                i4++;
                ad3[i4 - 1] = ad1[l - 1];
            }
        }

        int l4 = i4 - k4;
        ai1[0] = j - l4;
        if (k4 <= 0 || l4 <= 0) // Messages.throwIllegalArgumentException("com.imsl.stat",
                                // "WilcoxonRankSum.AllXYMissing", null);
        {
            throw new IllegalArgumentException("WilcoxonRankSum.AllXYMissing");
        }
        if (i < 25 && j < 25)
        {
            Object aobj[] =
            {
                    new Integer(i), new Integer(j)
            };
            // Warning.print(this, "com.imsl.stat",
            // "WilcoxonRankSum.NobsxNobsyTooSmall", aobj);
            System.out.println("WilcoxonRankSum.NobsxNobsyTooSmall");
        }
        k5 = k4 + l4;
        for (int i1 = 1; i1 <= k5; i1++)
        {
            ai2[i1 - 1] = i1;
        }

        int k2 = k4 + 1;
        int i3 = k5;
        int j4 = k4;
        for (int j1 = k2; j1 <= i3; j1++)
        {
            ai2[j1 - 1] = -j1;
        }

        l_svrgp(k5, ad3, ad3, ai2, 0);
        int l3 = 1;
        int i5 = 0;
        int j5 = 0;
        double d5 = 0.0D;
        ad8[0] = 0.0D;
        ad11[0] = 0.0D;
        do
        {
            int l2 = l3 + 1;
            int k3 = l3;
            int l5;
            if (ai2[l3 - 1] > 0)
            {
                l5 = 1;
            }
            else
            {
                l5 = 0;
            }
            byte byte0;
            if (ai2[l3 - 1] >= 0)
            {
                byte0 = 1;
            }
            else
            {
                byte0 = -1;
            }
            boolean flag = false;
            int k1;
            for (k1 = l2; k1 <= k5; k1++)
            {
                if (ad3[k1 - 1] - ad3[k1 - 2] > d)
                {
                    flag = true;
                    break;
                }
                if (ai2[k1 - 1] > 0)
                {
                    l5++;
                }
                byte byte1;
                if (ai2[k1 - 1] >= 0)
                {
                    byte1 = 1;
                }
                else
                {
                    byte1 = -1;
                }
                if (byte1 != byte0) // Warning.print(this, "com.imsl.stat",
                                    // "WilcoxonRankSum.AtLeastOneTie", null);
                {
                    System.out.println("WilcoxonRankSum.AtLeastOneTie");
                }
            }

            if (!flag)
            {
                k1 = k5 + 1;
            }
            l3 = k1;
            for (int l1 = k3; l1 <= (k3 + l5) - 1; l1++)
            {
                i5 += l1;
            }

            for (int i2 = l3 - l5; i2 <= l3 - 1; i2++)
            {
                j5 += i2;
            }

            double d7 = l3 - k3;
            ad5[0] = ((double) (k3 + l3) - 1.0D) / 2D;
            d5 += (double) l5 * ad5[0];
            l_s1msq(1, ad5, 1, d7, ad11, ad4, 1, ad8, 1);
        }
        while (l3 <= k5);
        double d9 = k5 - j4;
        double d8 = j4;
        double d10 = k5;
        double d1 = d8 * (d10 + 1.0D);
        double d2 = d9 * d1;
        ad2[0] = i5;
        ad2[1] = d1 - (double) i5;
        ad2[3] = j5;
        ad2[4] = d1 - (double) j5;
        d1 *= 0.5D;
        double d6 = 1.0D / Math.sqrt(0.083333333333333329D * d2);
        double d3 = ((Math.pow(d8, 2D) + Math.pow(d9, 2D) + d8 * d9 + d10) * 0.050000000000000003D) / d2;
        double d4 = 1.0D / Math.sqrt(6.2831853071795862D);
        int j3 = 1;
        for (int j2 = 1; j2 <= 2; j2++)
        {
            ad9[j2 - 1] = Math.min(ad2[j3 - 1], ad2[j3]);
            ad10[j2 - 1] = ad9[j2 - 1] - d1 - 0.5D;
            if (ad9[j2 - 1] - d1 < 0.0D)
            {
                ad10[j2 - 1]++;
            }
            ad10[j2 - 1] *= d6;
            double d11 = ad10[j2 - 1] * ad10[j2 - 1];
            ad6[j2 - 1] = 0.5D * Sfun.erfc(-Math.sqrt(0.5D) * ad10[j2 - 1]);
            ad7[j2 - 1] = -d4 * (ad10[j2 - 1] * (d11 - 3D)) * Math.exp(-0.5D * d11);
            ad2[j3 + 1] = ad6[j2 - 1] - ad7[j2 - 1] * d3;
            j3 += 3;
        }

        ad2[6] = d5;
        ad2[7] = Math.sqrt((ad8[0] * d8 * d9) / (d10 * (d10 - 1.0D)));
        if (ad2[7] != 0.0D)
        {
            ad2[8] = (d5 - (d8 * (d10 + 1.0D)) / 2D) / ad2[7];
            ad2[9] = 2D * (1.0D - Cdf.normal(Math.abs(ad2[8])));
        }
        else
        {
            ad2[8] = (1.0D / 0.0D);
            ad2[9] = 0.0D;
        }
    }

    private void l_s1msq(int i, double ad[], int j, double d, double ad1[], double ad2[], int k, double ad3[], int l)
    {
        if (ad1[0] < 0.0D) // Messages.throwIllegalArgumentException("com.imsl.stat",
                           // "ContingencyTable.NeedSwtEq0", null);
        {
            throw new IllegalArgumentException("ContingencyTable.NeedSwtEq0");
        }
        if (ad1[0] == 0.0D)
        {
            BLAS.set(i, 0.0D, ad3, 0, l);
            BLAS.copy(i, ad, 0, j, ad2, 0, k);
            ad1[0] = d;
        }
        else if (d != 0.0D && i > 0)
        {
            ad1[0] += d;
            if (ad1[0] > 0.0D)
            {
                double d3 = d / ad1[0];
                double d1 = d * (1.0D - d3);
                int k1;
                if (j < 0)
                {
                    k1 = 1 + (1 - i) * j;
                }
                else
                {
                    k1 = 1;
                }
                int l1;
                if (k < 0)
                {
                    l1 = 1 + (1 - i) * k;
                }
                else
                {
                    l1 = 1;
                }
                int j1;
                if (l < 0)
                {
                    j1 = 1 + (1 - i) * l;
                }
                else
                {
                    j1 = 1;
                }
                for (int i1 = 1; i1 <= i; i1++)
                {
                    double d2 = ad[k1 - 1] - ad2[l1 - 1];
                    ad2[l1 - 1] += d3 * d2;
                    ad3[j1 - 1] += d1 * d2 * d2;
                    k1 += j;
                    l1 += k;
                    j1 += l;
                }

            }
            else
            {
                if (ad1[0] < 0.0D)
                {
                    ad1[0] = 0.0D;
                }
                BLAS.set(i, 0.0D, ad2, 0, k);
                BLAS.set(i, 0.0D, ad3, 0, l);
            }
        }
    }

    private void l_svrgp(int i, double ad[], double ad1[], int ai[], int j)
    {
        int ai1[] = new int[21];
        int ai2[] = new int[21];
        System.arraycopy(ad, 0, ad1, 0, i);
        boolean flag = false;
        int l2 = 1;
        int k = 1;
        int l1 = i;
        double d = 0.375D;
        do
        {
            boolean flag2 = false;
            if (k == l1)
            {
                flag = false;
                flag2 = true;
            }
            if (!flag2)
            {
                if (d <= 0.58984369999999997D)
                {
                    d += 0.0390625D;
                }
                else
                {
                    d -= 0.21875D;
                }
            }
            boolean flag3;
            label0: do
            {
                flag3 = false;
                if (!flag2)
                {
                    int i2 = k;
                    int l = (int) ((double) k + (double) (l1 - k) * d);
                    double d1 = ad1[l - 1];
                    int i1 = ai[(j + l) - 1];
                    if (ad1[k - 1] > d1)
                    {
                        ad1[l - 1] = ad1[k - 1];
                        ad1[k - 1] = d1;
                        d1 = ad1[l - 1];
                        ai[(j + l) - 1] = ai[(j + k) - 1];
                        ai[(j + k) - 1] = i1;
                        i1 = ai[(j + l) - 1];
                    }
                    int k2 = l1;
                    boolean flag1 = false;
                    if (ad1[l1 - 1] >= d1)
                    {
                        flag1 = true;
                    }
                    if (!flag1)
                    {
                        ad1[l - 1] = ad1[l1 - 1];
                        ad1[l1 - 1] = d1;
                        d1 = ad1[l - 1];
                        ai[(j + l) - 1] = ai[(j + l1) - 1];
                        ai[(j + l1) - 1] = i1;
                        i1 = ai[(j + l) - 1];
                        if (ad1[k - 1] <= d1)
                        {
                            flag1 = true;
                        }
                    }
                    if (!flag1)
                    {
                        ad1[l - 1] = ad1[k - 1];
                        ad1[k - 1] = d1;
                        d1 = ad1[l - 1];
                        ai[(j + l) - 1] = ai[(j + k) - 1];
                        ai[(j + k) - 1] = i1;
                        i1 = ai[(j + l) - 1];
                        flag1 = true;
                    }
                    do
                    {
                        if (!flag1 && ad1[k2 - 1] != ad1[i2 - 1])
                        {
                            double d3 = ad1[k2 - 1];
                            ad1[k2 - 1] = ad1[i2 - 1];
                            ad1[i2 - 1] = d3;
                            int k1 = ai[(j + k2) - 1];
                            ai[(j + k2) - 1] = ai[(j + i2) - 1];
                            ai[(j + i2) - 1] = k1;
                        }
                        do
                        {
                            k2--;
                        }
                        while (ad1[k2 - 1] > d1);
                        do
                        {
                            i2++;
                        }
                        while (ad1[i2 - 1] < d1);
                        if (i2 > k2)
                        {
                            break;
                        }
                        flag1 = false;
                    }
                    while (true);
                    if (k2 - k <= l1 - i2)
                    {
                        ai1[l2 - 1] = i2;
                        ai2[l2 - 1] = l1;
                        l1 = k2;
                        l2++;
                    }
                    else
                    {
                        ai1[l2 - 1] = k;
                        ai2[l2 - 1] = k2;
                        k = i2;
                        l2++;
                    }
                    flag = true;
                }
                do
                {
                    if (!flag)
                    {
                        if (--l2 == 0)
                        {
                            return;
                        }
                        k = ai1[l2 - 1];
                        l1 = ai2[l2 - 1];
                    }
                    if (l1 - k >= 11)
                    {
                        flag3 = true;
                        continue label0;
                    }
                    if (k == 1)
                    {
                        continue label0;
                    }
                    k--;
                    do
                    {
                        label1:
                        {
                            if (++k != l1)
                            {
                                break label1;
                            }
                            flag = false;
                        }
                        if (true)
                        {
                            break;
                        }
                        double d2 = ad1[k];
                        int j1 = ai[j + k];
                        if (ad1[k - 1] > d2)
                        {
                            int j2 = k;
                            do
                            {
                                ad1[j2] = ad1[j2 - 1];
                                ai[j + j2] = ai[(j + j2) - 1];
                                j2--;
                            }
                            while (d2 < ad1[j2 - 1]);
                            ad1[j2] = d2;
                            ai[j + j2] = j1;
                        }
                    }
                    while (true);
                }
                while (true);
            }
            while (flag3);
        }
        while (true);
    }

    public void setFuzz(double d)
    {
        if (d < 0.0D)
        {
            Object aobj[] =
            {
                    "fuzz", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "Negative", aobj);
            throw new IllegalArgumentException("Negative");
        }
        l_fuzz = d;
        l_user_fuzz = 1;
    }

    public double[] getStatistics()
    {
        if (l_stat == null)
        {
            return null;
        }
        else
        {
            return (double[]) l_stat.clone();
        }
    }
}
