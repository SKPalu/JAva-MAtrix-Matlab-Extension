/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Feynman Perceptrons
 */
// Referenced classes of package com.imsl.stat:
// InverseCdf, CdfFunction, Cdf
public class ChiSquaredTest
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
     * 
     * public static class NoObservationsException extends IMSLRuntimeException
     * {
     * 
     * static final long serialVersionUID = 0x9e0f1cb1e711650dL;
     * 
     * public NoObservationsException(String s, Object aobj[]) {
     * super(Messages.formatMessage("com.imsl.stat.ErrorMessages", s, aobj)); }
     * }
     * 
     * public static class NotCDFException extends IMSLRuntimeException {
     * 
     * static final long serialVersionUID = 0x8ea2afd59aff397fL;
     * 
     * public NotCDFException(String s, Object aobj[]) {
     * super(Messages.formatMessage("com.imsl.stat.ErrorMessages", s, aobj)); }
     * }
     */

    // private static final int check = Messages.check(1);
    private static final double EPSILON_LARGE = 2.2204460492503E-016D;
    private CdfFunction cdf;
    private InverseCdf inverseCdf;
    private int nParameters;
    private double chiSquared;
    private double p;
    private double df;
    private double eps;
    private double cutpoints[];
    private double counts[];
    private double expect[];
    private double range[];
    private double pupper;
    private double plower;
    private boolean haveComputed;

    public ChiSquaredTest(CdfFunction cdffunction, double ad[], int i) // throws
                                                                       // NotCDFException
    {
        nParameters = i;
        init(cdffunction, ad.length);
        cutpoints = (double[]) ad.clone();
    }

    public ChiSquaredTest(CdfFunction cdffunction, int i, int j) // throws
                                                                 // NotCDFException,
                                                                 // InverseCdf.DidNotConvergeException
    {
        nParameters = j;
        init(cdffunction, i);
        computeCutpoints(i);
    }

    private void init(CdfFunction cdffunction, int i) // throws NotCDFException
    {
        cdf = cdffunction;
        range = range;
        eps = 2.2204460492503001E-015D;
        haveComputed = false;
        counts = new double[i + 1];
        expect = new double[i + 1];
        pupper = 1.0D;
        plower = 0.0D;
        range = null;
        inverseCdf = new InverseCdf(cdffunction);
        inverseCdf.setTolerance(0.0001D);
    }

    public void setRange(double d, double d1) // throws NotCDFException
    {
        double d2 = Math.abs(d1 - d);
        double d3 = Math.max(Math.abs(d), Math.abs(d1));
        if (d3 == 0.0D)
        {
            d3 = 1.0D;
        }
        if (d2 <= eps * d3)
        {
            return;
        }
        range = new double[2];
        range[0] = d;
        range[1] = d1;
        pupper = cdf.cdf(d1);
        plower = cdf.cdf(d);
        if (pupper > 1.0D + eps || plower < 0.0D - eps)
        {
            Object aobj[] =
            {
                    new Double(plower), new Double(pupper)
            };
            // throw new NotCDFException("ChiSquaredTest.NotCdf", aobj);
            throw new IllegalArgumentException("ChiSquaredTest.NotCdf");
        }
        if (pupper <= plower)
        {
            Object aobj1[] =
            {
                new Double(pupper - plower)
            };
            // throw new NotCDFException("ChiSquaredTest.OutOfRange", aobj1);
            throw new IllegalArgumentException("ChiSquaredTest.OutOfRange");
        }
        else
        {
            return;
        }
    }

    private synchronized void computeCutpoints(int i) // throws NotCDFException,
                                                      // InverseCdf.DidNotConvergeException
    {
        cutpoints = new double[i];
        double d = 0.0D;
        for (int j = 0; j < i; j++)
        {
            double d1 = j + 1;
            d1 = (d1 / (double) (i + 1)) * (pupper - plower) + plower;
            d = cutpoints[j] = inverseCdf.eval(d1, d);
            if (j == 0)
            {
                continue;
            }
            double d2 = Math.abs(cutpoints[j] - cutpoints[j - 1]);
            if (d2 < eps)
            {
                cutpoints[j] = cutpoints[j - 1];
            }
        }

    }

    public void update(double ad[], double ad1[]) // throws NotCDFException
    {
        if (ad1 == null)
        {
            for (int i = 0; i < ad.length; i++)
            {
                update(ad[i], 1.0D);
            }

        }
        else
        {
            for (int j = 0; j < ad.length; j++)
            {
                update(ad[j], ad1[j]);
            }

        }
    }

    public synchronized void update(double d, double d1) // throws
                                                         // NotCDFException
    {
        haveComputed = false;
        if (Double.isNaN(d1) || Double.isNaN(d))
        {
            return;
        }
        if (range != null && (d > range[1] || d <= range[0]))
        {
            return;
        }
        for (int i = 0; i < cutpoints.length; i++)
        {
            if (d <= cutpoints[i])
            {
                counts[i] += d1;
                return;
            }
        }

        counts[cutpoints.length] += d1;
    }

    private synchronized void compute() // throws NotCDFException
    {
        for (int i = 0; i < counts.length; i++)
        {
            if (counts[i] < -2.2204460492503001E-014D) // throw new
                                                       // NoObservationsException("ChiSquaredTest.AllDeleted",
                                                       // null);
            {
                throw new IllegalArgumentException("ChiSquaredTest.AllDeleted");
            }
        }

        double d = ssum(counts);
        if (d <= 0.0D) // throw new
                       // NoObservationsException("ChiSquaredTest.AllMissing",
                       // null);
        {
            throw new IllegalArgumentException("ChiSquaredTest.AllMissing");
        }
        df = counts.length;
        double d1 = plower;
        for (int j = 0; j < counts.length; j++)
        {
            double d2 = 0.0D;
            if (j + 1 < counts.length)
            {
                d2 = cdf.cdf(cutpoints[j]);
                if (d2 < plower - eps || d2 > pupper + eps)
                {
                    Object aobj[] =
                    {
                            new Double(plower), new Double(pupper), new Double(d2), new Integer(j)
                    };
                    // throw new NotCDFException("ChiSquaredTest.BadCdfRange",
                    // aobj);
                    throw new IllegalArgumentException("ChiSquaredTest.BadCdfRange");
                }
                expect[j] = (d2 - d1) / (pupper - plower);
            }
            else
            {
                expect[j] = (pupper - d1) / (pupper - plower);
            }
            d1 = d2;
            expect[j] *= d;
            if (expect[j] < 1.0D)
            {
                // Warning.print(this, "com.imsl.stat", "ChiSquaredTest.Less1",
                // null);
                System.out.println("ChiSquaredTest.Less1");
                continue;
            }
            if (expect[j] < 5D) // Warning.print(this, "com.imsl.stat",
                                // "ChiSquaredTest.Less5", null);
            {
                System.out.println("ChiSquaredTest.Less5");
            }
        }

        chiSquared = 0.0D;
        for (int k = 0; k < counts.length; k++)
        {
            double d3 = counts[k] - expect[k];
            if (expect[k] > 0.0D)
            {
                chiSquared += (d3 * d3) / expect[k];
            }
            else
            {
                df--;
            }
        }

        df += -1 - nParameters;
        if (df > 0.5D)
        {
            p = 1.0D - Cdf.chi(chiSquared, df);
        }
        else
        {
            p = (0.0D / 0.0D);
        }
        haveComputed = true;
    }

    public double getChiSquared() // throws NotCDFException
    {
        if (!haveComputed)
        {
            compute();
        }
        return chiSquared;
    }

    public double getP() // throws NotCDFException
    {
        if (!haveComputed)
        {
            compute();
        }
        return p;
    }

    public double getDegreesOfFreedom() // throws NotCDFException
    {
        if (!haveComputed)
        {
            compute();
        }
        return df;
    }

    public void setCutpoints(double ad[])
    {
        haveComputed = false;
        cutpoints = (double[]) ad.clone();
    }

    public double[] getCutpoints()
    {
        return (double[]) cutpoints.clone();
    }

    public double[] getCellCounts()
    {
        return (double[]) counts.clone();
    }

    public double[] getExpectedCounts()
    {
        if (!haveComputed)
        {
            compute();
        }
        return (double[]) expect.clone();
    }

    private static double ssum(double ad[])
    {
        double d = 0.0D;
        for (int i = 0; i < ad.length; i++)
        {
            d += ad[i];
        }

        return d;
    }
}
