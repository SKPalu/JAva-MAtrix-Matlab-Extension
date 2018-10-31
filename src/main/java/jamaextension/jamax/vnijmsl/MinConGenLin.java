/*
 * MinConGenLin.java
 *
 * Created on 31 March 2007, 16:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

import java.io.Serializable;

import jamaextension.jamax.ConditionalException;

// Referenced classes of package com.imsl.math:
//            BLAS

public class MinConGenLin implements Serializable, Cloneable
{
    public static class EqualityConstraintsException extends ConditionalException
    {

        static final long serialVersionUID = 0xe90443b1859eaf26L;

        public EqualityConstraintsException(String s)
        {
            super(s);
        }

    }

    public static class ConstraintsNotSatisfiedException extends ConditionalException
    {

        static final long serialVersionUID = 0xac5faebf920d3361L;

        public ConstraintsNotSatisfiedException(String s)
        {
            super(s);
        }

    }

    public static class VarBoundsInconsistentException extends ConditionalException
    {

        static final long serialVersionUID = 0xa1d11d469715e284L;

        public VarBoundsInconsistentException(String s)
        {
            super(s);
        }

    }

    public static class ConstraintsInconsistentException extends ConditionalException
    {

        static final long serialVersionUID = 0x8dda5935bf80f8adL;

        public ConstraintsInconsistentException(String s)
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

    static final long serialVersionUID = 0x30a0a22d7bd71645L;
    // private static final int check = Messages.check(1);
    private double l_a[];
    private double l_b[];
    private double l_xlb[];
    private double l_xub[];
    private double lv_value[];
    private int l_nvar;
    private int l_ncon;
    private int l_neq;
    private int iact_user[];
    private double alamda_user[];
    private int active_constraints_user;
    private int lagrange_multipliers_user;
    private transient Function l_fcn;
    private transient Gradient l_grad;
    private int user_xguess;
    private int l_maxfcn;
    private int l_nact;
    private double l_xguess[];
    private double l_tol;
    private double l_obj;

    public MinConGenLin(Function function, int i, int j, int k, double ad[], double ad1[], double ad2[], double ad3[])
    {
        l_fcn = function;
        if (i <= 0)
        {
            Object aobj[] =
            {
                    "nvar", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("MinConGenLin : Not positive for nvar = " + i);
        }
        l_nvar = i;
        if (j < 0)
        {
            Object aobj1[] =
            {
                    "ncon", new Integer(j)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "Negative", aobj1);
            throw new ConditionalException("MinConGenLin : Not positive for ncon = " + i);
        }
        l_ncon = j;
        if (k < 0)
        {
            Object aobj2[] =
            {
                    "neq", new Integer(k)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "Negative", aobj2);
            throw new ConditionalException("MinConGenLin : Negative for neq = " + i);
        }
        l_neq = k;
        if (k > j)
        {
            Object aobj3[] =
            {
                    "neq", "ncon"
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "XLessOrEqualY", aobj3);
            throw new ConditionalException("MinConGenLin : XLessOrEqualY (neq = " + k + ", ncon = " + j + ")");
        }
        if (ad.length != j * i)
        {
            Object aobj4[] =
            {
                    "a", new Integer(ad.length), new Integer(j * i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj4);
            throw new ConditionalException("MinConGenLin : NotEqual - a :  " + ad.length + " & " + (j * i));
        }
        l_a = (double[]) ad.clone();
        if (ad1.length != j)
        {
            Object aobj5[] =
            {
                    "b", new Integer(ad1.length), new Integer(j)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj5);
            throw new ConditionalException("MinConGenLin : NotEqual - b :  " + ad1.length + " & " + j);
        }
        l_b = (double[]) ad1.clone();
        if (ad2.length != i)
        {
            Object aobj6[] =
            {
                    "lowerBound", new Integer(ad2.length), new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj6);
            throw new ConditionalException("MinConGenLin : NotEqual - lowerBound :  " + ad2.length + " & " + i);
        }
        l_xlb = (double[]) ad2.clone();
        if (ad3.length != i)
        {
            Object aobj7[] =
            {
                    "upperBound", new Integer(ad3.length), new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj7);
            throw new ConditionalException("MinConGenLin : NotEqual - upperBound :  " + ad3.length + " & " + i);
        }
        l_xub = (double[]) ad3.clone();
        for (int l = 0; l < i; l++)
            if (ad2[l] > ad3[l])
            {
                Object aobj8[] =
                {
                        "lowerBound[" + l + "]", "upperBound[" + l + "]"
                };
                // Messages.throwIllegalArgumentException("com.imsl.math",
                // "XLessOrEqualY", aobj8);
                throw new ConditionalException("MinConGenLin : XLessOrEqualY : lowerBound[" + l + "], upperBound[" + l
                        + "]");
            }

        active_constraints_user = 0;
        iact_user = new int[2 * i + j];
        lagrange_multipliers_user = 0;
        alamda_user = new double[i];
        user_xguess = 0;
        l_xguess = new double[i];
        l_grad = (function instanceof Gradient) ? (Gradient) function : null;
        l_maxfcn = 0x7fffffff;
        l_tol = Math.sqrt(2.2204460492503131E-016D);
    }

    public final void solve() throws ConstraintsInconsistentException, VarBoundsInconsistentException,
            ConstraintsNotSatisfiedException, EqualityConstraintsException
    {
        int i = 0;
        double ad[] = l_xguess;
        double d = 0.0D;
        boolean flag = false;
        int ai[] =
        {
            0
        };
        double ad2[] =
        {
            0.0D
        };
        double d2 = l_tol;
        int ai1[] =
        {
            0
        };
        int ai2[] =
        {
            l_maxfcn
        };
        boolean flag1 = false;
        boolean flag2 = false;
        int k = l_nvar;
        int l = l_ncon;
        int i1 = l_neq;
        double ad3[] = l_a;
        double ad4[] = l_b;
        double ad5[] = l_xlb;
        double ad6[] = l_xub;
        Function function = l_fcn;
        Gradient gradient = l_grad;
        int j = k * k + 11 * k + l;
        double ad1[] = new double[j];
        lv_value = new double[k];
        double d1 = d2;
        if (l > 1)
            l_m1ran(l, k, ad3, ad3);
        if (l_grad != null)
            l_l2ong(function, gradient, k, l, i1, ad3, l, ad4, ad5, ad6, ad, d1, ai2, lv_value, ad2, ai1, iact_user,
                    alamda_user, i, ai, ad1);
        else
            l_l2onf(function, k, l, i1, ad3, l, ad4, ad5, ad6, ad, d1, ai2, lv_value, ad2, ai1, iact_user, alamda_user,
                    i, ai, ad1);
        l_nact = ai1[0];
        l_obj = ad2[0];
        if (l > 1)
            l_m1ran(k, l, ad3, ad3);
    }

    private void l_m1ran(int i, int j, double ad[], double ad1[])
    {
        boolean flag = false;
        int k1 = i * j - 1;
        if (i != j)
            flag = true;
        if (!flag)
        {
            if (ad1 != ad)
                BLAS.copy(i * j, ad, ad1);
            int l1 = 2;
            int i2 = i + 1;
            int j2 = i - 1;
            for (int k = i; k <= k1; k += i)
            {
                int k2 = l1 + j2;
                for (int i1 = l1; i1 <= k; i1++)
                {
                    double d = ad1[i1 - 1];
                    ad1[i1 - 1] = ad1[k2 - 1];
                    ad1[k2 - 1] = d;
                    k2 += i;
                }

                l1 += i2;
            }

            return;
        }
        double ad2[];
        if (ad == ad1)
            ad2 = new double[j * i];
        else
            ad2 = ad1;
        for (int l = 0; l < i; l++)
        {
            for (int j1 = 0; j1 < j; j1++)
                ad2[l + i * j1] = ad[j1 + j * l];

        }

        if (ad == ad1)
            BLAS.copy(i * j, ad2, ad1);
    }

    private void l_l2ong(Function function, Gradient gradient, int i, int j, int k, double ad[], int l, double ad1[],
            double ad2[], double ad3[], double ad4[], double d, int ai[], double ad5[], double ad6[], int ai1[],
            int ai2[], double ad7[], int i1, int ai3[], double ad8[]) throws ConstraintsInconsistentException,
            VarBoundsInconsistentException, ConstraintsNotSatisfiedException, EqualityConstraintsException
    {
        BLAS.copy(i, ad4, ad5);
        int l1 = 1;
        int k2 = l1 + i;
        int k3 = k2 + i;
        int l2 = k3 + i * i;
        int i3 = l2 + i;
        int j1 = i3 + i;
        int k1 = j1 + j + i + i;
        int l3 = k1 + i;
        int i2 = l3 + i;
        int j3 = i2 + i;
        int j2 = j3 + i;
        ai3[0] = 0;
        l_l3ong(function, gradient, i, j, k, ad, l, ad1, ad2, ad3, ad5, ad6, d, ai, ai2, ai1, ad7, i1, ai3, ad8,
                l1 - 1, k3 - 1, l2 - 1, i3 - 1, k2 - 1, j1 - 1, k1 - 1, l3 - 1, i2 - 1, j3 - 1, j2 - 1);
        if (ai3[0] != 1 && ai3[0] != 2 && ai3[0] != 3)
        {
            if (ai3[0] == 5)
                // throw new
                // ConstraintsInconsistentException("MinConGenLin.ConstraintsInconsistent",
                // null);
                throw new ConditionalException("l_l2ong : Constraints Inconsistent");
            if (ai3[0] == 6)
                // throw new
                // VarBoundsInconsistentException("MinConGenLin.VarBoundsInconsistent",
                // null);
                throw new ConditionalException("l_l2ong : Var Bounds Inconsistent");
            if (ai3[0] == 7)
                // throw new
                // ConstraintsNotSatisfiedException("MinConGenLin.ConstraintsNotSatisfied",
                // null);
                throw new ConditionalException("l_l2ong : Constraints Not Satisfied");
            if (ai3[0] == 9)
                // throw new
                // EqualityConstraintsException("MinConGenLin.EqualityConstraints",
                // null);
                throw new ConditionalException("l_l2ong : Equality Constraints");
        }
    }

    private void l_l2onf(Function function, int i, int j, int k, double ad[], int l, double ad1[], double ad2[],
            double ad3[], double ad4[], double d, int ai[], double ad5[], double ad6[], int ai1[], int ai2[],
            double ad7[], int i1, int ai3[], double ad8[]) throws ConstraintsInconsistentException,
            VarBoundsInconsistentException, ConstraintsNotSatisfiedException, EqualityConstraintsException
    {
        BLAS.copy(i, ad4, ad5);
        int l1 = 1;
        int k2 = l1 + i;
        int k3 = k2 + i;
        int l2 = k3 + i * i;
        int i3 = l2 + i;
        int j1 = i3 + i;
        int k1 = j1 + j + i + i;
        int l3 = k1 + i;
        int i2 = l3 + i;
        int j3 = i2 + i;
        int j2 = j3 + i;
        ai3[0] = 0;
        l_l3onf(function, i, j, k, ad, l, ad1, ad2, ad3, ad5, ad6, d, ai, ai2, ai1, ad7, i1, ai3, ad8, l1 - 1, k3 - 1,
                l2 - 1, i3 - 1, k2 - 1, j1 - 1, k1 - 1, l3 - 1, i2 - 1, j3 - 1, j2 - 1);
        if (ai3[0] != 1 && ai3[0] != 2 && ai3[0] != 3)
        {
            if (ai3[0] == 5)
                // throw new
                // ConstraintsInconsistentException("MinConGenLin.ConstraintsInconsistent",
                // null);
                throw new ConditionalException("l_l2onf : Constraints Inconsistent");

            if (ai3[0] == 6)
                // throw new
                // VarBoundsInconsistentException("MinConGenLin.VarBoundsInconsistent",
                // null);
                throw new ConditionalException("l_l2onf : Var Bounds Inconsistent");
            if (ai3[0] == 7)
                // throw new
                // ConstraintsNotSatisfiedException("MinConGenLin.ConstraintsNotSatisfied",
                // null);
                throw new ConditionalException("l_l2onf : Constraints Not Satisfied");
            if (ai3[0] == 9)
                // throw new
                // EqualityConstraintsException("MinConGenLin.EqualityConstraints",
                // null);
                throw new ConditionalException("l_l2onf : Equality Constraints");
        }
    }

    private void l_l3ong(Function function, Gradient gradient, int i, int j, int k, double ad[], int l, double ad1[],
            double ad2[], double ad3[], double ad4[], double ad5[], double d, int ai[], int ai1[], int ai2[],
            double ad6[], int i1, int ai3[], double ad7[], int j1, int k1, int l1, int i2, int j2, int k2, int l2,
            int i3, int j3, int k3, int l3)
    {
        int ai7[];
        label0:
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
            ai7 = (new int[]
            {
                0
            });
            double ad8[] =
            {
                0.0D
            };
            double ad9[] =
            {
                0.0D
            };
            double ad10[] =
            {
                0.0D
            };
            ad8[0] = -1D;
            ai6[0] = 0;
            ai7[0] = 0;
            int k5 = ai[0];
            ai3[0] = 4;
            l_l4ong(i, j, ad2, ad3, ad4, ai1, ai4, ai3, ad7, k1, l1, i2, ad9);
            ad10[0] = Math.max(0.01D, 10D * ad9[0]);
            if (k > 0)
            {
                l_l5ong(i, j, k, ad, l, ad1, ad3, ai1, ai4, ai3, ad7, k1, l1, ad9, k3, l3);
                if (ai3[0] == 5)
                {
                    ai[0] = ai7[0];
                    return;
                }
            }
            ai2[0] = ai4[0];
            ai5[0] = ai4[0];
            int j5 = ai2[0];
            for (int i4 = 1; i4 <= i; i4++)
            {
                int j4 = i4 - 1;
                if (ad2[j4] < ad3[j4])
                {
                    j5 += 2;
                    ai1[j5 - 2] = j + i4;
                    ai1[j5 - 1] = j + i + i4;
                }
            }

            l_l6ong(i, j, ad, l, ad1, ad2, ad3, ad4, ai1, ai2, ad6, ai3, ad7, j1, k1, l1, i2, ad9[0], ad10, ai4[0],
                    ai5, j5, k2, l2, i3, j3, j2, k3, l3);
            if (ai5[0] < j5)
            {
                ai3[0] = 6;
                ai[0] = ai7[0];
                return;
            }
            if (j > k)
            {
                int i5 = k + 1;
                for (int k4 = i5; k4 <= j; k4++)
                {
                    int l4 = k4 - 1;
                    j5++;
                    ai1[j5 - 1] = k4;
                }

            }
            do
            {
                l_l6ong(i, j, ad, l, ad1, ad2, ad3, ad4, ai1, ai2, ad6, ai3, ad7, j1, k1, l1, i2, ad9[0], ad10, ai4[0],
                        ai5, j5, k2, l2, i3, j3, j2, k3, l3);
                if (ai5[0] < j5)
                {
                    ai3[0] = 7;
                    ai[0] = ai7[0];
                    return;
                }
                if (ai4[0] == i)
                {
                    ai3[0] = 9;
                    ai[0] = ai7[0];
                    return;
                }
                l_l7ong(function, gradient, i, j, ad, l, ad1, ad2, ad3, ad4, ad5, d, ai1, ai2, ad6, i1, ai3, ad7, j1,
                        k1, l1, i2, ad9[0], ad8, ad10[0], ai4[0], j5, ai6, ai7, k5, j2, k2, l2, i3, j3, k3, l3);
                if (ad10[0] <= ad9[0] || ai2[0] <= 0)
                    break label0;
                if (ai7[0] == k5)
                    break;
                l_l8ong(i, j, ad, l, ad1, ad2, ad3, ad4, ai1, ai2[0], ad7, i2, ad9[0], ad10, ai4[0]);
            }
            while (true);
            ai3[0] = 8;
        }
        ai[0] = ai7[0];
    }

    private void l_l3onf(Function function, int i, int j, int k, double ad[], int l, double ad1[], double ad2[],
            double ad3[], double ad4[], double ad5[], double d, int ai[], int ai1[], int ai2[], double ad6[], int i1,
            int ai3[], double ad7[], int j1, int k1, int l1, int i2, int j2, int k2, int l2, int i3, int j3, int k3,
            int l3)
    {
        int ai7[];
        label0:
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
            ai7 = (new int[]
            {
                0
            });
            double ad8[] =
            {
                0.0D
            };
            double ad9[] =
            {
                0.0D
            };
            double ad10[] =
            {
                0.0D
            };
            ad10[0] = -1D;
            ai6[0] = 0;
            ai7[0] = 0;
            int k5 = ai[0];
            ai3[0] = 4;
            l_l4onf(i, j, ad2, ad3, ad4, ai1, ai4, ai3, ad7, k1, l1, i2, ad8);
            ad9[0] = Math.max(0.01D, 10D * ad8[0]);
            if (k > 0)
            {
                l_l5onf(i, j, k, ad, l, ad1, ad3, ai1, ai4, ai3, ad7, k1, l1, ad8[0], k3, l3);
                if (ai3[0] == 5)
                {
                    ai[0] = ai7[0];
                    return;
                }
            }
            ai2[0] = ai4[0];
            ai5[0] = ai4[0];
            int j5 = ai2[0];
            for (int i4 = 1; i4 <= i; i4++)
            {
                int j4 = i4 - 1;
                if (ad2[j4] < ad3[j4])
                {
                    j5 += 2;
                    ai1[j5 - 2] = j + i4;
                    ai1[j5 - 1] = j + i + i4;
                }
            }

            l_l6onf(i, j, ad, l, ad1, ad2, ad3, ad4, ai1, ai2, ad6, ai3, ad7, j1, k1, l1, i2, ad8[0], ad9, ai4[0], ai5,
                    j5, k2, l2, i3, j3, j2, k3, l3);
            if (ai5[0] < j5)
            {
                ai3[0] = 6;
                ai[0] = ai7[0];
                return;
            }
            if (j > k)
            {
                int i5 = k + 1;
                for (int k4 = i5; k4 <= j; k4++)
                {
                    int l4 = k4 - 1;
                    j5++;
                    ai1[j5 - 1] = k4;
                }

            }
            do
            {
                l_l6onf(i, j, ad, l, ad1, ad2, ad3, ad4, ai1, ai2, ad6, ai3, ad7, j1, k1, l1, i2, ad8[0], ad9, ai4[0],
                        ai5, j5, k2, l2, i3, j3, j2, k3, l3);
                if (ai5[0] < j5)
                {
                    ai3[0] = 7;
                    ai[0] = ai7[0];
                    return;
                }
                if (ai4[0] == i)
                {
                    ai3[0] = 9;
                    ai[0] = ai7[0];
                    return;
                }
                l_l7onf(function, i, j, ad, l, ad1, ad2, ad3, ad4, ad5, d, ai1, ai2, ad6, i1, ai3, ad7, j1, k1, l1, i2,
                        ad8[0], ad10, ad9[0], ai4[0], j5, ai6, ai7, k5, j2, k2, l2, i3, j3, k3, l3);
                if (ad9[0] <= ad8[0] || ai2[0] <= 0)
                    break label0;
                if (ai7[0] == k5)
                    break;
                l_l8onf(i, j, ad, l, ad1, ad2, ad3, ad4, ai1, ai2[0], ad7, i2, ad8[0], ad9, ai4[0]);
            }
            while (true);
            ai3[0] = 8;
        }
        ai[0] = ai7[0];
    }

    private void l_l4ong(int i, int j, double ad[], double ad1[], double ad2[], int ai[], int ai1[], int ai2[],
            double ad3[], int k, int l, int i1, double ad4[])
    {
        double d2 = 100D;
        ad4[0] = 1.0D;
        double d;
        double d1;
        do
        {
            ad4[0] *= 0.5D;
            d = d2 + 0.5D * ad4[0];
            d1 = d2 + ad4[0];
        }
        while (d2 < d && d < d1);
        ai1[0] = 0;
        for (int j1 = 1; j1 <= i; j1++)
        {
            int i2 = j1 - 1;
            if (ad[i2] > ad1[i2])
                return;
            if (ad[i2] == ad1[i2])
                ai1[0]++;
        }

        int j3 = 0;
        int k3 = i * i;
        for (int k1 = 1; k1 <= k3; k1++)
        {
            int j2 = k1 - 1;
            ad3[k + j2] = 0.0D;
        }

        int l2 = 0;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int k2 = l1 - 1;
            int i3;
            if (ad[k2] == ad1[k2])
            {
                ad2[k2] = ad1[k2];
                j3++;
                ad3[(l + j3) - 1] = 1.0D;
                ai[j3 - 1] = l1 + j + i;
                i3 = j3;
            }
            else
            {
                i3 = (l1 + ai1[0]) - j3;
            }
            ad3[(k + l2 + i3) - 1] = 1.0D;
            l2 += i;
            ad3[i1 + k2] = Math.abs(ad2[k2]);
        }

        ai2[0] = 1;
    }

    private void l_l4onf(int i, int j, double ad[], double ad1[], double ad2[], int ai[], int ai1[], int ai2[],
            double ad3[], int k, int l, int i1, double ad4[])
    {
        double d2 = 100D;
        ad4[0] = 1.0D;
        double d;
        double d1;
        do
        {
            ad4[0] *= 0.5D;
            d = d2 + 0.5D * ad4[0];
            d1 = d2 + ad4[0];
        }
        while (d2 < d && d < d1);
        ai1[0] = 0;
        for (int j1 = 1; j1 <= i; j1++)
        {
            int i2 = j1 - 1;
            if (ad[i2] > ad1[i2])
                return;
            if (ad[i2] == ad1[i2])
                ai1[0]++;
        }

        int j3 = 0;
        int k3 = i * i;
        for (int k1 = 1; k1 <= k3; k1++)
        {
            int j2 = k1 - 1;
            ad3[k + j2] = 0.0D;
        }

        int l2 = 0;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int k2 = l1 - 1;
            int i3;
            if (ad[k2] == ad1[k2])
            {
                ad2[k2] = ad1[k2];
                j3++;
                ad3[(l + j3) - 1] = 1.0D;
                ai[j3 - 1] = l1 + j + i;
                i3 = j3;
            }
            else
            {
                i3 = (l1 + ai1[0]) - j3;
            }
            ad3[(k + l2 + i3) - 1] = 1.0D;
            l2 += i;
            ad3[i1 + k2] = Math.abs(ad2[k2]);
        }

        ai2[0] = 1;
    }

    private void l_l5ong(int i, int j, int k, double ad[], int l, double ad1[], double ad2[], int ai[], int ai1[],
            int ai2[], double ad3[], int i1, int j1, double ad4[], int k1, int l1)
    {
        for (int k4 = 1; k4 <= k; k4++)
        {
            int l4 = k4 - 1;
            if (ai1[0] < i)
            {
                int i5 = ai1[0] + 1;
                ai[i5 - 1] = k4;
                l_l9ong(i, j, ad, l, ai, ai1, ad3, i1, j1, ad4[0], i5, k1, l1);
                if (ai1[0] == i5)
                    continue;
            }
            double d1 = ad1[l4];
            double d2 = Math.abs(ad1[l4]);
            if (ai1[0] > 0)
            {
                for (int i2 = 1; i2 <= i; i2++)
                {
                    int l2 = i2 - 1;
                    ad3[k1 + l2] = ad[l2 * l + l4];
                }

                int j4 = ai1[0];
                do
                {
                    double d3 = 0.0D;
                    int k3 = j4;
                    for (int j2 = 1; j2 <= i; j2++)
                    {
                        int i3 = j2 - 1;
                        d3 += ad3[(i1 + k3) - 1] * ad3[k1 + i3];
                        k3 += i;
                    }

                    d3 *= ad3[(j1 + j4) - 1];
                    int l3 = ai[j4 - 1];
                    double d;
                    if (l3 <= j)
                    {
                        for (int k2 = 1; k2 <= i; k2++)
                        {
                            int j3 = k2 - 1;
                            ad3[k1 + j3] += -d3 * ad[j3 * l + (l3 - 1)];
                        }

                        d = ad1[l3 - 1];
                    }
                    else
                    {
                        int i4 = l3 - j - i;
                        ad3[(k1 + i4) - 1] -= d3;
                        d = ad2[i4 - 1];
                    }
                    d1 += -d * d3;
                    d2 += Math.abs(d * d3);
                }
                while (--j4 >= 1);
            }
            if (Math.abs(d1) > ad4[0] * d2)
            {
                ai2[0] = 5;
                return;
            }
        }

    }

    private void l_l5onf(int i, int j, int k, double ad[], int l, double ad1[], double ad2[], int ai[], int ai1[],
            int ai2[], double ad3[], int i1, int j1, double d, int k1, int l1)
    {
        for (int k4 = 1; k4 <= k; k4++)
        {
            int l4 = k4 - 1;
            if (ai1[0] < i)
            {
                int i5 = ai1[0] + 1;
                ai[i5 - 1] = k4;
                l_l9onf(i, j, ad, l, ai, ai1, ad3, i1, j1, d, i5, k1, l1);
                if (ai1[0] == i5)
                    continue;
            }
            double d2 = ad1[l4];
            double d3 = Math.abs(ad1[l4]);
            if (ai1[0] > 0)
            {
                for (int i2 = 1; i2 <= i; i2++)
                {
                    int l2 = i2 - 1;
                    ad3[k1 + l2] = ad[l2 * l + l4];
                }

                int j4 = ai1[0];
                do
                {
                    double d4 = 0.0D;
                    int k3 = j4;
                    for (int j2 = 1; j2 <= i; j2++)
                    {
                        int i3 = j2 - 1;
                        d4 += ad3[(i1 + k3) - 1] * ad3[k1 + i3];
                        k3 += i;
                    }

                    d4 *= ad3[(j1 + j4) - 1];
                    int l3 = ai[j4 - 1];
                    double d1;
                    if (l3 <= j)
                    {
                        for (int k2 = 1; k2 <= i; k2++)
                        {
                            int j3 = k2 - 1;
                            ad3[k1 + j3] += -d4 * ad[j3 * l + (l3 - 1)];
                        }

                        d1 = ad1[l3 - 1];
                    }
                    else
                    {
                        int i4 = l3 - j - i;
                        ad3[(k1 + i4) - 1] -= d4;
                        d1 = ad2[i4 - 1];
                    }
                    d2 += -d1 * d4;
                    d3 += Math.abs(d1 * d4);
                }
                while (--j4 >= 1);
            }
            if (Math.abs(d2) > d * d3)
            {
                ai2[0] = 5;
                return;
            }
        }

    }

    private void l_l6ong(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            int ai[], int ai1[], double ad5[], int ai2[], double ad6[], int l, int i1, int j1, int k1, double d,
            double ad7[], int l1, int ai3[], int i2, int j2, int k2, int l2, int i3, int j3, int k3, int l3)
    {
        double ad8[] =
        {
            0.0D
        };
        double ad9[] =
        {
            0.0D
        };
        int ai4[] =
        {
            0
        };
        int l4 = 0;
        double d1 = 0.0D;
        int k4 = 0;
        ai2[0] = 0;
        boolean flag = true;
        boolean flag1 = true;
        do
        {
            if (flag)
            {
                l_l10ng(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1, ai2, ad6, i1, j1, k1, d, ad7[0], l1);
                if (ai2[0] > 0)
                    ai3[0] = ai1[0];
                if (ai3[0] == i2)
                    return;
            }
            if (flag1)
            {
                l4 = ai3[0];
                d1 = 0.0D;
            }
            l_l11ng(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1, ad5, ad6, l, i1, j1, k1, j2, k2, l2, d, ad7[0], ad8, ad9,
                    l1, ai3, i2, ai4, i3, j3, k3, l3);
            if (ad8[0] > 0.0D)
            {
                for (int i4 = 1; i4 <= i; i4++)
                {
                    int j4 = i4 - 1;
                    ad4[j4] += ad8[0] * ad6[k2 + j4];
                    ad6[k1 + j4] = Math.max(ad6[k1 + j4], Math.abs(ad4[j4]));
                }

                l_l9ong(i, j, ad, k, ai, ai1, ad6, i1, j1, d, ai4[0], j3, l3);
            }
            if (ai3[0] >= i2)
                break;
            if (ad8[0] == 0.0D)
            {
                if (ad7[0] <= d)
                    break;
                l_l8ong(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1[0], ad6, k1, d, ad7, l1);
                flag = true;
                flag1 = true;
                continue;
            }
            if (l4 < ai3[0])
            {
                flag = false;
                flag1 = true;
                continue;
            }
            if (d1 == 0.0D || ad9[0] < d1)
            {
                d1 = ad9[0];
                k4 = 0;
            }
            if (++k4 <= 2)
            {
                flag = false;
                flag1 = false;
                continue;
            }
            if (ad7[0] <= d)
                break;
            l_l8ong(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1[0], ad6, k1, d, ad7, l1);
            flag = true;
            flag1 = true;
        }
        while (true);
    }

    private void l_l6onf(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            int ai[], int ai1[], double ad5[], int ai2[], double ad6[], int l, int i1, int j1, int k1, double d,
            double ad7[], int l1, int ai3[], int i2, int j2, int k2, int l2, int i3, int j3, int k3, int l3)
    {
        double ad8[] =
        {
            0.0D
        };
        double ad9[] =
        {
            0.0D
        };
        int ai4[] =
        {
            0
        };
        int l4 = 0;
        double d1 = 0.0D;
        int k4 = 0;
        ai2[0] = 0;
        boolean flag = true;
        boolean flag1 = true;
        do
        {
            if (flag)
            {
                l_l10nf(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1, ai2, ad6, i1, j1, k1, d, ad7[0], l1);
                if (ai2[0] > 0)
                    ai3[0] = ai1[0];
                if (ai3[0] == i2)
                    return;
            }
            if (flag1)
            {
                l4 = ai3[0];
                d1 = 0.0D;
            }
            l_l11nf(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1, ad5, ad6, l, i1, j1, k1, j2, k2, l2, d, ad7[0], ad8, ad9,
                    l1, ai3, i2, ai4, i3, j3, k3, l3);
            if (ad8[0] > 0.0D)
            {
                for (int i4 = 1; i4 <= i; i4++)
                {
                    int j4 = i4 - 1;
                    ad4[j4] += ad8[0] * ad6[k2 + j4];
                    ad6[k1 + j4] = Math.max(ad6[k1 + j4], Math.abs(ad4[j4]));
                }

                l_l9onf(i, j, ad, k, ai, ai1, ad6, i1, j1, d, ai4[0], j3, l3);
            }
            if (ai3[0] >= i2)
                break;
            if (ad8[0] == 0.0D)
            {
                if (ad7[0] <= d)
                    break;
                l_l8onf(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1[0], ad6, k1, d, ad7, l1);
                flag = true;
                flag1 = true;
                continue;
            }
            if (l4 < ai3[0])
            {
                flag = false;
                flag1 = true;
                continue;
            }
            if (d1 == 0.0D || ad9[0] < d1)
            {
                d1 = ad9[0];
                k4 = 0;
            }
            if (++k4 <= 2)
            {
                flag = false;
                flag1 = false;
                continue;
            }
            if (ad7[0] <= d)
                break;
            l_l8onf(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1[0], ad6, k1, d, ad7, l1);
            flag = true;
            flag1 = true;
        }
        while (true);
    }

    private void l_l7ong(Function function, Gradient gradient, int i, int j, double ad[], int k, double ad1[],
            double ad2[], double ad3[], double ad4[], double ad5[], double d, int ai[], int ai1[], double ad6[], int l,
            int ai2[], double ad7[], int i1, int j1, int k1, int l1, double d1, double ad8[], double d2, int i2,
            int j2, int ai3[], int ai4[], int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4)
    {
        double ad9[] =
        {
            0.0D
        };
        double ad10[] =
        {
            0.0D
        };
        double ad11[] =
        {
            0.0D
        };
        double ad12[] =
        {
            0.0D
        };
        double ad13[] =
        {
            0.0D
        };
        double ad14[] =
        {
            0.0D
        };
        int ai5[] =
        {
            0
        };
        int ai6[] =
        {
            0
        };
        ai5[0] = j2;
        int k5 = ai3[0];
        int j6 = ai4[0];
        ad9[0] = 0.0D;
        double d3 = 0.0D;
        if (ai4[0] == 0 || ai2[0] == 1)
        {
            ad9[0] = function.f(ad4);
            ad5[0] = ad9[0];
            double ad15[] = new double[i];
            BLAS.copy(i, ad7, i1, 1, ad15, 0, 1);
            gradient.gradient(ad4, ad15);
            BLAS.copy(i, ad15, 0, 1, ad7, i1, 1);
            ai4[0]++;
        }
        double d4 = Math.abs(ad9[0] + ad9[0] + 1.0D);
        byte byte0 = -1;
        do
        {
            do
            {
                l_l11ng(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1, ad6, ad7, i1, j1, k1, l1, i3, j3, k3, d1, d2, ad10,
                        ad11, i2, ai5, j2, ai6, l3, l2, i4, j4);
                l_l12ng(i, j, ad, k, ai, ai1[0], ad6, ad7, i1, l2, j1, k1, i3, ad12, i2, ad13, i4, j4);
                if (ad13[0] <= d * d)
                {
                    ai2[0] = 1;
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte0 = -1;
                        return;
                    }
                }
                if (ad11[0] >= 0.0D)
                {
                    ai2[0] = 2;
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte0 = -1;
                        return;
                    }
                }
                boolean flag = false;
                if (ad9[0] >= d4)
                {
                    if ((d2 == d1 || ai1[0] == 0) && d3 > 0.0D)
                        flag = true;
                    if (!flag)
                    {
                        ai2[0] = 3;
                        if (l == 0)
                        {
                            return;
                        }
                        else
                        {
                            byte0 = -1;
                            return;
                        }
                    }
                }
                d3 = d4 - ad9[0];
                d4 = ad9[0];
                if (ai4[0] == k2)
                {
                    ai2[0] = 8;
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte0 = -1;
                        return;
                    }
                }
                if (d2 > d1 && ai3[0] > k5 && 0.10000000000000001D * ad12[0] >= Math.max(d3, -0.5D * ad11[0]))
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte0 = -1;
                        return;
                    }
                if (byte0 == ai3[0])
                {
                    int l5 = ai3[0] + Math.abs(l);
                    return;
                }
                ai3[0]++;
                l_l13ng(function, gradient, i, ad4, ad7, i1, j3, i4, j4, d1, ad10[0], ad11[0], ad9, ad14, ai4, k2, i3,
                        ad5);
                if (ad14[0] == 0.0D)
                {
                    ai2[0] = 3;
                    double d5 = 0.0D;
                    for (int k4 = 1; k4 <= i; k4++)
                    {
                        int i5 = k4 - 1;
                        d5 += Math.abs(ad7[j3 + i5] * ad7[j4 + i5]);
                    }

                    if (ad11[0] + d1 * d5 >= 0.0D)
                        ai2[0] = 2;
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte byte1 = -1;
                        return;
                    }
                }
                for (int l4 = 1; l4 <= i; l4++)
                {
                    int j5 = l4 - 1;
                    ad7[l1 + j5] = Math.max(ad7[l1 + j5], Math.abs(ad4[j5]));
                }

                l_l14ng(i, ad4, ai1[0], ad7, i1, j1, k3, i4, j4, ad8);
            }
            while (ad14[0] != ad10[0]);
            int i6 = ai[ai6[0] - 1];
            if (i6 > j)
            {
                i6 -= j;
                if (i6 <= i)
                    ad4[i6 - 1] = ad2[i6 - 1];
                else
                    ad4[i6 - i - 1] = ad3[i6 - i - 1];
            }
            l_l9ong(i, j, ad, k, ai, ai1, ad7, j1, k1, d1, ai6[0], i4, j4);
        }
        while (true);
    }

    private void l_l7onf(Function function, int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[],
            double ad4[], double ad5[], double d, int ai[], int ai1[], double ad6[], int l, int ai2[], double ad7[],
            int i1, int j1, int k1, int l1, double d1, double ad8[], double d2, int i2, int j2, int ai3[], int ai4[],
            int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4)
    {
        double ad9[] =
        {
            0.0D
        };
        double ad10[] =
        {
            0.0D
        };
        double ad11[] =
        {
            0.0D
        };
        double ad12[] =
        {
            0.0D
        };
        double ad13[] =
        {
            0.0D
        };
        double ad14[] =
        {
            0.0D
        };
        int ai5[] =
        {
            0
        };
        int ai6[] =
        {
            0
        };
        ai5[0] = j2;
        int k5 = ai3[0];
        int j6 = ai4[0];
        ad9[0] = 0.0D;
        double d3 = 0.0D;
        if (ai4[0] == 0 || ai2[0] == 1)
        {
            ad9[0] = function.f(ad4);
            ad5[0] = ad9[0];
            l_l21nf(function, i, ad4, ad9[0], ad7, i1, ai4);
            ai4[0]++;
        }
        double d4 = Math.abs(ad9[0] + ad9[0] + 1.0D);
        byte byte0 = -1;
        do
        {
            do
            {
                l_l11nf(i, j, ad, k, ad1, ad2, ad3, ad4, ai, ai1, ad6, ad7, i1, j1, k1, l1, i3, j3, k3, d1, d2, ad10,
                        ad11, i2, ai5, j2, ai6, l3, l2, i4, j4);
                l_l12nf(i, j, ad, k, ai, ai1[0], ad6, ad7, i1, l2, j1, k1, i3, ad12, i2, ad13, i4, j4);
                if (ad13[0] <= d * d)
                {
                    ai2[0] = 1;
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte0 = -1;
                        return;
                    }
                }
                if (ad11[0] >= 0.0D)
                {
                    ai2[0] = 2;
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte0 = -1;
                        return;
                    }
                }
                boolean flag = false;
                if (ad9[0] >= d4)
                {
                    if ((d2 == d1 || ai1[0] == 0) && d3 > 0.0D)
                        flag = true;
                    if (!flag)
                    {
                        ai2[0] = 3;
                        if (l == 0)
                        {
                            return;
                        }
                        else
                        {
                            byte0 = -1;
                            return;
                        }
                    }
                }
                d3 = d4 - ad9[0];
                d4 = ad9[0];
                if (ai4[0] == k2)
                {
                    ai2[0] = 8;
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte0 = -1;
                        return;
                    }
                }
                if (d2 > d1 && ai3[0] > k5 && 0.10000000000000001D * ad12[0] >= Math.max(d3, -0.5D * ad11[0]))
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte0 = -1;
                        return;
                    }
                if (byte0 == ai3[0])
                {
                    int l5 = ai3[0] + Math.abs(l);
                    return;
                }
                ai3[0]++;
                l_l13nf(function, i, ad4, ad7, i1, j3, i4, j4, d1, ad10[0], ad11[0], ad9, ad14, ai4, k2, i3, ad5);
                if (ad14[0] == 0.0D)
                {
                    ai2[0] = 3;
                    double d5 = 0.0D;
                    for (int k4 = 1; k4 <= i; k4++)
                    {
                        int i5 = k4 - 1;
                        d5 += Math.abs(ad7[j3 + i5] * ad7[j4 + i5]);
                    }

                    if (ad11[0] + d1 * d5 >= 0.0D)
                        ai2[0] = 2;
                    if (l == 0)
                    {
                        return;
                    }
                    else
                    {
                        byte byte1 = -1;
                        return;
                    }
                }
                for (int l4 = 1; l4 <= i; l4++)
                {
                    int j5 = l4 - 1;
                    ad7[l1 + j5] = Math.max(ad7[l1 + j5], Math.abs(ad4[j5]));
                }

                l_l14nf(i, ad4, ai1[0], ad7, i1, j1, k3, i4, j4, ad8);
            }
            while (ad14[0] != ad10[0]);
            int i6 = ai[ai6[0] - 1];
            if (i6 > j)
            {
                i6 -= j;
                if (i6 <= i)
                    ad4[i6 - 1] = ad2[i6 - 1];
                else
                    ad4[i6 - i - 1] = ad3[i6 - i - 1];
            }
            l_l9onf(i, j, ad, k, ai, ai1, ad7, j1, k1, d1, ai6[0], i4, j4);
        }
        while (true);
    }

    private void l_l8ong(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            int ai[], int l, double ad5[], int i1, double d, double ad6[], int j1)
    {
        double d3 = 0.0D;
        if (l > j1)
        {
            int k3 = j1 + 1;
            for (int i3 = k3; i3 <= l; i3++)
            {
                int j3 = i3 - 1;
                int k2 = ai[j3];
                double d1;
                double d2;
                if (k2 <= j)
                {
                    d1 = ad1[k2 - 1];
                    d2 = Math.abs(ad1[k2 - 1]);
                    for (int k1 = 1; k1 <= i; k1++)
                    {
                        int i2 = k1 - 1;
                        d1 += -ad[i2 * k + (k2 - 1)] * ad4[i2];
                        d2 += Math.abs(ad[i2 * k + (k2 - 1)] * ad5[i1 + i2]);
                    }

                }
                else
                {
                    int l2 = k2 - j;
                    if (l2 <= i)
                    {
                        d1 = ad4[l2 - 1] - ad2[l2 - 1];
                        d2 = ad5[(i1 + l2) - 1] + Math.abs(ad2[l2 - 1]);
                    }
                    else
                    {
                        l2 -= i;
                        d1 = ad3[l2 - 1] - ad4[l2 - 1];
                        d2 = ad5[(i1 + l2) - 1] + Math.abs(ad3[l2 - 1]);
                    }
                }
                if (d1 > 0.0D)
                    d3 = Math.max(d3, d1 / d2);
            }

        }
        ad6[0] = 0.10000000000000001D * Math.min(ad6[0], d3);
        if (ad6[0] <= d + d)
        {
            ad6[0] = d;
            for (int l1 = 1; l1 <= i; l1++)
            {
                int j2 = l1 - 1;
                ad5[i1 + j2] = Math.abs(ad4[j2]);
            }

        }
    }

    private void l_l8onf(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            int ai[], int l, double ad5[], int i1, double d, double ad6[], int j1)
    {
        double d3 = 0.0D;
        if (l > j1)
        {
            int k3 = j1 + 1;
            for (int i3 = k3; i3 <= l; i3++)
            {
                int j3 = i3 - 1;
                int k2 = ai[j3];
                double d1;
                double d2;
                if (k2 <= j)
                {
                    d1 = ad1[k2 - 1];
                    d2 = Math.abs(ad1[k2 - 1]);
                    for (int k1 = 1; k1 <= i; k1++)
                    {
                        int i2 = k1 - 1;
                        d1 += -ad[i2 * k + (k2 - 1)] * ad4[i2];
                        d2 += Math.abs(ad[i2 * k + (k2 - 1)] * ad5[i1 + i2]);
                    }

                }
                else
                {
                    int l2 = k2 - j;
                    if (l2 <= i)
                    {
                        d1 = ad4[l2 - 1] - ad2[l2 - 1];
                        d2 = ad5[(i1 + l2) - 1] + Math.abs(ad2[l2 - 1]);
                    }
                    else
                    {
                        l2 -= i;
                        d1 = ad3[l2 - 1] - ad4[l2 - 1];
                        d2 = ad5[(i1 + l2) - 1] + Math.abs(ad3[l2 - 1]);
                    }
                }
                if (d1 > 0.0D)
                    d3 = Math.max(d3, d1 / d2);
            }

        }
        ad6[0] = 0.10000000000000001D * Math.min(ad6[0], d3);
        if (ad6[0] <= d + d)
        {
            ad6[0] = d;
            for (int l1 = 1; l1 <= i; l1++)
            {
                int j2 = l1 - 1;
                ad5[i1 + j2] = Math.abs(ad4[j2]);
            }

        }
    }

    private void l_l10ng(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            int ai[], int ai1[], int ai2[], double ad5[], int l, int i1, int j1, double d, double d1, int k1)
    {
        int k3 = 0;
        double d5 = 0.0D;
        if (ai1[0] == 0)
            return;
        for (int l3 = 1; l3 <= ai1[0]; l3++)
        {
            int i4 = l3 - 1;
            int j3 = ai[i4];
            double d2;
            double d3;
            double d4;
            if (j3 <= j)
            {
                d2 = ad1[j3 - 1];
                d3 = Math.abs(ad1[j3 - 1]);
                d4 = d3;
                for (int l1 = 1; l1 <= i; l1++)
                {
                    int j2 = l1 - 1;
                    double d9 = ad[j2 * k + (j3 - 1)];
                    double d7 = d9 * ad4[j2];
                    d2 -= d7;
                    d3 += Math.abs(d7);
                    d4 += Math.abs(d9) * ad5[j1 + j2];
                }

            }
            else
            {
                k3 = j3 - j;
                if (k3 <= i)
                {
                    d2 = ad4[k3 - 1] - ad2[k3 - 1];
                    d3 = Math.abs(ad4[k3 - 1]) + Math.abs(ad2[k3 - 1]);
                    d4 = ad5[(j1 + k3) - 1] + Math.abs(ad2[k3 - 1]);
                    d5 = ad2[k3 - 1];
                }
                else
                {
                    k3 -= i;
                    d2 = ad3[k3 - 1] - ad4[k3 - 1];
                    d3 = Math.abs(ad4[k3 - 1]) + Math.abs(ad3[k3 - 1]);
                    d4 = ad5[(j1 + k3) - 1] + Math.abs(ad3[k3 - 1]);
                    d5 = ad3[k3 - 1];
                }
            }
            if (d2 == 0.0D)
                continue;
            double d8 = d2 / d3;
            if (l3 <= k1)
                d8 = -Math.abs(d8);
            if (d1 == d || d8 + d < 0.0D)
            {
                ai2[0] = 1;
                double d6 = d2 * ad5[i1 + i4];
                int i3 = l3;
                for (int i2 = 1; i2 <= i; i2++)
                {
                    int k2 = i2 - 1;
                    ad4[k2] += d6 * ad5[(l + i3) - 1];
                    i3 += i;
                    ad5[j1 + k2] = Math.max(ad5[j1 + k2], Math.abs(ad4[k2]));
                }

                if (j3 > j)
                    ad4[k3 - 1] = d5;
                continue;
            }
            if (d2 / d4 > d1)
                ai[i4] = -ai[i4];
        }

        int l2 = ai1[0];
        do
            if (ai[l2 - 1] < 0)
            {
                ai[l2 - 1] = -ai[l2 - 1];
                l_l15ng(i, j, ad, k, ai, ai1, ad5, l, i1, d, l2);
            }
        while (--l2 > k1);
    }

    private void l_l10nf(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            int ai[], int ai1[], int ai2[], double ad5[], int l, int i1, int j1, double d, double d1, int k1)
    {
        int k3 = 0;
        double d5 = 0.0D;
        if (ai1[0] == 0)
            return;
        for (int l3 = 1; l3 <= ai1[0]; l3++)
        {
            int i4 = l3 - 1;
            int j3 = ai[i4];
            double d2;
            double d3;
            double d4;
            if (j3 <= j)
            {
                d2 = ad1[j3 - 1];
                d3 = Math.abs(ad1[j3 - 1]);
                d4 = d3;
                for (int l1 = 1; l1 <= i; l1++)
                {
                    int j2 = l1 - 1;
                    double d9 = ad[j2 * k + (j3 - 1)];
                    double d7 = d9 * ad4[j2];
                    d2 -= d7;
                    d3 += Math.abs(d7);
                    d4 += Math.abs(d9) * ad5[j1 + j2];
                }

            }
            else
            {
                k3 = j3 - j;
                if (k3 <= i)
                {
                    d2 = ad4[k3 - 1] - ad2[k3 - 1];
                    d3 = Math.abs(ad4[k3 - 1]) + Math.abs(ad2[k3 - 1]);
                    d4 = ad5[(j1 + k3) - 1] + Math.abs(ad2[k3 - 1]);
                    d5 = ad2[k3 - 1];
                }
                else
                {
                    k3 -= i;
                    d2 = ad3[k3 - 1] - ad4[k3 - 1];
                    d3 = Math.abs(ad4[k3 - 1]) + Math.abs(ad3[k3 - 1]);
                    d4 = ad5[(j1 + k3) - 1] + Math.abs(ad3[k3 - 1]);
                    d5 = ad3[k3 - 1];
                }
            }
            if (d2 == 0.0D)
                continue;
            double d8 = d2 / d3;
            if (l3 <= k1)
                d8 = -Math.abs(d8);
            if (d1 == d || d8 + d < 0.0D)
            {
                ai2[0] = 1;
                double d6 = d2 * ad5[i1 + i4];
                int i3 = l3;
                for (int i2 = 1; i2 <= i; i2++)
                {
                    int k2 = i2 - 1;
                    ad4[k2] += d6 * ad5[(l + i3) - 1];
                    i3 += i;
                    ad5[j1 + k2] = Math.max(ad5[j1 + k2], Math.abs(ad4[k2]));
                }

                if (j3 > j)
                    ad4[k3 - 1] = d5;
                continue;
            }
            if (d2 / d4 > d1)
                ai[i4] = -ai[i4];
        }

        int l2 = ai1[0];
        do
            if (ai[l2 - 1] < 0)
            {
                ai[l2 - 1] = -ai[l2 - 1];
                l_l15nf(i, j, ad, k, ai, ai1, ad5, l, i1, d, l2);
            }
        while (--l2 > k1);
    }

    private void l_l11ng(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            int ai[], int ai1[], double ad5[], double ad6[], int l, int i1, int j1, int k1, int l1, int i2, int j2,
            double d, double d1, double ad7[], double ad8[], int k2, int ai2[], int l2, int ai3[], int i3, int j3,
            int k3, int l3)
    {
        double ad9[] =
        {
            0.0D
        };
        int i6 = l2 - ai2[0];
        if ((double) i6 > 0.0D)
        {
            for (int i4 = 1; i4 <= i; i4++)
            {
                int i5 = i4 - 1;
                ad6[l + i5] = 0.0D;
            }

            ad8[0] = 0.0D;
        }
        int i8 = ai2[0];
        int l7 = ai1[0];
        ai2[0] = ai1[0];
        int k7 = k2 + 1;
        for (int i7 = k7; i7 <= l2; i7++)
        {
            int j7 = i7 - 1;
            int j6 = ai[j7];
            double d2;
            double d3;
            if (j6 <= j)
            {
                d2 = ad1[j6 - 1];
                d3 = Math.abs(ad1[j6 - 1]);
                for (int j4 = 1; j4 <= i; j4++)
                {
                    int j5 = j4 - 1;
                    d2 += -ad4[j5] * ad[j5 * k + (j6 - 1)];
                    d3 += Math.abs(ad6[k1 + j5] * ad[j5 * k + (j6 - 1)]);
                }

            }
            else
            {
                int k6 = j6 - j;
                if (k6 <= i)
                {
                    d2 = ad4[k6 - 1] - ad2[k6 - 1];
                    d3 = Math.abs(ad6[(k1 + k6) - 1]) + Math.abs(ad2[k6 - 1]);
                }
                else
                {
                    k6 -= i;
                    d2 = ad3[k6 - 1] - ad4[k6 - 1];
                    d3 = Math.abs(ad6[(k1 + k6) - 1]) + Math.abs(ad3[k6 - 1]);
                }
            }
            ad6[(l1 + j6) - 1] = d2;
            double d5 = 0.0D;
            if (d3 != 0.0D)
                d5 = d2 / d3;
            if (i7 > i8 && d5 < 0.0D && d5 + d >= 0.0D)
            {
                double d4;
                if (j6 <= j)
                {
                    d4 = Math.abs(ad1[j6 - 1]);
                    for (int k4 = 1; k4 <= i; k4++)
                    {
                        int k5 = k4 - 1;
                        d4 += Math.abs(ad4[k5] * ad[k5 * k + (j6 - 1)]);
                    }

                }
                else
                {
                    int l6 = j6 - j;
                    if (l6 <= i)
                        d4 = Math.abs(ad4[l6 - 1]) + Math.abs(ad2[l6 - 1]);
                    else
                        d4 = Math.abs(ad4[l6 - i - 1]) + Math.abs(ad3[l6 - i - 1]);
                }
                if (Math.abs(d2) <= d4 * d)
                    d5 = 0.0D;
            }
            boolean flag = false;
            if (i7 <= ai1[0])
                flag = true;
            if (flag)
                continue;
            if (i7 <= i8 || d5 >= 0.0D)
            {
                ai2[0]++;
                if (ai2[0] < i7)
                    ai[j7] = ai[ai2[0] - 1];
                if (d5 > d1)
                {
                    ai[ai2[0] - 1] = j6;
                }
                else
                {
                    l7++;
                    ai[ai2[0] - 1] = ai[l7 - 1];
                    ai[l7 - 1] = j6;
                }
                continue;
            }
            if (j6 <= j)
            {
                for (int l4 = 1; l4 <= i; l4++)
                {
                    int l5 = l4 - 1;
                    ad6[l + l5] += ad[l5 * k + (j6 - 1)];
                }

            }
            else
            {
                j6 -= j;
                if (j6 <= i)
                    ad6[(l + j6) - 1]--;
                else
                    ad6[(l + j6) - i - 1]++;
            }
            ad8[0] += Math.abs(d2);
        }

        ad7[0] = 0.0D;
        if (i6 > 0 && ai2[0] == l2)
            return;
        l_l16ng(i, j, ad, k, ai, ai1, ad5, ad6, l, i1, j1, i2, j2, d, ad9, k2, l7, i3, j3, k3, l3);
        if (ad9[0] < 0.0D)
            l_l17ng(i, j, ad, k, ai, ad6, l1, i2, ad7, ad9, l7, ai2, l2, ai3);
        if (i6 == 0)
            ad8[0] = ad9[0];
    }

    private void l_l11nf(int i, int j, double ad[], int k, double ad1[], double ad2[], double ad3[], double ad4[],
            int ai[], int ai1[], double ad5[], double ad6[], int l, int i1, int j1, int k1, int l1, int i2, int j2,
            double d, double d1, double ad7[], double ad8[], int k2, int ai2[], int l2, int ai3[], int i3, int j3,
            int k3, int l3)
    {
        double ad9[] =
        {
            0.0D
        };
        int i6 = l2 - ai2[0];
        if ((double) i6 > 0.0D)
        {
            for (int i4 = 1; i4 <= i; i4++)
            {
                int i5 = i4 - 1;
                ad6[l + i5] = 0.0D;
            }

            ad8[0] = 0.0D;
        }
        int i8 = ai2[0];
        int l7 = ai1[0];
        ai2[0] = ai1[0];
        int k7 = k2 + 1;
        for (int i7 = k7; i7 <= l2; i7++)
        {
            int j7 = i7 - 1;
            int j6 = ai[j7];
            double d2;
            double d3;
            if (j6 <= j)
            {
                d2 = ad1[j6 - 1];
                d3 = Math.abs(ad1[j6 - 1]);
                for (int j4 = 1; j4 <= i; j4++)
                {
                    int j5 = j4 - 1;
                    d2 += -ad4[j5] * ad[j5 * k + (j6 - 1)];
                    d3 += Math.abs(ad6[k1 + j5] * ad[j5 * k + (j6 - 1)]);
                }

            }
            else
            {
                int k6 = j6 - j;
                if (k6 <= i)
                {
                    d2 = ad4[k6 - 1] - ad2[k6 - 1];
                    d3 = Math.abs(ad6[(k1 + k6) - 1]) + Math.abs(ad2[k6 - 1]);
                }
                else
                {
                    k6 -= i;
                    d2 = ad3[k6 - 1] - ad4[k6 - 1];
                    d3 = Math.abs(ad6[(k1 + k6) - 1]) + Math.abs(ad3[k6 - 1]);
                }
            }
            ad6[(l1 + j6) - 1] = d2;
            double d5 = 0.0D;
            if (d3 != 0.0D)
                d5 = d2 / d3;
            if (i7 > i8 && d5 < 0.0D && d5 + d >= 0.0D)
            {
                double d4;
                if (j6 <= j)
                {
                    d4 = Math.abs(ad1[j6 - 1]);
                    for (int k4 = 1; k4 <= i; k4++)
                    {
                        int k5 = k4 - 1;
                        d4 += Math.abs(ad4[k5] * ad[k5 * k + (j6 - 1)]);
                    }

                }
                else
                {
                    int l6 = j6 - j;
                    if (l6 <= i)
                        d4 = Math.abs(ad4[l6 - 1]) + Math.abs(ad2[l6 - 1]);
                    else
                        d4 = Math.abs(ad4[l6 - i - 1]) + Math.abs(ad3[l6 - i - 1]);
                }
                if (Math.abs(d2) <= d4 * d)
                    d5 = 0.0D;
            }
            boolean flag = false;
            if (i7 <= ai1[0])
                flag = true;
            if (flag)
                continue;
            if (i7 <= i8 || d5 >= 0.0D)
            {
                ai2[0]++;
                if (ai2[0] < i7)
                    ai[j7] = ai[ai2[0] - 1];
                if (d5 > d1)
                {
                    ai[ai2[0] - 1] = j6;
                }
                else
                {
                    l7++;
                    ai[ai2[0] - 1] = ai[l7 - 1];
                    ai[l7 - 1] = j6;
                }
                continue;
            }
            if (j6 <= j)
            {
                for (int l4 = 1; l4 <= i; l4++)
                {
                    int l5 = l4 - 1;
                    ad6[l + l5] += ad[l5 * k + (j6 - 1)];
                }

            }
            else
            {
                j6 -= j;
                if (j6 <= i)
                    ad6[(l + j6) - 1]--;
                else
                    ad6[(l + j6) - i - 1]++;
            }
            ad8[0] += Math.abs(d2);
        }

        ad7[0] = 0.0D;
        if (i6 > 0 && ai2[0] == l2)
            return;
        l_l16nf(i, j, ad, k, ai, ai1, ad5, ad6, l, i1, j1, i2, j2, d, ad9, k2, l7, i3, j3, k3, l3);
        if (ad9[0] < 0.0D)
            l_l17nf(i, j, ad, k, ai, ad6, l1, i2, ad7, ad9, l7, ai2, l2, ai3);
        if (i6 == 0)
            ad8[0] = ad9[0];
    }

    private void l_l12ng(int i, int j, double ad[], int k, int ai[], int l, double ad1[], double ad2[], int i1, int j1,
            int k1, int l1, int i2, double ad3[], int j2, double ad4[], int k2, int l2)
    {
        double d = 0.0D;
        for (int i3 = 1; i3 <= i; i3++)
        {
            int l4 = i3 - 1;
            ad2[j1 + l4] = ad2[i1 + l4];
        }

        if (l > 0)
        {
            boolean flag = false;
            do
            {
                for (int j9 = 1; j9 <= l; j9++)
                {
                    int k9 = j9 - 1;
                    int k7 = (l + 1) - j9;
                    int l6 = ai[k7 - 1];
                    double d1 = 0.0D;
                    int k6 = k7;
                    for (int j3 = 1; j3 <= i; j3++)
                    {
                        int i5 = j3 - 1;
                        d1 += ad2[(k1 + k6) - 1] * ad2[j1 + i5];
                        k6 += i;
                    }

                    d1 *= ad2[(l1 + k7) - 1];
                    if (!flag)
                        ad1[k7 - 1] = 0.0D;
                    if (k7 <= j2 || ad1[k7 - 1] + d1 < 0.0D)
                    {
                        ad1[k7 - 1] += d1;
                    }
                    else
                    {
                        d1 = -ad1[k7 - 1];
                        ad1[k7 - 1] = 0.0D;
                    }
                    if (d1 == 0.0D)
                        continue;
                    if (l6 <= j)
                    {
                        for (int k3 = 1; k3 <= i; k3++)
                        {
                            int j5 = k3 - 1;
                            ad2[j1 + j5] += -d1 * ad[j5 * k + (l6 - 1)];
                        }

                        continue;
                    }
                    int j7 = l6 - j;
                    if (j7 <= i)
                        ad2[(j1 + j7) - 1] += d1;
                    else
                        ad2[(j1 + j7) - i - 1] -= d1;
                }

                ad4[0] = 0.0D;
                if (l == i)
                    return;
                for (int l3 = 1; l3 <= i; l3++)
                {
                    int k5 = l3 - 1;
                    ad4[0] += Math.pow(ad2[j1 + k5], 2D);
                }

                if (flag)
                    break;
                flag = true;
                for (int l7 = 1; l7 <= l; l7++)
                {
                    int k8 = l7 - 1;
                    ad2[k2 + k8] = ad1[k8];
                }

                for (int i4 = 1; i4 <= i; i4++)
                {
                    int l5 = i4 - 1;
                    ad2[l2 + l5] = ad2[j1 + l5];
                }

                d = ad4[0];
            }
            while (true);
            if (d < ad4[0])
            {
                for (int i8 = 1; i8 <= l; i8++)
                {
                    int l8 = i8 - 1;
                    ad1[l8] = ad2[k2 + l8];
                }

                for (int j4 = 1; j4 <= i; j4++)
                {
                    int i6 = j4 - 1;
                    ad2[j1 + i6] = ad2[l2 + i6];
                }

                ad4[0] = d;
            }
        }
        else
        {
            ad4[0] = 0.0D;
            for (int k4 = 1; k4 <= i; k4++)
            {
                int j6 = k4 - 1;
                ad4[0] += Math.pow(ad2[i1 + j6], 2D);
            }

        }
        ad3[0] = 0.0D;
        if (j2 < l)
        {
            int l9 = j2 + 1;
            for (int j8 = l9; j8 <= l; j8++)
            {
                int i9 = j8 - 1;
                int i7 = ai[i9];
                if (ad2[(i2 + i7) - 1] > 0.0D)
                    ad3[0] += -ad1[i9] * ad2[(i2 + i7) - 1];
            }

        }
    }

    private void l_l12nf(int i, int j, double ad[], int k, int ai[], int l, double ad1[], double ad2[], int i1, int j1,
            int k1, int l1, int i2, double ad3[], int j2, double ad4[], int k2, int l2)
    {
        double d = 0.0D;
        for (int i3 = 1; i3 <= i; i3++)
        {
            int l4 = i3 - 1;
            ad2[j1 + l4] = ad2[i1 + l4];
        }

        if (l > 0)
        {
            boolean flag = false;
            do
            {
                for (int j9 = 1; j9 <= l; j9++)
                {
                    int k9 = j9 - 1;
                    int k7 = (l + 1) - j9;
                    int l6 = ai[k7 - 1];
                    double d1 = 0.0D;
                    int k6 = k7;
                    for (int j3 = 1; j3 <= i; j3++)
                    {
                        int i5 = j3 - 1;
                        d1 += ad2[(k1 + k6) - 1] * ad2[j1 + i5];
                        k6 += i;
                    }

                    d1 *= ad2[(l1 + k7) - 1];
                    if (!flag)
                        ad1[k7 - 1] = 0.0D;
                    if (k7 <= j2 || ad1[k7 - 1] + d1 < 0.0D)
                    {
                        ad1[k7 - 1] += d1;
                    }
                    else
                    {
                        d1 = -ad1[k7 - 1];
                        ad1[k7 - 1] = 0.0D;
                    }
                    if (d1 == 0.0D)
                        continue;
                    if (l6 <= j)
                    {
                        for (int k3 = 1; k3 <= i; k3++)
                        {
                            int j5 = k3 - 1;
                            ad2[j1 + j5] += -d1 * ad[j5 * k + (l6 - 1)];
                        }

                        continue;
                    }
                    int j7 = l6 - j;
                    if (j7 <= i)
                        ad2[(j1 + j7) - 1] += d1;
                    else
                        ad2[(j1 + j7) - i - 1] -= d1;
                }

                ad4[0] = 0.0D;
                if (l == i)
                    return;
                for (int l3 = 1; l3 <= i; l3++)
                {
                    int k5 = l3 - 1;
                    ad4[0] += Math.pow(ad2[j1 + k5], 2D);
                }

                if (flag)
                    break;
                flag = true;
                for (int l7 = 1; l7 <= l; l7++)
                {
                    int k8 = l7 - 1;
                    ad2[k2 + k8] = ad1[k8];
                }

                for (int i4 = 1; i4 <= i; i4++)
                {
                    int l5 = i4 - 1;
                    ad2[l2 + l5] = ad2[j1 + l5];
                }

                d = ad4[0];
            }
            while (true);
            if (d < ad4[0])
            {
                for (int i8 = 1; i8 <= l; i8++)
                {
                    int l8 = i8 - 1;
                    ad1[l8] = ad2[k2 + l8];
                }

                for (int j4 = 1; j4 <= i; j4++)
                {
                    int i6 = j4 - 1;
                    ad2[j1 + i6] = ad2[l2 + i6];
                }

                ad4[0] = d;
            }
        }
        else
        {
            ad4[0] = 0.0D;
            for (int k4 = 1; k4 <= i; k4++)
            {
                int j6 = k4 - 1;
                ad4[0] += Math.pow(ad2[i1 + j6], 2D);
            }

        }
        ad3[0] = 0.0D;
        if (j2 < l)
        {
            int l9 = j2 + 1;
            for (int j8 = l9; j8 <= l; j8++)
            {
                int i9 = j8 - 1;
                int i7 = ai[i9];
                if (ad2[(i2 + i7) - 1] > 0.0D)
                    ad3[0] += -ad1[i9] * ad2[(i2 + i7) - 1];
            }

        }
    }

    private void l_l13ng(Function function, Gradient gradient, int i, double ad[], double ad1[], int j, int k, int l,
            int i1, double d, double d1, double d2, double ad2[], double ad3[], int ai[], int j1, int k1, double ad4[])
    {
        double d10 = 0.0D;
        double d4 = 0.0D;
        double d15 = 0.90000000000000002D;
        int j4 = 0;
        double d13 = -1D;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int i3 = l1 - 1;
            ad1[l + i3] = ad[i3];
            ad1[i1 + i3] = ad1[j + i3];
            ad1[k1 + i3] = ad1[j + i3];
            if (ad1[k + i3] == 0.0D)
                continue;
            double d21 = Math.abs(ad[i3] / ad1[k + i3]);
            if (d13 < 0.0D || d21 < d13)
                d13 = d21;
        }

        ad3[0] = Math.min(1.0D, d1);
        double d19 = Math.max(d * d13, 9.9999999999999998E-013D * ad3[0]);
        ad3[0] = Math.max(d19, ad3[0]);
        double d16 = 0.0D;
        double d9 = ad2[0];
        double d3 = d2;
        double d18 = 0.0D;
        double d11 = ad2[0];
        double d6 = d2;
        double d17 = 0.0D;
        double d20 = 0.0D;
        double d12 = ad2[0];
        double d8 = Math.abs(d2);
        label0: do
        {
            do
            {
                for (int i2 = 1; i2 <= i; i2++)
                {
                    int j3 = i2 - 1;
                    ad[j3] = ad1[l + j3] + ad3[0] * ad1[k + j3];
                }

                ad2[0] = function.f(ad);
                ad4[0] = ad2[0];
                double ad5[] = new double[i];
                BLAS.copy(i, ad1, j, 1, ad5, 0, 1);
                gradient.gradient(ad, ad5);
                BLAS.copy(i, ad5, 0, 1, ad1, j, 1);
                j4++;
                double d7 = 0.0D;
                for (int j2 = 1; j2 <= i; j2++)
                {
                    int k3 = j2 - 1;
                    d7 += ad1[k + k3] * ad1[j + k3];
                }

                if (ad2[0] <= d12 && (ad2[0] < d12 || Math.abs(d7) < d8))
                {
                    d20 = ad3[0];
                    d12 = ad2[0];
                    for (int k2 = 1; k2 <= i; k2++)
                    {
                        int l3 = k2 - 1;
                        ad1[k1 + l3] = ad1[j + l3];
                    }

                    d8 = Math.abs(d7);
                }
                if (ai[0] + j4 == j1)
                    break label0;
                boolean flag = false;
                if (ad2[0] >= d9 + 0.10000000000000001D * (ad3[0] - d16) * d3)
                {
                    if (d17 > 0.0D || ad2[0] > d9 || d7 > 0.5D * d2)
                    {
                        d17 = ad3[0];
                        d10 = ad2[0];
                        d4 = d7;
                        flag = true;
                    }
                    if (!flag)
                    {
                        d16 = ad3[0];
                        d9 = ad2[0];
                        d3 = d7;
                    }
                }
                if (!flag)
                {
                    if (d7 >= 0.69999999999999996D * d3)
                        break label0;
                    d18 = ad3[0];
                    d11 = ad2[0];
                    d6 = d7;
                }
                if (d17 > 0.0D && d18 >= d15 * d17)
                    break label0;
                if (d17 == 0.0D)
                {
                    if (ad3[0] == d1)
                        break label0;
                    double d22 = 10D;
                    if (d7 > 0.90000000000000002D * d2)
                        d22 = d2 / (d2 - d7);
                    ad3[0] = Math.min(d22 * ad3[0], d1);
                    continue;
                }
                if (j4 != 1 && d18 <= 0.0D)
                    break;
                double d5 = (2D * (d10 - d11)) / (d17 - d18) - 0.5D * (d6 + d4);
                double d14;
                if (d5 >= 0.0D)
                    d14 = Math.max(0.10000000000000001D, (0.5D * d6) / (d6 - d5));
                else
                    d14 = (0.5D * d4 - d5) / (d4 - d5);
                ad3[0] = d18 + d14 * (d17 - d18);
            }
            while (true);
            ad3[0] *= 0.10000000000000001D;
        }
        while (ad3[0] >= d19);
        if (ad3[0] != d20)
        {
            ad3[0] = d20;
            ad2[0] = d12;
            for (int l2 = 1; l2 <= i; l2++)
            {
                int i4 = l2 - 1;
                ad[i4] = ad1[l + i4] + ad3[0] * ad1[k + i4];
                ad1[j + i4] = ad1[k1 + i4];
            }

        }
        ai[0] += j4;
    }

    private void l_l13nf(Function function, int i, double ad[], double ad1[], int j, int k, int l, int i1, double d,
            double d1, double d2, double ad2[], double ad3[], int ai[], int j1, int k1, double ad4[])
    {
        int ai1[] =
        {
            0
        };
        double d10 = 0.0D;
        double d4 = 0.0D;
        double d15 = 0.90000000000000002D;
        ai1[0] = 0;
        double d13 = -1D;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int i4 = l1 - 1;
            ad1[l + i4] = ad[i4];
            ad1[i1 + i4] = ad1[j + i4];
            ad1[k1 + i4] = ad1[j + i4];
            if (ad1[k + i4] == 0.0D)
                continue;
            double d21 = Math.abs(ad[i4] / ad1[k + i4]);
            if (d13 < 0.0D || d21 < d13)
                d13 = d21;
        }

        ad3[0] = Math.min(1.0D, d1);
        double d19 = Math.max(d * d13, 9.9999999999999998E-013D * ad3[0]);
        ad3[0] = Math.max(d19, ad3[0]);
        double d16 = 0.0D;
        double d9 = ad2[0];
        double d3 = d2;
        double d18 = 0.0D;
        double d11 = ad2[0];
        double d6 = d2;
        double d17 = 0.0D;
        double d20 = 0.0D;
        double d12 = ad2[0];
        double d8 = Math.abs(d2);
        do
        {
            do
            {
                for (int i2 = 1; i2 <= i; i2++)
                {
                    int j4 = i2 - 1;
                    ad[j4] = ad1[l + j4] + ad3[0] * ad1[k + j4];
                }

                ad2[0] = function.f(ad);
                ad4[0] = ad2[0];
                l_l21nf(function, i, ad, ad2[0], ad1, j, ai1);
                ai1[0]++;
                double d7 = 0.0D;
                for (int j2 = 1; j2 <= i; j2++)
                {
                    int k4 = j2 - 1;
                    d7 += ad1[k + k4] * ad1[j + k4];
                }

                if (ad2[0] <= d12 && (ad2[0] < d12 || Math.abs(d7) < d8))
                {
                    d20 = ad3[0];
                    d12 = ad2[0];
                    for (int k2 = 1; k2 <= i; k2++)
                    {
                        int l4 = k2 - 1;
                        ad1[k1 + l4] = ad1[j + l4];
                    }

                    d8 = Math.abs(d7);
                }
                if (ai[0] + ai1[0] == j1)
                {
                    if (ad3[0] != d20)
                    {
                        ad3[0] = d20;
                        ad2[0] = d12;
                        for (int l2 = 1; l2 <= i; l2++)
                        {
                            int i5 = l2 - 1;
                            ad[i5] = ad1[l + i5] + ad3[0] * ad1[k + i5];
                            ad1[j + i5] = ad1[k1 + i5];
                        }

                    }
                    ai[0] += ai1[0];
                    return;
                }
                boolean flag = false;
                if (ad2[0] >= d9 + 0.10000000000000001D * (ad3[0] - d16) * d3)
                {
                    if (d17 > 0.0D || ad2[0] > d9 || d7 > 0.5D * d2)
                    {
                        d17 = ad3[0];
                        d10 = ad2[0];
                        d4 = d7;
                        flag = true;
                    }
                    if (!flag)
                    {
                        d16 = ad3[0];
                        d9 = ad2[0];
                        d3 = d7;
                    }
                }
                if (!flag)
                {
                    if (d7 >= 0.69999999999999996D * d3)
                    {
                        if (ad3[0] != d20)
                        {
                            ad3[0] = d20;
                            ad2[0] = d12;
                            for (int i3 = 1; i3 <= i; i3++)
                            {
                                int j5 = i3 - 1;
                                ad[j5] = ad1[l + j5] + ad3[0] * ad1[k + j5];
                                ad1[j + j5] = ad1[k1 + j5];
                            }

                        }
                        ai[0] += ai1[0];
                        return;
                    }
                    d18 = ad3[0];
                    d11 = ad2[0];
                    d6 = d7;
                }
                if (d17 > 0.0D && d18 >= d15 * d17)
                {
                    if (ad3[0] != d20)
                    {
                        ad3[0] = d20;
                        ad2[0] = d12;
                        for (int j3 = 1; j3 <= i; j3++)
                        {
                            int k5 = j3 - 1;
                            ad[k5] = ad1[l + k5] + ad3[0] * ad1[k + k5];
                            ad1[j + k5] = ad1[k1 + k5];
                        }

                    }
                    ai[0] += ai1[0];
                    return;
                }
                if (d17 == 0.0D)
                {
                    if (ad3[0] == d1)
                    {
                        if (ad3[0] != d20)
                        {
                            ad3[0] = d20;
                            ad2[0] = d12;
                            for (int k3 = 1; k3 <= i; k3++)
                            {
                                int l5 = k3 - 1;
                                ad[l5] = ad1[l + l5] + ad3[0] * ad1[k + l5];
                                ad1[j + l5] = ad1[k1 + l5];
                            }

                        }
                        ai[0] += ai1[0];
                        return;
                    }
                    double d22 = 10D;
                    if (d7 > 0.90000000000000002D * d2)
                        d22 = d2 / (d2 - d7);
                    ad3[0] = Math.min(d22 * ad3[0], d1);
                    continue;
                }
                if (ai1[0] != 1 && d18 <= 0.0D)
                    break;
                double d5 = (2D * (d10 - d11)) / (d17 - d18) - 0.5D * (d6 + d4);
                double d14;
                if (d5 >= 0.0D)
                    d14 = Math.max(0.10000000000000001D, (0.5D * d6) / (d6 - d5));
                else
                    d14 = (0.5D * d4 - d5) / (d4 - d5);
                ad3[0] = d18 + d14 * (d17 - d18);
            }
            while (true);
            ad3[0] *= 0.10000000000000001D;
        }
        while (ad3[0] >= d19);
        if (ad3[0] != d20)
        {
            ad3[0] = d20;
            ad2[0] = d12;
            for (int l3 = 1; l3 <= i; l3++)
            {
                int i6 = l3 - 1;
                ad[i6] = ad1[l + i6] + ad3[0] * ad1[k + i6];
                ad1[j + i6] = ad1[k1 + i6];
            }

        }
        ai[0] += ai1[0];
    }

    private void l_l14ng(int i, double ad[], int j, double ad1[], int k, int l, int i1, int j1, int k1, double ad2[])
    {
        double d = 0.0D;
        double d1 = 0.0D;
        double d3 = 0.0D;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int j3 = l1 - 1;
            ad1[j1 + j3] = ad[j3] - ad1[j1 + j3];
            d += Math.pow(ad1[j1 + j3], 2D);
            d3 += ad1[k1 + j3] * ad1[j1 + j3];
            ad1[k1 + j3] = ad1[k + j3] - ad1[k1 + j3];
            d1 += ad1[k1 + j3] * ad1[j1 + j3];
        }

        if (d1 < 0.10000000000000001D * Math.abs(d3))
            return;
        int k5 = i;
        do
        {
            int k6 = k5;
            if (--k5 <= j)
                break;
            if (ad1[(i1 + k6) - 1] != 0.0D)
            {
                d3 = Math.abs(ad1[(i1 + k6) - 1])
                        * Math.sqrt(1.0D + Math.pow(ad1[(i1 + k5) - 1] / ad1[(i1 + k6) - 1], 2D));
                double d5 = ad1[(i1 + k5) - 1] / d3;
                double d6 = ad1[(i1 + k6) - 1] / d3;
                ad1[(i1 + k5) - 1] = d3;
                int l4 = k5;
                int i2 = 1;
                while (i2 <= i)
                {
                    int k3 = i2 - 1;
                    d3 = d5 * ad1[l + l4] - d6 * ad1[(l + l4) - 1];
                    ad1[(l + l4) - 1] = d5 * ad1[(l + l4) - 1] + d6 * ad1[l + l4];
                    ad1[l + l4] = d3;
                    l4 += i;
                    i2++;
                }
            }
        }
        while (true);
        if (ad2[0] < 0.0D)
        {
            ad2[0] = d / d1;
        }
        else
        {
            d3 = Math.sqrt((ad2[0] * d) / d1);
            ad2[0] = Math.min(ad2[0], d3);
            ad2[0] = Math.max(ad2[0], 0.10000000000000001D * d3);
        }
        int l6 = j + 1;
        d3 = Math.sqrt(d1);
        int i5 = l6;
        for (int j2 = 1; j2 <= i; j2++)
        {
            int l3 = j2 - 1;
            ad1[(l + i5) - 1] = ad1[j1 + l3] / d3;
            i5 += i;
        }

        if (l6 < i)
        {
            int j6 = l6 + 1;
            for (int l5 = j6; l5 <= i; l5++)
            {
                int i6 = l5 - 1;
                double d4 = 0.0D;
                int j5 = l5;
                for (int k2 = 1; k2 <= i; k2++)
                {
                    int i4 = k2 - 1;
                    d4 += ad1[k1 + i4] * ad1[(l + j5) - 1];
                    j5 += i;
                }

                d4 /= d1;
                double d2 = 0.0D;
                j5 = l5;
                for (int l2 = 1; l2 <= i; l2++)
                {
                    int j4 = l2 - 1;
                    ad1[(l + j5) - 1] += -d4 * ad1[j1 + j4];
                    d2 += Math.pow(ad1[(l + j5) - 1], 2D);
                    j5 += i;
                }

                if (d2 >= ad2[0])
                    continue;
                d4 = Math.sqrt(ad2[0] / d2);
                j5 = l5;
                for (int i3 = 1; i3 <= i; i3++)
                {
                    int k4 = i3 - 1;
                    ad1[(l + j5) - 1] *= d4;
                    j5 += i;
                }

            }

        }
    }

    private void l_l14nf(int i, double ad[], int j, double ad1[], int k, int l, int i1, int j1, int k1, double ad2[])
    {
        double d = 0.0D;
        double d1 = 0.0D;
        double d3 = 0.0D;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int j3 = l1 - 1;
            ad1[j1 + j3] = ad[j3] - ad1[j1 + j3];
            d += Math.pow(ad1[j1 + j3], 2D);
            d3 += ad1[k1 + j3] * ad1[j1 + j3];
            ad1[k1 + j3] = ad1[k + j3] - ad1[k1 + j3];
            d1 += ad1[k1 + j3] * ad1[j1 + j3];
        }

        if (d1 < 0.10000000000000001D * Math.abs(d3))
            return;
        int k5 = i;
        do
        {
            int k6 = k5;
            if (--k5 <= j)
                break;
            if (ad1[(i1 + k6) - 1] != 0.0D)
            {
                d3 = Math.abs(ad1[(i1 + k6) - 1])
                        * Math.sqrt(1.0D + Math.pow(ad1[(i1 + k5) - 1] / ad1[(i1 + k6) - 1], 2D));
                double d5 = ad1[(i1 + k5) - 1] / d3;
                double d6 = ad1[(i1 + k6) - 1] / d3;
                ad1[(i1 + k5) - 1] = d3;
                int l4 = k5;
                int i2 = 1;
                while (i2 <= i)
                {
                    int k3 = i2 - 1;
                    d3 = d5 * ad1[l + l4] - d6 * ad1[(l + l4) - 1];
                    ad1[(l + l4) - 1] = d5 * ad1[(l + l4) - 1] + d6 * ad1[l + l4];
                    ad1[l + l4] = d3;
                    l4 += i;
                    i2++;
                }
            }
        }
        while (true);
        if (ad2[0] < 0.0D)
        {
            ad2[0] = d / d1;
        }
        else
        {
            d3 = Math.sqrt((ad2[0] * d) / d1);
            ad2[0] = Math.min(ad2[0], d3);
            ad2[0] = Math.max(ad2[0], 0.10000000000000001D * d3);
        }
        int l6 = j + 1;
        d3 = Math.sqrt(d1);
        int i5 = l6;
        for (int j2 = 1; j2 <= i; j2++)
        {
            int l3 = j2 - 1;
            ad1[(l + i5) - 1] = ad1[j1 + l3] / d3;
            i5 += i;
        }

        if (l6 < i)
        {
            int j6 = l6 + 1;
            for (int l5 = j6; l5 <= i; l5++)
            {
                int i6 = l5 - 1;
                double d4 = 0.0D;
                int j5 = l5;
                for (int k2 = 1; k2 <= i; k2++)
                {
                    int i4 = k2 - 1;
                    d4 += ad1[k1 + i4] * ad1[(l + j5) - 1];
                    j5 += i;
                }

                d4 /= d1;
                double d2 = 0.0D;
                j5 = l5;
                for (int l2 = 1; l2 <= i; l2++)
                {
                    int j4 = l2 - 1;
                    ad1[(l + j5) - 1] += -d4 * ad1[j1 + j4];
                    d2 += Math.pow(ad1[(l + j5) - 1], 2D);
                    j5 += i;
                }

                if (d2 >= ad2[0])
                    continue;
                d4 = Math.sqrt(ad2[0] / d2);
                j5 = l5;
                for (int i3 = 1; i3 <= i; i3++)
                {
                    int k4 = i3 - 1;
                    ad1[(l + j5) - 1] *= d4;
                    j5 += i;
                }

            }

        }
    }

    private void l_l16ng(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], double ad2[], int l,
            int i1, int j1, int k1, int l1, double d, double ad3[], int i2, int j2, int k2, int l2, int i3, int j3)
    {
        double ad4[] =
        {
            0.0D
        };
        int j6 = 0;
        boolean flag = true;
        do
        {
            if (flag)
            {
                for (int k3 = 1; k3 <= i; k3++)
                {
                    int k4 = k3 - 1;
                    ad2[k2 + k4] = ad2[l + k4];
                }

                j6 = ai1[0];
            }
            if (j6 <= 0)
                break;
            double d1 = 0.0D;
            int k5 = j6;
            for (int l3 = 1; l3 <= i; l3++)
            {
                int l4 = l3 - 1;
                d1 += ad2[(i1 + k5) - 1] * ad2[k2 + l4];
                k5 += i;
            }

            d1 *= ad2[(j1 + j6) - 1];
            if (j6 > i2 && d1 > 0.0D)
            {
                l_l15ng(i, j, ad, k, ai, ai1, ad2, i1, j1, d, j6);
                flag = true;
            }
            else
            {
                int l5 = ai[j6 - 1];
                if (l5 <= j)
                {
                    for (int i4 = 1; i4 <= i; i4++)
                    {
                        int i5 = i4 - 1;
                        ad2[k2 + i5] += -d1 * ad[i5 * k + (l5 - 1)];
                    }

                }
                else
                {
                    int i6 = l5 - j;
                    if (i6 <= i)
                        ad2[(k2 + i6) - 1] += d1;
                    else
                        ad2[(k2 + i6) - i - 1] -= d1;
                }
                ad1[j6 - 1] = d1;
                j6--;
                flag = false;
            }
        }
        while (true);
        ad3[0] = 0.0D;
        if (ai1[0] < i)
        {
            l_l18ng(i, j, ad, k, ai, ai1, ad1, ad2, i1, j1, k1, l1, k2, d, ad4, i2, j2, l2, i3, j3);
            if (ad4[0] < 0.0D)
            {
                for (int j4 = 1; j4 <= i; j4++)
                {
                    int j5 = j4 - 1;
                    ad3[0] += ad2[k1 + j5] * ad2[l + j5];
                }

            }
        }
    }

    private void l_l16nf(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], double ad2[], int l,
            int i1, int j1, int k1, int l1, double d, double ad3[], int i2, int j2, int k2, int l2, int i3, int j3)
    {
        double ad4[] =
        {
            0.0D
        };
        int j6 = 0;
        boolean flag = true;
        do
        {
            if (flag)
            {
                for (int k3 = 1; k3 <= i; k3++)
                {
                    int k4 = k3 - 1;
                    ad2[k2 + k4] = ad2[l + k4];
                }

                j6 = ai1[0];
            }
            if (j6 <= 0)
                break;
            double d1 = 0.0D;
            int k5 = j6;
            for (int l3 = 1; l3 <= i; l3++)
            {
                int l4 = l3 - 1;
                d1 += ad2[(i1 + k5) - 1] * ad2[k2 + l4];
                k5 += i;
            }

            d1 *= ad2[(j1 + j6) - 1];
            if (j6 > i2 && d1 > 0.0D)
            {
                l_l15nf(i, j, ad, k, ai, ai1, ad2, i1, j1, d, j6);
                flag = true;
            }
            else
            {
                int l5 = ai[j6 - 1];
                if (l5 <= j)
                {
                    for (int i4 = 1; i4 <= i; i4++)
                    {
                        int i5 = i4 - 1;
                        ad2[k2 + i5] += -d1 * ad[i5 * k + (l5 - 1)];
                    }

                }
                else
                {
                    int i6 = l5 - j;
                    if (i6 <= i)
                        ad2[(k2 + i6) - 1] += d1;
                    else
                        ad2[(k2 + i6) - i - 1] -= d1;
                }
                ad1[j6 - 1] = d1;
                j6--;
                flag = false;
            }
        }
        while (true);
        ad3[0] = 0.0D;
        if (ai1[0] < i)
        {
            l_l18nf(i, j, ad, k, ai, ai1, ad1, ad2, i1, j1, k1, l1, k2, d, ad4, i2, j2, l2, i3, j3);
            if (ad4[0] < 0.0D)
            {
                for (int j4 = 1; j4 <= i; j4++)
                {
                    int j5 = j4 - 1;
                    ad3[0] += ad2[k1 + j5] * ad2[l + j5];
                }

            }
        }
    }

    private void l_l17ng(int i, int j, double ad[], int k, int ai[], double ad1[], int l, int i1, double ad2[],
            double ad3[], int j1, int ai1[], int k1, int ai2[])
    {
        int j2 = 0;
        double d = 0.0D;
        boolean flag = false;
        ad2[0] = 0.0D;
        ai2[0] = 0;
        int l2 = j1;
        boolean flag2;
        boolean flag3;
        boolean flag1 = flag2 = flag3 = true;
        do
        {
            if (flag1 && ++l2 > k1)
            {
                flag1 = false;
                flag2 = false;
                flag3 = true;
            }
            if (flag2)
            {
                j2 = ai[l2 - 1];
                if (j2 <= j)
                {
                    d = 0.0D;
                    for (int l1 = 1; l1 <= i; l1++)
                    {
                        int i2 = l1 - 1;
                        d += ad1[i1 + i2] * ad[i2 * k + (j2 - 1)];
                    }

                }
                else
                {
                    int k2 = j2 - j;
                    if (k2 <= i)
                        d = -ad1[(i1 + k2) - 1];
                    else
                        d = ad1[(i1 + k2) - i - 1];
                }
                boolean flag4 = false;
                if (flag)
                {
                    flag3 = false;
                    flag4 = true;
                }
                if (!flag4)
                {
                    if (d * ad1[(l + j2) - 1] <= 0.0D)
                    {
                        ad1[(l + j2) - 1] = 0.0D;
                    }
                    else
                    {
                        ad1[(l + j2) - 1] /= d;
                        if (ad2[0] == 0.0D || ad1[(l + j2) - 1] < ad2[0])
                        {
                            ad2[0] = ad1[(l + j2) - 1];
                            ai2[0] = l2;
                        }
                    }
                    flag1 = true;
                    flag2 = true;
                    flag3 = true;
                    continue;
                }
            }
            if (flag3)
            {
                if (ai2[0] <= ai1[0])
                    return;
                flag = true;
                l2 = ai2[0];
                flag1 = false;
                flag2 = true;
                flag3 = true;
                continue;
            }
            ai1[0]++;
            ai[ai2[0] - 1] = ai[ai1[0] - 1];
            ai[ai1[0] - 1] = j2;
            ad1[(l + j2) - 1] = 0.0D;
            ai2[0] = ai1[0];
            ad3[0] -= d;
            if (ad3[0] >= 0.0D || ai1[0] >= k1)
                break;
            double d1 = 0.0D;
            int j3 = j1 + 1;
            for (l2 = j3; l2 <= k1; l2++)
            {
                int i3 = l2 - 1;
                j2 = ai[i3];
                if (ad1[(l + j2) - 1] > 0.0D && (d1 == 0.0D || ad1[(l + j2) - 1] < d1))
                {
                    d1 = ad1[(l + j2) - 1];
                    ai2[0] = l2;
                }
            }

            if (d1 <= 0.0D)
                break;
            ad2[0] = d1;
            flag1 = false;
            flag2 = false;
            flag3 = true;
        }
        while (true);
    }

    private void l_l17nf(int i, int j, double ad[], int k, int ai[], double ad1[], int l, int i1, double ad2[],
            double ad3[], int j1, int ai1[], int k1, int ai2[])
    {
        int j2 = 0;
        double d = 0.0D;
        boolean flag = false;
        ad2[0] = 0.0D;
        ai2[0] = 0;
        int l2 = j1;
        boolean flag2;
        boolean flag3;
        boolean flag1 = flag2 = flag3 = true;
        do
        {
            if (flag1 && ++l2 > k1)
            {
                flag1 = false;
                flag2 = false;
                flag3 = true;
            }
            if (flag2)
            {
                j2 = ai[l2 - 1];
                if (j2 <= j)
                {
                    d = 0.0D;
                    for (int l1 = 1; l1 <= i; l1++)
                    {
                        int i2 = l1 - 1;
                        d += ad1[i1 + i2] * ad[i2 * k + (j2 - 1)];
                    }

                }
                else
                {
                    int k2 = j2 - j;
                    if (k2 <= i)
                        d = -ad1[(i1 + k2) - 1];
                    else
                        d = ad1[(i1 + k2) - i - 1];
                }
                boolean flag4 = false;
                if (flag)
                {
                    flag3 = false;
                    flag4 = true;
                }
                if (!flag4)
                {
                    if (d * ad1[(l + j2) - 1] <= 0.0D)
                    {
                        ad1[(l + j2) - 1] = 0.0D;
                    }
                    else
                    {
                        ad1[(l + j2) - 1] /= d;
                        if (ad2[0] == 0.0D || ad1[(l + j2) - 1] < ad2[0])
                        {
                            ad2[0] = ad1[(l + j2) - 1];
                            ai2[0] = l2;
                        }
                    }
                    flag1 = true;
                    flag2 = true;
                    flag3 = true;
                    continue;
                }
            }
            if (flag3)
            {
                if (ai2[0] <= ai1[0])
                    return;
                flag = true;
                l2 = ai2[0];
                flag1 = false;
                flag2 = true;
                flag3 = true;
                continue;
            }
            ai1[0]++;
            ai[ai2[0] - 1] = ai[ai1[0] - 1];
            ai[ai1[0] - 1] = j2;
            ad1[(l + j2) - 1] = 0.0D;
            ai2[0] = ai1[0];
            ad3[0] -= d;
            if (ad3[0] >= 0.0D || ai1[0] >= k1)
                break;
            double d1 = 0.0D;
            int j3 = j1 + 1;
            for (l2 = j3; l2 <= k1; l2++)
            {
                int i3 = l2 - 1;
                j2 = ai[i3];
                if (ad1[(l + j2) - 1] > 0.0D && (d1 == 0.0D || ad1[(l + j2) - 1] < d1))
                {
                    d1 = ad1[(l + j2) - 1];
                    ai2[0] = l2;
                }
            }

            if (d1 <= 0.0D)
                break;
            ad2[0] = d1;
            flag1 = false;
            flag2 = false;
            flag3 = true;
        }
        while (true);
    }

    private void l_l18ng(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], double ad2[], int l,
            int i1, int j1, int k1, int l1, double d, double ad3[], int i2, int j2, int k2, int l2, int i3)
    {
        boolean flag = false;
        int j5 = 0;
        int l8 = i2 + 1;
        double d1 = 0.0D;
        boolean flag1 = true;
        boolean flag5 = true;
        do
        {
            l_l19ng(i, ai1[0], ad2, l, j1, k1, l1, d, ad3);
            if (ad3[0] == 0.0D)
                return;
            if (ai1[0] == j2)
                return;
            int i9 = ai1[0] + 1;
            double d3 = 0.0D;
            for (int l5 = i9; l5 <= i; l5++)
            {
                int j6 = l5 - 1;
                d3 += Math.pow(ad2[k1 + j6], 2D);
            }

            if (d1 > 0.0D && d3 >= d1)
            {
                if (flag)
                    return;
                flag = true;
            }
            else
            {
                d1 = d3;
                flag = false;
            }
            int l6 = ai1[0];
            l_l20ng(i, j, ad, k, ai, ai1, ad2, l, i1, j1, d, j2, k2, l2, i3);
            if (ai1[0] == l6)
                return;
            ad1[ai1[0] - 1] = 0.0D;
            for (int j3 = 1; j3 <= i; j3++)
            {
                int j4 = j3 - 1;
                ad2[k2 + j4] = ad2[l1 + j4];
            }

            l6 = ai1[0];
            double d4 = 0.0D;
            int k5 = l6;
            for (int k3 = 1; k3 <= i; k3++)
            {
                int k4 = k3 - 1;
                d4 += ad2[(l + k5) - 1] * ad2[k2 + k4];
                k5 += i;
            }

            d4 *= ad2[(i1 + l6) - 1];
            ad2[(l2 + l6) - 1] = ad1[l6 - 1] + d4;
            if (l6 == ai1[0])
                ad2[(l2 + l6) - 1] = Math.min(ad2[(l2 + l6) - 1], 0.0D);
            int i6 = ai[l6 - 1];
            if (i6 <= j)
            {
                for (int l3 = 1; l3 <= i; l3++)
                {
                    int l4 = l3 - 1;
                    ad2[k2 + l4] += -d4 * ad[l4 * k + (i6 - 1)];
                }

            }
            else
            {
                int k6 = i6 - j;
                if (k6 <= i)
                    ad2[(k2 + k6) - 1] += d4;
                else
                    ad2[(k2 + k6) - i - 1] -= d4;
            }
            if (--l6 > i2)
            {
                boolean flag2 = false;
                boolean flag6 = false;
            }
            else
            {
                double d2 = 0.0D;
                if (l8 < ai1[0])
                {
                    int k8 = ai1[0] - 1;
                    for (int i7 = l8; i7 <= k8; i7++)
                    {
                        int l7 = i7 - 1;
                        if (ad2[l2 + l7] > 0.0D)
                        {
                            d2 = ad2[l2 + l7] / (ad2[l2 + l7] - ad1[l7]);
                            j5 = i7;
                        }
                    }

                }
                double d5 = 1.0D - d2;
                for (int j7 = l8; j7 <= ai1[0]; j7++)
                {
                    int i8 = j7 - 1;
                    ad1[i8] = Math.min(d5 * ad2[l2 + i8] + d2 * ad1[i8], 0.0D);
                }

                for (int i4 = 1; i4 <= i; i4++)
                {
                    int i5 = i4 - 1;
                    ad2[l1 + i5] = d5 * ad2[k2 + i5] + d2 * ad2[l1 + i5];
                }

                if (d2 > 0.0D)
                {
                    l_l15ng(i, j, ad, k, ai, ai1, ad2, l, i1, d, j5);
                    for (int k7 = j5; k7 <= ai1[0]; k7++)
                    {
                        int j8 = k7 - 1;
                        ad1[j8] = ad1[j8 + 1];
                    }

                    boolean flag3 = false;
                    boolean flag7 = true;
                }
                else if (ai1[0] < i)
                {
                    boolean flag4 = true;
                    boolean flag8 = true;
                }
                else
                {
                    ad3[0] = 0.0D;
                    return;
                }
            }
        }
        while (true);
    }

    private void l_l18nf(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], double ad2[], int l,
            int i1, int j1, int k1, int l1, double d, double ad3[], int i2, int j2, int k2, int l2, int i3)
    {
        boolean flag = false;
        int l6 = 0;
        int j5 = 0;
        int i8 = i2 + 1;
        double d1 = 0.0D;
        boolean flag1 = true;
        boolean flag2 = true;
        do
        {
            if (flag1)
            {
                l_l19nf(i, ai1[0], ad2, l, j1, k1, l1, d, ad3);
                if (ad3[0] == 0.0D)
                    return;
                if (ai1[0] == j2)
                    return;
                int j8 = ai1[0] + 1;
                double d3 = 0.0D;
                for (int l5 = j8; l5 <= i; l5++)
                {
                    int j6 = l5 - 1;
                    d3 += Math.pow(ad2[k1 + j6], 2D);
                }

                if (d1 > 0.0D && d3 >= d1)
                {
                    if (flag)
                        return;
                    flag = true;
                }
                else
                {
                    d1 = d3;
                    flag = false;
                }
                l6 = ai1[0];
                l_l20nf(i, j, ad, k, ai, ai1, ad2, l, i1, j1, d, j2, k2, l2, i3);
                if (ai1[0] == l6)
                    return;
                ad1[ai1[0] - 1] = 0.0D;
            }
            if (flag2)
            {
                for (int j3 = 1; j3 <= i; j3++)
                {
                    int j4 = j3 - 1;
                    ad2[k2 + j4] = ad2[l1 + j4];
                }

                l6 = ai1[0];
            }
            double d4 = 0.0D;
            int k5 = l6;
            for (int k3 = 1; k3 <= i; k3++)
            {
                int k4 = k3 - 1;
                d4 += ad2[(l + k5) - 1] * ad2[k2 + k4];
                k5 += i;
            }

            d4 *= ad2[(i1 + l6) - 1];
            ad2[(l2 + l6) - 1] = ad1[l6 - 1] + d4;
            if (l6 == ai1[0])
                ad2[(l2 + l6) - 1] = Math.min(ad2[(l2 + l6) - 1], 0.0D);
            int i6 = ai[l6 - 1];
            if (i6 <= j)
            {
                for (int l3 = 1; l3 <= i; l3++)
                {
                    int l4 = l3 - 1;
                    ad2[k2 + l4] += -d4 * ad[l4 * k + (i6 - 1)];
                }

            }
            else
            {
                int k6 = i6 - j;
                if (k6 <= i)
                    ad2[(k2 + k6) - 1] += d4;
                else
                    ad2[(k2 + k6) - i - 1] -= d4;
            }
            if (--l6 > i2)
            {
                flag1 = false;
                flag2 = false;
            }
            else
            {
                double d2 = 0.0D;
                if (i8 < ai1[0])
                {
                    int l7 = ai1[0] - 1;
                    for (l6 = i8; l6 <= l7; l6++)
                    {
                        int i7 = l6 - 1;
                        if (ad2[l2 + i7] > 0.0D)
                        {
                            d2 = ad2[l2 + i7] / (ad2[l2 + i7] - ad1[i7]);
                            j5 = l6;
                        }
                    }

                }
                double d5 = 1.0D - d2;
                for (l6 = i8; l6 <= ai1[0]; l6++)
                {
                    int j7 = l6 - 1;
                    ad1[j7] = Math.min(d5 * ad2[l2 + j7] + d2 * ad1[j7], 0.0D);
                }

                for (int i4 = 1; i4 <= i; i4++)
                {
                    int i5 = i4 - 1;
                    ad2[l1 + i5] = d5 * ad2[k2 + i5] + d2 * ad2[l1 + i5];
                }

                if (d2 > 0.0D)
                {
                    l_l15nf(i, j, ad, k, ai, ai1, ad2, l, i1, d, j5);
                    for (l6 = j5; l6 <= ai1[0]; l6++)
                    {
                        int k7 = l6 - 1;
                        ad1[k7] = ad1[k7 + 1];
                    }

                    flag1 = false;
                    flag2 = true;
                }
                else if (ai1[0] < i)
                {
                    flag1 = true;
                    flag2 = true;
                }
                else
                {
                    ad3[0] = 0.0D;
                    return;
                }
            }
        }
        while (true);
    }

    private void l_l15ng(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], int l, int i1, double d,
            int j1)
    {
        int j5 = 0;
        int i4 = 0;
        int j6 = ai1[0] - 1;
        if (j1 == ai1[0])
        {
            ai1[0] = j6;
            return;
        }
        int j4 = ai[j1 - 1];
        for (int k5 = j1; k5 <= j6; k5++)
        {
            int l5 = k5 - 1;
            int i6 = k5 + 1;
            int l3 = ai[i6 - 1];
            ai[l5] = l3;
            double d2;
            if (l3 <= j)
            {
                d2 = 0.0D;
                int k4 = k5;
                for (int k1 = 1; k1 <= i; k1++)
                {
                    int k2 = k1 - 1;
                    d2 += ad1[(l + k4) - 1] * ad[k2 * k + (l3 - 1)];
                    k4 += i;
                }

            }
            else
            {
                int k3 = l3 - j;
                if (k3 <= i)
                {
                    j5 = k3 * i - i;
                    d2 = -ad1[l + j5 + l5];
                }
                else
                {
                    k3 -= i;
                    j5 = k3 * i - i;
                    d2 = ad1[l + j5 + l5];
                }
            }
            double d9 = ad1[(i1 + i6) - 1];
            double d4 = d2 * d9;
            double d1 = Math.abs(d4);
            if (d1 * d < 1.0D)
                d1 = Math.sqrt(1.0D + d1 * d1);
            double d10 = d4 / d1;
            double d12 = 1.0D / d1;
            int l4 = k5;
            if (l3 > j)
            {
                for (int l1 = 1; l1 <= i; l1++)
                {
                    int l2 = l1 - 1;
                    double d5 = d10 * ad1[l + l4] - d12 * ad1[(l + l4) - 1];
                    ad1[(l + l4) - 1] = d10 * ad1[(l + l4) - 1] + d12 * ad1[l + l4];
                    ad1[l + l4] = d5;
                    l4 += i;
                }

                ad1[(l + j5 + i6) - 1] = 0.0D;
            }
            else
            {
                double d11 = 0.0D;
                for (int i2 = 1; i2 <= i; i2++)
                {
                    int i3 = i2 - 1;
                    double d7 = d10 * ad1[l + l4];
                    double d8 = d12 * ad1[(l + l4) - 1];
                    double d6 = Math.abs(ad[i3 * k + (l3 - 1)]) * (Math.abs(d7) + Math.abs(d8));
                    if (d6 > d11)
                    {
                        d11 = d6;
                        i4 = i2;
                    }
                    ad1[(l + l4) - 1] = d10 * ad1[(l + l4) - 1] + d12 * ad1[l + l4];
                    ad1[l + l4] = d7 - d8;
                    l4 += i;
                }

                double d3 = 0.0D;
                l4 = i6;
                for (int j2 = 1; j2 <= i; j2++)
                {
                    int j3 = j2 - 1;
                    d3 += ad1[(l + l4) - 1] * ad[j3 * k + (l3 - 1)];
                    l4 += i;
                }

                if (d3 != 0.0D)
                {
                    int i5 = (i4 * i - i) + i6;
                    ad1[(l + i5) - 1] += -d3 / ad[(i4 - 1) * k + (l3 - 1)];
                }
            }
            ad1[(i1 + i6) - 1] = -d1 * ad1[i1 + l5];
            ad1[i1 + l5] = d9 / d1;
        }

        ai[ai1[0] - 1] = j4;
        ai1[0] = j6;
    }

    private void l_l15nf(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], int l, int i1, double d,
            int j1)
    {
        int j5 = 0;
        int i4 = 0;
        int j6 = ai1[0] - 1;
        if (j1 == ai1[0])
        {
            ai1[0] = j6;
            return;
        }
        int j4 = ai[j1 - 1];
        for (int k5 = j1; k5 <= j6; k5++)
        {
            int l5 = k5 - 1;
            int i6 = k5 + 1;
            int l3 = ai[i6 - 1];
            ai[l5] = l3;
            double d2;
            if (l3 <= j)
            {
                d2 = 0.0D;
                int k4 = k5;
                for (int k1 = 1; k1 <= i; k1++)
                {
                    int k2 = k1 - 1;
                    d2 += ad1[(l + k4) - 1] * ad[k2 * k + (l3 - 1)];
                    k4 += i;
                }

            }
            else
            {
                int k3 = l3 - j;
                if (k3 <= i)
                {
                    j5 = k3 * i - i;
                    d2 = -ad1[l + j5 + l5];
                }
                else
                {
                    k3 -= i;
                    j5 = k3 * i - i;
                    d2 = ad1[l + j5 + l5];
                }
            }
            double d9 = ad1[(i1 + i6) - 1];
            double d4 = d2 * d9;
            double d1 = Math.abs(d4);
            if (d1 * d < 1.0D)
                d1 = Math.sqrt(1.0D + d1 * d1);
            double d10 = d4 / d1;
            double d12 = 1.0D / d1;
            int l4 = k5;
            if (l3 > j)
            {
                for (int l1 = 1; l1 <= i; l1++)
                {
                    int l2 = l1 - 1;
                    double d5 = d10 * ad1[l + l4] - d12 * ad1[(l + l4) - 1];
                    ad1[(l + l4) - 1] = d10 * ad1[(l + l4) - 1] + d12 * ad1[l + l4];
                    ad1[l + l4] = d5;
                    l4 += i;
                }

                ad1[(l + j5 + i6) - 1] = 0.0D;
            }
            else
            {
                double d11 = 0.0D;
                for (int i2 = 1; i2 <= i; i2++)
                {
                    int i3 = i2 - 1;
                    double d7 = d10 * ad1[l + l4];
                    double d8 = d12 * ad1[(l + l4) - 1];
                    double d6 = Math.abs(ad[i3 * k + (l3 - 1)]) * (Math.abs(d7) + Math.abs(d8));
                    if (d6 > d11)
                    {
                        d11 = d6;
                        i4 = i2;
                    }
                    ad1[(l + l4) - 1] = d10 * ad1[(l + l4) - 1] + d12 * ad1[l + l4];
                    ad1[l + l4] = d7 - d8;
                    l4 += i;
                }

                double d3 = 0.0D;
                l4 = i6;
                for (int j2 = 1; j2 <= i; j2++)
                {
                    int j3 = j2 - 1;
                    d3 += ad1[(l + l4) - 1] * ad[j3 * k + (l3 - 1)];
                    l4 += i;
                }

                if (d3 != 0.0D)
                {
                    int i5 = (i4 * i - i) + i6;
                    ad1[(l + i5) - 1] += -d3 / ad[(i4 - 1) * k + (l3 - 1)];
                }
            }
            ad1[(i1 + i6) - 1] = -d1 * ad1[i1 + l5];
            ad1[i1 + l5] = d9 / d1;
        }

        ai[ai1[0] - 1] = j4;
        ai1[0] = j6;
    }

    private void l_l19ng(int i, int j, double ad[], int k, int l, int i1, int j1, double d, double ad1[])
    {
        ad1[0] = 0.0D;
        if (j >= i)
            return;
        int k4 = j + 1;
        for (int k3 = k4; k3 <= i; k3++)
        {
            int i4 = k3 - 1;
            double d1 = 0.0D;
            double d3 = 0.0D;
            int i3 = k3;
            for (int k1 = 1; k1 <= i; k1++)
            {
                int j2 = k1 - 1;
                double d6 = ad[(k + i3) - 1] * ad[j1 + j2];
                d1 += d6;
                d3 += Math.abs(d6);
                i3 += i;
            }

            if (Math.abs(d1) <= d * d3)
                d1 = 0.0D;
            ad[i1 + i4] = d1;
        }

        int j3 = 0;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int k2 = l1 - 1;
            double d2 = 0.0D;
            double d4 = 0.0D;
            for (int l3 = k4; l3 <= i; l3++)
            {
                int j4 = l3 - 1;
                double d7 = ad[k + j3 + j4] * ad[i1 + j4];
                d2 -= d7;
                d4 += Math.abs(d7);
            }

            if (Math.abs(d2) <= d * d4)
                d2 = 0.0D;
            ad[l + k2] = d2;
            j3 += i;
        }

        double d5 = 0.0D;
        for (int i2 = 1; i2 <= i; i2++)
        {
            int l2 = i2 - 1;
            double d8 = ad[l + l2] * ad[j1 + l2];
            ad1[0] += d8;
            d5 += Math.abs(d8);
        }

        if (ad1[0] + d * d5 >= 0.0D)
            ad1[0] = 0.0D;
    }

    private void l_l9ong(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], int l, int i1, double d,
            int j1, int k1, int l1)
    {
        int k5 = 0;
        int l6 = 0;
        int k8 = ai1[0] + 1;
        int i5 = ai[j1 - 1];
        ai[j1 - 1] = ai[k8 - 1];
        ai[k8 - 1] = i5;
        if (i5 > j)
        {
            int j5 = i5 - j;
            double d4;
            if (j5 <= i)
            {
                d4 = -1D;
            }
            else
            {
                j5 -= i;
                d4 = 1.0D;
            }
            l6 = j5 * i - i;
            for (int i7 = 1; i7 <= i; i7++)
            {
                int l7 = i7 - 1;
                ad1[k1 + l7] = d4 * ad1[l + l6 + l7];
            }

        }
        else
        {
            for (int i2 = 1; i2 <= i; i2++)
            {
                int k3 = i2 - 1;
                ad1[l1 + k3] = ad[k3 * k + (i5 - 1)];
            }

            for (int j7 = 1; j7 <= i; j7++)
            {
                int i8 = j7 - 1;
                ad1[k1 + i8] = 0.0D;
                int l5 = j7;
                for (int j2 = 1; j2 <= i; j2++)
                {
                    int l3 = j2 - 1;
                    ad1[k1 + i8] += ad1[(l + l5) - 1] * ad1[l1 + l3];
                    l5 += i;
                }

            }

        }
        int k7 = i;
        do
        {
            int j8 = k7;
            if (--k7 <= ai1[0])
                break;
            if (ad1[(k1 + j8) - 1] != 0.0D)
            {
                double d5;
                if (Math.abs(ad1[(k1 + j8) - 1]) <= d * Math.abs(ad1[(k1 + k7) - 1]))
                    d5 = Math.abs(ad1[(k1 + k7) - 1]);
                else if (Math.abs(ad1[(k1 + k7) - 1]) <= d * Math.abs(ad1[(k1 + j8) - 1]))
                    d5 = Math.abs(ad1[(k1 + j8) - 1]);
                else
                    d5 = Math.abs(ad1[(k1 + j8) - 1])
                            * Math.sqrt(1.0D + Math.pow(ad1[(k1 + k7) - 1] / ad1[(k1 + j8) - 1], 2D));
                double d11 = ad1[(k1 + k7) - 1] / d5;
                double d13 = ad1[(k1 + j8) - 1] / d5;
                ad1[(k1 + k7) - 1] = d5;
                int i6 = k7;
                if (i5 > j)
                {
                    for (int k2 = 1; k2 <= i; k2++)
                    {
                        int i4 = k2 - 1;
                        double d6 = d11 * ad1[l + i6] - d13 * ad1[(l + i6) - 1];
                        ad1[(l + i6) - 1] = d11 * ad1[(l + i6) - 1] + d13 * ad1[l + i6];
                        ad1[l + i6] = d6;
                        i6 += i;
                    }

                    ad1[(l + l6 + j8) - 1] = 0.0D;
                }
                else
                {
                    double d12 = 0.0D;
                    for (int l2 = 1; l2 <= i; l2++)
                    {
                        int j4 = l2 - 1;
                        double d9 = d11 * ad1[l + i6];
                        double d10 = d13 * ad1[(l + i6) - 1];
                        double d7 = Math.abs(ad1[l1 + j4]) * (Math.abs(d9) + Math.abs(d10));
                        if (d7 > d12)
                        {
                            d12 = d7;
                            k5 = l2;
                        }
                        ad1[(l + i6) - 1] = d11 * ad1[(l + i6) - 1] + d13 * ad1[l + i6];
                        ad1[l + i6] = d9 - d10;
                        i6 += i;
                    }

                    double d1 = 0.0D;
                    i6 = j8;
                    for (int i3 = 1; i3 <= i; i3++)
                    {
                        int k4 = i3 - 1;
                        d1 += ad1[(l + i6) - 1] * ad1[l1 + k4];
                        i6 += i;
                    }

                    if (d1 != 0.0D)
                    {
                        int j6 = (k5 * i - i) + j8;
                        ad1[(l + j6) - 1] += -d1 / ad1[(l1 + k5) - 1];
                    }
                }
            }
        }
        while (true);
        if (ad1[(k1 + k8) - 1] == 0.0D)
            return;
        if (i5 <= j)
        {
            double d2 = 0.0D;
            double d3 = 0.0D;
            int k6 = k8;
            for (int j3 = 1; j3 <= i; j3++)
            {
                int l4 = j3 - 1;
                double d8 = ad1[(l + k6) - 1] * ad1[l1 + l4];
                d2 += d8;
                d3 += Math.abs(d8);
                k6 += i;
            }

            if (Math.abs(d2) <= d * d3)
                return;
        }
        ad1[(i1 + k8) - 1] = 1.0D / ad1[(k1 + k8) - 1];
        ai1[0] = k8;
    }

    private void l_l9onf(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], int l, int i1, double d,
            int j1, int k1, int l1)
    {
        int k5 = 0;
        int l6 = 0;
        int k8 = ai1[0] + 1;
        int i5 = ai[j1 - 1];
        ai[j1 - 1] = ai[k8 - 1];
        ai[k8 - 1] = i5;
        if (i5 > j)
        {
            int j5 = i5 - j;
            double d4;
            if (j5 <= i)
            {
                d4 = -1D;
            }
            else
            {
                j5 -= i;
                d4 = 1.0D;
            }
            l6 = j5 * i - i;
            for (int i7 = 1; i7 <= i; i7++)
            {
                int l7 = i7 - 1;
                ad1[k1 + l7] = d4 * ad1[l + l6 + l7];
            }

        }
        else
        {
            for (int i2 = 1; i2 <= i; i2++)
            {
                int k3 = i2 - 1;
                ad1[l1 + k3] = ad[k3 * k + (i5 - 1)];
            }

            for (int j7 = 1; j7 <= i; j7++)
            {
                int i8 = j7 - 1;
                ad1[k1 + i8] = 0.0D;
                int l5 = j7;
                for (int j2 = 1; j2 <= i; j2++)
                {
                    int l3 = j2 - 1;
                    ad1[k1 + i8] += ad1[(l + l5) - 1] * ad1[l1 + l3];
                    l5 += i;
                }

            }

        }
        int k7 = i;
        do
        {
            int j8 = k7;
            if (--k7 <= ai1[0])
                break;
            if (ad1[(k1 + j8) - 1] != 0.0D)
            {
                double d5;
                if (Math.abs(ad1[(k1 + j8) - 1]) <= d * Math.abs(ad1[(k1 + k7) - 1]))
                    d5 = Math.abs(ad1[(k1 + k7) - 1]);
                else if (Math.abs(ad1[(k1 + k7) - 1]) <= d * Math.abs(ad1[(k1 + j8) - 1]))
                    d5 = Math.abs(ad1[(k1 + j8) - 1]);
                else
                    d5 = Math.abs(ad1[(k1 + j8) - 1])
                            * Math.sqrt(1.0D + Math.pow(ad1[(k1 + k7) - 1] / ad1[(k1 + j8) - 1], 2D));
                double d11 = ad1[(k1 + k7) - 1] / d5;
                double d13 = ad1[(k1 + j8) - 1] / d5;
                ad1[(k1 + k7) - 1] = d5;
                int i6 = k7;
                if (i5 > j)
                {
                    for (int k2 = 1; k2 <= i; k2++)
                    {
                        int i4 = k2 - 1;
                        double d6 = d11 * ad1[l + i6] - d13 * ad1[(l + i6) - 1];
                        ad1[(l + i6) - 1] = d11 * ad1[(l + i6) - 1] + d13 * ad1[l + i6];
                        ad1[l + i6] = d6;
                        i6 += i;
                    }

                    ad1[(l + l6 + j8) - 1] = 0.0D;
                }
                else
                {
                    double d12 = 0.0D;
                    for (int l2 = 1; l2 <= i; l2++)
                    {
                        int j4 = l2 - 1;
                        double d9 = d11 * ad1[l + i6];
                        double d10 = d13 * ad1[(l + i6) - 1];
                        double d7 = Math.abs(ad1[l1 + j4]) * (Math.abs(d9) + Math.abs(d10));
                        if (d7 > d12)
                        {
                            d12 = d7;
                            k5 = l2;
                        }
                        ad1[(l + i6) - 1] = d11 * ad1[(l + i6) - 1] + d13 * ad1[l + i6];
                        ad1[l + i6] = d9 - d10;
                        i6 += i;
                    }

                    double d1 = 0.0D;
                    i6 = j8;
                    for (int i3 = 1; i3 <= i; i3++)
                    {
                        int k4 = i3 - 1;
                        d1 += ad1[(l + i6) - 1] * ad1[l1 + k4];
                        i6 += i;
                    }

                    if (d1 != 0.0D)
                    {
                        int j6 = (k5 * i - i) + j8;
                        ad1[(l + j6) - 1] += -d1 / ad1[(l1 + k5) - 1];
                    }
                }
            }
        }
        while (true);
        if (ad1[(k1 + k8) - 1] == 0.0D)
            return;
        if (i5 <= j)
        {
            double d2 = 0.0D;
            double d3 = 0.0D;
            int k6 = k8;
            for (int j3 = 1; j3 <= i; j3++)
            {
                int l4 = j3 - 1;
                double d8 = ad1[(l + k6) - 1] * ad1[l1 + l4];
                d2 += d8;
                d3 += Math.abs(d8);
                k6 += i;
            }

            if (Math.abs(d2) <= d * d3)
                return;
        }
        ad1[(i1 + k8) - 1] = 1.0D / ad1[(k1 + k8) - 1];
        ai1[0] = k8;
    }

    private void l_l19nf(int i, int j, double ad[], int k, int l, int i1, int j1, double d, double ad1[])
    {
        ad1[0] = 0.0D;
        if (j >= i)
            return;
        int k4 = j + 1;
        for (int k3 = k4; k3 <= i; k3++)
        {
            int i4 = k3 - 1;
            double d1 = 0.0D;
            double d3 = 0.0D;
            int i3 = k3;
            for (int k1 = 1; k1 <= i; k1++)
            {
                int j2 = k1 - 1;
                double d6 = ad[(k + i3) - 1] * ad[j1 + j2];
                d1 += d6;
                d3 += Math.abs(d6);
                i3 += i;
            }

            if (Math.abs(d1) <= d * d3)
                d1 = 0.0D;
            ad[i1 + i4] = d1;
        }

        int j3 = 0;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int k2 = l1 - 1;
            double d2 = 0.0D;
            double d4 = 0.0D;
            for (int l3 = k4; l3 <= i; l3++)
            {
                int j4 = l3 - 1;
                double d7 = ad[k + j3 + j4] * ad[i1 + j4];
                d2 -= d7;
                d4 += Math.abs(d7);
            }

            if (Math.abs(d2) <= d * d4)
                d2 = 0.0D;
            ad[l + k2] = d2;
            j3 += i;
        }

        double d5 = 0.0D;
        for (int i2 = 1; i2 <= i; i2++)
        {
            int l2 = i2 - 1;
            double d8 = ad[l + l2] * ad[j1 + l2];
            ad1[0] += d8;
            d5 += Math.abs(d8);
        }

        if (ad1[0] + d * d5 >= 0.0D)
            ad1[0] = 0.0D;
    }

    private void l_l20ng(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], int l, int i1, int j1,
            double d, int k1, int l1, int i2, int j2)
    {
        int i6 = 0;
        double d4 = 0.0D;
        double d3 = 0.0D;
        int j10 = ai1[0] + 1;
        int i10 = k1;
        int j6 = 0;
        for (int k2 = 1; k2 <= i; k2++)
        {
            int j4 = k2 - 1;
            ad1[l1 + j4] = 0.0D;
            for (int l6 = j10; l6 <= i; l6++)
            {
                int l7 = l6 - 1;
                ad1[l1 + j4] += Math.pow(ad1[l + j6 + l7], 2D);
            }

            j6 += i;
        }

        do
        {
            double d2 = 0.0D;
            for (int l8 = j10; l8 <= i10; l8++)
            {
                int l9 = l8 - 1;
                int i7 = ai[l9];
                double d5;
                double d7;
                double d9;
                if (i7 <= j)
                {
                    d5 = 0.0D;
                    d7 = 0.0D;
                    d9 = 0.0D;
                    for (int l2 = 1; l2 <= i; l2++)
                    {
                        int k4 = l2 - 1;
                        double d10 = ad1[j1 + k4] * ad[k4 * k + (i7 - 1)];
                        d5 += d10;
                        d7 += Math.abs(d10);
                        d9 += ad1[l1 + k4] * Math.pow(ad[k4 * k + (i7 - 1)], 2D);
                    }

                }
                else
                {
                    int i8 = i7 - j;
                    if (i8 <= i)
                    {
                        d5 = -ad1[(j1 + i8) - 1];
                    }
                    else
                    {
                        i8 -= i;
                        d5 = ad1[(j1 + i8) - 1];
                    }
                    d7 = Math.abs(d5);
                    d9 = ad1[(l1 + i8) - 1];
                }
                if (d5 <= d * d7)
                    continue;
                double d1 = (d5 * d5) / d9;
                if (d1 > d2)
                {
                    d2 = d1;
                    i6 = l8;
                    d4 = d5;
                    d3 = d7;
                }
            }

            if (d2 <= 0.0D)
                return;
            boolean flag = false;
            if (ai1[0] == 0)
                flag = true;
            if (!flag)
            {
                flag = true;
                int j7 = ai[i6 - 1];
                int k8;
                if (j7 <= j)
                {
                    k8 = 0;
                    for (int i3 = 1; i3 <= i; i3++)
                    {
                        int l4 = i3 - 1;
                        ad1[i2 + l4] = ad[l4 * k + (j7 - 1)];
                    }

                }
                else
                {
                    k8 = j7 - j;
                    for (int j3 = 1; j3 <= i; j3++)
                    {
                        int i5 = j3 - 1;
                        ad1[i2 + i5] = 0.0D;
                    }

                    if (k8 <= i)
                    {
                        ad1[(i2 + k8) - 1] = -1D;
                    }
                    else
                    {
                        k8 -= i;
                        ad1[(i2 + k8) - 1] = 1.0D;
                    }
                }
                int i9 = ai1[0];
                do
                {
                    double d11 = 0.0D;
                    int k6 = i9;
                    for (int k3 = 1; k3 <= i; k3++)
                    {
                        int j5 = k3 - 1;
                        d11 += ad1[(l + k6) - 1] * ad1[i2 + j5];
                        k6 += i;
                    }

                    d11 *= ad1[(i1 + i9) - 1];
                    int k7 = ai[i9 - 1];
                    if (k7 <= j)
                    {
                        for (int l3 = 1; l3 <= i; l3++)
                        {
                            int k5 = l3 - 1;
                            ad1[i2 + k5] += -d11 * ad[k5 * k + (k7 - 1)];
                        }

                    }
                    else
                    {
                        int j8 = k7 - j;
                        if (j8 <= i)
                            ad1[(i2 + j8) - 1] += d11;
                        else
                            ad1[(i2 + j8) - i - 1] -= d11;
                    }
                    double d6 = 0.0D;
                    double d8 = 0.0D;
                    for (int i4 = 1; i4 <= i; i4++)
                    {
                        int l5 = i4 - 1;
                        double d12 = ad1[j1 + l5] * ad1[i2 + l5];
                        d6 += d12;
                        d8 += Math.abs(d12);
                    }

                    d4 = Math.min(d4, d6);
                    d3 = Math.max(d3, d8);
                }
                while (--i9 >= 1);
                if (k8 > 0)
                    ad1[(j1 + k8) - 1] = 0.0D;
                if (d4 <= d * d3)
                    flag = false;
            }
            if (flag)
            {
                int j9 = ai1[0];
                l_l9ong(i, j, ad, k, ai, ai1, ad1, l, i1, d, i6, i2, j2);
                if (ai1[0] > j9)
                    return;
                i6 = j10;
            }
            if (j10 < i10)
            {
                int k9 = ai[i10 - 1];
                ai[i10 - 1] = ai[i6 - 1];
                ai[i6 - 1] = k9;
                i10--;
            }
            else
            {
                return;
            }
        }
        while (true);
    }

    private void l_l20nf(int i, int j, double ad[], int k, int ai[], int ai1[], double ad1[], int l, int i1, int j1,
            double d, int k1, int l1, int i2, int j2)
    {
        int i6 = 0;
        double d4 = 0.0D;
        double d3 = 0.0D;
        int j10 = ai1[0] + 1;
        int i10 = k1;
        int j6 = 0;
        for (int k2 = 1; k2 <= i; k2++)
        {
            int j4 = k2 - 1;
            ad1[l1 + j4] = 0.0D;
            for (int l6 = j10; l6 <= i; l6++)
            {
                int l7 = l6 - 1;
                ad1[l1 + j4] += Math.pow(ad1[l + j6 + l7], 2D);
            }

            j6 += i;
        }

        do
        {
            double d2 = 0.0D;
            for (int l8 = j10; l8 <= i10; l8++)
            {
                int l9 = l8 - 1;
                int i7 = ai[l9];
                double d5;
                double d7;
                double d9;
                if (i7 <= j)
                {
                    d5 = 0.0D;
                    d7 = 0.0D;
                    d9 = 0.0D;
                    for (int l2 = 1; l2 <= i; l2++)
                    {
                        int k4 = l2 - 1;
                        double d10 = ad1[j1 + k4] * ad[k4 * k + (i7 - 1)];
                        d5 += d10;
                        d7 += Math.abs(d10);
                        d9 += ad1[l1 + k4] * Math.pow(ad[k4 * k + (i7 - 1)], 2D);
                    }

                }
                else
                {
                    int i8 = i7 - j;
                    if (i8 <= i)
                    {
                        d5 = -ad1[(j1 + i8) - 1];
                    }
                    else
                    {
                        i8 -= i;
                        d5 = ad1[(j1 + i8) - 1];
                    }
                    d7 = Math.abs(d5);
                    d9 = ad1[(l1 + i8) - 1];
                }
                if (d5 <= d * d7)
                    continue;
                double d1 = (d5 * d5) / d9;
                if (d1 > d2)
                {
                    d2 = d1;
                    i6 = l8;
                    d4 = d5;
                    d3 = d7;
                }
            }

            if (d2 <= 0.0D)
                return;
            boolean flag = false;
            if (ai1[0] == 0)
                flag = true;
            if (!flag)
            {
                flag = true;
                int j7 = ai[i6 - 1];
                int k8;
                if (j7 <= j)
                {
                    k8 = 0;
                    for (int i3 = 1; i3 <= i; i3++)
                    {
                        int l4 = i3 - 1;
                        ad1[i2 + l4] = ad[l4 * k + (j7 - 1)];
                    }

                }
                else
                {
                    k8 = j7 - j;
                    for (int j3 = 1; j3 <= i; j3++)
                    {
                        int i5 = j3 - 1;
                        ad1[i2 + i5] = 0.0D;
                    }

                    if (k8 <= i)
                    {
                        ad1[(i2 + k8) - 1] = -1D;
                    }
                    else
                    {
                        k8 -= i;
                        ad1[(i2 + k8) - 1] = 1.0D;
                    }
                }
                int i9 = ai1[0];
                do
                {
                    double d11 = 0.0D;
                    int k6 = i9;
                    for (int k3 = 1; k3 <= i; k3++)
                    {
                        int j5 = k3 - 1;
                        d11 += ad1[(l + k6) - 1] * ad1[i2 + j5];
                        k6 += i;
                    }

                    d11 *= ad1[(i1 + i9) - 1];
                    int k7 = ai[i9 - 1];
                    if (k7 <= j)
                    {
                        for (int l3 = 1; l3 <= i; l3++)
                        {
                            int k5 = l3 - 1;
                            ad1[i2 + k5] += -d11 * ad[k5 * k + (k7 - 1)];
                        }

                    }
                    else
                    {
                        int j8 = k7 - j;
                        if (j8 <= i)
                            ad1[(i2 + j8) - 1] += d11;
                        else
                            ad1[(i2 + j8) - i - 1] -= d11;
                    }
                    double d6 = 0.0D;
                    double d8 = 0.0D;
                    for (int i4 = 1; i4 <= i; i4++)
                    {
                        int l5 = i4 - 1;
                        double d12 = ad1[j1 + l5] * ad1[i2 + l5];
                        d6 += d12;
                        d8 += Math.abs(d12);
                    }

                    d4 = Math.min(d4, d6);
                    d3 = Math.max(d3, d8);
                }
                while (--i9 >= 1);
                if (k8 > 0)
                    ad1[(j1 + k8) - 1] = 0.0D;
                if (d4 <= d * d3)
                    flag = false;
            }
            if (flag)
            {
                int j9 = ai1[0];
                l_l9onf(i, j, ad, k, ai, ai1, ad1, l, i1, d, i6, i2, j2);
                if (ai1[0] > j9)
                    return;
                i6 = j10;
            }
            if (j10 < i10)
            {
                int k9 = ai[i10 - 1];
                ai[i10 - 1] = ai[i6 - 1];
                ai[i6 - 1] = k9;
                i10--;
            }
            else
            {
                return;
            }
        }
        while (true);
    }

    private void l_l21nf(Function function, int i, double ad[], double d, double ad1[], int j, int ai[])
    {
        double ad2[] =
        {
            0.0D
        };
        double d1 = Math.sqrt(Math.max(0.0D, 2.2204460492503131E-016D));
        for (int k = 1; k <= i; k++)
        {
            int l = k - 1;
            double d2 = d1 * Math.max(Math.abs(ad[l]), 1.0D);
            if (ad[l] < 0.0D)
                d2 = -d2;
            double d3 = ad[l];
            ad[l] = d3 + d2;
            ad2[0] = function.f(ad);
            ai[0]++;
            ad[l] = d3;
            ad1[j + l] = (ad2[0] - d) / d2;
        }

    }

    /**
     * @deprecated Method getLagrangeMultiplerEst is deprecated
     */

    public double[] getLagrangeMultiplerEst()
    {
        if (l_nact > 0)
        {
            double ad[] = new double[l_nact];
            BLAS.copy(l_nact, alamda_user, ad);
            return (double[]) ad.clone();
        }
        else
        {
            return null;
        }
    }

    public double[] getLagrangeMultiplierEst()
    {
        if (l_nact > 0)
        {
            double ad[] = new double[l_nact];
            BLAS.copy(l_nact, alamda_user, ad);
            return (double[]) ad.clone();
        }
        else
        {
            return null;
        }
    }

    public double getObjectiveValue()
    {
        return l_obj;
    }

    public void setTolerance(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "tolerance", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new ConditionalException("setTolerance : Tolerance must be positive.");
        }
        l_tol = d;
    }

    public int getFinalActiveConstraintsNum()
    {
        return l_nact;
    }

    public double[] getSolution()
    {
        if (lv_value == null)
            return null;
        else
            return (double[]) lv_value.clone();
    }

    public int[] getFinalActiveConstraints()
    {
        if (iact_user == null)
            return null;
        else
            return (int[]) iact_user.clone();
    }

    public void setGuess(double ad[])
    {
        if (ad.length != l_nvar)
        {
            Object aobj[] =
            {
                    "guess", new Integer(ad.length), new Integer(l_nvar)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotEqual", aobj);
            throw new ConditionalException("setGuess : NotEqual -> guess = " + ad.length + " & l_nvar = " + l_nvar);
        }
        user_xguess = 1;
        l_xguess = (double[]) ad.clone();
    }

}