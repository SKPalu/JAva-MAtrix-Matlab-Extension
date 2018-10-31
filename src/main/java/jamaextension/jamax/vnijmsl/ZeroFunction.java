/*
 * ZeroFunction.java
 *
 * Created on 31 March 2007, 16:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

import java.io.Serializable;

import jamaextension.jamax.ConditionalException;

public class ZeroFunction implements Serializable, Cloneable
{
    public static interface Function
    {
        public abstract double f(double d);
    }

    static final long serialVersionUID = 0xdeffa0b0efb15389L;

    private static final double EPSILON_SMALL = 1.1102230246251999E-016D;
    private double errorAbsolute;
    private double errorRelative;
    private double spread;
    private double spreadTolerance;
    private int maxIterations;
    private int iterations[];
    private transient Function objectF;

    public ZeroFunction()
    {
        errorAbsolute = errorRelative = spreadTolerance = Math.sqrt(1.1102230246251999E-016D);
        spreadTolerance = 1.0000000000000001E-005D;
        spread = 1.0D;
        maxIterations = 100;
    }

    public synchronized void setAbsoluteError(double d)
    {
        if (d < 0.0D)
        {
            Object aobj[] =
            {
                    "AbsoluteError", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "Negative", aobj);
            throw new ConditionalException("setAbsoluteError : Negative--> AbsoluteError = " + d);
        }
        errorAbsolute = d;
    }

    public synchronized void setRelativeError(double d)
    {
        if (d < 0.0D || d > 1.0D)
        {
            Object aobj[] =
            {
                    "RelativeError", new Double(d), "[0,1]"
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotInInterval", aobj);
            throw new ConditionalException("setRelativeError : NotInInterval--> RelativeError = " + d
                    + ", must be in [0,1]");
        }
        errorRelative = d;
    }

    public synchronized void setSpreadTolerance(double d)
    {
        if (d < 0.0D)
        {
            Object aobj[] =
            {
                    "SpreadTolerance", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "Negative", aobj);
            throw new ConditionalException("setSpreadTolerance : NotInInterval--> SpreadTolerance = " + d);
        }
        spreadTolerance = d;
    }

    public synchronized void setSpread(double d)
    {
        spread = d;
    }

    public synchronized void setMaxIterations(int i)
    {
        if (i <= 0)
        {
            Object aobj[] =
            {
                    "MaxIterations", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("setMaxIterations : NotPositive--> MaxIterations = " + i);
        }
        maxIterations = i;
    }

    public synchronized int getIterations(int i)
    {
        return iterations[i];
    }

    public synchronized boolean allConverged()
    {
        for (int i = 0; i < iterations.length; i++)
            if (iterations[i] == maxIterations)
                return false;

        return true;
    }

    public synchronized double[] computeZeros(Function function, double ad[])
    {
        objectF = function;
        int i = ad.length;
        double ad1[] = new double[i];
        iterations = new int[i];
        for (int j = 0; j < i; j++)
        {
            double d;
            double d1;
            double d2;
            if (ad[j] == 0.0D)
            {
                d = -1D;
                d1 = 0.0D;
                d2 = 1.0D;
            }
            else
            {
                d = 0.90000000000000002D * ad[j];
                d1 = ad[j];
                d2 = 1.1000000000000001D * ad[j];
            }
            double d4 = function.f(d);
            double d5 = function.f(d1);
            double d6 = function.f(d2);

            label0: for (iterations[j] = 0; iterations[j] < maxIterations; iterations[j]++)
            {
                double d7 = (d6 - d5) / (d2 - d1);
                double d8 = (d5 - d4) / (d1 - d);
                double d9 = (d7 - d8) / (d2 - d);
                double d10 = d7 + (d2 - d1) * d9;
                double d11 = d10 * d10 - 4D * d6 * d9;
                d11 = Math.sqrt(Math.max(0.0D, d11));
                if (d10 < 0.0D)
                    d11 = -d11;
                double d3 = d2 - (2D * d6) / (d10 + d11);
                d = d1;
                d1 = d2;
                d2 = d3;
                d4 = d5;
                d5 = d6;
                d6 = function.f(d2);
                if (Double.isNaN(d2))
                {
                    d2 = d + Math.random() * (d1 - d);
                    continue;
                }
                int k = iterations[j];
                boolean flag = k > 3 && Math.abs(d3 - d1) < errorRelative;
                boolean flag1 = Math.abs(d6) < errorAbsolute;
                if (!flag && !flag1)
                    continue;
                int l = 0;
                do
                {
                    if (l >= j)
                        break label0;
                    if (Math.abs(ad1[l] - d2) < spreadTolerance)
                    {
                        d2 += spread;
                        d6 = function.f(d2);
                        continue label0;
                    }
                    l++;
                }
                while (true);
            }

            ad1[j] = d2;
        }

        return ad1;
    }

}