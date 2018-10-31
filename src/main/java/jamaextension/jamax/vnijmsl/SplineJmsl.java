/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

import java.io.Serializable;

import jamaextension.jamax.Matrix;

public abstract class SplineJmsl implements Serializable, Cloneable
{

    static final long serialVersionUID = 0x45629eb67f3c18f5L;
    protected double coef[][];
    protected double breakPoint[];
    protected static final double EPSILON_LARGE = 2.2204460492503E-016D;

    public SplineJmsl()
    {
    }

    protected void copyAndSortData(double ad[], double ad1[])
    {
        copyAndSortData(ad, ad1, null);
    }

    protected void copyAndSortData(double ad[], double ad1[], double ad2[])
    {
        boolean flag = true;
        int j = ad.length;
        if (j != ad1.length)
        {
            Object aobj[] =
            {
                    "xData", new Integer(ad.length), "yData", new Integer(ad1.length)
            };
            throw new IllegalArgumentException("UnequalLengths");
        }
        coef = new double[j][4];
        breakPoint = ad.clone();
        for (int k = 0; k < j; k++)
        {
            coef[k][0] = ad1[k];
        }

        for (int l = 1; l < j; l++)
        {
            if (breakPoint[l - 1] < breakPoint[l])
            {
                continue;
            }
            if (breakPoint[l - 1] == breakPoint[l])
            {
                Object aobj1[] =
                {
                        new Integer(l - 1), new Integer(l), new Double(breakPoint[l])
                };
                // throw new
                // IllegalArgumentException("com.imsl.math, SplineJmsl.DuplicateX"
                // );
                System.out.println("com.imsl.math, Spline.DuplicateX");
                continue;
            }
            flag = false;
            break;
        }

        if (flag)
        {
            return;
        }
        int j1;
        for (int i1 = j; i1 != 0; i1 = j1 + 1)
        {
            j1 = -1;
            for (int i = 0; i < i1 - 1; i++)
            {
                if (breakPoint[i] <= breakPoint[i + 1])
                {
                    continue;
                }
                double d = breakPoint[i + 1];
                breakPoint[i + 1] = breakPoint[i];
                breakPoint[i] = d;
                d = coef[i + 1][0];
                coef[i + 1][0] = coef[i][0];
                coef[i][0] = d;
                if (ad2 != null)
                {
                    double d1 = ad2[i + 1];
                    ad2[i + 1] = ad2[i];
                    ad2[i] = d1;
                }
                j1 = i;
            }

        }

        for (int k1 = 1; k1 < j; k1++)
        {
            if (breakPoint[k1 - 1] == breakPoint[k1])
            {
                Object aobj2[] =
                {
                        new Integer(k1 - 1), new Integer(k1), new Double(breakPoint[k1])
                };
                throw new IllegalArgumentException("com.imsl.math, Spline.DuplicateX");
            }
        }

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
        double d1 = 0.0D;
        int j = coef[0].length;
        int k = p3der(d);
        double d2 = j - i;
        if (d2 <= 0.0D)
        {
            return 0.0D;
        }
        double d3 = d - breakPoint[k];
        for (int l = j - 1; l >= i; l--)
        {
            d1 = (d1 / d2) * d3 + coef[k][l];
            d2--;
        }

        return d1;
    }

    public Matrix getCoef()
    {
        return new Matrix(this.coef.clone());
    }

    public Matrix value(Matrix ad)
    {
        return derivative(ad, 0);
    }

    public Matrix derivative(Matrix A, int i)
    {
        if (!A.isVector())
        {
            throw new IllegalArgumentException("derivative : Parameter \"A\" must be a vector and not a matrix.");
        }
        double ad[] = A.getRowPackedCopy();
        double ad1[] = new double[ad.length];
        int j = coef[0].length;
        int k = 0;
        for (int l = 0; l < ad.length; l++)
        {
            k = p3der(ad[l], k);
            double d = j - i;
            if (d <= 0.0D)
            {
                ad1[l] = 0.0D;
                continue;
            }
            double d1 = ad[l] - breakPoint[k];
            for (int i1 = j - 1; i1 >= i; i1--)
            {
                ad1[l] = (ad1[l] / d) * d1 + coef[k][i1];
                d--;
            }

        }

        return new Matrix(ad1);
    }

    public Matrix getBreakpoints()
    {
        return new Matrix(breakPoint.clone());
    }

    public double integral(double d, double d1)
    {
        int l = 0;
        int l1 = coef[0].length;
        double d4;
        double d5;
        if (d < d1)
        {
            d4 = d;
            d5 = d1;
            l = 1;
        }
        else if (d > d1)
        {
            d4 = d1;
            d5 = d;
            l = -1;
        }
        else
        {
            return 0.0D;
        }
        int j = p3der(d4);
        int k = p3der(d5);
        double d2 = (double) l1 + 1.0D;
        double d3 = d4 - breakPoint[j];
        double d6 = 0.0D;
        for (int i1 = l1 - 1; i1 >= 0; i1--)
        {
            d6 = (d6 / d2) * d3 + coef[j][i1];
            d2--;
        }

        double d7 = -d6 * d3;
        for (int i = j + 1; i <= k; i++)
        {
            d3 = breakPoint[j + 1] - breakPoint[j];
            d6 = 0.0D;
            d2 = l1 + 1;
            for (int j1 = l1 - 1; j1 >= 0; j1--)
            {
                d6 = (d6 / d2) * d3 + coef[j][j1];
                d2--;
            }

            d7 += d6 * d3;
            j++;
        }

        d3 = d5 - breakPoint[k];
        d6 = 0.0D;
        d2 = l1 + 1;
        for (int k1 = l1 - 1; k1 >= 0; k1--)
        {
            d6 = (d6 / d2) * d3 + coef[k][k1];
            d2--;
        }

        d7 += d6 * d3;
        return (double) l * d7;
    }

    private int p3der(double d)
    {
        return p3der(d, 0);
    }

    private int p3der(double d, int i)
    {
        int l = coef.length - 1;
        int i1 = coef[0].length;
        if (d < breakPoint[i])
        {
            i = 0;
        }
        int j1 = i + 1;
        if (j1 > l)
        {
            if (d >= breakPoint[l - 1])
            {
                return l - 1;
            }
            if (l <= 1)
            {
                return 0;
            }
            i = l - 2;
            j1 = l - 1;
        }
        if (d < breakPoint[j1])
        {
            if (d >= breakPoint[i])
            {
                return i;
            }
            int j = 1;
            do
            {
                j1 = i;
                i = j1 - j;
                if (i <= 0)
                {
                    break;
                }
                if (d >= breakPoint[i])
                {
                    return bisectInterval(d, i, j1);
                }
                j *= 2;
            }
            while (true);
            i = 0;
            if (d < breakPoint[0])
            {
                return 0;
            }
            else
            {
                return bisectInterval(d, i, j1);
            }
        }
        int k = 1;
        do
        {
            i = j1;
            j1 = i + k;
            if (j1 >= l - 1)
            {
                break;
            }
            if (d < breakPoint[j1])
            {
                return bisectInterval(d, i, j1);
            }
            k *= 2;
        }
        while (true);
        if (d >= breakPoint[l - 1])
        {
            return l - 1;
        }
        else
        {
            return bisectInterval(d, i, l - 1);
        }
    }

    private int bisectInterval(double d, int i, int j)
    {
        do
        {
            int k = (i + j) / 2;
            if (k == i)
            {
                return i;
            }
            if (d < breakPoint[k])
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
}
