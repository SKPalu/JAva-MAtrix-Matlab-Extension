/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

import jamaextension.jamax.Matrix;

public class SymmetricEigen
{

    private static final double EPSILON_LARGE = 2.2204460492503E-016D;
    private double eval[];
    private double evec[][];

    public SymmetricEigen(double ad[][])
    {
        this(ad, true);
    }

    public SymmetricEigen(double ad[][], boolean flag)
    {
        new Matrix(ad);// Matrix.checkSquareMatrix(ad);
        int i = ad.length;
        double ad1[][] = new double[3][i + 1];
        int ai[] = new int[i];
        eval = new double[i];
        if (flag)
        {
            evec = new double[i][i];
        }

        if (i == 1)
        {
            eval[0] = ad[0][0];
            if (flag)
            {
                evec[0][0] = 1.0D;
            }
            return;
        }
        double ad2[][] = new Matrix(ad).transpose().getArray();// Matrix.transpose(ad);
        boolean flag1 = true;
        double d = reduce(i, ad2, eval, ad1[1], ad1[2], evec, flag);

        if (d == 0.0D)
        {
            return;
        }
        double d1 = 0.0D;
        for (int j = 0; j < i; j++)
        {
            d1 += eval[j] * eval[j] + ad1[2][j];
        }

        d1 = Math.sqrt(d1 * d);
        double d2 = 4.9406564584124654E-324D;
        if (d2 * 1.7976931348623157E+308D < 1.0D)
        {
            d2 = 5.5626846462680035E-309D;
        }
        int k = 0;
        int l = i - 1;
        label0: do
        {
            for (int i1 = k; i1 < l; i1++)
            {
                double d3 = 2.2204460492503E-016D * (Math.abs(eval[i1]) + Math.abs(ad1[1][i1 + 1]));
                if (d2 < d3)
                {
                    d2 = d3;
                }
                if (Math.abs(ad1[1][i1 + 1]) <= d2)
                {
                    continue;
                }
                k = i1;
                int i3 = k + 1;
                do
                {
                    if (i3 >= l)
                    {
                        break;
                    }
                    if (Math.abs(ad1[1][i3 + 1]) <= d2)
                    {
                        l = i3;
                        break;
                    }
                    i3++;
                }
                while (true);
                System.arraycopy(eval, k, ad1[0], k, (l - k) + 1);
                if (!flag1)
                {
                    for (int j3 = 0; j3 < l - k; j3++)
                    {
                        ad1[2][k + j3] = ad1[1][k + j3] * ad1[1][k + j3];
                    }

                }
                triValues((l - k) + 1, ad1[0], k, ad1[2], k, ai);
                sort(ad1[0]);
                int ai1[] = new int[1];
                int ai2[] = new int[1];
                ai1[0] = k;
                ai2[0] = l;
                triVectors(ai1, ai2, i, eval, ad1[0], ad1[1], d1, evec, flag);
                k = ai1[0];
                l = ai2[0];
                if (k < l)
                {
                    flag1 = false;
                }
                else
                {
                    if (k != l)
                    {
                        continue;
                    }
                    k = l + 1;
                    l = i - 1;
                }
                continue label0;
            }

            if (d != 1.0D)
            {
                for (int j1 = 0; j1 < i; j1++)
                {
                    eval[j1] *= d;
                }

            }
            for (int k1 = 0; k1 < i; k1++)
            {
                ai[k1] = k1;
                ad1[2][k1] = -Math.abs(eval[k1]);
            }

            sort(ad1[2], ai);
            label1: for (int l1 = 0; l1 < i; l1++)
            {
                int k2 = l1;
                do
                {
                    if (k2 >= i)
                    {
                        continue label1;
                    }
                    if (ai[k2] == l1)
                    {
                        int l2 = ai[l1];
                        ai[l1] = k2;
                        ai[k2] = l2;
                        continue label1;
                    }
                    k2++;
                }
                while (true);
            }

            for (int i2 = i - 2; i2 >= 0; i2--)
            {
                if (i2 == ai[i2])
                {
                    continue;
                }
                double d4 = eval[i2];
                eval[i2] = eval[ai[i2]];
                eval[ai[i2]] = d4;
                if (!flag)
                {
                    continue;
                }
                for (int k3 = 0; k3 < i; k3++)
                {
                    double d5 = evec[i2][k3];
                    evec[i2][k3] = evec[ai[i2]][k3];
                    evec[ai[i2]][k3] = d5;
                }

            }

            if (flag)
            {
                for (int j2 = 0; j2 < i; j2++)
                {
                    double d6 = Math.abs(evec[j2][0]);
                    int l3 = 0;
                    for (int i4 = 1; i4 < i; i4++)
                    {
                        if (Math.abs(evec[j2][i4]) > d6)
                        {
                            l3 = i4;
                            d6 = Math.abs(evec[j2][i4]);
                        }
                    }

                    if (evec[j2][l3] >= 0.0D)
                    {
                        continue;
                    }
                    for (int j4 = 0; j4 < i; j4++)
                    {
                        evec[j2][j4] = -evec[j2][j4];
                    }

                }

                evec = new Matrix(evec).transpose().getArray();// Matrix.transpose(evec);
            }
            return;
        }
        while (true);
    }

    private void triValues(int i, double ad[], int j, double ad1[], int k, int ai[])
    {
        if (i == 1)
        {
            return;
        }
        double d = 4.9303806576312658E-032D;
        ad1[0 + k] = 0.0D;
        int l = 0;
        double d1 = 4.9406564584124654E-324D;
        if (d1 * 1.7976931348623157E+308D < 1.0D)
        {
            d1 = 5.5626846462680035E-309D;
        }
        int i1 = 0;
        ai[i1] = 0;
        int j1 = 0;
        int k1 = i - 1;
        boolean flag = false;
        do
        {
            label0:
            {
                double d2 = d * (0.5D * ad1[k1 + k] + ad[k1 + j] * ad[k1 + j]);
                if (d2 > d1)
                {
                    d1 = d2;
                }
                int l1 = k1;
                for (int k2 = l1; k2 >= j1 + 1 && ad1[k2 + k] <= d1; k2--)
                {
                    k1--;
                    l = 0;
                }

                int l2 = k1 - 2;
                do
                {
                    if (l2 < j1)
                    {
                        break;
                    }
                    if (ad1[l2 + 1 + k] <= d1)
                    {
                        i1++;
                        ai[i1] = l2 + 1;
                        break label0;
                    }
                    l2--;
                }
                while (true);
                if (j1 == k1 && i1 > 0)
                {
                    i1--;
                    j1 = ai[i1];
                    continue;
                }
            }
            int i2 = i1;
            for (int j2 = i2; j2 >= 0 && ai[j2] >= k1; j2--)
            {
                i1--;
                k1--;
            }

            if (k1 <= 0)
            {
                break;
            }
            j1 = ai[i1];
            l++;
            double d3 = (ad[(k1 - 1) + j] - ad[k1 + j]) * 0.5D;
            double d5 = Math.sqrt(d3 * d3 + ad1[k1 + k]) + Math.abs(d3);
            if (d3 < 0.0D)
            {
                d5 = -d5;
            }
            double d6 = ad[k1 + j] - ad1[k1 + k] / d5;
            double d7 = 1.0D;
            double d8 = 0.0D;
            double d9 = ad[j1 + j] - d6;
            double d10 = d9 * d9;
            d3 = d10 + ad1[j1 + 1 + k];
            double d11 = 1.0D / d3;
            double d13 = d7;
            d7 = d10 * d11;
            d8 = ad1[j1 + 1 + k] * d11;
            double d15 = d9;
            d9 = d7 * (ad[j1 + 1 + j] - d6) - d8 * d15;
            ad[j1 + j] = d15 + (ad[j1 + 1 + j] - d9);
            d10 = ad1[j1 + 1 + k];
            if (d7 != 0.0D)
            {
                d10 = (d9 * d9) / d7;
            }
            for (int i3 = j1 + 1; i3 < k1; i3++)
            {
                double d4 = d10 + ad1[i3 + 1 + k];
                double d12 = 1.0D / d4;
                ad1[i3 + k] = d8 * d4;
                double d14 = d7;
                d7 = d10 * d12;
                d8 = ad1[i3 + 1 + k] * d12;
                double d16 = d9;
                d9 = d7 * (ad[i3 + 1 + k] - d6) - d8 * d16;
                ad[i3 + j] = d16 + (ad[i3 + 1 + j] - d9);
                if (d7 == 0.0D)
                {
                    d10 = d14 * ad1[i3 + 1 + k];
                }
                else
                {
                    d10 = (d9 * d9) / d7;
                }
            }

            ad1[k1 + k] = d8 * d10;
            ad[k1 + j] = d9 + d6;
            if (l > 100)
            {
                ad[k1 + j] = (0.0D / 0.0D);
                k1--;
            }
        }
        while (true);
    }

    public double[] getValues()
    {
        return eval;
    }

    private double reduce(int i, double ad[][], double ad1[], double ad2[], double ad3[], double ad4[][], boolean flag)
    {
        double d = 0.0D;
        for (int j = 0; j < i; j++)
        {
            ad1[j] = ad[j][j];
            for (int j2 = j; j2 < i; j2++)
            {
                if (Math.abs(ad[j][j2]) > d)
                {
                    d = Math.abs(ad[j][j2]);
                }
            }

        }

        if (d == 0.0D)
        {
            if (Double.isNaN(ad[0][0]))
            {
                d = (0.0D / 0.0D);
                for (int k = 0; k < i; k++)
                {
                    ad1[k] = (0.0D / 0.0D);
                }

                if (flag)
                {
                    for (int l = 0; l < i; l++)
                    {
                        for (int k2 = 0; k2 < i; k2++)
                        {
                            ad4[l][k2] = (0.0D / 0.0D);
                        }

                    }

                }
            }
            else
            {
                for (int i1 = 0; i1 < i; i1++)
                {
                    ad1[i1] = 0.0D;
                }

                if (flag)
                {
                    for (int j1 = 0; j1 < i; j1++)
                    {
                        for (int l2 = 0; l2 < i; l2++)
                        {
                            ad4[j1][l2] = 0.0D;
                        }

                        ad4[j1][j1] = 1.0D;
                    }

                }
            }
        }
        else if (i == 1)
        {
            if (Double.isNaN(ad[0][0]))
            {
                d = (0.0D / 0.0D);
                if (flag)
                {
                    ad4[0][0] = (0.0D / 0.0D);
                }
            }
            else
            {
                d = 1.0D;
                if (flag)
                {
                    ad4[0][0] = 1.0D;
                }
            }
        }
        else
        {
            int k1 = (int) Math.rint(Math.log(d) / 2.3025850929940459D / 0.3010299956639812D);
            double d1 = 0.5D;
            if (d < 1.0D)
            {
                d1 = 1.0D / d1;
                k1 = -k1;
            }
            d = 1.0D;
            for (int i3 = 0; i3 < k1; i3++)
            {
                d *= d1;
            }

            if (d != 1.0D)
            {
                for (int j3 = 0; j3 < i; j3++)
                {
                    for (int i4 = j3; i4 < i; i4++)
                    {
                        ad[j3][i4] = d * ad[j3][i4];
                    }

                }

            }
            for (int l1 = 0; l1 < i - 2; l1++)
            {
                d1 = ad1[l1];
                ad1[l1] = ad[l1][l1];
                ad[l1][l1] = d1;
                ad3[l1 + 1] = 0.0D;
                ad2[l1 + 1] = ad[l1][l1 + 1];
                for (int k3 = l1 + 2; k3 < i; k3++)
                {
                    ad2[k3] = ad[l1][k3];
                    ad3[l1 + 1] += ad2[k3] * ad2[k3];
                }

                if (ad3[l1 + 1] + ad2[l1 + 1] * ad2[l1 + 1] <= 2.2204460492503E-016D)
                {
                    ad2[l1 + 1] = 0.0D;
                    ad[l1][l1 + 1] = 0.0D;
                    ad3[l1 + 1] = 0.0D;
                    continue;
                }
                if (ad3[l1 + 1] <= 2.2204460492503E-016D)
                {
                    ad2[l1 + 1] = ad[l1][l1 + 1];
                    ad[l1][l1 + 1] = 0.0D;
                    ad3[l1 + 1] = ad2[l1 + 1] * ad2[l1 + 1];
                    continue;
                }
                ad3[l1 + 1] += ad2[l1 + 1] * ad2[l1 + 1];
                double d3 = Math.sqrt(ad3[l1 + 1]);
                if (ad2[l1 + 1] < 0.0D)
                {
                    d3 = -d3;
                }
                d3 = -d3;
                double d5 = 1.0D / d3;
                ad[l1][l1 + 1] = d5 * ad2[l1 + 1] - 1.0D;
                for (int j5 = l1 + 2; j5 < i; j5++)
                {
                    ad[l1][j5] = d5 * ad2[j5];
                }

                d5 = 1.0D / ad[l1][l1 + 1];
                ad2[i - 1] = ad[l1][i - 1] * ad[i - 1][i - 1];
                for (int k5 = i - 2; k5 >= l1 + 1; k5--)
                {
                    d1 = ad[l1][k5] * ad[k5][k5];
                    double d6 = ad[l1][k5];
                    for (int k6 = i - 1; k6 >= k5 + 1; k6--)
                    {
                        d1 += ad[l1][k6] * ad[k5][k6];
                        ad2[k6] += d6 * ad[k5][k6];
                    }

                    ad2[k5] = d1;
                }

                d1 = 0.0D;
                for (int l5 = l1 + 1; l5 < i; l5++)
                {
                    d1 += ad2[l5] * ad[l1][l5];
                }

                d1 *= 0.5D * d5;
                for (int i6 = l1 + 1; i6 < i; i6++)
                {
                    ad2[i6] = d5 * (ad2[i6] + d1 * ad[l1][i6]);
                }

                for (int j6 = l1 + 1; j6 < i; j6++)
                {
                    d1 = ad2[j6];
                    double d7 = ad[l1][j6];
                    for (int l6 = j6; l6 < i; l6++)
                    {
                        ad[j6][l6] += d1 * ad[l1][l6] + d7 * ad2[l6];
                    }

                }

                ad2[l1 + 1] = d3;
            }

            d1 = ad1[i - 2];
            ad1[i - 2] = ad[i - 2][i - 2];
            ad[i - 2][i - 2] = d1;
            ad3[i - 1] = ad[i - 2][i - 1] * ad[i - 2][i - 1];
            ad2[i - 1] = ad[i - 2][i - 1];
            d1 = ad1[i - 1];
            ad1[i - 1] = ad[i - 1][i - 1];
            ad[i - 1][i - 1] = d1;
            d = 1.0D / d;
            if (flag)
            {
                for (int l3 = 0; l3 < i; l3++)
                {
                    for (int j4 = 0; j4 < i; j4++)
                    {
                        ad4[l3][j4] = 0.0D;
                    }

                }

                if (i > 1)
                {
                    ad4[i - 2][i - 2] = 1.0D;
                }
                ad4[i - 1][i - 1] = 1.0D;
                label0: for (int i2 = i - 3; i2 >= 0; i2--)
                {
                    ad4[i2][i2] = 1.0D;
                    if (ad[i2][i2 + 1] == 0.0D)
                    {
                        continue;
                    }
                    double d4 = 1.0D / ad[i2][i2 + 1];
                    int k4 = i2 + 1;
                    do
                    {
                        if (k4 >= i)
                        {
                            continue label0;
                        }
                        double d2 = 0.0D;
                        for (int l4 = i2 + 1; l4 < i; l4++)
                        {
                            d2 += ad[i2][l4] * ad4[k4][l4];
                        }

                        d2 *= d4;
                        for (int i5 = i2 + 1; i5 < i; i5++)
                        {
                            ad4[k4][i5] += d2 * ad[i2][i5];
                        }

                        k4++;
                    }
                    while (true);
                }

            }
        }
        ad3[0] = 0.0D;
        ad2[0] = 0.0D;
        return d;
    }

    private void triVectors(int ai[], int ai1[], int i, double ad[], double ad1[], double ad2[], double d,
            double ad3[][], boolean flag)
    {
        if (i == 1)
        {
            return;
        }
        int j = ai[0];
        int l1 = ai1[0];
        double d1 = 4.9406564584124654E-324D;
        if (d1 * 1.7976931348623157E+308D < 1.0D)
        {
            d1 = 5.5626846462680035E-309D;
        }
        int i2 = j;
        label0: do
        {
            if (i2 == l1)
            {
                int k = i2;
                ai[0] = k;
                ai1[0] = l1;
                return;
            }
            int j2 = 0;
            do
            {
                if (Math.abs(ad2[i2 + 1]) <= Math.abs(ad1[i2 + 1]) * 2.2204460492503E-016D)
                {
                    i2++;
                    if (j2 > 2)
                    {
                        int l = i2;
                        ai[0] = l;
                        ai1[0] = l1;
                        return;
                    }
                    continue label0;
                }
                d1 = Math.max(d1, 2.2204460492503E-016D * (Math.abs(ad[i2]) + Math.abs(ad2[i2 + 1])));
                if (Math.abs(ad2[i2 + 1]) <= d1)
                {
                    i2++;
                    if (j2 > 2)
                    {
                        int i1 = i2;
                        ai[0] = i1;
                        ai1[0] = l1;
                        return;
                    }
                    continue label0;
                }
                for (int k2 = i2 + 1; k2 <= l1 - 1; k2++)
                {
                    if (Math.abs(ad2[k2 + 1]) <= d1)
                    {
                        int j1 = i2;
                        ai[0] = j1;
                        ai1[0] = l1;
                        return;
                    }
                }

                int l2 = l1;
                j2++;
                double d2 = ad1[i2 + 1];
                if (j2 > 2)
                {
                    if (j2 >= 5 && Math.abs(ad2[i2 + 1]) <= d * 2.2204460492503E-016D)
                    {
                        int k1 = i2 + 1;
                        ai[0] = k1;
                        ai1[0] = l1;
                        return;
                    }
                    double d3 = (ad[i2 + 1] - ad[i2]) * 0.5D;
                    d2 = Math.sqrt(d3 * d3 + ad2[i2 + 1] * ad2[i2 + 1]) + Math.abs(d3);
                    if (d3 < 0.0D)
                    {
                        d2 = -d2;
                    }
                    d2 = ad[i2] - ad2[i2 + 1] / d2;
                }
                for (int i3 = i2; i3 <= l1; i3++)
                {
                    ad[i3] -= d2;
                }

                double d4 = ad[l1];
                double d5 = 1.0D;
                double d6 = 0.0D;
                int j3 = 0;
                for (int k3 = l1 - 1; k3 >= i2; k3--)
                {
                    j3++;
                    double d7 = d5;
                    double d9 = d6;
                    double d10 = d5 * ad2[k3 + 1];
                    double d11 = d5 * d4;
                    if (Math.abs(d4) < Math.abs(ad2[k3 + 1]))
                    {
                        d5 = d4 / ad2[k3 + 1];
                        double d12 = Math.sqrt(1.0D + d5 * d5);
                        ad2[k3 + 2] = d9 * ad2[k3 + 1] * d12;
                        d6 = 1.0D / d12;
                        d5 *= d6;
                    }
                    else
                    {
                        d6 = ad2[k3 + 1] / d4;
                        double d13 = Math.sqrt(1.0D + d6 * d6);
                        ad2[k3 + 2] = d9 * d4 * d13;
                        d5 = 1.0D / d13;
                        d6 *= d5;
                    }
                    d4 = d5 * ad[k3] - d6 * d10;
                    ad[k3 + 1] = d11 + d6 * (d5 * d10 + d6 * ad[k3]);
                    if (j3 != 2)
                    {
                        continue;
                    }
                    if (flag)
                    {
                        for (int j4 = 0; j4 < i; j4++)
                        {
                            double d14 = d7 * ad3[k3 + 1][j4] - d9 * ad3[k3 + 2][j4];
                            ad3[k3 + 2][j4] = d9 * ad3[k3 + 1][j4] + d7 * ad3[k3 + 2][j4];
                            ad3[k3 + 1][j4] = d6 * ad3[k3][j4] + d5 * d14;
                            ad3[k3][j4] = d5 * ad3[k3][j4] - d6 * d14;
                        }

                    }
                    j3 = 0;
                }

                if (j3 == 1 && flag)
                {
                    for (int l3 = 0; l3 < i; l3++)
                    {
                        double d8 = d6 * ad3[i2][l3] + d5 * ad3[i2 + 1][l3];
                        ad3[i2][l3] = d5 * ad3[i2][l3] - d6 * ad3[i2 + 1][l3];
                        ad3[i2 + 1][l3] = d8;
                    }

                }
                ad2[i2 + 1] = d6 * d4;
                ad[i2] = d5 * d4;
                for (int i4 = i2; i4 <= l1; i4++)
                {
                    ad[i4] += d2;
                }

            }
            while (j2 < 100);
            i2++;
        }
        while (true);
    }

    private void sort(double ad[], int ai[])
    {
        int j;
        for (int i = ad.length; i != 0; i = j + 1)
        {
            j = -1;
            for (int k = 0; k < i - 1; k++)
            {
                if (ad[k] > ad[k + 1])
                {
                    double d = ad[k + 1];
                    int l = ai[k + 1];
                    ad[k + 1] = ad[k];
                    ai[k + 1] = ai[k];
                    ad[k] = d;
                    ai[k] = l;
                    j = k;
                }
            }

        }

    }

    private void sort(double ad[])
    {
        int k;
        for (int i = ad.length; i != 0; i = k + 1)
        {
            k = -1;
            for (int j = 0; j < i - 1; j++)
            {
                if (Math.abs(ad[j]) > Math.abs(ad[j + 1]))
                {
                    double d = ad[j + 1];
                    ad[j + 1] = ad[j];
                    ad[j] = d;
                    k = j;
                }
            }

        }

    }

    public double[][] getVectors()
    {
        return evec;
    }

    public double performanceIndex(double ad[][])
    {
        double d = new Matrix(ad).norm1();// Matrix.oneNorm(ad);
        if (d == 0.0D)
        {
            return 1.7976931348623157E+308D;
        }
        double d1 = 0.0D;
        int i = ad.length;
        double ad1[] = new double[i];
        for (int j = 0; j < i; j++)
        {
            for (int k = 0; k < i; k++)
            {
                ad1[k] = 0.0D;
                for (int i1 = 0; i1 < i; i1++)
                {
                    ad1[k] += ad[k][i1] * evec[i1][j];
                }

            }

            for (int l = 0; l < i; l++)
            {
                ad1[l] -= eval[j] * evec[l][j];
            }

            double d2 = 0.0D;
            double d3 = 0.0D;
            for (int j1 = 0; j1 < i; j1++)
            {
                d2 += Math.abs(ad1[j1]) + Math.abs(ad1[j1]);
                d3 += Math.abs(evec[j1][j]) + Math.abs(evec[j1][j]);
            }

            if (d3 == 0.0D)
            {
                return 1.7976931348623157E+308D;
            }
            d1 = Math.max(d1, d2 / (d * d3 * 10D * 2.2204460492503E-016D * (double) i));
        }

        return d1;
    }
}
