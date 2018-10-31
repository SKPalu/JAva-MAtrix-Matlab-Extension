/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Feynman Perceptrons
 */
// public class Cdf {

// }

// FrontEnd Plus GUI for JAD
// DeCompiled : Cdf.class
public final class Cdf
{

    // private static final int check = Messages.check(1);
    private static final double EPSILON_SMALL = 1.1102230246251999E-016D;
    private static final double EPSILON_LARGE = 2.2204460492503E-016D;
    private static final double MSQRT2 = -Math.sqrt(2D);

    private Cdf()
    {
    }

    private static double logCombination(int i, int j)
    {
        double d = Sfun.logGamma(i + 1);
        double d1 = Sfun.logGamma(j + 1);
        double d2 = Sfun.logGamma((i - j) + 1);
        return d - d1 - d2;
    }

    public static double binomial(int i, int j, double d)
    {
        if (j <= 0 || d > 1.0D || d < 0.0D || Double.isNaN(d))
        {
            return (0.0D / 0.0D);
        }
        if (i < 0)
        {
            return 0.0D;
        }
        if (i > j)
        {
            return 1.0D;
        }
        if (d == 0.0D)
        {
            return 1.0D;
        }
        if (d == 1.0D)
        {
            return 0.0D;
        }
        double d1 = Math.log(d);
        double d2 = Math.log(1.0D - d);
        double d3 = Sfun.logGamma(j + 1);
        double d4 = 0.0D;
        if ((double) i < (double) j * d)
        {
            for (int k = 0; k <= i; k++)
            {
                double d5 = Sfun.logGamma(k + 1);
                double d7 = Sfun.logGamma((j - k) + 1);
                double d9 = d3 - d5 - d7;
                d4 += Math.exp(d9 + (double) k * d1 + (double) (j - k) * d2);
            }

        }
        else
        {
            for (int l = i; l >= 0; l--)
            {
                double d6 = Sfun.logGamma(l + 1);
                double d8 = Sfun.logGamma((j - l) + 1);
                double d10 = d3 - d6 - d8;
                d4 += Math.exp(d10 + (double) l * d1 + (double) (j - l) * d2);
            }

        }
        return d4;
    }

    public static double binomialProb(int i, int j, double d)
    {
        if (j <= 0 || d > 1.0D || d < 0.0D || i < 0 || i > j || Double.isNaN(d))
        {
            return (0.0D / 0.0D);
        }
        if (d == 1.0D || d == 0.0D)
        {
            return 0.0D;
        }
        else
        {
            return Math.exp(logCombination(j, i) + (double) i * Math.log(d) + (double) (j - i) * Math.log(1.0D - d));
        }
    }

    public static double poisson(int i, double d)
    {
        double d12 = 1E-300D;
        double d13 = Math.log(d12);
        if (Double.isNaN(d) || d < 0.0D)
        {
            return (0.0D / 0.0D);
        }
        if (i < 0)
        {
            return 0.0D;
        }
        int i1 = i + 1;
        if (d <= 2.2204460492503E-016D)
        {
            return 1.0D;
        }
        double d8 = d;
        double d10 = 1.0D;
        int l = 1;
        double d3 = -d;
        int j = (int) (d3 / d13);
        d3 -= (double) j * d13;
        d3 = Math.exp(d3);
        double d9 = i;
        double d11 = d;
        double d1 = d9 * Math.log(d11);
        double d2 = i1;
        d2 = Sfun.logGamma(d2);
        double d6 = (-d11 + d1) - d2;
        int j1 = (int) (d6 / d13);
        d6 -= (double) j1 * d13;
        d6 = Math.exp(d6);
        d1 = 1.0D;
        d2 = 1.0D;
        if (j == 0)
        {
            d1 = 1.0D - d3;
        }
        if (j1 == 0)
        {
            d2 = 1.0D - d6;
        }
        double d7 = 0.0D;
        do
        {
            int k = j - j1;
            if (k > 0 || k == 0 && d3 <= d6)
            {
                if (j == 0)
                {
                    d7 += d3;
                }
                if (l == i1)
                {
                    break;
                }
                d3 *= d8 / d10;
                if (d3 >= d2)
                {
                    double d4 = d3 * d12;
                    if (d4 != 0.0D)
                    {
                        d3 = d4;
                        j--;
                    }
                }
                l++;
                d10++;
                continue;
            }
            if (j1 == 0)
            {
                d7 += d6;
            }
            if (l == i1)
            {
                break;
            }
            d6 *= d9 / d11;
            if (d6 >= d1)
            {
                double d5 = d6 * d12;
                if (d5 != 0.0D)
                {
                    d6 = d5;
                    j1--;
                }
            }
            i1--;
            d9--;
        }
        while (true);
        if (d7 > 1.0D)
        {
            d7 = 1.0D;
        }
        return d7;
    }

    public static double poissonProb(int i, double d)
    {
        return Math.exp((-d + (double) i * Math.log(d)) - Sfun.logGamma(i + 1));
    }

    public static double beta(double d, double d1, double d2)
    {
        double d10 = d1;
        double d11 = d2;
        boolean flag = false;
        double d12 = 0.94999999999999996D * Math.log(2.2250738585072014E-308D);
        double d3 = (0.0D / 0.0D);
        if (d10 <= 0.0D || d11 <= 0.0D)
        {
            return d3;
        }
        if (d <= 0.0D)
        {
            return 0.0D;
        }
        if (d >= 1.0D)
        {
            return 1.0D;
        }
        double d9 = d;
        if ((d11 > d10 || d > 0.80000000000000004D) && d > 0.20000000000000001D)
        {
            d9 = 1.0D - d9;
            double d13 = d10;
            d10 = d11;
            d11 = d13;
            flag = true;
        }
        if (((d10 + d11) * d9) / (d10 + 1.0D) < 1.1102230246251999E-016D)
        {
            d3 = 0.0D;
            double d7 = d10 * Math.log(Math.max(d9, 4.9406564584124654E-324D)) - Math.log(d10) - Sfun.logBeta(d10, d11);
            if (d7 > d12 && d9 != 0.0D)
            {
                d3 = Math.exp(d7);
            }
            if (d9 != d || flag)
            {
                d3 = 1.0D - d3;
            }
            return d3;
        }
        double d14 = d11 - (double) (int) d11;
        if (d14 == 0.0D)
        {
            d14 = 1.0D;
        }
        double d8 = d10 * Math.log(d9) - Sfun.logBeta(d14, d10) - Math.log(d10);
        d3 = 0.0D;
        if (d8 >= d12)
        {
            d3 = Math.exp(d8);
            double d5 = d3 * d10;
            if (d14 != 1.0D)
            {
                int j = (int) Math.max(Math.log(1.1102230246251999E-016D) / Math.log(d9), 4D);
                for (int l = 1; l <= j; l++)
                {
                    d5 = (d5 * ((double) l - d14) * d9) / (double) l;
                    d3 += d5 / (d10 + (double) l);
                }

            }
        }
        if (d11 <= 1.0D)
        {
            if (d9 != d || flag)
            {
                d3 = 1.0D - d3;
            }
            return Math.max(Math.min(d3, 1.0D), 0.0D);
        }
        d8 = (d10 * Math.log(d9) + d11 * Math.log(1.0D - d9)) - Sfun.logBeta(d10, d11) - Math.log(d11);
        int i = (int) Math.max(d8 / d12, 0.0D);
        double d6 = Math.exp(d8 - (double) i * d12);
        double d4 = 1.0D / (1.0D - d9);
        double d15 = (d11 * d4) / ((d10 + d11) - 1.0D);
        double d16 = 0.0D;
        int k = (int) d11;
        if (d11 == (double) k)
        {
            k--;
        }
        for (int i1 = 0; i1 < k && (d15 > 1.0D || d6 / 1.1102230246251999E-016D > d16); i1++)
        {
            d6 = ((d11 - (double) i1) * d4 * d6) / ((d10 + d11) - (double) (i1 + 1));
            if (d6 > 1.0D)
            {
                i--;
                d6 *= 4.9406564584124654E-324D;
            }
            if (i == 0)
            {
                d16 += d6;
            }
        }

        d3 += d16;
        if (d9 != d || flag)
        {
            d3 = 1.0D - d3;
        }
        return Math.max(Math.min(d3, 1.0D), 0.0D);
    }

    public static double inverseBeta(double d, double d1, double d2)
    {
        double d14 = (0.0D / 0.0D);
        if (d1 <= 0.0D || d2 <= 0.0D || d <= 0.0D || d > 1.0D)
        {
            return (0.0D / 0.0D);
        }
        if (d == 1.0D)
        {
            return 1.0D;
        }
        double d8 = d;
        double d3 = beta(d8, d1, d2) - d;
        double d9;
        if (d3 == 0.0D)
        {
            d9 = d8;
        }
        else
        {
            d9 = d + 0.050000000000000003D;
            double d11 = 0.050000000000000003D;
            double d4;
            if (d9 <= 0.0D)
            {
                d4 = -d;
            }
            else if (d9 >= 1.0D)
            {
                d4 = 1.0D - d;
            }
            else
            {
                d4 = beta(d9, d1, d2) - d;
            }
            double d7 = Math.max(0.01D, (d4 - d3) / d11);
            double d15 = -d3 / d7;
            int i = 1;
            do
            {
                if (i >= 102)
                {
                    break;
                }
                d15 = 2D * d15;
                if (i <= 100)
                {
                    d9 = d8 + d15;
                    if (d9 <= 0.0D)
                    {
                        d4 = -d;
                    }
                    else if (d9 >= 1.0D)
                    {
                        d4 = 1.0D - d;
                    }
                    else
                    {
                        d4 = beta(d9, d1, d2) - d;
                    }
                    if (d3 * d4 < 0.0D)
                    {
                        break;
                    }
                    d8 = d9;
                }
                else
                {
                    d9 = 1.0D;
                    break;
                }
                i++;
            }
            while (true);
            boolean flag = false;
            for (int j = 1; j <= 100; j++)
            {
                double d13 = (d8 + d9) * 0.5D;
                double d6 = d4 - d3;
                double d12 = d9 - d8;
                if (d13 != 0.0D ? Math.abs(d12) < Math.abs(d13 * 2.2204460492503E-016D)
                        : Math.abs(d12) < 2.2204460492503E-016D)
                {
                    break;
                }
                double d10;
                if (flag)
                {
                    d10 = d13;
                }
                else
                {
                    d10 = d9 - (d4 * d12) / d6;
                }
                flag = false;
                double d5;
                if (d10 <= 0.0D)
                {
                    d5 = -d;
                }
                else if (d10 >= 1.0D)
                {
                    d5 = 1.0D - d;
                }
                else
                {
                    d5 = beta(d10, d1, d2) - d;
                }
                if (d5 * d4 <= 0.0D)
                {
                    d8 = d9;
                    d3 = d4;
                    d9 = d10;
                    d4 = d5;
                    continue;
                }
                d9 = d10;
                d4 = d5;
                d3 *= 0.5D;
                if (Math.abs(d4) > Math.abs(d3))
                {
                    d3 = 2D * d3;
                    flag = true;
                }
            }

        }
        d14 = 0.5D * (d8 + d9);
        return d14;
    }

    public static double F(double d, double d1, double d2)
    {
        if (Double.isNaN(d) || Double.isNaN(d1) || Double.isNaN(d2) || d1 <= 0.0D || d2 <= 0.0D)
        {
            return (0.0D / 0.0D);
        }
        if (d <= 0.0D)
        {
            return 0.0D;
        }
        else
        {
            return 1.0D - Sfun.betaIncomplete(d2 / (d2 + d1 * d), 0.5D * d2, 0.5D * d1);
        }
    }

    public static double inverseF(double d, double d1, double d2)
    {
        if (Double.isNaN(d) || Double.isNaN(d1) || Double.isNaN(d2))
        {
            return (0.0D / 0.0D);
        }
        if (d == 1.0D)
        {
            return 0.0D;
        }
        if (d == 0.0D)
        {
            return (1.0D / 0.0D);
        }
        double d4 = 1.0D - d;
        double d3;
        if (d4 < 0.0D || d4 > 1.0D || d1 <= 0.0D || d2 <= 0.0D)
        {
            d3 = (0.0D / 0.0D);
        }
        else
        {
            double d5 = 0.99999999999999978D;
            double d6 = 0.5D * d1;
            double d7 = 0.5D * d2;
            if (d4 <= 0.5D)
            {
                d3 = inverseBeta(d, d6, d7);
                if (d3 >= d5)
                {
                    d3 = (1.0D / 0.0D);
                }
                else
                {
                    d3 = (d2 * d3) / (d1 * (1.0D - d3));
                }
            }
            else
            {
                d3 = inverseBeta(d4, d7, d6);
                if (d3 == 0.0D)
                {
                    d3 = (1.0D / 0.0D);
                }
                else
                {
                    d3 = ((1.0D / d3 - 1.0D) * d2) / d1;
                }
            }
        }
        return d3;
    }

    public static double hypergeometricProb(int i, int j, int k, int l)
    {
        if (j < 0 || j > l || k < 0 || k > l || l < 0)
        {
            return (0.0D / 0.0D);
        }
        if (i < 0)
        {
            return 0.0D;
        }
        int i1 = (j - l) + k;
        if (i1 < 0)
        {
            i1 = 0;
        }
        if (i < i1)
        {
            return (0.0D / 0.0D);
        }
        else
        {
            double d = (logCombination(k, i) + logCombination(l - k, j - i)) - logCombination(l, j);
            return Math.exp(d);
        }
    }

    public static double hypergeometric(int i, int j, int k, int l)
    {
        double d8 = 0.0D;
        if (j <= 0 || k <= 0)
        {
            return (0.0D / 0.0D);
        }
        if (l < j || l < k)
        {
            return (0.0D / 0.0D);
        }
        if (i < 0)
        {
            return 0.0D;
        }
        if (i > j)
        {
            return 1.0D;
        }
        if (i > k)
        {
            return 1.0D;
        }
        if (j - i > l - k)
        {
            return 0.0D;
        }
        double d9 = 1.0D;
        double d10 = l;
        int k1 = Math.min(j, k);
        double d11 = k1;
        int l1 = Math.max(j, k);
        double d12 = l1;
        double d13 = i * (l + 2);
        double d14 = (k + 1) * (j + 1);
        boolean flag = d13 > d14;
        int i1;
        int j1;
        double d;
        double d1;
        double d2;
        double d4;
        double d5;
        if (!flag)
        {
            d2 = d12 - d11;
            d5 = d10 - d12 - d11;
            int i2 = j + (k - l);
            if (i2 < 0)
            {
                i2 = 0;
            }
            double d3 = i2;
            d1 = d11 - d3;
            d4 = d3 + 1.0D;
            j1 = i - i2;
            d = (d10 - d12) + d3;
            i1 = k1 - i2;
        }
        else
        {
            d2 = d10 - d12 - d11;
            d5 = d12 - d11;
            d1 = d11;
            d4 = 1.0D;
            j1 = k1 - i;
            i1 = l - l1;
            d = d10 - d11;
            if (k1 < i1)
            {
                i1 = k1;
                d = d12;
            }
        }
        int j2 = 0;
        double d15 = 4.9406564584124654E-323D;
        if (i1 != 0)
        {
            for (int k2 = 0; k2 < i1; k2++)
            {
                double d6 = d / d10;
                if (d6 < d15 / d9)
                {
                    d9 /= d15;
                    j2++;
                }
                d9 *= d6;
                d--;
                d10--;
            }

        }
        d8 = 0.0D;
        if (j1 != 0)
        {
            for (int l2 = 0; l2 < j1; l2++)
            {
                if (j2 == 0)
                {
                    d8 += d9;
                }
                double d7 = (d1 * (d1 + d2)) / (d4 * (d4 + d5));
                d9 *= d7;
                if (d9 >= 1.0D)
                {
                    d9 *= d15;
                    j2--;
                }
                d1--;
                d4++;
            }

        }
        if (j2 != 0)
        {
            d9 = 0.0D;
        }
        if (!flag)
        {
            d8 += d9;
        }
        else
        {
            d8 = 1.0D - d8;
        }
        return d8;
    }

    public static double gamma(double d, double d1)
    {
        double ad[] = new double[6];
        double ad1[] = new double[4];
        if (d1 <= 0.0D)
        {
            return (0.0D / 0.0D);
        }
        if (d <= 0.0D)
        {
            return 0.0D;
        }
        if (d1 <= 0.0D)
        {
            return (0.0D / 0.0D);
        }
        if (Math.log(d) >= Math.log(1.7976900000000001E+308D) / 3D)
        {
            return d1 < 0.5D * d ? 1.0D : (0.0D / 0.0D);
        }
        double d9 = Sfun.logGamma(d1);
        double d4 = d1 * Math.log(d);
        double d14 = d + d9;
        double d2 = Math.exp(d4 - d14);
        double d3 = 1E+030D;
        double d7 = 1E-008D;
        double d8;
        if (d > 1.0D && d >= d1)
        {
            double d13 = 1.0D - d1;
            double d16 = d + d13 + 1.0D;
            double d5 = 0.0D;
            ad[0] = 1.0D;
            ad[1] = d;
            ad[2] = d + 1.0D;
            ad[3] = d16 * d;
            d8 = ad[2] / ad[3];
            ad1[0] = ad[2];
            ad1[1] = ad[3];
            ad1[2] = ad[4];
            ad1[3] = ad[5];
            do
            {
                d5++;
                d13++;
                d16 += 2D;
                double d15 = d13 * d5;
                ad[4] = ad1[0] * d16 - ad[0] * d15;
                ad1[2] = ad[4];
                ad[5] = ad1[1] * d16 - ad[1] * d15;
                ad1[3] = ad[5];
                if (ad[5] == 0.0D)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        ad[i] = ad1[i];
                    }

                    ad1[0] = ad[2];
                    ad1[1] = ad[3];
                    if (Math.abs(ad[4]) >= d3 || Math.abs(ad[5]) >= d3)
                    {
                        for (int j = 0; j < 4; j++)
                        {
                            ad[j] /= d3;
                        }

                        ad1[0] = ad[2];
                        ad1[1] = ad[3];
                    }
                    continue;
                }
                double d10 = ad[4] / ad[5];
                double d12 = Math.abs(d8 - d10);
                if (d12 <= d10 * d7 * 10D && d12 <= d7)
                {
                    d8 = 1.0D - d8 * d2;
                    break;
                }
                d8 = d10;
                for (int k = 0; k < 4; k++)
                {
                    ad[k] = ad1[k];
                }

                ad1[0] = ad[2];
                ad1[1] = ad[3];
                if (Math.abs(ad[4]) >= d3 || Math.abs(ad[5]) >= d3)
                {
                    for (int l = 0; l < 4; l++)
                    {
                        ad[l] /= d3;
                    }

                    ad1[0] = ad[2];
                    ad1[1] = ad[3];
                }
            }
            while (true);
        }
        else
        {
            double d11 = d1;
            double d6 = 1.0D;
            d8 = 1.0D;
            do
            {
                d11++;
                d6 *= d / d11;
                d8 += d6;
                if (d6 <= d7)
                {
                    d8 = (d8 * d2) / d1;
                }
            }
            while (d6 > d7);
        }
        return d8;
    }

    public static double inverseGamma(double d, double d1)
    {
        double d19 = 1.7976931348623157E+308D / Math.log(1.7976931348623157E+308D);
        if (d < 0.0D || d > 1.0D || d1 <= 0.0D || d1 > d19)
        {
            return (0.0D / 0.0D);
        }
        if (d == 0.0D)
        {
            d = 1.1102230246251999E-016D;
        }
        if (d == 1.0D)
        {
            return (1.0D / 0.0D);
        }
        double d9 = inverseNormal(d);
        double d2 = 1.0D / (9D * d1);
        double d3 = d9 / Math.sqrt(9D * d1);
        double d4 = Math.pow(1.7976931348623157E+308D, 0.33333333333333331D);
        double d20 = Math.abs((1.0D - d2) + d3);
        if (d20 > d4)
        {
            return (0.0D / 0.0D);
        }
        double d18;
        if (9D * d1 <= Math.pow(d9, 2D))
        {
            d18 = d1 * Math.pow((1.0D - 1.0D / (9D * d1)) + d9 / Math.sqrt(9D * d1), 3D);
        }
        else
        {
            d18 = 0.0D;
        }
        double d13 = d18;
        if (d13 <= 0.0D)
        {
            d9 = -d;
        }
        else
        {
            d9 = gamma(d13, d1) - d;
        }
        double d14;
        if (d9 == 0.0D)
        {
            d14 = d13;
        }
        else
        {
            double d16;
            if (Math.abs(d18) >= 1.0D)
            {
                d14 = d18 * 1.05D;
                d16 = d14 - d13;
            }
            else
            {
                d14 = d18 + 0.050000000000000003D;
                d16 = 0.050000000000000003D;
            }
            double d10;
            if (d14 <= 0.0D)
            {
                d10 = -d;
            }
            else
            {
                d10 = gamma(d14, d1) - d;
            }
            double d12 = Math.max(0.01D, (d10 - d9) / d16);
            double d8 = -d9 / d12;
            int i = 0;
            do
            {
                if (i >= 102)
                {
                    break;
                }
                d8 = 2D * d8;
                d14 = d13 + d8;
                double d7;
                if (d14 > 0.0D)
                {
                    double d5 = Math.log(d14);
                    double d6 = Math.log(1.7976931348623157E+308D) / 3D;
                    d7 = d5 - d6;
                }
                else
                {
                    d7 = -1D;
                }
                if (i > 100 || d7 >= 0.0D && d1 >= 0.5D * d14)
                {
                    return (0.0D / 0.0D);
                }
                if (d14 <= 0.0D)
                {
                    d10 = -d;
                }
                else
                {
                    d10 = gamma(d14, d1) - d;
                }
                if (d9 * d10 < 0.0D)
                {
                    break;
                }
                d13 = d14;
                i++;
            }
            while (true);
            boolean flag = false;
            for (int j = 1; j < 102; j++)
            {
                double d21 = (d13 + d14) * 0.5D;
                double d22 = d10 - d9;
                double d17 = d14 - d13;
                if (d21 != 0.0D ? Math.abs(d17) < Math.abs(d21 * 2.2204460492503001E-015D)
                        : Math.abs(d17) < 2.2204460492503001E-015D)
                {
                    break;
                }
                double d15;
                if (flag)
                {
                    d15 = d21;
                }
                else
                {
                    d15 = d14 - (d10 * d17) / d22;
                }
                flag = false;
                double d11;
                if (d15 <= 0.0D)
                {
                    d11 = -d;
                }
                else
                {
                    d11 = gamma(d15, d1) - d;
                }
                if (d11 * d10 <= 0.0D)
                {
                    d13 = d14;
                    d9 = d10;
                    d14 = d15;
                    d10 = d11;
                    continue;
                }
                d14 = d15;
                d10 = d11;
                d9 *= 0.5D;
                if (Math.abs(d10) > Math.abs(d9))
                {
                    d9 = 2D * d9;
                    flag = true;
                }
            }

        }
        return (d13 + d14) * 0.5D;
    }

    public static double normal(double d)
    {
        return 0.5D * Sfun.erfc(-d * 0.70710678118654757D);
    }

    public static double inverseNormal(double d)
    {
        if (d < 0.0D || d > 1.0D)
        {
            return (0.0D / 0.0D);
        }
        if (d == 0.0D)
        {
            return (-1.0D / 0.0D);
        }
        if (d == 1.0D)
        {
            return (1.0D / 0.0D);
        }
        else
        {
            return MSQRT2 * Sfun.erfcInverse(2D * d);
        }
    }

    public static double chi(double d, double d1)
    {
        if (Double.isNaN(d) || Double.isNaN(d1))
        {
            return (0.0D / 0.0D);
        }
        if (d1 < 0.5D)
        {
            return (0.0D / 0.0D);
        }
        double d5 = 2.2204460492503001E-015D;
        double d7;
        if (d <= 9.9999999999999998E-013D)
        {
            d7 = 0.0D;
        }
        else if (d1 > 65D)
        {
            if (d < 2D)
            {
                d7 = 0.0D;
            }
            else
            {
                double d16 = 2D / (9D * d1);
                double d13 = (Math.pow(d / d1, 0.33333333333333331D) - (1.0D - d16)) / Math.sqrt(d16);
                d7 = normal(d13);
            }
        }
        else if (d > 200D)
        {
            d7 = 1.0D;
        }
        else
        {
            double d2 = 0.5D * d1;
            double d14 = 0.5D * d;
            double d6 = Sfun.gamma(d2);
            double d8 = Math.max(0.5D * d2, 13D);
            if (d14 >= d8)
            {
                double d15 = 1.0D / d14;
                double d3 = d2 - 1.0D;
                double d11 = d3 * d15;
                double d9 = 1.0D + d11;
                int i = 2;
                do
                {
                    if (i > 50)
                    {
                        break;
                    }
                    d3--;
                    d11 *= d3 * d15;
                    if (d11 <= d5 * d9)
                    {
                        break;
                    }
                    d9 += d11;
                    i++;
                }
                while (true);
                d9 = d15 * d9 * Math.exp(d2 * Math.log(d14) - d14);
                d7 = 1.0D - d9 / d6;
            }
            else if (d1 > 25D && d < 2D)
            {
                d7 = 0.0D;
            }
            else
            {
                double d10 = 1.0D / (d6 * d2);
                double d12 = d10;
                int j = 1;
                do
                {
                    if (j > 50)
                    {
                        break;
                    }
                    double d4 = j;
                    d12 *= d14 / (d2 + d4);
                    if (d12 <= d5 * d10)
                    {
                        break;
                    }
                    d10 += d12;
                    j++;
                }
                while (true);
                d7 = d10 * Math.exp(d2 * Math.log(d14) - d14);
            }
        }
        return d7;
    }

    public static double inverseChi(double d, double d1)
    {
        if (Double.isNaN(d) || Double.isNaN(d1))
        {
            return (0.0D / 0.0D);
        }
        if (d <= 0.0D || d > 1.0D || d1 < 0.5D)
        {
            return (0.0D / 0.0D);
        }
        if (d == 1.0D)
        {
            d -= 1.1102230246251999E-016D;
        }
        double d14 = normal(d);
        double d8 = 2D / (9D * d1);
        double d9 = d1 * Math.pow((1.0D - d8) + d14 * Math.sqrt(d8), 3D);
        if (d9 < 0.0D)
        {
            d9 = 0.0D;
        }
        double d3 = chi(d9, d1) - d;
        double d10;
        if (d3 == 0.0D)
        {
            d10 = d9;
        }
        else
        {
            double d12;
            if (Math.abs(d14) >= 1.0D)
            {
                d10 = d14 * 1.05D;
                d12 = d10 - d9;
            }
            else
            {
                d10 = d14 + 0.050000000000000003D;
                d12 = 0.050000000000000003D;
            }
            if (d10 < 0.0D)
            {
                d10 = 0.0D;
            }
            double d4 = chi(d10, d1) - d;
            double d7 = Math.max(0.01D, (d4 - d3) / d12);
            double d2 = -d3 / d7;
            int i = 0;
            do
            {
                if (i > 100)
                {
                    break;
                }
                d2 *= 2D;
                if (i > 100)
                {
                    return (0.0D / 0.0D);
                }
                d10 = d9 + d2;
                if (d10 < 0.0D)
                {
                    d10 = 0.0D;
                }
                d4 = chi(d10, d1) - d;
                if (d3 * d4 < 0.0D)
                {
                    break;
                }
                d9 = d10;
                i++;
            }
            while (true);
            boolean flag = false;
            for (int j = 1; j <= 100; j++)
            {
                double d15 = (d9 + d10) / 2D;
                double d6 = d4 - d3;
                double d13 = d10 - d9;
                if (Math.abs(d15) == 0.0D)
                {
                    if (Math.abs(d13) < 2.2204460492503001E-015D)
                    {
                        return (d9 + d10) / 2D;
                    }
                }
                else if (Math.abs(d13) < d15 * 10D * 2.2204460492503E-016D)
                {
                    return (d9 + d10) / 2D;
                }
                double d11;
                if (flag)
                {
                    d11 = d15;
                }
                else
                {
                    d11 = d10 - (d4 * d13) / d6;
                }
                flag = false;
                if (d11 < 0.0D)
                {
                    d11 = 0.0D;
                }
                double d5 = chi(d11, d1) - d;
                if (d5 * d4 <= 0.0D)
                {
                    d9 = d10;
                    d3 = d4;
                    d10 = d11;
                    d4 = d5;
                    continue;
                }
                d10 = d11;
                d4 = d5;
                d3 /= 2D;
                if (Math.abs(d4) > Math.abs(d3))
                {
                    d3 = 2D * d3;
                    flag = true;
                }
            }

        }
        return (d9 + d10) / 2D;
    }

    public static double studentsT(double d, double d1)
    {
        if (Double.isNaN(d) || d1 < 1.0D)
        {
            return (0.0D / 0.0D);
        }
        double d3 = d * d;
        double d7;
        if (d1 > d3)
        {
            d3 = d;
            double d11 = d1;
            int i = (int) d11;
            d3 *= d3;
            double d5 = d3 / d11;
            double d12 = 1.0D + d5;
            if (d11 != (double) i || d11 >= 20D || d11 > 200D)
            {
                double d4 = d12 - 1.0D;
                if (d4 != 0.0D)
                {
                    d5 *= Math.log(d12) / d4;
                }
                double d8 = d11 - 0.5D;
                d12 = 48D * d8 * d8;
                d5 *= d8;
                d5 = (((((-0.40000000000000002D * d5 - 3.2999999999999998D) * d5 - 24D) * d5 - 85.5D)
                        / (0.80000000000000004D * (d5 * d5) + 100D + d12) + d5 + 3D)
                        / d12 + 1.0D)
                        * Math.sqrt(d5);
                d7 = Sfun.erfc(d5 * Math.sqrt(0.5D));
            }
            else if (d11 < 20D && d3 < 4D)
            {
                d5 = Math.sqrt(d5);
                double d9 = d5;
                if (d11 == 1.0D)
                {
                    d9 = 0.0D;
                }
                do
                {
                    d11 -= 2D;
                    if (d11 > 1.0D)
                    {
                        d9 = ((d11 - 1.0D) / (d12 * d11)) * d9 + d5;
                    }
                }
                while (d11 > 1.0D);
                if (d11 == 0.0D)
                {
                    d9 /= Math.sqrt(d12);
                }
                if (d11 != 0.0D)
                {
                    d9 = (Math.atan(d5) + d9 / d12) * 0.63661977236758005D;
                }
                d7 = 1.0D - d9;
            }
            else
            {
                double d10 = 1.0D;
                double d6 = d11;
                double d13 = 0.0D;
                for (double d14 = 0.0D; d10 != d14; d10 += d6 / (d11 + d13))
                {
                    d13 += 2D;
                    d14 = d10;
                    d6 *= (d13 - 1.0D) / (d12 * d13);
                }

                for (; d11 > 1.0D; d11 -= 2D)
                {
                    d10 *= (d11 - 1.0D) / (d12 * d11);
                }

                if (d11 != 0.0D)
                {
                    d10 *= 0.63661977236758005D / Math.sqrt(d12);
                }
                d7 = d10;
            }
        }
        else
        {
            d7 = beta(d1 / (d1 + d3), 0.5D * d1, 0.5D);
        }
        double d2;
        if (d > 0.0D)
        {
            d2 = 1.0D - 0.5D * d7;
        }
        else
        {
            d2 = 0.5D * d7;
        }
        return d2;
    }

    public static double inverseStudentsT(double d, double d1)
    {
        if (Double.isNaN(d) || Double.isNaN(d1) || d < 0.0D || d > 1.0D || d1 < 1.0D)
        {
            return (0.0D / 0.0D);
        }
        if (d == 0.0D)
        {
            return (-1.0D / 0.0D);
        }
        if (Math.abs(d - 0.5D) < 2.2204460492503E-016D)
        {
            return 0.0D;
        }
        double d10;
        if (d < 0.5D)
        {
            d10 = 2D * d;
        }
        else
        {
            d10 = 2D * (1.0D - d);
        }
        double d11;
        if (Math.abs(d1 - 1.0D) <= 2.2204460492503E-016D)
        {
            double d2 = d10 * 0.5D * 3.1415926535897931D;
            d11 = Math.cos(d2) / Math.sin(d2);
        }
        else if (Math.abs(d1 - 2D) < 2.2204460492503E-016D)
        {
            d11 = Math.sqrt(2D / (d10 * (2D - d10)) - 2D);
        }
        else if (d1 <= 2D)
        {
            double d8 = 1.0D;
            double d7 = d1;
            double d13 = 1.0D - 2D * d;
            if (d > 0.5D)
            {
                d13 = -d13;
            }
            d11 = inverseBeta(1.0D - d13, 0.5D * d7, 0.5D * d8);
            if (d11 == 0.0D)
            {
                return (1.0D / 0.0D);
            }
            double d9 = ((1.0D / d11 - 1.0D) * d7) / d8;
            d11 = Math.sqrt(d9);
        }
        else
        {
            double d3 = 1.0D / (d1 - 0.5D);
            double d4 = 48D / (d3 * d3);
            double d5 = (((20700D * d3) / d4 - 98D) * d3 - 16D) * d3 + 96.359999999999999D;
            double d6 = ((94.5D / (d4 + d5) - 3D) / d4 + 1.0D) * Math.sqrt(d3 * 0.5D * 3.1415926535897931D) * d1;
            double d14 = d6 * d10;
            double d12 = Math.pow(d14, 2D / d1);
            if (d12 <= d3 + 0.050000000000000003D)
            {
                d12 = (((1.0D / (((d1 + 6D) / (d1 * d12) - 0.088999999999999996D * d6 - 0.82199999999999995D)
                        * (d1 + 2D) * 3D) + 0.5D / (d1 + 4D))
                        * d12 - 1.0D) * (d1 + 1.0D))
                        / (d1 + 2D) + 1.0D / d12;
            }
            else
            {
                d11 = 0.5D * d10;
                double d15 = inverseNormal(d11);
                d12 = d15 * d15;
                if (d1 < 5D)
                {
                    d5 += 0.29999999999999999D * (d1 - 4.5D) * (d15 + 0.59999999999999998D);
                }
                d5 += (((0.050000000000000003D * d6 * d15 - 5D) * d15 - 7D) * d15 - 2D) * d15 + d4;
                d12 = (((((0.40000000000000002D * d12 + 6.2999999999999998D) * d12 + 36D) * d12 + 94.5D) / d5 - d12 - 3D)
                        / d4 + 1.0D)
                        * d15;
                d12 *= d3 * d12;
                d6 = d12;
                if (d6 <= 0.002D)
                {
                    d12 = 0.5D * d12 * d12 + d12;
                }
                else
                {
                    d12 = Math.exp(d12) - 1.0D;
                }
            }
            d11 = Math.sqrt(d1 * d12);
        }
        if (d < 0.5D)
        {
            d11 = -d11;
        }
        return d11;
    }

    public static double Weibull(double d, double d1, double d2)
    {
        if (d < 0.0D)
        {
            return (0.0D / 0.0D);
        }
        else
        {
            return 1.0D - Math.exp(-Math.pow(d / d2, d1));
        }
    }
}
