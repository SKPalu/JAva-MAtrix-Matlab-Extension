/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

public class BLAS
{

    private static final double thres[] =
    {
            1.0010415475916E-146D, 4.4989137945432004E+161D, 2.2227587494851002E-162D, 1.9979190722022E+146D,
            5.0104209000224002E-293D, 1.9958403095347001E+292D
    };

    private BLAS()
    {
    }

    public static void copy(int i, double ad[], double ad1[])
    {
        for (int j = 0; j < i; j++)
        {
            ad1[j] = ad[j];
        }

    }

    public static void copy(int i, int ai[], int ai1[])
    {
        for (int j = 0; j < i; j++)
        {
            ai1[j] = ai[j];
        }

    }

    public static void copy(int i, double ad[], int j, int k, double ad1[], int l, int i1)
    {
        if (i <= 0)
        {
            return;
        }
        int k1 = 0;
        int l1 = 0;
        if (k < 0)
        {
            k1 = (-i + 1) * k;
        }
        if (i1 < 0)
        {
            l1 = (-i + 1) * i1;
        }
        for (int j1 = 0; j1 < i; j1++)
        {
            ad1[l + l1] = ad[j + k1];
            k1 += k;
            l1 += i1;
        }

    }

    public static void copy(int i, int ai[], int j, int k, int ai1[], int l, int i1)
    {
        if (i <= 0)
        {
            return;
        }
        int k1 = 0;
        int l1 = 0;
        if (k < 0)
        {
            k1 = (-i + 1) * k;
        }
        if (i1 < 0)
        {
            l1 = (-i + 1) * i1;
        }
        for (int j1 = 0; j1 < i; j1++)
        {
            ai1[l + l1] = ai[j + k1];
            k1 += k;
            l1 += i1;
        }

    }

    public static void set(int i, int j, int ai[], int k)
    {
        for (int l = 0; l < i; l++)
        {
            ai[l + k] = j;
        }

    }

    public static void set(int i, int j, int ai[], int k, int l)
    {
        for (int i1 = 0; i1 < i; i1++)
        {
            ai[i1 * l + k] = j;
        }

    }

    public static void set(int i, double d, double ad[], int j)
    {
        for (int k = 0; k < i; k++)
        {
            ad[k + j] = d;
        }

    }

    public static void set(int i, double d, double ad[], int j, int k)
    {
        if (i > 0)
        {
            if (k != 1)
            {
                if (k < 0)
                {
                    j += (-i + 1) * k;
                }
                for (int l = 0; l < i; l++)
                {
                    ad[j] = d;
                    j += k;
                }

            }
            else
            {
                for (int i1 = 0; i1 < i; i1++)
                {
                    ad[i1 + j] = d;
                }

            }
        }
    }

    public static void scal(int i, double d, double ad[], int j)
    {
        for (int k = 0; k < i; k++)
        {
            ad[k + j] *= d;
        }

    }

    public static void scal(int i, double d, double ad[], int j, int k)
    {
        if (i > 0)
        {
            if (k != 1)
            {
                int i2 = i * k;
                for (int l = 0; l < i2; l += k)
                {
                    ad[l + j] *= d;
                }

            }
            else
            {
                int l1 = i - (i / 5) * 5;
                for (int i1 = 0; i1 < l1; i1++)
                {
                    ad[i1 + j] *= d;
                }

                for (int j1 = l1; j1 < i; j1 += 5)
                {
                    int k1 = j1 + j;
                    ad[k1] *= d;
                    ad[k1 + 1] *= d;
                    ad[k1 + 2] *= d;
                    ad[k1 + 3] *= d;
                    ad[k1 + 4] *= d;
                }

            }
        }
    }

    public static void swap(int i, double ad[], int j, double ad1[], int k)
    {
        for (int l = 0; l < i; l++)
        {
            double d = ad[l + j];
            ad[l + j] = ad1[l + k];
            ad1[l + k] = d;
        }

    }

    public static void swap(int i, double ad[], int j, int k, double ad1[], int l, int i1)
    {
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
                    double d = ad[(j + l1) - 1];
                    ad[(j + l1) - 1] = ad1[(l + i2) - 1];
                    ad1[(l + i2) - 1] = d;
                    l1 += k;
                    i2 += i1;
                }

            }
            else
            {
                for (int k1 = 1; k1 <= i; k1++)
                {
                    double d1 = ad[k1 - 1];
                    ad[(j + k1) - 1] = ad1[(l + k1) - 1];
                    ad1[(l + k1) - 1] = d1;
                }

            }
        }
    }

    public static double dot(int i, double ad[], int j, double ad1[], int k)
    {
        double d = 0.0D;
        for (int l = 0; l < i; l++)
        {
            d += ad[l + j] * ad1[l + k];
        }

        return d;
    }

    public static double dot(int i, double ad[], int j, int k, double ad1[], int l, int i1)
    {
        double d = 0.0D;
        if (i > 0)
        {
            if (k != 1 || i1 != 1)
            {
                int j1 = j;
                int k1 = l;
                if (k < 0)
                {
                    j1 = (-i + 1) * k + j;
                }
                if (i1 < 0)
                {
                    k1 = (-i + 1) * i1 + l;
                }
                for (int i2 = 0; i2 < i; i2++)
                {
                    d += ad[j1] * ad1[k1];
                    j1 += k;
                    k1 += i1;
                }

            }
            else
            {
                int l1 = i % 5;
                for (int j2 = 0; j2 < l1; j2++)
                {
                    d += ad[j2 + j] * ad1[j2 + l];
                }

                for (int k2 = l1; k2 < i; k2 += 5)
                {
                    int l2 = k2 + j;
                    int i3 = k2 + l;
                    d += ad[l2] * ad1[i3] + ad[l2 + 1] * ad1[i3 + 1] + ad[l2 + 2] * ad1[i3 + 2] + ad[l2 + 3]
                            * ad1[i3 + 3] + ad[l2 + 4] * ad1[i3 + 4];
                }

            }
        }
        return d;
    }

    public static void axpy(int i, double d, double ad[], int j, double ad1[], int k)
    {
        for (int l = 0; l < i; l++)
        {
            ad1[l + k] += d * ad[l + j];
        }

    }

    public static void axpy(int i, double d, double ad[], int j, int k, double ad1[], int l, int i1)
    {
        if (i > 0 && d != 0.0D)
        {
            if (k != 1 || i1 != 1)
            {
                int j1 = j;
                int k1 = l;
                if (k < 0)
                {
                    j1 = (-i + 1) * k + j;
                }
                if (i1 < 0)
                {
                    k1 = (-i + 1) * i1 + l;
                }
                for (int i2 = 0; i2 < i; i2++)
                {
                    ad1[k1] += d * ad[j1];
                    j1 += k;
                    k1 += i1;
                }

            }
            else
            {
                int l1 = i % 4;
                for (int j2 = 0; j2 < l1; j2++)
                {
                    ad1[j2 + l] += d * ad[j2 + j];
                }

                for (int k2 = l1; k2 < i; k2 += 4)
                {
                    int l2 = k2 + j;
                    int i3 = k2 + l;
                    ad1[i3] += d * ad[l2];
                    ad1[i3 + 1] += d * ad[l2 + 1];
                    ad1[i3 + 2] += d * ad[l2 + 2];
                    ad1[i3 + 3] += d * ad[l2 + 3];
                }

            }
        }
    }

    public static void rotg(double ad[], double ad1[], double ad2[], double ad3[])
    {
        if (Math.abs(ad[0]) > Math.abs(ad1[0]))
        {
            double d1 = ad[0] + ad[0];
            double d3 = ad1[0] / d1;
            double d = Math.sqrt(0.25D + d3 * d3) * d1;
            ad2[0] = ad[0] / d;
            ad3[0] = d3 * (ad2[0] + ad2[0]);
            ad1[0] = ad3[0];
            ad[0] = d;
        }
        else if (ad1[0] == 0.0D)
        {
            ad2[0] = 1.0D;
            ad3[0] = 0.0D;
            ad[0] = 0.0D;
            ad1[0] = 0.0D;
        }
        else
        {
            double d2 = ad1[0] + ad1[0];
            double d4 = ad[0] / d2;
            ad[0] = Math.sqrt(0.25D + d4 * d4) * d2;
            ad3[0] = ad1[0] / ad[0];
            ad2[0] = d4 * (ad3[0] + ad3[0]);
            if (ad2[0] == 0.0D)
            {
                ad1[0] = 1.0D;
            }
            else
            {
                ad1[0] = 1.0D / ad2[0];
            }
        }
    }

    public static void rotmg(double ad[], double ad1[])
    {
        double d = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;
        double d5 = 0.0D;
        double d7 = 0.0D;
        double d9 = 0.0D;
        double d11 = 0.0D;
        double d13 = 0.0D;
        double d15 = 0.0D;
        double d18 = 0.0D;
        double d19 = 1.0D;
        double d20 = 2D;
        double d21 = 4096D;
        double d22 = 16780000D;
        double d23 = 5.9599999999999998E-008D;
        boolean flag = true;
        if (ad[0] >= d18)
        {
            double d8 = ad[1] * ad[3];
            if (d8 == d18)
            {
                d = -d20;
                ad1[0] = d;
                return;
            }
            double d6 = ad[0] * ad[2];
            double d12 = d8 * ad[3];
            double d10 = d6 * ad[2];
            boolean flag1;
            if (Math.abs(d10) > Math.abs(d12))
            {
                d3 = -ad[3] / ad[2];
                d2 = d8 / d6;
                double d16 = d19 - d2 * d3;
                if (d16 > d18)
                {
                    d = d18;
                    ad[0] /= d16;
                    ad[1] /= d16;
                    ad[2] *= d16;
                    flag1 = true;
                }
                else
                {
                    flag1 = false;
                }
            }
            else if (d12 >= d18)
            {
                d = d19;
                d1 = d6 / d8;
                d4 = ad[2] / ad[3];
                double d17 = d19 + d1 * d4;
                double d14 = ad[1] / d17;
                ad[1] = ad[0] / d17;
                ad[0] = d14;
                ad[2] = ad[3] * d17;
                flag1 = true;
            }
            else
            {
                flag1 = false;
            }
            if (flag1)
            {
                while (ad[0] <= d23)
                {
                    if (d == d18)
                    {
                        d1 = d19;
                        d4 = d19;
                        d = -d19;
                    }
                    else
                    {
                        d3 = -d19;
                        d2 = d19;
                        d = -d19;
                    }
                    ad[0] *= Math.pow(d21, 2D);
                    ad[2] /= d21;
                    d1 /= d21;
                    d2 /= d21;
                }
                if (ad[0] != d18)
                {
                    while (ad[0] >= d22)
                    {
                        if (d == d18)
                        {
                            d1 = d19;
                            d4 = d19;
                            d = -d19;
                        }
                        else
                        {
                            d3 = -d19;
                            d2 = d19;
                            d = -d19;
                        }
                        ad[0] = ad[0] / Math.pow(d21, 2D);
                        ad[2] *= d21;
                        d1 *= d21;
                        d2 *= d21;
                    }
                }
                if (ad[1] != d18)
                {
                    while (Math.abs(ad[1]) <= d23)
                    {
                        if (d == d18)
                        {
                            d1 = d19;
                            d4 = d19;
                            d = -d19;
                        }
                        else
                        {
                            d3 = -d19;
                            d2 = d19;
                            d = -d19;
                        }
                        ad[1] *= Math.pow(d21, 2D);
                        d3 /= d21;
                        d4 /= d21;
                    }
                    while (Math.abs(ad[1]) >= d22)
                    {
                        if (d == d18)
                        {
                            d1 = d19;
                            d4 = d19;
                            d = -d19;
                        }
                        else
                        {
                            d3 = -d19;
                            d2 = d19;
                            d = -d19;
                        }
                        ad[1] = ad[1] / Math.pow(d21, 2D);
                        d3 *= d21;
                        d4 *= d21;
                    }
                }
            }
            else
            {
                d = -d19;
                d1 = d18;
                d2 = d18;
                d3 = d18;
                d4 = d18;
                ad[0] = d18;
                ad[1] = d18;
                ad[2] = d18;
            }
        }
        else
        {
            d = -d19;
            d1 = d18;
            d2 = d18;
            d3 = d18;
            d4 = d18;
            ad[0] = d18;
            ad[1] = d18;
            ad[2] = d18;
        }
        if (d < d18)
        {
            ad1[1] = d1;
            ad1[2] = d3;
            ad1[3] = d2;
            ad1[4] = d4;
        }
        else if (d == d18)
        {
            ad1[2] = d3;
            ad1[3] = d2;
        }
        else
        {
            ad1[1] = d1;
            ad1[4] = d4;
        }
        ad1[0] = d;
    }

    public static void rotm(int i, double ad[], int j, int k, double ad1[], int l, int i1, double ad2[])
    {
        double d = ad2[0];
        if (i > 0 && d != -2D)
        {
            if (k == 1 && i1 == 1)
            {
                if (d == 0.0D)
                {
                    double d5 = ad2[3];
                    double d9 = ad2[2];
                    for (int j1 = 1; j1 <= i; j1++)
                    {
                        double d17 = ad[(j + j1) - 1];
                        double d23 = ad1[(l + j1) - 1];
                        ad[(j + j1) - 1] = d17 + d23 * d5;
                        ad1[(l + j1) - 1] = d17 * d9 + d23;
                    }

                }
                else if (d > 0.0D)
                {
                    double d1 = ad2[1];
                    double d13 = ad2[4];
                    for (int k1 = 1; k1 <= i; k1++)
                    {
                        double d18 = ad[(j + k1) - 1];
                        double d24 = ad1[(l + k1) - 1];
                        ad[(j + k1) - 1] = d18 * d1 + d24;
                        ad1[(l + k1) - 1] = -d18 + d13 * d24;
                    }

                }
                else if (d < 0.0D)
                {
                    double d2 = ad2[1];
                    double d6 = ad2[3];
                    double d10 = ad2[2];
                    double d14 = ad2[4];
                    for (int l1 = 1; l1 <= i; l1++)
                    {
                        double d19 = ad[(j + l1) - 1];
                        double d25 = ad1[(l + l1) - 1];
                        ad[(j + l1) - 1] = d19 * d2 + d25 * d6;
                        ad1[(l + l1) - 1] = d19 * d10 + d25 * d14;
                    }

                }
            }
            else
            {
                int l2 = 1;
                int i3 = 1;
                if (k < 0)
                {
                    l2 = 1 + (1 - i) * k;
                }
                if (i1 < 0)
                {
                    i3 = 1 + (1 - i) * i1;
                }
                if (d == 0.0D)
                {
                    double d7 = ad2[3];
                    double d11 = ad2[2];
                    for (int i2 = 1; i2 <= i; i2++)
                    {
                        double d20 = ad[(j + l2) - 1];
                        double d26 = ad1[(l + i3) - 1];
                        ad[(j + l2) - 1] = d20 + d26 * d7;
                        ad1[(l + i3) - 1] = d20 * d11 + d26;
                        l2 += k;
                        i3 += i1;
                    }

                }
                else if (d > 0.0D)
                {
                    double d3 = ad2[1];
                    double d15 = ad2[4];
                    for (int j2 = 1; j2 <= i; j2++)
                    {
                        double d21 = ad[(j + l2) - 1];
                        double d27 = ad1[(l + i3) - 1];
                        ad[(j + l2) - 1] = d21 * d3 + d27;
                        ad1[(l + i3) - 1] = -d21 + d15 * d27;
                        l2 += k;
                        i3 += i1;
                    }

                }
                else if (d < 0.0D)
                {
                    double d4 = ad2[1];
                    double d8 = ad2[3];
                    double d12 = ad2[2];
                    double d16 = ad2[4];
                    for (int k2 = 1; k2 <= i; k2++)
                    {
                        double d22 = ad[(j + l2) - 1];
                        double d28 = ad1[(l + i3) - 1];
                        ad[(j + l2) - 1] = d22 * d4 + d28 * d8;
                        ad1[(l + i3) - 1] = d22 * d12 + d28 * d16;
                        l2 += k;
                        i3 += i1;
                    }

                }
            }
        }
    }

    public static void rotm(int i, double ad[], int j, double ad1[], int k, double ad2[])
    {
        rotm(i, ad, 0, j, ad1, 0, k, ad2);
    }

    public static double nrm2(int i, double ad[], int j)
    {
        double d = 0.0D;
        double d4 = 0.0D;
        for (int k = 0; k < i; k++)
        {
            d += Math.abs(ad[k + j]);
            if (d > thres[3])
            {
                d = thres[4];
                double d2 = thres[5];
                d4 = 0.0D;
                for (int j1 = 0; j1 < i; j1++)
                {
                    d4 += d * ad[j1 + j] * (d * ad[j1 + j]);
                }

                d4 = Math.sqrt(d4) * d2;
                return d4;
            }
        }

        if (d < (double) i * thres[0])
        {
            double d1 = thres[1];
            double d3 = thres[2];
            d4 = 0.0D;
            for (int l = 0; l < i; l++)
            {
                d4 += d1 * ad[l + j] * (d1 * ad[l + j]);
            }

            d4 = Math.sqrt(d4) * d3;
        }
        else
        {
            for (int i1 = 0; i1 < i; i1++)
            {
                d4 += ad[i1 + j] * ad[i1 + j];
            }

            d4 = Math.sqrt(d4);
        }
        return d4;
    }

    public static double nrm2(int i, double ad[], int j, int k)
    {
        double ad1[] = new double[6];
        double d6 = 0.0D;
        double d = 1.0D;
        ad1[0] = 1.0010415475916E-146D;
        ad1[1] = 4.4989137945432004E+161D;
        ad1[2] = 2.2227587494851002E-162D;
        ad1[3] = 1.9979190722022E+146D;
        ad1[4] = 5.0104209000224002E-293D;
        ad1[5] = 1.9958403095347001E+292D;
        double d5 = d6;
        if (i > 0 && k >= 0)
        {
            if (k == 0)
            {
                d5 = i;
                d5 = Math.abs(ad[0]) * Math.sqrt(d5);
            }
            else
            {
                double d1 = d6;
                for (int l = 1; l <= i * k; l++)
                {
                    d1 += Math.abs(ad[(l - 1) + j]);
                    if (d1 > ad1[3])
                    {
                        d1 = ad1[4];
                        double d3 = ad1[5];
                        d5 = d6;
                        for (int j1 = 1; j1 <= i * k; j1++)
                        {
                            d5 += d1 * ad[(j1 - 1) + j] * (d1 * ad[(j1 - 1) + j]);
                        }

                        d5 = Math.sqrt(d5) * d3;
                        return d5;
                    }
                }

                if (d1 < (double) i * ad1[0])
                {
                    double d2 = ad1[1];
                    double d4 = ad1[2];
                    d5 = d6;
                    for (int k1 = 1; k1 <= i * k; k1++)
                    {
                        d5 += d2 * ad[(k1 - 1) + j] * (d2 * ad[(k1 - 1) + j]);
                    }

                    d5 = Math.sqrt(d5) * d4;
                    return d5;
                }
                for (int i1 = 1; i1 <= i * k; i1++)
                {
                    d5 += ad[(i1 - 1) + j] * ad[(i1 - 1) + j];
                }

                d5 = Math.sqrt(d5);
                return d5;
            }
        }
        return d5;
    }

    public static void rot(int i, double ad[], int j, double ad1[], int k, double ad2[], double ad3[])
    {
        if (i <= 0)
        {
            return;
        }
        for (int l = 0; l < i; l++)
        {
            double d = ad2[0] * ad[l + j] + ad3[0] * ad1[l + k];
            ad1[l + k] = ad2[0] * ad1[l + k] - ad3[0] * ad[l + j];
            ad[l + j] = d;
        }

    }

    public static void rot(int i, double ad[], int j, int k, double ad1[], int l, int i1, double ad2[], double ad3[])
    {
        if (i > 0)
        {
            if (k != 1 || i1 != 1)
            {
                int l1 = 1 + j;
                int i2 = 1 + l;
                if (k < 0)
                {
                    l1 = (-i + 1) * k + 1 + j;
                }
                if (i1 < 0)
                {
                    i2 = (-i + 1) * i1 + 1 + l;
                }
                for (int j1 = 0; j1 < i; j1++)
                {
                    double d = ad2[0] * ad[l1] + ad3[0] * ad1[i2];
                    ad1[i2] = ad2[0] * ad1[i2] - ad3[0] * ad[l1];
                    ad[l1] = d;
                    l1 += k;
                    i2 += i1;
                }

            }
            else
            {
                for (int k1 = 0; k1 < i; k1++)
                {
                    double d1 = ad2[0] * ad[k1 + j] + ad3[0] * ad1[k1 + l];
                    ad1[k1 + l] = ad2[0] * ad1[k1 + l] - ad3[0] * ad[k1 + j];
                    ad[k1 + j] = d1;
                }

            }
        }
    }

    static void ger(int i, int j, double d, double ad[], int k, double ad1[], int l, double ad2[][], int i1, int j1)
    {
        if (i == 0 || j == 0 || d == 0.0D)
        {
            return;
        }
        double ad3[] = new double[Math.max(i, j)];
        int k1 = l;
        for (int l1 = 0; l1 < j; l1++)
        {
            System.arraycopy(ad2[i1 + l1], j1, ad3, 0, i);
            axpy(i, d * ad1[k1], ad, k, ad3, 0);
            System.arraycopy(ad3, 0, ad2[i1 + l1], j1, i);
            k1++;
        }

    }

    public static void ger(int i, int j, double d, double ad[], int k, int l, double ad1[], int i1, int j1,
            double ad2[], int k1, int l1)
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
            axpy(i, d * ad1[(i1 + j2) - 1], ad, k, l, ad2, (k1 + i2) - 1, 1);
            j2 += j1;
            i2 += l1;
        }

    }

    public static void gemv(char c, int i, int j, double d, double ad[][], int k, int l, double ad1[], int i1,
            double d1, double ad2[], int j1)
    {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        if (c == 'N' || c == 'n')
        {
            flag = true;
        }
        if (c == 'T' || c == 't')
        {
            flag1 = true;
        }
        if (c == 'C' || c == 'c')
        {
            flag2 = true;
        }
        if (i == 0 || j == 0 || d == 0.0D && d1 == 1.0D)
        {
            return;
        }
        int i2;
        if (flag)
        {
            int k1 = j;
            i2 = i;
        }
        else
        {
            int l1 = i;
            i2 = j;
        }
        double ad3[] = new double[Math.max(i, j)];
        if (d1 != 1.0D)
        {
            if (d1 == 0.0D)
            {
                set(i2, 0.0D, ad2, j1);
            }
            else
            {
                scal(i2, d1, ad2, j1);
            }
        }
        if (d != 0.0D)
        {
            if (flag)
            {
                int j2 = i1;
                for (int l2 = 0; l2 < j; l2++)
                {
                    System.arraycopy(ad[k + l2], l, ad3, 0, i);
                    axpy(i, d * ad1[j2], ad3, 0, ad2, j1);
                    System.arraycopy(ad3, 0, ad[k + l2], l, i);
                    j2++;
                }

            }
            else
            {
                int k2 = j1;
                for (int i3 = 0; i3 < j; i3++)
                {
                    System.arraycopy(ad[k + i3], l, ad3, 0, i);
                    ad2[k2] += d * dot(i, ad3, 0, ad1, i1);
                    System.arraycopy(ad3, 0, ad[k + i3], l, i);
                    k2++;
                }

            }
        }
    }

    public static void gemv(char c, int i, int j, double d, double ad[], int k, int l, double ad1[], int i1, int j1,
            double d1, double ad2[], int k1, int l1)
    {
        boolean flag1 = c == 'N' || c == 'n';
        boolean flag2 = c == 'T' || c == 't';
        boolean flag = c == 'C' || c == 'c';
        if (i == 0 || j == 0 || d == 0.0D && d1 == 1.0D)
        {
            return;
        }
        int k3;
        int l3;
        if (flag1)
        {
            k3 = j;
            l3 = i;
        }
        else
        {
            k3 = i;
            l3 = j;
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
                set(l3, 0.0D, ad2, k1, Math.abs(l1));
            }
            else
            {
                scal(l3, d1, ad2, k1, Math.abs(l1));
            }
        }
        if (d == 0.0D)
        {
            return;
        }
        if (flag1)
        {
            int i3 = k2;
            for (int i2 = 1; i2 <= j; i2++)
            {
                axpy(i, d * ad1[(i1 + i3) - 1], ad, k + l * (i2 - 1), 1, ad2, k1, l1);
                i3 += j1;
            }

        }
        else
        {
            int j3 = l2;
            for (int j2 = 1; j2 <= j; j2++)
            {
                ad2[(k1 + j3) - 1] += d * dot(i, ad, k + l * (j2 - 1), 1, ad1, i1, j1);
                j3 += l1;
            }

        }
    }

    public static double asum(int i, double ad[], int j, int k)
    {
        double d = 0.0D;
        if (i > 0)
        {
            if (k != 1)
            {
                int l1 = i * k;
                int j1 = 1;
                int i1 = k;
                for (int l = ((l1 - 1) + i1) / i1 <= 0 ? 0 : ((l1 - 1) + i1) / i1; l > 0; l--)
                {
                    d += Math.abs(ad[(j + j1) - 1]);
                    j1 += i1;
                }

            }
            else
            {
                for (int k1 = 1; k1 <= i; k1++)
                {
                    d += Math.abs(ad[(j + k1) - 1]);
                }

            }
        }
        return d;
    }

    public static void trsv(char c, char c1, char c2, int i, double ad[], int j, int k, double ad1[], int l, int i1)
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
                if (i1 > 0)
                {
                    int j3 = 1;
                    for (int j1 = 1; j1 <= i; j1++)
                    {
                        ad1[(l + j3) - 1] -= dot(j1 - 1, ad, j + k * (j1 - 1), 1, ad1, l, i1);
                        if (flag)
                        {
                            ad1[(l + j3) - 1] /= ad[(j + j1 + k * (j1 - 1)) - 1];
                        }
                        j3 += i1;
                    }

                }
                else
                {
                    int k3 = (-i + 1) * i1 + 1;
                    for (int k1 = 1; k1 <= i; k1++)
                    {
                        ad1[(l + k3) - 1] -= dot(k1 - 1, ad, j + k * (k1 - 1), 1, ad1, (l + k3) - i1 - 1, i1);
                        if (flag)
                        {
                            ad1[(l + k3) - 1] /= ad[(j + k1 + k * (k1 - 1)) - 1];
                        }
                        k3 += i1;
                    }

                }
            }
            else if (i1 > 0)
            {
                int l3 = (i - 1) * i1 + 1;
                for (int l1 = i; l1 >= 1; l1--)
                {
                    if (l1 < i)
                    {
                        ad1[(l + l3) - 1] -= dot(i - l1, ad, (j + l1 + k * l1) - 1, k, ad1, (l + l3 + i1) - 1, i1);
                    }
                    if (flag)
                    {
                        ad1[(l + l3) - 1] /= ad[(j + l1 + k * (l1 - 1)) - 1];
                    }
                    l3 -= i1;
                }

            }
            else
            {
                int i4 = 1;
                for (int i2 = i; i2 >= 1; i2--)
                {
                    if (i2 < i)
                    {
                        ad1[(l + i4) - 1] -= dot(i - i2, ad, (j + i2 + k * i2) - 1, k, ad1, l, i1);
                    }
                    if (flag)
                    {
                        ad1[(l + i4) - 1] /= ad[(j + i2 + k * (i2 - 1)) - 1];
                    }
                    i4 -= i1;
                }

            }
        }
        else if (flag1)
        {
            if (i1 > 0)
            {
                int j4 = (i - 1) * i1 + 1;
                for (int j2 = i; j2 >= 1; j2--)
                {
                    if (j2 < i)
                    {
                        ad1[(l + j4) - 1] -= dot(i - j2, ad, j + j2 + k * (j2 - 1), 1, ad1, (l + j4 + i1) - 1, i1);
                    }
                    if (flag)
                    {
                        ad1[(l + j4) - 1] /= ad[(j + j2 + k * (j2 - 1)) - 1];
                    }
                    j4 -= i1;
                }

            }
            else
            {
                int k4 = 1;
                for (int k2 = i; k2 >= 1; k2--)
                {
                    if (k2 < i)
                    {
                        ad1[(l + k4) - 1] -= dot(i - k2, ad, j + k2 + k * (k2 - 1), 1, ad1, l, i1);
                    }
                    if (flag)
                    {
                        ad1[(l + k4) - 1] /= ad[(j + k2 + k * (k2 - 1)) - 1];
                    }
                    k4 -= i1;
                }

            }
        }
        else if (i1 > 0)
        {
            int l4 = 1;
            for (int l2 = 1; l2 <= i; l2++)
            {
                ad1[(l + l4) - 1] -= dot(l2 - 1, ad, (j + l2) - 1, k, ad1, l, i1);
                if (flag)
                {
                    ad1[(l + l4) - 1] /= ad[(j + l2 + k * (l2 - 1)) - 1];
                }
                l4 += i1;
            }

        }
        else
        {
            int i5 = (-i + 1) * i1 + 1;
            for (int i3 = 1; i3 <= i; i3++)
            {
                ad1[(l + i5) - 1] -= dot(i3 - 1, ad, (j + i3) - 1, k, ad1, (l + i5) - i1 - 1, i1);
                if (flag)
                {
                    ad1[(l + i5) - 1] /= ad[(j + i3 + k * (i3 - 1)) - 1];
                }
                i5 += i1;
            }

        }
    }

    public static double sum(int i, double ad[], int j, int k)
    {
        double d = 0.0D;
        if (i > 0)
        {
            if (k != 1)
            {
                int j1 = i * k;
                for (int l = 1; l <= j1; l += k)
                {
                    d += ad[(j + l) - 1];
                }

            }
            else
            {
                for (int i1 = 1; i1 <= i; i1++)
                {
                    d += ad[(j + i1) - 1];
                }

            }
        }
        return d;
    }

    public static int sum(int i, int ai[], int j, int k)
    {
        int l1 = 0;
        if (i > 0)
        {
            if (k != 1)
            {
                int i2 = i * k;
                int j1 = 1;
                int i1 = k;
                for (int l = ((i2 - 1) + i1) / i1 <= 0 ? 0 : ((i2 - 1) + i1) / i1; l > 0; l--)
                {
                    l1 += ai[(j + j1) - 1];
                    j1 += i1;
                }

            }
            else
            {
                for (int k1 = 1; k1 <= i; k1++)
                {
                    l1 += ai[(j + k1) - 1];
                }

            }
        }
        return l1;
    }

    public static void gemm(char c, char c1, int i, int j, int k, double d, double ad[], int l, int i1, double ad1[],
            int j1, int k1, double d1, double ad2[], int l1, int i2)
    {
        boolean flag = c == 'N' || c == 'n';
        boolean flag1 = c1 == 'N' || c1 == 'n';
        boolean flag2 = c == 'T' || c == 't' || c == 'C' || c == 'c';
        boolean flag3 = c1 == 'T' || c1 == 't' || c1 == 'C' || c1 == 'c';
        if (i == 0 || j == 0 || (d == 0.0D || k == 0) && d1 == 1.0D)
        {
            return;
        }
        if (d1 == 0.0D)
        {
            for (int i4 = 1; i4 <= j; i4++)
            {
                for (int j2 = 1; j2 <= i; j2++)
                {
                    ad2[l1 + (i4 - 1) * i2 + (j2 - 1)] = 0.0D;
                }

            }

        }
        else if (d1 == -1D)
        {
            for (int j4 = 1; j4 <= j; j4++)
            {
                for (int k2 = 1; k2 <= i; k2++)
                {
                    ad2[l1 + (j4 - 1) * i2 + (k2 - 1)] = -ad2[l1 + (j4 - 1) * i2 + (k2 - 1)];
                }

            }

        }
        else if (d1 != 1.0D)
        {
            for (int k4 = 1; k4 <= j; k4++)
            {
                for (int l2 = 1; l2 <= i; l2++)
                {
                    ad2[l1 + (k4 - 1) * i2 + (l2 - 1)] *= d1;
                }

            }

        }
        if (k == 0 || d == 0.0D)
        {
            return;
        }
        if (flag1 && flag)
        {
            for (int l5 = 1; l5 <= k; l5++)
            {
                for (int l4 = 1; l4 <= j; l4++)
                {
                    double d2 = d * ad1[j1 + (l4 - 1) * k1 + (l5 - 1)];
                    for (int i3 = 1; i3 <= i; i3++)
                    {
                        ad2[l1 + (l4 - 1) * i2 + (i3 - 1)] += d2 * ad[l + (l5 - 1) * i1 + (i3 - 1)];
                    }

                }

            }

        }
        else if (flag1 && flag2)
        {
            for (int i6 = 1; i6 <= k; i6++)
            {
                for (int i5 = 1; i5 <= j; i5++)
                {
                    double d3 = d * ad1[j1 + (i5 - 1) * k1 + (i6 - 1)];
                    for (int j3 = 1; j3 <= i; j3++)
                    {
                        ad2[l1 + (i5 - 1) * i2 + (j3 - 1)] += d3 * ad[l + (j3 - 1) * i1 + (i6 - 1)];
                    }

                }

            }

        }
        else if (flag3 && flag2)
        {
            for (int j6 = 1; j6 <= k; j6++)
            {
                for (int j5 = 1; j5 <= j; j5++)
                {
                    double d4 = d * ad1[j1 + (j6 - 1) * k1 + (j5 - 1)];
                    for (int k3 = 1; k3 <= i; k3++)
                    {
                        ad2[l1 + (j5 - 1) * i2 + (k3 - 1)] += d4 * ad[l + (k3 - 1) * i1 + (j6 - 1)];
                    }

                }

            }

        }
        else if (flag3 && flag)
        {
            for (int k6 = 1; k6 <= k; k6++)
            {
                for (int k5 = 1; k5 <= j; k5++)
                {
                    double d5 = d * ad1[j1 + (k6 - 1) * k1 + (k5 - 1)];
                    for (int l3 = 1; l3 <= i; l3++)
                    {
                        ad2[l1 + (k5 - 1) * i2 + (l3 - 1)] += d5 * ad[l + (k6 - 1) * i1 + (l3 - 1)];
                    }

                }

            }

        }
    }

    public static void dvcal(int i, double d, double ad[], int j, int k, double ad1[], int l, int i1)
    {
        if (i > 0)
        {
            if (k != 1 || i1 != 1)
            {
                int l1 = 1;
                int i2 = 1;
                for (int j1 = 1; j1 <= i; j1++)
                {
                    ad1[(l + i2) - 1] = d * ad[(j + l1) - 1];
                    l1 += k;
                    i2 += i1;
                }

            }
            else
            {
                for (int k1 = 1; k1 <= i; k1++)
                {
                    ad1[(l + k1) - 1] = d * ad[(j + k1) - 1];
                }

            }
        }
    }

    public static int iamax(int i, double ad[], int j, int k)
    {
        int k1 = 0;
        if (i >= 1)
        {
            k1 = 1;
            if (i > 1)
            {
                if (k != 1)
                {
                    double d = Math.abs(ad[j + 0]);
                    int l1 = i * k;
                    int j1 = 1;
                    for (int l = 1; l <= l1; l += k)
                    {
                        double d2 = Math.abs(ad[(j + l) - 1]);
                        if (d2 > d)
                        {
                            k1 = j1;
                            d = d2;
                        }
                        j1++;
                    }

                }
                else
                {
                    double d1 = Math.abs(ad[j + 0]);
                    for (int i1 = 2; i1 <= i; i1++)
                    {
                        double d3 = Math.abs(ad[(j + i1) - 1]);
                        if (d3 > d1)
                        {
                            k1 = i1;
                            d1 = d3;
                        }
                    }

                }
            }
        }
        return k1;
    }

    public static int iimax(int i, int ai[], int j, int k)
    {
        int j1 = 0;
        if (i >= 1)
        {
            j1 = 1;
            if (i != 1)
            {
                if (k != 1)
                {
                    int i2 = 1;
                    int k1 = ai[j + 0];
                    i2 += k;
                    for (int l = 2; l <= i; l++)
                    {
                        if (ai[(j + i2) - 1] > k1)
                        {
                            j1 = l;
                            k1 = ai[(j + i2) - 1];
                        }
                        i2 += k;
                    }

                }
                else
                {
                    int l1 = ai[j + 0];
                    for (int i1 = 2; i1 <= i; i1++)
                    {
                        if (ai[(j + i1) - 1] > l1)
                        {
                            j1 = i1;
                            l1 = ai[(j + i1) - 1];
                        }
                    }

                }
            }
        }
        return j1;
    }

    public static int iimin(int i, int ai[], int j, int k)
    {
        int j1 = 0;
        if (i >= 1)
        {
            j1 = 1;
            if (i != 1)
            {
                if (k != 1)
                {
                    int i2 = 1;
                    int k1 = ai[j + 0];
                    i2 += k;
                    for (int l = 2; l <= i; l++)
                    {
                        if (ai[(j + i2) - 1] < k1)
                        {
                            j1 = l;
                            k1 = ai[(j + i2) - 1];
                        }
                        i2 += k;
                    }

                }
                else
                {
                    int l1 = ai[j + 0];
                    for (int i1 = 2; i1 <= i; i1++)
                    {
                        if (ai[(j + i1) - 1] < l1)
                        {
                            j1 = i1;
                            l1 = ai[(j + i1) - 1];
                        }
                    }

                }
            }
        }
        return j1;
    }

    public static int ismax(int i, double ad[], int j, int k)
    {
        int j1 = 0;
        if (i >= 1)
        {
            j1 = 1;
            if (i != 1)
            {
                if (k != 1)
                {
                    int k1 = 1;
                    double d = ad[j + 0];
                    k1 += k;
                    for (int l = 2; l <= i; l++)
                    {
                        if (ad[(j + k1) - 1] > d)
                        {
                            j1 = l;
                            d = ad[(j + k1) - 1];
                        }
                        k1 += k;
                    }

                }
                else
                {
                    double d1 = ad[j + 0];
                    for (int i1 = 2; i1 <= i; i1++)
                    {
                        if (ad[(j + i1) - 1] > d1)
                        {
                            j1 = i1;
                            d1 = ad[(j + i1) - 1];
                        }
                    }

                }
            }
        }
        return j1;
    }

    public static int ismin(int i, double ad[], int j, int k)
    {
        int j1 = 0;
        if (i >= 1)
        {
            j1 = 1;
            if (i != 1)
            {
                if (k != 1)
                {
                    int k1 = 1;
                    double d = ad[j + 0];
                    k1 += k;
                    for (int l = 2; l <= i; l++)
                    {
                        if (ad[(j + k1) - 1] < d)
                        {
                            j1 = l;
                            d = ad[(j + k1) - 1];
                        }
                        k1 += k;
                    }

                }
                else
                {
                    double d1 = ad[j + 0];
                    for (int i1 = 2; i1 <= i; i1++)
                    {
                        if (ad[(j + i1) - 1] < d1)
                        {
                            j1 = i1;
                            d1 = ad[(j + i1) - 1];
                        }
                    }

                }
            }
        }
        return j1;
    }

    public static void add(int i, double d, double ad[], int j, int k)
    {
        if (i > 0)
        {
            if (k != 1)
            {
                int j1 = 1;
                for (int l = 1; l <= i; l++)
                {
                    ad[(j + j1) - 1] += d;
                    j1 += k;
                }

            }
            else
            {
                for (int i1 = 1; i1 <= i; i1++)
                {
                    ad[(j + i1) - 1] += d;
                }

            }
        }
    }

    public static double xyz(int i, double ad[], int j, double ad1[], int k, double ad2[], int l)
    {
        double d = 0.0D;
        if (i <= 0)
        {
            return d;
        }
        if (j != 1 || k != 1 || l != 1)
        {
            int k1 = 1;
            int l1 = 1;
            int i2 = 1;
            if (j < 0)
            {
                k1 = (-i + 1) * j + 1;
            }
            if (k < 0)
            {
                l1 = (-i + 1) * k + 1;
            }
            if (l < 0)
            {
                i2 = (-i + 1) * l + 1;
            }
            for (int i1 = 1; i1 <= i; i1++)
            {
                d += ad[k1 - 1] * ad1[l1 - 1] * ad2[i2 - 1];
                k1 += j;
                l1 += k;
                i2 += l;
            }

        }
        else
        {
            for (int j1 = 1; j1 <= i; j1++)
            {
                d += ad[j1 - 1] * ad1[j1 - 1] * ad2[j1 - 1];
            }

        }
        return d;
    }

    public static double xyz(int i, double ad[], int j, int k, double ad1[], int l, int i1, double ad2[], int j1, int k1)
    {
        double d = 0.0D;
        if (i <= 0)
        {
            return d;
        }
        if (k != 1 || i1 != 1 || k1 != 1)
        {
            int j2 = 1;
            int k2 = 1;
            int l2 = 1;
            if (k < 0)
            {
                j2 = (-i + 1) * k + 1;
            }
            if (i1 < 0)
            {
                k2 = (-i + 1) * i1 + 1;
            }
            if (k1 < 0)
            {
                l2 = (-i + 1) * k1 + 1;
            }
            for (int l1 = 1; l1 <= i; l1++)
            {
                d += ad[(j + j2) - 1] * ad1[(l + k2) - 1] * ad2[(j1 + l2) - 1];
                j2 += k;
                k2 += i1;
                l2 += k1;
            }

        }
        else
        {
            for (int i2 = 1; i2 <= i; i2++)
            {
                d += ad[(j + i2) - 1] * ad1[(l + i2) - 1] * ad2[(j1 + i2) - 1];
            }

        }
        return d;
    }

}
