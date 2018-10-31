/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Feynman Perceptrons
 */
// public class Hyperbolic {

// }
public class Hyperbolic
{

    // private static final int check = Messages.check(1);
    private static final double zero = 0D;
    private static final double one = 1D;
    private static final double huge = 1.0000000000000001E+300D;
    private static final double tiny = 1E-300D;
    private static final double two54 = Double.longBitsToDouble(0x4350000000000000L);
    private static final double twom54 = Double.longBitsToDouble(0x3c90000000000000L);
    private static final double o_threshold = Double.longBitsToDouble(0x40862e42fefa39efL);
    private static final double ln2 = Double.longBitsToDouble(0x3fe62e42fefa39efL);
    private static final double ln2_hi = Double.longBitsToDouble(0x3fe62e42fee00000L);
    private static final double ln2_lo = Double.longBitsToDouble(0x3dea39ef35793c76L);
    private static final double invln2 = Double.longBitsToDouble(0x3ff71547652b82feL);
    private static final double SINH_XMAX = Double.longBitsToDouble(0x408633ce8fb9f87dL);
    private static final double Q1 = Double.longBitsToDouble(0xbfa11111111110f4L);
    private static final double Q2 = Double.longBitsToDouble(0x3f5a01a019fe5585L);
    private static final double Q3 = Double.longBitsToDouble(0xbf14ce199eaadbb7L);
    private static final double Q4 = Double.longBitsToDouble(0x3ed0cfca86e65239L);
    private static final double Q5 = Double.longBitsToDouble(0xbe8afdb76e09c32dL);
    private static final double Lp1 = Double.longBitsToDouble(0x3fe5555555555593L);
    private static final double Lp2 = Double.longBitsToDouble(0x3fd999999997fa04L);
    private static final double Lp3 = Double.longBitsToDouble(0x3fd2492494229359L);
    private static final double Lp4 = Double.longBitsToDouble(0x3fcc71c51d8e78afL);
    private static final double Lp5 = Double.longBitsToDouble(0x3fc7466496cb03deL);
    private static final double Lp6 = Double.longBitsToDouble(0x3fc39a09d078c69fL);
    private static final double Lp7 = Double.longBitsToDouble(0x3fc2f112df3e5244L);

    private Hyperbolic()
    {
    }

    public static double expm1(double d)
    {
        double d3 = 0.0D;
        long l = Double.doubleToLongBits(d);
        long l1 = l & 0x8000000000000000L;
        double d9 = l1 != 0L ? -d : d;
        long l2 = l & 0x7fffffff00000000L;
        if (l2 >= 0x4043687a00000000L)
        {
            if (l2 >= 0x40862e4200000000L)
            {
                if (l2 >= 0x7ff0000000000000L)
                {
                    if ((l2 & 0xfffffffffffffL) != 0L)
                    {
                        return d + d;
                    }
                    else
                    {
                        return l1 != 0L ? -1D : d;
                    }
                }
                if (d > o_threshold)
                {
                    return (1.0D / 0.0D);
                }
            }
            if (l1 != 0L && d + 1E-300D < 0.0D)
            {
                return -1D;
            }
        }
        int i;
        if (l2 > 0x3fd62e4200000000L)
        {
            double d1;
            double d2;
            if (l2 < 0x3ff0a2b200000000L)
            {
                if (l1 == 0L)
                {
                    d1 = d - ln2_hi;
                    d2 = ln2_lo;
                    i = 1;
                }
                else
                {
                    d1 = d + ln2_hi;
                    d2 = -ln2_lo;
                    i = -1;
                }
            }
            else
            {
                i = (int) (invln2 * d + (l1 != 0L ? -0.5D : 0.5D));
                double d4 = i;
                d1 = d - d4 * ln2_hi;
                d2 = d4 * ln2_lo;
            }
            d = d1 - d2;
            d3 = d1 - d - d2;
        }
        else
        {
            if (l2 < 0x3c90000000000000L)
            {
                double d5 = 1.0000000000000001E+300D + d;
                return d - (d5 - (1.0000000000000001E+300D + d));
            }
            i = 0;
        }
        double d10 = 0.5D * d;
        double d11 = d * d10;
        double d12 = 1.0D + d11 * (Q1 + d11 * (Q2 + d11 * (Q3 + d11 * (Q4 + d11 * Q5))));
        double d6 = 3D - d12 * d10;
        double d13 = d11 * ((d12 - d6) / (6D - d * d6));
        if (i == 0)
        {
            return d - (d * d13 - d11);
        }
        d13 = d * (d13 - d3) - d3;
        d13 -= d11;
        if (i == -1)
        {
            return 0.5D * (d - d13) - 0.5D;
        }
        if (i == 1)
        {
            if (d < -0.25D)
            {
                return -2D * (d13 - (d + 0.5D));
            }
            else
            {
                return 1.0D + 2D * (d - d13);
            }
        }
        if (i <= -2 || i > 56)
        {
            d9 = 1.0D - (d13 - d);
            long l3 = Double.doubleToLongBits(d9);
            l3 += (long) i << 52;
            d9 = Double.longBitsToDouble(l3);
            return d9 - 1.0D;
        }
        d6 = 1.0D;
        if (i < 20)
        {
            double d7 = Double.longBitsToDouble(0x3ff0000000000000L - (0x20000000000000L >> i));
            d9 = d7 - (d13 - d);
            long l4 = Double.doubleToLongBits(d9);
            l4 += (long) i << 52;
            d9 = Double.longBitsToDouble(l4);
        }
        else
        {
            double d8 = Double.longBitsToDouble((long) (1023 - i) << 52);
            d9 = d - (d13 + d8);
            d9++;
            long l5 = Double.doubleToLongBits(d9);
            l5 += (long) i << 52;
            d9 = Double.longBitsToDouble(l5);
        }
        return d9;
    }

    public static double log1p(double d)
    {
        double d2 = 0.0D;
        double d3 = 0.0D;
        int k = 0;
        long l1 = Double.doubleToLongBits(d);
        int j = (int) (l1 >> 32);
        int l = j & 0x7fffffff;
        int i = 1;
        if (j < 0x3fda827a)
        {
            if (l >= 0x3ff00000)
            {
                if (d == -1D)
                {
                    return -two54 / 0.0D;
                }
                else
                {
                    return (d - d) / (d - d);
                }
            }
            if (l < 0x3e200000)
            {
                if (two54 + d > 0.0D && l < 0x3c900000)
                {
                    return d;
                }
                else
                {
                    return d - d * d * 0.5D;
                }
            }
            if (j > 0 || j <= 0xbfd2bec3)
            {
                i = 0;
                d2 = d;
                k = 1;
            }
        }
        if (j >= 0x7ff00000)
        {
            return d + d;
        }
        if (i != 0)
        {
            double d8;
            if (j < 0x43400000)
            {
                d8 = 1.0D + d;
                k = (int) (Double.doubleToLongBits(d8) >> 32);
                i = (k >> 20) - 1023;
                d3 = i <= 0 ? d - (d8 - 1.0D) : 1.0D - (d8 - d);
                d3 /= d8;
            }
            else
            {
                d8 = d;
                k = (int) (Double.doubleToLongBits(d8) >> 32);
                i = (k >> 20) - 1023;
                d3 = 0.0D;
            }
            k &= 0xfffff;
            if (k < 0x6a09e)
            {
                long l2 = Double.doubleToLongBits(d8);
                l2 &= 0xffffffffL;
                l2 |= ((long) k | 0x3ff00000L) << 32;
                d8 = Double.longBitsToDouble(l2);
            }
            else
            {
                i++;
                long l3 = Double.doubleToLongBits(d8);
                l3 &= 0xffffffffL;
                l3 |= ((long) k | 0x3fe00000L) << 32;
                d8 = Double.longBitsToDouble(l3);
                k = 0x100000 - k >> 2;
            }
            d2 = d8 - 1.0D;
        }
        double d1 = 0.5D * d2 * d2;
        if (k == 0)
        {
            if (d2 == 0.0D)
            {
                if (i == 0)
                {
                    return 0.0D;
                }
                else
                {
                    d3 += (double) i * ln2_lo;
                    return (double) i * ln2_hi + d3;
                }
            }
            double d6 = d1 * (1.0D - 0.66666666666666663D * d2);
            if (i == 0)
            {
                return d2 - d6;
            }
            else
            {
                return (double) i * ln2_hi - (d6 - ((double) i * ln2_lo + d3) - d2);
            }
        }
        double d4 = d2 / (2D + d2);
        double d5 = d4 * d4;
        double d7 = d5 * (Lp1 + d5 * (Lp2 + d5 * (Lp3 + d5 * (Lp4 + d5 * (Lp5 + d5 * (Lp6 + d5 * Lp7))))));
        if (i == 0)
        {
            return d2 - (d1 - d4 * (d1 + d7));
        }
        else
        {
            return (double) i * ln2_hi - (d1 - (d4 * (d1 + d7) + ((double) i * ln2_lo + d3)) - d2);
        }
    }

    public static double sinh(double d)
    {
        long l = Double.doubleToLongBits(d);
        double d5 = Math.abs(d);
        int j = (int) (l >> 32);
        int i = j & 0x7fffffff;
        if (i >= 0x7ff00000)
        {
            return d + d;
        }
        double d4 = 0.5D;
        if (j < 0)
        {
            d4 = -d4;
        }
        if (i < 0x40360000)
        {
            if (i < 0x3e300000 && 1.7976931348623157E+308D + d > 1.0D)
            {
                return d;
            }
            double d1 = expm1(d5);
            if (i < 0x3ff00000)
            {
                return d4 * (2D * d1 - (d1 * d1) / (d1 + 1.0D));
            }
            else
            {
                return d4 * (d1 + d1 / (d1 + 1.0D));
            }
        }
        if (i < 0x40862e42)
        {
            return d4 * Math.exp(d5);
        }
        if (d5 <= SINH_XMAX)
        {
            double d3 = Math.exp(0.5D * d5);
            double d2 = d4 * d3;
            return d2 * d3;
        }
        else
        {
            return d * 1.7976931348623157E+308D;
        }
    }

    public static double cosh(double d)
    {
        long l = Double.doubleToLongBits(d);
        double d6 = Math.abs(d);
        double d7 = 0.5D;
        int i = (int) (l >> 32);
        i &= 0x7fffffff;
        if (i >= 0x7ff00000)
        {
            return d * d;
        }
        if (i < 0x3fd62e43)
        {
            double d1 = expm1(d6);
            double d4 = 1.0D + d1;
            if (i < 0x3c800000)
            {
                return d4;
            }
            else
            {
                return 1.0D + (d1 * d1) / (d4 + d4);
            }
        }
        if (i < 0x40360000)
        {
            double d2 = Math.exp(d6);
            return d7 * d2 + d7 / d2;
        }
        if (i < 0x40862e42)
        {
            return d7 * Math.exp(d6);
        }
        if (d6 <= SINH_XMAX)
        {
            double d5 = Math.exp(d7 * d6);
            double d3 = d7 * d5;
            return d3 * d5;
        }
        else
        {
            return (1.0D / 0.0D);
        }
    }

    public static double tanh(double d)
    {
        long l = Double.doubleToLongBits(d);
        double d4 = 2D;
        int i = (int) (l >> 32);
        int j = i & 0x7fffffff;
        if (j >= 0x7ff00000)
        {
            if (i >= 0)
            {
                return 1.0D / d + 1.0D;
            }
            else
            {
                return 1.0D / d - 1.0D;
            }
        }
        double d3;
        if (j < 0x40360000)
        {
            if (j < 0x3c800000)
            {
                return d * (1.0D + d);
            }
            if (j >= 0x3ff00000)
            {
                double d1 = expm1(d4 * Math.abs(d));
                d3 = 1.0D - d4 / (d1 + d4);
            }
            else
            {
                double d2 = expm1(-d4 * Math.abs(d));
                d3 = -d2 / (d2 + d4);
            }
        }
        else
        {
            d3 = 1.0D;
        }
        return i < 0 ? -d3 : d3;
    }

    public static double asinh(double d)
    {
        long l = Double.doubleToLongBits(d);
        double d4 = Math.abs(d);
        int i = (int) (l >> 32);
        int j = i & 0x7fffffff;
        if (j >= 0x7ff00000)
        {
            return d + d;
        }
        if (j < 0x3e300000 && 1.0000000000000001E+300D + d > 1.0D)
        {
            return d;
        }
        double d3;
        if (j > 0x41b00000)
        {
            d3 = Math.log(d4) + ln2;
        }
        else if (j > 0x40000000)
        {
            double d1 = d4;
            d3 = Math.log(2D * d1 + 1.0D / (Math.sqrt(d * d + 1.0D) + d1));
        }
        else
        {
            double d2 = d * d;
            d3 = log1p(d4 + d2 / (1.0D + Math.sqrt(1.0D + d2)));
        }
        return i <= 0 ? -d3 : d3;
    }

    public static double acosh(double d)
    {
        long l = Double.doubleToLongBits(d);
        int i = (int) (l >> 32);
        if (i < 0x3ff00000)
        {
            return (d - d) / (d - d);
        }
        if (i >= 0x41b00000)
        {
            if (i >= 0x7ff00000)
            {
                return d + d;
            }
            else
            {
                return Math.log(d) + ln2;
            }
        }
        if (l == 0x3ff0000000000000L)
        {
            return 0.0D;
        }
        if (i > 0x40000000)
        {
            double d1 = d * d;
            return Math.log(2D * d - 1.0D / (d + Math.sqrt(d1 - 1.0D)));
        }
        else
        {
            double d2 = d - 1.0D;
            return log1p(d2 + Math.sqrt(2D * d2 + d2 * d2));
        }
    }

    public static double atanh(double d)
    {
        long l = Double.doubleToLongBits(d);
        double d2 = Math.abs(d);
        int i = (int) (l >> 32);
        int j = i & 0x7fffffff;
        if (d2 > 1.0D)
        {
            return (d - d) / (d - d);
        }
        if (j == 0x3ff00000)
        {
            return d / 0.0D;
        }
        if (j < 0x3e300000 && 1.0000000000000001E+300D + d > 0.0D)
        {
            return d;
        }
        d = d2;
        double d1;
        if (j < 0x3fe00000)
        {
            d1 = d + d;
            d1 = 0.5D * log1p(d1 + (d1 * d) / (1.0D - d));
        }
        else
        {
            d1 = 0.5D * log1p((d + d) / (1.0D - d));
        }
        return i < 0 ? -d1 : d1;
    }
}
