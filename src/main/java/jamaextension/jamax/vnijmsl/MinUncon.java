/*
 * MinUncon.java
 *
 * Created on 31 March 2007, 16:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

import java.io.Serializable;

import jamaextension.jamax.ConditionalException;

public class MinUncon implements Serializable, Cloneable
{
    public static interface Derivative extends Function
    {

        public abstract double g(double d);
    }

    public static interface Function
    {

        public abstract double f(double d);
    }

    static final long serialVersionUID = 0x4507229f1aea9b03L;
    // private static final int check = Messages.check(1);
    private static final double one = 1D;
    private static final double zero = 0D;
    private static final double tol = 2.2204460492503131E-016D;
    private double XGUESS;
    private double STEP;
    private double BOUND;
    private double XACC;
    private double GTOL;
    private int MAXFN;
    private transient Function F;
    private transient Derivative G;

    public MinUncon()
    {
        XGUESS = 0.0D;
        STEP = 0.10000000000000001D;
        BOUND = 100D;
        XACC = 1E-008D;
        MAXFN = 100;
    }

    public double computeMin(Function function)
    {
        if (function instanceof Derivative)
            return computeMinD((Derivative) function);
        else
            return computeMinF(function);
    }

    private double computeMinF(Function function)
    {
        double d78 = XGUESS;
        int i = 0;
        double d71 = 0.0D;
        double d18 = 0.0D;
        double d17 = 0.0D;
        byte byte1 = 0;
        double d70 = 0.0D;
        double d = 0.0D;
        double d44 = 0.0D;
        double d68 = 0.0D;
        double d72 = 0.0D;
        double d19 = 0.0D;
        double d69 = 0.0D;
        double d16 = 0.0D;
        byte byte0 = 0;
        label0: do
        {
            double d20;
            label1:
            {
                i++;
                d20 = function.f(d78);
                if (i <= 1)
                {
                    d68 = d78;
                    d70 = d78;
                    d17 = d20;
                    d = Math.max(0.0D, XACC);
                    if (BOUND <= d)
                    {
                        byte0 = 2;
                        break label0;
                    }
                    d44 = STEP;
                    double d79 = Math.abs(BOUND);
                    if (d44 < 0.0D)
                        d79 = -d79;
                    if (Math.abs(d44) > BOUND)
                        d44 = d79;
                    if (d44 == 0.0D)
                        d44 = 0.01D * BOUND;
                    d79 = Math.abs(d);
                    if (d44 < 0.0D)
                        d79 = -d79;
                    if (Math.abs(d44) < d)
                        d44 = d79;
                    do
                    {
                        d69 = d70 + 0.5D * Math.abs(d44);
                        d71 = d70 + Math.abs(d44);
                        if (d69 > d70 && d71 > d69)
                            break;
                        d44 = 5D * d44;
                    }
                    while (true);
                    if (BOUND < Math.abs(d44))
                    {
                        byte0 = 2;
                        break label0;
                    }
                    byte1 = 1;
                    d78 = d70 + d44;
                    double d21 = d70 + 1.5D * d44;
                    if (Math.abs(d21 - d68) >= BOUND)
                    {
                        double d80 = Math.abs(BOUND);
                        if (d44 < 0.0D)
                            d80 = -d80;
                        d78 = d68 + d80;
                        byte1 = 2;
                    }
                    continue;
                }
                if (i >= 3)
                {
                    d72 = d71;
                    d19 = d18;
                    if (d20 >= d17)
                    {
                        if (byte1 >= 4)
                        {
                            d71 = d78;
                            d18 = d20;
                            double d45 = (d70 - d71) / (d69 - d72);
                            if (d45 > 0.0D)
                                if (d16 != d17 && d19 == d17 && d18 == d17)
                                {
                                    d71 = d70;
                                    d70 = d78;
                                    d18 = d17;
                                    d17 = d20;
                                }
                                else
                                {
                                    double d1 = (d17 - d16) / (d70 - d69);
                                    double d6 = (d18 - d17) / (d71 - d70);
                                    double d63;
                                    if (byte1 >= 5)
                                    {
                                        double d11 = (d19 - d18) / (d72 - d71);
                                        d63 = 0.0D;
                                        if (d6 != 0.0D)
                                        {
                                            d63 = Math.abs(d69 - d71);
                                            double d46 = (d11 - d6) / (d69 - d71);
                                            if (d46 < 0.0D)
                                            {
                                                double d22 = 0.5D * Math.abs(d6
                                                        * ((d72 - d70) / (d11 - d6) + (d69 - d71) / (d6 - d1)));
                                                if (d22 < d63)
                                                    d63 = d22;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        byte1 = 5;
                                        d63 = 0.01D * Math.abs(d69 - d71);
                                    }
                                    d63 = Math.max(d63, 0.90000000000000002D * d);
                                    d78 = d70;
                                    if (d1 != d6)
                                        d78 = (0.5D * (d1 * (d70 + d71) - d6 * (d69 + d70))) / (d1 - d6);
                                    double d47 = (d69 - d70) / (d70 - d71);
                                    if (d47 < 1.0D)
                                    {
                                        double d23 = d69;
                                        d69 = d71;
                                        d71 = d23;
                                        d23 = d16;
                                        d16 = d18;
                                        d18 = d23;
                                    }
                                    if (Math.abs(d69 - d70) <= d)
                                        break label0;
                                    d47 = (d69 - d70) / (d70 - d71);
                                    if (d47 > 10D)
                                    {
                                        double d81 = 3D;
                                        if (d69 - d70 < 0.0D)
                                            d81 = -d81;
                                        double d24 = (d78 - d70) * d81;
                                        d24 = Math.max(d24, d63);
                                        d24 = Math.max(d24, Math.abs(d70 - d71));
                                        d24 = Math.min(d24, 0.10000000000000001D * Math.abs(d69 - d70));
                                        d81 = Math.abs(d24);
                                        if (d69 - d70 < 0.0D)
                                            d81 = -d81;
                                        d78 = d70 + d81;
                                    }
                                    else if (Math.abs(d69 - d70) <= 2.8999999999999999D * d)
                                    {
                                        d78 = 0.5D * (d69 + d70);
                                        if (Math.abs(d78 - d70) > d)
                                            d78 = 0.67000000000000004D * d70 + 0.33000000000000002D * d69;
                                    }
                                    else if (Math.abs(d78 - d70) < d63)
                                    {
                                        double d82 = Math.abs(d63);
                                        if (d78 - d70 < 0.0D)
                                            d82 = -d82;
                                        d78 = d70 + d82;
                                        if (Math.abs(d78 - d71) < Math.abs(d78 - d70))
                                        {
                                            double d83 = Math.abs(d63);
                                            if (d69 - d70 < 0.0D)
                                                d83 = -d83;
                                            d78 = d70 + d83;
                                        }
                                        if (Math.abs(d78 - d69) < Math.abs(d78 - d70))
                                            d78 = 0.5D * (d69 + d70);
                                    }
                                    if (d78 != d70 && (d69 - d78) / (d69 - d70) > 0.0D
                                            && (d78 - d71) / (d69 - d70) > 0.0D)
                                    {
                                        if (i < MAXFN)
                                            continue;
                                        byte0 = 4;
                                        break label0;
                                    }
                                    d78 = d69;
                                    do
                                    {
                                        double d73 = 0.5D * (d78 + d70);
                                        double d48 = (d73 - d70) / (d69 - d70);
                                        if (d48 <= 0.0D)
                                            break;
                                        d48 = (d78 - d73) / (d69 - d70);
                                        if (d48 <= 0.0D)
                                            break;
                                        d78 = d73;
                                    }
                                    while (true);
                                    if (d78 != d69)
                                    {
                                        if (i < MAXFN)
                                            continue;
                                        byte0 = 4;
                                    }
                                    else
                                    {
                                        byte0 = 5;
                                    }
                                    break label0;
                                }
                        }
                        else
                        {
                            if (byte1 == 3)
                            {
                                byte0 = 3;
                                break label0;
                            }
                            d69 = d78;
                            d16 = d20;
                            byte1 = 4;
                            double d2 = (d17 - d16) / (d70 - d69);
                            double d7 = (d18 - d17) / (d71 - d70);
                            double d64;
                            if (byte1 >= 5)
                            {
                                double d12 = (d19 - d18) / (d72 - d71);
                                d64 = 0.0D;
                                if (d7 != 0.0D)
                                {
                                    d64 = Math.abs(d69 - d71);
                                    double d49 = (d12 - d7) / (d69 - d71);
                                    if (d49 < 0.0D)
                                    {
                                        double d25 = 0.5D * Math.abs(d7
                                                * ((d72 - d70) / (d12 - d7) + (d69 - d71) / (d7 - d2)));
                                        if (d25 < d64)
                                            d64 = d25;
                                    }
                                }
                            }
                            else
                            {
                                byte1 = 5;
                                d64 = 0.01D * Math.abs(d69 - d71);
                            }
                            d64 = Math.max(d64, 0.90000000000000002D * d);
                            d78 = d70;
                            if (d2 != d7)
                                d78 = (0.5D * (d2 * (d70 + d71) - d7 * (d69 + d70))) / (d2 - d7);
                            double d50 = (d69 - d70) / (d70 - d71);
                            if (d50 < 1.0D)
                            {
                                double d26 = d69;
                                d69 = d71;
                                d71 = d26;
                                d26 = d16;
                                d16 = d18;
                                d18 = d26;
                            }
                            if (Math.abs(d69 - d70) <= d)
                                break label0;
                            d50 = (d69 - d70) / (d70 - d71);
                            if (d50 > 10D)
                            {
                                double d84 = 3D;
                                if (d69 - d70 < 0.0D)
                                    d84 = -d84;
                                double d27 = (d78 - d70) * d84;
                                d27 = Math.max(d27, d64);
                                d27 = Math.max(d27, Math.abs(d70 - d71));
                                d27 = Math.min(d27, 0.10000000000000001D * Math.abs(d69 - d70));
                                d84 = Math.abs(d27);
                                if (d69 - d70 < 0.0D)
                                    d84 = -d84;
                                d78 = d70 + d84;
                            }
                            else if (Math.abs(d69 - d70) <= 2.8999999999999999D * d)
                            {
                                d78 = 0.5D * (d69 + d70);
                                if (Math.abs(d78 - d70) > d)
                                    d78 = 0.67000000000000004D * d70 + 0.33000000000000002D * d69;
                            }
                            else if (Math.abs(d78 - d70) < d64)
                            {
                                double d85 = Math.abs(d64);
                                if (d78 - d70 < 0.0D)
                                    d85 = -d85;
                                d78 = d70 + d85;
                                if (Math.abs(d78 - d71) < Math.abs(d78 - d70))
                                {
                                    double d86 = Math.abs(d64);
                                    if (d69 - d70 < 0.0D)
                                        d86 = -d86;
                                    d78 = d70 + d86;
                                }
                                if (Math.abs(d78 - d69) < Math.abs(d78 - d70))
                                    d78 = 0.5D * (d69 + d70);
                            }
                            if (d78 != d70 && (d69 - d78) / (d69 - d70) > 0.0D && (d78 - d71) / (d69 - d70) > 0.0D)
                                continue;
                            d78 = d69;
                            do
                            {
                                double d74 = 0.5D * (d78 + d70);
                                double d51 = (d74 - d70) / (d69 - d70);
                                if (d51 <= 0.0D)
                                    break;
                                d51 = (d78 - d74) / (d69 - d70);
                                if (d51 <= 0.0D)
                                    break;
                                d78 = d74;
                            }
                            while (true);
                            if (d78 != d69)
                            {
                                if (i < MAXFN)
                                    continue;
                                byte0 = 4;
                            }
                            else
                            {
                                byte0 = 5;
                            }
                            break label0;
                        }
                        break label1;
                    }
                }
                else if (d20 >= d17)
                {
                    d44 = -d44;
                    d71 = d78;
                    d18 = d20;
                    byte1 = 1;
                    d78 = d70 + d44;
                    double d28 = d70 + 1.5D * d44;
                    if (Math.abs(d28 - d68) >= BOUND)
                    {
                        double d87 = Math.abs(BOUND);
                        if (d44 < 0.0D)
                            d87 = -d87;
                        d78 = d68 + d87;
                        byte1 = 2;
                    }
                    continue;
                }
                d71 = d70;
                d18 = d17;
                d70 = d78;
                d17 = d20;
                if (byte1 < 4)
                {
                    if (byte1 <= 1)
                    {
                        if (i > 2)
                        {
                            double d42 = (d70 - d71) / (d70 - d72);
                            double d43 = (d17 - d18) / (d17 - d19);
                            double d29 = 9D;
                            if (d43 < d42)
                                d29 = (1.5D * (d42 - d43 / d42)) / (d43 - d42);
                            d29 = Math.min(d29, 9D);
                            d29 = Math.max(d29, 2D);
                            d44 = d29 * d44;
                        }
                        d78 = d70 + d44;
                        double d30 = d70 + 1.5D * d44;
                        if (Math.abs(d30 - d68) >= BOUND)
                        {
                            double d88 = Math.abs(BOUND);
                            if (d44 < 0.0D)
                                d88 = -d88;
                            d78 = d68 + d88;
                            byte1 = 2;
                        }
                        continue;
                    }
                    if (byte1 == 3)
                    {
                        d69 = d72;
                        d16 = d19;
                        byte1 = 4;
                        double d3 = (d17 - d16) / (d70 - d69);
                        double d8 = (d18 - d17) / (d71 - d70);
                        double d65;
                        if (byte1 >= 5)
                        {
                            double d13 = (d19 - d18) / (d72 - d71);
                            d65 = 0.0D;
                            if (d8 != 0.0D)
                            {
                                d65 = Math.abs(d69 - d71);
                                double d52 = (d13 - d8) / (d69 - d71);
                                if (d52 < 0.0D)
                                {
                                    double d31 = 0.5D * Math.abs(d8
                                            * ((d72 - d70) / (d13 - d8) + (d69 - d71) / (d8 - d3)));
                                    if (d31 < d65)
                                        d65 = d31;
                                }
                            }
                        }
                        else
                        {
                            byte1 = 5;
                            d65 = 0.01D * Math.abs(d69 - d71);
                        }
                        d65 = Math.max(d65, 0.90000000000000002D * d);
                        d78 = d70;
                        if (d3 != d8)
                            d78 = (0.5D * (d3 * (d70 + d71) - d8 * (d69 + d70))) / (d3 - d8);
                        double d53 = (d69 - d70) / (d70 - d71);
                        if (d53 < 1.0D)
                        {
                            double d32 = d69;
                            d69 = d71;
                            d71 = d32;
                            d32 = d16;
                            d16 = d18;
                            d18 = d32;
                        }
                        if (Math.abs(d69 - d70) <= d)
                            break label0;
                        d53 = (d69 - d70) / (d70 - d71);
                        if (d53 > 10D)
                        {
                            double d89 = 3D;
                            if (d69 - d70 < 0.0D)
                                d89 = -d89;
                            double d33 = (d78 - d70) * d89;
                            d33 = Math.max(d33, d65);
                            d33 = Math.max(d33, Math.abs(d70 - d71));
                            d33 = Math.min(d33, 0.10000000000000001D * Math.abs(d69 - d70));
                            d89 = Math.abs(d33);
                            if (d69 - d70 < 0.0D)
                                d89 = -d89;
                            d78 = d70 + d89;
                        }
                        else if (Math.abs(d69 - d70) <= 2.8999999999999999D * d)
                        {
                            d78 = 0.5D * (d69 + d70);
                            if (Math.abs(d78 - d70) > d)
                                d78 = 0.67000000000000004D * d70 + 0.33000000000000002D * d69;
                        }
                        else if (Math.abs(d78 - d70) < d65)
                        {
                            double d90 = Math.abs(d65);
                            if (d78 - d70 < 0.0D)
                                d90 = -d90;
                            d78 = d70 + d90;
                            if (Math.abs(d78 - d71) < Math.abs(d78 - d70))
                            {
                                double d91 = Math.abs(d65);
                                if (d69 - d70 < 0.0D)
                                    d91 = -d91;
                                d78 = d70 + d91;
                            }
                            if (Math.abs(d78 - d69) < Math.abs(d78 - d70))
                                d78 = 0.5D * (d69 + d70);
                        }
                        if (d78 != d70 && (d69 - d78) / (d69 - d70) > 0.0D && (d78 - d71) / (d69 - d70) > 0.0D)
                            continue;
                        d78 = d69;
                        do
                        {
                            double d75 = 0.5D * (d78 + d70);
                            double d54 = (d75 - d70) / (d69 - d70);
                            if (d54 <= 0.0D)
                                break;
                            d54 = (d78 - d75) / (d69 - d70);
                            if (d54 <= 0.0D)
                                break;
                            d78 = d75;
                        }
                        while (true);
                        if (d78 != d69)
                        {
                            if (i < MAXFN)
                                continue;
                            byte0 = 4;
                        }
                        else
                        {
                            byte0 = 5;
                        }
                        break label0;
                    }
                    byte1 = 3;
                    double d34 = Math.max(0.90000000000000002D * d, 0.01D * Math.abs(d70 - d71));
                    double d92 = Math.abs(d34);
                    if (d71 - d70 < 0.0D)
                        d92 = -d92;
                    d78 = d70 + d92;
                    if (Math.abs(d78 - d71) < Math.abs(d78 - d70))
                        d78 = 0.5D * (d70 + d71);
                    double d55 = (d70 - d78) / (d70 - d71);
                    if (d55 > 0.0D)
                        continue;
                    byte0 = 3;
                    break label0;
                }
                double d56 = (d70 - d71) / (d69 - d72);
                if (d56 > 0.0D)
                    if (d16 != d17 && d19 == d17 && d18 == d17)
                    {
                        d71 = d70;
                        d70 = d78;
                        d18 = d17;
                        d17 = d20;
                    }
                    else
                    {
                        double d4 = (d17 - d16) / (d70 - d69);
                        double d9 = (d18 - d17) / (d71 - d70);
                        double d66;
                        if (byte1 >= 5)
                        {
                            double d14 = (d19 - d18) / (d72 - d71);
                            d66 = 0.0D;
                            if (d9 != 0.0D)
                            {
                                d66 = Math.abs(d69 - d71);
                                double d57 = (d14 - d9) / (d69 - d71);
                                if (d57 < 0.0D)
                                {
                                    double d35 = 0.5D * Math.abs(d9
                                            * ((d72 - d70) / (d14 - d9) + (d69 - d71) / (d9 - d4)));
                                    if (d35 < d66)
                                        d66 = d35;
                                }
                            }
                        }
                        else
                        {
                            byte1 = 5;
                            d66 = 0.01D * Math.abs(d69 - d71);
                        }
                        d66 = Math.max(d66, 0.90000000000000002D * d);
                        d78 = d70;
                        if (d4 != d9)
                            d78 = (0.5D * (d4 * (d70 + d71) - d9 * (d69 + d70))) / (d4 - d9);
                        double d58 = (d69 - d70) / (d70 - d71);
                        if (d58 < 1.0D)
                        {
                            double d36 = d69;
                            d69 = d71;
                            d71 = d36;
                            d36 = d16;
                            d16 = d18;
                            d18 = d36;
                        }
                        if (Math.abs(d69 - d70) <= d)
                            break label0;
                        d58 = (d69 - d70) / (d70 - d71);
                        if (d58 > 10D)
                        {
                            double d93 = 3D;
                            if (d69 - d70 < 0.0D)
                                d93 = -d93;
                            double d37 = (d78 - d70) * d93;
                            d37 = Math.max(d37, d66);
                            d37 = Math.max(d37, Math.abs(d70 - d71));
                            d37 = Math.min(d37, 0.10000000000000001D * Math.abs(d69 - d70));
                            d93 = Math.abs(d37);
                            if (d69 - d70 < 0.0D)
                                d93 = -d93;
                            d78 = d70 + d93;
                        }
                        else if (Math.abs(d69 - d70) <= 2.8999999999999999D * d)
                        {
                            d78 = 0.5D * (d69 + d70);
                            if (Math.abs(d78 - d70) > d)
                                d78 = 0.67000000000000004D * d70 + 0.33000000000000002D * d69;
                        }
                        else if (Math.abs(d78 - d70) < d66)
                        {
                            double d94 = Math.abs(d66);
                            if (d78 - d70 < 0.0D)
                                d94 = -d94;
                            d78 = d70 + d94;
                            if (Math.abs(d78 - d71) < Math.abs(d78 - d70))
                            {
                                double d95 = Math.abs(d66);
                                if (d69 - d70 < 0.0D)
                                    d95 = -d95;
                                d78 = d70 + d95;
                            }
                            if (Math.abs(d78 - d69) < Math.abs(d78 - d70))
                                d78 = 0.5D * (d69 + d70);
                        }
                        if (d78 != d70 && (d69 - d78) / (d69 - d70) > 0.0D && (d78 - d71) / (d69 - d70) > 0.0D)
                            continue;
                        d78 = d69;
                        do
                        {
                            double d76 = 0.5D * (d78 + d70);
                            double d59 = (d76 - d70) / (d69 - d70);
                            if (d59 <= 0.0D)
                                break;
                            d59 = (d78 - d76) / (d69 - d70);
                            if (d59 <= 0.0D)
                                break;
                            d78 = d76;
                        }
                        while (true);
                        if (d78 != d69)
                            continue;
                        byte0 = 5;
                        break label0;
                    }
            }
            do
            {
                double d38 = d69;
                d69 = d72;
                d72 = d38;
                d38 = d16;
                d16 = d19;
                d19 = d38;
                if (d16 == d17 || d19 != d17 || d18 != d17)
                    break;
                d71 = d70;
                d70 = d78;
                d18 = d17;
                d17 = d20;
            }
            while (true);
            double d5 = (d17 - d16) / (d70 - d69);
            double d10 = (d18 - d17) / (d71 - d70);
            double d67;
            if (byte1 >= 5)
            {
                double d15 = (d19 - d18) / (d72 - d71);
                d67 = 0.0D;
                if (d10 != 0.0D)
                {
                    d67 = Math.abs(d69 - d71);
                    double d60 = (d15 - d10) / (d69 - d71);
                    if (d60 < 0.0D)
                    {
                        double d39 = 0.5D * Math.abs(d10 * ((d72 - d70) / (d15 - d10) + (d69 - d71) / (d10 - d5)));
                        if (d39 < d67)
                            d67 = d39;
                    }
                }
            }
            else
            {
                byte1 = 5;
                d67 = 0.01D * Math.abs(d69 - d71);
            }
            d67 = Math.max(d67, 0.90000000000000002D * d);
            d78 = d70;
            if (d5 != d10)
                d78 = (0.5D * (d5 * (d70 + d71) - d10 * (d69 + d70))) / (d5 - d10);
            double d61 = (d69 - d70) / (d70 - d71);
            if (d61 < 1.0D)
            {
                double d40 = d69;
                d69 = d71;
                d71 = d40;
                d40 = d16;
                d16 = d18;
                d18 = d40;
            }
            if (Math.abs(d69 - d70) <= d)
                break;
            d61 = (d69 - d70) / (d70 - d71);
            if (d61 > 10D)
            {
                double d96 = 3D;
                if (d69 - d70 < 0.0D)
                    d96 = -d96;
                double d41 = (d78 - d70) * d96;
                d41 = Math.max(d41, d67);
                d41 = Math.max(d41, Math.abs(d70 - d71));
                d41 = Math.min(d41, 0.10000000000000001D * Math.abs(d69 - d70));
                d96 = Math.abs(d41);
                if (d69 - d70 < 0.0D)
                    d96 = -d96;
                d78 = d70 + d96;
            }
            else if (Math.abs(d69 - d70) <= 2.8999999999999999D * d)
            {
                d78 = 0.5D * (d69 + d70);
                if (Math.abs(d78 - d70) > d)
                    d78 = 0.67000000000000004D * d70 + 0.33000000000000002D * d69;
            }
            else if (Math.abs(d78 - d70) < d67)
            {
                double d97 = Math.abs(d67);
                if (d78 - d70 < 0.0D)
                    d97 = -d97;
                d78 = d70 + d97;
                if (Math.abs(d78 - d71) < Math.abs(d78 - d70))
                {
                    double d98 = Math.abs(d67);
                    if (d69 - d70 < 0.0D)
                        d98 = -d98;
                    d78 = d70 + d98;
                }
                if (Math.abs(d78 - d69) < Math.abs(d78 - d70))
                    d78 = 0.5D * (d69 + d70);
            }
            if (d78 != d70 && (d69 - d78) / (d69 - d70) > 0.0D && (d78 - d71) / (d69 - d70) > 0.0D)
                continue;
            d78 = d69;
            do
            {
                double d77 = 0.5D * (d78 + d70);
                double d62 = (d77 - d70) / (d69 - d70);
                if (d62 <= 0.0D)
                    break;
                d62 = (d78 - d77) / (d69 - d70);
                if (d62 <= 0.0D)
                    break;
                d78 = d77;
            }
            while (true);
            if (d78 != d69)
                continue;
            byte0 = 5;
            break;
        }
        while (true);
        if (byte0 == 2)
        {

            throw new ConditionalException("computeMinF : Bound is too Small");
        }
        else if (byte0 == 5)
        {

            System.out.println("computeMinF : Rounding Errors");
        }
        else if (byte0 == 3)
        {

            System.out.println("computeMinF : BeyondBound-Exception");
        }
        d78 = d70;
        return d78;
    }

    public void setGuess(double d)
    {
        XGUESS = d;
    }

    public void setStep(double d)
    {
        STEP = d;
    }

    public void setBound(double d)
    {
        BOUND = d;
    }

    public void setAccuracy(double d)
    {
        XACC = d;
    }

    public void setDerivtol(double d)
    {
        GTOL = d;
    }

    private double computeMinD(Derivative derivative)
    {
        double d18 = XGUESS;
        double d16 = 0.0D;
        double d3 = 0.0D;
        double d2 = 0.0D;
        boolean flag = false;
        double d15 = 0.0D;
        double d = 0.0D;
        double d6 = 0.0D;
        double d13 = 0.0D;
        double d17 = 0.0D;
        double d4 = 0.0D;
        double d14 = 0.0D;
        double d1 = 0.0D;
        double d19 = XGUESS - BOUND;
        double d20 = XGUESS + BOUND;
        double d28;
        if (XACC < 2.2204460492503131E-016D)
            d28 = Math.sqrt(2.2204460492503131E-016D);
        else
            d28 = XACC;
        double d29 = Math.max(GTOL, 0.0D);
        d1 = derivative.f(d19);
        d2 = derivative.f(d20);
        double d5 = derivative.f(d18);
        double d21;
        if (d1 < d5)
        {
            if (d1 < d2)
            {
                d21 = derivative.g(d19);
                d18 = d19;
                d5 = d1;
                if (d21 > 0.0D)
                {

                    throw new ConditionalException("computeMinD : BeyondBoundException - lower");
                }
            }
            else
            {
                d21 = derivative.g(d20);
                d18 = d20;
                d5 = d2;
                if (d21 < 0.0D)
                {

                    throw new ConditionalException("computeMinD : BeyondBoundException - upper");
                }
            }
        }
        else if (d2 < d5)
        {
            d21 = derivative.g(d20);
            d18 = d20;
            d5 = d2;
            if (d21 < 0.0D)
            {

                throw new ConditionalException("computeMinD : BeyondBoundException - upper");
            }
        }
        else
        {
            d21 = derivative.g(d18);
        }
        d6 = Math.abs(d21);
        do
        {
            if (Math.abs(d21) <= d29)
                return d18;
            double d37 = Math.max(1.0D, Math.abs(d18)) * d28;
            if (d6 < d37)
                d6 = d37;
            double d36;
            if (d21 > 0.0D)
            {
                d36 = d18 - d6;
                if (d36 <= d19)
                {
                    d36 = d19;
                    double d22 = d1;
                    double d25 = derivative.g(d36);
                    if (d22 >= d5)
                    {
                        double d30 = Math.abs(d36 - d18);
                        if (Math.abs(d30 - d37) > 2.2204460492503131E-015D && Math.abs(d21) > d29)
                            return cnvchk(d36, d18, d37, d21, d25, d29, d5, d22, d28, derivative);
                    }
                    if (d25 * d21 <= 0.0D)
                    {
                        double d7 = d18;
                        d18 = d36;
                        d36 = d7;
                        d7 = d5;
                        d5 = d22;
                        d22 = d7;
                        d7 = d21;
                        d21 = d25;
                        d25 = d7;
                        double d31 = Math.abs(d36 - d18);
                        if (Math.abs(d31 - d37) > 2.2204460492503131E-015D && Math.abs(d21) > d29)
                            return cnvchk(d36, d18, d37, d21, d25, d29, d5, d22, d28, derivative);
                    }
                    double d8 = (d21 - d25) / d6;
                    d18 = d36;
                    d5 = d22;
                    d21 = d25;
                    d6 = Math.abs(d21 / d8);
                    continue;
                }
            }
            else
            {
                d36 = d18 + d6;
                if (d36 >= d20)
                {
                    d36 = d20;
                    double d23 = d2;
                    double d26 = derivative.g(d36);
                    if (d23 >= d5)
                    {
                        double d32 = Math.abs(d36 - d18);
                        if (Math.abs(d32 - d37) > 2.2204460492503131E-015D && Math.abs(d21) > d29)
                            return cnvchk(d36, d18, d37, d21, d26, d29, d5, d23, d28, derivative);
                    }
                    if (d26 * d21 <= 0.0D)
                    {
                        double d9 = d18;
                        d18 = d36;
                        d36 = d9;
                        d9 = d5;
                        d5 = d23;
                        d23 = d9;
                        d9 = d21;
                        d21 = d26;
                        d26 = d9;
                        double d33 = Math.abs(d36 - d18);
                        if (Math.abs(d33 - d37) > 2.2204460492503131E-015D && Math.abs(d21) > d29)
                            return cnvchk(d36, d18, d37, d21, d26, d29, d5, d23, d28, derivative);
                    }
                    double d10 = (d21 - d26) / d6;
                    d18 = d36;
                    d5 = d23;
                    d21 = d26;
                    d6 = Math.abs(d21 / d10);
                    continue;
                }
            }
            double d24 = derivative.f(d36);
            double d27 = derivative.g(d36);
            if (d24 >= d5)
            {
                double d34 = Math.abs(d36 - d18);
                if (Math.abs(d34 - d37) > 2.2204460492503131E-015D && Math.abs(d21) > d29)
                    return cnvchk(d36, d18, d37, d21, d27, d29, d5, d24, d28, derivative);
            }
            if (d27 * d21 <= 0.0D)
            {
                double d11 = d18;
                d18 = d36;
                d36 = d11;
                d11 = d5;
                d5 = d24;
                d24 = d11;
                d11 = d21;
                d21 = d27;
                d27 = d11;
                double d35 = Math.abs(d36 - d18);
                if (Math.abs(d35 - d37) > 2.2204460492503131E-015D && Math.abs(d21) > d29)
                    return cnvchk(d36, d18, d37, d21, d27, d29, d5, d24, d28, derivative);
            }
            double d12 = (d21 - d27) / d6;
            d18 = d36;
            d5 = d24;
            d21 = d27;
            d6 = Math.abs(d21 / d12);
        }
        while (true);
    }

    private static final double dU3MID(double d, double d1, double d2, double d3, double d4, double d5)
    {
        double d6 = d - d1;
        double d7 = d5 + d4 + (3D * (d3 - d2)) / d6;
        double d8 = Math.sqrt(d7 * d7 - d5 * d4);
        if (d6 > 0.0D)
            d8 = -d8;
        double d9 = d - d6 * ((d4 - d8 - d7) / (d4 - d5 - 2D * d8));
        if (d9 < d)
        {
            if (d9 < d1)
                d9 = d - d6 * (((d4 + d8) - d7) / ((d4 - d5) + 2D * d8));
        }
        else if (d9 > d && d9 > d1)
            d9 = d - d6 * (((d4 + d8) - d7) / ((d4 - d5) + 2D * d8));
        return d9;
    }

    private static final double cnvchk(double d, double d1, double d2, double d3, double d4, double d5, double d6,
            double d7, double d8, Derivative derivative)
    {
        do
        {
            double d10 = dU3MID(d1, d, d6, d7, d3, d4);
            double d9 = d10 - d1;
            double d11 = d - d1;
            if (Math.abs(d9) < 0.25D * Math.abs(d11))
                d9 = 0.25D * d11;
            else if (Math.abs(d9) > 0.75D * Math.abs(d11))
                d9 = 0.75D * d11;
            d2 = Math.max(1.0D, Math.abs(d1)) * d8;
            if (Math.abs(d9) < d2)
            {
                double d12 = Math.abs(d2);
                if (d9 < 0.0D)
                    d12 = -d12;
                d9 = d12;
            }
            d10 = d1 + d9;
            double d13 = derivative.f(d10);
            double d14 = derivative.g(d10);
            if (d13 >= d6)
            {
                d = d10;
                d7 = d13;
                d4 = d14;
            }
            else if (d3 * d14 < 0.0D)
            {
                d = d1;
                d7 = d6;
                d4 = d3;
                d1 = d10;
                d6 = d13;
                d3 = d14;
            }
            else
            {
                d1 = d10;
                d6 = d13;
                d3 = d14;
            }
            d11 = Math.abs(d - d1);
            if (Math.abs(d11 - d2) <= 2.2204460492503131E-015D)
                return d1;
        }
        while (Math.abs(d3) > d5);
        return d1;
    }

}