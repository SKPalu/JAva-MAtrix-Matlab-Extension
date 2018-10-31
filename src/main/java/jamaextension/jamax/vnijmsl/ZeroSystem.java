/*
 * ZeroSystem.java
 *
 * Created on 31 March 2007, 16:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

import java.io.Serializable;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.ConditionalRuleException;

// Referenced classes of package com.imsl.math:
//            Array2, BLAS

public class ZeroSystem implements Serializable, Cloneable
{

    public static class TooManyIterationsException extends ConditionalRuleException
    {

        static final long serialVersionUID = 0x515ecd282859097bL;

        public TooManyIterationsException()
        {
            super("TooManyIterationsException.Message");
        }

        public TooManyIterationsException(String s, Object aobj[])
        {
            super(aobj);
        }

        public TooManyIterationsException(Object aobj[])
        {
            super(aobj);
        }
    }

    public static class ToleranceTooSmallException extends ConditionalRuleException
    {

        static final long serialVersionUID = 0xdb0488dd4dbc172eL;

        public ToleranceTooSmallException(String s, Object aobj[])
        {
            super(aobj);
        }
    }

    public static interface Jacobian extends Function
    {

        public abstract void jacobian(double ad[], double ad1[][]);
    }

    public static interface Function
    {

        public abstract void f(double ad[], double ad1[]);
    }

    public static class DidNotConvergeException extends ConditionalRuleException
    {

        static final long serialVersionUID = 0x2418ff52fe8d540eL;

        public DidNotConvergeException(String s)
        {
            super(s);
        }

        public DidNotConvergeException(String s, Object aobj[])
        {
            super(aobj);
        }
    }

    static final long serialVersionUID = 0xf1c2161a87e28c55L;

    private int n;
    private double x[];
    private double errorRelative;
    private int maxIterations;
    private double xinit[];
    private transient Function objectF;
    private transient double fnorm;
    private transient double delta;
    private transient boolean sing;
    private static final double EPSILON_SMALL = 1.1102230246251999E-016D;
    private static final double EPSILON_SMALL_SQRT = 1.0536712127723701E-008D;

    public ZeroSystem(int i)
    {
        n = i;
        maxIterations = 200;
        xinit = null;
        errorRelative = 1.0536712127723701E-008D;
    }

    public void setGuess(double ad[])
    {
        xinit = (double[]) ad.clone();
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

    private static double sqr(double d)
    {
        return d * d;
    }

    public synchronized double[] solve(Function function) throws TooManyIterationsException,
            ToleranceTooSmallException, DidNotConvergeException
    {
        double d = 100D;
        double ad[] = new double[n];
        double ad1[] = new double[n * n];
        double ad2[] = new double[(n * (n + 1)) / 2];
        double ad3[] = new double[n];
        double ad4[][] = new double[5][n];
        Array2 array2 = new Array2(ad1, n);
        objectF = function;
        int i = n - 1;
        int j = n - 1;
        byte byte0 = 2;
        BLAS.set(n, 1.0D, ad4[0], 0);
        if (xinit != null)
            x = (double[]) xinit.clone();
        else
            x = new double[n];
        solve(n, x, ad, ad1, ad2, ad3, i, j, byte0, d, ad4[0], ad4[1], ad4[2], ad4[3], ad4[4]);
        fnorm = BLAS.dot(n, ad, 0, ad, 0);
        return x;
    }

    private void solve(int i, double ad[], double ad1[], double ad2[], double ad3[], double ad4[], int j, int k, int l,
            double d, double ad5[], double ad6[], double ad7[], double ad8[], double ad9[])
            throws TooManyIterationsException, ToleranceTooSmallException, DidNotConvergeException
    {
        int k1;
        int k4;
        int l4;
        int i5;
        int j5;
        double d11;
        Array2 array2;
        d11 = 0.0D;
        array2 = new Array2(ad2, i);
        objectF.f(ad, ad1);
        fnorm = BLAS.nrm2(i, ad1, 0);
        int j4 = Math.min(j + k + 1, i);
        k1 = 1;
        l4 = 0;
        k4 = 0;
        i5 = 0;
        j5 = 0;
        // _L2:
        boolean flag = true;
        if (objectF instanceof Jacobian)
        {
            double ad10[][] = new double[i][i];
            ((Jacobian) objectF).jacobian(ad, ad10);
            for (int k5 = 0; k5 < i; k5++)
            {
                for (int l5 = 0; l5 < i; l5++)
                    array2.set(l5, k5, ad10[k5][l5]);

            }

        }
        else
        {
            computeJacobian(i, ad, ad1, ad2, j, k, ad6, ad7);
        }
        computeQR(i, i, ad2, false, null, ad6, ad7, ad8);
        if (k1 == 1)
        {
            if (l != 2)
            {
                BLAS.copy(i, ad7, ad5);
                for (int l1 = 0; l1 < i; l1++)
                    if (ad7[l1] == 0.0D)
                        ad5[l1] = 1.0D;

            }
            for (int i2 = 0; i2 < i; i2++)
                ad8[i2] = ad5[i2] * ad[i2];

            d11 = BLAS.nrm2(i, ad8, 0);
            delta = d * d11;
            if (delta == 0.0D)
                delta = d;
        }
        BLAS.copy(i, ad1, ad4);
        for (int j2 = 0; j2 < i; j2++)
            if (array2.get(j2, j2) != 0.0D)
            {
                double d6 = BLAS.dot(i - j2, ad2, (i + 1) * j2, ad4, j2);
                double d9 = -d6 / array2.get(j2, j2);
                BLAS.axpy(i - j2, d9, ad2, (i + 1) * j2, ad4, j2);
            }

        sing = false;
        for (int k2 = 0; k2 < i; k2++)
        {
            int l3 = k2;
            if (k2 > 0)
            {
                for (int i1 = 0; i1 < k2; i1++)
                {
                    ad3[l3] = array2.get(k2, i1);
                    l3 += i - i1 - 1;
                }

            }
            ad3[l3] = ad6[k2];
            if (ad6[k2] == 0.0D)
                sing = true;
        }

        l_n6qnf(i, i, ad2, ad6);
        if (l != 2)
        {
            for (int l2 = 0; l2 < i; l2++)
                ad5[l2] = Math.max(ad5[l2], ad7[l2]);

        }
        do
        {
            computeDirectionP(i, ad3, ad5, ad4, ad6, ad7, ad8);
            BLAS.scal(i, -1D, ad6, 0);
            for (int i3 = 0; i3 < i; i3++)
            {
                ad7[i3] = ad[i3] + ad6[i3];
                ad8[i3] = ad5[i3] * ad6[i3];
            }

            double d3 = BLAS.nrm2(i, ad8, 0);
            if (k1 == 1)
                delta = Math.min(delta, d3);
            objectF.f(ad7, ad9);
            double d2 = BLAS.nrm2(i, ad9, 0);
            double d1 = -1D;
            if (d2 < fnorm)
                d1 = 1.0D - sqr(d2 / fnorm);
            int i4 = 1;
            for (int j1 = 0; j1 < i; j1++)
            {
                double d7 = BLAS.dot(i - j1, ad3, i4 - 1, ad6, j1);
                i4 += i - j1;
                ad8[j1] = ad4[j1] + d7;
            }

            double d10 = BLAS.nrm2(i, ad8, 0);
            double d4 = 1.0D;
            if (d10 < fnorm)
                d4 = 1.0D - sqr(d10 / fnorm);
            double d5 = 0.0D;
            if (d4 > 0.0D)
                d5 = d1 / d4;
            if (d5 >= 0.10000000000000001D)
            {
                k4 = 0;
                l4++;
                if (d5 >= 0.5D || l4 > 1)
                    delta = Math.max(delta, d3 / 0.5D);
                if (Math.abs(d5 - 1.0D) <= 0.10000000000000001D)
                    delta = d3 / 0.5D;
            }
            else
            {
                l4 = 0;
                k4++;
                delta *= 0.5D;
            }
            if (d5 >= 0.0001D)
            {
                BLAS.copy(i, ad7, ad);
                BLAS.copy(i, ad9, ad1);
                for (int j3 = 0; j3 < i; j3++)
                    ad7[j3] = ad5[j3] * ad[j3];

                d11 = BLAS.nrm2(i, ad7, 0);
                fnorm = d2;
                if (k1++ > maxIterations)
                {
                    Object aobj[] =
                    {
                        new Integer(maxIterations)
                    };
                    throw new TooManyIterationsException(aobj);
                }
            }
            i5++;
            if (d1 >= 0.001D)
                i5 = 0;
            if (flag)
                j5++;
            if (d1 >= 0.10000000000000001D)
                j5 = 0;
            if (delta <= errorRelative * d11 || fnorm == 0.0D)
                return;
            if (0.10000000000000001D * Math.max(0.10000000000000001D * delta, d3) <= 1.1102230246251999E-016D * d11)
            {
                Object aobj1[] =
                {
                    new Double(errorRelative)
                };
                throw new ToleranceTooSmallException("ZeroSystem.ErrorTooSmall", aobj1);
            }
            if (j5 == 5 || i5 == 10)
                throw new DidNotConvergeException("ZeroSystem.DidNotConverge", null);
            if (k4 == 2)
                continue;
            for (int k3 = 0; k3 < i; k3++)
            {
                double d8 = BLAS.dot(i, ad2, k3 * i, ad9, 0);
                ad7[k3] = (d8 - ad8[k3]) / d3;
                ad6[k3] = ad5[k3] * ((ad5[k3] * ad6[k3]) / d3);
                if (d5 >= 0.0001D)
                    ad4[k3] = d8;
            }

            updateR(i, i, ad3, ad6, ad7, ad8);
            updateQ(i, i, ad2, i, ad7, ad8);
            updateQ(1, i, ad4, 1, ad7, ad8);
            flag = false;
        }
        while (true);
        // if(true) goto _L2; else goto _L1
        // _L1:
    }

    private static int DOCNT(int i, int j, int k)
    {
        int l = ((j - i) + k) / k;
        return l <= 0 ? 0 : l;
    }

    private void computeJacobian(int i, double ad[], double ad1[], double ad2[], int j, int k, double ad3[],
            double ad4[])
    {
        Array2 array2 = new Array2(ad2, i);
        int l = j + k + 1;
        if (l >= i)
        {
            for (int i1 = 0; i1 < i; i1++)
            {
                double d3 = ad[i1];
                double d = 1.0536712127723701E-008D * Math.abs(d3);
                if (d == 0.0D)
                    d = 1.0536712127723701E-008D;
                ad[i1] = d3 + d;
                objectF.f(ad, ad3);
                ad[i1] = d3;
                for (int k1 = 0; k1 < i; k1++)
                    array2.set(i1, k1, (ad3[k1] - ad1[k1]) / d);

            }

        }
        else
        {
            for (int j1 = 1; j1 <= l; j1++)
            {
                int j2 = j1;
                int l1;
                for (int k2 = DOCNT(j1, i, l1 = l); k2 > 0; k2--)
                {
                    ad4[j2 - 1] = ad[j2 - 1];
                    double d1 = 1.0536712127723701E-008D * Math.abs(ad4[j2 - 1]);
                    if (d1 == 0.0D)
                        d1 = 1.0536712127723701E-008D;
                    ad[j2 - 1] = ad4[j2 - 1] + d1;
                    j2 += l1;
                }

                objectF.f(ad, ad3);
                j2 = j1;
                int i2;
                for (int l2 = DOCNT(j1, i, i2 = l); l2 > 0; l2--)
                {
                    ad[j2 - 1] = ad4[j2 - 1];
                    double d2 = 1.0536712127723701E-008D * Math.abs(ad4[j2 - 1]);
                    if (d2 == 0.0D)
                        d2 = 1.0536712127723701E-008D;
                    for (int i3 = 1; i3 <= i; i3++)
                    {
                        array2.set(j2 - 1, i3 - 1, 0.0D);
                        if (i3 >= j2 - k && i3 <= j2 + j)
                            array2.set(j2 - 1, i3 - 1, (ad3[i3 - 1] - ad1[i3 - 1]) / d2);
                    }

                    j2 += i2;
                }

            }

        }
    }

    private void computeQR(int i, int j, double ad[], boolean flag, int ai[], double ad1[], double ad2[], double ad3[])
    {
        Array2 array2 = new Array2(ad, j);
        for (int k = 0; k < j; k++)
        {
            ad2[k] = BLAS.nrm2(i, ad, j * k);
            if (flag)
                ai[k] = k;
        }

        BLAS.copy(j, ad2, ad1);
        BLAS.copy(j, ad1, ad3);
        int l1 = Math.min(i, j);
        for (int l = 0; l < l1; l++)
        {
            if (flag)
            {
                int k2 = l;
                for (int i1 = l; i1 < j; i1++)
                    if (ad1[i1] > ad1[k2])
                        k2 = i1;

                if (k2 != l)
                {
                    for (int i2 = 0; i2 < i; i2++)
                    {
                        double d3 = array2.get(l, i2);
                        array2.set(l, i2, array2.get(k2, i2));
                        array2.set(k2, i2, d3);
                    }

                    ad1[k2] = ad1[l];
                    ad3[k2] = ad3[l];
                    int j1 = ai[l];
                    ai[l] = ai[k2];
                    ai[k2] = j1;
                }
            }
            double d = BLAS.nrm2(i - l, ad, (j + 1) * l);
            if (d != 0.0D)
            {
                if (array2.get(l, l) < 0.0D)
                    d = -d;
                for (int j2 = l; j2 < i; j2++)
                    array2.overEqual(l, j2, d);

                array2.plusEqual(l, l, 1.0D);
                if (j >= l + 2)
                {
                    for (int k1 = l + 1; k1 < j; k1++)
                    {
                        double d1 = BLAS.dot(i - l, ad, l * j + l, ad, k1 * j + l);
                        double d2 = d1 / array2.get(l, l);
                        BLAS.axpy(i - l, -d2, ad, (j + 1) * l, ad, k1 * j + l);
                        if (!flag || ad1[k1] == 0.0D)
                            continue;
                        d2 = array2.get(k1, l) / ad1[k1];
                        ad1[k1] *= Math.sqrt(Math.max(0.0D, 1.0D - d2 * d2));
                        if (0.050000000000000003D * sqr(ad1[k1] / ad3[k1]) <= 1.1102230246251999E-016D)
                        {
                            ad1[k1] = BLAS.nrm2(i - l - 1, ad, j * k1 + l + 1);
                            ad3[k1] = ad1[k1];
                        }
                    }

                }
            }
            ad1[l] = -d;
        }

    }

    private void l_n6qnf(int i, int j, double ad[], double ad1[])
    {
        Array2 array2 = new Array2(ad, j);
        int k1 = Math.min(i, j);
        if (k1 >= 2)
        {
            for (int k = 1; k < k1; k++)
                BLAS.set(k, 0.0D, ad, j * k);

        }
        if (i >= j + 1)
        {
            for (int l = j; l < i; l++)
            {
                BLAS.set(i, 0.0D, ad, j * l);
                array2.set(l, l, 1.0D);
            }

        }
        for (int i2 = 0; i2 < k1; i2++)
        {
            int j1 = k1 - i2 - 1;
            for (int l1 = j1; l1 < i; l1++)
                ad1[l1] = array2.get(j1, l1);

            BLAS.set(i - j1, 0.0D, ad, j * j1 + j1);
            array2.set(j1, j1, 1.0D);
            if (ad1[j1] == 0.0D)
                continue;
            for (int i1 = j1; i1 < i; i1++)
            {
                double d = BLAS.dot(i - j1, ad, j * i1 + j1, ad1, j1);
                double d1 = d / ad1[j1];
                BLAS.axpy(i - j1, -d1, ad1, j1, ad, j * i1 + j1);
            }

        }

    }

    private void computeDirectionP(int i, double ad[], double ad1[], double ad2[], double ad3[], double ad4[],
            double ad5[])
    {
        int i2 = (i * (i + 1)) / 2 + 1;
        for (int j2 = 0; j2 < i; j2++)
        {
            int k = i - j2;
            i2 -= j2 + 1;
            int k2 = i2 + 1;
            double d5 = BLAS.dot(i - k, ad, k2 - 1, ad3, k);
            double d7 = ad[i2 - 1];
            if (d7 == 0.0D)
            {
                int l2 = k;
                for (int j = 0; j < k; j++)
                {
                    d7 = Math.max(d7, Math.abs(ad[l2 - 1]));
                    l2 += i - j - 1;
                }

                d7 *= 1.1102230246251999E-016D;
                if (d7 == 0.0D)
                    d7 = 1.1102230246251999E-016D;
            }
            ad3[k - 1] = (ad2[k - 1] - d5) / d7;
        }

        BLAS.set(i, 0.0D, ad4, 0);
        for (int l = 0; l < i; l++)
            ad5[l] = ad1[l] * ad3[l];

        double d3 = BLAS.nrm2(i, ad5, 0);
        if (d3 > delta)
        {
            int i3 = 1;
            for (int i1 = 0; i1 < i; i1++)
            {
                double d8 = ad2[i1];
                BLAS.axpy(i - i1, d8, ad, i3 - 1, ad4, i1);
                i3 += i - i1;
                ad4[i1] /= ad1[i1];
            }

            double d2 = BLAS.nrm2(i, ad4, 0);
            double d4 = 0.0D;
            double d = delta / d3;
            if (d2 != 0.0D)
            {
                for (int j1 = 0; j1 < i; j1++)
                    ad4[j1] = ad4[j1] / d2 / ad1[j1];

                int j3 = 1;
                for (int k1 = 0; k1 < i; k1++)
                {
                    double d6 = BLAS.dot(i - k1, ad, j3 - 1, ad4, k1);
                    j3 += i - k1;
                    ad5[k1] = d6;
                }

                double d9 = BLAS.nrm2(i, ad5, 0);
                d4 = d2 / d9 / d9;
                d = 0.0D;
                if (d4 < delta)
                {
                    double d1 = BLAS.nrm2(i, ad2, 0);
                    double d10 = (d1 / d2) * (d1 / d3) * (d4 / delta);
                    d10 += -(delta / d3) * sqr(d4 / delta)
                            + Math.sqrt(sqr(d10 - delta / d3) + (1.0D - sqr(delta / d3)) * (1.0D - sqr(d4 / delta)));
                    d = ((delta / d3) * (1.0D - sqr(d4 / delta))) / d10;
                }
            }
            double d11 = (1.0D - d) * Math.min(d4, delta);
            for (int l1 = 0; l1 < i; l1++)
                ad3[l1] = d11 * ad4[l1] + d * ad3[l1];

        }
    }

    private void updateR(int i, int j, double ad[], double ad1[], double ad2[], double ad3[])
    {
        int l1 = (j * ((2 * i - j) + 1)) / 2 - (i - j);
        for (int k2 = j - 1; k2 < i; k2++)
            ad3[k2] = ad[(l1 + k2) - j];

        for (int j1 = j - 2; j1 >= 0; j1--)
        {
            l1 -= i - j1;
            ad3[j1] = 0.0D;
            if (ad2[j1] == 0.0D)
                continue;
            double d;
            double d4;
            double d8;
            if (Math.abs(ad2[j - 1]) < Math.abs(ad2[j1]))
            {
                double d6 = ad2[j - 1] / ad2[j1];
                d8 = 0.5D / Math.sqrt(0.25D + 0.25D * d6 * d6);
                d4 = d8 * d6;
                d = 1.0D;
                if (Math.abs(d4) * 1.7976931348623157E+308D > 1.0D)
                    d = 1.0D / d4;
            }
            else
            {
                double d10 = ad2[j1] / ad2[j - 1];
                d4 = 0.5D / Math.sqrt(0.25D + 0.25D * d10 * d10);
                d8 = d4 * d10;
                d = d8;
            }
            ad2[j - 1] = d8 * ad2[j1] + d4 * ad2[j - 1];
            ad2[j1] = d;
            int i2 = l1 - 1;
            for (int k = j1; k < i; k++)
            {
                double d2 = d4 * ad[i2] - d8 * ad3[k];
                ad3[k] = d8 * ad[i2] + d4 * ad3[k];
                ad[i2] = d2;
                i2++;
            }

        }

        for (int l = 0; l < i; l++)
            ad3[l] += ad2[j - 1] * ad1[l];

        sing = false;
        for (int k1 = 0; k1 < j - 1; k1++)
        {
            if (ad3[k1] != 0.0D)
            {
                double d1;
                double d5;
                double d9;
                if (Math.abs(ad[l1 - 1]) < Math.abs(ad3[k1]))
                {
                    double d7 = ad[l1 - 1] / ad3[k1];
                    d9 = 0.5D / Math.sqrt(0.25D + 0.25D * d7 * d7);
                    d5 = d9 * d7;
                    d1 = 1.0D;
                    if (Math.abs(d5) * 1.7976931348623157E+308D > 1.0D)
                        d1 = 1.0D / d5;
                }
                else
                {
                    double d11 = ad3[k1] / ad[l1 - 1];
                    d5 = 0.5D / Math.sqrt(0.25D + 0.25D * d11 * d11);
                    d9 = d5 * d11;
                    d1 = d9;
                }
                int j2 = l1 - 1;
                for (int i1 = k1; i1 < i; i1++)
                {
                    double d3 = d5 * ad[j2] + d9 * ad3[i1];
                    ad3[i1] = -d9 * ad[j2] + d5 * ad3[i1];
                    ad[j2] = d3;
                    j2++;
                }

                ad3[k1] = d1;
            }
            if (ad[l1 - 1] == 0.0D)
                sing = true;
            l1 += i - k1;
        }

        for (int l2 = j - 1; l2 < i; l2++)
            ad[(l1 + l2) - j] = ad3[l2];

        if (ad[l1 - 1] == 0.0D)
            sing = true;
    }

    private void updateQ(int i, int j, double ad[], int k, double ad1[], double ad2[])
    {
        Array2 array2 = new Array2(ad, k);
        if (j <= 1)
            return;
        for (int j1 = j - 2; j1 >= 0; j1--)
        {
            double d2;
            double d4;
            if (Math.abs(ad1[j1]) > 1.0D)
            {
                d2 = 1.0D / ad1[j1];
                d4 = Math.sqrt(1.0D - d2 * d2);
            }
            else
            {
                d4 = ad1[j1];
                d2 = Math.sqrt(1.0D - d4 * d4);
            }
            for (int l = 0; l < i; l++)
            {
                double d = d2 * array2.get(j1, l) - d4 * array2.get(j - 1, l);
                array2.set(j - 1, l, d4 * array2.get(j1, l) + d2 * array2.get(j - 1, l));
                array2.set(j1, l, d);
            }

        }

        for (int k1 = 0; k1 < j - 1; k1++)
        {
            double d3;
            double d5;
            if (Math.abs(ad2[k1]) > 1.0D)
            {
                d3 = 1.0D / ad2[k1];
                d5 = Math.sqrt(1.0D - d3 * d3);
            }
            else
            {
                d5 = ad2[k1];
                d3 = Math.sqrt(1.0D - d5 * d5);
            }
            for (int i1 = 0; i1 < i; i1++)
            {
                double d1 = d3 * array2.get(k1, i1) + d5 * array2.get(j - 1, i1);
                array2.set(j - 1, i1, -d5 * array2.get(k1, i1) + d3 * array2.get(j - 1, i1));
                array2.set(k1, i1, d1);
            }

        }

    }

}