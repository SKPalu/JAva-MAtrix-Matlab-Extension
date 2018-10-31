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

public class InverseCdf implements Serializable
{
    /*
     * public static class DidNotConvergeException extends IMSLException {
     * 
     * static final long serialVersionUID = 0x2418ff52fe8d540eL;
     * 
     * public DidNotConvergeException(String s) { super(s); }
     * 
     * public DidNotConvergeException(String s, Object aobj[]) {
     * super("com.imsl.math", s, aobj); } }
     */

    static final long serialVersionUID = 0xbe3bd929e9e7c9dL;
    // private static final int check = Messages.check(1);
    private CdfFunction cdf;
    private double tolerance;

    public InverseCdf(CdfFunction cdffunction)
    {
        cdf = cdffunction;
        tolerance = 0.0001D;
    }

    public void setTolerance(double d)
    {
        tolerance = d;
    }

    public double eval(double d, double d1) // throws DidNotConvergeException
    {
        double d2 = d1;
        double d3 = cdf.cdf(d2) - d;
        if (d3 == 0.0D)
        {
            return d2;
        }
        double d4;
        double d5;
        if (Math.abs(d1) >= 1.0D)
        {
            d4 = d1 * 1.05D;
            d5 = d4 - d2;
        }
        else
        {
            d4 = d1 + 0.050000000000000003D;
            d5 = 0.050000000000000003D;
        }
        double d7 = cdf.cdf(d4) - d;
        double d8 = Math.max(0.01D, (d7 - d3) / d5);
        double d9 = -d3 / d8;
        int i = 0;
        do
        {
            if (i > 100) // throw new
                         // DidNotConvergeException("InverseCDF.NoBound", null);
            {
                throw new IllegalArgumentException("InverseCDF.NoBound");
            }
            d9 *= 2D;
            d4 = d2 + d9;
            d7 = cdf.cdf(d4) - d;
            if (d3 * d7 < 0.0D)
            {
                break;
            }
            d2 = d4;
            i++;
        }
        while (true);
        boolean flag = false;
        for (int j = 1; j <= 100; j++)
        {
            double d10 = (d2 + d4) / 2D;
            double d11 = d7 - d3;
            double d6 = d4 - d2;
            if (d6 != 0.0D && d11 == 0.0D)
            {
                Object aobj[] =
                {
                        new Double(d2), new Double(d4), new Double(d3)
                };
                // throw new
                // ChiSquaredTest.NotCDFException("InverseCDF.NotMonotone",
                // aobj);
                throw new IllegalArgumentException("InverseCDF.NotMonotone");
            }
            if (Math.abs(d10) != 0.0D)
            {
                if (Math.abs(d6 / d10) < tolerance)
                {
                    return (d2 + d4) / 2D;
                }
            }
            else if (Math.abs(d6) < tolerance)
            {
                return (d2 + d4) / 2D;
            }
            double d12 = flag ? d10 : d4 - (d7 * d6) / d11;
            flag = false;
            double d13 = cdf.cdf(d12) - d;
            if (d13 * d7 <= 0.0D)
            {
                d2 = d4;
                d3 = d7;
                d4 = d12;
                d7 = d13;
                continue;
            }
            d4 = d12;
            d7 = d13;
            d3 /= 2D;
            if (Math.abs(d7) > Math.abs(d3))
            {
                d3 *= 2D;
                flag = true;
            }
        }

        // throw new DidNotConvergeException("InverseCDF.NoBound", null);
        throw new IllegalArgumentException("InverseCDF.NotMonotone");
    }
}
