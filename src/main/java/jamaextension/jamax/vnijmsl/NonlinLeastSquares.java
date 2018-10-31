/*
 * NonlinLeastSquares.java
 *
 * Created on 31 March 2007, 16:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

import java.io.Serializable;

import jamaextension.jamax.ConditionalRuleException;

public class NonlinLeastSquares implements Serializable, Cloneable
{

    public static interface Jacobian extends Function
    {

        public abstract void jacobian(double ad[], double ad1[][]);
    }

    public static interface Function
    {

        public abstract void f(double ad[], double ad1[]);
    }

    public static class TooManyIterationsException extends ConditionalRuleException
    {

        static final long serialVersionUID = 0x515ecd282859097bL;

        public TooManyIterationsException(String s)
        {
            super("TooManyIterationsException", s);
        }
    }

    public static class StepMaxException extends ConditionalRuleException
    {

        static final long serialVersionUID = 0x298825cd373745ebL;

        public StepMaxException(String s)
        {
            super("StepMaxException", s);
        }
    }

    public static class StepToleranceException extends ConditionalRuleException
    {

        static final long serialVersionUID = 0xeedb20ce25c93ba9L;

        public StepToleranceException(String s)
        {
            super("StepToleranceException", s);
        }

        /*
         * public StepToleranceException(String s, Object aobj[]) {
         * super("com.imsl.math", s, aobj); }
         */
    }

    public static class RelativeFunctionConvergenceException extends ConditionalRuleException
    {

        static final long serialVersionUID = 0xe170ed47711aef26L;

        public RelativeFunctionConvergenceException(String s)
        {
            super("RelativeFunctionConvergenceException", s);
        }

        /*
         * public RelativeFunctionConvergenceException(String s, Object aobj[])
         * { super("com.imsl.math", s, aobj); }
         */
    }

    public static class FalseConvergenceException extends ConditionalRuleException
    {

        static final long serialVersionUID = 0x98f354d7e077323cL;

        public FalseConvergenceException(String s)
        {
            super("FalseConvergenceException", s);
        }

        /*
         * public FalseConvergenceException(String s, Object aobj[]) {
         * super("com.imsl.math", s, aobj); }
         */
    }

    static final long serialVersionUID = 0xb8e79cc1fdade2c7L;
    // private static final int check = Messages.check(1);
    private static final double one = 1D;
    private static final double zero = 0D;
    private static final double tol = 2.2204460492503131E-016D;
    private static final double small = 2.2250738585072014E-308D;
    private static final double big = 1.7976931348623157E+308D;
    private static final double EPSILON_SMALL_SQRT = 1.0536712127723701E-008D;
    private static final double EPSILON_SMALL = 1.1102230246251999E-016D;
    private int M;
    private int N;
    private int m;
    private int n;
    private double x[];
    private double XGUESS[];
    private double XSCALE[];
    private double FSCALE[];
    private double DELTA;
    private double FALSTL;
    private double AFTOL;
    private double RFTOL;
    private double STEPTL;
    private double GRADTL;
    private double STEPMX;
    private int MAXJAC;
    private int MAXFCN;
    private int MXITER;
    private int NGOOD;
    private int INFO;
    private int IPARAM[];
    private double RPARAM[];
    private boolean first;
    private boolean gauss;
    private boolean mxtake;
    private transient Function F;
    private transient double deltap[] =
    {
        0.0D
    };
    private transient double gnleng[] =
    {
        0.0D
    };
    private transient double phi[] =
    {
        0.0D
    };
    private transient double phip[] =
    {
        0.0D
    };
    private transient double phipi[] =
    {
        0.0D
    };
    private transient double sgnorm[] =
    {
        0.0D
    };
    private transient double fpnrmp[] =
    {
        0.0D
    };
    private transient int nsing[] =
    {
        0
    };
    private transient int nmaxs[] =
    {
        0
    };
    private transient int icode;

    public NonlinLeastSquares(int i, int j)
    {
        IPARAM = new int[6];
        RPARAM = new double[7];
        double ad[] = new double[i];
        double ad1[] = new double[j];
        double ad2[] = new double[j];
        double d = 0.33333333333333331D;
        double d1 = 0.66666666666666663D;
        n = j;
        m = i;
        M = i;
        N = j;
        IPARAM[0] = 1;
        IPARAM[1] = (int) (-0.43429448190325182D * Math.log(2.2204460492503131E-016D) + 0.10000000000000001D);
        IPARAM[2] = 100;
        IPARAM[3] = 400;
        IPARAM[4] = 100;
        IPARAM[5] = 1;
        double d2 = 9.9999999999999995E-021D;
        RPARAM[0] = Math.pow(2.2204460492503131E-016D, d);
        RPARAM[1] = Math.pow(2.2204460492503131E-016D, d1);
        RPARAM[2] = Math.max(d2, Math.pow(2.2204460492503131E-016D, d1));
        RPARAM[3] = Math.max(d2 * d2, 4.9303806576313238E-032D);
        RPARAM[4] = 2.2204460492503131E-014D;
        RPARAM[5] = -999D;
        RPARAM[6] = -999D;
        FSCALE = ad;
        XSCALE = ad1;
        XGUESS = ad2;
        for (int k = 0; k < i; k++)
        {
            FSCALE[k] = 1.0D;
        }

        if (IPARAM[5] == 1)
        {
            for (int l = 0; l < j; l++)
            {
                XSCALE[l] = 1.0D;
            }

        }
        NGOOD = IPARAM[1];
        MXITER = IPARAM[2];
        MAXFCN = IPARAM[3];
        MAXJAC = IPARAM[4];
        GRADTL = RPARAM[0];
        STEPTL = RPARAM[1];
        RFTOL = RPARAM[2];
        AFTOL = RPARAM[3];
        FALSTL = RPARAM[4];
        STEPMX = RPARAM[5];
        DELTA = RPARAM[6];
        INFO = 0;
    }

    public double[] solve(Function function) throws TooManyIterationsException
    {
        int ai[] = new int[N];
        double ad[] = new double[N];
        double ad1[] = new double[N];
        double ad2[] = new double[N];
        double ad3[] = new double[2 * M];
        double ad4[] = new double[N];
        double ad5[] = new double[M];
        double ad6[] = new double[M];
        double ad7[][] = new double[N][M];
        double ad8[] = new double[N];
        double ad9[] = new double[N];
        double ad10[] = new double[M];
        double ad11[] = new double[M];
        double ad12[] = new double[N];
        double ad13[] = new double[1];
        double ad14[] = new double[1];
        double ad15[] = new double[N];
        double ad16[] = new double[1];
        F = function;
        if (M <= 0)
        {
            Object aobj[] =
            {
                    "M", new Integer(M)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalRuleException("solve", "Parameter \"M\" = " + M + " is negative. Must be positive.");
        }
        else if (N <= 0)
        {
            Object aobj1[] =
            {
                    "N", new Integer(N)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj1);
            throw new ConditionalRuleException("solve", "Parameter \"N\" = " + N + " is negative. Must be positive.");
        }
        else if (M < N)
        {
            Object aobj2[] =
            {
                    new Integer(N), new Integer(M)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NumberOfVariables", aobj2);
            throw new ConditionalRuleException("solve", "Parameter \"M\" = " + M
                    + " must be equal or greater than \"N\" = " + N);
        }
        else
        {
            System.arraycopy(XGUESS, 0, ad8, 0, n);
            if (STEPMX == -999D)
            {
                double d1 = 0.0D;
                for (int i = 0; i < n; i++)
                {
                    d1 += XSCALE[i] * ad8[i] * (XSCALE[i] * ad8[i]);
                }

                d1 = Math.sqrt(d1);
                double d2 = BLAS.nrm2(n, XSCALE, 0);
                STEPMX = 1000D * Math.max(d1, d2);
            }
            ad16[0] = 0.0D;
            ad13[0] = 0.0D;
            double d = NGOOD;
            double d3 = Math.max(2.2204460492503131E-016D, Math.pow(10D, -d));
            int j2 = 0;
            int k2 = 0;
            int l2 = 0;
            icode = 0;
            function.f(ad8, ad10);
            ad14[0] = 0.5D * BLAS.nrm2(m, ad10, 0) * BLAS.nrm2(m, ad10, 0);
            k2++;
            if (function instanceof Jacobian)
            {
                ((Jacobian) function).jacobian(ad8, ad7);
            }
            else
            {
                computeJacobian(m, n, ad8, ad10, d3, ad7, ad6);
            }
            l2++;
            boolean flag = false;
            for (int k1 = 0; k1 < n; k1++)
            {
                for (int j = 0; j < m; j++)
                {
                    ad[k1] = ad[k1] + ad7[k1][j] * FSCALE[j] * FSCALE[j] * ad10[j];
                }

            }

            du6lsf(ad8, ad9, ad10, ad14, ad, FSCALE, j2, k2, l2, false, flag);
            if (icode != -999)
            {
                do
                {
                    j2++;
                    for (int l1 = 0; l1 < n; l1++)
                    {
                        for (int k = 0; k < m; k++)
                        {
                            ad7[l1][k] = ad7[l1][k] * FSCALE[k];
                        }

                    }

                    for (int l = 0; l < n; l++)
                    {
                        ai[l] = 0;
                    }

                    computeQR(ad7, true, ai, ad1, ad2, ad3);
                    if (IPARAM[5] == 1)
                    {
                        du8lsf(j2, n, ad2);
                    }
                    du11nf(m, FSCALE, 1, ad10, ad6);
                    du10sf(m, n, ad7, ad1, ad6, ad6);
                    icode = 6;
                    first = true;
                    while (icode >= 4)
                    {
                        du7lsf(n, ad, ad7, ai, ad6, ad16, first, ad9, ad15, gauss, ad1, ad2, ad3);
                        du9lsf(m, n, ad8, ad14, ad, ad7, ai, ad9, ad4, ad5, ad12, ad10, ad11, ad13, k2);
                    }
                    if (function instanceof Jacobian)
                    {
                        ((Jacobian) function).jacobian(ad12, ad7);
                    }
                    else
                    {
                        computeJacobian(m, n, ad12, ad11, d3, ad7, ad6);
                    }
                    l2++;
                    for (int i1 = 0; i1 < n; i1++)
                    {
                        ad[i1] = 0.0D;
                    }

                    for (int i2 = 0; i2 < n; i2++)
                    {
                        for (int j1 = 0; j1 < m; j1++)
                        {
                            ad[i2] = ad[i2] + ad7[i2][j1] * FSCALE[j1] * FSCALE[j1] * ad11[j1];
                        }

                    }

                    du6lsf(ad12, ad9, ad11, ad13, ad, FSCALE, j2, k2, l2, false, flag);
                    if (icode == -999)
                    {
                        break;
                    }
                    System.arraycopy(ad12, 0, ad8, 0, n);
                    System.arraycopy(ad11, 0, ad10, 0, m);
                    ad14[0] = ad13[0];
                }
                while (true);
                System.arraycopy(ad12, 0, ad8, 0, n);
                System.arraycopy(ad11, 0, ad10, 0, m);
                ad14[0] = ad13[0];
            }
            IPARAM[2] = j2;
            IPARAM[3] = k2;
            IPARAM[4] = l2;
        }
        return ad8;
    }

    private void computeJacobian(int i, int j, double ad[], double ad1[], double d, double ad2[][], double ad3[])
    {
        double d2 = Math.sqrt(Math.max(d, 2.2204460492503131E-016D));
        for (int k = 0; k < j; k++)
        {
            double d3 = d2 * Math.max(Math.abs(ad[k]), 1.0D / XSCALE[k]);
            if (ad[k] < 0.0D)
            {
                d3 = -d3;
            }
            double d1 = ad[k];
            ad[k] = d1 + d3;
            F.f(ad, ad3);
            ad[k] = d1;
            for (int l = 0; l < i; l++)
            {
                ad2[k][l] = (ad3[l] - ad1[l]) / d3;
            }

        }

    }

    private void computeQR(double ad[][], boolean flag, int ai[], double ad1[], double ad2[], double ad3[])
    {
        int i = n;
        int j = i;
        int k = m;
        int l = Math.min(k, j);
        int i1 = 1;
        int j1 = 0;
        for (int k1 = 0; k1 < j; k1++)
        {
            boolean flag1 = ai[k1] > 0;
            boolean flag2 = ai[k1] < 0;
            ai[k1] = k1 + 1;
            if (flag2)
            {
                ai[k1] = -k1 - 1;
            }
            if (!flag1)
            {
                continue;
            }
            if (k1 + 1 != i1)
            {
                swap(k, ad[i1 - 1], ad[k1 - 1]);
            }
            ai[k1] = ai[i1 - 1];
            ai[i1 - 1] = k1 + 1;
            i1++;
        }

        j1 = j;
        for (int l1 = j - 1; l1 >= 0; l1--)
        {
            if (ai[l1] >= 0)
            {
                continue;
            }
            ai[l1] = -ai[l1];
            if (l1 + 1 != j1)
            {
                swap(k, ad[j1 - 1], ad[l1]);
                int l2 = ai[j1 - 1];
                ai[j1 - 1] = ai[l1];
                ai[l1] = l2;
            }
            j1--;
        }

        if (j1 >= i1)
        {
            for (int i2 = i1 - 1; i2 < j1; i2++)
            {
                ad1[i2] = BLAS.nrm2(k, ad[i2], 0);
                ad3[(i2 + k) - 1] = ad1[i2];
                ad2[i2] = ad1[i2];
            }

        }
        l = Math.min(k, j);
        for (int j2 = 1; j2 <= l; j2++)
        {
            if (j2 >= i1 && j2 < j1)
            {
                double d1 = 0.0D;
                int i3 = j2;
                for (int l3 = j2; l3 <= j1; l3++)
                {
                    if (ad1[l3 - 1] > d1)
                    {
                        d1 = ad1[l3 - 1];
                        i3 = l3 - 1;
                    }
                }

                if (i3 != j2 - 1)
                {
                    swap(k, ad[j2 - 1], ad[i3]);
                    ad1[i3] = ad1[j2 - 1];
                    ad3[(i3 + k) - 1] = ad3[(j2 + k) - 2];
                    int i4 = ai[i3];
                    ai[i3] = ai[j2 - 1];
                    ai[j2 - 1] = i4;
                }
            }
            ad1[j2 - 1] = 0.0D;
            if (j2 == k)
            {
                continue;
            }
            double d2 = BLAS.nrm2((k - j2) + 1, ad[j2 - 1], j2 - 1);
            if (d2 == 0.0D)
            {
                continue;
            }
            if (ad[j2 - 1][j2 - 1] != 0.0D)
            {
                d2 = Math.abs(d2);
                if (ad[j2 - 1][j2 - 1] < 0.0D)
                {
                    d2 = -d2;
                }
            }
            for (int j3 = 0; j3 < (k - j2) + 1; j3++)
            {
                ad[j2 - 1][(j2 - 1) + j3] /= d2;
            }

            ad[j2 - 1][j2 - 1]++;
            if (j >= j2 + 1)
            {
                BLAS.gemv('T', (k - j2) + 1, i - j2, -1D / ad[j2 - 1][j2 - 1], ad, j2, j2 - 1, ad[j2 - 1], j2 - 1,
                        0.0D, ad3, 0);
                BLAS.ger((k - j2) + 1, i - j2, 1.0D, ad[j2 - 1], j2 - 1, ad3, 0, ad, j2, j2 - 1);
                for (int k3 = j2 + 1; k3 <= j; k3++)
                {
                    if (k3 < i1 || k3 > j1 || ad1[k3 - 1] == 0.0D)
                    {
                        continue;
                    }
                    double d3 = 1.0D - (Math.abs(ad[k3 - 1][j2 - 1]) / ad1[k3 - 1])
                            * (Math.abs(ad[k3 - 1][j2 - 1]) / ad1[k3 - 1]);
                    d3 = Math.max(d3, 0.0D);
                    double d = d3;
                    d3 = 1.0D + 0.050000000000000003D * d3 * (ad1[k3 - 1] / ad3[(k3 + k) - 2])
                            * (ad1[k3 - 1] / ad3[(k3 + k) - 2]);
                    if (d3 == 1.0D)
                    {
                        ad1[k3 - 1] = BLAS.nrm2(k - j2, ad[k3 - 1], j2);
                        ad3[(k3 + k) - 2] = ad1[k3 - 1];
                    }
                    else
                    {
                        ad1[k3 - 1] = ad1[k3 - 1] * Math.sqrt(d);
                    }
                }

            }
            ad1[j2 - 1] = ad[j2 - 1][j2 - 1];
            ad[j2 - 1][j2 - 1] = -d2;
        }

        for (int k2 = 0; k2 < j; k2++)
        {
            ai[k2] = ai[k2] - 1;
        }

    }

    private static final void swap(int i, double ad[], double ad1[])
    {
        for (int j = 0; j < i; j++)
        {
            double d = ad[j];
            ad[j] = ad1[j];
            ad1[j] = d;
        }

    }

    private final void du6lsf(double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], int i,
            int j, int k, boolean flag, boolean flag1) throws TooManyIterationsException
    {
        int j1 = 0;
        if (ad3[0] <= AFTOL)
        {
            icode = -999;
        }
        else
        {
            double d = 0.0D;
            for (int l = 0; l < N; l++)
            {
                double d2;
                if (ad3[0] <= 2.2250738585072014E-308D)
                {
                    d2 = Math.abs(ad4[l]) * Math.max(Math.abs(ad[l]), 1.0D / XSCALE[l]);
                }
                else
                {
                    d2 = (Math.abs(ad4[l]) * Math.max(Math.abs(ad[l]), 1.0D / XSCALE[l])) / ad3[0];
                }
                d = Math.max(d2, d);
            }

            if (d <= GRADTL)
            {
                icode = -999;
            }
            else if (i == 0)
            {
                nmaxs[j1] = 0;
            }
            else
            {
                double d1 = 0.0D;
                for (int i1 = 0; i1 < n; i1++)
                {
                    double d3 = Math.abs(ad1[i1]) / Math.max(Math.abs(ad[i1]), 1.0D / XSCALE[i1]);
                    d1 = Math.max(d3, d1);
                }

                if (d1 <= STEPTL)
                {
                    icode = -999;
                    INFO = 1;
                }
                else if (icode == 2)
                {
                    INFO = 2;
                    Object aobj[] =
                    {
                        new Double(RFTOL)
                    };
                }
                else if (icode == 3)
                {
                    INFO = 3;
                }
                else
                {
                    if (i > MXITER)
                    {
                        Object aobj1[] =
                        {
                            new Integer(i)
                        };
                        throw new TooManyIterationsException(i + "");
                    }
                    if (flag1)
                    {
                        nmaxs[j1] = nmaxs[j1] + 1;
                        if (nmaxs[j1] == 5)
                        {
                            INFO = 4;
                        }
                    }
                }
            }
        }
    }

    private final void du7lsf(int i, double ad[], double ad1[][], int ai[], double ad2[], double ad3[], boolean flag,
            double ad4[], double ad5[], boolean flag1, double ad6[], double ad7[], double ad8[])
    {
        int ai1[] = new int[1];
        int l = 0;
        if (DELTA == -999D)
        {
            ad3[0] = 0.0D;
            du11nf(i, XSCALE, -1, ad, ad7);
            double d1 = BLAS.nrm2(i, ad7, 0) * BLAS.nrm2(i, ad7, 0);
            double d4 = 0.0D;
            for (int j = 0; j < i; j++)
            {
                double d6 = 0.0D;
                for (int i1 = j; i1 < i; i1++)
                {
                    int k2 = ai[i1];
                    d6 += (ad1[i1][j] * ad[k2]) / (XSCALE[k2] * XSCALE[k2]);
                }

                d4 += d6 * d6;
            }

            if (d4 <= 2.2250738585072014E-308D)
            {
                DELTA = d1 * Math.sqrt(d1);
            }
            else
            {
                DELTA = (d1 * Math.sqrt(d1)) / d4;
            }
            DELTA = Math.min(DELTA, STEPMX);
        }
        if (flag)
        {
            ai1[l] = i;
            double d9 = Math.abs(ad1[0][0]) * 1.1102230246251999E-016D;
            for (int j1 = 0; j1 < i; j1++)
            {
                if (Math.abs(ad1[j1][j1]) < d9 && ai1[l] == i)
                {
                    ai1[l] = j1;
                    ad1[j1][j1] = 0.0D;
                }
                if (ai1[l] < i)
                {
                    ad7[j1] = 0.0D;
                }
            }

            du12sf(ai1[l], ad1, ad2, ad7);
            for (int k1 = 0; k1 < i; k1++)
            {
                ad5[ai[k1]] = -ad7[k1];
            }

            du11nf(i, XSCALE, 1, ad5, ad7);
            gnleng[l] = BLAS.nrm2(i, ad7, 0);
            du11nf(i, XSCALE, -1, ad, ad7);
            sgnorm[l] = BLAS.nrm2(i, ad7, 0);
        }
        double d8 = 1.5D;
        double d = 0.75D;
        if (gnleng[l] <= d8 * DELTA)
        {
            flag1 = true;
            System.arraycopy(ad5, 0, ad4, 0, i);
            ad3[0] = 0.0D;
            DELTA = Math.min(DELTA, gnleng[l]);
        }
        else
        {
            flag1 = false;
            if (ad3[0] > 0.0D)
            {
                ad3[0] = ad3[0] - ((phi[l] + deltap[l]) / DELTA) * (((deltap[l] - DELTA) + phi[l]) / phip[l]);
            }
            phi[l] = gnleng[l] - DELTA;
            double d2;
            if (ai1[l] == i)
            {
                if (flag)
                {
                    flag = false;
                    for (int l1 = 0; l1 < i; l1++)
                    {
                        int l2 = ai[l1];
                        ad7[l1] = XSCALE[l2] * XSCALE[l2] * ad5[l2];
                    }

                    du12sf(i, ad1, ad7, ad7);
                    phipi[l] = (-BLAS.nrm2(i, ad7, 0) * BLAS.nrm2(i, ad7, 0)) / gnleng[l];
                }
                d2 = -phi[l] / phipi[l];
            }
            else
            {
                flag = false;
                d2 = 0.0D;
            }
            double d3 = sgnorm[l] / DELTA;
            boolean flag2 = false;
            do
            {
                if (ad3[0] < d2 || ad3[0] > d3)
                {
                    ad3[0] = Math.max(Math.sqrt(d2 * d3), 0.001D * d3);
                }
                double d7 = Math.sqrt(ad3[0]);
                for (int k = 0; k < i; k++)
                {
                    ad7[k] = d7 * XSCALE[k];
                }

                du11sf(i, ad1, ai, ad7, ad2, ad4, ad6, ad8);
                BLAS.scal(i, -1D, ad4, 0);
                du11nf(i, XSCALE, 1, ad4, ad8);
                double d5 = BLAS.nrm2(i, ad8, 0);
                phi[l] = d5 - DELTA;
                for (int i2 = 0; i2 < i; i2++)
                {
                    int i3 = ai[i2];
                    ad7[i2] = XSCALE[i3] * ad8[i3];
                }

                for (int j2 = 0; j2 < i; j2++)
                {
                    if (Math.abs(ad6[j2]) >= 2.2250738585072014E-308D)
                    {
                        ad7[j2] = ad7[j2] / ad6[j2];
                    }
                    if (j2 < i - 1)
                    {
                        BLAS.axpy(i - j2 - 1, -ad7[j2], ad1[j2], j2 + 1, ad7, j2 + 1);
                    }
                }

                phip[l] = (-BLAS.nrm2(i, ad7, 0) * BLAS.nrm2(i, ad7, 0)) / d5;
                if (d5 >= d * DELTA && d5 <= d8 * DELTA || d3 - d2 <= 2.2204460492503131E-015D)
                {
                    flag2 = true;
                }
                else
                {
                    d2 = Math.max(d2, ad3[0] - phi[l] / phip[l]);
                    if (phi[l] < 0.0D)
                    {
                        d3 = ad3[0];
                    }
                    ad3[0] = ad3[0] - (d5 / DELTA) * (phi[l] / phip[l]);
                }
            }
            while (!flag2);
        }
        deltap[l] = DELTA;
    }

    private void du8lsf(int i, int j, double ad[])
    {
        if (i == 1)
        {
            System.arraycopy(ad, 0, XSCALE, 0, j);
        }
        else
        {
            for (int k = 0; k < j; k++)
            {
                XSCALE[k] = Math.max(XSCALE[k], ad[k]);
            }

        }
        for (int l = 0; l < j; l++)
        {
            if (XSCALE[l] <= 9.9999999999999995E-007D)
            {
                XSCALE[l] = 1.0D;
            }
        }

    }

    private void du9lsf(int i, int j, double ad[], double ad1[], double ad2[], double ad3[][], int ai[], double ad4[],
            double ad5[], double ad6[], double ad7[], double ad8[], double ad9[], double ad10[], int k)
    {
        double d = 0.0001D;
        int l1 = 0;
        mxtake = false;
        du11nf(j, XSCALE, 1, ad4, ad7);
        double d6 = BLAS.nrm2(j, ad7, 0);
        for (int l = 0; l < j; l++)
        {
            ad7[l] = ad[l] + ad4[l];
        }

        F.f(ad7, ad9);
        k++;
        ad10[0] = 0.5D * BLAS.nrm2(i, ad9, 0) * BLAS.nrm2(i, ad9, 0);
        double d1 = ad10[0] - ad1[0];
        double d5 = BLAS.dot(j, ad2, 0, ad4, 0);
        if (icode != 5)
        {
            fpnrmp[l1] = 0.0D;
        }
        if (icode == 5 && (ad10[0] >= fpnrmp[l1] || d1 > d * d5))
        {
            icode = 0;
            System.arraycopy(ad5, 0, ad7, 0, j);
            System.arraycopy(ad6, 0, ad9, 0, i);
            ad10[0] = fpnrmp[l1];
            DELTA = 0.5D * DELTA;
        }
        else if (d1 >= d * d5)
        {
            double d3 = 0.0D;
            for (int i1 = 0; i1 < j; i1++)
            {
                double d7 = Math.abs(ad4[i1]) / Math.max(Math.abs(ad7[i1]), 1.0D / XSCALE[i1]);
                d3 = Math.max(d3, d7);
            }

            if (d3 < STEPTL)
            {
                icode = 1;
                System.arraycopy(ad, 0, ad7, 0, j);
                System.arraycopy(ad8, 0, ad9, 0, i);
            }
            else
            {
                icode = 4;
                double d8;
                if (Math.abs(d1 - d5) > 2.2250738585072014E-308D)
                {
                    d8 = (-d5 * d6) / (2D * (d1 - d5));
                }
                else
                {
                    d8 = (-d5 * d6) / 2D;
                }
                if (d8 < 0.10000000000000001D * DELTA)
                {
                    DELTA = 0.10000000000000001D * DELTA;
                }
                else if (d8 > 0.5D * DELTA)
                {
                    DELTA = 0.5D * DELTA;
                }
                else
                {
                    DELTA = d8;
                }
            }
        }
        else
        {
            double d2 = d5;
            for (int j1 = 0; j1 < j; j1++)
            {
                int i2 = ai[j1];
                double d9 = 0.0D;
                for (int j2 = j1; j2 < j; j2++)
                {
                    d9 += ad3[j2][j1] * ad4[i2];
                }

                d2 += 0.5D * d9 * d9;
            }

            boolean flag = Math.abs(d2 - d1) <= 0.10000000000000001D * Math.abs(d1);
            if (icode != 4 && (flag || d1 <= d5) && !gauss && DELTA <= 0.98999999999999999D * STEPMX)
            {
                icode = 5;
                System.arraycopy(ad7, 0, ad5, 0, j);
                System.arraycopy(ad9, 0, ad6, 0, i);
                fpnrmp[l1] = ad10[0];
                DELTA = Math.min(2D * DELTA, STEPMX);
            }
            else
            {
                icode = 0;
                if (d6 > 0.98999999999999999D * STEPMX)
                {
                    mxtake = true;
                }
                if (d1 >= 0.10000000000000001D * d2)
                {
                    DELTA = 0.5D * DELTA;
                }
                else if (d1 <= 0.75D * d2)
                {
                    DELTA = Math.min(2D * DELTA, STEPMX);
                }
            }
            if (d1 > 2D * d2)
            {
                double d4 = 0.0D;
                for (int k1 = 0; k1 < j; k1++)
                {
                    double d10 = Math.abs(ad4[k1]) / Math.max(Math.abs(ad7[k1]), 1.0D / XSCALE[k1]);
                    d4 = Math.max(d4, d10);
                }

                if (d4 < FALSTL)
                {
                    icode = 3;
                }
            }
            else if (Math.abs(d1) <= RFTOL * Math.abs(ad1[0]) && Math.abs(d2) <= RFTOL * Math.abs(ad1[0]))
            {
                icode = 2;
            }
        }
    }

    private void du10sf(int i, int j, double ad[][], double ad1[], double ad2[], double ad3[])
    {
        for (int k = 0; k < j; k++)
        {
            if (ad1[k] != 0.0D)
            {
                double d = ad1[k] * ad2[k];
                if (k < i)
                {
                    d += BLAS.dot(i - k - 1, ad[k], k + 1, ad2, k + 1);
                }
                double d1 = -d / ad1[k];
                ad2[k] = ad2[k] + ad1[k] * d1;
                if (k < i)
                {
                    BLAS.axpy(i - k - 1, d1, ad[k], k + 1, ad2, k + 1);
                }
            }
            ad3[k] = ad2[k];
        }

    }

    private void du11sf(int i, double ad[][], int ai[], double ad1[], double ad2[], double ad3[], double ad4[],
            double ad5[])
    {
        for (int j = 0; j < i; j++)
        {
            for (int i1 = j; i1 < i; i1++)
            {
                ad[j][i1] = ad[i1][j];
            }

            ad3[j] = ad[j][j];
        }

        System.arraycopy(ad2, 0, ad5, 0, i);
        for (int j1 = 0; j1 < i; j1++)
        {
            int j3 = ai[j1];
            if (ad1[j3] != 0.0D)
            {
                for (int k = j1; k < i; k++)
                {
                    ad4[k] = 0.0D;
                }

                ad4[j1] = ad1[j3];
                double d2 = 0.0D;
                for (int k2 = j1; k2 < i; k2++)
                {
                    if (ad4[k2] == 0.0D)
                    {
                        continue;
                    }
                    double d;
                    double d3;
                    if (Math.abs(ad[k2][k2]) < Math.abs(ad4[k2]))
                    {
                        double d1 = ad[k2][k2] / ad4[k2];
                        d3 = 0.5D / Math.sqrt(0.25D + 0.25D * d1 * d1);
                        d = d3 * d1;
                    }
                    else
                    {
                        double d5 = ad4[k2] / ad[k2][k2];
                        d = 0.5D / Math.sqrt(0.25D + 0.25D * d5 * d5);
                        d3 = d * d5;
                    }
                    ad[k2][k2] = d * ad[k2][k2] + d3 * ad4[k2];
                    double d6 = d * ad5[k2] + d3 * d2;
                    d2 = -d3 * ad5[k2] + d * d2;
                    ad5[k2] = d6;
                    int i3 = k2 + 1;
                    if (i < i3)
                    {
                        continue;
                    }
                    for (int l = i3; l < i; l++)
                    {
                        double d7 = d * ad[k2][l] + d3 * ad4[l];
                        ad4[l] = -d3 * ad[k2][l] + d * ad4[l];
                        ad[k2][l] = d7;
                    }

                }

            }
            ad4[j1] = ad[j1][j1];
            ad[j1][j1] = ad3[j1];
        }

        int l3 = i;
        for (int k1 = 0; k1 < i; k1++)
        {
            if (ad4[k1] == 0.0D && l3 == i)
            {
                l3 = k1;
            }
            if (l3 < i)
            {
                ad5[k1] = 0.0D;
            }
        }

        if (l3 >= 1)
        {
            for (int l2 = 0; l2 <= l3 - 1; l2++)
            {
                int l1 = l3 - l2 - 1;
                double d4 = 0.0D;
                int j2 = l1 + 1;
                if (l3 > j2)
                {
                    d4 = BLAS.dot(l3 - l1 - 1, ad[l1], j2, ad5, j2);
                }
                ad5[l1] = (ad5[l1] - d4) / ad4[l1];
            }

        }
        for (int i2 = 0; i2 < i; i2++)
        {
            int k3 = ai[i2];
            ad3[k3] = ad5[i2];
        }

    }

    private void du12sf(int i, double ad[][], double ad1[], double ad2[])
    {
        ad2[i - 1] = ad1[i - 1] / ad[i - 1][i - 1];
        for (int j = i - 2; j >= 0; j--)
        {
            double d = 0.0D;
            for (int k = 0; k < i - j - 1; k++)
            {
                d += ad[j + 1 + k][j] * ad2[j + 1 + k];
            }

            ad2[j] = (ad1[j] - d) / ad[j][j];
        }

    }

    private void du11nf(int i, double ad[], int j, double ad1[], double ad2[])
    {
        if (j < 0)
        {
            if (j == -1)
            {
                for (int k = 0; k < i; k++)
                {
                    ad2[k] = ad1[k] / ad[k];
                }

            }
            else
            {
                for (int l = 0; l < i; l++)
                {
                    ad2[l] = ad1[l] / Math.pow(ad[l], -j);
                }

            }
        }
        else if (j == 1)
        {
            for (int i1 = 0; i1 < i; i1++)
            {
                ad2[i1] = ad1[i1] * ad[i1];
            }

        }
        else
        {
            for (int j1 = 0; j1 < i; j1++)
            {
                ad2[j1] = Math.pow(ad[j1], j) * ad1[j1];
            }

        }
    }

    public void setGuess(double ad[])
    {
        XGUESS = (double[]) ad.clone();
    }

    public void setXscale(double ad[])
    {
        for (int i = 0; i < ad.length; i++)
        {
            if (ad[i] <= 0.0D)
            {
                Object aobj[] =
                {
                    ""
                };
                // Messages.throwIllegalArgumentException("com.imsl.math",
                // "XscaleNotPositive", aobj);
                throw new ConditionalRuleException("setXscale", "Element ad[" + i + "] = " + ad[i]
                        + ", is negative. Must be all positive.");
            }
        }

        XSCALE = (double[]) ad.clone();
    }

    public void setFscale(double ad[])
    {
        for (int i = 0; i < ad.length; i++)
        {
            if (ad[i] <= 0.0D)
            {
                Object aobj[] =
                {
                    ""
                };
                // Messages.throwIllegalArgumentException("com.imsl.math",
                // "FscaleNotPositive", aobj);
                throw new ConditionalRuleException("setFscale", "Element ad[" + i + "] = " + ad[i]
                        + ", is negative. Must be all positive.");
            }
        }

        FSCALE = (double[]) ad.clone();
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
            throw new ConditionalRuleException("setMaximumStepsize", "Parameter d = " + d
                    + ", is negative. Must be positive.");
        }
        else
        {
            STEPMX = d;
        }
    }

    public void setDigits(int i)
    {
        if (i <= 0)
        {
            Object aobj[] =
            {
                    "NGOOD", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalRuleException("setDigits", "Parameter i = " + i + ", is negative. Must be positive.");
        }
        else
        {
            NGOOD = i;
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
            throw new ConditionalRuleException("setMaxIterations", "Parameter i = " + i
                    + ", is negative. Must be positive.");
        }
        else
        {
            MXITER = i;
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
            throw new ConditionalRuleException("setGradientTolerance", "Parameter d = " + d
                    + ", is negative. Must be positive.");
        }
        else
        {
            GRADTL = d;
        }
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
            throw new ConditionalRuleException("setStepTolerance", "Parameter d = " + d
                    + ", is negative. Must be positive.");
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
            throw new ConditionalRuleException("setRelativeTolerance", "Parameter d = " + d
                    + ", is negative. Must be positive.");
        }
        else
        {
            RFTOL = d;
        }
    }

    public void setAbsoluteTolerance(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "AbsoluteTolerance", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalRuleException("setAbsoluteTolerance", "Parameter d = " + d
                    + ", is negative. Must be positive.");
        }
        else
        {
            AFTOL = d;
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
            throw new ConditionalRuleException("setFalseConvergenceTolerance", "Parameter d = " + d
                    + ", is negative. Must be positive.");
        }
        else
        {
            FALSTL = d;
        }
    }

    public void setInitialTrustRegion(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "InitialTrustRegion", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalRuleException("setInitialTrustRegion", "Parameter d = " + d
                    + ", is negative. Must be positive.");
        }
        else
        {
            DELTA = d;
        }
    }

    public int getErrorStatus()
    {
        return INFO;
    }
}