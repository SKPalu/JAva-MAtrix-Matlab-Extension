/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

import java.io.Serializable;

import jamaextension.jamax.Matrix;

public abstract class BSpline implements Serializable, Cloneable
{
    /*
     * private class BsplineToSpline extends Spline {
     * 
     * private static final long serialVersionUID = 0x9992db8250c81540L;
     * 
     * private BsplineToSpline() { int j = coef.length; int ai[] = new int[1];
     * double ad[] = new double[(j - order) + 2]; double ad1[] = new double[((j
     * - order) + 1) * order];
     * 
     * b2cpp(order, knot, j, coef, ai, ad, ad1);
     * 
     * //b2cpp(int i, double ad[], int j, double ad1[], int ai[], double ad2[],
     * double ad3[])
     * 
     * int i = ai[0]; breakPoint = new double[i + 1]; coef = new double[i +
     * 1][order]; for(int k = 0; k < i + 1; k++) {breakPoint[k] = ad[k];}
     * 
     * for(int l = 0; l < i; l++) { for(int i1 = 0; i1 < order; i1++) {
     * coef[l][i1] = ad1[l * order + i1];} }
     * 
     * }
     * 
     * }
     */

    static final long serialVersionUID = 0x52b6c28cf63e3678L;
    protected int order;
    protected double knot[];
    protected double coef[];

    public BSpline()
    {
    }

    public double value(double d)
    {
        return derivative(d, 0);
    }

    public double derivative(double d)
    {
        return derivative(d, 1);
    }

    public double derivative(double d, int i)
    {
        double d1 = b2der(i, d, order, knot, coef.length, coef);
        return d1;
    }

    public Matrix value(Matrix A)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException("derivative : Parameter \"A\" must be a vector and not a matrix.");
        }
        return derivative(A, 0);
    }

    public Matrix derivative(Matrix A, int i)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException("derivative : Parameter \"A\" must be a vector and not a matrix.");
        }
        double ad[] = A.getRowPackedCopy();
        double ad1[] = new double[ad.length];
        b21gd(i, ad.length, ad, order, knot, coef.length, coef, ad1);
        return new Matrix(ad1);
    }

    public Matrix getKnots()
    {
        return new Matrix(knot.clone());
    }

    public double integral(double d, double d1)
    {
        double d2 = 0.0D;
        d2 = b2itg(d, d1, order, knot, coef.length, coef);
        return d2;
    }

    // public Spline getSpline() { return new BsplineToSpline(); }
    private void p3der(int i, int j, double ad[], double d, int ai[])
    {
        int k = ai[0] + 1;
        if (k >= j)
        {
            if (d >= ad[j - 1])
            {
                ai[0] = j;
                return;
            }
            if (j <= 1)
            {
                ai[0] = 1;
                return;
            }
            ai[0] = j - 1;
            k = j;
        }
        if (d < ad[k - 1])
        {
            if (d >= ad[ai[0] - 1])
            {
                return;
            }
            int i1 = 1;
            do
            {
                k = ai[0];
                ai[0] = k - i1;
                if (ai[0] <= 1)
                {
                    break;
                }
                if (d >= ad[ai[0] - 1])
                {
                    ai[0] = bisectInterval(ad, d, ai[0], k);
                    return;
                }
                i1 *= 2;
            }
            while (true);
            ai[0] = 1;
            if (d < ad[0])
            {
                return;
            }
            else
            {
                ai[0] = bisectInterval(ad, d, ai[0], k);
                return;
            }
        }
        int j1 = 1;
        do
        {
            ai[0] = k;
            k = ai[0] + j1;
            if (k >= j)
            {
                break;
            }
            if (d < ad[k - 1])
            {
                ai[0] = bisectInterval(ad, d, ai[0], k);
                return;
            }
            j1 *= 2;
        }
        while (true);
        if (d >= ad[j - 1])
        {
            ai[0] = j;
            return;
        }
        else
        {
            int l = j;
            ai[0] = bisectInterval(ad, d, ai[0], l);
            return;
        }
    }

    private int bisectInterval(double ad[], double d, int i, int j)
    {
        do
        {
            int k = (i + j) / 2;
            if (k == i)
            {
                return i;
            }
            if (d < ad[k - 1])
            {
                j = k;
            }
            else
            {
                i = k;
            }
        }
        while (true);
    }

    private int bisectInterval2(double ad[], double d, int i, int j, int ai[])
    {
        do
        {
            int k = (i + j) / 2;
            if (k == i)
            {
                ai[0] = 0;
                return i;
            }
            if (d < ad[k - 1])
            {
                j = k;
            }
            else
            {
                i = k;
            }
        }
        while (true);
    }

    private double b2der(int i, double d, int j, double ad[], int k, double ad1[])
    {
        double ad2[] = new double[j];
        double ad3[] = new double[j];
        double ad4[] = new double[j];
        double d1 = 0.0D;
        if (j < 1)
        {
            Object aobj[] =
            {
                    "order", new Integer(j)
            };
            System.out.println("com.imsl.math : NotPositive");
            return d1;
        }
        if (i < 0)
        {
            Object aobj1[] =
            {
                    "ideriv", new Integer(i)
            };
            System.out.println("com.imsl.math : Negative");
            return d1;
        }
        if (k < j)
        {
            Object aobj2[] =
            {
                    "order", "nCoef"
            };
            System.out.println("com.imsl.math : XLessOrEqualY");
            return d1;
        }
        else
        {
            b3int(j, ad, k);
            double d2 = b3der(i, d, j, ad, k, ad1, ad2, ad3, ad4);
            return d2;
        }
    }

    private double b3der(int i, double d, int j, double ad[], int k, double ad1[], double ad2[], double ad3[],
            double ad4[])
    {
        int ai[] = new int[1];
        int ai1[] = new int[1];
        double d5 = 0.0D;
        if (i >= j)
        {
            double d1 = d5;
            return d1;
        }
        int i5 = k + j;
        b4der(ad, i5, d, ai, ai1);
        int l = ai[0];
        int l4 = ai1[0];
        if (l4 != 0)
        {
            double d2 = d5;
            return d2;
        }
        if (j <= 1)
        {
            d5 = ad1[l - 1];
            double d3 = d5;
            return d3;
        }
        int i4 = 1;
        if (l < j)
        {
            i4 = (j - l) + 1;
            for (int k1 = 1; k1 <= l; k1++)
            {
                ad3[k1 - 1] = d - ad[l - k1];
            }

            double d6 = ad3[l - 1];
            for (int l1 = 0; l1 < j - 1; l1++)
            {
                ad2[l1] = 0.0D;
                ad3[(l - 1) + l1] = d6;
            }

        }
        else
        {
            for (int i2 = 1; i2 <= j - 1; i2++)
            {
                ad3[i2 - 1] = d - ad[l - i2];
            }

        }
        int l3 = j;
        if (k < l)
        {
            l3 = i5 - l;
            for (int j2 = 1; j2 <= l3; j2++)
            {
                ad4[j2 - 1] = ad[(l + j2) - 1] - d;
            }

            double d7 = ad4[l3 - 1];
            for (int k2 = 0; k2 < j - l3; k2++)
            {
                ad2[l3 + k2] = 0.0D;
                ad4[(l3 - 1) + k2] = d7;
            }

        }
        else
        {
            for (int l2 = 1; l2 <= j - 1; l2++)
            {
                ad4[l2 - 1] = ad[(l + l2) - 1] - d;
            }

        }
        for (int i3 = 0; i3 < (l3 - i4) + 1; i3++)
        {
            ad2[(i4 - 1) + i3] = ad1[(((l - j) + i4) - 1) + i3];
        }

        for (int j3 = 1; j3 <= i; j3++)
        {
            int i1 = j - j3;
            for (int j4 = 1; j4 <= j - j3; j4++)
            {
                ad2[j4 - 1] = ((ad2[j4] - ad2[j4 - 1]) / (ad3[i1 - 1] + ad4[j4 - 1])) * (double) (j - j3);
                i1--;
            }

        }

        for (int k3 = i + 1; k3 <= j - 1; k3++)
        {
            int j1 = j - k3;
            for (int k4 = 1; k4 <= j - k3; k4++)
            {
                ad2[k4 - 1] = (ad2[k4] * ad3[j1 - 1] + ad2[k4 - 1] * ad4[k4 - 1]) / (ad3[j1 - 1] + ad4[k4 - 1]);
                j1--;
            }

        }

        d5 = ad2[0];
        double d4 = d5;
        return d4;
    }

    private void b4der(double ad[], int i, double d, int ai[], int ai1[])
    {
        int j1 = 1;
        int j = j1 + 1;
        if (j >= i)
        {
            if (d >= ad[i - 1])
            {
                ai[0] = i;
                if (d == ad[i - 1])
                {
                    for (ai[0]--; d == ad[ai[0] - 1]; ai[0]--)
                        ;
                    ai1[0] = 0;
                }
                else
                {
                    ai1[0] = 1;
                }
                return;
            }
            if (i <= 1)
            {
                ai1[0] = -1;
                ai[0] = 1;
                return;
            }
            j1 = i - 1;
            j = i;
        }
        if (d < ad[j - 1])
        {
            if (d >= ad[j1 - 1])
            {
                ai1[0] = 0;
                ai[0] = j1;
                return;
            }
            int l = 1;
            do
            {
                j = j1;
                j1 = j - l;
                if (j1 <= 1)
                {
                    break;
                }
                if (d >= ad[j1 - 1])
                {
                    ai[0] = bisectInterval2(ad, d, j1, j, ai1);
                    return;
                }
                l *= 2;
            }
            while (true);
            j1 = 1;
            if (d < ad[0])
            {
                ai1[0] = -1;
                ai[0] = 1;
                return;
            }
            else
            {
                ai[0] = bisectInterval2(ad, d, j1, j, ai1);
                return;
            }
        }
        int i1 = 1;
        do
        {
            j1 = j;
            j = j1 + i1;
            if (j >= i)
            {
                break;
            }
            if (d < ad[j - 1])
            {
                ai[0] = bisectInterval2(ad, d, j1, j, ai1);
                return;
            }
            i1 *= 2;
        }
        while (true);
        if (d >= ad[i - 1])
        {
            ai[0] = i;
            if (d == ad[i - 1])
            {
                do
                {
                    ai[0]--;
                }
                while (d == ad[ai[0] - 1]);
                ai1[0] = 0;
            }
            else
            {
                ai1[0] = 1;
            }
            return;
        }
        else
        {
            int k = i;
            ai[0] = bisectInterval2(ad, d, j1, k, ai1);
            return;
        }
    }

    void b3int(int i, double ad[], int j)
    {
        if (i < 1)
        {
            Object aobj[] =
            {
                    "order", new Integer(i)
            };
            System.out.println("com.imsl.math : NotPositive");
            return;
        }
        if (j < i)
        {
            Object aobj1[] =
            {
                    new Integer(j), new Integer(i)
            };
            System.out.println("com.imsl.math : BSpline.xDataLength");
            return;
        }
        int l = 1;
        for (int k = 2; k <= j + i; k++)
        {
            if (ad[k - 1] == ad[k - 2])
            {
                if (++l > i)
                {
                    Object aobj2[] =
                    {
                            "X", new Integer((k - 1 - l) + 1), new Integer(k - 1)
                    };
                    System.out.println("com.imsl.math : BSpline.KnotMultiplicity");
                    return;
                }
                continue;
            }
            if (ad[k - 1] < ad[k - 2])
            {
                Object aobj3[] =
                {
                        "X", new Integer(k - 2), new Double(ad[k - 2]), new Integer(k - 1), new Double(ad[k - 1])
                };
                System.out.println("com.imsl.math : BSpline.KnotsNotIncreasing");
                return;
            }
            l = 1;
        }

    }

    private void b21gd(int i, int j, double ad[], int k, double ad1[], int l, double ad2[], double ad3[])
    {
        int ai[] = new int[1];
        double ad4[] = new double[k * ((l - k) + 1)];
        double ad5[] = new double[(l - k) + 2];
        double ad6[] = new double[(k + 3) * k];
        int ai1[] = new int[j];
        double ad7[] = new double[j];
        double ad8[] = new double[j];
        if (j < 1)
        {
            Object aobj[] =
            {
                    "xData", new Integer(j), new Integer(1)
            };
            throw new IllegalArgumentException("com.imsl.math : TooFewPoints");
        }
        if (i < 0)
        {
            Object aobj1[] =
            {
                    "ideriv", new Integer(i)
            };
            System.out.println("com.imsl.math : Negative");
            return;
        }
        if (k < 1)
        {
            Object aobj2[] =
            {
                    "order", new Integer(k)
            };
            System.out.println("com.imsl.math : NotPositive");
            return;
        }
        for (int i1 = 2; i1 <= j; i1++)
        {
            if (ad[i1 - 2] >= ad[i1 - 1])
            {
                System.out.println("com.imsl.math : BSpline.XVecNotIncreasing");
                return;
            }
        }

        if (l < k)
        {
            Object aobj3[] =
            {
                    "order", "nCoef"
            };
            System.out.println("com.imsl.math : XLessOrEqualY");
            return;
        }
        int i2 = l;
        b2cpp(k, ad1, l, ad2, ai, ad5, ad4);
        int k2 = ai[0];
        l = i2;
        for (int j1 = 1; j1 <= j; j1++)
        {
            p3der(k, k2, ad5, ad[j1 - 1], ai);
            ai1[j1 - 1] = ai[0];
        }

        for (int k1 = 1; k1 <= j; k1++)
        {
            ad7[k1 - 1] = ad[k1 - 1] - ad5[ai1[k1 - 1] - 1];
            ad3[k1 - 1] = 0.0D;
        }

        double d = k - i;
        for (int j2 = k; j2 >= i + 1; j2--)
        {
            for (int l1 = 0; l1 < j; l1++)
            {
                ad8[l1] = ad4[(ai1[l1] - 1) * k + (j2 - 1)];
                ad3[l1] = (ad3[l1] / d) * ad7[l1] + ad8[l1];
            }

            d--;
        }

    }

    double bsppder(int i, double d, int j, int k, double ad[], double ad1[])
    {
        int ai[] = new int[1];
        double d5 = d;
        double d4 = 0.0D;
        if (k < 1)
        {
            Object aobj[] =
            {
                    "nintv", new Integer(k)
            };
            System.out.println("com.imsl.math : NotPositive");
            return d4;
        }
        if (i < 0)
        {
            Object aobj1[] =
            {
                    "ideriv", new Integer(i)
            };
            System.out.println("com.imsl.math : Negative");
            return d4;
        }
        if (j < 1)
        {
            Object aobj2[] =
            {
                    "order", new Integer(j)
            };
            System.out.println("com.imsl.math : NotPositive");
            return d4;
        }
        if (i >= j)
        {
            return d4;
        }
        p3der(j, k, ad, d5, ai);
        double d1 = j - i;
        double d2 = d5 - ad[ai[0] - 1];
        for (int l = j; l >= i + 1; l--)
        {
            d4 = (d4 / d1) * d2 + ad1[((ai[0] - 1) * j + l) - 1];
            d1--;
        }

        double d3 = d4;
        return d3;
    }

    private void b2cpp(int i, double ad[], int j, double ad1[], int ai[], double ad2[], double ad3[])
    {
        if (i < 1)
        {
            Object aobj[] =
            {
                    "order", new Integer(i)
            };
            System.out.println("com.imsl.math : NotPositive");
            return;
        }
        if (j < i)
        {
            Object aobj1[] =
            {
                    "order", "nCoef"
            };
            System.out.println("com.imsl.math : XLessOrEqualY");
            return;
        }
        else
        {
            b3int(i, ad, j);
            b3cpp(i, ad, j, ad1, ai, ad2, ad3);
            return;
        }
    }

    private void b3cpp(int i, double ad[], int j, double ad1[], int ai[], double ad2[], double ad3[])
    {
        double ad4[] = new double[i];
        double ad5[] = new double[i];
        double ad6[] = new double[i];
        double ad7[] = new double[i * i];
        int j2 = 0;
        ad2[0] = ad[i - 1];
        label0: for (int i2 = i; i2 <= j; i2++)
        {
            if (ad[i2] == ad[i2 - 1])
            {
                continue;
            }
            j2++;
            double d3 = ad[i2];
            ad2[j2] = d3;
            if (i > 1)
            {
                for (int k = 0; k < i; k++)
                {
                    ad7[k] = ad1[(i2 - i) + k];
                }

                for (int k1 = 1; k1 <= i - 1; k1++)
                {
                    for (int l = 1; l <= i - k1; l++)
                    {
                        double d = ad[(i2 + l) - 1] - ad[(((i2 + l) - i) + k1) - 1];
                        if (d > 0.0D)
                        {
                            ad7[(k1 * i + l) - 1] = ((ad7[(k1 - 1) * i + l] - ad7[((k1 - 1) * i + l) - 1]) / d)
                                    * (double) (i - k1);
                        }
                    }

                }

                ad4[0] = 1.0D;
                ad3[((j2 - 1) * i + i) - 1] = ad7[(i - 1) * i];
                int l1 = 1;
                do
                {
                    if (l1 > i - 1)
                    {
                        continue label0;
                    }
                    ad6[l1 - 1] = ad[(i2 + l1) - 1] - ad[i2 - 1];
                    ad5[l1 - 1] = ad[i2 - 1] - ad[i2 - l1];
                    double d1 = 0.0D;
                    for (int i1 = 1; i1 <= l1; i1++)
                    {
                        double d2 = ad4[i1 - 1] / (ad6[i1 - 1] + ad5[l1 - i1]);
                        ad4[i1 - 1] = d1 + ad6[i1 - 1] * d2;
                        d1 = ad5[l1 - i1] * d2;
                    }

                    ad4[l1] = d1;
                    double d4 = 0.0D;
                    for (int j1 = 0; j1 < l1 + 1; j1++)
                    {
                        d4 += ad4[j1] * ad7[(i - l1 - 1) * i + j1];
                    }

                    ad3[(j2 - 1) * i + (i - l1 - 1)] = d4;
                    l1++;
                }
                while (true);
            }
            ad3[(j2 - 1) * i] = ad1[i2 - 1];
        }

        ai[0] = j2;
    }

    private double b2itg(double d, double d1, int i, double ad[], int j, double ad1[])
    {
        double ad2[] = new double[i + 1];
        double ad3[] = new double[i + 1];
        double ad4[] = new double[i + 1];
        double ad5[] = new double[i + 1];
        double d2 = 0.0D;
        if (i < 1)
        {
            Object aobj[] =
            {
                    "order", new Integer(i)
            };
            System.out.println("com.imsl.math : NotPositive");
            return d2;
        }
        if (j < i)
        {
            Object aobj1[] =
            {
                    "order", "nCoef"
            };
            System.out.println("com.imsl.math : XLessOrEqualY");
            return d2;
        }
        else
        {
            b3int(i, ad, j);
            double d3 = b3itg(d, d1, i, ad, j, ad1, ad2, ad3, ad4, ad5);
            return d3;
        }
    }

    private double b3itg(double d, double d1, int i, double ad[], int j, double ad1[], double ad2[], double ad3[],
            double ad4[], double ad5[])
    {
        int ai[] = new int[1];
        int ai1[] = new int[1];
        int ai2[] = new int[1];
        double d2 = 0.0D;
        int k2;
        double d3;
        double d4;
        if (d < d1)
        {
            d3 = d;
            d4 = d1;
            k2 = 1;
        }
        else if (d > d1)
        {
            d3 = d1;
            d4 = d;
            k2 = -1;
        }
        else
        {
            System.out.println("com.imsl.math : BSpline.EqualLimits");
            return d2;
        }
        ai[0] = 0;
        if (d3 == ad[i - 1])
        {
            ai[0] = i;
        }
        if (d3 < ad[i - 1])
        {
            ai[0] = i;
            d3 = ad[i - 1];
            if (k2 == 1)
            {
                System.out.println("com.imsl.math : BSpline.LowerTooSmall");
            }
            else
            {
                System.out.println("com.imsl.math : BSpline.UpperTooSmall");
            }
        }
        ai1[0] = 0;
        if (d4 == ad[j])
        {
            ai1[0] = j;
        }
        if (d4 > ad[j])
        {
            ai1[0] = j;
            d4 = ad[j];
            if (k2 == 1)
            {
                System.out.println("com.imsl.math : BSpline.UpperTooBig");
            }
            else
            {
                System.out.println("com.imsl.math : BSpline.LowerTooBig");
            }
        }
        if (d3 <= ad[i - 1] && d4 <= ad[i - 1] || d3 >= ad[j] && d4 >= ad[j])
        {
            return d2;
        }
        if (d3 == d4)
        {
            return d2;
        }
        if (ai[0] == 0)
        {
            int k = j + i;
            b4der(ad, k, d3, ai, ai2);
        }
        int l2 = 0;
        ad2[l2] = 0.0D;
        for (int k1 = 1; k1 <= ai[0] - i; k1++)
        {
            ad2[l2] += ad1[k1 - 1] * (ad[(k1 + i) - 1] - ad[k1 - 1]);
        }

        for (int l1 = (ai[0] - i) + 1; l1 <= ai[0]; l1++)
        {
            l2++;
            ad2[l2] = ad2[l2 - 1] + ad1[l1 - 1] * (ad[(l1 + i) - 1] - ad[l1 - 1]);
        }

        for (int i1 = 0; i1 < i + 1; i1++)
        {
            ad3[i1] = (1.0D / (double) i) * ad2[i1];
        }

        int l = i + 1;
        double d6 = b4itg(d3, l, ad, j, ad3, ad4, ad5, ai[0]);
        if (ai1[0] == 0)
        {
            l = j + i;
            b4der(ad, l, d4, ai1, ai2);
        }
        if (ai1[0] - i > ai[0])
        {
            double d5 = ad2[l2];
            for (int i2 = ai[0] + 1; i2 <= ai1[0] - i; i2++)
            {
                d5 += ad1[i2 - 1] * (ad[(i2 + i) - 1] - ad[i2 - 1]);
            }

            l2 = 0;
            ad2[l2] = d5;
        }
        else if (ai[0] != ai1[0])
        {
            l2 = 0;
            ad2[l2] = ad2[ai1[0] - ai[0]];
        }
        if (ai[0] != ai1[0])
        {
            for (int j2 = (ai1[0] - i) + 1; j2 <= ai1[0]; j2++)
            {
                l2++;
                ad2[l2] = ad2[l2 - 1] + ad1[j2 - 1] * (ad[(j2 + i) - 1] - ad[j2 - 1]);
            }

        }
        for (int j1 = 0; j1 < i + 1; j1++)
        {
            ad3[j1] = (1.0D / (double) i) * ad2[j1];
        }

        l = i + 1;
        double d7 = b4itg(d4, l, ad, j, ad3, ad4, ad5, ai1[0]);
        d2 = (double) k2 * (d7 - d6);
        return d2;
    }

    private double b4itg(double d, int i, double ad[], int j, double ad1[], double ad2[], double ad3[], int k)
    {
        double d2 = 0.0D;
        for (int i1 = 1; i1 <= i - 1; i1++)
        {
            ad2[i1 - 1] = d - ad[k - i1];
        }

        for (int j1 = 1; j1 <= i - 1; j1++)
        {
            ad3[j1 - 1] = ad[(k + j1) - 1] - d;
        }

        for (int k1 = 1; k1 <= i - 1; k1++)
        {
            int l = i - k1;
            for (int l1 = 1; l1 <= i - k1; l1++)
            {
                ad1[l1 - 1] = (ad1[l1] * ad2[l - 1] + ad1[l1 - 1] * ad3[l1 - 1]) / (ad2[l - 1] + ad3[l1 - 1]);
                l--;
            }

        }

        d2 = ad1[0];
        double d1 = d2;
        return d1;
    }

    void b4int(double ad[], int i, double d, int j, double ad1[], double ad2[], double ad3[])
    {
        ad1[0] = 1.0D;
        for (int l = 1; l <= i - 1; l++)
        {
            ad3[l - 1] = ad[(j + l) - 1] - d;
            ad2[l - 1] = d - ad[j - l];
            double d1 = 0.0D;
            for (int k = 1; k <= l; k++)
            {
                double d2 = ad1[k - 1] / (ad3[k - 1] + ad2[l - k]);
                ad1[k - 1] = d1 + ad3[k - 1] * d2;
                d1 = ad2[l - k] * d2;
            }

            ad1[l] = d1;
        }

    }
}
