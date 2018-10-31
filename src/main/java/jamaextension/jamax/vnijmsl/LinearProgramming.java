/*
 * LinearProgramming.java
 *
 * Created on 31 March 2007, 16:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

import java.io.Serializable;

import jamaextension.jamax.ConditionalException;

// Referenced classes of package com.imsl.math:
//            SingularMatrixException, Matrix, BLAS, LU

public class LinearProgramming implements Serializable, Cloneable
{

    private class extLU extends LU
    {

        static final long serialVersionUID = 0xe2399ad9447555L;

        public double[][] getFactor()
        {
            return factor;
        }

        public int[] getPivot()
        {
            return ipvt;
        }

        public extLU(double ad[][]) throws SingularMatrixException
        {
            super(ad);
        }
    }

    public static class ProblemUnboundedException extends NumericDifficultyException
    {

        private static final long serialVersionUID = 0xc2ce4dae0c19fcc3L;

        public ProblemUnboundedException(String s)
        {
            super(s);
        }

        public ProblemUnboundedException()
        {
            super("LinearProgramming.ProblemUnbounded", null);
        }
    }

    public static class ProblemInfeasibleException extends NumericDifficultyException
    {

        private static final long serialVersionUID = 0xe3afac185ec92e12L;

        public ProblemInfeasibleException(String s)
        {
            super(s);
        }

        public ProblemInfeasibleException()
        {
            super("LinearProgramming.ProblemInfeasible", null);
        }
    }

    public static class NumericDifficultyException extends ConditionalException
    {

        static final long serialVersionUID = 0xadc7aac782b3b724L;

        public NumericDifficultyException(String s)
        {
            super(s);
        }

        public NumericDifficultyException(String s, Object aobj[])
        {
            super(s);
        }
    }

    public static class BoundsInconsistentException extends ConditionalException
    {

        static final long serialVersionUID = 0x649866928f970346L;

        public BoundsInconsistentException(String s)
        {
            super(s);
        }

        public BoundsInconsistentException(String s, Object aobj[])
        {
            super(s);
        }
    }

    /**
     * @deprecated Class WrongConstraintTypeException is deprecated
     */

    public static class WrongConstraintTypeException extends ConditionalException
    {

        static final long serialVersionUID = 0xcc4fa5b6c2f03d37L;

        public WrongConstraintTypeException(String s)
        {
            super(s);
        }

        public WrongConstraintTypeException(String s, Object aobj[])
        {
            super(s);
        }
    }

    static final long serialVersionUID = 0xc51673eba3b058dcL;

    private int max_itn;
    private int user_bu;
    private int user_xlb;
    private int user_xub;
    private int user_irtype;
    private double l_bu[];
    private double l_xlb[];
    private double l_xub[];
    private int l_irtype[];
    private double optimalValue;
    private double solution[];
    private double dualSolution[];
    private int l_m;
    private int l_n;
    private double l_a[];
    private double l_b[];
    private double l_c[];

    public LinearProgramming(double ad[][], double ad1[], double ad2[])
    {
        // Matrix.checkMatrix(ad);
        int adrow = ad.length;
        int adcol = ad[0].length;
        for (int j = 1; j < adrow; j++)
        {
            if (adcol != ad[j].length)
            {
                throw new IllegalArgumentException("LinearProgramming : Jagged matrix found for parameter 'ad'.");
            }
            else
            {
                adcol = ad[j].length;
            }
        }

        l_m = ad.length;
        if (l_m <= 0)
        {
            Object aobj[] =
            {
                    "a.length", new Integer(ad.length)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("LinearProgramming : Empty double array");
        }
        l_n = ad[0].length;
        if (l_n <= 0)
        {
            Object aobj1[] =
            {
                    "a[0].length", new Integer(ad[0].length)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj1);
            throw new ConditionalException("LinearProgramming : Empty double array");
        }
        l_a = new double[l_m * l_n];
        max_itn = 10000;
        user_bu = 0;
        l_bu = new double[l_m];
        user_xlb = 0;
        l_xlb = new double[l_n];
        user_xub = 0;
        l_xub = new double[l_n];
        user_irtype = 0;
        l_irtype = new int[l_m];
        for (int i = 0; i < ad.length; i++)
            BLAS.copy(ad[i].length, ad[i], 0, 1, l_a, i * ad[i].length, 1);

        if (ad1.length != l_m)
        {
            Object aobj2[] =
            {
                    "b", new Integer(ad1.length), new Integer(l_m)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj2);
            throw new ConditionalException("LinearProgramming : NotEqual -> b = " + ad1.length + ", and  l_m = " + l_m);
        }
        else
        {
            l_b = (double[]) ad1.clone();
        }
        if (ad2.length != l_n)
        {
            Object aobj3[] =
            {
                    "c", new Integer(ad2.length), new Integer(l_n)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj3);
            throw new ConditionalException("LinearProgramming : NotEqual -> c = " + ad2.length + ", and  l_n = " + l_n);
        }
        else
        {
            l_c = (double[]) ad2.clone();
        }
    }

    public Object clone()
    {
        Object obj = null;

        try
        {
            obj = super.clone();
        }
        catch (CloneNotSupportedException cnse)
        {
            cnse.printStackTrace();
        }
        return obj;
    }

    public final void solve() throws BoundsInconsistentException, NumericDifficultyException,
            ProblemInfeasibleException, ProblemUnboundedException, SingularMatrixException
    {
        int i = l_m;
        int j = l_n;
        double ad[] = l_a;
        double ad1[] = l_b;
        double ad2[] = l_c;
        double ad3[] = new double[j];
        byte byte0 = 5;
        int k = j;
        double ad4[] = new double[i];
        boolean flag = false;
        boolean flag1 = false;
        int l = 0;
        int ai[] = new int[i + j];
        int ai1[] = new int[i + j];
        boolean flag2 = false;
        double ad5[] = new double[i * (i + 28)];
        double ad6[] = new double[i * j];
        int ai2[] = new int[i * 27 + j];
        boolean flag3 = false;
        double ad7[] =
        {
            0.0D
        };
        if (user_irtype == 0)
            BLAS.set(i, 0, l_irtype, 0, 1);
        if (user_bu == 0)
            l_bu = (double[]) ad1.clone();
        if (user_xlb == 0)
            BLAS.set(j, 0.0D, l_xlb, 0, 1);
        if (user_xub == 0)
            BLAS.set(j, -1E+030D, l_xub, 0, 1);
        if (flag2)
            BLAS.scal(j, -1D, ad2, 0);
        for (int i1 = 0; i1 < j; i1++)
        {
            int j1 = i1 * i;
            BLAS.copy(i, ad, i1, k, ad6, j1, 1);
        }

        l_d2prs(i, j, ad6, i, ad1, l_bu, ad2, l_irtype, l_xlb, l_xub, l, ai, ai1, ad7, ad3, ad4, ad5, ai2, max_itn);
        if (flag2)
        {
            BLAS.scal(j, -1D, ad2, 0);
            BLAS.scal(i, -1D, ad4, 0);
            ad7[0] = -ad7[0];
        }
        dualSolution = (double[]) ad4.clone();
        optimalValue = ad7[0];
        solution = (double[]) ad3.clone();
    }

    private void l_d2prs(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], int ai[],
            double ad4[], double ad5[], int l, int ai1[], int ai2[], double ad6[], double ad7[], double ad8[],
            double ad9[], int ai3[], int i1) throws BoundsInconsistentException, NumericDifficultyException,
            SingularMatrixException
    {
        int ai4[] =
        {
            0
        };
        double d2 = Math.sqrt(2.2204460492503131E-016D);
        for (int l1 = 0; l1 < i; l1++)
        {
            if (ad2[l1] < ad1[l1] && ai[l1] == 3)
            {
                Object aobj[] =
                {
                        "b", "bl", "bu", new Integer(l1), new Double(ad1[l1]), new Double(ad2[l1])
                };
                throw new BoundsInconsistentException("LinearProgramming.BoundsInconsistent", aobj);
            }
            int k1 = ai[l1];
            if (k1 == 0)
            {
                ai3[j + l1] = 3;
                continue;
            }
            if (k1 == 1)
            {
                ai3[j + l1] = 2;
                continue;
            }
            if (k1 == 2)
            {
                ai3[j + l1] = 1;
                continue;
            }
            if (k1 == 3)
                ai3[j + l1] = 3;
        }

        double d = -1E+030D + d2;
        double d1 = 1E+030D - d2;
        for (int j1 = 0; j1 < j; j1++)
        {
            if (ad4[j1] >= d1 && ad5[j1] <= d)
            {
                ai3[j1] = 4;
                continue;
            }
            if (ad4[j1] >= d1)
            {
                ai3[j1] = 2;
                continue;
            }
            if (ad5[j1] <= d)
            {
                ai3[j1] = 1;
                continue;
            }
            ai3[j1] = 3;
            if (ad5[j1] < ad4[j1])
            {
                Object aobj1[] =
                {
                        "x", "xlb", "xub", new Integer(j1), new Double(ad4[j1]), new Double(ad5[j1])
                };
                throw new BoundsInconsistentException("LinearProgramming.BoundsInconsistent", aobj1);
            }
        }

        l_d3prs(i, j, ad, k, ad1, ad2, ad3, ad4, ad5, ad7, ad8, ad9, i * i, i * (i + 1), ai3, l, ai1, ai2, j + i, j + i
                + i, i * (i + 2), i * (i + 3), ai4, i1);
        if (ai4[0] == 0)
            ad6[0] = BLAS.dot(j, ad7, 0, ad3, 0);
        else if (ai4[0] == 2)
            ad6[0] = BLAS.dot(j, ad7, 0, ad3, 0);
    }

    private void l_d3prs(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            double ad5[], double ad6[], double ad7[], double ad8[], int l, int i1, int ai[], int j1, int ai1[],
            int ai2[], int k1, int l1, int i2, int j2, int ai3[], int k2) throws NumericDifficultyException,
            SingularMatrixException
    {
        int ai4[] =
        {
            0
        };
        int ai5[] =
        {
            0
        };
        int ai6[] =
        {
            0
        };
        int ai7[] =
        {
            0
        };
        int ai8[] =
        {
            0
        };
        int ai9[] =
        {
            0
        };
        int ai10[] =
        {
            0
        };
        int ai11[] =
        {
            0
        };
        double ad9[] =
        {
            0.0D
        };
        double ad10[] =
        {
            0.0D
        };
        double d1 = 2.2204460492503131E-016D;
        double d5 = Math.sqrt(d1);
        double d3 = d5;
        double d4 = d5;
        int l2 = 1;
        byte byte2 = 40;
        byte byte0 = 20;
        int l5 = 25 * i >= 1000 ? 1000 : 25 * i;
        ai3[0] = 0;
        byte byte1 = 1;
        int j6 = 0;
        boolean flag = false;
        int j5 = 0;
        boolean flag1 = false;
        if (j1 == 0)
        {
            l_d4prs(i, j, ad, k, ad4, ad5, l2, ad8, l, ai1, ai2, ai);
            flag1 = true;
        }
        else
        {
            l2 = 0;
            for (int i3 = 1; i3 <= j; i3++)
            {
                int k5 = i + i3;
                int k6 = ai1[k5 - 1];
                if (ai[k6 - 1] == 1)
                {
                    ai2[k6 - 1] = 1;
                    continue;
                }
                if (ai[k6 - 1] == 2)
                    ai2[k6 - 1] = 2;
            }

        }
        boolean flag2;
        boolean flag3;
        boolean flag4;
        do
        {
            if (!flag1)
            {
                for (int j3 = 1; j3 <= i; j3++)
                {
                    int l6 = ai1[j3 - 1];
                    if (l6 <= j)
                    {
                        BLAS.copy(i, ad, (l6 - 1) * k, 1, ad8, (j3 - 1) * i, 1);
                    }
                    else
                    {
                        BLAS.set(i, 0.0D, ad8, (j3 - 1) * i, 1);
                        ad8[(j3 - 1) * i + (l6 - j - 1)] = -1D;
                    }
                }

            }
            flag1 = false;
            double ad11[][] = new double[i][i];
            for (int k3 = 0; k3 < i; k3++)
                BLAS.copy(i, ad8, i * k3, 1, ad11[k3], 0, 1);

            extLU extlu = new extLU(ad11);
            int ai12[] = extlu.getPivot();
            ad11 = extlu.getFactor();
            for (int l3 = 0; l3 < i; l3++)
            {
                ai[k1 + l3] = ai12[l3] + 1;
                BLAS.copy(i, ad11[l3], 0, 1, ad8, i * l3, 1);
                ad8[i2 + l3] = 1.0D;
            }

            j5++;
            ai11[0] = 1;
            if (l2 == 0 || j6 != 0)
            {
                if (ai4[0] == 0)
                    l_d5prs(i, j, ad, k, ad4, ad5, ad1, ad2, ai, ai2, ad8, i1);
                else
                    ai4[0] = 0;
                l_d6prs(i, ad8, i, ai, k1, l1, j2, ai11[0], ad8, i1, 1, ad8, l, i2);
            }
            do
            {
                flag2 = false;
                flag3 = false;
                flag4 = false;
                j6++;
                l_d7prs(i, j, ad4, ad5, ad1, ad2, ai, ad8, i1, l, ai1, d5, ad10, ai8);
                if (ai8[0] != 0 && byte1 == 1)
                    byte1 = 2;
                else if (ai8[0] == 0 && byte1 == 2)
                {
                    byte1 = 1;
                    ai3[0] = 4;
                }
                if (byte1 == 2)
                {
                    for (int i4 = 1; i4 <= i; i4++)
                    {
                        int i7 = ai1[i4 - 1];
                        if (i7 > j)
                            ad8[(i1 + i4) - 1] = 0.0D;
                        else
                            ad8[(i1 + i4) - 1] = ad3[i7 - 1];
                    }

                }
                l_d6prs(i, ad8, i, ai, k1, l1, j2, ai11[0], ad8, i1, 2, ad7, 0, i2);
                double d2 = BLAS.asum(i, ad7, 0, 1) / (double) i;
                l_d8prs(i, j, ad, k, ad3, ad7, byte1, ai1, ad6);
                l_d9prs(i, j, ai9, ai1, ai, ai2, ad4, ad5, ad1, ad2, d1, d3, d2, ad6, ai7);
                if (ai7[0] == 0)
                {
                    flag2 = true;
                    break;
                }
                int j8 = ai1[ai9[0] - 1];
                if (j8 <= j)
                {
                    l_d6prs(i, ad8, i, ai, k1, l1, j2, ai11[0], ad, (j8 - 1) * k, 1, ad8, i1, i2);
                }
                else
                {
                    BLAS.set(i, 0.0D, ad8, i1, 1);
                    ad8[(i1 + j8) - j - 1] = -1D;
                    l_d6prs(i, ad8, i, ai, k1, l1, j2, ai11[0], ad8, i1, 1, ad8, i1, i2);
                }
                if (ai2[j8 - 1] == 2 || ai[j8 - 1] == 4 && ad6[ai9[0] - i - 1] > 0.0D)
                    BLAS.scal(i, -1D, ad8, i1);
                double d = BLAS.asum(i, ad8, i1, 1) / (double) i;
                l_d10rs(i, j, ai9[0], ai10, ai1, ai, ad9, d, d4, ad8, i1, ad4, ad5, ad1, ad2, l, ai6);
                if (ai6[0] == 0)
                {
                    flag4 = true;
                    break;
                }
                l_d11rs(i, j, ai9[0], ai10[0], ai1, ai, ai2, ad9[0], ad8, l, i1, ad4, ad5, ad1, ad2, d5, ad6, ai5);
                if (j6 > k2)
                {
                    flag3 = true;
                    break;
                }
                if (j6 % byte2 == 0)
                    break;
                if (j6 % byte0 == 0)
                {
                    l_d5prs(i, j, ad, k, ad4, ad5, ad1, ad2, ai, ai2, ad8, i1);
                    l_d12rs(i, j, ad, k, ai1, d5, d1, ad7, ad8, l, i1, ai4);
                    if (ai4[0] != 0)
                        break;
                }
                if (ai5[0] == 0)
                    continue;
                int i6 = (ai11[0] + i) - ai10[0];
                if (i6 > l5)
                    break;
                l_d13rs(i, ad8, i, ai, k1, i2, ai10[0], l1, j2, ai11, ad7);
            }
            while (true);
        }
        while (!flag2 && !flag3 && !flag4);
        if (flag2)
            if (byte1 == 2)
            {
                for (int j4 = 1; j4 <= i; j4++)
                {
                    int j7 = ai1[j4 - 1];
                    if (j7 <= j)
                        ad6[j7 - 1] = ad8[(l + j4) - 1];
                }

                for (int k4 = 1; k4 <= j; k4++)
                {
                    int k7 = ai1[(i + k4) - 1];
                    if (k7 <= j)
                        if (ai2[k7 - 1] == 1)
                            ad6[k7 - 1] = ad4[k7 - 1];
                        else if (ai2[k7 - 1] == 2)
                            ad6[k7 - 1] = ad5[k7 - 1];
                        else
                            ad6[k7 - 1] = 0.0D;
                }

            }
            else
            {
                ai3[0] = 3;
            }
        if (flag3)
        {
            ai3[0] = 2;
            if (byte1 == 2)
            {
                for (int l4 = 1; l4 <= i; l4++)
                {
                    int l7 = ai1[l4 - 1];
                    if (l7 <= j)
                        ad6[l7 - 1] = ad8[(l + l4) - 1];
                }

                for (int i5 = 1; i5 <= j; i5++)
                {
                    int i8 = ai1[(i + i5) - 1];
                    if (i8 <= j)
                        if (ai2[i8 - 1] == 1)
                            ad6[i8 - 1] = ad4[i8 - 1];
                        else if (ai2[i8 - 1] == 2)
                            ad6[i8 - 1] = ad5[i8 - 1];
                        else
                            ad6[i8 - 1] = 0.0D;
                }

            }
            else
            {
                ai3[0] = -2;
            }
        }
        if (flag4)
            if (byte1 == 2)
                ai3[0] = 1;
            else
                ai3[0] = 4;
        if (ai3[0] == 1)
            throw new ProblemUnboundedException();
        if (ai3[0] == 2)
        {
            // Warning.print(this, "com.imsl.math",
            // "LinearProgramming.TooManyIteration", null);
            System.out.println("LinearProgramming.TooManyIteration");
        }
        else
        {
            if (ai3[0] == 3)
                throw new ProblemInfeasibleException();
            if (ai3[0] == 4)
                throw new NumericDifficultyException("LinearProgramming.NumericDifficulty", null);
            if (ai3[0] == -2)
                // Warning.print(this, "com.imsl.math",
                // "LinearProgramming.TooManyIteration", null);
                System.out.println("LinearProgramming.TooManyIteration");
        }
    }

    private void l_d4prs(int i, int j, double ad[], int k, double ad1[], double ad2[], int l, double ad3[], int i1,
            int ai[], int ai1[], int ai2[])
    {
        if (l != 0)
        {
            BLAS.set(i * i, 0.0D, ad3, 0, 1);
            BLAS.set(i, -1D, ad3, 0, i + 1);
            for (int j1 = 1; j1 <= i; j1++)
            {
                int j2 = j + j1;
                ai[j1 - 1] = j2;
                ai1[j2 - 1] = -1;
            }

            for (int l1 = 1; l1 <= j; l1++)
            {
                int k2 = i + l1;
                ai[k2 - 1] = l1;
                if (ai2[l1 - 1] == 1)
                {
                    ai1[l1 - 1] = 1;
                    continue;
                }
                if (ai2[l1 - 1] == 2)
                {
                    ai1[l1 - 1] = 2;
                    continue;
                }
                if (ai2[l1 - 1] == 3)
                {
                    if (Math.abs(ad1[l1 - 1]) <= Math.abs(ad2[l1 - 1]))
                        ai1[l1 - 1] = 1;
                    else
                        ai1[l1 - 1] = 2;
                }
                else
                {
                    ai1[l1 - 1] = 3;
                }
            }

            for (int k1 = 1; k1 <= i; k1++)
            {
                double d = 0.0D;
                for (int i2 = 1; i2 <= j; i2++)
                {
                    if (ai1[i2 - 1] == 1)
                    {
                        d += ad[(i2 - 1) * k + (k1 - 1)] * ad1[i2 - 1];
                        continue;
                    }
                    if (ai1[i2 - 1] == 2)
                        d += ad[(i2 - 1) * k + (k1 - 1)] * ad2[i2 - 1];
                }

                ad3[(i1 + k1) - 1] = d;
            }

        }
    }

    private void l_d5prs(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            int ai[], int ai1[], double ad5[], int l)
    {
        double d = 0.0D;
        BLAS.set(i, 0.0D, ad5, l, 1);
        for (int i1 = 1; i1 <= j; i1++)
        {
            if (ai[i1 - 1] == 4 || ai1[i1 - 1] < 0)
                continue;
            if (ai1[i1 - 1] == 1)
                d = ad1[i1 - 1];
            else if (ai1[i1 - 1] == 2)
                d = ad2[i1 - 1];
            if (d != 0.0D)
                BLAS.axpy(i, -d, ad, (i1 - 1) * k, ad5, l);
        }

        for (int j1 = j + 1; j1 <= j + i; j1++)
        {
            if (ai[j1 - 1] == 4 || ai1[j1 - 1] < 0)
                continue;
            int k1 = j1 - j;
            if (ai1[j1 - 1] == 1)
                d = ad3[k1 - 1];
            else if (ai1[j1 - 1] == 2)
                d = ad4[k1 - 1];
            if (d != 0.0D)
                ad5[(l + k1) - 1] += d;
        }

    }

    private void l_d6prs(int i, double ad[], int j, int ai[], int k, int l, int i1, int j1, double ad1[], int k1,
            int l1, double ad2[], int i2, int j2)
    {
        if (l1 == 1)
        {
            l_d14rs(i, ad, j, ai, k, ad1, k1, l1, ad, j2);
            for (int k2 = 1; k2 <= j1 - 1; k2++)
            {
                int i3 = ai[(l + k2) - 1];
                if (i3 > 0)
                {
                    double d2 = ad[(j2 + i3) - 1];
                    ad[(j2 + i3) - 1] = ad[j2 + i3];
                    ad[j2 + i3] = d2;
                }
                double d = ad[(i1 + k2) - 1];
                if (d != 0.0D)
                {
                    int k3 = Math.abs(i3);
                    ad[j2 + k3] += ad[(j2 + k3) - 1] * d;
                }
            }

            BLAS.copy(i, ad, j2, 1, ad2, i2, 1);
            double ad3[] = new double[ad2.length - i2];
            BLAS.copy(ad2.length - i2, ad2, i2, 1, ad3, 0, 1);
            BLAS.trsv('u', 'n', 'n', i, ad, 0, j, ad3, 0, 1);
            BLAS.copy(ad2.length - i2, ad3, 0, 1, ad2, i2, 1);
        }
        else
        {
            BLAS.copy(i, ad1, k1, 1, ad2, i2, 1);
            double ad4[] = new double[ad2.length - i2];
            BLAS.copy(ad2.length - i2, ad2, i2, 1, ad4, 0, 1);
            BLAS.trsv('u', 't', 'n', i, ad, 0, j, ad4, 0, 1);
            BLAS.copy(ad2.length - i2, ad4, 0, 1, ad2, i2, 1);
            for (int l2 = j1 - 1; l2 >= 1; l2--)
            {
                double d1 = ad[(i1 + l2) - 1];
                int j3 = ai[(l + l2) - 1];
                if (d1 != 0.0D)
                {
                    int l3 = Math.abs(j3);
                    ad2[(i2 + l3) - 1] += ad2[i2 + l3] * d1;
                }
                if (j3 > 0)
                {
                    double d3 = ad2[(i2 + j3) - 1];
                    ad2[(i2 + j3) - 1] = ad2[i2 + j3];
                    ad2[i2 + j3] = d3;
                }
            }

            l_d14rs(i, ad, j, ai, k, ad2, i2, 2, ad2, i2);
        }
    }

    private void l_d7prs(int i, int j, double ad[], double ad1[], double ad2[], double ad3[], int ai[], double ad4[],
            int k, int l, int ai1[], double d, double ad5[], int ai2[])
    {
        ai2[0] = 1;
        ad5[0] = 0.0D;
        BLAS.set(i, 0.0D, ad4, k, 1);
        for (int i1 = 1; i1 <= i; i1++)
        {
            int j1 = ai1[i1 - 1];
            if (ai[j1 - 1] == 4)
                continue;
            double d7 = ad4[(l + i1) - 1];
            if (j1 > j)
            {
                int k1 = j1 - j;
                if (ai[j1 - 1] == 1)
                {
                    double d1 = ad2[k1 - 1] - d7;
                    if (d1 > d)
                    {
                        ad4[(k + i1) - 1] = -1D;
                        ai2[0] = 0;
                        ad5[0] += d1;
                    }
                    continue;
                }
                if (ai[j1 - 1] == 2)
                {
                    double d2 = d7 - ad3[k1 - 1];
                    if (d2 > d)
                    {
                        ad4[(k + i1) - 1] = 1.0D;
                        ai2[0] = 0;
                        ad5[0] += d2;
                    }
                    continue;
                }
                double d3 = ad2[k1 - 1] - d7;
                if (d3 > d)
                {
                    ad4[(k + i1) - 1] = -1D;
                    ai2[0] = 0;
                    ad5[0] += d3;
                    continue;
                }
                d3 = d7 - ad3[k1 - 1];
                if (d3 > d)
                {
                    ad4[(k + i1) - 1] = 1.0D;
                    ai2[0] = 0;
                    ad5[0] += d3;
                }
                continue;
            }
            if (ai[j1 - 1] == 1)
            {
                double d4 = ad[j1 - 1] - d7;
                if (d4 > d)
                {
                    ad4[(k + i1) - 1] = -1D;
                    ai2[0] = 0;
                    ad5[0] += d4;
                }
                continue;
            }
            if (ai[j1 - 1] == 2)
            {
                double d5 = d7 - ad1[j1 - 1];
                if (d5 > d)
                {
                    ad4[(k + i1) - 1] = 1.0D;
                    ai2[0] = 0;
                    ad5[0] += d5;
                }
                continue;
            }
            double d6 = ad[j1 - 1] - d7;
            if (d6 > d)
            {
                ad4[(k + i1) - 1] = -1D;
                ai2[0] = 0;
                ad5[0] += d6;
                continue;
            }
            d6 = d7 - ad1[j1 - 1];
            if (d6 > d)
            {
                ad4[(k + i1) - 1] = 1.0D;
                ai2[0] = 0;
                ad5[0] += d6;
            }
        }

    }

    private void l_d8prs(int i, int j, double ad[], int k, double ad1[], double ad2[], int l, int ai[], double ad3[])
    {
        for (int i1 = 1; i1 <= j; i1++)
        {
            int j1 = ai[(i + i1) - 1];
            if (j1 > j)
            {
                ad3[i1 - 1] = ad2[j1 - j - 1];
                continue;
            }
            if (l == 2)
                ad3[i1 - 1] = ad1[j1 - 1] - BLAS.dot(i, ad2, 0, ad, (j1 - 1) * k);
            else
                ad3[i1 - 1] = -BLAS.dot(i, ad2, 0, ad, (j1 - 1) * k);
        }

    }

    private void l_d9prs(int i, int j, int ai[], int ai1[], int ai2[], int ai3[], double ad[], double ad1[],
            double ad2[], double ad3[], double d, double d1, double d2, double ad4[], int ai4[])
    {
        double d7 = 0.0D;
        ai4[0] = 0;
        for (int k = i + 1; k <= i + j; k++)
        {
            int l = ai1[k - 1];
            if (l <= 0 || ai3[l - 1] == 0)
                continue;
            if (ai2[l - 1] == 3)
            {
                double d3;
                double d4;
                if (l > j)
                {
                    d4 = ad3[l - j - 1];
                    d3 = ad2[l - j - 1];
                }
                else
                {
                    d4 = ad1[l - 1];
                    d3 = ad[l - 1];
                }
                if (d4 - d3 <= d * (Math.abs(d4) + Math.abs(d3)))
                    continue;
            }
            double d6 = ad4[k - i - 1];
            if (ai3[l - 1] % 2 == 0)
                d6 = -d6;
            if (ai2[l - 1] == 4)
                d6 = -Math.abs(d6);
            if (d6 + d2 * d1 >= 0.0D)
                continue;
            double d5 = Math.abs(d6);
            if (d5 > d7)
            {
                ai4[0] = 1;
                d7 = d5;
                ai[0] = k;
            }
        }

    }

    private void l_d10rs(int i, int j, int k, int ai[], int ai1[], int ai2[], double ad[], double d, double d1,
            double ad1[], int l, double ad2[], double ad3[], double ad4[], double ad5[], int i1, int ai3[])
    {
        int k1 = 0;
        double d5;
        double d2 = d5 = 0.0D;
        double d7 = 1E+036D;
        ad[0] = 1E+036D;
        double d8 = 0.0D;
        ai3[0] = 1;
        int l1 = ai1[k - 1];
        if (ai2[l1 - 1] == 3)
        {
            if (l1 > j)
            {
                int j2 = l1 - j;
                ad[0] = ad5[j2 - 1] - ad4[j2 - 1];
            }
            else
            {
                ad[0] = ad3[l1 - 1] - ad2[l1 - 1];
            }
            ai[0] = k;
        }
        for (int j1 = 1; j1 <= i; j1++)
        {
            int i2 = ai1[j1 - 1];
            if (ai2[i2 - 1] == 4)
                continue;
            double d6 = ad1[(l + j1) - 1];
            if (Math.abs(d6) <= d * d1)
                continue;
            double d4;
            if (d6 > 0.0D)
            {
                if (ai2[i2 - 1] == 2)
                {
                    double d3;
                    if (i2 > j)
                        d3 = ad1[(i1 + j1) - 1] - ad5[i2 - j - 1];
                    else
                        d3 = ad1[(i1 + j1) - 1] - ad3[i2 - 1];
                    if (d3 <= d1)
                        continue;
                    d6 = d3 / d6;
                    if (d8 < d6)
                    {
                        d8 = d6;
                        k1 = j1;
                    }
                    continue;
                }
                if (i2 > j)
                    d4 = ad1[(i1 + j1) - 1] - ad4[i2 - j - 1];
                else
                    d4 = ad1[(i1 + j1) - 1] - ad2[i2 - 1];
                if (d4 <= -d1)
                    continue;
            }
            else
            {
                if (ai2[i2 - 1] == 1)
                {
                    if (i2 > j)
                        d4 = ad1[(i1 + j1) - 1] - ad4[i2 - j - 1];
                    else
                        d4 = ad1[(i1 + j1) - 1] - ad2[i2 - 1];
                    if (d4 >= -d1)
                        continue;
                    d6 = d4 / d6;
                    if (d8 < d6)
                    {
                        d8 = d6;
                        k1 = j1;
                    }
                    continue;
                }
                if (i2 > j)
                    d4 = ad1[(i1 + j1) - 1] - ad5[i2 - j - 1];
                else
                    d4 = ad1[(i1 + j1) - 1] - ad3[i2 - 1];
                if (d4 >= d1)
                    continue;
            }
            d6 = d4 / d6;
            if (d6 < 0.0D)
                d6 = 0.0D;
            if (ad[0] >= d6 && (ad[0] != d6 || Math.abs(ad1[(ai[0] - 1) + l]) < Math.abs(ad1[(l + j1) - 1])))
            {
                ad[0] = d6;
                ai[0] = j1;
            }
        }

        if (ad[0] >= d7)
            if (d8 == 0.0D)
            {
                ai3[0] = 0;
            }
            else
            {
                ai[0] = k1;
                ad[0] = d8;
            }
    }

    private void l_d11rs(int i, int j, int k, int l, int ai[], int ai1[], int ai2[], double d, double ad[], int i1,
            int j1, double ad1[], double ad2[], double ad3[], double ad4[], double d1, double ad5[], int ai3[])
    {
        ai3[0] = 1;
        BLAS.axpy(i, -d, ad, j1, ad, i1);
        if (k == l)
        {
            ai3[0] = 0;
            int k1 = ai[k - 1];
            if (ai2[k1 - 1] == 1)
                ai2[k1 - 1] = 2;
            else
                ai2[k1 - 1] = 1;
            return;
        }
        int l1 = ai[l - 1];
        if (ai1[l1 - 1] == 1)
            ai2[l1 - 1] = 1;
        else if (ai1[l1 - 1] == 2)
        {
            ai2[l1 - 1] = 2;
        }
        else
        {
            double d2;
            if (l1 > j)
                d2 = ad[(i1 + l) - 1] - ad3[l1 - j - 1];
            else
                d2 = ad[(i1 + l) - 1] - ad1[l1 - 1];
            if (Math.abs(d2) < d1)
                ai2[l1 - 1] = 1;
            else
                ai2[l1 - 1] = 2;
        }
        BLAS.copy(i - l, ad, i1 + l, 1, ad, (i1 + l) - 1, 1);
        l1 = ai[k - 1];
        if (ai2[l1 - 1] == 3)
        {
            if (ad5[k - i - 1] > 0.0D)
                ad[(i1 + i) - 1] = -d;
            else
                ad[(i1 + i) - 1] = d;
            ai2[l1 - 1] = -1;
            ai[k - 1] = ai[Math.abs(l) - 1];
            BLAS.copy(i - l, ai, l, 1, ai, l - 1, 1);
            ai[i - 1] = l1;
            return;
        }
        if (l1 > j)
        {
            int i2 = l1 - j;
            if (ai2[l1 - 1] == 1)
                ad[(i1 + i) - 1] = ad3[i2 - 1] + d;
            else
                ad[(i1 + i) - 1] = ad4[i2 - 1] - d;
        }
        else if (ai2[l1 - 1] == 1)
            ad[(i1 + i) - 1] = ad1[l1 - 1] + d;
        else
            ad[(i1 + i) - 1] = ad2[l1 - 1] - d;
        ai2[l1 - 1] = -1;
        ai[k - 1] = ai[Math.abs(l) - 1];
        BLAS.copy(i - l, ai, l, 1, ai, l - 1, 1);
        ai[i - 1] = l1;
    }

    private void l_d12rs(int i, int j, double ad[], int k, int ai[], double d, double d1, double ad1[], double ad2[],
            int l, int i1, int ai1[])
    {
        BLAS.copy(i, ad2, i1, 1, ad1, 0, 1);
        for (int i2 = 1; i2 <= i; i2++)
        {
            double d3 = ad2[(l + i2) - 1];
            if (d3 <= d1)
                continue;
            int l1 = ai[i2 - 1];
            if (l1 > j)
            {
                int j1 = l1 - j;
                ad1[j1 - 1] += d3;
            }
            else
            {
                BLAS.axpy(i, -d3, ad, (l1 - 1) * k, ad1, 0);
            }
        }

        double d2 = 0.0D;
        for (int k1 = 1; k1 <= i; k1++)
            d2 = Math.max(Math.abs(ad1[k1 - 1] / (Math.abs(ad2[(i1 + k1) - 1]) + 1.0D)), d2);

        ai1[0] = d2 <= d ? 0 : 1;
    }

    private void l_d13rs(int i, double ad[], int j, int ai[], int k, int l, int i1, int j1, int k1, int ai1[],
            double ad1[])
    {
        int l1 = i1 + 1;
        BLAS.copy(i - i1, ad, (l1 - 1) * j + (l1 - 1), j + 1, ad1, 0, 1);
        for (int i2 = i1; i2 <= i - 1; i2++)
            BLAS.copy(i2, ad, i2 * j, 1, ad, (i2 - 1) * j, 1);

        BLAS.copy(i, ad, l, 1, ad, (i - 1) * j, 1);
        int k2 = 1;
        for (int j2 = i1; j2 <= i - 1; j2++)
        {
            double d;
            if (Math.abs(ad[(j2 - 1) * j + (j2 - 1)]) >= Math.abs(ad1[k2 - 1]))
            {
                ai[(ai1[0] - 1) + j1] = -j2;
                d = -ad1[k2 - 1] / ad[(j2 - 1) * j + (j2 - 1)];
            }
            else
            {
                ai[(ai1[0] - 1) + j1] = j2;
                d = -ad[(j2 - 1) * j + (j2 - 1)] / ad1[k2 - 1];
                ad[(j2 - 1) * j + (j2 - 1)] = ad1[k2 - 1];
                BLAS.swap(i - j2, ad, j2 * j + (j2 - 1), j, ad, j2 * j + j2, j);
            }
            if (d != 0.0D)
                BLAS.axpy(i - j2, d, ad, j2 * j + (j2 - 1), j, ad, j2 * j + j2, j);
            ad[(ai1[0] - 1) + k1] = d;
            ai1[0]++;
            k2++;
        }

    }

    private void l_d14rs(int i, double ad[], int j, int ai[], int k, double ad1[], int l, int i1, double ad2[], int j1)
    {
        BLAS.copy(i, ad1, l, 1, ad2, j1, 1);
        if (i1 == 1)
        {
            for (int k1 = 1; k1 <= i - 1; k1++)
            {
                int i2 = ai[(k + k1) - 1];
                double d = ad2[(j1 + i2) - 1];
                if (i2 != k1)
                {
                    ad2[(j1 + i2) - 1] = ad2[(j1 + k1) - 1];
                    ad2[(j1 + k1) - 1] = d;
                }
                BLAS.axpy(i - k1, d, ad, (k1 - 1) * j + k1, ad2, j1 + k1);
            }

        }
        else if (i1 == 2)
        {
            for (int l1 = i - 1; l1 >= 1; l1--)
            {
                ad2[(j1 + l1) - 1] += BLAS.dot(i - l1, ad, (l1 - 1) * j + l1, ad2, j1 + l1);
                int j2 = ai[(k + l1) - 1];
                if (j2 != l1)
                {
                    double d1 = ad2[(j1 + j2) - 1];
                    ad2[(j1 + j2) - 1] = ad2[(j1 + l1) - 1];
                    ad2[(j1 + l1) - 1] = d1;
                }
            }

        }
    }

    public void setMaximumIteration(int i)
    {
        if (i <= 0)
        {
            Object aobj[] =
            {
                    "iterations", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);

        }
        else
        {
            max_itn = i;
        }
    }

    public void setUpperLimit(double ad[])
    {
        if (ad.length != l_m)
        {
            Object aobj[] =
            {
                    "upperLimit", new Integer(ad.length), new Integer(l_m)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj);
            throw new ConditionalException("setUpperLimit : NotEqual -> upperLimit = " + ad.length + " and l_m = "
                    + l_m);
        }
        else
        {
            user_bu = 1;
            l_bu = (double[]) ad.clone();
        }
    }

    public void setLowerBound(double ad[])
    {
        if (ad.length != l_n)
        {
            Object aobj[] =
            {
                    "lowerBound", new Integer(ad.length), new Integer(l_n)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj);
            throw new ConditionalException("setLowerBound : NotEqual -> lowerBound = " + ad.length + " and l_n = "
                    + l_n);
        }
        else
        {
            user_xlb = 1;
            l_xlb = (double[]) ad.clone();
        }
    }

    public void setUpperBound(double ad[])
    {
        if (ad.length != l_n)
        {
            Object aobj[] =
            {
                    "upperBound", new Integer(ad.length), new Integer(l_n)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj);
            throw new ConditionalException("setUpperBound : NotEqual -> upperBound = " + ad.length + " and l_n = "
                    + l_n);
        }
        else
        {
            user_xub = 1;
            l_xub = (double[]) ad.clone();
        }
    }

    public void setConstraintType(int ai[])
    {
        if (ai.length != l_m)
        {
            Object aobj[] =
            {
                    "constraintType", new Integer(ai.length), new Integer(l_m)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj);
            throw new ConditionalException("setConstraintType : NotEqual -> constraintType = " + ai.length
                    + " and l_m = " + l_m);
        }
        for (int i = 0; i < ai.length; i++)
            if (ai[i] < 0 || ai[i] > 3)
            {
                Object aobj1[] =
                {
                        "constraintType[" + i + "]", new Integer(ai[i])
                };
                // Messages.throwIllegalArgumentException("com.imsl.math",
                // "CannotBe", aobj1);
                throw new ConditionalException("setConstraintType : CannotBe -> constraintType[" + i + "]  and a[" + i
                        + "] = " + ai[i]);
            }

        user_irtype = 1;
        l_irtype = (int[]) ai.clone();
    }

    public double getOptimalValue()
    {
        return optimalValue;
    }

    public double[] getPrimalSolution()
    {
        if (solution == null)
            return null;
        else
            return (double[]) solution.clone();
    }

    public double[] getDualSolution()
    {
        if (dualSolution == null)
            return null;
        else
            return (double[]) dualSolution.clone();
    }

}