/*
 * MinUnconMultiVar.java
 *
 * Created on 31 March 2007, 16:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

import java.io.Serializable;

import jamaextension.jamax.ConditionalException;

public class MinUnconMultiVar implements Serializable, Cloneable
{
    public static class UnboundedBelowException extends ConditionalException
    {

        static final long serialVersionUID = 0x2418ff52fe8d540eL;

        public UnboundedBelowException(String s)
        {
            super(s);
        }

        public UnboundedBelowException(String s, Object aobj[])
        {
            super(s);
        }
    }

    public static class MaxIterationsException extends ConditionalException
    {

        static final long serialVersionUID = 0x2418ff52fe8d540eL;

        // throw new ConditionalException("computeMinF : Bound is too Small");

        public MaxIterationsException(String s)
        {
            super(s);
        }

        public MaxIterationsException(String s, Object aobj[])
        {
            super(s);
        }
    }

    public static class FalseConvergenceException extends ConditionalException
    {

        static final long serialVersionUID = 0x2418ff52fe8d540eL;

        public FalseConvergenceException(String s)
        {
            super(s);
        }

        public FalseConvergenceException(String s, Object aobj[])
        {
            super(s);
        }
    }

    public static class ApproximateMinimumException extends ConditionalException
    {

        static final long serialVersionUID = 0x2418ff52fe8d540eL;

        public ApproximateMinimumException(String s)
        {
            super(s);
        }

        public ApproximateMinimumException(String s, Object aobj[])
        {
            super(s);
        }
    }

    public static interface Gradient extends Function
    {

        public abstract void gradient(double ad[], double ad1[]);
    }

    public static interface Function
    {

        public abstract double f(double ad[]);
    }

    static final long serialVersionUID = 0x4507229f1aea9b03L;

    private static final double one = 1D;
    private static final double zero = 0D;
    private static final double tol = 2.2204460492503131E-016D;
    private static double atrans[][];
    private double XGUESS[];
    private double XSCALE[];
    private double FSCALE;
    private double XACC;
    private double GTOL;
    private static double FDIGIT;
    private static double GRADTL;
    private static double STEPTL;
    private static double RFTOL;
    private double AFTOL;
    private double FALSTL;
    private static double STEPMX;
    private static boolean STEPMXSet;
    private static int NMAXS;
    private static int MXITER;
    private static int errorStatus;
    private int ITER;
    private int NFCN;
    private int NGRAD;
    private int IHESS;
    private int N;
    private int NNFCN[];
    private int NNGRAD[];
    private transient Function F;
    private transient Gradient G;

    public MinUnconMultiVar(int i)
    {
        NNFCN = new int[1];
        NNGRAD = new int[1];
        double ad[] = new double[i];
        XGUESS = ad;
        double ad1[] = new double[i];
        for (int j = 0; j < i; j++)
            ad1[j] = 1.0D;

        XSCALE = ad1;
        double d = 1.0D;
        FSCALE = d;
        FDIGIT = 15D;
        MXITER = 100;
        IHESS = 0;
        N = i;
        GRADTL = Math.pow(2.2204460492503131E-016D, 0.33333333333333331D);
        STEPTL = Math.pow(2.2204460492503131E-016D, 0.66666666666666663D);
        RFTOL = Math.max(9.9999999999999995E-021D, STEPTL);
        AFTOL = Math.max(9.9999999999999995E-021D, 4.9303806576313238E-032D);
        FALSTL = 2.2204460492503131E-014D;
        STEPMXSet = false;
        errorStatus = 0;
    }

    public double[] computeMin(Function function) throws FalseConvergenceException, MaxIterationsException,
            UnboundedBelowException
    {
        Gradient gradient = (function instanceof Gradient) ? (Gradient) function : null;
        if (gradient == null)
            return computeMinF(function, gradient);
        else
            return computeMinG(function, gradient);
    }

    private final double[] computeMinF(Function function, Gradient gradient) throws FalseConvergenceException,
            MaxIterationsException, UnboundedBelowException
    {
        int l2;
        double ad2[];
        label0:
        {
            int ai[] = new int[1];
            boolean aflag[] = new boolean[1];
            boolean aflag1[] = new boolean[1];
            int i3 = N;
            double ad[] = new double[i3];
            double ad1[] = new double[i3];
            ad2 = new double[i3];
            double ad3[] = new double[i3];
            double ad4[] = new double[i3];
            double ad5[] = new double[i3];
            double ad6[][] = new double[i3][i3];
            double ad7[] = new double[i3];
            double ad8[] = new double[i3];
            double ad9[] = new double[i3];
            double ad10[] = new double[i3];
            int j3 = i3;
            System.arraycopy(XGUESS, 0, ad2, 0, i3);
            if (!STEPMXSet)
            {
                double d21 = 0.0D;
                for (int i = 0; i < i3; i++)
                    d21 += XSCALE[i] * ad2[i] * (XSCALE[i] * ad2[i]);

                d21 = Math.sqrt(d21);
                double d19 = BLAS.nrm2(i3, XSCALE, 0, 1);
                STEPMX = 1000D * Math.max(d21, d19);
                double d7 = STEPMX;
            }
            double d = 2.2204460492503131E-016D;
            double d1 = Math.max(d, Math.pow(10D, -FDIGIT));
            double d14 = Math.sqrt(Math.max(d1, 2.2204460492503131E-016D));
            l2 = 0;
            ai[0] = 0;
            boolean flag = false;
            aflag[0] = false;
            aflag1[0] = false;
            double d2 = function.f(ad2);
            double d13 = d2;
            for (int k1 = 0; k1 < i3; k1++)
            {
                double d8 = d14 * Math.max(Math.abs(ad2[k1]), 1.0D / XSCALE[k1]);
                if (ad2[k1] < 0.0D)
                    d8 = -d8;
                double d15 = ad2[k1];
                ad2[k1] = d15 + d8;
                double d18 = function.f(ad2);
                ad2[k1] = d15;
                ad4[k1] = (d18 - d2) / d8;
            }

            flag = true;
            du6inf(i3, ad2, ad1, d2, ad4, XSCALE, FSCALE, ai, l2, aflag1, aflag);
            if (errorStatus != 0 || ai[0] == -999)
                break label0;
            double d20 = Math.sqrt(Math.max(Math.abs(d2), FSCALE));
            for (int l1 = 0; l1 < i3; l1++)
            {
                for (int k2 = 0; k2 < i3; k2++)
                    ad6[k2][l1] = 0.0D;

                if (IHESS == 0)
                    ad6[l1][l1] = 1.0D;
                else
                    ad6[l1][l1] = d20 * XSCALE[l1];
            }

            double d3;
            do
            {
                l2++;
                ad3[0] = ad4[0] / ad6[0][0];
                for (int j = 1; j < i3; j++)
                {
                    double d22 = BLAS.dot(j, ad6[j], 0, 1, ad3, 0, 1);
                    ad3[j] = (ad4[j] - d22) / ad6[j][j];
                }

                ad3[i3 - 1] = ad3[i3 - 1] / ad6[i3 - 1][i3 - 1];
                atrans = new jamaextension.jamax.Matrix(ad6).transpose().getArray();// Matrix.transpose(ad6);
                for (int k = i3 - 2; k >= 0; k--)
                {
                    double d23 = BLAS.dot(i3 - k - 1, atrans[k], k + 1, 1, ad3, k + 1, 1);
                    ad3[k] = (ad3[k] - d23) / atrans[k][k];
                }

                BLAS.scal(i3, -1D, ad3, 0, 1);
                for (int l = 0; l < i3; l++)
                    ad7[l] = ad3[l] * XSCALE[l];

                double d4 = BLAS.nrm2(i3, ad7, 0, 1);
                d3 = du17nf(function, gradient, flag, i3, ad2, d2, ad4, ad3, XSCALE, STEPMX, STEPTL, ai, ad, ad5, ad1,
                        aflag, d4, d1, NNFCN, NNGRAD);
                if (ai[0] == 1 && flag)
                {
                    for (int i2 = 0; i2 < i3; i2++)
                    {
                        double d9 = d14 * Math.max(Math.abs(ad2[i2]), 1.0D / XSCALE[i2]);
                        if (ad2[i2] < 0.0D)
                            d9 = -d9;
                        double d16 = ad2[i2];
                        ad2[i2] = d16 + d9;
                        double d24 = function.f(ad2);
                        ad2[i2] = d16 - d9;
                        double d26 = function.f(ad2);
                        ad2[i2] = d16;
                        ad4[i2] = (d24 - d26) / (2D * d9);
                    }

                    flag = false;
                    double d11 = 0.0D;
                    for (int i1 = 0; i1 < i3; i1++)
                    {
                        double d5 = (Math.abs(ad4[i1]) * Math.max(Math.abs(ad2[i1]), 1.0D / XSCALE[i1]))
                                / Math.max(Math.abs(d2), FSCALE);
                        d11 = Math.max(d5, d11);
                    }

                    if (d11 <= GRADTL)
                    {
                        d13 = d2;
                        break label0;
                    }
                }
                if (ai[0] != 1 && flag)
                {
                    double d12 = 0.0D;
                    for (int j1 = 0; j1 < i3; j1++)
                    {
                        double d6 = (Math.abs(ad5[j1]) * Math.max(Math.abs(ad[j1]), 1.0D / XSCALE[j1]))
                                / Math.max(Math.abs(d3), FSCALE);
                        d12 = Math.max(d6, d12);
                    }

                    if (d12 <= 0.10000000000000001D)
                    {
                        for (int j2 = 0; j2 < i3; j2++)
                        {
                            double d10 = d14 * Math.max(Math.abs(ad[j2]), 1.0D / XSCALE[j2]);
                            if (ad[j2] < 0.0D)
                                d10 = -d10;
                            double d17 = ad[j2];
                            ad[j2] = d17 + d10;
                            double d25 = function.f(ad);
                            ad[j2] = d17 - d10;
                            double d27 = function.f(ad);
                            ad[j2] = d17;
                            ad5[j2] = (d25 - d27) / (2D * d10);
                        }

                        flag = false;
                    }
                }
                du6inf(i3, ad, ad1, d3, ad5, XSCALE, FSCALE, ai, l2, aflag1, aflag);
                if (errorStatus != 0 || ai[0] == -999)
                    break;
                du8inf(i3, ad1, ad4, ad5, d1, false, ad6, j3, ad7, ad8, ad9);
                System.arraycopy(ad, 0, ad2, 0, i3);
                System.arraycopy(ad5, 0, ad4, 0, i3);
                d2 = d3;
            }
            while (true);
            System.arraycopy(ad, 0, ad2, 0, i3);
            System.arraycopy(ad5, 0, ad4, 0, i3);
            d13 = d3;
        }
        ITER = l2;
        return ad2;
    }

    private final double[] computeMinG(Function function, Gradient gradient) throws FalseConvergenceException,
            MaxIterationsException, UnboundedBelowException
    {
        int ai[] = new int[1];
        boolean aflag[] = new boolean[1];
        boolean aflag1[] = new boolean[1];
        int l1 = N;
        double ad[] = new double[l1];
        double ad1[] = new double[l1];
        double ad2[] = new double[l1];
        double ad3[] = new double[l1];
        double ad4[] = new double[l1];
        double ad5[] = new double[l1];
        double ad6[][] = new double[l1][l1];
        double ad7[] = new double[l1];
        double ad8[] = new double[l1];
        double ad9[] = new double[l1];
        double ad10[] = new double[l1];
        int i2 = l1;
        System.arraycopy(XGUESS, 0, ad2, 0, l1);
        if (!STEPMXSet)
        {
            double d11 = 0.0D;
            for (int i = 0; i < l1; i++)
                d11 += XSCALE[i] * ad2[i] * (XSCALE[i] * ad2[i]);

            d11 = Math.sqrt(d11);
            double d9 = BLAS.nrm2(l1, XSCALE, 0, 1);
            STEPMX = 1000D * Math.max(d11, d9);
            double d5 = STEPMX;
        }
        double d = 2.2204460492503131E-016D;
        double d1 = Math.max(d, Math.pow(10D, -FDIGIT));
        double d8 = Math.sqrt(Math.max(d1, 2.2204460492503131E-016D));
        int k1 = 0;
        ai[0] = 0;
        aflag[0] = false;
        aflag1[0] = false;
        double d2 = function.f(ad2);
        double d6 = d2;
        gradient.gradient(ad2, ad4);
        boolean flag = false;
        du6inf(l1, ad2, ad1, d2, ad4, XSCALE, FSCALE, ai, k1, aflag1, aflag);
        if (errorStatus == 0 && ai[0] != -999)
        {
            double d10 = Math.sqrt(Math.max(Math.abs(d2), FSCALE));
            for (int i1 = 0; i1 < l1; i1++)
            {
                for (int j1 = 0; j1 < l1; j1++)
                    ad6[j1][i1] = 0.0D;

                if (IHESS == 0)
                    ad6[i1][i1] = 1.0D;
                else
                    ad6[i1][i1] = d10 * XSCALE[i1];
            }

            double d3;
            do
            {
                k1++;
                ad3[0] = ad4[0] / ad6[0][0];
                for (int j = 1; j < l1; j++)
                {
                    double d12 = BLAS.dot(j, ad6[j], 0, 1, ad3, 0, 1);
                    ad3[j] = (ad4[j] - d12) / ad6[j][j];
                }

                ad3[l1 - 1] = ad3[l1 - 1] / ad6[l1 - 1][l1 - 1];
                atrans = new jamaextension.jamax.Matrix(ad6).transpose().getArray();
                for (int k = l1 - 2; k >= 0; k--)
                {
                    double d13 = BLAS.dot(l1 - k - 1, atrans[k], k + 1, 1, ad3, k + 1, 1);
                    ad3[k] = (ad3[k] - d13) / atrans[k][k];
                }

                BLAS.scal(l1, -1D, ad3, 0, 1);
                for (int l = 0; l < l1; l++)
                    ad7[l] = ad3[l] * XSCALE[l];

                double d4 = BLAS.nrm2(l1, ad7, 0, 1);
                d3 = du17nf(function, gradient, flag, l1, ad2, d2, ad4, ad3, XSCALE, STEPMX, STEPTL, ai, ad, ad5, ad1,
                        aflag, d4, d1, NNFCN, NNGRAD);
                du6inf(l1, ad, ad1, d3, ad5, XSCALE, FSCALE, ai, k1, aflag1, aflag);
                if (errorStatus != 0 || ai[0] == -999)
                    break;
                du8inf(l1, ad1, ad4, ad5, d1, true, ad6, i2, ad7, ad8, ad9);
                System.arraycopy(ad, 0, ad2, 0, l1);
                System.arraycopy(ad5, 0, ad4, 0, l1);
                d2 = d3;
            }
            while (true);
            System.arraycopy(ad, 0, ad2, 0, l1);
            System.arraycopy(ad5, 0, ad4, 0, l1);
            double d7 = d3;
        }
        ITER = k1;
        return ad2;
    }

    public void setGuess(double ad[])
    {
        XGUESS = (double[]) ad.clone();
    }

    public void setXscale(double ad[])
    {
        for (int i = 0; i < ad.length; i++)
            if (ad[i] <= 0.0D)
            {
                Object aobj[] =
                {
                    ""
                };
                // Messages.throwIllegalArgumentException("com.imsl.math",
                // "XscaleNotPositive", aobj);
                throw new ConditionalException("setXscale : XscaleNotPositive");
            }

        XSCALE = (double[]) ad.clone();
    }

    public void setFscale(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                ""
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "FscaleNotPositive", aobj);
            FSCALE = d;
            throw new ConditionalException("setFscale : FscaleNotPositiv");
        }
    }

    public void setDigits(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "FDIGIT", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("setDigits : NotPositive --> FDIGIT");
        }
        else
        {
            FDIGIT = d;
        }
    }

    public void setMaxIterations(int i)
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
        else
        {
            MXITER = i;
        }
    }

    public void setIhess(int i)
    {
        IHESS = i;
    }

    public void setStepTolerance(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "StepTolerance", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("setStepTolerance : NotPositive--> StepTolerance = " + d);
        }
        else
        {
            STEPTL = d;
        }
    }

    public void setRelativeTolerance(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "RelativeTolerance", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("setRelativeTolerance : NotPositive--> RelativeTolerance = " + d);
        }
        else
        {
            RFTOL = d;
        }
    }

    public void setFalseConvergenceTolerance(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "FalseConvergenceTolerance", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("setFalseConvergenceTolerance : NotPositive--> FalseConvergenceTolerance = "
                    + d);
        }
        else
        {
            FALSTL = d;
        }
    }

    public void setMaximumStepsize(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "MaximumStepsize", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("setMaximumStepsize : NotPositive--> MaximumStepsize = " + d);
        }
        else
        {
            STEPMX = d;
            STEPMXSet = true;
        }
    }

    public void setGradientTolerance(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "GradientTolerance", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("setGradientTolerance : NotPositive--> GradientTolerance = " + d);
        }
        else
        {
            GRADTL = d;
        }
    }

    public synchronized int getIterations()
    {
        return ITER;
    }

    public int getErrorStatus()
    {
        return errorStatus;
    }

    private static final void du6inf(int i, double ad[], double ad1[], double d, double ad2[], double ad3[], double d1,
            int ai[], int j, boolean aflag[], boolean aflag1[]) throws FalseConvergenceException,
            MaxIterationsException, UnboundedBelowException
    {
        double d5 = 0.0D;
        double d2 = 0.0D;
        for (int k = 0; k < i; k++)
        {
            double d3 = (Math.abs(ad2[k]) * Math.max(Math.abs(ad[k]), 1.0D / ad3[k])) / Math.max(Math.abs(d), d1);
            d5 = Math.max(d3, d5);
        }

        if (d5 <= GRADTL)
            ai[0] = -999;
        else if (j == 0)
            NMAXS = 0;
        else if (ai[0] == 1)
        {
            errorStatus = 1;
        }
        else
        {
            double d6 = 0.0D;
            for (int l = 0; l < i; l++)
            {
                double d4 = Math.abs(ad1[l]) / Math.max(Math.abs(ad[l]), 1.0D / ad3[l]);
                d6 = Math.max(d4, d6);
            }

            if (d6 <= STEPTL)
            {
                ai[0] = -999;
                errorStatus = 3;
            }
            else if (ai[0] == 2)
            {
                errorStatus = 2;
            }
            else
            {
                if (ai[0] == 3)
                    throw new FalseConvergenceException("MinUnconMultiVar.FalseConvergence", null);
                if (j >= MXITER)
                    throw new MaxIterationsException("MinUnconMultiVar.MaxIterations", null);
                if (aflag1[0])
                {
                    NMAXS++;
                    if (NMAXS == 5)
                        throw new UnboundedBelowException("MinUnconMultiVar.UnboundedBelow", null);
                }
            }
        }
    }

    private static final double du17nf(Function function, Gradient gradient, boolean flag, int i, double ad[],
            double d, double ad1[], double ad2[], double ad3[], double d1, double d2, int ai[], double ad4[],
            double ad5[], double ad6[], boolean aflag[], double d3, double d4, int ai1[], int ai2[])
    {
        double d5 = 0.0001D;
        aflag[0] = false;
        ai[0] = 2;
        double d20 = 0.0D;
        double d19 = 0.0D;
        double d33 = d4;
        double d34 = Math.sqrt(Math.max(d33, 2.2204460492503131E-016D));
        System.arraycopy(ad2, 0, ad6, 0, i);
        if (d3 > d1)
        {
            double d16 = d1 / d3;
            BLAS.scal(i, d16, ad6, 0, 1);
            d3 = d1;
        }
        double d22 = BLAS.dot(i, ad1, 0, ad6, 0);
        double d21 = 0.0D;
        for (int j = 0; j < i; j++)
        {
            double d29 = Math.abs(ad6[j]) / Math.max(Math.abs(ad[j]), 1.0D / ad3[j]);
            d21 = Math.max(d21, d29);
        }

        double d12;
        if (gradient == null)
            d12 = Math.max(d2, d4) / d21;
        else
            d12 = d2 / d21;
        double d9 = 1.0D;
        double d35;
        do
        {
            System.arraycopy(ad, 0, ad4, 0, i);
            BLAS.axpy(i, d9, ad6, 0, ad4, 0);
            d35 = function.f(ad4);
            if (d35 <= d + d5 * d9 * d22)
            {
                if (gradient == null)
                {
                    if (flag)
                    {
                        for (int l = 0; l < i; l++)
                        {
                            double d36 = d34 * Math.max(Math.abs(ad4[l]), 1.0D / ad3[l]);
                            if (ad4[l] < 0.0D)
                                d36 = -d36;
                            double d51 = ad4[l];
                            ad4[l] = d51 + d36;
                            double d42 = function.f(ad4);
                            ad4[l] = d51;
                            ad5[l] = (d42 - d35) / d36;
                        }

                    }
                    else
                    {
                        for (int i1 = 0; i1 < i; i1++)
                        {
                            double d37 = d34 * Math.max(Math.abs(ad4[i1]), 1.0D / ad3[i1]);
                            if (ad4[i1] < 0.0D)
                                d37 = -d37;
                            double d52 = ad4[i1];
                            ad4[i1] = d52 + d37;
                            double d45 = function.f(ad4);
                            ad4[i1] = d52 - d37;
                            double d48 = function.f(ad4);
                            ad4[i1] = d52;
                            ad5[i1] = (d45 - d48) / (2D * d37);
                        }

                    }
                }
                else
                {
                    gradient.gradient(ad4, ad5);
                }
                double d14 = 0.90000000000000002D;
                double d23 = BLAS.dot(i, ad5, 0, ad6, 0);
                if (d23 < d14 * d22)
                {
                    if (d9 == 1.0D && d3 < d1)
                    {
                        double d11 = d1 / d3;
                        do
                        {
                            d20 = d9;
                            d19 = d35;
                            d9 = Math.min(2D * d9, d11);
                            System.arraycopy(ad, 0, ad4, 0, i);
                            BLAS.axpy(i, d9, ad6, 0, ad4, 0);
                            d35 = function.f(ad4);
                            if (d35 <= d + d5 * d9 * d22)
                            {
                                if (gradient == null)
                                {
                                    if (flag)
                                    {
                                        for (int j1 = 0; j1 < i; j1++)
                                        {
                                            double d38 = d34 * Math.max(Math.abs(ad4[j1]), 1.0D / ad3[j1]);
                                            if (ad4[j1] < 0.0D)
                                                d38 = -d38;
                                            double d53 = ad4[j1];
                                            ad4[j1] = d53 + d38;
                                            double d43 = function.f(ad4);
                                            ad4[j1] = d53;
                                            ad5[j1] = (d43 - d35) / d38;
                                        }

                                    }
                                    else
                                    {
                                        for (int k1 = 0; k1 < i; k1++)
                                        {
                                            double d39 = d34 * Math.max(Math.abs(ad4[k1]), 1.0D / ad3[k1]);
                                            if (ad4[k1] < 0.0D)
                                                d39 = -d39;
                                            double d54 = ad4[k1];
                                            ad4[k1] = d54 + d39;
                                            double d46 = function.f(ad4);
                                            ad4[k1] = d54 - d39;
                                            double d49 = function.f(ad4);
                                            ad4[k1] = d54;
                                            ad5[k1] = (d46 - d49) / (2D * d39);
                                        }

                                    }
                                }
                                else
                                {
                                    gradient.gradient(ad4, ad5);
                                }
                                d23 = BLAS.dot(i, ad5, 0, ad6, 0);
                            }
                        }
                        while (d35 <= d + d9 * d5 * d22 && d9 < d11 && d23 < d14 * d22);
                    }
                    if (d9 < 1.0D || d9 > 1.0D && d35 > d + d5 * d9 * d22)
                    {
                        double d10 = Math.min(d9, d20);
                        double d7 = Math.abs(d20 - d9);
                        double d17;
                        double d18;
                        if (d9 < d20)
                        {
                            d18 = d35;
                            d17 = d19;
                        }
                        else
                        {
                            d18 = d19;
                            d17 = d35;
                        }
                        do
                        {
                            double d30 = d23 * d7 * d7;
                            double d31 = d18 + d23 * d7;
                            if (Math.abs(d17 - d31) < 1.1102230246251565E-016D)
                            {
                                aflag[0] = false;
                                ai[0] = 1;
                                System.arraycopy(ad, 0, ad4, 0, i);
                                d35 = d;
                                return d35;
                            }
                            double d8 = -d30 / (2D * (d17 - d31));
                            if (d8 < 0.20000000000000001D * d7)
                                d8 = 0.20000000000000001D * d7;
                            d9 = d10 + d8;
                            System.arraycopy(ad, 0, ad4, 0, i);
                            BLAS.axpy(i, d9, ad6, 0, ad4, 0);
                            d35 = function.f(ad4);
                            if (d35 > d + d5 * d9 * d22)
                            {
                                d7 = d8;
                                d17 = d35;
                            }
                            else
                            {
                                if (gradient == null)
                                {
                                    if (flag)
                                    {
                                        for (int l1 = 0; l1 < i; l1++)
                                        {
                                            double d40 = d34 * Math.max(Math.abs(ad4[l1]), 1.0D / ad3[l1]);
                                            if (ad4[l1] < 0.0D)
                                                d40 = -d40;
                                            double d55 = ad4[l1];
                                            ad4[l1] = d55 + d40;
                                            double d44 = function.f(ad4);
                                            ad4[l1] = d55;
                                            ad5[l1] = (d44 - d35) / d40;
                                        }

                                    }
                                    else
                                    {
                                        for (int i2 = 0; i2 < i; i2++)
                                        {
                                            double d41 = d34 * Math.max(Math.abs(ad4[i2]), 1.0D / ad3[i2]);
                                            if (ad4[i2] < 0.0D)
                                                d41 = -d41;
                                            double d56 = ad4[i2];
                                            ad4[i2] = d56 + d41;
                                            double d47 = function.f(ad4);
                                            ad4[i2] = d56 - d41;
                                            double d50 = function.f(ad4);
                                            ad4[i2] = d56;
                                            ad5[i2] = (d47 - d50) / (2D * d41);
                                        }

                                    }
                                }
                                else
                                {
                                    gradient.gradient(ad4, ad5);
                                }
                                d23 = BLAS.dot(i, ad5, 0, ad6, 0);
                                if (d23 < d14 * d22)
                                {
                                    d10 = d9;
                                    d7 -= d8;
                                    d18 = d35;
                                }
                            }
                        }
                        while (d23 < d14 * d22 && d7 >= d12);
                        if (d23 < d14 * d22)
                        {
                            d35 = d18;
                            System.arraycopy(ad, 0, ad4, 0, i);
                            BLAS.axpy(i, d10, ad6, 0, ad4, 0);
                        }
                    }
                }
                ai[0] = 0;
                if (d9 * d3 > 0.98999999999999999D * d1)
                    aflag[0] = true;
            }
            else if (d9 < d12)
            {
                ai[0] = 1;
                System.arraycopy(ad, 0, ad4, 0, i);
            }
            else
            {
                double d32;
                if (d9 == 1.0D)
                {
                    d32 = -d22 / (2D * (d35 - d - d22));
                }
                else
                {
                    double d24 = d35 - d - d9 * d22;
                    double d25 = d19 - d - d20 * d22;
                    double d26 = 1.0D / (d9 - d20);
                    double d27 = d9 * d9;
                    double d28 = d20 * d20;
                    double d6 = d26 * (d24 / d27 - d25 / d28);
                    double d13 = d26 * ((-d24 * d20) / d27 + (d25 * d9) / d28);
                    double d15 = d13 * d13 - 3D * d6 * d22;
                    if (d6 == 0.0D)
                        d32 = -d22 / (2D * d13);
                    else
                        d32 = (-d13 + Math.sqrt(d15)) / (3D * d6);
                    if (d32 > 0.5D * d9)
                        d32 = 0.5D * d9;
                }
                d20 = d9;
                d19 = d35;
                if (d32 <= 0.10000000000000001D * d9)
                    d9 = 0.10000000000000001D * d9;
                else
                    d9 = d32;
            }
        }
        while (ai[0] >= 2);
        for (int k = 0; k < i; k++)
            ad6[k] = ad4[k] - ad[k];

        return d35;
    }

    private static final void du8inf(int i, double ad[], double ad1[], double ad2[], double d, boolean flag,
            double ad3[][], int j, double ad4[], double ad5[], double ad6[])
    {
        double ad7[] = new double[1];
        double ad8[] = new double[1];
        double ad9[] = new double[1];
        double ad10[][] = new double[1][1];
        double d3 = 2.2204460492503131E-016D;
        double d2 = d3;
        for (int k = 0; k < i; k++)
            ad4[k] = ad2[k] - ad1[k];

        double d7 = BLAS.dot(i, ad4, 0, ad, 0);
        double d6 = BLAS.nrm2(i, ad, 0, 1);
        double d11 = BLAS.nrm2(i, ad4, 0, 1);
        if (d7 >= Math.sqrt(d2) * d6 * d11)
        {
            atrans = new jamaextension.jamax.Matrix(ad3).transpose().getArray();
            for (int l = 0; l < i; l++)
                ad5[l] = BLAS.dot(i - l, atrans[l], l, 1, ad, l, 1);

            double d10 = BLAS.nrm2(i, ad5, 0, 1);
            double d1 = Math.sqrt(d7) / d10;
            double d4;
            if (flag)
                d4 = d;
            else
                d4 = Math.sqrt(d);
            boolean flag1 = true;
            for (int i1 = 0; i1 < i; i1++)
            {
                double d8 = BLAS.dot(i1 + 1, ad3[i1], 0, 1, ad5, 0, 1);
                double d5 = Math.max(Math.abs(ad1[i1]), Math.abs(ad2[i1]));
                if (Math.abs(ad4[i1] - d8) >= 2.2204460492503131E-016D * d5)
                    flag1 = false;
                ad6[i1] = ad4[i1] - d1 * d8;
            }

            if (!flag1)
            {
                double d9 = 1.0D / (Math.sqrt(d7) * d10);
                BLAS.scal(i, d9, ad5, 0, 1);
                for (int j1 = 1; j1 < i; j1++)
                {
                    for (int j2 = 0; j2 < j1; j2++)
                        ad3[j2][j1] = ad3[j1][j2];

                    for (int k2 = 0; k2 < j1; k2++)
                        ad3[j1][k2] = 0.0D;

                }

                int l2;
                for (l2 = i - 1; ad5[l2] == 0.0D && l2 > 0; l2--)
                    ;
                for (int k1 = l2; k1 > 0; k1--)
                {
                    ad8[0] = ad5[k1 - 1];
                    ad9[0] = ad5[k1];
                    BLAS.rotg(ad8, ad9, ad, ad7);
                    ad5[k1] = ad9[0];
                    ad5[k1 - 1] = ad8[0];
                    BLAS.rot((l2 - k1) + 2, ad3[k1 - 1], k1 - 1, 1, ad3[k1], k1 - 1, 1, ad, ad7);
                }

                BLAS.axpy(i, ad5[0], ad6, 0, 1, ad3[0], 0, 1);
                for (int l1 = 0; l1 < l2; l1++)
                {
                    ad8[0] = ad3[l1][l1];
                    ad9[0] = ad3[l1 + 1][l1];
                    BLAS.rotg(ad8, ad9, ad, ad7);
                    BLAS.rot(i - l1, ad3[l1], l1, 1, ad3[l1 + 1], l1, 1, ad, ad7);
                }

                for (int i2 = 1; i2 < i; i2++)
                {
                    for (int i3 = 0; i3 < i2; i3++)
                        ad3[i2][i3] = ad3[i3][i2];

                }

            }
        }
    }

}